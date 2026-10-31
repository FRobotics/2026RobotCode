package Lib4150;

import java.util.List;

import Lib4150.Lib4150TrajHelper.WeightedWaypoint;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.spline.QuinticHermiteSpline;
import edu.wpi.first.math.spline.Spline;
import edu.wpi.first.math.spline.SplineHelper;
import edu.wpi.first.math.spline.Spline.ControlVector;


// =============================================================================================
/**
 * Spline helper functions.  These are functions that LabVIEW has that are missing 
 * from WPILIB.
 *<br>
 *  File:   Lib4150SplineHelper.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150SplineHelper<br>
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
public class Lib4150SplineHelper {

    // --------object variables
    static private Translation2d[] LocInteriorPts;

    // --------constructor - private for pseudo static class

    // ---------------------------------------------------------------------------------------------
    /**
     * private constructor - can't call - pseudo static class.
     */
    private Lib4150SplineHelper() {
        return;
    }

    // --------public methods....

    // ---------------------------------------------------------------------------------------------
    /**
     * get cubic control vectors from weighted waypoints.
     * 
     * @param waypoints - Lib4150TrajHelper.WeightedWaypoint[] - weighted waypoints.
     * @param useWeights - boolean - if TRUE use weights.
     * 
     * @return controlvectors - ControlVector[]
     */
    static public Spline.ControlVector[] getCubicCtrlVectorsFromWeightedWaypoints( Lib4150TrajHelper.WeightedWaypoint[] waypoints, boolean useWeights ) {
        
        Spline.ControlVector[] retVector;
        int lastInx = waypoints.length -1;
        Translation2d[] interiorPts;
        Pose2d startPose = new Pose2d(waypoints[0].position, waypoints[0].direction);
        Pose2d endPose = new Pose2d(waypoints[lastInx].position, waypoints[lastInx].direction);
        if ( useWeights ) {
            retVector = new Spline.ControlVector[2];
            retVector[0] = Lib4150SplineHelper.getCubicControlVector(waypoints[0].weight, startPose );
            retVector[1] = Lib4150SplineHelper.getCubicControlVector(waypoints[lastInx].weight, endPose );
            interiorPts = null;
        }
        else {
            // --------need at least three items to have interior...
            if ( lastInx > 1 ) {
                interiorPts = new Translation2d[lastInx-1];
                for ( int j=1; j<(lastInx-1); j++ ) {
                    interiorPts[j-1] = waypoints[j].position;
                }
            }
            else {
                interiorPts = null;
            }
            retVector = SplineHelper.getCubicControlVectorsFromWaypoints(startPose, interiorPts, endPose);
        }

        // --------save interior points for others to use...
        LocInteriorPts = interiorPts;

        return retVector;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns quintic splines from a set of weighted waypoints.
     *
     * @param weightedWaypoints - List&lt;Lib4150TrajHelper.WeightedWaypoint&gt; The waypoints
     * 
     * @return splines - QuinticHermiteSpline[] - The created splines
     */
    static public QuinticHermiteSpline[] getQuinticSplinesFromWeightedWaypoints(List<WeightedWaypoint> weightedWaypoints) {

        QuinticHermiteSpline[] splines = new QuinticHermiteSpline[weightedWaypoints.size() - 1];

        for (int i = 0; i < weightedWaypoints.size() - 1; ++i) {
            WeightedWaypoint p0 = weightedWaypoints.get(i);
            WeightedWaypoint p1 = weightedWaypoints.get(i+1);

            double scalar = p0.weight;

            ControlVector controlVecA = Lib4150SplineHelper.getQuinticControlVector(scalar, p0.getPose() );
            ControlVector controlVecB = Lib4150SplineHelper.getQuinticControlVector(scalar, p1.getPose() );

        splines[i] =
            new QuinticHermiteSpline(controlVecA.x, controlVecB.x, controlVecA.y, controlVecB.y);
        }
        return splines;
    }


    // ========private methods.


    // ---------------------------------------------------------------------------------------------
    /**
     * Get the quintic control vector for this point and weight.
     * @param scalar - double - weight
     * @param point - Pose2d - point
     * 
     * @return controlVector - Spline.ControlVector - calculated control vector.
     */  
    static private Spline.ControlVector getQuinticControlVector(double scalar, Pose2d point) {
        return new Spline.ControlVector(
            new double[] {point.getX(), scalar * point.getRotation().getCos(), 0.0},
            new double[] {point.getY(), scalar * point.getRotation().getSin(), 0.0});
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Copied from WPILIB SplineHelper becauase it it private....
     * 
     * @param scalar - double - weight
     * @param point - Pose2d - pose of waypoint.
     * 
     * @return - controVector - Spline.ControlVector - returned control vector for this spline.
     */
    static private Spline.ControlVector getCubicControlVector(double scalar, Pose2d point) {
        return new Spline.ControlVector(
            new double[] {point.getX(), scalar * point.getRotation().getCos()},
            new double[] {point.getY(), scalar * point.getRotation().getSin()});
      }
    

    // ========GETTERS

    // ---------------------------------------------------------------------------------------------
    /**
     * get interior waypoints calculated by call to getCubicCtrlVectorsFromWeightedWaypoints.
     * This method exists since java can only return a single value.  Call this immediately
     * after calling getCubicCtrlVectorsFromWeightedWaypoints to get this extra return value.
     * 
     * @return interiorPts - Translation2d[] - array of interior locations.
     */
    static public Translation2d[] getInteriorWaypoints() {
        return LocInteriorPts;
    }

}
