package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
//import com.ctre.phoenix6.StatusSignal;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
//import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
//import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150PositionControl;
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


    private static final double MIN_ALLOWED_TURRET_ANGLE = -100.0;
    private static final double MAX_ALLOWED_TURRET_ANGLE = 100.0;

    // --------LAUNCHER TUNING CONSTANTS
    // --------overall normalization
    // --------normalization is usually = Max motor output / max device RPM
    private static final double Launcher_Kn = 0.000183705242146656;
    // --------feedforward
    // --------Ks - static feedforward is the amount of motor output to get started moving
    private static final double Launcher_Ks = 0.0120480016499597;
    // --------Kv -- velocity feedforward is the slope of the motor output to get a particular RPM ( + Ks )
    private static final double Launcher_Kv = 0.000181491957288762;
    // --------Ka -- acceleration constant -- Helps to accelerate or decellerate to a paricular RPM (we are not changing must so 0.0 for now)
    private static final double Launcher_Ka = 0.0;
    // --------PID
    // --------Kp - proportional constant    output =  error * Kp
    private static final double Launcher_Kp = 0.8 * Launcher_Kn;
    // --------Ki - integral constant   output  = Ki x integral( error )
    private static final double Launcher_Ki = 4.0 * Launcher_Kn;
    // --------kd = derivative constant     output = Kd * derivative( error )
    private static final double Launcher_Kd = 1E-6 * Launcher_Kn;
    // --------integral zone ( in sp/pv units )
    // --------Izone -- Error has to be within this amount to be used.
    private static final double Launcher_Izone = 80.0;
    // --------Irange - -min/max value that the integral PID term can have.
    private static final double Launcher_Irange = 0.3;


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
    private static double TurretDistance;
    private static Rotation2d DesiredTurretAngle;
    //private static double TurretRelativeAngle;
    private static double TurretMotorDemand;
    private static double LauncherMotorDemand;
    private static Lib4150PositionControl TurretPositionControl;
    private static double turretAngleEncoder;
    private static double turretAngleVelRPM = 0.0;
    private static DigitalInput clockwiseLimitSwitch;
    private static DigitalInput counterclockwiseLimitSwitch;
    private static boolean clockwiseLimitSwitchValue = false;
    private static boolean counterclockwiseLimitSwitchValue = false;
    private static PIDController launcherPID;
    private static SimpleMotorFeedforward launcherFeedforward;
    private static double locLauncherSpeedActual = 0.0;
    private static double launchertargetSpeed= 100;
    private static boolean launcherSpeedOnTarget = false;
    private static double turretGearRatio = 160;
    private static double locLauncherSpeed1;
    private static double locLauncherSpeed2;
    private static boolean locLauncherOn=false;
    private static boolean turretMode=false;    // false = hub, true = zone
    private static boolean isRed;
    private static boolean turretPositionOnTarget = false;

    private static boolean locTurretManualMode = false;
    private static boolean locTurretCmdManualMode = false;
    private static double locTurretCmdSetpoint = 0.0;

    private static boolean locLauncherManualMode = false;
    private static boolean locLauncherCmdManualMode = false;
    private static double locLauncherCmdSetpoint = 0.0;

    
    //private static SparkMax turretEncoder;
    private static double desiredTurretAngleDegrees;
    
    
    public static void init() {

        //TODO: is this the right spot for this (will this be run after the value in MatchSystem is created?) --- NO - it might not be set yet...   Call during execute
        //get team side from MatchSystem
        isRed = MatchSystem.isRed();

        TurretRotationMotor = new SparkMax(10,MotorType.kBrushless);
        TurrentRotationMotorEncoder = TurretRotationMotor.getEncoder();
        LauncherMotor = new SparkMax(11,MotorType.kBrushless);
        LauncherMotorEncoder = LauncherMotor.getEncoder();
        LauncherMotor2 = new SparkMax(12,MotorType.kBrushless);
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


        //TODO: get the values of the goal position based on alliance - currently using Red values
        //Blue: x: 11.92m y: 4.03m  (is this really red??)
        goalPoseRED = new Translation2d(11.92, 4.03);
        zonePoseRED = new Translation2d(15.0,6.6);

        goalPoseBLUE = new Translation2d(4.63, 4.03);
        zonePoseBLUE = new Translation2d(1.5,1.5);
        
        
        //PositionControl -- Turret -- everything is in degrees.
        TurretPositionControl = new Lib4150PositionControl(2.0,50.0, 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);
        //Speed control
        launcherFeedforward = new SimpleMotorFeedforward (Launcher_Ks, Launcher_Kv, Launcher_Ka);
        launcherPID = new PIDController ( Launcher_Kp, Launcher_Ki, Launcher_Kd);
        launcherPID.setIntegratorRange(-Launcher_Irange, Launcher_Irange);  // only allow integral to addd +/- this amount to output.
        launcherPID.setIZone(Launcher_Izone);        // only do integration when within this many RPMs.

        //limit switches
        clockwiseLimitSwitch = new DigitalInput(1);
        counterclockwiseLimitSwitch = new DigitalInput(2);
        //encoder

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");
        locNTSend.addItemDouble("TurretEncoderRotation", TurretLauncher::getturretAngleEncoder);
        locNTSend.addItemDouble("TurretDesiredAngle", TurretLauncher::getturretAngleTarget);
        locNTSend.addItemDouble("TurretMotorVelocityRPM", TurretLauncher::getTurretMotorRPM);
        locNTSend.addItemDouble("TurretMotorDmd", TurretLauncher::getTurretMotorDemand);
        locNTSend.addItemBoolean("TurretPositionOnTarget", TurretLauncher::getTurretPositionOnTarget);

        locNTSend.addItemDouble("LauncherMotorDmd", TurretLauncher::getLauncherMotorDemand);
        locNTSend.addItemBoolean("TurretclockwiseLimitSwitch", TurretLauncher::getClockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretcounterclockwiseLimitSwitch", TurretLauncher::getCounterclockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretManualMode", TurretLauncher::getTurretManualMode);
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
        clockwiseLimitSwitchValue = !clockwiseLimitSwitch.get();
        counterclockwiseLimitSwitchValue = !counterclockwiseLimitSwitch.get();

        turretAngleEncoder = TurrentRotationMotorEncoder.getPosition()*360/turretGearRatio;
        turretAngleVelRPM = (TurrentRotationMotorEncoder.getVelocity()*360/turretGearRatio)/60;

        //get team side from MatchSystem
        isRed = MatchSystem.isRed();

        
        
        // -------- calc stuff

        robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
        TurretOffset= TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
        robotPose = robotPose.minus(TurretOffset);  // TODO: Should this be add

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

        TurretDistance=robotPose.getDistance(targetPose);

        DesiredTurretAngle = (targetPose.minus(robotPose)).getAngle();

        DesiredTurretAngle = DesiredTurretAngle.minus(new Rotation2d(SwerveOdometry.getrotposition()) );
        double desiredTurretAngleDegRaw = DesiredTurretAngle.getDegrees();
        desiredTurretAngleDegrees = MathUtil.clamp( desiredTurretAngleDegRaw, MIN_ALLOWED_TURRET_ANGLE, MAX_ALLOWED_TURRET_ANGLE);

        // -------calculate launcher speed demand from distance to target....
        // -------move after the calculation for turret distance...
        launchertargetSpeed  = 1000.0 + TurretDistance * 200.0;


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


        //------Position Control
        TurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);

        //use limit switches to stop travel
        double turretMotorMin = -1.0;
        double turretMotorMax = 1.0;
        if (clockwiseLimitSwitchValue)
        {
            turretMotorMin = 0.0;
        }
        else if (counterclockwiseLimitSwitchValue)
        {
            turretMotorMax = 0.0;
        }
        TurretMotorDemand = MathUtil.clamp( TurretMotorDemand, turretMotorMin, turretMotorMax);

        // --------calculate if we on target ... use raw position before clamping to get accurate result.
        double turretRawError = desiredTurretAngleDegRaw - turretAngleEncoder;
        turretPositionOnTarget = ( Math.abs(turretRawError ) <= 2.0 );


        // --------LAUNCHER CONTROL
        //Reads current motor speed //double check unit conversion
        locLauncherSpeed1= LauncherMotorEncoder.getVelocity();
        locLauncherSpeed2= LauncherMotorEncoder2.getVelocity();
        locLauncherSpeedActual = (locLauncherSpeed1+locLauncherSpeed2) *0.5;

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


        // --------tell feeder how fast to go.
        FeederSystem.setMotorRPMTarget(useSpeedTarget * 1.3333333 * 2.0);


        //check if within 75 RPM of target speed
        if (Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 75)
        {
            launcherSpeedOnTarget = true;
        }
        else
        {
            launcherSpeedOnTarget = false;
        }

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


        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand);

        LauncherMotor.set(LauncherMotorDemand); 
        LauncherMotor2.set(LauncherMotorDemand);


        locNTSend.triggerUpdate();
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
    private static boolean getLauncherOn() {
        return locLauncherOn;
    }
    public static double getTurretMotorRPM() {
        return turretAngleVelRPM;
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

}

