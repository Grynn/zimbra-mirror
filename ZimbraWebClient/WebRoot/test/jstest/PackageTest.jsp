<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2006, 2007 Zimbra, Inc.

The contents of this file are subject to the Zimbra Public License
Version 1.2 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<%
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")) {
			contextPath = "";
		}
	%>
    <title>Package Test</title>
    <jsp:include page="../../public/Messages.jsp"/>
    <jsp:include page="../../public/Boot.jsp"/>
    <jsp:include page="../../public/Ajax.jsp"/>
    <script>
    	appContextPath = "<%=contextPath %>";
    	scriptLoadStart = (new Date()).getTime();
    </script>
	<script src="LoadTest.js">
  </head>
    <body>
<p>This test jsp measures how long it takes to load a sample js file. The sample file
LoadTest.js is a copy of ZimbraMail_all.js, which is 3.1M and 94K lines. A reload
is needed between tests since the package system recognizes when a resource has
already been loaded.</p>
<p>Load time via simple script tag: <span id="sltSpan"></span></p>
    <script>   	
    	scriptLoadTime = (new Date()).getTime() - scriptLoadStart;
		AjxPackage.setBasePath("<%=contextPath %>");
		function loadTestA() {
			var s = (new Date()).getTime();
			AjxPackage.require("test.jstest.LoadTest");
			var e = (new Date()).getTime();
			var t = e - s;
			var el = document.getElementById("resultsDivA");
			el.innerHTML = "That took " + t + "ms";
		}
		function loadTestB() {
			loadStart = (new Date()).getTime();
			AjxInclude(["/zimbra/test/jstest/LoadTest.js"], null, new AjxCallback(null, loadTestB_done));
		}
		function loadTestB_done() {
			var e = (new Date()).getTime();
			var t = e - loadStart;
			var el = document.getElementById("resultsDivB");
			el.innerHTML = "That took " + t + "ms";
		}
		document.getElementById("sltSpan").innerHTML = scriptLoadTime;
    </script>
    <button onclick="loadTestA();">AjxPackage</button>
    <div id="resultsDivA"></div>
    <button onclick="loadTestB();">AjxInclude</button>
    <div id="resultsDivB"></div>
    </body>
</html>
