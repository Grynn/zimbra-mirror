/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates an exception.
 * @constructor
 * @class
 * This is the base class for all exceptions in the Ajax toolkit.
 * 
 * @author Ross Dargahi
 * 
 * @param {string} [msg] 		the human readable message
 * @param {string|number} [code]	 any error or fault code
 * @param {string} [method] 	the name of the method throwing the exception
 * @param {string} [detail] 	any additional detail
 * 
 * @extends		AjxException
 */
DwtException = function(msg, code, method, detail) {
	if (arguments.length === 0) {return;}
	AjxException.call(this, msg, code, method, detail);
}

DwtException.prototype = new AjxException();
DwtException.prototype.constructor = DwtException;

DwtException.prototype.toString = 
function() {
	return "DwtException";
};

/**
 * Invalid parent exception code.
 */
DwtException.INVALIDPARENT = -1;

/**
 * Invalid operation exception code.
 */
DwtException.INVALID_OP = -2;

/**
 * Internal error exception code.
 */
DwtException.INTERNAL_ERROR = -3;

/**
 * Invalid parameter exception code.
 */
DwtException.INVALID_PARAM = -4;
