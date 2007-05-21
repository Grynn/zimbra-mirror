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


//
// DwtHoverEvent
//

DwtHoverEvent = function(type, delay, object, x, y) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.type = type;
	this.delay = delay;
	this.object = object;
	this.x = AjxUtil.isUndefined(x) ? -1 : x;
	this.y = AjxUtil.isUndefined(y) ? -1 : y;
}

DwtHoverEvent.prototype = new DwtEvent;
DwtHoverEvent.prototype.constructor = DwtHoverEvent;

DwtHoverEvent.prototype.reset =
function() {
	this.type = 0;
	this.delay = 0;
	this.object = null;
	this.x = -1;
	this.y = -1;
}