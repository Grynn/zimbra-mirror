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
* @class ZaCos
* Data model for zimbraCos object
* @ constructor ZaCos
* @param app reference to the application instance
* @author Greg Solovyev
**/
ZaCos = function() {
	ZaItem.call(this,"ZaCos");
	this._init();
}
ZaItem.loadMethods["ZaCos"] = new Array();
ZaItem.modifyMethods["ZaCos"] = new Array();
ZaItem.initMethods["ZaCos"] = new Array();

ZaCos.prototype = new ZaItem;
ZaCos.prototype.constructor = ZaCos;

ZaCos.NONE = "none";
//object attributes
ZaCos.A_zimbraNotes="zimbraNotes";
ZaCos.A_zimbraMailQuota="zimbraMailQuota";
ZaCos.A_zimbraMinPwdLength="zimbraPasswordMinLength";
ZaCos.A_zimbraMaxPwdLength="zimbraPasswordMaxLength";
ZaCos.A_zimbraPasswordMinUpperCaseChars = "zimbraPasswordMinUpperCaseChars";
ZaCos.A_zimbraPasswordMinLowerCaseChars = "zimbraPasswordMinLowerCaseChars";
ZaCos.A_zimbraPasswordMinPunctuationChars = "zimbraPasswordMinPunctuationChars";
ZaCos.A_zimbraPasswordMinNumericChars = "zimbraPasswordMinNumericChars";
ZaCos.A_zimbraPasswordMinDigitsOrPuncs = "zimbraPasswordMinDigitsOrPuncs";
ZaCos.A_zimbraMinPwdAge = "zimbraPasswordMinAge";
ZaCos.A_zimbraMaxPwdAge = "zimbraPasswordMaxAge";
ZaCos.A_zimbraEnforcePwdHistory ="zimbraPasswordEnforceHistory";
ZaCos.A_zimbraPasswordLocked = "zimbraPasswordLocked";
ZaCos.A_name = "cn";
ZaCos.A_description = "description";
ZaCos.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";
ZaCos.A_zimbraQuotaWarnPercent = "zimbraQuotaWarnPercent";
ZaCos.A_zimbraQuotaWarnInterval = "zimbraQuotaWarnInterval";
ZaCos.A_zimbraQuotaWarnMessage = "zimbraQuotaWarnMessage";

ZaCos.A_zimbraAdminAuthTokenLifetime = "zimbraAdminAuthTokenLifetime";
ZaCos.A_zimbraAuthTokenLifetime = "zimbraAuthTokenLifetime";
ZaCos.A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
ZaCos.A_zimbraContactMaxNumEntries = "zimbraContactMaxNumEntries";
ZaCos.A_zimbraMailMinPollingInterval = "zimbraMailMinPollingInterval";
ZaCos.A_zimbraMailMessageLifetime = "zimbraMailMessageLifetime";
ZaCos.A_zimbraMailTrashLifetime = "zimbraMailTrashLifetime";
ZaCos.A_zimbraMailSpamLifetime = "zimbraMailSpamLifetime";
ZaCos.A_zimbraMailHostPool = "zimbraMailHostPool";
ZaCos.A_zimbraAvailableSkin = "zimbraAvailableSkin";
ZaCos.A_zimbraZimletAvailableZimlets = "zimbraZimletAvailableZimlets";
ZaCos.A_zimbraMailForwardingAddressMaxLength = "zimbraMailForwardingAddressMaxLength";
ZaCos.A_zimbraMailForwardingAddressMaxNumAddrs = "zimbraMailForwardingAddressMaxNumAddrs";
ZaCos.A_zimbraPrefItemsPerVirtualPage="zimbraPrefItemsPerVirtualPage",

ZaCos.A_zimbraDataSourceMinPollingInterval = "zimbraDataSourceMinPollingInterval";
ZaCos.A_zimbraDataSourcePop3PollingInterval = "zimbraDataSourcePop3PollingInterval";
ZaCos.A_zimbraDataSourceImapPollingInterval = "zimbraDataSourceImapPollingInterval";
ZaCos.A_zimbraDataSourceCalendarPollingInterval = "zimbraDataSourceCalendarPollingInterval";
ZaCos.A_zimbraDataSourceRssPollingInterval = "zimbraDataSourceRssPollingInterval";
ZaCos.A_zimbraDataSourceCaldavPollingInterval = "zimbraDataSourceCaldavPollingInterval";


ZaCos.A_zimbraProxyAllowedDomains = "zimbraProxyAllowedDomains";
//prefs
ZaCos.A_zimbraPrefMandatorySpellCheckEnabled = "zimbraPrefMandatorySpellCheckEnabled";
ZaCos.A_zimbraPrefAppleIcalDelegationEnabled = "zimbraPrefAppleIcalDelegationEnabled";
ZaCos.A_zimbraPrefCalendarShowPastDueReminders = "zimbraPrefCalendarShowPastDueReminders";
ZaCos.A_zimbraPrefCalendarToasterEnabled = "zimbraPrefCalendarToasterEnabled";
ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf = "zimbraPrefCalendarAllowCancelEmailToSelf";
ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite = "zimbraPrefCalendarAllowPublishMethodInvite";
ZaCos.A_zimbraPrefCalendarAllowForwardedInvite = "zimbraPrefCalendarAllowForwardedInvite";
ZaCos.A_zimbraPrefCalendarReminderFlashTitle = "zimbraPrefCalendarReminderFlashTitle";
ZaCos.A_zimbraPrefCalendarReminderSoundsEnabled = "zimbraPrefCalendarReminderSoundsEnabled";
ZaCos.A_zimbraPrefCalendarSendInviteDeniedAutoReply = "zimbraPrefCalendarSendInviteDeniedAutoReply";
ZaCos.A_zimbraPrefCalendarAutoAddInvites = "zimbraPrefCalendarAutoAddInvites";
ZaCos.A_zimbraPrefCalendarApptVisibility = "zimbraPrefCalendarApptVisibility";
ZaCos.A_zimbraPrefCalendarNotifyDelegatedChanges = "zimbraPrefCalendarNotifyDelegatedChanges";
ZaCos.A_zimbraPrefCalendarInitialView = "zimbraPrefCalendarInitialView";
ZaCos.A_zimbraPrefClientType = "zimbraPrefClientType";
ZaCos.A_zimbraPrefTimeZoneId = "zimbraPrefTimeZoneId";
ZaCos.A_zimbraAllowAnyFromAddress = "zimbraAllowAnyFromAddress";
ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
ZaCos.A_zimbraPrefCalendarUseQuickAdd = "zimbraPrefCalendarUseQuickAdd";
ZaCos.A_zimbraPrefGroupMailBy = "zimbraPrefGroupMailBy";
ZaCos.A_zimbraPrefIncludeSpamInSearch = "zimbraPrefIncludeSpamInSearch";
ZaCos.A_zimbraPrefIncludeTrashInSearch = "zimbraPrefIncludeTrashInSearch";
ZaCos.A_zimbraPrefMailInitialSearch = "zimbraPrefMailInitialSearch";
ZaCos.A_zimbraMaxMailItemsPerPage = "zimbraMaxMailItemsPerPage";
ZaCos.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaCos.A_zimbraPrefMailPollingInterval = "zimbraPrefMailPollingInterval";
ZaCos.A_zimbraPrefAutoSaveDraftInterval = "zimbraPrefAutoSaveDraftInterval";
ZaCos.A_zimbraPrefMailFlashTitle = "zimbraPrefMailFlashTitle";
ZaCos.A_zimbraPrefMailFlashIcon = "zimbraPrefMailFlashIcon" ;
ZaCos.A_zimbraPrefMailSoundsEnabled = "zimbraPrefMailSoundsEnabled" ;
ZaCos.A_zimbraPrefMailToasterEnabled = "zimbraPrefMailToasterEnabled";
ZaCos.A_zimbraPrefMessageIdDedupingEnabled = "zimbraPrefMessageIdDedupingEnabled";
ZaCos.A_zimbraPrefUseKeyboardShortcuts = "zimbraPrefUseKeyboardShortcuts";
ZaCos.A_zimbraPrefSaveToSent = "zimbraPrefSaveToSent";
ZaCos.A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
ZaCos.A_zimbraPrefForwardReplyInOriginalFormat = "zimbraPrefForwardReplyInOriginalFormat";
ZaCos.A_zimbraPrefAutoAddAddressEnabled = "zimbraPrefAutoAddAddressEnabled";
ZaCos.A_zimbraPrefComposeFormat = "zimbraPrefComposeFormat";
ZaCos.A_zimbraPrefMessageViewHtmlPreferred = "zimbraPrefMessageViewHtmlPreferred";
ZaCos.A_zimbraPrefShowSearchString = "zimbraPrefShowSearchString";
//ZaCos.A_zimbraPrefMailSignatureStyle = "zimbraPrefMailSignatureStyle";
ZaCos.A_zimbraPrefUseTimeZoneListInCalendar = "zimbraPrefUseTimeZoneListInCalendar";
ZaCos.A_zimbraPrefImapSearchFoldersEnabled = "zimbraPrefImapSearchFoldersEnabled";
ZaCos.A_zimbraPrefCalendarApptReminderWarningTime = "zimbraPrefCalendarApptReminderWarningTime";
ZaCos.A_zimbraPrefSkin = "zimbraPrefSkin";
ZaCos.A_zimbraPrefGalAutoCompleteEnabled = "zimbraPrefGalAutoCompleteEnabled";
ZaCos.A_zimbraPrefWarnOnExit = "zimbraPrefWarnOnExit" ;
ZaCos.A_zimbraPrefShowSelectionCheckbox = "zimbraPrefShowSelectionCheckbox" ;
ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily = "zimbraPrefHtmlEditorDefaultFontFamily";
ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize = "zimbraPrefHtmlEditorDefaultFontSize" ;
ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor = "zimbraPrefHtmlEditorDefaultFontColor" ;
ZaCos.A_zimbraMailSignatureMaxLength = "zimbraMailSignatureMaxLength" ;
ZaCos.A_zimbraPrefDisplayExternalImages = "zimbraPrefDisplayExternalImages" ;
ZaCos.A_zimbraPrefOutOfOfficeCacheDuration = "zimbraPrefOutOfOfficeCacheDuration";
//ZaCos.A_zimbraPrefIMAutoLogin = "zimbraPrefIMAutoLogin";
ZaCos.A_zimbraPrefMailDefaultCharset = "zimbraPrefMailDefaultCharset";
ZaCos.A_zimbraPrefLocale = "zimbraPrefLocale" ;
ZaCos.A_zimbraJunkMessagesIndexingEnabled = "zimbraJunkMessagesIndexingEnabled";
ZaCos.A_zimbraPrefMailSendReadReceipts = "zimbraPrefMailSendReadReceipts";
ZaCos.A_zimbraPrefAdminConsoleWarnOnExit = "zimbraPrefAdminConsoleWarnOnExit" ;

//features
ZaCos.A_zimbraFeatureCrocodocEnabled = "zimbraFeatureCrocodocEnabled";
ZaCos.A_zimbraFeatureExportFolderEnabled = "zimbraFeatureExportFolderEnabled";
ZaCos.A_zimbraFeatureImportFolderEnabled = "zimbraFeatureImportFolderEnabled";
ZaCos.A_zimbraDumpsterEnabled = "zimbraDumpsterEnabled";
ZaCos.A_zimbraPrefCalendarFirstDayOfWeek = "zimbraPrefCalendarFirstDayOfWeek"; 
ZaCos.A_zimbraFeatureReadReceiptsEnabled = "zimbraFeatureReadReceiptsEnabled";
ZaCos.A_zimbraFeatureMailPriorityEnabled = "zimbraFeatureMailPriorityEnabled";
ZaCos.A_zimbraFeatureIMEnabled = "zimbraFeatureIMEnabled";
ZaCos.A_zimbraFeatureInstantNotify = "zimbraFeatureInstantNotify";
ZaCos.A_zimbraFeatureImapDataSourceEnabled = "zimbraFeatureImapDataSourceEnabled";
ZaCos.A_zimbraFeaturePop3DataSourceEnabled = "zimbraFeaturePop3DataSourceEnabled";
ZaCos.A_zimbraFeatureIdentitiesEnabled = "zimbraFeatureIdentitiesEnabled";
ZaCos.A_zimbraFeatureContactsEnabled="zimbraFeatureContactsEnabled";
ZaCos.A_zimbraFeatureCalendarEnabled="zimbraFeatureCalendarEnabled";
ZaCos.A_zimbraFeatureTasksEnabled = "zimbraFeatureTasksEnabled" ;
ZaCos.A_zimbraFeatureTaggingEnabled="zimbraFeatureTaggingEnabled";
ZaCos.A_zimbraFeaturePeopleSearchEnabled="zimbraFeaturePeopleSearchEnabled";
ZaCos.A_zimbraFeatureAdvancedSearchEnabled="zimbraFeatureAdvancedSearchEnabled";
ZaCos.A_zimbraFeatureSavedSearchesEnabled="zimbraFeatureSavedSearchesEnabled";
ZaCos.A_zimbraFeatureConversationsEnabled="zimbraFeatureConversationsEnabled";
ZaCos.A_zimbraFeatureChangePasswordEnabled="zimbraFeatureChangePasswordEnabled";
ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled="zimbraFeatureInitialSearchPreferenceEnabled";
ZaCos.A_zimbraFeatureFiltersEnabled="zimbraFeatureFiltersEnabled";
ZaCos.A_zimbraFeatureGalEnabled="zimbraFeatureGalEnabled";
ZaCos.A_zimbraFeatureMAPIConnectorEnabled = "zimbraFeatureMAPIConnectorEnabled";
ZaCos.A_zimbraFeatureMailForwardingEnabled = "zimbraFeatureMailForwardingEnabled";
ZaCos.A_zimbraFeatureMailSendLaterEnabled = "zimbraFeatureMailSendLaterEnabled";
//ZaCos.A_zimbraFeatureFreeBusyViewEnabled = "zimbraFeatureFreeBusyViewEnabled";
ZaCos.A_zimbraFeatureSharingEnabled="zimbraFeatureSharingEnabled";
ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled = "zimbraFeatureCalendarReminderDeviceEmailEnabled";
//ZaCos.A_zimbraFeatureNotebookEnabled="zimbraFeatureNotebookEnabled"
ZaCos.A_zimbraFeatureBriefcasesEnabled="zimbraFeatureBriefcasesEnabled";
ZaCos.A_zimbraFeatureExternalFeedbackEnabled = "zimbraFeatureExternalFeedbackEnabled";
ZaCos.A_zimbraFeatureBriefcaseDocsEnabled = "zimbraFeatureBriefcaseDocsEnabled";
ZaCos.A_zimbraImapEnabled = "zimbraImapEnabled";
ZaCos.A_zimbraPop3Enabled = "zimbraPop3Enabled";
ZaCos.A_zimbraFeatureHtmlComposeEnabled = "zimbraFeatureHtmlComposeEnabled";
ZaCos.A_zimbraFeatureGalAutoCompleteEnabled = "zimbraFeatureGalAutoCompleteEnabled";
ZaCos.A_zimbraFeatureManageZimlets = "zimbraFeatureManageZimlets";
ZaCos.A_zimbraFeatureSkinChangeEnabled = "zimbraFeatureSkinChangeEnabled";
ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled = "zimbraFeatureOutOfOfficeReplyEnabled";
ZaCos.A_zimbraFeatureNewMailNotificationEnabled = "zimbraFeatureNewMailNotificationEnabled";
ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled = "zimbraFeatureMailPollingIntervalPreferenceEnabled" ;
ZaCos.A_zimbraFeatureOptionsEnabled = "zimbraFeatureOptionsEnabled" ;
//ZaCos.A_zimbraFeatureShortcutAliasesEnabled = "zimbraFeatureShortcutAliasesEnabled" ;
ZaCos.A_zimbraFeatureMailEnabled = "zimbraFeatureMailEnabled";
ZaCos.A_zimbraFeatureGroupCalendarEnabled = "zimbraFeatureGroupCalendarEnabled";
ZaCos.A_zimbraFeatureFlaggingEnabled = "zimbraFeatureFlaggingEnabled" ;
ZaCos.A_zimbraFeatureManageSMIMECertificateEnabled = "zimbraFeatureManageSMIMECertificateEnabled";
ZaCos.A_zimbraFeatureSMIMEEnabled = "zimbraFeatureSMIMEEnabled";

//security
ZaCos.A_zimbraPasswordLockoutEnabled = "zimbraPasswordLockoutEnabled";
ZaCos.A_zimbraPasswordLockoutDuration = "zimbraPasswordLockoutDuration";
ZaCos.A_zimbraPasswordLockoutMaxFailures = "zimbraPasswordLockoutMaxFailures";
ZaCos.A_zimbraPasswordLockoutFailureLifetime = "zimbraPasswordLockoutFailureLifetime";

//file retension
ZaCos.A_zimbraNumFileVersionsToKeep = "zimbraNumFileVersionsToKeep";
ZaCos.A_zimbraUnaccessedFileLifetime = "zimbraUnaccessedFileLifetime";
ZaCos.A_zimbraFileTrashLifetime = "zimbraFileTrashLifetime";
ZaCos.A_zimbraFileSendExpirationWarning = "zimbraFileSendExpirationWarning";
ZaCos.A_zimbraFileExpirationWarningDays = "zimbraFileExpirationWarningDays";
// right
ZaCos.RIGHT_LIST_COS = "listCos";
ZaCos.RIGHT_LIST_ZIMLET = "listZimlet";
ZaCos.RIGHT_GET_ZIMLET = "getZimlet";
ZaCos.RIGHT_GET_HOSTNAME = "zimbraVirtualHostname";

ZaCos.A_zimbraFreebusyExchangeUserOrg = "zimbraFreebusyExchangeUserOrg" ;
ZaCos.cacheCounter = 0;
ZaCos.staticCosByNameCacheTable={};
ZaCos.staticCosByIdCacheTable = {};
ZaCos.putCosToCache = function(cos) {
	if(ZaCos.cacheCounter==100) {
		ZaCos.staticCosByNameCacheTable = {};
		ZaCos.staticCosByIdCacheTable = {};
		ZaCos.cacheCounter = 0;
	}
		
	if(!ZaCos.staticCosByNameCacheTable[cos.name] || !ZaCos.staticCosByIdCacheTable[cos.id]) {
		ZaCos.cacheCounter++;
		ZaCos.staticCosByNameCacheTable[cos.name] = cos;
		ZaCos.staticCosByIdCacheTable[cos.id] = cos;
	}
}

ZaCos.MAJOR_FEATURES_CHOICES = [
    {value: ZaCos.A_zimbraFeatureMailEnabled, label:ZaMsg.NAD_zimbraFeatureMailEnabled },
    {value: ZaCos.A_zimbraFeatureContactsEnabled, label:ZaMsg.NAD_FeatureContactsEnabled },
    {value: ZaCos.A_zimbraFeatureCalendarEnabled, label:ZaMsg.NAD_FeatureCalendarEnabled },
    {value:ZaCos.A_zimbraFeatureTasksEnabled, label:ZaMsg.NAD_FeatureTaskEnabled},
    //{value:ZaCos.A_zimbraFeatureNotebookEnabled,label:ZaMsg.NAD_zimbraFeatureNotebookEnabled},
    {value:ZaCos.A_zimbraFeatureBriefcasesEnabled,label:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled},
    {value:ZaCos.A_zimbraFeatureIMEnabled,label:ZaMsg.NAD_zimbraFeatureIMEnabled},
    {value:ZaCos.A_zimbraFeatureOptionsEnabled, label:ZaMsg.NAD_zimbraFeatureOptionsEnabled}
        
];

ZaCos.RENAME_COS_RIGHT = "renameCos";
ZaCos.CREATE_COS_RIGHT = "createCos";
ZaCos.DELETE_COS_RIGHT = "deleteCos";
//internal attributes - do not send these to the server
//ZaCos.A_zimbraMailAllServersInternal = "allserversarray";
//ZaCos.A_zimbraMailHostPoolInternal = "hostpoolarray";

ZaCos.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this.type = ZaItem.COS;
}
ZaItem.initMethods["ZaCos"].push(ZaCos.initMethod);


ZaCos.loadMethod =
function (by, val) {
	var soapDoc = AjxSoapDoc.create("GetCosRequest", ZaZimbraAdmin.URN, null);
	var el = soapDoc.set("cos", val);
	el.setAttribute("by", by);
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.getMethod().setAttribute("attrs", this.attrsToGet.join(","));
	}	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController(),
			busyMsg: ZaMsg.BUSY_GET_COS
		}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetCosResponse;
	this.initFromJS(resp.cos[0]);

	if(this.attrs[ZaAccount.A_zimbraPrefMailPollingInterval]) {
    	var poIntervalInS = ZaUtil.getLifeTimeInSeconds(this.attrs[ZaAccount.A_zimbraPrefMailPollingInterval]);
        if (poIntervalInS >= 1)
            this.attrs[ZaAccount.A_zimbraPrefMailPollingInterval] = poIntervalInS + "s";
    }

	if(this.attrs[ZaCos.A_zimbraProxyAllowedDomains] &&
       (!(this.attrs[ZaCos.A_zimbraProxyAllowedDomains] instanceof Array)) ) {
		this.attrs[ZaCos.A_zimbraProxyAllowedDomains] = [this.attrs[ZaCos.A_zimbraProxyAllowedDomains]];
	}
}
ZaItem.loadMethods["ZaCos"].push(ZaCos.loadMethod);

ZaCos.prototype.refresh = 
function () {
	this.load("name", this.name);
}

ZaCos.prototype.initFromJS =
function (obj) {
	ZaItem.prototype.initFromJS.call(this, obj);
	if(typeof(this.attrs[ZaCos.A_zimbraMailHostPool]) == 'string'){
		this.attrs[ZaCos.A_zimbraMailHostPool] = [this.attrs[ZaCos.A_zimbraMailHostPool]];
	}
}

/**
* public ZaCos.rename
* @param name - name for the new COS
* @param attrs - map of attributes
**/
ZaCos.prototype.create = 
function(name, mods) {
	var soapDoc = AjxSoapDoc.create("CreateCosRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("name", name);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) { //only set if not empty
				for(var ix=0; ix <cnt; ix++) {
					if(mods[aname][ix] instanceof String)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else if(mods[aname][ix] instanceof Object)
						var attr = soapDoc.set("a", mods[aname][ix].toString());
					else 
						var attr = soapDoc.set("a", mods[aname][ix]);
						
					attr.setAttribute("n", aname);
				}
			} 
		} else if(mods[aname] && (mods[aname].length || !isNaN(mods[aname]) )) {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}	
	}
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_CREATE_COS
		}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.CreateCosResponse;
	this.initFromJS(resp.cos[0]);
}

/**
* public ZaCos.rename
* @param newName - new name
**/
ZaCos.prototype.rename = 
function(newName) {
	var soapDoc = AjxSoapDoc.create("RenameCosRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_RENAME_COS
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.RenameCosResponse;
	this.initFromJS(resp.cos[0]);	
}

/**
* public ZaCos.remove
* sends DeleteCosRequest SOAP command
**/
ZaCos.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteCosRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_COS
	}
	ZaRequestMgr.invoke(params, reqMgrParams);
}
/**
* public ZaCos.modifyMethod
* @param mods - map of modified attributes
**/
ZaCos.modifyMethod = 
function (mods) {
	var gotSomething = false;
	
	var soapDoc = AjxSoapDoc.create("ModifyCosRequest", ZaZimbraAdmin.URN, null);
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
				//set empty values
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
		
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_COS
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyCosResponse;
	this.initFromJS(resp.cos[0]);
	ZaCos.putCosToCache(this);

}
ZaItem.modifyMethods["ZaCos"].push(ZaCos.modifyMethod);
/**
* Returns HTML for a tool tip for this cos.
*/
ZaCos.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;'>";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("COS");				
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addAttrRow(ZaItem.A_description, html, idx);
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaCos.getAll =
function() {
	var soapDoc = AjxSoapDoc.create("GetAllCosRequest", ZaZimbraAdmin.URN, null);	
	//var getAllCosCmd = new ZmCsfeCommand ();
	var params = new Object ();
	params.soapDoc = soapDoc ;
	var reqMgrParams = {
			controller: ZaApp.getInstance().getCurrentController(),
			busyMsg: ZaMsg.BUSY_GET_ALL_COS
		}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllCosResponse;
	var list = new ZaItemList(ZaCos);
	list.loadFromJS(resp);
	//list.sortByName();	
	
	return list;
}

ZaCos.getCosChoices = function () {
    if (ZaCos.cosChoices) {
        return ZaCos.cosChoices ;
    }else{
        ZaCos.cosChoices = new XFormChoices
    }
}





ZaCos.getDefaultCos4Account =
function (accountName){
	var defaultCos ;
	var defaultDomainCos ;

		
	if (!accountName) {
		return defaultCos; //default cos
	}
	
	var domainName = ZaAccount.getDomain(accountName);
	var domainCosId ;
	var domain;
    try {
        domain= ZaDomain.getDomainByName(domainName);
    } catch (ex) {
        domain = undefined;
    }

	if(domain) {
		domainCosId = domain.attrs[ZaDomain.A_domainDefaultCOSId] ;
		//when domainCosId doesn't exist, we always set default cos
		if (!domainCosId) {
			var cos = ZaCos.getCosByName("default");
			return cos ;
		} else{
			var cos = ZaCos.getCosById (domainCosId);
			if(!cos)
				cos = ZaCos.getCosByName("default");
			
		 	return cos ;
			//return cosList.getItemById(domainCosId);
		}
	} else {
		return null;
	}
	
}

ZaCos.getCosByName = 
function(cosName) {
	if(!cosName)
		return null;
		
	var cos = ZaCos.staticCosByNameCacheTable[cosName];
	if(!cos) {
		cos = new ZaCos();
		try {
			cos.load("name", cosName);
		} catch (ex) {
			if(ex.code == ZmCsfeException.NO_SUCH_COS) {
				return null;
			} else {
				throw (ex);
			}
		}
		ZaCos.putCosToCache(cos);
	}
	return cos;	
} 

ZaCos.getCosById = 
function (cosId) {
	if(!cosId)
		return null;
		
	var cos = ZaCos.staticCosByIdCacheTable[cosId];
	if(!cos) {
		cos = new ZaCos();
		try {
			cos.load("id", cosId);
		} catch (ex) {
			if(ex.code == ZmCsfeException.NO_SUCH_COS) {
				return null;
			} else {
				throw (ex);
			}
		}
		ZaCos.putCosToCache(cos);
	}
	return cos;
	
	/*var cnt = cosListArray.length;
	for(var i = 0; i < cnt; i++) {
		if(cosListArray[i].id == cosId) {
			return cosListArray[i];
		}
	}*/
}

ZaCos.myXModel = {
    items: [
        {id:"getAttrs",type:_LIST_},
    	{id:"setAttrs",type:_LIST_},
    	{id:"rights",type:_LIST_},
        {id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
        {id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp},
        {id:ZaCos.A_zimbraMailHostPool, ref:"attrs/" + ZaCos.A_zimbraMailHostPool, type:_LIST_, dataType: _STRING_,outputType:_LIST_},
        {id:ZaCos.A_zimbraNotes, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraNotes},
        {id:ZaCos.A_zimbraMailQuota, type:_MAILQUOTA_, ref:"attrs."+ZaCos.A_zimbraMailQuota},
        {id:ZaCos.A_zimbraMinPwdLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMinPwdLength, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraMaxPwdLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMaxPwdLength, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordMinUpperCaseChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinUpperCaseChars, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordMinLowerCaseChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinLowerCaseChars, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordMinPunctuationChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinPunctuationChars, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordMinNumericChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinNumericChars, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordMinDigitsOrPuncs, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinDigitsOrPuncs, maxInclusive:2147483647, minInclusive:0},        
        {id:ZaCos.A_zimbraMinPwdAge, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMinPwdAge, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraMaxPwdAge, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMaxPwdAge, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraEnforcePwdHistory, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraEnforcePwdHistory, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordLocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPasswordLocked},
        {id:ZaCos.A_name, type:_STRING_, ref:"attrs/"+ZaCos.A_name},
//        {id:ZaCos.A_description, type:_STRING_, ref:"attrs/"+ZaCos.A_description},
        ZaItem.descriptionModelItem ,
        {id:ZaCos.A_zimbraAttachmentsBlocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAttachmentsBlocked},
        {id:ZaCos.A_zimbraAuthTokenLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraAuthTokenLifetime},
        {id:ZaCos.A_zimbraAdminAuthTokenLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraAdminAuthTokenLifetime},
        {id:ZaCos.A_zimbraMailIdleSessionTimeout, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailIdleSessionTimeout},
        {id:ZaCos.A_zimbraContactMaxNumEntries, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraContactMaxNumEntries, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraMailForwardingAddressMaxLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMailForwardingAddressMaxLength, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraMailForwardingAddressMaxNumAddrs, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMailForwardingAddressMaxNumAddrs, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraMailMinPollingInterval, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailMinPollingInterval},
        {id:ZaCos.A_zimbraMailMessageLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailMessageLifetime},
        {id:ZaCos.A_zimbraMailTrashLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailTrashLifetime},
        {id:ZaCos.A_zimbraPrefItemsPerVirtualPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefItemsPerVirtualPage},
        {id:ZaCos.A_zimbraMailSpamLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailSpamLifetime},

        {id:ZaCos.A_zimbraQuotaWarnPercent, type:_NUMBER_, ref:"attrs/" + ZaCos.A_zimbraQuotaWarnPercent},
        {id:ZaCos.A_zimbraQuotaWarnInterval, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraQuotaWarnInterval},
        {id:ZaCos.A_zimbraQuotaWarnMessage, type:_STRING_, ref:"attrs/" + ZaCos.A_zimbraQuotaWarnMessage},

//pref
		{id:ZaCos.A_zimbraPrefMandatorySpellCheckEnabled, types:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefMandatorySpellCheckEnabled,  choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefAppleIcalDelegationEnabled, types:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefAppleIcalDelegationEnabled,  choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarShowPastDueReminders, types:_ENUM_, ref:"attrs/" + ZaCos.A_zimbraPrefCalendarShowPastDueReminders, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarToasterEnabled, type:_ENUM_, ref:"attrs/" + ZaCos.A_zimbraPrefCalendarToasterEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAllowCancelEmailToSelf, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAllowPublishMethodInvite,choices:ZaModel.BOOLEAN_CHOICES}, 
		{id:ZaCos.A_zimbraPrefCalendarAllowForwardedInvite, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAllowForwardedInvite, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarReminderFlashTitle, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarReminderFlashTitle, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarReminderSoundsEnabled, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarReminderSoundsEnabled, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarAutoAddInvites, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAutoAddInvites, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarNotifyDelegatedChanges, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarNotifyDelegatedChanges, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaCos.A_zimbraPrefCalendarApptVisibility, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarApptVisibility, choices:ZaSettings.apptVisibilityChoices},
		{id:ZaCos.A_zimbraPrefCalendarFirstDayOfWeek, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarFirstDayOfWeek, choices:ZaSettings.dayOfWeekChoices},
		{id:ZaCos.A_zimbraPrefCalendarInitialView, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarInitialView, choices:ZaSettings.calendarViewChoinces},
        {id:ZaCos.A_zimbraPrefClientType,type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefClientType, choices:ZaSettings.clientTypeChoices},
        {id:ZaCos.A_zimbraPrefTimeZoneId,type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefTimeZoneId, choices:ZaSettings.timeZoneChoices},
        {id:ZaCos.A_zimbraPrefGroupMailBy, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefGroupMailBy},
        {id:ZaCos.A_zimbraPrefIncludeSpamInSearch, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefIncludeSpamInSearch, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefIncludeTrashInSearch, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefIncludeTrashInSearch, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefMailInitialSearch, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefMailInitialSearch},
        {id:ZaCos.A_zimbraPrefUseKeyboardShortcuts, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefUseKeyboardShortcuts, type:_ENUM_},
        {id:ZaCos.A_zimbraAllowAnyFromAddress, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAllowAnyFromAddress, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefSaveToSent, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefSaveToSent, type:_ENUM_},
        {id:ZaCos.A_zimbraMaxMailItemsPerPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMaxMailItemsPerPage,maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPrefMailItemsPerPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefMailItemsPerPage, choices:[10,25,50,100]},
        {id:ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily, choices:ZaModel.FONT_FAMILY_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize, choices:ZaModel.FONT_SIZE_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor, ref:"attrs/"+ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor, type:_STRING_},
        {id:ZaCos.A_zimbraMailSignatureMaxLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMailSignatureMaxLength},
        {id:ZaCos.A_zimbraPrefMailToasterEnabled, type:_ENUM_, ref:"attrs/" + ZaCos.A_zimbraPrefMailToasterEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaCos.A_zimbraPrefMessageIdDedupingEnabled, type:_ENUM_, ref:"attrs/" + ZaCos.A_zimbraPrefMessageIdDedupingEnabled, choices:ZaModel.BOOLEAN_CHOICES},
	{id:ZaCos.A_zimbraPrefComposeInNewWindow, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefComposeInNewWindow, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefComposeFormat, choices:ZaModel.COMPOSE_FORMAT_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefComposeFormat, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefAutoAddAddressEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefGroupMailBy, choices:ZaModel.GROUP_MAIL_BY_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefGroupMailBy, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefShowSearchString, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefShowSearchString, type:_ENUM_},
        //{id:ZaCos.A_zimbraPrefMailSignatureStyle, choices:ZaModel.SIGNATURE_STYLE_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailSignatureStyle, type:_ENUM_,defaultValue:"internet"},
        {id:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefMailPollingInterval, ref:"attrs/"+ZaCos.A_zimbraPrefMailPollingInterval, type:_ENUM_, choices: ZaSettings.mailPollingIntervalChoices},
	{id:ZaCos.A_zimbraPrefAutoSaveDraftInterval, ref:"attrs/"+ZaCos.A_zimbraPrefAutoSaveDraftInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourceMinPollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourceMinPollingInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourcePop3PollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourcePop3PollingInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourceImapPollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourceImapPollingInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourceCalendarPollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourceCalendarPollingInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourceRssPollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourceRssPollingInterval, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraDataSourceCaldavPollingInterval, ref:"attrs/"+ZaCos.A_zimbraDataSourceCaldavPollingInterval, type:_MLIFETIME_},
	{id:ZaCos.A_zimbraProxyAllowedDomains, type: _LIST_, ref:"attrs/"+ZaCos.A_zimbraProxyAllowedDomains, listItem:{ type: _STRING_}}, 
        {id:ZaCos.A_zimbraPrefMailFlashIcon, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailFlashIcon, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefMailFlashTitle, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailFlashTitle, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefMailSoundsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailSoundsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefCalendarUseQuickAdd, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarUseQuickAdd, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, choices:ZaModel.REMINDER_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefSkin, ref:"attrs/"+ZaCos.A_zimbraPrefSkin, type:_STRING_},
        {id:ZaCos.A_zimbraPrefGalAutoCompleteEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefGalAutoCompleteEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefWarnOnExit, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefWarnOnExit, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefAdminConsoleWarnOnExit, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefAdminConsoleWarnOnExit, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefShowSelectionCheckbox, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefShowSelectionCheckbox, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefIMAutoLogin, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefIMAutoLogin, type:_ENUM_},
        {id:ZaCos.A_zimbraAvailableSkin, ref:"attrs/" + ZaCos.A_zimbraAvailableSkin, type:_LIST_, dataType: _STRING_,outputType:_LIST_},
        {id:ZaCos.A_zimbraZimletAvailableZimlets, ref:"attrs/" + ZaCos.A_zimbraZimletAvailableZimlets, type:_LIST_, dataType: _STRING_,outputType:_LIST_},
        {id:ZaCos.A_zimbraPrefDisplayExternalImages, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefDisplayExternalImages, type:_ENUM_},
        {id:ZaCos.A_zimbraPrefOutOfOfficeCacheDuration, ref:"attrs/"+ZaCos.A_zimbraPrefOutOfOfficeCacheDuration, type:_MLIFETIME_},
        {id:ZaCos.A_zimbraPrefMailDefaultCharset,type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefMailDefaultCharset, choices:ZaSettings.mailCharsetChoices},
        {id:ZaCos.A_zimbraPrefLocale, type: _STRING_, ref: "attrs/" + ZaCos.A_zimbraPrefLocale  },
        {id:ZaCos.A_zimbraJunkMessagesIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraJunkMessagesIndexingEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefMailSendReadReceipts, choices:ZaModel.SEND_READ_RECEPIT_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailSendReadReceipts, type:_ENUM_},
//features
		{id:ZaCos.A_zimbraFeatureCrocodocEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureCrocodocEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureExportFolderEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureExportFolderEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureImportFolderEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureImportFolderEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraDumpsterEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraDumpsterEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefCalendarSendInviteDeniedAutoReply, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarSendInviteDeniedAutoReply, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureReadReceiptsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureReadReceiptsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureMailPriorityEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailPriorityEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureImapDataSourceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureImapDataSourceEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeaturePop3DataSourceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeaturePop3DataSourceEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureIdentitiesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureIdentitiesEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureContactsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureContactsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureCalendarEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureTasksEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureTasksEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureIMEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaCos.A_zimbraFeatureIMEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureInstantNotify, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaCos.A_zimbraFeatureInstantNotify, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureTaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureTaggingEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeaturePeopleSearchEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeaturePeopleSearchEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureSavedSearchesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureConversationsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureConversationsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureChangePasswordEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureFiltersEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureFiltersEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureGalEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureGalEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureMAPIConnectorEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMAPIConnectorEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureManageSMIMECertificateEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureManageSMIMECertificateEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureSMIMEEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSMIMEEnabled, type:_ENUM_},
	{id:ZaCos.A_zimbraFeatureMailForwardingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailForwardingEnabled, type:_ENUM_},
	{id:ZaCos.A_zimbraFeatureMailSendLaterEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailSendLaterEnabled, type:_ENUM_},
        //{id:ZaCos.A_zimbraFeatureFreeBusyViewEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureFreeBusyViewEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureCalendarReminderDeviceEmailEnabled, type:_ENUM_},
	//{id:ZaCos.A_zimbraFeatureNotebookEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureNotebookEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureBriefcasesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureBriefcasesEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureExternalFeedbackEnabled , choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureExternalFeedbackEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureBriefcaseDocsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureBriefcaseDocsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraImapEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraImapEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraPop3Enabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPop3Enabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureSharingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSharingEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureNewMailNotificationEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureNewMailNotificationEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureOptionsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureOptionsEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureSkinChangeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSkinChangeEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureManageZimlets, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureManageZimlets, type:_ENUM_},
        //{id:ZaCos.A_zimbraFeatureShortcutAliasesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureShortcutAliasesEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureMailEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureGroupCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureGroupCalendarEnabled, type:_ENUM_},
        {id:ZaCos.A_zimbraFeatureFlaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureFlaggingEnabled, type:_ENUM_},
        //security
        {id:ZaCos.A_zimbraPasswordLockoutEnabled, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutEnabled, choices:ZaModel.BOOLEAN_CHOICES},
        {id:ZaCos.A_zimbraPasswordLockoutDuration, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutDuration},
        {id:ZaCos.A_zimbraPasswordLockoutMaxFailures, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutMaxFailures, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraPasswordLockoutFailureLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutFailureLifetime},
        //interop
        {id:ZaCos.A_zimbraFreebusyExchangeUserOrg ,type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraFreebusyExchangeUserOrg },
        
        
        //file retension
        {id:ZaCos.A_zimbraFileTrashLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraFileTrashLifetime},
        {id:ZaCos.A_zimbraUnaccessedFileLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraUnaccessedFileLifetime},
        {id:ZaCos.A_zimbraNumFileVersionsToKeep, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraNumFileVersionsToKeep, maxInclusive:2147483647, minInclusive:0},
        {id:ZaCos.A_zimbraFileSendExpirationWarning, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraFileSendExpirationWarning,
        	choices:["none", "owner", "all"]
        },
        {id:ZaCos.A_zimbraFileExpirationWarningDays, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraFileExpirationWarningDays}
    ]
};


ZaCos.prototype.manageSpecialAttrs =
function () {
	var warning = "" ;
	
	//handle the unrecognized timezone
	var tz = this.attrs[ZaCos.A_zimbraPrefTimeZoneId] ;
	if (tz) {
		var n_tz = ZaModel.setUnrecoganizedChoiceValue(tz, ZaSettings.timeZoneChoices) ;
		if (tz != n_tz) {
			this.attrs[ZaCos.A_zimbraPrefTimeZoneId] = n_tz ;
			warning += AjxMessageFormat.format(ZaMsg.WARNING_TIME_ZONE_INVALID , [ tz, "cos - \"" + this.name +"\""]);
		}
	}

	//handle the unrecognized mail charset
	var mdc = this.attrs[ZaCos.A_zimbraPrefMailDefaultCharset] ;
	if (mdc) {
		var n_mdc = ZaModel.setUnrecoganizedChoiceValue(mdc, ZaSettings.mailCharsetChoices) ;
		if (mdc != n_mdc) {
			this.attrs[ZaCos.A_zimbraPrefMailDefaultCharset] = n_mdc ;
			warning += AjxMessageFormat.format(ZaMsg.WARNING_CHARSET_INVALID , [ mdc, "cos - \"" + this.name +"\""]);
		}
	}

    //handle the unrecognized locale value
    var lv = this.attrs[ZaCos.A_zimbraPrefLocale] ;
    if (lv) {
        var n_lv = ZaModel.setUnrecoganizedChoiceValue(lv, ZaSettings.getLocaleChoices()) ;
		if (lv != n_lv) {
			this.attrs[ZaCos.A_zimbraPrefLocale] = n_lv ;
			warning += AjxMessageFormat.format(ZaMsg.WARNING_LOCALE_INVALID , [ lv, "cos - \"" + this.name +"\""]);
		}
    }

    //display warnings about the if manageSpecialAttrs return value
	if (warning && warning.length > 0) {
		ZaApp.getInstance().getCurrentController().popupMsgDialog (warning, true);
	}	
	
}
ZaCos.globalRights = {};
ZaCos.getEffectiveCosList = function(adminId) {

    var soapDoc = AjxSoapDoc.create("GetAllEffectiveRightsRequest", ZaZimbraAdmin.URN, null);
    var elGrantee = soapDoc.set("grantee", adminId);
    elGrantee.setAttribute("type", "usr");
    elGrantee.setAttribute("by", "id");

    var params = {};
    params.soapDoc = soapDoc;
    params.asyncMode = false;
    var reqMgrParams = {
        controller : ZaApp.getInstance().getCurrentController(),
        busyMsg : ZaMsg.BUSY_GET_EFFICIENT_COS_LIST
    }

    var cosNameList = [];
    try {
        var resp = ZaRequestMgr.invoke(params, reqMgrParams);
        if(!resp || resp.Body.GetAllEffectiveRightsResponse.Fault)
            return cosNameList;
        var targets = resp.Body.GetAllEffectiveRightsResponse.target;
        for(var i = 0; i < targets.length; i++) {
            if(targets[i].type != ZaItem.COS)
                continue;
            if(!targets[i].entries && !targets[i].all) continue;
            
            if(targets[i].all) { 
            	//we have access to all domains
            	if(targets[i].all.length && targets[i].all[0] && targets[i].all[0].right && targets[i].all[0].right.length) {
            		for(var j=0;j<targets[i].all[0].right.length;j++) {
            			ZaCos.globalRights[targets[i].all[0].right[j].n] = true;
            		}
            	}
            }
            
            for(var j = 0; j < targets[i].entries.length; j++) {
                var entry = targets[i].entries[j].entry;
                for(var k = 0; k < entry.length; k++)
                    cosNameList.push(entry[k].name);
            }
            break;
        }
        return cosNameList;
    } catch(ex) {
        return cosNameList;
    }

}

ZaCos.prototype.countAllAccounts = function() {
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("limit", "1");
	var query = "(" + ZaAccount.A_COSId + "=" + this.id + ")";

    if(this.name == "default") {
        query = "(|(!(" + ZaAccount.A_COSId + "=*))" + query + ")";
    }
    query = "(&" + query + "(!("+ ZaAccount.A_zimbraIsSystemAccount +"=TRUE)))" ;
	soapDoc.set("query", query);
    soapDoc.set("types", ZaSearch.ACCOUNTS);
	var command = new ZmCsfeCommand();
	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;
    cmdParams.noAuthToken = true;
    try {
	    var resp = command.invoke(cmdParams).Body.SearchDirectoryResponse;
        if(resp.searchTotal)
            return  resp.searchTotal;
        else return 0;
    } catch(ex) {
        throw (ex);
    }
    return 0;
}

ZaCos.prototype.countAllDomains = function() {
	var soapDoc = AjxSoapDoc.create("SearchDirectoryRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("limit", "1");
	var query = "(" + ZaDomain.A_domainDefaultCOSId + "=" + this.id + ")";

    if(this.name == "default") {
        query = "(|(!(" + ZaDomain.A_domainDefaultCOSId + "=*))" + query + ")";
    }
	soapDoc.set("query", query);
    soapDoc.set("types", ZaSearch.DOMAINS);
	var command = new ZmCsfeCommand();
	var cmdParams = new Object();
	cmdParams.soapDoc = soapDoc;
    cmdParams.noAuthToken = true;
    try {
	    var resp = command.invoke(cmdParams).Body.SearchDirectoryResponse;
        if(resp.searchTotal)
            return  resp.searchTotal;
        else return 0;
    } catch(ex) {
        throw (ex);
    }
    return 0;
}


ZaCos.checkValues = function(tmpObj){
   if(tmpObj.attrs == null) {
		//show error msg
        ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_UNKNOWN);
	    return false;
	}

	//name
	if(ZaItem.hasWritePermission(ZaCos.A_name,tmpObj)) {
		 if((tmpObj.attrs[ZaCos.A_name] == null || tmpObj.attrs[ZaCos.A_name].length < 1 )) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_NAME_REQUIRED);
			return false;
		} else {
			tmpObj.name = tmpObj.attrs[ZaCos.A_name];
		}

		if(tmpObj.name.length > 256 || tmpObj.attrs[ZaCos.A_name].length > 256) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_COS_NAME_TOOLONG);
			return false;
		}
	}
	/**
	* check values
	**/

	//if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars])) {
   	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordMinUpperCaseChars,tmpObj)) {
	   if (tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars])) {
			//show error msg
           ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinUpperCaseChars]));
			return false;
		}
	}
   	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordMinLowerCaseChars,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinLowerCaseChars]));
			return false;
		}
   	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordMinPunctuationChars,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinPunctuationChars]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordMinNumericChars,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinNumericChars]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordMinDigitsOrPuncs,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinDigitsOrPuncs] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinDigitsOrPuncs] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinDigitsOrPuncs])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordMinDigitsOrPuncs]));
			return false;
		}
	}

	if(tmpObj.attrs[ZaCos.A_zimbraMailQuota] != null && tmpObj.attrs[ZaCos.A_zimbraMailQuota] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMailQuota])) {
		//show error msg
        ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailQuota]));
		return false;
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraContactMaxNumEntries,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries] != null && tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraContactMaxNumEntries]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMinPwdLength,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] != null && tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMinPwdLength]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMaxPwdLength,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] != null && tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMaxPwdLength]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMaxPwdLength,tmpObj) && ZaItem.hasWritePermission(ZaCos.A_zimbraMinPwdLength,tmpObj)) {
		if (tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] != null &&  tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] != null) {
			if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) > 0) {
				//show error msg
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDLENGTH);
				return false;
			}
		}
	}
	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMinPwdAge,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_passMinAge]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMaxPwdAge,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_passMaxAge]));
			return false;
		}
	}
	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMinPwdAge,tmpObj) && ZaItem.hasWritePermission(ZaCos.A_zimbraMaxPwdAge,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge] != null && tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] != null ){
			if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) > 0) {
				//show error msg
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MAX_MIN_PWDAGE);
				return false;
			}
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraAuthTokenLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraAuthTokenLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraAuthTokenLifetime])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraAuthTokenLifetime]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPrefOutOfOfficeCacheDuration,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPrefOutOfOfficeCacheDuration] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPrefOutOfOfficeCacheDuration])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefOutOfOfficeCacheDuration]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMailIdleSessionTimeout,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMailIdleSessionTimeout] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailIdleSessionTimeout])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailIdleSessionTimeout]));
			return false;
		}
	}

        if(ZaItem.hasWritePermission(ZaCos.A_zimbraPrefAutoSaveDraftInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraPrefAutoSaveDraftInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPrefAutoSaveDraftInterval])) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefAutoSaveDraftInterval]));
                        return false;
                }
        }

        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourceMinPollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval])) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourceMinPollingInterval]));
                        return false;
                }
        }
        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourcePop3PollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourcePop3PollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourcePop3PollingInterval])) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourcePop3PollingInterval]));
                        return false;
                }
		var min_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] ;
		var p_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourcePop3PollingInterval] ;
                if(p_dataInterval != null && min_dataInterval != null) {
                        if (ZaUtil.getLifeTimeInSeconds(p_dataInterval) < ZaUtil.getLifeTimeInSeconds(min_dataInterval)){
                                ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_dataInterval) ;
                                return false ;
                        }
                }

        }
        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourceImapPollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourceImapPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourceImapPollingInterval])) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourceImapPollingInterval]));
                        return false;
                }
                var min_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] ;
                var p_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceImapPollingInterval] ;
                if(p_dataInterval != null && min_dataInterval != null) {
                        if (ZaUtil.getLifeTimeInSeconds(p_dataInterval) < ZaUtil.getLifeTimeInSeconds(min_dataInterval)){
                                ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_dataInterval) ;
                                return false ;
                        }
                }
        }
        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourceCalendarPollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourceCalendarPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourceCalendarPollingInterval])) {
                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourceCalendarPollingInterval]));
                        return false;
                }
                var min_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] ;
                var p_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceCalendarPollingInterval] ;
                if(p_dataInterval != null && min_dataInterval != null) {
			if (ZaUtil.getLifeTimeInSeconds(p_dataInterval) < ZaUtil.getLifeTimeInSeconds(min_dataInterval)){
				ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_dataInterval) ;
                                return false ;
                        }
                }
        }
        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourceRssPollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourceRssPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourceRssPollingInterval])) {

                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraDataSourceRssPollingInterval]));
                        return false;
                }
                var min_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] ;
                var p_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceRssPollingInterval] ;
                if(p_dataInterval != null && min_dataInterval != null) {
                        if (ZaUtil.getLifeTimeInSeconds(p_dataInterval) < ZaUtil.getLifeTimeInSeconds(min_dataInterval)){
                                ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_dataInterval) ;                                        return false ;
                        }
		}
        }
        if(ZaItem.hasWritePermission(ZaCos.A_zimbraDataSourceCaldavPollingInterval,tmpObj)) {
                if(tmpObj.attrs[ZaCos.A_zimbraDataSourceCaldavPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraDataSourceCaldavPollingInterval])) {

                    ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zzimbraDataSourceCaldavPollingInterval]));
                        return false;
                }
                var min_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceMinPollingInterval] ;
                var p_dataInterval = tmpObj.attrs[ZaCos.A_zimbraDataSourceCaldavPollingInterval] ;
                if(p_dataInterval != null && min_dataInterval != null) {
			if (ZaUtil.getLifeTimeInSeconds(p_dataInterval) < ZaUtil.getLifeTimeInSeconds(min_dataInterval)){
                                ZaApp.getInstance().getCurrentController().popupErrorDialog (ZaMsg.tt_mailPollingIntervalError + min_dataInterval) ;
                                return false ;
                        }
                }
        }

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPrefMailPollingInterval,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPrefMailPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPrefMailPollingInterval])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPrefMailPollingInterval]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPrefMailPollingInterval,tmpObj)) {
		var n_minPollingInterval = tmpObj.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;

		if(n_minPollingInterval != null && !AjxUtil.isLifeTime(n_minPollingInterval)) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailMinPollingInterval]));
			return false;
		}

		//var o_minPollingInterval = this.currentObject.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
        var o_minPollingInterval = tmpObj.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
		if (o_minPollingInterval != null && ZaUtil.getLifeTimeInSeconds (n_minPollingInterval)
			 > ZaUtil.getLifeTimeInSeconds(o_minPollingInterval)){
			 ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format (ZaMsg.tt_minPollingIntervalWarning, [o_minPollingInterval, n_minPollingInterval]),  true);
		}
	}


	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMailMessageLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime] != null) {

			if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime])) {
				//show error msg
                ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailMessageLifetime]));
				return false;
			}
			var itestVal = parseInt(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime].substr(0, tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime].length-1));
			if(itestVal > 0 && itestVal < 31) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_MESSAGE_LIFETIME_BELOW_31);
				return false;
			}
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMailTrashLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMailTrashLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailTrashLifetime])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailTrashLifetime]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMailSpamLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraMailSpamLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailSpamLifetime])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraMailSpamLifetime]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordLockoutDuration,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutDuration] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutDuration])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordLockoutDuration]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraPasswordLockoutFailureLifetime,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutFailureLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutFailureLifetime])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraPasswordLockoutFailureLifetime]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraEnforcePwdHistory,tmpObj)) {
		if(tmpObj.attrs[ZaCos.A_zimbraEnforcePwdHistory] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraEnforcePwdHistory])) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.MSG_zimbraEnforcePwdHistory]));
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraMaxMailItemsPerPage,tmpObj) && ZaItem.hasWritePermission(ZaCos.A_zimbraPrefMailItemsPerPage,tmpObj)) {
		var maxItemsPerPage;
		if(tmpObj.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage] != null) {
			maxItemsPerPage = parseInt (tmpObj.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage]);
		} else {
			maxItemsPerPage = parseInt ( tmpObj._defaultValues.attrs[ZaAccount.A_zimbraMaxMailItemsPerPage]);
		}

		var prefItemsPerPage;
		if(tmpObj.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage] != null) {
			prefItemsPerPage = parseInt (tmpObj.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage]);
		} else {
			prefItemsPerPage = parseInt ( tmpObj._defaultValues.attrs[ZaAccount.A_zimbraPrefMailItemsPerPage]);
		}

		if(maxItemsPerPage < prefItemsPerPage) {
			//show error msg
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ITEMS_PER_PAGE_OVER_MAX);
			return false;
		}
	}

	if(ZaItem.hasWritePermission(ZaCos.A_zimbraAvailableSkin,tmpObj)) {
		//check that current theme is part of selected themes
		if(tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] !=null && tmpObj.attrs[ZaCos.A_zimbraAvailableSkin].length > 0 && tmpObj.attrs[ZaCos.A_zimbraPrefSkin] ) {
			var arr = tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] instanceof Array ? tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] : [tmpObj.attrs[ZaCos.A_zimbraAvailableSkin]];
			var cnt = arr.length;
			var found=false;
			for(var i=0; i < cnt; i++) {
				if(arr[i]==tmpObj.attrs[ZaCos.A_zimbraPrefSkin]) {
					found=true;
					break;
				}
			}
			if(!found) {
				//show error msg
                ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format (ZaMsg.COS_WarningCurrentThemeNotAvail, [tmpObj.attrs[ZaCos.A_zimbraPrefSkin], tmpObj.attrs[ZaCos.A_zimbraPrefSkin]]));
				return false;
			}
		}
	}
    return true;
}
