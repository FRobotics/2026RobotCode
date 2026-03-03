package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class AgitatorSystem {

    private AgitatorSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want agitator to be on.  FALSE = we want agitator to be off
    private static boolean locAgitatorOn = false; 
    private static boolean locAgitatorOnRev = false; 
    private static SparkMax AgitatorMotor;
    private static RelativeEncoder AgitatorMotorEncoder;
    private static double AgitatorOutput = 0.0;
    private static double AgitatorRPM = 0.0;

    private static double locAgitatorSetpointRPM = 0.0;
    private static double locAgitatorFFoutput = 0.0;
    private static double locAgitatorPIDoutput = 0.0;
    private static SimpleMotorFeedforward AgitatorFeedFwd;
    private static PIDController AgitatorPID;
    private static Lib4150DigEdgeOn AgitatorZeroEdgeOn;
    private static final double Agitator_Kn = 1.0 / 5000.0; // max RPM guess.
    private static final double Agitator_Ks = 0.0;
    private static final double Agitator_Kv = Agitator_Kn;
    private static final double Agitator_Ka = 0.0;
    private static final double Agitator_Kp = Agitator_Kn * 0.0;
    private static final double Agitator_Ki = Agitator_Kn * 0.0;
    private static final double Agitator_Kd = Agitator_Kn * 1.0E-6;
    private static final double Agitator_Izone = 200.0;  // Error RPM where I is used.
    private static final double Agitator_Imax = 0.30;    // Max output of integral term.


    public static void init() {


        // init agitator motor
        AgitatorMotor = new SparkMax(8,MotorType.kBrushless);
        AgitatorMotorEncoder = AgitatorMotor.getEncoder();

        //Speed control
        AgitatorFeedFwd = new SimpleMotorFeedforward(Agitator_Ks, Agitator_Kv, Agitator_Ka);
        AgitatorPID = new PIDController( Agitator_Kp, Agitator_Ki, Agitator_Kd);
        AgitatorPID.setIntegratorRange(-Agitator_Imax, Agitator_Imax);  // only allow integral to add +/- this amount to output.
        AgitatorPID.setIZone(Agitator_Izone);        // only do integration when within this many RPMs.
        AgitatorZeroEdgeOn = new Lib4150DigEdgeOn();


        // ensure agitator starts off
        cmdAgitatorOff();

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AgitatorSystem");

        locNTSend.addItemBoolean("AgitatorState", AgitatorSystem::getAgitatorState);
        locNTSend.addItemDouble("AgitatorOutput", AgitatorSystem::getMotorOutput);
        locNTSend.addItemDouble("AgitatorRPM", AgitatorSystem::getMotorRPM);
        locNTSend.addItemDouble("AgitatorRPMTarget", AgitatorSystem::getMotorRPMTarget);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        AgitatorRPM = AgitatorMotorEncoder.getVelocity();

        // if on, output 0.2
        // if off, output 0

        if (locAgitatorOn){
            locAgitatorSetpointRPM = 0.5 / Agitator_Kn;
        }
        else if ( locAgitatorOnRev ) {
            locAgitatorSetpointRPM = -0.1 / Agitator_Kn;
        }
        else {
            locAgitatorSetpointRPM = 0.0;
        };


        // --------do speed control
        locAgitatorFFoutput = AgitatorFeedFwd.calculate( locAgitatorSetpointRPM );
        locAgitatorPIDoutput = AgitatorPID.calculate(AgitatorRPM, locAgitatorSetpointRPM);
        // --------special case for 0.0  -- don't control just coast.
        if ( AgitatorZeroEdgeOn.execEdgeOn( locAgitatorSetpointRPM == 0.0 ) ) {
            AgitatorPID.reset();      // reset integral.
        }
        if ( locAgitatorSetpointRPM == 0.0 ) {
            locAgitatorPIDoutput = 0.0;
        }
        AgitatorOutput = MathUtil.clamp( locAgitatorFFoutput+locAgitatorPIDoutput, -1.0, 1.0 );

        AgitatorMotor.set(AgitatorOutput);

        locNTSend.triggerUpdate();
    }

    public static boolean getAgitatorState() {
        return locAgitatorOn;
    }

    public static void cmdAgitatorOn() {
        locAgitatorOn=true;
        locAgitatorOnRev=false;
    }
    public static void cmdAgitatorOnRev() {
        locAgitatorOnRev=true;
        locAgitatorOn=false;
    }
    public static void cmdAgitatorOff() {
        locAgitatorOn=false;
        locAgitatorOnRev=false;
    }
     public static double getMotorOutput() {
        return AgitatorOutput;
    }
     public static double getMotorRPM() {
        return AgitatorRPM;
    }
     public static double getMotorRPMTarget() {
        return locAgitatorSetpointRPM;
    }

}
