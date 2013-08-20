/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2013 Zimbra Software, LLC.
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
 * Represents a SOAP Fault
 * @class
 * Public attributes:
 *
 * - faultCode: The SOAP fault code
 * - reason: Reason string
 * - errorCode: server error code
 * 
 * @private
 */
AjxSoapFault = function(faultEl) {
	if (arguments.length == 0) return;
	var prefix = faultEl.prefix;
	var codeStr = prefix + ":Code";
	var reasonStr = prefix + ":Reason";
	var detailStr = prefix + ":Detail"
	// We will assume a correctly formatted Fault element
	var len = faultEl.childNodes.length;
	for (var i = 0; i < len; i++) {
		var childNode = faultEl.childNodes[i];
		if (childNode.nodeName == codeStr) {
			var faultCode = childNode.firstChild.firstChild.nodeValue;
			if (faultCode == (prefix + ":VersionMismatch"))
				this.faultCode = AjxSoapFault.VERSION_MISMATCH;
			else if (faultCode == (prefix + ":MustUnderstand"))
				this.faultCode = AjxSoapFault.MUST_UNDERSTAND;
			else if (faultCode == (prefix + ":DataEncodingUnknown"))
				this.faultCode = AjxSoapFault.DATA_ENCODING_UNKNOWN;
			else if (faultCode == (prefix + ":Sender"))
				this.faultCode = AjxSoapFault.SENDER;
			else if (faultCode == (prefix + ":Receiver"))
				this.faultCode = AjxSoapFault.RECEIVER;
			else
				this.faultCode = AjxSoapFault.UNKNOWN;		
		} else if (childNode.nodeName == reasonStr) {
			this.reason = childNode.firstChild.firstChild.nodeValue;
		} else if (childNode.nodeName == detailStr) {
			this.errorCode = childNode.firstChild.firstChild.firstChild.nodeValue;
		}
	}
}

/**
 * Returns a string representation of this object.
 * 
 * @return	{string}	a string representation of this object
 */
AjxSoapFault.prototype.toString = 
function() {
	return "AjxSoapFault";
}

AjxSoapFault.SENDER = -1;
AjxSoapFault.RECEIVER = -2;
AjxSoapFault.VERSION_MISMATCH = -3;
AjxSoapFault.MUST_UNDERSTAND = -4;
AjxSoapFault.DATA_ENCODING_UNKNOWN = -5;
AjxSoapFault.UNKNOWN = -6;
