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
package com.zimbra.zme;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.Locale;

public class Shortcut implements Serializable {
	public static final int ACTION_MOVE_TO_FOLDER = 1;
	public static final int ACTION_TAG = 2;
    public static final int ACTION_RUN_SAVED_SEARCH = 3;
	
	public int button;   // shortcut button
	public int action;   // move to folder, tag, etc
	public String destId;     // folder id or tag id
	public String dest;
	
	private Shortcut() {
	}
	
	public Shortcut(int button) {
		this.button = button;
	}
	
    public boolean isConfigured() {
        return action != 0;
    }
    
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("#").append(button).append(" ");
		if (action == 0)
			return buf.append(Locale.get("settings.NotConfigured")).toString();
		else if (action == ACTION_MOVE_TO_FOLDER)
			buf.append(Locale.get("settings.MoveToFolder"));
		else
			buf.append(Locale.get("settings.TagWith"));
		buf.append(" ");
		buf.append(dest);
		return buf.toString();
	}
}
