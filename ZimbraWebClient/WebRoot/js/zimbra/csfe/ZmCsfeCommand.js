function ZmCsfeCommand() {
}

ZmCsfeCommand._COOKIE_NAME = "LS_AUTH_TOKEN";

// All the cache and context stuff is to support async calls in the future
ZmCsfeCommand.serverUri = null;
ZmCsfeCommand._authToken = null;
ZmCsfeCommand._sessionId = null;

ZmCsfeCommand.getAuthToken =
function() {
	// See if the auth token is cached. If not try and get it from the cookie
	if (ZmCsfeCommand._authToken != null)
		return ZmCsfeCommand._authToken;
	var authToken = LsCookie.getCookie(document, ZmCsfeCommand._COOKIE_NAME)
	ZmCsfeCommand._authToken = authToken;
	return authToken;
}

ZmCsfeCommand.setCookieName =
function(cookieName) {
	ZmCsfeCommand._COOKIE_NAME = cookieName;
}

ZmCsfeCommand.setAuthToken =
function(authToken, lifetimeMs, sessionId) {
	ZmCsfeCommand._authToken = authToken;
	if (lifetimeMs != null) {
		var exp = new Date();
		var lifetime = parseInt(lifetimeMs);
		exp.setTime(exp.getTime() + lifetime);
		LsCookie.setCookie(document, ZmCsfeCommand._COOKIE_NAME, authToken, exp, "/");
	} else {
		LsCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
	}
	if (sessionId)
		ZmCsfeCommand.setSessionId(sessionId);
}

ZmCsfeCommand.clearAuthToken =
function() {
	ZmCsfeCommand._authToken = null;
	LsCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
}

ZmCsfeCommand.getSessionId =
function() {
	return ZmCsfeCommand._sessionId;
}

ZmCsfeCommand.setSessionId =
function(id) {
	ZmCsfeCommand._sessionId = id;
}

ZmCsfeCommand.invoke =
function(soapDoc, noAuthTokenRequired, serverUri, targetServer, useXml) {
	// See if we have an auth token, if not, then mock up and need to authenticate or just have no auth cookie
	if (!noAuthTokenRequired) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken)
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, "ZmCsfeCommand.invoke");
		var sessionId = ZmCsfeCommand.getSessionId();
		var hdr = soapDoc.createHeaderElement();
		var ctxt = soapDoc.set("context", null, hdr);
		ctxt.setAttribute("xmlns", "urn:liquid");
		soapDoc.set("authToken", authToken, ctxt);
		if (sessionId)
			soapDoc.set("sessionId", sessionId, ctxt);
		if (targetServer)
			soapDoc.set("targetServer", targetServer, ctxt);
	}
	
	if (!useXml) {
		var js = soapDoc.set("format", null, ctxt);
		js.setAttribute("type", "js");
	}

	DBG.println(LsDebug.DBG1, "<H4>REQUEST</H4>");
	DBG.printXML(LsDebug.DBG1, soapDoc.getXml());

	var xmlResponse = false;
	try {
		var uri = serverUri || ZmCsfeCommand.serverUri;
		var requestStr = !LsEnv.isSafari 
			? soapDoc.getXml() 
			: soapDoc.getXml().replace("soap=", "xmlns:soap=");
			
		var _st = new Date();
		var response = LsRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
		var _en = new Date();
		DBG.println(LsDebug.DBG1, "ROUND TRIP TIME: " + (_en.getTime() - _st.getTime()));

		var respDoc = null;
		if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
			respDoc = response.text;
		} else {
			xmlResponse = true;
			// responseXML is empty under IE
			respDoc = (LsEnv.isIE || response.xml == null)
				? LsSoapDoc.createFromXml(response.text) 
				: LsSoapDoc.createFromDom(response.xml);
		}
	} catch (ex) {
		if (ex instanceof LsSoapException) {
			throw ex;
		} else if (ex instanceof LsException) {
			throw ex; 
		}  else {
			var newEx = new ZmCsfeException();
			newEx.method = "ZmCsfeCommand.invoke";
			newEx.detail = ex.toString();
			newEx.code = ZmCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
			throw newEx;
		}
	}
	
	DBG.println(LsDebug.DBG1, "<H4>RESPONSE</H4>");

	var resp;
	if (xmlResponse) {
		DBG.printXML(LsDebug.DBG1, respDoc.getXml());
		var body = respDoc.getBody();
		var fault = LsSoapDoc.element2FaultObj(body);
		if (fault) {
			throw new ZmCsfeException("Csfe service error", fault.errorCode, "ZmCsfeCommand.invoke", fault.reason);
		}
		if (useXml)
			return body;

		resp = "{";
		var hdr = respDoc.getHeader();
		if (hdr)
			resp += LsUtil.xmlToJs(hdr) + ",";
		resp += LsUtil.xmlToJs(body);
		resp += "}";
	} else {
		resp = respDoc;	
	}

	var data = new Object();
	eval("data=" + resp);
	DBG.dumpObj(data, -1);

	var fault = data.Body.Fault;
	if (fault)
		throw new ZmCsfeException(fault.Reason.Text, fault.Detail.Error.Code, "ZmCsfeCommand.invoke", fault.Code.Value);
	if (data.Header && data.Header.context && data.Header.context.sessionId)
		ZmCsfeCommand.setSessionId(data.Header.context.sessionId);

	return data;
}

ZmCsfeCommand.setServerUri =
function(uri) {
	ZmCsfeCommand.serverUri = uri;
}
