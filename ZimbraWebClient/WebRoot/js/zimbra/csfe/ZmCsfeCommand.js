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

// Reasons for re-sending a request
ZmCsfeCommand.REAUTH	= "reauth";
ZmCsfeCommand.RETRY		= "retry";

// Static methods

ZmCsfeCommand.getAuthToken =
function() {
	return AjxCookie.getCookie(document, ZmCsfeCommand._COOKIE_NAME);
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
	var trace="";
	if(fault.Detail.Error.Trace) {
		trace = fault.Detail.Error.Trace;
	}
	return new ZmCsfeException(msg, errorCode, method, faultCode, fault.Detail.Error.a, trace);
};

/**
 * Returns the method name (*Request) of the given request, which may be either a SOAP doc
 * or a JSON object.
 * 
 * @param request	[AjxSoapDoc|object]		request
 */
ZmCsfeCommand.getMethodName =
function(request) {
	if (request instanceof AjxSoapDoc) {
		return request._methodEl.tagName
	} else {
		var methodName = "[unknown]";
		for (var prop in request) {
			if (prop.indexOf("Request") != -1) {
				methodName = prop;
				break;
			}
		}
		return methodName;
	}
};

ZmCsfeCommand.prototype.toString =
function() {
	return "ZmCsfeCommand";
};

/**
 * Sends a SOAP request to the server and processes the response. The request can be in the form
 * of a SOAP document, or a JSON object.
 *
 * @param params			[hash]				hash of params:
 *        soapDoc			[AjxSoapDoc]		the SOAP document that represents the request
 *        jsonObj			[object]			JSON object that represents the request (alternative to soapDoc)
 *        noAuthToken		[boolean]*			If true, the check for an auth token is skipped
 *        serverUri			[string]*			URI to send the request to
 *        targetServer		[string]*			Host that services the request
 *        useXml			[boolean]*			If true, an XML response is requested
 *        noSession			[boolean]*			If true, no session info is included
 *        changeToken		[string]*			Current change token
 *        highestNotifySeen [int]*  	    	Sequence # of the highest notification we have processed
 *        asyncMode			[boolean]*			If true, request sent asynchronously
 *        callback			[AjxCallback]*		Callback to run when response is received (async mode)
 *        logRequest		[boolean]*			If true, SOAP command name is appended to server URL
 *        accountId			[string]*			ID of account to execute on behalf of
 *        accountName		[string]*			name of account to execute on behalf of
 *        skipAuthCheck		[boolean]*			don't check if auth token has changed
 *        resend			[constant]*			reason for resending request
 */
ZmCsfeCommand.prototype.invoke =
function(params) {

	if (!(params && (params.soapDoc || params.jsonObj))) { return; }

	var requestStr = params.soapDoc ? this._getSoapRequestStr(params) : this._getJsonRequestStr(params);

	var rpcCallback;
	try {
		var uri = (params.serverUri || ZmCsfeCommand.serverUri) + params.methodNameStr;
		this._st = new Date();
		
		var requestHeaders = {"Content-Type": "application/soap+xml; charset=utf-8"};
		if (AjxEnv.isIE6 && (location.protocol == "https:")) { //bug 22829
			requestHeaders["Connection"] = "Close";
		}
			
		if (params.asyncMode) {
			//DBG.println(AjxDebug.DBG1, "set callback for asynchronous response");
			rpcCallback = new AjxCallback(this, this._runCallback, [params]);
			this._rpcId = AjxRpc.invoke(requestStr, uri, requestHeaders, rpcCallback);
		} else {
			//DBG.println(AjxDebug.DBG1, "parse response synchronously");
			var response = AjxRpc.invoke(requestStr, uri, requestHeaders);
			return (!params.returnXml) ? (this._getResponseData(response, params)) : response;
		}
	} catch (ex) {
		if (!(ex && (ex instanceof ZmCsfeException || ex instanceof AjxSoapException || ex instanceof AjxException))) {
			var newEx = new ZmCsfeException();
			newEx.method = params.methodNameStr;
			newEx.detail = ex ? ex.toString() : "undefined exception";
			newEx.code = ZmCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
			ex = newEx;
		}
		if (params.asyncMode) {
			rpcCallback.run(new ZmCsfeResult(ex, true));
		} else {
			throw ex;
		}
	}
};

ZmCsfeCommand.prototype._getJsonRequestStr =
function(params) {

	var obj = {Header:{}, Body:params.jsonObj};

	var context = obj.Header.context = {_jsns:"urn:zimbra"};
	var ua_name = ["ZimbraWebClient - ", AjxEnv.browser, " (", AjxEnv.platform, ")"].join("");
	context.userAgent = {name:ua_name};
	if (ZmCsfeCommand.clientVersion) {
		context.userAgent.version = ZmCsfeCommand.clientVersion;
	}
	if (params.noSession) {
		context.nosession = {};
	}
	var sessionId = ZmCsfeCommand.getSessionId();
	if (sessionId) {
		context.sessionId = {_content:sessionId, id:sessionId};
	}
	if (params.targetServer) {
		context.targetServer = {_content:params.targetServer};
	}
	if (params.highestNotifySeen) {
		context.notify = {seq:params.highestNotifySeen};
	}
	if (params.changeToken) {
		context.change = {token:params.changeToken, type:"new"};
	}

	// if we're not checking auth token, we don't want token/acct mismatch	
	if (!params.skipAuthCheck) {
		if (params.accountId) {
			context.account = {_content:params.accountId, by:"id"}
		} else if (params.accountName) {
			context.account = {_content:params.accountName, by:"name"}
		}
	}
	
	// Tell server what kind of response we want
	if (!params.useXml) {
		context.format = {type:"js"};
	}

	params.methodNameStr = ZmCsfeCommand.getMethodName(params.jsonObj);

	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken) {
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, params.methodNameStr);
		}
		if (ZmCsfeCommand._curAuthToken && !params.skipAuthCheck && 
			(params.resend != ZmCsfeCommand.REAUTH) && (authToken != ZmCsfeCommand._curAuthToken)) {
			throw new ZmCsfeException("AuthToken has changed", ZmCsfeException.AUTH_TOKEN_CHANGED, params.methodNameStr);
		}
		context.authToken = ZmCsfeCommand._curAuthToken = authToken;
	}
	
	DBG.println(AjxDebug.DBG1, ["<H4>", params.methodNameStr, params.asyncMode ? " (asynchronous)" : "" ,"</H4>"].join(""), params.methodNameStr);
	var requestStr = AjxStringUtil.objToString(obj);
	DBG.dumpObj(AjxDebug.DBG1, obj);

	return requestStr;
};

ZmCsfeCommand.prototype._getSoapRequestStr =
function(params) {

	var soapDoc = params.soapDoc;

	if (!params.resend) {

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
		} else {
			var sessionId = ZmCsfeCommand.getSessionId();
			var si = soapDoc.set("session", null, context);
			if (sessionId) {
				si.setAttribute("id", sessionId);
			}
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
		
		// Tell server what kind of response we want
		if (!params.useXml) {
			var js = soapDoc.set("format", null, context);
			js.setAttribute("type", "js");
		}
	}

	params.methodNameStr = ZmCsfeCommand.getMethodName(soapDoc);

	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (!authToken) {
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, params.methodNameStr);
		}
		if (ZmCsfeCommand._curAuthToken && !params.skipAuthCheck && 
			(params.resend != ZmCsfeCommand.REAUTH) && (authToken != ZmCsfeCommand._curAuthToken)) {
			throw new ZmCsfeException("AuthToken has changed", ZmCsfeException.AUTH_TOKEN_CHANGED, params.methodNameStr);
		}
		ZmCsfeCommand._curAuthToken = authToken;
		if (params.resend == ZmCsfeCommand.REAUTH) {
			// replace old auth token with current one
			var nodes = soapDoc.getDoc().getElementsByTagName("authToken");
			if (nodes && nodes.length == 1) {
				DBG.println(AjxDebug.DBG1, "Re-auth: replacing auth token");
				nodes[0].firstChild.data = authToken;
			} else {
				// can't find auth token, just add it to context element
				nodes = soapDoc.getDoc().getElementsByTagName("context");
				if (nodes && nodes.length == 1) {
					DBG.println(AjxDebug.DBG1, "Re-auth: re-adding auth token");
					soapDoc.set("authToken", authToken, nodes[0]);
				} else {
					DBG.println(AjxDebug.DBG1, "Re-auth: could not find context!");
				}
			}
		} else if (!params.resend){
			soapDoc.set("authToken", authToken, context);
		}
	}
	
	DBG.println(AjxDebug.DBG1, ["<H4>", params.methodNameStr, params.asyncMode ? " (asynchronous)" : "" ,"</H4>"].join(""), params.methodNameStr);
	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	return soapDoc.getXml();
};

/**
 * Runs the callback that was passed to invoke() for an async command.
 *
 * @param callback	[AjxCallback]	Callback to run with response data
 * @param params	[hash]			hash of params (see method invoke())
 */
ZmCsfeCommand.prototype._runCallback =
function(params, result) {
	if (!result) { return; }

	var response;
	if (result instanceof ZmCsfeResult) {
		response = result; // we already got an exception and packaged it
	} else {
		response = this._getResponseData(result, params);
	}
	this._en = new Date();

	if (params.callback) {
		params.callback.run(response);
	} else {
		DBG.println(AjxDebug.DBG1, "ZmCsfeCommand.prototype._runCallback: Missing callback!");
	}
};

/**
 * Takes the response to an RPC request and returns a JS object with the response data.
 *
 * @param response	[Object]		RPC response with properties "text" and "xml"
 * @param params	[hash]			hash of params (see method invoke())
 */
ZmCsfeCommand.prototype._getResponseData =
function(response, params) {
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
				throw new ZmCsfeException(null, ZmCsfeException.EMPTY_RESPONSE, params.methodNameStr);
			}
			// responseXML is empty under IE
			respDoc = (AjxEnv.isIE || response.xml == null) ? AjxSoapDoc.createFromXml(response.text) :
															  AjxSoapDoc.createFromDom(response.xml);
		} catch (ex) {
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
		if (!respDoc) {
			var ex = new ZmCsfeException(null, ZmCsfeException.SOAP_ERROR, params.methodNameStr, "Bad XML response doc");
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
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
	DBG.println(AjxDebug.DBG1, ["<H4> RESPONSE", params.asyncMode ? " (asynchronous)" : "" ,"</H4>"].join(""), linkName);

	var obj = {};

	if (xmlResponse) {
		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		obj = respDoc._xmlDoc.toJSObject(true, false, true);
	} else {
		try {
			eval("obj=" + respDoc);
		} catch (ex) {
			if (ex.name == "SyntaxError") {
				ex = new ZmCsfeException(null, ZmCsfeException.BAD_JSON_RESPONSE, params.methodNameStr);
			}
			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (params.asyncMode) {
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
		var ex = ZmCsfeCommand.faultToEx(fault, params.methodNameStr);
		if (params.asyncMode) {
			result.set(ex, true, obj.Header);
			return result;
		} else {
			throw ex;
		}
	} else if (!response.success) {
		// bad XML or JS response that had no fault
		var ex = new ZmCsfeException(null, ZmCsfeException.CSFE_SVC_ERROR, params.methodNameStr, "HTTP response status " + response.status);
		if (params.asyncMode) {
			result.set(ex, true);
			return result;
		} else {
			throw ex;
		}
	} else {
		// good response
		if (params.asyncMode) {
			result.set(obj);
		}
	}

	if (obj.Header && obj.Header.context && obj.Header.context.sessionId) {
		ZmCsfeCommand.setSessionId(obj.Header.context.sessionId);
	}

	return params.asyncMode ? result : obj;
};

/**
 * Cancels this request (which must be async).
 */
ZmCsfeCommand.prototype.cancel =
function() {
	if (!this._rpcId) { return; }

	var req = AjxRpc.getRpcRequest(this._rpcId);
	if (req) {
		req.cancel();
	}
};

// DEPRECATED - instead, use instance method invoke() above
ZmCsfeCommand.invoke =
function(soapDoc, noAuthToken, serverUri, targetServer, useXml, noSession, changeToken) {
	DBG.println(AjxDebug.DBG1, "Warning: use of deprecated method ZmCsfeCommand.invoke()");
	var command = new ZmCsfeCommand();
	var cmdParams = {
		soapDoc:soapDoc,
		noAuthToken:noAuthToken,
		serverUri:serverUri,
		targetServer:targetServer,
		useXml:useXml,
		noSession:noSession,
		changeToken:changeToken,
		asyncMode:false
	};
	return command.invoke(cmdParams);
};
