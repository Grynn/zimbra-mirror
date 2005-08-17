function LsException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	this.msg = msg;
	this.code = code;
	this.method = method;
	this.detail = detail;
}

LsException.prototype.toString = 
function() {
	return "LsException";
}

LsException.prototype.dump = 
function() {
	return "LsException: msg="+this.msg+" code="+this.code+" method="+this.method+" detail="+this.detail;
}
LsException.INVALIDPARENT 			= "LsException.INVALIDPARENT";
LsException.INVALID_OP 				= "LsException.INVALID_OP";
LsException.INTERNAL_ERROR 			= "LsException.INTERNAL_ERROR";
LsException.INVALID_PARAM 			= "LsException.INVALID_PARAM";
LsException.UNIMPLEMENTED_METHOD 	= "LsException.UNIMPLEMENTED_METHOD";
LsException.NETWORK_ERROR 			= "LsException.NETWORK_ERROR";
LsException.OUT_OF_RPC_CACHE		= "LsException.OUT_OF_RPC_CACHE";
LsException.UNSUPPORTED 			= "LsException.UNSUPPORTED";
LsException.UNKNOWN_ERROR 			= "LsException.UNKNOWN_ERROR";
