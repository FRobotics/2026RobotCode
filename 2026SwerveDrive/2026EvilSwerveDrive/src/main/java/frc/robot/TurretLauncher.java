package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class TurretLauncher {

    private TurretLauncher(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.

    public static void init() {

        // open motors
        // set system state
        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    public static boolean getAgitatorExtended() {
        return locIntakeExtended;
    }

}

