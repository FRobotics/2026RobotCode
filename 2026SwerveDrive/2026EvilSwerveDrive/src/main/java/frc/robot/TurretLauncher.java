package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class TurretLauncher {

    private TurretLauncher(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    // TODO: This isnt the intake system.  Remove or revise.
    private static boolean locIntakeExtended = false;  // false if up, true if down.

    public static void init() {

        // TODO: open and configure motors
        // TODO: open and configure sensors
        // TODO: set initial system state

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");

        // TODO: This isn't the intake system.  Remove or revise.
        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    // TODO: This isn't the agitator system.  Remove or revise.
    public static boolean getAgitatorExtended() {
        return locIntakeExtended;
    }

}

