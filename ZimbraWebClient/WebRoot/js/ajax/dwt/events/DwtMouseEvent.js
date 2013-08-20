/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * 
 * @private
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
	if (!dontCallParent) {
		DwtUiEvent.prototype.reset.call(this);
	}
	this.button = 0;
};

DwtMouseEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtUiEvent.prototype.setFromDhtmlEvent.apply(this, arguments);
	if (!ev) { return; }

	if (ev.which) { // Mozilla or Safari3
		switch (ev.which) {
			case 1:  this.button = DwtMouseEvent.LEFT; break;
			case 2:  this.button = DwtMouseEvent.MIDDLE; break;
			case 3:  this.button = DwtMouseEvent.RIGHT; break;
			default: this.button = DwtMouseEvent.NONE;
		}
	} else if (ev.button) { // IE
		if ((ev.button & 1) != 0) {
			this.button = DwtMouseEvent.LEFT;
		} else if ((ev.button & 2) != 0) {
			this.button = DwtMouseEvent.RIGHT;
		} else if ((ev.button & 4) != 0) {
			this.button = DwtMouseEvent.MIDDLE;
		} else {
			this.button = DwtMouseEvent.NONE;
		}
	}

	if (AjxEnv.isMac && this.button) {
		// Mac only comes with one button, but can take a USB multibutton mouse. Single-button will translate
		// CTRL-LEFT into RIGHT, but leave ctrlKey set to true. Convert that into vanilla RIGHT click. That
		// means we can't distinguish a CTRL-RIGHT, but oh well.
		if (this.ctrlKey && (this.button == DwtMouseEvent.LEFT || this.button == DwtMouseEvent.RIGHT)) {
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
