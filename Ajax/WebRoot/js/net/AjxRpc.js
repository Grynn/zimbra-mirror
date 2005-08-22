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

AjxRpc._rpcCache = new Array();
AjxRpc._RPC_CACHE_MAX = 10;

function AjxRpc() {
}

/**
* @method public static AjxRpc.invoke
* @param requestStr - http request string
* @param serverUrl - URI for HTTP request
* @requestHeaders - HTTP request headers
* @param callback - AjxCallback instance
**/
AjxRpc.invoke =
function(requestStr, serverUrl, requestHeaders, callback) {
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
		if(!callback)		
			rpcCtxt.rpcRequestObj.busy = false;
		throw newEx;
	}
	if(!callback)
		rpcCtxt.rpcRequestObj.busy = false;
	return response;
}


/**
* inner class _RpcCtxt
 members:
 	rpcRequestObj : AjxRpcRequest
**/
function _RpcCtxt() {
	this.rpcRequestObj = new AjxRpcRequest(true);
}

/**
* singleton factory of _RpcCtxt objects
**/
AjxRpc._getRpcCtxt = 
function() {
	var rpcCtxt = null
	for (var i = 0; i < AjxRpc._rpcCache.length; i++) {
		rpcCtxt = AjxRpc._rpcCache[i];
		if (!rpcCtxt.rpcRequestObj.busy) {
			DBG.println(AjxDebug.DBG1, "Found free Rpc Context in cache");
			break;
		}
	}
	
	// if we didnt find a non-busy rpc cache, create new one
	if (i == AjxRpc._rpcCache.length) {
		if (AjxRpc._rpcCache.length == AjxRpc._RPC_CACHE_MAX) {
			DBG.println(AjxDebug.DBG1, "Out of RPC Contexts");
			throw new AjxException("Out of RPC cache", AjxException.OUT_OF_RPC_CACHE, "ZmCsfeCommand._getRpcCtxt");	
		}
		rpcCtxt = new _RpcCtxt();
		AjxRpc._rpcCache.push(rpcCtxt);
		
		// XXX: this should never eval to true in synchronous mode
		//      REMOVE THIS CHECK WHEN ASYNCH HAS BEEN IMPLEMENTED
		if (AjxRpc._rpcCache.length > 1)
			DBG.println("XXXX: ---- more than one rpc cache created ---- :XXXX");
	}
	rpcCtxt.rpcRequestObj.busy = true;
	return rpcCtxt;
}
