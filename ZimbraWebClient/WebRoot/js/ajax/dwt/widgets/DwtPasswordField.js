/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
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

DwtPasswordField = function(params) {
	if (arguments.length == 0) return;

	params = params || { parent: DwtShell.getShell(window) };
	params.type = DwtInputField.PASSWORD; 
	DwtInputField.call(this, params);

	this._tabGroup = new DwtTabGroup(this._htmlElId);

	// TODO: templatize DwtInputField -- then we don't need to explicitly call _createHtml
	this._createHtml();
};
DwtPasswordField.prototype = new DwtInputField;
DwtPasswordField.prototype.constructor = DwtPasswordField;

//
// Data
//

DwtPasswordField.prototype.TEMPLATE = "dwt.Widgets#DwtPasswordField";

//
// Public methods
//

DwtPasswordField.prototype.getTabGroupMember = function() {
	return this._tabGroup;
};

//
// Protected methods
//

DwtPasswordField.prototype._createHtml = function(templateId) {
	var data = { id: this._htmlElId };
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtPasswordField.prototype._createHtmlFromTemplate =
function(templateId, data) {
	this._tabGroup.removeAllMembers();

	// save old contents
	var fragment = document.createDocumentFragment();
	var child = this.getHtmlElement().firstChild;
	while (child) {
		var sibling = child.nextSibling;
		fragment.appendChild(child);
		child = sibling;
	};

	// create HTML and append content
	DwtInputField.prototype._createHtmlFromTemplate.apply(this, arguments);
	var inputEl = document.getElementById(data.id+"_input");
	inputEl.appendChild(fragment);
	this._tabGroup.addMember(this.getInputElement());

	var showCheckboxEl = document.getElementById(data.id+"_show_password");
	if (showCheckboxEl) {
		this._showCheckbox = new DwtCheckbox({parent:this});
		this._showCheckbox.setText(AjxMsg.showPassword);
		this._showCheckbox.addSelectionListener(new AjxListener(this, this._handleShowCheckbox));
		this._showCheckbox.replaceElement(showCheckboxEl);
		this._tabGroup.addMember(this._showCheckbox);
	}
};

DwtPasswordField.prototype._handleShowCheckbox = function(event) {
	var checked = event.detail;
	this.setInputType(checked ? DwtInputField.STRING : DwtInputField.PASSWORD);
};