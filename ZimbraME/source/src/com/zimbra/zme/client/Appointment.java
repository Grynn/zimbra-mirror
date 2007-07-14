package com.zimbra.zme.client;

public class Appointment {
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
	
	public String mId;
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
	}
	
	public Appointment(Appointment a) {
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
}
