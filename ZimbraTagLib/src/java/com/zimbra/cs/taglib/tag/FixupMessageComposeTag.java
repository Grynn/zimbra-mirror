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
                mCompose.setCheckedAttachmentName(part.getPartName());
            }
        }
    }
}
