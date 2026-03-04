
package frc.robot;

import java.util.ArrayList;
import java.util.Optional;

import Lib4150.Lib4150MessageUtil;
import Lib4150.Lib4150NetTableSystemSend;

import choreo.Choreo;
import choreo.trajectory.EventMarker;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;

public class TrajectorySystem {


     
    private static final double KX = 10.0; 
    private static final double KY = 10.0;
    private static final double KRot = 7.5; //KRot needs to be adjusted based on robot preformance

    private static Lib4150NetTableSystemSend locNTSend;
    private static Choreo.TrajectoryCache TrajectoryStorage;

    // things for executing the trajectory
    private static Trajectory<SwerveSample> TrajectoryToRun;
    // TODO: add starting time, add initComplete
    private static double startTime = 0.0;
    private static double elapsedTrajTime = 0.0;
    private static double xErr = 0.0;
    private static double yErr = 0.0;
    private static double rotErr = 0.0;
    private static boolean ontarget = false;
    private static boolean done = false;
    private static boolean havesample = false;
    private static double timeLengthOfTrajectory = 0.0;
    private static ArrayList<TrajectoryEvent> events;

    private TrajectorySystem(){

    }

    public static void TrajectoryInit() {

        TrajectoryStorage = new Choreo.TrajectoryCache();
        

        String[] TrajectoryNames = Choreo.availableTrajectories();

        for ( String oneTraj : TrajectoryNames     )  {

            TrajectoryStorage.loadTrajectory(oneTraj);

            // --------some debug...for event markers...
            // var tryToLoadTrajectory = TrajectoryStorage.loadTrajectory(oneTraj);
            // Trajectory<SwerveSample> junk;
            // if ( tryToLoadTrajectory.isPresent()) {
            //     junk = (Trajectory<SwerveSample>)tryToLoadTrajectory.get();
            //     // --------loop over events.
            //     for ( EventMarker myEvent : junk.events()) {
            //         Lib4150MessageUtil.SendInfo("Event marker :"+myEvent.timestamp+"--"+myEvent.event);
            //     }
            // }

        }
        // --------tell match system trajectories are read
        MatchSystem.setRobotPhaseTrajectoriesRead();
    
        locNTSend = new Lib4150NetTableSystemSend("TrajectorySystem");
        locNTSend.addItemDouble("ElapsedTrajTime", TrajectorySystem::getElapsedTrajTime);
        locNTSend.addItemDouble("xErr", TrajectorySystem::getxErr);
        locNTSend.addItemDouble("yErr", TrajectorySystem::getyErr);
        locNTSend.addItemDouble("rotErr", TrajectorySystem::getrotErr);
        locNTSend.addItemBoolean("onTarget", TrajectorySystem::getOnTarget);
        locNTSend.addItemBoolean("done", TrajectorySystem::getDone);
        locNTSend.addItemBoolean("haveSample", TrajectorySystem::gethaveSample);
        locNTSend.addItemDouble("timeLengthOfTrajectory", TrajectorySystem::getTimeLengthOfTrajectory);
    }

    @SuppressWarnings("unchecked")
    //TODO: should this be a primative???
    public static boolean FollowTrajectory(Boolean Init, String TrajectoryName, double SystemElapsedTime){

        if(Init) {
            startTime = SystemElapsedTime;
            var tryToLoadTrajectory = TrajectoryStorage.loadTrajectory(TrajectoryName);
            if ( tryToLoadTrajectory.isPresent()) {
                TrajectoryToRun = (Trajectory<SwerveSample>)tryToLoadTrajectory.get();
                timeLengthOfTrajectory = TrajectoryToRun.getTotalTime();
                // --------create list of events..
                events = new ArrayList<TrajectoryEvent>();
                for ( EventMarker oneEvent : TrajectoryToRun.events() ) {
                    events.add( new TrajectoryEvent( oneEvent.timestamp, oneEvent.event));
                }
            }
        }

        // --------calc elapsed time for trajectory
        Double elapsedTime = SystemElapsedTime - startTime;

        elapsedTrajTime = elapsedTime;

        Optional<SwerveSample> oursample = TrajectoryToRun.sampleAt(elapsedTime, false);

        SwerveSample realsample;
        if ( oursample.isPresent() ) {
            realsample = oursample.get();
            havesample = true;
            xErr = ( realsample.x - SwerveOdometry.getxposition());
            yErr = ( realsample.y - SwerveOdometry.getyposition());
            // rotErr = ( realsample.heading - SwerveOdometry.getrotposition());
            rotErr = MathUtil.angleModulus( realsample.heading - SwerveOdometry.getrotposition());
            double xvelDmd = realsample.vx + KX * xErr;
            double yvelDmd = realsample.vy + KY * yErr;
            // double rotvelDmd = MathUtil.angleModulus(realsample.omega + KRot * rotErr);
            double rotvelDmd = realsample.omega + KRot * rotErr;

            if ( Math.abs(xErr) < 2 && Math.abs(yErr) < 2 && Math.abs(rotErr) < Units.degreesToRadians(3)){
                ontarget = true;
            }
            else {
                ontarget = false;
            }        
            SwerveDrive.setDesiredSpeed(new ChassisSpeeds( xvelDmd, yvelDmd, rotvelDmd));
            // -------- is it time to run an event
            // -------- we have some events.
            if ( events.size() > 0 ) {
                // --------is is time to run an event?  (for now assume events are at least 20ms apart., if not we need a loop).
                if ( events.get(0).hasTimeElapsed(elapsedTime) ) {
                    // --------run the event.
                    SupervisoryCmds.executeString(events.get(0).getEventName());
                    // --------some debug.
                    Lib4150MessageUtil.SendInfo("Trajectory running event:"+events.get(0).getEventName()+" @ "+events.get(0).getTimeOffsetSec());
                    // --------remove the event so we don't run it twice.
                    events.remove(0);
                }
            }
        }
        else {
            SwerveDrive.setDesiredSpeed(new ChassisSpeeds( 0, 0, 0));
            havesample = false;
            ontarget = false;
        }
        
        boolean complete = (elapsedTime >= TrajectoryToRun.getTotalTime()) && ontarget;  
        done = complete;

        locNTSend.triggerUpdate();

        return complete;

    }
    public static double getElapsedTrajTime(){
        return elapsedTrajTime;
    }
    public static double getxErr(){
        return xErr;
    }
    public static double getyErr(){
        return yErr;
    }
    public static double getrotErr(){
        return rotErr;
    }
    public static boolean getOnTarget(){
        return ontarget;
    }
    public static boolean getDone(){
        return done;
    }
    public static boolean gethaveSample(){
        return havesample;
    }
    public static double getTimeLengthOfTrajectory(){
        return timeLengthOfTrajectory;
    }
    // TODO: add getters for new things written to network tables.  ontarget, havesample,done.
}
