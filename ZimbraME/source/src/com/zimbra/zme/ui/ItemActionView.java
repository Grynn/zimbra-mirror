/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import com.zimbra.zme.ZimbraME;

import de.enough.polish.util.Locale;

public class ItemActionView extends View {

	public ItemActionView(ZimbraME midlet) {
		super(midlet);
		/*
		//#style ChoiceGroupPopup
		mActionCG = new ChoiceGroup(null, ChoiceGroup.POPUP);
		//#style ChoiceItemPopup
		mActionCG.append(Locale.get("settings.MoveToFolder"), null);
		//#style ChoiceItemPopup
		mActionCG.append(Locale.get("settings.TagWith"), null);
		//#style ChoiceItemPopup
		mActionCG.append(Locale.get("settings.RunSavedSearch"), null);
		f.append(SHORTCUTS_TAB, mShortcutActionCG);
		*/
	}
	
	public void commandAction(Command c, Displayable d) {}
}
