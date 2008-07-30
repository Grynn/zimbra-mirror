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

function onSubfolder() {
    if (document.submitForm.subfolderName.value == "") {
        var date = new Date();
        var s = date.toLocaleString();

        s.replace(/\//, "-");
        document.submitForm.subfolderName.value = s;
    }
    zd.toggle("subfolderInput");
}

function onSubmit() {
    var resolve;
    var submitForm = document.submitForm;
    var types = dataTypes();
    var url = "/home/" + encodeURIComponent("${bean.email}") + "/" +
	encodeURIComponent(folderName()) + "?fmt=tgz";

    for (var i = 0; i < submitForm.resolve.length; i++) {
        if (submitForm.resolve[i].checked) {
            resolve = submitForm.resolve[i].value;
            break;
        }
    }
    if (types == null) {
        alert("<fmt:message key="TypeEmpty"/>");
        return;
    } if (submitForm.file.value == "") {
        alert("<fmt:message key="ImportNoFile"/>");
        return;
    } else if (resolve == "replace" && !confirm("<fmt:message key="OnReplaceWarn"/>")) {
        return;
    } else if (resolve == "reset" && !confirm("<fmt:message key="OnResetWarn"/>")) {
        return;
    }
    url += "&callback=done&resolve=" + resolve;
    if (submitForm.subfolder.checked)
        url += "&subfolder=" + encodeURIComponent(submitForm.subfolderName.value);
    url += "&types=" + types;
    zd.hide("submitButton");
    zd.set("status", "<span class='ZOfflineNotice'><fmt:message key="Processing"/></span>");
    submitForm.action = url;
    submitForm.submit();
}

function done(errstr) {
    zd.hide("status");
    if (errstr) {
        alert(errstr);
        zd.toggle("submitButton");
        history.go(-1);
    } else {
        document.doneForm.verb = "imp";
        document.doneForm.submit();
    }
}

</script>
</head>

<body>
<br><br><br><br><br><br>
<div align="center">
<div id="importData" class="ZWizardPage">
<div class="ZWizardPageTitle">
<div id="settings_hint" class="ZFloatInHead"></div>
    <span id="pageTitle">
        <fmt:message key='ImportDataTitle'><fmt:param>${bean.accountName}</fmt:param></fmt:message>
    </span>
</div>
<br>
<form name="submitForm" method="POST" enctype="multipart/form-data" target="iframe">
<table cellpadding="0" cellspacing="0" width="75%">
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td><nobr><fmt:message key="ImportFile"/></nobr></td>
                    <td align="right"><input type="file" name="file" size="30" accept="application/x-tar-compressed"></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td><hr></td></tr>
    <tr><td><zd:dataTypes/></td></tr>
    <tr><td><zd:folderList export="false"/></td></tr>
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td>
                        <table>
                            <tr>
                                <td width="1%"><input type="checkbox" name="subfolder" onClick="onSubfolder()"></td>
                                <td><nobr><fmt:message key="ImportSubfolder"/></nobr></td>
                                <td align="right" id="subfolderInput" style="display:none">
                                    <input name="subfolderName" size="30">
                               </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td><hr></td></tr>
    <tr>
        <td>
            <table width="100%">
               <tr><td><nobr><fmt:message key="ImportResolve"/></nobr></td></tr>
                <tr>
                    <td align="left" width="1%">
                        <table>
                            <tr>
                                <td width="1%"><input type="radio" name="resolve" value="skip" checked></td>
                                <td><nobr><fmt:message key="ImportSkip"/></nobr></td>
                            </tr>
                        </table>
                    </td>
                    <td align="center">
                        <table>
                            <tr>
                                <td align="right"><input type="radio" name="resolve" value="replace"></td>
                                <td align="left"><nobr><fmt:message key="ImportReplace"/></nobr></td>
                            </tr>
                        </table>
                    </td>
                    <td align="right" width="1%">
                        <table>
                            <tr>
                                <td width="1%"><input type="radio" name="resolve" value="reset"></td>
                                <td><nobr><fmt:message key="ImportReset"/></nobr></td>
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
            <button class='DwtButton' id="submitButton" onclick="onSubmit()">
                <fmt:message key="ImportData"/>
            </button>
        </td>
        <td class="ZWizardButtonSpacer">
            <p><span id="status"></span></p>
        </td>
        <td class="ZWizardButton" width="1%">
            <button class='DwtButton' id="cancelButton" onclick="document.doneForm.submit()">
                <fmt:message key="Cancel"/>
            </button>
        </td>
    </tr>
</table>
</div>
<div>
    <iframe name="iframe" id="iframe" frameborder="0" scrolling="no" style="width:0px;height:0px;border:0px"></iframe>
</div>
<form name="doneForm" action="/zimbra/desktop/${bean.zmail ? "z" : "x"}mail.jsp" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="verb">
</form>
</div>
</body>
</html>
