/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2011 VMware, Inc.
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
 * Creates a callback which consists of at least a function reference, and possibly also
 * an object to call it from.
 * @constructor
 * @class
 * This class represents a callback function which can be called standalone, or from a
 * given object. What the callback takes as arguments and what it returns are left to the
 * client.
 *
 * @author Conrad Damon
 * 
 * @param {object}	obj		the object to call the function from
 * @param {function}	func	the callback function
 * @param {array}	args   the default arguments
 * 
 */
AjxCallback = function(obj, func, args) {
	if (arguments.length == 0) return;

    if (typeof arguments[0] == "function") {
        this.obj = null;
        this.func = arguments[0];
        this.args = arguments[1];
    }
    else {
        this.obj = obj;
        this.func = func;
        this.args = args;
    }
};

AjxCallback.prototype.isAjxCallback = true;
AjxCallback.prototype.toString = function() { return "AjxCallback"; }

AjxCallback.NOP = new AjxCallback(function(){});

/**
 * Runs the callback function, from within the object if there is one. The
 * called function passed arguments are the concatenation of the argument
 * array passed to this object's constructor and the argument array passed
 * to the <code>run</code> method. Whatever the called function returns is
 * returned to the caller.
 *
 * @param {array}	[arg1..argN]	the first argument which will be appended to the argument
 *				array passed to this object's constructor. Any number of
 *				arguments may be passed to the <code>run</code> method.
 */
AjxCallback.prototype.run =
function(/* arg1 ... argN */) {
	// combine original args with new ones
	var args = [];

	// sometimes we want to pass a null or false argument, so simply
	// checking for if (this.args) won't do.
	if (typeof this.args != "undefined") {
		if (this.args!==null && this.args instanceof Array) { // IE mysteriously screws up the instanceof test if this.args is null
			// NOTE: We must NOT use this.args directly if this method's
			//       params are gonna be pushed onto the array because it
			//       will change the original args!
			args = arguments.length > 0 ? args.concat(this.args) : this.args;
		} else {
			args.push(this.args);
		}
	}

	for (var i = 0; i < arguments.length; ++i) {
		args.push(arguments[i]);
	}

	// invoke function
	if (this.func) {
        return this.func.apply(this.obj || window, args);
	}
};

/**
 * This version of {@link AjxCallback.run} is here for {@link AjxDispatcher}, because it has a <code>run()</code>
 * method in which it marshals arguments into an array. That leads to a problem
 * in which the arguments are marshalled twice, so that by the time AjxDispatcher
 * calls <code>callback.run(args)</code>, the args have already been collected into an array.
 * Then when the function is invoked, it gets passed an actual array instead of the
 * intended arg list. Calling <code>callback.run.apply(callback, args)</code> works on Firefox,
 * but IE throws the error "Object expected", so we do this instead.
 *
 * @param	{array}	argList	 an array of arguments and treats them as an argument list, instead of as a single argument
 * 
 * @private
 */
AjxCallback.prototype.run1 =
function(argList) {
	// combine original args with new ones
	var args = [];

	// sometimes we want to pass a null or false argument, so simply
	// checking for if (this.args) won't do.
	if (typeof this.args != "undefined") {
		if (this.args!==null && this.args instanceof Array) {
			// NOTE: We must NOT use this.args directly if this method's
			//       params are gonna be pushed onto the array because it
			//       will change the original args!
			args = arguments.length > 0 ? args.concat(this.args) : this.args;
		} else {
			args.push(this.args);
		}
	}

	if (argList && argList.length) {
		for (var i = 0; i < argList.length; ++i) {
			args.push(argList[i]);
		}
	}

	// invoke function
	if (this.func) {
		return this.func.apply(this.obj || window, args);
	}
};

/**
 * This method returns a plain function that will call your supplied "func" in the context
 * of "obj" and pass to it, in this order, any additional arguments that you
 * pass to <code>simpleClosure</code> and the arguments that were passed to it at the call
 * time.
 *
 * <p>
 * An example should do:
 *
 * <pre>
 *   div.onclick = AjxCallback.simpleClosure(this.handler, this, "some data");
 *   ...
 *   this.handler = function(data, event) {
 *      // event will be passed for DOM2 compliant browsers
 *      // and data is "some data"
 *   };
 * </pre>
 * 
 * @param	{function}	func		the function
 * @param	{object}	obj			the object to call the function from
 * @param	{array}	[arg1...argN]		any number of arguments
 */
AjxCallback.simpleClosure = function(func, obj) {
	var args = [];
	for (var i = 2; i < arguments.length; ++i)
		args.push(arguments[i]);
	return function() {
		var args2 = [];
		for (var i = 0; i < arguments.length; ++i)
			args2.push(arguments[i]);
		return func.apply(obj || this, args.concat(args2));
	};
};

AjxCallback.returnFalse = function() { return false; };

AjxCallback.isNull = function(x) { return x == null; };
