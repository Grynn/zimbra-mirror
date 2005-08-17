function DwtDragEvent() {
	this.operation = null;
	this.srcControl = null;
	this.action = null;
	this.doIt = false;
	this.srcData = null;
}

DwtDragEvent.DRAG_START = 1;
DwtDragEvent.SET_DATA = 2;
DwtDragEvent.DRAG_END = 3;
