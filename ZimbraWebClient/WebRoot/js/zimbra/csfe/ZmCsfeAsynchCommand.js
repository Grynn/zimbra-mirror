/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* @class ZmCsfeAsynchCommand
* This class executes Csfe commands assynchronously.
* When command is executed (Rpc call returns) all invokeListeners are notified.
* @ constructor
* @author Greg Solovyev
**/

function ZmCsfeAsynchCommand () {
	ZmCsfeCommand.call(this);
	this.invokeListeners = new Array();
	this.commandSoapDoc = "";
	this._responseSoapDoc = null;
	this._st = null; //start time (call sent)
	this._en = null; //end time (call returned)
}

ZmCsfeAsynchCommand.prototype = new ZmCsfeCommand;
ZmCsfeAsynchCommand.prototype.constructor = ZmCsfeAsynchCommand;

ZmCsfeAsynchCommand.prototype.toString = 
function () {
	return 	"ZmCsfeAsynchCommand";
}

/**
* @method addInvokeListener
* @param obj : AjxCallback
* use this method to be notified when rpc call returns.
* Callback receives an argument that is either responseSoapDocBody or exceptionObject
* -	_responseSoapDoc:AjxSoapDoc is a resonse SOAP document
**/
ZmCsfeAsynchCommand.prototype.addInvokeListener = 
function (obj) {
	this.invokeListeners.push(obj);
}

/**
* @method removeInvokeListener
* @param obj
* use this method to unsubscribe obj from events of this command object
**/
ZmCsfeAsynchCommand.prototype.removeInvokeListener = 
function (obj) {
	var cnt = this.invokeListeners.length;
	var ix = 0;
	for(; ix < cnt; ix++) {
		if (this.invokeListeners[ix] == obj) {
			this.invokeListeners[ix] = null;
			break;
		}
	}
}


ZmCsfeAsynchCommand.prototype._fireInvokeEvent = 
function (exceptionObject) {
	var cnt = this.invokeListeners.length;
	for(var ix = 0; ix < cnt; ix++) {
		if (this.invokeListeners[ix] != null) {
			if(exceptionObject) {
				this.invokeListeners[ix].run(exceptionObject);
			} else {
				if(this._responseSoapDoc) {
					this.invokeListeners[ix].run(this._responseSoapDoc);					
				} else {
					this.invokeListeners[ix].run(new ZmCsfeException("Csfe service error", AjxException.UNKNOWN_ERROR, "ZmCsfeAsynchCommand.prototype._fireInvokeEvent", "Service returned empty document"));				
				}
			}
		}
	}
}

/**
* @method rpcCallback
* @param response
* this method is called by XMLHttpRequest object's event handler.
response obejct contains the following properties
text, xml, success, status
**/
ZmCsfeAsynchCommand.prototype.rpcCallback = 
function (response) {
	this._en = new Date();
	DBG.println(AjxDebug.DBG1, "<H4>ASYNCHRONOUS REQUEST RETURNED</H4>");
	DBG.println(AjxDebug.DBG1, "ASYNCHRONOUS ROUND TRIP TIME: " + (this._en.getTime() - this._st.getTime()));	
	var newEx = null;
	if(!response.success) {
		try {
			var respDoc = AjxEnv.isIE || response.xml == null
							? AjxSoapDoc.createFromXml(response.text) 
							: AjxSoapDoc.createFromDom(response.xml);		
			if(respDoc.getBody()) {
				DBG.println(AjxDebug.DBG1, "<H4>RESPONSE</H4>");
				DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
			
				var fault = AjxSoapDoc.element2FaultObj(respDoc.getBody());
				if (fault) {
					newEx = new ZmCsfeException("Csfe service error", fault.errorCode, "ZmCsfeAsynchCommand.prototype.rpcCallback", fault.reason);
				}		
			} 							
		} catch (ex) {
			newEx =	new ZmCsfeException();
			newEx.method = "ZmCsfeAsynchCommand.prototype.rpcCallback";
			newEx.detail = "Unknown problem ecnountered while communicating to server. ";
			newEx.detail += "text: ";
			newEx.detail += response.text; 
			newEx.detail += "\n";
			newEx.detail += "xml: ";
			newEx.detail += response.xml;		
			newEx.detail += "\n";
			newEx.detail += "status: ";
			newEx.detail += response.status;		
			newEx.detail += "\n";
			newEx.code = ZmCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
		}
	} else {
		try {
			// responseXML is empty under IE and FF doesnt seem to populate xml if faulted
			var respDoc = AjxEnv.isIE || response.xml == null
							? AjxSoapDoc.createFromXml(response.text) 
							: AjxSoapDoc.createFromDom(response.xml);
			this._responseSoapDoc = respDoc;
			DBG.println(AjxDebug.DBG1, "<H4>RESPONSE</H4>");
			DBG.printXML(AjxDebug.DBG1, respDoc.getXml());
		} catch (ex) {
			if ((ex instanceof AjxSoapException) || (ex instanceof AjxException)) {
				newEx =	ex;
			} else {
				newEx =	new ZmCsfeException();
				newEx.method = "ZmCsfeAsynchCommand.prototype.rpcCallback";
				newEx.detail = ex.toString();
				newEx.code = ZmCsfeException.UNKNOWN_ERROR;
				newEx.msg = "Unknown Error";
			}
		}
		try {
			//check if we received a Fault message from server
			var fault = AjxSoapDoc.element2FaultObj(this._responseSoapDoc.getBody());
			if (fault) {
				newEx = new ZmCsfeException("Csfe service error", fault.errorCode, "ZmCsfeAsynchCommand.prototype.rpcCallback", fault.reason);
			}
		} catch (ex) {
			newEx = ex;
		}
	}
	//call event listeners
	this._fireInvokeEvent(newEx);
}

ZmCsfeAsynchCommand.prototype.invoke = 
function (soapDoc, noAuthTokenRequired, serverUri, targetServer, useXml) {
	if (!noAuthTokenRequired) {
		var authToken = ZmCsfeCommand.getAuthToken();
		if (authToken == null)
			throw new ZmCsfeException("AuthToken required", ZmCsfeException.NO_AUTH_TOKEN, "ZmCsfeCommand.invoke");
		var sessionId = ZmCsfeCommand.getSessionId();
		var hdr = soapDoc.createHeaderElement();
		var ctxt = soapDoc.set("context", null, hdr);
		ctxt.setAttribute("xmlns", "urn:zimbra");
		soapDoc.set("authToken", authToken, ctxt);
		if (sessionId != null)
			soapDoc.set("sessionId", sessionId, ctxt);
		if(targetServer != null)
			soapDoc.set("targetServer", targetServer, ctxt);
	}
	
	if (!useXml) {
		var js = soapDoc.set("format", null, ctxt);
		js.setAttribute("type", "js");
	}
	
	try {
		DBG.println(AjxDebug.DBG1, "<H4>ASYNCHRONOUS REQUEST</H4>");
		DBG.printXML(AjxDebug.DBG1, soapDoc.getXml());
		var uri = serverUri || ZmCsfeCommand.serverUri;
		var requestStr = !AjxEnv.isSafari 
			? soapDoc.getXml() 
			: soapDoc.getXml().replace("soap=", "xmlns:soap=");
			
		this._st = new Date();
		AjxRpc.invoke(requestStr, uri,  {"Content-Type": "application/soap+xml; charset=utf-8"}, new AjxCallback(this, ZmCsfeAsynchCommand.prototype.rpcCallback)); //asynchronous call returns null 
	} catch (ex) {
		//JavaScript error, network error or unknown error may happen
		var newEx = new ZmCsfeException();
		newEx.method = "ZmCsfeCommand.invoke";
		if (ex instanceof AjxException) {
			newEx.detail = ex.msg + ": " + ex.code + " (" + ex.method + ")";
			newEx.msg = "Network Error";
			newEx.code = ex.code;
		} else {
			newEx.detail = ex.toString();
			newEx.code = ZmCsfeException.UNKNOWN_ERROR;
			newEx.msg = "Unknown Error";
		}
		//notify listeners
		this._fireInvokeEvent(newEx);
	}	
}
