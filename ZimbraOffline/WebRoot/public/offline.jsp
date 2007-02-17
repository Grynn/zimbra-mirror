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

<%
    final String LOCALHOST_URL = "http://localhost:7633";
    final String LOCALHOST_ADMIN_URL = "https://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;

    final String OFFLINE_REMOTE_URL = "offlineRemoteServerUri";
    final String OFFLINE_REMOTE_PASSWORD = "offlineRemotePassword";

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

    String error = null;
    Cookie cookie = null;
    if (act != null) {
        try {
            if (param_account.length() == 0) {
                error = "Account name must be a valid email address";
            } else if (param_password.length() == 0) {
                error = "Password must not be empty";
            } else if (param_url.length() == 0) {
                error = "Remote server URL must be valid";
            } else if (act.equals("new")) {
                Map attrs = new TreeMap();
                attrs.put(OFFLINE_REMOTE_URL, param_url);
                prov.createAccount(param_account, param_password, attrs);
            } else {
                Account account = prov.get(Provisioning.AccountBy.name, param_account);
                if (account == null) {
                    error = "Account not found";
                } else {
                    if (act.equals("login")) {
                        String username = account.getName();
                        String password = account.getAttr(OFFLINE_REMOTE_PASSWORD);

                        ZMailbox.Options options = new ZMailbox.Options(username, Provisioning.AccountBy.name, password, LOCALHOST_URL + ZimbraServlet.USER_SERVICE_URI);
                        options.setNoSession(false);
                        String auth = ZMailbox.getMailbox(options).getAuthToken();
                        cookie = new Cookie("ZM_AUTH_TOKEN", auth);
                        cookie.setPath("/");
                        cookie.setMaxAge(31536000);
                        response.addCookie(cookie);
                        response.sendRedirect("/zimbra/mail");
                    } else if (act.equals("modify")) {
                        Map attrs = new TreeMap();
                        attrs.put(OFFLINE_REMOTE_URL, param_url);
                        if (!param_password.equals("****")) {
                            attrs.put(OFFLINE_REMOTE_PASSWORD, param_password);
                        }
                        prov.modifyAttrs(account, attrs, true);
                    } else if (act.equals("reset")) {
                        prov.deleteMailbox(account.getId());
                        //TODO: need to access again to trigger creation of mailbox and start sync
                    } else if (act.equals("delete")) {
                        prov.deleteMailbox(account.getId());
                        prov.deleteAccount(account.getId());
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

<%--
  Created by IntelliJ IDEA.
  User: jjzhuang
  Date: Jan 17, 2007
  Time: 11:57:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <c:set var="skin" value="${not empty param.skin ? param.skin : 'sand'}"/>
    <!-- skin is ${skin} -->
    <style type="text/css">
       @import url( "<c:url value='/css/common,login,zhtml,${skin},skin.css?skin=${skin}'/>" );
    </style>
    
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title>Zimbra Offline Account Configuration</title>
<script type="text/javascript">

    function OnLogin(f) {
        f.act.value = "login";
        f.submit();
        return true;
    }

    function OnModify(f) {
        f.act.value = "modify";
        f.submit();
        return true;
    }

    function OnReset(f) {
        if (confirm('Local disk content of offline account "' + f.account.value + '" will be deleted. The offline account will resync everything from "' + f.server_url.value + '". OK to proceed?')) {
            f.act.value = "reset"
            f.submit();
        }
        return true;
    }

    function OnDelete(f) {
        if (confirm('Offline account "' + f.account.value + '" and its content will be purged from disk. The corresponding server account on "' + f.server_url.value + '" will not be affected. OK to proceed?')) {
            f.act.value = "delete"
            f.submit();
        }
        return true;
    }

</script>
</head>
<body>

<p><h2>Zimbra Offline Account Configuration</h2></p>


<% if (error == null && act != null) { %>

<font color="blue">
<% if (act.equals("new")) { %>
    <p>Offline account "<%= param_account %>" added.</p>
<% } else if (act.equals("login")) { %>
    <%= cookie.getValue() %>
<% } else if (act.equals("modify")) { %>
    <p>Offline account "<%= param_account %>" updated.</p>
<% } else if (act.equals("reset")) { %>
    <p>Offline account "<%= param_account %>" reset.</p>
<% } else if (act.equals("delete")) { %>
    <p>Offline account "<%= param_account %>" deleted.</p>
<% } %>
</font>

<% } else if (error != null) { %>

    <p><font color="red"><%= error %></font></p>

<% } %>


<%
    List<Account> accounts =  prov.getAllAccounts(null);
    if (accounts.size() > 0) {
    for (int i = 0; i < accounts.size(); ++i) {
        Account acc = accounts.get(i);
%>

    <form name="acc_<%= i %>" action="/zimbra/" method="POST">

        <p><table>
        <tr><th colspan=2 bgcolor="#C0C0C0">Offline Account <%=i+1%></th></tr>
        <tr><td><b>Email</b>:</td><td><input type="text" value="<%= acc.getName() %>" size=30 disabled></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" name="password" value="****" size=30></td></tr>
        <tr><td><b>Zimbra Server URL</b>:</td><td><input type="text" name="server_url" value="<%= acc.getAttr(OFFLINE_REMOTE_URL) %>" size=30></td></tr>

        <input type="hidden" name="account" value="<%= acc.getName() %>">
        <input type="hidden" name="act">

        <tr><td colspan=2>
        <input type="button" size="10" value="Login" onclick="return OnLogin(acc_<%= i %>)">&nbsp;
        <input type="button" size="10" value="Modify" onclick="return OnModify(acc_<%= i %>)">&nbsp;
        <input type="button" size="10" value="Reset" onclick="return OnReset(acc_<%= i %>)">&nbsp;
        <input type="button" size="10" value="Delete" onclick="return OnDelete(acc_<%= i %>)">
        </td></tr>
        </table></p>

    </form>

<% } %>

<p>&nbsp;</p><p>&nbsp;</p>

<% } %>

    <form action="/zimbra/" method="POST">

<%
    if (error == null || act == null || !act.equals("new")) {
        param_account = "";
        param_password = "";
        param_url = "";
    }
%>
        <p><table>
        <tr><th colspan=2 bgcolor="#C0C0C0">Add New Zimbra Account</th></tr>
        <tr><td><b>Email</b>:</td><td><input type="text" name="account" value="<%= param_account %>" size=30></td><td><font color="gray">e.g. john@company.com</font></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" name="password" value="<%= param_password %>" size=30></td><td></td></tr>
        <tr><td><b>Zimbra Server URL</b>:</td><td><input type="text" name="server_url" value="<%= param_url %>" size=30></td><td><font color="gray">e.g. http//mail.company.com</font></td></tr>

        <input type="hidden" name="act" value="new">

        <tr><td colspan=2>
        <input type="submit" value="Add New Account">
        </td></tr>
        </table></p>

    </form>


    <form>
        <p>&nbsp;</p><p><table valign="bottom">
        <tr><th colspan=2 bgcolor="#C0C0C0">Add New Pop Account (Coming soon...)</th></tr>
        <tr><td><b>Email</b>:</td><td><input type="text" size=30 disabled></td><td></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" size=30 disabled></td><td></td></tr>
        <tr><td><b>Pop Server Host</b>:</td><td><input type="text" size=30 disabled></td><td><b>Port</b>: <input type="text" size=10 disabled></td></tr>
       
        <input type="hidden" name="act" value="new">

        <tr><td colspan=2>
        <input type="submit" value="Add New Account" disabled>
        </td></tr>
        </table></p>
    </form>

    <form>
        <p>&nbsp;</p><p><table valign="bottom">
        <tr><th colspan=2 bgcolor="#C0C0C0">Add New IMAP Account (Coming soon...)</th></tr>
        <tr><td><b>Email</b>:</td><td><input type="text" size=30 disabled></td><td></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" size=30 disabled></td><td></td></tr>
        <tr><td><b>IMAP Server Host</b>:</td><td><input type="text" size=30 disabled></td><td><b>Port</b>: <input type="text" size=10 disabled></td></tr>

        <input type="hidden" name="act" value="new">

        <tr><td colspan=2>
        <input type="submit" value="Add New Account" disabled>
        </td></tr>
        </table></p>
    </form>


</body>
</html>