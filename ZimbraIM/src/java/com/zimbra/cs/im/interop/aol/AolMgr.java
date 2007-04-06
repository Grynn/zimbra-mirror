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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.snac.SnacRequest;
import net.kano.joscar.snac.SnacRequestListener;
import net.kano.joscar.snaccmd.icbm.SendImIcbm;

import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.ZimbraLog;

/**
 * 
 */
public class AolMgr extends ClassLogger {
    public AolMgr(AolEventListener listener) {
        super(ZimbraLog.im);
        mConnections = new ArrayList<AolConnection>();
        mListener = listener;
    }
    
    public String getInstanceInfo() {
        return ("AolMgr("+mListener.toString()+")");
    }
    
    public String toString() {
        return ("AolMgr("+mListener.toString()+")");
    }
    
    public synchronized void sendMessage(String username, String message) throws IOException {
        send(new SendImIcbm(username, message));
    }

    public synchronized void connect(String username, String password) throws IOException {
        assert (!isConnected());
        if (isConnected())
            throw new IllegalStateException("Already logged in");
        
        AolConnection login = new LoginConnection(this, LOGIN_HOSTNAME, LOGIN_PORT, username, password);
        login.start();
    }

    public synchronized boolean isConnected() {
        return mConnections.size() > 0;
    }

    public synchronized void disconnect() {
        // copy into 2nd list to avoid concurrent modifications issues
        List<AolConnection> toKill = new ArrayList<AolConnection>(mConnections.size());
        toKill.addAll(mConnections);
        for (AolConnection c : toKill) {
            c.stop();
        }
    }

    public synchronized void registerConnection(AolConnection conn) {
        debug("RegisterConnection: %s", conn);
        assert (!mConnections.contains(conn));
        if (!mConnections.contains(conn))
            mConnections.add(conn);
    }

    public synchronized void unregisterConnection(AolConnection conn) {
        debug("UnRegisterConnection: %s", conn);
//        assert (mConnections.contains(conn));
        if (!mConnections.contains(conn))
            warn("Closing connection %s but not in connection list", conn);
        mConnections.remove(conn);
    }

    AolEventListener getListener() {
        return mListener;
    }
    
    AolConnection findConnection(int family) {
        if (mConnections.size() == 0)
            return null;
        
        for (AolConnection c : mConnections) {
            if (Arrays.binarySearch(c.getSnacFamilies(), family) >= 0) {
                return c;
            }
        }
        return mConnections.get(0);
    }
    
    SnacRequest send(SnacCommand cmd) {
        return send(cmd, null);
    }
    
    SnacRequest send(SnacCommand cmd, SnacRequestListener listener) {
        SnacRequest request = new SnacRequest(cmd, listener);
        return send(request);
    }
    
    SnacRequest send(SnacRequest request) {
        int family = request.getCommand().getFamily();
        AolConnection c = findConnection(family);
        c.sendRequest(request);
        return request;
    }

    private AolEventListener mListener;
    private List<AolConnection> mConnections;

    private static final String LOGIN_HOSTNAME = "login.oscar.aol.com";
    private static final int LOGIN_PORT = 5190;
}
