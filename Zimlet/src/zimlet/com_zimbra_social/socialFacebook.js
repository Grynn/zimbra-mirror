/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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

/*global FB */
/*global window */
/*global document */
/*global alert */
/*global testAPI */


//Author: Raja Rao DV (rrao@zimbra.com)

function com_zimbra_socialFacebook(zimlet) {
	this.zimlet = zimlet;
	this.waitingForApproval = false;
	this.itemsLimit = 50;
	this._extendedPerms = "read_stream,publish_stream,friends_activities,user_activities,friends_likes,user_likes";
	this._tableIdAndFBProfilesCache = [];
	this.appId = this.zimlet.getConfig("social_facebook_app_id");
	this.appSecret = this.zimlet.getConfig("social_facebook_app_secret");
	this._initFacebook();
}

com_zimbra_socialFacebook.prototype._initFacebook = function() {
	var div = document.createElement("DIV");
	div.id = "fb-root";

	var that = this; //for closure

	window.fbAsyncInit = function () {
		FB.init({
			appId: that.appId, // App ID
			//channelUrl: '//WWW.YOUR_DOMAIN.COM/channel.html', //Channel File - we don't use social plugins so not helpful, I believe. and it would complicate the setup on every installation
			status: true, // check login status
			cookie: true, // enable cookies to allow the server to access the session
			xfbml: true  // parse XFBML
		});

		// Additional init code here

	};

// Load the SDK asynchronously
	(function (d) {
		var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
		if (d.getElementById(id)) {
			return;
		}
		js = d.createElement('script');
		js.id = id;
		js.async = true;
		js.src = "//connect.facebook.net/en_US/all.js";
		ref.parentNode.insertBefore(js, ref);
	}(document));

};

com_zimbra_socialFacebook.prototype._loadFacebookAccount = function() {
	var that = this;
	FB.api('/me', function (response) {
		that._loadFacebookAccountHandleResponse(response);
	});
};

com_zimbra_socialFacebook.prototype._loadFacebookAccountHandleResponse = function(response) {
	if (response.error) {
		//This sometimes happens after a change in authentication. Try second time and it works. This is facebook.
		var dlg = appCtxt.getMsgDialog();
		dlg.setMessage(this.zimlet.getMessage("fbTemporaryError"), DwtMessageDialog.WARNING_STYLE);
		dlg.popup();
		return;
	}
	var account = {};
	account.name = response.name;
	account.id = response.id;

	var addFBdlg = this.zimlet.preferences.getAddFBInfoDlg();
	if (addFBdlg && addFBdlg.isPoppedUp()) { //might not be popped up if this comes from the error case in _getStreamCallback
		this.zimlet.preferences.hideAddFBInfoDlg();
		var msgDialog = appCtxt.getMsgDialog();
		var msg = this.zimlet.getMessage("accountAddedSuccessfully");
		msgDialog.setMessage(msg, DwtMessageDialog.INFO_STYLE);
		msgDialog.popup();
	}

	account.at = FB.getAuthResponse().accessToken;
	this.updateFacebookAccountFromObj(account);
	var authStr = this.zimlet.getMessage("accountsUpdated");
	this.zimlet.preferences._updateAccountsTable({message: authStr, color: "green"});
	this.zimlet.preferences._setAccountPrefDlgAuthMessage(authStr, "green");
	this._extendAccessToken(account);
};

/**
 * extending the access token from the short term (1-2 hours) to long term (60 days)
 * @param account
 * @private
 */
com_zimbra_socialFacebook.prototype._extendAccessToken = function(account) {
	var url = "https://graph.facebook.com/oauth/access_token";

	var params = [
		["client_id", this.appId],
		["client_secret", this.appSecret],
		["grant_type", "fb_exchange_token"],
		["fb_exchange_token", account.at]
	];
	var urlParams = this._getFBParams(params);

	this._doPOST(url, urlParams, this._extendAccessTokenHandleResponse.bind(this, account));
};

com_zimbra_socialFacebook.prototype._extendAccessTokenHandleResponse = function(account, response) {
	if (!response.success) {
		return;
	}

	var accessToken = null;
	var pairs = response.text.split("&");
	for (var i = 0; i < pairs.length; i++) {
		var pair = pairs[i].split("=");
		if (pair[0] === "access_token") {
			accessToken = pair[1];
			break;
		}
	}
	if (!accessToken) {
		return;
	}

	account.at = accessToken;
};

com_zimbra_socialFacebook.prototype._addFBComment = function(params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = this.zimlet.tableIdAndAccountMap[params.tableId];
	var paramsArray = [
		["method", "Stream.addComment"],
		["access_token", account.at],
		["comment", document.getElementById(params.commentFieldId).value],
		["uid", account.uid],
		["post_id", params.postId]
	];
	var urlParams = this._getFBParams(paramsArray, account.secret);
	params["account"] = account;
	this._doPOST(url, urlParams, new AjxCallback(this, this._addFBCommentCallback, params));
};

com_zimbra_socialFacebook.prototype._addFBCommentCallback =
function (params, response) {
	if (response.success) {
		var field = document.getElementById(params.commentFieldId);
		field.value = this.zimlet.getMessage("writeAComment");
		field.style.color = "gray";
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("commentAdded"), ZmStatusView.LEVEL_INFO);
		document.getElementById(params.commentBoxId).style.display = "none";
		//resetar timer
		var timer = setInterval(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 400000);
		this.zimlet.tableIdAndTimerMap[params.tableId] = timer;
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(this.zimlet.getMessage("couldNotAddCommentTofb")+"<br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype._publishToFacebook =
function (params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = params.account;
	var paramsArray = [
		["method", "Stream.publish"],
		["access_token", account.at],
		["message",params.message],
		["uid", account.uid]
	];
	if (params.targetUser != undefined) {
		paramsArray.push(["target_id", params.targetUser]);
	}
	var urlParams = this._getFBParams(paramsArray, account.secret);
	this._doPOST(url, urlParams, new AjxCallback(this, this._publishToFacebookCallback, params));
};

com_zimbra_socialFacebook.prototype._publishToFacebookCallback =
function (params, response) {
	if (response.success) {
		if (this.zimlet.updateField) {
			this.zimlet.updateField.value = "";
			this.zimlet.showNumberOfLetters();
		}

		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("updatesSent"), ZmStatusView.LEVEL_INFO);
		var tableId = this.zimlet._getTableIdFromAccount(params.account);
		if (tableId) {
			setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, tableId, params.account), 3000);//refresh table after 3 secs
		}
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		var msg = jsonObj.error;
		msgDialog.setMessage(this.zimlet.getMessage("couldNotPostToFacebook"), DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype._getExtendedPermissionCallback =
function (params, response) {
	var permission = params.permission;
	var account = params.account;
	if (response.text == "0")
		account[permission] = "NO";
	else if (response.text == "1")
		account[permission] = "YES";

	if (params.callback)
		params.callback.run(this);
};

com_zimbra_socialFacebook.prototype.authorizeExtendedPermission =
function(params) {
	var permission = params.permission;
	var account = params.account;
	var url = "https://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm=" + permission + "&api_key=" + this.apiKey;
	this.zimlet.openCenteredWindow(AjxStringUtil.urlComponentEncode(url + params));

};

com_zimbra_socialFacebook.prototype.getExtendedPermForRead =
function () {
	var url = "https://www.facebook.com/authorize.php?";
	var params = "version=1.0&ext_perm=read_stream&api_key=" + this.apiKey;
	this.zimlet.openCenteredWindow(url + params);
};

com_zimbra_socialFacebook.prototype._fbGetStream =
function (tableId, account) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.get"],
		["access_token", account.at],
		["limit", this.itemsLimit],
		["filter_key", "nf"] //there was also ",pp" here - but that caused an error. No idea what it was supposed to do.
	];

	var sinceOrUntilParams = this._getSinceOrUntilParams(tableId);
	if(sinceOrUntilParams != "") {
		paramsArray.push(sinceOrUntilParams);
	}
	var params = this._getFBParams(paramsArray, account.secret);
	if (!tableId) {
		var tableId = this.zimlet._showCard({headerName:"facebook", type:"FACEBOOK", autScroll:true});
	}
	this._doPOST(url, params, new AjxCallback(this, this._getStreamCallback, tableId));

};

com_zimbra_socialFacebook.prototype._getSinceOrUntilParams =
function (tableId) {
	var refreshType = this.zimlet.tableIdAndRefreshType[tableId];
	var id;
	var idName;
	if(refreshType == "OLDER") {
		id = this.zimlet._tableIdAndBottomPostIdMap[tableId];
		if(!id) {//when the first page has no results..
			id = this.zimlet.tableIdAndMarkAsReadId[tableId];
		}
		idName = "end_time";
	} else if(refreshType == "NEWER") {
		id = this.zimlet.tableIdAndTopPostIdMap[tableId];
		idName = "start_time";
	} else  {
		return "";
	}
	if(id) {
		return [idName, id];
	} else {
		return "";
	}
};

com_zimbra_socialFacebook.prototype.postLike =
function (obj) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.addLike"],
		["access_token", obj.account.at],
		["post_id", obj.postId]
	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._postLikeCallback, obj));
};

com_zimbra_socialFacebook.prototype._postLikeCallback =
function (params, response) {
	if (response.success) {
		setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
	} else {
		var msgDialog = appCtxt.getMsgDialog();
		msgDialog.setMessage(this.zimlet.getMessage("couldNotAddLikeToFb") +" <br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
};

com_zimbra_socialFacebook.prototype.insertMoreComments =
function (obj) {
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Stream.getComments"],
		["access_token", obj.account.at],
		["post_id", obj.postId]
	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._getMoreCommentsCallback, obj));
};

com_zimbra_socialFacebook.prototype._getMoreCommentsCallback =
function (obj, response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("couldNotGetComments"), response);
	if(jsonObj.error) {
		if(appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotGetComments") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	obj["moreComments"] = jsonObj;
	this._getUserInfo(obj);
};

com_zimbra_socialFacebook.prototype._getUserInfo =
function (obj) {
	var moreComments = obj.moreComments;
	var uids = "";
	for (var i = 0; i < moreComments.length; i++) {
		if (uids == "") {
			uids = moreComments[i].fromid;
		} else {
			uids = uids + "," + moreComments[i].fromid;
		}
	}
	var url = "https://api.facebook.com/restserver.php";
	var paramsArray = [
		["method", "Users.getInfo"],
		["uids", uids],
		["fields", "name,pic_square,profile_url"],
		["call_id", (new Date()).getTime()],
		["access_token", obj.account.at]

	];
	var params = this._getFBParams(paramsArray, obj.account.secret);
	this._doPOST(url, params, new AjxCallback(this, this._getUsersInfoCallback, obj));
};

com_zimbra_socialFacebook.prototype._getUsersInfoCallback =
function (obj, response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("tryRefreshingFBCard"), response);
	if(jsonObj.error) {
		if(appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("tryRefreshingFBCard") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var moreprofiles = jsonObj;
	var fbProfiles = this._getFBProfiles(obj.tableId);
	if(fbProfiles && fbProfiles instanceof Array) {
		fbProfiles = fbProfiles.concat(moreprofiles);
		this._cacheFBProfiles(obj.tableId, fbProfiles);
	}
	var html = this.zimlet._getCommentsHtml(obj.moreComments, obj.moreComments.length, obj.postId, obj.divId, obj.account, obj.tableId);
	try {
		document.getElementById(obj.divId).innerHTML = html;
	} catch(e) {
	}
};

com_zimbra_socialFacebook.prototype._getStreamCallback =
function (tableId, response) {
	var jsonObj = this.zimlet._extractJSONResponse(tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	var posts = jsonObj.posts;
	if(jsonObj.profiles && posts) {
		this._cacheFBProfiles(tableId, jsonObj.profiles);
		if(posts && !(posts instanceof Array)) {
			posts = [];
		}
		this.zimlet.createCardView({tableId:tableId, items:posts, type:"FACEBOOK"});
	}
	if(posts) {
		this.zimlet.createCardView({tableId:tableId, items:posts, type:"FACEBOOK"});
	}
	else if (jsonObj.error || jsonObj.error_code){
		this.loginToFB(true); //try to refresh the access token if app is authorized (or not logged in to facebook). Then the user can click "retry" and it might work.
		jsonObj.error = jsonObj.error || jsonObj.error_code;
		this.zimlet.createCardView({tableId:tableId, items:jsonObj, type:"FACEBOOK"});
	}
};

com_zimbra_socialFacebook.prototype._cacheFBProfiles =
function (tableId, profiles) {
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if(!pageNumber) {
		pageNumber = 1;
	}
	if(!this._tableIdAndFBProfilesCache[tableId]) {
		this._tableIdAndFBProfilesCache[tableId] = [];
	}
	this._tableIdAndFBProfilesCache[tableId][pageNumber] = profiles;
};

com_zimbra_socialFacebook.prototype._getFBProfiles =
function (tableId) {
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if(!pageNumber) {
		pageNumber = 1;
	}
	return this._tableIdAndFBProfilesCache[tableId][pageNumber];
};

com_zimbra_socialFacebook.prototype._getSignatureFromJSP =
function (args) {
	var params = new Array;
	for (var i = 0; i < args.length; i++) {
		var item = args[i];
		params.push(item[0] + "=" + item[1]);
	}
	params.push("isZD="+this.isZD);
	var url = this.zimlet.getResource("md5.jsp") + "?" + params.join("&");
	var response = AjxRpc.invoke(null, url, null, null, true);
	var obj = eval("(" + response.text + ")");
	return obj.signature;
};


com_zimbra_socialFacebook.prototype.loginToFB =
function (abortIfNotAuthorized) {

	var that = this;
	FB.getLoginStatus(function (response) {
		if (response.status === 'connected') {
			that._loadFacebookAccount();
		}
		else if (!abortIfNotAuthorized || response.status !== 'not_authorized') {
			//user is either logged in but not authorized, or not logged in. Anyway do the same - try to log in and authorize the user.
			that._doLoginToFB();
		}
	});
};

com_zimbra_socialFacebook.prototype._doLoginToFB = function() {
	var that = this;
	FB.login(function(response) {
		if (!response.authResponse) {
			//the user did not login (canceled or didn't authorize)
			return;
		}
		that._loadFacebookAccount();
	}, {scope: this._extendedPerms});
};


com_zimbra_socialFacebook.prototype._convertFB_JsonStrToUrlEncodedStr = function(text) {
	var jsonObj = eval("(" + text + ")");
	return ["id=", jsonObj.id,"&at=",accessToken,"&name=",jsonObj.name].join("");
};

com_zimbra_socialFacebook.prototype._doPOST =
function (url, params, callback) {
	var hdrs = [];
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = params.length;
	hdrs["Connection"] = "close";
	var entireurl = ZmZimletBase.PROXY + url;
	AjxRpc.invoke(params, entireurl, hdrs, callback, false);
};

com_zimbra_socialFacebook.prototype._getFBParams =
function(otherParamsArray, secret, signatureFromJSP) {
	var paramsArray = [
		["format", "json"]
	].concat(otherParamsArray);

	var arry = new Array();
	for (var i = 0; i < paramsArray.length; i++) {
		arry.push(AjxStringUtil.urlComponentEncode(paramsArray[i][0]) + "=" + AjxStringUtil.urlComponentEncode(paramsArray[i][1]));
	}
	return arry.join("&");
};


com_zimbra_socialFacebook.prototype.updateFacebookAccount = function(text) {
	var nv = text.split("&");
	var tObj = {};
	for (var i = 0; i < nv.length; i++) {
		var tmp = nv[i].split("=");
		tObj[tmp[0]] = tmp[1];
	}
	this.updateFacebookAccountFromObj(tObj);
};


com_zimbra_socialFacebook.prototype.updateFacebookAccountFromObj = function(tObj) {
	if (tObj["__type"] == undefined) {
		tObj["__type"] = "facebook";
	}
	if (tObj["__on"] == undefined) {
		tObj["__on"] = "true";
	}
	if (tObj["__pos"] == undefined) {
		tObj["pos"] = "";
	}
	if (tObj["__s"] == undefined) { //__s means shown & 1 means true
		tObj["__s"] = "1";
	}
	//to normalize names with fb
	tObj.type = tObj["__type"];
	this.zimlet.allAccounts[this._accountIndex(tObj)] = tObj;
};

com_zimbra_socialFacebook.prototype._accountIndex = function(account) {
	return account.type + account.id;
};

com_zimbra_socialFacebook.prototype._updateFacebookStream =
function(tableId, account) {
	this._fbGetStream(tableId, account);
};

com_zimbra_socialFacebook.prototype.getFacebookProfile =
function(id, tableId) {
	var fbProfiles = this._getFBProfiles(tableId);
	for (var i = 0; i < fbProfiles.length; i++) {
		var reqId = fbProfiles[i].id == undefined ? fbProfiles[i].uid : fbProfiles[i].id;
		if (id == reqId) {
			return fbProfiles[i];
		}
	}
};