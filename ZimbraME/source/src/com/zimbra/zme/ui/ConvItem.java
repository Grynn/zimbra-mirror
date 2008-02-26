/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;

//#if true
	//# import de.enough.polish.ui.FramedForm;
//#endif
import de.enough.polish.ui.Style;

public class ConvItem extends MailItem {

	private static int DEF_NUM_PARTICIPANTS = 5;
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;
	private Font mUnreadFont;
	private int mUnreadFontHeight;
	private Font mReadFont;
	private int mReadFontHeight;
	
	private String[] mParticipants; // Participants
	private int mNumParticipants;
	private String mParticipantsStr;
	private int mSubjDims;
	private int mNumMsgs;
	
	//#ifdef polish.usePolishGui
		public ConvItem(ZimbraME m,
						MailListView parentView,
						Style style) {
			super(m, parentView, style);
		}
	//#else
		public ConvItem(ZimbraME m,
						MailListView parentView) {
			super(m, parentView);
		}
	//#endif

	/**
	 * Add a participant to the conversation 
	 * @param participant
	 */
	public void addConvParticipant(String participant) {
		if (mParticipants == null) {
			mParticipants = new String[DEF_NUM_PARTICIPANTS];
			mNumParticipants = 0;
		} else if (mNumParticipants == mParticipants.length) {
			String[] newArray = new String[mNumParticipants + DEF_NUM_PARTICIPANTS];
			for (int i = 0; i < mNumParticipants; i++)
				newArray[i] = mParticipants[i];
			mParticipants = newArray;
		}
		mParticipants[mNumParticipants++] = participant;
		mDirty = true;
		if (mVisible) 
			repaint();	
	}

	/**
	 * The number of messages in a convesation. (Conv)
	 * @param numMsgs
	 */
	public void setNumMsgsInConv(int numMsgs) {
		mNumMsgs = numMsgs;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	protected void doSetState(boolean newState,
						  	  int stateBit) {
		if (stateBit == UNREAD) {
			if (newState) {
				mFlags = mFlags | UNREAD;
				mFont = mUnreadFont;
				mFontHeight = mUnreadFontHeight;
			} else {
				mFlags = mFlags & ~UNREAD;
				mFont = mReadFont;
				mFontHeight = mReadFontHeight;
			}
		} else {
			mFlags = (newState) ? mFlags | stateBit : mFlags & ~stateBit;
		}
		mDirty = true;
		invalidate();
		if (mVisible)
			repaint();
	}


	protected int getMinContentHeight() {
		return MINIMUM_CONTENT_HEIGHT;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		return mFontHeight * 2 + SPACING;
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}


	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {

		if (mNumPendingUpdates > 0) {
			drawUpdating(g, w, h, mFont, mFontColor);
			return;
		}

		/* there is lots of room for optimization here.*/
		if (mDirty) {
			if ((mFlags & UNREAD) == UNREAD) {
				mIcon = UNREADMSG_ICON;
				mIconWidth = mIcon.getWidth();
				mFont = mUnreadFont;
				mFontHeight = mUnreadFontHeight;
			} else {
				mIcon = READMSG_ICON;
				mIconWidth = mIcon.getWidth();
				mFont = mReadFont;
				mFontHeight = mReadFontHeight;
			}
			
			mDateWidth = mFont.stringWidth(mDateStr);
			String numMsgsStr = (mNumMsgs == 1) ? "" : " (" + mNumMsgs + ")";

			int newWidth = w - mDateWidth - SPACING * 2 - mFont.stringWidth(numMsgsStr); // minimum separation
			
			if ((mFlags & ATTACHMENT) == ATTACHMENT)
				newWidth -= (SPACING + ATTACH_ICON_WIDTH);

			mParticipantsStr = getParticipantsStr(mParticipants, mNumParticipants, newWidth, mFont) + numMsgsStr;
			
			mSubjDims = (mIcon != null) ? w - SPACING * 3 - mIconWidth : w - SPACING * 2;
			if ((mFlags & FLAGGED) == FLAGGED) {
				mSubjDims -= (SPACING + FLAG_ICON_WIDTH + SPACING);
			}
			
			mDirty = false;
		}

		g.setFont(mFont);
		g.setColor(mFontColor);

		// Draw the participants list
		g.drawString(mParticipantsStr, SPACING, 0, Graphics.TOP | Graphics.LEFT);
	
		// Draw the attachment icon right next to the dateif we have an attachment
		if ((mFlags & ATTACHMENT) == ATTACHMENT)
			g.drawImage(ATTACH_ICON, w - SPACING * 2 - mDateWidth, SPACING, Graphics.TOP | Graphics.RIGHT);
	
		// Draw the date to right-aligned
		g.drawString(mDateStr, w - SPACING, 0, Graphics.TOP | Graphics.RIGHT);
	
		// The second line of will display the status icon (e.g new, forwarded etc) and the subject.
		// It also shows if the message is flagged 
		// TODO show flagged
		g.drawImage(mIcon, SPACING, mFontHeight + SPACING, Graphics.TOP | Graphics.LEFT);
		g.drawString(Util.elidString(mSubject, mSubjDims, mFont), SPACING * 2 + mIconWidth , mFontHeight + SPACING, Graphics.TOP | Graphics.LEFT);

		if ((mFlags & FLAGGED) == FLAGGED) 
			g.drawImage(FLAG_ICON, w - SPACING, mFontHeight + SPACING, Graphics.TOP | Graphics.RIGHT);
	}

	protected void handleKeyPress(int keyCode) {
		int gameAction = getGameAction(keyCode);
		if (keyCode != Canvas.KEY_NUM5 && gameAction == Canvas.FIRE
			|| keyCode != Canvas.KEY_NUM6 && gameAction == Canvas.RIGHT) {
			mMidlet.getMsgListView().retrieveConv(mId, mSubject, mParentView, (mFlags & UNREAD) == UNREAD);
		} else if (keyCode == Canvas.KEY_NUM9 && !mParentView.mFragmentShowing 
				   && mFragment != null && mFragment.length() > 0) {
				Dialogs.showStatusMsg(mFragment, mParentView.mView, false);
				mParentView.mFragmentShowing = true;
				return;
		} else if (keyCode != Canvas.KEY_NUM8 && gameAction == Canvas.DOWN){
			ConvItem c = null;
			//#if true
				//# FramedForm f = (FramedForm)mParentView.mView;
				//# c = (ConvItem)f.get(f.size() - 1);
			//#endif
			if (c == this)
				mParentView.getMore(this);
		} else {
			mParentView.keyPressed(keyCode, gameAction, this);
		}
		mParentView.mFragmentShowing = false;
		Dialogs.hideScreenInfo();
	}

	/**
	 * This is called by the J2ME Polish infrastructure to set the style on the item
	 * @param style
	 */
	public void setStyle(Style style) {
		//#if true
		//# super.setStyle(style);
		//#endif

		if (mReadFont != style.font) {
			mReadFont = style.font;
			mReadFontHeight = mReadFont.getHeight();
			mUnreadFont = Font.getFont(mReadFont.getFace(), Font.STYLE_BOLD, mReadFont.getSize());
			mUnreadFontHeight = mUnreadFont.getHeight();
			invalidate();
		}

		// Set the default font to read font. This may be changed by the setUnread method
		if (mFont == null) {
			mFont = mReadFont;
			mFontHeight = mReadFontHeight;
		}

		mFontColor = style.getFontColor();
	}

}
