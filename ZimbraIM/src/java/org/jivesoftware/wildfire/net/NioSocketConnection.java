package org.jivesoftware.wildfire.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

public class NioSocketConnection extends SocketConnection {
    
    private FakeSocket.MinaFakeSocket mNioSocket;
    private CharsetEncoder mCharsetEncoder;
    
    public NioSocketConnection(PacketDeliverer backupDeliverer, FakeSocket.MinaFakeSocket socket, boolean isSecure)
    throws IOException {
        super(backupDeliverer, socket, isSecure);
        
        if (socket == null) {
            throw new NullPointerException("Socket channel must be non-null");
        }
        
        mNioSocket = socket;
        mCharsetEncoder = sCharset.newEncoder();
        
        writer = new BufferedWriter(getNioWriter());
        xmlSerializer = new XMLSocketWriter(writer, this);
    }
    
    public Writer getNioWriter() {
        return new NioWriter(mNioSocket.getIoSession(), mCharsetEncoder);
    }
    
    public void startTLS(boolean clientMode, String remoteServer) throws IOException {
    }
    
    public void startCompression() throws IOException {
    }

    protected void closeConnection() {
        try {
            release();
            socket.close();
        } catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.close")
                    + "\n" + this.toString(), e);
        }
    }
    
}
