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
package org.jivesoftware.wildfire.vcard;

/**
 * Interface to listen for vCard changes. Use the
 * {@link org.jivesoftware.wildfire.vcard.VCardManager#addListener(VCardListener)}
 * method to register for events.
 *
 * @author Remko Tron&ccedil;on
 */
public interface VCardListener {
    /**
     * A vCard was created.
     *
     * @param user the user for which the vCard was created.
     */
    public void vCardCreated(String user);

    /**
     * A vCard was updated.
     *
     * @param user the user for which the vCard was updated.
     */
    public void vCardUpdated(String user);

    /**
     * A vCard was deleted.
     *
     * @param user the user for which the vCard was deleted.
     */
    public void vCardDeleted(String user);
}
