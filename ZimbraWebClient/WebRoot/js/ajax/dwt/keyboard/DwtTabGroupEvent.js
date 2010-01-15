/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class
* This class represents a the tab event. This event is used to indicate changes in
* the stat of DwtTabGroups (e.g. member addition and deletion). 
* 
* @author Ross Dargahi
* 
* @see DwtTabGroup
*/
DwtTabGroupEvent = function() {
	/** Tab group for which the event is being generated
	 * @type DwtTabGroup
	 */
	this.tabGroup = null;
	
	/** New focus memeber 
	 * @type {DwtControl|HTMLElement}
	 */
	this.newFocusMember = null;
}

/**
 * @return return a string version of the class' name
 * @type String
 */
DwtTabGroupEvent.prototype.toString = 
function() {
	return "DwtTabGroupEvent";
}


/**
 * Resets the members of the event
 */
DwtTabGroupEvent.prototype.reset =
function() {
	this.tabGroup = null;
	this.newFocusMember = null;
}
