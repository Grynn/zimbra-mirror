function DwtDragSource(supportedOps) {
	this._supportedOps = supportedOps
	this._evtMgr = new AjxEventMgr();
}

DwtDragSource._DRAG_LISTENER = "DwtDragSource._DRAG_LISTENER";

DwtDragSource._dragEvent = new DwtDragEvent();

DwtDragSource.prototype.toString = 
function() {
	return "DwtDragSource";
}

DwtDragSource.prototype.addDragListener =
function(dragSourceListener) {
	this._evtMgr.addListener(DwtDragSource._DRAG_LISTENER, dragSourceListener);
}

DwtDragSource.prototype.removeDragListener =
function(dragSourceListener) {
	this._evtMgr.removeListener(DwtDragSource._DRAG_LISTENER, dragSourceListener);
}


/* 
* The following  methods are called by DwtControl during the Drag lifecycle 
*/


DwtDragSource.prototype._beginDrag =
function(operation, srcControl) {
	if (!(this._supportedOps & operation))
		return Dwt.DND_DROP_NONE;
		
	DwtDragSource._dragEvent.operation = operation;
	DwtDragSource._dragEvent.srcControl = srcControl;
	DwtDragSource._dragEvent.action = DwtDragEvent.DRAG_START;
	DwtDragSource._dragEvent.srcData = null;
	DwtDragSource._dragEvent.doit = true;
	this._evtMgr.notifyListeners(DwtDragSource._DRAG_LISTENER, DwtDragSource._dragEvent);
	return DwtDragSource._dragEvent.operation;
}

DwtDragSource.prototype._getData =
function() {
	DwtDragSource._dragEvent.action = DwtDragEvent.SET_DATA;
	this._evtMgr.notifyListeners(DwtDragSource._DRAG_LISTENER, DwtDragSource._dragEvent);
	return DwtDragSource._dragEvent.srcData;
}

DwtDragSource.prototype._endDrag =
function() {
	DwtDragSource._dragEvent.action = DwtDragEvent.DRAG_END;
	DwtDragSource._dragEvent.doit = false;
	this._evtMgr.notifyListeners(DwtDragSource._DRAG_LISTENER, DwtDragSource._dragEvent);
	return DwtDragSource._dragEvent.doit;
}
