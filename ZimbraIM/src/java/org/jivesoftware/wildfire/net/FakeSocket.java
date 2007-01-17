package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.handler.support.IoSessionInputStream;
import org.apache.mina.handler.support.IoSessionOutputStream;

public abstract class FakeSocket {

//    public abstract InputStream getInputStream() throws IOException;
//    public abstract OutputStream getOutputStream() throws IOException;
    public abstract InetAddress getInetAddress();
    public abstract int getPort();
    public abstract boolean isClosed();
    public abstract void close() throws IOException;
    public abstract SocketChannel getChannel();
    public abstract void setSoTimeout(int timeout) throws SocketException;
    public abstract void setKeepAlive(boolean on) throws SocketException;
    
    
//    InputStream in = new IoSessionInputStream();
//    OutputStream out = new IoSessionOutputStream( session );
    
    
    public static FakeSocket.RealFakeSocket create(Socket sock) {
        return new RealFakeSocket(sock);
    }
    
    protected FakeSocket() {}
    
    public static final class RealFakeSocket extends FakeSocket {
        private Socket mSock = null;
        
        protected RealFakeSocket(Socket sock) {
            mSock = sock;
        }
        
        public InputStream getInputStream() throws IOException { return mSock.getInputStream(); }
        public OutputStream getOutputStream() throws IOException { return mSock.getOutputStream(); }
        public InetAddress getInetAddress() {return mSock.getInetAddress(); }
        public int getPort() { return mSock.getPort(); }
        public boolean isClosed() { return mSock.isClosed(); }
        public void close() throws IOException { mSock.close(); }
        public SocketChannel getChannel() { return mSock.getChannel(); }
        public void setSoTimeout(int timeout) throws SocketException { mSock.setSoTimeout(timeout); }
        public void setKeepAlive(boolean on) throws SocketException { mSock.setKeepAlive(on); }
    }
    
    public static final class MinaFakeSocket extends FakeSocket {
        public static interface ReadReadyCallback {
            void messageReceived(ByteBuffer buf);
        }
        private ReadReadyCallback mReadReadyCallback;
        private IoSession mIoSession= null;
        
        public MinaFakeSocket(IoSession ios) {
            mIoSession = ios;
        }
        
        public InetAddress getInetAddress() {return ((InetSocketAddress)mIoSession.getRemoteAddress()).getAddress(); }
        public int getPort() { return ((InetSocketAddress)mIoSession.getRemoteAddress()).getPort(); }
        public boolean isClosed() { return mIoSession.isClosing(); }
        public void close() throws IOException { mIoSession.close(); }
        public SocketChannel getChannel() { return null; }
        public void setSoTimeout(int timeout) throws SocketException { }
        public void setKeepAlive(boolean on) throws SocketException { }
        
        public void setReadReadyCallback(ReadReadyCallback cb ) { mReadReadyCallback = cb; }
        public void readReady(ByteBuffer cb) { mReadReadyCallback.messageReceived(cb); }
        public IoSession getIoSession() { return mIoSession; }
    }
    
}
