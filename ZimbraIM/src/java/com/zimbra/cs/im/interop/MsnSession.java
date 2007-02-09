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

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import net.sf.jml.MsnContact;
import net.sf.jml.MsnContactList;
import net.sf.jml.MsnFileTransfer;
import net.sf.jml.MsnGroup;
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
import net.sf.jml.message.MsnMimeMessage;
import net.sf.jml.message.MsnSystemMessage;
import net.sf.jml.message.MsnUnknownMessage;

/**
 * Represents a single local user's session with the MSN service
 */
class MsnSession extends Session implements MsnContactListListener, MsnMessageListener, MsnMessengerListener,
            MsnSwitchboardListener, MsnFileTransferListener {

    static final HashMap<MsnUserStatus, Presence> sPresenceMap = new HashMap<MsnUserStatus, Presence>();

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
            public Session createSession(Service service, JID jid, String name, String password) {
                return new MsnSession(service, new JID(jid.toBareJID()), name, password);
            }
        };
    }

    private int mChatId = 100;

    boolean mLoginCompleted = false;

    MsnMessenger mMessenger;

    MsnSession(Service interop, JID userJid, String username, String password) {
        super(interop, userJid, username, password);

        mMessenger = MsnMessengerFactory.createMsnMessenger(username, password);
    }

    public void contactAddCompleted(MsnMessenger messenger, MsnContact contact) {
        debug("contactAddCompleted");
    }

    public void contactAddedMe(MsnMessenger messenger, MsnContact contact) {
        debug("contactAddedMe");
    }

    public void contactJoinSwitchboard(MsnSwitchboard switchboard, MsnContact contact) {
        debug("contactJoinSwitchboard");
        Object o = switchboard.getAttachment();
        if (o instanceof Message) {
            Message m = (Message) o;
            switchboard.setAttachment(m.getThread());
            sendMessageOverSwitchboard(switchboard, m);
        }
    }

    public void contactLeaveSwitchboard(MsnSwitchboard switchboard, MsnContact contact) {
        debug("contactLeaveSwitchboard");
    }

    public void contactListInitCompleted(MsnMessenger messenger) {
        debug("contactListInitCompleted");
        initialSyncContactList();
    }

    public void contactListSyncCompleted(MsnMessenger messenger) {
        debug("contactListSyncCompleted");
    }

    public void contactRemoveCompleted(MsnMessenger messenger, MsnContact contact) {
        debug("contactRemoveCompleted");
    }

    public void contactRemovedMe(MsnMessenger messenger, MsnContact contact) {
        debug("contactRemovedMe");
    }

    public void contactStatusChanged(MsnMessenger messenger, MsnContact contact) {
        debug("contactStatusChanged");
        updateContactStatus(contact);
    }

    public void controlMessageReceived(MsnSwitchboard switchboard, MsnControlMessage message,
                MsnContact contact) {
        debug("controlMessageReceived: %s", message.getHeaders().toString());
    }

    public void datacastMessageReceived(MsnSwitchboard switchboard, MsnDatacastMessage message,
                MsnContact contact) {
        debug("datacastMessageReceived");
    }

    public void exceptionCaught(MsnMessenger messenger, Throwable throwable) {
        debug("exceptionCaught: %s", throwable);
    }

    public void fileTransferFinished(MsnFileTransfer transfer) {
        debug("fileTransferFinished");
    }

    public void fileTransferProcess(MsnFileTransfer transfer) {
        debug("fileTransferProcess");
    }

    public void fileTransferRequestReceived(MsnFileTransfer transfer) {
        debug("fileTransferRequestReceived");
    }

    public void fileTransferStarted(MsnFileTransfer transfer) {
        debug("fileTransferStarted");
    }

    MsnContact findContactFromJid(JID jid) throws UserNotFoundException {
        if (jid.getNode() == null)
            throw new UserNotFoundException();

        MsnContactList list = mMessenger.getContactList();
        MsnContact[] contacts = list.getContacts();
        for (MsnContact c : contacts) {
            if (c.getId().equals(jid.getNode()))
                return c;
        }
        throw new UserNotFoundException();
    }

    MsnSwitchboard findSwitchboard(String threadId, Message m) {
        MsnSwitchboard[] sbs = mMessenger.getActiveSwitchboards();
        for (MsnSwitchboard sb : sbs) {
            if (sb.getAttachment() != null && sb.getAttachment().equals(threadId))
                return sb;
        }

        m.setThread(threadId);
        mMessenger.newSwitchboard(m);
        return null;
    }

    synchronized String generateThreadId() {
        String toRet = getUserJid().toBareJID() + "-iop-" + mChatId;
        mChatId++;
        return toRet;
    }

    MsnContact getContactFromJid(JID jid) {
        MsnContactList contacts = mMessenger.getContactList();
        return contacts.getContactById(jid.getNode());
    }

    JID getJidForContact(MsnContact contact) {
        String contactId = contact.getId();
        contactId = contactId.replace('@', '%');
        return new JID(contactId, getDomain(), null);
    }

    public void groupAddCompleted(MsnMessenger messenger, MsnGroup group) {
        debug("groupAddCompleted");
    }

    public void groupRemoveCompleted(MsnMessenger messenger, MsnGroup group) {
        debug("groupRemoveCompleted");
    }

    void handleProbe(Presence pres) throws UserNotFoundException {
        MsnContact c = findContactFromJid(pres.getTo());
        updateContactStatus(c);
    }

    void initialSyncContactList() {
        for (MsnContact c : mMessenger.getContactList().getContacts()) {
            updateContactSubscription(c);
            updateContactStatus(c);
        }
    }

    public void instantMessageReceived(MsnSwitchboard switchboard, MsnInstantMessage message,
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

        sendMessage(getJidForContact(contact), m);
    }

    public void loginCompleted(MsnMessenger messenger) {
        debug("loginCompleted");
        mLoginCompleted = true;
    }

    public void logout(MsnMessenger messenger) {
        debug("logout");
        mLoginCompleted = false;
    }

    public void ownerStatusChanged(MsnMessenger messenger) {
        debug("ownerStatusChanged");
    }

    List<Packet> processMessage(Message m) {
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

    void refreshAllPresence() {
        for (MsnContact c : mMessenger.getContactList().getContacts()) {
            updateContactStatus(c);
        }
    }

    boolean sendMessageOverSwitchboard(MsnSwitchboard sb, Message m) {
        MsnInstantMessage msg = new MsnInstantMessage();
        msg.setContent(m.getBody());

        return sb.sendMessage(msg, false);
    }

    void setPresence(Presence pres) {
        if (mLoginCompleted) {
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

    void subclassLogOff() {
        mMessenger.removeContactListListener(this);
        mMessenger.removeFileTransferListener(this);
        mMessenger.removeMessageListener(this);
        mMessenger.removeMessengerListener(this);
        mMessenger.removeSwitchboardListener(this);
        mMessenger.logout();
    }

    boolean subclassLogOn() {
        mMessenger.addContactListListener(this);
        mMessenger.addFileTransferListener(this);
        mMessenger.addMessageListener(this);
        mMessenger.addMessengerListener(this);
        mMessenger.addSwitchboardListener(this);
        mMessenger.login();
        return true;
    }

    public void switchboardClosed(MsnSwitchboard switchboard) {
        debug("switchboardClosed");
    }

    public void switchboardStarted(MsnSwitchboard switchboard) {
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

    public void systemMessageReceived(MsnMessenger messenger, MsnSystemMessage message) {
        debug("systemMessageReceived: %s", message.getContent());
    }

    public void unknownMessageReceived(MsnSwitchboard switchboard, MsnUnknownMessage message,
                MsnContact contact) {
        debug("unknownMessageReceived: %s", message.getContentAsString());
    }

    void updateContactStatus(MsnContact contact) {
        MsnUserStatus status = contact.getStatus();
        Presence p = sPresenceMap.get(status).createCopy();
        p.setStatus(contact.getDisplayName());
        updatePresence(getJidForContact(contact), p);
    }

    void updateContactSubscription(MsnContact contact) {
        try {
            addRosterSubscription(getJidForContact(contact), contact.getFriendlyName(), "MSN");
        } catch (UserNotFoundException e) {
            error("UserNotFoundException", e);
        }
    }
}
