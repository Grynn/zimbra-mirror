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

import java.util.Vector;

public class Folder extends MailboxItem {
	public Vector mSubfolders;
	public Folder mParent;
    public String mView;
    
    public Folder() {
        mItemType = FOLDER;
        mSubfolders = new Vector();
    }
    public boolean showThisFolder() {
        return mView != null && 
            (mView.compareTo(ZClientMobile.MSG_TYPE) == 0 ||
             mView.compareTo(ZClientMobile.APPT_TYPE) == 0);
    }
    public StringBuffer getPath() {
        StringBuffer buf;
        if (mParent != null)
            buf = mParent.getPath();
        else
            buf = new StringBuffer();
        if (mName != null)
            buf.append("/").append(mName);
        return buf;
    }
    public boolean hasChildren() {
        return mSubfolders.size() > 0;
    }
    public boolean hasParent() {
        return mParent != null && mParent.mParent != null;
    }
}
