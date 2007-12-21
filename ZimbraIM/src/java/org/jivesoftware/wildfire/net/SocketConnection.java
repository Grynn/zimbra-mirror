/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.*;
import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.interceptor.InterceptorManager;
import org.jivesoftware.wildfire.interceptor.PacketRejectedException;
import org.xmpp.packet.Packet;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An object to track the state of a XMPP client-server session.
 * Currently this class contains the socket channel connecting the
 * client and server.
 *
 * @author Iain Shigeoka
 */
public abstract class SocketConnection implements Connection {

    /**
     * The utf-8 charset for decoding and encoding XMPP packet streams.
     */
    public static final String CHARSET = "UTF-8";
    public static final Charset sCharset = Charset.forName(CHARSET);
    
    protected static Map<SocketConnection, String> instances =
            new ConcurrentHashMap<SocketConnection, String>();

    /**
     * Milliseconds a connection has to be idle to be closed. Timeout is disabled by default. It's
     * up to the connection's owner to configure the timeout value. Sending stanzas to the client
     * is not considered as activity. We are only considering the connection active when the
     * client sends some data or hearbeats (i.e. whitespaces) to the server.
     * The reason for this is that sending data will fail if the connection is closed. And if
     * the thread is blocked while sending data (because the socket is closed) then the clean up
     * thread will close the socket anyway.
     */
    protected long idleTimeout = -1;

    final protected Map<ConnectionCloseListener, Object> listeners =
            new HashMap<ConnectionCloseListener, Object>();

    protected SocketReader socketReader;

    protected Writer writer;
    protected AtomicBoolean writing = new AtomicBoolean(false);

    /**
     * Deliverer to use when the connection is closed or was closed when delivering
     * a packet.
     */
    protected PacketDeliverer backupDeliverer;

    protected Session session;
    protected boolean secure;
    protected boolean compressed;
    public org.jivesoftware.util.XMLWriter xmlSerializer;
    protected boolean flashClient = false;
    protected int majorVersion = 1;
    protected int minorVersion = 0;
    protected String language = null;

    protected long writeStarted = -1;

    /**
     * TLS policy currently in use for this connection.
     */
    protected TLSPolicy tlsPolicy = TLSPolicy.optional;

    /**
     * Compression policy currently in use for this connection.
     */
    protected CompressionPolicy compressionPolicy = CompressionPolicy.disabled;

    public static Collection<SocketConnection> getInstances() {
        return instances.keySet();
    }

    /**
     * Create a new session using the supplied socket.
     *
     * @param backupDeliverer the packet deliverer this connection will use when socket is closed.
     * @param socket the socket to represent.
     * @param isSecure true if this is a secure connection.
     * @throws NullPointerException if the socket is null.
     */
    public SocketConnection(PacketDeliverer backupDeliverer, boolean isSecure)
            throws IOException {
        this.secure = isSecure;
        this.backupDeliverer = backupDeliverer;
        instances.put(this, "");
    }

    /**
     * Secures the plain connection by negotiating TLS with the client. When connecting
     * to a remote server then <tt>clientMode</tt> will be <code>true</code> and
     * <tt>remoteServer</tt> is the server name of the remote server. Otherwise <tt>clientMode</tt>
     * will be <code>false</code> and  <tt>remoteServer</tt> null.
     *
     * @param clientMode boolean indicating if this entity is a client or a server.
     * @param remoteServer server name of the remote server we are connecting to or <tt>null</tt>
     *        when not in client mode.
     * @throws IOException if an error occured while securing the connection.
     */
    abstract public void startTLS(boolean clientMode, String remoteServer) throws IOException;

    /**
     * Start using compression for this connection. Compression will only be available after TLS
     * has been negotiated. This means that a connection can never be using compression before
     * TLS. However, it is possible to use compression without TLS.
     *
     * @throws IOException if an error occured while starting compression.
     */
    abstract public void startCompression() throws IOException;

    public boolean validate() {
        if (isClosed()) {
            return false;
        }
        boolean allowedToWrite = false;
        try {
            requestWriting();
            allowedToWrite = true;
            // Register that we started sending data on the connection
            writeStarted();
            writer.write(" ");
            writer.flush();
        }
        catch (Exception e) {
            Log.warn("Closing no longer valid connection" + "\n" + this.toString(), e);
            close();
        }
        finally {
            // Register that we finished sending data on the connection
            writeFinished();
            if (allowedToWrite) {
                releaseWriting();
            }
        }
        return !isClosed();
    }

    public void init(Session owner) {
        session = owner;
    }

    public Object registerCloseListener(ConnectionCloseListener listener, Object handbackMessage) {
        Object status = null;
        if (isClosed()) {
            listener.onConnectionClose(handbackMessage);
        }
        else {
            status = listeners.put(listener, handbackMessage);
        }
        return status;
    }

    public Object removeCloseListener(ConnectionCloseListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Returns the Writer used to send data to the connection. The writer should be
     * used with caution. In the majority of cases, the {@link #deliver(Packet)}
     * method should be used to send data instead of using the writer directly.
     * You must synchronize on the writer before writing data to it to ensure
     * data consistency:
     *
     * <pre>
     *  Writer writer = connection.getWriter();
     * synchronized(writer) {
     *     // write data....
     * }</pre>
     *
     * @return the Writer for this connection.
     */
    public Writer getWriter() {
        return writer;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public TLSPolicy getTlsPolicy() {
        return tlsPolicy;
    }

    /**
     * Sets whether TLS is mandatory, optional or is disabled. When TLS is mandatory clients
     * are required to secure their connections or otherwise their connections will be closed.
     * On the other hand, when TLS is disabled clients are not allowed to secure their connections
     * using TLS. Their connections will be closed if they try to secure the connection. in this
     * last case.
     *
     * @param tlsPolicy whether TLS is mandatory, optional or is disabled.
     */
    public void setTlsPolicy(TLSPolicy tlsPolicy) {
        this.tlsPolicy = tlsPolicy;
    }

    public CompressionPolicy getCompressionPolicy() {
        return compressionPolicy;
    }

    /**
     * Sets whether compression is enabled or is disabled.
     *
     * @param compressionPolicy whether Compression is enabled or is disabled.
     */
    public void setCompressionPolicy(CompressionPolicy compressionPolicy) {
        this.compressionPolicy = compressionPolicy;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Sets the number of milliseconds a connection has to be idle to be closed. Sending
     * stanzas to the client is not considered as activity. We are only considering the
     * connection active when the client sends some data or hearbeats (i.e. whitespaces)
     * to the server.
     *
     * @param timeout the number of milliseconds a connection has to be idle to be closed.
     */
    public void setIdleTimeout(long timeout) {
        this.idleTimeout = timeout;
    }

    public int getMajorXMPPVersion() {
        return majorVersion;
    }

    public int getMinorXMPPVersion() {
        return minorVersion;
    }

    /**
     * Sets the XMPP version information. In most cases, the version should be "1.0".
     * However, older clients using the "Jabber" protocol do not set a version. In that
     * case, the version is "0.0".
     *
     * @param majorVersion the major version.
     * @param minorVersion the minor version.
     */
    public void setXMPPVersion(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language code that should be used for this connection (e.g. "en").
     *
     * @param language the language code.
     */
    public void setLanaguage(String language) {
        this.language = language;
    }

    public boolean isFlashClient() {
        return flashClient;
    }

    /**
     * Sets whether the connected client is a flash client. Flash clients need to
     * receive a special character (i.e. \0) at the end of each xml packet. Flash
     * clients may send the character \0 in incoming packets and may start a
     * connection using another openning tag such as: "flash:client".
     *
     * @param flashClient true if the if the connection is a flash client.
     */
    public void setFlashClient(boolean flashClient) {
        this.flashClient = flashClient;
    }

    /**
     * Returns the packet deliverer to use when delivering a packet over the socket fails. The
     * packet deliverer will retry to send the packet using some other connection, will store
     * the packet offline for later retrieval or will just drop it.
     *
     * @return the packet deliverer to use when delivering a packet over the socket fails.
     */
    public PacketDeliverer getPacketDeliverer() {
        return backupDeliverer;
    }

    public void close() {
        boolean wasClosed = false;
        synchronized (this) {
            if (!isClosed()) {
                try {
                    if (session != null) {
                        session.setStatus(Session.STATUS_CLOSED);
                    }
                    boolean allowedToWrite = false;
                    try {
                        requestWriting();
                        allowedToWrite = true;
                        // Register that we started sending data on the connection
                        writeStarted();
                        writer.write("</stream:stream>");
                        if (flashClient) {
                            writer.write('\0');
                        }
                        writer.flush();
                    }
                    catch (IOException e) {
                        // Do nothing
                    }
                    finally {
                        // Register that we finished sending data on the connection
                        writeFinished();
                        if (allowedToWrite) {
                            releaseWriting();
                        }
                    }
                }
                catch (Exception e) {
                    Log.error(LocaleUtils.getLocalizedString("admin.error.close")
                            + "\n" + this.toString(), e);
                }
                closeConnection();
                wasClosed = true;
            }
        }
        if (wasClosed) {
            notifyCloseListeners();
        }
    }

    public void systemShutdown() {
        deliverRawText("<stream:error><system-shutdown " +
                "xmlns='urn:ietf:params:xml:ns:xmpp-streams'/></stream:error>");
        close();
    }

    void writeStarted() {
        writeStarted = System.currentTimeMillis();
    }

    void writeFinished() {
        writeStarted = -1;
    }

    /**
     * Returns true if the socket was closed due to a bad health. The socket is considered to
     * be in a bad state if a thread has been writing for a while and the write operation has
     * not finished in a long time or when the client has not sent a heartbeat for a long time.
     * In any of both cases the socket will be closed.
     *
     * @return true if the socket was closed due to a bad health.s
     */
    boolean checkHealth() {
        // Check that the sending operation is still active
        long writeTimestamp = writeStarted;
        if (writeTimestamp > -1 && System.currentTimeMillis() - writeTimestamp >
                JiveGlobals.getIntProperty("xmpp.session.sending-limit", 60000)) {
            // Close the socket
            if (Log.isDebugEnabled()) {
                Log.debug("Closing connection: " + this + " that started sending data at: " +
                        new Date(writeTimestamp));
            }
            forceClose();
            return true;
        }
        else {
            // Check if the connection has been idle. A connection is considered idle if the client
            // has not been receiving data for a period. Sending data to the client is not
            // considered as activity.
            if (idleTimeout > -1 && socketReader != null &&
                    System.currentTimeMillis() - socketReader.getLastActive() > idleTimeout) {
                // Close the socket
                if (Log.isDebugEnabled()) {
                    Log.debug("Closing connection that has been idle: " + this);
                }
                forceClose();
                return true;
            }
        }
        return false;
    }

    protected void release() {
        writeStarted = -1;
        instances.remove(this);
    }

    /**
     * Forces the connection to be closed immediately no matter if closing the socket takes
     * a long time. This method should only be called from {@link SocketSendingTracker} when
     * sending data over the socket has taken a long time and we need to close the socket, discard
     * the connection and its session.
     */
    protected void forceClose() {
        if (session != null) {
            // Set that the session is closed. This will prevent threads from trying to
            // deliver packets to this session thus preventing future locks.
            session.setStatus(Session.STATUS_CLOSED);
        }
        closeConnection();
        // Notify the close listeners so that the SessionManager can send unavailable
        // presences if required.
        notifyCloseListeners();
    }

    abstract protected void closeConnection();

    public void deliver(Packet packet) throws UnauthorizedException, PacketException {
        if (isClosed()) {
            backupDeliverer.deliver(packet);
        }
        else {
            try {
                // Invoke the interceptors before we send the packet
                InterceptorManager.getInstance().invokeInterceptors(packet, session, false, false);
                boolean errorDelivering = false;
                boolean allowedToWrite = false;
                try {
                    requestWriting();
                    allowedToWrite = true;
                    xmlSerializer.write(packet.getElement());
                    if (flashClient) {
                        writer.write('\0');
                    }
                    xmlSerializer.flush();
//                    writer.flush();
                }
                catch (Exception e) {
                    Log.debug("Error delivering packet" + "\n" + this.toString(), e);
                    errorDelivering = true;
                }
                finally {
                    if (allowedToWrite) {
                        releaseWriting();
                    }
                }
                if (errorDelivering) {
                    close();
                    // Retry sending the packet again. Most probably if the packet is a
                    // Message it will be stored offline
                    backupDeliverer.deliver(packet);
                }
                else {
                    // Invoke the interceptors after we have sent the packet
                    if (session != null) { // TODO fix this (it's a hack to deal with cloudrouting)
                        InterceptorManager.getInstance().invokeInterceptors(packet, session, false, true);
                        session.incrementServerPacketCount();
                    }
                }
            }
            catch (PacketRejectedException e) {
                // An interceptor rejected the packet so do nothing
            }
        }
    }

    public void deliverRawText(String text) {
        if (!isClosed()) {
            boolean errorDelivering = false;
            boolean allowedToWrite = false;
            try {
                requestWriting();
                allowedToWrite = true;
                // Register that we started sending data on the connection
                writeStarted();
                writer.write(text);
                if (flashClient) {
                    writer.write('\0');
                }
                writer.flush();
            }
            catch (Exception e) {
                Log.debug("Error delivering raw text" + "\n" + this.toString(), e);
                errorDelivering = true;
            }
            finally {
                // Register that we finished sending data on the connection
                writeFinished();
                if (allowedToWrite) {
                    releaseWriting();
                }
            }
            if (errorDelivering) {
                close();
            }
        }
    }

    /**
     * Notifies all close listeners that the connection has been closed.
     * Used by subclasses to properly finish closing the connection.
     */
    protected void notifyCloseListeners() {
        synchronized (listeners) {
            for (ConnectionCloseListener listener : listeners.keySet()) {
                try {
                    listener.onConnectionClose(listeners.get(listener));
                }
                catch (Exception e) {
                    Log.error("Error notifying listener: " + listener, e);
                }
            }
        }
    }

    protected void requestWriting() throws Exception {
        for (;;) {
            if (writing.compareAndSet(false, true)) {
                // We are now in writing mode and only we can write to the socket
                return;
            }
            else {
                // Check health of the socket
                if (checkHealth()) {
                    // Connection was closed then stop
                    throw new Exception("Probable dead connection was closed");
                }
                else {
                    Thread.sleep(1);
                }
            }
        }
    }

    protected void releaseWriting() {
        writing.compareAndSet(true, false);
    }

    public void setSocketReader(SocketReader socketReader) {
        this.socketReader = socketReader;
    }
}