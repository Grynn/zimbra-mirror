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
//////////////////////////////////////////////////////////////////////////////

function Com_Zimbra_Ybabelfish() {
}

Com_Zimbra_Ybabelfish.prototype = new ZmZimletBase();
Com_Zimbra_Ybabelfish.prototype.constructor = Com_Zimbra_Ybabelfish;


// Consts

Com_Zimbra_Ybabelfish.URL = "http://babelfish.yahoo.com/translate_txt";


// Public methods

// This method is called when an item is dropped on the Zimlet item as realized
// in the UI. At this point the Zimlet should perform the actions it needs to
// for the drop. This method defines the following formal parameters:
//
// - zmObject
// - canvas
Com_Zimbra_Ybabelfish.prototype.doDrop =
function(zmObject) {
	this._zmObject = zmObject;
	this._isUserInput = false;

	// create a dialog if one does not already exist
	if (!this._yBabelfishDialog) {
		this._initialize();
	}

	// reset widgets
	this._contentDIV.innerHTML = AjxStringUtil.nl2br(this._zmObject.body);
	this._contentTA.style.visibility = "hidden";
	this._contentDIV.style.visibility = "visible";

	// reset widgets so user can read translated text
	this._langSelect.setSelected(15);

	this._yBabelfishDialog.popup();
};

Com_Zimbra_Ybabelfish.prototype.doubleClicked =
function(canvas) {
	this._isUserInput = true;

	// create a dialog if one does not already exist
	if (!this._yBabelfishDialog) {
		this._initialize();
	}

	// reset widgets
	this._contentTA.value = "";
	this._contentDIV.style.visibility = "hidden";
	this._contentTA.style.visibility = "visible";
	this._contentTA.focus();

	this._yBabelfishDialog.popup();
};


// Private methods

Com_Zimbra_Ybabelfish.prototype._makeRequest =
function(lang, text) {
	var reqParams = [];
	var i = 0;

	// params for babelfish
	reqParams[i++] = "ei=UTF8&doit=done&fr=bf-res&intl=1&tt=urltext&trtext=";
	reqParams[i++] = AjxStringUtil.urlEncode(text);
	reqParams[i++] = "&lp=";
	reqParams[i++] = AjxStringUtil.urlEncode(lang || "en_es");
	reqParams[i++] = "&btnTrTxt=Translate";

	var reqHeader = { "User-Agent": navigator.userAgent, "Content-Type": "application/x-www-form-urlencoded", "Referer": Com_Zimbra_Ybabelfish.URL };
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(Com_Zimbra_Ybabelfish.URL);

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._resultCallback));
};

Com_Zimbra_Ybabelfish.prototype._initialize =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("440", "175");

	this._languages = [
		{ value: "zh_en",		label: "Chinese-simp to English" },
		{ value: "zh_zt",		label: "Chinese-simp to Chinese-trad" },
		{ value: "zt_en",		label: "Chinese-trad to English" },
		{ value: "zt_zh",		label: "Chinese-trad to Chinese-simp" },
		{ value: "en_zh",		label: "English to Chinese-simp" },
		{ value: "en_zt",		label: "English to Chinese-trad" },
		{ value: "en_nl",		label: "English to Dutch" },
		{ value: "en_fr",		label: "English to French" },
		{ value: "en_de",		label: "English to German" },
		{ value: "en_el",		label: "English to Greek" },
		{ value: "en_it",		label: "English to Italian" },
		{ value: "en_ja",		label: "English to Japanese" },
		{ value: "en_ko",		label: "English to Korean" },
		{ value: "en_pt",		label: "English to Portuguese" },
		{ value: "en_ru",		label: "English to Russian" },
		{ value: "en_es",		label: "English to Spanish" },
		{ value: "nl_en",		label: "Dutch to English" },
		{ value: "nl_fr",		label: "Dutch to French" },
		{ value: "fr_nl",		label: "French to Dutch" },
		{ value: "fr_en",		label: "French to English" },
		{ value: "fr_de",		label: "French to German" },
		{ value: "fr_el",		label: "French to Greek" },
		{ value: "fr_it",		label: "French to Italian" },
		{ value: "fr_pt",		label: "French to Portuguese" },
		{ value: "fr_es",		label: "French to Spanish" },
		{ value: "de_en",		label: "German to English" },
		{ value: "de_fr",		label: "German to French" },
		{ value: "el_en",		label: "Greek to English" },
		{ value: "el_fr",		label: "Greek to French" },
		{ value: "it_en",		label: "Italian to English" },
		{ value: "it_fr",		label: "Italian to French" },
		{ value: "ja_en",		label: "Japanese to English" },
		{ value: "ko_en",		label: "Korean to English" },
		{ value: "pt_en",		label: "Portuguese to English" },
		{ value: "pt_fr",		label: "Portuguese to French" },
		{ value: "ru_en",		label: "Russian to English" },
		{ value: "es_en",		label: "Spanish to English" },
		{ value: "es_fr",		label: "Spanish to French" }
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
		this._langSelect.addOption(option.label, i==15, option.value);
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
	this._yBabelfishDialog = this._createDialog({title:"Yahoo! Translator: Babel Fish", view:this._parentView});
	this._yBabelfishDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._yBabelfishDialogOkListener));
};

Com_Zimbra_Ybabelfish.prototype._populate =
function(resp) {
	var result = resp.success ? resp.text : null;
	var divIdx = result ? result.indexOf("<div style=\"padding:0.6em;\"") : null;
	var div = divIdx ? Dwt.parseHtmlFragment(result.substring(divIdx)) : null;

	if (this._isUserInput) {
		this._contentTA.style.visibility = "hidden";
		this._contentDIV.style.visibility = "visible";
	}

	this._contentDIV.innerHTML = div
		? AjxStringUtil.nl2br(div.innerHTML)
		: "An error occurred attempting to translate this message.";
};


// Listeners

Com_Zimbra_Ybabelfish.prototype._yBabelfishDialogOkListener =
function(ev) {
	this._yBabelfishDialog.popdown();
};

Com_Zimbra_Ybabelfish.prototype._translateListener =
function(ev) {
	var value = this._isUserInput ? this._contentTA.value : this._zmObject.body;
	this._makeRequest(this._langSelect.getValue(), value);
};


// Callbacks

Com_Zimbra_Ybabelfish.prototype._resultCallback =
function(obj) {
	this._populate(obj);

	if (!this._yBabelfishDialog.isPoppedUp())
		this._yBabelfishDialog.popup();
};
