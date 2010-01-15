<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2005, 2006, 2007, 2010 Zimbra, Inc.

The contents of this file are subject to the Zimbra Public License
Version 1.3 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>Memory Leak tests</title>

		<jsp:include page="../../public/Messages.jsp"/>
        <jsp:include page="../../public/Boot.jsp"/>
		<jsp:include page="../../public/Ajax.jsp"/>
		<jsp:include page="../../public/jsp/Zimbra.jsp"/>
		<jsp:include page="../../public/jsp/ZimbraCore.jsp"/>

		<script type="text/javascript" src="MemLeakTests.js"></script>

		<script language="JavaScript">   	
			function launch() {
				DBG = new AjxDebug(AjxDebug.NONE);
				MemLeakTests.run(document.domain);
			};
			AjxCore.addOnloadListener(launch);
		</script>

	</head>

	<body>
	</body>

</html>
