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

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.zimbra.zme.Util;
import com.zimbra.zme.ZimbraME;
import com.zimbra.zme.client.Appointment;

import de.enough.polish.ui.Style;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;


public class CalendarItem extends CustomItem {
	
	public static final int SPACING = 2;
	
	private static Image EXCEPTION_ICON;
	private static int EXCEPTION_ICON_WIDTH;
	private static Image INVITEE_ICON;
	private static int INVITEE_ICON_WIDTH;
	private static Image RECURRING_ICON;
	private static int RECURRING_ICON_WIDTH;
	private static Image REMINDER_ICON;
	private static int REMINDER_ICON_WIDTH;
	private static int MAX_ICON_HEIGHT;
	{
		try {
			EXCEPTION_ICON = Image.createImage("/ApptException.png");
			EXCEPTION_ICON_WIDTH = EXCEPTION_ICON.getWidth();
			
			INVITEE_ICON = Image.createImage("/Invitee.png");
			INVITEE_ICON_WIDTH = INVITEE_ICON.getWidth();
			
			RECURRING_ICON = Image.createImage("/ApptRecur.png");
			RECURRING_ICON_WIDTH = RECURRING_ICON.getWidth();
						
			REMINDER_ICON = Image.createImage("/ApptReminder.png");
			REMINDER_ICON_WIDTH = REMINDER_ICON.getWidth();
						
			MAX_ICON_HEIGHT = Math.max(EXCEPTION_ICON.getHeight(), 
									Math.max(INVITEE_ICON.getHeight(), 
											Math.max(RECURRING_ICON.getHeight(), REMINDER_ICON.getHeight())));
		} catch (Exception e) {
			//#debug
			System.out.println("CalendarItem.init: Exception " + e);
		}
		
	}

	public Appointment mAppt;

	private ZimbraME mMidlet;
	private Font mFont;
	private int mFontHeight;
	private int mFontColor;
	private CalendarView mParentView;
	private String mStartTimeStr;
	private String mEndTimeStr;
	private int mTimeStrLen;
	private String[] mSubjLines;
	private String mStatus;
	private boolean mSelected;
	
	//#ifdef polish.usePolishGui
		public CalendarItem(ZimbraME m,
							Appointment a,
							View parentView,
							Style style) {
			//#if true
				//# super("", style);
			//#else
			super("");
				//#endif
			
			init(m, parentView, a);
		}
	//#else
		public CalendarItem(ZimbraME m,
							Appointment a,
				 		 	View parentView) {
			super("");
			init(m, parentView, a);
		}
	//#endif

	
	protected void keyPressed(int keyCode) {
		int ga = getGameAction(keyCode);
		if (keyCode != Canvas.KEY_NUM5 && ga == Canvas.FIRE) {
		} else if (keyCode == Canvas.KEY_NUM9 && !mParentView.mFragmentShowing 
		   && mAppt.getFragment() != null && mAppt.getFragment().length() > 0) {
				Dialogs.showStatusMsg(mAppt.getFragment(), mParentView.mView, false);
				mParentView.mFragmentShowing = true;
				return;
		} else { 
			mParentView.keyPressed(keyCode, getGameAction(keyCode), this);
		}
		mParentView.mFragmentShowing = false;
		Dialogs.hideScreenInfo();
	}

	protected int getMinContentHeight() {
		return 40;
	}

	protected int getMinContentWidth() {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected int getPrefContentHeight(int width) {
		// initialize height to the status + detail icon
		int h = SPACING + Math.max(mFontHeight, MAX_ICON_HEIGHT);;
		
		// If the location is not null, then add a line for it
		if (mAppt.mLocation != null){
			h += mFontHeight;
		}
		
		if (mSubjLines != null) {
			for (int i = 0; i < mSubjLines.length; i++)
				h += mFontHeight + SPACING;
		}
		
		return h;
	}

	protected int getPrefContentWidth(int height) {
		return Display.getDisplay(mMidlet).getCurrent().getWidth();
	}

	protected void paint(Graphics g, 
			 			 int w, 
			 			 int h) {
		g.setFont(mFont);
		g.setColor(mFontColor);
		
		g.drawString(mStartTimeStr, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawString(mEndTimeStr, 0, SPACING + mFontHeight, Graphics.TOP | Graphics.LEFT);
		
		if (mSubjLines == null) {
			if (mAppt.mSubj == null)
				mAppt.mSubj = Locale.get("calendar.NoSubject");
			/* we will assume that the width of an item will not change. This is actually not a very dangerous
			 * assumption and saves a bunch of computation */
			mSubjLines = TextUtil.wrap(mAppt.mSubj, mFont, w - mTimeStrLen, w - mTimeStrLen);
			invalidate();
		}

		int yCursor = 0;
		
		for (int i = 0; i < mSubjLines.length; i++) {
			g.drawString(mSubjLines[i], mTimeStrLen, yCursor, Graphics.TOP | Graphics.LEFT);
			yCursor += SPACING + mFontHeight;
		}

		if (mAppt.mLocation != null) {
			g.drawString(Util.elidString(mAppt.mLocation, w - mTimeStrLen, mFont), mTimeStrLen, yCursor, Graphics.TOP | Graphics.LEFT);
			yCursor += SPACING + mFontHeight;
		}
		
		int offset = (mFontHeight > MAX_ICON_HEIGHT) ? yCursor + (mFontHeight - MAX_ICON_HEIGHT) / 2 : yCursor;
		
		if (mAppt.mIsException && w > EXCEPTION_ICON_WIDTH) {
			g.drawImage(EXCEPTION_ICON, w, offset, Graphics.TOP | Graphics.RIGHT);
			w -= (EXCEPTION_ICON_WIDTH + SPACING);
		} else if (mAppt.mRecurring && w > RECURRING_ICON_WIDTH) {
			g.drawImage(RECURRING_ICON, w, offset, Graphics.TOP | Graphics.RIGHT);;			
			w -= (RECURRING_ICON_WIDTH + SPACING);
		}

		if (!mAppt.mHasAlarm && w > REMINDER_ICON_WIDTH) {
			g.drawImage(REMINDER_ICON, w, offset, Graphics.TOP | Graphics.RIGHT);
			w -= (REMINDER_ICON_WIDTH + SPACING);
		}

		if (!mAppt.mAmIOrganizer && w > INVITEE_ICON_WIDTH) {
			g.drawImage(INVITEE_ICON, w, offset, Graphics.TOP | Graphics.RIGHT);
			w -= (INVITEE_ICON_WIDTH + SPACING);
		}
		
		if (mStatus == null) {
			if (mAppt.mAmIOrganizer) {
				mStatus = "";		
			} else if (mAppt.mApptStatus == Appointment.EVT_CANCELLED) {
				mStatus = Locale.get("calendar.Cancelled");		
			} else {
				switch (mAppt.mMyStatus) {
					case Appointment.ACCEPTED:
						mStatus = Locale.get("calendar.Accepted");		
						break;
					case Appointment.DECLINED:
						mStatus = Locale.get("calendar.Declined");		
						break;
					case Appointment.TENTATIVE:
						mStatus = Locale.get("calendar.Tentative");		
						break;
					case Appointment.NEEDS_ACTION:
						mStatus = Locale.get("calendar.New");		
						break;
					case Appointment.DELEGATED:
						mStatus = Locale.get("calendar.Delegated");
						break;
				}
			}
			mStatus = Util.elidString(mStatus, w - mTimeStrLen, mFont);
		}
		offset = (mFontHeight < MAX_ICON_HEIGHT) ? yCursor + (MAX_ICON_HEIGHT - mFontHeight) / 2 : yCursor;
		g.drawString(mStatus, mTimeStrLen, yCursor, Graphics.TOP | Graphics.LEFT);				
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


	public void setStyle(Style style) {
		//#if true
			//# super.setStyle(style);
		//#endif
		mFont = style.font;
		mFontHeight = mFont.getHeight();
		mFontColor = style.getFontColor();
		mTimeStrLen = mFont.stringWidth((Util.TIME_FMT == 24) ? "00:00 -" : "00:00MM -") + SPACING;
	}
	
	private void init(ZimbraME m,
					  View parentView,
					  Appointment a) {
		mMidlet = m;
		mParentView = (CalendarView)parentView;
		mAppt = a;
		
		Calendar c = Calendar.getInstance();
		Date d = new Date(a.mStart);
		
		c.setTime(d);
		mStartTimeStr = Util.getTime(c, false) + " -";
		
		d.setTime(a.mStart + a.mDuration);
		c.setTime(d);
		mEndTimeStr = Util.getTime(c, false);
	}
}
