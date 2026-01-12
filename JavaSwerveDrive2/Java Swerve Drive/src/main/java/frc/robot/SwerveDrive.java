package frc.robot;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;

public class SwerveDrive {
    static private swervemodule module1;
    static private swervemodule module2;
    static private swervemodule module3;
    static private swervemodule module4;
    static private ChassisSpeeds locSpeedTarget = new ChassisSpeeds();
    static private ChassisSpeeds locSpeedActual = new ChassisSpeeds();
    static public SwerveDriveKinematics publicDriveKinematics;

    private SwerveDrive (){

    }
    static public void SwerveInit(){    
        //module1 is left front, module2 is right front, module3 is left rear, module4 is right rear
        module1=new swervemodule(21, 22, Units.inchesToMeters(SwerveTeleop.motorOffsetX), Units.inchesToMeters(SwerveTeleop.motorOffsetY), 20, 21, 22, 0.0);
        module2=new swervemodule(23, 24, Units.inchesToMeters(SwerveTeleop.motorOffsetX), -1*Units.inchesToMeters(SwerveTeleop.motorOffsetY), 30, 31, 32, 0.0);
        module3=new swervemodule(25, 26, -1*Units.inchesToMeters(SwerveTeleop.motorOffsetX), Units.inchesToMeters(SwerveTeleop.motorOffsetY), 40, 41, 42, 0.0);
        module4=new swervemodule(27, 28, -1*Units.inchesToMeters(SwerveTeleop.motorOffsetX), -1*Units.inchesToMeters(SwerveTeleop.motorOffsetY), 50, 51, 52, 0.0);

        //init kinematics
        publicDriveKinematics = new SwerveDriveKinematics(
            module1.getModuleLocation(),
            module2.getModuleLocation(),
            module3.getModuleLocation(),
            module4.getModuleLocation()  );

        
    }
    static public void SwerveExec(double timeValue){
        //-------calc swerve module states from chassis speed
        SwerveModuleState[] desiredModStates = publicDriveKinematics.toSwerveModuleStates( locSpeedTarget);

        //------ Safeguard against going too fast
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredModStates,Units.feetToMeters( SwerveTeleop.maxLinearSpeed));

        //----- Drives each Module
        //-----must be in same order as def
        module1.ExecuteLogic( desiredModStates[0], timeValue);
        module2.ExecuteLogic( desiredModStates[1], timeValue);
        module3.ExecuteLogic( desiredModStates[2], timeValue);
        module4.ExecuteLogic( desiredModStates[3], timeValue);


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
