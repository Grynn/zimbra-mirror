<%@page import='java.text.DateFormat,java.util.Date' %>
Dynamic content generated at
<%= DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, request.getLocale()).format(new Date()) %>.