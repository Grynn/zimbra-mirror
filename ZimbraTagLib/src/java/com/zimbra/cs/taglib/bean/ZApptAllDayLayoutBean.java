package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZApptSummary;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class ZApptAllDayLayoutBean {

    long mStartTime;
    long mEndTime;
    int mNumDays;

    List<ZApptSummary> mAllday; // all all-day appts in this range
    List<List<ZApptSummary>> mRows;

    public long getStartTime() { return mStartTime; }
    public long getEndTime() { return mEndTime; }

    public List<ZApptSummary> getAllDayAppts() { return mAllday; }
    public List<List<ZApptSummary>> getRows() { return mRows; }
    public Date getDate() { return new Date(mStartTime); }

    public int getNumberOfRows() {
        return mRows.size();
    }

    public ZApptAllDayLayoutBean(List<ZApptSummary> appts, long startTime, long endTime, int numDays) {
        mAllday = new ArrayList<ZApptSummary>();
        mStartTime = startTime;
        mEndTime = endTime;
        mNumDays = numDays;

        for (ZApptSummary appt : appts) {
            if (appt.isAllDay() && appt.isInRange(mStartTime, mEndTime)) {
                mAllday.add(appt);
            }
        }
        computeOverlapInfo();
    }

    private void computeOverlapInfo() {
        mRows = new ArrayList<List<ZApptSummary>>();
        mRows.add(new ArrayList<ZApptSummary>());
        for (ZApptSummary appt : mAllday) {
            boolean overlap = false;
            for (List<ZApptSummary> row : mRows) {
                overlap = false;
                for (ZApptSummary currentAppt : row) {
                    overlap = appt.isOverLapping(currentAppt);
                    if (overlap) break;
                }
                if (!overlap) {
                    row.add(appt);
                    break;
                }
            }
            // if we got through all rows with overlap, add one
            if (overlap) {
                List<ZApptSummary> newRow = new ArrayList<ZApptSummary>();
                newRow.add(appt);
                mRows.add(newRow);
            }
        }
    }
}
