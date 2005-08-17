function DwtMouseEvent() {
	DwtUiEvent.call(this, true);
	this.button = 0;
	this._populated = false;
}

DwtMouseEvent.prototype = new DwtUiEvent;
DwtMouseEvent.prototype.constructor = DwtMouseEvent;

DwtMouseEvent.prototype.toString = 
function() {
	return "DwtMouseEvent";
}

DwtMouseEvent.NONE = 0;
DwtMouseEvent.LEFT = 1;
DwtMouseEvent.MIDDLE = 2;
DwtMouseEvent.RIGHT = 3;

DwtMouseEvent.prototype.setFromDhtmlEvent =
function(ev) {
	ev = DwtUiEvent.prototype.setFromDhtmlEvent.call(this, ev);
	if (ev.offsetX != null) { // IE
		if ((ev.button & 1) != 0)
			this.button = DwtMouseEvent.LEFT;
		else if ((ev.button & 2) != 0)
			this.button = DwtMouseEvent.RIGHT;
		else if ((ev.button & 4) != 0)
			this.button = DwtMouseEvent.MIDDLE;
		else
			this.button = DwtMouseEvent.NONE;
	} else if (ev.layerX != null) { // Mozilla
		if (ev.which == 1)
			this.button = DwtMouseEvent.LEFT;
		else if (ev.which == 2)
			this.button = DwtMouseEvent.MIDDLE;
		else if (ev.which == 3)
			this.button = DwtMouseEvent.RIGHT;
		else
			this.button = DwtMouseEvent.NONE;
	}
	if (LsEnv.isMac) {
		// if ctrlKey and LEFT mouse, turn into RIGHT mouse with no ctrl key
		if (this.ctrlKey && this.button == DwtMouseEvent.LEFT) {
			this.button = DwtMouseEvent.RIGHT;
			this.ctrlKey = false;
		}
		// allow alt-key to be used for ctrl-select
		if (this.altKey) {
			this.ctrlKey = true;
			this.altKey = false;
		}
	}
}