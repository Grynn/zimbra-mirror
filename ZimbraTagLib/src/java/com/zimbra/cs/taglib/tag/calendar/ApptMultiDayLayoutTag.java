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
import com.zimbra.client.ZAppointmentHit;

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
    private int DEFAULT_MIN_START = 0;
    private int DEFAULT_MIN_END = 0;

    private static final long MSECS_PER_MINUTE = 1000*60;
    private static final long MSECS_PER_HOUR = MSECS_PER_MINUTE * 60;
    private static final long MSECS_PER_DAY = MSECS_PER_HOUR * 24;
    private static final long MSECS_INCR = MSECS_PER_MINUTE * 15;

    private String mVar;
    private TimeZone mTimeZone;
    private long mStart = -1;
    private long mEnd = -1;
    private int mNumDays = 1;
    private String mWDays;
    private long mWeekStart;
    private long mHourStart = DEFAULT_HOUR_START;
    private long mHourEnd = DEFAULT_HOUR_END;
    private long mMinStart = DEFAULT_MIN_START;
    private long mMinEnd = DEFAULT_MIN_END;
    private ZApptSummariesBean mAppointments;
    private long mMsecsDayStart;
    private long mMsecsDayEnd;
    private String mSchedule; // comma-sep list of folders ids to render in "schedule" mode
    boolean mScheduleMode;
    boolean mIsPrint;

    private List<Boolean> workDays;
    public void setSchedule(String schedule) { this.mSchedule = schedule; }
    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setDays(int numDays) { this.mNumDays = numDays; }
    public void setWdays(String wDays) {this.mWDays = wDays;}
    public void setWeekStart(long firstDay) {this.mWeekStart = firstDay;}
    public void setHourstart(long start) { this.mHourStart = start; }
    public void setHourend(long end) { this.mHourEnd = end; }
    public void setMinstart(long minstart) { this.mMinStart = minstart; }
    public void setMinend(long minend) { this.mMinEnd = minend; }
    public void setAppointments(ZApptSummariesBean appts) { this.mAppointments = appts; }
    public void setTimezone(TimeZone timeZone) { mTimeZone = timeZone; }
    public void setIsprint(boolean isprint) {mIsPrint = isprint; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();

        workDays = new ArrayList<Boolean>();
        for(int i = 0;i<7;i++)
            workDays.add(false);

        String inpWdays [] = mWDays.split(",");

        for(String day:inpWdays) {
            workDays.remove(Integer.parseInt(day));
            workDays.add(Integer.parseInt(day),true);
        }


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
        List<List<ZApptRowLayoutBean>> rowsSeperatedByDays = computeRowsSeperatedByDays(days);
        List<List<ZApptRowLayoutBean>> allDayRowsSeperatedByDays = computeAllDayRowsSeperatedByDays(days);

        jctxt.setAttribute(mVar, new ZApptMultiDayLayoutBean(days, allDayRows, rows, rowsSeperatedByDays, allDayRowsSeperatedByDays), PageContext.PAGE_SCOPE);
    }

    private List<ZApptRowLayoutBean> computeAllDayRows(List<ZApptDayLayoutBean> days) {

        ZApptAllDayLayoutBean allday = new ZApptAllDayLayoutBean(mAppointments.getAppointments(), mStart, mEnd, mNumDays, mScheduleMode);
        double percentPerDay;
        if (mScheduleMode) {
            String folders[] = mSchedule.split(",");
            percentPerDay = 100.0 / folders.length;
        } else {
            percentPerDay = 100.0 / mWDays.split(",").length;
        }
        List<ZApptRowLayoutBean> allDayRows = new ArrayList<ZApptRowLayoutBean>();
        int rowNum = 0;
        for (List<ZAppointmentHit> row : allday.getRows()) {

            List<ZApptCellLayoutBean> cells = new ArrayList<ZApptCellLayoutBean>();
            Boolean addCellsToRow = false;
            for (int dayIndex = 0; dayIndex < days.size(); dayIndex++) {
                ZApptDayLayoutBean day = days.get(dayIndex);
                String folderId = day.getFolderId();

                if(!workDays.get((day.getDay() + (int)mWeekStart) % 7))
                 continue;

                ZAppointmentHit match = null;
                for (ZAppointmentHit appt : row) {
                    if (appt.isInRange(day.getStartTime(), day.getEndTime()) && (folderId == null || folderId.equals(appt.getFolderId()))) {
                        match = appt;
                        addCellsToRow = true;
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
            if(addCellsToRow)
                allDayRows.add(new ZApptRowLayoutBean(cells, rowNum++, mStart));
        }
        return allDayRows;
    }

    private List<ZApptRowLayoutBean> computeRows(List<ZApptDayLayoutBean> days) {
       List<ZApptRowLayoutBean> rows = new ArrayList<ZApptRowLayoutBean>();


        double percentPerDay;
        if (mScheduleMode) {
            String folders[] = mSchedule.split(",");
            percentPerDay = 100.0 / folders.length;
        } else {
            percentPerDay = 100.0 / mWDays.split(",").length;
        }

        for (ZApptDayLayoutBean day : days) {
            if(!workDays.get((day.getDay() + (int)mWeekStart) %7))
                   continue;
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
                        colIndex += cell.getColSpan() - 1;
                }

                if (rowNum >= rows.size())
                    rows.add(new ZApptRowLayoutBean(cells, rowNum, msecsRangeStart));
                rowNum++;
            }
        }
        return rows;
    }

    /*
     *  @function - computeAllDayRowsSeperatedByDays
     *  @param - Accepts an array of days (ZApptDayLayoutBean)
     *  @param - Returns a List of "AllDay" appointments for the given number of days.
     *           This function computes an array of AllDay appointments for the input days array.
     *
     */
    private List<List<ZApptRowLayoutBean>> computeAllDayRowsSeperatedByDays(List<ZApptDayLayoutBean> days) {
        /**
         * Get all "AllDay" appointments within the input date range (mstart, mEnd)
         */
        ZApptAllDayLayoutBean allday = new ZApptAllDayLayoutBean(mAppointments.getAppointments(), mStart, mEnd, mNumDays, mScheduleMode);

        double percentPerDay = 100.0;
        List<List<ZApptRowLayoutBean>> allDayRowsofRows = new ArrayList<List<ZApptRowLayoutBean>>();

        /**
         * For each input day
         */
        for (int dayIndex = 0; dayIndex < days.size(); dayIndex++)
        {
            int rowNum = 0;
            List<ZApptRowLayoutBean> allDayRows = new ArrayList<ZApptRowLayoutBean>();
            List<ZApptCellLayoutBean> cells = new ArrayList<ZApptCellLayoutBean>();
            ZApptDayLayoutBean day = days.get(dayIndex);

            String folderId = day.getFolderId();
            for (List<ZAppointmentHit> row : allday.getRows()) {
                /**
                 * Find "allDay" appointments for the this day - day[dayIndex].
                 * Build cells for an appointment hit and add it to the allDayRow.
                 */
                ZAppointmentHit match = null;
                for (ZAppointmentHit appt : row) {
                    if (appt.isInRange(day.getStartTime(), day.getEndTime()) && (folderId == null || folderId.equals(appt.getFolderId()))) {
                        match = appt;
                        break;
                    }
                }
                ZApptCellLayoutBean cell = new ZApptCellLayoutBean(day);
                int daySpan = 1;
                /**
                 * If an "allDay" appointment is found for this day, create a cell and add to cells.
                 */
                if (match != null) {
                    cell.setAppt(match);
                    cell.setIsFirst(true);
                    cell.setRowSpan(1);
                    daySpan = computeAllDayDaySpan(match, days, dayIndex+1);
                    int colSpan = 0;
                    for (int d = 0; d < daySpan; d++)
                        colSpan += days.get(dayIndex+d).getMaxColumns();
                    cell.setColSpan(colSpan);
                    cell.setDaySpan(daySpan);
                    cell.setWidth((int)(percentPerDay*cell.getColSpan()));
                    cells.add(cell);
                }
                if (daySpan > 1)
                    dayIndex += daySpan-1;
            }
            allDayRows.add(new ZApptRowLayoutBean(cells, rowNum++, mStart));
            /**
             * Add allDay row for this day to the allDayRowsofRows.
             */
            allDayRowsofRows.add(allDayRows);
        }
        return allDayRowsofRows;
    }

    /*
     *  @function - computeRowsSeperatedByDays
     *  @param - Accepts an array of days (ZApptDayLayoutBean)
     *  @param - Returns a List of appointments for the given number of days.
     *           This function computes an array of appointments for the input days array.
     *
     */
    private List<List<ZApptRowLayoutBean>> computeRowsSeperatedByDays(List<ZApptDayLayoutBean> days) {

        List<List<ZApptRowLayoutBean>> rowsForAllDays = new ArrayList<List<ZApptRowLayoutBean>>();

        double percentPerDay = 100.0;

        for (ZApptDayLayoutBean day : days) {
            List<ZApptRowLayoutBean> rowsForOneDay = new ArrayList<ZApptRowLayoutBean>();
            int numCols = day.getColumns().size();
            double percentPerCol = percentPerDay/numCols;
            // need new one for each day
            HashMap<ZAppointmentHit, ZApptCellLayoutBean> mDoneAppts = new HashMap<ZAppointmentHit, ZApptCellLayoutBean>();
            int rowNum = 0;
            long lastRange = day.getStartTime() + mMsecsDayEnd;
            for (long msecsRangeStart = day.getStartTime()+ mMsecsDayStart; msecsRangeStart < lastRange; msecsRangeStart += MSECS_INCR) {
                long msecsRangeEnd = msecsRangeStart + MSECS_INCR;

                List<ZApptCellLayoutBean> cells =
                        rowNum < rowsForOneDay.size() ? rowsForOneDay.get(rowNum).getCells() : new ArrayList<ZApptCellLayoutBean>();

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
                    /**
                     * If an appointment is found for this row, create a cell and add to cells.
                     */
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
                        colIndex += cell.getColSpan() - 1;
                }

                if (rowNum >= rowsForOneDay.size())
                    rowsForOneDay.add(new ZApptRowLayoutBean(cells, rowNum, msecsRangeStart));
                rowNum++;
            }
            rowsForAllDays.add(rowsForOneDay);
        }
        return rowsForAllDays;
    }

    private void computeDayStartEnd(List<ZApptDayLayoutBean> days) {
        long hourStart = (mHourStart == -1 || mHourStart > mHourEnd) ? DEFAULT_HOUR_START : mHourStart;
        long hourEnd = mHourEnd == -1 || mHourEnd < hourStart ? DEFAULT_HOUR_END : mHourEnd;
        long minStart = (mMinStart == -1 || mMinStart > mMinEnd) ? DEFAULT_MIN_START : mMinStart;
        long minEnd = (mMinEnd == -1 || mMinEnd < minStart) ? DEFAULT_MIN_END : mMinEnd;

        mMsecsDayStart = MSECS_PER_HOUR * hourStart + MSECS_PER_MINUTE * minStart;
        mMsecsDayEnd = MSECS_PER_HOUR * hourEnd + MSECS_PER_MINUTE * minEnd;
        /**
         * In case of print request, we get the start and end hours to print. No need to compute the earliest appt time. 
         */
        if (!mIsPrint) {
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
