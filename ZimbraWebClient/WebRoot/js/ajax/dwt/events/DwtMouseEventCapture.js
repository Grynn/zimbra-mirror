/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
* Helper class for event capturing
*
* @constructor
* @class DwtMouseEventCapture
*
* @author Ross Dargahi
*
* @param targetObj [DOM Element:required]	Target element
* @param id [string:optional]				ID for this capture instance.
* @param mouseOverHdlr [function:optional]	Browser event handler
* @param mouseDownHdlr [function:optional]	Browser event handler
* @param mouseMoveHdlr [function:optional]	Browser event handler
* @param mouseUpHdlr [function:optional]	Browser event handler
* @param mouseOutHdlr [function:optional]	Browser event handler
* @param hardCapture [boolean:optional]		If true, then event propagation is halted at this element
*/
DwtMouseEventCapture = function(targetObj, id, mouseOverHdlr, mouseDownHdlr, mouseMoveHdlr, mouseUpHdlr, mouseOutHdlr, mouseWheelHdlr, hardCapture) {
	this.targetObj = targetObj;
	this._id = id
	this._mouseOverHdlr = (mouseOverHdlr != null) ? mouseOverHdlr : DwtMouseEventCapture.emptyHdlr;
	this._mouseDownHdlr = (mouseDownHdlr != null) ? mouseDownHdlr : DwtMouseEventCapture.emptyHdlr;
	this._mouseMoveHdlr = (mouseMoveHdlr != null) ? mouseMoveHdlr : DwtMouseEventCapture.emptyHdlr;
	this._mouseUpHdlr = (mouseUpHdlr != null) ? mouseUpHdlr : DwtMouseEventCapture.emptyHdlr;
	this._mouseOutHdlr = (mouseOutHdlr != null) ? mouseOutHdlr : DwtMouseEventCapture.emptyHdlr;
	// NOTE: This is for any code that relies on the old signature
	if (typeof mouseWheelHdlr == "boolean") {
		hardCapture = mouseWheelHdlr;
		mouseWheelHdlr = null;
	}
	this._mouseWheelHdlr = (mouseWheelHdlr != null) ? mouseWheelHdlr : DwtMouseEventCapture.emptyHdlr;
	this._hardCapture = (hardCapture == null || hardCapture == true) ? true : false;
}

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
