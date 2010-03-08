<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<jsp:useBean id="extBean" class="com.zimbra.cs.offline.jsp.ExtensionBean"/>

<fmt:setBundle basename="/messages/ZdMsg" scope="request"/>

<% pageContext.setAttribute("devMode", request.getParameter("dev")); %>

<zd:auth/>

<c:set var='accountFlavor' value="${param.accountFlavor eq null ? '' : param.accountFlavor}"/>
<c:set var='verb' value="${param.verb eq null ? '' : param.verb}"/>
<c:set var='save' value='Save'/>
<c:set var="uri" value="${zdf:addAuthToken('/desktop/accsetup.jsp', devMode)}"/>
<c:set var='betaLink'>
	<fmt:message key='BetaNoteSupport'>
		<fmt:param>
			<a href=https://www.zimbra.com/products/desktop_support.html target=_blank><fmt:message key='BetaNoteLink'/></a>
		</fmt:param>
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
				<li><fmt:message key='GmailLogin'><fmt:param><a href=http://gmail.com target=_blank><fmt:message key='Gmail'/></a></fmt:param></fmt:message></li>
				<li><fmt:message key='GmailClickTop'><fmt:param><b><fmt:message key='GmailSettingsLink'/></b></fmt:param></fmt:message></li>
				<li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailFwdPOP'/></b></fmt:param></fmt:message></li>
				<li><fmt:message key='GmailSelect'><fmt:param><b><fmt:message key='GmailEnableIMAP'/></b></fmt:param></fmt:message></li>
				<li><fmt:message key='GmailClick'><fmt:param><b><fmt:message key='GmailSaveChgs'/></b></fmt:param></fmt:message></li>
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
<title><fmt:message key="ZimbraDesktop"/></title>

<!--
<link rel="stylesheet" href="<c:url value="/css/common,desktop.css">
    <c:param name="debug" value="${devMode}" />
    <c:param name="skin" value="${bean.skin}" />
</c:url>" type="text/css">
-->
<link rel="stylesheet" type="text/css" href="<c:url value="/skins/_base/base2/desktop.css"></c:url>">
<link rel="stylesheet" type="text/css" href="<c:url value="/skins/${bean.skin}/desktop.css"></c:url>">

<link rel="SHORTCUT ICON" href="<c:url value='/img/logo/favicon.ico'/>">

<script type="text/javascript" src="/js/desktop.js"></script>

<script type="text/javascript">

function InitScreen() {
}

function accntChange(accnt) {
	document.newAccnt.submit();
}

function OnCancel() {
	window.location = "${zdf:addAuthToken('/desktop/console.jsp', devMode)}";
}

function OnDelete() {
	if (confirm("${onDeleteWarn}")) {
		document.accountForm.verb.value = "del";
		onSubmit();
	}
}

function OnSubmit() {
	zd.hide("cancelButton");
	zd.disableButton("saveButton", "<fmt:message key='Processing'/>");
	zd.enable("email");
	zd.enable("password");
	if (document.getElementById("port"))
		zd.enable("port");
	if (document.getElementById("smtpPort"))
		zd.enable("smtpPort");
	if (document.getElementById("smtpPassword"))
		zd.enable("smtpPassword");
	document.accountForm.submit();
}

function onEditLink(id, keep, makeInvisible) {
	var elem = document.getElementById(id + "Link");

	if (makeInvisible)
		elem.style.visibility = "hidden";
	else
		elem.style.display = "none";
	elem = document.getElementById(id);
	if (elem.type == "password" && !keep) {
		elem.value = "";
	}
	zd.enable(elem);
	elem.focus();
}

<c:if test="${not empty accountFlavor}">
	function InitScreen() {
		if (document.getElementById("password"))
			zd.disable('password');
		if (document.getElementById("smtpPassword"))
			zd.disable('smtpPassword');
		<c:if test="${bean.password eq '' or not zdf:isValid(bean, 'password') || verb eq 'add'}">
			onEditLink("password", true);
		</c:if>
		<c:if test="${bean.smtpConfigSupported && (bean.smtpPassword eq '' or not zdf:isValid(bean, 'smtpPassword') || verb eq 'add')}">
			onEditLink("smtpPassword", true);
		</c:if>
		<c:if test="${verb eq 'add' or verb eq ''}">
			document.getElementById('accountName').focus();
		</c:if>
	}

	function SetPort() {
		if (zd.isDisabled("port")) {
			if (${bean.type eq 'pop3'})
				zd.set("port", zd.isChecked("ssl") ? "995" : "110");
			else if (${bean.type eq 'imap'})
				zd.set("port", zd.isChecked("ssl") ? "993" : "143");
			else if (${bean.type eq 'zimbra' or bean.type eq 'xsync'})
				zd.set("port", zd.isChecked("ssl") ? "443" : "80");
		}
	}

	function SetSmtpPort() {
		if (zd.isDisabled("smtpPort"))
			zd.set("smtpPort", zd.isChecked("smtpSsl") ? "465" : "25");
	}
</c:if>

</script>
</head>

<body onload="InitScreen();">
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
				<tr>
					<td class="ZPanelTabs">
						<table border=0 cellpadding=0 cellspacing=0>
							<tr>
								<td><div class="ZPanelTabInactive ZPanelFirstTab" onclick='OnCancel()'><fmt:message key='HeadTitle'/></div></td>
								<td>
									<div class="ZPanelTabActive ZPanelTab">
										<c:choose>
											<c:when test="${empty bean.accountId}">
												<fmt:message key='AccountAdd'></fmt:message>
											</c:when>
											<c:otherwise>
												<fmt:message key='EditAccount'></fmt:message>
											</c:otherwise>
										</c:choose>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td class="ZPanelInfoOuter">
						<div class="ZPanelInfoInner">
							<center>
							<table border=0 cellpadding=0 cellspacing=0 class="ZTable">
								<tr>
									<td colspan=2>
										<c:choose>
											<c:when test="${accountFlavor eq ''}">
											</c:when>
											<c:when test="${not empty bean.error}">
												<div id="message" class="ZError">
													${bean.error}
													<c:if test="${not empty bean.sslCertInfo}">
														<zd:sslCertError/><br>
														<c:choose>
															<c:when test="${bean.sslCertInfo.acceptable}">
																<c:set var='save' value='CertAcceptButton'/>
																<div><fmt:message key='CertAcceptWarning'/></div>
															</c:when>
															<c:otherwise>
																<div><fmt:message key='CertCantAccept'/></div>
															</c:otherwise>
														</c:choose>
													</c:if>
												</div>
											</c:when>
											<c:when test="${not bean.allValid}">
												<div id="message" class="ZError">
													<fmt:message key='PlsCorrectInput'/>
												</div>
											</c:when>
										</c:choose>
									</td>
								</tr>
								<c:if test="${empty bean.accountId}">
									<tr>
										<td>
											<div class="ZFieldLabel"><fmt:message key='AccountType'/>:</div>
										</td>
										<td width=90%>
											<form name="newAccnt" action="" method="POST">
												<select name="accountFlavor" id="accountFlavor" onchange="accntChange(this)" class="ZSelect">
													<option value=""><fmt:message key='AccountSelect'/></option>
													<option value="Zimbra" <c:if test="${accountFlavor eq 'Zimbra'}">selected</c:if> ><fmt:message key='Zimbra'/></option>
													<option value="Gmail" <c:if test="${accountFlavor eq 'Gmail'}">selected</c:if> ><fmt:message key='Gmail'/></option>
													<option value="YMP" <c:if test="${accountFlavor eq 'YMP'}">selected</c:if> ><fmt:message key='YMP'/></option>
													<c:if test="${extBean.xsyncEnabled}">
													  <option value="Xsync" <c:if test="${accountFlavor eq 'Xsync'}">selected</c:if> ><fmt:message key='Xsync'/></option>
													</c:if>
													<option value="MSE" <c:if test="${accountFlavor eq 'MSE'}">selected</c:if> ><fmt:message key='MSE'/></option>
													<option value="Imap" <c:if test="${accountFlavor eq 'Imap'}">selected</c:if> ><fmt:message key='Imap'/></option>
													<option value="Pop" <c:if test="${accountFlavor eq 'Pop'}">selected</c:if> ><fmt:message key='POP'/></option>
												</select>
												<input type="hidden" name="verb" id="verb" value=""></input>
											</form>
										</td>
									</tr>
								</c:if>
								<c:if test="${not empty accountFlavor}">
									<form name="accountForm" action="${uri}" method="POST" onsubmit="OnSubmit();">
										<input type="hidden" name="accountId" value="${bean.accountId}">
										<input type="hidden" name="accountFlavor" value="${accountFlavor}">
										<input type="hidden" id="verb" name="verb" value="${empty bean.accountId ? 'add' : 'mod'}" >
										<input type="hidden" name="verb" value="${verb}">
										<c:if test="${bean.type ne 'zimbra' and bean.type ne 'xsync' and not empty bean.domain}">
											<input type="hidden" name="domain" value="${bean.domain}">
										</c:if>
										<c:if test="${not empty bean.sslCertInfo and bean.sslCertInfo.acceptable}">
											<input type="hidden" name="sslCertAlias" value="${bean.sslCertInfo.alias}">
										</c:if>
										<c:choose>
											<c:when test="${accountFlavor eq ''}">
											</c:when>
											<c:when test="${not bean.noVerb && (bean.allOK || not (bean.add || bean.modify))}">
												<jsp:forward page="${zdf:addAuthToken('/desktop/console.jsp', devMode)}">
													<jsp:param name="accountName" value="${bean.accountName}"></jsp:param>
													<jsp:param name="error" value="${bean.error}"></jsp:param>
													<jsp:param name="verb" value="${bean.verb}"></jsp:param>
												</jsp:forward>
											</c:when>
											<c:when test="${bean.add || empty bean.accountId}">
												<c:if test="${not empty help || not empty beta}">
													<tr>
														<td align=right>
															<nobr><img src="<c:url value='/img/imgAccount${accountFlavor}48.png'/>" align=absbottom>&nbsp;&nbsp;</nobr>
														</td>
														<td class="ZAccountHelp">
															<c:if test="${not empty help}">
																<div>${help}</div>
																<c:if test="${not empty helpInfo}">
																	<div id="helpInfo" style="display:none">${helpInfo}</div>
																</c:if>
															</c:if>
															<c:if test="${not empty beta}">
																<div class="ZAccountWarning">${betaWarn}</div>
																<div id="beta" style="display:none">${beta}</div>
															</c:if>
														</td>
													</tr>
												</c:if>
											</c:when>
											<c:otherwise>
												${zdf:reload(bean)}
											</c:otherwise>
										</c:choose>
										<tr>
											<td>
												<div class="${zdf:isValid(bean, 'accountName') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='AccountName'/>:</div>
											</td>
											<td>
												<input class="ZField" type="text" id="accountName" name="accountName" value="${bean.accountName}">
											</td>
										</tr>
										<c:if test="${bean.type ne 'zimbra'}">
											<tr>
												<td>
													<div class="ZFieldLabel"><fmt:message key='FullName'/>:</div>
												</td>
												<td>
													<input class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}">
												</td>
											</tr>
										</c:if>
										<tr id="emailRow">
											<td>
												<div class="${zdf:isValid(bean, 'email') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='EmailAddress'/>:</div>
											</td>
											<td>
												<input class="ZField" type="text" id="email" name="email" value="${bean.email}" <c:if test="${not empty bean.accountId}">disabled="disabled"</c:if>>
											</td>
										</tr>
										<c:if test="${bean.serverConfigSupported}">
											<c:if test="${bean.usernameRequired}">
												<tr>
													<td colspan=2 class="ZRowPadding"></td>
												</tr>
												<tr>
													<td colspan=2>
														<div class="ZFieldLabel ZSectionLabel"><fmt:message key='ReceivingMail'/></div>
														<hr class="ZHorizontalLine"/>
													</td>
												</tr>
												<c:if test="${bean.type eq 'xsync'}">
													<tr>
														<td>
															<div class="ZFieldLabel"><fmt:message key='Domain'/>:</div>
														</td>
														<td><input class="ZField" type="text" id="domain" name="domain" value="${bean.domain}"></td>
													</tr>
												</c:if>
												<tr id="usernameRow">
													<td>
														<div class="${zdf:isValid(bean, 'username') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='UserName'/>:</div>
													</td>
													<td><input class="ZField" type="text" id="username" name="username" value="${bean.username}"></td>
												</tr>
											</c:if>
										</c:if>
										<tr id="passwordRow">
											<td>
												<div class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Password'/>:</div>
											</td>
											<td>
												<table border=0 cellpadding=0 cellspacing=0 width=100% class="ZTableInner">
													<tr>
														<td>
															<input class="ZField" type="password" id="password" name="password" value="${bean.password}" disabled="disabled">
														</td>
														<td class="ZSubLink" id="passwordLink">
															<a href="javascript:;" onclick="onEditLink('password'); return false;"><fmt:message key='Edit'/></a>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<c:if test="${bean.serverConfigSupported}">
											<tr id="mailServerRow">
												<td>
													<div class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='InMailServer'/>:</div>
												</td>
												<td>
													<table border=0 cellpadding=0 cellspacing=0 width=100% class="ZTableInner">
														<tr>
															<td width=60%>
																<input class="ZField" type="text" id="host" name="host" value="${bean.host}">
															</td>
															<td align=right>
																<table border=0>
																	<tr>
																		<td>
																			<div class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldErrorLabel'}"><fmt:message key='Port'/>:</div>
																		</td>
																		<td>
																			<input type="text" class="ZField" id="port" name="port" value="${bean.port}" size=5 disabled="disabled">
																		</td>
																		<td class="ZSubLink" id="portLink">
																			<a href="javascript:;" onclick="onEditLink('port', false, true); return false;"><fmt:message key='Edit'/></a>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr id="mailSecureRow">
												<td>
													<div class="ZFieldLabel"><fmt:message key='Security'/>:</div>
												</td>
												<td>
													<table border=0 cellpadding=0 cellspacing=0 class="ZTableInner">
														<tr>
															<td>
																<input type="radio" id="cleartext" name="security" value="cleartext" ${bean.security == 'cleartext' ? 'checked' : ''} onclick="SetPort()">
																<label class="ZRadioLabel" for="cleartext"><fmt:message key='SecurityNone'/></label>
															</td>
															<td>&nbsp;&nbsp;</td>
															<td>
																<input type="radio" id="ssl" name="security" value="ssl" ${bean.security == 'ssl' ? 'checked' : ''} onclick="SetPort()">
																<label class="ZRadioLabel" for="ssl"><fmt:message key='SecuritySsl'/></label>
															</td>
															<td>&nbsp;&nbsp;</td>
															<c:if test="${bean.type ne 'zimbra' and bean.type ne 'xsync'}">
																<td>
																	<input type="radio" id="tls" name="security" value="tls" ${bean.security == 'tls' ? 'checked' : ''} onclick="SetPort()">
																	<label class="ZRadioLabel" for="tls"><fmt:message key='SecurityTls'/></label>
																</td>
																<td>&nbsp;&nbsp;</td>
																<td>
																	<input type="radio" id="tls_if_available" name="security" value="tls_if_available" ${bean.security == 'tls_if_available' ? 'checked' : ''} onclick="SetPort()">
																	<label class="ZRadioLabel" for="tls_if_available"><fmt:message key='SecurityTlsIfAvailable'/></label>
																</td>
															</c:if>
														</tr>
													</table>
												</td>
											</tr>
											<c:if test="${bean.smtpConfigSupported}">
												<tr>
													<td colspan=2 class="ZRowPadding"></td>
												</tr>
												<tr>
													<td colspan=2>
														<div class="ZFieldLabel ZSectionLabel">
															<fmt:message key='SendingMail'/>
															<hr class="ZHorizontalLine"/>
														</div>
													</td>
												</tr>
												<tr id="smtpServerRow">
													<td>
														<div class="${zdf:isValid(bean, 'smtpHost') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='OutMailServer'/>:</div>
													</td>
													<td>
														<table border=0 cellpadding=0 cellspacing=0 width=100% class="ZTableInner">
															<tr>
																<td width=60%>
																	<input class="ZField" type="text" id="smtpHost" name="smtpHost" value="${bean.smtpHost}">
																</td>
																<td align=right>
																	<table border=0>
																		<tr>
																			<td>
																				<div class="${zdf:isValid(bean, 'smtpPort') ? 'ZFieldLabel' : 'ZFieldErrorLabel'}"><fmt:message key='Port'/>:</div>
																			</td>
																			<td>
																				<input class="ZField" type="text" id="smtpPort" name="smtpPort" value="${bean.smtpPort}" size=5 disabled="disabled">
																			</td>
																			<td class="ZSubLink" id="smtpPortLink"><a href="javascript:;" onclick="onEditLink('smtpPort', false, true); return false;"><fmt:message key='Edit'/></a></td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr id="smtpSecureRow">
													<td>
														<div class="ZFieldLabel"><fmt:message key='Security'/>:</div>
													</td>
													<td>
														<input type="checkbox" id="smtpSsl" name="smtpSsl" ${bean.smtpSsl ? 'checked' : ''} onclick="SetSmtpPort()">
														<label class="ZCheckboxLabel" for="smtpSsl"><fmt:message key='SecureSmtp'/></label>
													</td>
												</tr>
												<tr id="smtpAuthRow">
													<td>
														<div class="ZFieldLabel"><fmt:message key='SmtpAuth'/>:</div>
													</td>
													<td>
														<input type="checkbox" id="smtpAuth" name="smtpAuth" ${bean.smtpAuth ? 'checked' : ''} onclick='zd.toggle("smtpAuthSettingsRow", this.checked, "smtpUsername");'>
														<label class="ZCheckboxLabel" for="smtpAuth"><fmt:message key='SmtpAuthInfo'/></label>
													</td>
												</tr>
												<tr id="smtpAuthSettingsRow" ${bean.smtpAuth ? '' : 'style="display:none"'}>
													<td>
														<div class="ZFieldLabel"></div>
													</td>
													<td>
														<table border=0 cellpadding=0 cellspacing=0 width=100%>
															<tr>
																<td width=35%>
																	<div class="${zdf:isValid(bean, 'smtpUsername') ? 'ZFieldSubLabel' : 'ZFieldErrorSubLabel'}"><fmt:message key='UserName'/>:</div>
																</td>
																<td>
																	<input class="ZField" type="text" id="smtpUsername" name="smtpUsername" value="${bean.smtpUsername}" onkeypress='zd.markElementAsManuallyChanged(this)'>
																</td>
															</tr>
															<tr>
																<td width=35%>
																	<div class="${zdf:isValid(bean, 'smtpPassword') ? 'ZFieldSubLabel' : 'ZFieldErrorSubLabel'}"><fmt:message key='Password'/>:</div>
																</td>
																<td>
																	<table border=0 cellpadding=0 cellspacing=0 width=100% class="ZTableInner">
																		<tr>
																			<td>
																				<input class="ZField" type="password" id="smtpPassword" name="smtpPassword" value="${bean.smtpPassword}" disabled="disabled"></td>
																			<td id="smtpPasswordLink" width="1%"><a href="javascript:;" onclick="onEditLink('smtpPassword'); return false;"><fmt:message key='Edit'/></a>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr id="replyToRow">
													<td valign=top>
														<div class="ZFieldLabel"><fmt:message key='ReplyTo'/>:</div>
													</td>
													<td>
														<table border=0 cellpadding=0 cellspacing=0 width=100%>
															<tr>
																<td width=35%>
																	<div class="ZFieldSubLabel"><fmt:message key='Name'/>:</div>
																</td>
																<td>
																	<input class="ZField" type="text" id="replyToDisplay" name="replyToDisplay" value="${bean.replyToDisplay}" onkeypress='zd.markElementAsManuallyChanged(this)'>
																</td>
															</tr>
															<tr>
																<td width=35%>
																	<div class="ZFieldSubLabel"><fmt:message key='EmailAddress'/>:</div>
																</td>
																<td>
																	<input class="ZField" type="text" id="replyTo" name="replyTo" value="${bean.replyTo}" onkeypress='zd.markElementAsManuallyChanged(this)'>
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</c:if>
										</c:if>
										<tr>
											<td colspan=2 class="ZRowPadding"></td>
										</tr>
										<tr>
											<td colspan=2>
												<div class="ZFieldLabel ZSectionLabel">
													<fmt:message key='SyncOptions'/>
													<hr class="ZHorizontalLine"/>
												</div>
											</td>
										</tr>
										<tr>
											<td>
												<div class="ZFieldLabel"><fmt:message key='SyncFrequency'/>:</div>
											</td>
											<td>
												<select class="ZSelect" id="syncFreqSecs" name="syncFreqSecs">
													<option value="-1" ${bean.syncFreqSecs == -1 ? 'selected' : ''}><fmt:message key='SyncManually'/></option>
													<c:if test="${bean.type eq 'zimbra' or bean.type eq 'xsync'}">
														<option value="0" ${bean.syncFreqSecs == 0 ? 'selected' : ''}><fmt:message key='SyncNewArrive'/></option>
													</c:if>
													<option value="60" ${bean.syncFreqSecs == 60 ? 'selected' : ''}><fmt:message key='SyncEveryMin'/></option>
													<option value="300" ${bean.syncFreqSecs == 300 ? 'selected' : ''}><fmt:message key='SyncEvery5'/></option>
													<option value="900" ${bean.syncFreqSecs == 900 ? 'selected' : ''}><fmt:message key='SyncEvery15'/></option>
													<option value="1800" ${bean.syncFreqSecs == 1800 ? 'selected' : ''}><fmt:message key='SyncEvery30'/></option>
													<option value="3600" ${bean.syncFreqSecs == 3600 ? 'selected' : ''}><fmt:message key='SyncEvery1Hr'/></option>
													<option value="14400" ${bean.syncFreqSecs == 14400 ? 'selected' : ''}><fmt:message key='SyncEvery4Hr'/></option>
													<option value="43200" ${bean.syncFreqSecs == 43200 ? 'selected' : ''}><fmt:message key='SyncEvery12Hr'/></option>
												</select>
											</td>
										</tr>
										<c:if test="${bean.type eq 'pop3'}">
											<tr id="popSettingsRow">
												<td>
													<div class="ZFieldLabel"><fmt:message key='SyncMsgs'/>:</div>
												</td>
												<td>
													<table cellpadding=0 cellspacing=0 width=85% class="ZTableInner">
														<tr>
															<td>
																<input type="radio" id="leaveOnServer" name="leaveOnServer" ${bean.leaveOnServer ? '' : 'checked'} value="false">
																<label class="ZRadioLabel" "for=leaveOnServer"><fmt:message key='SyncMsgsDelete'/></label>
															</td>
															<td>
																<input type="radio" id="leaveOnServer" name="leaveOnServer" ${bean.leaveOnServer ? 'checked' : ''} value="true">
																<label class="ZRadioLabel" for="leaveOnServer"><fmt:message key='SyncMsgsLeave'/></label>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</c:if>
										<c:if test="${bean.calendarSyncSupported}">
											<tr id="syncCalendarRow">
												<td><div class="ZFieldLabel"></div></td>
												<td>
													<input type="checkbox" id="calendarSyncEnabled" name="calendarSyncEnabled" ${bean.calendarSyncEnabled ? 'checked' : ''}>
													<label class="ZCheckboxLabel" for="calendarSyncEnabled"><fmt:message key='SyncCalendarInfo'/></label>
												</td>
											</tr>
										</c:if>
										<c:if test="${bean.contactSyncSupported}">
											<tr id="syncContactsRow" >
												<td><div class="ZFieldLabel"></div></td>
												<td>
													<input type="checkbox" id="contactSyncEnabled" name="contactSyncEnabled" ${bean.contactSyncEnabled ? 'checked' : ''}>
													<label class="ZCheckboxLabel" for="contactSyncEnabled"><fmt:message key='SyncContactsInfo'/></label>
												</td>
											</tr>
										</c:if>
										<c:if test="${not empty bean.accountId}">
											<tr id="debugTraceRow">
												<td></td>
												<td>
													<input type="checkbox" id="debugTraceEnabled" name="debugTraceEnabled" ${bean.debugTraceEnabled ? 'checked' : ''}>
													<label class="ZCheckboxLabel" for="debugTraceEnabled"><fmt:message key='EnableTrace'/></label>
												</td>
											</tr>
										</c:if>
										<tr><td></td></tr>
										<tr>
											<td colspan=2>
												<table border=0 cellpadding=0 cellspacing=0 width=100% class="ZTableInner">
													<tr>
														<c:if test="${accountFlavor ne ''}">
															<td>
																<table border=0>
																	<tr>
																		<td><div id="saveButton" class="ZPanelButton" onclick='OnSubmit()' onmouseover='zd.OnHover(this, true)' onmouseout='zd.OnHover(this)'><fmt:message key='${save}'/></div></td>
																	</tr>
																</table>
															</td>
														</c:if>
														<td align="right">
															<table border=0>
																<tr>
																	<td><div id="cancelButton" class="ZPanelButton ZCancel" onclick='OnCancel()' onmouseover='zd.OnHover(this, true)' onmouseout='zd.OnHover(this)'><fmt:message key='Cancel'/></div></td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</form>
								</c:if>
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

</body>
</html>
