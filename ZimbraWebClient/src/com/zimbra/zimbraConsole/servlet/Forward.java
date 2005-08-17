package com.liquidsys.liquidConsole.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Forward extends LCServlet
{
    
    public static final String DEFAULT_FORWARD_URL = 
	"/public/launchLiquidMail.jsp";
    private static final String PARAM_FORWARD_URL = "fu";
    
    public void doGet (HttpServletRequest req,
		       HttpServletResponse resp) {

	try {
            if (shouldRedirectUrl(req)){
                String redirectTo = getRedirectUrl(req);
                resp.sendRedirect(redirectTo);
                return;
            }
            
	    String url = getReqParameter(req, PARAM_FORWARD_URL,
                                         DEFAULT_FORWARD_URL);
	    String qs = req.getQueryString();
	    if (qs != null && !qs.equals("")){
		url = url + "?" + qs;
	    }
	    ServletContext sc = getServletConfig().getServletContext();
	    sc.getRequestDispatcher(url).forward(req, resp);
	} catch (Exception ex) {
	    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    ex.printStackTrace ();
	}
    }    
}
