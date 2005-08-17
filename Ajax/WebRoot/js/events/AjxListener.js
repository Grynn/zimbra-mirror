/**
* Creates a new listener.
* @constructor
* @class
* This class represents a listener, which is a function to be called in response to an event.
* A listener is a slightly specialized callback: it has a handleEvent() method, and it doesn't
* return a value.
*
* @author Ross Dargahi
* @param obj	(optional) the object to call the function from
* @param func	the listener function
*/
function LsListener(obj, method) {
	LsCallback.call(this, obj, method);
}

LsListener.prototype = LsCallback;
LsListener.prototype.constructor = LsListener;

LsListener.prototype.toString = 
function() {
	return "LsListener";
}

/**
* Invoke the listener function.
*
* @param ev		the event object that gets passed to an event handler
*/
LsListener.prototype.handleEvent =
function(ev) {
	return LsCallback.prototype.run.call(this, ev);
}
