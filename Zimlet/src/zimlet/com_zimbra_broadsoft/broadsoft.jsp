<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 * @Author Raja Rao DV
 *
--%>
 
<%@ page language="java" import="org.apache.commons.httpclient.Credentials"%>
<%@ page language="java" import="org.apache.commons.httpclient.HostConfiguration"%>
<%@ page language="java" import="org.apache.commons.httpclient.HttpClient"%>
<%@ page language="java" import="org.apache.commons.httpclient.UsernamePasswordCredentials"%>
<%@ page language="java" import="org.apache.commons.httpclient.auth.AuthScope"%>
<%@ page language="java" import="org.apache.commons.httpclient.methods.PutMethod"%>
<%@ page language="java" import="org.apache.commons.httpclient.methods.StringRequestEntity"%>

<%
	String server = request.getParameter("server");
	String turnon = request.getParameter("turnon");
	String email = request.getParameter("email");
	String password =  request.getParameter("password");
	String action =  request.getParameter("action");
	String resp = "";
	if(action.contains("doNotDisturb")) {
		resp = doNotDisturb(server, turnon, email, password);
	} else 	if(action.contains("callAnyWhere")) {
		resp = callAnyWhere(server, turnon, email, password);
	}
		/*String result = "{"
			  + "\"server\": \""+server+"\","
			  + "\"turnon\": \""+turnon+"\","
			  + "\"email\": \""+email+"\","
			  + "\"password\": \""+password+"\""
			  + "\"response\": \""+resp+"\""
			  + "}";
			*/  
		
%>

<%=  resp %>

<%!public String doNotDisturb(String server, String turnOn, String userName, String password) {
		String url = "https://"+server+"/com.broadsoft.xsi-actions/v1.0/user/"+userName+"/services/DoNotDisturb";
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DoNotDisturb xmlns=\"http://schema.broadsoft.com/xsi-actions\">";
		body = body + "<isActive>"+turnOn+"</isActive><ringSplash>false</ringSplash></DoNotDisturb>";

		try {
			HttpClient httpClient = new HttpClient();
			PutMethod putMethod = new PutMethod(url);
			if (body != null) {
				putMethod.setRequestEntity(new StringRequestEntity(body,
						"application/xml", "UTF-8"));
			}
			Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
			httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
			int code = httpClient.executeMethod(putMethod);
			return ""+code;
			// Response response = new Response(code,
			// putMethod.getResponseBody());
			//out.println("code:" + code);
		} catch (Exception e) {
			return e.toString();
		}

}

%>


<%!public String callAnyWhere(String server, String turnOn, String userName, String password) {
		String url = "https://"+server+"/com.broadsoft.xsi-actions/v1.0/user/"+userName+"/services/broadworksanywhere";
		
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><BroadWorksAnywhere xmlns=\"http://schema.broadsoft.com/xsi-actions\"><alertAllLocationsForClickToDialCalls>true</alertAllLocationsForClickToDialCalls><locations><location><phoneNumber>4083498112</phoneNumber><isActive>true</isActive></location></locations></BroadWorksAnywhere>";
		try {
			HttpClient httpClient = new HttpClient();
			PutMethod putMethod = new PutMethod(url);
			if (body != null) {
				putMethod.setRequestEntity(new StringRequestEntity(body,
						"application/xml", "UTF-8"));
			}
			Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
			httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
			int code = httpClient.executeMethod(putMethod);

			return ""+code;
			// Response response = new Response(code,
			// putMethod.getResponseBody());
			//out.println("code:" + code);
		} catch (Exception e) {
			return e.toString();
		}

}

%>