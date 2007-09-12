package org.jivesoftware.wildfire.group;

/**
 * Thrown when attempting to create a group that already exists.
 *
 * @author Iain Shigeoka
 */
public class GroupAlreadyExistsException extends Exception {


    public GroupAlreadyExistsException() {
        super();
    }

    public GroupAlreadyExistsException(String message) {
        super(message);
    }

    public GroupAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public GroupAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}