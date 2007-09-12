/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
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
