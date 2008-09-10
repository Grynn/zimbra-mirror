/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
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

DictionaryPrefsDialog = function(shell, className, parent) {
	className = className || "DictionaryPrefsDialog";
	this._zimlet = parent;
	var title = "Default Dictionary";
	DwtDialog.call(this, {parent:shell, className:className, title:title});
	this.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._prefSelected));
	this._createSearchHtml();
};

DictionaryPrefsDialog.prototype = new DwtDialog;
DictionaryPrefsDialog.prototype.constructor = DictionaryPrefsDialog;

DictionaryPrefsDialog.prototype._createSearchHtml = function() {

	this._dictSelect = new DwtSelect({parent:this});

	for (i = 0; i < this._zimlet._dictionaries.length; i++) {
		var option = this._zimlet._dictionaries[i];
		this._dictSelect.addOption(option.label, option.value == Com_Zimbra_Dictionary.prototype._dictDatabase, option.value);
	}

	var table = document.createElement("TABLE");
	table.border = 0;
	table.cellPadding = 0;
	table.cellSpacing = 4;

	row = table.insertRow(-1);
	cell = row.insertCell(-1);
	cell.innerHTML = "Dictionary Search Results from <a target=\"_blank\" href=\""+Com_Zimbra_Dictionary.prototype._dictionaryServerTop+"\">"+Com_Zimbra_Dictionary.prototype._dictionaryServerTop+"</a><br/><br/>Set Default Dictionary:";
	cell.appendChild(this._dictSelect.getHtmlElement());

	var element = this._getContentDiv();
	element.appendChild(table);
};

DictionaryPrefsDialog.prototype.popup = function(name, callback) {
	
	this.setTitle("Dictionary Preferences");
	this._dictSelect.setSelected(Com_Zimbra_Dictionary.prototype._dictDatabase);
	
	// enable buttons
	this.setButtonEnabled(DwtDialog.OK_BUTTON, true);
	this.setButtonEnabled(DwtDialog.CANCEL_BUTTON, true);
	
	// show
	DwtDialog.prototype.popup.call(this);
};

DictionaryPrefsDialog.prototype.popdown = 
function() {
	ZmDialog.prototype.popdown.call(this);
};

DictionaryPrefsDialog.prototype._prefSelected =
function(){
	this._zimlet.setUserProperty("dict", this._dictSelect.getValue(), true);
	Com_Zimbra_Dictionary.prototype._dictDatabase = this._dictSelect.getValue();
	this.popdown();
};

