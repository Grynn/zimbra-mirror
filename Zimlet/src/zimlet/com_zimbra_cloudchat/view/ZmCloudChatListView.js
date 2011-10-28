function ZmCloudChatListView(zimlet, model, controller, tabPage, dontShowUsersInTab) {
	ZmCloudListView.call(this, zimlet, model, controller);
	controller.view = this;//store myself
	this.id = "ZmCloudChatListView_feed_div_" + Dwt.getNextId();
	this._isTypingStr = " " + this.zimlet.getMessage("isTyping");
	this.chatUsers = this.controller.users;
	this.parentView = document.getElementById(tabPage.postsDiv);
	this.chatInfoDiv = document.getElementById(tabPage.chatInfoDivId);
	this.currentUsersEmailOrAliasForThisChat = controller.currentUsersEmailOrAliasForThisChat;
	this._dateFormatter = AjxDateFormat.getDateTimeInstance(AjxDateFormat.LONG, AjxDateFormat.SHORT);

	this._userAndPresenceMap = [];
	this.tabPage = tabPage;
	this.userListViewId = tabPage.userListViewId;
	this.dontShowUsersInTab = dontShowUsersInTab;
	this.model.itemAdded.attach(AjxCallback.simpleClosure(
			this._insertIncomingMessage, this));
	this.model.connectionEvent.attach(AjxCallback.simpleClosure(
			this._handleConnectionEvent, this));

	this.model.itemPresenceUpdated.attach(AjxCallback.simpleClosure(
			this._updateItemPresence, this));

}

ZmCloudChatListView.prototype = new ZmCloudListView;
ZmCloudChatListView.prototype.constructor = ZmCloudChatListView;

ZmCloudChatListView.prototype.createView = function() {
	this.htmlEl = document.createElement("ul");
	this.htmlEl.id = this.id;
	this.htmlEl.style.listStyleType = "none";
	this.htmlEl.style.padding = "0";
	this.parentView.appendChild(this.htmlEl);
	this.isRendered = true;
};

ZmCloudChatListView.prototype._handleConnectionEvent = function(message, raw) {
	if (raw) {
		this._appendConnectionMessage(message);
	}
	var jsonObj = this.getParsedMsg(message);
	if (!jsonObj) {
		return;
	}
	this._appendConnectionMessage(jsonObj.connectionData);
};

ZmCloudChatListView.prototype._insertIncomingMessage = function(jsonObj) {
	if(!jsonObj) {
		return;
	}
	if (jsonObj.action == "GET_PRESENCE") {
		this._updatePresence(jsonObj.data);
		return;
	}
	if (jsonObj instanceof Array) {
		for (var i = 0; i < jsonObj.length; i++) {
			this.__appendMessage(jsonObj[i]);
		}
	} else {
		this._clearUserIsTyping();
		this.__appendMessage(jsonObj);
	}

};

ZmCloudChatListView.prototype.handleUserIsTyping = function(jsonObj) {
	if(jsonObj.user != this.currentUsersEmailOrAliasForThisChat) {
	 	this.chatInfoDiv.innerHTML  = jsonObj.user + this._isTypingStr;
		//clear after 5 secs
		setTimeout(AjxCallback.simpleClosure(this._clearUserIsTyping, this), 5000);
	}
};

ZmCloudChatListView.prototype._clearUserIsTyping = function() {
	this.chatInfoDiv.innerHTML = "";
};

ZmCloudChatListView.prototype.getParsedMsg = function(message) {
	var jsonObj;
	var couldNotParse = false;
	if (!message) {
		couldNotParse = true;
	} else {
		try {
			jsonObj = JSON.parse(message);
		} catch(e) {
			couldNotParse = true;
		}
	}
	if (couldNotParse) {
		this._appendErrorMsg("Could not parse data");
		return;
	}
	return jsonObj;
};

ZmCloudChatListView.prototype._updateItemPresence = function(item) {
	var email = item.email;
	var presence = item.presence;
	var hasUser = false;
	for(var i =0; i < this.chatUsers.length; i++) {
		if(this.chatUsers[i] == email) {
			hasUser = true;
			break;
		}
	}
	if(hasUser && !this.dontShowUsersInTab) {
		this._userAndPresenceMap[email] = presence;
		this._updatePresenceHTML(email, presence, false);
	}
	if(presence == "ONLINE") {
		this.controller.reconnectWithUser(email);
	}
	this._appendConnectionMessage(email + " is now "+ presence.toLowerCase());
};

ZmCloudChatListView.prototype._updatePresence = function(users) {
	if (!this._userListDiv) {
		this._usersListDiv = document.getElementById(this.userListViewId);
	}
	this._updateUserPresenceMap(users);
	var html = [];
	for (var email in this._userAndPresenceMap) {
		var presence = this._userAndPresenceMap[email];
		if(!this.dontShowUsersInTab) {
			this._updatePresenceHTML(email, presence, true);
		}
	}
};

ZmCloudChatListView.prototype._updatePresenceHTML = function(email, presence, addIfNotPresent) {
		var presenceDomId = [email, "_presence_", this.id].join("");
		var dom = document.getElementById(presenceDomId);
		if (dom) {
			dom.innerHTML = this._getUserNameAndPresenceHtml(email.split("@")[0], presence);
			return;
		}
		if(addIfNotPresent){
			var el = document.createElement('div');
			el.id = presenceDomId;
			el.className = "CloudChatFeedRow";
			el.padding = "2px";
			el.innerHTML = this._getUserNameAndPresenceHtml(email.split("@")[0], presence);
			this._usersListDiv.appendChild(el);
		}
};

ZmCloudChatListView.prototype._updateUserPresenceMap = function(users) {
	for (var i = 0; i < this.chatUsers.length; i++) {
		var chatUser = this.chatUsers[i];
		var presenceFound = false;
		for (var j = 0; j < users.length; j++) {
			var user = users[j];
			if (!user) {
				continue;
			}
			if (user.indexOf(chatUser) == 0) {
				var tmp = user.split(":");
				this._userAndPresenceMap[chatUser] = tmp[1];
				presenceFound = true;
				break;
			}
		}
		if (!presenceFound) {
			this._userAndPresenceMap[chatUser] = "OFFLINE";
		}
	}
};

ZmCloudChatListView.prototype.__appendMessage = function(jsonObj) {
	var el = document.createElement('li');
	el.className = "CloudChatFeedRow";
	el.innerHTML = this.__getRowHtml(jsonObj);
	this.htmlEl.appendChild(el);
	this.parentView.scrollTop = 1000000;
};

ZmCloudChatListView.prototype._appendConnectionMessage = function(connectionMsg) {
	var el = document.createElement('li');
	el.className = "CloudChatFeedConnectionRow";
	el.innerHTML = this.__getConnectionRowHtml(connectionMsg);
	this.htmlEl.appendChild(el);
	this.parentView.scrollTop = 1000000;
};

ZmCloudChatListView.prototype._appendErrorMsg = function(errorMsg) {
	var el = document.createElement('li');
	el.className = "CloudChatFeedErrorRow";
	el.innerHTML = errorMsg;
	this.htmlEl.appendChild(el);
	this.parentView.scrollTop = 1000000;
};

//ZmCloudChatListView.prototype.__getUserHtml = function(email, presence, presenceId) {
//	var username = email.split("@")[0];
//	return this._getUserNameAndPresenceHtml(username, presence);
//};

ZmCloudChatListView.prototype.__getConnectionRowHtml = function(text) {
	var html = [];
	html.push("<table align=center width=95%><tr><td><div class='CloudChatInfoDiv' width=100%>"
			,text
			,"</div></td></tr></table>");
	return html.join("");
};

ZmCloudChatListView.prototype.__getRowHtml = function(jsonObj) {
	var username = jsonObj.email.split("@")[0];
	var picsUrl = "pics/" + username + ".jpg";
	var tDivId = Dwt.getNextId();
	var html = [];
	html.push("<table width=100%>"
			, "<tr>"
			, "<td width=32px valign=top>"
			, "<div class='ImgCCPerson32'></div>"
			, "</td>"
			, "<td valign=top>"
			, "<div style='padding:4px'>"
			, "<a style='color:darkblue;font-size:12px;font-weight:bold'>", username, ":</a>&nbsp;"
			, "<label style='font-size:12px;'>"
			, jsonObj.data
			, "</label>"
			, "</div>"

			//, "<div tyle='padding:4px'>"
			//, "<label  id='", tDivId, "' style='color:black;background:#FFFF99;font-style:italics;'>"
			//, this._translateText(jsonObj.data, tDivId)
			//, " </label>"
			//, "</div>"

			, "<br/><label style='color:gray;font-size:11px'>"
			, this.__getTime(jsonObj.time)
			, " </label> <br/>"
			, "</td>"
			, "</tr>"
			, "</table>");

	return html.join("");
};

ZmCloudChatListView.prototype.__getTime = function(parsedDate) {
	return this._dateFormatter.format(new Date(parsedDate));
};

ZmCloudChatListView.prototype._translateText = function(origText, divId) {
	var reqParams = [];
	var i = 0;

	var currentUserName = appCtxt.getSettings().getInfoResponse.name;
	var langPair = "|en";
	if(currentUserName == "admin@rr.zimbra.com") {
		langPair = "|ja";
	}
	// params for google translator
	reqParams[i++] = "q=";
	reqParams[i++] = AjxStringUtil.urlEncode(origText);
	reqParams[i++] = "&langpair=";
	reqParams[i++] = AjxStringUtil.urlEncode(langPair);
	reqParams[i++] = "&hl=en&v=1.0&format=text";
	var reqHeader = {
		"User-Agent": navigator.userAgent,
		"Content-Type": "application/x-www-form-urlencoded",
		"Referrer": "http://translate.google.com/translate_t"
	};
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode("http://ajax.googleapis.com/ajax/services/language/translate");

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._translationHandler, divId));
};

ZmCloudChatListView.prototype._translationHandler = function(divId, result) {
	var html = [];

	if (!result.success) {
		this._appendErrorMsg("Could not connect to translate server");
		return;
	}

	var jsonObj = eval("(" + result.text + ")");
	if (jsonObj.responseStatus == 400) {
		this._appendErrorMsg("Did not understand translation");
		return;
	}
	if (jsonObj.responseData && jsonObj.responseData.translatedText) {
		html.push("Translated text: ", jsonObj.responseData.translatedText);

		document.getElementById(divId).innerHTML = html.join("");
	}
};

ZmCloudChatListView.prototype._getUserNameAndPresenceHtml = function(text, presence) {
	var pClass = "ImgMsgRead";
	if (presence == "ONLINE") {
		text = "<b>" + text + "</b>";
		pClass = "ImgMsgUnread";
	} else {
		text = "<label style='color:gray;cursor: pointer;'>" + text + "</label>";
	}
	var html = [];
	var idx = 0;
	html[idx++] = "<table cellpadding=\"0\" cellspacing=\"2\">";
	html[idx++] = "<tr>";
	html[idx++] = "<td width=16px valign=middle>";
	html[idx++] = "<div class='ImgPerson'></div>";
	html[idx++] = "</td>";
	html[idx++] = "<td width=16px valign=middle>";
	html[idx++] = "<div class='";
	html[idx++] = pClass;
	html[idx++] = "' />";
	html[idx++] = "</td>";
	html[idx++] = "<td width='90%'>";
	html[idx++] = text;
	html[idx++] = "</td>";
	html[idx++] = "</tr>";
	html[idx++] = "</table>";
	return html.join("");
};