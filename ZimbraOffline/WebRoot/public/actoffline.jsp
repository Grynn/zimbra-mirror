<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>

<%!
  private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
%>
<%
  try {
        SoapProvisioning prov = new SoapProvisioning();
        prov.soapSetURI(LOCALHOST_ADMIN_URL);
        prov.soapZimbraAdminAuthenticate();
        
        String act = request.getParameter("act");
        String acnt = request.getParameter("account");
        
        if(act.equals("del") && acnt != null) {
            Account account = prov.get(Provisioning.AccountBy.name, acnt);
            prov.deleteMailbox(account.getId());
            prov.deleteAccount(account.getId());
            out.print("DONE");
        }
        
  } catch (Exception e) {
      out.print("ERROR");  
  }
%>
