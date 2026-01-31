package frc.robot;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.function.BooleanSupplier;

import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.SPI;

public class SwerveDrive {
    static private swervemodule module1;
    static private swervemodule module2;
    static private swervemodule module3;
    static private swervemodule module4;
    static private ChassisSpeeds locSpeedTarget = new ChassisSpeeds();
    static private ChassisSpeeds locSpeedActual = new ChassisSpeeds();
    static public SwerveDriveKinematics publicDriveKinematics;
    static private ADIS16470_IMU swerveGyro;
    static private double gyroYawDeg = 0.0;
    static private double gyroRollDeg = 0.0;
    static private double gyroPitchDeg = 0.0;
    static private boolean gyroConnected = false;
    static private Lib4150NetTableSystemSend sender;


    private SwerveDrive (){

    }

    static public void SwerveInit(){    
        //module1 is left front, module2 is right front, module3 is left rear, module4 is right rear
        module1=new swervemodule(Units.inchesToMeters(SwerveTeleop.motorOffsetX), Units.inchesToMeters(SwerveTeleop.motorOffsetY), 20, 21, 22);
        module2=new swervemodule(Units.inchesToMeters(SwerveTeleop.motorOffsetX), -1*Units.inchesToMeters(SwerveTeleop.motorOffsetY), 30, 31, 32);
        module3=new swervemodule(-1*Units.inchesToMeters(SwerveTeleop.motorOffsetX), Units.inchesToMeters(SwerveTeleop.motorOffsetY), 40, 41, 42);
        module4=new swervemodule(-1*Units.inchesToMeters(SwerveTeleop.motorOffsetX), -1*Units.inchesToMeters(SwerveTeleop.motorOffsetY), 50, 51, 52);

        //init kinematics
        publicDriveKinematics = new SwerveDriveKinematics(
            module1.getModuleLocation(),
            module2.getModuleLocation(),
            module3.getModuleLocation(),
            module4.getModuleLocation()  );

        //init gyro
        // original values, roll=kx, yaw=ky, pitch=kz
        swerveGyro = new ADIS16470_IMU(ADIS16470_IMU.IMUAxis.kZ, ADIS16470_IMU.IMUAxis.kX, ADIS16470_IMU.IMUAxis.kY, SPI.Port.kOnboardCS0, ADIS16470_IMU.CalibrationTime._8s );

        // TODO: NO.  Dont read the gyro here.  It has to be read every execute cycle.  Move to the execute method.
        
        
        // Network Table
        sender = new Lib4150NetTableSystemSend("SwerveDrive");

        // TODO: NO Get rid of these... They are not needed.
        // DoubleSupplier gyroRoll = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kRoll);
        // DoubleSupplier gyroYaw = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kYaw);
        // DoubleSupplier gyroPitch = () -> swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kPitch);
        // BooleanSupplier gyroConnect = () -> swerveGyro.isConnected();

        // TODO: NO.  What is this?  Don't pass strings to network tables - unless there is no other choice.  Break out each part of the value.
        Supplier<String> speedTarget = () -> locSpeedTarget.toString();

        //Pitch and roll are swapped
        sender.addItemDouble("Pitch", SwerveDrive::getPitch);
        sender.addItemDouble("Roll", SwerveDrive::getRoll);
        sender.addItemDouble("Yaw", SwerveDrive::getYaw);
        sender.addItemBoolean("Connected", SwerveDrive::getGyroConnected);
        // TODO: change string to VelXDmd, VelYDmd, VelRotDmd items...
        sender.addItemString("Speed Target", speedTarget);
        // TODO: Send actual X,Y,Rotation speed.



        
    }

    static public double getPitch(){
        return gyroPitchDeg;
    }

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

    // TODO: Add getters for individual parts of TargetSpeed

    static public ChassisSpeeds getActualSpeed(){
        return locSpeedActual;
    }

    // TODO: Add getters for individual parts of ActualSpeed

    static public SwerveModulePosition[] getModulePositions(){
        SwerveModulePosition[] xxxx =  { module1.getModulePosition(), module2.getModulePosition(), module3.getModulePosition(), module4.getModulePosition()};
        return xxxx;
    }
    
    static public void SwerveExec(double systemElapsedTimeSec){

        // TODO: The gyro has to be read each cycle here....  Move the read code from init to here!!!...
        gyroRollDeg = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kYaw);
        gyroYawDeg  = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kRoll);
        gyroPitchDeg = swerveGyro.getAngle(ADIS16470_IMU.IMUAxis.kPitch);
        gyroConnected = swerveGyro.isConnected();

        //-------calc swerve module states from chassis speed
        SwerveModuleState[] desiredModStates = publicDriveKinematics.toSwerveModuleStates( locSpeedTarget);

        //------ Safeguard against going too fast
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredModStates,Units.feetToMeters( SwerveTeleop.maxLinearSpeed));

        //----- Drives each Module
        //-----must be in same order as def
        module1.ExecuteLogic( desiredModStates[0], systemElapsedTimeSec);
        module2.ExecuteLogic( desiredModStates[1], systemElapsedTimeSec);
        module3.ExecuteLogic( desiredModStates[2], systemElapsedTimeSec);
        module4.ExecuteLogic( desiredModStates[3], systemElapsedTimeSec);

        // TODO: Calculate actual speed so other systems can see this!

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
        locSpeedTarget = speeds;
    }

    
}
