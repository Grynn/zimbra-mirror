function DwtDisposeEvent(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
}

DwtDisposeEvent.prototype = new DwtEvent;
DwtDisposeEvent.prototype.constructor = DwtDisposeEvent;

DwtDisposeEvent.prototype.toString = 
function() {
	return "DwtDisposeEvent";
}
