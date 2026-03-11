package frc.robot;

public class AutoStep {

    
   
    // TODO: Suggest adding the following Cmd -- ExecuteCmd (command name is in Function string variable)
    // TODO: Suggest adding the following Cmd -- ExecuteCheckFor (command name is in function string variable )
    // TODO: Suggest adding the following Cmd -- AutoDone -- This will do nothing forever....similar to wait but with a forever timeout...
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
        Descend;
    }

    public static StepCmd StepCommandCases(int input){
        switch (input) {
            case 0:
                return StepCmd.DriveStraight;
            case 1:
                return StepCmd.DriveTurn;
            case 2:
                return StepCmd.FollowAbsTrajectory;
            case 3:
                return StepCmd.FollowRelTrajectory;
            case 4:
                return StepCmd.AutoWait;
            case 5:
                return StepCmd.FollowAbsTrajWithTimedCmd;
            case 6:
                return StepCmd.FollowRelTrajWithTimedComd;
            case 7:
                return StepCmd.Collect;
            case 8:
                return StepCmd.Shoot;
            case 9:
                return StepCmd.BallsToAlliance;
            case 10:
                return StepCmd.Climb;
            case 11:
                return StepCmd.Descend;
            default:
                return StepCmd.AutoWait;
        
        }
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
