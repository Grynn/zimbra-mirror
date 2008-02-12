/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.BeanUtils;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZVoiceMailItemHit;
import com.zimbra.common.service.ServiceException;

import java.util.Date;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import com.zimbra.cs.taglib.tag.i18n.I18nUtil;
import java.io.IOException;

// Sets up the compose bean when a voice mail is being forwarded/replied
public class VoiceMailComposeTag extends ZimbraSimpleTag {

    public static final String F_reply = "reply";

    private ZMessageComposeBean mCompose;
    private String mPhone;
    private String mVoiceId;
    private String mOperation;

    public void setCompose(ZMessageComposeBean compose) { this.mCompose = compose; }
    public void setPhone(String phone) { this.mPhone = phone; }
    public void setVoiceid(String id) { this.mVoiceId = id; }
    public void setOperation(String operation) { this.mOperation = operation; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext jctxt = getJspContext();
            PageContext pageContext = (PageContext) jctxt;

            String subjectKey = F_reply.equals(mOperation) ? "voiceMailReplySubject" : "voiceMailForwardSubject";
            String subject = I18nUtil.getLocalizedMessage(pageContext, subjectKey);
            mCompose.setSubject(subject);

            ZVoiceMailItemHit hit = ZVoiceMailItemHit.deserialize(mVoiceId, mPhone);
            Object[] bodyArgs = {
                    hit.getDisplayCaller(),
                    BeanUtils.displayDuration(pageContext, hit.getDuration()),
                    BeanUtils.displayMsgDate(pageContext, new Date(hit.getDate())),
            };
            String body = I18nUtil.getLocalizedMessage(pageContext, "voiceMailBody", bodyArgs);
            mCompose.setContent(body);
        } catch (ServiceException e) {
            throw new JspTagException("voice mail compose failed", e);
        }
    }
}