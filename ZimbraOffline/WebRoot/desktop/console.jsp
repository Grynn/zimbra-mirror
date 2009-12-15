<!--
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009 Zimbra, Inc.
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/messages/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

<zd:auth/>

<c:set var="accounts" value="${bean.accounts}"/>
<c:set var='add'><fmt:message key='AccountAdd'/></c:set>
<c:set var='login'><fmt:message key='GotoDesktop'/></c:set>

<c:if test="${param.loginOp != 'logout' && (param.client == 'advanced' || (param.client == 'standard' && fn:length(accounts) == 1))}">
    <jsp:forward page="${zdf:addAuthToken('/desktop/login.jsp')}"/>
</c:if>

<html>
<head>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<link rel="shortcut icon" href="/zimbra/favicon.ico" type="image/vnd.microsoft.icon">
<link rel="stylesheet" href="/zimbra/css/common.css" type="text/css">
<link rel="stylesheet" href="/zimbra/css/desktop.css?skin=${bean.skin}" type="text/css">
<title><fmt:message key="ZimbraDesktop"/></title>
<link rel="SHORTCUT ICON" href="<c:url value='/img/logo/favicon.ico'/>">

<script type="text/javascript" src="/zimbra/desktop/js/desktop.js"></script>
<script type="text/javascript">
function OnAdd() {
    window.location = "${zdf:addAuthToken('/zimbra/desktop/accsetup.jsp')}";
}

function OnDelete(id, name, type, flavor) {
    if (confirm("<fmt:message key='OnDeleteWarn'/>"))
        submit(id, name, type, flavor, "del");
}

function OnEdit(id, name, type, flavor) {
    submit(id, name, type, flavor, "");
}

function OnLogin() {
    zd.hide("addButton");
    zd.hide("loginButton");
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Loading'/></span>");
    window.location = "${zdf:addAuthToken('/zimbra/desktop/login.jsp')}";
}

function OnDefault(id, name, type, flavor) {
    document.accountForm.action = "${zdf:addAuthToken('/zimbra/desktop/console.jsp')}";
    submit(id, name, type, flavor, "");
}

function OnReset(id, name, type, flavor) {
    if (confirm("<fmt:message key='OnResetWarn'/>"))
        submit(id, name, type, flavor, "rst");
}

function OnReindex(id, name, type, flavor) {
    if (confirm("<fmt:message key='OnReindexWarn'/>"))
        submit(id, name, type, flavor, "idx");
}

function submit(id, name, type, flavor, verb) {
    if (verb != "") {
	zd.hide("addButton");
	zd.hide("loginButton");
	zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
    }
    document.accountForm.accountId.value = id;
    document.accountForm.accountName.value = name;
    document.accountForm.accountType.value = type;
    document.accountForm.accountFlavor.value = flavor;
    document.accountForm.verb.value = verb;
    document.accountForm.submit();
}
</script>
</head>
<body>
<div class="ZPanelPadding" align="center">
  <table class="ZPanel" cellpadding="0" cellspacing="0">
    <tr>
      <td class="ZPanelLogo">
        <img src="/zimbra/desktop/img/ZimbraDesktop.png" border="0">
      </td>
    </tr>
    <tr>
      <td class="ZPanelInfo">
<c:choose>
<c:when test="${empty accounts}">
        <table align="center" cellpadding="4" cellspacing="0">
          <tr>
            <td class="ZPanelTitle">
                <fmt:message key='WelcomeTitle'></fmt:message>
            </td>
          </tr>
          <tr><td><hr class="ZSeparator"></td></tr>
          <tr>
            <td>
              <div class="ZWelcome"><fmt:message key='WelcomeDesc1'/></div>
	      <br>
              <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfo1'/></div>
              <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfo2'/></div>
	      <br>
              <div class="ZWelcome"><fmt:message key='WelcomeDesc2'/></div>
              <ol class="ZWelcome">
                <li>
                  <div class="ZWelcome"><fmt:message key='WelcomeDescP1'/></div>
                  <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfoP1'/></div>
                </li>
                <li>
                  <div class="ZWelcome"><fmt:message key='WelcomeDescP2'/></div>
                  <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfoP2'/></div>
                </li>
                <li>
                  <div class="ZWelcome"><fmt:message key='WelcomeDescP3'/></div>
                  <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfoP3'/></div>
                </li>
                <li>
                  <div class="ZWelcome"><fmt:message key='WelcomeDescP4'/></div>
                  <div class="ZWelcomeInfo"><fmt:message key='WelcomeDescInfoP4'/></div>
                </li>
              </ol>
            </td>
          </tr>
          <tr><td><hr class="ZSeparator"></td></tr>
          <tr>
            <td align="center">
              <table cellpadding="5" cellspacing="0" width="95%">
                <tr>
                  <td align="center"><zd:button onclick='OnAdd()' text='${add}'/></td>
                </tr>
              </table>
            <td>
          </tr>
	</table>
</c:when>
<c:otherwise>
        <table align="center" width="80%" cellpadding="4" cellspacing="0">
          <tr>
            <td class="ZPanelTitle">
                <fmt:message key='HeadTitle'></fmt:message>
            </td>
          </tr>
          <tr><td><hr class="ZSeparator"></td></tr>
<c:if test="${not empty param.verb && not empty param.accountName}">
          <tr>
            <td>
              <div id="message" class="ZMessageInfo">
    <c:choose>
    <c:when test="${not empty param.error}">
                <span class='ZOfflineError'>${param.error}</span>
    </c:when>
    <c:when test="${param.verb eq 'add'}">
                <b><fmt:message key='ServiceAdded'><fmt:param>${param.accountName}</fmt:param></fmt:message></b>
                <p><fmt:message key='ServiceAddedNote'/></p>
    </c:when>
    <c:otherwise>
	<c:choose>
	<c:when test="${param.verb eq 'del'}">
	    <c:set var="key" value="ServiceDeleted"/>
	</c:when>
	<c:when test="${param.verb eq 'mod'}">
	    <c:set var="key" value="ServiceUpdated"/>
	</c:when>
	<c:when test="${param.verb eq 'rst'}">
	    <c:set var="key" value="ServiceReset"/>
	</c:when>
	<c:when test="${param.verb eq 'idx'}">
        <c:set var="key" value="ServiceReindex"/>
    </c:when>
	</c:choose>
                <b><fmt:message key="${key}"><fmt:param>${param.accountName}</fmt:param></fmt:message></b>
    </c:otherwise>
    </c:choose>
              </div>
            </td>
          </tr>
          <tr><td><hr class="ZSeparator"></td></tr>
</c:if>
          <tr>
            <td align="center">
              <table cellpadding="0" cellspacing="0" width="100%">
<c:set var='default' value='true'/>
<c:forEach items="${accounts}" var="account">
<c:if test="${not empty account.errorCode}">
                <tr>
                  <td colspan="2" width="100%">
                    <div class="ZMessageInfo"><span class='ZOfflineError'>${account.userFriendlyErrorMessage}</span>
                      <c:if test="${not empty account.errorMsg}">
                        <a href="javascript:zd.toggle('errorDetails')">(<fmt:message key='DebugInfo'/>)</a>
                        <div id="errorDetails" style="display:none;overflow:hidden">
                          <p class="ZFieldSubLabelLeft">
                            <b><fmt:message key='DebugMsg'/></b>: ${account.errorMsg}
                          </p>
                          <c:if test="${not empty account.exception}">
                            <p class="ZFieldSubLabelLeft">
                              <b><fmt:message key='DebugStack'/></b>: <pre>${account.exception}</pre>
                            </p>
                          </c:if>
                          <p class="ZFieldSubLabelLeft"><b><fmt:message key='DebugActionNote'/></b></p>
                        </div>
                      </c:if>
                    </div>
                  </td>
                </tr>
</c:if>
                <tr>
                  <td>
                    <div class="ZAccountName">${account.name}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="ZAccountInfo">${account.email}</div>
                  </td>
                  <td width="1%">
                    <div class="ZAccountInfo">
    <c:choose>
       <c:when test="${account.statusUnknown}">
                      <i><img src="/zimbra/img/startup/ImgOffline.gif" align="absmiddle">&nbsp;<fmt:message key='StatusUnknown'/></i>
       </c:when>
       <c:when test="${account.statusOffline}">
                      <i><img src="/zimbra/img/startup/ImgImAway.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOffline'/></i>
       </c:when>
       <c:when test="${account.statusOnline}">
                      <i><img src="/zimbra/img/startup/ImgImAvailable.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOnline'/></i>
       </c:when>
       <c:when test="${account.statusRunning}">
                      <i><img src="/zimbra/img/animated/Imgwait_16.gif" align="absmiddle">&nbsp;<fmt:message key='StatusInProg'/></i>
       </c:when>
       <c:when test="${account.statusAuthFailed}">
                      <i><img src="/zimbra/img/startup/ImgImDnd.gif" align="absmiddle">&nbsp;<fmt:message key='StatusCantLogin'/></i>
       </c:when>
       <c:when test="${account.statusError}">
                      <i><img height="14" width="14" src="/zimbra/img/dwt/ImgCritical.gif" align="absmiddle">&nbsp;<fmt:message key='StatusErr'/></i>
       </c:when>
    </c:choose>
		    </div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="ZAccountInfo">
                      <a href="javascript:OnEdit('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Edit"/></a>&nbsp;
                      <a href="javascript:OnDelete('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Delete"/></a>&nbsp;
                      <a href="javascript:OnReset('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="ResetData"/></a>&nbsp;
                      <a href="javascript:OnReindex('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Reindex"/></a>
	<c:if test="${not default}">
                     &nbsp;<a href="javascript:OnDefault('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><img src="/zimbra/img/startup/ImgAccordionClosed.gif" width="12" heigh="12" align="absbottom" border="0"></a>
       </c:if>
                      </div>
                    </td>
    <c:choose>
    	<c:when test='${account.lastSync != null}'>
                    <td align="right">
                      <div class="ZAccountInfo">
                        <i class="ZHint"><fmt:message key='LastSync'><fmt:param><fmt:formatDate value="${account.lastSync}" type="both" dateStyle="short" timeStyle="short"/></fmt:param></fmt:message></i>
                      </div>
                    </td>
    	</c:when>
    </c:choose>
                  </tr>
                  <tr><td colspan="2"><hr class="ZSeparator"></td></tr>
<c:set var='default' value='false'/>
</c:forEach>
                </table>
              </td>
            </tr>
            <tr>
              <td align="center">
                <table cellpadding="5" cellspacing="0" width="95%">
                  <tr>
                    <td id="loginButton" align="left">
                      <zd:button onclick='OnLogin()' text='${login}' type='default'/>
                    </td>
                    <td align="center"><span id="whattodo" class="ZOfflineNotice"></span></td>
                    <td id="addButton" align="right">
                      <zd:button onclick='OnAdd()' text='${add}'/>
                    </td>
                  </tr>
              </table>
            </td>
          </tr>
        </table>
</c:otherwise>
</c:choose>
      </td>
    </tr>

  </table>
</div>
<div>
  <zd:tips userAgent="${header['User-Agent']}"/>
</div>

<form name="accountForm" action="${zdf:addAuthToken('/zimbra/desktop/accsetup.jsp')}" method="POST">
    <input type="hidden" name="accountId">
    <input type="hidden" name="accountName">
    <input type="hidden" name="accountType">
    <input type="hidden" name="accountFlavor">
    <input type="hidden" name="verb">
</form>
</body>
</html>

