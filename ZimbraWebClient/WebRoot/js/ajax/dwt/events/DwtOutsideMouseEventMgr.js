/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011 Zimbra, Inc.
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
 * Creates a singleton that manages outside mouse clicks for a given widget.
 * @constructor
 * @class
 * This class is designed to make it easy for a widget to detect mouse events
 * that happen somewhere outside its HTML structure. The typical use case is
 * so that a menu can pop down when a user clicks outside of it, but there are
 * others. For the most part, we care about mousedown events.
 *
 * There are several ways to detecting outside mouse events:
 *
 * 1. Set the body element to capture all mouse events, using DwtMouseEventCapture.
 *    That is all that's needed for non-IE browsers.
 * 2. Any control that receives an event calls the global listener through
 *    DwtEventManager.
 * 3. The shell listens for mouse events.
 * 4. Listen for window blur events.
 *
 * 2 and 3 are used by IE only. Note that the controls and the shell must be handling
 * those events in order to trigger the listeners.
 *
 * 4 is not used by IE, since window.onblur does not work correctly on IE. It's possible to
 * use document.onfocusout, but that is triggered even on clicks within the document, so
 * you need to check activeElement and cross your fingers. It's not worth the risk.
 *
 * We also have classes that create elements in another document (IFRAME) forward
 * their mouse events to us so that we can notify a client object if appropriate.
 * The two classes that do that are DwtIframe and DwtHtmlEditor.
 *
 * The framework can support multiple simultaneous clients. For example, a context
 * menu and toast could both be listening for outside mouse clicks at the same time.
 * Each will be notified as appropriate. If the toast is clicked, the context menu
 * will be notified of an outside click.
 *
 * A client may also add an element to its defined "inside" area by calling
 * startListening() with the same ID. One use case is a menu that pops up a submenu.
 * The two are considered together when determining if a mouse click was "outside".
 * If the submenu pops down, its element is removed from the area to check.
 */
DwtOutsideMouseEventMgr = function() {

	this._reset();
	this._mouseEventListener = new AjxListener(null, DwtOutsideMouseEventMgr._mouseEventHdlr);
	DwtOutsideMouseEventMgr.INSTANCE = this;
	this.id = "DwtOutsideMouseEventMgr";
};

DwtOutsideMouseEventMgr.prototype.toString =
function() {
	return "DwtOutsideMouseEventMgr";
};

DwtOutsideMouseEventMgr.EVENTS = [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEWHEEL];
DwtOutsideMouseEventMgr.EVENTS_HASH = AjxUtil.arrayAsHash(DwtOutsideMouseEventMgr.EVENTS);

/**
 * Start listening for outside mouse events on behalf of the given object.
 *
 * @param {hash}		params					hash of params:
 * @param {string}		params.id				unique ID for this listening session
 * @param {DwtControl}	params.obj				control on behalf of whom we're listening
 * @param {string}		params.elementId		ID of reference element, if other than control's HTML element
 * @param {AjxListener}	params.outsideListener	listener to call when we get an outside mouse event
 * @param {boolean}		params.noWindowBlur		if true, don't listen from window blur events; useful for dev
 */
DwtOutsideMouseEventMgr.prototype.startListening =
function(params) {

	DBG.println("out", "start listening: " + params.id);

	if (!(params && params.outsideListener)) { return; }
	var id = params.id;

	if (!this._menuCapObj) {
		// we only need a single menu capture object, create it lazily
		var mecParams = {
			id:		this.id,
			hardCapture:	false,
			mouseDownHdlr:	DwtOutsideMouseEventMgr._mouseEventHdlr,
			mouseWheelHdlr:	DwtOutsideMouseEventMgr._mouseEventHdlr
		}
		this._menuCapObj = new DwtMouseEventCapture(mecParams);
	}

	var elementId = params.elementId ||
		(params.obj && params.obj.getHTMLElId && params.obj.getHTMLElId());

	var context = this._byId[id];
	if (context) {
		// second and subsequent calls with same ID will just add element IDs; typical case is submenu
		DBG.println("out", "add element ID " + elementId + " for ID " + id);
		context.elementIds.push(elementId);
		DBG.println("out", "element IDs: " + context.elementIds);
		return;
	}
	else {
		DBG.println("out", "element ID: " + elementId);
		context = this._byId[id] = {
			id:					id,
			obj:				params.obj,
			elementIds:			[elementId],
			outsideListener:	params.outsideListener
		}
	}

	// add various event listeners when we get our first client
	if (this._numIds == 0) {
		if (AjxEnv.isIE) {
			var shell = DwtShell.getShell(window);
			var events = DwtOutsideMouseEventMgr.EVENTS;
			shell._setEventHdlrs(events);
			for (var i = 0; i < events.length; i++) {
				var ev = events[i];
				shell.addListener(ev, this._mouseEventListener);
				DwtEventManager.addListener(ev, this._mouseEventListener);
			}
		}

		if (!AjxEnv.isIE && !params.noWindowBlur) {
			this._savedWindowBlurHandler = window.onblur;
			window.onblur = DwtOutsideMouseEventMgr._mouseEventHdlr;
		}

		// start a new capture session
		DBG.println("out", "capture");
		this._menuCapObj.capture();
		this._capturing = true;
	}

	this._numIds++;
};

/**
 * Stop listening for outside mouse events. Listening is stopped for the element
 * provided, if any, or for the element indicated by the context ID. Outside
 * listeners are removed once there are no more elements in the context.
 *
 * @param {string|hash}	params				ID, if string, otherwise hash of params:
 * @param {string}		params.id			unique ID for this listening session
 * @param {DwtControl}	params.obj			control on behalf of whom we're listening
 * @param {string}		params.elementId	ID of element to remove from listening context
 * @param {boolean}		params.noWindowBlur	if true, don't listen from window blur events; useful for dev
 */
DwtOutsideMouseEventMgr.prototype.stopListening =
function(params) {

	if (typeof params == "string") {
		params = {id:params};
	}
	var id = params.id;
	var context = this._byId[id];
	if (!context) { return; }

	var elIds = context.elementIds;
	var elementId = params.elementId || (params.obj && params.obj.getHTMLElId());
	DBG.println("out", "stop listening: " + elementId);
	if (elementId) {
		AjxUtil.arrayRemove(elIds, elementId);
		if (elIds.length > 0) {
			// still at least one element to check against
			return;
		}
	}

	// no more elements in this context, remove listeners
	delete this._byId[id];
	this._numIds--;

	if (this._numIds == 0) {
		if (AjxEnv.isIE) {
			var shell = DwtShell.getShell(window);
			var events = DwtOutsideMouseEventMgr.EVENTS;
			shell._setEventHdlrs(events, true);
			for (var i = 0; i < events.length; i++) {
				var ev = events[i];
				shell.removeListener(ev, this._mouseEventListener);
				DwtEventManager.removeListener(ev, this._mouseEventListener);
			}
		}

		if (!AjxEnv.isIE && !params.noWindowBlur) {
			window.onblur = this._savedWindowBlurHandler;
		}

		this._reset();
	}
};

DwtOutsideMouseEventMgr.prototype._reset =
function() {

	if (this._capturing && (DwtMouseEventCapture.getId() == this.id)) {
		DBG.println("out", "release");
		this._menuCapObj.release();
		this._capturing = false;
	}
	this._byId		= {};
	this._numIds	= 0;
};

/**
 * If the event is one we're listening for, check its target to see if
 * we should pass it along.
 *
 * @param {Event}	ev
 */
DwtOutsideMouseEventMgr.forwardEvent =
function(ev) {

	if (!ev) { return; }
	var omem = DwtOutsideMouseEventMgr.INSTANCE;
	if (!omem._numIds) { return; }

	var type = "on" + ev.type;
	if (DwtOutsideMouseEventMgr.EVENTS_HASH[type]) {
		DwtOutsideMouseEventMgr._mouseEventHdlr(ev);
	}
};

/**
 * Call the client's outside listener if the event happened outside of the elements
 * defined by the client's context. Note that the event that gets passed in might be
 * a DOM event, or a DwtMouseEvent. That's okay, since both have a "target" property.
 *
 * @param {Event|DwtMouseEvent}	ev		event
 */
DwtOutsideMouseEventMgr._mouseEventHdlr =
function(ev) {

	var omem = DwtOutsideMouseEventMgr.INSTANCE;
	var targetEl = DwtUiEvent.getTarget(ev);
	DBG.println("out", "target: " + targetEl.id);
	for (var id in omem._byId) {
		var runListener = true;
		var context = omem._byId[id];
		var elementIds = context.elementIds;
		DBG.println("out", "element IDs for " + id + ": " + context.elementIds);
		for (var i = 0; i < elementIds.length; i++) {
			DBG.println("out", "check: " + elementIds[i]);
			var el = document.getElementById(elementIds[i]);
			if (Dwt.isAncestor(el, targetEl)) {
				runListener = false;
				break;
			}
		}
		if (runListener) {
			DBG.println("out", "run listener for: " + context.id);
			context.outsideListener.run(ev, context);
		}
	}

	DwtUiEvent.setBehaviour(ev, false, true);
	return true;
};

// create our singleton instance
new DwtOutsideMouseEventMgr();
