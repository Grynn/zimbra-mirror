package org.jivesoftware.wildfire;

/**
 * Thrown when a channel lookup fails to find the specified channel.
 *
 * @author Matt Tucker
 */
public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException() {
        super();
    }

    public ChannelNotFoundException(String msg) {
        super(msg);
    }
}
