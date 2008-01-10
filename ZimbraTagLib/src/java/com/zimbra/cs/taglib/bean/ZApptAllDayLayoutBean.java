/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ZApptAllDayLayoutBean {

    long mStartTime;
    long mEndTime;
    int mNumDays;

    List<ZAppointmentHit> mAllday; // all all-day appts in this range
    List<List<ZAppointmentHit>> mRows;

    public long getStartTime() { return mStartTime; }
    public long getEndTime() { return mEndTime; }

    public List<ZAppointmentHit> getAllDayAppts() { return mAllday; }
    public List<List<ZAppointmentHit>> getRows() { return mRows; }
    public Date getDate() { return new Date(mStartTime); }

    public int getNumberOfRows() {
        return mRows.size();
    }

    public ZApptAllDayLayoutBean(List<ZAppointmentHit> appts, long startTime, long endTime, int numDays, boolean scheduleMode) {
        mAllday = new ArrayList<ZAppointmentHit>();
        mStartTime = startTime;
        mEndTime = endTime;
        mNumDays = numDays;

        for (ZAppointmentHit appt : appts) {
            if (appt.isAllDay() && appt.isInRange(mStartTime, mEndTime)) {
                mAllday.add(appt);
            }
        }
        Collections.sort(mAllday, new Comparator<ZAppointmentHit>() {
            public int compare(ZAppointmentHit a1, ZAppointmentHit a2) {
                return new Long(((a2.getEndTime() - mStartTime) - (a1.getEndTime() - mStartTime))).intValue();
            }
        });
        computeOverlapInfo(scheduleMode);
    }

    private void computeOverlapInfo(boolean scheduleMode) {
        mRows = new ArrayList<List<ZAppointmentHit>>();
        mRows.add(new ArrayList<ZAppointmentHit>());
        for (ZAppointmentHit appt : mAllday) {
            boolean overlap = false;
            for (List<ZAppointmentHit> row : mRows) {
                overlap = false;
                if (!scheduleMode) {
                    for (ZAppointmentHit currentAppt : row) {
                        overlap = appt.isOverLapping(currentAppt);
                        if (overlap) break;
                    }
                } else {
                     for (ZAppointmentHit currentAppt : row) {
                        overlap = appt.getFolderId().equals(currentAppt.getFolderId());
                        if (overlap) break;
                    }
                }
                if (!overlap) {
                    row.add(appt);
                    break;
                }
            }
            // if we got through all rows with overlap, add one
            if (overlap) {
                List<ZAppointmentHit> newRow = new ArrayList<ZAppointmentHit>();
                newRow.add(appt);
                mRows.add(newRow);
            }
        }
    }
}
