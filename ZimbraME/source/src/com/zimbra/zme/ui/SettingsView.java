/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.io.IOException;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.Settings;
import com.zimbra.zme.Shortcut;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Folder;
import com.zimbra.zme.client.MailboxItem;
import com.zimbra.zme.client.SavedSearch;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.List;
import de.enough.polish.ui.Style;
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

public class SettingsView extends View implements ItemCommandListener, ItemStateListener, TabbedFormListener {

	private static final Command SAVE = new Command(Locale.get("main.Save"), Command.CANCEL, 1);
    private static final Command OK = new Command(Locale.get("main.Ok"), Command.OK, 1);
    private static final Command DELETE = new Command(Locale.get("main.Delete"), Command.OK, 1);

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
	
    // Choice item indexes for shortcut action
    private static final int SHORTCUT_FOLDER = 0;
    private static final int SHORTCUT_TAG = 1;
    private static final int SHORTCUT_SEARCH = 2;
    
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
	private de.enough.polish.ui.ListItem mShortcutList;
    private de.enough.polish.ui.ListItem mShortcutEditScreen;
    private ChoiceGroup mShortcutActionCG;
    private ChoiceGroup mShortcutButtonCG;
    private ShortcutItem mSelectedShortcut;
	
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
		
		f.addCommand(SAVE);
		f.setCommandListener(this);
		f.setItemStateListener(this);
	}
	
	public void setCurrent() {
		//#if true
			//# initTabContent(((TabbedForm)mView).getSelectedTab());
		//#endif
		mMidlet.mDisplay.setCurrent(mView);
	}

    public void commandAction(Command cmd, de.enough.polish.ui.Item item) {
        item = this.mShortcutList.getFocusedItem();
        
        //#debug
        System.out.println("cmd: "+cmd.getLabel()+", type: "+cmd.getCommandType()+", item: "+item);
        if (item instanceof ShortcutItem) {
            ShortcutItem shortcut = (ShortcutItem) item;
            createShortcutEditTab(shortcut);
        }
    }
    
    public void commandAction(Command cmd, 
			  				  Displayable d) {
        if (d == Dialogs.mErrorD) {
            mMidlet.setTopViewCurrent();
        } else if (cmd == DELETE) {
            ShortcutItem item = (ShortcutItem)mShortcutList.getFocusedItem();
            item.shortcut.action = 0;
            createShortcutsTab();
        } else if (cmd == OK || cmd == CANCEL) {
            if (cmd == OK) {
                // save the current shortcut
                int button = mSelectedShortcut.shortcut.button;
                //#debug
                System.out.println("saving shortcut "+button);
                Shortcut s = mSettings.getShortcut(button);
                s.copy(mSelectedShortcut.shortcut);
            }
            
            createShortcutsTab();
        } else {
            try {
                mSettings.flush();
                mMidlet.setTopViewCurrent();
            } catch (IOException e) {
                Dialogs.popupErrorDialog(mMidlet, this, Locale.get("error.FailedToSaveSettings"));
            }
        }
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
        if (item == mShortcutButtonCG) {
            //#debug
            System.out.println("button: "+mShortcutButtonCG.getSelectedIndex());
            mSelectedShortcut.shortcut.button = mShortcutButtonCG.getSelectedIndex();
            return;
        }
        
        CollectionView v;
        switch (mShortcutActionCG.getSelectedIndex()) {
        case SHORTCUT_FOLDER:
        default:
            v = mMidlet.gotoFolderPickerView(mView);
            break;
        case SHORTCUT_TAG:
            v = mMidlet.gotoTagView(mView, CollectionView.TAG_PICKER, mSelectedShortcut.shortcut.destId);
            break;
        case SHORTCUT_SEARCH:
            v = mMidlet.gotoSavedSearchPickerView(mView);
            break;
        }
        v.setListener(new PickerListener(this, mSelectedShortcut));
	}
	
	private void initTabContent(int tabIdx) {
        TabbedForm f = null;
        //#if true
            //# f = (TabbedForm)mView;
        //#endif
    
		switch(tabIdx) {
			case GENERAL_TAB:
				initGeneralTab();
                f.removeCommand(DELETE);
				break;
			case DISPLAY_TAB:
				initDisplayTab();
                f.removeCommand(DELETE);
				break;
			case SHORTCUTS_TAB:
				initShortcutsTab();
                f.addCommand(DELETE);
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
		mShortcutList = new de.enough.polish.ui.ListItem(null);
        Shortcut[] shortcuts = mSettings.getShortcuts();
        Shortcut firstUnused = null;
        for (int i = 0; i < shortcuts.length; i++) {
            if (!shortcuts[i].isConfigured()) {
                firstUnused = shortcuts[i];
                break;
            }
        }

        ChoiceItem ci = null;
        if (firstUnused != null) {
            //#style ChoiceItem
            ci = new ShortcutItem(Locale.get("settings.NewShortcut"), null, List.IMPLICIT, firstUnused);
            mShortcutList.append(ci);
        }
        for (int i = 0; i < shortcuts.length; i++) {
            if (!shortcuts[i].isConfigured())
                continue;
            //#style ChoiceItem
            ci = new ShortcutItem(shortcuts[i].toString(), null, List.IMPLICIT, shortcuts[i]);
            mShortcutList.append(ci);
        }

        f.deleteAll(SHORTCUTS_TAB);
        f.removeCommand(OK);
        f.removeCommand(CANCEL);
        f.addCommand(SAVE);
        f.addCommand(DELETE);
        f.append(SHORTCUTS_TAB, mShortcutList);
        mShortcutList.setItemCommandListener(this);
        mShortcutList.setDefaultCommand(List.SELECT_COMMAND);
	}

    private void createShortcutEditTab(ShortcutItem si) {
        TabbedForm f = null;
        //#if true
            //# f = (TabbedForm)mView;
        //#endif
        
        mSelectedShortcut = new ShortcutItem(si);
        int selectedIndex;
        switch (mSelectedShortcut.shortcut.action) {
        case Shortcut.ACTION_MOVE_TO_FOLDER:
        default:
            selectedIndex = SHORTCUT_FOLDER;
            break;
        case Shortcut.ACTION_TAG:
            selectedIndex = SHORTCUT_TAG;
            break;
        case Shortcut.ACTION_RUN_SAVED_SEARCH:
            selectedIndex = SHORTCUT_SEARCH;
            break;
        }

        mShortcutEditScreen = new de.enough.polish.ui.ListItem(Locale.get("settings.EditShortcut"));
        
        //#style SpanningLabel
        Item item = new StringItem(null, Locale.get("settings.Button"));
        mShortcutEditScreen.append(item);
        
        //#style ChoiceGroupPopup
        mShortcutButtonCG = new ChoiceGroup(null, Choice.POPUP);
        for (int i = 0; i < 10; i++) {
            //#style ChoiceItemPopup
            mShortcutButtonCG.append("# "+i, null);
        }

        mShortcutButtonCG.setSelectedIndex(mSelectedShortcut.shortcut.button, true);
        mShortcutEditScreen.append(mShortcutButtonCG);
        
        //#style SpanningLabel
        item = new StringItem(null, Locale.get("settings.Action"));
        mShortcutEditScreen.append(item);
        
        //#style ChoiceGroupIndented
        mShortcutActionCG = new ChoiceGroup(null, ChoiceGroup.EXCLUSIVE);
        //#style ChoiceItem
        mShortcutActionCG.append(mSelectedShortcut.shortcut.toString(false, Shortcut.ACTION_MOVE_TO_FOLDER), null);
        //#style ChoiceItem
        mShortcutActionCG.append(mSelectedShortcut.shortcut.toString(false, Shortcut.ACTION_TAG), null);
        //#style ChoiceItem
        mShortcutActionCG.append(mSelectedShortcut.shortcut.toString(false, Shortcut.ACTION_RUN_SAVED_SEARCH), null);
        mShortcutEditScreen.append(mShortcutActionCG);
        
        mShortcutActionCG.setSelectedIndex(selectedIndex, true);
        f.removeCommand(SAVE);
        f.removeCommand(DELETE);
        f.addCommand(OK);
        f.addCommand(CANCEL);
        f.deleteAll(SHORTCUTS_TAB);
        f.append(SHORTCUTS_TAB, mShortcutEditScreen);
    }
    
    static class ShortcutItem extends ChoiceItem {
        Shortcut shortcut;
        ShortcutItem(ShortcutItem copy) {
            super(copy.text, null, 0);
            shortcut = new Shortcut(copy.shortcut);
        }
        ShortcutItem(String text, Image image, int type, Shortcut s) {
            super(text, image, type);
            shortcut = s;
        }
        ShortcutItem(String text, Image image, int type, Shortcut s, Style style) {
            super(text, image, type, style);
            shortcut = s;
        }
    }
    
    static class PickerListener implements ZmeListener {
        SettingsView v;
        ShortcutItem s;
        PickerListener(SettingsView view, ShortcutItem item) {
            v = view;
            s = item;
        }
        public void action(Object obj, Object data) {
            if (data instanceof MailboxItem)
                handleMailboxItemPick((MailboxItem)data);
            else if (data instanceof MailboxItem[])
                handleMultiMailboxItemPick((MailboxItem[])data);
        }
        public void handleMailboxItemPick(MailboxItem mi) {
            if (mi instanceof Folder)
                s.shortcut.action = Shortcut.ACTION_MOVE_TO_FOLDER;
            else if (mi instanceof SavedSearch)
                s.shortcut.action = Shortcut.ACTION_RUN_SAVED_SEARCH;
            s.shortcut.destId = new String[1];
            s.shortcut.destId[0] = mi.mId;
            s.shortcut.dest = new String[1];
            s.shortcut.dest[0] = mi.mName;
            v.createShortcutEditTab(s);
        }
        public void handleMultiMailboxItemPick(MailboxItem[] mi) {
            s.shortcut.dest = new String[mi.length];
            s.shortcut.destId = new String[mi.length];
            for (int i = 0; i < mi.length; i++) {
                s.shortcut.destId[i] = mi[i].mId;
                s.shortcut.dest[i] = mi[i].mName;
            }
            s.shortcut.action = Shortcut.ACTION_TAG;
            v.createShortcutEditTab(s);
        }
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
