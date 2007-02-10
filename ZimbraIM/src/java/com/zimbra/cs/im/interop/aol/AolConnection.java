/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 ("License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
 * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.aol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;

import net.kano.joscar.flap.AsynchronousFlapProcessor;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.flap.FlapPacketListener;
import net.kano.joscar.flap.FlapProcessor;
import net.kano.joscar.flapcmd.DefaultFlapCmdFactory;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.net.ConnProcessorExceptionEvent;
import net.kano.joscar.net.ConnProcessorExceptionHandler;
import net.kano.joscar.snac.ClientSnacProcessor;
import net.kano.joscar.snac.FamilyVersionPreprocessor;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snac.SnacPacketListener;
import net.kano.joscar.snac.SnacRequest;
import net.kano.joscar.snac.SnacRequestListener;
import net.kano.joscar.snac.SnacRequestSentEvent;
import net.kano.joscar.snac.SnacRequestTimeoutEvent;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.snaccmd.DefaultClientFactoryList;
import net.kano.joscar.snaccmd.auth.ClientVersionInfo;
import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.ZimbraLog;

public abstract class AolConnection extends ClassLogger implements SnacPacketListener, SnacRequestListener,
    FlapPacketListener, ConnProcessorExceptionHandler {

    protected static final ClientVersionInfo PROTOCOL_VERSION =
    //      new ClientVersionInfo("AOL Instant Messenger, version 5.1.3036/WIN32", 5, 1, 0, 3036, 239);
        new ClientVersionInfo("AOL Instant Messenger, version 5.5.3415/WIN32", -1, 5, 5, 0, 3415, 239);

    AolConnection(AolMgr mgr, String host, int port) throws UnknownHostException {
        super(ZimbraLog.im);
        mAddr = InetAddress.getByName(host);
        mPort = port;
        mMgr = mgr;
    }

    protected String sprintf(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

    protected void exceptionReceived(String desc, Throwable t, boolean fatal) {
        getEventListener().exception(desc, t, fatal);
    }
    
    public void handleException(ConnProcessorExceptionEvent event) {
        exceptionReceived("Type="+event.getType()+" Reason="+event.getReason(), 
            event.getException(), false);
    }
    

    /* (non-Javadoc)
     * @see com.zimbra.common.util.ClassLogger#getInstanceInfo()
     */
    @Override
    public String getInstanceInfo() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "AolConnection(" + mAddr + ":" + mPort + " " + mMgr.toString()+")";
    }

    /* (non-Javadoc)
     * @see net.kano.joscar.snac.OutgoingSnacRequestListener#handleSent(net.kano.joscar.snac.SnacRequestSentEvent)
     */
    public void handleSent(SnacRequestSentEvent e) {
//        info("SNAC Request Sent: %s", e);
    }

    /* (non-Javadoc)
     * @see net.kano.joscar.snac.SnacResponseListener#handleResponse(net.kano.joscar.snac.SnacResponseEvent)
     */
    public void handleResponse(SnacResponseEvent e) {
//        info("Got SNAC response: %s", e);
    }

    /* (non-Javadoc)
     * @see net.kano.joscar.snac.OutgoingSnacRequestListener#handleTimeout(net.kano.joscar.snac.SnacRequestTimeoutEvent)
     */
    public void handleTimeout(SnacRequestTimeoutEvent event) {
//        info("SNAC request timeout: %s", event);
    }

    /* (non-Javadoc)
     * @see net.kano.joscar.flap.FlapPacketListener#handleFlapPacket(net.kano.joscar.flap.FlapPacketEvent)
     */
    public void handleFlapPacket(FlapPacketEvent e) {
//        info("Got FLAP packet: %s", e);
    }

    /* (non-Javadoc)
     * @see net.kano.joscar.snac.SnacPacketListener#handleSnacPacket(net.kano.joscar.snac.SnacPacketEvent)
     */
    public void handleSnacPacket(SnacPacketEvent e) {
//        info("Got SNAC packet: %s", e);
    }

    /**
     * Overload this if you want, called after the socket has been connected
     * 
     * @throws IOException
     */
    protected void subclassRun() throws IOException {}

    /**
     * Optional hook.  Called before the socket is closed.
     */
    protected void subclassStop() {}

    /**
     * Call this to start the connection.  The handleXXXX functions will be called when packets
     * happen.  Call stop() to stop the connection
     * 
     * @throws IOException
     */
    final void start() throws IOException {
        debug("Connecting to: %s:%d", mAddr, mPort);
        mSocket = new Socket(mAddr, mPort);
        mFlapProcessor = new AsynchronousFlapProcessor(mSocket);
        mSnacProcessor = new ClientSnacProcessor(mFlapProcessor);

        mFlapProcessor.addExceptionHandler(this);
        mFlapProcessor.setFlapCmdFactory(new DefaultFlapCmdFactory());
        mSnacProcessor.addPreprocessor(new FamilyVersionPreprocessor());
        mSnacProcessor.getCmdFactoryMgr().setDefaultFactoryList(new DefaultClientFactoryList());

        // create a FLAP packet listener to initialize the FLAP portion of the
        // connection
        mFlapProcessor.addPacketListener(this);

        // create a SNAC packet listener to listen for any other packets (note
        // that on the login connection, this shouldn't actually get any
        // packets, since they're all SNAC responses; this is here to show you
        // how to do it)
        mSnacProcessor.addPacketListener(this);

        // FLAP packets can't be read if there's nothing to read them, so we
        // create a FLAP reading loop (which in turn reads SNAC's, and so on)
        mSocketReader = new Thread("AOL Socket Reader") {
            @Override
            public void run() {
                try {
                    mFlapProcessor.runFlapLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mSocketReader.start();
        mMgr.registerConnection(this);
        debug("Connection complete!");
        subclassRun();
    }

    /**
     * Disconnect this connection.  
     */
    final void stop() {
        debug("Closing!");
        subclassStop();
        try {
            mSocket.close();
        } catch (IOException ex) {
            debug("IOException during close (received Auth Response)");
        }
        mMgr.unregisterConnection(this);
        mSocketReader = null;
    }

    /**
     * Send a request out on this connection.  Responses will come to this object's 
     * handleResponse() API  
     * 
     * @param cmd
     * @return
     */
    SnacRequest request(SnacCommand cmd) {
        return request(cmd, null);
    }

    /**
     * Send a request on this connection specifying a custom response listener
     * 
     * @param cmd
     * @param listener
     * @return
     */
    SnacRequest request(SnacCommand cmd, SnacRequestListener listener) {
        SnacRequest req = new SnacRequest(cmd, listener);
        sendRequest(req);
        return req;
    }

    void sendRequest(SnacRequest req) {
        if (!req.hasListeners())
            req.addListener(this);
        mSnacProcessor.sendSnac(req);
    }

    protected FlapProcessor getFlapProcessor() {
        return mFlapProcessor;
    }

    protected ClientSnacProcessor getSnacProcessor() {
        return mSnacProcessor;
    }

    protected AolEventListener getEventListener() {
        return mMgr.getListener();
    }

    protected AolMgr getManager() {
        return mMgr;
    }
    
    int[] getSnacFamilies() {
        return new int[0];
    }
    
    private InetAddress mAddr;
    private int mPort;
    private FlapProcessor mFlapProcessor;
    private ClientSnacProcessor mSnacProcessor;
    private Socket mSocket;
    private Thread mSocketReader;
    private AolMgr mMgr;
}
