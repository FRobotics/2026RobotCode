// --------file: LIB4150PositionControl.java

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
public class Lib4150PositionControl {

    // --------internal object variables.
    private double locFPGATime = 0.0;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a XXXXXXXX
    *                   
    *   @param  paramName - type - description.
    */
    public Lib4150PositionControl( double errorDeadband, double errorThreshold, double outputDeadband, double outputThreshold,
                                    double maxOutput, double Kd, boolean filterDerivative, boolean negateOutput ) {
    }


    // ========SETTERS

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    // --------set new tuning parameters - slope based.
    // **FUTURE** public void setTuning( double slope, double offset, errorDeadband, outputDeadband  ) {
    // }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setTuning( double errorDeadband, double errorThreshold, double outputDeadband, double outputThreshold ) {
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setVirtualLimits( double fwdLimit, double revLimit ) {
        return;
    }
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setMaxOutput( double maxOutput ) {
        return;
    }
    
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setDerivativeTuning( double Kd, boolean filterDerivative ) {
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setNegateOutput( boolean negateOutput ) {
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public void setDesiredOnTargetCount( int desiredOnTargetCount ) {
        return;
    }

    
    // ========GETTERS
    
    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getPositionError() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean getOnTarget() {
        return false;
    }



    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getFwdVirtualLimit() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getRevVirtualLimit() {
        return 0.0;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getMaxOutput() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getSlope() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getOffset() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getErrorThreshold() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getErrorDeadband() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getOutputThreshold() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getOutputDeadband() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double getKd() {
        return 0.0;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean getFilterDerivative() {
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public boolean getNegateOutput() {
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public int getDesiredOnTargetCount() {
        return 0;
    }


    // --------EXECUTE LOGIC

    // --------TIME NOT PASSED AS ARGUMENT

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExec( double targetPos, double actualPos ) {
        // --------set variables needed for internal routine
        locFPGATime = Timer.getFPGATimestamp();
        return this.locPosCtrlExec( );
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExec( double ctrlError ) {
        locFPGATime = Timer.getFPGATimestamp();
        return this.locPosCtrlExec( );
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExec( double targetPos, double actualPos, boolean fwdLimitReached, boolean revLimitReached ) {
        locFPGATime = Timer.getFPGATimestamp();
        return this.locPosCtrlExec(  );
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExec( double ctrlError, boolean fwdLimitReached, boolean revLimitReached  ) {
        locFPGATime = Timer.getFPGATimestamp();
        return this.locPosCtrlExec(  );
    }

    // --------TIME PASSED AS ARGUMENT


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExecWTime( double targetPos, double actualPos, double elapsedSystemTime ) {
        // --------set variables needed for internal routine
        locFPGATime = elapsedSystemTime;
        return this.locPosCtrlExec( );
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExecWTime( double ctrlError, double elapsedSystemTime ) {
        locFPGATime = elapsedSystemTime;
        return this.locPosCtrlExec( );
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExecWTime( double targetPos, double actualPos, 
                    boolean fwdLimitReached, boolean revLimitReached,
                    double elapsedSystemTime ) {
        locFPGATime = elapsedSystemTime;
        return this.locPosCtrlExec(  );
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    public double PosCtrlExeWTime( double ctrlError, boolean fwdLimitReached, boolean revLimitReached, double elapsedSystemTime  ) {
        locFPGATime = elapsedSystemTime;
        return this.locPosCtrlExec(  );
    }


    // ---------------------------------------------------------------------------------------------
    /**
    *   DESCRIPTION
    *
    *   @param  paramName - type - description.
    *   @return returnName - type - decription
    */
    private double locPosCtrlExec() {
        // --------just to make compiler happy...REPLACE...
        return locFPGATime;
    }

}
