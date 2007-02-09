///*
// * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
// * 
// * The contents of this file are subject to the Mozilla Public License Version
// * 1.1 ("License"); you may not use this file except in compliance with the
// * License. You may obtain a copy of the License at
// * http://www.zimbra.com/license
// * 
// * Software distributed under the License is distributed on an "AS IS" basis,
// * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
// * the specific language governing rights and limitations under the License.
// * 
// * The Original Code is: Zimbra Collaboration Suite Server.
// * 
// * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
// * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
// * 
// * Contributor(s):
// * 
// * ***** END LICENSE BLOCK *****
// */
//package com.zimbra.cs.im.interop;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//
//import org.jivesoftware.wildfire.user.UserNotFoundException;
//import org.xmpp.packet.JID;
//import org.xmpp.packet.Message;
//import org.xmpp.packet.Packet;
//import org.xmpp.packet.PacketError;
//import org.xmpp.packet.Presence;
//
//import com.zimbra.common.util.SystemUtil;
//
//import ymsg.network.AccountLockedException;
//import ymsg.network.LoginRefusedException;
//import ymsg.network.StatusConstants;
//import ymsg.network.YahooUser;
//import ymsg.network.event.SessionChatEvent;
//import ymsg.network.event.SessionConferenceEvent;
//import ymsg.network.event.SessionErrorEvent;
//import ymsg.network.event.SessionEvent;
//import ymsg.network.event.SessionExceptionEvent;
//import ymsg.network.event.SessionFileTransferEvent;
//import ymsg.network.event.SessionFriendEvent;
//import ymsg.network.event.SessionNewMailEvent;
//import ymsg.network.event.SessionNotifyEvent;
//
///**
// * Yahoo Interop Session
// */
//public class YahooSession extends Session implements ymsg.network.event.SessionListener {
//
//    static final HashMap<Long, Presence> sPresenceMap = new HashMap<Long, Presence>();
//
//    static {
//        Presence pres = new Presence();
//
//        sPresenceMap.put(StatusConstants.STATUS_AVAILABLE, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_INVISIBLE, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_NOTINOFFICE, pres.createCopy());
//
//        pres.setShow(Presence.Show.away);
//        sPresenceMap.put(StatusConstants.STATUS_BRB, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_NOTATHOME, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_NOTATDESK, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_STEPPEDOUT, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_IDLE, pres.createCopy());
//
//        pres.setShow(Presence.Show.dnd);
//        sPresenceMap.put(StatusConstants.STATUS_ONPHONE, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_BUSY, pres.createCopy());
//
//        pres.setShow(Presence.Show.xa);
//        sPresenceMap.put(StatusConstants.STATUS_OUTTOLUNCH, pres.createCopy());
//        sPresenceMap.put(StatusConstants.STATUS_ONVACATION, pres.createCopy());
//
//        pres.setShow(null);
//        pres.setType(Presence.Type.unavailable);
//        sPresenceMap.put(StatusConstants.STATUS_OFFLINE, pres.createCopy());
//
//    }
//
//    static SessionFactory getFactory() {
//        return new SessionFactory() {
//            public Session createSession(Service service, JID jid, String name, String password) {
//                return new YahooSession(service, new JID(jid.toBareJID()), name, password);
//            }
//        };
//    }
//
//    private ymsg.network.Session mYahoo;
//
//    public YahooSession(Service interop, JID userJid, String username, String password) {
//        super(interop, userJid, username, password);
//    }
//
//    public void buzzReceived(SessionEvent arg0) {
//        debug("buzzReceived");
//    }
//
//    public void chatConnectionClosed(SessionEvent arg0) {
//        debug("chatConnectionClosed");
//    }
//
//    public void chatLogoffReceived(SessionChatEvent arg0) {
//        debug("chatLogoffReceived");
//    }
//
//    public void chatLogonReceived(SessionChatEvent arg0) {
//        debug("chatLogonReceived");
//    }
//
//    public void chatMessageReceived(SessionChatEvent arg0) {
//        debug("chatMessageReceived");
//    }
//
//    public void chatUserUpdateReceived(SessionChatEvent arg0) {
//        debug("chatUserUpdateReceived");
//    }
//
//    public void conferenceInviteDeclinedReceived(SessionConferenceEvent arg0) {
//        debug("conferenceInviteDeclinedReceived");
//    }
//
//    public void conferenceInviteReceived(SessionConferenceEvent arg0) {
//        debug("conferenceInviteReceived");
//    }
//
//    public void conferenceLogoffReceived(SessionConferenceEvent arg0) {
//        debug("conferenceLogoffReceived");
//    }
//
//    public void conferenceLogonReceived(SessionConferenceEvent arg0) {
//        debug("conferenceLogonReceived");
//    }
//
//    public void conferenceMessageReceived(SessionConferenceEvent arg0) {
//        debug("conferenceMessageReceived");
//    }
//
//    public void connectionClosed(SessionEvent arg0) {
//        debug("connectionClosed");
//        super.logOff();
//        mYahoo = null;
//    }
//
//    public void contactRejectionReceived(SessionEvent arg0) {
//        debug("contactRejectionReceived");
//    }
//
//    public void contactRequestReceived(SessionEvent arg0) {
//        debug("contactRequestReceived");
//    }
//
//    public void errorPacketReceived(SessionErrorEvent arg0) {
//        debug("errorPacketReceived");
//    }
//
//    public void fileTransferReceived(SessionFileTransferEvent arg0) {
//        debug("fileTransferReceived");
//    }
//
//    YahooUser findContactFromJid(JID jid) throws UserNotFoundException {
//        String id = getIdFromJid(jid);
//        if (id != null) {
//            for (YahooUser user : ((Collection<YahooUser>) mYahoo.getUsers().values())) {
//                if (id.equals(user.getId()))
//                    return user;
//            }
//        }
//        throw new UserNotFoundException();
//    }
//
//    public void friendAddedReceived(SessionFriendEvent arg0) {
//        debug("friendAddedReceived");
//    }
//
//    public void friendRemovedReceived(SessionFriendEvent arg0) {
//        debug("friendRemovedReceived");
//    }
//
//    public void friendsUpdateReceived(SessionFriendEvent arg0) {
//        debug("friendsUpdateReceived");
//        syncContactList(arg0);
//    }
//
//    String getIdFromJid(JID jid) {
//        return jid.getNode().replace('%', '@');
//    }
//
//    JID getJidForContact(String contactId) {
//        contactId = contactId.replace('@', '%');
//        return new JID(contactId, getDomain(), null);
//    }
//
//    JID getJidForContact(YahooUser contact) {
//        return getJidForContact(contact.getId());
//    }
//
//    @Override
//    void handleProbe(Presence pres) throws UserNotFoundException {
//        YahooUser c = findContactFromJid(pres.getTo());
//        updateContactStatus(c);
//    }
//
//    public void inputExceptionThrown(SessionExceptionEvent ex) {
//        debug("inputExceptionThrown: %s %s", ex.toString(), SystemUtil.getStackTrace(ex.getException()));
//    }
//
//    public void listReceived(SessionEvent arg0) {
//        debug("listReceived");
//    }
//
//    public void messageReceived(SessionEvent message) {
//        debug("messageReceived");
//        Message m = new Message();
//        m.setType(Message.Type.chat);
//        m.setBody(message.getMessage());
//        sendMessage(getJidForContact(message.getFrom()), m);
//    }
//
//    public void newMailReceived(SessionNewMailEvent arg0) {
//        debug("newMailReceived");
//    }
//
//    public void notifyReceived(SessionNotifyEvent arg0) {
//        debug("notifyReceived");
//    }
//
//    public void offlineMessageReceived(SessionEvent arg0) {
//        debug("offlineMessageReceived");
//    }
//
//    @Override
//    List<Packet> processMessage(Message m) {
//        try {
//            mYahoo.sendMessage(getIdFromJid(m.getTo()), m.getBody());
//        } catch (IOException e) {
//            m.setError(PacketError.Condition.recipient_unavailable);
//            List<Packet> toRet = new ArrayList<Packet>();
//            toRet.add(m);
//            return toRet;
//        }
//        return null;
//    }
//
//    @Override
//    void refreshAllPresence() {
//        for (YahooUser user : ((Collection<YahooUser>) mYahoo.getUsers().values())) {
//            updateContactSubscription(user);
//            updateContactStatus(user);
//        }
//    }
//
//    @Override
//    void setPresence(Presence pres) {}
//
//    void subclassLogOff() {
//        try {
//            if (mYahoo != null) {
//                mYahoo.logout();
//                mYahoo = null;
//            }
//        } catch (IOException e) {
//            info("IOException logging out of yahoo", e);
//        }
//    }
//
//    @Override
//    boolean subclassLogOn() {
//        mYahoo = new ymsg.network.Session();
//        mYahoo.addSessionListener(this);
//        try {
//            mYahoo.login(getUsername(), getPassword());
//        } catch (AccountLockedException e) {
//            error("AccountLockedException", e);
//            return false;
//        } catch (IOException e) {
//            error("IOException", e);
//            return false;
//        } catch (LoginRefusedException e) {
//            info("LoginRefusedException", e);
//            return false;
//        }
//        return true;
//    }
//
//    void syncContactList(SessionFriendEvent event) {
//        for (YahooUser user : event.getFriends()) {
//            if (user != null) {
//                updateContactSubscription(user);
//                updateContactStatus(user);
//            }
//        }
//    }
//
//    void updateContactStatus(YahooUser contact) {
//        long status = contact.getStatus();
//        Presence p = sPresenceMap.get(status);
//        if (p == null) {
//            debug("Could not find presence map entry for status: %d", status);
//            p = sPresenceMap.get(StatusConstants.STATUS_AVAILABLE);
//        }
//        p = p.createCopy();
//        p.setStatus(contact.getId());
//        updatePresence(getJidForContact(contact), p);
//    }
//
//    void updateContactSubscription(YahooUser contact) {
//        try {
//            addRosterSubscription(getJidForContact(contact), contact.getId(), "YAHOO");
//        } catch (UserNotFoundException e) {
//            error("UserNotFoundException", e);
//        }
//    }
//}
