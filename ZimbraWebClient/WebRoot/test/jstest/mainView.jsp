<!-- 
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.2

The contents of this file are subject to the Zimbra Public License
Version 1.2 ("License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
the License for the specific language governing rights and limitations
under the License.

The Original Code is: Zimbra Collaboration Suite Web Client

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
All Rights Reserved.

Contributor(s):

***** END LICENSE BLOCK *****
-->

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
