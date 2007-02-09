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
package com.zimbra.cs.im.interop;

import java.util.List;

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.ZimbraLog;

/**
 * Base class for a specific interop session, ie one particular user's login to
 * a remote IM service.
 */
abstract class Session extends ClassLogger {

    private boolean mIsLoggedOn;
    private String mPassword;
    private Service mService;
    private JID mUserJid;
    private String mUsername;

    Session(Service interop, JID userJid, String username, String password) {
        super(ZimbraLog.im);
        this.mService = interop;
        this.mUserJid = userJid;
        this.mUsername = username;
        this.mPassword = password;
    }
    
    void addRosterSubscription(JID remoteId, String friendlyName, List<String> groups)
                throws UserNotFoundException {
        mService.addRosterSubscription(mUserJid, remoteId, friendlyName, groups);
    }

    void addRosterSubscription(JID remoteId, String friendlyName, String group)
                throws UserNotFoundException {
        mService.addRosterSubscription(mUserJid, remoteId, friendlyName, group);
    }

    String getDomain() {
        return mService.getReplyAddress(mUserJid).getDomain();
    }

    @Override
    protected String getInstanceInfo() {
        return "Session[service=" + this.mService.getName() + ",user=" + this.mUserJid
                    + "]";
    }

    String getPassword() {
        return mPassword;
    }

    String getPrintStr() {
        return this.toString();
    }

    JID getUserJid() {
        return mUserJid;
    }

    String getUsername() {
        return mUsername;
    }

    boolean isLoggedOn() {
        return mIsLoggedOn;
    }

    void loginFailed(String s) { }
    
    void logOff() {
        mIsLoggedOn = false;
    }
    abstract boolean logOn();
    abstract List<Packet> processMessage(Message m);
    abstract void setPresence(Presence pres);
    abstract List<Packet> handleProbe(Presence pres);
    abstract void refreshPresence();
    
    List<Packet> processPresence(Presence pres) {
        if (pres.getType() == null) {
            if (!mIsLoggedOn) {
                if (logOn()) {
                    mIsLoggedOn = true;
                    debug("Logged on");
                } else {
                    info("FAILED to log on");
                }
            }
            if (mIsLoggedOn) {
                setPresence(pres);
            }
        } else {
            switch (pres.getType()) {
                case error:
                    debug("ignoring presence error: %s", pres);
                    return null;
                case unavailable:
//                    logOff();
                    break;
                case probe:
                    handleProbe(pres);
                    break;
                default:
            }
        }
        return null;
    }

    void sendMessage(JID remoteId, Message message) {
        message.setTo(mUserJid);
        message.setFrom(remoteId);
        try {
            mService.send(message);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }

    void setPassword(String password) {
        this.mPassword = password;
    }

    void setUsername(String username) {
        this.mUsername = username;
    }

    void updatePresence(JID remoteId, Presence pres) {
        pres.setTo(mUserJid);
        pres.setFrom(remoteId);
        try {
            mService.send(pres);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }
}