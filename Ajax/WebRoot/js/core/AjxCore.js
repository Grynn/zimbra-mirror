function LsCore() {}

LsCore._objectIds = [null];


LsCore.assignId = 
function(anObject) {
	var myId = LsCore._objectIds.length;
	LsCore._objectIds[myId]= anObject;
	return myId;
};

LsCore.unassignId = 
function(anId) {
	LsCore._objectIds[anId]= null;
};

LsCore.objectWithId = 
function(anId) {
	return LsCore._objectIds[anId];
};

/**
 * Adds a listener to an element, for the given event name.
 */
LsCore.addListener = 
function(eventSource, eventName, action) {
	eventSource = LsCore._getEventSource(eventSource);
	var listenerStruct = LsCore._getListenerStruct(eventSource, eventName, true);
	listenerStruct.list[listenerStruct.list.length] = action;
};

/**
 * sets a one time event handler for the given eventName.
 */
LsCore.setEventHandler = 
function(eventSource, eventName, action) {
	eventSource = LsCore._getEventSource(eventSource);
	var listenerStruct = LsCore._getListenerStruct(eventSource, eventName, true);
	listenerStruct.single = action;
};

/**
 * removes a listener for a given event
 */
LsCore.removeListener = 
function(eventSource, eventName, action) {
	eventSource = LsCore._getEventSource(eventSource);
	var listenerStruct = LsCore._getListenerStruct(eventSource, eventName);

	if (listenerStruct) {
		var listenerList = listenerStruct.list;
		for (var i = 0; i < listenerList.length; i++) {
			if (listenerList[i] == action)
				listenerList[i] = null;
		}
	}
};

/**
 * removes all listeners for a given eventName, and source
 */
LsCore.removeAllListeners = 
function(eventSource, eventName) {
	eventSource = LsCore._getEventSource(eventSource);
	var listenerStruct = LsCore._getListenerStruct(eventSource, eventName);

	if (listenerStruct) {
		var listenerList = listenerStruct.list;
		for (var i = 0; i < listenerList.length; i++)
			listenerList[i] = null;
	}
	LsCore.unassignId(listenerStruct.id);
};

/**
 * notifies listeners of the event. This only needs to be called if
 * the event is not a standard DOM event. Those types of event callbacks
 * will be triggered by their event handlers
 */
LsCore.notifyListeners = 
function(eventSource, eventName, arg1) {
	eventSource = LsCore._getEventSource(eventSource);
	var listenerStruct = LsCore._getListenerStruct(eventSource, eventName)
	if (listenerStruct)
		eventSource[eventName](arg1);
};

LsCore._getEventSource = 
function(eventSource) {
	if (typeof(eventSource) == 'string')
		eventSource = document.getElementById(eventSource);
	return eventSource;
};

LsCore.getListenerStruct = 
function (eventSource, eventName) {
	return LsCore._getListenerStruct(eventSource, eventName);
};

/**
 * gets the existing struct for the eventSource, or creates a new one.
 */
LsCore._getListenerStruct = 
function(eventSource, eventName, create) {
	var listenerStruct = null;
	if (eventSource[eventName]) {
		var id = eventSource[eventName]._lsListenerStructId;
		listenerStruct = LsCore.objectWithId(id);
	} else if (create) {
		listenerStruct = LsCore._setupListener(eventSource, eventName);
	}

	return listenerStruct;
};
    
/**
 * Creates a listener struct
 */
LsCore._setupListener = 
function(eventSource, eventName, id) {
	var listenerStruct = new Object();
	listenerStruct.list = new Array();
	listenerStruct.single = null;
	var id = listenerStruct.id = LsCore.assignId(listenerStruct);
	var handler = LsCore._createListenerClosure(id);
	eventSource[eventName] = handler;
	eventSource[eventName]._lsListenerStructId = id;

	return listenerStruct;
};

LsCore._createListenerClosure = 
function(id) {
	var closure = function(arg1) {
		var listenerStruct = LsCore.objectWithId(id);
		var listenerList = listenerStruct.list;
		for (var i = 0; i < listenerList.length; i++) {
			var callback = listenerList[i];
			if (callback) {
				if (typeof(callback) == 'string') {
					eval(callback);
				} else {
					// handle LsListener callbacks as well as simple functions
					if (callback.handleEvent) {
						callback.handleEvent(arg1, this);
					} else {
						callback(arg1, this);
					}
				}
			}
		}
        if (listenerStruct.single) {
			var callback = listenerStruct.single;
			if (typeof(callback) == 'string') {
				eval(callback);
			} else {
				return callback.handleEvent
					? callback.handleEvent(arg1, this)
					: callback(arg1, this);
			}
		}
	}
	return closure;
};

/**
 * Convenience method for adding onload listeners
 */
LsCore.addOnloadListener = 
function(action) {
	if (window.onload && (!window.onload._lsListenerStructId)) {
		var priorListener = window.onload;
		window.onload = null;
		LsCore.addListener(window, "onload", priorListener);
	}

	LsCore.addListener(window, "onload", action);
};

/**
 * Convenience method for adding onunload listeners
 */    
LsCore.addOnunloadListener = 
function(action) {
	if (window.onunload && (!window.onunload._lsListenerStructId)) {
		var priorListener = window.onunload;
		window.onunload = null;
		LsCore.addListener(window, "onunload", priorListener);
	}

	LsCore.addListener(window, "onunload", action);
};
