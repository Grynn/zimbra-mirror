package org.jivesoftware.wildfire;

/**
 * Generates stream ids in different ways depending on the server set up.
 *
 * @author Iain Shigeoka
 */
public interface StreamIDFactory {

    /**
     * Generate a stream id.
     *
     * @return A new, unique stream id
     */
    public StreamID createStreamID();
}
