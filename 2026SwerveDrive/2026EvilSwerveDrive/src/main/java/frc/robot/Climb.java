package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class Climb {

    private Climb(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    private static boolean locClimbExtended = false;  // false if up, true if down.

    public static void init() {

        // TODO: open and configure motors
        // TODO: open and configure sensors.
        // TODO: set initial system state
        // TODO: register command names with auto (so they can be called by name.)

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("Climb");

       
        locNTSend.addItemBoolean("ClimbIsExtended", Climb::getClimbExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    
    public static boolean getClimbExtended() {
        return locClimbExtended;
    }

}
