/*
 * Copyright (C) 2006, The Apache Software Foundation.
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


/**
 * This class represents a combo box.
 *
 * @param parent    {DwtComposite} Parent widget (required)
 * @param className {string} CSS class. If not provided defaults to "DwtHorizontalSlider" or "DwtVerticalSlider" (optional)
 * @param positionType {string} Positioning style (absolute, static, or relative). If
 *         not provided defaults to DwtComposite.STATIC_STYLE (optional)
 */
function DwtComboBox(parent, inputParams, className, positionType) {
    if (arguments.length == 0) return;

    className = className || "DwtComboBox";
    DwtComposite.call(this, parent, className, positionType);
    
    this._input = null;
    this._button = null;
    
    this._textToValue = {}; // Map of text strings to their values.

	this._hasMenuCallback = true;
	this._menuItemListenerObj = new AjxListener(this, this._menuItemListener);

    this._createHtml(inputParams);
};

DwtComboBox.prototype = new DwtComposite;
DwtComboBox.prototype.constructor = DwtComboBox;

DwtComboBox.HORIZONTAL = 1;
DwtComboBox.VERTICAL = 2;

DwtComboBox.prototype.toString =
function() {
    return "DwtComboBox";
};

/**
 * Adds an entry to the combo box.
 * 
 * @param text		the user-visible text for the entry
 * @param value		the value for the entry
 * @param selected	if true, this entry is selected
 */
DwtComboBox.prototype.add =
function(text, value, selected) {
	this._textToValue[text] = value;
	if (!this._hasMenuCallback) {
		var menu = this._button.getMenu();
    	this._createMenuItem(menu, text);
	}
	if (selected) {
		this.setText(text);
	}
};

/**
 * Returns the value of the currently selected entry. If the entry
 * is one that was not added via the add method (that is, if it was
 * typed in by the user) then null is returned.
 */
DwtComboBox.prototype.getValue =
function() {
	var text = this.getText();
	if (this._textToValue.hasOwnProperty(text)) {
		return this._textToValue[text];
	} else {
		return null;
	}
};

/**
 * Returns the text of the currently selected entry.
 */
DwtComboBox.prototype.getText =
function() {
	return this._input.getValue();
};

/**
 * Sets the selected text.
 */
DwtComboBox.prototype.setText =
function(text) {
	this._input.setValue(text);
};

DwtComboBox.prototype._createMenu =
function() {
    var menu = new DwtMenu(this);
    for (var i in this._textToValue) {
    	this._createMenuItem(menu, i);
    }
	this._hasMenuCallback = false;
	return menu;
};

DwtComboBox.prototype._createMenuItem =
function(menu, text) {
	var item = new DwtMenuItem(menu);
	item.setText(text);
	item.addSelectionListener(this._menuItemListenerObj);
	if (!this._menuWidth) {
		this._menuWidth = this.getW() - 10; // 10 is some fudge factor that lines up the menu right.
	}
    item.getHtmlElement().style.minWidth = this._menuWidth;
};

DwtComboBox.prototype._menuItemListener =
function(ev) {
	var menuItem = ev.dwtObj;
	this._input.setValue(menuItem.getText());
	var input = this._input.getInputElement();
	input.focus();
	input.select();
};

DwtComboBox.prototype._createHtml =
function(inputParams) {
    var element = this.getHtmlElement();
    var args = { id:this._htmlElId };
    element.innerHTML = AjxTemplate.expand("ajax.dwt.templates.Widgets#DwtComboBox", args);

	inputParams = inputParams || {};
	inputParams.parent = this;
	inputParams.size = inputParams.size || 40;
    this._input = new DwtInputField(inputParams);
    this._input.getInputElement().style.border = "none";
    this._input.replaceElement(args.id + "_input");
    
    this._button = new DwtComboBoxButton(this);
	this._button.setMenu(new AjxListener(this, this._createMenu), true);
    this._button.replaceElement(args.id + "_button");
};

/////////////////////////////////////////////////////////////////////////////////////
// DwtComboBoxButton: Stylizable button just for use in combo boxes.
/////////////////////////////////////////////////////////////////////////////////////
function DwtComboBoxButton(parent, className) {
	DwtButton.call(this, parent, null, className, Dwt.STATIC_STYLE);
}

DwtComboBoxButton.prototype = new DwtButton;
DwtComboBoxButton.prototype.constructor = DwtComboBoxButton;

DwtComboBoxButton.prototype.toString =
function() {
    return "DwtComboBoxButton";
};

DwtComboBoxButton.prototype.TEMPLATE = "ajax.dwt.templates.Widgets#DwtComboBoxButton"

