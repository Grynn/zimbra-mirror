/**
* @class
* Use this class to implement an efficient String Buffer.
*	It is especially useful for assembling HTML.
*
*	Useage:
* 	1) For a small amount of text, call it statically as:
*
*		LsBuffer.concat("a", 1, "b", this.getFoo(), ...);
*
*	2) Or create an instance and use that to assemble a big pile of HTML:
*
*		var buffer = new LsBuffer();
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

function LsBuffer() {
	this.clear();
	if (arguments.length > 0) {
		arguments.join = this.buffer.join;
		this.buffer[this.buffer.length] = arguments.join("");
	}
}
LsBuffer.prototype.toString = function () {
	return this.buffer.join("");
}
LsBuffer.prototype.join = function (delim) {
	if (delim == null) delim = "";
	return this.buffer.join(delim);
}
LsBuffer.prototype.append = function () {
	arguments.join = this.buffer.join;
	this.buffer[this.buffer.length] = arguments.join("");
}
LsBuffer.prototype.join = function (str) {
	return this.buffer.join(str);
}
LsBuffer.prototype.set = function(str) {
	this.buffer = [str];
}
LsBuffer.prototype.clear = function() {
	this.buffer = [];
}
LsBuffer.concat = function() {
	arguments.join = Array.prototype.join;
	return arguments.join("");
}
LsBuffer.append = LsBuffer.concat;
