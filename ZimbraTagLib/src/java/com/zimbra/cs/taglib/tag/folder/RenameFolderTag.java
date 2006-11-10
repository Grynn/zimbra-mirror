package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.JspException;
import java.io.IOException;

public class RenameFolderTag extends ZimbraSimpleTag {

    private String mId;
    private String mNewName;

    public void setId(String id) { mId = id; }
    public void setNewname(String newname) { mNewName = newname; }

    public void doTag() throws JspException, IOException {
        try {
            getMailbox().renameFolder(mId, mNewName);
        } catch (ServiceException e) {
            throw new JspException(e);
        }
    }
}
