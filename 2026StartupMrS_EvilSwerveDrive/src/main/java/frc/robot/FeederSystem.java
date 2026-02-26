package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

public class FeederSystem {

    private FeederSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want feeder to be on.  FALSE = we want feeder to be off
    private static boolean locFeederOn = false; 
    private static SparkMax FeederMotor;
    private static RelativeEncoder FeederMotorEncoder;
    private static double FeederOutput = 0.0;
    private static double FeederRPM = 0.0;

    private static double locFeederSetpointRPM = 0.0;
    private static double locFeederFFoutput = 0.0;
    private static double locFeederPIDoutput = 0.0;
    private static SimpleMotorFeedforward FeederFeedFwd;
    private static PIDController FeederPID;
    private static final double Feeder_Kn = 1.0 / 5000.0; // max RPM guess.
    private static final double Feeder_Ks = 0.0;
    private static final double Feeder_Kv = Feeder_Kn;
    private static final double Feeder_Ka = 0.0;
    private static final double Feeder_Kp = Feeder_Kn * 0.5;
    private static final double Feeder_Ki = Feeder_Kn * 2.0;
    private static final double Feeder_Kd = Feeder_Kn * 1.0E-6;
    private static final double Feeder_Izone = 120.0;  // Error RPM where I is used.
    private static final double Feeder_Imax = 0.30;    // Max output of integral term.


    public static void init() {


        // init network table
        FeederMotor = new SparkMax(9,MotorType.kBrushless);
        FeederMotorEncoder = FeederMotor.getEncoder();

        //Speed control
        FeederFeedFwd = new SimpleMotorFeedforward(Feeder_Ks, Feeder_Kv, Feeder_Ka);
        FeederPID = new PIDController( Feeder_Kp, Feeder_Ki, Feeder_Kd);
        FeederPID.setIntegratorRange(-Feeder_Imax, Feeder_Imax);  // only allow integral to add +/- this amount to output.
        FeederPID.setIZone(Feeder_Izone);        // only do integration when within this many RPMs.


        // start with feeder off
        cmdFeederOff();

        locNTSend = new Lib4150NetTableSystemSend("FeederSystem");

        locNTSend.addItemBoolean("FeederState", FeederSystem::getFeederState);
        locNTSend.addItemDouble("FeederOutput", FeederSystem::getMotorOutput);
        locNTSend.addItemDouble("FeederRPM", FeederSystem::getMotorRPM);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double SystemElapsedTime) {

        // --------get feeder RPM
        FeederRPM = FeederMotorEncoder.getVelocity();

        // if on, output 0.2
        // if off, output 0
        if (locFeederOn){
            //FeederOutput=0.2;
            locFeederSetpointRPM = 0.2 / Feeder_Kn;
            // ----could use something like this...
            // locFeederSetpointRPM = MathUtil.clamp( TurretLauncher.getLauncherTargetSpeed() - 100.0, 0.0, 5000.0) ;
        }
        else {
            // FeederOutput=0;
            locFeederSetpointRPM = 0.0;
        };

        // --------do speed control
        locFeederFFoutput = FeederFeedFwd.calculate( locFeederSetpointRPM );
        locFeederPIDoutput = FeederPID.calculate(FeederRPM, locFeederSetpointRPM);
        // --------special case for 0.0  -- don't control just coast.
        if ( locFeederSetpointRPM == 0.0 ) {
            FeederPID.reset();      // reset integral.
            locFeederPIDoutput = 0.0;
        }
        FeederOutput = MathUtil.clamp( locFeederFFoutput+locFeederPIDoutput, -1.0, 1.0 );

        FeederMotor.set(FeederOutput);

        locNTSend.triggerUpdate();
    }

    public static boolean getFeederState() {
        return locFeederOn;
    }

    public static void cmdFeederOn() {
        locFeederOn=true;
    }

    public static void cmdFeederOff() {
        locFeederOn=false;
    }

    public static double getMotorOutput() {
        return FeederOutput;
    }

    public static double getMotorRPM() {
        return FeederRPM;
    }

}

