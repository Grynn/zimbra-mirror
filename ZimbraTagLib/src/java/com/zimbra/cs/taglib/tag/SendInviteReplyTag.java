package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.zclient.ZDateTime;
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
    
    private ZMessageComposeBean mCompose;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            ZOutgoingMessage m = mCompose.toOutgoingMessage(mbox);

            ZDateTime instance = mCompose.getInviteReplyInst() == 0 ? null :  new ZDateTime(mCompose.getInviteReplyInst(), mCompose.getInviteReplyAllDay(), mbox.getPrefs().getTimeZone());

            String compNum = mCompose.getCompNum();
            if (compNum != null && compNum.length()==0)
                compNum = "0";

            String instCompNum = mCompose.getInstanceCompNum();
            if (instCompNum != null && instCompNum.length()==0)
                instCompNum = null;


            if (instance != null && instCompNum != null)
                compNum = instCompNum;

            ZSendInviteReplyResult response = mbox.sendInviteReply(mCompose.getMessageId(), compNum, ReplyVerb.fromString(mCompose.getInviteReplyVerb()), true, null, instance, m);
            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
