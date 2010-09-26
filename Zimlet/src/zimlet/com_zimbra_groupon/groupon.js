/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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


/**
* @author Raja Rao DV (rrao@zimbra.com)
*/


function com_zimbra_groupon_handlerObject() {
}

/// Zimlet handler objects, such as com_zimbra_groupon, must inherit from
/// ZmZimletBase.  The 2 lines below achieve this.
com_zimbra_groupon_handlerObject.prototype = new ZmZimletBase();
com_zimbra_groupon_handlerObject.prototype.constructor = com_zimbra_groupon_handlerObject;

var GrouponZimlet = com_zimbra_groupon_handlerObject;

//static variables
GrouponZimlet.API_KEY = "da764d22a837b923b4ff39fa4f2383edcf3d1a8e";
GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY = "grouponZimlet_showPopupOnceADay1";

GrouponZimlet.SHOW_FEATURED_AS_POPUP = "showDealAsPopup";
GrouponZimlet.SHOW_IN_CARD_VIEW = "showInCardView";
GrouponZimlet.USER_PROP_LAST_UPDATE = "grouponZimlet_lastUpdateDate";
GrouponZimlet.DIVISIONS_URL = "http://www.groupon.com/api/v1/divisions?format=json";

GrouponZimlet.prototype.init = function() {
	this.grouponDivisions = null;
	this._getDivisions();
	this._shell = this.getShell();
	this._sfAppName = this.createApp("Groupon", "GrouponIcon", "See amazing daily deals from Groupon");
	this._dealAreaCode = this.getUserProperty("grouponZimlet_myCityCode");
	var grouponZimlet_showPopupOnceADay = this.getUserProperty(GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY) == "true";
	if (grouponZimlet_showPopupOnceADay) {
		this._checkDateAndShowPopup();
	}
};

/**
 * Shows Groupon Deal popup once a day
 */
GrouponZimlet.prototype._checkDateAndShowPopup =
function() {
	var emailLastUpdateDate = this.getUserProperty(GrouponZimlet.USER_PROP_LAST_UPDATE);
	this._todayStr = this._getTodayStr();
	if (emailLastUpdateDate != this._todayStr) {
		this.getDeals(this._dealAreaCode, GrouponZimlet.SHOW_FEATURED_AS_POPUP);
		//saving current date is done only if valid response is returned
	}
};

GrouponZimlet.prototype._getDivisions = function() {
	var callback = new AjxCallback(this, this._handleGetDivisions);
	var url = GrouponZimlet.DIVISIONS_URL;
	url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, url, {"X-GrouponToken":GrouponZimlet.API_KEY}, callback, true);
};

GrouponZimlet.prototype._handleGetDivisions = function(response) {
	var jsonObj = {};
	try {
		jsonObj = eval("(" + response.text + ")");
	} catch(ex) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("grouponError")+response.status, ZmStatusView.LEVEL_WARNING);
		return;
	}
	if (!response.success) {
		appCtxt.getAppController().setStatusMsg(this.getMessage("grouponError")+response.status, ZmStatusView.LEVEL_WARNING);
		return;
	}
	this.grouponDivisions = jsonObj;
	this.setUserProperty(GrouponZimlet.USER_PROP_LAST_UPDATE, this._todayStr, true);//save current date upon valid response
};

//is called only once and after appActive
GrouponZimlet.prototype.appLaunch = function(appName) {
	if (appName != this._sfAppName) {
		return;
	}

	this.tabApp = appCtxt.getCurrentApp();//actual app
	this.grouponApp = new Com_Zimbra_GrouponApp(this, this.tabApp);
	this.grouponApp.show();
	this.getDeals(this._dealAreaCode, GrouponZimlet.SHOW_IN_CARD_VIEW);
};

GrouponZimlet.prototype.getDeals = function(areaCode, mode) {
	var callback = new AjxCallback(this, this._handleGetDeals, mode);
	var url = ["http://www.groupon.com/api/v1/",areaCode,"/deals?format=json"].join("");
	url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, url, {"X-GrouponToken":GrouponZimlet.API_KEY}, callback, true);
};

GrouponZimlet.prototype._handleGetDeals = function(mode, response) {
	var jsonObj = {};
	try {
		jsonObj = eval("(" + response.text + ")");
	} catch(ex) {
			appCtxt.getAppController().setStatusMsg(this.getMessage("grouponError")+response.status, ZmStatusView.LEVEL_WARNING);
			return;
	}
	if (!response.success) {
			appCtxt.getAppController().setStatusMsg(this.getMessage("grouponError")+response.status, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var deals = jsonObj.deals;
	if (mode == GrouponZimlet.SHOW_FEATURED_AS_POPUP) {
		this._showFeaturedDealAsPopup(deals);
		return;
	}
	var len = deals ? deals.length : 0;
	for (var i = 0; i < len; i++) {
		this._openNewsFeedCard(deals[i]);
	}
};

GrouponZimlet.prototype._showFeaturedDealAsPopup = function(deals) {
	var len = deals ? deals.length : 0;
	for (var i = 0; i < len; i++) {
		var deal = deals[i];
		if (deal.placement_priority == "featured") {
			this._showPopup(deal);
			break;
		}
	}
};

GrouponZimlet.prototype._showPopup = function(deal) {
	var gApp = new Com_Zimbra_GrouponApp(this);
	var html = gApp._getCardDetailedHtml(deal, GrouponZimlet.SHOW_FEATURED_AS_POPUP);
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
	appCtxt.getAppController().setStatusMsg(html, ZmStatusView.LEVEL_INFO, null, transitions);
};


GrouponZimlet.prototype._openNewsFeedCard = function(deal) {
	var cardProps = new Com_Zimbra_GrouponCardProps();
	if (deal.placement_priority == "featured") {
		cardProps.featured = true;
	} else {
		cardProps.featured = false;
	}

	cardProps.deal = deal;
	cardProps.headerName = deal.vendor_name;
	cardProps.type = "FACEBOOK";
	cardProps.headerIcon = params.headerIcon;
	cardProps.autoScroll = true;
	var tableId = this.grouponApp._showCard(cardProps);

	cardProps.isClosed = false;
	cardProps.feedPostParentId = this.userId;
	this.grouponApp._allCardsProps[tableId] = cardProps;
	this.grouponApp.createCardView(cardProps.tableId, cardProps.deal);
};

/*
 * -------------------------------------
 * Preference Dialog related functions
 * -------------------------------------
 */

/**
 * This method is called when the panel is double-clicked.
 *
 */
GrouponZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * This method is called when the panel is single-clicked.
 *
 */
GrouponZimlet.prototype.singleClicked = function() {
	this._displayPrefDialog();
};

/**
 * Displays the preferences dialog.
 *
 */
GrouponZimlet.prototype._displayPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this._setPreferencesChkBoxVal();
		this.pbDialog.popup();//simply popup the dialog
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().style.overflow = "auto";
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();

	var dialog_args = {
		title	: this.getMessage("preferences_title"),
		view	: this.pView,
		standardButtons : [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON],
		parent	: this.getShell()
	};

	this.pbDialog = new ZmDialog(dialog_args);
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._setPreferencesChkBoxVal();
	this.pbDialog.popup();
};

/**
 * Sets the preferences.
 */
GrouponZimlet.prototype._setPreferencesChkBoxVal =
function() {
	if (this.getUserProperty(GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY) == "true") {
		document.getElementById(GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY).checked = true;
	}
};

/**
 * Creates the preferences view.
 *
 * @return	{string}	the view HTML
 * @see		_displayPrefDialog
 */
GrouponZimlet.prototype._createPreferenceView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='";
	html[i++] = GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY;
	html[i++] = "'  type='checkbox'/>";
	html[i++] = this.getMessage("showDailyDealsAsPopup");
	html[i++] = "</DIV>";
	return html.join("");
};

/**
 * Preferences dialog OK button listener.
 *
 * @see		_displayPrefDialog
 */
GrouponZimlet.prototype._okBtnListner =
function() {
	this.setUserProperty(GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY, document.getElementById(GrouponZimlet.PROP_SHOW_POPUP_ONCE_A_DAY).checked, true);
	appCtxt.getAppController().setStatusMsg(this.getMessage("preferences_saved"), ZmStatusView.LEVEL_INFO);
	this.pbDialog.popdown();//hide the dialog
};

/*
 * -------------------------------------
 * Supporting functions
 * -------------------------------------
 */

/**
 * Gets today as a string.
 *
 * @return	{string}	today as a string
 */
GrouponZimlet.prototype._getTodayStr =
function() {
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(), todayDate.getMonth(), todayDate.getDate());
	return this._formatDate(todayStart.getMonth() + 1, todayStart.getDate(), todayStart.getFullYear());
};

/**
 * Formats the date.
 *
 * @param	{string}	month		the month
 * @param	{string}	day		the day
 * @param	{string}	year		the year
 * @return	{string}	the formatted date
 */
GrouponZimlet.prototype._formatDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"y", indx:ds.indexOf("y")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(grouponZimlet_sortTimeObjs);
	for (var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if (name == "m") {
			fString.push(month);
		} else if (name == "y") {
			fString.push(year);
		} else if (name == "d") {
			fString.push(day);
		}
	}
	return fString.join("/");
};

/**
 * Sorts time objects based on index
 *
 * @param	{hash}	a A hash
 * @param  {string} a.name  first letter of month/year/date
 * @param  {int} a.indx  index of month/year/date
 * @param	{hash}	b A hash
 * @param  {string} b.name  first letter of month/year/date
 * @param  {int} b.indx  index of month/year/date
 * @return	{hash}	sorted objects
 */
function grouponZimlet_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}