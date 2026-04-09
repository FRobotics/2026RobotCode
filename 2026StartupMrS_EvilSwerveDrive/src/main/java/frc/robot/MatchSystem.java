package frc.robot;

import java.util.Optional;

import Lib4150.Lib4150NetTableSystemSend;

import com.ctre.phoenix6.SignalLogger;
import com.revrobotics.util.StatusLogger;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * Class to manage items related to the match and overall robot execution.  
 * Currently these include:
 *  - Current robot state - phase of startup
 *  - Getting match data - such as alliance color.
 *  - Managing data logging for robot testing and tuning.
 */
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

    //  -------- didn't understand what param was for, so removed...
    private static void setRed() {
        areRed = true;
        areBlue = false;
        return;

    }
    private static void setBlue()
    {
        areBlue = true;
        areRed = false;
        return;

    }
    private static void setUnknown()
    {
        areBlue = false;
        areRed = false;
        return;

    }
    /**
     * Are we part of the red alliance ?
     * 
     * @return - areRed - boolean - returns TRUE if we are on the RED alliance.
     */
    public static boolean isRed() {
        return areRed;
    }
    /**
     * Are we part of the blue alliance ?
     * 
     * @return - areBlue - boolean - returns TRUE if we are on the BLUE alliance.
     */
    public static boolean isBlue() {
        return areBlue;
    }

    /**
     * Initialize the MatchSystem.   Must be called just once during robot initialization.
     */
    public static void init() {

        // init network table
        locNTSend = new Lib4150NetTableSystemSend("MatchSystem");

        locNTSend.addItemBoolean("isRed", MatchSystem::isRed);
        locNTSend.addItemBoolean("isBlue", MatchSystem::isBlue);
        locNTSend.addItemDouble("RobotCodePhase", MatchSystem::getRobotCodePhaseNumber);
        
        // --------enable data logging --- disable this for competition.
        //DataLogManager.start();

        // --------disable automatic REV logging
        StatusLogger.disableAutoLogging();

        // --------disable automatic CTRE logging
        SignalLogger.enableAutoLogging(false);

        locNTSend.triggerUpdate();

        return;
    }

    /**
     * Run this when robot is disabled.  It will retrieve the alliance color and
     * store for later retrieval by all.
     */
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
    /**
     * Sets robot execute phase to "InitialStartup"
     */
    public static void setRobotPhaseInitialStartup() {
        locRobotCodePhase = RobotCodePhase.InitialStartup;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Gyro Calibrated"
     */
    public static void setRobotPhaseGyroCalibrated() {
        locRobotCodePhase = RobotCodePhase.GyroCalibrated;
        locNTSend.triggerUpdate();
        return;
    }  
    /**
     * Sets robot execute phase to "Trajectorie Read"
     */
    public static void setRobotPhaseTrajectoriesRead() {
        locRobotCodePhase = RobotCodePhase.TrajectoriesRead;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Autos Read"
     */
    public static void setRobotPhaseAutosRead() {
        locRobotCodePhase = RobotCodePhase.AutosRead;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Startup Complete"
     */
    public static void setRobotPhaseStartupComplete() {
        locRobotCodePhase = RobotCodePhase.StartupComplete;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Execute Disabled"
     */
    public static void setRobotPhaseDisabled() {
        locRobotCodePhase = RobotCodePhase.Disabled;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Auto Init Complete"
     */
    public static void setRobotPhaseAutoInitComplete() {
        locRobotCodePhase = RobotCodePhase.AutoInitComplete;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Running Auto"
     */
    public static void setRobotPhaseAutoRunning() {
        locRobotCodePhase = RobotCodePhase.AutoRunning;
        locNTSend.triggerUpdate();
        return;
    } 
    /**
     * Sets robot execute phase to "Running Teleop"
     */
    public static void setRobotPhaseTeleopRunning() {
        locRobotCodePhase = RobotCodePhase.TeleopRunning;
        locNTSend.triggerUpdate();
        return;
    } 

    /**
     * Get the current robot execute phase number - used to send to NT
     * 
     * @return phaseNumber - double - current robot execution phase number
     */
    public static double getRobotCodePhaseNumber() {
        return (double)locRobotCodePhase.ordinal();
    }
    
}
