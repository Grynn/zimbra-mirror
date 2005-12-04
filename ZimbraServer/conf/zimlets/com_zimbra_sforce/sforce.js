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
 * The Original Code is: Zimbra Collaboration Suite.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Zimlet to handle integration with a SalesForce account  //
//  @author Mihai Bazon, <mihai@zimbra.com>                 //
//////////////////////////////////////////////////////////////

function Com_Zimbra_SForce() {
	this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;
	this.XMLNS = "urn:partner.soap.sforce.com";
	this.done_login = new AjxCallback(this, this.done_login);
	this.done_logout = new AjxCallback(this, this.done_logout);
};

Com_Zimbra_SForce.LOGIN_SERVER = "https://www.salesforce.com/services/Soap/u/6.0";

Com_Zimbra_SForce.prototype = new ZmZimletBase;
Com_Zimbra_SForce.prototype.constructor = Com_Zimbra_SForce;

// SOAP utils

Com_Zimbra_SForce.prototype._makeEnvelope = function(method) {
	return AjxSoapDoc.create(method, this.XMLNS, null,
				 "http://schemas.xmlsoap.org/soap/envelope/");
};

Com_Zimbra_SForce.prototype.rpc = function(soap, callback) {
	var envEl = soap.getDoc().firstChild;
	// Seems we need to set these or otherwise will get a "VersionMismatch"
	// message from SForce
	envEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	envEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
	this.sendRequest(soap, this.SERVER, { SOAPAction: "m" }, callback);
};

// METHOD: login

Com_Zimbra_SForce.prototype.login = function() {
	var soap = this._makeEnvelope("login");
	soap.set("username", this.getUserProperty("user"));
	soap.set("password", this.getUserProperty("passwd"));
	this.rpc(soap, this.done_login);
};

Com_Zimbra_SForce.prototype.done_login = function(result) {
	//alert(result.text);
// 	try {
// 		var ans = AjxXmlDoc.createFromXml(result.text).toJSObject(true, false);
// 	} catch(ex) {
// 		alert(ex.dump());
// 	}
	var txt = result.text;
	if (AjxEnv.isIE) {
		// for some reason IE fails at this tag and nothing works
		// thereafter, so the only option I can figure out is to remove
		// it.
		txt = txt.replace(/<userDefaultCurrencyIsoCode.*?\x2f>/, "");
	}
	var ans = AjxXmlDoc.createFromXml(txt).toJSObject(true, false);
	if (ans.Body.loginResponse) {
		ans = ans.Body.loginResponse.result;
		this.SERVER = String(ans.serverUrl);
		this.sessionId = String(ans.sessionId);
		this.userId = String(ans.userId);
		this.userInfo = ans.userInfo;
		//alert(this.userInfo.toSource());
		this.displayStatusMessage("SForce: " + this.userInfo.userFullName + " logged in.");
	} else {
		ans = ans.Body.Fault;
		if (ans) {
			this.displayErrorMessage("<b>Login to SalesForce failed:</b><br />&nbsp;&nbsp;&nbsp;&nbsp;"
						 + ans.faultstring + "</b><br />"
						 + "Please review your preferences.");
		}
	}
};

// METHOD: logout

Com_Zimbra_SForce.prototype.logout = function() {
	this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;
	this.sessionId = null;
	this.userId = null;
	this.userInfo = null;
};

// UI handlers

Com_Zimbra_SForce.prototype.doDrop = function(obj) {
	switch (obj.toString()) {
	    case "ZmMailMsg":
		this.displayErrorMessage("You dropped an email message!");
		break;

	    case "ZmConv":
		this.displayErrorMessage("You dropped a conversation!");
		break;

	    case "ZmContact":
		this.displayErrorMessage("You dropped a contact!");
		break;
	}
};

Com_Zimbra_SForce.prototype.panelItemClicked = function() {
	this.login();
};

Com_Zimbra_SForce.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;
	}
};
