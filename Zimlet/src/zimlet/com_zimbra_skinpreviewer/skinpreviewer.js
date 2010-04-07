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
 * Constructor.
 * 
 */
function com_zimbra_skinpreviewer() {
}

com_zimbra_skinpreviewer.prototype = new ZmZimletBase();
com_zimbra_skinpreviewer.prototype.constructor = com_zimbra_skinpreviewer;


com_zimbra_skinpreviewer.prototype._loadAvailableSkins =
function() {
	var soapDoc = AjxSoapDoc.create("GetAvailableSkinsRequest", "urn:zimbraAccount");
	var respCallback = new AjxCallback(this, this._handleResponseLoadAvailableSkins);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
};

com_zimbra_skinpreviewer.prototype._handleResponseLoadAvailableSkins =
function(response) {
	this._availableSkins = [];
	var resp = response.getResponse().GetAvailableSkinsResponse;
	var skins = resp.skin;
	if (skins && skins.length) {
		for (var i = 0; i < skins.length; i++) {
			this._availableSkins.push(skins[i].name);
		}
	}
	this.showPrefDialog();
};

com_zimbra_skinpreviewer.prototype.doubleClicked = function() {
	this.singleClicked();
};


com_zimbra_skinpreviewer.prototype.singleClicked = function() {
	this._loadAvailableSkins();

};

com_zimbra_skinpreviewer.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();


	var previewButtonId = Dwt.getNextId();
	var previewButton = new DwtDialog_ButtonDescriptor(previewButtonId, ("Preview Selected Skins"), DwtDialog.ALIGN_RIGHT);
	this.pbDialog = this._createDialog({title:"'Skin Previewer' Zimlet Preferences", view:this.pView, standardButtons:[ DwtDialog.CANCEL_BUTTON], extraButtons:[previewButton]});
	this.pbDialog.setButtonListener(previewButtonId, new AjxListener(this, this._previewSelectedSkins));
	this.pbDialog.popup();

};

com_zimbra_skinpreviewer.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV align='center' class='skinpreviewer_cardDetailDiv'>";
	html[i++] = "Preview and Compare multiple Skins simultaneously";
	html[i++] = "</DIV>";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "Please Select skins to preview. Use Ctrl+click or Shift+click to select multiple skins.";
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<select id=\"skinprev_skinmenu\" multiple='' size=10>";
	var len = this._availableSkins.length;
	for (var j = 0; j < len; j++) {
		var itm = this._availableSkins[j];
		html[i++] = "<option value=" + itm + ">" + itm + "</option>";
	}
	html[i++] = "</select>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	return html.join("");

};

com_zimbra_skinpreviewer.prototype._previewSelectedSkins =
function() {
	var url = AjxUtil.formatUrl({});
	var ch = "";
	if (url.indexOf("?") > 0) {
		ch = "&skin=";
	} else {
		ch = "?skin=";
	}

	var skinNamesArry = this._getSelectedSkins();
	if (skinNamesArry.length == 0)
		return;

	var len = skinNamesArry.length;
	for (var i = 0; i < len; i++) {
		var newurl = url + ch + skinNamesArry[i];
		setTimeout(AjxCallback.simpleClosure(this._openAWindow, this, newurl), i * 5000);
	}

};

com_zimbra_skinpreviewer.prototype._openAWindow =
function(url) {
	window.open(url);
};


com_zimbra_skinpreviewer.prototype._getSelectedSkins =
function() {
	var me = document.getElementById("skinprev_skinmenu");
	var selectedSkins = [];
	for (var i = 0; i < me.options.length; i++) {
		if (me.options[i].selected) {
			selectedSkins.push(me.options[i].value);
		}
	}
	return selectedSkins;
};