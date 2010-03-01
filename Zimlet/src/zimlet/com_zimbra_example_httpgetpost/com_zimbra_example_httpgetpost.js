/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

com_zimbra_example_httpgetpost_HandlerObject = function() {
};
com_zimbra_example_httpgetpost_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_httpgetpost_HandlerObject.prototype.constructor = com_zimbra_example_httpgetpost_HandlerObject;

/**
 * This method gets called by the Zimlet framework when a context menu item is selected.
 * 
 * @param	{String}	itemId		the Id of selected menu item
 */
com_zimbra_example_httpgetpost_HandlerObject.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
	case "CALL_VIA_HTTP_GET":
		this._executeHttpGet();
		break;
	case "CALL_VIA_HTTP_POST":
		this._executeHttpPost();
		break;
	case "CALL_VIA_HTTP_EXTERNAL_GET":
		this._executeExternalHttpGet();
		break;
	default:
		// do nothing
		break;
	}

};

/**
 * Performs a "GET" against the zimlet jsp page.
 * 
 */
com_zimbra_example_httpgetpost_HandlerObject.prototype._executeHttpGet = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");

	var response = AjxRpc.invoke(null, jspUrl, null, null, true);

	if (response.success == true) {
		appCtxt.getAppController().setStatusMsg(response.text);		
	}
	
};

/**
 * Performs a "POST" against the zimlet jsp page.
 * 
 */
com_zimbra_example_httpgetpost_HandlerObject.prototype._executeHttpPost = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");

	var response = AjxRpc.invoke(null, jspUrl, null, null, false);

	if (response.success == true) {
		appCtxt.getAppController().setStatusMsg(response.text);		
	}
	
};

/**
 * Performs a "GET" against an external server using the Proxy Servlet.
 * 
 */
com_zimbra_example_httpgetpost_HandlerObject.prototype._executeExternalHttpGet = 
function() {
	
	var extServer = "http://search.twitter.com/search.json";

	// create and encode the query params of the external server url
	var extServerParams = ["q", "=", AjxStringUtil.urlComponentEncode("london bridge")].join("");
	
	 // add parameters to the url
	var extServerUrl = [extServer, "?", params].join("");

	// url encode the external server url
	// since it will part of the query params for the proxy servet
	var encodedExtServerUrl = AjxStringUtil.urlComponentEncode(extServerUrl);
	
	// create proxy servlet URL
	var proxyServletUrl = [ZmZimletBase.PROXY, encodedExtServerUrl].join(""); 

	// submit the URL and asynchronous response (using callback)
	AjxRpc.invoke(null, proxyServletUrl, null, new AjxCallback(this, this._httpExternalGetCallback), false);
	
};

/**
 * Handles the callback from the external http GET AjxRpc.invoke().
 * 
 * 
 * @see		com_zimbra_example_httpgetpost_HandlerObject._executeExternalHttpGet
 */
com_zimbra_example_httpgetpost_HandlerObject.prototype._httpExternalGetCallback =
function(response) {

	if (response.success == false) {
		// display the error response
		appCtxt.getAppController().setStatusMsg("Error: " + response.text, ZmStatusView.LEVEL_WARNING);
		return;
	}

	// display the response
	appCtxt.getAppController().setStatusMsg(response.text);		

};
