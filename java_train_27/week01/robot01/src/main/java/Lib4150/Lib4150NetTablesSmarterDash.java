// --------file: LIB410NetTablesSmarterDash.java

package Lib4150;

import java.nio.ByteBuffer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

// =============================================================================================
/**
 * Smarter Dashboard IO class.  This reads and writes individual values to network tables.
 * This defines and gets the Network Table entry only once to help make this efficient.
 * The overhead should be reduced from SmartDashboard. 
 *<br>
 *  File:   Lib4150NetTablesSmarterDash.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150NetTablesSmarterDash<br>
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
 *  1.01    11/17/2025  Jim Simpson     Added string types.<br>
 * =============================================================================================<br>
 *<br>
 * @author     Jim Simpson
 * @version    1.0
 * @since      2025-11-01
*/
public class Lib4150NetTablesSmarterDash {

    // --------internal variables

    // --------string name
    private String locEntryName;

    /** The NetworkTable */
    private static NetworkTable table;

    // --------NT tables entry
    private NetworkTableEntry locNtEntry;

    // --------set the table value.  This is a little odd, but I copied it from SmartDashboard...
    static {
        setNetworkTableInstance(NetworkTableInstance.getDefault());
    }

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a Lib4150NetTablesSmarterDash object.  
    *
    * @param EntryName - String - The network table name for this value.
    */
    public Lib4150NetTablesSmarterDash( String EntryName ) {
        
        // --------set the local entry for this entryname string.
        locNtEntry = table.getEntry(EntryName);
        // --------set locl entry name
        locEntryName = new String( EntryName );

        return;
    }


    // ---------------------------------------------------------------------------------------------
    /**
    * Set the NetworkTable instance used for entries. For testing purposes; use with caution.
    *
    * @param inst NetworkTable instance
    */
    private static synchronized void setNetworkTableInstance(NetworkTableInstance inst) {
        table = inst.getTable("SmartDashboard");
        // tablesToData.clear();
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Check to see if this entry has been defined.  If not, define it.
     */
    private void checkIfDefined( ) {
        // --------if not defined...
        if ( locNtEntry == null ) {
            locNtEntry = table.getEntry(locEntryName);
        }
        return;
    } 

    // ---------------------------------------------------------------------------------------------
    /**
     * Get the network table handle (index) for this value.
     * 
     * @return NtEntry - int - NT entry number for this value.
     */
    public int getIndex() {
        return locNtEntry.getHandle();
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send number to network tables.
     * 
     * @param value - Number - send this value to Network Tables.
     */
    public void putNumber( Number value ) {
        this.checkIfDefined();
        locNtEntry.setNumber( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read number from network tables.
     * 
     * @param valueDefault - Number - default value to use if entry not defined.
     * 
     * @return value - Number - read the value from Network Tables.
     */
    public Number getNumber( Number valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getNumber( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send double number to network tables.
     * 
     * @param value - double - send this value to Network Tables.
     */
    public void putDouble( double value ) {
        this.checkIfDefined();
        locNtEntry.setNumber( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read double number from network tables.
     * 
     * @param valueDefault - double - default value to use if entry not defined.
     * 
     * @return value - double - read the value from Network Tables.
     */
    public double getDouble( double valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getDouble( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send float number to network tables.
     * 
     * @param value - float - send this value to Network Tables.
     */
    public void putFloat( float value ) {
        this.checkIfDefined();
        locNtEntry.setFloat( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read float number from network tables.
     * 
     * @param valueDefault - float - default value to use if entry not defined.
     * 
     * @return value - float - read the value from Network Tables.
     */
    public float getFloat( float valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getFloat( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send int (int32) number to network tables.  Note that java only supports
     * 32 bit and 64 bit signed integers.  The NT implentation only supports 64 bit signed
     * integers.  The value is converted first before being sent.
     * 
     * @param value - int - send this value to Network Tables.
     */
    public void putInt32( int value ) {
        long tmpLong = value;
        this.checkIfDefined();
        locNtEntry.setInteger(tmpLong);
        return;
    }
    
    // ---------------------------------------------------------------------------------------------
    /**
     * read int (int32) number from network tables.  Note that java only supports
     * 32 bit and 64 bit signed integers.  The NT implentation only supports 64 bit signed
     * integers.  The value is converted to 32 bits after being read.
     * 
     * @param valueDefault - int - default value to use if entry not defined.
     * 
     * @return value - int - read the value from Network Tables.
     */
    public int getInt32( int valueDefault ) {
        this.checkIfDefined();
        long tmpDefault = valueDefault;
        long tmpResult = locNtEntry.getInteger( tmpDefault );
        return (int)tmpResult;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send long (int64) number to network tables.
     * 
     * @param value - long - send this value to Network Tables.
     */
    public void putInt64( long value ) {
        this.checkIfDefined();
        locNtEntry.setInteger( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read long (int64) number from network tables.
     * 
     * @param valueDefault - long - default value to use if entry not defined.
     * 
     * @return value - long - read the value from Network Tables.
     */
    public float getInt64( long valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getInteger( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send boolean number to network tables.
     * 
     * @param value - boolean - send this value to Network Tables.
     */
    public void putBoolean( boolean value ) {
        this.checkIfDefined();
        locNtEntry.setBoolean( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read boolean value from network tables.
     * 
     * @param valueDefault - boolean - default value to use if entry not defined.
     * 
     * @return value - boolean - read the value from Network Tables.
     */
    public boolean getBoolean( boolean valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getBoolean( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send String to network tables.
     * 
     * @param value - String - send this value to Network Tables.
     */
    public void putString( String value ) {
        this.checkIfDefined();
        locNtEntry.setString( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read string value from network tables.
     * 
     * @param valueDefault - String - default value to use if entry not defined.
     * 
     * @return value - String - read the value from Network Tables.
     */
    public String getBoolean( String valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getString( valueDefault );
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * send raw byte array to network tables.
     * 
     * @param value - byte[] - send this value to Network Tables.
     */
    public void putRaw( byte[] value ) {
        this.checkIfDefined();
        locNtEntry.setRaw( value );
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * read raw, byte[] from network tables.
     * 
     * @param valueDefault - byte[] - default value to use if entry not defined.
     * 
     * @return value - byte[] - read the value from Network Tables.
     */
    public byte[] getRaw( byte[] valueDefault ) {
        this.checkIfDefined();
        return locNtEntry.getRaw( valueDefault );
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * send raw ByteBuffer to network tables.
     * 
     * @param value - ByteBuffer - send this value to Network Tables.
     */
    public void putRaw( ByteBuffer value ) {
        this.checkIfDefined();
        locNtEntry.setRaw( value );
        return;
    }


}
