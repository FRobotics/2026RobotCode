package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.Timer;



public class SwerveOdometry {

    
    static private SwerveDrivePoseEstimator locPoseEst;
    // TODO: suggest creating the object during init.  Network Table may not be initialized yet!!
    static private Lib4150NetTableSystemSend sender = new Lib4150NetTableSystemSend("Odometry");
    static private double xpos = 0.0;
    static private double ypos = 0.0;
    static private double rotpos = 0.0;
    // TODO: if swerveOdometry was replaced by locPoseEst then remove...
    static private SwerveDriveOdometry swerveOdometry;

    // TODO: define function as public or private
    static void init(){
        // TODO: put some comments here.  For example, this is the initial robot position (pose)
        Pose2d initPose = new Pose2d(0.0,0.0,new Rotation2d(0.0));
        // TODO: should Yaw be Roll here???
        // TODO: modulePositions can't be null.... it looks like you can call SwerveDrive.getModulePositions()...
        locPoseEst = new SwerveDrivePoseEstimator( SwerveDrive.publicDriveKinematics, new Rotation2d(SwerveDrive.getYaw()),  null, initPose );
        // TODO: if using PoseEstimation instead of Odometry, remove "swerveOdometry".
        swerveOdometry = new SwerveDriveOdometry(SwerveDrive.publicDriveKinematics, new Rotation2d(SwerveDrive.getRoll()), SwerveDrive.getModulePositions(), initPose);

        // TODO: Suggest creating the sender object here and not during declaration.  The Network Table server may have been initialized prior to now.
        sender.addItemDouble("X position", SwerveOdometry::getxposition);
        sender.addItemDouble("Y position", SwerveOdometry::getyposition);
        sender.addItemDouble("Rotation position", SwerveOdometry::getrotposition);


    }
    
    // TODO: define function as public or private
    static void execute(){
        double startTime = Timer.getFPGATimestamp();

        // TODO: put all the executable logic above this.....
        double endTime = Timer.getFPGATimestamp();
        double getElasped= endTime - startTime;
        double execElapsedTime;

        // TODO: What is this for.  Don't think this is needed...
        execElapsedTime=getElasped*1000;

        // TODO: currTimeSecs, gyroangle(Rotation2D), wheelPosition(serveModukePosition{})
        // TODO: suggest passing "currentTime" into this function from robot.java so each 20 millisecond cycle (except vision) uses the same time!
        // TODO: Gyro angle can't be null.  Get from swerve drive.
        // TODO: Wheel positions can't be null.  Get from swerve drive.
        locPoseEst.updateWithTime(execElapsedTime, null, null);
        xpos = locPoseEst.getEstimatedPosition().getX();
        ypos = locPoseEst.getEstimatedPosition().getY();
        rotpos = locPoseEst.getEstimatedPosition().getRotation().getRadians();

        // TODO: move the endTime and later logic 
        
        sender.triggerUpdate();
    }

    // TODO: define function as public or private
    static double getExecElapsedTimeMilli(){
        return(Timer.getFPGATimestamp());
    }

    // TODO: getter functions should return something.  Are you returning a Pose2d or Pose3d here?
    // TODO: define function as public or private
    static void getpose(){
      locPoseEst.getEstimatedPosition();
    }

    // TODO: define function as public or private
    static double getrotposition(){
        return(rotpos);
    }

    // TODO: define function as public or private
    static double getxposition(){
        //getEstimatedPosition is from Pose2D
        return(xpos);
    }

    // TODO: define function as public or private
    static double getyposition(){
        return(ypos);
    }

    // TODO: define function as public or private
    // TODO: what is this for?  Where is the data coming from.
    static void setstartingpose(double xPos, double yPos, double rotRadians){
        Rotation2d rot= new Rotation2d(rotRadians);
        Pose2d newPose = new Pose2d(xPos, yPos, rot);
        //Pose2D is object that stores x,y,and rotation in radians. 
        locPoseEst.resetPose(newPose);
        locPoseEst.resetPosition(rot, null, newPose);
        //TODO: change DriveSwerveSystem.getCurrentFieldRotation, DriveSwerveSystem.getModulePositions, newPose
    }

    // TODO: Add a public function to allow vision to add updates to the pose estimator..

    
}

