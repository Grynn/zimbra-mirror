package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequirePostTag extends ZimbraSimpleTag {

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        PageContext pageContext = (PageContext) ctxt;
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        try {
            if (!request.getMethod().equals("POST"))
            throw ServiceException.INVALID_REQUEST("only valid with POST", null);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
