/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* class AjxRpcRequest encapsulates XMLHttpRequest as _httpReq
*
**/
function AjxRpcRequest(init) {
	if (arguments.length == 0) return;
	
	if (!AjxRpcRequest._inited) 
		AjxRpcRequest._init();	
	
	if (AjxEnv.isIE) {
		this._httpReq = new ActiveXObject(AjxRpcRequest._msxmlVers);
	} else if (AjxEnv.isSafari || AjxEnv.isNav) {
		this._httpReq =  new XMLHttpRequest();
	}
	this.busy=false;
}

AjxRpcRequest._inited = false;
AjxRpcRequest._msxmlVers = null;


AjxRpcRequest.prototype.toString = 
function() {
	return "AjxRpcRequest";
}

/**
* @method public AjxRpcRequest.prototype.invoke
* @param requestStr - http request string
* @param serverUrl - URI for HTTP request
* @requestHeaders - HTTP request headers
* @param callback - AjxCallback instance. if call back is null, then the call is synchronous
**/
AjxRpcRequest.prototype.invoke =
function(requestStr, serverUrl, requestHeaders, callback) {
	// TODO Allow arbritatry request headers to be passed in
	this._httpReq.open("post", serverUrl, (callback != null) );
	if (callback) {
		var tempThis = this;
		DBG.println(AjxDebug.DBG1, "Have callback");
		this._httpReq.onreadystatechange = function (evt) {
			DBG.println(AjxDebug.DBG1, "ReadyState changed");
			if(!tempThis) {
				//IE sometimes looses objects
				callback.run( {text: null, xml: null, success: false, status: null} );				
			}
			DBG.println(AjxDebug.DBG1, "ready state = " + tempThis._httpReq.readyState);
			if(tempThis._httpReq.readyState==4) {
				DBG.println(AjxDebug.DBG1, "status = " + tempThis._httpReq.status);				
				if(tempThis._httpReq.status==200) {
					callback.run( {text: tempThis._httpReq.responseText, xml: tempThis._httpReq.responseXML, success: true} );				
				} else {
					callback.run( {text: tempThis._httpReq.responseText, xml: tempThis._httpReq.responseXML, success: false, status: tempThis._httpReq.status} );				
				}
				tempThis.busy = false;
				tempThis = null;
			}
		}
	}	

	if (requestHeaders) {
		for (var i in requestHeaders) {
			this._httpReq.setRequestHeader(i, requestHeaders[i]);
			DBG.println("REQ. HEADER: " + i + " - " + requestHeaders[i]);
		}
	}
	this._httpReq.send(requestStr);
	if (callback) {
		return;
	} else {
		return {text: this._httpReq.responseText, xml: this._httpReq.responseXML};
	}
}

AjxRpcRequest._init =
function() {
	if (AjxEnv.isIE) {
		var msxmlVers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				// search for the xml version on user's machine
				var x = new ActiveXObject(msxmlVers[i]);
				AjxRpcRequest._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
				// do nothing
			}
		}
		if (AjxRpcRequest._msxmlVers == null)
			throw new AjxException("MSXML not installed", AjxException.INTERNAL_ERROR, "AjxRpc._init");
	}
	AjxRpcRequest._inited = true;
}

