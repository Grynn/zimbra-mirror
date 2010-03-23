/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class ZApptDayLayoutBean {

    private long mStartTime;
    private long mEndTime;
    private int mDay;
    private int mNumDays;
    private String mFolderId;
    private static final long MSECS_PER_MINUTE = 1000*60;
    private static final long MSECS_PER_HOUR = MSECS_PER_MINUTE * 60;
    private static final long MSECS_PER_DAY = MSECS_PER_HOUR * 24;

    List<ZAppointmentHit> mAllday; // all all-day appts in this range
    List<ZAppointmentHit> mAppts;  // all non-day appts in this range
    ZAppointmentHit mEarliestAppt; // non-allday appt with earliest start time
    ZAppointmentHit mLatestAppt;   // non-allday appt with latest end time
    List<List<ZAppointmentHit>> mColumns;

    public ZAppointmentHit getEarliestAppt() { return mEarliestAppt; }
    public ZAppointmentHit getLatestAppt() { return mLatestAppt; }
    public long getStartTime() { return mStartTime; }
    public long getEndTime() { return mEndTime; }

    public List<ZAppointmentHit> getAllDayAppts() { return mAllday; }
    public List<List<ZAppointmentHit>> getColumns() { return mColumns; }
    public int getDay() { return mDay; }

    public int getMaxColumns() {
        return mColumns.size();
    }

    public int getWidth() {
        return (int)(100.0/mNumDays);
    }

    public ZApptDayLayoutBean(List<ZAppointmentHit> appts, long dayStartTime, int day, int numDays, String folderId, long msecsIncr) {
        mAllday = new ArrayList<ZAppointmentHit>();
        mAppts = new ArrayList<ZAppointmentHit>();
        //mStartTime = startCal.getTimeInMillis();
        //mEndTime = BeanUtils.addDay(startCal, 1).getTimeInMillis();
        mStartTime = dayStartTime;
        mEndTime = mStartTime + MSECS_PER_DAY;
        mDay = day;
        mNumDays = numDays;
        mFolderId = folderId;

        for (ZAppointmentHit appt : appts) {
            if (appt.isInRange(mStartTime, mEndTime) && (mFolderId == null || mFolderId.equals(appt.getFolderId()))) {
                if (appt.isAllDay())
                    mAllday.add(appt);
                else {
                    mAppts.add(appt);
                    // keep track of earliest and latest
                    if ((mEarliestAppt == null || appt.getStartTime() < mEarliestAppt.getStartTime()))
                        mEarliestAppt = appt;
                    if ((mLatestAppt == null || appt.getEndTime() > mLatestAppt.getEndTime()))
                        mLatestAppt = appt;
                }
            }
        }
        computeOverlapInfo(msecsIncr);
    }

    public String getFolderId() {
        return mFolderId;
    }
    
    private void computeOverlapInfo(long msecsIncr) {
        mColumns = new ArrayList<List<ZAppointmentHit>>();
        mColumns.add(new ArrayList<ZAppointmentHit>());
        for (ZAppointmentHit appt : mAppts) {
            boolean overlap = false;
            for (List<ZAppointmentHit> col : mColumns) {
                overlap = false;
                for (ZAppointmentHit currentAppt : col) {
                    overlap = appt.isOverLapping(currentAppt, msecsIncr);
                    if (overlap) break;
                }
                if (!overlap) {
                    col.add(appt);
                    break;
                }
            }
            // if we got through all columns with overlap, add one
            if (overlap) {
                List<ZAppointmentHit> newCol = new ArrayList<ZAppointmentHit>();
                newCol.add(appt);
                mColumns.add(newCol);
            }
        }
    }
}
