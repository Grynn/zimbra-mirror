package com.zimbra.cs.taglib.tag;

import com.zimbra.common.calendar.TZIDMapper;
import com.zimbra.common.calendar.TZIDMapper.TZ;
import com.zimbra.cs.taglib.bean.ZTimeZoneBean;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.Iterator;

public class ForEachTimeZoneTag extends ZimbraSimpleTag {

    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspFragment body = getJspBody();
        if (body == null) return;
        JspContext jctxt = getJspContext();
        Iterator<TZ> zones = TZIDMapper.iterator();
        while (zones.hasNext()) {
            TZ tz = zones.next();
            jctxt.setAttribute(mVar, new ZTimeZoneBean(tz));
            body.invoke(null);
        }
    }
}
