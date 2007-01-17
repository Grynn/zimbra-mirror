package org.jivesoftware.wildfire.net;

import java.net.InetAddress;

import org.apache.mina.common.IoSession;
import org.jivesoftware.wildfire.Connection;
import org.jivesoftware.wildfire.ConnectionCloseListener;
import org.jivesoftware.wildfire.PacketDeliverer;
import org.jivesoftware.wildfire.Session;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.xmpp.packet.Packet;

public class NewNonBlockingSocketConnection implements Connection {
    
    public NewNonBlockingSocketConnection(PacketDeliverer _deliverer, IoSession _session, boolean isSecure)
    {
        
    }
    
    public void close() {
        // TODO Auto-generated method stub
        
    }

    public void deliver(Packet packet) throws UnauthorizedException {
        // TODO Auto-generated method stub
        
    }

    public void deliverRawText(String text) {
        // TODO Auto-generated method stub
        
    }

    public CompressionPolicy getCompressionPolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    public InetAddress getInetAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLanguage() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getMajorXMPPVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getMinorXMPPVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    public TLSPolicy getTlsPolicy() {
        // TODO Auto-generated method stub
        return null;
    }

    public void init(Session session) {
        // TODO Auto-generated method stub
        
    }

    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCompressed() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFlashClient() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSecure() {
        // TODO Auto-generated method stub
        return false;
    }

    public Object registerCloseListener(ConnectionCloseListener listener, Object handbackMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object removeCloseListener(ConnectionCloseListener listener) {
        // TODO Auto-generated method stub
        return null;
    }

    public void systemShutdown() {
        // TODO Auto-generated method stub
        
    }

    public boolean validate() {
        // TODO Auto-generated method stub
        return false;
    }

}
