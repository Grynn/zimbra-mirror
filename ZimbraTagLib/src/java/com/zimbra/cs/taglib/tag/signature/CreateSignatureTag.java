package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateSignatureTag extends ZimbraSimpleTag {

    private String mName;
    private String mVar;
    private String mValue;

    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }
    public void setVar(String var) { mVar = var; }

    public void doTag() throws JspException, IOException {
        try {
            String id = getMailbox().createSignature(new ZSignature(mName, mValue));
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
