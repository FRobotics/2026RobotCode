// --------file: App.java

package trankdriveone;

import java.util.Scanner;


// =============================================================================================
/**
 *  The main application class for the simulated tank drive robot.  This is NOT high level
 *  simulation.  Rather, inputs and outputs are read and written from the console.
 *<br>
 *  File:   App.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Main<br>
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
 *  1.00    09/28/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-09-28
*/
public class App {
   

    
    // ---------------------------------------------------------------------------------------------
    /**
    *   Constructor.  Doesn't do anything...
    */
    App() {

    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Main program for the application class..
    *
    *   @param args - String array -- command line arguments.
    */
    public static void main(String[] args) {

    	// --------define variables

        // --------only define once, outside the loop.
        Scanner inputScanner = new Scanner(System.in);

        // --------indicates if we are going to stop the loop.
        boolean loopForever = true;

        // -------------------------------------------------------------------
        // --------ROBOT MAIN LOOP.
        // --------loop forever.  Normally for a real robot this loop would have
        // --------a timed wait in it for 20 millisecods
        while ( loopForever ) {

            // --------execute the telop code.
            // --------Since this is a static method, call it by calling the class name
            // --------rather than be calling an object instance.
            TeleopSystem.Execute();

            // --------execute the drive system code.
            // --------Since this is a static method, call it by calling the class name
            // --------rather than be calling an object instance.
            TankDriveSystem.Execute();

            // --------code to see if we want to exit the program.
            if ( TeleopSystem.getLeftRawJoystick() == -9999.0 ) {
                loopForever = false;
            }

            // --------normally the robot code would wait the remainder of 20 milliseconds
            // --------for each loop.
        }

        // --------close the input scanner.
        inputScanner.close();

    }
}
