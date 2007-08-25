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

DwtFocusEvent = function(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}
DwtFocusEvent.prototype = new DwtEvent;
DwtFocusEvent.prototype.constructor = DwtFocusEvent;

DwtFocusEvent.FOCUS = 1;
DwtFocusEvent.BLUR = 2;

DwtFocusEvent.prototype.toString = 
function() {
	return "DwtFocusEvent";
}

DwtFocusEvent.prototype.reset = 
function() {
	this.dwtObj = null;
	this.state = DwtFocusEvent.FOCUS;
}
