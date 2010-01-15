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
* Creates a CSFE result object.
* @constructor
* @class
* This class represents the result of a CSFE request. The data is either the 
* response that was received, or an exception. If the request resulted in a 
* SOAP fault from the server, there will also be a SOAP header present.
*
* @author Conrad Damon
* @param data			[Object]	response data
* @param isException	[boolean]	true if the data is an exception object
* @param header			[object]	the SOAP header
*/
ZmCsfeResult = function(data, isException, header) {
	this.set(data, isException, header);
};

ZmCsfeResult.prototype.toString =
function() {
	return "ZmCsfeResult";
};

/**
* Sets the content of the result.
*
* @param data			[Object]	response data
* @param isException	[boolean]	true if the data is an exception object
* @param header			[object]	the SOAP header
*/
ZmCsfeResult.prototype.set =
function(data, isException, header) {
	this._data = data;
	this._isException = (isException === true);
	this._header = header;
};

/**
* Returns the response data. If there was an exception, throws the exception.
*/
ZmCsfeResult.prototype.getResponse =
function() {
	if (this._isException) {
		throw this._data;
	} else {
		return this._data;
	}
};

/**
* Returns the exception object, if any.
*/
ZmCsfeResult.prototype.getException =
function() {
	return this._isException ? this._data : null;
};

ZmCsfeResult.prototype.isException = 
function() {
	return this._isException;
};

/**
* Returns the SOAP header that came with a SOAP fault.
*/
ZmCsfeResult.prototype.getHeader =
function() {
	return this._header;
};
