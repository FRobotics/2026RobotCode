// --------file: Lib4150DigitalEdgeChange.java

package frc.robot;

// =============================================================================================
/**
 *  implements a digital edge change detection
 *<br>
 *  File:   Lib4150DigitalEdgeChange.java<br>
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
 *  1.00    11/09/2025  Ezra Duchamp     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Ezra Duchamp
 * @version    1.0
 * @since      2025-11-09
*/
public class Lib4150DigitalEdgeChange {

    // --------internal object variables.
    private boolean locPrevValue = false;
    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150DigitalEdgeChange( ) {
        this( false ); 
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150DigitalEdgeChange( boolean initialValue ) {
        locPrevValue = initialValue;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean EdgeChangeExec( boolean currentValue ) {
        boolean returnValue = false;
        returnValue = currentValue != locPrevValue;
        locPrevValue = currentValue;

      
        return returnValue; // replace when adding real code...
    }

}

