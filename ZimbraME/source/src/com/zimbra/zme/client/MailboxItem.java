/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite J2ME Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.client;

public abstract class MailboxItem {
    public static final int MESSAGE = 1;
    public static final int FOLDER  = 2;
    public static final int SAVEDSEARCH = 3;
    public static final int TAG = 4;
    public static final int ATTACHMENT = 5;
    public static final int APPOINTMENT = 6;
    
    public int mItemType;
    public String mId;
    public String mName;
    
    public boolean hasChildren() {
        return false;
    }
    public boolean hasParent() {
        return false;
    }
}
