package com.zimbra.cs.offline.jsp;

import javax.servlet.http.HttpServlet;

public class ConfigServlet extends HttpServlet {

	private static final long serialVersionUID = 8124246834674440988L;

    private static final String LOCALHOST_URL_PREFIX = "http://localhost:";
    
    public static String LOCALHOST_SOAP_URL;
    public static String LOCALHOST_ADMIN_URL;
    
	@Override
	public void init() {
		int port = Integer.parseInt(getServletConfig().getInitParameter("port"));
		int adminPort = Integer.parseInt(getServletConfig().getInitParameter("adminPort"));
		
		//setting static variables
		LOCALHOST_SOAP_URL = LOCALHOST_URL_PREFIX + port + "/service/soap/";
		LOCALHOST_ADMIN_URL = LOCALHOST_URL_PREFIX + adminPort + "/service/admin/soap/";
    }
}
