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


/**
 * This class represents the ZME users profile. The profile include such things as username and password, and URL (if
 * it is not hard coded in the property file. The profile information is stored to persistent storage.
 */
package com.zimbra.zme;


import de.enough.polish.io.RmsStorage;
import de.enough.polish.io.Serializable;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class Settings implements Serializable {
	public static final int SLOW_TICKER = 2;
	public static final int MED_TICKER = 4;
	public static final int FAST_TICKER = 6;
	
    private static final String RS_NAME = "settingsStore";
    private static final String MY_NAME = "Settings";

    private static RmsStorage mRmsStore;
    private static Vector mListeners = new Vector();

    private String mUname;
    private String mPword;
    private String mServerURL;
    private String mAuthToken;
    private boolean mKeepSignedIn;
    private boolean mCacheContacts;    
    private boolean mPreloadContacts;
    private boolean mDelWOConf;
    private boolean mDelWOCConv;
    private boolean mDelWOCMsg;
    private boolean mShowConvTicker;
    private boolean mShowMsgTicker;
    private boolean mShowApptTicker;
    private int mTickerSpeed;
    private Shortcut[] mShortcuts;
    
    private boolean mDirty;
    
    public static Settings load() {
    	Settings s = null;
    	try {
    		//#debug
    		System.out.println("Loading settings...");
			mRmsStore = new RmsStorage(RS_NAME);
			s = (Settings)mRmsStore.read(MY_NAME);
			
			if (s.mUname == null)
				s.mUname = "";
			
			if (s.mPword == null)
				s.mPword = "";
			
			if (s.mServerURL == null)
				s.mServerURL = "";
			
			if (s.mAuthToken == null)
				s.mAuthToken = "";
			
			if (s.mTickerSpeed == 0)
				s.mTickerSpeed = MED_TICKER;

    		//#debug
    		System.out.println("Settings loaded");
		} catch (IOException e) {
			//#debug
			System.out.println("Couldn't load settings from record store");
			s = new Settings();
		}
        if (s.mShortcuts == null) {
            s.mShortcuts = new Shortcut[10];
            for (int i = 0; i < 10; i++)
                s.mShortcuts[i] = new Shortcut(i);
        }
                
        s.mShortcuts[1].action = Shortcut.ACTION_MOVE_TO_FOLDER;
        s.mShortcuts[1].dest = "Trash";
        s.mShortcuts[2].action = Shortcut.ACTION_TAG;
        s.mShortcuts[2].dest = "Important";
        
		//#debug
		System.out.println(s.toString());
    	return s;
    }
    
    public Settings() {
        mUname = "";
        mPword = "";
        mServerURL = "";
        mAuthToken = "";
        mKeepSignedIn = false;
     
        mCacheContacts = false;        
        mPreloadContacts = false;
        
        mShowConvTicker = false;
        mShowMsgTicker = false;
        mShowApptTicker = false;
        mTickerSpeed = MED_TICKER;
        
        mDelWOConf = true;
        mDelWOCConv = true;
        mDelWOCMsg = true;
    }

    public String toString() {
    	StringBuffer sb = new StringBuffer();
       	sb.append("mServerURL: ").append(mServerURL);
       	sb.append("\nmAuthToken: ").append(mAuthToken);
    	sb.append("\nmUname: ").append(mUname);
    	sb.append("\nmPword: ").append(mPword);
    	sb.append("\nmKeepSignedIn: ").append(mKeepSignedIn);
    	sb.append("\nmCacheContacts: ").append(mCacheContacts);
    	sb.append("\nmPreloadContacts: ").append(mPreloadContacts);
    	sb.append("\nmShowConvTicker: ").append(mShowConvTicker);
    	sb.append("\nmShowMsgTicker: ").append(mShowMsgTicker);
    	sb.append("\nmShowApptTicker: ").append(mShowApptTicker);
    	sb.append("\nmTickerSpeed: ").append(mTickerSpeed);
        sb.append("\nmShortcuts: ");
        for (int i = 0; i < mShortcuts.length; i++)
            if (mShortcuts[i].isConfigured())
                sb.append("\n\t").append(mShortcuts[i]);
    	return sb.toString();
    }
    
    public void addListener(ZmeListener l) {
    	mListeners.addElement(l);
    }
    
    public void removeListener(ZmeListener l) {
    	mListeners.removeElement(l);
    }
    
    public String getUsername() {
        return mUname;
    }

    public void setUsername(String uname) {
    	if (uname.compareTo(mUname) != 0) {
	    	mUname = uname;
	        mDirty = true;
	        callListeners();
    	}
    }
 
    public String getAuthToken() {
    	return mAuthToken;
    }

    public void setAuthToken(String authToken) {
    	if (authToken.compareTo(mAuthToken) != 0) {
	    	mAuthToken = authToken;
	        mDirty = true;
	        callListeners();
    	}
   }
    
    public String getServerUrl() {
    	return mServerURL;
    }

    public void setServerUrl(String serverUrl) {
    	if (serverUrl.compareTo(mServerURL) != 0) {
	    	mServerURL = serverUrl;
	        mDirty = true;
	        callListeners();
    	}
   }
    
    public String getPassword() {
    	return mPword;
    }

    public void setPassword(String pword) {
    	if (pword.compareTo(mPword) != 0) {
	    	mPword = pword;
	        mDirty = true;
	        callListeners();
    	}
    }

    public boolean getKeepSignedIn() {
        return mKeepSignedIn;
    }

    public void setKeepSignedIn(boolean keepSignedIn) {
    	if (keepSignedIn != mKeepSignedIn) {
	    	mKeepSignedIn = keepSignedIn;
	    	mDirty = true;
	        callListeners();
    	}
    }

    public boolean getPreloadContacts() {
        return mPreloadContacts;
    }

    public void setPreloadContacts(boolean preload) {
    	if (preload != mPreloadContacts) {
	    	mPreloadContacts = preload;
	    	mDirty = true;
	        callListeners();
    	}
    }
    
    public boolean getCacheContacts() {
        return mCacheContacts;
    }

    public void setCacheContacts(boolean cacheContacts) {
    	if (cacheContacts != mCacheContacts) {
    		mCacheContacts = cacheContacts;
    		mDirty = true;
	        callListeners();
    	}
    }

	public boolean getDelWOCConv() {
		return mDelWOCConv;
	}

	public void setDelWOCConv(boolean delWOCConv) {
		if (delWOCConv != mDelWOCConv) {
			mDelWOCConv = delWOCConv;
			mDirty = true;
	        callListeners();
		}
	}

	public boolean getDelWOCMsg() {
		return mDelWOCMsg;
	}

	public void setDelWOCMsg(boolean delWOCMsg) {
		if (delWOCMsg != mDelWOCMsg) {
			mDelWOCMsg = delWOCMsg;
			mDirty = true;
	        callListeners();
		}
	}

	public boolean getDelWOConf() {
		return mDelWOConf;
	}

	public void setDelWOConf(boolean delWOConf) {
		if (delWOConf != mDelWOConf) {
			mDelWOConf = delWOConf;
			mDirty = true;
	        callListeners();
		}
	}
	
	public boolean getShowConvTicker() {
		return mShowConvTicker;
	}

	public void setShowConvTicker(boolean showConvTicker) {
		if (showConvTicker != mShowConvTicker) {
			mShowConvTicker = showConvTicker;
			mDirty = true;
	        callListeners();
		}
	}
	
	public boolean getShowMsgTicker() {
		return mShowMsgTicker;
	}

	public void setShowMsgTicker(boolean showMsgTicker) {
		if (showMsgTicker != mShowMsgTicker) {
			mShowMsgTicker = showMsgTicker;
			mDirty = true;
	        callListeners();
		}
	}
	
	public boolean getShowApptTicker() {
		return mShowApptTicker;
	}

	public void setShowApptTicker(boolean showApptTicker) {
		if (showApptTicker != mShowApptTicker) {
			mShowApptTicker = showApptTicker;
			mDirty = true;
	        callListeners();
		}
	}

	public int getTickerSpeed() {
		return mTickerSpeed;
	}

	public void setTickerSpeed(int tickerSpeed) {
		if (tickerSpeed != mTickerSpeed) {
			mTickerSpeed = tickerSpeed;
			mDirty = true;
	        callListeners();
		}
	}
	
	public void setShortcut(Shortcut s) {
		int button = s.button;
		mShortcuts[button] = s;
	}
	
	public Shortcut getShortcut(int button) {
		return mShortcuts[button];
	}

    public Shortcut[] getShortcuts() {
        return mShortcuts;
    }
    
    public void flush() 
			throws IOException {
		if (mDirty) {
			//#debug
			System.out.println("Flushing Settings...");
			mRmsStore.save(this, MY_NAME);
			mDirty = false;
			//#debug
			System.out.println("Settings Saved");
			//#debug
			System.out.println(toString());
		}
    }
    
    private void callListeners() {
    	for (Enumeration e = mListeners.elements(); e.hasMoreElements(); ) {
    		ZmeListener l = (ZmeListener)e.nextElement();
    		l.action(this, null);
    	}
    }
}
