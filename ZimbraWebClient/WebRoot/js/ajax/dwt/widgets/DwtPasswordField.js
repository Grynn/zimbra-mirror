/*
 * Copyright (C) 2007, The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		this._showCheckbox = new DwtCheckbox(this);
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