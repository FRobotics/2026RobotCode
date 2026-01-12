// --------file: LIB4150RateOfChange.java

package frc.robot;

import edu.wpi.first.wpilibj.Timer;

// =============================================================================================
/**
 *  DESCRIPTION...
 *<br>
 *  File:   Lib4150RateOfChange.java<br>
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
public class Lib4150RateOfChange {
    private double prevValue = 0.0;
    private double prevTime = 0.0;
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
        double Speed = 0.0;
        double diffValue= inputValue - prevValue;
        double diffTime = timeValue - prevTime;
        if (diffTime == 0){
            return Speed;
        }
        
        else {
        return 0.0;
        }
    }

}
