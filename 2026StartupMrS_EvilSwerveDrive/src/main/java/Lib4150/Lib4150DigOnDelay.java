package Lib4150;

public class Lib4150DigOnDelay {
    
    // --------class constants

    // --------class types
    private enum OnDelayState {
        WaitForOne,
        WaitForTimer,
        WaitForZero
    }

    // --------class variables
    private OnDelayState locState = OnDelayState.WaitForOne;
    private double locStartTime = 0.0;      // start of timer.
    private double locDelayTime = 0.0;      // how much to delay.

    // --------constructors
    public Lib4150DigOnDelay( double delayTime, double systemElapsedTimeSec  ) {
        this(delayTime, systemElapsedTimeSec, false );
    }

    public Lib4150DigOnDelay( double delayTime, double systemElapsedTimeSec, boolean initialState  ) {
        locDelayTime = delayTime;
        locStartTime = systemElapsedTimeSec;
        if ( initialState ) {
            locState = OnDelayState.WaitForZero;
        }
        else {
            locState = OnDelayState.WaitForOne;
        }
    }

    // --------methods
    public boolean ExecOnDelay( boolean value, double elapsedTimeSec ) {

        boolean retValue = false;


        switch ( locState ) {

            case WaitForOne:
                retValue = false;
                locStartTime = elapsedTimeSec;
                if ( value ) {
                    locState = OnDelayState.WaitForTimer;
                }
                break;
            
            case WaitForTimer:
                if ( !value ) {
                    retValue = false;
                    locStartTime = elapsedTimeSec;
                    locState = OnDelayState.WaitForOne;
                }
                else if ( locStartTime + locDelayTime >= elapsedTimeSec ) {
                    retValue = true;
                    locState = OnDelayState.WaitForZero;
                }
                else {
                    retValue = false;
                }
                break;

            case WaitForZero:
                if ( value ) {
                    retValue = true;
                }
                else {
                    locState = OnDelayState.WaitForOne;
                    locStartTime = elapsedTimeSec;
                    retValue = false;
                }
                break;
        }

        return retValue;
    }

    // --------setters
    public void setDelayTime( double delayTimeSec ) {
        locDelayTime = delayTimeSec;
    }

    // --------getters
    public double getDelayTIme() {
        return locDelayTime;
    }

    public double getRemainingSec(double systemElapsedTimeSec) {

        return Math.max( ( locStartTime + locDelayTime ) - systemElapsedTimeSec, 0.0 );
    }
}
