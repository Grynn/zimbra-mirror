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
 * 
 * @private
 */
DwtUiEvent = function(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}

DwtUiEvent.prototype = new DwtEvent;
DwtUiEvent.prototype.constructor = DwtUiEvent;

DwtUiEvent.prototype.isDwtUiEvent;
DwtUiEvent.prototype.toString = function() { return "DwtUiEvent"; }

DwtUiEvent.prototype.reset =
function() {
	this.dwtObj = null
	this.altKey = false;
	this.ctrlKey = false;
	this.metaKey = false;
	this.shiftKey = false;
	this.target = null;
	this.type = null;
	this.docX = -1;
	this.docY = -1;
	this.elementX = -1;
	this.elementY = -1;
	this.ersatz = false; // True means this event was manufactured
	this._stopPropagation = false;
	this._returnValue = true;
	this._dontCallPreventDefault = false; // True means to allow the the (unusual) situation in Firefox where we
	                                      // want the event handler to return false without calling preventDefault().
}

/**
 * Pass caller's "this" as 'target' if using IE and the ev may have come from another window. The target
 * will be used to get to the window that generated the event, so the event can be found.
 */
DwtUiEvent.getEvent =
function(ev, target) {
	ev = ev || window.event;
	if (ev) { return ev; }

	// get event from iframe in IE; see http://www.outofhanwell.com/blog/index.php?cat=25
	if (target) {
		DBG.println(AjxDebug.DBG3, "getEvent: Checking other window for event");
		var pw = (target.ownerDocument || target.document || target).parentWindow;
		return pw ? pw.event : null;
	}
}

/**
 * Returns the target element of the event.
 * 
 * @param ev				[Event]		DHTML event
 * @param useRelatedTarget	[boolean]*	if true, return element that was related to this event;
 * 										for a MOUSEOVER or MOUSEOUT event, that's the element
 * 										moved from/to.
 */
DwtUiEvent.getTarget =
function(ev, useRelatedTarget)  {
	ev = DwtUiEvent.getEvent(ev);
	if (!ev) { return null; }
	if (!useRelatedTarget) {
		if (ev.target) {
			// if text node (like on Safari) return parent
			return (ev.target.nodeType == 3) ? ev.target.parentNode : ev.target;
		} else if (ev.srcElement) {		// IE
			return ev.srcElement;
		}
	} else {
		if (ev.relatedTarget) {
			return ev.relatedTarget;
		} else if (ev.toElement) {		// IE
			return ev.toElement;
		} else if (ev.fromElement) {	// IE
			return ev.fromElement;
		}
	}
	return null;
}

/**
 * Returns the first element with a value for the given property, working its way up the element chain.
 *
 * @param ev				[Event]		DHTML event
 * @param prop				[string]	the name of a property
 * @param useRelatedTarget	[boolean]*	if true, return element that was related to this event;
 * @param value				[string]*	expected value of given property
 */
DwtUiEvent.getTargetWithProp =
function(ev, prop, useRelatedTarget, value)  {
	var htmlEl = DwtUiEvent.getTarget(ev, useRelatedTarget);
	while (htmlEl) {
		var elValue = Dwt.getAttr(htmlEl, prop);
		if (elValue != null && elValue !== "" && (!value || (elValue == value))) {
			return htmlEl;
		}
		htmlEl = htmlEl.parentNode;
	}
	return null;
}

/**
 * Returns the first element with values for all of the given properties, working its way up the element chain.
 *
 * @param ev				[Event]		DHTML event
 * @param props				[array]		a list of property names (strings)
 */
DwtUiEvent.getTargetWithProps =
function(ev, props)  {
	var htmlEl = DwtUiEvent.getTarget(ev);
	while (htmlEl) {
		var okay = true;
		for (var i in props) {
			var val = Dwt.getAttr(htmlEl, props[i]);
			if (val == null || val === "") {
				htmlEl = htmlEl.parentNode;
				okay = false;
				break;
			}
		}
		if (okay)
			return htmlEl;
	}
	return null;
}

DwtUiEvent.copy = 
function(dest, src) {
	dest.altKey = src.altKey;
	dest.ctrlKey = src.ctrlKey;
	dest.metaKey = src.metaKey;
	dest.shiftKey = src.shiftKey;
	dest.target = src.target;
	dest.type = src.type;
	dest.dwtObj = src.dwtObj;
	dest.docX = src.docX;
	dest.docY = src.docY;
	dest.elementX = src.elementX;
	dest.elementY = src.elementY;
	dest.ersatz = src.ersatz;
	dest._stopPropagation = src._stopPropagation;
	dest._returnValue = src._returnValue;
}

/**
 * Copies properties from the native DHTML event to this DWT event object. The target
 * control can be optionally fetched by providing true as the second argument.
 * 
 * @param ev	[Event]				DHTML event
 * @param obj	[DwtControl|true]	if true, the target object will be fetched; otherwise
 * 									used to set target object if present
 */
DwtUiEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtUiEvent.getEvent(ev);
	if (!ev) { return; }
	this.altKey = ev.altKey;
	this.ctrlKey = ev.ctrlKey;
	this.metaKey = ev.metaKey;
	this.shiftKey = ev.shiftKey;
	this.type = ev.type;
	this.target = DwtUiEvent.getTarget(ev);
	this.dwtObj = (obj === true) ? DwtControl.getTargetControl(ev) : obj;

	// Compute document coordinates
	if (ev.pageX != null) {
		this.docX = ev.pageX;
		this.docY = ev.pageY;
	} else if (ev.clientX != null) {
		this.docX = ev.clientX + document.body.scrollLeft - document.body.clientLeft;
		this.docY = ev.clientY + document.body.scrollTop - document.body.clientTop;
		if (document.body.parentElement) {
				var bodParent = document.body.parentElement;
				this.docX += bodParent.scrollLeft - bodParent.clientLeft;
				this.docY += bodParent.scrollTop - bodParent.clientTop;
		}
	}
	// Compute Element coordinates
	if (ev.offsetX != null) {
		this.elementX = ev.offsetX;
		this.elementY = ev.offsetY;
	} else if (!AjxEnv.isWebKitBased && ev.layerX != null) {
		this.elementX = ev.layerX;
		this.elementY = ev.layerY;
	} else { // fail hard for others
		this.elementX = Dwt.DEFAULT;
		this.elementY = Dwt.DEFAULT;
	}
	
	this.ersatz = false;
	return ev;
}

DwtUiEvent.prototype.setToDhtmlEvent =
function(ev) {
	DwtUiEvent.setBehaviour(ev, this._stopPropagation, this._returnValue, this._dontCallPreventDefault);
}

DwtUiEvent.setBehaviour =
function(ev, stopPropagation, allowDefault, dontCallPreventDefault) {
	var dhtmlEv = DwtUiEvent.getEvent(ev);
	DwtUiEvent.setDhtmlBehaviour(dhtmlEv, stopPropagation, allowDefault, dontCallPreventDefault);
};

DwtUiEvent.setDhtmlBehaviour =
function(dhtmlEv, stopPropagation, allowDefault, dontCallPreventDefault) {

	dhtmlEv = DwtUiEvent.getEvent(dhtmlEv);
	if (!dhtmlEv) { return; }

	// stopPropagation is referring to the function found in Mozilla's event object
	if (dhtmlEv.stopPropagation != null) {
		if (stopPropagation)
			dhtmlEv.stopPropagation();
		if (!allowDefault && !dontCallPreventDefault)
			dhtmlEv.preventDefault();
	} else {
		// IE only..
		dhtmlEv.returnValue = allowDefault;
		dhtmlEv.cancelBubble = stopPropagation;
	}
};

/**
 * @deprecated
 * Use DwtControl.getTargetControl() instead.
 * 
 * Returns a control (DWT object) based on the event, by finding the event target and using
 * its reference to a DWT object in the element's "dwtObj" expando property.
 * 
 * @param ev				[Event]		DHTML event
 * @param useRelatedTarget	[boolean]*	if true, return element that was related to this event;
 */
DwtUiEvent.getDwtObjFromEvent =
function(ev, useRelatedTarget) {
	var htmlEl = DwtUiEvent.getTargetWithProp(ev, "dwtObj", useRelatedTarget);
	return htmlEl ? Dwt.getObjectFromElement(htmlEl) : null;
};

/**
 * @deprecated
 * Instead, do something like this:
 * 		var htmlEl = DwtUiEvent.getTargetWithProp(ev, "myProp");
 * 		var obj = DwtControl.findControl(htmlEl);
 * 
 * Returns a control (DWT object) based on the event, by finding the event target with the
 * given property and using its reference to a DWT object.
 * 
 * @param ev				[Event]		DHTML event
 * @param useRelatedTarget	[boolean]*	if true, return element that was related to this event;
 */
DwtUiEvent.getDwtObjWithProp =
function(ev, prop) {
	var htmlEl = DwtUiEvent.getTargetWithProps(ev, ["dwtObj", prop]);
	return htmlEl ? Dwt.getObjectFromElement(htmlEl) : null;
};
