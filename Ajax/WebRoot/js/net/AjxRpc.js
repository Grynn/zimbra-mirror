/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function AjxRpc() {
}

// pool of RPC contexts
AjxRpc._rpcCache = new Array();
AjxRpc._RPC_CACHE_MAX = 10;

/**
* Submits a request to a URL. The request is handled through a pool of request
* contexts (each a wrapped XmlHttpRequest). The context does the real work.
*
* @param requestStr		[string]		HTTP request string/document
* @param serverUrl		[string]		request target
* @param requestHeaders	[Array]			HTTP request headers
* @param callback		[AjxCallback]	callback (for async requests)
*/
AjxRpc.invoke =
function(requestStr, serverUrl, requestHeaders, callback) {

	var asyncMode = (callback != null);
	var rpcCtxt = AjxRpc._getRpcCtxt();

	try {
	 	var response = rpcCtxt.rpcRequestObj.invoke(requestStr, serverUrl, requestHeaders, callback);
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
		if (!asyncMode)		
			rpcCtxt.rpcRequestObj.busy = false;
		throw newEx;
	}
	if (!asyncMode)
		rpcCtxt.rpcRequestObj.busy = false;

	return response;
}


/**
* Wrapper for a request context.
*
* @param id		unique ID for this context
*/
function _RpcCtxt(id) {
	this.id = id;
	this.rpcRequestObj = new AjxRpcRequest(id);
}

/*
* Factory method for getting context objects.
*/
AjxRpc._getRpcCtxt = 
function() {

	var rpcCtxt = null;
	
	// See if we have one in the pool that's now free
	for (var i = 0; i < AjxRpc._rpcCache.length; i++) {
		rpcCtxt = AjxRpc._rpcCache[i];
		if (!rpcCtxt.rpcRequestObj.busy) {
			DBG.println(AjxDebug.DBG1, "Found free RPC context");
			break;
		}
	}
	
	// If there's no free context available, create one
	if (i == AjxRpc._rpcCache.length) {
		if (AjxRpc._rpcCache.length == AjxRpc._RPC_CACHE_MAX) {
			DBG.println(AjxDebug.DBG1, "Out of RPC contexts");
			throw new AjxException("Out of RPC cache", AjxException.OUT_OF_RPC_CACHE, "ZmCsfeCommand._getRpcCtxt");	
		}
		var id = "_rpcCtxt_" + i;
		rpcCtxt = new _RpcCtxt(id);
		DBG.println(AjxDebug.DBG1, "Created RPC " + id);
		AjxRpc._rpcCache.push(rpcCtxt);
	}
	rpcCtxt.rpcRequestObj.busy = true;
	return rpcCtxt;
}
