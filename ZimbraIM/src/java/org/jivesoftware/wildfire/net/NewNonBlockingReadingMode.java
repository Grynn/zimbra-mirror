package org.jivesoftware.wildfire.net;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.zimbra.cs.im.xp.parse.ApplicationException;
import com.zimbra.cs.im.xp.parse.EntityParser;
import com.zimbra.cs.im.xp.parse.NonblockingCallbackParser;
import com.zimbra.cs.im.xp.parse.OpenEntity;
import com.zimbra.cs.im.xp.parse.Tester;
import com.zimbra.cs.im.xp.tok.PrologParser;
import com.zimbra.cs.im.xp.util.NonblockingInputStream;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.Connection;
import org.jivesoftware.wildfire.SessionManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;
import java.util.Locale;

/**
 * TODOs
 *    -- parser to understand ByteBuffers (save extra copy)
 *    -- use real prolog parsing
 *
 *    
 * 
 */
public class NewNonBlockingReadingMode extends SocketReadingMode implements FakeSocket.MinaFakeSocket.ReadReadyCallback {

    private static byte[] sXMLPrologBytes = new byte[] { '<', '?', 'x', 'm', 'l' };
    private static byte[] sXMLPrologEndBytes = new byte[] { '?', '>' };

    NonblockingCallbackParser mParser = null;
    byte[] mInitialBuf = null;
    boolean mAuthenticated = false;
    
    public NewNonBlockingReadingMode(FakeSocket sock,  SocketReader socketReader)  {
        super(sock, socketReader);
        
        ((FakeSocket.MinaFakeSocket)socket).setReadReadyCallback(this);
    }
    public void run() {}
    
    static enum State {
        NO_SESSION,
        START_SASL,
        SASL_COMPLETING,
        START_COMPRESSION,
        RUNNING;
    }
    
    long getLastActive() {
        return new Date().getTime();
    }
    
    private State mState = State.NO_SESSION;
    
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
            case START_COMPRESSION:
                break;
            case RUNNING:
                String name = e.getName();
                if ("auth".equals(name)) {
                    if (authenticateClient(e)) {
                        mState = State.SASL_COMPLETING;
                    }
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

    public void messageReceived(ByteBuffer bb) {
        boolean closeIt = false;
        
        try {
            // TODO, eliminate double-buffering here (make parser ByteBuffer-aware)
            byte[] buf= new byte[bb.remaining()];
            bb.get(buf);
            
            if (mParser == null) {
                // find the xml prolog <?xml...?>
                
                if (mInitialBuf != null) {
                    byte[] newInitial = new byte[mInitialBuf.length + buf.length];
                    System.arraycopy(mInitialBuf, 0, newInitial, 0, mInitialBuf.length);
                    System.arraycopy(buf, 0, newInitial, mInitialBuf.length, buf.length);
                    mInitialBuf = newInitial;
                } else {
                    int bufOff = 0;
                    for (bufOff = 0; bufOff  < buf.length; bufOff++) {
                        if (buf[bufOff] == '<')
                            break;
                    }
                    if (bufOff == 0)
                        mInitialBuf = buf;
                    else {
                        if (bufOff < buf.length){
                            mInitialBuf = new byte[buf.length - bufOff];
                            System.arraycopy(buf, bufOff, mInitialBuf, 0, buf.length - bufOff);
                        } else {
                            // ignore garbage at start
                        }
                    }
                }
                
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
                                socket.close();
                                return;
                            } else {
                                int endPrologIdx = endIdx + sXMLPrologEndBytes.length;
                                byte[] leftover = new byte[mInitialBuf.length - endPrologIdx];
                                System.arraycopy(mInitialBuf, endPrologIdx, leftover, 0, mInitialBuf.length-endPrologIdx);
                                
                                Log.info("Handshaking complete for client");
                                mParser = new NonblockingCallbackParser(Locale.getDefault());
                                
                                mParser.parseBytes(leftover, leftover.length);
//                                if (mParser.getInitialStreamElement() != null) {
//                                    process(mParser.getInitialStreamElement());
//                                    mParser.clearInitialStreamElement();
//                                }
                                for (Element e : mParser.getCompletedElements()) {
                                    process(e);
                                }
                                mParser.clearCompletedElements();
                            }
                        }
                    } else {
                        if (mInitialBuf.length > 100) {
                            StringBuilder sb = new StringBuilder();
                            for (byte b : mInitialBuf) 
                                sb.append((char)b);
                            Log.info("Invalid handshake at beginning of stream: \""+sb.toString()+"\"");
                            socket.close();
                            return;
                        }
                    }
                }
            } else {
                mParser.parseBytes(buf,  buf.length);
//                if (mState == State.NO_SESSION && mParser.getInitialStreamElement() != null)
//                    process(mParser.getInitialStreamElement());
                for (Element e : mParser.getCompletedElements()) {
                    process(e);
                }
                mParser.clearCompletedElements();
            }
        } catch (ApplicationException e) {

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
                    socket.close();
                } catch (Exception e) {}
            }
        }
        
            
        
//        isReading = false;
//        socketReader.open = true;
//        
//        streamReader = new StreamReader();
//        
//        
//            if (false) {
//                if (!mStarted) {
//                    byte[] bbuf = new byte[255];
//                    int read = socket.getInputStream().read(bbuf, 0, bbuf.length);
//                    Charset encoder = Charset.defaultCharset();
//                    CharBuffer charBuffer = encoder.decode(ByteBuffer.wrap(bbuf));
//                    charBuffer.flip();
//                    char[] buf = charBuffer.array();
//                    startString += new String(buf);
//                    startString = startString.trim();
//
//                    boolean starts = startString.startsWith(STREAM_START);
//                    boolean ends = startString.endsWith(">"); 
//                    if (starts && ends) {
//                        // Found an stream:stream tag...
//                        if (!sessionCreated) {
//                            sessionCreated = true;
//                            socketReader.reader.getXPPParser().setInput(new StringReader(
//                                        startString + ((startString.indexOf("</stream:stream") == -1) ? "</stream:stream>" :
//                                        "")));
//                            socketReader.createSession();
//                        }
//                        else if (awaytingSasl) {
//                            awaytingSasl = false;
//                            saslSuccessful();
//                        }
//                        else if (awaitingForCompleteCompression) {
//                            awaitingForCompleteCompression = false;
//                            compressionSuccessful();
//                        }
//                        mStarted = true;
//                        return;
//                    } else {
//                        if (startString.length() > 255)
//                            socket.close();
//                    }
//                }
//
//                // Check if the socket is open
//                if (socketReader.open) {
//                    // Verify semaphore and if there are data into the socket.
//                    if (!isReading && !isScheduled) {
//                        try {
//                            // Semaphore to avoid concurrent schedule of the same read operation.
//                            isScheduled = true;
//                            // Schedule execution with executor
//                            IOExecutor.execute(streamReader);
//                        }
//                        catch (Exception e) {
//                            if (socketReader.session != null) {
//                                Log.warn(LocaleUtils.getLocalizedString("admin.error.stream") +
//                                            ". Session: " +
//                                            socketReader.session, e);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        catch (Exception e) {
//            socketReader.shutdown();
//            // There is an exception...
//            Log.error(e);
//        }
//        if (!socketReader.open) {
//            socketReader.shutdown();
//        }
    }

    protected void tlsNegotiated() throws XmlPullParserException, IOException {
//        XmlPullParser xpp = socketReader.reader.getXPPParser();
//        InputStream is = socketReader.connection.getTLSStreamHandler().getInputStream();
//        xpp.setInput(new InputStreamReader(is, CHARSET));
////        xmlLightWeightParser.setInput( is, CHARSET );
//        super.tlsNegotiated();
    }

    protected boolean compressClient(Element doc) throws IOException, XmlPullParserException {
//        assert(false); // TODO FIXME!
//        boolean answer = super.compressClient(doc);
//        if (answer) {
//            XmlPullParser xpp = socketReader.reader.getXPPParser();
//            // Reset the parser since a new stream header has been sent from the client
//            if (socketReader.connection.getTLSStreamHandler() == null) {
//                InputStream is;
////                if (socketChannel != null) {
////                    // DANIELE: Create an inputstream using the utility class ChannelInputStream.
////                    is = new ChannelInputStream(socketChannel);
////                }
////                else {
////                    is = socket.getInputStream();
////                }
////                is = ServerTrafficCounter.wrapInputStream(is);
////
////                ZInputStream in = new ZInputStream(is);
////                in.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
////                xpp.setInput(new InputStreamReader(in, CHARSET));
//            }
//            else {
//                ZInputStream in = new ZInputStream(
//                        socketReader.connection.getTLSStreamHandler().getInputStream());
//                in.setFlushMode(JZlib.Z_PARTIAL_FLUSH);
//                xpp.setInput(new InputStreamReader(in, CHARSET));
////                xmlLightWeightParser.setInput( in, CHARSET );
//            }
//        }
//        return answer;
        return false;
    }

//    class StreamReader implements Runnable {

        /*
         * This method is invoked when client send data to the channel.
         *
         */
//        public void run() {
//            System.out.println("READ!");
//            try {
//                // If no other reading operations are perform
//                if (!isReading) {
//                    // Change the semaphore status
//                    isReading = true;
//                    // Call the XML light-wieght parser to read data...
//                    xmlLightWeightParser.read();
//                    // Check if the parser has found a complete message...
//                    if (xmlLightWeightParser.areThereMsgs()) {
//                        // Process every message found
//                        String[] msgs = xmlLightWeightParser.getMsgs();
//                        for (int i = 0; i < msgs.length; i++) {
//                            //System.out.println( "Processing " + msgs[ i ] );
//                            readStream(msgs[i]);
//                        }
//                    }
//                }
//            }
//            catch (IOException e) {
//                if (socketReader.session != null) {
//                    // DANIELE: Remove session from SessionManager. I don't know if
//                    // this is the easy way.
//                    // TODO Review this. Closing the connection should be used???
//                    SessionManager.getInstance().removeSession(
//                            SessionManager.getInstance().getSession(
//                                    socketReader.session.getAddress()));
//                }
//                try {
//                    xmlLightWeightParser.getChannel().close();
//                }
//                catch (IOException e1) {
//                }
//                // System.out.println( "Client disconnecting" );
//            }
//            catch (Exception e) {
//                if (socketReader.session != null) {
//                    Log.warn(LocaleUtils.getLocalizedString("admin.error.stream") + ". Session: " +
//                            socketReader.session, e);
//                }
//                e.printStackTrace();
//            }
//            finally {
//                isReading = false;
//                isScheduled = false;
//            }
//        }
//
//        /**
//         * Process a single message
//         */
//        private void readStream(String msg) throws Exception {
//
//            if (msg.trim().startsWith(STREAM_START)) {
//                // Found an stream:stream tag...
//                if (!sessionCreated) {
//                    sessionCreated = true;
//                    socketReader.reader.getXPPParser().setInput(new StringReader(
//                            msg + ((msg.indexOf("</stream:stream") == -1) ? "</stream:stream>" :
//                                    "")));
//                    socketReader.createSession();
//                }
//                else if (awaytingSasl) {
//                    awaytingSasl = false;
//                    saslSuccessful();
//                }
//                else if (awaitingForCompleteCompression) {
//                    awaitingForCompleteCompression = false;
//                    compressionSuccessful();
//                }
//                return;
//            }
//
//            // Create dom in base on the string.
//            Element doc = socketReader.reader.parseDocument(msg).getRootElement();
//            if (doc == null) {
//                // No document found.
//                return;
//            }
//            String tag = doc.getName();
//            if ("starttls".equals(tag)) {
//                // Negotiate TLS
//                if (negotiateTLS()) {
//                    tlsNegotiated();
//                }
//                else {
//                    socketReader.open = false;
//                    socketReader.session = null;
//                }
//            }
//            else if ("auth".equals(tag)) {
//                // User is trying to authenticate using SASL
//                if (authenticateClient(doc)) {
//                    // SASL authentication was successful so open a new stream and offer
//                    // resource binding and session establishment (to client sessions only)
//                    awaytingSasl = true;
//                }
//                else if (socketReader.connection.isClosed()) {
//                    socketReader.open = false;
//                    socketReader.session = null;
//                }
//            }
//            else if ("compress".equals(tag))
//            {
//                // Client is trying to initiate compression
//                if (compressClient(doc)) {
//                    // Compression was successful so open a new stream and offer
//                    // resource binding and session establishment (to client sessions only)
//                    awaitingForCompleteCompression = true;
//                }
//            }
//            else {
//                socketReader.process(doc);
//            }
//        }
//    }
}
