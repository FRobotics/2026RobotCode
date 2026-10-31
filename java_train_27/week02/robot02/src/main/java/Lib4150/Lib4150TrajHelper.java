package Lib4150;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.PoseWithCurvature;
import edu.wpi.first.math.spline.Spline.ControlVector;
import edu.wpi.first.math.spline.SplineHelper;
import edu.wpi.first.math.spline.SplineParameterizer.MalformedSplineException;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryParameterizer;


// =============================================================================================
/**
 * Trajectory Helper functions.  These are functions that LabVIEW has that are missing 
 * from WPILIB.
 *<br>
 *  File:   Lib4150TrajHelper.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150TrajHelper<br>
 *<br>
 *  Depends on:<br>
 *          WPILIB - various functions and types are used.
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    11/25/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-11-25
*/
public class Lib4150TrajHelper {

    // --------object constants
    static private final Transform2d kFlip = new Transform2d(Translation2d.kZero, Rotation2d.kPi);
    static private final Trajectory kDoNothingTrajectory = new Trajectory(List.of(new Trajectory.State()));
    static private BiConsumer<String, StackTraceElement[]> errorFunc;

    // --------object types

    // ---------------------------------------------------------------------------------------------
    // --------global enum for create spline types.  (This may only be available on LabVIEW )
    /**
     * Type of spline to use for trajectory creation.
     */
    static public enum SplineType {
        /**
         * Cubic spline without weights.
         */
        Cubic,
        /**
         * Cubic spline with weights.
         */
        CubicWithWeights,
        /**
         * Quintic spline without weights.
         */
        Quintic,
        /**
         * Quintic spline with weights.
         */
        QuinticWithWeights 
    }

    // --------object data.. hopefully none.. maybe things for multivalue returns.

    // ---------------------------------------------------------------------------------------------
    // --------constructor -- private
    /**
     * Private constructor - can't call - pseudo static class.
     */
    private Lib4150TrajHelper() {
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Create a trajectory from Weighted weighpoints.
     * 
     * @param type - SplineType - type of spline to use when creating trajectory.
     * @param waypoints - WeightedWaypoint[] - array of weighted waypoints.
     * @param config = TrajectoryConfig - configuration (constraints) for this trajectory
     * 
     * @return traj - Trajectory - created trajectory
      */
    static public Trajectory TrajectoryGenerate_MakeGeneric( SplineType type, WeightedWaypoint[] waypoints, TrajectoryConfig config ) {

        Trajectory retTraj= new Trajectory();
        ControlVector[] ctrlVectors;

        // --------create trajectory based on selected spline type
        switch ( type ) {
            case Cubic:
                ctrlVectors = Lib4150SplineHelper.getCubicCtrlVectorsFromWeightedWaypoints(waypoints, false);
                retTraj = TrajectoryGenerator.generateTrajectory(ctrlVectors[0],Arrays.asList(Lib4150SplineHelper.getInteriorWaypoints()),ctrlVectors[1], config);
                break;
            case CubicWithWeights:
                ctrlVectors = Lib4150SplineHelper.getCubicCtrlVectorsFromWeightedWaypoints(waypoints, true);
                retTraj = TrajectoryGenerator.generateTrajectory(ctrlVectors[0],Arrays.asList(Lib4150SplineHelper.getInteriorWaypoints()),ctrlVectors[1], config);
                break;
            case Quintic:
                // --------create non-weighted waypoints..
                ArrayList<Pose2d>  poses = new ArrayList<>();
                for ( WeightedWaypoint myWp : waypoints ) {
                    poses.add(new Pose2d( myWp.position, myWp.direction ));
                }
                retTraj = TrajectoryGenerator.generateTrajectory(poses, config);
                break;
            case QuinticWithWeights:
                retTraj = Lib4150TrajHelper.generateTrajectoryQuniticWeighted(Arrays.asList(waypoints), config);
                break;
        }
        return retTraj;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Generates a trajectory from the given weighted waypoints and trajectory configuration. This method 
     * uses quintic hermite splines -- therefore, all points must be represented by Pose2d objects. 
     * Continuous curvature is guaranteed in this method.
     *
     * @param weightedWaypoints - List&lt;WeightedWaypoint&gt; List of waypoints..
     * @param config - TrajectoryConfig - The configuration for the trajectory.
     * @return traj - Trajectory - The generated trajectory.
     */
    public static Trajectory generateTrajectoryQuniticWeighted( 
                                List<WeightedWaypoint> weightedWaypoints, 
                                TrajectoryConfig config ) {
 
        // --------if reversed, flip the pose.  If not create pose arrayList from
        // --------weighted waypoint. 
        List<WeightedWaypoint> newWeightedWaypoints = new ArrayList<>();
        if (config.isReversed()) {
            for (WeightedWaypoint originalWaypoint : weightedWaypoints) {
                Pose2d myPose = originalWaypoint.getPose().plus(kFlip);
                newWeightedWaypoints.add( new WeightedWaypoint(myPose.getTranslation(), myPose.getRotation(), originalWaypoint.weight));
            }
        } else {
            for (WeightedWaypoint originalWaypoint : weightedWaypoints) {
                newWeightedWaypoints.add( originalWaypoint);   // redundant...
            }
        }

        // --------Get the spline points from weighted waypoints...
        List<PoseWithCurvature> points;
        try {
            points =
                TrajectoryGenerator.splinePointsFromSplines(
                    SplineHelper.optimizeCurvature(
                        Lib4150SplineHelper.getQuinticSplinesFromWeightedWaypoints(newWeightedWaypoints)));
        } 
        catch (MalformedSplineException ex) {
            reportError(ex.getMessage(), ex.getStackTrace());
            return kDoNothingTrajectory;
        }

        // --------Change the points back to their original orientation.
        if (config.isReversed()) {
            for (var point : points) {
                point.poseMeters = point.poseMeters.plus(kFlip);
                point.curvatureRadPerMeter *= -1;
            }
        }

        // --------Generate and return trajectory.
        return TrajectoryParameterizer.timeParameterizeTrajectory(
            points,
            config.getConstraints(),
            config.getStartVelocity(),
            config.getEndVelocity(),
            config.getMaxVelocity(),
            config.getMaxAcceleration(),
            config.isReversed());
    }


    // --------private methods..

    
    // ---------------------------------------------------------------------------------------------
    /**
     * Report error 
     * 
     * @param error - String - error string
     * @param stackTrace - StackTraceElement[] - the stack to track
     */
    private static void reportError(String error, StackTraceElement[] stackTrace) {
        if (errorFunc != null) {
        errorFunc.accept(error, stackTrace);
        } 
        else {
            MathSharedStore.reportError(error, stackTrace);
        }
        return;
    }


    // =============================================================================================
    /**
     * Class for weighted waypoint data.
     */
    static public class WeightedWaypoint {

        // --------local data.
        /**
         * position - Translation2d - SI units
         */
        public Translation2d position = new Translation2d();
        /**
         * direction of travel - Rotation2d
         */
        public Rotation2d direction = new Rotation2d();
        /**
         * Weight value - double - This is really in meters.
         */
        public double weight = 1.0; // really in meters.

        // ---------------------------------------------------------------------------------------------
        // --------constructor
        /**
         * Construct a weighted waypoint
         * 
         * @param pos - Translation2d - position - SI units
         * @param travelDirection - Rotation2d - direction of travel
         * @param weight - double - how straight should robot pass through this point - meters
         */
        public WeightedWaypoint( Translation2d pos, Rotation2d travelDirection, double weight ) {
            this.position = pos;
            this.direction = travelDirection;
            this.weight = weight;
            return;
        }

        // ---------------------------------------------------------------------------------------------
        // --------constructor
        /**
         * 
         * @param x - double - X position - meters
         * @param y - double - Y position - meters
         * @param travelDirectionDeg - double - travel direction - degrees
         * @param weight = double - how straight should robot pass through this point - meters
         */
        public WeightedWaypoint( double x, double y, double travelDirectionDeg, double weight  ) {
            this( new Translation2d( x, y ), Rotation2d.fromDegrees(travelDirectionDeg), weight );
            return;
        }

        // --------methods
        
        // ---------------------------------------------------------------------------------------------
        /**
         * Get the pose2d for this weighted waypoint
         * 
         * @return pose - Pose2d - the pose of this weighted waypoint.
         */
        public Pose2d getPose() {
            return new Pose2d( this.position, this.direction );
        }


    }

}
