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
* @class
* Use this class to implement an efficient String Buffer.
*	It is especially useful for assembling HTML.
*
*	Useage:
* 	1) For a small amount of text, call it statically as:
*
*		AjxBuffer.concat("a", 1, "b", this.getFoo(), ...);
*
*	2) Or create an instance and use that to assemble a big pile of HTML:
*
*		var buffer = new AjxBuffer();
*		buffer.append("foo", myObject.someOtherFoo(), ...);
*		...
*		buffer.append(fooo.yetMoreFoo());
*		return buffer.toString()
*
*	It is useful (and quicker!) to create a single buffer and then pass that to subroutines
*	that are doing assembly of HTML pieces for you.
*
*	Note that in both modes you can pass as many arguments you like to the
*	methods -- this is quite a bit faster than concatenating the arguments
*	with the + sign (eg: don't do  buffer.append("a" + b.foo());	)
*
* @author Owen Williams
*/

AjxBuffer = function() {
	this.clear();
	if (arguments.length > 0) {
		arguments.join = this.buffer.join;
		this.buffer[this.buffer.length] = arguments.join("");
	}
}
AjxBuffer.prototype.toString = function () {
	return this.buffer.join("");
}
AjxBuffer.prototype.join = function (delim) {
	if (delim == null) delim = "";
	return this.buffer.join(delim);
}
AjxBuffer.prototype.append = function () {
	arguments.join = this.buffer.join;
	this.buffer[this.buffer.length] = arguments.join("");
}
AjxBuffer.prototype.join = function (str) {
	return this.buffer.join(str);
}
AjxBuffer.prototype.set = function(str) {
	this.buffer = [str];
}
AjxBuffer.prototype.clear = function() {
	this.buffer = [];
}
AjxBuffer.concat = function() {
	arguments.join = Array.prototype.join;
	return arguments.join("");
}
AjxBuffer.append = AjxBuffer.concat;
