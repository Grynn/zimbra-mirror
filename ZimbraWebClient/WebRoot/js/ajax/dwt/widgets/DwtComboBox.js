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

/**
 * @constructor
 * @class
 * This class represents a combo box.
 *
 * @author Dave Comfort
 * 
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        inputParams	[hash]				params for the input (see DwtInputField)
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 *        dialog		[DwtDialog]*		containing dialog (to use as menu parent)
 */
DwtComboBox = function(params) {
    if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtComboBox.PARAMS);
    params.className = params.className || "DwtComboBox";
    DwtComposite.call(this, params);
    
    this.input = null;
    this._button = null;
    
    this._textToValue = {}; // Map of text strings to their values.
    this._valueToText = {};
    this._valueToItem = {};
	this._size = 0;

	this._dialog = params.dialog;
    this._hasMenuCallback = true;
	this._menuItemListenerObj = new AjxListener(this, this._menuItemListener);

    this._inputParams = params.inputParams;
    this._createHtml();
};

DwtComboBox.PARAMS = ["parent", "inputParams", "className", "posStyle", "dialog"];

DwtComboBox.prototype = new DwtComposite;
DwtComboBox.prototype.constructor = DwtComboBox;

DwtComboBox.prototype.toString =
function() {
    return "DwtComboBox";
};

//
// Data
//

DwtComboBox.prototype.TEMPLATE = "dwt.Widgets#DwtComboBox";

//
// Public methods
//

DwtComboBox.prototype.getTabGroupMember = function() {
	return this._tabGroup;
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
    this._valueToText[value] = text;
    if (!this._hasMenuCallback) {
		var menu = this._button.getMenu();
    	this._createMenuItem(menu, text);
	}
	if (selected) {
		this.setText(text);
	}
	this._size++;
	this._updateButton();
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
		this._size--;
		this._updateButton();
	}
};

/** Clears the list. */
DwtComboBox.prototype.removeAll = function() {
    this._button.setMenu(new AjxCallback(this, this._createMenu), true);
    this._hasMenuCallback = true;

    this._textToValue = {};
    this._valueToText = {};
    this._valueToItem = {};
	this._size = 0;
	this._updateButton();
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

DwtComboBox.prototype.setValue = function(value) {
	var text = this._valueToText[value];
	this.setText(text || value);
};

/**
 * Returns the text of the currently selected entry.
 */
DwtComboBox.prototype.getText =
function() {
	return this.input.getValue();
};

/**
 * Sets the selected text.
 */
DwtComboBox.prototype.setText =
function(text) {
	this.input.setValue(text);
};

DwtComboBox.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtComposite.prototype.setEnabled.call(this, enabled);
		this.input.setEnabled(enabled);
		this._button.setEnabled(enabled);
    }
};

/** Focuses the input field. */
DwtComboBox.prototype.focus = function() {
    this.input.focus();
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
	var item = new DwtMenuItem({parent:menu});
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
	this.input.setValue(menuItem.getText());
	var input = this.input.getInputElement();
	input.focus();
	input.select();
};

DwtComboBox.prototype._updateButton =
function() {
	this._button.setVisible(this._size > 0);
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
	inputParams.skipCaretHack = true;
	delete this._inputParams;
    
    this.input = new DwtInputField(inputParams);
    this.input.replaceElement(data.id + "_input");
    
    this._button = new DwtComboBoxButton({parent:this});
	this._button.setMenu(new AjxListener(this, this._createMenu), true);
    this._button.replaceElement(data.id + "_button");
	this._updateButton();

	this._tabGroup = new DwtTabGroup(this._htmlElId);
	this._tabGroup.addMember(this.input);
	this._tabGroup.addMember(this._button);
};

/** The input field inherits the id for accessibility purposes. */
DwtComboBox.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
	DwtComposite.prototype._replaceElementHook.apply(this, arguments);
	// set input settings
	if (oel.size) {
		this.input.getInputElement().size = oel.size;
	}
	if (oel.title) {
		this.input.setHint(oel.title);
	}
};

//
// Classes
//

/**
 * DwtComboBoxButton: Stylizable button just for use in combo boxes.
 * 
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        className		[string]*			CSS class
 */
DwtComboBoxButton = function(params) {
	params = Dwt.getParams(arguments, DwtComboBoxButton.PARAMS);
	params.posStyle = Dwt.RELATIVE_STYLE;
	DwtButton.call(this, params);
}

DwtComboBoxButton.prototype = new DwtButton;
DwtComboBoxButton.prototype.constructor = DwtComboBoxButton;

DwtComboBoxButton.prototype.toString =
function() {
    return "DwtComboBoxButton";
};

DwtComboBoxButton.PARAMS = ["parent", "className"];

// Data

DwtComboBoxButton.prototype.TEMPLATE = "dwt.Widgets#DwtComboBoxButton"

