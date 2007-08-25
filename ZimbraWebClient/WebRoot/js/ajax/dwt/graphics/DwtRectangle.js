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
