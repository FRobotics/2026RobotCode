package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class SupervisoryCmds {

    private static Lib4150NetTableSystemSend locNTSend;
    private static String locMode = "Starting";
    
     // --------private constructor.
    private SupervisoryCmds() {
    }

    // --------in order to do network tables an init function is needed to define the value.
    public static void init() {
        locNTSend = new Lib4150NetTableSystemSend("Supervisory");
        locNTSend.addItemString("Mode",SupervisoryCmds::getMode);
        locNTSend.triggerUpdate();
    }
 
    // --------collecting balls
    public static void Collecting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOnState();
        IntakeSystem.cmdRockDisable();  // redundant.
        TurretLauncher.cmdLauncherOff();
        locMode = "Collecting";
        locNTSend.triggerUpdate();
    }

    // --------shoot balls at target, not intaking
    public static void Shooting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOn();
        // IntakeSystem.setDownOffState();
        IntakeSystem.setDownOffState();
        //IntakeSystem.cmdRockEnable();   // enable intake rocking... 
        TurretLauncher.cmdLauncherOn();
        TurretLauncher.cmdBallsToHub();
        locMode = "Shooting";
        locNTSend.triggerUpdate();
    }

    // --------do nothing -- normal driving with intake down.
    public static void StopAction(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOffState();
        IntakeSystem.cmdRockDisable();  // redundant.
        TurretLauncher.cmdLauncherOff();
        locMode = "Driving Only";
        locNTSend.triggerUpdate();
    }

    // --------collect balls and launch to alliance side
    public static void BallsToAlliance(){
        AgitatorSystem.cmdAgitatorOn(); 
        FeederSystem.cmdFeederOn();
        // IntakeSystem.setDownOffState();
        IntakeSystem.setDownOnState();
        IntakeSystem.cmdRockDisable();  // redundant.
        TurretLauncher.cmdLauncherOn();
        TurretLauncher.cmdBallsToZone();
        locMode = "BallsToAlliance";
        locNTSend.triggerUpdate();
    }

    // --------do climb.   BE CAREFULL - IF INTAKE NEEDS TO BE FULLY UP FIRST THIS WONT WORK.
    // --------            DOES THE TURRET NEED TO BE AT ZERO FIRST!
    public static void ClimbExtend(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOffState();
        IntakeSystem.cmdRockDisable();  // redundant.
        TurretLauncher.cmdLauncherOff();
        Climb.cmdExtendInc();
        locMode = "Climb - Extend";
        locNTSend.triggerUpdate();
    }

    // --------climb dowm (retract)
    public static void ClimbRetract(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOffState();
        IntakeSystem.cmdRockDisable();  // redundant.
        TurretLauncher.cmdLauncherOff();
        Climb.cmdRetractInc();
        locMode = "Climb - Retract";
        locNTSend.triggerUpdate();
    }

    // --------play defense
    public static void Defense(){
        // AgitatorSystem.cmdAgitatorOff();
        // FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        // IntakeSystem.cmdRockDisable();  // redundant.
        // TurretLauncher.cmdLauncherOff();
    }


    // --------get the string describing our mode.
    public static String getMode() {
        return locMode;
    }

    // --------execute a supervisory command based on its name.
    public static void executeString( String cmdName ) {

        // --------collecting
        if ( cmdName.equalsIgnoreCase("collecting")) {
            Collecting();
        }
        else if ( cmdName.equalsIgnoreCase("shooting")) {
            Shooting();
        }
        else if ( cmdName.equalsIgnoreCase("stopaction")) {
            StopAction();
        }
        else if ( cmdName.equalsIgnoreCase("ballstoalliance")) {
            BallsToAlliance();
        }
        else if ( cmdName.equalsIgnoreCase("climb")) {
            ClimbExtend();
        }
        else if ( cmdName.equalsIgnoreCase("descend")) {
            ClimbRetract();
        }
        else if ( cmdName.equalsIgnoreCase("defense")) {
            Defense();
        }
        return;

    }

}
