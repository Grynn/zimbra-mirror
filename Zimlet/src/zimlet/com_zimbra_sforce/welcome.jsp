<%
    Cookie[] cookies = request.getCookies();
    String authToken = "";
    for (Cookie cooky : cookies) {
        if (cooky.getName().equals("ZM_AUTH_TOKEN")) {
            authToken = cooky.getValue();
        }
    }
    out.println("<!-- AuthToken: " + authToken + "-->");
%>

<h1>Salesforce + Zimbra SSO</h1>

<a target="_new" href="<%=request.getParameter("url")%>">Go to Zimbra</a>