function DwtException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	AjxException.call(this, msg, code, method, detail);
}

DwtException.prototype = new AjxException;
DwtException.prototype.constructor = DwtException;

DwtException.prototype.toString = 
function() {
	return "DwtException";
}

DwtException.INVALIDPARENT = -1;
DwtException.INVALID_OP = -2;
DwtException.INTERNAL_ERROR = -3;
DwtException.INVALID_PARAM = -4;