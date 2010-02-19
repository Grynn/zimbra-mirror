/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

com_zimbra_example_soap_HandlerObject = function() {
};

com_zimbra_example_soap_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_soap_HandlerObject.prototype.constructor = com_zimbra_example_soap_HandlerObject;

/**
 * This method is called by the Zimlet framework when a context menu item is selected.
 * 
 */
com_zimbra_example_soap_HandlerObject.prototype.menuItemSelected = 
function(itemId) {
	switch (itemId) {
		case "menuId_soap_request_xml":
			this._submitSOAPRequestXML();
			break;
		case "menuId_soap_request_json":
			this._submitSOAPRequestJSON();
			break;
	}
};

/**
 * Submits a SOAP request in XML format.
 * 
 * <GetAccountInfoRequest xmlns="urn:zimbraAccount">
 *     <account by="name">user1</account>
 * </GetAccountInfoRequest>
 *
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._submitSOAPRequestXML =
function() {
		
	var soapDoc = AjxSoapDoc.create("GetAccountInfoRequest", "urn:zimbraAccount");

	var accountNode = soapDoc.set("account", appCtxt.getUsername());
	accountNode.setAttribute("by", "name");
	
	var params = {
			soapDoc: soapDoc,
			asyncMode: true,
			callback: (new AjxCallback(this, this._handleSOAPResponseXML)),
			errorCallback: (new AjxCallback(this, this._handleSOAPErrorResponseXML))
			};

	appCtxt.getAppController().sendRequest(params);
};

/**
 * Handles the SOAP response.
 * 
 * @param	{ZmCsfeResult}		result		the result
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._handleSOAPResponseXML =
function(result) {

	if (result.isException()) {
		// do something with exception
		var exception = result.getException();		

		return;
	}
	
	// do something with response (in JSON format)
	var response = result.getResponse().GetAccountInfoResponse;

	var name = response.name;
	var soapURL = response.publicURL;
	var soapURL = response.soapURL;
	var zimbraId = result.getResponse().GetAccountInfoResponse._attrs.zimbraId;
	var zimbraMailHost = result.getResponse().GetAccountInfoResponse._attrs.zimbraMailHost;
	
	appCtxt.setStatusMsg("GetAccountInfoResponse (XML) success - "+name);	
};

/**
 * Handles the SOAP error response.
 * 
 * @param	{ZmCsfeException}		ex		the exception
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._handleSOAPErrorResponseXML =
function(ex) {

	var errorMsg = ex.getErrorMsg(); // the error message
	var dump = ex.dump(); // the complete error dump

};

/**
 * Submits a SOAP request in JSON format.
 * 
 * 
 * GetAccountInfoRequest: {
 *   _jsns: "urn:zimbraAccount",
 *   account: {
 *     _content: "user1",
 *     by: "name"
 *    }
 * }
 *
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._submitSOAPRequestJSON =
function() {

	var jsonObj = {GetAccountInfoRequest:{_jsns:"urn:zimbraAccount"}};
	var	request = jsonObj.GetAccountInfoRequest;
	request.account = {_content: appCtxt.getUsername(), by: "name"};
	
	var params = {
			jsonObj:jsonObj,
			asyncMode:true,
			callback: (new AjxCallback(this, this._handleSOAPResponseJSON)),
			errorCallback: (new AjxCallback(this, this._handleSOAPErrorResponseJSON))
		};
	
	return appCtxt.getAppController().sendRequest(params);

};

/**
 * Handles the SOAP response.
 * 
 * @param	{ZmCsfeResult}		result		the result
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._handleSOAPResponseJSON =
function(result) {

	if (result.isException()) {
		// do something with exception
		var exception = result.getException();		

		return;
	}
	
	// do something with response (in JSON format)
	var response = result.getResponse().GetAccountInfoResponse;

	var name = response.name;
	var soapURL = response.publicURL;
	var soapURL = response.soapURL;
	var zimbraId = result.getResponse().GetAccountInfoResponse._attrs.zimbraId;
	var zimbraMailHost = result.getResponse().GetAccountInfoResponse._attrs.zimbraMailHost;
	
	appCtxt.setStatusMsg("GetAccountInfoResponse (JSON) success - "+name);	
};

/**
 * Handles the SOAP error response.
 * 
 * @param	{ZmCsfeException}		ex		the exception
 * @private
 */
com_zimbra_example_soap_HandlerObject.prototype._handleSOAPErrorResponseJSON =
function(ex) {

	var errorMsg = ex.getErrorMsg(); // the error message
	var dump = ex.dump(); // the complete error dump

};
