/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.io.IOException;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Mailbox;
import com.zimbra.zme.client.ZmeSvcException;

import de.enough.polish.util.Locale;

public class LoginView extends View implements ItemStateListener, ResponseHdlr {

	private static final int MAX_FIELD_LEN = 256;
	
	private static final Command SIGN_IN = new Command(Locale.get("login.Signin"), Command.SCREEN, 1);
	
	private TextField mServerUrlField; // Server url field. Only visible if not specified in the JAD
	private TextField mUnameField;
	private TextField mPwordField;
	private ChoiceGroup mKeepSignedInField;
	private StringItem mErrorText; 
	
	private boolean mSaveProfileInfo; // true - profile info needs saving

	
	/**
	 * Creates the login form for logging into the ZCS
	 * @param midlet
	 */
	public LoginView(ZimbraME midlet) {
		super(midlet);
		//#style LoginView
		mView = new Form(null);
		
		try {
			//#style LoginLogo 
			ImageItem ii = new ImageItem("", Image.createImage("/zimbraLogo.png"), ImageItem.LAYOUT_DEFAULT, null);
			mView.append(ii);
		} catch (IOException ex) {
			// Couldn't create logo. Oh well...
			//#debug
			System.out.println("LoginForm.LoginForm: Could not create logo");
		}

		// If the server URL is user specified, then provide the entry fields
		if (mMidlet.mUserServerUrl) {
			mView.append(Locale.get("login.ServerUrl"));
			//#style InputField
			mServerUrlField = new TextField("", null, MAX_FIELD_LEN, TextField.URL);
			mView.append(mServerUrlField);
		}
		
		mView.append(Locale.get("login.Uname"));
		//#style InputField
		mUnameField = new TextField("", null, MAX_FIELD_LEN, TextField.ANY);
		mView.append(mUnameField);
		
		mView.append(Locale.get("login.Pword"));
		//#style InputField
		mPwordField = new TextField("", null, MAX_FIELD_LEN, TextField.PASSWORD);
		mView.append(mPwordField);

		//#style ChoiceGroup
		mKeepSignedInField = new ChoiceGroup("", ChoiceGroup.MULTIPLE);
		//#style ChoiceItem
		mKeepSignedInField.append(Locale.get("login.KeepSignedIn"), null);
		mView.append(mKeepSignedInField);
	
		//#style LoginErrorText
		mErrorText = new StringItem("", "");
		mView.append(mErrorText);

		mView.addCommand(SIGN_IN);
		mView.addCommand(ZimbraME.EXIT);
		mView.setCommandListener(this);
		mView.setItemStateListener(this);
	}

	/**
	 * Called when the authtoken expires
	 * @param nextView The view that should be activated when login is successfull
	 */
	public void sessionExpired(View nextView) {
		mErrorText.setText(Locale.get("login.SessionExpired"));
		mMidlet.mMbox.mAuthToken = null;
		setNext(nextView);
		setCurrent();
	}
	
	public void setCurrent() {
		String serverUrl = null;
		boolean keepSignedIn = mMidlet.mSettings.getKeepSignedIn();
		
		if (keepSignedIn && mMidlet.mMbox.mAuthToken != null && mMidlet.mMbox.mAuthToken.length() > 0) {
			postAuth();
		} else {
			String uname = mMidlet.mSettings.getUsername();
			String pword = mMidlet.mSettings.getPassword();
			
			if (mServerUrlField != null) {
				if (mMidlet.mServerUrl != null)
					mServerUrlField.setString(mMidlet.mServerUrl);
				else
					mServerUrlField.setString("http://");
			} else {
				serverUrl = mMidlet.mServerUrl;
			}
			
			mUnameField.setString(uname);
			if (keepSignedIn)
				mPwordField.setString(pword);
			mKeepSignedInField.setSelectedIndex(0, keepSignedIn);
	
			// check to see if they are to be kept logged (note this field will only be set to true if the
			// profile was successfully loaded) and if so the uname/pword are specified. In this case 
			// we don't even need to render the form just go ahead and login.
			if (keepSignedIn && uname != null && uname.length() > 0 && serverUrl != null && serverUrl.length() > 0) {
				//Show the work in progress dialog
				Dialogs.popupWipDialog(mMidlet, this, Locale.get("login.LoggingIn"));
				mMidlet.mMbox.login(uname, pword, this);
				mSaveProfileInfo = false;
			} else {				
				mMidlet.mDisplay.setCurrent(mView);
				mSaveProfileInfo = true;
			}
	
			if (mServerUrlField != null) {
				//# focus(mServerUrlField);
			} else {
				//# focus(mUnameField);
			}
		}
	}
	
	public void commandAction(Command cmd, 
							  Displayable d) {
		if (d == mView) {
			if (cmd == SIGN_IN) {
				// Validate uname and pword. Just make sure there is data in them
				if (mUnameField.getString().compareTo("") == 0 || mPwordField.getString().compareTo("") == 0) {
					mErrorText.setText(Locale.get("login.FieldsNotEmpty"));
				} else if (mServerUrlField != null && mServerUrlField.getString().compareTo("") == 0) {
					// If the server URL is not specified in the JAD, then make sure that the user has
					// filled in the field
					mErrorText.setText(Locale.get("login.SvrFieldNotEmpty"));
				} else {
					// If the server URL field is present, it means the user has had to specify the server
					// URL. 
					if (mServerUrlField != null) {
					    String url = mServerUrlField.getString().toLowerCase();
                        if (url.endsWith("/"))
                            url = url.substring(0, url.length()-1);
                        if (!url.startsWith("http"))
                            url = "http://" + url;
						mMidlet.mMbox.mServerUrl = url + mMidlet.mServerSvcPath;
						mMidlet.mMbox.mSetAuthCookieUrl = mServerUrlField.getString() + ZimbraME.SET_AUTH_COOKIE_PATH;
						mMidlet.mMbox.mRestUrl = mMidlet.mServerUrl + ZimbraME.DEF_REST_PATH;
                    }
					//#debug
					System.out.println("LoginForm.commandAction: Initiating login");
					//Show the work in progress dialog
					Dialogs.popupWipDialog(mMidlet, this, Locale.get("login.LoggingIn"));
					mMidlet.mMbox.login(mUnameField.getString(), mPwordField.getString(), this);
                    mMidlet.mUsername = mUnameField.getString();
				}
			} else {
				// Delegate the command handling up to the midlet since
				mMidlet.commandAction(cmd, d);
			}
		} else if (d == Dialogs.mWipD) {
			cancel();
		}
	}
	
	/**
	 * This is the implementation for the ReponseHdlr interface handleResponse method. It 
	 * handles the reponse from server and is called form the Mailbox.run method
	 * @param op The operation object, in this case Mailbox.AUTH
	 * @param resp The response object. This can be an exception or in this case the Mailbox object
	 */
	public void handleResponse(Object op,
							   Object resp) {
		//#debug
		System.out.println("LoginForm.handleResponse");
		if (resp instanceof Mailbox) {
			if (op == Mailbox.AUTH) {
				//#debug 
				System.out.println("LoginForm.handleResponse: login successful");
				/* Log in is successful, install the next renderable item */			
				if (mSaveProfileInfo) {
					// Set this regardless since it has no bearing on the actual save succeeding
					mSaveProfileInfo = false;
					mMidlet.mSettings.setUsername(mUnameField.getString());
					mMidlet.mSettings.setPassword(mPwordField.getString());
		
					boolean[] b = new boolean[1];
					mKeepSignedInField.getSelectedFlags(b);
					mMidlet.mSettings.setKeepSignedIn(b[0]);
					if (mMidlet.mSettings.getKeepSignedIn())
						mMidlet.mSettings.setAuthToken(mMidlet.mMbox.mAuthToken);
					else
						mMidlet.mSettings.setAuthToken("");
					
					if (mMidlet.mUserServerUrl) {
						mMidlet.mSettings.setServerUrl(mServerUrlField.getString());
						mMidlet.mServerUrl = mServerUrlField.getString();
					}
				
					try {
						mMidlet.mSettings.flush();
					} catch (Exception ex) {
						//#debug
						System.out.println("LoginForm.handleReponse: *** FAILED TO SAVE PROFILE INFO ***");
						//TODO issue warning if failed - maybe via a ticker or warning dialog?
					}
				}
				postAuth();	
            }
		} else if (resp instanceof ZmeSvcException){
			Object ec = ((ZmeSvcException)resp).mErrorCode;
			if (ec == ZmeSvcException.ACCT_AUTHFAILED) {
				/* Login failed. Set form error message and return */
				mErrorText.setText(Locale.get("login.AuthFailed"));
				mMidlet.mDisplay.setCurrent(mView);
				mSaveProfileInfo = true; // Set to true since the user needs to change info
			} else {
				mMidlet.mMbox.mAuthToken = null;
				mMidlet.handleResponseError(resp, this);
			}
		} else {
			mMidlet.handleResponseError(resp, this);			
		}
	}

	public void cancel() {
		//#debug 
		System.out.println("LoginForm.cancel: Cancelling Login");
		mMidlet.mMbox.cancelOp();
		mMidlet.mDisplay.setCurrent(mView);
	}

	public void itemStateChanged(Item item) {
		//#ifndef polish.blackberry
			//# Form form = (Form)mView;
			//# if (item == mServerUrlField) {
				//# form.focus(mUnameField);
			//# } else if (item == mUnameField) {
				//# form.focus(mPwordField);
			//# }
		//#endif
	}	

	private void postAuth() {
		if (mMidlet.mSettings.getPreloadContacts()) {
			mMidlet.getContactPickerListView(null).load();
		}
		
		if (mNext instanceof View) {
			((View)mNext).load();
		}
	}
}

