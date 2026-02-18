
package frc.robot;

// TODO: Remove unused imports.
import java.security.Timestamp;
import java.util.Optional;

import Lib4150.Lib4150NetTableSystemSend;
import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;

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

    private TrajectorySystem(){

    }

    public static void TrajectoryInit() {

        TrajectoryStorage = new Choreo.TrajectoryCache();

        String[] TrajectoryNames = Choreo.availableTrajectories();

        for ( String oneTraj : TrajectoryNames     )  {

            TrajectoryStorage.loadTrajectory(oneTraj);

        }
    
     locNTSend = new Lib4150NetTableSystemSend("TrajectorySystem");
     locNTSend.addItemDouble("ElapsedTrajTime", TrajectorySystem::getElapsedTrajTime);
     locNTSend.addItemDouble("xErr", TrajectorySystem::getxErr);
     locNTSend.addItemDouble("yErr", TrajectorySystem::getyErr);
     locNTSend.addItemDouble("rotErr", TrajectorySystem::getrotErr);   
     // TODO: add network table entries for ontarget, done, havesample, timelengthoftrajectory (rename if you don't like name)
    }

    @SuppressWarnings("unchecked")
    //TODO: should this be a primative???
    public static boolean FollowTrajectory(Boolean Init, String TrajectoryName, double SystemElapsedTime){

        if(Init) {
            startTime = SystemElapsedTime;
            var tryToLoadTrajectory = TrajectoryStorage.loadTrajectory(TrajectoryName);
            if ( tryToLoadTrajectory.isPresent()) {
                TrajectoryToRun = (Trajectory<SwerveSample>)tryToLoadTrajectory.get();
            }
        }

        // --------calc elapsed time for trajectory
        Double ElapsedTime = SystemElapsedTime - startTime;

        Optional<SwerveSample> oursample = TrajectoryToRun.sampleAt(ElapsedTime, false);

        SwerveSample realsample;
        if ( oursample.isPresent() ) {
            realsample = oursample.get();
            havesample = true;
            xErr = ( realsample.x - SwerveOdometry.getxposition());
            double xvelDmd = realsample.vx + KX * xErr;
            double yvelDmd = realsample.vy + KY * ( realsample.y - SwerveOdometry.getyposition());
            double rotvelDmd = realsample.omega + KRot * ( realsample.heading - SwerveOdometry.getrotposition());

            if ( Math.abs(xErr) < 2 && Math.abs(yErr) < 2 && Math.abs(rotErr) < Units.degreesToRadians(3)){
                ontarget = true;
            }
            else {
                ontarget = false;
            }        
            
            

            SwerveDrive.setDesiredSpeed(new ChassisSpeeds( xvelDmd, yvelDmd, rotvelDmd));
        }
        else {
            SwerveDrive.setDesiredSpeed(new ChassisSpeeds( 0, 0, 0));
            havesample = false;
            ontarget = false;
        }

        // TODO: add calculate of "complete" and return "complete" to user.  complete = elapsedtime >= trajectory time length AND onTarget.    

        return false;

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
    // TODO: add getters for new things written to network tables.  ontarget, havesample,done.
}
