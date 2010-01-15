/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


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
* @param args   [primitive or Array]	default arguments
*/
AjxListener = function(obj, method, args) {
	AjxCallback.call(this, obj, method, args);
}

AjxListener.prototype = new AjxCallback();
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
	return this.run(ev);
}
