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

/**
 * @constructor
 * @class
 * Represents a point. A point has an x-coordinate and y-coordinate
 * 
 * @author Ross Dargahi
 * 
 * @param {number} x x coordinate
 * @param {number} y y coordinate
 */
DwtPoint = function(x, y) {
	this.x = x;
	this.y = y;
}

DwtPoint.tmp = new DwtPoint(0, 0);

/**
 * This method returns this class' name.
 * 
 * @return class name
 * @type String
 */
DwtPoint.prototype.toString = 
function() {
	return "DwtPoint";
}

/**
 * This method sets the values of a point
 * 
 * @param {number} x x coordinate
 * @param {number} y y coordinate
 */
 DwtPoint.prototype.set =
 function(x, y) {
 	this.x = x;
 	this.y = y;
 }
 
