package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean.Action;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZIdentity;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Set;
import java.util.List;

public class ComputeMessageComposeTag extends ZimbraSimpleTag {

    public static final String ACTION_NEW = "new";
    public static final String ACTION_REPLY = "reply";
    public static final String ACTION_REPLY_ALL = "replyAll";
    public static final String ACTION_FORWARD = "forward";
    public static final String ACTION_RESEND = "resend";
    public static final String ACTION_DRAFT = "draft";            

    private String mVar;
    private ZMessageBean mMessage;
    private String mAction;

    public void setVar(String var) { this.mVar = var; }

    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setAction(String action) { mAction = action; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext jctxt = getJspContext();
            PageContext pc = (PageContext) jctxt;
            ZMailbox mailbox = getMailbox();

            Set<String> aliases = mailbox.getAccountInfo(false).getEmailAddresses();

            ZMessageComposeBean compose;
            List<ZIdentity> identities = mailbox.getAccountInfo(false).getIdentities();

            if (ACTION_REPLY.equals(mAction))
                compose = new ZMessageComposeBean(Action.REPLY, mMessage, identities, aliases, pc);
            else if (ACTION_REPLY_ALL.equals(mAction))
                compose = new ZMessageComposeBean(Action.REPLY_ALL, mMessage, identities, aliases, pc);
            else if (ACTION_FORWARD.equals(mAction))
                compose = new ZMessageComposeBean(Action.FORWARD, mMessage, identities, aliases, pc);
            else if (ACTION_RESEND.equals(mAction))
                compose = new ZMessageComposeBean(Action.RESEND, mMessage, identities, aliases, pc);
            else if (ACTION_DRAFT.equals(mAction))
                compose = new ZMessageComposeBean(Action.DRAFT, mMessage, identities, aliases, pc);
            else
                compose = new ZMessageComposeBean(Action.NEW, null, identities, aliases, pc);

            jctxt.setAttribute(mVar, compose, PageContext.PAGE_SCOPE);
            
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }



}
