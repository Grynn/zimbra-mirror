<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.CalDavBean" scope="request"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

<c:set var="uri" value="/zimbra/desktop/caldav.jsp"/>

${zdf:doRequest(bean)}

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<title><fmt:message key="ZimbraDesktop"/></title>
<style type="text/css">
    @import url(/zimbra/desktop/css/offline.css);
</style>
<script type="text/javascript" src="js/desktop.js"></script>
</head>

<body onload="InitScreen()">
<br><br><br><br><br><br>
<div align="center">
<zd:caldavNew uri="${uri}"/>
</div>
</body>
</html>
