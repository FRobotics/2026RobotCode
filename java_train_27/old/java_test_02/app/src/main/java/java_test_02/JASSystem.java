package java_test_02;

// --------imports go here....
//import 

public class JASSystem {

    // --------global and class wide created data types go here
    public enum system_state {
        INITIALIZING,
        AT_REST,
        SHOOTING,
        INTAKING,
        OFF
    }

    // --------global and class wide constants can go here
    // --------public items can be accessed outside of this file (our class...)
    public static final double MAX_SPEED = 10.0;
    
    // --------global and class wide variables (including objects)  go here..
    // --------make everything "private" and use getter and setter methods instead
    // --------making values "public"
    // --------private items are only accessable inside this file (class...)
    private static double position = 0.0;
    private static boolean on_or_off_value = false;
    private static system_state our_state = system_state.INITIALIZING;

    // --------This is the mandatory constructor method.   Since this is a
    // --------pseudo "static" class.   You can't make a separate object from this
    // --------class.   Since it is private, no one can call this.
    private JASSystem() {
        return;
    }

    // --------this is a public method to do one time initialization for your system
    public static void init() {
        
        System.out.println( "JASSystem init called");

        // --------initialize sensors

        // --------initialize actuators

        // --------initialize network table variables.

        // --------set starting conditions.

        // --------register our actions with the auto system....FUTURE....

        return;
    }

    // --------this will be called every 20 milliseconds by your robot..
    public static void execute() {

        // --------read sensors

        // --------calculate things (closed loop control and such)
        position = position * 0.4 + 1.0;

        // --------output to actuators.

        // --------update network table variables..

        return;
    }


    // --------this is a public getter for my variable
    public static double getPosition() {
        return position;
    }

    // --------this is private setter for my variable --- only other methods in this class
    // --------can call me
    private static void setPosition( double position_to_set ) {
        position = position_to_set;
        return;
    }


}
