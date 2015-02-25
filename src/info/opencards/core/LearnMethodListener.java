package info.opencards.core;

/**
 * An API which needs to be implemented by <code>LearnProcessManager</code>s in order to work. The methods defined in
 * this interface are used to give some feedback from the leraning-method back to the managing
 * <code>ProcessManager</code>
 *
 * @author Holger Brandl
 * @see LearnMethod
 */
public interface LearnMethodListener {


    /**
     * invoked after the status of an <code>Item</code> has changed.
     */
    void itemChanged(Item item, boolean stillOnSchedule, Integer feedback);


    void cardFileProcessingFinished(boolean wasInterrupted);


    void processStatusInfo(String statusMsg, double completeness);
}
