package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

public class AutoSystem {

    private static Lib4150NetTableSystemSend locNTSend;
    
    
     static void execute(){}

    

     // TODO: This may have to change.  It looks like java wants to call the execute routine every 20ms.  The concept is the same...not a for loop though..
    public static void ExecuteList(AutoRoutine AutoList){

        int count = AutoList.getStepAmount();
        for ( int index = 0; index < count; index++){
            //get step
            AutoStep ourStep = AutoList.getStep(index);   
            
            // run step
            switch (ourStep.getCmd()){
                case DriveStraight:
                        break;
                
                case DriveTurn:
                        break;

                case FollowAbsTrajectory:
                        break;

                case FollowRelTrajectory:
                        break;

                case AutoWait:
                        break;

                case FollowAbsTrajWithTimedCmd:
                        break;

                case FollowRelTrajWithTimedComd:
                        break;

                case IntakUpOff:
                      
                        break;

                case IntakeDownOff:
                        break;

                case IntakeDownOn:
                        break;
            }
        
        }

    } 

     public static void init() {

        // TODO: remove the comments about opening motors.   Auto system has no motors..
        // open motors
        // set system state

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AutoSystem");

        //locNTSend.addItemBoolean(, AutoSystem::);
        
        //locNTSend.triggerUpdate(, AutoSystem::);

        
        
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    
    






}
