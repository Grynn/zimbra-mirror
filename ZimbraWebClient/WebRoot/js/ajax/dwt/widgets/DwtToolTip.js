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
 * Singleton tooltip class.
 */
DwtToolTip = function(shell, className, dialog) {

	if (arguments.length == 0) { return; }

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
    var templateId = "dwt.Widgets#" + this._borderStyle;
    this._div.innerHTML = AjxTemplate.expand(templateId, "tooltip");

    var params = AjxTemplate.getParams(templateId);
    this._offsetX = (params.width != null) ? Number(params.width) : DwtToolTip.POPUP_OFFSET_X;
    this._offsetY = (params.height != null) ? Number(params.height) : DwtToolTip.POPUP_OFFSET_Y;

    // save reference to content div
    this._contentDiv = document.getElementById("tooltipContents");

    Dwt.setHandler(this._div, DwtEvent.ONMOUSEOVER, AjxCallback.simpleClosure(this._mouseOverListener, this));
};

DwtToolTip.prototype.isDwtToolTip = true;
DwtToolTip.prototype.toString = function() { return "DwtToolTip"; };

//
// Constants
//

DwtToolTip.TOOLTIP_DELAY = 750;

DwtToolTip.WINDOW_GUTTER = 5;	// space to leave between tooltip and edge of shell
DwtToolTip.POPUP_OFFSET_X = 5;	// default horizontal offset from control
DwtToolTip.POPUP_OFFSET_Y = 5;	// default vertical offset from control

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

/**
 * Shows the tooltip. By default, its position will be relative to the location of the
 * cursor when the mouseover event happened. Alternatively, the control that generated
 * the tooltip can be passed in, and the tooltip will be positioned relative to it. If
 * the control is a large composite control (eg a DwtListView), the hover event can be
 * passed so that the actual target of the event can be found.
 * 
 * @param {number}			x					X-coordinate of cursor
 * @param {number}			y					Y-coordinate of cursor
 * @param {boolean}			skipInnerHTML		if true, do not copy content to DOM
 * @param {boolean}			popdownOnMouseOver	if true, hide tooltip on mouseover
 * @param {DwtControl}		obj					control that tooltip is for (optional)
 * @param {DwtHoverEvent}	hoverEv				hover event (optional)
 */
DwtToolTip.prototype.popup = 
function(x, y, skipInnerHTML, popdownOnMouseOver, obj, hoverEv) {
    if (this._popupAction) {
        AjxTimedAction.cancelAction(this._popupAction);
        this._popupAction = null;
    }
	// popdownOnMouseOver may be true to pop down the tooltip if the mouse hovers over the tooltip. Optionally,
	// it can be an AjxCallback that will be called after popping the tooltip down.
    this._popdownOnMouseOver = popdownOnMouseOver;
    if (this._content != null) {
		if(!skipInnerHTML) {
            this._contentDiv.innerHTML = this._content;
        }

		this._popupAction = new AjxTimedAction(this, this._positionElement, [x, y, obj, hoverEv]);
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
	if (this._content != null && this._poppedUp && !this.isSticky) {
		Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		this._poppedUp = false;
	}
};

//
// Protected methods
//

// Positions the tooltip relative to the base element based on vertical and horizontal offsets.
DwtToolTip.prototype._positionElement = 
function(startX, startY, obj, hoverEv) {
	
    this._popupAction = null;
	
	var wdSize = DwtShell.getShell(window).getSize();
	var wdWidth = wdSize.x, wdHeight = wdSize.y;

	var tooltipX, tooltipY, baseLoc;
	var baseEl = obj && obj.getTooltipBase(hoverEv);
	if (baseEl) {
		baseLoc = Dwt.toWindow(baseEl);
		var baseSz = Dwt.getSize(baseEl);
		tooltipX = baseLoc.x + this._offsetX;
		tooltipY = baseLoc.y + baseSz.y + this._offsetY;
	}
	else {
		tooltipX = startX + this._offsetX;
		tooltipY = startY + this._offsetY;
	}

	var popupSize = Dwt.getSize(this._div);
	var popupWidth = popupSize.x, popupHeight = popupSize.y;

	// check for sufficient room to the right
	if (tooltipX + popupWidth > wdWidth - DwtToolTip.WINDOW_GUTTER) {
		tooltipX = wdWidth - DwtToolTip.WINDOW_GUTTER - popupWidth;
	}
	// check for sufficient room below
	if (tooltipY + popupHeight > wdHeight - DwtToolTip.WINDOW_GUTTER) {
		tooltipY = (baseLoc ? baseLoc.y : tooltipY) - this._offsetY - popupHeight;
	}

	Dwt.setLocation(this._div, tooltipX, tooltipY);
	var zIndex = this._dialog ? this._dialog.getZIndex() + Dwt._Z_INC : Dwt.Z_TOOLTIP;
	Dwt.setZIndex(this._div, zIndex);
    this._poppedUp = true;
};

DwtToolTip.prototype._mouseOverListener = 
function(ev) {
    if (this._popdownOnMouseOver && this._poppedUp && !this.isSticky) {
        var callback = (this._popdownOnMouseOver.isAjxCallback) ? this._popdownOnMouseOver : null;
        this.popdown();
        if (callback) {
            callback.run();
		}
    }
};
