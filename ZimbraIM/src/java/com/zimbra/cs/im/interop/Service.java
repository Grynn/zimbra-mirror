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

/**
 * This class represents one type of IM service that we interoperate with. There
 * is one instance of this class for every service type defined in
 * {@link Interop.ServiceName}.
 */
class Service implements Component, RosterEventListener {

    final void debug(String format, Object... objects) {
        if (Log.isDebugEnabled()) {
            Log.debug("Interop:" + getName() + " - " + format, objects);
        }
    }
    final void info(String format, Object... objects) {
        if (Log.isInfoEnabled()) {
            Log.info("Interop:" + getName() + " - " + format, objects);
        }
    }
    final void warn(String format, Object... objects) {
        if (Log.isWarnEnabled()) {
            Log.warn("Interop:" + getName() + " - " + format, objects);
        }
    }
    final void error(String format, Object... objects) {
        if (Log.isErrorEnabled()) {
            Log.error("Interop:" + getName() + " - " + format, objects);
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactAdded(org.jivesoftware.wildfire.roster.Roster,
     *      org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactAdded(Roster roster, RosterItem item) {}
    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactDeleted(org.jivesoftware.wildfire.roster.Roster,
     *      org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactDeleted(Roster roster, RosterItem item) {}
    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#contactUpdated(org.jivesoftware.wildfire.roster.Roster,
     *      org.jivesoftware.wildfire.roster.RosterItem)
     */
    public void contactUpdated(Roster roster, RosterItem item) {}
    /*
     * (non-Javadoc)
     * 
     * @see org.jivesoftware.wildfire.roster.RosterEventListener#rosterLoaded(org.jivesoftware.wildfire.roster.Roster)
     */
    public void rosterLoaded(Roster roster) {}

    Service(String name, String description, SessionFactory fact) {
        mName = name;
        mDescription = description;
        mRosterManager = new RosterManager();
        mFact = fact;
    }

    SessionFactory mFact;

    public String getName() {
        return mName;
    }
    public String getDescription() {
        return mDescription;
    }
    public void initialize(JID jid, ComponentManager componentManager) {
        mJid = jid;
        mComponentManager = componentManager;
    }
    public void start() {}
    public void shutdown() {}

    JID getReplyAddress(JID userJID) {
        return new JID(null, getName() + "." + userJID.getDomain(), null);
    }

    List<String> getTransportDomains() {
        ArrayList<String> toRet = new ArrayList<String>();
        toRet.add(mJid.getDomain());
        return toRet;
    }

    void send(Packet p) throws ComponentException {
        mComponentManager.sendPacket(this, p);
    }

    public void processPacket(Packet packet) {
        try {
            info("processing packet: %s", packet.toXML());
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

    protected List<Packet> processIQ(IQ iq) {
        return null;
    }

    protected List<Packet> processPresence(Presence pres) {
        JID to = pres.getTo();
        JID from = pres.getFrom();

        if (to.getNode() == null) {
            Session s = getSession(from.toBareJID());
            if (s != null)
                return s.processPresence(pres);
        } else {
            info("Got directed presence packet: %s", pres);
        }

        return null;
    }
    protected List<Packet> processMessage(Message message) {
        JID to = message.getTo();
        JID from = message.getFrom();

        if (to.getNode() == null) {
            debug("Ignoring Message to transport: %s", message.toXML());
        } else {
            Session s = getSession(from.toBareJID());
            if (s != null) {
                s.processMessage(message);
            } else {
                info("Couldn't find session, ignoring message: %s", message.toXML());
            }
        }

        return null;
    }

    Session getSession(String bareJid) {
        synchronized (mSessions) {
            return mSessions.get(bareJid);
        }
    }

    void addRosterSubscription(JID userJid, JID remoteId, String friendlyName,
                String group) throws UserNotFoundException {

        ArrayList<String> groupsAL = new ArrayList<String>(1);
        groupsAL.add(group);
        addRosterSubscription(userJid, remoteId, friendlyName, groupsAL);
    }

    void addRosterSubscription(JID userJid, JID remoteId, String friendlyName,
                List<String> groups) throws UserNotFoundException {

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
                        || (existing.getNickname() != null && !existing.getNickname()
                                    .equals(friendlyName)) || (!groupsEqual)) {
                existing.setSubStatus(RosterItem.SUB_BOTH);
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
                RosterItem newItem = roster.createRosterItem(remoteId, friendlyName,
                            null, true, false);
                newItem.setSubStatus(RosterItem.SUB_BOTH);
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
    
    void removeRosterSubscription(JID userJid, JID remoteId) throws UserNotFoundException {
        Roster roster = mRosterManager.getRoster(userJid.toBareJID());
        try {
            roster.deleteRosterItem(remoteId, true);
        } catch (SharedGroupException e) {
            debug("Ignoring SharedGroupException from removeRosterSubscription(%s, %s)", userJid, remoteId);
        }
    }
    
    void removeAllSubscriptions(JID userJid, String domain) throws UserNotFoundException {
        Roster roster = mRosterManager.getRoster(userJid.toBareJID());
        
        Collection<RosterItem> items = roster.getRosterItems();
        for (RosterItem item : items) {
            if (domain.equals(item.getJid().getDomain())) {
                try {
                    roster.deleteRosterItem(item.getJid(), true);
                } catch (SharedGroupException e) {
                    debug("Ignoring SharedGroupException from removeAllSubscriptions(%s, %s)", userJid, item.getJid());
                }
            }
        }
    }

    void connectUser(JID jid, String name, String password, String transportName,
                String group) throws ComponentException, UserNotFoundException {
        synchronized (mSessions) {
            addRosterSubscription(jid, getReplyAddress(jid), transportName, group);
            Session s = mSessions.get(jid.toBareJID());
            if (s != null) {
                s.setUsername(name);
                s.setPassword(password);
            } else {
                mSessions.put(jid.toBareJID(), mFact.createSession(this, new JID(jid
                            .toBareJID()), name, password));
            }
        }
        Presence p = new Presence(Presence.Type.probe);
        p.setTo(jid);
        p.setFrom(getReplyAddress(jid));
        send(p);
    }

    void disconnectUser(JID jid) throws ComponentException, UserNotFoundException {
        Session s = mSessions.get(jid.toBareJID());
        if (s != null) {
            s.disconnect();
        }
        removeAllSubscriptions(jid, getReplyAddress(jid).getDomain());
    }

    protected RosterManager mRosterManager;
    protected String mName;
    protected String mDescription;
    protected Map<String /* users barejid */, Session> mSessions = new HashMap<String, Session>();
    protected ComponentManager mComponentManager;
    protected JID mJid;
}
