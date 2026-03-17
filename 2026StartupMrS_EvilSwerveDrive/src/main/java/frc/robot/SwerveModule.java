package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

import Lib4150.Lib4150PositionControl;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;


import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.StatusSignal;
import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;

/**
 * Class used to control one swerve module
 */
public class SwerveModule {


    // --------class constants

    // SparkFlex motor output = 1.0
    // SparkFlex speed -- for previous robots was 13.079750113990022243423043851432 feet/sec
    // Kn (normalization constant) = SparkFlex motor out / SparkFlex speed = 0.07645406
    // when doing everything in meters then SparkFlex speed is 3.98670783

    //Now 15 ft/sec -> 4.572 m/s
    // --------maximum velocity m/sec
    private static final double Drive_MaxV = 4.572;
    // --------normalization constant max_motor_out / max_velocity
    private static final double Drive_Kn = 1.0 / Drive_MaxV;
    // --------feedforward static constant - minimum output
    private static final double Drive_Ks = 0.0;
    // --------feedforward velocity constant 
    private static final double Drive_Kv = ( 1.0 - Drive_Ks) / Drive_MaxV;
    // --------feedforward acceleration constant ( lead function, or kicker circuit )
    private static final double Drive_Ka = 0.0;
    // --------PID proportional constant
    private static final double Drive_Kp = Drive_Kn * 0.5;
    // --------PID integral constant
    private static final double Drive_Ki =  Drive_Kn * 2.5;
    // --------PID derivative constant
    private static final double Drive_Kd =  Drive_Kn * 1.0e-5;
    // --------Izone - maximum error magnitude to allow use of integral 
    private static final double Drive_Izone = 0.4;  // m/sec
    // --------Irange - allowed range of integral output
    private static final double Drive_Irange = 0.25;   // motor output units.


    // --------class variables
    // --------sensors and actuators
    private SparkFlex driveMotor;
    private SparkFlex spinMotor;
    private CANcoder spinAbsEncoder;
    // --------controllers
    private Lib4150PositionControl spinPositionControl;
    private SimpleMotorFeedforward drivFeedforward;
    private PIDController drivePID;
    // --------agregate data
    private Translation2d moduleoffset;
    private SwerveModulePosition modulePosition = new SwerveModulePosition();
    private SwerveModuleState moduleState = new SwerveModuleState();
    // --------individual data
    private double xOff = 0.0;      // meters
    private double yOff = 0.0;      // meters
    private int driveid = 0;        // can id
    private int spinid = 0;         // can id
    private int spinEnc = 0;        // can id
    // --------drive motor values
    private double locSpeedActual = 0.0;        
    private double locDistanceActual = 0.0;     
    private double locDriveSpeedDemand = 0.0;   
    private double locDriveMotorOutput = 0.0;  
    // --------spin motor values
    private double actualSpinAngleRad = 0.0;
    private double locDesiredSpinAngleRad = 0.0;
    private double locSpinMotorOutput = 0.0;

    /**
     * SwerveModule constructor 
     * 
     * @param paraXoffset - double - X offset from chassis speed location - Meters
     * @param paraYoffset - double - Y offset from chassis speed location - Meters
     * @param paradriveid - int - CAN id of drive motor - Spark Flex
     * @param paraspinid - int - CAN id of spin motor - Spark Flex
     * @param paraspinEnc - int - CAN id of absolute encoder -  CTRE CanCoder
     */
    public SwerveModule(double paraXoffset, double paraYoffset, int paradriveid, int paraspinid, int paraspinEnc) {

        xOff = paraXoffset;
        yOff = paraYoffset;
        driveid = paradriveid;
        spinid = paraspinid;
        spinEnc = paraspinEnc;

        moduleoffset = new Translation2d(xOff, yOff);
        driveMotor = new SparkFlex(driveid,MotorType.kBrushless);
        spinMotor = new SparkFlex(spinid,MotorType.kBrushless);

        SparkFlexConfig driveConfig = new SparkFlexConfig();
        SparkFlexConfig spinConfig = new SparkFlexConfig();

        driveConfig.idleMode(IdleMode.kBrake);
        spinConfig.idleMode(IdleMode.kBrake);
        driveConfig.smartCurrentLimit(50);
        spinConfig.smartCurrentLimit(20);
        driveConfig.openLoopRampRate(0.2);
        spinConfig.openLoopRampRate(0.08);

        //position and velocity conversion
        //convert from rotations to feet
        //4.01019527 in diameter * pi / 12 in to feet / 6.75 gear ratio
        final double driveDistFactor = 0.155535671604;
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
        // --------was 2 deg, see if we can do 1.   min output was 0.005
        spinPositionControl = new Lib4150PositionControl(Units.degreesToRadians(1.0), Units.degreesToRadians(50.0), 
                                    0.003, 0.35, 0.8, 1.0e-5, false, true);

        // original was in feet .... change to meters...
        
        // drivFeedforward = new SimpleMotorFeedforward (0.0, 0.07645406, 0.0);
        drivFeedforward = new SimpleMotorFeedforward (Drive_Ks, Drive_Kv, Drive_Ka);
        //drivePID = new PIDController (0.07645406*1.0, 0.07645406*0.5, 1.0e-7*0.7645406);
        // drivePID = new PIDController (0.07645406*.5, 0, 0);
        drivePID = new PIDController ( Drive_Kp, Drive_Ki,Drive_Kd);
        drivePID.setIZone(Drive_Izone);
        drivePID.setIntegratorRange(-Drive_Irange, Drive_Irange);

        return;
    }

    /**
     * Perform data acquisition and control for this swerve module.
     * 
     * @param parmModState - SwerveModuleState - desired module state - spin angle, drive speed.
     * @param systemElapsedTime - double - current system elapsed time - seconds.
     */        
    public void ExecuteLogic( SwerveModuleState parmModState, double systemElapsedTime ) {
        
        // --------get sensor data
        this.readSensors();

        double currentAngle = actualSpinAngleRad;
        parmModState.optimize(new Rotation2d(currentAngle));
        
        // ---------------------------------
        // control the spin motor
        // double targetAngle
        locDesiredSpinAngleRad = parmModState.angle.getRadians();

        locSpinMotorOutput = spinPositionControl.PosCtrlExec(locDesiredSpinAngleRad, currentAngle);
        spinMotor.set(locSpinMotorOutput);

        double driveSpinErrorCompensation = MathUtil.clamp(
                                                Math.abs(
                                                (new Rotation2d(locDesiredSpinAngleRad)).minus(new Rotation2d(currentAngle)).getCos()), 
                                                0.0, 1.0);
        
        // control the drive motor
        locDriveSpeedDemand = parmModState.speedMetersPerSecond * driveSpinErrorCompensation;
        double feedForward = drivFeedforward.calculate(locDriveSpeedDemand);
        double PIDoutput = drivePID.calculate(locSpeedActual, locDriveSpeedDemand);
        locDriveMotorOutput = -MathUtil.clamp( feedForward + PIDoutput, -1.0, 1.0);
        driveMotor.set(locDriveMotorOutput);
              
        return;
    }

    /**
     * Read sensors.  Store in local class variables.
     */
    private void readSensors(){
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
        
        return;
    }

    // ============COMMANDS

    // ---------none -- desired values are passed to the execute logic function.

    // ============GETTERS
    
    /**
     * Get Module Position
     * 
     * @return modulePosition - SwerveModulePosition - Current position of module (distance,angle)
     */
    public SwerveModulePosition getModulePosition(){
        return modulePosition;
    }

    /**
     * Get current module state
     * 
     * @return moduleState - SwerveModuleState - Current module state (speed,angle)
     */
    public SwerveModuleState getSwerveModuleState(){
        return moduleState;
    }

    /**
     * Get module location relative to location of chassis speeds (usually center of robot)
     * 
     * @return moduleOffset - Translation2d -- offset of module from location of chassis speed.
     */
    public Translation2d getModuleLocation() {
        return moduleoffset;
    }

    /**
     * Get total drive distance - meters
     * 
     * @return driveDistance - double - total drive distance - meters
     */
    public double getDriveDistance() {
        return locDistanceActual;
    }

    /**
     * Get current drive speed - m/sec
     * 
     * @return driveSpeed - double - current drive speed - meters/sec
     */
    public double getDriveSpeed() {
        return locSpeedActual;
    }

    /**
     * Get current drive speed demand - m/sec
     * 
     * @return driveSpeedDmd - double - desired drive speed - meters/sec
     */
    public double getDriveSpeedDmd() {
        return locDriveSpeedDemand;
    }

    /**
     * Current drive motor output - +/- 1.0
     * 
     * @return driveMotorOutput - double - Current drive motor output - +/- 1.0
     */
    public double getDriveMotorOutput() {
        return locDriveMotorOutput;
    }
    
    /**
     * Get current spin position - degrees
     * 
     * @return - spinPos - double - current spin position - degrees
     */
    public double getSpinPosDeg() {
        return Units.radiansToDegrees( actualSpinAngleRad );
    }

    /**
     * get current optimized desired spin position - degrees
     * 
     * @return desiredSpinPos - double - optimized desired spin position - degrees
     */
    public double getSpinPosDmdDeg() {
        return Units.radiansToDegrees( locDesiredSpinAngleRad);
    }

     /**
     * Current spin motor output - +/- 1.0
     * 
     * @return spinMotorOutput - double - Current spin motor output - +/- 1.0
     */
    public double getSpinMotorOutput() {
        return locSpinMotorOutput;
    }
}