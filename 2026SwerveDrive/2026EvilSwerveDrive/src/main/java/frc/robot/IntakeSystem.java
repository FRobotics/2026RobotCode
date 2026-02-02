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
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax intakeMotor1;
    private static SparkMax intakeMotor2;
    private static DutyCycleEncoder intakeEncoder;
    private static int intakeState;//1 is up off 2 is down off 3 is down on
    private static double intakeAngle;
    //TODO determine up and down angles on the robot -- suggest starting with 90 is up and 0 is down...
    private static double intakeSpeed;
    // TODO: suggest using position control class to control intake arm position...  See swervemodule

    public static void init() {

        //motors
        intakeMotor1 = new SparkMax(1,MotorType.kBrushless);
        intakeMotor2 = new SparkMax(2,MotorType.kBrushless);
        
        //open motor config
        SparkMaxConfig intake1Config = new SparkMaxConfig();
        SparkMaxConfig intake2Config = new SparkMaxConfig();

        //motor Config
        //TODO: config values need changed/tuned
        intake1Config.idleMode(IdleMode.kBrake);
        intake2Config.idleMode(IdleMode.kBrake);
        intake1Config.smartCurrentLimit(50);
        intake2Config.smartCurrentLimit(20);
        intake1Config.openLoopRampRate(0.2);
        intake2Config.openLoopRampRate(0.08);
        
        //sensors
        intakeEncoder = new DutyCycleEncoder(4);

        //initial state
        intakeAngle=90;
        intakeSpeed=0;
        intakeState=1;

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");

        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        locNTSend.addItemBoolean("IntakeIsExtendedOn", IntakeSystem::getIntakeExtended);
        
        locNTSend.triggerUpdate();
        
       
        
        
    }

    public static void executeLogic() {
        locNTSend.triggerUpdate();
        //TODO set actual angles
        if (intakeState>1){
            intakeAngle=0;
        }else{
            intakeAngle=90;
        }

        if (intakeState==3){
            intakeSpeed=1;
        }else {
            intakeSpeed=0;
        }//TODO set actual speeds for intake speed and angle

        // TODO: Only read intake arm encoder sensor once !!  Set an internal variable.  Publist to network tables.
        // TODO: Read limit switch to an internal variable so it can be published to network tables. 
        // TODO: This is a type of BANG-BANG control.  It won't work for controlling position!  I don't see the motor being turned off?
        if (intakeAngle>intakeEncoder.get()+1){//+1 and -1 for within by 1 degree
            intakeMotor2.set(1);
        }else if((intakeAngle<intakeEncoder.get()-1)&& !(intakeMotor2.getForwardLimitSwitch().isPressed())){
            intakeMotor2.set(-1);
        }else{
            if (intakeState==1){
                locIntakeExtended=false;
            }else{
                locIntakeExtended=true;
            }
        }
        intakeMotor1.set(intakeSpeed);
        // TODO: Do this before executing the positioning logic.  Is "intakeAngle" desired or actual position.  
        if (intakeMotor2.getForwardLimitSwitch().isPressed()){
            intakeAngle=0;
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
    public static double getIntakeAngle(){
        return intakeAngle;
    }
    public static boolean getIntakeExtended() {
        return locIntakeExtended;
    }

}
