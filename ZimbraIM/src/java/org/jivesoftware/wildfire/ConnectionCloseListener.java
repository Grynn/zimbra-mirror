package org.jivesoftware.wildfire;

/**
 * Implement and register with a connection to receive notification
 * of the connection closing.
 *
 * @author Iain Shigeoka
 */
public interface ConnectionCloseListener {
    /**
     * Called when a connection is closed.
     *
     * @param handback The handback object associated with the connection listener during Connection.registerCloseListener()
     */
    public void onConnectionClose(Object handback);
}
