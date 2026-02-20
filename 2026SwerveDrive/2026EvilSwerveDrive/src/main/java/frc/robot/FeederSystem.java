package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;


public class FeederSystem {

    private FeederSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want feeder to be on.  FALSE = we want feeder to be off
    private static boolean locFeederOn = false; 
    private static SparkMax FeederMotor;
    private static RelativeEncoder FeederMotorEncoder;
    private static double FeederOutput = 0;
    private static double FeederRPM = 0.0;

    public static void init() {


        // init network table
        // TODO: Set feeder motor CAN ID
        FeederMotor = new SparkMax(68,MotorType.kBrushless);
        FeederMotorEncoder = FeederMotor.getEncoder();

        // start with feeder off
        cmdFeederOff();

        locNTSend = new Lib4150NetTableSystemSend("FeederSystem");

        locNTSend.addItemBoolean("FeederState", FeederSystem::getFeederState);
        locNTSend.addItemDouble("FeederOutput", FeederSystem::getMotorOutput);
        locNTSend.addItemDouble("FeederRPM", FeederSystem::getMotorRPM);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic() {

        FeederRPM = FeederMotorEncoder.getVelocity();

        // if on, output 0.2
        // if off, output 0
        if (locFeederOn){
            FeederOutput=0.2;
        }
        else {
            FeederOutput=0;
        };

        // TODO: Might need to do speed control here...
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

    public static double getMotorRPM() {
        return FeederRPM;
    }

}

