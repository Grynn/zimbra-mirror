<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<fmt:setBundle basename="/messages/ZdMsg" scope="request"/>

<jsp:useBean id="bean" class="com.zimbra.cs.offline.jsp.ConsoleBean"/>
<jsp:setProperty name="bean" property="*"/>
<jsp:setProperty name="bean" property="locale" value="${pageContext.request.locale}"/>

<% pageContext.setAttribute("devMode", request.getParameter("dev")); %>

<zd:auth/>

<c:set var="accounts" value="${bean.accounts}"/>
<c:set var='add'><fmt:message key='AccountAdd'/></c:set>
<c:set var='login'><fmt:message key='GotoDesktop'/></c:set>

<c:if test="${param.loginOp != 'logout' && (param.client == 'advanced' || (param.client == 'standard' && fn:length(accounts) == 1))}">
	<jsp:forward page="${zdf:addAuthToken('/desktop/login.jsp', devMode)}"/>
</c:if>

<html>
<head>
<c:if test="${not empty bean.accounts}">
<meta http-equiv="refresh" content="12;url=${zdf:addAuthToken('/desktop/console.jsp', devMode)}" >
</c:if>
<meta http-equiv="CACHE-CONTROL" content="NO-CACHE">
<title><fmt:message key="ZimbraDesktop"/></title>

<link rel="stylesheet" type="text/css" href="<c:url value="/skins/_base/base2/desktop.css"></c:url>">
<link rel="stylesheet" type="text/css" href="<c:url value="/skins/${bean.skin}/desktop.css"></c:url>">
<link rel="SHORTCUT ICON" href="<c:url value='/img/logo/favicon.ico'/>">

<script type="text/javascript" src="/js/desktop.js"></script>

<script type="text/javascript">
function OnAdd() {
	window.location = "${zdf:addAuthToken('/desktop/accsetup.jsp', devMode)}";
}

function OnDelete(id, name, type, flavor) {
	if (confirm("<fmt:message key='OnDeleteWarn'/>")) {
		submit(id, name, type, flavor, "del");
	}
}

function OnEdit(id, name, type, flavor) {
	submit(id, name, type, flavor, "");
}

function OnLogin() {
	zd.disableButton("loginButton", "<fmt:message key='Loading'/>");
	window.location = "${zdf:addAuthToken('/desktop/login.jsp', devMode)}";
}

function OnDefault(id, name, type, flavor) {
	document.accountForm.action = "${zdf:addAuthToken('/desktop/console.jsp', devMode)}";
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
	if (verb != "")
		zd.disableButton("loginButton", "<fmt:message key='Processing'/>");
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
<center>
<table border=0 cellpadding=0 cellspacing=0>
<tr>
	<td>
		<div class="ZPanel">
			<table border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td>
						<div class="ZPanelLogo"></div>
					</td>
				</tr>
			</table>
			<table border=0 cellpadding=0 cellspacing=0 width=100%>
				<c:choose>
					<c:when test="${not empty accounts}">
						<tr>
							<td class="ZPanelTabs">
								<table border=0 cellpadding=0 cellspacing=0>
									<tr>
										<td><div class="ZPanelTabActive ZPanelFirstTab"><fmt:message key='HeadTitle'/></div></td>
										<td><div class="ZPanelTabInactive ZPanelTab" onclick='OnAdd()'><fmt:message key='AccountAdd'/></div></td>
									</tr>
								</table>
							</td>
						</tr>
					</c:when>
				</c:choose>
				<tr>
					<td class="ZPanelInfoOuter">
						<div class="ZPanelInfoInner">
							<center>
								<table border=0 cellpadding=0 cellspacing=0 width=95%>
									<tr><td></td></tr>
									<c:choose>
										<c:when test="${empty accounts}">
											<tr>
												<td>
													<p class="ZWelcome"><fmt:message key='WelcomeDesc1'/></p>
													<p class="ZWelcome"><fmt:message key='WelcomeDescInfo1'/></p>
													<p class="ZWelcome"><fmt:message key='WelcomeDescInfo2'/></p>
													<p class="ZWelcome"><fmt:message key='WelcomeDesc2'/></p>
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
											<tr>
												<td>
													<table border=0 cellpadding=0 cellspacing=0 align=right>
														<tr>
															<td>
																<div class="ZPanelButton" onclick='OnAdd()' onmouseover='zd.OnHover(this, true)' onmouseout='zd.OnHover(this)'><fmt:message key='AccountAdd'/></div>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:if test="${not empty param.verb && not empty param.accountName}">
												<tr>
													<td>
														<c:choose>
															<c:when test="${not empty param.error}">
																<div id="message" class="ZError">${param.error}</div>
															</c:when>
															<c:when test="${param.verb eq 'add'}">
																<div id="message" class="ZInfo">
																	<fmt:message key='ServiceAdded'><fmt:param>${param.accountName}</fmt:param></fmt:message>
																	<p><fmt:message key='ServiceAddedNote'/></p>
																</div>
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
																<div id="message" class="ZInfo">
																	<fmt:message key="${key}"><fmt:param>${param.accountName}</fmt:param></fmt:message>
																</div>
															</c:otherwise>
														</c:choose>
													</td>
												</tr>
											</c:if>
											<c:set var='default' value='true'/>
											<c:forEach items="${accounts}" var="account">
												<tr>
													<td>
														<div class="${not empty account.errorCode ? 'ZAccountBad' : 'ZAccountGood'}">
															<table border=0 width=100%">
																<tr>
																	<td rowspan=2 width=1%>
																		<img src="<c:url value='/img/imgAccount${account.flavor}48.png'/>" align=absbottom>
																	</td>
																	<td>
																		<table border=0 width=100% class="${not empty account.errorCode ? 'ZAccountHeaderBad' : 'ZAccountHeaderGood'}">
																			<tr>
																				<td>
																					<table border=0>
																						<tr>
																							<td><div class="ZAccountName">${account.name}</div></td>
																							<c:if test="${not default}">
																								<td>
																									<a title='<fmt:message key="SetAsDefault"/>' href="javascript:OnDefault('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')">
																										<img src="/zimbra/img/startup/ImgAccordionClosed.gif" width=12 height=12 align="absbottom" border="0"></a>
																								</td>
																							</c:if>
																						</tr>
																					</table>
																				</td>
																				<td align=right class="ZAccountActions">
																					<a href="javascript:OnEdit('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Edit"/></a>&nbsp;
																					<a href="javascript:OnDelete('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Delete"/></a>&nbsp;
																					<a href="javascript:OnReset('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="ResetData"/></a>&nbsp;
																					<a href="javascript:OnReindex('${account.id}', '${fn:replace(account.name, "'", "\\'")}', '${account.type}', '${account.flavor}')"><fmt:message key="Reindex"/></a>
																				</td>
																			</tr>
																		</table>
																		<table border=0 width=100%>
																			<tr>
																				<td>
																					<div class="ZAccountEmail">${account.email}</div>
																				</td>
																				<td align=right>
																					<table border=0>
																						<tr>
																							<c:choose>
																								<c:when test='${account.lastSync != null}'>
																									<td class="ZAccountLastSync">
																										<fmt:message key='LastSync'><fmt:param><fmt:formatDate value="${account.lastSync}" type="both" dateStyle="short" timeStyle="short"/></fmt:param></fmt:message>
																									</td>
																								</c:when>
																							</c:choose>
																							<td class="ZAccountStatus">
																								<c:choose>
																									<c:when test="${account.statusUnknown}">
																										<img src="/zimbra/img/startup/ImgOffline.gif" align="absmiddle">&nbsp;<fmt:message key='StatusUnknown'/>
																									</c:when>
																									<c:when test="${account.statusOffline}">
																										<img src="/zimbra/img/startup/ImgImAway.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOffline'/>
																									</c:when>
																									<c:when test="${account.statusOnline}">
																										<img src="/zimbra/img/startup/ImgImAvailable.gif" align="absmiddle">&nbsp;<fmt:message key='StatusOnline'/>
																									</c:when>
																									<c:when test="${account.statusRunning}">
																										<img src="/zimbra/img/animated/ImgSpinner.gif" align="absmiddle">&nbsp;<fmt:message key='StatusInProg'/>
																									</c:when>
																									<c:when test="${account.statusAuthFailed}">
																										<img src="/zimbra/img/startup/ImgImDnd.gif" align="absmiddle">&nbsp;<fmt:message key='StatusCantLogin'/>
																									</c:when>
																									<c:when test="${account.statusError}">
																										<img height="14" width="14" src="/zimbra/img/dwt/ImgCritical.gif" align="absmiddle">&nbsp;<fmt:message key='StatusErr'/>
																									</c:when>
																								</c:choose>
																							</td>
																						</tr>
																					</table>
																				</td>
																			</tr>
																		</table>
																		<c:if test="${not empty account.errorCode}">
																			<div class="ZAccountError">
																				<div class="ZAccountErrorMessage">${account.userFriendlyErrorMessage}</div>
																				<c:if test="${not empty account.errorMsg}">
																					<a href="javascript:zd.toggle('errorDetails')">(<fmt:message key='DebugInfo'/>)</a>
																					<div id="errorDetails">
																						<div class="ZAccountErrorDetails">
																							${account.errorMsg}
																							<c:if test="${not empty account.exception}">
																								<b><fmt:message key='DebugStack'/>:</b>
																								<pre>${account.exception}</pre>
																							</c:if>
																						</div>
																						<b><fmt:message key='DebugActionNote'/></b>
																					</div>
																				</c:if>
																			</div>
																		</c:if>
																	</div>
																</td>
															</td>
														</tr>
													</table>
												</tr>
												<c:set var='default' value='false'/>
											</c:forEach>
											<tr>
												<td>
													<table border=0 cellpadding=0 cellspacing=0>
														<tr>
															<td>
																<div id="loginButton" class="ZPanelButton" onclick='OnLogin()' onmouseover='zd.OnHover(this, true)' onmouseout='zd.OnHover(this)'><fmt:message key='GotoDesktop'/> &#187;</div>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</c:otherwise>
									</c:choose>
								</table>
							</center>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</td>
</tr>
</table>
</center>

<zd:tips userAgent="${header['User-Agent']}"/>

<form name="accountForm" action="${zdf:addAuthToken('/desktop/accsetup.jsp', devMode)}" method="POST">
	<input type="hidden" name="accountId">
	<input type="hidden" name="accountName">
	<input type="hidden" name="accountType">
	<input type="hidden" name="accountFlavor">
	<input type="hidden" name="verb">
</form>

</body>
</html>

