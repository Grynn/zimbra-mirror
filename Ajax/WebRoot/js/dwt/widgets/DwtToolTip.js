/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
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
	
	var borderStyle = AjxEnv.isIE ? "hover_IE" : "hover";
	var substitutions = { id: "tooltip" };
	this._borderStart = DwtBorder.getBorderStartHtml(borderStyle, substitutions);
	this._borderEnd = DwtBorder.getBorderEndHtml(borderStyle, substitutions);	
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
function(delay, x, y) {
	if (this._popupActionId != -1) {
		AjxTimedAction.cancelAction(this._popupActionId);
		this._popupAction.obj = this;
		delay = (delay == null) ? DwtToolTip._TOOLTIP_DELAY : (delay > 0) ? delay : 0;
		if (x) {
			this._popupAction.params.removeAll();
			this._popupAction.params.add(x);
			this._popupAction.params.add(y);
		}
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
		this._div.innerHTML = this._borderStart + this._content + this._borderEnd;
	
		var WINDOW_GUTTER = 5;
		var POPUP_OFFSET_X = 8;
		var POPUP_OFFSET_Y = 8;

		var tt = document.getElementById('tooltip_tip_t');
		var tb = document.getElementById('tooltip_tip_b');
		var t = tt;

		var ex = x;
		var ey = y;

		var w = this.shell.getSize();
		var ww = w.x;
		var wh = w.y;

		var p = Dwt.getSize(this._div);
		var pw = p.x;
		var ph = p.y;

		var btEl = document.getElementById('tooltip_border_tm');
		var blEl = document.getElementById('tooltip_border_ml');
		var brEl = document.getElementById('tooltip_border_mr');
		var bbEl = document.getElementById('tooltip_border_bm');

		var bth = Dwt.getSize(btEl).y;
		var blw = Dwt.getSize(blEl).x;
		var brw = Dwt.getSize(brEl).x;
		var bbh = Dwt.getSize(bbEl).y;

		var ttw = Dwt.getSize(tt).x;
		var tth = Dwt.getSize(tt).y;
		var tbw = Dwt.getSize(tb).x;
		var tbh = Dwt.getSize(tb).y;

		/***
		DBG.println(
			"---<br>"+
		    "event: &lt;"+ex+","+ey+"><br>"+
			"window: "+ww+"x"+wh+"<br>"+
		    "popup: "+pw+"x"+ph+"<br>"+
		    "borders: top="+btEl+", left="+blEl+", right="+brEl+", bottom="+bbEl+"<br>"+
		    "borders: top="+bth+", left="+blw+", right="+brw+", bottom="+bbh+"<br>"+
		    "tip: top="+ttw+"x"+tth+", bottom="+tbw+"x"+tbh
	    );
	    /***/

		var px = ex - pw / 2 - POPUP_OFFSET_X;
		var py;
		
		var ty;
		var tw;

		// tip up
		var adjust = tbh; // NOTE: because bottom tip is relative
		if (ph + ey + tth - bth + POPUP_OFFSET_Y < wh - WINDOW_GUTTER + adjust) {
			py = ey + tth - bth + POPUP_OFFSET_Y;
			tb.style.display = "none";
			ty = bth - tth;
			tw = ttw;
			t = tt;
		}
		
		// tip down
		else {
			py = ey - ph - tbh + bbh - POPUP_OFFSET_Y;
			py += tbh; // NOTE: because bottom tip is relative
			tt.style.display = "none";
			ty = -bbh;
			tw = tbw;
			t = tb;
		}
		//DBG.println("position: &lt;"+px+","+py+">");

		// make sure popup is wide enough for tip graphic
		if (pw - blw - brw < tw) {
			var contentEl = document.getElementById("tooltip_contents");
			contentEl.width = tw; // IE
			contentEl.style.width = String(tw)+"px"; // everyone else
		}
		
		// adjust popup x-location
		if (px < WINDOW_GUTTER) {
			px = WINDOW_GUTTER;
		}
		else if (px + pw > ww - WINDOW_GUTTER) {
			px = ww - WINDOW_GUTTER - pw;
		}
		//DBG.println("position: &lt;"+px+","+py+">");
		
		// adjust tip x-location
		var tx = ex - px - tw / 2;
		//DBG.println("tip: &lt;"+tx+","+ty+">");
		if (tx + tw > pw - brw) {
			tx = pw - brw - tw;
		}
		if (tx < blw) {
			tx = blw;
		}
		//DBG.println("tip: &lt;"+tx+","+ty+">");

		t.style.left = tx;
		t.style.top = ty;
		if (t == tb) {
			var y = t.offsetTop;//Dwt.getLocation(t).y;
			this._div.style.clip = "rect(auto,auto,"+(y + tbh)+",auto)";
		}
		else {
			this._div.style.clip = "rect(auto,auto,auto,auto)";
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
