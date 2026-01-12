// --------file: TankDriveSystem.java

package trankdriveone;


// =============================================================================================
/**
 *  The TankDriveSystem is a pseudo static class that implements all functions necessary
 *  for a robot tank drive.
 *<br>
 *  File:   TankDriveSystem.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          TankDriveSystem.SetSpeedTarget<br>
 *          TankDriveSystem.Execute<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *          For this to work correctly, the execute method needs to be called every
 *          20 millisecond robot cycle.  <br>
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    09/28/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-09-28
*/
public class TankDriveSystem {

    // ---------------------------------------------------------------------------------------------
    /**
    *   Private constructor.  Can't be called.  Makes this a pseudo static class. 
    *
    * @return nothing
    */
    private TankDriveSystem( ) {

    }

    // --------internal constants.
    static private final double MOTOR_FRICTION = 0.04; // where the motor starts to move.
    static private final double MAX_DRIVE_SPEED = 12.4; // actual max speed 12.4 FT/SEC0
    static private final double DRIVE_MOTOR_SLOPE = ( 1.0 - MOTOR_FRICTION) / 1.0;

    // --------internal variables.
    static private double locLeftSpeedTarget_FPS = 0.0;     // left drive target speed FT/SEC
    static private double locRightSpeedTarget_FPS = 0.0;    // right drive target speed FT/SEC



    // ---------------------------------------------------------------------------------------------
    /**
    *   Set the tank drive speed target for left and right wheels.
    *
    *   @param  leftSpeedTarget_FPS - double - Desired left wheel speed FT/SEC
    *   @param  rightSpeedTarget_FPS - double - Desired right wheel speed FT/SEC
    */
    static public void setSpeedTarget( double leftSpeedTarget_FPS, double rightSpeedTarget_FPS ) {
        locLeftSpeedTarget_FPS = leftSpeedTarget_FPS;
        locRightSpeedTarget_FPS = rightSpeedTarget_FPS;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   Executes the logic of the tank drive system.  This should be called every 20 milliseconds.
    *   It will eventually output values to the motor controllers.
    *
    */
    static public void Execute() {


        double leftDriveMotorOutput_PCT = 0.0;        // left Drive Speed Demand Percent        
        double rightDriveMotorOutput_PCT = 0.0;       // right Drive Speed Demand Percent

        // --------------------------------------------------------------------
        // --------DRIVE CODE

        // --------take speed demand and convert to motor output
        // --------max speed 12.4 feet/sec.
        // --------robot doesn't start to move until output is 0.08

        // --------left drive motor.
        if ( locLeftSpeedTarget_FPS > 0 ) {
            leftDriveMotorOutput_PCT = MOTOR_FRICTION + locLeftSpeedTarget_FPS / MAX_DRIVE_SPEED * DRIVE_MOTOR_SLOPE; 
        }
        else if ( locLeftSpeedTarget_FPS < 0 ) {
            leftDriveMotorOutput_PCT = -MOTOR_FRICTION + locLeftSpeedTarget_FPS / MAX_DRIVE_SPEED * DRIVE_MOTOR_SLOPE; 
        }
        else {
            leftDriveMotorOutput_PCT = 0.0;
        }

        // --------right drive motor
        if ( locRightSpeedTarget_FPS > 0 ) {
            rightDriveMotorOutput_PCT = MOTOR_FRICTION + locRightSpeedTarget_FPS / MAX_DRIVE_SPEED * DRIVE_MOTOR_SLOPE; 
        }
        else if ( locRightSpeedTarget_FPS < 0 ) {
            rightDriveMotorOutput_PCT = -MOTOR_FRICTION + locRightSpeedTarget_FPS / MAX_DRIVE_SPEED * DRIVE_MOTOR_SLOPE; 
        }
        else {
            rightDriveMotorOutput_PCT = 0.0;
        }

        // --------write out to console the motor demands..
        // --------Speed demand.
        System.out.println("motor demand: " + leftDriveMotorOutput_PCT + ", "+ rightDriveMotorOutput_PCT);


    }
    
}
