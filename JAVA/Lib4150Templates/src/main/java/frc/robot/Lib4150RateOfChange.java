// --------file: LIB4150RateOfChange.java

package frc.robot;

import edu.wpi.first.wpilibj.Timer;

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
public class Lib4150RateOfChange {

    // --------internal object variables.

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150RateOfChange() {
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double RateOfChangeCalc( double inputValue ) {
        return this.RateOfChangeCalc(inputValue, Timer.getFPGATimestamp() );
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double RateOfChangeCalc( double inputValue, double timeValue ) {
        return 0.0;
    }

}
