package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class Climb {

    private Climb(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    // TODO: This isn't the intake system.  Remove or change.
    private static boolean locIntakeExtended = false;  // false if up, true if down.

    public static void init() {

        // TODO: open and configure motors
        // TODO: open and configure sensors.
        // TODO: set initial system state
        // TODO: register command names with auto (so they can be called by name.)

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("Climb");

        // TODO: This isnt the intake system. change to climb functions..
        locNTSend.addItemBoolean("ClimbIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    // TODO: This isnt the agitator system.  Remove or change ..
    public static boolean getAgitatorExtended() {
        return locIntakeExtended;
    }

}
