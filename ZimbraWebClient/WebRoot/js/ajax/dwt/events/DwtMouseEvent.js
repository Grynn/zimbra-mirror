/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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


DwtMouseEvent = function() {
	DwtUiEvent.call(this, true);
	this.reset(true);
};

DwtMouseEvent.prototype = new DwtUiEvent;
DwtMouseEvent.prototype.constructor = DwtMouseEvent;

DwtMouseEvent.prototype.toString = 
function() {
	return "DwtMouseEvent";
};

DwtMouseEvent.NONE		= 0;
DwtMouseEvent.LEFT 		= 1;
DwtMouseEvent.MIDDLE	= 2;
DwtMouseEvent.RIGHT		= 3;

DwtMouseEvent.prototype.reset =
function(dontCallParent) {
	if (!dontCallParent)
		DwtUiEvent.prototype.reset.call(this);
	this.button = 0;
	this._populated = false;
};

DwtMouseEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtUiEvent.prototype.setFromDhtmlEvent.apply(this, arguments);

	if (ev.layerX != null) { // Mozilla or Safari3
		switch (ev.which) {
			case 1:  this.button = DwtMouseEvent.LEFT; break;
			case 2:  this.button = DwtMouseEvent.MIDDLE; break;
			case 3:  this.button = DwtMouseEvent.RIGHT; break;
			default: this.button = DwtMouseEvent.NONE;
		}
	} else if (ev.offsetX != null) { // IE
		if ((ev.button & 1) != 0)
			this.button = DwtMouseEvent.LEFT;
		else if ((ev.button & 2) != 0)
			this.button = DwtMouseEvent.RIGHT;
		else if ((ev.button & 4) != 0)
			this.button = DwtMouseEvent.MIDDLE;
		else
			this.button = DwtMouseEvent.NONE;
	}

	if (AjxEnv.isMac) {
		// if ctrlKey and LEFT mouse, turn into RIGHT mouse with no ctrl key
		if (this.ctrlKey && this.button == DwtMouseEvent.LEFT) {
			this.button = DwtMouseEvent.RIGHT;
			this.ctrlKey = false;
		}
		// allow alt-key to be used for ctrl-select
		if (this.altKey) {
			this.ctrlKey = true;
			this.altKey = false;
		}
	}
};
