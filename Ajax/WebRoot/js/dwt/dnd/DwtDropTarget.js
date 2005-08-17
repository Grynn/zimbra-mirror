function DwtDropTarget(transferType) {
	this._transferTypes = new Array();
	this._hasMultiple = false;
	if (transferType) {
		for (var i = 0; i < arguments.length; i++)
			this._transferTypes[i] = arguments[i];
			
		this._transferTypes.length = i;
	}
	this._evtMgr = new LsEventMgr();
}

DwtDropTarget._DROP_LISTENER = "DwtDropTarget._DROP_LISTENER";

DwtDropTarget._dropEvent = new DwtDropEvent();

DwtDropTarget.prototype.toString = 
function() {
	return "DwtDropTarget";
}

DwtDropTarget.prototype.addDropListener =
function(dropTargetListener) {
	this._evtMgr.addListener(DwtDropTarget._DROP_LISTENER, dropTargetListener);
}

DwtDropTarget.prototype.removeDropListener =
function(dropTargetListener) {
	this._evtMgr.removeListener(DwtDropTarget._DROP_LISTENER, dropTargetListener);
}

/* Check to see if transferType is a valid target. Note that transferType can be an array of items
 * or just an single item */
DwtDropTarget.prototype.isValidTarget =
function(items) {
	if (items instanceof Array) {
		for (var i = 0; i < items.length; i++) {
			if (!this._checkTarget(items[i]))
				return false;
		}
		return true;
	} else {
		return this._checkTarget(items);
	}
}

DwtDropTarget.prototype.markAsMultiple = 
function() {
	this._hasMultiple = true;
};
DwtDropTarget.prototype.hasMultipleTargets = 
function () {
	return this._hasMultiple;
};

DwtDropTarget.prototype._checkTarget =
function(item) {
	for (var i = 0; i < this._transferTypes.length; i++) {
		if (item instanceof this._transferTypes[i])
			return true;
	}	
	if (i == this._transferTypes.length)
		return false;
}

DwtDropTarget.prototype.getTransferTypes =
function() {
	return this._transferTypes;
}

/* variable length parameter */
DwtDropTarget.prototype.setTransferTypes =
function(transferType) {
	for (var i = 0; i < arguments.length; i++)
		this._transferTypes[i] = arguments[i];
	this._transferTypes.length = i;
}

/* 
* The following methods are called by DwtControl during the Drag lifecycle 
*/
DwtDropTarget.prototype._dragEnter =
function(operation, targetControl, srcData, ev) {
	DwtDropTarget._dropEvent.operation = operation;
	DwtDropTarget._dropEvent.targetControl = targetControl;
	DwtDropTarget._dropEvent.action = DwtDropEvent.DRAG_ENTER;
	DwtDropTarget._dropEvent.srcData = srcData;
	DwtDropTarget._dropEvent.uiEvent = ev;
	DwtDropTarget._dropEvent.doIt = true;
	this._evtMgr.notifyListeners(DwtDropTarget._DROP_LISTENER, DwtDropTarget._dropEvent);
	return DwtDropTarget._dropEvent.doIt;
}

DwtDropTarget.prototype._dragLeave =
function() {
	DwtDropTarget._dropEvent.action = DwtDropEvent.DRAG_LEAVE;
	this._evtMgr.notifyListeners(DwtDropTarget._DROP_LISTENER, DwtDropTarget._dropEvent);
}

DwtDropTarget.prototype._dragOpChanged =
function(newOperation) {
	DwtDropTarget._dropEvent.operation = newOperation;
	DwtDropTarget._dropEvent.action = DwtDropEvent.DRAG_OP_CHANGED;
	this._evtMgr.notifyListeners(DwtDropTarget._DROP_LISTENER, DwtDropTarget._dropEvent);
	return DwtDropTarget._dropEvent.doIt;
}

DwtDropTarget.prototype._drop =
function(srcData, ev) {
	DwtDropTarget._dropEvent.action = DwtDropEvent.DRAG_DROP;
	DwtDropTarget._dropEvent.srcData = srcData;
	DwtDropTarget._dropEvent.uiEvent = ev;
	this._evtMgr.notifyListeners(DwtDropTarget._DROP_LISTENER, DwtDropTarget._dropEvent);
	return DwtDropTarget._dropEvent.doIt;
}
