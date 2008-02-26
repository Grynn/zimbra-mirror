/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
