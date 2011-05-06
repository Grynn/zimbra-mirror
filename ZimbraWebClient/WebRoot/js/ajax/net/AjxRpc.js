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
 * This static class provides an interface for send requests to a server. It
 * essentially wraps {@link AjxRpcRequest}. This {@link AjxRpc} link maintains a cache of
 * {@link AjxRpcRequest} objects which it attempts to reuse before allocating additional
 * objects. It also has a mechanism whereby if an {@link AjxRpcRequest} object is 
 * in a "busy" state for a extended period of time, it will reap it appropriately.
 *
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 * @see AjxRpcRequest
 */
AjxRpc = function() {
};

AjxRpc.__rpcCache		= [];		// The pool of RPC contexts available
AjxRpc.__rpcOutstanding	= {};		// The pool of RPC contexts in use
AjxRpc.__RPC_CACHE_MAX	= 50;		// maximum number of busy contexts we can have
AjxRpc.__RPC_COUNT		= 0;
AjxRpc.__RPC_REAP_AGE	= 300000;	// mark any context older than this (in ms) as free

/**
 * Submits a request to a URL. The request is handled through a pool of request
 * contexts (each a wrapped XmlHttpRequest). The context does the real work.
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
 * @see	AjxRpcRequest#invoke
 * 
 */
AjxRpc.invoke =
function(requestStr, serverUrl, requestHeaders, callback, useGet, timeout) {

	var asyncMode = (callback != null);
	var rpcCtxt = AjxRpc.__getFreeRpcCtxt();

	try {
		var response = rpcCtxt.invoke(requestStr, serverUrl, requestHeaders, callback, useGet, timeout);
	} catch (ex) {
		var newEx = new AjxException();
		newEx.method = "AjxRpc.prototype._invoke";
		if (ex instanceof Error) {
			newEx.detail = ex.message;
			newEx.code = AjxException.NETWORK_ERROR;
			newEx.msg = "Network error";
		} else {
			newEx.detail = ex.toString();
			newEx.code = AjxException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
		}
		if (!asyncMode) {
			AjxRpc.freeRpcCtxt(rpcCtxt);
		}
		throw newEx;
	}
	if (!asyncMode) {
		// we're done using this rpcCtxt. Add it back to the pool
		AjxRpc.freeRpcCtxt(rpcCtxt);
	}
	return response;
};

/**
 * @private
 */
AjxRpc.freeRpcCtxt =
function(rpcCtxt) {
	// we're done using this rpcCtxt. Add it back to the pool
	if (AjxRpc.__rpcOutstanding[rpcCtxt.id]) {
		DBG.println("req", "--- freeing rpcCtxt " + rpcCtxt.id);
		AjxRpc.__rpcCache.push(rpcCtxt);
		delete AjxRpc.__rpcOutstanding[rpcCtxt.id];
	}
};

AjxRpc.removeRpcCtxt =
function(rpcCtxt) {
	DBG.println("req", "REMOVE rpcCtxt " + rpcCtxt.id);
	if (AjxRpc.__rpcOutstanding[rpcCtxt.id]) {
		delete AjxRpc.__rpcOutstanding[rpcCtxt.id];
	}
	AjxUtil.arrayRemove(AjxRpc.__rpcCache, rpcCtxt);
};

/**
 * Returns the request from the RPC context with the given ID.
 *
 * @param {String} id RPC context ID
 * 
 * @return The <i>AjxRpcRequest</i> object associated with <code>id</code> or null
 * 		if no object exists for the supplied id
 * @type AjxRpcRequest
 * 
 * @private
 */
AjxRpc.getRpcRequestById = 
function(id) {
	return (AjxRpc.__rpcOutstanding[id]);
};

/**
 * Factory method for getting context objects.
 * 
 * @private
 */
AjxRpc.__getFreeRpcCtxt = 
function() {
	var rpcCtxt;

	if (AjxRpc.__rpcCache.length > 0) {
		rpcCtxt = AjxRpc.__rpcCache.pop();
		DBG.println("req", "reusing RPC ID " + rpcCtxt.id);
		AjxDebug.println(AjxDebug.RPC, "reusing RPC ID " + rpcCtxt.id);
	} else {
		if (AjxRpc.__RPC_COUNT < AjxRpc.__RPC_CACHE_MAX) {
			// we haven't reached our limit, so create a new AjxRpcRequest
			var id = "__RpcCtxt_" + AjxRpc.__RPC_COUNT;
			rpcCtxt = new AjxRpcRequest(id);
			AjxRpc.__RPC_COUNT++;
			DBG.println("req", "Created RPC " + id + ", total created: " + AjxRpc.__RPC_COUNT);
			AjxDebug.println(AjxDebug.RPC, "Created RPC " + id + ", total created: " + AjxRpc.__RPC_COUNT);
		} else {
			// yikes, we're out of rpc's! Look for an old one to kill.
			rpcCtxt = AjxRpc.__reap();

			// if reap didn't find one either, bail.
			if (!rpcCtxt) {
				var text = [];
				for (var i in AjxRpc.__rpcOutstanding) {
					var rpcCtxt = AjxRpc.__rpcOutstanding[i];
					var req = rpcCtxt.requestStr;
					if (req) {
						text.push(req.replace(/"authToken":"\w*"/, '"authToken":"[removed]"'));
					}
				}
				var detail = text.join("<br><br>");
				AjxDebug.println(AjxDebug.RPC, "Out of RPC cache!!! Outstanding requests: " + detail);
				throw new AjxException("Out of RPC cache", AjxException.OUT_OF_RPC_CACHE, "AjxRpc.__getFreeRpcCtxt", detail);
			}
		}
	}

	AjxRpc.__rpcOutstanding[rpcCtxt.id] = rpcCtxt;

	// always reset timestamp before returning rpcCtxt
	rpcCtxt.timestamp = (new Date()).getTime();
	return rpcCtxt;
};

/**
 * Searches for an "expired" rpc. If found, cancels it and returns it.
 * @private
 */
AjxRpc.__reap =
function() {
	var rpcCtxt;
	var time = (new Date()).getTime();

	for (var i in AjxRpc.__rpcOutstanding) {
		rpcCtxt = AjxRpc.__rpcOutstanding[i];
		if (rpcCtxt.timestamp + AjxRpc.__RPC_REAP_AGE < time) {
			DBG.println(AjxDebug.DBG1, "AjxRpc.__reap: cleared RPC context " + rpcCtxt.id);
			AjxDebug.println(AjxDebug.RPC, "AjxRpc.__reap: cleared RPC context " + rpcCtxt.id);
			rpcCtxt.cancel();
			delete AjxRpc.__rpcOutstanding[i];
			return rpcCtxt;
		}
	}
	return null;
};
