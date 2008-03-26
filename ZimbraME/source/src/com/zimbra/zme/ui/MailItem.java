/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.zimbra.zme.ResponseHdlr;
import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Mailbox;

import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;
import de.enough.polish.util.StringTokenizer;

public abstract class MailItem extends ZmeCustomItem implements ResponseHdlr {
	
    public static final int MINIMUM_CONTENT_HEIGHT = 40;
    
	protected static final int SPACING = 2;

	protected static final int UNREAD = 0x01;
	protected static final int FLAGGED = 0x02;
	protected static final int ATTACHMENT = 0x04;
	protected static final int FORWARDED = 0x08;
	protected static final int REPLIED = 0x10;

	protected static Image UNREADMSG_ICON;
	protected static Image READMSG_ICON;
	protected static Image INVITE_ICON;
	protected static Image ATTACH_ICON;
	protected static int ATTACH_ICON_WIDTH;
	protected static Image FLAG_ICON;
	protected static int FLAG_ICON_WIDTH;


	protected static final Calendar mCalObj = Calendar.getInstance();
	protected static final Date mDateObj = new Date();

	{
		try {
			INVITE_ICON = Image.createImage("/MeetingRequest.png");
			FLAG_ICON = Image.createImage("/Flag.png");
			FLAG_ICON_WIDTH = FLAG_ICON.getWidth();
			UNREADMSG_ICON = Image.createImage("/Message.png");
			READMSG_ICON = Image.createImage("/ReadMessage.png");
			ATTACH_ICON = Image.createImage("/Attachment.png");
			ATTACH_ICON_WIDTH = ATTACH_ICON.getWidth();
		} catch (IOException e) {
			//#debug
			System.out.println("MailItem.init: IOException " + e);
		}
	}
		
	// Common attributes
	public String mFragment;
	public String mId;
	public String mSortField; 
	public String[] mTags;
    public String mFolderId;

	protected MailListView mParentView;
	protected String mSubject;
	protected long mSentDate;
	protected long mDate;
	protected String mDateStr;
	protected int mDateWidth;
	protected boolean mElided;
	protected int mFlags;
	protected Image mIcon;
	protected int mIconWidth;
	protected int mNumPendingUpdates; //Number of pending updates
	protected boolean mSelected; // Item is selected
	protected boolean mDirty; // Item is dirty due to field being changed
	protected boolean mVisible; // True: item is visible, else invisible
	
	private String mUpdatingText;
	private int mUpdatingContentLen;
	private String[] mNewTags;

	//#ifdef polish.usePolishGui
		protected MailItem(ZimbraME m,
						   MailListView parentView,
						   Style style) {
			//#if true
				//# super(m, style);
			//#else
				super(m);
			//#endif
		
			init(parentView);
		}
	//#else
		protected MailItem(ZimbraME m,
				   		   MailListView parentView) {
			super(m);
			init(parentView);
		}
	//#endif

	private void init(MailListView parentView) {
		mParentView = parentView;
	}

	/**
	 * Sets the item's subject
	 * @param subject
	 */
	public void setSubject(String subject) {
		mSubject = subject;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	public void toggleUnread(boolean update) {
		setUnread(!((mFlags & UNREAD) == UNREAD), update);
	}

	public void setUnread(boolean val,
						  boolean update) {
		setItemState(val, UNREAD, update);
	}

	public boolean getUnread() {
		return ((mFlags & UNREAD) == UNREAD);
	}

	public void toggleFlagged(boolean update) {
		setFlagged(!((mFlags & FLAGGED) == FLAGGED), update);
	}

	public void setFlagged(boolean val,
						   boolean update) {
		setItemState(val, FLAGGED, update);
	}
	
	public void setTags(String[] tagIds) {
		/* TODO Though we have the machinery in place for multiple concurrent updates on an item,
		 * there is some concurrency risk to this now that we are allowing for concurrent operations
		 * Need to do some more research/cleaup if we are to allow this*/
		 if (isUpdating())
			return;
		setUpdating(true, Locale.get("msgList.UpdatingItemTags"));
		mNewTags = tagIds;
		mMidlet.mMbox.tagItem(mId, tagIds, this);			
	}

	public boolean getFlagged() {
		return ((mFlags & FLAGGED) == FLAGGED);
	}

	public void deleteItem() {
		/* TODO Though we have the machinery in place for multiple concurrent updates on an item,
		 * there is some concurrency risk to this now that we are allowing for concurrent operations
		 * Need to do some more research/cleaup if we are to allow this*/
		 if (isUpdating())
			return;
		setUpdating(true, Locale.get("msgList.DeletingItem"));
		mMidlet.mMbox.deleteItem(mId, this);	
	}
    
	public void handleResponse(Object op, 
							   Object resp) {
		//#debug
		System.out.println("MailItem.handleResponse");
		setUpdating(false, null);
		if (resp instanceof Mailbox) {
			if (op == Mailbox.FLAGITEM) {
				//#debug
				System.out.println("MailItem.handleResponse: Item flag changed");
				setFlagged(!((mFlags & FLAGGED) == FLAGGED), false);
				mParentView.itemStateChanged(this, MailListView.FLAG_CHANGED);
			} else if (op == Mailbox.MARKITEMUNREAD) {
				//#debug
				System.out.println("MailItem.handleResponse: Item read state changed");
				setUnread(!((mFlags & UNREAD) == UNREAD), false);
				mParentView.itemStateChanged(this, MailListView.UNREAD_CHANGED);
			} else if (op == Mailbox.DELETEITEM) {
				//#debug
				System.out.println("MailItem.handleResponse: Item deleted");
				mParentView.itemStateChanged(this, MailListView.DELETED);
			} else if (op == Mailbox.TAGITEM) {
				//#debug
				System.out.println("MailItem.handleResponse: Item tags updated");
				this.mTags = mNewTags;
				mNewTags = null;
            } else if (op == Mailbox.MOVEITEM) {
                //#debug
                System.out.println("MailItem.handleResponse: Item moved");
                mParentView.itemStateChanged(this, MailListView.DELETED);
			}
		} else {
			if (op == Mailbox.TAGITEM)
				mNewTags = null;
			
			mMidlet.handleResponseError(resp, mParentView);
		}
	}

	public void setForwarded(boolean forwarded) {
		mFlags = (forwarded) ? mFlags | FORWARDED : mFlags & ~FORWARDED;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	public void setReplied(boolean replied) {
		mFlags = (replied) ? mFlags | REPLIED : mFlags & ~REPLIED;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	/**
	 * Set if the item has one or more attachments
	 * @param hasAttach
	 */
	public void setHasAttach(boolean hasAttach) {
		mFlags = (hasAttach) ? mFlags | ATTACHMENT : mFlags & ~ATTACHMENT;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	/**
	 * Set the item's date
	 * @param date
	 */
	public void setDate(long date) {
		synchronized (mDateObj) {
			mDateObj.setTime(date);
			mCalObj.setTime(mDateObj);
			mDateStr = Util.getDate(mCalObj);
		}
		mDate = date;
		mDirty = true;
		if (mVisible)
			repaint();
	}

	public void setSentDate(long date) {
		mSentDate = date;
	}

	public Calendar getReceivedDateAsCalendar() {
		return getDateAsCalendar(mDate);
	}

	public Calendar getSentDateAsCalendar() {
		return getDateAsCalendar(mSentDate);
	}

	private Calendar getDateAsCalendar(long date) {
		if (date == 0)
			return null;

		Calendar cal = Calendar.getInstance();
		synchronized (mDateObj) {
			mDateObj.setTime(date);
			cal.setTime(mDateObj);
		}
		return cal;
	}

	/**
	 * Subclassed implement this method to set the flag value etc
	 * @param newState true if set
	 * @param stateBit the flag being set
	 */
	protected abstract void doSetState(boolean newState,
		  	 				 		   int stateBit);


	protected boolean isUpdating() {
		return (mNumPendingUpdates > 0);
	}

	protected void setUpdating(boolean isUpdating,
							   String  text) {
		/* we keep track of the number of outstanding updates because we don't
		 * want to change the status if another operation is in progress for example
		 * the user may have marked an item unread and immediately marked it as flagged
		 * so that there are 2 ops in flight. Don't want to change the state until
		 * both are completed */
		mNumPendingUpdates = (isUpdating) ? mNumPendingUpdates + 1 : mNumPendingUpdates - 1;
		if (mNumPendingUpdates < 0) mNumPendingUpdates = 0;

		mUpdatingText = (text != null) ? text : "";
		mUpdatingContentLen = -1;
		mDirty = true;
		if (mVisible)
			// Force icon redraw
			repaint();
	}

	protected void drawUpdating(Graphics g,
								int w, 
								int h,
								Font font,
								int fontColor) {
		if (mUpdatingContentLen == -1) {
			mUpdatingText = Util.elidString(mUpdatingText, w - (ZimbraME.CLOCK_ICON_WIDTH + SPACING), font);
			mUpdatingContentLen = font.stringWidth(mUpdatingText);
		}
		g.setFont(font);
		g.setColor(fontColor);
		int x = (w - ZimbraME.CLOCK_ICON_WIDTH - SPACING - mUpdatingContentLen) / 2;
		int y = (h - Math.min(font.getHeight(), ZimbraME.CLOCK_ICON_HEIGHT)) / 2;
		g.drawImage(ZimbraME.CLOCK_ICON, x, y, Graphics.TOP | Graphics.LEFT);
		g.drawString(mUpdatingText, x + ZimbraME.CLOCK_ICON_WIDTH + SPACING, y, Graphics.TOP | Graphics.LEFT);
		
	}

	protected boolean traverse(int dir,
			   				   int viewportWidth,
			   				   int viewportHeight,
			   				   int[] visRect_inout) {
		if (mSelected) {
		} else {
			mParentView.itemHasFocus(this);
			mSelected = true;
		}
		
		// we will change this to true when we implement the above
		return false;
	}
	
	protected void traverseOut() {
		mSelected = false;
	}

	protected void pointerPressed(int x,
            					  int y) {
		//#debug
		System.out.println("MailItem.pointerPressed: " + mId);
	}
	
	protected void showNotify() {
		mVisible = true;
	}
	
	protected void hideNotify() {
		mVisible = false;
	}
	
	protected String getParticipantsStr(String[] participants,
										int len,
										int width,
										Font font) {
		if (participants == null)
			return "";

		if (len == 0) {
			return "";
		} else if (len == 1) {
			String p = participants[0];
			return (font.stringWidth(p) > width) ? Util.elidString(p, width, font) : p;
		} else {
			StringBuffer p = new StringBuffer();
			for (int i = 0; i < len; i++) {
				if (i == 1 && mElided)
					p.append(",...,");
				else if (i > 0)
					p.append(", ");
				StringTokenizer st = new StringTokenizer(participants[i], ' ');	
				p.append(st.nextToken());
				String s = p.toString();
				if (font.stringWidth(s) > width)
					return Util.elidString(s, width, font);
			}
			return p.toString();
		}
	}

	private void setItemState(boolean newState,
			  				  int stateBit,
			  				  boolean update) {
		if (!update) {
			doSetState(newState, stateBit);
		} else {
			/* TODO Though we have the machinery in place for multiple concurrent updates on an item,
			 * there is some concurrency risk to this now that we are allowing for concurrent updates
			 * Need to do some more research/cleaup if we are to allow this*/
			 if (isUpdating())
				return;

			// Check to see if we already in the requested state
			if ((newState && (mFlags & stateBit) == stateBit)
					|| (!newState && (mFlags & stateBit) != stateBit))
				return;
			switch(stateBit) {
				case FLAGGED:
					setUpdating(true, Locale.get("msgList.UpdatingItemFlagState"));
					mMidlet.mMbox.flagItem(mId, newState, this);
					break;
				case UNREAD:
					setUpdating(true, Locale.get("msgList.UpdatingItemUnreadState"));
					mMidlet.mMbox.markItemUnread(mId, newState, this);
					break;
			}		
		}
	}
}

