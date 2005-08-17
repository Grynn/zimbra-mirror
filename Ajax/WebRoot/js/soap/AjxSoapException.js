function LsSoapException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	LsException.call(this, msg, code, method, detail);
}

LsSoapException.prototype.toString = 
function() {
	return "LsSoapException";
}

LsSoapException.prototype = new LsException;
LsSoapException.prototype.constructor = LsSoapException;

LsSoapException.INTERNAL_ERROR 		= "INTERNAL_ERROR";
LsSoapException.INVALID_PDU 		= "INVALID_PDU";
LsSoapException.ELEMENT_EXISTS 		= "ELEMENT_EXISTS";
