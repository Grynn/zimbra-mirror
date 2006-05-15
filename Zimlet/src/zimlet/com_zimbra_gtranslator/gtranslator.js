/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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

	// reset widgets so user can read translated text
	this._langSelect.setSelected(0);
	this._translateButton.setVisible(false);
	this._contentTA.style.backgroundColor = "#EEEEEE";

	this._makeRequest(null, this._zmObject.body);
};

Com_Zimbra_Gtranslator.prototype.doubleClicked =
function(canvas) {
	this._isUserInput = true;

	// create a dialog if one does not already exist
	if (!this._gTranslatorDialog) {
		this._initialize();
	}

	// reset widgets so user can enter text
	this._translateButton.setVisible(true);
	this._contentTA.readOnly = false;
	this._contentTA.value = "";
	this._contentTA.style.backgroundColor = "#FFFFFF";

	this._gTranslatorDialog.popup();
};


// Private methods

Com_Zimbra_Gtranslator.prototype._makeRequest = 
function(lang, text) {
	var reqParams = new Array();
	var i = 0;

	// params for google translator
	reqParams[i++] = "text=";
	reqParams[i++] = AjxStringUtil.urlEncode(text);
	reqParams[i++] = "&langpair=";
	reqParams[i++] = AjxStringUtil.urlEncode(lang || "en|de");	// default to "English to German"
	reqParams[i++] = "&hl=en";
	reqParams[i++] = "&ie=UTF8";

	var reqHeader = { "User-Agent": navigator.userAgent, "Content-Type": "application/x-www-form-urlencoded", "Referrer": "http://translate.google.com/translate_t" };
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(Com_Zimbra_Gtranslator.URL);

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._resultCallback));
};

Com_Zimbra_Gtranslator.prototype._initialize = 
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("440", "175");
	this._parentView.getHtmlElement().style.overflow = "auto";

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
	var html = new Array();
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
	this._langSelect = new DwtSelect(this._parentView);
	this._langSelect.reparentHtmlElement(selectId);
	this._langSelect.addChangeListener(new AjxListener(this, this._langChangeListener));

	for (i = 0; i < this._languages.length; i++) {
		var option = this._languages[i];
		this._langSelect.addOption(option.label, i==0, option.value);
	}

	// add translate DwtButton
	this._translateButton = new DwtButton(this._parentView);
	this._translateButton.reparentHtmlElement(translateId);
	this._translateButton.setText("Translate");
	this._translateButton.addSelectionListener(new AjxListener(this, this._translateListener));

	// add textarea holding content
	this._contentTA = document.createElement("TEXTAREA");
	this._contentTA.style.height = "140px";
	this._contentTA.style.border = "0px";
	this._contentTA.style.color = "#000000";
	this._contentTA.style.padding = "3px";
	this._parentView.getHtmlElement().appendChild(this._contentTA);

	// finally, create dialog holding all these widgets
	this._gTranslatorDialog = this._createDialog({title:"Google Translator", view:this._parentView});
	this._gTranslatorDialog._disableFFhack();
	this._gTranslatorDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._gTranslatorDialogOkListener));
};

Com_Zimbra_Gtranslator.prototype._populate = 
function(resp) {
	var result = resp.success ? resp.text : null;
	var taIdx = result ? result.indexOf("<textarea") : null;
	var ta = taIdx ? Dwt.parseHtmlFragment(result.substring(taIdx)) : null;

	this._contentTA.readOnly = false;
	this._contentTA.value = ta
		? ta.value
		: "An error occurred attempting to translate this message.";
	this._contentTA.readOnly = true;
};


// Listeners

Com_Zimbra_Gtranslator.prototype._gTranslatorDialogOkListener = 
function(ev) {
	this._gTranslatorDialog.popdown();
};

Com_Zimbra_Gtranslator.prototype._langChangeListener = 
function(ev) {
	if (this._isUserInput)
		return;

	this._makeRequest(ev._args.newValue, this._zmObject.body);
};

Com_Zimbra_Gtranslator.prototype._translateListener = 
function(ev) {
	this._makeRequest(this._langSelect.getValue(), this._contentTA.value, true);
};


// Callbacks

Com_Zimbra_Gtranslator.prototype._resultCallback = 
function(obj) {
	this._populate(obj);

	if (!this._gTranslatorDialog.isPoppedUp())
		this._gTranslatorDialog.popup();
};
