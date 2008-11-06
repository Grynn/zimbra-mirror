<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

<c:set var="accounts" value="${bean.accounts}"/>
<c:set var='add'><fmt:message key='AccountAdd'/></c:set>
<c:set var='login'><fmt:message key='GotoDesktop'/></c:set>
<c:set var='moveup'><fmt:message key='MoveUp'/></c:set>

<c:if test="${param.loginOp != 'logout' && (param.client == 'advanced' || (param.client == 'standard' && fn:length(accounts) == 1))}">
    <jsp:forward page="/desktop/login.jsp"/>
</c:if>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/desktop/css/offline.css" type="text/css">
<title><fmt:message key="ZimbraDesktop"/></title>

<script type="text/javascript" src="js/desktop.js"></script>
<script type="text/javascript">
function OnAdd() {
    window.location = "/zimbra/desktop/accsetup.jsp";
}

function OnDelete(id, type, flavor) {
    if (confirm("<fmt:message key='OnDeleteWarn'/>"))
        submit(id, type, flavor, "del");
}

function OnEdit(id, type, flavor) {
    submit(id, type, flavor, "");
}

function OnLogin() {
    window.location = "/zimbra/desktop/login.jsp";
}

function OnPromote(id, type, flavor) {
    document.accountForm.action = "/zimbra/desktop/console.jsp";
    submit(id, type, flavor);
}

function OnReset(id, type, flavor) {
    if (confirm("<fmt:message key='OnResetWarn'/>"))
        submit(id, type, flavor, "rst");
}

function submit(id, type, flavor, verb) {
    zd.hide("addButton");
    zd.hide("loginButton");
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
    document.accountForm.accountId.value = id;
    document.accountForm.accountType.value = type;
    document.accountForm.accountFlavor.value = flavor;
    document.accountForm.verb.value = verb;
    document.accountForm.submit();
}
</script>
</head>
<body>
<form name="accountForm" action="/zimbra/desktop/accsetup.jsp" method="POST">
    <input type="hidden" name="accountId">
    <input type="hidden" name="accountType">
    <input type="hidden" name="accountFlavor">
    <input type="hidden" name="verb">
</form>
<br><br>
<div align="center">
<img src="/zimbra/desktop/img/YahooZimbraLogo.gif" border="0">
<br><br>
<div class="whiteBg">
<div id="console" align="center">
<table cellpadding="5" width="90%">
<c:choose>
<c:when test="${empty accounts}">
    <tr>
        <td align="center">
            <div class="ZWizardPageTitle">
                <div id='settings_hint' class='ZFloatInHead'></div>
                <span id='pageTitle'>
                    <h2>
                        <fmt:message key='WizardTitle'></fmt:message>
                    </h2>
                </span>
            </div>
        </td>
    </tr>
    <tr><td class="ZSeparator">&nbsp;</td></tr>
    <tr>
        <td>
            <div align="center"><fmt:message key='WizardDesc'/></div><br>
            <span class="padding">
                <ol>
                    <li>
                        <b><fmt:message key='WizardDescP1'/></b><br>
                        <fmt:message key='WizardDescInfo1'/>
                    </li>
                    <li><b><fmt:message key='WizardDescP2'/></b><br>
                        <fmt:message key='WizardDescInfo2'/>
                    </li>
                    <li><b><fmt:message key='WizardDescP3'/></b><br>
                        <fmt:message key='WizardDescInfo3'/>
                    </li>
                    <li><b><fmt:message key='WizardDescP4'/></b><br>
                        <fmt:message key='WizardDescInfo4'/>
                    </li>
                </ol>
            <br>
        </td>
    </tr>
    <tr>
        <td align="center"><zd:button onclick='OnAdd()' text='${add}'/></td>
    </tr>
</c:when>
<c:otherwise>
    <tr>
        <td align="center">
            <div class="ZWizardPageTitle">
                <div id='settings_hint' class='ZFloatInHead'></div>
                <span id='pageTitle'>
                    <h2>
                        <fmt:message key='HeadTitle'></fmt:message>
                    </h2>
                </span>
            </div>
        </td>
    </tr>
    <tr><td class="ZSeparator">&nbsp;</td></tr>
<c:if test="${not empty param.verb && not empty param.srvcName}">
    <tr>
        <td>
            <div id="serviceMessage" class="infoBg">
    <c:choose>
        <c:when test="${param.verb eq 'add'}">
                <b><fmt:message key='ServiceAdded'><fmt:param>${param.accountName}</fmt:param></fmt:message></b>
                <p><fmt:message key='ServiceAddedNote'/></p>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${param.verb eq 'del'}">
                    <c:set var="key" value="ServiceDeleted"/>
                </c:when>
                <c:when test="${param.verb eq 'mod'}">
                    <c:set var="key" value="ServiceUpdated"/>
                </c:when>
                <c:when test="${param.verb eq 'rst'}">
                    <c:set var="key" value="ServiceReset"/>
                </c:when>
            </c:choose>
                <b><fmt:message key="${key}"><fmt:param>${param.accountName}</fmt:param></fmt:message></b>
        </c:otherwise>
    </c:choose>
            </div>
        </td>
    </tr>
</c:if>
    <tr>
        <td>
            <table cellpadding="0" cellspacing="0" width="100%">
<c:forEach items="${accounts}" var="account">
                <tr>
                    <td>
                        <h3 style="display:inline;">${account.name}</h3>
                    </td>
                </tr>
                <tr>
                    <td>${account.email}</td>
                    <td>
    <c:choose>
       <c:when test="${account.statusUnknown}">
                        <i><img src="/zimbra/img/im/ImgOffline.gif" align="absmiddle">&nbsp;<fmt:message key='StatusUnknown'/></i>
       </c:when>
       <c:when test="${account.statusOffline}">
                        <i><img src="/zimbra/img/im/ImgImAway.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOffline'/></i>
       </c:when>
       <c:when test="${account.statusOnline}">
                        <i><img src="/zimbra/img/im/ImgImAvailable.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOnline'/></i>
       </c:when>
       <c:when test="${account.statusRunning}">
                        <i><img src="/zimbra/img/animated/Imgwait_16.gif" align="absmiddle">&nbsp;<fmt:message key='StatusInProg'/></i>
       </c:when>
       <c:when test="${account.statusAuthFailed}">
                        <i><img src="/zimbra/img/im/ImgImDnd.gif" align="absmiddle">&nbsp;<fmt:message key='StatusCantLogin'/></i>
       </c:when>
       <c:when test="${account.statusError}">
                        <i><img height="14" width="14" src="/zimbra/img/dwt/ImgCritical.gif" align="absmiddle">&nbsp;<fmt:message key='StatusErr'/></i>
       </c:when>
    </c:choose>
                   </td>
                   <td align="center" width="80px">&nbsp;
   <c:if test="${not account.first}">
                       <a href="javascript:OnPromote('${account.id}', '${account.type}', '${account.flavor}')"><img src="/zimbra/desktop/img/sortArrow.gif" border="0" alt="${moveup}"></a>
   </c:if>
                   </td>
                </tr>
                <tr>
                    <td>
                        <a href="javascript:OnEdit('${account.id}', '${account.type}', '${account.flavor}')" id='editButton'><fmt:message key="Edit"/></a>&nbsp;
                        <a href="javascript:OnDelete('${account.id}', '${account.type}', '${account.flavor}')" id='deleteButton'><fmt:message key="Delete"/></a>&nbsp;
                        <a href="javascript:OnReset('${account.id}', '${account.type}', '${account.flavor}')"  id='resetButton'><fmt:message key="ResetData"/></a>
                    </td>
    <c:choose>
    	<c:when test='${account.lastSync != null}'>
                    <td>
                        <i class="ZHint"><fmt:message key='LastSync'/>&nbsp;<fmt:formatDate value="${account.lastSync}" type="both" dateStyle="short" timeStyle="short"/></i>
                    </td>
    	</c:when>
    </c:choose>
                </tr>
                <tr><td class="ZSeparator" colspan="3">&nbsp;</td></tr>
</c:forEach>
            </table>
        </td>
    </tr>
    <tr></tr>
    <tr>
        <td>
            <table cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td id="loginButton">
                        <zd:button onclick='OnLogin()' text='${login}'/>
                    </td>
                    <td align="center"><span id="whattodo" class="ZOfflineNotice"></span></td>
                    <td id="addButton" align="right">
                        <zd:button onclick='OnAdd()' text='${add}'/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</c:otherwise>
</c:choose>
</table>
</div>
</body>
</html>

