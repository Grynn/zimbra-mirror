package com.zimbra.webClient.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rajendra
 * Date: Aug 19, 2008
 * Time: 10:38:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class XssFilter implements Filter {

    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws
            ServletException {
        System.out.println("XSS Filter initialized");
        this.filterConfig = filterConfig;
    }


    public void destroy() {

        System.out.println("XSS Filter destroyed");

        this.filterConfig = null;

    }


    public void doFilter(ServletRequest request, ServletResponse response,

                         FilterChain chain)

            throws IOException, ServletException {

        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);

    }
}
