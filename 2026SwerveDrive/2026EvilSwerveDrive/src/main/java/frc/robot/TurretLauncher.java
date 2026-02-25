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
import edu.wpi.first.math.util.Units;
//import edu.wpi.first.units.measure.Angle;
//import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

public class TurretLauncher {

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
    private static Translation2d goalPose;
    private static Translation2d zonePose;
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
    private static boolean turretMode=false;
    private static boolean isRed;

    
    private static double Launcher_Kn = 1.0 / 3.98670783;
    //private static SparkMax turretEncoder;
    private static double desiredTurretAngleDegrees;
    
    
    public static void init() {

        //TODO: is this the right spot for this (will this be run after the value in MatchSystem is created?)
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
        //Blue: x: 11.92m y: 4.03m
        goalPose = new Translation2d(4.63, 4.03);
        zonePose = new Translation2d(1.5,1.5);
        
        
        //PositionControl
        TurretPositionControl = new Lib4150PositionControl(Units.rotationsToDegrees(2.0),Units.rotationsToDegrees(50.0), 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);
        //Speed control
        launcherFeedforward = new SimpleMotorFeedforward (0.0, Launcher_Kn, 0.0);
        launcherPID = new PIDController ( Launcher_Kn *.5, 0, 0);

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
        locNTSend.addItemDouble("LauncherMotorDmd", TurretLauncher::getLauncherMotorDemand);
        locNTSend.addItemBoolean("TurretclockwiseLimitSwitch", TurretLauncher::getClockwiseLimitSwitch);
        locNTSend.addItemBoolean("TurretcounterclockwiseLimitSwitch", TurretLauncher::getCounterclockwiseLimitSwitch);
        locNTSend.addItemDouble("LauncherSpeed1",TurretLauncher::getLauncherSpeed1 );
        locNTSend.addItemDouble("LauncherSpeed2", TurretLauncher::getLauncherSpeed2);
        locNTSend.addItemDouble("LauncherSpeedActual", TurretLauncher::getLauncherActualSpeed);
        locNTSend.addItemDouble("LauncherTargetSpeed", TurretLauncher::getLauncherTargetSpeed);
        locNTSend.addItemBoolean("LauncherSpeedOnTarget", TurretLauncher::getLauncherSpeedOnTarget);



        
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // -------- read sensors
        clockwiseLimitSwitchValue = clockwiseLimitSwitch.get();
        counterclockwiseLimitSwitchValue = counterclockwiseLimitSwitch.get();

        turretAngleEncoder = TurrentRotationMotorEncoder.getPosition()*360/turretGearRatio;
        turretAngleVelRPM = (TurrentRotationMotorEncoder.getVelocity()*360/turretGearRatio)/60;


        
        
        // -------- calc stuff
        launchertargetSpeed  = 1000.0 + TurretDistance * 200.0;

        robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
        TurretOffset= TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
        robotPose = robotPose.minus(TurretOffset);  // TODO: Should this be add

        Translation2d targetPose = goalPose;

        if (turretMode){
            targetPose=zonePose;
        }
        else {
            targetPose=goalPose;
        }

        TurretDistance=robotPose.getDistance(targetPose);

        DesiredTurretAngle = (targetPose.minus(robotPose)).getAngle();

        DesiredTurretAngle = DesiredTurretAngle.minus(new Rotation2d(SwerveOdometry.getrotposition()) );
        desiredTurretAngleDegrees = MathUtil.clamp( DesiredTurretAngle.getDegrees(), -100.0, 100.0);


        //------Position Control
        TurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);

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

        // Set launcher motor demand
        double launchFeedForward = launcherFeedforward.calculate(useSpeedTarget);
        double launcherPIDOutput = launcherPID.calculate(locLauncherSpeedActual, useSpeedTarget);
        LauncherMotorDemand = MathUtil.clamp(launchFeedForward+launcherPIDOutput, -1.0, 1.0);

        //TODO: this needs to actually do something (Task #34)
        //use limit switches to stop travel
        if (clockwiseLimitSwitchValue)
        {

        }
        else if (counterclockwiseLimitSwitchValue)
        {

        }

        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand);

        if ( !locLauncherOn ) {
            LauncherMotorDemand = 0;
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
}

