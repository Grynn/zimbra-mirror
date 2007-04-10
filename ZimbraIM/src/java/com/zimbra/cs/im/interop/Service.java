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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.SharedGroupException;
import org.jivesoftware.wildfire.roster.Roster;
import org.jivesoftware.wildfire.roster.RosterEventListener;
import org.jivesoftware.wildfire.roster.RosterItem;
import org.jivesoftware.wildfire.roster.RosterManager;
import org.jivesoftware.wildfire.user.UserAlreadyExistsException;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.zimbra.common.util.ClassLogger;
import com.zimbra.common.util.ZimbraLog;

/**
 * This class represents one type of IM service that we interoperate with. There
 * is one instance of this class for every service type defined in
 * {@link Interop.ServiceName}.
 */
final class Service extends ClassLogger implements Component, RosterEventListener {

    protected ComponentManager mComponentManager;
    private SessionFactory mFact;
    protected JID mJid;
    protected Interop.ServiceName mName;
    protected RosterManager mRosterManager;
    protected Map<String /* users barejid */, InteropSession> mSessions = new HashMap<String, InteropSession>();

    Service(Interop.ServiceName name, SessionFactory fact) {
        super(ZimbraLog.im);
        mName = name;
        mRosterManager = new RosterManager();
        mFact = fact;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#addingContact(org.jivesoftware.wildfire.roster.Roster,
     *      org.jivesoftware.wildfire.roster.RosterItem, boolean)
     */
    public boolean addingContact(Roster roster, RosterItem item, boolean persistent) {
        List<String> myDomains = getTransportDomains();
        if (myDomains.contains(item.getJid().getDomain())) {
            info("INTEROP.ADDINGCONTACT %s returning FALSE", item.getJid().toString());
            return false;
        } else {
            info("INTEROP.ADDINGCONTACT %s returning TRUE", item.getJid().toString());
            return true;
        }
    }

    void addOrUpdateRosterSubscription(JID userJid, JID remoteId, String friendlyName, List<String> groups, boolean twoWay)
                throws UserNotFoundException {
        
        twoWay = true;

        Roster roster = mRosterManager.getRoster(userJid.toBareJID());
        try {
            RosterItem existing = roster.getRosterItem(remoteId);

            // compare the list of groups
            boolean groupsEqual = true;
            if (groups.size() != existing.getGroups().size()) {
                groupsEqual = false;
            } else {
                for (String group : groups) {
                    if (!existing.getGroups().contains(group))
                        groupsEqual = false;
                }
            }

            // are they different? If so, update!
            if ((existing.getAskStatus() != RosterItem.ASK_NONE)
                || (existing.getNickname() == null && friendlyName != null)
                || (existing.getNickname() != null && !existing.getNickname().equals(friendlyName))
                || (!groupsEqual)) {
                existing.setSubStatus(twoWay ? RosterItem.SUB_BOTH : RosterItem.SUB_TO);
                existing.setAskStatus(RosterItem.ASK_NONE);
                existing.setNickname(friendlyName);
                try {
                    existing.setGroups(groups);
                } catch (Exception e) {
                    warn("Caught exception adding groups: %s", e);
                }
                roster.updateRosterItem(existing);
            }
        } catch (UserNotFoundException e) {
            try {
                RosterItem newItem = roster.createRosterItem(remoteId, friendlyName, null, true, false);
                newItem.setSubStatus(twoWay ? RosterItem.SUB_BOTH : RosterItem.SUB_TO);
                newItem.setAskStatus(RosterItem.ASK_NONE);
                try {
                    newItem.setGroups(groups);
                } catch (Exception ignoreme) {
                    warn("Caught exception adding groups: %s", e);
                }
                roster.updateRosterItem(newItem);
            } catch (SharedGroupException sge) {
                assert (false); // server bug (null group list above!
            } catch (UserAlreadyExistsException uae) {
                Log.error("UserAlreadyExistsException: %s", uae);
            }
        }
    }

    void addOrUpdateRosterSubscription(JID userJid, JID remoteId, String friendlyName, String group, boolean twoWay)
                throws UserNotFoundException {

        ArrayList<String> groupsAL = new ArrayList<String>(1);
        groupsAL.add(group);
        addOrUpdateRosterSubscription(userJid, remoteId, friendlyName, groupsAL, twoWay);
    }

    void connectUser(JID jid, String name, String password, String transportName, String group)
                throws ComponentException, UserNotFoundException {
        synchronized (mSessions) {
            // add the SERVICE user (two-way sub)
            addOrUpdateRosterSubscription(jid, getServiceJID(jid), transportName, group, true);
            InteropSession s = mSessions.get(jid.toBareJID());
            if (s != null) {
                s.setUsername(name);
                s.setPassword(password);
            } else {
                mSessions.put(jid.toBareJID(), mFact.createSession(this, new JID(jid.toBareJID()), name,
                            password));
            }
        }
        // send a probe to the user's jid -- if the user is online, then we'll
        // get a presence packet which will trigger a logon
        Presence p = new Presence(Presence.Type.probe);
        p.setTo(new JID(jid.toBareJID()));
        p.setFrom(getServiceJID(jid));
        send(p);
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactAdded(org.jivesoftware.wildfire.roster.Roster, org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactAdded(Roster roster, RosterItem item) {}

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactDeleted(org.jivesoftware.wildfire.roster.Roster, org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactDeleted(Roster roster, RosterItem item) {}

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactUpdated(org.jivesoftware.wildfire.roster.Roster, org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactUpdated(Roster roster, RosterItem item) {}

    /**
     * @param jid
     * @throws ComponentException
     * @throws UserNotFoundException
     */
    void disconnectUser(JID jid) throws ComponentException, UserNotFoundException {
        InteropSession s = mSessions.remove(jid.toBareJID());
        if (s != null) {
            s.shutdown();
        }
        removeAllSubscriptions(jid, getServiceJID(jid).getDomain());
    }

    /* (non-Javadoc)
     * @see org.xmpp.component.Component#getDescription()
     */
    public String getDescription() {
        return mName.getDescription();
    }

    /* (non-Javadoc)
     * @see com.zimbra.common.util.ClassLogger#getInstanceInfo()
     */
    @Override
    protected String getInstanceInfo() {
        return "Interop[" + this.getName() + "] - ";
    }

    /* @see org.xmpp.component.Component#getName() */
    public String getName() {
        return mName.name();
    }

    /**
     * @param userJID
     * @return
     */
    JID getServiceJID(JID userJID) {
        return new JID(null, getName() + "." + userJID.getDomain(), null);
    }

    /**
     * @param bareJid
     * @return
     */
    private InteropSession getSession(String bareJid) {
        synchronized (mSessions) {
            return mSessions.get(bareJid);
        }
    }

    List<String> getTransportDomains() {
        ArrayList<String> toRet = new ArrayList<String>();
        toRet.add(mJid.getDomain());
        return toRet;
    }

    public void initialize(JID jid, ComponentManager componentManager) {
        mJid = jid;
        mComponentManager = componentManager;
    }

    protected List<Packet> processIQ(IQ iq) {
        return null;
    }

    protected List<Packet> processMessage(Message message) {
        JID to = message.getTo();
        JID from = message.getFrom();

        if (to.getNode() == null) {
            debug("Ignoring Message to transport: %s", message.toXML());
        } else {
            InteropSession s = getSession(from.toBareJID());
            if (s != null) {
                return s.processMessage(message);
            } else {
                info("Couldn't find session, ignoring message: %s", message.toXML());
            }
        }
        return null;
    }

    public void processPacket(Packet packet) {
        try {
            debug("Service.processPacket: %s", packet.toXML());
            List<Packet> replies = null;
            if (packet instanceof IQ)
                replies = processIQ((IQ) packet);
            else if (packet instanceof Presence)
                replies = processPresence((Presence) packet);
            else if (packet instanceof Message)
                replies = processMessage((Message) packet);
            else {
                debug("ignoring unknown packet: %s", packet);
            }

            if (replies != null)
                for (Packet p : replies)
                    send(p);
        } catch (Exception e) {
            Log.error("Interop:" + getName() + " - exception in processPacket", e);
        }
    }

    protected List<Packet> processPresence(Presence pres) {
        JID from = pres.getFrom();
        
        InteropSession s = getSession(from.toBareJID());
        if (s != null) 
            return s.processPresence(pres);
        else {
            debug("Unknown session: sending unavailable reply");
            Presence p = new Presence(Presence.Type.unavailable);
            p.setFrom(pres.getTo());
            p.setTo(pres.getFrom());
            List<Packet> toRet = new ArrayList<Packet>(1);
            toRet.add(p);
            return toRet;
        }
    }

    /**
     * Refresh all presence for this user -- used when a new session has come
     * online and needs to probe all the remote users
     * 
     * @param jid
     */
    void refreshAllPresence(JID jid) {
        InteropSession s = getSession(jid.toBareJID());
        serviceOnline(jid);
        if (s != null)
            s.refreshAllPresence();
    }

    /**
     * Remove all subscriptions for this service from the specified user.  This is
     * done when the user logs out of the remote interop system.
     * 
     * @param userJid
     * @param domain
     * @throws UserNotFoundException
     */
    void removeAllSubscriptions(JID userJid, String domain) throws UserNotFoundException {
        Roster roster = mRosterManager.getRoster(userJid.toBareJID());

        Collection<RosterItem> items = roster.getRosterItems();
        for (RosterItem item : items) {
            if (domain.equals(item.getJid().getDomain())) {
                try {
                    roster.deleteRosterItem(item.getJid(), true);
                } catch (SharedGroupException e) {
                    debug("Ignoring SharedGroupException from removeAllSubscriptions(%s, %s)", userJid, item
                                .getJid());
                }
            }
        }
    }

    
    /**
     * Only have to update the presence for the *service* here
     * 
     * @param userJid
     */
    void serviceOnline(JID userJid) {
        Presence p = new Presence();
        p.setFrom(getServiceJID(userJid));
        p.setTo(userJid);
        try {
            send(p);
        } catch (ComponentException e) {
            error("ComponentException: %s", e);
        }
    }
    /**
     * Used when the transport loses connectivity: we update the presence state for ALL 
     * of our roster items 
     * 
     * @param userId
     * @param p
     */
    void serviceOffline(JID userJid) {
        try {
            Roster roster = mRosterManager.getRoster(userJid.toBareJID());
            String domain = getServiceJID(userJid).getDomain();
            
            Collection<RosterItem> items = roster.getRosterItems();
            for (RosterItem item : items) {
                if (domain.equals(item.getJid().getDomain())) {
                    Presence p = new Presence(Presence.Type.unavailable);
                    p.setTo(userJid);
                    p.setFrom(item.getJid());
                    try {
                        send(p);
                    } catch (ComponentException e) {
                        error("ComponentException: %s", e);
                    }
                }
            }
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    void removeRosterSubscription(JID userJid, JID remoteId) throws UserNotFoundException {
        Roster roster = mRosterManager.getRoster(userJid.toBareJID());
        try {
            roster.deleteRosterItem(remoteId, true);
        } catch (SharedGroupException e) {
            debug("Ignoring SharedGroupException from removeRosterSubscription(%s, %s)", userJid, remoteId);
        }
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#rosterLoaded(org.jivesoftware.wildfire.roster.Roster)
     */
    public void rosterLoaded(Roster roster) {}

    void send(Packet p) throws ComponentException {
        mComponentManager.sendPacket(this, p);
    }

    /* (non-Javadoc)
     * @see org.xmpp.component.Component#shutdown()
     */
    public void shutdown() {
        for (InteropSession s : mSessions.values()) {
            s.shutdown();
        }
        mSessions.clear();
    }

    /* (non-Javadoc)
     * @see org.xmpp.component.Component#start()
     */
    public void start() {}
}
