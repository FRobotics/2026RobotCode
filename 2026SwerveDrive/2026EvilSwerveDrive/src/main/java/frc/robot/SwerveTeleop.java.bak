package frc.robot;



import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

public class SwerveTeleop {
   
    private static XboxController myXboxController;
    private static XboxController myXboxController2;
    private static ChassisSpeeds myChassisSpeeds;
    private static double YIn=0.0;
    private static double XIn=0.0;
    private static double RotIn=0.0;
    
    // true is field orient false is robot orient
    private static boolean orient = true; //true is field false is robot oriented
    private static double exeTime=0.0;
    private static double endTime=0.0;
    private static double lastStartTime=0.0;
    private static double periodicTime=0.0;
    private static double startTime=0.0;

    private static Lib4150NetTableSystemSend locNTsend;

    private SwerveTeleop(){

    }
    
    public static void init() {
        myXboxController = new XboxController(0);
        myXboxController2 = new XboxController(1);
        myChassisSpeeds = new ChassisSpeeds(0,0,0);

        // TODO: add driver button box - if you want one
        // TODO: add aux button box - if you want one.

        locNTsend = new Lib4150NetTableSystemSend("Teleop");
        locNTsend.addItemDouble("DriveSpeedTarg_X", SwerveTeleop::getDriveSpeedTargX);
        locNTsend.addItemDouble("DriveSpeedTarg_Y", SwerveTeleop::getDriveSpeedTargY);
        locNTsend.addItemDouble("DriveSpeedTarg_Rot", SwerveTeleop::getDriveSpeedTargRot);
        locNTsend.addItemBoolean("FieldOriented", SwerveTeleop::getFieldOrientedDriving); 
        locNTsend.triggerUpdate();
        
    }
    
    public static void SwerveExecute(){
        
        //start time --- only for diagnostices !!!
        startTime = Timer.getFPGATimestamp();
        periodicTime = (startTime - lastStartTime)*1000;
        lastStartTime = startTime;

        // --------read joysticks
        YIn=myXboxController.getLeftX();
        XIn=myXboxController.getLeftY();  //reverses the x and y
        RotIn=myXboxController.getRightX();
        if (myXboxController.getLeftStickButtonPressed()){
            orient= !orient;
        }

        // --------apply deadband to joystick....
        YIn=MathUtil.applyDeadband(YIn,.05);
        XIn=MathUtil.applyDeadband(XIn,.05);
        RotIn=MathUtil.applyDeadband(RotIn,.05);

        //---------Square Values at 1, 0, and -1
        YIn = YIn * YIn * Math.signum(YIn);
        XIn = XIn * XIn * Math.signum(XIn);
        RotIn = RotIn * RotIn * Math.signum(RotIn);

        //--------Changes values to Ft and degrees
        double YInFT = YIn * SwerveDrive.maxLinearSpeed;
        double XInFT = XIn * SwerveDrive.maxLinearSpeed;
        double RotInDeg = RotIn * SwerveDrive.maxRotSpeed;

        //------Converts the X, Y, and Rotation values in new units
        //switcheed X and Y to negative
        myChassisSpeeds.vxMetersPerSecond = Units.feetToMeters(-XInFT);
        myChassisSpeeds.vyMetersPerSecond = Units.feetToMeters(-YInFT);
        myChassisSpeeds.omegaRadiansPerSecond = Units.degreesToRadians(-RotInDeg);

        if (orient) {
            myChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(myChassisSpeeds, new Rotation2d(SwerveOdometry.getrotposition()));
        }

        //-----tell drive system our desired speed
        SwerveDrive.setDesiredSpeed(myChassisSpeeds);

        //------- Second controller buttons
        if (myXboxController2.getAButtonPressed()){
            SupervisoryCmds.Collecting();
        }
        if (myXboxController2.getBButtonPressed()){
            SupervisoryCmds.StopAction();
        }
        if(myXboxController2.getLeftBumperButtonPressed()){
            SupervisoryCmds.Shooting();
        }
        if (myXboxController2.getRightBumperButtonPressed()){
            SupervisoryCmds.BallsToAlliance();
        }
        if (myXboxController2.getYButtonPressed()){
            SupervisoryCmds.Descend();
        }
        if (myXboxController2.getXButtonPressed()){
            SupervisoryCmds.Climb();
        }
        if (myXboxController2.getRightStickButtonPressed()){
            SupervisoryCmds.Defense();
        }
        


      
        endTime = Timer.getFPGATimestamp();
        exeTime = (endTime-startTime)*1000;

        locNTsend.triggerUpdate();
    }

    public static boolean getFieldOrientedDriving(){
        return(orient);
    }

    public static double getTeleopElapsedTimeMilli(){
        return(exeTime);
    }

    public static double getTeleopPeriodicTimeMilli(){
        return(periodicTime);
    }

    public static double getDriveSpeedTargX() {
        return myChassisSpeeds.vxMetersPerSecond; 
    }
    public static double getDriveSpeedTargY() {
        return myChassisSpeeds.vyMetersPerSecond; 
    }
    public static double getDriveSpeedTargRot() {
        return myChassisSpeeds.omegaRadiansPerSecond; 
    }

    

}
