package frc.robot;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.wpilibj.Timer;

public class SwerveOdometry {

    static private SwerveDrivePoseEstimator locPosEst;
    static void init(){

    }
    
    static void execute(){
        double startTime = Timer.getFPGATimestamp();
        double execElapsedTimeMilli;
        

        double endTime = Timer.getFPGATimestamp();
        double timeElapsed = endTime-startTime;
        execElapsedTimeMilli=timeElapsed*1000;
        
        locPosEst.updateWithTime(timeElapsed, null, null);
    }

    static double getExecElapsedTimeMilli(){
        return(Timer.getFPGATimestamp());
    }

    static void getpose(){
        locPosEst.getEstimatedPosition();
    }

    static double getrotposition(){
        return(locPosEst.getEstimatedPosition().getRotation().getRadians());
        
    }

    static double getxposition(){
        return(locPosEst.getEstimatedPosition().getX());
        
    }

    static double getyposition(){
        return(locPosEst.getEstimatedPosition().getY());
        
    }

    static void setstartingpose(double x, double y, double rotRadians){
        //Rotation2d rot = new Rotation2d(rotRadians);
        //Pose2d newPose = new Pose2d(x, y, rot);
        
    }

}

