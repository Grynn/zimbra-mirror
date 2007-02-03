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

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.zclient.ZApptSummary;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
        
        jctxt.setAttribute(mVar, rawInfo);
        body.invoke(null);
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
