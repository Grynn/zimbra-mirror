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


function DwtToolTip(shell, className, dialog) {

	this.shell = shell;
	this._dialog = dialog;
	this._poppedUp = false;
	this._div = document.createElement("div");
	this._div.className = className || "DwtToolTip";
	this._div.style.position = DwtControl.ABSOLUTE_STYLE;
	this.shell.getHtmlElement().appendChild(this._div);
	Dwt.setZIndex(this._div, Dwt.Z_HIDDEN);
	Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	var substitutions = { id: "tooltip" };
	this._borderStart = DwtBorder.getBorderStartHtml(this._borderStyle, substitutions);
	this._borderEnd = DwtBorder.getBorderEndHtml(this._borderStyle, substitutions);
	this._borderWidth = DwtBorder.getBorderWidth(this._borderStyle);
	this._borderHeight = DwtBorder.getBorderHeight(this._borderStyle);
}

DwtToolTip.prototype._borderStyle = "DwtToolTip";

DwtToolTip.TOOLTIP_DELAY = 750;

DwtToolTip.prototype.toString = 
function() {
	return "DwtToolTip";
}

DwtToolTip.prototype.getContent =
function() {
	return this._div.innerHTML;
}

DwtToolTip.prototype.setContent =
function(content, setInnerHTML) {
	this._content = content;
	if(setInnerHTML) {
		this._div.innerHTML = this._borderStart + this._content + this._borderEnd;
	}
}
	
DwtToolTip.prototype.popup = 
function(x, y, skipInnerHTML) {
	if (this._content != null) {
		if(!skipInnerHTML) {
			this._div.innerHTML = this._borderStart + this._content + this._borderEnd;
		}

		var element = this._div;
		var baseId = "tooltip";
		var clip = true;
		var dialog = this._dialog;	
		this._positionElement(element, x, y, baseId, clip, dialog);
		this._poppedUp = true;
	}
}

DwtToolTip.prototype.popdown = 
function() {
	if (this._content != null && this._poppedUp) {
		Dwt.setLocation(this._div, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
		this._poppedUp = false;
	}
}

DwtToolTip.prototype._positionElement = 
function(element, startX, startY, baseId, clip, dialog) {
	var WINDOW_GUTTER = 5;
	var POPUP_OFFSET_X = 8;
	var POPUP_OFFSET_Y = 8;

	var topPointer = document.getElementById(baseId+'TopPointer'),
		size = Dwt.getSize(topPointer),
		topPointerWidth = size.x,
		topPointerHeight = size.y
	;

	var bottomPointer = document.getElementById(baseId+'BottomPointer'),
		size = Dwt.getSize(bottomPointer)
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


/*
	if (AjxEnv.useTransparentPNGs) {
		var bsEl = document.getElementById(baseId+'_border_shadow_b');
		var bsh = Dwt.getSize(bsEl).y;
	}
*/

	/***
	DBG.println(
		"---<br>"+
	    "event: &lt;"+startX+","+startY+"><br>"+
		"window: "+wdWidth+"x"+wdHeight+"<br>"+
	    "popup: "+popupWidth+"x"+popupHeight+"<br>"+
	    "borders: top="+btEl+", left="+blEl+", right="+brEl+", bottom="+bbEl+"<br>"+
	    "borders: top="+topBorderHeight+", left="+leftBorderWidth+", right="+rightBorderWidth+", bottom="+bottomBorderHeight+"<br>"+
	    "tip: top="+topPointerWidth+"x"+topPointerHeight+", bottom="+bottomPointerWidth+"x"+bottomPointerHeight
    );
    /***/

	var popupX = startX - popupWidth / 2 - POPUP_OFFSET_X,
		popupY
	;
	
	var pointerY,
		pointerX,
		pointerWidth
	;

	// top pointer
	// NOTE: bottomPointerHeight added sbecause bottom pointer is relative
	if (popupHeight + startY + topPointerHeight - topBorderHeight + POPUP_OFFSET_Y < wdHeight - WINDOW_GUTTER + bottomPointerHeight) {
		popupY = startY + topPointerHeight - topBorderHeight + POPUP_OFFSET_Y;
		bottomPointer.style.display = "none";
		pointerY = topBorderHeight - topPointerHeight;
		pointerWidth = topPointerWidth;
		pointer = topPointer;
	}
	
	// bottom pointer
	else {
		popupY = startY - popupHeight - bottomPointerHeight + bottomBorderHeight - POPUP_OFFSET_Y;
		popupY += bottomPointerHeight; // NOTE: because bottom pointer is relative
		topPointer.style.display = "none";
		pointerY = -bottomBorderHeight;
		if (AjxEnv.useTransparentPNGs) {
			pointerY -= bsh;
		}
		pointerWidth = bottomPointerWidth;
		pointer = bottomPointer;
	}

	// make sure popup is wide enough for pointer
	if (popupWidth - leftBorderWidth - rightBorderWidth < pointerWidth) {
		var contentEl = document.getElementById(baseId+"Contents");
		contentEl.width = pointerWidth; // IE
		contentEl.style.width = String(pointerWidth)+"popupX"; // everyone else
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
	if (clip) {
		if (pointer == bottomPointer) {
			element.style.clip = "rect(auto,auto,"+(pointer.offsetTop + bottomPointerHeight)+",auto)";
		}
		else {
			element.style.clip = "rect(auto,auto,auto,auto)";
		}
	}

	Dwt.setLocation(element, popupX, popupY);
	var zIndex = dialog ? dialog.getZIndex() + Dwt._Z_INC : Dwt.Z_TOOLTIP;
	Dwt.setZIndex(element, zIndex);
}
