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
package org.jivesoftware.wildfire.muc.spi;

import java.util.Date;

import org.jivesoftware.wildfire.muc.MUCRoom;
import org.jivesoftware.wildfire.muc.MultiUserChatServer;
import org.xmpp.packet.Message;
import org.xmpp.packet.JID;

/**
 * Represents an entry in the conversation log of a room. An entry basically obtains the necessary
 * information to log from the message adding a timestamp of when the message was sent to the room.
 * 
 * @author Gaston Dombiak
 */
class ConversationLogEntry {

    private Date date;

    private String subject;

    private String body;

    private JID sender;
    
    private String nickname;
    
    private long roomID;
    
    private MultiUserChatServer server;

    /**
     * Creates a new ConversationLogEntry that registers that a given message was sent to a given
     * room on a given date.
     * 
     * @param date the date when the message was sent to the room.
     * @param room the room that received the message.
     * @param message the message to log as part of the conversation in the room.
     * @param sender the real XMPPAddress of the sender (e.g. john@example.org). 
     */
    public ConversationLogEntry(MultiUserChatServer server, Date date, MUCRoom room, Message message, JID sender) {
        this.server = server;
        this.date = date;
        this.subject = message.getSubject();
        this.body = message.getBody();
        this.sender = sender;
        this.roomID = room.getID();
        this.nickname = message.getFrom().getResource();
    }
    
    /** 
     * Returns service domain of the MUC instance this entry is for
     */
    public String getServiceDomain() {
        return server.getServiceDomain();
    }

    /**
     * Returns the body of the logged message.
     * 
     * @return the body of the logged message.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the XMPP address of the logged message's sender.
     * 
     * @return the XMPP address of the logged message's sender.
     */
    public JID getSender() {
        return sender;
    }

    /**
     * Returns the nickname that the user had at the moment that the message was sent to the room.
     * 
     * @return the nickname that the user had at the moment that the message was sent to the room.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the subject of the logged message.
     * 
     * @return the subject of the logged message.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the date when the logged message was sent to the room.
     * 
     * @return the date when the logged message was sent to the room.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the ID of the room where the message was sent.
     * 
     * @return the ID of the room where the message was sent.
     */
    public long getRoomID() {
        return roomID;
    }

}