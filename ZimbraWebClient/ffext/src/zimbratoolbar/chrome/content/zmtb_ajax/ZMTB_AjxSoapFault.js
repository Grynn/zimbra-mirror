/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/* Represents a SOAP Fault
*
* Public attributes:
*
* - faultCode: The SOAP fault code
* - reason: Reason string
* - errorCode: server error code
*/
function ZMTB_AjxSoapFault(faultEl) {
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
				this.faultCode = ZMTB_AjxSoapFault.VERSION_MISMATCH;
			else if (faultCode == (prefix + ":MustUnderstand"))
				this.faultCode = ZMTB_AjxSoapFault.MUST_UNDERSTAND;
			else if (faultCode == (prefix + ":DataEncodingUnknown"))
				this.faultCode = ZMTB_AjxSoapFault.DATA_ENCODING_UNKNOWN;
			else if (faultCode == (prefix + ":Sender"))
				this.faultCode = ZMTB_AjxSoapFault.SENDER;
			else if (faultCode == (prefix + ":Receiver"))
				this.faultCode = ZMTB_AjxSoapFault.RECEIVER;
			else
				this.faultCode = ZMTB_AjxSoapFault.UNKNOWN;		
		} else if (childNode.nodeName == reasonStr) {
			this.reason = childNode.firstChild.firstChild.nodeValue;
		} else if (childNode.nodeName == detailStr) {
			this.errorCode = childNode.firstChild.firstChild.firstChild.nodeValue;
		}
	}
}

ZMTB_AjxSoapFault.prototype.toString = 
function() {
	return "ZMTB_AjxSoapFault";
}

ZMTB_AjxSoapFault.SENDER = -1;
ZMTB_AjxSoapFault.RECEIVER = -2;
ZMTB_AjxSoapFault.VERSION_MISMATCH = -3;
ZMTB_AjxSoapFault.MUST_UNDERSTAND = -4;
ZMTB_AjxSoapFault.DATA_ENCODING_UNKNOWN = -5;
ZMTB_AjxSoapFault.UNKNOWN = -6;
