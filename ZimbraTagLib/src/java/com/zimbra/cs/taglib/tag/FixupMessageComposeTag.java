package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;

import javax.servlet.jsp.JspException;
import java.io.IOException;

public class FixupMessageComposeTag extends ZimbraSimpleTag {


    private ZMessageComposeBean mCompose;
    private ZMessageBean mMessage;


    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }

    public void doTag() throws JspException, IOException {
        mCompose.setOrignalAttachments(mMessage.getAttachments());
    }
}
