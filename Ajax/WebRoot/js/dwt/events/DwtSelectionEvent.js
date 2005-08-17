function DwtSelectionEvent(init) {
	if (arguments.length == 0) return;
	DwtUiEvent.call(this, true);
	this.button = 0;
	this.detail = null;
	this.item = null;
}

DwtSelectionEvent.prototype = new DwtUiEvent;
DwtSelectionEvent.prototype.constructor = DwtSelectionEvent;

DwtSelectionEvent.prototype.toString = 
function() {
	return "DwtSelectionEvent";
}

DwtSelectionEvent.prototype.setFromDhtmlEvent =
function(ev, win) {
	ev = DwtUiEvent.prototype.setFromDhtmlEvent.call(this, ev);
}
