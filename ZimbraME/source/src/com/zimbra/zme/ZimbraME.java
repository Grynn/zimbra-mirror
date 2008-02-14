/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007 Zimbra, Inc.  All Rights Reserved.
 * 
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.zme;

import javax.microedition.midlet.MIDlet;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;

import java.io.IOException;
import java.util.Vector;

import com.zimbra.zme.client.Appointment;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.ZmeSvcException;
import com.zimbra.zme.ui.ApptView;
import com.zimbra.zme.ui.CalendarView;
import com.zimbra.zme.ui.CollectionView;
import com.zimbra.zme.ui.ContactListView;
import com.zimbra.zme.ui.Dialogs;
import com.zimbra.zme.ui.LoginView;
import com.zimbra.zme.ui.ComposeView;
import com.zimbra.zme.ui.ConvListView;
import com.zimbra.zme.ui.MailItem;
import com.zimbra.zme.ui.MsgListView;
import com.zimbra.zme.ui.SettingsView;
import com.zimbra.zme.ui.View;
import com.zimbra.zme.ui.ZmeCustomItem;

import de.enough.polish.util.Locale;
import de.enough.polish.util.Debug;      

public class ZimbraME extends MIDlet implements CommandListener {

	public static final Command CANCEL = new Command(Locale.get("main.Cancel"), Command.CANCEL, 10);
	public static final Command EXIT = new Command(Locale.get("main.Exit"), Command.EXIT, 10);
	public static final Command LOGOUT = new Command(Locale.get("main.Logout"), Command.EXIT, 10);
	public static final Command OK = new Command(Locale.get("main.Ok"), Command.OK, 10);
	public static final Command SEARCH = new Command(Locale.get("main.Search"), Command.ITEM, 1);

	public static final Command GOTO = new Command(Locale.get("main.Goto"), Command.ITEM, 1);
	public static final Command GOTO_CALENDAR = new Command(Locale.get("main.Calendar"), Command.ITEM, 1);
	public static final Command GOTO_FOLDERS = new Command(Locale.get("main.Folders"), Command.ITEM, 1);
	public static final Command GOTO_INBOX = new Command(Locale.get("main.Inbox"), Command.ITEM, 1);
	public static final Command GOTO_SAVEDSEARCHES = new Command(Locale.get("main.SavedSearches"), Command.ITEM, 1);
	public static final Command GOTO_SENT = new Command(Locale.get("main.SentMail"), Command.ITEM, 1);
	public static final Command GOTO_SETTINGS = new Command(Locale.get("main.Settings"), Command.ITEM, 1);
	public static final Command GOTO_TAGS = new Command(Locale.get("main.Tags"), Command.ITEM, 1);

	public static Image CLOCK_ICON;
	public static int CLOCK_ICON_HEIGHT;
	public static int CLOCK_ICON_WIDTH;

	{
		try {
			CLOCK_ICON = Image.createImage("/Clock.png");
			CLOCK_ICON_WIDTH = CLOCK_ICON.getWidth();
			CLOCK_ICON_HEIGHT = CLOCK_ICON.getHeight();
		} catch (IOException e) {
			//#debug
			System.out.println("ZimbraME.init: IOException " + e);
		}
	}

	private static final Command DOSEARCH = new Command(Locale.get("main.Search"), Command.OK, 1);

	// if debugging is enabled, then create a debug command that will show the debug log on a real device
	//#ifdef polish.debugEnabled
		public static final Command SHOW_LOG = new Command(Locale.get("main.DebugLog"), Command.ITEM, 1);
	//#endif


    public static final String DEF_REST_PATH = "/service/home/";
    public static final String DEF_HOME_PATH = "/service/home/~/";
	public static final String SET_AUTH_COOKIE_PATH = "/zimbra/public/setauth.jsp";
	private static final String DEF_SVC_PATH = "/service/soap";

	private static final String SERVER_URL_PROP = "Server-URL";
	private static final String SERVER_SVC_PATH = "Server-Svc-Path";

	public Display mDisplay;

	public Mailbox mMbox;
    public Settings mSettings;
    public String mUsername;
	public String mServerUrl; // Server's URL
	public String mServerSvcPath; // Service path. Added to the server url e.g. "/service/soap"
	public boolean mUserServerUrl; //If true the user must enter the server url. Else is specified in JAD

    private boolean mInited;
	private TextBox mSearchTextBox;
    private Displayable mPrevView; // Previous view
    private ContactListView mContactPickerListView;
    private CalendarView mCalendarView;
    private View mTopView; // Top level view
	private ConvListView mInboxView;
	private ConvListView mSearchView;
	private MsgListView mMsgListView;
    private boolean mRunCustomShortcut;

    public ZimbraME () {
    }

    public ConvListView getInboxView() {
    	return mInboxView;
    }

    public CalendarView getCalendarView() {
        return mCalendarView;
    }
    
    public void gotoInboxView() {
    	mTopView = getInboxView();
    	mTopView.setCurrent();
    }

    public ConvListView getSearchView() {
    	if (mSearchView == null) {
    		//#style SearchView
    		mSearchView = new ConvListView(null, this, ConvListView.SEARCH_VIEW);
    	}
    	return mSearchView;
    }
    
    public void gotoSearchView() {
    	mTopView = getSearchView();
    	mTopView.setCurrent();
    }

    public MsgListView getMsgListView() {
    	if (mMsgListView == null) {
		    //#style MsgListView
		    mMsgListView = new MsgListView(null, this);
    	}
    	return mMsgListView;
    }

    public LoginView getLoginView() {
    	return new LoginView(this);
    }

    public ContactListView getContactPickerListView() {
    	if (mContactPickerListView == null)
    		//#style ContactListView
    		mContactPickerListView = new ContactListView(this, ContactListView.PICKER_STYLE);
    	return mContactPickerListView;
    }

    public ComposeView createComposeView() {
    	//#style ComposeView
    	ComposeView c = new ComposeView(this);
    	return c;
    }

    public ComposeView gotoComposeView(Displayable current) {
    	ComposeView c = createComposeView();
    	c.setNext(current);
    	c.setCurrent();
    	return c;
    }

    public ApptView gotoApptView(Displayable current, Appointment appt) {
        //#style ApptView
        ApptView a = new ApptView(this, appt);
        if (appt == null)
            a.setCurrent();
        
        a.setNext(current);
        return a;
    }
    
    public CollectionView gotoSavedSearchView(Displayable current) {
   		//#style CollectionView
     	CollectionView cv = new CollectionView(this, CollectionView.SAVEDSEARCH);
    	if (mMbox.mSavedSearches == null) {
    		cv.load();
    	} else {
    		cv.render();
    		cv.setCurrent();
    	}
		cv.setNext(current);
		return cv;
    }

    public CollectionView gotoSavedSearchPickerView(Displayable current) {
        //#style CollectionView
        CollectionView cv = new CollectionView(this, CollectionView.SAVEDSEARCH_PICK);
        if (mMbox.mSavedSearches == null) {
            cv.load();
        } else {
            cv.render();
            cv.setCurrent();
        }
        cv.setNext(current);
        return cv;
    }

    public CollectionView gotoFolderSearchView(Displayable current) {
   		//#style CollectionView
     	CollectionView cv = new CollectionView(this, CollectionView.FOLDER_SEARCH);
    	if (mMbox.mRootFolder == null) {
    		cv.load();
    	} else {
    		cv.render();
    		cv.setCurrent();
    	}
		cv.setNext(current);
		return cv;
    }

    public CollectionView gotoFolderPickerView(Displayable current) {
        //#style CollectionView
        CollectionView cv = new CollectionView(this, CollectionView.FOLDER_PICK);
        if (mMbox.mRootFolder == null) {
            cv.load();
        } else {
            cv.render();
            cv.setCurrent();
        }
        cv.setNext(current);
        return cv;
    }

    public CollectionView gotoAttachmentListView(Displayable current,
    										     Vector attachmentList) {
   		//#style CollectionView
     	CollectionView cv = new CollectionView(this, CollectionView.ATTACHMENTLIST);
    	cv.load(attachmentList);
    	cv.render();
    	cv.setCurrent();
		cv.setNext(current);
    	return cv;
    }
    
    public CollectionView gotoTagView(Displayable current,
    							      int tagViewType, 
    								  String[] tags) {
   		//#style CollectionView
     	CollectionView cv = new CollectionView(this, tagViewType);
    	if (mMbox.mTags == null) {
    		cv.load();
    	} else {
    		cv.render();
    		cv.setCurrent();
    	}
    	cv.setTags(tags);
		cv.setNext(current);
		return cv;
    }

    
    public View getTopView() {
    	return mTopView;
    }
    
    public void setTopViewCurrent() {
    	mDisplay.setCurrent(mTopView.getDisplayable());
    }

    public void execSearch(String query,
    					   String sortBy,
    					   String types) {

		ConvListView clv = getSearchView();
		if (query != null)
			clv.setQuery(query, sortBy, types);
		else
			clv.setQuery(mSearchTextBox.getString(), sortBy, types);
		mTopView = clv;
		clv.load();
    }

    public void gotoFolder(String folderName) {
        //#style InboxView
        ConvListView folderView = new ConvListView(folderName, this, ConvListView.FOLDER_VIEW);
        folderView.setQuery("in:\""+folderName+"\"", null, null);
        //folderView.setQueryRest(mUsername, folderName, null, null);
        mTopView = folderView;
        folderView.load();
    }
    
    public void gotoCalendarView() {
    	if (mCalendarView == null) {
    		//#style CalendarView
    		mCalendarView = new CalendarView(this);
    		mCalendarView.load();
    	} else {
    		mCalendarView.setCurrent();
    	}
    	mTopView = mCalendarView;
    }

    public void gotoSettings() {
 
	   View settingsView = new SettingsView(this, mSettings);
	   settingsView.setCurrent();
    }

    public void commandAction(Command cmd,
    						  Displayable d) {
    	if (d == mSearchTextBox) {
    		// Dealing with the search box commands
	    	if (cmd == DOSEARCH) {
	    		execSearch(null, null, null);
	    	} else if (cmd == CANCEL) {
	    		mDisplay.setCurrent(mPrevView);
	    	}
    	} else if (d == Dialogs.mErrorD) {
    		mDisplay.setCurrent(mPrevView);
    	} else if (cmd == EXIT) {
    		exit();
    	} else if (cmd == SEARCH) {
    		doSearch(d);
    	} else if (cmd == GOTO_CALENDAR) {
    		gotoCalendarView();
    	} else if (cmd == GOTO_FOLDERS) {
			gotoFolderSearchView(d);
    	} else if (cmd == GOTO_INBOX) {
			gotoInboxView();
    	} else if (cmd == GOTO_SENT) {
    		execSearch("in:sent", null, null);
    	} else if (cmd == GOTO_SETTINGS) {
    		gotoSettings();
    	} else if (cmd == GOTO_SAVEDSEARCHES) {
    		gotoSavedSearchView(d);
    	} else if (cmd == GOTO_TAGS) {
    		gotoTagView(d, CollectionView.TAG_SEARCH, null);
    	}


    	//#ifdef polish.debugEnabled
	    	if (cmd == SHOW_LOG) {
	    		Debug.showLog(mDisplay);
	    	}
    	//#endif
    }

    public void exit() {
		destroyApp(true);
		notifyDestroyed();
    }

    public void keyPressed(int keyCode,
                           Displayable d) {
    	switch (keyCode) {
    		case Canvas.KEY_NUM1:
    			doSearch(d);
    			break;
    		case Canvas.KEY_NUM0:
    			mTopView = mInboxView;
    			setTopViewCurrent();
    			break;
    		case Canvas.KEY_NUM6:
    			gotoSavedSearchView(d);
    			break;
    	}
    }

    public boolean handleCustomShortcut(ZmeCustomItem item, int keyCode) {
        switch (keyCode) {
        case Canvas.KEY_POUND:
            mRunCustomShortcut = true;
            return true;
        default:
            if (mRunCustomShortcut) {
                mRunCustomShortcut = false;
                int num = keyCode - Canvas.KEY_NUM0;
                if (num < 0 || num > 9)
                    break;
                Shortcut s = mSettings.getShortcut(num);
                if (!s.isConfigured())
                    break;
                //#debug
                System.out.println("shortcut: #"+num);
                runCustomShortcut(item, s);
                return true;
            }
            break;
        }
        return false;
    }
    
    private void runCustomShortcut(ZmeCustomItem item, Shortcut s) {
        // XXX currently handles MailItems only.  need to consolidate
        // the item classes in order for other items to be used.
        if (!(item instanceof MailItem))
            return;
        MailItem mailItem = (MailItem) item;
        switch (s.action) {
        case Shortcut.ACTION_MOVE_TO_FOLDER:
            this.mMbox.moveItem(mailItem.mId, s.destId[0], mailItem);
            break;
        case Shortcut.ACTION_TAG:
            mailItem.setTags(s.destId);
            break;
        case Shortcut.ACTION_RUN_SAVED_SEARCH:
            for (int i = 0; i < mMbox.mSavedSearches.size(); i++) {
                SavedSearch ss = (SavedSearch)mMbox.mSavedSearches.elementAt(i);
                if (ss.mId.equals(s.destId[0])) {
                    execSearch(ss.mQuery, ss.mSortBy, ss.mTypes); 
                    break;
                }
            }
            break;
        }
    }
    
	public void handleResponseError(Object resp,
									View view) {
		//#debug
		System.out.println("ZimbraME.handleResponseError");

		mPrevView = view.getDisplayable();
		if (resp instanceof ZmeSvcException) {
			//#debug
			System.out.println("ZimbraME.handleResponseError: Fault from server");
			String ec = ((ZmeSvcException)resp).mErrorCode;
			//TODO need to handle sendMsg failures
			if (ec == ZmeSvcException.SVC_AUTHEXPIRED || ec == ZmeSvcException.SVC_AUTHREQUIRED) {
				/* Session expired, try to relogin */
				getLoginView().sessionExpired(view);
				return;
			} else if (ec == ZmeSvcException.MAIL_QUERYPARSEERROR) {
				// TODO is the belwo true? What about a bogus saved search?
				// Query string error. We will assume this can only happen due to the user
				// entering a bogus string so send them back to the search view
				Dialogs.popupErrorDialog(this, this, Locale.get("error.Parse"));
				mPrevView = mSearchTextBox;
			} else {
				Dialogs.popupErrorDialog(this, this, ec);
			}
		} else if (resp instanceof ZmeException) {
			ZmeException e = (ZmeException)resp;
			if (e.mErrCode == ZmeException.IO_ERROR) {
				//#debug
				System.out.println("ZimbraME.handleResponseError: IOException");
				String errMsg = (e.getMessage());
				errMsg = (errMsg == null) ? "" : "\n\n" + errMsg;
				Dialogs.popupErrorDialog(this, this, Locale.get("error.NetworkError") + errMsg);
			} else {
				//#debug
				System.out.println("ZimbraME.handleResponseError: General error (1): " + e.mErrCode);
				Dialogs.popupErrorDialog(this, this, Locale.get("error.GeneralError") + "\n\nCode: " + e.mErrCode + "\nMsg: " + e.getMessage());
			}
		} else if (resp instanceof Exception) {
			//#debug
			System.out.println("ZimbraME.handleResponseError: General error (2): " + resp);
			Exception e = (Exception)resp;
			String errMsg = (e.getMessage());
			errMsg = (errMsg == null) ? "" : "\n\n" + errMsg;
			Dialogs.popupErrorDialog(this, this, Locale.get("error.GeneralError") + errMsg);
		}
	}

	public void logout() {
		init();
		mMbox.mAuthToken = null;
		View loginView = getLoginView();
		loginView.setNext(mInboxView);
		loginView.setCurrent();
	}
	
	private void init() {
        mInited = true;
        mServerUrl = getAppProperty(SERVER_URL_PROP);
        mServerSvcPath = getAppProperty(SERVER_SVC_PATH); 
        if (mServerSvcPath == null)
        	mServerSvcPath = DEF_SVC_PATH;

        mSettings = Settings.load();

		try {
			mMbox = new Mailbox(1);
		} catch (ZmeException ex) {
			//#debug
			System.out.println("ZimbraME.startApp: ZmeException " + ex);
			//TODO Fatal dialog, then exit
		}

		if (mServerUrl == null || mServerUrl.compareTo("USERDEFINED") == 0) {
			mUserServerUrl = true;
			mServerUrl = mSettings.getServerUrl();
		} else {
			mUserServerUrl = false;
		}

		mMbox.mServerUrl = mServerUrl + mServerSvcPath;
		mMbox.mSetAuthCookieUrl = mServerUrl + ZimbraME.SET_AUTH_COOKIE_PATH;
		mMbox.mRestUrl = mServerUrl + DEF_REST_PATH;
		mMbox.mMidlet = this;
		mMbox.mAuthToken = mSettings.getAuthToken();

		mDisplay = Display.getDisplay(this); 

		//#style InboxView
		mInboxView = new ConvListView(null, this, ConvListView.INBOX_VIEW);
		mInboxView.setQuery("in:inbox", null, null);
		mTopView = mInboxView;

	}

    protected void startApp() {
    	if (!mInited) {
			init();
			View loginView = getLoginView();
			loginView.setNext(mInboxView);
			loginView.setCurrent();
    	}
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean bool) {
    }

    private void doSearch(Displayable d) {
		if (mSearchTextBox == null) {
			mSearchTextBox = new TextBox(Locale.get("main.Search"), null, 1024, TextField.ANY);
			mSearchTextBox.setString("");
			mSearchTextBox.addCommand(DOSEARCH);
			mSearchTextBox.addCommand(CANCEL);
			mSearchTextBox.setCommandListener(this);
		}
		mPrevView = d;
		mDisplay.setCurrent(mSearchTextBox);
    }

    public void openAttachment(String msgId, String part) throws ConnectionNotFoundException {
        StringBuffer buf = new StringBuffer();
        buf.append(mServerUrl);
        buf.append(DEF_HOME_PATH);
        buf.append("?id=");
        buf.append(msgId);
        buf.append("&part=");
        buf.append(part);
        buf.append("&view=html");
        buf.append("&authToken=");
        buf.append(mMbox.mAuthToken);
        platformRequest(buf.toString());
    }
}
