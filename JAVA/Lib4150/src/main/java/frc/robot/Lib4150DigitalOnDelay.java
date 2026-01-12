// --------file: LIB4150DigitalOnDelay.java

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
public class Lib4150DigitalOnDelay {

    // --------internal object variables.


    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150DigitalOnDelay( double delayTimeSec ) {
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setDelayTime( double delayTimeSec ) {
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getRemainingTime() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean OnDelayExec( boolean input ) {
        return this.OnDelayExec(input, Timer.getFPGATimestamp() );
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean OnDelayExec( boolean input, double elapsedTimeSec ) {
        return false;
    }
}
