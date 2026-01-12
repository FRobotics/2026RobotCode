// --------file: DigEdgeOff.java

package lib4150;

// =============================================================================================
/**
 *  The DigEdgeOff class implements edge off detection, used by sequential boolean logic
 *  where the output is true only when the input transitions from true to false. <br>     
 *<br>
 *  File:   DigEdgeOff.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          DigEdgeOff<br>
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
public class DigEdgeOff {

    private boolean _loc_prevValue = false;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a digital edge off detector with default parameters:
    *                   initialValue: false 
    */
    public DigEdgeOff( ) {
        this( false ); 
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a digital edge off detector. 
    *                   
    *   @param  initialValue - boolean - the initial value of the output of this block.
    */
    public DigEdgeOff( boolean initialValue ) {
        this._loc_prevValue = initialValue;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   Executes the logic of the Edge Off detector.
    *
    *   @param  currentValue - boolean - current state of the value to test.
    *   @return edgeValue - boolean - Returns true when the value transitions from true to false
    */
    public boolean EdgeOffExec( boolean currentValue ) {

        boolean returnValue = false;
        if ( !currentValue && this._loc_prevValue ) {
            returnValue = true;
        }
        this._loc_prevValue = currentValue;
        return returnValue;
    }

}
