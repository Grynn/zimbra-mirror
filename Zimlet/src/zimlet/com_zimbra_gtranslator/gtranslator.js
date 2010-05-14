/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Zimlet to translate a message using Google.
 * 
 * @author Parag Shah
 */
function com_zimbra_gtranslator_HandlerObject() {
}

com_zimbra_gtranslator_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_gtranslator_HandlerObject.prototype.constructor = com_zimbra_gtranslator_HandlerObject;

/**
 * Simplify handler object
 *
 */
var GTranslatorZimlet = com_zimbra_gtranslator_HandlerObject;

/**
 * Defines the Google Translator URL.
 */
GTranslatorZimlet.GOOGLE_TRANSLATOR_URL = "http://translate.google.com/translate_t";

/**
 * This method is called when an item is dropped on the Zimlet item as realized
 * in the UI. At this point the Zimlet should perform the actions it needs to
 * for the drop. This method defines the following formal parameters:
 *
 * @param	{ZmConv|ZmMsg}	zmObject		the dropped object
 */
GTranslatorZimlet.prototype.doDrop =
function(zmObject) {

	// create a dialog if one does not already exist
	if (!this._gTranslatorDialog) {
		this._initializeTranslatorDialog();
	}

	var type = zmObject.TYPE;
	var msg = null;
	
	switch (type) {
		case "ZmConv": {
			var conv = zmObject.srcObj; // {ZmConv}
			msg = conv.getFirstHotMsg();
			break;
		}
		case "ZmMailMsg": {
			msg = zmObject.srcObj; // {ZmMailMsg}
			break;
		}
	}

	var body = "";
	if (msg != null)
		body = msg.fragment;
	
	this._zmObjectBody = body;
	this._isUserInput = false;

	// reset widgets
	this._contentDIV.innerHTML = AjxStringUtil.nl2br(body);
	this._contentTA.style.visibility = "hidden";
	this._contentDIV.style.visibility = "visible";

	// reset widgets so user can read translated text
	this._langSelect.setSelected(0);

	this._gTranslatorDialog.popup();
};

/**
 * Called by framework on double-click.
 */
GTranslatorZimlet.prototype.doubleClicked =
function(canvas) {
	this._isUserInput = true;

	// create a dialog if one does not already exist
	if (!this._gTranslatorDialog) {
		this._initializeTranslatorDialog();
	}

	// reset widgets
	this._contentTA.value = "";
	this._contentDIV.style.visibility = "hidden";
	this._contentTA.style.visibility = "visible";
	this._contentTA.focus();

	this._gTranslatorDialog.popup();
};

/**
 * Makes the request to Google.
 * 
 * @param	{string}	lang		the language pair
 * @param	{string}		text	the text to translate
 */
GTranslatorZimlet.prototype._makeRequest =
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
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(GTranslatorZimlet.GOOGLE_TRANSLATOR_URL);

	AjxRpc.invoke(reqParams.join(""), url, reqHeader, new AjxCallback(this, this._resultCallback));
};

/**
 * Initializes the zimlet translator dialog.
 * 
 */
GTranslatorZimlet.prototype._initializeTranslatorDialog =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("440", "175");

	this._languages = [
		{ value: "en|de",		label: this.getMessage("GTranslatorZimlet_language_englishGerman") },
		{ value: "en|es", 		label: this.getMessage("GTranslatorZimlet_language_englishSpanish") },
		{ value: "en|fr", 		label: this.getMessage("GTranslatorZimlet_language_englishFrench") },
		{ value: "en|it",		label: this.getMessage("GTranslatorZimlet_language_englishItalian") },
		{ value: "en|pt",		label: this.getMessage("GTranslatorZimlet_language_englishPortuguese") },
		{ value: "en|ja",		label: this.getMessage("GTranslatorZimlet_language_englishJapanese") },
		{ value: "en|ko",		label: this.getMessage("GTranslatorZimlet_language_englishKorean") },
		{ value: "en|zh-CN",	label: this.getMessage("GTranslatorZimlet_language_englishChineseSimplified") },
		{ value: "de|en",		label: this.getMessage("GTranslatorZimlet_language_germanEnglish") },
		{ value: "de|fr",		label: this.getMessage("GTranslatorZimlet_language_germanFrench") },
		{ value: "es|en",		label: this.getMessage("GTranslatorZimlet_language_spanishEnglish") },
		{ value: "fr|en",		label: this.getMessage("GTranslatorZimlet_language_frenchEnglish") },
		{ value: "fr|de",		label: this.getMessage("GTranslatorZimlet_language_frenchGerman") },
		{ value: "it|en",		label: this.getMessage("GTranslatorZimlet_language_italianEnglish") },
		{ value: "pt|en",		label: this.getMessage("GTranslatorZimlet_language_portugueseEnglish") },
		{ value: "ja|en",		label: this.getMessage("GTranslatorZimlet_language_japaneseEnglish") },
		{ value: "ko|en",		label: this.getMessage("GTranslatorZimlet_language_koreanEnglish") },
		{ value: "zh-CN|en",	label: this.getMessage("GTranslatorZimlet_language_chineseSimplifiedEnglish") }
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
	this._translateButton.setText(this.getMessage("GTranslatorZimlet_dialog_translateButton"));
	this._translateButton.addSelectionListener(new AjxListener(this, this._gTranslatorDialogTranslateListener));

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

	var dialogArgs = {
				title	: this.getMessage("GTranslatorZimlet_dialog_title"),
				view	: this._parentView,
				parent	: this.getShell()
				};
	
	// finally, create dialog holding all these widgets
	this._gTranslatorDialog = new ZmDialog(dialogArgs);
	this._gTranslatorDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._gTranslatorDialogOkListener));
};

/**
 * Populates the results.
 * 
 */
GTranslatorZimlet.prototype._populateResults =
function(resp) {
	
	var result = resp.success ? resp.text : null;
	var divIdx = result ? result.indexOf("<span id=result_box") : null;
	var div = divIdx >= 0 ? Dwt.parseHtmlFragment(result.substring(divIdx)) : null;

	if (this._isUserInput) {
		this._contentTA.style.visibility = "hidden";
		this._contentDIV.style.visibility = "visible";
	}

	var tmpInnerHTML = this.getMessage("GTranslatorZimlet_dialog_errorTranslating");
	
	if (div != null && divIdx >= 0) {
		tmpInnerHTML = div.innerHTML;
	}
	
	this._contentDIV.innerHTML = tmpInnerHTML;
};

/**
 * Handles the OK button.
 * 
 * @see		_initializeTranslatorDialog
 */
GTranslatorZimlet.prototype._gTranslatorDialogOkListener =
function(ev) {
	this._gTranslatorDialog.popdown();
};

/**
 * Handles the translate button.
 * 
 * @see		_initializeTranslatorDialog
 */
GTranslatorZimlet.prototype._gTranslatorDialogTranslateListener =
function(ev) {
	var value = this._isUserInput ? this._contentTA.value : this._zmObjectBody;
	this._makeRequest(this._langSelect.getValue(), value);
};

/**
 * Handles the request callback.
 * 
 * @see			_makeRequest
 */
GTranslatorZimlet.prototype._resultCallback =
function(obj) {
	this._populateResults(obj);

	if (!this._gTranslatorDialog.isPoppedUp())
		this._gTranslatorDialog.popup();
};
