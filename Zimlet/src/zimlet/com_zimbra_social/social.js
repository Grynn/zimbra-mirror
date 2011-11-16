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

function com_zimbra_social_handlerObject() {
}

com_zimbra_social_handlerObject.prototype = new ZmZimletBase();
com_zimbra_social_handlerObject.prototype.constructor = com_zimbra_social_handlerObject;

var SocialZimlet = com_zimbra_social_handlerObject;

SocialZimlet.SOCIALIZE_BUTTON = "SOCIAL_ZIMLET_TOOLBAR_BUTTON";

SocialZimlet.prototype.init =
		function() {
			this.initializeVariables();
			this._createsocialApp();
		};


 SocialZimlet.prototype.initializeToolbar =
		 function(app, toolbar, controller, view) {
			 if (!this.preferences || !this.preferences.social_pref_socializeBtnOn)
				 return;

			 if (view == ZmId.VIEW_CONVLIST ||
					 view == ZmId.VIEW_CONV ||
					 view == ZmId.VIEW_TRAD) {

				 var op = toolbar.getOp(ZmId.OP_ACTIONS_MENU);
				 if(op) {
					 var menu = op.getMenu();
					 if(menu) {
						 if(menu.getMenuItem(SocialZimlet.SOCIALIZE_BUTTON)) {
							 return;
						 }
						 var mi = menu.createMenuItem(SocialZimlet.SOCIALIZE_BUTTON, {image:"social-icon",
							 				text:this.getMessage("socialize"), tooltip: this.getMessage("socializeTooltip")});

						 mi.addSelectionListener(new AjxListener(this.miniDlg, this.miniDlg._buttonListener, [controller]));
					 }
				 } else {
					 var button = toolbar.createOp(SocialZimlet.SOCIALIZE_BUTTON, {text: ZmMsg.socialBtnLabel,
						 tooltip: this.getMessage("socializeTooltip"), image: "social-icon"});

					 button.addSelectionListener(new AjxListener(this.miniDlg, this.miniDlg._buttonListener, [controller]));
				 }
			 }
 };

SocialZimlet.prototype.toggleFields =
		function() {
			if (this.miniDlg.miniDlgON) {
				this.updateField = document.getElementById("social_statusTextArea_miniDlg");
				//this.whatAreYouDoingField = "NOT_PRESENT";
				this.nuLettersAllowedField = document.getElementById("social_numberOfLettersAllowed_miniDlg");
				this.nuLetterAllowedDiv = document.getElementById("social_numberOfLettersAllowedDiv_miniDlg");

				this.updateButton = this.miniDlg.updateButton_miniDlg;
				this.undoShortenUrlDiv = document.getElementById('social_undoShortenURLDIV_miniDlg');
				this.autoShortenCheckbox = document.getElementById('social_autoShortenCheckbox_miniDlg');
				this.shortenUrlButtonDiv = document.getElementById("social_shortenUrlButtonDIV_miniDlg");
				this.undoShortenURLLink = document.getElementById("social_undoShortenURLLink_miniDlg");
			} else {
				this.updateField = document.getElementById("social_statusTextArea");
				//this.whatAreYouDoingField = document.getElementById("social_whatareyoudoingLabel");
				this.nuLettersAllowedField = document.getElementById("social_numberOfLettersAllowed");
				this.nuLetterAllowedDiv = document.getElementById("social_numberOfLettersAllowedDiv");
				this.updateButton = this.updateButton_main;
				this.undoShortenUrlDiv = document.getElementById('social_undoShortenURLDIV');
				this.autoShortenCheckbox = document.getElementById('social_autoShortenCheckbox');
				this.shortenUrlButtonDiv = document.getElementById("social_shortenUrlButtonDIV");
				this.undoShortenURLLink = document.getElementById("social_undoShortenURLLink");

			}
		};

//------------

SocialZimlet.prototype.updateUIWidgets =
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

SocialZimlet.prototype.initializeVariables =
		function() {
			if (this._variablesInitialized != undefined)//initialize only once
				return;

			ZmZimletBase.prototype.init.apply(this, arguments);
			this.tableIdAndTimerMap = new Array();
			this.tableIdAndAccountMap = new Array();
			this.tableIdAndSearchMap = new Array();
			this.tableIdAndHttpErrorMap = new Array();
			this._FBPostIdAndCommentboxMap = new Array();
			this._SCPostIdAndCommentboxMap = new Array();
			this.tableIdAndEndPostIdMap = new Array();
			this.tableIdAndResultsMap = new Array();
			this.allAccounts = new Array();
			this._allHashLinks = new Array();
			this._allSocialcastHashLinks = new Array();
			this._allReweetLinks = new Array();
			this._allDMLinks = new Array();
			this._allTwitterDeleteLinks = new Array();
			this._allReplyLinks = new Array();
			this._allFollowLinks = new Array();
			this._allAccountsLinks = new Array();
			this._allSocialcastAccountsLinks = new Array();
			this._allFacebookCommentsLinks = new Array();
			this._allSCCommentsLinks = new Array();
			this._allFBLikeLinks = new Array();
			this._allSCLikeLinks = new Array();
			this._allFBMoreCommentsLinks = new Array();
			this._allSCMoreCommentsLinks = new Array();
			this._scCommentDivAndHiddenDivsMap = new Array();
			this.tableIdAndPageNumberMap = new Array();
			this._tableIdAndBottomPostIdMap = new Array();
			this.tableIdAndRefreshType = new Array();
			this.tableIdAndMarkAsReadId = new Array();
			this.tableIdAndTopPostIdMap = new Array();
			this.tableIdAndSCStreamMap = new Array();
			this.tableIdAndHighestPostIdMap = new Array();
			this.tableIdAndCacheMap = new Array();
			this.tableIdAndUnreadMap = new Array();
			this.cardInfoSectionIdsArray = new Array();//every card's data div (used to reset its height when window is resized)
			this.socialcastDisplayedStreams = new Array();
			this.socialcastDisplayedStreamsHASH = new Array();
			this.isTextPasted = false;
			this._autoShorten = true;

			this.preferences = new com_zimbra_socialPreferences(this);
			this.twitter = new com_zimbra_socialTwitter(this, this.preferences);
			this.socialcast = new com_zimbra_socialcast(this);
			this.facebook = new com_zimbra_socialFacebook(this);
			this.tweetmeme = new com_zimbra_socialTweetMeme(this);
			this.digg = new com_zimbra_socialDigg(this);
			this.miniDlg = new com_zimbra_socialMiniDlg(this);
			this.socialOAuth = new SocialOAuth(this);
			this.loadAllAccountsFromDB();
			this._objectManager = new ZmObjectManager(new DwtComposite(this.getShell()));

			this.shortnersRegex = /bit.ly|tinyurl.com|is.gd|tr.im|ow.ly|cli.gs|u.mavrev.com|twurl.nl|tiny.cc|digg.com|su.pr|snipr.com|short.to|budurl.com|snipurl.com|just.as|alturl.com|om.ly|snurl.com|adjix.com|redirx.com|doiop.com|easyurl.net|u.nu|myurl.in|rubyurl.com|kl.am|sn.im/i;
			this.urlRegEx = /((telnet:)|((https?|ftp|gopher|news|file):\/\/)|(www.[\w\.\_\-]+))[^\s\<\>\[\]\{\}\'']*/gi;

			//scan for tweets..
			this.social_emailLastUpdateDate = this.getUserProperty("social_emailLastUpdateDate");
			var todayStr = this.twitter._getTodayStr();
			if (this.social_emailLastUpdateDate != todayStr && this.preferences.social_pref_SocialMailUpdateOn) {
				this.twitter.scanForUpdates("SEND_EMAIL");
				this.setUserProperty("social_emailLastUpdateDate", todayStr, true);
			}
			var streams = this.getUserProperty("socialcastDisplayedStreams");
			if (!streams) {
				this.socialcastDisplayedStreams = [];
				//un:un, n:streamName, id
			} else {
				try{
					streams = JSON.parse(streams);
				} catch (e) {
					streams = [];
				}
				if(!(streams instanceof Array)) {
					streams = [];
				}
				for(var i = 0; i < streams.length; i++) {
					var sObj = streams[i];
					var hash = sObj.un + sObj.n + sObj.id;
					if(!this.socialcastDisplayedStreamsHASH[hash] && sObj.__s == "1") {
						 this.socialcastDisplayedStreamsHASH[hash] = true;
						 this.socialcastDisplayedStreams.push(sObj);
					}
				}
				//for (var i = 0; i < this.socialcastDisplayedStreams.length; i++) {
				//	var sObj = this.socialcastDisplayedStreams[i];
				//	this.socialcastDisplayedStreamsHASH[sObj.un + sObj.n + sObj.id] = true;
				//}
			}

			if (this.preferences.social_pref_showTweetAlertsOn) {
				this._displayTwitterAlert();
			}
			this._variablesInitialized = true;
		};

SocialZimlet.prototype._displayTwitterAlert =
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


SocialZimlet.prototype._addTweetButtons =
		function() {
			this.updateButton = this.updateButton_main = new DwtButton({parent:this.getShell()});
			this.updateButton_main.setText(this.getMessage("update"));
			this.updateButton_main.addSelectionListener(new AjxListener(this, this._postToTweetOrFB));
			document.getElementById("social_updateStatusButton").appendChild(this.updateButton_main.getHtmlElement());

			var cancelButton = new DwtButton({parent:this.getShell()});
			cancelButton.setText(this.getMessage("cancel"));
			cancelButton.addSelectionListener(new AjxListener(this, this._cancelPost));
			document.getElementById("social_cancelPostButton").appendChild(cancelButton.getHtmlElement());

			var shortenButton = new DwtButton({parent:this.getShell()});
			shortenButton.setText(this.getMessage("shortenUrl"));
			shortenButton.addSelectionListener(new AjxListener(this, this._shortenUrlButtonListener));
			document.getElementById("social_shortenUrlButtonDIV").appendChild(shortenButton.getHtmlElement());

			var searchButton = new DwtButton({parent:this.getShell()});
			searchButton.setText(this.getMessage("twitterSearch"));
			searchButton.addSelectionListener(new AjxListener(this, this._twitterSearchBtnListener));
			document.getElementById("social_searchButton").appendChild(searchButton.getHtmlElement());

			Dwt.setHandler(document.getElementById("social_searchField"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this.twitterSearchKeyHdlr, this));

			Dwt.setHandler(this.updateField, DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this.showNumberOfLetters, this));
			this.updateField.onfocus = AjxCallback.simpleClosure(this._handleFieldFocusBlur, this, this.updateField, this.getMessage("whatAreYouDoing"));
			this.updateField.onblur = AjxCallback.simpleClosure(this._handleFieldFocusBlur, this, this.updateField, this.getMessage("whatAreYouDoing"));
		};

SocialZimlet.prototype._addAccountCheckBoxListeners =
		function() {
			for (var accntId in this.allAccounts) {
				var callback = AjxCallback.simpleClosure(this._saveToAccountCheckboxesPref, this, accntId);
				var domObj = document.getElementById(this.allAccounts[accntId].checkboxId);
				if (domObj) {
					Dwt.setHandler(domObj, DwtEvent.ONCLICK, callback);
				}
			}
			for (var i = 0; i < this.socialcastAccounts.length; i++) {
				var scAccount = this.socialcastAccounts[i];
				var callback = AjxCallback.simpleClosure(this._updateSCCheckboxesPref, this, scAccount);
				var domObj = document.getElementById(scAccount.checkboxId);
				if (domObj) {
					Dwt.setHandler(domObj, DwtEvent.ONCLICK, callback);
				}
			}
		};

SocialZimlet.prototype._cancelPost =
		function() {
			var statusField = this.updateField;
			statusField.value = "";
			statusField.focus();
			this.showNumberOfLetters();
		};

SocialZimlet.prototype._shortenUrlButtonListener =
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
			var suButton = new DwtDialog_ButtonDescriptor(suBtnId, this.getMessage("shortenUrl"), DwtDialog.ALIGN_RIGHT);
			this._shortenUrlDialog = this._createDialog({title:this.getMessage("shortenUrlDlgLabel"), view:this._shortenUrlView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[suButton]});

			this._shortenUrlDialog.setButtonListener(suBtnId, new AjxListener(this, this._postToUrlShortner, {}));
			if (selectedTxt.indexOf("http:") == 0) {
				document.getElementById("com_zimbra_twitter_longUrl_field").value = selectedTxt;
			}
			this._shortenUrlDialog.popup();
		};

SocialZimlet.prototype._postToUrlShortner =
		function(params) {
			var longUrl = params.longUrl;
			var callback = params.callback;
			if (!longUrl) {
				var longUrlField = document.getElementById("com_zimbra_twitter_longUrl_field");
				if (longUrlField) {
					longUrl = document.getElementById("com_zimbra_twitter_longUrl_field").value;
				} else {
					return;
				}
			}

			if (longUrl == "")
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

SocialZimlet.prototype._getStatusFieldSelectedText =
		function() {
			var statusField = this.updateField;
			return  AjxStringUtil.trim(statusField.value.substring(statusField.selectionStart, statusField.selectionEnd));
		};

SocialZimlet.prototype._replaceStatusFieldSelectedTxt =
		function(selectedTxt, newTxt) {
			var statusField = this.updateField;
			statusField.value = statusField.value.replace(selectedTxt, newTxt);
		};

SocialZimlet.prototype._postToUrlShortnerCallback =
		function(longUrl, response) {

			if (!this._failedToShortenUrls) {
				this._failedToShortenUrls = [];
			}
			if (this._failedToShortenUrls[longUrl]) {
				return;
			}
			if (!response.success) {
				this._failedToShortenUrls[longUrl] = true;
				appCtxt.getAppController().setStatusMsg(this.getMessage("couldNotShorten"), ZmStatusView.LEVEL_WARNING);
				return;
			}
			try {
				var text = eval("(" + response.text + ")");
				var shortUrl = text.results[longUrl].shortUrl;
				if (!shortUrl) {
					this._failedToShortenUrls[longUrl] = true;
					appCtxt.getAppController().setStatusMsg(this.getMessage("couldNotShorten"), ZmStatusView.LEVEL_WARNING);
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
				this._failedToShortenUrls[longUrl] = true;
				appCtxt.getAppController().setStatusMsg(this.getMessage("couldNotShorten"), ZmStatusView.LEVEL_WARNING);
			}
			this._autoShorten = false;
			this.showNumberOfLetters();
			this._autoShorten = true;

			if (this._shortenUrlDialog) {
				this._shortenUrlDialog.popdown();
			}
		};

SocialZimlet.prototype._createShortenURLView =
		function() {
			var html = new Array();
			var i = 0;
			html[i++] = "<BR>";
			html[i++] = "<DIV>";
			html[i++] = "Long URL:<input id='com_zimbra_twitter_longUrl_field'  type='text' width=100% style='width:100%'/>";
			html[i++] = "</DIV>";
			return html.join("");
		};

SocialZimlet.prototype._getTableIdFromAccount =
		function(reqAccount) {
			for (var id in this.tableIdAndAccountMap) {
				var actAccnt = this.tableIdAndAccountMap[id];
				if (actAccnt.name == reqAccount.name)
					return id;
			}
			return "";
		};

SocialZimlet.prototype._saveToAccountCheckboxesPref =
		function(accntId, ev) {
			if (this.allAccounts[accntId].__on == "true")
				this.allAccounts[accntId].__on = "false";
			else
				this.allAccounts[accntId].__on = "true";

			this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			this._updateMaxAllowedCharsToUpdate();
			this._showHideMaxAlowedCharsDiv();
		};

SocialZimlet.prototype._updateSCCheckboxesPref =
		function(scAccount, ev) {
			if (scAccount.__on == "true") {
				scAccount.__on = "false";
			} else {
				scAccount.__on = "true";
			}

			this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts), true);
			this._updateMaxAllowedCharsToUpdate();
		};

SocialZimlet.prototype._saveAutoShortenUrlPref =
		function(ev) {
			if (this.autoShortenCheckbox.checked) {
				this.shortenUrlButtonDiv.style.display = "none";
			} else {
				this.shortenUrlButtonDiv.style.display = "block";
			}
			this.setUserProperty("social_pref_autoShortenURLOn", this.autoShortenCheckbox.checked, true);
		};

SocialZimlet.prototype.showNumberOfLetters =
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
			this.updateButton.setEnabled(true);

			if (val.toLowerCase().indexOf("d @") == 0 && len == 3) {
				appCtxt.getAppController().setStatusMsg(this.getMessage("sendDirectMsg"), ZmStatusView.LEVEL_WARNING);
			}
			this._updateNumberOfLettersField(len);
			if (this._autoShorten && this.autoShortenCheckbox.checked) {
				this.autoShortenURL(val);
			}
		};

SocialZimlet.prototype._updateNumberOfLettersField =
		function(count) {
			var clr = "";
			var left = this.maxAllowedCharsToUpdate - count;
			if (left >= 0) {
				clr = "green";
			} else {
				clr = "red";
			}
			this.nuLettersAllowedField.innerHTML = left;
			this.nuLettersAllowedField.style.color = clr;
		};

SocialZimlet.prototype._updateMaxAllowedCharsToUpdate =
		function () {
			this.maxAllowedCharsToUpdate = -1;
			for (var id in this.allAccounts) {
				var account = this.allAccounts[id];
				if (account.type == "twitter" && account.__on == "true") {
					this.maxAllowedCharsToUpdate = 140;
					break;
				} else if (account.type == "facebook" && account.__on == "true") {
					this.maxAllowedCharsToUpdate = 420;
				}
			}
			if (this.updateField && this.nuLettersAllowedField) {
				var len = this.updateField.value == this.getMessage("whatAreYouDoing") ? 0 : this.updateField.value.length;
				this._updateNumberOfLettersField(len);
			}

		};

SocialZimlet.prototype._showHideMaxAlowedCharsDiv =
		function() {
			if (this.maxAllowedCharsToUpdate == -1) {
				this.nuLetterAllowedDiv.style.display = "none";
			} else {
				this.nuLetterAllowedDiv.style.display = "block";
			}
		};

SocialZimlet.prototype.autoShortenURL =
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
			var urlsToShorten = this.getUrlsToShorten(potentialUrlsToShorten);
			for (var i = 0; i < urlsToShorten.length; i++) {
				this._postToUrlShortner({longUrl:urlsToShorten[i],callback:null});
			}
			this.isTextPasted = false;
		};

SocialZimlet.prototype.getUrlsToShorten =
		function(potentialUrlsToShorten) {
			var urlsToShorten = new Array();
			for (var i = 0; i < potentialUrlsToShorten.length; i++) {
				var url = potentialUrlsToShorten[i];
				if (!this.shortnersRegex.test(url)) {
					urlsToShorten.push(url);
				}
			}
			return urlsToShorten;
		};

SocialZimlet.prototype.twitterSearchKeyHdlr =
		function(ev) {
			var event = ev || window.event;
			if (event.keyCode == 13) {
				this._twitterSearchBtnListener();
			}
		};

SocialZimlet.prototype._twitterSearchBtnListener =
		function() {
			var val = document.getElementById("social_searchField").value;
			if (AjxStringUtil.trim(val) == "" || val.indexOf("<") >= 0 || val.length > 20) {
				return;
			}

			var tableId = this._showCard({headerName:val, type:"SEARCH", autoScroll:true});
			var sParams = {query:val, tableId:tableId, type:"SEARCH"};
			this.twitter.twitterSearch(sParams);
			var timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
			this.tableIdAndTimerMap[tableId] = timer;
			var search = {name:val, axn:"on", _p:""};
			this.tSearchFolders.push({name:val, icon:"SearchFolder", account:"", type:"SEARCH", search:search});

			this.twitter.allSearches.push(search);
			this.tableIdAndSearchMap[tableId] = search;
			this.twitter._updateAllSearches(val, "on");
		};

SocialZimlet.prototype._loadInformation =
		function() {
			this.twitter.getTwitterTrends();
			this.tweetmeme.loadTweetMemeCategories();
			this.digg.getDiggCategories();
		};

SocialZimlet.prototype._constructSkin =
		function() {
			var subs = {
				undo: this.getMessage("undo"),
				autoShortenUrl: this.getMessage("autoShortenUrl"),
				charactersLeft: this.getMessage("charactersLeft"),
				whatAreYouDoingMsg: this.getMessage("whatAreYouDoing")
			};
			return AjxTemplate.expand("com_zimbra_social.templates.Social#Skin", subs);
		};

SocialZimlet.prototype._setMainCardHeight =
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

SocialZimlet.prototype._resizeHandler =
		function(ev) {
			this._setMainCardHeight();
		}

SocialZimlet.prototype._addUpdateToCheckboxes =
		function() {
			var html = [];
			var idx = 0;
			var hasAccounts = false;
			html[idx++] = "<TABLE>";
			html[idx++] = "<TR><td>";
			html[idx++] = "<label style='font-size:12px;color:black;font-weight:bold'>" + this.getMessage("updateTo");
			html[idx++] = "</label>";
			html[idx++] = "</TD>";
			for (var id in this.allAccounts) {
				hasAccounts = true;
				var turnOnStr = "";
				if (this.allAccounts[id].__on == "true") {
					turnOnStr = "checked";
				}
				var chkbxId = this.allAccounts[id].checkboxId = "social_updateToCheckbox_" + id;
				html[idx++] = this._getUpdateToChkboxCellHtml(chkbxId, turnOnStr, this.allAccounts[id].name, this.allAccounts[id].type);

			}
			for (var i = 0; i < this.socialcastAccounts.length; i++) {
				hasAccounts = true;
				turnOnStr = "";
				var account = this.socialcastAccounts[i];
				if (account.__on == "true") {
					turnOnStr = "checked";
				}
				var chkbxId = this.socialcastAccounts[i].checkboxId = "social_updateToCheckbox_" + Dwt.getNextId();
				var menuId = this.socialcastAccounts[i].menuId = "social_socialcastMenuId_" + Dwt.getNextId();
				html[idx++] = this._getUpdateToChkboxCellHtml(chkbxId, turnOnStr, account.name, "SOCIALCAST", true, menuId);
			}
			html[idx++] = "<td id='social_updateMenuTD'>";

			"</td>";
			html[idx++] = "</TR></TABLE>";

			if (hasAccounts)
				return html.join("");
			else {
				return ["<label style='font-size:12px;color:#555555;font-style:italic'>",this.getMessage("noAccountsMsg"),"</label>"].join("");
			}
		};

SocialZimlet.prototype._addSocialcastGroupsMenuHndler = function() {
	for (var i = 0; i < this.socialcastAccounts.length; i++) {
		var account = this.socialcastAccounts[i];
		var menuId = account.menuId;
		if (!menuId) {
			return;
		}
		var div = document.getElementById(menuId);
		if (div) {
			//div.onclick =  (new AjxListener(this.socialcast, this.socialcast.showGroupsMenu, [account]));
			div.onclick = AjxCallback.simpleClosure(this.socialcast.showGroupsMenu, this.socialcast, account);
		}
	}
};
/*
 SocialZimlet.prototype._setUpdateMenu = function() {
 if(!document.getElementById("social_updateMenuTD")) {
 return;
 }
 btn = new DwtButton({parent:this.getShell()});
 btn.setText("Update To:");
 btn.setImage("Zimbra");
 var menu = new ZmPopupMenu(btn); //create menu
 btn.setMenu(menu);//add menu to button
 document.getElementById("social_updateMenuTD").appendChild(btn.getHtmlElement());
 for (var id in this.allAccounts) {
 var account = this.allAccounts[id];
 var name = account.name;
 var checked = account.__on == "true" ? true : false;
 var id = "menuItemId4";
 var icon = "social_twitterIcon";
 if (account.type == "facebook") {
 icon = "social_facebookIcon";
 }
 var mi = menu.createMenuItem(id, {image:icon, text:name, style:DwtMenuItem.CHECK_STYLE});
 mi.addSelectionListener(new AjxListener(this, this._buttonListener));
 if (checked) {
 mi.setChecked(true, true);//sets the item as checked
 }
 }
 for (var i = 0; i < this.socialcastAccounts.length; i++) {
 turnOnStr = "";
 var account = this.socialcastAccounts[i];
 var name = account.n;
 var checked = account.__on == "true" ? true : false;
 var id = "social_updateToCheckbox_" + Dwt.getNextId();
 var icon = "social_socialcastIcon";
 var gm = this.getUserProperty(account.e+"_groupMemberships");
 if(!gm) {
 gm = [{n:"My Colleagues", id:""}];
 } else  {
 gm = JSON.parse(gm);
 }
 var mi = menu.createMenuItem(id, {image:icon, text:name, style:DwtMenuItem.CASCADE_STYLE});
 var submenu = new ZmPopupMenu(mi); //create submenu
 mi.setMenu(submenu);//add submenu to menuitem

 var mi = submenu.createMenuItem(id, {image:icon, text:"My Colleagues", style:DwtMenuItem.RADIO_STYLE});
 mi.addSelectionListener(new AjxListener(this, this._buttonListener, "socialcast_group_mycolleagues"));
 for(var j =0; j < gm.length; j++) {
 var group = gm[j];
 var checked = false;
 var id = group.id;
 var id = "socialcast_group_id"+id;
 var mi = submenu.createMenuItem(id, {image:icon, text:group.n, style:DwtMenuItem.RADIO_STYLE});
 mi.addSelectionListener(new AjxListener(this, this._buttonListener, id));
 if (checked) {
 mi.setChecked(true, true);//sets the item as checked
 }
 }
 }
 };
 */
SocialZimlet.prototype._getUpdateToChkboxCellHtml =
		function(chkbxId, turnOnStr, name, type, hasMenu, menuId) {
			type = type.toUpperCase();
			var icon = "";
			if (type == "FACEBOOK") {
				icon = "social_facebookIcon";
			} else if (type == "TWITTER") {
				icon = "social_twitterIcon";
			} else if (type == "SOCIALCAST") {
				icon = "social_socialcastIcon";
			}
			var html = [];
			var idx = 0;
			html[idx++] = "<TD valign='middle' align=center>";
			html[idx++] = "<input type='checkbox'  " + turnOnStr + "  id='" + chkbxId + "'>";
			html[idx++] = "</TD>";
			html[idx++] = "<td>";
			html[idx++] = AjxImg.getImageHtml(icon);
			html[idx++] = "</td>";
			html[idx++] = "<TD valign='middle' align=center>";
			html[idx++] = name;
			html[idx++] = "</td>";
			if (hasMenu && menuId) {
				html[idx++] = "<td><div class='ImgNodeExpanded' id='" + menuId + "'></div></td>";
			}
			html[idx++] = "<td width=20px></td>";
			return html.join("");
		};

SocialZimlet.prototype._getMaxHeaderTextLength =
		function() {
			if (!this.maxHeaderTextLength) {
				var cardWidth = parseInt(this.preferences.social_pref_cardWidthList.replace("px", ""));
				this.maxHeaderTextLength = (cardWidth / 50) * 2;
				this.maxHeaderTextLength = this.maxHeaderTextLength + 2;
			}
			return this.maxHeaderTextLength;
		};


SocialZimlet.prototype._showCard =
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

			if (headerName.length > this._getMaxHeaderTextLength()) {
				trimName = headerName.substring(0, this._getMaxHeaderTextLength()) + "..";
			}

			var prettyName = type.toLowerCase();
			prettyName = prettyName.charAt(0).toUpperCase() + prettyName.slice(1) + ": " + trimName;
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
			} else if (type == "SOCIALCAST") {
				hdrCellColor = "white";
				iconName = "social_socialcastIcon";
				hdrClass = "social_axnClass social_socialcastColor";
			}
			var hdrCellStyle = "style='font-size:12px;color:" + hdrCellColor + ";font-weight:bold;font-family:'Lucida Grande',sans-serif;'";
			var card = "";
			var sDistance = "";
			if (type == "PROFILE" && tweetTableId) { //inset profile card right-next to the from-card
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
				if (type == "ACCOUNT" || type == "SOCIALCASE" || type == "FACEBOOK" || type == "SEARCH") {
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
			if (type == "ACCOUNT" || type == "FACEBOOK" || type == "SOCIALCAST" || type == "SEARCH") {
				html[i++] = "<td width='5%' valign='middle' align='right'>";
				html[i++] = "<img  title='" + this.getMessage("moveLeft") + "' src='" + this.getResource("social_leftArrow.png") + "' id='social_leftArrow" + this.cardIndex + "'/></td>";
				html[i++] = "<td width='5%' valign='middle' align='left'>";
				html[i++] = "<img  title='" + this.getMessage("moveRight") + "' src='" + this.getResource("social_rightArrow.png") + "' id='social_rightArrow" + this.cardIndex + "'/></td>";
			}

			if (type == "ACCOUNT" || type == "SEARCH" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS" || type == "FACEBOOK" || type == "SOCIALCAST") {
				html[i++] = "<td width='5%'valign='middle'>";
				html[i++] = "<img    title='" + this.getMessage("markAsRead") + "' src='" + this.getResource("social_markread.gif") + "' id='social_markAsReadBtn" + this.cardIndex + "' />";
				html[i++] = "</td>";
			}

			html[i++] = "<td width='5%' valign='middle'></td>";
			if (type != "PROFILE") {
				html[i++] = "<td width='5%' valign='middle'>";
				html[i++] = "<img  title='" + this.getMessage("refreshThisFeed") + "' src='" + this.getResource("social_refreshBtn.gif") + "' id='social_refreshBtn" + this.cardIndex + "'/></td>";
			}

			html[i++] = "<td width='5%' valign='middle'></td><td width='5%' valign='middle'>";
			html[i++] = "<img   title='" + this.getMessage("closeThisFeed") + "' src='" + this.getResource("social_closeBtn.png") + "' id='social_closeBtn" + this.cardIndex + "'/></td>";

			html[i++] = "</tr>";
			html[i++] = "</table>";
			html[i++] = "</DIV>";

			html[i++] = "</TD>";
			html[i++] = "</tr>";
			html[i++] = "</table>";
			html[i++] = "</DIV>";

			html[i++] = "<DIV id='" + cardInfoSectionId + "' style='overflow:auto;height:" + this._mainCardsHeight + ";width:" + this.preferences.social_pref_cardWidthList + "' class='social_individualCardClass'>";
			html[i++] = "</DIV>";
			html[i++] = "</div>";
			card.innerHTML = html.join("");

			var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
			var callback = AjxCallback.simpleClosure(this._handleCloseButton, this, params);
			document.getElementById("social_closeBtn" + this.cardIndex).onclick = callback;

			if (type != "PROFILE") {
				var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
				var callback = AjxCallback.simpleClosure(this._handleRefreshButton, this, params);
				document.getElementById("social_refreshBtn" + this.cardIndex).onclick = callback;
			}
			if (type == "ACCOUNT" || type == "SEARCH" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS" || type == "FACEBOOK" || type == "SOCIALCAST") {
				var params = {row:row, cellId:card.id, tableId:cardInfoSectionId, headerName:origHeaderName, type:type}
				var callback = AjxCallback.simpleClosure(this._handleMarkAsReadButton, this, params);
				document.getElementById("social_markAsReadBtn" + this.cardIndex).onclick = callback;
			}

			if (type == "ACCOUNT" || type == "FACEBOOK" || type == "SOCIALCAST" || type == "SEARCH") {
				var callback = AjxCallback.simpleClosure(this._swapColumns, this, "social_card" + this.cardIndex, 1);
				document.getElementById("social_rightArrow" + this.cardIndex).onclick = callback;

				var callback = AjxCallback.simpleClosure(this._swapColumns, this, "social_card" + this.cardIndex, -1);
				document.getElementById("social_leftArrow" + this.cardIndex).onclick = callback;
			}
			if (autoScroll) {
				document.getElementById('social_twitterCardsDiv').scrollLeft =
						(parseInt(this.preferences.social_pref_cardWidthList.replace("px", "")) + 10) * sDistance;
			}
			this._setMsgToCard(cardInfoSectionId, this.getMessage("loading"));
			return cardInfoSectionId;
		};

SocialZimlet.prototype._swapColumns =
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
			this._saveAllCardsAndPositions();
			this.saveUserProperties();
			appCtxt.getAppController().setStatusMsg(this.getMessage("feedPositionSaved"), ZmStatusView.LEVEL_INFO);
		};

SocialZimlet.prototype._saveAllCardsAndPositions =
		function() {
			for (var tableId in this.tableIdAndAccountMap) {
				this.tableIdAndAccountMap[tableId]["__pos"] = this._getTableCardPosition(tableId);
			}
			this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString());

			for (var tableId in this.tableIdAndSearchMap) {
				this.tableIdAndSearchMap[tableId]["__pos"] = this._getTableCardPosition(tableId);
			}
			this.setUserProperty("social_AllTwitterSearches", this.twitter.getAllSearchesAsJSON());
		};

SocialZimlet.prototype._getTableCardPosition =
		function(tableId) {
			if (!this._twitterCardsParentTable) {
				this._twitterCardsParentTable = document.getElementById('social_twitterCardsParentTable');
			}
			var reqId = tableId.replace("social_cardInfoSectionId", "social_card");
			var cells = this._twitterCardsParentTable.rows[0].cells;
			for (var i = 0; i < cells.length; i++) {
				if (cells[i].id == reqId) {
					return i;
				}
			}
			return -1;
		};

SocialZimlet.prototype._setCardUnreadCount =
		function(tableId, unReadCount, type) {
			var currentPage = 1;
			if (this.tableIdAndPageNumberMap[tableId]) {
				currentPage = this.tableIdAndPageNumberMap[tableId];
			}
			this._storeUnreadCount(tableId, unReadCount);
			var totalUnreadCount = this._getTotalUnreadCount(tableId, unReadCount);
			var itemsLimit = this._getItemsLimit(type);
			itemsLimit = itemsLimit + (currentPage - 1) * itemsLimit;
			var plusStr = "";
			if (totalUnreadCount == itemsLimit) {
				plusStr = "+";
			}
			var cellId = tableId.replace("social_cardInfoSectionId", "social_unreadCountCell");
			var cell = document.getElementById(cellId);
			if (cell == null)
				return;

			if (totalUnreadCount == 0) {
				cell.className = "";
				cell.innerHTML = "";
			} else {
				cell.className = "social_unReadClass";
				cell.innerHTML = totalUnreadCount + plusStr;
			}
		};


SocialZimlet.prototype._getItemsLimit =
		function(type) {
			var itemsLimit = 0;
			if (type == "FACEBOOK") {
				itemsLimit = this.facebook.itemsLimit;
			} else if (type == "SEARCH") {
				itemsLimit = this.preferences.social_pref_numberofTweetsSearchesToReturn;
			} else if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
				itemsLimit = this.preferences.social_pref_numberofTweetsToReturn;
			}
			return itemsLimit;
		};

SocialZimlet.prototype._handleUndoShortenLink =
		function() {
			if (this.latestShortenedUrl) {
				this.updateField.value = this.updateField.value.replace(this.latestShortenedUrl.shortUrl, this.latestShortenedUrl.longUrl);
			}
			this.undoShortenUrlDiv.style.display = "none";
		};

SocialZimlet.prototype._handleRefreshButton =
		function(params) {
			var tableId = params.tableId;
			this._resetPagination(tableId);
			this._setMsgToCard(params.tableId, this.getMessage("loading"));
			this._doRefreshFeeds(tableId, params.type, params.headerName);
		};

SocialZimlet.prototype._resetPagination =
		function(tableId) {
			this.tableIdAndPageNumberMap[tableId] = 1;
			this._tableIdAndBottomPostIdMap[tableId] = null;
			this.tableIdAndTopPostIdMap[tableId] = null;
			this.tableIdAndRefreshType[tableId] = "REFRESH";
		};

SocialZimlet.prototype._doRefreshFeeds =
		function(tableId, type, headerName) {
			if (type == "SEARCH" || type == "TREND") {
				this.twitter.twitterSearch({query:headerName, tableId:tableId, type:type});
			} else if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
				this.twitter.getTwitterFeeds({tableId: tableId, account: this.tableIdAndAccountMap[tableId], type:type});
			} else if (type == "TWEETMEME") {
				this.tweetmeme.tweetMemeSearch({headerName:headerName, tableId:tableId});
			} else if (type == "DIGG") {
				this.digg.diggSearch({headerName:headerName, tableId:tableId});
			} else if (type == "FACEBOOK") {
				this.facebook._fbGetStream(tableId, this.tableIdAndAccountMap[tableId]);
			} else if (type == "SOCIALCAST") {
				var scStreamId = this.tableIdAndSCStreamMap[tableId] ? this.tableIdAndSCStreamMap[tableId].id : null;
				this.socialcast.getMessages(tableId, this.tableIdAndAccountMap[tableId], scStreamId);
			}
		};

SocialZimlet.prototype._handleCardMouseOver =
		function(id) {
			document.getElementById(id).style.display = "block";
		};

SocialZimlet.prototype._handleCardMouseOut =
		function(id) {
			document.getElementById(id).style.display = "none";
		};

SocialZimlet.prototype._handleMarkAsReadButton =
		function(params) {
			var type = params.type;
			var tableId = params.tableId;
			var _p = this.tableIdAndHighestPostIdMap[tableId];
			this.tableIdAndMarkAsReadId[tableId] = _p;
			var feedObj = type == "SEARCH" ? this.tableIdAndSearchMap[tableId] : this.tableIdAndAccountMap[tableId];
			if (type == "SEARCH") {
				feedObj._p = _p;
				this.twitter._updateAllSearches(feedObj.name, "on");
			} else if (type == "ACCOUNT" || type == "FACEBOOK") {
				feedObj._p = _p;
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else if (type == "SOCIALCAST") {
				feedObj._p = _p;
				this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts), true);
			} else if (type == "MENTIONS") {
				feedObj._m = _p;
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else if (type == "DIRECT_MSGS") {
				feedObj._d = _p;
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else if (type == "SENT_MSGS") {
				feedObj._s = _p;
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			}
			this._resetPagination(tableId);
			this._doRefreshFeeds(tableId, type, feedObj.name);
		};

SocialZimlet.prototype._handleDeleteButton =
		function(params) {
			var headerName = params.headerName;
			for (var i = 0; i < this.tSearchFolders.length; i++) {
				if (this.tSearchFolders[i].name == headerName) {
					this.tSearchFolders.splice(i, 1);
					this._createTreeView();
					break;
				}
			}
			params.row.deleteCell(document.getElementById(params.cellId).cellIndex);
			this.twitter._updateAllSearches(params.headerName, "delete");
		};

SocialZimlet.prototype._handleCloseButton =
		function(params) {
			params.row.deleteCell(document.getElementById(params.cellId).cellIndex);
			this.twitter._updateAllSearches(params.headerName, "off");
			var type = params.type;
			if (type == "ACCOUNT" || type == "FACEBOOK") {
				this.tableIdAndAccountMap[params.tableId].isDisplayed = false;
				this.tableIdAndAccountMap[params.tableId]["__s"] = "0";
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
				delete this.tableIdAndAccountMap[params.tableId];
			} else if (type == "SOCIALCAST") {
				var tableId = params.tableId;
				var s = this.tableIdAndSCStreamMap[tableId];
				if (s) {
					for (var i = 0; i < this.socialcastDisplayedStreams.length; i++) {
						var sObj = this.socialcastDisplayedStreams[i];
						if (sObj.un == s.un && sObj.n == s.n && sObj.id == s.id) {
							this.socialcastDisplayedStreams[i].__s = "0";
							break;
						}
					}
					this.setUserProperty("socialcastDisplayedStreams", JSON.stringify(this.socialcastDisplayedStreams), true);
				} else {
					this.tableIdAndAccountMap[params.tableId].isDisplayed = false;
					this.tableIdAndAccountMap[params.tableId]["__s"] = "0";
					this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts), true);
				}
			}
			if (params.tableId) {
				var timer = this.tableIdAndTimerMap[params.tableId];
				if (timer) { //remove update timers
					clearInterval(timer);
				}
			}
			this.tableIdAndCacheMap[params.tableId] = [];
		};

//---------------------------------------------------------------------------------
// OVERRIDE ZIMLET FRAMEWORK FUNCTIONS AND CREATE APP
//---------------------------------------------------------------------------------
SocialZimlet.prototype.doubleClicked =
		function() {
			this.singleClicked();
		};

SocialZimlet.prototype.singleClicked =
		function() {
			//DO NOTHING
		};

SocialZimlet.prototype._createsocialApp =
		function() {
			this._socialAppName = this.createApp(this.getMessage("social"), "social-icon", this.getMessage("socalAppTooltip"));
		};

SocialZimlet.prototype.appActive = function(appName, active) {
	if (active) {
		if (!this._launched) {
			this.appLaunch(appName);
		}
		this.appName = appName;
		this._sociallistViews = [];
		this.initializeVariables();
		//welcome dlg
		if (this.getUserProperty("social_pref_dontShowWelcomeScreenOn") == "false") {
			this.preferences._showWelcomeDlg();
		}
		document.title = this.getMessage("zimbraSocial");
	}
	else {
		this._hideApp(appName);
	}
};

SocialZimlet.prototype._hideApp = function(appName) {
	//dont do anything
};

SocialZimlet.prototype.appLaunch = function(appName, params) {
	if (this._socialAppName != appName) {
		return;
	}
	if (this._launched) {
		return;
	}
	this.app = appCtxt.getApp(appName);

	this.showAppView();
	this._launched = true;
};


SocialZimlet.prototype._createTreeView =
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

			html[i++] = this._getTreeHeaderHTML(this.getMessage("accounts"), expandIconId);	//header
			html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
			for (var j = 0; j < this.socialcastAccounts.length; j++) {
				var folder = this.socialcastAccounts[j];
				folder["name"] = folder.n;
				folder["icon"] = "social_socialcastIcon";
				folder["type"] = "SOCIALCAST";
				var streams = this.getUserProperty(folder.e + "_streams");
				try {
					if (streams && streams != "") {
						var streamObjs = JSON.parse(streams);
					}
				} catch (e) {
					//ignore
				}
				if (streamObjs && (streamObjs instanceof Array) && streamObjs.length > 0) {
					var childExpandIconId = "social_expandIcon_" + Dwt.getNextId();
					this.expandIconAndFolderTreeMap[childExpandIconId] = new Array();
					html[i++] = this._getFolderHTML(folder, expandIconId, childExpandIconId, false, true);
					for (var m = 0; m < streamObjs.length; m++) {
						var sObj = streamObjs[m];
						html[i++] = this._getFolderHTML({name:sObj.n, icon:"social_socialcastIcon", account: folder, type:"SOCIALCAST", streamId:sObj.id, streamName:sObj.n}, expandIconId, childExpandIconId, true);
					}
				} else {
					html[i++] = this._getFolderHTML(this.socialcastAccounts[j], expandIconId);
				}
			}
			for (var j = 0; j < this.systemFolders.length; j++) {
				var folder = this.systemFolders[j];
				if (folder.account.type == "twitter") {
					var childExpandIconId = "social_expandIcon_" + Dwt.getNextId();
					this.expandIconAndFolderTreeMap[childExpandIconId] = new Array();
					html[i++] = this._getFolderHTML(folder, expandIconId, childExpandIconId);
					html[i++] = this._getFolderHTML({name:"@" + folder.account.name + " " + this.getMessage("mentions"), icon:"social_twitterIcon", account: folder.account, type:"MENTIONS"}, expandIconId, childExpandIconId, true);
					html[i++] = this._getFolderHTML({name:this.getMessage("directMsgs"), icon:"social_twitterIcon", account: folder.account, type:"DIRECT_MSGS"}, expandIconId, childExpandIconId, true);
					html[i++] = this._getFolderHTML({name:this.getMessage("sentMsgs"), icon:"social_twitterIcon", account: folder.account, type:"SENT_MSGS"}, expandIconId, childExpandIconId, true);
				} else {
					html[i++] = this._getFolderHTML(folder, expandIconId);
				}
			}

			html[i++] = "</div>";
			//-----------------------

			//-----------------------
			var expandIconId = "social_expandIcon_" + Dwt.getNextId();
			this.expandIconAndFolderTreeMap[expandIconId] = new Array();
			html[i++] = this._getTreeHeaderHTML(this.getMessage("twitterSearches"), expandIconId);	//header
			html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
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
				html[i++] = this._getTreeHeaderHTML(this.getMessage("twitterTrends"), expandIconId);	//header
				html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
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
				html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
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
				html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
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
				this.prefFolders.push({name:this.getMessage("preferences"), icon:"Preferences", account:"", type:"PREFERENCES"});
				this.prefFolders.push({name:this.getMessage("help"), icon:"Help", account:"", type:"HELP"});
			}
			this.expandIconAndFolderTreeMap[expandIconId] = new Array();
			html[i++] = this._getTreeHeaderHTML(this.getMessage("settings"), expandIconId);	//header
			html[i++] = "<div class='DwtTreeItemLevel1ChildDiv' style='display: block;'>";
			for (var j = 0; j < this.prefFolders.length; j++) {
				var folder = this.prefFolders[j];
				html[i++] = this._getFolderHTML(folder, expandIconId);
			}
			html[i++] = "</div>";
			//-----------------------

			element.innerHTML = html.join("");
			element.onclick = AjxCallback.simpleClosure(this._handleTreeClick, this);

		};

SocialZimlet.prototype._getTreeHeaderHTML =
		function(treeName, expandIconId) {
			var subs = {
				treeName: treeName,
				expandIconId: expandIconId
			};
			return AjxTemplate.expand("com_zimbra_social.templates.Social#CardHeader", subs);
		};

SocialZimlet.prototype._getFolderHTML =
		function(folder, expandIconId, childExpandIconId, isSubFolder, isTopLevelParentFolder) {
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
			if (folder.type == "ACCOUNT" || folder.type == "DIRECT_MSGS" || folder.type == "MENTIONS"
					|| folder.type == "SENT_MSGS" || folder.type == "FACEBOOK") {
				id = "socialTreeItem__" + folder.type + "_" + folder.account.type + "_" + folder.account.name;
				this.treeIdAndAccountMap[id] = folder.account;
			} else if (folder.type == "SOCIALCAST") {
				if (folder.streamId && folder.streamName) {
					id = "socialTreeItem__" + folder.type + "STREAM:" + folder.streamName + "==::==" + folder.streamId;
				} else {
					id = "socialTreeItem__" + folder.type + "_" + folder.un;
				}
				this.treeIdAndAccountMap[id] = folder;
			} else {
				id = "socialTreeItem__" + folder.type + "_" + Dwt.getNextId();
			}
			if (folder.type == "SEARCH") {
				this.treeIdAndSearchMap[id] = folder.search;
			}
			if (expandIconId && !this.expandIconAndFolderTreeMap[expandIconId]) {
				this.expandIconAndFolderTreeMap[expandIconId] = new Array();
			}
			this.expandIconAndFolderTreeMap[expandIconId].push(id);

			if (isSubFolder) {
				if (childExpandIconId && !this.expandIconAndFolderTreeMap[childExpandIconId]) {
					this.expandIconAndFolderTreeMap[childExpandIconId] = new Array();
				}
				this.expandIconAndFolderTreeMap[childExpandIconId].push(id);
			}
			html[i++] = "<div class='DwtTreeItem' id='" + id + "'>";

			html[i++] = "<TABLE width=100% cellpadding='0' cellspacing='0'>";
			html[i++] = "<TR>";
			html[i++] = "<TD style='width:16px;height:16px' align='center'>";
			if (folder.account && folder.account.type == "twitter" && folder.type == "ACCOUNT" || isTopLevelParentFolder) {
				html[i++] = "<div class='ImgNodeExpanded' id= '" + childExpandIconId + "'/>";
			} else {
				html[i++] = AjxImg.getImageHtml("Blank_16");
			}
			html[i++] = "</TD>";
			if (isSubFolder) {
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
			}
			html[i++] = "<TD style='width:16px;height:16px;padding-right:5px'>";
			html[i++] = AjxImg.getImageHtml(folder.icon);
			html[i++] = "</TD>";
			html[i++] = "<TD class='DwtTreeItem-Text' nowrap=''>";
			html[i++] = folder.name;
			html[i++] = "</TD>";
			html[i++] = "<TD style='width:16px;height:16px;padding-right:5px'>";
			html[i++] = AjxImg.getImageHtml("Blank_16");
			html[i++] = "</TD>";
			if (folder.type == "SEARCH") {
				html[i++] = "<TD style='width:16px;height:16px;padding-right:5px'>";
				html[i++] = "<div class='ImgClearSearch'/>";
				html[i++] = "</TD>";
			}
			html[i++] = "</TR>";
			html[i++] = "</TABLE>";
			html[i++] = "</div>";
			html[i++] = "</div>";
			return html.join("");
		};

SocialZimlet.prototype._handleTreeClick =
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
			var elId = el.id;
			if (elId.indexOf("socialTreeItem__ACCOUNT") == 0) {
				account = this.treeIdAndAccountMap[elId];
				tableId = this._showCard({headerName:label, type:"ACCOUNT", autoScroll:true});
				this.tableIdAndAccountMap[tableId] = account;
				this.tableIdAndMarkAsReadId[tableId] = account._p;
				this.twitter.getTwitterFeeds({tableId: tableId, account: account, type:"ACCOUNT"});
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.getTwitterFeeds, this.twitter, {tableId: tableId, account: account, type:"ACCOUNT"}), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
				account.isDisplayed = true;
				account["__s"] = "1";//__s means shown, 1 means true
				//this._saveAllCardsAndPositions();
			} else if (elId.indexOf("socialTreeItem__MENTIONS") == 0) {
				account = this.treeIdAndAccountMap[elId];
				tableId = this._showCard({headerName:label, type:"MENTIONS", autoScroll:true});
				this.tableIdAndAccountMap[tableId] = account;
				this.tableIdAndMarkAsReadId[tableId] = account._m;
				this.twitter.getTwitterFeeds({tableId: tableId, account: account, type:"MENTIONS"});
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.getTwitterFeeds, this.twitter, {tableId: tableId, account: account, type:"MENTIONS"}), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			} else if (elId.indexOf("socialTreeItem__DIRECT_MSGS") == 0) {
				account = this.treeIdAndAccountMap[elId];
				tableId = this._showCard({headerName:label, type:"DIRECT_MSGS", autoScroll:true});
				this.tableIdAndAccountMap[tableId] = account;
				this.tableIdAndMarkAsReadId[tableId] = account._d;
				this.twitter.getTwitterFeeds({tableId: tableId, account: account, type:"DIRECT_MSGS"});
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.getTwitterFeeds, this.twitter, {tableId: tableId, account: account, type:"DIRECT_MSGS"}), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			} else if (elId.indexOf("socialTreeItem__SENT_MSGS") == 0) {
				account = this.treeIdAndAccountMap[elId];
				tableId = this._showCard({headerName:label, type:"SENT_MSGS", autoScroll:true});
				this.tableIdAndAccountMap[tableId] = account;
				this.tableIdAndMarkAsReadId[tableId] = account._s;
				this.twitter.getTwitterFeeds({tableId: tableId, account: account, type:"SENT_MSGS"});
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.getTwitterFeeds, this.twitter, {tableId: tableId, account: account, type:"SENT_MSGS"}), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			} else if (elId.indexOf("socialTreeItem__MANAGE_ACCOUNTS") == 0) {
				this.preferences._showManageAccntsDlg();
			} else if (elId.indexOf("socialTreeItem__PREFERENCES") == 0) {
				this.preferences._showPreferencesDlg();
			} else if (elId.indexOf("socialTreeItem__HELP") == 0) {
				this.preferences._showWelcomeDlg();
			} else if (elId.indexOf("socialTreeItem__SEARCH") == 0) {
				var search = this.treeIdAndSearchMap[elId];
				if (origTarget.className == "ImgClearSearch") {//delete search
					this.twitter._updateAllSearches(search.name, "delete");
					return;
				}
				var search = this.treeIdAndSearchMap[elId];
				tableId = this._showCard({headerName:search.name, type:"SEARCH", autoScroll:true});
				this.tableIdAndSearchMap[tableId] = search;
				this.tableIdAndMarkAsReadId[tableId] = search._p;
				sParams = {query:search.name, tableId:tableId, type:"SEARCH"};
				this.twitter.twitterSearch(sParams);
				this.twitter._updateAllSearches(label, "on");
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			} else if (elId.indexOf("socialTreeItem__TREND") == 0) {
				var sParams = {headerName:label, type:"TREND", autoScroll:true};
				tableId = this._showCard(sParams);
				this.twitter.twitterSearch({query:label, tableId:tableId, type:"TREND"});
				timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
			} else if (elId.indexOf("socialTreeItem__TWEETMEME") == 0) {
				tableId = this._showCard({headerName:label, type:"TWEETMEME", autoScroll:true});
				this.tweetmeme.tweetMemeSearch({headerName:label, tableId:tableId});
			} else if (elId.indexOf("socialTreeItem__DIGG") == 0) {
				tableId = this._showCard({headerName:label, type:"DIGG", autoScroll:true});
				this.digg.diggSearch({headerName:label, tableId:tableId});
			} else if (elId.indexOf("socialTreeItem__FACEBOOK") == 0) {
				tableId = this._showCard({headerName:"facebook", type:"FACEBOOK",autoScroll:true});
				account = this.treeIdAndAccountMap[elId];
				this.facebook._fbGetStream(tableId, account);
				timer = setInterval(AjxCallback.simpleClosure(this.facebook._updateFacebookStream, this.facebook, tableId, account), 400000);
				this.tableIdAndTimerMap[tableId] = timer;
				this.tableIdAndAccountMap[tableId] = account;
				this.tableIdAndMarkAsReadId[tableId] = account._p;
				account.isDisplayed = true;
				account["__s"] = "1";//__s means shown, 1 means true
				//this._saveAllCardsAndPositions();
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
			} else if (elId.indexOf("socialTreeItem__SOCIALCAST") == 0) {
				account = this.treeIdAndAccountMap[elId];
				var streamid;
				var isStream = false;
				if (elId.indexOf("STREAM:") > 0) {
					var sObj = elId.split("STREAM:")[1];
					var tmpArry = sObj.split("==::==");
					var streamName = tmpArry[0];
					streamid = tmpArry[1];
					account = account.account;
					isStream = true;
				}
				if(account.isValid) {
					this._displaySocialcastCard(account, streamid, streamName);
				} else {
					this._showWarningMsg(this.getMessage("couldNotAuthenticateSC"));
				}

				/*
				 tableId = this._showCard({headerName:account.name, type:"SOCIALCAST",autoScroll:true});
				 this.socialcast.getMessages(tableId, account);
				 timer = setInterval(AjxCallback.simpleClosure(this.socialcast.getMessages, this.socialcast, tableId, account), 400000);
				 this.tableIdAndTimerMap[tableId] = timer;
				 this.tableIdAndAccountMap[tableId] = account;
				 this.tableIdAndMarkAsReadId[tableId] = account._p;
				 account["__s"] = "1";//__s means shown, 1 means true
				 */
				//this._saveAllCardsAndPositions();
				if (!isStream) {
					this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts));
				}
			}
			this.saveUserProperties();
		};

SocialZimlet.prototype._displaySocialcastCard =
		function(account, streamId, streamName) {
			var hdrName = streamName ? streamName : account.name;
			var un = account.un;
			tableId = this._showCard({headerName:hdrName, type:"SOCIALCAST",autoScroll:true});
			if (streamId) {
				this.tableIdAndSCStreamMap[tableId] = {un:un, id:streamId, n:streamName};
				var hash = un + streamName + streamId;
				if (!this.socialcastDisplayedStreamsHASH[hash]) {
					this.socialcastDisplayedStreams.push({un:un, n:streamName, id:streamId, __s:"1"});
				} else {
					this.socialcastDisplayedStreamsHASH[hash].__s = "1";
				}
				this.setUserProperty("socialcastDisplayedStreams", JSON.stringify(this.socialcastDisplayedStreams), true);
			}
			this.tableIdAndPageNumberMap[tableId] = 1;
			this.socialcast.getMessages(tableId, account, streamId);
			timer = setInterval(AjxCallback.simpleClosure(this.socialcast.getMessages, this.socialcast, tableId, account, streamId), 400000);
			this.tableIdAndTimerMap[tableId] = timer;
			this.tableIdAndAccountMap[tableId] = account;
			if (!streamId) {
				this.tableIdAndMarkAsReadId[tableId] = account._p;
				account["__s"] = "1";//__s means shown, 1 means true
			}
		};

SocialZimlet.prototype._treeListener =
		function(ev) {
			var txt = ev.item._text;
			if (ev.detail == 1)
				return;

			this._showStream();
		};

SocialZimlet.prototype._getShortNameFromName =
		function(name) {
			for (var i = 0; i < SocialZimlet.folders.length; i++) {
				var fldr = SocialZimlet.folders[i];
				if (fldr.n == name) {
					return fldr.sn;
				}
			}
			return "";
		};

SocialZimlet.prototype._getIdFromName =
		function(name) {
			for (var i = 0; i < SocialZimlet.folders.length; i++) {
				var fldr = SocialZimlet.folders[i];
				if (fldr.n == name) {
					return fldr.id;
				}
			}
			return "";
		};

SocialZimlet.prototype.showAppView =
		function() {
			this.addTwitterSearchWidget();
			this.app.setContent(this._constructSkin());
			this._view = this.app.getController().getView();
			this._view.addControlListener(new AjxListener(this, this._resizeHandler));//add resize handler
			this._setMainCardHeight();
			this._dontAutoScroll = true;
			this._updateAllWidgetItems({updateSearchTree:true, updateSystemTree:true, updateAccntCheckboxes:true, searchCards:true});
			this.toggleFields();
			this.updateUIWidgets();
			this._showHideMaxAlowedCharsDiv();

			this._addTweetButtons();
			this._loadInformation();
			this._setMainCardHeight();
			this._dontAutoScroll = false;
			this.miniDlg.miniDlgON = false;//set this first
		};

SocialZimlet.prototype.addTwitterSearchWidget =
		function() {
			var subs = {
				updateCheckBoxesHtml:  this._addUpdateToCheckboxes()
			};
			var toolbar = this.app.getToolbar();
			toolbar.getHtmlElement().innerHTML = AjxTemplate.expand("com_zimbra_social.templates.Social#TwitterSearchWidget", subs);
			//this._setUpdateMenu();
			this._addSocialcastGroupsMenuHndler();
		};

SocialZimlet.prototype._postToTweetOrFB =
		function(ev) {
			var event = ev || window.event;
			var code = event.which ? event.which : event.keyCode;
			if (code == 13) { //ignore enter key
				return;
			} else if (code == 118 && event.ctrlKey) {//see if they are pasting something
				this.isTextPasted = true;
				return;
			}

			var isDM = false;
			var noAccountSelected = true;

			var message = this.updateField.value;
			if (message.length == 0) {
				return;
			}
			if (message.toLowerCase().indexOf("d @") == 0) {
				isDM = true;
			}

			for (var id in this.allAccounts) {
				var account = this.allAccounts[id];
				if (account.type == "twitter") {
					if (message.length > 140) {
						appCtxt.getAppController().setStatusMsg(this.getMessage("moreThan140Chars"), ZmStatusView.LEVEL_WARNING);
						continue;
					}

					this.twitter.postToTwitter(account, message);
					noAccountSelected = false;
				} else if (account.__on == "true" && account.type == "facebook" && !isDM) {
					if (message.length > 420) {
						appCtxt.getAppController().setStatusMsg(this.getMessage("moreThan420Chars"), ZmStatusView.LEVEL_WARNING);
						continue;
					}
					noAccountSelected = false;
					this.facebook._publishToFacebook({account:account, message:message});
				}
			}
			for (var i = 0; i < this.socialcastAccounts.length; i++) {
				var account = this.socialcastAccounts[i];
				if (account.__on == "true" && account.type == "SOCIALCAST" && !isDM) {
					noAccountSelected = false;
					this.socialcast._publishToSocialcast({account:account, message:message});
				}
			}
			if (noAccountSelected) {
				appCtxt.getAppController().setStatusMsg(this.getMessage("pleaseSelectAnAccount"), ZmStatusView.LEVEL_WARNING);
				return;
			}
			if (this.miniDlg.socialMiniDialog && this.miniDlg.socialMiniDialog.isPoppedUp()) {
				this.miniDlg.socialMiniDialog.popdown();
				this.miniDlg.miniDlgON = false;//set this first
				this.toggleFields();
			}
		};

SocialZimlet.prototype.createCardView =
		function(params) {
			var tableId = params.tableId;
			var jsonObj = params.items;
			var type = params.type;
			var additionalParams = params.additionalParams;
			var plusDateFormat = "yyyy-MM-ddThh:mm:ss+hh:mm";
			var minusDateFormat = "yyyy-MM-ddThh:mm:ss-hh:mm";
			var account = "";
			if (this.miniDlg.miniDlgON) {//return if its from miniDlg view
				return;
			}
			if (!tableId) {
				return;
			}
			var html = [];
			var i = 0;

			if (jsonObj == undefined) {
				return;
			}
			var markAsReadId = this.tableIdAndMarkAsReadId[tableId];
			var numberOfPosts = jsonObj.length;
			var isPreviouslyReadFlagDisplayed = false;
			var unReadCount = 0;
			var accountName = "";
			var socialcastUserName = "";
			var socialcastUsersUrl = "";
			var socialTopicsbaseUrl = "";
			this._cacheResults(tableId, jsonObj);
			if (numberOfPosts == 0) {
				this._addNewerAndOlderLinks(tableId, numberOfPosts, html, "top", type);
				var i = html.length;
				html[i++] = "<br/><br/><div width=90% align=center><label style='color:#0000FF;font-weight:bold;font-size:12px'>" + this.getMessage("noDataFound") + "</label></div>";
				document.getElementById(tableId).innerHTML = html.join("");
				var query = "";
				if (additionalParams && additionalParams.query) {
					query = additionalParams.query;
				}
				this._addOlderAndNewerLinkHandlers(tableId, type, query);
				return;
			}
			if (jsonObj.error) {
				this._setErrorToCard(tableId, jsonObj.error);
				return;
			}
			if (type == "PROFILE_MSGS") {
				type = "ACCOUNT";
			}
			if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS"
					|| type == "SENT_MSGS" || type == "FACEBOOK" || type == "SOCIALCAST") {
				if (!this.tableIdAndAccountMap[tableId]) {
					return;
				}
				var account = this.tableIdAndAccountMap[tableId];
				accountName = account.name;
				if (type == "SOCIALCAST") {
					socialcastUserName = account.un;
					socialcastUsersUrl = ["https://", account.s, "/users/"].join("");
					socialTopicsbaseUrl = ["https://", account.s, "/topics/"].join("");
				}
			}

			for (var k = 0; k < numberOfPosts; k++) {
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
				var currentPostId = "";
				var rowId = Dwt.getNextId();
				var markAsUnread = false;
				var columnSpan = 2;
				if (type == "FACEBOOK") {
					currentPostId = obj.updated_time;
				} else if (type == "SOCIALCAST") {
					currentPostId = obj.created_at;
					if (currentPostId.indexOf("+") > 0) {
						currentPostId = AjxDateFormat.parse(plusDateFormat, currentPostId).getTime();
					} else {
						currentPostId = AjxDateFormat.parse(minusDateFormat, currentPostId).getTime();
					}
				} else {
					currentPostId = obj.id;
				}

				if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS" || type == "FACEBOOK" || type == "SOCIALCAST" || type == "SEARCH") {
					if (k == 0) {
						this.tableIdAndTopPostIdMap[tableId] = currentPostId;
						if (this._isPageDirtyWithNewPosts(tableId, currentPostId)) {
							return;
						}
						this._addNewerAndOlderLinks(tableId, numberOfPosts, html, "top", type);
						this._addRefreshNoticeDiv(tableId, html);
						i = html.length;
					}

					if (k == (numberOfPosts - 1)) {
						this._tableIdAndBottomPostIdMap[tableId] = currentPostId;
					}
					if (!markAsReadId || markAsReadId < currentPostId) {
						markAsUnread = true;
						columnSpan = 3;
						unReadCount++;
					}
					if (!this.tableIdAndHighestPostIdMap[tableId] || this.tableIdAndHighestPostIdMap[tableId] < currentPostId) {
						this.tableIdAndHighestPostIdMap[tableId] = currentPostId;
					}
				}

				if (type == "SEARCH" || type == "TREND") {//for both search and trends
					screen_name = obj.from_user;
					created_at = obj.created_at;
					text = " " + obj.text;
					userId = obj.from_user_id;
					source = AjxStringUtil.htmlDecode(obj.source).replace(/&quot;/g, "'");
					imageAnchor = "<TD width=48px height=48px align='center' valign='top'> ";
					imageAnchor = imageAnchor + "<div class='social_accountBg'><a  href='http://twitter.com/" + screen_name + "' target='_blank' style='color:white'>";
					imageAnchor = imageAnchor + "<img height='48' width='48' src='" + obj.profile_image_url + "' />";
					imageAnchor = imageAnchor + "</a></div>";
					imageAnchor = imageAnchor + "</td>";
				} else if (type == "TWEETMEME") {
					screen_name = "tweetmeme";
					created_at = obj.created_at;
					text = " " + obj.title + " " + obj.url;
					source = "tweetmeme";
					tweetcount = obj.url_count;
					imageAnchor = "<TD> ";
					imageAnchor = imageAnchor + "<a  href='" + obj.url + "' target='_blank' style='color:gray'>";
					imageAnchor = imageAnchor + "<table><tr>";
					imageAnchor = imageAnchor + "<td class='social_tweetMemeBg' width=48px height=48px align='center'  valign='middle'>";
					imageAnchor = imageAnchor + tweetcount + "</td></tr><tr><td class='social_tweetMemeRetweetBg'>" + this.getMessage("retweets") + "</td></tr></table>";
					imageAnchor = imageAnchor + "</a>";
					imageAnchor = imageAnchor + "</td>";
				} else if (type == "DIGG") {
					diggCount = obj.diggs;
					text = [" ", obj.title, " ", obj.link].join("");
					screen_name = "digg";
					source = "digg";
					created_at = obj.promote_date;
					imageAnchor = "<TD  valign='top'>";
					imageAnchor = imageAnchor + "<a  href='" + obj.href + "' target='_blank' style='color:gray'>";
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
						source = AjxStringUtil.htmlDecode(obj.source).replace(/&quot;/g, "'");
					else
						source = "web";

					imageAnchor = "<TD width=48px height=48px align='center'  valign='top'> ";
					imageAnchor = imageAnchor + "<div class='social_accountBg'>";
					imageAnchor = imageAnchor + "<a id='" + this._getAccountLinkId(screen_name, tableId) + "' class='FakeAnchor' style='color:white'>";
					imageAnchor = imageAnchor + "<img height='48' width='48' src='" + user.profile_image_url + "' />";
					imageAnchor = imageAnchor + "</a></div>";
					imageAnchor = imageAnchor + "</td>";
					notFollowing = user.following == null;
					followId = userId;
				} else if (type == "FACEBOOK") {
					if (obj.is_hidden) {
						continue;
					}

					var user = this.facebook.getFacebookProfile(obj.actor_id, tableId);
					screen_name = user.name;
					var targetUser = this.facebook.getFacebookProfile(obj.target_id, tableId);
					if (targetUser) {
						screen_name += " > " + targetUser.name;
					}
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
					imageAnchor = imageAnchor + "<div class='social_accountBg'><a href='" + user.url + "' target='_blank' style='color:white'>";
					imageAnchor = imageAnchor + "<img height='48' width='48' src='" + user.pic_square + "' />";
					imageAnchor = imageAnchor + "</a></div>";
					imageAnchor = imageAnchor + "</td>";
					notFollowing = user.following == null;
					followId = userId;
				} else if (type == "SOCIALCAST") {
					user = obj.user;
					userId = user.id;
					screen_name = user.username;
					var recipients = obj.recipients;

					source = obj.source ? obj.source.name : "web";
					profile_image_url = user.avatars.square45;
					text = " " + obj.body;
					created_at = obj.created_at;
					imageAnchor = "<TD width=48px height=48px align='center'  valign='top'> ";
					imageAnchor = imageAnchor + "<div class='social_accountBg'><a href='" + user.url + "' target='_blank' style='color:white'>";
					imageAnchor = imageAnchor + "<img height='48' width='48' src='" + profile_image_url + "' />";
					imageAnchor = imageAnchor + "</a></div>";
					imageAnchor = imageAnchor + "</td>";

				} else {
					return;
				}
				if (source.indexOf("<a ") >= 0) {
					source = source.replace("<a ", "<a target='_blank' style='font-size:11px;'");
				}
				var parsedDate = "";
				if (type != "FACEBOOK" && type != "DIGG" && type != "SOCIALCAST") {
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
					timeStr = tmpTime + " " + this.getMessage("secondsAgo");
				} else if (tmpTime < 60) {
					timeStr = tmpTime + " " + this.getMessage("minutesAgo");
				} else if (tmpTime >= 60) {
					tmpTime = Math.round(tmpTime / 60);
					if (tmpTime < 24) {
						if (tmpTime == 1)
							timeStr = this.getMessage("about") + " " + tmpTime + " " + this.getMessage("hourAgo");
						else
							timeStr = this.getMessage("about") + " " + tmpTime + " " + this.getMessage("hoursAgo");
						;
					} else {
						var d = new Date(parsedDate);
						var arry = d.toString().split(" ");
						timeStr = [ arry[1]," ", arry[2], " " + this.getMessage("at") + " ", d.getHours(),  ":",  d.getMinutes(),  ":",  d.getSeconds()].join("");
					}
				}
				created_at = timeStr;

				//pass it through zimlets to get url, phone, emoticons etc
				if (this._zimletDiv == undefined) {
					this._zimletDiv = document.createElement("div");
				}
				this._zimletDiv.innerHTML = AjxStringUtil.htmlEncode(text);
				this._objectManager.findObjectsInNode(this._zimletDiv);
				var zimletyFiedTxt = this._zimletDiv.innerHTML;
				if (type == "SOCIALCAST") {
					zimletyFiedTxt = this._socialcastReplaceHash(zimletyFiedTxt, socialTopicsbaseUrl);
					zimletyFiedTxt = this._socialcastReplaceAt(zimletyFiedTxt, socialcastUsersUrl);
				} else {
					zimletyFiedTxt = this._replaceHash(zimletyFiedTxt);
					zimletyFiedTxt = this._replaceAt(zimletyFiedTxt, userId, tableId, screen_name);
				}

				html[i++] = "<div id='" + rowId + "' class='social_rowsDiv'>";
				html[i++] = "<TABLE width=100%>";
				html[i++] = "<TR>";
				html[i++] = imageAnchor;
				if (markAsUnread) {
					html[i++] = "<td valign=top> <img title='" + this.getMessage("unreadItem") + "'  src='" + this.getResource("social_unreadMsgIcon.gif") + "' ></img></td>";
				}
				html[i++] = "<TD class='social_feedText' width=90%>";

				if (type != "TWEETMEME" && type != "FACEBOOK" && type != "SOCIALCAST") {
					html[i++] = [" <a href='javascript:void(0)' style='color:darkblue;font-size:12px;font-weight:bold' id='", this._getAccountLinkId(screen_name, tableId),
						"'>", screen_name, ":</a> "].join("");
				} else if (type == "SOCIALCAST") {
					//html[i++] = "<label style='color:#262626;font-size:12px;font-weight:bold'>" + user.name + ": </label>";
					html[i++] = [" <a href='javascript:void(0)' style='color:darkblue;font-size:12px;font-weight:bold' id='", this._getSocialcastAccountLinkId(user.url),
						"'>", user.name, ":</a> "].join("");
					if (recipients && recipients.length > 0) {
						html[i++] = "<label style='font-weight: bold;'> > </label>";
						var rHtml = [];
						for (var t = 0; t < recipients.length; t++) {
							var r = recipients[t];
							rHtml.push([" <a href='javascript:void(0)' style='color:darkblue;font-size:12px;font-weight:bold' id='", this._getSocialcastAccountLinkId(r.url),
								"'>", r.name, "</a> "].join(""));
						}
						html[i++] = rHtml.join(",");
					}
				} else {
					html[i++] = "<label style='color:#262626;font-size:12px;font-weight:bold'>" + screen_name + ": </label>";
				}
				html[i++] = zimletyFiedTxt;
				html[i++] = "<br/><label style='color:gray;font-size:11px'>&nbsp;" + created_at + "</label><br/>";

				html[i++] = "</TD>";
				html[i++] = "</TR>";

				if (type == "FACEBOOK" || type == "SOCIALCAST") {
					var additionalInfo = {};
					if (type == "FACEBOOK") {
						additionalInfo = this._getAdditionalFBMessageInfo(obj, this.tableIdAndAccountMap[tableId], tableId);
					} else if (type == "SOCIALCAST") {
						additionalInfo = this._getAdditionalSCMessageInfo(obj, this.tableIdAndAccountMap[tableId], tableId);
					}
					if (additionalInfo.html != "") {
						html[i++] = "<TR>";
						html[i++] = "<TD colspan=" + columnSpan + ">";
						html[i++] = additionalInfo.html;
						html[i++] = "</TD>";
						html[i++] = "</TR>";
					}
					html[i++] = "<TR>";
					html[i++] = "<TD align=center colspan=" + columnSpan + ">";
					html[i++] = "<DIV id='" + additionalInfo.commentBoxId + "' style='display:none' />";
					html[i++] = "</TD>";
					html[i++] = "</TR>";
				}

				html[i++] = "<TR>";
				html[i++] = "<TD colspan=" + columnSpan + " style='color:gray'>";
				html[i++] = "<table width=100%>";
				html[i++] = "<TR>";
				if (type == "TWEETMEME" || type == "DIGG") {
					html[i++] = "<TD  style='color:gray;font-size:11px;text-align:left'>";
				} else {
					html[i++] = "<TD style='color:gray;font-size:11px;text-align:left'>";
				}
				html[i++] = source.indexOf("via") != -1 ? source : this.getMessage("via") + " " + source;
				html[i++] = "</td>";
				html[i++] = "<td colspan=" + columnSpan + " align=right style='text-align:right'>";

				if (type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS") {
					if (accountName == screen_name) {
						html[i++] = "<a href='javascript:void(0)'  title='" + this.getMessage("deletePost") + "' style='color:gray;font-size:11px' id='" + this._gettwitterDeleteLinkId(obj.id, tableId, type) + "'>" + this.getMessage("del") + "</a>&nbsp;&nbsp;";
					}
				}

				if (type == "ACCOUNT" || type == "SEARCH" || type == "TREND" || type == "DIRECT_MSGS") {
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("directMessage") + "' style='color:gray;font-size:11px' id='" + this._gettwitterDMLinkId("d @" + screen_name) + "'>" + this.getMessage("dm") + "</a>&nbsp;&nbsp;";
				}
				if (type != "DIRECT_MSGS") {
					var linkName = this.getMessage("retweet");
					if (type == "FACEBOOK" || type == "SOCIALCAST") {
						linkName = this.getMessage("share");
					}
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("retweetThisItem") + "' style='color:gray;font-size:11px' id='" + this._gettwitterRetweetLinkId("RT @" + screen_name + text) + "'>" + linkName + "</a>&nbsp;&nbsp;";
				}

				if (type != "TWEETMEME" && type != "FACEBOOK" && type != "SOCIALCAST" && type != "SOCIALCAST" && type != "DIGG" && type != "DIRECT_MSGS") {
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("replyToThisPerson") + "' style='color:gray;font-size:11px' id='" + this._gettwitterReplyLinkId("@" + screen_name) + "'>" + this.getMessage("reply") + "</a>";
				}
				if (type == "FACEBOOK") {
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("likeThisPost") + "' style='color:gray;font-size:11px' id='" + this._getFBLikeLinkId(obj.post_id, tableId) + "'>" + this.getMessage("like") + "</a>&nbsp;&nbsp;";
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("commentOnThisPost") + "' style='color:gray;font-size:11px' id='" + this._getFacebookCommentLinkId(obj.post_id, tableId) + "'>" + this.getMessage("comment") + "</a>";
				}
				if (type == "SOCIALCAST") {
					if (socialcastUserName != obj.user.username) {
						html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("likeThisPost") + "' style='color:gray;font-size:11px' id='" + this._getSCLikeLinkId(obj.id, tableId) + "'>" + this.getMessage("like") + "</a>&nbsp;&nbsp;";
					}
					html[i++] = "<a href='javascript:void(0)' title='" + this.getMessage("commentOnThisPost") + "' style='color:gray;font-size:11px' id='" + this._getSCCommentLinkId(obj.id, tableId) + "'>" + this.getMessage("comment") + "</a>";
				}
				html[i++] = "</td>";
				html[i++] = "</TR>";
				html[i++] = "</TABLE>";
				html[i++] = "</TD>";
				html[i++] = "</TR>";
				html[i++] = "</TABLE>";
				html[i++] = "</DIV>";
			}

			this._addNewerAndOlderLinks(tableId, numberOfPosts, html, "bottom", type);

			if (!document.getElementById(tableId)) {
				return;
			}
			document.getElementById(tableId).innerHTML = html.join("");
			if (type == "SEARCH" || type == "ACCOUNT" || type == "MENTIONS" || type == "DIRECT_MSGS" || type == "SENT_MSGS" || type == "FACEBOOK" || type == "SOCIALCAST") {
				this._setCardUnreadCount(tableId, unReadCount, type);
				var query = "";
				if (additionalParams && additionalParams.query) {
					query = additionalParams.query;
				}
				this._addOlderAndNewerLinkHandlers(tableId, type, query);
			}
			this._addRetweetLinkHandlers();
			this._addReplyLinkHandlers();
			this._addDMLinkHandlers();
			this._addAccountLinkHandlers();
			this._addHashHandlers();
			if (type == "FACEBOOK") {
				this._addFbCommentLinkHandlers();
				this._addFbPostLikeLinkHandlers();
				this._addFbMoreCommentLinkHandlers();
			}
			if (type == "SOCIALCAST") {
				this._addSocialcastHashHandlers();
				this._addSCCommentLinkHandlers();
				this._addSCPostLikeLinkHandlers();
				this._addSCMoreCommentLinkHandlers();
				this._addSCAccountLinkHandlers();
			}

			this._addTwitterDeleteLinkHandlers();
		};

SocialZimlet.prototype._addNewerAndOlderLinks =
		function(tableId, numberOfPosts, html, position, type) {
			var i = html.length;
			var olderItemsLnkId = tableId + "_olderPostsLinkId_" + position;
			var newerItemsLnkId = tableId + "_newerPostsLinkId_" + position;
			var mainDivDisplayStyle = "";

			var newerLnkDisplayStyle = "none";
			if (this.tableIdAndPageNumberMap[tableId] > 1) {
				newerLnkDisplayStyle = "";
			}
			var olderLnkDisplayStyle = "";
			/*
			 var olderLnkDisplayStyle = "none";
			 var itemsLimit = this._getItemsLimit(type);
			 if(numberOfPosts == itemsLimit) {
			 olderLnkDisplayStyle = "";
			 }
			 */
			if (olderLnkDisplayStyle == "none" && newerLnkDisplayStyle == "none") {
				mainDivDisplayStyle = "none";
			}
			html[i++] = ["<div style='display:", mainDivDisplayStyle, "' class='social_rowsDiv' width=100%><table width=100%><tr><td align=left><a style='display:", newerLnkDisplayStyle,
				"' href='javascript:void(0)' id='", newerItemsLnkId, "' >< ", this.getMessage("newerItems"), "</a></td><td align=right><a  style='display:", olderLnkDisplayStyle,
				"' href='javascript:void(0)' id='", olderItemsLnkId, "' >", this.getMessage("olderItems"), " ></a></td></tr></table></div>"].join("");

		};

SocialZimlet.prototype._addRefreshNoticeDiv =
		function(tableId, html) {
			var i = html.length;
			var refreshNoticeId = tableId + "_refreshNotice";
			html[i++] = "<div class='social_yellow' id='" + refreshNoticeId + "' width=100% style='display:none;color:red;'></div>";
		};

SocialZimlet.prototype._isPageDirtyWithNewPosts =
		function(tableId, newId) {
			var isDirty = false;

			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			if (pageNumber > 1 && (newId < this.tableIdAndHighestPostIdMap[tableId])) {
				isDirty = false;
			} else if (pageNumber > 1 && (newId > this.tableIdAndHighestPostIdMap[tableId])) {
				document.getElementById(tableId + "_refreshNotice").innerHTML = this.getMessage("refreshToLoadNewTweets");
				document.getElementById(tableId + "_refreshNotice").style.display = "block";
				isDirty = true;
			} else if (newId == this.tableIdAndHighestPostIdMap[tableId]) {
				isDirty = false;
			}
			return isDirty;
		};

SocialZimlet.prototype._cacheResults =
		function(tableId, jsonObj) {
			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			if (!this.tableIdAndCacheMap[tableId]) {
				this.tableIdAndCacheMap[tableId] = [];
			}
			this.tableIdAndCacheMap[tableId][pageNumber] = jsonObj;
		};

SocialZimlet.prototype._storeUnreadCount =
		function(tableId, unreadCount) {
			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			if (!this.tableIdAndUnreadMap[tableId]) {
				this.tableIdAndUnreadMap[tableId] = [];
			}
			this.tableIdAndUnreadMap[tableId][pageNumber] = unreadCount;
		};

SocialZimlet.prototype._getTotalUnreadCount =
		function(tableId, unreadCount) {
			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			var pages = this.tableIdAndUnreadMap[tableId];
			if (!pages) {
				return unreadCount;
			}
			var count = 0;
			for (var i = 1; i < pageNumber; i++) {
				count = count + pages[i];
			}
			return count + unreadCount;
		};

SocialZimlet.prototype._showCardOlderMsgs =
		function(tableId, type, query) {
			this._setMsgToCard(tableId, this.getMessage("loading"));
			this.tableIdAndRefreshType[tableId] = "OLDER";
			this._updatePageNumber(tableId, 1);
			this._doRefreshFeeds(tableId, type, query);
		};

SocialZimlet.prototype._showCardNewerMsgs =
		function(tableId, type, query) {
			this._setMsgToCard(tableId, this.getMessage("loading"));
			this._updatePageNumber(tableId, -1);
			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			var table = this.tableIdAndCacheMap[tableId];
			var additionalParams = {query:query};
			if (table && table[pageNumber]) {
				this.createCardView({tableId:tableId, items:table[pageNumber], type:type, additionalParams:additionalParams});
			} else {//not cached, simply refresh
				this._doRefreshFeeds(tableId, type, query);
			}
		};

SocialZimlet.prototype._updatePageNumber =
		function(tableId, plusOrMinusOne) {
			var pageNumber = this.tableIdAndPageNumberMap[tableId];
			if (!pageNumber) {
				pageNumber = 1;
			}
			this.tableIdAndPageNumberMap[tableId] = pageNumber + plusOrMinusOne;
		};

SocialZimlet.prototype._getAdditionalSCMessageInfo =
		function(obj, account, tableId) {
			var html = [];
			var i = 0;
			var commentBoxId = "social_SCcommentBoxId_" + Dwt.getNextId();
			this._SCPostIdAndCommentboxMap[obj.id] = commentBoxId;
			var attachments = obj.attachments;
			var media_files = obj.media_files;
			html[i++] = "<ul style='list-style-type:none;list-style:none outside none;clear:both;pading:8px 0 0;overflow:hidden;'>";
			for (var j = 0; j < media_files.length; j++) {
				var m = media_files[j];
				html[i++] = "<li style='clear:left;display:block;'><a href='" + m.url + "' target='_blank'><img style='width:45px;height:45px' src='" + m.thumbnails.square45 + "'></img></a></li>";

			}
			html[i++] = "</ul>";
			html[i++] = "<ul style='list-style-type:none;list-style:none outside none;clear:both;pading:8px 0 0;overflow:hidden;'>";
			for (var j = 0; j < attachments.length; j++) {
				var a = attachments[j];
				html[i++] = "<li style='clear:left;display:block;'><a target='_blank' href='" + a.url + "'>" + a.filename + "</a></li>";

			}

			if (obj.likes.length > 0) {
				html[i++] = "<table width=100% cellpadding=1 cellspacing=1><tr><td align='center'>";
				html[i++] = "<tr>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "<TD>";
				html[i++] = "<DIV class='social_FBCommentRow'>";
				if (obj.likes.length == 1) {
					html[i++] = "<a  href='" + obj.likes.href + "' target='_blank'>" + this.getMessage("onePersonLikeThis") + "</a> ";
				} else {
					html[i++] = "<a  href='" + obj.likes.href + "' target='_blank'>" + obj.likes.length + " " + this.getMessage("peopleLikeThis") + "</a>";
				}
				html[i++] = "</div>";
				html[i++] = "</td>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "</tr></table>";
			}

			if (obj.comments.length > 0) {
				var comments = obj.comments;
				var commentsDivId = "social_commentsdiv_" + Dwt.getNextId();
				html[i++] = "<div id='" + commentsDivId + "'>";
				html[i++] = this._getCommentsHtml(comments, obj.comments_count, obj.id, commentsDivId, account, tableId);
				html[i++] = "</div>";
			}
			var str = html.join("");
			if (str == "")
				return  {html:"", commentBoxId:commentBoxId};
			else
				return {html:"<BR/>" + str, commentBoxId:commentBoxId};
		};

SocialZimlet.prototype._getAdditionalFBMessageInfo =
		function(obj, account, tableId) {

			var html = new Array();
			var i = 0;
			if (obj.description) {
				html[i++] = obj.description;
			}
			var commentBoxId = "social_fbcommentBoxId_" + Dwt.getNextId();
			this._FBPostIdAndCommentboxMap[obj.post_id] = commentBoxId;
			var attachment = obj.attachment;
			if (attachment && attachment.media && !(attachment.media instanceof Array) && !attachment.media.src) {
				html[i++] = "<table width=100%><tr><td>";
				if (attachment.href && attachment.name && attachment.href != "") {
					html[i++] = "<a  href='" + attachment.href + "' target='_blank'>";
					html[i++] = attachment.name;
					html[i++] = "</a>";
					html[i++] = "<br/>";
				}
				if (attachment.description) {
					html[i++] = "<div style='font-size:11px' class='social_feedText'>";
					html[i++] = attachment.description;
					html[i++] = "</div>";
				}
				html[i++] = "</td></tr></table>";
			} else if (attachment && attachment.media != undefined && attachment.media.length > 0) {
				var medias = attachment.media;
				var counter = 0;
				var maxItems = 2;
				var cardW = parseInt(this.preferences.social_pref_cardWidthList.replace("px", ""));
				if (cardW <= 350) {
					maxItems = 1;
				}
				html[i++] = "<table width=100%>";
				html[i++] = "<TR>";
				for (var j = 0; j < medias.length; j++) {
					var media = medias[j];
					var videoEmbedded = false;
					var isYouTube = false;
					if (!media.video) {
						media.src = media.src && typeof(media.src) == "string" ? (media.src.indexOf("/") == 0 ? "https://www.facebook.com" + media.src : media.src) : "";
					} else if (media.video && media.video.source_url) {
						media.src = media.video.source_url;
						if (typeof(media.src) != "string") {
							media.src = "";
						}
						if (media.src.indexOf("youtube") > 0) {
							media.src = media.src.replace("autoplay=1", "autoplay=0");
							media.src = media.src + "&fs=1";//add fullscreen button
							isYouTube = true;
						}
						videoEmbedded = true;
					} else if (media.video && media.video.preview_img) {
						media.src = media.video.preview_img;
						videoEmbedded = true;
					} else {
						media.src = "";
					}
					if (videoEmbedded) {
						html[i++] = "<TD  valign='top'>";
					} else {
						html[i++] = "<TD width='100px' valign='top'>";
					}
					html[i++] = "<table cellspacing='0' cellpadding='2' width=100%>";
					html[i++] = "<TR>";
					html[i++] = "<TD  valign='top'>";
					if (media.src != "" && media.href) {
						var id = Dwt.getNextId();
						var arry = media.href.split("?v=");
						var vid = "";
						if (arry.length == 2) {
							vid = arry[1];
						}
						if (isYouTube) {
							html[i++] = "<object type='application/x-shockwave-flash' style='width:100%; height:350px;' data='" + media.src + "'><param name='movie' value='" + media.src + "' /></object>";
						} else {
							if (media.type == "link") {
								html[i++] = "<a  href='" + media.href + "' target='_blank' >Open external link</a>";
							} else if (media.type == "video" && vid != "") {
								html[i++] = "<object width='100%' height='224' >";
								html[i++] = "<param name='allowfullscreen' value='true' />";
								html[i++] = "<param name='allowscriptaccess' value='always' />";
								html[i++] = "<param name='movie' value='http://www.facebook.com/v/" + vid + "' />";
								html[i++] = "<embed src='http://www.facebook.com/v/" + vid + "' type='application/x-shockwave-flash'";
								html[i++] = "allowscriptaccess='always' allowfullscreen='true' width='100%' height='224'>";
								html[i++] = "</embed>";
								html[i++] = "</object>";
							} else if (media.type == "video" && vid == "") {
								html[i++] = "<a  href='" + media.href + "' target='_blank' >" + this.getMessage("openExternalLink") + "</a>";
							} else {
								html[i++] = "<div class='social_shadow'><a  style='color:white' href='" + media.href + "' target='_blank' >" + "<img width='100px' height='100px' SRC='" + media.src + "' ></img></a></div>";
							}

						}
					}
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
						html[i++] = "<a  href='" + attachment.href + "' target='_blank'>";
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
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "<TD>";
				html[i++] = "<DIV class='social_FBCommentRow'>";
				if (obj.likes.count == 1) {
					html[i++] = "<a  href='" + obj.likes.href + "' target='_blank'>" + this.getMessage("onePersonLikeThis") + "</a> ";
				} else {
					html[i++] = "<a  href='" + obj.likes.href + "' target='_blank'>" + obj.likes.count + " " + this.getMessage("peopleLikeThis") + "</a>";
				}
				html[i++] = "</div>";
				html[i++] = "</td>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "</tr></table>";
			}

			if (obj.comments && obj.comments.comment_list != undefined) {
				var comments = obj.comments.comment_list;
				var commentsDivId = "social_commentsdiv_" + Dwt.getNextId();
				html[i++] = "<div id='" + commentsDivId + "'>";
				html[i++] = this._getCommentsHtml(comments, obj.comments.count, obj.post_id, commentsDivId, account, tableId);
				html[i++] = "</div>";
			}

			var str = html.join("");
			if (str == "")
				return  {html:"", commentBoxId:commentBoxId};
			else
				return {html:"<BR/>" + str, commentBoxId:commentBoxId};
		};

SocialZimlet.prototype._setErrorToCard =
		function(tableId, error) {
			var id = Dwt.getNextId();
			var html = [];
			html.push("<br/><br/><div width=90% align=center><label style='color:gray;font-weight:bold;font-size:12px'>", error, " </label>",
					"<a id='", id, "' href='javascript:void(0)' style='text-decoration:underline;color:gray' >",
					this.getMessage("details"), "</a><br/><br/><label style='color:gray;font-style:italic'>", this.getMessage("clickOnRefreshToTryAgain"), "</label></div>");

			if (document.getElementById(tableId) && error) {
				document.getElementById(tableId).innerHTML = html.join("");
				document.getElementById(id).onclick = AjxCallback.simpleClosure(this._displayFeedErrorWindow, this, tableId);
			}
		};

SocialZimlet.prototype._setMsgToCard =
		function(tableId, msg) {
			var html = [];
			html.push("<br/><br/><div width=90% align=center><label style='color:gray;font-weight:bold;font-size:12px'>", msg, "</label></div>");
			document.getElementById(tableId).innerHTML = html.join("");
		};

SocialZimlet.prototype._displayFeedErrorWindow =
		function(tableId) {
			var win = this.openCenteredWindow("");
			if (this.tableIdAndHttpErrorMap[tableId]) {
				win.document.write("<div style='color:blue;font-size:16px;font-weight:bold;background:#FFFF99'>" + this.getMessage("httpErrorStatement") + "</div>");
				win.document.write(this.tableIdAndHttpErrorMap[tableId]);
			}
		};

SocialZimlet.prototype._getCommentsHtml =
		function(comments, totlCmnts, postId, divId, account, tableId) {
			var html = new Array();
			var i = 0;
			var socialcastUsersUrl = ["https://", account.s, "/users/"].join("");
			var socialTopicsbaseUrl = ["https://", account.s, "/topics/"].join("");
			if (this._zimletDiv == undefined) {
				this._zimletDiv = document.createElement("div");
			}
			var actualComments = 0;
			var photoUrl = "";
			var photoSrc = "";
			var userName = "";
			var accountType = account.type;
			var maxSCComments = 2;
			for (var j = 0; j < totlCmnts; j++) {
				actualComments = j + 1;

				var comment = comments[j];
				if (!comment) {
					break;
				}
				if (accountType == "facebook") {
					var profile = this.facebook.getFacebookProfile(comment.fromid, tableId);
					if (!profile) {
						continue;
					}
					photoUrl = profile.url ? profile.url : profile.profile_url;
					photoSrc = profile.pic_square;
					userName = profile.name;
				} else if (accountType == "SOCIALCAST") {
					photoUrl = comment.user.url;
					photoSrc = comment.user.avatars.square30;
					userName = comment.user.name;
				}
				var commentRowId = Dwt.getNextId();
				if (j >= maxSCComments && accountType == "SOCIALCAST") {
					html[i++] = "<div style='display:none;' id='" + commentRowId + "'>";
					if (!this._scCommentDivAndHiddenDivsMap[divId]) {
						this._scCommentDivAndHiddenDivsMap[divId] = [];
					}
					this._scCommentDivAndHiddenDivsMap[divId].push(commentRowId);
				} else {
					html[i++] = "<div>";
				}
				html[i++] = "<table width=100% cellpadding=1 cellspacing=1>";
				html[i++] = "<tr>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "<TD>";
				html[i++] = "<DIV class='social_FBCommentRow'>";
				html[i++] = "<table width=100%>";
				html[i++] = "<TR>";
				html[i++] = "<TD width=32px valign='top'>";
				html[i++] = "<div width=32px height=32px  align='center' class='social_accountBg'> ";
				html[i++] = "<a  href='" + photoUrl + "' target='_blank' style='color: white;'>";
				html[i++] = "<img height='32' width='32' src='" + photoSrc + "' />";
				html[i++] = "</a>";
				html[i++] = "</div>";
				html[i++] = "</TD><TD class='social_fbcommentText'>";
				//pass it through zimlets to get url, phone, emoticons etc
				this._zimletDiv.innerHTML = AjxStringUtil.htmlEncode(comment.text);
				this._objectManager.findObjectsInNode(this._zimletDiv);
				var zimletyFiedTxt = " " + this._zimletDiv.innerHTML;
				if (accountType == "SOCIALCAST") {
					zimletyFiedTxt = this._socialcastReplaceHash(zimletyFiedTxt, socialTopicsbaseUrl);
					zimletyFiedTxt = this._socialcastReplaceAt(zimletyFiedTxt, socialcastUsersUrl);
				} else {
					zimletyFiedTxt = this._replaceHash(zimletyFiedTxt);
				}
				html[i++] = zimletyFiedTxt + "<br><label  style='color:gray;font-size:11px'> - " + userName + "</label>";
				html[i++] = "</TD></TR>";
				html[i++] = "</TABLE>";
				html[i++] = "</DIV>";
				html[i++] = "</TD>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "</TR>";
				html[i++] = "</table>";
				html[i++] = "</div>";
			}
			if (actualComments < totlCmnts || (accountType == "SOCIALCAST" && actualComments > maxSCComments)) {
				var seeAllComments = AjxMessageFormat.format(this.getMessage("seeAllComments"), totlCmnts);
				var moreCommentsLinkId = "";
				var moreLinkRowDiv = Dwt.getNextId();
				if (accountType == "facebook") {
					moreCommentsLinkId = this._getFacebookMoreCommentsLinkId(postId, divId, account, tableId);
				} else if (accountType == "SOCIALCAST") {
					moreCommentsLinkId = this._getSocialcastMoreCommentsLinkId(postId, divId, account, tableId, moreLinkRowDiv);
				}
				html[i++] = "<table width=100% cellpadding=1 cellspacing=1><tr><td>";
				html[i++] = "<tr>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "<TD>";
				html[i++] = ["<DIV id='",moreLinkRowDiv,"' class='social_FBCommentRow'>"].join("");
				html[i++] = ["<a  href='javascript:void(0)' id='", moreCommentsLinkId, "'>",seeAllComments,"</a>"].join("");
				html[i++] = "</div>";
				html[i++] = "</td>";
				html[i++] = "<TD style='width:16px;height:16px' align='center'>";
				html[i++] = AjxImg.getImageHtml("Blank_16");
				html[i++] = "</TD>";
				html[i++] = "</tr></table>";
			}
			return html.join("");

		};

SocialZimlet.prototype._gettwitterRetweetLinkId =
		function(rt) {
			var id = "social_retweetLink_" + Dwt.getNextId();
			this._allReweetLinks[id] = {hasHandler:false, rt:rt};
			return id;
		};

SocialZimlet.prototype._gettwitterDMLinkId =
		function(dm) {
			var id = "social_DMLink_" + Dwt.getNextId();
			this._allDMLinks[id] = {hasHandler:false, dm:dm};
			return id;
		};

SocialZimlet.prototype._gettwitterDeleteLinkId =
		function(postId, tableId, type) {
			var id = "social_twitterDeleteLink_" + Dwt.getNextId();
			this._allTwitterDeleteLinks[id] = {hasHandler:false, account:this.tableIdAndAccountMap[tableId], tableId:tableId, postId:postId, type:type};
			return id;
		};

SocialZimlet.prototype._getAccountLinkId =
		function(screen_name, tableId) {
			var id = "social_accountsLink_" + Dwt.getNextId();
			this._allAccountsLinks[id] = {hasHandler:false, tableId:tableId, screen_name:screen_name};
			return id;
		};

SocialZimlet.prototype._getSocialcastAccountLinkId =
		function(url) {
			var id = "social_SCaccountsLink_" + Dwt.getNextId();
			this._allSocialcastAccountsLinks[id] = {hasHandler:false, url:url};
			return id;

		}
SocialZimlet.prototype._getFacebookCommentLinkId =
		function(postId, tableId) {
			var id = "social_FaceBookCommentLink_" + Dwt.getNextId();
			this._allFacebookCommentsLinks[id] = {hasHandler:false, tableId:tableId, postId:postId};
			return id;
		};
SocialZimlet.prototype._getSCCommentLinkId =
		function(postId, tableId) {
			var id = "social_FBCommenttLink_" + Dwt.getNextId();
			this._allSCCommentsLinks[id] = {hasHandler:false, tableId:tableId, postId:postId};
			return id;
		};

SocialZimlet.prototype._getFBLikeLinkId =
		function(postId, tableId) {
			var id = "social_FBLikeLink_" + Dwt.getNextId();
			this._allFBLikeLinks[id] = {hasHandler:false, tableId:tableId, account:this.tableIdAndAccountMap[tableId], postId:postId};
			return id;
		};
SocialZimlet.prototype._getSCLikeLinkId =
		function(postId, tableId) {
			var id = "social_SCLikeLink_" + Dwt.getNextId();
			this._allSCLikeLinks[id] = {hasHandler:false, tableId:tableId, account:this.tableIdAndAccountMap[tableId], postId:postId};
			return id;
		};

SocialZimlet.prototype._getFacebookMoreCommentsLinkId =
		function(postId, divId, account, tableId) {
			var id = "social_FBMoreCommentsLink_" + Dwt.getNextId();
			this._allFBMoreCommentsLinks[id] = {hasHandler:false, postId:postId, divId:divId, account:account, tableId:tableId};
			return id;
		};

SocialZimlet.prototype._getSocialcastMoreCommentsLinkId =
		function(postId, divId, account, tableId, moreLinkDivId) {
			var id = "social_SCMoreCommentsLink_" + Dwt.getNextId();
			this._allSCMoreCommentsLinks[id] = {hasHandler:false, postId:postId, divId:divId, account:account, tableId:tableId, moreLinkDivId:moreLinkDivId};
			return id;
		};

SocialZimlet.prototype._gettwitterReplyLinkId =
		function(reply) {
			var id = "social_replyLink_" + Dwt.getNextId();
			this._allReplyLinks[id] = {hasHandler:false, reply:reply};
			return id;
		};

SocialZimlet.prototype._gettwitterFollowLinkId =
		function(userId, tableId) {
			var id = "social_followLink_" + Dwt.getNextId();
			this._allFollowLinks[id] = {hasHandler:false, userId:userId, tableId:tableId};
			return id;
		};

SocialZimlet.prototype._addOlderAndNewerLinkHandlers =
		function(tableId, type, query) {
			var topOlderLink = document.getElementById(tableId + "_olderPostsLinkId_top");
			if (topOlderLink) {
				topOlderLink.onclick = AjxCallback.simpleClosure(this._showCardOlderMsgs, this, tableId, type, query);
			}
			var topNewerLink = document.getElementById(tableId + "_newerPostsLinkId_top");
			if (topNewerLink) {
				topNewerLink.onclick = AjxCallback.simpleClosure(this._showCardNewerMsgs, this, tableId, type, query);
			}

			//when there are no msgs, we will just show one set of links(top-set)
			var bottomOlderLink = document.getElementById(tableId + "_olderPostsLinkId_bottom");
			if (bottomOlderLink) {
				bottomOlderLink.onclick = AjxCallback.simpleClosure(this._showCardOlderMsgs, this, tableId, type, query);
			}
			var bottomNewerLink = document.getElementById(tableId + "_newerPostsLinkId_bottom");
			if (bottomNewerLink) {
				bottomNewerLink.onclick = AjxCallback.simpleClosure(this._showCardNewerMsgs, this, tableId, type, query);
			}
		};


SocialZimlet.prototype._addRetweetLinkHandlers =
		function() {
			for (var id in this._allReweetLinks) {
				var obj = this._allReweetLinks[id];
				var el = document.getElementById(id);
				if (el && !obj.hasHandler) {
					el.onclick = AjxCallback.simpleClosure(this.addRetweetText, this, obj.rt);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addDMLinkHandlers =
		function() {
			for (var id in this._allDMLinks) {
				var obj = this._allDMLinks[id];
				var el = document.getElementById(id);
				if (el && !obj.hasHandler) {
					el.onclick = AjxCallback.simpleClosure(this.addDMText, this, obj.dm);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addTwitterDeleteLinkHandlers =
		function() {
			for (var id in this._allTwitterDeleteLinks) {
				var obj = this._allTwitterDeleteLinks[id];
				var el = document.getElementById(id);
				if (el && !obj.hasHandler) {
					el.onclick = AjxCallback.simpleClosure(this.twitter.deletePost, this.twitter, obj);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addFbCommentLinkHandlers =
		function() {
			for (var id in this._allFacebookCommentsLinks) {
				var obj = this._allFacebookCommentsLinks[id];
				var el = document.getElementById(id);
				if (el && !obj.hasHandler) {
					el.onclick = AjxCallback.simpleClosure(this._displayCommentWidget, this, {type:"FACEBOOK", postId:obj.postId, tableId:obj.tableId, linkId:id});
					obj.hasHandler = true;
				}
			}
		};
SocialZimlet.prototype._addSCCommentLinkHandlers =
		function() {
			for (var id in this._allSCCommentsLinks) {
				var obj = this._allSCCommentsLinks[id];
				var el = document.getElementById(id);
				if (el && !obj.hasHandler) {
					el.onclick = AjxCallback.simpleClosure(this._displayCommentWidget, this, {type :"SOCIALCAST",postId:obj.postId, tableId:obj.tableId, linkId:id});
					obj.hasHandler = true;
				}
			}
		};


SocialZimlet.prototype._addFbPostLikeLinkHandlers =
		function() {
			for (var id in this._allFBLikeLinks) {
				var obj = this._allFBLikeLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.facebook.postLike, this.facebook, obj);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addSCPostLikeLinkHandlers =
		function() {
			for (var id in this._allSCLikeLinks) {
				var obj = this._allSCLikeLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.socialcast.postLike, this.socialcast, obj);
					obj.hasHandler = true;
				}
			}
		};


SocialZimlet.prototype._addFbMoreCommentLinkHandlers =
		function() {
			for (var id in this._allFBMoreCommentsLinks) {
				var obj = this._allFBMoreCommentsLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.facebook.insertMoreComments, this.facebook, obj);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addSCMoreCommentLinkHandlers =
		function() {
			for (var id in this._allSCMoreCommentsLinks) {
				var obj = this._allSCMoreCommentsLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.socialcast._insertMoreComments, this.socialcast, obj);
					obj.hasHandler = true;
				}
			}
		};


SocialZimlet.prototype._addReplyLinkHandlers =
		function() {
			for (var id in this._allReplyLinks) {
				var obj = this._allReplyLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.addReplyText, this, obj.reply);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addAccountLinkHandlers =
		function() {
			for (var id in this._allAccountsLinks) {
				var obj = this._allAccountsLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.twitter.showUserProfile, this.twitter, obj.screen_name, obj.tableId);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._addSCAccountLinkHandlers =
		function() {
			for (var id in this._allSocialcastAccountsLinks) {
				var obj = this._allSocialcastAccountsLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.openCenteredWindow, this, obj.url);
					obj.hasHandler = true;
				}
			}
		};


SocialZimlet.prototype._addHashHandlers =
		function() {
			for (var id in this._allHashLinks) {
				var obj = this._allHashLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this._handleHashLinks, this, obj.word);
					obj.hasHandler = true;
				}
			}
		};
SocialZimlet.prototype._addSocialcastHashHandlers =
		function() {
			for (var id in this._allSocialcastHashLinks) {
				var obj = this._allSocialcastHashLinks[id];
				var el = document.getElementById(id);
				if (!obj.hasHandler && el) {
					el.onclick = AjxCallback.simpleClosure(this.openCenteredWindow, this, obj.url);
					obj.hasHandler = true;
				}
			}
		};

SocialZimlet.prototype._handleHashLinks =
		function(query) {
			document.getElementById("social_searchField").value = query;
			this._twitterSearchBtnListener();
		};

SocialZimlet.prototype._replaceHash =
		function(text) {
			var re = /([^a-zA-Z0-9_-]#[a-zA-Z0-9_-]+)/gm;
			var newStr = "";
			var start = 0;
			while (match = re.exec(text)) {
				var word = match[0];
				var end = re.lastIndex;
				var part = text.substring(start, end);
				var id = "social_hashlink_" + Dwt.getNextId();
				var url = ["<a  href='javascript:void(0)' id='", id, "'>", word, "</a>"].join("");
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
SocialZimlet.prototype._socialcastReplaceHash =
		function(text, socialTopicsbaseUrl) {
			var re = /([^a-zA-Z0-9_-]#[a-zA-Z0-9_-]+)/gm;
			var newStr = "";
			var start = 0;
			while (match = re.exec(text)) {
				var word = match[0];
				var end = re.lastIndex;
				var part = text.substring(start, end);
				var id = "social_hashlink_" + Dwt.getNextId();
				var url = ["<a  href='javascript:void(0)' id='", id, "'>", word, "</a>"].join("");
				newStr = newStr + part.replace(word, url);
				this._allSocialcastHashLinks[id] = {hasHandler:false, url: socialTopicsbaseUrl + (AjxStringUtil.trim(word.replace("#", "")))};
				start = end;
			}
			var extraStr = "";
			if (start < text.length) {
				extraStr = text.substring(start, text.length)
			}
			return newStr + extraStr;

		};
/topics/

SocialZimlet.prototype._replaceAt =
		function(text, userId, tableId, screen_name) {
			var re = /([^a-zA-Z0-9_-]@[a-zA-Z0-9_-]+)/gm;
			var newStr = "";
			var start = 0;
			while (match = re.exec(text)) {
				var word = match[0];
				var end = re.lastIndex;
				var part = text.substring(start, end);
				var id = this._getAccountLinkId(AjxStringUtil.trim(word.replace("@", "")), tableId);
				var url = ["<a  href='javascript:void(0)' id='", id, "'>", word, "</a>"].join("");
				newStr = newStr + part.replace(word, url);
				start = end;
			}
			var extraStr = "";
			if (start < text.length) {
				extraStr = text.substring(start, text.length)
			}
			return newStr + extraStr;
		};

SocialZimlet.prototype._socialcastReplaceAt =
		function(text, baseUsersUrl) {
			var re = /([^a-zA-Z0-9_-]@[a-zA-Z0-9_-]+)/gm;
			var newStr = "";
			var start = 0;
			while (match = re.exec(text)) {
				var word = match[0];
				var end = re.lastIndex;
				var part = text.substring(start, end);
				var id = this._getSocialcastAccountLinkId(baseUsersUrl + AjxStringUtil.trim(word.replace("@", "")), tableId);
				var url = ["<a  href='javascript:void(0)' id='", id, "'>", word, "</a>"].join("");
				newStr = newStr + part.replace(word, url);
				start = end;
			}
			var extraStr = "";
			if (start < text.length) {
				extraStr = text.substring(start, text.length)
			}
			return newStr + extraStr;
		};


SocialZimlet.prototype.addRetweetText = function(rt) {
	this.isTextPasted = true;
	this.addReplyText(rt);
	this.isTextPasted = false;
};
SocialZimlet.prototype.addDMText = function(dm) {
	this.addReplyText(dm);
};

SocialZimlet.prototype._displayCommentWidget = function(params) {

	var html = new Array();
	var i = 0;
	var commentBtnId = "social_commentbtn_" + Dwt.getNextId();
	var commentFieldId = "social_commentField_" + Dwt.getNextId();
	var commentBoxId;
	if (params.type == "FACEBOOK") {
		commentBoxId = this._FBPostIdAndCommentboxMap[params.postId];
	} else if (params.type == "SOCIALCAST") {
		commentBoxId = this._SCPostIdAndCommentboxMap[params.postId];
	}
	html[i++] = "<Textarea column=40 rows='5' cols='20' id='" + commentFieldId + "' style='width: 90%;height:30px;border:1px solid #BDC7D8;color:gray'>" + this.getMessage("writeAComment") + "</TextArea><BR/>";
	html[i++] = "<div  align=center id='" + commentBtnId + "' />";

	var div = document.getElementById(commentBoxId);
	div.style.display = "block";
	div.innerHTML = html.join("");
	clearInterval(this.tableIdAndTimerMap[params.tableId]);//clear timer to make sure we dont clear comments
	params["commentBoxId"] = commentBoxId;
	params["commentFieldId"] = commentFieldId;
	params["commentBtnId"] = commentBtnId;
	this._addCommentsHandlers(params);
};

SocialZimlet.prototype._addCommentsHandlers = function(params) {
	var btn = new DwtButton({parent:this.getShell()});
	btn.setText(this.getMessage("comment"));
	if (params.type == "FACEBOOK") {
		btn.setImage("social_facebookIcon");
		btn.addSelectionListener(new AjxListener(this.facebook, this.facebook._addFBComment, params));
	} else if (params.type == "SOCIALCAST") {
		btn.setImage("social_socialcastIcon");
		btn.addSelectionListener(new AjxListener(this.socialcast, this.socialcast._addScComment, params));
	}
	document.getElementById(params.commentBtnId).appendChild(btn.getHtmlElement());
	var field = document.getElementById(params.commentFieldId);
	params["field"] = field;
	field.onfocus = AjxCallback.simpleClosure(this._handleAddCommentField, this, params);
	field.onblur = AjxCallback.simpleClosure(this._handleAddCommentField, this, params);
};

SocialZimlet.prototype._handleAddCommentField = function(params, ev) {
	var field = params.field;
	var message = this.getMessage("writeAComment");
	this._handleFieldFocusBlur(field, message, ev);

	var event = ev || window.event;
	if (event && event.type == "blur" && field.value == "" || field.value == this.getMessage("writeAComment")) {
		document.getElementById(params.commentBoxId).style.display = "none";
	}
};

SocialZimlet.prototype._handleFieldFocusBlur = function(field, message, ev) {
	var event = ev || window.event;
	if (field.value == message) {
		field.value = "";
		field.style.color = "black";
	}
	if (event && event.type == "blur" && field.value == "" || field.value == message) {
		field.value = message;
		field.style.color = "gray";
	}
};


SocialZimlet.prototype.addReplyText = function(val) {
	var statusField = this.updateField;
	statusField.value = val + " ";//allow a space
	statusField.focus();
	this.showNumberOfLetters();
	this.setFieldFocused(statusField);
};

SocialZimlet.prototype.setFieldFocused = function(field) {
	field.focus();
	field.style.color = "black";
};

SocialZimlet.prototype.getAllAccountsAsString = function() {
	var str = "";
	for (var accntId in this.allAccounts) {
		var accnt = this.allAccounts[accntId];
		var val = "";
		for (var name in accnt) {
			if (name == "checkboxId" || name == "raw" || name == "isDisplayed" || name == "offline_access" || name == "publish_stream" || name == "read_stream") {
				continue;
			}

			if ((!accnt[name] && accnt[name] != 0) || accnt[name] == "undefined") {
				accnt[name] = "";
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

SocialZimlet.prototype.loadAllAccountsFromDB = function() {
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

SocialZimlet.prototype._updateAllWidgetItems =
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
				this.systemFolders.push({name:this.getMessage("addRemoveAccounts"), icon:"Group", account:"", type:"MANAGE_ACCOUNTS"});
				//this.systemFolders.push({name:"Zimlet Preferences", icon:"Preferences", account:"", type:"PREFERENCES"});
			}
			if (params.updateSearchTree || params.updateSystemTree || params.updateTrendsTree || params.updateTweetMemeTree || params.updateDiggTree) {
				this._createTreeView();
			}
			if (params.updateAccntCheckboxes) {
				document.getElementById("social_updateToCell").innerHTML = this._addUpdateToCheckboxes();
				//this._setUpdateMenu();
				this._addSocialcastGroupsMenuHndler();
				this._addAccountCheckBoxListeners();
				this._updateMaxAllowedCharsToUpdate();
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
						this.tableIdAndAccountMap[tableId] = account;
						this.tableIdAndMarkAsReadId[tableId] = account._p;
						this.twitter.getTwitterFeeds({tableId: tableId, account: account, type:"ACCOUNT"});
						var timer = setInterval(AjxCallback.simpleClosure(this.twitter.getTwitterFeeds, this.twitter, {tableId: tableId, account: account, type:"ACCOUNT"}), 400000);
						this.tableIdAndTimerMap[tableId] = timer;

						account.isDisplayed = true;
						account["__s"] = "1";//__s means shown, 1 means true
						//this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
					} else if (account.type == "facebook") {
						if (account.__s == "0") {//if already displayed OR should-not display(__s == "0")
							continue;
						}
						var tableId = this._showCard({headerName:account.name, type:"FACEBOOK", autoScroll:false});
						this.tableIdAndAccountMap[tableId] = account;
						this.tableIdAndMarkAsReadId[tableId] = account._p;
						this.facebook._fbGetStream(tableId, account);
						var timer = setInterval(AjxCallback.simpleClosure(this.facebook._updateFacebookStream, this.facebook, tableId, account), 400000);
						this.tableIdAndTimerMap[tableId] = timer;
						account.isDisplayed = true;
						account["__s"] = "1";//__s means shown, 1 means true
						//this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString(), true);
					} else {
						var search = account;
						if (search.axn == "on") {
							var label = search.name;
							var tableId = this._showCard({headerName:label, type:"SEARCH", autoScroll:false});
							this.tableIdAndSearchMap[tableId] = search;
							this.tableIdAndMarkAsReadId[tableId] = search._p;
							var sParams = {query:label, tableId:tableId, type:"SEARCH"};
							this.twitter.twitterSearch(sParams);
							var timer = setInterval(AjxCallback.simpleClosure(this.twitter.twitterSearch, this.twitter, sParams), 400000);
							this.tableIdAndTimerMap[tableId] = timer;
						}
					}

				}
				this._displaySocialcastCards();
				this._saveAllCardsAndPositions();
				this.setUserProperty("social_AllTwitterAccounts", this.getAllAccountsAsString());
				//this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts));
				this.saveUserProperties();
			}
		};

SocialZimlet.prototype._displaySocialcastCards =
		function() {
			var postCallback = new AjxCallback(this, this._displayAllSCCards);
			for (var i = 0; i < this.socialcastAccounts.length; i++) {
				var account = this.socialcastAccounts[i];
				this.socialcast.authenticate(account, postCallback);
			}
		};

SocialZimlet.prototype._displayAllSCCards =
		function(account) {
			if (!account.isValid) {
				this._showWarningMsg(this.getMessage("couldNotAuthenticateSC"));
				return;
			}
			if (account.__s && account.__s != "0") {
				this._displaySocialcastCard(account);
			}
			var un = account.un;
			for (var j = 0; j < this.socialcastDisplayedStreams.length; j++) {
				var sObj = this.socialcastDisplayedStreams[j];
				if (un == sObj.un && sObj.__s == "1") {
					this._displaySocialcastCard(account, sObj.id, sObj.n);
				}
			}
			this.setUserProperty("socialcastAccounts", JSON.stringify(this.socialcastAccounts));
			this.saveUserProperties();
		};

SocialZimlet.prototype._sortAndMergeAccountsAndSearches =
		function() {
			var simpleArry = [];
			for (var id in this.allAccounts) {
				var item = this.allAccounts[id];
				if (item.__pos && (item.__pos != "" || item.__pos != "undefined")) {
					simpleArry.push(item);
				}
			}
			for (var id in this.twitter.allSearches) {
				var item = this.twitter.allSearches[id];
				if (item.axn == "on" || item.__pos && (item.__pos != "" || item.__pos != "undefined")) {
					simpleArry.push(item);
				}
			}
			simpleArry = simpleArry.sort(social_sortAccounts);
			return simpleArry;
		};

function social_sortAccounts(a, b) {
	var x = parseInt(a.__pos);
	var y = parseInt(b.__pos);
	return ((x < y) ? 1 : ((x > y) ? -1 : 0));
}

SocialZimlet.prototype._extractJSONResponse =
		function(tableId, errorName, response) {
			var text = response.text;
			var errorCode = response.status;
			var success = true;
			var jsonObj;
			if (response.status != 200 && response.success == false) {
				success = false;
			} else {
				try {
					jsonObj = JSON.parse(text);
				} catch(e) {
				}
				if (jsonObj) {
					if ((jsonObj.error && jsonObj.error != "") || (jsonObj.status && jsonObj.status != "success")
							|| (jsonObj.error_code && jsonObj.error_code != "")) {
						if (jsonObj.error_code) {
							errorCode = jsonObj.error_code;
						}
						success = false;
					}
				} else {
					success = false;
				}
			}

			if (!success) {
				text = ["{\"error\":\"",this.getMessage("couldNotLoad"),"\"}"].join("");
				jsonObj = eval("(" + text + ")");
				if (tableId) {
					this.tableIdAndHttpErrorMap[tableId] = response.text;
					var timer = this.tableIdAndTimerMap[tableId];
					if (timer) { //remove update timers
						clearInterval(timer);
					}
				}
			}
			return jsonObj;
		};

SocialZimlet.prototype._showWarningMsg = function(message) {
	var style = DwtMessageDialog.WARNING_STYLE;
	var dialog = appCtxt.getMsgDialog();
	this.warningDialog = dialog;
	dialog.setMessage(message, style);
	dialog.popup();
};

SocialZimlet.prototype.openCenteredWindow =
		function (url) {
			var width = 800;
			var height = 600;
			var left = parseInt((screen.availWidth / 2) - (width / 2));
			var top = parseInt((screen.availHeight / 2) - (height / 2));
			var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,scrollbars,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
			var win = window.open(url, "subWind", windowFeatures);
			if (!win) {
				this._showWarningMsg(ZmMsg.popupBlocker);
			}
			return win;
		};