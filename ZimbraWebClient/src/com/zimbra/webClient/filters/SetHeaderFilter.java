/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 VMware, Inc.
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;

public final class SetHeaderFilter extends com.zimbra.cs.servlet.SetHeaderFilter {
    private static final Log LOG = ZimbraLog.webclient;

    // --------------------------------------------------------------
    // Class constants
    // --------------------------------------------------------------
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String HEADER_VAL_GZIP = "gzip";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String HEADER_VARY = "Vary";
    private static final String ATTR_NAME_VERSION = "version";
    private static final String ATTR_NAME_FILE_EXTENSION = "fileExtension";
    private static final String HEADER_CACHE_CONTROL = "Cache-control";
    private static final String NO_CACHE_CONTROL_VALUE = "no-store, no-cache, must-revalidate, max-age=0";
    private static final String NO_CACHE_PRAGMA_VALUE = "no-cache";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final String HEADER_EXPIRES = "Expires";
    private static final String ALREADY_EXPIRED = "Tue, 24 Jan 2000 20:46:50 GMT";
    private static final String HEADER_CONTENT_ENCODING= "Content-encoding";
    private static final String TIME_FORMAT = "EEE, d MMM yyyy HH:mm:ss";
    private static final String GMT = "GMT";
    private static final String HEADER_X_FRAME_OPTIONS = "X-Frame-Options";
    private static final String X_FRAME_OPTIONS_VALUE = "SAMEORIGIN";

    protected int debug = 0;
    protected String jsVersion = null;
	protected boolean allowInFrame = false;
    protected FilterConfig config = null;
    protected Pattern extensionPattern = null;
    protected boolean serverSupportsGzip = true;
    protected String gzipExtension = null;
    protected int expiresValue = 0;
    protected boolean isProdMode = true;
    protected String futureCacheControl = null;
    protected Pattern noCachePattern = null;

    private String mailUrl;
    private String mailUrlHome;
    private String mailUrlUser;


    @Override
    protected Log getLogger() {
        return LOG;
    }

    // --------------------------------------------------------------
    // init helper methods
    // --------------------------------------------------------------

    private String getInitParameter (String key, String def) {
        String value = this.config.getInitParameter(key);
        if (value != null) {
            value = value.trim();
        } else {
            value = def;
        }
        return value;
    }

    private int getInitParameterInt (String key, int def) {
        int retVal = def;
        String value = this.config.getInitParameter(key);
        if (value!=null) {
            retVal = Integer.parseInt(value);
        }
        return retVal;
    }

    private boolean getInitParameterBool (String key, boolean def) {
        String value = this.config.getInitParameter(key);
        boolean ret = def;
        if (value != null) {
            ret = Boolean.valueOf(value);
        }
        return ret;
    }

    private void getExtensionsRegex () {
        String value = getInitParameter("extensions", ".zgz");
        String [] fileExtensions = value.split(",");
        StringBuffer extensionRegexSb = new StringBuffer(".*(");
        for (int i = 0; i < fileExtensions.length; ++i){
            extensionRegexSb.append(fileExtensions[i].trim());
            if (i < fileExtensions.length - 1){
                extensionRegexSb.append('|');
            }
        }
        extensionRegexSb.append(")");
        String extensionRegex = extensionRegexSb.toString();
        extensionPattern = Pattern.compile(extensionRegex);
    }

    private void getNoCachePatternList () {
        String value = getInitParameter("noCachePatternList", ".jsp");
        String [] tempList = value.split(",");
        StringBuffer noCachePatternSb = new StringBuffer(".*(");
        for (int i = 0; i < tempList.length; ++i){
            /**
             * If the value of zimbraMailURL is set to "/", the noCachePatternList
             * will have "//". Replace the "//", if any, by "/" before the
             * pattern match.
             */
            noCachePatternSb.append(tempList[i].trim().replaceAll("//", "/"));
            if (i < tempList.length - 1){
                noCachePatternSb.append('|');
            }
        }
        noCachePatternSb.append(")");
        String noCachePatternRegex = noCachePatternSb.toString();
        noCachePattern = Pattern.compile(noCachePatternRegex);
    }

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.config = filterConfig;
        if (filterConfig != null) {
            debug = getInitParameterInt("debug", 0);
            jsVersion = getInitParameter("jsVersion", "0");
			allowInFrame = getInitParameterBool("allowInFrame", false);
            serverSupportsGzip = getInitParameterBool("shouldSupportGzip",true);
            getExtensionsRegex();
            getNoCachePatternList();
            gzipExtension = getInitParameter("GzipExtension", ".zgz");
            expiresValue = getInitParameterInt("Expires", 0);
            futureCacheControl = "public, max-age=" + expiresValue;
            isProdMode = getInitParameterBool("ProdMode", true);

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            mailUrl = (String) envCtx.lookup("mailUrl");
        } catch (NamingException ne) {
        ne.printStackTrace();
        }

        if (mailUrl == null) {
        mailUrl = "/zimbra";
        }
        mailUrlHome = mailUrl + "/home/";
          mailUrlUser = mailUrl + "/user/";

            if (debug > 0) {
                System.out.println("Filter variables:");
                System.out.println("  debug = " + debug);
                System.out.println("  jsVersion = " + jsVersion);
                System.out.println("  serverSupportsGzip = " + serverSupportsGzip);
                System.out.println("  gzipExtension = " + gzipExtension);
                System.out.println("  expiresValue = " + expiresValue);
                System.out.println("  isProdMoe = " + isProdMode);
                System.out.println("  extensionRegex = " + extensionPattern.pattern());
                System.out.println("  noCachPattern = " + noCachePattern.pattern());
                System.out.println("  noCachPattern = " + noCachePattern.pattern());
        System.out.println("  mailUrl = " + mailUrl);
        System.out.println("  mailUrlHome = " + mailUrlHome);
        System.out.println("  mailUrlUser = " + mailUrlUser);
            }
        }
    }

    /**
     * Take this filter out of service.
     */
    @Override
    public void destroy() {
        super.destroy();
        this.config = null;
    }

    private boolean isHttpRequest(ServletRequest request,
                                  ServletResponse response) {
        boolean ret = false;
        if ((request instanceof HttpServletRequest) &&
            (response instanceof HttpServletResponse)){
            ret = true;
        }
        return ret;
    }

    private boolean shouldProcess(ServletRequest request,
                                  ServletResponse response) {
        boolean process = false;
        if ( isHttpRequest(request, response) && extensionPattern != null){
            process = true;
        }
        return process;
    }

    // --------------------------------------------------------------
    // Main filter methods
    // --------------------------------------------------------------

    /**
     * Main filter method.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        super.doFilter(request, response, chain);
        // Clear logging context before each servlet request.
        ZimbraLog.clearContext();
    }

    @Override
    public boolean doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (debug > 0) {
            System.out.println("@doFilter");
        }
        if (!super.doFilter(request, response)) {
            return false;
        }

        /***/
        // TODO: We could pass all the init-params through but we only need one for now.
        request.setAttribute("init.Expires", String.valueOf(expiresValue));
        /***
        Enumeration pnames = this.config.getInitParameterNames();
        while (pnames.hasMoreElements()) {
            String pname = (String)pnames.nextElement();
            String pvalue = this.config.getInitParameter(pname);
            if (pvalue == null) continue;
            request.setAttribute("init."+pname, pvalue);
        }
        /***/

        // make sure we're dealing with an http request
        HttpServletRequest req;
        HttpServletResponse resp;
        boolean proceed = shouldProcess(request, response);
        if (proceed){
            req = (HttpServletRequest)request;
            resp = (HttpServletResponse)response;
        } else {
            if (debug > 0){
                System.out.println("not proceeding with filter");
            }
            return true;
        }

        String uri = req.getRequestURI();

        // before we check whether we can compress, let's check
        // what sort of cache control headers we should use for this
        // request.
        setCacheControlHeaders(req, resp);

        boolean supportCompression;
        supportCompression = this.supportsGzip(req, resp);
        setRequestAttributes(req, supportCompression);
        secureCookieIfNecessary(req);
        if (!supportCompression) {
            if (debug > 0) {
                System.out.println("doFilter gets called wo compression");
            }
        } else {
            Matcher m = extensionPattern.matcher(uri);
            if (debug > 0) {
                System.out.println("Seeing if uri " + uri + " matches the extension regex of "
                        + extensionPattern.pattern());
                System.out.println("Does it? " + m.matches());
            }
            if ( m.matches() ) {
                // not sure why I can't set the headers after
                // the chain.doFilter call, but ...
                // I can't.
                // [A] Because you can't write the headers after
                // [A] content has already been written to the
                // [A] response!
                if (debug > 0) {
                    System.out.println("setting headers");
                }
                resp.setHeader(HEADER_CONTENT_ENCODING, HEADER_VAL_GZIP);
                resp.setHeader(HEADER_VARY, HEADER_ACCEPT_ENCODING);
            }
        }

        return true;
    }

    private void setRequestAttributes( HttpServletRequest req, boolean supportsCompression){
        if (supportsCompression) {
            req.setAttribute(ATTR_NAME_FILE_EXTENSION, gzipExtension);
        }
        req.setAttribute(ATTR_NAME_VERSION, jsVersion);
        String mode = req.getParameter("mode");
        if (!isProdMode){
            if (mode == null){
                mode = "mjsf";
            } else if (!mode.equals("mjsf")){
                mode = "sjsf";
            }
        }
        req.setAttribute("mode", mode);
        req.setAttribute("prodMode", isProdMode);
    }

    private void secureCookieIfNecessary(HttpServletRequest req) {

        HttpSession httpSession = req.getSession(false);
        if (httpSession != null) {
            return;
        }

        boolean secureCookie = req.getScheme().equals("https");

        /*
         * This is a https req, and we don't have a session yet.
         * This is probably the first req of the session, which means user
         * browsed to https://, which means we should stay in https for all
         * mail modes.  We need to secure the JSESSIONID cookie.
         *
         * We do this by SessionManager.setSecureCookies(true), jetty
         * will set secure on the session cookie.  This has to be done
         * here because it has to be done *before* the session is created.
         * After the session is created, jetty would create the return
         * the JSESSIONID cookie in the response for the request for that
         * the session is created; and it will be too late to call
         * setSecureCookies because jetty has already baked the session cookie.
         */
        ServletContext servletContext = config.getServletContext();
        if (servletContext instanceof org.eclipse.jetty.servlet.ServletContextHandler.Context) {
        	org.eclipse.jetty.servlet.ServletContextHandler.Context sContext =
        	    (org.eclipse.jetty.servlet.ServletContextHandler.Context)servletContext;

            // get the WebAppContext
            org.eclipse.jetty.server.handler.ContextHandler contextHandler = sContext.getContextHandler();
            if (contextHandler instanceof org.eclipse.jetty.servlet.ServletContextHandler) {
            	org.eclipse.jetty.servlet.ServletContextHandler context =
            	    (org.eclipse.jetty.servlet.ServletContextHandler)contextHandler;

                // get SessionManager
                org.eclipse.jetty.server.SessionManager sessionManager = context.getSessionHandler().getSessionManager();
                if (sessionManager instanceof org.eclipse.jetty.server.session.AbstractSessionManager) {
                    org.eclipse.jetty.server.session.AbstractSessionManager asm =
                        (org.eclipse.jetty.server.session.AbstractSessionManager)sessionManager;
                    asm.getSessionCookieConfig().setSecure(secureCookie);
                    asm.setHttpOnly(true);
                }
            }
        }
    }

    /**
     * Returns whether the browser supports gzipped content.
     * Checks a query parameter first, then the Accept-Encoding header.
     *
     * Note: The serverSupportsGzip configuration variable can override
     * this method.
     * @param req
     * @param resp
     * @return boolean.
     */
    public boolean supportsGzip(HttpServletRequest req, HttpServletResponse resp){
        // If this filter has been configured to not respond
        // with gzipped content, just return false.
        if (!serverSupportsGzip){
            return false;
        }
        boolean supportCompression = false;
        // Are we allowed to compress ?
        // Check for the query parameter first, to see if they are
        // trying to override what the browser is saying.
        String s = req.getParameter(HEADER_VAL_GZIP);
        if (debug > 0){
            System.out.println("Header " + HEADER_VAL_GZIP + " = " + s);
        }
        // We'll handle dev mode as an odd case, where only if
        // the user specifies that he wants gzip, will he get it.
        if (!isProdMode) {
            if (s != null ){
                if (TRUE.equals(s)) {
                    supportCompression = true;
                }
            }
            if (debug > 0) {
                System.out.print("in dev mode -- compression on? " + supportCompression + " ");
            }
            return supportCompression;
        }

        if (FALSE.equals(s)) {
            if (debug > 0) {
                System.out.print("got parameter gzip=false -->");
                System.out.println("don't compress, just chain filter");
            }
            supportCompression = false;
        } else {
            // we didn't find the query parameter, so check the accept
            // encoding headers
            @SuppressWarnings("unchecked")
            Enumeration<String> e = req.getHeaders(HEADER_ACCEPT_ENCODING);
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                if (name.indexOf(HEADER_VAL_GZIP) != -1) {
                    if (debug > 0) {
                        System.out.println("supports compression");
                    }
                    supportCompression = true;
                } else {
                    if (debug > 0) {
                        System.out.println("no support for compresion");
                    }
                }
            }
        }
        return supportCompression;
    }

    // ----------------------------------------------------------
    // Cache control methods
    // ----------------------------------------------------------

    public void setCacheControlHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();
        // is this a request for a jsp page?
        if (debug > 0){
            System.out.println("URI = " + uri + " ");
        }

        if ("true".equals(req.getParameter("weboffline"))) return;

        if (isDynamicContent(uri)){
            setJspCacheControlHeaders(resp);
        } else {
            setStaticResourceCacheControlHeaders(req, resp);
        }
    }

    private boolean isDynamicContent(String uri) {
        Matcher m = noCachePattern.matcher(uri);
        if (debug > 0) {
            System.out.println("Seeing if uri " + uri + " matches the noCache pattern " + noCachePattern.pattern());
            System.out.println("Does it? " + m.matches());
        }
        if (m.matches()) {
            return true;
        }
        return false;
    }

    private void setJspCacheControlHeaders(HttpServletResponse resp) {
        resp.setHeader(HEADER_EXPIRES, ALREADY_EXPIRED);
        resp.setHeader(HEADER_CACHE_CONTROL, NO_CACHE_CONTROL_VALUE);
        resp.setHeader(HEADER_PRAGMA, NO_CACHE_PRAGMA_VALUE);
		if (!allowInFrame) {
			resp.setHeader(HEADER_X_FRAME_OPTIONS, X_FRAME_OPTIONS_VALUE);
		}
    }

    private void setStaticResourceCacheControlHeaders(HttpServletRequest req, HttpServletResponse resp) {
        if (expiresValue > 0 && req.getMethod().equals("GET")) {
            TimeZone gmt = TimeZone.getTimeZone(GMT);
            long now = System.currentTimeMillis();
            Date expiresDate = new Date(now + (expiresValue * (long)1000));
            DateFormat df = new SimpleDateFormat(TIME_FORMAT);
            df.setTimeZone(gmt);
            resp.setHeader(HEADER_EXPIRES, df.format(expiresDate) + " " + GMT);
            resp.setHeader(HEADER_CACHE_CONTROL, futureCacheControl);
        }
    }
}
