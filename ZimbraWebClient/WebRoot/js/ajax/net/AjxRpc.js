/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
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

AjxRpc.__RPC_CACHE_MAX		= 50;		// maximum number of busy contexts we can have
AjxRpc.__RPC_ID				= 0;		// used for context IDs
AjxRpc.__RPC_IN_USE			= 0;		// number of contexts that are busy
AjxRpc.__RPC_HIGH_WATER		= 0;		// high water mark for busy contexts
AjxRpc.__RPC_REAP_AGE		= 300000;	// 5 minutes; mark any context older than this (in ms) as free
AjxRpc.__RPC_REAP_INTERVAL	= 1800000;	// 30 minutes; run the reaper this often

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
 * @param {Constant} [method] 		the HTTP method -- GET, POST, PUT, DELETE. if <code>true</code>, use get method for backward compatibility
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
 * 		If there is an error, then the following will be returned
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
function(requestStr, serverUrl, requestHeaders, callback, method, timeout) {

	var asyncMode = (callback != null);
	var rpcCtxt = AjxRpc.__getFreeRpcCtxt();

	try {
		var response = rpcCtxt.invoke(requestStr, serverUrl, requestHeaders, callback, method, timeout);
	} catch (ex) {
		var newEx = new AjxException();
		newEx.method = "AjxRpc.prototype._invoke";
		if (ex instanceof Error) {
			newEx.detail = ex.message;
			newEx.code = AjxException.NETWORK_ERROR;
			newEx.msg = "Network error";
		} else if (ex.code == 101){
			// Chrome 
			newEx.detail = ex.message;
			newEx.code = AjxException.NETWORK_ERROR;
			newEx.msg = "Network error";
		} else {
			newEx.detail = ex.toString();
			newEx.code = AjxException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
		}
		// exception hit: we're done whether sync or async, free the context
		AjxRpc.freeRpcCtxt(rpcCtxt);
		throw newEx;
	}
	if (!asyncMode) {
		// we've returned from a sync request, free the context
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
		AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " --- freeing rpcCtxt " + rpcCtxt.id);
		AjxRpc.__rpcCache.push(rpcCtxt);
		delete AjxRpc.__rpcOutstanding[rpcCtxt.id];
		AjxRpc.__RPC_IN_USE--;
	}
};

AjxRpc.removeRpcCtxt =
function(rpcCtxt) {
	AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " REMOVE rpcCtxt " + rpcCtxt.id);
	if (AjxRpc.__rpcOutstanding[rpcCtxt.id]) {
		delete AjxRpc.__rpcOutstanding[rpcCtxt.id];
		AjxRpc.__RPC_IN_USE--;
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
		AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " reusing RPC ID " + rpcCtxt.id);
	} else {
		if (AjxRpc.__RPC_IN_USE < AjxRpc.__RPC_CACHE_MAX) {
			// we haven't reached our limit, so create a new AjxRpcRequest
			var id = "__RpcCtxt_" + AjxRpc.__RPC_ID;
			rpcCtxt = new AjxRpcRequest(id);
			AjxRpc.__RPC_ID++;
			AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " Created RPC " + id);
		} else {
			// yikes, we're out of rpc's! Look for an old one to kill.
			rpcCtxt = AjxRpc.__reap();

			// if reap didn't find one either, bail.
			if (!rpcCtxt) {
				var text = [];
				for (var i in AjxRpc.__rpcOutstanding) {
					var rpcCtxt = AjxRpc.__rpcOutstanding[i];
					text.push(rpcCtxt.methodName);
				}
				var detail = text.join("<br>") + "<br>";
				AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " Out of RPC cache!!! Outstanding requests: " + detail);
				throw new AjxException("Out of RPC cache", AjxException.OUT_OF_RPC_CACHE, "AjxRpc.__getFreeRpcCtxt", detail);
			}
		}
	}

	AjxRpc.__rpcOutstanding[rpcCtxt.id] = rpcCtxt;
	AjxRpc.__RPC_IN_USE++;
	if (AjxRpc.__RPC_IN_USE > AjxRpc.__RPC_HIGH_WATER) {
		AjxRpc.__RPC_HIGH_WATER = AjxRpc.__RPC_IN_USE;
		AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " High water mark: " + AjxRpc.__RPC_HIGH_WATER);
	}

	// always reset timestamp before returning rpcCtxt
	rpcCtxt.timestamp = (new Date()).getTime();
	return rpcCtxt;
};

/**
 * Frees expired contexts.
 * 
 * @param {boolean}	all		if true, frees all expired contexts; otherwise, returns the first one it finds
 * @private
 */
AjxRpc.__reap =
function(all) {
	var rpcCtxt;
	var time = (new Date()).getTime();
	AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " Running RPC context reaper");
	for (var i in AjxRpc.__rpcOutstanding) {
		rpcCtxt = AjxRpc.__rpcOutstanding[i];
		if ((rpcCtxt.timestamp + AjxRpc.__RPC_REAP_AGE) < time) {
			DBG.println(AjxDebug.DBG1, "AjxRpc.__reap: cleared RPC context " + rpcCtxt.id);
			AjxDebug.println(AjxDebug.RPC, AjxDebug._getTimeStamp() + " AjxRpc.__reap: cleared RPC context " + rpcCtxt.id);
			rpcCtxt.cancel();
			delete AjxRpc.__rpcOutstanding[i];
			AjxRpc.__RPC_IN_USE--;
			if (!all) {
				return rpcCtxt;
			}
		}
	}
	return null;
};

window.setInterval(AjxRpc.__reap.bind(null, true), AjxRpc.__RPC_REAP_INTERVAL);
