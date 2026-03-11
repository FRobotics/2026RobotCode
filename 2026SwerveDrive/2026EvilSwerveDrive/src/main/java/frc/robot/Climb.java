package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.DigitalInput;

public class Climb {

    private Climb(){}

    
    private static SparkMax ClimbMotor1;
    private static RelativeEncoder ClimbEncoder1;
    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    private static boolean locClimbExtended = false;  // false if up, true if down.
    private static DigitalInput ClimbLimitSwitch;
    private static boolean ClimbLimitSwitchState;
    private static Lib4150DigEdgeOn ClimbLimitSwitchEdgeOn;
    private static double ClimbMotorDemand;
    
    private static double locClimbSetpointRPM = 0.0;
    private static double locCmdClimbSetpointRPM = 0.0;
    private static double locClimbFFOutput = 0.0;
    private static double locClimbPIDOutput = 0.0;
    private static SimpleMotorFeedforward ClimbFeedForward;
    private static PIDController ClimbPID;


    public static void init() {

        ClimbMotor1 = new SparkMax(18, MotorType.kBrushless);
        ClimbEncoder1 = ClimbMotor1.getEncoder();
        ClimbLimitSwitch = new DigitalInput(3);
        ClimbLimitSwitchState = false;
        ClimbLimitSwitchEdgeOn = new Lib4150DigEdgeOn();
        
        
        
        
        
        // TODO: open and configure sensors.
        // TODO: set initial system state
        // TODO: register command names with auto (so they can be called by name.)

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("Climb");

       
        locNTSend.addItemBoolean("ClimbIsExtended", Climb::getClimbExtended);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        if(locClimbExtended){

        }
        
        
        
        locNTSend.triggerUpdate();
    }

    
    public static boolean getClimbExtended() {
        return locClimbExtended;
    }

}

