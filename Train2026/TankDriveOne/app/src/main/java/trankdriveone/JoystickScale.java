// --------file JoystickScale.java

package trankdriveone;


// =============================================================================================
/**
 *  The JoystickScale.jave class adds a deadband around 0 for the joystick.  It also squares
 *  the value to allow easier driving.
 *<br>
 *  File:   JoystickScale.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          JoystickScale( double deadband )
 *          Execute<br>
 *<br>
 *  Depends on:<br>
 *          none - no external libraries required<br>
 *<br>
 *  Operating system specifics:<br>
 *          None - transportable<br>
 *<br>
 *  Notes:<br>
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    09/28/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-09-28
*/
public class JoystickScale {

    // --------local variables.
    private double localDeadBand = 0.0;
    private double localSlope = 0.0;
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a JoystickScale object.  
    *
    * @param deadBand - double - the deadband for this joystick. 
    */
    public JoystickScale(  double deadBand ) {
        this.localDeadBand = deadBand;
        this.localSlope = ( 1.0 / ( 1.0 - deadBand ));
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Execute - perform the scaling for this joystick.  
    *
    * @param rawJoyStickValue - double - the raw value of the joystick (+/- 1.0)
    * @return scaledJoyStickValue - double - the resulting scaled joystick value (+/- 1.0)
    */
    public double Execute( double rawJoyStickValue ) {

        double outputValue = 0.0;

        if ( rawJoyStickValue > this.localDeadBand) {
            outputValue = ( rawJoyStickValue - this.localDeadBand )  * this.localSlope;
            outputValue *= outputValue;     // square joystick value.
        }
        else if ( rawJoyStickValue < -this.localDeadBand ) {
            outputValue = ( rawJoyStickValue + this.localDeadBand ) * this.localSlope;                
            outputValue *= ( -1.0 * outputValue );     // square joystick value.
        }
        else {
            outputValue = 0.0;
        }

        return outputValue;

    }


}
