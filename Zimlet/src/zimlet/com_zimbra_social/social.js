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

function com_zimbra_social() {
}

com_zimbra_social.prototype = new ZmZimletBase();
com_zimbra_social.prototype.constructor = com_zimbra_social;


com_zimbra_social.prototype.init =
function() {
	this._createsocialApp();
};

com_zimbra_social.prototype.initializeVariables =
function() {
	if(this._variablesInitialized != undefined)//initialize only once
		return;

	ZmZimletBase.prototype.init.apply(this, arguments);
	this.tableIdAndTimerMap = new Array();
	this.tableIdAndAccountMap = new Array();
	this._FBPostIdAndCommentboxMap = new Array();
	this.tableIdAndPostIdMap = new Array();
	this.tableIdAndResultsMap = new Array();
	this.allAccounts = new Array();
	this._allHashLinks = new Array();
	this._allReweetLinks = new Array();
	this._allDMLinks = new Array();
	this._allTwitterDeleteLinks = new Array();
	this._allReplyLinks = new Array();
	this._allFollowLinks = new Array();
	this._allAccountsLinks = new Array();
	this._allFacebookCommentsLinks = new Array();
	this.cardInfoSectionIdsArray = new Array();//every card's data div (used to reset its height when window is resized)
	this._migrateOldPropsNames();//from tweetzi to social

	this.preferences = new com_zimbra_socialPreferences(this);
	this.twitter = new com_zimbra_socialTwitter(this);
	this.facebook = new com_zimbra_socialFacebook(this);
	this.tweetmeme = new com_zimbra_socialTweetMeme(this);
	this.digg = new com_zimbra_socialDigg(this);

	this.loadAllAccountsFromDB();
	this._objectManager = new ZmObjectManager(new DwtComposite(this.getShell()));


	//scan for tweets..
	this.social_emailLastUpdateDate = this.getUserProperty("social_emailLastUpdateDate");
	var todayStr = this.twitter._getTodayStr();
	if(this.social_emailLastUpdateDate != todayStr && this.preferences.social_pref_SocialMailUpdateOn) {
		this.twitter.scanForUpdates();
		this.setUserProperty("social_emailLastUpdateDate", todayStr, true);
	}



	this._variablesInitialized = true;

};

com_zimbra_social.prototype._migrateOldPropsNames =
function() {
	var props = new Array();
	var propsToEmpty = new Array();
	props["tweetzi_AllTwitterAccounts"] = "social_AllTwitterAccounts";
	props["tweetZi_emailLastUpdateDate"] = "social_emailLastUpdateDate";
	props["tweetzi_AllTwitterSearches"] = "social_AllTwitterSearches";
	props["tweetzi_facebook_api_key"] = "social_facebook_api_key";
	props["tweetzi_facebook_secret"] = "social_facebook_secret";
	props["tweetzi_twitter_consumer_key"] = "social_twitter_consumer_key";
	props["tweetzi_twitter_consumer_secret"] = "social_twitter_consumer_secret";
	for(var oldprop in props) {
		var migrated = false;
		var old_prop_val = this.getPropValueFromSettings(oldprop);
		var new_prop_val = this.getPropValueFromSettings(props[oldprop]);
		if(old_prop_val != undefined && old_prop_val != "" && (new_prop_val == undefined || new_prop_val == "")) {
			migrated = true;
			this.setUserProperty(props[oldprop], old_prop_val);
		}
	}			
	if(migrated)
		this.saveUserProperties();
};

com_zimbra_social.prototype.getPropValueFromSettings =
function(name) {
	try{
		if(this._propsInSettings == undefined) {
			this._propsInSettings = appCtxt.getActiveAccount().settings.getInfoResponse.props.prop;
		}
	}catch(e) {
		this._propsInSettings = new Array();
	}
	if(this._propsInSettings == undefined)
		return (new Array());

	for(var i =0; i < this._propsInSettings.length; i++) {
		if(name == this._propsInSettings[i].name)
			return this._propsInSettings[i]._content;		
	}
	return (new Array());
};

com_zimbra_social.prototype._addTweetButtons =
function() {
	this.updateButton = new DwtButton({parent:this.getShell()});
	this.updateButton.setText("update");
	this.updateButton.addSelectionListener(new AjxListener(this, this._postToTweetOrFB));
	document.getElementById("social_updateStatusButton").appendChild(this.updateButton.getHtmlElement());

	var shortenUrlButton = new DwtButton({parent:this.getShell()});
	shortenUrlButton.setText("shorten url");
	shortenUrlButton.addSelectionListener(new AjxListener(this, this._shortenUrlButtonListener));
	document.getElementById("social_shortenUrlButton").appendChild(shortenUrlButton.getHtmlElement());

	var searchButton = new DwtButton({parent:this.getShell()});
	searchButton.setText("twitter search");
	searchButton.addSelectionListener(new AjxListener(this, this._twitterSearchBtnListener));
	document.getElementById("social_searchButton").appendChild(searchButton.getHtmlElement());

	Dwt.setHandler(document.getElementById("social_searchField"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this.twitterSearchKeyHdlr, this));
	Dwt.setHandler(document.getElementById("social_statusTextArea"), DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this.showNumberOfLetters, this));
	Dwt.setHandler(document.getElementById("social_statusTextArea"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this._postToTweetOrFB, this));
};

com_zimbra_social.prototype._addAccountCheckBoxListeners =
function() {
	for (var accntId in this.allAccounts) {
		var callback = AjxCallback.simpleClosure(this._saveToAccountCheckboxesPref, this, accntId);
		Dwt.setHandler(document.getElementById(this.allAccounts[accntId].checkboxId), DwtEvent.ONCLICK, callback);
	}
};

com_zimbra_social.prototype._shortenUrlButtonListener =
function() {
	var selectedTxt = this._getStatusFieldSelectedText();
	//if zimlet dialog already exists...
	if (this._shortenUrlDialog) {
		if (selectedTxt.indexOf("http:") == 0) {
			document.getElementById("com_zimbra_twitter_longUrl_field").value = selectedTxt;
		}
		this._shortenUrlDialog.popup();
		return;
	}
	this._shortenUrlView = new DwtComposite(this.getShell());
	this._shortenUrlView.setSize(450, 50);
	this._shortenUrlView.getHtmlElement().style.overflow = "auto";
	this._shortenUrlView.getHtmlElement().innerHTML = this._createShortenURLView();
	var suBtnId = Dwt.getNextId();
	var suButton = new DwtDialog_ButtonDescriptor(suBtnId, "Shorten URL", DwtDialog.ALIGN_RIGHT);
	this._shortenUrlDialog = this._createDialog({title:"Shorten URL", view:this._shortenUrlView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[suButton]});

	this._shortenUrlDialog.setButtonListener(suBtnId, new AjxListener(this, this._postToUrlShortner));
	if (selectedTxt.indexOf("http:") == 0) {
		document.getElementById("com_zimbra_twitter_longUrl_field").value = selectedTxt;
	}
	this._shortenUrlDialog.popup();
};

com_zimbra_social.prototype._postToUrlShortner =
function() {
	var longUrl = document.getElementById("com_zimbra_twitter_longUrl_field").value;
	
	var url = "http://api.bit.ly/shorten?version=2.0.1&longUrl="+longUrl+"&login=zimbra&apiKey=R_20927271403ca63a07c25d17edc32a1d";
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._postToUrlShortnerCallback, longUrl), false);
};

com_zimbra_social.prototype._getStatusFieldSelectedText =
function() {
	var statusField = document.getElementById("social_statusTextArea");
	return  AjxStringUtil.trim(statusField.value.substring(statusField.selectionStart, statusField.selectionEnd));
};

com_zimbra_social.prototype._replaceStatusFieldSelectedTxt =
function(selectedTxt, newTxt) {
	var statusField = document.getElementById("social_statusTextArea");
	statusField.value = statusField.value.replace(selectedTxt, newTxt);
};

com_zimbra_social.prototype._postToUrlShortnerCallback =
function(longUrl, response) {
	if (!response.success) {
		appCtxt.getAppController().setStatusMsg("Could Not Shorten", ZmStatusView.LEVEL_WARNING);
		return;
	}
	try{
		var text =  eval("(" + response.text + ")");
		var shortUrl = text.results[longUrl].shortUrl;
		var selectedTxt = this._getStatusFieldSelectedText();
		if (selectedTxt == longUrl) {
			this._replaceStatusFieldSelectedTxt(selectedTxt, shortUrl);
		} else {
			document.getElementById("social_statusTextArea").value = document.getElementById("social_statusTextArea").value + " " + shortUrl;
		}
	}catch(e) {
		appCtxt.getAppController().setStatusMsg("Could Not Shorten", ZmStatusView.LEVEL_WARNING);
	}
	this._shortenUrlDialog.popdown();
};

com_zimbra_social.prototype._createShortenURLView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "Long URL:<input id='com_zimbra_twitter_longUrl_field'  type='text' width=100% style='width:100%'/>";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_social.prototype._getTableIdFromAccount =
function(reqAccount) {
	for (var id in this.tableIdAndAccountMap) {
		var actAccnt = this.tableIdAndAccountMap[id];
		if (actAccnt.name == reqAccount.name)
			return id;
	}
	return "";
};

com_zimbra_social.prototype._saveToAccountCheckboxesPref =
function(accntId, ev) {
	if (this.allAccounts[accntId].__on == "true")
		this.allAccounts[accntId].__on = "false";
	else
		this.allAccounts[accntId].__on = "true";

	this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
};

com_zimbra_social.prototype.showNumberOfLetters =
function(ev) {
	var val = document.getElementById("social_statusTextArea").value;
	var len = val.length;
	if (len > 140) {
		this.updateButton.setEnabled(false);
	} else {
		this.updateButton.setEnabled(true);
	}
	if (val.toLowerCase().indexOf("d @") == 0) {
		document.getElementById("social_whatareyoudoingLabel").innerHTML = "Send Direct/private Message:";
		document.getElementById("social_whatareyoudoingLabel").style.color = "yellow";
	} else {
		document.getElementById("social_whatareyoudoingLabel").innerHTML = "What are you doing?";
		document.getElementById("social_whatareyoudoingLabel").style.color = "black";
	}
	com_zimbra_social._updateNumberOfLettersField(len);

};

com_zimbra_social._updateNumberOfLettersField =
function(count) {
	var clr = "";
	if (count > 140) {
		clr = "red";
	} else {
		clr = "green";
	}
	var html = [];
	var idx = 0;
	html[idx++] = "<label style=\"font-size:14px;color:" + clr + ";font-weight:bold\">";
	html[idx++] = count;
	html[idx++] = "</label>";
	document.getElementById("social_numberOfLettersAllowed").innerHTML = html.join("");
};

com_zimbra_social.prototype.twitterSearchKeyHdlr =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode == 13) {
		this._twitterSearchBtnListener();
	}
};

com_zimbra_social.prototype._twitterSearchBtnListener =
function() {
	var val = document.getElementById("social_searchField").value;
	var tableId = this._showCard({headerName:val, type:"SEARCH", autoScroll:true});
	var sParams = {query:val, tableId:tableId, type:"SEARCH"};
	this.twitter.twitterSearch(sParams);
	var timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
	this.tableIdAndTimerMap[tableId] = timer;
	var search = {name:val, axn:"on", pId:""};
	this.tSearchFolders.push({name:val, icon:"SearchFolder", account:"", type:"SEARCH", search:search});

	this.twitter.allSearches.push(search);
	this.tableIdAndSearchMap[tableId] = search;
	this.twitter._updateAllSearches(val, "on");
};

com_zimbra_social.prototype._loadInformation =
function() {
	this.twitter.getTwitterTrends();
	this.tweetmeme.getTweetmemeCategories();
	this.digg.getDiggCategories();
};

com_zimbra_social.prototype._constructSkin =
function() {
	var html = [];
	var idx = 0;
	html[idx++] = "<DIV class='overviewHeader' id='social_topSxn'>";
	html[idx++] = "<TABLE>";
	html[idx++] = "<TR><td id='social_updateToCell'>";
	html[idx++] = this._addUpdateToCheckboxes();
	html[idx++] = "</TD>";
	html[idx++] = "<TD align=center valign=middle>";
	html[idx++] = "<div id='social_numberOfLettersAllowed' />";
	html[idx++] = "<label style=\"font-size:16px;color:green;font-weight:bold\">";
	html[idx++] = "0";
	html[idx++] = "</label>";
	html[idx++] = "</TD>";
	html[idx++] = "</tr>";
	html[idx++] = "<TR><TD style=\"width:100%;\" >";
	html[idx++] = "<input  style=\"width:100%;height:25px\" autocomplete=\"off\" id=\"social_statusTextArea\" ></input>";
	html[idx++] = "</TD>";
	html[idx++] = "<TD>";
	html[idx++] = "<div id='social_updateStatusButton' />";
	html[idx++] = "</TD>";
	html[idx++] = "</TR></TABLE>";
	html[idx++] = "</DIV>";
	html[idx++] = "<DIV id='social_twitterCardsDiv' class='social_twitterCardsDiv DwtPropertyPage'>";
	html[idx++] = "<table id='social_twitterCardsParentTable' cellspacing=10px>";
	html[idx++] = "</table>";
	html[idx++] = "</DIV>";
	return html.join("");
};

com_zimbra_social.prototype._setMainCardHeight =
function() {
	var mainCardsDiv = document.getElementById('social_twitterCardsDiv');
	mainCardsDiv.style.overflow = "auto";
	var parent = this._view.getHtmlElement();
	mainCardsDiv.style.width = parent.style.width;
	mainCardsDiv.style.height = (parseInt(parent.style.height.replace("px", "")) - document.getElementById("social_topSxn").offsetHeight);
	//24+10 is the height of the header-section(where close, delete buttons are present)
	this._mainCardsHeight = (parseInt(mainCardsDiv.style.height.replace("px", "")) - 75) + "px";

	for (var i = 0; i < this.cardInfoSectionIdsArray.length; i++) {
		var infoCard = document.getElementById(this.cardInfoSectionIdsArray[i]);
		if (infoCard != null) {
			infoCard.style.height = this._mainCardsHeight;
		}
	}
};

com_zimbra_social.prototype._resizeHandler =
function(ev) {
	this._setMainCardHeight();
}

com_zimbra_social.prototype._addUpdateToCheckboxes =
function() {
	var html = [];
	var idx = 0;
	var hasAccounts = false;
	html[idx++] = "<TABLE>";
	html[idx++] = "<TR><td>";
	html[idx++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">update to: ";
	html[idx++] = "</label>";
	html[idx++] = "</TD>";
	for (var id in this.allAccounts) {
		hasAccounts = true;
		var turnOnStr = "";
		if (this.allAccounts[id].__on == "true") {
			turnOnStr = "checked";
		}
		var chkbxId = this.allAccounts[id].checkboxId = "social_updateToCheckbox_" + id;
		html[idx++] = "<TD valign=middle align=center>";
		html[idx++] = "<input type='checkbox'  " + turnOnStr + "  id='" + chkbxId + "'>";
		html[idx++] = "</TD><TD valign=middle align=center>";
		html[idx++] = this.allAccounts[id].name;
		html[idx++] = " &nbsp;&nbsp;&nbsp;&nbsp;";
	}

	html[idx++] = "</TR></TABLE>";


	if (hasAccounts)
		return html.join("");
	else {
		return "<label style=\"font-size:12px;color:blue;font-weight:bold\">Please Click on 'Add/Remove Accounts' in the left pane to add twitter & facebook accounts</label>";
	}
};

com_zimbra_social.prototype._showCard =
function(params) {
	var headerName = params.headerName;
	var type = params.type;
	var tweetTableId = params.tweetTableId;
	var autoScroll = params.autoScroll;
	var cardsTable = document.getElementById('social_twitterCardsParentTable');
	var row;
	var origHeaderName = headerName;
	if (cardsTable.rows.length == 0) {
		row = cardsTable.insertRow(0);
	} else {
		row = cardsTable.rows[0];
	}
	if (this.cardIndex == undefined) {
		this.cardIndex = 0;
	} else {
		this.cardIndex = this.cardIndex + 1;
	}
	var hdrClass = "social_axnClass social_generalColor";
	var prettyName = type.toLowerCase() + ": " + headerName;
	var iconName = "social_twitterIcon";
	var hdrCellColor = "black";
	if (type == "ACCOUNT") {
		hdrClass = "social_axnClass social_twitterColor";
		prettyName = "twitter: " + headerName;
	} else if (type == "FACEBOOK") {
		hdrClass = "social_axnClass social_facebookColor";
		hdrCellColor = "white";
		prettyName = headerName;
		iconName = "social_facebookIcon";
	} else if (type == "TWEETMEME") {
		iconName = "social_tweetMemeIcon";
		hdrClass = "social_tweetMemeRetweetBg";
		hdrCellColor = "white";
	} else if (type == "DIGG") {
		iconName = "social_diggIcon";
		hdrClass = "social_diggHdrBg";
	} else if (type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
		hdrClass = "social_axnClass social_twitterColor";
		prettyName = type.toLowerCase();
	}else if(type == "TREND") {
		iconName = "social_trendIcon";
	}
	var hdrCellStyle = "style=\"font-size:12px;color:" + hdrCellColor + ";font-weight:bold;font-family:'Lucida Grande',sans-serif;\"";
	var card = "";
	var sDistance = "";
	if (type == "ACCOUNT" || type == "FACEBOOK") {
		card = row.insertCell(0);
		sDistance = 0;
	} else if (type == "PROFILE") { //inset profile card right-next to the from-card
		tweetTableId = tweetTableId.replace("social_cardInfoSectionId", "social_card");
		var cells = row.childNodes;
		for (var i = 0; i < cells.length; i++) {
			if (tweetTableId.indexOf(cells[i].id) == 0) {
				card = row.insertCell(i + 1);
				break;
			}
		}
		sDistance = 410 * i;
	} else {
		var _indx = 0;
		for (var id in this.allAccounts) {
			if (this.allAccounts[id].isDisplayed) {
				_indx++;
			}
		}
		card = row.insertCell(_indx);
		sDistance = 410 * _indx;
	}
	if (autoScroll) {
		document.getElementById('social_twitterCardsDiv').scrollLeft = sDistance;
	}
	card.id = "social_card" + this.cardIndex;
	var cardInfoSectionId = "social_cardInfoSectionId" + this.cardIndex;
	//used to reset heights when window is resized
	this.cardInfoSectionIdsArray.push(cardInfoSectionId);
	var html = [];
	var i = 0;
	html[i++] = "<div width=400px  class='social_cardDiv'>";
	var elStyle = ""
	if(AjxEnv.isFirefox){
		elStyle="style='height: 27px;'";
	} else {
		elStyle ="style='height: 32px;'";
	}
	html[i++] = "<DIV  width=400px "+elStyle+" class='" + hdrClass + "' >";
	html[i++] = "<table width='100%'>";
	html[i++] = "<tr><td width='5%' >" + AjxImg.getImageHtml(iconName) + "</td>";
	html[i++] = "<td width=95%><table><tr><td " + hdrCellStyle + " >" + prettyName + "</td><td id='social_unreadCountCell" + this.cardIndex + "'></td></tr></table></td>";
	if (type == "ACCOUNT" || type == "SEARCH" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
		html[i++] = "<td width='5%'>";
		html[i++] = "<img    title=\"Clear contents (aka mark as read)\" src=\"" + this.getResource("social_markread.gif") + "\" id='social_markAsReadBtn" + this.cardIndex + "' />";
		html[i++] = "</td>";
	}

	if (type == "SEARCH") {
		html[i++] = "<td width='5%'>";
		html[i++] = "<img  title=\"Delete this search permanently from overview panel\" src=\"" + this.getResource("social_deleteBtn.gif") + "\" id='social_deleteBtn" + this.cardIndex + "' />";
		html[i++] = "</td>";
	}

	html[i++] = "<td width='5%'></td>";
	if (type != "PROFILE") {
		html[i++] = "<td width='5%'>";
		html[i++] = "<img  title=\"Refresh this card\" src=\"" + this.getResource("social_refreshBtn.gif") + "\" id='social_refreshBtn" + this.cardIndex + "'/></td>";
	}
	html[i++] = "<td width='5%'></td><td width='5%'>";
	html[i++] = "<img  title=\"Close this card\" src=\"" + this.getResource("social_closeBtn.png") + "\" id='social_closeBtn" + this.cardIndex + "'/></td></tr>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV id='" + cardInfoSectionId + "' style=\"overflow:auto;height:" + this._mainCardsHeight + ";\" class='social_individualCardClass'>";
	html[i++] = "</DIV>";
	html[i++] = "</div>";
	card.innerHTML = html.join("");

	var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
	if (type == "SEARCH") {
		var callback = AjxCallback.simpleClosure(this._handleDeleteButton, this, params);
		document.getElementById("social_deleteBtn" + this.cardIndex).onclick = callback;
	}
	var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
	var callback = AjxCallback.simpleClosure(this._handleCloseButton, this, params);
	document.getElementById("social_closeBtn" + this.cardIndex).onclick = callback;

	if (type != "PROFILE") {
		var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
		var callback = AjxCallback.simpleClosure(this._handleRefreshButton, this, params);
		document.getElementById("social_refreshBtn" + this.cardIndex).onclick = callback;
	}
	if (type == "ACCOUNT" || type == "SEARCH" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
		var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
		var callback = AjxCallback.simpleClosure(this._handleMarkAsReadButton, this, params);
		document.getElementById("social_markAsReadBtn" + this.cardIndex).onclick = callback;
	}

	return cardInfoSectionId;
};

com_zimbra_social.prototype._setCardUnreadCount =
function(tableId, unReadCount) {
	var cellId = tableId.replace("social_cardInfoSectionId", "social_unreadCountCell");


	var cell = document.getElementById(cellId);
	if(cell == null)
		return;

	if (unReadCount == 0) {
		cell.className = "";
		cell.innerHTML = "";
	} else {
		cell.className = "social_unReadClass";
		cell.innerHTML = unReadCount;
	}
};

com_zimbra_social.prototype._handleRefreshButton =
function(params) {
	if (params.type == "SEARCH" || params.type == "TREND") {
		this.twitter.twitterSearch({query:params.headerName, tableId:params.tableId, type:params.type});
	} else if (params.type == "ACCOUNT") {
		this.twitter.getFriendsTimeLine({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "TWEETMEME") {
		this.tweetmeme.tweetMemeSearch({query:params.headerName, tableId:params.tableId});
	}  else if (params.type == "DIGG") {
		this.digg.diggSearch({query:params.headerName, tableId:params.tableId});
	}else if (params.type == "FACEBOOK") {
		this.facebook._fbGetStream(params.tableId, this.tableIdAndAccountMap[params.tableId]);
	} else if (params.type == "MENTIONS") {
		this.twitter.getMentions({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "DIRECT_MSGS") {
		this.twitter.getDirectMessages({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "SENT_MSGS") {
		this.twitter.getSentMessages({tableId:params.tableId, account:this.tableIdAndAccountMap[params.tableId]});
	}
};

com_zimbra_social.prototype._handleMarkAsReadButton =
function(params) {
	if (params.type == "SEARCH") {
		var search = this.tableIdAndSearchMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		search.pId = pId;
		this.twitter._updateAllSearches(search.name, "on");
	} else if (params.type == "ACCOUNT") {
		var account = this.tableIdAndAccountMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		account.__postId = pId;
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
	} else if (params.type == "MENTIONS") {
		var account = this.tableIdAndAccountMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		account.__mId = pId;
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);		
	}else if(params.type == "DIRECT_MSGS") {
		var account = this.tableIdAndAccountMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		account.__dmId = pId;
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);	
	}else if(params.type == "SENT_MSGS") {
		var account = this.tableIdAndAccountMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		account.__smId = pId;
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);	
	}
	this.createCardView(params.tableId, this.tableIdAndResultsMap[params.tableId], params.type);
};

com_zimbra_social.prototype._handleDeleteButton =
function(params) {
	var headerName = params.headerName;
	for (var i = 0; i < this.tSearchFolders.length; i++) {
		if (this.tSearchFolders[i].name == headerName) {
			this.tSearchFolders.splice(i, 1);
			this._createTreeView();
			break;
		}
	}
	if(params.tableId) {
		var timer = this.tableIdAndTimerMap[params.tableId];
		if (timer) { //remove update timers
			clearInterval(timer);
		}
	}

	params.row.deleteCell(document.getElementById(params.cellId).cellIndex);
	this.twitter._updateAllSearches(params.headerName, "delete");

};

com_zimbra_social.prototype._handleCloseButton =
function(params) {
	params.row.deleteCell(document.getElementById(params.cellId).cellIndex);
	this.twitter._updateAllSearches(params.headerName, "off");
	var type = params.type;
	if (type == "ACCOUNT" || type == "FACEBOOK") {
		this.tableIdAndAccountMap[params.tableId].isDisplayed = false;
	}
	if(params.tableId) {
		var timer = this.tableIdAndTimerMap[params.tableId];
		if (timer) { //remove update timers
			clearInterval(timer);
		}
	}
};


//---------------------------------------------------------------------------------
// OVERRIDE ZIMLET FRAMEWORK FUNCTIONS AND CREATE APP
//---------------------------------------------------------------------------------
com_zimbra_social.prototype.doubleClicked =
function() {
	this.singleClicked();
};

com_zimbra_social.prototype.singleClicked =
function() {
	//DO NOTHING
};

com_zimbra_social.prototype._createsocialApp =
function() {
	this._socialAppName = this.createApp("Social", "ZimbraIcon", "Twitter & Facebook");
};

com_zimbra_social.prototype.appActive = function(appName, active) {
	if (active) {
		this.appName = appName;
		this._sociallistViews = [];
		this.initializeVariables();
	}
	else {
		this._hideApp(appName);
	}
};

com_zimbra_social.prototype._hideApp = function(appName) {
	//dont do anything
};
com_zimbra_social.prototype.appLaunch = function(appName, params) {
	if (this._socialAppName != appName)
		return;
	
	this.app = appCtxt.getApp(appName);
	this.showAppView();
};

com_zimbra_social.prototype._selectDefaultFolder =
function() {
	this._treeView.setSelected(this._getIdFromName("Missed Calls"));
};

com_zimbra_social.prototype._createTreeView =
function() {
	var html = new Array();
	var i = 0;
	this.expandIconAndFolderTreeMap = new Array();
	var activeApp = appCtxt.getCurrentApp();
	if (activeApp.getName() != this._socialAppName)
		return;

	var overview = activeApp ? activeApp.getOverview() : null;
	var element = overview.getHtmlElement();

//-----------------------
	var expandIconId = "social_expandIcon_" + Dwt.getNextId();
	this.expandIconAndFolderTreeMap[expandIconId] = new Array();
	html[i++] = this._getTreeHeaderHTML("Accounts", expandIconId);	//header
	html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";

	for (var j = 0; j < this.systemFolders.length; j++) {
		var folder = this.systemFolders[j];
		if (folder.account.type == "twitter") {
			var childExpandIconId = "social_expandIcon_" + Dwt.getNextId();
			this.expandIconAndFolderTreeMap[childExpandIconId] = new Array();
			html[i++] = this._getFolderHTML(folder, expandIconId, childExpandIconId);
			html[i++] = this._getFolderHTML({name:"@" + folder.account.name + " (mentions)", icon:"social_twitterIcon", account: folder.account, type:"MENTIONS"}, expandIconId, childExpandIconId, true);
			html[i++] = this._getFolderHTML({name:"direct messages", icon:"social_twitterIcon", account: folder.account, type:"DIRECT_MSGS"}, expandIconId, childExpandIconId, true);
			html[i++] = this._getFolderHTML({name:"sent messages", icon:"social_twitterIcon", account: folder.account, type:"SENT_MSGS"}, expandIconId, childExpandIconId, true);

		} else {
			html[i++] = this._getFolderHTML(folder, expandIconId);
		}
	}
	html[i++] = "</div>";
//-----------------------

//-----------------------
	var expandIconId = "social_expandIcon_" + Dwt.getNextId();
	this.expandIconAndFolderTreeMap[expandIconId] = new Array();
	html[i++] = this._getTreeHeaderHTML("Twitter Searches", expandIconId);	//header
	html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";
	for (var j = 0; j < this.tSearchFolders.length; j++) {
		var folder = this.tSearchFolders[j];
		html[i++] = this._getFolderHTML(folder, expandIconId);
	}
	html[i++] = "</div>";
//-----------------------

//-----------------------
	if (this.tTrendsFolders) {
		var expandIconId = "social_expandIcon_" + Dwt.getNextId();
		this.expandIconAndFolderTreeMap[expandIconId] = new Array();
		html[i++] = this._getTreeHeaderHTML("Twitter Trends", expandIconId);	//header
		html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";
		for (var j = 0; j < this.tTrendsFolders.length; j++) {
			var trend = this.tTrendsFolders[j];
			html[i++] = this._getFolderHTML(trend, expandIconId);
		}
		html[i++] = "</div>";
	}
//-----------------------

//-----------------------
	if (this.tDiggFolders) {
		var expandIconId = "social_expandIcon_" + Dwt.getNextId();
		this.expandIconAndFolderTreeMap[expandIconId] = new Array();
		html[i++] = this._getTreeHeaderHTML("Digg", expandIconId);	//header
		html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";
		for (var j = 0; j < this.tDiggFolders.length; j++) {
			var folder = this.tDiggFolders[j];
			html[i++] = this._getFolderHTML(folder, expandIconId);
		}
		html[i++] = "</div>";
	}
//-----------------------

//-----------------------
	if (this.tTweetMemeFolders) {
		var expandIconId = "social_expandIcon_" + Dwt.getNextId();
		this.expandIconAndFolderTreeMap[expandIconId] = new Array();
		html[i++] = this._getTreeHeaderHTML("TweetMeme", expandIconId);	//header
		html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";
		for (var j = 0; j < this.tTweetMemeFolders.length; j++) {
			var folder = this.tTweetMemeFolders[j];
			html[i++] = this._getFolderHTML(folder, expandIconId);
		}
		html[i++] = "</div>";
	}
//-----------------------

//-----------------------
	var expandIconId = "social_expandIcon_" + Dwt.getNextId();
	if(this.prefFolder == undefined) {
		this.prefFolders = new Array();
		//this.prefFolders.push({name:"Add/Remove Accounts", icon:"Group", account:"", type:"MANAGE_ACCOUNTS"});
		this.prefFolders.push({name:"Preferences", icon:"Preferences", account:"", type:"PREFERENCES"});
	}
	this.expandIconAndFolderTreeMap[expandIconId] = new Array();
	html[i++] = this._getTreeHeaderHTML("Settings", expandIconId);	//header
	html[i++] = "<div class=\"DwtTreeItemLevel1ChildDiv\" style=\"display: block;\">";
	for (var j = 0; j < this.prefFolders.length; j++) {
		var folder = this.prefFolders[j];
		html[i++] = this._getFolderHTML(folder, expandIconId);
	}
	html[i++] = "</div>";
//-----------------------

	element.innerHTML = html.join("");
	element.onclick = AjxCallback.simpleClosure(this._handleTreeClick, this);

};

com_zimbra_social.prototype._getTreeHeaderHTML =
function(treeName, expandIconId) {
	var html = new Array();
	var i = 0;
	if (expandIconId) {
		html[i++] = "<div  class='overviewHeader'>";
	} else {
		html[i++] = "<div  class='overviewHeader'>";
	}
	html[i++] = "<TABLE cellpadding=\"0\" cellspacing=\"0\">";
	html[i++] = "<TR>";
	html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
	html[i++] = "<div class=\"ImgNodeExpanded\" id= '" + expandIconId + "'/>";
	html[i++] = "</TD>";
	html[i++] = "<td class=\"imageCell\" />";
	html[i++] = "<TD  class='overviewHeader-Text'>";
	html[i++] = treeName;
	html[i++] = "</TD>";
	html[i++] = "<TD style=\"width:16px;height:16px\">";
	html[i++] = AjxImg.getImageHtml("Blank_16");
	html[i++] = "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";
	return html.join("");
};

com_zimbra_social.prototype._getFolderHTML =
function(folder, expandIconId, childExpandIconId, isSubFolder) {
	var html = new Array();
	var i = 0;
	if (this.treeIdAndAccountMap == undefined) {
		this.treeIdAndAccountMap = new Array();
	}
	if (this.treeIdAndSearchMap == undefined) {
		this.treeIdAndSearchMap = new Array();
	}
	html[i++] = "<div class='DwtComposite'>";
	var id = "";
	if (folder.type == "ACCOUNT" || folder.type == "DIRECT_MSGS" || folder.type == "MENTIONS" || folder.type == "SENT_MSGS" || folder.type == "FACEBOOK") {
		id = "socialTreeItem__" + folder.type + "_" + folder.account.type + "_" + folder.account.name;
		this.treeIdAndAccountMap[id] = folder.account;
	} else {
		id = "socialTreeItem__" + folder.type + "_" + Dwt.getNextId();
	}
	if (folder.type == "SEARCH") {
		this.treeIdAndSearchMap[id] = folder.search;
	}
	this.expandIconAndFolderTreeMap[expandIconId].push(id);
	if (isSubFolder) {
		this.expandIconAndFolderTreeMap[childExpandIconId].push(id);
	}
	html[i++] = "<div class='DwtTreeItem' id='" + id + "'>";

	html[i++] = "<TABLE width=100% cellpadding=\"0\" cellspacing=\"0\">";
	html[i++] = "<TR>";
	html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
	if (folder.account.type == "twitter" && folder.type == "ACCOUNT") {
		html[i++] = "<div class=\"ImgNodeExpanded\" id= '" + childExpandIconId + "'/>";
	} else {
		html[i++] = AjxImg.getImageHtml("Blank_16");
	}
	html[i++] = "</TD>";
	if (isSubFolder) {
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
	}
	html[i++] = "<TD style=\"width:16px;height:16px;padding-right:5px\">";
	html[i++] = AjxImg.getImageHtml(folder.icon);
	html[i++] = "</TD>";
	html[i++] = "<TD class='DwtTreeItem-Text' nowrap=''>";
	html[i++] = folder.name;
	html[i++] = "</TD>";
	html[i++] = "<TD style=\"width:16px;height:16px;padding-right:5px\">";
	html[i++] = AjxImg.getImageHtml("Blank_16");
	html[i++] = "</TD>";
	if(folder.type == "SEARCH") {
		html[i++] = "<TD style=\"width:16px;height:16px;padding-right:5px\">";
		html[i++] = "<div class=\"ImgClearSearch\"/>";
		html[i++] = "</TD>";
	}
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</div>";
	html[i++] = "</div>";
	return html.join("");
};

com_zimbra_social.prototype._handleTreeClick =
function(ev) {
	if (AjxEnv.isIE) {
		ev = window.event;
	}
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	var el = dwtev.target;
	var origTarget = dwtev.target;
	if (origTarget.className == "ImgNodeExpanded" || origTarget.className == "ImgNodeCollapsed") {
		var display = "block";
		if (origTarget.className == "ImgNodeExpanded") {
			origTarget.className = "ImgNodeCollapsed";
			display = "none";
		} else {
			origTarget.className = "ImgNodeExpanded";
		}
		var foldersId = this.expandIconAndFolderTreeMap[origTarget.id];
		for (var i = 0; i < foldersId.length; i++) {
			document.getElementById(foldersId[i]).style.display = display;
		}
		return;
	}
	while (el && el.className != "DwtTreeItem") {
		el = el.parentNode;
	}
	if (el == null)
		return;

	var tds = el.getElementsByTagName("td");
	var label = "";
	for (var i = 0; i < tds.length; i++) {
		var td = tds[i];
		if (td.className == "DwtTreeItem-selected" || td.className == "DwtTreeItem-Text") {
			label = AjxEnv.isIE ? td.innerText : td.textContent;
			break;
		}
	}
	if (td.className == "DwtTreeItem-Text") {
		if (this.previousFolderCell) {
			this.previousFolderCell.className = "DwtTreeItem-Text";
			td.className = "DwtTreeItem-selected";
			this.previousFolderCell = td;
		} else {
			td.className = "DwtTreeItem-selected";
			this.previousFolderCell = td;
		}
	}
	var account = "";
	var itemType = "";
	var tableId = "";
	var timer = "";
	if (el.id.indexOf("socialTreeItem__ACCOUNT") == 0) {
		account = this.treeIdAndAccountMap[el.id];
		tableId = this._showCard({headerName:label, type:"ACCOUNT", autoScroll:true});
		this.twitter.getFriendsTimeLine({tableId: tableId, account: account});
		timer = setInterval(AjxCallback.simpleClosure(this.twitter._updateAccountStream, this.twitter, tableId, account), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
		this.tableIdAndAccountMap[tableId] = account;
		account.isDisplayed = true;
	} else if (el.id.indexOf("socialTreeItem__MENTIONS") == 0) {
		account = this.treeIdAndAccountMap[el.id];
		tableId = this._showCard({headerName:label, type:"MENTIONS", autoScroll:true});
		this.twitter.getMentions({tableId: tableId, account: account});
		timer = setInterval(AjxCallback.simpleClosure(this.twitter.getMentions, this.twitter, {tableId: tableId, account: account}), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
		this.tableIdAndAccountMap[tableId] = account;
	} else if (el.id.indexOf("socialTreeItem__DIRECT_MSGS") == 0) {
		account = this.treeIdAndAccountMap[el.id];
		tableId = this._showCard({headerName:label, type:"DIRECT_MSGS", autoScroll:true});
		this.twitter.getDirectMessages({tableId: tableId, account: account});
		timer = setInterval(AjxCallback.simpleClosure(this.twitter.getDirectMessages, this.twitter, {tableId: tableId, account: account}), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
		this.tableIdAndAccountMap[tableId] = account;
	} else if (el.id.indexOf("socialTreeItem__SENT_MSGS") == 0) {
		account = this.treeIdAndAccountMap[el.id];
		tableId = this._showCard({headerName:label, type:"SENT_MSGS", autoScroll:true});
		this.twitter.getSentMessages({tableId: tableId, account: account});
		timer = setInterval(AjxCallback.simpleClosure(this.twitter.getSentMessages, this.twitter, {tableId: tableId, account: account}), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
		this.tableIdAndAccountMap[tableId] = account;
	} else if (el.id.indexOf("socialTreeItem__MANAGE_ACCOUNTS") == 0) {
		this.preferences._showManageAccntsDlg();
	} else if (el.id.indexOf("socialTreeItem__PREFERENCES") == 0) {
		this.preferences._showPreferencesDlg();		
	} else if (el.id.indexOf("socialTreeItem__SEARCH") == 0) {
		var search = this.treeIdAndSearchMap[el.id];
		if(	origTarget.className == "ImgClearSearch") {//delete search
			this.twitter._updateAllSearches(search.name, "delete");
			return;
		}
		var search = this.treeIdAndSearchMap[el.id];
		tableId = this._showCard({headerName:search.name, type:"SEARCH", autoScroll:true});
		sParams = {query:search.name, tableId:tableId, type:"SEARCH"};
		this.twitter.twitterSearch(sParams);
		this.tableIdAndSearchMap[tableId] = search;
		this.twitter._updateAllSearches(label, "on");
		timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
	} else if (el.id.indexOf("socialTreeItem__TREND") == 0) {
		var sParams = {headerName:label, type:"TREND", autoScroll:true};
		tableId = this._showCard(sParams);
		this.twitter.twitterSearch({query:label, tableId:tableId, type:"TREND"});
		timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
	} else if (el.id.indexOf("socialTreeItem__TWEETMEME") == 0) {
		tableId = this._showCard({headerName:label, type:"TWEETMEME", autoScroll:true});
		this.tweetmeme.tweetMemeSearch({query:label, tableId:tableId});
	} else if (el.id.indexOf("socialTreeItem__DIGG") == 0) {
		tableId = this._showCard({headerName:label, type:"DIGG", autoScroll:true});
		this.digg.diggSearch({query:label, tableId:tableId});
	} else if (el.id.indexOf("socialTreeItem__FACEBOOK") == 0) {
		tableId = this._showCard({headerName:"facebook", type:"FACEBOOK",autoScroll:true});
		account = this.treeIdAndAccountMap[el.id];
		this.facebook._fbGetStream(tableId, account);
		timer = setInterval(AjxCallback.simpleClosure(this.facebook._updateFacebookStream, this.facebook, tableId, account), 400000);
		this.tableIdAndTimerMap[tableId] = timer;
		this.tableIdAndAccountMap[tableId] = account;
	}
};

com_zimbra_social.prototype._treeListener =
function(ev) {
	var txt = ev.item._text;
	if (ev.detail == 1)
		return;

	this._showStream();
};

com_zimbra_social.prototype._getShortNameFromName =
function(name) {
	for (var i = 0; i < com_zimbra_social.folders.length; i++) {
		var fldr = com_zimbra_social.folders[i];
		if (fldr.n == name) {
			return fldr.sn;
		}
	}
	return "";
};

com_zimbra_social.prototype._getIdFromName =
function(name) {
	for (var i = 0; i < com_zimbra_social.folders.length; i++) {
		var fldr = com_zimbra_social.folders[i];
		if (fldr.n == name) {
			return fldr.id;
		}
	}
	return "";
};

com_zimbra_social.prototype.showAppView =
function() {
	//get list of twitter searches, facebook stuffs and show the view
	//-check if the user has turned on twitter and facebook
	this.app.setContent(this._constructSkin());
	this._view = this.app.getController().getView();
	this._view.addControlListener(new AjxListener(this, this._resizeHandler));//add resize handler
	this._setMainCardHeight();
	this._dontAutoScroll = true;
	this._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:true});

	this.addTwitterSearchWidget();
	this._addTweetButtons();
	this._loadInformation();
	this._dontAutoScroll = false;
};

com_zimbra_social.prototype.addTwitterSearchWidget =
function() {
	var html = new Array();
	var idx = 0;
	html[idx++] = "<DIV >";
	html[idx++] = "<TABLE cellpadding=0 cellspacing=0 valign='middle'><TR><TD width=90% valign='middle'>";
	html[idx++] = "<label  valign='middle' style=\"font-size:14px;color:black;font-weight:bold\" id='social_whatareyoudoingLabel'>What are you doing?";
	html[idx++] = "</label>";
	html[idx++] = "</TD><TD valign='middle'>";
	html[idx++] = "<input   style=\"width:200px;\" type=\"text\" autocomplete=\"off\" id=\"social_searchField\" rows=\"2\" cols=\"40\"></input>";
	html[idx++] = "</TD><TD valign='middle'>";
	html[idx++] = "<div  valign='middle' id='social_searchButton' />";
	html[idx++] = "</TD>";
	html[idx++] = "<TD width=16px valign='middle'>";
	html[idx++] =  AjxImg.getImageHtml("Blank_16");
	html[idx++] = "</TD>";
	html[idx++] = "<TD valign='middle'>";
	html[idx++] = "<div  id='social_shortenUrlButton' />";
	html[idx++] = "</TD>";
	html[idx++] = "</TR></TABLE>";
	html[idx++] = "</DIV>";
	var toolbar = this.app.getToolbar();
	toolbar.getHtmlElement().innerHTML = html.join("");
};

com_zimbra_social.prototype._postToTweetOrFB =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode != undefined && event.keyCode != 13) {//if not enter key, simply update counter
		this.showNumberOfLetters(ev);
		return;
	}
	var isDM = false;
	var noAccountSelected = true;
	var message = document.getElementById("social_statusTextArea").value;
	if (message > 140)
		return;

	for (var id in this.allAccounts) {
		var account = this.allAccounts[id];
		if (account.type == "twitter") {
			this.twitter.postToTwitter(account, message);
			noAccountSelected = false;
		} else if (account.__on == "true" && account.type == "facebook" && !isDM) {
			noAccountSelected = false;
			this.facebook._publishToFacebook({account:account, message:message});
		}
	}
	if (noAccountSelected) {
		appCtxt.getAppController().setStatusMsg("Please Select an account to send", ZmStatusView.LEVEL_WARNING);
	}
};

com_zimbra_social.prototype.createCardView =
function(tableId, jsonObj, type) {
	var html = [];
	var i = 0;

	var unReadCount = 0;
	var pId = "";

	var rowIdsToMarkAsRead = new Array();
	if (jsonObj == undefined)
		return;

	if (jsonObj.length == 0) {
		document.getElementById(tableId).innerHTML = "<br/><br/><div width=90% align=center><label style=\"color:#0000FF;font-weight:bold;font-size:12px\">No data found</label></div>";
		return;
	}
	for (var k = 0; k < jsonObj.length; k++) {
		var obj = jsonObj[k];
		var user = "";
		var profile_image_url = "";
		var screen_name = "";
		var source = "";
		var created_at = "";
		var imageAnchor = "";
		var text = "";
		var tweetcount = "";
		var followId = "";
		var notFollowing = true;
		var userId = "";
		var diggCount = "";
		if (k == 0 && (type == "SEARCH" || type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS")) {
			this.tableIdAndResultsMap[tableId] = jsonObj;
			this.tableIdAndPostIdMap[tableId] = obj.id;
			if (type == "SEARCH") {
				var search = this.tableIdAndSearchMap[tableId];
				pId = search.pId ? search.pId : "";
			} else if (type == "ACCOUNT") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__postId ? accnt.__postId : "";
			}else if (type == "MENTIONS") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__mId ? accnt.__mId : "";
			}else if (type == "DIRECT_MSGS") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__dmId ? accnt.__dmId : "";
			}else if (type == "SENT_MSGS") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__smId ? accnt.__smId : "";
			}

		}
		var rowId = Dwt.getNextId();
		if (pId && obj.id <= pId) {
			rowIdsToMarkAsRead.push(rowId);
		} else {
			unReadCount++;
		}

		if (type == "SEARCH" || type == "TREND") {//for both search and trends
			screen_name = obj.from_user;
			created_at = obj.created_at;
			text = " " + obj.text;
			userId = obj.from_user_id;
			source = AjxStringUtil.htmlDecode(obj.source).replace(/&quot;/g, "\"");
			imageAnchor = "<TD width=48px height=48px align='center' class='social_accountBg'> ";
			imageAnchor = imageAnchor + "<a  href=\"http://twitter.com/" + screen_name + "\" target=\"_blank\" style=\"color:white\">";
			imageAnchor = imageAnchor + "<img height=\"48\" width=\"48\" src=\"" + obj.profile_image_url + "\" />";
			imageAnchor = imageAnchor + "</a>";
			imageAnchor = imageAnchor + "</td>";
		} else	 if (type == "TWEETMEME") {
			screen_name = "tweetmeme";
			created_at = obj.created_at;
			text = " " + obj.title + " " + obj.alias;
			source = "tweetmeme";
			tweetcount = obj.tweetcount;
			imageAnchor = "<TD > ";
			imageAnchor = imageAnchor + "<a  href=\"" + obj.url + "\" target=\"_blank\" style=\"color:gray\">";
			imageAnchor = imageAnchor + "<table><tr>";
			imageAnchor = imageAnchor + "<td class='social_tweetMemeBg' width=48px height=48px align='center' valign='middle'>";
			imageAnchor = imageAnchor + tweetcount + "</td></tr><tr><td class='social_tweetMemeRetweetBg'>retweets</td></tr></table>";
			imageAnchor = imageAnchor + "</a>";
			imageAnchor = imageAnchor + "</td>";
		}  else	 if (type == "DIGG") {
			diggCount = obj.diggs;
			text = [" ", obj.title, " ", obj.link].join("");
			screen_name = "digg";
			source = "digg";
			created_at = obj.promote_date;
			imageAnchor = "<TD>";
			imageAnchor = imageAnchor + "<a  href=\"" + obj.href + "\" target=\"_blank\" style=\"color:gray\">";
			imageAnchor = imageAnchor + "<div class='social_diggBg'>"; 
			imageAnchor = imageAnchor + "<table width=100% cellpadding=0 cellspacing=0><tr><td style='font-size:14px;font-weight:bold' align=center width=100%>"+diggCount + "</td></tr><tr><td align=center valign=middle  width=100%>diggs</td></tr></tablee></td></tr></table>";
			imageAnchor = imageAnchor + "</div>";
			imageAnchor = imageAnchor + "</a>";
			imageAnchor = imageAnchor + "</td>";

		} else if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
			user = obj.user ? obj.user : obj.sender; //sender.id is returned in Direct_msgs(instead of user.id)
			userId = user.id;
			screen_name = user.screen_name;
			created_at = obj.created_at;
			text = " " + obj.text;
			if (obj.source)
				source = AjxStringUtil.htmlDecode(obj.source).replace(/&quot;/g, "\"");
			else
				source = "web";

			imageAnchor = "<TD width=48px height=48px align='center' class='social_accountBg'> ";
			imageAnchor = imageAnchor + "<a id='" + this._getAccountLinkId(screen_name, tableId) + "' class='FakeAnchor' style=\"color:white\">";
			imageAnchor = imageAnchor + "<img height=\"48\" width=\"48\" src=\"" + user.profile_image_url + "\" />";
			imageAnchor = imageAnchor + "</a>";
			imageAnchor = imageAnchor + "</td>";
			notFollowing = user.following == null;
			followId = userId;
		} else if (type == "FACEBOOK") {
			var user = this.facebook._getFacebookProfile(obj.actor_id);
			screen_name = user.name;
			created_at = obj.created_time;
			if (obj.message != "") {
				text = obj.message;
			} else if (obj.attachment.name != undefined) {
				text = obj.attachment.name;
			}

			if (obj.attribution == null) {
				source = "facebook";
			} else {
				source = obj.attribution;
			}

			imageAnchor = "<TD width=48px height=48px align='center'> ";
			imageAnchor = imageAnchor + "<div class='social_accountBg'><a href='" + user.url + "' target='_blank' style=\"color:white\">";
			imageAnchor = imageAnchor + "<img height=\"48\" width=\"48\" src=\"" + user.pic_square + "\" />";
			imageAnchor = imageAnchor + "</a></div>";
			imageAnchor = imageAnchor + "</td>";
			notFollowing = user.following == null;
			followId = userId;
		} else {
			return;
		}
		if (source.indexOf("<a ") >= 0) {
			source = source.replace("<a ", "<a target=\"_blank\" ");
		}
		var parsedDate = "";
		if (type != "FACEBOOK" && type != "DIGG") {
			parsedDate = Date.parse(created_at);
		} else {
			parsedDate = created_at * 1000;
		}
		var tmpTime = ( (new Date()).getTime() - parsedDate) / (60 * 1000);
		tmpTime = Math.round(tmpTime);
		var timeStr = "";
		if (tmpTime < 1) {
			timeStr = tmpTime + " seconds ago";
		} else if (tmpTime < 60) {
			timeStr = tmpTime + " minutes ago";
		} else if (tmpTime >= 60) {
			tmpTime = Math.round(tmpTime / 60);
			if (tmpTime < 24) {
				if(tmpTime == 1)
					timeStr = "about " + tmpTime + " hour ago";
				else
					timeStr = "about " + tmpTime + " hours ago";
			} else {
				timeStr = (new Date(parsedDate)).toString().split("GMT")[0];
			}
		}
		created_at = timeStr;

		//pass it through zimlets to get url, phone, emoticons etc
		var div = document.createElement("div");
		div.innerHTML = text;
		this._objectManager.findObjectsInNode(div);
		var zimletyFiedTxt = div.innerHTML;
		zimletyFiedTxt = this._replaceHash(zimletyFiedTxt);
		zimletyFiedTxt = this._replaceAt(zimletyFiedTxt, userId, tableId, screen_name);

		html[i++] = "<div id='" + rowId + "' class='social_rowsDiv'>";
		html[i++] = "<TABLE width=100%>";
		html[i++] = "<TR>";
		html[i++] = imageAnchor;
		html[i++] = "<TD style=\"font-size:12px;font-family:'Lucida Grande',sans-serif;\">";
		if (type != "TWEETMEME" && type != "FACEBOOK") {
			html[i++] = "<b> <a href=\"#\" style=\"color:#0000CC\" id='" + this._getAccountLinkId(screen_name, tableId) + "'>" + screen_name + ":</a></b>" + zimletyFiedTxt;
		} else {
			html[i++] = "<label style=\"color:#666666;font-weight:bold\">" + screen_name + ": </label>" + zimletyFiedTxt;
		}
		//html[i++] = this._getAdditionalFBMessageInfo(obj);

		html[i++] = "</TD>";
		html[i++] = "</TR>";

		if (type == "FACEBOOK") {
			var additionalInfo = this._getAdditionalFBMessageInfo(obj);
			if (additionalInfo.html != "") {
				html[i++] = "<TR>";
				html[i++] = "<TD colspan=2>";
				html[i++] = additionalInfo.html;
				html[i++] = "</TD>";
				html[i++] = "</TR>";
			}
			html[i++] = "<TR>";
			html[i++] = "<TD align=center colspan=2>";
			html[i++] = "<DIV id='" + additionalInfo.commentBoxId + "' style='display:none' />";
			html[i++] = "</TD>";
			html[i++] = "</TR>";
		}

		html[i++] = "<TR>";
		html[i++] = "<TD colspan=2 style=\"color:gray\">";
		html[i++] = "<table width=100%>";
		html[i++] = "<TR>";
		//if(type == "ACCOUNT" && notFollowing){
		//	html[i++] = "<TD width=85% style=\"color:gray\">";
		//}
		if (type == "TWEETMEME" || type == "DIGG") {
			html[i++] = "<TD width=95% style=\"color:gray\">";
		} else {
			html[i++] = "<TD width=90% style=\"color:gray\">";
		}
		html[i++] = created_at + " from " + source;
		html[i++] = "</td>";
		html[i++] = "<td colspan=2 >";

		if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
			if(this.twitterAccountNames == undefined) {
				this.twitterAccountNames = new Array();

					for (var accntId in this.allAccounts) {
						var accnt = this.allAccounts[accntId];
						if(accnt.type == "twitter") {
							this.twitterAccountNames[accnt.name] = true;
						}
					}
			}

			if(this.twitterAccountNames[screen_name]) {
				html[i++] = "<a href=\"#\" style=\"color:gray\" id='" + this._gettwitterDeleteLinkId(obj.id, tableId) + "'>delete</a>&nbsp;&nbsp;";
			}		
		}

		if (type == "ACCOUNT" || type == "SEARCH" || type == "TREND") {
			html[i++] = "<a href=\"#\" style=\"color:gray\" id='" + this._gettwitterDMLinkId("d @" + screen_name) + "'>dm</a>&nbsp;&nbsp;";
		}

		html[i++] = "<a href=\"#\" style=\"color:gray\" id='" + this._gettwitterRetweetLinkId("RT @" + screen_name + text) + "'>retweet</a>&nbsp;&nbsp;";

		if (type != "TWEETMEME" && type != "FACEBOOK" && type != "DIGG") {
			html[i++] = "<a href=\"#\" style=\"color:gray\" id='" + this._gettwitterReplyLinkId("@" + screen_name) + "'>reply</a>";
		}
		if (type == "FACEBOOK") {
			html[i++] = "<a href=\"#\" style=\"color:gray\" id='" + this._getFacebookCommentLinkId(obj.post_id, tableId) + "'>comment</a>";
		}
		html[i++] = "</td>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</TD>";
		html[i++] = "</TR>";

		html[i++] = "<TR>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</DIV>";
	}
	var showOlderMsgsId = Dwt.getNextId();
	html[i++] = "<br/><div width=80% align=center><a id='" + showOlderMsgsId + "' href=\"#\" style=\"color:#0000FF;font-weight:bold;font-size:12px;display:none\" >";
	html[i++] = "Click here to see older messages";
	html[i++] = "</a></div>";
	document.getElementById(tableId).innerHTML = html.join("");
	if (type == "SEARCH" || type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
		this._setCardRowsAsRead(rowIdsToMarkAsRead, showOlderMsgsId);
		this._setCardUnreadCount(tableId, unReadCount);
		if (rowIdsToMarkAsRead.length > 0) {
			var showOlderItemsLink = document.getElementById(showOlderMsgsId);
			showOlderItemsLink.style.display = "block";
			showOlderItemsLink.onclick = AjxCallback.simpleClosure(this._showCardOlderMsgs, this, rowIdsToMarkAsRead, showOlderItemsLink);
		}
	}
	this._addRetweetLinkHandlers();
	this._addReplyLinkHandlers();
	this._addDMLinkHandlers();
	this._addAccountLinkHandlers();
	this._addHashHandlers();
	this._addFbCommentLinkHandlers();
	this._addTwitterDeleteLinkHandlers();
};

com_zimbra_social.prototype._setCardRowsAsRead =
function(rowIdsToMarkAsRead, showOlderMsgsId) {
	for (var i = 0; i < rowIdsToMarkAsRead.length; i++) {
		var row = document.getElementById(rowIdsToMarkAsRead[i]);
		row.style.display = "none";
	}
};

com_zimbra_social.prototype._showCardOlderMsgs =
function(rowIdsToMarkAsRead, showOlderItemsLink) {
	for (var i = 0; i < rowIdsToMarkAsRead.length; i++) {
		var row = document.getElementById(rowIdsToMarkAsRead[i]);
		if (row)
			row.style.display = "block";
	}
	showOlderItemsLink.style.display = "none";
};

com_zimbra_social.prototype._getAdditionalFBMessageInfo =
function(obj) {
	var html = new Array();
	var i = 0;
	if (obj.description) {
		html[i++] = obj.description;
	}
	var commentBoxId = "social_fbcommentBoxId_" + Dwt.getNextId();
	this._FBPostIdAndCommentboxMap[obj.post_id] = commentBoxId;
	if (obj.attachment && obj.attachment.media != undefined && obj.attachment.media.length > 0) {
		var medias = obj.attachment.media;
		html[i++] = "<table width=100%>";
		var counter = 0;
		var maxItems = 2;
		html[i++] = "<TR>";

		for (var j = 0; j < medias.length; j++) {
			var media = medias[j];

			html[i++] = "<TD valign='top' height=\"100\" width=\"100\">";

			html[i++] = "<table cellspacing='0' cellpadding='0' width=100%>";
			html[i++] = "<TR>";
			html[i++] = "<TD width='115px'  valign='top'>";
			html[i++] = "<a  href=\"" + media.href + "\" target=\"_blank\"  style=\"color:gray\">";
			html[i++] = "<img  width='115px'  src=\"" + media.src + "\" />";
			html[i++] = "</a>";
			html[i++] = "</TD>";
			html[i++] = "</TR><TR><TD>";
			html[i++] = media.alt;
			html[i++] = "<TD></TR>";
			html[i++] = "</TABLE>";
			html[i++] = "</TD>";
			if (counter == maxItems) {
				html[i++] = "</TR><TR>";
				counter = -1;
			}
			counter++;
		}
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "<BR/>";
	}
	if (obj.comments && obj.comments.comment_list != undefined) {
		var comments = obj.comments.comment_list;

		for (var j = 0; j < comments.length; j++) {
			var comment = comments[j];
			var profile = this.facebook._getFacebookProfile(comment.fromid);
			html[i++] = "<table width=100%>";
			html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
			html[i++] = AjxImg.getImageHtml("Blank_16");
			html[i++] = "</TD>";
			html[i++] = "<TD>";
			html[i++] = "<DIV class='social_FBCommentRow'>";
			html[i++] = "<table width=100%>";
			html[i++] = "<TR>";
			html[i++] = "<TD width=32px>";
			html[i++] = "<div width=32px height=32px align='center' class='social_accountBg'> ";
			html[i++] = "<a  href=\"" + profile.url + "\" target=\"_blank\" >";
			html[i++] = "<img height=\"32\" width=\"32\" src=\"" + profile.pic_square + "\" />";
			html[i++] = "</a>";
			html[i++] = "</div>";
			html[i++] = "</TD><TD>";
			html[i++] = comment.text + "<br><label  style=\"color:gray\"> - " + profile.name + "</label>";
			html[i++] = "</TD></TR>";
			html[i++] = "</TABLE>";
			html[i++] = "</DIV>";
			html[i++] = "</TD>";
			html[i++] = "</table>";
		}
	}
	
	var str = html.join("");
	if (str == "")
		return  {html:"", commentBoxId:commentBoxId};
	else
		return {html:"<BR/>" + str, commentBoxId:commentBoxId};
};

com_zimbra_social.prototype._gettwitterRetweetLinkId =
function(rt) {
	var id = "social_retweetLink_" + Dwt.getNextId();
	this._allReweetLinks[id] = {hasHandler:false, rt:rt};
	return id;
};

com_zimbra_social.prototype._gettwitterDMLinkId =
function(dm) {
	var id = "social_DMLink_" + Dwt.getNextId();
	this._allDMLinks[id] = {hasHandler:false, dm:dm};
	return id;
};

com_zimbra_social.prototype._gettwitterDeleteLinkId =
function(postId, tableId) {
	var id = "social_twitterDeleteLink_" + Dwt.getNextId();
	this._allTwitterDeleteLinks[id] = {hasHandler:false, account:this.tableIdAndAccountMap[tableId], tableId:tableId, postId:postId};
	return id;
};
com_zimbra_social.prototype._getAccountLinkId =
function(screen_name, tableId) {
	var id = "social_accountsLink_" + Dwt.getNextId();
	this._allAccountsLinks[id] = {hasHandler:false, tableId:tableId, screen_name:screen_name};
	return id;
};

com_zimbra_social.prototype._getFacebookCommentLinkId =
function(postId, tableId) {
	var id = "social_FaceBookCommentLink_" + Dwt.getNextId();
	this._allFacebookCommentsLinks[id] = {hasHandler:false, tableId:tableId, postId:postId};
	return id;
};

com_zimbra_social.prototype._gettwitterReplyLinkId =
function(reply) {
	var id = "social_replyLink_" + Dwt.getNextId();
	this._allReplyLinks[id] = {hasHandler:false, reply:reply};
	return id;
};

com_zimbra_social.prototype._gettwitterFollowLinkId =
function(userId, tableId) {
	var id = "social_followLink_" + Dwt.getNextId();
	this._allFollowLinks[id] = {hasHandler:false, userId:userId, tableId:tableId};
	return id;
};
com_zimbra_social.prototype._addRetweetLinkHandlers =
function() {
	for (var id in this._allReweetLinks) {
		var obj = this._allReweetLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.addRetweetText, this, obj.rt);
			obj.hasHandler = true;
		}
	}
};
com_zimbra_social.prototype._addDMLinkHandlers =
function() {
	for (var id in this._allDMLinks) {
		var obj = this._allDMLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.addDMText, this, obj.dm);
			obj.hasHandler = true;
		}
	}
};
com_zimbra_social.prototype._addTwitterDeleteLinkHandlers =
function() {
	for (var id in this._allTwitterDeleteLinks) {
		var obj = this._allTwitterDeleteLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.twitter.deletePost, this.twitter, obj);
			obj.hasHandler = true;
		}
	}
};


com_zimbra_social.prototype._addFbCommentLinkHandlers =
function() {
	for (var id in this._allFacebookCommentsLinks) {
		var obj = this._allFacebookCommentsLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.displayFbCommentWidget, this, {postId:obj.postId, tableId:obj.tableId, linkId:id});
			obj.hasHandler = true;
		}
	}
};

com_zimbra_social.prototype._addReplyLinkHandlers =
function() {
	for (var id in this._allReplyLinks) {
		var obj = this._allReplyLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.addReplyText, this, obj.reply);
			obj.hasHandler = true;
		}
	}
};

com_zimbra_social.prototype._addAccountLinkHandlers =
function() {
	for (var id in this._allAccountsLinks) {
		var obj = this._allAccountsLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.twitter.showUserProfile, this.twitter, obj.screen_name, obj.tableId);
			obj.hasHandler = true;
		}
	}
};

com_zimbra_social.prototype._addHashHandlers =
function() {
	for (var id in this._allHashLinks) {
		var obj = this._allHashLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this._handleHashLinks, this, obj.word);
			obj.hasHandler = true;
		}
	}
};

com_zimbra_social.prototype._handleHashLinks =
function(query) {
	document.getElementById("social_searchField").value = query;
	this._twitterSearchBtnListener();
};

com_zimbra_social.prototype._replaceHash =
function(text) {
	var re = /([^a-zA-Z0-9_-]#[a-zA-Z0-9_-]+)/gm;
	var newStr = "";
	var start = 0;
	while (match = re.exec(text)) {
		var word = match[0];
		var end = re.lastIndex;
		var part = text.substring(start, end);
		var id = "social_hashlink_" + Dwt.getNextId();
		var url = ["<a  href=\"#\" id='", id, "'>", word, "</a>"].join("");
		newStr = newStr + part.replace(word, url);
		this._allHashLinks[id] = {hasHandler:false, word:word};
		start = end;
	}
	var extraStr = "";
	if(start < text.length) {
		extraStr = text.substring(start, text.length)
	}
	return newStr + extraStr;

};

com_zimbra_social.prototype._replaceAt =
function(text, userId, tableId, screen_name) {
	var re = /([^a-zA-Z0-9_-]@[a-zA-Z0-9_-]+)/gm;
	var newStr = "";
	var start = 0;
	while (match = re.exec(text)) {
		var word = match[0];
		var end = re.lastIndex;
		var part = text.substring(start, end);
		var id = this._getAccountLinkId(AjxStringUtil.trim(word.replace("@","")), tableId);
		var url = ["<a  href=\"#\" id='", id, "'>", word, "</a>"].join("");
		newStr = newStr + part.replace(word, url);
		start = end;
	}
	var extraStr = "";
	if(start < text.length) {
		extraStr = text.substring(start, text.length)
	}
	return newStr + extraStr;
};

com_zimbra_social.prototype.addRetweetText = function(rt) {
	this.addReplyText(rt);
};
com_zimbra_social.prototype.addDMText = function(dm) {
	this.addReplyText(dm);
};

com_zimbra_social.prototype.displayFbCommentWidget = function(params) {
	var html = new Array();
	var i = 0;
	var commentBtnId = "social_fbcommentbtn_" + Dwt.getNextId();
	var commentFieldId = "social_fbcommentField_" + Dwt.getNextId();
	var commentBoxId = this._FBPostIdAndCommentboxMap[params.postId];
	html[i++] = "<Textarea column=40 rows=\"5\" cols=\"20\" id='" + commentFieldId + "' style=\"width: 90%;height:30px;border:1px solid #BDC7D8;color:gray\">write a comment...</TextArea><BR/>";
	html[i++] = "<div  align=center id='" + commentBtnId + "' />";

	var div = document.getElementById(commentBoxId);
	div.style.display = "block";
	div.innerHTML = html.join("");
	clearInterval(this.tableIdAndTimerMap[params.tableId]);//clear timer to make sure we dont clear comments
	params["commentBoxId"] = commentBoxId;
	params["commentFieldId"] = commentFieldId;
	params["commentBtnId"] = commentBtnId;
	this.addFbCommentsHandlers(params);
};


com_zimbra_social.prototype.addFbCommentsHandlers = function(params) {
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText("comment");
	btn.setImage("social_facebookIcon");
	btn.addSelectionListener(new AjxListener(this.facebook, this.facebook._addFBComment, params));
	document.getElementById(params.commentBtnId).appendChild(btn.getHtmlElement());
	var field = document.getElementById(params.commentFieldId);
	params["field"] = field;
	field.onfocus = AjxCallback.simpleClosure(this._handleAddCommentField, this, params);
	field.onblur = AjxCallback.simpleClosure(this._handleAddCommentField, this, params);
};

com_zimbra_social.prototype._handleAddCommentField = function(params, ev) {
	var event = ev || window.event;

	var field = params.field;
	if (field.value == "write a comment...") {
		field.value = "";
		field.style.color = "black";
	}
	if (event.type == "blur" && field.value == "" || field.value == "write a comment...") {
		document.getElementById(params.commentBoxId).style.display = "none";
	}
};

com_zimbra_social.prototype.addReplyText = function(val) {
	var statusField = document.getElementById("social_statusTextArea");
	statusField.value = val + " ";//allow a space
	statusField.focus();
	this.showNumberOfLetters();
};

com_zimbra_social.prototype.getAllAccountsAsString = function() {
	var str = "";
	for (var accntId in this.allAccounts) {
		var accnt = this.allAccounts[accntId];
		var val = "";
		for (var name in accnt) {
			if (name == "raw" || name == "isDisplayed" || name == "offline_access" || name == "publish_stream" || name == "read_stream") {
				continue;
			}
			if (val == "") {
				val = name + "=" + accnt[name];
			} else {
				val = val + "&" + name + "=" + accnt[name];
			}
		}

		if (str == "") {
			str = val;
		} else {
			str = str + "::" + val;
		}
	}
	return str;
};

com_zimbra_social.prototype.loadAllAccountsFromDB = function() {
	var allAccnts = this.getUserProperty("social_AllTwitterAccounts");
	if (allAccnts == "" || allAccnts == undefined) {
		return;
	}
	var arry = allAccnts.split("::");
	for (var i = 0; i < arry.length; i++) {
		var accntStr = arry[i];
		if (accntStr.indexOf("__type=twitter") > 0) {
			this.twitter.manageTwitterAccounts(accntStr);
		} else if (accntStr.indexOf("__type=facebook") > 0) {
			this.facebook.manageFacebookAccounts(accntStr);
		}
	}
};

com_zimbra_social.prototype._updateAllWidgetItems =
function(params) {
	if (params.updateSearchTree) {
		this.tSearchFolders = new Array();
		for (var i = 0; i < this.twitter.allSearches.length; i++) {
			var search = this.twitter.allSearches[i];
			this.tSearchFolders.push({name:search.name, icon:"Search", account:"", type:"SEARCH", search:search});
		}
	}

	if (params.updateTrendsTree) {
		this.tTrendsFolders = new Array();
		for (var name in this.twitter.allTrends) {
			this.tTrendsFolders.push({name:name, icon:"social_trendIcon", account:"", type:"TREND"});
		}
	}
	if (params.updateTweetMemeTree) {
		this.tTweetMemeFolders = new Array();
		for (var i = 0; i < this.tweetmeme.allTweetMemeCats.length; i++) {
			this.tTweetMemeFolders.push({name:this.tweetmeme.allTweetMemeCats[i].name, icon:"social_tweetMemeIcon", account:"", type:"TWEETMEME"});
		}
	}
	if (params.updateDiggTree) {
		this.tDiggFolders = new Array();
		for (var i = 0; i < this.digg.allDiggCats.length; i++) {
			this.tDiggFolders.push({name:this.digg.allDiggCats[i].name, icon:"social_diggIcon", account:"", type:"DIGG"});
		}
	}

	if (params.updateSystemTree) {
		this.systemFolders = new Array();

		for (var id in this.allAccounts) {
			var account = this.allAccounts[id];
			if (account.__type == "twitter") {
				this.systemFolders.push({name:account.name, icon:"social_twitterIcon", account:account, type:"ACCOUNT"});
			} else if (account.__type == "facebook") {
				this.systemFolders.push({name:account.name, icon:"social_facebookIcon",  account:account, type:"FACEBOOK"});
			}
		}
		this.systemFolders.push({name:"Add/Remove Accounts", icon:"Group", account:"", type:"MANAGE_ACCOUNTS"});
		//this.systemFolders.push({name:"Zimlet Preferences", icon:"Preferences", account:"", type:"PREFERENCES"});
	}
	if (params.updateSearchTree || params.updateSystemTree || params.updateTrendsTree || params.updateTweetMemeTree || params.updateDiggTree) {
		this._createTreeView();
	}
	if (params.updateAccntCheckboxes) {
		document.getElementById("social_updateToCell").innerHTML = this._addUpdateToCheckboxes();
		this._addAccountCheckBoxListeners();
		for (var id in this.allAccounts) {
			var account = this.allAccounts[id];
			if (account.isDisplayed)
				continue;
			if (account.type == "twitter") {
				var tableId = this._showCard({headerName:account.name, type:"ACCOUNT", autoScroll:false});
				this.twitter.getFriendsTimeLine({tableId: tableId, account: account});
			} else if (account.type == "facebook") {
				var tableId = this._showCard({headerName:account.name, type:"FACEBOOK", autoScroll:false});
				this.facebook._fbGetStream(tableId, account);
			}
			account.isDisplayed = true;
			if (account.type == "twitter") {
				var timer = setInterval(AjxCallback.simpleClosure(this.twitter._updateAccountStream, this.twitter, tableId, account), 400000);
			} else if (account.type == "facebook") {
				var timer = setInterval(AjxCallback.simpleClosure(this.facebook._updateFacebookStream, this.facebook, tableId, account), 400000);
			}
			this.tableIdAndTimerMap[tableId] = timer;
			this.tableIdAndAccountMap[tableId] = account;

		}

	}
	if (params.searchCards) {
		if (this.tableIdAndSearchMap == undefined) {
			this.tableIdAndSearchMap = new Array();
		}
		for (var i = 0; i < this.twitter.allSearches.length; i++) {
			var search = this.twitter.allSearches[i];
			if (search.axn == "on") {
				var label = search.name;
				var tableId = this._showCard({headerName:label, type:"SEARCH", autoScroll:false});
				this.tableIdAndSearchMap[tableId] = search;
				var sParams = {query:label, tableId:tableId, type:"SEARCH"};
				this.twitter.twitterSearch(sParams);
				var timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			}
		}
	}
};
