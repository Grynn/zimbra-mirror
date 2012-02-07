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

function com_zimbra_socialTwitter(zimlet, preferences) {
	this.zimlet = zimlet;
	this.preferences = preferences;
	this.allSearches = new Array();
	this.allTrends = new Array();
	this.emailContentObj = new Array();
	this.showAlertObj = new Array();
	this.loadAllSearchesFromDB();
	this.isZD = false;
	try {
		var version = appCtxt.getActiveAccount().settings.getInfoResponse.version;
		if (version.toLowerCase().indexOf("desktop") > 0) {
			this.isZD = true;
		}
	} catch(e) {
		//ignore
	}
}
com_zimbra_socialTwitter.FRIENDS_TIMELINE_URL = "https://api.twitter.com/1/statuses/friends_timeline.json";
com_zimbra_socialTwitter.MENTIONS_URL = "https://api.twitter.com/1/statuses/mentions.json";
com_zimbra_socialTwitter.DM_URL = "https://api.twitter.com/1/direct_messages.json";
com_zimbra_socialTwitter.DM_URL_POST = "https://api.twitter.com/1/direct_messages/new.json";
com_zimbra_socialTwitter.UPDATE_URL = "http://api.twitter.com/1/statuses/update.json";
com_zimbra_socialTwitter.PROFILE_BASE_URL = "https://twitter.com/statuses/user_timeline/";
com_zimbra_socialTwitter.DELETE_POST_BASE_URL = "https://api.twitter.com/1/statuses/destroy/";
com_zimbra_socialTwitter.SEARCH_BASE_URL = "http://search.twitter.com/search.json";
com_zimbra_socialTwitter.FRIENDSHIP_BASE_URL = "https://twitter.com/friendships/show.json";


com_zimbra_socialTwitter.prototype._getTodayStr = function() {
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(), todayDate.getMonth(), todayDate.getDate());
	return this._normalizeDate(todayStart.getMonth() + 1, todayStart.getDate(), todayStart.getFullYear());
};

com_zimbra_socialTwitter.prototype._normalizeDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"yyyy", indx:ds.indexOf("yyyy")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(social_sortTimeObjs);
	for (var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if (name == "m") {
			fString.push(month);
		} else if (name == "yyyy") {
			fString.push(year);
		} else if (name == "d") {
			fString.push(day);
		}
	}
	return fString.join("/");
};

function social_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}

com_zimbra_socialTwitter.prototype.getTwitterTrends =
function() {
	//23424977 is woeid for USA
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("https://api.twitter.com/1/trends/23424977.json");
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._trendsCallback), true);
};

com_zimbra_socialTwitter.prototype._trendsCallback =
function(response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("twitterError"), response);
	var trends = [];
	if(jsonObj && jsonObj[0] && jsonObj[0].trends) {
		trends =  jsonObj[0].trends;
	}

	this.allTrends = new Array();
	for (var i = 0; i < trends.length; i++) {
		this.allTrends[trends[i].name] = true;
	}
	if (this.zimlet.preferences.social_pref_trendsPopularIsOn && trends.length > 0) {
		for (var i = 0; i < 1; i++) {
			var name = trends[i].name;
			var tableId = this.zimlet._showCard({headerName:name, type:"TREND", autoScroll:false});
			var sParams = {query:name, tableId:tableId, type:"TREND"};
			this.twitterSearch(sParams);
			var timer = setInterval(AjxCallback.simpleClosure(this.twitterSearch, this, sParams), 400000);
			this.zimlet.tableIdAndTimerMap[tableId] = timer;
		}
		this.zimlet._updateAllWidgetItems({updateTrendsTree:true});
	}
};

com_zimbra_socialTwitter.prototype.twitterSearch =
function(params) {
	var components = new Array();
	components["rpp"] = this.preferences.social_pref_numberofTweetsSearchesToReturn;
	components["q"] = params.query;
	this._addSinceAndMaxIds(params.tableId, components);
	var callback = new AjxCallback(this, this._twitterSearchCallback, params);
	this.zimlet.socialOAuth.makeSimpleHTTPGet({url: com_zimbra_socialTwitter.SEARCH_BASE_URL, components: components, callback: callback});

};

com_zimbra_socialTwitter.prototype._twitterSearchCallback =
function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("twitterError"), response);
	if (jsonObj.results) {
		jsonObj = jsonObj.results;
	}
	this.zimlet.createCardView({tableId:params.tableId, items:jsonObj, type:params.type, additionalParams:params});
};

com_zimbra_socialTwitter.prototype.getTwitterFeeds =
function(params) {
	var account = params.account;
	var callback = params.callback;
	var screen_name = params.screen_name;//used for user profile
	var type = params.type;
	if (!callback) {
		callback = new AjxCallback(this, this._twitterItemsHandler, {account:account, tableId:params.tableId, type:type});
	}
	this.zimlet.socialOAuth.setAuthTokens({oauth_token: account.oauth_token, oauth_token_secret: account.oauth_token_secret});
	var components = this._getAdditionalParams(params.tableId);
	var url = "";
	var useSimpleHttpGet = false;
	if (type == "ACCOUNT") {
		url = com_zimbra_socialTwitter.FRIENDS_TIMELINE_URL;
	} else if (type == "DIRECT_MSGS") {
		url = com_zimbra_socialTwitter.DM_URL;
	} else if (type == "MENTIONS") {
		url = com_zimbra_socialTwitter.MENTIONS_URL;
	} else if (type == "SENT_MSGS") {
		useSimpleHttpGet = true;
		url = [com_zimbra_socialTwitter.PROFILE_BASE_URL, params.account.screen_name, ".json"].join("");
	} else if (type == "PROFILE_MSGS") {
		useSimpleHttpGet = true;
		url = [com_zimbra_socialTwitter.PROFILE_BASE_URL, params.screen_name, ".json"].join("");
	}
	if(useSimpleHttpGet) {
		this.zimlet.socialOAuth.makeSimpleHTTPGet({url: url, components: components, callback: callback});
	} else {
		this.zimlet.socialOAuth.makeHTTPGet({url: url, components: components, callback: callback});
	}
};

com_zimbra_socialTwitter.prototype._twitterItemsHandler =
function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("twitterError"), response);
	if (jsonObj.results) {
		jsonObj = jsonObj.results;
	}
	this.zimlet.createCardView({tableId:params.tableId, items:jsonObj, type:params.type, additionalParams:params});
};

com_zimbra_socialTwitter.prototype._checkIfFollowing = function(params) {
	var components = new Array();
	components["source_screen_name"] = params.account.name;
	components["target_screen_name"] = params.profileAccnt.screen_name;
	var callback = new AjxCallback(this, this._checkIfFollowingCallback, params);
	this.zimlet.socialOAuth.makeSimpleHTTPGet({url: com_zimbra_socialTwitter.FRIENDSHIP_BASE_URL, components: components, callback: callback});
};

com_zimbra_socialTwitter.prototype._checkIfFollowingCallback = function(params, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	params["following"] = false;
	if(jsonObj.relationship && jsonObj.relationship.source && jsonObj.relationship.source.following) {
		params["following"] = true;
	}
	this._addFollowUnFollowBtn(params);
};

com_zimbra_socialTwitter.prototype._addFollowUnFollowBtn = function(params) {
	var btn = new DwtButton({parent:this.zimlet.getShell()});
	if (params.following) {
		btn.setText(this.zimlet.getMessage("unFollow"));
		btn.setImage("social_unFollowIcon");
		btn.addSelectionListener(new AjxListener(this, this._twitterFollowMe, params));
		document.getElementById(params.id).appendChild(btn.getHtmlElement());
	} else {
		btn.setText(this.zimlet.getMessage("followMeOnTwitter"));
		btn.setImage("social_twitterIcon");
		btn.addSelectionListener(new AjxListener(this, this._twitterFollowMe, params));
		document.getElementById(params.id).appendChild(btn.getHtmlElement());
	}
};

com_zimbra_socialTwitter.prototype._twitterFollowMe =
function(origParams) {
	var profileAccnt = origParams.profileAccnt;
	var profileId = profileAccnt.id ? profileAccnt.id : profileAccnt.from_user_id;
	var createOrDestroy = "create";
	if (origParams.following)
		createOrDestroy = "destroy";

	var callback = new AjxCallback(this, this._twitterFollowMeCallback, origParams);
	var url = ["https://twitter.com/friendships/", createOrDestroy, "/", profileId, ".json"].join("");
	var params = {account:origParams.account, url:url, contentType: "application/x-www-form-urlencoded", callback:callback}
	this._makeHTTPPost(params);
};

com_zimbra_socialTwitter.prototype._makeHTTPPost =
function(params) {
	var account = params.account;
	this.zimlet.socialOAuth.setAuthTokens({oauth_token: account.oauth_token, oauth_token_secret: account.oauth_token_secret});
	this.zimlet.socialOAuth.makeHTTPPost(params);
};

com_zimbra_socialTwitter.prototype._twitterFollowMeCallback =
function(origParams, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	if (jsonObj.error != undefined) {
		var msgDialog = appCtxt.getMsgDialog();
		var msg = jsonObj.error;
		msgDialog.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
	setTimeout(AjxCallback.simpleClosure(this._setUserProfileView, this, origParams.tableId, origParams.profileAccnt), 4000);//refresh table after 4 secs
};

com_zimbra_socialTwitter.prototype.postToTwitter =
function(account, message) {
	var url = "";
	if (account.__on != "true") {
		return;
	}
	var msgParts = this._getPostMsgParts();
	var components = [];
	var postBody = "";
	if (msgParts.isDM) {
		components["screen_name"] = msgParts.screen_name;
		components["text"] = msgParts.data;
		url = com_zimbra_socialTwitter.DM_URL_POST;
	} else {
		url = com_zimbra_socialTwitter.UPDATE_URL;
		components["status"] = msgParts.data;
	}
	var callback = new AjxCallback(this, this._postToTweetCallback, account);
	var params = {account:account, url:url, postBody:postBody, components:components, contentType: "application/x-www-form-urlencoded", callback:callback};
	this._makeHTTPPost(params);
};

com_zimbra_socialTwitter.prototype.getUpdateUrl =
function(account) {
	var data = this.zimlet.updateField.value;
	var additionalParams = new Array();
	additionalParams["status"] = data;
	params = {account:account, actionUrl:"http://twitter.com/statuses/update.json", http:"POST", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};

com_zimbra_socialTwitter.prototype.deletePost =
function(origParams) {
	var url = [com_zimbra_socialTwitter.DELETE_POST_BASE_URL, origParams.postId, ".json"].join("");
	var callback = new AjxCallback(this, this._deletePostCallback, origParams);
	var params = {account:origParams.account, url:url, postBody:"", components:[], contentType: "application/x-www-form-urlencoded", callback:callback};
	this._makeHTTPPost(params);
};

com_zimbra_socialTwitter.prototype._deletePostCallback =
function(origParams, response) {
	if (!response.success) {
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("twitterError") + response.status, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var params = {tableId: origParams.tableId, account: origParams.account, type: origParams.type};
	setTimeout(AjxCallback.simpleClosure(this.getTwitterFeeds, this, params), 3000);//refresh table after 3 secs
};

com_zimbra_socialTwitter.prototype._getPostMsgParts =
function() {
	var returnParams = {screen_name:"", isDM: false, text:""};
	var val = this.zimlet.updateField.value;
	if (val.toLowerCase().indexOf("d @") != 0) {
		returnParams["data"] = val;
		return returnParams;
	}

	var arry = val.split(" ");
	var toUser = arry[1].replace("@", "");
	var data = "";
	for (var i = 2; i < arry.length; i++) {
		if (data == "")
			data = arry[i];
		else
			data = data + " " + arry[i];
	}
	returnParams["isDM"] = true;
	returnParams["data"] = data;
	returnParams["screen_name"] = toUser;
	return returnParams;
};


com_zimbra_socialTwitter.prototype._encodeNormalizedStr =
function(normalizedStr, ignoreEncodingArray) {
	if (!ignoreEncodingArray)
		ignoreEncodingArray = new Array();
	var encodStr = "";
	var tmp1 = normalizedStr.split("&");
	for (var i = 0; i < tmp1.length; i++) {
		var tmp2 = tmp1[i].split("=");
		var name = tmp2[0];
		var value = tmp2[1];
		var ignoreEncoding = false;
		for (var j = 0; j < ignoreEncodingArray.length; j++) {
			if (ignoreEncodingArray[j] == name) {
				ignoreEncoding = true;
				break;
			}
		}

		if (encodStr == "")
			encodStr = AjxStringUtil.urlComponentEncode(name) + "=" + (ignoreEncoding ? value : AjxStringUtil.urlComponentEncode(value));
		else
			encodStr = encodStr + "&" + AjxStringUtil.urlComponentEncode(name) + "=" + (ignoreEncoding ? value : AjxStringUtil.urlComponentEncode(value));

	}
	return encodStr;
};

com_zimbra_socialTwitter.prototype._postToTweetCallback =
function(account, response) {
	if (!response.success) {
		var text = response.text;
		try{
			jsonObj = eval("(" + text + ")");
		} catch (e) {
			jsonObj = {error: this.zimlet.getMessage("twitterError")}
		}
		if (jsonObj.error != undefined) {
			var msgDialog = appCtxt.getMsgDialog();
			var msg = jsonObj.error;
			msgDialog.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
			msgDialog.popup();
		}
		return;
	}
	var jsonObj = eval("(" + response.text + ")");
	if (this.zimlet.updateField) {
		this.zimlet.updateField.value = "";
		this.zimlet.showNumberOfLetters();
	}
	appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("updatesSent"), ZmStatusView.LEVEL_INFO);
	var tableId = this.zimlet._getTableIdFromAccount(account);
	if (tableId) {
		setTimeout(AjxCallback.simpleClosure(this.getTwitterFeeds, this, {tableId: tableId, account: account, type:"ACCOUNT"}), 3000);//refresh table after 3 secs
	}
};

com_zimbra_socialTwitter.prototype.handleTwitterCallback =
function(params, response) {
	var _p = "";
	var unReadCount = 0;
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("twitterError"), response);
	if (jsonObj.results) {
		jsonObj = jsonObj.results;
	}
	if (jsonObj.length == 0) {
		return;
	}
	var accnt = params.account;
	var type = params.type;
	var items = new Array();
	var user = null;
	for (var k = 0; k < jsonObj.length; k++) {
		var obj = jsonObj[k];
		if (k == 0) {
			if (type == "ACCOUNT") {
				_p = accnt._p ? accnt._p : "";
			} else if (type == "MENTIONS") {
				_p = accnt._m ? accnt._m : "";
			} else if (type == "DIRECT_MSGS") {
				_p = accnt._d ? accnt._d : "";
			} else if (type == "SENT_MSGS") {
				_p = accnt._s ? accnt._s : "";
			}
		}

		if (obj.id > _p || _p == "") {
			unReadCount++;
			if (unReadCount < 5) {
				user = obj.user ? obj.user : obj.sender;
				items.push({text:user.screen_name + ": " + obj.text, profile_image_url: user.profile_image_url});
			}
		}
	}
	if (params.action == "SEND_EMAIL") {
		if (!this.emailContentObj[accnt.name]) {
			this.emailContentObj[accnt.name] = {};
		}
		this.emailContentObj[accnt.name][type] = {unReadCount:unReadCount, items:items};
		if (params.callback) {
			params.callback.run(this);
		} else {
			this._addMessage(this._constructMime(this.emailContentObj));
			this.emailContentObj = {};
		}
	} else if (params.action == "SHOW_ALERT") {
		if (!this.showAlertObj[accnt.name]) {
			this.showAlertObj[accnt.name] = {};
		}
		this.showAlertObj[accnt.name][type] = {unReadCount:unReadCount, items:items};
		if (params.callback) {
			params.callback.run(this);
		} else {
			this._showAlert(this.showAlertObj);
			this.showAlertObj[accnt.name] = {};
		}
	}
};

com_zimbra_socialTwitter.prototype._showAlert =
function(showAlertObj) {
	var html = new Array();
	var i = 0;

	var totalUnreadCount = 0;
	var accntsLength = 0;
	for (var accntName in showAlertObj) {
		var accnt = showAlertObj[accntName];
		var firstItem = accnt["ACCOUNT"].items[0];
		if (!firstItem) {
			continue;
		}
		var accUC = accnt["ACCOUNT"] ? accnt["ACCOUNT"].unReadCount : 0;
		var dmUC = accnt["DIRECT_MSGS"] ? accnt["DIRECT_MSGS"].unReadCount : 0;
		var mUC = accnt["MENTIONS"] ? accnt["MENTIONS"].unReadCount : 0;

		totalUnreadCount = totalUnreadCount + accUC + dmUC + mUC;
		html[i++] = "<TABLE width=500px>";
		html[i++] = "<TR><TD>";
		html[i++] = "<label style='font-weight:bold;font-size:12px;color:darkblue'>" + accntName + ": </label>";
		html[i++] = "<label style='font-weight:bold;font-size:12px'>";
		html[i++] = ["Account: ",accUC, " DM: ",dmUC, " Mentions: ",mUC].join("");
		html[i++] = "</label></TD></TR></TABLE>";
		html[i++] = "<TABLE width=500px>";
		html[i++] = "<TD width=48px height=48px align='center' valign='top'> ";
		html[i++] = "<div style='background;white'>";
		html[i++] = "<img height=\"48\" width=\"48\" src=\"" + firstItem.profile_image_url + "\" />";
		html[i++] = "</div>";
		html[i++] = "</td>";
		html[i++] = "<TD> ";
		html[i++] = firstItem.text;
		html[i++] = "</td>";
		html[i++] = "</tr>";
		html[i++] = "</TABLE>";
		accntsLength++;
	}
	var hdr = new Array();
	var j = 0;
	hdr[j++] = "<TABLE width=500px>";
	hdr[j++] = "<TR><TD align=left>";
	hdr[j++] = "<label style='font-weight:bold;font-size:13px;color:blue';font-family:'Lucida Grande',sans-serif;>";
	hdr[j++] = AjxMessageFormat.format(this.zimlet.getMessage("youHaveXUnreadTweets"), totalUnreadCount);
	hdr[j++] = "</label>";
	hdr[j++] = "</td>";
	hdr[j++] = "</tr>";
	hdr[j++] = "</TABLE>";

	if (totalUnreadCount == 0)
		return;

	var transitions = [];
	transitions.push(ZmToast.FADE_IN);
	for (var i = 0; i < accntsLength; i++) {
		transitions.push(ZmToast.PAUSE);
	}
	transitions.push(ZmToast.FADE_OUT);
	appCtxt.getAppController().setStatusMsg(hdr.join("") + html.join(""), ZmStatusView.LEVEL_INFO, null, transitions);
};

com_zimbra_socialTwitter.prototype._constructMime =
function(emailContentObj) {
	var html = new Array();
	var i = 0;
	html[i++] = "Return-Path: social@example.zimbra.com\n";
	html[i++] = "Received: from localhost (LHLO rr.zimbra.com) (127.0.0.1) by rr.zimbra.com\n";
	html[i++] = "with LMTP; " + (new Date()).toString() + "\n";
	html[i++] = "Received: by mail02.prod.aol.net (1.38.193.5/16.2) id AA10153;\n";
	html[i++] = (new Date()).toString() + "\n";
	html[i++] = "From: " + this.zimlet.getMessage("zimbraSocial") + " <social@zimbra.zimlet.com>\n";
	html[i++] = "Original-Sender: Zimbra social <social@zimbra.zimlet.com>\n";
	html[i++] = "To: " + appCtxt.getActiveAccount().name + "\n";
	html[i++] = "Date: " + (new Date()).toString() + "\n";
	var subject = new Array();
	var j = 0;
	var totalUnreadCount = 0;
	var summary = new Array();
	for (var accntName in emailContentObj) {
		var accnt = emailContentObj[accntName];
		var accUC = accnt.ACCOUNT ? accnt.ACCOUNT.unReadCount : 0;
		var dmUC = accnt.DIRECT_MSGS ? accnt.DIRECT_MSGS.unReadCount : 0;
		var mUC = accnt.MENTIONS ? accnt.MENTIONS.unReadCount : 0;

		totalUnreadCount = totalUnreadCount + accUC + dmUC + mUC;
		summary[j++] = "----------------------------\n";
		summary[j++] = this.zimlet.getMessage("account") + " " + accntName + "\n";
		summary[j++] = "----------------------------\n";
		summary[j++] = this.zimlet.getMessage("messages") + " " + accUC + "\n";
		summary[j++] = this.zimlet.getMessage("directMessages") + " " + dmUC + "\n";
		summary[j++] = this.zimlet.getMessage("mentions") + mUC + "\n";
		summary[j++] = "\n\n";
	}
	html[i++] = AjxMessageFormat.format(this.zimlet.getMessage("tweetSubject"), totalUnreadCount);
	var body = new Array();
	var m = 0;
	for (var accntName in emailContentObj) {
		var accnt = emailContentObj[accntName];
		var props = ["ACCOUNT", "DIRECT_MSGS", "MENTIONS"];
		for (var j = 0; j < props.length; j++) {
			var prop = props[j];
			var propStr = "";
			if(!accnt[prop]) {
				continue;
			}
			if (prop == "ACCOUNT")
				propStr = accntName + ": " + this.zimlet.getMessage("newMsgs") + "(" + accnt[prop].unReadCount + ")";
			else
				propStr = accntName + ": " + prop + "(" + accnt[prop].unReadCount + ")";
			body[m++] = "----------------------------------------------------\n";
			body[m++] = propStr;
			body[m++] = "\n----------------------------------------------------\n\n";

			var items = accnt[prop].items;
			for (var k = 0; k < items.length; k++) {
				body[m++] = items[k].text;
				body[m++] = "\n\n";
			}
		}
	}
	html[i++] = summary.join("") + body.join("");
	return html.join("");

};

com_zimbra_socialTwitter.prototype._addMessage =
function(mime) {
	var soapDoc = AjxSoapDoc.create("AddMsgRequest", "urn:zimbraMail");
	var m = soapDoc.set("m");
	m.setAttribute("l", "2");
	soapDoc.set("content", mime, m, "urn:zimbraMail");
	var callback = new AjxCallback(this, this._handleAddMessage);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:callback});
};

com_zimbra_socialTwitter.prototype._handleAddMessage =
function(response) {

};

com_zimbra_socialTwitter.prototype._getAdditionalParams =
function(tableId) {
	var additionalParams = new Array();
	additionalParams["count"] = this.preferences.social_pref_numberofTweetsToReturn;
	this._addSinceAndMaxIds(tableId, additionalParams);
	return additionalParams;
};

com_zimbra_socialTwitter.prototype._addSinceAndMaxIds =
function(tableId, additionalParams) {

	var id;
	var idName;
	var refreshType = this.zimlet.tableIdAndRefreshType[tableId];
	if (refreshType == "OLDER") {
		id = this.zimlet._tableIdAndBottomPostIdMap[tableId];
		if (!id) {//when the first page has no results..
			id = this.zimlet.tableIdAndMarkAsReadId[tableId];
		}
		idName = "max_id";
	} else if (refreshType == "NEWER") {
		id = this.zimlet.tableIdAndTopPostIdMap[tableId];
		idName = "max_id";
	} else {
		return additionalParams;
	}
	if (!id || id == "undefined") {
		return additionalParams;
	} else {
		return additionalParams[idName] = id;
	}
};

com_zimbra_socialTwitter.prototype.performOAuth =
function() {
	var oauthResultCallback = new AjxCallback(this, this._handleOAuthResult);
	var params = {requestTokenUrl: "https://twitter.com/oauth/request_token",
		authorizeBaseUrl: "https://twitter.com/oauth/authorize?oauth_token=",
		accessTokenUrl: "https://twitter.com/oauth/access_token"};

	this.zimlet.socialOAuth.setAppName("Twitter");
	this.zimlet.socialOAuth.oauthResultCallback = oauthResultCallback;
	this.zimlet.socialOAuth.setGoToButtonDetails(this.zimlet.getMessage("goToTwitter"), "social_twitterIcon");
	this.zimlet.socialOAuth.setOAuthUrls(params);
	this.zimlet.socialOAuth.showOAuthDialog("Twitter");
};

com_zimbra_socialTwitter.prototype._handleOAuthResult =
function(result) {
	if (!result.success) {
		var errorDialog = appCtxt.getErrorDialog();
		errorDialog.reset();
		var msg = result.httpResponse && result.httpResponse.status ? "HTTP ERROR "+ result.httpResponse.status : this.zimlet.getMessage("unknownError");
		var detailStr = result.httpResponse && result.httpResponse.text ? result.httpResponse.text : this.zimlet.getMessage("unknownError");
		errorDialog.setMessage(msg, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZmMsg.zimbraTitle);
		errorDialog.popup();
		return;
	}

	var oauthTokens = result.oauthTokens;
	this.oauth_token = oauthTokens["oauth_token"];
	this.oauth_token_secret = oauthTokens["oauth_token_secret"];
	this.oauth_screen_name = oauthTokens["screen_name"];
	this._twitterAccessTokenCallbackHandler(result.httpResponse);
};

com_zimbra_socialTwitter.prototype._twitterAccessTokenCallbackHandler =
function(response) {
	this.manageTwitterAccounts(response.text);
	this.zimlet.preferences._updateAccountsTable();
};

com_zimbra_socialTwitter.prototype.manageTwitterAccounts = function(text) {
	var nv = text.split("&");
	var tObj = {};
	for (var i = 0; i < nv.length; i++) {
		var tmp = nv[i].split("=");
		tObj[tmp[0]] = tmp[1];
	}
	if (tObj["__type"] == undefined) {
		tObj["__type"] = "twitter";
	}
	if (tObj["__on"] == undefined) {
		tObj["__on"] = "true";
	}
	if (tObj["__s"] == undefined) {//__s means shown & 1 means true
		tObj["__s"] = "1";
	}
	if (tObj["_p"] == undefined) {
		tObj["_p"] = "";
	}
	if (tObj["__pos"] == undefined) {
		tObj["__pos"] = "";
	}
	//to normalize names with fb
	tObj.raw = text;
	tObj.name = tObj.screen_name;
	tObj.type = tObj["__type"];
	if (tObj.name == undefined) {//pin is invalid or something else is wrong
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("twitterPinInvalid"), ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	} else {
		this.zimlet.allAccounts[tObj.user_id + tObj.screen_name] = tObj;
	}
};

com_zimbra_socialTwitter.prototype.getAllSearchesAsString = function() {
	var str = "";
	for (var i = 0; i < this.allSearches.length; i++) {
		var search = this.allSearches[i];
		if (str == "") {
			str = search;
		} else {
			str = str + "::" + search;
		}
	}
	return str;
};

com_zimbra_socialTwitter.prototype.loadAllSearchesFromDB = function() {
	var allSearches = this.zimlet.getUserProperty("social_AllTwitterSearches");
	if (allSearches == "" || allSearches == undefined) {
		return;
	}
	this.allSearches = eval("(" + allSearches + ")");
};

com_zimbra_socialTwitter.prototype._updateAllSearches =
function(searchName, action, _p) {
	if (_p == undefined)
		_p = "";

	var needToUpdate = false;
	var hasSearches = false;
	var newAllSearches = new Array();

	for (var i = 0; i < this.allSearches.length; i++) {
		hasSearches = true;
		var origSearch = this.allSearches[i];
		var currSearchName = origSearch.name;
		if (currSearchName == searchName && action != "delete") {
			origSearch.axn = action;
			newAllSearches.push(origSearch);
			needToUpdate = true;
		} else if (currSearchName != searchName) {
			newAllSearches.push(origSearch);
		} else {//deleting the item
			needToUpdate = true;
		}
	}

	if (needToUpdate && hasSearches) {
		this.allSearches = newAllSearches;
		this.zimlet.setUserProperty("social_AllTwitterSearches", this.getAllSearchesAsJSON(), true);
		this.zimlet._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:false, updateAccntCheckboxes:false, searchCards:false});
	}
};

com_zimbra_socialTwitter.prototype.updateSearchPositions =
function() {

	if (needToUpdate && hasSearches) {
		this.allSearches = newAllSearches;
		this.zimlet.setUserProperty("social_AllTwitterSearches", this.getAllSearchesAsJSON(), true);
		this.zimlet._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:false, updateAccntCheckboxes:false, searchCards:false});
	}
};

com_zimbra_socialTwitter.prototype.getAllSearchesAsJSON =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "[";
	for (var j = 0; j < this.allSearches.length; j++) {
		var obj = this.allSearches[j];
		if (!obj.__pos || obj.__pos == "undefined")
			obj.__pos = "";

		html[i++] = "{";
		html[i++] = "name:";
		html[i++] = "\"" + obj.name + "\"";
		html[i++] = ",axn:";
		html[i++] = "\"" + obj.axn + "\"";
		html[i++] = ",__pos:";
		html[i++] = "\"" + obj.__pos + "\"";
		html[i++] = ",_p:";
		html[i++] = "\"" + obj._p + "\"";
		html[i++] = "}";
		if (j != this.allSearches.length - 1) {
			html[i++] = ",";
		}
	}
	html[i++] = "]";
	return html.join("");
};

com_zimbra_socialTwitter.prototype.getProfileMessages =
function(params) {
	this.getMessages(params.tableId, params.account, params.callback, this.getProfileMessagesUrl(params.tableId, params.account, params.screen_name), "ACCOUNT");
};

com_zimbra_socialTwitter.prototype.showUserProfile =
function(screen_name, tweetTableId) {
	var tableId = this.zimlet._showCard({headerName:screen_name, type:"PROFILE", tweetTableId:tweetTableId, autoScroll:true});
	var actionUrl = "https://twitter.com/users/show/" + screen_name + ".json";
	var entireurl = ZmZimletBase.PROXY + actionUrl;
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this.showUserProfileHandler, tableId), true);
};

com_zimbra_socialTwitter.prototype.showUserProfileHandler =
function(tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this._setUserProfileView(tableId, jsonObj);
};

com_zimbra_socialTwitter.prototype._setUserProfileView =
function(tableId, profileAccnt) {
	var html = [];
	var i = 0;
	var followMeDivIdAndAccountsMap = new Array();
	for (var id in this.zimlet.allAccounts) {
		var account = this.zimlet.allAccounts[id];
		if (account.type == "twitter") {
			followMeDivIdAndAccountsMap.push({profileAccnt:profileAccnt, account:account, tableId:tableId, id:"social_followmebutton_" + Dwt.getNextId()});
		}
	}

	html[i++] = "<DIV  class='social_profileInnerDiv'>";
	html[i++] = "<DIV><img src=\"" + profileAccnt.profile_image_url + "\" /></DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<TABLE width=100%>";
	html[i++] = "<TR><TD colspan=2>" + (profileAccnt.description == null ? "" : profileAccnt.description) + "</TD></TR>";
	html[i++] = "<TR><TD width=25%>"+this.zimlet.getMessage("followers") +"</TD><TD>" + (profileAccnt.followers_count == null ? "" : profileAccnt.followers_count) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("friends") +"</TD><TD>" + (profileAccnt.friends_count == null ? "" : profileAccnt.friends_count) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("updates") +"</TD><TD>" + (profileAccnt.statuses_count == null ? "" : profileAccnt.statuses_count) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("name") +"</TD><TD>" + (profileAccnt.name == null ? "" : profileAccnt.name) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("location") +"</TD><TD>" + (profileAccnt.location == null ? "" : profileAccnt.location) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("timezone") +"</TD><TD>" + (profileAccnt.time_zone == null ? "" : profileAccnt.time_zone) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("favorites") +"</TD><TD>" + (profileAccnt.favourites_count == null ? "" : profileAccnt.favourites_count) + "</TD></TR>";
	html[i++] = "<TR><TD  width=25%>"+this.zimlet.getMessage("twitterPage") +"</TD><TD><a href='http://twitter.com/" + profileAccnt.screen_name + "' target='_blank' >" + "http://twitter.com/" + profileAccnt.screen_name + "</a></TD></TR>";

	for (var j = 0; j < followMeDivIdAndAccountsMap.length; j++) {
		var obj = followMeDivIdAndAccountsMap[j];
		html[i++] = "<TR><td>" + obj.account.name + "</td><TD id='" + obj.id + "'></TD></TR>";
	}
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "</DIV>";

	var msgsTableId = tableId + "__" + Dwt.getNextId();
	html[i++] = "<div id='" + msgsTableId + "' width=100%></div>";
	document.getElementById(tableId).style.backgroundImage = "url('" + profileAccnt.profile_background_image_url + "')";
	document.getElementById(tableId).innerHTML = html.join("");

	for (var j = 0; j < followMeDivIdAndAccountsMap.length; j++) {
		var params = followMeDivIdAndAccountsMap[j];
		this._checkIfFollowing(params);
	}
	this.zimlet.tableIdAndAccountMap[msgsTableId] = account;
	//this.getProfileMessages({tableId: msgsTableId, account: account, screen_name: profileAccnt.screen_name});
	this.getTwitterFeeds({tableId: msgsTableId, account: account, screen_name: profileAccnt.screen_name, type:"PROFILE_MSGS"});
};

com_zimbra_socialTwitter.prototype.scanForUpdates =
function(action) {
	var account = "";
	var accountList = new Array();
	for (var id in this.zimlet.allAccounts) {
		account = this.zimlet.allAccounts[id];
		if (account.type == "twitter") {
			accountList.push(account);
		}
	}
	if (accountList.length == 1) {
		account = accountList[0];
		var callback4 = new AjxCallback(this, this.handleTwitterCallback, {account:account, type:"ACCOUNT", action:action});
		var callback3 = new AjxCallback(this, this.getTwitterFeeds, {account:account, type:"ACCOUNT", callback:callback4});
		var callback2 = new AjxCallback(this, this.handleTwitterCallback, {account:account, type:"DIRECT_MSGS", callback: callback3, action:action});
		var callback1 = new AjxCallback(this, this.getTwitterFeeds, {account:account, type:"DIRECT_MSGS", callback:callback2});
		var callback0 = new AjxCallback(this, this.handleTwitterCallback, {account:account, type:"MENTIONS", callback:callback1, action:action});
		this.getTwitterFeeds({account:account, type:"MENTIONS", callback: callback0});
	} else if (accountList.length == 2) {
		var account1 = accountList[0];
		var account2 = accountList[1];
		var callback10 = new AjxCallback(this, this.handleTwitterCallback, {account:account2, type:"ACCOUNT", action:action});
		var callback9 = new AjxCallback(this, this.getTwitterFeeds, {account:account2, type:"ACCOUNT", callback:callback10});
		var callback8 = new AjxCallback(this, this.handleTwitterCallback, {account:account2, type:"DIRECT_MSGS", callback: callback9, action:action});
		var callback7 = new AjxCallback(this, this.getTwitterFeeds, {account:account2, type:"DIRECT_MSGS", callback:callback8});
		var callback6 = new AjxCallback(this, this.handleTwitterCallback, {account:account2, type:"MENTIONS", callback:callback7, action:action});
		var callback5 = new AjxCallback(this, this.getTwitterFeeds, {account:account2, type:"MENTIONS", callback:callback6});

		var callback4 = new AjxCallback(this, this.handleTwitterCallback, {account:account1, type:"ACCOUNT", callback:callback5, action:action});
		var callback3 = new AjxCallback(this, this.getTwitterFeeds, {account:account1, type:"ACCOUNT", callback:callback4});
		var callback2 = new AjxCallback(this, this.handleTwitterCallback, {account:account1, type:"DIRECT_MSGS", callback: callback3, action:action});
		var callback1 = new AjxCallback(this, this.getTwitterFeeds, {account:account1, type:"DIRECT_MSGS", callback:callback2});
		var callback0 = new AjxCallback(this, this.handleTwitterCallback, {account:account1, type:"MENTIONS", callback:callback1, action:action});
		this.getTwitterFeeds({account:account1, type:"MENTIONS", callback: callback0});
	}
};