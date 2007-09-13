package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetInfoJSONTag extends ZimbraSimpleTag {

    private String mVar;
    private String mAuthToken;
    private String mItemsPerPage;
    private String mTypes;
    private boolean mDoSearch;

    public void setVar(String var) { this.mVar = var; }
    public void setAuthtoken(String authToken) { this.mAuthToken = authToken; }
    public void setDosearch(boolean doSearch) { mDoSearch = doSearch; }
    public void setItemsperpage(String itemsPerPage) { mItemsPerPage = itemsPerPage; }
    public void setTypes(String types) { mTypes = types; }
    
    public void doTag() throws JspException, IOException {
        try {
            JspContext ctxt = getJspContext();
            String url = ZJspSession.getSoapURL((PageContext) ctxt);
            Element e = ZMailbox.getBootstrapJSON(url, mAuthToken, mDoSearch, mItemsPerPage, mTypes);
            ctxt.setAttribute(mVar, e.toString(),  PageContext.REQUEST_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
