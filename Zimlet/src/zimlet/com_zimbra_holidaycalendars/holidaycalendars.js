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

/**
 * View country holidays based on ical.mac.com calendar feeds.
 * 
 * @author Raja Rao DV
 */
function com_zimbra_holidaycalendars_HandlerObject() {
}

com_zimbra_holidaycalendars_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_holidaycalendars_HandlerObject.prototype.constructor = com_zimbra_holidaycalendars_HandlerObject;

/**
 * Simplify handler object
 *
 */
var HolidayCalendarsZimlet = com_zimbra_holidaycalendars_HandlerObject;

/**
 * Defines the "appointment" view.
 */
HolidayCalendarsZimlet.VIEW = "appointment";

/**
 * Initializes the zimlet.
 * 
 */
HolidayCalendarsZimlet.prototype.init =
function() {
};

/**
 * Loads the calendar feeds.
 */
HolidayCalendarsZimlet.prototype._loadFeeds =
function() {
	this.feeds = [];
	//i=image, t=title(in the dialog), fn=(folderName)
	this.feeds["webcal://ical.mac.com/ical/Australian32Holidays.ics"] = {i:"ImgFlagAU",  t:this.getMessage("HolidayCalendarsZimlet_holidays_australian") };
	this.feeds["webcal://ical.mac.com/ical/China32Lunar32Simplified32Holidays.ics"] = {i:"ImgFlagCN", t:this.getMessage("HolidayCalendarsZimlet_holidays_chineseSimplified") };
	this.feeds["webcal://ical.mac.com/ical/Brazil32Holidays.ics"] = {i:"ImgFlagBR",  t:this.getMessage("HolidayCalendarsZimlet_holidays_brazil") };
	this.feeds["webcal://ical.mac.com/ical/Danish32Holidays.ics"] = {i:"ImgFlagDK",  t:this.getMessage("HolidayCalendarsZimlet_holidays_danish")};
	this.feeds["webcal://ical.mac.com/ical/Dutch32Holidays.ics"] = {i:"ImgFlagNL",  t:this.getMessage("HolidayCalendarsZimlet_holidays_dutch") };
	this.feeds["webcal://ical.mac.com/ical/Irish32Holidays.ics"] = {i:"ImgFlagIE",  t:this.getMessage("HolidayCalendarsZimlet_holidays_irish") };
	this.feeds["webcal://ical.mac.com/ical/Finnish32Holidays.ics"] = {i:"ImgFlagFI",  t:this.getMessage("HolidayCalendarsZimlet_holidays_finnish") };
	this.feeds["webcal://ical.mac.com/ical/French32Holidays.ics"] = {i:"ImgFlagFR",  t:this.getMessage("HolidayCalendarsZimlet_holidays_french") };
	this.feeds["webcal://ical.mac.com/ical/German32Holidays.ics"] = {i:"ImgFlagDE",  t:this.getMessage("HolidayCalendarsZimlet_holidays_german") };
	this.feeds["webcal://ical.mac.com/ical/Hong32Kong32Holidays.ics"] = {i:"ImgFlagHK",  t:this.getMessage("HolidayCalendarsZimlet_holidays_hongKong") };
	this.feeds["webcal://ical.mac.com/ical/India32Holidays.ics"] = {i:"ImgFlagIN",  t:this.getMessage("HolidayCalendarsZimlet_holidays_india") };
	this.feeds["webcal://ical.mac.com/ical/Italian32Holidays.ics"] = {i:"ImgFlagIT",  t:this.getMessage("HolidayCalendarsZimlet_holidays_italian") };
	this.feeds["webcal://ical.mac.com/ical/Japanese32Holidays.ics"] = {i:"ImgFlagJP",  t:this.getMessage("HolidayCalendarsZimlet_holidays_japanese") };
	this.feeds["webcal://ical.mac.com/ical/Mexican32Holidays.ics"] = {i:"ImgFlagMX",  t:this.getMessage("HolidayCalendarsZimlet_holidays_mexican") };
	this.feeds["webcal://ical.mac.com/ical/Norwegian32Holidays.ics"] = {i:"ImgFlagNO",  t:this.getMessage("HolidayCalendarsZimlet_holidays_norwegian") };
	this.feeds["webcal://ical.mac.com/ical/New32Zealand32Holidays.ics"] = {i:"ImgFlagNZ",  t:this.getMessage("HolidayCalendarsZimlet_holidays_newZealand") };
	this.feeds["webcal://ical.mac.com/ical/Portuguese32Holidays.ics"] = {i:"ImgFlagPT",  t:this.getMessage("HolidayCalendarsZimlet_holidays_portuguese") };
	this.feeds["webcal://ical.mac.com/ical/South32Africa32Holidays.ics"] = {i:"ImgFlagZA",  t:this.getMessage("HolidayCalendarsZimlet_holidays_southAfrican") };
	this.feeds["webcal://ical.mac.com/ical/South32Korean32Holidays.ics"] = {i:"ImgFlagKR",  t:this.getMessage("HolidayCalendarsZimlet_holidays_southKorean") };
	this.feeds["webcal://ical.mac.com/ical/Swedish32Holidays.ics"] = {i:"ImgFlagSE",  t:this.getMessage("HolidayCalendarsZimlet_holidays_swedish") };
	this.feeds["webcal://ical.mac.com/ical/Spain32Holidays.ics"] = {i:"ImgFlagES",  t:this.getMessage("HolidayCalendarsZimlet_holidays_spain") };
	this.feeds["webcal://ical.mac.com/ical/Taiwan32Holidays.ics"] = {i:"ImgFlagTW",  t:this.getMessage("HolidayCalendarsZimlet_holidays_taiwan") };
	this.feeds["webcal://ical.mac.com/ical/UK32Holidays.ics"] = {i:"ImgFlagGB",  t:this.getMessage("HolidayCalendarsZimlet_holidays_uk") };
	this.feeds["webcal://ical.mac.com/ical/US32Holidays.ics"] = {i:"ImgFlagUS",  t:this.getMessage("HolidayCalendarsZimlet_holidays_us") };
};

/**
 * Called by the framework on double-click.
 */
HolidayCalendarsZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Called by the framework on single-click.
 */
HolidayCalendarsZimlet.prototype.singleClicked =
function() {
	this._initializeDlg();
};

/**
 * Creates a folder.
 * 
 * @param	{hash}	params		a hash of parameters
 */
HolidayCalendarsZimlet.prototype._createFolder =
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

/**
 * Callback for create folder.
 * 
 * @see		_createFolder
 */
HolidayCalendarsZimlet.prototype._createFldrCallback =
function(params, response) {
	var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
	
	var successMsg = AjxMessageFormat.format(this.getMessage("HolidayCalendarsZimlet_successSubscribing"), params.name);
		
	appCtxt.getAppController().setStatusMsg(successMsg, ZmStatusView.LEVEL_INFO, null, transitions);
};

/**
 * Callback for create folder error.
 * 
 * @see		_createFldrErrCallback
 */
HolidayCalendarsZimlet.prototype._createFldrErrCallback =
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
	appCtxt.getAppController().setStatusMsg(this.getMessage("HolidayCalendarsZimlet_errorSubscribing"), ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}
	return false;
};

/**
 * Shows an error message.
 * 
 * @param	{string}	msg		the message
 */
HolidayCalendarsZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

/**
 * Initializes the calendar dialog.
 */
HolidayCalendarsZimlet.prototype._initializeDlg =
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
	
	var dialogArgs = {
			title	: this.getMessage("HolidayCalendarsZimlet_dialog_title"),
			view	: this._parentView,
			parent	: this.getShell(),
			standardButtons : [DwtDialog.OK_BUTTON]
	};
	
	this.rssFeedDialog = new ZmDialog(dialogArgs);
	this._addBtnListeners();
	this.rssFeedDialog.popup();
};

/**
 * Constructs the view for the calendar dialog.
 * 
 * @see			_initializeDlg
 */
HolidayCalendarsZimlet.prototype._constructView =
function() {
	var html = new Array();
	var i = 0;
	var idCnt = 0;
	this._btnidAndUrl = [];

	for (var el in this.feeds) {

		var img = this.feeds[el].i;
		var title = this.feeds[el].t;
		var btnId = "hcals_btn" + idCnt;
		this._btnidAndUrl[btnId] = { url:el, foldername:title };
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
		html[i++] = "<TD width=10%><button id='hcals_btn" + idCnt + "' type=\"button\">";
		html[i++] = this.getMessage("HolidayCalendarsZimlet_dialog_subscribeButton");
		html[i++] = "</button></TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</div>";
		idCnt = idCnt + 1;
	}
	return html.join("");
};

/**
 * Adds the button listeners.
 * 
 * @see			_initializeDlg
 */
HolidayCalendarsZimlet.prototype._addBtnListeners =
function() {
	for (var id in this._btnidAndUrl) {
		document.getElementById(id).onclick = AjxCallback.simpleClosure(this._onSubscribeClick, this, this._btnidAndUrl[id]);
	}
};

/**
 * Handles the subscribe button event.
 * 
 * @param	{hash}		params		a hash of params
 * @param	{string}	params.foldername		the folder name
 * @param	{string}	params.url		the feed url
 * 
 * @see			_initializeDlg
 */
HolidayCalendarsZimlet.prototype._onSubscribeClick =
function(params) {
	// create the RSS folder
	var fldrName = params.foldername;
	var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT];
	
	var createFolderMsg = AjxMessageFormat.format(this.getMessage("HolidayCalendarsZimlet_createFolderMsg"), fldrName);
	
	appCtxt.getAppController().setStatusMsg(createFolderMsg, ZmStatusView.LEVEL_INFO, null, transitions);
	
	var parentFldrId = "1";
	var randomnumber = Math.floor(Math.random() * 9);
	var params = {color:null,name:fldrName, url:params.url,  view:HolidayCalendarsZimlet.VIEW, l:parentFldrId, color:randomnumber};
	
	this._createFolder(params);
};
