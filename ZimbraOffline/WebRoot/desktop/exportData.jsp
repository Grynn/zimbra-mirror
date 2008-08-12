<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.MailBean" scope="request"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

${zdf:reload(bean)}

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<title><fmt:message key="ZimbraDesktop"/></title>
<style type="text/css">
        @import url(/zimbra/desktop/css/offline.css);
</style>
<script type="text/javascript" src="js/desktop.js"></script>
<script type="text/javascript">

var timeoutId;

function onCancel() {
    document.doneForm.verb.value = "";
    document.doneForm.submit();
}

function onSearch() {
    zd.toggle("searchQuery");
}

function onSubmit() {
    var cancelButton = document.getElementById("cancelButton");
    var optionForm = document.optionForm;
    var submitForm = document.submitForm;
    var types = dataTypes();
    var url = "/home/" + encodeURIComponent("${bean.email}") + "/" +
	encodeURIComponent(folderName());

    if (types == null) {
        alert("<fmt:message key="TypeEmpty"/>");
        return;
    }
    zd.hide("submitButton");
    zd.set("status", "<span class='ZOfflineNotice><fmt:message key="Processing"/></span>");
    submitForm.action = url;
    submitForm.name.value = optionForm.name.value;
    submitForm.query.value = optionForm.query.value;
    submitForm.types.value = types;
    submitForm.submit();
    timeoutId = setTimeout('done()', 5000);
}

function done(errstr) {
    zd.hide("status");
    if (errstr) {
        clearTimeout(timeoutId);
        alert(errstr);
        zd.toggle("submitButton");
        history.go(-1);
    } else {
        document.doneForm.verb.value = "exp";
        document.doneForm.submit();
    }
}

</script>
</head>

<body>
<br><br><br><br><br><br>
<div align="center">
<div id="exportData" class="ZWizardPage">
<div class="ZWizardPageTitle">
<div id="settings_hint" class="ZFloatInHead"></div>
    <span id="pageTitle">
        <fmt:message key='ExportDataTitle'><fmt:param>${bean.accountName}</fmt:param></fmt:message>
    </span>
</div>
<br>
<form name="optionForm">
<table cellpadding="0" width="68%">
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td><nobr><fmt:message key="ExportName"/></nobr></td>
                    <td align="right"><input name="name" size="30" value="${bean.accountName}"></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td><hr></td></tr>
    <tr><td><zd:dataTypes/></td></tr>
    <tr><td><zd:folderList export="true"/></td></tr>
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td>
                        <table>
                            <tr>
                                <td width="1%"><input type="checkbox" name="search" onClick="onSearch()"></td>
                                <td><nobr><fmt:message key="ExportQuery"/></nobr></td>
                                <td align="right" id="searchQuery" style="display:none">
                                    <input name="query" size="30">
                               </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</form>
<table class="ZWizardButtonBar" width="100%">
    <tr>
        <td class="ZWizardButton" width="1%">
            <button class='DwtButton' id="submitButton"" onclick="onSubmit()">
                <fmt:message key="ExportData"/>
            </button>
        </td>
        <td class="ZWizardButtonSpacer">
            <p><span id="status"></span></p>
        </td>
        <td class="ZWizardButton" width="1%">
            <button class='DwtButton' id="cancelButton" onclick="onCancel()">
                <fmt:message key="Cancel"/>
            </button>
        </td>
    </tr>
</table>
</div>
<div>
    <iframe name="iframe" id="iframe" frameborder="0 scrolling="no" style="width:0px;height:0px;border:0px"></iframe>
</div>
<form name="submitForm" method="GET" target="iframe">
    <input type="hidden" name="callback" value="done">
    <input type="hidden" name="fmt" value="tgz">
    <input type="hidden" name="name">
    <input type="hidden" name="query">
    <input type="hidden" name="types">
</form>
<form name="doneForm" action="/zimbra/desktop/${bean.zmail ? "z" : "x"}mail.jsp" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="verb">
</form>
</div>
</body>
</html>
