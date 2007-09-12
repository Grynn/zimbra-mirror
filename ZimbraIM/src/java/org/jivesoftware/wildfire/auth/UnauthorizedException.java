package org.jivesoftware.wildfire.auth;

/**
 * Thrown if a user does not have permission to access a particular method.
 *
 * @author Iain Shigeoka
 */
public class UnauthorizedException extends Exception {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}