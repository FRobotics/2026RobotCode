package frc.robot;

public class AutoStep {

    
   
    static public enum StepCmd{

        DriveStraight,
        DriveTurn,
        FollowAbsTrajectory,
        FollowRelTrajectory,
        AutoWait,
        FollowAbsTrajWithTimedCmd,
        FollowRelTrajWithTimedComd,
        IntakUpOff,
        IntakeDownOff,
        IntakeDownOn

    }

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
