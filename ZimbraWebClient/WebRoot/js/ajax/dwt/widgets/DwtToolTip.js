/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


DwtToolTip = function(shell, className, dialog) {
	if (arguments.length == 0) return;
	this.shell = shell;
	this._dialog = dialog;
	this._poppedUp = false;
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
function(x, y, skipInnerHTML) {
	if (this._content != null) {
		if(!skipInnerHTML) {
            this._contentDiv.innerHTML = this._content;
        }

		var element = this._div;
		var baseId = "tooltip";
		var clip = true;
		var dialog = this._dialog;	
		this._positionElement(element, x, y, baseId, clip, dialog);
		this._poppedUp = true;
	}
};

DwtToolTip.prototype.popdown = 
function() {
	if (this._content != null && this._poppedUp) {
		Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		this._poppedUp = false;
	}
};

//
// Protected methods
//

DwtToolTip.prototype._positionElement = 
function(element, startX, startY, baseId, clip, dialog) {
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
    
    var size = Dwt.getSize(bottomPointer)
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
};
