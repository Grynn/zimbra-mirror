/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */

//Author: Raja Rao DV (rrao@zimbra.com)


function com_zimbra_tweetziFacebook(zimlet) {
	this.zimlet = zimlet;
	this.waitingForApproval = false;
	this.apiKey = this.zimlet.getUserProperty("tweetzi_facebook_api_key");
	this.apiSecret = this.zimlet.getUserProperty("tweetzi_facebook_secret");
}

 com_zimbra_tweetziFacebook.prototype._addFBComment = function(params) {
	var url = "http://api.facebook.com/restserver.php";
	var account = this.zimlet.tableIdAndAccountMap[params.tableId];
	var paramsArray = [
		["method", "Stream.addComment"],
		["session_key", account.session_key],
		["comment", document.getElementById(params.commentFieldId).value],
		["uid", account.uid],
		["post_id", params.postId]
	];
	var urlParams = this._getFBParams(paramsArray, account.secret);
	params["account"] = account;
	this._doPOST(url, urlParams, new AjxCallback(this, this._addFBCommentCallback, params));
};

 com_zimbra_tweetziFacebook.prototype._addFBCommentCallback =
function (params, response) {
	if(response.success) {
		var field = document.getElementById(params.commentFieldId);
		field.value = "write a comment...";
		field.style.color = "gray";
		appCtxt.getAppController().setStatusMsg("Comment added", ZmStatusView.LEVEL_INFO);
		document.getElementById(params.commentBoxId).style.display = "none";
		//resetar timer
		var timer = setInterval(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 400000);
		this.zimlet.tableIdAndTimerMap[params.tableId] = timer;
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage("Could Not Add Comment to facebook <br/>"+response.text, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_tweetziFacebook.prototype._publishToFacebook =
function (params) {
	var url = "http://api.facebook.com/restserver.php";
	var account = params.account;
	var paramsArray = [
		["method", "Stream.publish"],
		["session_key", account.session_key],
		["message",params.message],
		["uid", account.uid]					  
	];
	if(params.targetUser != undefined) {
		paramsArray.push(["target_id", params.targetUser]);
	}
	var urlParams = this._getFBParams(paramsArray, account.secret);
	this._doPOST(url, urlParams, new AjxCallback(this, this._publishToFacebookCallback, params));
};

 com_zimbra_tweetziFacebook.prototype._publishToFacebookCallback =
function (params, response) {
	if(response.success) {
		document.getElementById("tweetzi_statusTextArea").value = "";
		this.zimlet.showNumberOfLetters();
		appCtxt.getAppController().setStatusMsg("Updates Sent", ZmStatusView.LEVEL_INFO);
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, this.zimlet._getTableIdFromAccount(params.account), params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		var msg =  jsonObj.error;
		msgDialog.setMessage("Could Not Post to facebook.", DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

 com_zimbra_tweetziFacebook.prototype._getExtendedPermissionInfo =
function (params) {
	var url = "http://api.facebook.com/restserver.php";
	var account = params.account;
	var paramsArray = [
		["method", "Users.hasAppPermission"],
		["session_key", account.session_key],
		["ext_perm", params.permission],
		["uid", account.uid],
		["call_id", (new Date()).getTime()]			  
	];
	var urlParams = this._getFBParams(paramsArray, account.secret);
	this._doPOST(url, urlParams, new AjxCallback(this, this._getExtendedPermissionCallback, params));
};

com_zimbra_tweetziFacebook.prototype._getExtendedPermissionCallback =
function (params, response) {
	var permission = params.permission;
	var account = params.account;
	if(response.text == "0")
		account[permission] = "NO";
	else if(response.text == "1")
		account[permission] = "YES";
	
	if(params.callback)
		params.callback.run(this);
};

com_zimbra_tweetziFacebook.prototype.authorizeExtendedPermission =
function(params) {
	var permission = params.permission;
	var account = params.account;
	var url = "http://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm="+permission+"&api_key="+this.apiKey;
	window.open(ZmZimletBase.PROXY +AjxStringUtil.urlComponentEncode(url + params), "Authorize facebook", "toolbar=no,menubar=no,width=0.1px,height=0.1px");
};

com_zimbra_tweetziFacebook.prototype.getExtendedPermForRead =
function () {
	var url = "http://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm=read_stream&api_key="+this.apiKey;
	window.Open = window.open(url + params, "tfbOpen", "toolbar=no,menubar=no,width=0.1px,height=0.1px");
};
com_zimbra_tweetziFacebook.prototype._fbGetStream =
function (tableId, account) {
	var url = "http://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.get"],
		["session_key", account.session_key]
	];
	var params = this._getFBParams(paramsArray, account.secret);
	if (!tableId)
		var tableId = this.zimlet._showCard({headerName:"facebook", type:"FACEBOOK", autScroll:true});

	this._doPOST(url, params, new AjxCallback(this, this._getStreamCallback, tableId));
};

com_zimbra_tweetziFacebook.prototype._getStreamCallback =
function (tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	if(jsonObj.errorCode) {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(jsonObj.error_msg, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
		return;
	}
	this._fb_profiles = jsonObj.profiles;
	this.zimlet.createCardView(tableId, jsonObj.posts, "FACEBOOK");
};

com_zimbra_tweetziFacebook.prototype._getFacebookHTML =
function () {

};

com_zimbra_tweetziFacebook.prototype._fbCreateToken =
function () {
	var url = "http://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Auth.createToken"]
	];
	var params = this._getFBParams(paramsArray, this.apiSecret);
	this._doPOST(url, params, new AjxCallback(this, this._fbCreateTokenCallback));
};

com_zimbra_tweetziFacebook.prototype._fbCreateTokenCallback =
function (response) {
	var text = response.text;
	this.fb_auth_token = eval("(" + text + ")");
	var url = "http://www.facebook.com/login.php?";
	var params = "version=1.0&auth_token=" + this.fb_auth_token + "&api_key="+this.apiKey;
	window.open(url + params, "", "toolbar=no,menubar=no,width=0.1px,height=0.1px");
	this.waitingForApproval = true;
	//this.getSessionTimer = setInterval(AjxCallback.simpleClosure(this._getSessionId, this), 60000);
}
com_zimbra_tweetziFacebook.prototype._getSessionId =
function () {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "facebook.auth.getSession"],
		["auth_token", this.fb_auth_token]
	];
	var params = this._getFBParams(paramsArray, this.apiSecret);
	this._doPOST(url, params, new AjxCallback(this, this._sessionIdCallback));
};

com_zimbra_tweetziFacebook.prototype._sessionIdCallback =
function (response) {
		var text = response.text;
		if(text.indexOf("session_key") >=0 && text.indexOf("secret") >=0){
			clearInterval(this.getSessionTimer);
			var fbStr = this._convertFB_JsonStrToUrlEncodedStr(text);
			this.manageFacebookAccounts(fbStr);
			this.zimlet.preferences._setAccountPrefDlgAuthMessage("STEP2. Facebook Authorization Recieved. Please aurhorize other rights", "blue");
			this.zimlet.preferences._updateAccountsTable({message:"STEP2. Facebook Authorization Recieved. Please aurhorize other rights", color:"blue"});
			this.zimlet.preferences._updateAllFBPermissions({message:"STEP2. Facebook Authorization Recieved. Please aurhorize other rights", color:"blue"});
			this.waitingForApproval = false;
		}
};

com_zimbra_tweetziFacebook.prototype._convertFB_JsonStrToUrlEncodedStr = function(text) {
	var jsonObj = eval("(" + text + ")");
	var fb_raw = "";
	for (var el in jsonObj) {
		if (fb_raw == "")
			fb_raw = el + "=" + jsonObj[el];
		else
			fb_raw = fb_raw + "&" + el + "=" + jsonObj[el];
	}
	return fb_raw;
};

com_zimbra_tweetziFacebook.prototype._doPOST =
function (url, params, callback) {
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = params.length;
	hdrs["Connection"] = "close";
	var entireurl = ZmZimletBase.PROXY + url;
	AjxRpc.invoke(params, entireurl, hdrs, callback, false);
}

com_zimbra_tweetziFacebook.prototype._getFBParams =
function(otherParamsArray, secret) {
	var paramsArray = [
		["api_key",this.apiKey],
		["v","1.0"],
		["format", "json"]
	].concat(otherParamsArray);
	paramsArray = paramsArray.sort();
	var sig = "";
	if (paramsArray.length > 0) {
		for (var i = 0; i < paramsArray.length; i++) {
			sig = sig + paramsArray[i][0] + "=" + paramsArray[i][1];
		}
		sig = hex_md5(sig + secret);
	}

	var arry = new Array();
	for (var i = 0; i < paramsArray.length; i++) {
		arry.push(AjxStringUtil.urlComponentEncode(paramsArray[i][0]) + "=" + AjxStringUtil.urlComponentEncode(paramsArray[i][1]));
	}
	arry.push(AjxStringUtil.urlComponentEncode("sig") + "=" + AjxStringUtil.urlComponentEncode(sig));
	return arry.join("&");
};


com_zimbra_tweetziFacebook.prototype.manageFacebookAccounts = function(text) {
	var nv = text.split("&");
	var tObj = {};
	for (var i = 0; i < nv.length; i++) {
		var tmp = nv[i].split("=");
		tObj[tmp[0]] = tmp[1];
	}
	if (tObj["__type"] == undefined) {
		tObj["__type"] = "facebook";
	}
	if (tObj["__on"] == undefined) {
		tObj["__on"] = "true";
	}
	//to normalize names with fb
	tObj.raw = text;
	tObj.name = "facebook";
	tObj.type = tObj["__type"];
	this.zimlet.allAccounts[tObj.name + tObj.uid] = tObj;
};


com_zimbra_tweetziFacebook.prototype._updateFacebookStream =
function(tableId, account) {
	this._fbGetStream(tableId, account);
};

com_zimbra_tweetziFacebook.prototype._getFacebookProfile =
function(id) {
	for (var i = 0; i < this._fb_profiles.length; i++) {
		if (id == this._fb_profiles[i].id) {
			return this._fb_profiles[i];
		}
	}
};