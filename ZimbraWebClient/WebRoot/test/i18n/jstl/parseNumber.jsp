<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
	TD { text-align: right; }
</style>

<c:set var="number" value="${1234.5678}" scope="request" />

<h2>${number}</h2>
<li>parse to output: <fmt:parseNumber value='${number}' pattern="###0.####" /></li>

<c:set var="locale" value="${pageContext.request.locale}" scope="request" />
<jsp:include page="parseNumber_table.jsp" />

<% pageContext.setAttribute("locale", new Locale("ja"), PageContext.REQUEST_SCOPE); %>
<jsp:include page="parseNumber_table.jsp" />
