/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

/**
 * Constructor.
 */
function com_zimbra_discover() {
}

com_zimbra_discover.prototype = new ZmZimletBase();
com_zimbra_discover.prototype.constructor = com_zimbra_discover;


com_zimbra_discover.discover = "DISCOVER";


com_zimbra_discover.prototype.init =
function() {
	this.discZimletON = this.getUserProperty("turnONDiscoverZimletNew") == "true";
	this.selectionsList = ["humor","art","business","entertainment","politics","technology","sports"];
	this.feeds_entertainment = this.feeds_business = this.feeds_art = this.feeds_humor = "";
	this.feeds_politics = this.feeds_sports = this.feeds_technology = "";
	if (!this.discZimletON) {
		return;
	}
	//this._masterFeed = "http://rajaraodv.feedpublish.com/rss.xml";
	//this._getMasterFeed();
	this._loadFeeds();
	this.initToolbarButton();
};
com_zimbra_discover.prototype._loadFeeds =
function() {
	//feedpublish is completely flacky, so hardcode.
	this.feeds_humor = "http://feeds.delicious.com/v2/json/tag/funny+cool+humor+photo?count=200::http://feeds.delicious.com/v2/json/tag/funny+cool+prank?count=25::http://feeds.delicious.com/v2/json/tag/funny+cool+humor?count=25::http://feeds.delicious.com/v2/json/tag/funny+jokes?count=25::http://feeds.delicious.com/v2/json/tag/funny+cool+humor+awesome?count=30::http://rss.stumbleupon.com/buzz/humor::http://feeds.delicious.com/v2/json/tag/funny+pictures+awesome?count=200";
	this.feeds_business = "http://feeds.delicious.com/v2/json/tag/business+interesting+startups?count=50::http://feeds.delicious.com/v2/json/tag/business+interesting+entrepreneurship+cool?count=30::http://feeds.delicious.com/v2/json/tag/business+web2.0+cool?count=30::http://feeds.delicious.com/v2/json/tag/business+cool+social+collaboration?count=30";
	this.feeds_entertainment = "http://feeds.delicious.com/v2/json/tag/cool+celebritycount=40::http://feeds.delicious.com/v2/json/tag/cool+entertainment?count=100";
	this.feeds_politics = "http://feeds.delicious.com/v2/json/tag/politics+cool?count=50::http://feeds.delicious.com/v2/json/tag/business+interesting+entrepreneurship+cool?count=30::http://feeds.delicious.com/v2/json/tag/business+web2.0+cool?count=30::http://feeds.delicious.com/v2/json/tag/business+cool+social+collaboration?count=30";
	this.feeds_technology = "http://feeds.delicious.com/v2/json/tag/cool+tips+programming?count=40::http://feeds.delicious.com/v2/json/tag/cool+tips+webdesign+interesting?count=10::http://feeds.delicious.com/v2/json/tag/webdesign+interesting+useful+cool?count=15::http://feeds.delicious.com/v2/json/tag/interesting+useful+cool+apple?count=15::http://feeds.delicious.com/v2/json/tag/cool+interesting+computer?count=50";
	this.feeds_sports = "http://feeds.delicious.com/v2/json/tag/sports+photos?count=150";
	this.feeds_art = "http://feeds.delicious.com/v2/json/tag/art+awesome+amazing?count=75::http://feeds.delicious.com/v2/json/tag/art+painting+interesting?count=100::http://feeds.delicious.com/v2/json/tag/art+creative+awesome?count=100";
};

/*
com_zimbra_discover.prototype._getMasterFeed =
function() {
	var feed = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this._masterFeed);
	AjxRpc.invoke(null, feed, null, new AjxCallback(this, this._masterFeedHandler), true);
};

com_zimbra_discover.prototype._masterFeedHandler =
function(result) {
	var items = "";
	try {
		items = result.xml.getElementsByTagName("item");
	} catch(e) {//there was some expn getting feed
		return;
	}
	for (var i = 0; i < items.length; i++) {
		try {
			var title = desc = "";
			var titleObj = items[i].getElementsByTagName("title")[0].firstChild;
			var descObj = items[i].getElementsByTagName("description")[0].firstChild;
			if (titleObj.textContent) {
				title = titleObj.textContent;
				desc = descObj.textContent;
			} else if (titleObj.text) {
				title = titleObj.text;
				desc = descObj.text;
			}


			//feed adds a table to the description, remove it
			desc = desc.replace("<table border='0'><tr><td valign='top'></td><td valign='top'>", "");
			desc = desc.replace("</td></tr></table> ", "");
			desc = desc.replace("\\n", "");

			if (title == "humor")
				this.feeds_humor = desc;
			else if (title == "business")
				this.feeds_business = desc;
			else if (title == "entertainment")
					this.feeds_entertainment = desc;
				else if (title == "politics")
						this.feeds_politics = desc;
					else if (title == "technology")
							this.feeds_technology = desc;
						else if (title == "sports")
								this.feeds_sports = desc;
							else if (title == "art")
									this.feeds_art = desc;
		} catch(e) {
		}
	}
	this.initToolbarButton();
};
*/
com_zimbra_discover.prototype._initializeVariables =
function() {

	this.linksString = "";
	this.feedsToUse = new Array();
	this._feedCount = 0;
	this.noOptionsAreSelected = true;
	this.getFeeds();
};

com_zimbra_discover.prototype.initToolbarButton = function() {
	if (!appCtxt.get(ZmSetting.MAIL_ENABLED))
		this._toolbar = true;

	if (this._toolbar)
		return;
	// Add the discover Button to the conversation page
	var viewid = appCtxt.getAppViewMgr().getCurrentViewId();
	if(viewid == ZmId.VIEW_CONVLIST) {
		this._cnvController = AjxDispatcher.run("GetConvListController");
		this._cnvController._discover = this;
		if (!this._cnvController._toolbar) {
			// initialize the conv controller's toolbar
			this._cnvController._initializeToolBar();
		}
		this._toolbar = this._cnvController._toolbar.CLV;
	} else if(viewid == ZmId.VIEW_TRAD) {
		this._tradController = AjxDispatcher.run("GetTradController");
		this._tradController._discover = this;
		if (!this._tradController._toolbar) {
			// initialize the trad controller's toolbar
			this._tradController._initializeToolBar();
		}
		this._toolbar = this._tradController._toolbar.TV;
	}

	if(!this._toolbar)
		return;//dont add button

	var indx = this._toolbar.getItemCount() + 1;

	// Add button to toolbar
	if (!this._toolbar.getButton(com_zimbra_discover.discover)) {
		ZmMsg.discoverlabel = "discover!";
		ZmMsg.discovertip = "Opens websites based on topics of your choice";

		var btn = this._toolbar.createOp(
			com_zimbra_discover.discover,
		{
			text	: ZmMsg.discoverlabel,
			tooltip : ZmMsg.discovertip,
			index   :indx,
			image   : "dy-panelIcon"
		}
			);

		btn.addSelectionListener(new AjxListener(this, this.discBtnListener));
	}
};

com_zimbra_discover.prototype.discBtnListener =
function() {
	this._initializeVariables();
	if (this.noOptionsAreSelected) {//if no selections were present, show the dialog again
		this.showSelectDialog();
		return;
	}
	this.getLinks();
};

com_zimbra_discover.prototype.showSelectDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.setSize("470", "320");
	this.pView.getHtmlElement().style.background = "white";
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	this.pbDialog = this._createDialog({title:"discover! preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));


	this._checkUncheckPreferredOptions();
	this.pbDialog.popup();

};

com_zimbra_discover.prototype._okBtnListner =
function() {
	this.savePreferences();
	this._topicsJustSelected = true;
	this.setUserProperty("dy_usingFirstTime", "false");
	this.pbDialog.popdown();
	this._enableDisableZimlet();

	//it we just enabled, start immediately
	if (document.getElementById("dy_enableDiscZimlet").checked) {
		this._initializeVariables();
		this.getLinks();
	}

};

com_zimbra_discover.prototype.savePreferences =
function() {
	for (var i = 0; i < this.selectionsList.length; i++) {
		var optn = this.selectionsList[i];
		if (document.getElementById("dy_chk_" + optn).checked) {
			this.setUserProperty("dy_pref_" + optn, "true");
		} else {
			this.setUserProperty("dy_pref_" + optn, "false");
		}
	}
};

com_zimbra_discover.prototype._checkUncheckPreferredOptions =
function() {
	for (var i = 0; i < this.selectionsList.length; i++) {
		var optn = this.selectionsList[i];
		if (this.getUserProperty("dy_pref_" + optn) == "true") {
			document.getElementById("dy_chk_" + optn).checked = true;
		}
	}

	if (this.getUserProperty("turnONDiscoverZimletNew") == "true") {
		document.getElementById("dy_enableDiscZimlet").checked = true;
	}


};

com_zimbra_discover.prototype.getFeeds =
function() {
	for (var i = 0; i < this.selectionsList.length; i++) {
		var feeds;
		var optn = this.selectionsList[i];
		if (this._topicsJustSelected != null) {
			if (document.getElementById("dy_chk_" + optn).checked) {
				this.noOptionsAreSelected = false;
				feeds = eval("(this.feeds_" + optn + ")").split("::");
				this.getFeedsToUse(feeds);
			}
		} else if (this.getUserProperty("dy_pref_" + optn) == "true") {
			this.noOptionsAreSelected = false;
			feeds = eval("(this.feeds_" + optn + ")").split("::");
			this.getFeedsToUse(feeds);
		}
	}

};

com_zimbra_discover.prototype.getFeedsToUse =
function(feedArray) {
	for (var i = 0; i < feedArray.length; i++) {
		this.feedsToUse.push(feedArray[i]);
	}
};

com_zimbra_discover.prototype.getLinks =
function() {

	if (this.getUserProperty("dy_usingFirstTime") == "true" && this._topicsJustSelected == null) {
		this.showSelectDialog();
	} else {
		var feedStr = this.feedsToUse[this._feedCount];
		var feed = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(feedStr);
		var cat = this.getFeedCategory(feedStr);
		this._feedCount = this._feedCount + 1;
		AjxRpc.invoke(null, feed, null, new AjxCallback(this, this._handleResult, cat), true);
	}

};

//categories muse be 4 char long
com_zimbra_discover.prototype.getFeedCategory =
function(feedStr) {
	if (this.feeds_humor.indexOf(feedStr) >= 0)
		return "humo";
	else if (this.feeds_business.indexOf(feedStr) >= 0)
		return "busi";
	else if (this.feeds_entertainment.indexOf(feedStr) >= 0)
			return "ente";
		else if (this.feeds_politics.indexOf(feedStr) >= 0)
				return "poli";
			else if (this.feeds_technology.indexOf(feedStr) >= 0)
					return "tech";
				else if (this.feeds_sports.indexOf(feedStr) >= 0)
						return "spor";
					else if (this.feeds_art.indexOf(feedStr) >= 0)
							return "arts";
};

com_zimbra_discover.prototype.getFullCategoryName =
function(shortName) {
	if (shortName == "humo")
		return "humor";
	else if (shortName == "busi")
		return "business";
	else if (shortName == "ente")
			return "entertainment";
		else if (shortName == "poli")
				return "politics";
			else if (shortName == "tech")
					return "technology";
				else if (shortName == "spor")
						return "sports";
					else if (shortName == "arts")
							return "arts";
};

com_zimbra_discover.prototype.openNewWindow =
function() {
	this._extWindow = window.open(this.getResource("discoverWindow.html"));
	setTimeout(AjxCallback.simpleClosure(this.postLinksToNewWindow, this, this.linksString), 1000);
	setTimeout(AjxCallback.simpleClosure(this.openFirstUrl, this, this.linksString), 1500);
};

com_zimbra_discover.prototype.openFirstUrl =
function(ls) {
	DBG.println(AjxDebug.DBG1, "*****ls: " + ls);

	var linksArry = ls.split("::");
	var randomnumber = Math.floor(Math.random() * linksArry.length);
	var url = linksArry[randomnumber];
	var cat = url.substring(0, 4);//get category
	url = url.substring(4);//remove category

	try {
		if (this._extWindow.document.frames) {
			this._extWindow.document.frames['externalpages'].location.href = url;
		} else {
			this._extWindow.document.getElementById('externalpages').src = url;
		}
		this._extWindow.document.getElementById('discover_urlName').innerHTML = " url: " + url;
		this._extWindow.document.getElementById('discover_urlType').innerHTML = " type: " + this.getFullCategoryName(cat);


		//for the first time,say that user can click on that..
		if (this.getUserProperty("dy_discoverBtnClickedOnExternalWindow") == "false" && this._userKnowsAbtDiscBtn == undefined) {
			this._extWindow.document.getElementById("discover_clickMsg").innerHTML = "click on the button to discover! more webpages";
			this._clickHereMsgTIid = setInterval(AjxCallback.simpleClosure(this._checkIfDiscBtnOnExtWindowClicked, this), 10000);
		}

	} catch(e) {
	}
};

com_zimbra_discover.prototype._checkIfDiscBtnOnExtWindowClicked =
function() {
	try {
		if (this._extWindow && this._extWindow.document &&
				this._extWindow.document.getElementById("discover_clickMsg").innerHTML == "") 
			{
			clearInterval(this._clickHereMsgTIid);
			this.setUserProperty("dy_discoverBtnClickedOnExternalWindow", "true", true);
		}
		this._userKnowsAbtDiscBtn = true;
	} catch(e) {
	}


};

com_zimbra_discover.prototype.postLinksToNewWindow =
function(ls) {
	try {
		this._extWindow.document.getElementById("urls").innerHTML = ls;
	} catch(e) {
	}
};

com_zimbra_discover.prototype._handleResult =
function(cat, result) {
	var checkJson_delicious = true;//json from delicious
	var checkJson_BOSS = false;//json from BOSS
	var check_rss = false;//rss
	if (checkJson_delicious) {
		try {
			var jsonArry = new Array();
			jsonArry = eval("(" + result.text + ")");
			if (jsonArry.length != 0) {
				for (var i = 0; i < jsonArry.length; i++) {
					var url = jsonArry[i].u;
					if (this.linksString == "") {
						this.linksString = cat + url;
					} else {
						this.linksString = this.linksString + "::" + cat + url;
					}
				}
			} else {
				check_rss = true;
			}
		} catch(e) {
			check_rss = true;
		}
	}

	if (check_rss) {
		try {
			var temp = result.xml.getElementsByTagName("item");
			if (temp.length != 0) {
				for (var i = 0; i < temp.length; i++) {
					var lnk = "";
					var lnkObj = temp[i].getElementsByTagName("link")[0].firstChild;
					if (lnkObj.nodeValue) {
						lnk = lnkObj.nodeValue;
					} else if (lnkObj.text) {
						lnk = lnkObj.text;
					}

					if (this.linksString == "") {
						this.linksString = cat + lnk;
					} else {
						this.linksString = this.linksString + "::" + cat + lnk;
					}
				}
			} else {
				checkJson_BOSS = true;
			}

		} catch(e) {
			checkJson_BOSS = true;
		}
	}

	if (checkJson_BOSS) {
		try {
			var jsonArry = new Array();
			jsonArry = eval("(" + result.text + ")").ResultSet.Result;
			if (jsonArry.length != 0) {
				for (var i = 0; i < jsonArry.length; i++) {
					var url = jsonArry[i].Url;
					if (this.linksString == "") {
						this.linksString = cat + url;
					} else {
						this.linksString = this.linksString + "::" + cat + url;
					}
				}
			} else {
			}
		} catch(e) {
		}
	}

	DBG.println(AjxDebug.DBG1, "***** check_rss checkJson_BOSS: " + check_rss + "  " + checkJson_BOSS);
	var len = this.linksString.split("::").length;

	//open new window after getting the first set of urls
	if (this._feedCount == 1 && this.linksString != "") {
		this.openNewWindow();
	}

	//load all the feeds in feedsToUse array
	if (this._feedCount < this.feedsToUse.length) {
		setTimeout(AjxCallback.simpleClosure(this.getLinks, this), 500);
	} else {//after all the feeds are loaded, post the new set of links to newWindow
		this.postLinksToNewWindow(this.linksString);
		this._feedCount = 0;//reset feedcount so if the window was closed, we can reopen
		this.linksString = "";
	}

};

com_zimbra_discover.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV align='center' class='dy_selectOptDiv'>";
	html[i++] = "<TABLE class='dy_selectOptTable'><TR><TD>Select some of your favourite topics to discover!</TD></TR></TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<TABLE>";
	html[i++] = "<tr><td><input id='dy_chk_humor'  type='checkbox'/></td><td class='dy_optionsName'>humor<span class='dy_optionsEtc'>(funny photos, blogs, jokes etc)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_art'  type='checkbox'/></td><td class='dy_optionsName'>art<span class='dy_optionsEtc'>(photos, websites & articles related art)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_business'  type='checkbox'/></td><td class='dy_optionsName'>business<span class='dy_optionsEtc'>(stocks, entrepreneurship, startups etc)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_entertainment'  type='checkbox'/></td><td class='dy_optionsName'>entertainment<span class='dy_optionsEtc'>(tv, movies, celebrity news and photos)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_politics'  type='checkbox'/></td><td class='dy_optionsName'>politics<span class='dy_optionsEtc'>(anything political)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_technology'  type='checkbox'/></td><td class='dy_optionsName'>technology<span class='dy_optionsEtc'>(software, programming, design tips and tricks etc)</span></td></tr>";
	html[i++] = "<tr><td><input id='dy_chk_sports'  type='checkbox'/></td><td class='dy_optionsName'>sports<span class='dy_optionsEtc'>(nfl, nba, fantasy football, cricket etc)</span></td></tr>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='dy_enableDiscZimlet'  type='checkbox'/><span class='dy_optionsEtc'> Enable discover! zimlet</span>";
	html[i++] = "</DIV>";
	return html.join("");

};

com_zimbra_discover.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_discover.prototype.singleClicked = function() {
	this.showSelectDialog();
};


com_zimbra_discover.prototype._enableDisableZimlet =
function() {
	this._reloadRequired = false;

	if (document.getElementById("dy_enableDiscZimlet").checked) {
		if (!this.discZimletON) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONDiscoverZimletNew", "true");

	} else {
		this.setUserProperty("turnONDiscoverZimletNew", "false");
		if (this.discZimletON)
			this._reloadRequired = true;
	}
	this.pbDialog.popdown();
	if (this._reloadRequired) {
		this.saveUserProperties(new AjxCallback(this, this._refreshBrowser));
	} else{
		this.saveUserProperties(null);
	}

};

com_zimbra_discover.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};
