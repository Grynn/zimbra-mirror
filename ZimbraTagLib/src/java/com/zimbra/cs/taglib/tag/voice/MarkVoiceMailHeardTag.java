package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class MarkVoiceMailHeardTag extends ZimbraSimpleTag {

    private String mId;
    private String mPhone;
    private String mVar;
    private boolean mHeard;

    public void setId(String id) { this.mId = id; }
    public void setPhone(String phone) { this.mPhone = phone; }
    public void setHeard(boolean heard) { this.mHeard = heard; }
    public void setVar(String var) { mVar = var; }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            ZMailbox.ZActionResult result = mbox.markVoiceMailHeard(mPhone, mId, mHeard);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
