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

function com_zimbra_social() {
}

com_zimbra_social.prototype = new ZmZimletBase();
com_zimbra_social.prototype.constructor = com_zimbra_social;

com_zimbra_social.prototype.init =
function() {
	this.initializeVariables();

	this._createsocialApp();
};

com_zimbra_social.prototype.initializeToolbar =
function(app, toolbar, controller, view) {

	if (!this.preferences.social_pref_toolbarButtonOn)
		return;

	if (view == ZmId.VIEW_CONVLIST ||
	    view == ZmId.VIEW_CONV ||
	    view == ZmId.VIEW_TRAD)
	{
		var buttonIndex = -1;
		for (var i = 0, count = toolbar.opList.length; i < count; i++) {
			if (toolbar.opList[i] == ZmOperation.VIEW_MENU) {
				buttonIndex = i + 1;
				break;
			}
		}
		ZmMsg.socialBtnLabel = "Socialize";
		var buttonArgs = {
			text    : ZmMsg.socialBtnLabel,
			tooltip: "Allows you to share email or RSS contents via twitter or facebook",
			index: buttonIndex,
			image: "social_greenAppIcon"
		};
		var button = toolbar.createOp("SOCIAL_ZIMLET_TOOLBAR_BUTTON", buttonArgs);
		button.addSelectionListener(new AjxListener(this.miniDlg, this.miniDlg._buttonListener, [controller]));
	}
};

com_zimbra_social.prototype.toggleFields =
function() {
	if (this.miniDlg.miniDlgON) {
		this.updateField = document.getElementById("social_statusTextArea_miniDlg");
		this.whatAreYouDoingField = "NOT_PRESENT";
		this.nuLettersAllowedField = document.getElementById("social_numberOfLettersAllowed_miniDlg");
		this.updateButton = this.miniDlg.updateButton_miniDlg;
		this.undoShortenUrlDiv = document.getElementById('social_undoShortenURLDIV_miniDlg');
		this.autoShortenCheckbox = document.getElementById('social_autoShortenCheckbox_miniDlg');
		this.shortenUrlButtonDiv = document.getElementById("social_shortenUrlButtonDIV_miniDlg");
		this.undoShortenURLLink = document.getElementById("social_undoShortenURLLink_miniDlg");
	} else {
		this.updateField = document.getElementById("social_statusTextArea");
		this.whatAreYouDoingField = document.getElementById("social_whatareyoudoingLabel");
		this.nuLettersAllowedField = document.getElementById("social_numberOfLettersAllowed");
		this.updateButton = this.updateButton_main;
		this.undoShortenUrlDiv = document.getElementById('social_undoShortenURLDIV');
		this.autoShortenCheckbox = document.getElementById('social_autoShortenCheckbox');
		this.shortenUrlButtonDiv = document.getElementById("social_shortenUrlButtonDIV");
		this.undoShortenURLLink = document.getElementById("social_undoShortenURLLink");

	}
};

//------------

com_zimbra_social.prototype.updateUIWidgets =
function() {
	this.autoShortenCheckbox.checked = this.preferences.social_pref_autoShortenURLOn;
	if (this.autoShortenCheckbox.checked) {

		this.shortenUrlButtonDiv.style.display = "none";
	} else {
		this.shortenUrlButtonDiv.style.display = "block";
	}
	var callback = AjxCallback.simpleClosure(this._saveAutoShortenUrlPref, this);
	Dwt.setHandler(this.autoShortenCheckbox, DwtEvent.ONCLICK, callback);

	var callback = AjxCallback.simpleClosure(this._handleUndoShortenLink, this);
	this.undoShortenURLLink.onclick = callback;
};

com_zimbra_social.prototype.initializeVariables =
function() {
	if (this._variablesInitialized != undefined)//initialize only once
		return;

	ZmZimletBase.prototype.init.apply(this, arguments);
	this.tableIdAndTimerMap = new Array();
	this.tableIdAndAccountMap = new Array();
	this.tableIdAndSearchMap = new Array();
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
	this._allFBLikeLinks = new Array();
	this._allFBMoreCommentsLinks = new Array();
	this.cardInfoSectionIdsArray = new Array();//every card's data div (used to reset its height when window is resized)
	//this._migrateOldPropsNames();//from tweetzi to social
	this.isTextPasted = false;
	this._autoShorten = true;

	this.preferences = new com_zimbra_socialPreferences(this);
	this.twitter = new com_zimbra_socialTwitter(this, this.preferences);
	this.facebook = new com_zimbra_socialFacebook(this);
	this.tweetmeme = new com_zimbra_socialTweetMeme(this);
	this.digg = new com_zimbra_socialDigg(this);
	this.miniDlg = new com_zimbra_socialMiniDlg(this);

	this.loadAllAccountsFromDB();
	this._objectManager = new ZmObjectManager(new DwtComposite(this.getShell()));

	this.shortnersRegex = /bit.ly|tinyurl.com|is.gd|tr.im|ow.ly|cli.gs|u.mavrev.com|twurl.nl|tiny.cc|digg.com|su.pr|snipr.com|short.to|budurl.com|snipurl.com|just.as|alturl.com|om.ly|snurl.com|adjix.com|redirx.com|doiop.com|easyurl.net|u.nu|myurl.in|rubyurl.com|kl.am|sn.im/i;
	this.urlRegEx = /((telnet:)|((https?|ftp|gopher|news|file):\/\/)|(www.[\w\.\_\-]+))[^\s\<\>\[\]\{\}\'\"]*/gi;

	//scan for tweets..
	this.social_emailLastUpdateDate = this.getUserProperty("social_emailLastUpdateDate");
	var todayStr = this.twitter._getTodayStr();
	if (this.social_emailLastUpdateDate != todayStr && this.preferences.social_pref_SocialMailUpdateOn) {
		this.twitter.scanForUpdates("SEND_EMAIL");
		this.setUserProperty("social_emailLastUpdateDate", todayStr, true);
	}

	if (this.preferences.social_pref_showTweetAlertsOn) {
		this._displayTwitterAlert();
	}

	this._variablesInitialized = true;

};

com_zimbra_social.prototype._displayTwitterAlert =
function() {
	var showAlert = false;
	var currTime = (new Date()).getTime();
	var social_alertUpdateTime = this.getUserProperty("social_alertUpdateTime");
	if (!social_alertUpdateTime) {
		showAlert = true;
	} else {
		var diff = (currTime - social_alertUpdateTime) / (60 * 1000);
		if (diff > 30) {
			showAlert = true;
		} else {
			showAlert = false;
		}
	}
	if (showAlert) {
		this.twitter.scanForUpdates("SHOW_ALERT");
		setInterval(AjxCallback.simpleClosure(this.twitter.scanForUpdates, this.twitter, "SHOW_ALERT"), 1800000);//every 30 minutes
		this.setUserProperty("social_alertUpdateTime", currTime, true);
	}
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
	for (var oldprop in props) {
		var migrated = false;
		var old_prop_val = this.getPropValueFromSettings(oldprop);
		var new_prop_val = this.getPropValueFromSettings(props[oldprop]);
		if (old_prop_val != undefined && old_prop_val != "" && (new_prop_val == undefined || new_prop_val == "")) {
			migrated = true;
			this.setUserProperty(props[oldprop], old_prop_val);
		}
	}
	if (migrated)
		this.saveUserProperties();
};

com_zimbra_social.prototype.getPropValueFromSettings =
function(name) {
	try {
		if (this._propsInSettings == undefined) {
			this._propsInSettings = appCtxt.getActiveAccount().settings.getInfoResponse.props.prop;
		}
	} catch(e) {
		this._propsInSettings = new Array();
	}
	if (this._propsInSettings == undefined)
		return (new Array());

	for (var i = 0; i < this._propsInSettings.length; i++) {
		if (name == this._propsInSettings[i].name)
			return this._propsInSettings[i]._content;
	}
	return (new Array());
};

com_zimbra_social.prototype._addTweetButtons =
function() {
	this.updateButton_main = new DwtButton({parent:this.getShell()});
	this.updateButton_main.setText("Update");
	this.updateButton_main.addSelectionListener(new AjxListener(this, this._postToTweetOrFB));
	document.getElementById("social_updateStatusButton").appendChild(this.updateButton_main.getHtmlElement());

	var cancelButton = new DwtButton({parent:this.getShell()});
	cancelButton.setText("Cancel");
	cancelButton.addSelectionListener(new AjxListener(this, this._cancelPost));
	document.getElementById("social_cancelPostButton").appendChild(cancelButton.getHtmlElement());

	var shortenButton = new DwtButton({parent:this.getShell()});
	shortenButton.setText("Shorten URL");
	shortenButton.addSelectionListener(new AjxListener(this, this._shortenUrlButtonListener));
	document.getElementById("social_shortenUrlButtonDIV").appendChild(shortenButton.getHtmlElement());

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

com_zimbra_social.prototype._cancelPost =
function() {
	var statusField = this.updateField;
	statusField.value = "";
	statusField.focus();
	this.showNumberOfLetters();
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
	this._shortenUrlDialog = this._createDialog({title:"Shorten URL (bit.ly)", view:this._shortenUrlView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[suButton]});

	this._shortenUrlDialog.setButtonListener(suBtnId, new AjxListener(this, this._postToUrlShortner, {}));
	if (selectedTxt.indexOf("http:") == 0) {
		document.getElementById("com_zimbra_twitter_longUrl_field").value = selectedTxt;
	}
	this._shortenUrlDialog.popup();
};

com_zimbra_social.prototype._postToUrlShortner =
function(params) {
	var longUrl = params.longUrl;
	var callback = params.callback;
	if (!longUrl) {
		var longUrlField = document.getElementById("com_zimbra_twitter_longUrl_field");
		if(longUrlField) {
			longUrl = document.getElementById("com_zimbra_twitter_longUrl_field").value;
		} else {
			return;
		}
	}

	if(longUrl == "")
		return;

	var url = "http://api.bit.ly/shorten?"
			+ "version=" + AjxStringUtil.urlComponentEncode("2.0.1")
			+ "&longUrl=" + AjxStringUtil.urlComponentEncode(longUrl)
			+ "&login=" + AjxStringUtil.urlComponentEncode("zimbra")
			+ "&apiKey=" + AjxStringUtil.urlComponentEncode("R_20927271403ca63a07c25d17edc32a1d");
	if (!callback)
		callback = new AjxCallback(this, this._postToUrlShortnerCallback, longUrl);

	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, callback, false);
};

com_zimbra_social.prototype._getStatusFieldSelectedText =
function() {
	var statusField = this.updateField;
	return  AjxStringUtil.trim(statusField.value.substring(statusField.selectionStart, statusField.selectionEnd));
};

com_zimbra_social.prototype._replaceStatusFieldSelectedTxt =
function(selectedTxt, newTxt) {
	var statusField = this.updateField;
	statusField.value = statusField.value.replace(selectedTxt, newTxt);
};

com_zimbra_social.prototype._postToUrlShortnerCallback =
function(longUrl, response) {
	if (!response.success) {
		appCtxt.getAppController().setStatusMsg("Could Not Shorten", ZmStatusView.LEVEL_WARNING);
		return;
	}
	try {
		var text = eval("(" + response.text + ")");
		var shortUrl = text.results[longUrl].shortUrl;
		if (!shortUrl) {
			appCtxt.getAppController().setStatusMsg("Could Not Shorten", ZmStatusView.LEVEL_WARNING);
			return;
		} else {
			if (this.updateField.value.indexOf(longUrl) >= 0) {
				this.updateField.value = this.updateField.value.replace(longUrl, shortUrl);
			} else {
				this.updateField.value = this.updateField.value + " " + shortUrl;
			}
			this.latestShortenedUrl = {shortUrl: shortUrl, longUrl: longUrl};
			this.undoShortenUrlDiv.style.display = "block";
		}
	} catch(e) {
		appCtxt.getAppController().setStatusMsg("Could Not Shorten", ZmStatusView.LEVEL_WARNING);
	}
	this._autoShorten = false;
	this.showNumberOfLetters();
	this._autoShorten = true;

	if (this._shortenUrlDialog) {
		this._shortenUrlDialog.popdown();
	}
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

com_zimbra_social.prototype._saveAutoShortenUrlPref =
function(ev) {
	if (this.autoShortenCheckbox.checked) {
		this.shortenUrlButtonDiv.style.display = "none";
	} else {
		this.shortenUrlButtonDiv.style.display = "block";
	}
	this.setUserProperty("social_pref_autoShortenURLOn", this.autoShortenCheckbox.checked, true);
};

com_zimbra_social.prototype.showNumberOfLetters =
function(ev) {

	var event = ev || window.event;
	if (event) {
		var code = event.which ? event.which : event.keyCode;
		if (AjxEnv.isIE) {
			if (code == 86 && event.ctrlKey) {
				this.isTextPasted = true;
			}
		}
		if (code == 16 || code == 17) {//ctrl keyUp or shift keyUp
			return;
		}
	}
	var val = this.updateField.value;
	var len = val.length;
	if (len > 140) {
		this.updateButton.setEnabled(false);
	} else {
		this.updateButton.setEnabled(true);
	}
	if (this.whatAreYouDoingField != "NOT_PRESENT") {
		if (val.toLowerCase().indexOf("d @") == 0) {
			this.whatAreYouDoingField.innerHTML = "Send Direct/private Message:";
			this.whatAreYouDoingField.style.color = "yellow";
		} else {
			this.whatAreYouDoingField.innerHTML = "What are you doing?";
			this.whatAreYouDoingField.style.color = "black";
		}
	}
	this._updateNumberOfLettersField(len);
	if (this._autoShorten && this.autoShortenCheckbox.checked) {
		this.autoShortenURL(val);
	}
};

com_zimbra_social.prototype._updateNumberOfLettersField =
function(count) {
	var clr = "";
	var left = 140 - count;
	if (left >= 0) {
		clr = "green";
	} else {
		clr = "red";
	}
	this.nuLettersAllowedField.innerHTML = left;
	this.nuLettersAllowedField.style.color = clr;
};

com_zimbra_social.prototype.autoShortenURL =
function(text) {
	var matches = new Array();

	while (match = this.urlRegEx.exec(text)) {
		matches.push(match[0]);
		var end = this.urlRegEx.lastIndex;
		start = end;
	}
	var shorten = false;
	var potentialUrlsToShorten = new Array();
	if (matches.length > 0 && this.isTextPasted) {
		shorten = true;
		potentialUrlsToShorten = matches;
	} else if (matches.length > 0 && !this.isTextPasted) {
		for (var i = 0; i < matches.length; i++) {
			var match = matches[i];
			if (text.indexOf(match + " ") >= 0) {//must have a space after the text
				potentialUrlsToShorten.push(match);
			}
		}
	}
	var sText = text.toLowerCase();
	var urlsToShorten = new Array();
	for (var i = 0; i < potentialUrlsToShorten.length; i++) {
		var url = potentialUrlsToShorten[i];
		if (!this.shortnersRegex.test(url)) {
			urlsToShorten.push(url);
		}
	}
	for (var i = 0; i < urlsToShorten.length; i++) {
		this._postToUrlShortner({longUrl:urlsToShorten[i],callback:null});
	}
	this.isTextPasted = false;
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
	html[idx++] = "<TABLE width=100%>";
	html[idx++] = "<TR><TD style=\"width:90%;\" >";
	html[idx++] = "<input  style=\"width:100%;height:25px\" autocomplete=\"off\" id=\"social_statusTextArea\" ></input>";
	html[idx++] = "</TD>";

	html[idx++] = "<TD rowspan=2 align=center valign='middle'>";
	html[idx++] = "<table width=100%><tr><td align=center>";
	html[idx++] = "<label style=\"font-size:18px;color:green;font-weight:bold\" id='social_numberOfLettersAllowed'>140</label>";
	html[idx++] = "</td></tr><tr><td align=center>";
	html[idx++] = "<label>Characters Left</label></td></tr></table>";
	html[idx++] = "</TD>";
	html[idx++] = "</TR>";

	html[idx++] = "<TR><TD>";
	html[idx++] = "<table width=100%><tr>";
	html[idx++] = "<td align=left> <div id='social_shortenUrlButtonDIV' /></td>";
	html[idx++] = "<td align=left><input type='checkbox'  id='social_autoShortenCheckbox'></input></td><td  nowrap=''><label style='color:#252525'>Auto Shorten URL</label></td>";
	html[idx++] = "<td align=left width=90%><div id='social_undoShortenURLDIV' style='display:none'><a  href='#' id='social_undoShortenURLLink' style='text-decoration:underline;font-weight:bold'>undo</a></div></td>";
	html[idx++] = "<td align=right><div id='social_cancelPostButton' />";
	html[idx++] = "</td><td align=right><div id='social_updateStatusButton' /></td></tr></table>";
	html[idx++] = "</TD></TR>";

	html[idx++] = "</TABLE>";
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
		html[idx++] = "<TD valign='middle' align=center>";
		html[idx++] = "<input type='checkbox'  " + turnOnStr + "  id='" + chkbxId + "'>";
		html[idx++] = "</TD><TD valign='middle' align=center>";
		html[idx++] = this.allAccounts[id].name;
		html[idx++] = " &nbsp;&nbsp;&nbsp;&nbsp;";
	}

	html[idx++] = "</TR></TABLE>";

	if (hasAccounts)
		return html.join("");
	else {
		return "<label style=\"font-size:12px;color:#555555;font-style:italic\">No accounts have been added yet! Click on 'Add/Remove Accounts' to add one </label>";
	}
};
com_zimbra_social.prototype._getMaxHeaderTextLength =
function() {
	if(!this.maxHeaderTextLength) {
		var cardWidth = parseInt(this.preferences.social_pref_cardWidthList.replace("px", ""));
		this.maxHeaderTextLength = (cardWidth/50)*2;
	}
	return this.maxHeaderTextLength;
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
	var trimName = headerName;
	
	if(headerName.length > this._getMaxHeaderTextLength()) {
		trimName = headerName.substring(0, this._getMaxHeaderTextLength()) + "..";		
	}

	var prettyName = type.toLowerCase() + ": " + trimName;
	var iconName = "social_twitterIcon";
	var hdrCellColor = "black";
	if (type == "ACCOUNT") {
		hdrClass = "social_axnClass social_twitterColor";
		prettyName = "twitter: " + trimName;
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
	} else if (type == "TREND") {
		iconName = "social_trendIcon";
	}
	var hdrCellStyle = "style=\"font-size:12px;color:" + hdrCellColor + ";font-weight:bold;font-family:'Lucida Grande',sans-serif;\"";
	var card = "";
	var sDistance = "";
	if (type == "PROFILE") { //inset profile card right-next to the from-card
		tweetTableId = tweetTableId.replace("social_cardInfoSectionId", "social_card");
		var cells = row.childNodes;
		for (var i = 0; i < cells.length; i++) {
			if (tweetTableId.indexOf(cells[i].id) == 0) {
				card = row.insertCell(i + 1);
				break;
			}
		}
		sDistance = i;
	} else if (!autoScroll) {//when cards are initially shown..
		if (type == "ACCOUNT" || type == "FACEBOOK" || type == "SEARCH") {
			card = row.insertCell(0);
			sDistance = 0;
		} else {
			card = row.insertCell(row.cells.length);
			sDistance = row.cells.length;
		}
	} else {
		card = row.insertCell(0);
		sDistance = 0;
	}
	/*
	 if (type == "ACCOUNT" || type == "FACEBOOK") {
	 card = row.insertCell(-1);
	 sDistance =  row.cells.length;
	 } else if (type == "PROFILE") { //inset profile card right-next to the from-card
	 tweetTableId = tweetTableId.replace("social_cardInfoSectionId", "social_card");
	 var cells = row.childNodes;
	 for (var i = 0; i < cells.length; i++) {
	 if (tweetTableId.indexOf(cells[i].id) == 0) {
	 card = row.insertCell(i + 1);
	 break;
	 }
	 }
	 sDistance = i;
	 } else {
	 var _indx = 0;
	 for (var id in this.allAccounts) {
	 if (this.allAccounts[id].isDisplayed) {
	 _indx++;
	 }
	 }
	 card = row.insertCell(_indx);
	 sDistance =  _indx;
	 }
	 */

	card.id = "social_card" + this.cardIndex;
	var cardInfoSectionId = "social_cardInfoSectionId" + this.cardIndex;
	//used to reset heights when window is resized
	this.cardInfoSectionIdsArray.push(cardInfoSectionId);
	var html = [];
	var i = 0;
	html[i++] = "<div  class='social_cardDiv' id='social_cardsDiv" + this.cardIndex + "'>";
	var elStyle = ""
	if (AjxEnv.isFirefox) {
		elStyle = "style='height: 28px;'";
	} else {
		elStyle = "style='height: 32px;'";
	}
	html[i++] = "<DIV " + elStyle + " class='" + hdrClass + "' >";
	html[i++] = "<table width='100%'>";
	html[i++] = "<tr>";

	html[i++] = "<td  valign='middle'>" + AjxImg.getImageHtml(iconName) + "</td>";
	html[i++] = "<td width=100% valign='middle'><table><tr><td " + hdrCellStyle + " >" + prettyName + "</td><td id='social_unreadCountCell" + this.cardIndex + "'></td></tr></table></td>";

	html[i++] = "<TD width='5%'>";
	html[i++] = "<DIV style='display:block;' id='social_cardButtonsDiv" + this.cardIndex + "'>";
	html[i++] = "<table>";
	html[i++] = "<tr>";
	if (type == "ACCOUNT" || type == "FACEBOOK" || type == "SEARCH") {
		html[i++] = "<td width='5%' valign='middle' align='right'>";
		html[i++] = "<img  title=\"Move this feed left\" src=\"" + this.getResource("social_leftArrow.png") + "\" id='social_leftArrow" + this.cardIndex + "'/></td>";
		html[i++] = "<td width='5%' valign='middle' align='left'>";
		html[i++] = "<img  title=\"Move this feed right\" src=\"" + this.getResource("social_rightArrow.png") + "\" id='social_rightArrow" + this.cardIndex + "'/></td>";
	}

	if (type == "ACCOUNT" || type == "SEARCH" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
		html[i++] = "<td width='5%'valign='middle'>";
		html[i++] = "<img    title=\"Clear contents (aka mark as read)\" src=\"" + this.getResource("social_markread.gif") + "\" id='social_markAsReadBtn" + this.cardIndex + "' />";
		html[i++] = "</td>";
	}

	//	if (type == "SEARCH") {
	//		html[i++] = "<td width='5%' valign='middle'>";
	//		html[i++] = "<img  title=\"Delete this search permanently from overview panel\" src=\"" + this.getResource("social_deleteBtn.gif") + "\" id='social_deleteBtn" + this.cardIndex + "' />";
	//		html[i++] = "</td>";
	//	}

	html[i++] = "<td width='5%' valign='middle'></td>";
	if (type != "PROFILE") {
		html[i++] = "<td width='5%' valign='middle'>";
		html[i++] = "<img  title=\"Refresh this feed\" src=\"" + this.getResource("social_refreshBtn.gif") + "\" id='social_refreshBtn" + this.cardIndex + "'/></td>";
	}

	html[i++] = "<td width='5%' valign='middle'></td><td width='5%' valign='middle'>";
	html[i++] = "<img  title=\"Close this feed\" src=\"" + this.getResource("social_closeBtn.png") + "\" id='social_closeBtn" + this.cardIndex + "'/></td>";

	html[i++] = "</tr>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";

	html[i++] = "</TD>";
	html[i++] = "</tr>";
	html[i++] = "</table>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV id='" + cardInfoSectionId + "' style=\"overflow:auto;height:" + this._mainCardsHeight + ";width:" + this.preferences.social_pref_cardWidthList + "\" class='social_individualCardClass'>";
	html[i++] = "</DIV>";
	html[i++] = "</div>";
	card.innerHTML = html.join("");

	var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
	//	if (type == "SEARCH") {
	//		var callback = AjxCallback.simpleClosure(this._handleDeleteButton, this, params);
	//		document.getElementById("social_deleteBtn" + this.cardIndex).onclick = callback;
	//	}
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

	if (type == "ACCOUNT" || type == "FACEBOOK" || type == "SEARCH") {
		var callback = AjxCallback.simpleClosure(this._swapColumns, this, "social_card" + this.cardIndex, 1);
		document.getElementById("social_rightArrow" + this.cardIndex).onclick = callback;

		var callback = AjxCallback.simpleClosure(this._swapColumns, this, "social_card" + this.cardIndex, -1);
		document.getElementById("social_leftArrow" + this.cardIndex).onclick = callback;
	}
	// var callback = AjxCallback.simpleClosure(this._handleCardMouseOver, this, "social_cardButtonsDiv"+this.cardIndex);
	//document.getElementById("social_cardsDiv"+this.cardIndex).onmouseover = callback;
	//var callback = AjxCallback.simpleClosure(this._handleCardMouseOut, this, "social_cardButtonsDiv"+this.cardIndex);
	//document.getElementById("social_cardsDiv"+this.cardIndex).onmouseout = callback;

	if (autoScroll) {
		document.getElementById('social_twitterCardsDiv').scrollLeft =
		(parseInt(this.preferences.social_pref_cardWidthList.replace("px", "")) + 10) * sDistance;
	}
	return cardInfoSectionId;
};

com_zimbra_social.prototype._swapColumns =
function  (cellId, colIndex2) {
	var table = document.getElementById('social_twitterCardsParentTable');
	var cells = table.rows[0].cells;
	var colIndex1 = "";
	for (var i = 0; i < cells.length; i++) {
		if (cellId == cells[i].id) {
			colIndex1 = i;
			colIndex2 = colIndex1 + colIndex2;
			break;
		}

	}
	if (colIndex2 < 0 || colIndex2 >= cells.length)
		return;
	var fromIndex = colIndex1;
	var toIndex = colIndex2;

	if (table && table.rows && table.insertBefore && colIndex1 != colIndex2) {
		colIndex1 = Number(colIndex1);
		colIndex2 = Number(colIndex2);
		if (colIndex1 == colIndex2 - 1 || colIndex1 == colIndex2 + 1) {
			if (colIndex1 > colIndex2) {
				var tempIndex = colIndex1;
				colIndex1 = colIndex2;
				colIndex2 = tempIndex;
			}
			for (var i = 0; i < table.rows.length; i++) {
				var row = table.rows[i];
				row.insertBefore(row.cells[colIndex2], row.cells[colIndex1]);
			}
		} else {
			for (var i = 0; i < table.rows.length; i++) {
				var row = table.rows[i];
				var cell1 = row.cells[colIndex1];
				var cell2 = row.cells[colIndex2];
				var siblingCell1 = row.cells[colIndex1 + 1];
				if (typeof siblingCell1 == 'undefined') {
					siblingCell1 = null;
				}
				row.insertBefore(cell1, cell2);
				row.insertBefore(cell2, siblingCell1);
			}
		}
	}

	var scrollTo = -1;
	var divWidth = parseInt(document.getElementById('social_twitterCardsDiv').style.width.replace("px", ""));
	var sLeftWidth = document.getElementById('social_twitterCardsDiv').scrollLeft;
	var cardsWidth = parseInt(this.preferences.social_pref_cardWidthList.replace("px", ""));
	var cardsPerPage = parseInt(divWidth / cardsWidth);
	var lastCardVisible = parseInt((divWidth + sLeftWidth) / cardsWidth);
	var currentPageNumber = parseInt(lastCardVisible / cardsPerPage);
	if (fromIndex == (lastCardVisible - 1) && (fromIndex < toIndex ))
		scrollTo = toIndex;
	else if (fromIndex == (lastCardVisible - cardsPerPage) && (fromIndex > toIndex ))
		scrollTo = toIndex - (cardsPerPage - 1);

	if (scrollTo != -1) {
		document.getElementById('social_twitterCardsDiv').scrollLeft =
		(parseInt(this.preferences.social_pref_cardWidthList.replace("px", "")) + 10) * scrollTo;
	}
	//save positions
	for (var tableId in this.tableIdAndAccountMap) {
		var reqId = tableId.replace("social_cardInfoSectionId", "social_card");
		for (var i = 0; i < cells.length; i++) {
			if (cells[i].id == reqId) {
				this.tableIdAndAccountMap[tableId]["__pos"] = i;
				break;
			}
		}
	}
	this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);

	for (var tableId in this.tableIdAndSearchMap) {
		var reqId = tableId.replace("social_cardInfoSectionId", "social_card");
		for (var i = 0; i < cells.length; i++) {
			if (cells[i].id == reqId) {
				this.tableIdAndSearchMap[tableId]["__pos"] = i;
				break;
			}
		}
	}
	this.setUserProperty("social_AllTwitterSearches", this.twitter.getAllSearchesAsJSON(), true);
	appCtxt.getAppController().setStatusMsg("Feed Position Saved", ZmStatusView.LEVEL_INFO);
};

com_zimbra_social.prototype._setCardUnreadCount =
function(tableId, unReadCount) {
	var cellId = tableId.replace("social_cardInfoSectionId", "social_unreadCountCell");
	var cell = document.getElementById(cellId);
	if (cell == null)
		return;

	if (unReadCount == 0) {
		cell.className = "";
		cell.innerHTML = "";
	} else {
		cell.className = "social_unReadClass";
		cell.innerHTML = unReadCount;
	}
};

com_zimbra_social.prototype._handleUndoShortenLink =
function() {
	if (this.latestShortenedUrl) {
		this.updateField.value = this.updateField.value.replace(this.latestShortenedUrl.shortUrl, this.latestShortenedUrl.longUrl);
	}
	this.undoShortenUrlDiv.style.display = "none";
};

com_zimbra_social.prototype._handleRefreshButton =
function(params) {
	if (params.type == "SEARCH" || params.type == "TREND") {
		this.twitter.twitterSearch({query:params.headerName, tableId:params.tableId, type:params.type});
	} else if (params.type == "ACCOUNT") {
		this.twitter.getFriendsTimeLine({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "TWEETMEME") {
		this.tweetmeme.tweetMemeSearch({query:params.headerName, tableId:params.tableId});
	} else if (params.type == "DIGG") {
		this.digg.diggSearch({query:params.headerName, tableId:params.tableId});
	} else if (params.type == "FACEBOOK") {
		this.facebook._fbGetStream(params.tableId, this.tableIdAndAccountMap[params.tableId]);
	} else if (params.type == "MENTIONS") {
		this.twitter.getMentions({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "DIRECT_MSGS") {
		this.twitter.getDirectMessages({tableId: params.tableId, account: this.tableIdAndAccountMap[params.tableId]});
	} else if (params.type == "SENT_MSGS") {
		this.twitter.getSentMessages({tableId:params.tableId, account:this.tableIdAndAccountMap[params.tableId]});
	}
};

com_zimbra_social.prototype._handleCardMouseOver =
function(id) {
	document.getElementById(id).style.display = "block";
};

com_zimbra_social.prototype._handleCardMouseOut =
function(id) {
	document.getElementById(id).style.display = "none";
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
	} else if (params.type == "DIRECT_MSGS") {
		var account = this.tableIdAndAccountMap[params.tableId];
		var pId = this.tableIdAndPostIdMap[params.tableId];
		account.__dmId = pId;
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
	} else if (params.type == "SENT_MSGS") {
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
	if (params.tableId) {
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
		this.tableIdAndAccountMap[params.tableId]["__s"] = "0";
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
	}
	if (params.tableId) {
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
	this._socialAppName = this.createApp("Social", "social_greenAppIcon", "Socialize with your friends and family via Facebook & Twitter");
};

com_zimbra_social.prototype.appActive = function(appName, active) {
	if (active) {
		this.appName = appName;
		this._sociallistViews = [];
		this.initializeVariables();
		//welcome dlg
		if (this.getUserProperty("social_pref_dontShowWelcomeScreenOn") == "false") {
			this.preferences._showWelcomeDlg();
		}

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
	if (this.prefFolder == undefined) {
		this.prefFolders = new Array();
		//this.prefFolders.push({name:"Add/Remove Accounts", icon:"Group", account:"", type:"MANAGE_ACCOUNTS"});
		this.prefFolders.push({name:"Preferences", icon:"Preferences", account:"", type:"PREFERENCES"});
		this.prefFolders.push({name:"Help", icon:"Help", account:"", type:"HELP"});

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
	if (folder.type == "SEARCH") {
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
		account["__s"] = "1";//__s means shown, 1 means true
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
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
	} else if (el.id.indexOf("socialTreeItem__HELP") == 0) {
		this.preferences._showWelcomeDlg();
	} else if (el.id.indexOf("socialTreeItem__SEARCH") == 0) {
		var search = this.treeIdAndSearchMap[el.id];
		if (origTarget.className == "ImgClearSearch") {//delete search
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
		account.isDisplayed = true;
		account["__s"] = "1";//__s means shown, 1 means true
		this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
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
	this.addTwitterSearchWidget();
	this.app.setContent(this._constructSkin());
	this._view = this.app.getController().getView();
	this._view.addControlListener(new AjxListener(this, this._resizeHandler));//add resize handler
	this._setMainCardHeight();
	this._dontAutoScroll = true;
	this._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:true});

	this._addTweetButtons();
	this._loadInformation();
	this._setMainCardHeight();
	this._dontAutoScroll = false;
	this.miniDlg.miniDlgON = false;//set this first
	this.toggleFields();
	this.updateUIWidgets();
};

com_zimbra_social.prototype.addTwitterSearchWidget =
function() {
	var html = new Array();
	var idx = 0;
	html[idx++] = "<DIV >";
	html[idx++] = "<TABLE width=100% cellpadding=0 cellspacing=0 valign='middle'><TR>";
	html[idx++] = "<TD width=16px valign='middle'>";
	html[idx++] = AjxImg.getImageHtml("Blank_16");
	html[idx++] = "</TD>";
	html[idx++] = "<TD  nowrap='' width=220px  valign='left'>";
	html[idx++] = "<label  valign='middle'  style=\"font-size:14px;color:black;font-weight:bold\" id='social_whatareyoudoingLabel' >What are you doing?";
	html[idx++] = "</label>";
	html[idx++] = "</TD>";
	html[idx++] = "<TD width=16px valign='middle'>";
	html[idx++] = AjxImg.getImageHtml("Blank_16");
	html[idx++] = "</TD>";
	html[idx++] = "<td  width=70% nowrap='' id='social_updateToCell'>";
	html[idx++] = this._addUpdateToCheckboxes();
	html[idx++] = "</TD>";
	html[idx++] = "<TD valign='middle' align='right'>";
	html[idx++] = "<input   style=\"width:150px;\" type=\"text\" autocomplete=\"off\" id=\"social_searchField\"></input>";
	html[idx++] = "</TD><TD valign='middle'>";
	html[idx++] = "<div  valign='middle' id='social_searchButton' />";
	html[idx++] = "</TD>";
	html[idx++] = "</TR></TABLE>";
	html[idx++] = "</DIV>";
	var toolbar = this.app.getToolbar();
	toolbar.getHtmlElement().innerHTML = html.join("");
};

com_zimbra_social.prototype._postToTweetOrFB =
function(ev) {
	var event = ev || window.event;
	if (event.keyCode != undefined && event.keyCode != 13) {//if not enter key
		var code = event.which ? event.which : event.keyCode ;
		if (code == 118 && event.ctrlKey) {//see if they are pasting something
			this.isTextPasted = true;
		}
		return;
	}
	var isDM = false;
	var noAccountSelected = true;

	var message = this.updateField.value;
	if (message.length == 0) {
		return;
	}
	if (message.length > 140) {
		appCtxt.getAppController().setStatusMsg("More than 140 Characters is not allowed", ZmStatusView.LEVEL_WARNING);
		return;
	}

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
		return;
	}
	if (this.miniDlg.socialMiniDialog && this.miniDlg.socialMiniDialog.isPoppedUp()) {
		this.miniDlg.socialMiniDialog.popdown();
		this.miniDlg.miniDlgON = false;//set this first
		this.toggleFields();
	}

};

com_zimbra_social.prototype.createCardView =
function(tableId, jsonObj, type) {
	if (this.miniDlg.miniDlgON)//return if its from miniDlg view
		return;

	if (!tableId)
		return;

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
			} else if (type == "MENTIONS") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__mId ? accnt.__mId : "";
			} else if (type == "DIRECT_MSGS") {
				var accnt = this.tableIdAndAccountMap[tableId];
				pId = accnt.__dmId ? accnt.__dmId : "";
			} else if (type == "SENT_MSGS") {
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
			imageAnchor = "<TD width=48px height=48px align='center' valign='top'> ";
			imageAnchor = imageAnchor + "<div class='social_accountBg'><a  href=\"http://twitter.com/" + screen_name + "\" target=\"_blank\" style=\"color:white\">";
			imageAnchor = imageAnchor + "<img height=\"48\" width=\"48\" src=\"" + obj.profile_image_url + "\" />";
			imageAnchor = imageAnchor + "</a></div>";
			imageAnchor = imageAnchor + "</td>";
		} else     if (type == "TWEETMEME") {
			screen_name = "tweetmeme";
			created_at = obj.created_at;
			text = " " + obj.title + " " + obj.url;
			source = "tweetmeme";
			tweetcount = obj.url_count;
			imageAnchor = "<TD > ";
			imageAnchor = imageAnchor + "<a  href=\"" + obj.url + "\" target=\"_blank\" style=\"color:gray\">";
			imageAnchor = imageAnchor + "<table><tr>";
			imageAnchor = imageAnchor + "<td class='social_tweetMemeBg' width=48px height=48px align='center'  valign='middle'>";
			imageAnchor = imageAnchor + tweetcount + "</td></tr><tr><td class='social_tweetMemeRetweetBg'>retweets</td></tr></table>";
			imageAnchor = imageAnchor + "</a>";
			imageAnchor = imageAnchor + "</td>";
		} else     if (type == "DIGG") {
			diggCount = obj.diggs;
			text = [" ", obj.title, " ", obj.link].join("");
			screen_name = "digg";
			source = "digg";
			created_at = obj.promote_date;
			imageAnchor = "<TD  valign='top'>";
			imageAnchor = imageAnchor + "<a  href=\"" + obj.href + "\" target=\"_blank\" style=\"color:gray\">";
			imageAnchor = imageAnchor + "<div class='social_diggBg'>";
			imageAnchor = imageAnchor + "<table width=100% cellpadding=0 cellspacing=0><tr><td style='font-size:14px;font-weight:bold' align=center width=100%>" + diggCount + "</td></tr><tr><td align=center valign='middle'  width=100%>diggs</td></tr></tablee></td></tr></table>";
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

			imageAnchor = "<TD width=48px height=48px align='center'  valign='top'> ";
			imageAnchor = imageAnchor + "<div class='social_accountBg'>";
			imageAnchor = imageAnchor + "<a id='" + this._getAccountLinkId(screen_name, tableId) + "' class='FakeAnchor' style=\"color:white\">";
			imageAnchor = imageAnchor + "<img height=\"48\" width=\"48\" src=\"" + user.profile_image_url + "\" />";
			imageAnchor = imageAnchor + "</a></div>";
			imageAnchor = imageAnchor + "</td>";
			notFollowing = user.following == null;
			followId = userId;
		} else if (type == "FACEBOOK") {
			if (obj.is_hidden) {
				continue;
			}

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
			text = " " + text;
			imageAnchor = "<TD width=48px height=48px align='center'  valign='top'> ";
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
			source = source.replace("<a ", "<a target=\"_blank\" style='font-size:11px;'");
		}
		var parsedDate = "";
		if (type != "FACEBOOK" && type != "DIGG") {
			created_at = created_at.replace("+0000", "");
			created_at = created_at + " +0000";//hack to make it work in IE
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
				if (tmpTime == 1)
					timeStr = "about " + tmpTime + " hour ago";
				else
					timeStr = "about " + tmpTime + " hours ago";
			} else {
				var d = new Date(parsedDate);
				var arry = d.toString().split(" ");
				timeStr = [ arry[1]," ", arry[2], " at ", d.getHours(),  ":",  d.getMinutes(),  ":",  d.getSeconds()].join("");
			}
		}
		created_at = timeStr;

		//pass it through zimlets to get url, phone, emoticons etc
		if (this._zimletDiv == undefined) {
			this._zimletDiv = document.createElement("div");
		}
		this._zimletDiv.innerHTML = text;
		this._objectManager.findObjectsInNode(this._zimletDiv);
		var zimletyFiedTxt = this._zimletDiv.innerHTML;
		zimletyFiedTxt = this._replaceHash(zimletyFiedTxt);
		zimletyFiedTxt = this._replaceAt(zimletyFiedTxt, userId, tableId, screen_name);

		html[i++] = "<div id='" + rowId + "' class='social_rowsDiv'>";
		html[i++] = "<TABLE width=100%>";
		html[i++] = "<TR>";
		html[i++] = imageAnchor;
		html[i++] = "<TD class='social_feedText' width=90%>";
		if (type != "TWEETMEME" && type != "FACEBOOK") {
			html[i++] = [" <a href=\"#\" style=\"color:darkblue;font-size:12px;font-weight:bold\" id='", this._getAccountLinkId(screen_name, tableId),
				"'>", screen_name, ":</a> "].join("");
		} else {
			html[i++] = "<label style=\"color:#262626;font-size:12px;font-weight:bold\">" + screen_name + ": </label>";
		}
		html[i++] = zimletyFiedTxt;
		html[i++] = "<br/><label style='color:gray;font-size:11px'>&nbsp;" + created_at + "</label><br/>";

		// html[i++] = "<br/><span align=right style='color:gray;font-size:10px'>&nbsp;- "+created_at+ " via " + source+"</span>";
		html[i++] = "</TD>";
		html[i++] = "</TR>";

		if (type == "FACEBOOK") {
			var additionalInfo = this._getAdditionalFBMessageInfo(obj, this.tableIdAndAccountMap[tableId]);
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
			html[i++] = "<TD width=95% style=\"color:gray;font-size:11px\">";
		} else {
			html[i++] = "<TD width=90% style=\"color:gray;font-size:11px\">";
		}
		html[i++] = source.indexOf("via") != -1 ? source : "via " + source;
		html[i++] = "</td>";
		html[i++] = "<td colspan=2 >";

		if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
			if (this.twitterAccountNames == undefined) {
				this.twitterAccountNames = new Array();

				for (var accntId in this.allAccounts) {
					var accnt = this.allAccounts[accntId];
					if (accnt.type == "twitter") {
						this.twitterAccountNames[accnt.name] = true;
					}
				}
			}

			if (this.twitterAccountNames[screen_name]) {
				html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._gettwitterDeleteLinkId(obj.id, tableId) + "'>delete</a>&nbsp;&nbsp;";
			}
		}

		if (type == "ACCOUNT" || type == "SEARCH" || type == "TREND") {
			html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._gettwitterDMLinkId("d @" + screen_name) + "'>dm</a>&nbsp;&nbsp;";
		}

		html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._gettwitterRetweetLinkId("RT @" + screen_name + text) + "'>retweet</a>&nbsp;&nbsp;";

		if (type != "TWEETMEME" && type != "FACEBOOK" && type != "DIGG") {
			html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._gettwitterReplyLinkId("@" + screen_name) + "'>reply</a>";
		}
		if (type == "FACEBOOK") {
			html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._getFBLikeLinkId(obj.post_id, tableId) + "'>like</a>&nbsp;&nbsp;";

			html[i++] = "<a href=\"#\" style=\"color:gray;font-size:11px\" id='" + this._getFacebookCommentLinkId(obj.post_id, tableId) + "'>comment</a>";
		}
		html[i++] = "</td>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</TD>";
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
	this._addFbPostLikeLinkHandlers();
	this._addFbMoreCommentLinkHandlers();
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
function(obj, account) {
	var html = new Array();
	var i = 0;
	if (obj.description) {
		html[i++] = obj.description;
	}
	var commentBoxId = "social_fbcommentBoxId_" + Dwt.getNextId();
	this._FBPostIdAndCommentboxMap[obj.post_id] = commentBoxId;
	if (obj.attachment && obj.attachment.media != undefined && obj.attachment.media.length > 0) {
		var attachment = obj.attachment;
		var medias = attachment.media;
		var counter = 0;
		var maxItems = 2;
		var cardW = parseInt(this.preferences.social_pref_cardWidthList.replace("px", ""));
		if (cardW <= 350)
			maxItems = 1;

		html[i++] = "<table width=100%>";
		html[i++] = "<TR>";
		for (var j = 0; j < medias.length; j++) {
			var media = medias[j];
			html[i++] = "<TD width='100px' valign='top'>";
			html[i++] = "<table cellspacing='0' cellpadding='0' width=100%>";
			html[i++] = "<TR>";
			html[i++] = "<TD width='100px'  valign='top'>";

			html[i++] = "<a  href=\"" + media.href + "\" target=\"_blank\"  style=\"color:white\">";
			html[i++] = "<div width='100px' style='border:1px solid #CCCCCC;padding:2px'>";
			html[i++] = "<img  width='100px'  src=\"" + media.src + "\" />";
			html[i++] = "</div>";
			html[i++] = "</a>";

			html[i++] = "</TD>";
			html[i++] = "</TR><TR><TD>";

			if (!media.alt && attachment.description && media.type == "link" && medias.length > 1) {
				html[i++] = "<div style='font-size:11px' class='social_feedText'>";
				html[i++] = attachment.description;
				html[i++] = "</div>";
			} else {
				html[i++] = media.alt;
			}
			html[i++] = "</TD></TR>";
			html[i++] = "</TABLE>";
			html[i++] = "</TD>";
			html[i++] = "<TD valign='top'>";
			if (attachment.name && attachment.href && media.type == "link" && medias.length == 1) {
				html[i++] = "<a  href=\"" + attachment.href + "\" target='_blank'>";
				html[i++] = attachment.name;
				html[i++] = "</a>";
				html[i++] = "<br/>";
			}
			if (attachment.description && media.type == "link" && medias.length == 1) {
				html[i++] = "<div style='font-size:11px' class='social_feedText'>";
				html[i++] = attachment.description;
				html[i++] = "</div>";
			}
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

	if (obj.likes.count > 0) {
		html[i++] = "<table width=100% cellpadding=1 cellspacing=1><tr><td align='center'>";
		html[i++] = "<tr>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "<TD>";
		html[i++] = "<DIV class='social_FBCommentRow'>";
		if (obj.likes.count == 1) {
			html[i++] = "<a  href=\"" + obj.likes.href + "\" target=\"_blank\">1 person</a> likes this";
		} else {
			html[i++] = "<a  href=\"" + obj.likes.href + "\" target=\"_blank\">" + obj.likes.count + " people</a> like this";
		}
		html[i++] = "</div>";
		html[i++] = "</td>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "</tr></table>";
	}

	if (obj.comments && obj.comments.comment_list != undefined) {
		var comments = obj.comments.comment_list;
		var commentsDivId = "social_commentsdiv_" + Dwt.getNextId();
		html[i++] = "<div id='" + commentsDivId + "'>";
		html[i++] = this._getCommentsHtml(comments, obj.comments.count, obj.post_id, commentsDivId, account);
		html[i++] = "</div>";
	}

	var str = html.join("");
	if (str == "")
		return  {html:"", commentBoxId:commentBoxId};
	else
		return {html:"<BR/>" + str, commentBoxId:commentBoxId};
};

com_zimbra_social.prototype._getCommentsHtml =
function(comments, totlCmnts, postId, divId, account) {
	var html = new Array();
	var i = 0;
	if (this._zimletDiv == undefined) {
		this._zimletDiv = document.createElement("div");
	}

	for (var j = 0; j < comments.length; j++) {
		var comment = comments[j];
		var profile = this.facebook._getFacebookProfile(comment.fromid);
		html[i++] = "<table width=100% cellpadding=1 cellspacing=1>";
		html[i++] = "<tr>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "<TD>";
		html[i++] = "<DIV class='social_FBCommentRow'>";
		html[i++] = "<table width=100%>";
		html[i++] = "<TR>";
		html[i++] = "<TD width=32px valign='top'>";
		html[i++] = "<div width=32px height=32px  align='center' class='social_accountBg'> ";
		var pUrl = profile.url ? profile.url : profile.profile_url;
		html[i++] = "<a  href=\"" + pUrl + "\" target=\"_blank\" style='color: white;'>";
		html[i++] = "<img height=\"32\" width=\"32\" src=\"" + profile.pic_square + "\" />";
		html[i++] = "</a>";
		html[i++] = "</div>";
		html[i++] = "</TD><TD class='social_fbcommentText'>";
		//pass it through zimlets to get url, phone, emoticons etc
		this._zimletDiv.innerHTML = comment.text;
		this._objectManager.findObjectsInNode(this._zimletDiv);
		html[i++] = this._zimletDiv.innerHTML + "<br><label  style=\"color:gray;font-size:11px\"> - " + profile.name + "</label>";
		html[i++] = "</TD></TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</DIV>";
		html[i++] = "</TD>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "</TR>";
		html[i++] = "</table>";
	}
	if (comments.length < totlCmnts) {
		var moreCommentsLinkId = this._getFacebookMoreCommentsLinkId(postId, divId, account);
		html[i++] = "<table width=100% cellpadding=1 cellspacing=1><tr><td>";
		html[i++] = "<tr>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "<TD>";
		html[i++] = "<DIV class='social_FBCommentRow'>";
		html[i++] = ["<a  href=\"#\" id='", moreCommentsLinkId, "'>See all ", totlCmnts, " comments</a>"].join("");
		html[i++] = "</div>";
		html[i++] = "</td>";
		html[i++] = "<TD style=\"width:16px;height:16px\" align='center'>";
		html[i++] = AjxImg.getImageHtml("Blank_16");
		html[i++] = "</TD>";
		html[i++] = "</tr></table>";
	}
	return html.join("");

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

com_zimbra_social.prototype._getFBLikeLinkId =
function(postId, tableId) {
	var id = "social_FBLikeLink_" + Dwt.getNextId();
	this._allFBLikeLinks[id] = {hasHandler:false, tableId:tableId, account:this.tableIdAndAccountMap[tableId], postId:postId};
	return id;
};

com_zimbra_social.prototype._getFacebookMoreCommentsLinkId =
function(postId, divId, account) {
	var id = "social_FBMoreCommentsLink_" + Dwt.getNextId();
	this._allFBMoreCommentsLinks[id] = {hasHandler:false, postId:postId, divId:divId, account:account};
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

com_zimbra_social.prototype._addFbPostLikeLinkHandlers =
function() {
	for (var id in this._allFBLikeLinks) {
		var obj = this._allFBLikeLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.facebook.postLike, this.facebook, obj);
			obj.hasHandler = true;
		}
	}
};

com_zimbra_social.prototype._addFbMoreCommentLinkHandlers =
function() {
	for (var id in this._allFBMoreCommentsLinks) {
		var obj = this._allFBMoreCommentsLinks[id];
		if (!obj.hasHandler) {
			document.getElementById(id).onclick = AjxCallback.simpleClosure(this.facebook.insertMoreComments, this.facebook, obj);
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
	if (start < text.length) {
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
		var id = this._getAccountLinkId(AjxStringUtil.trim(word.replace("@", "")), tableId);
		var url = ["<a  href=\"#\" id='", id, "'>", word, "</a>"].join("");
		newStr = newStr + part.replace(word, url);
		start = end;
	}
	var extraStr = "";
	if (start < text.length) {
		extraStr = text.substring(start, text.length)
	}
	return newStr + extraStr;
};

com_zimbra_social.prototype.addRetweetText = function(rt) {
	this.isTextPasted = true;
	this.addReplyText(rt);
	this.isTextPasted = false;
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
	var statusField = this.updateField;
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
		var accountsAndSearches = this._sortAndMergeAccountsAndSearches();
		for (var i = 0; i < accountsAndSearches.length; i++) {
			var account = accountsAndSearches[i];
			if (account.isDisplayed) {
				continue;
			}
			if (account.type == "twitter") {
				if (account.__s == "0") {//if already displayed OR should-not display(__s == "0")
					continue;
				}
				var tableId = this._showCard({headerName:account.name, type:"ACCOUNT", autoScroll:false});
				this.twitter.getFriendsTimeLine({tableId: tableId, account: account});
				var timer = setInterval(AjxCallback.simpleClosure(this.twitter._updateAccountStream, this.twitter, tableId, account), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
				this.tableIdAndAccountMap[tableId] = account;
				account.isDisplayed = true;
				account["__s"] = "1";//__s means shown, 1 means true
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else if (account.type == "facebook") {
				if (account.__s == "0") {//if already displayed OR should-not display(__s == "0")
					continue;
				}
				var tableId = this._showCard({headerName:account.name, type:"FACEBOOK", autoScroll:false});
				this.facebook._fbGetStream(tableId, account);
				var timer = setInterval(AjxCallback.simpleClosure(this.facebook._updateFacebookStream, this.facebook, tableId, account), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
				this.tableIdAndAccountMap[tableId] = account;
				account.isDisplayed = true;
				account["__s"] = "1";//__s means shown, 1 means true
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else {
				var search = account;
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
	}
};

com_zimbra_social.prototype._sortAndMergeAccountsAndSearches =
function() {
	var simpleArry = [];
	if (!this.accountsWithPositions) {
		this.accountsWithPositions = new Array();
	}
	for (var id in this.allAccounts) {
		var item = this.allAccounts[id];
		simpleArry.push(item);
		if (item.__pos && (item.__pos != "" || item.__pos != "undefined")) {
			this.accountsWithPositions.push(item);
		}
	}
	for (var id in this.twitter.allSearches) {
		var item = this.twitter.allSearches[id];
		simpleArry.push(item);
		if (item.__pos && (item.__pos != "" || item.__pos != "undefined")) {
			this.accountsWithPositions.push(item);
		}
	}

	return simpleArry.sort(social_sortAccounts);
};

function social_sortAccounts(a, b) {
	var x = parseInt(a.__pos);
	var y = parseInt(b.__pos);
	return ((x < y) ? 1 : ((x > y) ? -1 : 0));
}

com_zimbra_social.prototype._showWarningMsg = function(message) {
	var style = DwtMessageDialog.WARNING_STYLE;
	var dialog = appCtxt.getMsgDialog();
	this.warningDialog = dialog;
	dialog.setMessage(message, style);
	dialog.popup();
};

com_zimbra_social.prototype.openCenteredWindow =
function (url) {
	var width = 800;
	var height = 600;
	var left = parseInt((screen.availWidth / 2) - (width / 2));
	var top = parseInt((screen.availHeight / 2) - (height / 2));
	var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
	var win = window.open(url, "subWind", windowFeatures);
	if (!win) {
		this._showWarningMsg(ZmMsg.popupBlocker);
	}
};