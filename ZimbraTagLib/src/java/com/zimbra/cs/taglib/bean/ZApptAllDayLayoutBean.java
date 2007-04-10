package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZAppointmentHit;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

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
