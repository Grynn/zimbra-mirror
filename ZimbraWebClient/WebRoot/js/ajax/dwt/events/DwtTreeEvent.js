/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 VMware, Inc.
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
DwtTreeEvent = function() {
	DwtSelectionEvent.call(this, true);
}

DwtTreeEvent.prototype = new DwtSelectionEvent;
DwtTreeEvent.prototype.constructor = DwtTreeEvent;

DwtTreeEvent.prototype.toString = 
function() {
	return "DwtTreeEvent";
}

DwtTreeEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtSelectionEvent.prototype.setFromDhtmlEvent.apply(this, arguments);
}
