package frc.robot;

import java.util.Optional;

import Lib4150.Lib4150NetTableSystemSend;

import com.revrobotics.util.StatusLogger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class MatchSystem {

    // type defs
    static private enum RobotCodePhase{

        InitialStartup, 
        GyroCalibrated, 
        TrajectoriesRead, 
        AutosRead, 
        StartupComplete, 
        Disabled,
        AutoInitComplete, 
        AutoRunning,
        TeleopRunning;
    }

    // constants
    private static final int SKIP_INTERVAL = 10;

    // class variables
    private static boolean areRed;
    private static boolean areBlue;
    private static int skipCounter = 0;
    private static Lib4150NetTableSystemSend locNTSend;
    private static RobotCodePhase locRobotCodePhase = RobotCodePhase.InitialStartup;


  
    private MatchSystem(){}

    private static void setRed() {
        areRed = true;
        areBlue = false;
        return;

    }
    private static void setBlue(){
        areBlue = true;
        areRed = false;
        return;
    }
    private static void setUnknown(){
        areBlue=false;
        areRed=false;
        return;

    }
    public static boolean isRed() {
        return areRed;
    }
    public static boolean isBlue() {
        return areBlue;
    }

    public static void init() {

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("MatchSystem");
        locNTSend.addItemBoolean("isRed", MatchSystem::isRed);
        locNTSend.addItemBoolean("isBlue", MatchSystem::isBlue);
        locNTSend.addItemDouble("RobotCodePhase", MatchSystem::getRobotCodePhaseNumber);

        // --------disable automatic REV logging
        StatusLogger.disableAutoLogging();

        locNTSend.triggerUpdate();
    }

    public static void disableExec() {

        skipCounter++;

        if ( skipCounter >= SKIP_INTERVAL) {
            skipCounter = 0;
            Optional<Alliance> ourAlliance = DriverStation.getAlliance();
            if ( ourAlliance.isPresent()) {
                switch ( ourAlliance.get() ) {
                    case Red:
                        setRed();
                        break;
                    case Blue:
                        setBlue();
                        break;
                    default:
                        setUnknown();
                        break;
                }
            }
            else {
                setUnknown();
            }
            locRobotCodePhase = RobotCodePhase.Disabled;
            locNTSend.triggerUpdate();

        }

        return;
    }


    // --------setters for robot phase.
    public static void setRobotPhaseInitialStartup() {
        locRobotCodePhase = RobotCodePhase.InitialStartup;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseGyroCalibrated() {
        locRobotCodePhase = RobotCodePhase.GyroCalibrated;
        locNTSend.triggerUpdate();
        return;
    }  
    public static void setRobotPhaseTrajectoriesRead() {
        locRobotCodePhase = RobotCodePhase.TrajectoriesRead;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseAutosRead() {
        locRobotCodePhase = RobotCodePhase.AutosRead;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseStartupComplete() {
        locRobotCodePhase = RobotCodePhase.StartupComplete;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseDisabled() {
        locRobotCodePhase = RobotCodePhase.Disabled;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseAutoInitComplete() {
        locRobotCodePhase = RobotCodePhase.AutoInitComplete;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseAutoRunning() {
        locRobotCodePhase = RobotCodePhase.AutoRunning;
        locNTSend.triggerUpdate();
        return;
    } 
    public static void setRobotPhaseTeleopRunning() {
        locRobotCodePhase = RobotCodePhase.TeleopRunning;
        locNTSend.triggerUpdate();
        return;
    }
    public static double getRobotCodePhaseNumber(){
        return (double)locRobotCodePhase.ordinal();
    }

    
}
