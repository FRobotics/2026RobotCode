
package frc.robot;

import choreo.Choreo;


public class TrajectorySystem {
   
    private static Choreo.TrajectoryCache TrajectoryStorage;

    private static Trajectory<SwerveSample> TrajectoryToRun;

    private TrajectorySystem(){

    }

    public static void TrajectoryInit() {

        TrajectoryStorage = new Choreo.TrajectoryCache();

        String[] TrajectoryNames = Choreo.availableTrajectories();

        for ( String oneTraj : TrajectoryNames     )  {

            TrajectoryStorage.loadTrajectory(oneTraj);

        }
    


    }

    public static boolean FollowTrajectory(Boolean Init, String TrajectoryName){

        if(Init){
            TrajectoryToRun = TrajectoryStorage.loadTrajectory(TrajectoryName);
        }
    }

}
