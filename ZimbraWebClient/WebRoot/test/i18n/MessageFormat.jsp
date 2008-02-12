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