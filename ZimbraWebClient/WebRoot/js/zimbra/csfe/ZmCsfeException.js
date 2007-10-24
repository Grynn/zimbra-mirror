/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

ZmCsfeException = function(msg, code, method, detail, data) {
	AjxException.call(this, msg, code, method, detail);
	
	if (data) {
		this.data = {};
		for (var i = 0; i < data.length; i++) {
			var item = data[i];
			var key = item.n;
			if (!this.data[key]) {
				this.data[key] = [];
			}
			this.data[key].push(item._content);
		}
	}
};

ZmCsfeException.prototype = new AjxException;
ZmCsfeException.prototype.constructor = ZmCsfeException;

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

ZmCsfeException.getErrorMsg =
function(code, args) {
	var msg = ZMsg[code];
	if (!msg) { return ""; }
	this.msg = this.msg || msg;
	return args ? AjxMessageFormat.format(msg, args) : msg;
};

//
// Public methods
//

ZmCsfeException.prototype.getErrorMsg =
function(args) {
	return ZmCsfeException.getErrorMsg(this.code, args);
};

ZmCsfeException.prototype.getData =
function(key) {
	return this.data ? this.data[key] : null;
};

//
// Constants for server exceptions
//

ZmCsfeException.AUTH_TOKEN_CHANGED	= "AUTH_TOKEN_CHANGED";
ZmCsfeException.BAD_JSON_RESPONSE	= "BAD_JSON_RESPONSE";
ZmCsfeException.CSFE_SVC_ERROR		= "CSFE_SVC_ERROR";
ZmCsfeException.EMPTY_RESPONSE		= "EMPTY_RESPONSE";
ZmCsfeException.NETWORK_ERROR		= "NETWORK_ERROR";
ZmCsfeException.NO_AUTH_TOKEN		= "NO_AUTH_TOKEN";
ZmCsfeException.SOAP_ERROR			= "SOAP_ERROR";

ZmCsfeException.LICENSE_ERROR				= "service.LICENSE_ERROR";
ZmCsfeException.SVC_ALREADY_IN_PROGRESS		= "service.ALREADY_IN_PROGRESS";
ZmCsfeException.SVC_AUTH_EXPIRED			= "service.AUTH_EXPIRED";
ZmCsfeException.SVC_AUTH_REQUIRED			= "service.AUTH_REQUIRED";
ZmCsfeException.SVC_FAILURE					= "service.FAILURE";
ZmCsfeException.SVC_INVALID_REQUEST			= "service.INVALID_REQUEST";
ZmCsfeException.SVC_PARSE_ERROR				= "service.PARSE_ERROR";
ZmCsfeException.SVC_PERM_DENIED				= "service.PERM_DENIED";
ZmCsfeException.SVC_RESOURCE_UNREACHABLE	= "service.RESOURCE_UNREACHABLE";
ZmCsfeException.SVC_UNKNOWN_DOCUMENT		= "service.UNKNOWN_DOCUMENT";
ZmCsfeException.SVC_TEMPORARILY_UNAVAILABLE	= "service.TEMPORARILY_UNAVAILABLE";
ZmCsfeException.SVC_WRONG_HOST				= "service.WRONG_HOST";

ZmCsfeException.ACCT_AUTH_FAILED			= "account.AUTH_FAILED";
ZmCsfeException.ACCT_CHANGE_PASSWORD		= "account.CHANGE_PASSWORD";
ZmCsfeException.ACCT_EXISTS					= "account.ACCOUNT_EXISTS";
ZmCsfeException.ACCT_INVALID_PASSWORD		= "account.INVALID_PASSWORD";
ZmCsfeException.ACCT_INVALID_PREF_NAME		= "account.INVALID_PREF_NAME";
ZmCsfeException.ACCT_INVALID_PREF_VALUE		= "account.INVALID_PREF_VALUE";
ZmCsfeException.ACCT_MAINTENANCE_MODE		= "account.MAINTENANCE_MODE";
ZmCsfeException.ACCT_NO_SUCH_ACCOUNT		= "account.NO_SUCH_ACCOUNT";
ZmCsfeException.ACCT_NO_SUCH_SAVED_SEARCH	= "account.NO_SUCH_SAVED_SEARCH";
ZmCsfeException.ACCT_NO_SUCH_TAG			= "account.ACCT_NO_SUCH_TAG";
ZmCsfeException.ACCT_PASS_CHANGE_TOO_SOON	= "account.PASSWORD_CHANGE_TOO_SOON";
ZmCsfeException.ACCT_PASS_LOCKED			= "account.PASSWORD_LOCKED";
ZmCsfeException.ACCT_PASS_RECENTLY_USED		= "account.PASSWORD_RECENTLY_USED";
ZmCsfeException.COS_EXISTS					= "account.COS_EXISTS";
ZmCsfeException.DISTRIBUTION_LIST_EXISTS	= "account.DISTRIBUTION_LIST_EXISTS";
ZmCsfeException.DOMAIN_EXISTS				= "account.DOMAIN_EXISTS";
ZmCsfeException.DOMAIN_NOT_EMPTY			= "account.DOMAIN_NOT_EMPTY";
ZmCsfeException.IDENTITY_EXISTS				= "account.IDENTITY_EXISTS";
ZmCsfeException.NO_SUCH_DISTRIBUTION_LIST	= "account.NO_SUCH_DISTRIBUTION_LIST";
ZmCsfeException.NO_SUCH_DOMAIN				= "account.NO_SUCH_DOMAIN";
ZmCsfeException.MAINTENANCE_MODE			= "account.MAINTENANCE_MODE";
ZmCsfeException.TOO_MANY_IDENTITIES			= "account.TOO_MANY_IDENTITIES";
ZmCsfeException.TOO_MANY_SEARCH_RESULTS		= "account.TOO_MANY_SEARCH_RESULTS";

ZmCsfeException.MAIL_ALREADY_EXISTS					= "mail.ALREADY_EXISTS";
ZmCsfeException.MAIL_IMMUTABLE						= "mail.IMMUTABLE_OBJECT";
ZmCsfeException.MAIL_INVALID_NAME					= "mail.INVALID_NAME";
ZmCsfeException.MAIL_MAINTENANCE_MODE				= "mail.MAINTENANCE_MODE";
ZmCsfeException.MAIL_NO_SUCH_CONV					= "mail.NO_SUCH_CONV";
ZmCsfeException.MAIL_NO_SUCH_FOLDER					= "mail.NO_SUCH_FOLDER";
ZmCsfeException.MAIL_NO_SUCH_ITEM					= "mail.NO_SUCH_ITEM";
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

ZmCsfeException.VOLUME_NO_SUCH_PATH = "volume.NO_SUCH_PATH";
