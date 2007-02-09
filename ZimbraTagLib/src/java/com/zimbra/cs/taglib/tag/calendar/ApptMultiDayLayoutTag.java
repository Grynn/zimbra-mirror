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

package com.zimbra.cs.taglib.tag.calendar;

import com.zimbra.cs.taglib.bean.ZApptCellLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptMultiDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptRowLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.bean.ZApptAllDayLayoutBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZApptSummary;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApptMultiDayLayoutTag extends ZimbraSimpleTag {

    private int DEFAULT_HOUR_START = 8;
    private int DEFAULT_HOUR_END = 18;
    private static final long MSECS_PER_MINUTE = 1000*60;
    private static final long MSECS_PER_HOUR = MSECS_PER_MINUTE * 60;
    private static final long MSECS_PER_DAY = MSECS_PER_HOUR * 24;

    private String mVar;
    private long mStart = -1;
    private long mEnd = -1;
    private int mNumDays = 1;
    private long mHourStart = DEFAULT_HOUR_START;
    private long mHourEnd = DEFAULT_HOUR_END;
    private ZApptSummariesBean mAppointments;

    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setDays(int numDays) { this.mNumDays = numDays; }
    public void setHourstart(long start) { this.mHourStart = start; }
    public void setHourend(long end) { this.mHourEnd = end; }
    public void setAppointments(ZApptSummariesBean appts) { this.mAppointments = appts; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();


        mEnd = mStart + MSECS_PER_DAY *mNumDays;

        ZApptAllDayLayoutBean allday = new ZApptAllDayLayoutBean(mAppointments.getAppointments(), mStart, mEnd, mNumDays);

        List<ZApptDayLayoutBean> days = new ArrayList<ZApptDayLayoutBean>(mNumDays);

        for (int i=0; i < mNumDays; i++) {
            days.add(new ZApptDayLayoutBean(mAppointments.getAppointments(), mStart + MSECS_PER_DAY *i, mStart + MSECS_PER_DAY *(i+1), i, mNumDays));
        }


        long hourStart = (mHourStart == -1 || mHourStart > mHourEnd) ? DEFAULT_HOUR_START : mHourStart;
        long hourEnd = mHourEnd == -1 || mHourEnd < hourStart ? DEFAULT_HOUR_END : mHourEnd;

        long msecsStart = MSECS_PER_HOUR * hourStart;
        long msecsEnd = MSECS_PER_HOUR * hourEnd;
        long msecsIncr = MSECS_PER_MINUTE * 15;

        // compute earliest/latest appt time across all days
        for (ZApptDayLayoutBean day : days) {

            if (day.getEarliestAppt() != null) {
                //System.err.printf("msecs apptStart(%s) dayEnd(%d) dayStart(%d)\n", day.getEarliestAppt().getEndTime(), day.getEndTime(), day.getStartTime());
                long start = day.getEarliestAppt().getStartTime();
                if (start < day.getStartTime()) {
                    msecsStart = 0;
                } else { //if ((start - day.getStartTime()) <  msecsStart) {
                    start = ((start-day.getStartTime())/ MSECS_PER_HOUR) * MSECS_PER_HOUR;
                    if (start < msecsStart) msecsStart = start;
                }
            }

            if (day.getLatestAppt() != null) {
                //System.err.printf("msecs apptEnd(%s) dayEnd(%d) dayStart(%d)\n", day.getLatestAppt().getEndTime(), day.getEndTime(), day.getStartTime());
                long end = day.getLatestAppt().getEndTime();
                if (end > day.getEndTime()) {
                    msecsEnd = MSECS_PER_DAY;
                } else { //if ((end - day.getStartTime()) > msecsEnd) {
                    end = ((end - day.getStartTime())/ MSECS_PER_HOUR) * MSECS_PER_HOUR;
                    if (end > msecsEnd) msecsEnd = end;
                }
            }
        }

        // santiy checks
        if (msecsStart < 0 )
            msecsStart = 0;
        if (msecsEnd > MSECS_PER_DAY || msecsEnd < msecsStart)
            msecsEnd = MSECS_PER_DAY;

        double percentPerDay = 100.0 / mNumDays;
        
        List<ZApptRowLayoutBean> rows = new ArrayList<ZApptRowLayoutBean>();

        for (ZApptDayLayoutBean day : days) {
            int numCols = day.getColumns().size();
            double percentPerCol = percentPerDay/numCols;
            // need new one for each day
            HashMap<ZApptSummary, ZApptCellLayoutBean> mDoneAppts = new HashMap<ZApptSummary, ZApptCellLayoutBean>();
            int rowNum = 0;
            long lastRange = day.getStartTime() + msecsEnd;
            for (long msecsRangeStart = day.getStartTime()+msecsStart; msecsRangeStart < lastRange; msecsRangeStart += msecsIncr) {
                long msecsRangeEnd = msecsRangeStart + msecsIncr;

                List<ZApptCellLayoutBean> cells =
                        rowNum < rows.size() ? rows.get(rowNum).getCells() : new ArrayList<ZApptCellLayoutBean>();

                for (int colIndex = 0; colIndex < numCols; colIndex++) {
                    List<ZApptSummary> rawColumn = day.getColumns().get(colIndex);
                    ZApptSummary match = null;
                    for (ZApptSummary a : rawColumn) {
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
                            cell.setRowSpan(computeRowSpan(match, msecsIncr, day.getStartTime()+msecsStart, day.getStartTime()+msecsEnd));
                            cell.setColSpan(computeColSpan(match.getStartTime(), match.getEndTime(), day.getColumns(), colIndex+1));
                        } else {
                            cell.setColSpan(existingCol.getColSpan());
                        }
                    } else {
                        cell.setRowSpan(1);
                        cell.setColSpan(computeColSpan(msecsRangeStart, msecsRangeEnd, day.getColumns(), colIndex+1));
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

        List<ZApptRowLayoutBean> allDayRows = new ArrayList<ZApptRowLayoutBean>();
        int rowNum = 0;
        for (List<ZApptSummary> row : allday.getRows()) {

            List<ZApptCellLayoutBean> cells = new ArrayList<ZApptCellLayoutBean>();

            for (int dayIndex = 0; dayIndex < days.size(); dayIndex++) {
                ZApptDayLayoutBean day = days.get(dayIndex);

                ZApptSummary match = null;
                for (ZApptSummary appt : row) {
                    if (appt.isInRange(day.getStartTime(), day.getEndTime())) {
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
                cell.setWidth((int)(percentPerDay*cell.getColSpan()));
                cells.add(cell);
                if (daySpan > 1)
                    dayIndex += daySpan-1;
            }
            allDayRows.add(new ZApptRowLayoutBean(cells, rowNum++, mStart));
        }

        jctxt.setAttribute(mVar, new ZApptMultiDayLayoutBean(days, allDayRows, rows), PageContext.PAGE_SCOPE);
    }

    private int computeAllDayDaySpan(ZApptSummary match, List<ZApptDayLayoutBean> days, int dayIndex) {
        int daySpan = 1;
        while(dayIndex < days.size()) {
            ZApptDayLayoutBean day = days.get(dayIndex);
            if (!match.isOverLapping(day.getStartTime(), day.getEndTime()))
                return daySpan;
            daySpan++;
            dayIndex++;
        }
        return daySpan;
    }

    private long computeColSpan(long start, long end, List<List<ZApptSummary>> columns, int colIndex) {
        int i = colIndex;
        for (; i < columns.size(); i++) {
            for (ZApptSummary appt: columns.get(i)) {
                if (ZApptSummary.isOverLapping(start, end, appt.getStartTime(), appt.getEndTime())) {
                    return (i - colIndex) + 1;
                }
            }
        }
        return (i - colIndex) + 1;
    }

    private long computeRowSpan(ZApptSummary match, long msecsIncr, long msecsStart, long msecsEnd) {
        if (msecsStart < match.getStartTime())
            msecsStart = match.getStartTime();
        if (msecsEnd > match.getEndTime())
            msecsEnd = match.getEndTime();

        long rowspan = (msecsEnd - msecsStart) / msecsIncr;
        return rowspan == 0 ? 1 : rowspan;

    }

  
}
