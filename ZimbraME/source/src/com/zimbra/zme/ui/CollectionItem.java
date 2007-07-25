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
import com.zimbra.zme.client.Attachment;
import com.zimbra.zme.client.Folder;
import com.zimbra.zme.client.SavedSearch;
import com.zimbra.zme.client.Tag;

import de.enough.polish.ui.Style;

public class CollectionItem extends CustomItem {

	private static final int SPACING = 2;
	
	private static Image CHECKBOX_ICON;
	private static Image CHECKBOXCHECKED_ICON;
	private static int CHECKBOX_ICON_WIDTH;

	{
		try {
			CHECKBOX_ICON = Image.createImage("/CheckBox.png");
			CHECKBOXCHECKED_ICON = Image.createImage("/CheckBoxChecked.png");
			CHECKBOX_ICON_WIDTH = CHECKBOX_ICON.getWidth();
		} catch (IOException e) {
			//#debug
			System.out.println("SavedSearchItem.init: IOException " + e);
		}
	}

	public SavedSearch mSavedSearch;
	public Tag mTag;
	public Attachment mAttachment;
    public Folder mFolder;
	private boolean mSelectable;
	private boolean mSelected;
	private ZimbraME mMidlet;
	private View mParentView;
	private Font mFont;
	private int mFontColor;

	//#ifdef polish.usePolishGui
		public CollectionItem(ZimbraME m,
							  View parentView,
						      SavedSearch ss,
						      boolean selectable,
						      Style style) {

			//#if true
				//# super("", style);
			//#else
				super("");
			//#endif
			mMidlet = m;
			mParentView = parentView;
			mSavedSearch = ss;
			mSelectable = selectable;
		}

		public CollectionItem(ZimbraME m,
							  View parentView,
							  Tag tag,
							  boolean selectable,
							  Style style) {
			//#if true
				//# super("", style);
			//#else
				super("");
			//#endif
			mMidlet = m;
			mParentView = parentView;
			mTag = tag;
			mSelectable = selectable;
		}
		
		public CollectionItem(ZimbraME m,
							  View parentView,
							  Attachment attachment,
							  boolean selectable,
							  Style style) {
			//#if true
				//# super("", style);
			//#else
				super("");
			//#endif
			mMidlet = m;
			mParentView = parentView;
			mAttachment = attachment;
			mSelectable = selectable;
		}
		
        public CollectionItem(ZimbraME m,
                  View parentView,
                  Folder folder,
                  boolean selectable,
                  Style style) {
            //#if true
                //# super("", style);
            //#else
                super("");
            //#endif
                mMidlet = m;
                mParentView = parentView;
                mFolder = folder;
                mSelectable = selectable;
}
	//#else	
		
		public CollectionItem(ZimbraME m,
							  View parentView,
							  SavedSearch ss,
							  boolean selectable) {
			super("");
		}
		
		public CollectionItem(ZimbraME m,
				  			  View parentView,
							  Tag tag,
							  boolean selectable) {
			super("");
		}
		
		public CollectionItem(ZimbraME m,
				  			  View parentView,
							  Attachment attachment,
							  boolean selectable) {
			super("");
		}
		
        public CollectionItem(ZimbraME m,
                  View parentView,
                  Folder f,
                  boolean selectable) {
            super("");
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
	
	protected void keyPressed(int keyCode) {
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
		//#if true
			//# return Math.max(style.font.getHeight(), CHECKBOX_ICON.getHeight());
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

		if (mSelectable) {
			if (mSelected)
				g.drawImage(CHECKBOXCHECKED_ICON, 0, 0, Graphics.TOP | Graphics.LEFT);
			else
				g.drawImage(CHECKBOX_ICON, 0, 0, Graphics.TOP | Graphics.LEFT);
			w -= (SPACING + CHECKBOX_ICON_WIDTH);
		}

		String str = null;
		if (mSavedSearch != null) {
			str = Util.elidString(mSavedSearch.mName, w, mFont);
		} else if (mTag != null) {
			str = Util.elidString(mTag.mName, w, mFont);
		} else if (mAttachment != null) {
			str = Util.elidString(mAttachment.mFilename, w, mFont);
		} else if (mFolder != null) {
            str = Util.elidString(mFolder.mName, w, mFont);
        }
		g.drawString(str, CHECKBOX_ICON_WIDTH + SPACING, 0, Graphics.TOP | Graphics.LEFT);				
	}

	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontColor = style.getFontColor();
	}	
}
