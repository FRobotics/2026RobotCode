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
    // --------FEEDER TUNING CONSTANTS
    // --------based on data from girls of steel testing 3/14/2026
    // --------overall normalization
    // --------normalization is usually = Max motor output / max device RPM
    private static final double Feeder_MaxRPM = 5688.565;
    // private static final double Feeder_Kn = 1.0 / 5734.6; 
    private static final double Feeder_Kn = 1.0 / Feeder_MaxRPM; 
    // --------feedforward
    // --------Ks - static feedforward is the amount of motor output to get started moving
    //private static final double Feeder_Ks = 0.0173712037501424;
    private static final double Feeder_Ks = 0.0164277342930673;
    // --------Kv -- velocity feedforward is the slope of the motor output to get a particular RPM ( + Ks )
    //private static final double Feeder_Kv = 0.000171352226013573;
    private static final double Feeder_Kv = ( 1.0 - Feeder_Ks ) / Feeder_MaxRPM;
    // --------Ka -- acceleration constant -- Helps to accelerate or decellerate to a paricular RPM (we are not changing must so 0.0 for now)
    private static final double Feeder_Ka = 0.0;
    // --------PID
    // --------Kp - proportional constant    output =  error * Kp
    private static final double Feeder_Kp = Feeder_Kn * 3.0;    // was 0.6  
    // --------Ki - integral constant   output  = Ki x integral( error )
    private static final double Feeder_Ki = Feeder_Kn * 2.0;
    // --------kd = derivative constant     output = Kd * derivative( error )
    private static final double Feeder_Kd = Feeder_Kn * 2.0E-4; 
    // --------integral zone ( in sp/pv units )
    // --------Izone -- Error has to be within this amount to be used.
    private static final double Feeder_Izone = 60.0;  // Error RPM where I is used.
    // --------Irange - -min/max value that the integral PID term can have.
    private static final double Feeder_Imax = 0.30;    // Max output of integral term.

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want feeder to be on.  FALSE = we want feeder to be off
    private static boolean locFeederOn = false; 
    private static SparkMax FeederMotor;
    private static RelativeEncoder FeederMotorEncoder;
    private static double FeederOutput = 0.0;
    private static double FeederRPM = 0.0;

    private static double locFeederSetpointRPM = 0.0;
    private static double locCmdFeederSetpointRPM = 0.0;
    private static double locFeederFFOutput = 0.0;
    private static double locFeederPIDOutput = 0.0;
    private static SimpleMotorFeedforward FeederFeedForward;
    private static PIDController FeederPID;
    private static int RevCount = 0;
    // private static boolean FeederReverse = true;


    public static void init() {


        // init network table
        FeederMotor = new SparkMax(CanId.Feeder,MotorType.kBrushless);
        FeederMotorEncoder = FeederMotor.getEncoder();

        //Speed control
        FeederFeedForward = new SimpleMotorFeedforward(Feeder_Ks, Feeder_Kv, Feeder_Ka);
        FeederPID = new PIDController(Feeder_Kp, Feeder_Ki, Feeder_Kd);
        FeederPID.setIntegratorRange(-Feeder_Imax, Feeder_Imax);  // only allow integral to add +/- this amount to output.
        FeederPID.setIZone(Feeder_Izone);        // only do integration when within this many RPMs.


        // start with feeder off
        cmdFeederOff();

        locNTSend = new Lib4150NetTableSystemSend("FeederSystem");

        locNTSend.addItemBoolean("FeederState", FeederSystem::getFeederState);
        locNTSend.addItemDouble("FeederOutput", FeederSystem::getMotorOutput);
        locNTSend.addItemDouble("FeederTargetRPM", FeederSystem::getMotorTargetRPM);
        locNTSend.addItemDouble("FeederRPM", FeederSystem::getMotorRPM);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double SystemElapsedTime) {

        // --------get feeder RPM
        FeederRPM = FeederMotorEncoder.getVelocity();

        // if on, output 0.2
        // if off, output 0
        if (locFeederOn  && TurretLauncher.getAgitatorStartPermissive() ){
            //FeederOutput=0.2;
            //locFeederSetpointRPM = 0.2 / Feeder_Kn;
            if (RevCount < 40){
                RevCount ++;
                locFeederSetpointRPM = -500.0;
            }
            else {
                locFeederSetpointRPM = locCmdFeederSetpointRPM;
            }
        }
        else {
            // FeederOutput=0;
            locFeederSetpointRPM = 0.0;
            RevCount = 0;
        };

        //-----Speed Control
        locFeederFFOutput = FeederFeedForward.calculate(locFeederSetpointRPM);
        locFeederPIDOutput = FeederPID.calculate(FeederRPM, locFeederSetpointRPM);
        // --------special case for 0.0  -- don't control just coast.
        if (locFeederSetpointRPM == 0.0) {
            FeederPID.reset();      // reset integral.
            locFeederPIDOutput = 0.0;
            locFeederFFOutput = 0.0;
        }
        FeederOutput = (MathUtil.clamp(locFeederFFOutput+locFeederPIDOutput, -1.0, 1.0));
        FeederOutput *= -1.0;
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
    public static double getMotorTargetRPM() {
        return locFeederSetpointRPM;
    }

    /*public static void setFeederReverse( boolean val) {
        FeederReverse = val;
    }*/

     public static void setMotorRPMTarget( double parmRPM) {
        if (locFeederOn) {
            locCmdFeederSetpointRPM = parmRPM;
        }
    }

}

