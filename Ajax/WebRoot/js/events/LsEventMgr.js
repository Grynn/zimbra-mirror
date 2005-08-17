function LsEventMgr() {
	this._listeners = new Object();
}

LsEventMgr.prototype.toString = 
function() {
	return "LsEventMgr";
}

LsEventMgr.prototype.addListener =
function(eventType, listener) {
	var lv = this._listeners[eventType];
	if (lv == null) {
		lv = this._listeners[eventType] = new LsVector();
	}         	 
	if (!lv.contains(listener)) {
		lv.add(listener);
		return true;
	}
	return false;
}

LsEventMgr.prototype.notifyListeners =
function(eventType, event) {
	var lv = this._listeners[eventType];
	if (lv != null) {
		var a = lv.getArray();
		var s = lv.size();
		var retVal = null;
		var c = null;
		for (var i = 0; i < s; i++) {
			c = a[i];
			retVal = c.handleEvent ? c.handleEvent(event) : c(event);
			if (retVal === false)
				return;
		}
	}	
}

LsEventMgr.prototype.isListenerRegistered =
function(eventType) {
	var lv = this._listeners[eventType];
	return (lv != null && lv.size() > 0);
}

LsEventMgr.prototype.removeListener = 
function(eventType, listener) {
	var lv = this._listeners[eventType];
	if (lv != null) {
		lv.remove(listener);
		return true;
	}
	return false;
}

LsEventMgr.prototype.removeAll = 
function(eventType) {
	var lv = this._listeners[eventType];
	if (lv != null) {
		lv.removeAll();
		return true;
	}
	return false;
}
