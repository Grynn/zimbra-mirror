/**
 * static class that wraps around LsEventManager
 */
function DwtEventManager () {
    
}

DwtEventManager._instance = new AjxEventMgr();

DwtEventManager._domEventToDwtMap = {
	'ondblclick': DwtEvent.ONDBLCLICK,
	'onmousedown': DwtEvent.ONMOUSEDOWN ,
	'onmouseup': DwtEvent.ONMOUSEUP,
	'onmousemove': DwtEvent.ONMOUSEMOVE,
	'onmouseout': DwtEvent.ONMOUSEOUT,
	'onmouseover': DwtEvent.ONMOUSEOVER,
	'onselectstart': DwtEvent.ONSELECTSTART,
	'onchange': DwtEvent.ONCHANGE
};

DwtEventManager.addListener = 
function(eventType, listener) {
	DwtEventManager._instance.addListener(eventType, listener);
};

DwtEventManager.notifyListeners = 
function(eventType, event) {
	DwtEventManager._instance.notifyListeners(eventType, event);
};

DwtEventManager.removeListener = 
function(eventType, listener) {
	DwtEventManager._instance.removeListener(eventType, listener);
};

// This tries to listen on a given element as well as to any event
// received by other Dwt widgets
DwtEventManager.addGlobalListener = 
function (element, eventType, listener) {
	AjxCore.addListener(element, eventType, listener);
	var dwtEventName = DwtEventManager._domEventToDwtMap[eventType];
	if (dwtEventName) {
		DwtEventManager.addListener(dwtEventName, listener);
	}
}
