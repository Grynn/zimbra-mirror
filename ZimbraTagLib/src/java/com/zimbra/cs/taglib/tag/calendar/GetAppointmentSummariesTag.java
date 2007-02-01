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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZApptSummariesBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZApptSummary;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZFolder.View;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class GetAppointmentSummariesTag extends ZimbraSimpleTag {

    private String mVar;
    private long mStart;
    private long mEnd;
    private String mFolderId;

    public void setVar(String var) { this.mVar = var; }
    public void setStart(long start) { this.mStart = start; }
    public void setEnd(long end) { this.mEnd = end; }
    public void setFolderid(String folderId) { this.mFolderId = folderId; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            
            if (mFolderId == null || mFolderId.length() == 0) {
                StringBuilder sb = new StringBuilder();
                getCheckedCalendarFolders(mbox.getUserRoot(), sb);
                if (sb.length() > 0) mFolderId = sb.toString();
            }

            List<ZApptSummary> appts = null;
            if (mFolderId == null || mFolderId.length() == 0) {
                // if non are checked, return no appointments (to match behavior of ajax client
                appts = new ArrayList<ZApptSummary>();
            } else if (mFolderId.indexOf(',') == -1) {
                appts = mbox.getApptSummaries(mStart, mEnd, mFolderId);
            } else {
                appts = new ArrayList<ZApptSummary>();
                for (String folder : mFolderId.split(",")) {
                    appts.addAll(mbox.getApptSummaries(mStart, mEnd, folder));
                }
            }
            jctxt.setAttribute(mVar, new ZApptSummariesBean(appts),  PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    private void getCheckedCalendarFolders(ZFolder f, StringBuilder sb) {
        if (f.getDefaultView() == View.appointment && f.isCheckedInUI()) {
            if (sb.length() > 0) sb.append(',');
            sb.append(f.getId());
        }
        for (ZFolder child : f.getSubFolders()) {
            getCheckedCalendarFolders(child, sb);
        }
    }
}
