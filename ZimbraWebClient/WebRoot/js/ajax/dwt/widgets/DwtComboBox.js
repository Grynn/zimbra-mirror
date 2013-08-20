/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates a combo box.
 * @constructor
 * @class
 * This class represents a combo box.
 *
 * @author Dave Comfort
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      parent		the parent widget
 * @param {boolean}	useLabel		Set to true if the value should be shown in a DwtLabel. Defaults to false, showing it in a DwtInputField.
 * @param {hash}	inputParams		params for the input (see {@link DwtInputField} or {@link DwtLabel})
 * @param {string}      className		the CSS class
 * @param {constant}      posStyle		the positioning style (see {@link DwtControl})
 * @param {int}     maxRows         The number of maxRows needed in drop down(see {@link DwtMenu})
 * @param {constant} layout         The layout of the drop down(see {@link DwtMenu})
 * @param {boolean} autoScroll    Set to true if auto scroll to the input text is needed. Defaults to false.
 * 
 * @extends		DwtComposite
 */
DwtComboBox = function(params) {
    if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtComboBox.PARAMS);
    params.className = params.className || "DwtComboBox";
    DwtComposite.call(this, params);
    
    this.input = null;
	this._menu = null;
    this._button = null;
    
    this._textToValue = {}; // Map of text strings to their values.
    this._valueToText = {};
    this._valueToItem = {};
	this._size = 0;

    this._hasMenuCallback = true;
	this._menuItemListenerObj = new AjxListener(this, this._menuItemListener);

    this._inputParams = params.inputParams;
	this._useLabel = Boolean(params.useLabel);
	this._maxRows = params.maxRows;
	this._layout = params.layout;
	this._autoScroll = params.autoScroll || false;
    this._createHtml();
};

DwtComboBox.PARAMS = ["parent", "inputParams", "className", "posStyle", "dialog"];

DwtComboBox.prototype = new DwtComposite;
DwtComboBox.prototype.constructor = DwtComboBox;

DwtComboBox.prototype.isDwtComboBox = true;
DwtComboBox.prototype.toString = function() { return "DwtComboBox"; };

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
 * Adds the change listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtComboBox.prototype.addChangeListener = function(listener) {
	this.addListener(DwtEvent.ONCHANGE, listener);
};

/**
 * Removes the change listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtComboBox.prototype.removeChangeListener = function(listener) {
	this.removeListener(DwtEvent.ONCHANGE, listener);
};

/**
 * Adds an entry to the combo box list.
 * 
 * @param {string}	text		the user-visible text for the entry
 * @param {string}	value		the value for the entry
 * @param {boolean}	selected	if <code>true</code>, the entry is selected
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

/**
 * Removes the specified value from the list.
 *
 * @param	{string}	value		the value
 */
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

/**
 * Removes all the items in the list.
 * 
 */
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
 * Gets the value of the currently selected entry. If the entry
 * is one that was not added via the add method (that is, if it was
 * typed in by the user) then <code>null</code> is returned.
 * 
 * @return	{string}	the value
 */
DwtComboBox.prototype.getValue =
function() {
	var text = this.getText();
	return this._textToValue[text];
};

/**
 * Sets the value.
 * 
 * @param	{string}	value		the value
 */
DwtComboBox.prototype.setValue = function(value) {
	var text = this._valueToText[value];
	this.setText(text || value);
};

/**
 * Gets the text of the currently selected entry.
 * 
 * @return	{string}	the text
 */
DwtComboBox.prototype.getText =
function() {
	return this._useLabel ? this.input.getText() : this.input.getValue();
};

/**
 * Sets the selected text.
 * 
 * @param	{string}	text		the text
 */
DwtComboBox.prototype.setText =
function(text) {
	if (this._useLabel)
		this.input.setText(text);
	else
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

DwtComboBox.prototype.focus = function() {
    this.input.focus();
};


//
// Protected methods
//

DwtComboBox.prototype._createMenu =
function() {
	var params = {parent:this};
	if (this._maxRows) {
		params.maxRows = this._maxRows;
	}
	if (this._layout) {
		params.layout = this._layout;
	}
    var menu = this._menu = new DwtMenu(params);
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
	var ovalue = this.getText();
	var nvalue = menuItem.getText();
	this.setText(nvalue);
	this._menu.popdown();

	// notify our listeners
	var event = DwtUiEvent.getEvent(ev);
	event._args = { selectObj: this, newValue: nvalue, oldValue: ovalue };
	this.notifyListeners(DwtEvent.ONCHANGE, event);

	if (!this._useLabel) {
	    var input = this.input.getInputElement();
	    input.focus();
	    input.select();
	}
};

DwtComboBox.prototype._handleKeyDown = function(ev) {
	this.__ovalue = this.getText();
	return true;
};

DwtComboBox.prototype._handleKeyUp = function(ev) {
	// propagate event to DwtInputField
	DwtInputField._keyUpHdlr(ev);
	// notify our listeners
	var event = DwtUiEvent.getEvent(ev);
	var newValue = this.getText();
	event._args = { selectObj: this, newValue: newValue, oldValue: this.__ovalue };
	this.notifyListeners(DwtEvent.ONCHANGE, event);
	if (this._menu && this._autoScroll && newValue != this.__ovalue) {
		//if auto scroll is on then scroll to the index which starts with
		//the value in input field
		var index = 0;
		for (var text in this._textToValue) {
			if (text.indexOf(newValue) == 0) {
				this._menu.scrollToIndex(index);
				break;
			}
			index++;
		}
	}
	return true;
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
	delete this._inputParams;
    
	this.input = (this._useLabel ?
	              new DwtLabel(inputParams) : new DwtInputField(inputParams));
    this.input.replaceElement(data.id + "_input");
	this.input.setHandler(DwtEvent.ONKEYDOWN, AjxCallback.simpleClosure(this._handleKeyDown, this));
	this.input.setHandler(DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this._handleKeyUp, this));

    this._button = new DwtComboBoxButton({parent:this});
	this._button.setMenu(new AjxListener(this, this._createMenu), true);
    this._button.replaceElement(data.id + "_button");
	this._updateButton();

	this._tabGroup = new DwtTabGroup(this._htmlElId);
	this._tabGroup.addMember(this.input);
	this._tabGroup.addMember(this._button);
};

/**
 * The input field inherits the id for accessibility purposes.
 * 
 * @private
 */
DwtComboBox.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
	DwtComposite.prototype._replaceElementHook.apply(this, arguments);
	// set input settings
	if (oel.size) {
		var el = (this._useLabel ?
		          this.input.getHtmlElement() :
		          this.input.getInputElement());
		el.size = oel.size;
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
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}       params.parent		the parent widget
 * @param	{string}       params.className		the CSS class
 * 
 * @extends		DwtButton
 * @private
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

