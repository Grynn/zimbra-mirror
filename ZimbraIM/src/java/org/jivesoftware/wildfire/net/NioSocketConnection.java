package org.jivesoftware.wildfire.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.IoSession;
import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;

public class NioSocketConnection extends SocketConnection {
    
    private CharsetEncoder mCharsetEncoder;
    private IoSession mIoSession;
    
    public NioSocketConnection(PacketDeliverer backupDeliverer, IoSession session, boolean isSecure)
    throws IOException {
        super(backupDeliverer, isSecure);
        
        if (session == null) {
            throw new NullPointerException("Socket channel must be non-null");
        }
        
        mIoSession = session;
        mCharsetEncoder = sCharset.newEncoder();
        
        writer = new BufferedWriter(getNioWriter());
        xmlSerializer = new XMLSocketWriter(writer, this);
    }
    
    public Writer getNioWriter() {
        return new NioWriter(mIoSession, mCharsetEncoder);
    }
    
    public void startTLS(boolean clientMode, String remoteServer) throws IOException {
    }
    
    public void startCompression() throws IOException {
    }

    protected void closeConnection() {
        try {
            release();
            mIoSession.close();
        } catch (Exception e) {
            Log.error(LocaleUtils.getLocalizedString("admin.error.close")
                    + "\n" + this.toString(), e);
        }
    }
    
    public InetAddress getInetAddress() {
        return ((InetSocketAddress)mIoSession.getRemoteAddress()).getAddress(); 
    }

    /**
     * Returns the port that the connection uses.
     *
     * @return the port that the connection uses.
     */
    public int getPort() {
        return ((InetSocketAddress)mIoSession.getRemoteAddress()).getPort();        
    }

    
    public boolean isClosed() {
        if (session == null) {
            return mIoSession.isClosing();
        }
        return session.getStatus() == Session.STATUS_CLOSED;
    }
    
    
}
