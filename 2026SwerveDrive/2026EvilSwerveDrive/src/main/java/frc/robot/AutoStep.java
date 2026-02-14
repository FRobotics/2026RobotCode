package frc.robot;

public class AutoStep {

    
   
    // TODO: Suggest adding the following Cmd -- ExecuteCmd (command name is in Function variable)
    // TODO: Suggest adding the following Cmd -- ExecuteCheckFor (command name is in function )
    // TODO: Suggest adding the following Cmd -- AutoDone -- This will do nothing forever....
    static public enum StepCmd{

        DriveStraight(0),
        DriveTurn(1),
        FollowAbsTrajectory(2),
        FollowRelTrajectory(3),
        AutoWait(1),
        FollowAbsTrajWithTimedCmd(1),
        FollowRelTrajWithTimedComd(1), 
        Collect(1),
        Shoot(1),
        Stop(1),
        BallsToAlliance(1),
        Climb(1),
        Descend(1);

        private final int id;

        private StepCmd(int id){
            this.id= id;
        }

        public int getId(){
            return id;
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
