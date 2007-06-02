package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZUserAgentBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetUserAgentTag extends ZimbraSimpleTag {

    private static final String UA_SESSION = "ZUserAgentBean.SESSION";
    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        ctxt.setAttribute(mVar, getUserAgent(ctxt),  PageContext.REQUEST_SCOPE);
    }

    public static ZUserAgentBean getUserAgent(JspContext ctxt) {
        PageContext pctxt = (PageContext) ctxt;
        HttpServletRequest req = (HttpServletRequest) pctxt.getRequest();

        ZUserAgentBean ua = (ZUserAgentBean) ctxt.getAttribute(UA_SESSION, PageContext.SESSION_SCOPE);
        if ( ua == null) {
            ua = new ZUserAgentBean(req.getHeader("User-Agent"));
            ctxt.setAttribute(UA_SESSION, ua,  PageContext.SESSION_SCOPE);

        }
        return ua;
    }
    
}
