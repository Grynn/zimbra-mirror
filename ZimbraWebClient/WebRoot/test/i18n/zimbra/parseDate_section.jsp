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
<h3>TimeZone: "${param.timeZone}", Locale: "${param.locale}"</h3>
<h4>Date</h4>
<jsp:include page="parseDate_table.jsp">
	<jsp:param name="timeZone" value="${param.timeZone}" />
	<jsp:param name="locale" value="${param.locale}" />
	<jsp:param name="type" value="date" />
</jsp:include>
<h4>Time</h4>
<jsp:include page="parseDate_table.jsp">
	<jsp:param name="timeZone" value="${param.timeZone}" />
	<jsp:param name="locale" value="${param.locale}" />
	<jsp:param name="type" value="time" />
</jsp:include>
