/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package org.jivesoftware.wildfire;

import org.jivesoftware.util.IMConfig;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.container.BasicModule;
import org.jivesoftware.wildfire.privacy.PrivacyList;
import org.jivesoftware.wildfire.privacy.PrivacyListManager;
import org.jivesoftware.wildfire.user.UserManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

/**
 * Controls what is done with offline messages.
 *
 * @author Iain Shigeoka
 */
public class OfflineMessageStrategy extends BasicModule {

    private static int quota = 100*1024; // Default to 100 K.
    private static Type type = Type.drop;

    private OfflineMessageStore messageStore;
    private PacketRouter router;

    public OfflineMessageStrategy() {
        super("Offline Message Strategy");
    }

    public int getQuota() {
        return quota;
    }

    public OfflineMessageStrategy.Type getType() {
        return type;
    }

    public void storeOffline(Message message) {
        if (message != null) {
            // Do nothing if the message was sent to the server itself or to an anonymous user
            JID recipientJID = message.getTo();
            if (recipientJID == null
                        || XMPPServer.getInstance().getServerNames().contains(recipientJID) 
                        || recipientJID.toBareJID() == null 
                        || !UserManager.getInstance().isRegisteredUser(recipientJID.toBareJID())) {
                return;
            }
            // Do not store messages of type groupchat, error or headline as specified in JEP-160
            if (Message.Type.groupchat == message.getType() ||
                    Message.Type.error == message.getType() ||
                    Message.Type.headline == message.getType()) {
                return;
            }
            // Do not store messages if communication is blocked
            PrivacyList list =
                    PrivacyListManager.getInstance().getDefaultPrivacyList(recipientJID.toBareJID());
            if (list != null && list.shouldBlockPacket(message)) {
                return;
            }

            if (type == Type.bounce) {
                bounce(message);
            }
            else if (type == Type.store) {
                store(message);
            }
            else if (type == Type.store_and_bounce) {
                if (underQuota(message)) {
                    store(message);
                }
                else {
                    bounce(message);
                }
            }
            else if (type == Type.store_and_drop) {
                if (underQuota(message)) {
                    store(message);
                }
            }
        }
    }

    private boolean underQuota(Message message) {
        return quota > messageStore.getSize(message.getTo().toBareJID()) + message.toXML().length();
    }

    private void store(Message message) {
        messageStore.addMessage(message);
    }

    private void bounce(Message message) {
        // Do nothing if the sender was the server itself
        if (message.getFrom() == null) {
            return;
        }
        try {
            // Generate a rejection response to the sender
            Message errorResponse = message.createCopy();
            errorResponse.setError(new PacketError(PacketError.Condition.item_not_found,
                    PacketError.Type.continue_processing));
            errorResponse.setFrom(message.getTo());
            errorResponse.setTo(message.getFrom());
            // Send the response
            router.route(errorResponse);
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    public void initialize(XMPPServer server) {
        super.initialize(server);
        messageStore = server.getOfflineMessageStore();
        router = server.getPacketRouter();

        int quota = IMConfig.XMPP_OFFLINE_QUOTA.getInt();
        if (quota >= 0) {
            OfflineMessageStrategy.quota = quota;
        }
        String type = IMConfig.XMPP_OFFLINE_TYPE.getString();
        if (type != null && type.length() > 0) {
            OfflineMessageStrategy.type = Type.valueOf(type);
        }
    }

    /**
     * Strategy types.
     */
    public enum Type {

        /**
         * All messages are bounced to the sender.
         */
        bounce,

        /**
         * All messages are silently dropped.
         */
        drop,

        /**
         * All messages are stored.
         */
        store,

        /**
         * Messages are stored up to the storage limit, and then bounced.
         */
        store_and_bounce,

        /**
         * Messages are stored up to the storage limit, and then silently dropped.
         */
        store_and_drop
    }
}