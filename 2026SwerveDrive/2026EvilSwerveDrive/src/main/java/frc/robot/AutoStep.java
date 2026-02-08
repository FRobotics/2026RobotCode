package frc.robot;

public class AutoStep {

    
   
    // TODO: Suggest adding the following Cmd -- ExecuteCmd (command name is in Function variable)
    // TODO: Suggest adding the following Cmd -- ExecuteCheckFor (command name is in function )
    // TODO: Suggest adding the following Cmd -- AutoDone -- This will do nothing forever....
    static public enum StepCmd{

        DriveStraight,
        DriveTurn,
        FollowAbsTrajectory,
        FollowRelTrajectory,
        AutoWait,
        FollowAbsTrajWithTimedCmd,
        FollowRelTrajWithTimedComd, 
        Collect,
        Shoot,
        Stop,
        BallsToAlliance,
        Climb,
        Descend

    }

    // TODO: add some documentation....
    private StepCmd Cmd;
    private double Timeout;
    private double Param1;
    private double Param2;
    private double Param3;
    private String Function;

    public AutoStep(StepCmd ParamCmd, double TO, double P1, double P2, double P3, String Funct){
        Cmd = ParamCmd;
        Timeout = TO;
        Param1 = P1;
        Param2 = P2;
        Param3 = P3;
        Function = Funct;
    }

    public StepCmd getCmd() {
        return Cmd;        
    }

    public double getTimeout(){
        return Timeout;
    }

    public double getParam1(){
        return Param1;
    }

    public double getParam2(){
        return Param2;
    }
    
    public double getParam3(){
        return Param3;
    }
    
    public String getFunction(){
        return Function;
    }
}
