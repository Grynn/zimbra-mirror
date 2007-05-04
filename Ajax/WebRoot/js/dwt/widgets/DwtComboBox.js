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
function DwtComboBox(parent, inputParams, className, positionType, dialog) {
    if (arguments.length == 0) return;

    className = className || "DwtComboBox";
    DwtComposite.call(this, parent, className, positionType);
    
    this._input = null;
    this._button = null;
    
    this._textToValue = {}; // Map of text strings to their values.
    this._valueToText = {};
    this._valueToItem = {};

    this._dialog = dialog;
    this._hasMenuCallback = true;
	this._menuItemListenerObj = new AjxListener(this, this._menuItemListener);

    this._inputParams = inputParams;
    this._createHtml();
};

DwtComboBox.prototype = new DwtComposite;
DwtComboBox.prototype.constructor = DwtComboBox;

DwtComboBox.prototype.toString =
function() {
    return "DwtComboBox";
};

//
// Data
//

DwtComboBox.prototype.TEMPLATE = "ajax.dwt.templates.Widgets#DwtComboBox";

//
// Public methods
//

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
    this._valueToText[value] = text;
    if (!this._hasMenuCallback) {
		var menu = this._button.getMenu();
    	this._createMenuItem(menu, text);
	}
	if (selected) {
		this.setText(text);
	}
};

/** Removes the specified value from the list. */
DwtComboBox.prototype.remove = function(value) {
    var item = this._valueToItem[value];
    if (item) {
        this._button.getMenu().removeChild(item);
        var text = this._valueToText[value];
        delete this._textToValue[text];
        delete this._valueToText[value];
        delete this._valueToItem[value];
        if (this.getText() == text) {
            this.setText("");
        }
    }
};

/** Clears the list. */
DwtComboBox.prototype.removeAll = function() {
    this._button.setMenu(new AjxCallback(this, this._createMenu), true);
    this._hasMenuCallback = true;

    this._textToValue = {};
    this._valueToText = {};
    this._valueToItem = {};
};

/**
 * Returns the value of the currently selected entry. If the entry
 * is one that was not added via the add method (that is, if it was
 * typed in by the user) then null is returned.
 */
DwtComboBox.prototype.getValue =
function() {
	var text = this.getText();
	return this._textToValue[text];
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

DwtComboBox.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtComposite.prototype.setEnabled.call(this, enabled);
		this._input.setEnabled(enabled);
		this._button.setEnabled(enabled);
    }
};

/** Focuses the input field. */
DwtComboBox.prototype.focus = function() {
    this._input.focus();
};

//
// Protected methods
//

DwtComboBox.prototype._createMenu =
function() {
    var menu = new DwtMenu(this, null, null, null, this._dialog);
    for (var i in this._textToValue) {
    	var item = this._createMenuItem(menu, i);
        var value = this._textToValue[i];
        this._valueToItem[value] = item;
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
    return item;
};

DwtComboBox.prototype._menuItemListener =
function(ev) {
	var menuItem = ev.dwtObj;
	this._input.setValue(menuItem.getText());
	var input = this._input.getInputElement();
	input.focus();
	input.select();
};

DwtComboBox.prototype._createHtml = function(templateId) {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtComboBox.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtComposite.prototype._createHtmlFromTemplate.call(this, templateId, data);

	var inputParams = this._inputParams || {};
	inputParams.parent = this;
	inputParams.size = inputParams.size || 40;
    delete this._inputParams;
    
    this._input = new DwtInputField(inputParams);
    this._input.getInputElement().style.border = "none"; // TODO: this should be done w/ CSS
    this._input.replaceElement(data.id + "_input");
    
    this._button = new DwtComboBoxButton(this);
	this._button.setMenu(new AjxListener(this, this._createMenu), true);
    this._button.replaceElement(data.id + "_button");
};

//
// Classes
//

/**
 * DwtComboBoxButton: Stylizable button just for use in combo boxes.
 */
function DwtComboBoxButton(parent, className) {
	DwtButton.call(this, parent, null, className, Dwt.STATIC_STYLE);
}

DwtComboBoxButton.prototype = new DwtButton;
DwtComboBoxButton.prototype.constructor = DwtComboBoxButton;

DwtComboBoxButton.prototype.toString =
function() {
    return "DwtComboBoxButton";
};

// Data

DwtComboBoxButton.prototype.TEMPLATE = "ajax.dwt.templates.Widgets#DwtComboBoxButton"

