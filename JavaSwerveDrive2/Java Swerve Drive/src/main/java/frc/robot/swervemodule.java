package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import Lib4150.Lib4150PositionControl;
import edu.wpi.first.math.geometry.Rotation2d;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import com.ctre.phoenix6.hardware.CANcoder;


public class swervemodule {


    private double xOff = 0.0;
    private double yOff = 0.0;
    private int driveid = 0;
    private int spinid = 0;
    private int spinEnc = 0;
    private double magOffset = 0.0;
    private Translation2d moduleoffset;
    private double spinPosRad = 0.0;
    private double driveSpeedMPS = 0.0;
    private SparkMax driveMotor;
    private SparkMax spinMotor;
    private Lib4150PositionControl spinPositionControl;
    private PIDController drivePID;
    private SimpleMotorFeedforward drivFeedforward;
    private CANcoder absoluteAngle;

    public swervemodule(double paraXoffset, double paraYoffset, int paradriveid, int paraspinid, int paraspinEnc, double paramagOffset) {
        xOff = paraXoffset;
        yOff = paraYoffset;
        driveid = paradriveid;
        spinid = paraspinid;
        spinEnc = paraspinEnc;
        magOffset = paramagOffset;

        moduleoffset = new Translation2d(xOff, yOff);
        driveMotor = new SparkMax(driveid,MotorType.kBrushless);
        spinMotor = new SparkMax(spinid,MotorType.kBrushless);
        absoluteAngle = new CANcoder(spinEnc);
        
        // (Error Deadband,error threshhold )
        spinPositionControl = new Lib4150PositionControl(Units.degreesToRadians(2.0), Units.degreesToRadians(50.0), 0.005, 0.35, 0.8, 1.0e-5, false, true);

        drivFeedforward = new SimpleMotorFeedforward (0.0, 0.07645406, 0.0);
        drivePID = new PIDController (0.07645406*1.0, 0.07645406*0.5, 1.0e-7*0.7645406);

    }

    public Translation2d getModuleLocation() {
        return moduleoffset;
    }
        
    public void ExecuteLogic( SwerveModuleState parmModState, double timeValue ) {
        
        //TODO:
        //Check how encoders actualy output; maybe need to change this.
        /*/if(spinPosRad > Angle)
        {
            rotationMotor.set(0.1); //current pos greater than desired > cw
        }
        else if (spinPosRad < Angle) 
        {
            rotationMotor.set(-0.1); //current pos less than desired > ccw
        }
        else
        {
            rotationMotor.set(0.0); //at right angle; stop
        }*/
        double currentAngle = absoluteAngle.getAbsolutePosition().getValueAsDouble()*2.0*Math.PI;
        parmModState.optimize(new Rotation2d(currentAngle));
        
        double targetAngle = parmModState.angle.getRadians();

        spinMotor.set(spinPositionControl.PosCtrlExec(targetAngle, currentAngle));
        

        double TargetSpeed = parmModState.speedMetersPerSecond;
        double ActualSpeed = driveMotor.getEncoder().getVelocity();
        double feedForward = drivFeedforward.calculate(TargetSpeed);
        double PIDoutput = drivePID.calculate(ActualSpeed, TargetSpeed);
        double MotorDemand = MathUtil.clamp(feedForward+PIDoutput, -1.0, 1.0);
        driveMotor.set(MotorDemand);

    }


    public void readSensors(){
        //TODO: actually read hardware here,
        spinPosRad = 0.0;
        driveSpeedMPS = 0.0;

    }

    


}
