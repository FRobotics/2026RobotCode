package frc.robot;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoFunctions {
    private AutoFunctions(){}

    public static boolean autoWait()
    {
        //set drive demand to zero. is this the right method?
        SwerveDrive.setDesiredSpeed(new ChassisSpeeds(0, 0, 0));
        return false;
    }
}
