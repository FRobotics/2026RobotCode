package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class AgitatorSystem {

    private AgitatorSystem(){}

    // contants
    private static final double Agitator_Kn = 1.0 / 4914.0; // max RPM guess.
    private static final double Agitator_Ks = 0.0;
    private static final double Agitator_Kv = Agitator_Kn;
    private static final double Agitator_Ka = 0.0;
    private static final double Agitator_Kp = Agitator_Kn * 1.0;    // leave at zero untill we get max RPM...
    private static final double Agitator_Ki = Agitator_Kn * 0.0;
    private static final double Agitator_Kd = Agitator_Kn * 1.0E-6;
    private static final double Agitator_Izone = 200.0;  // Error RPM where I is used.
    private static final double Agitator_Imax = 0.30;    // Max output of integral term.

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    
    private static boolean locAgitatorOn = false;   // TRUE = we want agitator to be on.  FALSE = we want agitator to be off
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
    private static SlewRateLimiter AgitatorRateLimit;
    private static boolean locFwdStalled = false;
    private static int locStallStateNumb = 0;   // -- 0 - no stall, 1 - stall wait, 2 - reverse wait
    private static double locStallTimer = 0.0;



    /**
     * One time initialization for the agitator system.  This should be called when the robot boots.
     */
    public static void init() {


        // init agitator motor
        AgitatorMotor = new SparkMax(8,MotorType.kBrushless);
        AgitatorMotorEncoder = AgitatorMotor.getEncoder();

        // --------output rate limit
        AgitatorRateLimit = new SlewRateLimiter(3.0);       // 1/3 second to full output.

        //Speed control
        AgitatorFeedForward = new SimpleMotorFeedforward(Agitator_Ks, Agitator_Kv, Agitator_Ka);
        AgitatorPID = new PIDController(Agitator_Kp, Agitator_Ki, Agitator_Kd);
        AgitatorPID.setIntegratorRange(-Agitator_Imax, Agitator_Imax);  // only allow integral to add +/- this amount to output.
        AgitatorPID.setIZone(Agitator_Izone);        // only do integration when within this many RPMs.
        AgitatorZeroEdgeOn = new Lib4150DigEdgeOn();

        // --------delays for stall
        locStallStateNumb = 0;
        locStallTimer = 0.0;

        // ensure agitator starts off
        cmdAgitatorOff();

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AgitatorSystem");

        locNTSend.addItemBoolean("AgitatorState", AgitatorSystem::getAgitatorState);
        locNTSend.addItemBoolean("AgitatorStalled", AgitatorSystem::getAgitatorStallState);
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

        // if on, output pctDmdFromDash -- default 0.50
        // if off, output 0

        if (locAgitatorOn){
            double pctDmdFromDash = SmartDashboard.getNumber("AgitatorSystem/DashSpeedPct", 0.50);
            locAgitatorSetpointRPM = pctDmdFromDash / Agitator_Kn;
            // --------if launcher not up to speed set demand at zero.
            if ( !TurretLauncher.getAgitatorStartPermissive() ) {
                locAgitatorSetpointRPM = 0.0;
            }
        }
        else {
            locAgitatorSetpointRPM = 0.0;
            locFwdStalled = false;
            locStallStateNumb = 0;
        };


        // --------calculate values for reverse job when forward stalls
        // --------turn rev stall prevention on
        // --------process the "stall" state machine
        switch ( locStallStateNumb ) {
            // --------wait for stall to occur
            case 0:
                locFwdStalled = false;
                locStallTimer = systemElapsedTimeSec;
                if ( (locAgitatorSetpointRPM > 0.0) && (Math.abs( AgitatorRPM ) < 120.0) ) {
                    locStallStateNumb = 1;
                } 
                break;
            // --------wait for timer to expire
            case 1:
                locFwdStalled = false;
                if ( (locAgitatorSetpointRPM <= 0.0) ) {
                    locStallStateNumb = 0;
                } 
                else if ( Math.abs( AgitatorRPM ) > 180 ) {
                    locStallStateNumb = 0;
                }
                else if ( systemElapsedTimeSec > ( locStallTimer + 0.6 ) ) {
                    locStallStateNumb = 2;
                    locStallTimer = systemElapsedTimeSec;
                }
                break;
            case 2:
                locFwdStalled = true;
                if ( systemElapsedTimeSec > ( locStallTimer + 1.0 ) ) {
                    locStallStateNumb = 0;
                    locStallTimer = systemElapsedTimeSec;
                }
                break;
        }

        // --------actual speed setpoint based on stall....
        double tmpAgitatorSetpointRPM = ( locFwdStalled  ) ? -locAgitatorSetpointRPM : locAgitatorSetpointRPM;
        
        // --------Agitator Speed Control
        locAgitatorFFoutput = AgitatorFeedForward.calculate(tmpAgitatorSetpointRPM);
        locAgitatorPIDoutput = AgitatorPID.calculate(AgitatorRPM, tmpAgitatorSetpointRPM);

        // --------special case for 0.0  -- don't control just coast.
        if ( AgitatorZeroEdgeOn.execEdgeOn( locAgitatorSetpointRPM == 0.0 ) ) {
            AgitatorPID.reset();      // reset integral.
        }
        if ( locAgitatorSetpointRPM == 0.0 ) {
            locAgitatorPIDoutput = 0.0;
        }

        // --------rate limit and clamp the motor output.
        AgitatorOutput = MathUtil.clamp( AgitatorRateLimit.calculate( locAgitatorFFoutput+locAgitatorPIDoutput ), -1.0, 1.0 );

        // --------send demand to motor.
        AgitatorMotor.set(AgitatorOutput);

        // ---------update network tables.
        locNTSend.triggerUpdate();
        return;
    }

    // --------COMMANDS

    /**
     * turn the agitator on
     */
    public static void cmdAgitatorOn() {
        locAgitatorOn=true;
        return;
    }

    /**
     * Turn the agitator off
     */
    public static void cmdAgitatorOff() {
        locAgitatorOn=false;
        return;
    }

    // -------- GETTERS
    
    /**
     * Get the current agitator state,  false = off, true = on
     * 
     * @return - Current state - boolean - false = off, true = on
     */
    public static boolean getAgitatorState() {
        return locAgitatorOn;
    }

    
    /**
     * Get the current agitator stall state,  false = off, true = on
     * When stalled the motor will try to run backwards.
     * 
     * @return - Current stall state - boolean - false = normal, true = stalled
     */
    public static boolean getAgitatorStallState() {
        return locFwdStalled;
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
