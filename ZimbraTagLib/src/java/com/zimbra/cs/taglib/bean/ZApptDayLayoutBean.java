/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite Server.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZApptSummary;

import java.util.ArrayList;
import java.util.List;

public class ZApptDayLayoutBean {

    private long mStartTime;
    private long mEndTime;
    private int mDay;
    private int mNumDays;
    private String mFolderId;

    List<ZApptSummary> mAllday; // all all-day appts in this range
    List<ZApptSummary> mAppts;  // all non-day appts in this range
    ZApptSummary mEarliestAppt; // non-allday appt with earliest start time
    ZApptSummary mLatestAppt;   // non-allday appt with latest end time
    List<List<ZApptSummary>> mColumns;

    public ZApptSummary getEarliestAppt() { return mEarliestAppt; }
    public ZApptSummary getLatestAppt() { return mLatestAppt; }
    public long getStartTime() { return mStartTime; }
    public long getEndTime() { return mEndTime; }

    public List<ZApptSummary> getAllDayAppts() { return mAllday; }
    public List<List<ZApptSummary>> getColumns() { return mColumns; }
    public int getDay() { return mDay; }

    public int getMaxColumns() {
        return mColumns.size();
    }

    public int getWidth() {
        return (int)(100.0/mNumDays);
    }

    public ZApptDayLayoutBean(List<ZApptSummary> appts, long startTime, long endTime, int day, int numDays, String folderId, long msecsIncr) {
        mAllday = new ArrayList<ZApptSummary>();
        mAppts = new ArrayList<ZApptSummary>();
        mStartTime = startTime;
        mEndTime = endTime;
        mDay = day;
        mNumDays = numDays;
        mFolderId = folderId;

        for (ZApptSummary appt : appts) {
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
        mColumns = new ArrayList<List<ZApptSummary>>();
        mColumns.add(new ArrayList<ZApptSummary>());
        for (ZApptSummary appt : mAppts) {
            boolean overlap = false;
            for (List<ZApptSummary> col : mColumns) {
                overlap = false;
                for (ZApptSummary currentAppt : col) {
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
                List<ZApptSummary> newCol = new ArrayList<ZApptSummary>();
                newCol.add(appt);
                mColumns.add(newCol);
            }
        }
    }
}
