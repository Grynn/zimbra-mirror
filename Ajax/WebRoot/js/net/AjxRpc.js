LsRpc._rpcCache = new Array();
LsRpc._RPC_CACHE_MAX = 10;

function LsRpc() {
}

/**
* @method public static LsRpc.invoke
* @param requestStr - http request string
* @param serverUrl - URI for HTTP request
* @requestHeaders - HTTP request headers
* @param callback - LsCallback instance
**/
LsRpc.invoke =
function(requestStr, serverUrl, requestHeaders, callback) {
	var rpcCtxt = LsRpc._getRpcCtxt();

	try {
	 	var response = rpcCtxt.rpcRequestObj.invoke(requestStr, serverUrl, requestHeaders, callback);
	} catch (ex) {
		var newEx = new LsException();
		newEx.method = "LsRpc.prototype._invoke";
		if (ex instanceof Error) {
			newEx.detail = ex.message;
			newEx.code = LsException.NETWORK_ERROR;
			newEx.msg = "Network error";
		} else {
			newEx.detail = ex.toString();
			newEx.code = LsException.UNKNOWN_ERROR;
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
 	rpcRequestObj : LsRpcRequest
**/
function _RpcCtxt() {
	this.rpcRequestObj = new LsRpcRequest(true);
}

/**
* singleton factory of _RpcCtxt objects
**/
LsRpc._getRpcCtxt = 
function() {
	var rpcCtxt = null
	for (var i = 0; i < LsRpc._rpcCache.length; i++) {
		rpcCtxt = LsRpc._rpcCache[i];
		if (!rpcCtxt.rpcRequestObj.busy) {
			DBG.println(LsDebug.DBG1, "Found free Rpc Context in cache");
			break;
		}
	}
	
	// if we didnt find a non-busy rpc cache, create new one
	if (i == LsRpc._rpcCache.length) {
		if (LsRpc._rpcCache.length == LsRpc._RPC_CACHE_MAX) {
			DBG.println(LsDebug.DBG1, "Out of RPC Contexts");
			throw new LsException("Out of RPC cache", LsException.OUT_OF_RPC_CACHE, "LsCsfeCommand._getRpcCtxt");	
		}
		rpcCtxt = new _RpcCtxt();
		LsRpc._rpcCache.push(rpcCtxt);
		
		// XXX: this should never eval to true in synchronous mode
		//      REMOVE THIS CHECK WHEN ASYNCH HAS BEEN IMPLEMENTED
		if (LsRpc._rpcCache.length > 1)
			DBG.println("XXXX: ---- more than one rpc cache created ---- :XXXX");
	}
	rpcCtxt.rpcRequestObj.busy = true;
	return rpcCtxt;
}
