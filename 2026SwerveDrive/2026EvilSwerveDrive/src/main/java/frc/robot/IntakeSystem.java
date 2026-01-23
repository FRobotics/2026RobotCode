package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtendedOff = false;  // false if up, true if down.
    private static boolean locIntakeExtenedOn = false; 


    
    public static void init() {

        // open motors
        // set system state
        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();


        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");
        locNTSend.addItemBoolean("IntakeIsExtendedOn", IntakeSystem::getIntakeExtended);
        
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    public static boolean getIntakeExtended() {
        return locIntakeExtendedOff;
    }

}
