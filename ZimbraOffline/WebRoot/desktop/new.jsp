<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<title>Zimbra Desktop ${bean.appVersion}</title>
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

function OnXmail() {
    window.location = "/zimbra/desktop/xmail.jsp";
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
    <div class="ZWizardPageTitle">What type of account do you want to set up?</div>


    <table cellpadding=10 style='margin-left:20px;'>
        <tr>
            <td valign=top width=200px>
                <button class='DwtButton' onclick="OnZmail()" style='width:100%'>
                    <nobr><img src="/zimbra/img/logo/ImgZimbraIcon.gif" align="absmiddle"> Zimbra Account</nobr>
                </button>
            </td>
            <td>Set up an account on a Zimbra mail server.
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnYmail()" style='width:100%'>
                    <nobr>Yahoo! Mail Plus Account</nobr>
                </button>
            </td>
            <td>Note: you need a <a href="javascript:zd.toggle('YahooMailPlus')">Yahoo! Mail Plus account</a>
                for this to work.
            </td>
        </tr>
        <tr id='YahooMailPlus' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'>About Yahoo! Mail Plus</div>
                    <p>If you are not yet a Yahoo! Mail user, <a href=http://mail.yahoo.com target=_blank>go here</a> to sign up.</p>
                    <p>Once you are a Yahoo! Mail user, login to your account and click on <b>Mail Upgrades</b> to upgrade to a Yahoo! Mail Plus account.</p>
                    <a href="javascript:zd.toggle('YahooMailPlus')">Done</a>
                </div>
            </td>
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnGmail()" style='width:100%'>
                    <nobr>Gmail Account</nobr>
                </button>
            </td>
            <td>Note: your Gmail account <a href="javascript:zd.toggle('Gmail')">must allow IMAP access</a>.</td>
        </tr>

        <tr id='Gmail' style='display:none'>
            <td colspan=2>
                <div class='infoBox' style='margin:0px 30px 0px 30px;'>
                    <div class='infoTitle'>To allow IMAP access from your Gmail account</div>
            
                        <ol>
                            <li>Log in to your <a href=http://gmail.com target=_blank>Gmail account</a>.
                            <li>Click <b>Settings</b> at the top of any Gmail page.
                            <li>Click <b>Forwarding and POP/IMAP</b>.
                            <li>Select <b>Enable IMAP</b>.
                            <li>Click <b>Save Changes</b>.
                            <li>Close Gmail, come back to this screen and click the <b>Gmail Account</b> button to above.
                        </ol>

                    <a href="javascript:zd.toggle('Gmail')">Done</a>
                </div>
            </td>       
        </tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="OnXmail()" style='width:100%'>
                    <nobr>Other POP/IMAP Account</nobr>
                </button>
            </td>
            <td>Set up an account on an other POP or IMAP mail server,
                    for example, an account from your Internet Service Provider.
            </td>
        </tr>
    </table>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="OnCancel()">Cancel</button>
            </td>
    </table>
</div>
</div>
</body>
</html>
