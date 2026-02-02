package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

// TODO: suggest removing unused imports
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class FeederSystem {

    private FeederSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want feeder to be on.  FALSE = we want feeder to be off
    private static boolean locFeederOn = false; 
    private static SparkMax FeederMotor;
    private static double FeederOutput = 0;

    public static void init() {


        // init network table
        // TODO: Set feeder motor CAN ID
        FeederMotor = new SparkMax(68,MotorType.kBrushless);

        // start with feeder off
        cmdFeederOff();

        locNTSend = new Lib4150NetTableSystemSend("FeederSystem");

        locNTSend.addItemBoolean("FeederState", FeederSystem::getFeederState);
        locNTSend.addItemDouble("FeederOutput", FeederSystem::getMotorOutput);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        // if on, output 0.2
        // if off, output 0
        if (locFeederOn){
            FeederOutput=0.2;
        }
        else {
            FeederOutput=0;
        };

        FeederMotor.set(FeederOutput);

        locNTSend.triggerUpdate();
    }

    public static boolean getFeederState() {
        return locFeederOn;
    }

    public static void cmdFeederOn() {
        locFeederOn=true;
    }

    public static void cmdFeederOff() {
        locFeederOn=false;
    }

    public static double getMotorOutput() {
        return FeederOutput;
    }

}

