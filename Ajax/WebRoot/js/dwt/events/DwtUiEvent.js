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

function DwtUiEvent(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}

DwtUiEvent.prototype = new DwtEvent;
DwtUiEvent.prototype.constructor = DwtUiEvent;

DwtUiEvent.prototype.toString = 
function() {
	return "DwtUiEvent";
}

DwtUiEvent.prototype.reset =
function() {
	this.dwtObj = null
	this.altKey = false;
	this.ctrlKey = false;
	this.shiftKey = false;
	this.target = null;
	this.type = null;
	this.docX = -1;
	this.docY = -1;
	this.elementX = -1;
	this.elementY = -1;
	this._stopPropagation = false;
	this._returnValue = true;
}

DwtUiEvent.getEvent =
function(ev) {
	return (ev) ? ev : ((window.event) ? window.event : null);
}

DwtUiEvent.getTarget =
function(ev)  {
	ev = DwtUiEvent.getEvent(ev);
	if (ev.target) {
		/* if text node (like on Safari) return parent */
		if (ev.target.nodeType == 3) {
			return ev.target.parentNode;
		} else {
			return ev.target;
		}
	} else if (ev.srcElement) {
		return ev.srcElement;
	} else {
		return null;
	}
}

/**
* Returns the first element with a non-null value for the given property, working its way up the element chain.
*
* @param ev		a UI event
* @param prop	the name of a property
* @returns		an element
*/
DwtUiEvent.getTargetWithProp =
function(ev, prop)  {
	var htmlEl = DwtUiEvent.getTarget(ev);
	while (htmlEl) {
		if (htmlEl[prop] != null) {
			return htmlEl;
		}
		htmlEl = htmlEl.parentNode;
	}
	return null;
}

/**
* Returns the first element with a non-null value for the given properties, working its way up the element chain.
*
* @param ev		a UI event
* @param props	a list of property names
* @returns		an element
*/
DwtUiEvent.getTargetWithProps =
function(ev, props)  {
	var htmlEl = DwtUiEvent.getTarget(ev);
	while (htmlEl) {
		var okay = true;
		for (var i in props) {
			if (htmlEl[props[i]] == null) {
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

DwtUiEvent.getDwtObjFromEvent =
function(ev) {
	var htmlEl = DwtUiEvent.getTargetWithProp(ev, "dwtObj");
	return htmlEl ? Dwt.getObjectFromElement(htmlEl) : null;
}

DwtUiEvent.getDwtObjWithProp =
function(ev, prop) {
	var htmlEl = DwtUiEvent.getTargetWithProps(ev, ["dwtObj", prop]);
	return htmlEl ? Dwt.getObjectFromElement(htmlEl) : null;
}

DwtUiEvent.copy = 
function(dest, src) {
	dest.altKey = src.altKey;
	dest.ctrlKey = src.ctrlKey;
	dest.shiftKey = src.shiftKey;
	dest.target = src.target;
	dest.type = src.type;
	dest.dwtObj = src.dwtObj;
	dest.docX = src.docX;
	dest.docY = src.docY;
	dest.elementX = src.elementX;
	dest.elementY = src.elementY;
	dest._stopPropagation = src._stopPropagation;
	dest._returnValue = src._returnValue;
}

DwtUiEvent.prototype.setFromDhtmlEvent =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	if (!ev) return;
	this.altKey = ev.altKey;
	this.ctrlKey = ev.ctrlKey;
	this.shiftKey = ev.shiftKey;
	this.type = ev.type;
	this.target = DwtUiEvent.getTarget(ev);
	var target = this.target;
	while (target != null) {
		if (target.dwtObj != null) {
			this.dwtObj = Dwt.getObjectFromElement(target);
			break;
		}
		target = target.parentNode;
	}

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
	} else if (ev.pageX != null) {
		var p = (this.dwtObj != null) 
		? Dwt.toWindow(this.dwtObj.getHtmlElement(), 0, 0) : new DwtPoint(0,0);
		this.elementX = (ev.pageX - p.x >= 0) ? ev.pageX - p.x : 0;
		this.elementY = (ev.pageY - p.y >= 0) ? ev.pageY - p.y : 0;
	}
	return ev;
}

DwtUiEvent.prototype.setToDhtmlEvent =
function(ev) {
	DwtUiEvent.setBehaviour(ev, this._stopPropagation, this._returnValue);	
}

DwtUiEvent.setBehaviour =
function(ev, stopPropagation, allowDefault) {
	ev = DwtUiEvent.getEvent(ev);
	
	// stopPropagation is referring to the function found in Mozilla's event object
	if (ev.stopPropagation != null) {
		if (stopPropagation)
			ev.stopPropagation();
		if (!allowDefault)
			ev.preventDefault();
	} else {
		// IE only..
		ev.returnValue = allowDefault; 
		ev.cancelBubble = stopPropagation;
	}
}
