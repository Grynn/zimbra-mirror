/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

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
	var authToken = AjxCookie.getCookie(document, ZmCsfeCommand._COOKIE_NAME)
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
		AjxCookie.setCookie(document, ZmCsfeCommand._COOKIE_NAME, authToken, exp, "/");
	} else {
		AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
	}
	if (sessionId)
		ZmCsfeCommand.setSessionId(sessionId);
}

ZmCsfeCommand.clearAuthToken =
function() {
	ZmCsfeCommand._authToken = null;
	AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
}

ZmCsfeCommand.getSessionId =
function() {
	return ZmCsfeCommand._sessionId;
}

ZmCsfeCommand.setSessionId =
function(sessionId) {
	var id = (sessionId instanceof Array) ? sessionId[0].id : sessionId;
	ZmCsfeCommand._sessionId = parseInt(id);
}

ZmCsfeCommand.invoke =
function(soapDoc, noAuthTokenRequired, serverUri, targetServer, useXml, noSession, changeToken) {
	var hdr = soapDoc.createHeaderElement();
	var context = soapDoc.set("context", null, hdr);
	context.setAttribute("xmlns", "urn:zimbra");
	if (noSession)
		soapDoc.set("nosession", null, context);
	var sessionId = ZmCsfeCommand.getSessionId();
	if (sessionId) {
		var si = soapDoc.set("sessionId", null, context);
		si.setAttribute("id", sessionId);
	}
	if (targetServer)
		soapDoc.set("targetServer", targetServer, context);
	if (changeToken) {
		var ct = soapDoc.set("change", null, context);
		ct.setAttribute("token", changeToken);
		ct.setAttribute("type", "new");
	}
	
	// See if we have an auth token, if not, then mock up and need to authenticate or just have no auth cookie
	if (!noAuthTokenRequired) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken)
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, "ZmCsfeCommand.invoke");
		soapDoc.set("authToken", authToken, context);
	}
	
	if (!useXml) {
		var js = soapDoc.set("format", null, context);
		js.setAttribute("type", "js");
	}

	DBG.println(AjxDebug.DBG1, "<H4>REQUEST</H4>");
	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	var xmlResponse = false;
	try {
		var uri = serverUri || ZmCsfeCommand.serverUri;
		var requestStr = !AjxEnv.isSafari 
			? soapDoc.getXml() 
			: soapDoc.getXml().replace("soap=", "xmlns:soap=");
			
		var _st = new Date();
		var response = AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
		var _en = new Date();
		DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (_en.getTime() - _st.getTime()));

		var respDoc = null;
		if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
			respDoc = response.text;
		} else {
			xmlResponse = true;
			// responseXML is empty under IE
			respDoc = (AjxEnv.isIE || response.xml == null)
				? AjxSoapDoc.createFromXml(response.text) 
				: AjxSoapDoc.createFromDom(response.xml);
		}
	} catch (ex) {
		if (ex instanceof AjxSoapException) {
			throw ex;
		} else if (ex instanceof AjxException) {
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
	
	DBG.println(AjxDebug.DBG1, "<H4>RESPONSE</H4>");

	var resp;
	if (xmlResponse) {
		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		var body = respDoc.getBody();
		var fault = AjxSoapDoc.element2FaultObj(body);
		if (fault) {
			throw new ZmCsfeException("Csfe service error", fault.errorCode, "ZmCsfeCommand.invoke", fault.reason);
		}
		if (useXml)
			return body;

		resp = "{";
		var hdr = respDoc.getHeader();
		if (hdr)
			resp += AjxUtil.xmlToJs(hdr) + ",";
		resp += AjxUtil.xmlToJs(body);
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
