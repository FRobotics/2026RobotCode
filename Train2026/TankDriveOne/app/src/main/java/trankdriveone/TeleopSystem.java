// --------file: TeleopSystem.java

package trankdriveone;

import java.util.Scanner;


// =============================================================================================
/**
 *  The TeleopSystem is a pseudo static class that implements all functions necessary
 *  for robot driver interaction.  This is only used during the Teleop part of the competition.
 *<br>
 *  File:   TeleopSystem.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          TeleopSystem.Execute<br>
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
public class TeleopSystem {

    // ---------------------------------------------------------------------------------------------
    /**
    *   Private constructor.  Can't be called.  Makes this a pseudo static class. 
    */
    private TeleopSystem() {

    }

    // --------internal variables..
    static private final double JOY_DEADBAND = 0.05;    

    // --------only define once.
    static private Scanner inputScanner = new Scanner(System.in);
    static private JoystickScale leftScale = new JoystickScale( JOY_DEADBAND );
    static private JoystickScale rightScale = new JoystickScale( JOY_DEADBAND );

    private static double leftJoyRaw = 0.0;
    private static double rightJoyRaw = 0.0;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get the left raw joystick value.
    *
    *  @return leftJoyRaw - double - Raw value of left joystick.
    */
    static public double getLeftRawJoystick() {
        return leftJoyRaw;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get the right raw joystick value.
    *
    *  @return rightJoyRaw - double - Raw value of right joystick.
    */
    static public double getRightRawJoystick() {
        return rightJoyRaw;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Executes the logic of the tank drive system.  This should be called every 20 milliseconds.
    *   It will eventually output values to the motor controllers.
    *
    */
    static public void Execute() {


        double leftJoyScaled = 0.0;
        double rightJoyScaled = 0.0;

        double leftDriveSpeedDmd_FPS = 0.0;        // left Drive Speed Demand FT/Sec        
        double rightDriveSpeedDmd_FPS = 0.0;       // right Drive Speed Demand FT/Sec


        // --------account for joystick slop around 0.
        // final double JOY_DEADBAND = 0.05;
        // final double JOY_SLOPE = 1.0 / ( 1.0 - JOY_DEADBAND);
        final double MAX_TELEOP_DRIVE_SPEED = 12.0;     // FT/SEC

        // --------read joystick values.....

        // --------read joystick values fron console.
        System.out.println("Enter a value for left joy: ");
        leftJoyRaw = inputScanner.nextDouble();

        System.out.println("Enter a value for right joy: ");
        rightJoyRaw = inputScanner.nextDouble();

        // --------let user know what they typed
        System.out.println("Entered x and y are: " + leftJoyRaw + ", "+ rightJoyRaw);

        // --------characterize joystick for easier drving.  square..
        // --------account for joystick reading non-zero around zero.  Add 
        // --------a deadband.
        leftJoyScaled = leftScale.Execute( leftJoyRaw );
        rightJoyScaled = rightScale.Execute( rightJoyRaw );

        // --------convert to drive speed demand  (max allowed driving speed
        // --------is 12 feet/second.)
        leftDriveSpeedDmd_FPS = MAX_TELEOP_DRIVE_SPEED * leftJoyScaled;
        rightDriveSpeedDmd_FPS = MAX_TELEOP_DRIVE_SPEED * rightJoyScaled;

        // -------- tell the tank drive system how fast we want to go...
        TankDriveSystem.setSpeedTarget( leftDriveSpeedDmd_FPS, rightDriveSpeedDmd_FPS );
        
        // --------let user know what they typed
        System.out.println("Scaled left and right are: " + leftJoyScaled + ", "+ rightJoyScaled);

        // --------Speed demand.
        System.out.println("Scaled left and right Speed Demand: " + leftDriveSpeedDmd_FPS + ", "+ rightDriveSpeedDmd_FPS);


    }


    
}
