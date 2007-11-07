/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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
 * Creates an error dialog which basically means it will have a "Report" button
 * @constructor
 * @class
 * A normal DwtMessageDialog w/ a "Report" button that will post user info to the 
 * server when clicked.
 */
ZmErrorDialog = function(parent, msgs) {

	// go ahead and cache the navigator and subject info now (since it should never change)		
	this._strNav = this._getNavigatorInfo();
	this._subjPfx = this._getSubjectPrefix();

	var reportButton = new DwtDialog_ButtonDescriptor(ZmErrorDialog.REPORT_BUTTON, msgs.report, DwtDialog.ALIGN_LEFT);
	var detailButton = new DwtDialog_ButtonDescriptor(ZmErrorDialog.DETAIL_BUTTON, msgs.showDetails, DwtDialog.ALIGN_LEFT);
	DwtMessageDialog.call(this, parent, null, null, [reportButton, detailButton]);

	this.registerCallback(ZmErrorDialog.REPORT_BUTTON, this._reportCallback, this);
	this.registerCallback(ZmErrorDialog.DETAIL_BUTTON, this._showDetail, this);
	
	this._showDetailsMsg = msgs.showDetails;
	this._hideDetailsMsg = msgs.hideDetails;
}

ZmErrorDialog.prototype = new DwtMessageDialog;
ZmErrorDialog.prototype.constructor = ZmErrorDialog;

ZmErrorDialog.prototype.toString = function() {
	return "ZmErrorDialog";
};

//
// Consts
//

ZmErrorDialog.REPORT_BUTTON = ++DwtDialog.LAST_BUTTON;
ZmErrorDialog.DETAIL_BUTTON = ++DwtDialog.LAST_BUTTON;
ZmErrorDialog.REPORT_URL = "//www.zimbra.com/e/";

//
// Data
//

ZmErrorDialog.prototype._detailsVisible = false;
ZmErrorDialog.prototype.CONTROLS_TEMPLATE = "zimbra.Widgets#ZmErrorDialogControls";

//
// Public methods
//

ZmErrorDialog.prototype.reset =
function() {
	this.setDetailString();
	DwtMessageDialog.prototype.reset.call(this);
};

/**
* Sets the text that shows up when the Detail button is pressed.
*
* @param text	detail text
*/
ZmErrorDialog.prototype.setDetailString = 
function(text) {
    if (!(this._button[ZmErrorDialog.DETAIL_BUTTON])) { return; }

    this._button[ZmErrorDialog.DETAIL_BUTTON].setVisible(text != null);
    this._detailStr = text;
};

ZmErrorDialog.prototype.setMessage =
function(msgStr, detailStr, style, title) {
	this._msgStr = msgStr;
	this._detailStr = detailStr;
	this._msgStyle = style;
	this._msgTitle = title;

	// clear the 'detailsVisible' flag and reset the title of the 'showDetails' button
	this._detailsVisible = false;
	this._button[ZmErrorDialog.DETAIL_BUTTON].setText(this._showDetailsMsg);
	
	DwtMessageDialog.prototype.setMessage.call(this, msgStr, style, title);
};


ZmErrorDialog.prototype.popdown = function() {
    DwtMessageDialog.prototype.popdown.call(this);
    this.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, true);
};

//
// Protected methods
//

ZmErrorDialog.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtMessageDialog.prototype._createHtmlFromTemplate.call(this, templateId, data);
};

ZmErrorDialog.prototype._getNavigatorInfo =
function() {
	var strNav = [];
	var idx = 0;
	
	// Add the url
	strNav[idx++] = "\n\n";
	strNav[idx++] = "href: ";
	strNav[idx++] = location.href;
	strNav[idx++] = "\n";

	for (var i in navigator) {
		// Skip functions
		if(typeof navigator[i] == "function") {continue;}
		if(typeof navigator[i] == "unknown") {continue;}	// IE7
		strNav[idx++] = i + ": " + navigator[i] + "\n";
	}
	return strNav.join("");
};

ZmErrorDialog.prototype._getSubjectPrefix = 
function() {
	var strSubj = [];
	var idx = 0;
	
	strSubj[idx++] = "ER: ";
	
	if (AjxEnv.isIE) 				strSubj[idx++] = "IE ";
	else if (AjxEnv.isFirefox)		strSubj[idx++] = "FF ";
	else if (AjxEnv.isMozilla)		strSubj[idx++] = "MOZ ";
	else if (AjxEnv.isSafari)		strSubj[idx++] = "SAF ";
	else if (AjxEnv.isOpera)		strSubj[idx++] = "OPE ";
	else							strSubj[idx++] = "UKN ";

	if (AjxEnv.isWindows)			strSubj[idx++] = "WIN ";
	else if (AjxEnv.isLinux)		strSubj[idx++] = "LNX ";
	else if (AjxEnv.isMac)			strSubj[idx++] = "MAC ";
	else							strSubj[idx++] = "UNK ";

	strSubj[idx++] = appCtxt.get(ZmSetting.CLIENT_VERSION) + " ";
	return strSubj.join("");
};

ZmErrorDialog.prototype._getUserPrefs = 
function() {
	var currSearch = appCtxt.getCurrentSearch();
	var strPrefs = [];
	var idx = 0;

	// Add username and current search
	strPrefs[idx++] = "\n\n";
	strPrefs[idx++] = "username: ";
	strPrefs[idx++] = appCtxt.get(ZmSetting.USERNAME);
	strPrefs[idx++] = "\n";
	if (currSearch) {
		strPrefs[idx++] = "currentSearch: ";
		strPrefs[idx++] = currSearch.query;
		strPrefs[idx++] = "\n";
	}
	for (var i in ZmSetting.INIT) {
		if (ZmSetting.INIT[i][0]) {
			strPrefs[idx++] = ZmSetting.INIT[i][0];
			strPrefs[idx++] = ": ";
			strPrefs[idx++] = ("" + ZmSetting.INIT[i][3]);
			strPrefs[idx++] = "\n";
		}
	}
	return strPrefs.join("");
};

// Callbacks

ZmErrorDialog.prototype._reportCallback =
function() {
	// iframe initialization - recreate iframe if IE and reuse if FF
	if (!this._iframe || AjxEnv.isIE) {
		this._iframe = document.createElement("iframe");
		this._iframe.style.width = this._iframe.style.height = 0;
		this._iframe.style.visibility = "hidden";

		var contentDiv = this._getContentDiv();
		contentDiv.appendChild(this._iframe);
	}
	
	var strPrefs = this._getUserPrefs();
	var formId = Dwt.getNextId();

	// generate html form for submission via POST
	var html = [];
	var idx = 0;
	var subject = this._subjPfx + this._detailStr.substring(0,40);
	var scheme = (location.protocol == 'https:') ? "https:" : "http:";
	html[idx++] = "<html><head></head><body><form id='";
	html[idx++] = formId;
	html[idx++] = "' method='POST' action='";
	html[idx++] = scheme;
	html[idx++] = ZmErrorDialog.REPORT_URL;
	html[idx++] = "'>";
	html[idx++] = "<textarea name='details'>";
	html[idx++] = this._detailStr;
	html[idx++] = "version - ";
	html[idx++] = appCtxt.get(ZmSetting.CLIENT_VERSION);
	html[idx++] = "\n";
	html[idx++] = "release - ";
	html[idx++] = appCtxt.get(ZmSetting.CLIENT_RELEASE);
	html[idx++] = "\n";
	html[idx++] = "date - ";
	html[idx++] = appCtxt.get(ZmSetting.CLIENT_DATETIME);
	html[idx++] = "</textarea><textarea name='navigator'>";
	html[idx++] = this._strNav;
	html[idx++] = "</textarea><textarea name='prefs'>";
	html[idx++] = strPrefs;
	html[idx++] = "</textarea><textarea name='subject'>";
	html[idx++] = subject;
	html[idx++] = "</textarea></form></body></html>";

	var idoc = Dwt.getIframeDoc(this._iframe);
	idoc.open();
	idoc.write(html.join(""));
	idoc.close();

	// submit the form!
	var form = idoc.getElementById(formId);
	if (form) {
		form.submit();
	}

	this.popdown();
};

// Displays the detail text
ZmErrorDialog.prototype._showDetail = 
function() {
	this._detailsVisible = !this._detailsVisible;

	var msg = this._msgStr;
	if (this._detailsVisible) {
		msg += "<hr> " + this._detailStr.substr(0,300);
		if (this._detailStr.length > 300) msg += "...";
	}
	DwtMessageDialog.prototype.setMessage.call(this, msg, this._msgStyle, this._msgTitle);
	this._button[ZmErrorDialog.DETAIL_BUTTON].setText(this._detailsVisible ? this._hideDetailsMsg : this._showDetailsMsg);
};
