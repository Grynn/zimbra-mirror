/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
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

// note, DwtWindowManager is also in this file

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

DwtResizableWindow = function(parent, className) {
	if (!className)
		className = "DwtResizableWindow";

	this._minPos = null;
	this._minSize = null;
	this._maxPos = true;	// "true" means restrict to parent size
	this._loc = { x: 0, y: 0 };
	this._visible = false;
	this._active = null;
	DwtComposite.call(this, parent, className, DwtControl.ABSOLUTE_STYLE);

	this.addControlListener(new AjxListener(this, this.__onControlEvent));
	this.addDisposeListener(new AjxListener(this, this.__onDispose));
};

DwtResizableWindow.prototype = new DwtComposite;
DwtResizableWindow.prototype.constructor = DwtResizableWindow;

DwtResizableWindow.HTML = [
	"<div onmousedown='DwtResizableWindow.__static_dlgMouseDown(event)' class='DwtResizableWindow-topcont'>",
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
//	"<div class='DwtResizableWindow-inactiveCover' onmousedown='DwtResizableWindow.__static_dlgMouseDown(event)'></div>",
	"</div>"
].join("");

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
	var maxPos = this._maxPos;
	if (maxPos === true) {
		maxPos = this.parent.getSize();
		var tmp = this.getSize();
		maxPos.x -= tmp.x + 4;
		maxPos.y -= tmp.y + 4;
	}
	if (maxPos) {
		if (pos.x != null && maxPos.x != null)
			if (pos.x > maxPos.x)
				pos.x = maxPos.x;
		if (pos.y != null && maxPos.y != null)
			if (pos.y > maxPos.y)
				pos.y = maxPos.y;
	}
        var wasVisible = this._visible;
        this._visible = true;
	this.setLocation(pos.x, pos.y);
	if (!wasVisible && this.isListenerRegistered(DwtEvent.POPUP))
		this.notifyListeners(DwtEvent.POPUP, { dwtObj: this });
	this.setActive(true);
};

DwtResizableWindow.prototype.popdown = function() {
	// this.setActive(false); // heh, the element vanished by this time
	this.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	this._visible = false;
	if (this.isListenerRegistered(DwtEvent.POPDOWN))
		this.notifyListeners(DwtEvent.POPDOWN, { dwtObj: this });
};

DwtResizableWindow.prototype.focus = function() {
	DwtComposite.prototype.focus.call(this);
	if (this.isListenerRegistered("myFOCUS")) {
		this.notifyListeners("myFOCUS", { dwtObj: this });
	}
};

DwtResizableWindow.prototype.blur = function() {
	// hmm, this doesn't exist.
	// DwtComposite.prototype.blur.call(this);
	if (this.isListenerRegistered("myBLUR")) {
		this.notifyListeners("myBLUR", { dwtObj: this });
	}
};

DwtResizableWindow.prototype.setActive = function(val) {
	var el = this.getHtmlElement();
	this._active = !!val;
	Dwt.condClass(el, val, "DwtResizableWindow-focused", "DwtResizableWindow-unfocused");
	if (val) {
		this.focus();
	} else {
		this.blur();
	}
	// FIXME: should we call this.focus() instead of triggering SELECTION event?
	if (this.isListenerRegistered(DwtEvent.SELECTION)) {
		var selEv = DwtShell.selectionEvent;
		selEv.item = this;
		selEv.detail = val; // true when active
		this.notifyListeners(DwtEvent.SELECTION, selEv);
	}
};

DwtResizableWindow.prototype.setLocation = function(x, y) {
	if (this._visible) {
		DwtComposite.prototype.setLocation.call(this, x, y);
	}
	if (x != Dwt.LOC_NOWHERE && x != Dwt.DEFAULT)
		this._loc.x = x;
	if (y != Dwt.LOC_NOWHERE && y != Dwt.DEFAULT)
		this._loc.y = y;
};

DwtResizableWindow.prototype.enableMoveWithElement = function(el) {
	if (el instanceof DwtControl)
		el = el.getHtmlElement();
	el.onmousedown = AjxCallback.simpleClosure(DwtResizableWindow.__static_handleMouseDown,
						   null, 5, this);
};

DwtResizableWindow.prototype.getWindowManager = function() {
	return this._windowManager;
};

DwtResizableWindow.prototype.getSize = function() {
        if (!this._gotSize)
                this._gotSize = DwtComposite.prototype.getSize.call(this);
        return { x: this._gotSize.x,
                 y: this._gotSize.y }; // return a copy
};

/* BEGIN: listeners */

// FOCUS
DwtResizableWindow.prototype.addFocusListener = function(listener) {
	this.addListener("myFOCUS", listener);
};
DwtResizableWindow.prototype.removeFocusListener = function(listener) {
	this.removeListener("myFOCUS", listener);
};

// BLUR
DwtResizableWindow.prototype.addBlurListener = function(listener) {
	this.addListener("myBLUR", listener);
};
DwtResizableWindow.prototype.removeBlurListener = function(listener) {
	this.removeListener("myBLUR", listener);
};

// POPUP
DwtResizableWindow.prototype.addPopupListener = function(listener) {
	this.addListener(DwtEvent.POPUP, listener);
};
DwtResizableWindow.prototype.removePopupListener = function(listener) {
	this.removeListener(DwtEvent.POPUP, listener);
};

// POPDOWN
DwtResizableWindow.prototype.addPopdownListener = function(listener) {
	this.addListener(DwtEvent.POPDOWN, listener);
};
DwtResizableWindow.prototype.removePopdownListener = function(listener) {
	this.removeListener(DwtEvent.POPDOWN, listener);
};

// SELECTION (triggers both when FOCUS on BLUR, passing ev.detail = true if focused */
DwtResizableWindow.prototype.addSelectionListener = function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};
DwtResizableWindow.prototype.removeSelectionListener = function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);
};
DwtResizableWindow.prototype.removeSelectionListeners = function() {
	this.removeAllListeners(DwtEvent.SELECTION);
};

/* END: listeners */

DwtResizableWindow.prototype.setMinPos = function(x, y) {
	this._minPos = { x: x, y: y };
};

DwtResizableWindow.prototype.setMinSize = function(w, h) {
	this._minSize = { x: w, y: h }; // let's keep using x and y for uniformity
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
	el.isDwtResizableWindow = true;

	Dwt.addClass(el, "DwtResizableWindow-unfocused");

	this._visible = true;
	this.popdown();

	el.innerHTML = DwtResizableWindow.HTML;

        if (!AjxEnv.isIE) {
                // for capable browsers we can catch events at capture
                // phase and make sure we activate the dialog upon
                // mousedown when there are widgets that disable bubbling.
                el.firstChild.onmousedown = null;
                el.firstChild.addEventListener("mousedown", DwtResizableWindow.__static_dlgMouseDown, true);
        }

	this.__captureObj = new DwtMouseEventCapture(
		this, "DwtResizableWindow",
		null,		// mouseover
		null,		// mousedown
		DwtResizableWindow.__static_resizeMouseMove,
		DwtResizableWindow.__static_resizeMouseUp,
		null,		// mouseout
		true		// hard capture
	);

	// this._setMouseEventHdlrs();
	// this.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this.__dlgMouseDown));

	// this.enableMoveWithElement(this._getContentDiv().nextSibling);
};

DwtResizableWindow.prototype.__dlgMouseDown = function(ev) {
	this.setActive(true);
};

DwtResizableWindow.prototype.__handleMouseDown = function(ev, side) {
	if (side != null) {
		Dwt.addClass(this.getHtmlElement(), "DwtResizableWindow-resizing");
		this.__resizing = { side  : side,
				    evpos : { x: ev.docX,
					      y: ev.docY },
				    size  : this.getSize(),
				    wpos  : this.getLocation()
				  };
		this.__captureObj.capture();
	}
};

DwtResizableWindow.prototype.__resizeMouseMove = function(ev) {
	var r = this.__resizing;
	var dx = ev.docX - r.evpos.x;
	var dy = ev.docY - r.evpos.y;

	var x = null, y = null, width = null, height = null;

	switch (r.side) {

	    case 1:		// NW
		width = r.size.x - dx;
		height = r.size.y - dy;
		x = r.wpos.x + dx;
		y = r.wpos.y + dy;
		break;

	    case 2:		// N
		height = r.size.y - dy;
		y = r.wpos.y + dy;
		break;

	    case 3:		// NE
		width = r.size.x + dx;
		height = r.size.y - dy;
		y = r.wpos.y + dy;
		break;

	    case 4:		// W
		width = r.size.x - dx;
		x = r.wpos.x + dx;
		break;

	    case 5:		// MOVE! :-)
		x = r.wpos.x + dx;
		y = r.wpos.y + dy;
		break;

	    case 6:		// E
		width = r.size.x + dx;
		break;

	    case 7:		// SW
		width = r.size.x - dx;
		height = r.size.y + dy;
		x = r.wpos.x + dx;
		break;

	    case 8:		// S
		height = r.size.y + dy;
		break;

	    case 9:		// SE
		width = r.size.x + dx;
		height = r.size.y + dy;
		break;
	}

	var maxPos = this._maxPos;
	if (maxPos === true) {
		// restrict to parent
		maxPos = this.parent.getSize();
		var tmp = this.getSize() || (new DwtPoint(0,0));
		if (width != null)
			tmp.x = width;
		if (height != null)
			tmp.y = height;
		maxPos.x -= tmp.x + 4;
		maxPos.y -= tmp.y + 4;
	}

	if (maxPos) {
		if (x != null && maxPos.x != null)
			if (x > maxPos.x) {
				x = maxPos.x
				width = null;
			}
		if (y != null && maxPos.y != null)
			if (y > maxPos.y) {
				y = maxPos.y;
				height = null;
			}
	}

	if (this._minPos) {
		if (x != null && this._minPos.x != null)
			if (x < this._minPos.x) {
				x = this._minPos.x;
				width = null;
			}
		if (y != null && this._minPos.y != null)
			if (y < this._minPos.y) {
				y = this._minPos.y;
				height = null;
			}
	}

	if (this._minSize) {
		if (width != null && this._minSize.x != null)
			if (width < this._minSize.x)
				width = this._minSize.x;
		if (height != null && this._minSize.y != null)
			if (height < this._minSize.y)
				height = this._minSize.y;
	}

	if (width != null || height != null)
		this.setSize(width == null ? Dwt.DEFAULT : width,
			     height == null ? Dwt.DEFAULT : height);

	if (x != null || y != null)
		this.setLocation(x == null ? Dwt.DEFAULT : x,
				 y == null ? Dwt.DEFAULT : y);
};

DwtResizableWindow.prototype.__resizeMouseUp = function(ev) {
	this.__resizing = null;
	this.__captureObj.release();
	Dwt.delClass(this.getHtmlElement(), "DwtResizableWindow-resizing");
};

DwtResizableWindow.prototype.__onControlEvent = function(ev) {
	if (ev.type & DwtControlEvent.RESIZE) {
                this._gotSize = { x: ev.newWidth,
                                  y: ev.newHeight };
		var div = this._getContentDiv();
		if (AjxEnv.isIE) {
			try {
				// for other browsers this is not necessary (see dwt.css)
				var w = ev.newWidth - 2 * div.offsetLeft - 2;
				var h = ev.newHeight - 2 * div.offsetTop - 1;
				div.style.width = w + "px";
				div.style.height = h + "px";
				var el = div.firstChild;
				el.style.width = w + "px";
				el.style.height = h + "px";
			} catch(ex) {}
		}
		if (this._view) {
			this._view.setSize(div.offsetWidth, div.offsetHeight);
		}
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
			obj = DwtUiEvent.getDwtObjWithProp(mouseEv, "isDwtResizableWindow");
		obj.__dlgMouseDown(ev);
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
	DwtUiEvent.getDwtObjWithProp(mouseEv, "isDwtResizableWindow").__dlgMouseDown(mouseEv);
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


/**
 * @constructor
 * @class
 *
 * A DwtWindowManager maintains a list of windows.  It sets hooks to
 * automatically focus the next window when one was hidden/destroyed, etc.
 *
 * @author Mihai Bazon / mihai@zimbra.com
 */

DwtWindowManager = function(parent) {
	if (arguments.length > 0) {
		DwtComposite.call(this, parent, "DwtWindowManager", Dwt.ABSOLUTE_STYLE);

		// all managed windows are present in this array
		this.all_windows = new AjxVector();

		// this one maintains the visible windows.  the correct
		// z-order is also given by this array (ultimately, the last
		// window in this array is the focused one and should be on
		// top)
		this.visible_windows = new AjxVector();

		this.active_index = -1;

		this._windowPopupListener = new AjxListener(this, this._windowPopupListener);
		this._windowPopdownListener = new AjxListener(this, this._windowPopdownListener);
		this._windowFocusListener = new AjxListener(this, this._windowFocusListener);
		this._windowBlurListener = new AjxListener(this, this._windowBlurListener);
		this._windowDisposeListener = new AjxListener(this, this._windowDisposeListener);

		this.setZIndex(Dwt.Z_WINDOW_MANAGER);
	}
};

DwtWindowManager.prototype = new DwtComposite;
DwtWindowManager.prototype.constructor = DwtWindowManager;

DwtWindowManager.prototype.getActiveWindow = function() {
	return this.visible_windows.get(this.active_index);
};

DwtWindowManager.prototype.getWindowByType = function(type) {
        var a = this.visible_windows.sub(function(w) {
                return !(w instanceof type);
        });
        return a.get(0);
};

DwtWindowManager.prototype.computeNewLoc = function() {
	// cascade
	var win = this.getActiveWindow();
	if (!win)
		return { x: 0, y: 0 };
	// do cascade for now; it would be nice to implement a
	// smart positioning algorithm
	return { x: win._loc.x + 25, y: win._loc.y + 25 };
};

// call this BEFORE calling drw.popup() in order for the new window to be properly managed
DwtWindowManager.prototype.manageWindow = function(drw, pos) {
	if (!this.all_windows.contains(drw)) {
		if (drw._windowManager)
			drw._windowManager.unmanageWindow(drw);
		if (drw.parent !== this)
			drw.reparent(this);
		this.all_windows.add(drw);
		drw.addPopupListener(this._windowPopupListener);
		drw.addPopdownListener(this._windowPopdownListener);
		drw.addFocusListener(this._windowFocusListener);
		drw.addBlurListener(this._windowBlurListener);
		drw.addDisposeListener(this._windowDisposeListener);
		drw._windowManager = this;
		if (pos == null)
			pos = this.computeNewLoc(drw);
		drw.popup(pos);
	}
};

DwtWindowManager.prototype.unmanageWindow = function(drw) {
	if (this.all_windows.contains(drw)) {
		drw.setActive(false);
		drw.popdown();
		this.all_windows.remove(drw);
		this.visible_windows.remove(drw);
		drw.removePopupListener(this._windowPopupListener);
		drw.removePopdownListener(this._windowPopdownListener);
		drw.removeFocusListener(this._windowFocusListener);
		drw.removeBlurListener(this._windowBlurListener);
		drw.removeDisposeListener(this._windowDisposeListener);
		drw._windowManager = null;
	}
};

DwtWindowManager.prototype.activateNextWindow = function() {
	if (this.visible_windows.size() >= 2)
		this.visible_windows.get(0).setActive(true);
};

DwtWindowManager.prototype.activatePrevWindow = function() {
	if (this.visible_windows.size() >= 2) {
		var win = this.getActiveWindow();
		this.visible_windows.get(this.visible_windows.size() - 2).setActive(true);
		this.visible_windows.remove(win);
		this.visible_windows.add(win, 0);
	}
};

DwtWindowManager.prototype._windowPopupListener = function(ev) {
	var win = ev.dwtObj;
	this.visible_windows.remove(win);
	this.visible_windows.add(win);
};

DwtWindowManager.prototype._windowPopdownListener = function(ev) {
	var win = ev.dwtObj;
	var cur = this.getActiveWindow();
	this.visible_windows.remove(win);
	if (cur === win) {
		// since it was active, let's activate the previous one
		win = this.visible_windows.getLast();
		if (win)
			win.setActive(true);
	}
};

DwtWindowManager.prototype._windowFocusListener = function(ev) {
	var win = ev.dwtObj;

	// unselect the active window
	var cur = this.getActiveWindow();

	if (cur === win)
		return;

	if (cur) {
		// console.log("Deactivating window %d", this.active_index);
		cur.setActive(false);
	}

	// push the new one at the end
	this.visible_windows.remove(win);
	this.visible_windows.add(win);
	this.active_index = this.visible_windows.size() - 1;

	// console.log("Managing %d windows, active: %d", this.visible_windows.size(), this.active_index);

	this._resetZIndexes();
};

DwtWindowManager.prototype._windowDisposeListener = function(ev) {
	var win = ev.dwtObj;

	// there's no point to call the full bloat, since the handlers will go away anyhow.
	//	this.unmanageWindow(win);

	// just remove it from our arrays
	this.all_windows.remove(win);

	// it should automagically go away from visible_windows too, because of DwtResizableWindow::__onDispose
	// BUT it doesn't.
	this.visible_windows.remove(win);
};

DwtWindowManager.prototype._resetZIndexes = function() {
	var start = Dwt.Z_VIEW + 50; // FIXME: should we actually add anything? not sure..
	for (var i = this.visible_windows.size(); --i >= 0;) {
		var win = this.visible_windows.get(i);
		win.setZIndex(start + i);
	}
};

DwtWindowManager.prototype._windowBlurListener = function(ev) {
	// console.log("Resetting active_index");
	this.active_index = -1;
};

// delegate the parent, since we don't really have a size.
DwtWindowManager.prototype.getSize = function() {
	return this.parent.getSize();
};

DwtWindowManager.prototype.getBounds = function() {
	return this.parent.getBounds();
};
