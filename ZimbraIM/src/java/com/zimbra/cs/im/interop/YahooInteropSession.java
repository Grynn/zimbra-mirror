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
import java.util.HashSet;
import java.util.List;

import org.jivesoftware.wildfire.roster.RosterItem;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.zimbra.cs.im.interop.yahoo.JYMsgAuthProvider;
import com.zimbra.cs.im.interop.yahoo.YMSGStatus;
import com.zimbra.cs.im.interop.yahoo.Yahoo;
import com.zimbra.cs.im.interop.yahoo.YahooBuddy;
import com.zimbra.cs.im.interop.yahoo.YahooError;
import com.zimbra.cs.im.interop.yahoo.YahooEventListener;
import com.zimbra.cs.im.interop.yahoo.YahooGroup;
import com.zimbra.cs.im.interop.yahoo.YahooMessage;
import com.zimbra.cs.im.interop.yahoo.YahooSession;

class YahooInteropSession extends InteropSession implements YahooEventListener {

    static final HashMap<YMSGStatus, Presence> sPresenceMap = new HashMap<YMSGStatus, Presence>();

    static {
        Presence pres = new Presence();
        
        sPresenceMap.put(YMSGStatus.NONE, pres.createCopy());
        sPresenceMap.put(YMSGStatus.AVAILABLE, pres.createCopy());
        sPresenceMap.put(YMSGStatus.INVISIBLE, pres.createCopy());
        sPresenceMap.put(YMSGStatus.CUSTOM, pres.createCopy());
        sPresenceMap.put(YMSGStatus.WEBLOGIN, pres.createCopy());
        
        pres.setShow(Presence.Show.dnd);
        sPresenceMap.put(YMSGStatus.BUSY, pres.createCopy());
        sPresenceMap.put(YMSGStatus.ONPHONE, pres.createCopy());

        pres.setShow(Presence.Show.away);
        sPresenceMap.put(YMSGStatus.IDLE, pres.createCopy());
        sPresenceMap.put(YMSGStatus.BRB, pres.createCopy());

        pres.setShow(Presence.Show.xa);
        sPresenceMap.put(YMSGStatus.NOTATDESK, pres.createCopy());
        sPresenceMap.put(YMSGStatus.STEPPEDOUT, pres.createCopy());
        sPresenceMap.put(YMSGStatus.NOTATHOME, pres.createCopy());
        sPresenceMap.put(YMSGStatus.NOTINOFFICE, pres.createCopy());
        sPresenceMap.put(YMSGStatus.ONVACATION, pres.createCopy());
        sPresenceMap.put(YMSGStatus.OUTTOLUNCH, pres.createCopy());

        pres.setShow(null);
        pres.setType(Presence.Type.unavailable);
        sPresenceMap.put(YMSGStatus.OFFLINE, pres.createCopy());
    }

    static SessionFactory getFactory() {
        return new SessionFactory() {
            public InteropSession createSession(Service service, JID jid, String name, String password) {
                return new YahooInteropSession(service, new JID(jid.toBareJID()), name, password);
            }
            public boolean isEnabled() { 
                return JYMsgAuthProvider.available();
            }

        };
    }

    private YahooInteropSession(Service interop, JID userJid, String username, String password) {
        super(interop, userJid, username, password);
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#authFailed(com.zimbra.cs.im.interop.yahoo.YahooSession) */
    public synchronized void authFailed(YahooSession session) {
        assert(mIsConnecting);
        mIsConnecting = false;
        info("AuthFailed");
        notifyConnectCompleted(ConnectCompletionStatus.AUTH_FAILURE);
    }

    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#buddyAdded(com.zimbra.cs.im.interop.yahoo.YahooSession, java.lang.String, java.lang.String) */
    public synchronized void buddyAdded(YahooSession session, YahooBuddy buddy, YahooGroup group) {
        debug("BuddyAdded: "+buddy.toString()+" "+group.toString());
        Presence subscribed = new Presence(Presence.Type.subscribed);
        send(getJidForContact(buddy), subscribed);
        updateContactSubscription(buddy);
        updateContactStatus(buddy);
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#buddyAddedUs(com.zimbra.cs.im.interop.yahoo.YahooSession, java.lang.String, java.lang.String, java.lang.String) */
    public synchronized void buddyAddedUs(YahooSession session, String ourId, String theirId, String msg) {
        debug("Buddy Added Us ("+ourId+") theirId="+theirId+" msg="+msg);
        Presence p = new Presence(Presence.Type.subscribe);
        send(getJidForContactId(theirId), p);
    }

    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#buddyRemoved(com.zimbra.cs.im.interop.yahoo.YahooSession, java.lang.String, java.lang.String) */
    public synchronized void buddyRemoved(YahooSession session, YahooBuddy buddy, YahooGroup group) {
        debug("Buddy Removed: "+buddy+" from group "+group);
        Presence subscribed = new Presence(Presence.Type.unsubscribed);
        send(getUserJid(), subscribed);
        updateContactSubscription(buddy);
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#buddyStatusChanged(com.zimbra.cs.im.interop.yahoo.YahooSession, com.zimbra.cs.im.interop.yahoo.YahooBuddy) */
    public synchronized void buddyStatusChanged(YahooSession session, YahooBuddy buddy) {
        debug("Buddy Status Changed: "+buddy.toString());
        updateContactStatus(buddy);
    }

    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#connectFailed(com.zimbra.cs.im.interop.yahoo.YahooSession) */
    public synchronized void connectFailed(YahooSession session) {
        debug("ConnectFailed: "+session.toString());
        assert(mIsConnecting);
        mIsConnecting = false;
        notifyConnectCompleted(ConnectCompletionStatus.COULDNT_CONNECT);
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#error(com.zimbra.cs.im.interop.yahoo.YahooSession, com.zimbra.cs.im.interop.yahoo.YahooError, long, java.lang.Object[]) */
    public synchronized void error(YahooSession session, YahooError error, long code, Object[] args) {
        if (mLog.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("ERROR: ");
            sb.append(error.toString());
            sb.append(" code=").append(code);
            boolean atFirst = true;
            if (args != null) {
                sb.append(" args: ");
                for (Object o : args) {
                    if (!atFirst)
                        sb.append(", ");
                    atFirst = false;
                    sb.append(o);
                }
            }
            info(sb.toString());
        }
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#loggedOn(com.zimbra.cs.im.interop.yahoo.YahooSession) */
    public synchronized void loggedOn(YahooSession session) {
        assert(mIsConnecting);
        mIsConnecting = false;
        debug("loggedOn");
        notifyConnectCompleted(ConnectCompletionStatus.SUCCESS);
    }
    
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#receivedBuddyList(com.zimbra.cs.im.interop.yahoo.YahooSession) */
    public synchronized void receivedBuddyList(YahooSession session) {
        debug("receivedBuddyList");
        for (YahooBuddy buddy : mYahoo.buddies()) {
            updateContactSubscription(buddy);
            updateContactStatus(buddy);
        }
    }

    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#receivedMessage(com.zimbra.cs.im.interop.yahoo.YahooSession, com.zimbra.cs.im.interop.yahoo.YahooMessage) */
    public synchronized void receivedMessage(YahooSession session, YahooMessage msg) {
        debug("Received Message: "+msg.toString());
        
        Message m = new Message();
        m.setType(Message.Type.chat);
        m.setBody(msg.getMessage());
        
        send(getJidForContactId(msg.getFrom()), m);
    }
    /* @see com.zimbra.cs.im.interop.yahoo.YahooEventListener#sessionClosed(com.zimbra.cs.im.interop.yahoo.YahooSession) */
    public synchronized void sessionClosed(YahooSession session) {
        debug("Session Closed");
        if (mIsConnecting) {
            notifyConnectCompleted(ConnectCompletionStatus.OTHER_TEMPORARY_FAILURE);
            mIsConnecting = false;
        } else
            notifyDisconnected();
    }
    
    
    private synchronized YahooBuddy findContactFromJid(JID jid) throws UserNotFoundException {
        if (jid.getNode() == null)
            throw new UserNotFoundException();
        
        String jidAsContactId = getContactIdFromJID(jid); 

        for (YahooBuddy c : mYahoo.buddies()) {
            if (c.getName().equals(jidAsContactId))
                return c;
        }
        throw new UserNotFoundException();
    }
    
    private synchronized List<String> getAllGroupNames(YahooBuddy buddy) {
        ArrayList<String> groupNames = new ArrayList<String>();
        for (YahooGroup group : mYahoo.groups()) {
            if (group.contains(buddy))
                groupNames.add(group.getName());
        }
        return groupNames;
    }
    
    private synchronized String getContactIdFromJID(JID jid) {
        return jid.getNode().replace('%', '@');
    }

    private synchronized JID getJidForContact(YahooBuddy contact) {
        String contactId = contact.getName();
        contactId = contactId.replace('@', '%');
        return new JID(contactId, getDomain(), null);
    }

    private synchronized JID getJidForContactId(String contactId) {
        contactId = contactId.replace('@', '%');
        return new JID(contactId, getDomain(), null);
    }

    private synchronized void updateContactStatus(YahooBuddy contact) {
        Presence p = sPresenceMap.get(contact.getStatus());
        if (p == null) 
            p = sPresenceMap.get(YMSGStatus.BUSY);
        p = p.createCopy();
        updatePresence(getJidForContact(contact), p);
    }
    
    private synchronized void updateContactSubscription(YahooBuddy buddy) { 
        try {
            List<String> groupNames = getAllGroupNames(buddy);
            if (groupNames.size() == 0) {
                removeRosterSubscription(getJidForContact(buddy));
            } else {
                addOrUpdateRosterSubscription(getJidForContact(buddy), buddy.getName(), 
                    groupNames, RosterItem.SUB_TO);
            }
        } catch (UserNotFoundException e) {
            error("UserNotFoundException", e);
        }
    }

    protected synchronized void connect() {
        mYahoo = Yahoo.connect(this, new JYMsgAuthProvider(), getUsername(), getPassword());
        mIsConnecting = true;
    }

    protected synchronized void disconnect() {
        mYahoo.disconnect();
        mIsConnecting = false;
    }

    /* @see com.zimbra.cs.im.interop.Session#handleProbe(org.xmpp.packet.Presence) */
    @Override
    protected synchronized void handleProbe(Presence pres) throws UserNotFoundException {
        YahooBuddy contact = findContactFromJid(pres.getTo());
        updateContactStatus(contact);
    }

    /* @see com.zimbra.cs.im.interop.Session#refreshAllPresence() */
    @Override
    protected synchronized void refreshAllPresence() {
        for (YahooBuddy b : mYahoo.buddies()) {
            updateContactStatus(b);
        }
    }

    /* @see com.zimbra.cs.im.interop.Session#processMessage(org.xmpp.packet.Message) */
    @Override
    protected synchronized List<Packet> sendMessage(Message m) {
        mYahoo.sendMessage(getContactIdFromJID(m.getTo()), m.getBody());
        return null;
    }
    
    /* @see com.zimbra.cs.im.interop.Session#setPresence(org.xmpp.packet.Presence) */
    @Override
    protected synchronized void setPresence(Presence pres) {
        String displayStatus = null;
        YMSGStatus status = YMSGStatus.AVAILABLE;
        if (pres.getType() == null) {
            Presence.Show show = pres.getShow();
            if (show != null) {
                switch (show) {
                    case chat:
                        status = YMSGStatus.AVAILABLE;;
                        break;
                    case away:
                        status = YMSGStatus.BRB;
                        break;
                    case xa:
                        status = YMSGStatus.STEPPEDOUT;
                        break;
                    case dnd:
                        status = YMSGStatus.BUSY;
                        break;
                }
            }

            if (pres.getStatus() != null)
                displayStatus = pres.getStatus();

        } else if (pres.getType() == Presence.Type.unavailable) {
            status = YMSGStatus.OFFLINE;
        } else {
            status = null;
        }
        if (status != null)
            mYahoo.setMyStatus(status, displayStatus);
    }
    
    /**
     * @param buddy
     * @return
     */
    private List<YahooGroup> getGroupsForBuddy(YahooBuddy buddy) {
        List<YahooGroup> toRet = new ArrayList<YahooGroup>();
        for (YahooGroup yg: mYahoo.groups()) {
            if (yg.contains(buddy)) {
                toRet.add(yg);
            }
        }
        return toRet;
    }
    
    /* @see com.zimbra.cs.im.interop.InteropSession#updateExternalSubscription(boolean) */
    @Override
    protected synchronized void updateExternalSubscription(JID remoteJID, List<String> groups) {
        if (groups.size() == 0) {
            groups.add("Buddies");
        }
        String remoteName = this.getContactIdFromJID(remoteJID);
        YahooBuddy buddy = mYahoo.getBuddy(remoteName);
        if (buddy == null) {
            for (String group : groups) {
                mYahoo.addBuddy(remoteName, group);
            }
        } else {
            HashSet<String> requestedGroups = new HashSet<String>();
            // we'll remove the ones we find which are already on the friend, then add the rest
            requestedGroups.addAll(groups);
            
            // do removes AFTER adds, otherwise the server will re-request
            // authorization from the client!
            List<String> toRemove = new ArrayList<String>();
            
            for (YahooGroup yg : getGroupsForBuddy(buddy)) {
                if (!requestedGroups.remove(yg.getName())) {
                    // on buddy, but not requested -- REMOVE
                    toRemove.add(yg.getName());
                }
            }
            
            for (String groupName : requestedGroups) {
                // not on buddy already -- ADD
                mYahoo.addBuddy(remoteName, groupName);
            }
            
            for (String remove : toRemove) {
                mYahoo.removeBuddy(remoteName, remove);
            }
        }
    }
    
    /* @see com.zimbra.cs.im.interop.InteropSession#removeExternalSubscription(org.xmpp.packet.JID) */
    @Override
    protected synchronized void removeExternalSubscription(JID remoteJID) {
        String remoteName = this.getContactIdFromJID(remoteJID);
        YahooBuddy buddy = mYahoo.getBuddy(remoteName);
        if (buddy != null) {
            for (YahooGroup g : getGroupsForBuddy(buddy)) {
                mYahoo.removeBuddy(remoteName, g.getName());
            }
        }
    }
    
    private boolean mIsConnecting = false;
    YahooSession mYahoo;
}
