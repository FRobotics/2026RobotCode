package Lib4150;

public class Lib4150RateOfChange3 {
    
    // --------class constants

    // --------class types

    // --------class variables
    private double locOldValue2 = 0.0;
    private double locOldValue1 = 0.0;
    private double locCurValue = 0.0;
    private double locOldTime2 = 0.0;
    private double locOldTime1 = 0.0;
    private double locCurTime = 0.0;

    private boolean locInitialized = false;

    // --------constructors
    public Lib4150RateOfChange3() {

    }

    // --------methods
    public double ExecROC3( double value, double elapsedTimeSec ) {
        if ( !locInitialized ) {
            locOldValue2 = value;
            locOldValue1 = value;
            locCurValue = value;
            locOldTime2 = elapsedTimeSec - 2.0;
            locOldTime1 = elapsedTimeSec - 1.0;
            locCurTime = elapsedTimeSec;
            locInitialized = true;
        }
        locOldValue2 = locOldValue1;
        locOldValue1 = locCurValue;
        locCurValue = value;
        locOldTime2 = locOldTime1;
        locOldTime1 = locCurTime;
        locCurTime = elapsedTimeSec;

        return ( locCurValue - locOldValue2 ) / ( locCurTime - locOldTime2 );

    }

    // --------setters

    // --------getters
}
