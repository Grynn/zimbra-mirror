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
 */

//////////////////////////////////////////////////////////////////////////////
// Zimlet to translate a message
// @author Zimlet author: Parag Shah.
//////////////////////////////////////////////////////////////////////////////

function Com_Zimbra_Gtranslator() {
}

Com_Zimbra_Gtranslator.prototype = new ZmZimletBase();
Com_Zimbra_Gtranslator.prototype.constructor = Com_Zimbra_Gtranslator;


// Consts

Com_Zimbra_Gtranslator.URL = "http://translate.google.com/translate_t";


// Public methods

// This method is called when an item is dropped on the Zimlet item as realized
// in the UI. At this point the Zimlet should perform the actions it needs to
// for the drop. This method defines the following formal parameters:
//
// - zmObject
// - canvas
Com_Zimbra_Gtranslator.prototype.doDrop =
function(zmObject) {
	this._zmObject = zmObject;
	this._isUserInput = false;

	// create a dialog if one does not already exist
	if (!this._gTranslatorDialog) {
		this._initialize();
	}

	// reset widgets
	this._contentDIV.innerHTML = AjxStringUtil.nl2br(this._zmObject.body);
	this._contentTA.style.visibility = "hidden";
	this._contentDIV.style.visibility = "visible";

	// reset widgets so user can read translated text
	this._langSelect.setSelected(0);

	this._gTranslatorDialog.popup();
};

Com_Zimbra_Gtranslator.prototype.doubleClicked =
function(canvas) {
	this._isUserInput = true;

	// create a dialog if one does not already exist
	if (!this._gTranslatorDialog) {
		this._initialize();
	}

	// reset widgets
	this._contentTA.value = "";
	this._contentDIV.style.visibility = "hidden";
	this._contentTA.style.visibility = "visible";
	this._contentTA.focus();

	this._gTranslatorDialog.popup();
};


// Private methods

Com_Zimbra_Gtranslator.prototype._makeRequest =
function(lang, text) {
	var reqParams = [];
	var i = 0;

	// params for google translator
	reqParams[i++] = "text=";
	reqParams[i++] = AjxStringUtil.urlEncode(text);
	reqParams[i++] = "&langpair=";
	reqParams[i++] = AjxStringUtil.urlEncode(lang || "en|de");
	reqParams[i++] = "&hl=en&ie=UTF8";

	var reqHeader = { "User-Agent": navigator.userAgent, "Content-Type": "application/x-www-form-urlencoded", "Referrer": "http://translate.google.com/translate_t" };
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(Com_Zimbra_Gtranslator.URL);

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._resultCallback));
};

Com_Zimbra_Gtranslator.prototype._initialize =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("440", "175");

	this._languages = [
		{ value: "en|de",		label: "English to German" },
		{ value: "en|es", 		label: "English to Spanish" },
		{ value: "en|fr", 		label: "English to French" },
		{ value: "en|it",		label: "English to Italian" },
		{ value: "en|pt",		label: "English to Portuguese" },
		{ value: "en|ja",		label: "English to Japanese BETA" },
		{ value: "en|ko",		label: "English to Korean BETA" },
		{ value: "en|zh-CN",	label: "English to Chinese (Simplified) BETA" },
		{ value: "de|en",		label: "German to English" },
		{ value: "de|fr",		label: "German to French" },
		{ value: "es|en",		label: "Spanish to English" },
		{ value: "fr|en",		label: "French to English" },
		{ value: "fr|de",		label: "French to German" },
		{ value: "it|en",		label: "Italian to English" },
		{ value: "pt|en",		label: "Portuguese to English" },
		{ value: "ja|en",		label: "Japanese to English BETA" },
		{ value: "ko|en",		label: "Korean to English BETA" },
		{ value: "zh-CN|en",	label: "Chinese (Simplified) to English BETA" }
	];

	// add header section which will hold language DwtSelect and Translate button
	var selectId = Dwt.getNextId();
	var translateId = Dwt.getNextId();

	var div = document.createElement("div");
	var html = [];
	var i = 0;
	html[i++] = "<table border=0 width=100%><tr>";
	html[i++] = "<td width=100% id='";
	html[i++] = selectId;
	html[i++] = "'></td><td id='";
	html[i++] = translateId;
	html[i++] = "'></td></tr></table>";
	div.innerHTML = html.join("");
	this._parentView.getHtmlElement().appendChild(div);

	// add DwtSelect holding language options
	this._langSelect = new DwtSelect({parent:this._parentView});
	this._langSelect.reparentHtmlElement(selectId);
	for (i = 0; i < this._languages.length; i++) {
		var option = this._languages[i];
		this._langSelect.addOption(option.label, i==0, option.value);
	}

	// add translate DwtButton
	this._translateButton = new DwtButton({parent:this._parentView});
	this._translateButton.reparentHtmlElement(translateId);
	this._translateButton.setText("Translate");
	this._translateButton.addSelectionListener(new AjxListener(this, this._translateListener));

	// add textarea holding content
	this._contentTA = document.createElement("TEXTAREA");
	this._contentTA.style.height = "140px";
	this._contentTA.style.width = "435px";
	this._contentTA.style.padding = "3px";
	this._contentTA.style.position = "absolute";
	this._parentView.getHtmlElement().appendChild(this._contentTA);

	// add DIV holding content
	this._contentDIV = document.createElement("DIV");
	this._contentDIV.style.height = "140px";
	this._contentDIV.style.width = "435px";
	this._contentDIV.style.backgroundColor = "#FFFFFF";
	this._contentDIV.style.padding = "3px";
	this._contentDIV.style.position = "absolute";
	this._contentDIV.style.overflow = "auto";
	this._parentView.getHtmlElement().appendChild(this._contentDIV);

	// finally, create dialog holding all these widgets
	this._gTranslatorDialog = this._createDialog({title:"Google Translator", view:this._parentView});
	this._gTranslatorDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._gTranslatorDialogOkListener));
};

Com_Zimbra_Gtranslator.prototype._populate =
function(resp) {
	var result = resp.success ? resp.text : null;
	var divIdx = result ? result.indexOf("<div id=result_box") : null;
	var div = divIdx ? Dwt.parseHtmlFragment(result.substring(divIdx)) : null;

	if (this._isUserInput) {
		this._contentTA.style.visibility = "hidden";
		this._contentDIV.style.visibility = "visible";
	}

	this._contentDIV.innerHTML = div
		? div.innerHTML
		: "An error occurred attempting to translate this message.";
};


// Listeners

Com_Zimbra_Gtranslator.prototype._gTranslatorDialogOkListener =
function(ev) {
	this._gTranslatorDialog.popdown();
};

Com_Zimbra_Gtranslator.prototype._translateListener =
function(ev) {
	var value = this._isUserInput ? this._contentTA.value : this._zmObject.body;
	this._makeRequest(this._langSelect.getValue(), value);
};


// Callbacks

Com_Zimbra_Gtranslator.prototype._resultCallback =
function(obj) {
	this._populate(obj);

	if (!this._gTranslatorDialog.isPoppedUp())
		this._gTranslatorDialog.popup();
};
