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

import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZApptSummary;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;

public class ForEachAppointmentTag extends ZimbraSimpleTag {

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
        for (ZApptSummary appt : mAppointments.getAppointments()) {
            if (mStart == -1 || mEnd ==-1 || appt.isInRange(mStart, mEnd)) {
                jctxt.setAttribute(mVar, appt);
                body.invoke(null);
            }
        }
    }
}
