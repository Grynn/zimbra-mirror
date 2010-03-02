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
 * Creates an exception.
 * @constructor
 * @class
 * This is the base class for all exceptions in the Zimbra Ajax Toolkit.
 * 
 * @author Ross Dargahi
 * 
 * @param {String} 		[msg]		the human readable message
 * @param {String|int} 	[code]	any error or fault code
 * @param {String} 		[method] 	the name of the method throwing the exception
 * @param {String} 		[detail]		any additional detail
 */
AjxException = function(msg, code, method, detail) {
	if (arguments.length == 0) return;
	
	/** 
	 * Human readable message if applicable
	 * @type String
	 */
	this.msg = msg;
	
	/** 
	 * Error or fault code if applicable
	 * @type String|int
	 */
	this.code = code;
	
	/**
	 * Name of the method throwing the exception if applicable
	 * @type String
	 */
	this.method = method;
	
	/**
	 * Any additional detail
	 * @type String
	 */
	this.detail = detail;
}

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
AjxException.prototype.toString = 
function() {
	return "AjxException";
}

/**
 * Dumpes the exception.
 * 
 * @return {String}	the state of the exception
 */
AjxException.prototype.dump = 
function() {
	return "AjxException: msg="+this.msg+" code="+this.code+" method="+this.method+" detail="+this.detail;
}

/**
 * Invalid parent exception code.
 * @type String
 */
AjxException.INVALIDPARENT 			= "AjxException.INVALIDPARENT";

/** Invalid operation exception code.
 * @type String */
AjxException.INVALID_OP 			= "AjxException.INVALID_OP";

/** Internal error exception code.
 * @type String */
AjxException.INTERNAL_ERROR 		= "AjxException.INTERNAL_ERROR";

/** Invalid parameter to method/operation exception code.
 * @type String */
AjxException.INVALID_PARAM 			= "AjxException.INVALID_PARAM";

/** Unimplemented method called exception code.
 * @type String */
AjxException.UNIMPLEMENTED_METHOD 	= "AjxException.UNIMPLEMENTED_METHOD";

/** Network error exception code.
 * @type String */
AjxException.NETWORK_ERROR 			= "AjxException.NETWORK_ERROR";

/** Out or RPC cache exception code.
 * @type String */
AjxException.OUT_OF_RPC_CACHE		= "AjxException.OUT_OF_RPC_CACHE";

/** Unsupported operation code.
 * @type String */
AjxException.UNSUPPORTED 			= "AjxException.UNSUPPORTED";

/** Unknown error exception code.
 * @type String */
AjxException.UNKNOWN_ERROR 			= "AjxException.UNKNOWN_ERROR";

/** Operation canceled exception code.
 * @type String */
AjxException.CANCELED				= "AjxException.CANCELED";
