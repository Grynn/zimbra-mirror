package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMimePartBean;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FixupMessageComposeTag extends ZimbraSimpleTag {


    private ZMessageComposeBean mCompose;
    private ZMessageBean mMessage;


    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        ZMailbox mailbox = getMailbox();

        if (mCompose.getOriginalAttachmentNames().isEmpty())
            return;

        List<ZMimePartBean> parts = mCompose.getOriginalAttachments();
        if (parts == null) {
            parts = new ArrayList<ZMimePartBean>();
            mCompose.setOrignalAttachments(parts);
        }

        List<ZMimePartBean> attachments = mMessage.getAttachments();

        for (String name : mCompose.getOriginalAttachmentNames()) {
            for (ZMimePartBean part : attachments) {
                if (part.getPartName().equals(name))
                parts.add(part);
            }
        }
    }
}
