<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop ${bean.appVersion}</title>
<style type="text/css">
    @import url(/zimbra/desktop/css/offline.css);
    @import url(/zimbra/desktop/css/desktop.css);
</style>
<script type="text/javascript" src="js/desktop.js"></script>
<script type="text/javascript">

function OnAccount(id, zmail) {
    hidden_form.accountId.value = id;
    if (zmail)
        hidden_form.action = "/zimbra/desktop/zmail.jsp";
    else
        hidden_form.action = "/zimbra/desktop/xmail.jsp";
    hidden_form.submit();
}

function OnNew() {
    window.location = "/zimbra/desktop/new.jsp";
}

function OnLogin() {
    window.location = "/zimbra/desktop/login.jsp";
}

</script>
</head>

<body>

<c:choose>
<c:when test="${empty bean.accounts}">

<div id="welcome" class='ZWizardPage ZWizardPageBig'>
    <div class='ZWizardPageTitle'>
        Welcome to the Zimbra Desktop setup wizard!
    </div>

    <p>Zimbra Desktop allows you to access your email while your computer 
        is disconnected from the internet.

    </p>

    <p>To use Zimbra Desktop, you must first enter settings for an existing mail account.  </p>

    <p>     You must be online to setup the account -- if you are not online now, 
        please quit and launch the application again later when you are connected.
    </p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton-focused' onclick="OnNew()">Set Up an Account</button>
            </td>
    </table>
</div>

</c:when>
<c:otherwise>


<form name="hidden_form" method="POST">
    <input type="hidden" name="accountId">
</form>


<div id="console" class="ZWizardPage">
    <div class="ZWizardPageTitle">
		<div class='ZFloatInHead'>Click &nbsp; <b><img src='/zimbra/img/startup/ImgLogoff.gif' width=16px height=16px align=top> Setup</a></b> &nbsp; to come back here later.</div>
    	Account Setup
    </div>

	<p>Click an account name below to manage it.
	</p>


    <table class="ZWizardTable" cellpadding=5 border=0>
    	<tr><th>Account Name</th><th>Email Address</th><th>Last Sync</th><th>Status</th></tr>
    	
    	<c:forEach items="${bean.accounts}" var="account">
	        <tr><td><a href="javascript:OnAccount('${account.id}', ${account.zmail})">${account.name}</a></td>
	            <td>${account.email}</td>
	            <td>${account.lastSync}</td>
	            <td>${account.syncStatus}</td>
	        </tr>
    	</c:forEach>
    </table>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="OnNew()">Set Up Another Account</button>
            </td>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton-focused' onclick="OnLogin()">Go to Zimbra Desktop</button>
            </td>
         </tr>
    </table>
</div>

</c:otherwise>
</c:choose>

</body>
</html>

