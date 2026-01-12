// --------file: Lib4150DigitalSetResetFlipFlop.java

package frc.robot;

// =============================================================================================
/**
 *  DESCRIPTION...
 *<br>
 *  File:   FILENAME.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          CLASSNAME<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *          NOTES_IF_ANY.
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    XX/XX/2025  NAME     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     NAME
 * @version    1.0
 * @since      XXXX-XX-XX
*/
public class Lib4150DigitalSetResetFlipFlop {

    // --------internal object variables.

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150DigitalSetResetFlipFlop( ) {
        this( false, false ); 
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150DigitalSetResetFlipFlop( boolean overrideValue, boolean initialValue ) {
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean SRFlipFlopSet() {
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean SRFlipFlopReset() {
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean SRFlipFlopExec( boolean setValue, boolean resetValue ) {
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean getValue( ) {
        return false;
    }

}
