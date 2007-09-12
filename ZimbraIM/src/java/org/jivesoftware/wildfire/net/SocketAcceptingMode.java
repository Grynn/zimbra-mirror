package org.jivesoftware.wildfire.net;

import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Abstract class for {@link BlockingAcceptingMode} and {@link NonBlockingAcceptingMode}.
 *
 * @author Gaston Dombiak
 */
abstract class SocketAcceptingMode {

    /**
     * True while this thread should continue running.
     */
    protected boolean notTerminated = true;

    /**
     * Holds information about the port on which the server will listen for connections.
     */
    protected ServerPort serverPort;

    /**
     * socket that listens for connections.
     */
    protected ServerSocket serverSocket;

    protected ConnectionManager connManager;

    protected SocketAcceptingMode(ConnectionManager connManager, ServerPort serverPort) {
        this.connManager = connManager;
        this.serverPort = serverPort;
    }

    public abstract void run();

    public void shutdown() {
        notTerminated = false;
        try {
            ServerSocket sSock = serverSocket;
            serverSocket = null;
            if (sSock != null) {
                sSock.close();
            }
        }
        catch (IOException e) {
            // we don't care, no matter what, the socket should be dead
        }
    }
}
