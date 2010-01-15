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
 * @constructor
 * @class
 * Base class for all exceptions in the toolkit
 * 
 * @author Ross Dargahi
 * 
 * @param {string} msg Human readable message (optional)
 * @param {string|number} code Any error or fault code (optional)
 * @param {string} method Name of the method throwing the exception (optional)
 * @param {string} detail Any additional detail (optional)
 */
DwtException = function(msg, code, method, detail) {
	if (arguments.length === 0) {return;}
	AjxException.call(this, msg, code, method, detail);
}

DwtException.prototype = new AjxException();
DwtException.prototype.constructor = DwtException;

/**
 * This method returns this class' name. Subclasses will
 * override this method to return their own name
 * 
 * @return class name
 * @type String
 */
DwtException.prototype.toString = 
function() {
	return "DwtException";
};

/** Invalid parent exception code
 * @type number */
DwtException.INVALIDPARENT = -1;

/** Invalid operation exception code
 * @type number */
DwtException.INVALID_OP = -2;

/** Internal error exception code
 * @type number */
DwtException.INTERNAL_ERROR = -3;

/** Invalid parameter exception code
 * @type number */
DwtException.INVALID_PARAM = -4;