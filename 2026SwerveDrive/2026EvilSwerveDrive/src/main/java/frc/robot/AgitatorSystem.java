package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class AgitatorSystem {

    private AgitatorSystem(){}

    // contants
    private static final double Agitator_Kn = 1.0 / 5000.0; // max RPM guess.
    private static final double Agitator_Ks = 0.0;
    private static final double Agitator_Kv = Agitator_Kn;
    private static final double Agitator_Ka = 0.0;
    private static final double Agitator_Kp = Agitator_Kn * 0.5;
    private static final double Agitator_Ki = Agitator_Kn * 0.0;
    private static final double Agitator_Kd = Agitator_Kn * 1.0E-6;
    private static final double Agitator_Izone = 200.0;  // Error RPM where I is used.
    private static final double Agitator_Imax = 0.30;    // Max output of integral term.

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
    private static Lib4150DigEdgeOn AgitatorZeroEdgeOn;



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
            double pctDmdFromDash = SmartDashboard.getNumber("AgitatorSystem/DashSpeedPct", 0.5);
            locAgitatorSetpointRPM = pctDmdFromDash / Agitator_Kn;
            // --------if launcher not up to speed set demand at zero.
            if ( !TurretLauncher.getAgitatorStartPermissive() ) {
                locAgitatorSetpointRPM = 0.0;
            }
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
        return;
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
        locAgitatorOnRev=false;
        return;
    }
    /**
     * turn the agitator on in reverse
     */
    public static void cmdAgitatorOnRev() {
        locAgitatorOnRev=true;
        locAgitatorOn=false;
        return;
    }

    /**
     * Turn the agitator off
     */
    public static void cmdAgitatorOff() {
        locAgitatorOn=false;
        locAgitatorOnRev=false;
        return;
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
    /**
     * Get the current motor RPM target (revolutions per minute)
     * @return - double - current motor RPM target
     */
     public static double getMotorRPMTarget() {
        return locAgitatorSetpointRPM;
    }

}
