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
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZInvite;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZInvite.ZDateTime;
import com.zimbra.cs.zclient.ZInvite.ZComponent;
import com.zimbra.cs.zclient.ZMailbox.ZAppointmentResult;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class SaveAppointmentTag extends ZimbraSimpleTag {

    private String mVar;

    private ZMessageComposeBean mCompose;
    private ZMessageBean mMessage;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }
    public void setMessage(ZMessageBean message) { mMessage = message; }
    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            ZInvite inv = mCompose.toInvite(mbox, mMessage);

            ZInvite previousInv = mMessage != null ? mMessage.getInvite() : null;
            ZComponent prevComp = previousInv != null ? previousInv.getComponent() : null;

            mCompose.setInviteContent(mbox, inv, previousInv);

            ZDateTime exceptionId = prevComp != null && prevComp.isException() ? prevComp.getStart() : null;

            ZOutgoingMessage m = mCompose.toOutgoingMessage(mbox);

            String folderId = mCompose.getApptFolderId();
            if (folderId == null || folderId.length() == 0)
                folderId = ZFolder.ID_CALENDAR;

            ZAppointmentResult response = null;

            if (mMessage != null) {
                if (mCompose.getUseInstance()) {
                    if (mCompose.getExceptionInviteId() != null && mCompose.getExceptionInviteId().length() > 0) {
                        response = mbox.modifyAppointment(mCompose.getExceptionInviteId(), /*TODO:pass thru */ "0", exceptionId , m, inv);
                    } else {
                        exceptionId = new ZDateTime(mCompose.getInstanceStartTime(), mCompose.getAllDay(), mbox.getPrefs().getTimeZone());
                        response = mbox.createAppointmentException(mCompose.getInviteId(), "0", exceptionId, m, inv, null);
                    }
                } else {
                    response = mbox.modifyAppointment(mCompose.getInviteId(), /*TODO:pass thru */ "0", exceptionId, m, inv);
                }

            } else {
                response = mbox.createAppointment(folderId, null, m, inv, null);
            }

            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
