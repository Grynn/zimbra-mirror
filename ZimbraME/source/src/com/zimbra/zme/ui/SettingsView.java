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

package com.zimbra.zme.ui;

import java.io.IOException;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.Settings;
import com.zimbra.zme.Shortcut;
import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.List;
import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.TabbedFormListener;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

/**
 * TODO Add ability to cancel out of settings (warn if changes made)
 * 
 * @author rossd
 *
 */

public class SettingsView extends View implements ItemStateListener, TabbedFormListener {

	private static final Command OK = new Command(Locale.get("main.Save"), Command.CANCEL, 1);
	private static final Command EDIT = new Command(Locale.get("settings.Edit"), Command.ITEM, 1);
    private static final Command SELECT = new Command(Locale.get("settings.Select"), Command.OK, 1);

	// TabbedForm tab indexes
	private static final int DISPLAY_TAB = 1;
	private static final int GENERAL_TAB = 0;
	//private static final int MAIL_TAB = 2;
	private static final int SHORTCUTS_TAB = 2;
	
	// Choice item indexes for show fragment ticker
	private static final int TICKER_CONV = 0;
	private static final int TICKER_MSG = 1;
	private static final int TICKER_APPT = 2;
	
	// Choice item indexes for fragment ticker speed
	private static final int TICKER_SLOW = 0;
	private static final int TICKER_MED = 1;
	private static final int TICKER_FAST = 2;
	
	private static final int MAX_FIELD_LEN = 255;
	
	private Settings mSettings;
	
	// General Tab Elements
	private ChoiceGroup mKeepSignedInCG;
	private ChoiceGroup mCacheContactsCG;
	private ChoiceGroup mPreloadContactsCG;
	private ChoiceGroup mDelWOConfCG;
	private ChoiceGroup mDelWOConfSubCG;
	private ChoiceItem mDWOCConvCI;
	private ChoiceItem mDWOCMsgCI;
	
	// Display Tab Elements
	private ChoiceGroup mTickerCG;
	private ChoiceGroup mTickerSpeedCG;
	
	// Shortcut Tab Elements
	private de.enough.polish.ui.ChoiceGroup mShortcutActionCG;
    
    private List mShortcutEditScreen;
	
	public SettingsView(ZimbraME midlet,
						Settings settings) {
		super(midlet);
		
		mSettings = settings;
		
		//#style SettingsView
		TabbedForm f = new TabbedForm(Locale.get("main.Settings"), new String[]
									  {Locale.get("settings.General"),
									   Locale.get("settings.Display"), 
									   //Locale.get("settings.Mail"),
									   Locale.get("settings.Shortcuts")}, 
									   null);
	
		//#if true
			//# mView = f;
		//#endif
		
		f.setItemStateListener(this);
		f.setTabbedFormListener(this);

		createGeneralTab();
		createDisplayTab();
		createShortcutsTab();
		
		f.addCommand(OK);
		f.setCommandListener(this);
		f.setItemStateListener(this);
	}
	
	public void setCurrent() {
		//#if true
			//# initTabContent(((TabbedForm)mView).getSelectedTab());
		//#endif
		mMidlet.mDisplay.setCurrent(mView);
	}
	
    public void commandAction(Command cmd, 
			  				  Displayable d) {
        if (d == Dialogs.mErrorD) {
            mMidlet.setTopViewCurrent();
        } else if (d == mShortcutEditScreen) {
            if (cmd == SELECT) {
                // save the new shortcut
            }
            setCurrent();
        } else if (d == mView && cmd == EDIT) {
            gotoShortcutEditScreen();
        } else {
            try {
                mSettings.flush();
                mMidlet.setTopViewCurrent();
            } catch (IOException e) {
                Dialogs.popupErrorDialog(mMidlet, this, Locale.get("error.FailedToSaveSettings"));
            }
        }
	}
	
    private void gotoShortcutEditScreen() {
        String title = Locale.get("settings.ConfigureShortcut") + " " + mShortcutActionCG.getSelectedIndex();
        //#style SettingsView
        mShortcutEditScreen = new List(title, javax.microedition.lcdui.Choice.EXCLUSIVE);
        mShortcutEditScreen.append(Locale.get("settings.MoveToFolder"), null);
        mShortcutEditScreen.append(Locale.get("settings.TagWith"), null);
        mShortcutEditScreen.addCommand(SELECT);
        mShortcutEditScreen.addCommand(ZimbraME.CANCEL);
        mShortcutEditScreen.setCommandListener(this);

        mMidlet.mDisplay.setCurrent(mShortcutEditScreen);
    }
    
	public void notifyTabChangeCompleted(int oldTabIdx, 
							 			 int newTabIdx) {
		initTabContent(newTabIdx);
	}
	
	public boolean notifyTabChangeRequested(int oldTabIdx, 
											int newTabIdx) {
		return true;
	}
	
	public void itemStateChanged(Item item) {
		TabbedForm f = null;
		//#if true
			//# f = (TabbedForm)mView;
		//#endif
		switch(f.getSelectedTab()) {
			case GENERAL_TAB:
				itemStateChangedGeneralTab(item);
				break;
			case DISPLAY_TAB:
				itemStateChangedDisplayTab(item);
				break;
			case SHORTCUTS_TAB:
				itemStateChangedShortcutsTab(item);
				break;
		}
	}

	private void itemStateChangedGeneralTab(Item item) {
		ChoiceGroup cg = (ChoiceGroup)item;
		if (cg == mKeepSignedInCG) {
			mSettings.setKeepSignedIn(cg.isSelected(0));
		} else if (cg == mCacheContactsCG) {
			mSettings.setCacheContacts(cg.isSelected(0));
		} else if (cg == mPreloadContactsCG) {
			mSettings.setPreloadContacts(cg.isSelected(0));
		} else if (cg == mDelWOConfCG) {
			setDelWOConfCG(cg.isSelected(0));
		} else if (cg == mDelWOConfSubCG) {
			mSettings.setDelWOCConv(mDWOCConvCI.isSelected);
			mSettings.setDelWOCMsg(mDWOCMsgCI.isSelected);
		}
	}
	
	private void itemStateChangedDisplayTab(Item item) {
		ChoiceGroup cg = (ChoiceGroup)item;
		if (cg == mTickerCG) {
			mSettings.setShowConvTicker(cg.isSelected(TICKER_CONV));
			mSettings.setShowMsgTicker(cg.isSelected(TICKER_MSG));
			mSettings.setShowApptTicker(cg.isSelected(TICKER_APPT));
		} else if (cg == mTickerSpeedCG) {
			switch (mTickerSpeedCG.getSelectedIndex()) {
				case TICKER_SLOW:
					mSettings.setTickerSpeed(Settings.SLOW_TICKER);
					break;
				case TICKER_MED:
					mSettings.setTickerSpeed(Settings.MED_TICKER);
					break;
				case TICKER_FAST:
					mSettings.setTickerSpeed(Settings.FAST_TICKER);
					break;
			}
		}
	}
	
	private void itemStateChangedShortcutsTab(Item item) {
		ChoiceGroup cg = (ChoiceGroup)item;
	}
	
	private void initTabContent(int tabIdx) {
		switch(tabIdx) {
			case GENERAL_TAB:
				initGeneralTab();
				showEditCommand(false);
				break;
			case DISPLAY_TAB:
				initDisplayTab();
				showEditCommand(false);
				break;
			case SHORTCUTS_TAB:
				initShortcutsTab();
				showEditCommand(true);
				break;
		}
	}
	
	private void initGeneralTab() {
		mKeepSignedInCG.setSelectedIndex(0, mSettings.getKeepSignedIn());
		mCacheContactsCG.setSelectedIndex(0, mSettings.getCacheContacts());		
		mPreloadContactsCG.setSelectedIndex(0, mSettings.getPreloadContacts());
		
		setDelWOConfCG(mSettings.getDelWOConf());
		mDWOCConvCI.select(mSettings.getDelWOCConv());
		mDWOCMsgCI.select(mSettings.getDelWOCMsg());
	}

	private void initDisplayTab() {
		mTickerCG.setSelectedIndex(TICKER_CONV, mSettings.getShowConvTicker());
		mTickerCG.setSelectedIndex(TICKER_MSG, mSettings.getShowMsgTicker());
		mTickerCG.setSelectedIndex(TICKER_APPT, mSettings.getShowApptTicker());
		
		switch (mSettings.getTickerSpeed()) {
			case Settings.SLOW_TICKER:
				mTickerSpeedCG.setSelectedIndex(TICKER_SLOW, true);
				break;
			case Settings.MED_TICKER:
				mTickerSpeedCG.setSelectedIndex(TICKER_MED, true);
				break;
			case Settings.FAST_TICKER:
				mTickerSpeedCG.setSelectedIndex(TICKER_FAST, true);
				break;
		}
	}
	
	private void initShortcutsTab() {
	}

	private void showEditCommand(boolean show) {
		TabbedForm f = null;
		//#if true
			//# f = (TabbedForm)mView;
		//#endif

		if (f != null) {
			if (show)
				f.addCommand(EDIT);
			else
				f.removeCommand(EDIT);
		}
	}

	private void createGeneralTab() {
		TabbedForm f = null;
		//#if true
			//# f = (TabbedForm)mView;
		//#endif
		
		//#style ChoiceGroup
		mKeepSignedInCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mKeepSignedInCG.append(Locale.get("login.KeepSignedIn"), null);
		f.append(GENERAL_TAB, mKeepSignedInCG);

		//#style ChoiceGroup
		mCacheContactsCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mCacheContactsCG.append(Locale.get("settings.CacheContacts"), null);
		/* TODO Add this in later
			f.append(GENERAL_TAB, mCacheContactsCG);
		*/
		
		//========== Preload Contacts
		
		//#style ChoiceGroup
		mPreloadContactsCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mPreloadContactsCG.append(Locale.get("settings.PreloadContacts"), null);
		f.append(GENERAL_TAB, mPreloadContactsCG);
		
		//========== Delete w/o confirmation
		
		boolean delWOC = mSettings.getDelWOConf();
		//#style ChoiceGroupOffset
		mDelWOConfCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mDelWOConfCG.append(Locale.get("settings.DelWOConf"), null);
		mDelWOConfCG.setSelectedIndex(0, delWOC);
		f.append(GENERAL_TAB, mDelWOConfCG);
		
		//#style ChoiceGroupIndented
		mDelWOConfSubCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mDWOCConvCI = new ChoiceItem(Locale.get("settings.Conversations"), null, Choice.MULTIPLE);
		//#style ChoiceItem
		mDWOCMsgCI = new ChoiceItem(Locale.get("settings.Messages"), null, Choice.MULTIPLE);
		f.append(GENERAL_TAB, mDelWOConfSubCG);
		
		//TODO remove when Polish fixes bug that causes layout/highlighting weirdness if the below is removed
		//#style Spacer
		StringItem s = new StringItem(null, "");
		f.append(GENERAL_TAB, s);
	}

	private void createDisplayTab() {
		TabbedForm f = null;
		//#if true
			//# f = (TabbedForm)mView;
		//#endif
		
		//#style SpanningLabel
		StringItem s = new StringItem(null, Locale.get("settings.ShowTickersFor"));
		f.append(DISPLAY_TAB, s);
		
		//#style ChoiceGroupIndented
		mTickerCG = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mTickerCG.append(Locale.get("settings.Conversations"), null);
		//#style ChoiceItem
		mTickerCG.append(Locale.get("settings.Messages"), null);
		//#style ChoiceItem
		mTickerCG.append(Locale.get("settings.Appointments"), null);
		f.append(DISPLAY_TAB, mTickerCG);

		//========== Ticker Speed

		//#style SpanningLabel
		s = new StringItem(null, Locale.get("settings.TickerSpeed"));
		f.append(DISPLAY_TAB, s);
		
		//#style ChoiceGroupIndented
		mTickerSpeedCG = new ChoiceGroup(null, ChoiceGroup.EXCLUSIVE);
		//#style ChoiceItem
		mTickerSpeedCG.append(Locale.get("settings.Slow"), null);
		//#style ChoiceItem
		mTickerSpeedCG.append(Locale.get("settings.Medium"), null);
		//#style ChoiceItem
		mTickerSpeedCG.append(Locale.get("settings.Fast"), null);
		f.append(DISPLAY_TAB, mTickerSpeedCG);
		
		
		//TODO remove when Polish fixes bug that causes layout/highlighting weirdness if the below is removed
		//#style Spacer
		s = new StringItem(null, "");
		f.append(DISPLAY_TAB, s);
	}
	
	private void createShortcutsTab() {
		TabbedForm f = null;
		//#if true
			//# f = (TabbedForm)mView;
		//#endif
		
		//#style ChoiceGroup
		mShortcutActionCG = new de.enough.polish.ui.ChoiceGroup("", ChoiceGroup.MULTIPLE);
		
		for (int i = 0; i < 10; i++) {
			Shortcut s = mSettings.getShortcut(i);
			//#style ChoiceItem
			mShortcutActionCG.append(s.toString(), null);
		}
		f.append(SHORTCUTS_TAB, mShortcutActionCG);
	}

	private void setDelWOConfCG(boolean selected) {
		// TODO Hack around J2ME Polish bug that cause the items to not get focus when
		// re-enabled
		mDelWOConfSubCG.deleteAll();
		
		if (selected) {
			//#style ChoiceItem
			UiAccess.setAccessible(mDWOCConvCI, true);
			//#style ChoiceItem
			UiAccess.setAccessible(mDWOCMsgCI, true);					
		} else {
			//#style ChoiceItemDisabled
			UiAccess.setAccessible(mDWOCConvCI, false);
			//#style ChoiceItemDisabled
			UiAccess.setAccessible(mDWOCMsgCI, false);
		}
		
		//TODO Continue of hack above
		//#if true
			//# mDelWOConfSubCG.append(mDWOCConvCI);
			//# mDelWOConfSubCG.append(mDWOCMsgCI);
		//#endif
		
		mSettings.setDelWOConf(selected);
	}
	
}
