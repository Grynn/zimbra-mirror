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

<%@ page language="java" import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>

		<title>Callback Test</title>

		<script src="/zimbra/js/ajax/boot/AjxCallback.js"></script>
		<script src="/zimbra/js/ajax/util/AjxBuffer.js"></script>

		<script>
			function myFunc() {
				var x = "hello";
			}
			
			var buffer = new AjxBuffer("hello", "there");
			var num = 100000;
			function testAjxCallback() {
				var s = (new Date()).getTime();
				for (var i = 0; i < num; i++) {
					var callback = new AjxCallback(buffer, buffer.join);
					callback.run(" ");
				}
				var e = (new Date()).getTime();
				var t = e - s;
				var el = document.getElementById("resultsDivA");
				el.innerHTML = num + " iterations took " + t + "ms";
			}
			
			function testClosure() {
				var s = (new Date()).getTime();
				for (var i = 0; i < num; i++) {
					var callback = AjxCallback.simpleClosure(buffer.join, buffer);
					callback(" ");
				}
				var e = (new Date()).getTime();
				var t = e - s;
				var el = document.getElementById("resultsDivB");
				el.innerHTML = num + " iterations took " + t + "ms";
			}
		</script>

	</head>

	<body>
    <button onclick="testAjxCallback();">AjxCallback</button>
    <div id="resultsDivA"></div>
    <button onclick="testClosure();">closure</button>
    <div id="resultsDivB"></div>
	</body>

</html>
