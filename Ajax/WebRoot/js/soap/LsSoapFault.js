/* Represents a SOAP Fault
*
* Public attributes:
*
* - faultCode: The SOAP fault code
* - reason: Reason string
* - errorCode: server error code
*/
function LsSoapFault(faultEl) {
	if (arguments.length == 0) return;
	var prefix = faultEl.prefix;
	var codeStr = prefix + ":Code";
	var reasonStr = prefix + ":Reason";
	var detailStr = prefix + ":Detail"
	// We will assume a correctly formatted Fault element
	for (var i = 0; i < faultEl.childNodes.length; i++) {
		var childNode = faultEl.childNodes[i];
		if (childNode.nodeName == codeStr) {
			var faultCode = childNode.firstChild.firstChild.nodeValue;
			if (faultCode == (prefix + ":VersionMismatch"))
				this.faultCode = LsSoapFault.VERSION_MISMATCH;
			else if (faultCode == (prefix + ":MustUnderstand"))
				this.faultCode = LsSoapFault.MUST_UNDERSTAND;
			else if (faultCode == (prefix + ":DataEncodingUnknown"))
				this.faultCode = LsSoapFault.DATA_ENCODING_UNKNOWN;
			else if (faultCode == (prefix + ":Sender"))
				this.faultCode = LsSoapFault.SENDER;
			else if (faultCode == (prefix + ":Receiver"))
				this.faultCode = LsSoapFault.RECEIVER;
			else
				this.faultCode = LsSoapFault.UNKNOWN;		
		} else if (childNode.nodeName == reasonStr) {
			this.reason = childNode.firstChild.firstChild.nodeValue;
		} else if (childNode.nodeName == detailStr) {
			this.errorCode = childNode.firstChild.firstChild.firstChild.nodeValue;
		}
	}
}

LsSoapFault.prototype.toString = 
function() {
	return "LsSoapFault";
}

LsSoapFault.SENDER = -1;
LsSoapFault.RECEIVER = -2;
LsSoapFault.VERSION_MISMATCH = -3;
LsSoapFault.MUST_UNDERSTAND = -4;
LsSoapFault.DATA_ENCODING_UNKNOWN = -5;
LsSoapFault.UNKNOWN = -6;
