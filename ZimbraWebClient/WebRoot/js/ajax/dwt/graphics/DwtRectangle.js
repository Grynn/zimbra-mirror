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

/**
 * Creates a rectangle.
 * @constructor
 * @class
 * This class represents a rectangle. A point has an x-coordinate, y-coordinate, height and width.
 * 
 * @author Ross Dargahi
 * 
 * @param {int} x 	the x coordinate
 * @param {int} y 	the y coordinate
 * @param {int} width 	the width
 * @param {int} height 	the height
 */
DwtRectangle = function(x, y, width, height) {

	/**
	 * The x-coordinate.
	 * @type	int
	 */
	this.x = x;
	/**
	 * The y-coordinate.
	 * @type	int
	 */
	this.y = y;
	/**
	 * The width.
	 * @type	int
	 */
	this.width = width;
	/**
	 * The height.
	 * @type	int
	 */
	this.height = height;
}

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
DwtRectangle.prototype.toString = 
function() {
	return "DwtRectangle";
}

/**
 * Sets the values of the rectangle.
 * 
 * @param {int} x 	the x coordinate
 * @param {int} y 	the y coordinate
 * @param {int} width 	the width
 * @param {int} height 	the height
 */
 DwtRectangle.prototype.set =
 function(x, y, width, height) {
 	this.x = x;
 	this.y = y;
 }
