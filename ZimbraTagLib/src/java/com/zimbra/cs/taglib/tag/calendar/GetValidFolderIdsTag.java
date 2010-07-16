
package com.zimbra.cs.taglib.tag.calendar;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;


import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;


public class GetValidFolderIdsTag extends ZimbraSimpleTag {
    private String mVar;
    private String mVarException;
    private String mFolderId;
    private ZMailboxBean mMailbox;

    public void setVar(String var) { this.mVar = var; }
    public void setVarexception(String varException) { this.mVarException = varException; }
    public void setFolderid(String folderId) { this.mFolderId = folderId; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();
            String result = mbox.getValidFolderIds(mFolderId);
            jctxt.setAttribute(mVar, result,  PageContext.PAGE_SCOPE);
        } catch (ServiceException e){
            if (mVarException != null) {
                jctxt.setAttribute(mVarException, e,  PageContext.PAGE_SCOPE);
                jctxt.setAttribute(mVar, "",  PageContext.PAGE_SCOPE);
            } else {
                throw new JspTagException(e);
            }
        }

    }
}