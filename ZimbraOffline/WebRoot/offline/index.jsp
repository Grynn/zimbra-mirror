<%@ page import="java.io.File" %>
<%@ page import="java.io.FileWriter" %>
<%--
  Created by IntelliJ IDEA.
  User: jjzhuang
  Date: Jan 17, 2007
  Time: 11:57:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Zimbra Offline Client Configuration</title></head>
  <body>

  <p>Zimbra Offline Client Configuration</p>


<%
    String server_url = request.getParameter("server_url");
    server_url = server_url == null ? "" : server_url.trim();

    String account = request.getParameter("account");
    account = account == null ? "" : account.trim();

    String password = request.getParameter("password");
    password = password == null ? "" : password.trim();

    boolean done = false;
    if (server_url.length() > 0 && account.length() > 0 && password.length() > 0) {
        File tmp = File.createTempFile("zmprov", ".txt");
        FileWriter fw = new FileWriter(tmp);
        fw.write("ca " + account + " " + password + " offlineRemoteServerUri " + server_url);
        fw.close();

        com.zimbra.cs.account.ProvUtil.main(new String[]{"-f", tmp.getAbsolutePath()});

        tmp.delete();
        done = true;
    }
%>

<% if (done) { %>
    Account "<%= account %>" provisioned.  Click <a href="http://localhost:7070/zimbra/">here</a> to login.
<% } else { %>

  <form action="/service/offline/">

    Remote Server URL: <input type="text" name="server_url" value="<%= server_url %>" size=60><br>
    Account (email): <input type="text" name="account" value="<%= account %>" size=30><br>
    Password: <input type="password" name="password" value="<%= password %>" size=30><br>

    <input type="submit" value="Submit">

  </form>
  
<% } %>

  </body>
</html>