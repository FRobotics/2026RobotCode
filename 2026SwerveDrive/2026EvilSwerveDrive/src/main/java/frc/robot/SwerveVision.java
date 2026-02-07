package frc.robot;

//import java.util.List;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
//import org.photonvision.PhotonPoseEstimator.PoseStrategy;
//import org.photonvision.targeting.PhotonPipelineResult;
//import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
//import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;

public class SwerveVision {
    
    private static final String cam1name = "Arducam_OV9281_USB_Camera";

    private static PhotonCamera camera1;
    private static PhotonPoseEstimator photonPoseEstimator1;
    private static AprilTagFieldLayout fieldLayout;
    // TODO: will have to set physical camera offset position once it is known.
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera1 = new Transform3d( Units.inchesToMeters(10.0), 
                                                                Units.inchesToMeters(11.0), 
                                                                Units.inchesToMeters(8.5), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(15.0), 
                                                                Units.degreesToRadians(25.0)));

    private SwerveVision(){}

    
    public static void init(){
        camera1 = new PhotonCamera(cam1name);

        try {
            fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltWelded);
        } catch (Exception e) {
            // Handle the exception if the field layout cannot be loaded
            throw new RuntimeException("Failed to load AprilTag field layout", e);
        } finally {}



        photonPoseEstimator1 = new PhotonPoseEstimator(fieldLayout,robotToCamera1);
           
    }


    // TODO: Suggest creating a fast thread to check network tables for updates or use a NT subscriber function.
    
    public static void execute(double systemElapsedTimeSec){

        Optional<EstimatedRobotPose> visionEst = Optional.empty();

        for (var result : camera1.getAllUnreadResults()) {
            visionEst = photonPoseEstimator1.estimateCoprocMultiTagPose(result);
            if (visionEst.isEmpty()) {
                visionEst = photonPoseEstimator1.estimatePnpDistanceTrigSolvePose(result);
            }
            if ( visionEst.isPresent() ) {
                SwerveOdometry.addVisionMeasurement(visionEst.get().estimatedPose.toPose2d(), visionEst.get().timestampSeconds);
            }
        }



    }
    




    

}
