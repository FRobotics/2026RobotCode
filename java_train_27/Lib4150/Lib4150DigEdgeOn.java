package Lib4150;


// =============================================================================================
/**
 * This class implements a digital Edge On detector.  
 *<br>
 *  File:   Lib4150DigEdgeOn.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150DigEdgeOn<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *          For this to work correctly, the execute method needs to be called everytime
 *          the input value is acquired.<br>
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    02/25/2026  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2026-02-25
*/
public class Lib4150DigEdgeOn {
    
   // --------constants
    private boolean locLastValue = true;

    // --------constructor(s)

    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a digital edge on detector with a default initial previous value of TRUE.
     * 
     */
    public Lib4150DigEdgeOn(  ) {
        this(true);
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a digital edge on detector.  Desired initial previous value must be specified.
     * 
     * @param initialValue - boolean - initial previous value
     */
    public Lib4150DigEdgeOn( boolean initialValue ) {
        locLastValue = initialValue;
        return;
    }
    

    // ---------------------------------------------------------------------------------------------
    /**
     * Execute the digital edge on detecgtor.
     * cycle.
     * 
     * @param inputValue - boolean - input value to be checked for an on edge.
     * 
     * @return outputValue - boolean - returns true if an on edge is detected.
     */
    public boolean execEdgeOn( boolean inputValue ) {
        boolean outputValue = !locLastValue && inputValue;
        locLastValue = inputValue;
        return outputValue;
    }


    // ========getters



    // ========setters


}
