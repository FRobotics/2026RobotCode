package frc.robot;



import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.math.MathUtil;
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
    //measured in feet
   // private static final double motorOffsetTotal=(Math.sqrt(Math.pow(motorOffsetX, 2) + Math.pow(motorOffsetY, 2)))/12;
    //private static final double circumfrence=2*Math.PI*motorOffsetTotal;
    //private static final double realRotSpeed = maxLinearSpeed/circumfrence * 360;
    // true is field orient false is robot orient
    private static boolean orient;
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

        // TODO: add second controller.
        // TODO: add driver button box - if you want one
        // TODO: add aux button box - if you want one.

        locNTsend = new Lib4150NetTableSystemSend("Teleop");
        locNTsend.addItemDouble("DriveSpeedTarg_X", SwerveTeleop::getDriveSpeedTargX);
        locNTsend.addItemDouble("DriveSpeedTarg_Y", SwerveTeleop::getDriveSpeedTargY);
        locNTsend.addItemDouble("DriveSpeedTarg_Rot", SwerveTeleop::getDriveSpeedTargRot);
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
        //-----tell drive system our desired speed
        SwerveDrive.setDesiredSpeed(myChassisSpeeds);

      
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

    /*   public static double TeleopCalc(double input) {

        double joyval = input;
        if ((joyval <= 0.05) && (joyval >= -0.05)) {
            joyval = 0.0;
        }
        if (joyval < -1) {
            joyval = -1;
        }
        if (joyval > 1) {
            joyval = 1;
        }
        if (joyval >= 0) {
            joyval = joyval * joyval;
        } else {
            joyval = joyval * -joyval;
        }
        return joyval;
    }
*/

}
