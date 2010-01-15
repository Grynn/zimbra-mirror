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



/**
 * @constructor
 * @class
 * This class encapsulates the XML HTTP request, hiding differences between
 * browsers. The internal request object depends on the browser. While it is 
 * possible to use this class directly, <i>ZMTB_AjxRpc</i> provides a managed interface
 * to this class 
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @param {String} Optional ID to identify this object (optional)
 * 
 * @see ZMTB_AjxRpc
 * 
 * @requires ZMTB_AjxCallback
 * @requires AjxDebug
 * @requires ZMTB_AjxEnv
 * @requires ZMTB_AjxException
 * @requires ZMTB_AjxTimedAction
 */
function ZMTB_AjxRpcRequest(id) {
	if (!ZMTB_AjxRpcRequest.__inited) {
		ZMTB_AjxRpcRequest.__init();
    }

	/** (optional) id for this object.
	 * @type String|Int */
    this.id = id;
	/** private*/
    this.__httpReq = ZMTB_AjxRpcRequest.__msxmlVers ? new ActiveXObject(ZMTB_AjxRpcRequest.__msxmlVers) :
    											 new XMLHttpRequest();
};

/**
 * Timed out exception
 * @type Int
 */
ZMTB_AjxRpcRequest.TIMEDOUT = -1000;

/** @private */
ZMTB_AjxRpcRequest.__inited = false;

/** @private */
ZMTB_AjxRpcRequest.__msxmlVers = null;

/**
 * @return class name
 * @type String
 */
ZMTB_AjxRpcRequest.prototype.toString = 
function() {
	return "ZMTB_AjxRpcRequest";
};

/**
 * Sends this request to the target URL. If there is a callback, the request is
 * performed asynchronously.
 * 
 * @param {String} requestStr HTTP request string/document
 * @param {String} serverUrl request target 
 * @param {Array} requestHeaders Array of HTTP request headers (optional)
 * @param {ZMTB_AjxCallback} callback callback for asynchronous requests. This callback 
 * 		will be invoked when the requests completes. It will be passed the same
 * 		values as when this method is invoked synchronously (see the return values
 * 		below) with the exception that if the call times out (see timeout param 
 * 		below), then the object passed to the callback will be the same as in the 
 * 		error case with the exception that the status will be set to 
 * 		<code>ZMTB_AjxRpcRequest.TIMEDOUT</code>
 * @param {Boolean} useGet if true use get method, else use post. If ommitted
 * 		defaults to post
 * @param {Int} timeout Timeout (in milliseconds) after which the request is 
 * 		cancelled (optional)
 * 
 * @return If invoking in asynchronous mode, then it will return the id of the 
 * 		underlying <i>ZMTB_AjxRpcRequest</i> object. Else if invoked synchronously, if
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
 * 		<li>success - boolean set to false </li>
 * 		<li>status - http status</li>
 * 		</ul>
 * @type Object
 * 
 * @throws ZMTB_AjxException.NETWORK_ERROR, ZMTB_AjxException.UNKNOWN_ERROR
 * 
 * @see ZMTB_AjxRpc#invoke
 */
ZMTB_AjxRpcRequest.prototype.invoke =
function(requestStr, serverUrl, requestHeaders, callback, useGet, timeout) {

	var asyncMode = (callback != null);
	
	// An exception here will be caught by ZMTB_AjxRpc.invoke
	this.__httpReq.open((useGet) ? "get" : "post", serverUrl, asyncMode);

	if (asyncMode) {
		this.__callback = callback;
        if (timeout) {
            var action = new ZMTB_AjxTimedAction(this, ZMTB_AjxRpcRequest.__handleTimeout, {callback: callback, req: this});
            callback._timedActionId = ZMTB_AjxTimedAction.scheduleAction(action, timeout);
        }
        var tempThis = this;
//		DBG.println(AjxDebug.DBG3, "Async RPC request");
		this.__httpReq.onreadystatechange = function(ev) {ZMTB_AjxRpcRequest.__handleResponse(tempThis, callback);};
	} else {
		// IE appears to run handler even on sync requests, so we need to clear it
		this.__httpReq.onreadystatechange = function(ev) {};
	}

	if (requestHeaders) {
		for (var i in requestHeaders) {
			this.__httpReq.setRequestHeader(i, requestHeaders[i]);
//			DBG.println(AjxDebug.DBG3, "Async RPC request: Add header " + i + " - " + requestHeaders[i]);
		}
	}

	this.__httpReq.send(requestStr);
	if (asyncMode) {
		return this.id;
	} else {
		if (this.__httpReq.status == 200) {
			return {text: this.__httpReq.responseText, xml: this.__httpReq.responseXML, success: true};
		} else {
			return {text: this.__httpReq.responseText, xml: this.__httpReq.responseXML, success: false, status: this.__httpReq.status};
		}
	}
};

/**
 * Cancels a pending request 
 */
ZMTB_AjxRpcRequest.prototype.cancel =
function() {
//	DBG.println(AjxDebug.DBG1, "Aborting HTTP request");
	this.__httpReq.abort();
};

/**
 * Handler that runs when an asynchronous request timesout.
 *
 * @param {ZMTB_AjxCallback} callback callback to run after timeout
 * 
 * @private
 */
ZMTB_AjxRpcRequest.__handleTimeout =
function(args) {
//    DBG.println(AjxDebug.DBG3, "Async RPC request: _handleTimeout");
    args.req.cancel();
    args.callback.run( {text: null, xml: null, success: false, status: ZMTB_AjxRpcRequest.TIMEDOUT} );
};

/**
 * Handler that runs when an asynchronous response has been received. It runs a
 * callback to initiate the response handling.
 *
 * @param req		[ZMTB_AjxRpcRequest]		request that generated the response
 * @param callback	[ZMTB_AjxCallback]		callback to run after response is received
 * 
 * @private
 */
ZMTB_AjxRpcRequest.__handleResponse =
function(req, callback) {
	if (!req || !req.__httpReq) {
		// If IE receives a 500 error, the object reference can be lost
		// DBG.println(AjxDebug.DBG1, "Async RPC request: Lost request object!!!");
		callback.run( {text: null, xml: null, success: false, status: 500} );
		return;
	}
    //DBG.println(AjxDebug.DBG3, "Async RPC request: ready state = " + req.__httpReq.readyState);
	if (req.__httpReq.readyState == 4) {
        if(callback._timedActionId !== null) {
            ZMTB_AjxTimedAction.cancelAction(callback._timedActionId);
        }
        var status = 500;
        try {
            status = req.__httpReq.status;
        } catch (ex) {
            // Use default status of 500 above.
        }
        if (status == 200) {
			callback.run( {text: req.__httpReq.responseText, xml: req.__httpReq.responseXML, success: true} );				
        } else {
			callback.run( {text: req.__httpReq.responseText, xml: req.__httpReq.responseXML, success: false, status: status} );
        }
		
		if (req.__ctxt)
			req.__ctxt.busy = false;
	}
};


/**
 * This method is called by <i>ZMTB_AjxRpc</i> which can be considered a "friend" clas
 * It is used to set the __RpcCtxt associated with this object
 * @private
 */
ZMTB_AjxRpcRequest.prototype.__setCtxt =
function(ctxt) {
	this.__ctxt = ctxt;
};

/** @private */
ZMTB_AjxRpcRequest.__init =
function() {
	if (!window.XMLHttpRequest && window.ActiveXObject) {
		// search for the latest xmlhttp version on user's machine (IE 6)
		var msxmlVers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
		for (var i = 0; i < msxmlVers.length; i++) {
			try {
				var x = new ActiveXObject(msxmlVers[i]);
				ZMTB_AjxRpcRequest.__msxmlVers = msxmlVers[i];
				break;
			} catch (ex) {
				// do nothing
			}
		}
		if (!ZMTB_AjxRpcRequest.__msxmlVers) {
			throw new ZMTB_AjxException("MSXML not installed", ZMTB_AjxException.INTERNAL_ERROR, "ZMTB_AjxRpc._init");
        }
    }
	ZMTB_AjxRpcRequest.__inited = true;
};

