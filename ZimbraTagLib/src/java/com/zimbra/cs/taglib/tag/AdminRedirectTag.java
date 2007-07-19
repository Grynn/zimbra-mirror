package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.ZJspSession;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import java.io.IOException;

public class AdminRedirectTag extends ZimbraSimpleTag {

    String mDefaultPath = "/zimbraAdmin";

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pageContext = (PageContext) jctxt;

        String adminRedirect = ZJspSession.getAdminLoginRedirectUrl(pageContext, mDefaultPath);
        if (adminRedirect != null) {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            response.sendRedirect(adminRedirect);
            throw new SkipPageException();
        }
    }
}
