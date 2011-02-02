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
 * Search wikipedia.
 * 
 * @author Kevin Henrikson
 */
function com_zimbra_wikipedia_HandlerObject() {
}

com_zimbra_wikipedia_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_wikipedia_HandlerObject.prototype.constructor = com_zimbra_wikipedia_HandlerObject;

/**
 * Simplify handler object
 *
 */
var WikipediaZimlet = com_zimbra_wikipedia_HandlerObject;


/**
 * Called by the Zimbra framework when the panel item was double clicked.
 */
WikipediaZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by the Zimbra framework when the panel item was clicked.
 */
WikipediaZimlet.prototype.singleClicked = function() {
	var editorProps = [
		{ label 		 : this.getMessage("WikipediaZimlet_label_search"),
		  name           : "search",
		  type           : "string",
		  value          : "",
		  minLength      : 4,
		  maxLength      : 100
		}
		];
	if (!this._dlg_propertyEditor) {
		var view = new DwtComposite(this.getShell());
		this._propertyEditor = new DwtPropertyEditor(view, true);
		var pe = this._propertyEditor;
		pe.initProperties(editorProps);
		var dialog_args = {
			title : this.getMessage("WikipediaZimlet_dialog_title"),
			view  : view
		};
		this._dlg_propertyEditor = this._createDialog(dialog_args);
		var dlg = this._dlg_propertyEditor;
		pe.setFixedLabelWidth();
		pe.setFixedFieldWidth();
		dlg.setButtonListener(DwtDialog.OK_BUTTON,
				      new AjxListener(this, function() {
				          if (!pe.validateData()) {return;}
					      this._doSearch();
				      }));
	}
	this._dlg_propertyEditor.popup();
};

/**
 * Perform search.
 */
WikipediaZimlet.prototype._doSearch =
function() {
	this._dlg_propertyEditor.popdown();
	this._displaySearchResult(this._propertyEditor.getProperties().search);
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;
};

/**
 * Display search results.
 */
WikipediaZimlet.prototype._displaySearchResult = 
function(search) {
    var url = "http://www.wikipedia.org/search-redirect.php?language="+this._getLanguageCode()+"&go=Go&search=" + AjxStringUtil.urlEncode(search);
	this.openCenteredWindow(url);
};

/**
 * Display search results.
 */
WikipediaZimlet.prototype._getLanguageCode = 
function() {
	var locale = appCtxt.getSettings().getSetting("LOCALE_NAME").value;
	if(!locale) {
		return "en";
	}
	locale = locale.toLowerCase();
	var retLocale = "en";
	switch(locale) {
		case "en_au":
		case "en_uk":
		case "en_us":
			retLocale = "en";
			break;
		case "ja":
		case "nl":
		case "de":
		case "fr":
		case "it":
		case "pl":
		case "pt":
		case "ru":
		case "es":
			retLocale = locale;
			break;
		case "pt_br":
			retLocale = "pt";
			break;
	}
	return retLocale;
};

/**
 * Shows warning message (used for popup blocker)
 */
WikipediaZimlet.prototype._showWarningMsg = function(message) {
	var style = DwtMessageDialog.WARNING_STYLE;
	var dialog = appCtxt.getMsgDialog();
	this.warningDialog = dialog;
	dialog.setMessage(message, style);
	dialog.popup();
};

/**
 * Opens new wikipedia browser at the center of the monitor
 */
WikipediaZimlet.prototype.openCenteredWindow =
function (url) {
	var width = 800;
	var height = 600;
	var left = parseInt((screen.availWidth / 2) - (width / 2));
	var top = parseInt((screen.availHeight / 2) - (height / 2));
	var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,scrollbars,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
	var win = window.open(url, "subWind", windowFeatures);
	if (!win) {
		this._showWarningMsg(ZmMsg.popupBlocker);
	}
	return win;
};