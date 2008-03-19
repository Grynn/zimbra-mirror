/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;

import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeException;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.ZmeSvcException;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ConvListView extends MailListView {
	
	public static final int INBOX_VIEW = 1;
	public static final int SEARCH_VIEW = 2;
    public static final int FOLDER_VIEW = 3;

	protected static final Command SHOW_FRAGMENT = new Command(Locale.get("mailList.Fragment"), Command.ITEM, 1);
	private static final Command GOTO_SEARCHVIEW = new Command(Locale.get("mailList.SearchView"), Command.ITEM, 1);
	private static final Command SAVE = new Command(Locale.get("main.Save"), Command.ITEM, 1);
	private static final Command SAVE_SEARCH = new Command(Locale.get("mailList.SaveSearch"), Command.ITEM, 1);
	private static final Command REFRESH = new Command(Locale.get("main.Refresh"), Command.BACK, 1);

	private static boolean mInitialLoad = true;
	
	private String mQuery;
	private int mViewType;
	private boolean mGettingMore;
	private String mSavedTitle;
	private TextBox mSaveSearchTB;
	
	//#ifdef polish.usePolishGui
		public ConvListView(String title,
							ZimbraME midlet,
							int viewType,
							Style style) {
			super(null, midlet, style);
            mSavedTitle = title;
			init(viewType);
		}
	//#else
		public ConvListView(String title,
							ZimbraME midlet,
							int viewType) {
			super(null, midlet);
            mSavedTitle = title;
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
		if (mQuery == null || mQuery.equals(""))
			mQuery = ZimbraME.DEFAULT_QUERY;
		if (mViewType == SEARCH_VIEW && mHeader != null) {
			mHeader.setText(Locale.get("main.Search") + ": " + queryStr);
		}
	}
	
    private String mUser;
    private String mFolder;
    
    public void setQueryRest(String user, String folder, String sortBy, String types) {
        mUser = user;
        mFolder = folder;
        if (mViewType == FOLDER_VIEW && mHeader != null) {
            mHeader.setText(folder);
        }
    }
    
    public void setQuery(String folder,
                            String queryStr,
                            String sortBy,
                            String types) {

        //TODO support sortBy and types
        mQuery = queryStr;
        if (mViewType == FOLDER_VIEW && mHeader != null) {
            mHeader.setText(folder);
        }
    }

	public void load() {
	    //if (mQuery == null)
			//return;
		
		mResults.mNewSet = true;
		
        //Show the work in progress dialog
        String msg = (mInitialLoad ? Locale.get("main.LoadingMbox") : Locale.get("main.Searching"));
        Dialogs.popupWipDialog(mMidlet, this, msg);
        
        if (mQuery != null)
            mMidlet.mMbox.searchMail(mQuery, true, null, INITIAL_RESULT_SIZE, this, mResults, this);
        else
            mMidlet.mMbox.searchMailRest(mUser, mFolder, null, INITIAL_RESULT_SIZE, this, mResults, this);
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
	
    public MsgItem createMsgItem() {
        //#style MsgItem
        MsgItem m = new MsgItem(mMidlet, this);
        return m;
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
		System.out.println("ConvListView.handleResponse");

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
        if (op != Mailbox.LOADMAILBOX)
            mMidlet.mDisplay.setCurrent(mView);

		if (resp instanceof Mailbox) {
            MailItem lastItem = null;
			if (op == Mailbox.SEARCHMAIL || op == Mailbox.SEARCHMAILREST) {
				//#debug 
				System.out.println("ConvListView.handleResponse: search successful");
				
				//Clear out the current list if it is a new set of data
				if (mResults.mNewSet) {
					f.deleteAll();
					f.append(Graphics.TOP, mHeader);    
                }
				mMoreHits = mResults.mMore;
		
				Vector results = mResults.mResults;
				if (results.size() > 0) {
					for (Enumeration e = results.elements() ; e.hasMoreElements() ;) {
                        lastItem = (MailItem)e.nextElement();
					    f.append(lastItem);
                    }
				} else if (mResults.mNewSet){
					if (mViewType == INBOX_VIEW)
						mNoDataItem.setText(Locale.get("main.InboxEmpty"));
					else
						mNoDataItem.setText(Locale.get("main.NoSearchResultsMatched"));
	
					f.append(mNoDataItem);
					if (mView.getTicker() != null)
						mView.getTicker().setString("");
				}
			} else { // CreateSearchFolder
				
			}

            
            if (op == Mailbox.SEARCHMAIL && mMoreHits && f.size() <= INITIAL_RESULT_SIZE) {
                // prefetch more messages
                mResults.mNewSet = false;
                mGettingMore = true;
                mSavedTitle = mHeader.getText();
                mMidlet.mMbox.searchMail(mQuery, true, lastItem, DEF_RESULT_SIZE, this, mResults, this);
            }
            if (mInitialLoad) {
                // fetch the rest of the mailbox information
                mMidlet.mMbox.loadMailbox(this);
                mInitialLoad = false;
            }
            
		} else if (resp instanceof ZmeSvcException) {
			//#debug
			System.out.println("ConvListView.handleResponse: Fault from server");
			String ec = ((ZmeSvcException)resp).mErrorCode;
				
			if (ec == ZmeSvcException.MAIL_ALREADYEXISTS) {
				mMidlet.mDisplay.setCurrent(mView);
				Dialogs.popupErrorDialog(mMidlet, this, Locale.get("mailList.SavedSearchExists"));
			} else {
				mMidlet.handleResponseError(resp, this);
			}
		} else if (resp instanceof ZmeException) {
            //#debug
            System.out.println("ConvListView.handleResponse: Exception");
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
			} else if (cmd == GOTO_SEARCHVIEW) {
				mMidlet.gotoSearchView();
			} else if (cmd == SAVE_SEARCH) {
				doSaveSearch();
			} else if (cmd == BACK) {
				mMidlet.gotoInboxView();
			} else {
				// Delegate the command handling up to the parent
				super.commandAction(cmd, d);
			}
		} else if (d == mSaveSearchTB) {
			mMidlet.mDisplay.setCurrent(mView);
			if (cmd == SAVE) {
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("mailList.SavingSearch"));
				mMidlet.mMbox.createSavedSearch(mSaveSearchTB.getString(), mQuery, this);
			}
			mSaveSearchTB = null;
		} else if (d == Dialogs.mWipD) {
			mMidlet.mMbox.cancelOp();
			mMidlet.mDisplay.setCurrent(mView);		
		} else if (d == Dialogs.mErrorD) {
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
			mMidlet.mMbox.searchMail(mQuery, true, lastItem, DEF_RESULT_SIZE, this, mResults, this);
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
		} else {
			super.action(source, data);
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

    private void doSaveSearch() {
		if (mSaveSearchTB == null) {
			mSaveSearchTB = new TextBox(Locale.get("main.Save"), null, 1024, TextField.ANY);
			mSaveSearchTB.setString("");
			mSaveSearchTB.addCommand(SAVE);
			mSaveSearchTB.addCommand(CANCEL);
			mSaveSearchTB.setCommandListener(this);
		}
		mMidlet.mDisplay.setCurrent(mSaveSearchTB);
    }
	
	private void init(int viewType) {
		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
		
		mViewType = viewType;
		mMidlet.mSettings.addListener(this);
		
		if (viewType == INBOX_VIEW) {
			//#style InboxViewHeader
			mHeader = new StringItem(null, Locale.get("main.Inbox"));
		} else if (viewType == FOLDER_VIEW) {
			//#style InboxViewHeader
			mHeader = new StringItem(null, mSavedTitle);			
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
			f.addSubCommand(mToggleUnreadCmd, ACTIONS);
		//#else
			f.addSubCommand(mToggleUnreadCmd, ACTIONS);
		//#endif
		
		//#ifdef tmp.hasCmdKeyEvts
			//#style StarMenuItem
			f.addSubCommand(mToggleFlagCmd, ACTIONS);
		//#else
			f.addSubCommand(mToggleFlagCmd, ACTIONS);
		//#endif
			
		f.addSubCommand(TAG, ACTIONS);
		
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
		
		if (mViewType != INBOX_VIEW) {
			//#ifdef tmp.hasCmdKeyEvts
				//#style ZeroMenuItem
				f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
			//#else
				f.addSubCommand(ZimbraME.GOTO_INBOX, ZimbraME.GOTO);
			//#endif
		}
		
		f.addSubCommand(ZimbraME.GOTO_SENT, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_CALENDAR, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_FOLDERS, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_TAGS, ZimbraME.GOTO);
		f.addSubCommand(ZimbraME.GOTO_SETTINGS, ZimbraME.GOTO);
		
		if (mViewType == SEARCH_VIEW)
			f.addCommand(SAVE_SEARCH);
		
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
		f.addCommand(ZimbraME.LOGOUT);
		
		//#ifdef polish.debugEnabled
			f.addCommand(ZimbraME.SHOW_LOG);
		//#endif
		
		if (mViewType == INBOX_VIEW)
			f.addCommand(REFRESH);
		else
			f.addCommand(BACK);
			
		//#undefine tmp.hasCmdKeyEvts
	}

}
