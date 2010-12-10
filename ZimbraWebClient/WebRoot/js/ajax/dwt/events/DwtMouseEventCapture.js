/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a helper class for mouse event capturing.
 * @constructor
 * @class
 *
 * @author Ross Dargahi
 *
 * @param {hash}	params			a hash of parameters
 * @param {Element}      params.targetObj			the target element
 * @param {string}      params.id				the ID for this capture instance.
 * @param {function}      params.mouseOverHdlr		the browser event handler
 * @param {function}      params.mouseDownHdlr		the browser event handler
 * @param {function}      params.mouseMoveHdlr		the browser event handler
 * @param {function}      params.mouseUpHdlr		the browser event handler
 * @param {function}      params.mouseOutHdlr		the browser event handler
 * @param {function}      params.mouseWheelHdlr	the browser event handler
 * @param {boolean}       params.hardCapture		if <code>true</code>, event propagation is halted at this element (IE only)
 * @param {function}      params.onRelease			function to call when capturer is being released
 * @param {boolean}       params.enableAnyEvent		if <code>true</code>, handlers may be called even when a DwtControl is halting propagation
 * 
 * @private
 */
DwtMouseEventCapture = function(params) {

	params = Dwt.getParams(arguments, DwtMouseEventCapture.PARAMS);

	this.targetObj = params.targetObj;
	this._id = params.id;
	this._mouseOverHdlr = params.mouseOverHdlr || DwtMouseEventCapture.emptyHdlr;
	this._mouseDownHdlr = params.mouseDownHdlr || DwtMouseEventCapture.emptyHdlr;
	this._mouseMoveHdlr = params.mouseMoveHdlr || DwtMouseEventCapture.emptyHdlr;
	this._mouseUpHdlr = params.mouseUpHdlr || DwtMouseEventCapture.emptyHdlr;
	this._mouseOutHdlr = params.mouseOutHdlr || DwtMouseEventCapture.emptyHdlr;
	this._mouseWheelHdlr = params.mouseWheelHdlr || DwtMouseEventCapture.emptyHdlr;
	this._hardCapture = (params.hardCapture !== false);
	this._onRelease = AjxUtil.isFunction(params.onRelease) && params.onRelease || null;
	this._enableAnyEvent = params.enableAnyEvent;
}

DwtMouseEventCapture.PARAMS = ["targetObj", "id", "mouseOverHdlr", "mouseDownHdlr", "mouseMoveHdlr",
							   "mouseUpHdlr", "mouseOutHdlr", "mouseWheelHdlr", "hardCapture", "onRelease", "enableAnyEvent"];

DwtMouseEventCapture._capturing = false;

DwtMouseEventCapture.getCaptureObj =
function() {
	return window._mouseEventCaptureObj;
}

DwtMouseEventCapture.getTargetObj =
function() {
	return window._mouseEventCaptureObj ? window._mouseEventCaptureObj.targetObj : null;
}

DwtMouseEventCapture.getId =
function() {
	return window._mouseEventCaptureObj ? window._mouseEventCaptureObj._id : null;
}

DwtMouseEventCapture.prototype.toString = 
function() {
	return "DwtMouseEventCapture";
}

DwtMouseEventCapture.prototype.capturing =
function() {
	return DwtMouseEventCapture._capturing;
}

DwtMouseEventCapture.prototype.capture =
function() {
	if (window._mouseEventCaptureObj) {
		window._mouseEventCaptureObj.release();
	}

	if (document.body != null && document.body.addEventListener != null) {
		document.body.addEventListener("mouseover", this._mouseOverHdlr, true);
		document.body.addEventListener("mousedown", this._mouseDownHdlr, true);
		document.body.addEventListener("mousemove", this._mouseMoveHdlr, true);
		document.body.addEventListener("mouseup", this._mouseUpHdlr, true);
		document.body.addEventListener("mouseout", this._mouseOutHdlr, true);
		document.body.addEventListener("DOMMouseScroll", this._mouseWheelHdlr, true);
	} else {
		this._savedMouseOverHdlr = document.onmouseover;
		this._savedMouseDownHdlr = document.onmousedown;
		this._savedMouseMoveHdlr = document.onmousemove;
		this._savedMouseUpHdlr = document.onmouseup;
		this._savedMouseOutHdlr = document.onmouseout;
		this._savedMouseWheelHdlr = document.onmousewheel;
		document.onmouseover = this._mouseOverHdlr;
		document.onmousedown = this._mouseDownHdlr;
		document.onmousemove = this._mouseMoveHdlr;
		document.onmouseup = this._mouseUpHdlr;
		document.onmouseout = this._mouseOutHdlr;
		document.onmousewheel = this._mouseWheelHdlr;
	}
	if (this._hardCapture && document.body && document.body.setCapture) {
		document.body.setCapture();
	}
	window._mouseEventCaptureObj = this;
	DwtMouseEventCapture._capturing = true;
}


DwtMouseEventCapture.prototype.release = 
function() {
	if (window._mouseEventCaptureObj == null) { return; }
	if (this._onRelease) {
		this._onRelease();
	}

	var obj = window._shellCaptureObj;
	if (document.body && document.body.addEventListener) {
		document.body.removeEventListener("mouseover", this._mouseOverHdlr, true);
		document.body.removeEventListener("mousedown", this._mouseDownHdlr, true);
		document.body.removeEventListener("mousemove", this._mouseMoveHdlr, true);
		document.body.removeEventListener("mouseup", this._mouseUpHdlr, true);
		document.body.removeEventListener("mouseout", this._mouseOutHdlr, true);
		document.body.removeEventListener("DOMMouseScroll", this._mouseWheelHdlr, true);
	} else {
		document.onmouseover = this._savedMouseOverHdlr
		document.onmousedown = this._savedMouseDownHdlr;
		document.onmousemove = this._savedMouseMoveHdlr;
		document.onmouseup = this._savedMouseUpHdlr;
		document.onmouseout = this._savedMouseOutHdlr;
		document.onmousewheel = this._savedMouseWheelHdlr;
	}
	if (this._hardCapture && document.body && document.body.releaseCapture) {
		document.body.releaseCapture();
	}
	window._mouseEventCaptureObj = null;
	DwtMouseEventCapture._capturing = false;
}

DwtMouseEventCapture.prototype.runHandler =
function(type, ev) {
	if (this._enableAnyEvent) {
		switch (type) {
			case DwtEvent.ONMOUSEOVER:
				this._mouseOverHdlr && this._mouseOverHdlr(ev);
				break;
			case DwtEvent.ONMOUSEDOWN:
				this._mouseDownHdlr && this._mouseDownHdlr(ev);
				break;
			case DwtEvent.ONMOUSEMOVE:
				this._mouseMoveHdlr && this._mouseMoveHdlr(ev);
				break;
			case DwtEvent.ONMOUSEUP:
				this._mouseUpHdlr && this._mouseUpHdlr(ev);
				break;
			case DwtEvent.ONMOUSEOUT:
				this._mouseOutHdlr && this._mouseOutHdlr(ev);
				break;
			case DwtEvent.ONMOUSEWHEEL:
				this._mouseWheelHdlr && this._mouseWheelHdlr(ev);
				break;
		}
	}
}

DwtMouseEventCapture.runHandler =
function(type, ev) {
	if (window._mouseEventCaptureObj)
		window._mouseEventCaptureObj.runHandler(type, ev);
}

DwtMouseEventCapture.emptyHdlr =
function(ev) {
	var capObj = DwtMouseEventCapture.getCaptureObj();
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (capObj._hardCapture) {
		mouseEv._stopPropagation = true;
		mouseEv._returnValue = false;
		mouseEv.setToDhtmlEvent(ev);
		return false;	
	} else {
		mouseEv._stopPropagation = false;
		mouseEv._returnValue = true;
		mouseEv.setToDhtmlEvent(ev);
		return true;
	}	
}
