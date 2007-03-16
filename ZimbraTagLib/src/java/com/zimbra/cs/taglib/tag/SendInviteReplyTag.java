package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ReplyVerb;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZSendInviteReplyResult;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class SendInviteReplyTag extends ZimbraSimpleTag {

    private String mVar;
    private String mTo;
    private String mReplyTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mSubject;
    private String mContentType = "text/plain";
    private String mContent;
    private String mReplyType;
    private String mInReplyTo;
    private String mMessageId;
    private String mMessages;
    private String mAttachments;
    private ZMessageComposeBean mCompose;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            ZOutgoingMessage m = mCompose.toOutgoingMessage(mbox);

            ZSendInviteReplyResult response = mbox.sendInviteReply(mCompose.getMessageId(), "0", ReplyVerb.fromString(mCompose.getInviteReplyVerb()), true, null, null, m);
            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
