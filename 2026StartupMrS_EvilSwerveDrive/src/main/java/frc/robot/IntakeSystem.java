package frc.robot;

import Lib4150.Lib4150DigEdgeOn;
import Lib4150.Lib4150DigOnDelay;
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
import edu.wpi.first.wpilibj.Timer;

public class IntakeSystem {

    private IntakeSystem(){}

    // contants

    // --------intake arm positoin setpoints.
    private static final double INTAKESTART_ANGLE = 90.0;
    private static final double INTAKEUP_ANGLE = 85.0; // leave a little leeway for slippage and error setting angle at bottom.
    private static final double INTAKEDOWN_ANGLE = 0.0;
    private static final double INTAKEDOWN_LIMITSWITCH_ANGLE = 0.9;

    // --------intake arm positionm control
    private static final double INTAKEARM_ERR_DEADBAND = 4.0;       // degrees
    private static final double INTAKEARM_ERR_THRESHOLD = 25.0;     // degrees
    private static final double INTAKEARM_OUT_DEADBAND = 0.005;     // motor output units.
    private static final double INTAKEARM_OUT_THRESHOLD = 0.30;     // motor output units.
    private static final double INTAKEARM_OUT_MAX = 0.50;           // motor outpuot units.
    private static final double INTAKEARM_KD =  0.0006;             // motor output units.  Assume 10 deg change, 
                                                                    // so averaged deriviative = 10/3/0.020 = 166.67.  Want extra output of 0.1
                                                                    // so Kd = 0.1 / 166.67 = 0.0006  (start with small change, increment as needed.)
    private static final boolean INTAKEARM_FILTER_DERIVATIVE = true;    // take average of last 3 derivatives.
    private static final boolean INTAKEARM_REVERSE = false;         // invert motor outpuot.
    // --------intake arm output rate limiter
    private static final double INTAKEARM_RATELIMIT = 10.0;         // motor units/second
  
    // private static final double ARM_GRAVITY_CONSTANT = 0.112;    // was 0.13
    private static final double ARM_GRAVITY_CONSTANT = 0.09;    // was 0.13

    private static final double PICKUP_MOTOR_ON = 1.00;
    private static final double PICKUP_MOTOR_OFF = 0.0;

    // --------ball shooting pickup arm rocking...
    private static final double ROCK_DOWN_TIME = 2.0;   // time arm is down - seconds
    private static final double ROCK_UP_TIME = 3.5;     // time arm is up - seconds
    private static final double ROCK_UP_POS = 40.0;     // arm pos for up - degrees

    // --------ball shooting pickup arm rocking alternate sitting up.
    private static final double ROCK_ALT_DOWN_TIME = 1.0;   // time arm is down - seconds
    private static final double ROCK_ALT_POS_1 = 20.0;      // first position degrees
    private static final double ROCK_ALT_POS_1_TIME = 2.0;  // seconds
    private static final double ROCK_ALT_POS_2 = 35.0;      // second position degrees
    private static final double ROCK_ALT_POS_2_TIME = 2.0;  // seconds
    private static final double ROCK_ALT_POS_3 = 50.0;      // third positoin degrees

    // --------ball pickup motor stall constants
    private static final double STALL_DETECT_TIME = 0.40;       // seconds to detect stall
    private static final double STALL_DETECT_MIN_RPM = 90.0;   // RPM below this indicates stall.
    private static final double STALL_DETECT_HYSTERESIS_RPM = 120.0;    // RPM indicates no longer stalled.
    private static final double STALL_REVERSE_TIME = 0.60;      // seconds to go in reverse.
    private static final double STALL_REVERSE_MOTOR = -1.00;    // motor output to un-jam things.


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
    // private static double intakeGearRatio = 32.2;
    private static double intakeGearRatio = 53.6666666;
    private static Lib4150DigEdgeOn IntakeArmLowLimitSwitchEdgeOn;
    private static Lib4150DigOnDelay IntakeArmLowLimitSwitchOnDelay;
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
    private static boolean locPickupStalled = false;
    private static int locPickupStallState = 0;
    private static double locStallTimer = 0.0;

    
    public static void init() {

        //motors
        IntakeBallMotor = new SparkMax(CanId.IntakeBallMotor,MotorType.kBrushless);
        IntakeArmMotor = new SparkMax(CanId.IntakeArmMotor,MotorType.kBrushless);

        
        
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
        intakeAngleTarget=INTAKESTART_ANGLE;
        IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKESTART_ANGLE));
        intakeSpeed=0.0;
        intakeState=1;

        locPickupStallState = 0;
        locPickupStalled = false;

        cmdRockDisable();

        IntakeArmLowLimitSwitchState = false;
        IntakeArmAngleActual = 0.0;

        IntakeArmLowLimitSwitchEdgeOn = new Lib4150DigEdgeOn();
        IntakeArmLowLimitSwitchOnDelay = new Lib4150DigOnDelay(0.5, Timer.getFPGATimestamp() );

        // position units are degrees.
        // was 30, now 35...
        // IntakePositionControl = new Lib4150PositionControl( 4.0, 35.0, 
        //                     0.005, 0.25, 0.25, 1.0e-5, false, false);
        // -------be more agressive for rocking...
        IntakePositionControl = new Lib4150PositionControl( INTAKEARM_ERR_DEADBAND, INTAKEARM_ERR_THRESHOLD, 
                            INTAKEARM_OUT_DEADBAND, INTAKEARM_OUT_THRESHOLD, INTAKEARM_OUT_MAX, 
                            INTAKEARM_KD, INTAKEARM_FILTER_DERIVATIVE, INTAKEARM_REVERSE);

        IntakeArmRateLimit = new SlewRateLimiter(INTAKEARM_RATELIMIT);  // 0 to full in 0.1 seconds.


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
        locNTSend.addItemBoolean("BallMotorStalled", IntakeSystem::getPickupMotorStalled);
         
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
        //IntakeArmLowLimitSwitchState = !IntakeArmLowLimitSwitch.get() || ( IntakeArmLowLimitSwitchState && ( IntakeArmAngleActual <= (INTAKEDOWN_LIMITSWITCH_ANGLE+2.0)));
        IntakeArmLowLimitSwitchState = !IntakeArmLowLimitSwitch.get();

        // ---------if we just hit the limit switch, set the value of the encoder position.
        // ---------give the arm a little time to settle, then update value.
        if ( IntakeArmLowLimitSwitchEdgeOn.execEdgeOn( IntakeArmLowLimitSwitchOnDelay.ExecOnDelay(IntakeArmLowLimitSwitchState, systemElapsedTimeSec) ) ) {
            IntakeArmMotorEncoder.setPosition( calcEncoderRawValueFromArmDeg(INTAKEDOWN_LIMITSWITCH_ANGLE));
        }
        
        // --------read the arm position in degrees.
        IntakeArmAngleActual = calcArmDegFromRawEncoder( IntakeArmMotorEncoder.getPosition() );     // pos of arm ??

        // --------read the RPM of the ball intake.  This is to help determine what motor output is desired.
        intakeMotorRPM = IntakeBallMotorEncoder.getVelocity(); // ball intake rpm

        
        //1 is up off 2 is down off 3 is down on

        // down (on or off )
        if (intakeState>1){
            intakeAngleTarget= INTAKEDOWN_ANGLE;
        }
        // up
        else{
            intakeAngleTarget= INTAKEUP_ANGLE;
        }

        // on
        if (intakeState==3){
            intakeSpeed=PICKUP_MOTOR_ON;
        }
        // off
        else {
            intakeSpeed=PICKUP_MOTOR_OFF;
            locPickupStallState = 0;
            locPickupStalled = false;
        }


        // --------process intake arm rocking (or just sitting up)
        // process_intake_arm_rocking(systemElapsedTimeSec);
        process_intake_arm_rocking_ALT(systemElapsedTimeSec);
     
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
        
        // ========Intake Ball Pickup Motor
        process_ball_intake_stall_detect( systemElapsedTimeSec );
        // --------do motor stall processing


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

    // --------process intake arm rocking state machine
    @SuppressWarnings("unused")
    private static void process_intake_arm_rocking(double mySystemElapsedTimeSec) {
        // --------should we rock ??? -- if so, process..
        // --------regardless of enable - only rock if state is 2 (down, off)
        if ( intakeState == 2 && intakeRockEnable ) {
            // --------process rock state machine
            switch ( intakeRockState ) {
                // --------remember start time.  Leave angle which should be down, alone
                case 0:
                    intakeRockStepTime = mySystemElapsedTimeSec;
                    intakeRockState = 1;
                    break;
                // --------down, waitin for timer to expire.
                case 1:
                    if ( mySystemElapsedTimeSec > ( intakeRockStepTime+ROCK_DOWN_TIME) ) {
                        intakeRockState = 2;
                    }
                    break;
                // --------up, set start time.
                case 2:
                    intakeAngleTarget = ROCK_UP_POS;
                    intakeRockStepTime = mySystemElapsedTimeSec;
                    intakeRockState = 3;
                    break;
                // --------up, wait for up time to expire..
                case 3:
                    intakeAngleTarget = ROCK_UP_POS;
                    if ( mySystemElapsedTimeSec > ( intakeRockStepTime+ROCK_UP_TIME) ) {
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
    }



    // --------process intake arm alternate rocking (sitting up) state machine
    @SuppressWarnings("unused")
    private static void process_intake_arm_rocking_ALT(double mySystemElapsedTimeSec) {
        // --------should we rock ??? -- if so, process..
        // --------regardless of enable - only rock if state is 2 (down, off)
        if ( intakeState == 2 && intakeRockEnable ) {
            // --------process rock state machine
            switch ( intakeRockState ) {
                // --------remember start time.  Leave angle which should be down, alone
                case 0:
                    intakeRockStepTime = mySystemElapsedTimeSec;
                    intakeRockState = 1;
                    break;
                // --------down, waitin for timer to expire.
                case 1:
                    if ( mySystemElapsedTimeSec > ( intakeRockStepTime+ROCK_ALT_DOWN_TIME) ) {
                        intakeRockState = 2;
                    }
                    break;
                // --------timer expired set first position..
                case 2:
                    intakeAngleTarget = ROCK_ALT_POS_1;
                    intakeRockStepTime = mySystemElapsedTimeSec;
                    intakeRockState = 3;
                    break;
                // --------up, wait for pos 1 time to expire.
                case 3:
                    intakeAngleTarget = ROCK_ALT_POS_1;
                    if ( mySystemElapsedTimeSec > ( intakeRockStepTime+ROCK_ALT_POS_1_TIME) ) {
                        intakeRockState = 4;
                    }
                    break;
                // --------timer expired set second position..
                case 4:
                    intakeAngleTarget = ROCK_ALT_POS_2;
                    intakeRockStepTime = mySystemElapsedTimeSec;
                    intakeRockState = 5;
                    break;
                // --------up, wait for pos2 time to expire.
                case 5:
                    intakeAngleTarget = ROCK_ALT_POS_2;
                    if ( mySystemElapsedTimeSec > ( intakeRockStepTime+ROCK_ALT_POS_2_TIME) ) {
                        intakeRockState = 6;
                    }
                    break;
                // --------timer expired set third and last position..  never leave this state until shooting is done.
                case 6:
                    intakeAngleTarget = ROCK_ALT_POS_3;
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
    }


    // --------process ball install stall detection
    private static void process_ball_intake_stall_detect( double mySystemElapsedTimeSec ) {
        // --------calculate values for reverse jog when forward stalls
        // --------turn rev stall prevention on
        // --------process the "stall" state machine
        // --------variable "intakeSpeed" is motor demand +/- 1.0.
        switch ( locPickupStallState ) {
            // --------wait for stall to occur
            case 0:
                locPickupStalled = false;
                locStallTimer = mySystemElapsedTimeSec;
                if ( (intakeSpeed > 0.0) && (Math.abs( intakeMotorRPM ) < STALL_DETECT_MIN_RPM) ) {
                    locPickupStallState = 1;
                } 
                break;
            // --------wait for timer to expire, then set stalled.
            case 1:
                locPickupStalled = false;
                if ( (intakeSpeed <= 0.0) ) {
                    locPickupStallState = 0;
                } 
                else if ( Math.abs( intakeMotorRPM ) > STALL_DETECT_HYSTERESIS_RPM ) {
                    locPickupStallState = 0;
                }
                else if ( mySystemElapsedTimeSec > ( locStallTimer + STALL_DETECT_TIME ) ) {
                    locPickupStallState = 2;
                    locStallTimer = mySystemElapsedTimeSec;
                }
                break;
            // --------we are stalled, reverse motor until timer expires
            case 2:
                locPickupStalled = true;
                if ( mySystemElapsedTimeSec > ( locStallTimer + STALL_REVERSE_TIME ) ) {
                    locPickupStallState = 0;
                    locStallTimer = mySystemElapsedTimeSec;
                }
                break;
        }

        // -------- if stalled set speed.
        if ( locPickupStalled ) {
            intakeSpeed = STALL_REVERSE_MOTOR;
        }
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

    // --------get pickup motor stalled
    public static boolean getPickupMotorStalled() {
        return locPickupStalled;
    }
}
