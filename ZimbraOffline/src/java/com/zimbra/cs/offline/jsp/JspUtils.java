package com.zimbra.cs.offline.jsp;

import javax.servlet.ServletRequest;

public class JspUtils {
    
	public static String getRequestParameter(ServletRequest request, String param) {
		String val = request.getParameter(param);
		if (val == null)
			throw new RuntimeException("Parameter " + param + " is required");
		return val.trim();
	}
	
	
	public static String getRequestParameter(ServletRequest request, String param, String defaultVal) {
		String val = request.getParameter(param);
		return val == null ? defaultVal : val.trim();
	}
	
	public static boolean getRequestParameterAsBoolean(ServletRequest request, String param) {
		String val = request.getParameter(param);
		return val == null ? false : true;
	}
}
