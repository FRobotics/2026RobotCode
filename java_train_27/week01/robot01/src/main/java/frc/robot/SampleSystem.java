// --------file: SampleSystem.java

// --------this is the name of a group of files that go together.  
// --------In our case, all files residing under the same directory
// --------need to have the same package name.
package frc.robot;

// --------This imports definitions of library routines that this file
// --------uses.  These include standard java things, and WPILIB robotics
// --------library things, and things that you may create.
//import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import Lib4150.Lib4150NetTableSystemSend;


// =============================================================================================
/**
 *  Describe what this system is supposed to do...... 
 * 
 *<br>
 *  File:   SampleSystem.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          SampleSystem<br>
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
 *  1.00    mm/dd/yyyy  Your name     Created.<br>
 *  1.01    mm/dd/yyyy  Your name     Description of what changed...<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.01
 * @since      yyyy-mm-dd
*/
public class SampleSystem {

    // =============================================================================================
    // --------public/global data types go here...  These are available outside of this file.

    // =============================================================================================
    // --------private data types go here...  These are only available inside this file.

    // =============================================================================================
    // --------public/global constants go here.  These are available outside of this file.
    // --------These should be defined as "public static final"

    // =============================================================================================
    // --------private constants go here.  These are only available inside this file.
    // --------These should be defined as "private static final"
    private static DigitalInput dig_in_a;
    private static DigitalInput dig_in_b;
    private static DigitalInput dig_in_c;
    private static DigitalOutput dig_out;

    private static boolean dig_a = false;
    private static boolean dig_b = false;
    private static boolean dig_c = false;

    private static boolean calc_1 = false;
    private static boolean calc_2 = false;
    private static boolean calc_3 = false;
    private static boolean calc_out = false;

    private static Lib4150NetTableSystemSend ntvals;


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

    // =============================================================================================
    /**
     * --------This is the "constructor" method.  It is required.  It must have exactly the 
     * --------same name as the class.  Since our robot systems are "pseudo static" classes,
     * --------this must be defined as "private", so that others can't try and instantiate
     * --------an object of this class.
     */
    private SampleSystem( ) {
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
        dig_in_a = new DigitalInput( 1 );
        dig_in_b = new DigitalInput( 2 );
        dig_in_c = new DigitalInput( 3 );

        dig_out = new DigitalOutput(5);

        // --------create objects and set initial configuration and scaling for actuators

        // --------set initial position, speed, control, system state data.

        // --------create all network table variables.
        ntvals = new Lib4150NetTableSystemSend( "SampleSystem" );

        ntvals.addItemBoolean("DigIn01", SampleSystem::getDigA );
        ntvals.addItemBoolean("DigIn02", SampleSystem::getDigB );
        ntvals.addItemBoolean("DigIn03", SampleSystem::getDigC );
        ntvals.addItemBoolean("DigOut", SampleSystem::getDigOut );

        // --------back to the main robot code.
        return;
    }

     // =============================================================================================
     /**
      * This routine is called by the main robot code every 20 milliseconds.  It is called regardless
      * of robot mode or game phase.  This routine should read all sensors, scale and filter input
      * data, perform all calculations and control logic, output values to actuators, then update
      * network table variables.
      */
    public static void execute( ) {

        // --------read and scale all sensors.  This includes reading any network table values
        // --------such as data from camera systems.
        dig_a = dig_in_a.get();
        dig_b = dig_in_b.get();
        dig_c = dig_in_c.get();

        // --------filter all sensors.

        // --------do all control calculations.
        // --------This is a sum of products combinatorial logic calculation
        calc_1 = dig_a && dig_b && !dig_c;
        calc_2 = dig_a && !dig_b && dig_c;
        calc_3 = !dig_a && dig_b && dig_c;
        calc_out = calc_1 || calc_2 || calc_3;

        // --------write control results to actuators
        dig_out.set(calc_out);

        // --------trigger network table value output
        ntvals.triggerUpdate();

        // --------return to robot to do other systems...
        return;
    }

    // =============================================================================================
    /**
     * Sample public method to get the value of something from this system.
     * Create as many of these routines as needed for this system.  Anything that will written
     * to network tables will need a getter routine.
     * @return - double - Description and UNITS of what is returned.
     */
    public static double getSomeDouble( ) {
        return 0.0;     // return our class value
    }
    /**
     * Get digital input 1
     * @return - boolean - value of digital input 1
     */
    public static boolean getDigA( ) {
        return dig_a;     // return our class value
    }
    /**
     * Get digital input 2
     * @return - boolean - value of digital input 2
     */
    public static boolean getDigB( ) {
        return dig_b;     // return our class value
    }
    /**
     * Get digital input 3
     * @return - boolean - value of digital input 3
     */
    public static boolean getDigC( ) {
        return dig_c;     // return our class value
    }
    /**
     * Get digital output
     * @return - boolean - value of digital output
     */
    public static boolean getDigOut( ) {
        return calc_out;     // return our class value
    }

    // =============================================================================================
    /**
     * Sample public method to set the value of a variable in this class.  This routine only needs
     * to exist if code outside of this class needs to set the value.
     * This routine should do whatever limit checking and clamping as needed to ensure the new
     * value is valid
     * @param newValue - double - description and UNITS of value to update
     */
    public static void setSomeDouble( double newValue ) {

        // --------perform limit checking and clamping as required!

        // --------set our class varible to the value of newValue

        return;
    }


}
