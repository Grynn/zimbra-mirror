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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ConvListView extends MailListView {
	
	public static final int INBOX_VIEW = 1;
	public static final int SEARCH_VIEW = 2;

	protected static final Command SHOW_FRAGMENT = new Command(Locale.get("mailList.Fragment"), Command.ITEM, 1);
	private static final Command GOTO_SEARCHVIEW = new Command(Locale.get("mailList.SearchView"), Command.ITEM, 1);
	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.BACK, 1);
	private static final Command EXIT = new Command(Locale.get("main.Exit"), Command.ITEM, 10);

	private static boolean mInitialLoad = true;
	
	private String mQuery;
	private int mDefResultSize;
	private int mViewType;
	private boolean mGettingMore;
	private String mSavedTitle;
	
	//#ifdef polish.usePolishGui
		public ConvListView(String title,
							ZimbraME midlet,
							int viewType,
							Style style) {
			super(title, midlet, style);
			init(viewType);
		}
	//#else
		public ConvListView(String title,
							ZimbraME midlet,
							int viewType) {
			super(title, midlet);
			init(viewType);
		}
	//#endif
		
	/**
	 * Set the query string. The query string should be a valid ZCS mail query
	 * 
	 * @param queryStr
	 * @param sortBy
	 * @param types
	 */
	public void setQuery(String queryStr,
						 String sortBy,
						 String types) {
		
		//TODO support sortBy and types
		mQuery = queryStr;
		if (mViewType == SEARCH_VIEW && mHeader != null) {
			mHeader.setText("\"" + queryStr + "\"");
		}
	}
	
	/**
	 * Set the number of hits to return for search results
	 * 
	 * @param defResultSize
	 */
	public void setDefResultSize(int defResultSize) {
		mDefResultSize = defResultSize;
	}
	
	public void load() {
		if (mQuery == null)
			return;
		
		mResults.mNewSet = true;
		
		// If this is the first load, then we want to load the mailbox (including folders/tags)
		// This is IMHO a real hack job need to think of a better way of doing this
		if (!mInitialLoad) {
			mMidlet.mMbox.searchMail(mQuery, true, null, mDefResultSize, this, mResults, this);
			//Show the work in progress dialog
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("main.Searching"));
		} else {
			mMidlet.mMbox.loadMailbox(mMidlet, mQuery, true, mDefResultSize, this, mResults, this);
			//Show the work in progress dialog
			Dialogs.popupWipDialog(mMidlet, this, Locale.get("main.LoadingMbox"));
		}
	}
	
	public ConvItem createConvItem() {
		ConvItem c;
		if (mViewType == INBOX_VIEW) {
			//#style InboxConvItem
			c = new ConvItem(mMidlet, this);
		} else {
			//#style SearchConvItem
			c = new ConvItem(mMidlet, this);			
		}
		return c;
	}
	
	public void convDeleted(String convId) {
		//See if the conv is in the list
		int sz = mView.size();
		ConvItem item;
		for (int i = 0; i < sz; i++) {
			item = (ConvItem)mView.get(i);
			if (convId.compareTo(item.mId) == 0) {
				deleteItem(item);
				break;
			}
		}
	}
	
	protected void itemStateChanged(MailItem item,
            				     	int what) {
		//TODO when we cache conversations, need to update all the cached children's state	
		//updateList(mMidlet.getInboxView(), item, what);
		//updateList(mMidlet.getSearchView(), item, what);
		
		switch (what) {
			case DELETED:
				deleteItem(item);
				break;
			default:
				//#if true
					//# if (mView.getCurrentItem() == item) {
						//# itemHasFocus(item);
					//# }
				//#endif
				break;
		}
	}

	public void handleResponse(Object op,
							   Object resp) {
		//#debug
		System.out.println("ConvListItem.handleResponse");

		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif

		if (op == Mailbox.SEARCHMAIL) {
			if (mViewType == INBOX_VIEW) {
				//#style InboxViewHeader
				UiAccess.setStyle(mHeader);
			} else {
				//#style SearchViewHeader
				UiAccess.setStyle(mHeader);			
			}
			
			if (mGettingMore)
				mHeader.setText(mSavedTitle);
	
			mGettingMore = false;
		}
		
		if (resp instanceof Mailbox) {
			//#debug 
			System.out.println("ConvListView.handleResponse: search successful");
			
			// see load()
			mInitialLoad = false;
			
			//Clear out the current list if it is a new set of data
			if (mResults.mNewSet)
				f.deleteAll();
			mMoreHits = mResults.mMore;
	
			Vector results = mResults.mResults;
			if (results.size() > 0) {
				for (Enumeration e = results.elements() ; e.hasMoreElements() ;)
				    f.append((MailItem)e.nextElement());
			} else if (mResults.mNewSet){
				if (mViewType == INBOX_VIEW)
					mNoDataItem.setText(Locale.get("main.InboxEmpty"));
				else
					mNoDataItem.setText(Locale.get("main.NoSearchResultsMatched"));

				f.append(mNoDataItem);
				if (mView.getTicker() != null)
					mView.getTicker().setString("");
			}

			mMidlet.mDisplay.setCurrent(mView);
		} else {
			mMidlet.handleResponseError(resp, this);
		}
	}

	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (mFragmentShowing) {
				mFragmentShowing = false;
				Dialogs.hideScreenInfo();
			}
			if (cmd == REFRESH) {
				if (!mGettingMore)
					load();
			} else if (cmd == EXIT) {
				mMidlet.exit();
			} else if (cmd == GOTO_SEARCHVIEW) {
				mMidlet.gotoSearchView();
			} else {
				// Delegate the command handling up to the parent
				super.commandAction(cmd, d);
			}
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);			
		} else {
			super.commandAction(cmd, d);
		}
	}
	
	public void keyPressed(int keyCode,
						   int gameAction,
						   Item item) {
		if (keyCode == Canvas.KEY_NUM0) {
			if (mViewType == INBOX_VIEW)
				mMidlet.gotoSearchView();
			else
				mMidlet.gotoInboxView();
		} else {
			super.keyPressed(keyCode, gameAction, item);
		}
	}
		
	/* This method is called by MailItem subclasses when they are requesting more data 
	 * be added to the list*/
	public void getMore(MailItem lastItem) {
		if (mMoreHits && !mGettingMore) {
			mGettingMore = true;
			mResults.mNewSet = false;				
			mMidlet.mMbox.searchMail(mQuery, true, lastItem, mDefResultSize, this, mResults, this);
			//Show the work in progress dialog
			//mMidlet.setWipView(this, Locale.get("mailList.GettingMoreData"));
			
			if (mViewType == INBOX_VIEW) {
				//#style InboxViewHeaderBusy
				UiAccess.setStyle(mHeader);
			} else {
				//#style SearchViewHeaderBusy
				UiAccess.setStyle(mHeader);			
			}
			mSavedTitle = mHeader.getText();
			mHeader.setText(Locale.get("mailList.Fetching"));
		}
	}
	
	public void action(Object source, 
					   Object data) {
		if (source == mMidlet.mSettings) {
			setTickerData();
		}	
	}
	
	public void setCurrent() {
		setTickerData();
		super.setCurrent();
	}
	
	private void setTickerData() {
		showTicker(mMidlet.mSettings.getShowConvTicker());
		if (mShowTicker) {
			Item item = null;
			//#if true
				//# item = mView.getCurrentItem();
			//#endif
			if (item != null) {
				if (item instanceof MailItem) {
					MailItem m = (MailItem)item;
					mTicker.setString((m.mFragment != null) ? m.mFragment : "");
				}
			} else { //Empty list
				mTicker.setString("");
			}
		}		
	}
	
	protected  boolean confirmDeletes() {
		return !(mMidlet.mSettings.getDelWOConf() && mMidlet.mSettings.getDelWOCConv());
	}
	
	
	protected void itemHasFocus(MailItem item) {
		if (mShowTicker)
			mTicker.setString((item.mFragment != null) ? item.mFragment : "");
		
		if ((item.mFlags & MailItem.FLAGGED) == MailItem.FLAGGED)
			UiAccess.setCommandLabel(mView, mToggleFlag, Locale.get("mailList.Unflag"));
		else 
			UiAccess.setCommandLabel(mView, mToggleFlag, Locale.get("mailList.Flag"));

		if ((item.mFlags & MailItem.UNREAD) == MailItem.UNREAD)
			UiAccess.setCommandLabel(mView, mToggleUnread, Locale.get("mailList.MarkRead"));
		else 
			UiAccess.setCommandLabel(mView, mToggleUnread, Locale.get("mailList.MarkUnread"));
	}

	private void init(int viewType) {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		mDefResultSize = DEF_RESULT_SIZE;
		mViewType = viewType;
		mMidlet.mSettings.addListener(this);
		
		if (viewType == INBOX_VIEW) {
			//#style InboxViewHeader
			mHeader = new StringItem(null, Locale.get("main.Inbox"));
		} else {
			//#style SearchViewHeader
			mHeader = new StringItem(null, "");			
		}
		
		f.append(Graphics.TOP, mHeader);	
		
		mNoDataItem.setText(Locale.get("main.NoSearchResultsMatched"));
		f.append(mNoDataItem);

		showTicker(mMidlet.mSettings.getShowConvTicker());
		setMenu();
	}
	
	private void setMenu() {
		//#if polish.hasCommandKeyEvents || (polish.key.LeftSoftKey:defined && polish.key.RightSoftKey:defined)
			//#define tmp.hasCmdKeyEvts
		//#endif
		
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		f.addCommand(ACTIONS);
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style ThreeMenuItem
			f.addSubCommand(mToggleUnread, ACTIONS);
		//#else
			f.addSubCommand(mToggleUnread, ACTIONS);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style StarMenuItem
			f.addSubCommand(mToggleFlag, ACTIONS);
		//#else
			f.addSubCommand(mToggleFlag, ACTIONS);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style EightMenuItem
			f.addSubCommand(JUNK, ACTIONS);
		//#else
			f.addSubCommand(JUNK, ACTIONS);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style SevenMenuItem
			f.addSubCommand(DELETE, ACTIONS);
		//#else
			f.addSubCommand(DELETE, ACTIONS);
		//#endif

		//#ifdef tmp.hasCmdKeyEvts
			//#style NineMenuItem
			f.addSubCommand(SHOW_FRAGMENT, ACTIONS);
		//#else
			f.addSubCommand(SHOW_FRAGMENT, ACTIONS);
		//#endif
	

		f.addCommand(ZimbraME.GOTO);
		
		if (mViewType == SEARCH_VIEW) {
			//#ifdef tmp.hasCmdKeyEvts
				//#style ZeroMenuItem
				f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
			//#else
				f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
			//#endif
		}
		
		f.addSubCommand(ZimbraME.GOTO_SENT, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_CALENDAR, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_SAVEDSEARCHES, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_FOLDERS, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_TAGS, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_SETTINGS, ZimbraME.GOTO);
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style OneMenuItem
			f.addCommand(ZimbraME.SEARCH);
		//#else
			f.addCommand(ZimbraME.SEARCH);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style TwoMenuItem
			f.addCommand(COMPOSE);
		//#else
			f.addCommand(COMPOSE);
		//#endif
			
		f.addCommand(ZimbraME.EXIT);
		
		//#ifdef polish.debugEnabled
			f.addCommand(ZimbraME.SHOW_LOG);
		//#endif
		
		f.addCommand(REFRESH);
			
		//#undefine tmp.hasCmdKeyEvts
	}

}
