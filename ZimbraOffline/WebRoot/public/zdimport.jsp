<%@ page import="java.util.HashMap" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.account.DataSource" %>
<%@ page import="com.zimbra.cs.account.DataSource.ConnectionType" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@page import="com.zimbra.cs.mailbox.Mailbox"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.zimbra.cs.account.Provisioning.DataSourceBy"%>
<%@page import="com.zimbra.cs.offline.jsp.JspProvStub"%>
<%@page import="com.zimbra.cs.offline.jsp.JspConstants"%>
<%@page import="com.zimbra.cs.offline.jsp.JspUtils"%>
<%@page import="com.zimbra.cs.offline.jsp.JspConstants.JspVerb"%>
<%@page import="com.zimbra.cs.offline.common.OfflineConstants"%>

<%
	final String LOCALHOST_THIS_URL = JspConstants.LOCALHOST_URL + "/zimbra/public/zdimport.jsp";
	final String ZDSETUP_URL = "/zimbra/public/zdsetup.jsp";
	    
	String skin = "sand";

    JspConstants.JspVerb verb = null;	
    String error = null;
	JspProvStub stub = JspProvStub.getInstance();
    verb = JspConstants.JspVerb.fromString(JspUtils.getRequestParameter(request, JspConstants.PARAM_VERB, null));
        
    String param_accountid = JspUtils.getRequestParameter(request, JspConstants.PARAM_ACCOUNT_ID, "");
    String param_service = JspUtils.getRequestParameter(request, JspConstants.PARAM_DATASOURCE_NAME, "");
    String param_username = JspUtils.getRequestParameter(request, JspConstants.PARAM_USERNAME, "");
    String param_password = JspUtils.getRequestParameter(request, JspConstants.PARAM_PASSWORD, "");
    
    String param_email = JspUtils.getRequestParameter(request, JspConstants.PARAM_EMAIL, "");
    String param_from_display = JspUtils.getRequestParameter(request, JspConstants.PARAM_FROM_DISPLAY, "");
    String param_replyto = JspUtils.getRequestParameter(request, JspConstants.PARAM_REPLYTO, "");
    String param_replyto_display = JspUtils.getRequestParameter(request, JspConstants.PARAM_REPLYTO_DISPLAY, "");

    String param_host = JspUtils.getRequestParameter(request, JspConstants.PARAM_SERVER_HOST, "");
    String param_port = JspUtils.getRequestParameter(request, JspConstants.PARAM_SERVER_PORT, "");

    String param_protocol = JspUtils.getRequestParameter(request, JspConstants.PARAM_SERVER_PROTOCOL, "imap");
    DataSource.Type dsType = DataSource.Type.valueOf(param_protocol);
    String imap_checked = dsType == DataSource.Type.imap ? JspConstants.CHECKED : "";
    String pop3_checked = dsType == DataSource.Type.pop3 ? JspConstants.CHECKED : "";

    //boolean isPopLeaveOnServer = JspUtils.getRequestParameterAsBoolean(request, JspConstants.PARAM_POP_LEAVE_ON_SERVER);
    //String leave_on_server_checked = isPopLeaveOnServer ? JspConstants.CHECKED : "";

    boolean isServerSsl = JspUtils.getRequestParameterAsBoolean(request, JspConstants.PARAM_SERVER_SSL);
    String ssl_checked = isServerSsl ? JspConstants.CHECKED : "";

    String param_smtp_host = JspUtils.getRequestParameter(request, JspConstants.PARAM_SMTP_HOST, "");
    String param_smtp_port = JspUtils.getRequestParameter(request, JspConstants.PARAM_SMTP_PORT, "");
    
    boolean isSmtpSsl = JspUtils.getRequestParameterAsBoolean(request, JspConstants.PARAM_SMTP_SSL);
    String smtp_ssl_checked = isSmtpSsl ? JspConstants.CHECKED : "";
    
    boolean isSmtpAuthRequired = JspUtils.getRequestParameterAsBoolean(request, JspConstants.PARAM_SMTP_AUTH);
    String smtp_auth_checked = isSmtpAuthRequired ? JspConstants.CHECKED : "";
    
    String param_smtp_user = JspUtils.getRequestParameter(request, JspConstants.PARAM_SMTP_USER, "");
    String param_smtp_pass = JspUtils.getRequestParameter(request, JspConstants.PARAM_SMTP_PASS, "");

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
	    
	if (verb != null) {
	    try {
			DataSource ds = null;
			Map<String, Object> dsAttrs = new HashMap<String, Object>();
			if (verb.isAdd() || verb.isModify()) {
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
			    } else if (param_email.length() == 0) {
                    error = "Email must not be empty";
			    } else {
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, JspConstants.TRUE);
			        dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, param_username);
			        
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEmailAddress, param_email);
			        dsAttrs.put(Provisioning.A_zimbraPrefFromDisplay, param_from_display);
			        dsAttrs.put(Provisioning.A_zimbraPrefReplyToAddress, param_replyto);
			        dsAttrs.put(Provisioning.A_zimbraPrefReplyToDisplay, param_replyto_display);
			
			        dsAttrs.put(Provisioning.A_zimbraDataSourceHost, param_host);
			        dsAttrs.put(Provisioning.A_zimbraDataSourcePort, param_port);
			        dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, (isServerSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
			
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpHost, param_smtp_host);
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpPort, param_smtp_port);
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpConnectionType, (isSmtpSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, isSmtpAuthRequired ? JspConstants.TRUE : JspConstants.FALSE);
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, param_smtp_user);
			
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncInterval, JspUtils.formatSyncInterval(param_interval, param_unit));
			
			        if (dsType == DataSource.Type.pop3) {
			            dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, JspConstants.TRUE);
		                dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, Integer.toString(Mailbox.ID_FOLDER_INBOX));
			        }
			
			        if (!param_password.equals(JspConstants.MASKED_PASSWORD)) {
			            dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, param_password);
			        }
			        if (!param_smtp_pass.equals(JspConstants.MASKED_PASSWORD)) {
			            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, param_smtp_pass);
			        }
			    }
			}
			
			if (error == null) {                
			    if (verb.isAdd()) {
			        stub.createOfflineDataSource(param_service, param_email, dsType, dsAttrs);
			    } else {
			        if (param_accountid.length() == 0) {
			            error = "Account ID missing";
			        } else if (verb.isModify()) {
			            stub.modifyOfflineDataSource(param_accountid, param_service, dsAttrs);
			        } else if (verb.isReset()) {
					    stub.resetOfflineDataSource(param_accountid);
					} else if (verb.isDelete()) {
					    stub.deleteOfflineDataSource(param_accountid);
					} else {
					    error = "Unknown action";
	                }
	            }
			}
        } catch (Throwable t) {
            error = t.getMessage();
        }
	}

	List<DataSource> dataSources = stub.getOfflineDataSources();
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Desktop <%=com.zimbra.common.localconfig.LC.get("zdesktop_version")%></title>
<style type="text/css">
<!--
@import url(<%=JspConstants.LOCALHOST_RESOURCE_URL%>css/imgs,common,dwt,msgview,login,zm,<%=skin%>_imgs,skin.css?debug=1&skin=<%=skin%>);
-->
</style>

<script type="text/javascript" src="<%=JspConstants.LOCALHOST_RESOURCE_URL%>js/ajax/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%=JspConstants.LOCALHOST_RESOURCE_URL%>js/ajax/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%=JspConstants.LOCALHOST_RESOURCE_URL%>js/ajax/util/AjxTimedAction.js"></script>
<script type="text/javascript">

function InitScreen() {
<% if (error != null) { %>
<% if (verb.isAdd()) { %>
    showNewScreen();
<% } else if (verb.isModify()) { %>
    showChangeScreen();
<% } else if (verb.isReset() || verb.isDelete()) { %>
    showManage();
<% } %>
<% } else { %>
<% if (verb == null) { %>
    showManage();
<% } else if (verb.isAdd()) { %>
    showCreated();
<% } else if (verb.isModify()) { %>
    showModified();    
<% } else if (verb.isReset() || verb.isDelete()) { %>
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

function zdsetup() {
    window.location = "<%=ZDSETUP_URL%>"
}

function OnNew() {
    new_service.submit();
}

function OnModify(f) {
    f.submit();
}

function OnReset(accountid, service) {
    if (confirm('All downloaded data will be deleted.  Data on the server will be downloaded again. OK to proceed?')) {
        hidden_form.<%=JspConstants.PARAM_VERB%>.value = "<%=JspVerb.rst%>";
        hidden_form.<%=JspConstants.PARAM_ACCOUNT_ID%>.value = accountid;
        hidden_form.<%=JspConstants.PARAM_DATASOURCE_NAME%>.value = service;
        hidden_form.submit();
    }
}

function OnDelete(accountid, service) {
    if (confirm('Service "' + service + '" information and downloaded data will be deleted.  Data on the server will not be affected. OK to proceed?')) {
        hidden_form.<%=JspConstants.PARAM_VERB%>.value = "<%=JspVerb.del%>";
        hidden_form.<%=JspConstants.PARAM_ACCOUNT_ID%>.value = accountid;
        hidden_form.<%=JspConstants.PARAM_DATASOURCE_NAME%>.value = service;
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
    <input type="hidden" name="<%=JspConstants.PARAM_VERB%>">
    <input type="hidden" name="<%=JspConstants.PARAM_ACCOUNT_ID%>">
    <input type="hidden" name="<%=JspConstants.PARAM_DATASOURCE_NAME%>">
</form>


<div id="manageServices" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Services</div>


    <%
    	if (error != null) {
    %>
        <p><font color="red"><%=error%></font></p>
    <%
    	} else {
    %>
        <p>&nbsp;</p>
    <%
    	}
    %>


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
                    <button onclick="OnReset('<%=ds.getAccountId()%>', '<%=service%>')" style='width:100%'>
                        <nobr>Reset Service</nobr>
                    </button>
                </td>
                <td>Delete all downloaded data.  Any data on the server will be downloaded again.
                </td>
            </tr>
            <tr>
                <td valign=top>
                    <button onclick="OnDelete('<%=ds.getAccountId()%>', '<%=service%>')" style='width:100%'>
                        <nobr>Delete Service</nobr>
                    </button>
                </td>
                <td>Delete service and all downloaded data.  Any data on the server will not be affected.
                </td>
            </tr>
        </table>

    <%
    	}
    %>
    <%
    	} else {
    %>

    <p>No service has been provisioned</p>

    <%
    	}
    %>

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
                <button onclick="showNewScreen()">Add New Service</button>
            </td>
    </table>
</div>

<%
	if (verb != null && verb.isAdd()) {
%>

<div id="serviceCreated" class="ZWizardPage">
    <div class="ZWizardPageTitle">Service Created</div>

    <p>Your mail service "<%=param_service%>" has been successfully set up.
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

<%
	}
%>


<%
	if (verb != null && verb.isModify()) {
%>

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

<%
	}
%>

<%
	if (verb != null && (verb.isReset() || verb.isDelete())) {
%>

<div id="serviceDeleted" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Service</div>

    <%
    	if (verb.isReset()) {
    %>
    <p>Service "<%=param_service%>" has been reset.</p>
    <%
    	} else {
    %>
    <p>Service "<%=param_service%>" have been deleted.</p>
    <%
    	}
    %>
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

<%
	}
%>

<%
	if (dataSources != null && dataSources.size() > 0) {
        for (int i = 0; i < dataSources.size(); ++i) {
            DataSource ds = dataSources.get(i);
            
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

            ConnectionType connType = ds.getConnectionType();
            String sslChecked = connType == DataSource.ConnectionType.ssl ? JspConstants.CHECKED : "";

            dsType = ds.getType(); //pop3 or imap
            String pop3Checked = dsType == DataSource.Type.pop3 ? JspConstants.CHECKED : "";
            String imapChecked = dsType == DataSource.Type.imap ? JspConstants.CHECKED : "";

            String smtpHost = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpHost, "");
            String smtpPort = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpPort, "");
            String smtpConnTypeStr = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpConnectionType);
            ConnectionType smtpConnType = smtpConnTypeStr == null ? ConnectionType.cleartext : ConnectionType.valueOf(smtpConnTypeStr);
            String smtpSslChecked = smtpConnType == DataSource.ConnectionType.ssl ? JspConstants.CHECKED : "";
            
            isSmtpAuthRequired = ds.getBooleanAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, false);
            String smtpAuthChecked = isSmtpAuthRequired ? JspConstants.CHECKED : "";
            String smtpUser = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, "");
            String smtpPass = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, "");
            if (smtpPass != null && smtpPass.length() > 0)
                smtpPass = JspConstants.MASKED_PASSWORD;

            String interval = ds.getAttr(OfflineConstants.A_zimbraDataSourceSyncInterval);
            if (interval == null || interval.length() == 0) {
                interval = "5m";
            }
            unit_sec_selected = interval.endsWith("s") ? "selected" : "";
            unit_min_selected = unit_sec_selected.length() == 0 ? "selected" : "";
            interval = interval.substring(0, interval.length() - 1);
%>

<div id="changeService_<%=i%>" class="ZWizardPage">
<div class="ZWizardPageTitle">Change Service Settings</div>

<%
	if (error != null) {
%>
<p><font color="red"><%=error%>
</font></p>
<%
	} else if (verb != null && verb.isModify()) {
%>
<p><font color="blue">Service settings have been updated.</font></p>
<%
	} else {
%>
<p>What do you want to change?</p>
<%
	}
%>


<form name="update_account_<%=i%>" action="<%=LOCALHOST_THIS_URL%>" method="POST">
    <input type="hidden" name="<%=JspConstants.PARAM_VERB%>" value="<%=JspVerb.mod%>">
    <input type="hidden" name="<%=JspConstants.PARAM_ACCOUNT_ID%>" value="<%=ds.getAccountId()%>">
    <input type="hidden" name="<%=JspConstants.PARAM_DATASOURCE_NAME%>" value="<%=ds.getName()%>">
    <input type="hidden" name="<%=JspConstants.PARAM_USERNAME%>" value="<%=ds.getUsername()%>">
    <input type="hidden" name="<%=JspConstants.PARAM_SERVER_PROTOCOL%>" value="<%=dsType%>">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Service name:</td>
            <td><input style='width:200px' class="ZField" type="text" value="<%=ds.getName()%>" disabled></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">User name:</td>
            <td><input style='width:200px' class="ZField" type="text" value="<%=ds.getUsername()%>" disabled></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:100px' class="ZField" type="password" name="<%=JspConstants.PARAM_PASSWORD%>"
                       value="<%=JspConstants.MASKED_PASSWORD%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_EMAIL%>"
                       value="<%=email%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Display name:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_FROM_DISPLAY%>"
                       value="<%=fromDisplay%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">ReplyTo address:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_REPLYTO%>"
                       value="<%=replyto%>"></td>
        </tr>        
        <tr>
            <td class="ZFieldLabel">ReplyTo display:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_REPLYTO_DISPLAY%>"
                       value="<%=replytoDisplay%>"></td>
        </tr>
        
        <tr>
            <td class="ZFieldLabel">Server host:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SERVER_HOST%>"
                       value="<%=serverHost%>"> <font color="gray">(e.g. mail.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" name="<%=JspConstants.PARAM_SERVER_PORT%>"
                       value="<%=serverPort%>"> <font color="gray">(e.g. 80)</font> &nbsp;&nbsp;<a href="#"
                                                                                                   onclick="editPort();">Edit</a>
            </td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SERVER_SSL%>" <%=sslChecked%>></td>
        </tr>
        <tr>
            <td><input type=radio value="imap" <%=imapChecked%> disabled>
            </td>
            <td><label class="ZRadioLabel" for='mod_protocol_imap'>IMAP4</label></td>
        </tr>
        <tr>
            <td><input type=radio value="pop3" <%=pop3Checked%> disabled>
            </td>
            <td><label class="ZRadioLabel" for='mod_protocol_pop'>POP3</label></td>
        </tr>

        
        <tr>
            <td class="ZFieldLable">Leave on server (only applicable to POP):</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_POP_LEAVE_ON_SERVER%>" checked disabled></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>(forced during beta)</label></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_HOST%>"
                       value="<%=smtpHost%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_PORT%>"
                       value="<%=smtpPort%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SMTP_SSL%>" <%=smtpSslChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP Authentication Required:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SMTP_AUTH%>" <%=smtpAuthChecked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_USER%>"
                       value="<%=smtpUser%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" name="<%=JspConstants.PARAM_SMTP_PASS%>"
                       value="<%=smtpPass%>"></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Check mail every:</td>
            <td><input style='width:50px' class="ZField" type="text" name="sync_interval"
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

<%
	}
%>

<%
	}
%>


<%
	if (error == null || verb == null || !verb.isAdd()) {
        param_service = "";
        param_username = "";
        param_password = "";

        param_host = "";
        param_port = "";
        param_protocol = "";

        param_smtp_host = "";
        param_smtp_port = "";

        param_interval = "5";
        unit_sec_selected = "";
        unit_min_selected = "selected";
    }
%>


<div id="newService" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Mail Service Setup
</div>

<%
	if (error == null) {
%>
<p>To establish a connection to a mail service and
    to verify that your access, enter
    your service account username (could be same as your email address), password, the server's address, protocol, and
    connection information.
    Configure how often to synchronize with the server in either minutes or seconds.
</p>
<%
	} else {
%>
<p><font color="red"><%=error%>
</font></p>
<%
	}
%>

<form name="new_service" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="<%=JspConstants.PARAM_VERB%>" value="<%=JspVerb.add%>">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">Service name:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_DATASOURCE_NAME%>"
                       value="<%=param_service%>"> <font color="gray">(e.g. My ISP)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">User name:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_USERNAME%>"
                       value="<%=param_username%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:100px' class="ZField" type="password" name="<%=JspConstants.PARAM_PASSWORD%>"
                       value="<%=param_password%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_EMAIL%>"
                       value="<%=param_email%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Display name:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_FROM_DISPLAY%>"
                       value="<%=param_from_display%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">ReplyTo address:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_REPLYTO%>"
                       value="<%=param_replyto%>"></td>
        </tr>        
        <tr>
            <td class="ZFieldLabel">ReplyTo display:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_REPLYTO_DISPLAY%>"
                       value="<%=param_replyto_display%>"></td>
        </tr>
        
        <tr>
            <td class="ZFieldLabel">Server host:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SERVER_HOST%>"
                       value="<%=param_host%>"> <font color="gray">(e.g. mail.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Server port:</td>
            <td><input style='width:50px' class="ZField" type="text" name="<%=JspConstants.PARAM_SERVER_PORT%>"
                       value="<%=param_port%>"> <font color="gray">(e.g. 110)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SERVER_SSL%>" <%=ssl_checked%>></td>
        </tr>
        <tr>
            <td><input type=radio name="<%=JspConstants.PARAM_SERVER_PROTOCOL%>" value="imap" <%=imap_checked%>></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>IMAP4</label></td>
        </tr>
        <tr>
            <td><input type=radio name="<%=JspConstants.PARAM_SERVER_PROTOCOL%>" value="pop3" <%=pop3_checked%>></td>
            <td><label class="ZRadioLabel" for='protocol_pop'>POP3</label></td>
        </tr>
        
        <tr>
            <td class="ZFieldLable">Leave on server (only applicable to POP):</td>
            <td><input class="ZField" type="checkbox" name="leave_on_server" checked disabled></td>
            <td><label class="ZRadioLabel" for='protocol_imap'>(forced during beta)</label></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_HOST%>"
                       value="<%=param_smtp_host%>"> <font color="gray">(e.g. smtp.company.com)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_PORT%>"
                       value="<%=param_smtp_port%>"> <font color="gray">(e.g. 25)</font></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use Secure connection:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SMTP_SSL%>" <%=smtp_ssl_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP Authentication Required:</td>
            <td><input class="ZField" type="checkbox" name="<%=JspConstants.PARAM_SMTP_AUTH%>" <%=smtp_auth_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" name="<%=JspConstants.PARAM_SMTP_USER%>"
                       value="<%=param_smtp_user%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" name="<%=JspConstants.PARAM_SMTP_PASS%>"
                       value="<%=param_smtp_pass%>"></td>
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

</body>
</html>
