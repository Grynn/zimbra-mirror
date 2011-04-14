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
 * @private
 */
DwtHoverEvent = function(type, delay, object, x, y) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.type = type;
	this.delay = delay;
	this.object = object;
	this.x = x || -1;
	this.y = y || -1;
}

DwtHoverEvent.prototype = new DwtEvent;
DwtHoverEvent.prototype.constructor = DwtHoverEvent;

DwtHoverEvent.prototype.toString = function() { return "DwtHoverEvent"; };

DwtHoverEvent.prototype.reset =
function() {
	this.type = 0;
	this.delay = 0;
	this.object = null;
	this.x = -1;
	this.y = -1;
};
