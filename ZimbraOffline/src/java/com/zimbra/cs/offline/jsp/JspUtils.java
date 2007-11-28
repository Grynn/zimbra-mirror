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
	
	
	public static String formatSyncInterval(String interval_number, String interval_unit) {
        try {
            int number = Integer.parseInt(interval_number);
            if (interval_unit.equals("seconds")) {
                number = number < 60 ? 60 : number;
                return Integer.toString(number) + 's';
            } else {
                number = number < 1 ? 1 : number;
                return Integer.toString(number) + 'm';
            }
        } catch (Exception x) {
            throw new RuntimeException("Sync interval must be a valid number");
        }
    }
}
