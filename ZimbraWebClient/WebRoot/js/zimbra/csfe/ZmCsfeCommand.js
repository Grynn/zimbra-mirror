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

// Static properties

// Global settings for each CSFE command
ZmCsfeCommand._COOKIE_NAME = "ZM_AUTH_TOKEN";
ZmCsfeCommand._serverUri = null;
ZmCsfeCommand._authToken = null;
ZmCsfeCommand._sessionId = null;

// Static methods

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

ZmCsfeCommand.setServerUri =
function(uri) {
	ZmCsfeCommand._serverUri = uri;
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

/*
* Sends a SOAP request to the server and processes the response.
*
* @param soapDoc		[AjxSoapDoc]	The SOAP document that represents the request
* @param noAuthToken	[boolean]*		If true, the check for an auth token is skipped
* @param serverUri		[string]*		URI to send the request to
* @param targetServer	[string]*		Host that services the request
* @param useXml			[boolean]*		If true, an XML response is requested
* @param noSession		[boolean]*		If true, no session info is included
* @param changeToken	[string]*		Current change token
* @param callback		[AjxCallback]*	Callback to run when response is received (async mode)
*/
ZmCsfeCommand.prototype.invoke =
function(soapDoc, noAuthToken, serverUri, targetServer, useXml, noSession, changeToken, callback) {

	// Add the SOAP header and context
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
	
	// Get auth token from cookie if required
	if (!noAuthToken) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken)
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, "ZmCsfeCommand.invoke");
		soapDoc.set("authToken", authToken, context);
	}
	
	// Tell server what kind of response we want
	this._useXml = useXml;
	if (!useXml) {
		var js = soapDoc.set("format", null, context);
		js.setAttribute("type", "js");
	}

	var asyncMode = (callback != null);

	DBG.println(AjxDebug.DBG1, asyncMode ? "<H4>REQUEST (asynchronous)</H4>" : "<H4>REQUEST</H4>");
	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	try {
		var uri = serverUri || ZmCsfeCommand._serverUri;
		var requestStr = soapDoc.getXml();
		if (AjxEnv.isSafari)
			requestStr = requestStr.replace("soap=", "xmlns:soap=");
			
		this._st = new Date();
		
		if (asyncMode) {
			var rpcCallback = new AjxCallback(this, this._runCallback, callback);
			this._rpcId = AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"}, rpcCallback);
		} else {
			var response = AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
			return this._getResponseData(response, false);
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
}

/*
* Takes the response to an RPC request and returns a JS object with the response data.
*
* @param response	[Object]	RPC response with properties "text" and "xml"
* @param asyncMode	[boolean]	true if we're in asynchronous mode
*/
ZmCsfeCommand.prototype._getResponseData =
function(response, asyncMode) {
	this._en = new Date();
	DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (this._en.getTime() - this._st.getTime()));

	var xmlResponse = false;
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
	
	DBG.println(AjxDebug.DBG1, asyncMode ? "<H4>RESPONSE (asynchronous)</H4>" : "<H4>RESPONSE</H4>");

	var resp;
	if (xmlResponse) {
		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		var body = respDoc.getBody();
		var fault = AjxSoapDoc.element2FaultObj(body);
		if (fault) {
			var ex = new ZmCsfeException("Csfe service error", fault.errorCode, "ZmCsfeCommand.prototype.invoke", fault.reason);
			if (asyncMode)
				return ex;
			else
				throw ex;
		}
		if (this._useXml)
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
	DBG.dumpObj(AjxDebug.DBG1, data, -1);

	var fault = data.Body.Fault;
	if (fault) {
		var ex = new ZmCsfeException(fault.Reason.Text, fault.Detail.Error.Code, "ZmCsfeCommand.prototype.invoke", fault.Code.Value);
		if (asyncMode)
			return ex;
		else
			throw ex;
	}
	
	if (data.Header && data.Header.context && data.Header.context.sessionId)
		ZmCsfeCommand.setSessionId(data.Header.context.sessionId);

	return data;
}

/*
* Runs the callback that was passed to invoke() for an async command.
*
* @param callback	[AjxCallback]	Callback to run with response data
* @param response	[Object]		RPC response object
*/
ZmCsfeCommand.prototype._runCallback =
function(args) {

	var callback = args[0];
	var response = args[1];

	var respData = this._getResponseData(response, true);
	this._en = new Date();

	if (!callback) {
		DBG.println(AjxDebug.DBG1, "Could not find callback!");
		return;
	}

	callback.run(respData);
}

// DEPRECATED - instead, use instance method invoke() above
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
		var uri = serverUri || ZmCsfeCommand._serverUri;
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
