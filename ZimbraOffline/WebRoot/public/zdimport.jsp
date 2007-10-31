<%@ page import="java.util.HashMap" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.account.DataSource" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@page import="com.zimbra.cs.mailbox.Mailbox"%>

<%!
    private final String LOCALHOST_URL = "http://localhost:7633";
    private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String LOCALHOST_THIS_URL = LOCALHOST_URL + "/zimbra/public/zdimport.jsp";

    private final String ZDSETUP_URL = "/zimbra/public/zdsetup.jsp";
    private final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";

    private final String A_zimbraDataSourceSyncInterval = "zimbraDataSourceSyncInterval";

    private final String A_zimbraDataSourceUseProxy = "zimbraDataSourceUseProxy";
    private final String A_zimbraDataSourceProxyHost = "zimbraDataSourceProxyHost";
    private final String A_zimbraDataSourceProxyPort = "zimbraDataSourceProxyPort";

    private final String A_zimbraDataSourceSmtpHost = "zimbraDataSourceSmtpHost";
    private final String A_zimbraDataSourceSmtpPort = "zimbraDataSourceSmtpPort";
    private final String A_zimbraDataSourceSmtpConnectionType = "zimbraDataSourceSmtpConnectionType";
    private final String A_zimbraDataSourceSmtpAuthRequired = "zimbraDataSourceSmtpAuthRequired";
    private final String A_zimbraDataSourceSmtpAuthUsername = "zimbraDataSourceSmtpAuthUsername";
    private final String A_zimbraDataSourceSmtpAuthPassword = "zimbraDataSourceSmtpAuthPassword";
    
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

    final String LOCAL_ACCOUNT_NAME = "local@host.local";
    Account localAccount = prov.get(Provisioning.AccountBy.name, LOCAL_ACCOUNT_NAME);
    String act = request.getParameter("act");
    if (act != null && act.equals("wipe")) {
        prov.deleteMailbox(localAccount.getId());
        prov.deleteAccount(localAccount.getId());
        localAccount = prov.get(Provisioning.AccountBy.name, LOCAL_ACCOUNT_NAME);
    }
        
    List<DataSource> dataSources = prov.getAllDataSources(localAccount);

    String param_service = request.getParameter("service");
    param_service = param_service == null ? "" : param_service.trim();
    String param_username = request.getParameter("username");
    param_username = param_username == null ? "" : param_username.trim();
    String param_password = request.getParameter("password");
    param_password = param_password == null ? "" : param_password.trim();
    
    String param_email = request.getParameter("email");
    param_email = param_email == null ? "" : param_email.trim();
    String param_from_display = request.getParameter("from_display");
    param_from_display = param_from_display == null ? "" : param_from_display.trim();
    String param_replyto = request.getParameter("replyto");
    param_replyto = param_replyto == null ? "" : param_replyto.trim();
    String param_replyto_display = request.getParameter("replyto_display");
    param_replyto_display = param_replyto_display == null ? "" : param_replyto_display.trim();

    String param_host = request.getParameter("server_host");
    param_host = param_host == null ? "" : param_host.trim();
    String param_port = request.getParameter("server_port");
    param_port = param_port == null ? "" : param_port.trim();

    String param_protocol = request.getParameter("protocol_name");
    DataSource.Type dsType = param_protocol == null ? DataSource.Type.pop3 : DataSource.Type.valueOf(param_protocol);
    String pop3_checked = dsType == DataSource.Type.pop3 ? "checked" : "";
    String imap_checked = dsType == DataSource.Type.imap ? "checked" : "";

    String param_leave_on_server = request.getParameter("leave_on_server");
    String leave_on_server_checked = param_leave_on_server == null ? "" : "checked";
    param_leave_on_server = param_leave_on_server == null ? "FALSE" : "TRUE";

    String param_pop_folder = request.getParameter("pop_folder");
    String pop_to_inbox_checked = "new".equals(param_pop_folder) ? "" : "checked";
    String pop_to_new_checked = "new".equals(param_pop_folder) ? "checked" : "";
    
    String param_secure = request.getParameter("server_secure");
    DataSource.ConnectionType connType = param_secure == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.ssl;
    String ssl_checked = connType == DataSource.ConnectionType.ssl ? "checked" : "";

    String param_smtp_host = request.getParameter("smtp_host");
    param_smtp_host = param_smtp_host == null ? "" : param_smtp_host.trim();
    String param_smtp_port = request.getParameter("smtp_port");
    param_smtp_port = param_smtp_port == null ? "" : param_smtp_port.trim();
    String param_smtp_secure = request.getParameter("smtp_secure");
    DataSource.ConnectionType smtpConnType = param_smtp_secure == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.ssl;
    String smtp_ssl_checked = smtpConnType == DataSource.ConnectionType.ssl ? "checked" : "";
    String param_smtp_auth = request.getParameter("smtp_auth");
    String smtp_auth_checked = param_smtp_auth == null ? "" : "checked";
    param_smtp_auth = param_smtp_auth == null ? "FALSE" : "TRUE";
    String param_smtp_user = request.getParameter("smtp_user");
    param_smtp_user = param_smtp_user == null ? "" : param_smtp_user.trim();
    String param_smtp_pass = request.getParameter("smtp_pass");
    param_smtp_pass = param_smtp_pass == null ? "" : param_smtp_pass.trim();

    String param_use_proxy = request.getParameter("use_proxy");
    param_use_proxy = param_use_proxy == null ? "FALSE" : "TRUE";
    String use_proxy_checked = param_use_proxy != null && param_use_proxy.equalsIgnoreCase("true") ? "checked" : "";
    String param_proxy_host = request.getParameter("proxy_host");
    param_proxy_host = param_proxy_host == null ? "" : param_proxy_host.trim();
    String param_proxy_port = request.getParameter("proxy_port");
    param_proxy_port = param_proxy_port == null ? "" : param_proxy_port.trim();

    String param_interval = request.getParameter("sync_interval");
    String param_unit = request.getParameter("interval_unit");
    if (param_interval == null || param_interval.trim().length() == 0) {
        param_interval = "5";
        param_unit = "minutes";
    } else {
        param_interval = param_interval.trim();
    }
    String unit_sec_selected = param_unit.equals("seconds") ? "selected" : "";
    String unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";

    String error = null;
    if (act != null && !act.equals("wipe")) {
        try {
            DataSource ds = null;
            Map<String, Object> dsAttrs = new HashMap<String, Object>();
            if (act.equals("new") || act.equals("modify")) {
                if (param_service.length() == 0) {
                    error = "Service name must not be empty";
                } else if (param_username.length() == 0) {
                    error = "User name must not be empty";
                } else if (param_password.length() == 0) {
                    error = "Password must not be empty";
                } else if (param_host.length() == 0) {
                    error = "Server host must be a valid hostname or IP address";
                } else if (param_port.length() == 0) {
                    error = "Server port must be a valid port number";
                } else {
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, "TRUE");
                    dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, param_username);
                    
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEmailAddress, param_email);
                    dsAttrs.put(Provisioning.A_zimbraPrefFromDisplay, param_from_display);
                    dsAttrs.put(Provisioning.A_zimbraPrefReplyToAddress, param_replyto);
                    dsAttrs.put(Provisioning.A_zimbraPrefReplyToDisplay, param_replyto_display);
    
                    dsAttrs.put(Provisioning.A_zimbraDataSourceHost, param_host);
                    dsAttrs.put(Provisioning.A_zimbraDataSourcePort, param_port);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, connType.toString());

                    dsAttrs.put(A_zimbraDataSourceSmtpHost, param_smtp_host);
                    dsAttrs.put(A_zimbraDataSourceSmtpPort, param_smtp_port);
                    dsAttrs.put(A_zimbraDataSourceSmtpConnectionType, smtpConnType.toString());
                    dsAttrs.put(A_zimbraDataSourceSmtpAuthRequired, param_smtp_auth);
                    dsAttrs.put(A_zimbraDataSourceSmtpAuthUsername, param_smtp_user);

                    dsAttrs.put(A_zimbraDataSourceSyncInterval, formatSyncInterval(param_interval, param_unit));

                    dsAttrs.put(A_zimbraDataSourceUseProxy, param_use_proxy);
                    dsAttrs.put(A_zimbraDataSourceProxyHost, param_proxy_host);
                    dsAttrs.put(A_zimbraDataSourceProxyPort, param_proxy_port);

                    if (dsType == DataSource.Type.pop3) {
                        dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, "TRUE");
                        
                        if (param_pop_folder.equals("inbox"))
                            dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, Integer.toString(Mailbox.ID_FOLDER_INBOX));
                    }

                    if (!param_password.equals("********")) {
                        dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, param_password);
                    }
                    if (!param_smtp_pass.equals("********")) {
                        dsAttrs.put(A_zimbraDataSourceSmtpAuthPassword, param_smtp_pass);
                    }
                }
            }

            if (error == null) {
                if (act.equals("smtp")) {
	                if (param_smtp_host.length() == 0) {
	                    error = "SMTP host must be a valid hostname or IP address";
                    } else if (param_smtp_port.length() == 0) {
                        error = "SMTP port must be a valid port number";
                    } else if (param_smtp_auth.equalsIgnoreCase("TRUE") &&
                                (param_smtp_user.length() == 0 || param_smtp_pass.length() == 0)) {
                        error = "User name and password must not be empty if SMTP auth required";
                    } else {
                        Map<String, Object> attrs = new HashMap<String, Object>();
                        attrs.put(A_zimbraDataSourceSmtpHost, param_smtp_host);
                        attrs.put(A_zimbraDataSourceSmtpPort, param_smtp_port);
                        attrs.put(A_zimbraDataSourceSmtpConnectionType, smtpConnType.toString());
                        attrs.put(A_zimbraDataSourceSmtpAuthRequired, param_smtp_auth);
                        attrs.put(A_zimbraDataSourceSmtpAuthUsername, param_smtp_user);
                        attrs.put(A_zimbraDataSourceSmtpAuthPassword, param_smtp_pass);
                        prov.modifyAttrs(localAccount, attrs, true);
                    }
                } else if (act.equals("new")) {
                    prov.createDataSource(localAccount, dsType, param_service, dsAttrs);
                } else {
                    for (int i = 0; i < dataSources.size(); ++i) {
                        ds = dataSources.get(i);
                        if (ds.getName().equals(param_service)) {
                            break;
                        }
                    }
                    if (ds == null) {
                        error = "Service not found";
                    } else {
                        if (act.equals("modify")) {
                            prov.modifyDataSource(localAccount, ds.getId(), dsAttrs);
                        } else if (act.equals("delete")) {
                            prov.deleteDataSource(localAccount, ds.getId());
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

    dataSources = prov.getAllDataSources(localAccount);
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

<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/util/AjxTimedAction.js"></script>
<script type="text/javascript">

<%--<%--%>
//dataSources = prov.getAllDataSources(localAccount);
//if (dataSources != null && dataSources.size() > 0) {
<%--%>--%>

function InitScreen() {
<% if (error != null) { %>
<% if (act.equals("new")) { %>
    showNewScreen();
<% } else if (act.equals("modify")) { %>
    showChangeScreen();
<% } else if (act.equals("smtp")) { %>
    showSmtpScreen();
<% } else if (act.equals("delete") || act.equals("wipe")) { %>
    showManage();
<% } %>
<% } else { %>
<% if (act == null) { %>
    showManage();
<% } else if (act.equals("new")) { %>
    showCreated();
<% } else if (act.equals("modify")) { %>
    showModified();
<% } else if (act.equals("smtp")) { %>
    showSmtpSaved();    
<% } else if (act.equals("delete") || act.equals("wipe")) { %>
    showDeleted();
<% } %>
<% } %>
}

function showManage() {
    byId('manageServices').style.display = 'block';
}

function showCreated() {
    byId('serviceCreated').style.display = 'block';
}

function hideCreated() {
    byId('serviceCreated').style.display = 'none';
}

function showModified() {
    byId('serviceModified').style.display = 'block';
}

function hideModified() {
    byId('serviceModified').style.display = 'none';
}

function showSmtpSaved() {
    byId('smtpSaved').style.display = 'block';
}

function hideSmtpSaved() {
    byId('smtpSaved').style.display = 'none';
}

function showDeleted() {
    byId('serviceDeleted').style.display = 'block';
}

function hideDeleted() {
    byId('serviceDeleted').style.display = 'none';
}

function showNewScreen() {
    byId('manageServices').style.display = 'none';
    byId('newService').style.display = 'block';
}

function backFromNew() {
    byId('manageServices').style.display = 'block';
    byId('newService').style.display = 'none';
}

function showChangeScreen(i) {
    byId('manageServices').style.display = 'none';
    byId('changeService_' + i).style.display = 'block';
}

function backFromChange(i) {
    byId('manageServices').style.display = 'block';
    byId('changeService_' + i).style.display = 'none';
}

function showSmtpScreen() {
    byId('manageServices').style.display = 'none';
    byId('smtp').style.display = 'block';
}

function backFromSmtp() {
    byId('manageServices').style.display = 'block';
    byId('smtp').style.display = 'none';
}

function zdsetup() {
    window.location = "<%=ZDSETUP_URL%>"
}

function OnNew() {
    new_service.submit();
}

function OnModify(f) {
    f.submit();
}

function OnSmtp() {
    smtp_form.submit();
}

function OnDelete(service) {
    if (confirm('Service "' + service + '" information will be deleted. Data already downloaded as well as data on the server will not be affected. OK to proceed?')) {
        hidden_form.act.value = "delete";
        hidden_form.service.value = service;
        hidden_form.submit();
    }
}

function OnWipe() {
    if (confirm('All POP/IMAP settings and downloaded data will be purged from local disk. This might take a long time depending on the mailbox size. OK to proceed?')) {
        hidden_form.act.value = "wipe"
        hidden_form.submit();
    }
}

function byId(id) {
    return document.getElementById(id);
}


</script>
</head>
<body onload="InitScreen()">

<form name="hidden_form" action="<%=LOCALHOST_THIS_URL%>" method="POST">
    <input type="hidden" name="act">
    <input type="hidden" name="service">
</form>


<div id="manageServices" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Services</div>


    <% if (error != null) { %>
        <p><font color="red"><%= error %></font></p>
    <% } else { %>
        <p>&nbsp;</p>
    <% } %>


    <%
        if (dataSources != null && dataSources.size() > 0) {
            for (int i = 0; i < dataSources.size(); ++i) {
                DataSource ds = dataSources.get(i);
                String service = ds.getName();
                String username = ds.getUsername();
    %>
    <div class="ZWizardPageTitle"><%=service%> --&gt; <%=username%></div>


        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top>
                    <button onclick="showChangeScreen(<%=i%>)" style='width:140px'>
                        <nobr>Change Service Setup</nobr>
                    </button>
                </td>
                <td>Change service setup (password, check mail interval, etc)</td>
            </tr>
            <tr>
                <td valign=top>
                    <button onclick="OnDelete('<%=service%>')" style='width:100%'>
                        <nobr>Delete Service</nobr>
                    </button>
                </td>
                <td>Delete service setup information. Your already downloaded data as well as data on the server will
                    not be affected.
                </td>
            </tr>
        </table>

    <% } %>
    <% } else { %>

    <p>No service has been provisioned</p>

    <% } %>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="zdsetup()">Back</button>
            </td>

            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="OnWipe()">Delete All POP/IMAP Data</button>
            </td>

            <!-- td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="showSmtpScreen()">Config Default SMTP</button>
            </td-->

            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="showNewScreen()">Add New Service</button>
            </td>
    </table>
</div>

<% if (act != null && act.equals("new")) { %>

<div id="serviceCreated" class="ZWizardPage">
    <div class="ZWizardPageTitle">Service Created</div>

    <p>Your mail service "<%= param_service %>" has been successfully set up.
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
                <button onclick="hideCreated();showManage()">OK</button>
            </td>
    </table>
</div>

<% } %>


<% if (act != null && act.equals("modify")) { %>

<div id="serviceModified" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Service</div>

    <p>Changes to service "<%=param_service%>" has been saved.</p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="hideModified();showManage()">OK</button>
            </td>
    </table>
</div>

<% } %>

<% if (act != null && act.equals("smtp")) { %>

<div id="smtpSaved" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Service</div>

    <p>Default SMTP settings have been saved.</p>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="hideSmtpSaved();showManage()">OK</button>
            </td>
    </table>
</div>

<% } %>

<% if (act != null && (act.equals("delete") || act.equals("wipe"))) { %>

<div id="serviceDeleted" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Service</div>

    <% if (act.equals("delete")) { %>
    <p>Service "<%=param_service%>" has been deleted.</p>
    <% } else { %>
    <p>All POP/IMAP settings and data have been deleted.</p>
    <% } %>
    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button onclick="hideDeleted();showManage()">OK</button>
            </td>
    </table>
</div>

<% } %>

<%
    if (dataSources != null && dataSources.size() > 0) {
        for (int i = 0; i < dataSources.size(); ++i) {
            DataSource ds = dataSources.get(i);
            String service = ds.getName();
            String username = ds.getUsername();

            String email = ds.getEmailAddress();
            email = email == null ? "" : email;
            String fromDisplay = ds.getFromDisplay();
            fromDisplay = fromDisplay == null ? "" : fromDisplay;
            String replyto = ds.getReplyToAddress();
            replyto = replyto == null ? "" : replyto;
            String replytoDisplay = ds.getReplyToDisplay();
            replytoDisplay = replytoDisplay == null ? "" : replytoDisplay;

            String serverHost = ds.getHost();
            int serverPort = ds.getPort();

            connType = ds.getConnectionType();
            String sslChecked = connType == DataSource.ConnectionType.ssl ? "checked" : "";

            dsType = ds.getType(); //pop3 or imap
            String pop3Checked = dsType == DataSource.Type.pop3 ? "checked" : "";
            String imapChecked = dsType == DataSource.Type.imap ? "checked" : "";

            String popToInboxChecked = null;
            String popToNewChecked = null;
            int folderId = ds.getFolderId();
            if (folderId == Mailbox.ID_FOLDER_INBOX) {
                popToInboxChecked = "checked";
                popToNewChecked = "";
            } else {
                popToInboxChecked = "";
                popToNewChecked = "checked";
            }

            String smtpHost = ds.getAttr(A_zimbraDataSourceSmtpHost);
            smtpHost = smtpHost == null ? "" : smtpHost;
            String smtpPort = ds.getAttr(A_zimbraDataSourceSmtpPort);
            smtpPort = smtpPort == null ? "" : smtpPort;
            String smtpConnTypeStr = ds.getAttr(A_zimbraDataSourceSmtpConnectionType);
            smtpConnType = smtpConnTypeStr == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.valueOf(smtpConnTypeStr);
            String smtpSslChecked = smtpConnType == DataSource.ConnectionType.ssl ? "checked" : "";
            String smtpAuth = ds.getAttr(A_zimbraDataSourceSmtpAuthRequired);
            String smtpAuthChecked = smtpAuth != null && smtpAuth.equalsIgnoreCase("true") ? "checked" : "";
            String smtpUser = ds.getAttr(A_zimbraDataSourceSmtpAuthUsername);
            smtpUser = smtpUser == null ? "" : smtpUser;
            String smtpPass = ds.getAttr(A_zimbraDataSourceSmtpAuthPassword);
            if (smtpPass != null && smtpPass.length() > 0)
                smtpPass = "********";

            String useProxy = ds.getAttr(A_zimbraDataSourceUseProxy);
            String useProxyChecked = useProxy != null && useProxy.equalsIgnoreCase("true") ? "checked" : "";
            
            String proxyHost = ds.getAttr(A_zimbraDataSourceProxyHost);
            proxyHost = proxyHost == null ? "" : proxyHost;
            String proxyPort = ds.getAttr(A_zimbraDataSourceProxyPort);
            proxyPort = proxyPort == null ? "" : proxyPort;

            String interval = ds.getAttr(A_zimbraDataSourceSyncInterval);
            if (interval == null || interval.length() == 0) {
                interval = "5m";
            }
            unit_sec_selected = interval.endsWith("s") ? "selected" : "";
            unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";
            interval = interval.substring(0, interval.length() - 1);
%>

<div id="changeService_<%=i%>" class="ZWizardPage">
<div class="ZWizardPageTitle">Change Service Settings</div>

<% if (error != null) { %>
<p><font color="red"><%= error %>
</font></p>
<% } else if (act != null && act.equals("modify")) { %>
<p><font color="blue">Service settings have been updated.</font></p>
<% } else { %>
<p>What do you want to change?</p>
<% } %>


<form name="update_account_<%=i%>" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="modify">
    <input type="hidden" name="service" value="<%=service%>">
    <input type="hidden" name="username" value="<%=username%>">
    <input type="hidden" name="protocol_name" value="<%=dsType.toString()%>">
    <input type="hidden" name="pop_folder" value="<%=folderId == Mailbox.ID_FOLDER_INBOX ? "inbox" : "new"%>">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Service name:</td>
            <td><input style='width:200px' class="ZField" type="text" value="<%=service%>" disabled></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">User name:</td>
            <td><input style='width:200px' class="ZField" type="text" value="<%=username%>" disabled></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password"
                       value="********"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email"
                       value="<%=email%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Display name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="from_display" name="from_display"
                       value="<%=fromDisplay%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">ReplyTo address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="replyto" name="replyto"
                       value="<%=replyto%>"></td>
        </tr>        
        <tr>
            <td class="ZFieldLabel">ReplyTo display:</td>
            <td><input style='width:200px' class="ZField" type="text" id="replyto_display" name="replyto_display"
                       value="<%=replytoDisplay%>"></td>
        </tr>
        
        <tr>
            <td class="ZFieldLabel">Server host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="mod_server_host" name="server_host"
                       value="<%=serverHost%>"> <font color="gray">(e.g. mail.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="mod_server_port" name="server_port"
                       value="<%=serverPort%>"> <font color="gray">(e.g. 80)</font> &nbsp;&nbsp;<a href="#"
                                                                                                   onclick="editPort();">Edit</a>
            </td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="mod_server_secure" name="server_secure" <%=sslChecked%>></td>
        </tr>
        <tr>
            <td><input type=radio id='mod_protocol_pop' value="pop3" <%=pop3Checked%> disabled>
            </td>
            <td><label class="ZRadioLabel" for='mod_protocol_pop'>POP3</label></td>
        </tr>
        <tr>
            <td><input type=radio id='mod_protocol_imap' value="imap" <%=imapChecked%> disabled>
            </td>
            <td><label class="ZRadioLabel" for='mod_protocol_imap'>IMAP4</label></td>
        </tr>
        
        <tr>
            <td class="ZFieldLable">Leave on server (only applicable to POP):</td>
            <td><input class="ZField" type="checkbox" id="leave_on_server" name="leave_on_server" checked disabled></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>(forced during beta)</label></td>
        </tr>
        
        <tr>
            <td><input type=radio id='pop_folder_inbox' value="inbox" <%=popToInboxChecked%> disabled></td>
            <td><label class="ZRadioLabel" for='pop_folder_inbox'>POP to Inbox</label></td>
        </tr>
        <tr>
            <td><input type=radio id='pop_folder_new' value="new" <%=popToNewChecked%> disabled></td>
            <td><label class="ZRadioLabel" for='pop_folder_new'>Create New Folder</label></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="mod_smtp_host" name="smtp_host"
                       value="<%=smtpHost%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="mod_smtp_port" name="smtp_port"
                       value="<%=smtpPort%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="mod_smtp_secure" name="smtp_secure" <%=smtpSslChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP Authentication Required:</td>
            <td><input class="ZField" type="checkbox" id="mod_smtp_auth" name="smtp_auth" <%=smtpAuthChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user"
                       value="<%=smtpUser%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="smtp_user" name="smtp_pass"
                       value="<%=smtpPass%>"></td>
        </tr>

        <tr>
            <td class="ZFieldLable">Use SOCKS proxy:</td>
            <td><input class="ZField" type="checkbox" id="mod_use_proxy" name="use_proxy" <%=useProxyChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SOCKS proxy host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="mod_proxy_host" name="proxy_host"
                       value="<%=proxyHost%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SOCKS proxy port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="mod_proxy_port" name="proxy_port"
                       value="<%=proxyPort%>"> <font color="gray">(e.g. 8888)</font></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Check mail every:</td>
            <td><input style='width:50px' class="ZField" type="text" id="syncQuantity" name="sync_interval"
                       value="<%=interval%>">
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
            <button onclick="backFromChange(<%=i%>)">Back</button>
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
        param_service = "";
        param_username = "";
        param_password = "";

        param_host = "";
        param_port = "";
        param_protocol = "";
        param_secure = "";

        param_smtp_host = "";
        param_smtp_port = "";
        param_smtp_secure = "";
        param_smtp_auth = "";

        param_proxy_host = "";
        param_proxy_port = "";

        param_interval = "5";
        unit_sec_selected = "";
        unit_min_selected = "selected";
    }
%>


<div id="newService" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Mail Service Setup
</div>

<% if (error == null) { %>
<p>To establish a connection to a mail service and
    to verify that your access, enter
    your service account username (could be same as your email address), password, the server's address, protocol, and
    connection information.
    Configure how often to synchronize with the server in either minutes or seconds.
</p>
<% } else { %>
<p><font color="red"><%= error %>
</font></p>
<% } %>

<form name="new_service" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="new">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Service name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="service" name="service"
                       value="<%=param_service%>"> <font color="gray">(e.g. My ISP)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">User name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="username" name="username"
                       value="<%=param_username%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password"
                       value="<%=param_password%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email"
                       value="<%=param_email%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Display name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="from_display" name="from_display"
                       value="<%=param_from_display%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">ReplyTo address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="replyto" name="replyto"
                       value="<%=param_replyto%>"></td>
        </tr>        
        <tr>
            <td class="ZFieldLabel">ReplyTo display:</td>
            <td><input style='width:200px' class="ZField" type="text" id="replyto_display" name="replyto_display"
                       value="<%=param_replyto_display%>"></td>
        </tr>
        
        <tr>
            <td class="ZFieldLabel">Server host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="server_host" name="server_host"
                       value="<%=param_host%>"> <font color="gray">(e.g. mail.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="server_port" name="server_port"
                       value="<%=param_port%>"> <font color="gray">(e.g. 110)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="server_secure" name="server_secure" <%=ssl_checked%>></td>
        </tr>
        <tr>
            <td><input type=radio id='protocol_pop' name="protocol_name" value="pop3" <%=pop3_checked%>></td>
            <td><label class="ZRadioLabel" for='protocol_pop'>POP3</label></td>
        </tr>
        <tr>
            <td><input type=radio id='protocol_imap' name="protocol_name" value="imap" <%=imap_checked%>></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>IMAP4</label></td>
        </tr>
        
        <tr>
            <td class="ZFieldLable">Leave on server (only applicable to POP):</td>
            <td><input class="ZField" type="checkbox" id="leave_on_server" name="leave_on_server" checked disabled></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>(forced during beta)</label></td>
        </tr>
        
        <tr>
            <td><input type=radio id='pop_folder_inbox' name="pop_folder" value="inbox" <%=pop_to_inbox_checked%>></td>
            <td><label class="ZRadioLabel" for='pop_folder_inbox'>POP to Inbox</label></td>
        </tr>
        <tr>
            <td><input type=radio id='pop_folder_new' name="pop_folder" value="new" <%=pop_to_new_checked%>></td>
            <td><label class="ZRadioLabel" for='pop_folder_new'>Create New Folder</label></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host"
                       value="<%=param_smtp_host%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="smtp_port" name="smtp_port"
                       value="<%=param_smtp_port%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtp_ssl_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP Authentication Required:</td>
            <td><input class="ZField" type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtp_auth_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user"
                       value="<%=param_smtp_user%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="smtp_user" name="smtp_pass"
                       value="<%=param_smtp_pass%>"></td>
        </tr>


        <tr>
            <td class="ZFieldLable">Use SOCKS proxy:</td>
            <td><input class="ZField" type="checkbox" id="use_proxy" name="use_proxy" <%=use_proxy_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SOCKS proxy host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxy_host" name="proxy_host"
                       value="<%=param_proxy_host%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SOCKS proxy port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="proxy_port" name="proxy_port"
                       value="<%=param_proxy_port%>"> <font color="gray">(e.g. 8888)</font></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Synchronize every:</td>
            <td><input style='width:50px' class="ZField" type="text" id="sync_interval" name="sync_interval"
                       value=<%=param_interval%>>
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
            <button onclick="backFromNew()">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnNew()">Test</button>
        </td>
</table>
</div>

<%
    if (error == null || act == null || !act.equals("smtp")) {
        param_smtp_host = localAccount.getAttr(A_zimbraDataSourceSmtpHost);
		param_smtp_host = param_smtp_host == null ? "" : param_smtp_host;
        param_smtp_port = localAccount.getAttr(A_zimbraDataSourceSmtpPort);
        param_smtp_port = param_smtp_port == null ? "" : param_smtp_port;
        param_smtp_user = localAccount.getAttr(A_zimbraDataSourceSmtpAuthUsername);
        param_smtp_user = param_smtp_user == null ? "" : param_smtp_user;
        param_smtp_pass = localAccount.getAttr(A_zimbraDataSourceSmtpAuthPassword);
        param_smtp_pass = param_smtp_pass == null ? "" : param_smtp_pass;        
               
		String smtpConnTypeStr = localAccount.getAttr(A_zimbraDataSourceSmtpConnectionType);
		smtpConnType = smtpConnTypeStr == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.valueOf(smtpConnTypeStr);
		smtp_ssl_checked = smtpConnType == DataSource.ConnectionType.ssl ? "checked" : "";
		String smtpAuth = localAccount.getAttr(A_zimbraDataSourceSmtpAuthRequired);
		smtp_auth_checked = smtpAuth != null && smtpAuth.equalsIgnoreCase("true") ? "checked" : "";
    }
%>


<div id="smtp" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Default SMTP Settings
</div>

<% if (error != null) { %>
<p><font color="red"><%= error %></font></p>
<% } %>

<p>Configure the SMTP server for sending outgoing messages</p>

<form name="smtp_form" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="smtp">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host"
                       value="<%=param_smtp_host%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="smtp_port" name="smtp_port"
                       value="<%=param_smtp_port%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use secure connection:</td>
            <td><input class="ZField" type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtp_ssl_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP authentication required:</td>
            <td><input class="ZField" type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtp_auth_checked%>></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user"
                       value="<%=param_smtp_user%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="smtp_user" name="smtp_pass"
                       value="<%=param_smtp_pass%>"></td>
        </tr>
    </table>

</form>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button onclick="backFromSmtp()">Back</button>
        </td>
        <td class="ZWizardButton">
            <button onclick="OnSmtp()">Save</button>
        </td>
</table>
</div>

</body>
</html>
