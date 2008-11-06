<%@ page import="com.zimbra.cs.offline.jsp.ZmailBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<c:set var="accountFlavor" value="${param.accountFlavor eq null ? '' : param.accountFlavor}"/>
<c:set var="uri" value="/zimbra/desktop/accsetup.jsp"/>

<c:choose>
    <c:when test="${accountFlavor eq 'AOL'}">
        <jsp:useBean id="abean" class="com.zimbra.cs.offline.jsp.AmailBean" scope="request"/>
        <jsp:setProperty name="abean" property="*"/>
        <jsp:setProperty name="abean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(abean)}
        <c:set var="bean" value="${abean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Gmail'}">
        <jsp:useBean id="gbean" class="com.zimbra.cs.offline.jsp.GmailBean" scope="request"/>
        <jsp:setProperty name="gbean" property="*"/>
        <jsp:setProperty name="gbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(gbean)}
        <c:set var="bean" value="${gbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Imap'}">
        <jsp:useBean id="ibean" class="com.zimbra.cs.offline.jsp.ImapBean" scope="request"/>
        <jsp:setProperty name="ibean" property="*"/>
        <jsp:setProperty name="ibean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(ibean)}
        <c:set var="bean" value="${ibean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Live'}">
        <jsp:useBean id="lbean" class="com.zimbra.cs.offline.jsp.LiveBean" scope="request"/>
        <jsp:setProperty name="lbean" property="*"/>
        <jsp:setProperty name="lbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(lbean)}
        <c:set var="bean" value="${lbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'MSE'}">
        <jsp:useBean id="mbean" class="com.zimbra.cs.offline.jsp.MmailBean" scope="request"/>
        <jsp:setProperty name="mbean" property="*"/>
        <jsp:setProperty name="mbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(mbean)}
        <c:set var="bean" value="${mbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Pop'}">
        <jsp:useBean id="pbean" class="com.zimbra.cs.offline.jsp.PopBean" scope="request"/>
        <jsp:setProperty name="pbean" property="*"/>
        <jsp:setProperty name="pbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(pbean)}
        <c:set var="bean" value="${pbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'YMP'}">
        <jsp:useBean id="ybean" class="com.zimbra.cs.offline.jsp.YmailBean" scope="request"/>
        <jsp:setProperty name="ybean" property="*"/>
        <jsp:setProperty name="ybean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(ybean)}
        <c:set var="bean" value="${ybean}" scope="request"/>    
    </c:when>
    <c:when test="${accountFlavor eq 'Zimbra'}">
        <jsp:useBean id="zbean" class="com.zimbra.cs.offline.jsp.ZmailBean" scope="request"/>
        <jsp:setProperty name="zbean" property="*"/>
        <jsp:setProperty name="zbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(zbean)}
        <c:set var="bean" value="${zbean}" scope="request"/>    
    </c:when>
    <c:otherwise>
    </c:otherwise>
</c:choose>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/desktop/css/offline.css" type="text/css">
<title><fmt:message key="ZimbraDesktop"/></title>

<script type="text/javascript" src="js/desktop.js"></script>
<script type="text/javascript">
function InitScreen() {}

function accntChange(accnt) {
   document.newAccnt.submit();
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnDelete() {
    if (confirm("${onDeleteWarn}")) {
        document.accountForm.verb.value = "del";
        onSubmit();
    }
}

function OnSubmit() {
    zd.enable("accountName");
    zd.hide("cancelButton");
    zd.hide("saveButton");
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
<c:if test="not ${bean.smtpConfigSupported}">
    zd.enable("smtpPassword");
</c:if>
    document.accountForm.submit();
}

function onEditPassword(id) {
    var elem = document.getElementById(id);

    zd.hide("editPasswordRow");
    zd.show("passwordRow");
    elem.value = "";
    elem.focus();
}

function onEditPort(link, id) {
    var elem = document.getElementById(id);

    link.display = "none";
    zd.enable(elem);
    elem.focus();
}
</script>
</head>
<body onload="InitScreen();">
    <br><br>
    <div align="center">
    <img src="/zimbra/desktop/img/YahooZimbraLogo.gif" border="0">
    <br><br>
    <div class="whiteBg">
    <div id="accountType" align="center">
    <table cellpadding="5">
<c:choose>
    <c:when test="${empty bean.accountId}">
        <tr>
            <td align="center" colspan="2">
                <div class="ZWizardPageTitle">
                    <div id='settings_hint' class='ZFloatInHead'></div>
                    <span id='pageTitle'>
                        <h2>
                            <fmt:message key='AccountAdd'></fmt:message>
                        </h2>
                    </span>
                </div>
            </td>
        </tr>
    </c:when>
    <c:otherwise>
        <tr>
            <td align="center" colspan="2">
                <div class="ZWizardPageTitle">
                    <div id='settings_hint' class='ZFloatInHead'></div>
                    <span id='pageTitle'>
                        <h2>
                            <fmt:message key='AccountChange'><fmt:param><fmt:message key='${accountFlavor}'></fmt:message></fmt:param></fmt:message>
                        </h2>
                    </span>
                </div>
            </td>
        </tr>
    </c:otherwise>
</c:choose>
        <tr><td class="ZSeparator" colspan="2">&nbsp;</td></tr>
        <tr>
            <td colspan="2" align="center" width="450px">
<c:choose>
    <c:when test="${accountFlavor eq ''}">
    </c:when>
    <c:when test="${not empty bean.error}" >
                <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
                <p class='ZOfflineError'><fmt:message key='PlsCorrectInput'/></p>
    </c:when>
</c:choose>
            </td>
        </tr>
<c:if test="${empty bean.accountId}">
        <tr>
            <td class="ZFieldLabel"><fmt:message key='AccountType'/></td>
            <td>
                <form name="newAccnt" action="" method="POST">
                    <select name="accountFlavor" id="accountFlavor" onchange="accntChange(this)" class="ZSelect">
                        <option value=""><fmt:message key='AccountSelect'/></option>
                        <option value="AOL" <c:if test="${accountFlavor eq 'AOL'}"> selected </c:if> ><fmt:message key='AOL'/></option>
                        <option value="Gmail" <c:if test="${accountFlavor eq 'Gmail'}"> selected </c:if> ><fmt:message key='Gmail'/></option>
                        <option value="Imap" <c:if test="${accountFlavor eq 'Imap'}"> selected </c:if> ><fmt:message key='Imap'/></option>
                        <option value="MSE" <c:if test="${accountFlavor eq 'MSE'}"> selected </c:if> ><fmt:message key='MSE'/></option>
                        <option value="Live" <c:if test="${accountFlavor eq 'Live'}"> selected </c:if> ><fmt:message key='Live'/></option>
                        <option value="Pop"<c:if test="${accountFlavor eq 'Pop'}"> selected </c:if> ><fmt:message key='Pop'/></option>
                        <option value="YMP" <c:if test="${accountFlavor eq 'YMP'}"> selected </c:if> > <fmt:message key='YMP'/></option>
                        <option value="Zimbra" <c:if test="${accountFlavor eq 'Zimbra'}"> selected </c:if> > <fmt:message key='Zimbra'/></option>
                    </select>
                </form>
            </td>
        </tr>
</c:if>
<c:choose>
    <c:when test="${accountFlavor eq ''}">
    </c:when>
    <c:when test="${not bean.noVerb && bean.allOK}">
        <jsp:forward page="console.jsp">
            <jsp:param name="accountName" value="${bean.accountName}"></jsp:param>
            <jsp:param name="verb" value="${bean.verb}"></jsp:param>
        </jsp:forward>
    </c:when>
    <c:when test="${bean.add || empty bean.accountId}">
        <zd:xmailManage accountFlavor="${accountFlavor}" uri="${uri}" verb="add"/>
    </c:when>
    <c:otherwise>
        ${zdf:reload(bean)}
        <zd:xmailManage accountFlavor="${accountFlavor}" uri="${uri}" verb="mod"/>
    </c:otherwise>
</c:choose>
        <tr><td class="ZSeparator" colspan="2">&nbsp;</td></tr>
        <tr>
            <td align="center" colspan="2">
                <table cellpadding="0" cellspacing="0" width="70%">
                    <tr>
                        <td id="saveButton">
<c:if test="${accountFlavor ne ''}">
<c:set var='save'><fmt:message key='Save'/></c:set>
                            <zd:button onclick='OnSubmit()' text='${save}'/>
</c:if>
                        </td>
                        <td align="center"><span id="whattodo" class="ZOfflineNotice"></span></td>
                        <td id="cancelButton" align="right">
<c:set var='cancel'><fmt:message key='Cancel'/></c:set>
                            <zd:button onclick='OnCancel()' text='${cancel}' primary='false'/>
			</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>
</div>
</div>
</body>
</html>
