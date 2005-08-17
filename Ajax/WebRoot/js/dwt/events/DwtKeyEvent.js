function DwtKeyEvent() {
	DwtUiEvent.call(this, true);
	this.keyCode = 0;
}


DwtKeyEvent.KEY_END_OF_TEXT =  0x03;
DwtKeyEvent.KEY_TAB = 0x09;
DwtKeyEvent.KEY_RETURN = 0x0D;
DwtKeyEvent.KEY_ENTER = 0x0D;
DwtKeyEvent.KEY_ESCAPE = 0x1B;

DwtKeyEvent.prototype = new DwtUiEvent;
DwtKeyEvent.prototype.constructor = DwtKeyEvent;

DwtKeyEvent.prototype.toString = 
function() {
	return "DwtKeyEvent";
}

DwtKeyEvent.isKeyEvent =
function(ev) {
	return (ev.type.search(/^key/i) != -1);
}

DwtKeyEvent.isKeyPressEvent =
function(ev) {
	return (LsEnv.isIE && ev.type == "keydown") || (ev.type == "keypress");
}


DwtKeyEvent.prototype.setFromDhtmlEvent =
function(ev) {
	DwtUiEvent.prototype.setFromDhtmlEvent.call(this, ev);
	this.charCode = (ev.charCode) ? ev.charCode : ev.keyCode;
	this.keyCode = ev.keyCode;
}

DwtKeyEvent.getCharCode =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	return (ev.charCode) ? ev.charCode : ev.keyCode;
}

DwtKeyEvent.copy = 
function(dest, src) {
	DwtUiEvent.copy(dest, src);
	dest.charCode = src.charCode;
	dest.keyCode = src.keyCode;
}
