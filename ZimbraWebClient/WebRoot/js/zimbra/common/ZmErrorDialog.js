/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
 * @overview
 * This file defines the Zimbra error dialog.
 *
 */

/**
 * Creates an error dialog.
 * @class
 * Creates an error dialog which will have a "Send Error Report" button.
 * A normal {@link DwtMessageDialog} with a "Send Error Report" button that will post user info to the 
 * server when clicked.
 * 
 * @param	{Object}	parent		the parent
 * @param	{Hash}		msgs		a hash of messages
 * @param	{String}	msgs.showDetails		the show details message
 * @param	{String}	msgs.hideDetails		the hide details message
 * 
 * @extends DwtMessageDialog
 */
ZmErrorDialog = function(parent, msgs) {

	// go ahead and cache the navigator and subject info now (since it should never change)		
	this._strNav = this._getNavigatorInfo();
	this._subjPfx = this._getSubjectPrefix();

	var reportButton = new DwtDialog_ButtonDescriptor(ZmErrorDialog.REPORT_BUTTON, msgs.report, DwtDialog.ALIGN_LEFT);
	var detailButton = new DwtDialog_ButtonDescriptor(ZmErrorDialog.DETAIL_BUTTON, msgs.showDetails, DwtDialog.ALIGN_LEFT);
	DwtMessageDialog.call(this, {parent:parent, extraButtons:[reportButton, detailButton], id:"ErrorDialog"});

	this.registerCallback(ZmErrorDialog.REPORT_BUTTON, this._reportCallback, this);
	this.registerCallback(ZmErrorDialog.DETAIL_BUTTON, this.showDetail, this);
	
	this._showDetailsMsg = msgs.showDetails;
	this._hideDetailsMsg = msgs.hideDetails;

	this._setAllowSelection();
};

ZmErrorDialog.prototype = new DwtMessageDialog;
ZmErrorDialog.prototype.constructor = ZmErrorDialog;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmErrorDialog.prototype.toString =
function() {
	return "ZmErrorDialog";
};

//
// Consts
//

ZmErrorDialog.REPORT_BUTTON = "Report";
ZmErrorDialog.DETAIL_BUTTON = "Detail";
ZmErrorDialog.DEFAULT_REPORT_URL = "//www.zimbra.com/e/";

//
// Data
//

ZmErrorDialog.prototype._detailsVisible = false;
ZmErrorDialog.prototype.CONTROLS_TEMPLATE = "zimbra.Widgets#ZmErrorDialogControls";

//
// Public methods
//

/**
 * Resets the dialog.
 * 
 */
ZmErrorDialog.prototype.reset =
function() {
	this.setDetailString();
	DwtMessageDialog.prototype.reset.call(this);
};

/**
* Sets the text to display when the "Show Details" button is pressed.
*
* @param {String}	text	the detail text
*/
ZmErrorDialog.prototype.setDetailString = 
function(text) {
	if (!(this._button[ZmErrorDialog.DETAIL_BUTTON])) { return; }

	this._button[ZmErrorDialog.DETAIL_BUTTON].setVisible(text != null);
	this._detailStr = text;
};

/**
 * Sets the message style (info/warning/critical) and content.
 *
 * @param {String}	msgStr		the message text
 * @param {String}	detailStr	the detail text
 * @param {constant}	style		the style (see {@link DwtMessageDialog} <code>_STYLE</code> constants)
 * @param {String}	title		the dialog box title
 */
ZmErrorDialog.prototype.setMessage =
function(msgStr, detailStr, style, title) {
	this._msgStr = msgStr;
	this.setDetailString(detailStr);
	this._msgStyle = style;
	this._msgTitle = title;

	// clear the 'detailsVisible' flag and reset the title of the 'showDetails' button
	this._detailsVisible = false;
	this._button[ZmErrorDialog.DETAIL_BUTTON].setText(this._showDetailsMsg);
	
	// Set the content, enveloped
	this._updateContent();
};

/**
 * Sets/updates the content
 */
ZmErrorDialog.prototype._updateContent = 
function() {
	var data = {
		message: this._msgStr,
		detail: this._detailStr,
		showDetails: this._detailsVisible
	};
	var html = AjxTemplate.expand("zimbra.Widgets#ZmErrorDialogContent", data);
	this.setSize(Dwt.CLEAR, this._detailsVisible ? "300" : Dwt.CLEAR);
	DwtMessageDialog.prototype.setMessage.call(this, html, this._msgStyle, this._msgTitle);
};

/**
 * Pops-up the error dialog.
 * 
 * @param {Object}	loc				the desired location
 * @param {Boolean}	hideReportButton	if <code>true</code>, do not show "Send Error Report" button
 * 
 */
ZmErrorDialog.prototype.popup =
function(loc, hideReportButton) {
	if (hideReportButton) {
		this.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, false);
	}
	DwtMessageDialog.prototype.popup.call(this, loc);
};

/**
 * Pops-down the dialog.
 * 
 */
ZmErrorDialog.prototype.popdown =
function() {
	DwtMessageDialog.prototype.popdown.call(this);

	// reset dialog
	this.setSize(Dwt.CLEAR, Dwt.CLEAR);
	this.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, true);
};

//
// Protected methods
//
/**
 * @private
 */
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
		if(AjxEnv.isIE && i === "mimeTypes") {continue;}
		strNav[idx++] = i + ": " + navigator[i] + "\n";
	}
	return strNav.join("");
};

/**
 * @private
 */
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

/**
 * @private
 */
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

/**
 * @private
 */
ZmErrorDialog.prototype._reportCallback =
function() {
	this._iframe = document.createElement("iframe");
	this._iframe.style.width = this._iframe.style.height = 0;
	this._iframe.style.visibility = "hidden";

	var contentDiv = this._getContentDiv();
	contentDiv.appendChild(this._iframe);

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
	html[idx++] = appCtxt.get(ZmSetting.ERROR_REPORT_URL) || ZmErrorDialog.DEFAULT_REPORT_URL;
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
		appCtxt.setStatusMsg(ZmMsg.errorReportSent);
	}

	this.popdown();
};

/**
 * Displays the detail text
 */
ZmErrorDialog.prototype.showDetail = 
function() {
	this._detailsVisible = !this._detailsVisible;
	this._updateContent();
	this._button[ZmErrorDialog.DETAIL_BUTTON].setText(this._detailsVisible ? this._hideDetailsMsg : this._showDetailsMsg);
};
