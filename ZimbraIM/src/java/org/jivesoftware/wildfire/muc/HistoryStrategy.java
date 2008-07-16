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
package org.jivesoftware.wildfire.muc;

import org.jivesoftware.util.IMConfig;
import org.xmpp.packet.Message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>Multi-User Chat rooms may cache history of the conversations in the room in order to
 * play them back to newly arriving members.</p>
 * 
 * <p>This class is an internal component of MUCRoomHistory that describes the strategy that can 
 * be used, and provides a method of administering the history behavior.</p>
 *
 * @author Gaston Dombiak
 * @author Derek DeMoro
 */
public class HistoryStrategy {

    /**
     * The type of strategy being used.
     */
    private Type type = Type.number;

    /**
     * List containing the history of messages.
     */
    private ConcurrentLinkedQueue<Message> history = new ConcurrentLinkedQueue<Message>();
    /**
     * The maximum number of chat history messages stored for the room.
     */
    private int maxNumber;
    /**
     * The parent history used for default settings, or null if no parent
     * (chat server defaults).
     */
    private HistoryStrategy parent;
    /**
     * Track the latest room subject change or null if none exists yet.
     */
    private Message roomSubject = null;
    
    /**
     * Create a history strategy with the given parent strategy (for defaults) or null if no 
     * parent exists.
     *
     * @param parentStrategy The parent strategy of this strategy or null if none exists.
     */
    public HistoryStrategy(HistoryStrategy parentStrategy) {
        this.parent = parentStrategy;
        if (parent == null) {
            try {
                type = Type.valueOf(IMConfig.XMPP_MUC_HISTORY_TYPE.getString());
            } catch (Exception e) {
                type = Type.number;
            }
            maxNumber = IMConfig.XMPP_MUC_HISTORY_MAXNUMBER.getInt();
        } else {
            type = Type.defaulType;
            maxNumber = parent.getMaxNumber();
        }
    }

    /**
     * Strategy type.
     */
    public enum Type {
        defaulType, none, all, number;
    }

    /**
     * Obtain the maximum number of messages for strategies using message number limitations.
     *
     * @return The maximum number of messages to store in applicable strategies.
     */
    public int getMaxNumber() {
        return maxNumber;
    }

    /**
     * Obtain the type of history strategy being used.
     *
     * @return The current type of strategy being used.
     */
    public Type getType(){
        return type;
    }

    /**
     * Add a message to the current chat history. The strategy type will determine what 
     * actually happens to the message.
     *
     * @param packet The packet to add to the chatroom's history.
     */
    public void addMessage(Message packet){
        // get the conditions based on default or not
        Type strategyType;
        int strategyMaxNumber;
        if (type == Type.defaulType && parent != null) {
            strategyType = parent.getType();
            strategyMaxNumber = parent.getMaxNumber();
        }
        else {
            strategyType = type;
            strategyMaxNumber = maxNumber;
        }

        // Room subject change messages are special
        boolean subjectChange = false;
        if (packet.getSubject() != null && packet.getSubject().length() > 0){
            subjectChange = true;
            roomSubject = packet;
        }

        // store message according to active strategy
        if (strategyType == Type.none){
            if (subjectChange) {
                history.clear();
                history.add(packet);
            }
        }
        else if (strategyType == Type.all) {
            history.add(packet);
        }
        else if (strategyType == Type.number) {
            if (history.size() >= strategyMaxNumber) {
                // We have to remove messages so the new message won't exceed
                // the max history size
                // This is complicated somewhat because we must skip over the
                // last room subject
                // message because we want to preserve the room subject if
                // possible.
                Iterator historyIter = history.iterator();
                while (historyIter.hasNext() && history.size() > strategyMaxNumber) {
                    if (historyIter.next() != roomSubject) {
                        historyIter.remove();
                    }
                }
            }
            history.add(packet);
        }
    }

    boolean isHistoryEnabled() {
        Type strategyType = type;
        if (type == Type.defaulType && parent != null) {
            strategyType = parent.getType();
        }
        return strategyType != HistoryStrategy.Type.none;
    }

    /**
     * Obtain the current history as an iterator of messages to play back to a new room member.
     * 
     * @return An iterator of Message objects to be sent to the new room member.
     */
    public Iterator<Message> getMessageHistory(){
        LinkedList<Message> list = new LinkedList<Message>(history);
        return list.iterator();
    }

    /**
     * Obtain the current history to be iterated in reverse mode. This means that the returned list 
     * iterator will be positioned at the end of the history so senders of this message must 
     * traverse the list in reverse mode.
     * 
     * @return A list iterator of Message objects positioned at the end of the list.
     */
    public ListIterator<Message> getReverseMessageHistory(){
        LinkedList<Message> list = new LinkedList<Message>(history);
        return list.listIterator(list.size());
    }
    
    /**
     * Returns true if there is a message within the history of the room that has changed the
     * room's subject.
     *
     * @return true if there is a message within the history of the room that has changed the
     *         room's subject.
     */
    public boolean hasChangedSubject() {
        return roomSubject != null;
    }
}
