<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Zimlets
Copyright (C) 2006, 2007 Zimbra, Inc.

The contents of this file are subject to the Yahoo! Public License
Version 1.0 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" import="java.io.*, java.util.*, javax.naming.*"%>
<%
	String name = (String) request.getParameter("name");
	String path = (String) request.getParameter("path");
	String subject = (String) request.getParameter("subject");
	String id = (String) request.getParameter("id");
	PrintWriter pw = response.getWriter();
    if (name == null) 
	    pw.println("id=" + id + "; subject=" + subject);
	else 
		pw.println("name=" + name + "; path=" + path); 
%>
