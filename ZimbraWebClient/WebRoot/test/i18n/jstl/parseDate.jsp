<%@ page import="java.util.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
</style>

<jsp:useBean id="now" class='java.util.Date' scope="request" />

<h2>${requestScope.now}</h2>

<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="PST" />
	<jsp:param name="locale" value="${pageContext.request.locale}" />
</jsp:include>

<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="EST" />
	<jsp:param name="locale" value="${pageContext.request.locale}" />
</jsp:include>

<fmt:setLocale value="ja" />
<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="EST" />
	<jsp:param name="locale" value="ja" />
</jsp:include>
