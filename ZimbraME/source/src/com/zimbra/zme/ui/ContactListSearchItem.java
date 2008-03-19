/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

public class ContactListSearchItem extends CustomItem {
	
	private static final char[] KEY2_CHARS = {'a', 'b', 'c', '2'};
	private static final char[] KEY3_CHARS = {'d', 'e', 'f', '3'};
	private static final char[] KEY4_CHARS = {'g', 'h', 'i', '4'};
	private static final char[] KEY5_CHARS = {'j', 'k', 'l', '5'};
	private static final char[] KEY6_CHARS = {'m', 'n', 'o', '6'};
	private static final char[] KEY7_CHARS = {'p', 'q', 'r', 's', '7'};
	private static final char[] KEY8_CHARS = {'t', 'u', 'v', '8'};
	private static final char[] KEY9_CHARS = {'w', 'x', 'y', 'z', '9'};
	
	private static final String SEARCH = Locale.get("main.Search") + ":";
	private static final int REPEAT_DELAY = 750; //msec
	private static final int PADDING = 2;
	private static final int MAX_LEN = 10;
	
	private ZimbraME mMidlet;
	private StringBuffer mText;
	private long mLastPressTime;
	private int mLastKeyCode;
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;
	private int mLabelLen;
	
	//#ifdef polish.usePolishGui
		public ContactListSearchItem(ZimbraME m,
						 			 View parentView,
						 			 Style style) {
			//#if true
				//# super("", style);
			//#else
				super("");
			//#endif
			
			init(m, parentView);
		}
	//#else
		public ContactListSearchItem(ZimbraME m,
	 		     					 View parentView) {
			super("");
			init(m, parentView);
		}
	//#endif

	private void init(ZimbraME m,
					  View parentView) {
		mMidlet = m;
		mText = new StringBuffer();
		UiAccess.setAccessible(this, false);
	}
	
	public String getText() {
		return mText.toString();
	}
	
	public void reset() {
		mText.delete(0, mText.length());
		notifyStateChanged();
		repaint();
	}
	
	public void addKeyPress(int keyCode) {
		char c = 0;
		boolean directInput = false;
		//#ifdef polish.TextField.UseDirectInput
		//# directInput = true;
		//#endif
		int gameAction = getGameAction(keyCode);
		if (!directInput && keyCode != Canvas.KEY_NUM4 && gameAction == Canvas.LEFT) {
			if (mText.length() > 0) {
				mText.deleteCharAt(mText.length() - 1);
				notifyStateChanged();
			} else
				return;
		}
		//#ifdef polish.TextField.UseDirectInput
		//# c = (char)keyCode;
		//#else
		//# c = addNativeInputChar(keyCode);
		//#endif
		if (c != 0) {
			mText.append(c);
			notifyStateChanged();
		}
		repaint();
	}
	
	private char addNativeInputChar(int keyCode) {
		char c = 0;
		long now = (new Date()).getTime();
		int len = mText.length();
		if (len > MAX_LEN)
			return c;

		char last = (len != 0) ? mText.charAt(len - 1) : '*';
		switch(keyCode) {
		case Canvas.KEY_NUM0:
			c = '0';
			break;
		case Canvas.KEY_NUM1:
			c = '1';
			break;
		case Canvas.KEY_NUM2:
			c = calcKeyChar(KEY2_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM3:
			c = calcKeyChar(KEY3_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM4:
			c = calcKeyChar(KEY4_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM5:
			c = calcKeyChar(KEY5_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM6:
			c = calcKeyChar(KEY6_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM7:
			c = calcKeyChar(KEY7_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM8:
			c = calcKeyChar(KEY8_CHARS, keyCode, last, now);
			break;
		case Canvas.KEY_NUM9:
			c = calcKeyChar(KEY9_CHARS, keyCode, last, now);
			break;
		default:
			break;
		}
		mLastPressTime = now;
		mLastKeyCode = keyCode;
		return c;
	}
	
	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		//#if true
			//# return style.font.getHeight() + PADDING * 2 + 1;
		//#else
			return 40;
		//#endif
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}


	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {
		g.setColor(255, 255, 255);
		g.fillRect(mLabelLen + PADDING * 2, PADDING, w - mLabelLen - PADDING * 2, mFontHeight + PADDING);
		g.setColor(0, 0, 0);
		g.drawRect(mLabelLen + PADDING * 2 - 1, PADDING - 1, w - mLabelLen - PADDING * 2, mFontHeight + PADDING);

		g.setFont(mFont);
		g.setColor(mFontColor);
		g.drawString(SEARCH, 0, (h - mFontHeight) / 2, Graphics.TOP | Graphics.LEFT);
		g.drawString(getText(), mLabelLen + PADDING * 3, PADDING , Graphics.TOP | Graphics.LEFT);
	}

	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontHeight = mFont.getHeight();
		mFontColor = style.getFontColor();
		mLabelLen = mFont.stringWidth(SEARCH);
	}	

	private char calcKeyChar(char[] targetChars,
							 int keyCode,
							 char last,
            				 long now) {
		int len = targetChars.length;
		if (mLastKeyCode == keyCode && (now - mLastPressTime) < REPEAT_DELAY) {
			for (int i = 0; i < len; i++) {
				if (targetChars[i] == last) {
					mText.delete(mText.length()-1, mText.length());
					return ((i == len - 1) ? targetChars[0] : targetChars[i + 1]);
				}
			}
		}
		return targetChars[0];
	}
}
