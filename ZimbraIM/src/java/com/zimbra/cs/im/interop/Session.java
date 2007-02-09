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

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.ZimbraLog;

/**
 * Base class for a specific interop session, ie one particular user's login to
 * a remote IM service.
 */
abstract class Session extends ClassLogger {

    private String mPassword;
    private Service mService;
    private JID mUserJid;
    private String mUsername;
    private boolean mIsLoggedOn;

    protected Session(Service interop, JID userJid, String username, String password) {
        super(ZimbraLog.im);
        this.mService = interop;
        this.mUserJid = userJid;
        this.mUsername = username;
        this.mPassword = password;
    }
    
    final void addRosterSubscription(JID remoteId, String friendlyName, List<String> groups)
                throws UserNotFoundException {
        mService.addRosterSubscription(mUserJid, remoteId, friendlyName, groups);
    }

    final void addRosterSubscription(JID remoteId, String friendlyName, String group)
                throws UserNotFoundException {
        mService.addRosterSubscription(mUserJid, remoteId, friendlyName, group);
    }

    final String getDomain() {
        return mService.getReplyAddress(mUserJid).getDomain();
    }

    @Override
    protected String getInstanceInfo() {
        return "Session[service=" + this.mService.getName() + ",user=" + this.mUserJid
                    + "]";
    }

    final String getPassword() {
        return mPassword;
    }

    final String getPrintStr() {
        return this.toString();
    }

    final JID getUserJid() {
        return mUserJid;
    }

    final String getUsername() {
        return mUsername;
    }

    final boolean isLoggedOn() {
        return mIsLoggedOn;
    }

    /**
     * Disconnect the user from the remote IM service
     */
    abstract void subclassLogOff();
    
    /**
     * Connect the user to the remote transport service
     */
    abstract boolean subclassLogOn();
    
    /**
     * Forward the specified message to the remote IM service
     * @param m
     *      XMPP Message
     * @return
     */
    abstract List<Packet> processMessage(Message m);

    
    /**
     * Update our user's presence on the remote IM service
     * 
     * @param pres
     */
    abstract void setPresence(Presence pres);
    
    /**
     * Return the presence state of the specified user on the
     * remote IM service
     * 
     * @param pres
     * @return
     */
    abstract void handleProbe(Presence pres) throws UserNotFoundException;
    
    
    /**
     * Refresh the presence state of ALL users on our buddy list on
     * the remote IM service -- this is equivalent to sending
     * a probe for every entry on my remote buddy list. 
     */
    abstract void refreshAllPresence();
    
    /**
     * Log the session on or off.  Subclasses must implement the subclassLogOn function
     * 
     * @return TRUE if we are logged on (or logon in process
     */
    final boolean logOn() {
        if (!mIsLoggedOn) {
            if (subclassLogOn()) {
                mIsLoggedOn = true;
            }
        }
        return mIsLoggedOn;
    }
    
    /**
     * Subclasses must implement the subclassLogOff function
     */
    final void logOff() {
        if (mIsLoggedOn) {
            mIsLoggedOn = false;
            subclassLogOff();
        }
    }
    
    final List<Packet> processPresence(Presence pres) {
        if (pres.getType() == null) {
            if (logOn()) {
                debug("Logged on");
            } else {
                info("FAILED to log on");
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
// Don't do this right now: need to properly handle multiple-resources
// before we can logoff like this
//                    logOff();
                    break;
                case probe:
                    if (pres.getTo().getNode() != null) {
                        try {
                            handleProbe(pres);
                        } catch (UserNotFoundException e) {
                            Presence error = new Presence(Presence.Type.error);
                            error.setTo(pres.getFrom());
                            error.setFrom(pres.getTo());
                            error.setError(PacketError.Condition.recipient_unavailable);
                            List<Packet> toRet = new ArrayList<Packet>();
                            toRet.add(error);
                            return toRet;
                        }
                    }
                    break;
                default:
            }
        }
        return null;
    }

    final void sendMessage(JID remoteId, Message message) {
        message.setTo(mUserJid);
        message.setFrom(remoteId);
        try {
            mService.send(message);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }

    final void setPassword(String password) {
        this.mPassword = password;
    }

    final void setUsername(String username) {
        this.mUsername = username;
    }

    final void updatePresence(JID remoteId, Presence pres) {
        pres.setTo(mUserJid);
        pres.setFrom(remoteId);
        try {
            mService.send(pres);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }
}