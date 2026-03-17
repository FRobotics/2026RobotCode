package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
//import com.ctre.phoenix6.StatusSignal;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
//import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
//import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150PositionControl;
//import Lib4150.Lib4150RateOfChange3;
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
    private static final double TURRET_FILTER_TIME_CONST = 0.100;

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
    private static double TurretDistance;
    private static Rotation2d DesiredTurretAngle;
    //private static double TurretRelativeAngle;
    private static double TurretMotorDemand;
    private static double LauncherMotorDemand;
    private static Lib4150FilterLowPassBW1 TurretPosFilter;
    private static Lib4150PositionControl TurretPositionControl;
    private static double turretAngleEncoder;
    private static double turretAngleVelRPM = 0.0;
    private static DigitalInput clockwiseLimitSwitch;
    private static DigitalInput counterclockwiseLimitSwitch;
    private static Lib4150DigEdgeOn TurretCWLimitSwitchEdgeOn;
    private static Lib4150DigEdgeOn TurretCCWLimitSwitchEdgeOn;
    private static boolean clockwiseLimitSwitchValue = false;
    private static boolean counterclockwiseLimitSwitchValue = false;
    private static PIDController launcherPID;
    private static SimpleMotorFeedforward launcherFeedforward;
    private static double locLauncherSpeedActual = 0.0;
    private static double launchertargetSpeed= 100.0;
    private static boolean launcherSpeedOnTarget = false;
    // private static double turretGearRatio = 40.0;
    private static double turretGearRatio = 160.0;
    private static double locLauncherSpeed1;
    private static double locLauncherSpeed2;
    private static boolean locLauncherOn=false;
    private static boolean turretMode=false;
    private static boolean isRed;
    private static Translation2d goalPoseRed;
    private static Translation2d goalPoseBlue;
    private static Translation2d zonePoseRed;
    private static Translation2d zonePoseBlue;
    private static double intakeAngle;
    private static boolean launcherAgitatorPermissive = false;
    private static boolean tmpclockwiseLimitSwitch;
    private static boolean tmpcounterclockwiseLimitSwitch;
    // Launcher Tunning Constants
    // Max motor output/ max device rpm
    private static final double Launcher_Kn = 1.0 / 5721.6;
    // static feedforward (amount of motor output to get started moving)
    private static final double Launcher_Ks = 0.0149140408235032;
    // --------Kv -- velocity feedforward is the slope of the motor output to get a particular RPM ( + Ks )
    private static final double Launcher_Kv = 0.000172170358450721;
    // --------Ka -- acceleration constant -- Helps to accelerate or decellerate to a paricular RPM (we are not changing must so 0.0 for now)
    private static final double Launcher_Ka = 0.0;
    // --------PID
    // --------Kp - proportional constant    output =  error * Kp
    private static final double Launcher_Kp =Launcher_Kn *  4.0;       // was 0.7
    // --------Ki - integral constant   output  = Ki x integral( error )
    private static final double Launcher_Ki = Launcher_Kn * 3.5;
    // --------kd = derivative constant     output = Kd * derivative( error )
    private static final double Launcher_Kd = Launcher_Kn * 1E-5;
    // --------integral zone ( in sp/pv units )
    // --------Izone -- Error has to be within this amount to be used.
    private static final double Launcher_Izone = 60.0;
    // --------Irange - -min/max value that the integral PID term can have.
    private static final double Launcher_Imax = 0.3;
    //private static final double LAUNCHER_FILTER_TIME_CONST = 0.100;   // seconds
    private static final double LAUNCHER_M = 193.820210097687;
    private static final double LAUNCHER_B = 1030.12111625272;

    
    //private static SparkMax turretEncoder;
    private static double desiredTurretAngleDegrees;
    private static double desiredTurretAngleDegRaw;    
    
    public static void init() {
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
        //cmdTurretAutoMode();
        //cmdLauncherAutoMode();
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


        //Blue: x: 11.92m y: 4.03m
        goalPoseRed = new Translation2d(11.92,4.03);
        zonePoseRed = new Translation2d(15.0,6.6);
        
        goalPoseBlue = new Translation2d(4.63, 4.03);
        zonePoseBlue = new Translation2d(1.5,1.5);        
        
        //PositionControl
        TurretPositionControl = new Lib4150PositionControl(1.0,30.0,0.05, 1.00, 1.00, 1.0e-5, false, false);
        //Speed control
        launcherFeedforward = new SimpleMotorFeedforward (Launcher_Ks, Launcher_Kv, Launcher_Ka);
        launcherPID = new PIDController ( Launcher_Kp, Launcher_Ki, Launcher_Kd);
        launcherPID.setIntegratorRange(-Launcher_Imax, Launcher_Imax);
        launcherPID.setIZone(Launcher_Izone);

        //limit switches
        clockwiseLimitSwitch = new DigitalInput(1);
        counterclockwiseLimitSwitch = new DigitalInput(2);
        TurretCWLimitSwitchEdgeOn = new Lib4150DigEdgeOn();
        TurretCCWLimitSwitchEdgeOn = new Lib4150DigEdgeOn();

        //encoder

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");
        locNTSend.addItemDouble("TurretEncoderRotation", TurretLauncher::getturretAngleEncoder);
        locNTSend.addItemDouble("TurretDesiredAngle", TurretLauncher::getturretAngleTarget);
        locNTSend.addItemDouble("TurretMotorVelocityRPM", TurretLauncher::getTurretMotorRPM);
        locNTSend.addItemDouble("TurretMotorDmd", TurretLauncher::getTurretMotorDemand);
        //locNTSend.addItemDouble("TurretAngleVelocityDegSec", TurretLauncher::getTurretMotorDegSec);
        locNTSend.addItemDouble("LauncherMotorDmd", TurretLauncher::getLauncherMotorDemand);
        locNTSend.addItemBoolean("TurretclockwiseLimitSwitch", TurretLauncher::getClockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretcounterclockwiseLimitSwitch", TurretLauncher::getCounterclockwiseLimitSwitch);
        locNTSend.addItemDouble("LauncherSpeed1",TurretLauncher::getLauncherSpeed1 );
        locNTSend.addItemDouble("LauncherSpeed2", TurretLauncher::getLauncherSpeed2);
        locNTSend.addItemDouble("LauncherSpeedActual", TurretLauncher::getLauncherActualSpeed);
        locNTSend.addItemDouble("LauncherTargetSpeed", TurretLauncher::getLauncherTargetSpeed);
        locNTSend.addItemBoolean("LauncherSpeedOnTarget", TurretLauncher::getLauncherSpeedOnTarget);
        locNTSend.addItemBoolean("LauncherOn", TurretLauncher::getLauncherOn);
        //locNTSend.addItemBoolean("LauncherManualMode", TurretLauncher::getLauncherManualMode);



        
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // -------- read sensors and correct limit switches
        turretAngleEncoder = (TurrentRotationMotorEncoder.getPosition()*360.0/turretGearRatio) + 180.0 - 3.7;
        turretAngleVelRPM = (TurrentRotationMotorEncoder.getVelocity()*360.0/turretGearRatio)/60.0;

        tmpclockwiseLimitSwitch = !clockwiseLimitSwitch.get();
        tmpcounterclockwiseLimitSwitch = !counterclockwiseLimitSwitch.get();

        if (tmpcounterclockwiseLimitSwitch==false){
            counterclockwiseLimitSwitchValue = tmpcounterclockwiseLimitSwitch;
        } else{
            if ((turretAngleEncoder>= 305)&&(counterclockwiseLimitSwitchValue==true)) {
                counterclockwiseLimitSwitchValue = true;
            } else {
                counterclockwiseLimitSwitchValue = false;
            }
        }

        if (tmpclockwiseLimitSwitch==false){
            clockwiseLimitSwitchValue=tmpclockwiseLimitSwitch;
        } else{
            if ((turretAngleEncoder<= 55)&&(clockwiseLimitSwitchValue==true)){
                clockwiseLimitSwitchValue = true;
            } else {
                clockwiseLimitSwitchValue = false;
            }
        }


        //get team side from MatchSystem
        isRed = MatchSystem.isRed();        
        Translation2d targetPose = goalPoseRed;

        if (turretMode) {
            if (isRed){
                targetPose = zonePoseRed;

            }
            else {
                targetPose = zonePoseBlue;
            }

        }
        else {
            if (isRed){
                targetPose = goalPoseRed;
            }
            else{
                targetPose = goalPoseBlue;
            }
        }
        
        
        // -------- calc stuff

        robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
        TurretOffset= TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
        robotPose = robotPose.minus(TurretOffset);



        TurretDistance=robotPose.getDistance(targetPose);
        DesiredTurretAngle = (targetPose.minus(robotPose)).getAngle();

        DesiredTurretAngle = DesiredTurretAngle.minus(new Rotation2d(SwerveOdometry.getrotposition()) );
        desiredTurretAngleDegrees = MathUtil.clamp( DesiredTurretAngle.getDegrees(), MIN_ALLOWED_TURRET_ANGLE, MAX_ALLOWED_TURRET_ANGLE);

        // -------calculate launcher speed demand from distance to target....
        // -------move after the calculation for turret distance...
        launchertargetSpeed  = 700.0 + TurretDistance * 200.0;


        //------Position Control
        TurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);

        //-------------------------------
        if (IntakeSystem.intakeAngleTarget > 45){
            desiredTurretAngleDegrees = 180;
        }
        //Reads current motor speed //double check unit conversion
        locLauncherSpeed1= LauncherMotorEncoder.getVelocity();
        locLauncherSpeed2= LauncherMotorEncoder2.getVelocity();
        locLauncherSpeedActual = (locLauncherSpeed1+locLauncherSpeed2) *0.5;
        
        double useSpeedTarget = launchertargetSpeed;
        if ( !locLauncherOn ) {
            useSpeedTarget = 0.0;
        }

        //check if within 75 RPM of target speed
        if (Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 75)
        {
            launcherSpeedOnTarget = true;
        }
        else
        {
            launcherSpeedOnTarget = false;
        }

        launcherAgitatorPermissive = (( Math.abs(locLauncherSpeedActual - launchertargetSpeed) < 200.0 ) );
        // Set launcher motor demand
        double launchFeedForward = launcherFeedforward.calculate(useSpeedTarget);
        double launcherPIDOutput = launcherPID.calculate(locLauncherSpeedActual, useSpeedTarget);
        LauncherMotorDemand = MathUtil.clamp(launchFeedForward+launcherPIDOutput, -1.0, 1.0);


        //use limit switches to stop travel
        // clamp output based on limit switches.
        double tmpMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);
        double turretHigh = (clockwiseLimitSwitchValue) ? 0.0: -1.0;
        double turretLow = (counterclockwiseLimitSwitchValue) ? 0.0 : 1.0;
        
        TurretMotorDemand = MathUtil.clamp(tmpMotorDemand, turretLow, turretHigh);        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand);

        if ( !locLauncherOn ) {
            LauncherMotorDemand = 0;
            launcherPID.reset();
        }
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
    public static boolean getAgitatorStartPermissive() {
        return launcherAgitatorPermissive;
    }
/*public static void cmdTurretManualMode() {
        locTurretCmdManualMode = true;
    }*/

    // --------request turret auto mode
   /*  public static void cmdTurretAutoMode() {
        locTurretCmdManualMode = false;
    }}
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
    }*/

    
}