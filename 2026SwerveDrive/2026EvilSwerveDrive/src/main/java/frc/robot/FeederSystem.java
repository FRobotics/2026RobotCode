package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;


import com.revrobotics.spark.SparkLowLevel.MotorType;
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class FeederSystem {

    private FeederSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want agitator to be on.  FALSE = we want agitator to be off
    private static boolean locFeederOn = false; 
    private static SparkMax FeederMotor;
    private static double FeederOutput = 0;

    public static void init() {


        // init network table
        FeederMotor = new SparkMax(68,MotorType.kBrushless);
        locNTSend = new Lib4150NetTableSystemSend("FeederSystem");

        locNTSend.addItemBoolean("GetFeeder", FeederSystem::getFeederState);
        locNTSend.addItemDouble("FeederOutput", FeederSystem::getMotorOutput);
        cmdAgitatorOff();
        
        locNTSend.triggerUpdate();

        // if on, output 0.2
        // if off, output 0

        
    }

    public static void executeLogic() {

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

    public static void cmdAgitatorOn() {
        locFeederOn=true;
    }

    public static void cmdAgitatorOff() {
        locFeederOn=false;
    }
     public static double getMotorOutput() {
        return FeederOutput;
    }

}

