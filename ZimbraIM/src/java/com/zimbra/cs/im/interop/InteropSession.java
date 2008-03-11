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
package com.zimbra.cs.im.interop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.dom4j.Element;
import org.jivesoftware.wildfire.roster.RosterItem;
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
public abstract class InteropSession extends ClassLogger {

    private class ConnectTask implements Callable<Void> {
        public Void call() {
            info("Starting connect attempt");
            clearNextConnectTime();
            connect();
            return null;
        }
    }
    
    public static enum ConnectCompletionStatus {
        AUTH_FAILURE, 
        COULDNT_CONNECT,
        DISABLED, // interrupt connection, go to DISABLED state
        OTHER_PERMANENT_FAILURE, 
        OTHER_TEMPORARY_FAILURE, 
        SUCCESS;
    }
    
    public static enum State {
        BAD_AUTH, 
        INTENTIONALLY_OFFLINE, // because our presence map says so
        DISABLED, // you connected from another location, or manually disabled service
        ONLINE, 
        SHUTDOWN,
        START, 
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
    
    final synchronized void clearNextConnectTime() {
        mNextConnectTime = 0;
    }

    public String toString() {
        return "InteropSession[service=" + this.mService.getName() + ",user=" + this.mUserJid
        + "]";
    }

    /**
     * Valid state transitions:
     * 
     * START    --> (IO or TRY or SHUT or DISABLED)
     * IO       --> (TRY or SHUT or DISABLED)
     * TRY      --> (IO or ON or BAD or SHUT or DISABLED)
     * ON       --> (IO or SHUT or TRY or DISABLED)
     * BAD      --> (SHUT or DISABLED)
     * DISABLED --> (TRY or SHUT)
     * SHUT     --> none
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
                    case DISABLED:
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
                    case DISABLED:
                    case SHUTDOWN:
                        break;
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case DISABLED:
                switch (newState) {
                    case TRYING_TO_CONNECT:
                        mState = newState;
                        startTryingToConnect();
                        break;
                    case SHUTDOWN:
                        break;
                    default:debug("DISABLED: Ignored state transition request to: "+mState+" to "+newState); return;
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
                    case DISABLED: // fall-through
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
                    case DISABLED: // fall-through
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
                    case ONLINE:
                        throw new IllegalStateException("Transition from BAD_AUTH to ONLINE? How?");
                    case DISABLED:
                    case SHUTDOWN: 
                        break;
                    default:debug("Ignored invalid state transition request: "+mState+" to "+newState); return;
                }
                break;
            case SHUTDOWN:
                debug("Ignored invalid state transition request: "+mState+" to "+newState); return;                
        }

        // update the state, if it hasn't been already
        mState = newState;

        
        if (mState != State.START) {
            // Send a message from the Interop buddy to the user: the Web Client will
            // interpret the message as a system notification, XMPP clients will display it
            // as a plain message right now.
            Message m = new Message();
            m.setType(Message.Type.chat);
            Element x = m.addChildElement("x", "zimbra:interop");
            Element state = x.addElement("state");
            
            switch (mState) {
                case BAD_AUTH:
                    state.addAttribute("value", "bad_auth");
                    m.setBody("Connection FAILED: Incorrect username or password");
                    break;
                case INTENTIONALLY_OFFLINE:
                    state.addAttribute("value", "intentionally_offline");
                    m.setBody("You have been disconnected from the gateway as you are currently OFFLINE");
                    break;
                case DISABLED:
                    state.addAttribute("value", "disabled");
                    m.setBody("The "+mService.getName()+" service has been disabled");
                    break;
                case ONLINE:
                    state.addAttribute("value", "online");
                    m.setBody("You have successfully connected to the interop service");
                    break;
                case SHUTDOWN:
                    state.addAttribute("value", "shutdown");
                    m.setBody("You have been disconnected because the gateway is shutting down.");
                    break;
                case START:
                    assert(false); // notreached
                    break;
                case TRYING_TO_CONNECT:
                    state.addAttribute("value", "trying_to_connect");
                    long timeUntilRetry = System.currentTimeMillis() - mNextConnectTime;
                    if (timeUntilRetry > 0) {
                        state.addAttribute("delay", Long.toString(timeUntilRetry));
                        m.setBody("Next gateway connection attempt in "+(timeUntilRetry/1000)+" seconds");
                    } else {
                        m.setBody("Attempting to connect to the interop service");
                    }
                    break;
                default:
                    x.addElement("unknown");
                assert false;
                debug("Unknown state "+mState+" at end of changeState"); 
            }
            
            if (m != null)
                send(mService.getServiceJID(mUserJid), m);
        }
    }

    /**
     * Start the trying-to-connect task, if there isn't one
     */
    private synchronized final void startTryingToConnect() {
        if (mConnectTask == null) {
            mRetryInterval = (INITIAL_RETRY_INTERVAL>>1); // since we double it before we use it
            mConnectTask = new ConnectTask();
            mNextConnectTime = System.currentTimeMillis()+500;
            Interop.sTaskScheduler.schedule(mConnectTask, mConnectTask, false, 500, 500);
        }
    }
    
    /**
     * Stop the trying-to-connect task, if there is one
     */
    private synchronized final void stopTryingToConnect() {
        if (mConnectTask != null) {
            mNextConnectTime = 0;
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
            case DISABLED:
                notifyOtherLocationDisconnect();
                mConnectTask = null;
            case AUTH_FAILURE:
            case OTHER_PERMANENT_FAILURE:
                changeState(State.BAD_AUTH);
                mConnectTask = null;
                break;
            case COULDNT_CONNECT:
            case OTHER_TEMPORARY_FAILURE:
                mRetryInterval = Math.min(mRetryInterval*2, MAXIMUM_RETRY_INTERVAL);
                info("Scheduling reconnect attempt for %d seconds", (mRetryInterval/1000));
                mNextConnectTime = System.currentTimeMillis()+mRetryInterval;
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
     * We received a "you have connected from another location" message
     * and are about to be disconnected.
     * 
     * Transports which allow multiple logons from the same user MUST NOT
     * call this api.
     */
    protected final synchronized void notifyOtherLocationDisconnect() {
        Message m = new Message();
        m.setType(Message.Type.chat);
        Element x = m.addChildElement("x", "zimbra:interop");
        x.addElement("username").setText(getUsername());
        Element otherLocation = x.addElement("otherLocation");
        m.setBody("Your account "+this.getUsername()+" has logged onto the service from another location.");
        send(mService.getServiceJID(mUserJid), m);
        changeState(State.DISABLED);
    }

    /**
     * Forward the specified message to the remote IM service
     * @param m
     *      XMPP Message
     * @returnp
     */
    protected abstract List<Packet> sendMessage(Message m);
    
    /**
     * Update the subscription on the remote IM service
     * 
     * @param remoteJID 
     * @param groups  List of groups the buddy should be in 
     * @return
     */
    protected abstract void updateExternalSubscription(JID remoteJID, List<String> groups);
    
    /**
     * Remove the subscription from the remote IM service 
     * 
     * @param remoteJID
     */
    protected abstract void removeExternalSubscription(JID remoteJID);
    
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
    
    protected final synchronized void addOrUpdateRosterSubscription(JID remoteId, String friendlyName, List<String> groups,
        RosterItem.SubType subType) throws UserNotFoundException {
        mService.addOrUpdateRosterSubscription(mUserJid, remoteId, friendlyName, groups, subType,
            RosterItem.ASK_NONE, RosterItem.RECV_NONE);
    }
    
    protected final synchronized void addOrUpdateRosterSubscription(JID remoteId, String friendlyName, String group,
        RosterItem.SubType subType)
                throws UserNotFoundException {
        mService.addOrUpdateRosterSubscription(mUserJid, remoteId, friendlyName, group, 
            subType, RosterItem.ASK_NONE, RosterItem.RECV_NONE);
    }
    
    protected final synchronized void removeRosterSubscription(JID remoteId) {
        try {
            mService.removeRosterSubscription(mUserJid, remoteId);
        } catch (UserNotFoundException e) {}
    }
    
    protected final synchronized String getDomain() {
        return mService.getServiceJID(mUserJid).getDomain();
    }

    protected final String getPassword() {
        return mPassword;
    }
    
    final String getPrintStr() {
        return this.toString();
    }

    protected final JID getUserJid() {
        return mUserJid;
    }
    protected final String getUsername() {
        return mUsername;
    }
    
    protected final List<Packet> processMessage(Message msg) {
        sendMessage(msg);
        return null;
    }
    
    /**
     * @param pres
     * @return
     */
    protected final List<Packet> processPresence(Presence pres) {
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
                    if (pres.getTo().getNode() != null) {
                        debug("Ignoring directed presence to interop user: %s", pres);
                    } else {
                        // targeted at the service
                        mPresenceMap.remove(resource);
                        if (mPresenceMap.isEmpty())
                            changeState(State.INTENTIONALLY_OFFLINE);
                    }
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
                    } else {
                        // probe is to the SERVICE.  Return our state
                        List<Packet> toRet = new ArrayList<Packet>(1);
                        Presence p = new Presence();
                        toRet.add(p);
                        p.setFrom(pres.getTo());
                        p.setTo(pres.getFrom());
                        if (mState != State.ONLINE)
                            p.setType(Presence.Type.unavailable);
                        return toRet;
                    }
                    break;
                case unsubscribed:
                    debug("Ignoring UNSUBSCRIBED presence block for now");
                    break;
                case unsubscribe:
                    //updateExternalSubscription(false, pres.getTo(), null);
                    break;
                case subscribed:
                    debug("Ignoring SUBSCRIBED presence block for now");
                    break;
                case subscribe: 
                    //updateExternalSubscription(true, pres.getTo(), mService.getName());
                    break;
                default:
            }
        }
        return null;
    }
    
    protected final void send(JID remoteId, Packet message) {
        message.setTo(mUserJid);
        message.setFrom(remoteId);
        try {
            mService.send(message);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }
    
//    protected final void setPassword(String password) {
//        this.mPassword = password;
//    }
    
    protected final void setUsername(String username) {
        this.mUsername = username;
    }
    
    /**
     * Disconnect this user completely, do not reconnect: we're either shutting
     * down the system, or we're unregistering this user
     */
    protected final void shutdown() {
        changeState(State.SHUTDOWN);
    }
    
    
    /**
     * Upcall from the session to tell the service that a remote
     * user's presence has changed
     */
    protected final void updatePresence(JID remoteId, Presence pres) {
        pres.setTo(mUserJid);
        pres.setFrom(remoteId);
        try {
            mService.send(pres);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }
    
    private ConnectTask mConnectTask = null;
    private long mNextConnectTime = 0;
    private String mPassword;
    private HashMap<String /*resource*/, Presence> mPresenceMap = new HashMap<String, Presence>();
    private long mRetryInterval;
    private Service mService;
    private State mState = State.START;
    private JID mUserJid;
    private String mUsername;

    protected final synchronized State getState() {
        return mState;
    }
    
    final synchronized long getNextConnectTime() {
        return mNextConnectTime;
    }
}