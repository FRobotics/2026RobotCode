package frc.robot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

import Lib4150.Lib4150NetTableSystemSend;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;

public class AutoSystem {

    private static Lib4150NetTableSystemSend locNTSend;

    private static int locExecIndex = 0;
    private static int locExecLastIndex = -1;
    private static boolean locExecInitStep = true;
    private static AutoRoutine locExecRoutine;
    private static Timer autoTimer = new Timer();
    private static boolean locExecDoNextStep = false;
    private static String AUTO_FILE_EXTENSION = ".csv";
    private static File AUTO_DIR = new File(Filesystem.getDeployDirectory(), "auto");
    private static double locStepTime = 0.0;

    private static HashMap<String, AutoRoutine> autoHashMap;

    /**
     * ExecuteListInit - call this prior to running any and each auto routine.  
     * 
     * @param autoRun -String - The name of the auto routine to run.
     */
    public static void ExecuteListInit(String autoRun ) {

        // -------find our auto to run
        AutoRoutine autoToRun = null;
        autoToRun = AutoSystem.getAuto( autoRun );

        // -------get the routine to run
        locExecRoutine = autoToRun;

        // -------init the index into our auto to run
        locExecIndex = 0;
        // -------init the last step we used
        locExecLastIndex = -1; // non-sense for first time

        // -------we should init this step...
        locExecInitStep = true;

        // -------tell system auto init is done.
        MatchSystem.setRobotPhaseAutoInitComplete();
        
    }

    public static String[] availableAutos() {
        List<String> autos = new ArrayList<>();
        File[] files = AUTO_DIR.listFiles();
        if (files != null) {
                for (File file : files) {
                        if (file.getName().endsWith(AUTO_FILE_EXTENSION)) {
                                autos.add(
                                file.getName().substring(0, file.getName().length() - AUTO_FILE_EXTENSION.length()));
                        }
                 }
        }
        return autos.toArray(new String[0]);
    }

    public static String[] readFiles(String[] files){
        ArrayList<AutoRoutine> routines = new ArrayList<AutoRoutine>();
        ArrayList<String> routineNames = new ArrayList<String>();
        for (String fileInstance : files){
                File file = new File(AUTO_DIR, fileInstance + AUTO_FILE_EXTENSION);
                try (Scanner myReader = new Scanner(file)) {
                        ArrayList<AutoStep> newRoutine= new ArrayList<AutoStep>();
                        while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                if (!data.startsWith("#")){
                                        String[] datas = data.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                                        int commandNum;
                                        double timeout;
                                        double param1;
                                        double param2;
                                        double param3;
                                        String Funcname;
                                        commandNum = (int)Double.parseDouble(datas[0]);
                                        timeout = Double.parseDouble(datas[1]);
                                        param1 = Double.parseDouble(datas[2]);
                                        param2 = Double.parseDouble(datas[3]);
                                        param3 = Double.parseDouble(datas[4]);
                                        // Regex explanation:
                                        // ^\" : Matches a double quote at the start of the string.
                                        // (.*) : Captures any character (0 or more times) as group 1.
                                        // \"$ : Matches a double quote at the end of the string.
                                        // If the pattern matches, it is replaced by the content of group 1 ($1).
                                        Funcname = datas[5].trim().replaceAll("^\"(.*)\"$", "$1");
                                        AutoStep newStep=new AutoStep(AutoStep.StepCommandCases(commandNum), timeout, param1, param2, param3, Funcname);
                                        newRoutine.add(newStep);
                                }
                        }
                        AutoRoutine routineToAdd = new AutoRoutine(fileInstance.split(AUTO_FILE_EXTENSION)[0], newRoutine);
                        routines.add(routineToAdd);
                        autoHashMap.put(fileInstance.split(AUTO_FILE_EXTENSION)[0], routineToAdd);
                        System.out.println("Auto - Added routine:"+fileInstance.split(AUTO_FILE_EXTENSION)[0]);
                        routineNames.add(fileInstance.split(AUTO_FILE_EXTENSION)[0]);

                } catch (Exception e) {

                        e.printStackTrace();
                }
        }
        return routineNames.toArray(new String[0]);
    }
      
    public static AutoRoutine getAuto(String name)
    {
        return autoHashMap.get(name);
    }

    // this gets called every 20 ms.   execlistinit needs to be called first to set things up.
    public static void ExecuteList(double SystemElapsedTime){

            
        AutoStep ourStep = locExecRoutine.getStep(locExecIndex);


        // --------is this the first time for this step
        if ( locExecIndex != locExecLastIndex ) {
                locExecInitStep = true;
                locExecLastIndex = locExecIndex;
        }
        else {
                locExecInitStep = false;
        }

        // First time in this step.  if so reset timer.
        if ( locExecInitStep ) {
                autoTimer.reset();
                autoTimer.start();
        }

        locStepTime = autoTimer.get();

        locExecDoNextStep = false;

        // do step
        switch (ourStep.getCmd()){

                case DriveStraight:
                        // -- p1 - how far m, p2 - how fast m/sec, p3 angle deg
                        locExecDoNextStep = AutoFunctions.autoDriveStraight(locExecInitStep, ourStep.getParam1(),ourStep.getParam2(),ourStep.getParam3());
                        break;
                
                case DriveTurn:
                        // -- p1 - how far - deg, p2 - how fast deg/sec
                        locExecDoNextStep = AutoFunctions.autoDriveSpin(locExecInitStep, ourStep.getParam1(),ourStep.getParam2());
                        break;

                case FollowAbsTrajectory:
                        // --------this will also issue events.
                        locExecDoNextStep = TrajectorySystem.FollowTrajectory(locExecInitStep, ourStep.getFunction(), SystemElapsedTime);
                        break;

                case FollowRelTrajectory:
                        break;

                case AutoWait:
                        locExecDoNextStep = AutoFunctions.autoWait();
                        break;

                case FollowAbsTrajWithTimedCmd:
                        locExecDoNextStep = TrajectorySystem.FollowTrajectory(locExecInitStep, ourStep.getFunction(), SystemElapsedTime);
                        break;

                case FollowRelTrajWithTimedComd:
                        break;

                case Collect:
                        SupervisoryCmds.Collecting();
                        locExecDoNextStep = true;
                        break;

                case Shoot:
                        SupervisoryCmds.Shooting();
                        locExecDoNextStep = true;
                        break;

                case Stop:
                        SupervisoryCmds.StopAction();
                        locExecDoNextStep = true;
                        break;

                case BallsToAlliance:
                        SupervisoryCmds.BallsToAlliance();
                        locExecDoNextStep = true;
                        break;
                
                case Climb:
                        SupervisoryCmds.Climb();
                        locExecDoNextStep = true;
                        break;
                
                case Descend:
                        SupervisoryCmds.Descend();
                        locExecDoNextStep = true;
                        break;
                
                case Defense:
                        SupervisoryCmds.Defense();
                        locExecDoNextStep = true;
                        break;
        }

        // did we time out.
        if ( ourStep.getTimeout() > 0.0 ) {
                if ( autoTimer.hasElapsed(ourStep.getTimeout()) ) { 
                        locExecDoNextStep = true;
                }
        }

        // should we do next step
        if ( locExecDoNextStep ) {
                locExecIndex++;
        }
        // --------tell system we are running autos
        MatchSystem.setRobotPhaseAutoRunning();
        
        locNTSend.triggerUpdate();

    } 

    // one time init when robot boots up.
    public static String[] init() {

        autoTimer.reset();
        autoTimer.start();

        autoHashMap = new HashMap<String,AutoRoutine>();

        // load all autos into cache
        String[] readAutos = AutoSystem.readFiles(AutoSystem.availableAutos());
        // --------tell match system trajectories are read
        MatchSystem.setRobotPhaseAutosRead();

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AutoSystem");

        locNTSend.addItemDouble("Step", AutoSystem::getStep);
        locNTSend.addItemDouble("Timeout", AutoSystem::getTimeout);
        locNTSend.addItemDouble("Parm1", AutoSystem::getParm1);
        locNTSend.addItemDouble("Parm2", AutoSystem::getParm2);
        locNTSend.addItemDouble("Parm3", AutoSystem::getParm3);
        locNTSend.addItemString("function", AutoSystem::getFunction);
        locNTSend.addItemDouble("StepTime", AutoSystem::getLocStepTime);
        //locNTSend.addItemBoolean(, AutoSystem::);
        //locNTSend.addItemDouble(, AutoSystem::);
        
        locNTSend.triggerUpdate();

        return readAutos;
        
    }

    public static double getStep(){
        double returnGetStepValue = 0.0;
        if ( locExecRoutine != null ) {
                returnGetStepValue =  (double)locExecRoutine.getStep(locExecIndex).getCmd().ordinal();
        }
        return returnGetStepValue;
    }
    public static double getTimeout(){
        double returnGetTimeoutValue = 0.0;
        if (locExecRoutine != null){ 
        returnGetTimeoutValue = locExecRoutine.getStep(locExecIndex).getTimeout();
        }
        return returnGetTimeoutValue;
    }
    public static double getParm1(){
        double returnGetParm1Value = 0.0;
        if (locExecRoutine != null){
                returnGetParm1Value = locExecRoutine.getStep(locExecIndex).getParam1();
        }
        return returnGetParm1Value;
    }
    public static double getParm2(){
        double returnGetParm2Value = 0.0;
        if (locExecRoutine != null){
                returnGetParm2Value = locExecRoutine.getStep(locExecIndex).getParam2();
        }
        return returnGetParm2Value;
    }
    public static double getParm3(){
        double returnGetParm3Value = 0.0;
        if (locExecRoutine != null){
                returnGetParm3Value = locExecRoutine.getStep(locExecIndex).getParam3();
        }
        return returnGetParm3Value;
    }
    public static String getFunction(){
        String returnGetFunctionValue = "";
        if (locExecRoutine != null){
                returnGetFunctionValue = locExecRoutine.getStep(locExecIndex).getFunction();
        }
        return returnGetFunctionValue;
    }
    public static double getLocStepTime(){
        return locStepTime;
    }

    //public static void executeLogic() {
    //
    //    locNTSend.triggerUpdate();
    //}

}
