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
/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 
