package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Climb {

    private Climb(){}

    
    private static SparkMax ClimbMotor1;
    private static SparkMax ClimbMotor2;
    private static RelativeEncoder ClimbEncoder1;
    private static RelativeEncoder climbEncoder2;

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    private static boolean locClimbExtended = false;  // false if up, true if down.

    public static void init() {

        ClimbMotor1 = new SparkMax(0, MotorType.kBrushless);
        ClimbMotor2 = new SparkMax(1, MotorType.kBrushless);
        ClimbEncoder1 = ClimbMotor1.getEncoder();
        climbEncoder2 = ClimbMotor2.getEncoder();
        
        // TODO: set initial system state
        // TODO: register command names with auto (so they can be called by name.)

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("Climb");

       
        locNTSend.addItemBoolean("ClimbIsExtended", Climb::getClimbExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        locNTSend.triggerUpdate();
    }

    
    public static boolean getClimbExtended() {
        return locClimbExtended;
    }

}
