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
package com.zimbra.zme.client;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

public class Appointment extends MailboxItem {
	// My status
    public static final int NEEDS_ACTION = 1;
    public static final int TENTATIVE = 2;
    public static final int ACCEPTED = 3;
    public static final int DECLINED = 4;
    public static final int DELEGATED = 5;
    
    // Event Status
    public static final int EVT_TENTATIVE = 1;
    public static final int EVT_CONFIRMED = 2;
    public static final int EVT_CANCELLED = 3;
	
    public boolean mLoaded;
    public String mInvId;
	public String mFolderId;
	public int mApptStatus;
	public int mMyStatus;
	public boolean mIsAllDay;
	public boolean mHasAlarm;
	public boolean mAmIOrganizer;
	public boolean mIsException;
	public boolean mOtherAttendees;
	public String mSubj;
	public String mFragment;
    public String mDescription;
	public String mLocation;
	public long mStart;
	public long mDuration;
	protected Appointment mBase;

	public String mOrganizerEmail;
	public Vector mAttendees;

    public int mRecurrence;
    public String mRrule;

    public static final int NOT_RECURRING = 0;
    public static final int DAILY = 1;
    public static final int WEEKLY = 2;
    public static final int EVERY2WEEK = 3;
    public static final int MONTHLY = 4;
    public static final int YEARLY = 5;
    public static final int CUSTOM = 6;

    
	public Appointment() {
        this(null);
	}
	
	public Appointment(Appointment a) {
        mLoaded = false;
        mItemType = APPOINTMENT;
        mBase = a;
        mAttendees = new Vector();
        copyFrom(a);
	}
    
    public void copyFrom(Appointment a) {
        if (a == null)
            return;
        mLoaded = a.mLoaded;
        mId = a.mId;
        mInvId = a.mInvId;
        mFolderId = a.mFolderId;
        mApptStatus = a.mApptStatus;
        mMyStatus = a.mMyStatus;
        mIsAllDay = a.mIsAllDay;
        mRecurrence = a.mRecurrence;
        mHasAlarm= a.mHasAlarm;
        mAmIOrganizer = a.mAmIOrganizer;
        mIsException = a.mIsException;
        mOtherAttendees = a.mOtherAttendees;
        mSubj = a.mSubj;
        mLocation = a.mLocation;
        mStart = a.mStart;
        mDuration = a.mDuration;
        mFragment = a.mFragment;
        mDescription = a.mDescription;
        mOrganizerEmail = a.mOrganizerEmail;
        mRecurrence = a.mRecurrence;
        mRrule = a.mRrule;
        
        mAttendees.removeAllElements();
        Enumeration elem = a.mAttendees.elements();
        while (elem.hasMoreElements())
            mAttendees.addElement(elem.nextElement());
    }
    
    public boolean isRecurring() {
        return mRecurrence != NOT_RECURRING;
    }
    public String getFragment() {
        if (mFragment == null && mBase != null)
            return mBase.getFragment();
        return mFragment;
    }
    public boolean occursOnSameDay(Appointment another) {
        Calendar mine = Calendar.getInstance();
        Calendar theirs = Calendar.getInstance();
        mine.setTime(new Date(mStart));
        theirs.setTime(new Date(another.mStart));
        return mine.get(Calendar.YEAR)  == theirs.get(Calendar.YEAR)
            && mine.get(Calendar.MONTH) == theirs.get(Calendar.MONTH)
            && mine.get(Calendar.DATE)  == theirs.get(Calendar.DATE);
    }
    public String getStartDateTime() {
        if (mStart > 0)
            return getDateTime(new Date(mStart));
        return null;
    }
    public String getEndDateTime() {
        if (mStart > 0)
            return getDateTime(new Date(mStart+mDuration));
        return null;
    }
    private String getDateTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        StringBuffer buf = new StringBuffer();
        formatInt(c.get(Calendar.YEAR), 4, buf);
        formatInt(c.get(Calendar.MONTH) + 1, 2, buf);
        formatInt(c.get(Calendar.DATE), 2, buf);
        if (mIsAllDay)
            return buf.toString();
        buf.append("T");
        formatInt(c.get(Calendar.HOUR_OF_DAY), 2, buf);
        formatInt(c.get(Calendar.MINUTE), 2, buf);
        formatInt(c.get(Calendar.SECOND), 2, buf);
        buf.append("Z");
        return buf.toString();
    }
    
    private void formatInt(int i, int digits, StringBuffer sb) {
        int width = 1;
        int max = 10;
        while (max <= i) {
            width++;
            max *= 10;
        }
        while (width < digits) {
            sb.append("0");
            width++;
        }
        sb.append(Integer.toString(i));
    }
}
