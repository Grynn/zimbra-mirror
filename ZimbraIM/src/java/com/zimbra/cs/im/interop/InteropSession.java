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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;

/**
 * Base class for a specific interop session, ie one particular user's login to
 * a remote IM service.
 */
abstract class InteropSession extends ClassLogger {

    private class ConnectTask implements Callable<Void> {
        public Void call() {
            info("Starting connect attempt");
            connect();
            return null;
        }
    }
    protected static enum ConnectCompletionStatus {
        AUTH_FAILURE, COULDNT_CONNECT, OTHER_PERMANENT_FAILURE, OTHER_TEMPORARY_FAILURE, SUCCESS;
    }
    
    protected static enum State {
        BAD_AUTH, // start
        INTENTIONALLY_OFFLINE, // because our presence map says so
        ONLINE, 
        SHUTDOWN,
        START, // auth is bad (or other unrecoverable error)
        TRYING_TO_CONNECT, // user is unregistered, we are shutdown, DON'T COME BACK UP
        ;
    }
    
    private static final long INITIAL_RETRY_INTERVAL = 30 * Constants.MILLIS_PER_SECOND;
    private static final long MAXIMUM_RETRY_INTERVAL = Constants.MILLIS_PER_HOUR;
    
    protected InteropSession(Service interop, JID userJid, String username, String password) {
        super(ZimbraLog.im);
        this.mService = interop;
        this.mUserJid = userJid;
        this.mUsername = username;
        this.mPassword = password;
    }

    public String toString() {
        return "Session[service=" + this.mService.getName() + ",user=" + this.mUserJid
        + "]";
    }

    /**
     * Valid state transitions:
     * 
     * START --->  (IO or TRY or SHUT)
     * IO    --->  (TRY or SHUT)
     * TRY   --->  (IO or ON or BAD or SHUT)
     * ON    --->  (IO or SHUT or TRY)
     * BAD   --->  (SHUT)
     * SHUT  --->  none
     * 
     * DISC  ---> Do nothing, unless we're ONLINE...in that case go to TRY
     */
    private synchronized void changeState(State newState) {
        if (mState == newState)
            return;
        
        switch (mState) {
            case START:
                switch (newState) {
                    case INTENTIONALLY_OFFLINE:
                        break;
                    case TRYING_TO_CONNECT:
                        mState = newState;
                        startTryingToConnect();
                        break;
                    case SHUTDOWN: 
                        break;
                    default: debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case INTENTIONALLY_OFFLINE:
                switch (newState) {
                    case TRYING_TO_CONNECT:
                        mState = newState;
                        startTryingToConnect();
                        break;
                    case SHUTDOWN: 
                        break;
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case TRYING_TO_CONNECT:
                switch (newState) {
                    case ONLINE:
                        mState = State.ONLINE; // set BEFORE pushing presence!
                        mService.serviceOnline(getUserJid());
                        setPresence(getEffectivePresence());
                        break;
                    case INTENTIONALLY_OFFLINE: // fall-through
                    case BAD_AUTH: // fall-through
                    case SHUTDOWN:
                        mState = newState;
                        stopTryingToConnect();
                        disconnect();
                        break;
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case ONLINE:
                switch (newState) {
                    case INTENTIONALLY_OFFLINE: // fall-through
                    case SHUTDOWN:
                        mState = newState;
                        disconnect();
                        break;
                    case TRYING_TO_CONNECT:
                        mState = newState;
                        startTryingToConnect();
                        break;
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case BAD_AUTH:
                switch (newState) {
                    case SHUTDOWN: 
                        break;
                    case ONLINE:
                        throw new IllegalStateException("Transition from BAD_AUTH to ONLINE? How?");
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case SHUTDOWN:
                debug("Ignored invalid state transition request: "+mState+" to "+newState); return;                
        }

        // update the state, if it hasn't been already
        mState = newState;
    }

    /**
     * Start the trying-to-connect task, if there isn't one
     */
    private synchronized final void startTryingToConnect() {
        if (mConnectTask == null) {
            mRetryInterval = (INITIAL_RETRY_INTERVAL>>1); // since we double it before we use it
            mConnectTask = new ConnectTask(); 
            Interop.sTaskScheduler.schedule(mConnectTask, mConnectTask, false, 100, 100);
        }
    }
    
    /**
     * Stop the trying-to-connect task, if there is one
     */
    private synchronized final void stopTryingToConnect() {
        if (mConnectTask != null) {
            Interop.sTaskScheduler.cancel(mConnectTask, false);
            mConnectTask = null;
        }
    }

    /**
     * Connect to the remote service.  Subclass must call notifyConnectCompleted() 
     * when connect completes (success or failure)
     */
    protected abstract void connect();

    /**
     * Disconnect us from the remote service.  Subclass must call notifyDisconnected()
     * once the disconnection actually happens
     */
    protected abstract void disconnect();

    protected Presence getEffectivePresence() {
        if (mPresenceMap.size() == 0) {
            return new Presence(Presence.Type.unavailable);
        } else {
            // return the first one for now TODO FIXME
            return mPresenceMap.values().iterator().next();
        }
    }

    @Override
    protected String getInstanceInfo() {
        return toString();
    }

    /**
     * Return the presence state of the specified user on the
     * remote IM service
     * 
     * @param pres
     * @return
     */
    protected abstract void handleProbe(Presence pres) throws UserNotFoundException;

    
    /**
     * @return TRUE if the user is locally online (ie if we should log them into the remote
     * service)
     */
    protected boolean localPresenceAvailable() {
        return !mPresenceMap.isEmpty();
    }
    
    protected synchronized final void notifyConnectCompleted(ConnectCompletionStatus status) {
        debug("ConnectCompleted: "+status);
        
        if (mState != State.TRYING_TO_CONNECT) {
            // race condition: changed states underneath us!
            info("ConnectCompleted(%s) but current state is %s.  Disconnecting.", status, mState);
            disconnect();
            return;
        }
        
        assert(mConnectTask != null); 
        switch (status) {
            case SUCCESS:
                changeState(State.ONLINE);
                mConnectTask = null;
                break;
            case AUTH_FAILURE:
            case OTHER_PERMANENT_FAILURE:
                changeState(State.BAD_AUTH);
                mConnectTask = null;
                break;
            case COULDNT_CONNECT:
            case OTHER_TEMPORARY_FAILURE:
                mRetryInterval = Math.min(mRetryInterval*2, MAXIMUM_RETRY_INTERVAL);
                info("Scheduling reconnect attempt for %d seconds", (mRetryInterval/1000)); 
                Interop.sTaskScheduler.schedule(mConnectTask, mConnectTask, false, mRetryInterval, mRetryInterval);
                break;
        }
    }
    
    protected final synchronized void notifyDisconnected() {
        mService.serviceOffline(getUserJid());
        
        // reconnect if they were ONLINE
        if (mState == State.ONLINE)
            changeState(State.TRYING_TO_CONNECT);
    }

    /**
     * Forward the specified message to the remote IM service
     * @param m
     *      XMPP Message
     * @return
     */
    protected abstract List<Packet> processMessage(Message m);
    
    /**
     * Refresh the presence state of ALL users on our buddy list on
     * the remote IM service -- this is equivalent to sending
     * a probe for every entry on my remote buddy list. 
     */
    protected abstract void refreshAllPresence();
    
    /**
     * Update our user's presence on the remote IM service
     * 
     * @param pres
     */
    protected abstract void setPresence(Presence pres);
    
    final synchronized void addOrUpdateRosterSubscription(JID remoteId, String friendlyName, List<String> groups)
                throws UserNotFoundException {
        mService.addOrUpdateRosterSubscription(mUserJid, remoteId, friendlyName, groups, false);
    }
    
    final synchronized void addOrUpdateRosterSubscription(JID remoteId, String friendlyName, String group)
                throws UserNotFoundException {
        mService.addOrUpdateRosterSubscription(mUserJid, remoteId, friendlyName, group, false);
    }
    final synchronized String getDomain() {
        return mService.getServiceJID(mUserJid).getDomain();
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
    
    /**
     * @param pres
     * @return
     */
    final List<Packet> processPresence(Presence pres) {
        mPresenceMap.remove(""); // remove existing blank resource entry
        String resource = pres.getFrom().getResource();
        if (StringUtil.isNullOrEmpty(resource)) {
            debug("Presence from : "+pres.getFrom()+" HAS NO RESOURCE!");
            resource = ""; 
        }
        
        if (pres.getType() == null) {
            mPresenceMap.put(resource, pres);
            if (mState == State.ONLINE) {
                setPresence(getEffectivePresence());                
            } else {
                changeState(State.TRYING_TO_CONNECT);
            }
        } else {
            switch (pres.getType()) {
                case error:
                    debug("ignoring presence error: %s", pres);
                    return null;
                case unavailable:
                    mPresenceMap.remove(resource);
                    if (mPresenceMap.isEmpty())
                        changeState(State.INTENTIONALLY_OFFLINE);
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
    
    /**
     * Disconnect this user completely, do not reconnect: we're either shutting
     * down the system, or we're unregistering this user
     */
    final void shutdown() {
        changeState(State.SHUTDOWN);
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
    
    private ConnectTask mConnectTask = null;
    private String mPassword;
    private HashMap<String /*resource*/, Presence> mPresenceMap = new HashMap<String, Presence>();
    private long mRetryInterval;
    private Service mService;
    private State mState = State.START;
    private JID mUserJid;
    private String mUsername;
}