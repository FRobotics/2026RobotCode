package frc.robot;

//import java.util.List;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
//import org.photonvision.PhotonPoseEstimator.PoseStrategy;
//import org.photonvision.targeting.PhotonPipelineResult;
//import org.photonvision.targeting.PhotonTrackedTarget;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
//import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;

public class SwerveVision {
    
    private static Lib4150NetTableSystemSend    locNtSend;
    private static AprilTagFieldLayout fieldLayout;

    // --------camera 1
    private static final String cam1name = "Arducam_OV9281_USB_Camera";
    private static long locCam1Count = 0;
    private static PhotonCamera camera1;
    private static PhotonPoseEstimator photonPoseEstimator1;
    // TODO: will have to set physical camera offset position once it is known.
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera1 = new Transform3d( Units.inchesToMeters(10.0), 
                                                                Units.inchesToMeters(11.0), 
                                                                Units.inchesToMeters(8.5), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(15.0), 
                                                                Units.degreesToRadians(-25.0)));
    private static double cam1X = 0.0;
    private static double cam1Y = 0.0;
    private static double cam1Z = 0.0;
    private static double cam1orient = 0.0;

    // --------private constructor
    private SwerveVision(){}

    // one time initialization when robot starts    
    public static void init(){

        try {
            fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);
        } catch (Exception e) {
            // Handle the exception if the field layout cannot be loaded
            throw new RuntimeException("Failed to load AprilTag field layout", e);
        } finally {}

        // --------camera 1        
        camera1 = new PhotonCamera(cam1name);
        photonPoseEstimator1 = new PhotonPoseEstimator(fieldLayout,robotToCamera1);

        
        //add items to push to network tables
        locNtSend = new Lib4150NetTableSystemSend("Vision");
        // --------camera 1
        locNtSend.addItemDouble("Cam1Count", SwerveVision::getCam1Count);
        locNtSend.addItemDouble("Cam1X", SwerveVision::getCam1X);
        locNtSend.addItemDouble("Cam1Y", SwerveVision::getCam1Y);
        locNtSend.addItemDouble("Cam1Z", SwerveVision::getCam1Z);
        locNtSend.addItemDouble("Cam1Orientation", SwerveVision::getCam1Orient);
           
    }


    // TODO: Suggest creating a fast thread to check network tables for updates or use a NT subscriber function.
    
    public static void execute(double systemElapsedTimeSec){

        // --------camera 1
        photonPoseEstimator1.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst1 = Optional.empty();

        for (var result : camera1.getAllUnreadResults()) {
            visionEst1 = photonPoseEstimator1.estimateCoprocMultiTagPose(result);
            if (visionEst1.isEmpty()) {
                visionEst1 = photonPoseEstimator1.estimatePnpDistanceTrigSolvePose(result);
            }
            if ( visionEst1.isPresent() ) {
                locCam1Count++;
                SwerveOdometry.addVisionMeasurement(visionEst1.get().estimatedPose.toPose2d(), visionEst1.get().timestampSeconds);
                cam1X = visionEst1.get().estimatedPose.getX();
                cam1Y = visionEst1.get().estimatedPose.getY();
                cam1Z = visionEst1.get().estimatedPose.getZ();
                cam1orient = visionEst1.get().estimatedPose.getRotation().getZ();   // yaw
            }
        }

        

        locNtSend.triggerUpdate();

    }

    // --------camera 1
    public static double getCam1Count() {
        return (double)locCam1Count;
    }
    public static double getCam1X() {
        return cam1X;
    }
    public static double getCam1Y() {
        return cam1Y;
    }
    public static double getCam1Z() {
        return cam1Z;
    }
    public static double getCam1Orient() {
        return cam1orient;
    }



    

}
