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
 *@Author Raja Rao DV
 */


function com_zimbra_supporttool() {
}

com_zimbra_supporttool.prototype = new ZmZimletBase();
com_zimbra_supporttool.prototype.constructor = com_zimbra_supporttool;


com_zimbra_supporttool.prototype.init =
function() {
	this._currentVersion =  appCtxt.getSettings().getInfoResponse.version;
	this._saveVersions();
};

com_zimbra_supporttool.prototype._saveVersions =
function() {
	this.setUserProperty("supporttool_currentVersion", this._currentVersion, true);
	var pv = this.getUserProperty("supporttool_previousVersions");
	if (pv != "") {
		if (pv.indexOf(this._currentVersion) == -1) {//if current version isnt present, then add it
			pv = this._currentVersion + "::" + pv;
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


//depricated - we now use appCtxt.getSettings().getInfoResponse.version in .init to get the version. 
//but, if that breaks, call this method instead
/*
com_zimbra_supporttool.prototype._getVersion =
function() {
	var soapDoc = AjxSoapDoc.create("GetInfoRequest", "urn:zimbraAccount");
	var method = soapDoc.getMethod();
	method.setAttribute("sections", "mbox");
	var command = new ZmCsfeCommand();
	var resp = command.invoke({soapDoc: soapDoc});
	return resp.Body.GetInfoResponse.version;

};
*/

com_zimbra_supporttool.prototype.sortByType =
function(a, b) {
	var x = a.type;
	var y = b.type;
	return ((y < x) ? -1 : ((y > x) ? 1 : 0));
};

com_zimbra_supporttool.prototype._createCompareView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD>Paste Preferences of an account and press 'Compare' button</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV style=\"overflow:auto;width:99%;height:180px;\" >";
	html[i++] = "<textarea rows='300' cols='50' id='supporttool_accntPref2TextArea'></textarea>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD>COMPARISION RESULTS:</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV id='supporttool_compareResults' >";
	html[i++] = "</DIV>";
	html[i++] = "<DIV id='supporttool_mismatchCntDiv'>";
	html[i++] = "</DIV>";

	return html.join("");
};
com_zimbra_supporttool.prototype._createVersionNameValueArray =
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

com_zimbra_supporttool.prototype._compareBtnListener =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' style=\"overflow:auto;width:99%;height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD   width='40%'>PREFERENCE NAME</TD><TD align='center' width='30%'>OTHER ACCOUNT</TD><TD align='center' width='30%'>THIS ACCOUNT</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  style=\"overflow:auto;height:180px;width:99%\">";
	var val = document.getElementById("supporttool_accntPref2TextArea").value;
	var props = val.split("\n");
	var mismatchCnt = 0;
	for (var j = 0; j < props.length; j++) {

		try {
			var err = false;
			try {

				var line = props[j];
				if (line == "")//skip
					continue;

				var nv = line.split("=");

				var name = AjxStringUtil.trim(nv[0]);
				var otherVal = AjxStringUtil.trim(nv[1]);
			} catch(e) {//any expn during parsing..
				err = true;
			}

			var currentVal = this._getValue(name);
			currentVal = AjxStringUtil.trim("" + currentVal + "");

			html[i++] = "<DIV class='supporttool_cardListDiv' >";
			html[i++] = "<TABLE width='100%'>";
			if (err) {
				html[i++] = "<TR>";
				html[i++] = "<TD width='40%' class='supporttool_errTd'>ERROR READING THIS LINE: </TD><TD COLSPAN='2' class='supporttool_errTd'>" + line + "</TD>";
				html[i++] = "</TR>";
				mismatchCnt++;
			} else if (otherVal == currentVal) {
				html[i++] = "<TR>";
				html[i++] = "<TD width='40%'>" + name + "</TD><TD align='center' width='30%'>" + otherVal + "</TD><TD align='center' width='30%'>" + currentVal + "</TD>";
				html[i++] = "</TR>";
			} else {
				html[i++] = "<TR  class='supporttool_mismatchRow'>";
				html[i++] = "<TD width='40%'>" + name + "</TD><TD align='center' width='30%'>" + otherVal + "</TD><TD align='center' width='30%'>" + currentVal + "</TD>";
				html[i++] = "</TR>";
				mismatchCnt++;
			}
			html[i++] = "</TABLE>";
			html[i++] = "</DIV>";
		} catch(e) {
			html[i++] = "<DIV class='supporttool_cardListDiv' >";
			html[i++] = "<TABLE width='100%'>";
			html[i++] = "<TR>";
			html[i++] = "<TD width='40%' class='supporttool_errTd'>ERROR READING THIS LINE: </TD><TD COLSPAN='2' class='supporttool_errTd'>" + line + "</TD>";
			html[i++] = "</TR>";
			html[i++] = "</TABLE>";
			html[i++] = "</DIV>";
			mismatchCnt++;
		}
	}
	html[i++] = "</DIV>";
	document.getElementById("supporttool_compareResults").innerHTML = html.join("");
	this._updateMismatchCount(mismatchCnt);
};


com_zimbra_supporttool.prototype._updateMismatchCount =
function(mismatchCnt) {
	var mm = new Array();
	var cnt = 0;
	mm[cnt++] = "<DIV id='supporttool_mismatchCntDiv' class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\">";
	mm[cnt++] = "<TABLE width='100%'>";
	mm[cnt++] = "<TR>";
	if(mismatchCnt==0){
		mm[cnt++] = "<TD><B><font color='green'>NUMBER OF ITEMS MISMATCHED: " + mismatchCnt + "</font></B></TD>";
	}else {
		mm[cnt++] = "<TD><B><font color='red'>NUMBER OF ITEMS MISMATCHED: " + mismatchCnt + "</font></B></TD>";
	}
	mm[cnt++] = "</TR>";
	mm[cnt++] = "</TABLE>";
	mm[cnt++] = "</DIV>";
	document.getElementById("supporttool_mismatchCntDiv").innerHTML = mm.join("");
};

com_zimbra_supporttool.prototype._exportAsHtmlListener =
function() {
	this._expWindow = window.open(this.getResource("exportWindow.html"));
	setTimeout(AjxCallback.simpleClosure(this._postToExportWindow, this), 1500);
};

com_zimbra_supporttool.prototype._postToExportWindow = function() {
	this._expWindow.document.getElementById('supporttool_exportPrefDiv').innerHTML = this._prefDetailsHTML;
};

com_zimbra_supporttool.prototype._sendEmailWithPrefInfo = function() {
	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	var toOverride = null;

	var subjOverride = "Version and Preferences info of my account";
	var extraBodyText = this._constructEmailBdy();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg,
		toOverride: toOverride, subjOverride: subjOverride,
		extraBodyText: extraBodyText});
	if (this._preferenceDialog) {
		this._preferenceDialog.popdown();
	}


	if (this._compareDialog) {
		this._compareDialog.popdown();
	}
};

com_zimbra_supporttool.prototype._constructEmailBdy =
function() {
	var newLine = "";
	if (appCtxt.getSettings().getSetting("COMPOSE_AS_FORMAT").value == "text") {
		newLine = "\r\n";
	} else {
		newLine = "<BR/>";
	}

	var html = new Array();
	var i = 0;
	html[i++] = newLine + newLine;
	html[i++] = "PS: You can drag-drop this email onto Support Tool Zimlet to instantly compare version and preferences with your account." + newLine;
	html[i++] = "-----------------------------------------------------" + newLine;
	html[i++] = "  -- VERSION AND BROWSER DETAILS --                  " + newLine;
	html[i++] = "-----------------------------------------------------" + newLine;
	for (var el in this._versionNameValArray) {
		html[i++] = el + "=" + this._versionNameValArray[el] + newLine;
	}
	html[i++] = "browserUserAgent=" + navigator.userAgent + newLine;
	html[i++] = "---------------------END----------------------------" + newLine;

	html[i++] = newLine + newLine + newLine;
	html[i++] = "-----------------------------------------------------" + newLine;
	html[i++] = "          -- PREFERENCES DETAILS --                  " + newLine;
	html[i++] = "-----------------------------------------------------" + newLine;
	for (var j = 0; j < this.settingArry.length; j++) {
		var setting = this.settingArry[j];
		if (setting.name == undefined || setting.name == null)
			continue;
		html[i++] = setting.name + "=" + setting.value + newLine;
	}
	html[i++] = "---------------------END----------------------------" + newLine;
	return html.join("");
};

com_zimbra_supporttool.prototype._getValue =
function(name) {
	for (var j = 0; j < this.settingArry.length; j++) {
		var setting = this.settingArry[j];
		if (setting.name == name) {
			return setting.value;
		}
	}
	//also check if its a versionname value we are after.
	if (this._versionNameValArray[name]) {
		return this._versionNameValArray[name];
	}

	if (name == "browserUserAgent") {
		return   navigator.userAgent;
	}

	return "PREFERENCE_NOT_FOUND";
};

com_zimbra_supporttool.prototype._createPrefView =
function() {
	var html = new Array();
	var i = 0;
	this._bDayAndEmail = new Array();
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' align=\"center\" style=\"overflow:auto;width:99%; height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD align='center'>ZIMBRA VERSION HISTORY</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  align=\"center\" style=\"overflow:auto;width:99%;height:40px;\" >";
	var prevVersionCounter = 1;
	for (var k = 0; k < this._prevVersions.length; k++) {
		html[i++] = "<DIV class='supporttool_cardListDiv'>";
		html[i++] = "<TABLE width='100%'>";
		html[i++] = "<TR>";
		var vname = "Current Version:";
		if (k > 0) {
			vname = "Previous Version" + prevVersionCounter + ":";
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
	html[i++] = "<TD align='center'>BROWSER AND OPERATING SYSTEM</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV  align=\"center\" style=\"overflow:auto;width:99%;height:43px;\" >";
	html[i++] = "<DIV class='supporttool_cardListDiv'>";
	html[i++] = "<TABLE width='100%'>";
	html[i++] = "<TR>";
	html[i++] = "<TD width='20%'><B>User Agent:</B></TD><TD width='80%'>" + navigator.userAgent + "</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "</DIV>";

	html[i++] = "<BR>";

	html[i++] = "<DIV class='supporttool_cardHdrDivTop' style=\"overflow:auto;width:99%; height:20px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD align='center'>ZIMBRA PREFERENCES</TD>";
	html[i++] = "</TR>";
	html[i++] = "</TABLE>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV class='supporttool_cardHdrDivTB' style=\"overflow:auto;width:99%;height:22px;\" >";
	html[i++] = "<TABLE width='100%'  style=\"font-weight:bold;\">";
	html[i++] = "<TR>";
	html[i++] = "<TD   width='40%'>PREFERENCE NAME</TD><TD align='center' width='60%'>CURRENT VALUE</TD>";
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
	//this.settingArry = this.settingArry.sort(this.sortByType);

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

com_zimbra_supporttool.prototype.doDrop =
function(msg) {
	this._propsFromMail = new Array();
	var body = msg.body;
	var lines = body.split("\r\n");
	for (var i = 0; i < lines.length; i++) {
		var line = lines[i];
		if (line.indexOf("zimbra") == 0 || line.indexOf("browser") == 0 && line.indexOf("=") > 0) {
			this._propsFromMail.push(line);
		}
	}

	this.singleClicked();
	this._showCompareDlg();
	document.getElementById("supporttool_accntPref2TextArea").value = this._propsFromMail.join("\n");
	this._compareBtnListener();

};

com_zimbra_supporttool.prototype.doubleClicked = function() {
	this.singleClicked();
};
com_zimbra_supporttool.prototype.singleClicked = function() {
	this._showPreferenceDlg();
};


com_zimbra_supporttool.prototype._showPreferenceDlg = function() {
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

	var createRemindersBtn = new DwtDialog_ButtonDescriptor(reminderBtnId, ("Compare Preferences Tool"), DwtDialog.ALIGN_LEFT);
	var sendPrefEmailBtn = new DwtDialog_ButtonDescriptor(sendPrefEmailBtnId, ("Send Email"), DwtDialog.ALIGN_LEFT);
	var exportAsHtmlBtn = new DwtDialog_ButtonDescriptor(exportAsHtmlBtnId, ("Export As HTML"), DwtDialog.ALIGN_LEFT);


	this._preferenceDialog = this._createDialog({title:"Support Tool Zimlet: View, compare or  email Account Preferences", view:this._preferenceView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[sendPrefEmailBtn, exportAsHtmlBtn, createRemindersBtn]});
	this._preferenceDialog.setButtonListener(reminderBtnId, new AjxListener(this, this._showCompareDlg));
	this._preferenceDialog.setButtonListener(sendPrefEmailBtnId, new AjxListener(this, this._sendEmailWithPrefInfo));
	this._preferenceDialog.setButtonListener(exportAsHtmlBtnId, new AjxListener(this, this._exportAsHtmlListener));


	this._formatDlg(this._preferenceView);

	this._preferenceDialog.popup();
};

com_zimbra_supporttool.prototype._formatDlg = function(view) {
	//this._preferenceView.getHtmlElement().parentNode.style.background = "white";//can be set AFTER dlg is created
	var el = view.getHtmlElement();

	while (el != undefined && el != null && el.className != "DwtDialog WindowOuterContainer") {
		el = el.parentNode;
	}
	if (el.className == "DwtDialog WindowOuterContainer")
		el.style.padding = "0px";
};

com_zimbra_supporttool.prototype._showCompareDlg = function() {
	//if zimlet dialog already exists...
	if (this._compareDialog) {
		this._compareDialog.popup();
		return;
	}
	this._compareView = new DwtComposite(this.getShell());
	this._compareView.setSize("500", "500");
	this._compareView.getHtmlElement().style.overflow = "auto";
	this._compareView.getHtmlElement().innerHTML = this._createCompareView();
	var createCompareBtnId = Dwt.getNextId();
	var createCompareBtn = new DwtDialog_ButtonDescriptor(createCompareBtnId, ("Compare"), DwtDialog.ALIGN_LEFT);

	this._compareDialog = this._createDialog({title:"Compare Preferences of your account with a different account", view:this._compareView, standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[createCompareBtn]});
	this._compareDialog.setButtonListener(createCompareBtnId, new AjxListener(this, this._compareBtnListener));

	this._formatDlg(this._compareView);
	this._compareDialog.popup();
};