package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class TurretLauncher {

    private double TURRETOFFSET;
    private Rotation2d RobotRotation;

    private TurretLauncher(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static SparkMax TurretRotationMotor; 
    private static Translation2d robotPose;
    private static Translation2d TurretOffset;
    private static Translation2d goalPose;
    private static double TurretDistance;
    private static double DesiredTurretAngle;
    private static double TurretRelativeAngle;

    public static void init() {

        
        TurretRotationMotor = new SparkMax(5,MotorType.kBrushless);
        
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
        //TODO: get the values of the goal position based on alliance
        goalPose = new Translation2d();
        
        // init network table
        locNTSend = new Lib4150NetTableSystemSend("TurretLauncher");

        
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        locNTSend.triggerUpdate();

        robotPose = new Translation2d(SwerveOdometry.getxposition(),SwerveOdometry.getyposition());
        TurretOffset.rotateBy(new Rotation2d(SwerveOdometry.getrotposition()));
        robotPose.minus(TurretOffset);

        TurretDistance=robotPose.getDistance(goalPose);

        DesiredTurretAngle = Math.tan();

        





    }



}

