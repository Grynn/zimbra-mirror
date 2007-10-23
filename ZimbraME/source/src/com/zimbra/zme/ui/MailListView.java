/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.MailboxItem;
import com.zimbra.zme.client.ResultSet;

//#if true
//# import de.enough.polish.ui.FramedForm;
//#endif
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public abstract class MailListView extends View implements ResponseHdlr, ZmeListener {
	
	// Constants indicating item state change.
	public static final int FLAG_CHANGED = 1;
	public static final int UNREAD_CHANGED = 2;
	public static final int MSG_LOADED = 3;
	public static final int DELETED = 4;
    public static final int INVITE_REPLIED = 5;
	
    protected static int INITIAL_RESULT_SIZE = 5;
	protected static int DEF_RESULT_SIZE = 15;

	protected static final Command TAG = new Command(Locale.get("mailList.Tag"), Command.ITEM, 1);
	protected static final Command JUNK = new Command(Locale.get("mailList.Junk"), Command.ITEM, 1);

	protected Command mToggleUnreadCmd;
	protected Command mToggleFlagCmd;
	protected StringItem mHeader;
	protected boolean mMoreHits;
	protected ResultSet mResults;
	protected boolean mFragmentShowing;
	
	//#ifdef polish.usePolishGui
		public MailListView(String title,
							ZimbraME midlet,
							Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(title, style);
			//#endif
			init(title);		
		}
	//#else
		public MailListView(String title,
							ZimbraME midlet) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(title);
			//#endif
			init(title);		
		}
	//#endif
	
	public void commandAction(Command cmd, 
			  				  Displayable d) {
		if (d == mView) {
			if (cmd == COMPOSE)
				mMidlet.gotoComposeView(d);
			else if (cmd == mToggleFlagCmd)
				toggleFlag();
			else if (cmd == mToggleUnreadCmd)
				toggleUnread();
			else if (cmd == TAG) {
				MailItem m = null;
				//#ifdef polish.usePolishGui
					//# m = (MailItem)((FramedForm)mView).getCurrentItem();
				//#endif				
				CollectionView cv = mMidlet.gotoTagView(mView, CollectionView.TAG_PICKER, m.mTags);
				cv.setListener(this);
			}
		}
		super.commandAction(cmd, d, true);
	}
	
	public void action(Object source,
				       Object data) {
		if (source instanceof CollectionView) {
			CollectionView cv = (CollectionView)source;
			if (cv.getType() == CollectionView.TAG_PICKER) {
				MailItem m = null;
				//#ifdef polish.usePolishGui
					//# m = (MailItem)((FramedForm)mView).getCurrentItem();
				//#endif
				if (m != null) {
                    MailboxItem[] tags = (MailboxItem[]) data;
                    int count = tags.length;
                    String[] tagIds = new String[count];
                    for (int i = 0; i < count; i++)
                        tagIds[i] = tags[i].mId;
					m.setTags(tagIds); 
				}
			}
		}
	}
	
	protected void deleteItemConfirmed() {
		MailItem m = null;
		//#ifdef polish.usePolishGui
			//# m = (MailItem)((FramedForm)mView).getCurrentItem();
		//#endif
		if (m != null)
			m.deleteItem();		
	}

	protected void itemStateChanged(MailItem item,
         							int what) {
	}
	
	protected void keyPressed(int keyCode,
						   	  int gameAction,
						   	  Item item) {
    	switch (keyCode) {
			case Canvas.KEY_STAR:
				toggleFlag();
				break;
			case Canvas.KEY_NUM2:
				mMidlet.gotoComposeView(mView);
				break;
			case Canvas.KEY_NUM3:
				toggleUnread();
				break;
			default:
				super.keyPressed(keyCode, gameAction, item);
    	}
	}

	public Displayable getDisplayable() {
		return mView;
	}
	
	/* This method is called by MailItem subclasses when they are requesting more data 
	 * be added to the list*/
	public void getMore(MailItem lastItem) {
		if (mMoreHits) {
			//#debug
			System.out.println("GETTING MORE DATA");
		}
	}
	
	private void init(String title) {
        int height = mView.getHeight();
        INITIAL_RESULT_SIZE = (height - MailItem.MINIMUM_CONTENT_HEIGHT)
                        / MailItem.MINIMUM_CONTENT_HEIGHT + 2;
        
		mView.setCommandListener(this);
		//#style NoResultItem
		mNoDataItem = new ZmeStringItem(mMidlet, this, "");
		
		// Setup result set object
		mResults = new ResultSet();
		mResults.mItemFactory = this;
		
		mToggleUnreadCmd = new Command(Locale.get("mailList.MarkRead"), Command.ITEM, 1);
		mToggleFlagCmd = new Command(Locale.get("mailList.Flag"), Command.ITEM, 1);
	}

	private void toggleFlag() {
		// Get the selected item. If it is not null, then toggle the flag on it
		MailItem m = null;
		//#ifdef polish.usePolishGui
			//# m = (MailItem)((FramedForm)mView).getCurrentItem();
		//#endif
		if (m != null)
			m.toggleFlagged(true);		
	}
	
	private void toggleUnread() {
		// Get the selected item. If it is not null, then toggle the flag on it
		MailItem m = null;
		//#ifdef polish.usePolishGui
			//# m = (MailItem)((FramedForm)mView).getCurrentItem();
		//#endif
		if (m != null)
			m.toggleUnread(true);		
	}
	
    
    
    protected void itemHasFocus(MailItem item) {
        if (mShowTicker)
            mTicker.setString((item.mFragment != null) ? item.mFragment : "");
        
        if ((item.mFlags & MailItem.FLAGGED) == MailItem.FLAGGED)
            UiAccess.setCommandLabel(mView, mToggleFlagCmd, Locale.get("mailList.Unflag"));
        else 
            UiAccess.setCommandLabel(mView, mToggleFlagCmd, Locale.get("mailList.Flag"));

        if ((item.mFlags & MailItem.UNREAD) == MailItem.UNREAD)
            UiAccess.setCommandLabel(mView, mToggleUnreadCmd, Locale.get("mailList.MarkRead"));
        else 
            UiAccess.setCommandLabel(mView, mToggleUnreadCmd, Locale.get("mailList.MarkUnread"));
    }
}
