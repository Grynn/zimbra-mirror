/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function ZMTBCsfeCommand() {
};

// Static properties

// Global settings for each CSFE command
ZMTBCsfeCommand._COOKIE_NAME = "ZM_AUTH_TOKEN";
ZMTBCsfeCommand.serverUri = null;
ZMTBCsfeCommand._authToken = null;
ZMTBCsfeCommand._sessionId = null;

// Static methods

ZMTBCsfeCommand.getAuthToken =
function() {
	// See if the auth token is cached. If not try and get it from the cookie
	if (ZMTBCsfeCommand._authToken != null)
		return ZMTBCsfeCommand._authToken;
	var authToken = ZMTB_AjxCookie.getCookie(document, ZMTBCsfeCommand._COOKIE_NAME)
	ZMTBCsfeCommand._authToken = authToken;
	return authToken;
};

ZMTBCsfeCommand.setCookieName =
function(cookieName) {
	ZMTBCsfeCommand._COOKIE_NAME = cookieName;
};

ZMTBCsfeCommand.setServerUri =
function(uri) {
	ZMTBCsfeCommand.serverUri = uri;
};

ZMTBCsfeCommand.setAuthToken =
function(authToken, lifetimeMs, sessionId) {
	ZMTBCsfeCommand._authToken = authToken;
	if (lifetimeMs != null) {
		var exp = null;
		if(lifetimeMs > 0) {
			exp = new Date();
			var lifetime = parseInt(lifetimeMs);
			exp.setTime(exp.getTime() + lifetime);
		}
		ZMTB_AjxCookie.setCookie(document, ZMTBCsfeCommand._COOKIE_NAME, authToken, exp, "/");		
	} else {
		ZMTB_AjxCookie.deleteCookie(document, ZMTBCsfeCommand._COOKIE_NAME, "/");
	}
	if (sessionId)
		ZMTBCsfeCommand.setSessionId(sessionId);
};

ZMTBCsfeCommand.clearAuthToken =
function() {
	ZMTBCsfeCommand._authToken = null;
	ZMTB_AjxCookie.deleteCookie(document, ZMTBCsfeCommand._COOKIE_NAME, "/");
};

ZMTBCsfeCommand.getSessionId =
function() {
	return ZMTBCsfeCommand._sessionId;
};

ZMTBCsfeCommand.setSessionId =
function(sessionId) {
	var id = (sessionId instanceof Array) ? sessionId[0].id : sessionId;
	ZMTBCsfeCommand._sessionId = parseInt(id);
};

ZMTBCsfeCommand.faultToEx =
function(fault, method) {
	var trace = ZMTB_AjxStringUtil.getAsString(fault.Detail.Error.Trace);
	var faultCode = ZMTB_AjxStringUtil.getAsString(fault.Code.Value);
	var errorCode = ZMTB_AjxStringUtil.getAsString(fault.Detail.Error.Code);
	var reasonText = fault.Reason.Text + (trace ? "\n" + trace : "");
	var requestId = fault.requestId;
	return new ZMTBCsfeException(reasonText, errorCode, method, faultCode, fault.Detail.Error.a, requestId);
};

/**
* Sends a SOAP request to the server and processes the response.
*
* @param soapDoc		[ZMTB_AjxSoapDoc]	The SOAP document that represents the request
* @param noAuthToken	[boolean]*		If true, the check for an auth token is skipped
* @param serverUri		[string]*		URI to send the request to
* @param targetServer	[string]*		Host that services the request
* @param useXml			[boolean]*		If true, an XML response is requested
* @param noSession		[boolean]*		If true, no session info is included
* @param changeToken	[string]*		Current change token
* @param highestNotifySeen [int]*       Sequence # of the highest notification we have processed
* @param asyncMode		[boolean]*		If true, request sent asynchronously
* @param callback		[ZMTB_AjxCallback]*	Callback to run when response is received (async mode)
* @param logRequest		[boolean]*		If true, SOAP command name is appended to server URL
* @param accountId		[string]*		ID of account to execute on behalf of
* @param accountName	[string]*		name of account to execute on behalf of
*/
ZMTBCsfeCommand.prototype.invoke =
function(params) {
	
	if (!params.soapDoc) return;

	var soapDoc = params.soapDoc;
	// Add the SOAP header and context
	var hdr = soapDoc.createHeaderElement();
	var context = soapDoc.set("context", null, hdr, "urn:zimbra");

	var ua = soapDoc.set("userAgent", null, context);
	var name = ["ZimbraToolbar - ", ZMTB_AjxEnv.browser, " (", ZMTB_AjxEnv.platform, ")"].join("");
	ua.setAttribute("name", name);
	ua.setAttribute("version", ZMTBCsfeCommand.clientVersion);

	if (params.noSession) {
		soapDoc.set("nosession", null, context);
	}
	var sessionId = ZMTBCsfeCommand.getSessionId();
	
	//Zimbra 6 requires that an empty session element be included in order to receive a refresh block
	var si2 = soapDoc.set("session", null, context);
	if (sessionId)
	{
		var si = soapDoc.set("sessionId", null, context);
		si.setAttribute("id", sessionId);
		si2.setAttribute("id", sessionId);
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
	

	if (params.accountId) {
		var acc = soapDoc.set("account", params.accountId, context);
		acc.setAttribute("by", "id");
	} else if (params.accountName) {
		var acc = soapDoc.set("account", params.accountName, context);
		acc.setAttribute("by", "name");
	}
	

	// Get auth token from cookie if required
	if (!params.noAuthToken) {
		var authToken = ZMTBCsfeCommand.getAuthToken();
		if (!authToken)
			throw new ZMTBCsfeException("AuthToken required", ZMTBCsfeException.NO_AUTH_TOKEN, "ZMTBCsfeCommand.invoke");
		soapDoc.set("authToken", authToken, context);
	}

	// Tell server what kind of response we want
	if (!params.useXml) {
		var js = soapDoc.set("format", null, context);
		js.setAttribute("type", "js");
	}

	var asyncMode = params.asyncMode;
	var methodNameStr = soapDoc.getMethod().nodeName;
    //	DBG.println(AjxDebug.DBG1, ["<H4>", methodNameStr, (asyncMode) ? " (asynchronous)" : "" ,"</H4>"].join(""), methodNameStr);
    //	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	var rpcCallback;
	try {
		var uri = params.serverUri || ZMTBCsfeCommand.serverUri;
		if (params.logRequest)
			uri = uri + soapDoc._methodEl.nodeName;
		var requestStr = soapDoc.getXml();
		if (ZMTB_AjxEnv.isSafari && !ZMTB_AjxEnv.isSafariNightly)
			requestStr = requestStr.replace("soap=", "xmlns:soap=");

		this._st = new Date();

		if (asyncMode) {
//			DBG.println(AjxDebug.DBG1, "set callback for asynchronous response");	
			rpcCallback = new ZMTB_AjxCallback(this, this._runCallback, params.callback);
			this._rpcId = ZMTB_AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"}, rpcCallback);
		} else {
//			DBG.println(AjxDebug.DBG1, "parse response synchronously");	
			var response = ZMTB_AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
			if (!params.returnXml) {
				return this._getResponseData(response, false);
			} else {
				return response;
			}
		}
	} catch (ex) {
		if (!(ex && (ex instanceof ZMTBCsfeException || ex instanceof AjxSoapException || ex instanceof ZMTB_AjxException))) {
			var newEx = new ZMTBCsfeException();
			newEx.method = "ZMTBCsfeCommand.invoke";
			newEx.detail = ex ? ex.toString() : "undefined exception";
			newEx.code = ZMTBCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
			ex = newEx;
		}
		if (asyncMode) {
			rpcCallback.run(new ZMTBCsfeResult(ex, true));
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
ZMTBCsfeCommand.prototype._getResponseData =
function(response, asyncMode) {
	this._en = new Date();
    //	DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (this._en.getTime() - this._st.getTime()));

	var result = new ZMTBCsfeResult();
	var xmlResponse = false;
	var respDoc = null;
	if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
		respDoc = response.text;
	} else {
		// an XML response if we requested one, or a fault
		try {
			xmlResponse = true;
			if (!(response.text || (response.xml && (typeof response.xml) == "string"))) {
				// If IE can't reach the server, it returns immediately with an empty response rather than waiting and timing out
				throw new ZMTBCsfeException("Csfe service error", ZMTBCsfeException.NETWORK_ERROR, "ZMTBCsfeCommand.prototype.invoke", "Empty HTTP response");
			}
			// responseXML is empty under IE
			respDoc = (ZMTB_AjxEnv.isIE || response.xml == null) ? ZMTB_AjxSoapDoc.createFromXml(response.text) :
															  ZMTB_AjxSoapDoc.createFromDom(response.xml);
		} catch (ex) {
          //			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}
		if (!respDoc) {
			var ex = new ZMTBCsfeException("Csfe service error", ZMTBCsfeException.SOAP_ERROR, "ZMTBCsfeCommand.prototype.invoke", "Bad XML response doc");
            //			DBG.dumpObj(AjxDebug.DBG1, ex);
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
    //	DBG.println(AjxDebug.DBG1, ["<H4> RESPONSE", (asyncMode) ? " (asynchronous)" : "" ,"</H4>"].join(""), linkName);

	var data = {};

	if (xmlResponse) {
      //		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		data = respDoc._xmlDoc.toJSObject(true, false, true);
	} else {
		try {
			eval("data=" + respDoc);
		} catch (ex) {
          //			DBG.dumpObj(AjxDebug.DBG1, ex);
			if (asyncMode) {
				result.set(ex, true);
				return result;
			} else {
				throw ex;
			}
		}

	}

    //	DBG.dumpObj(AjxDebug.DBG1, data, -1);

	var fault = data.Body.Fault;
	if (fault) {
		// if (asyncMode)
		// 	result.set(data);
		// JS response with fault
		var ex = ZMTBCsfeCommand.faultToEx(fault, "ZMTBCsfeCommand.prototype.invoke");
		if (asyncMode) {
			result.set(ex, true, data.Header);
			return result;
		} else {
			throw ex;
		}
	} else if (!response.success) {
		// bad XML or JS response that had no fault
		var ex = new ZMTBCsfeException("Csfe service error", ZMTBCsfeException.CSFE_SVC_ERROR,
									 "ZMTBCsfeCommand.prototype.invoke", "HTTP response status " + response.status);
		if (asyncMode) {
			result.set(ex, true);
			return result;
		} else {
			throw ex;
		}
	} else {
		// good response
		if (asyncMode)
			result.set(data);
	}

	if (data.Header && data.Header.context && data.Header.context.sessionId)
		ZMTBCsfeCommand.setSessionId(data.Header.context.sessionId);
	else if(data.Header && data.Header.context && data.Header.context.session)
		ZMTBCsfeCommand.setSessionId(data.Header.context.session.id);

	return asyncMode ? result : data;
};

/**
* Runs the callback that was passed to invoke() for an async command.
*
* @param callback	[ZMTB_AjxCallback]	Callback to run with response data
* @param response	[Object]		RPC response object
*/
ZMTBCsfeCommand.prototype._runCallback =
function(callback, result) {
	if (!result) return;

	var response;
	if (result instanceof ZMTBCsfeResult) {
		response = result; // we already got an exception and packaged it
	} else {
		response = this._getResponseData(result, true);
	}
	this._en = new Date();

	if (!callback) {
      //		DBG.println(AjxDebug.DBG1, "Could not find callback!");
		return;
	}

	if (callback) callback.run(response);
};

/**
* Cancels this request (which must be async).
*/
ZMTBCsfeCommand.prototype.cancel =
function() {
	if (!this._rpcId) return;

	var req = ZMTB_AjxRpc.getRpcRequest(this._rpcId);
	if (req)
		req.cancel();
};

// DEPRECATED - instead, use instance method invoke() above
ZMTBCsfeCommand.invoke =
function(soapDoc, noAuthTokenRequired, serverUri, targetServer, useXml, noSession, changeToken) {
	var hdr = soapDoc.createHeaderElement();
	var context = soapDoc.set("context", null, hdr, "urn:zimbra");

	if (noSession)
		soapDoc.set("nosession", null, context);
	var sessionId = ZMTBCsfeCommand.getSessionId();
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
		var authToken = ZMTBCsfeCommand.getAuthToken();
		if (!authToken)
			throw new ZMTBCsfeException("AuthToken required", ZMTBCsfeException.NO_AUTH_TOKEN, "ZMTBCsfeCommand.invoke");
		soapDoc.set("authToken", authToken, context);
	}

	if (!useXml) {
		var js = soapDoc.set("format", null, context);
		js.setAttribute("type", "js");
	}

    //	DBG.println(AjxDebug.DBG1, "<H4>REQUEST</H4>");
    //	DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());

	var xmlResponse = false;
	try {
		var uri = serverUri || ZMTBCsfeCommand.serverUri;
		var requestStr = ZMTB_AjxEnv.isSafari && !ZMTB_AjxEnv.isSafariNightly
			? soapDoc.getXml().replace("soap=", "xmlns:soap=")
			: soapDoc.getXml();
			
		var _st = new Date();
		var response = ZMTB_AjxRpc.invoke(requestStr, uri, {"Content-Type": "application/soap+xml; charset=utf-8"});
		var _en = new Date();
        //		DBG.println(AjxDebug.DBG1, "ROUND TRIP TIME: " + (_en.getTime() - _st.getTime()));

		var respDoc = null;
		if (typeof(response.text) == "string" && response.text.indexOf("{") == 0) {
			respDoc = response.text;
		} else {
			xmlResponse = true;
			// responseXML is empty under IE
			respDoc = (ZMTB_AjxEnv.isIE || response.xml == null)
				? ZMTB_AjxSoapDoc.createFromXml(response.text) 
				: ZMTB_AjxSoapDoc.createFromDom(response.xml);
		}
	} catch (ex) {
		if (ex instanceof AjxSoapException) {
			throw ex;
		} else if (ex instanceof ZMTB_AjxException) {
			throw ex; 
		}  else {
			var newEx = new ZMTBCsfeException();
			newEx.method = "ZMTBCsfeCommand.invoke";
			newEx.detail = ex.toString();
			newEx.code = ZMTBCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
			throw newEx;
		}
	}
	
    //	DBG.println(AjxDebug.DBG1, "<H4>RESPONSE</H4>");

	var resp;
	if (xmlResponse) {
      //		DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		var body = respDoc.getBody();
		var fault = ZMTB_AjxSoapDoc.element2FaultObj(body);
		if (fault) {
			throw new ZMTBCsfeException("Csfe service error", fault.errorCode, "ZMTBCsfeCommand.invoke", fault.reason);
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
    //	DBG.dumpObj(data, -1);

	var fault = data.Body.Fault;
	if (fault)
		throw new ZMTBCsfeException(fault.Reason.Text, fault.Detail.Error.Code, "ZMTBCsfeCommand.invoke", fault.Code.Value);
	if (data.Header && data.Header.context && data.Header.context.sessionId)
		ZMTBCsfeCommand.setSessionId(data.Header.context.sessionId);

	return data;
};
