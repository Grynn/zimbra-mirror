// DvModel is data with a change listener.
function DvModel() {
	this._evtMgr = new LsEventMgr();
}

DvModel.prototype.toString = 
function() {
	return "DvModel";
}

DvModel.prototype.addChangeListener = 
function(listener) {
	return this._evtMgr.addListener(DvEvent.L_MODIFY, listener);
}

DvModel.prototype.removeChangeListener = 
function(listener) {
	return this._evtMgr.removeListener(DvEvent.L_MODIFY, listener);    	
}
