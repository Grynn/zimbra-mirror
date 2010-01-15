<%@ page buffer="8kb" session="false" autoFlush="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page session="false" %>
<%@ page import="java.util.Locale,com.zimbra.cs.zclient.ZAuthResult" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zd" tagdir="/WEB-INF/tags/desktop" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<zd:auth/>

<%!
    static String getParameter(HttpServletRequest request, String pname, String defValue) {
	String value = request.getParameter(pname);
	return value != null ? value : defValue;
    }

    static String getAttribute(HttpServletRequest request, String aname, String defValue) {
	Object object = request.getAttribute(aname);
	String value = object != null ? String.valueOf(object) : null;
	return value != null ? value : defValue;
    }
%>
<%
    String contextPath = request.getContextPath();
    if (contextPath.equals("/")) {
	contextPath = "";
    }

    ZAuthResult authResult = (ZAuthResult) request.getAttribute("authResult");

    response.setHeader("Expires", "Tue, 24 Jan 2000 17:46:50 GMT");
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<!--
 launchZD.jsp
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%
    java.util.List<String> localePref = authResult.getPrefs().get("zimbraPrefLocale");
    if (localePref != null && localePref.size() > 0)
	request.setAttribute("localeId", localePref.get(0));

    boolean isDev = getParameter(request, "dev", "0").equals("1");
    if (isDev) {
	if (request.getAttribute("debug") == null)
	    request.setAttribute("debug", "1");
	request.setAttribute("fileExtension", "");
	request.setAttribute("gzip", "false");
	request.setAttribute("mode", "mjsf");
	request.setAttribute("packages", "dev");
    }

    String debug = getParameter(request, "debug", getAttribute(request, "debug", null));
    String extraPackages = getParameter(request, "packages", getAttribute(request, "packages", null));
    String mode = getAttribute(request, "mode", null);
    boolean isDevMode = mode != null && mode.equalsIgnoreCase("mjsf");
    boolean isOfflineMode = true;
    boolean isSkinDebugMode = mode != null && mode.equalsIgnoreCase("skindebug");
    boolean isDebug = isSkinDebugMode || isDevMode;

    String editor = getParameter(request, "editor", "");
    String ext = getAttribute(request, "fileExtension", null);
    String lang = "";
    Locale locale = request.getLocale();
    String localeId = getAttribute(request, "localeId", null);
    String prodMode = getAttribute(request, "prodMode", "");
    String res;
    String skin = authResult.getSkin();
    String vers = getAttribute(request, "version", "");

    if (ext == null || isDevMode)
	ext = "";
    if (localeId != null) {
	int index = localeId.indexOf("_");
	if (index == -1) {
	    lang = "&language=" + localeId;
	    locale = new Locale(localeId);
	} else {
	    String language = localeId.substring(0, index);
	    String country = localeId.substring(localeId.length() - 2);
	    lang = "&language=" + language + "&country=" + country;;
	    locale = new Locale(language, country);
	}
    }
    res = "?v=" + vers + (isDebug ? "&debug=1" : "") + lang + "&skin=" + skin + "&mode="+mode;

    pageContext.setAttribute("app", "");
    pageContext.setAttribute("contextPath", contextPath);
    pageContext.setAttribute("editor", editor);
    pageContext.setAttribute("ext", ext);
    pageContext.setAttribute("isDebug", isDebug);
    pageContext.setAttribute("isDevMode", isDev);
    pageContext.setAttribute("isOfflineMode", "true");
    pageContext.setAttribute("isProdMode", !prodMode.equals(""));
    pageContext.setAttribute("locale", locale);
    pageContext.setAttribute("res", res);
    pageContext.setAttribute("skin", skin);
    pageContext.setAttribute("vers", vers);
%>

<fmt:setLocale value='${locale}' scope='request' />
<fmt:setBundle basename="/messages/ZdMsg" scope="request" />

<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title><fmt:message key="ZimbraDesktop"/></title>
<link rel="stylesheet" href="<c:url value="/css/images,common,dwt,msgview,login,zm,wiki,skin.css">
    <c:param name="v" value="${vers}" />
    <c:param name="debug" value='${isDebug?"1":""}' />
    <c:param name="skin" value="${skin}" />
    <c:param name="locale" value="${locale}" />
</c:url>" type="text/css" />
<link rel="SHORTCUT ICON" href="<c:url value='/img/logo/favicon.ico'/>">
<script>
    appContextPath = "${zm:jsEncode(contextPath)}";
    appCurrentSkin = "${zm:jsEncode(skin)}";
    appExtension   = "${zm:jsEncode(ext)}";
    appDevMode     = ${isDevMode};
    isTinyMCE      = true;
</script>
</head>
<body>

<script type="text/javascript" src="/res/I18nMsg,AjxMsg,ZMsg,ZmMsg,AjxKeys,ZmKeys,ZdMsg,AjxTemplateMsg.js<%=res%>"></script>

<!-- image overlays and masks -->
<script>
<jsp:include page="/img/images.css.js" />
<jsp:include page="/skins/${skin}/img/images.css.js" />
    document.write("<DIV style='display:none'>");
    for (var id in AjxImgData) {
	var data = AjxImgData[id];
	if (data.f)
	    data.f = data.f.replace(/@AppContextPath@/, appContextPath);
	document.write("<IMG id='",id,"' src='",data.d||data.f,"'>");
    }
    document.write("</DIV>");
</script>

<jsp:include page="/html/skin.html">
    <jsp:param name="servlet-path" value="/html/skin.html" />
    <jsp:param name='client' value='advanced' />
    <jsp:param name='skin' value='${skin}' />
    <jsp:param name="locale" value="${locale}" />
    <jsp:param name='debug' value='${isDebug}' />
    <jsp:param name="customerDomain" value="${param.customerDomain}" />
</jsp:include>

<!-- bootstrap classes -->
<% if (isDevMode) { %>
    <jsp:include page="/public/Boot.jsp" />
<% } else { %>
<script type="text/javascript">
    <jsp:include>
	<jsp:attribute name='page'>/js/Boot_all.js</jsp:attribute>
    </jsp:include>
</script>
<% } %>

<script>
    AjxPackage.setBasePath("<%=contextPath%>/js");
    AjxPackage.setExtension("<%= isDevMode ? "" : "_all" %>.js");
    AjxPackage.setQueryString("v=<%=vers%>");
    AjxTemplate.setBasePath("<%=contextPath%>/templates");
    AjxTemplate.setExtension(".template.js");
    AjxEnv.DEFAULT_LOCALE = "${zm:javaLocaleId(locale)}";
</script>
<script>
<jsp:include page="/js/ajax/util/AjxTimezoneData.js" />
</script>
<%
    String allPackages = "Startup1_1,Startup1_2";
    if (extraPackages != null) {
	if (extraPackages.equals("dev")) {
	    extraPackages = "Startup2,CalendarCore,Calendar,CalendarAppt,ContactsCore,Contacts,IMCore,IM,MailCore,Mail,Mixed,NotebookCore,Notebook,BriefcaseCore,Briefcase,PreferencesCore,Preferences,TasksCore,Tasks,Browse,Extras,Share,Zimlet,ZimletApp,Alert,ImportExport,BrowserPlus";
	}
	allPackages += "," + extraPackages;
    }

    String pprefix = isDevMode ? "public/jsp" : "js";
    String psuffix = isDevMode ? ".jsp" : "_all.js";
    String[] pnames = allPackages.split(",");

    for (String pname : pnames) {
        String pageurl = "/" + pprefix + "/" + pname + psuffix;
	pageContext.setAttribute("pageurl", pageurl);
	if (isDevMode) { %>
            <jsp:include page='${pageurl}' />
     <% } else { %>
<script src="${contextPath}${pageurl}${ext}?v=${vers}"></script>
     <% } %>
<%  }
%>

<script type="text/javascript">
<jsp:include page='/js/skin.js'>
    <jsp:param name='servlet-path' value='/js/skin.js' />
    <jsp:param name='client' value='advanced' />
    <jsp:param name='skin' value='${skin}' />
    <jsp:param name="locale" value="${locale}" />
    <jsp:param name='debug' value='${isDebug}' />
    <jsp:param name="templates" value="split" />
</jsp:include>
</script>

<c:if test="${not requestScope['skin.templates.included']}">
<script type="text/javascript" src="<c:url value='/js/skin.js'>
    <c:param name='client' value='advanced' />
    <c:param name='skin' value='${skin}' />
    <c:param name="locale" value="${locale}" />
    <c:param name='debug' value='${isDebug}' />
    <c:param name="templates" value="only" />
    <c:param name="v" value="${vers}" />
    </c:url>">
</script>
</c:if>

<script>
    // compile locale specific templates
    for (var pkg in window.AjxTemplateMsg) {
	var text = AjxTemplateMsg[pkg];
	AjxTemplate.compile(pkg, true, true, text);
    }
</script>

<script>
    var cacheKillerVersion = "${zm:jsEncode(vers)}";

    function launch() {
	// quit if this function has already been called
	if (arguments.callee.done)
	    return;
	arguments.callee.done = true;

	if (_timer) {
	    clearInterval(_timer);
	    _timer = null;
	}

	var prodMode = ${isProdMode};
	var debugLevel = "<%= (debug != null) ? debug : "" %>";

	if (!prodMode || debugLevel) {
	    AjxDispatcher.require("Debug");
	    DBG = new AjxDebug(AjxDebug.NONE, null, false);
	    // figure out the debug level
	    if (debugLevel == 't') {
		DBG.showTiming(true);
	    } else {
		DBG.setDebugLevel(debugLevel);
	    }
	}

	AjxHistoryMgr.BLANK_FILE = "${contextPath}/public/blankHistory.html";

<c:set var="types" value="${requestScope.authResult.attrs.zimbraFeatureConversationsEnabled[0] eq 'FALSE' ? 'message' : requestScope.authResult.prefs.zimbraPrefGroupMailBy[0]}"/>
<zm:getInfoJSON var="getInfoJSON" authtoken="${requestScope.authResult.authToken}" dosearch="false" itemsperpage="50" types="${types}"/>
        var batchInfoResponse = ${getInfoJSON};

<c:if test="${editor eq 'tinymce'}">
        window.isTinyMCE = true; 
</c:if>
	var settings = {
	    "dummy":1<c:forEach var="pref" items="${requestScope.authResult.prefs}">,
	    "${pref.key}":"${zm:jsEncode(pref.value[0])}"</c:forEach>
<c:forEach var="attr" items="${requestScope.authResult.attrs}">,
	    "${attr.key}":"${zm:jsEncode(attr.value[0])}"
</c:forEach>
	};
	var params = {
	    settings:settings, batchInfoResponse:batchInfoResponse,
	    devMode:${isDevMode}, offlineMode:${isOfflineMode}
	};
	ZmZimbraMail.run(params);
    }

    // Mozilla and Opera 9 expose the event we could use
    if (document.addEventListener) {
        document.addEventListener("DOMContentLoaded", launch, null);

        //	mainly for Opera 8.5, won't be fired if DOMContentLoaded fired already.
        document.addEventListener("load", launch, null);
    }

    // 	for Internet Explorer. readyState will not be achieved on init call
    if (AjxEnv.isIE && AjxEnv.isWindows) {
        document.attachEvent("onreadystatechange", function(e) {
            if (document.readyState == "complete")
                launch();
        });
    }

    if (/(WebKit|khtml)/i.test(navigator.userAgent)) {
        var _timer = setInterval(function() {
            if (/loaded|complete/.test(document.readyState))
                launch();
        }, 10);
    }

    AjxCore.addOnloadListener(launch);
    AjxCore.addOnunloadListener(ZmZimbraMail.unload);
</script>
</body>
</html>

