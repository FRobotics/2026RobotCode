
package frc.robot;

import java.util.Optional;

import choreo.Choreo;
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;


public class TrajectorySystem {
   
    private static Choreo.TrajectoryCache TrajectoryStorage;

    // things for executing the trajectory
    private static Trajectory<SwerveSample> TrajectoryToRun;
    // TODO: add starting time, add initComplete

    
    private TrajectorySystem(){

    }

    public static void TrajectoryInit() {

        TrajectoryStorage = new Choreo.TrajectoryCache();

        String[] TrajectoryNames = Choreo.availableTrajectories();

        for ( String oneTraj : TrajectoryNames     )  {

            TrajectoryStorage.loadTrajectory(oneTraj);

        }
    


    }

    @SuppressWarnings("unchecked")
    public static boolean FollowTrajectory(Boolean Init, String TrajectoryName){

        if(Init) {
            var tryToLoadTrajectory = TrajectoryStorage.loadTrajectory(TrajectoryName);
            if ( tryToLoadTrajectory.isPresent()) {
                TrajectoryToRun = (Trajectory<SwerveSample>)tryToLoadTrajectory.get();
            }
        }
        return false;
    }
}
