/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a password field.
 * @constructor
 * @class
 * 
 * @param	{hash}		params		a hash of parameters
 * @param {DwtComposite}      params.parent			the parent widget
 * @param {string}      params.initialValue		the initial value of the field
 * @param {number}      params.size				size of the input field (in characters)
 * @param {number}      params.rows				the number of rows (more than 1 means textarea)
 * @param {boolean}      params.forceMultiRow		if <code>true</code>, forces use of textarea even if rows == 1
 * @param {number}      params.maxLen			the maximum length (in characters) of the input
 * @param {constant}      params.errorIconStyle		the error icon style
 * @param {constant}      params.validationStyle	the validation type
 * @param  {function}     params.validator			the custom validation function
 * @param {Object}      params.validatorCtxtObj		the object context for validation function
 * @param {string}      params.className			the CSS class
 * @param {constant}      params.posStyle			the positioning style (see {@link DwtControl})
 * @param {boolean}      params.required          if <code>true</code>, mark as required.
 * @param {string}      params.hint				a hint to display in the input field when the value is empty.
 * @param {string}      params.id				an explicit ID to use for the control's DIV element
 * @param {string}      params.inputId			an explicit ID to use for the control's INPUT element
 * 
 * @extends		DwtInputField
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

/**
 * Shows the password.
 * 
 * @param	{boolean}	show		if <code>true</code>, show the password
 */
DwtPasswordField.prototype.setShowPassword = function(show) {
	this._showCheckbox.setSelected(show);
	this.setInputType(show ? DwtInputField.STRING : DwtInputField.PASSWORD);
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
	this.setShowPassword(event.detail);
};

/**
* Overrides DwtInputField getValue to not do the leading/trailing spaces trimming.
*
* @return {string} the value
*/
DwtPasswordField.prototype.getValue =
function() {
	return this._inputField.value;
};

