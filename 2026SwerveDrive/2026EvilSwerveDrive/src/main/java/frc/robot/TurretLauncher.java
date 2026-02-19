package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150PositionControl;
import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

public class TurretLauncher {

    private double TURRETOFFSET;
    private Rotation2d RobotRotation;

    private TurretLauncher(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static SparkMax TurretRotationMotor; 
    private static RelativeEncoder TurrentRotationMotorEncoder;  // sample....
    private static Translation2d robotPose;
    private static Translation2d TurretOffset;
    private static Translation2d goalPose;
    private static double TurretDistance;
    private static Rotation2d DesiredTurretAngle;
    private static double TurretRelativeAngle;
    private static double TurretMotorDemand;
    private static Lib4150PositionControl TurretPositionControl;
    private static double turretAngleEncoder;
    private static double turretAngleVelRPM = 0.0;
    private static DigitalInput clockwiseLimitSwitch;
    private static DigitalInput counterclockwiseLimitSwitch;
    private static boolean clockwiseLimitSwitchValue = false;
    private static boolean counterclockwiseLimitSwitchValue = false;
    //private static SparkMax turretEncoder;
    private static double desiredTurretAngleDegrees;


    public static void init() {

        // TODO: Set CAN ID
        TurretRotationMotor = new SparkMax(5,MotorType.kBrushless);
        TurrentRotationMotorEncoder = TurretRotationMotor.getEncoder();

        //open motor config
        SparkMaxConfig TurretSpinConfig = new SparkMaxConfig();
        
        //motor Config
        //TODO: config values need to be changed/tuned
        TurretSpinConfig.idleMode(IdleMode.kBrake);
        TurretSpinConfig.smartCurrentLimit(50);
        TurretSpinConfig.openLoopRampRate(0.2);
       
        // TODO: open and configure sensors
        
        //set initial system state
        TurretOffset = new Translation2d(4.872 , 0);
        //TODO: get the values of the goal position based on alliance - currently using Red values
        //Blue: x: 11.92m y: 4.03m
        goalPose = new Translation2d(4.63, 4.03);
        
        //limit switches
        clockwiseLimitSwitch = new DigitalInput(0);
        counterclockwiseLimitSwitch = new DigitalInput(1);
        //encoder

        // TODO: This will not be throughbore encoder any longer.  Use built in SparkMax encoder....
        // - Looks like this is completed line 53-53?
  
        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");
        locNTSend.addItemDouble("TurretEncoderRotation", TurretLauncher::getturretAngleEncoder);
        locNTSend.addItemDouble("TurretDesiredAngle", TurretLauncher::getturretAngleTarget);
        locNTSend.addItemDouble("TurretMotorVelocityRPM", TurretLauncher::getTurretMotorRPM);
        locNTSend.addItemDouble("TurretMotorDmd", TurretLauncher::getTurretMotorDemand);

        
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // -------- read sensors
        clockwiseLimitSwitchValue = clockwiseLimitSwitch.get();
        counterclockwiseLimitSwitchValue = counterclockwiseLimitSwitch.get();
        // TODO: Convert to degrees.  Think that the default is rotations.  Also take into account gear ratio...
        turretAngleEncoder = TurrentRotationMotorEncoder.getPosition();
        turretAngleVelRPM = TurrentRotationMotorEncoder.getVelocity();

        // -------- calc stuff

        robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
        TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
        robotPose.minus(TurretOffset);

        TurretDistance=robotPose.getDistance(goalPose);

        DesiredTurretAngle = (goalPose.minus(robotPose)).getAngle();

        DesiredTurretAngle = DesiredTurretAngle.minus(new Rotation2d(SwerveOdometry.getrotposition()));
        desiredTurretAngleDegrees = DesiredTurretAngle.getDegrees();


        //------Position Control
        // TODO: Define this in init.  Not here over and over....
        TurretPositionControl = new Lib4150PositionControl(2.0, 50.0, 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);
        
        TurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);

        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand); 

        locNTSend.triggerUpdate();
    }

    private static double getTurretMotorDemand() {
        return TurretMotorDemand;
    }
        public static double getturretAngleEncoder() {
        return turretAngleEncoder;
    }

    private static double getturretAngleTarget(){
        return desiredTurretAngleDegrees;
    }

    public static double getTurretMotorRPM() {
        return turretAngleVelRPM;
    }



}

