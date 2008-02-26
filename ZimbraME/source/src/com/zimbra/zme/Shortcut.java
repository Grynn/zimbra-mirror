/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
	public String[] destId;     // folder id or tag id
	public String[] dest;
	
    public Shortcut(Shortcut copy) {
        copy(copy);
    }
    
	public Shortcut(int button) {
		this.button = button;
	}

    public void copy(Shortcut copy) {
        button = copy.button;
        action = copy.action;
        destId = copy.destId;
        dest = copy.dest;
    }
    public boolean isConfigured() {
        return action != 0;
    }
    
	public String toString() {
        return toString(true, action);
    }
    public String toString(boolean full, int action) {
		StringBuffer buf = new StringBuffer();
        if (full)
            buf.append("#").append(button).append(" ");
		if (action == 0)
			return buf.append(Locale.get("settings.NotConfigured")).toString();
		else if (action == ACTION_MOVE_TO_FOLDER)
			buf.append(Locale.get("settings.MoveToFolder"));
        else if (action == ACTION_RUN_SAVED_SEARCH)
            buf.append(Locale.get("settings.RunSavedSearch"));
		else
			buf.append(Locale.get("settings.TagWith"));
        if (action == this.action && dest != null) {
            buf.append(": ");
            for (int i = 0; i < dest.length; i++) {
                if (i > 0)
                    buf.append(",");
                buf.append(dest[i]);
            }
        }
		return buf.toString();
	}
}
