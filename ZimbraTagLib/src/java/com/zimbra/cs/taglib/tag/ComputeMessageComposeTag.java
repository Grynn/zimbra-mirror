package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ComputeMessageComposeTag extends ZimbraSimpleTag {

    public static final String ACTION_NEW = "new";
    public static final String ACTION_REPLY = "reply";
    public static final String ACTION_REPLY_ALL = "replyAll";
    public static final String ACTION_FORWARD = "forward";        

    private String mVar;
    private ZMessageBean mMessage;
    private String mAction;

    public void setVar(String var) { this.mVar = var; }

    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setAction(String action) { mAction = action; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        //ZMailbox mbox = getMailbox();

        ZMessageComposeBean compose;
        if (ACTION_REPLY.equals(mAction))
            compose = ZMessageComposeBean.reply(mMessage, null);
        else if (ACTION_REPLY_ALL.equals(mAction))
            compose = ZMessageComposeBean.replyAll(mMessage, null);
        else if (ACTION_FORWARD.equals(mAction))
            compose = ZMessageComposeBean.forward(mMessage, null);
        else
            compose = ZMessageComposeBean.newMessage(null);
        jctxt.setAttribute(mVar, compose, PageContext.PAGE_SCOPE);
    }



}
