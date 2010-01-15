/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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



function ZMTB_AjxSoapException(msg, code, method, detail) {
	ZMTB_AjxException.call(this, msg, code, method, detail);
}

ZMTB_AjxSoapException.prototype.toString = 
function() {
	return "ZMTB_AjxSoapException";
}

ZMTB_AjxSoapException.prototype = new ZMTB_AjxException;
ZMTB_AjxSoapException.prototype.constructor = ZMTB_AjxSoapException;

ZMTB_AjxSoapException.INTERNAL_ERROR 	= "INTERNAL_ERROR";
ZMTB_AjxSoapException.INVALID_PDU 		= "INVALID_PDU";
ZMTB_AjxSoapException.ELEMENT_EXISTS 	= "ELEMENT_EXISTS";
