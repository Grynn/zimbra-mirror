/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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


/**
 * @constructor
 * @class
 * This class encapsulates the XML HTTP request, hiding differences between
 * browsers. The internal request object depends on the browser. While it is 
 * possible to use this class directly, {@link AjxRpc} provides a managed interface
 * to this class 
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @param {string} [id]		the ID to identify this object
 * 
 * @see AjxRpc
 * 
 */
AjxRpcRequest = function(id) {
	if (!AjxRpcRequest.__inited) {
		AjxRpcRequest.__init();
	}

	/**
	 * The id for this object.
	 */
	this.id = id;
	this.__httpReq = AjxRpcRequest.__msxmlVers
		? (new ActiveXObject(AjxRpcRequest.__msxmlVers))
		: (new XMLHttpRequest());
};

AjxRpcRequest.prototype.isAjxRpcRequest = true;
AjxRpcRequest.prototype.toString = function() { return "AjxRpcRequest"; };

AjxRpcRequest.TIMEDOUT		= -1000;		// Timed out exception

AjxRpcRequest.__inited		= false;
AjxRpcRequest.__msxmlVers	= null;


/**
 * Sends this request to the target URL. If there is a callback, the request is
 * performed asynchronously.
 * 
 * @param {string} [requestStr] 	the HTTP request string/document
 * @param {string} serverUrl 	the request target 
 * @param {array} [requestHeaders] an array of HTTP request headers
 * @param {AjxCallback} callback 	the callback for asynchronous requests. This callback 
 * 		will be invoked when the requests completes. It will be passed the same
 * 		values as when this method is invoked synchronously (see the return values
 * 		below) with the exception that if the call times out (see timeout param 
 * 		below), then the object passed to the callback will be the same as in the 
 * 		error case with the exception that the status will be set to 
 * 		{@link AjxRpcRequest.TIMEDOUT}.
 * @param {boolean} [useGet=false] 		if <code>true</code>, use get method; otherwise, use post
 * @param {number} [timeout] 		the timeout (in milliseconds) after which the request is canceled
 * 
 * @return {object|hash}	if invoking in asynchronous mode, then it will return the id of the 
 * 		underlying {@link AjxRpcRequest} object. Else if invoked synchronously, if
 * 		there is no error (i.e. we get a HTTP result code of 200 from the server),
 * 		an object with the following attributes is returned
 * 		<ul>
 * 		<li>text - the string response text</li>
 * 		<li>xml - the string response xml</li>
 * 		<li>success - boolean set to true</li>
 * 		</ul>
 * 		If there is an eror, then the following will be returned
 * 		<ul>
 * 		<li>text - the string response text<li>
 * 		<li>xml - the string response xml </li>
 * 		<li>success - boolean set to <code>false</code></li>
 * 		<li>status - HTTP status</li>
 * 		</ul>
 * 
 * @throws	{AjxException.NETWORK_ERROR}	a network error occurs
 * @throws	{AjxException.UNKNOWN_ERROR}	an unknown error occurs
 * 
 * @see AjxRpc.invoke
 */
AjxRpcRequest.prototype.invoke =
function(requestStr, serverUrl, requestHeaders, callback, useGet, timeout) {

	var asyncMode = (callback != null);
	this.requestStr = requestStr;	// for debugging

	// An exception here will be caught by AjxRpc.invoke
	this.__httpReq.open((useGet) ? "get" : "post", serverUrl, asyncMode);

	if (asyncMode) {
		if (timeout) {
			var action = new AjxTimedAction(this, this.__handleTimeout, [callback]);
			callback._timedActionId = AjxTimedAction.scheduleAction(action, timeout);
		}
		var tempThis = this;
		this.__httpReq.onreadystatechange = function(ev) {
			if (window.AjxRpcRequest) {
				AjxRpcRequest.__handleResponse(tempThis, callback);
			}
		}
	} else {
		// IE appears to run handler even on sync requests, so we need to clear it
		this.__httpReq.onreadystatechange = function(ev) {};
	}

	if (requestHeaders) {
		for (var i in requestHeaders) {
            if (requestHeaders.hasOwnProperty(i)) {
                this.__httpReq.setRequestHeader(i, requestHeaders[i]);
            }
		}
	}

	AjxDebug.println(AjxDebug.RPC, "RPC send: " + this.id);
	this.__httpReq.send(requestStr);
	if (asyncMode) {
		return this.id;
	} else {
		if (this.__httpReq.status == 200 || this.__httpReq.status == 201) {
			return {text:this.__httpReq.responseText, xml:this.__httpReq.responseXML, success:true};
		} else {
			return {text:this.__httpReq.responseText, xml:this.__httpReq.responseXML, success:false, status:this.__httpReq.status};
		}
	}
};

/**
 * Cancels a pending request.
 * 
 */
AjxRpcRequest.prototype.cancel =
function() {
	AjxRpc.freeRpcCtxt(this);
    if (AjxEnv.isFirefox3_5up) {
		// bug 55911
        this.__httpReq.onreadystatechange = function(){};
    }
    this.__httpReq.abort();
};

/**
 * Handler that runs when an asynchronous request timesout.
 *
 * @param {AjxCallback} callback 	the callback to run after timeout
 * 
 * @private
 */
AjxRpcRequest.prototype.__handleTimeout =
function(callback) {
	this.cancel();
	callback.run( {text:null, xml:null, success:false, status:AjxRpcRequest.TIMEDOUT} );
};

/**
 * Handler that runs when an asynchronous response has been received. It runs a
 * callback to initiate the response handling.
 *
 * @param {AjxRpcRequest}	req		the request that generated the response
 * @param {AjxCallback}	callback	the callback to run after response is received
 * 
 * @private
 */
AjxRpcRequest.__handleResponse =
function(req, callback) {

	try {

	if (!req || !req.__httpReq) {

		req.cancel();

		// If IE receives a 500 error, the object reference can be lost
		DBG.println(AjxDebug.DBG1, "Async RPC request: Lost request object!!!");
		AjxDebug.println(AjxDebug.RPC, "Async RPC request: Lost request object!!!");
		callback.run( {text:null, xml:null, success:false, status:500} );
		return;
	}

	if (req.__httpReq.readyState == 4) {
		if (callback._timedActionId !== null) {
			AjxTimedAction.cancelAction(callback._timedActionId);
		}

		var status = 500;
		try {
			status = req.__httpReq.status;
		} catch (ex) {
			// Use default status of 500 above.
		}

		if (status == 200 || status == 201) {
			callback.run( {text:req.__httpReq.responseText, xml:req.__httpReq.responseXML, success:true, reqId:req.id} );
		} else {
			callback.run( {text:req.__httpReq.responseText, xml:req.__httpReq.responseXML, success:false, status:status, reqId:req.id} );
		}

		AjxRpc.freeRpcCtxt(req);
	}

	} catch (ex) {
		if (window.AjxException) {
			AjxException.reportScriptError(ex);
		}
	}
};

/**
 * @private
 */
AjxRpcRequest.__init =
function() {
	if (!window.XMLHttpRequest && window.ActiveXObject) {
		// search for the latest xmlhttp version on user's machine (IE 6)
		var msxmlVers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				var x = new ActiveXObject(msxmlVers[i]);
				AjxRpcRequest.__msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
				// do nothing
			}
		}
		if (!AjxRpcRequest.__msxmlVers) {
			throw new AjxException("MSXML not installed", AjxException.INTERNAL_ERROR, "AjxRpc._init");
		}
	}
	AjxRpcRequest.__inited = true;
};
