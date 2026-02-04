package frc.robot;

import Lib4150.Lib4150NetTableSystemSend;

import java.util.function.DoubleSupplier; //TODO: Remove unused import?

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
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax intakeMotor1;
    private static SparkMax intakeMotor2;
    private static DutyCycleEncoder intakeEncoder;
    private static int intakeState;//1 is up off 2 is down off 3 is down on
    private static double intakeAngleTarget;
    public static boolean limitState;

    private static double encoderRot; //stores current value from encoder
    //TODO: determine up and down angles on the robot -- suggest starting with 90 is up and 0 is down...
    private static double intakeSpeed;
    //TODO: suggest using position control class to control intake arm position...  See SwerveModule

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

        limitState = intakeMotor2.getForwardLimitSwitch().isPressed(); //is there only one limit switch?

        //TODO: this will be overwritten by encoder value before use. Is it fine to initalise to zero as default? Please check
        encoderRot = 0;

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

        locNTSend.triggerUpdate();
        //TODO: set actual angles
        if (intakeState>1){
            intakeAngleTarget=0;
        }else{
            intakeAngleTarget=90;
        }

        if (intakeState==3){
            intakeSpeed=1;
        }else {
            intakeSpeed=0;
        }//TODO: set actual speeds for intake speed and angle
        if (limitState){
            intakeAngleTarget=0;
        }

        //get encoder rotations
        encoderRot = intakeEncoder.get();
        // TODO: This is a type of BANG-BANG control.  It won't work for controlling position!  I don't see the motor being turned off?
        if (intakeAngleTarget>encoderRot+1){//+1 and -1 for within by 1 degree
            intakeMotor2.set(1);
        }else if((intakeAngleTarget<encoderRot-1)&& !(limitState)){
            intakeMotor2.set(-1);
        }else{
            if (intakeState==1){
                locIntakeExtended=false;
            }else{
                locIntakeExtended=true;
            }
        }
        intakeMotor1.set(intakeSpeed);
        
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
    private static double getEncoderRot() {
        return encoderRot;
    }
}
