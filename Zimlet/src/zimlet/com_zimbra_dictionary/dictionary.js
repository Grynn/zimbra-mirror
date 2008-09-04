/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

function Com_Zimbra_Dictionary() {
	this._dictionaries = [
		{ value: "*", label: "Any" },
		{ value: "!", label: "First match" },
		{ value: "gcide", label: "The Collaborative International Dictionary of English v.0.48" },
		{ value: "wn", label: "WordNet (r) 2.0" },
		{ value: "moby-thes", label: "Moby Thesaurus II by Grady Ward, 1.0" },
		{ value: "elements", label: "Elements database 20001107" },
		{ value: "vera", label: "Virtual Entity of Relevant Acronyms (Version 1.9, June 2002)" },
		{ value: "jargon", label: "Jargon File (4.3.1, 29 Jun 2001)" },
		{ value: "foldoc", label: "The Free On-line Dictionary of Computing (27 SEP 03)" },
		{ value: "easton", label: "Easton's 1897 Bible Dictionary" },
		{ value: "hitchcock", label: "Hitchcock's Bible Names Dictionary (late 1800's)" },
		{ value: "bouvier", label: "Bouvier's Law Dictionary, Revised 6th Ed (1856)" },
		{ value: "devils", label: "THE DEVIL'S DICTIONARY ((C)1911 Released April 15 1993)" },
		{ value: "world02", label: "CIA World Factbook 2002" },
		{ value: "gazetteer", label: "U.S. Gazetteer (1990)" },
		{ value: "gaz-county", label: "U.S. Gazetteer Counties (2000)" },
		{ value: "gaz-place", label: "U.S. Gazetteer Places (2000)" },
		{ value: "gaz-zip", label: "U.S. Gazetteer Zip Code Tabulation Areas (2000)" },
		{ value: "--exit--", label: "----------------------" },
		{ value: "afr-deu", label: "Africaan-German Freedict dictionary" },
		{ value: "afr-eng", label: "Africaan-English Freedict Dictionary" },
		{ value: "ara-eng", label: "English-Arabic Freedict Dictionary" },
		{ value: "cro-eng", label: "Croatian-English Freedict Dictionary" },
		{ value: "cze-eng", label: "Czech-English Freedict dictionary" },
		{ value: "dan-eng", label: "Danish-English Freedict dictionary" },
		{ value: "deu-eng", label: "German-English Freedict dictionary" },
		{ value: "deu-fra", label: "German-French Freedict dictionary" },
		{ value: "deu-ita", label: "German-Italian Freedict dictionary" },
		{ value: "deu-nld", label: "German-Nederland Freedict dictionary" },
		{ value: "deu-por", label: "German-Portugese Freedict dictionary" },
		{ value: "eng-afr", label: "English-Africaan Freedict Dictionary" },
		{ value: "eng-ara", label: "English-Arabic FreeDict Dictionary" },
		{ value: "eng-cro", label: "English-Croatian Freedict Dictionary" },
		{ value: "eng-cze", label: "English-Czech fdicts/FreeDict Dictionary" },
		{ value: "eng-deu", label: "English-German Freedict dictionary" },
		{ value: "eng-fra", label: "English-French Freedict Dictionary" },
		{ value: "eng-hin", label: "English-Hindi Freedict Dictionary" },
		{ value: "eng-hun", label: "English-Hungarian Freedict Dictionary" },
		{ value: "eng-iri", label: "English-Irish Freedict dictionary" },
		{ value: "eng-ita", label: "English-Italian Freedict dictionary" },
		{ value: "eng-lat", label: "English-Latin Freedict dictionary" },
		{ value: "eng-nld", label: "English-Netherlands Freedict dictionary" },
		{ value: "eng-por", label: "English-Portugese Freedict dictionary" },
		{ value: "eng-rom", label: "English-Romanian FreeDict dictionary" },
		{ value: "eng-rus", label: "English-Russian Freedict dictionary" },
		{ value: "eng-spa", label: "English-Spanish Freedict dictionary" },
		{ value: "eng-swa", label: "English-Swahili xFried/FreeDict Dictionary" },
		{ value: "eng-swe", label: "English-Swedish Freedict dictionary" },
		{ value: "eng-tur", label: "English-Turkish FreeDict Dictionary" },
		{ value: "eng-wel", label: "English-Welsh Freedict dictionary" },
		{ value: "fra-deu", label: "French-German Freedict dictionary" },
		{ value: "fra-eng", label: "French-English Freedict dictionary" },
		{ value: "fra-nld", label: "French-Nederlands Freedict dictionary" },
		{ value: "hin-eng", label: "English-Hindi Freedict Dictionary [reverse index]" },
		{ value: "hun-eng", label: "Hungarian-English FreeDict Dictionary" },
		{ value: "iri-eng", label: "Irish-English Freedict dictionary" },
		{ value: "ita-deu", label: "Italian-German Freedict dictionary" },
		{ value: "jpn-deu", label: "Japanese-German Freedict dictionary" },
		{ value: "kha-deu", label: "Khasi-German FreeDict Dictionary" },
		{ value: "lat-deu", label: "Latin-German Freedict dictionary" },
		{ value: "lat-eng", label: "Latin-English Freedict dictionary" },
		{ value: "nld-deu", label: "Nederlands-German Freedict dictionary" },
		{ value: "nld-eng", label: "Nederlands-English Freedict dictionary" },
		{ value: "nld-fra", label: "Nederlands-French Freedict dictionary" },
		{ value: "por-deu", label: "Portugese-German Freedict dictionary" },
		{ value: "por-eng", label: "Portugese-English Freedict dictionary" },
		{ value: "sco-deu", label: "Scottish-German Freedict dictionary" },
		{ value: "scr-eng", label: "Serbo-Croat-English Freedict dictionary" },
		{ value: "slo-eng", label: "Slovenian-English Freedict dictionary" },
		{ value: "spa-eng", label: "Spanish-English Freedict dictionary" },
		{ value: "swa-eng", label: "Swahili-English xFried/FreeDict Dictionary" },
		{ value: "swe-eng", label: "Swedish-English Freedict dictionary" },
		{ value: "tur-deu", label: "Turkish-German Freedict dictionary" },
		{ value: "tur-eng", label: "Turkish-English Freedict dictionary" },
		{ value: "english", label: "English Monolingual Dictionaries" },
		{ value: "trans", label: "Translating Dictionaries" },
		{ value: "all", label: "All Dictionaries (English-Only and Translating)" },
		{ value: "web1913", label: "Webster's Revised Unabridged Dictionary (1913)" },
		{ value: "world95", label: "The CIA World Factbook (1995)" }
	];
}

Com_Zimbra_Dictionary.prototype = new ZmZimletBase();
Com_Zimbra_Dictionary.prototype.constructor = Com_Zimbra_Dictionary;

Com_Zimbra_Dictionary.prototype.init =
function() {
	var look = this.lookup;
	var keyAction = appCtxt.getAppController().handleKeyAction;
	appCtxt.getAppController().getKeyMapMgr().setMapping("Global", "Ctrl+89", "dictionaryLookup"); //Ctrl-Y
	appCtxt.getAppController().getKeyMapMgr().setMapping("ZmComposeController", "Ctrl+89", "dictionaryLookup");
	appCtxt.getAppController().getKeyMapMgr().reloadMap("Global");
	appCtxt.getAppController().getKeyMapMgr().reloadMap("ZmComposeController");
	appCtxt.getAppController().handleKeyAction = function (actionCode, ev) {
		var continueKeyAction = true;
		if (actionCode == "dictionaryLookup") {
			look();
			continueKeyAction = false;
		}
		if (continueKeyAction) {
			keyAction(actionCode, ev);
		}
	}
	var t = new Date();
	this._time = t.getTime();
	
	Com_Zimbra_Dictionary.prototype._dictDatabase = this.getUserProperty("dict");
};

Com_Zimbra_Dictionary.prototype.lookup = function() {
	//Safari calls this twice, so don't run more than once per second (also to avoid overloading the dictionary server):
	var t = new Date();
	if (t.getTime() - this._time < 1000) {
		return;
	}
	this._time = t.getTime();

	var thisIframe;

	var userSelection;
	var fromIframe = true;
	//appCtxt.getAppViewMgr().getCurrentView().getClassName()
	if (appCtxt.getAppViewMgr().getCurrentView().getClassName() == "ZmComposeView") {
		fromIframe = false;
		var els = document.getElementsByTagName("textarea");
		for (var i = 0; i < els.length; i++) {
			if (els[i].className == "DwtHtmlEditorTextArea") {
				userSelection = (els[i].value).substring(els[i].selectionStart, els[i].selectionEnd);
			}
		}
	} else {
		var els = document.getElementsByTagName("iframe");
		for (var i = 0; i < els.length; i++) {
			if ((els[i].id == "zv__CLV__MSG_body__iframe" || els[i].id == "zv__CV__MSG_body__iframe") && (!userSelection || (userSelection && new String(userSelection.text?userSelection.text:userSelection).length <= 1))) {
				if (els[i].contentWindow.window.getSelection) {
					userSelection = els[i].contentWindow.window.getSelection();
				}
				else if (els[i].contentWindow.document.selection) {
					userSelection = els[i].contentWindow.document.selection.createRange();
				}
				thisIframe = els[i];
			}
		}
	}

	var selectedText = userSelection;
	if (userSelection.text) {
		selectedText = userSelection.text;
	}

	Com_Zimbra_Dictionary.prototype._dictionaryWord = new String(selectedText);
	if (Com_Zimbra_Dictionary.prototype._dictionaryWord == "") {
		return;
	}

	try {
		if (!fromIframe) {
			throw new Exception();
		}
		
		var rangeObject;
		if (userSelection.getRangeAt) {
			rangeObject = userSelection.getRangeAt(0);
		} else { // Safari!
			rangeObject = document.createRange();
			rangeObject.setStart(userSelection.anchorNode,userSelection.anchorOffset);
			rangeObject.setEnd(userSelection.focusNode,userSelection.focusOffset);
		}
		
		var lookupNode = thisIframe.contentWindow.document.createElement("span");
		lookupNode.id = "dictionaryLookup";
		rangeObject.insertNode(lookupNode);
	} catch (e) {
		Com_Zimbra_Dictionary.prototype._createDictionaryDefinitionDialog();
		return;
	}
	
	var obj = thisIframe.contentWindow.document.getElementById("dictionaryLookup");
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		curleft += obj.offsetLeft;
		curtop += obj.offsetTop;
	}
	obj = thisIframe;
	if (obj.offsetParent) {
		do {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
			curleft -= obj.scrollLeft;
			curtop -= obj.scrollTop;
		} while (obj = obj.offsetParent);
	}

	var tooltip = DwtShell.getShell(window).getToolTip();
	tooltip.setContent('<div style="width: 550px; height: 250px;" id="dictionaryTooltip">Loading...</div>');
	tooltip.popup(curleft,curtop);
	Com_Zimbra_Dictionary.prototype._findDictionaryDefinition();

	thisIframe.contentWindow.document.getElementById("dictionaryLookup").parentNode.removeChild(thisIframe.contentWindow.document.getElementById("dictionaryLookup"));
};

Com_Zimbra_Dictionary.prototype._createDictionaryDefinitionDialog = function() {
	var view = new DwtComposite(DwtShell.getShell(window));
	view.getHtmlElement().innerHTML = '<div style="width: 550px; height: 250px;" id="dictionaryDialog">Loading...</div>';

	var dialog_args = {
		title : "Definition of "+Com_Zimbra_Dictionary.prototype._dictionaryWord,
		view  : view,
		parent: DwtShell.getShell(window),
		standardButtons: [DwtDialog.OK_BUTTON]
	};

	var dlg = new ZmDialog(dialog_args);
	dlg.popup();

	Com_Zimbra_Dictionary.prototype._findDictionaryDefinition();

	dlg.popdown = function () {
		document.getElementById("dictionaryDialog").parentNode.removeChild(document.getElementById("dictionaryDialog"));
		DwtDialog.prototype.popdown.call(dlg);
	}

	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			dlg.popdown();
			dlg.dispose();
		}));
};

Com_Zimbra_Dictionary.prototype._findDictionaryDefinition = function() {
	var dictUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("http://www.dict.org/bin/Dict?Database="+Com_Zimbra_Dictionary.prototype._dictDatabase+"&Form=Dict1&Strategy=*&submit=Submit+query&Query="+Com_Zimbra_Dictionary.prototype._dictionaryWord.replace(/\ /g, "+"));
	AjxRpc.invoke(null, dictUrl, { "User-Agent": navigator.userAgent, "Referer": "http://www.dict.org/bin/Dict" }, new AjxCallback(Com_Zimbra_Dictionary.prototype, Com_Zimbra_Dictionary.prototype._updateDictionaryDefinition), true);
};

Com_Zimbra_Dictionary.prototype._updateDictionaryDefinition = function(result) {
	if (!result.success) {
		result = "Error connecting to dictionary server.";
	} else {
		result = result.text;
		var beginIndex = result.indexOf("<hr>");
		var endIndex = result.lastIndexOf("<hr>");

		if (beginIndex && endIndex) {
			result = result.substring(beginIndex, endIndex);
		} else if (beginIndex) {
			result = result.substring(beginIndex);
		}
		result = result.replace(/href="\//g, 'target="_blank" href="http://www.dict.org/');
	}
	
	result = '<div style="width: 550px; height: 250px; overflow: auto;">'+result+'</div>';
	
	var divTooltip = document.getElementById("dictionaryTooltip");
	var divDialog = document.getElementById("dictionaryDialog");
	if (divTooltip) {
		divTooltip.innerHTML = result;
	}
	if (divDialog) {
		divDialog.innerHTML = result;
	}
};

Com_Zimbra_Dictionary.prototype.doubleClicked = function() {
	this._showPrefs();
};

Com_Zimbra_Dictionary.prototype.singleClicked = function() {
	var editorProps = [
		{ label 		 : "Word",
		  name           : "word",
		  type           : "string",
		  value          : "",
		  minLength      : 1,
		  maxLength      : 100
		}
		];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title : "Dictionary Search",
			view  : view
		};
		this._dlg_propertyEditor = this._createDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          //if (!pe.validateData()) {return;}
					      this._doSearch();
				      }));
	}
	this._dlg_propertyEditor.popup();
};

Com_Zimbra_Dictionary.prototype._doSearch =
function() {
	this._dlg_propertyEditor.popdown();
	Com_Zimbra_Dictionary.prototype._dictionaryWord = this._propertyEditor.getProperties().word;
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
	this._createDictionaryDefinitionDialog();
};

Com_Zimbra_Dictionary.prototype.menuItemSelected = 
function(itemId) {
	switch (itemId) {
		case "prefs":
			this._showPrefs();
			break;
	}
}

Com_Zimbra_Dictionary.prototype._showPrefs =
function() {
	if(!this._prefsDialog) {
		this._prefsDialog = new DictionaryPrefsDialog(appCtxt._shell, null, this);
	}
	this._prefsDialog.popup();
};
