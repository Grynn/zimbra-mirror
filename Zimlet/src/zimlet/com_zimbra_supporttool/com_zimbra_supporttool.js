/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
 * Constructor.
 *
 * @author Raja Rao DV (rrao@zimbra.com)
 */
function com_zimbra_supporttool_HandlerObject() {
}

com_zimbra_supporttool_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_supporttool_HandlerObject.prototype.constructor = com_zimbra_supporttool_HandlerObject;

/**
 * Simplify handler object
 *
 */
var SupportToolZimlet = com_zimbra_supporttool_HandlerObject;


/**
 * Initializes zimlet
 */
SupportToolZimlet.prototype.init =
function() {
	this._currentVersion = appCtxt.getSettings().getInfoResponse.version;
	this._saveVersions();
};

/**
 * Saves versions
 */
SupportToolZimlet.prototype._saveVersions =
function() {
	this.setUserProperty("supporttool_currentVersion", this._currentVersion, true);
	var pv = this.getUserProperty("supporttool_previousVersions");
	if (pv != "") {
		if (pv.indexOf(this._currentVersion) == -1) {//if current version isnt present, then add it
			pv = this._currentVersion + "::" + pv;
			pv = this._get5PrevVersions(pv);
			this.setUserProperty("supporttool_previousVersions", pv, true);
		}
		this._prevVersions = pv.split("::");

	} else if (pv == "") {
		pv = this._currentVersion;
		this.setUserProperty("supporttool_previousVersions", this._currentVersion, true);
		this._prevVersions = [this._currentVersion];
	}
	this._createVersionNameValueArray();
};

/**
 * Gets 5 latest Zimbra Versions
 * @param {string} pv Zimbra versions separated by ::
 */
SupportToolZimlet.prototype._get5PrevVersions =
function(pv) {
	var arry = pv.split("::");
	var versions = new Array();
	for (var i = 0; i < arry.length && i < 5; i++) {
		versions.push(arry[i]);
	}
	return versions.join("::");
};

/**
 * creates  a hash of current and past Zimbra versions
 */
SupportToolZimlet.prototype._createVersionNameValueArray =
function() {
	var prevVersionCounter = 1;
	this._versionNameValArray = new Array();
	for (var k = 0; k < this._prevVersions.length; k++) {
		if (k == 0) {
			this._versionNameValArray["zimbraCurrentVersion"] = this._prevVersions[k];
		} else {
			this._versionNameValArray["zimbraPreviousVersion" + prevVersionCounter] = this._prevVersions[k];
			prevVersionCounter++;
		}
	}
};

/**
 * A listener
 */
SupportToolZimlet.prototype._exportAsHtmlListener =
function() {
	this._expWindow = window.open(this.getResource("exportWindow.html"));
	setTimeout(AjxCallback.simpleClosure(this._postToExportWindow, this), 1500);
};

/**
 * Posts Account information to a html-file
 */
SupportToolZimlet.prototype._postToExportWindow = function() {
	this._expWindow.document.getElementById('supporttool_exportPrefDiv').innerHTML = this._prefDetailsHTML;
};

/**
 * Pastes Account information to Email-compose-window
 */
SupportToolZimlet.prototype._sendEmailWithPrefInfo = function() {
	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	var toOverride = null;

	var subjOverride = this.getMessage("SupportToolZimlet_emailSubject");
	var extraBodyText = this._constructEmailBdy();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg,
		toOverride: toOverride, subjOverride: subjOverride,
		extraBodyText: extraBodyText});
	if (this._preferenceDialog) {
		this._preferenceDialog.popdown();
	}
};

/**
 * Constructs Html body
 * @returns {string} html
 */
SupportToolZimlet.prototype._constructEmailBdy =
function() {
	var newLine = "";
	if (appCtxt.getSettings().getSetting("COMPOSE_AS_FORMAT").value == "text") {
		newLine = "\r\n";
	} else {
		newLine = "<BR/>";
	}

	var html = new Array();
	var i = 0;
	html.push(newLine, newLine);
	html.push("-----------------------------------------------------", newLine);
	html.push("  -- ", this.getMessage("SupportToolZimlet_versionAndBrowser"), " --                  ", newLine);
	html.push("-----------------------------------------------------",newLine);
	for (var el in this._versionNameValArray) {
		html.push(el, "=", this._versionNameValArray[el], newLine);
	}
	html.push("browserUserAgent=",navigator.userAgent, newLine);
	html.push("---------------------",this.getMessage("SupportToolZimlet_end"),"----------------------------", newLine);
	html.push(newLine, newLine, newLine);
	html.push("-----------------------------------------------------", newLine);
	html.push("          -- ",this.getMessage("SupportToolZimlet_prefDetails")," --                  " , newLine);
	html.push("-----------------------------------------------------" , newLine);
	for (var j = 0; j < this.settingArry.length; j++) {
		var setting = this.settingArry[j];
		if (setting.name == undefined || setting.name == null)
			continue;
		html.push(setting.name, "=", setting.value , newLine);
	}
	html.push("---------------------",this.getMessage("SupportToolZimlet_end"),"----------------------------" , newLine);
	return html.join("");
};

/**
 * Creates preferences view
 */
SupportToolZimlet.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	this._bDayAndEmail = new Array();
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = ["<TD align='center'>",this.getMessage("SupportToolZimlet_versionHistory"),"</TD>"].join("");
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  align=\"center\" style=\"overflow:auto;width:99%;height:40px;\" >";
	var prevVersionCounter = 1;
	for (var k = 0; k < this._prevVersions.length; k++) {
		html[i++] = "<DIV class='supporttool_cardListDiv'>";
		html[i++] = "<TABLE width='100%'>";
		html[i++] = "<TR>";
		var vname = this.getMessage("SupportToolZimlet_currentVersion");
		if (k > 0) {
			vname = this.getMessage("SupportToolZimlet_previousVersion")+ prevVersionCounter + ":";
			prevVersionCounter++;
		}
		html[i++] = "<TD width='30%'><B>" + vname + "</B></TD><TD width='70%'>" + this._prevVersions[k] + "</TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</DIV>";
	}
	html[i++] = "</DIV>";

	html[i++] = "<BR>";
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = ["<TD align='center'>",this.getMessage("SupportToolZimlet_browserAndOS"),"</TD>"].join("");
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  align=\"center\" style=\"overflow:auto;width:99%;height:43px;\" >";
	html[i++] = "<DIV class='supporttool_cardListDiv'>";
	html[i++] = "<TABLE width='100%'>";
	html[i++] = "<TR>";
	html[i++] = ["<TD width='20%'><B>",this.getMessage("SupportToolZimlet_userAgent"),"</B></TD><TD width='80%'>", navigator.userAgent, "</TD>"].join("");
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "</DIV>";

	html[i++] = "<BR>";

	html[i++] = "<DIV class='supporttool_cardHdrDivTop' style=\"overflow:auto;width:99%; height:20px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = ["<TD align='center'>",this.getMessage("SupportToolZimlet_zimbraPrefs"),"</TD>"].join("");
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' style=\"overflow:auto;width:99%;height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = ["<TD   width='40%'>",this.getMessage("SupportToolZimlet_prefName"),"</TD><TD align='center' width='60%'>",this.getMessage("SupportToolZimlet_currentName"),"</TD>"].join("");
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";

	html[i++] = "<DIV  style=\"overflow:auto;height:240px;width:99%\">";
	this.settingArry = new Array();
	var settings = appCtxt.getSettings()._settings;
	var m = 0;
	for (var el in settings) {
		this.settingArry[m] = eval("settings." + el);
		m++;
	}

	for (var j = 0; j < this.settingArry.length; j++) {
		var setting = this.settingArry[j];
		if (setting.name == undefined)
			continue;

		html[i++] = "<DIV class='supporttool_cardListDiv' >";
		html[i++] = "<TABLE width='100%'>";
		html[i++] = "<TR>";
		var aVal = setting.value;
		html[i++] = "<TD width='45%'>" + setting.name + "</TD><TD align='center' width='50%'>" + aVal + "</TD>";
		html[i++] = "</TR>";
		html[i++] = "</TABLE>";
		html[i++] = "</DIV>";
	}
	html[i++] = "</DIV>";

	this._prefDetailsHTML = html.join("");
	this._prefDetailsHTML = this._prefDetailsHTML.replace("height:20px;", "").replace("height:22px;", "");
	this._prefDetailsHTML = this._prefDetailsHTML.replace("height:240px;", "").replace("height:40px;", "").replace("height:43px;", "");
	return html.join("");

};

/**
 * Shows preferences dialog when user double-clicks on panel item
 */
SupportToolZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Shows preferences dialog when user single-clicks on panel item
 */
SupportToolZimlet.prototype.singleClicked = function() {
	this._showPreferenceDlg();
};

/**
 *Creates and displays Preferences dialog
 */
SupportToolZimlet.prototype._showPreferenceDlg = function() {
	//if zimlet dialog already exists...
	if (this._preferenceDialog) {
		this._preferenceDialog.popup();
		return;
	}
	this._preferenceView = new DwtComposite(this.getShell());
	this._preferenceView.setSize("500", "500");
	this._preferenceView.getHtmlElement().style.overflow = "auto";

	this._preferenceView.getHtmlElement().innerHTML = this._createPrefView();
	var reminderBtnId = Dwt.getNextId();
	var sendPrefEmailBtnId = Dwt.getNextId();
	var exportAsHtmlBtnId = Dwt.getNextId();
	var sendPrefEmailBtn = new DwtDialog_ButtonDescriptor(sendPrefEmailBtnId, this.getMessage("SupportToolZimlet_sendEmail"), DwtDialog.ALIGN_LEFT);
	var exportAsHtmlBtn = new DwtDialog_ButtonDescriptor(exportAsHtmlBtnId, this.getMessage("SupportToolZimlet_exportAsHtml"), DwtDialog.ALIGN_LEFT);
	this._preferenceDialog = new ZmDialog({parent:this.getShell(),title: this.getMessage("SupportToolZimlet_prefDlgHdr"), view:this._preferenceView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[sendPrefEmailBtn, exportAsHtmlBtn]});
	this._preferenceDialog.setButtonListener(sendPrefEmailBtnId, new AjxListener(this, this._sendEmailWithPrefInfo));
	this._preferenceDialog.setButtonListener(exportAsHtmlBtnId, new AjxListener(this, this._exportAsHtmlListener));
	this._formatDlg(this._preferenceView);
	this._preferenceDialog.popup();
};

/**
 * Formats dialog
 * @param {object} view Preferences view
 */
SupportToolZimlet.prototype._formatDlg = function(view) {
	var el = view.getHtmlElement();
	while (el != undefined && el != null && el.className != "DwtDialog WindowOuterContainer") {
		el = el.parentNode;
	}
	if (el.className == "DwtDialog WindowOuterContainer") {
		el.style.padding = "0px";
	}
};