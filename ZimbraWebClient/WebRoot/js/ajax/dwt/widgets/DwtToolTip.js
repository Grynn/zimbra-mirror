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
 * 
 * @private
 */
DwtToolTip = function(shell, className, dialog) {
	if (arguments.length == 0) return;
	this.shell = shell;
	this._dialog = dialog;
	this._poppedUp = false;
	this.isSticky = false;
	this._div = document.createElement("div");
	this._div.className = className || "DwtToolTip";
	this._div.style.position = DwtControl.ABSOLUTE_STYLE;
	this.shell.getHtmlElement().appendChild(this._div);
	Dwt.setZIndex(this._div, Dwt.Z_HIDDEN);
	Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);

    // create html
    // NOTE: This id is ok because there's only ever one instance of a tooltip
    var templateId = "dwt.Widgets#"+this._borderStyle;
    this._div.innerHTML = AjxTemplate.expand(templateId, "tooltip");

    var params = AjxTemplate.getParams(templateId);
    this._borderWidth = Number(params.width);
    this._borderHeight = Number(params.height);

    // save reference to content div
    this._contentDiv = document.getElementById("tooltipContents");

    Dwt.setHandler(this._div, DwtEvent.ONMOUSEOVER, AjxCallback.simpleClosure(this._mouseOverListener, this));
}

DwtToolTip.prototype.toString =
function() {
	return "DwtToolTip";
};

//
// Constants
//

DwtToolTip.TOOLTIP_DELAY = 750;

//
// Data
//

DwtToolTip.prototype._borderStyle = "DwtToolTip";

//
// Public methods
//

DwtToolTip.prototype.getContent =
function() {
    return this._div.innerHTML;
};

DwtToolTip.prototype.setContent =
function(content, setInnerHTML) {
	this._content = content;
	if(setInnerHTML) {
        this._contentDiv.innerHTML = this._content;
    }
};
	
DwtToolTip.prototype.popup = 
function(x, y, skipInnerHTML, popdownOnMouseOver) {
    if (this._popupAction) {
        AjxTimedAction.cancelAction(this._popupAction);
        this._popupAction = null;
    }
    this._popdownOnMouseOver = popdownOnMouseOver; // popdownOnMouseOver may be truthy to pop down the tooltip if the mouse hovers over the tooltip. Optionally, it can be an AjxCallback that will be called after popping the tooltip down
    if (this._content != null) {
		if(!skipInnerHTML) {
            this._contentDiv.innerHTML = this._content;
        }

		this._popupAction = new AjxTimedAction(this, this._positionElement, [x, y]);
		AjxTimedAction.scheduleAction(this._popupAction, 5);
	}
};

/*
* setSticky allows making the tooltip not to popdown. 
* IMPORTANT: Tooltip is singleton inside Zimbra i.e. only one instance of tooltip is reused by all objects. 
* So, it is very important for the code setting tooltip to sticky to have some mechanism to close the tooltip by itself. 
* Like have a close-button inside tooltip and when clicked, should set the setSticky(false) and then close the tooltip.
* 
*/
DwtToolTip.prototype.setSticky = 
function(bool) {
	this.isSticky = bool;
};

DwtToolTip.prototype.popdown = 
function() {
    this._popdownOnMouseOver = false;
    if (this._popupAction) {
        AjxTimedAction.cancelAction(this._popupAction);
        this._popupAction = null;
    }
	if (this._content != null && this._poppedUp) {
		Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		this._poppedUp = false;
	}
};

//
// Protected methods
//

DwtToolTip.prototype._positionElement = 
function(startX, startY) {
    this._popupAction = null;

    var element =  this._div;
	var baseId = "tooltip";
	var dialog = this._dialog;

	var WINDOW_GUTTER = 5;
	var POPUP_OFFSET_X = 8;
	var POPUP_OFFSET_Y = 8;

    var topPointer = document.getElementById(baseId+'TopPointer');
    topPointer.style.display = "block";

    var size = Dwt.getSize(topPointer),
		topPointerWidth = size.x,
		topPointerHeight = size.y
	;

	var bottomPointer = document.getElementById(baseId+'BottomPointer');
    bottomPointer.style.display = "block";
    
    size = Dwt.getSize(bottomPointer),
		bottomPointerWidth = size.x,
		bottomPointerHeight = size.y
	;

	var pointer = topPointer;

	var wdSize = DwtShell.getShell(window).getSize(),
		wdWidth = wdSize.x,
		wdHeight = wdSize.y
	;

	var popupSize = Dwt.getSize(element),
		popupWidth = popupSize.x,
		popupHeight = popupSize.y
	;

	var topBorderHeight = this._borderHeight,
		bottomBorderHeight = this._borderHeight,
		leftBorderWidth = this._borderWidth,
		rightBorderWidth = this._borderWidth
	;

	var popupX = startX - popupWidth / 2 - POPUP_OFFSET_X,
		popupY
	;
	
	var pointerY,
		pointerX,
		pointerWidth
	;

	// top pointer
	// NOTE: bottomPointerHeight added sbecause bottom pointer is absolute
    if (startY + POPUP_OFFSET_Y + topPointerHeight - topBorderHeight + popupHeight < wdHeight - WINDOW_GUTTER) {
        bottomPointer.style.display = "none";
		popupY = startY + POPUP_OFFSET_Y + topPointerHeight - topBorderHeight;
		pointerY = topBorderHeight - topPointerHeight;
		pointerWidth = topPointerWidth;
		pointer = topPointer;
	}
	
	// bottom pointer
	else {
        topPointer.style.display = "none";
        popupY = startY - POPUP_OFFSET_Y - bottomPointerHeight + bottomBorderHeight - popupHeight;
		pointerY = popupHeight - bottomBorderHeight;
		pointerWidth = bottomPointerWidth;
		pointer = bottomPointer;
	}

	// make sure popup is wide enough for pointer
	var contentEl = document.getElementById(baseId+"Contents");
	if (popupWidth - leftBorderWidth - rightBorderWidth < pointerWidth) {
		contentEl.width = pointerWidth; // IE
		contentEl.style.width = String(pointerWidth)+"px"; // everyone else
	} else {
		contentEl.width = "auto"; // IE
		contentEl.style.width = "auto"; // everyone else
	}
	
	// adjust popup x-location
	if (popupX < WINDOW_GUTTER) {
		popupX = WINDOW_GUTTER;
	}
	else if (popupX + popupWidth > wdWidth - WINDOW_GUTTER) {
		popupX = wdWidth - WINDOW_GUTTER - popupWidth;
	}
	
	// adjust pointer x-location
	pointerX = startX - popupX - pointerWidth / 2;
	if (pointerX + pointerWidth > popupWidth - rightBorderWidth) {
		pointerX = popupWidth - rightBorderWidth - pointerWidth;
	}
	if (pointerX < leftBorderWidth) {
		pointerX = leftBorderWidth;
	}

    pointer.style.left = pointerX;
	pointer.style.top = pointerY;

	Dwt.setLocation(element, popupX, popupY);
	var zIndex = dialog ? dialog.getZIndex() + Dwt._Z_INC : Dwt.Z_TOOLTIP;
	Dwt.setZIndex(element, zIndex);
    this._poppedUp = true;
};

DwtToolTip.prototype._mouseOverListener = 
function(ev) {
	if(this.isSticky) {
		return;
	}
    if (this._popdownOnMouseOver && this._poppedUp) {
        var callback = (this._popdownOnMouseOver instanceof AjxCallback) ? this._popdownOnMouseOver : null;
        this.popdown();
        if (callback)
            callback.run();
    }
};
