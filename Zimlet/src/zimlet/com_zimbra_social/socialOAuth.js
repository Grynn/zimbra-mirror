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
function SocialOAuth(zimlet, oauthResultCallback) {
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

SocialOAuth.prototype.setGoToButtonDetails = function(btnText, btnIcon) {
	this._goTobtnText = btnText;
	this._goTobtnIcon = btnIcon;
};

SocialOAuth.prototype.setOAuthUrls = function(params) {
	this.requestTokenUrl = params.requestTokenUrl;
	this.authorizeBaseUrl = params.authorizeBaseUrl;
	this.accessTokenUrl = params.accessTokenUrl;
};

SocialOAuth.prototype.setAuthTokens = function(oauthTokens) {
	this.oauth_token = oauthTokens["oauth_token"];
	this.oauth_token_secret = oauthTokens["oauth_token_secret"];
};

SocialOAuth.prototype.setAppName = function(appName) {
	this.appName = appName;
};


SocialOAuth.prototype.showOAuthDialog = function(serviceName) {
	this.serviceName = serviceName;
	this._oauthAccessTokenResponse = [];
	this._oauthRequestResponse = [];
	if (this._getPinDialog) {
		document.getElementById("Oauth_pin_field").value = "";
		this._getPinDialog.popup();
		return;
	}
	this._getPinView = new DwtComposite(this._shell);
	this._getPinView.getHtmlElement().style.overflow = "auto";
	this._getPinView.getHtmlElement().innerHTML = this._createPINView();
	this._getPinDialog = this.zimlet._createDialog({title:this.zimlet.getMessage("addAccountViaOAuth"), view:this._getPinView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._getPinDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okgetPinBtnListener));
	this.goButton = new DwtButton({parent:this._shell});
	this.goButton.setText(this._goTobtnText);
	this.goButton.setImage(this._goTobtnIcon);
	this.goButton.addSelectionListener(new AjxListener(this, this._openOauthAuthorizeURL));
	document.getElementById("Oauth_goToOauthPage").appendChild(this.goButton.getHtmlElement());
	this._getPinDialog.popup();
};

SocialOAuth.prototype._createPINView =
function() {
	try{
		var stepNotes = this.zimlet.getMessage("stepNotes");
		var step1 = this.zimlet.getMessage("step1").replace("{0}", this.serviceName).replace("{1}", this.serviceName);
	}catch(e) {
	}
	var subs = {
		logoutfirst:  AjxMessageFormat.format(this.zimlet.getMessage("logoutfirst"), this.serviceName),
		stepsToAddAccount: AjxMessageFormat.format(this.zimlet.getMessage("stepsToAddAccount"), this.serviceName),
		step1: step1,
		step2: AjxMessageFormat.format(this.zimlet.getMessage("step2"), this.serviceName),
		step3: this.zimlet.getMessage("step3"),
		step4: AjxMessageFormat.format(this.zimlet.getMessage("step4"), this.serviceName),
		step5: this.zimlet.getMessage("step5"),
		step6: AjxMessageFormat.format(this.zimlet.getMessage("step6"), this.serviceName),
		step7: this.zimlet.getMessage("step7"),
		stepNotes: stepNotes
	};
	return AjxTemplate.expand("com_zimbra_social.templates.OAuth#OAuthDialogView", subs);
};

SocialOAuth.prototype._okgetPinBtnListener =
function() {

	var pin = document.getElementById("Oauth_pin_field").value;
	pin = AjxStringUtil.trim(pin);
	if (pin == "" || pin.length > 7) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("pleaseEnter5to7DigitToken"), ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}
	this._pinHandler(pin);
	this._getPinDialog.popdown();
};

//step 1
SocialOAuth.prototype._openOauthAuthorizeURL =
function() {
	var authorizationHeader = this._getRequestTokenHeader();
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Authorization"] = authorizationHeader;
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(this.requestTokenUrl);
	var callback =  new AjxCallback(this, this._oauthRequestTokenHandler);	
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, "", entireurl, hdrs, callback, false), 300);
};

//step 2
SocialOAuth.prototype._oauthRequestTokenHandler =
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
	this.zimlet.openCenteredWindow(this.authorizeBaseUrl +  AjxStringUtil.urlComponentEncode(this._oauthRequestResponse["oauth_token"]));
};

//step 3
SocialOAuth.prototype._pinHandler =
function(pin) {
	var authorizationHeader = this._getAccessTokenHeader(pin);
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Authorization"] = authorizationHeader;
	var entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(this.accessTokenUrl);
	var callback =  new AjxCallback(this, this._oauthAccessTokenHandler);
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, "", entireurl, hdrs, callback, false), 300);
};

//step 4
SocialOAuth.prototype._oauthAccessTokenHandler =
function(response) {
	this._response = response;
	if(!response.success) {
		this._success = false;
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

SocialOAuth.prototype._returnOAuthResult =
function() {
	var result = {success: this._success, oauthTokens: this._oauthAccessTokenResponse, httpResponse: this._response};
	if(this.oauthResultCallback) {
		this.oauthResultCallback.run(result);
	}
};

SocialOAuth.prototype.makeHTTPGet =
function(params) {
	params["method"] = "GET";
	var components = params.components;
	var url = params.url;
	var hdrs = new Array();
	if(!params["noOAuth"]) {//used for makeSimpleHTTPGet
		hdrs["Authorization"] = this._getGeneralAPIHeader(params);
	}
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

SocialOAuth.prototype.makeSimpleHTTPGet =
function(params) {
	params["noOAuth"] = true;
	this.makeHTTPGet(params);
};

SocialOAuth.prototype.makeHTTPPost =
function(params) {
	params["method"] = "POST";
	var components = params.components;
	var authorizationHeader = this._getGeneralAPIHeader(params);
	var url = params.url;
	var postBody = params.postBody;
	var contentType = params.contentType;
	var urlComponents = [];
	var entireurl = "";
	for(var componentName in components) {
		urlComponents.push(componentName + "=" + AjxStringUtil.urlComponentEncode(components[componentName]));
	}
	urlComponents = urlComponents.join("&");

	var hdrs = new Array();
	hdrs["Authorization"] = authorizationHeader;
	if(contentType) {
		hdrs["Content-Type"] = params.contentType;
		//urlencoded, then use urlcomponents as postbody
		if(contentType == "application/x-www-form-urlencoded") {
			postBody = urlComponents;
			entireurl = ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(url);
		} else {
			entireurl =  ZmZimletBase.PROXY +  AjxStringUtil.urlComponentEncode(url + "?" + urlComponents);
		}
	}
	if(postBody) {
		hdrs["Content-Length"] = params.postBody.length;
	} else {
		postBody = "";
	}
	hdrs["Authorization"] = authorizationHeader;

	
	if(!params.callback) {
		params.callback = "";
	}
	var callback =  new AjxCallback(this, this._httpGetHandler, params.callback);
	setTimeout(AjxCallback.simpleClosure(this._rpcInvoke, this, postBody, entireurl, hdrs, callback, false), 300);
};

SocialOAuth.prototype._httpGetHandler =
function(postCallback, response) {
	if(postCallback && postCallback != "") {
		postCallback.run(response);
	}
};

//used for requestToken
SocialOAuth.prototype._getRequestTokenHeader =
function() {
	var pArray = new Array();
	pArray.push("method=POST");
	pArray.push("url=" + AjxStringUtil.urlComponentEncode(this.requestTokenUrl));
	pArray.push("nonce=" + this._getNonce());
	pArray.push("oauth_callback=oob");
	pArray.push("isZD="+this.isZD);

	var jspUrl = this.zimlet.getResource("oauth.jsp") + "?" + pArray.join("&");
	var response = AjxRpc.invoke(null, jspUrl, null, null, true);
	var authorizationHeader = response.text;
	return authorizationHeader;
};

//used for accessToken
SocialOAuth.prototype._getAccessTokenHeader =
function(pin) {
	var pArray = new Array();
	pArray.push("method=POST");
	pArray.push("url=" + AjxStringUtil.urlComponentEncode(this.accessTokenUrl));
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
SocialOAuth.prototype._getGeneralAPIHeader =
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

SocialOAuth.prototype._rpcInvoke =
function(postData, url, hdrs, callback, useGet) {
	var result = AjxRpc.invoke(postData, url, hdrs, callback, useGet);
	return result;
};

SocialOAuth.prototype._getNonce =
function() {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var result = "";
	for (var i = 0; i < 6; ++i) {
		var rnum = Math.floor(Math.random() * chars.length);
		result += chars.substring(rnum, rnum + 1);
	}
	return result;
};