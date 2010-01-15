/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.event;

import org.jivesoftware.wildfire.Session;

/**
 * Interface to listen for session events. Use the
 * {@link SessionEventDispatcher#addListener(SessionEventListener)}
 * method to register for events.
 *
 * @author Matt Tucker
 */
public interface SessionEventListener {

    /**
     * A session was created.
     *
     * @param session the session.
     */
    public void sessionCreated(Session session);    

    /**
     * A session was destroyed
     *
     * @param session the session.
     */
    public void sessionDestroyed(Session session);

    /**
     * An anonymous session was created.
     *
     * @param session the session.
     */
    public void anonymousSessionCreated(Session session);
    
    /**
     * An anonymous session was created.
     *
     * @param session the session.
     */
    public void anonymousSessionDestroyed(Session session);
}