package com.zimbra.webClient.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;

public class SetCookieServlet extends ZCServlet
{
    
    private static final String PARAM_AUTH_TOKEN = "authToken";
    private static final String PARAM_PUBLIC_COMPUTER = "publicComputer";
    private static final String PARAM_QUERY_STRING_TO_CARRY = "qs";
    private static final String PARAM_AUTH_TOKEN_LIFETIME = "atl";
    private static final String DEFAULT_MAIL_URL = "/zimbra/mail";
    
    private static final String HEADER_HOST = "host";
    private static final String HEADER_REFERER = "referer";

    private static String redirectLocation;
    
    public void init(ServletConfig servletConfig) {
        redirectLocation = servletConfig.getInitParameter("mailUrl");
        if (redirectLocation == null) {
            redirectLocation = DEFAULT_MAIL_URL;
        }
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException
    {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    
    public void doPost (HttpServletRequest req, HttpServletResponse resp) {
        
        try {
            String authToken = getReqParameter(req, PARAM_AUTH_TOKEN);
            if (authToken == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            } else {
                String atl = getReqParameter(req, PARAM_AUTH_TOKEN_LIFETIME);
                String publicComputer = getReqParameter(req,
                                                        PARAM_PUBLIC_COMPUTER);
                boolean isPublic = false;
                if (publicComputer != null) {
                    isPublic = new Boolean(publicComputer).booleanValue();
                }

                int lifetime = -1;
                if (!isPublic){
                    try {
                        int lifetimeMs = Integer.parseInt(atl);
                        lifetime = lifetimeMs / 1000;
                    } catch (NumberFormatException ne){
                        lifetime = -1;
                    }
                }

                Cookie c = new Cookie("LS_AUTH_TOKEN", authToken);
                c.setPath("/");
                c.setMaxAge(lifetime);                
                resp.addCookie(c);
            }
            
            String host = req.getHeader(HEADER_HOST);
            String referer = req.getHeader(HEADER_REFERER);
            //System.out.println("Host == " + host + " referer = " + referer);
            boolean abs = true;
            if (!shouldRedirectUrl(req) && referer != null && (referer.matches("[^/]*//" + host + "/.*")) ) {
                abs = false;
            } else {
                abs = true;
            }
            String redirectTo = getRedirectUrl(req, redirectLocation, null, 
                                               abs, true);
            //System.out.println("RedirectTo = " + redirectTo);
            
            resp.sendRedirect(redirectTo);
        } catch (IOException ie) {
	    // do nothing
        } catch (IllegalStateException is){
	    // do nothing
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ex.printStackTrace ();
        }
    }    
	
}
