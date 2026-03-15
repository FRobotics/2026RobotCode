package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Climb system
 */
public class Climb {

    private Climb(){}

    
    // -------- class constants
    private static final double RETRACT_ENC_VALUE = 0.0;
    private static final double RETRACT_HYSTERESIS_ENC_VALUE = RETRACT_ENC_VALUE + 20.0;
    private static final double EXTEND_ENC_VALUE = 300.0;
    private static final double EXTEND_HYSTERESIS_ENC_VALUE = EXTEND_ENC_VALUE - 20.0;;

    private static final double RETRACT_MOTOR_DMD = -0.40;
    private static final double EXTEND_MOTOR_DMD = 0.20;
    private static final double OFF_MOTOR_DMD = 0.00;

    // -------- class variables

    // -------- sensors and actuators
    private static SparkFlex ClimbMotor1;
    private static RelativeEncoder ClimbEncoder1;
    private static DigitalInput RetractLimitSwitch;
    // -------- other objects
    private static Lib4150NetTableSystemSend locNTSend;
    // -------- primative values
    private static boolean locRetractRawLimitSwitch = false;
    private static boolean locRetractSoftLimitSwitch = false;
    private static boolean locExtendSoftLimitSwitch = false;
    private static double locClimbEncoder1Rotations = 0.0;
    private static double locRawMotorDemand = 0.0;
    private static double locMotorDemand = 0.0;
    private static double locRetractClamp = 0.0;
    private static double locExtendClamp = 0.0;
    // --------command ExtendInc - for teleop
    private static int locCmdExtendIncRequest = 0;
    private static int locCmdExtendIncProcessed = 0;
    // --------command RetractInc - for teleop
    private static int locCmdRetractIncRequest = 0;
    private static int locCmdRetractIncProcessed = 0;


    /**
     * One time initialization.  This must be called once when robot boots.
     */
    public static void init() {

        ClimbMotor1 = new SparkFlex(CanId.Climb, MotorType.kBrushless);
        ClimbEncoder1 = ClimbMotor1.getEncoder();
        ClimbEncoder1.setPosition(RETRACT_ENC_VALUE);

        RetractLimitSwitch = new DigitalInput(3);
        locRetractRawLimitSwitch = false;

        // --------initialize some values just in case...
        locRetractSoftLimitSwitch = false;
        locExtendSoftLimitSwitch = false;
        locMotorDemand = OFF_MOTOR_DMD;

        locCmdExtendIncRequest = 0;
        locCmdExtendIncProcessed = locCmdExtendIncRequest;
        locCmdRetractIncRequest = 0;
        locCmdRetractIncProcessed = locCmdRetractIncRequest;
        
         // -------- init network table
        locNTSend = new Lib4150NetTableSystemSend("Climb");
       
        locNTSend.addItemBoolean("IsExtended", Climb::getClimbExtended);
        locNTSend.addItemBoolean("IsRetracted", Climb::getClimbRetracted);
        locNTSend.addItemBoolean("RetractedLimitSwitch", Climb::getClimbRetractedLimitSwitch);
        locNTSend.addItemDouble("Position", Climb::getPosition);
        locNTSend.addItemDouble("MotorDemand", Climb::getMotorDemand);
        
        locNTSend.triggerUpdate();
        
        return;        
    }

    /**
     * Execute logic for climb system
     * 
     * @param systemElapsedTimeSec - double - Current system elapsed time - seconds
     */
    public static void executeLogic(double systemElapsedTimeSec) {

        // -------- Read sensors 
		// -------- motor encoder in rotations, 
        locClimbEncoder1Rotations = ClimbEncoder1.getPosition();

		// -------- physical retract limit switch.
        locRetractRawLimitSwitch = RetractLimitSwitch.get();

	    // -------- set soft retract limit switch
		// --------     Turn on retract soft limit switch when physical switch is ON.
        if ( locRetractRawLimitSwitch ) {
            locRetractSoftLimitSwitch = true;
        }
		// --------     Turn off retract soft limit switch when encoder value is above X and 
		// -------- 	    physical limit switch is off
		// --------         ( if neither of these is true, soft limit switch remains as is.
		// --------         This is really a set-reset flip flop.)
        if ( !locRetractRawLimitSwitch && locClimbEncoder1Rotations > RETRACT_HYSTERESIS_ENC_VALUE  ) {
            locRetractSoftLimitSwitch = false;
        }

	    // -------- set soft extend limit switch
		// --------     Turn on extend soft limit switch when encoder >= fully extended.
        if ( locClimbEncoder1Rotations >= EXTEND_ENC_VALUE ) {
            locExtendSoftLimitSwitch = true;
        }
		// -------- Turn off extend soft limit switch when 
		// -------- 	encoder <= (fully extended-extended hyteresis value)
		// -------- ( if neither of these is true, soft limit switch remains as is.
		// -------- This is really a set-reset flip flop.)
        if ( locClimbEncoder1Rotations < EXTEND_HYSTERESIS_ENC_VALUE) {
            locExtendSoftLimitSwitch = false;
        }

	    // -------- process command and set desired un-clamped motor output.

		// -------- Commands:
		// -------- 	RetractInc - motor output = one direction constant (for teleop)
		// -------- 	ExtendInc - motor output = other direction constant (for teleop)
		// -------- 	RetractFull - retract until hit retract soft limit switch or 
		// -------- 		maybe timeout (for auto)
		// -------- 	ExtendFull - extend until hit extend soft limit switch or
		// -------- 		maybe timeout (for auto)
		// -------- 	no command - motor output = 0.
        if ( locCmdExtendIncRequest != locCmdExtendIncProcessed ) {
            locRawMotorDemand = EXTEND_MOTOR_DMD;
        }
        else if ( locCmdRetractIncRequest != locCmdRetractIncProcessed ) {
            locRawMotorDemand = RETRACT_MOTOR_DMD;
        }
        else {
            locRawMotorDemand = OFF_MOTOR_DMD;
        }
		// -------- always reset processed value for all commands each time, 
		// -------- 	even if command is not used. (both on at the same time).
        locCmdExtendIncProcessed = locCmdExtendIncRequest;
        locCmdRetractIncProcessed = locCmdRetractIncRequest;

	    // -------- set retract clamp value.
		// --------     if soft retract limit switch is on use 0 otherwise -1.0
        locRetractClamp = ( locRetractSoftLimitSwitch ) ? 0.0 : -1.0;
	    // -------- set extend clamp value.
		// --------     if soft extend limit switch is on use 0 otherwise +1.0
        locExtendClamp = ( locExtendSoftLimitSwitch ) ? 0.0 : 1.0;

	    // -------- clamp motor demand
        locMotorDemand = MathUtil.clamp( locRawMotorDemand, locRetractClamp, locExtendClamp );

	    // -------- output motor demand.
        ClimbMotor1.set(locMotorDemand);

	    // -------- pdate network tables..
        locNTSend.triggerUpdate();

        return;
    }

    // ============ COMMANDS
    /**
     * Send command to Extend the climber.  This command is valid for
     * only 1 execution cycle.  To continue to Extend, the command
     * must be re-issued.  This is designed to be called by Teleop.
     */
    public static void cmdExtendInc() {
        locCmdExtendIncRequest++;
        return;
    }
    /**
     * Send command to Retract the climber.  This command is valid for
     * only 1 execution cycle.  To continue to Retract, the command
     * must be re-issued.  This is designed to be called by Teleop.
     */
    public static void cmdRetractInc() {
        locCmdRetractIncRequest++;
        return;
    }

    // ============ GETTERS

    /**
     * Get the climber is extended soft limit switch value
     * @return extended - boolean - TRUE if fully extended
     */
    public static boolean getClimbExtended() {
        return locExtendSoftLimitSwitch;
    }
    /**
     * Get the climber is retracted soft limit switch value
     * @return retracted - boolean - TRUE if fully retracted
     */
    public static boolean getClimbRetracted() {
        return locRetractSoftLimitSwitch;
    }
    /**
     * Get the climber is retracted physical limit switch value
     * @return - retractLimitSwitch - boolean - TRUE when retracted limit switch is true.
     */
    public static boolean getClimbRetractedLimitSwitch() {
        return locRetractRawLimitSwitch;
    }
    /**
     * Get the climber position.  Unitless. 0 should be fully retracted
     * The value goes positive as the climber is extended.
     * @return - position - double - position of climber as determined by motor encoder.
     */
    public static double getPosition() {
        return locClimbEncoder1Rotations;
    }
    /**
     * Get the climber motor demand value.
     * @return motorDemand - double - Current motor demand +/- 1.0
     */
    public static double getMotorDemand() {
        return locMotorDemand;
    }

}

