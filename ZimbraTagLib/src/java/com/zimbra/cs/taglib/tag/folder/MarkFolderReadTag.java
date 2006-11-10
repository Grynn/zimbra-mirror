package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.JspException;
import java.io.IOException;

public class MarkFolderReadTag extends ZimbraSimpleTag {

    private String mId;

    public void setId(String id) { mId = id; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().markFolderRead(mId);
        } catch (ServiceException e) {
            throw new JspException(e);
        }
    }
}
