/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////////////////////
// This zimlet checks for x-mailer which contains name of the email-client that was used to
//send the email and displays that information when the mail is opened
// @author Zimlet author: Raja Rao DV(rrao@zimbra.com)
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_skinchanger() {
}

com_zimbra_skinchanger.prototype = new ZmZimletBase();
com_zimbra_skinchanger.prototype.constructor = com_zimbra_skinchanger;

com_zimbra_skinchanger.prototype.init =
function() {
	this.turnONSkinChangerZimletNew = this.getUserProperty("turnONSkinChangerZimletNew") == "true";
	if(!this.turnONSkinChangerZimletNew)
		return;

	this.skinc_selectedFreq = this.getUserProperty("skinc_selectedFreq");
	this.skinc_skinWasChangedOnDate = this.getUserProperty("skinc_skinWasChangedOnDate");


	var weekdays = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
	this._day = weekdays[new Date().getDay()];
	if (this.skinc_skinWasChangedOnDate == this._formatChangedOnDate())//change only once a day
		return;

	if (this.skinc_selectedFreq != "Everyday" && this.skinc_selectedFreq != this._day)
		return;


	this._loadAvailableSkins(new AjxCallback(this, this._handleResponseLoadAvailableSkins));

};

com_zimbra_skinchanger.prototype._loadAvailableSkins =
function(callback) {
	var soapDoc = AjxSoapDoc.create("GetAvailableSkinsRequest", "urn:zimbraAccount");
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:callback});
};

com_zimbra_skinchanger.prototype._handleResponseLoadAvailableSkins =
function(response) {
	this._availableSkins = [];
	var resp = response.getResponse().GetAvailableSkinsResponse;
	var skins = resp.skin;
	if (skins && skins.length) {
		for (var i = 0; i < skins.length; i++) {
			this._availableSkins.push(skins[i].name);
		}
	}

	this._updateSkin();
};

com_zimbra_skinchanger.prototype._updateSkin =
function() {
	var randomnumber = Math.floor(Math.random() * this._availableSkins.length);
	var soapDoc = AjxSoapDoc.create("ModifyPrefsRequest", "urn:zimbraAccount");
	var node = soapDoc.set("pref", this._availableSkins[randomnumber]);
	node.setAttribute("name", "zimbraPrefSkin");

	var respCallback = new AjxCallback(this, this._handleSkinChangeResponse);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
};

com_zimbra_skinchanger.prototype._formatChangedOnDate =
function() {
	var dArry = (new Date()).toString().split(" ");
	return dArry[0] + " " + dArry[1] + " " + dArry[2] + " " + dArry[3];

};
com_zimbra_skinchanger.prototype._handleSkinChangeResponse =
function() {
	this.setUserProperty("skinc_skinWasChangedOnDate", this._formatChangedOnDate(), true);
};


com_zimbra_skinchanger.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_skinchanger.prototype.singleClicked = function() {
	this.showPrefDialog();
};

com_zimbra_skinchanger.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	if (this.getUserProperty("turnONSkinChangerZimletNew") == "true") {
		document.getElementById("turnONSkinChangerZimletNew_chkbx").checked = true;
	}

	this.pbDialog = this._createDialog({title:"'Skin Changer' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._updateSelectedItem();
	this.pbDialog.popup();

};

com_zimbra_skinchanger.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "Please Select the frequency or day on which you want the skin to be changed";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<select id='skinc_availSkinList'>";
	var days = [ "Friday", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Everyday"];
	var len = days.length;
	for (var j = 0; j < len; j++) {
		html[i++] = "<option value=" + days[j] + ">" + days[j] + "</option>";
	}
	html[i++] = "</select>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<BR>";
	if (this.skinc_skinWasChangedOnDate != "") {
		html[i++] = "<DIV>";
		html[i++] = "Note: Skin was last changed on: " + this.skinc_skinWasChangedOnDate;
		html[i++] = "</DIV>";
	}
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnONSkinChangerZimletNew_chkbx'  type='checkbox'/>Enable 'Skin Changer' Zimlet (Changing this would refresh browser)";
	html[i++] = "</DIV>";
	return html.join("");

};

com_zimbra_skinchanger.prototype._updateSelectedItem =
function() {
	var optn = document.getElementById("skinc_availSkinList").options;
	for (var i = 0; i < optn.length; i++) {
		if (optn[i].value == this.skinc_selectedFreq) {
			optn[i].selected = true;
			break;
		}
	}
};

com_zimbra_skinchanger.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnONSkinChangerZimletNew_chkbx").checked) {
		if (!this.turnONSkinChangerZimletNew) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONSkinChangerZimletNew", "true", true);
	} else {
		this.setUserProperty("turnONSkinChangerZimletNew", "false", true);
		if (this.turnONSkinChangerZimletNew)
			this._reloadRequired = true;
	}
	var lst = document.getElementById("skinc_availSkinList");
	if (lst.value != this.skinc_selectedFreq) {
		this.setUserProperty("skinc_selectedFreq", lst.value, true);
		this._reloadRequired = true;
	}

	this.pbDialog.popdown();

	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		setTimeout(AjxCallback.simpleClosure(this._refreshBrowser, this), 2000);
	}
};

com_zimbra_skinchanger.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};