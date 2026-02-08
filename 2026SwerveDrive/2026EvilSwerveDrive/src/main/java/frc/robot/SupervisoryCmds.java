package frc.robot;


public class SupervisoryCmds {
    
    public static void Collecting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOnState();
    }

    public static void Shooting(){
        AgitatorSystem.cmdAgitatorOn();
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
    }

    public static void StopAction(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setDownOffState();
    }

    public static void BallsToAlliance(){
        AgitatorSystem.cmdAgitatorOn(); 
        FeederSystem.cmdFeederOn();
        IntakeSystem.setDownOffState();
    }

    public static void Climb(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
        
    }

    public static void Descend(){
        AgitatorSystem.cmdAgitatorOff();
        FeederSystem.cmdFeederOff();
        IntakeSystem.setUpOffState();
    }


}
