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
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Contact;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;

public class AddrEntryItem extends CustomItem implements ZmeListener, CommandListener {
	public static final int NEW_MODE = 1;
	public static final int EDIT_MODE = 2;
	
	private static final Command NEW = new Command(Locale.get("addressPicker.New"), Command.ITEM, 1);
	private static final Command DONE = new Command(Locale.get("addressPicker.Done"), Command.CANCEL, 1);
	
	private static final int SPACING = 2;
	
	private ZimbraME mMidlet;
	private Vector mCLItems;
	private Vector mContacts;
	private String[] mAddrs;
	private Font mFont;
	private int mFontColor;
	private int mMode;
	private FramedForm mEditForm;
	
	//#ifdef polish.usePolishGui
		public AddrEntryItem(ZimbraME m,
	            Style style) {
			//#if true
				//# super("", style);
			//#else
				super("");
			//#endif
			init(m);
		}
	//#else
		public AddrEntryItem(ZimbraME m) {
			super("");
			init(m);
		}
	//#endif
		
	public void setMode(int mode) {
		mMode = mode;	
		if (mode == EDIT_MODE && mEditForm == null) {
			//#style AddrEntryEditForm
			mEditForm = new FramedForm(null);
			
			//#style AddrEntryEditFormHeader
			StringItem si = new StringItem(null, Locale.get("addrEntryEdit.EditRecipients"));
			mEditForm.append(Graphics.TOP, si);
			
			mEditForm.addCommand(NEW);
			mEditForm.addCommand(DONE);
			mEditForm.setCommandListener(this);
		}
	}
	
	public void setAddresses(String[] addrs) {
		mContacts = mMidlet.getContactPickerListView().getContactsForEmailAddrs(addrs);
		createAddrs();
		invalidate();
		notifyStateChanged();
	}
	
	public Vector getContacts() {
		return (mContacts == null || mContacts.size() == 0) ? null : mContacts;
	}
	
	protected void keyPressed(int keyCode) {
		if (keyCode != Canvas.KEY_NUM5 && getGameAction(keyCode) == Canvas.FIRE) {
			if (mMode == NEW_MODE) {
				ContactListView clv = mMidlet.getContactPickerListView();
				clv.setDoneListener(this);
				clv.setNext(this);
				clv.reset(mContacts);
				clv.setCurrent();
			} else {
				/* If we are in edit mode and have some items in the field, then show the edit form, else
				 * behave like new mode */
				mEditForm.deleteAll();
				if (mContacts != null && mContacts.size() > 0) {
					createConvListItems(mContacts, false, true);
					ContactListItem cli;
					for (Enumeration e = mCLItems.elements(); e.hasMoreElements(); ) {
						cli = (ContactListItem)e.nextElement();
						cli.setChecked(true);
						mEditForm.append(cli);
					}
					mMidlet.mDisplay.setCurrent(mEditForm);
				} else {
					mMode = NEW_MODE;
					keyPressed(keyCode);
				}
			}
		}
	}

	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		int fontHeight = mFont.getHeight();
		if (mAddrs != null) {
			int h = 0;
			for (int i = 0; i < mAddrs.length; i++)
				h += fontHeight + SPACING;
			return h; 
		} else {
			return fontHeight;
		}
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}


	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {		
		g.setFont(mFont);
		g.setColor(mFontColor);
		
		if (mAddrs != null) {
			int cursor = 0;
			int fontHeight = mFont.getHeight();
			for (int i = 0; i < mAddrs.length; i++) {
				g.drawString(Util.elidString(mAddrs[i], w, mFont), SPACING, cursor, Graphics.TOP | Graphics.LEFT);
				cursor += SPACING + fontHeight;
			}
		} else {
			g.drawString(" ", SPACING, 0, Graphics.TOP | Graphics.LEFT);
		}
	}

	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontColor = style.getFontColor();
	}

	/* This method is called from ContactListView when the user has invoked the DONE command*/
	public void action(Object source,
					   Object data) {
        Vector contacts = mMidlet.getContactPickerListView().getSelectedContacts();
        if (contacts == null)
            return;
		if (mMode == NEW_MODE) {
		    setMode(EDIT_MODE);
		    mContacts = contacts;
			createAddrs();
			invalidate();
		} else {
			createConvListItems(contacts, true, true);
			for (Enumeration e = contacts.elements(); e.hasMoreElements(); )
				mContacts.addElement(e.nextElement());
			mEditForm.deleteAll();
			ContactListItem cli;
			for (Enumeration e = mCLItems.elements(); e.hasMoreElements(); ) {
				cli = (ContactListItem)e.nextElement();
				mEditForm.append(cli);
			}
		}
	}	

	/* This method is the commandAction handler for the edit form */
	public void commandAction(Command cmd, 
			  				  Displayable d) {
		if (cmd == DONE) {
			Vector tmp = new Vector();
			ContactListItem cli;
			for (Enumeration e = mCLItems.elements(); e.hasMoreElements(); ) {
				cli = (ContactListItem)e.nextElement();
				if (cli.getChecked())
					tmp.addElement(cli.mContact);
			}
			mContacts = tmp;
			createAddrs();
			invalidate();
			mMidlet.mDisplay.setCurrentItem(this);
		} else if (cmd == NEW) {
			ContactListView clv = mMidlet.getContactPickerListView();
			clv.setDoneListener(this);
			clv.setNext(mEditForm);
			// Don't want anything checked
			clv.reset(null);
			clv.setCurrent();
		}
	}

	private void init(ZimbraME m) {
		mMidlet = m;
		mMode = NEW_MODE;
		mCLItems = new Vector();		
	}
	
	private void createAddrs() {
		if (mContacts == null || mContacts.size() == 0) {
			mAddrs = null;
			return;
		} else {
			int size = mContacts.size();;
			mAddrs = new String[size];
			Contact c;
			for (int i = 0; i < size; i++) {
				c = (Contact)mContacts.elementAt(i);
				//#if (${bytes(polish.HeapSize)} >= ${bytes(1MB)}) or (polish.HeapSize == dynamic)	
					StringBuffer s = new StringBuffer();
					if (c.mFirstName != null)
						s.append(c.mFirstName);
					
					if (c.mLastName != null) {
						if (s.length() > 0)
							s.append(" ").append(c.mLastName);
						else
							s.append(c.mLastName);
					}
					if (s.length() > 0) {
						s.append(" <").append(c.mEmail).append(">");
						mAddrs[i] = s.toString();
					} else
				//#endif
				mAddrs[i] = c.mEmail;
			}
		}
	}
	
	private void createConvListItems(Vector contacts,
									 boolean append,
									 boolean setChecked) {
		if (mContacts == null)
			return;
		
		if (!append)
			mCLItems.removeAllElements();
		
		ContactListItem cli;
		Contact c;
        for (Enumeration e = contacts.elements(); e.hasMoreElements(); ) {
        	c = (Contact)e.nextElement();
			//#style ContactListItem
			cli = new ContactListItem(mMidlet, c, null, ContactListItem.PICKER); 
			if (setChecked)
				cli.setChecked(true);
			mCLItems.addElement(cli);
        }		
	}

}
