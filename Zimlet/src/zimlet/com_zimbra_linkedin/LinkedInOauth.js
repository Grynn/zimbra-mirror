/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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
function LinkedInZimletOAuth(zimlet, oauthResultCallback) {
	this.zimlet = zimlet;
	this._shell =  zimlet.getShell();
	this.isZD = false;
	this._oauthAccessTokenResponse = [];
	this._oauthRequestResponse = [];
	this._success = false;
	this.oauthResultCallback = oauthResultCallback;//set this to return oauthResult info back to Zimlet

	try{
		var version = appCtxt.getActiveAccount().settings.getInfoResponse.version;
		if(version.toLowerCase().indexOf("desktop") > 0) {
			this.isZD = true;
		}
	}catch(e) {
		//ignore
	}
};
//static variables
LinkedInZimletOAuth.REQUEST_TOKEN_URL = "https://api.linkedin.com/uas/oauth/requestToken";
LinkedInZimletOAuth.AUTHORIZE_BASE_URL = "https://www.linkedin.com/uas/oauth/authorize?oauth_token=";
LinkedInZimletOAuth.ACCESS_TOKEN_URL ="https://api.linkedin.com/uas/oauth/accessToken";

LinkedInZimletOAuth.prototype.setAuthTokens = function(oauthTokens) {
	this.oauth_token = oauthTokens["oauth_token"];
	this.oauth_token_secret = oauthTokens["oauth_token_secret"];
};

LinkedInZimletOAuth.prototype.showOAuthDialog = function() {
	this._oauthAccessTokenResponse = [];
	this._oauthRequestResponse = [];
	if (this._getPinDialog) {
		document.getElementById("com_zimbra_linkedin_pin_field").value = "";
		this._getPinDialog.popup();
		return;
	}
	this._getPinView = new DwtComposite(this._shell);
	this._getPinView.getHtmlElement().style.overflow = "auto";
	this._getPinView.getHtmlElement().innerHTML = this._createPINView();
	this._getPinDialog = this.zimlet._createDialog({title:this.zimlet.getMessage("enterToken"), view:this._getPinView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._getPinDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okgetPinBtnListener));
	this.goButton = new DwtButton({parent:this._shell});
	this.goButton.setText(this.zimlet.getMessage("goTolinkedIn"));
	this.goButton.setImage("LinkedinZimletIcon");
	this.goButton.addSelectionListener(new AjxListener(this, this._openOauthAuthorizeURL));
	document.getElementById("linkedin_goToLinkedInPage").appendChild(this.goButton.getHtmlElement());
	this._getPinDialog.popup();
};

LinkedInZimletOAuth.prototype._createPINView =
function() {
	var subs = {
		stepsToAddLinkedInAccount: this.zimlet.getMessage("stepsToAddLinkedInAccount"),
		step1: this.zimlet.getMessage("step1"),
		step2: this.zimlet.getMessage("step2"),
		step3: this.zimlet.getMessage("step3"),
		step4: this.zimlet.getMessage("step4"),
		step5: this.zimlet.getMessage("step5"),
		step6: this.zimlet.getMessage("step6"),
		step7: this.zimlet.getMessage("step7"),
		stepNotes: this.zimlet.getMessage("stepNotes")
	};
	return AjxTemplate.expand("com_zimbra_linkedin.templates.LinkedIn#OAuthDialogView", subs);
};

LinkedInZimletOAuth.prototype._okgetPinBtnListener =
function() {

	var pin = document.getElementById("com_zimbra_linkedin_pin_field").value;
	pin = AjxStringUtil.trim(pin);
	if (pin == "" || pin.length > 7) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("pleaseEnter5to7DigitToken"), ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}
	this._linkedInPINHandler(pin);
	this._getPinDialog.popdown();
};

//step 1
LinkedInZimletOAuth.prototype._openOauthAuthorizeURL =
function() {
	var authorizationHeader = this._getRequestTokenHeader();
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Authorization"] = authorizationHeader;
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(LinkedInZimletOAuth.REQUEST_TOKEN_URL);
	var callback =  new AjxCallback(this, this._oauthRequestTokenHandler);	
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, "", entireurl, hdrs, callback, false), 300);
};

//step 2
LinkedInZimletOAuth.prototype._oauthRequestTokenHandler =
function(response) {
	if(!response.success) {
		this._success = false;
		this._response = response;
		this._returnOAuthResult();
		return;
	}
	var arry = response.text.split("&");
	for(var i=0; i < arry.length; i++) {
		var nvArray = arry[i].split("=");
		if(nvArray.length == 2) {
			this._oauthRequestResponse[nvArray[0]] = nvArray[1];
		}
	}
	this._openCenteredWindow(LinkedInZimletOAuth.AUTHORIZE_BASE_URL + this._oauthRequestResponse["oauth_token"]);
};

//step 3
LinkedInZimletOAuth.prototype._linkedInPINHandler =
function(pin) {
	var authorizationHeader = this._getAccessTokenHeader(pin);
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Authorization"] = authorizationHeader;
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(LinkedInZimletOAuth.ACCESS_TOKEN_URL);
	var callback =  new AjxCallback(this, this._oauthAccessTokenHandler);
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, "", entireurl, hdrs, callback, false), 300);
};

//step 4
LinkedInZimletOAuth.prototype._oauthAccessTokenHandler =
function(response) {
	if(!response.success) {
		this._success = false;
		this._response = response;
		this._returnOAuthResult();
		return;
	}
	var arry = response.text.split("&");
	for(var i=0; i < arry.length; i++) {
		var nvArray = arry[i].split("=");
		if(nvArray.length == 2) {
			this._oauthAccessTokenResponse[nvArray[0]] = nvArray[1];
		}
	}
	this._success = true;
	this._returnOAuthResult();
	 
};

LinkedInZimletOAuth.prototype._returnOAuthResult =
function() {
	var result = {success: this._success, oauthTokens: this._oauthAccessTokenResponse, httpResponse: this._response};
	if(this.oauthResultCallback) {
		this.oauthResultCallback.run(result);
	}
};

LinkedInZimletOAuth.prototype.makeHTTPGet =
function(params) {
	params["method"] = "GET";
	var components = params.components;
	var authorizationHeader = this._getGeneralAPIHeader(params);
	var url = params.url;
	var hdrs = new Array();
	hdrs["Authorization"] = authorizationHeader;
	var urlComponents = [];
	for(var componentName in components) {
		urlComponents.push(componentName + "=" + AjxStringUtil.urlComponentEncode(components[componentName]));
	}
	urlComponents = urlComponents.join("&");
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(url + "?" + urlComponents);
	if(!params.callback) {
		params.callback = "";
	}
	var callback =  new AjxCallback(this, this._httpGetHandler, params.callback);
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, "", entireurl, hdrs, callback, true), 300);
};

LinkedInZimletOAuth.prototype.makeHTTPPost =
function(params) {
	params["method"] = "POST";
	var components = params.components;
	var authorizationHeader = this._getGeneralAPIHeader(params);
	var url = params.url;
	var postBody = params.postBody;
	var hdrs = new Array();
	hdrs["Authorization"] = authorizationHeader;
	if( params.contentType) {
		hdrs["Content-Type"] = params.contentType;
	}
	if(postBody) {
		hdrs["Content-Length"] = params.postBody.length;
	} else {
		postBody = "";
	}
	hdrs["Authorization"] = authorizationHeader;
	var urlComponents = [];
	for(var componentName in components) {
		urlComponents.push(componentName + "=" + AjxStringUtil.urlComponentEncode(components[componentName]));
	}
	urlComponents = urlComponents.join("&");
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(url + "?" + urlComponents);
	if(!params.callback) {
		params.callback = "";
	}
	var callback =  new AjxCallback(this, this._httpGetHandler, params.callback);
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, postBody, entireurl, hdrs, callback, false), 300);
};

LinkedInZimletOAuth.prototype._httpGetHandler =
function(postCallback, response) {
	if(postCallback && postCallback != "") {
		postCallback.run(response);
	}
};

//used for requestToken
LinkedInZimletOAuth.prototype._getRequestTokenHeader =
function() {
	var pArray = new Array();
	pArray.push("method=POST");
	pArray.push("url=" + AjxStringUtil.urlComponentEncode(LinkedInZimletOAuth.REQUEST_TOKEN_URL));
	pArray.push("nonce=" + this._getNonce());
	pArray.push("oauth_callback=oob");
	pArray.push("isZD="+this.isZD);

	var jspUrl = this.zimlet.getResource("oauth.jsp") + "?" + pArray.join("&");
	var response = AjxRpc.invoke(null, jspUrl, null, null, true);
	var authorizationHeader = response.text;
	return authorizationHeader;
};

//used for accessToken
LinkedInZimletOAuth.prototype._getAccessTokenHeader =
function(pin) {
	var pArray = new Array();
	pArray.push("method=POST");
	pArray.push("url=" + AjxStringUtil.urlComponentEncode(LinkedInZimletOAuth.ACCESS_TOKEN_URL));
	pArray.push("token=" + AjxStringUtil.urlComponentEncode(this._oauthRequestResponse["oauth_token"]));
	pArray.push("tokenSecret=" + AjxStringUtil.urlComponentEncode(this._oauthRequestResponse["oauth_token_secret"]));
	pArray.push("oauth_verifier=" + AjxStringUtil.urlComponentEncode(pin));
	pArray.push("nonce=" + this._getNonce());
	pArray.push("isZD="+this.isZD);

	var jspUrl = this.zimlet.getResource("oauth.jsp") + "?" + pArray.join("&");
	var response = AjxRpc.invoke(null, jspUrl, null, null, true);
	return response.text;
};

//used for accessToken
LinkedInZimletOAuth.prototype._getGeneralAPIHeader =
function(params) {
	var method = params.method;
	var url = params.url;
	var components = params.components ? params.components : {};
	var pArray = new Array();
	pArray.push("method="+method);
	pArray.push("url=" + AjxStringUtil.urlComponentEncode(url));
	pArray.push("token=" + AjxStringUtil.urlComponentEncode(this.oauth_token));
	pArray.push("tokenSecret=" + AjxStringUtil.urlComponentEncode(this.oauth_token_secret));
	pArray.push("nonce=" + this._getNonce());
	for(var componentName in components) {
		pArray.push(componentName+"="+AjxStringUtil.urlComponentEncode(components[componentName]));
	}
	pArray.push("isZD="+this.isZD);

	var jspUrl = this.zimlet.getResource("oauth.jsp") + "?" + pArray.join("&");
	var response = AjxRpc.invoke(null, jspUrl, null, null, true);
	return response.text;
};

LinkedInZimletOAuth.prototype._rpcInvoke =
function(postData, url, hdrs, callback, useGet) {
	var result = AjxRpc.invoke(postData, url, hdrs, callback, useGet);
	return result;
};

LinkedInZimletOAuth.prototype._getNonce =
function() {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var result = "";
	for (var i = 0; i < 6; ++i) {
		var rnum = Math.floor(Math.random() * chars.length);
		result += chars.substring(rnum, rnum + 1);
	}
	return result;
};

LinkedInZimletOAuth.prototype._openCenteredWindow =
function (url) {
	var width = 800;
	var height = 600;
	var left = parseInt((screen.availWidth / 2) - (width / 2));
	var top = parseInt((screen.availHeight / 2) - (height / 2));
	var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
	var win = window.open(url, "subWind", windowFeatures);
	if (!win) {
		this.zimlet.showWarningMsg(ZmMsg.popupBlocker);
	}
};
