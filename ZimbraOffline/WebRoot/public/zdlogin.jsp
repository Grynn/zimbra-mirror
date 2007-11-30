<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.zimbra.cs.account.Provisioning" %>
<%@ page import="com.zimbra.cs.account.Account" %>
<%@ page import="com.zimbra.cs.zclient.ZMailbox" %>
<%@ page import="com.zimbra.cs.account.soap.SoapProvisioning" %>
<%@ page import="com.zimbra.cs.servlet.ZimbraServlet" %>
<%@ page import="com.zimbra.common.service.ServiceException" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%!
    private final String LOCALHOST_URL = "http://localhost:7633/zimbra/";
    private final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + ZimbraServlet.ADMIN_SERVICE_URI;
%>
<%

    try {
        request.setAttribute("password", "anythingisfine");
        request.setAttribute("loginOp","login");
        request.setAttribute("zrememberme","1");

    }catch(Exception e) {
        response.sendRedirect(LOCALHOST_URL);
    }
%>

<c:set var="prefsToFetch" value="zimbraPrefSkin,zimbraPrefClientType,zimbraPrefLocale"/>
<c:set var="attrsToFetch" value="zimbraFeatureMailEnabled,zimbraFeatureCalendarEnabled,zimbraFeatureContactsEnabled,zimbraFeatureIMEnabled,zimbraFeatureNotebookEnabled,zimbraFeatureOptionsEnabled,zimbraFeaturePortalEnabled,zimbraFeatureTasksEnabled,zimbraFeatureVoiceEnabled,zimbraFeatureBriefcasesEnabled,zimbraFeatureMailUpsellEnabled,zimbraFeatureContactsUpsellEnabled,zimbraFeatureCalendarUpsellEnabled,zimbraFeatureVoiceUpsellEnabled"/>

<c:catch var="loginException">
    <c:choose>
        <c:when test="${(requestScope.loginOp eq 'login') && !(empty requestScope.username) && !(empty requestScope.password)}">
            <zm:login username="${requestScope.username}" password="${requestScope.password}" varRedirectUrl="postLoginUrl" varAuthResult="authResult" rememberme="${requestScope.zrememberme == '1'}"
                    prefs="${prefsToFetch}" attrs="${attrsToFetch}"
                    requestedSkin="${param.skin}"/>
	    </c:when>
        <c:otherwise>
	        <%-- try and use existing cookie if possible --%>
	        <c:set var="authtoken" value="${not empty param.zauthtoken ? param.zauthtoken : cookie.ZM_AUTH_TOKEN.value}"/>
	        <c:if test="${not empty authtoken}">
	            <zm:login authtoken="${authtoken}" authtokenInUrl="${not empty param.zauthtoken}"
	                      varRedirectUrl="postLoginUrl" varAuthResult="authResult"
	                      rememberme="${param.zrememberme == '1'}"
                          prefs="${prefsToFetch}" attrs="${attrsToFetch}"
                          requestedSkin="${param.skin}"/>
	            <%-- continue on at not empty authResult test --%>
	        </c:if>
	    </c:otherwise>
    </c:choose>
</c:catch>

<c:if test="${empty authResult}">
    <c:redirect url="http://localhost:7633/zimbra/"/>    
</c:if>
<c:if test="${not empty authResult}">
    <c:choose>
        <c:when test="${not empty postLoginUrl}">
            <c:redirect url="${postLoginUrl}"/>
        </c:when>
        <c:otherwise>
            <c:set var="client" value="${param.client}"/>
            <c:if test="${empty client and useMobile}"><c:set var="client" value="mobile"/></c:if> 
            <c:if test="${empty client or client eq 'preferred'}">
                <c:set var="client" value="${requestScope.authResult.prefs.zimbraPrefClientType[0]}"/>
            </c:if>
            <c:choose>
                <c:when test="${client eq 'advanced'}">
                    <jsp:forward page="/public/launchZCS.jsp"/>
                </c:when>
                <c:when test="${client eq 'standard'}">
                    <c:redirect url="/h/search?mesg=welcome&initial=true">
                        <c:forEach var="p" items="${paramValues}">
                            <c:forEach var='value' items='${p.value}'>
                                <c:if test="${not fn:contains(ignoredQueryParams, p.key)}">
                                    <c:param name="${p.key}" value='${value}'/>
                                </c:if>
                            </c:forEach>
                        </c:forEach>
                    </c:redirect>
                </c:when>
                <c:when test="${client eq 'mobile'}">
                    <c:redirect url="/m/main">
                            <c:forEach var="p" items="${paramValues}">
                                <c:forEach var='value' items='${p.value}'>
                                    <c:if test="${not fn:contains(ignoredQueryParams, p.key)}">
                                        <c:param name="${p.key}" value='${value}'/>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                    </c:redirect>
                </c:when>
                <c:otherwise>
                    <jsp:forward page="/public/launchZCS.jsp"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:if>