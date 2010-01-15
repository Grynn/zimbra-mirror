/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.event;

import org.jivesoftware.wildfire.user.User;

import java.util.Map;

/**
 * Interface to listen for group events. Use the
 * {@link UserEventDispatcher#addListener(UserEventListener)}
 * method to register for events.
 *
 * @author Matt Tucker
 */
public interface UserEventListener {

    /**
     * A user was created.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userCreated(User user, Map params);

    /**
     * A user is being deleted.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userDeleting(User user, Map params);

    /**
     * A user's name, email, or an extended property was changed.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userModified(User user, Map params);
}