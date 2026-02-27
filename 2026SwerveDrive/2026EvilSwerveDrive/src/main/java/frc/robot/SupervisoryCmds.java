package frc.robot;


public class SupervisoryCmds {
    
    public static void Collecting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOnState();
        TurretLauncher.cmdLauncherOff();
    }

    public static void Shooting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
        TurretLauncher.cmdLauncherOn();
        TurretLauncher.cmdBallsToHub();
    }

    public static void StopAction(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOffState();
        TurretLauncher.cmdLauncherOff();
    }

    public static void BallsToAlliance(){
        AgitatorSystem.cmdAgitatorOn(); 
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
        TurretLauncher.cmdLauncherOn();
        TurretLauncher.cmdBallsToZone();
    }

    public static void Climb(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
        
    }

    public static void Descend(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
    }
    public static void Defense(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        TurretLauncher.cmdLauncherOff();
    }


}
