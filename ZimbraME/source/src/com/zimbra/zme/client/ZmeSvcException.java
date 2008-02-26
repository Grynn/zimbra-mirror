/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.client;

import java.util.Hashtable;

public class ZmeSvcException extends Exception {
	
	public static final String SVC_FAILURE = "Service Failure";
	public static final String SVC_PERMDENIED = "service.PERM_DENIED";
	public static final String SVC_AUTHREQUIRED = "service.AUTH_REQUIRED";
	public static final String SVC_AUTHEXPIRED = "service.AUTH_EXPIRED";
	public static final String ACCT_AUTHFAILED = "account.AUTH_FAILED";
	public static final String ACCT_CHANGEPWORD = "account.CHANGE_PASSWORD";
	public static final String ACCT_PWORDLOCKED = "account.PASSWORD_LOCKED";
	public static final String ACCT_PWORDTOOSOON = "account.PASSWORD_CHANGE_TOO_SOON";
	public static final String ACCT_PWORDTOORECENT = "account.PASSWORD_RECENTLY_USED";
	public static final String ACCT_NOSUCHACCT = "account.NO_SUCH_ACCOUNT";
	public static final String ACCT_INVALIDPWORD = "account.INVALID_PASSWORD";
	public static final String MAIL_ALREADYEXISTS = "mail.ALREADY_EXISTS";
	public static final String MAIL_NOSUCHCONV = "mail.NO_SUCH_CONV";
	public static final String MAIL_NOSUCHMSG = "mail.NO_SUCH_MSG";
	public static final String MAIL_NOSUCHPART = "mail.NO_SUCH_PART";
	public static final String MAIL_QUERYPARSEERROR = "mail.QUERY_PARSE_ERROR";
	public static final String MAIL_NOSUCHCONTACT = "mail.NO_SUCH_CONTACT";
	public static final String MAIL_MODIFYCONFLICT = "mail.MODIFY_CONFLICT";
	
	private static final Hashtable mErrorMap = new Hashtable();
	
	{
		mErrorMap.put(SVC_PERMDENIED, SVC_PERMDENIED);
		mErrorMap.put(SVC_AUTHREQUIRED, SVC_AUTHREQUIRED);
		mErrorMap.put(SVC_AUTHEXPIRED, SVC_AUTHEXPIRED);
		mErrorMap.put(ACCT_AUTHFAILED, ACCT_AUTHFAILED);
		mErrorMap.put(ACCT_CHANGEPWORD, ACCT_CHANGEPWORD);
		mErrorMap.put(ACCT_PWORDLOCKED, ACCT_PWORDLOCKED);
		mErrorMap.put(ACCT_PWORDTOOSOON, ACCT_PWORDTOOSOON);
		mErrorMap.put(ACCT_PWORDTOORECENT, ACCT_PWORDTOORECENT);
		mErrorMap.put(ACCT_NOSUCHACCT, ACCT_NOSUCHACCT);
		mErrorMap.put(ACCT_INVALIDPWORD, ACCT_INVALIDPWORD);
		mErrorMap.put(MAIL_ALREADYEXISTS, MAIL_ALREADYEXISTS);
		mErrorMap.put(MAIL_NOSUCHCONV, MAIL_NOSUCHCONV);
		mErrorMap.put(MAIL_NOSUCHMSG, MAIL_NOSUCHMSG);
		mErrorMap.put(MAIL_NOSUCHPART, MAIL_NOSUCHPART);
		mErrorMap.put(MAIL_QUERYPARSEERROR, MAIL_QUERYPARSEERROR);
		mErrorMap.put(MAIL_NOSUCHCONTACT, MAIL_NOSUCHCONTACT);
		mErrorMap.put(MAIL_MODIFYCONFLICT, MAIL_MODIFYCONFLICT);
	}
	
	public String mErrorCode;
	public Hashtable mErrorAttrs;
	
	public ZmeSvcException(String errorCode,
						   Hashtable errorAttrs) {
		String fc = (String)mErrorMap.get(errorCode);
		mErrorCode = (fc == null) ? SVC_FAILURE : fc;
		mErrorAttrs = errorAttrs;
	}
}
