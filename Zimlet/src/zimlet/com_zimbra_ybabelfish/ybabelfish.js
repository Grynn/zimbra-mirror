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
	if(zmObject.length > 1) {
        if (!this._inputDialog) {
            this._inputDialog = new DwtDialog(appCtxt.getShell(),null,this.getMessage("dropOnlyOneMessage"),[DwtDialog.OK_BUTTON]);
             this._inputDialog.popup();
            return;
        }
        else {
            this._inputDialog.popup();
            return;
        }
    }
    this._zmObject = zmObject;
	this._isUserInput = false;

	// create a dialog if one does not already exist
	if (!this._yBabelfishDialog) {
		this._initialize();
	}
	
	// set language according to prefs
	if (this._lang != this.getUserProperty("trans_language")) {
		this._resetDefaultLang();
	}

	// reset widgets
	this._contentDIV.innerHTML = AjxStringUtil.nl2br(this._zmObject.body);
	this._contentTA.style.visibility = "hidden";
	this._contentDIV.style.visibility = "visible";

	this._populated = false;

	// reset widgets so user can read translated text
	if (this._defaultLang) {
		this._langSelect.setSelected(this._defaultLang);
	} else {
		this._langSelect.setSelected(15);
	}

	this._yBabelfishDialog.popup();
};

Com_Zimbra_Ybabelfish.prototype.doubleClicked =
function(canvas) {
	this._isUserInput = true;

	// create a dialog if one does not already exist
	if (!this._yBabelfishDialog) {
		this._initialize();
	}
	
	// set language according to prefs
	if (this._lang != this.getUserProperty("trans_language")) {
		this._resetDefaultLang();
	}

	// reset widgets
	this._contentTA.value = "";
	this._contentDIV.style.visibility = "hidden";
	this._contentTA.style.visibility = "visible";
	this._contentTA.focus();

	this._populated = false;

	// reset widgets so user can read translated text
	if (this._defaultLang) {
		this._langSelect.setSelected(this._defaultLang);
	} else {
		this._langSelect.setSelected(15);
	}

	this._yBabelfishDialog.popup();
};

Com_Zimbra_Ybabelfish.prototype.menuItemSelected = 
function(itemId) {
	switch (itemId) {
		case "prefs":
			this._showPrefs();
			break;
	}
}

Com_Zimbra_Ybabelfish.prototype._showPrefs =
function() {
	if(!this._prefsDialog) {
		this._prefsDialog = new YBabelfishPrefsDialog(appCtxt._shell, null, this);
	}
	this._prefsDialog.popup();
};


Com_Zimbra_Ybabelfish.prototype._makeRequest =
function(lang, text) {
    text = text.replace(/&/g,'&amp;');
    var encodedText = AjxStringUtil.urlEncode(text);
    encodedText = encodedText.replace(/&amp;/g,'%26');
    encodedText = encodedText.replace(/#/g,'%23');
    var reqHeader = { "User-Agent": navigator.userAgent, "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8", "Referer": Com_Zimbra_Ybabelfish.URL, "Host": "babelfish.yahoo.com" };

	AjxRpc.invoke(null, this.getResource("ybabelfish.jsp")+"?text="+encodedText+"&lang="+AjxStringUtil.urlEncode(lang || "en_es")+"&userAgent="+AjxStringUtil.urlEncode(navigator.userAgent), reqHeader, new AjxCallback(this, this._resultCallback), true);
};

Com_Zimbra_Ybabelfish.prototype._initialize =
function() {
	this._parentView = new DwtComposite(this.getShell());
	this._parentView.setSize("440", "175");

	// add header section which will hold language DwtSelect
	var selectId = Dwt.getNextId();

	var div = document.createElement("div");
	var html = [];
	var i = 0;
	html[i++] = "<table border=0 width=100%><tr>";
	html[i++] = "<td width=100% id='";
	html[i++] = selectId;
	html[i++] = "'></td></tr></table>";
	div.innerHTML = html.join("");
	this._parentView.getHtmlElement().appendChild(div);

	// add DwtSelect holding language options
	this._langSelect = new DwtSelect({parent:this._parentView});
	this._langSelect.reparentHtmlElement(selectId);

	this._resetDefaultLang();

	for (i = 0; i < this._languages.length; i++) {
		var option = this._languages[i];
		this._langSelect.addOption(option.label, option.value == this._lang, option.value);
	}

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

	// add translate and close DwtButton
	var translateId = Dwt.getNextId();
	var mailId = Dwt.getNextId();
	var closeId = Dwt.getNextId();

	this._translateButton = new DwtDialog_ButtonDescriptor(translateId, this.getMessage("translateButton"), DwtDialog.ALIGN_RIGHT);
	this._mailButton = new DwtDialog_ButtonDescriptor(mailId, this.getMessage("mailButton"), DwtDialog.ALIGN_RIGHT);
	this._closeButton = new DwtDialog_ButtonDescriptor(closeId, this.getMessage("closeButton"), DwtDialog.ALIGN_RIGHT);

	// finally, create dialog holding all these widgets
	this._yBabelfishDialog = this._createDialog({title:this.getMessage("title"), view:this._parentView, standardButtons:[], extraButtons:[this._translateButton, this._mailButton, this._closeButton]});
	
	this._yBabelfishDialog.setButtonListener(translateId, new AjxListener(this, this._translateListener));
	this._yBabelfishDialog.setButtonListener(mailId, new AjxListener(this, this._sendListener));
	this._yBabelfishDialog.setButtonListener(closeId, new AjxListener(this, this._yBabelfishDialogOkListener));
};

Com_Zimbra_Ybabelfish.prototype._resetDefaultLang = 
function() {
	this._lang = this.getUserProperty("trans_language");
	var localeSetting = appCtxt.get(ZmSetting.LOCALE_NAME)?appCtxt.get(ZmSetting.LOCALE_NAME):"en";

	if (this._lang == "default") {
		if (localeSetting.substr(0,2) == "en") {
			this._lang = "en_es";
		} else {
			this._lang = "en_" + localeSetting.substr(0,2);
		}
	}
	for (i = 0; i < this._languages.length; i++) {
		if (this._languages[i].value == this._lang) {
			this._defaultLang = i;
		}
	}
}

Com_Zimbra_Ybabelfish.prototype._populate =
function(resp) {
	var result = resp.success ? resp.text : null;
	var divIdx = result ? result.indexOf("<div style=\"padding:0.6em;\"") : null;
	this._originalDIV = divIdx ? Dwt.parseHtmlFragment(result.substring(divIdx)) : null;

	if (this._isUserInput) {
		this._contentTA.style.visibility = "hidden";
		this._contentDIV.style.visibility = "visible";
	}

	this._populated = true;

	this._contentDIV.innerHTML = this._originalDIV
		? AjxStringUtil.nl2br(this._originalDIV.innerHTML)
		: "An error occurred when attempting to translate this message.";
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

Com_Zimbra_Ybabelfish.prototype._sendListener =
function(ev) {

	if (this._populated) {
		var body = this._originalDIV.innerHTML;
	} else {
		return;
	}

	if (appCtxt.get(ZmSetting.HTML_COMPOSE_ENABLED) &&
		appCtxt.get(ZmSetting.COMPOSE_AS_FORMAT) == ZmSetting.COMPOSE_HTML)
	{
		body = AjxStringUtil.nl2br(body);
	}

	var params = {
		action: ZmOperation.NEW_MESSAGE,
		extraBodyText: body
	};

	this._yBabelfishDialog.popdown();

	var cc = AjxDispatcher.run("GetComposeController");
	cc.doAction(params);
};


// Callbacks

Com_Zimbra_Ybabelfish.prototype._resultCallback =
function(obj) {
	this._populate(obj);

	if (!this._yBabelfishDialog.isPoppedUp())
		this._yBabelfishDialog.popup();
};
