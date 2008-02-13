<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>

<c:if test="${param.loginOp != 'logout' && (param.client == 'advanced' || (param.client == 'standard' && fn:length(bean.accounts) == 1))}">
    <jsp:forward page="/desktop/login.jsp"/>
</c:if>

<html>
<head>
<meta http-equiv="refresh" content="60" >
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<title>Zimbra Desktop ${bean.appVersion}</title>
<style type="text/css">
    @import url(/zimbra/desktop/css/offline.css);
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

function OnLoginTo(username) {
    hidden_form.username.value = username;
    hidden_form.action = "/zimbra/desktop/login.jsp";
    hidden_form.submit();
}

</script>
</head>

<body>
<br><br><br><br><br><br>
<div align="center">
<c:choose>
<c:when test="${empty bean.accounts}">

<div id="welcome" class='ZWizardPage ZWizardPageBig'>
    <div class='ZWizardPageTitle'>
        Welcome to the Zimbra Desktop setup wizard!
    </div>
<span class="padding">
    <p>Zimbra Desktop allows you to access your email while your computer 
        is disconnected from the internet.

    </p>

    <p>To use Zimbra Desktop, you must first enter settings for an existing mail account.  </p>

    <p>     You must be online to setup the account -- if you are not online now, 
        please quit and launch the application again later when you are connected.
    </p>
    </span>
    <table class="ZWizardButtonBar" width="100%">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton" width="1%">
                <button class='DwtButton-focused' onclick="OnNew()">Set Up an Account</button>
            </td>
    </table>

</div>

</c:when>
<c:otherwise>


<form name="hidden_form" method="POST">
    <input type="hidden" name="accountId">
    <input type="hidden" name="username">
</form>


<div id="console" class="ZWizardPage">
    <div class="ZWizardPageTitle">
		<table border=0 width=100% cellpadding=0 cellspacing=0>
			<tr>
				<td class='ZHeadTitle'>
					Zimbra Desktop Accounts Setup
				</td>
				<td class='ZHeadHint'>
					Click <b><img src='/zimbra/img/startup/ImgLogoff.gif' width=16px height=16px align=top> Setup</b> to come back here later.
				</td>
			</tr>
		</table>
    </div>
<span class="padding">
	<p>Click an account name below to manage it.</p>


    <table class="ZWizardTable" cellpadding=5 border=0 align="center">
    	<tr><th>Account Name</th><th>Email Address</th><th>Last Sync</th><th>Status</th></tr>
    	
    	<c:forEach items="${bean.accounts}" var="account">
	        <tr><td><a href="javascript:OnAccount('${account.id}', ${account.zmail})">${account.name}</a></td>
	            <td>${account.email}</td>
	            <td>${account.lastSync}</td>
	            <td><table border="0" cellspacing="0" cellpadding="0"><tr><td class="noborder">
		            <c:choose>
	                   <c:when test="${account.statusUnknown}">
	                      <img src="/zimbra/img/im/ImgOffline.gif">
	                   </c:when>
	                   <c:when test="${account.statusOffline}">
	                       <img src="/zimbra/img/im/ImgImAway.gif">
	                   </c:when>
	                   <c:when test="${account.statusOnline}">
	                       <img src="/zimbra/img/im/ImgImAvailable.gif">
	                   </c:when>
	                   <c:when test="${account.statusRunning}">
	                       <img src="/zimbra/img/animated/Imgwait_16.gif">
	                   </c:when>
	                   <c:when test="${account.statusAuthFailed}">
	                       <img src="/zimbra/img/im/ImgImDnd.gif">
	                   </c:when>
	                   <c:when test="${account.statusError}">
	                       <img height="14" width="14" src="/zimbra/img/dwt/ImgCritical.gif">
	                   </c:when>
		           </c:choose>
		       </td>
		       <td class="noborder">&nbsp;</td>
		       <td class="noborder">     
		           <c:choose>
                       <c:when test="${account.statusUnknown}">
                           unknown
                       </c:when>
                       <c:when test="${account.statusOffline}">
                           offline
                       </c:when>
                       <c:when test="${account.statusOnline}">
                           online
                       </c:when>
                       <c:when test="${account.statusRunning}">
                           in progress
                       </c:when>
                       <c:when test="${account.statusAuthFailed}">
                           can't login
                       </c:when>
                       <c:when test="${account.statusError}">
                           error
                       </c:when>
                   </c:choose>
		       </td></tr></table>
	           </td>
	        </tr>
    	</c:forEach>
    </table>
</span>
<br>
    <table class="ZWizardButtonBar" width="100%">
        <tr>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="OnNew()">Set Up Another Account</button>
            </td>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton" width="1%">
                <button class='DwtButton-focused' onclick="OnLogin()">Go to Zimbra Desktop</button>
            </td>
         </tr>
    </table>
</div>

</c:otherwise>
</c:choose>
</div>
</body>
</html>

