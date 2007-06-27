package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModifySignatureTag extends ZimbraSimpleTag {

    private String mId;
    private String mName;
    private String mValue;

    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }

    public void doTag() throws JspException, IOException {
        try {
            Map<String,Object> attrs = new HashMap<String,Object>();
            attrs.put(Provisioning.A_zimbraPrefSignatureId, mId);
            attrs.put(Provisioning.A_zimbraPrefSignatureName, mName);
            attrs.put(Provisioning.A_zimbraPrefMailSignature, mValue);
            getMailbox().modifySignature(new ZSignature(mName, attrs));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
