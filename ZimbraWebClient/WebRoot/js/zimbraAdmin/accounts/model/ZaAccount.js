/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaAccount
* @contructor ZaAccount
* @param ZaApp app
* this class is a model for zimbraAccount ldap objects
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaAccount = function(noInit) {
	if (noInit) return;	
	ZaItem.call(this, "ZaAccount");
	this._init();
	this.type = ZaItem.ACCOUNT;
}

ZaAccount.prototype = new ZaItem;
ZaAccount.prototype.constructor = ZaAccount;

ZaItem.loadMethods["ZaAccount"] = new Array();
ZaItem.initMethods["ZaAccount"] = new Array();
ZaItem.modifyMethods["ZaAccount"] = new Array();
ZaItem.createMethods["ZaAccount"] = new Array();
ZaItem.ObjectModifiers["ZaAccount"] = [];
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
ZaAccount.A_homePhone = "homePhone" ;
ZaAccount.A_mobile = "mobile";
ZaAccount.A_pager = "pager" ;
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

ZaAccount.A_zimbraIsAdminAccount = "zimbraIsAdminAccount";
ZaAccount.A_zimbraIsDelegatedAdminAccount = "zimbraIsDelegatedAdminAccount" ;

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
ZaAccount.A_zimbraContactMaxNumEntries = "zimbraContactMaxNumEntries";
ZaAccount.A_zimbraMailForwardingAddressMaxLength = "zimbraMailForwardingAddressMaxLength";
ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs = "zimbraMailForwardingAddressMaxNumAddrs";
ZaAccount.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";
ZaAccount.A_zimbraQuotaWarnPercent = "zimbraQuotaWarnPercent";
ZaAccount.A_zimbraQuotaWarnInterval = "zimbraQuotaWarnInterval";
ZaAccount.A_zimbraQuotaWarnMessage = "zimbraQuotaWarnMessage";
ZaAccount.A_zimbraIsSystemResource = "zimbraIsSystemResource";
ZaAccount.A_zimbraExcludeFromCMBSearch = "zimbraExcludeFromCMBSearch";

ZaAccount.A_zimbraAdminAuthTokenLifetime  = "zimbraAdminAuthTokenLifetime";
ZaAccount.A_zimbraAuthTokenValidityValue = "zimbraAuthTokenValidityValue";
ZaAccount.A_zimbraAuthTokenLifetime = "zimbraAuthTokenLifetime";
ZaAccount.A_zimbraMailMessageLifetime = "zimbraMailMessageLifetime";
ZaAccount.A_zimbraMailSpamLifetime = "zimbraMailSpamLifetime";
ZaAccount.A_zimbraMailTrashLifetime = "zimbraMailTrashLifetime";
ZaAccount.A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
ZaAccount.A_zimbraAvailableSkin = "zimbraAvailableSkin";
ZaAccount.A_zimbraZimletAvailableZimlets = "zimbraZimletAvailableZimlets";

ZaAccount.A_zimbraDataSourcePollingInterval = "zimbraDataSourcePollingInterval";

//prefs
ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled = "zimbraPrefAppleIcalDelegationEnabled";
ZaAccount.A_zimbraPrefCalendarShowPastDueReminders = "zimbraPrefCalendarShowPastDueReminders";
ZaAccount.A_zimbraPrefCalendarToasterEnabled = "zimbraPrefCalendarToasterEnabled";
ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf = "zimbraPrefCalendarAllowCancelEmailToSelf";
ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite = "zimbraPrefCalendarAllowPublishMethodInvite";
ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite = "zimbraPrefCalendarAllowForwardedInvite";
ZaAccount.A_zimbraPrefCalendarReminderFlashTitle = "zimbraPrefCalendarReminderFlashTitle";
ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges = "zimbraPrefCalendarNotifyDelegatedChanges";
ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek = "zimbraPrefCalendarFirstDayOfWeek";
ZaAccount.A_zimbraPrefCalendarInitialView = "zimbraPrefCalendarInitialView";
ZaAccount.A_zimbraPrefCalendarForwardInvitesTo = "zimbraPrefCalendarForwardInvitesTo";
ZaAccount.A_zimbraPrefClientType = "zimbraPrefClientType";
ZaAccount.A_zimbraPrefTimeZoneId = "zimbraPrefTimeZoneId";
ZaAccount.A_zimbraAllowAnyFromAddress = "zimbraAllowAnyFromAddress";
ZaAccount.A_zimbraAllowFromAddress = "zimbraAllowFromAddress";
ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
ZaAccount.A_zimbraPrefCalendarUseQuickAdd = "zimbraPrefCalendarUseQuickAdd";
ZaAccount.A_zimbraPrefSaveToSent="zimbraPrefSaveToSent";
ZaAccount.A_zimbraPrefMailSignature="zimbraPrefMailSignature";
ZaAccount.A_zimbraPrefMailSignatureEnabled="zimbraPrefMailSignatureEnabled";
ZaAccount.A_zimbraPrefSentMailFolder = "zimbraPrefSentMailFolder";
ZaAccount.A_zimbraPrefGroupMailBy = "zimbraPrefGroupMailBy";
ZaAccount.A_zimbraPrefIncludeSpamInSearch = "zimbraPrefIncludeSpamInSearch";
ZaAccount.A_zimbraPrefIncludeTrashInSearch = "zimbraPrefIncludeTrashInSearch";
ZaAccount.A_zimbraPrefMailInitialSearch = "zimbraPrefMailInitialSearch";
ZaAccount.A_zimbraMaxMailItemsPerPage = "zimbraMaxMailItemsPerPage";
ZaAccount.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaAccount.A_zimbraPrefMailPollingInterval = "zimbraPrefMailPollingInterval";
ZaAccount.A_zimbraPrefAutoSaveDraftInterval = "zimbraPrefAutoSaveDraftInterval";
ZaAccount.A_zimbraPrefMailFlashTitle = "zimbraPrefMailFlashTitle";
ZaAccount.A_zimbraPrefMailFlashIcon = "zimbraPrefMailFlashIcon" ;
ZaAccount.A_zimbraPrefMailSoundsEnabled = "zimbraPrefMailSoundsEnabled" ;
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
//ZaAccount.A_zimbraPrefMailSignatureStyle = "zimbraPrefMailSignatureStyle";
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
ZaAccount.A_zimbraPrefLocale ="zimbraPrefLocale" ;
ZaAccount.A_zimbraJunkMessagesIndexingEnabled = "zimbraJunkMessagesIndexingEnabled" ;
ZaAccount.A_zimbraPrefMailSendReadReceipts = "zimbraPrefMailSendReadReceipts";
ZaAccount.A_zimbraPrefReadReceiptsToAddress = "zimbraPrefReadReceiptsToAddress";
ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit = "zimbraPrefAdminConsoleWarnOnExit" ;
ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled = "zimbraPrefMandatorySpellCheckEnabled";

//features
ZaAccount.A_zimbraFeatureManageZimlets = "zimbraFeatureManageZimlets";
ZaAccount.A_zimbraFeatureImportExportFolderEnabled = "zimbraFeatureImportExportFolderEnabled";
ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled = "zimbraPrefCalendarReminderSoundsEnabled";
ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply = "zimbraPrefCalendarSendInviteDeniedAutoReply";
ZaAccount.A_zimbraPrefCalendarAutoAddInvites = "zimbraPrefCalendarAutoAddInvites";
ZaAccount.A_zimbraPrefCalendarApptVisibility = "zimbraPrefCalendarApptVisibility";
ZaAccount.A_zimbraFeatureReadReceiptsEnabled = "zimbraFeatureReadReceiptsEnabled";
ZaAccount.A_zimbraFeatureMailPriorityEnabled = "zimbraFeatureMailPriorityEnabled";
ZaAccount.A_zimbraFeatureInstantNotify = "zimbraFeatureInstantNotify";
ZaAccount.A_zimbraFeatureImapDataSourceEnabled = "zimbraFeatureImapDataSourceEnabled";
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
ZaAccount.A_zimbraFeatureBriefcaseSpreadsheetEnabled = "zimbraFeatureBriefcaseSpreadsheetEnabled";
ZaAccount.A_zimbraFeatureBriefcaseSlidesEnabled = "zimbraFeatureBriefcaseSlidesEnabled";
ZaAccount.A_zimbraFeatureBriefcaseDocsEnabled = "zimbraFeatureBriefcaseDocsEnabled";
ZaAccount.A_zimbraFeatureHtmlComposeEnabled = "zimbraFeatureHtmlComposeEnabled";
ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled = "zimbraFeatureGalAutoCompleteEnabled";
ZaAccount.A_zimbraImapEnabled = "zimbraImapEnabled";
ZaAccount.A_zimbraPop3Enabled = "zimbraPop3Enabled";
ZaAccount.A_zimbraFeatureSkinChangeEnabled = "zimbraFeatureSkinChangeEnabled";
ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled = "zimbraFeatureOutOfOfficeReplyEnabled";
ZaAccount.A_zimbraFeatureNewMailNotificationEnabled = "zimbraFeatureNewMailNotificationEnabled";
ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled = "zimbraFeatureMailPollingIntervalPreferenceEnabled" ;
ZaAccount.A_zimbraHideInGal = "zimbraHideInGal";
//ZaAccount.A_zimbraMailCanonicalAddress = "zimbraMailCanonicalAddress";
ZaAccount.A_zimbraMailCatchAllAddress = "zimbraMailCatchAllAddress" ;
ZaAccount.A_zimbraFeatureOptionsEnabled = "zimbraFeatureOptionsEnabled";
//ZaAccount.A_zimbraFeatureShortcutAliasesEnabled = "zimbraFeatureShortcutAliasesEnabled" ;
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
ZaAccount.A_zimbraAdminConsoleUIComponents = "zimbraAdminConsoleUIComponents";

ZaAccount.A_zimbraFreebusyExchangeUserOrg = "zimbraFreebusyExchangeUserOrg" ;
//readonly
ZaAccount.A_zimbraLastLogonTimestamp = "zimbraLastLogonTimestamp";
ZaAccount.A_zimbraPasswordModifiedTime = "zimbraPasswordModifiedTime";


ZaAccount.ACCOUNT_STATUS_ACTIVE = "active";
ZaAccount.ACCOUNT_STATUS_MAINTENANCE = "maintenance";
ZaAccount.ACCOUNT_STATUS_LOCKED = "locked";
ZaAccount.ACCOUNT_STATUS_LOCKOUT = "lockout";
ZaAccount.ACCOUNT_STATUS_CLOSED = "closed";
ZaAccount.ACCOUNT_STATUS_PENDING = "pending" ;

//this attributes are not used in the XML object, but is used in the model
ZaAccount.A2_ldap_ds = "ldap_ds";
ZaAccount.A2_zimbra_ds = "zimbra_ds";
ZaAccount.A2_datasources = "datasources";
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
ZaAccount.A2_nonMemberListSelected = "nonMemberListSelected" ;
ZaAccount.A2_indirectMemberListSelected = "indirectMemberListSelected" ;
ZaAccount.A2_directMemberListSelected = "directMemberListSelected" ;
ZaAccount.A2_showSameDomain = "showSameDomain" ;
ZaAccount.A2_domainLeftAccounts = "leftDomainAccounts" ;
ZaAccount.A2_publicMailURL = "publicMailURL";
ZaAccount.A2_adminSoapURL = "adminSoapURL";
ZaAccount.A2_soapURL = "soapURL";
ZaAccount.MAXSEARCHRESULTS = ZaSettings.MAXSEARCHRESULTS;
ZaAccount.RESULTSPERPAGE = ZaSettings.RESULTSPERPAGE;

ZaAccount.A2_accountTypes = "accountTypes" ; //used to save the account types available to this account based on domain
ZaAccount.A2_currentAccountType = "currentAccountType" ; //used to save the current account type - cos id
ZaAccount.A2_alias_selection_cache = "alias_selection_cache";
ZaAccount.A2_fwdAddr_selection_cache = "fwdAddr_selection_cache";
ZaAccount.A2_calFwdAddr_selection_cache = "calFwdAddr_selection_cache";
ZaAccount.A2_fp_selection_cache = "fp_selection_cache"; 
ZaAccount.A2_errorMessage = "errorMessage";
ZaAccount.A2_warningMessage = "warningMessage";
//constants for rights

ZaAccount.SET_PASSWORD_RIGHT = "setAccountPassword";
ZaAccount.RENAME_ACCOUNT_RIGHT = "renameAccount";
ZaAccount.REINDEX_MBX_RIGHT = "reindexMailbox";
ZaAccount.DELETE_ACCOUNT_RIGHT = "deleteAccount";
ZaAccount.GET_MBX_DUMP_RIGHT = "getMailboxDump";
ZaAccount.VIEW_MAIL_RIGHT = "adminLoginAs";
ZaAccount.ADD_ACCOUNT_ALIAS_RIGHT = "addAccountAlias";
ZaAccount.REMOVE_ACCOUNT_ALIAS_RIGHT = "removeAccountAlias";
ZaAccount.GET_ACCOUNT_MEMBERSHIP_RIGHT = "getAccountMembership";
ZaAccount.GET_MAILBOX_INFO_RIGHT = "getMailboxInfo";
ZaAccount.GET_ACCOUNT_INFO_RIGHT = "getAccountInfo";

ZaAccount.checkValues = 
function(tmpObj) {
	/**
	* check values
	**/

	if(ZaItem.hasWritePermission(ZaAccount.A_name,tmpObj) && (tmpObj.name == null || tmpObj.name.length < 1)) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_REQUIRED);
		return false;
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_lastName,tmpObj) && (tmpObj.attrs[ZaAccount.A_lastName] == null || tmpObj.attrs[ZaAccount.A_lastName].length < 1)) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_LAST_NAME_REQUIRED);
		return false;
	}

	/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;
	}*/
	if(ZaItem.hasWritePermission(ZaAccount.A_name,tmpObj) && !AjxUtil.isValidEmailNonReg(tmpObj.name)) {
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_NAME_INVALID);
		return false;		
	}

	var maxPwdLen = Number.POSITIVE_INFINITY;
	var minPwdLen = 0;	
	var maxPwdAge = Number.POSITIVE_INFINITY;
	var minPwdAge = 0;		


	//validate this account's password constraints
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMinPwdLength])) ;
		return false;
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMaxPwdLength])) ;
		return false;
	}	
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]);
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]);
		
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdAge,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMinPwdAge])) ;		
		return false;
	}		
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdAge,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] != "" && tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge])) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMaxPwdAge])) ;
		return false;
	}		
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdAge,tmpObj) && tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge])
		tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]);
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdAge,tmpObj) && tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])
		tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] = parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]);
	
	//validate password length against this account's or COS setting
	//if the account did not have a valid cos id - pick the first COS
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
			minPwdLen = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMinPwdLength]);
		} else {
			minPwdLen = parseInt(tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMinPwdLength]);
		}
	}	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
			maxPwdLen = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxPwdLength]);
		} else {
			maxPwdLen = parseInt (tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMaxPwdLength]);
		}
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj) || ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj)) {	
		if(maxPwdLen < minPwdLen) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDLENGTH);
			return false;		
		}
	}		

	//validate password age settings
	//if the account did not have a valid cos id - pick the first COS
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge] != null) {
			maxPwdAge = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxPwdAge]);
		} else {
			maxPwdAge = parseInt ( tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMaxPwdAge]);
		}
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdAge,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge] != null) {
			minPwdAge = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMinPwdAge]);
		} else {
			minPwdAge = parseInt (tmpObj._defaultValues.attrs[ZaCos.A_zimbraMinPwdAge]);
		}
	}		
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxPwdLength,tmpObj) || ZaItem.hasWritePermission(ZaAccount.A_zimbraMinPwdLength,tmpObj)) {	
		if(maxPwdAge < minPwdAge) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDAGE);
			return false;		
		}	
	}
	//if there is a password - validate it
	if(ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_password]!=null || tmpObj[ZaAccount.A2_confirmPassword]!=null) {
			if(tmpObj.attrs[ZaAccount.A_password] != tmpObj[ZaAccount.A2_confirmPassword]) {
				//show error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			} 			
			if(tmpObj.attrs[ZaAccount.A_password].length < minPwdLen || AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length < minPwdLen) { 
				//show error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen));
				return false;		
			}
			
			if(AjxStringUtil.trim(tmpObj.attrs[ZaAccount.A_password]).length > maxPwdLen) { 
				//show error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen));
				return false;		
			}
		}
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailQuota,tmpObj)) {		
		if(tmpObj.attrs[ZaAccount.A_zimbraMailQuota] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailQuota] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMailQuota])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailQuota])) ;
			return false;
		}
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraContactMaxNumEntries,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] != "" && tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraContactMaxNumEntries])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailForwardingAddressMaxLength,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxLength] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxLength] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxLength])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailForwardingAddressMaxLength])) ;
			return false;
		}
	}	

        if(ZaItem.hasWritePermission(ZaAccount.A_zimbraDataSourcePollingInterval,tmpObj)) {
                var p_dataPollingInterval = tmpObj.attrs[ZaAccount.A_zimbraDataSourcePollingInterval] ;
                if( p_dataPollingInterval != "" && p_dataPollingInterval !=null && !AjxUtil.isLifeTime(p_dataPollingInterval)) {
                        ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourcePollingInterval])) ;
                        return false;
                }
        }

        if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefAutoSaveDraftInterval,tmpObj)) {
                var p_autoSaveInterval = tmpObj.attrs[ZaAccount.A_zimbraPrefAutoSaveDraftInterval] ;
                if( p_autoSaveInterval != "" && p_autoSaveInterval !=null && !AjxUtil.isLifeTime(p_autoSaveInterval)) {
                        ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefAutoSaveDraftInterval])) ;
                        return false;
                }
        }
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailForwardingAddressMaxNumAddrs])) ;		
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraContactMaxNumEntries,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries])
			tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraContactMaxNumEntries]);
	}

	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinUpperCaseChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinUpperCaseChars])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinUpperCaseChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars])
			tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinUpperCaseChars]);
	}

	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinLowerCaseChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinLowerCaseChars])) ;		
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinLowerCaseChars,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars])
			tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinLowerCaseChars]);
	}

	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinPunctuationChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinPunctuationChars])) ;		
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinPunctuationChars,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars])
			tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinPunctuationChars]);
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinNumericChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinNumericChars])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordMinNumericChars,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars])
			tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars] = parseInt	(tmpObj.attrs[ZaAccount.A_zimbraPasswordMinNumericChars]);
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenLifetime,tmpObj)) {			
		if(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraAuthTokenLifetime])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraAuthTokenLifetime])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraAdminAuthTokenLifetime,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraAdminAuthTokenLifetime])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraAdminAuthTokenLifetime])) ;
			return false;
		}	
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration] != "" && tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefOutOfOfficeCacheDuration])) ;
			return false;
		}
	}
		
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefMailPollingInterval,tmpObj)) {
		var p_mailPollingInterval = tmpObj.attrs[ZaAccount.A_zimbraPrefMailPollingInterval] ;
		if( p_mailPollingInterval != "" && p_mailPollingInterval !=null && !AjxUtil.isLifeTime(p_mailPollingInterval)) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefMailPollingInterval])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailMinPollingInterval,tmpObj)) {
		var min_mailPollingInterval = tmpObj.attrs[ZaAccount.A_zimbraMailMinPollingInterval]
		if( min_mailPollingInterval != "" && min_mailPollingInterval !=null && !AjxUtil.isLifeTime(min_mailPollingInterval)) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailMinPollingInterval])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailMinPollingInterval,tmpObj)) {	
		if (min_mailPollingInterval == "" || min_mailPollingInterval == null) {
			//take the cos value
			min_mailPollingInterval = tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMailMinPollingInterval];
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefMailPollingInterval,tmpObj)) {
		if (p_mailPollingInterval == "" || p_mailPollingInterval == null){
			p_mailPollingInterval = tmpObj._defaultValues.attrs[ZaAccount.A_zimbraPrefMailPollingInterval];
		}
		if(p_mailPollingInterval != null && min_mailPollingInterval != null) {
			if (ZaUtil.getLifeTimeInSeconds(p_mailPollingInterval) < ZaUtil.getLifeTimeInSeconds(min_mailPollingInterval)){
				ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_mailPollingInterval) ;
				return false ;
			}
		}
	}
			
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailIdleSessionTimeout,tmpObj)) {		
		if(tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailIdleSessionTimeout])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailIdleSessionTimeout])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailMessageLifetime,tmpObj)) {		
		if(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime] !=null) {
			if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime])) {
				//show error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailMessageLifetime])) ;
				return false;
			}
			var itestVal = parseInt(tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime].substr(0, tmpObj.attrs[ZaAccount.A_zimbraMailMessageLifetime].length-1));			
			if(itestVal > 0 && itestVal < 31) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MESSAGE_LIFETIME_BELOW_31);
				return false;
			}
		}			
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailTrashLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailTrashLifetime])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailTrashLifetime])) ;
			return false;
		}	
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMailSpamLifetime,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] != 0 && tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraMailSpamLifetime])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailSpamLifetime])) ;
			return false;
		}		
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordLockoutFailureLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime] !=null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutFailureLifetime])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordLockoutFailureLifetime])) ;		
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPasswordLockoutDuration,tmpObj)) {	
		if(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] != "" && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] !=null && tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration] !=0 && !AjxUtil.isLifeTime(tmpObj.attrs[ZaAccount.A_zimbraPasswordLockoutDuration])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordLockoutDuration])) ;
			return false;
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefContactsPerPage,tmpObj)) {		
		if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] != "" && tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefContactsPerPage])) ;
			return false;
		}
	}	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefContactsPerPage,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage])
			tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraPrefContactsPerPage]);
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraEnforcePwdHistory,tmpObj)) {
		if(tmpObj.attrs[ZaAccount.A__zimbraEnforcePwdHistory] != "" && tmpObj.attrs[ZaAccount.A_zimbraEnforcePwdHistory] !=null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaAccount.A_zimbraEnforcePwdHistory])) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraEnforcePwdHistory])) ;
			return false;
		}	
		if(tmpObj.attrs[ZaAccount.A_zimbraEnforcePwdHistory])
			tmpObj.attrs[ZaAccount.A_zimbraEnforcePwdHistory] = parseInt(tmpObj.attrs[ZaAccount.A_zimbraEnforcePwdHistory]);
	}
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxMailItemsPerPage,tmpObj)) {	
		var maxItemsPerPage;
		if(tmpObj.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage] != null) {
			maxItemsPerPage = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage]);
		} else {
			maxItemsPerPage = parseInt ( tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage]);
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefMailItemsPerPage,tmpObj)) {	
		var prefItemsPerPage;
		if(tmpObj.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage] != null) {
			prefItemsPerPage = parseInt (tmpObj.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage]);
		} else {
			prefItemsPerPage = parseInt ( tmpObj._defaultValues.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage]);
		}
	}
	
	if(ZaItem.hasWritePermission(ZaAccount.A_zimbraPrefMailItemsPerPage,tmpObj) && ZaItem.hasWritePermission(ZaAccount.A_zimbraMaxMailItemsPerPage,tmpObj)) {		
		if(maxItemsPerPage < prefItemsPerPage) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ITEMS_PER_PAGE_OVER_MAX);
			return false;		
		}	
	}			
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_SKIN_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		//check that current theme is part of selected themes
		var currentTheme = tmpObj.attrs[ZaAccount.A_zimbraPrefSkin] ? tmpObj.attrs[ZaAccount.A_zimbraPrefSkin] : tmpObj._defaultValues.attrs[ZaCos.A_zimbraPrefSkin];
		var availableThemes = tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] ? tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] : tmpObj._defaultValues.attrs[ZaCos.A_zimbraAvailableSkin];	
	
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
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format (ZaMsg.NAD_WarningCurrentThemeNotAvail, [currentTheme, currentTheme]));
				return false;			
			}
		}	
	}		

    if (!ZaAccount.isAccountTypeSet(tmpObj))  {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_TYPE_NOT_SET);
        return false;
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
function (tmpObj, account) {
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
		reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
		reqMgrParams.busyMsg = ZaMsg.BUSY_CREATE_ACCOUNTS ;
		//reqMgrParams.busyMsg = "Creating Accounts ...";
		//resp = createAccCommand.invoke(params).Body.CreateAccountResponse;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CreateAccountResponse;
	} catch (ex) {
		throw ex;
		return null;
	}
	
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
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.WARNING_ALIAS_EXISTS, [failedAliases]));
			} else if(failedAliasesCnt > 1) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.WARNING_ALIASES_EXIST, [failedAliases]));
			}
		} catch (ex) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);
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
	var gotSomething = false;
	//update the object
	var soapDoc = AjxSoapDoc.create("ModifyAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		gotSomething = true;
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
	
	if(!gotSomething)
		return;
		
	//var modifyAccCommand = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller:ZaApp.getInstance().getCurrentController(),
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
function(accId) {
	var retVal={authToken:"", lifetime:0};
	var soapDoc = AjxSoapDoc.create("DelegateAuthRequest", ZaZimbraAdmin.URN, null);	
	var attr = soapDoc.set("account", accId);
	attr.setAttribute("by", "id");
	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	//var resp = command.invoke(params).Body.DelegateAuthResponse;
	var reqMgrParams = {
		controller: ZaApp.getInstance().getCurrentController ()
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.DelegateAuthResponse ; 
	retVal.authToken = resp.authToken[0]._content;
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
ZaReindexMailbox.A_status = "status";
ZaReindexMailbox.A_numSucceeded = "numSucceeded";
ZaReindexMailbox.A_numFailed = "numFailed";
ZaReindexMailbox.A_numRemaining = "numRemaining";
ZaReindexMailbox.A_mbxId = "mbxId";
ZaReindexMailbox.A_numTotal = "numTotal";
ZaReindexMailbox.A_numDone = "numDone";
ZaReindexMailbox.A_pollInterval = "pollInterval";
ZaReindexMailbox.A_progressMsg = "progressMsg";
ZaReindexMailbox.A_errorDetail = "errorDetail";
ZaReindexMailbox.A_resultMsg = "resultMsg";

ZaReindexMailbox.myXModel = {
	items: [
		{id:ZaReindexMailbox.A_status, ref:ZaReindexMailbox.A_status, type:_STRING_},						
		{id:ZaReindexMailbox.A_numSucceeded, ref:ZaReindexMailbox.A_numSucceeded, type:_NUMBER_},								
		{id:ZaReindexMailbox.A_numFailed, ref:ZaReindexMailbox.A_numFailed, type:_NUMBER_},										
		{id:ZaReindexMailbox.A_numRemaining, ref:ZaReindexMailbox.A_numRemaining, type:_NUMBER_},												
		{id:ZaReindexMailbox.A_mbxId, ref:ZaReindexMailbox.A_mbxId, type:_STRING_},														
		{id:ZaReindexMailbox.A_numTotal, ref:ZaReindexMailbox.A_numTotal, type:_NUMBER_},								
		{id:ZaReindexMailbox.A_numDone, ref:ZaReindexMailbox.A_numDone, type:_NUMBER_},							
		{id:ZaReindexMailbox.A_pollInterval, ref:ZaReindexMailbox.A_pollInterval, type:_STRING_},
		{id:ZaReindexMailbox.A_progressMsg, ref:ZaReindexMailbox.A_progressMsg, type:_STRING_},
		{id:ZaReindexMailbox.A_resultMsg, ref:ZaReindexMailbox.A_pollInterval, type:_STRING_},
		{id:ZaReindexMailbox.A_errorDetail, ref:ZaReindexMailbox.A_pollInterval, type:_STRING_}
	]
};

ZaAccount.prototype.remove = 
function(callback) {
	var soapDoc;
	if(this[ZaAccount.A2_ldap_ds] || this[ZaAccount.A2_zimbra_ds]) {
		soapDoc = AjxSoapDoc.create("DeleteGalSyncAccountRequest", ZaZimbraAdmin.URN, null);
		var accEl = soapDoc.set("account", this.id);
		accEl.setAttribute("by", "id");
	} else {
		soapDoc = AjxSoapDoc.create("DeleteAccountRequest", ZaZimbraAdmin.URN, null);
		soapDoc.set("id", this.id);
	}
	
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
function (respObj, instance, form) {
	if(!respObj)
		return;
	if(respObj.isException && respObj.isException()) {
		var errCode = respObj.getException().code;
		if(errCode && errCode == "service.NOT_IN_PROGRESS") {
			form.setInstanceValue("", ZaReindexMailbox.A_errorDetail);
			form.setInstanceValue("", ZaReindexMailbox.A_resultMsg);
			form.setInstanceValue(ZaMsg.NAD_ACC_ReindexingNotRunning, ZaReindexMailbox.A_progressMsg);
			if(instance.numRemaining > 0 || instance.status == "started") {
				form.setInstanceValue(instance.numTotal, ZaReindexMailbox.A_numDone);
				form.setInstanceValue(ZaMsg.NAD_ACC_ReindexingComplete, ZaReindexMailbox.A_progressMsg);
				form.setInstanceValue("complete", ZaReindexMailbox.A_status);
			} else {
				form.setInstanceValue(null, ZaReindexMailbox.A_status);
				//instance.status = null;
			}
		} else if(errCode && errCode == ZmCsfeException.EMPTY_RESPONSE) {
			form.setInstanceValue(ZaMsg.ERROR_RECEIVED_EMPTY_RESPONSE,ZaReindexMailbox.A_resultMsg);
			form.setInstanceValue(null,ZaReindexMailbox.A_errorDetail);
			form.setInstanceValue("error", ZaReindexMailbox.A_status);
		} else {
			var detail = respObj.getException().detail;
			var msg = respObj.getException().msg;
			var strBuf = [];
			if(detail) {
				strBuf.push(detail);
			}	
			if(msg) {
				strBuf.push(msg);
			}
			form.setInstanceValue(AjxMessageFormat.format(ZaMsg.FAILED_REINDEX,[errCode]),ZaReindexMailbox.A_resultMsg);
			form.setInstanceValue(strBuf.join("\n"),ZaReindexMailbox.A_errorDetail);
			form.setInstanceValue("error", ZaReindexMailbox.A_status);
		}
	} else  {
		var resp;
		if(respObj.getResponse) {
			resp = respObj.getResponse();
		} else if(respObj.Body.ReIndexResponse) {
			resp = respObj;
		}
		if(resp && resp.Body.ReIndexResponse) {
			form.setInstanceValue(resp.Body.ReIndexResponse.status, ZaReindexMailbox.A_status);

			if(instance.status == "started") {
				form.setInstanceValue(0, ZaReindexMailbox.A_numDone);
				form.setInstanceValue(ZaMsg.NAD_ACC_ReindexingStarted, ZaReindexMailbox.A_progressMsg);
			}
			if(resp.Body.ReIndexResponse.progress && resp.Body.ReIndexResponse.progress[0]) {
				var progress = resp.Body.ReIndexResponse.progress[0];
				
				form.setInstanceValue(progress.numFailed, ZaReindexMailbox.A_numFailed);
				form.setInstanceValue(progress.numSucceeded, ZaReindexMailbox.A_numSucceeded);
				form.setInstanceValue(progress.numRemaining, ZaReindexMailbox.A_numRemaining);
				form.setInstanceValue(progress.numSucceeded + progress.numFailed + progress.numRemaining, ZaReindexMailbox.A_numTotal);
				form.setInstanceValue(progress.numFailed + progress.numSucceeded, ZaReindexMailbox.A_numDone);
				form.setInstanceValue(AjxMessageFormat.format(ZaMsg.NAD_ACC_ReindexingStatus,[progress.numSucceeded,progress.numRemaining,progress.numFailed]), ZaReindexMailbox.A_progressMsg);
				
				//instance.numFailed = progress.numFailed;
				//instance.numSucceeded = progress.numSucceeded;				
				//instance.numRemaining = progress.numRemaining;	
				//instance.numTotal = instance.numSucceeded + instance.numFailed + instance.numRemaining;
				//instance.numDone  = instance.numFailed + instance.numSucceeded;					
				//instance.progressMsg = String(ZaMsg.NAD_ACC_ReindexingStatus).replace("{0}", instance.numSucceeded).replace("{1}",instance.numRemaining).replace("{2}", instance.numFailed);				
				if(instance.status == "cancelled") {
					form.setInstanceValue((instance.progressMsg + "<br>" + ZaMsg.NAD_ACC_ReindexingCancelled), ZaReindexMailbox.A_progressMsg);
					//instance.progressMsg = instance.progressMsg + "<br>" + ZaMsg.NAD_ACC_ReindexingCancelled;
				}			
				if(instance.numRemaining == 0) {
					form.setInstanceValue(instance.numTotal, ZaReindexMailbox.A_numDone);
					//instance.numDone = instance.numTotal;
				}					
			}
		}
	}
}

ZaAccount.prototype.initFromDom =
function(node) {
	this.name = node.getAttribute("name");
	this.id = node.getAttribute("id");
	this.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
	this.attrs[ZaAccount.A_zimbraMailForwardingAddress] = new Array();
	this.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo] = new Array();
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
	this.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo] = new Array();	
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
	if(!(this.attrs[ZaAccount.A_description] instanceof Array)) {
		this.attrs[ZaAccount.A_description] = [this.attrs[ZaAccount.A_description]];
	}
	
	if(!this.attrs[ZaItem.A_zimbraId] && this.id) {
		this.attrs[ZaItem.A_zimbraId] = this.id;
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
		idx = this._addRow(ZaMsg.NAD_MailServer, this.attrs[ZaAccount.A_mailHost], html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}



ZaAccount.loadMethod = 
function(by, val) {
	var soapDoc, params, resp;
	//batch the rest of the requests
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");

	if(by=="id") {
		this.id = val;
		this.attrs[ZaItem.A_zimbraId] = val;
		var getAccDoc = soapDoc.set("GetAccountRequest", null, null, ZaZimbraAdmin.URN);
		getAccDoc.setAttribute("applyCos", "0");
		if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
			getAccDoc.setAttribute("attrs", this.attrsToGet.join(","));
		}
		var elBy = soapDoc.set("account", val, getAccDoc);
		elBy.setAttribute("by", by);		
	} else {
		var getAccDoc = AjxSoapDoc.create("GetAccountRequest", ZaZimbraAdmin.URN, null);
		getAccDoc.getMethod().setAttribute("applyCos", "0");
		if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
			getAccDoc.getMethod().setAttribute("attrs", this.attrsToGet.join(","));
		}
		var elBy = getAccDoc.set("account", val);
		elBy.setAttribute("by", by);

		//var getAccCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = getAccDoc;	
		var reqMgrParams = {
			controller:ZaApp.getInstance().getCurrentController()
		}
		resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAccountResponse;
		this.attrs = new Object();
		this.initFromJS(resp.account[0]);
	}
	
	if(!AjxUtil.isEmpty(this.attrs[ZaAccount.A_mailHost]) && ZaItem.hasRight(ZaAccount.GET_MAILBOX_INFO_RIGHT,this)) {
		var getMailboxReq = soapDoc.set("GetMailboxRequest", null, null, ZaZimbraAdmin.URN);
		var mbox = soapDoc.set("mbox", "", getMailboxReq);
		mbox.setAttribute("id", this.attrs[ZaItem.A_zimbraId]);
	}				
	this[ZaAccount.A2_confirmPassword] = null;
	
	//Make a GetAccountMembershipRequest
	if(ZaItem.hasRight(ZaAccount.GET_ACCOUNT_MEMBERSHIP_RIGHT,this)) {
		var getAccMembershipReq = soapDoc.set("GetAccountMembershipRequest", null, null, ZaZimbraAdmin.URN);
		var account = soapDoc.set("account", this.attrs[ZaItem.A_zimbraId], getAccMembershipReq);
		account.setAttribute("by","id");		
	}
	
	if(ZaItem.hasRight(ZaAccount.GET_ACCOUNT_INFO_RIGHT,this)) {
		var getAccInfoReq = soapDoc.set("GetAccountInfoRequest", null, null, ZaZimbraAdmin.URN);
		var account = soapDoc.set("account", this.attrs[ZaItem.A_zimbraId], getAccInfoReq);
		account.setAttribute("by","id");		
	}

	if(ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT, this)) {
		var getDSReq = soapDoc.set("GetDataSourcesRequest", null, null, ZaZimbraAdmin.URN);
		var elId = soapDoc.set("id", this.attrs[ZaItem.A_zimbraId], getDSReq);
	}
	
    var hasError = false ;
    var lastException  ;
	if(by=="id" || 
		ZaItem.hasRight(ZaAccount.GET_ACCOUNT_INFO_RIGHT,this) || ZaItem.hasRight(ZaAccount.GET_ACCOUNT_MEMBERSHIP_RIGHT,this) ||
		(!AjxUtil.isEmpty(this.attrs[ZaAccount.A_mailHost]) && ZaItem.hasRight(ZaAccount.GET_MAILBOX_INFO_RIGHT,this)) ) {
		try {
			params = new Object();
			params.soapDoc = soapDoc;	
			var reqMgrParams ={
				controller:ZaApp.getInstance().getCurrentController()
			}
			var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
			if(respObj.isException && respObj.isException()) {
				ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaAccount.loadMethod", null, false);
			    hasError  = true ;
                lastException = ex ;
            } else if(respObj.Body.BatchResponse.Fault) {
				var fault = respObj.Body.BatchResponse.Fault;
				if(fault instanceof Array)
					fault = fault[0];
			
				if (fault) {
					// JS response with fault
					var ex = ZmCsfeCommand.faultToEx(fault);
					ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaAccount.loadMethod", null, false);
                    hasError = true ;
                    lastException = ex ;
                }
			} else {
				var batchResp = respObj.Body.BatchResponse;
				
				if(batchResp.GetAccountResponse) {
					resp = batchResp.GetAccountResponse[0];
					this.initFromJS(resp.account[0]);
				}
				
				if(batchResp.GetMailboxResponse) {
					resp = batchResp.GetMailboxResponse[0];
					if(resp && resp.mbox && resp.mbox[0]) {
						this.attrs[ZaAccount.A2_mbxsize] = resp.mbox[0].s;
					}
				}
				
				if(batchResp.GetAccountMembershipResponse) {
					resp = batchResp.GetAccountMembershipResponse[0];
					this[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.parseGetAccMembershipResponse(resp) ;
					this[ZaAccount.A2_directMemberList + "_more"] = 
						(this[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
					this[ZaAccount.A2_indirectMemberList + "_more"] = 
						(this[ZaAccount.A2_memberOf][ZaAccount.A2_indirectMemberList].length > ZaAccountMemberOfListView.SEARCH_LIMIT) ? 1: 0;
				}
				
				if(batchResp.GetAccountInfoResponse) {
					resp = batchResp.GetAccountInfoResponse[0];
					if(resp[ZaAccount.A2_publicMailURL] && resp[ZaAccount.A2_publicMailURL][0])
						this[ZaAccount.A2_publicMailURL] = resp[ZaAccount.A2_publicMailURL][0]._content;
					
					if(resp[ZaAccount.A2_adminSoapURL] && resp[ZaAccount.A2_adminSoapURL][0])
						this[ZaAccount.A2_adminSoapURL] = resp[ZaAccount.A2_adminSoapURL][0]._content;
					
					if(resp[ZaAccount.A2_soapURL] && resp[ZaAccount.A2_soapURL][0])
						this[ZaAccount.A2_soapURL] = resp[ZaAccount.A2_soapURL][0]._content;
				
				    if (resp.cos && resp.cos.id)
				        this[ZaAccount.A2_currentAccountType] = resp.cos.id ;					
				}
				
				if(batchResp.GetDataSourcesResponse && batchResp.GetDataSourcesResponse instanceof Array && batchResp.GetDataSourcesResponse[0]) {
					this[ZaAccount.A2_datasources] = new ZaItemList(ZaDataSource);
					this[ZaAccount.A2_datasources].loadFromJS(batchResp.GetDataSourcesResponse[0]);
					var dss = this[ZaAccount.A2_datasources].getArray(); 
					if(dss && dss.length) {
						for(var i=0; i < dss.length; i++) {
							if(dss[i].attrs[ZaDataSource.A_zimbraDataSourceType] == ZaDataSource.DS_TYPE_GAL) {
								if(dss[i].attrs[ZaDataSource.A_zimbraGalType] == ZaDataSource.GAL_TYPE_ZIMBRA) {
									this[ZaAccount.A2_zimbra_ds] = dss[i];
								} else if(dss[i].attrs[ZaDataSource.A_zimbraGalType] == ZaDataSource.GAL_TYPE_LDAP) {
									this[ZaAccount.A2_ldap_ds] = dss[i];
								}
							}
						}
					}
				}
			}
		} catch (ex) {
			//show the error and go on
			//we should not stop the Account from loading if some of the information cannot be acces
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccount.prototype.load", null, false);
		    hasError = true ;
            lastException = ex ;
        }
	}

    if (hasError) {
        throw lastException ;
    }
	
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
	
}

ZaItem.loadMethods["ZaAccount"].push(ZaAccount.loadMethod);

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
    	{id:"getAttrs",type:_LIST_},
    	{id:"setAttrs",type:_LIST_},
    	{id:"rights",type:_LIST_},
    	{id:ZaItem.A_zimbraACE, ref:"attrs/" + ZaItem.A_zimbraACE, type:_LIST_},
    	{id:ZaAccount.A2_errorMessage, ref:ZaAccount.A2_errorMessage, type:_STRING_},
    	{id:ZaAccount.A2_warningMessage, ref:ZaAccount.A2_warningMessage, type:_STRING_},
        {id:ZaAccount.A2_domainLeftAccounts, ref:ZaAccount.A2_domainLeftAccounts, type:_STRING_},
        {id:ZaAccount.A_name, type:_STRING_, ref:"name", required:true, 
        	constraints: {type:"method", value:
			   function (value, form, formItem, instance) {				   
				   if (value){
					  	if(AjxUtil.isValidEmailNonReg(value)) {
						   return value;
					   } else {
						   throw ZaMsg.ErrorInvalidEmailAddress;
					   }
				   }
			   }
			}
        },
        {id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
        {id:ZaAccount.A_uid, type:_STRING_, ref:"attrs/"+ZaAccount.A_uid},
        {id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp},
        {id:ZaAccount.A_accountName, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountName},
        {id:ZaAccount.A_firstName, type:_STRING_, ref:"attrs/"+ZaAccount.A_firstName},
        {id:ZaAccount.A_lastName, type:_STRING_, ref:"attrs/"+ZaAccount.A_lastName, required:true},
        {id:ZaAccount.A_mail, type:_STRING_, ref:"attrs/"+ZaAccount.A_mail},
        {id:ZaAccount.A_password, type:_STRING_, ref:"attrs/"+ZaAccount.A_password},
        {id:ZaAccount.A2_confirmPassword, type:_STRING_},
         ZaItem.descriptionModelItem,
            /*
        {id:ZaAccount.A_description, type: _LIST_, ref:"attrs/"+ZaAccount.A_description,
            listItem:{type:_STRING_}
        },    */
        {id:ZaAccount.A_telephoneNumber, type:_STRING_, ref:"attrs/"+ZaAccount.A_telephoneNumber},
        {id:ZaAccount.A_mobile, type:_STRING_, ref:"attrs/"+ZaAccount.A_mobile},
        {id:ZaAccount.A_pager, type:_STRING_, ref:"attrs/"+ZaAccount.A_pager},
        {id:ZaAccount.A_homePhone, type:_STRING_, ref:"attrs/"+ZaAccount.A_homePhone},
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
//        {id:ZaAccount.A_zimbraMailCanonicalAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaAccount.A_zimbraMailCanonicalAddress},
        {id:ZaAccount.A_accountStatus, type:_STRING_, ref:"attrs/"+ZaAccount.A_accountStatus},
        {id:ZaAccount.A_notes, type:_STRING_, ref:"attrs/"+ZaAccount.A_notes},
        {id:ZaAccount.A_zimbraMailQuota, type:_COS_MAILQUOTA_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota},
        {id:ZaAccount.A_mailHost, type:_STRING_, ref:"attrs/"+ZaAccount.A_mailHost},
        {id:ZaAccount.A_COSId, type:_STRING_, ref:"attrs/" + ZaAccount.A_COSId},
        {id:ZaAccount.A_zimbraIsAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraIsAdminAccount},
        {id:ZaAccount.A_zimbraIsSystemResource, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraIsSystemResource},
        
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
		{id:ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, type:_LIST_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, listItem:{type:_EMAIL_ADDRESS_}},        
        {id:ZaAccount.A_zimbraPasswordMustChange, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPasswordMustChange},
        {id:ZaAccount.A_zimbraPasswordLocked, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLocked, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraContactMaxNumEntries, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraContactMaxNumEntries, maxInclusive:2147483647, minInclusive:0},
        {id:ZaAccount.A_zimbraMailForwardingAddressMaxLength, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMailForwardingAddressMaxLength, maxInclusive:2147483647, minInclusive:0},
	{id:ZaAccount.A_zimbraDataSourcePollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraDataSourcePollingInterval},
        {id:ZaAccount.A_zimbraPrefAutoSaveDraftInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefAutoSaveDraftInterval},
        {id:ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs, maxInclusive:2147483647, minInclusive:0},
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
        {id:ZaAccount.A_zimbraPrefSaveToSent, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefSaveToSent, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefMailSignature, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSignature},
        {id:ZaAccount.A_zimbraPrefMailSignatureEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSignatureEnabled},
        //preferences
        {id:ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarShowPastDueReminders, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarShowPastDueReminders, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarToasterEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarToasterEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarReminderFlashTitle, type:_COS_ENUM_, ref:"attrs/" + ZaAccount.A_zimbraPrefCalendarReminderFlashTitle, choices:ZaModel.BOOLEAN_CHOICES}, 
        {id:ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges, type:_COS_ENUM_, ref:"attrs/" + ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek, choices:ZaSettings.dayOfWeekChoices},
        {id:ZaAccount.A_zimbraPrefCalendarInitialView, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarInitialView, choices:ZaSettings.calendarViewChoinces},
        {id:ZaAccount.A_zimbraPrefClientType, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefClientType, choices:ZaSettings.clientTypeChoices},
        {id:ZaAccount.A_zimbraPrefTimeZoneId,type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefTimeZoneId, choices:ZaSettings.timeZoneChoices},
        {id:ZaAccount.A_zimbraPrefMailDefaultCharset,type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailDefaultCharset, choices:ZaSettings.mailCharsetChoices},
        {id:ZaAccount.A_zimbraPrefLocale,type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefLocale},
        {id:ZaAccount.A_zimbraPrefSentMailFolder, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefSentMailFolder},
        {id:ZaAccount.A_zimbraPrefIncludeSpamInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeSpamInSearch, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefIncludeTrashInSearch, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefIncludeTrashInSearch, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefMailInitialSearch, type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailInitialSearch},
        {id:ZaAccount.A_zimbraMaxMailItemsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraMaxMailItemsPerPage,maxInclusive:2147483647, minInclusive:0},
        {id:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailItemsPerPage, choices:[10,25,50,100]},
        {id:ZaAccount.A_zimbraPrefMailPollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailPollingInterval},
        {id:ZaAccount.A_zimbraMailMinPollingInterval, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraMailMinPollingInterval},
        {id:ZaAccount.A_zimbraPrefMailFlashIcon, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailFlashIcon, type:_COS_ENUM_},
        {id:ZaAccount.A_zimbraPrefMailFlashTitle, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailFlashTitle, type:_COS_ENUM_},
        {id:ZaAccount.A_zimbraPrefMailSoundsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSoundsEnabled, type:_COS_ENUM_},
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
        {id:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailForwardingAddress,
         constraints: {type:"method", value:
	     function (value, form, formItem, instance) {				   	 
                 if (value){
		      var i;
                      var startIndex = 0;
                      var endIndex = 0;
                      var currentEmailAddress;
                      var ret;
                      var isThrown = false;
                      value = value.replace(/\s/g,""); //delete all the white space
                      for(i = 0; i < value.length; i++){
                         endIndex = value.indexOf(",", startIndex);
                         if(endIndex == -1){
                            currentEmailAddress = value.substring(startIndex);
                    
                            if(!AjxUtil.isEmailAddress(currentEmailAddress, false)){
                                   isThrown = true;
                            }
                            
                            break;
                         }   
                         currentEmailAddress = value.substring(startIndex, endIndex);
                         
                         if(!AjxUtil.isEmailAddress(currentEmailAddress, false)){ 
                             isThrown = true;
                             break;
                         }
                      
                         startIndex = endIndex + 1;
                    }
                    if(isThrown){
                       throw  ZaMsg.ErrorInvalidEmailAddressList;
                    } 	
	         }
                return value;
	   }
	 } 
        },
        {id:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefNewMailNotificationEnabled},
        {id:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeReply},
        {id:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled},
        {id:ZaAccount.A_zimbraPrefShowSearchString, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefShowSearchString, choices:ZaModel.BOOLEAN_CHOICES},
        //{id:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSignatureStyle, choices:ZaModel.SIGNATURE_STYLE_CHOICES},
        {id:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarUseQuickAdd, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefSkin, type:_COS_STRING_, ref:"attrs/"+ZaAccount.A_zimbraPrefSkin},
        {id:ZaAccount.A_zimbraAvailableSkin, type:_COS_LIST_, ref:"attrs/" + ZaAccount.A_zimbraAvailableSkin, dataType: _STRING_},
        {id:ZaAccount.A_zimbraZimletAvailableZimlets, type:_COS_LIST_, ref:"attrs/" + ZaAccount.A_zimbraZimletAvailableZimlets, dataType: _STRING_,outputType:_LIST_},
        {id:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefGalAutoCompleteEnabled},
        {id:ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit},
        {id:ZaAccount.A_zimbraPrefWarnOnExit, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefWarnOnExit},
        {id:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaAccount.A_zimbraPrefShowSelectionCheckbox},
        {id:ZaAccount.A_zimbraPrefDisplayExternalImages, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefDisplayExternalImages, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration},
        {id:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraJunkMessagesIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefMailSendReadReceipts, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefMailSendReadReceipts, choices:ZaModel.SEND_READ_RECEPIT_CHOICES},
		{id:ZaAccount.A_zimbraPrefReadReceiptsToAddress, type:_EMAIL_ADDRESS_, ref:"attrs/"+ZaAccount.A_zimbraPrefReadReceiptsToAddress},
        {id:ZaAccount.A_zimbraPrefCalendarAutoAddInvites, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarAutoAddInvites, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPrefCalendarApptVisibility, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPrefCalendarApptVisibility, choices:ZaSettings.apptVisibilityChoices},
        //features
        {id:ZaAccount.A_zimbraFeatureManageZimlets, type:_COS_ENUM_, ref:"attrs/" + ZaAccount.A_zimbraFeatureManageZimlets, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureImportExportFolderEnabled, type:_COS_ENUM_, ref:"attrs/" + ZaAccount.A_zimbraFeatureImportExportFolderEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailPriorityEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaAccount.A_zimbraFeatureReadReceiptsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureReadReceiptsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureImapDataSourceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureImapDataSourceEnabled, choices:ZaModel.BOOLEAN_CHOICES},
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
        {id:ZaAccount.A_zimbraFeatureBriefcaseSpreadsheetEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureBriefcaseSpreadsheetEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureBriefcaseSlidesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureBriefcaseSlidesEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureBriefcaseDocsEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureBriefcaseDocsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureMailForwardingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailForwardingEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureSharingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureSharingEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        //{id:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, choices:ZaModel.BOOLEAN_CHOICES},
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
        //{id:ZaAccount.A2_quota, type:_MAILQUOTA_2_, ref:"attrs/"+ZaAccount.A_zimbraMailQuota},
        {id:ZaAccount.A2_autodisplayname, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A2_autoMailServer, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A2_autoCos, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A2_alias_selection_cache, type:_LIST_},
        {id:ZaAccount.A2_fwdAddr_selection_cache, type:_LIST_},
        {id:ZaAccount.A2_calFwdAddr_selection_cache, type:_LIST_},
        {id:ZaAccount.A2_fp_selection_cache, type:_LIST_},
        {id:ZaAccount.A_zimbraHideInGal, type:_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraHideInGal, choices:ZaModel.BOOLEAN_CHOICES},

        //security
        {id:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaAccount.A_zimbraPasswordLockoutDuration, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutDuration},
        {id:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_COS_NUMBER_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutMaxFailures, maxInclusive:2147483647, minInclusive:0},
        {id:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_COS_MLIFETIME_, ref:"attrs/"+ZaAccount.A_zimbraPasswordLockoutFailureLifetime},

        //interop
        {id:ZaAccount.A_zimbraFreebusyExchangeUserOrg, ref:"attrs/" +  ZaAccount.A_zimbraFreebusyExchangeUserOrg, type:_COS_STRING_},
        
        //datasources
        {id:ZaAccount.A2_ldap_ds, ref:ZaAccount.A2_ldap_ds, type:_OBJECT_, items:ZaDataSource.myXModel.items},
        {id:ZaAccount.A2_zimbra_ds, ref:ZaAccount.A2_zimbra_ds, type:_OBJECT_, items:ZaDataSource.myXModel.items},
        {id:ZaAccount.A2_datasources, ref:ZaAccount.A2_datasources, type:_LIST_, listItem:{type:_OBJECT_, items:ZaDataSource.myXModel.items}}
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
	var desc = ZaAccount.getAccountStatusMsg (val);
	return (desc == null) ? val : desc;
}

/* Translation of Account status values into screen names */     

ZaAccount.getAccountStatusMsg = function (status) {
    if (status == ZaAccount.ACCOUNT_STATUS_ACTIVE)  {
        return ZaMsg.accountStatus_active;
    }else if (status == ZaAccount.ACCOUNT_STATUS_CLOSED) {
        return ZaMsg.accountStatus_closed;
    }else if (status == ZaAccount.ACCOUNT_STATUS_LOCKED ) {
        return  ZaMsg.accountStatus_locked;
    }else if (status == ZaAccount.ACCOUNT_STATUS_LOCKOUT){
        return  ZaMsg.accountStatus_lockout;
    }else if (status == ZaAccount.ACCOUNT_STATUS_MAINTENANCE){
        return  ZaMsg.accountStatus_maintenance;
    }else if (status == ZaAccount.ACCOUNT_STATUS_PENDING) {
        return ZaMsg.accountStatus_pending ;
    }else {
        return "";
    }
}

ZaAccount.initMethod = function () {
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

ZaAccount.isAutoMailServer = function () {
	return (this.getInstanceValue(ZaAccount.A2_autoMailServer)=="FALSE" && !AjxUtil.isEmpty(ZaApp.getInstance().getServerListChoices().getChoices()) && !AjxUtil.isEmpty(ZaApp.getInstance().getServerListChoices().getChoices().values));
}


ZaAccount.setCosChanged = function (value, event, form) {
	var oldVal = this.getInstanceValue();
	if(oldVal == value)
		return;
			
	this.setInstanceValue(value);
	
	if(ZaItem.ID_PATTERN.test(value)) {
		this._defaultValues = ZaCos.getCosById(value);
	} else {
		this.setError(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[value]));
		var event = new DwtXFormsEvent(form, this, value);
		form.notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
		return;
	}
} 

ZaAccount.setDomainChanged =
function (value, event, form){
	//form.parent.setDirty(true);
	try {
		var instance = form.getInstance();
		this.setInstanceValue(value);
		var p = form.parent ;
        var oldDomainName = this.getOldDomainPart ();
        var newDomainName = ZaAccount.getDomain(value) ;
        var domainObj;
        try {
        	domainObj =  ZaDomain.getDomainByName(newDomainName) ;
        } catch (ex) {
        	if(ex.code == ZmCsfeException.SVC_PERM_DENIED) {
        		form.getModel().setInstanceValue(form.getInstance(),"setAttrs",[]);
        		form.getModel().setInstanceValue(form.getInstance(),ZaAccount.A2_errorMessage,AjxMessageFormat.format(ZaMsg.CANNOT_CREATE_ACCOUNTS_IN_THIS_DOMAIN,[newDomainName]));
        		return;
        	} else if(ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
        		return;
        	} else {
        		throw (ex);
        	}
        	
        }
        if ((newDomainName != oldDomainName)
				//set the right default cos at the account creation time
				|| (instance [ZaAccount.A_name].indexOf("@") == 0)) 
		{ //see if the cos needs to be updated accordingly
			try {
				ZaItem.prototype.loadNewObjectDefaults.call(instance,"name", newDomainName);
			} catch (ex) {
				if(ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
        			return value;
        		} else {
        			throw (ex);
        		}
			}
			
			if(instance.getAttrs[ZaAccount.A_zimbraAvailableSkin] || instance.getAttrs.all) {
				var skins = ZaApp.getInstance().getInstalledSkins();
				
				if(AjxUtil.isEmpty(skins)) {
					if(domainObj && domainObj.attrs && !AjxUtil.isEmpty(domainObj.attrs[ZaDomain.A_zimbraAvailableSkin])) {
						//if we cannot get all zimlets try getting them from domain
						skins = domainObj.attrs[ZaDomain.A_zimbraAvailableSkin];
					} else if(instance._defaultValues && instance._defaultValues.attrs && !AjxUtil.isEmpty(instance._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin])) {
						//if we cannot get all zimlets from domain either, just use whatever came in "defaults" which would be what the COS value is
						skins = instance._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin];
					} else {
						skins = [];
					}
				} else {
					if (AjxUtil.isString(skins))	 {
						skins = [skins];
					}
				}
				if(ZaNewAccountXWizard.themeChoices) {
					ZaNewAccountXWizard.themeChoices.setChoices(skins);
					ZaNewAccountXWizard.themeChoices.dirtyChoices();
				}		
				
				if(ZaAccountXFormView.themeChoices) {
					ZaAccountXFormView.themeChoices.setChoices(skins);
					ZaAccountXFormView.themeChoices.dirtyChoices();
				}		
				
			}	
	
			if(instance.getAttrs[ZaAccount.A_zimbraZimletAvailableZimlets] || instance.getAttrs.all) {
				//get sll Zimlets
				var allZimlets = ZaZimlet.getAll("extension");
		
				if(!AjxUtil.isEmpty(allZimlets) && allZimlets instanceof ZaItemList || allZimlets instanceof AjxVector)
					allZimlets = allZimlets.getArray();
		
				if(AjxUtil.isEmpty(allZimlets)) {
					
					if(domainObj && domainObj.attrs && !AjxUtil.isEmpty(domainObj.attrs[ZaDomain.A_zimbraZimletDomainAvailableZimlets])) {
						//if we cannot get all zimlets try getting them from domain
						allZimlets = domainObj.attrs[ZaDomain.A_zimbraZimletDomainAvailableZimlets];
					} else if(instance._defaultValues && instance._defaultValues.attrs && !AjxUtil.isEmpty(instance._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets])) {
						allZimlets = instance._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
					} else {
						allZimlets = [];
					}
					if(ZaNewAccountXWizard.zimletChoices) {
						ZaNewAccountXWizard.zimletChoices.setChoices(allZimlets);
						ZaNewAccountXWizard.zimletChoices.dirtyChoices();
					}
					
					if(ZaAccountXFormView.zimletChoices) {
						ZaAccountXFormView.zimletChoices.setChoices(allZimlets);
						ZaAccountXFormView.zimletChoices.dirtyChoices();
					}					
					
				} else {
					//convert objects to strings	
					var cnt = allZimlets.length;
					var _tmpZimlets = [];
					for(var i=0; i<cnt; i++) {
						var zimlet = allZimlets[i];
						_tmpZimlets.push(zimlet.name);
					}
					if(ZaNewAccountXWizard.zimletChoices) {
						ZaNewAccountXWizard.zimletChoices.setChoices(_tmpZimlets);
						ZaNewAccountXWizard.zimletChoices.dirtyChoices();
					}
					if(ZaAccountXFormView.zimletChoices) {
						ZaAccountXFormView.zimletChoices.setChoices(_tmpZimlets);
						ZaAccountXFormView.zimletChoices.dirtyChoices();
					}					
				}
			}				
			form.refresh();
		}
                   
        //if domain name is not changed, we don't want to update the account type output
        if  (oldDomainName !=  newDomainName){   
            if (domainObj && domainObj.attrs ){
                var maxDomainAccounts = domainObj.attrs[ZaDomain.A_domainMaxAccounts] ;
                var cosMaxAccounts = domainObj.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] ;
                if (maxDomainAccounts) {
                    maxDomainAccounts = parseInt (maxDomainAccounts);
                }
                if ((maxDomainAccounts && maxDomainAccounts > 0)
                        && (!cosMaxAccounts || cosMaxAccounts.length <= 0)) {
                    //only show domain left accounts when zimbraDomainMaxAccounts is set, but zimbraDomainCOSMaxAccounts is not set
                    var usedAccounts = domainObj.getUsedDomainAccounts(newDomainName );
                    form.getModel().setInstanceValue(form.getInstance(),     ZaAccount.A2_domainLeftAccounts,
                        AjxMessageFormat.format (ZaMsg.NAD_DomainAccountLimits, [maxDomainAccounts - usedAccounts, newDomainName]));
                }else if (cosMaxAccounts && cosMaxAccounts.length > 0){
                    //update the account type information
                    form.getModel().setInstanceValue(form.getInstance(),ZaAccount.A2_errorMessage,"");
                    form.getModel().setInstanceValue(form.getInstance(),ZaAccount.A2_accountTypes,domainObj.getAccountTypes ());
                    form.parent.updateAccountType();
                }else{
                    form.getModel().setInstanceValue(form.getInstance(),ZaAccount.A2_domainLeftAccounts,null);
                }
             }
        }

        if(form.parent.setDirty)  { //edit account view
			form.parent.setDirty(true);	
        }
        
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccount.setDomainChanged", null, false);	
	}
}

ZaAccount.generateDisplayName =
function (instance, firstName, lastName, initials) {
	var oldDisplayName = this.getInstanceValue(ZaAccount.A_displayname);
	var newDisplayname = "";
	if(firstName)
		newDisplayname = firstName;
	else
		newDisplayname = "";
		
	if(initials) {
		
		newDisplayname += " ";
		newDisplayname += initials;
		newDisplayname += ".";
	}
	if(lastName) {
		if(newDisplayname.length > 0)
			newDisplayname += " ";
			
	    newDisplayname += lastName;
	} 
	if(newDisplayname == oldDisplayName) {
		return false;
	} else {
		this.getModel().setInstanceValue(instance, ZaAccount.A_displayname, newDisplayname);
		return true;
	}
}

ZaAccount.setDefaultCos =
function (instance) {
	var defaultCos = ZaCos.getDefaultCos4Account(instance[ZaAccount.A_name]);
			
	if(defaultCos.id) {
		instance._defaultValues = defaultCos;
		instance.attrs[ZaAccount.A_COSId] = defaultCos.id;	
	}
}

ZaAccount.prototype.getCurrentCos =
function (){
	try {
		var cosId = this.attrs[ZaAccount.A_COSId] ;
		var currentCos ;
		currentCos = ZaCos.getCosById(this.attrs[ZaAccount.A_COSId]);
		if (!currentCos){
			currentCos = ZaCos.getDefaultCos4Account( this.name );
		}
		return currentCos ;
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAccount.prototype.getCurrentCos", null, false);
	}	
}

//the serverStr is in format yyyyMMddHHmmssZ to be converted to MM/dd/yyyy HH:mm:ss 
ZaAccount.getLastLoginTime =
function (serverStr) {
	if (serverStr) {
		return ZaItem.formatServerTime(serverStr);
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
		var n_tz = ZaModel.setUnrecoganizedChoiceValue(tz, ZaSettings.timeZoneChoices) ;
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


    //handle the unrecognized locale value
    var lv = this.attrs[ZaCos.A_zimbraPrefLocale] ;
    if (lv) {
        var n_lv = ZaModel.setUnrecoganizedChoiceValue(lv, ZaSettings.getLocaleChoices()) ;
		if (lv != n_lv) {
			this.attrs[ZaCos.A_zimbraPrefLocale] = n_lv ;
			warning += AjxMessageFormat.format(ZaMsg.WARNING_LOCALE_INVALID , [ lv, "account - \"" + this.name +"\""]);
		}
    }
    //display warnings about the if manageSpecialAttrs return value
	if (warning && warning.length > 0) { 
		ZaApp.getInstance().getCurrentController().popupMsgDialog (warning, true);
	}	
}
ZaAccount.isAdminAccount = function () {
	try {
		return (this.getInstanceValue(ZaAccount.A_zimbraIsAdminAccount)=="TRUE" || this.getInstanceValue(ZaAccount.A_zimbraIsDelegatedAdminAccount)=="TRUE");
	} catch (ex)	 {
		return false;
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
	try {
		var instance  = this.getInstance () ;
    	var gc   = ZaApp.getInstance().getGlobalConfig();
    	var sc =  ZaApp.getInstance().getServerByName(instance.attrs[ZaAccount.A_mailHost]);
    	var s_mailpurge = sc.attrs[ZaServer.A_zimbraMailPurgeSleepInterval] ;    //always end with [s,m,h,d]
    	var g_mailpurge = gc.attrs[ZaGlobalConfig.A_zimbraMailPurgeSleepInterval] ;
    	if (s_mailpurge === _UNDEFINED_ || s_mailpurge === null)  {
        	if (AjxEnv.hasFirebug) console.log("server setting A_zimbraMailPurgeSleepInterval is NOT set.")
        	if (g_mailpurge != null && ZaUtil.getLifeTimeInSeconds(g_mailpurge) == 0) {
            	return false ;
        	}
    	} else if (ZaUtil.getLifeTimeInSeconds(s_mailpurge) == 0){
        	return false ;
    	}
		
    	return true ;
	} catch (ex) {
		return true;
   	}
}

ZaAccount.isEmailRetentionPolicyDisabled = function () {
	return !ZaAccount.isEmailRetentionPolicyEnabled.call(this);
}

ZaAccount.getAccountTypeOutput = function (isNewAccount) {
    var form = this.getForm () ;
    var instance = form.getInstance () ;
    var acctTypes = instance[ZaAccount.A2_accountTypes] ;
    var out = [] ;
    if (acctTypes && acctTypes.length > 0) {
        /*
        var currentCos = ZaCos.getCosById(instance.attrs[ZaAccount.A_COSId], form.parent._app) ;
        var currentType = null ;
        if (currentCos)
            currentType = currentCos.id ;
        */
        var currentType = instance[ZaAccount.A2_currentAccountType] ;
        var domainName = ZaAccount.getDomain (instance.name) ;
        var domainObj =  ZaDomain.getDomainByName (domainName, form.parent._app);


        out.push("<table with=100%>");
        out.push("<colgroup><col width='200px' /><col width='200px' /><col width='200px' /></colgroup> ");
        out.push("<tbody>") ;

        var radioGroupName = "account_type_radio_group_" + Dwt.getNextId() ;
        //make sure CountAccountRequest is called to refresh the used accounts counts
        domainObj.updateUsedAccounts();  
        for (var i=0; i < acctTypes.length; i ++) {
            var cos = ZaCos.getCosById (acctTypes[i] , ZaApp.getInstance()) ;
            if (cos == null) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(
                        AjxMessageFormat.format(ZaMsg.ERROR_INVALID_ACCOUNT_TYPE, [acctTypes[i]]));
                return ;
            }
            var accountTypeDisplayValue = cos.attrs[ZaCos.A_description] ;
            if (!accountTypeDisplayValue)
                accountTypeDisplayValue = cos.name ;
            
            //3 columns a row
            if (i % 3 == 0) { //first col, need open <tr>
                out.push("<tr>") ;
            }
            out.push("<td>") ;

            //output the contents
            var usedAccounts = domainObj.getUsedAccounts(cos.name);
            var availableAccounts = domainObj.getAvailableAccounts(cos.name);

            out.push("<div>" +
                     "<label style='font-weight: bold;"
                    + ((availableAccounts > 0 || currentType == acctTypes[i] ) ? "" : "color: #686357;")
                    + "'>") ;
            //account type is disable when no accounts available
            out.push("<input type=radio name=" + radioGroupName + " value=" + acctTypes[i]
                    + ((availableAccounts > 0 || currentType == acctTypes[i] ) ?  (" onclick=\"ZaAccount.setAccountType.call("
                                    + this.getGlobalRef() + ", '" + acctTypes[i] +  "', event );\" ") : (" disabled "))
                    + ((currentType == acctTypes[i]) ? " checked " : "" )
                    + " />") ;
            out.push(accountTypeDisplayValue + "</label></div>") ;

            out.push("<div>" + AjxMessageFormat.format(ZaMsg.AccountsAvailable, [usedAccounts, availableAccounts])  + "</div> ") ;
            out.push("</td>")
       
            if ((i % 3 == 2) || (i + 1 == acctTypes.length)) { //last col, need close </td>
                out.push("</tr>") ;
            }
        }

        out.push("</tbody></table>") ;
    }

    return out.join("") ; 
}

ZaAccount.setAccountType = function (newType, ev) {
    //console.log ("Account Type Changed") ;
    var form = this.getForm() ;
    var instance = form.getInstance () ;

    var newCos = ZaCos.getCosById (newType) ;
    if (newCos.id != instance.attrs[ZaAccount.A_COSId])  {
        //change the account type
        if (instance.cos) instance._defaultValues = newCos ;
        instance.autoCos = "FALSE" ;
        instance.attrs[ZaAccount.A_COSId] = newCos.id ;
        form.parent._isCosChanged = true ;

        form.itemChanged(this, newType, ev);

        if(form.parent.setDirty)
			form.parent.setDirty(true);
    }
}

ZaAccount.isAccountTypeSet = function (tmpObj) {

    var cosId = tmpObj.attrs [ZaAccount.A_COSId] || tmpObj[ZaAccount.A2_currentAccountType];    
    if (!tmpObj.accountTypes  || tmpObj.accountTypes.length <= 0) {
        return  true ; //account type is not present, no need to check if it is set
    } else if (!cosId){
        return false ;
    }

    for (var i=0; i < tmpObj.accountTypes.length; i ++) {
        if (cosId == tmpObj.accountTypes[i] )
            return true ;
    }

    return false ;

}

ZaAccount.getCatchAllDomain = function (domainName) {
    return "@" + domainName ;
}

//find the catch all account for the domain
ZaAccount.getCatchAllAccount = function (domainName) {
	  /* var accounts = ZaAccount.getAllDomainAccounts (domainName) ;
	    for (var i=0; i < accounts.length; i++) {
	        if (accounts [i].attrs[ZaAccount.A_zimbraMailCatchAllAddress] == ZaAccount.getCatchAllDomain(domainName)) {
	            return accounts [i].id;
	        }
	   }
	 */
	  var searchParams = {
	     limit : 1 , //just need one
	     type : [ZaSearch.ACCOUNTS] ,
	     domain: domainName ,
	     applyCos:  0,
	     attrs: [ZaAccount.A_zimbraMailCatchAllAddress],
	     query:(["(",ZaAccount.A_zimbraMailCatchAllAddress,"=",ZaAccount.getCatchAllDomain(domainName),")"].join(""))
	  }	
	  
	  var resp =  ZaSearch.searchDirectory (searchParams).Body.SearchDirectoryResponse ;
	  var list = new ZaItemList(ZaAccount);
	  list.loadFromJS(resp);
	  var arr = list.getArray();
	  if(!AjxUtil.isEmpty(arr)) {
	  	if(arr[0]) {
	  		return arr[0];
	  	}
	  }  
	  return new ZaAccount(ZaApp.getInstance());
}

//++++++++++Modify CatchAll +++++++++++++++++++++++++
ZaAccount.modifyCatchAll =
function (accountId, domainName) {
    if (accountId == null | accountId.length <= 0) {
        return ;
    }
    var soapDoc = AjxSoapDoc.create("ModifyAccountRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", accountId);
    var catchAllDomain = "" ;
    if (domainName == null || domainName.length == 0) {
        //remove the catchAll value from the account
        catchAllDomain = "" ;
    }else if (domainName.indexOf("@") == -1) { //has no @
        catchAllDomain = ZaAccount.getCatchAllDomain (domainName) ;
    }else if (domainName.indexOf("@") != 0) {
        catchAllDomain = domainName.substring(domainName.lastIndexOf("@"))
    }else {
        catchAllDomain = domainName ;
    }
    var el = soapDoc.set("a", catchAllDomain) ;

    el.setAttribute("n", ZaAccount.A_zimbraMailCatchAllAddress) ;

    var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	command.invoke(params);
}
