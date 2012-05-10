/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
 * Creates a checkbox.
 * @constructor
 * @class
 * This class represents a checkbox.
 * 
 * @param {hash}	params	a hash of parameters
 * @param {DwtComposite}	params.parent	the parent widget
 * @param {DwtCheckbox.TEXT_LEFT|DwtCheckbox.TEXT_RIGHT}       [params.style=DwtCheckbox.TEXT_RIGHT] 	the text style
 * @param {string}       params.name		the input control name (required for IE)
 * @param {string}       params.value     the input control value
 * @param {boolean}       params.checked	the input control checked status (required for IE)
 * @param {string}       params.className	the CSS class
 * @param {constant}       params.posStyle	the positioning style (see {@link Dwt})
 * @param {string}       params.id		an explicit ID to use for the control's HTML element
 * @param {number}       params.index 	the index at which to add this control among parent's children
 * 
 *  @extends		DwtControl
 */
DwtCheckbox = function(params) {
	if (arguments.length == 0) { return; }

	params = Dwt.getParams(arguments, DwtCheckbox.PARAMS);
	params.className = params.className || "DwtCheckbox";

	DwtControl.call(this, params);

	this._textPosition = DwtCheckbox.DEFAULT_POSITION;
	this._initName = params.name;
    this._initValue = params.value;
	this._initChecked = params.checked;
	this._createHtml();
};

DwtCheckbox.prototype = new DwtControl;
DwtCheckbox.prototype.constructor = DwtCheckbox;

DwtCheckbox.prototype.isDwtCheckbox = true;
DwtCheckbox.prototype.isInputControl = true;
DwtCheckbox.prototype.toString = function() { return "DwtCheckbox"; };

//
// Constants
//
DwtCheckbox.PARAMS = [
	"parent",
	"style",
	"name",
	"checked",
	"className",
	"posStyle",
	"id",
	"index",
    "value"
];
/**
 * Defines the "left" text style position.
 */
DwtCheckbox.TEXT_LEFT			= "left";
/**
 * Defines the "right" text style position.
 */
DwtCheckbox.TEXT_RIGHT			= "right";
/**
 * Defines the default text style position.
 */
DwtCheckbox.DEFAULT_POSITION	= DwtCheckbox.TEXT_RIGHT;

//
// Data
//
DwtCheckbox.prototype.TEMPLATE = "dwt.Widgets#DwtCheckbox";

//
// Public methods
//
DwtCheckbox.prototype.getTabGroupMember =
function() {
	return this._inputEl;
};

DwtCheckbox.prototype.focus =
function() {
	if (this._inputEl) {
		this._inputEl.focus();
		DwtShell.getShell(window).getKeyboardMgr().grabFocus(this.getTabGroupMember());
	}
};

DwtCheckbox.prototype.blur =
function() {
	if (this._inputEl) {
		this._inputEl.blur();
	}
};

// listeners

/**
 * Adds a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtCheckbox.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtCheckbox.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

// properties

/**
 * Sets the enabled state.
 * 
 * @param	{boolean}	enabled		if <code>true</code>, the checkbox is enabled
 */
DwtCheckbox.prototype.setEnabled =
function(enabled) {
	if (enabled != this._enabled) {
		DwtControl.prototype.setEnabled.call(this, enabled);
		this._inputEl.disabled = !enabled;
		var className = enabled ? "Text" : "DisabledText";
		if (this._textElLeft) this._textElLeft.className = className;
		if (this._textElRight) this._textElRight.className = className;
	}
};

/**
 * Sets the selected state.
 * 
 * @param	{boolean}	selected		if <code>true</code>, the checkbox is selected
 */
DwtCheckbox.prototype.setSelected =
function(selected) {
	if (this._inputEl && this._inputEl.checked != selected) {
		this._inputEl.checked = selected;
	}
};

/**
 * Checks if the checkbox is selected state.
 * 
 * @return	{boolean}	<code>true</code> if the checkbox is selected
 */
DwtCheckbox.prototype.isSelected =
function() {
	return this._inputEl && this._inputEl.checked;
};

/**
 * Sets the checkbox text.
 * 
 * @param		{string}	text		the text
 */
DwtCheckbox.prototype.setText =
function(text) {
	if (this._textEl && this._text != text) {
		this._text = text;
		this._textEl.innerHTML = text || "";
	}
};

/**
 * Gets the checkbox text.
 * 
 * @return	{string}	the text
 */
DwtCheckbox.prototype.getText =
function() {
	return this._text;
};

/**
 * Sets the text position.
 * 
 * @param	{DwtCheckbox.TEXT_LEFT|DwtCheckbox.TEXT_RIGHT}		position	the position
 */
DwtCheckbox.prototype.setTextPosition =
function(position) {
	this._textEl = position == DwtCheckbox.TEXT_LEFT ? this._textElLeft : this._textElRight;
	if (this._textPosition != position) {
		this._textPosition = position;
		if (this._textElLeft) this._textElLeft.innerHTML = "";
		if (this._textElRight) this._textElRight.innerHTML = "";
		this.setText(this._text);
	}
};

/**
 * Gets the text position.
 * 
 * @return	{DwtCheckbox.TEXT_LEFT|DwtCheckbox.TEXT_RIGHT}		the position
 */
DwtCheckbox.prototype.getTextPosition =
function() {
	return this._textPosition;
};

/**
 * Sets the value.
 * 
 * @param	{string}		value		the value
 */
DwtCheckbox.prototype.setValue =
function(value) {
    var object = this._inputEl || this;
	if (object.value !== value) {
        object.value = value;
    }
};

/**
 * Gets the value.
 * 
 * @return		{string}		the value
 */
DwtCheckbox.prototype.getValue =
function() {
    var object = this._inputEl || this;
	return object.value != null ? object.value : this.getText();
};

/**
 * Gets the input element.
 * 
 * @return		{Element}		the element
 */
DwtCheckbox.prototype.getInputElement =
function() {
	return this._inputEl;
};

//
// DwtControl methods
//

DwtCheckbox.prototype.setToolTipContent = function(content) {
    if (content && !this.__mouseEventsSet) {
        // NOTE: We need mouse events in order to initiate tooltips on hover.
        // TODO: This should be done transparently in DwtControl for all
        // TODO: controls with tooltips.
        this.__mouseEventsSet = true;
        this._setMouseEvents();
    }
    DwtControl.prototype.setToolTipContent.apply(this, arguments);
};

//
// Protected methods
//

/**
 * The input field inherits the id for accessibility purposes.
 * 
 * @private
 */
DwtCheckbox.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
	nel = this.getInputElement();
	DwtControl.prototype._replaceElementHook.call(this, oel, nel, inheritClass, inheritStyle);
	if (oel.id) {
		this.setHtmlElementId(oel.id+"_control");
		nel.id = oel.id;
		if (this._textEl) {
			this._textEl.setAttribute(AjxEnv.isIE ? "htmlFor" : "for", oel.id);
		}
	}
};

//
// Private methods
//

DwtCheckbox.prototype._createHtml =
function(templateId) {
	var data = { id: this._htmlElId };
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtCheckbox.prototype._createHtmlFromTemplate =
function(templateId, data) {
	// NOTE: If  you don't set the name and checked status when
	//       creating checkboxes and radio buttons on IE, they will
	//       not take the first programmatic value. So we pass in
	//       the init values from the constructor.
	data.name = this._initName || this._htmlElId;
    data.value = this._initValue;
	data.checked = Boolean(this._initChecked) ? "checked" : "";
	DwtControl.prototype._createHtmlFromTemplate.call(this, templateId, data);
	this._inputEl = document.getElementById(data.id+"_input");
	if (this._inputEl) {
		var keyboardMgr = DwtShell.getShell(window).getKeyboardMgr();
		var handleFocus = AjxCallback.simpleClosure(keyboardMgr.grabFocus, keyboardMgr, this.getTabGroupMember());
		Dwt.setHandler(this._inputEl, DwtEvent.ONFOCUS, handleFocus);
		Dwt.setHandler(this._inputEl, DwtEvent.ONCLICK, DwtCheckbox.__handleClick);
	}
	this._textElLeft = document.getElementById(data.id+"_text_left");
	this._textElRight = document.getElementById(data.id+"_text_right");
	this.setTextPosition(this._textPosition);
};

//
// Private functions
//

DwtCheckbox.__handleClick =
function(evt) {
	var event = DwtUiEvent.getEvent(evt);
	var target = DwtUiEvent.getTarget(event);

	var selEv = DwtShell.selectionEvent;
	DwtUiEvent.copy(selEv, event);
	selEv.item = this;
	selEv.detail = target.checked;

	var checkbox = DwtControl.findControl(target);
	checkbox.setSelected(target.checked);
	checkbox.notifyListeners(DwtEvent.SELECTION, selEv);
};
