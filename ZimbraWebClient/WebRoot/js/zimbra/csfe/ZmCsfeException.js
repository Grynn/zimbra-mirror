function ZmCsfeException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	LsException.call(this, msg, code, method, detail);
}

ZmCsfeException.prototype = new LsException;
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
