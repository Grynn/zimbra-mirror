package org.jivesoftware.wildfire.auth;

/**
 * Thrown if a user does not have permission to access a particular method.
 *
 * @author Jay Kline
 */
public class UnauthenticatedException extends Exception {

    public UnauthenticatedException() {
        super();
    }

    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException(Throwable cause) {
        super(cause);
    }

    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }
}