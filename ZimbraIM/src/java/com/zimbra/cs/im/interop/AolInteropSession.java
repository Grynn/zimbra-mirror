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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.kano.joscar.snaccmd.FullUserInfo;
import net.kano.joscar.snaccmd.InfoData;
import net.kano.joscar.snaccmd.conn.WarningNotification;
import net.kano.joscar.snaccmd.icbm.InstantMessage;
import net.kano.joscar.ssiitem.BuddyItem;
import net.kano.joscar.ssiitem.SsiItemObj;

import org.jivesoftware.wildfire.roster.RosterItem;
import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

import com.zimbra.cs.im.interop.aol.AolConnection;
import com.zimbra.cs.im.interop.aol.AolEventListener;
import com.zimbra.cs.im.interop.aol.AolMgr;

/**
 * Session with the AIM service
 */
public class AolInteropSession extends InteropSession implements AolEventListener {

    /* @see com.zimbra.cs.im.interop.InteropSession#removeExternalSubscription(org.xmpp.packet.JID) */
    @Override
    protected void removeExternalSubscription(JID remoteJID) {
        // TODO Auto-generated method stub
        
    }

    /* @see com.zimbra.cs.im.interop.InteropSession#updateExternalSubscription(boolean) */
    @Override
    protected void updateExternalSubscription(JID remoteJID, List<String> group) {
        // TODO Auto-generated method stub
        assert(false); // writeme!
    }

    public synchronized void receivedBuddyOffline(String name) {
        debug("buddyOffline: %s", name);
    }

    public synchronized void receivedBuddyStatus(FullUserInfo info) {
        debug("receivedBuddyStatus: %s", info);
        updateContactStatus(info);
    }

    public synchronized void receivedIM(FullUserInfo sender, InstantMessage message) {
        debug("receivedIM from  %s: %s", sender, message);

        Message m = new Message();
        m.setType(Message.Type.chat);
        m.setBody(message.getMessage());
        send(getJidForContact(sender.getScreenname()), m);
    }

    private JID getJidForContact(String screenName) {
        screenName = screenName.replace('@', '%');
        return new JID(screenName, getDomain(), null);
    }
    
    private String getIdFromJid(JID jid) {
        return jid.getNode().replace('%', '@');
    }

    private synchronized void updateContactSubscription(String screenName, String displayName, String group) {
        try {
            addOrUpdateRosterSubscription(getJidForContact(screenName), displayName, 
                group, RosterItem.SUB_TO);
        } catch (UserNotFoundException e) {
            error("UserNotFoundException", e);
        }
    }

    static final HashMap<Long, Presence> sIcqPresenceMap = new HashMap<Long, Presence>();

    static {
        Presence pres = new Presence();
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_INVISIBLE, pres.createCopy());
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_DEFAULT, pres.createCopy());
        pres.setShow(Presence.Show.dnd);
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_DND, pres.createCopy());
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_OCCUPIED, pres.createCopy());
        pres.setShow(Presence.Show.away);
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_AWAY, pres.createCopy());
        pres.setShow(Presence.Show.xa);
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_NA, pres.createCopy());
        pres.setShow(Presence.Show.chat);
        sIcqPresenceMap.put(FullUserInfo.ICQSTATUS_FFC, pres.createCopy());

        pres.setShow(null);
    }        


    private synchronized void updateContactStatus(FullUserInfo info) {
        Presence pres;
        if (info.getIcqStatus() >=0) {
            pres = sIcqPresenceMap.get(info.getIcqStatus());
            if (pres == null) {
                pres = new Presence();
            } else {
                pres = pres.createCopy();
            }
        } else {
            pres = new Presence();
            if (info.getAwayStatus()) {
                pres.setShow(Presence.Show.away);
            }
        }
        updatePresence(getJidForContact(info.getScreenname()), pres);
    }

    public synchronized void receivedSSI(List<SsiItemObj> items) {
        notifyConnectCompleted(ConnectCompletionStatus.SUCCESS);
        
        debug("receivedSSI: %d items", items.size());
        for (SsiItemObj ssi : items) {
            if (ssi instanceof BuddyItem) {
                BuddyItem bi = (BuddyItem)ssi;
                updateContactSubscription(bi.getScreenname(), bi.getAlias(), "AOL");
            }
        }
    }

    public synchronized void receivedUserInfo(FullUserInfo user, InfoData info) {
        debug("receivedUserInfo for user %s: %s", user, info);
    }

    public synchronized void receivedWarning(WarningNotification warning) {
        debug("receivedWarning: %s", warning);
    }

    public synchronized void receivedTypingNotification(AolConnection conn, String screenName, int typingState) {
        debug("typingNotification for  %s : %d", screenName, typingState);
    }

    public synchronized void receivedYourUserInfo(FullUserInfo user) {
        debug("yourUserInfo: %s", user);
    }

    public synchronized void exception(String desc, Throwable t, boolean fatal) {
        if (fatal) {
            error("Fatal Exception: %s - %s", desc, t);
            try {
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyDisconnected();
        } else {
            info("Exception: %s - %s", desc, t);
        }
    }

    public AolInteropSession(Service interop, JID userJid, String username, String password) {
        super(interop, userJid, username, password);
    }

    static SessionFactory getFactory() {
        return new SessionFactory() {
            public InteropSession createSession(Service service, JID jid, String name, String password) {
                return new AolInteropSession(service, new JID(jid.toBareJID()), name, password);
            }
            public boolean isEnabled() { return true; }
        };
    }

    @Override
    protected synchronized void handleProbe(Presence pres) throws UserNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    protected synchronized List<Packet> sendMessage(Message m) {
        try {
            mAol.sendMessage(getIdFromJid(m.getTo()), m.getBody());
        } catch (IOException e) {
            m.setError(PacketError.Condition.recipient_unavailable);
            List<Packet> toRet = new ArrayList<Packet>();
            toRet.add(m);
            return toRet;
        }
        return null;
    }

    @Override
    protected synchronized void refreshAllPresence() {
        // TODO Auto-generated method stub

    }

    @Override
    protected synchronized void setPresence(Presence pres) {
        // TODO Auto-generated method stub

    }

    @Override
    protected synchronized void disconnect() {
        mAol.disconnect();
        mAol = null;
    }

    @Override
    protected synchronized void connect() {
        try {
            mAol = new AolMgr(this);
            mAol.connect(this.getUsername(), this.getPassword());
        } catch (IOException e) {
            info("Caught IOException while logging on", e);
            notifyConnectCompleted(ConnectCompletionStatus.AUTH_FAILURE);
        }
    }

    private AolMgr mAol;
}
