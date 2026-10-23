// --------file: LIB4150PositionControl.java

package Lib4150;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;

// =============================================================================================
/**
 *  Position Control closed loop Controller.   This is similar to a PD controller with the 
 *  added functionality of an error deadband, a minimum output value, and clamped maximum /
 *  minimum output.  
 *<br>
 *  File:   LIB4150PositionControl.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          LIB4150PositionControl<br>
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
public class Lib4150PositionControl {

    // --------local object variables.

    // --------constations
    private static final double BIG_POS_VALUE = 9.9E+30;
    private static final double BIG_NEG_VALUE = -9.9E+30;
    // --------realtime values.
    private double locPosError = 0.0;
    private double locActualPos = 0.0;
    // private boolean locActualPosExists = false;
    private double locFPGATime = 0.0;
    private boolean locFwdLimitSwitch = false;
    private boolean locRevLimitSwitch = false;
    // --------saved values...
    private int locOnTargetCount = 0;
    private double locLastPosError = 0.0;
    private double locLastDeriv1 = 0.0;
    private double locLastDeriv2 = 0.0;
    private double locLastFPGATime = 0.0;
    // --------tuning values.
    private double locTuningFwdVirtualLimit = BIG_POS_VALUE;
    private double locTuningRevVirtualLimit = BIG_NEG_VALUE;
    private double locTuningMaxOutput = 0.0;
    private double locTuningSlope = 0.0;
    private double locTuningOffset = 0.0;
    private double locTuningErrorThreshold = 0.0;
    private double locTuningErrorDeadband = 0.0;
    private double locTuningOutputThreshold = 0.0;
    private double locTuningOutputDeadband = 0.0;   
    private double locTuningKd = 0.0;
    private boolean locTuningFilterDeriv = false;
    private boolean locTuningNegateOutput = false;
    private int locTuningOnTargetCount = 10;

    private double locTuningMinInput = BIG_NEG_VALUE;
    private double locTuningMaxInput = BIG_POS_VALUE;
    private boolean locTuningContinuous = false;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a Lib4150PositionControl object.  
    *   Slope and offset are calculated from the error deadband, error threshold, output deadband and
    *   output threshold.  Often maxOutput is set to the same value as output threshold.
    *
    * @param  errorDeadband - double - error deadband value.
    * @param  errorThreshold - double - error threshold value.
    * @param  outputDeadband - double - output deadband value.
    * @param  outputThreshold - double - output threshold value.
    * @param maxOutput - double - absolute value of maximum allowed output.
    * @param Kd - double - derivative tuning parameter is multiplied to the derivative, which is added to the output.
    * @param filterDerivative - boolean - filter the derivative when Kd is non-zero.
    * @param negateOutput - boolean - after all calculations negate the calculated output. Used where motors are reversed.
    */
    public Lib4150PositionControl( double errorDeadband, double errorThreshold, double outputDeadband, double outputThreshold,
                                    double maxOutput, double Kd, boolean filterDerivative, boolean negateOutput ) {
        // --------init object variables.
        // --------saved.
        locOnTargetCount = 0;
        locLastPosError = 0.0;
        locLastDeriv1 = 0.0;
        locLastDeriv2 = 0.0;
        locLastFPGATime = 0.0;
        // -------tuning
        this.setTuning( errorDeadband, errorThreshold, outputDeadband, outputThreshold);
        this.setVirtualLimits(9.9e30, -9.9e30);
        this.setMaxOutput(maxOutput);
        this.setDerivativeTuning(Kd, filterDerivative);
        this.setNegateOutput(negateOutput);
        this.setDesiredOnTargetCount(10);   // --------default of 10 is 200ms at a scan rate of 20ms    
        return;
    }


    // ========SETTERS


    // ---------------------------------------------------------------------------------------------
    // --------set new tuning parameters - slope based.
    // public void setTuning( double slope, double offset, errorDeadband, outputDeadband  ) {
    // --------set local tuning values.
    // --------calculate errorThreshold and outputThreshold.
    // }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Set new tuning parameters based on the Deadband and Threshold points.  Slope and offset are
    *   calculated from these values.  Often the maximum output will be set to the same value as
    *   outputThreshold, however this is not required.
    *
    *   @param  errorDeadband - double - error deadband value.
    *   @param  errorThreshold - double - error threshold value.
    *   @param  outputDeadband - double - output deadband value.
    *   @param  outputThreshold - double - output threshold value.
    */
    public void setTuning( double errorDeadband, double errorThreshold, double outputDeadband, double outputThreshold ) {
        // --------set local values...
        locTuningErrorThreshold = errorThreshold;
        locTuningErrorDeadband = errorDeadband;
        locTuningOutputThreshold = outputThreshold;
        locTuningOutputDeadband = outputDeadband;   
        // --------calculate slope
        locTuningSlope = ( locTuningOutputThreshold - locTuningOutputDeadband ) / 
                        ( locTuningErrorThreshold - locTuningErrorDeadband);
        locTuningOffset = locTuningOutputThreshold - ( locTuningSlope * locTuningErrorThreshold );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Set virtual forward and reverse position limits.
    *
    *   @param  fwdLimit - double - value of the forward position limit.  Output will not exceed
    *                               zero when the position is greater or equal to this value.
    *   @param  revLimit - double - value of the reverse positin limit.  Output will not be below
    *                               zero when the position is less or equal to this value.
    */
    public void setVirtualLimits( double fwdLimit, double revLimit ) {
        locTuningFwdVirtualLimit = fwdLimit;
        locTuningRevVirtualLimit = revLimit;
        return;
    }
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   Set maximum output.  This has no effect on the tuning.  The output is limited to be within
    *   +/- of this value.  Often this is set to the same value as output threshold.
    *
    *   @param  maxOutput - double - value of the maximum allowed output.
    */
    public void setMaxOutput( double maxOutput ) {
        locTuningMaxOutput = maxOutput;
        return;
    }
    
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   Set derivative tuning.
    *
    *   @param  Kd - double -  Derivative gain constant.  If this is zero, derivative term will
    *                          not be used.
    *   @param  filterDerivative - boolean - If TRUE, the derivative will be filtered by averaging 
    *                           the last 3 raw derivative values.  This can help smooth the control. 
    */
    public void setDerivativeTuning( double Kd, boolean filterDerivative ) {
        locTuningKd = Kd;
        locTuningFilterDeriv = filterDerivative;
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Set negate output
    *
    *   @param  negateOutput - boolean -  if TRUE the output will be multiplied by -1.0.
    */
    public void setNegateOutput( boolean negateOutput ) {
        locTuningNegateOutput = negateOutput;
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Set on target count
    *
    *   @param  desiredOnTargetCount - int -  sets number of consecutive execution cycles that the
    *                               error must be within the error deadband before the system is
    *                               considered to be on target.  This does not effect control action.
    */
    public void setDesiredOnTargetCount( int desiredOnTargetCount ) {
        locTuningOnTargetCount = desiredOnTargetCount;
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Set if the input is continuous.  When continuous is set to true the input value wraps from
     * the maximum input to the minimum input.  This is useful for controlling the position of a
     * rotational system.
     * 
     * @param continuousInput - boolean - Set to TRUE if this is a continuous system.
     * @param minInput - double - minimum allowed input value.  Modulus values are calculated when
     *                            the value is below this value.
     * @param MaxInput - double - maximum allowed input value.  Modulus values are calculated when
     *                            the value is above this value.
     */
    public void setContinuous( boolean continuousInput, double minInput, double MaxInput ) {
        locTuningContinuous = continuousInput;
        locTuningMinInput = minInput;
        locTuningMaxInput = MaxInput;
        return;
    }
    
    // ========GETTERS
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   Get position error
    *
    *   @return  positionError - double - current position error.  
    */
    public double getPositionError() {
        return locPosError;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get on target status
    *
    *   @return  onTarget - boolean - returns TRUE when position error has been within error deadband
    *                           consecutively for the defined number of execution cycles.
    */
    public boolean getOnTarget() {
        return locOnTargetCount >= locTuningOnTargetCount;
    }



    // ---------------------------------------------------------------------------------------------
    /**
    *   Get forward virtual limit value
    *
    *   @return  fwdLimit - double - forward virtual limit
    */
    public double getFwdVirtualLimit() {
        return locTuningFwdVirtualLimit;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get reverse virtual limit value
    *
    *   @return  revLimit - double - reverse virtual limit
    */
    public double getRevVirtualLimit() {
        return locTuningRevVirtualLimit;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   Get max output tuning value.  The output is clamped to be within +/- of this value.
    *
    *   @return  maxOutput - double - maximum allowed output.
    */
    public double getMaxOutput() {
        return locTuningMaxOutput;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get slope tuning constant.  This is the value calculated for the straight line output
    *   ramp.  It it is calculated from error deadband, error threshold, output deadband, output
    *   threshold.  If the error deadband and output deadbands are set to zero, this controller
    *   becomes a PID control with this slope = Kp.  (Ki is always zero.)
    *
    *   @return  slope - double - get tuning slope (Kp)
    */
    public double getSlope() {
        return locTuningSlope;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Get offset tuning constant.  This is the value calculated for the straight line output
    *   ramp.  It it is calculated from error deadband, error threshold, output deadband, output
    *   threshold.
    *
    *   @return  offset - double - get tuning offset
    */
    public double getOffset() {
        return locTuningOffset;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get tuning error threshold value.  This along with the error deadband, output deadband, and 
     * output threshold are used to calculate the slope of the output ramp.
     * 
     * @return errorThreshold - double - value of error threshold
     */
    public double getErrorThreshold() {
        return locTuningErrorThreshold;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get tuning error deadband value.  When error is within +/- of this value the output is set
     * to zero.  Also the on target count is incremented.  This along with the error threshold, 
     * output deadband, and output threshold are used to calculate the slope of the output ramp.
     * 
     * @return errorDeadband - double - value of error deadband.
     */
    public double getErrorDeadband() {
        return locTuningErrorDeadband;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get tuning output threshold value.  This along with the output deadband, error deadband, and
     * error threshold are used to calculate the slope of the output ramp.
     * 
     * @return - outputThreshold - double - output threshold tuning value.
     */
    public double getOutputThreshold() {
        return locTuningOutputThreshold;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get tuning output deadband value.  When the error exceeds the error deadband the output jumps
     * from zero to this value (or minus this value based on sign of the error. ).  This term accounts
     * for friction of the system.  This along with the output threshold, error deadband, and
     * error threshold are used to calculate the slope of the output ramp.
     * 
     * @return outputDeadband - double - output deadband tuning value.
     */
    public double getOutputDeadband() {
        return locTuningOutputDeadband;
    }

    // ---------------------------------------------------------------------------------------------
    // --------get tuning  - kd
    /**
     * get deriative gain tuning constant Kd.
     * 
     * @return Kd - double - derivative gain constant.
     */
    public double getKd() {
        return locTuningKd;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * gets tuning value indicating that calculated derivative will be filtered by averaging the
     * last 3 calculated derivatives.  This can be used to smooth a noisy system allowing the 
     * derivative term to provide useful control.
     * 
     * @return filterDerivative - boolean - returns TRUE when derivative filtering is enabled.
     */
    public boolean getFilterDerivative() {
        return locTuningFilterDeriv;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get negate output tuning value.  When true the calculated control output is negated.
     * 
     * @return negateOutput - boolean - When TRUE calculated control output is negated.
     */
    public boolean getNegateOutput() {
        return locTuningNegateOutput;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get consecutive on target count tuning value requred to set on target value.
     * 
     * @return OnTargetCount - int - number of consecutive times system must be within error 
     * deadband to be considerd on target.
     */
    public int getDesiredOnTargetCount() {
        return locTuningOnTargetCount;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get system is continuous tuning value.
     * 
     * @return continuous - boolean - system is continuous.  Max input value wraps to min input value.
     */
    public boolean getContinous() {
        return locTuningContinuous;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get minimum allowed input tuning value.
     * 
     * @return minInput - double - minimum allowed input tuning value.
     */
    public double getMinInput() {
        return locTuningMinInput;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * get maximum allowed input tuning value.
     * 
     * @return maxInput - double - maximum allowed input tuning value.
     */
    public double getMaxInput() {
        return locTuningMaxInput;
    }

    // --------EXECUTE LOGIC

    // ---------------------------------------------------------------------------------------------
    // --------execute logic
    /**
     * Calculate control output for the given control error.  
     * 
     * @param targetPos - double - desired position.
     * @param actualPos - double - measured position.
     * 
     * @return controlOutput - double - calculated control output.
     */
    public double PosCtrlExec( double targetPos, double actualPos ) {
        if ( locTuningContinuous ) {
            locPosError = Lib4150AdvancedPID.modulusError(targetPos, actualPos, locTuningMinInput, locTuningMaxInput);
            locActualPos = MathUtil.inputModulus(locActualPos, locTuningMinInput, locTuningMaxInput);
        }
        else {
            locPosError = targetPos - actualPos;
            locActualPos = actualPos;
        }
        locFPGATime = Timer.getFPGATimestamp();
        locFwdLimitSwitch = false;
        locRevLimitSwitch = false;
        // --------call internal function to calculate control output
        return this.locPosCtrlExec( );
    }


    // ---------------------------------------------------------------------------------------------
    // --------execute logic -- with error instead.
    /**
     * Calculate control output for the given control error.
     * 
     * @param ctrlError - double - control error ( desired position - actual position )
     * 
     * @return controlOutput - double - calculated control output.
     */
    public double PosCtrlExec( double ctrlError ) {
        if ( locTuningContinuous ) {
            locPosError = MathUtil.inputModulus(ctrlError, locTuningMinInput, locTuningMaxInput);
        }
        else {
            locPosError = ctrlError;
        }
        locActualPos = 0.0;
        locFPGATime = Timer.getFPGATimestamp();
        locFwdLimitSwitch = false;
        locRevLimitSwitch = false;
        // --------call internal function to calculate control output
        return this.locPosCtrlExec( );
    }

    // ---------------------------------------------------------------------------------------------
    // --------execute logic - with limit switches.
    /**
     * Calculate control output for the given control error and limit switch values.
     * 
     * @param targetPos - double - desired position.
     * @param actualPos - double - measured position.
     * @param fwdLimitReached - boolean - TRUE when forward limit has been reached.
     * @param revLimitReached - boolean - TRUE when reverse limit has been reached.
     * 
     * @return controlOutput - double - calculated control output.
     */
    public double PosCtrlExec( double targetPos, double actualPos, boolean fwdLimitReached, boolean revLimitReached ) {
        if ( locTuningContinuous ) {
            locPosError = Lib4150AdvancedPID.modulusError(targetPos, actualPos, locTuningMinInput, locTuningMaxInput);
            locActualPos = MathUtil.inputModulus(locActualPos, locTuningMinInput, locTuningMaxInput);
        }
        else {
            locPosError = targetPos - actualPos;
            locActualPos = actualPos;
        }
        locFPGATime = Timer.getFPGATimestamp();
        locFwdLimitSwitch = fwdLimitReached;
        locRevLimitSwitch = revLimitReached;
        // --------call internal function to calculate control output
        return this.locPosCtrlExec(  );
    }

    // ---------------------------------------------------------------------------------------------
    // --------execute logic -- with error instead.
    /**
     * Calculate control output for the given control error and limit switch values.
     * 
     * @param ctrlError - double - control error ( desired position - actual position )
     * @param fwdLimitReached - boolean - TRUE when forward limit has been reached.
     * @param revLimitReached - boolean - TRUE when reverse limit has been reached.
     * 
     * @return controlOutput - double - calculated control output.
     */
    public double PosCtrlExec( double ctrlError, boolean fwdLimitReached, boolean revLimitReached  ) {
        if ( locTuningContinuous ) {
            locPosError = MathUtil.inputModulus(ctrlError, locTuningMinInput, locTuningMaxInput);
        }
        else {
            locPosError = ctrlError;
        }
        locActualPos = 0.0;
        locFPGATime = Timer.getFPGATimestamp();
        locFwdLimitSwitch = fwdLimitReached;
        locRevLimitSwitch = revLimitReached;
        // --------call internal function to calculate control output
        return this.locPosCtrlExec(  );
    }

    // --------execute logic -- INTERNAL FUNCTION TO DO THE WORK!!
    /**
     * execute position control logic -- INTERNAL FUNCTION TO DO THE WORK!!
     * 
     * @return - controlOutput - double - calculated control output
     */
    private double locPosCtrlExec() {

        boolean fwdLimitHit;
        boolean revLimitHit;
        double fwdErrorLimit = BIG_POS_VALUE;
        double revErrorLimit = BIG_NEG_VALUE;
        double clampedError = 0.0;
        double derivError = 0.0;
        double derivative = 0.0;
        double deriv0 = 0.0;
        boolean errorOutsideDeadband = false;
        double controlOutput = 0.0;
        double offset = 0.0;

        // --------calculate if fwd or rev limits have been hit.
        // --------if actual position doesn't exist, its value will be zero and the virtual checks will fail.
        fwdLimitHit = locFwdLimitSwitch || ( locActualPos >= locTuningFwdVirtualLimit);
        revLimitHit = locRevLimitSwitch || ( locActualPos <= locTuningRevVirtualLimit);

        // --------get limits on the error... and clamp the error value
        if ( fwdLimitHit ) {
            fwdErrorLimit = 0.0;
        }
        if ( revLimitHit ) {
            revErrorLimit = 0.0;
        }
        clampedError = MathUtil.clamp( locPosError, revErrorLimit, fwdErrorLimit);
        errorOutsideDeadband = ( Math.abs(clampedError) > locTuningErrorDeadband );

        // --------if Kd is non-zero calculate derivative term.
        // --------calculate error to use for derivative....
        if ( errorOutsideDeadband ) {
            if ( clampedError > 0.0 ) {
                derivError = clampedError - locTuningErrorDeadband; 
            }
            else {
                derivError = clampedError + locTuningErrorDeadband;
            }
        }
        else {
            derivError = 0.0;
        }
        if ( locTuningKd != 0.0 ) {
            derivative = ( derivError - locLastPosError ) / Math.max( 0.001, locFPGATime - locLastFPGATime);
            deriv0 = derivative;
            if ( locTuningFilterDeriv ) {
                deriv0 = derivative;
                derivative = ( deriv0 + locLastDeriv1 + locLastDeriv2 ) / 3.0;
            }
        }
        else {
            derivative = 0.0;
            deriv0 = 0.0;
        }
        locLastDeriv2 = locLastDeriv1;
        locLastDeriv1 = deriv0;
        locLastPosError = derivError;
        locLastFPGATime = locFPGATime;

        // --------calculate control output...
        // --------if error outside deadband.
        if ( errorOutsideDeadband ) {
            locOnTargetCount = 0;   // --------reset on target count.
            offset = locTuningOffset;
            if ( clampedError < 0.0 ) {
                offset *= -1.0;
            }
            
            // --------calc control output
            // --------   = derivative * Kd + error * slope + offset
            controlOutput = MathUtil.clamp( derivative * locTuningKd + clampedError * locTuningSlope + offset, 
                                    -locTuningMaxOutput, locTuningMaxOutput );
            if ( locTuningNegateOutput ) {
                controlOutput *= -1.0;
            }                            
        }

        // -------error is inside deadband, output is zero...
        else {
            locOnTargetCount = Math.min( locOnTargetCount+1,2000000000);  // --------inc on target count
            controlOutput = 0.0;
        }

        return controlOutput;
    }

}
