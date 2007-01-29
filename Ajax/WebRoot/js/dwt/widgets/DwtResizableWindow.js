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

/**
 * @constructor
 * @class
 *
 * A non-modal window that can be resized from all edges and corners.  Multiple
 * windows can be displayed at once.  There's always only one active window.
 * Internally it keeps track of active windows and (de)activates them as
 * necessary when windows are created or destroyed.
 *
 * Unlike DwtDialog this kind of window doesn't have a title.
 *
 * Normally you would want to display only one widget inside such a window.  It
 * can be a DwtComposite to contain more.  Use setView() for this.
 *
 * @author Mihai Bazon / mihai@zimbra.com
 */

function DwtResizableWindow(parent, className) {
	if (!className)
		className = "DwtResizableWindow";

	this._loc = DwtResizableWindow.computeNewLoc();
	DwtComposite.call(this, parent, className, DwtControl.ABSOLUTE_STYLE);

	this.addControlListener(new AjxListener(this, this.__onResize));
	this.addDisposeListener(new AjxListener(this, this.__onDispose));
};

DwtResizableWindow.prototype = new DwtComposite;
DwtResizableWindow.prototype.constructor = DwtResizableWindow;

DwtResizableWindow.VISIBLE_WINDOWS = new AjxVector();

DwtResizableWindow.HTML = [
	"<div class='DwtResizableWindow-topcont'>",
	"<table class='DwtResizableWindow-handles'>",

	"<tr class='DwtResizableWindow-handles-top'>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(1, null, event)' class='DwtResizableWindow-NW'></td>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(2, null, event)' class='DwtResizableWindow-N'></td>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(3, null, event)' class='DwtResizableWindow-NE'></td>",
	"</tr>",

	"<tr class='DwtResizableWindow-handles-mid'>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(4, null, event)' class='DwtResizableWindow-W'></td>",
	"<td class='DwtResizableWindow-C'></td>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(6, null, event)' class='DwtResizableWindow-E'></td>",
	"</tr>",

	"<tr class='DwtResizableWindow-handles-bot'>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(7, null, event)' class='DwtResizableWindow-SW'></td>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(8, null, event)' class='DwtResizableWindow-S'></td>",
	"<td onmousedown='DwtResizableWindow.__static_handleMouseDown(9, null, event)' class='DwtResizableWindow-SE'></td>",
	"</tr>",

	"</table>",
	"<div class='DwtResizableWindow-cont'></div>",
	"<div class='DwtResizableWindow-inactiveCover' onclick='DwtResizableWindow.__static_dlgMouseDown(event)'></div>",
	"</div>"
].join("");

DwtResizableWindow.getActiveWindow = function() {
	return DwtResizableWindow.VISIBLE_WINDOWS.getLast();
};

DwtResizableWindow.computeNewLoc = function() {
	// cascade
	var win = DwtResizableWindow.getActiveWindow();
	if (!win)
		return { x: 0, y: 0 };
	// do cascade for now; perhaps it would be nice to implement a smart positioning algorithm
	return { x: win._loc.x + 20, y: win._loc.y + 20 };
};

DwtResizableWindow.prototype.setView = function(newView) {
	this._view = newView;
	if (newView)
		this._getContentDiv().appendChild(newView.getHtmlElement());
};

DwtResizableWindow.prototype.popup = function(pos) {
	if (!pos)
		pos = this._loc;
	if (!pos)
		pos = { x: 0, y: 0 };
	this._visible = true;
	this.setLocation(pos.x, pos.y);
	this.setActive(true);
};

DwtResizableWindow.prototype.popdown = function() {
	this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	this._visible = false;
	var a = DwtResizableWindow.VISIBLE_WINDOWS;
	a.remove(this);
	if (a.getLast())
		a.getLast().setActive(true, true);
};

DwtResizableWindow.prototype.setActive = function(val, dontInactivateLast) {
	var a = DwtResizableWindow.VISIBLE_WINDOWS;
	if (val) {
		if (!dontInactivateLast) {
			var last = a.getLast();
			if (last)
				last.setActive(false);
		}
		a.remove(this);
		a.add(this);
		Dwt.addClass(this.getHtmlElement(), "DwtResizableWindow-focused");
		this.focus();
	} else {
		Dwt.delClass(this.getHtmlElement(), "DwtResizableWindow-focused");
	}
};

DwtResizableWindow.prototype.setLocation = function(x, y) {
	if (this._visible) {
		DwtComposite.prototype.setLocation.call(this, x, y);
	}
	if (x != Dwt.LOC_NOWHERE)
		this._loc.x = x;
	if (y != Dwt.LOC_NOWHERE)
		this._loc.y = y;
};

DwtResizableWindow.prototype.enableMoveWithElement = function(el) {
	if (el instanceof DwtControl)
		el = el.getHtmlElement();
	el.onmousedown = AjxCallback.simpleClosure(DwtResizableWindow.__static_handleMouseDown,
						   null, 5, this);
};


// Protected

DwtResizableWindow.prototype._getContentDiv = function() {
	// BEWARE: don't mess with the HTML code above or this might not work correctly
	return this.getHtmlElement().firstChild.childNodes[1];
};

// Private

DwtResizableWindow.prototype.__initCtrl = function() {
	DwtComposite.prototype.__initCtrl.call(this);
	var el = this.getHtmlElement();

	this._visible = true;
	this.popdown();

	el.innerHTML = DwtResizableWindow.HTML;

	this.__captureObj = new DwtMouseEventCapture(
		this, "DwtResizableWindow",
		null,		// mouseover
		null,		// mousedown
		DwtResizableWindow.__static_resizeMouseMove,
		DwtResizableWindow.__static_resizeMouseUp,
		null,		// mouseout
		true		// hard capture
	);
};

DwtResizableWindow.prototype.__dlgMouseDown = function(ev) {
	this.setActive(true);
};

DwtResizableWindow.prototype.__handleMouseDown = function(ev, side) {
	this.__resizing = { side  : side,
			    evpos : { x: ev.docX,
				      y: ev.docY },
			    size  : this.getSize(),
			    wpos  : this.getLocation()
	};
	this.__captureObj.capture();
};

DwtResizableWindow.prototype.__resizeMouseMove = function(ev) {
	var r = this.__resizing;
	var dx = ev.docX - r.evpos.x;
	var dy = ev.docY - r.evpos.y;

	switch (r.side) {

	    case 1:		// NW
		this.setSize(r.size.x - dx, r.size.y - dy);
		this.setLocation(r.wpos.x + dx, r.wpos.y + dy);
		break;

	    case 2:		// N
		this.setSize(Dwt.DEFAULT, r.size.y - dy);
		this.setLocation(Dwt.DEFAULT, r.wpos.y + dy);
		break;

	    case 3:		// NE
		this.setSize(r.size.x + dx, r.size.y - dy);
		this.setLocation(Dwt.DEFAULT, r.wpos.y + dy);
		break;

	    case 4:		// W
		this.setSize(r.size.x - dx, Dwt.DEFAULT);
		this.setLocation(r.wpos.x + dx, Dwt.DEFAULT);
		break;

	    case 5:		// MOVE! :-)
		this.setLocation(r.wpos.x + dx, r.wpos.y + dy);
		break;

	    case 6:		// E
		this.setSize(r.size.x + dx, Dwt.DEFAULT);
		break;

	    case 7:		// SW
		this.setSize(r.size.x - dx, r.size.y + dy);
		this.setLocation(r.wpos.x + dx, Dwt.DEFAULT);
		break;

	    case 8:		// S
		this.setSize(Dwt.DEFAULT, r.size.y + dy);
		break;

	    case 9:		// SE
		this.setSize(r.size.x + dx, r.size.y + dy);
		break;
	}
};

DwtResizableWindow.prototype.__resizeMouseUp = function(ev) {
	this.__resizing = null;
	this.__captureObj.release();
};

DwtResizableWindow.prototype.__onResize = function(ev) {
	var div = this._getContentDiv();
	if (this._view) {
		this._view.setSize(div.offsetWidth, div.offsetHeight);
	}
};

DwtResizableWindow.prototype.__onDispose = function(ev) {
	this.popdown();
};









/* Static and internal */

DwtResizableWindow.__static_handleMouseDown = function(side, obj, ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	if (mouseEv.button == DwtMouseEvent.LEFT) {
		if (!obj)
			obj = DwtUiEvent.getDwtObjFromEvent(mouseEv);
		obj.__handleMouseDown(mouseEv, side);
        }
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
        return false;
};

DwtResizableWindow.__static_dlgMouseDown = function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	DwtUiEvent.getDwtObjFromEvent(mouseEv).__dlgMouseDown(mouseEv);
};

DwtResizableWindow.__static_resizeMouseMove = function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	DwtMouseEventCapture.getTargetObj().__resizeMouseMove(mouseEv);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
};

DwtResizableWindow.__static_resizeMouseUp = function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	DwtMouseEventCapture.getTargetObj().__resizeMouseUp(mouseEv);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
};
