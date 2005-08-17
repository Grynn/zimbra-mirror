function AjxSoapException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	AjxException.call(this, msg, code, method, detail);
}

AjxSoapException.prototype.toString = 
function() {
	return "AjxSoapException";
}

AjxSoapException.prototype = new AjxException;
AjxSoapException.prototype.constructor = AjxSoapException;

AjxSoapException.INTERNAL_ERROR 		= "INTERNAL_ERROR";
AjxSoapException.INVALID_PDU 		= "INVALID_PDU";
AjxSoapException.ELEMENT_EXISTS 		= "ELEMENT_EXISTS";
