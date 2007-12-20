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

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import org.dom4j.Element;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.xmlpull.v1.XmlPullParserException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;

/**
 * Process incoming packets using a blocking model. Once a session has been created
 * an endless loop is used to process incoming packets. Packets are processed
 * sequentially.
 *
 * @author Gaston Dombiak
 */
class BlockingReadingMode extends SocketReadingMode {
    
    Socket mRealSocket;

    public BlockingReadingMode(Socket socket, SocketReader socketReader) {
        super(socketReader);
        mRealSocket = socket;
    }

    /**
     * A dedicated thread loop for reading the stream and sending incoming
     * packets to the appropriate router.
     */
    public void run() {
        try {
            socketReader.setInput(new InputStreamReader(
                        ServerTrafficCounter.wrapInputStream(mRealSocket.getInputStream()), CHARSET));

            if (socketReader.session == null) {
                // The session is NULL, so this is an incoming socket.  We need to
                // parse the stream tag and create a session
                try {
                    // Read in the opening tag and prepare for packet stream
                    socketReader.createSessionForBlockingMode();
                }
                catch (IOException e) {
                    Log.debug("Error creating session", e);
                    throw e;
                }
            } else {
                // session is already set -- this is an OUTGOING socket, we'll skip 
                // the initial stream element for now (should be reporting it to the session TODO)
                socketReader.getInitialStreamElement();
            }

            // Read the packet stream until it ends
            if (socketReader.session != null) {
                readStream();
            }

        }
        catch (EOFException eof) {
            // Normal disconnect
        }
        catch (SocketException se) {
            // The socket was closed. The server may close the connection for several
            // reasons (e.g. user requested to remove his account). Do nothing here.
            se.printStackTrace();
        }
        catch (AsynchronousCloseException ace) {
            // The socket was closed.
            ace.printStackTrace();
        }
        catch (XmlPullParserException ie) {
            // It is normal for clients to abruptly cut a connection
            // rather than closing the stream document. Since this is
            // normal behavior, we won't log it as an error.
            // Log.error(LocaleUtils.getLocalizedString("admin.disconnect"),ie);
            ie.printStackTrace();
        }
        catch (Exception e) {
            if (socketReader.session != null) {
                Log.warn(LocaleUtils.getLocalizedString("admin.error.stream") + ". Session: " +
                        socketReader.session, e);
            }
        }
        finally {
            if (socketReader.session != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("Logging off " + socketReader.session.getAddress() + " on " + socketReader.connection);
                }
                try {
                    socketReader.session.getConnection().close();
                }
                catch (Exception e) {
                    Log.warn(LocaleUtils.getLocalizedString("admin.error.connection")
                            + "\n" + socketReader.toString());
                }
            }
            else {
                // Close and release the created connection
                socketReader.connection.close();
                Log.debug(LocaleUtils.getLocalizedString("admin.error.connection")
                        + "\n" + socketReader.toString());
            }
            socketReader.shutdown();
        }
    }

    /**
     * Read the incoming stream until it ends.
     */
    private void readStream() throws Exception {
        socketReader.open = true;
        while (socketReader.open) {
//            Element doc = socketReader.reader.parseDocument().getRootElement();
            Element doc = socketReader.getNextElement();
            if (doc == null) {
                // Stop reading the stream since the client has sent an end of
                // stream element and probably closed the connection.
                return;
            }
            String tag = doc.getName();
            if ("starttls".equals(tag)) {
                // Negotiate TLS
                if (negotiateTLS()) {
                    tlsNegotiated();
                }
                else {
                    socketReader.open = false;
                    socketReader.session = null;
                }
            }
            else if ("auth".equals(tag)) {
                // User is trying to authenticate using SASL
                if (authenticateClient(doc)) {
                    // SASL authentication was successful so open a new stream and offer
                    // resource binding and session establishment (to client sessions only)
                    saslSuccessful();
                }
                else if (socketReader.connection.isClosed()) {
                    socketReader.open = false;
                    socketReader.session = null;
                }
            }
            else if ("compress".equals(tag))
            {
                // Client is trying to initiate compression
                if (compressClient(doc)) {
                    // Compression was successful so open a new stream and offer
                    // resource binding and session establishment (to client sessions only)
                    compressionSuccessful();
                }
            }
            else {
                socketReader.process(doc);
            }
        }
    }

    protected void tlsNegotiated() throws XmlPullParserException, IOException {
        StdSocketConnection stdConnect = (StdSocketConnection)(socketReader.connection);
        
        // Reset the parser to use the new reader
        socketReader.setInput(new InputStreamReader(
                    stdConnect.getTLSStreamHandler().getInputStream(), CHARSET));
        
        // Skip new stream element
        socketReader.skipNextStartTag();
        super.tlsNegotiated();
    }

    protected void saslSuccessful() throws XmlPullParserException, IOException {
        // Reset the parser since a new stream header has been sent from the client
        socketReader.resetInput();
        
        // Skip the opening stream sent by the client
        socketReader.skipNextStartTag();
        
        super.saslSuccessful();
    }

    protected boolean compressClient(Element doc) throws XmlPullParserException, IOException {
        boolean answer = super.compressClient(doc);
        StdSocketConnection stdConnect = (StdSocketConnection)(socketReader.connection);
        
        if (answer) {
            // Reset the parser since a new stream header has been sent from the client
            if (stdConnect.getTLSStreamHandler() == null) {
                ZInputStream in = new ZInputStream(
                        ServerTrafficCounter.wrapInputStream(mRealSocket.getInputStream()));
                in.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
                socketReader.setInput(new InputStreamReader(in, CHARSET));
            }
            else {
                ZInputStream in = new ZInputStream(
                            stdConnect.getTLSStreamHandler().getInputStream());
                in.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
                socketReader.setInput(new InputStreamReader(in, CHARSET));
            }
        }
        return answer;
    }

    protected void compressionSuccessful() throws XmlPullParserException, IOException {
        // Skip the opening stream sent by the client
        socketReader.skipNextStartTag();
        super.compressionSuccessful();
    }
}
