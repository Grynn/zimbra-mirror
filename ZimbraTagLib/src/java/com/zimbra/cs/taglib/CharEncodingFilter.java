package com.zimbra.cs.taglib;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CharEncodingFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {}

    public void destroy() { }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            req.setCharacterEncoding("UTF-8");
            chain.doFilter(req, res);
        } catch (UnsupportedEncodingException e) {
            // this should never happen
            throw new ServletException("setCharacterEncoding to UTF-8 failed", e);
        }
    }
}
