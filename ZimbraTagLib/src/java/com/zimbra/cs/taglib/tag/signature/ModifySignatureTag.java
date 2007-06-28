package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifySignatureTag extends ZimbraSimpleTag {

    private String mId;
    private String mName;
    private String mValue;

    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifySignature(new ZSignature(mId, mName, mValue));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
