package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.SPI;

public class SwerveDrive {

    //measured in inches
    private static final double motorOffsetX=9.875; 
    private static final double motorOffsetY=13.375;

    public static final double maxLinearSpeed = 17.0; // measured in fps - was 15
    public static final double maxRotSpeed = 360.0; //measured in degrees

    static private SwerveModule module1;    // front left
    static private SwerveModule module2;    // front right
    static private SwerveModule module3;    // rear left
    static private SwerveModule module4;    // rear right
    static private ChassisSpeeds locSpeedTarget = new ChassisSpeeds();
    static private ChassisSpeeds locSpeedActual = new ChassisSpeeds();
    static public SwerveDriveKinematics publicDriveKinematics;
    static private ADIS16470_IMU swerveGyro;
    static private double gyroYawDeg = 0.0;
    static private double gyroRollDeg = 0.0;
    static private double gyroPitchDeg = 0.0;
    static private boolean gyroConnected = false;
    static private Lib4150NetTableSystemSend sender;
    //multiplier for auto turning
    static private double Kg = 1.0;
    


    private SwerveDrive (){

    }

    static public void SwerveInit(){    
        //module1 is left front, module2 is right front, module3 is left rear, module4 is right rear
        module1=new SwerveModule(Units.inchesToMeters(motorOffsetX), Units.inchesToMeters(motorOffsetY), 20, 21, 22);
        module2=new SwerveModule(Units.inchesToMeters(motorOffsetX), -1*Units.inchesToMeters(motorOffsetY), 30, 31, 32);
        module3=new SwerveModule(-1*Units.inchesToMeters(motorOffsetX), Units.inchesToMeters(motorOffsetY), 40, 41, 42);
        module4=new SwerveModule(-1*Units.inchesToMeters(motorOffsetX), -1*Units.inchesToMeters(motorOffsetY), 50, 51, 52);

        //init kinematics
        publicDriveKinematics = new SwerveDriveKinematics(
            module1.getModuleLocation(),
            module2.getModuleLocation(),
            module3.getModuleLocation(),
            module4.getModuleLocation()  );

        //init gyro
        // original values, roll=kx, yaw=ky, pitch=kz
        swerveGyro = new ADIS16470_IMU(ADIS16470_IMU.IMUAxis.kZ, ADIS16470_IMU.IMUAxis.kX, ADIS16470_IMU.IMUAxis.kY, SPI.Port.kOnboardCS0, ADIS16470_IMU.CalibrationTime._8s );
        // tell everyone we are calibrated
        MatchSystem.setRobotPhaseGyroCalibrated();
        
        // Network Table
        sender = new Lib4150NetTableSystemSend("SwerveDrive");

        // DoubleSupplier gyroRoll = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kRoll);
        // DoubleSupplier gyroYaw = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kYaw);
        // DoubleSupplier gyroPitch = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kPitch);
        // BooleanSupplier gyroConnect = () -> swerveGyro.isConnected();

        //speed target stuff
        sender.addItemDouble("vxTarget", SwerveDrive::getTargetVX);
        sender.addItemDouble("vyTarget", SwerveDrive::getTargetVY);
        sender.addItemDouble("omegaTarget", SwerveDrive::getTargetOmega);

        //Pitch and roll are swapped
        sender.addItemDouble("GyroPitch", SwerveDrive::getPitch);
        sender.addItemDouble("GyroRoll", SwerveDrive::getRoll);
        sender.addItemDouble("GyroYaw", SwerveDrive::getYaw);
        sender.addItemBoolean("GyroConnected", SwerveDrive::getGyroConnected);

        //Send actual X,Y,Rotation speed.
        sender.addItemDouble("vxActual", SwerveDrive::getActualVX);
        sender.addItemDouble("vyActual", SwerveDrive::getActualVY);
        sender.addItemDouble("omegaActual", SwerveDrive::getActualOmega);

        // --------module 1
        sender.addItemDouble( "1/driveSpeedDmd", SwerveDrive.module1::getDriveSpeedDmd);
        sender.addItemDouble( "1/driveSpeed", SwerveDrive.module1::getDriveSpeed);
        sender.addItemDouble( "1/driveMotorOut", SwerveDrive.module1::getDriveMotorOutput);
        sender.addItemDouble( "1/spinPosDmdDeg", SwerveDrive.module1::getSpinPosDmdDeg);
        sender.addItemDouble( "1/spinPosDeg", SwerveDrive.module1::getSpinPosDeg);
        sender.addItemDouble( "1/spinMotorOut", SwerveDrive.module1::getSpinMotorOutput);

        // --------module 2
        sender.addItemDouble( "2/driveSpeedDmd", SwerveDrive.module2::getDriveSpeedDmd);
        sender.addItemDouble( "2/driveSpeed", SwerveDrive.module2::getDriveSpeed);
        sender.addItemDouble( "2/driveMotorOut", SwerveDrive.module2::getDriveMotorOutput);
        sender.addItemDouble( "2/spinPosDmdDeg", SwerveDrive.module2::getSpinPosDmdDeg);
        sender.addItemDouble( "2/spinPosDeg", SwerveDrive.module2::getSpinPosDeg);
        sender.addItemDouble( "2/spinMotorOut", SwerveDrive.module2::getSpinMotorOutput);

        // --------module 3
        sender.addItemDouble( "3/driveSpeedDmd", SwerveDrive.module3::getDriveSpeedDmd);
        sender.addItemDouble( "3/driveSpeed", SwerveDrive.module3::getDriveSpeed);
        sender.addItemDouble( "3/driveMotorOut", SwerveDrive.module3::getDriveMotorOutput);
        sender.addItemDouble( "3/spinPosDmdDeg", SwerveDrive.module3::getSpinPosDmdDeg);
        sender.addItemDouble( "3/spinPosDeg", SwerveDrive.module3::getSpinPosDeg);
        sender.addItemDouble( "3/spinMotorOut", SwerveDrive.module3::getSpinMotorOutput);

        // --------module 4
        sender.addItemDouble( "4/driveSpeedDmd", SwerveDrive.module4::getDriveSpeedDmd);
        sender.addItemDouble( "4/driveSpeed", SwerveDrive.module4::getDriveSpeed);
        sender.addItemDouble( "4/driveMotorOut", SwerveDrive.module4::getDriveMotorOutput);
        sender.addItemDouble( "4/spinPosDmdDeg", SwerveDrive.module4::getSpinPosDmdDeg);
        sender.addItemDouble( "4/spinPosDeg", SwerveDrive.module4::getSpinPosDeg);
        sender.addItemDouble( "4/spinMotorOut", SwerveDrive.module4::getSpinMotorOutput);

        
    }

    static public double getPitch(){
        return gyroPitchDeg;
    }

    /**
     *  get current gyro yaw value in degrees
     * 
     * @return YawDeg - double - Yaw value in degrees.
     */
    static public double getYaw(){
        return gyroYawDeg;
    }

    static public double getRoll(){
        return gyroRollDeg;
    }

    static public boolean getGyroConnected() {
        return gyroConnected;
    }

    static public ChassisSpeeds getTargetSpeed(){
        return locSpeedTarget;
    }
    //individual parts of target speed
    static public double getTargetVX() {
        return locSpeedTarget.vxMetersPerSecond;
    }
    static public double getTargetVY() {
        return locSpeedTarget.vyMetersPerSecond;
    }
    static public double getTargetOmega() {
        return locSpeedTarget.omegaRadiansPerSecond;
    }

    
    static public ChassisSpeeds getActualSpeed(){
        return locSpeedActual;
    }
    //individual parts of actual speed
    static public double getActualVX() {
        return locSpeedActual.vxMetersPerSecond;
    }
    static public double getActualVY() {
        return locSpeedActual.vyMetersPerSecond;
    }
    static public double getActualOmega() {
        return locSpeedActual.omegaRadiansPerSecond;
    }
    

    static public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] xxxx =  { module1.getModulePosition(), module2.getModulePosition(), module3.getModulePosition(), module4.getModulePosition()};
        return xxxx;
    }
    
    static public void SwerveExec(double systemElapsedTimeSec){

        // --------real robot has roboRIO flat...  Pitch and Roll might be reversed...
        gyroRollDeg = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kRoll);
        gyroPitchDeg = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kPitch);
        gyroYawDeg  = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kYaw);
        gyroConnected = swerveGyro.isConnected();

        //-------calc swerve module states from chassis speed
        SwerveModuleState[] desiredModStates = publicDriveKinematics.toSwerveModuleStates( locSpeedTarget);

        //------ Safeguard against going too fast
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredModStates,Units.feetToMeters( maxLinearSpeed));

        //----- Drives each Module
        //-----must be in same order as def
        module1.ExecuteLogic( desiredModStates[0], systemElapsedTimeSec);
        module2.ExecuteLogic( desiredModStates[1], systemElapsedTimeSec);
        module3.ExecuteLogic( desiredModStates[2], systemElapsedTimeSec);
        module4.ExecuteLogic( desiredModStates[3], systemElapsedTimeSec);

        locSpeedActual = publicDriveKinematics.toChassisSpeeds( module1.getSwerveModuleState(), module2.getSwerveModuleState(), 
                                                                module3.getSwerveModuleState(), module4.getSwerveModuleState() );

        //update network tables
        sender.triggerUpdate();

    }
    
    /**
     * Sets locSpeedTarget to speeds
     * 
     * @param speeds
     */
    static public void setDesiredSpeed(ChassisSpeeds speeds)
    {
        if(TurretLauncher.getTurretManualMode()){
            locSpeedTarget = new ChassisSpeeds(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, Kg * TurretLauncher.getTurretMotorDemand());
        }else{
            locSpeedTarget = speeds;
        }
        
    }

    
}
