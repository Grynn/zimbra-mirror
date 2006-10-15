package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZConversationBean;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.zclient.ZConversation;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetConversationTag extends ZimbraSimpleTag {

    private String mVar;
    private String mId;

    public void setVar(String var) { this.mVar = var; }

    public void setId(String id) { this.mId = id; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            ZConversation conv = mbox.getConversation(mId);
            jctxt.setAttribute(mVar, new ZConversationBean(conv),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            getJspContext().getOut().write(e.toString());
        }
    }
}
