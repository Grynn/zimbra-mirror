function DwtDropEvent() {
	this.operation = null;
	this.targetControl = null;
	this.action = null;
	this.srcData = null;
	this.doIt = false;
}

DwtDropEvent.DRAG_ENTER = 1;
DwtDropEvent.DRAG_LEAVE = 2;
DwtDropEvent.DRAG_OP_CHANGED = 3;
DwtDropEvent.DRAG_DROP = 4;