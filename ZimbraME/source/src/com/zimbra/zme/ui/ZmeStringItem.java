/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

/**
 * @class
 * This class provides a multi-line StringItem class that also accepts user keypresses and passes
 * then up to the parent view. This allows for keyboard shortcuts to work in for example empty lists.
 * 
 * @author Ross Dargahi
 */

package com.zimbra.zme.ui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.Style;
import de.enough.polish.util.TextUtil;

public class ZmeStringItem extends ZmeCustomItem {

	private static final int SPACING = 2;
	
	private String mText;
	private String[] mTextLines;
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;
	private View mParentView;
	private boolean mDirty;
	private int mLastW;
	
	//#ifdef polish.usePolishGui
		public ZmeStringItem(ZimbraME m,
						 	 View parentView,
						 	 String text,
						 	 Style style) {
			//#if true
				//# super(m, style);
			//#else
				super(m);
			//#endif
			setText(text);
			mParentView = parentView;
		}
	//#else
		public ZmeStringItem(ZimbraME m,
		 		 			 View parentView,
		 		 			 String text) {
			super(m);
			setText(text);
			mParentView = parentView;
		}
	//#endif
	
	public void setText(String text) {
		mText = text;
		mDirty = true;
	}
	
	protected void handleKeyPress(int keyCode) {
		mParentView.keyPressed(keyCode, getGameAction(keyCode), this);
	}

	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		if (mTextLines != null) {
			int h = -SPACING;
			for (int i = 0; i < mTextLines.length; i++)
				h += mFontHeight + SPACING;
			return h;
		} else {
			return mFontHeight;
		}
	}

	protected int getPrefContentWidth(int height) {
		Displayable curr = mMidlet.mDisplay.getCurrent();
		if (curr != null)
			return mMidlet.mDisplay.getCurrent().getWidth();
		else
			return 40;
	}


	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {
		if (mDirty || w != mLastW) {
			mLastW = w;
			mDirty = false;
			if (mText != null)
				mTextLines = TextUtil.wrap(mText, mFont, w, w);
			invalidate();
		}
		g.setFont(mFont);
		g.setColor(mFontColor);
		if (mTextLines != null) {
			int cursor = 0;
			for (int i = 0; i < mTextLines.length; i++) {
				g.drawString(mTextLines[i], (w - mFont.stringWidth(mTextLines[i])) / 2, cursor, Graphics.TOP | Graphics.LEFT);
				cursor += SPACING + mFontHeight;
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
		mDirty = true;
	}	
}
