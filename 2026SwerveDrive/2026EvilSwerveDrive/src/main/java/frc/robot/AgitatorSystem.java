package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

// TODO: suggest removing unused imports
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import edu.wpi.first.wpilibj.DutyCycleEncoder;

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
    private static SimpleMotorFeedforward AgitatorFeedForward;
    private static PIDController AgitatorPID;
    private static final double Agitator_Kn = 1.0 / 5000.0;
    private static final double Agitator_Ks = 0.0;
    private static final double Agitator_Kv = Agitator_Kn;
    private static final double Agitator_Ka = 0.0;
    private static final double Agitator_Kp = Agitator_Kn * 0.0;
    private static final double Agitator_Ki = Agitator_Kn * 0.0;
    private static final double Agitator_Kd = Agitator_Kn * 1.0E-6;
    private static final double Agitator_Izone = 200.0;
    private static final double Agitator_Imax = 0.30;



    /**
     * One time initialization for the agitator system.  This should be called when the robot boots.
     */
    public static void init() {


        // init agitator motor
        AgitatorMotor = new SparkMax(8,MotorType.kBrushless);
        AgitatorMotorEncoder = AgitatorMotor.getEncoder();

        //Speed control
        AgitatorFeedForward = new SimpleMotorFeedforward(Agitator_Ks, Agitator_Kv, Agitator_Ka);
        AgitatorPID = new PIDController(Agitator_Kp, Agitator_Ki, Agitator_Kd);
        AgitatorPID.setIntegratorRange(-Agitator_Imax, Agitator_Imax);
        AgitatorPID.setIZone(Agitator_Izone);


        // ensure agitator starts off
        cmdAgitatorOff();

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AgitatorSystem");

        locNTSend.addItemBoolean("AgitatorState", AgitatorSystem::getAgitatorState);
        locNTSend.addItemDouble("AgitatorOutput", AgitatorSystem::getMotorOutput);
        locNTSend.addItemDouble("AgitatorRPM", AgitatorSystem::getMotorRPM);
        
        locNTSend.triggerUpdate();
        
    }

    /**
     * Execute the logic for agitator.  This should be called every 20 millseconds
     * 
     * @param systemElapsedTimeSec - double - Operating system elapsed time in seconds.
     */
    public static void executeLogic(double systemElapsedTimeSec) {

        AgitatorRPM = AgitatorMotorEncoder.getVelocity();

        // if on, output 0.2
        // if off, output 0

        if (locAgitatorOn){
            locAgitatorSetpointRPM = 0.5 / Agitator_Kn;
        }
        else if ( locAgitatorOnRev){
            locAgitatorSetpointRPM = -0.1 / Agitator_Kn;
        }
        else {
            locAgitatorSetpointRPM = 0.0;
        };

        // Agitator Speed Control
        locAgitatorFFoutput = AgitatorFeedForward.calculate(locAgitatorSetpointRPM);
        locAgitatorPIDoutput = AgitatorPID.calculate(AgitatorRPM, locAgitatorSetpointRPM);


        AgitatorMotor.set(AgitatorOutput);

        locNTSend.triggerUpdate();
    }

    /**
     * Get the current agitator state,  false = off, true = on
     * 
     * @return - Current state - boolean - false = off, true = on
     */
    public static boolean getAgitatorState() {
        return locAgitatorOn;
    }

    /**
     * turn the agitator on
     */
    public static void cmdAgitatorOn() {
        locAgitatorOn=true;
    }

    /**
     * Turn the agitator off
     */
    public static void cmdAgitatorOff() {
        locAgitatorOn=false;
    }
    /**
     * Get the current motor demand output
     * @return - double - motor demand +/- 1.0
     */
     public static double getMotorOutput() {
        return AgitatorOutput;
    }
    /**
     * Get the current motor RPM (revolutions per minute)
     * @return - double - current motor RPM
     */
     public static double getMotorRPM() {
        return AgitatorRPM;
    }

}
