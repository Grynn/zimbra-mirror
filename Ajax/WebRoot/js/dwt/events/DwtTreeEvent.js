function DwtTreeEvent() {
	DwtSelectionEvent.call(this, true);
}

DwtTreeEvent.prototype = new DwtSelectionEvent;
DwtTreeEvent.prototype.constructor = DwtTreeEvent;

DwtTreeEvent.prototype.toString = 
function() {
	return "DwtTreeEvent";
}

DwtTreeEvent.prototype.setFromDhtmlEvent =
function(ev, win) {
	ev = DwtSelectionEvent.prototype.setFromDhtmlEvent.call(this, ev);
}
