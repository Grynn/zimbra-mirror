/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
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


DwtRectangle = function(x, y, width, height) {

	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
}

DwtRectangle.prototype.toString = 
function() {
	return "DwtRectangle";
}

/**
 * This method sets the values of a point
 * 
 * @param {number} x x coordinate
 * @param {number} y y coordinate
 */
 DwtRectangle.prototype.set =
 function(x, y, width, height) {
 	this.x = x;
 	this.y = y;
 }
