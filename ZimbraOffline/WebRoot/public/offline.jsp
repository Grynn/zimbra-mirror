<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.zclient.ZMailbox" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@ page import="com.zimbra.common.util.EasySSLProtocolSocketFactory" %>
<%@ page import="org.apache.commons.httpclient.protocol.Protocol" %>
<%@ page import="com.zimbra.common.service.ServiceException" %>
<%@ page import="com.zimbra.common.util.Pair" %>

<%!
    private final String LOCALHOST_URL = "http://localhost:7633";
    private final String LOCALHOST_ADMIN_URL = "https://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String LOCALHOST_MAIL_URL = LOCALHOST_URL + "/zimbra/mail";
    private final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";

    private final String OFFLINE_REMOTE_URL = "offlineRemoteServerUri";
    private final String OFFLINE_REMOTE_PASSWORD = "offlineRemotePassword";
    private final String OFFLINE_SYNC_INTERVAL = "offlineSyncInterval";

    private ZMailbox.Options getMailboxOptions(String username, String password) {
        ZMailbox.Options options = new ZMailbox.Options(username, Provisioning.AccountBy.name, password, LOCALHOST_URL + ZimbraServlet.USER_SERVICE_URI);
        options.setNoSession(false);
        return options;
    }

    private ZMailbox.Options getMailboxOptions(Account account) {
        return getMailboxOptions(account.getName(), account.getAttr(OFFLINE_REMOTE_PASSWORD));
    }

    private void setAuthCookie(String username, String password, HttpServletResponse response) throws ServiceException {
        String auth = ZMailbox.getMailbox(getMailboxOptions(username, password)).getAuthToken();
        Cookie cookie = new Cookie("ZM_AUTH_TOKEN", auth);
        cookie.setPath("/");
        cookie.setMaxAge(31536000);
        response.addCookie(cookie);
    }

    private void setAuthCookie(Account account, HttpServletResponse response) throws ServiceException {
        setAuthCookie(account.getName(), account.getAttr(OFFLINE_REMOTE_PASSWORD), response);
    }

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("ZM_AUTH_TOKEN", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String formatSyncInterval(String interval_number, String interval_unit) throws Exception {
        try {
            int number = Integer.parseInt(interval_number);
            if (interval_unit.equals("seconds")) {
                number = number < 60 ? 60 : number;
                return Integer.toString(number) + 's';
            } else {
                number = number < 1 ? 1 : number;
                return Integer.toString(number) + 'm';
            }
        } catch (Exception x) {
            throw new Exception("Sync interval must be a valid number");
        }
    }


	// skin stuff, added by Owen
//	final String SKIN_COOKIE_NAME = "ZM_SKIN";
	String skin = "sand";
/*
	String requestSkin = request.getParameter("skin");
	if (requestSkin != null) {
		skin = requestSkin;
	} else if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(SKIN_COOKIE_NAME)) {
				skin = cookie.getValue();
			}
		}
	}
*/	
%>

<%
    Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 7634);
    Protocol.registerProtocol("https", easyhttps);

    SoapProvisioning prov = new SoapProvisioning();
    prov.soapSetURI(LOCALHOST_ADMIN_URL);
    prov.soapZimbraAdminAuthenticate();

    String act = request.getParameter("act");

    String param_account = request.getParameter("account");
    param_account = param_account == null ? "" : param_account.trim();

    String param_password = request.getParameter("password");
    param_password = param_password == null ? "" : param_password.trim();

    String param_url = request.getParameter("server_url");
    param_url = param_url == null ? "" : param_url.trim();

    String param_interval = request.getParameter("sync_interval");
    String param_unit = request.getParameter("interval_unit");
    if (param_interval == null || param_interval.trim().length() == 0) {
        param_interval = "60";
        param_unit = "seconds";
    } else {
        param_interval = param_interval.trim();
    }

    String unit_sec_selected = param_unit.equals("seconds") ? "selected" : "";
    String unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";

    String error = null;
    if (act != null) {
        try {
            if (param_account.length() == 0) {
                error = "Account name must be a valid email address";
            } else if (act.equals("new")) {
                if (param_password.length() == 0) {
                    error = "Password must not be empty";
                } else if (param_url.length() == 0) {
                    error = "Remote server URL must be valid";
                } else {
                    Map attrs = new TreeMap();
                    attrs.put(OFFLINE_REMOTE_URL, param_url);
                    attrs.put(OFFLINE_SYNC_INTERVAL, formatSyncInterval(param_interval, param_unit));
                    prov.createAccount(param_account, param_password, attrs);
                    setAuthCookie(param_account, param_password, response);
                }
            } else {
                Account account = prov.get(Provisioning.AccountBy.name, param_account);
                if (account == null) {
                    error = "Account not found";
                } else {
                    if (act.equals("login")) {
                        setAuthCookie(account, response);
                        response.sendRedirect(LOCALHOST_MAIL_URL);
			return;
                    } else if (act.equals("modify")) {
                        Map attrs = new TreeMap();
                        attrs.put(OFFLINE_REMOTE_URL, param_url);
                        attrs.put(OFFLINE_SYNC_INTERVAL, formatSyncInterval(param_interval, param_unit));
                        if (!param_password.equals("********")) {
                            attrs.put(OFFLINE_REMOTE_PASSWORD, param_password);
                        }
                        prov.modifyAttrs(account, attrs, true);
                        setAuthCookie(account, response);
                    } else if (act.equals("reset")) {
                        prov.deleteMailbox(account.getId());
                        //need to access again to trigger creation of mailbox and start sync
                        ZMailbox.getMailbox(getMailboxOptions(account));
                    } else if (act.equals("delete")) {
                        prov.deleteMailbox(account.getId());
                        prov.deleteAccount(account.getId());
                        clearAuthCookie(response);
                    } else {
                        error = "Unknown action";
                    }
                }
            }
        } catch (Throwable t) {
            error = t.getMessage();
        }
    }

%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop Account Manager</title>
<style type="text/css">
<!--
@import url(<%= LOCALHOST_RESOURCE_URL %>css/imgs,common,dwt,msgview,login,zm,<%= skin %>_imgs,skin.css?debug=1&skin=<%= skin %>);
-->
</style>

<%
    List<Account> accounts =  prov.getAllAccounts(null);
%>



<script type="text/javascript">

<%
if (accounts.size() > 0) {
%>

    function showManageAccount() {
        byId('manageAccount').style.display = 'block';
        byId('changeSettings').style.display = 'none';
    }

    function showChangeSettings() {
        byId('manageAccount').style.display = 'none';
        byId('changeSettings').style.display = 'block';
    }

    function OnLogin() {
        login_form.submit();
    }

    function OnModify() {
        update_account.act.value = "modify";
        update_account.submit();
    }

    function OnReset() {
        if (confirm('Local disk content of desktop account "' + update_account.account.value + '" will be deleted. The desktop account will resync everything from "' + update_account.server_url.value + '". OK to proceed?')) {
            update_account.act.value = "reset"
            update_account.submit();
        }
    }

    function OnDelete() {
        if (confirm('Desktop account "' + update_account.account.value + '" and its content will be purged from disk. The corresponding server account on "' + update_account.server_url.value + '" will not be affected. OK to proceed?')) {
            update_account.act.value = "delete"
            update_account.submit();
        }
    }

    function OnSaveChange() {
        update_account.act.value = "modify";
        update_account.submit();
    }

    function InitScreen() {
        <% if (act == null || act.equals("reset")) { %>
            showManageAccount();
        <% } else if (act.equals("modify")) { %>
            showChangeSettings();
        <% } else if (act.equals("new")) { %>
            byId('setupWizard3').style.display = 'block';
        <% } %>
    }

<% } else { %>

    function showSetupWizardStart() {
        byId('setupWizard1').style.display = 'block';
        byId('setupWizard2').style.display = 'none';
        byId('accountDeleted').style.display = 'none';
    }

    function showZimbraAccountPage() {
        byId('setupWizard1').style.display = 'none';
        byId('setupWizard2').style.display = 'block';
        byId('accountDeleted').style.display = 'none';
    }

    function showAccountDeletedPage() {
        byId('setupWizard1').style.display = 'none';
        byId('setupWizard2').style.display = 'none';
        byId('accountDeleted').style.display = 'block';
    }

    function OnNew() {
        new_account.act.value = "new";
        new_account.submit();
        return true;
    }


	function togglePlatformNotice(id) {
		// LINUX???
		var isMac = (navigator.userAgent.indexOf("Macintosh") > -1);
		id = id + (isMac ? "-Mac" : "-isWin");
		toggleNotice(id);
	}
	
	
    function InitScreen() {
        <% if (act == null) { %>
            showSetupWizardStart();
        <% } else if (act.equals("delete")) { %>
            showAccountDeletedPage();
        <% } else { %>
            showZimbraAccountPage();
        <% } %>
    }

<% } %>



    function byId(id) {
        return document.getElementById(id);
    }


    function launch() {
        window.location = "http://localhost:7633/zimbra/mail";
    }

    function toggleNotice(id) {
        var it = byId(id);
        it.style.display = (it.style.display == 'block' ? 'none' : 'block');
    }

</script>
</head>
<body onload="InitScreen()">


<%
    if (accounts.size() > 0) {
        Account acc = accounts.get(0);
        String name = acc.getName();
        String url = acc.getAttr(OFFLINE_REMOTE_URL);
        String interval = acc.getAttr(OFFLINE_SYNC_INTERVAL);
        if (interval == null || interval.length() == 0) {
            interval = "60s";
        }
        unit_sec_selected = interval.endsWith("s") ? "selected" : "";
        unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";
        interval = interval.substring(0, interval.length() - 1);
%>

    <form name="login_form" action="/zimbra/" method="POST">
        <input type="hidden" name="account" value="<%=name%>">
        <input type="hidden" name="act" value="login">
    </form>

<% if (act != null && act.equals("new")) { %>

    <div id="setupWizard3" class="ZWizardPage">
        <div class="ZWizardPageTitle"><div class='ZWizardPageNumber'>3 of 3</div> Account Setup Confirmed</div>

        <p>Your account "<%= param_account %>" has been successfully set up.
        </p>

        <p>The first synchronization takes a little while to run, but you
            can start using this account right away.
        </p>

        <p>For the best experience, always access your email with the "Zimbra Desktop" icon
            on your desktop/startmenu/etc, whether online or offline.
            You will be logged in automatically.
        </p>

        <p>Press <span class="ZWizardButtonRef">Launch</span> to run Zimbra Desktop now.
        </p>

        <table class="ZWizardButtonBar"><tr>
            <td class="ZWizardButtonSpacer"><div></div></td>
            <td class="ZWizardButton"><button onclick="OnLogin()">Launch</button></td>
        </table>
    </div>

<% } else { %>


    <div id="manageAccount" class="ZWizardPage">
        <div class="ZWizardPageTitle">Manage Account</div>

        <% if (error != null) { %>
            <p><font color="red"><%= error %></font></p>
        <% } else if (act != null && act.equals("reset")) { %>
            <p><font color="blue">All local data has been cleared and account will resynchronize with the server.</font></p>
        <% } else { %>
            <p>What do you want to do?</p>
        <% } %>

        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top><button onclick="showChangeSettings()" style='width:140px'><nobr>Change Account Setup</nobr></button></td>
                <td>Change account setup (password, synch interval, etc)</td>
            </tr>
            <tr>
                <td valign=top><button onclick="OnReset()" style='width:100%'><nobr>Reset Desktop Account</nobr></button></td>
                <td>Clear all local mail data and resynchronize with the server.</td>
            </tr>
            <tr>
                <td valign=top><button onclick="OnDelete()" style='width:100%'><nobr>Delete Desktop Account</nobr></button></td>
                <td>Delete all local mail data and login information.
                    You can still access this account through your web browser.
                    The next time you use Zimbra Desktop, the setup wizard will prompt you to set up another account.
                </td>
            </tr>
        </table>

        <table class="ZWizardButtonBar"><tr>
            <td class="ZWizardButtonSpacer"><div></div></td>
            <td class="ZWizardButton"><button onclick="OnLogin()">Launch</button></td>
        </table>
    </div>

    <div id="changeSettings" class="ZWizardPage">
        <div class="ZWizardPageTitle">Change Account Settings</div>

        <% if (error != null) { %>
            <p><font color="red"><%= error %></font></p>
        <% } else if (act != null && act.equals("modify")) { %>
            <p><font color="blue">Desktop mailbox settings have been updated.</font></p>
        <% } else { %>
            <p>What do you want to change?</p>
        <% } %>


        <form name="update_account" action="/zimbra/" method="POST">

        <input type="hidden" name="account" value="<%=name%>">
        <input type="hidden" name="act">

        <table class="ZWizardForm">
            <tr>
                <td class="ZFieldLabel">Zimbra Server URL:</td>
                <td><input style='width:200px' class="ZField" type="text" id="url" name="server_url" value="<%=url%>"></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Email address:</td>
                <td><input style='width:200px' class="ZField" type="text" id="email" value="<%=name%>" disabled> <a href="javascript:toggleNotice('changeAccount')">How to change account?</a></td>
            </tr>
	<tr><td colspan='2'>
        <div id='changeAccount' class='infoBox' style='display:none'>
                <div class='infoTitle'>Only a single Zimbra account is supported.</div>

                <p>If you want to replace the existing desktop account with another one you must first delete the existing desktop account:
                <ol>
                        <li> Press <span class='ZWizardButtonRef'>Back</span> to go back to Manage Account
                        <li> Press <span class='ZWizardButtonRef'>Delete Desktop Account</span> and confirm to delete the existing account
                        <li> Once downloaded mailbox data has been deleted, follow the wizard to setup a new account
                </ol>
                <a href="javascript:toggleNotice('changeAccount')">Done</a>
        </div>
	</td></tr>
            <tr>
                <td class="ZFieldLabel">Password:</td>
                <td><input style='width:100px' class="ZField" type="password" id="paswd" name="password" value="********"></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Synchronize every:</td>
                <td><input style='width:50px' class="ZField" type="text" id="syncQuantity" name="sync_interval" value="<%=interval%>">
                    <select class="ZSelect" id="syncUnits" name="interval_unit">
                        <option <%=unit_sec_selected%>>seconds</option>
                        <option <%=unit_min_selected%>>minutes</option>
                    </select>
                </td>
            </tr>
        </table>


       </form>

        <table class="ZWizardButtonBar"><tr>
            <td class="ZWizardButtonSpacer"><div></div></td>
            <td class="ZWizardButton"><button onclick="showManageAccount()">Back</button></td>
            <td class="ZWizardButton"><button onclick="OnModify()">Save Changes</button></td>
        </table>

    </div>

<% } %>

<% } else { %>

<div id="accountDeleted" class="ZWizardPage">
	<div class="ZWizardPageTitle">Manage Account</div>

    <p>Desktop mailbox of "<%=param_account%>" has been deleted.</p>

	<table class="ZWizardButtonBar"><tr>
		<td class="ZWizardButtonSpacer"><div></div></td>
		<td class="ZWizardButton"><button onclick="showSetupWizardStart()">OK</button></td>
	</table>
</div>

<%
    if (error == null || act == null || !act.equals("new")) {
        param_account = "";
        param_password = "";
        param_url = "";
        param_interval = "60";
    }
%>

<div id="setupWizard1" class='ZWizardPage' style='display:block'>
	<div class='ZWizardPageTitle'><div class='ZWizardPageNumber'>1 of 3</div> Zimbra Desktop Setup</div>
	<div class='ZWizardHeader'>Welcome to Zimbra Desktop setup wizard</div>

	<p>You will be guided through the steps to set up Zimbra Desktop
		to synchronize your email for use while your computer is disconnected from the Internet.
	</p>

	<p>You must be online to set up your account -- if you are not online now,
		please re-launch the application later when you are connected.
	</p>

	<p>In order to synchronize your email, we must store the login
		information and email data on your computer.  For maximum security,
		you may want to verify that your computer login password is required
		to access this computer.  <a href="javascript:togglePlatformNotice('secureSetup')">How do I do this?</a>
	</p>

	<div id='secureSetup-Win' class='infoBox' style='display:none'>
		<div class='infoTitle'>For maximum security, follow all of the guidelines below.</div>

		<p>On Windows:
		<ol>
			<li> Launch Start -> Control Panel.
			<li> Make sure you have a reasonable password on the account
			<li> Require the password to log in
			<li> Require password to resume from hibernation/standby
			<li> Require password to unlock screen saver
		</ol>
		<a href="javascript:togglePlatformNotice('secureSetup')">Done</a>
	</div>

	<div id='secureSetup-Mac' class='infoBox' style='display:none'>
		<div class='infoTitle'>For maximum security, follow all of the guidelines below.</div>
		<ol>
			<li> Launch "System Preferences".
			<li> Choose the "Accounts" icon and ensure you have a reasonable passsword on the account
			<li> Choose the "Security" icon and:
				<ol>
					<li> Check the options:
						<br>
						[] Require password to wake this computer from sleep or screen saver
						<br>
						[] Log out after [X] minutes of inactivity

					<li> Uncheck the options:
						<br>
						[] Disable automatic login
				</ol>
		</ol>

		<a href="javascript:togglePlatformNotice('secureSetup')">Done</a>
	</div>

	<div class="ZWizardHeader">What type of account do you want to set up?</div>

	<table class="ZWizardForm">
		<tr><td><input type=radio id='accountType_zimbra' name="accountType" value="zimbra" checked="true"></td>
			<td><label class="ZRadioLabel" for='accountType_zimbra'>Zimbra account</label></td>
		</tr>
		<tr><td><input type=radio id='accountType_pop' name="accountType" value="pop" disabled></td>
			<td><label class="ZRadioLabelDisabled" for='accountType_pop'>POP account (coming soon)</label></td>
		</tr>
		<tr><td><input type=radio id='accountType_imap' name="accountType" value="imap" disabled></td>
			<td><label class="ZRadioLabelDisabled" for='accountType_imap'>IMAP account (coming soon)</label></td>
		</tr>
	</table>


	<table class="ZWizardButtonBar"><tr>
		<td class="ZWizardButtonSpacer"><div></div></td>
		<td class="ZWizardButton ZDisabled"><button>Back</button></td>
		<td class="ZWizardButton"><button onclick="showZimbraAccountPage()">Next</button></td>
	</table>
</div>



<div id="setupWizard2" class="ZWizardPage">
	<div class="ZWizardPageTitle"><div class='ZWizardPageNumber'>2 of 3</div> Zimbra Account Setup</div>

    <% if (error == null) { %>
        <p>To establish a connection to the Zimbra server and 
        	to verify that your mailbox account is accessible, enter 
        	your Zimbra account email address, password, and the server's URL address. 
			Configure how often to synchronize with the server in either minutes or seconds. 
		</p>
    <% } else { %>
        <p><font color="red"><%= error %></font></p>
    <% } %>

    <form name="new_account" action="/zimbra/" method="POST">

    <input type="hidden" name="act">

    <table class="ZWizardForm">
		<tr>
			<td class="ZFieldLabel">Zimbra Server URL:</td>
			<td><input style='width:200px' class="ZField" type="text" id="server_url" name="server_url" value="<%=param_url%>"> <font color="gray">including http:// or https:// (e.g. http://mail.company.com)</font></td>
		</tr>
		<tr>
			<td class="ZFieldLabel">Email address:</td>
			<td><input style='width:200px' class="ZField" type="text" id="account" name="account" value="<%=param_account%>"> <font color="gray">including @domain (e.g. john@company.com)</font></td>
		</tr>
		<tr>
			<td class="ZFieldLabel">Password:</td>
			<td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="<%=param_password%>"> <font color="gray">server login password</font></td>
		</tr>
		<tr>
			<td class="ZFieldLabel">Synchronize every:</td>
			<td><input style='width:50px' class="ZField" type="text" id="sync_interval" name="sync_interval" value=<%=param_interval%>>
				<select class="ZSelect" id="interval_unit" name="interval_unit">
					<option <%=unit_sec_selected%>>seconds</option>
					<option <%=unit_min_selected%>>minutes</option>
				</select>
				<font color="gray">how often to synchronize with server</font>
			</td>
		</tr>
	</table>

    </form>

    <p>Press <span class="ZWizardButtonRef">Test</span> to verify these settings</p>

	<table class="ZWizardButtonBar"><tr>
		<td class="ZWizardButtonSpacer"><div></div></td>
		<td class="ZWizardButton"><button onclick="showSetupWizardStart()">Back</button></td>
		<td class="ZWizardButton"><button onclick="OnNew()">Test</button></td>
	</table>
</div>

<% } %>

</body>
</html>
