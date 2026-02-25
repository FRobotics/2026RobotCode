package frc.robot;

import java.util.Optional;

import Lib4150.Lib4150NetTableSystemSend;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class MatchSystem {
  
    private static final int SKIP_INTERVAL = 10;

    private static boolean areRed;
    private static boolean areBlue;
    private static int skipCounter = 0;
    private static Lib4150NetTableSystemSend locNTSend;


  
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
            locNTSend.triggerUpdate();

        }


        return;
    }

    
}
