<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="java.text.*" %>
<% MessageFormat formatter = new MessageFormat("You last logged in on {3,date,short} at {3,time,short}, {0}."); %>
<h3>Formats By Argument Index</h3>
<% {
	Format[] formats = formatter.getFormatsByArgumentIndex();
	for (int i = 0; i < formats.length; i++) { %>
		<li>formats[<%=i%>] = <%=formats[i]%></li>
<%	}
} %><h3>Formats</h3>
<% {
	Format[] formats = formatter.getFormats();  
	for (int i = 0; i < formats.length; i++) { %>
		<li>formats[<%=i%>] = <%=formats[i]%></li>
<%	}
} %>