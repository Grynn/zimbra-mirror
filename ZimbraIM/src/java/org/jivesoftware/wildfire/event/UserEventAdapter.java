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

import org.jivesoftware.wildfire.user.User;

import java.util.Map;

/**
 * An abstract adapter class for receiving user events. 
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 */
public class UserEventAdapter implements UserEventListener  {
    public void userCreated(User user, Map params) {
    }

    public void userDeleting(User user, Map params) {
    }

    public void userModified(User user, Map params) {
    }
}
