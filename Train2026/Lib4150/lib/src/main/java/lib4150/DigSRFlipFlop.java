
 package lib4150;

// =============================================================================================
/**
 *  The SRFlipFlop class implements a set/reset flip flop, used in sequential digital logic.<br>     
 *<br>
 *  File:   DigSRFlipFlop.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          DigSRFlipFLop<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
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
public class DigSRFlipFlop {

    private boolean _loc_prevValue = false;
    private boolean _loc_overrideOutput = false;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a set/reset flip flop with default parameters:
    *                   overrideValue: false, initialValue: false 
    */
    public DigSRFlipFlop( ) {
        this( false, false ); 
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a set/reset flip flop with provided parametrs: 
    *   @param  overrideValue - boolean - the value to output when both set and reset are true.
    *   @param  initialValue - boolean - the initial value of the output of this block.
    */
    public DigSRFlipFlop( boolean overrideValue, boolean initialValue ) {
        this._loc_overrideOutput = overrideValue;
        this._loc_prevValue = initialValue;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Sets the value to true.  Reset is implied to be false
    *   @return currentValue - boolean - current value of the flipflop.
    */
    public boolean SRFlipFlopSet() {
        this._loc_prevValue = true;
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Resets the value to false.  Reset is implied to be true
    *   @return currentValue - boolean - current value of the flipflop.
    */
    public boolean SRFlipFlopReset() {
        this._loc_prevValue = false;
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Executes the logic of the Set/Reset flip flop
    *   @param  setValue - boolean - state of the set input
    *   @param  resetValue - boolean - state of the reset input
    *   @return currentValue - boolean - current value of the flipflop.
    */
    public boolean SRFlipFlopExec( boolean setValue, boolean resetValue ) {

        if ( setValue && !resetValue ) {
            return this.SRFlipFlopSet();
        }
        else if ( !setValue & resetValue ) {
            return this.SRFlipFlopReset();
        }
        else if ( !setValue & !resetValue ) {
            return this._loc_prevValue;
        }
        else {
            this._loc_prevValue = this._loc_overrideOutput;
            return this._loc_prevValue;
        }
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Gets the current value of the flip flop
    *   @return currentValue - boolean - current value of the flipflop.
    */
    public boolean SRFlipFlopValue( ) {

        return this._loc_prevValue;
    }

}
