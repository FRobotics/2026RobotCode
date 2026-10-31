// --------file: LIB4150NetTableSystemSend.java

package Lib4150;

import java.util.ArrayList;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.function.BooleanSupplier;

// =============================================================================================
/**
 * Network Table update helper for Systems.  This is intended to be a simpler (and perhaps better)
 * than WPILIB Sendable.  This also works with pseudo static classes that don't have a constructor.
 * A single thread is created to update the items to Network Tables defined in this object.
 *<br>
 *  File:   Lib4150NetTableSystemSend.java<br>
 *<br>
 *  Referenceable items: (classes)<br>
 *          Lib4150NetTableSystemSend<br>
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
public class Lib4150NetTableSystemSend {

    // --------internal class variables
    private String locNtTreeBranchName;
    private ArrayList<helperItemDouble> locItemsDouble = new ArrayList<helperItemDouble>();
    private ArrayList<helperItemBoolean> locItemsBoolean = new ArrayList<helperItemBoolean>();
    private ArrayList<helperItemString> locItemsString = new ArrayList<helperItemString>();
    private workerThread locWorkerThread;

    // ---------------------------------------------------------------------------------------------
    /**
    *   Construct a Lib4150NetTableSystemSend object.  
    *
    * @param NtTreeBranchName - String - The smartdashboard name prefix for all items in the object.
    */
    public Lib4150NetTableSystemSend( String NtTreeBranchName ) {

        // --------save the branch name..
        locNtTreeBranchName = NtTreeBranchName;

        // --------create the helper thread..
        locWorkerThread = new workerThread(this,locNtTreeBranchName);
        locWorkerThread.start();

        // --------set a slightly lower priority than the main control thread loop.
        int ctrlPriority = locWorkerThread.getPriority();
        Lib4150MessageUtil.SendInfo(" control thread priority = "+ ctrlPriority );
        locWorkerThread.setPriority( ctrlPriority - 1);

        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * add definition of a double item to update.
     * 
     * @param itemName - String - Network Table name for this item.
     * @param getter - DoubleSupplier - function reference to get the value for this thing.
     */
    public void addItemDouble( String itemName, DoubleSupplier getter) {
        locItemsDouble.add(new helperItemDouble( locNtTreeBranchName + "/" + itemName, getter ) );
        return;
    }

    
    // ---------------------------------------------------------------------------------------------
    /**
     * add definition of a boolean item to update.
     * 
     * @param itemName - String - Network Table name for this item.
     * @param getter - BooleanSupplier - function reference to get the value for this thing.
     */
    public void addItemBoolean( String itemName, BooleanSupplier getter) {
        locItemsBoolean.add(new helperItemBoolean( locNtTreeBranchName + "/" + itemName, getter ) );
        return;
    }

        
    // ---------------------------------------------------------------------------------------------
    /**
     * add definition of a string item to update.
     * 
     * @param itemName - String - Network Table name for this item.
     * @param getter - Supplier&lt;String&gt; - function reference to get the value for this thing.
     */
    public void addItemString( String itemName, Supplier<String> getter) {
        locItemsString.add(new helperItemString( locNtTreeBranchName + "/" + itemName, getter ) );
        return;
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * call this to trigger the separate thread to update all the defined items
     * in Network Tables.
     */
    public void triggerUpdate() {
        // --------the thread is in a long wait.  Signal an interrupt to that
        // --------thread to wake it up and do the update.
        locWorkerThread.interrupt();
        return;
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * private worker.  This is called by the thread to read all the item values and
     * write them to Network Tables.
     */
    private void updateAllItems() {

        // --------read all double values and update dashboard.
        // --------could probably have used the newer stream object...
        for ( helperItemDouble thing : locItemsDouble ) {
            thing.updateDash();
        }
        // --------read all boolean values and update dashboard.
        // --------could probably have used the newer stream object...
        for ( helperItemBoolean thing : locItemsBoolean ) {
            thing.updateDash();
        }
        // --------read all string values and update dashboard.
        // --------could probably have used the newer stream object...
        for ( helperItemString thing : locItemsString ) {
            thing.updateDash();
        }

        return;
    }

    // =============================================================================================
    /**
     * class to store the definition of a double item whose value is to be read and written
     * to Network Tables.
     */
    private class helperItemDouble {

        // --------local object storage.
        private DoubleSupplier locHelperGetter;
        private Lib4150NetTablesSmarterDash locSmarterDash;

        // ---------------------------------------------------------------------------------------------
        /**
         * Construct a helpItemDouble object.
         * 
         * @param name - String - Network Table name for this item.
         * @param getter - DoubleSupplier - function reference to get the value for this thing.
         */
        public helperItemDouble( String name, DoubleSupplier getter ) {
            locHelperGetter = getter;
            locSmarterDash = new Lib4150NetTablesSmarterDash( name );
            return;
        }
       
        // ---------------------------------------------------------------------------------------------
        /**
         * update Network tables with the current value of this item.
         */
        private void updateDash() {

            // --------get the value, then update network tables.
            locSmarterDash.putDouble( locHelperGetter.getAsDouble() );
            return;
        }

    }

    // =============================================================================================
    // ----------private class for boolean data.
    private class helperItemBoolean {

        private BooleanSupplier locHelperGetter;
        private Lib4150NetTablesSmarterDash locSmarterDash;

        // ---------------------------------------------------------------------------------------------
        /**
         * Construct a helpItemBoolean object.
         * 
         * @param name - String - Network Table name for this item.
         * @param getter - BooleanSupplier - function reference to get the value for this thing.
         */
        public helperItemBoolean( String name, BooleanSupplier getter ) {
            locHelperGetter = getter;
            locSmarterDash = new Lib4150NetTablesSmarterDash( name );
            return;
        }

        // ---------------------------------------------------------------------------------------------
        /**
         * update Network tables with the current value of this item.
         */
        private void updateDash() {
            locSmarterDash.putBoolean( locHelperGetter.getAsBoolean() );
        }
    }


    // =============================================================================================
    /**
     * class to store the definition of a string item whose value is to be read and written
     * to Network Tables.
     */
    private class helperItemString {

        // --------local object storage.
        private Supplier<String> locHelperGetter;
        private Lib4150NetTablesSmarterDash locSmarterDash;

        // ---------------------------------------------------------------------------------------------
        /**
         * Construct a helpItemString object.
         * 
         * @param name - String - Network Table name for this item.
         * @param getter - Supplier&lt;String&gt; - function reference to get the value for this thing.
         */
        public helperItemString( String name, Supplier<String> getter ) {
            locHelperGetter = getter;
            locSmarterDash = new Lib4150NetTablesSmarterDash( name );
            return;
        }
       
        // ---------------------------------------------------------------------------------------------
        /**
         * update Network tables with the current value of this item.
         */
        private void updateDash() {

            // --------get the value, then update network tables.
            locSmarterDash.putString( locHelperGetter.get() );
            return;
        }

    }
 
    // =============================================================================================
    /**
     * private class for separate worker thread to update Network Tables.
     */
    private class workerThread extends Thread {

        // --------object data
        private boolean runMe = true;
        private Lib4150NetTableSystemSend locParent;

        // ---------------------------------------------------------------------------------------------
        /**
         * Construct the worker thread to update all the Network Tables values.
         * @param parent
         * @param name
         */
        public workerThread( Lib4150NetTableSystemSend parent, String name ) {

            // --------remember the object of our parent.  We need to call back
            // --------to the parent to update the items.
            locParent = parent;

            // --------set the name of this thread...Just nice, not really useful.
            this.setName("NThelper_"+name);

            return;
        }

        // ---------------------------------------------------------------------------------------------
        /**
         * this is the method that executes in a separate thread
         * 
         */
        @Override
        public void run() {

            boolean printed = false;

            this.runMe = true;

            Lib4150MessageUtil.SendInfo(Thread.currentThread().getName() + " is running.");
            Lib4150MessageUtil.SendInfo( this.getName() + " thread priority is " + this.getPriority());

            // --------do forever, until asked to stop...
            while ( this.runMe ) {

                // --------sleep -- wait for interrupt...
                // --------if interrupt doesn't happen in a minute, update values anyway.
                try {
                    Thread.sleep(60000);  // essentially forever.  wait for interrupt..
                } catch (InterruptedException e) {
                    // Lib4150MessageUtil.SendInfo(this.getName() + " was interrupted.");
                }

                // --------do work here.  Update Network Table Variables...
                // --------update dashboard
                locParent.updateAllItems();

                // --------some diags on lowering priority..
                if (!printed) {
                    Lib4150MessageUtil.SendInfo( this.getName() + " thread priority is " + this.getPriority());
                    printed = true;
                }
            }
            
            // -------we were requested to quit.
            Lib4150MessageUtil.SendInfo(this.getName() + " finished.");
            return;
        }
    
    }
}