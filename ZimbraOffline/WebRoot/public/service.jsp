
<%@page import="java.util.HashMap"%>
<%@page import="com.zimbra.cs.account.Provisioning.AccountBy"%>
<%@page import="com.zimbra.cs.mailbox.Folder"%>
<%@page import="com.zimbra.cs.account.Provisioning.DataSourceBy"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.account.DataSource" %>
<%@ page import="com.zimbra.cs.mailbox.Mailbox" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>

<%!
    private final String LOCALHOST_URL = "http://localhost:7633";
    private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
    private final String LOCALHOST_LOGIN_URL = "/public/loginlocal.jsp";
    private final String LOCALHOST_LOGIN_DEV_URL = "/public/loginlocal.jsp?dev=1";
    private final String LOCALHOST_THIS_URL = LOCALHOST_URL + "/zimbra/public/service.jsp";
    private final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";

    private final String OFFLINE_SYNC_INTERVAL = "offlineSyncInterval";

    private final String OFFLINE_PROXY_HOST = "offlineProxyHost";
    private final String OFFLINE_PROXY_PORT = "offlineProxyPort";
    private final String OFFLINE_PROXY_USER = "offlineProxyUser";
    private final String OFFLINE_PROXY_PASS = "offlineProxyPass";
    
    private final String A_zimbraDataSourceSmtpHost = "zimbraDataSourceSmtpHost";
    private final String A_zimbraDataSourceSmtpPort = "zimbraDataSourceSmtpPort";
    private final String A_zimbraDataSourceSmtpConnectionType = "zimbraDataSourceSmtpConnectionType";
    private final String A_zimbraDataSourceSmtpAuthRequired = "zimbraDataSourceSmtpAuthRequired";

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
    
    String skin = "sand";
%>

<%
    SoapProvisioning prov = new SoapProvisioning();
    prov.soapSetURI(LOCALHOST_ADMIN_URL);
    prov.soapZimbraAdminAuthenticate();
    
    final String LOCAL_ACCOUNT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    Account localAccount = prov.get(Provisioning.AccountBy.id, LOCAL_ACCOUNT_ID);
    List<DataSource> dataSources = null;
    String login = null;
    login = localAccount.getName();
    dataSources = prov.getAllDataSources(localAccount);

    String act = request.getParameter("act");

    String param_service = request.getParameter("service");
    param_service = param_service == null ? "" : param_service.trim();
    String param_username = request.getParameter("username");
    param_username = param_username == null ? "" : param_username.trim();
    String param_password = request.getParameter("password");
    param_password = param_password == null ? "" : param_password.trim();

    String param_host = request.getParameter("server_host");
    param_host = param_host == null ? "" : param_host.trim();
    String param_port = request.getParameter("server_port");
    param_port = param_port == null ? "" : param_port.trim();
    String param_protocol = request.getParameter("protocol_name");
    param_protocol = param_protocol == null ? "" : param_protocol.trim(); //pop3 or imap
    String param_secure = request.getParameter("server_secure");
    DataSource.ConnectionType connType = param_secure == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.ssl;
    String sslChecked = connType == DataSource.ConnectionType.ssl ? "checked" : "";

    String param_smtp_host = request.getParameter("smtp_host");
    param_smtp_host = param_smtp_host == null ? "" : param_smtp_host.trim();
    String param_smtp_port = request.getParameter("smtp_port");
    param_smtp_port = param_smtp_port == null ? "" : param_smtp_port.trim();
    String param_smtp_secure = request.getParameter("smtp_secure");
    DataSource.ConnectionType smtpConnType = param_smtp_secure == null ? DataSource.ConnectionType.cleartext : DataSource.ConnectionType.ssl;
    String smtpSslChecked = smtpConnType == DataSource.ConnectionType.ssl ? "checked" : "";
    String param_smtp_auth = request.getParameter("smtp_auth");
    String smtpAuthChecked = param_smtp_auth == null ? "" : "checked";
    param_smtp_auth = param_smtp_auth == null ? "false" : "true";

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

    String isDev = (String) request.getParameter("dev");

    String error = null;
    if (act != null) {
        try {
            if (act.equals("new")) {
                if (param_service.length() == 0) {
                    error = "Service name must not be empty";
                } else if (param_username.length() ==  0) {
                    error = "User name must not be empty";
                } else if (param_password.length() == 0) {
                    error = "Password must not be empty";
                } else if (param_host.length() == 0) {
                    error = "Server host must be a valid hostname or IP address";
                } else if (param_port.length() == 0) {
                    error = "Server port must be a valid port number";
                } else {
                    Map<String, Object> dsAttrs = new HashMap<String, Object>();
                    
			        dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, Integer.toString(Mailbox.ID_FOLDER_INBOX));
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, "TRUE");
			        dsAttrs.put(Provisioning.A_zimbraDataSourceHost, param_host);
			        dsAttrs.put(Provisioning.A_zimbraDataSourcePort, param_port);
			        dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, connType.toString());
			        dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, param_username);
			        dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, param_password);
                    
                    dsAttrs.put(A_zimbraDataSourceSmtpHost, param_smtp_host);
                    dsAttrs.put(A_zimbraDataSourceSmtpPort, param_smtp_port);
                    dsAttrs.put(A_zimbraDataSourceSmtpConnectionType, smtpConnType.toString());
                    dsAttrs.put(A_zimbraDataSourceSmtpHost, param_smtp_auth);

                    dsAttrs.put(OFFLINE_SYNC_INTERVAL, formatSyncInterval(param_interval, param_unit));

                    dsAttrs.put(OFFLINE_PROXY_HOST, param_proxy_host);
                    dsAttrs.put(OFFLINE_PROXY_PORT, param_proxy_port);
                    dsAttrs.put(OFFLINE_PROXY_USER, param_proxy_user);
                    dsAttrs.put(OFFLINE_PROXY_PASS, param_proxy_pass);

                    DataSource.Type dsType = DataSource.Type.valueOf(param_protocol);
                    if (dsType == DataSource.Type.pop3)
                        dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, "TRUE");
                    
                    prov.createDataSource(localAccount, dsType, param_service, dsAttrs);
                }
            } else {
                DataSource ds = null;
                for (int i = 0; i < dataSources.size(); ++i) {
                    ds = dataSources.get(i);
                    if (ds.getName().equals(param_service))
                        break;
                }
                
                if (ds == null) {
                    error = "Service not found";
                } else {
                    if (act.equals("login")) {
                        if (isDev != null && isDev.equals("1")) {
	                        pageContext.forward(LOCALHOST_LOGIN_DEV_URL);
	                    } else {
	                        pageContext.forward(LOCALHOST_LOGIN_URL);
	                    }
						return;
                    } else if (act.equals("modify")) {
                        Map<String, Object> dsAttrs = new HashMap<String, Object>();
                    
	                    dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, Integer.toString(Mailbox.ID_FOLDER_INBOX));
	                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, "TRUE");
	                    dsAttrs.put(Provisioning.A_zimbraDataSourceHost, param_host);
	                    dsAttrs.put(Provisioning.A_zimbraDataSourcePort, param_port);
	                    dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, connType.toString());
	                    dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, param_username);
	                    dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, param_password);
	                    
	                    dsAttrs.put(A_zimbraDataSourceSmtpHost, param_smtp_host);
	                    dsAttrs.put(A_zimbraDataSourceSmtpPort, param_smtp_port);
	                    dsAttrs.put(A_zimbraDataSourceSmtpConnectionType, smtpConnType.toString());
	                    dsAttrs.put(A_zimbraDataSourceSmtpAuthRequired, param_smtp_auth);
	
	                    dsAttrs.put(OFFLINE_SYNC_INTERVAL, formatSyncInterval(param_interval, param_unit));
	
	                    dsAttrs.put(OFFLINE_PROXY_HOST, param_proxy_host);
	                    dsAttrs.put(OFFLINE_PROXY_PORT, param_proxy_port);
	                    dsAttrs.put(OFFLINE_PROXY_USER, param_proxy_user);
	                    dsAttrs.put(OFFLINE_PROXY_PASS, param_proxy_pass);
	
	                    DataSource.Type dsType = DataSource.Type.valueOf(param_protocol);
	                    if (dsType == DataSource.Type.pop3)
	                        dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, "TRUE");

                        if (!param_password.equals("********")) {
                            dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, param_password);
                        }
                        prov.modifyDataSource(localAccount, ds.getId(), dsAttrs);
                    } else if (act.equals("delete")) {
                        prov.deleteDataSource(localAccount, ds.getId());
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
<title>Zimbra Desktop <%= com.zimbra.common.localconfig.LC.get("zdesktop_version") %></title>
<style type="text/css">
<!--
@import url(<%= LOCALHOST_RESOURCE_URL %>css/imgs,common,dwt,msgview,login,zm,<%= skin %>_imgs,skin.css?debug=1&skin=<%= skin %>);
-->
</style>

<%
%>


<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%= LOCALHOST_RESOURCE_URL %>js/ajax/util/AjxTimedAction.js"></script>
<script type="text/javascript">

<%
if (dataSources != null && dataSources.size() > 0) {
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

    function OnDelete() {
        if (confirm('Service "' + update_account.service.value + '" information will be deleted. Data already downloaded as well as data on the server will not be affected. OK to proceed?')) {
            window.location = "<%=LOCALHOST_THIS_URL%>?act=delete&service=" + update_account.service.value;
        }

    }

    function OnSaveChange() {
        update_account.act.value = "modify";
        update_account.submit();
    }

    function InitScreen() {
        <% if (act == null) { %>
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
		var launchUrl = "http://localhost:7633/zimbra/mail";
		var isDev = "<%= (isDev != null) ? isDev : "" %>";
		if (isDev == "1") {
			launchUrl = "http://localhost:7633/zimbra/mail?dev=1";
		}
        window.location = launchUrl;
    }

    function toggleNotice(id) {
        var it = byId(id);
        it.style.display = (it.style.display == 'block' ? 'none' : 'block');
    }

</script>
</head>
<body onload="InitScreen()">


<%
    if (dataSources != null && dataSources.size() > 0) {
        DataSource ds = dataSources.get(0);

        String service = ds.getName();
        String username = ds.getUsername();
        
        String serverHost = ds.getHost();
        int serverPort = ds.getPort();
        connType = ds.getConnectionType();
        DataSource.Type dsType = ds.getType(); //pop3 or imap
        
        String proxyHost = ds.getAttr(OFFLINE_PROXY_HOST);
        proxyHost = proxyHost == null ? "" : proxyHost;
        String proxyPort = ds.getAttr(OFFLINE_PROXY_PORT);
        proxyPort = proxyPort == null ? "" : proxyPort;
        String proxyUser = ds.getAttr(OFFLINE_PROXY_USER);
        proxyUser = proxyUser == null ? "" : proxyUser;
        String proxyPass = ds.getAttr(OFFLINE_PROXY_PASS);
        proxyPass = proxyPass == null ? "" : proxyPass;

        sslChecked = connType == DataSource.ConnectionType.ssl ? "checked" : "";

        
        String interval = null;
        if (interval == null || interval.length() == 0) {
            interval = "60s";
        }
        unit_sec_selected = interval.endsWith("s") ? "selected" : "";
        unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";
        interval = interval.substring(0, interval.length() - 1);
%>

    <form name="login_form" action="<%=LOCALHOST_THIS_URL%>" method="POST">
        <input type="hidden" name="act" value="login">
        <input type="hidden" name="dev" value="<%=isDev%>">
    </form>

<% if (act != null && act.equals("new")) { %>

    <div id="setupWizard3" class="ZWizardPage">
        <div class="ZWizardPageTitle"><div class='ZWizardPageNumber'>3 of 3</div> Service Setup Confirmed</div>

        <p>Your mail service "<%= param_service %>" has been successfully set up.
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
        <div class="ZWizardPageTitle">Manage Service</div>

        <% if (error != null) { %>
            <p><font color="red"><%= error %></font></p>
        <% } else { %>
            <p>What do you want to do?</p>
        <% } %>

        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top><button onclick="showChangeSettings()" style='width:140px'><nobr>Change Service Setup</nobr></button></td>
                <td>Change service setup (password, check mail interval, etc)</td>
            </tr>
            <tr>
                <td valign=top><button onclick="OnDelete()" style='width:100%'><nobr>Delete Service</nobr></button></td>
                <td>Delete service setup information.  Your already downloaded data as well as data on the server will not be affected.
                </td>
            </tr>
        </table>

        <table class="ZWizardButtonBar"><tr>
            <td class="ZWizardButtonSpacer"><div></div></td>
            <td class="ZWizardButton"><button onclick="OnLogin()">Launch</button></td>
        </table>
    </div>

    <div id="changeSettings" class="ZWizardPage">
        <div class="ZWizardPageTitle">Change Service Settings</div>

        <% if (error != null) { %>
            <p><font color="red"><%= error %></font></p>
        <% } else if (act != null && act.equals("modify")) { %>
            <p><font color="blue">Service settings have been updated.</font></p>
        <% } else { %>
            <p>What do you want to change?</p>
        <% } %>


        <form name="update_account" action="<%=LOCALHOST_THIS_URL%>" method="POST">

        <input type="hidden" name="act">

        <table class="ZWizardForm">
            <tr>
                <td class="ZFieldLabel">Service name:</td>
                <td><input type="hidden" name="service" value="<%=service%>" disabled></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">User name:</td>
                <td><input style='width:200px' class="ZField" type="text" id="username" value="<%=username%>"></td>
            </tr>
	        <tr>
	           <td colspan='2'>
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
	           </td>
	        </tr>
            <tr>
                <td class="ZFieldLabel">Password:</td>
                <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="********"></td>
            </tr>
            
            <tr>
			    <td class="ZFieldLabel">Server host:</td>
			    <td><input style='width:200px' class="ZField" type="text" id="server_host" name="server_host" value="<%=serverHost%>"> <font color="gray">(e.g. mail.company.com)</font></td>
		    </tr>
            <tr>
                <td class="ZFieldLabel">Server port:</td>
                <td><input style='width:50px' class="ZField" type="text" id="server_port" name="server_port" value="<%=serverPort%>"> <font color="gray">(e.g. 80)</font> &nbsp;&nbsp;<a href="#" onclick="editPort();">Edit</a></td>
            </tr>
            <tr>
                <td class="ZFieldLable">Use Secure connection:</td>
                <td><input class="ZField" type="checkbox" id="server_secure" name="server_secure" <%=sslChecked%>></td>
            </tr>
            <tr><td><input type=radio id='protocol_pop' name="protocol_name" value="pop3" <%=!"pop3".equals(param_protocol) ? "checked" : ""%>></td>
                <td><label class="ZRadioLabel" for='protocol_name'>POP3</label></td>
            </tr>
            <tr><td><input type=radio id='protocol_imap' name="protocol_name" value="imap" <%="imap".equals(param_protocol) ? "checked" : ""%>></td>
                <td><label class="ZRadioLabel" for='protocol_name'>IMAP4</label></td>
            </tr>

            <tr>
                <td class="ZFieldLabel">Proxy host:</td>
                <td><input style='width:200px' class="ZField" type="text" id="proxy_host" name="proxy_host" value="<%=proxyHost%>"> <font color="gray">(e.g. proxy.company.com)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy port:</td>
                <td><input style='width:50px' class="ZField" type="text" id="proxy_port" name="proxy_port" value="<%=proxyPort%>"> <font color="gray">(e.g. 8888)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy username:</td>
                <td><input style='width:200px' class="ZField" type="text" id="proxy_user" name="proxy_user" value="<%=proxyUser%>"> <font color="gray">(if proxy requires authentication)</font></td>
            </tr>
            <tr>
                <td class="ZFieldLabel">Proxy password:</td>
                <td><input style='width:200px' class="ZField" type="text" id="proxy_pass" name="proxy_pass" value="<%=proxyPass%>"> <font color="gray">(if proxy requires authentication)</font></td>
            </tr>

	        <tr>
	            <td class="ZFieldLabel">SMTP host:</td>
	            <td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host" value="<%=param_smtp_host%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
	        </tr>
	        <tr>
	            <td class="ZFieldLabel">SMTP port:</td>
	            <td><input style='width:50px' class="ZField" type="text" id="smtp_port" name="smtp_port" value="<%=param_smtp_port%>"> <font color="gray">(e.g. 25)</font></td>
	        </tr>
	        <tr>
	            <td class="ZFieldLable">Use Secure connection:</td>
	            <td><input class="ZField" type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtpSslChecked%>></td>
	        </tr>
	        <tr>
	            <td class="ZFieldLable">SMTP Authentication Required:</td>
	            <td><input class="ZField" type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtpAuthChecked%>></td>
	        </tr>
            
            <tr>
                <td class="ZFieldLabel">Check mail every:</td>
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
	<div class="ZWizardPageTitle">Manage Service</div>

    <p>Service "<%=param_service%>" has been deleted.</p>

	<table class="ZWizardButtonBar"><tr>
		<td class="ZWizardButtonSpacer"><div></div></td>
		<td class="ZWizardButton"><button onclick="showSetupWizardStart()">OK</button></td>
	</table>
</div>

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
        param_proxy_user = "";
        param_proxy_pass = "";
        
        param_interval = "60";        
    }
%>

<div id="setupWizard1" class='ZWizardPage' style='display:block'>
	<div class='ZWizardPageTitle'><div class='ZWizardPageNumber'>1 of 3</div> Zimbra Desktop Setup</div>
	<div class='ZWizardHeader'>Welcome to Zimbra Desktop setup wizard</div>

	<p>You will be guided through the steps to set up Zimbra Desktop
		to synchronize your email for use while your computer is disconnected from the Internet.
	</p>

	<p>You must be online to set up your service -- if you are not online now,
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

	<table class="ZWizardButtonBar"><tr>
		<td class="ZWizardButtonSpacer"><div></div></td>
		<td class="ZWizardButton ZDisabled"><button>Back</button></td>
		<td class="ZWizardButton"><button onclick="showZimbraAccountPage()">Next</button></td>
	</table>
</div>



<div id="setupWizard2" class="ZWizardPage">
	<div class="ZWizardPageTitle"><div class='ZWizardPageNumber'>2 of 3</div> Mail Service Setup</div>

    <% if (error == null) { %>
        <p>To establish a connection to a mail service and 
        	to verify that your access, enter 
        	your service account username (could be same as your email address), password, the server's address, protocol, and connection information. 
			Configure how often to synchronize with the server in either minutes or seconds. 
		</p>
    <% } else { %>
        <p><font color="red"><%= error %></font></p>
    <% } %>

    <form name="new_account" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Service name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="service" name="service" value="<%=param_service%>"> <font color="gray">(e.g. My ISP)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">User name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="username" name="username" value="<%=param_username%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="<%=param_password%>"></td>
        </tr>
        
        <tr>
			<td class="ZFieldLabel">Server host:</td>
			<td><input style='width:200px' class="ZField" type="text" id="server_host" name="server_host" value="<%=param_host%>"> <font color="gray">(e.g. mail.company.com)</font></td>
		</tr>
        <tr>
             <td class="ZFieldLabel">Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="server_port" name="server_port" value="<%=param_port%>"> <font color="gray">(e.g. 110)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="server_secure" name="server_secure" <%=sslChecked%>></td>
        </tr>
        <tr><td><input type=radio id='protocol_pop' name="protocol_name" value="pop3" <%=!"pop3".equals(param_protocol) ? "checked" : ""%>></td>
            <td><label class="ZRadioLabel" for='protocol_name'>POP3</label></td>
        </tr>
        <tr><td><input type=radio id='protocol_imap' name="protocol_name" value="imap" <%="imap".equals(param_protocol) ? "checked" : ""%>></td>
            <td><label class="ZRadioLabel" for='protocol_name'>IMAP4</label></td>
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
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host" value="<%=param_smtp_host%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="smtp_port" name="smtp_port" value="<%=param_smtp_port%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
		<tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtpSslChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP Authentication Required:</td>
            <td><input class="ZField" type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtpAuthChecked%>></td>
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
