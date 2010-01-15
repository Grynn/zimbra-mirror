/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2009 Zimbra, Inc.
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

function ZMTBCsfeException(msg, code, method, detail, data, requestId) {
	ZMTB_AjxException.call(this, msg, code, method, detail);
	this.requestId = requestId;
	if (data) {
		this.data = {};
		for (var i = 0; i < data.length; i++) {
			var item = data[i];
			var key = item.n;
			if (!this.data[key])
				this.data[key] = [];
			this.data[key].push(item._content);
		}
	}
};

ZMTBCsfeException.prototype = new ZMTB_AjxException;
ZMTBCsfeException.prototype.constructor = ZMTBCsfeException;

ZMTBCsfeException.prototype.toString =
function() {
	return "ZMTBCsfeException";
};

//
// Constants
//

// structured data keys
ZMTBCsfeException.MAIL_SEND_ADDRESS_FAILURE_INVALID = "invalid";
ZMTBCsfeException.MAIL_SEND_ADDRESS_FAILURE_UNSENT = "unsent";

//
// Static data

ZMTBCsfeException._codeToMsg = {};

//
// Static functions
//

ZMTBCsfeException.define =
function(name, code, msg) {
	ZMTBCsfeException[name] = code;
	ZMTBCsfeException._codeToMsg[code] = msg;
};

ZMTBCsfeException.getErrorMsg = function(code, args) {
	return args ? AjxMessageFormat.format(ZMTBCsfeException._codeToMsg[code], args) : ZMTBCsfeException._codeToMsg[code];
};

//
// Public methods
//

ZMTBCsfeException.prototype.getErrorMsg =
function(args) {
	return ZMTBCsfeException.getErrorMsg(this.code, args);
};

ZMTBCsfeException.prototype.getData =
function(key) {
	return this.data ? this.data[key] : null;
};

//
// Hack! Fix me
//
ZMsg = new Array();
ZMsg.errorAlreadyExists = "An object with that name already exists."
ZMsg.errorModifyConflict = "Item version number conflict. Another version was added between edit and save."
ZMsg.errorInvalidName = "Sorry, \"{0}\" is not a valid name. It contains at least one invalid character."
ZMsg.errorInvalidPass = "You have entered an invalid password."
ZMsg.errorInvalidPrefName = "Invalid preference name."
ZMsg.errorInvalidPrefValue = "Invalid preference value."
ZMsg.errorMaintenanceMode = "This account is currently in maintenance mode."
ZMsg.errorNetwork = "A network error has occurred."
ZMsg.errorNoSuchAcct = "No such account exists."
ZMsg.errorNoSuchConv = "No such conversation exists."
ZMsg.errorNoSuchFolder = "No such folder exists."
ZMsg.errorNoSuchMsg = "No such message exists."
ZMsg.errorNoSuchPart = "No such message part exists."
ZMsg.errorNoSuchSavedSearch = "No such saved search exists."
ZMsg.errorNoSuchTag = "No such tag exists."
ZMsg.errorParse = "A parsing error has occurred."
ZMsg.errorPassChange = "Your password in no longer valid. Please choose a new password."
ZMsg.errorPassChangeTooSoon = "Password cannot be changed too soon."
ZMsg.errorPassLocked = "Password locked. User not allowed to change password. Please contact your System Administrator."
ZMsg.errorPassRecentlyUsed = "The password you submitted has recently been used. Please choose a different password."
ZMsg.errorPermission = "Permission denied."
ZMsg.errorQueryParse = "Unable to parse the search query."
ZMsg.errorQuotaExceeded = "Your message could not be sent because you have exceeded your mail quota."
ZMsg.errorService = "A network service error has occurred."
ZMsg.errorTooManyContacts = "Contact could not be created because you have exceeded your contact quota."
ZMsg.errorTooManyTerms = "Please be more specific in your search criteria."
ZMsg.errorUnableToImport = "Unable to import contacts. The .CSV file may be invalid."
ZMsg.errorUnknownDoc = "Unknown document."
ZMsg.mailSendFailure = "Could not send message: {0}"
ZMsg.mailSendAddressFailure = "Could not send message due to invalid address(es)"
ZMsg.errorEmptyResponse = "Received empty response form server"
ZMsg.errorTooManyIdentities = "The identity could not be created because you have exceeded your identity quota."


//
// Static initializer
//

ZMTBCsfeException.define("CSFE_SVC_ERROR", "CSFE_SVC_ERROR", ZMsg.errorService);
ZMTBCsfeException.define("NETWORK_ERROR", "NETWORK_ERROR", ZMsg.errorNetwork);
ZMTBCsfeException.define("NO_AUTH_TOKEN", "NO_AUTH_TOKEN");
ZMTBCsfeException.define("SOAP_ERROR", "SOAP_ERROR", ZMsg.errorNetwork);

// CSFE Exceptions
ZMTBCsfeException.define("SVC_ALREADY_IN_PROGRESS", "service.ALREADY_IN_PROGRESS");
ZMTBCsfeException.define("SVC_AUTH_EXPIRED", "service.AUTH_EXPIRED");
ZMTBCsfeException.define("SVC_AUTH_REQUIRED", "service.AUTH_REQUIRED");
ZMTBCsfeException.define("SVC_FAILURE", "service.FAILURE", ZMsg.errorService);
ZMTBCsfeException.define("SVC_INVALID_REQUEST", "service.INVALID_REQUEST");
ZMTBCsfeException.define("SVC_PARSE_ERROR", "service.PARSE_ERROR", ZMsg.errorParse);
ZMTBCsfeException.define("SVC_PERM_DENIED", "service.PERM_DENIED", ZMsg.errorPermission);
ZMTBCsfeException.define("SVC_RESOURCE_UNREACHABLE", "service.RESOURCE_UNREACHABLE");
ZMTBCsfeException.define("SVC_UNKNOWN_DOCUMENT", "service.UNKNOWN_DOCUMENT", ZMsg.errorUnknownDoc);
ZMTBCsfeException.define("SVC_WRONG_HOST", "service.WRONG_HOST");
ZMTBCsfeException.define("SVC_TEMPORARILY_UNAVAILABLE", "service.TEMPORARILY_UNAVAILABLE");


ZMTBCsfeException.define("ACCT_AUTH_FAILED", "account.AUTH_FAILED");
ZMTBCsfeException.define("ACCT_CHANGE_PASSWORD", "account.CHANGE_PASSWORD", ZMsg.errorPassChange);
ZMTBCsfeException.define("ACCT_EXISTS", "account.ACCOUNT_EXISTS");
ZMTBCsfeException.define("ACCT_INVALID_PASSWORD", "account.INVALID_PASSWORD", ZMsg.errorInvalidPass);
ZMTBCsfeException.define("ACCT_INVALID_PREF_NAME", "account.INVALID_PREF_NAME", ZMsg.errorInvalidPrefName);
ZMTBCsfeException.define("ACCT_INVALID_PREF_VALUE", "account.INVALID_PREF_VALUE", ZMsg.errorInvalidPrefValue);
ZMTBCsfeException.define("ACCT_MAINTENANCE_MODE", "account.MAINTENANCE_MODE", ZMsg.errorMaintenanceMode);
ZMTBCsfeException.define("ACCT_NO_SUCH_ACCOUNT", "account.NO_SUCH_ACCOUNT", ZMsg.errorNoSuchAcct);
ZMTBCsfeException.define("NO_SUCH_DISTRIBUTION_LIST", "account.NO_SUCH_DISTRIBUTION_LIST");
ZMTBCsfeException.define("ACCT_NO_SUCH_SAVED_SEARCH", "account.NO_SUCH_SAVED_SEARCH", ZMsg.errorNoSuchSavedSearch);
ZMTBCsfeException.define("ACCT_NO_SUCH_TAG", "account.ACCT_NO_SUCH_TAG", ZMsg.errorNoSuchTag);
ZMTBCsfeException.define("ACCT_PASS_CHANGE_TOO_SOON", "account.PASSWORD_CHANGE_TOO_SOON", ZMsg.errorPassChangeTooSoon);
ZMTBCsfeException.define("ACCT_PASS_LOCKED", "account.PASSWORD_LOCKED", ZMsg.errorPassLocked);
ZMTBCsfeException.define("ACCT_PASS_RECENTLY_USED", "account.PASSWORD_RECENTLY_USED", ZMsg.errorPassRecentlyUsed);
ZMTBCsfeException.define("DOMAIN_NOT_EMPTY", "account.DOMAIN_NOT_EMPTY");
ZMTBCsfeException.define("DISTRIBUTION_LIST_EXISTS", "account.DISTRIBUTION_LIST_EXISTS");
ZMTBCsfeException.define("IDENTITY_EXISTS", "account.IDENTITY_EXISTS");
ZMTBCsfeException.define("TOO_MANY_IDENTITIES", "account.TOO_MANY_IDENTITIES", ZMsg.errorTooManyIdentities);

ZMTBCsfeException.define("COS_EXISTS", "account.COS_EXISTS");

ZMTBCsfeException.define("DOMAIN_EXISTS", "account.DOMAIN_EXISTS");

ZMTBCsfeException.define("MAIL_ALREADY_EXISTS", "mail.ALREADY_EXISTS", ZMsg.errorAlreadyExists);
ZMTBCsfeException.define("MODIFY_CONFLICT", "mail.MODIFY_CONFLICT", ZMsg.errorModifyConflict);
ZMTBCsfeException.define("MAIL_INVALID_NAME", "mail.INVALID_NAME", ZMsg.errorInvalidName);
ZMTBCsfeException.define("MAIL_MAINTENANCE_MODE", "mail.MAINTENANCE_MODE", ZMsg.errorMaintenanceMode);
ZMTBCsfeException.define("MAIL_NO_SUCH_CONV", "mail.NO_SUCH_CONV", ZMsg.errorNoSuchConv);
ZMTBCsfeException.define("MAIL_NO_SUCH_FOLDER", "mail.NO_SUCH_FOLDER", ZMsg.errorNoSuchFolder);
ZMTBCsfeException.define("MAIL_NO_SUCH_MSG", "mail.NO_SUCH_MSG", ZMsg.errorNoSuchMsg);
ZMTBCsfeException.define("MAIL_NO_SUCH_PART", "mail.NO_SUCH_PART", ZMsg.errorNoSuchPart);
ZMTBCsfeException.define("MAIL_NO_SUCH_TAG", "mail.NO_SUCH_TAG", ZMsg.errorNoSuchTag);
ZMTBCsfeException.define("MAIL_QUERY_PARSE_ERROR", "mail.QUERY_PARSE_ERROR", ZMsg.errorQueryParse);
ZMTBCsfeException.define("MAIL_QUOTA_EXCEEDED", "mail.QUOTA_EXCEEDED", ZMsg.errorQuotaExceeded);
ZMTBCsfeException.define("MAIL_SEND_ABORTED_ADDRESS_FAILURE", "mail.SEND_ABORTED_ADDRESS_FAILURE", ZMsg.mailSendAddressFailure);
ZMTBCsfeException.define("MAIL_SEND_FAILURE", "mail.SEND_FAILURE", ZMsg.mailSendFailure);
ZMTBCsfeException.define("MAIL_TOO_MANY_CONTACTS", "mail.TOO_MANY_CONTACTS", ZMsg.errorTooManyContacts);
ZMTBCsfeException.define("MAIL_TOO_MANY_TERMS", "mail.TOO_MANY_QUERY_TERMS_EXPANDED", ZMsg.errorTooManyTerms);
ZMTBCsfeException.define("MAIL_UNABLE_TO_IMPORT_CONTACTS", "mail.UNABLE_TO_IMPORT_CONTACTS", ZMsg.errorUnableToImport);

ZMTBCsfeException.define("VOLUME_NO_SUCH_PATH", "volume.NO_SUCH_PATH");
ZMTBCsfeException.define("LICENSE_ERROR", "service.LICENSE_ERROR");
