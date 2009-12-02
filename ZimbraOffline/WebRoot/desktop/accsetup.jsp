<!--
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="com.zimbra.cs.offline.jsp.ZmailBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<zd:auth/>

<c:set var="accountFlavor" value="${param.accountFlavor eq null ? '' : param.accountFlavor}"/>
<c:set var='cancel'><fmt:message key='Cancel'/></c:set>
<c:set var='save'><fmt:message key='Save'/></c:set>
<c:set var='buttonType' value="default"/>
<c:set var="uri" value="${zdf:addAuthToken('/zimbra/desktop/accsetup.jsp')}"/>
<c:set var='betaLink'>
    <fmt:message key='BetaNoteSupport'>
        <fmt:param>
        <a href=https://www.zimbra.com/products/desktop_support.html target=_blank><fmt:message key='BetaNoteLink'/></a></fmt:param>
    </fmt:message>
</c:set>
<c:set var="betaWarn">
    <fmt:message key='BetaWarn'>
        <fmt:param><a href="javascript:zd.toggle('beta')"><fmt:message key='BetaService'/></a></fmt:param>
    </fmt:message>
</c:set>

<c:choose>
    <c:when test="${accountFlavor eq 'Gmail'}">
        <jsp:useBean id="gbean" class="com.zimbra.cs.offline.jsp.GmailBean" scope="request"/>
        <jsp:setProperty name="gbean" property="*"/>
        <jsp:setProperty name="gbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(gbean)}
        <c:set var="bean" value="${gbean}" scope="request"/>
        <c:set var="help">
            <fmt:message key='GmailNote'>
                <fmt:param><a href="javascript:zd.toggle('helpInfo')"><fmt:message key='ClickHere'/></a></fmt:param>
            </fmt:message>
        </c:set>
        <c:set var="helpInfo">
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
        <c:set var="help">
            <fmt:message key='IMAPNote'/>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'MSE'}">
        <jsp:useBean id="mbean" class="com.zimbra.cs.offline.jsp.MmailBean" scope="request"/>
        <jsp:setProperty name="mbean" property="*"/>
        <jsp:setProperty name="mbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(mbean)}
        <c:set var="bean" value="${mbean}" scope="request"/>
        <c:set var="help">
            <fmt:message key='MSENote'/>
        </c:set>
        <c:set var="beta">
            <fmt:message key='BetaNoteExchange'>
                <fmt:param>${betaLink}</fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'Pop'}">
        <jsp:useBean id="pbean" class="com.zimbra.cs.offline.jsp.PopBean" scope="request"/>
        <jsp:setProperty name="pbean" property="*"/>
        <jsp:setProperty name="pbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(pbean)}
        <c:set var="bean" value="${pbean}" scope="request"/>
        <c:set var="help">
            <fmt:message key='POPNote'/>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'Xsync'}">
        <jsp:useBean id="xbean" class="com.zimbra.cs.offline.jsp.XsyncBean" scope="request"/>
        <jsp:setProperty name="xbean" property="*"/>
        <jsp:setProperty name="xbean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(xbean)}
        <c:set var="bean" value="${xbean}" scope="request"/>
        <c:set var="help">
            <fmt:message key='XsyncNote'/>
        </c:set>
        <c:set var="beta">
            <fmt:message key='BetaNoteXsync'>
                <fmt:param>${betaLink}</fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:when test="${accountFlavor eq 'YMP'}">
        <jsp:useBean id="ybean" class="com.zimbra.cs.offline.jsp.YmailBean" scope="request"/>
        <jsp:setProperty name="ybean" property="*"/>
        <jsp:setProperty name="ybean" property="locale" value="${pageContext.request.locale}"/>
        ${zdf:doRequest(ybean)}
        <c:set var="bean" value="${ybean}" scope="request"/>    
        <c:set var="help">
	    <fmt:message key='YMPNote'>
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
	        <fmt:param><a href="http://www.zimbra.com" target="_blank">www.zimbra.com</a></fmt:param>
            </fmt:message>
        </c:set>
    </c:when>
    <c:otherwise>
      <jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.MailBean"/>
      <jsp:setProperty name="bean" property="*"/>
      <jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>
    </c:otherwise>
</c:choose>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/css/desktop.css?skin=${bean.skin}" type="text/css">
<title><fmt:message key="ZimbraDesktop"/></title>

<script type="text/javascript" src="/zimbra/desktop/js/desktop.js"></script>
<script type="text/javascript">
function InitScreen() {}

function accntChange(accnt) {
   document.newAccnt.submit();
}

function OnCancel() {
    window.location = "${zdf:addAuthToken('/zimbra/desktop/console.jsp')}";
}

function OnDelete() {
    if (confirm("${onDeleteWarn}")) {
        document.accountForm.verb.value = "del";
        onSubmit();
    }
}

function OnSubmit() {
    zd.enable("accountName");
    zd.enable("email");
    zd.enable("password");
    if (document.getElementById("port"))
	zd.enable("port");
    if (document.getElementById("smtpPort"))
	zd.enable("smtpPort");
    if (document.getElementById("smtpPassword"))
	zd.enable("smtpPassword");
    zd.hide("cancelButton");
    zd.hide("saveButton");
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
    document.accountForm.submit();
}

function onEditLink(id, keep) {
    var elem = document.getElementById(id + "Link");

    elem.style.display = "none";
    elem = document.getElementById(id);
    if (elem.type == "password" && !keep)
        elem.value = "";
    zd.enable(elem);
    elem.focus();
}
</script>
</head>
<body onload="InitScreen()">
<div class="ZPanelPadding" align="center">
  <table class="ZPanel" cellpadding="0" cellspacing="0">
    <tr>
      <td class="ZPanelLogo">
        <img src="/zimbra/desktop/img/ZimbraDesktop.png" border="0">
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
<c:when test="${not empty bean.error}">
          <tr>
            <td colspan="2" width="450px">
              <div id="message" class="ZMessageInfo">
                <span class='ZOfflineError'>${bean.error}</span>
    <c:if test="${not empty bean.sslCertInfo}">
                <zd:sslCertError/>
                <br>
	<c:choose>
	<c:when test="${bean.sslCertInfo.acceptable}">
	   <c:set var='save'><fmt:message key='CertAcceptButton'/></c:set>
	   <c:set var='buttonType' value="Warn"/>
                <span class='ZOfflineError'><fmt:message key='CertAcceptWarning'/></span>
	</c:when>
	<c:otherwise>
                <span class='ZOfflineError'><fmt:message key='CertCantAccept'/></span>
	</c:otherwise>
	</c:choose>
    </c:if>
              </div>
            </td>
          </tr>
</c:when>
<c:when test="${not bean.allValid}" >
          <tr>
            <td colspan="2" width="450px">
              <div id="message" class="ZMessageInfo"><span class='ZOfflineError'><fmt:message key='PlsCorrectInput'/></span></div>
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
                  <option value="Xsync" <c:if test="${accountFlavor eq 'Xsync'}">selected</c:if> ><fmt:message key='Xsync'/></option>
                  <option value="MSE" <c:if test="${accountFlavor eq 'MSE'}">selected</c:if> ><fmt:message key='MSE'/></option>
                  <option value="Imap" <c:if test="${accountFlavor eq 'Imap'}">selected</c:if> ><fmt:message key='Imap'/></option>
                  <option value="Pop" <c:if test="${accountFlavor eq 'Pop'}">selected</c:if> ><fmt:message key='POP'/></option>
                  </select>
                </form>
              </td>
            </tr>
</c:if>
<c:choose>
<c:when test="${accountFlavor eq ''}">
</c:when>
<c:when test="${not bean.noVerb && (bean.allOK || not (bean.add || bean.modify))}">
    <jsp:forward page="${zdf:addAuthToken('console.jsp')}">
	<jsp:param name="accountName" value="${bean.accountName}"></jsp:param>
	<jsp:param name="error" value="${bean.error}"></jsp:param>
	<jsp:param name="verb" value="${bean.verb}"></jsp:param>
    </jsp:forward>
</c:when>
<c:when test="${bean.add || empty bean.accountId}">
    <c:if test="${not empty help || not empty beta}">
            <tr>
	      <td></td>
              <td class="ZAccountHelp">
        <c:if test="${not empty help}"><div>${help}</div>
            <c:if test="${not empty helpInfo}">
                <div id="helpInfo" style="display:none">${helpInfo}</div>
            </c:if>
        </c:if>
        <c:if test="${not empty beta}">
                <div>${betaWarn}</div>
                <div id="beta" style="display:none">${beta}</div>
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
                      <zd:button onclick='OnSubmit()' text='${save}' type='${buttonType}'/>
                    </td>
                    <td align="center" width="9%"><span id="whattodo" class="ZOfflineNotice"></span></td>
</c:if>
                    <td id="cancelButton" align="right">
                      <zd:button onclick='OnCancel()' text='${cancel}'/>
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
<zd:tips userAgent="${header['User-Agent']}"/>
</body>
</html>
