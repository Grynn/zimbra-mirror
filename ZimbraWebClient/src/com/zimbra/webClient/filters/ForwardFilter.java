/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.webClient.filters;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ForwardFilter implements Filter {

    private ServletContext ctxt;
    
    @Override public void init(FilterConfig filterConfig) throws ServletException {
        ctxt = filterConfig.getServletContext().getContext("/service");
    }

    @Override public void destroy() { }

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (ctxt == null ||
                !(request instanceof HttpServletRequest) ||
                !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        String path = req.getServletPath();
        
        // CalDAV service discovery (bug 35008). 
        // redirect any WebDAV request sent to the root URI "/" to "/dav/"
        if ("/".equalsIgnoreCase(path) && req.getMethod().equals("PROPFIND")) {
            RequestDispatcher dispatcher = ctxt.getRequestDispatcher("/dav");
            dispatcher.forward(req, resp);
        } else if ("/robots.txt".equalsIgnoreCase(path)) {
            RequestDispatcher dispatcher = ctxt.getRequestDispatcher("/robots.txt");
            dispatcher.forward(req, resp);
        } else {
            chain.doFilter(request, response);
        }
    }
}
