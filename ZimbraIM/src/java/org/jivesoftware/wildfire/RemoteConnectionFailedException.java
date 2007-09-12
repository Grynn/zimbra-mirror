package org.jivesoftware.wildfire;

/**
 * Thrown when something failed verifying the key of a Originating Server with an Authoritative
 * Server in a dialback operation.
 *
 * @author Gaston Dombiak
 */
public class RemoteConnectionFailedException extends Exception {

    public RemoteConnectionFailedException() {
        super();
    }

    public RemoteConnectionFailedException(String msg) {
        super(msg);
    }
}
