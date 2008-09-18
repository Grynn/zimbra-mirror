<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

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

function OnCancel() {
    window.location = "/zimbra/desktop/console.jsp";
}

function OnZmail() {
    window.location = "/zimbra/desktop/zmail.jsp";
}

function OnYmail() {
    window.location = "/zimbra/desktop/ymail.jsp";
}

function OnGmail() {
    window.location = "/zimbra/desktop/gmail.jsp";
}

function OnLmail() {
    window.location = "/zimbra/desktop/lmail.jsp";
}

function OnAmail() {
    window.location = "/zimbra/desktop/amail.jsp";
}

function OnXmail() {
    window.location = "/zimbra/desktop/xmail.jsp";
}

function OnMmail() {
    window.location = "/zimbra/desktop/mmail.jsp";
}

</script>
</head>

<body>
<br><br><br><br><br><br>
<div align="center">
<form name="hidden_form" method="POST">
    <input type="hidden" name="accountId">
</form>

<div id="accountType" class="ZWizardPage">
    <div class="ZWizardPageTitle"><fmt:message key='WhatAcct'/></div>

<span class="padding">
    <table cellpadding=10 style='margin-left:20px;'>
        <tr>
            <td valign=top width=200px>
                <button class='DwtButton' onclick="OnZmail()" style='width:100%'>
                    <nobr><img src="/zimbra/img/logo/ImgZimbraIcon.gif" align="absmiddle"> <fmt:message key='ZimbraAcct'/></nobr>
                </button>
            </td>
            <td>
				<fmt:message key='SetupOnZimbraSvr'>
					<fmt:param><a href="javascript:zd.toggle('Zimbra')"><fmt:message key='Zimbra'/></a></fmt:param>
			    </fmt:message>
            </td>
        </tr>
        <tr id='Zimbra' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'><fmt:message key='AbountZCS'/></div>
                    <p><fmt:message key='ToLearnZCS'>
					     <fmt:param><a href=http://www.zimbra.com target=_blank>www.zimbra.com</a></fmt:param>
					   </fmt:message>
					</p>
                    <a href="javascript:zd.toggle('Zimbra')"><fmt:message key='Close'/></a>
                </div>
            </td>
        </tr>
        
        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnYmail()" style='width:100%'>
                    <nobr><fmt:message key='YMPAcct'/></nobr>
                </button>
            </td>
            <td><fmt:message key='YMPNote'> 
					<fmt:param><a href="javascript:zd.toggle('YahooMailPlus')"><fmt:message key='YMP'/></a></fmt:param>
                </fmt:message>
            </td>
        </tr>
        <tr id='YahooMailPlus' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'><fmt:message key='YMPAbout'/></div>
                    <p><fmt:message key='YMPToSignup'>
						 <fmt:param><a href=http://mail.yahoo.com target=_blank><fmt:message key='YMPLink'/></a></fmt:param>
					   </fmt:message>
					</p>
                    <a href="javascript:zd.toggle('YahooMailPlus')"><fmt:message key='Close'/></a>
                </div>
            </td>
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnGmail()" style='width:100%'>
                    <nobr><fmt:message key='GmailAcct'/></nobr>
                </button>
            </td>
            <td><fmt:message key='GmailNote'>
					<fmt:param><a href="javascript:zd.toggle('Gmail')"><fmt:message key='GmailMustAllowIMAP'/></a></fmt:param>
			    </fmt:message>
			</td>
        </tr>

        <tr id='Gmail' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'><fmt:message key='GmailToAllowIMAP'/></div>
            
                        <ol>
                            <li><fmt:message key='GmailLogin'><fmt:param><a href=http://gmail.com target=_blank><fmt:message key='Gmail'/></a></fmt:param></fmt:message>
                            <li><fmt:message key='GmailClickTop'><fmt:param><b><fmt:message key='GmailSettingsLink'/></b></fmt:param></fmt:message>
                            <li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailFwdPOP'/></b></fmt:param></fmt:message>
                            <li><fmt:message key='GmailSelect'><fmt:param><b><fmt:message key='GmailEnableIMAP'/></b></fmt:param></fmt:message>
                            <li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailSaveChgs'/></b></fmt:param></fmt:message>
                            <li><fmt:message key='GmailClose'><fmt:param><b><fmt:message key='GmailAcct'/></b></fmt:param></fmt:message>
                        </ol>

                    <a href="javascript:zd.toggle('Gmail')"><fmt:message key='Close'/></a>
                </div>
            </td>       
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnLmail()" style='width:100%'>
                    <nobr><fmt:message key='LiveAcct'/></nobr>
                </button>
            </td>
            <td><fmt:message key='LiveNote'>
                                        <fmt:param><a href="javascript:zd.toggle('Live')"><fmt:message key='LiveMustAllowClient'/></a></fmt:param>
                            </fmt:message>
                        </td>
        </tr>
        <tr id='Live' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'><fmt:message key='LiveAbout'/></div>
                    <p><fmt:message key='LiveLimit'></fmt:message></p>
                    <a href="javascript:zd.toggle('Live')"><fmt:message key='Close'/></a>
                </div>
            </td>
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnAmail()" style='width:100%'>
                    <nobr><fmt:message key='AOLAcct'/></nobr>
                </button>
            </td>
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnMmail()" style='width:100%'>
                    <nobr><fmt:message key='MSEAcct'/></nobr>
                </button>
            </td>
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnXmail()" style='width:100%'>
                    <nobr><fmt:message key='OtherAcct'/></nobr>
                </button>
            </td>
            <td><fmt:message key='OtherAcctNote'/>
            </td>
        </tr>
    </table>
</span>
    <table class="ZWizardButtonBar" width="100%">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton" width="1%">
                <button class='DwtButton' onclick="OnCancel()"><fmt:message key='Cancel'/></button>
            </td>
    </table>
</div>
</div>
</body>
</html>
