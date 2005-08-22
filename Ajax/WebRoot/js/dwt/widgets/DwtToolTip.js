/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

function DwtToolTip(shell, className, dialog) {

	this.shell = shell;
	this._dialog = dialog;
	this._div = shell.getDocument().createElement("div");
	this._div.className = className || "DwtToolTip";
	this._div.style.position = DwtControl.ABSOLUTE_STYLE;
//	this._div.style.padding = "2px";
//	this._div.style.whiteSpace = "nowrap";
	this.shell.getHtmlElement().appendChild(this._div);
	this._popupAction = new AjxTimedAction();
	this._popupAction.method = DwtToolTip.prototype._popupToolTip;
	this._popdownAction = new AjxTimedAction();
	this._popdownAction.method = DwtToolTip.prototype._popdownToolTip;
	this._popupActionId = -1;
	this._popdownActionId = -1;
	Dwt.setZIndex(this._div, Dwt.Z_HIDDEN);
}

DwtToolTip._TOOLTIP_DELAY = 750;

DwtToolTip.prototype.toString = 
function() {
	return "DwtToolTip";
}

DwtToolTip.prototype.getContent =
function() {
	return this._div.innerHTML;
}

DwtToolTip.prototype.setContent =
function(content) {
	this._content = content;
}
	
DwtToolTip.prototype.mouseOver =
function(x, y, delay) {
	if (this._popdownActionId != -1) {
		AjxTimedAction.cancelAction(this._popdownActionId);
		this._popdownActionId = -1;
	}
	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);
		this._popupActionId = -1;
	}
	if (Dwt.getZIndex(this._div) == Dwt.Z_HIDDEN) {
		if (this._content != null && this._popupActionId == -1) {
			delay = (delay == null) ? DwtToolTip._TOOLTIP_DELAY : (delay > 0) ? delay : 0;
			this._popupAction.params.removeAll();
			this._popupAction.params.add(x);
			this._popupAction.params.add(y);
			this._popupAction.obj = this;
		 	this._popupActionId = AjxTimedAction.scheduleAction(this._popupAction, delay);
		}
	}
}

DwtToolTip.prototype.mouseMove =
function(delay) {
	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);
		this._popupAction.obj = this;
		delay = (delay == null) ? DwtToolTip._TOOLTIP_DELAY : (delay > 0) ? delay : 0;
	 	this._popupActionId = AjxTimedAction.scheduleAction(this._popupAction, delay);
	}
}

DwtToolTip.prototype.mouseDown =
function() {
	this._popdownToolTip();
}

DwtToolTip.prototype.mouseOut =
function(delay) {
	delay = delay || 0;
	if (this._popdownActionId == -1) {
		if (this._popupActionId != -1 || (this._content != null && (Dwt.getZIndex(this._div) != Dwt.Z_HIDDEN))) {	
			this._popdownAction.obj = this;
			delay = (delay == null) ? 50 : delay;
			if (delay > 0)
				this._popdownActionId = AjxTimedAction.scheduleAction(this._popdownAction, delay);
			else 
				this._popdownToolTip();
		}
	}
}

DwtToolTip.prototype._popupToolTip = 
function(x, y) {
	if (this._content != null) {
		var divContent = DwtBorder.getBorderStartHtml("TL_balloon") + 
							this._content + 
						 DwtBorder.getBorderEndHtml("TL_balloon");
		this._div.innerHTML = divContent;
		
		var WINDOW_GUTTER = 10;

		var ex = x;
		var ey = y;

		var w = this.shell.getSize();
		var ww = w.x;
		var wh = w.y;

		var p = Dwt.getSize(this._div);
		var pw = p.x;
		var ph = p.y;
		
		var px = ex;
		var py = ey;

		// HACK: need to hard-code the tip dimensions
		var tw = 20;
		var th = 70;
		
		/***
		DBG.println(
			"---<br>"+
			"event = &lt;"+ex+","+ey+"&gt;<br>"+
			"window = "+ww+"x"+wh+"<br>"+
			"popup = "+pw+"x"+ph
		);
		/***/

		// adjust popup position
		if (ex - tw < WINDOW_GUTTER) {
			px = WINDOW_GUTTER + tw;
		}
		else if (ex + tw + pw > ww - WINDOW_GUTTER) {
			px = ex - pw - tw;
			var borderStyle = "TR_balloon";
			var divContent = DwtBorder.getBorderStartHtml(borderStyle) + 
							this._content + 
						DwtBorder.getBorderEndHtml(borderStyle);
			this._div.innerHTML = divContent;
		}

		if (py < WINDOW_GUTTER) {
			py = WINDOW_GUTTER;
		}
		else if (py + ph + th > wh - WINDOW_GUTTER) {
			py = ey - ph - th;
			var borderStyle = ex + tw + pw > ww - WINDOW_GUTTER ? "BR_balloon" : "BL_balloon";
			var divContent = DwtBorder.getBorderStartHtml(borderStyle) + 
							this._content + 
						DwtBorder.getBorderEndHtml(borderStyle);
			this._div.innerHTML = divContent;
		}

		// NOTE: Firefox shows the tooltip in its original position before
		//		 moving it. This causes it to "jump" between the old and new
		//		 position.
		this._div.style.display = "none";
		Dwt.setLocation(this._div, px, py);
		var zIndex = this._dialog ? this._dialog.getZIndex() + Dwt.Z_INC : Dwt.Z_TOOLTIP;
		Dwt.setZIndex(this._div, zIndex);
		this._div.style.display = "block";
	}
	this._popupActionId = -1;
}

DwtToolTip.prototype._popdownToolTip = 
function() {
	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);	
		this._popupActionId = -1;
	}
	if (this._content != null) {
		Dwt.setZIndex(this._div, Dwt.Z_HIDDEN);
	}
	this._popdownActionId = -1;
}
