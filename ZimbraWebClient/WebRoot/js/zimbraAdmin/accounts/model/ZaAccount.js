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

/**
* @class ZaAccount
* @contructor ZaAccount
* @param ZaApp app
* this class is a model for zimbraAccount ldap objects
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaAccount = function(app) {
	if (arguments.length == 0) return;	
	ZaItem.call(this, app,"ZaAccount");
	this._init(app);
	this.type = ZaItem.ACCOUNT;
}

ZaAccount.prototype = new ZaItem;
ZaAccount.prototype.constructor = ZaAccount;

ZaItem.loadMethods["ZaAccount"] = new Array();
ZaItem.initMethods["ZaAccount"] = new Array();
ZaItem.modifyMethods["ZaAccount"] = new Array();
ZaItem.createMethods["ZaAccount"] = new Array();
ZaAccount.renameMethods = new Array();
ZaAccount.changePasswordMethods = new Array();

//object attributes
ZaAccount.A_name = "name";
ZaAccount.A_uid = "uid";
ZaAccount.A_accountName = "cn"; //contact name
ZaAccount.A_firstName = "givenName"; //first name
ZaAccount.A_lastName = "sn"; //last name
ZaAccount.A_mail = "mail";
ZaAccount.A_password = "password";
ZaAccount.A_description = "description";
ZaAccount.A_telephoneNumber = "telephoneNumber";
ZaAccount.A_displayname = "displayName";
ZaAccount.A_country = "co"; //country
ZaAccount.A_company = "company";
ZaAccount.A_initials = "initials"; //middle initial
ZaAccount.A_city = "l";
ZaAccount.A_orgUnit = "ou";
ZaAccount.A_office = "physicalDeliveryOfficeName";
ZaAccount.A_street = "street";
ZaAccount.A_zip = "postalCode";
ZaAccount.A_state = "st";
ZaAccount.A_mailDeliveryAddress = "zimbraMailDeliveryAddress";
ZaAccount.A_accountStatus = "zimbraAccountStatus";
ZaAccount.A_notes = "zimbraNotes";
ZaAccount.A_zimbraMailQuota = "zimbraMailQuota";
ZaAccount.A_mailHost = "zimbraMailHost";
ZaAccount.A_COSId = "zimbraCOSId";
ZaAccount.A_isAdminAccount = "zimbraIsAdminAccount";
ZaAccount.A_zimbraMinPwdLength="zimbraPasswordMinLength";
ZaAccount.A_zimbraMaxPwdLength="zimbraPasswordMaxLength";
ZaAccount.A_zimbraPasswordMinUpperCaseChars = "zimbraPasswordMinUpperCaseChars";
ZaAccount.A_zimbraPasswordMinLowerCaseChars = "zimbraPasswordMinLowerCaseChars";
ZaAccount.A_zimbraPasswordMinPunctuationChars = "zimbraPasswordMinPunctuationChars";
ZaAccount.A_zimbraPasswordMinNumericChars = "zimbraPasswordMinNumericChars";
ZaAccount.A_zimbraMinPwdAge="zimbraPasswordMinAge";
ZaAccount.A_zimbraMaxPwdAge="zimbraPasswordMaxAge";
ZaAccount.A_zimbraEnforcePwdHistory="zimbraPasswordEnforceHistory";
ZaAccount.A_zimbraMailAlias="zimbraMailAlias";
ZaAccount.A_zimbraMailForwardingAddress="zimbraMailForwardingAddress";
ZaAccount.A_zimbraPasswordMustChange="zimbraPasswordMustChange";
ZaAccount.A_zimbraPasswordLocked="zimbraPasswordLocked";
ZaAccount.A_zimbraDomainName = "zimbraDomainName";
ZaAccount.A_zimbraContactMaxNumEntries = "zimbraContactMaxNumEntries";
ZaAccount.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";
ZaAccount.A_zimbraQuotaWarnPercent = "zimbraQuotaWarnPercent";
ZaAccount.A_zimbraQuotaWarnInterval = "zimbraQuotaWarnInterval";
ZaAccount.A_zimbraQuotaWarnMessage = "zimbraQuotaWarnMessage";
ZaAccount.A_zimbraIsSystemResource = "zimbraIsSystemResource";
ZaAccount.A_zimbraExcludeFromCMBSearch = "zimbraExcludeFromCMBSearch";

ZaAccount.A_zimbraAdminAuthTokenLifetime  = "zimbraAdminAuthTokenLifetime";
ZaAccount.A_zimbraAuthTokenLifetime = "zimbraAuthTokenLifetime";
ZaAccount.A_zimbraMailMessageLifetime = "zimbraMailMessageLifetime";
ZaAccount.A_zimbraMailSpamLifetime = "zimbraMailSpamLifetime";
ZaAccount.A_zimbraMailTrashLifetime = "zimbraMailTrashLifetime";
ZaAccount.A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
ZaAccount.A_zimbraAvailableSkin = "zimbraAvailableSkin";
ZaAccount.A_zimbraZimletAvailableZimlets = "zimbraZimletAvailableZimlets";
//prefs
ZaAccount.A_zimbraPrefClientType = "zimbraPrefClientType";
ZaAccount.A_zimbraPrefTimeZoneId = "zimbraPrefTimeZoneId";
ZaAccount.A_zimbraAllowAnyFromAddress = "zimbraAllowAnyFromAddress";
ZaAccount.A_zimbraAllowFromAddress = "zimbraAllowFromAddress";
ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
ZaAccount.A_zimbraPrefCalendarUseQuickAdd = "zimbraPrefCalendarUseQuickAdd";
ZaAccount.A_prefSaveToSent="zimbraPrefSaveToSent";
ZaAccount.A_prefMailSignature="zimbraPrefMailSignature";
ZaAccount.A_prefMailSignatureEnabled="zimbraPrefMailSignatureEnabled";
ZaAccount.A_zimbraPrefSentMailFolder = "zimbraPrefSentMailFolder";
ZaAccount.A_zimbraPrefGroupMailBy = "zimbraPrefGroupMailBy";
ZaAccount.A_zimbraPrefIncludeSpamInSearch = "zimbraPrefIncludeSpamInSearch";
ZaAccount.A_zimbraPrefIncludeTrashInSearch = "zimbraPrefIncludeTrashInSearch";
ZaAccount.A_zimbraPrefMailInitialSearch = "zimbraPrefMailInitialSearch";
ZaAccount.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaAccount.A_zimbraPrefMailPollingInterval = "zimbraPrefMailPollingInterval";
ZaAccount.A_zimbraMailMinPollingInterval = "zimbraMailMinPollingInterval";
ZaAccount.A_zimbraPrefOutOfOfficeFromDate = "zimbraPrefOutOfOfficeFromDate";
ZaAccount.A_zimbraPrefOutOfOfficeUntilDate = "zimbraPrefOutOfOfficeUntilDate";
ZaAccount.A_zimbraPrefOutOfOfficeReply = "zimbraPrefOutOfOfficeReply";
ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled = "zimbraPrefOutOfOfficeReplyEnabled";
ZaAccount.A_zimbraPrefReplyToAddress = "zimbraPrefReplyToAddress";
ZaAccount.A_zimbraPrefUseKeyboardShortcuts = "zimbraPrefUseKeyboardShortcuts";
ZaAccount.A_zimbraPrefContactsPerPage = "zimbraPrefContactsPerPage";
ZaAccount.A_zimbraMemberOf = "zimbraMemberOf";
ZaAccount.A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat = "zimbraPrefForwardReplyInOriginalFormat";
ZaAccount.A_zimbraPrefAutoAddAddressEnabled = "zimbraPrefAutoAddAddressEnabled";
ZaAccount.A_zimbraPrefComposeFormat = "zimbraPrefComposeFormat";
ZaAccount.A_zimbraPrefMessageViewHtmlPreferred = "zimbraPrefMessageViewHtmlPreferred";
ZaAccount.A_zimbraPrefNewMailNotificationAddress = "zimbraPrefNewMailNotificationAddress";
ZaAccount.A_zimbraPrefNewMailNotificationEnabled = "zimbraPrefNewMailNotificationEnabled";
ZaAccount.A_zimbraPrefOutOfOfficeReply = "zimbraPrefOutOfOfficeReply";
ZaAccount.A_zimbraPrefShowSearchString = "zimbraPrefShowSearchString";
ZaAccount.A_zimbraPrefMailSignatureStyle = "zimbraPrefMailSignatureStyle";
ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar = "zimbraPrefUseTimeZoneListInCalendar";
ZaAccount.A_zimbraPrefImapSearchFoldersEnabled = "zimbraPrefImapSearchFoldersEnabled";
ZaAccount.A_zimbraPrefMailForwardingAddress = "zimbraPrefMailForwardingAddress";
ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled = "zimbraPrefMailLocalDeliveryDisabled";
ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime = "zimbraPrefCalendarApptReminderWarningTime";
ZaAccount.A_zimbraPrefSkin = "zimbraPrefSkin";
ZaAccount.A_zimbraPrefGalAutoCompleteEnabled = "zimbraPrefGalAutoCompleteEnabled";
ZaAccount.A_zimbraPrefWarnOnExit = "zimbraPrefWarnOnExit" ;
ZaAccount.A_zimbraPrefShowSelectionCheckbox = "zimbraPrefShowSelectionCheckbox" ;
ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize = "zimbraPrefHtmlEditorDefaultFontSize" ;
ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily = "zimbraPrefHtmlEditorDefaultFontFamily" ;
ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor = "zimbraPrefHtmlEditorDefaultFontColor" ;
ZaAccount.A_zimbraMailSignatureMaxLength = "zimbraMailSignatureMaxLength" ;
ZaAccount.A_zimbraPrefDisplayExternalImages = "zimbraPrefDisplayExternalImages" ;
ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration = "zimbraPrefOutOfOfficeCacheDuration";
ZaAccount.A_zimbraPrefMailDefaultCharset = "zimbraPrefMailDefaultCharset";
ZaAccount.A_zimbraJunkMessagesIndexingEnabled = "zimbraJunkMessagesIndexingEnabled" ;

//features
ZaAccount.A_zimbraFeatureMailPriorityEnabled = "zimbraFeatureMailPriorityEnabled";
ZaAccount.A_zimbraFeatureInstantNotify = "zimbraFeatureInstantNotify";
ZaAccount.A_zimbraFeaturePop3DataSourceEnabled = "zimbraFeaturePop3DataSourceEnabled";
ZaAccount.A_zimbraFeatureIdentitiesEnabled = "zimbraFeatureIdentitiesEnabled";
ZaAccount.A_zimbraFeatureMailForwardingEnabled = "zimbraFeatureMailForwardingEnabled";
ZaAccount.A_zimbraFeatureContactsEnabled="zimbraFeatureContactsEnabled";
ZaAccount.A_zimbraFeatureCalendarEnabled="zimbraFeatureCalendarEnabled";
ZaAccount.A_zimbraFeatureTasksEnabled="zimbraFeatureTasksEnabled";
ZaAccount.A_zimbraFeatureTaggingEnabled="zimbraFeatureTaggingEnabled";
ZaAccount.A_zimbraFeatureAdvancedSearchEnabled="zimbraFeatureAdvancedSearchEnabled";
ZaAccount.A_zimbraFeatureSavedSearchesEnabled="zimbraFeatureSavedSearchesEnabled";
ZaAccount.A_zimbraFeatureConversationsEnabled="zimbraFeatureConversationsEnabled";
ZaAccount.A_zimbraFeatureChangePasswordEnabled="zimbraFeatureChangePasswordEnabled";
ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled="zimbraFeatureInitialSearchPreferenceEnabled";
ZaAccount.A_zimbraFeatureFiltersEnabled="zimbraFeatureFiltersEnabled";
ZaAccount.A_zimbraFeatureGalEnabled="zimbraFeatureGalEnabled";
ZaAccount.A_zimbraFeatureSharingEnabled="zimbraFeatureSharingEnabled";
ZaAccount.A_zimbraFeatureNotebookEnabled = "zimbraFeatureNotebookEnabled";
ZaAccount.A_zimbraFeatureBriefcasesEnabled = "zimbraFeatureBriefcasesEnabled";
ZaAccount.A_zimbraFeatureHtmlComposeEnabled = "zimbraFeatureHtmlComposeEnabled";
ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled = "zimbraFeatureGalAutoCompleteEnabled";
ZaAccount.A_zimbraImapEnabled = "zimbraImapEnabled";
ZaAccount.A_zimbraPop3Enabled = "zimbraPop3Enabled";
ZaAccount.A_zimbraFeatureSkinChangeEnabled = "zimbraFeatureSkinChangeEnabled";
ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled = "zimbraFeatureOutOfOfficeReplyEnabled";
ZaAccount.A_zimbraFeatureNewMailNotificationEnabled = "zimbraFeatureNewMailNotificationEnabled";
ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled = "zimbraFeatureMailPollingIntervalPreferenceEnabled" ;
ZaAccount.A_zimbraHideInGal = "zimbraHideInGal";
ZaAccount.A_zimbraMailCanonicalAddress = "zimbraMailCanonicalAddress";
ZaAccount.A_zimbraFeatureOptionsEnabled = "zimbraFeatureOptionsEnabled";
ZaAccount.A_zimbraFeatureShortcutAliasesEnabled = "zimbraFeatureShortcutAliasesEnabled" ;
ZaAccount.A_zimbraFeatureMailEnabled = "zimbraFeatureMailEnabled" ;
ZaAccount.A_zimbraFeatureGroupCalendarEnabled = "zimbraFeatureGroupCalendarEnabled" ;
ZaAccount.A_zimbraFeatureIMEnabled = "zimbraFeatureIMEnabled" ;
ZaAccount.A_zimbraFeatureFlaggingEnabled = "zimbraFeatureFlaggingEnabled" ;
ZaAccount.A_zimbraForeignPrincipal = "zimbraForeignPrincipal" ;
//security
ZaAccount.A_zimbraPasswordLockoutEnabled = "zimbraPasswordLockoutEnabled";
ZaAccount.A_zimbraPasswordLockoutDuration = "zimbraPasswordLockoutDuration";
ZaAccount.A_zimbraPasswordLockoutMaxFailures = "zimbraPasswordLockoutMaxFailures";
ZaAccount.A_zimbraPasswordLockoutFailureLifetime = "zimbraPasswordLockoutFailureLifetime";
//readonly
ZaAccount.A_zimbraLastLogonTimestamp = "zimbraLastLogonTimestamp";
ZaAccount.A_zimbraPasswordModifiedTime = "zimbraPasswordModifiedTime";


ZaAccount.ACCOUNT_STATUS_ACTIVE = "active";
ZaAccount.ACCOUNT_STATUS_MAINTENANCE = "maintenance";
ZaAccount.ACCOUNT_STATUS_LOCKED = "locked";
ZaAccount.ACCOUNT_STATUS_LOCKOUT = "lockout";
ZaAccount.ACCOUNT_STATUS_CLOSED = "closed";

//this attributes are not used in the XML object, but is used in the model
ZaAccount.A2_confirmPassword = "confirmPassword";
ZaAccount.A2_mbxsize = "mbxSize";
ZaAccount.A2_quota = "quota2";
ZaAccount.A2_autodisplayname = "autodisplayname";
ZaAccount.A2_autoMailServer = "automailserver";
ZaAccount.A2_autoCos = "autoCos" ;
ZaAccount.A2_myCOS = "mycos";
ZaAccount.A2_newAlias = "newalias";

//ZaAccount.A2_newForward = "newforward";
ZaAccount.A2_aliases = "aliases";
ZaAccount.A2_forwarding = "forwardings";

//Group (Member Of tab needed)
ZaAccount.A2_memberOf = "memberOf" ;
//ZaAccount.A2_isgroup = "isgroup" ;
ZaAccount.A2_directMemberList = "directMemberList" ;
ZaAccount.A2_indirectMemberList = "indirectMemberList";
ZaAccount.A2_nonMemberList = "nonMemberList" ;
ZaAccount.A2_showSameDomain = "showSameDomain" ;
ZaAccount.A2_domainLeftAccounts = "leftDomainAccounts" ;

ZaAccount.MAXSEARCHRESULTS = ZaSettings.MAXSEARCHRESULTS;
ZaAccount.RESULTSPERPAGE = ZaSettings.RESULTSPERPAGE;

ZaAccount.checkValues = 
function(tmpObj, app) {
	/**
	* check values
	**/

	if(tmpObj.name == null || tmpObj.name.length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED);
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_lastName] == null || tmpObj.attrs[ZaAccount.A_lastName].length < 1) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_LAST_NAME_REQUIRED);
		return false;
	}

	/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;
	}*/
	if(tmpObj.name.lastIndexOf ("@")!=tmpObj.name.indexOf ("@")) {
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;		
	}
	var myCos = null;
	var maxPwdLen = Number.POSITIVE_INFINITY;
	var minPwdLen = 0;	
	var maxPwdAge = Number.POSITIVE_INFINITY;
	var minPwdAge = 0;		
	try {
		//find out what is this account's COS
		if(ZaSettings.COSES_ENABLED) {
			myCos = app.getCosList().getItemById(tmpObj.attrs[ZaAccount.A_COSId]);
			if(!myCos) {
				var cosList = app.getCosList();
				if(cosList.size() > 0) {
					//myCos = cosList[0];
					myCos = ZaCos.getDefaultCos4Account(tmpObj[ZaAccount.A_name], cosList, app);
					tmpObj.attrs[ZaAccount.A_COSId] = myCos.id;
				}
			}		
		}
	} catch (ex) {
		app.getCurrentController()._handleException(ex, "ZaAccount.checkValues", null, false);
	}	
	

	//validate this account's password constraints
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinLength + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxLength + " ! ");
		return false;
	}	
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]);
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]);
		
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinAge + " ! ");

		return false;
	}		
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxAge + " ! ");

		return false;
	}		
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]);
	
	if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])
		tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] = parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]);
	
	//validate password length against this account's or COS setting
	//if the account did not have a valid cos id - pick the first COS
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
		minPwdLen = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]);
	} else if(ZaSettings.COSES_ENABLED) {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMinPwdLength] > 0) {
				minPwdLen = parseInt(myCos.attrs[ZaCos.A_zimbraMinPwdLength]);
			}
		}
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
		maxPwdLen = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]);
	} else if(ZaSettings.COSES_ENABLED) {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMaxPwdLength] > 0) {
				maxPwdLen = parseInt (myCos.attrs[ZaCos.A_zimbraMaxPwdLength]);
			}		
		}
	}
	
	if(maxPwdLen < minPwdLen) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDLENGTH);
		return false;		
	}
		

	//validate password age settings
	//if the account did not have a valid cos id - pick the first COS
	if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] != null) {
		maxPwdAge = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge]);
	} else if(ZaSettings.COSES_ENABLED) {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMaxPwdAge] > 0) {
				maxPwdAge = parseInt ( myCos.attrs[ZaCos.A_zimbraMaxPwdAge]);
			}
		}
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] != null) {
		minPwdAge = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]);
	} else if(ZaSettings.COSES_ENABLED) {
		if(myCos) {
			if(myCos.attrs[ZaCos.A_zimbraMinPwdAge] > 0) {
				minPwdAge = parseInt (myCos.attrs[ZaCos.A_zimbraMinPwdAge]);
			}		
		}
	}
		

	if(maxPwdAge < minPwdAge) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDAGE);
		return false;		
	}	
	
	//if there is a password - validate it
	if(tmpObj.attrs[ZaAccount.A_password]!=null || tmpObj[ZaAccount.A2_confirmPassword]!=null) {
		if(tmpObj.attrs[ZaAccount.A_password] != tmpObj[ZaAccount.A2_confirmPassword]) {
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		} 			
		if(tmpObj.attrs[ZaAccount.A_password].length < minPwdLen || AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length < minPwdLen) { 
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen));
			return false;		
		}
		
		if(AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length > maxPwdLen) { 
			//show error msg
			app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen));
			return false;		
		}
	} 		
	if(tmpObj.attrs[ZaAccount.A_zimbraMailQuota] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailQuota] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMailQuota])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailQuota + " ! ");
		return false;
	}

	if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] != "" && tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_ContactMaxNumEntries + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])
		tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries]);
	

	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinUpperCaseChars + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars])
		tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars]);


	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinLowerCaseChars + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars])
		tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars]);


	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinPunctuationChars + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars])
		tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars]);


	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinNumericChars + " ! ");
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars])
		tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars]);
		
	if(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AuthTokenLifetime + " ! ");

		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AdminAuthTokenLifetime + " ! ");

		return false;
	}	

	if(tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration] != "" && tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration + " ! ");

		return false;
	}
	
	var p_mailPollingInterval = tmpObj.attrs[ZaAccount.A_zimbraPrefMailPollingInterval] ;
	if( p_mailPollingInterval != "" && p_mailPollingInterval !=null && !AjxUtil.isLifeTime(p_mailPollingInterval)) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPrefMailPollingInterval + " ! ");

		return false;
	}
	
	var min_mailPollingInterval = tmpObj.attrs[ZaAccount.A_zimbraMailMinPollingInterval]
	if( min_mailPollingInterval != "" && min_mailPollingInterval !=null && !AjxUtil.isLifeTime(min_mailPollingInterval)) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraMailMinPollingInterval + " ! ");

		return false;
	}
	
	if ((min_mailPollingInterval == "" || min_mailPollingInterval == null)&& (myCos)) {
		//take the cos value
		min_mailPollingInterval = myCos.attrs[ZaAccount.A_zimbraMailMinPollingInterval];
	}
	
	if ((p_mailPollingInterval == "" || p_mailPollingInterval == null) && (myCos) && myCos.attrs[ZaAccount.A_zimbraPrefMailPollingInterval] != null){
		p_mailPollingInterval = myCos.attrs[ZaAccount.A_zimbraPrefMailPollingInterval];
	}
	if(p_mailPollingInterval != null && min_mailPollingInterval != null) {
		if (myCos && (ZaUtil.getLifeTimeInSeconds(p_mailPollingInterval) < ZaUtil.getLifeTimeInSeconds(min_mailPollingInterval))){
			app.getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_mailPollingInterval) ;
			return false ;
		}
	}		
	if(tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailIdleSessionTimeout + " ! ");

		return false;
	}
		
	if(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailMessageLifetime + " ! ");

		return false;
	}			

	if(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailTrashLifetime + " ! ");

		return false;
	}	
	
	if(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] != 0 && tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailSpamLifetime + " ! ");
		
		return false;
	}		

	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime + " ! ");
		
		return false;
	}
	
	if(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] !=null && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] !=0 && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordLockoutDuration+ " ! ");
		
		return false;
	}
		
	if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] != "" && tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_PrefContactsPerPage + " ! ");

		return false;
	}	
	if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])
		tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage]);

	if(tmpObj.attrs[ZaAccount.A_passEnforceHistory] != "" && tmpObj.attrs[ZaAccount.A_passEnforceHistory] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_passEnforceHistory])) {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passEnforceHistory + " ! ");

		return false;
	}	
	if(tmpObj.attrs[ZaAccount.A_passEnforceHistory])
		tmpObj.attrs[ZaAccount.A_passEnforceHistory] = parseInt(tmpObj.attrs[ZaAccount.A_passEnforceHistory]);
		
	if((!tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddress] || tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddress].length<1) && !tmpObj.attrs[ZaAccount.A_zimbraPrefMailForwardingAddress] && tmpObj.attrs[ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled]=="TRUE") {
		//show error msg
		app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_NO_FWD_REQ_LOCALDELIV);

		return false;
		
	}
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		//check that current theme is part of selected themes
		var currentTheme = tmpObj.attrs[ZaAccount.A_zimbraPrefSkin] ? tmpObj.attrs[ZaAccount.A_zimbraPrefSkin] : ( ZaSettings.COSES_ENABLED ? tmpObj.cos.attrs[ZaCos.A_zimbraPrefSkin] : null);
		var availableThemes = tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] ? tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] : (ZaSettings.COSES_ENABLED ? tmpObj.cos.attrs[ZaCos.A_zimbraAvailableSkin] : null);	
	
		if(currentTheme && availableThemes) {
			var arr = availableThemes instanceof Array ? availableThemes : [availableThemes];
			var cnt = arr.length;
			var found=false;
			for(var i=0; i < cnt; i++) {
				if(arr[i]==currentTheme) {
					found=true;
					break;
				}
			}
			if(!found) {
				//show error msg
				app.getCurrentController().popupErrorDialog(AjxMessageFormat.format (ZaMsg.NAD_WarningCurrentThemeNotAvail, [currentTheme, currentTheme]));
				return false;			
			}
		}	
	}		

	return true;
}

/**
* Creates a new ZaAccount. This method makes SOAP request to create a new account record. 
* @param tmpObj
* @param app {ZaApp}
* @param account {ZaAccount}
**/
ZaAccount.createMethod = 
function (tmpObj, account, app) {
	tmpObj.attrs[ZaAccount.A_mail] = tmpObj.name;	
	var resp;	
	//create SOAP request
	var soapDoc = AjxSoapDoc.create("CreateAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set(ZaAccount.A_name, tmpObj.name);
	if(tmpObj.attrs[ZaAccount.A_password] && tmpObj.attrs[ZaAccount.A_password].length > 0)
		soapDoc.set(ZaAccount.A_password, tmpObj.attrs[ZaAccount.A_password]);
		
	if(tmpObj[ZaAccount.A2_autoMailServer] == "TRUE") {
		tmpObj.attrs[ZaAccount.A_mailHost] = null;
	}
	
	//check if we need to set the cosId
	if (tmpObj[ZaAccount.A2_autoCos] == "TRUE" ) {
		tmpObj.attrs[ZaAccount.A_COSId] = null ;
	}
	
	//check if zimbraAvailableSkin has been changed
/*	if(ZaSettings.SKIN_PREFS_ENABLED) {
		var skinIds = new Array();
		if((tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] instanceof AjxVector) && tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] && tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin].size()) {
			var cnt = tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin].size();
			for(var i = 0; i < cnt; i ++) {
				skinIds.push(tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin].get(i).toString());
			}
			if(cnt > 0 ) {
				tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] = skinIds;
			} else 
				tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] = "";
				
		} else
			tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] = "";
	}	*/
	for (var aname in tmpObj.attrs) {
		if(aname == ZaAccount.A_password || aname == ZaAccount.A_zimbraMailAlias || aname == ZaItem.A_objectClass || aname == ZaAccount.A2_mbxsize || aname == ZaAccount.A_mail) {
			continue;
		}	
		
		if(tmpObj.attrs[aname] instanceof Array) {
			var cnt = tmpObj.attrs[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(typeof(tmpObj.attrs[aname][ix])=="object") {
						var attr = soapDoc.set("a", tmpObj.attrs[aname][ix].toString());
						attr.setAttribute("n", aname);
					} else {
						var attr = soapDoc.set("a", tmpObj.attrs[aname][ix]);
						attr.setAttribute("n", aname);						
					}
				}
			} 
		} else if (tmpObj.attrs[aname] instanceof AjxVector) {
			var tmpArray = tmpObj.attrs[aname].getArray();
			var cnt = tmpArray.length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(tmpArray[ix] !=null) {
						if(typeof(tmpArray[ix])=="object") {
							var attr = soapDoc.set("a", tmpArray[ix].toString());
							attr.setAttribute("n", aname);
						} else {
							var attr = soapDoc.set("a", tmpArray[ix]);
							attr.setAttribute("n", aname);
						}
					}
				}
			} 			
			
		} else {	
			if(tmpObj.attrs[aname] != null) {
				if(typeof(tmpObj.attrs[aname]) == "object") {				
					var attr = soapDoc.set("a", tmpObj.attrs[aname].toString());
					attr.setAttribute("n", aname);
				} else {
					var attr = soapDoc.set("a", tmpObj.attrs[aname]);
					attr.setAttribute("n", aname);					
				}
			}
		}
	}
	try {

		//var createAccCommand = new ZmCsfeCommand();
		var csfeParams = new Object();
		csfeParams.soapDoc = soapDoc;	
		var reqMgrParams = {} ;
		reqMgrParams.controller = app.getCurrentController();
		reqMgrParams.busyMsg = ZaMsg.BUSY_CREATE_ACCOUNTS ;
		//reqMgrParams.busyMsg = "Creating Accounts ...";
		//resp = createAccCommand.invoke(params).Body.CreateAccountResponse;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CreateAccountResponse;
	} catch (ex) {
		throw ex;
		return null;
	}
	
	//account.initFromDom(resp.firstChild);
	account.initFromJS(resp.account[0]);		
	//add aliases
	if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length) {
		var tmpObjCnt = tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length;
		var failedAliases = "";
		var failedAliasesCnt = 0;
		try {
			for(var ix=0; ix < tmpObjCnt; ix++) {
				try {
					account.addAlias(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
					account.attrs[ZaAccount.A_zimbraMailAlias].push(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
				} catch (ex) {
					if(ex.code == ZmCsfeException.ACCT_EXISTS) {
						//if failed because account exists just show a warning
						failedAliases += ("<br>" + tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
						failedAliasesCnt++;
					} else {
						//if failed for another reason - jump out
						throw (ex);
					}
				}
			}
			if(failedAliasesCnt == 1) {
				app.getCurrentController().popupErrorDialog(ZaMsg.WARNING_ALIAS_EXISTS + failedAliases);
			} else if(failedAliasesCnt > 1) {
				app.getCurrentController().popupErrorDialog(ZaMsg.WARNING_ALIASES_EXIST + failedAliases);
			}
		} catch (ex) {
			app.getCurrentController().popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);
			return null;
		}	
	}	
}
ZaItem.createMethods["ZaAccount"].push(ZaAccount.createMethod);

/**
* @method modify
* Updates ZaAccount attributes (SOAP)
* @param mods set of modified attributes and their new values
*/
ZaAccount.modifyMethod =
function(mods) {
	//update the member of first
	try {
		if (ZaAccountMemberOfListView._addList.length >0) { //you have new membership to be added.
			ZaAccountMemberOfListView.addNewGroupsBySoap(this, ZaAccountMemberOfListView._addList);
		}	
		ZaAccountMemberOfListView._addList = []; //reset
	}catch (ex){
		ZaAccountMemberOfListView._addList = []; //reset
		this._app.getCurrentController()._handleException(ex, "ZaAccount.modifyMethod: add group failed", null, false);	//try not to halt the account modification	
	}
	
	try {
		if (ZaAccountMemberOfListView._removeList.length >0){//you have membership to be removed
			ZaAccountMemberOfListView.removeGroupsBySoap(this, ZaAccountMemberOfListView._removeList);
		}
		ZaAccountMemberOfListView._removeList = []; //reset
	}catch (ex){
		ZaAccountMemberOfListView._removeList = []; //reset
		this._app.getCurrentController()._handleException(ex, "ZaAccount.modifyMethod: remove group failed", null, false);		
	}
	
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					var attr = null;
					if(mods[aname][ix] instanceof String)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix] instanceof Object)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix])
						var attr = soapDoc.set("a", mods[aname][ix]);
	
					if(attr)
						attr.setAttribute("n", aname);
				}
			} else {
				var attr = soapDoc.set("a", "");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	//var modifyAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller: this._app.getCurrentController(),
		busyMsg: ZaMsg.BUSY_MODIFY_ACCOUNT 
	} ;
	
	//resp = modifyAccCommand.invoke(params).Body.ModifyAccountResponse;
	resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyAccountResponse ;
	
	this.initFromJS(resp.account[0]);
	this[ZaAccount.A2_confirmPassword] = null;
	//invalidate the original tooltip
	this._toolTip = null ;
	return;
}
ZaItem.modifyMethods["ZaAccount"].push(ZaAccount.modifyMethod);



ZaAccount.getViewMailLink = 
function(accId, app) {
	var retVal={authToken:"", lifetime:0};
	var soapDoc = AjxSoapDoc.create("DelegateAuthRequest", ZaZimbraAdmin.URN, null);	
	var attr = soapDoc.set("account", accId);
	attr.setAttribute("by", "id");
	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	//var resp = command.invoke(params).Body.DelegateAuthResponse;
	var reqMgrParams = {
		controller: app.getCurrentController ()
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.DelegateAuthResponse ; 
	retVal.authToken = resp.authToken;
	retVal.lifetime = resp.lifetime;
	
	return retVal;
}

ZaReindexMailbox = function() {
	this.status = null;
	this.numSucceeded = 0;
	this.numFailed = 0;	
	this.numRemaining = 0;	
	this.numTotal = 100;	
	this.numDone = 0;	
	this.progressMsg = ZaMsg.NAD_ACC_ReindexingNotRunning;
	this.mbxId = null;
	this.resultMsg = null;
	this.errorDetail = null;
	this.pollInterval = 500;	
}

ZaReindexMailbox.myXModel = {
	items: [
		{id:"status", ref:"status", type:_STRING_},						
		{id:"numSucceeded", ref:"numSucceeded", type:_NUMBER_},								
		{id:"numFailed", ref:"numFailed", type:_NUMBER_},										
		{id:"numRemaining", ref:"numRemaining", type:_NUMBER_},												
		{id:"mbxId", ref:"mbxId", type:_STRING_},														
		{id:"numTotal", ref:"numTotal", type:_NUMBER_},			
		{id:"numDone", ref:"numDone", type:_NUMBER_},					
		{id:"numDone", ref:"numDone", type:_NUMBER_},							
		{id:"pollInterval", ref:"pollInterval", type:_STRING_},
		{id:"progressMsg", ref:"progressMsg", type:_STRING_}
	]
};

ZaAccount.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	this.deleteCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	this.deleteCommand.invoke(params);		
}

ZaAccount.getReindexStatus = 
function (mbxId, callback) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("action", "status");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	var resp = null;
	try {
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		if(callback) {
			params.asyncMode = true;
			params.callback = callback;
			command.invoke(params);	
		} else {
			resp = command.invoke(params);	
		}
		
	} catch (ex) {
		if(ex.code == "service.NOT_IN_PROGRESS") {
			resp = null;
		} else {
			throw (ex);
		}
	}
	return resp;
}

ZaAccount.startReindexMailbox = 
function (mbxId, callback) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("action", "start");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	
	var resp;
	try {
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		if(callback) {
			params.asyncMode = true;
			params.callback = callback;
			command.invoke(params);	
		} else {
			resp = command.invoke(params);	
		}

	} catch (ex) {
		resp = ex;
	}
	return resp;
}

ZaAccount.abortReindexMailbox = 
function (mbxId, callback) {
	var soapDoc = AjxSoapDoc.create("ReIndexRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("action", "cancel");
	var attr = soapDoc.set("mbox", null);
	attr.setAttribute("id", mbxId);
	var resp;
	try {
		var command = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		if(callback) {
			params.asyncMode = true;
			params.callback = callback;
			command.invoke(params);	
		} else {
			resp = command.invoke(params);	
		}	
	} catch (ex) {
		resp = ex;
	}
	return resp;
}

ZaAccount.parseReindexResponse =
function (respObj, instance) {
	if(!respObj)
		return;
	if(respObj.isException && respObj.isException()) {
		var errCode = respObj.getException().code;
		if(errCode && errCode == "service.NOT_IN_PROGRESS") {
			instance.errorDetail = "";
			instance.resultMsg = "";	
			instance.progressMsg = ZaMsg.NAD_ACC_ReindexingNotRunning;
			if(instance.numRemaining > 0 || instance.status == "started") {
				instance.numDone = instance.numTotal;
				instance.status = "complete";	
				instance.progressMsg = ZaMsg.NAD_ACC_ReindexingComplete;			
			} else {
				instance.status = null;
			}
		} else {
			instance.resultMsg = String(ZaMsg.FAILED_REINDEX).replace("{0}", errCode);
			instance.errorDetail = respObj.getException().detail+"\n"+respObj.getException().msg;
			instance.status = "error";	
		
		}
	} else  {
		var resp;
		if(respObj.getResponse) {
			resp = respObj.getResponse();
		} else if(respObj.Body.ReIndexResponse) {
			resp = respObj;
		}
		if(resp && resp.Body.ReIndexResponse) {
			instance.status = resp.Body.ReIndexResponse.status;
			if(instance.status == "started") {
				instance.numDone = 0;
				instance.progressMsg = ZaMsg.NAD_ACC_ReindexingStarted;							
			}
			if(resp.Body.ReIndexResponse.progress && resp.Body.ReIndexResponse.progress[0]) {
				var progress = resp.Body.ReIndexResponse.progress[0];
				instance.numFailed = progress.numFailed;
				instance.numSucceeded = progress.numSucceeded;				
				instance.numRemaining = progress.numRemaining;	
				instance.numTotal = instance.numSucceeded + instance.numFailed + instance.numRemaining;
				instance.numDone  = instance.numFailed + instance.numSucceeded;					
				instance.progressMsg = String(ZaMsg.NAD_ACC_ReindexingStatus).replace("{0}", instance.numSucceeded).replace("{1}",instance.numRemaining).replace("{2}", instance.numFailed);				
				if(instance.status == "cancelled") {
					instance.progressMsg = instance.progressMsg + "<br>" + ZaMsg.NAD_ACC_ReindexingCancelled;
				}			
				if(instance.numRemaining == 0) {
					instance.numDone = instance.numTotal;
				}					
			}
		}
	}
}
/*
ZaAccount.parseReindexResponse = 
function (arg, respObj) {
//	var numFailed, numSucceeded,numRemaining,numTotal,numDone,resultMsg,errorDetail,status;
	if (!respObj)
		respObj = new ZaReindexMailbox();

	if(!arg) {
		respObj.status = null;
		return;
	}
		
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		if(arg.code && arg.code == "service.NOT_IN_PROGRESS") {
			respObj.status = null;
			respObj.errorDetail = "";
			respObj.resultMsg = "";	
			respObj.progressMsg = ZaMsg.NAD_ACC_ReindexingNotRunning;
			if(respObj.numRemaining > 0) {
				respObj.numDone = respObj.numTotal;
			}
		} else {
			respObj.resultMsg = String(ZaMsg.FAILED_REINDEX).replace("{0}", arg.code);
			respObj.errorDetail = arg.detail;
			respObj.status = "error";	
		}
	} else {
		var node;
		if(arg instanceof AjxSoapDoc) {
			node = arg.getBody().firstChild;
		} else {
			node = arg.firstChild;
		}
		var status = node.getAttribute("status");
		respObj.status = status;
		if(status == "cancelled") {
			respObj.progressMsg = ZaMsg.NAD_ACC_ReindexingCancelled;
		}		
		if(node.firstChild) {
			respObj.numFailed = parseInt(node.firstChild.getAttribute("numFailed"));
			respObj.numSucceeded = parseInt(node.firstChild.getAttribute("numSucceeded"));
			respObj.numRemaining = parseInt(node.firstChild.getAttribute("numRemaining"));
			respObj.numTotal = respObj.numRemaining + respObj.numFailed + respObj.numSucceeded;
			respObj.numDone  = respObj.numFailed + respObj.numSucceeded;	
			respObj.progressMsg = String(ZaMsg.NAD_ACC_ReindexingStatus).replace("{0}", respObj.numSucceeded).replace("{1}",respObj.numRemaining).replace("{2}", respObj.numFailed);
			if(status == "cancelled") {
				respObj.progressMsg = respObj.progressMsg + "<br>" + ZaMsg.NAD_ACC_ReindexingCancelled;
			}			
			if(respObj.numRemaining == 0) {
				respObj.numDone = respObj.numTotal;
			}
			//temp fix 
		}
	}
}*/

ZaAccount.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.attrs[ZaAccount.A_zimbraMailForwardingAddress] = new Array();
	this.attrs[ZaAccount.A_zimbraAllowFromAddress] = new Array();
    this.attrs[ZaAccount.A_zimbraForeignPrincipal ] = [];
    var children = node.childNodes;
	for (var i=0; i< children.length;  i++) {
		child = children[i];
		if (child.nodeName != 'a') continue;
		var name = child.getAttribute("n");
		if (child.firstChild != null) {
			var value = child.firstChild.nodeValue;
			if (name in this.attrs) {
				var vc = this.attrs[name];
				if ((typeof vc) == "object") {
					vc.push(value);
				} else {
					this.attrs[name] = [vc, value];
				}
			} else {
				this.attrs[name] = value;
			}
		}
	}
}

ZaAccount.prototype.initFromJS = 
function (account) {
	if(!account)
		return;
		
	this.attrs = new Object();	
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.name = account.name;
	this.id = account.id;
	var len = account.a.length;
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.attrs[ZaAccount.A_zimbraMailForwardingAddress] = new Array();	
	this.attrs[ZaAccount.A_zimbraAllowFromAddress] = new Array();
    this.attrs[ZaAccount.A_zimbraForeignPrincipal ] = [];
    for(var ix = 0; ix < len; ix++) {
		if(!this.attrs[[account.a[ix].n]]) {
			this.attrs[[account.a[ix].n]] = account.a[ix]._content;
		} else {
			if(!(this.attrs[[account.a[ix].n]] instanceof Array)) {
				this.attrs[[account.a[ix].n]] = [this.attrs[[account.a[ix].n]]];
			} 
			this.attrs[[account.a[ix].n]].push(account.a[ix]._content);
		}
	}
	
}

/**
* Returns HTML for a tool tip for this account.
*/
ZaAccount.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350' >";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("Account");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addRow(ZaMsg.NAD_ZimbraID, this.id, html, idx);
		
		if (ZaSettings.COSES_ENABLED) {
			var cos = this.getCurrentCos();
			if(cos) {
				idx = this._addRow(ZaMsg.NAD_ClassOfService, cos.name, html, idx);
			}
		}
		//idx = this._addRow(ZaMsg.NAD_DisplayName+":", this.attrs[ZaAccount.A_displayname], html, idx);
		
		if(ZaSettings.SERVERS_ENABLED) {
			idx = this._addRow(ZaMsg.NAD_MailServer, this.attrs[ZaAccount.A_mailHost], html, idx);
		}
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}



ZaAccount.loadMethod = 
function(by, val, withCos) {
	var soapDoc = AjxSoapDoc.create("GetAccountRequest", ZaZimbraAdmin.URN, null);
	if(withCos) {
		soapDoc.getMethod().setAttribute("applyCos", "1");	
	} else {
		soapDoc.getMethod().setAttribute("applyCos", "0");		
	}
	var elBy = soapDoc.set("account", val);
	elBy.setAttribute("by", by);

	//var getAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller: this._app.getCurrentController()
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAccountResponse;
	this.attrs = new Object();
	this.initFromJS(resp.account[0]);

	soapDoc = AjxSoapDoc.create("GetMailboxRequest", ZaZimbraAdmin.URN, null);
	var mbox = soapDoc.set("mbox", "");
	mbox.setAttribute("id", this.attrs[ZaItem.A_zimbraId]);
	try {
		//var getMbxCommand = new ZmCsfeCommand();
		params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams ={
			controller: this._app.getCurrentController()
		}
		resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetMailboxResponse;
		
		if(resp && resp.mbox && resp.mbox[0]) {
			this.attrs[ZaAccount.A2_mbxsize] = resp.mbox[0].s;
		}
	} catch (ex) {
		//show the error and go on
		//we should not stop the Account from loading if some of the information cannot be accessed
		this._app.getCurrentController()._handleException(ex, "ZaAccount.prototype.load", null, false);
	}
				
	this[ZaAccount.A2_confirmPassword] = null;
	
	var autoDispName;
	if(this.attrs[ZaAccount.A_firstName])
		autoDispName = this.attrs[ZaAccount.A_firstName];
	else
		autoDispName = "";
		
	if(this.attrs[ZaAccount.A_initials]) {
		autoDispName += " ";
		autoDispName += this.attrs[ZaAccount.A_initials];
		autoDispName += ".";
	}
	if(this.attrs[ZaAccount.A_lastName]) {
		if(autoDispName.length > 0)
			autoDispName += " ";
			
	    autoDispName += this.attrs[ZaAccount.A_lastName];
	} 	
	
	if( autoDispName == this.attrs[ZaAccount.A_displayname]) {
		this[ZaAccount.A2_autodisplayname] = "TRUE";
	} else {
		this[ZaAccount.A2_autodisplayname] = "FALSE";
	}
	
	//Make a GetAccountMembershipRequest
	this[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.getAccountMemberShip(this._app, val, by ) ;
	this[ZaAccount.A2_directMemberList + "_more"] = 
			(this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
	this[ZaAccount.A2_indirectMemberList + "_more"] = 
			(this[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
}

ZaItem.loadMethods["ZaAccount"].push(ZaAccount.loadMethod);

ZaAccount.prototype.refresh = 
function(withCos) {
	this.load("id", this.id, withCos);
	
}

/**
* public rename; 
**/
ZaAccount.prototype.rename = 
function (newName) {
	//Instrumentation code start
	if(ZaAccount.renameMethods) {
		var methods = ZaAccount.renameMethods;
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this, newName);
			}
		}
	}	
	//Instrumentation code end
}

/**
* public renameMethod; sends RenameAccountRequest soap request
**/
ZaAccount.renameMethod = 
function (newName) {
	var soapDoc = AjxSoapDoc.create("RenameAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
}
ZaAccount.renameMethods.push(ZaAccount.renameMethod);

/**
* private changePasswordMethod; sends SetPasswordRequest soap request
* @param newPassword
**/
ZaAccount.changePasswordMethod = 
function (newPassword) {
	var soapDoc = AjxSoapDoc.create("SetPasswordRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	soapDoc.set("newPassword", newPassword);	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);	
}
/**
* private _changePassword;
* @param newPassword
**/
ZaAccount.prototype.changePassword = 
function (newPassword) {
	//Instrumentation code start
	if(ZaAccount.changePasswordMethods) {
		var methods = ZaAccount.changePasswordMethods;
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this, newPassword);
			}
		}
	}	
	//Instrumentation code end
}
ZaAccount.changePasswordMethods.push(ZaAccount.changePasswordMethod);

/**
* ZaAccount.myXModel - XModel for XForms
**/
ZaAccount.myXModel = { 
	items: [
		{id:ZaAccount.A2_domainLeftAccounts, ref:ZaAccount.A2_domainLeftAccounts, type:_STRING_},
		{id:ZaAccount.A_name, type:_STRING_, ref:"name", required:true},
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaAccount.A_uid, type:_STRING_, ref:"attrs/"+ZaAccount.A_uid},
		{id:ZaAccount.A_accountName, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountName},
		{id:ZaAccount.A_firstName, type:_STRING_, ref:"attrs/"+ZaAccount.A_firstName},
		{id:ZaAccount.A_lastName, type:_STRING_, ref:"attrs/"+ZaAccount.A_lastName, required:true},
		{id:ZaAccount.A_mail, type:_STRING_, ref:"attrs/"+ZaAccount.A_mail},
		{id:ZaAccount.A_password, type:_STRING_, ref:"attrs/"+ZaAccount.A_password},
		{id:ZaAccount.A2_confirmPassword, type:_STRING_},
		{id:ZaAccount.A_description, type:_STRING_, ref:"attrs/"+ZaAccount.A_description},
		{id:ZaAccount.A_telephoneNumber, type:_STRING_, ref:"attrs/"+ZaAccount.A_telephoneNumber},
		{id:ZaAccount.A_displayname, type:_STRING_, ref:"attrs/"+ZaAccount.A_displayname},
		{id:ZaAccount.A_country, type:_STRING_, ref:"attrs/"+ZaAccount.A_country},
		{id:ZaAccount.A_company, type:_STRING_, ref:"attrs/"+ZaAccount.A_company},
		{id:ZaAccount.A_initials, type:_STRING_, ref:"attrs/"+ZaAccount.A_initials},
		{id:ZaAccount.A_city, type:_STRING_, ref:"attrs/"+ZaAccount.A_city},
		{id:ZaAccount.A_orgUnit, type:_STRING_, ref:"attrs/"+ZaAccount.A_orgUnit},
		{id:ZaAccount.A_office, type:_STRING_, ref:"attrs/"+ZaAccount.A_office},
		{id:ZaAccount.A_street, type:_STRING_, ref:"attrs/"+ZaAccount.A_street},
		{id:ZaAccount.A_zip, type:_STRING_, ref:"attrs/"+ZaAccount.A_zip},
		{id:ZaAccount.A_state, type:_STRING_, ref:"attrs/"+ZaAccount.A_state},
		{id:ZaAccount.A_mailDeliveryAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaAccount.A_mailDeliveryAddress},
		{id:ZaAccount.A_zimbraMailCanonicalAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaAccount.A_zimbraMailCanonicalAddress},		
		{id:ZaAccount.A_accountStatus, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountStatus},
		{id:ZaAccount.A_notes, type:_STRING_, ref:"attrs/"+ZaAccount.A_notes},
		{id:ZaAccount.A_zimbraMailQuota, type:_COS_MAILQUOTA_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota},
		{id:ZaAccount.A_mailHost, type:_STRING_, ref:"attrs/"+ZaAccount.A_mailHost},
		{id:ZaAccount.A_COSId, type:_STRING_, ref:"attrs/" + ZaAccount.A_COSId},
		{id:ZaAccount.A_isAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_isAdminAccount},
		{id:ZaAccount.A_zimbraLastLogonTimestamp, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraLastLogonTimestamp},
		{id:ZaAccount.A_zimbraMaxPwdLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMaxPwdLength, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraMinPwdLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMinPwdLength, maxInclusive:2147483647, minInclusive:0},

		{id:ZaAccount.A_zimbraPasswordMinUpperCaseChars, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordMinUpperCaseChars, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraPasswordMinLowerCaseChars, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordMinLowerCaseChars, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraPasswordMinPunctuationChars, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordMinPunctuationChars, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraPasswordMinNumericChars, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordMinNumericChars, maxInclusive:2147483647, minInclusive:0},

		{id:ZaAccount.A_zimbraMinPwdAge, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMinPwdAge, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraMaxPwdAge, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMaxPwdAge, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraEnforcePwdHistory, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraEnforcePwdHistory, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraMailAlias, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailAlias, listItem:{type:_EMAIL_ADDRESS_}},
        {id:ZaAccount.A_zimbraForeignPrincipal, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraForeignPrincipal, listItem:{type:_STRING_}},
		{id:ZaAccount.A_zimbraMailForwardingAddress, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraMailForwardingAddress, listItem:{type:_EMAIL_ADDRESS_}},
		{id:ZaAccount.A_zimbraPasswordMustChange, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPasswordMustChange}, 
		{id:ZaAccount.A_zimbraPasswordLocked, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLocked, choices:ZaModel.BOOLEAN_CHOICES}, 
		{id:ZaAccount.A_zimbraDomainName, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraDomainName},
		{id:ZaAccount.A_zimbraContactMaxNumEntries, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraContactMaxNumEntries, maxInclusive:2147483647, minInclusive:0},
		{id:ZaAccount.A_zimbraAttachmentsBlocked, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsBlocked, choices:ZaModel.BOOLEAN_CHOICES}, 

		{id:ZaAccount.A_zimbraQuotaWarnPercent, type:_COS_NUMBER_, ref:"attrs/" + ZaAccount.A_zimbraQuotaWarnPercent},
		{id:ZaAccount.A_zimbraQuotaWarnInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraQuotaWarnInterval},
		{id:ZaAccount.A_zimbraQuotaWarnMessage, type:_COS_STRING_, ref:"attrs/" + ZaAccount.A_zimbraQuotaWarnMessage},
		
		{id:ZaAccount.A_zimbraAuthTokenLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraAuthTokenLifetime},
		{id:ZaAccount.A_zimbraAdminAuthTokenLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraAdminAuthTokenLifetime},		
		{id:ZaAccount.A_zimbraMailIdleSessionTimeout, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraMailIdleSessionTimeout},
		{id:ZaAccount.A_zimbraMailMessageLifetime, type:_COS_MLIFETIME_, ref:"attrs/" + ZaAccount.A_zimbraMailMessageLifetime},
		{id:ZaAccount.A_zimbraMailSpamLifetime, type:_COS_MLIFETIME_, ref:"attrs/" + ZaAccount.A_zimbraMailSpamLifetime},
		{id:ZaAccount.A_zimbraMailTrashLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraMailTrashLifetime},
		{id:ZaAccount.A_prefSaveToSent, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_prefSaveToSent, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_prefMailSignature, type:_STRING_, ref:"attrs/"+ZaAccount.A_prefMailSignature},
		{id:ZaAccount.A_prefMailSignatureEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_prefMailSignatureEnabled},
		//preferences
		{id:ZaAccount.A_zimbraPrefClientType, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefClientType, choices:ZaSettings.clientTypeChoices},
		{id:ZaAccount.A_zimbraPrefTimeZoneId,type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefTimeZoneId, choices:ZaSettings.timeZoneChoices},
		{id:ZaAccount.A_zimbraPrefMailDefaultCharset,type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailDefaultCharset, choices:ZaSettings.mailCharsetChoices},
		{id:ZaAccount.A_zimbraPrefSentMailFolder, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefSentMailFolder},
		{id:ZaAccount.A_zimbraPrefIncludeSpamInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeSpamInSearch, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefIncludeTrashInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeTrashInSearch, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefMailInitialSearch, type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailInitialSearch},
		{id:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailItemsPerPage, choices:[10,25,50,100]},
		{id:ZaAccount.A_zimbraPrefMailPollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailPollingInterval},
		{id:ZaAccount.A_zimbraMailMinPollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraMailMinPollingInterval},
		{id:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReply},
		{id:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled},
		{id:ZaAccount.A_zimbraPrefReplyToAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefReplyToAddress},
		{id:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefUseKeyboardShortcuts, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraAllowAnyFromAddress, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAllowAnyFromAddress, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraAllowFromAddress,type: _LIST_, ref:"attrs/"+ZaAccount.A_zimbraAllowFromAddress, listItem:{type:_STRING_}},
		{id:ZaAccount.A_zimbraPrefContactsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefContactsPerPage, choices:[10,25,50,100]},
		{id:ZaAccount.A_zimbraPrefComposeInNewWindow, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefComposeInNewWindow, choices:ZaModel.BOOLEAN_CHOICES},				
		{id:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, choices:ZaModel.BOOLEAN_CHOICES},						
		{id:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefAutoAddAddressEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefComposeFormat, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefComposeFormat, choices:ZaModel.COMPOSE_FORMAT_CHOICES},
		{id:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, choices:ZaModel.FONT_FAMILY_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, type:_COS_ENUM_},		
		{id:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, choices:ZaModel.FONT_SIZE_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, type:_COS_ENUM_},		
		{id:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, ref:"attrs/"+ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, type:_COS_STRING_},
		{id:ZaAccount.A_zimbraMailSignatureMaxLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMailSignatureMaxLength},							
		{id:ZaAccount.A_zimbraPrefGroupMailBy, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefGroupMailBy, choices:ZaModel.GROUP_MAIL_BY_CHOICES},					
		{id:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefNewMailNotificationAddress},
		{id:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailForwardingAddress},		
		{id:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefNewMailNotificationEnabled},
		{id:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReply},
		{id:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled},		
		{id:ZaAccount.A_zimbraPrefShowSearchString, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefShowSearchString, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSignatureStyle, choices:ZaModel.SIGNATURE_STYLE_CHOICES},
		{id:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarUseQuickAdd, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPrefSkin, type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefSkin},
		{id:ZaAccount.A_zimbraAvailableSkin, type:_COS_LIST_, ref:"attrs/" + ZaAccount.A_zimbraAvailableSkin, dataType: _STRING_},
		{id:ZaAccount.A_zimbraZimletAvailableZimlets, type:_COS_LIST_, ref:"attrs/" + ZaAccount.A_zimbraZimletAvailableZimlets, dataType: _STRING_},		
		{id:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefGalAutoCompleteEnabled},
		{id:ZaAccount.A_zimbraPrefWarnOnExit, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefWarnOnExit},
		{id:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefShowSelectionCheckbox},
		{id:ZaAccount.A_zimbraPrefDisplayExternalImages, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefDisplayExternalImages, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration},
		{id:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraJunkMessagesIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		
		//features
		{id:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailPriorityEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureIdentitiesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureIdentitiesEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureContactsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureContactsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureCalendarEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureTasksEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureTasksEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureTaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureSavedSearchesEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureConversationsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureChangePasswordEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureFiltersEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureGalEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureGalEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureMailEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureNotebookEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraFeatureBriefcasesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureBriefcasesEnabled, choices:ZaModel.BOOLEAN_CHOICES},				
		{id:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureMailForwardingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailForwardingEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraFeatureSharingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureSharingEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureOptionsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureOptionsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureSkinChangeEnabled, choices:ZaModel.BOOLEAN_CHOICES},						
		{id:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime, choices:ZaModel.REMINDER_CHOICES},
		{id:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, choices:ZaModel.BOOLEAN_CHOICES},				
		{id:ZaAccount.A_zimbraFeatureGroupCalendarEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureGroupCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES},				
		{id:ZaAccount.A_zimbraFeatureIMEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureIMEnabled, choices:ZaModel.BOOLEAN_CHOICES},				
		{id:ZaAccount.A_zimbraFeatureInstantNotify, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureInstantNotify, choices:ZaModel.BOOLEAN_CHOICES},						
		{id:ZaAccount.A_zimbraFeatureFlaggingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureFlaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES},						
		{id:ZaAccount.A_zimbraImapEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraPop3Enabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPop3Enabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaModel.currentStep, type:_NUMBER_, ref:ZaModel.currentStep},
		{id:ZaAccount.A2_newAlias, type:_STRING_},
		{id:ZaAccount.A2_aliases, type:_LIST_,listItem:{type:_STRING_}},
		{id:ZaAccount.A2_forwarding, type:_LIST_,listItem:{type:_STRING_}},
		{id:ZaAccount.A2_mbxsize, type:_NUMBER_, ref:"attrs/"+ZaAccount.A2_mbxsize},
		{id:ZaAccount.A2_quota, type:_MAILQUOTA_2_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota},
		{id:ZaAccount.A2_autodisplayname, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A2_autoMailServer, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A2_autoCos, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraHideInGal, type:_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraHideInGal, choices:ZaModel.BOOLEAN_CHOICES},

		//security
		{id:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaAccount.A_zimbraPasswordLockoutDuration, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutDuration},		
		{id:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutMaxFailures, maxInclusive:2147483647, minInclusive:0},		
		{id:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutFailureLifetime}				
		/* , Put these model items into the ZaAccounteMemberOfListView
		{id:ZaAccount.A2_isgroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_isgroup},
		{id:ZaAccount.A2_directMemberList, type: _DWT_LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_directMemberList},
		{id:ZaAccount.A2_indirectMemberList, type: _DWT_LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_indirectMemberList},
		{id:ZaAccount.A2_nonMemberList, type: _DWT_LIST_, ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_nonMemberList},
		{id:ZaAccount.A2_directMemberList + "_offset", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_nonMemberList + "_offset", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_directMemberList + "_more", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_nonMemberList + "_more", type:_NUMBER_, defaultValue: 0},
		{id:ZaAccount.A2_showSameDomain, type: _ENUM_, choices:ZaModel.BOOLEAN_CHOICES, 
			ref:ZaAccount.A2_memberOf + "/" + ZaAccount.A2_showSameDomain, defaultValue: "FALSE" },
		{id:"query", type:_STRING_} */
	]
};



ZaItem._ATTR[ZaAccount.A_displayname] = ZaMsg.attrDesc_accountName;
ZaItem._ATTR[ZaAccount.A_description] = ZaMsg.attrDesc_description;
ZaItem._ATTR[ZaAccount.A_firstName] = ZaMsg.attrDesc_firstName;
ZaItem._ATTR[ZaAccount.A_lastName] = ZaMsg.attrDesc_lastName;
ZaItem._ATTR[ZaAccount.A_accountStatus] = ZaMsg.attrDesc_accountStatus;
ZaItem._ATTR[ZaAccount.A_mailHost] =  ZaMsg.attrDesc_mailHost;
ZaItem._ATTR[ZaAccount.A_zimbraMailQuota] = ZaMsg.attrDesc_zimbraMailQuota;
ZaItem._ATTR[ZaAccount.A_notes] = ZaMsg.attrDesc_notes;

ZaAccount._accountStatus = 
function(val) {
	var desc = ZaAccount._ACCOUNT_STATUS[val];
	return (desc == null) ? val : desc;
}

/* Translation of Account status values into screen names */
ZaAccount._ACCOUNT_STATUS = new Object ();
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE] = ZaMsg.accountStatus_active;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED] = ZaMsg.accountStatus_closed;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED] = ZaMsg.accountStatus_locked;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKOUT] = ZaMsg.accountStatus_lockout;
ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE] = ZaMsg.accountStatus_maintenance;

ZaAccount.initMethod = function (app) {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
    this.attrs[ZaAccount.A_zimbraForeignPrincipal] = new Array (); ;
}
ZaItem.initMethods["ZaAccount"].push(ZaAccount.initMethod);

ZaAccount.getDomain =
function (accountName) {
	if (!accountName) return null;
	return accountName.substring(accountName.lastIndexOf ("@") + 1 ) ;	
}

ZaAccount.setDomainChanged =
function (value, event, form){
	//form.parent.setDirty(true);
	try {
		var instance = form.getInstance();
		var p = form.parent ;
		var newDomainName = ZaAccount.getDomain(value) ;
		if ((ZaSettings.COSES_ENABLED) && (! form.parent._isCosChanged) 
			&& ((newDomainName != ZaAccount.getDomain(instance [ZaAccount.A_name] ))
				//set the right default cos at the account creation time
				|| instance [ZaAccount.A_name].indexOf("@") == 0)) 
		{ //see if the cos needs to be updated accordingly
			var cosList = form.getController().getCosList();
			instance.cos = ZaCos.getDefaultCos4Account.call(p, value, cosList, form.parent._app );
			instance.attrs[ZaAccount.A_COSId] = instance.cos.id ;
			
			
		}else if (!ZaSettings.COSES_ENABLED ){
			if ((!p._domains) || (!p._domains[newDomainName])){
				//send the GetDomainRequest
				var soapDoc = AjxSoapDoc.create("GetDomainRequest", ZaZimbraAdmin.URN, null);	
				var domainEl = soapDoc.set("domain", newDomainName);
				domainEl.setAttribute ("by", "name");
				//var getDomainCommand = new ZmCsfeCommand();
				var params = new Object();
				params.soapDoc = soapDoc;	
				var reqMgrParams = {
					controller: form.parent._app.getCurrentController()
				}
				var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetDomainResponse;
				
				var domain = new ZaItem ();
				domain.initFromJS (resp.domain[0]);
				
				//keep the domain instance, so the future call is not needed.
				//it is used in new account and edit account
				if (p._domains) {
					p._domains[newDomainName] = domain ;
				}
			}
		}
		
		if (ZaDomain.A_domainMaxAccounts && p._domains && p._domains[newDomainName]){ 
			var maxDomainAccounts = p._domains[newDomainName].attrs[ZaDomain.A_domainMaxAccounts] ;
			if (maxDomainAccounts && maxDomainAccounts > 0) {
				
				var usedAccounts = ZaSearch.getUsedDomainAccounts(newDomainName, form.parent._app.getCurrentController() );
				instance[ZaAccount.A2_domainLeftAccounts] = 
					AjxMessageFormat.format (ZaMsg.NAD_DomainAccountLimits, [maxDomainAccounts - usedAccounts, newDomainName]) ;
			}else{
				instance[ZaAccount.A2_domainLeftAccounts] = null ;
			}
		}
	
		if(form.parent.setDirty)
			form.parent.setDirty(true);	
			
		this.setInstanceValue(value);
		form.refresh();
	} catch (ex) {
		form.parent._app.getCurrentController()._handleException(ex, "ZaAccount.setDomainChanged", null, false);	
	}
}

ZaAccount.generateDisplayName =
function (instance, firstName, lastName, initials) {
	var oldDisplayName = instance.attrs[ZaAccount.A_displayname];
	
	if(firstName)
		instance.attrs[ZaAccount.A_displayname] = firstName;
	else
		instance.attrs[ZaAccount.A_displayname] = "";
		
	if(initials) {
		instance.attrs[ZaAccount.A_displayname] += " ";
		instance.attrs[ZaAccount.A_displayname] += initials;
		instance.attrs[ZaAccount.A_displayname] += ".";
	}
	if(lastName) {
		if(instance.attrs[ZaAccount.A_displayname].length > 0)
			instance.attrs[ZaAccount.A_displayname] += " ";
			
	    instance.attrs[ZaAccount.A_displayname] += lastName;
	} 
	if(instance.attrs[ZaAccount.A_displayname] == oldDisplayName) {
		return false;
	} else {
		return true;
	}
}

ZaAccount.setDefaultCos =
function (instance, cosList, app) {
	if (!cosList) {
	   	throw (new AjxException ("No cos is available.")) ;
	}
	var defaultCos = ZaCos.getDefaultCos4Account(instance[ZaAccount.A_name], cosList, app)
			
	if(defaultCos.id) {
		instance.cos = defaultCos;
		instance.attrs[ZaAccount.A_COSId] = defaultCos.id;	
	}
}

ZaAccount.prototype.getCurrentCos =
function (){
	try {
		var cosId = this.attrs[ZaAccount.A_COSId] ;
		var currentCos ;
		var cosList = this._app.getCosList();
		if (cosId) {
			currentCos = cosList.getItemById(cosId);
		}
		
		if (!currentCos){
			currentCos = ZaCos.getDefaultCos4Account( this.name, cosList, this._app );
		}
		return currentCos ;
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaAccount.prototype.getCurrentCos", null, false);
	}	
}

//the serverStr is in format yyyyMMddHHmmssZ to be converted to MM/dd/yyyy HH:mm:ss 
ZaAccount.getLastLoginTime =
function (serverStr) {
	if (serverStr) {
		/*
		return serverStr.substring (4, 6) + "/" + serverStr.substring(6, 8) + "/" + serverStr.substring(0, 4) +
				" " + serverStr.substring (8, 10) + ":" + serverStr.substring (10, 12) + ":"
				+ serverStr.substring (12, 14) ;*/
		var ajxTKServerStr = serverStr.substring(0,8) + "T" + serverStr.substring(8) ;
		var curDate = AjxDateUtil.parseServerDateTime(ajxTKServerStr);	
		var formatter = new AjxDateFormat("MM/dd/yyyy HH:mm:ss");
		return formatter.format(curDate) ;	
	}else{
		return ZaMsg.Last_Login_Never;
	}
}

ZaAccount.prototype.manageSpecialAttrs =
function () {
	var warning = "" ;
	
	//handle the unrecognized timezone
	var tz = this.attrs[ZaAccount.A_zimbraPrefTimeZoneId] ;
	if (tz) {
		var n_tz = ZaModel.setUnrecoganizedTimezone(tz) ;
		if (tz != n_tz) {
			this.attrs[ZaAccount.A_zimbraPrefTimeZoneId] = n_tz ;
			warning +=  AjxMessageFormat.format(ZaMsg.WARNING_TIME_ZONE_INVALID ,  [ tz, "account - \"" + this.name +"\""] );
		}
	}

	//handle the unrecognized mail charset
	var mdc = this.attrs[ZaAccount.A_zimbraPrefMailDefaultCharset] ;
	if (mdc) {
		var n_mdc = ZaModel.setUnrecoganizedChoiceValue(mdc, ZaSettings.mailCharsetChoices) ;
		if (mdc != n_mdc) {
			this.attrs[ZaAccount.A_zimbraPrefMailDefaultCharset] = n_mdc ;
			warning += AjxMessageFormat.format(ZaMsg.WARNING_CHARSET_INVALID , [ mdc, "account - \"" + this.name +"\""]);
		}
	}
	//display warnings about the if manageSpecialAttrs return value
	if (warning && warning.length > 0) {
		this._app.getCurrentController().popupMsgDialog (warning, true);
	}	
}

/**
 * Test if the email retention policy should be enabled based on
 * if (serversetting is not set) { //check global setting
       if (gs != 0 ) {
        enable ERP for account on this server
        } else {
        disable ERP for account on this server
  }
}else{  //check server setting
    if ( serverSetting != 0 ) {
        enable ERP for account on this server
      } else if (serverSetting == 0 ){
        disable ERP  for account on this server
      }

}
 */
ZaAccount.isEmailRetentionPolicyEnabled = function () {
    var app = this.getController() ; // it actually returns the app
    var instance  = this.getInstance () ;
    var gc   = app.getGlobalConfig();
    var sc =  app.getServerByName(instance.attrs[ZaAccount.A_mailHost]);
    var s_mailpurge = sc.attrs[ZaServer.A_zimbraMailPurgeSleepInterval] ;    //always end with [s,m,h,d]
    var g_mailpurge = gc.attrs[ZaGlobalConfig.A_zimbraMailPurgeSleepInterval] ;
    if (s_mailpurge === _UNDEFINED_ || s_mailpurge === null)  {
        if (AjxEnv.hasFirebug) console.log("server setting A_zimbraMailPurgeSleepInterval is NOT set.")
        if (g_mailpurge != null && ZaUtil.getLifeTimeInSeconds(g_mailpurge) == 0) {
            return false ;
        }
    }else if (ZaUtil.getLifeTimeInSeconds(s_mailpurge) == 0){
         return false ;
    }

    return true ;
}