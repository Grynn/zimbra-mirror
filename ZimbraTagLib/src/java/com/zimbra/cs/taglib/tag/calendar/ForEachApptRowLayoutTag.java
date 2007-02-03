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
import com.zimbra.cs.taglib.bean.ZApptRowLayoutBean;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZApptSummary;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForEachApptRowLayoutTag extends ZimbraSimpleTag {

    private String mVar;
    private long mStart = -1;
    private long mEnd = -1;
    private ZApptSummariesBean mAppointments;

    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setEnd(long end) { this.mEnd = end; }
    public void setAppointments(ZApptSummariesBean appts) { this.mAppointments = appts; }

    public void doTag() throws JspException, IOException {
        JspFragment body = getJspBody();
        if (body == null) return;
        JspContext jctxt = getJspContext();

        List<ZApptSummary> appts = new ArrayList<ZApptSummary>();

        for (ZApptSummary appt : mAppointments.getAppointments()) {
            if (mStart == -1 || mEnd ==-1 || appt.isInRange(mStart, mEnd)) {
                appts.add(appt);
            }
        }

        RawLayoutInfo rawInfo = computeRawLayout(appts);

        long msecsStart = mStart + (1000*60*60*9);
        long msecsEnd = mStart + (1000*60*60*18);
        long msecsIncr = (1000*60*15);

        int numCols = rawInfo.columns.size();
        double perCol = 100.0/numCols;
        HashMap<ZApptSummary, ZApptColumnLayoutBean> mDoneAppts = new HashMap<ZApptSummary, ZApptColumnLayoutBean>();
        int rowNum = 0;
        for (long msecsRangeStart = msecsStart, msecsRangeEnd = msecsStart + msecsIncr;
             msecsRangeStart < msecsEnd; msecsRangeStart += msecsIncr, msecsRangeEnd += msecsIncr) {

            List<ZApptColumnLayoutBean> mColumns = new ArrayList<ZApptColumnLayoutBean>();

            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                List<ZApptSummary> rawColumn = rawInfo.columns.get(colIndex); 
                ZApptSummary match = null;
                for (ZApptSummary a : rawColumn) {
                    if (a.isInRange(msecsRangeStart, msecsRangeEnd)) {
                        match = a;
                        break;
                    }
                }
                ZApptColumnLayoutBean col = new ZApptColumnLayoutBean();
                if (match != null) {
                    col.setAppt(match);
                    ZApptColumnLayoutBean existingCol = mDoneAppts.get(match);
                    if (existingCol == null) {
                        col.setIsFirst(true);
                        mDoneAppts.put(match, col);
                        col.setRowSpan(computeRowSpan(match, msecsIncr, msecsStart, msecsEnd));
                        col.setColSpan(computeColSpan(match.getStartTime(), match.getEndTime(), rawInfo.columns, colIndex+1));
                    } else {
                        col.setColSpan(existingCol.getColSpan());
                    }
                } else {
                    col.setRowSpan(1);
                    col.setColSpan(computeColSpan(msecsRangeStart, msecsRangeEnd, rawInfo.columns, colIndex+1));
                }
                col.setWidth((int)(perCol*col.getColSpan()));
                mColumns.add(col);
                if (col.getColSpan() > 1)
                        colIndex += col.getColSpan();
            }
            jctxt.setAttribute(mVar, new ZApptRowLayoutBean(mColumns, rowNum++, msecsRangeStart));
            body.invoke(null);
        }
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

    private RawLayoutInfo computeRawLayout(List<ZApptSummary> appts) {
        RawLayoutInfo result = new RawLayoutInfo();
        result.columns = new ArrayList<List<ZApptSummary>>();
        result.columns.add(new ArrayList<ZApptSummary>());
        for (ZApptSummary appt : appts) {
            boolean overlap = false;
            for (List<ZApptSummary> col : result.columns) {
                overlap = false;
                for (ZApptSummary currentAppt : col) {
                    overlap = appt.isOverLapping(currentAppt);
                    if (overlap) break;
                }
                if (!overlap) {
                    col.add(appt);
                    if (!appt.isAllDay()) {
                        if ((result.earliestAppt == null || appt.getStartTime() < result.earliestAppt.getStartTime()))
                            result.earliestAppt = appt;
                        if ((result.latestAppt == null || appt.getEndTime() > result.latestAppt.getStartTime()))
                            result.latestAppt = appt;
                    }
                    break;
                }
            }
            // if we got through all columns with overlap, add one
            if (overlap) {
                List<ZApptSummary> newCol = new ArrayList<ZApptSummary>();
                newCol.add(appt);
                result.columns.add(newCol);
            }
        }
        return result;
    }

    public static class RawLayoutInfo {
        ZApptSummary earliestAppt; // appt with earliest start time
        ZApptSummary latestAppt; // appt with latest end time
        List<List<ZApptSummary>> columns;

        public List<List<ZApptSummary>> getColumns() {
            return columns;
        }
    }
}
