package com.zimbra.webClient.xss;

import com.zimbra.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rajendra
 * Date: Aug 19, 2008
 * Time: 10:24:28 AM
 * The request wrapper class to filter out harmful param values (xss attacks)
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
    }

    public String getParameter(String s) {
        String val = super.getParameter(s);
        if (val != null)
            val = StringUtil.escapeHtml(val);
        return val;
    }

    public String[] getParameterValues(String s) {
        String[] vals = super.getParameterValues(s);
        if (vals != null) {
            for (int i = 0; i < vals.length; i++) {
                vals[i] = StringUtil.escapeHtml(vals[i]);
            }
        }
        return vals;
    }

    public Map getParameterMap() {
        Map m = super.getParameterMap();
        if (m != null) {
            Iterator it = m.keySet().iterator();
            while (it.hasNext()) {
                Object k = it.next();
                Object v = m.get(k);
                if (v instanceof String) {
                    v = StringUtil.escapeHtml((String) v);
                }
            }
        }
        return m;
    }
}
