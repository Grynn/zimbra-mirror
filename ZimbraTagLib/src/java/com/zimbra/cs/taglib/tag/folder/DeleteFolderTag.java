package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class DeleteFolderTag extends ZimbraSimpleTag {

    private String mId;

    public void setId(String id) { mId = id; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().deleteFolder(mId);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
