package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class AgitatorSystem {

    private AgitatorSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TODO: this isn't the intake system.  Remove or revise.
    private static boolean locIntakeExtended = false;  // false if up, true if down.

    public static void init() {

        // TODO: open and configure motors
        // TODO: open and configure sensors
        // TODO: set initial system state

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AgitatorSystem");

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    // TODO: Rename or replace variable named Intake...
    public static boolean getAgitatorExtended() {
        return locIntakeExtended;
    }

}
