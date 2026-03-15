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

    private static final double ARM_GRAVITY_CONSTANT = 0.11;    // was 0.13

    private static final double PICKUP_MOTOR_ON = 0.75;
    private static final double PICKUP_MOTOR_OFF = 0.0;

    private static final double ROCK_DOWN_TIME = 2.0;   // time arm is down - seconds
    private static final double ROCK_UP_TIME = 3.0;     // time arm is up - seconds
    private static final double ROCK_UP_POS = 35.0;     // arm pos for up - degrees

    // class/object variables
    private static Lib4150NetTableSystemSend locNTSend;
    private static boolean locIntakeExtended = false;  // false if up, true if down.
    private static SparkMax IntakeBallMotor;   // intake ??
    private static SparkMax IntakeArmMotor;   // arm ??
    private static RelativeEncoder IntakeBallMotorEncoder;
    private static Lib4150PositionControl IntakePositionControl;
    private static RelativeEncoder IntakeArmMotorEncoder;
    private static int intakeState;//1 is up off, 2 is down off, 3 is down on,
    private static double intakeAngleTarget;
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
    // --------variables for rocking..
    private static boolean intakeRockEnable = false;
    private static int intakeRockState = 0; // states - 0 - Off or Wait to Start, 1 - Start, set up, 2 - up, wait to finish
    private static double intakeRockStepTime = 0.0;
    //private static double locRockTargetAngle = 0.0;

    
    public static void init() {

        //motors
        IntakeBallMotor = new SparkMax(6,MotorType.kBrushless);
        IntakeArmMotor = new SparkMax(5,MotorType.kBrushless);

        
        
        //open motor config
        //SparkMaxConfig intake1Config = new SparkMaxConfig();
        //SparkMaxConfig intake2Config = new SparkMaxConfig();
        
        //motor Config
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
        IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEUPANGLE));
        intakeSpeed=0.0;
        intakeState=1;
        cmdRockDisable();

        IntakeArmLowLimitSwitchState = false;
        IntakeArmAngleActual = 0.0;

        IntakeArmLowLimitSwitchEdgeOn = new Lib4150DigEdgeOn();

        // position units are degrees.
        // was 30, now 35...
        IntakePositionControl = new Lib4150PositionControl( 4.0, 35.0, 
                            0.005, 0.25, 0.25, 1.0e-5, false, false);

        IntakeArmRateLimit = new SlewRateLimiter(.75);  // 0 to full in 1.3 seconds.


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

        // --------rev through bore encoder in absolute mode. -- NOT USED FOR CONTROL YET.
        IntakeArmABSEncPos = IntakeArmABSEnc.get() * 360.0;

        // --------read the arm down limit switch..
        // --------if we hit the limit switch once, keep it on until the angle is above the limit switch value....
        // --------Add a little hysteresis of 2.0 degrees for the IntakeArmAngleActual position.
        // --------This depends on the previous value of IntakeArmLowLimitSwitchState and IntakeArmAngleActual
        IntakeArmLowLimitSwitchState = !IntakeArmLowLimitSwitch.get() || ( IntakeArmLowLimitSwitchState && ( IntakeArmAngleActual <= (INTAKEDOWNLIMITSWITCHANGLE+2.0)));

        // ---------if we just hit the limit switch, set the value of the encoder position.
        if ( IntakeArmLowLimitSwitchEdgeOn.execEdgeOn(IntakeArmLowLimitSwitchState) ) {
            // TODO: DEBUG LEAVE THIS OUT FOR NOW IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEDOWNLIMITSWITCHANGLE));
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


        // --------should we rock ??? -- if so, process..
        // --------regardless of enable - only rock if state is 2 (down, off)
        if ( intakeState == 2 && intakeRockEnable ) {
            // --------process rock state machine
            switch ( intakeRockState ) {
                // --------remember start time.  Leave angle which should be down, alone
                case 0:
                    intakeRockStepTime = systemElapsedTimeSec;
                    intakeRockState = 1;
                    break;
                // --------down, waitin for timer to expire.
                case 1:
                    if ( systemElapsedTimeSec > ( intakeRockStepTime+ROCK_DOWN_TIME) ) {
                        intakeRockState = 2;
                    }
                    break;
                // --------up, set start time.
                case 2:
                    intakeAngleTarget = ROCK_UP_POS;
                    intakeRockStepTime = systemElapsedTimeSec;
                    intakeRockState = 3;
                    break;
                // --------up, wait for up time to expire..
                case 3:
                    intakeAngleTarget = ROCK_UP_POS;
                    if ( systemElapsedTimeSec > ( intakeRockStepTime+ROCK_UP_TIME) ) {
                        intakeRockState = 0;
                    }
                    break;
                // --------something is wrong, disable rock
                default:
                    cmdRockDisable();
                    break;
            }

        }
        else {
            intakeRockState = 0;
        }
     
        // --------do arm position control - values in degrees
        // --------grav constant was 0.10, now 0.13.
        intakeAngleMotorDemand= IntakePositionControl.PosCtrlExec(intakeAngleTarget, IntakeArmAngleActual) ;
        double intakeAngleGravityConstant = Math.cos(Units.degreesToRadians(IntakeArmAngleActual)) * ARM_GRAVITY_CONSTANT;
        // --------gently remove the gravity constant
        if ( IntakeArmAngleActual <= 10.0 ) {
            intakeAngleGravityConstant = intakeAngleGravityConstant * IntakeArmAngleActual / 10.0;
        }
        // --------clamp and rate limit final output
        double tmpLowClamp = (IntakeArmLowLimitSwitchState) ? 0.0 : -1.0;

        // --------rate limit then clamp.
        intakeAngleMotorDemand = MathUtil.clamp( IntakeArmRateLimit.calculate( intakeAngleMotorDemand + intakeAngleGravityConstant), tmpLowClamp, 1.0 );
        IntakeArmMotor.set(intakeAngleMotorDemand);
        
        // set output for ball intake motor.
        IntakeBallMotor.set(intakeSpeed);

        // ---------set local variable if arm extended for network tables.
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
    public static void cmdRockEnable(){
        intakeRockEnable = true;
        return;
    }
    public static void cmdRockDisable(){
        intakeRockEnable = false;
        return;
    }
    public static void setDownOffState(){
        intakeState=2;
        cmdRockDisable();    // also set rock state off.
        return;
    }
    public static void setDownOnState(){
        intakeState=3;
        return;
    }
    public static void setUpOffState(){
        intakeState=1;
        cmdRockDisable();    // also set rock state off.
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
