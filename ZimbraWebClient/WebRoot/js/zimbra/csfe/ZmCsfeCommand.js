/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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
 */

ZmCsfeCommand = function() {
};

// Static properties

// Global settings for each CSFE command
ZmCsfeCommand._COOKIE_NAME = "ZM_AUTH_TOKEN";
ZmCsfeCommand.serverUri = null;
ZmCsfeCommand._sessionId = null;

// Static methods

ZmCsfeCommand.getAuthToken =
function() {
	return AjxCookie.getCookie(document, ZmCsfeCommand._COOKIE_NAME)
};

ZmCsfeCommand.setCookieName =
function(cookieName) {
	ZmCsfeCommand._COOKIE_NAME = cookieName;
};

ZmCsfeCommand.setServerUri =
function(uri) {
	ZmCsfeCommand.serverUri = uri;
};

ZmCsfeCommand.setAuthToken =
function(authToken, lifetimeMs, sessionId) {
	if (lifetimeMs != null) {
		var exp = null;
		if(lifetimeMs > 0) {
			exp = new Date();
			var lifetime = parseInt(lifetimeMs);
			exp.setTime(exp.getTime() + lifetime);
		}
		AjxCookie.setCookie(document, ZmCsfeCommand._COOKIE_NAME, authToken, exp, "/");		
	} else {
		AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
	}
	if (sessionId) {
		ZmCsfeCommand.setSessionId(sessionId);
	}
};

ZmCsfeCommand.clearAuthToken =
function() {
	AjxCookie.deleteCookie(document, ZmCsfeCommand._COOKIE_NAME, "/");
};

ZmCsfeCommand.getSessionId =
function() {
	return ZmCsfeCommand._sessionId;
};

ZmCsfeCommand.setSessionId =
function(sessionId) {
	var id = (sessionId instanceof Array) ? sessionId[0].id : sessionId;
	ZmCsfeCommand._sessionId = parseInt(id);
};

ZmCsfeCommand.faultToEx =
function(fault, method) {
	var faultCode = AjxStringUtil.getAsString(fault.Code.Value);
	var errorCode = AjxStringUtil.getAsString(fault.Detail.Error.Code);
	var msg = AjxStringUtil.getAsString(fault.Reason.Text);
	return new ZmCsfeException(msg, errorCode, method, faultCode, fault.Detail.Error.a);
};

/**
 * Sends a SOAP request to the server and processes the response.
 *
 * @param params			[hash]			hash of params:
 *        soapDoc			[AjxSoapDoc]	The SOAP document that represents the request
 *        noAuthToken		[boolean]*		If true, the check for an auth token is skipped
 *        serverUri			[string]*		URI to send the request to
 *        targetServer		[string]*		Host that services the request
 *        useXml			[boolean]*		If true, an XML response is requested
 *        noSession			[boolean]*		If true, no session info is included
 *        changeToken		[string]*		Current change token
 *        highestNotifySeen [int]*  	     Sequence # of the highest notification we have processed
 *        asyncMode			[boolean]*		If true, request sent asynchronously
 *        callback			[AjxCallback]*	Callback to run when response is received (async mode)
 *        logRequest		[boolean]*		If true, SOAP command name is appended to server URL
 *        accountId			[string]*		ID of account to execute on behalf of
 *        accountName		[string]*		name of account to execute on behalf of
 *        skipAuthCheck		[boolean]*		don't check if auth token has changed
 */
ZmCsfeCommand.prototype.invoke =
function(params) {

	if (!params.soapDoc) return;

	var soapDoc = params.soapDoc;
	// Add the SOAP header and context
	var hdr = soapDoc.createHeaderElement();
	var context = soapDoc.set("context", null, hdr, "urn:zimbra");

	var ua = soapDoc.set("userAgent", null, context);
	var name = ["ZimbraWebClient - ", AjxEnv.browser, " (", AjxEnv.platform, ")"].join("");
	ua.setAttribute("name", name);
	if (ZmCsfeCommand.clientVersion) {
		ua.setAttribute("version", ZmCsfeCommand.clientVersion);
	}

	if (params.noSession) {
		soapDoc.set("nosession", null, context);
	}
	var sessionId = ZmCsfeCommand.getSessionId();
	if (sessionId) {
		var si = soapDoc.set("sessionId", null, context);
		si.setAttribute("id", sessionId);
	}
	if (params.targetServer) {
		soapDoc.set("targetServer", params.targetServer, context);
	}
	if (params.highestNotifySeen) {
	  	var notify = soapDoc.set("notify", null, context);
	  	notify.setAttribute("seq", params.highestNotifySeen);
	}
	if (params.changeToken) {
		var ct = soapDoc.set("change", null, context);
		ct.setAttribute("token", params.changeToken);
		ct.setAttribute("type", "new");
	}

	// if we're not checking auth token, we don't want token/acct mismatch	
	if (!params.skipAuthCheck) {
		if (params.accountId) {
			var acc = soapDoc.set("account", params.accountId, context);
			acc.setAttribute("by", "id");
		} else if (params.accountName) {
			var acc = soapDoc.set("account", params.accountName, context);
			acc.setAttribute("by", "name");
		}
	}
	
	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken) {
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, "ZmCsfeCommand.invoke");
		}
		if (ZmCsfeCommand._curAuthToken && !params.skipAuthCheck && (authToken != ZmCsfeCommand._curAuthToken)) {
			throw new ZmCsfeException("AuthToken has changed", ZmCsfeException.AUTH_TOKEN_CHANGED, "ZmCsfeCommand.invoke");
		}
		soapDoc.set("authToken", authToken, context);
		ZmCsfeCommand._curAuthToken = authToken;
	}
	
	// Tell server what kind of response we want
	if (!params.useXml) {
		var js = soapDoc.set("format", null, context);
		js.setAttribute("type", "js");
	}

	var asyncMode = params.asyncMode;
	var methodNameStr = soapDoc.getMethod().nodeName;
	DBG.println(AjxDebug.DBG1, ["<H4>", methodNameStr, (asyncMode) ? " (asynchronous)" : "" ,"</H4>"].join(""), methodNameStr);
	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	var rpcCallback;
	try {
		var uri = params.serverUri || ZmCsfeCommand.serverUri;
		//if (params.logRequest)
			uri = uri + soapDoc._methodEl.nodeName;
		var requestStr = soapDoc.getXml();

		this._st = new Date();

		if (asyncMode) {
//			DBG.println(AjxDebug.DBG1, "set callback for asynchronous response");	
			rpcCallback = new AjxCallback(this, this._runCallback, params.callback);
			this._rpcId = AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"}, rpcCallback);
		} else {
//			DBG.println(AjxDebug.DBG1, "parse response synchronously");	
			var response = AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
			if (!params.returnXml) {
				return this._getResponseData(response, false);
			} else {
				return response;
			}
		}
	} catch (ex) {
		if (!(ex && (ex instanceof ZmCsfeException || ex instanceof AjxSoapException || ex instanceof AjxException))) {
			var newEx = new ZmCsfeException();
			newEx.method = "ZmCsfeCommand.invoke";
			newEx.detail = ex ? ex.toString() : "undefined exception";
			newEx.code = ZmCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
			ex = newEx;
		}
		if (asyncMode) {
			rpcCallback.run(new ZmCsfeResult(ex, true));
		} else {
			throw ex;
		}
	}
};

/**
* Takes the response to an RPC request and returns a JS object with the response data.
*
* @param response	[Object]	RPC response with properties "text" and "xml"
* @param asyncMode	[boolean]	true if we're in asynchronous mode
*/
ZmCsfeCommand.prototype._getResponseData =
function(response, asyncMode) {
	this._en = new Date();
	DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (this._en.getTime() - this._st.getTime()));

	var result = new ZmCsfeResult();
	var xmlResponse = false;
	var respDoc = null;
	if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
		respDoc = response.text;
	} else {
		// an XML response if we requested one, or a fault
		try {
			xmlResponse = true;
			if (!(response.text || (response.xml && (typeof response.xml) == "string"))) {
				// If we can't reach the server, req returns immediately with an empty response rather than waiting and timing out
				throw new ZmCsfeException("Cannot connect to server", ZmCsfeException.NETWORK_ERROR, "ZmCsfeCommand.prototype.invoke", "empty HTTP response");
			}
			// responseXML is empty under IE
			respDoc = (AjxEnv.isIE || response.xml == null) ? AjxSoapDoc.createFromXml(response.text) :
															  AjxSoapDoc.createFromDom(response.xml);
		} catch (ex) {
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
		if (!respDoc) {
			var ex = new ZmCsfeException("Csfe service error", ZmCsfeException.SOAP_ERROR, "ZmCsfeCommand.prototype.invoke", "Bad XML response doc");
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
	}

	var linkName = "Response";
	if (respDoc && respDoc.match) {
		var m = respDoc.match(/\{"?Body"?:\{"?(\w+)"?:/);
		if (m && m.length) linkName = m[1];
	}
	DBG.println(AjxDebug.DBG1, ["<H4> RESPONSE", (asyncMode) ? " (asynchronous)" : "" ,"</H4>"].join(""), linkName);

	var obj = {};

	if (xmlResponse) {
		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		obj = respDoc._xmlDoc.toJSObject(true, false, true);
	} else {
		try {
			eval("obj=" + respDoc);
		} catch (ex) {
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}

	}

	DBG.dumpObj(AjxDebug.DBG1, obj, -1);

	var fault = obj.Body.Fault;
	if (fault) {
		// JS response with fault
		var ex = ZmCsfeCommand.faultToEx(fault, "ZmCsfeCommand.prototype.invoke");
		if (asyncMode) {
			result.set(ex, true, obj.Header);
			return result;
		} else {
			throw ex;
		}
	} else if (!response.success) {
		// bad XML or JS response that had no fault
		var ex = new ZmCsfeException("Csfe service error", ZmCsfeException.CSFE_SVC_ERROR,
									 "ZmCsfeCommand.prototype.invoke", "HTTP response status " + response.status);
		if (asyncMode) {
			result.set(ex, true);
			return result;
		} else {
			throw ex;
		}
	} else {
		// good response
		if (asyncMode) {
			result.set(obj);
		}
	}

	if (obj.Header && obj.Header.context && obj.Header.context.sessionId) {
		ZmCsfeCommand.setSessionId(obj.Header.context.sessionId);
	}

	return asyncMode ? result : obj;
};

/**
* Runs the callback that was passed to invoke() for an async command.
*
* @param callback	[AjxCallback]	Callback to run with response data
* @param response	[Object]		RPC response object
*/
ZmCsfeCommand.prototype._runCallback =
function(callback, result) {
	if (!result) return;

	var response;
	if (result instanceof ZmCsfeResult) {
		response = result; // we already got an exception and packaged it
	} else {
		response = this._getResponseData(result, true);
	}
	this._en = new Date();

	if (!callback) {
		DBG.println(AjxDebug.DBG1, "Could not find callback!");
		return;
	}

	if (callback) callback.run(response);
};

/**
* Cancels this request (which must be async).
*/
ZmCsfeCommand.prototype.cancel =
function() {
	if (!this._rpcId) return;

	var req = AjxRpc.getRpcRequest(this._rpcId);
	if (req)
		req.cancel();
};

// DEPRECATED - instead, use instance method invoke() above
ZmCsfeCommand.invoke =
function(soapDoc, noAuthTokenRequired, serverUri, targetServer, useXml, noSession, changeToken) {
	var hdr = soapDoc.createHeaderElement();
	var context = soapDoc.set("context", null, hdr, "urn:zimbra");

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
		var requestStr = soapDoc.getXml();
			
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

	var obj = new Object();
	eval("obj=" + resp);
	DBG.dumpObj(obj, -1);

	var fault = obj.Body.Fault;
	if (fault)
		throw new ZmCsfeException(fault.Reason.Text, fault.Detail.Error.Code, "ZmCsfeCommand.invoke", fault.Code.Value);
	if (obj.Header && obj.Header.context && obj.Header.context.sessionId)
		ZmCsfeCommand.setSessionId(obj.Header.context.sessionId);

	return obj;
};
