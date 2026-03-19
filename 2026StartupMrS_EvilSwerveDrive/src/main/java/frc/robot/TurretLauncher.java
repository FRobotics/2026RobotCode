package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
//import com.ctre.phoenix6.StatusSignal;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
//import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
//import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150PositionControl;
import Lib4150.Lib4150RateOfChange3;
import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150FilterLowPassBW1;
import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
//import edu.wpi.first.math.util.Units;
//import edu.wpi.first.units.measure.Angle;
//import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

public class TurretLauncher {


    // --------CONSTANTS

    // --------THESE WILL NEED TO BE TUNED...
    private static final double MIN_INTAKE_ANGLE = 65.0;
    // --------Turret angle range is 0.0 - 360.0 degrees.
    private static final double MIN_ALLOWED_TURRET_ANGLE = 50.0;
    private static final double MIN_LIMIT_SWITCH_TURRET_ANGLE = MIN_ALLOWED_TURRET_ANGLE;
    private static final double MAX_ALLOWED_TURRET_ANGLE = 310.0;
    private static final double MAX_LIMIT_SWITCH_TURRET_ANGLE = MAX_ALLOWED_TURRET_ANGLE;
    private static final double TURRET_FILTER_TIME_CONST = 0.100;   // seconds

    // --------LAUNCHER TUNING CONSTANTS
    // --------based on data from girls of steel testing day 3/14/2026
    private static final double Launcher_MaxRPM = 5678.93;
    // --------overall normalization
    // --------normalization is usually = Max motor output / max device RPM
    //private static final double Launcher_Kn = 1.0 / 5721.6;
    private static final double Launcher_Kn = 1.0 / Launcher_MaxRPM;
    // --------feedforward
    // --------Ks - static feedforward is the amount of motor output to get started moving
    //private static final double Launcher_Ks = 0.0149140408235032;
    private static final double Launcher_Ks = 0.0147960434685002;
    // --------Kv -- velocity feedforward is the slope of the motor output to get a particular RPM ( + Ks )
    //private static final double Launcher_Kv = 0.000172170358450721;
    private static final double Launcher_Kv = ( 1.0 - Launcher_Ks ) / Launcher_MaxRPM;
    // --------Ka -- acceleration constant -- Helps to accelerate or decellerate to a paricular RPM (we are not changing must so 0.0 for now)
    private static final double Launcher_Ka = 0.0;
    // --------PID
    // --------Kp - proportional constant    output =  error * Kp
    private static final double Launcher_Kp = Launcher_Kn *  4.0;       // was 0.7
    // --------Ki - integral constant   output  = Ki x integral( error )
    private static final double Launcher_Ki = Launcher_Kn * 3.5;
    // --------kd = derivative constant     output = Kd * derivative( error )
    private static final double Launcher_Kd = Launcher_Kn * 1E-5;
    // --------integral zone ( in sp/pv units )
    // --------Izone -- Error has to be within this amount to be used.
    private static final double Launcher_Izone = 60.0;
    // --------Irange - -min/max value that the integral PID term can have.
    private static final double Launcher_Irange = 0.3;
    private static final double TARG_DISTANCE_FILTER_TIME_CONST = 0.100;   // seconds
    // private static final double LAUNCHER_M = 183.586426696663;   
    // private static final double LAUNCHER_B = 991.971428571429;
    private static final double LAUNCHER_M = 193.820210097687;
    private static final double LAUNCHER_B = 1030.12111625272;



    //private double TURRETOFFSET;
    //private Rotation2d RobotRotation;

    private TurretLauncher(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static SparkMax TurretRotationMotor;
    private static SparkMax LauncherMotor;
    private static SparkMax LauncherMotor2; 
    private static RelativeEncoder TurrentRotationMotorEncoder;  // sample....
    private static RelativeEncoder LauncherMotorEncoder;
    private static RelativeEncoder LauncherMotorEncoder2;
    private static Translation2d robotPose;
    private static Translation2d TurretOffset;
    private static Translation2d goalPoseRED;
    private static Translation2d zonePoseRED;
    private static Translation2d goalPoseBLUE;
    private static Translation2d zonePoseBLUE;
    private static double TargetDistance;
    private static Rotation2d DesiredTurretAngle;
    private static double TurretMotorDemand;
    private static double LauncherMotorDemand;
    private static Lib4150FilterLowPassBW1 TurretPosFilter;
    private static Lib4150PositionControl TurretPositionControl;
    private static double turretAngleEncoder;
    private static double turretAngleVelDegSec = 0.0;
    private static DigitalInput clockwiseLimitSwitch;
    private static DigitalInput counterclockwiseLimitSwitch;
    private static Lib4150DigEdgeOn TurretCWLimitSwitchEdgeOn;
    private static Lib4150DigEdgeOn TurretCCWLimitSwitchEdgeOn;
    private static boolean clockwiseLimitSwitchValue = false;
    private static boolean counterclockwiseLimitSwitchValue = false;
    private static PIDController launcherPID;
    private static Lib4150FilterLowPassBW1 TargetDistanceFilter;
    private static SimpleMotorFeedforward launcherFeedforward;
    private static double locLauncherSpeedActual = 0.0;
    private static double launchertargetSpeed= 100.0;
    private static boolean launcherSpeedOnTarget = false;
    // private static double turretGearRatio = 40.0;
    private static double turretGearRatio = 160.0;
    private static double locLauncherSpeed1;
    private static double locLauncherSpeed2;
    private static boolean locLauncherOn=false;
    private static boolean turretMode=false;    // false = hub, true = zone
    private static boolean isRed;
    private static boolean turretPositionOnTarget = false;
    private static boolean launcherAgitatorPermissive = false;
    private static boolean locTurretManualMode = true;
    private static boolean locTurretCmdManualMode = true;
    private static double locTurretCmdSetpoint = 180.0;

    private static boolean locLauncherManualMode = false;
    private static boolean locLauncherCmdManualMode = false;
    private static double locLauncherCmdSetpoint = 0.0;

    private static Lib4150RateOfChange3 locTargetAngleROC = new Lib4150RateOfChange3();
    private static double locTargetAngleRotVel = 0.0;
    // private static Lib4150RateOfChange3 locTurretAngleROC = new Lib4150RateOfChange3();
    // private static double locTurretAngleRotVel = 0.0;

    
    //private static SparkMax turretEncoder;
    private static double desiredTurretAngleDegrees;
    private static double desiredTurretAngleDegRaw;    
    
    public static void init() {

        //get team side from MatchSystem  -- also need to call in execute method
        isRed = MatchSystem.isRed();

        TurretRotationMotor = new SparkMax(CanId.TurretMotor,MotorType.kBrushless);
        TurrentRotationMotorEncoder = TurretRotationMotor.getEncoder();
        LauncherMotor = new SparkMax(CanId.LauncherMotor1,MotorType.kBrushless);
        LauncherMotorEncoder = LauncherMotor.getEncoder();
        LauncherMotor2 = new SparkMax(CanId.LauncherMotor2,MotorType.kBrushless);
        LauncherMotorEncoder2 = LauncherMotor2.getEncoder();

        cmdLauncherOff();
        cmdBallsToHub();
        cmdTurretAutoMode();
        cmdLauncherAutoMode();
        //open motor config
        //SparkMaxConfig TurretSpinConfig = new SparkMaxConfig();
        
        //motor Config
        // will  use rev hardare config stuff not this
        //TurretSpinConfig.idleMode(IdleMode.kBrake);
        //TurretSpinConfig.smartCurrentLimit(50);
        //TurretSpinConfig.openLoopRampRate(0.2);
        
      
        //set initial system state
        // offset is 5.75 inches from the center of the robot or 0.14605 meters.
        TurretOffset = new Translation2d( -0.14605 , 0);


        //Blue: x: 11.92m y: 4.03m  (is this really red??)
        goalPoseRED = new Translation2d(11.92, 4.03);
        zonePoseRED = new Translation2d(15.0,6.6);

        goalPoseBLUE = new Translation2d(4.63, 4.03);
        zonePoseBLUE = new Translation2d(1.5,1.5);
        
        
        //PositionControl -- Turret -- everything is in degrees.
        TurretPositionControl = new Lib4150PositionControl(1.0,30.0, 
                            0.05, 1.00, 1.00, 1.0e-5, false, false);

        TurretPosFilter = new Lib4150FilterLowPassBW1(TURRET_FILTER_TIME_CONST, 0.020);

        //Speed control
        TargetDistanceFilter = new Lib4150FilterLowPassBW1(TARG_DISTANCE_FILTER_TIME_CONST, 0.020);
        launcherFeedforward = new SimpleMotorFeedforward (Launcher_Ks, Launcher_Kv, Launcher_Ka);
        launcherPID = new PIDController ( Launcher_Kp, Launcher_Ki, Launcher_Kd);
        launcherPID.setIntegratorRange(-Launcher_Irange, Launcher_Irange);  // only allow integral to addd +/- this amount to output.
        launcherPID.setIZone(Launcher_Izone);        // only do integration when within this many RPMs.

        //limit switches
        clockwiseLimitSwitch = new DigitalInput(1);
        counterclockwiseLimitSwitch = new DigitalInput(2);
        TurretCWLimitSwitchEdgeOn = new Lib4150DigEdgeOn();
        TurretCCWLimitSwitchEdgeOn = new Lib4150DigEdgeOn();

        //encoder

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");

        // --------auto target calc info
        locNTSend.addItemDouble("TargetAngle", TurretLauncher::getTargetAngle);
        locNTSend.addItemDouble("TargetDistance", TurretLauncher::getTargetDistance);
        locNTSend.addItemDouble("TurretDesiredAngleVel", TurretLauncher::getTurretAngleTargetVel);
        
        // --------turret control
        locNTSend.addItemBoolean("TurretclockwiseLimitSwitch", TurretLauncher::getClockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretcounterclockwiseLimitSwitch", TurretLauncher::getCounterclockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretPositionOnTarget", TurretLauncher::getTurretPositionOnTarget);
        locNTSend.addItemBoolean("TurretManualMode", TurretLauncher::getTurretManualMode);
        locNTSend.addItemDouble("TurretEncoderRotation", TurretLauncher::getturretAngleEncoder);
        locNTSend.addItemDouble("TurretDesiredAngle", TurretLauncher::getturretAngleTarget);
        locNTSend.addItemDouble("TurretAngleVelocityDegSec", TurretLauncher::getTurretMotorDegSec);
        locNTSend.addItemDouble("TurretMotorDmd", TurretLauncher::getTurretMotorDemand);
        // --------launcher control
        locNTSend.addItemDouble("LauncherMotorDmd", TurretLauncher::getLauncherMotorDemand);
        locNTSend.addItemDouble("LauncherSpeed1",TurretLauncher::getLauncherSpeed1 );
        locNTSend.addItemDouble("LauncherSpeed2", TurretLauncher::getLauncherSpeed2);
        locNTSend.addItemDouble("LauncherSpeedActual", TurretLauncher::getLauncherActualSpeed);
        locNTSend.addItemDouble("LauncherTargetSpeed", TurretLauncher::getLauncherTargetSpeed);
        locNTSend.addItemBoolean("LauncherSpeedOnTarget", TurretLauncher::getLauncherSpeedOnTarget);
        locNTSend.addItemBoolean("LauncherOn", TurretLauncher::getLauncherOn);
        locNTSend.addItemBoolean("LauncherManualMode", TurretLauncher::getLauncherManualMode);



        
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // -------- read sensors ---- note limit switch engated returns false, so negate.

        // --------read the clock wise limit switch.... (This is the low value)
        // --------if we hit the limit switch once, keep it on until the angle is above the limit switch value....
        // --------Add a little hysteresis of 2.0 degrees for the Turret actual position.
        // --------This depends on the previous value of clockwiseLimitSwitchValue and turretAngleEncoder
        clockwiseLimitSwitchValue = !clockwiseLimitSwitch.get() || ( clockwiseLimitSwitchValue && ( turretAngleEncoder <= (MIN_LIMIT_SWITCH_TURRET_ANGLE+3.0)));

        // --------read the counter clock wise limit switch.... (This is the high value)
        // --------if we hit the limit switch once, keep it on until the angle is above the limit switch value....
        // --------Add a little hysteresis of 2.0 degrees for the Turret actual position.
        // --------This depends on the previous value of clockwiseLimitSwitchValue and turretAngleEncoder
        counterclockwiseLimitSwitchValue = !counterclockwiseLimitSwitch.get() || ( counterclockwiseLimitSwitchValue && ( turretAngleEncoder >= (MAX_LIMIT_SWITCH_TURRET_ANGLE-3.0)));

        // ---------if we just hit the high limit switch, set the value of the encoder position.
        if ( TurretCCWLimitSwitchEdgeOn.execEdgeOn(counterclockwiseLimitSwitchValue) ) {
            //TODO: after char -- TurrentRotationMotorEncoder.setPosition( calcEncoderRawValueFromTurretDeg(MAX_LIMIT_SWITCH_TURRET_ANGLE));
        }

        // ---------if we just hit the low limit switch, set the value of the encoder position.
        if ( TurretCWLimitSwitchEdgeOn.execEdgeOn(clockwiseLimitSwitchValue) ) {
            //TODO: after char -- TurrentRotationMotorEncoder.setPosition( calcEncoderRawValueFromTurretDeg(MIN_LIMIT_SWITCH_TURRET_ANGLE));
        }

        // --------read the turret position and velocity
        turretAngleEncoder = calcTurretDegFromRawEncoder( TurrentRotationMotorEncoder.getPosition() ) - 3.7;
        turretAngleVelDegSec = calcTurretVelFromRawEncoder(TurrentRotationMotorEncoder.getVelocity() );

        // ------------------------------------------------------------------------------------------------------------
        // --------AUTO SETPOINT CALCULATIONS.
        // --------get team side from MatchSystem
        isRed = MatchSystem.isRed();

        // --------if odometry is valid then calculate desired turret angle and distance to target.
        if ( SwerveOdometry.isOdometryValid() ) {
        
            // -------- calc distance to target, and turret angle to setpoint based on absolute odometry..

            // --------where the center of the robot is on the field.
            robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
            // --------turret relative position to center of robot.
            TurretOffset= TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
            // --------turret absolute field position.
            robotPose = robotPose.minus(TurretOffset);  // TODO: Should this be add

            // --------get the position of our target..  Alliance or goal, red or blue...
            Translation2d targetPose = goalPoseRED;

            if (turretMode){
                if ( isRed ) {
                    targetPose=zonePoseRED;
                }
                else {
                    targetPose=zonePoseBLUE;
                }
            }
            else {
                if ( isRed ) {
                    targetPose=goalPoseRED;
                }
                else {
                    targetPose=goalPoseBLUE;
                }
            }

            // --------calculate distance to target.
            TargetDistance=TargetDistanceFilter.execFilter(robotPose.getDistance(targetPose), systemElapsedTimeSec);

            DesiredTurretAngle = (targetPose.minus(robotPose)).getAngle();

            DesiredTurretAngle = DesiredTurretAngle.minus(new Rotation2d(SwerveOdometry.getrotposition()) );
            desiredTurretAngleDegRaw = TurretPosFilter.execFilter( MathUtil.inputModulus( DesiredTurretAngle.getDegrees(), 0.0, 360.0),
                                        systemElapsedTimeSec);
            desiredTurretAngleDegrees = MathUtil.clamp( desiredTurretAngleDegRaw, MIN_ALLOWED_TURRET_ANGLE, MAX_ALLOWED_TURRET_ANGLE);

        }
        // --------no valid odometry, set default turret position and distance to target.
        else {
            desiredTurretAngleDegRaw = 180.0;   // degrees
            desiredTurretAngleDegrees = 180.0;  // degrees
            TargetDistance = 2.0;   // meters
        }

        // -------calculate launcher speed demand from distance to target....
        // -------move after the calculation for turret distance...
        launchertargetSpeed  = LAUNCHER_B + TargetDistance * LAUNCHER_M;


        // ------------------------------------------------------------------------------------------------------------
        // --------TURRET CONTROL
        // --------process turrent manual mode setpoint.
        if ( locTurretCmdManualMode ) {         // we want manual mode
            if ( !locTurretManualMode ) {       // just changed to manual mode.
                locTurretCmdSetpoint = desiredTurretAngleDegrees;   // on first changing use current auto setpoint for bumpless transfer
                locTurretManualMode = true;
            }
            else {                              // has been manual mode for a while.
                desiredTurretAngleDegRaw = locTurretCmdSetpoint;
                desiredTurretAngleDegrees = MathUtil.clamp( locTurretCmdSetpoint, MIN_ALLOWED_TURRET_ANGLE, MAX_ALLOWED_TURRET_ANGLE);
            }
        }
        // --------want auto mode
        else {
            if ( locTurretManualMode ) {
                locTurretManualMode = false;
            }
        }

        // --------if intake arm is still up, don't move turret!!
        if ( IntakeSystem.getIntakeArmAngleActual() > MIN_INTAKE_ANGLE ) {
            desiredTurretAngleDegrees = 180.0;
        }

        // --------calculate the first derivative (rate of change) of the turret deg setpoint, giving us Deg/Sec.
        // --------Smooth it a little.
        // --------Once we know how fast the turret moves, we can create a feedforward to add to the position
        // --------control.  This will allow us to better track the target as we are moving, rather to
        // --------wait for the position control to catch up..
        // TODO: After characterizing turret rotation speed, use this as a feedforward...
        locTargetAngleRotVel = locTargetAngleROC.ExecROC3( desiredTurretAngleDegrees, systemElapsedTimeSec );

        //------Position Control
        double tmpTurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);

        // --------now clamp output based on limit switches...
        double tmpLow = ( clockwiseLimitSwitchValue ) ? 0.0 : -1.0;
        double tmpHigh = ( counterclockwiseLimitSwitchValue ) ? 0.0 : 1.0;
        TurretMotorDemand = MathUtil.clamp(tmpTurretMotorDemand, tmpLow, tmpHigh );

        // --------calculate if we on target ... use raw position before clamping to get accurate result.
        double turretRawError = desiredTurretAngleDegRaw - turretAngleEncoder;
        turretPositionOnTarget = ( Math.abs(turretRawError ) <= 2.0 );

        // ------------------------------------------------------------------------------------------------------------
        // --------LAUNCHER CONTROL
        //Reads current motor speed //double check unit conversion
        locLauncherSpeed1= LauncherMotorEncoder.getVelocity();
        locLauncherSpeed2= LauncherMotorEncoder2.getVelocity();
        locLauncherSpeedActual = (locLauncherSpeed1+locLauncherSpeed2) * 0.5;

        // --------process launcher manual mode setpoint.
        if ( locLauncherCmdManualMode ) {         // we want manual mode
            if ( !locLauncherManualMode ) {       // just changed to manual mode.
                locLauncherCmdSetpoint = launchertargetSpeed;   // on first changing use current auto setpoint for bumpless transfer
                locLauncherManualMode = true;
            }
            else {                              // has been manual mode for a while.
                launchertargetSpeed = MathUtil.clamp( locLauncherCmdSetpoint, 0.0, 6000.0);
            }
        }
        // --------want auto mode
        else {
            if ( locLauncherManualMode ) {
                locLauncherManualMode = false;
            }
        }
        
        double useSpeedTarget = launchertargetSpeed;
        if ( !locLauncherOn ) {
            useSpeedTarget = 0.0;
        }

        // --------tell feeder how fast to go.   For now approx 80%
        FeederSystem.setMotorRPMTarget(useSpeedTarget * 1.3333333 * 2.0 * 0.95);

        //check if within 75 RPM of target speed
        launcherSpeedOnTarget = (Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 75.0 );
        // launcherAgitatorPermissive = (( Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 200.0 ) && locLauncherSpeedActual > 500.0);
        launcherAgitatorPermissive = (( Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 100.0 ) );

        // Set launcher motor demand
        double launchFeedForward = launcherFeedforward.calculate(useSpeedTarget);
        double launcherPIDOutput = launcherPID.calculate(locLauncherSpeedActual, useSpeedTarget);
        // --------special case for 0.0  -- don't control just coast.
        if ( useSpeedTarget == 0.0 ) {
            launcherPID.reset();      // reset integral.
            launcherPIDOutput = 0.0;
        }

        LauncherMotorDemand = MathUtil.clamp(launchFeedForward+launcherPIDOutput, -1.0, 1.0);
        // --------this could be redundant.
        if ( !locLauncherOn ) {
            LauncherMotorDemand = 0;
            launcherPID.reset();
        }


        // ------------------------------------------------------------------------------------------------------------
        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand);

        LauncherMotor.set(LauncherMotorDemand); 
        LauncherMotor2.set(LauncherMotorDemand);


        locNTSend.triggerUpdate();
        return;
    }


    
    // --------internal calculation routines
    
    // --------calculate turret position in degrees given the raw encoder value in rotations.
    // --------allow angle value to go 0 360.0
    private static double calcTurretDegFromRawEncoder( double parmEncoderRotations ) {
        return 180.0+parmEncoderRotations*360.0/turretGearRatio;
    }

    
    // --------calculate turret velocity in degrees/sec given the raw encoder value in rotations.
    // --------allow angle value to go 0 360.0
    private static double calcTurretVelFromRawEncoder( double parmEncoderVelRPM ) {
        return parmEncoderVelRPM*360.0/turretGearRatio/60.0;
    }

    // ---------calculate the raw encoder value in rotations given the arm position in degrees
    // --------allow angle value to go 0 360.0
    @SuppressWarnings("unused")
    private static double calcEncoderRawValueFromTurretDeg( double turretDeg ) {
        double tmpTurretDeg = MathUtil.inputModulus(turretDeg, 0.0, 360.0);
        return (tmpTurretDeg-180.0) / 360.0 * turretGearRatio;
    }


    
              //METERS      

    private static double getTurretMotorDemand() {
        return TurretMotorDemand;
    }
    private static double getLauncherMotorDemand(){
        return LauncherMotorDemand;
    }
        public static double getturretAngleEncoder() {
        return turretAngleEncoder;
    }
    private static double getturretAngleTarget(){
        return desiredTurretAngleDegrees;
    }
    private static boolean getLauncherSpeedOnTarget() {
        return launcherSpeedOnTarget;
    }
    public static boolean getAgitatorStartPermissive() {
        return launcherAgitatorPermissive;
    }
    private static boolean getLauncherOn() {
        return locLauncherOn;
    }
    public static double getTurretMotorDegSec() {
        return turretAngleVelDegSec;
    }
    public static boolean getClockwiseLimitSwitch(){
        return clockwiseLimitSwitchValue;
    }
    public static boolean getCounterclockwiseLimitSwitch(){
        return counterclockwiseLimitSwitchValue;
    }
    public static double getLauncherSpeed1(){
        return locLauncherSpeed1;
    }
    public static double getLauncherSpeed2(){
        return locLauncherSpeed2;
    }
    public static double getLauncherActualSpeed(){
        return locLauncherSpeedActual;
    }
        public static double getLauncherTargetSpeed(){
        return launchertargetSpeed;
    }
    public static void cmdLauncherOn(){
        locLauncherOn=true;
    }
    public static void cmdLauncherOff(){
        locLauncherOn=false;
    }
    public static void cmdBallsToHub(){
        turretMode = false;
    }
    public static void cmdBallsToZone(){
        turretMode = true;
    }

    // --------TURRET MANUAL MODE
    // --------request turret manual mode
    public static void cmdTurretManualMode() {
        locTurretCmdManualMode = true;
    }

    // --------request turret auto mode
    public static void cmdTurretAutoMode() {
        locTurretCmdManualMode = false;
    }

    // --------increment or decrement the turret manual setpoint
    public static void cmdTurretIncDecManualSetpoint( double chgAmount ) {
        locTurretCmdSetpoint = MathUtil.clamp( locTurretCmdSetpoint + chgAmount, MIN_ALLOWED_TURRET_ANGLE, MAX_ALLOWED_TURRET_ANGLE) ;
    }

    // --------get turret manual mode.
    public static boolean getTurretManualMode() {
        return locTurretManualMode;
    }

    // --------Launcher manual mode.
    // --------request launcher manual mode
    public static void cmdLauncherManualMode() {
        locLauncherCmdManualMode = true;
    }

    // --------request launcher auto mode
    public static void cmdLauncherAutoMode() {
        locLauncherCmdManualMode = false;
    }

    // --------increment or decrement the launcher manual setpoint
    public static void cmdLauncherIncDecManualSetpoint( double chgAmount ) {
        locLauncherCmdSetpoint = MathUtil.clamp( locLauncherCmdSetpoint + chgAmount, 0.0, 5000.0) ;
    }

    // --------get Launcher manual mode.
    public static boolean getLauncherManualMode() {
        return locLauncherManualMode;
    }

    // --------get Turret Position on Target
    public static boolean getTurretPositionOnTarget() {
        return turretPositionOnTarget;
    }

    // --------target

    // --------get target angle rate of change  (This is rate of change of turret setpoint...including manual mode.)
    public static double getTurretAngleTargetVel() {
        return locTargetAngleRotVel;    // deg/sec
    }

    // --------get target angle -- actual target, not manual or clampled
    public static double getTargetAngle() {
        return desiredTurretAngleDegRaw; 
    }

    // --------get target distance - actual target, not manual or clamped.
    public static double getTargetDistance() {
        return TargetDistance;    
    }

    
}

