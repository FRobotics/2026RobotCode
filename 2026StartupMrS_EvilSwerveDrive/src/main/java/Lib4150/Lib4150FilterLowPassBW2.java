package Lib4150;

import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.wpilibj.Timer;

// =============================================================================================
/**
 * This class implements a 2nd order low pass butterworth filter using finite differences.
 * This filter smooths an input, reducing noise, while sacrificing resposonsiveness.  A small
 * amount of delay or lag is introduced.  However often the reduction of noise is worth
 * the additional lag time.  After creating the object, the ExecFilter method should be called
 * periodically to smooth the input value.  The output value should reach 99 percent of the step
 * change of the input after 5 time constants.
 *<br>
 *  File:   Lib4150FilterLowPassBW2.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150FilterLowPassBW2<br>
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
 *<br>
 * ========================== Version History ==================================================<br>
 *  1.00    11/19/2025  Jim Simpson     Created.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-11-19
*/
public class Lib4150FilterLowPassBW2 {

    // --------constants
    //--unused-- private static final int IN_GAIN_COUNT = 3;
    //--unused-- private static final int OUT_GAIN_COUNT = 2;
    // --------object data storage
    private double locTimeConstant = 0.0;
    private double locEstimatedPeriod = 0.020;
    private double locLastElapsedTime = -1.0;
    private double locAllowedPeriodDiff = 0.005;
    private double[] locAConsts = { 0.0, 0.0, 0.0 };   // input gains
    private double[] locBConsts = { 0.0, 0.0 };   // output gains
    private double[] locSavedInputs = { 0.0, 0.0, 0.0 };
    private double[] locSavedOutputs = { 0.0, 0.0 };

    // --------constructor

    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a low pass 2nd order butterworth filter.
     * 
     * @param timeConstant - double - time constant - seconds
     * @param estimatedPeriod - double - estimated execution period - seconds.
     */
    public Lib4150FilterLowPassBW2( double timeConstant, double estimatedPeriod ) {
        this(0.0, timeConstant, estimatedPeriod);
        return;
    }
    
    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a low pass 2nd order butterworth filter.
     * 
     * @param inputValue - double - initial input value.
     * @param timeConstant - double - time constant - seconds
     * @param estimatedPeriod - double - estimated execution period - seconds.
     */
    public Lib4150FilterLowPassBW2( double inputValue, double timeConstant, double estimatedPeriod ) {

        // --------calculate constants for this filter.
        this.setTimeConstant(timeConstant, estimatedPeriod);
        // --------reset
        this.reset(inputValue);

        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Execute the 2nd order low pass butterworth filter.  This should be called every execution
     * cycle.
     * 
     * @param input - double - input value to be filtered.
     * @param elapsedTime - double - system elapsed time - seconds.
     * @param enable - boolean - set TRUE to enable the filter.  When FALSE the filter is reset and
     *                           the input is passed through to the output.
     * 
     * @return filteredValue - double - filtered input value.
     */
    public double execFilter( double input, double elapsedTime, boolean enable ) {
        double retValue = input;
        // --------first time through set last time to be this time - estimated period.
        if ( locLastElapsedTime < 0.0 ) {
            locLastElapsedTime = elapsedTime - locEstimatedPeriod;
        }
        double period = Math.max( 0.001, elapsedTime - locLastElapsedTime );
        locLastElapsedTime = elapsedTime;
        // --------do we need to calc new constants??
        if ( ( Math.abs( locEstimatedPeriod - period ) > locAllowedPeriodDiff ) && enable ) {
            locEstimatedPeriod = period;
            this.setTimeConstant(locTimeConstant, locEstimatedPeriod);
            this.reset(locSavedInputs[0]);  // use last input value not this one...
        }

        // --------if enabled perform the filtering.
        if ( enable ) {
            locSavedInputs[2] = locSavedInputs[1];
            locSavedInputs[1] = locSavedInputs[0];
            locSavedInputs[0] = input;
            retValue = locSavedInputs[0] * locAConsts[0] + locSavedInputs[1] * locAConsts[1]  + locSavedInputs[2] * locAConsts[2]
                    - ( locSavedOutputs[0] * locBConsts[0] + locSavedOutputs[1] * locBConsts[1]);
            locSavedOutputs[1] = locSavedOutputs[0];
            locSavedOutputs[0] = retValue;
        }
        // --------not enabled, pass the input to the output and reset the saved filter values.
        else {
            this.reset(input);
        }
        return retValue;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Execute the 2nd order low pass butterworth filter.  This should be called every execution
     * cycle.
     * 
     * @param input - double - input value to be filtered.
     * @param elapsedTime - double - system elapsed time - seconds.
     * 
     * @return filteredValue - double - filtered input value.
     */
    public double execFilter( double input, double elapsedTime ) {

        return this.execFilter(input, elapsedTime, true);
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Reset the filter.  The saved input and output values are set to zero.
     */
    public void reset() {
        this.reset(0.0);
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Reset the filter.  
     * 
     * @param inputValue - double - value to used for the saved inputs and outputs.
     */
    public void reset(double inputValue ) {
        locSavedInputs[0] = inputValue;
        locSavedInputs[1] = inputValue;
        locSavedInputs[2] = inputValue;
        locSavedOutputs[0] = inputValue;
        locSavedOutputs[1] = inputValue;
        return;
    } 

    // ========getters

    // ---------------------------------------------------------------------------------------------
    /**
     * Get the current time constant.
     * 
     * @return timeConstant - double - seconds.
     */
    public double getTimeConstant() {
        return locTimeConstant;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Get Estimated execution period
     * 
     * @return executionPeriod - double - Estimated execution period - seconds.
     */
    public double getEstimatedPeriod() {
        return locEstimatedPeriod;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Get maximum allowed time period difference before finite difference constants are re-calculated.
     * The initial and default value is 0.005 seconds.  Setting this to a very large value will
     * disable finite difference constant recalculation.
     * 
     * @return maxAllowedPeriodDifference - double - maximum allowed time period difference - seconds.
     */
    public double getAllowedPeriodDifference() {
        return locAllowedPeriodDiff;
    }

    /**
     * get calculated input gain constants.
     * 
     * @return inputGains - double[] - input gain constants.
     */
    public double[] getInputConstants() {
        return locAConsts;
    }

    /**
     * get calculated output gain constants.
     * 
     * @return outputGains - double[] - output gain constants.
     */
    public double[] getOutputConstants() {
        return locBConsts;
    }

    // ========setters

    // ---------------------------------------------------------------------------------------------
    /**
     * Set time consant and estimated execution period.  Finite difference constants are recalculated.
     * Time constant must be larger than the estimated execution period.  When a step change in the input
     * value occurs, the output will reach 99% of the input value in 5 time constants.
     * 
     * @param timeConstant - double - time constant - seconds.
     * @param estimatedPeriod - double - estimated execution period - seconds.
     */
    public void setTimeConstant( double timeConstant, double estimatedPeriod ) {

        // --------save estimated period
        locEstimatedPeriod = estimatedPeriod;

        // -------- y = A0 * x(0) + A1 * x(-1) + A2 * x(-2) - ( B1 * y(-1) + B2 * y(-2) )
        // -------- Ts = sample period
        // -------- Tc = time constant
        // -------- C = cotangent( Ts * 0.5 / Tc ) = 1 / Tangent(Ts * 0.5 / Tc)
        // -------- A0 = n0 / d0
        // -------- A1 = n1 / d0
        // -------- A2 = n2 / d0
        // -------- B1 = d1 / d0
        // -------- B2 = d2 / d0
        // -------- n0 = 1
        // -------- n1 = 2
        // -------- n2 = 1
        // -------- d0 = c^2 + sqrt(2) * c + 1
        // -------- d1 = -2 * ( C^2 - 1 ) = 2 - 2 * C^2
        // -------- d2 = C^2 - sqrt(2) * C + 1

        // --------calcuate input (a) and output (b) gains.
        double useTimeConstant = Math.max( timeConstant, estimatedPeriod+0.0001 );
        double C = 1.0 / Math.tan( MathUtil.clamp( estimatedPeriod * 0.5 / useTimeConstant, 0.0, Math.PI * 0.5 ));

        double c_square = C * C;
        double two_squareroot = Math.sqrt(2.0);

        final double n0 = 1.0;
        final double n1 = 2.0;
        final double n2 = 1.0;

        double d0_inv = 1.0 / (c_square + two_squareroot * C + 1.0);
        double d1 = 2.0 - 2.0 * c_square;
        double d2 = c_square - two_squareroot * C + 1.0;

        locAConsts[0] = n0 * d0_inv;
        locAConsts[1] = n1 * d0_inv;
        locAConsts[2] = n2 * d0_inv;

        locBConsts[0] = d1 * d0_inv;
        locBConsts[1] = d2 * d0_inv;

        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set maximum allowed time period difference before finite difference constants are re-calculated.
     * The initial and default value is 0.005 seconds.  Setting this to a very large value will
     * disable finite difference constant recalculation.
     * 
     * @param allowedPeriodDiffSec - double - max allowed time period difference - seconds.
     */
    public void setAllowedPeriodDifference( double allowedPeriodDiffSec ) {
        locAllowedPeriodDiff = allowedPeriodDiffSec;
    }

}
