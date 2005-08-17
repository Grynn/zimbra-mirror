/**
* class LsRpcRequest encapsulates XMLHttpRequest as _httpReq
*
**/
function LsRpcRequest(init) {
	if (arguments.length == 0) return;
	
	if (!LsRpcRequest._inited) 
		LsRpcRequest._init();	
	
	if (LsEnv.isIE) {
		this._httpReq = new ActiveXObject(LsRpcRequest._msxmlVers);
	} else if (LsEnv.isSafari || LsEnv.isNav) {
		this._httpReq =  new XMLHttpRequest();
	}
	this.busy=false;
}

LsRpcRequest._inited = false;
LsRpcRequest._msxmlVers = null;


LsRpcRequest.prototype.toString = 
function() {
	return "LsRpcRequest";
}

/**
* @method public LsRpcRequest.prototype.invoke
* @param requestStr - http request string
* @param serverUrl - URI for HTTP request
* @requestHeaders - HTTP request headers
* @param callback - LsCallback instance. if call back is null, then the call is synchronous
**/
LsRpcRequest.prototype.invoke =
function(requestStr, serverUrl, requestHeaders, callback) {
	// TODO Allow arbritatry request headers to be passed in
	this._httpReq.open("post", serverUrl, (callback != null) );
	if (callback) {
		var tempThis = this;
		DBG.println(LsDebug.DBG1, "Have callback");
		this._httpReq.onreadystatechange = function (evt) {
			DBG.println(LsDebug.DBG1, "ReadyState changed");
			if(!tempThis) {
				//IE sometimes looses objects
				callback.run( {text: null, xml: null, success: false, status: null} );				
			}
			DBG.println(LsDebug.DBG1, "ready state = " + tempThis._httpReq.readyState);
			if(tempThis._httpReq.readyState==4) {
				DBG.println(LsDebug.DBG1, "status = " + tempThis._httpReq.status);				
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

LsRpcRequest._init =
function() {
	if (LsEnv.isIE) {
		var msxmlVers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				// search for the xml version on user's machine
				var x = new ActiveXObject(msxmlVers[i]);
				LsRpcRequest._msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
				// do nothing
			}
		}
		if (LsRpcRequest._msxmlVers == null)
			throw new LsException("MSXML not installed", LsException.INTERNAL_ERROR, "LsRpc._init");
	}
	LsRpcRequest._inited = true;
}

