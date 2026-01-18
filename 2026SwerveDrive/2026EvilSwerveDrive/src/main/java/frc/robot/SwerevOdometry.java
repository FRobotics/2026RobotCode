package frc.robot;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;


public class SwerevOdometry {

    
    static private SwerveDrivePoseEstimator locPoseEst;
     
    static void init(){
       
    }
    
    static void execute(){
        double startTime = Timer.getFPGATimestamp();
        double endTime = Timer.getFPGATimestamp();
        double getElasped= endTime - startTime;
        double execElapsedTime;

        execElapsedTime=getElasped*1000;
// todo: currTimeSecs, gyroangle(Rotation2D), wheelPosition(serveModukePosition{})
        locPoseEst.updateWithTime(execElapsedTime, null, null);
        
    }

    static double getExecElapsedTimeMilli(){
        return(Timer.getFPGATimestamp());
    }

    static void getpose(){
      locPoseEst.getEstimatedPosition();
    }

    static double getrotposition(){
        locPoseEst.getEstimatedPosition().getRotation().getRadians();
        return(0.0);
    }

    static double getxposition(){
        locPoseEst.getEstimatedPosition().getX();
        //getEstimatedPosition is from Pose2D
        return(0.0);
    }

    static double getyposition(){
        locPoseEst.getEstimatedPosition().getY();
        return(0.0);
    }

    static void setstartingpose(double xPos, double yPos, double rotRadians){
        Rotation2d rot= new Rotation2d(rotRadians);
        Pose2d newPose = new Pose2d(xPos, yPos, rot);
        //Pose2D is object that stores x,y,and rotation in radians. 
        locPoseEst.resetPose(newPose);
        locPoseEst.resetPosition(rot, null, newPose);
        //Todo: change DriveSwerveSystem.getCurrentFieldRotation, DriveSwerveSystem.getModulePositions, newPose
    }

}

