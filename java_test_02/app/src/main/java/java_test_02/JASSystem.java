package java_test_02;

public class JASSystem {

    // --------global and class wide created data types go here

    // --------global and class wide constants can go here
    public static final double MAX_SPEED = 10.0;
    
    // --------global and class wide variables (including objects)  go here..
    // --------make everything "private" and use getter and setter methods instead
    // --------making values "public"
    private static double position = 0.0;

    // --------This is the mandatory constructor method.   Since this is a
    // --------pseudo "static" class.   You can't make a separate object from this
    // --------class
    private JASSystem() {
        return;
    }

    // --------this is a public method to do one time initialization for your system
    public static void init() {
        
        System.out.println( "JASSystem init called");

        return;
    }

    // --------this will be called every 20 milliseconds by your robot..
    public static void execute() {
        position = position * 0.4 + 1.0;
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
