package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Angle;
//import edu.wpi.first.wpilibj.CAN;
import Lib4150.Lib4150PositionControl;
import edu.wpi.first.math.geometry.Rotation2d;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
//import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import com.ctre.phoenix6.hardware.CANcoder;
//import com.ctre.phoenix6.swerve.jni.SwerveJNI.ModuleState;
//import com.ctre.phoenix6.signals.SensorDirectionValue;
import com.ctre.phoenix6.StatusSignal;
//import com.ctre.phoenix6.configs.CANcoderConfiguration;
//import com.ctre.phoenix6.configs.CANcoderConfigurator;
//import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;

public class swervemodule {


    // max motor output = 1.0
    // max speed -- for previous robots was 13.079750113990022243423043851432 feet/sec
    // Kn (normalization constant) = max motor out / max speed = 0.07645406
    // when doing everything in meters then max speed is 3.98670783
    private static double Drive_Kn = 1.0 / 3.98670783;

    private double xOff = 0.0;
    private double yOff = 0.0;
    private int driveid = 0;
    private int spinid = 0;
    private int spinEnc = 0;
    private Translation2d moduleoffset;
    //private double spinPosRad = 0.0;
    //private double driveSpeedMPS = 0.0;
    private SparkMax driveMotor;
    private SparkMax spinMotor;
    private Lib4150PositionControl spinPositionControl;
    private PIDController drivePID;
    private SimpleMotorFeedforward drivFeedforward;
     
    private CANcoder spinAbsEncoder;
    private double locSpeedActual = 0.0;
    private double locDistanceActual = 0.0;
    private double actualSpinAngleRad = 0.0;
    private SwerveModulePosition modulePosition = new SwerveModulePosition();
    private SwerveModuleState moduleState = new SwerveModuleState();

    public swervemodule(double paraXoffset, double paraYoffset, int paradriveid, int paraspinid, int paraspinEnc) {

        xOff = paraXoffset;
        yOff = paraYoffset;
        driveid = paradriveid;
        spinid = paraspinid;
        spinEnc = paraspinEnc;
        

        moduleoffset = new Translation2d(xOff, yOff);
        driveMotor = new SparkMax(driveid,MotorType.kBrushless);
        spinMotor = new SparkMax(spinid,MotorType.kBrushless);
        

        SparkMaxConfig driveConfig = new SparkMaxConfig();
        SparkMaxConfig spinConfig = new SparkMaxConfig();

        driveConfig.idleMode(IdleMode.kBrake);
        spinConfig.idleMode(IdleMode.kBrake);
        driveConfig.smartCurrentLimit(50);
        spinConfig.smartCurrentLimit(20);
        driveConfig.openLoopRampRate(0.2);
        spinConfig.openLoopRampRate(0.08);

        //position and velocity conversion
        //convert from rotations to feet
        //3.705 in diameter * pi / 12 in to feet / 6.75 gear ratio
        final double driveDistFactor = 0.143699;
        driveConfig.encoder.positionConversionFactor(driveDistFactor);
        driveConfig.encoder.velocityConversionFactor(driveDistFactor/60.0);

        final double spinDistFactor = 1.0/360.0;
        spinConfig.encoder.positionConversionFactor(spinDistFactor);
        spinConfig.encoder.velocityConversionFactor(spinDistFactor/60.0);

        driveMotor.configure(driveConfig, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
        spinMotor.configure(spinConfig, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
         
        spinAbsEncoder = new CANcoder(spinEnc);

        /* 
        CANcoderConfiguration absEncoderConfig = new CANcoderConfiguration();
        absEncoderConfig.MagnetSensor.SensorDirection = SensorDirectionValue.CounterClockwise_Positive;
        absEncoderConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint = 0.5;
        absEncoderConfig.MagnetSensor.MagnetOffset = Units.degreesToRotations(absEncoderOffset);
        
        CANcoderConfigurator spinConfigurator = spinAbsEncoder.getConfigurator();
        MagnetSensorConfigs spinMagConfig = new MagnetSensorConfigs();
        spinMagConfig.SensorDirection = SensorDirectionValue.CounterClockwise_Positive;
        spinMagConfig.AbsoluteSensorDiscontinuityPoint = 0.5;
        spinMagConfig.MagnetOffset = Units.degreesToRotations(absEncoderOffset);
        */
        // getting config but not applying it
        
        
        // (Error Deadband,error threshhold )
        spinPositionControl = new Lib4150PositionControl(Units.degreesToRadians(2.0), Units.degreesToRadians(50.0), 0.005, 0.35, 0.8, 1.0e-5, false, true);

        // original was in feet .... change to meters...

        
        // drivFeedforward = new SimpleMotorFeedforward (0.0, 0.07645406, 0.0);
        drivFeedforward = new SimpleMotorFeedforward (0.0, Drive_Kn, 0.0);
        //drivePID = new PIDController (0.07645406*1.0, 0.07645406*0.5, 1.0e-7*0.7645406);
        // drivePID = new PIDController (0.07645406*.5, 0, 0);
        drivePID = new PIDController ( Drive_Kn *.5, 0, 0);
        
    }

    public Translation2d getModuleLocation() {
        return moduleoffset;
    }
        
    public SwerveModulePosition getModulePosition(){
        return modulePosition;
    }
        
    public void ExecuteLogic( SwerveModuleState parmModState, double systemElapsedTime ) {
        
        this.readSensors();

        double currentAngle = actualSpinAngleRad;
        parmModState.optimize(new Rotation2d(currentAngle));
        
        // control the spin motor
        double targetAngle = parmModState.angle.getRadians();

        spinMotor.set(spinPositionControl.PosCtrlExec(targetAngle, currentAngle));
        
        // control the drive motor
        double TargetSpeed = parmModState.speedMetersPerSecond;
        double ActualSpeed = locSpeedActual;
        double feedForward = drivFeedforward.calculate(TargetSpeed);
        double PIDoutput = drivePID.calculate(ActualSpeed, TargetSpeed);
        double MotorDemand = -MathUtil.clamp(feedForward+PIDoutput, -1.0, 1.0);
        driveMotor.set(MotorDemand);
        
        

    }

    public SwerveModulePosition getSwerveModulePosition(){
        return modulePosition;
    }
    
    public SwerveModuleState getSwerveModuleState(){
        return moduleState;
    }

    public void readSensors(){
        //Reads current drive speed
        RelativeEncoder driveEncoder = driveMotor.getEncoder();
        locDistanceActual = -Units.feetToMeters(driveEncoder.getPosition()); //double check unit conversion
        locSpeedActual = -Units.feetToMeters(driveEncoder.getVelocity());      //METERS
        //Reads absolute encoder
        StatusSignal<Angle> absolutepositioSignal = spinAbsEncoder.getAbsolutePosition();
        actualSpinAngleRad = MathUtil.angleModulus(Units.rotationsToRadians(absolutepositioSignal.getValueAsDouble()));

        //---------------
        modulePosition.angle = Rotation2d.fromRadians(actualSpinAngleRad);
        modulePosition.distanceMeters = locDistanceActual;

        moduleState.speedMetersPerSecond = locSpeedActual;
        moduleState.angle = Rotation2d.fromRadians(actualSpinAngleRad);        

    }

    


}
