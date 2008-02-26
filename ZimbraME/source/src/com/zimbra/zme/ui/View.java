/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.ui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Ticker;

import com.zimbra.zme.Settings;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Appointment;
import com.zimbra.zme.client.ItemFactory;

import de.enough.polish.ui.TreeItem;
import de.enough.polish.util.Locale;

public abstract class View implements CommandListener, ItemFactory {
    protected static class AcceptDeclineCommand extends Command {
        private String mStatus;
        private int mStatusVal;
        public AcceptDeclineCommand(String label, int type, int priority, String status, int statusVal) {
            super(label, type, priority);
            mStatus = status;
            mStatusVal = statusVal;
        }
        public String getStatus() {
            return mStatus;
        }
        public int getStatusVal() {
            return mStatusVal;
        }
    }
	protected static final Command ACTIONS = new Command(Locale.get("main.Actions"), Command.ITEM, 1);
	protected static final Command DELETE = new Command(Locale.get("main.Delete"), Command.ITEM, 1);
	protected static final Command COMPOSE = new Command(Locale.get("main.Compose"), Command.ITEM, 1);
	
	protected static final Command BACK = new Command(Locale.get("main.Back"), Command.BACK, 1);
	protected static final Command CANCEL = new Command(Locale.get("main.Cancel"), Command.CANCEL, 1);
	
    protected static final Command ACCEPT = new AcceptDeclineCommand(Locale.get("calendar.Accept"), Command.ITEM, 1, "ACCEPT", Appointment.ACCEPTED);
    protected static final Command DECLINE = new AcceptDeclineCommand(Locale.get("calendar.Decline"), Command.ITEM, 1, "DECLINE", Appointment.DECLINED);
    protected static final Command TENTATIVE = new AcceptDeclineCommand(Locale.get("calendar.Tentative"), Command.ITEM, 1, "TENTATIVE", Appointment.TENTATIVE);
    
	//#style SlowTicker
	protected static final Ticker SLOW_TICKER = new Ticker("");
	//#style MedTicker
	protected static final Ticker MED_TICKER = new Ticker("");
	//#style FastTicker
	protected static final Ticker FAST_TICKER = new Ticker("");

	protected static Vector mTags;
	
	protected Form mView;
	protected ZimbraME mMidlet;
	protected Object mNext;
	protected Ticker mTicker;
	protected boolean mShowTicker;
	protected int mTickerSpeed;
	protected ZmeStringItem mNoDataItem;

	protected View(ZimbraME midlet) {
		mMidlet = midlet;
	}
	
	public Displayable getDisplayable() {
		return mView;
	}
	
	public void setCurrent() {
		mMidlet.mDisplay.setCurrent(mView);
	}
	
	public void setNext(View nextView) {
		mNext = nextView;
	}
	
	public void setNext(Item nextItem) {
		mNext = nextItem;
	}
	
	public void setNext(Displayable nextDisplayable) {
		mNext = nextDisplayable;
	}
		
	/**
	 * Subclasses should implement this method to make any network call that they need to make. 
	 * Note this method is explicity called by LoginView when a session has expired. The LoginView will
	 * reauthenticate the user then call this method.
	 */
	public void load() {}
	
	/* The methods below are default implementation for ItemFactory methods. Basically ZClientMobile is tightly
	 * coupled to the UI classes. This is because we are collapsing (as much as possible) the model and view
	 * classes in order to keep the number of classes and runtime instances down. This leads to some 
	 * nastiness in ZClientMobile needing to be able to instantiate UI elements classes that are representing
	 * the model classes. Gosh this is ugly blech blech blech
	 */
	public ConvItem createConvItem() {return null;}
	public MsgItem createMsgItem() {return null;}
	public TreeItem createFolderItem() {return null;}

	public void commandAction(Command cmd, 
			  				  Displayable d,
			  				  boolean propogateToMidlet) {
		if (d == mView) {
			if (cmd == DELETE) {
				if (confirmDeletes())
					Dialogs.popupConfirmDialog(mMidlet, this, Locale.get("main.DeleteConfirm"));
				else 
					deleteItemConfirmed();
			} else { 
				// Delegate the command handling up to the midlet
				mMidlet.commandAction(cmd, d);
			}
		} else if (d == Dialogs.mConfirmD) {
			if (cmd == Dialogs.YES) {
				setCurrent();
				deleteItemConfirmed();
			}
		} else if (propogateToMidlet){
			// Delegate the command handling up to the midlet
			mMidlet.commandAction(cmd, d);			
		}
	}
	
	/**
	 * Subclasses may override this method to indicate if deletes should require a confirm
	 * dialog
	 */
	protected boolean confirmDeletes() {
		return true;
	}
	
	/**
	 * Subclasses should override this method to delete the selected item if they 
	 * support item deletion
	 */
	protected void deleteItemConfirmed() {
	}
	
	/**
	 * Subclasses may override this method to delete the item.
	 * @param itemToDelete
	 */
	protected void deleteItem(Item itemToDelete) {
		int sz = mView.size();
		for (int i = 0; i < sz; i++) {
			Item item;
			item = mView.get(i);
			if (item == itemToDelete) {
				mView.delete(i);
				break;
			}
		}
		if (sz == 1) {
			mView.append(mNoDataItem);
		}
	}

	protected void keyPressed(int keyCode,
						   	  int gameAction,
						   	  Item item) {
        //#debug
        System.out.println("key pressed: "+keyCode+" gameAction: "+gameAction);
		if (keyCode == Canvas.KEY_NUM7) {
			if (confirmDeletes())
				Dialogs.popupConfirmDialog(mMidlet, this, Locale.get("main.DeleteConfirm"));
			else 
				deleteItemConfirmed();
		} else {
			mMidlet.keyPressed(keyCode, mView);
		}
	}
	
	protected void showTicker(boolean show) {
		if (show) {
			switch (mMidlet.mSettings.getTickerSpeed()) {
				case Settings.SLOW_TICKER:
					mTicker = SLOW_TICKER;
					break;
				case Settings.MED_TICKER:
					mTicker = MED_TICKER;
					break;
				case Settings.FAST_TICKER:
					mTicker = FAST_TICKER;
					break;
			}
			mView.setTicker(mTicker);
		} else {
			mView.setTicker(null);
		}
		mShowTicker = show;
	}
	
	protected void setNextCurrent() {
		if (mNext != null) {
			if (mNext instanceof View)
				mMidlet.mDisplay.setCurrent(((View)mNext).mView);
			else if (mNext instanceof Displayable)
				mMidlet.mDisplay.setCurrent((Displayable)mNext);
			else if (mNext instanceof Item)
				mMidlet.mDisplay.setCurrentItem(((Item)mNext));
		}
	}
	
}
