<%@ page import="java.io.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
</style>

<jsp:useBean id="now" class='java.util.Date' />
<h2>${now}</h2>

<table border=1 cellspacing="0" cellpadding="3">
	<tr><th>default</th>
		<td><fmt:formatDate value="${now}" /></td>
		<th>default date</th>
		<td><fmt:formatDate value="${now}" type='date' /></td>
		<th>default time</th>
		<td><fmt:formatDate value="${now}" type='time' /></td>
	</tr>
</table>

<%!
	static final String BASENAME = "/messages/test/i18n/formatDate";
	static final byte[] MESSAGES = (
		"date.short = {0,date,short}\n"+
		"date.medium = {0,date,medium}\n"+
		"date.long = {0,date,long}\n"+
		"date.full = {0,date,full}\n"+
		"time.short = {0,time,short}\n"+
		"time.medium = {0,time,medium}\n"+
		"time.long = {0,time,long}\n"+
		"time.full = {0,time,full}\n"
	).getBytes();
%>

<%
	File file = new File(getServletContext().getRealPath("/WEB-INF/classes"+BASENAME+".properties"));
	if (!file.exists()) {
		File dir = file.getParentFile();
		dir.mkdirs();
		OutputStream fout = new FileOutputStream(file);
		fout.write(MESSAGES);
		fout.close();
	}
%>

<fmt:setBundle basename="<%=BASENAME%>" />

<h3>Timezone: default, Locale: request</h3>
<table border=1 cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Length</th><th colspan="2">Date</th><th colspan="2">Time</th></tr>
	<tr><th>formatDate</th><th>{0,date,<u>length</u>}</th><th>formatDate</th><th>{0,time,<u>length</u>}</th></tr>
	<tr><th>short</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="short" /></td>
		<td><fmt:message key="date.short"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="short" /></td>
		<td><fmt:message key="time.short"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>medium</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="medium" /></td>
		<td><fmt:message key="date.medium"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="medium" /></td>
		<td><fmt:message key="time.medium"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>long</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="long" /></td>
		<td><fmt:message key="date.long"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="long" /></td>
		<td><fmt:message key="time.long"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>full</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="full" /></td>
		<td><fmt:message key="date.full"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="full" /></td>
		<td><fmt:message key="time.full"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
</table>

<h3>Timezone: "EST" (fmt:setTimezone), Locale: request</h3>
<fmt:setTimeZone value="EST" />
<table border=1 cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Length</th><th colspan="2">Date</th><th colspan="2">Time</th></tr>
	<tr><th>formatDate</th><th>{0,date,<u>length</u>}</th><th>formatDate</th><th>{0,time,<u>length</u>}</th></tr>
	<tr><th>short</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="short" /></td>
		<td><fmt:message key="date.short"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="short" /></td>
		<td><fmt:message key="time.short"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>medium</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="medium" /></td>
		<td><fmt:message key="date.medium"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="medium" /></td>
		<td><fmt:message key="time.medium"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>long</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="long" /></td>
		<td><fmt:message key="date.long"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="long" /></td>
		<td><fmt:message key="time.long"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>full</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="full" /></td>
		<td><fmt:message key="date.full"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="full" /></td>
		<td><fmt:message key="time.full"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
</table>

<h3>Timezone: "EST" (fmt:setTimezone), Locale: "ja"</h3>
<fmt:setTimeZone value="EST" />
<fmt:setLocale value="ja" scope="request" />
<table border=1 cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Length</th><th colspan="2">Date</th><th colspan="2">Time</th></tr>
	<tr><th>formatDate</th><th>{0,date,<u>length</u>}</th><th>formatDate</th><th>{0,time,<u>length</u>}</th></tr>
	<tr><th>short</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="short" /></td>
		<td><fmt:message key="date.short"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="short" /></td>
		<td><fmt:message key="time.short"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>medium</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="medium" /></td>
		<td><fmt:message key="date.medium"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="medium" /></td>
		<td><fmt:message key="time.medium"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>long</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="long" /></td>
		<td><fmt:message key="date.long"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="long" /></td>
		<td><fmt:message key="time.long"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>full</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="full" /></td>
		<td><fmt:message key="date.full"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="full" /></td>
		<td><fmt:message key="time.full"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
</table>

<h3>Timezone: "CST" (fmt:timezone), Locale: "ja"</h3>
<fmt:timeZone value="CST">
<fmt:setLocale value="ja" />
<table border=1 cellspacing="0" cellpadding="3">
	<tr><th rowspan="2">Length</th><th colspan="2">Date</th><th colspan="2">Time</th></tr>
	<tr><th>formatDate</th><th>{0,date,<u>length</u>}</th><th>formatDate</th><th>{0,time,<u>length</u>}</th></tr>
	<tr><th>short</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="short" /></td>
		<td><fmt:message key="date.short"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="short" /></td>
		<td><fmt:message key="time.short"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>medium</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="medium" /></td>
		<td><fmt:message key="date.medium"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="medium" /></td>
		<td><fmt:message key="time.medium"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>long</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="long" /></td>
		<td><fmt:message key="date.long"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="long" /></td>
		<td><fmt:message key="time.long"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
	<tr><th>full</th>
		<td><fmt:formatDate value="${now}" type="date" dateStyle="full" /></td>
		<td><fmt:message key="date.full"><fmt:param value="${now}" /></fmt:message></td>
		<td><fmt:formatDate value="${now}" type="time" timeStyle="full" /></td>
		<td><fmt:message key="time.full"><fmt:param value="${now}" /></fmt:message></td>
	</tr>
</table>
</fmt:timeZone>