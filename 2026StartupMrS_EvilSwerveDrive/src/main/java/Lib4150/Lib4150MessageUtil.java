package Lib4150;

import edu.wpi.first.hal.DriverStationJNI;
// import edu.wpi.first.wpilibj.DriverStation;
/**
 * Utility class to allow us to control what messages sent to the console are like.
 */
public class Lib4150MessageUtil {

    /**
     * Can't call constructor.  Utility static class..
     */
    private Lib4150MessageUtil() {
        return;
    }

    /**
     * send an informational message to console.   Should not be used in control loops!
     * 
     * @param message - String - message to send.
     */
    static public void SendInfo(String message ) {
        DriverStationJNI.sendConsoleLine("Info:"+message+"\n");
        // DriverStation.reportWarning("Info:"+message+"\n", false);

    }
    /**
     * send an error message to console.   Should not be used in control loops!
     * 
     * @param message - String - message to send.
     */

    static public void SendError(String message ) {
        DriverStationJNI.sendConsoleLine("Error:"+message+"\n");
        // DriverStation.reportError(message+"\n", false);
    }

}
