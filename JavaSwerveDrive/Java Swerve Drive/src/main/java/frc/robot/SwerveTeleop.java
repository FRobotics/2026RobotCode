package frc.robot;


import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.XboxController;

public class SwerveTeleop {
   
    private static XboxController myXboxController;
    private static ChassisSpeeds myChassisSpeeds;
    private static double YIn=0;
    private static double XIn=0;
    private static double RotIn=0;
    
    private SwerveTeleop(){

    }
    
    public static void init() {
        myXboxController = new XboxController(0); 
        myChassisSpeeds = new ChassisSpeeds(0,0,0);
        
    }
    
    public static void SwerveExecute(){
        YIn=myXboxController.getLeftY();
        XIn=myXboxController.getLeftX();
        RotIn=myXboxController.getRightX();
        YIn=MathUtil.applyDeadband(YIn,.05);
        XIn=MathUtil.applyDeadband(XIn,.05);
        RotIn=MathUtil.applyDeadband(RotIn,.05);
    }

    public static double TeleopCalc(double input){
        
        double joyval = input;
        if ((joyval<=0.05) && (joyval >= -0.05)) {
            joyval = 0.0;
        }
        if (joyval< -1) {
            joyval = -1;
        }
        if (joyval > 1) {
            joyval = 1;
        }
        if (joyval >= 0){
            joyval=joyval*joyval;
        }
        else{
            joyval=joyval*-joyval;
        }
        return joyval;
    }



}
