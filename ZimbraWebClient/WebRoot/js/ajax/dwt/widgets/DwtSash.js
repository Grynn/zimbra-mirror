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
 * Creates a sash.
 * @constructor
 * @class
 * A sash is a thin moveable border between two components that can be moved in
 * order to change the sizes of the elements on either side. Those one one side
 * will become larger, and those on the other side will become smaller.
 * 
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      params.parent		the parent widget
 * @param {DwtSash.HORIZONTAL_STYLE|DwtSash.VERTICAL_STYLE}      [params.style=DwtSash.VERTICAL_STYLE]			the sash style
 * @param {string}      params.className		the CSS class
 * @param    {number}	[params.threshold=1]		minimum movement (in pixels) that forces a UI update. Clients will likely want to set it higher.
 * @param {constant}       params.posStyle		the positioning style (see {@link DwtControl})
 * 
 * @extends		DwtControl
 */
DwtSash = function(params) {
	params = Dwt.getParams(arguments, DwtSash.PARAMS);
	params.className = params.className || "DwtSash";
	params.posStyle = params.posStyle || DwtControl.ABSOLUTE_STYLE;
	DwtControl.call(this, params);

    var htmlElement = this.getHtmlElement();
    var templatePrefix = "dwt.Widgets#";
    if (!params.style || params.style != DwtSash.HORIZONTAL_STYLE) {
		this._style = DwtSash.VERTICAL_STYLE;
		htmlElement.style.cursor = AjxEnv.isIE ? "row-resize" : "s-resize";
		htmlElement.innerHTML = AjxTemplate.expand(templatePrefix + "DwtVerticalSash");
	} else {
		this._style = DwtSash.HORIZONTAL_STYLE;
		htmlElement.style.cursor = AjxEnv.isIE ? "col-resize" : "w-resize";
		htmlElement.innerHTML = AjxTemplate.expand(templatePrefix + "DwtHorizontalSash");
	}
	this._threshold = (params.threshold > 0) ? params.threshold : 1;

	this._captureObj = new DwtMouseEventCapture({
		targetObj:this,
		id:"DwtSash",
		mouseOverHdlr:DwtSash._mouseOverHdlr,
		mouseDownHdlr:DwtSash._mouseDownHdlr,
		mouseMoveHdlr:DwtSash._mouseMoveHdlr,
		mouseUpHdlr:DwtSash._mouseUpHdlr,
		mouseOutHdlr:DwtSash._mouseOutHdlr
	});
	this.setHandler(DwtEvent.ONMOUSEDOWN, DwtSash._mouseDownHdlr);
	this.setHandler(DwtEvent.ONMOUSEOVER, DwtSash._mouseOverHdlr);
	this.setHandler(DwtEvent.ONMOUSEOUT, DwtSash._mouseOutHdlr);

	this.setZIndex(Dwt.Z_VIEW);
}

DwtSash.PARAMS = ["parent", "style", "className", "threshold", "posStyle"];

DwtSash.prototype = new DwtControl;
DwtSash.prototype.constructor = DwtSash;

DwtSash.prototype.toString = 
function() {
	return "DwtSash";
}

/**
 * Defines the "horizontal" sash style.
 */
DwtSash.HORIZONTAL_STYLE  = 1;
/**
 * Defines the "vertical" sash style.
 */
DwtSash.VERTICAL_STYLE = 2;

// The callback function will be called with a proposed delta. It should return a value indication
// how much of a delta was actually applied.
DwtSash.prototype.registerCallback =
function(callbackFunc, callbackObj) {
	this._callbackFunc = callbackFunc;
	this._callbackObj = callbackObj;
};

DwtSash._mouseOverHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSash._mouseDownHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	DwtEventManager.notifyListeners(DwtEvent.ONMOUSEDOWN, mouseEv);
	var sash = mouseEv.dwtObj;
	if (sash._callbackFunc != null) {
		sash._captureObj.capture();
		sash._startCoord = (sash._style == DwtSash.HORIZONTAL_STYLE) 
				? mouseEv.docX : mouseEv.docY;
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	
	return false;	
}

DwtSash._mouseMoveHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	var delta = 0;
	var sash = DwtMouseEventCapture.getTargetObj();
	if (sash._style == DwtSash.HORIZONTAL_STYLE) {
		if (mouseEv.docX > 0 && mouseEv.docX != sash._startCoord)
			delta = mouseEv.docX - sash._startCoord;
	} else  {
		if (mouseEv.docY > 0 && mouseEv.docY != sash._startCoord)
			delta = mouseEv.docY - sash._startCoord;
	}
		
	if (Math.abs(delta) >= sash._threshold) {
		if (sash._callbackObj != null)
			delta = sash._callbackFunc.call(sash._callbackObj, delta);
		else 
			delta = sash._callbackFunc(delta);		
		sash._startCoord += delta;
		// If movement happened, then shift our location by the actual amount of movement
		if (delta != 0 && sash.getHtmlElement().style.position == Dwt.ABSOLUTE_STYLE) {
			if (sash._style == DwtSash.HORIZONTAL_STYLE)
				sash.setLocation(sash.getLocation().x + delta, Dwt.DEFAULT);
			else
				sash.setLocation(Dwt.DEFAULT, sash.getLocation().y + delta);
		}
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSash._mouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}

	var sash = DwtMouseEventCapture.getTargetObj();
	if (sash._callbackFunc != null) {
		DwtMouseEventCapture.getCaptureObj().release();
	}

	sash.notifyListeners(DwtEvent.ONMOUSEUP, mouseEv);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSash._mouseOutHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

