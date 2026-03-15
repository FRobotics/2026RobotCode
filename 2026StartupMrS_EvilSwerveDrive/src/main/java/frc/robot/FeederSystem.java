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
    // --------overall normalization
    // --------normalization is usually = Max motor output / max device RPM
    private static final double Feeder_Kn = 1.0 / 5734.6; 
    // --------feedforward
    // --------Ks - static feedforward is the amount of motor output to get started moving
    private static final double Feeder_Ks = 0.0173712037501424;
    // --------Kv -- velocity feedforward is the slope of the motor output to get a particular RPM ( + Ks )
    private static final double Feeder_Kv = 0.000171352226013573;
    // --------Ka -- acceleration constant -- Helps to accelerate or decellerate to a paricular RPM (we are not changing must so 0.0 for now)
    private static final double Feeder_Ka = 0.0;
    // --------PID
    // --------Kp - proportional constant    output =  error * Kp
    private static final double Feeder_Kp = Feeder_Kn * 0.60;
    // --------Ki - integral constant   output  = Ki x integral( error )
    private static final double Feeder_Ki = Feeder_Kn * 2.5;
    // --------kd = derivative constant     output = Kd * derivative( error )
    private static final double Feeder_Kd = Feeder_Kn * 1.0E-6;
    // --------integral zone ( in sp/pv units )
    // --------Izone -- Error has to be within this amount to be used.
    private static final double Feeder_Izone = 100.0;  // Error RPM where I is used.
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



    public static void init() {


        // init network table
        FeederMotor = new SparkMax(9,MotorType.kBrushless);
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
        if (locFeederOn){
            //FeederOutput=0.2;
            //locFeederSetpointRPM = 0.2 / Feeder_Kn;
            locFeederSetpointRPM = locCmdFeederSetpointRPM;
        }
        else {
            // FeederOutput=0;
            locFeederSetpointRPM = 0.0;
        };

        //-----Speed Control
        locFeederFFOutput = FeederFeedForward.calculate(locFeederSetpointRPM);
        locFeederPIDOutput = FeederPID.calculate(FeederRPM, locFeederSetpointRPM);
        // --------special case for 0.0  -- don't control just coast.
        if (locFeederSetpointRPM == 0.0) {
            FeederPID.reset();      // reset integral.
            locFeederPIDOutput = 0.0;
        }
        FeederOutput = MathUtil.clamp(locFeederFFOutput+locFeederPIDOutput, -1.0, 1.0);

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

    public static void setMotorRPMTarget( double parmRPM) {
        locCmdFeederSetpointRPM = parmRPM;
    }

}

