package org.jivesoftware.wildfire;

/**
 * A unique identifier for a stream.
 *
 * @author Iain Shigeoka
 */
public interface StreamID {

    /**
     * Obtain a unique identifier for easily identifying this stream in
     * a database.
     *
     * @return The unique ID for this stream
     */
    public String getID();
}