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

    // --------global constants
    private static final double MAX_ALLOWED_AMBIGUITY = 0.30;
    
    private static Lib4150NetTableSystemSend    locNtSend;
    private static AprilTagFieldLayout fieldLayout;
    //cameras are ordered from left to right while looking at intake

    // --------camera 1---back left
    private static final String cam1name = "OV9281-2603";
    private static long locCam1Count = 0;
    private static PhotonCamera camera1;
    private static PhotonPoseEstimator photonPoseEstimator1;
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera1 = new Transform3d( Units.inchesToMeters(-9.5), 
                                                                Units.inchesToMeters(7.5), 
                                                                Units.inchesToMeters(16.5), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(20.0), 
                                                                Units.degreesToRadians(150.0-3.7)));
    private static double cam1X = 0.0;
    private static double cam1Y = 0.0;
    private static double cam1Z = 0.0;
    private static double cam1orient = 0.0;


     // --------camera 2----back right
    private static final String cam2name = "OV9782-2602";
    private static long locCam2Count = 0;
    private static PhotonCamera camera2;
    private static PhotonPoseEstimator photonPoseEstimator2;
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera2 = new Transform3d( Units.inchesToMeters(-9.5), 
                                                                Units.inchesToMeters(-7.5), 
                                                                Units.inchesToMeters(16.5), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(20.0), 
                                                                Units.degreesToRadians(-150.0-3.7)));

    private static double cam2X = 0.0;
    private static double cam2Y = 0.0;
    private static double cam2Z = 0.0;
    private static double cam2orient = 0.0;

     // --------camera 3----middle left
    private static final String cam3name = "OV9782-2604";
    private static long locCam3Count = 0;
    private static PhotonCamera camera3;
    private static PhotonPoseEstimator photonPoseEstimator3;
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera3 = new Transform3d( Units.inchesToMeters(-1.0), 
                                                                Units.inchesToMeters(14.25), 
                                                                Units.inchesToMeters(9.0), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(20.0), 
                                                                Units.degreesToRadians(90.0)));
    private static double cam3X = 0.0;
    private static double cam3Y = 0.0;
    private static double cam3Z = 0.0;
    private static double cam3orient = 0.0;

     // --------camera 4----middle right
    private static final String cam4name = "OV9281-2601";
    private static long locCam4Count = 0;
    private static PhotonCamera camera4;
    private static PhotonPoseEstimator photonPoseEstimator4;
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera4 = new Transform3d( Units.inchesToMeters(-1.0), 
                                                                Units.inchesToMeters(-14.25), 
                                                                Units.inchesToMeters(9.0), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(20.0), 
                                                                Units.degreesToRadians(-90.0)));
    private static double cam4X = 0.0;
    private static double cam4Y = 0.0;
    private static double cam4Z = 0.0;
    private static double cam4orient = 0.0;

    // --------camera 5----front middle driver camera
         private static final String cam5name = "OV9782-2603";
    private static long locCam5Count = 0;
    private static PhotonCamera camera5;
    private static PhotonPoseEstimator photonPoseEstimator5;
    // KEN x = 10.0, Y = 11.0 Z = 8.5, Roll = 0.0, Pitch = 15.0, yaw = -25.0 
    private static Transform3d robotToCamera5 = new Transform3d( Units.inchesToMeters(15.0625), 
                                                                Units.inchesToMeters(9.25), 
                                                                Units.inchesToMeters(19.625), 
                                                new Rotation3d( 0.0, 
                                                                Units.degreesToRadians(0.0), 
                                                                Units.degreesToRadians(0.0)));
    private static double cam5X = 0.0;
    private static double cam5Y = 0.0;
    private static double cam5Z = 0.0;
    private static double cam5orient = 0.0;


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

        // --------camera 2        
        camera2 = new PhotonCamera(cam2name);
        photonPoseEstimator2 = new PhotonPoseEstimator(fieldLayout,robotToCamera2);

        // --------camera 3
        camera3 = new PhotonCamera(cam3name);
        photonPoseEstimator3 = new PhotonPoseEstimator(fieldLayout,robotToCamera3);

        // --------camera 4
        camera4 = new PhotonCamera(cam4name);
        photonPoseEstimator4 = new PhotonPoseEstimator(fieldLayout,robotToCamera4);

        camera5 = new PhotonCamera(cam5name);
        photonPoseEstimator5 = new PhotonPoseEstimator(fieldLayout, robotToCamera5);

        // --------camera 5
        camera5 = new PhotonCamera(cam5name);
        photonPoseEstimator5 = new PhotonPoseEstimator(fieldLayout,robotToCamera5);

        
        //add items to push to network tables
        locNtSend = new Lib4150NetTableSystemSend("Vision");
        // --------camera 1
        locNtSend.addItemDouble("Cam1Count", SwerveVision::getCam1Count);
        locNtSend.addItemDouble("Cam1X", SwerveVision::getCam1X);
        locNtSend.addItemDouble("Cam1Y", SwerveVision::getCam1Y);
        locNtSend.addItemDouble("Cam1Z", SwerveVision::getCam1Z);
        locNtSend.addItemDouble("Cam1Orientation", SwerveVision::getCam1Orient);

        
        locNtSend.addItemDouble("Cam2Count", SwerveVision::getCam2Count);
        locNtSend.addItemDouble("Cam2X", SwerveVision::getCam2X);
        locNtSend.addItemDouble("Cam2Y", SwerveVision::getCam2Y);
        locNtSend.addItemDouble("Cam2Z", SwerveVision::getCam2Z);
        locNtSend.addItemDouble("Cam2Orientation", SwerveVision::getCam2Orient);

        locNtSend.addItemDouble("Cam3Count", SwerveVision::getCam3Count);
        locNtSend.addItemDouble("Cam3X", SwerveVision::getCam3X);
        locNtSend.addItemDouble("Cam3Y", SwerveVision::getCam3Y);
        locNtSend.addItemDouble("Cam3Z", SwerveVision::getCam3Z);
        locNtSend.addItemDouble("Cam3Orientation", SwerveVision::getCam3Orient);

        locNtSend.addItemDouble("Cam4Count", SwerveVision::getCam4Count);
        locNtSend.addItemDouble("Cam4X", SwerveVision::getCam4X);
        locNtSend.addItemDouble("Cam4Y", SwerveVision::getCam4Y);
        locNtSend.addItemDouble("Cam4Z", SwerveVision::getCam4Z);
        locNtSend.addItemDouble("Cam4Orientation", SwerveVision::getCam4Orient);
 
        locNtSend.addItemDouble("Cam5Count", SwerveVision::getCam5Count);
        locNtSend.addItemDouble("Cam5X", SwerveVision::getCam5X);
        locNtSend.addItemDouble("Cam5Y", SwerveVision::getCam5Y);
        locNtSend.addItemDouble("Cam5Z", SwerveVision::getCam5Z);
        locNtSend.addItemDouble("Cam5Orientation", SwerveVision::getCam5Orient);
           
    }

    


  
    public static void execute(double systemElapsedTimeSec){

        // --------camera 1
        photonPoseEstimator1.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst1 = Optional.empty();

        for (var result : camera1.getAllUnreadResults()) {
            visionEst1 = photonPoseEstimator1.estimateCoprocMultiTagPose(result);
            if (visionEst1.isEmpty()) {
                Optional<EstimatedRobotPose> tmpVisionEst1 = photonPoseEstimator1.estimatePnpDistanceTrigSolvePose(result);
                if ( tmpVisionEst1.isPresent() ) {
                    if ( tmpVisionEst1.get().targetsUsed.get(0).poseAmbiguity <= MAX_ALLOWED_AMBIGUITY ) {
                        visionEst1 = tmpVisionEst1;
                    }
                }
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


        // --------camera 2
        photonPoseEstimator2.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst2 = Optional.empty();

        for (var result : camera2.getAllUnreadResults()) {
            visionEst2 = photonPoseEstimator2.estimateCoprocMultiTagPose(result);
            if (visionEst2.isEmpty()) {
                Optional<EstimatedRobotPose> tmpVisionEst2 = photonPoseEstimator2.estimatePnpDistanceTrigSolvePose(result);
                if ( tmpVisionEst2.isPresent() ) {
                    if ( tmpVisionEst2.get().targetsUsed.get(0).poseAmbiguity <= MAX_ALLOWED_AMBIGUITY ) {
                        visionEst2 = tmpVisionEst2;
                    }
                }
            }
            if ( visionEst2.isPresent() ) {
                locCam2Count++;
                SwerveOdometry.addVisionMeasurement(visionEst2.get().estimatedPose.toPose2d(), visionEst2.get().timestampSeconds);
                cam2X = visionEst2.get().estimatedPose.getX();
                cam2Y = visionEst2.get().estimatedPose.getY();
                cam2Z = visionEst2.get().estimatedPose.getZ();
                cam2orient = visionEst2.get().estimatedPose.getRotation().getZ();   // yaw
            }
        }
        // --------camera 3
        photonPoseEstimator3.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst3 = Optional.empty();

        for (var result : camera3.getAllUnreadResults()) {
            visionEst3 = photonPoseEstimator3.estimateCoprocMultiTagPose(result);
            if (visionEst3.isEmpty()) {
                Optional<EstimatedRobotPose> tmpVisionEst3 = photonPoseEstimator3.estimatePnpDistanceTrigSolvePose(result);
                if ( tmpVisionEst3.isPresent() ) {
                    if ( tmpVisionEst3.get().targetsUsed.get(0).poseAmbiguity <= MAX_ALLOWED_AMBIGUITY ) {
                        visionEst3 = tmpVisionEst3;
                    }
                }
            }
            if ( visionEst3.isPresent() ) {
                locCam3Count++;
                SwerveOdometry.addVisionMeasurement(visionEst3.get().estimatedPose.toPose2d(), visionEst3.get().timestampSeconds);
                cam3X = visionEst3.get().estimatedPose.getX();
                cam3Y = visionEst3.get().estimatedPose.getY();
                cam3Z = visionEst3.get().estimatedPose.getZ();
                cam3orient = visionEst3.get().estimatedPose.getRotation().getZ();   // yaw
            }
        }
        // --------camera 4
        photonPoseEstimator4.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst4 = Optional.empty();

        for (var result : camera4.getAllUnreadResults()) {
            visionEst4 = photonPoseEstimator4.estimateCoprocMultiTagPose(result);
            if (visionEst4.isEmpty()) {
                Optional<EstimatedRobotPose> tmpVisionEst4 = photonPoseEstimator4.estimatePnpDistanceTrigSolvePose(result);
                if ( tmpVisionEst4.isPresent() ) {
                    if ( tmpVisionEst4.get().targetsUsed.get(0).poseAmbiguity <= MAX_ALLOWED_AMBIGUITY ) {
                        visionEst4 = tmpVisionEst4;
                    }
                }
            }
            if ( visionEst4.isPresent() ) {
                locCam4Count++;
                SwerveOdometry.addVisionMeasurement(visionEst4.get().estimatedPose.toPose2d(), visionEst4.get().timestampSeconds);
                cam4X = visionEst4.get().estimatedPose.getX();
                cam4Y = visionEst4.get().estimatedPose.getY();
                cam4Z = visionEst4.get().estimatedPose.getZ();
                cam4orient = visionEst4.get().estimatedPose.getRotation().getZ();   // yaw
            }
        }

        // --------camera 5
        photonPoseEstimator5.addHeadingData(systemElapsedTimeSec, new Rotation2d(SwerveOdometry.getrotposition())); // needed for trig/distance
        Optional<EstimatedRobotPose> visionEst5 = Optional.empty();

        for (var result : camera5.getAllUnreadResults()) {
            visionEst5 = photonPoseEstimator5.estimateCoprocMultiTagPose(result);
            if (visionEst5.isEmpty()) {
                Optional<EstimatedRobotPose> tmpVisionEst5 = photonPoseEstimator5.estimatePnpDistanceTrigSolvePose(result);
                if ( tmpVisionEst5.isPresent() ) {
                    if ( tmpVisionEst5.get().targetsUsed.get(0).poseAmbiguity <= MAX_ALLOWED_AMBIGUITY ) {
                        visionEst5 = tmpVisionEst5;
                    }
                }
            }
            if ( visionEst5.isPresent() ) {
                locCam5Count++;
                SwerveOdometry.addVisionMeasurement(visionEst5.get().estimatedPose.toPose2d(), visionEst5.get().timestampSeconds);
                cam5X = visionEst5.get().estimatedPose.getX();
                cam5Y = visionEst5.get().estimatedPose.getY();
                cam5Z = visionEst5.get().estimatedPose.getZ();
                cam5orient = visionEst5.get().estimatedPose.getRotation().getZ();   // yaw
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

    // --------camera 2
    public static double getCam2Count() {
        return (double)locCam2Count;
    }
    public static double getCam2X() {
        return cam2X;
    }
    public static double getCam2Y() {
        return cam2Y;
    }
    public static double getCam2Z() {
        return cam2Z;
    }
    public static double getCam2Orient() {
        return cam2orient;
    }
     // --------camera 3
    public static double getCam3Count() {
        return (double)locCam3Count;
    }
    public static double getCam3X() {
        return cam3X;
    }
    public static double getCam3Y() {
        return cam3Y;
    }
    public static double getCam3Z() {
        return cam3Z;
    }
    public static double getCam3Orient() {
        return cam3orient;
    }
     // --------camera 4
    public static double getCam4Count() {
        return (double)locCam4Count;
    }
    public static double getCam4X() {
        return cam4X;
    }
    public static double getCam4Y() {
        return cam4Y;
    }
    public static double getCam4Z() {
        return cam4Z;
    }
    public static double getCam4Orient() {
        return cam4orient;
    }
    
     // --------camera 5
    public static double getCam5Count() {
       return (double)locCam5Count;
    }
    public static double getCam5X() {
        return cam5X;
    }
    public static double getCam5Y() {
        return cam5Y;
    }
    public static double getCam5Z() {
        return cam5Z;
    }
    public static double getCam5Orient() {
       return cam5orient;
    }
    

}
