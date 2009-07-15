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

function com_zimbra_tweetziTwitter(zimlet) {
	this.zimlet = zimlet;
	this.allSearches = new Array();
	this.allTrends = new Array();
	this.loadAllSearchesFromDB();
	this.consumerKey = this.zimlet.getUserProperty("tweetzi_twitter_consumer_key");
	this.consumerSecret = this.zimlet.getUserProperty("tweetzi_twitter_consumer_secret");

}

com_zimbra_tweetziTwitter.prototype.getTwitterTrends =
function() {
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("http://search.twitter.com/trends.json");
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._trendsCallback), true);
};

com_zimbra_tweetziTwitter.prototype._trendsCallback =
function(response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	var trends = jsonObj.trends;
	if (!response.success) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Twitter Error: " + response.text, ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}

	this.allTrends = new Array();
	for (var i = 0; i < trends.length; i++) {
		this.allTrends[trends[i].name] = true;
	}
	for (var i = 0; i < 2; i++) {
		var name = trends[i].name;
		var tableId = this.zimlet._showCard({headerName:name, type:"TREND", autoScroll:false});
		var sParams = {query:name, tableId:tableId, type:"TREND"};
		this.twitterSearch(sParams);
		var timer = setInterval(AjxCallback.simpleClosure(this.twitterSearch, this, sParams), 400000);
		this.zimlet.tableIdAndTimerMap[tableId] = timer;
	}
	this.zimlet._updateAllWidgetItems({updateTrendsTree:true});

};



com_zimbra_tweetziTwitter.prototype.twitterSearch =
function(params) {
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("http://search.twitter.com/search.json?q=" + AjxStringUtil.urlComponentEncode(params.query));
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._twitterSearchCallback, params), true);
};

com_zimbra_tweetziTwitter.prototype._twitterSearchCallback =
function(params, response) {
	var text = response.text;
	if (!response.success) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Twitter Error: " + text, ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}
	var jsonObj = eval("(" + text + ")");
	this.zimlet.createCardView(params.tableId, jsonObj.results, params.type);
};

com_zimbra_tweetziTwitter.prototype.getFriendsTimeLine =
function(tableId, account) {
	var url = this.getFriendsTimelineUrl(account);
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this.friendsTimelineCallback, tableId), true);
};
com_zimbra_tweetziTwitter.prototype.getMentions =
function(tableId, account) {
	var url = this.getMentionsUrl(account);
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this.mentionsCallback, tableId), true);
};
com_zimbra_tweetziTwitter.prototype.getDirectMessages =
function(tableId, account) {
	var url = this.getDMSentUrl(account);
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this.getDirectMessagesCallback, tableId), true);
};

com_zimbra_tweetziTwitter.prototype.showUserProfile =
function(screen_name, tweetTableId) {
	var tableId = this.zimlet._showCard({headerName:screen_name, type:"PROFILE", tweetTableId:tweetTableId, autoScroll:true});
	var actionUrl = "https://twitter.com/users/show/" + screen_name + ".json";
	var entireurl = ZmZimletBase.PROXY + actionUrl;
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this.showUserProfileHandler, tableId), true);

};

com_zimbra_tweetziTwitter.prototype.showUserProfileHandler =
function(tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this._setUserProfileView(tableId, jsonObj);
};



com_zimbra_tweetziTwitter.prototype._setUserProfileView =
function(tableId, profileAccnt) {
	var html = [];
	var i = 0;
	var followMeDivIdAndAccountsMap = new Array();
	for(var id in this.zimlet.allAccounts) {
		var account =  this.zimlet.allAccounts[id];
		if(account.type == "twitter") {
			followMeDivIdAndAccountsMap.push({profileAccnt:profileAccnt, account:account, tableId:tableId, id:"tweetzi_followmebutton_"+Dwt.getNextId()});
		}
	}
	html[i++] = "<DIV  class='tweetzi_profileInnerDiv'>";
	html[i++] = "<DIV><img src=\"" + profileAccnt.profile_image_url + "\" /></DIV>";
	html[i++] = "<DIV >";
	html[i++] = "<TABLE width=100%>";
	html[i++] = "<TR><TD colspan=2>" + (profileAccnt.description == null ? "" :  profileAccnt.description) + "</TD></TR>";
	html[i++] = "<TR><TD width=50%>followers:</TD><TD>" + (profileAccnt.followers_count == null ? "" :  profileAccnt.followers_count) + "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>friends:</TD><TD>"  + (profileAccnt.friends_count == null ? "" :  profileAccnt.friends_count) +  "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>updates:</TD><TD>" + (profileAccnt.statuses_count == null ? "" :  profileAccnt.statuses_count) +  "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>name:</TD><TD>" + (profileAccnt.name == null ? "" :  profileAccnt.name) +   "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>location:</TD><TD>"  + (profileAccnt.location == null ? "" :  profileAccnt.location) +  "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>timezone:</TD><TD>"  + (profileAccnt.time_zone == null ? "" :  profileAccnt.time_zone) + "</TD></TR>";
	html[i++] = "<TR><TD  width=50%>favourites:</TD><TD>"  + (profileAccnt.favourites_count == null ? "" :  profileAccnt.favourites_count) +   "</TD></TR>";
	for(var j=0; j< followMeDivIdAndAccountsMap.length; j++) {
		var obj =  followMeDivIdAndAccountsMap[j];
		html[i++] = "<TR><td>"+ obj.account.name+"</td><TD id='"+obj.id +"'></TD></TR>";
	}
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	document.getElementById(tableId).style.backgroundImage = "url('" + profileAccnt.profile_background_image_url + "')";
	document.getElementById(tableId).innerHTML = html.join("");

	for(var j=0; j< followMeDivIdAndAccountsMap.length; j++) {
		var params =  followMeDivIdAndAccountsMap[j];
		this._checkIfFollowing(params);
	}
};

com_zimbra_tweetziTwitter.prototype._checkIfFollowing = function(params) {
	//https://twitter.com/friendships/show.xml?source_screen_name=rajaraodv&target_screen_name=techcrunch
		var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("https://twitter.com/friendships/show.json?source_screen_name="+params.account.name+"&target_screen_name="+params.profileAccnt.screen_name);
		AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._checkIfFollowingCallback, params), true);
};

com_zimbra_tweetziTwitter.prototype._checkIfFollowingCallback = function(params, response) {
	var text = response.text;
	var jsonObj = eval("("+ text + ")");
	params["following"] = jsonObj.relationship.source.following;
	this._addFollowUnFollowBtn(params);
};

com_zimbra_tweetziTwitter.prototype._addFollowUnFollowBtn = function(params) {
	var btn = new DwtButton({parent:this.zimlet.getShell()});
	if(params.following) {
		btn.setText("Unfollow");
		btn.setImage("tweetzi_unFollowIcon");
		btn.addSelectionListener(new AjxListener(this, this._twitterFollowMe, params));
		document.getElementById(params.id).appendChild(btn.getHtmlElement());
	} else {
		btn.setText("follow me on twitter");
		btn.setImage("tweetzi_twitterIcon");
		btn.addSelectionListener(new AjxListener(this, this._twitterFollowMe, params));
		document.getElementById(params.id).appendChild(btn.getHtmlElement());
	}
};


com_zimbra_tweetziTwitter.prototype._twitterFollowMe =
function(origParams) {
	var profileAccnt = origParams.profileAccnt;
	var profileId = profileAccnt.id ? profileAccnt.id : profileAccnt.from_user_id;
	var createOrDestroy = "create";
	if(origParams.following)
		createOrDestroy = "destroy";

	var actionUrl = "https://twitter.com/friendships/"+createOrDestroy+"/" + profileId + ".json";
	var params = this.getFollowUserParams(actionUrl, origParams.profileId, origParams.account);
	var hdrs = new Array();
	hdrs["Content-type"] = "application/x-www-form-urlencoded";
	hdrs["Content-length"] = params.length;
	hdrs["Connection"] = "close";
	var entireurl = ZmZimletBase.PROXY + actionUrl;
	AjxRpc.invoke(params, entireurl, hdrs, new AjxCallback(this, this._twitterFollowMeCallback, origParams), false);
};


com_zimbra_tweetziTwitter.prototype._twitterFollowMeCallback =
function(origParams, response) {
	var text = response.text;
	var jsonObj = eval("("+text+ ")");
	if(jsonObj.error != undefined) {
		var msgDialog = appCtxt.getMsgDialog();
		var msg =  jsonObj.error;
		msgDialog.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
		msgDialog.popup();
	}
	setTimeout(AjxCallback.simpleClosure(this._setUserProfileView, this, origParams.tableId, origParams.profileAccnt), 4000);//refresh table after 4 secs
};

com_zimbra_tweetziTwitter.prototype.getFollowUserParams =
function(actionUrl, profileId, account) {
	var ts = account.tokenSecret;
	var ot = account.oauth_token;
	var accessor = { consumerSecret: this.consumerSecret
		, tokenSecret   : ts};
	var message = { method: "POST"
		, action: actionUrl
		, parameters: new Array()
	};
	message.parameters.push(["oauth_consumer_key",this.consumerKey]);
	message.parameters.push(["oauth_version","1.0"]);
	message.parameters.push(["oauth_timestamp", OAuth.timestamp()]);
	message.parameters.push(["oauth_nonce", OAuth.nonce(11)]);
	message.parameters.push(["oauth_signature_method", "HMAC-SHA1"]);
	message.parameters.push(["oauth_token", ot]);

	OAuth.SignatureMethod.sign(message, accessor);
	var normalizedParams = OAuth.SignatureMethod.normalizeParameters(message.parameters);
	var signature = OAuth.getParameter(message.parameters, "oauth_signature");
	var signatureBaseString = OAuth.SignatureMethod.getBaseString(message);
	var authorizationHeader = OAuth.getAuthorizationHeader("", message.parameters);

	var val = this._encodeNormalizedStr(normalizedParams);
	val = val + "&" + AjxStringUtil.urlComponentEncode("oauth_signature") + "=" + AjxStringUtil.urlComponentEncode(signature);
	return val;
};


com_zimbra_tweetziTwitter.prototype.postToTwitter =
function(account, message) {
	var isDM = false;
	if(message.indexOf("DM @") == 0){//use this to make sure not to send message direct message to FB
		isDM = true;
	}
	if (account.__on == "true") {
		if(isDM) {
			data = this.getDMPostUrl(account);
			actionUrl = "https://twitter.com/direct_messages/new.json";
		} else {
			data = this.getTweetUrl(account);
			actionUrl = "https://twitter.com/statuses/update.json";
		}
		var hdrs = new Array();
		hdrs["Content-type"] = "application/x-www-form-urlencoded";
		hdrs["Content-length"] = data.length;
		hdrs["Connection"] = "close";
		var entireurl = ZmZimletBase.PROXY + actionUrl;
		AjxRpc.invoke(data, entireurl, hdrs, new AjxCallback(this, this._postToTweetCallback, account), false);
	}
};


com_zimbra_tweetziTwitter.prototype.getTweetUrl =
function(account) {
	var data = document.getElementById("tweetzi_statusTextArea").value;
	var additionalParams  = new Array();
	additionalParams["status"] = data;
	params = {account:account, actionUrl:"https://twitter.com/statuses/update.json", http:"POST", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};


com_zimbra_tweetziTwitter.prototype.getDMPostUrl =
function(account) {
	var val = document.getElementById("tweetzi_statusTextArea").value;
	var arry = val.split(" ");
	var toUser = arry[1].replace("@", "");
	var data = "";
	for(var i=2; i< arry.length; i++) {
		if(data == "")
			data = arry[i];
		else
			data = data + " " +arry[i];
	}
	var additionalParams  = new Array();
	additionalParams["text"] = data;
	additionalParams["screen_name"] = toUser;
	params = {account:account, actionUrl:"https://twitter.com/direct_messages/new.json", http:"POST", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};

com_zimbra_tweetziTwitter.prototype.getMentionsUrl =
function(account) {
	var additionalParams  = new Array();
	params = {account:account, actionUrl:"https://twitter.com/statuses/mentions.json", http:"GET", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};

com_zimbra_tweetziTwitter.prototype.getDMSentUrl =
function(account) {
	var additionalParams  = new Array();
	params = {account:account, actionUrl:"https://twitter.com/direct_messages.json", http:"GET", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};

com_zimbra_tweetziTwitter.prototype.getTwitterUrl =
function(params) {
	var account = params.account;
	var actionUrl =  params.actionUrl;
	var http = params.http;
	var additionalParams = params.additionalParams;
	if(additionalParams == undefined) {
		additionalParams = new Array();
	}

	var ts = account.tokenSecret;
	var ot = account.oauth_token;
	var accessor = { consumerSecret: this.consumerSecret
		, tokenSecret   : ts};
	var message = { method: http
		, action: actionUrl
		, parameters: new Array()
	};
	message.parameters.push(["oauth_consumer_key",this.consumerKey]);
	message.parameters.push(["oauth_version","1.0"]);
	message.parameters.push(["oauth_timestamp", OAuth.timestamp()]);
	message.parameters.push(["oauth_nonce", OAuth.nonce(11)]);
	message.parameters.push(["oauth_signature_method", "HMAC-SHA1"]);
	message.parameters.push(["oauth_token", ot]);

	OAuth.SignatureMethod.sign(message, accessor);
	var normalizedParams = OAuth.SignatureMethod.normalizeParameters(message.parameters);
	var signature = OAuth.getParameter(message.parameters, "oauth_signature");
	var signatureBaseString = OAuth.SignatureMethod.getBaseString(message);
	var authorizationHeader = OAuth.getAuthorizationHeader("", message.parameters);

	var val = this._encodeNormalizedStr(normalizedParams);
	additionalParams["oauth_signature"] = signature;
	var apArray = new Array();
	for(var name  in additionalParams) {
		apArray.push(AjxStringUtil.urlComponentEncode(name) + "=" + AjxStringUtil.urlComponentEncode(additionalParams[name]));
	}

	val = val + "&" + apArray.join("&");
	return actionUrl+"?"+val;
};

com_zimbra_tweetziTwitter.prototype._encodeNormalizedStr =
function(normalizedStr) {
	var encodStr = "";
	var tmp1 = normalizedStr.split("&");
	for (var i = 0; i < tmp1.length; i++) {
		var tmp2 = tmp1[i].split("=");
		if (encodStr == "")
			encodStr = AjxStringUtil.urlComponentEncode(tmp2[0]) + "=" + AjxStringUtil.urlComponentEncode(tmp2[1]);
		else
			encodStr = encodStr + "&" + AjxStringUtil.urlComponentEncode(tmp2[0]) + "=" + AjxStringUtil.urlComponentEncode(tmp2[1]);

	}
	return encodStr;
};

com_zimbra_tweetziTwitter.prototype._postToTweetCallback =
function(account, response) {
	var jsonObj = eval("("+response.text + ")");
	if (!response.success) {
		var msgDialog = appCtxt.getMsgDialog();
		var msg =  jsonObj.error;
		msgDialog.setMessage(msg, DwtMessageDialog.INFO_STYLE);
		msgDialog.popup();
		return;
	}
	document.getElementById("tweetzi_statusTextArea").value = "";
	this.zimlet.showNumberOfLetters();
	appCtxt.getAppController().setStatusMsg("Updates Sent", ZmStatusView.LEVEL_INFO);
	setTimeout(AjxCallback.simpleClosure(this._updateAccountStream, this, this.zimlet._getTableIdFromAccount(account), account), 3000);//refresh table after 3 secs
}

com_zimbra_tweetziTwitter.prototype.friendsTimelineCallback =
function(tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this.zimlet.createCardView(tableId, jsonObj, "ACCOUNT");
};
com_zimbra_tweetziTwitter.prototype.mentionsCallback =
function(tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this.zimlet.createCardView(tableId, jsonObj, "MENTIONS");
};
com_zimbra_tweetziTwitter.prototype.getDirectMessagesCallback =
function(tableId, response) {
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this.zimlet.createCardView(tableId, jsonObj, "DIRECT_MSGS");
};


com_zimbra_tweetziTwitter.prototype.getFriendsTimelineUrl =
function(account) {
	var additionalParams  = new Array();
	params = {account:account, actionUrl:"https://twitter.com/statuses/friends_timeline.json", http:"GET", additionalParams:additionalParams};
	return this.getTwitterUrl(params);
};


com_zimbra_tweetziTwitter.prototype.performOAuth =
function() {
	var url = this.getRequestTokenUrl();
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._twitterCallback), true);
};

com_zimbra_tweetziTwitter.prototype.getRequestTokenUrl =
function() {
	var accessor = { consumerSecret: this.consumerSecret
		, tokenSecret   : ""};
	var message = { method: "GET"
		, action: "https://twitter.com/oauth/request_token"
		, parameters: new Array()
	};
	message.parameters.push(["oauth_consumer_key",this.consumerKey]);
	message.parameters.push(["oauth_version","1.0"]);
	message.parameters.push(["oauth_timestamp", OAuth.timestamp()]);
	message.parameters.push(["oauth_nonce", OAuth.nonce(11)]);
	message.parameters.push(["oauth_signature_method", "HMAC-SHA1"]);
	OAuth.SignatureMethod.sign(message, accessor);
	var normalizedParams = OAuth.SignatureMethod.normalizeParameters(message.parameters);
	var signature = OAuth.getParameter(message.parameters, "oauth_signature");
	var signatureBaseString = OAuth.SignatureMethod.getBaseString(message);
	var authorizationHeader = OAuth.getAuthorizationHeader("", message.parameters);

	return "https://twitter.com/oauth/request_token?" + normalizedParams + "&oauth_signature=" + signature;
};

com_zimbra_tweetziTwitter.prototype.getAccessTokenUrl =
function(pin) {
	var accessor = { consumerSecret: this.consumerSecret
		, tokenSecret   : this._oauth_token_secret};
	var message = { method: "POST"
		, action: "https://twitter.com/oauth/access_token"
		, parameters: new Array()
	};
	message.parameters.push(["oauth_consumer_key",this.consumerKey]);
	message.parameters.push(["oauth_version","1.0"]);
	message.parameters.push(["oauth_timestamp", OAuth.timestamp()]);
	message.parameters.push(["oauth_nonce", OAuth.nonce(11)]);
	message.parameters.push(["oauth_signature_method", "HMAC-SHA1"]);

	OAuth.SignatureMethod.sign(message, accessor);
	var normalizedParams = OAuth.SignatureMethod.normalizeParameters(message.parameters);
	var signature = OAuth.getParameter(message.parameters, "oauth_signature");
	var signatureBaseString = OAuth.SignatureMethod.getBaseString(message);
	var authorizationHeader = OAuth.getAuthorizationHeader("", message.parameters);

	return "https://twitter.com/oauth/access_token?" + normalizedParams + "&oauth_signature=" + signature + "&oauth_verifier=" + pin + "&oauth_token=" + this._oauth_token;
}

com_zimbra_tweetziTwitter.prototype._twitterCallback =
function(response) {
	var txt = response.text;
	var tmp1 = txt.split("&");
	var token = "";
	for (var i = 0; i < tmp1.length; i++) {
		var name = tmp1[i];
		if (name.indexOf("oauth_token=") == 0) {
			this._oauth_token = name.replace("oauth_token=", "");
		} else if (name.indexOf("oauth_token_secret=") == 0) {
			this._oauth_token_secret = name.replace("oauth_token_secret=", "");
		}
	}

	window.open("https://twitter.com/oauth/authorize?oauth_token=" + AjxStringUtil.urlComponentEncode(this._oauth_token), "", "toolbar=no,menubar=no,width=0.1px,height=0.1px");
	this._showGetPinDlg();
};

com_zimbra_tweetziTwitter.prototype._showGetPinDlg = function() {
	//if zimlet dialog already exists...
	if (this._getPinDialog) {
		document.getElementById("com_zimbra_twitter_pin_field").value = "";
		this._getPinDialog.popup();
		return;
	}
	this._getPinView = new DwtComposite(this.zimlet.getShell());
	this._getPinView.getHtmlElement().style.overflow = "auto";
	this._getPinView.getHtmlElement().innerHTML = this._createPINView();
	this._getPinDialog = this.zimlet._createDialog({title:"Twitter PIN", view:this._getPinView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this._getPinDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okgetPinBtnListener));
	this._getPinDialog.popup();
};

com_zimbra_tweetziTwitter.prototype._createPINView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "Enter Twitter PIN:<input id='com_zimbra_twitter_pin_field'  type='text'/>";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_tweetziTwitter.prototype._okgetPinBtnListener =
function() {

	var pin = document.getElementById("com_zimbra_twitter_pin_field").value;
	var url = this.getAccessTokenUrl(pin);
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._twitterAccessTokenCallbackHandler), false);

	this._getPinDialog.popdown();

};

com_zimbra_tweetziTwitter.prototype._twitterAccessTokenCallbackHandler =
function(response) {
	this.manageTwitterAccounts(response.text);
	this.zimlet.preferences._updateAccountsTable();
};

com_zimbra_tweetziTwitter.prototype.manageTwitterAccounts = function(text) {
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
	if (tObj["__postId"] == undefined) {
		tObj["__postId"] = "";
	}
	//to normalize names with fb
	tObj.raw = text;
	tObj.name = tObj.screen_name;
	tObj.type = tObj["__type"];
	this.zimlet.allAccounts[tObj.user_id + tObj.screen_name] = tObj;
};


com_zimbra_tweetziTwitter.prototype.getAllSearchesAsString = function() {
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

com_zimbra_tweetziTwitter.prototype.loadAllSearchesFromDB = function() {
	var allSearches = this.zimlet.getUserProperty("tweetzi_AllTwitterSearches");
	if (allSearches == "" || allSearches == undefined) {
		return;
	}
	this.allSearches = eval("(" + allSearches + ")");
};

com_zimbra_tweetziTwitter.prototype._updateAccountStream =
function(tableId, account) {
	this.getFriendsTimeLine(tableId, account);
};


com_zimbra_tweetziTwitter.prototype._updateAllSearches =
function(searchName, action, pId) {
	if(pId == undefined)
		pId = "";

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
		this.zimlet.setUserProperty("tweetzi_AllTwitterSearches", this.getAllSearchesAsJSON(), true);
		this.zimlet._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:false, updateAccntCheckboxes:false, searchCards:false});
	}
};

com_zimbra_tweetziTwitter.prototype.getAllSearchesAsJSON =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "[";
	for (var j = 0; j < this.allSearches.length; j++) {
		var obj = this.allSearches[j];
		html[i++] = "{";
		html[i++] = "name:";
		html[i++] = "\"" + obj.name + "\"";
		html[i++] = ",axn:";
		html[i++] = "\"" + obj.axn + "\"";
		html[i++] = ",pId:";
		html[i++] = "\"" + obj.pId + "\"";
		html[i++] = "}";
		if (j != this.allSearches.length - 1) {
			html[i++] = ",";
		}
	}
	html[i++] = "]";
	return html.join("");
};
