package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;



public class SwerveOdometry {

    
    static private SwerveDrivePoseEstimator locPoseEst;
    static private Lib4150NetTableSystemSend sender;
    static private double xpos = 0.0;
    static private double ypos = 0.0;
    static private double rotpos = 0.0;
    static private double execElapsedTime = 0.0;
    
    public static void init(){
        // TODO: put some comments here.  For example, this is the initial robot position (pose)
        Pose2d initPose = new Pose2d(0.0,0.0,new Rotation2d(0.0));
        // TODO: should Yaw be Roll here???
        
        locPoseEst = new SwerveDrivePoseEstimator( SwerveDrive.publicDriveKinematics, new Rotation2d(SwerveDrive.getYaw()),  SwerveDrive.getModulePositions(), initPose );
        
        

        sender = new Lib4150NetTableSystemSend("Odometry");
        sender.addItemDouble("X_position", SwerveOdometry::getxposition);
        sender.addItemDouble("Y_position", SwerveOdometry::getyposition);
        sender.addItemDouble("Rotation_position", SwerveOdometry::getrotposition);
        sender.addItemDouble("execElapsedTime", SwerveOdometry::getExecCycleTime);


    }
    
    
    static void execute(double systemElapsedTime){


        // TODO: Use these ONLY for diagnostics...  
        double startTime = Timer.getFPGATimestamp();

        // TODO: currTimeSecs, gyroangle(Rotation2D), wheelPosition(serveModukePosition{})

        locPoseEst.updateWithTime( systemElapsedTime, new Rotation2d(SwerveDrive.getYaw()), SwerveDrive.getModulePositions());
        xpos = locPoseEst.getEstimatedPosition().getX();
        ypos = locPoseEst.getEstimatedPosition().getY();
        rotpos = locPoseEst.getEstimatedPosition().getRotation().getRadians();

        execElapsedTime = Timer.getFPGATimestamp() - startTime;

        sender.triggerUpdate();
        
    }

    public static double getExecCycleTime(){
        return execElapsedTime;
    }

   
    public static Pose2d getpose(){
      return locPoseEst.getEstimatedPosition();
    }

    
    public static double getrotposition(){
        return(rotpos);
    }

    
    public static double getxposition(){
        //getEstimatedPosition is from Pose2D
        return(xpos);
    }

    
    public static double getyposition(){
        return(ypos);
    }

    // TODO: define function as public or private
    // TODO: what is this for?  Where is the data coming from.
    public static void setstartingpose(double xPos, double yPos, double rotRadians){
        Rotation2d rot= new Rotation2d(rotRadians);
        Pose2d newPose = new Pose2d(xPos, yPos, rot);
        //Pose2D is object that stores x,y,and rotation in radians. 
        locPoseEst.resetPose(newPose);
        locPoseEst.resetPosition(rot, null, newPose);
        //TODO: change DriveSwerveSystem.getCurrentFieldRotation, DriveSwerveSystem.getModulePositions, newPose
    }

    // TODO: Add a public function to allow vision to add updates to the pose estimator..

    
}

