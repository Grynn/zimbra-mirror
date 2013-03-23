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
 * Creates a rectangle.
 * @constructor
 * @class
 * This class represents a rectangle. A point has an x-coordinate, y-coordinate, height and width.
 * 
 * @author Ross Dargahi
 * 
 * @param {number} x 	the x coordinate
 * @param {number} y 	the y coordinate
 * @param {number} width 	the width
 * @param {number} height 	the height
 */
DwtRectangle = function(x, y, width, height) {

	/**
	 * The x-coordinate.
	 * @type	number
	 */
	this.x = x;
	/**
	 * The y-coordinate.
	 * @type	number
	 */
	this.y = y;
	/**
	 * The width.
	 * @type	number
	 */
	this.width = width;
	/**
	 * The height.
	 * @type	number
	 */
	this.height = height;
}

/**
 * Returns a string representation of the object.
 * 
 * @return		{string}		a string representation of the object
 */
DwtRectangle.prototype.toString = 
function() {
	return "DwtRectangle";
}

/**
 * Sets the values of the rectangle.
 * 
 * @param {number} x 	the x coordinate
 * @param {number} y 	the y coordinate
 * @param {number} width 	the width
 * @param {number} height 	the height
 */
 DwtRectangle.prototype.set =
 function(x, y, width, height) {
 	this.x = x;
 	this.y = y;
 }
