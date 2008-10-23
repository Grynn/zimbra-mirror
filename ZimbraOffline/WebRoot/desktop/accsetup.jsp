<%@ page import="com.zimbra.cs.offline.jsp.ZmailBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<c:set var="accntType" value="${param.accntType}"/>

<c:if test="${accntType eq null}">
    <c:set var="accntType" value="ZimbraAcct"/>   
</c:if>


<c:if test="${accntType eq 'ZimbraAcct'}">
    <jsp:useBean id="zbean" class="com.zimbra.cs.offline.jsp.ZmailBean" scope="request"/>
    <jsp:setProperty name="zbean" property="*"/>
    <jsp:setProperty name="zbean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(zbean)}
    <c:set var="bean" value="${zbean}" scope="request"/>
</c:if>
<c:if test="${accntType eq 'YMPAcct'}">
    <jsp:useBean id="ybean" class="com.zimbra.cs.offline.jsp.YmailBean" scope="request"/>
    <jsp:setProperty name="ybean" property="*"/>
    <jsp:setProperty name="ybean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(ybean)}
    <c:set var="bean" value="${ybean}" scope="request"/>    
</c:if>
<c:if test="${accntType eq 'GmailAcct'}">
    <jsp:useBean id="gbean" class="com.zimbra.cs.offline.jsp.GmailBean" scope="request"/>
    <jsp:setProperty name="gbean" property="*"/>
    <jsp:setProperty name="gbean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(gbean)}
    <c:set var="bean" value="${gbean}" scope="request"/>
</c:if>
<c:if test="${accntType eq 'LiveAcct'}">
    <jsp:useBean id="lbean" class="com.zimbra.cs.offline.jsp.LmailBean" scope="request"/>
    <jsp:setProperty name="lbean" property="*"/>
    <jsp:setProperty name="lbean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(lbean)}
    <c:set var="bean" value="${lbean}" scope="request"/>
</c:if>
<c:if test="${accntType eq 'AOLAcct'}">
    <jsp:useBean id="abean" class="com.zimbra.cs.offline.jsp.AmailBean" scope="request"/>
    <jsp:setProperty name="abean" property="*"/>
    <jsp:setProperty name="abean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(abean)}
    <c:set var="bean" value="${abean}" scope="request"/>
</c:if>
<c:if test="${accntType eq 'MSEAcct'}">
    <jsp:useBean id="mbean" class="com.zimbra.cs.offline.jsp.MmailBean" scope="request"/>
    <jsp:setProperty name="mbean" property="*"/>
    <jsp:setProperty name="mbean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(mbean)}
    <c:set var="bean" value="${mbean}" scope="request"/>
</c:if>
<c:if test="${accntType eq 'OtherAcct'}">
    <jsp:useBean id="xbean" class="com.zimbra.cs.offline.jsp.XmailBean" scope="request"/>
    <jsp:setProperty name="xbean" property="*"/>
    <jsp:setProperty name="xbean" property="locale" value="${pageContext.request.locale}"/>
    ${zdf:doRequest(xbean)}
    <c:set var="bean" value="${xbean}" scope="request"/>
</c:if>
<c:set var="uri" value="/zimbra/desktop/accsetup.jsp"/>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/desktop/css/offline.css" type="text/css">

<title><fmt:message key="ZimbraDesktop"/></title>

<script type="text/javascript" src="js/desktop.js"></script>
</head>

<script type="text/javascript">
    function accntChange(accnt) {
       document.newAccnt.submit();
    }

    function OnCancel() {
    window.location = '/zimbra/desktop/accsetup.jsp';
    }

</script>
<body onload="InitScreen();">
<br><br>
<div align="center">
<img src="/zimbra/desktop/img/YahooZimbraLogo.gif" border="0">
<br><br>
<div class="whiteBg">
<div id="accountType">

<c:choose>
    <c:when test="${not empty bean.error}" >
        <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
        <p class='ZOfflineError'><fmt:message key='PlsCorrectInput'/></p>
    </c:when>
</c:choose>
<c:if test="${empty bean.accountId}">
    <form name="newAccnt" action="" method="POST">
        <table cellpadding="10">
            <tr>
                <td class="ZFieldLabel"><fmt:message key='AccountType'/></td>
                <td><select name="accntType" id="accntType" onchange="accntChange(this)" class="ZSelect">
                    <option value="ZimbraAcct" <c:if test="${param.accntType eq 'ZimbraAcct'}"> selected </c:if> > <fmt:message key='Zimbra'/></option>
                    <option value="YMPAcct" <c:if test="${param.accntType eq 'YMPAcct'}"> selected </c:if> > <fmt:message key='YMP'/></option>
                    <option value="GmailAcct" <c:if test="${param.accntType eq 'GmailAcct'}"> selected </c:if> ><fmt:message key='Gmail'/></option>
                    <option value="LiveAcct" <c:if test="${param.accntType eq 'LiveAcct'}"> selected </c:if> ><fmt:message key='LiveAcct'/></option>
                    <option value="AOLAcct" <c:if test="${param.accntType eq 'AOLAcct'}"> selected </c:if> ><fmt:message key='AOLAcct'/></option>
                    <option value="MSEAcct" <c:if test="${param.accntType eq 'MSEAcct'}"> selected </c:if> ><fmt:message key='MSEAcct'/></option>
                    <option value="OtherAcct"<c:if test="${param.accntType eq 'OtherAcct'}"> selected </c:if> ><fmt:message key='OtherAcct'/></option>
                </select>
                </td>
            </tr>
        </table>
    </form>
</c:if>

<c:if test="${accntType eq 'ZimbraAcct'}">
    <div id="ZimbraAcctDtls">
        <c:choose>
            <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
                <zd:zmailNew uri="${uri}"/>
            </c:when>

            <c:when test="${bean.modify && not bean.allOK}">
                <zd:zmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
                ${zdf:reload(bean)}
                <zd:zmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${not bean.noVerb && bean.allOK}">
                <jsp:forward page="console.jsp">
                    <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                    <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
                </jsp:forward>
            </c:when>

            <c:otherwise>
                <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
<c:if test="${accntType eq 'YMPAcct'}">
    <div id="YMPAcctDtls">
        <c:choose>
            <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
                <zd:ymailNew uri="${uri}"/>
            </c:when>

            <c:when test="${bean.modify && not bean.allOK}">
                <zd:xmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
                ${zdf:reload(bean)}
                <zd:xmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${not bean.noVerb && bean.allOK}">
                <jsp:forward page="console.jsp">
                    <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                    <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
                </jsp:forward>
            </c:when>

            <c:otherwise>
                <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
<c:if test="${accntType eq 'GmailAcct'}">
    <div id="GmailAcctDtls">
        <c:choose>
            <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
                <zd:gmailNew uri="${uri}"/>
            </c:when>

            <c:when test="${bean.modify && not bean.allOK}">
                <zd:xmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
                ${zdf:reload(bean)}
                <zd:xmailManage uri="${uri}"/>
            </c:when>

            <c:when test="${not bean.noVerb && bean.allOK}">
                <jsp:forward page="console.jsp">
                    <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                    <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
                </jsp:forward>
            </c:when>

            <c:otherwise>
                <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
<c:if test="${accntType eq 'LiveAcct'}">
    <c:choose>
        <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
            <zd:lmailNew uri="${uri}"/>
        </c:when>

        <c:when test="${bean.modify && not bean.allOK}">
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
            ${zdf:reload(bean)}
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${not bean.noVerb && bean.allOK}">
            <jsp:forward page="console.jsp">
                    <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                    <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
             </jsp:forward>
        </c:when>

        <c:otherwise>
            <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${accntType eq 'AOLAcct'}">
    <c:choose>
        <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
            <zd:amailNew uri="${uri}"/>
        </c:when>

        <c:when test="${bean.modify && not bean.allOK}">
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
            ${zdf:reload(bean)}
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${not bean.noVerb && bean.allOK}">
            <jsp:forward page="console.jsp">
                    <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                    <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
            </jsp:forward>
        </c:when>

        <c:otherwise>
            <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${accntType eq 'MSEAcct'}">
    <c:choose>
        <c:when test="${(bean.noVerb && empty bean.accountId) || (bean.add && not bean.allOK)}">
            <zd:mmailNew uri="${uri}"/>
        </c:when>

        <c:when test="${bean.modify && not bean.allOK}">
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${(bean.noVerb && not empty bean.accountId) || ((bean.reset || bean.delete) && not bean.allOK)}">
            ${zdf:reload(bean)}
            <zd:xmailManage uri="${uri}"/>
        </c:when>

        <c:when test="${not bean.noVerb && bean.allOK}">
            <jsp:forward page="console.jsp">
                <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
            </jsp:forward>
        </c:when>

        <c:otherwise>
            <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
        </c:otherwise>
    </c:choose>
</c:if>
<c:if test="${accntType eq 'OtherAcct'}">
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
            <jsp:forward page="console.jsp">
                <jsp:param name="srvcName" value="${bean.accountName}"></jsp:param>
                <jsp:param name="accntVerb" value="${bean.verb}"></jsp:param>
            </jsp:forward>
        </c:when>

        <c:otherwise>
            <p class='ZOfflineError'><fmt:message key='UnexpectedError'/></p>
        </c:otherwise>
    </c:choose>
</c:if>
</div>

<table cellpadding="3">
    <tr>
        <td class="ZFieldLabel">&nbsp;</td>
        <td>
            <a href="#" id="saveButton" onclick="OnSubmit()"><img src="/zimbra/desktop/img/saveButton.gif" border="0"></a>
        </td>
        <td>
            <a href="#" id="cancelButton" onclick="OnCancel()"><img src="/zimbra/desktop/img/cancelButton.gif" border="0"></a>
        </td>
        <td><span id="whattodo" class="ZOfflineNotice"></span></td>
    </tr>
</table>

</div>
</div>
</body>
</html>