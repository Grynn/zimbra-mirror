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
 * This class represents a slider.
 *
 * @param parent    {DwtControl} Parent widget (required)
 * @param orientation	{contant} Orientation of slider. DwtSlider.HORIZONTAL or DwtSlider.VERTICAL
 * @param className {string} CSS class. If not provided defaults to "DwtHorizontalSlider" or "DwtVerticalSlider" (optional)
 * @param posStyle {string} Positioning style (absolute, static, or relative). If
 *         not provided defaults to DwtControl.STATIC_STYLE (optional)
 */
DwtSlider = function(parent, orientation, className, posStyle) {
    if (arguments.length == 0) return;
    this._orientation = orientation || DwtSlider.HORIZONTAL;
    className = className || (this._orientation == DwtSlider.HORIZONTAL ? "DwtHorizontalSlider" : "DwtVerticalSlider");
    DwtControl.call(this, {parent:parent, className:className, posStyle:posStyle});

    this._size = 0;
    this._buttonSize = 0;

    this._value = 0;
    this._minimum = 0;
    this._maximum = 100;
    
    this._isDragging = false;

	DwtDragTracker.init(this, null, 0, 0, this._dragListener, this);

    this._createHtml();
};

DwtSlider.prototype = new DwtControl;
DwtSlider.prototype.constructor = DwtSlider;

DwtSlider.HORIZONTAL = 1;
DwtSlider.VERTICAL = 2;

DwtSlider.prototype.toString =
function() {
    return "DwtSlider";
};

/**
 * Sets the value of the slider, moving the position button accordingly.
 *
 * @param value		{number} The value
 * @param notify	{boolean} true to notify change listeners of the new value
 */
DwtSlider.prototype.setValue =
function(value, notify) {
	// Adjust the value into the valid range.
	value = Math.max(this._minimum, value);
	value = Math.min(this._maximum, value);
	this._value = value;

	// Move the button.
	var location = this._valueToLocation(value);
    var property = this._orientation == DwtSlider.HORIZONTAL ? "left" : "top";
    var element = this._getButtonElement();
    element.style[property] = location;
    
	// Send notification.
	if (notify) {
		if (!this._changeEvent) {
			this._changeEvent = new DwtEvent(true);
			this._changeEvent.dwtObj = this;
		}
	    this.notifyListeners(DwtEvent.ONCHANGE, this._changeEvent);
	}
};

/**
 * Returns the slider's value.
 */
DwtSlider.prototype.getValue =
function() {
	return this._value;
};

/**
 * Sets the range and value of the slider.
 *
 * @param minimum	{number} The minimum allowed value
 * @param maximum	{number} The maximum allowed value
 * @param value		{number} The value
 * @param notify	{boolean} true to notify change listeners of the new value
 */
DwtSlider.prototype.setRange =
function(minimum, maximum, newValue, notify) {
	if (minimum >= maximum) {
		throw new DwtException("Invalid slider range: [" + minimum + ", " + maximum + "]");
	};

	this._minimum = minimum;
	this._maximum = maximum;
	if (typeof newValue == "undefined") {
		newValue = minimum;
	}
	this.setValue(newValue, notify);
};

/**
 * Returns the minimum allowed value
 */
DwtSlider.prototype.getMinimum =
function() {
	return this._minimum;
};

/**
 * Returns the maximum allowed value
 */
DwtSlider.prototype.getMaximum =
function() {
	return this._maximum;
};

/**
 * Returns true if the slider is currently dragging.
 */
DwtSlider.prototype.isDragging =
function() {
	return this._isDragging;
};

/**
 * Adds a change listener.
 *
 * @param listener	{AjxListener} The listener
 */
DwtSlider.prototype.addChangeListener = 
function(listener) {
    this.addListener(DwtEvent.ONCHANGE, listener);
};

DwtSlider.prototype._setLocation =
function(location, notify) {
	var value = this._locationToValue(location);
	this.setValue(value, notify);
};

DwtSlider.prototype._getLocation =
function() {
	return this._valueToLocation(this._value);
};

DwtSlider.prototype._valueToLocation =
function(value) {
	if (this._orientation == DwtSlider.HORIZONTAL) {
	    return (value - this._minimum) / (this._maximum - this._minimum) * (this._size - this._buttonSize);
	} else {
	    return this._size - this._buttonSize - (value - this._minimum) / (this._maximum - this._minimum) * (this._size - this._buttonSize);
	}
};

DwtSlider.prototype._locationToValue =
function(location) {
	if (this._orientation == DwtSlider.HORIZONTAL) {
	    return location / (this._size - this._buttonSize) * (this._maximum - this._minimum) + this._minimum;
	} else {
	    return (this._size - this._buttonSize - location) / (this._size - this._buttonSize) * (this._maximum - this._minimum) + this._minimum;
	}
};

DwtSlider.prototype._calculateSizes =
function() {
	var property = this._orientation == DwtSlider.HORIZONTAL ? "x" : "y";
	this._buttonSize = Dwt.getSize(this._getButtonElement())[property];
	this._size = Dwt.getSize(this.getHtmlElement())[property];
	if (this._buttonSize >= this._size) {
		throw new DwtException("Invalid slider sizes");
	}
};

DwtSlider.prototype._getButtonElement =
function() {
	return document.getElementById(this._htmlElId + "_button");
};

DwtSlider.prototype._createHtml =
function() {
    var element = this.getHtmlElement();
    var args = { id:this._htmlElId };
    var template = this._orientation == DwtSlider.HORIZONTAL ? 
    	"dwt.Widgets#DwtHorizontalSlider" : 
    	"dwt.Widgets#DwtVerticalSlider";
    element.innerHTML = AjxTemplate.expand(template, args);
    this._calculateSizes();
};

DwtSlider.prototype._dragListener =
function(obj, a, b) {
	var elementProperty = this._orientation == DwtSlider.HORIZONTAL ? "x" : "y";
	var eventProperty = this._orientation == DwtSlider.HORIZONTAL ? "docX" : "docY";
	if (obj.state == DwtDragTracker.STATE_START) {
		// If clicked outside of button, move button immediately.
		var windowLocation = Dwt.toWindow(this.getHtmlElement(), 0, 0);
		var clickLocation = obj.mouseEv[eventProperty] - windowLocation[elementProperty];
		var buttonLocation = this._getLocation();
		if (clickLocation < buttonLocation || clickLocation > (buttonLocation + this._buttonSize)) {
			this._setLocation(clickLocation - this._buttonSize / 2, true);
		}

		// Save the original position in the tracker's user data.
		obj.userData = { location: this._getLocation(), value: this._value };
		this._isDragging = true;
		this._moved = false;
	} else {
		if (obj.state == DwtDragTracker.STATE_END) {
			this._isDragging = false;
		} else if (obj.state == DwtDragTracker.STATE_DRAGGING) {
			this._moved = true;
		}
		if (this._moved) {
			var location = obj.userData.location + obj.delta[elementProperty];
			this._setLocation(location, true);
		}
	}
};
