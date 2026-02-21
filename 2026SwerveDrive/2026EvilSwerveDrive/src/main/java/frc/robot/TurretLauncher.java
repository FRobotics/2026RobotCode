package frc.robot;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.ctre.phoenix6.StatusSignal;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import Lib4150.Lib4150PositionControl;
import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

public class TurretLauncher {

    private double TURRETOFFSET;
    private Rotation2d RobotRotation;

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
    private static double TurretDistance;
    private static Rotation2d DesiredTurretAngle;
    private static double TurretRelativeAngle;
    private static double TurretMotorDemand;
    private static double LauncherMotorDemand = 0.5;
    private static Lib4150PositionControl TurretPositionControl;
    private static double turretAngleEncoder;
    private static double turretAngleVelRPM = 0.0;
    private static DigitalInput clockwiseLimitSwitch;
    private static DigitalInput counterclockwiseLimitSwitch;
    private static boolean clockwiseLimitSwitchValue = false;
    private static boolean counterclockwiseLimitSwitchValue = false;
    private static PIDController launcherPID;
    private static SimpleMotorFeedforward launcherFeedforward;
    private double locLauncherSpeedActual = 0.0;
    
        private static double Launcher_Kn = 1.0 / 3.98670783;
        //private static SparkMax turretEncoder;
        private static double desiredTurretAngleDegrees;
    
    
        public static void init() {
    
            // TODO: Set CAN ID
            TurretRotationMotor = new SparkMax(10,MotorType.kBrushless);
            TurrentRotationMotorEncoder = TurretRotationMotor.getEncoder();
            LauncherMotor = new SparkMax(11,MotorType.kBrushless);
            LauncherMotorEncoder = LauncherMotor.getEncoder();
            LauncherMotor2 = new SparkMax(12,MotorType.kBrushless);
            LauncherMotorEncoder2 = LauncherMotor2.getEncoder();
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
    
            //Speed control
            launcherFeedforward = new SimpleMotorFeedforward (0.0, Launcher_Kn, 0.0);
            launcherPID = new PIDController ( Launcher_Kn *.5, 0, 0);
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
        locNTSend.addItemDouble("LauncherMotorDmd", TurretLauncher::getLauncherMotorDemand);


        
        
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
        TurretPositionControl = new Lib4150PositionControl(Units.rotationsToDegrees(2.0),Units.rotationsToDegrees(50.0), 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);
        
        TurretMotorDemand = TurretPositionControl.PosCtrlExec(desiredTurretAngleDegrees, turretAngleEncoder);



        // --------output to actuators (motors)
        TurretRotationMotor.set(TurretMotorDemand);
        LauncherMotor.set(LauncherMotorDemand); 
        LauncherMotor2.set(LauncherMotorDemand);

        locNTSend.triggerUpdate();
    }

    public void readSensors(){
        //Reads current drive speed
        RelativeEncoder LauncherEncoder = LauncherMotor.getEncoder(); //double check unit conversion
        locLauncherSpeedActual = -Units.feetToMeters(LauncherEncoder.getVelocity());      //METERS      

    }

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

    public static double getTurretMotorRPM() {
        return turretAngleVelRPM;
    }



}

