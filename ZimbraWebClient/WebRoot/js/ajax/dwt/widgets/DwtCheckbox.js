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

/**
 * @constructor
 * @class
 * This class implements a checkbox.
 * 
 * @param params	[hash]			hash of params:
 *        parent	[DwtComposite] 	parent widget
 *        style 	[constant]*		The text style. May be one of: <i>DwtCheckbox.TEXT_LEFT</i> or
 * 									<i>DwtCheckbox.TEXT_RIGHT</i> arithimatically or'd (|) with one of:
 * 									<i>DwtCheckbox.ALIGN_LEFT</i>, <i>DwtCheckbox.ALIGN_CENTER</i>, or
 * 									<i>DwtCheckbox.ALIGN_LEFT</i>.
 * 									The first determines were in the checkbox the text will appear
 * 									(if set), the second determine how the content of the text will be
 * 									aligned. The default value for this parameter is: 
 * 									<code>DwtCheckbox.TEXT_LEFT | DwtCheckbox.ALIGN_CENTER</code>.
 *        name		[string]		The input control name. Required for IE.
 *        checked	[boolean]		The input control checked status. Required for IE.
 *        className	[string]*		CSS class
 *        posStyle	[constant]*		positioning style
 *        id		[string]*		an explicit ID to use for the control's HTML element
 *        index 	[int]*			index at which to add this control among parent's children 
 */
DwtCheckbox = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtCheckbox.PARAMS);
	params.className = params.className || "DwtCheckbox";
	DwtControl.call(this, params);
	this._initName = params.name;
	this._initChecked = params.checked;
	this._createHtml();
}

DwtCheckbox.PARAMS = ["parent", "style", "name", "checked", "className", "posStyle", "id", "index"];

DwtCheckbox.prototype = new DwtControl;
DwtCheckbox.prototype.constructor = DwtCheckbox;

DwtCheckbox.prototype.toString = function() {
	return "DwtCheckbox";
};

//
// Constants
//

DwtCheckbox.TEXT_LEFT = "left";
DwtCheckbox.TEXT_RIGHT = "right";

DwtCheckbox.DEFAULT_POSITION = DwtCheckbox.TEXT_RIGHT;

//
// Data
//

DwtCheckbox.prototype.TEMPLATE = "dwt.Widgets#DwtCheckbox";

DwtCheckbox.prototype._textPosition = DwtCheckbox.DEFAULT_POSITION;

//
// Public methods
//
DwtCheckbox.prototype.getTabGroupMember = function() {
	return this._inputEl;
};

DwtCheckbox.prototype.focus = function() {
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

DwtCheckbox.prototype.addSelectionListener =
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};
DwtCheckbox.prototype.removeSelectionListener =
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};

// properties

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

DwtCheckbox.prototype.setSelected = function(selected) {
    if (this._inputEl && this._inputEl.checked != selected) {
        this._inputEl.checked = selected;
    }
};
DwtCheckbox.prototype.isSelected = function() {
    return this._inputEl && this._inputEl.checked;
};

DwtCheckbox.prototype.setText = function(text) {
    if (this._textEl && this._text != text) {
        this._text = text;
        this._textEl.innerHTML = text || "";
    }
};
DwtCheckbox.prototype.getText = function() {
    return this._text;
};

DwtCheckbox.prototype.setTextPosition = function(position) {
	this._textEl = position == DwtCheckbox.TEXT_LEFT ? this._textElLeft : this._textElRight;
	if (this._textPosition != position) {
		this._textPosition = position;
		if (this._textElLeft) this._textElLeft.innerHTML = "";
		if (this._textElRight) this._textElRight.innerHTML = "";
		this.setText(this._text);
	}
};
DwtCheckbox.prototype.getTextPosition = function() {
	return this._textPosition;
};

DwtCheckbox.prototype.setValue = function(value) {
	if (this._value != value) {
		this._value = value;
		if (this._inputEl) {
			this._inputEl.value = value;
		}
	}
};
DwtCheckbox.prototype.getValue = function() {
    return this._value != null ? this._value : this._text;
};

DwtCheckbox.prototype.getInputElement = function() {
    return this._inputEl;
};

//
// Protected methods
//

/** The input field inherits the id for accessibility purposes. */
DwtCheckbox.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
    nel = this.getInputElement();
    DwtControl.prototype._replaceElementHook.call(this, oel, nel, inheritClass, inheritStyle);
    if (oel.id) {
        nel.id = oel.id;
        if (this._textEl) {
            this._textEl.setAttribute("for", oel.id);
        }
    }
};

//
// Private methods
//

DwtCheckbox.prototype._createHtml = function(templateId) {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtCheckbox.prototype._createHtmlFromTemplate = function(templateId, data) {
	// NOTE: If  you don't set the name and checked status when
	//       creating checkboxes and radio buttons on IE, they will
	//       not take the first programmatic value. So we pass in
	//       the init values from the constructor.
	data.name = this._initName || this._htmlElId;
	data.checked = Boolean(this._initChecked) ? "checked" : "";
	DwtControl.prototype._createHtmlFromTemplate.call(this, templateId, data);
	this._inputEl = document.getElementById(data.id+"_input");
	if (this._inputEl) {
		var keyboardMgr = DwtShell.getShell(window).getKeyboardMgr();
		var handleFocus = AjxCallback.simpleClosure(keyboardMgr.grabFocus, keyboardMgr, this.getTabGroupMember());
		Dwt.setHandler(this._inputEl, DwtEvent.ONFOCUS, handleFocus);
		Dwt.setHandler(this._inputEl, DwtEvent.ONCLICK, DwtCheckbox.__handleClick);
		Dwt.associateElementWithObject(this._inputEl, this);
	}
	this._textElLeft = document.getElementById(data.id+"_text_left");
	this._textElRight = document.getElementById(data.id+"_text_right");
	this.setTextPosition(this._textPosition);
};

//
// Private functions
//

DwtCheckbox.__handleClick = function(evt) {
    var event = DwtUiEvent.getEvent(evt);
    var target = DwtUiEvent.getTarget(event);

    var selEv = DwtShell.selectionEvent;
    DwtUiEvent.copy(selEv, event);
    selEv.item = this;
    selEv.detail = target.checked;

    var checkbox = Dwt.getObjectFromElement(target);
    checkbox.setSelected(target.checked);
    checkbox.notifyListeners(DwtEvent.SELECTION, selEv);
};