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
package com.zimbra.cs.taglib.tag.calendar;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZAppointmentHit;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ZApptSummaryResult;
import com.zimbra.cs.zclient.ZMailbox.ZGetFreeBusyResult;
import com.zimbra.cs.zclient.ZSearchParams;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class GetFreeBusyAppointmentsTag extends ZimbraSimpleTag {

    private String mVar;
    private String mEmail;
    private String mVarException;
    private long mStart;
    private long mEnd;
    private ZMailboxBean mMailbox;

    public void setVar(String var) { this.mVar = var; }
    public void setEmail(String email) { this.mEmail = email; }
    public void setVarexception(String varException) { this.mVarException = varException; }

    public void setStart(long start) { this.mStart = start; }
    public void setEnd(long end) { this.mEnd = end; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();

            List<ZGetFreeBusyResult> result =
                mbox.getFreeBusy(mEmail, mStart, mEnd);
            List<ZAppointmentHit>appts = mbox.createAppointmentHits(result.get(0).getTimeSlots());
            jctxt.setAttribute(mVar, new ZApptSummariesBean(appts),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            if (mVarException != null) {
                jctxt.setAttribute(mVarException, e,  PageContext.PAGE_SCOPE);
                jctxt.setAttribute(mVar, new ZApptSummariesBean(new ArrayList<ZAppointmentHit>()),  PageContext.PAGE_SCOPE);
            } else {
                throw new JspTagException(e);
            }
        }
    }
}
