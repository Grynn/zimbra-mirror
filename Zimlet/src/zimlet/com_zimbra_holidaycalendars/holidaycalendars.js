/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 * Shows about 25 country's holidays based on ical.mac.com
 */

function com_zimbra_holidaycalendars() {
}

com_zimbra_holidaycalendars.prototype = new ZmZimletBase();
com_zimbra_holidaycalendars.prototype.constructor = com_zimbra_holidaycalendars;
com_zimbra_holidaycalendars.view = "appointment";
com_zimbra_holidaycalendars.holidaycalendarsFolder = 'Holiday Calendars';

com_zimbra_holidaycalendars.prototype._loadFeeds =
function() {
	this.feeds = [];
	//c = category, i=image, t=title(in the dialog), fn=(folderName)
	this.feeds["webcal://ical.mac.com/ical/Australian32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagAU",  t:"Australian Holidays"};
	this.feeds["webcal://ical.mac.com/ical/China32Lunar32Simplified32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagCN",  t:"Chinese Lunar Simplified Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Brazil32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagBR",  t:"Brazil Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Danish32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagDK",  t:"Danish Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Dutch32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagNL",  t:"Dutch Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Irish32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagIE",  t:"Irish Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Finnish32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagFI",  t:"Finnish Holidays"};
	this.feeds["webcal://ical.mac.com/ical/French32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagFR",  t:"French Holidays"};
	this.feeds["webcal://ical.mac.com/ical/German32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagDE",  t:"German Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Hong32Kong32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagHK",  t:"Hong Kong Holidays"};
	this.feeds["webcal://ical.mac.com/ical/India32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagIN",  t:"India Holidays"};
	//this.feeds["webcal://ical.mac.com/ical/Indonesian32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagAU",  t:"Indonesian Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Italian32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagIT",  t:"Italian Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Japanese32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagJP",  t:"Japanese Holidays"};
	//this.feeds["webcal://ical.mac.com/ical/Malaysia32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagMY",  t:"Malasian Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Mexican32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagMX",  t:"Mexican Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Norwegian32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagNO",  t:"Norwegian Holidays"};
	this.feeds["webcal://ical.mac.com/ical/New32Zealand32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagNZ",  t:"New Zealand Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Portuguese32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagPT",  t:"Portuguese Holidays"};
	//this.feeds["webcal://ical.mac.com/ical/Philippines32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagPI",  t:"Philippines Holidays"};
	//this.feeds["webcal://ical.mac.com/ical/Singapore32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagSG",  t:"Singapore Holidays"};
	this.feeds["webcal://ical.mac.com/ical/South32Africa32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagZA",  t:"South African Holidays"};
	this.feeds["webcal://ical.mac.com/ical/South32Korean32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagKR",  t:"South Korean Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Swedish32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagSE",  t:"Swedish Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Spain32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagES",  t:"Spain Holidays"};
	this.feeds["webcal://ical.mac.com/ical/Taiwan32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagTW",  t:"Taiwan Holidays"};
	this.feeds["webcal://ical.mac.com/ical/UK32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagGB",  t:"UK Holidays"};
	this.feeds["webcal://ical.mac.com/ical/US32Holidays.ics"] = {c:"Holiday Calendars", i:"ImgFlagUS",  t:"US Holidays"};
};

com_zimbra_holidaycalendars.prototype.singleClicked =
function() {
	this._initializeDlg();
};

com_zimbra_holidaycalendars.prototype._createFolder =
function(params) {
	var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
	var folder = jsonObj.CreateFolderRequest.folder = {};
	for (var i in params) {
		if (i == "callback" || i == "errorCallback" || i == "postCallback") {
			continue;
		}

		var value = params[i];
		if (value) {
			folder[i] = value;
		}
	}
	var _createFldrCallback = new AjxCallback(this, this._createFldrCallback, params);
	var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params);
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
};

com_zimbra_holidaycalendars.prototype._createFldrCallback =
function(params, response) {
	if (params.name == com_zimbra_holidaycalendars.holidaycalendarsFolder) {
		this.mainRssFeedFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback) {
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("Successfully subscribed to holiday calendar", ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

com_zimbra_holidaycalendars.prototype._createFldrErrCallback =
function(params, ex) {
	if (!params.url && !params.name) {
		return false;
	}

	var msg;
	if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name]);
	} else if (params.url) {
		var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
		msg = AjxMessageFormat.format(errorMsg, params.url);
	}
	var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
	appCtxt.getAppController().setStatusMsg("Could Not Subscribe to holiday calendar", ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}
	return false;
};

com_zimbra_holidaycalendars.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

com_zimbra_holidaycalendars.prototype._initializeDlg =
function() {
	if (this.rssFeedDialog) {
		this.rssFeedDialog.popup();
		return;
	}

	this._loadFeeds();
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("550", "300");
	this._parentView.getHtmlElement().style.overflow = "auto";
	this._parentView.getHtmlElement().innerHTML = this._constructView();
	this.rssFeedDialog = this._createDialog({title:"Subscribe to Holiday Calendars", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
	this._addBtnListeners();
	this.rssFeedDialog.popup();
};

com_zimbra_holidaycalendars.prototype._constructView =
function() {
	this._currentCategory = "";
	var html = new Array();
	var i = 0;
	var idCnt = 0;
	this._btnidAndUrl = [];

	for (var el in this.feeds) {

		var img = this.feeds[el].i;
		var category = this.feeds[el].c;
		var title = this.feeds[el].t;
		var btnId = "hcals_btn" + idCnt;
		this._btnidAndUrl[btnId] = {url:el, foldername:title, category:this.feeds[el].c};
		if (this._currentCategory != category) {
			html[i++] = "<div class='hcals_HdrDiv'>";
			html[i++] = category;
			html[i++] = "</div>";
			this._currentCategory = category;
		}
		html[i++] = "<div class='hcals_sectionDiv'>";
		html[i++] = "<TABLE  cellpadding=5>";
		html[i++] = "<TR>";
		html[i++] = "<TD width=5%><DIV class='" + img + "'></DIV></TD>";
		html[i++] = "<TD width=80%>";
		html[i++] = "<B>";
		html[i++] = title;
		html[i++] = "</B>";
		html[i++] = "<BR>";
		html[i++] = el;
		html[i++] = "</TD>";
		html[i++] = "<TD width=10%><button id='hcals_btn" + idCnt + "' type=\"button\">Subscribe</button></TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</div>";
		idCnt = idCnt + 1;
	}
	return html.join("");
};

com_zimbra_holidaycalendars.prototype._addBtnListeners =
function() {
	for (var id in this._btnidAndUrl) {
		document.getElementById(id).onclick = AjxCallback.simpleClosure(this._onSubscribeClick, this, this._btnidAndUrl[id]);
	}
};

com_zimbra_holidaycalendars.prototype._onSubscribeClick =
function(params) {
	this._createRSSFolder(params);
};

com_zimbra_holidaycalendars.prototype._createRSSFolder =
function(params) {
	var fldrName = params.foldername;
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];
	appCtxt.getAppController().setStatusMsg("Creating '" + fldrName + "' Calendar...", ZmStatusView.LEVEL_INFO, null, transitions);
	var parentFldrId = "1";
	var randomnumber = Math.floor(Math.random() * 9);
	var params = {color:null,name:fldrName, url:params.url,  view:com_zimbra_holidaycalendars.view, l:parentFldrId, color:randomnumber};
	this._createFolder(params);
};