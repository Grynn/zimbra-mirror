package org.jivesoftware.wildfire.filetransfer.proxy;

import org.jivesoftware.util.Cacheable;
import org.jivesoftware.wildfire.filetransfer.FileTransferProgress;

import java.io.IOException;

/**
 * Tracks the different connections related to a proxy file transfer. There are two connections, the
 * initiator and the target and when both connections are completed the transfer can begin.
 */
public interface ProxyTransfer extends Cacheable, FileTransferProgress {

    /**
     * Sets the transfer digest for a file transfer. The transfer digest uniquely identifies a file
     * transfer in the system.
     *
     * @param digest the digest which uniquely identifies this transfer.
     */
    public void setTransferDigest(String digest);

    /**
     * Returns the transfer digest uniquely identifies a file transfer in the system.
     *  
     * @return the transfer digest uniquely identifies a file transfer in the system.
     */
    public String getTransferDigest();

    /**
     * Returns true if the Bytestream is ready to be activated and the proxy transfer can begin.
     *
     * @return true if the Bytestream is ready to be activated.
     */
    public boolean isActivatable();

    /**
     * Transfers the file from the initiator to the target.
     */
    public void doTransfer() throws IOException;
}
