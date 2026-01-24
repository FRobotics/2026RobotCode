package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.Timer;



public class SwerveOdometry {

    
    static private SwerveDrivePoseEstimator locPoseEst;
    static private Lib4150NetTableSystemSend sender = new Lib4150NetTableSystemSend("Odometry");
    static private double xpos = 0.0;
    static private double ypos = 0.0;
    static private double rotpos = 0.0;
    static private SwerveDriveOdometry swerveOdometry;

    static void init(){
        Pose2d initPose = new Pose2d(0.0,0.0,new Rotation2d(0.0));
        locPoseEst = new SwerveDrivePoseEstimator( SwerveDrive.publicDriveKinematics, new Rotation2d(SwerveDrive.getYaw()),  null, initPose );
        swerveOdometry = new SwerveDriveOdometry(SwerveDrive.publicDriveKinematics, new Rotation2d(SwerveDrive.getRoll()), SwerveDrive.getModulePositions(), initPose);

        sender.addItemDouble("X position", SwerveOdometry::getxposition);
        sender.addItemDouble("Y position", SwerveOdometry::getyposition);
        sender.addItemDouble("Rotation position", SwerveOdometry::getrotposition);


    }
    
    static void execute(){
        double startTime = Timer.getFPGATimestamp();
        double endTime = Timer.getFPGATimestamp();
        double getElasped= endTime - startTime;
        double execElapsedTime;

        execElapsedTime=getElasped*1000;
// TODO: currTimeSecs, gyroangle(Rotation2D), wheelPosition(serveModukePosition{})
        locPoseEst.updateWithTime(execElapsedTime, null, null);
        xpos = locPoseEst.getEstimatedPosition().getX();
        ypos = locPoseEst.getEstimatedPosition().getY();
        rotpos = locPoseEst.getEstimatedPosition().getRotation().getRadians();
        
        sender.triggerUpdate();
    }

    static double getExecElapsedTimeMilli(){
        return(Timer.getFPGATimestamp());
    }

    static void getpose(){
      locPoseEst.getEstimatedPosition();
    }

    static double getrotposition(){
        return(rotpos);
    }

    static double getxposition(){
        //getEstimatedPosition is from Pose2D
        return(xpos);
    }

    static double getyposition(){
        return(ypos);
    }

    static void setstartingpose(double xPos, double yPos, double rotRadians){
        Rotation2d rot= new Rotation2d(rotRadians);
        Pose2d newPose = new Pose2d(xPos, yPos, rot);
        //Pose2D is object that stores x,y,and rotation in radians. 
        locPoseEst.resetPose(newPose);
        locPoseEst.resetPosition(rot, null, newPose);
        //TODO: change DriveSwerveSystem.getCurrentFieldRotation, DriveSwerveSystem.getModulePositions, newPose
    }

    
}

