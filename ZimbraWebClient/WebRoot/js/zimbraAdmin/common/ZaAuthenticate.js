/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaAuthenticate(appCtxt) {
	if (arguments.length == 0) return;
	this._appCtxt = appCtxt;
	this._uname = "";
}

ZaAuthenticate._isAdmin = true;

ZaAuthenticate.prototype.toString = 
function() {
	return "ZaAuthenticate";
}

ZaAuthenticate.prototype.execute =
function (uname, pword, isPublic) {
	var soapDoc = AjxSoapDoc.create("AuthRequest", "urn:zimbraAdmin", null);
	
//	var header = soapDoc.createHeaderElement();
//	var context = soapDoc.set("context", null, header);
//	context.setAttribute("xmlns", "urn:zimbraAdmin");
//	soapDoc.set("nosession", null, context);
	this._uname = uname;
	soapDoc.set("name", uname);
	soapDoc.set("password", pword);
	var resp = ZmCsfeCommand.invoke(soapDoc, true, null, null, true).firstChild;
	this._processResponse(resp);	
}

ZaAuthenticate.prototype._processResponse =
function(resp) {
	var els = resp.childNodes;
	var len = els.length;
	var el, authToken, sessionId;
	AjxCookie.setCookie(document, ZaSettings.ADMIN_NAME_COOKIE, this._uname, null, "/");			    	
	for (var i = 0; i < len; i++) {
		el = els[i];
		if (el.nodeName == "authToken")
			authToken = el.firstChild.nodeValue;
/*		else if (el.nodeName == "lifetime")
			lifetime = el.firstChild.nodeValue;*/
		else if (el.nodeName=="sessionId")
			sessionId = el.firstChild.nodeValue;
		else if (el.nodeName=="a") { //TODO: Move this code to an external file
			if(ZaAccount.A_zimbraIsDomainAdminAccount == el.getAttribute("n")) {
				var value = el.firstChild.nodeValue;
				if(value=="true") {
					AjxCookie.setCookie(document, ZaSettings.ADMIN_TYPE_COOKIE, "domain", null, "/");			    	
				} else {
					AjxCookie.setCookie(document, ZaSettings.ADMIN_TYPE_COOKIE, "super", null, "/");			    	
				}					
			}
		}
	}
	ZmCsfeCommand.setAuthToken(authToken, -1, sessionId);
	ZaSettings.init();					
}
