/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

//Author: Raja Rao DV (rrao@zimbra.com)

function com_zimbra_socialcast(zimlet) {
	this.zimlet = zimlet;
	this._accountEmailAndMenuMap = [];
	this.accountEmailAndSelectedGroupIdMap = [];
}

com_zimbra_socialcast.prototype._insertMoreComments = function(params) {
	var hDivs = this.zimlet._scCommentDivAndHiddenDivsMap[params.divId];
	for (var i = 0; i < hDivs.length; i++) {
		var hDiv = document.getElementById(hDivs[i]);
		if (hDiv) {
			hDiv.style.display = "block";
		}
	}
	var moreLinkDiv = document.getElementById(params.moreLinkDivId);
	if (moreLinkDiv) {
		moreLinkDiv.style.display = "none";
	}
};

com_zimbra_socialcast.prototype.getMessages = function(tableId, account, streamId) {
	var authHeader = this.make_basic_auth(account.e, account.p);
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if (!pageNumber || pageNumber < 0) {
		pageNumber = 1;
	}
	if(streamId) {
		var url = [ "https://", account.s, "/api/streams/",streamId,"/messages.json?page=", pageNumber].join("");
	} else {
		var url = [ "https://", account.s, "/api/messages.json?page=", pageNumber].join("");
	}
	var hdrs = new Array();
	hdrs["Authorization"] = authHeader;
	//hdrs["content-type"] = "application/x-www-form-urlencoded";
	//hdrs["content-length"] = data.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(data, feedUrl, hdrs, new AjxCallback(this,
			this._handleGetMessages, tableId), true);
};

com_zimbra_socialcast.prototype.showUserProfile = function (params) {
	var tweetTableId = params.tableId;
	var account = this.zimlet.tableIdAndAccountMap[tweetTableId];
	var userId = params.userId;
	var tableId = this.zimlet._showCard({headerName:params.fullName, type:"PROFILE", tweetTableId:tweetTableId, autoScroll:true});
	 params.tableId = tableId;
	var authHeader = this.make_basic_auth(account.e, account.p);

	var url = [ "https://", account.s, "/api/users/",userId,".json"].join("");
	var hdrs = new Array();
	hdrs["Authorization"] = authHeader;

	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(data, feedUrl, hdrs, new AjxCallback(this,
			this._handleShowUserProfile, params), true);
};

com_zimbra_socialcast.prototype._handleShowUserProfile = function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	if (jsonObj.error) {
		if (appCtxt.getCurrentAppName().indexOf("social") == -1) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("errorTryRefreshing") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
};



com_zimbra_socialcast.prototype.setSocialcastStreams = function(params) {
	var account = params.account;
	var authHeader = this.make_basic_auth(account.e, account.p);
	var url = [ "https://", account.s, "/api/streams.json"].join("");
	var hdrs = new Array();
	hdrs["Authorization"] = authHeader;
	//hdrs["content-type"] = "application/x-www-form-urlencoded";
	//hdrs["content-length"] = data.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(data, feedUrl, hdrs, new AjxCallback(this,
			this._handleSetSocialcastStreams, params), true);
};

com_zimbra_socialcast.prototype._handleSetSocialcastStreams = function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	if (jsonObj.error) {
		if (appCtxt.getCurrentAppName().indexOf("social") == -1) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("errorTryRefreshing") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var streams = jsonObj.streams;
	var arry = [];
	var len = (streams instanceof Array) ? streams.length : 0;
	for (var i = 0; i < len; i++) {
		var stream = streams[i];
		arry.push({n: stream.name, id: stream.id});
	}
	var account = params.account;
	this.zimlet.setUserProperty(account.e + "_streams", JSON.stringify(arry));
	this.zimlet.saveUserProperties();
	if (params.postCallback) {
		params.postCallback.run(account);
	}
};

com_zimbra_socialcast.prototype.setGroupMemberships = function(params) {
	var account = params.account;
	var authHeader = this.make_basic_auth(account.e, account.p);
	var url = [ "https://", account.s, "/api/group_memberships.json"].join("");
	var hdrs = new Array();
	hdrs["Authorization"] = authHeader;
	//hdrs["content-type"] = "application/x-www-form-urlencoded";
	//hdrs["content-length"] = data.length;
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(data, feedUrl, hdrs, new AjxCallback(this,
			this._handleGetGroupMemberships, params), true);
};

com_zimbra_socialcast.prototype._handleGetGroupMemberships = function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	if (jsonObj.error) {
		if (appCtxt.getCurrentAppName().indexOf("social") == -1) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("errorTryRefreshing") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var groupMemberships = jsonObj.group_memberships;
	var arry = [];
	var len = (groupMemberships instanceof Array) ? groupMemberships.length : 0;
	for (var i = 0; i < len; i++) {
		var group = groupMemberships[i].group;
		arry.push({n: group.name, id:group.id});

	}
	var account = params.account;
	this.zimlet.setUserProperty(account.e + "_groupMemberships", JSON.stringify(arry));
	this.zimlet.saveUserProperties();
	if (params.postCallback) {
		params.postCallback.run(account);
	}
};

com_zimbra_socialcast.prototype._handleGetMessages = function(tableId, response) {
	var jsonObj = this.zimlet._extractJSONResponse(tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
	if (jsonObj.error) {
		if (appCtxt.getCurrentAppName().indexOf("social") == -1) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("errorTryRefreshing") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	this.zimlet.createCardView({tableId:tableId, items:jsonObj.messages,  type:"SOCIALCAST"});
};

com_zimbra_socialcast.prototype.make_basic_auth = function(user, password) {
	var tok = user + ':' + password;
	var hash = Base64.encode(tok);
	return "Basic " + hash;
};

com_zimbra_socialcast.prototype._addScComment = function(params) {
	var url = "https://api.facebook.com/restserver.php";
	var account = this.zimlet.tableIdAndAccountMap[params.tableId];
	var comment = document.getElementById(params.commentFieldId).value;
	var url = [ "https://", account.s, "/api/messages/",params.postId,"/comments.json"].join("");
	var paramsArray = [
		["comment[text]", comment]
		//["message[from]", "VMware Zimbra"]
	];

	//if (params.targetUser != undefined) {
	//	paramsArray.push(["target_id", params.targetUser]);
	//}
	var urlParams = this._getSCParams(paramsArray);
	this._doPOST(url, urlParams, new AjxCallback(this, this._addSCCommentCallback, params), account);
};

com_zimbra_socialcast.prototype._addSCCommentCallback =
		function (params, response) {
			if (response.success) {
				var account = this.zimlet.tableIdAndAccountMap[params.tableId];
				var field = document.getElementById(params.commentFieldId);
				field.value = this.zimlet.getMessage("writeAComment");
				field.style.color = "gray";
				appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("commentAdded"), ZmStatusView.LEVEL_INFO);
				document.getElementById(params.commentBoxId).style.display = "none";
				var tableId = params.tableId;
				//resetart timer
				if (tableId) {
					var timer = setInterval(AjxCallback.simpleClosure(this.zimlet._doRefreshFeeds, this.zimlet, tableId, account.type), 400000);
					this.zimlet.tableIdAndTimerMap[tableId] = timer;
					setTimeout(AjxCallback.simpleClosure(this.zimlet._doRefreshFeeds, this.zimlet, tableId, account.type), 3000);//refresh table after 3 secs
				}
			} else {
				var msgDialog = appCtxt.getMsgDialog();
				msgDialog.setMessage(this.zimlet.getMessage("couldNotAddCommentToSC") + "<br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
				msgDialog.popup();
			}
		};

com_zimbra_socialcast.prototype._publishToSocialcast =
		function (params) {
			//var url = "https://api.facebook.com/restserver.php";
			var account = params.account;
			var url = [ "https://", account.s, "/api/messages.json"].join("");
			var groupId = this.accountEmailAndSelectedGroupIdMap[account.e];
			if(!groupId) {
				groupId = "";
			}
			var paramsArray = [
				["message[body]", params.message],
				["message[group_id]", groupId]
			];

			//if (params.targetUser != undefined) {
			//	paramsArray.push(["target_id", params.targetUser]);
			//}
			var urlParams = this._getSCParams(paramsArray);
			this._doPOST(url, urlParams, new AjxCallback(this, this._publishToSocialcastCallback, params), account);
		};

com_zimbra_socialcast.prototype._publishToSocialcastCallback =
		function (params, response) {
			var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("tryRefreshingFBCard"), response);
			if (jsonObj.error) {
				if (appCtxt.getCurrentAppName().indexOf("social") == -1) {//dont show error unless in social tab
					return;
				}
				appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotUpdate") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
			} else {
				if (this.zimlet.updateField) {
					this.zimlet.updateField.value = "";
					this.zimlet.showNumberOfLetters();
				}

				appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("updatesSent"), ZmStatusView.LEVEL_INFO);
				var tableId = this.zimlet._getTableIdFromAccount(params.account);
				if (tableId) {
					setTimeout(AjxCallback.simpleClosure(this.zimlet._doRefreshFeeds, this.zimlet, tableId, params.account.type), 3000);//refresh table after 3 secs
				}
			}
		};

com_zimbra_socialcast.prototype._getExtendedPermissionInfo =
		function (params) {
			var url = "https://api.facebook.com/restserver.php";
			var account = params.account;
			var paramsArray = [
				["method", "Users.hasAppPermission"],
				["session_key", account.session_key],
				["ext_perm", params.permission],
				["uid", account.uid],
				["call_id", (new Date()).getTime()]
			];
			var urlParams = this._getSCParams(paramsArray, account.secret);
			this._doPOST(url, urlParams, new AjxCallback(this, this._getExtendedPermissionCallback, params));
		};


com_zimbra_socialcast.prototype._getExtendedPermissionCallback =
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


com_zimbra_socialcast.prototype.authorizeExtendedPermission =
		function(params) {
			var permission = params.permission;
			var account = params.account;
			var url = "https://www.facebook.com/authorize.php?";
			var params = "version=1.0&ext_perm=" + permission + "&api_key=" + this.apiKey;
			this.zimlet.openCenteredWindow(AjxStringUtil.urlComponentEncode(url + params));

		};

com_zimbra_socialcast.prototype.getExtendedPermForRead =
		function () {
			var url = "https://www.facebook.com/authorize.php?";
			var params = "version=1.0&ext_perm=read_stream&api_key=" + this.apiKey;
			this.zimlet.openCenteredWindow(url + params);
		};

com_zimbra_socialcast.prototype._fbGetStream =
		function (tableId, account) {
			var url = "https://api.facebook.com/restserver.php";
			var paramsArray = [
				["method", "Stream.get"],
				["session_key", account.session_key],
				["limit", this.itemsLimit],
				["filter_key", "nf,pp"]
			];

			var sinceOrUntilParams = this._getSinceOrUntilParams(tableId);
			if (sinceOrUntilParams != "") {
				paramsArray.push(sinceOrUntilParams);
			}
			var params = this._getSCParams(paramsArray, account.secret);
			if (!tableId) {
				var tableId = this.zimlet._showCard({headerName:"facebook", type:"FACEBOOK", autScroll:true});
			}
			this._doPOST(url, params, new AjxCallback(this, this._getStreamCallback, tableId));
		};

com_zimbra_socialcast.prototype._getSinceOrUntilParams =
		function (tableId) {
			var refreshType = this.zimlet.tableIdAndRefreshType[tableId];
			var id;
			var idName;
			if (refreshType == "OLDER") {
				id = this.zimlet._tableIdAndBottomPostIdMap[tableId];
				if (!id) {//when the first page has no results..
					id = this.zimlet.tableIdAndMarkAsReadId[tableId];
				}
				idName = "end_time";
			} else if (refreshType == "NEWER") {
				id = this.zimlet.tableIdAndTopPostIdMap[tableId];
				idName = "start_time";
			} else {
				return "";
			}
			if (id) {
				return [idName, id];
			} else {
				return "";
			}
		};

com_zimbra_socialcast.prototype.postLike =
		function (params) {
			var account = params.account;
			//https://demo.socialcast.com/api/messages/399/likes.json
			var url = ["https://",account.s,"/api/messages/", params.postId, "/likes.json"].join("");
			this._doPOST(url, "", new AjxCallback(this, this._publishToSocialcastCallback, params), account);
		};

com_zimbra_socialcast.prototype._postLikeCallback =
		function (params, response) {
			if (response.success) {
				setTimeout(AjxCallback.simpleClosure(this._updateFacebookStream, this, params.tableId, params.account), 3000);//refresh table after 3 secs
			} else {
				var msgDialog = appCtxt.getMsgDialog();
				msgDialog.setMessage(this.zimlet.getMessage("couldNotAddLikeToFb") + " <br/>" + response.text, DwtMessageDialog.WARNING_STYLE);
				msgDialog.popup();
			}
		};

com_zimbra_socialcast.prototype.insertMoreComments =
		function (obj) {
			var url = "https://api.facebook.com/restserver.php";
			var paramsArray = [
				["method", "Stream.getComments"],
				["session_key", obj.account.session_key],
				["post_id", obj.postId]
			];
			var params = this._getSCParams(paramsArray, obj.account.secret);
			this._doPOST(url, params, new AjxCallback(this, this._getMoreCommentsCallback, obj));
		};

com_zimbra_socialcast.prototype._getMoreCommentsCallback =
		function (obj, response) {
			var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("couldNotGetComments"), response);
			if (jsonObj.error) {
				if (appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
					return;
				}
				appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotGetComments") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
				return;
			}
			obj["moreComments"] = jsonObj;
			this._getUserInfo(obj);
		};

com_zimbra_socialcast.prototype._getUserInfo =
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
				["session_key", obj.account.session_key]

			];
			var params = this._getSCParams(paramsArray, obj.account.secret);
			this._doPOST(url, params, new AjxCallback(this, this._getUsersInfoCallback, obj));
		};

com_zimbra_socialcast.prototype._getUsersInfoCallback =
		function (obj, response) {
			var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("tryRefreshingFBCard"), response);
			if (jsonObj.error) {
				if (appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
					return;
				}
				appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("tryRefreshingFBCard") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
				return;
			}
			var moreprofiles = jsonObj;
			var fbProfiles = this._getFBProfiles(obj.tableId);
			if (fbProfiles && fbProfiles instanceof Array) {
				fbProfiles = fbProfiles.concat(moreprofiles);
				this._cacheFBProfiles(obj.tableId, fbProfiles);
			}
			var html = this.zimlet._getCommentsHtml(obj.moreComments, obj.moreComments.length, obj.postId, obj.divId, obj.account, obj.tableId);
			try {
				document.getElementById(obj.divId).innerHTML = html;
			} catch(e) {
			}
		};

com_zimbra_socialcast.prototype._getStreamCallback =
		function (tableId, response) {
			var jsonObj = this.zimlet._extractJSONResponse(tableId, this.zimlet.getMessage("errorTryRefreshing"), response);
			var posts = jsonObj.posts;
			if (jsonObj.profiles && posts) {
				this._cacheFBProfiles(tableId, jsonObj.profiles);
				if (posts && !(posts instanceof Array)) {
					posts = [];
				}
				this.zimlet.createCardView({tableId:tableId, items:posts, type:"FACEBOOK"});
			} else if (jsonObj.error) {
				this.zimlet.createCardView({tableId:tableId, items:jsonObj, type:"FACEBOOK"});
			} else if (jsonObj.error_code && jsonObj.error_code != "") {
				jsonObj.error = jsonObj.error_code;
				this.zimlet.createCardView({tableId:tableId, items:jsonObj,  type:"FACEBOOK"});
			}
		};

com_zimbra_socialcast.prototype._cacheFBProfiles =
		function (tableId, profiles) {
			var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			if (!this._tableIdAndFBProfilesCache[tableId]) {
				this._tableIdAndFBProfilesCache[tableId] = [];
			}
			this._tableIdAndFBProfilesCache[tableId][pageNumber] = profiles;
		};

com_zimbra_socialcast.prototype._getFBProfiles =
		function (tableId) {
			var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			return this._tableIdAndFBProfilesCache[tableId][pageNumber];
		};

com_zimbra_socialcast.prototype._getSignatureFromJSP =
		function (args) {
			var params = new Array;
			for (var i = 0; i < args.length; i++) {
				var item = args[i];
				params.push(item[0] + "=" + item[1]);
			}
			params.push("isZD=" + this.isZD);
			var url = this.zimlet.getResource("md5.jsp") + "?" + params.join("&");
			var response = AjxRpc.invoke(null, url, null, null, true);
			var obj = eval("(" + response.text + ")");
			return obj.signature;
		};

com_zimbra_socialcast.prototype.fbCreateToken =
		function () {
			var url = "https://api.facebook.com/restserver.php";
			var paramsArray = [
				["method", "Auth.createToken"]
			];
			var signature = this._getSignatureFromJSP(paramsArray);
			setTimeout(AjxCallback.simpleClosure(this._doFbCreateToken, this, url, paramsArray, signature), 500);//delay calling by .5 secs(otherwise, sometimes breaks in ff)
		};

com_zimbra_socialcast.prototype._doFbCreateToken =
		function (url, paramsArray, signature) {
			var params = this._getSCParams(paramsArray, null, signature);
			this._doPOST(url, params, new AjxCallback(this, this._fbCreateTokenCallback));
			this.zimlet.preferences.showAddFBInfoDlg();
		};

com_zimbra_socialcast.prototype._fbCreateTokenCallback =
		function (response) {
			var text = response.text;
			this.fb_auth_token = eval("(" + text + ")");
			this.loginToFB(this.fb_auth_token);
		};

com_zimbra_socialcast.prototype.loginToFB =
		function (authToken) {
			var url = "https://www.facebook.com/login.php?";
			var params = new Array();
			params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
			params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
			params["v"] = AjxStringUtil.urlComponentEncode("1.0");
			params["connect_display"] = AjxStringUtil.urlComponentEncode("popup");
			params["next"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_success.html");
			params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");
			if (authToken) {
				params["auth_token"] = AjxStringUtil.urlComponentEncode(this.fb_auth_token);
			}
			if (!authToken) {
				params["req_perms"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
			}
			var tmp = [];
			for (var name in params) {
				tmp.push(name + "=" + params[name]);
			}
			var p = tmp.join("&");
			url = url + p;
			this.zimlet.openCenteredWindow(url);
		};

com_zimbra_socialcast.prototype._getSessionId =
		function () {
			var url = "https://api.facebook.com/restserver.php";
			var paramsArray = [
				["method", "auth.getSession"],
				["auth_token", this.fb_auth_token]
			];
			var signature = this._getSignatureFromJSP(paramsArray);
			setTimeout(AjxCallback.simpleClosure(this._doGetSessionId, this, url, paramsArray, signature), 500);//delay calling by .5 secs(otherwise, sometimes breaks in ff)


		};
com_zimbra_socialcast.prototype._doGetSessionId =
		function (url, paramsArray, signature) {
			var params = this._getSCParams(paramsArray, null, signature);
			this._doPOST(url, params, new AjxCallback(this, this._sessionIdCallback));
		};

com_zimbra_socialcast.prototype._sessionIdCallback =
		function (response) {
			var text = response.text;
			if (text.indexOf("session_key") >= 0 && text.indexOf("secret") >= 0) {
				var fbStr = this._convertFB_JsonStrToUrlEncodedStr(text);
				this.manageFacebookAccounts(fbStr);
				var authStr = this.zimlet.getMessage("fbSignInLine3");
				this.zimlet.preferences._setAccountPrefDlgAuthMessage(authStr, "blue");
				this.zimlet.preferences._updateAccountsTable({message:authStr, color:"blue"});
				this.zimlet.preferences._updateAllFBPermissions({message:authStr, color:"blue",askForPermissions:false});
			}
			this.zimlet.preferences._getFbInfoDialog.popdown();
		};

com_zimbra_socialcast.prototype.askForPermissions =
		function (fromAuthorizeBtn) {
			var params = new Array();
			if (!fromAuthorizeBtn) {
				var url = "https://www.facebook.com/connect/prompt_permissions.php?";
				params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
				params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
				params["v"] = AjxStringUtil.urlComponentEncode("1.0");
				params["display"] = AjxStringUtil.urlComponentEncode("popup");
				params["extern"] = AjxStringUtil.urlComponentEncode("1");
				params["next"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_success.html");
				params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");

				params["ext_perm"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
			} else {
				var url = "https://www.facebook.com/login.php?";
				params["api_key"] = AjxStringUtil.urlComponentEncode(this.apiKey);
				params["fbconnect"] = AjxStringUtil.urlComponentEncode("true");
				params["v"] = AjxStringUtil.urlComponentEncode("1.0");
				params["connect_display"] = AjxStringUtil.urlComponentEncode("popup");
				params["cancel_url"] = AjxStringUtil.urlComponentEncode("http://www.facebook.com/connect/login_failure.html");
				params["req_perms"] = AjxStringUtil.urlComponentEncode(this._extendedPerms);
			}
			var tmp = [];
			for (var name in params) {
				tmp.push(name + "=" + params[name]);
			}
			var p = tmp.join("&");
			url = url + p;
			this.zimlet.openCenteredWindow(url);
		};

com_zimbra_socialcast.prototype._convertFB_JsonStrToUrlEncodedStr = function(text) {
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

com_zimbra_socialcast.prototype._doPOST =
		function (url, params, callback, account) {
			var hdrs = new Array();
			hdrs["Content-type"] = "application/x-www-form-urlencoded";
			hdrs["Content-length"] = params.length;
			hdrs["Connection"] = "close";
			hdrs["Authorization"] = this.make_basic_auth(account.e, account.p);
			var entireurl = ZmZimletBase.PROXY + url;
			AjxRpc.invoke(params, entireurl, hdrs, callback, false);
		};

com_zimbra_socialcast.prototype._getSCParams =
		function(paramsArray) {
			var arry = new Array();
			for (var i = 0; i < paramsArray.length; i++) {
				arry.push(AjxStringUtil.urlComponentEncode(paramsArray[i][0]) + "=" + AjxStringUtil.urlComponentEncode(paramsArray[i][1]));
			}
			return arry.join("&");
		};

com_zimbra_socialcast.prototype.manageFacebookAccounts = function(text) {
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
	if (tObj["__pos"] == undefined) {
		tObj["pos"] = "";
	}
	if (tObj["__s"] == undefined) { //__s means shown & 1 means true
		tObj["__s"] = "1";
	}
	//to normalize names with fb
	tObj.raw = text;
	tObj.name = "facebook";
	tObj.type = tObj["__type"];
	this.zimlet.allAccounts[tObj.name + tObj.uid] = tObj;
};

com_zimbra_socialcast.prototype._updateFacebookStream =
		function(tableId, account) {
			this._fbGetStream(tableId, account);
		};

com_zimbra_socialcast.prototype.getFacebookProfile =
		function(id, tableId) {
			var fbProfiles = this._getFBProfiles(tableId);
			for (var i = 0; i < fbProfiles.length; i++) {
				var reqId = fbProfiles[i].id == undefined ? fbProfiles[i].uid : fbProfiles[i].id;
				if (id == reqId) {
					return fbProfiles[i];
				}
			}
		};

com_zimbra_socialcast.prototype.showGroupsMenu = function(account, ev) {
	if (AjxEnv.isIE) {
		ev = window.event;
	}
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	var accEmail = account.e;
	if (this._accountEmailAndMenuMap[accEmail]) {
		var menu = this._accountEmailAndMenuMap[accEmail].menu;
		if (menu) {
			menu.popup(0, dwtev.docX, dwtev.docY);
			// if we have an action menu, don't let the browser show its context menu too
			ev._dontCallPreventDefault = false;
			ev._returnValue = false;
			ev._stopPropagation = true;
			return;
		}
	}
	if (!this._groupMenus) {
		this._groupMenus = [];
	} else if (this._groupMenus[accEmail]) {
		this._groupMenus[accEmail].popup();
		return;
	}
	ev._dontCallPreventDefault = true;
	ev._returnValue = true;
	ev._stopPropagation = true;
	var gm = this.zimlet.getUserProperty(accEmail + "_groupMemberships");
	var selectedGroupId = this.zimlet.getUserProperty(accEmail+ "_selectedGroupId");
	if (!gm) {
		gm = [];
	} else {
		gm = JSON.parse(gm);
	}
	var icon = "social_socialcastIcon";
	var menu = new ZmActionMenu({parent:DwtShell.getShell(window), menuItems:ZmOperation.NONE});
	//var params = {image:data.icon, text:this.process(data.label),disImage:data.disabledIcon};
	//var item = menu.createMenuItem(data.id, params);
	var groupId = this.zimlet.getUserProperty(accEmail+ "_selectedGroupId");
	if(!groupId) {
		groupId = "";
	}
	this.accountEmailAndSelectedGroupIdMap[accEmail] = groupId;

	var mi = menu.createMenuItem(id, {image:icon, text:"My Colleagues", style:DwtMenuItem.RADIO_STYLE});
	if(groupId == "") {
		mi.setChecked(true, true);//sets the item as checked by default
	}
	mi.addSelectionListener(new AjxListener(this, this._saveSelectedGroupIdForAccount, [accEmail, ""]));
	for (var j = 0; j < gm.length; j++) {
		var group = gm[j];
		var checked = false;
		var id = group.id;
		var miId = "socialcast_group_id" + id;
		var mi = menu.createMenuItem(miId, {image:icon, text:group.n, style:DwtMenuItem.RADIO_STYLE});
		mi.addSelectionListener(new AjxListener(this, this._saveSelectedGroupIdForAccount, [accEmail,  group.id]));
		if (selectedGroupId && selectedGroupId == id) {
			mi.setChecked(true, true);//sets the item as checked
		}
	}
	if (menu) {
		this._accountEmailAndMenuMap[accEmail] = {account:account, menu:menu};
		menu.popup(0, dwtev.docX, dwtev.docY);
		// if we have an action menu, don't let the browser show its context menu too
		ev._dontCallPreventDefault = false;
		ev._returnValue = false;
		ev._stopPropagation = true;
	}

};


com_zimbra_socialcast.prototype._saveSelectedGroupIdForAccount = function(accEmail, groupId) {
	this.accountEmailAndSelectedGroupIdMap[accEmail] = groupId;
	this.zimlet.setUserProperty(accEmail+ "_selectedGroupId", groupId, true);
};

