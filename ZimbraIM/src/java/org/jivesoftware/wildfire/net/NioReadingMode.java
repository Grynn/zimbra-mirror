/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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

import com.zimbra.cs.im.xp.parse.ApplicationException;

import org.apache.mina.common.ByteBuffer;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.Connection;
import org.jivesoftware.wildfire.net.NioParser.NioParserException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import java.util.Date;
import java.util.Locale;

/**
 * TODOs
 *    -- parser to understand ByteBuffers (save extra copy)
 *
 *    
 * 
 */
class NioReadingMode extends SocketReadingMode implements NioCompletionHandler {
    
    static enum State {
        NO_SESSION,
        START_SASL,
        START_TLS,
        SASL_COMPLETING,
        START_COMPRESSION,
        RUNNING;
    }

    private NioParser mParser = null;
    private State mState = State.NO_SESSION;
    
    /**
     * @param sock
     * @param socketReader
     */
    public NioReadingMode(SocketReader socketReader)  {
        super(socketReader);
        
        mParser = new NioParser(Locale.getDefault());
    }
    
    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.SocketReadingMode#run()
     */
    public void run() {
        throw new UnsupportedOperationException("run() method not supported for Nio SocketReadingMode");
    }
    
    /**
     * @return
     */
    long getLastActive() {
        return new Date().getTime();
    }
    
    /**
     * @param e
     * @throws Exception
     */
    private void process(Element e) throws Exception {
        Log.debug("NioReadingMode: Processing Element: "+e.asXML());
        
        switch (mState) {
            case NO_SESSION:
                assert("stream:stream".equals(e.getQualifiedName()));
                socketReader.createSession(e);
                mState = State.RUNNING;
                break;
            case START_SASL:
                if (authenticateClient(e)) {
                    mState = State.SASL_COMPLETING;
                }
                break;
            case SASL_COMPLETING:
                if ("stream:stream".equals(e.getQualifiedName())) {
                    saslSuccessful();
                    mState = State.RUNNING;
                }
                break;
            case START_TLS:
                if ("stream:stream".equals(e.getQualifiedName())) {
                    tlsNegotiated();
                    mState = State.RUNNING;
                }
                break;
            case START_COMPRESSION:
                break;
            case RUNNING:
                String name = e.getName();
                if ("auth".equals(name)) {
                    if (authenticateClient(e)) {
                        mState = State.SASL_COMPLETING;
                    }
                } else if ("starttls".equals(name)) {
                    if (negotiateTLS())
                        mState = State.START_TLS;
                } else {
                    socketReader.process(e);
                }
                break;
            default:
                throw new IllegalStateException("Unknown or Invalid parser state: "+mState);
        }
    }
    
    protected boolean authenticateClient(Element doc) throws DocumentException, IOException, XmlPullParserException { 
//      Ensure that connection was secured if TLS was required
        if (socketReader.connection.getTlsPolicy() == Connection.TLSPolicy.required &&
                    !socketReader.connection.isSecure()) {
            socketReader.closeNeverSecuredConnection();
            return false;
        }
        
        SASLAuthentication.Status status = SASLAuthentication.handle(socketReader.session, doc);
        switch(status) {
            case needResponse:
                mState = State.START_SASL;
                return false;
            case failed:
                mState = State.RUNNING;
                return false;
            case authenticated:
                mState = State.RUNNING;
                return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.NioCompletionHandler#nioClosed()
     */
    public void nioClosed() {
        socketReader.connection.close();
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.NioCompletionHandler#nioReadCompleted(org.apache.mina.common.ByteBuffer)
     */
    public void nioReadCompleted(ByteBuffer bb) {
        boolean closeIt = false;
        
        try {
            mParser.parseBytes(bb);
            for (Element e : mParser.getCompletedElements()) {
                process(e);
            }
            mParser.clearCompletedElements();
        } catch (NioParserException e) {
            Log.debug(e.toString());
            closeIt = true;
        } catch (ApplicationException e) {
            // parse error
            closeIt = true;
            e.printStackTrace();
        } catch (EOFException eof) {
            closeIt = true;
            // Normal disconnect
        } catch (SocketException se) {
            closeIt = true;
            // The socket was closed. The server may close the connection for several
            // reasons (e.g. user requested to remove his account). Do nothing here.
            se.printStackTrace();
        } catch (AsynchronousCloseException ace) {
            closeIt = true;
            // The socket was closed.
            ace.printStackTrace();
        } catch (IOException e) {
            closeIt = true;
            e.printStackTrace();
        } catch (XmlPullParserException ie) {
            closeIt = true;
            // It is normal for clients to abruptly cut a connection
            // rather than closing the stream document. Since this is
            // normal behavior, we won't log it as an error.
            // Log.error(LocaleUtils.getLocalizedString("admin.disconnect"),ie);
            ie.printStackTrace();
        } catch (Exception e) {
            closeIt = true;
            if (socketReader.session != null) {
                Log.warn(LocaleUtils.getLocalizedString("admin.error.stream") + ". Session: " +
                            socketReader.session, e);
            } else {
                Log.warn(LocaleUtils.getLocalizedString("admin.error.stream") + e);
            }
        } finally {
            if (closeIt) {
                try {
                    socketReader.connection.close();
                } catch (Exception e) {}
            }
        }
    }

    protected boolean compressClient(Element doc) throws IOException, XmlPullParserException {
        return false;
    }
}
