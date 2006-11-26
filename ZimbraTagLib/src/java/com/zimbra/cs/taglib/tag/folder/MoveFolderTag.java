package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class MoveFolderTag extends ZimbraSimpleTag {

    private String mId;
    private String mParentId;

    public void setId(String id) { mId = id; }
    public void setParentid(String parentid) { mParentId = parentid; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().moveFolder(mId, mParentId);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
