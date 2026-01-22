package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class evilIntakesystem {

    private evilIntakesystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.

    public static void init() {

        // open motors
        // set system state
        // init network table
        locNTSend = new Lib4150NetTableSystemSend("EvilIntakeSystem");

        locNTSend.addItemBoolean("IntakeIsExtended", evilIntakesystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    public static boolean getIntakeExtended() {
        return locIntakeExtended;
    }

}
