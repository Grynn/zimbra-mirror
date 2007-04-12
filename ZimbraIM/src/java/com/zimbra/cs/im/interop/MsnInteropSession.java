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

import net.sf.jml.Email;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnContactList;
import net.sf.jml.MsnFileTransfer;
import net.sf.jml.MsnGroup;
import net.sf.jml.MsnList;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnContactListListener;
import net.sf.jml.event.MsnFileTransferListener;
import net.sf.jml.event.MsnMessageListener;
import net.sf.jml.event.MsnMessengerListener;
import net.sf.jml.event.MsnSwitchboardListener;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.jml.message.MsnControlMessage;
import net.sf.jml.message.MsnDatacastMessage;
import net.sf.jml.message.MsnInstantMessage;
import net.sf.jml.message.MsnSystemMessage;
import net.sf.jml.message.MsnUnknownMessage;

import org.jivesoftware.wildfire.roster.RosterItem;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * Represents a single local user's session with the MSN service
 */
class MsnInteropSession extends InteropSession implements MsnContactListListener, MsnMessageListener, MsnMessengerListener,
            MsnSwitchboardListener, MsnFileTransferListener {

    /* @see com.zimbra.cs.im.interop.InteropSession#removeExternalSubscription(org.xmpp.packet.JID) */
    @Override
    protected void removeExternalSubscription(JID remoteJID) {
        // TODO Auto-generated method stub
        
    }

    private static final HashMap<MsnUserStatus, Presence> sPresenceMap = new HashMap<MsnUserStatus, Presence>();
    
    static {
        Presence pres = new Presence();

        sPresenceMap.put(MsnUserStatus.IDLE, pres.createCopy());
        sPresenceMap.put(MsnUserStatus.ONLINE, pres.createCopy());
        sPresenceMap.put(MsnUserStatus.HIDE, pres.createCopy());

        pres.setShow(Presence.Show.dnd);
        sPresenceMap.put(MsnUserStatus.BUSY, pres.createCopy());

        pres.setShow(Presence.Show.away);
        sPresenceMap.put(MsnUserStatus.ON_THE_PHONE, pres.createCopy());
        sPresenceMap.put(MsnUserStatus.BE_RIGHT_BACK, pres.createCopy());

        pres.setShow(Presence.Show.xa);
        sPresenceMap.put(MsnUserStatus.OUT_TO_LUNCH, pres.createCopy());
        sPresenceMap.put(MsnUserStatus.AWAY, pres.createCopy());

        pres.setShow(null);
        pres.setType(Presence.Type.unavailable);
        sPresenceMap.put(MsnUserStatus.OFFLINE, pres.createCopy());
    }

    static SessionFactory getFactory() {
        return new SessionFactory() {
            public InteropSession createSession(Service service, JID jid, String name, String password) {
                return new MsnInteropSession(service, new JID(jid.toBareJID()), name, password);
            }
            public boolean isEnabled() { return true; }
        };
    }

    private MsnInteropSession(Service interop, JID userJid, String username, String password) {
        super(interop, userJid, username, password);

        mMessenger = MsnMessengerFactory.createMsnMessenger(username, password);
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactAddCompleted(net.sf.jml.MsnMessenger, net.sf.jml.MsnContact) */
    public synchronized void contactAddCompleted(MsnMessenger messenger, MsnContact contact) {
        debug("contactAddCompleted");
        List<String> groups = mPendingBuddyGroups.remove(contact.getEmail());
        for (String group : groups) {
            addToGroup(group, contact.getEmail());
        }
        updateContactSubscription(contact);
        updateContactStatus(contact);
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactAddedMe(net.sf.jml.MsnMessenger, net.sf.jml.MsnContact) */
    public synchronized void contactAddedMe(MsnMessenger messenger, MsnContact contact) {
        debug("contactAddedMe");
        Presence p = new Presence(Presence.Type.subscribe);
        send(getJidForContact(contact), p);
        messenger.unblockFriend(contact.getEmail());
    }

    /* @see net.sf.jml.event.MsnSwitchboardListener#contactJoinSwitchboard(net.sf.jml.MsnSwitchboard, net.sf.jml.MsnContact) */
    public synchronized void contactJoinSwitchboard(MsnSwitchboard switchboard, MsnContact contact) {
        debug("contactJoinSwitchboard");
        Object o = switchboard.getAttachment();
        if (o instanceof Message) {
            Message m = (Message) o;
            switchboard.setAttachment(m.getThread());
            sendMessageOverSwitchboard(switchboard, m);
        }
    }

    /* @see net.sf.jml.event.MsnSwitchboardListener#contactLeaveSwitchboard(net.sf.jml.MsnSwitchboard, net.sf.jml.MsnContact) */
    public synchronized void contactLeaveSwitchboard(MsnSwitchboard switchboard, MsnContact contact) {
        debug("contactLeaveSwitchboard");
        Object o = switchboard.getAttachment();
        if (o instanceof Message) {
            Message m = (Message) o;
            switchboard.setAttachment(m.getThread());
            sendMessageOverSwitchboard(switchboard, m);
        }
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactListInitCompleted(net.sf.jml.MsnMessenger) */
    public synchronized void contactListInitCompleted(MsnMessenger messenger) {
        {
            StringBuilder sb = new StringBuilder("contactListInitCompleted");
            MsnContactList l = mMessenger.getContactList();
            for (MsnContact c : l.getContacts()) {
                sb.append("\tContact: ").append(c.getEmail()).append(" status=").append(c.getStatus()).append('\n');
            }
            debug(sb.toString());
        }
        MsnContactList l = mMessenger.getContactList();
        for (MsnContact c : l.getContacts()) {
            if (c.isInList(MsnList.FL)) {
                updateContactSubscription(c);
                updateContactStatus(c);
            } else {
                debug("Skipping contact: %s, not in FL", c.toString());
            }
        }
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactListSyncCompleted(net.sf.jml.MsnMessenger) */
    public synchronized void contactListSyncCompleted(MsnMessenger messenger) {
        StringBuilder sb = new StringBuilder("ContactListSyncCompleted:\n");
        debug(sb.toString());
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactRemoveCompleted(net.sf.jml.MsnMessenger, net.sf.jml.MsnContact) */
    public synchronized void contactRemoveCompleted(MsnMessenger messenger, MsnContact contact) {
        debug("contactRemoveCompleted: %s", contact.getEmail().toString());
        updateContactSubscription(contact);
        updateContactStatus(contact);
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactRemovedMe(net.sf.jml.MsnMessenger, net.sf.jml.MsnContact) */
    public synchronized void contactRemovedMe(MsnMessenger messenger, MsnContact contact) {
        debug("contactRemovedMe");
    }

    /* @see net.sf.jml.event.MsnContactListListener#contactStatusChanged(net.sf.jml.MsnMessenger, net.sf.jml.MsnContact) */
    public synchronized void contactStatusChanged(MsnMessenger messenger, MsnContact contact) {
        debug("contactStatusChanged: %s changed to %s", contact.getEmail().toString(), contact.getStatus().toString());
        updateContactStatus(contact);
    }
    
    /* @see net.sf.jml.event.MsnMessageListener#controlMessageReceived(net.sf.jml.MsnSwitchboard, net.sf.jml.message.MsnControlMessage, net.sf.jml.MsnContact) */
    public synchronized void controlMessageReceived(MsnSwitchboard switchboard, MsnControlMessage message,
                MsnContact contact) {
        debug("controlMessageReceived: %s", message.getHeaders().toString());
    }

    /* @see net.sf.jml.event.MsnMessageListener#datacastMessageReceived(net.sf.jml.MsnSwitchboard, net.sf.jml.message.MsnDatacastMessage, net.sf.jml.MsnContact) */
    public synchronized void datacastMessageReceived(MsnSwitchboard switchboard, MsnDatacastMessage message,
                MsnContact contact) {
        debug("datacastMessageReceived");
    }

    /* @see net.sf.jml.event.MsnMessengerListener#exceptionCaught(net.sf.jml.MsnMessenger, java.lang.Throwable) */
    public synchronized void exceptionCaught(MsnMessenger messenger, Throwable throwable) {
        debug("exceptionCaught: %s", throwable.toString());
        throwable.printStackTrace();
        disconnect();
        notifyDisconnected();
    }

    /* @see net.sf.jml.event.MsnFileTransferListener#fileTransferFinished(net.sf.jml.MsnFileTransfer) */
    public synchronized void fileTransferFinished(MsnFileTransfer transfer) {
        debug("fileTransferFinished");
    }

    /* @see net.sf.jml.event.MsnFileTransferListener#fileTransferProcess(net.sf.jml.MsnFileTransfer) */
    public synchronized void fileTransferProcess(MsnFileTransfer transfer) {
        debug("fileTransferProcess");
    }

    /* @see net.sf.jml.event.MsnFileTransferListener#fileTransferRequestReceived(net.sf.jml.MsnFileTransfer) */
    public synchronized void fileTransferRequestReceived(MsnFileTransfer transfer) {
        debug("fileTransferRequestReceived");
    }

    /* @see net.sf.jml.event.MsnFileTransferListener#fileTransferStarted(net.sf.jml.MsnFileTransfer) */
    public synchronized void fileTransferStarted(MsnFileTransfer transfer) {
        debug("fileTransferStarted");
    }

    /* @see net.sf.jml.event.MsnContactListListener#groupAddCompleted(net.sf.jml.MsnMessenger, net.sf.jml.MsnGroup) */
    public synchronized void groupAddCompleted(MsnMessenger messenger, MsnGroup group) {
        debug("groupAddCompleted");
        List<Email> toAdd = mPendingGroupChanges.remove(group.getGroupName());
        if (toAdd != null) {
            for (Email e : toAdd) {
                addToGroup(group, e);
            }
        }
    }

    /* @see net.sf.jml.event.MsnContactListListener#groupRemoveCompleted(net.sf.jml.MsnMessenger, net.sf.jml.MsnGroup) */
    public synchronized void groupRemoveCompleted(MsnMessenger messenger, MsnGroup group) {
        debug("groupRemoveCompleted");
    }

    /* @see net.sf.jml.event.MsnMessageListener#instantMessageReceived(net.sf.jml.MsnSwitchboard, net.sf.jml.message.MsnInstantMessage, net.sf.jml.MsnContact) */
    public synchronized void instantMessageReceived(MsnSwitchboard switchboard, MsnInstantMessage message,
                MsnContact contact) {
        debug("instantMessageReceived from %s: %s", contact.getFriendlyName(), message.getContent());

        Message m = new Message();
        m.setType(Message.Type.chat);
        m.setBody(message.getContent());

        if (switchboard.getAttachment() != null) {
            if (switchboard.getAttachment() instanceof String) {
                m.setThread((String) switchboard.getAttachment());
            }
        } else {
            String threadId = generateThreadId();
            m.setThread(threadId);
            switchboard.setAttachment(threadId);
        }

        send(getJidForContact(contact), m);
    }

    /* @see net.sf.jml.event.MsnMessengerListener#loginCompleted(net.sf.jml.MsnMessenger) */
    public synchronized void loginCompleted(MsnMessenger messenger) {
        debug("loginCompleted");
        mIsConnecting = false;
        notifyConnectCompleted(ConnectCompletionStatus.SUCCESS);
    }

    /* @see net.sf.jml.event.MsnMessengerListener#logout(net.sf.jml.MsnMessenger) */
    public synchronized void logout(MsnMessenger messenger) {
        debug("logout");
        if (mIsConnecting) {
            notifyConnectCompleted(ConnectCompletionStatus.OTHER_TEMPORARY_FAILURE);
        } else {
            notifyDisconnected();
        }
        mIsConnecting = false;
    }

    /* @see net.sf.jml.event.MsnContactListListener#ownerStatusChanged(net.sf.jml.MsnMessenger) */
    public synchronized void ownerStatusChanged(MsnMessenger messenger) {
        debug("ownerStatusChanged, now: %s", messenger.getOwner().getStatus().toString());
    }

    /* @see net.sf.jml.event.MsnSwitchboardListener#switchboardClosed(net.sf.jml.MsnSwitchboard) */
    public synchronized void switchboardClosed(MsnSwitchboard switchboard) {
        debug("switchboardClosed");
    }

    /* @see net.sf.jml.event.MsnSwitchboardListener#switchboardStarted(net.sf.jml.MsnSwitchboard) */
    public synchronized void switchboardStarted(MsnSwitchboard switchboard) {
        debug("switchboardStarted: %s", switchboard.toString());
        Object o = switchboard.getAttachment();
        if (o instanceof Message) {
            Message m = (Message) o;
            if (m.getTo() != null) {
                MsnContact contact = getContactFromJid(m.getTo());
                switchboard.inviteContact(contact.getEmail());
                m.setTo((JID) null);
            }
        } else {
            System.out.println(o);
        }
    }
    
    /* @see net.sf.jml.event.MsnMessageListener#systemMessageReceived(net.sf.jml.MsnMessenger, net.sf.jml.message.MsnSystemMessage) */
    public synchronized void systemMessageReceived(MsnMessenger messenger, MsnSystemMessage message) {
        debug("systemMessageReceived");
//        debug("systemMessageReceived: %s - %s", message.toString(), message.getContent());
    }

    /* @see net.sf.jml.event.MsnMessageListener#unknownMessageReceived(net.sf.jml.MsnSwitchboard, net.sf.jml.message.MsnUnknownMessage, net.sf.jml.MsnContact) */
    public synchronized void unknownMessageReceived(MsnSwitchboard switchboard, MsnUnknownMessage message,
                MsnContact contact) {
        debug("unknownMessageReceived: %s", message.getContentAsString());
    }

    private synchronized MsnContact findContactFromJid(JID jid) throws UserNotFoundException {
        if (jid.getNode() == null)
            throw new UserNotFoundException();

        MsnContactList list = mMessenger.getContactList();
        MsnContact[] contacts = list.getContacts();
        String jidAsContactId = getContactIdFromJID(jid);
        for (MsnContact c : contacts) {
            if (c.getEmail().toString().equals(jidAsContactId))
                return c;
        }
        throw new UserNotFoundException();
    }

    private synchronized MsnSwitchboard findSwitchboard(String threadId, Message m) {
        MsnSwitchboard[] sbs = mMessenger.getActiveSwitchboards();
        for (MsnSwitchboard sb : sbs) {
            if (sb.getAttachment() != null && sb.getAttachment().equals(threadId))
                return sb;
        }

        m.setThread(threadId);
        mMessenger.newSwitchboard(m);
        return null;
    }

    private synchronized String generateThreadId() {
        String toRet = getUserJid().toBareJID() + "-iop-" + mChatId;
        mChatId++;
        return toRet;
    }

    private synchronized MsnContact getContactFromJid(JID jid) {
        MsnContactList contacts = mMessenger.getContactList();
        Email email = Email.parseStr(getContactIdFromJID(jid));
        return contacts.getContactByEmail(email);
    }

    private synchronized String getContactIdFromJID(JID jid) {
        return jid.getNode().replace('%', '@');
    }

    private synchronized JID getJidForContact(MsnContact contact) {
        String contactId = contact.getEmail().toString();
        contactId = contactId.replace('@', '%');
        return new JID(contactId, getDomain(), null);
    }

    private synchronized boolean sendMessageOverSwitchboard(MsnSwitchboard sb, Message m) {
        MsnInstantMessage msg = new MsnInstantMessage();
        msg.setContent(m.getBody());

        return sb.sendMessage(msg, false);
    }

    private synchronized void updateContactStatus(MsnContact contact) {
        debug("updateContactStatus(%s : %s)", contact.getEmail().toString(), contact.getStatus().toString());
        MsnUserStatus status = contact.getStatus();
        Presence p = sPresenceMap.get(status).createCopy();
        p.setStatus(contact.getDisplayName());
        updatePresence(getJidForContact(contact), p);
    }

    private synchronized void updateContactSubscription(MsnContact contact) {
        try {
            MsnGroup[] groups = contact.getBelongGroups();
//            List<String> groupNames = new ArrayList<String>(groups.length);
//            for (MsnGroup group : groups) {
//                groupNames.add(group.getGroupName());
//          }
            addOrUpdateRosterSubscription(getJidForContact(contact), contact.getFriendlyName(), 
                "Buddies", RosterItem.SUB_TO);
        } catch (UserNotFoundException e) {
            error("UserNotFoundException", e);
        }
    }

    protected synchronized void connect() {
        mIsConnecting = true;
        mMessenger.setLogIncoming(true);
        mMessenger.setLogOutgoing(true);
//        mMessenger.getOwner().setStatus(MsnUserStatus.BUSY);
        mMessenger.addContactListListener(this);
        mMessenger.addFileTransferListener(this);
        mMessenger.addMessageListener(this);
        mMessenger.addMessengerListener(this);
        mMessenger.addSwitchboardListener(this);
        mMessenger.login();
    }

    protected synchronized void disconnect() {
        mMessenger.removeContactListListener(this);
        mMessenger.removeFileTransferListener(this);
        mMessenger.removeMessageListener(this);
        mMessenger.removeMessengerListener(this);
        mMessenger.removeSwitchboardListener(this);
        mMessenger.logout();
        notifyDisconnected();
    }
    
    protected synchronized void handleProbe(Presence pres) throws UserNotFoundException {
        MsnContact c = findContactFromJid(pres.getTo());
        updateContactStatus(c);
    }
    
    protected synchronized void refreshAllPresence() {
        for (MsnContact c : mMessenger.getContactList().getContacts()) {
            updateContactStatus(c);
        }
    }

    protected synchronized List<Packet> sendMessage(Message m) {
        String threadId = m.getThread();
        if (threadId == null) {
            threadId = generateThreadId();
        }
        MsnSwitchboard sb = findSwitchboard(threadId, m);
        if (sb != null) {
            sendMessageOverSwitchboard(sb, m);
        }

        return null;
    }

    protected synchronized void setPresence(Presence pres) {
        if (!mIsConnecting) {
            String displayStatus = null;
            MsnUserStatus status = MsnUserStatus.ONLINE;
            if (pres.getType() == null) {
                Presence.Show show = pres.getShow();
                if (show != null) {
                    switch (show) {
                        case chat:
                            status = MsnUserStatus.ONLINE;
                            break;
                        case away:
                            status = MsnUserStatus.AWAY;
                            break;
                        case xa:
                            status = MsnUserStatus.OUT_TO_LUNCH;
                            break;
                        case dnd:
                            status = MsnUserStatus.BUSY;
                            break;
                    }
                }
                
                if (pres.getStatus() != null)
                    displayStatus = pres.getStatus();

            } else if (pres.getType() == Presence.Type.unavailable) {
                status = MsnUserStatus.OFFLINE;
            } else {
                status = null;
            }

            if (status != null)
                mMessenger.getOwner().setStatus(status);
            if (displayStatus != null)
                mMessenger.getOwner().setPersonalMessage(displayStatus);
        }
    }
    
    private synchronized MsnGroup findGroup(String name) {
        MsnGroup[] groups = mMessenger.getContactList().getGroups();
        for (MsnGroup group : groups) {
            if (group.getGroupName().equals(name))
                return group;
        }
        return null;
    }
    
    private synchronized void addToGroup(String groupName, Email email) {
        MsnGroup grp = findGroup(groupName);
        if (grp == null) {
            List<Email> list = mPendingGroupChanges.get(groupName);
            if (list == null) {
                list = new ArrayList<Email>();
                mPendingGroupChanges.put(groupName, list);
            }
            if (!list.contains(email))
                list.add(email);
            mMessenger.addGroup(groupName);
        } else {
            addToGroup(grp, email);
        }
    }
    
    private synchronized void addToGroup(MsnGroup group, Email email) {
        mMessenger.copyFriend(email, group.getGroupId());
    }
    
    private HashMap<String /*groupName*/, List<Email> /*itemsToAdd*/> mPendingGroupChanges = new HashMap<String, List<Email>>();
    
    private synchronized void addOrUpdateBuddy(Email email, String friendlyName, List<String> groups) {
        MsnContact contact = mMessenger.getContactList().getContactByEmail(email);
//        if (contact != null && contact.getBelongGroups().length > 0) {
        if (contact != null) {
            HashSet<String> requestedGroups = new HashSet<String>();
            requestedGroups.addAll(groups);
            // we'll remove the ones we find which are already on the friend, then add the rest
            
            // check existing groups for ones which are no longer htere
            MsnGroup[] contactGroups = contact.getBelongGroups();
            for (MsnGroup g : contactGroups) {
                if (!requestedGroups.remove(g.getGroupName())) {
                    mMessenger.removeFriend(email, g.getGroupId());
                } 
            }
            
            for (String groupName : requestedGroups) {
                addToGroup(groupName, email);
            }
        } else {
            List<String> list = mPendingBuddyGroups.get(email);
            if (list == null) {
                list = new ArrayList<String>();
                mPendingBuddyGroups.put(email, list);
            }
            list.addAll(groups);
            mMessenger.addFriend(email, friendlyName);
        }
    }
    
    private HashMap<Email /*buddyName*/, List<String> /*groupsToAdd*/> mPendingBuddyGroups = new HashMap<Email, List<String>>();
    
    
    /* @see com.zimbra.cs.im.interop.InteropSession#updateExternalSubscription(boolean) */
    @Override
    protected void updateExternalSubscription(JID remoteJID, List<String> groups) {
        String remoteName = this.getContactIdFromJID(remoteJID);
        Email email = Email.parseStr(remoteName);
        if (groups.size() == 0) {
            MsnContact contact = mMessenger.getContactList().getContactByEmail(email);
            for (MsnGroup mgrp : contact.getBelongGroups()) {
                mMessenger.removeFriend(email, mgrp.getGroupId());
            }
        } else {
            addOrUpdateBuddy(email, email.toString(), groups);
        }
    }

    private int mChatId = 100;

    boolean mIsConnecting = false;

    MsnMessenger mMessenger;
}
