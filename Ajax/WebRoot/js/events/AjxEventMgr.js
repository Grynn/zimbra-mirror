function AjxEventMgr() {
	this._listeners = new Object();
}

AjxEventMgr.prototype.toString = 
function() {
	return "AjxEventMgr";
}

AjxEventMgr.prototype.addListener =
function(eventType, listener) {
	var lv = this._listeners[eventType];
	if (lv == null) {
		lv = this._listeners[eventType] = new AjxVector();
	}         	 
	if (!lv.contains(listener)) {
		lv.add(listener);
		return true;
	}
	return false;
}

AjxEventMgr.prototype.notifyListeners =
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

AjxEventMgr.prototype.isListenerRegistered =
function(eventType) {
	var lv = this._listeners[eventType];
	return (lv != null && lv.size() > 0);
}

AjxEventMgr.prototype.removeListener = 
function(eventType, listener) {
	var lv = this._listeners[eventType];
	if (lv != null) {
		lv.remove(listener);
		return true;
	}
	return false;
}

AjxEventMgr.prototype.removeAll = 
function(eventType) {
	var lv = this._listeners[eventType];
	if (lv != null) {
		lv.removeAll();
		return true;
	}
	return false;
}
