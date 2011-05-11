/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
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
* A listener is a slightly specialized callback: it has a {@link #handleEvent} method, and it does not
* return a value.
*
* @author Ross Dargahi
* 
* @param {Object}	obj	the object to call the function from
* @param {function}	func	the listener function
* @param {primative|array}	args   the default arguments
* 
* @extends		AjxCallback
*/
AjxListener = function(obj, method, args) {
	AjxCallback.call(this, obj, method, args);
}

AjxListener.prototype = new AjxCallback();
AjxListener.prototype.constructor = AjxListener;

AjxListener.prototype.isAjxListener = true;
AjxListener.prototype.toString = function() { return "AjxListener"; }

/**
* Invoke the listener function.
*
* @param {AjxEvent}		ev		the event object that gets passed to an event handler
*/
AjxListener.prototype.handleEvent =
function(ev) {
	return this.run(ev);
}
