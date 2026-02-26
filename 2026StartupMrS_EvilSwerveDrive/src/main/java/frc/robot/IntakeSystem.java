package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;
import Lib4150.Lib4150PositionControl;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
//import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
//import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.DutyCycleEncoder;
//import edu.wpi.first.wpilibj.Encoder;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants
    private static final double INTAKEUPANGLE = 90.0;
    private static final double INTAKEDOWNANGLE = 0.0;
    private static final double INTAKEDOWNLIMITSWITCHANGLE = 2.0;

    private static final double PICKUP_MOTOR_ON = 0.25;
    private static final double PICKUP_MOTOR_OFF = 0.0;

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax intakeMotor1;   // intake ??
    private static SparkMax intakeMotor2;   // arm ??
    private static RelativeEncoder intakeMotor1Encoder;
    private static Lib4150PositionControl IntakePositionControl;
    private static RelativeEncoder intakeMotor2Encoder;
    private static int intakeState;//1 is up off, 2 is down off, 3 is down on,
    private static double intakeAngleTarget;
    private static boolean limitState;
    private static double encoderRot; //stores current value from encoder
    private static double intakeSpeed;
    private static double intakeMotorRPM;
    private static double intakeAngleMotorDemand;
    private static DigitalInput limitSwitch;
    // private static double intakeGearRatio = 36.0;
    private static double intakeGearRatio = 32.2;
    private static Lib4150DigEdgeOn IntakeArmLowLimitSwitchEdgeOn;
  
    public static void init() {

        //motors
        // TODO: Say which motor is arm and which is intake....  Is 1 intake, and 2 arm ??
        intakeMotor1 = new SparkMax(6,MotorType.kBrushless);
        intakeMotor2 = new SparkMax(5,MotorType.kBrushless);

        
        
        //open motor config
        //SparkMaxConfig intake1Config = new SparkMaxConfig();
        //SparkMaxConfig intake2Config = new SparkMaxConfig();
        
        //motor Config
        //TODO: config values need to be changed/tuned
        /*intake1Config.idleMode(IdleMode.kBrake);
        intake2Config.idleMode(IdleMode.kBrake);
        intake1Config.smartCurrentLimit(50);
        intake2Config.smartCurrentLimit(20);
        intake1Config.openLoopRampRate(0.2);
        intake2Config.openLoopRampRate(0.08);*/
        
        //sensors
        intakeMotor1Encoder = intakeMotor1.getEncoder();
        intakeMotor2Encoder = intakeMotor2.getEncoder();
        limitSwitch = new DigitalInput(0);

        //initial state
        intakeAngleTarget=90.0;
        intakeSpeed=0.0;
        intakeState=1;

        limitState = false;
        encoderRot = 0.0;

        IntakeArmLowLimitSwitchEdgeOn = new Lib4150DigEdgeOn();

        IntakePositionControl = new Lib4150PositionControl(Units.degreesToRadians(2.0), Units.degreesToRadians(50.0), 
                            0.005, 0.35, 0.35, 1.0e-5, false, false);

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");


        // --------intake arm        
        //encoder rotations
        locNTSend.addItemBoolean("IntakeLimitIsPressed", IntakeSystem::getLimitState);
        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        // TODO: Is this intake or arm motor ????
        locNTSend.addItemDouble("EncoderRotation", IntakeSystem::getEncoderRot);
        locNTSend.addItemDouble("IntakeAngleTarget", IntakeSystem::getIntakeAngleTarget);
        locNTSend.addItemDouble("IntakeAngleMotorOut",IntakeSystem::getIntakeAngleMotorOut);
        // -------intake ball collector
        locNTSend.addItemBoolean("BallIntakeOn", IntakeSystem::getBallIntakeState);
        locNTSend.addItemDouble("IntakeMotorOut", IntakeSystem::getIntakeSpeed);
        locNTSend.addItemDouble("IntakeMotorRPM", IntakeSystem::getIntakeMotorRPM);
         
        locNTSend.triggerUpdate();
        return;
         
    }

    public static void executeLogic(double systemElapsedTimeSec) {

        // --------read the arm down limit switch..
        // --------if we hit the limit switch once, keep it on until the angle is above the limit switch value....
        // --------Add a little hysteresis of 2.0 degrees for the encoderRot position.
        // --------This depends on the previous value of limitState and encoderRot
        limitState = limitSwitch.get() || ( limitState && ( encoderRot <= (INTAKEDOWNLIMITSWITCHANGLE+2.0)));

        // ---------if we just hit the limit switch, set the value of the encoder position.
        if ( IntakeArmLowLimitSwitchEdgeOn.execEdgeOn(limitState) ) {
            // TODO: remove comment when limit switch is wired.
            //intakeMotor2Encoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEDOWNLIMITSWITCHANGLE));
        }
        
        // --------read the arm position in degrees.
        encoderRot = calcArmDegFromRawEncoder( intakeMotor2Encoder.getPosition() );     // pos of arm ??

        // --------read the RPM of the ball intake.  This is to help determine what motor output is desired.
        intakeMotorRPM = intakeMotor1Encoder.getVelocity(); // ball intake rpm

        
        //1 is up off 2 is down off 3 is down on

        // down (on or off )
        if (intakeState>1){
            intakeAngleTarget= INTAKEDOWNANGLE;
        }
        // up
        else{
            intakeAngleTarget= INTAKEUPANGLE;
        }

        // on
        if (intakeState==3){
            intakeSpeed=PICKUP_MOTOR_ON;
        }
        // off
        else {
            intakeSpeed=PICKUP_MOTOR_OFF;
        }

        // do arm position control
        intakeAngleMotorDemand=IntakePositionControl.PosCtrlExec(intakeAngleTarget, encoderRot);
        intakeMotor2.set(intakeAngleMotorDemand);
        
        // set output for ball intake motor.
        intakeMotor1.set(intakeSpeed);
        
        // TODO: what is this for?  Maybe check the actual angle -- encoderRot -- instead.  
        if (intakeState==1){
            locIntakeExtended=false;
        }else{
            locIntakeExtended=true;
        }

        locNTSend.triggerUpdate();
        return;
    }

    // --------internal calculation routines
    
    // ---------calculate the arm position in degrees given the raw encoder value in rotations.
    private static double calcArmDegFromRawEncoder( double encoderRotations ) {
        return encoderRotations*360.0/intakeGearRatio+90.0;
    }

    // ---------calculate the raw encoder value in rotations given the arm position in degrees
    private static double calcEncoderRawValueFromArmDeg( double armDeg ) {
        return armDeg/360.0*intakeGearRatio-90.0;
    }


    // --------setters
    public static void setDownOffState(){
        intakeState=2;
        return;
    }
    public static void setDownOnState(){
        intakeState=3;
        return;
    }
    public static void setUpOffState(){
        intakeState=1;
        return;
    }

    // --------getters
    public static int getIntakeState(){
        return intakeState;
    }
    public static double getIntakeSpeed(){
        return intakeSpeed;
    }
    public static double getIntakeMotorRPM(){
        return intakeMotorRPM;
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
    public static double getIntakeAngleMotorOut() {
        return intakeAngleMotorDemand;
    }
    // --------ball intake is on.
    public static boolean getBallIntakeState() {
        return ( intakeState == 3 );
    }
}
