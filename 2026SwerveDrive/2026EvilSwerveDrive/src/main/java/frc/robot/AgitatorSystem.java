package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

// TODO: suggest removing unused imports
// import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
// import com.revrobotics.spark.config.SparkMaxConfig;
// import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class AgitatorSystem {

    private AgitatorSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;

    // TRUE = we want agitator to be on.  FALSE = we want agitator to be off
    private static boolean locAgitatorOn = false; 
    private static SparkMax AgitatorMotor;
    private static double AgitatorOutput = 0;

    public static void init() {


        // init agitator motor
        // TODO: UPDATE AGITATOR MOTOR CAN ID
        AgitatorMotor = new SparkMax(70,MotorType.kBrushless);

        // ensure agitator starts off
        cmdAgitatorOff();

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AgitatorSystem");

        locNTSend.addItemBoolean("AgitatorState", AgitatorSystem::getAgitatorState);
        locNTSend.addItemDouble("AgitatorOutput", AgitatorSystem::getMotorOutput);
        
        locNTSend.triggerUpdate();
        
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // if on, output 0.2
        // if off, output 0

        if (locAgitatorOn){
            AgitatorOutput=0.2;
        }
        else {
            AgitatorOutput=0;
        };

        AgitatorMotor.set(AgitatorOutput);

        locNTSend.triggerUpdate();
    }

    public static boolean getAgitatorState() {
        return locAgitatorOn;
    }

    public static void cmdAgitatorOn() {
        locAgitatorOn=true;
    }

    public static void cmdAgitatorOff() {
        locAgitatorOn=false;
    }
     public static double getMotorOutput() {
        return AgitatorOutput;
    }

}
