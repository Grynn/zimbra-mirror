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

package com.zimbra.zme.ui;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;

import de.enough.polish.ui.Style;

public class CollectionItem extends CustomItem {

	private static final int SPACING = 2;
	
	private static Image SAVEDSEARCH_ICON;
	private static int SAVEDSEARCH_ICON_WIDTH;

	{
		try {
			SAVEDSEARCH_ICON = Image.createImage("/SearchFolder.png");
			SAVEDSEARCH_ICON_WIDTH = SAVEDSEARCH_ICON.getWidth();
		} catch (IOException e) {
			//#debug
			System.out.println("SavedSearchItem.init: IOException " + e);
		}
	}

	public static final int SAVED_SEARCH = 1;
	public static final int TAG = 2;
	
	public int mType; // This collections type
	public String mId;
	public String mName;
	
	public String mQuery; // Saved search query
	public String mTypes; // Saved search types
	public String mSortBy; // Saved search sort by
	
	public String mColor; // Tag color
		
	private ZimbraME mMidlet;
	private Font mFont;
	private int mFontColor;

	public CollectionItem(ZimbraME m,
						  int type) {
		super("");
		mMidlet = m;
		mType = type;
	}

	public CollectionItem(ZimbraME m,
						  int type,
						  Style style) {
		//#if true
			//# super("", style);
		//#else
			super("");
		//#endif
		mMidlet = m;
		mType = type;
	}
	
	protected void keyPressed(int keyCode) {
		int gameAction = getGameAction(keyCode);
		
		switch(mType) {
			case SAVED_SEARCH: {
				if (keyCode != Canvas.KEY_NUM5 && gameAction == Canvas.FIRE)
					mMidlet.execSearch(mQuery, mSortBy, mTypes); 
				break;
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
		//#if true
			//# return Math.max(style.font.getHeight(), SAVEDSEARCH_ICON.getHeight());
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
		g.setFont(mFont);
		g.setColor(mFontColor);

		switch (mType) {
			case SAVED_SEARCH: {
				// Draw Icon
				g.drawImage(SAVEDSEARCH_ICON, 0, 0, Graphics.TOP | Graphics.LEFT);
				w -= (SPACING + SAVEDSEARCH_ICON_WIDTH);
				String str = Util.elidString(mName, w, mFont);
				g.drawString(str, SPACING + SAVEDSEARCH_ICON_WIDTH, 0, Graphics.TOP | Graphics.LEFT);
				break;
			}
		}
	}

	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontColor = style.getFontColor();
	}	
}
