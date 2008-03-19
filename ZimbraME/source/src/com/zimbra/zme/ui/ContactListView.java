/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.ui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Contact;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ContactListView extends View implements ResponseHdlr, ItemStateListener {
	
	public static final int PICKER_STYLE = 1; // For picking addresses
	public static final int LIST_STYLE = 2; // For contact search results
	
	private static final Command NEW = new Command(Locale.get("addressPicker.New"), Command.ITEM, 1);
	private static final Command DONE = new Command(Locale.get("addressPicker.Done"), Command.CANCEL, 1);
	private static final Command OK = new Command(Locale.get("main.Ok"), Command.OK, 1);
	
	//#if (${bytes(polish.HeapSize)} >= ${bytes(1MB)}) or (polish.HeapSize == dynamic)
	//# private static final int MAX_TO_DISPLAY = 100;
	//#else
	private static final int MAX_TO_DISPLAY = 20;
	//#endif
	
	private TextBox mNewAddrTB;
	private ContactListSearchItem mCLSearchItem;
	private Hashtable mEmailAddrToContact;
	private StringItem mHeader;
	private ZmeStringItem mMsgItem;
	private ZmeListener mListener;
	private int mListStyle;
	private boolean mContactsLoaded;
	private Contact[] mTmpList;
	private boolean mLoading;
	
	//#ifdef polish.usePolishGui
		public ContactListView(ZimbraME midlet,
							   int listStyle,
							   Style style) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null, style);
			//#endif
			init(listStyle);
		}
	//#else
		public ContactListView(ZimbraME midlet,
				   			   int listStyle) {
			super(midlet);
			//#if true
				//# mView = new FramedForm(null);
			//#endif
			init(listStyle);
		}
	//#endif

	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("ContactListView.handleResponse");
		mLoading = false;
		//#style ContactListViewHeader
		UiAccess.setStyle(mHeader);
	
		if (resp instanceof Mailbox) {
			//#debug 
			System.out.println("ContactListView.handleResponse: loading contacts successful");
			loadContacts();
			
			if (mMidlet.mDisplay.getCurrent() != mView)
				Dialogs.showStatusMsg("Contacts Loaded", (Screen)mMidlet.mDisplay.getCurrent(), true);
		} else {
			mMidlet.handleResponseError(resp, this);
		}		
	}

	/**
	 * Register the change listener with this view. The change listener is only called if this
	 * is a PICKER_STYLE contact list view. It is called when the user invokes the done command
	 * @param l
	 */
	public void setDoneListener(ZmeListener l) {
		mListener = l;
	}
		
	public Vector getSelectedContacts() {
		if (mLoading)
			return null;
		
		Vector selected = null;
		
		for (Enumeration e = mMidlet.mMbox.mContacts.elements(); e.hasMoreElements(); ) {
			Contact c = (Contact)e.nextElement();
			if (c.mSelected) {
				if (selected == null)
					selected = new Vector();
				selected.addElement(c);
			}
		}
		return selected;
	}
	
	public Vector getContactsForEmailAddrs(String[] emailAddrs) {
		
		if (mLoading || emailAddrs == null)
			return null;
		
		Vector items = new Vector();
		Contact c;
		
		for (int i = 0; i < emailAddrs.length && emailAddrs[i] != null; i++) {
			c = (Contact)mEmailAddrToContact.get(emailAddrs[i]);
			if (c == null) {
				c = new Contact();
				c.mEmail = emailAddrs[i];
			} 
			items.addElement(c);
		}	
		return items;
	}
	
	public void reset(Vector contacts) {
		if (mLoading)
			return;
		
		if (mMidlet.mMbox.mContacts != null)
			for (Enumeration e = mMidlet.mMbox.mContacts.elements(); e.hasMoreElements();)
				((Contact)e.nextElement()).mSelected = false;
		
		if (contacts != null) {
			for (Enumeration e = contacts.elements(); e.hasMoreElements();)
				((Contact)e.nextElement()).mSelected = true;
		}
		
		mCLSearchItem.reset();
	}
	
	public void setCurrent() {
		if (!mContactsLoaded && !mLoading) {
			load();
			return;
		}
		mMidlet.mDisplay.setCurrent(mView);
	}
	
	public void load() {
		preload();
		mMidlet.mDisplay.setCurrent(mView);
	}
	
	public void preload() {
		if (!mContactsLoaded && !mLoading) {
			mLoading = true;
			mView.deleteAll();
			mMsgItem.setText(Locale.get("main.LoadingContacts"));
			mView.append(mMsgItem);
			//#style ContactListViewHeaderBusy
			UiAccess.setStyle(mHeader);
			mMidlet.mMbox.getContacts(this);
		}
	}

	protected void keyPressed(int keyCode,
			   			   	  int gameAction,
			   			   	  Item item) {
		/* HACK ALERT
		 * Since ContactListItems may appear in the edit form of a AddrEntryForm, we need to be sure that
		 * we only react to keypresses in those ContactListItem's when we are visible
		 */
		if (mView.isShown())
			mCLSearchItem.addKeyPress(keyCode);
	}
	
	public void commandAction(Command cmd, 
			  				  Displayable d) {
		if (d == mView) {
			if (cmd == DONE) {
				mListener.action(this, null);
				setNextCurrent();
			} else if (cmd == NEW) {
				mNewAddrTB = new TextBox(null, null, 255, TextField.EMAILADDR);
				mNewAddrTB.addCommand(CANCEL);
				mNewAddrTB.addCommand(OK);
				mNewAddrTB.setCommandListener(this);
				mMidlet.mDisplay.setCurrent(mNewAddrTB);
			}
		} else if (d == mNewAddrTB) {
			if (cmd == OK) {
				String addr = mNewAddrTB.getString();
				if (addr != null && addr.length() > 0) {
					Contact c = (Contact)mEmailAddrToContact.get(addr);
					if (c == null) {
						c = new Contact();
						c.mEmail = mNewAddrTB.getString();
						c.mSelected = true;
						c.mNew = true;
						mEmailAddrToContact.put(addr, c);
						mMidlet.mMbox.addContact(c);
					} else {
						c.mSelected = true;
					}
					itemStateChanged(null);
				}
			}
			mNewAddrTB = null;
			mMidlet.mDisplay.setCurrent(mView);
		}
	}

	//TODO Modify to support LIST_STYLE
	public void itemStateChanged(Item item) {
		String stem = mCLSearchItem.getText();
		if (stem.length() == 0 || mMidlet.mMbox.mContacts == null)
			updateView("");
		else
			updateView(stem);
	}

	private void init(int listStyle) {
		mListStyle = listStyle;
		
		FramedForm form = null;
		//#if true
			//# form = (FramedForm)mView;
		//#endif
	
		
		//#style CLSearchItem
		mCLSearchItem = new ContactListSearchItem(mMidlet, this);	

		//#style NoResultItem
		mMsgItem = new ZmeStringItem(mMidlet, this, Locale.get("main.NoSearchResultsMatched"));
	
		if (mListStyle == PICKER_STYLE) {
			//#style ContactListViewHeader
			mHeader = new StringItem(null, Locale.get("contactListView.ContactPicker"));
		} else {
			//#style ContactListViewHeader
			mHeader = new StringItem(null, Locale.get("contactListView.Contacts"));		
		}

		form.append(Graphics.TOP, mHeader);
		form.append(Graphics.TOP, mCLSearchItem);
		
		mEmailAddrToContact = new Hashtable();
		mTmpList = new Contact[MAX_TO_DISPLAY];
		
		form.setItemStateListener(this);
		form.setCommandListener(this);
		
		form.addCommand(NEW);
		form.addCommand(DONE);
	}
	
	private void loadContacts() {
		mContactsLoaded = true;
		updateView(null);
	}
	
	private void updateView(String stem) {
		int cnt = 0;
		boolean overflow = false;	
		Contact c;
		if (mMidlet.mMbox.mContacts != null) {
			for (Enumeration e = mMidlet.mMbox.mContacts.elements() ; e.hasMoreElements() && !overflow;) {
				c = (Contact)e.nextElement();
				
				if ((c.mFirstName != null && (stem == null || c.mFirstName.toLowerCase().startsWith(stem)))
						|| (c.mLastName != null && (stem == null || c.mLastName.toLowerCase().startsWith(stem)))) {
					mTmpList[cnt++] = c;
					if (cnt >= mTmpList.length) {
						overflow = true;
						break;
					}
				} else if ((stem == null || c.mEmail.toLowerCase().startsWith(stem))) {
					mTmpList[cnt++] = c;
					if (cnt >= mTmpList.length) {
						overflow = true;
						break;
					}
				}
			}
		}

		FramedForm f = null;
		//#if true
			//# f = (FramedForm)mView;
		//#endif
	
		f.deleteAll();
		f.append(Graphics.TOP, mHeader);
		f.append(Graphics.TOP, mCLSearchItem);
		if (cnt == 0) {
			mMsgItem.setText(Locale.get("contactListView.NoContacts"));
			f.append(mMsgItem);
		} else if (!overflow) {
			ContactListItem cli;
			for (int i = 0; i < cnt; i++) {
				//#style ContactListItem
				cli = new ContactListItem(mMidlet, mTmpList[i], this, ContactListItem.PICKER);
				cli.setChecked(mTmpList[i].mSelected);
				f.append(cli);
			}
		} else {
			mMsgItem.setText(Locale.get("contactListView.TooMany"));
			f.append(mMsgItem);
		}
		f.focus(0);			
	}
}
