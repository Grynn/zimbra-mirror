/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.msg;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMessage;
import com.zimbra.cs.zclient.ZGetMessageParams;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class GetMessageTag extends ZimbraSimpleTag {
    
    private String mVar;
    private String mId;
    private boolean mMarkread;
    private boolean mWanthtml;
    private boolean mWantHtmlSet;
    private boolean mNeuterimages;
    private boolean mRaw;
    private String mPart;
    private ZMailboxBean mMailbox;
    private String mReqHdrs;
    
    public void setVar(String var) { this.mVar = var; }
    
    public void setId(String id) { this.mId = id; }    
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }

    public void setMarkread(boolean markread) { this.mMarkread = markread; }
    public void setWanthtml(boolean wanthtml) {
        this.mWanthtml = wanthtml;
        this.mWantHtmlSet = true;
    }

    public void setRaw(boolean raw) { this.mRaw = raw; }
    public void setNeuterimages(boolean neuter) { this.mNeuterimages = neuter; }
    public void setPart(String part) { this.mPart = part; }

    public void setRequestHeaders(String reqhdrs) { this.mReqHdrs = reqhdrs; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();
            boolean wantHtml = mWantHtmlSet ? mWanthtml : mbox.getPrefs().getMessageViewHtmlPreferred();
            ZGetMessageParams params = new ZGetMessageParams();
            params.setId(mId);
            params.setMarkRead(mMarkread);
            params.setWantHtml(wantHtml);
            params.setNeuterImages(mNeuterimages);
            params.setRawContent(mRaw);
            params.setPart(mPart);
            params.setReqHeaders(mReqHdrs);
            ZMessage message = mbox.getMessage(params);
            jctxt.setAttribute(mVar, new ZMessageBean(message),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
