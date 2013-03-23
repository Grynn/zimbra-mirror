/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
 * @overview
 * This file contains the exception class.
 */
/**
 * Creates an exception.
 * @class
 * This class represents an exception returned by the server as a response, generally as a fault. The fault
 * data is converted to properties of the exception.
 *
 * @param {Hash}	params	a hash of parameters
 * @param {String}      params.msg		the explanation (Fault.Reason.Text)
 * @param {String}      params.code		the error code (Fault.Detail.Error.Code)
 * @param {String}      params.method	the request name
 * @param {String}      params.detail	the Fault.Code.Value
 * @param {Object}      [params.data]		an optional structured fault data (Fault.Detail.Error.a)
 * @param {String}      params.trace		the trace info (Fault.Detail.Error.Trace)
 * @param {String}       params.request	the SOAP or JSON that represents the request
 * 
 * @extends		AjxException
 */
ZmCsfeException = function(params) {

	params = Dwt.getParams(arguments, ZmCsfeException.PARAMS);

	AjxException.call(this, params.msg, params.code, params.method, params.detail);
	
	if (params.data) {
		this.data = {};
		for (var i = 0; i < params.data.length; i++) {
			var item = params.data[i];
			var key = item.n;
			if (!this.data[key]) {
				this.data[key] = [];
			}
			this.data[key].push(item._content);
		}
	}
	
	this.trace = params.trace;
	this.request = params.request;
};

ZmCsfeException.PARAMS = ["msg", "code", "method", "detail", "data", "trace"];

ZmCsfeException.prototype = new AjxException;
ZmCsfeException.prototype.constructor = ZmCsfeException;
ZmCsfeException.prototype.isZmCsfeException = true;

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmCsfeException.prototype.toString =
function() {
	return "ZmCsfeException";
};

//
// Constants
//

// structured data keys
ZmCsfeException.MAIL_SEND_ADDRESS_FAILURE_INVALID = "invalid";
ZmCsfeException.MAIL_SEND_ADDRESS_FAILURE_UNSENT = "unsent";

//
// Static functions
//

/**
 * Gets the error messages.
 * 
 * @param	{String}	code	the code
 * @param	{Array}	args		the message format args
 * 
 * @return	{String}	the message
 */
ZmCsfeException.getErrorMsg =
function(code, args) {
	var msg = ZMsg[code];
	if (!msg) {
		ZmCsfeException._unknownFormat = ZmCsfeException._unknownFormat || new AjxMessageFormat(ZMsg.unknownError);
		return ZmCsfeException._unknownFormat.format(code);
	}
	this.msg = this.msg || msg;
	return args ? AjxMessageFormat.format(msg, args) : msg;
};

//
// Public methods
//

/**
 * Gets the error message.
 * 
 * @param	{Array}	args		the message format args
 * @return	{String}	the message
 */
ZmCsfeException.prototype.getErrorMsg =
function(args) {
	return ZmCsfeException.getErrorMsg(this.code, args);
};

/**
 * Gets the data.
 * 
 * @param	{Object}	key		the key
 * 
 * @return	{Object}	the data
 */
ZmCsfeException.prototype.getData =
function(key) {
	return this.data && this.data[key];
};

//
// Constants for server exceptions
//

ZmCsfeException.AUTH_TOKEN_CHANGED					= "AUTH_TOKEN_CHANGED";
ZmCsfeException.BAD_JSON_RESPONSE					= "BAD_JSON_RESPONSE";
ZmCsfeException.CSFE_SVC_ERROR						= "CSFE_SVC_ERROR";
ZmCsfeException.EMPTY_RESPONSE						= "EMPTY_RESPONSE";
ZmCsfeException.NETWORK_ERROR						= "NETWORK_ERROR";
ZmCsfeException.NO_AUTH_TOKEN						= "NO_AUTH_TOKEN";
ZmCsfeException.SOAP_ERROR							= "SOAP_ERROR";

ZmCsfeException.LICENSE_ERROR						= "service.LICENSE_ERROR";
ZmCsfeException.SVC_ALREADY_IN_PROGRESS				= "service.ALREADY_IN_PROGRESS";
ZmCsfeException.SVC_AUTH_EXPIRED					= "service.AUTH_EXPIRED";
ZmCsfeException.SVC_AUTH_REQUIRED					= "service.AUTH_REQUIRED";
ZmCsfeException.SVC_FAILURE							= "service.FAILURE";
ZmCsfeException.SVC_INVALID_REQUEST					= "service.INVALID_REQUEST";
ZmCsfeException.SVC_PARSE_ERROR						= "service.PARSE_ERROR";
ZmCsfeException.SVC_PERM_DENIED						= "service.PERM_DENIED";
ZmCsfeException.SVC_RESOURCE_UNREACHABLE			= "service.RESOURCE_UNREACHABLE";
ZmCsfeException.SVC_UNKNOWN_DOCUMENT				= "service.UNKNOWN_DOCUMENT";
ZmCsfeException.SVC_TEMPORARILY_UNAVAILABLE			= "service.TEMPORARILY_UNAVAILABLE";
ZmCsfeException.SVC_WRONG_HOST						= "service.WRONG_HOST";

ZmCsfeException.ACCT_AUTH_FAILED					= "account.AUTH_FAILED";
ZmCsfeException.ACCT_CHANGE_PASSWORD				= "account.CHANGE_PASSWORD";
ZmCsfeException.ACCT_EXISTS							= "account.ACCOUNT_EXISTS";
ZmCsfeException.ACCT_TOO_MANY_ACCOUNTS      		= "account.TOO_MANY_ACCOUNTS" ;
ZmCsfeException.ACCT_INVALID_ATTR_VALUE				= "account.INVALID_ATTR_VALUE";
ZmCsfeException.ACCT_INVALID_PASSWORD				= "account.INVALID_PASSWORD";
ZmCsfeException.ACCT_INVALID_PREF_NAME				= "account.INVALID_PREF_NAME";
ZmCsfeException.ACCT_INVALID_PREF_VALUE				= "account.INVALID_PREF_VALUE";
ZmCsfeException.ACCT_MAINTENANCE_MODE				= "account.MAINTENANCE_MODE";
ZmCsfeException.ACCT_NO_SUCH_ACCOUNT				= "account.NO_SUCH_ACCOUNT";
ZmCsfeException.ACCT_NO_SUCH_SAVED_SEARCH			= "account.NO_SUCH_SAVED_SEARCH";
ZmCsfeException.ACCT_NO_SUCH_TAG					= "account.ACCT_NO_SUCH_TAG";
ZmCsfeException.ACCT_PASS_CHANGE_TOO_SOON			= "account.PASSWORD_CHANGE_TOO_SOON";
ZmCsfeException.ACCT_PASS_LOCKED					= "account.PASSWORD_LOCKED";
ZmCsfeException.ACCT_PASS_RECENTLY_USED				= "account.PASSWORD_RECENTLY_USED";
ZmCsfeException.COS_EXISTS							= "account.COS_EXISTS";
ZmCsfeException.DISTRIBUTION_LIST_EXISTS			= "account.DISTRIBUTION_LIST_EXISTS";
ZmCsfeException.DOMAIN_EXISTS						= "account.DOMAIN_EXISTS";
ZmCsfeException.DOMAIN_NOT_EMPTY					= "account.DOMAIN_NOT_EMPTY";
ZmCsfeException.IDENTITY_EXISTS						= "account.IDENTITY_EXISTS";
ZmCsfeException.NO_SUCH_DISTRIBUTION_LIST			= "account.NO_SUCH_DISTRIBUTION_LIST";
ZmCsfeException.NO_SUCH_DOMAIN						= "account.NO_SUCH_DOMAIN";
ZmCsfeException.MAINTENANCE_MODE					= "account.MAINTENANCE_MODE";
ZmCsfeException.TOO_MANY_IDENTITIES					= "account.TOO_MANY_IDENTITIES";
ZmCsfeException.TOO_MANY_SEARCH_RESULTS				= "account.TOO_MANY_SEARCH_RESULTS";
ZmCsfeException.NO_SUCH_COS 						= "account.NO_SUCH_COS";
ZmCsfeException.SIGNATURE_EXISTS                    = "account.SIGNATURE_EXISTS";

ZmCsfeException.CANNOT_CHANGE_VOLUME = "volume.CANNOT_CHANGE_TYPE_OF_CURRVOL";
ZmCsfeException.CANNOT_DELETE_VOLUME_IN_USE = "volume.CANNOT_DELETE_VOLUME_IN_USE";
ZmCsfeException.NO_SUCH_VOLUME						= "volume.NO_SUCH_VOLUME";
ZmCsfeException.ALREADY_EXISTS						= "volume.ALREADY_EXISTS";
ZmCsfeException.VOLUME_NO_SUCH_PATH					= "volume.NO_SUCH_PATH";

ZmCsfeException.MAIL_ALREADY_EXISTS					= "mail.ALREADY_EXISTS";
ZmCsfeException.MAIL_IMMUTABLE						= "mail.IMMUTABLE_OBJECT";
ZmCsfeException.MAIL_INVALID_NAME					= "mail.INVALID_NAME";
ZmCsfeException.MAIL_INVITE_OUT_OF_DATE				= "mail.INVITE_OUT_OF_DATE";
ZmCsfeException.MAIL_MAINTENANCE_MODE				= "mail.MAINTENANCE";
ZmCsfeException.MAIL_MESSAGE_TOO_BIG				= "mail.MESSAGE_TOO_BIG";
ZmCsfeException.MAIL_NO_SUCH_CALITEM				= "mail.NO_SUCH_CALITEM";
ZmCsfeException.MAIL_NO_SUCH_CONV					= "mail.NO_SUCH_CONV";
ZmCsfeException.MAIL_NO_SUCH_FOLDER					= "mail.NO_SUCH_FOLDER";
ZmCsfeException.MAIL_NO_SUCH_ITEM					= "mail.NO_SUCH_ITEM";
ZmCsfeException.MAIL_NO_SUCH_MOUNTPOINT				= "mail.NO_SUCH_MOUNTPOINT";
ZmCsfeException.MAIL_NO_SUCH_MSG					= "mail.NO_SUCH_MSG";
ZmCsfeException.MAIL_NO_SUCH_PART					= "mail.NO_SUCH_PART";
ZmCsfeException.MAIL_NO_SUCH_TAG					= "mail.NO_SUCH_TAG";
ZmCsfeException.MAIL_QUERY_PARSE_ERROR				= "mail.QUERY_PARSE_ERROR";
ZmCsfeException.MAIL_QUOTA_EXCEEDED					= "mail.QUOTA_EXCEEDED";
ZmCsfeException.MAIL_SEND_ABORTED_ADDRESS_FAILURE	= "mail.SEND_ABORTED_ADDRESS_FAILURE";
ZmCsfeException.MAIL_SEND_FAILURE					= "mail.SEND_FAILURE";
ZmCsfeException.MAIL_TOO_MANY_CONTACTS				= "mail.TOO_MANY_CONTACTS";
ZmCsfeException.MAIL_TOO_MANY_TERMS					= "mail.TOO_MANY_QUERY_TERMS_EXPANDED";
ZmCsfeException.MAIL_UNABLE_TO_IMPORT_APPOINTMENTS	= "mail.MAIL_UNABLE_TO_IMPORT_APPOINTMENTS";
ZmCsfeException.MAIL_UNABLE_TO_IMPORT_CONTACTS		= "mail.UNABLE_TO_IMPORT_CONTACTS";
ZmCsfeException.MODIFY_CONFLICT						= "mail.MODIFY_CONFLICT";
ZmCsfeException.TOO_MANY_TAGS						= "mail.TOO_MANY_TAGS";
ZmCsfeException.CANNOT_RENAME                       = "mail.CANNOT_RENAME";
ZmCsfeException.CANNOT_UNLOCK                       = "mail.CANNOT_UNLOCK";
ZmCsfeException.CANNOT_LOCK                         = "mail.CANNOT_LOCK";
ZmCsfeException.LOCKED                              = "mail.LOCKED";


ZmCsfeException.OFFLINE_ONLINE_ONLY_OP				= "offline.ONLINE_ONLY_OP";
