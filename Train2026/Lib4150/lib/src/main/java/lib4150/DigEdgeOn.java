// --------file: DigEdgeOn.java

package lib4150;

// =============================================================================================
/**
 *  The DigEdgeOn class implements edge on detection, used by sequential boolean logic
 *  where the output is true only when the input transitions from false to true. <br>     
 *<br>
 *  File:   DigEdgeOn.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          DigEdgeOn<br>
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
 *          This is a sample class.  It contains an overloaded constructor,
 *          and a method to execute the logic.<br>  
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    09/15/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-09-15
*/
public class DigEdgeOn {

    private boolean _loc_prevValue = false;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a digital edge on detector with default parameters:
    *                   initialValue: false 
    */
    public DigEdgeOn( ) {
        this( false ); 
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a digital edge on detector. 
    *                   
    *   @param  initialValue - boolean - the initial value of the output of this block.
    */
    public DigEdgeOn( boolean initialValue ) {
        this._loc_prevValue = initialValue;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   Executes the logic of the Edge On detector.
    *
    *   @param  currentValue - boolean - current state of the value to test.
    *   @return edgeValue - boolean - Returns true when the value transitions from false to true
    */
    public boolean EdgeOnExec( boolean currentValue ) {

        boolean returnValue = false;
        if ( currentValue && !this._loc_prevValue ) {
            returnValue = true;
        }
        this._loc_prevValue = currentValue;
        return returnValue;
    }

}
