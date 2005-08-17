<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="inline" value="outline" scope="session"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Zimbra Browser</title>
    <script type="text/javascript" src="/ZimbraConsole/js/debug/Debug.js"></script>
    <jsp:include page="util.jsp"/>
    <jsp:include page="DWT.jsp"/>
  </head>
  <body>
    <script language="JavaScript">
      <jsp:include page="main.js"/>
    </script>
  </body>
</html>
