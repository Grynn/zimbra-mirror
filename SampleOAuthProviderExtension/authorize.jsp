<%@ page buffer="8kb" autoFlush="true" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlclient" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<fmt:setLocale value='${pageContext.request.locale}' scope='request' />
<fmt:setBundle basename="/messages/ZmMsg" scope="request"/>
<fmt:setBundle basename="/messages/ZMsg" var="zmsg" scope="request"/>

<%-- query params to ignore when constructing form port url or redirect url --%>
<c:set var="ignoredQueryParams" value="loginOp,loginNewPassword,loginConfirmNewPassword,loginErrorCode,username,password,zrememberme,zlastserver,client"/>
<c:set var="prefsToFetch" value="zimbraPrefSkin,zimbraPrefClientType,zimbraPrefLocale,zimbraPrefMailItemsPerPage,zimbraPrefGroupMailBy,zimbraPrefAdvancedClientEnforceMinDisplay"/>
<c:set var="attrsToFetch" value="zimbraFeatureMailEnabled,zimbraFeatureCalendarEnabled,zimbraFeatureContactsEnabled,zimbraFeatureIMEnabled,zimbraFeatureNotebookEnabled,zimbraFeatureOptionsEnabled,zimbraFeaturePortalEnabled,zimbraFeatureTasksEnabled,zimbraFeatureVoiceEnabled,zimbraFeatureBriefcasesEnabled,zimbraFeatureMailUpsellEnabled,zimbraFeatureContactsUpsellEnabled,zimbraFeatureCalendarUpsellEnabled,zimbraFeatureVoiceUpsellEnabled,zimbraFeatureConversationsEnabled"/>

<%
    String appDesc = (String)request.getAttribute("CONS_DESC");
	if(appDesc==null){
	        appDesc = request.getParameter("CONS_DESC");
			        }
    String token = (String)request.getAttribute("TOKEN");
	if(token==null){
		token = request.getParameter("oauth_token");
		}
%>

<c:catch var="loginException">
        <c:if test="${(param.loginOp eq 'login') && !(empty param.username) && !(empty param.password)}">
		    <c:choose>
	        	<c:when test="${!empty cookie.ZM_TEST}">
		            <zm:login username="${param.username}" password="${param.password}" varRedirectUrl="postLoginUrl" varAuthResult="authResult"
		                      newpassword="${param.loginNewPassword}" rememberme="false"
		                      prefs="${prefsToFetch}" attrs="${attrsToFetch}"
							  requestedSkin="${param.skin}"/>
		            <%-- continue on at not empty authResult test --%>
		    	</c:when>
		        <c:otherwise>
		            <c:set var="errorCode" value="noCookies"/>
		            <fmt:message var="errorMessage" key="errorCookiesDisabled"/>
		        </c:otherwise>
		    </c:choose>
	    </c:if>
</c:catch>

<c:if test="${not empty authResult}">
	  <%
	  com.zimbra.cs.zclient.ZAuthResult zar = (com.zimbra.cs.zclient.ZAuthResult) pageContext.findAttribute("authResult");
	  com.zimbra.common.auth.ZAuthToken zat = (com.zimbra.common.auth.ZAuthToken) zar.getAuthToken();

	  request.setAttribute("ZM_AUTH_TOKEN",zat.getValue());
	  out.clear();
	  application.getContext("/service").getRequestDispatcher("/extension/oauth/authorization").forward(request, response);
	  %>
</c:if>

<c:if test="${loginException != null}">
    <zm:getException var="error" exception="${loginException}"/>
    <c:set var="errorCode" value="${error.code}"/>
    <fmt:message bundle="${zmsg}" var="errorMessage" key="${errorCode}"/>
</c:if>

<c:url var="formActionUrl" value="/">
    <c:forEach var="p" items="${paramValues}">
        <c:forEach var='value' items='${p.value}'>
            <c:if test="${not fn:contains(ignoredQueryParams, p.key)}">
                <c:param name="${p.key}" value='${value}'/>
            </c:if>
        </c:forEach>
    </c:forEach>
</c:url>

<%
	Cookie testCookie = new Cookie("ZM_TEST", "true");
	testCookie.setSecure(com.zimbra.cs.taglib.ZJspSession.secureAuthTokenCookie(request));
	response.addCookie(testCookie);
%>


<html>
<head>
<!--
 login.jsp
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <title><fmt:message key="zimbraLoginTitle"/></title>
    <app:skin />
    <c:set var="version" value="${initParam.zimbraCacheBusterVersion}"/>
    <meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=8.0; user-scalable=1;">
    <meta name="description" content="<fmt:message key="zimbraLoginMetaDesc"/>">
    <link  rel="stylesheet" type="text/css" href="<c:url value='/css/common,login,zhtml,skin.css'>
		<c:param name="skin"	value="${skin}" />
		<c:param name="v"		value="${version}" />
	</c:url>">
    <fmt:message key="favIconUrl" var="favIconUrl"/>
    <link rel="SHORTCUT ICON" href="<c:url value='${favIconUrl}'/>">
</head>
<c:set value="/img" var="iconPath" scope="request"/>
<body>
<table width="100%" style="height:100%;">
    <tr>
        <td align="center" valign="middle">
            <div id="ZloginPanel">
                <table width="100%">
                    <tr>
                        <td>
                            <table width="100%">
                                <tr>
                                    <td align="center" valign="middle">
                                        <a href="http://www.zimbra.com/" id="bannerLink" target="_new"><span style="cursor:pointer;display:block;" class="ImgLoginBanner"></span></a>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div id="ZLoginAppName"><fmt:message key="splashScreenAppName"/></div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td id="ZloginBodyContainer">
                            <c:if test="${errorCode != null}">
                                <!-- ${fn:escapeXml(error.stackStrace)} -->
                                <div id="ZloginErrorPanel">
                                    <table width="100%">
                                        <tr>
                                            <td valign="top" width="40">
                                                <img alt='<fmt:message key="ALT_ERROR"/>' src="<app:imgurl value='dwt/ImgCritical_32.gif'/>"/>
                                            </td>
                                            <td class="errorText">
                                                <c:out value="${errorMessage}"/>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </c:if>

                            <div id="ZloginFormPanel">
                                <form method="post" name="loginForm" action="/zimbra/public/authorize.jsp">
                                    <input type="hidden" name="loginOp" value="login"/>
									<input type="hidden" name="oauth_token" value="<%= token %>"/>
									<input type="hidden" name="CONS_DESC" value="<%= appDesc %>"/>
									<table width="100%" cellpadding="4">
										<tr>
						                   <td align="center">
						                     <% out.println(appDesc); %>
					                       </td>
						               </tr>
									</table>
                                    <table width="100%" cellpadding="4">
                                        <tr>
                                            <td class="zLoginLabelContainer"><label for="username"><fmt:message key="username"/>:</label></td>
                                            <td colspan="2" class="zLoginFieldContainer">
                                                <input id="username" class="zLoginField" name="username" type="text" value="${fn:escapeXml(param.username)}" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="zLoginLabelContainer"><label for="password"><fmt:message key="password"/>:</label></td>
                                            <td colspan="2" class="zLoginFieldContainer">
                                                <input id="password" class="zLoginField" name="password" type="password" value="${fn:escapeXml(param.password)}"/>
                                            </td>
                                        </tr>
										<tr>
                                            <td class="zLoginLabelContainer"></td>
                                            <td align=right><input type="submit" class="zLoginButton"
                                                       value="<fmt:message key="login"/>"/></td>
                                        </tr>
                                    </table>
                                </form>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </td>
    </tr>
</table>
<script>
  <jsp:include page="/js/skin.js">
    <jsp:param name="templates" value="false" />
    <jsp:param name="client" value="advanced" />
    <jsp:param name='servlet-path' value='/js/skin.js' />
  </jsp:include>
  var link = document.getElementById("bannerLink");
  if (link) {
    link.href = skin.hints.banner.url;
  }
</script>
</body>
</html>
