function DwtControlEvent() {
	this.reset();
}
DwtControlEvent.prototype = new DwtEvent;
DwtControlEvent.prototype.constructor = DwtControlEvent;

DwtControlEvent.prototype.toString = 
function() {
	return "DwtControlEvent";
}

DwtControlEvent.prototype.reset = 
function() {
	this.oldX = Dwt.DEFAULT;
	this.oldY = Dwt.DEFAULT;
	this.oldWidth = Dwt.DEFAULT;
	this.oldHeight = Dwt.DEFAULT;
	this.newX = Dwt.DEFAULT;
	this.newY = Dwt.DEFAULT;
	this.newWidth = Dwt.DEFAULT;
	this.newHeight = Dwt.DEFAULT;
}
