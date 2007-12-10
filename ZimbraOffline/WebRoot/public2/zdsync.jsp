<%@ page import="java.util.HashMap" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.account.DataSource" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>

<%!
    private final String LOCALHOST_URL = "http://localhost:7633";
    private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String LOCALHOST_THIS_URL = LOCALHOST_URL + "/zimbra/public/zdsync.jsp";

    private final String ZDSETUP_URL = "/zimbra/public/zdsetup.jsp";
    private final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";
    
    private final String ZDLOGIN_URL = "/public/zdlogin.jsp";
    private final String ZDLOGIN_DEV_URL = "/public/zdlogin.jsp?dev=1";

    private final String OFFLINE_PROXY_HOST = "offlineProxyHost";
    private final String OFFLINE_PROXY_PORT = "offlineProxyPort";
    private final String OFFLINE_PROXY_USER = "offlineProxyUser";
    private final String OFFLINE_PROXY_PASS = "offlineProxyPass";

    private final String OFFLINE_REMOTE_URL = "offlineRemoteServerUri";
    private final String OFFLINE_REMOTE_PORT = "offlineRemoteServerPort";
    private final String OFFLINE_REMOTE_SERVER = "offlineRemoteServerName";
    private final String OFFLINE_REMOTE_SECURECONN = "offlineRemoteSecureConn";
    private final String OFFLINE_REMOTE_PASSWORD = "offlineRemotePassword";
    private final String OFFLINE_SYNC_INTERVAL = "offlineSyncInterval";

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

    String skin = "sand";
%>

<%
    SoapProvisioning prov = new SoapProvisioning();
    prov.soapSetURI(LOCALHOST_ADMIN_URL);
    prov.soapZimbraAdminAuthenticate();

    String isDev = (String)request.getParameter("dev");
    isDev = isDev == null ? "" : isDev.trim();
    String act = request.getParameter("act");

    String param_account = request.getParameter("account");
    param_account = param_account == null ? "" : param_account.trim();

    if (act != null && act.equals("login")) {
        if (param_account.length() > 0)
            request.setAttribute("username", param_account);
        if (isDev != null && isDev.equals("1")) {
            pageContext.forward(ZDLOGIN_DEV_URL);
        } else {
            pageContext.forward(ZDLOGIN_URL);
        }
        return;
    }

    String pwdchange = request.getParameter("chng");
    String param_password = request.getParameter("password");
    param_password = param_password == null ? "" : param_password.trim();

    //bug:15554 zimbra server url shouldn't require http:// or https://
    String param_server = request.getParameter("server_name");
    param_server = param_server == null ? "" : param_server.trim();

    String param_port = request.getParameter("server_port");
    param_port = param_port == null ? "80" : param_port.trim();

    String param_secureconn = request.getParameter("server_secured");
    param_secureconn = param_secureconn == null ? "0" : "1";
    if (param_secureconn.equals("1") && param_port.equals("80")) {
        param_port = "443";
    }

    String param_url = param_secureconn.equals("1") ? "https://" : "http://";
    if (param_port.equals("443") || param_port.equals("80")) {
        param_url = param_url + param_server;
    } else {
        param_url = param_url + param_server + ":" + param_port;
    }
    //end

    String param_proxy_host = request.getParameter("proxy_host");
    param_proxy_host = param_proxy_host == null ? "" : param_proxy_host.trim();
    String param_proxy_port = request.getParameter("proxy_port");
    param_proxy_port = param_proxy_port == null ? "" : param_proxy_port.trim();
    String param_proxy_user = request.getParameter("proxy_user");
    param_proxy_user = param_proxy_user == null ? "" : param_proxy_user.trim();
    String param_proxy_pass = request.getParameter("proxy_pass");
    param_proxy_pass = param_proxy_pass == null ? "" : param_proxy_pass.trim();

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
            Account account = null;
            Map<String, Object> attrs = new HashMap<String, Object>();
            if (act.equals("new") || act.equals("modify")) {
                if (param_account.length() == 0) {
                    error = "Service name must not be empty";
                } else if (param_password.length() == 0) {
                    error = "Password must not be empty";
                } else if (param_server.length() == 0) {
                    error = "Server name must be a valid hostname or IP address";
                } else if (param_port.length() == 0) {
                    error = "Server port must be a valid port number";
                } else {
                    attrs.put(OFFLINE_REMOTE_URL, param_url);
                    attrs.put(OFFLINE_REMOTE_PORT, param_port);
                    attrs.put(OFFLINE_REMOTE_SERVER, param_server);
                    attrs.put(OFFLINE_REMOTE_SECURECONN, param_secureconn);
                    attrs.put(OFFLINE_SYNC_INTERVAL, formatSyncInterval(param_interval, param_unit));

                    attrs.put(OFFLINE_PROXY_HOST, param_proxy_host);
                    attrs.put(OFFLINE_PROXY_PORT, param_proxy_port);
                    attrs.put(OFFLINE_PROXY_USER, param_proxy_user);
                    attrs.put(OFFLINE_PROXY_PASS, param_proxy_pass);

                    if (!param_password.equals("********")) {
                        attrs.put(OFFLINE_REMOTE_PASSWORD, param_password);
                    }
                }
            }


            if (error == null) {
                if (act.equals("new")) {
                    prov.createAccount(param_account, param_password, attrs);
                } else {
                    account = prov.get(Provisioning.AccountBy.name, param_account);
                    if (account == null) {
                        error = "Account not found";
                    } else {
                        if (act.equals("modify")) {
                            prov.modifyAttrs(account, attrs, true);
                        } else if (act.equals("reset")) {
                            prov.deleteMailbox(account.getId());
                        } else if (act.equals("delete")) {
                            prov.deleteMailbox(account.getId());
                            prov.deleteAccount(account.getId());
                        } else {
                            error = "Unknown action";
                        }
                    }
                }
            }
        } catch (Throwable t) {
            error = t.getMessage();
        }
    }

    List<Account> accounts = prov.getAllAccounts(null);
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop <%= com.zimbra.common.localconfig.LC.get("zdesktop_version") %></title>
<style type="text/css">
<!--
@import url(<%= LOCALHOST_RESOURCE_URL %>css/imgs,common,dwt,msgview,login,zm,<%= skin %>_imgs,skin.css?debug=1&skin=<%= skin %>);
-->
</style>

<!-- load the minimal ajax library used to show/hide/etc -->
<script type="text/javascript" src="Ajax.js"></script>

<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/util/AjxTimedAction.js"></script>
<script type="text/javascript">

function InitScreen() {
<% if (error != null) { %>
<% if (act.equals("new")) { %>
    Ajax.showPanel('newAccount');
<% } else if (act.equals("modify")) { %>
	Ajax.showPanel('changeAccount_i');
<% } else if (act.equals("reset") || act.equals("delete")) { %>
	Ajax.showPanel('manageAccounts');
<% } %>
<% } else { %>
<% if (act == null) { %>
	Ajax.showPanel('manageAccounts');
<% } else if (act.equals("new")) { %>
	Ajax.showPanel('accountCreated');
<% } else if (act.equals("modify")) { %>
	Ajax.showPanel('accountModified');
<% } else if (act.equals("reset")) { %>
	Ajax.showPanel('accountReset');
<% } else if (act.equals("delete")) { %>
	Ajax.showPanel('accountDeleted');
<% } %>
<% } %>
}


function showSetupPage() {
    window.location = "<%=ZDSETUP_URL%>"
}

function OnNew() {
    new_account.submit();
}

function OnModify(f) {
    f.submit();
}

function OnLogin(account) {
    hidden_form.act.value = "login";
    hidden_form.account.value = account;
    hidden_form.submit();
}

function OnReset(account, server) {
    if (confirm('Local disk content of desktop account "' + account + '" will be deleted. The desktop account will resync everything from "' + server + '". OK to proceed?')) {
        hidden_form.act.value = "reset"
        hidden_form.account.value = account;
        hidden_form.submit();
    }
}

function OnDelete(account, server) {
    if (confirm('Desktop account "' + account + '" and its content will be purged from disk. The corresponding server account on "' + server + '" will not be affected. This might take a long time depending on the mailbox size. OK to proceed?')) {
        hidden_form.act.value = "delete"
        hidden_form.account.value = account;
        hidden_form.submit();
    }
}

function toDelete(account, server) {
    if (confirm('Desktop account "' + account + '" and its content will be purged from disk. The corresponding server account on "' + server + '" will not be affected. This might take a long time depending on the mailbox size. OK to proceed?')) {
        var id = "DELACCOUNT";
        var ajxRpcReq = new AjxRpcRequest(id);
        var ajxCallBack = new AjxCallback(null,actCallBack);
        ajxRpcReq.invoke(null, "/public/actoffline.jsp?act=del&account="+ account, null, ajxCallBack, true, null);
    }
}

function actCallBack(req) {
    if(req.success && req.text == "DONE") {
        window.location = "http://localhost:7633/zimbra/";
    } else {
        alert("Error while deleting the account "+update_account.account.value);
    }
}

function secureCheck() {
    var secCheck = document.getElementById('server_secured') == null ? document.getElementById('secured') : document.getElementById('server_secured') ;
    if(secCheck.checked) {
        document.getElementById('server_port') == null ? document.getElementById('port').value = "443" : document.getElementById('server_port').value = "443";
    } else {
        document.getElementById('server_port') == null ? document.getElementById('port').value = "80" : document.getElementById('server_port').value = "80";
    }
}

function editPort() {
    document.getElementById('server_port') == null ? document.getElementById('port').disabled = false : document.getElementById('server_port').disabled = false;
}

</script>
</head>
<body onload="InitScreen()">

<form name="hidden_form" action="<%=LOCALHOST_THIS_URL%>" method="POST">
    <input type="hidden" name="act">
    <input type="hidden" name="account">
</form>


<!-- NOTE: replace with file panel/manageAccounts.jsp --->
<div id="manageAccounts" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts</div>


    <% if (error != null) { %>
        <p><font color="red"><%= error %></font></p>
    <% } else { %>
        <p>&nbsp;</p>
    <% } %>


    <%
        if (accounts != null && accounts.size() > 0) {
            for (int i = 0; i < accounts.size(); ++i) {
                Account account = accounts.get(i);
                String server = account.getAttr(OFFLINE_REMOTE_SERVER);
                String username = account.getName();
    %>
    <div class="ZWizardPageTitle"><%=server%> --&gt; <%=username%></div>


        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top>
                    <button onclick="Ajax.showPanel('changeAccount_<%=i%>')" style='width:140px'>
                        <nobr>Change Account Setup</nobr>
                    </button>
                </td>
                <td>Change account setup (password, check mail interval, etc)</td>
            </tr>
            <tr>
                <td valign=top>
                    <button onclick="OnReset('<%=username%>', '<%=server%>')" style='width:100%'>
                        <nobr>Reset Desktop Account</nobr>
                    </button>
                </td>
                <td>Clear all local mail data and resynchronize with the server.
                </td>
            </tr>
            <tr>
                <td valign=top>
                    <button onclick="OnDelete('<%=username%>', '<%=server%>')" style='width:100%'>
                        <nobr>Delete Desktop Account</nobr>
                    </button>
                </td>
                <td>Delete all local mail data and login information.
                    You can still access this account through your web browser.
                    The next time you use Zimbra Desktop, the setup wizard will prompt you to set up another account.
                </td>
            </tr>
            <tr>
                <td valign=top>
                    <button onclick="OnLogin('<%=username%>')" style='width:100%'>
                        <nobr>Go To This Mailbox</nobr>
                    </button>
                </td>
                <td>(Temporary) Go to the single mailbox view of this account.  For multi-mailbox view click "Back" and "Launch".
                </td>
            </tr>
        </table>

    <% } %>
    <% } else { %>

    <p>No account has been provisioned</p>

    <% } %>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="showSetupPage()">Back</button>
            </td>

            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="Ajax.showPanel('newAccount')">Add New Account</button>
            </td>
    </table>
</div>

<% if (act != null && act.equals("new")) { %>

<!-- NOTE: replace with file panel/accountCreated.jsp --->
<div id="accountCreated" class="ZWizardPage">
    <div class="ZWizardPageTitle">Desktop Account Created</div>

    <p>Your account "<%= param_account %>" has been successfully set up.
    </p>

    <p>The first synchronization takes a little while to run, but you
        can start using this account right away.
    </p>

    <p>For the best experience, always access your email with the "Zimbra Desktop" icon
        on your desktop/startmenu/etc, whether online or offline.
        You will be logged in automatically.
    </p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="Ajax.showPanel('manageAccounts')">OK</button>
            </td>
    </table>
</div>

<% } %>


<% if (act != null && act.equals("modify")) { %>

<!-- NOTE: replace with file panel/accountModified.jsp --->
<div id="accountModified" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts</div>

    <p>Changes to account "<%=param_account%>" has been saved.</p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="Ajax.showPanel('manageAccounts')">OK</button>
            </td>
    </table>
</div>

<% } %>

<% if (act != null && act.equals("reset")) { %>

<!-- NOTE: replace with file panel/accountReset.jsp --->
<div id="accountReset" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts</div>

    <p>Account "<%=param_account%>" has been reset.  It will take a while to resync all the data from server.</p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="Ajax.showPanel('manageAccounts')">OK</button>
            </td>
    </table>
</div>

<% } %>

<% if (act != null && act.equals("delete")) { %>

<!-- NOTE: replace with file panel/accountDeleted.jsp --->
<div id="accountDeleted" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts</div>

    <p>Account "<%=param_account%>" has been deleted.</p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="Ajax.showPanel('manageAccounts')">OK</button>
            </td>
    </table>
</div>

<% } %>

<%
    if (accounts != null && accounts.size() > 0) {
        for (int i = 0; i < accounts.size(); ++i) {
            Account acc = accounts.get(i);
            String name = acc.getName();
            String port = acc.getAttr(OFFLINE_REMOTE_PORT);
            String servername = acc.getAttr(OFFLINE_REMOTE_SERVER);
            String secureconn = acc.getAttr(OFFLINE_REMOTE_SECURECONN);
            String interval = acc.getAttr(OFFLINE_SYNC_INTERVAL);

            String accountProxyHost = acc.getAttr(OFFLINE_PROXY_HOST);
            accountProxyHost = accountProxyHost == null ? "" : accountProxyHost;
            String accountProxyPort = acc.getAttr(OFFLINE_PROXY_PORT);
            accountProxyPort = accountProxyPort == null ? "" : accountProxyPort;
            String accountProxyUser = acc.getAttr(OFFLINE_PROXY_USER);
            accountProxyUser = accountProxyUser == null ? "" : accountProxyUser;
            String accountProxyPass = acc.getAttr(OFFLINE_PROXY_PASS);
            accountProxyPass = accountProxyPass == null ? "" : accountProxyPass;

            String checked = secureconn.equals("1") ? "checked" : "";

            if (interval == null || interval.length() == 0) {
                interval = "60s";
            }
            unit_sec_selected = interval.endsWith("s") ? "selected" : "";
            unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";
            interval = interval.substring(0, interval.length() - 1);
%>

<!-- NOTE: replace with file panel/changeAccount_i.jsp --->
<div id="changeAccount_<%=i%>" class="ZWizardPage">
<div class="ZWizardPageTitle">Change Account Settings</div>

<% if (error != null) { %>
<p><font color="red"><%= error %>
</font></p>
<% } else if (act != null && act.equals("modify")) { %>
<p><font color="blue">Account settings have been updated.</font></p>
<% } else { %>
<p>What do you want to change?</p>
<% } %>


<form name="update_account_<%=i%>" action="<%=LOCALHOST_THIS_URL%>" method="POST">

        <input type="hidden" name="account" value="<%=name%>">
        <input type="hidden" name="act" value="modify">

        <table class="ZWizardForm">
            <tr>
                <td class="ZFieldLabel">Zimbra Server name:</td>
                <td><input style='width:200px' class="ZField" type="text" id="servername" name="server_name" value="<%=servername%>"> <font color="gray">(e.g. mail.company.com)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Zimbra Server port:</td>
                <td><input style='width:50px' class="ZField" type="text" id="port" name="server_port" value="<%=port%>"> <font color="gray">(e.g. 80)</font> &nbsp;&nbsp;<a href="#" onclick="editPort();">Edit</a></td>
            </tr>
            <tr>
                <td class="ZFieldLable">Use Secure connection:</td>
                <td><input <%=checked%> class="ZField" type="checkbox" id="secured" name="server_secured" onclick="secureCheck();"></td>
            </tr>


            <tr>
                <td class="ZFieldLabel">Proxy host:</td>
                <td><input style='width:200px' class="ZField" type="text" id="mod_proxyhost" name="proxy_host" value="<%=accountProxyHost%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy port:</td>
                <td><input style='width:50px' class="ZField" type="text" id="mod_proxyport" name="proxy_port" value="<%=accountProxyPort%>"> <font color="gray">(e.g. 8888)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy username:</td>
                <td><input style='width:200px' class="ZField" type="text" id="mod_proxyuser" name="proxy_user" value="<%=accountProxyUser%>"> <font color="gray">(if proxy requires authentication)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy password:</td>
                <td><input style='width:200px' class="ZField" type="text" id="mod_proxypass" name="proxy_pass" value="<%=accountProxyPass%>"> <font color="gray">(if proxy requires authentication)</font></td>
            </tr>


            <tr>
                <td class="ZFieldLabel">Email address:</td>
                <td><input style='width:200px' class="ZField" type="text" id="email" value="<%=name%>" disabled> <a href="javascript:Ajax.toggle('changeAccountInfo')">How to change account?</a></td>
            </tr>
        <tr><td colspan='2'>
        <div id='changeAccountInfo' class='infoBox' style='display:none'>
                <div class='infoTitle'>Only a single Zimbra account is supported.</div>

                <p>If you want to replace the existing desktop account with another one you must first delete the existing desktop account:
                <ol>
                        <li> Press <span class='ZWizardButtonRef'>Back</span> to go back to Manage Account
                        <li> Press <span class='ZWizardButtonRef'>Delete Desktop Account</span> and confirm to delete the existing account
                        <li> Once downloaded mailbox data has been deleted, follow the wizard to setup a new account
                </ol>
                <a href="javascript:Ajax.toggle('changeAccountInfo')">Done</a>
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

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button onclick="Ajax.showPanel('manageAccounts')">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnModify(update_account_<%=i%>)">Save Changes</button>
        </td>
</table>

</div>

<% } %>

<% } %>


<%
    if (error == null || act == null || !act.equals("new")) {
        param_account = "";
        param_password = "";
        param_url = "";
        param_interval = "60";
        param_port = "80";
        param_server = "";

        param_proxy_host = "";
        param_proxy_port = "";
        param_proxy_user = "";
        param_proxy_pass = "";
    }
%>


<!-- NOTE: replace with file panel/newAccount.jsp --->
<div id="newAccount" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Zimbra Account Setup
</div>

<% if (error == null) { %>
<p>To establish a connection to the Zimbra server and 
            to verify that your mailbox account is accessible, enter 
            your Zimbra account email address, password, and the server's URL address. 
            Configure how often to synchronize with the server in either minutes or seconds. 
</p>
<% } else { %>
<p><font color="red"><%= error %>
</font></p>
<% } %>

<form name="new_account" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="new">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Zimbra Server name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="server_name" name="server_name" value="<%=param_server%>"> <font color="gray">(e.g. mail.company.com)</font></td>
        </tr>
        <tr>
             <td class="ZFieldLabel">Zimbra Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" disabled="true" id="server_port" name="server_port" value="<%=param_port%>"> <font color="gray">(e.g. 80)</font>&nbsp;&nbsp;<a href="#" onclick="editPort();">Edit</a></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="server_secured" name="server_secured" onclick="secureCheck();"></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Proxy host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxyhost" name="proxy_host" value="<%=param_proxy_host%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="proxyport" name="proxy_port" value="<%=param_proxy_port%>"> <font color="gray">(e.g. 8888)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxyuser" name="proxy_user" value="<%=param_proxy_user%>"> <font color="gray">(if proxy requires authentication)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Proxy password:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxypass" name="proxy_pass" value="<%=param_proxy_pass%>"> <font color="gray">(if proxy requires authentication)</font></td>
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

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button onclick="Ajax.showPanel('manageAccounts')">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnNew()">Test</button>
        </td>
</table>
</div>

</body>
</html>
