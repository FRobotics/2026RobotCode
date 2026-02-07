package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;
import Lib4150.Lib4150PositionControl;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import Lib4150.Lib4150PositionControl;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax intakeMotor1;
    private static SparkMax intakeMotor2;
    private static Lib4150PositionControl IntakePositionControl;
    private static DutyCycleEncoder intakeEncoder;
    private static int intakeState;//1 is up off 2 is down off 3 is down on
    private static double intakeAngleTarget;
    public static boolean limitState;
    private static double INTAKEUPANGLE;
    private static double INTAKEDOWNANGLE;
    private static double encoderRot; //stores current value from encoder
    //TODO: determine up and down angles on the robot -- suggest starting with 90 is up and 0 is down...
    private static double intakeSpeed;
    private static double intakeAngleMotorDemand;
  
    public static void init() {

        //motors
        intakeMotor1 = new SparkMax(1,MotorType.kBrushless);
        intakeMotor2 = new SparkMax(2,MotorType.kBrushless);
        
        //open motor config
        SparkMaxConfig intake1Config = new SparkMaxConfig();
        SparkMaxConfig intake2Config = new SparkMaxConfig();

        //motor Config
        //TODO: config values need to be changed/tuned
        intake1Config.idleMode(IdleMode.kBrake);
        intake2Config.idleMode(IdleMode.kBrake);
        intake1Config.smartCurrentLimit(50);
        intake2Config.smartCurrentLimit(20);
        intake1Config.openLoopRampRate(0.2);
        intake2Config.openLoopRampRate(0.08);
        
        //sensors
        intakeEncoder = new DutyCycleEncoder(4);

        //initial state
        intakeAngleTarget=90;
        intakeSpeed=0;
        intakeState=1;

        INTAKEDOWNANGLE= 0;
        INTAKEUPANGLE=  90;
        encoderRot = 0;

        IntakePositionControl = new Lib4150PositionControl(Units.degreesToRadians(2.0), Units.degreesToRadians(50.0), 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");

        locNTSend.addItemBoolean("IntakeLimitIsPressed", IntakeSystem::getLimitState);

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        locNTSend.addItemBoolean("IntakeIsExtendedOn", IntakeSystem::getIntakeExtended);
        
        //encoder rotations
        locNTSend.addItemDouble("EncoderRotation", IntakeSystem::getEncoderRot);

        locNTSend.triggerUpdate();
         
    }

    public static void executeLogic(double systemElapsedTimeSec) {
        limitState = intakeMotor2.getForwardLimitSwitch().isPressed();
        encoderRot = intakeEncoder.get();

        locNTSend.triggerUpdate();
        
        if (intakeState>1){
            intakeAngleTarget= INTAKEDOWNANGLE;
        }else{
            intakeAngleTarget= INTAKEUPANGLE;
        }

        if (intakeState==3){
            intakeSpeed=1;
        }else {
            intakeSpeed=0;
        }
        intakeAngleMotorDemand=IntakePositionControl.PosCtrlExec(intakeAngleTarget, encoderRot);
        intakeMotor2.set(intakeAngleMotorDemand);
        
        intakeMotor1.set(intakeSpeed);
        
        if (intakeState==1){
            locIntakeExtended=false;
        }else{
            locIntakeExtended=true;
        }
    }

    public static void setDownOffState(){
        intakeState=2;
    }
    public static void setDownOnState(){
        intakeState=3;
    }
    public static void setUpOffState(){
        intakeState=1;
    }
    public static int getIntakeState(){
        return intakeState;
    }
    public static double getIntakeSpeed(){
        return intakeSpeed;
    }
    public static double getIntakeAngleTarget(){
        return intakeAngleTarget;
    }
    public static boolean getIntakeExtended() {
        return locIntakeExtended;
    }
    public static boolean getLimitState() {
        return limitState;
    }
    public static double getEncoderRot() {
        return encoderRot;
    }
    public static double getIntakeAngleMotorDemand() {
        return intakeAngleMotorDemand;
    }
}
