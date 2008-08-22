<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.MailBean" scope="request"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

${zdf:doRequest(bean)}

<c:set var="prefsToFetch" value="zimbraPrefSkin,zimbraPrefClientType,zimbraPrefLocale"/>
<c:set var="attrsToFetch" value="zimbraFeatureMailEnabled,zimbraFeatureCalendarEnabled,zimbraFeatureContactsEnabled,zimbraFeatureIMEnabled,zimbraFeatureNotebookEnabled,zimbraFeatureOptionsEnabled,zimbraFeaturePortalEnabled,zimbraFeatureTasksEnabled,zimbraFeatureVoiceEnabled,zimbraFeatureBriefcasesEnabled,zimbraFeatureMailUpsellEnabled,zimbraFeatureContactsUpsellEnabled,zimbraFeatureCalendarUpsellEnabled,zimbraFeatureVoiceUpsellEnabled"/>

<c:catch var="loginException">
    <zm:login username="${empty param.username ? bean.loginUsername : param.username}" password="anythingisfine"
        varRedirectUrl="postLoginUrl" varAuthResult="authResult"
        prefs="${prefsToFetch}" attrs="${attrsToFetch}"
        rememberme="true" requestedSkin="${param.skin}"/>
</c:catch>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<title><fmt:message key="ZimbraDesktop"/></title>
<style type="text/css">@import url(/zimbra/desktop/css/offline.css);</style>
<script type="text/javascript" src="js/desktop.js"></script>
<script type="text/javascript">

function OnCancel() {
    document.submitForm.action = "/zimbra/desktop/${bean.zmail ? 'z' : 'x'}mail.jsp";
    document.submitForm.submit();
}

function OnExport() {
    document.submitForm.action = "/zimbra/desktop/exportData.jsp";
    document.submitForm.submit();
}

function OnImport() {
    document.submitForm.action = "/zimbra/desktop/importData.jsp";
    document.submitForm.submit();
}

function OnReset() {
    if (confirm("<fmt:message key='OnResetWarn'/>")) {
        zd.disable("cancelButton");
        zd.disable("exportButton");
        zd.disable("importButton");
        zd.disable("resetButton");
        zd.set("status", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
        document.submitForm.verb.value = "rst";
        document.submitForm.submit();
    }
}

</script>
</head>

<body>
<br><br><br><br><br><br>
<div align="center">
<c:choose>
    <c:when test="${bean.noVerb && bean.allOK}">
<div id="dataManage" class="ZWizardPage">
<div class="ZWizardPageTitle">
<div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'>
        <fmt:message key='ManageDataTitle'><fmt:param>${bean.accountName}</fmt:param></fmt:message>
    </span>
</div>
<table cellpadding=10 style='margin-left: 20px;'>
    <tr>
        <td valign=top width=200px>
            <button class='DwtButton' id="exportButton" onclick="OnExport()" style='width: 100%'>
                <nobr><fmt:message key='ExportData'/></nobr>
            </button>
        </td>
    </tr>
    <tr>
        <td valign=top>
            <button class='DwtButton' id="importButton" onclick="OnImport()" style='width: 100%'>
                <nobr><fmt:message key='ImportData'/></nobr>
            </button>
        </td>
    </tr>
    <tr>
        <td valign=top>
            <button class='DwtButton' id="resetButton" onclick="OnReset()" style='width: 100%'>
                <nobr><fmt:message key='ResetData'/></nobr>
            </button>
        </td>
    </tr>
</table>
<table class="ZWizardButtonBar" width="100%">
    <tr>
        <td class="ZWizardButtonSpacer">
            <p><span id="status"></span></p>
        </td>
        <td class="ZWizardButton" width="1%">
            <button class='DwtButton' id="cancelButton" onclick="OnCancel()">
                <fmt:message key="Cancel"/>
            </button>
        </td>
    </tr>
</table>
</div>
<form name="submitForm" action="/zimbra/desktop/manageData.jsp" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="email" value="${bean.email}">
    <input type="hidden" name="verb">
</form>
    </c:when>
    <c:otherwise>
        <zd:xmailDone uri="/zimbra/desktop/console.jsp" name="${bean.accountName}"/>
    </c:otherwise>
</c:choose>
</div>
</body>
</html>
