<%@ page import="java.util.*" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<jsp:useBean id="now" class="java.util.Date" />
<%
	TimeZone est = TimeZone.getTimeZone("US/Eastern");
	pageContext.setAttribute("est", est);
	TimeZone pst = TimeZone.getTimeZone("US/Pacific");
	pageContext.setAttribute("pst", pst);
%>

<h3>fmt:setTimeZone</h3>
<h4>As String: "US/Eastern"</h4>
<fmt:setTimeZone value='US/Eastern' />
<h4>As TimeZone: ${est}</h4>
<fmt:setTimeZone value="${est}" />

<h3>fmt:timeZone</h3>
<h4>As String: "US/Pacific"</h4>
<fmt:timeZone value='US/Pacific'></fmt:timeZone>
<h4>As TimeZone: ${pst}</h4>
<fmt:timeZone value="${pst}"></fmt:timeZone>

<h3>Format Time</h3>
<h4>US/Eastern</h4>
<fmt:timeZone value="${est}">
<li>fmt:timeZone: <fmt:formatDate value="${now}" type="time" /></li>
</fmt:timeZone>
<li>String attr: <fmt:formatDate value="${now}" type="time" timeZone="US/Eastern" /></li>
<li>TimeZone attr: <fmt:formatDate value="${now}" type="time" timeZone="${est}" /></li>

<h4>US/Pacific</h4>
<fmt:timeZone value="${pst}">
<li>fmt:timeZone: <fmt:formatDate value="${now}" type="time" /></li>
</fmt:timeZone>
<li>String attr: <fmt:formatDate value="${now}" type="time" timeZone="US/Pacific" /></li>
<li>TimeZone attr: <fmt:formatDate value="${now}" type="time" timeZone="${pst}" /></li>
