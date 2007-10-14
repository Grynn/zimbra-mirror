<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@ page import="com.zimbra.cs.account.DataSource" %>

<%!
    private final String ZDBASE_URL = "http://localhost:7633";
    private final String ZDADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String ZDLOGIN_URL = "/public/zdlogin.jsp";
    private final String ZDLOGIN_DEV_URL = "/public/zdlogin.jsp?dev=1";

    private final String ZDSYNC_URL = "/zimbra/public/zdsync.jsp";
    private final String ZDIMPORT_URL = "/zimbra/public/zdimport.jsp";

    private final String ZDSETUP_URL = "/zimbra/public/zdsetup.jsp";
    private final String RESOURCE_URL = "/zimbra/";

    private final String OFFLINE_PROXY_HOST = "offlineProxyHost";
    private final String OFFLINE_PROXY_PORT = "offlineProxyPort";
    private final String OFFLINE_PROXY_USER = "offlineProxyUser";
    private final String OFFLINE_PROXY_PASS = "offlineProxyPass";

    String skin = "sand";
%>

<%
    SoapProvisioning prov = new SoapProvisioning();
    prov.soapSetURI(ZDADMIN_URL);
    prov.soapZimbraAdminAuthenticate();

    final String LOCAL_ACCOUNT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    Account localAccount = prov.get(Provisioning.AccountBy.id, LOCAL_ACCOUNT_ID);
    
    String isDev = (String)request.getParameter("dev");
    isDev = isDev == null ? "" : isDev.trim();
    String act = request.getParameter("act");

    if (act != null && act.equals("login")) {
        if (isDev != null && isDev.equals("1")) {
            pageContext.forward(ZDLOGIN_DEV_URL);
        } else {
            pageContext.forward(ZDLOGIN_URL);
        }
        return;
    }

    String param_proxy_host = request.getParameter("proxy_host");
    param_proxy_host = param_proxy_host == null ? "" : param_proxy_host.trim();
    String param_proxy_port = request.getParameter("proxy_port");
    param_proxy_port = param_proxy_port == null ? "" : param_proxy_port.trim();
    String param_proxy_user = request.getParameter("proxy_user");
    param_proxy_user = param_proxy_user == null ? "" : param_proxy_user.trim();
    String param_proxy_pass = request.getParameter("proxy_pass");
    param_proxy_pass = param_proxy_pass == null ? "" : param_proxy_pass.trim();


    String error = null;
    if (act != null && act.equals("modify")) {
        try {
            Map<String, Object> attrs = new HashMap<String, Object>();

            attrs.put(OFFLINE_PROXY_HOST, param_proxy_host);
            attrs.put(OFFLINE_PROXY_PORT, param_proxy_port);
            attrs.put(OFFLINE_PROXY_USER, param_proxy_user);
            attrs.put(OFFLINE_PROXY_PASS, param_proxy_pass);

            prov.modifyAttrs(localAccount, attrs, true);
        } catch (Throwable t) {
            error = t.getMessage();
        }
    }
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop <%= com.zimbra.common.localconfig.LC.get("zdesktop_version") %></title>
<style type="text/css">
<!--
@import url(<%= RESOURCE_URL %>css/imgs,common,dwt,msgview,login,zm,<%= skin %>_imgs,skin.css?debug=1&skin=<%= skin %>);
-->
</style>

<script type="text/javascript" src="<%= RESOURCE_URL %>js/ajax/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%= RESOURCE_URL %>js/ajax/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%= RESOURCE_URL %>js/ajax/util/AjxTimedAction.js"></script>
<script type="text/javascript">

function byId(id) {
    return document.getElementById(id);
}

function toggleNotice(id) {
    var it = byId(id);
    it.style.display = (it.style.display == 'block' ? 'none' : 'block');
}

function togglePlatformNotice(id) {
    // LINUX???
    var isMac = (navigator.userAgent.indexOf("Macintosh") > -1);
    id = id + (isMac ? "-Mac" : "-isWin");
    toggleNotice(id);
}

function ShowWelcome() {
    byId('welcome').style.display = 'block';
    byId('console').style.display = 'none';
    //byId('settings').style.display = 'none';
}

function ShowConsole() {
    byId('welcome').style.display = 'none';
    byId('console').style.display = 'block';
    //byId('settings').style.display = 'none';
}

function ShowSettings() {
    byId('welcome').style.display = 'none';
    byId('console').style.display = 'none';
    //byId('settings').style.display = 'block';
}

function zdsync() {
    window.location = "<%=ZDSYNC_URL%>";
}

function zdimport() {
    window.location = "<%=ZDIMPORT_URL%>";
}


function OnLogin() {
    hidden_form.act.value = "login";
    hidden_form.submit();
}

function OnModifySettings() {
    settings.act.value = "modify";
    settings.submit();
}

function InitScreen() {
<% if (act != null && act.equals("modify")) { %>
    ShowSettings(); 
<%--<% } else if (accounts.size() + dataSources.size() == 0) { %>--%>
//    ShowWelcome();
<% } else { %>
    ShowConsole();
<% } %>
}

</script>
</head>
<body onload="InitScreen()">


<form name="hidden_form" action="<%=ZDSETUP_URL%>" method="POST">
    <input type="hidden" name="act">
    <input type="hidden" name="dev" value="<%=isDev%>">
</form>

<div id="welcome" class='ZWizardPage' style='display:block'>
    <div class='ZWizardPageTitle'>
        Zimbra Desktop Setup
    </div>
    <div class='ZWizardHeader'>Welcome to Zimbra Desktop setup wizard</div>

    <p>You will be guided through the steps to set up Zimbra Desktop
       to synchronize your email for use while your computer is disconnected from the Internet.
    </p>

    <p>You must be online to set up your account -- if you are not online now,
       please re-launch the application later when you are connected.
    </p>

    <p>In order to synchronize your email, we must store the login
        information and email data on your computer. For maximum security,
        you may want to verify that your computer login password is required
        to access this computer. <a href="javascript:togglePlatformNotice('secureSetup')">How do I do this?</a>
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

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton ZDisabled">
                <button>Back</button>
            </td>
            <td class="ZWizardButton">
                <button onclick="ShowConsole()">Next</button>
            </td>
    </table>
</div>


<div id="console" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts</div>

    <% if (error != null) { %>
    <p><font color="red"><%= error %>
    </font></p>
    <% } else if (act != null && act.equals("reset")) { %>
    <p><font color="blue">All local data has been cleared and account will resynchronize with the server.</font></p>
    <% } else { %>
    <p>What do you want to do?</p>
    <% } %>

    <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
        <tr>
            <td valign=top>
                <button onclick="zdsync()" style='width:100%'>
                    <nobr>Manage Zimbra Accounts</nobr>
                </button>
            </td>
            <td>Manage sync relationship with Zimbra accounts at service providers
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button onclick="zdimport()" style='width:100%'>
                    <nobr>Manage POP/IMAP Accounts</nobr>
                </button>
            </td>
            <td>Manage settings for importing data from POP/IMAP accounts at service providers
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button onclick="zdyahoo()" style='width:100%' disabled>
                    <nobr>Manage Yahoo! Accounts</nobr>
                </button>
            </td>
            <td> (Coming soon) Manage settings for importing data from Yahoo! accounts
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button onclick="ShowSettings()" style='width:100%' disabled>
                    <nobr>Change Settings</nobr>
                </button>
            </td>
            <td>(Coming soon) Change settings such as network proxy, etc.</td>
        </tr>

    </table>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="OnLogin()">Launch</button>
            </td>
    </table>
</div>

<div id="settings" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Network Proxy Settings
</div>

<% if (error != null) { %>
<p><font color="red"><%= error %></font></p>
<% } %>

<p>
    Some corportate firewalls require the use of proxy servers in order to connect to the Internet.
    If you are not sure whether this applies to you, please consult with your IT department.
</p>

<form name="settings" action="/zimbra/" method="POST">

    <input type="hidden" name="act">

    <table class="ZWizardForm">

        <tr>
            <td class="ZFieldLabel">Proxy host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxyhost" name="proxy_host"
                       value="<%=param_proxy_host%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="proxyport" name="proxy_port"
                       value="<%=param_proxy_port%>"> <font color="gray">(e.g. 8888)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxyuser" name="proxy_user"
                       value="<%=param_proxy_user%>"> <font color="gray">(if proxy requires authentication)</font>
            </td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy password:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxypass" name="proxy_pass"
                       value="<%=param_proxy_pass%>"> <font color="gray">(if proxy requires authentication)</font>
            </td>
        </tr>

    </table>

</form>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button onclick="ShowConsole()">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnModifySettings()" disabled>Save Changes</button>
        </td>
</table>

</div>

<div id="changesSaved" class="ZWizardPage">
<div class="ZWizardPageTitle">Change Settings</div>

<% if (error != null) { %>
<p><font color="red"><%= error %>
</font></p>
<% } else if (act != null && act.equals("modify")) { %>
<p><font color="blue">Desktop mailbox settings have been updated.</font></p>
<% } else { %>
<p>What do you want to change?</p>
<% } %>


<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button onclick="showManageAccount()">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnModify()">Save Changes</button>
        </td>
</table>

</div>

</body>
</html>
