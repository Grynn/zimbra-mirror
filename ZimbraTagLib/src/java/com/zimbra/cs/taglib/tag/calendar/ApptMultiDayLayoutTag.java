/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

package com.zimbra.cs.taglib.tag.calendar;

import com.zimbra.cs.taglib.bean.ZApptCellLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptMultiDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptRowLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.bean.ZApptAllDayLayoutBean;
import com.zimbra.cs.taglib.bean.BeanUtils;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZAppointmentHit;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Calendar;

public class ApptMultiDayLayoutTag extends ZimbraSimpleTag {

    private int DEFAULT_HOUR_START = 8;
    private int DEFAULT_HOUR_END = 18;
    private static final long MSECS_PER_MINUTE = 1000*60;
    private static final long MSECS_PER_HOUR = MSECS_PER_MINUTE * 60;
    private static final long MSECS_PER_DAY = MSECS_PER_HOUR * 24;
    private static final long MSECS_INCR = MSECS_PER_MINUTE * 15;

    private String mVar;
    private TimeZone mTimeZone;
    private long mStart = -1;
    private long mEnd = -1;
    private int mNumDays = 1;
    private long mHourStart = DEFAULT_HOUR_START;
    private long mHourEnd = DEFAULT_HOUR_END;
    private ZApptSummariesBean mAppointments;
    private long mMsecsDayStart;
    private long mMsecsDayEnd;
    private String mSchedule; // comma-sep list of folders ids to render in "schedule" mode
    boolean mScheduleMode;

    public void setSchedule(String schedule) { this.mSchedule = schedule; }
    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setDays(int numDays) { this.mNumDays = numDays; }
    public void setHourstart(long start) { this.mHourStart = start; }
    public void setHourend(long end) { this.mHourEnd = end; }
    public void setAppointments(ZApptSummariesBean appts) { this.mAppointments = appts; }
    public void setTimezone(TimeZone timeZone) { mTimeZone = timeZone; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();

        List<ZApptDayLayoutBean> days = new ArrayList<ZApptDayLayoutBean>();

        mScheduleMode = (mSchedule != null && mSchedule.length() > 0);
        if (mScheduleMode) {
            String folders[] = mSchedule.split(",");
            mNumDays = folders.length;

            Calendar startCal = Calendar.getInstance(mTimeZone);
            startCal.setTimeInMillis(mStart);

            mEnd = BeanUtils.addDay(startCal, 1).getTimeInMillis();

            for (int i=0; i < folders.length; i++) {
                days.add(new ZApptDayLayoutBean(mAppointments.getAppointments(), mStart, i, mNumDays, folders[i], MSECS_INCR));
            }
        } else {

            Calendar startCal = Calendar.getInstance(mTimeZone);
            startCal.setTimeInMillis(mStart);
            long dayStartTime = mStart;
            mEnd = BeanUtils.addDay(startCal, mNumDays).getTimeInMillis();

            for (int i=0; i < mNumDays; i++) {
                days.add(new ZApptDayLayoutBean(mAppointments.getAppointments(), dayStartTime, i, mNumDays, null, MSECS_INCR));
                /*
                 * StartTime = Prev Day Start Time + 24hrs( this will respect the DST_OFFSET, if any)
                 */
                //BeanUtils.getNextDay(startCal);
                dayStartTime += MSECS_PER_DAY;
            }
        }

        computeDayStartEnd(days);

        List<ZApptRowLayoutBean> rows = computeRows(days);
        List<ZApptRowLayoutBean> allDayRows = computeAllDayRows(days);

        jctxt.setAttribute(mVar, new ZApptMultiDayLayoutBean(days, allDayRows, rows), PageContext.PAGE_SCOPE);
    }

    private List<ZApptRowLayoutBean> computeAllDayRows(List<ZApptDayLayoutBean> days) {
        ZApptAllDayLayoutBean allday = new ZApptAllDayLayoutBean(mAppointments.getAppointments(), mStart, mEnd, mNumDays, mScheduleMode);

        double percentPerDay = 100.0 / mNumDays;
        List<ZApptRowLayoutBean> allDayRows = new ArrayList<ZApptRowLayoutBean>();
        int rowNum = 0;
        for (List<ZAppointmentHit> row : allday.getRows()) {

            List<ZApptCellLayoutBean> cells = new ArrayList<ZApptCellLayoutBean>();

            for (int dayIndex = 0; dayIndex < days.size(); dayIndex++) {
                ZApptDayLayoutBean day = days.get(dayIndex);
                String folderId = day.getFolderId();

                ZAppointmentHit match = null;
                for (ZAppointmentHit appt : row) {
                    if (appt.isInRange(day.getStartTime(), day.getEndTime()) && (folderId == null || folderId.equals(appt.getFolderId()))) {
                        match = appt;
                        break;
                    }
                }
                ZApptCellLayoutBean cell = new ZApptCellLayoutBean(day);
                int daySpan;
                if (match != null) {
                    cell.setAppt(match);
                    cell.setIsFirst(true);
                    cell.setRowSpan(1);
                    daySpan = computeAllDayDaySpan(match, days, dayIndex+1);
                    int colSpan = 0;
                    for (int d = 0; d < daySpan; d++)
                        colSpan += days.get(dayIndex+d).getMaxColumns();
                    cell.setColSpan(colSpan);
                } else {
                    cell.setRowSpan(1);
                    cell.setColSpan(day.getMaxColumns());
                    daySpan = 1;
                }
                cell.setDaySpan(daySpan);
                cell.setWidth((int)(percentPerDay*cell.getColSpan()));
                cells.add(cell);
                if (daySpan > 1)
                    dayIndex += daySpan-1;
            }
            allDayRows.add(new ZApptRowLayoutBean(cells, rowNum++, mStart));
        }
        return allDayRows;
    }

    private List<ZApptRowLayoutBean> computeRows(List<ZApptDayLayoutBean> days) {
       List<ZApptRowLayoutBean> rows = new ArrayList<ZApptRowLayoutBean>();


        double percentPerDay = 100.0 / mNumDays;

        for (ZApptDayLayoutBean day : days) {
            int numCols = day.getColumns().size();
            double percentPerCol = percentPerDay/numCols;
            // need new one for each day
            HashMap<ZAppointmentHit, ZApptCellLayoutBean> mDoneAppts = new HashMap<ZAppointmentHit, ZApptCellLayoutBean>();
            int rowNum = 0;
            long lastRange = day.getStartTime() + mMsecsDayEnd;
            for (long msecsRangeStart = day.getStartTime()+ mMsecsDayStart; msecsRangeStart < lastRange; msecsRangeStart += MSECS_INCR) {
                long msecsRangeEnd = msecsRangeStart + MSECS_INCR;

                List<ZApptCellLayoutBean> cells =
                        rowNum < rows.size() ? rows.get(rowNum).getCells() : new ArrayList<ZApptCellLayoutBean>();

                for (int colIndex = 0; colIndex < numCols; colIndex++) {
                    List<ZAppointmentHit> rawColumn = day.getColumns().get(colIndex);
                    ZAppointmentHit match = null;
                    for (ZAppointmentHit a : rawColumn) {
                        if (a.isInRange(msecsRangeStart, msecsRangeEnd)) {
                            match = a;
                            break;
                        }
                    }
                    ZApptCellLayoutBean cell = new ZApptCellLayoutBean(day);
                    if (match != null) {
                        cell.setAppt(match);
                        ZApptCellLayoutBean existingCol = mDoneAppts.get(match);
                        if (existingCol == null) {
                            cell.setIsFirst(true);
                            mDoneAppts.put(match, cell);
                            cell.setRowSpan(computeRowSpan(match, MSECS_INCR, day.getStartTime()+ mMsecsDayStart, day.getStartTime()+ mMsecsDayEnd));
                            cell.setColSpan(computeColSpan(match.getStartTime(), match.getEndTime(), day.getColumns(), colIndex+1, MSECS_INCR));
                        } else {
                            cell.setColSpan(existingCol.getColSpan());
                        }
                    } else {
                        cell.setRowSpan(1);
                        cell.setColSpan(computeColSpan(msecsRangeStart, msecsRangeEnd, day.getColumns(), colIndex+1, MSECS_INCR));
                    }
                    cell.setWidth((int)(percentPerCol*cell.getColSpan()));
                    cells.add(cell);
                    if (cell.getColSpan() > 1)
                        colIndex += cell.getColSpan();
                }

                if (rowNum >= rows.size())
                    rows.add(new ZApptRowLayoutBean(cells, rowNum, msecsRangeStart));
                rowNum++;
            }
        }
        return rows;
    }
    
    private void computeDayStartEnd(List<ZApptDayLayoutBean> days) {
        long hourStart = (mHourStart == -1 || mHourStart > mHourEnd) ? DEFAULT_HOUR_START : mHourStart;
        long hourEnd = mHourEnd == -1 || mHourEnd < hourStart ? DEFAULT_HOUR_END : mHourEnd;

        mMsecsDayStart = MSECS_PER_HOUR * hourStart;
        mMsecsDayEnd = MSECS_PER_HOUR * hourEnd;


        // compute earliest/latest appt time across all days
        for (ZApptDayLayoutBean day : days) {

            if (day.getEarliestAppt() != null) {
                //System.err.printf("msecs apptStart(%s) dayEnd(%d) dayStart(%d)\n", day.getEarliestAppt().getEndTime(), day.getEndTime(), day.getStartTime());
                long start = day.getEarliestAppt().getStartTime();
                if (start < day.getStartTime()) {
                    mMsecsDayStart = 0;
                } else { //if ((start - day.getStartTime()) <  msecsDayStart) {
                    start = ((start-day.getStartTime())/ MSECS_PER_HOUR) * MSECS_PER_HOUR;
                    if (start < mMsecsDayStart) mMsecsDayStart = start;
                }
            }

            if (day.getLatestAppt() != null) {
                //System.err.printf("msecs apptEnd(%s) dayEnd(%d) dayStart(%d)\n", day.getLatestAppt().getEndTime(), day.getEndTime(), day.getStartTime());
                long end = day.getLatestAppt().getEndTime();
                if (end > day.getEndTime()) {
                    mMsecsDayEnd = day.getEndTime() - day.getStartTime();
                } else { //if ((end - day.getStartTime()) > msecsDayEnd) {
                    //end = ((end - day.getStartTime())/ MSECS_PER_HOUR) * MSECS_PER_HOUR;
                    end = ((end - day.getStartTime() + MSECS_PER_HOUR - 1)/ MSECS_PER_HOUR) * MSECS_PER_HOUR;
                    if (end > mMsecsDayEnd) mMsecsDayEnd = end;
                }
            }
        }

        // santiy checks
        if (mMsecsDayStart < 0 )
            mMsecsDayStart = 0;
//        if (mMsecsDayEnd > MSECS_PER_DAY || mMsecsDayEnd < mMsecsDayStart)
//            mMsecsDayEnd = MSECS_PER_DAY;
    }
    
    private int computeAllDayDaySpan(ZAppointmentHit match, List<ZApptDayLayoutBean> days, int dayIndex) {
        int daySpan = 1;

        while(dayIndex < days.size() && !mScheduleMode) {
            ZApptDayLayoutBean day = days.get(dayIndex);
            if (!match.isOverLapping(day.getStartTime(), day.getEndTime()))
                return daySpan;


            daySpan++;
            dayIndex++;
        }
        return daySpan;
    }

    private long computeColSpan(long start, long end, List<List<ZAppointmentHit>> columns, int colIndex, long msecsIncr) {
        int i = colIndex;
        for (; i < columns.size(); i++) {
            for (ZAppointmentHit appt: columns.get(i)) {
                if (ZAppointmentHit.isOverLapping(start, end, appt.getStartTime(), appt.getEndTime(), msecsIncr)) {
                    return (i - colIndex) + 1;
                }
            }
        }
        return (i - colIndex) + 1;
    }

    private long computeRowSpan(ZAppointmentHit match, long msecsIncr, long msecsStart, long msecsEnd) {
        if (msecsStart < match.getStartTime())
            msecsStart = match.getStartTime();
        if (msecsEnd > match.getEndTime())
            msecsEnd = match.getEndTime();

        msecsStart = ((long)(msecsStart / msecsIncr)) * msecsIncr;
        msecsEnd = ((long)((msecsEnd + msecsIncr - 1) / msecsIncr)) * msecsIncr;
        
        long rowspan = (msecsEnd - msecsStart) / msecsIncr;
        return rowspan == 0 ? 1 : rowspan;

    }

  
}
