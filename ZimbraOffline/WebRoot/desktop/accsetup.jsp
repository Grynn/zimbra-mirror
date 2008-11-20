<%@ page import="com.zimbra.cs.offline.jsp.ZmailBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<c:set var="accountFlavor" value="${param.accountFlavor eq null ? '' : param.accountFlavor}"/>
<c:set var='cancel'><fmt:message key='Cancel'/></c:set>
<c:set var='save'><fmt:message key='Save'/></c:set>
<c:set var="uri" value="/zimbra/desktop/accsetup.jsp"/>

<c:choose>
    <c:when test="${accountFlavor eq 'AOL'}">
        <jsp:useBean id="abean" class="com.zimbra.cs.offline.jsp.AmailBean" scope="request"/>
        <jsp:setProperty name="abean" property="*"/>
        <jsp:setProperty name="abean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(abean)}
        <c:set var="bean" value="${abean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Gmail'}">
        <jsp:useBean id="gbean" class="com.zimbra.cs.offline.jsp.GmailBean" scope="request"/>
        <jsp:setProperty name="gbean" property="*"/>
        <jsp:setProperty name="gbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(gbean)}
        <c:set var="bean" value="${gbean}" scope="request"/>
        <c:set var="help">
            <fmt:message key='GmailNote'>
                <fmt:param><a href="javascript:zd.toggle('helpInfo')"><fmt:message key='GmailMustAllowIMAP'/></a></fmt:param>
            </fmt:message>
        </c:set>
        <c:set var="helpInfo">
            <div><fmt:message key='GmailToAllowIMAP'/></div>
            <ol>
                <li><fmt:message key='GmailLogin'><fmt:param><a href=http://gmail.com target=_blank><fmt:message key='Gmail'/></a></fmt:param></fmt:message>
                <li><fmt:message key='GmailClickTop'><fmt:param><b><fmt:message key='GmailSettingsLink'/></b></fmt:param></fmt:message>
                <li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailFwdPOP'/></b></fmt:param></fmt:message>
                <li><fmt:message key='GmailSelect'><fmt:param><b><fmt:message key='GmailEnableIMAP'/></b></fmt:param></fmt:message>
                <li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailSaveChgs'/></b></fmt:param></fmt:message>
            </ol>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'Imap'}">
        <jsp:useBean id="ibean" class="com.zimbra.cs.offline.jsp.ImapBean" scope="request"/>
        <jsp:setProperty name="ibean" property="*"/>
        <jsp:setProperty name="ibean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(ibean)}
        <c:set var="bean" value="${ibean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Live'}">
        <jsp:useBean id="lbean" class="com.zimbra.cs.offline.jsp.LiveBean" scope="request"/>
        <jsp:setProperty name="lbean" property="*"/>
        <jsp:setProperty name="lbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(lbean)}
        <c:set var="bean" value="${lbean}" scope="request"/>
        <c:set var="help">
	    <fmt:message key='LiveNote'>
                <fmt:param><a href="javascript:zd.toggle('helpInfo')"><fmt:message key='LiveMustAllowClient'/></a></fmt:param>
            </fmt:message>
        </c:set>
        <c:set var="helpInfo">
	    <fmt:message key='LiveLimit'>
	        <fmt:param><a href=http://mail.yahoo.com target=_blank><fmt:message key='YMPLink'/></a></fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'MSE'}">
        <jsp:useBean id="mbean" class="com.zimbra.cs.offline.jsp.MmailBean" scope="request"/>
        <jsp:setProperty name="mbean" property="*"/>
        <jsp:setProperty name="mbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(mbean)}
        <c:set var="bean" value="${mbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'Pop'}">
        <jsp:useBean id="pbean" class="com.zimbra.cs.offline.jsp.PopBean" scope="request"/>
        <jsp:setProperty name="pbean" property="*"/>
        <jsp:setProperty name="pbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(pbean)}
        <c:set var="bean" value="${pbean}" scope="request"/>
    </c:when>
    <c:when test="${accountFlavor eq 'YMP'}">
        <jsp:useBean id="ybean" class="com.zimbra.cs.offline.jsp.YmailBean" scope="request"/>
        <jsp:setProperty name="ybean" property="*"/>
        <jsp:setProperty name="ybean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(ybean)}
        <c:set var="bean" value="${ybean}" scope="request"/>    
        <c:set var="help">
	    <fmt:message key='YMPNote'>
                <fmt:param><a href="javascript:zd.toggle('helpInfo')"><fmt:message key='YMP'/></a></fmt:param>
            </fmt:message>
        </c:set>
        <c:set var="helpInfo">
	    <fmt:message key='YMPToSignup'>
	        <fmt:param><a href=http://mail.yahoo.com target=_blank><fmt:message key='YMPLink'/></a></fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'Zimbra'}">
        <jsp:useBean id="zbean" class="com.zimbra.cs.offline.jsp.ZmailBean" scope="request"/>
        <jsp:setProperty name="zbean" property="*"/>
        <jsp:setProperty name="zbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(zbean)}
        <c:set var="bean" value="${zbean}" scope="request"/>    
        <c:set var="help">
	    <fmt:message key='ToLearnZCS'>
	        <fmt:param><a href="http://www.zimbra.com" target="_blank"><fmt:message key='Zimbra'/></a></fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:otherwise>
    </c:otherwise>
</c:choose>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/desktop/css/offline.css" type="text/css">
<title><fmt:message key="ZimbraDesktop"/></title>

<script type="text/javascript" src="/zimbra/desktop/js/desktop.js"></script>
<script type="text/javascript">
function InitScreen() {}

function accntChange(accnt) {
   document.newAccnt.submit();
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnDelete() {
    if (confirm("${onDeleteWarn}")) {
        document.accountForm.verb.value = "del";
        onSubmit();
    }
}

function OnSubmit() {
    zd.enable("accountName");
    if (document.getElementById("port"))
	zd.enable("port");
    if (document.getElementById("smtpPort"))
	zd.enable("smtpPort");
    zd.hide("cancelButton");
    zd.hide("saveButton");
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
<c:if test="not ${bean.smtpConfigSupported}">
    zd.enable("smtpPassword");
</c:if>
    document.accountForm.submit();
}

function onEditPassword(id) {
    var elem = document.getElementById(id);

    zd.hide("editPasswordRow");
    zd.show("passwordRow");
    elem.value = "";
    elem.focus();
}

function onEditPort(link, id) {
    var elem = document.getElementById(id);

    link.display = "none";
    zd.enable(elem);
    elem.focus();
}
</script>
</head>
<body onload="InitScreen()">
<div align="center">
  <table class="ZPanel" cellpadding="0" cellspacing="0">
    <tr>
      <td class="ZPanelLogo">
        <img src="/zimbra/desktop/img/YahooZimbraLogo.gif" border="0">
      </td>
    </tr>
    <tr>
      <td class="ZPanelInfo">
        <table align="center" cellpadding="4" cellspacing="0">
          <tr>
            <td class="ZPanelTitle" colspan="2">
<c:choose>
<c:when test="${empty bean.accountId}">
                <fmt:message key='AccountAdd'></fmt:message>
</c:when>
<c:otherwise>
                <fmt:message key='AccountChange'><fmt:param><fmt:message key='${accountFlavor}'></fmt:message></fmt:param></fmt:message>
</c:otherwise>
</c:choose>
            </td>
          </tr>
          <tr><td colspan="2"><hr class="ZSeparator"></td></tr>
<c:choose>
<c:when test="${accountFlavor eq ''}">
</c:when>
<c:when test="${not empty bean.error}" >
          <tr>
            <td colspan="2" width="450px">
              <p class='ZOfflineError'>${bean.error}</p>
            </td>
          </tr>
</c:when>
<c:when test="${not bean.allValid}" >
          <tr>
            <td colspan="2" width="450px">
              <p class='ZOfflineError'><fmt:message key='PlsCorrectInput'/></p>
            </td>
          </tr>
</c:when>
</c:choose>
<c:if test="${empty bean.accountId}">
          <tr>
            <td class="ZFieldLabel"><fmt:message key='AccountType'/></td>
            <td>
              <form name="newAccnt" action="" method="POST">
                <select name="accountFlavor" id="accountFlavor" onchange="accntChange(this)" class="ZSelect">
                  <option value=""><fmt:message key='AccountSelect'/></option>
                  <option value="Zimbra" <c:if test="${accountFlavor eq 'Zimbra'}">selected</c:if> ><fmt:message key='Zimbra'/></option>
                  <option value="YMP" <c:if test="${accountFlavor eq 'YMP'}">selected</c:if> ><fmt:message key='YMP'/></option>
                  <option value="Gmail" <c:if test="${accountFlavor eq 'Gmail'}">selected</c:if> ><fmt:message key='Gmail'/></option>
                  <option value="Live" <c:if test="${accountFlavor eq 'Live'}">selected</c:if> ><fmt:message key='Live'/></option>
                  <option value="AOL" <c:if test="${accountFlavor eq 'AOL'}">selected</c:if> ><fmt:message key='AOL'/></option>
                  <option value="MSE" <c:if test="${accountFlavor eq 'MSE'}">selected</c:if> ><fmt:message key='MSE'/></option>
                  <option value="Imap" <c:if test="${accountFlavor eq 'Imap'}">selected</c:if> ><fmt:message key='Imap'/></option>
                  <option value="Pop" <c:if test="${accountFlavor eq 'Pop'}">selected</c:if> ><fmt:message key='Pop'/></option>
                  </select>
                </form>
              </td>
            </tr>
</c:if>
<c:choose>
<c:when test="${accountFlavor eq ''}">
</c:when>
<c:when test="${not bean.noVerb && bean.allOK}">
    <jsp:forward page="console.jsp">
	<jsp:param name="accountName" value="${bean.accountName}"></jsp:param>
	<jsp:param name="verb" value="${bean.verb}"></jsp:param>
    </jsp:forward>
</c:when>
<c:when test="${bean.add || empty bean.accountId}">
    <c:if test="${not empty help}">
            <tr>
	      <td></td>
              <td class="ZAccountHelp">
                ${help}
	<c:if test="${not empty helpInfo}">
                <div id="helpInfo" style="display:none"><br>${helpInfo}</div>
	</c:if>
              </td>
            </tr>
    </c:if>
    <zd:xmailManage accountFlavor="${accountFlavor}" uri="${uri}" verb="add"/>
</c:when>
<c:otherwise>
    ${zdf:reload(bean)}
    <zd:xmailManage accountFlavor="${accountFlavor}" uri="${uri}" verb="mod"/>
</c:otherwise>
</c:choose>
            <tr><td colspan="2"><hr class="ZSeparator"></td></tr>
            <tr>
              <td align="center" colspan="2">
                <table cellpadding="5" cellspacing="0" width="95%">
                  <tr>
<c:if test="${accountFlavor ne ''}">
                    <td id="saveButton" align="left">
                      <zd:button onclick='OnSubmit()' text='${save}'/>
                    </td>
                    <td align="center" width="9%"><span id="whattodo" class="ZOfflineNotice"></span></td>
</c:if>
                    <td id="cancelButton" align="right">
                      <zd:button onclick='OnCancel()' text='${cancel}' primary='false'/>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
        </table>
      </td>
    </tr>
  </table>
</div>
</body>
</html>
