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
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.ZmeListener;
import com.zimbra.zme.client.Contact;

//#ifdef polish.usePolishGui
//# import de.enough.polish.ui.FramedForm;
//#endif
import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;

public class AddrEntryItem extends CustomItem implements ZmeListener, CommandListener {
	public static final int NEW_MODE = 1;
	public static final int EDIT_MODE = 2;
	
	private static final Command NEW = new Command(Locale.get("addressPicker.New"), Command.ITEM, 1);
	private static final Command DONE = new Command(Locale.get("addressPicker.Done"), Command.CANCEL, 1);
	
	private static final int SPACING = 2;
	
	private ZimbraME mMidlet;
	private Vector mContacts;
	private String[] mAddrs;
	private Font mFont;
	private int mFontColor;
	private int mMode;
	private Form mEditForm;
	
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
			//#ifdef polish.usePolishGui
			//#style AddrEntryEditForm
			//# mEditForm = new FramedForm(null);
			//#else
			mEditForm = new Form(null);
			//#endif
			
			mEditForm.addCommand(NEW);
			mEditForm.addCommand(DONE);
			mEditForm.setCommandListener(this);
		}
	}
	
	public void setAddresses(String[] addrs) {
		for (int i = 0; i < addrs.length; i++) {
			Contact c = null;
			if (mMidlet.mMbox.mContacts != null) {
				for (Enumeration e = mMidlet.mMbox.mContacts.elements(); e.hasMoreElements(); ) {
					c = (Contact)e.nextElement();
					if (c.mEmail != null && c.mEmail.equalsIgnoreCase(addrs[i]))
						break;
					c = null;
				}
			}
			if (c == null) {
				c = new Contact();
				c.mEmail = addrs[i];
			}
			mContacts.addElement(c);
		}
		createAddrs();
		invalidate();
		notifyStateChanged();
	}
	
	public Vector getContacts() {
		if (mContacts == null || mContacts.size() == 0)
			return null;
		return mContacts;
	}
	
	protected void keyPressed(int keyCode) {
		if (keyCode != Canvas.KEY_NUM5 && getGameAction(keyCode) == Canvas.FIRE) {
			if (mContacts == null || mContacts.size() == 0)
				setMode(NEW_MODE);
			if (mMode == NEW_MODE) {
				CollectionView cv = mMidlet.getContactPickerListView(mMidlet.mDisplay.getCurrent());
				cv.setListener(this);
				cv.setCurrent();
			} else {
				showEditForm();
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
        Vector contacts = (Vector)data;
        if (contacts == null)
            return;
		if (mMode == NEW_MODE) {
		    setMode(EDIT_MODE);
		    mContacts = contacts;
			createAddrs();
			invalidate();
		} else {
			for (Enumeration e = contacts.elements(); e.hasMoreElements(); ) {
				Object obj = e.nextElement();
				if (!mContacts.contains(obj))
					mContacts.addElement(obj);
			}
			showEditForm();
		}
	}	

	private void showEditForm() {
		mEditForm.deleteAll();
		
		//#style AddrEntryEditFormHeader
		StringItem si = new StringItem(null, Locale.get("addrEntryEdit.EditRecipients"));
		//#ifdef polish.usePolishGui
		//# ((FramedForm)mEditForm).append(Graphics.TOP, si);
		//#else
		mEditForm.append(si);
		//#endif
		
		View v = mMidlet.getTopView();
		if (mContacts != null && mContacts.size() > 0) {
			for (Enumeration e = mContacts.elements(); e.hasMoreElements(); ) {
	            //#style CollectionItem
	            CollectionItem c = new CollectionItem(mMidlet, v, (Contact)e.nextElement(), true);
	            c.setSelected(true);
	            mEditForm.append(c);
			}
			mMidlet.mDisplay.setCurrent(mEditForm);
		}
	}
	
	/* This method is the commandAction handler for the edit form */
	public void commandAction(Command cmd, 
			  				  Displayable d) {
		if (cmd == DONE) {
			if (mEditForm != null) {
				mContacts.removeAllElements();
				for (int i = 0; i < mEditForm.size(); i++) {
					Item item = mEditForm.get(i);
					if (item instanceof CollectionItem) {
						CollectionItem ci = (CollectionItem) item;
						if (ci.getSelected())
							mContacts.addElement(ci.mItem);
						
					}
				}
			}
			createAddrs();
			invalidate();
			mMidlet.mDisplay.setCurrentItem(this);
		} else if (cmd == NEW) {
			CollectionView cv = mMidlet.getContactPickerListView(mMidlet.mDisplay.getCurrent());
			cv.setListener(this);
			cv.setCurrent();
		}
	}

	private void init(ZimbraME m) {
		mMidlet = m;
		mMode = NEW_MODE;
		mContacts = new Vector();		
	}
	
	private void createAddrs() {
		if (mContacts == null || mContacts.size() == 0) {
			mAddrs = null;
			return;
		} else {
			int size = mContacts.size();
			mAddrs = new String[size];
			Contact c;
			for (int i = 0; i < size; i++) {
				c = (Contact) mContacts.elementAt(i);
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
					mAddrs[i] = c.mEmail;
			}
		}
	}
}
