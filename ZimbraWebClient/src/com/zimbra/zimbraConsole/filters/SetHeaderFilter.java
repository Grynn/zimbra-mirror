
package com.zimbra.zimbraConsole.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class SetHeaderFilter implements Filter {
    
    // --------------------------------------------------------------
    // Class constants
    // --------------------------------------------------------------
    private static final String ATTR_JS_VERSION = "jsVersion";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String HEADER_VAL_GZIP = "gzip";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ATTR_NAME_VERSION = "version";
    private static final String ATTR_NAME_FILE_EXTENSION = "fileExtension";
    private static final String ATTR_NAME_PROD_MODE = "isProdMode";
    private static final String HEADER_CACHE_CONTROL = "Cache-control";
    private static final String NO_CACHE_CONTROL_VALUE = 
        	"no-cache, must-revalidate, max-age=0";
    private static final String HEADER_EXPIRES = "Expires";
    private static final String ALREADY_EXPIRED = 
        	"Tue, 24 Jan 2000 20:46:50 GMT";
    private static final String HEADER_CONTENT_ENCODING= "Content-encoding";
    private static final String JSP_EXTENSION = ".jsp"; 
    private static final String TIME_FORMAT = "EEE, d MMM yyyy HH:mm:ss";
    private static final String GMT = "GMT";
    
    protected int debug = 0;
    protected String jsVersion = null;
    protected FilterConfig config = null;
    protected Pattern extensionPattern = null;
    protected boolean serverSupportsGzip = true;
    protected String gzipExtension = null;
    protected int expiresValue = 0;
    protected boolean isProdMode = true;
    protected String futureCacheControl = null;
    
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
            ret = (new Boolean(value)).booleanValue();
        }
        return ret;
    }

    private void getExtensionsRegex () {
        String value = getInitParameter("extensions", ".js, .html");
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

    // --------------------------------------------------------------
    // init methods
    // --------------------------------------------------------------
    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {
        this.config = filterConfig;
        String value = null;
        if (filterConfig != null) {
            debug = getInitParameterInt("debug", 0);
            //int jv = getInitParameterInt("jsVersion", 0);
            jsVersion = getInitParameter("jsVersion", "0");
            serverSupportsGzip = getInitParameterBool("shouldSupportGzip",
                                                      true);
            getExtensionsRegex();
            gzipExtension = getInitParameter("GzipExtension", ".jgz");
            expiresValue = getInitParameterInt("Expires", 0);
            futureCacheControl = "max-age:" + expiresValue + ", must-revalidate";
            isProdMode = getInitParameterBool("ProdMode", true);
            if (debug > 0) {
                System.out.println("Filter variables:");
                System.out.println("  debug = " + debug);
                System.out.println("  jsVersion = " + jsVersion);
                System.out.println("  serverSupportsGzip = " + 
                                   serverSupportsGzip);
                System.out.println("  gzipExtension = " + gzipExtension);
                System.out.println("  expiresValue = " + expiresValue);
                System.out.println("  isProdMoe = " + isProdMode);
                System.out.println("  extensionRegex = " + 
                                   extensionPattern.pattern());
            }
        }
    }

    /**
    * Take this filter out of service.
    */
    public void destroy() {
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
    public void doFilter ( ServletRequest request, ServletResponse response,
                           FilterChain chain ) throws IOException,
                                                      ServletException {
        
        if (debug > 0) {
            System.out.println("@doFilter");
        }
        // make sure we're dealing with an http request
        HttpServletRequest req = null;
        HttpServletResponse resp = null;
        boolean proceed = shouldProcess(request, response);
        if (proceed){
            req = (HttpServletRequest)request;
            resp = (HttpServletResponse)response;
        } else {
            if (debug > 0){
                System.out.println("not proceeding with filter");
            }
            chain.doFilter(request, response);
            return;
        }
        
        String uri = req.getRequestURI();        

        // before we check whether we can compress, let's check
        // what sort of cache control headers we should use for this
        // request.
        setCacheControlHeaders(req, resp);
        
        boolean supportCompression = false;
        supportCompression = this.supportsGzip(req, resp);
        setRequestAttributes(req, resp, supportCompression);
        if (!supportCompression) {
            if (debug > 0) {
                System.out.println("doFilter gets called wo compression");
            }
            chain.doFilter(req, resp);
            return;
        } else {
            boolean setGzipHeaders = false;
            Matcher m = extensionPattern.matcher(uri);
            if (debug > 0) {
                System.out.println("Seeing if uri " + uri +
                                   " matches the extension regex of " 
                                   + extensionPattern.pattern());
                System.out.println("Does it? " + m.matches());
            }
            if ( m.matches() ) {
                // not sure why I can't set the headers after 
                // the chain.doFilter call, but ...
                // I can't.
                if (debug > 0) {
                    System.out.println("setting headers");                    
                }
                resp.setHeader(HEADER_CONTENT_ENCODING, HEADER_VAL_GZIP);
            }
            chain.doFilter(req, resp);
            return;
        }
    }

    private void setRequestAttributes( HttpServletRequest req, HttpServletResponse resp,
            boolean supportsCompression){
        if (supportsCompression) {
            req.setAttribute(ATTR_NAME_FILE_EXTENSION, gzipExtension);
        }
        req.setAttribute(ATTR_NAME_VERSION, jsVersion);
        String mode = (String) req.getParameter("mode");
        if (!isProdMode){
            if (mode == null){
                mode = "mjsf";
            } else if (!mode.equals("mjsf")){
                mode = "sjsf";
            }
        }
        req.setAttribute("mode", mode);
        
        String loRes = (String) req.getParameter("loRes");
        if (loRes != null)
        	req.setAttribute("loRes", loRes);
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
    public boolean supportsGzip (HttpServletRequest req, 
                                 HttpServletResponse resp){
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
            Enumeration e = req.getHeaders(HEADER_ACCEPT_ENCODING);
            while (e.hasMoreElements()) {
                String name = (String)e.nextElement();
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
    
    public void setCacheControlHeaders (HttpServletRequest req,
                                        HttpServletResponse resp){
        String uri = (String) req.getRequestURI();
        // is this a request for a jsp page?
        if (debug > 0){
            System.out.println("URI = " + uri + " ");
        }
        // TODO: Change hardcoded webapp name
        if ( isDynamicContent(uri)){
            setJspCacheControlHeaders(req, resp);
        } else {
            setStaticResourceCacheControlHeaders(req, resp);
        }
    }
    
    private static final String URI_LIQUID = "/liquid/";
    private static final String URI_MAIL = "/liquid/mail";
    private static final String URI_AUTH = "/liquid/auth/";
    
    private boolean isDynamicContent (String uri) 
    {
        return ( (uri != null) && 
		 ( uri.equals(URI_LIQUID) ||
		   (uri.indexOf(URI_MAIL) != -1) ||
		   uri.equals(URI_AUTH) ||
		   (uri.indexOf(JSP_EXTENSION)) != -1));
	
    }
    
    private void setJspCacheControlHeaders(HttpServletRequest req, 
                                           HttpServletResponse resp){
        resp.setHeader(HEADER_CACHE_CONTROL, NO_CACHE_CONTROL_VALUE);
        resp.setHeader(HEADER_EXPIRES, ALREADY_EXPIRED);
    }
    
    
    private void setStaticResourceCacheControlHeaders(
                                                HttpServletRequest req, 
                                                HttpServletResponse resp){
        if (expiresValue > 0) {
            TimeZone gmt = TimeZone.getTimeZone(GMT);
            long now = System.currentTimeMillis();
            Date expiresDate = new Date(now + (expiresValue * 1000));
            DateFormat df = new SimpleDateFormat(TIME_FORMAT); 
            df.setTimeZone(gmt);
            resp.setHeader(HEADER_EXPIRES, df.format(expiresDate) + " " + GMT);
            resp.setHeader(HEADER_CACHE_CONTROL, futureCacheControl);
        }
    }
}

