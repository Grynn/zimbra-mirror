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
 * This static class provides an interface for send requests to a server. It
 * essentially wraps <i>ZMTB_AjxRpcRequest</i>. <i>ZMTB_AjxRpc</i> maintainse a cache of
 * <i>ZMTB_AjxRpcRequest</i> objects which it attempts to reuse before allocating additional
 * objects. It also has a mechanism whereby if an <i>AjxRcpRequest</i> object is 
 * in a "busy" state for a extended period of time, it will reap it appropriately.
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @see ZMTB_AjxRpcRequest
**/
function ZMTB_AjxRpc() {
};

/** The pool of RPC contexts
 * @private*/
ZMTB_AjxRpc.__rpcCache = [];

/** maximum number of busy contexts we can have
 * @private */
ZMTB_AjxRpc.__RPC_CACHE_MAX = 100;

/** run reaper when number of busy contexts is multiple of this
 * @private */
ZMTB_AjxRpc.__RPC_REAP_COUNT = 5;

/** mark any context older than this (in ms) as free
 * @private */
ZMTB_AjxRpc.__RPC_REAP_AGE = 300000;

/**
 * Submits a request to a URL. The request is handled through a pool of request
 * contexts (each a wrapped XmlHttpRequest). The context does the real work.
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
 * @see ZMTB_AjxRpcRequest#invoke
 * 
 * @requires AjxDebug
 * @requires ZMTB_AjxException
 * @requires ZMTB_AjxRpcRequest
 */
ZMTB_AjxRpc.invoke =
function(requestStr, serverUrl, requestHeaders, callback, useGet, timeout) {

	var asyncMode = (callback != null);
	var rpcCtxt = ZMTB_AjxRpc.__getFreeRpcCtxt();

	try {
	 	var response = rpcCtxt.req.invoke(requestStr, serverUrl, requestHeaders, callback, useGet, timeout);
	} catch (ex) {
		var newEx = new ZMTB_AjxException();
		newEx.method = "ZMTB_AjxRpc.prototype._invoke";
		if (ex instanceof Error) {
			newEx.detail = ex.message;
			newEx.code = ZMTB_AjxException.NETWORK_ERROR;
			newEx.msg = "Network error";
		} else {
			newEx.detail = ex.toString();
			newEx.code = ZMTB_AjxException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
		}
		if (!asyncMode)		
			rpcCtxt.busy = false;
		throw newEx;
	}
	if (!asyncMode)
		rpcCtxt.busy = false;
	return response;
};

/**
 * Returns the request from the RPC context with the given ID.
 *
 * @param {String} id RPC context ID
 * 
 * @return The <i>ZMTB_AjxRpcRequest</i> object associated with <code>id</code> or null
 * 		if no object exists for the supplied id
 * @type ZMTB_AjxRpcRequest
 */
ZMTB_AjxRpc.getRpcRequest = 
function(id) {
	for (var i = 0; i < ZMTB_AjxRpc.__rpcCache.length; i++) {
		var rpcCtxt = ZMTB_AjxRpc.__rpcCache[i];
		if (rpcCtxt.id == id)
			return rpcCtxt.req;
	}
	return null;
};


/**
 * Factory method for getting context objects.
 * @private
 */
ZMTB_AjxRpc.__getFreeRpcCtxt = 
function() {

	var rpcCtxt = null;
	
	// See if we have one in the pool that's now free
	for (var i = 0; i < ZMTB_AjxRpc.__rpcCache.length; i++) {
		rpcCtxt = ZMTB_AjxRpc.__rpcCache[i];
		if (!rpcCtxt.busy) {
//			DBG.println(AjxDebug.DBG1, "Found free RPC context: " + rpcCtxt.id);
			break;
		}
	}
	
	// If there's no free context available, create one
	if (i == ZMTB_AjxRpc.__rpcCache.length) {
		if (ZMTB_AjxRpc.__rpcCache.length == ZMTB_AjxRpc.__RPC_CACHE_MAX) {
//			DBG.println(AjxDebug.DBG1, "Out of RPC contexts");
			throw new ZMTB_AjxException("Out of RPC cache", ZMTB_AjxException.OUT_OF_RPC_CACHE, "ZMTB_AjxRpc.__getFreeRpcCtxt");
		} else if (i > 0 && (i % ZMTB_AjxRpc.__RPC_REAP_COUNT == 0)) {
//			DBG.println(AjxDebug.DBG1, i + " busy RPC contexts");
			ZMTB_AjxRpc.__reap();
		}
		var id = "__RpcCtxt_" + i;
		rpcCtxt = new __RpcCtxt(id);
//		DBG.println(AjxDebug.DBG1, "Created RPC " + id);
		ZMTB_AjxRpc.__rpcCache.push(rpcCtxt);
	}
	rpcCtxt.busy = true;
	rpcCtxt.timestamp = (new Date()).getTime();
	return rpcCtxt;
};

/**
 * Frees up busy contexts that are older than a certain age.
 * @private
 */
ZMTB_AjxRpc.__reap =
function() {
	var time = (new Date()).getTime();
	for (var i = 0; i < ZMTB_AjxRpc.__rpcCache.length; i++) {
		var rpcCtxt = ZMTB_AjxRpc.__rpcCache[i];
		if (rpcCtxt.timestamp + ZMTB_AjxRpc.__RPC_REAP_AGE < time) {
//			DBG.println(AjxDebug.DBG1, "ZMTB_AjxRpc.__reap: cleared RPC context " + rpcCtxt.id);
			rpcCtxt.req.cancel();
			rpcCtxt.busy = false;
		}
	}

};

/**
* Wrapper class for a request context.
*
* @param {String|Int} id Unique ID for this context
* @private
*/
function __RpcCtxt(id) {
	this.id = id;
	this.req = new ZMTB_AjxRpcRequest(id);
	this.req.__setCtxt(this)
	this.busy = false;
};
