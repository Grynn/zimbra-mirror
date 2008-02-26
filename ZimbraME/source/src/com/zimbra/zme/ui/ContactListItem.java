/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007 Zimbra, Inc.
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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Contact;

import de.enough.polish.ui.Style;

public class ContactListItem extends ZmeCustomItem {

	public static final int PICKER = 1;
	public static final int SIMPLE = 2;
	
	public static final int SPACING = 2;
	
	private static Image UNCHECKED_ICON;
	private static Image CHECKED_ICON;
	private static int ICON_WIDTH;
	private static int ICON_HEIGHT;

	{
		try {
			UNCHECKED_ICON = Image.createImage("/CheckBox.png");
			CHECKED_ICON = Image.createImage("/CheckBoxChecked.png");
			ICON_WIDTH = CHECKED_ICON.getWidth();
			ICON_HEIGHT = CHECKED_ICON.getHeight();
		} catch (IOException e) {
			//#debug
			System.out.println("ContactListItem.init: IOException " + e);
		}
		
	}

	public Contact mContact;

	private Font mFont;
	private int mFontHeight;
	private int mFontColor;
	private View mParentView;
	private int mItemStyle;
	private String mNameStr;
	
	//#ifdef polish.usePolishGui
		public ContactListItem(ZimbraME m,
							   Contact c,
						 	   View parentView,
						 	   int itemStyle,
						 	   Style style) {
			//#if true
				//# super(m, style);
			//#else
				super(m);
			//#endif=
			init(parentView, itemStyle, c);
		}
	//#else
		public ContactListItem(ZimbraME m,
						   	   Contact c,
						   	   View parentView,
						   	   int itemStyle) {
			super(m);
			init(parentView, itemStyle, c);
		}
	//#endif
		
	public void setChecked(boolean checked) {
		if (mItemStyle == PICKER && checked != mContact.mSelected) {
			mContact.mSelected = checked;
			repaint();
		}
	}
	
	public boolean getChecked() {
		return mContact.mSelected;
	}
	
	public void setContact(Contact c) {
		mContact = c;
		
		//#if (${bytes(polish.HeapSize)} >= ${bytes(1MB)}) or (polish.HeapSize == dynamic)
			StringBuffer n = new StringBuffer();
			
			if (c.mFirstName != null)
				n.append(c.mFirstName);
			
			if (c.mLastName != null) {
				if (n.length() != 0)
					n.append(" ").append(c.mLastName);
				else
					n.append(c.mLastName);
			}
			
			if (n.length() != 0)
				mNameStr = n.toString();
			else 
				mNameStr = null;
		//#endif	
		invalidate();
	}

	
	protected void handleKeyPress(int keyCode) {
		if (mItemStyle == PICKER && keyCode != Canvas.KEY_NUM5 && getGameAction(keyCode) == Canvas.FIRE)
			setChecked(!mContact.mSelected);
		else if (mParentView != null)
			mParentView.keyPressed(keyCode, getGameAction(keyCode), this);
	}

	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		if (mItemStyle == SIMPLE)
			return mFontHeight;
		else {
			if (mNameStr != null)
				return Math.max(mFontHeight * 2 + SPACING, ICON_HEIGHT);
			else
				return Math.max(mFontHeight, ICON_HEIGHT);
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
		
		String str;

		if (mItemStyle == SIMPLE) {
			str = Util.elidString(mNameStr, w, mFont);
			g.drawString(str, 0, 0, Graphics.TOP | Graphics.LEFT);			
		} else {
			if (mContact.mSelected)
				g.drawImage(CHECKED_ICON, 0, (h - ICON_HEIGHT) / 2, Graphics.TOP | Graphics.LEFT);
			else
				g.drawImage(UNCHECKED_ICON, 0, (h - ICON_HEIGHT) / 2, Graphics.TOP | Graphics.LEFT);
			int spacing = ICON_WIDTH + SPACING * 3;
			w -= spacing;
			
			if (mNameStr != null) {
				str = Util.elidString(mNameStr, w, mFont);
				g.drawString(str, spacing, 0, Graphics.TOP | Graphics.LEFT);
				str = Util.elidString(mContact.mEmail, w, mFont);
				g.drawString(str, spacing, SPACING + mFontHeight, Graphics.TOP | Graphics.LEFT);
			} else {
				str = Util.elidString(mContact.mEmail, w, mFont);
				g.drawString(str, spacing, (ICON_HEIGHT > mFontHeight) ? (ICON_HEIGHT - mFontHeight) / 2 : 0, 
							 Graphics.TOP | Graphics.LEFT);
			}
			
		}
	}

	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontHeight = mFont.getHeight();
		mFontColor = style.getFontColor();
	}
	
	private void init(View parentView,
					  int itemStyle,
					  Contact c) {
		mParentView = parentView;
		mItemStyle = itemStyle;
		setContact(c);
	}
}
