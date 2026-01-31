package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtendedOff = false;  // false if up, true if down.
    private static boolean locIntakeExtenedOn = false; 
    private static SparkMax intakeMotor1;
    private static SparkMax intakeMotor2;
    private static DutyCycleEncoder intakeEncoder;

    
    public static void init() {

        intakeMotor1 = new SparkMax(1,MotorType.kBrushless);
        intakeMotor2 = new SparkMax(2,MotorType.kBrushless);

        SparkMaxConfig intake1Config = new SparkMaxConfig();
        SparkMaxConfig intake2Config = new SparkMaxConfig();

        //TODO: config values need changed/tuned
        intake1Config.idleMode(IdleMode.kBrake);
        intake2Config.idleMode(IdleMode.kBrake);
        intake1Config.smartCurrentLimit(50);
        intake2Config.smartCurrentLimit(20);
        intake1Config.openLoopRampRate(0.2);
        intake2Config.openLoopRampRate(0.08);
        
        // TODO: open and configure sensors
        intakeEncoder = new DutyCycleEncoder(4);
        intakeMotor1.getForwardLimitSwitch();
        // TODO: set initial system state

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();

        // TODO: This appears to be a duplicate.  Suggest removal.
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");
        // TODO: This appears to be a duplicate.  Suggest removal.
        locNTSend.addItemBoolean("IntakeIsExtendedOn", IntakeSystem::getIntakeExtended);
       
        
    }

    public static void executeLogic() {

        locNTSend.triggerUpdate();
    }

    public static boolean getIntakeExtended() {
        return locIntakeExtendedOff;
    }

}
