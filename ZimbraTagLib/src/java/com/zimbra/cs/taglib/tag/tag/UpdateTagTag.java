package com.zimbra.cs.taglib.tag.tag;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZTag;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class UpdateTagTag extends ZimbraSimpleTag {

    private String mId;
    private String mName;
    private ZTag.Color mColor;

    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setColor(String color) throws ServiceException { mColor = ZTag.Color.fromString(color); }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().updateTag(mId, mName, mColor);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
