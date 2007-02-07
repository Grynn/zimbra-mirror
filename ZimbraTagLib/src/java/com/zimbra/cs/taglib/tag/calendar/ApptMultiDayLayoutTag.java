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

import com.zimbra.cs.taglib.bean.ZApptColumnLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptMultiDayLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptRowLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
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
    private static final long MSEC_PER_MINUTE = 1000*60;
    private static final long MSEC_PER_HOUR = MSEC_PER_MINUTE * 60;
    private static final long MSEC_PER_DAY = MSEC_PER_HOUR * 24;

    private String mVar;
    private long mStart = -1;
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


        List<ZApptDayLayoutBean> days = new ArrayList<ZApptDayLayoutBean>(mNumDays);

        for (int i=0; i < mNumDays; i++) {
            days.add(new ZApptDayLayoutBean(mAppointments.getAppointments(), mStart + MSEC_PER_DAY*i, mStart + MSEC_PER_DAY*(i+1), i, mNumDays));
        }


        long hourStart = (mHourStart == -1 || mHourStart > mHourEnd) ? DEFAULT_HOUR_START : mHourStart;
        long hourEnd = mHourEnd == -1 || mHourEnd < hourStart ? DEFAULT_HOUR_END : mHourEnd;

        long msecsStart = MSEC_PER_HOUR * hourStart;
        long msecsEnd = MSEC_PER_HOUR * hourEnd;
        long msecsIncr = MSEC_PER_MINUTE *15;

        // compute earliest/latest appt time across all days
        for (ZApptDayLayoutBean day : days) {

            if (day.getEarliestAppt() != null) {
                long start = day.getEarliestAppt().getStartTime();
                if (start < day.getStartTime()) {
                    start = 0;
                } else if ((start - day.getStartTime()) <  msecsStart) {
                    start = ((start-day.getStartTime())/ MSEC_PER_HOUR) * MSEC_PER_HOUR;
                }
                if (start < msecsStart) msecsStart = start;
            }

            if (day.getLatestAppt() != null) {
                long end = day.getLatestAppt().getEndTime();
                if (end > day.getEndTime()) {
                    end = MSEC_PER_DAY;
                } else if ((end - day.getStartTime()) > msecsEnd) {
                    end = ((end-day.getStartTime())/ MSEC_PER_HOUR) * MSEC_PER_HOUR;
                }
                if (end > msecsEnd) msecsEnd = end;
            }
        }

        // santiy checks
        if (msecsStart < 0) msecsStart = 0;
        if (msecsEnd > MSEC_PER_DAY) msecsEnd = MSEC_PER_DAY;


        double percentPerDay = 100.0 / mNumDays;

        List<ZApptRowLayoutBean> rows = new ArrayList<ZApptRowLayoutBean>();
        
        for (ZApptDayLayoutBean day : days) {
            int numCols = day.getColumns().size();
            double percentPerCol = percentPerDay/numCols;
            // need new one for each day
            HashMap<ZApptSummary, ZApptColumnLayoutBean> mDoneAppts = new HashMap<ZApptSummary, ZApptColumnLayoutBean>();
            int rowNum = 0;
            long lastRange = day.getStartTime() + msecsEnd;
            for (long msecsRangeStart = day.getStartTime()+msecsStart; msecsRangeStart < lastRange; msecsRangeStart += msecsIncr) {
                long msecsRangeEnd = msecsRangeStart + msecsIncr;

                List<ZApptColumnLayoutBean> columns =
                        rowNum < rows.size() ? rows.get(rowNum).getColumns() : new ArrayList<ZApptColumnLayoutBean>();

                for (int colIndex = 0; colIndex < numCols; colIndex++) {
                    List<ZApptSummary> rawColumn = day.getColumns().get(colIndex);
                    ZApptSummary match = null;
                    for (ZApptSummary a : rawColumn) {
                        if (a.isInRange(msecsRangeStart, msecsRangeEnd)) {
                            match = a;
                            break;
                        }
                    }
                    ZApptColumnLayoutBean col = new ZApptColumnLayoutBean(day);
                    if (match != null) {
                        col.setAppt(match);
                        ZApptColumnLayoutBean existingCol = mDoneAppts.get(match);
                        if (existingCol == null) {
                            col.setIsFirst(true);
                            mDoneAppts.put(match, col);
                            col.setRowSpan(computeRowSpan(match, msecsIncr, day.getStartTime()+msecsStart, day.getStartTime()+msecsEnd));
                            col.setColSpan(computeColSpan(match.getStartTime(), match.getEndTime(), day.getColumns(), colIndex+1));
                        } else {
                            col.setColSpan(existingCol.getColSpan());
                        }
                    } else {
                        col.setRowSpan(1);
                        col.setColSpan(computeColSpan(msecsRangeStart, msecsRangeEnd, day.getColumns(), colIndex+1));
                    }
                    col.setWidth((int)(percentPerCol*col.getColSpan()));
                    columns.add(col);
                    if (col.getColSpan() > 1)
                        colIndex += col.getColSpan();
                }

                if (rowNum >= rows.size())
                    rows.add(new ZApptRowLayoutBean(columns, rowNum, msecsRangeStart));
                rowNum++;
            }
        }
        jctxt.setAttribute(mVar, new ZApptMultiDayLayoutBean(days, rows), PageContext.PAGE_SCOPE);
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
