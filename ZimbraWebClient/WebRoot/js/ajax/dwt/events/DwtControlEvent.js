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


DwtControlEvent = function() {
	this.reset();
}
DwtControlEvent.prototype = new DwtEvent;
DwtControlEvent.prototype.constructor = DwtControlEvent;

// type of control event
//      RESIZE	       -- for setSize
//      MOVE	       -- for setLocation
//      RESIZE | MOVE  -- for setBounts (bitwise or)

DwtControlEvent.RESIZE = 1;
DwtControlEvent.MOVE = 2;

DwtControlEvent.prototype.toString = 
function() {
	return "DwtControlEvent";
}

DwtControlEvent.prototype.reset = 
function(type) {
	this.oldX = Dwt.DEFAULT;
	this.oldY = Dwt.DEFAULT;
	this.oldWidth = Dwt.DEFAULT;
	this.oldHeight = Dwt.DEFAULT;
	this.newX = Dwt.DEFAULT;
	this.newY = Dwt.DEFAULT;
	this.newWidth = Dwt.DEFAULT;
	this.newHeight = Dwt.DEFAULT;
	this.type = type || null;
}
