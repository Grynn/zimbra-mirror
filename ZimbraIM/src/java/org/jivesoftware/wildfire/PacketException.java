package org.jivesoftware.wildfire;

/**
 * Represents a runtime packet exception typically from a malformed
 * packet. Uncaught Packet exceptions will cause the originating session
 * to close.
 *
 * @author Iain Shigeoka
 */
public class PacketException extends RuntimeException {

    public PacketException() {
    }

    public PacketException(String s) {
        super(s);
    }

}
