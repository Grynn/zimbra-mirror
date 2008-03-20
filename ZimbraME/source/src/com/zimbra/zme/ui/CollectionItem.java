/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
import com.zimbra.zme.client.MailboxItem;

import de.enough.polish.ui.Style;

public class CollectionItem extends ZmeCustomItem {

	private static final int SPACING = 2;
	
    private static Image LEFTARROW_ICON;
    private static Image RIGHTARROW_ICON;
	private static Image CHECKBOX_ICON;
	private static Image CHECKBOXCHECKED_ICON;
	private static int CHECKBOX_ICON_WIDTH;
	private static int CHECKBOX_ICON_HEIGHT;
    private static int ARROW_ICON_WIDTH;
    private static int ARROW_ICON_HEIGHT;

	{
		try {
			CHECKBOX_ICON = Image.createImage("/CheckBox.png");
			CHECKBOXCHECKED_ICON = Image.createImage("/CheckBoxChecked.png");
			CHECKBOX_ICON_WIDTH = CHECKBOX_ICON.getWidth();
			CHECKBOX_ICON_HEIGHT = CHECKBOX_ICON.getHeight();
			LEFTARROW_ICON = Image.createImage("/LeftArrow.png");
			RIGHTARROW_ICON = Image.createImage("/RightArrow.png");
			ARROW_ICON_WIDTH = LEFTARROW_ICON.getWidth();
			ARROW_ICON_HEIGHT = LEFTARROW_ICON.getHeight();
		} catch (IOException e) {
			//#debug
			System.out.println("CollectionItem: IOException " + e);
		}
	}

    public MailboxItem mItem;
	private boolean mSelectable;
	private boolean mSelected;
	private View mParentView;
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;

	//#ifdef polish.usePolishGui
		public CollectionItem(ZimbraME m,
							  View parentView,
						      MailboxItem item,
						      boolean selectable,
						      Style style) {

			//#if true
				//# super(m, style);
			//#else
				super(m);
			//#endif
			mParentView = parentView;
			mItem = item;
			mSelectable = selectable;
		}

	//#else	
		
		public CollectionItem(ZimbraME m,
							  View parentView,
							  MailboxItem item,
							  boolean selectable) {
			super(m);
		}
		
	//#endif
	
	public void setSelected(boolean selected) {
		if (mSelected != selected) {
			mSelected = selected;
			invalidate();
		}
	}
	
	public boolean getSelected() {
		return mSelected;
	}
	
	protected void handleKeyPress(int keyCode) {
		int gameAction = getGameAction(keyCode);
		if (mSelectable && gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
			setSelected(!getSelected());
		} else {
			mParentView.keyPressed(keyCode, gameAction, this);
		}
	}

	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		boolean hasDescription = mItem.getDescription() != null;
		if (hasDescription)
			return Math.max(mFontHeight * 2 + SPACING, CHECKBOX_ICON_HEIGHT);
		else
			return Math.max(mFontHeight, CHECKBOX_ICON_HEIGHT);
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}


	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {
		g.setFont(mFont);
		g.setColor(mFontColor);
        
        int offset = CHECKBOX_ICON_WIDTH + SPACING;

		if (mSelectable) {
			if (mSelected)
				g.drawImage(CHECKBOXCHECKED_ICON, 0, (h - CHECKBOX_ICON_HEIGHT) / 2, Graphics.TOP | Graphics.LEFT);
			else
				g.drawImage(CHECKBOX_ICON, 0, (h - CHECKBOX_ICON_HEIGHT) / 2, Graphics.TOP | Graphics.LEFT);
			w -= (SPACING + CHECKBOX_ICON_WIDTH);
		} else {
		    if (mItem.hasParent())
                g.drawImage(LEFTARROW_ICON, 0, (h - ARROW_ICON_HEIGHT) / 2, Graphics.TOP | Graphics.LEFT);
            if (mItem.hasChildren())
                g.drawImage(RIGHTARROW_ICON, w, (h - ARROW_ICON_HEIGHT) / 2, Graphics.TOP | Graphics.RIGHT);
            w -= (SPACING + ARROW_ICON_WIDTH * 2);
            offset = ARROW_ICON_WIDTH + SPACING;
        }
        

		String str = Util.elidString(mItem.getName(), w, mFont);
		g.drawString(str, offset, 0, Graphics.TOP | Graphics.LEFT);
		if (mItem.getDescription() != null) {
			str = Util.elidString(mItem.getDescription(), w, mFont);
			g.drawString(str, offset, SPACING + mFontHeight, Graphics.TOP | Graphics.LEFT);
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
}
