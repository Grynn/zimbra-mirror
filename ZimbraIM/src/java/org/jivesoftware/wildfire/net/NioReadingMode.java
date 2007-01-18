package org.jivesoftware.wildfire.net;
import com.zimbra.cs.im.xp.parse.ApplicationException;

import org.apache.mina.common.ByteBuffer;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.Connection;
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
public class NioReadingMode extends SocketReadingMode implements NioCompletionHandler {

    private static byte[] sXMLPrologBytes = new byte[] { '<', '?', 'x', 'm', 'l' };
    private static byte[] sXMLPrologEndBytes = new byte[] { '?', '>' };
    
    static enum State {
        IN_PROLOG,  // look for <? xml ... ?>
        NO_SESSION,
        START_SASL,
        START_TLS,
        SASL_COMPLETING,
        START_COMPRESSION,
        RUNNING;
    }

    private NioParser mParser = null;
    private byte[] mInitialBuf = null;
    private State mState = State.IN_PROLOG;
    
    /**
     * @param sock
     * @param socketReader
     */
    public NioReadingMode(SocketReader socketReader)  {
        super(socketReader);
    }
    
    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.SocketReadingMode#run()
     */
    public void run() {
        assert(false);
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
        Log.info("Processing Element: "+e.asXML());
        
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
    
    
    /**
     * 
     * just like lhs.indexOf(rhs) for Strings
     * 
     * @param lhs
     * @param rhs
     * @return
     */
    private int byteArrayIndexOf(byte[] lhs, byte[] rhs) {
        if (lhs.length < rhs.length)
            throw new IllegalArgumentException("byteArrayIndexOf: lhs must be larger or same length as rhs (parameters in wrong order?)");
        
        for (int start = 0; start < lhs.length; start++) {
            if (lhs.length - start < rhs.length)
                return -1;
            
            if (lhs[start] == rhs[0]) {
                boolean eq = true;
                for (int i = 1; i < rhs.length; i++) {
                    if (lhs[start+i] != rhs[i]) {
                        eq = false;
                        break;
                    }
                }
                if (eq)
                    return start;
            }
        }
        return -1; 
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
            // TODO, eliminate double-buffering here (make parser ByteBuffer-aware)
            // be careful: parser assumes it can take ownership of byte[], need to modify code
            // to remove this assumption if we convert things to ByteBuffers
            byte[] buf= new byte[bb.remaining()];
            bb.get(buf);
            
            switch (mState) {
                case IN_PROLOG: // find the xml prolog <?xml...?>
                    assert(mParser == null);
                    
                    // append new data into initial buf (create it if necessary) 
                    if (mInitialBuf != null) {
                        byte[] newInitial = new byte[mInitialBuf.length + buf.length];
                        System.arraycopy(mInitialBuf, 0, newInitial, 0, mInitialBuf.length);
                        System.arraycopy(buf, 0, newInitial, mInitialBuf.length, buf.length);
                        mInitialBuf = newInitial;
                    } else {
                        mInitialBuf = buf;
                    }

                    // see if we can find the <? xml ... ?> (+ 4 bytes of text)...
                    if (mInitialBuf != null && mInitialBuf.length >= sXMLPrologBytes.length) {
                        int index = byteArrayIndexOf(mInitialBuf, sXMLPrologBytes);
                        if (index >= 0) {
                            int endIdx = byteArrayIndexOf(mInitialBuf, sXMLPrologEndBytes);
                            if (endIdx > 0) {
                                if (endIdx <= index) {
                                    StringBuilder sb = new StringBuilder();
                                    for (byte b : mInitialBuf) 
                                        sb.append((char)b);
                                    Log.info("Garbage at beginning of stream: \""+sb.toString()+"\"");
                                    socketReader.connection.close();
                                    return;
                                } else {
                                    int endPrologIdx = endIdx + sXMLPrologEndBytes.length;
                                    int leftoverLen = mInitialBuf.length-endPrologIdx;
                                    if (leftoverLen >= 4) { // parser needs 4 initial bytes to start parsing
                                        Log.info("Handshaking complete for client");
                                        byte[] leftover = new byte[mInitialBuf.length - endPrologIdx];
                                        System.arraycopy(mInitialBuf, endPrologIdx, leftover, 0, leftoverLen);
                                        mParser = new NioParser(Locale.getDefault());
                                        mState = State.NO_SESSION;
                                        mParser.parseBytes(leftover, leftover.length);
                                        for (Element e : mParser.getCompletedElements()) {
                                            process(e);
                                        }
                                        mParser.clearCompletedElements();
                                        mInitialBuf = null;
                                    }
                                }
                            }
                        }
                    }
                        
                    // sanity check
                    if (mParser == null && mInitialBuf != null && mInitialBuf.length > 100) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : mInitialBuf) 
                            sb.append((char)b);
                        Log.info("Invalid handshake at beginning of stream: \""+sb.toString()+"\"");
                        socketReader.connection.close();
                        mInitialBuf = null;
                        return;
                    }
                    break;
                default:
                {
                    mParser.parseBytes(buf,  buf.length);
                    for (Element e : mParser.getCompletedElements()) {
                        process(e);
                    }
                    mParser.clearCompletedElements();
                }
                break;
            }
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
