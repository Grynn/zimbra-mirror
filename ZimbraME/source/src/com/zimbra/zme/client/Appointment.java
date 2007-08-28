package com.zimbra.zme.client;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
	
	public String mFolderId;
	public int mApptStatus;
	public int mMyStatus;
	public boolean mIsAllDay;
	public boolean mRecurring;
	public boolean mHasAlarm;
	public boolean mAmIOrganizer;
	public boolean mIsException;
	public boolean mOtherAttendees;
	public String mSubj;
	public String mFragment;
	public String mLocation;
	public long mStart;
	public long mDuration;
	protected Appointment mBase;
    
	public Appointment() {
        mItemType = APPOINTMENT;
	}
	
	public Appointment(Appointment a) {
        mItemType = APPOINTMENT;
        mBase = a;
		mId = a.mId;
		mFolderId = a.mFolderId;
		mApptStatus = a.mApptStatus;
		mMyStatus = a.mMyStatus;
		mIsAllDay = a.mIsAllDay;
		mRecurring = a.mRecurring;
		mHasAlarm= a.mHasAlarm;
		mAmIOrganizer = a.mAmIOrganizer;
		mIsException = a.mIsException;
		mOtherAttendees = a.mOtherAttendees;
		mSubj = a.mSubj;
		mLocation = a.mLocation;
		mStart = a.mStart;
		mDuration = a.mDuration;
		mFragment = a.mFragment;
	}
    
    public String getFragment() {
        if (mFragment == null && mBase != null)
            return mBase.getFragment();
        return mFragment;
    }
    
    public String getStartDateTime() {
        return getDateTime(new Date(mStart));
    }
    public String getEndDateTime() {
        return getDateTime(new Date(mStart+mDuration));
    }
    private String getDateTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        StringBuffer buf = new StringBuffer();
        formatInt(c.get(Calendar.YEAR), 4, buf);
        formatInt(c.get(Calendar.MONTH) + 1, 2, buf);
        formatInt(c.get(Calendar.DATE), 2, buf);
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
        while (max < i) {
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
