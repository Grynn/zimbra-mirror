/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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
DwtDateRangeEvent = function(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}

DwtDateRangeEvent.prototype = new DwtEvent;
DwtDateRangeEvent.prototype.constructor = DwtDateRangeEvent;

DwtDateRangeEvent.prototype.toString = 
function() {
	return "DwtDateRangeEvent";
}

DwtDateRangeEvent.prototype.reset =
function() {
	this.start = null;
	this.end = null;
}
