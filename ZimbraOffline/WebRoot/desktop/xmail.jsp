<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.XmailBean" scope="request"/>
<jsp:setProperty name="bean" property="*"/>

<c:set var="uri" value="/zimbra/desktop/xmail.jsp"/>

${zdf:doRequest(bean)}

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop ${bean.appVersion}</title>
<style type="text/css">
    @import url(offline.css);
</style>
<script type="text/javascript" src="js/Ajax.js"></script>
</head>

<body onload="InitScreen()">

<c:choose>
    <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
        <zd:xmailNew uri="${uri}"/>
    </c:when>
    
    <c:when test="${bean.modify && not bean.allOK}">
        <zd:xmailManage uri="${uri}"/>
    </c:when>
    
    <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
        ${zdf:reload(bean)}
        <zd:xmailManage uri="${uri}"/>
    </c:when>
    
    <c:when test="${not bean.noVerb && bean.allOK}">
        <zd:xmailDone uri="${uri}" name="${bean.dataSourceName}"/>
    </c:when>
    
    <c:otherwise>
        <p class='ZOfflineError'>Unexpected error!</p>
    </c:otherwise>
</c:choose>

</body>
</html>
