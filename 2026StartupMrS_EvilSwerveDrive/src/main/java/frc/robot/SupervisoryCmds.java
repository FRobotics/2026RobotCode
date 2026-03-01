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
        TurretLauncher.cmdLauncherOff();
        locMode = "Collecting";
        locNTSend.triggerUpdate();
    }

    // --------shoot balls at target, not intaking
    public static void Shooting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
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
        TurretLauncher.cmdLauncherOff();
        locMode = "Driving Only";
        locNTSend.triggerUpdate();
    }

    // --------collect balls and launch to alliance side
    public static void BallsToAlliance(){
        AgitatorSystem.cmdAgitatorOn(); 
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
        TurretLauncher.cmdLauncherOn();
        TurretLauncher.cmdBallsToZone();
        locMode = "BallsToAlliance";
        locNTSend.triggerUpdate();
    }

    // --------Do you want a "Defend" mode?

    // --------do climb.   BE CAREFULL - IF INTAKE NEEDS TO BE FULLY UP FIRST THIS WONT WORK.
    // --------            DOES THE TURRET NEED TO BE AT ZERO FIRST!
    public static void Climb(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
        locMode = "Climb - Up";
        locNTSend.triggerUpdate();
    }

    // --------climb dowm
    public static void Descend(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
        locMode = "Climb - Down";
        locNTSend.triggerUpdate();
    }

    public static void Defense(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
    }

    // --------get the string describing our mode.
    public static String getMode() {
        return locMode;
    }

}
