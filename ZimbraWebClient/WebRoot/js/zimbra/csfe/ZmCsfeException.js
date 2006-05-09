/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZmCsfeException(msg, code, method, detail, data) {
	AjxException.call(this, msg, code, method, detail);
	
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

ZmCsfeException._codeToMsg = {};

ZmCsfeException.define =
function(name, code, msg) {
	ZmCsfeException[name] = code;
	ZmCsfeException._codeToMsg[code] = msg;
};

ZmCsfeException.prototype = new AjxException;
ZmCsfeException.prototype.constructor = ZmCsfeException;

ZmCsfeException.prototype.toString = 
function() {
	return "ZmCsfeException";
};

ZmCsfeException.prototype.getErrorMsg =
function(args) {
	return args ? AjxMessageFormat.format(ZmCsfeException._codeToMsg[this.code], args) : ZmCsfeException._codeToMsg[this.code];
};

ZmCsfeException.prototype.getData =
function(key) {
	return this.data ? this.data[key] : null;
};

ZmCsfeException.define("CSFE_SVC_ERROR", "CSFE_SVC_ERROR", ZMsg.errorService);
ZmCsfeException.define("NETWORK_ERROR", "NETWORK_ERROR", ZMsg.errorNetwork);
ZmCsfeException.define("NO_AUTH_TOKEN", "NO_AUTH_TOKEN");
ZmCsfeException.define("SOAP_ERROR", "SOAP_ERROR", ZMsg.errorNetwork);

// CSFE Exceptions
ZmCsfeException.define("SVC_ALREADY_IN_PROGRESS", "service.ALREADY_IN_PROGRESS");
ZmCsfeException.define("SVC_AUTH_EXPIRED", "service.AUTH_EXPIRED");
ZmCsfeException.define("SVC_AUTH_REQUIRED", "service.AUTH_REQUIRED");
ZmCsfeException.define("SVC_FAILURE", "service.FAILURE", ZMsg.errorService);
ZmCsfeException.define("SVC_INVALID_REQUEST", "service.INVALID_REQUEST");
ZmCsfeException.define("SVC_PARSE_ERROR", "service.PARSE_ERROR", ZMsg.errorParse);
ZmCsfeException.define("SVC_PERM_DENIED", "service.PERM_DENIED", ZMsg.errorPermission);
ZmCsfeException.define("SVC_RESOURCE_UNREACHABLE", "service.RESOURCE_UNREACHABLE");
ZmCsfeException.define("SVC_UNKNOWN_DOCUMENT", "service.UNKNOWN_DOCUMENT", ZMsg.errorUnknownDoc);
ZmCsfeException.define("SVC_WRONG_HOST", "service.WRONG_HOST");
ZmCsfeException.define("SVC_TEMPORARILY_UNAVAILABLE", "service.TEMPORARILY_UNAVAILABLE");


ZmCsfeException.define("ACCT_AUTH_FAILED", "account.AUTH_FAILED");
ZmCsfeException.define("ACCT_CHANGE_PASSWORD", "account.CHANGE_PASSWORD", ZMsg.errorPassChange);
ZmCsfeException.define("ACCT_EXISTS", "account.ACCOUNT_EXISTS");
ZmCsfeException.define("ACCT_INVALID_PASSWORD", "account.INVALID_PASSWORD", ZMsg.errorInvalidPass);
ZmCsfeException.define("ACCT_INVALID_PREF_NAME", "account.INVALID_PREF_NAME", ZMsg.errorInvalidPrefName);
ZmCsfeException.define("ACCT_INVALID_PREF_VALUE", "account.INVALID_PREF_VALUE", ZMsg.errorInvalidPrefValue);
ZmCsfeException.define("ACCT_MAINTENANCE_MODE", "account.MAINTENANCE_MODE", ZMsg.errorMaintenanceMode);
ZmCsfeException.define("ACCT_NO_SUCH_ACCOUNT", "account.NO_SUCH_ACCOUNT", ZMsg.errorNoSuchAcct);
ZmCsfeException.define("NO_SUCH_DISTRIBUTION_LIST", "account.NO_SUCH_DISTRIBUTION_LIST");
ZmCsfeException.define("ACCT_NO_SUCH_SAVED_SEARCH", "account.NO_SUCH_SAVED_SEARCH", ZMsg.errorNoSuchSavedSearch);
ZmCsfeException.define("ACCT_NO_SUCH_TAG", "account.ACCT_NO_SUCH_TAG", ZMsg.errorNoSuchTag);
ZmCsfeException.define("ACCT_PASS_CHANGE_TOO_SOON", "account.PASSWORD_CHANGE_TOO_SOON", ZMsg.errorPassChangeTooSoon);
ZmCsfeException.define("ACCT_PASS_LOCKED", "account.PASSWORD_LOCKED", ZMsg.errorPassLocked);
ZmCsfeException.define("ACCT_PASS_RECENTLY_USED", "account.PASSWORD_RECENTLY_USED", ZMsg.errorPassRecentlyUsed);
ZmCsfeException.define("DOMAIN_NOT_EMPTY", "account.DOMAIN_NOT_EMPTY");
ZmCsfeException.define("DISTRIBUTION_LIST_EXISTS", "account.DISTRIBUTION_LIST_EXISTS");

ZmCsfeException.define("COS_EXISTS", "account.COS_EXISTS");

ZmCsfeException.define("DOMAIN_EXISTS", "account.DOMAIN_EXISTS");

ZmCsfeException.define("MAIL_ALREADY_EXISTS", "mail.ALREADY_EXISTS", ZMsg.errorAlreadyExists);
ZmCsfeException.define("MAIL_INVALID_NAME", "mail.INVALID_NAME", ZMsg.errorInvalidName);
ZmCsfeException.define("MAIL_NO_SUCH_CONV", "mail.NO_SUCH_CONV", ZMsg.errorNoSuchConv);
ZmCsfeException.define("MAIL_NO_SUCH_FOLDER", "mail.NO_SUCH_FOLDER", ZMsg.errorNoSuchFolder);
ZmCsfeException.define("MAIL_NO_SUCH_MSG", "mail.NO_SUCH_MSG", ZMsg.errorNoSuchMsg);
ZmCsfeException.define("MAIL_NO_SUCH_PART", "mail.NO_SUCH_PART", ZMsg.errorNoSuchPart);
ZmCsfeException.define("MAIL_NO_SUCH_TAG", "mail.NO_SUCH_TAG", ZMsg.errorNoSuchTag);
ZmCsfeException.define("MAIL_QUERY_PARSE_ERROR", "mail.QUERY_PARSE_ERROR", ZMsg.errorQueryParse);
ZmCsfeException.define("MAIL_QUOTA_EXCEEDED", "mail.QUOTA_EXCEEDED", ZMsg.errorQuotaExceeded);
ZmCsfeException.define("MAIL_SEND_ABORTED_ADDRESS_FAILURE", "mail.SEND_ABORTED_ADDRESS_FAILURE", ZMsg.mailSendAddressFailure);
ZmCsfeException.define("MAIL_SEND_FAILURE", "mail.SEND_FAILURE", ZMsg.mailSendFailure);
ZmCsfeException.define("MAIL_TOO_MANY_CONTACTS", "mail.TOO_MANY_CONTACTS", ZMsg.errorTooManyContacts);
ZmCsfeException.define("MAIL_TOO_MANY_TERMS", "mail.TOO_MANY_QUERY_TERMS_EXPANDED", ZMsg.errorTooManyTerms);
ZmCsfeException.define("MAIL_UNABLE_TO_IMPORT_CONTACTS", "mail.UNABLE_TO_IMPORT_CONTACTS", ZMsg.errorUnableToImport);

ZmCsfeException.define("VOLUME_NO_SUCH_PATH", "volume.NO_SUCH_PATH");
// structured data keys
ZmCsfeException.MAIL_SEND_ADDRESS_FAILURE_INVALID = "invalid";
ZmCsfeException.MAIL_SEND_ADDRESS_FAILURE_UNSENT = "unsent";
