package frc.robot;

/**
 * This is a data only class (mostly) containing choreo events to execute. 
 */
public class TrajectoryEvent {

    private double locTimeOffsetSec = 0.0;
    private String locEventName = "";

    /**
     * Construct a Trajectory Event.
     * 
     * @param parmTimeOffsetSec
     * @param parmEventName
     */
    public TrajectoryEvent( double parmTimeOffsetSec, String parmEventName ) {
        locEventName = parmEventName;
        locTimeOffsetSec = parmTimeOffsetSec;
        return;
    }

    /**
     * See if the time to execute this event has arriaved.
     * @param parmTimeToCheck - double - Time to check.
     * @return - boolean - returns TRUE if it is time to execute this event.
     */
    public boolean hasTimeElapsed( double parmTimeToCheck ) {
        return ( parmTimeToCheck >= locTimeOffsetSec );
    }

    /**
     * Get time offset seconds
     * @return - double - time offset in seconds.
     */
    public double getTimeOffsetSec( ) {
        return locTimeOffsetSec;
    }

    /**
     * get event name string
     * @return - String - event name
     */
    public String getEventName() {
        return locEventName;
    } 


}
