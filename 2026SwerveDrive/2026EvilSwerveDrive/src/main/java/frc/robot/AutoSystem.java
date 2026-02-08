package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
public class AutoSystem {

    private static Lib4150NetTableSystemSend locNTSend;

    private static int locExecIndex = 0;
    private static int locExecLastIndex = -1;
    private static boolean locExecInitStep = true;
    private static AutoRoutine locExecRoutine;
    private static Timer autoTimer = new Timer();
    private static boolean locExecDoNextStep = false;
    

    public static void ExecuteListInit(AutoRoutine autoToRun ) {

        // -------get the routine to run
        locExecRoutine = autoToRun;

        // -------init the index into our auto to run
        locExecIndex = 0;
        // -------init the last step we used
        locExecLastIndex = -1; // non-sense for first time

        // -------we should init this step...
        locExecInitStep = true;
    }

     // TODO: This may have to change.  It looks like java wants to call the execute routine every 20ms.  The concept is the same...not a for loop though..
    public static void ExecuteList(){


        AutoStep ourStep = locExecRoutine.getStep(locExecIndex);
        
        // --------is this the first time for this step
        if ( locExecIndex != locExecLastIndex ) {
                locExecInitStep = true;
                locExecLastIndex = locExecIndex;
        }
        else {
                locExecInitStep = false;
        }

        // First time in this step.  if so reset timer.
        if ( locExecInitStep ) {
                autoTimer.reset();
                autoTimer.start();
        }

        locExecDoNextStep = false;

        // do step
        switch (ourStep.getCmd()){
                case DriveStraight:
                        // locExecDoNextStep = DoDriveStraight( parms )''
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

                case Collect:
                        SupervisoryCmds.Collecting();
                        locExecDoNextStep = true;
                        break;

                case Shoot:
                        SupervisoryCmds.Shooting();
                        locExecDoNextStep = true;
                        break;

                case Stop:
                        SupervisoryCmds.StopAction();
                        locExecDoNextStep = true;
                        break;

                case BallsToAlliance:
                        SupervisoryCmds.BallsToAlliance();
                        locExecDoNextStep = true;
                        break;
                
                case Climb:
                        SupervisoryCmds.Climb();
                        locExecDoNextStep = true;
                        break;
                
                case Descend:
                        SupervisoryCmds.Descend();
                        locExecDoNextStep = true;
                        break;
        }

        // did we time out.
        if ( ourStep.getTimeout() > 0.0 ) {
                if ( autoTimer.hasElapsed(ourStep.getTimeout()) ) { 
                        locExecDoNextStep = true;
                }
        }

        // should we do next step
        if ( locExecDoNextStep ) {
                locExecIndex++;
        }
        

    } 

     public static void init() {

        autoTimer.reset();
        autoTimer.start();
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
