// --------file: SampleSystem.java

// --------this is the name of a group of files that go together.  
// --------In our case, all files residing under the same directory
// --------need to have the same package name.
package Systems;

// --------This imports definitions of library routines that this file
// --------uses.  These include standard java things, and WPILIB robotics
// --------library things, and things that you may create.
import Lib4150.Lib4150PositionControl;
import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.wpilibj.DutyCycleEncoder;

// --------for use with spark max motor controller
//import com.revrobotics.spark.SparkMax;
//import com.revrobotics.spark.SparkLowLevel.MotorType;

// --------for use with talon srx motor controller
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;


// =============================================================================================
/**
 *  Sample position control 
 * 
 *<br>
 *  File:   SamplePosCtrlSystem.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          SamplePosCtrlSYstem.init()<br>
 *          SamplePosCtrlSYstem.execute()<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.01    10/30/2026  Jim Simpson     Initial sample code <br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.01
 * @since      2026-10-30
*/
public class PosCtrlSystem {

    // =============================================================================================
    // --------public/global data types go here...  These are available outside of this file.

    // =============================================================================================
    // --------private data types go here...  These are only available inside this file.

    // =============================================================================================
    // --------public/global constants go here.  These are available outside of this file.
    // --------These should be defined as "public static final"
    private static final double SAMP_POS_CTRL_ERR_DEADBAND = 0.1;
    private static final double SAMP_POS_CTRL_ERR_THRESHOLD = 50.0;
    private static final double SAMP_POS_CTRL_OUT_MIN = 0.05;
    private static final double SAMP_POS_CTRL_OUT_THRESHOLD = 0.20;
    private static final double SAMP_POS_CTRL_OUT_MAX = 0.20;
    private static final double SAMP_POS_CTRL_KD = 1.0e-5;
    private static final boolean SAMP_POS_CTRL_FILTER_DERIV = true;
    private static final boolean SAMP_POS_CTRL_NEGATE_OUTPUT = false;

    private static final int SAMP_ABSENCODER_INPUT_DIO = 7;
    private static final double SAMP_ABSENCODER_SCALE_FACTOR = 360.0;

    private static final int SAMP_MOTOR_CAN_ID = 22;
    

    // =============================================================================================
    // --------private constants go here.  These are only available inside this file.
    // --------These should be defined as "private static final"

    // =============================================================================================
    // --------public variables go here.  These are available to all the methods in this file 
    // --------and to all methods outside of this file.
    // --------These should be defined as "public static"
    // --------objects from standard java libraries can be instantiated here.   It is better
    // --------to instantiate WPILIB objects in the Init method in this class and not in
    // --------the variable declaration here.
    // --------public variables should be used with CAUTION.  It is better to make variables
    // --------private and create getter and setter methods to allow access to get and set
    // --------the data.

    // =============================================================================================
    // --------private variables go here.  These are available only to methods in this file. 
    // --------These should be defined as "private static"
    // --------objects from standard java libraries can be instantiated here.   It is better
    // --------to instantiate WPILIB objects in the Init method in this class and not in
    // --------the variable declaration here.
    private static Lib4150PositionControl   samplePosCtrl;
    private static DutyCycleEncoder  samplePosCtrlAbsEncoder;
    private static Lib4150NetTableSystemSend outputNTValues;
    // --------spark max motor controller
    //private static SparkMax     samplePosCtrlMotor;
    // --------talon srx motor controller
    private static TalonSRX       samplePosCtrlMotor;

    private static double positionCurrentDeg = 0.0;
    private static boolean positionValid = false;
    private static double positionTargetDeg = 0.0;
    private static double positionErrorDeg = 0.0;
    private static double positionMotorOut = 0.0;
    private static boolean positionOnTarget = false;

    // =============================================================================================
    /**
     * --------This is the "constructor" method.  It is required.  It must have exactly the 
     * --------same name as the class.  Since our robot systems are "pseudo static" classes,
     * --------this must be defined as "private", so that others can't try and instantiate
     * --------an object of this class.
     */
    private PosCtrlSystem( ) {
        return;
    }

    // =============================================================================================
    /**
     * This routine is called once by the main robot code when the robot first is powered on.  
     * It instantiate all objects for sensors, actuators, control logic, and network table values.
     * It also initializes starting positions, speeds, and other values as needed.
     */
    public static void init( ) {

        // --------create objects and set initial configuration and scaling for sensors.

        // --------encoder to measure position....
        samplePosCtrlAbsEncoder = new DutyCycleEncoder(SAMP_ABSENCODER_INPUT_DIO );

        // ---------motor to move arm..
        // --------for use with spark max motor controller (more than this is needed!)
        //samplePosCtrlMotor = new SparkMax(SAMP_MOTOR_CAN_ID, MotorType.kBrushless);

        // --------for use with talon srx motor controller
        samplePosCtrlMotor = new TalonSRX(SAMP_MOTOR_CAN_ID);

        // --------create objects and set initial configuration and scaling for actuators

        // --------create other objects.
        samplePosCtrl = new Lib4150PositionControl( SAMP_POS_CTRL_ERR_DEADBAND, SAMP_POS_CTRL_ERR_THRESHOLD,
            SAMP_POS_CTRL_OUT_MIN, SAMP_POS_CTRL_OUT_THRESHOLD, SAMP_POS_CTRL_OUT_MAX, 
            SAMP_POS_CTRL_KD, SAMP_POS_CTRL_FILTER_DERIV, SAMP_POS_CTRL_NEGATE_OUTPUT );


        // --------set initial position, speed, control, system state data.

        // --------create all network table variables.
        outputNTValues = new Lib4150NetTableSystemSend("PosCtrlSystem");
        
        outputNTValues.addItemDouble("PositionDeg", PosCtrlSystem::getPositionCurrentDeg);
        outputNTValues.addItemDouble("TargetDeg", PosCtrlSystem::getPositionTargetDeg);
        outputNTValues.addItemDouble("ErrorDeg", PosCtrlSystem::getPositionErrorDeg);
        outputNTValues.addItemDouble("MotorOutputPct", PosCtrlSystem::getMotorOutput);
        outputNTValues.addItemBoolean("PositionValid", PosCtrlSystem::getPositionValid);
        outputNTValues.addItemBoolean("PositionOnTarget", PosCtrlSystem::getPositionOnTarget);


        // --------back to the main robot code.
        return;
    }

     // =============================================================================================
     /**
      * This routine is called by the main robot code every 20 milliseconds.  It is called regardless
      * of robot mode or game phase.  This routine should read all sensors, scale and filter input
      * data, perform all calculations and control logic, output values to actuators, then update
      * network table variables.
      * 
      * @param system_elapsed_time_secs - double - elapsed system time in seconds
      */
    public static void execute( double system_elapsed_time_secs ) {

        // --------read and scale all sensors.  This includes reading any network table values
        // --------such as data from camera systems.
        positionCurrentDeg = samplePosCtrlAbsEncoder.get() * SAMP_ABSENCODER_SCALE_FACTOR;
        positionValid = samplePosCtrlAbsEncoder.isConnected();
        // ---------consider skipping control calculation and output if not valid!!!

        // --------filter all sensors.

        // --------do all control calculations.

        // --------calculate position control
        positionMotorOut = samplePosCtrl.PosCtrlExec( positionTargetDeg, positionCurrentDeg);
        positionErrorDeg = samplePosCtrl.getPositionError();

        // --------write control results to actuators

        // --------for use with Spark Max motor controller.
        //samplePosCtrlMotor.set( positionMotorOut );

        // --------for use with talon srx motor controller
        samplePosCtrlMotor.set(ControlMode.PercentOutput, positionMotorOut);

        // --------trigger network table value output
        outputNTValues.triggerUpdate();

        // --------return to robot to do other systems...
        return;
    }


    // =============================================================================================
    /**
     * Get current position in degrees.
     * @return - double - position in degrees
     */
    public static double getPositionCurrentDeg( ) {
        return positionCurrentDeg;
    }

    // =============================================================================================
    /**
     * Get current position error in degrees.
     * @return - double - position error in degrees
     */
    public static double getPositionErrorDeg( ) {
        return positionErrorDeg;
    }

    // =============================================================================================
    /**
     * Get current position target in degrees.
     * @return - double - position target in degrees
     */
    public static double getPositionTargetDeg( ) {
        return positionTargetDeg;
    }

    // =============================================================================================
    /**
     * Get current motor output.
     * @return - double - motor output
     */
    public static double getMotorOutput( ) {
        return positionMotorOut;
    }

    // =============================================================================================
    /**
     * Get current position valid.
     * @return - boolean - position is valid
     */
    public static boolean getPositionValid( ) {
        return positionValid;
    }

    // =============================================================================================
    /**
     * Get current position on target
     * @return - boolean - position on target
     */
    public static boolean getPositionOnTarget( ) {
        return positionOnTarget;
    }

    // =============================================================================================
    /**
     * Set position target degrees
     * @param newValue - double - New target position degrees
     */
    public static void setPositionTargetDeg( double newValue ) {

        // --------perform limit checking and clamping as required
        if ( newValue >= 0 && newValue <= 360.0 ) {

            // --------set our class varible to the value of newValue
            positionTargetDeg = newValue;
        }

        return;
    }


}
