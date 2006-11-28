package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZComposeUploaderBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ComputeComposeUploaderTag extends ZimbraSimpleTag {

    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        jctxt.setAttribute(mVar, new ZComposeUploaderBean((HttpServletRequest) pc.getRequest()), PageContext.REQUEST_SCOPE);
    }
}
