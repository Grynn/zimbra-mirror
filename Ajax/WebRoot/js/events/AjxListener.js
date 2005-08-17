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
function AjxListener(obj, method) {
	AjxCallback.call(this, obj, method);
}

AjxListener.prototype = AjxCallback;
AjxListener.prototype.constructor = AjxListener;

AjxListener.prototype.toString = 
function() {
	return "AjxListener";
}

/**
* Invoke the listener function.
*
* @param ev		the event object that gets passed to an event handler
*/
AjxListener.prototype.handleEvent =
function(ev) {
	return AjxCallback.prototype.run.call(this, ev);
}
