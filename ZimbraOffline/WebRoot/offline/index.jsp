<%@ page import="java.io.File" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.zimbra.cs.account.offline.OfflineProvisioning" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zimbra.cs.mailbox.MailboxManager" %>
<%@ page import="com.zimbra.cs.mailbox.Mailbox" %>
<%@ page import="com.zimbra.cs.zclient.ZMailbox" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.cs.zclient.ZGetInfoResult" %>

<%
    final String LOCALHOST_URL = "http://localhost:7633";

    OfflineProvisioning prov = (OfflineProvisioning)Provisioning.getInstance();

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
                attrs.put(OfflineProvisioning.A_offlineRemoteServerUri, param_url);
                prov.createAccount(param_account, param_password, attrs);
            } else {
                Account account = prov.get(Provisioning.AccountBy.name, param_account);
                if (account == null) {
                    error = "Account not found";
                } else {
                    if (act.equals("login")) {
                        String username = account.getName();
                        String password = account.getAttr(OfflineProvisioning.A_offlineRemotePassword);
                        String serverurl = account.getAttr(OfflineProvisioning.A_offlineRemoteServerUri);

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
                        attrs.put(OfflineProvisioning.A_offlineRemoteServerUri, param_url);
                        if (!param_password.equals("****")) {
                            attrs.put(OfflineProvisioning.A_offlineRemotePassword, param_password);
                        }
                        prov.modifyAttrs(account, attrs, true);
                    } else if (act.equals("reset")) {
                        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(account);
                        mbox.deleteMailbox();
                        //access again to trigger creation of mailbox and start sync
                        mbox = MailboxManager.getInstance().getMailboxByAccount(account);
                    } else if (act.equals("delete")) {
                        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(account);
                        mbox.deleteMailbox();
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
    List<Account> accounts = prov.getAllAccounts();
    if (accounts.size() > 0) {
    for (int i = 0; i < accounts.size(); ++i) {
        Account acc = accounts.get(i);
%>

    <form name="acc_<%= i %>" action="/service/offline/" method="POST">

        <p><table>
        <tr><th colspan=2 bgcolor="#C0C0C0">Offline Account <%=i+1%></th></tr>
        <tr><td><b>User</b>:</td><td><input type="text" value="<%= acc.getName() %>" size=30 disabled></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" name="password" value="****" size=30></td></tr>
        <tr><td><b>Server URL</b>:</td><td><input type="text" name="server_url" value="<%= acc.getAttr(OfflineProvisioning.A_offlineRemoteServerUri) %>" size=30></td></tr>

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


    <form action="/service/offline/" method="POST">

<%
    if (error == null || act == null || !act.equals("new")) {
        param_account = "";
        param_password = "";
        param_url = "";
    }
%>
        <p><table>
        <tr><th colspan=2 bgcolor="#C0C0C0">Add New Offline Account</th></tr>
        <tr><td><b>User</b>:</td><td><input type="text" name="account" value="<%= param_account %>" size=30></td></tr>
        <tr><td><b>Password</b>:</td><td><input type="password" name="password" value="<%= param_password %>" size=30></td></tr>
        <tr><td><b>Server URL</b>:</td><td><input type="text" name="server_url" value="<%= param_url %>" size=30></td></tr>

        <input type="hidden" name="act" value="new">

        <tr><td colspan=2>
        <input type="submit" value="Add New Account">
        </td></tr>
        </table></p>

    </form>

</body>
</html>