package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150NetTableSystemSend;
import Lib4150.Lib4150PositionControl;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants
    private static final double INTAKEUPANGLE = 90.0;
    private static final double INTAKEDOWNANGLE = 0.0;
    private static final double INTAKEDOWNLIMITSWITCHANGLE = 0.9;

    private static final double PICKUP_MOTOR_ON = 0.75;
    private static final double PICKUP_MOTOR_OFF = 0.0;

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax IntakeBallMotor;   // intake ??
    private static SparkMax IntakeArmMotor;   // arm ??
    private static RelativeEncoder IntakeBallMotorEncoder;
    private static Lib4150PositionControl IntakePositionControl;
    private static RelativeEncoder IntakeArmMotorEncoder;
    private static int intakeState;//1 is up off, 2 is down off, 3 is down on,
    private static boolean intakeRockState = false;
    public static double intakeAngleTarget;
    private static boolean IntakeArmLowLimitSwitchState;
    private static double IntakeArmAngleActual; //stores current value from encoder
    private static double intakeSpeed;
    private static double intakeMotorRPM;
    private static double intakeAngleMotorDemand;
    private static DigitalInput IntakeArmLowLimitSwitch;
    // private static double intakeGearRatio = 36.0;
    private static double intakeGearRatio = 32.2;
    private static Lib4150DigEdgeOn IntakeArmLowLimitSwitchEdgeOn;
    // --------rev through bore encoder - abs mode
    private static DigitalInput IntakeArmABSEncDI;
    private static DutyCycleEncoder IntakeArmABSEnc;
    private static double IntakeArmABSEncPos = 0.0;
    // --------a little rate limiting on starting the intake move..
    private static SlewRateLimiter IntakeArmRateLimit;
    // --------next rock time...
    private static double locNextRockTime = 0.0;
    private static double locRockTargetAngle = 0.0;
    private static double encoderRot;

    
    public static void init() {

        //motors
        IntakeBallMotor = new SparkMax(6,MotorType.kBrushless);
        IntakeArmMotor = new SparkMax(5,MotorType.kBrushless);

        
        
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
        IntakeBallMotorEncoder = IntakeBallMotor.getEncoder();
        IntakeArmMotorEncoder = IntakeArmMotor.getEncoder();
        // -------limit switch is false when engaged.
        IntakeArmLowLimitSwitch = new DigitalInput(0);

        // --------rev absolute encoder.
        IntakeArmABSEncDI = new DigitalInput(4);
        IntakeArmABSEnc = new DutyCycleEncoder(IntakeArmABSEncDI);

        //initial state
        intakeAngleTarget=INTAKEUPANGLE;
        intakeSpeed=0.0;
        intakeState=1;
        intakeRockState = false;

        IntakeArmLowLimitSwitchState = false;
        IntakeArmAngleActual = 0.0;

        IntakeArmLowLimitSwitchEdgeOn = new Lib4150DigEdgeOn();

        // position units are degrees.
        // was 30, now 35...
        IntakePositionControl = new Lib4150PositionControl( 2.0, 35.0, 
                            0.005, 0.25, 0.25, 1.0e-5, false, false);

        IntakeArmRateLimit = new SlewRateLimiter(2.0);  // 0 to full in 1/2 second.

        IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEUPANGLE));

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("IntakeSystem");


        // --------intake arm        
        // --------Intake Arm
        locNTSend.addItemBoolean("IntakeLimitIsPressed", IntakeSystem::getIntakeArmLowLimitSwitchState);
        locNTSend.addItemBoolean("IntakeIsExtended", IntakeSystem::getIntakeExtended);
        locNTSend.addItemDouble("IntakeAngleActual", IntakeSystem::getIntakeArmAngleActual);
        locNTSend.addItemDouble("IntakeAngleActualABS", IntakeSystem::getIntakeArmAngleActualABS);
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

        // --------rev through bore encoder in absolute mode.
        IntakeArmABSEncPos = IntakeArmABSEnc.get() * 360.0;

        // --------read the arm down limit switch..
        // --------if we hit the limit switch once, keep it on until the angle is above the limit switch value....
        // --------Add a little hysteresis of 2.0 degrees for the IntakeArmAngleActual position.
        // --------This depends on the previous value of IntakeArmLowLimitSwitchState and IntakeArmAngleActual
        IntakeArmLowLimitSwitchState = !IntakeArmLowLimitSwitch.get() || ( IntakeArmLowLimitSwitchState && ( IntakeArmAngleActual <= (INTAKEDOWNLIMITSWITCHANGLE+2.0)));

        // ---------if we just hit the limit switch, set the value of the encoder position.
        if ( IntakeArmLowLimitSwitchEdgeOn.execEdgeOn(IntakeArmLowLimitSwitchState) ) {
            IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEDOWNLIMITSWITCHANGLE));
        }
        
        // --------read the arm position in degrees.
        IntakeArmAngleActual = calcArmDegFromRawEncoder( IntakeArmMotorEncoder.getPosition() );     // pos of arm ??

        // --------read the RPM of the ball intake.  This is to help determine what motor output is desired.
        intakeMotorRPM = IntakeBallMotorEncoder.getVelocity(); // ball intake rpm

        
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

        
        // do control
        intakeAngleMotorDemand=IntakePositionControl.PosCtrlExec(intakeAngleTarget, IntakeArmAngleActual);
        // do arm position control - values in degrees
        // grav constant was 0.10, now 0.13.
        intakeAngleMotorDemand=IntakeArmRateLimit.calculate( IntakePositionControl.PosCtrlExec(intakeAngleTarget, IntakeArmAngleActual) );
        double intakeAngleGravityConstant = Math.cos(Units.degreesToRadians(IntakeArmAngleActual)) * 0.13;
        // --------gently remove the gravity constant
        if ( IntakeArmAngleActual <= 8.0 ) {
            intakeAngleGravityConstant = intakeAngleGravityConstant * IntakeArmAngleActual / 8.0;
        }
        intakeAngleMotorDemand = MathUtil.clamp( intakeAngleMotorDemand + intakeAngleGravityConstant, -1.0, 1.0 );
        IntakeArmMotor.set(intakeAngleMotorDemand);
        
        // set output for ball intake motor.
        IntakeBallMotor.set(intakeSpeed);
        
        // TODO: what is this for?  Maybe check the actual angle -- IntakeArmAngleActual -- instead.  
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
        return ( armDeg - 90.0 ) /360.0*intakeGearRatio;
    }


    // --------setters
    public static void setRockOffState(){
        intakeState=2;
        intakeRockState = true;
        return;
    }
    public static void setDownOffState(){
        intakeState=2;
        intakeRockState = false;
        return;
    }
    public static void setDownOnState(){
        intakeState=3;
        intakeRockState = false;
        return;
    }
    public static void setUpOffState(){
        intakeState=1;
        intakeRockState = false;
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
    public static boolean getIntakeArmLowLimitSwitchState() {
        return IntakeArmLowLimitSwitchState;
    }
    public static double getIntakeArmAngleActual() {
        return IntakeArmAngleActual;
    }
    public static double getIntakeArmAngleActualABS() {
        return IntakeArmABSEncPos;
    }
    public static double getIntakeAngleMotorOut() {
        return intakeAngleMotorDemand;
    }
    // --------ball intake is on.
    public static boolean getBallIntakeState() {
        return ( intakeState == 3 );
    }
}
