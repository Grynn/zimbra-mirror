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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMimePartBean;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.List;

public class FixupMessageComposeTag extends ZimbraSimpleTag {


    private ZMessageComposeBean mCompose;
    private ZMessageBean mMessage;
    private boolean mNewAttachments;


    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }

    public void setNewattachments(boolean newAttachments) { mNewAttachments = newAttachments; }

    public void doTag() throws JspException, IOException {
        List<ZMimePartBean> attachments = mMessage.getAttachments();
        mCompose.setOrignalAttachments(attachments);
        if (mNewAttachments) {
            for (ZMimePartBean part : attachments) {
                mCompose.setCheckedAttachmentName(part.getPartName(),(part.getContentId() == null || part.getContentId().equals("") ? "true" : part.getContentId()));
            }
        }
    }
}
