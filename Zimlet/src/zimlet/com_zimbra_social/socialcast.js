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

com_zimbra_socialcast.prototype.authenticate = function(account, postCallback) {
	var hdrs = new Array();
	var data = ["email=", AjxStringUtil.urlEncode(account.e),"&password=", AjxStringUtil.urlEncode(account.p)].join("");
	hdrs["content-type"] = "application/x-www-form-urlencoded";
	hdrs["content-length"] = data.length;
	var feedUrl = ZmZimletBase.PROXY + ["https://",account.s,"/api/authentication.json"].join("");
	var authCallback = new AjxCallback(this,
			this._handleAuthenticate, [account, postCallback]);
	var response = AjxRpc.invoke(data, feedUrl, hdrs, authCallback, false);

};

com_zimbra_socialcast.prototype._handleAuthenticate = function(account, postCallback, response) {
	if(response.success) {
		account.isValid = true;
	} else {
		account.isValue = false;
	}
	if(postCallback) {
		postCallback.run(account);
	}
};

com_zimbra_socialcast.prototype.getMessages = function(tableId, account, streamId) {
	var authHeader = this.make_basic_auth(account.e, account.p);
	var pageNumber = this.zimlet.tableIdAndPageNumberMap[tableId];
	if (!pageNumber || pageNumber < 0) {
		pageNumber = 1;
	}
	if (streamId) {
		var url = [ "https://", account.s, "/api/streams/",streamId,"/messages.json?page=", pageNumber].join("");
	} else {
		var url = [ "https://", account.s, "/api/messages.json?page=", pageNumber].join("");
	}
	var hdrs = new Array();
	hdrs["Authorization"] = authHeader;

	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this,
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
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this,
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
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this,
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
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, feedUrl, hdrs, new AjxCallback(this,
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
	this.zimlet.createCardView({tableId:tableId, items:(jsonObj.messages || jsonObj),  type:"SOCIALCAST"});
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
			if (!groupId) {
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
	var selectedGroupId = this.zimlet.getUserProperty(accEmail + "_selectedGroupId");
	if (!gm) {
		gm = [];
	} else {
		gm = JSON.parse(gm);
	}
	var icon = "social_socialcastIcon";
	var menu = new ZmActionMenu({parent:DwtShell.getShell(window), menuItems:ZmOperation.NONE});
	var groupId = this.zimlet.getUserProperty(accEmail + "_selectedGroupId");
	if (!groupId) {
		groupId = "";
	}
	this.accountEmailAndSelectedGroupIdMap[accEmail] = groupId;

	var mi = menu.createMenuItem(id, {image:icon, text:"My Colleagues", style:DwtMenuItem.RADIO_STYLE});
	if (groupId == "") {
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
	this.zimlet.setUserProperty(accEmail + "_selectedGroupId", groupId, true);
};

