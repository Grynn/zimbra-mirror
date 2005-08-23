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
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZmCsfeException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	AjxException.call(this, msg, code, method, detail);
}

ZmCsfeException.prototype = new AjxException;
ZmCsfeException.prototype.constructor = ZmCsfeException;

ZmCsfeException.prototype.toString = 
function() {
	return "ZmCsfeException";
}

ZmCsfeException.CSFE_SVC_ERROR 				= "CSFE_SVC_ERROR";
ZmCsfeException.NETWORK_ERROR 				= "NETWORK_ERROR";
ZmCsfeException.NO_AUTH_TOKEN 				= "NO_AUTH_TOKEN";
ZmCsfeException.SOAP_ERROR 					= "SOAP_ERROR";

// CSFE Exceptions
ZmCsfeException.SVC_AUTH_EXPIRED 			= "service.AUTH_EXPIRED";
ZmCsfeException.SVC_AUTH_REQUIRED 			= "service.AUTH_REQUIRED";
ZmCsfeException.SVC_FAILURE 				= "service.FAILURE";
ZmCsfeException.SVC_INVALID_REQUEST 		= "service.INVALID_REQUEST";
ZmCsfeException.SVC_PARSE_ERROR 			= "service.PARSE_ERROR";
ZmCsfeException.SVC_PERM_DENIED 			= "service.PERM_DENIED";
ZmCsfeException.SVC_UNKNOWN_DOCUMENT 		= "service.UNKNOWN_DOCUMENT";
ZmCsfeException.SVC_WRONG_HOST 				= "service.WRONG_HOST";

ZmCsfeException.ACCT_AUTH_FAILED 			= "account.AUTH_FAILED";
ZmCsfeException.ACCT_EXISTS 				= "account.ACCOUNT_EXISTS";
ZmCsfeException.ACCT_INVALID_PASSWORD 		= "account.INVALID_PASSWORD";
ZmCsfeException.ACCT_INVALID_PREF_NAME 		= "account.INVALID_PREF_NAME";
ZmCsfeException.ACCT_INVALID_PREF_VALUE 	= "account.INVALID_PREF_VALUE";
ZmCsfeException.ACCT_NO_SUCH_ACCOUNT 		= "account.NO_SUCH_ACCOUNT";
ZmCsfeException.ACCT_NO_SUCH_SAVED_SEARCH 	= "account.NO_SUCH_SAVED_SEARCH";
ZmCsfeException.ACCT_NO_SUCH_TAG 			= "account.ACCT_NO_SUCH_TAG";
ZmCsfeException.ACCT_PASS_RECENTLY_USED 	= "account.PASSWORD_RECENTLY_USED";
ZmCsfeException.DOMAIN_NOT_EMPTY			= "account.DOMAIN_NOT_EMPTY";

ZmCsfeException.COS_EXISTS 					= "account.COS_EXISTS";

ZmCsfeException.DOMAIN_EXISTS 				= "account.DOMAIN_EXISTS";

ZmCsfeException.MAIL_INVALID_NAME 			= "mail.INVALID_NAME";
ZmCsfeException.MAIL_NO_SUCH_FOLDER 		= "mail.NO_SUCH_FOLDER";
ZmCsfeException.MAIL_NO_SUCH_TAG 			= "mail.NO_SUCH_TAG";
ZmCsfeException.MAIL_NO_SUCH_CONV 			= "mail.NO_SUCH_CONV";
ZmCsfeException.MAIL_NO_SUCH_MSG 			= "mail.NO_SUCH_MSG";
ZmCsfeException.MAIL_NO_SUCH_PART 			= "mail.NO_SUCH_PART";
ZmCsfeException.MAIL_QUOTA_EXCEEDED 		= "mail.QUOTA_EXCEEDED";
ZmCsfeException.MAIL_QUERY_PARSE_ERROR 		= "mail.QUERY_PARSE_ERROR";
ZmCsfeException.MAIL_UNKNOWN_LOCAL_RECIPIENTS = "mail.UNKNOWN_LOCAL_RECIPIENTS";
