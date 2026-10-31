// --------file: LIB4150AdvancedPID.java

package Lib4150;

import edu.wpi.first.math.MathUtil;

// =============================================================================================
/**
 *  Advanced PID controller class.  Contains a number of enhancements over the WPILIB PID, 
 *  including: auto/manual mode, online tuning without I term reset, I windup remediation.
 * 
 *<br>
 *  File:   filename.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          filename<br>
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
 *  1.00    11/01/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-11-01
*/
public class Lib4150AdvancedPID {

    // --------some internal constants
    private static final double BIG_POS_NUMB = 9.9E30; 
    private static final double BIG_NEG_NUMB = -9.9E30; 

    // --------internal object variables
    private double locPV = 0.0;
    private double locSP = 0.0;
    private double locPosError = 0.0;   
    private double locVelError = 0.0;
    private double locPrevPosError = 0.0;
    private boolean locAntiWindupActive = false;
    private double locPrevDerivative1 = 0.0;
    private double locPrevDerivative2 = 0.0;
    private double locPrev_F_P_1 = 0.0;
    private double locPrev_F_P_2 = 0.0;
    private double locTotalIntegral = 0.0;
    private double locLastSystemTime = 0.0;
    private double locPeriod = 0.020;

    // --------saved between execution steps

    // --------tuning values
    private double locKp = 0.0;
    private double locKi = 0.0;
    private double locKd = 0.0;
    private double locKf = 1.0;
    private double locKn = 1.0;
    private double locMinIntegral = BIG_NEG_NUMB;
    private double locMaxIntegral = BIG_POS_NUMB;
    private double locMinInput = BIG_NEG_NUMB;
    private double locMaxInput = BIG_POS_NUMB;
    private double locIntegralZone = BIG_POS_NUMB;
    private boolean locContinousInput = false;
    private double locPosTolerance = 0.0;
    private double locVelTolerance = BIG_POS_NUMB;
    private double locMinOutput = BIG_NEG_NUMB;
    private double locMaxOutput = BIG_POS_NUMB;
    private boolean locFilterDerivative = false;


    // ---------------------------------------------------------------------------------------------
    /**
     * Creates a Lib4150AdvancedPID object with the provided tuning parameters. 
     * 
     * @param Kp - double - Proportional gain constant.
     * @param Ki - double - Integral gain constant.
     * @param Kd - double - Derivative gain constant.
     * @param Kf - double - Feedforware gain constant.
     * @param Kn - double - Normalization gain constant.  When this is 1.0, tuning constants have
     * "CO" relative units, otherwise tuning constants have "PV" relative units.
     */
    public Lib4150AdvancedPID( double Kp, double Ki, double Kd, double Kf, double Kn ) {
        this.setPIDFN(Kp, Ki, Kd, Kf, Kn);
        this.reset();
    }


    // --------CONTROL / STATUS ROUTINES

    // ---------------------------------------------------------------------------------------------
    /**
     * Resets the PID controller.  All saved errors, integrals, and other values used to calculate
     * output are reset to zero.
     */
    public void reset() {
        locPrevPosError = 0.0;
        locTotalIntegral = 0.0;
        locPrevDerivative1 = 0.0;
        locPrevDerivative2 = 0.0;
        locPrev_F_P_1 = 0.0;
        locPrev_F_P_2 = 0.0;
        locPosError = 0.0;
        locVelError = 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Determine if the controller is within tolerance.  Returns true when the error has been
     * within tolerance.  Note that the error is sometimes referred to as "position" error.  However
     * the error units are often not distance units.  The error units could be speed, position, or 
     * something else.
     * 
     * @return atSetpoint - boolean - TRUE when error is within tolerance.
     */
    public boolean atSetpoint() {
        return Math.abs(locPosError) < locPosTolerance && Math.abs(locVelError) < locVelTolerance;
    }


    // --------Execute routine(s)

    // ---------------------------------------------------------------------------------------------
    /**
     * Calculate the control output CO for this controller provided the process variable (PV), 
     * setpoint (SP), and feedforward (FF).  Auto/Manual status and system elapsed time (seconds)
     * must also be provided.
     *
     * This control uses this classis PIDF algorithm to calculate the control output:
     *       CO = Kf * FF + Kp * error + Ki * integral of error + Kd * derivative of error
     * 
     * Anti-integral windup is performed so the output limits are not exeeded.  This speeds the response
     * of the controller when integral saturation occurs.  
     * 
     * In manual mode the output is calculated as:
     *       CO = Kf * FF
     * In manual mode an integral term is calculated so that when transitioning back to auto mode, the
     * output is bumpless (a step change in the output doesn't occur).
     * 
     * @param PV - double - Process variable (measured) value, in "PV" units.
     * @param SP - double - Setpoint value, in "PV" units.
     * @param FF - double - Feedforward value, in "PV" units.
     * @param auto - boolean - TRUE when in auto mode.  FALSE for manual mode.
     * @param systemTime - double - 
     * 
     * @return CO - double - calculated control output value.
     */
    public double AdvPIDExec( double PV, double SP, double FF, boolean auto, double systemTime ) {
        double co = 0;
        double integralSeg = 0.0;
        double derivative = 0.0;
        boolean windup1 = false;

        this.setSetpoint(SP);
        locPV = PV;

        // --------ensure we have a reasonable period.  Nothing should be executing faster than 2 milliseconds.
        locPeriod = Math.max( 0.002, systemTime - locLastSystemTime);   

        // --------calculate error
        if ( locContinousInput ) {
            double lastErr = locPosError;
            locPosError = Lib4150AdvancedPID.modulusError( locSP, locPV, locMinInput, locMaxInput);    
            locVelError = Lib4150AdvancedPID.modulusError( locPosError, lastErr, locMinInput, locMaxInput ) 
                            / locPeriod;
        }
        else {
            double lastErr = locPosError;
            locPosError = locSP - locPV;
            locVelError = ( locPosError - lastErr) / locPeriod;
        }

        // --------calculate derivative
        double derivative0 = (locPosError - locPrevPosError) / locPeriod; 
        if ( locFilterDerivative ) {
            derivative = (derivative0 + locPrevDerivative1 + locPrevDerivative2) / 3.0; 
        }
        else {
            derivative = derivative0;
        }
        // --------save old derivatives.
        locPrevDerivative2 = locPrevDerivative1;
        locPrevDerivative1 = derivative0;

        // --------auto mode calculations.
        if ( auto ) {

            // --------integral term
            // --------are we outside the error integral zone.. then no integral
            // --------or if integral gain is zero.
            if ( ( Math.abs(locPosError) > locIntegralZone) || (locKi == 0.0) ) {
                locTotalIntegral = 0.0;
                integralSeg = 0.0;
                windup1 = false;
            }
            // --------calculate integral.
            else {
                // --------integrate by trapezoids. Add this trapezoid to the total.  
                integralSeg = ( locPosError + locPrevPosError ) * 0.5 * locPeriod * locKi;
                locTotalIntegral = MathUtil.clamp( integralSeg+locTotalIntegral, locMinIntegral, locMaxIntegral);
                windup1 = true;
            }

            // --------calculate output
            double co_fp = FF * locKf + locPosError * locKp;
            double co_fpi = co_fp + locTotalIntegral;
            co = MathUtil.clamp( co_fpi + derivative * locKd, locMinOutput, locMaxOutput ); 

            // --------calculate anti integral windup.
            // --------do it when -- have an integral and ff+prop+int is outside of 
            // --------output range and ff+prop is within output range
            boolean windup2 = !Lib4150AdvancedPID.inRange(co_fpi, locMinOutput, locMaxOutput);
            boolean windup3 = Lib4150AdvancedPID.inRange(co_fp, locMinOutput, locMaxOutput);

            // --------see if we need to do anti-windup logic...
            if ( windup1 && windup2 && windup3 ) {
                // --------adjust total integral...
                // TODO: Antiwindup assumes MinOutput and MaxOutput are opposites!
                locTotalIntegral = Math.signum(locTotalIntegral) * 
                        ( Math.min( Math.abs(locTotalIntegral), 
                            Math.max( 0.0, 
                                Math.abs( locMaxOutput ) - Math.abs( ( locPrev_F_P_2 + locPrev_F_P_1 + co_fp ) / 3.0 ) ) 
                            ) 
                        );

                locAntiWindupActive = true;
            }
            // --------dont need to do anti windup logic.
            else {
                locAntiWindupActive = false;
            }
            locPrev_F_P_2 = locPrev_F_P_1;
            locPrev_F_P_1 = co_fp;
        }

        // --------manual mode calculations
        else {
            double prop_term = locPosError * locKp;
            double deriv_term = derivative * locKd;
            double pd_term = prop_term + deriv_term;

            locPrev_F_P_2 = locPrev_F_P_1;
            locPrev_F_P_1 = FF * locKf + prop_term;
            locAntiWindupActive = false;

            co = MathUtil.clamp( FF * locKf, locMinOutput, locMaxOutput );

            // --------save total integral for bumpless transfer to auto.
            if ( Math.abs(locKi) < 1.0e-9 ) {
                locTotalIntegral = 0.0;
            }
            else {
                locTotalIntegral = co - pd_term; 
            }
        }

        return co;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Utility function to determine if a value is within the range of values provided.
     * 
     * @param value - double - value to check.
     * @param min - double - minmum limit value
     * @param max - double - maximum limit value.
     * 
     * @return inRange - boolean - TRUE when input value isn't outside the limit values.
     */
    static public boolean inRange( double value, double min, double max ) {
        return value >= min && value <= max; 
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Utility function to calculate the modulus error for a system with continuous PV and SP.  If 
     * the PV or SP values overflow their limits, their modulus values are calculated and used.  The 
     * calcualted error is also considered to be a modulus value.
     * 
     * @param Reference_SP - double - Current setpoint value.
     * @param Measurement_PV - double - Current process variable value.
     * @param minimumInput - double - Minimum input limit value
     * @param maximumInput - double - Maximum input limit value.
     * 
     * @return modulusError - double - Calculated error value.
     */
    static public double modulusError( double Reference_SP, double Measurement_PV, double minimumInput, double maximumInput ) {

        double retValue = 0.0;

        double range = maximumInput - minimumInput;
        double useSP = ( Reference_SP - minimumInput ) % range;
        double usePV = ( Measurement_PV - minimumInput ) % range;
        double modErr = useSP - usePV;

        double test = ( range * 0.5 );

        if ( modErr > test ) {
            retValue = modErr - range;
        }
        else if ( modErr < -test ) {
            retValue = modErr + range;
        }
        else {
            retValue = modErr;
        }
        return retValue;
    }

    // --------GETTERS

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the value of Ki, which is the integral gain constant.
     * 
     * @return Ki - double - Integral gain constant.
     */
    public double getKi() {
        return locKi / locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the value of Kp, which is the proportional gain constant.
     * 
     * @return Kp - double - Proprotional gain constant.
     */
    public double getKp() {
        return locKp / locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the value of Kd, which is the derivative gain constant.
     * 
     * @return Kd - double - Derivative gain constant.
     */
    public double getKd() {
        return locKd / locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the value of Kf, which is the feedforward gain constant.
     *   
     * @return Kf - double - Feedforward gain constant.
     */
    public double getKf() {
        return locKf / locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns value of Kn, which is the normalization gain factor.  This constant is normally 
     * calculated as maximum output divided by process variable value at maximum output.  This can 
     * be set to 1.0 to not do normalization.  Using normalization can help simplify determining
     * the other gain constants.
     * 
     * @return  Kn - double - Normalization constant.
     */
    public double getKn() {
        return locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the last execution period in seconds.  This is the difference in time between the 
     * most recent and previous PID execute calls.  This is the same ass getLastPeriod.
     * 
     * @return lastPeriod - double - Last period in seconds.
     */
    public double getPeriod() {
        return this.getLastPeriod();
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the last execution period in seconds.  This is the difference in time between the 
     * most recent and previous PID execute calls.
     * 
     * @return lastPeriod - double - Last period in seconds.
     */
    public double getLastPeriod() {
        return locPeriod;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the current controller control error value.  (Sometimes this is also reffered to as
     * position error. )
     * 
     * @return controlError - double - current control error.
     */
    public double getError() {
        return locPosError;
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the current controller control error value.  (Sometimes this is also reffered to as
     * position error. ).  This function is the same as getError().
     * 
     * @return controlError - double - current control error.
     */
    public double getPositionError() {
        return this.getError();
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the rate of error change.  This is calculated as ( current Error - last error ) / period.
     * This is sometimes referred to as Velocity Error, which can be confusing since the units are not
     * likely velocity units.
     * 
     * @return  errorChangeRate - double - Rate of change of the calcualted error value.
     */
    public double getVelocityError() {
        return locVelError;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns the current setpoint value.
     * 
     * @return setpoint - double - current setpoint value.
     */
    public double getSetpoint() {
        return locSP;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns TRUE when the continuous input configuration parameters is TRUE.
     * 
     * @return continuousInput - boolean - Returns TRUE when continuous input configuration is set.
     */
    public boolean getContinuousInput() {
        return locContinousInput;
    }

    /**
     * Get the minimum input limit value.  This is only used when continuous input is set to TRUE.
     * 
     * @return  minInputLimit - double - Returns the current value of the minimum input limit.
     */
    public double getMinInput() {
        return locMinInput;
    }

    /**
     * Get the maximum input limit value.  This is only used when continuous input is set to TRUE.
     * 
     * @return  maxInputLimit - double - Returns the current value of the maximum input limit.
     */
    public double getMaxInput() {
        return locMaxInput;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns TRUE when integral anti-windup is being performed.
     * 
     * @return antiWindupActive - boolean - TRUE when anti-windup logic is changing the integral total.
     */
    public boolean getAntiWindupActive() {
        return locAntiWindupActive;
    }



    // --------SETTERS

    // ---------------------------------------------------------------------------------------------
    /**
     * Set the derivative gain constant, Kd.  This value is multiplied by the normalization
     * constant, Kn before being applied.  If Kn is 1.0, then this is in "output" relative units.  
     * Otherwise this is in "PV" relative units.
     * 
     * @param Kd - double - New derivative gain constant.
     */
    public void setKd( double Kd) {
        locKd = Kd * locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set the proportional gain constant, Kp.  This value is multiplied by the normalization
     * constant, Kn before being applied.  If Kn is 1.0, then this is in "output" relative units.  
     * Otherwise this is in "PV" relative units.
     * 
     * @param Kp - double - New proportional gain constant.
     */
    public void setKp( double Kp) {
        locKp = Kp * locKn;        
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set the integral gain constant, Ki.  This value is multiplied by the normalization
     * constant, Kn before being applied.  If Kn is 1.0, then this is in "output" relative units.  
     * Otherwise this is in "PV" relative units.
     * 
     * @param Ki - double - New integral gain constant.
     */
    public void setKi( double Ki) {
        locKi = Ki * locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Sets if derivative filtering should be performed when calculting the derivative value to
     * add to the control output.  When TRUE the derivative is calculated as the average of the 
     * last three raw derivative values.   This defaults to FALSE if not set.
     * 
     * @param filterDerivative - boolean - If TRUE causes derivative to be filtered.
     */
    public void setDerivativeFilter( boolean filterDerivative ) {
        locFilterDerivative = filterDerivative;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Sets the minimum and maximum allowed integral term.  This is in "CO" units.  Settings these
     * values to a fraction of the minimum and maximum allowed output can help to minimize integral
     * windup, which most often occurs after a significant setpoint change causing the error to be
     * relatively large for a relatively long period of time.  Note that this does not prevent
     * integral windup when the setpoint and output are close to their high or low limits.
     * These default to very large (almost infinite) positive and negative values if not set. 
     * 
     * @param minIntegral - double - Minimum integral output value.
     * @param maxIntegral - double - Maximum integral output value.
     */
    public void setIntegralRange( double minIntegral, double maxIntegral) {
        locMinIntegral = minIntegral;
        locMaxIntegral = maxIntegral;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Sets the integral zone.  The absolute value of the calculated error must be within this value
     * to allow the PID to calculate and use an integral term.  This can help prevent the integral
     * term from winding up when the setpoint is moving, but the process variable is lagging behind.
     * If not specified, the default value is 9.9E+30.  This value is specified in "PV" relative units.
     * This defaults to a vary large (almost infinite) value if not set.
     * 
     * @param integralZone - double - New value for integral zone.
     */
    public void setIntegralZone( double integralZone ) {
        locIntegralZone = integralZone;

    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Sets the minimum and maximum value of the controller output (CO).  Setting these allows
     * integral windup logic to operate properly, in additional forcing the output to be within
     * these limits.  If not set these values default to very large (almost infinite) negative and
     * positive values.
     * 
     * @param minOutput - double - Sets smallest allowed value for controller output (CO)
     * @param maxOutput - double - Sets largest allowed value for controller output (CO)
     */
    public void setOutputLimits( double minOutput, double maxOutput ) {
        locMinOutput = minOutput;
        locMaxOutput = maxOutput;
    }

    // // ---------------------------------------------------------------------------------------------
    // /**
    //  *  This is calculated every scan cycle...so there is no setter!
    //  *
    //  * @param period
    //  */
    // public void setPeriod( double period ) {
    //     locPeriod = period;
    // }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set new tuning constants for Kp, Ki, and Kd.  These take effect immediately upon the next
     * call to the Exec function.
     * 
     * @param Kp - double - New Proportional gain constant.
     * @param Ki - double - New Integral gain constant.
     * @param Kd - double - New Derivative gain constant.
     */
    public void setPID( double Kp, double Ki, double Kd ) {
        locKp = Kp * locKn;
        locKi = Ki * locKn;
        locKd = Kd * locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set new tuning constants for Kp, Ki, Kd, and Kf.  These take effect immediately upon the next
     * call to the Exec function.
     * 
     * @param Kp - double - New Proportional gain constant.
     * @param Ki - double - New Integral gain constant.
     * @param Kd - double - New Derivative gain constant.
     * @param Kf - double - New Feedforware gain constant.
     */
    public void setPIDF( double Kp, double Ki, double Kd, double Kf ) {
        locKp = Kp * locKn;
        locKi = Ki * locKn;
        locKd = Kd * locKn;
        locKf = Kf * locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set new tuning constants for Kp, Ki, Kd, Kf, and Kn.  These take effect immediately upon the next
     * call to the Exec function.  Internally the new Kp, Ki, Kd, and Kf values are affected by the new
     * Kn value.
     * 
     * @param Kp - double - New Proportional gain constant.
     * @param Ki - double - New Integral gain constant.
     * @param Kd - double - New Derivative gain constant.
     * @param Kf - double - New Feedforware gain constant.
     * @param Kn - double - New Normalization gain constant.  When this is 1.0, tuning constants have
     * "CO" relative units, otherwise tuning constants have "PV" relative units.
     */
    public void setPIDFN( double Kp, double Ki, double Kd, double Kf, double Kn ) {
        locKn = Kn;
        locKp = Kp * locKn;
        locKi = Ki * locKn;
        locKd = Kd * locKn;
        locKf = Kf * locKn;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     *   NOT USEFUL SO IT IS PRIVATE.  Sets the current value of the setpoint.
     * 
     * @param setPoint
     */
    private void setSetpoint( double setPoint ) {
        locSP = setPoint;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set the error tolerance in "PV" relative units..  (This is sometimes also called position
     * tolerance).  This sets the value used to determine if the controller is "AtSetpoint".  This 
     * value is not used by the control logic to calculate the control output.  If not set, the 
     * default is 0.0.
     * 
     * @param posTolerance - double - New error tolerance value in "PV" units.
     */
    public void setTolerance( double posTolerance ) {
        locPosTolerance = posTolerance;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set the error tolerance in "PV" relative units and the error rate of change tolerance in
     * "PV/Sec" units.  (These are sometimes also called position and velocity tolerances).  
     * This sets the values used to determine if the controller is "AtSetpoint".  These values 
     * are not used by the control logic to calculate the control output.  If not set, the 
     * defaults are 0.0 and infinite.
     * 
     * @param posTolerance - double - Error tolerance in "PV" units.
     * @param velTolerance - double - Error rate of change tolerance in "PV/Sec" units.
     */
    public void setTolerancePV( double posTolerance, double velTolerance ) {
        locPosTolerance = posTolerance;
        locVelTolerance = velTolerance;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Sets if the Process Variable and Setpoint are continuous.  This occurs when the maximum value
     * wraps around to the minimum value.  This most often occurs when controlling a circular angle
     * which wraps from PI to -PI.  If this function is not caled, the default is FALSE.
     * The minimum and maximum input limits are also set with this method.
     * 
     * Note that the actual PV and SP can exceed the input limits.  When this occurs, the modulus of
     * these values based on the input limits are used.
     * 
     * @param continuousInput - boolean - Sets continuous input when TRUE.
     * @param minInput - double - minimum value of input.
     * @param maxInput - double - maximum value of input.
     */
    public void setContinuousInput(boolean continuousInput, double minInput, double maxInput ) {
        locContinousInput = continuousInput;
        locMinInput = minInput;
        locMaxInput = maxInput;
    }

}
