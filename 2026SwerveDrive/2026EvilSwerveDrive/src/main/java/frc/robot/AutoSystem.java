package frc.robot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

import Lib4150.Lib4150NetTableSystemSend;
// TODO: Remove unused imports.
import choreo.trajectory.SwerveSample;
import choreo.trajectory.Trajectory;
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

    private static HashMap<String, AutoRoutine> autoHashMap;


    // TODO: Add doc -- call this just prior to running ANY auto... Robot.java has a place for this.  It isn't the overall just once init.
    public static void ExecuteListInit(AutoRoutine autoToRun ) {

        // -------get the routine to run
        locExecRoutine = autoToRun;

        // -------init the index into our auto to run
        locExecIndex = 0;
        // -------init the last step we used
        locExecLastIndex = -1; // non-sense for first time

        // -------we should init this step...
        locExecInitStep = true;
        
    }

    public static String[] availableTrajectories() {
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

    public static void readFiles(String[] files){
        ArrayList<AutoRoutine> routines = new ArrayList<AutoRoutine>();
        for (String fileInstance : files){
                File file = new File(fileInstance);
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
                                        Funcname = datas[5];
                                        AutoStep newStep=new AutoStep(AutoStep.StepCommandCases(commandNum), timeout, param1, param2, param3, Funcname);
                                        newRoutine.add(newStep);
                                }
                        }
                        AutoRoutine routineToAdd = new AutoRoutine(fileInstance.split(AUTO_FILE_EXTENSION)[0], newRoutine);
                        routines.add(routineToAdd);
                        autoHashMap.put(fileInstance.split(AUTO_FILE_EXTENSION)[0], routineToAdd);
                
                } catch (Exception e) {

                        e.printStackTrace();
                }
        }
    }
      
    public AutoRoutine getAuto(String name)
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

        locExecDoNextStep = false;

        // do step
        switch (ourStep.getCmd()){
                case DriveStraight:
                        // locExecDoNextStep = AutoFunctions.autoDriveStraight();
                        break;
                
                case DriveTurn:
                        // locExecDoNextStep = AutoFunctions.autoDriveSpin();
                        break;

                case FollowAbsTrajectory:
                        locExecDoNextStep = TrajectorySystem.FollowTrajectory(locExecInitStep, ourStep.getFunction(), SystemElapsedTime);
                        break;

                case FollowRelTrajectory:
                        break;

                case AutoWait:
                        locExecDoNextStep = AutoFunctions.autoWait();
                        break;

                case FollowAbsTrajWithTimedCmd:
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
        
        // TODO: add locNTSend trigger update.

    } 

    // one time init when robot boots up.
    public static void init() {

        autoTimer.reset();
        autoTimer.start();

        // TODO: Read all the auto routines into cache here.

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("AutoSystem");
        // TODO: Add items to NT tables for debugging and watching autos.   Suggest  Step, Timeout, Parm1, Parm2, Parm3, Function.  Add public getters for these.
        //locNTSend.addItemBoolean(, AutoSystem::);
        //locNTSend.addItemDouble(, AutoSystem::);
        
        //locNTSend.triggerUpdate(, AutoSystem::);
        
    }

    //public static void executeLogic() {
    //
    //    locNTSend.triggerUpdate();
    //}

}
