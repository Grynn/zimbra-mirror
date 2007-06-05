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


package com.zimbra.zme;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;

import java.io.IOException;

import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.ZmeSvcException;
import com.zimbra.zme.ui.CalendarView;
import com.zimbra.zme.ui.ContactListView;
import com.zimbra.zme.ui.Dialogs;
import com.zimbra.zme.ui.LoginView;
import com.zimbra.zme.ui.ComposeView;
import com.zimbra.zme.ui.ConvListView;
import com.zimbra.zme.ui.MsgListView;
import com.zimbra.zme.ui.SavedSearchView;
import com.zimbra.zme.ui.SettingsView;
import com.zimbra.zme.ui.View;

import de.enough.polish.util.Locale;
import de.enough.polish.util.Debug;      

public class ZimbraME extends MIDlet implements CommandListener{

	public static final Command CANCEL = new Command(Locale.get("main.Cancel"), Command.CANCEL, 10);
	public static final Command EXIT = new Command(Locale.get("main.Exit"), Command.EXIT, 10);
	public static final Command SEARCH = new Command(Locale.get("main.Search"), Command.ITEM, 1);

	public static final Command GOTO = new Command(Locale.get("main.Goto"), Command.ITEM, 1);
	public static final Command GOTO_CALENDAR = new Command(Locale.get("main.Calendar"), Command.ITEM, 1);
	public static final Command GOTO_INBOX = new Command(Locale.get("main.Inbox"), Command.ITEM, 1);
	public static final Command GOTO_SAVEDSEARCHES = new Command(Locale.get("main.SavedSearches"), Command.ITEM, 1);
	public static final Command GOTO_SENT = new Command(Locale.get("main.SentMail"), Command.ITEM, 1);
	public static final Command GOTO_SETTINGS = new Command(Locale.get("main.Settings"), Command.ITEM, 1);

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


	private static final String DEF_SVC_PATH = "/service/soap";

	private static final String SERVER_URL_PROP = "Server-URL";
	private static final String SERVER_SVC_PATH = "Server-Svc-Path";

	public Display mDisplay;

	public Mailbox mMbox;
    public Settings mSettings;
	public String mServerUrl; // Server's URL
	public String mServerSvcPath; // Service path. Added to the server url e.g. "/service/soap"
	public boolean mUserServerUrl; //If true the user must enter the server url. Else is specified in JAD

    private boolean mInited;
	private javax.microedition.lcdui.TextBox mSearchTextBox;
    private Displayable mPrevView; // Previous view
    private ContactListView mContactPickerListView;
    private CalendarView mCalendarView;
    private View mTopView; // Top level view
	private ConvListView mInboxView;
	private MsgListView mMsgListView;

    public ZimbraME () {
    }

    public ConvListView getInboxView() {
    	return mInboxView;
    }

    public ConvListView getSearchView() {
		//#style SearchView
		return new ConvListView(null, this, ConvListView.SEARCH_VIEW);
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

    public void setComposeView(Displayable current) {
    	ComposeView c = createComposeView();
    	c.setNext(current);
    	c.setCurrent();
    }

    public void setSavedSearchView(Displayable current) {
   		//#style SavedSearchView
     	SavedSearchView ssv = new SavedSearchView(this);
    	if (mMbox.mSavedSearches == null) {
    		ssv.load();
    	} else {
    		ssv.setSavedSearches(mMbox.mSavedSearches);
    		ssv.setCurrent();
    	}
		ssv.setNext(current);
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
		//#style SearchView
		ConvListView clv = new ConvListView(null, this, ConvListView.SEARCH_VIEW);
		if (query != null)
			clv.setQuery(query, sortBy, types);
		else
			clv.setQuery(mSearchTextBox.getString(), sortBy, types);
		mTopView = clv;
		clv.load();
    }

    public void gotoCalendar() {
    	if (mCalendarView == null) {
    		//#style CalendarView
    		mCalendarView = new CalendarView(this);
    		mCalendarView.load();
    	} else {
    		mCalendarView.setCurrent();
    	}
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
    		// TODO do I need to se mTopView?
    		gotoCalendar();
    	} else if (cmd == GOTO_INBOX) {
			mTopView = mInboxView;
			setTopViewCurrent();
    	} else if (cmd == GOTO_SENT) {
    		execSearch("in:sent", null, null);
    	} else if (cmd == GOTO_SETTINGS) {
    		gotoSettings();
    	} else if (cmd == GOTO_SAVEDSEARCHES) {
    		setSavedSearchView(d);
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
    			setSavedSearchView(d);
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
				Dialogs.popupErrorDialog(this, this, Locale.get("error.GeneralError") + "\n\nCode: " + e.mErrCode);
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
	

    protected void startApp() {
    	if (!mInited) {
	        mInited = true;
	        mServerUrl = getAppProperty(SERVER_URL_PROP);
	        mServerSvcPath = getAppProperty(SERVER_SVC_PATH); 
	        if (mServerSvcPath == null)
	        	mServerSvcPath = DEF_SVC_PATH;

	        mSettings = Settings.load();

	        if (mServerUrl == null || mServerUrl.compareTo("USERDEFINED") == 0) {
	        	mUserServerUrl = true;
	        	mServerUrl = mSettings.getServerUrl();
	        } else {
	        	mUserServerUrl = false;
	        }

	        try {
	        	mMbox = new Mailbox(2);
	        } catch (ZmeException ex) {
	        	//#debug
	        	System.out.println("ZimbraME.startApp: ZmeException " + ex);
	        	//TODO Fatal dialog, then exit
	        }

	        mMbox.mServerUrl = mServerUrl + mServerSvcPath;
	        mMbox.mMidlet = this;
	        mMbox.mAuthToken = mSettings.getAuthToken();
	        
	        mDisplay = Display.getDisplay(this); 

	        //#style InboxView
	        mInboxView = new ConvListView(null, this, ConvListView.INBOX_VIEW);
	        mInboxView.setQuery("in:inbox", null, null);
	        mTopView = mInboxView;

//	        gotoSettingsView();
//	    	return;

	        View loginView = getLoginView();
 	    	loginView = new LoginView(this);
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
			mSearchTextBox = new javax.microedition.lcdui.TextBox(Locale.get("main.Search"), null, 1024, TextField.ANY);
			mSearchTextBox.setString("");
			mSearchTextBox.addCommand(DOSEARCH);
			mSearchTextBox.addCommand(CANCEL);
			mSearchTextBox.setCommandListener(this);
		}
		mPrevView = d;
		mDisplay.setCurrent(mSearchTextBox);
    }

}
