package Lib4150;

import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.wpilibj.Timer;

// =============================================================================================
/**
 * This class implements a 1st order low pass butterworth filter using finite differences.
 * This filter smooths an input, reducing noise, while sacrificing resposonsiveness.  A small
 * amount of delay or lag is introduced.  However often the reduction of noise is worth
 * the additional lag time.  After creating the object, the ExecFilter method should be called
 * periodically to smooth the input value.  The output value should reach 99 percent of the step
 * change of the input after 5 time constants.
 *<br>
 *  File:   Lib4150FilterLowPassBW1.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150FilterLowPassBW1<br>
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
public class Lib4150FilterLowPassBW1 {

    // --------constants
    //--unused-- private static final int IN_GAIN_COUNT = 2;
    //--unused-- private static final int OUT_GAIN_COUNT = 1;
    // --------object data storage
    private double locTimeConstant = 0.0;
    private double locEstimatedPeriod = 0.020;
    private double locLastElapsedTime = -1.0;
    private double locAllowedPeriodDiff = 0.005;
    private double[] locAConsts = { 0.0, 0.0 };   // input gains
    private double[] locBConsts = { 0.0 };   // output gains
    private double[] locSavedInputs = { 0.0, 0.0 };
    private double[] locSavedOutputs = { 0.0 };

    // --------constructor

    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a low pass 1st order butterworth filter.
     * 
     * @param timeConstant - double - time constant - seconds
     * @param estimatedPeriod - double - estimated execution period - seconds.
     */
    public Lib4150FilterLowPassBW1( double timeConstant, double estimatedPeriod ) {
        this(0.0, timeConstant, estimatedPeriod);
        return;
    }
    
    // ---------------------------------------------------------------------------------------------
    /**
     * Construct a low pass 1st order butterworth filter.
     * 
     * @param inputValue - double - initial input value.
     * @param timeConstant - double - time constant - seconds
     * @param estimatedPeriod - double - estimated execution period - seconds.
     */
    public Lib4150FilterLowPassBW1( double inputValue, double timeConstant, double estimatedPeriod ) {

        // --------calculate constants for this filter.
        this.setTimeConstant(timeConstant, estimatedPeriod);
        // --------reset
        this.reset(inputValue);

        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Execute the 1st order low pass butterworth filter.  This should be called every execution
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
            locSavedInputs[1] = locSavedInputs[0];
            locSavedInputs[0] = input;
            retValue = locSavedInputs[0] * locAConsts[0] + locSavedInputs[1] * locAConsts[1] - locSavedOutputs[0] * locBConsts[0];
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
     * Execute the 1st order low pass butterworth filter.  This should be called every execution
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
        locSavedOutputs[0] = inputValue;
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

        // --------calcuate input (a) and output (b) gains.
        double useTimeConstant = Math.max( timeConstant, estimatedPeriod+0.0001 );
        double C = 1.0 / Math.tan( MathUtil.clamp( estimatedPeriod * 0.5 / useTimeConstant, 0.0, Math.PI * 0.5 ));

        double D = C + 1.0;
        double A0 = 1.0 / D;    // Also A1, 1/D0;

        locAConsts[0] = A0;
        locAConsts[1] = A0;

        double D1 = 1.0 - C;

        locBConsts[0] = D1 * A0;  // or D1 / D0

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
