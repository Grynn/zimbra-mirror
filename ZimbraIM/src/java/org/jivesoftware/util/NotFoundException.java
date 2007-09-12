package org.jivesoftware.util;

/**
 * <p>Flags an exception when something requested is not found.</p>
 * <p>Use this class when it's not worth creating a unique xNotFoundException class, or
 * where the context of the call makes it obvious what type of object was not found.</p>
 *
 * @author Iain Shigeoka
 */
public class NotFoundException extends Exception {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
