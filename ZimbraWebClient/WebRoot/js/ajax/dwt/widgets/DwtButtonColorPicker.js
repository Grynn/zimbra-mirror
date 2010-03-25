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
 * Creates a button
 * @constructor
 * @class
 * This class ntegrates {@link DwtButton} with a popup {@link DwtColorPicker}. This class is useful to
 * present a color picker button with an integrated drop-down for choosing from
 * a color palette. You can use addSelectionListener to register a handler
 * that will get called when a new color is selected.  Inspect "ev.detail" to
 * retrieve the color (guaranteed to be in #RRGGBB format).
 * <p>
 * The button also features a DIV that displays the currently selected color.
 * Upon clicking that DIV, the color will be cleared (in this event, ev.detail
 * will be the empty string in your selection listener).  Note you must call
 * showColorDisplay() in order for this DIV to be displayed.
 * <p>
 * All constructor arguments are passed forward to the {@link DwtButton} constructor.
 *
 * @extends DwtButton
 * @author Mihai Bazon
 * 
 * @param {hash}	params		a hash of parameters
 * @param  {DwtComposite}     params.parent		the parent widget
 * @param  {constant}     params.style			the button style
 * @param  {string}     params.className		the CSS class
 * @param  {constant}     params.posStyle		the positioning style
 * @param  {string}     params.id			the ID to use for the control's HTML element
 * @param  {number}     params.index 		the index at which to add this control among parent's children
 * @param  {boolean}     params.allowColorInput if <code>true</code>, allow a text field to allow user to input their customized RGB value
 * @param  {boolean}     params.noFillLabel	if <code>true</code>, do not fill label
 * 
 * @extends		DwtButton
 */
DwtButtonColorPicker = function(params) {
    if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtButtonColorPicker.PARAMS);
	params.actionTiming = DwtButton.ACTION_MOUSEUP;
    DwtButton.call(this, params);

	// WARNING: we pass boolean instead of a DwtDialog because (1) we don't
	// have a dialog right now and (2) DwtMenu doesn't seem to make use of
	// this parameter in other ways than to establish the zIndex.  That's
	// unnecessarily complex :-(
	var m = new DwtMenu({parent:this, style:DwtMenu.COLOR_PICKER_STYLE});
	this.setMenu(m);
	var cp = new DwtColorPicker(m, null, null, params.noFillLabel, params.allowColorInput);
	cp.addSelectionListener(new AjxListener(this, this._colorPicked));
    this.__colorPicker = cp ;    //for xform item _DWT_COLORPICKER_
	// no color initially selected
	this.__color = "";
};

DwtButtonColorPicker.PARAMS = ["parent", "style", "className", "posStyle", "id", "index", "noFillLabel", "allowColorInput"];

DwtButtonColorPicker.prototype = new DwtButton;
DwtButtonColorPicker.prototype.constructor = DwtButtonColorPicker;

//
// Constants
//

DwtButtonColorPicker._RGB_RE = /rgb\(([0-9]{1,3}),\s*([0-9]{1,3}),\s*([0-9]{1,3})\)/;

DwtButtonColorPicker._hexdigits = [ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' ];

//
// Data
//

//MOW:  DwtButtonColorPicker.prototype.TEMPLATE = "dwt.Widgets#ZButtonColorPicker";

//
// Public methods
//

/**
 * Utility function that converts the given integer to its hexadecimal representation.
 *
 * @param {number}		n 		the number to convert
 * @param {number}		[pad] 	the number of digits in the final number (zero-padded if required)
 * @return	{string}	the hexadecimal representation
 */
DwtButtonColorPicker.toHex =
function(n, pad) {
	var digits = [];
	while (n) {
		var d = DwtButtonColorPicker._hexdigits[n & 15];
		digits.push(d);
		n = n >> 4;
	}
	if (pad != null) {
		pad -= digits.length;
		while (pad-- > 0)
			digits.push('0');
	}
	digits.reverse();
	return digits.join("");
};

/**
 * Shows the color display. Call this function to display a DIV that shows the currently
 * selected color. This DIV also has the ability to clear the current color.
 * 
 * @param	{boolean}	disableMouseOver		if <code>true</code>, disable the mouse over
 */
DwtButtonColorPicker.prototype.showColorDisplay =
function(disableMouseOver) {
    if (!this._colorEl) return;

    if (!disableMouseOver) {
		this._colorEl.onmouseover = DwtButtonColorPicker.__colorDisplay_onMouseOver;
		this._colorEl.onmouseout = DwtButtonColorPicker.__colorDisplay_onMouseOut;
		this._colorEl.onmousedown = DwtButtonColorPicker.__colorDisplay_onMouseDown;
	}
};

/**
 * Gets the color.
 * 
 * @return {string}		the currently selected color
 */
DwtButtonColorPicker.prototype.getColor =
function() {
	return this.__color;
};

/**
 * Set the current color.
 *
 * @param {string} color 		the desired color. Pass the empty string "" to clear the selection.
 */ 
DwtButtonColorPicker.prototype.setColor =
function(color) {
	// let's make sure we keep it in #RRGGBB format
	var rgb = color.match(DwtButtonColorPicker._RGB_RE);
	if (rgb) {
		color = "#" +
			DwtButtonColorPicker.toHex(parseInt(rgb[1]), 2) +
			DwtButtonColorPicker.toHex(parseInt(rgb[2]), 2) +
			DwtButtonColorPicker.toHex(parseInt(rgb[3]), 2);
	}
	this.__color = color;
    var colorEl = this._colorEl;
    if (colorEl)
		colorEl.style.backgroundColor = color;
};

//
// Protected methods
//

DwtButtonColorPicker.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, templateId, data);

	// set the color display bit inside the title of the widget
	var displayHtml = AjxTemplate.expand('dwt.Widgets#ZButtonColorDisplay', data);
	this.setText(displayHtml);

    this._colorEl = document.getElementById(data.id+"_color");
};


// override "_setMinWidth" since that doesn't apply for this type of button
DwtButtonColorPicker.prototype._setMinWidth = function() {}


/// Protected function that is called when a color is chosen from the popup
/// DwtColorPicker.  Sets the current color to the chosen one and calls the
/// DwtButton's selection handlers if any.
DwtButtonColorPicker.prototype._colorPicked =
function(ev) {
	var color = ev.detail;
	this.__color = this.__detail = color;
    var colorEl = this._colorEl;
    if (colorEl) {
		colorEl.style.backgroundColor = color;
	}
	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var selEv = DwtShell.selectionEvent;
		// DwtUiEvent.copy(selEv, ev);
		selEv.item = this;
		selEv.detail = color;
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};

//
// Private methods
//

/// When the color display DIV is hovered, we show a small "X" icon to suggest
/// the end user that the selected color can be cleared.
DwtButtonColorPicker.prototype.__colorDisplay_onMouseOver =
function(ev, div) {
	if (!this.getEnabled())
		return;
	Dwt.addClass(div, "ImgDisable");
};

DwtButtonColorPicker.prototype.__colorDisplay_onMouseOut =
function(ev, div) {
	if (!this.getEnabled())
		return;
	Dwt.delClass(div, "ImgDisable");
};

/// Clears the selected color.  This function is called when the color display
/// DIV is clicked.
DwtButtonColorPicker.prototype.__colorDisplay_onMouseDown =
function(ev, div) {
	if (!this.getEnabled())
		return;
	var dwtev = DwtShell.mouseEvent;
	dwtev.setFromDhtmlEvent(ev);
	this.__color = this.__detail = div.style.backgroundColor = "";

 	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
 		var selEv = DwtShell.selectionEvent;
 		// DwtUiEvent.copy(selEv, ev);
 		selEv.item = this;
 		selEv.detail = "";
 		this.notifyListeners(DwtEvent.SELECTION, selEv);
 	}

	dwtev._stopPropagation = true;
	dwtev._returnValue = false;
	dwtev.setToDhtmlEvent(ev);
	return false;
};

// static event dispatchers

DwtButtonColorPicker.__colorDisplay_onMouseOver =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);
	obj.__colorDisplay_onMouseOver(ev, this);
};

DwtButtonColorPicker.__colorDisplay_onMouseOut =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);
	obj.__colorDisplay_onMouseOut(ev, this);
};

DwtButtonColorPicker.__colorDisplay_onMouseDown =
function(ev) {
	var obj = DwtControl.getTargetControl(ev);
	obj.__colorDisplay_onMouseDown(ev, this);
};
