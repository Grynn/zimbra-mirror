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

import org.jivesoftware.wildfire.group.Group;

import java.util.Map;

/**
 * Interface to listen for group events. Use the
 * {@link GroupEventDispatcher#addListener(GroupEventListener)}
 * method to register for events.
 *
 * @author Matt Tucker
 */
public interface GroupEventListener {

    /**
     * A group was created.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void groupCreated(Group group, Map params);

    /**
     * A group is being deleted.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void groupDeleting(Group group, Map params);

    /**
     * A group's name, description, or an extended property was changed.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void groupModified(Group group, Map params);

    /**
     * A member was added to a group.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void memberAdded(Group group, Map params);

    /**
     * A member was removed from a group.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void memberRemoved(Group group, Map params);

    /**
     * An administrator was added to a group.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void adminAdded(Group group, Map params);

    /**
     * An administrator was removed from a group.
     *
     * @param group the group.
     * @param params event parameters.
     */
    public void adminRemoved(Group group, Map params);
}