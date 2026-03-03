package frc.robot;

import Lib4150.Lib4150PositionControl;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.util.Units;

public class AutoFunctions {

    // --------class constants
    private static final ChassisSpeeds DRIVE_OFF = new ChassisSpeeds(0, 0, 0);

    // --------constants for spin command
    static private final double SPIN_MAX_SPIN_RATE = 270.0;  // max rate deg/sec
    static private final double SPIN_MAX_SPIN_ACCEL = SPIN_MAX_SPIN_RATE * 5.0;  // max rate deg/sec

    static private final double STRAIGHT_MAX_DRIVE_RATE = Units.feetToMeters(14.0);  // max rate m/sec
    static private final double STRAIGHT_MAX_DRIVE_ACCEL = STRAIGHT_MAX_DRIVE_RATE * 2.0;  // max rate m/sec^2 // 0.5 sec to full speed.  was just 30.


    // --------class type definitions

    // --------class variables
    // --------variables for spin command - units in degrees
    private static double locDriveSpinTargetDeg = 0.0; // setpoint
    static private final SlewRateLimiter locDriveSpinlimit = new SlewRateLimiter( SPIN_MAX_SPIN_ACCEL ); // 0.2 sec to full speed.
    static private final Lib4150PositionControl locDriveSpinCtrl = new Lib4150PositionControl(
                    0.4,30.0,
                    0.02, SPIN_MAX_SPIN_RATE, 
                    SPIN_MAX_SPIN_RATE, 1e-5,true,false);


    // --------for DriveStraight -- meters, deg
    static private final SlewRateLimiter locDriveStraightlimit = new SlewRateLimiter( STRAIGHT_MAX_DRIVE_ACCEL ); 
    static private final Lib4150PositionControl locDriveStraightCtrl = new Lib4150PositionControl(
                    0.02,2.0,
                    0.02, STRAIGHT_MAX_DRIVE_RATE, 
                    STRAIGHT_MAX_DRIVE_RATE, 1e-5,true,false);
    static private double locDriveStraightTarget = 0.0; // setpoint
    // --------for DriveStraightCalcDist
    static private double[] locStraightAccumDist = { 0.0, 0.0, 0.0, 0.0 };
    static private SwerveModulePosition[] locStraightLastModPos = { new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition() };
    static private Rotation2d[] locStraightPrevAngle = { new Rotation2d(), new Rotation2d(), new Rotation2d(), new Rotation2d() };

    // --------constructors
    private AutoFunctions(){

    }

    /**
     * Wait a number of seconds.  Sets the drive demand to zero.  The time out
     * seconds should be set to the desired wait time.  As such no timing logic
     * needs to be performed by this function. 
     * 
     * @return - cmdDone - boolean - returns TRUE if command is done.  This function always returns FALSE.
     */
    public static boolean autoWait()
    {
        //set drive demand to zero. is this the right method?
        SwerveDrive.setDesiredSpeed( DRIVE_OFF );
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * autoDriveSpin - spin robot in place.
     *  
     * Note:
     *  - some variables used by this function are class wide static value.  
     * 
     * @param autoInitStep - boolean - When TRUE, this is the first time through this step.
     * @param degreesToTurn - double - How far to turn.  positive = counter clockwise.
     * @param maxVelocity_DegPerSec - double - maximum velocity for turning - deg/sec
     * 
     * @return - cmdDone - boolean - returns TRUE if command is done.
     */
    static public boolean autoDriveSpin(boolean autoInitStep, double degreesToTurn, double maxVelocity_DegPerSec ) {

        boolean retValue = false;

        // -------first time -- get target gyro reading..., init position control
        if ( autoInitStep ) {
            locDriveSpinTargetDeg = SwerveDrive.getYaw() + degreesToTurn;
            locDriveSpinCtrl.setMaxOutput(maxVelocity_DegPerSec);
            locDriveSpinCtrl.setDesiredOnTargetCount(10);
            locDriveSpinlimit.reset(0.0);
        }

        // --------get current gyro reading.  Do position control logic.  Set driving output.
        double currentGyroDeg = SwerveDrive.getYaw();
        double spinCO = locDriveSpinCtrl.PosCtrlExec(locDriveSpinTargetDeg, currentGyroDeg);
        spinCO = locDriveSpinlimit.calculate(spinCO);
        SwerveDrive.setDesiredSpeed(new ChassisSpeeds(0,0,Units.degreesToRadians(spinCO)));

        // -------- see if we are done.
        if ( locDriveSpinCtrl.getOnTarget() ) {
            retValue = true;
            SwerveDrive.setDesiredSpeed(DRIVE_OFF);    // just in case.
        }
        else {
            retValue = false;
        }
        // --------set parent items for display
        // TODO: Add error and on target to auto system
        // AutoSystem.setExecErrorOrientDeg(Lib4150AdvancedPID.modulusError( locDriveSpinTargetDeg, currentGyroDeg, -180, 180));
        // AutoSystem.setExecOnTarget(locDriveSpinCtrl.getOnTarget());

        return retValue;
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * DriveStraight - drive straight - at a given angle.  Orientation angle of robot stays the same.
     *  
     * @param autoInitStep - boolean - When TRUE, this is the first time through this step.
     * @param metersToDrive - double - How far to drive.  
     * @param maxVelocity_metersPerSec - double - maximum velocity for driving straight - meter/sec
     * @param angleToDrive_Deg - double - angle to drive 0 = forward.
     * 
     * @return nextStep - boolean - if TRUE, go to next step.
     */
    static public boolean autoDriveStraight(boolean autoInitStep, double metersToDrive, double maxVelocity_metersPerSec, double angleToDrive_Deg ) {


        boolean retValue = false;

        // -------first time -- get target gyro reading..., init position control
        if ( autoInitStep ) {
            locDriveStraightTarget = 0.0 + metersToDrive;
            locDriveStraightCtrl.setMaxOutput(maxVelocity_metersPerSec);
            locDriveStraightCtrl.setDesiredOnTargetCount(10);
            locDriveStraightlimit.reset(0.0);
        }

        // --------get wheel position reading.  Do position control logic.  Set driving output.
        double currentDistance = DriveStraightCalcTraveledDist(autoInitStep,angleToDrive_Deg );

        // --------do position control to get our desired velocity
        double straightCO = locDriveStraightCtrl.PosCtrlExec(locDriveStraightTarget, currentDistance);
        straightCO = locDriveStraightlimit.calculate(straightCO);
        double angleRad = Units.degreesToRadians(angleToDrive_Deg);
        double xCO = Math.cos( angleRad ) * straightCO;
        double yCO = Math.sin( angleRad ) * straightCO;
        SwerveDrive.setDesiredSpeed(new ChassisSpeeds(xCO,yCO,0.0));
        
        // -------- see if we are done.
        if ( locDriveStraightCtrl.getOnTarget() ) {
            retValue = true;
            SwerveDrive.setDesiredSpeed(DRIVE_OFF);     // just in case.
        }
        else {
            retValue = false;
        }
        // --------set parent variables for display
        // TODO: Add error and on target to auto system
        // AutoSystem.setExecErrorXMeters(Units.feetToMeters(locDriveStraightTarget - currentDistance));
        // AutoSystem.setExecOnTarget(locDriveStraightCtrl.getOnTarget());

        return retValue;
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Calculate the distance traveled for the DriveStraight routine.  This is calculated by
     * obtaining the module positions, calculating the change in distance in the desired direction
     * of travel, summing this over each iteration, then returning the average distance.
     * 
     * @param init - boolean - first time, clear accumulated values
     * 
     * @return distance - double - traveled distance - meters
     */
    static private double DriveStraightCalcTraveledDist( boolean init, double angleToDrive_Deg ) {

        double totalDistanceM = 0.0;

        // --------if init, clear saved distances, get starting module pos
        if ( init ) {
            locStraightAccumDist[0] = 0.0;
            locStraightAccumDist[1] = 0.0;
            locStraightAccumDist[2] = 0.0;
            locStraightAccumDist[3] = 0.0;

            // --------YUK copy data not objects.  Objects will update.
            SwerveModulePosition[] currentPosition = SwerveDrive.getModulePositions();
            locStraightLastModPos[0].angle = Rotation2d.fromRadians(currentPosition[0].angle.getRadians());
            locStraightLastModPos[0].distanceMeters = currentPosition[0].distanceMeters;
            locStraightLastModPos[1].angle = Rotation2d.fromRadians(currentPosition[1].angle.getRadians());
            locStraightLastModPos[1].distanceMeters = currentPosition[1].distanceMeters;
            locStraightLastModPos[2].angle = Rotation2d.fromRadians(currentPosition[2].angle.getRadians());
            locStraightLastModPos[2].distanceMeters = currentPosition[2].distanceMeters;
            locStraightLastModPos[3].angle = Rotation2d.fromRadians(currentPosition[3].angle.getRadians());
            locStraightLastModPos[3].distanceMeters = currentPosition[3].distanceMeters;

            locStraightPrevAngle[0] = new Rotation2d( currentPosition[0].angle.getRadians() );
            locStraightPrevAngle[1] = new Rotation2d( currentPosition[1].angle.getRadians() );
            locStraightPrevAngle[2] = new Rotation2d( currentPosition[2].angle.getRadians() );
            locStraightPrevAngle[3] = new Rotation2d( currentPosition[3].angle.getRadians() );
        }
        // --------get module positions, calc distance in our direction.
        else {
            SwerveModulePosition[] currentPosition = SwerveDrive.getModulePositions();
            // --------calc distance in our direction.
            double desiredAngleRad = Units.degreesToRadians(angleToDrive_Deg);
            locStraightAccumDist[0] += ( currentPosition[0].distanceMeters - locStraightLastModPos[0].distanceMeters ) * Math.cos(locStraightPrevAngle[0].interpolate( currentPosition[0].angle,0.5).getRadians() - desiredAngleRad);
            locStraightAccumDist[1] += ( currentPosition[1].distanceMeters - locStraightLastModPos[1].distanceMeters ) * Math.cos(locStraightPrevAngle[1].interpolate( currentPosition[1].angle,0.5).getRadians() - desiredAngleRad);
            locStraightAccumDist[2] += ( currentPosition[2].distanceMeters - locStraightLastModPos[2].distanceMeters ) * Math.cos(locStraightPrevAngle[2].interpolate( currentPosition[2].angle,0.5).getRadians() - desiredAngleRad);
            locStraightAccumDist[3] += ( currentPosition[3].distanceMeters - locStraightLastModPos[3].distanceMeters ) * Math.cos(locStraightPrevAngle[3].interpolate( currentPosition[3].angle,0.5).getRadians() - desiredAngleRad);
            // --------save for next time
            locStraightPrevAngle[0] = new Rotation2d( currentPosition[0].angle.getRadians());
            locStraightPrevAngle[1] = new Rotation2d( currentPosition[1].angle.getRadians());
            locStraightPrevAngle[2] = new Rotation2d( currentPosition[2].angle.getRadians());
            locStraightPrevAngle[3] = new Rotation2d( currentPosition[3].angle.getRadians());


            // --------save current to last
            // --------YUK copy data not objects.  Objects will update.
            locStraightLastModPos[0].angle = Rotation2d.fromRadians(currentPosition[0].angle.getRadians());
            locStraightLastModPos[0].distanceMeters = currentPosition[0].distanceMeters;
            locStraightLastModPos[1].angle = Rotation2d.fromRadians(currentPosition[1].angle.getRadians());
            locStraightLastModPos[1].distanceMeters = currentPosition[1].distanceMeters;
            locStraightLastModPos[2].angle = Rotation2d.fromRadians(currentPosition[2].angle.getRadians());
            locStraightLastModPos[2].distanceMeters = currentPosition[2].distanceMeters;
            locStraightLastModPos[3].angle = Rotation2d.fromRadians(currentPosition[3].angle.getRadians());
            locStraightLastModPos[3].distanceMeters = currentPosition[3].distanceMeters;

            // --------calculate total average distance...            
            totalDistanceM = (locStraightAccumDist[0]+locStraightAccumDist[1]+locStraightAccumDist[2]+locStraightAccumDist[3]) * 0.25;
        }

        return totalDistanceM;
    }
    
}
