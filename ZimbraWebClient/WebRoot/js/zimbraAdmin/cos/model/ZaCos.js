/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaCos
* Data model for zimbraCos object
* @ constructor ZaCos
* @param app reference to the application instance
* @author Greg Solovyev
**/
function ZaCos(app) {
	ZaItem.call(this, app, "ZaCos");
	this.attrs = new Object();
	this[ZaCos.A_zimbraMailHostPoolInternal] = new AjxVector();
	this.id = "";
	this.name="";
	this._app = app;	
	this.type = ZaItem.COS;
}
ZaItem.loadMethods["ZaCos"] = new Array();
ZaItem.modifyMethods["ZaCos"] = new Array();

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
ZaCos.A_zimbraMinPwdAge = "zimbraPasswordMinAge";
ZaCos.A_zimbraMaxPwdAge = "zimbraPasswordMaxAge";
ZaCos.A_zimbraEnforcePwdHistory ="zimbraPasswordEnforceHistory";
ZaCos.A_zimbraPasswordLocked = "zimbraPasswordLocked";
ZaCos.A_name = "cn";
ZaCos.A_description = "description";
ZaCos.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";

ZaCos.A_zimbraAuthTokenLifetime = "zimbraAuthTokenLifetime";
ZaCos.A_zimbraAdminAuthTokenLifetime = "zimbraAdminAuthTokenLifetime";
ZaCos.A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
ZaCos.A_zimbraContactMaxNumEntries = "zimbraContactMaxNumEntries";
ZaCos.A_zimbraMailMinPollingInterval = "zimbraMailMinPollingInterval";
ZaCos.A_zimbraMailMessageLifetime = "zimbraMailMessageLifetime";
ZaCos.A_zimbraMailTrashLifetime = "zimbraMailTrashLifetime";
ZaCos.A_zimbraMailSpamLifetime = "zimbraMailSpamLifetime";
ZaCos.A_zimbraMailHostPool = "zimbraMailHostPool";
ZaCos.A_zimbraAvailableSkin = "zimbraAvailableSkin";
ZaCos.A_zimbraZimletAvailableZimlets = "zimbraZimletAvailableZimlets";
//prefs
ZaCos.A_zimbraAllowAnyFromAddress = "zimbraAllowAnyFromAddress";
ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
ZaCos.A_zimbraPrefCalendarUseQuickAdd = "zimbraPrefCalendarUseQuickAdd";
ZaCos.A_zimbraPrefGroupMailBy = "zimbraPrefGroupMailBy";
ZaCos.A_zimbraPrefIncludeSpamInSearch = "zimbraPrefIncludeSpamInSearch";
ZaCos.A_zimbraPrefIncludeTrashInSearch = "zimbraPrefIncludeTrashInSearch";
ZaCos.A_zimbraPrefMailInitialSearch = "zimbraPrefMailInitialSearch";
ZaCos.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaCos.A_zimbraPrefMailPollingInterval = "zimbraPrefMailPollingInterval";
ZaCos.A_zimbraPrefUseKeyboardShortcuts = "zimbraPrefUseKeyboardShortcuts";
ZaCos.A_zimbraPrefSaveToSent = "zimbraPrefSaveToSent";
ZaCos.A_zimbraPrefContactsPerPage="zimbraPrefContactsPerPage";
ZaCos.A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
ZaCos.A_zimbraPrefForwardReplyInOriginalFormat = "zimbraPrefForwardReplyInOriginalFormat";
ZaCos.A_zimbraPrefAutoAddAddressEnabled = "zimbraPrefAutoAddAddressEnabled";
ZaCos.A_zimbraPrefMailItemsPerPage = "zimbraPrefMailItemsPerPage";
ZaCos.A_zimbraPrefComposeFormat = "zimbraPrefComposeFormat";
ZaCos.A_zimbraPrefMessageViewHtmlPreferred = "zimbraPrefMessageViewHtmlPreferred";
ZaCos.A_zimbraPrefShowSearchString = "zimbraPrefShowSearchString";
ZaCos.A_zimbraPrefMailSignatureStyle = "zimbraPrefMailSignatureStyle";
ZaCos.A_zimbraPrefUseTimeZoneListInCalendar = "zimbraPrefUseTimeZoneListInCalendar";
ZaCos.A_zimbraPrefImapSearchFoldersEnabled = "zimbraPrefImapSearchFoldersEnabled";
ZaCos.A_zimbraPrefCalendarApptReminderWarningTime = "zimbraPrefCalendarApptReminderWarningTime";
ZaCos.A_zimbraPrefSkin = "zimbraPrefSkin";
ZaCos.A_zimbraPrefGalAutoCompleteEnabled = "zimbraPrefGalAutoCompleteEnabled";

//features
ZaCos.A_zimbraFeaturePop3DataSourceEnabled = "zimbraFeaturePop3DataSourceEnabled";
ZaCos.A_zimbraFeatureIdentitiesEnabled = "zimbraFeatureIdentitiesEnabled";
ZaCos.A_zimbraFeatureContactsEnabled="zimbraFeatureContactsEnabled";
ZaCos.A_zimbraFeatureCalendarEnabled="zimbraFeatureCalendarEnabled";
ZaCos.A_zimbraFeatureTaggingEnabled="zimbraFeatureTaggingEnabled";
ZaCos.A_zimbraFeatureAdvancedSearchEnabled="zimbraFeatureAdvancedSearchEnabled";
ZaCos.A_zimbraFeatureSavedSearchesEnabled="zimbraFeatureSavedSearchesEnabled";
ZaCos.A_zimbraFeatureConversationsEnabled="zimbraFeatureConversationsEnabled";
ZaCos.A_zimbraFeatureChangePasswordEnabled="zimbraFeatureChangePasswordEnabled";
ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled="zimbraFeatureInitialSearchPreferenceEnabled";
ZaCos.A_zimbraFeatureFiltersEnabled="zimbraFeatureFiltersEnabled";
ZaCos.A_zimbraFeatureGalEnabled="zimbraFeatureGalEnabled";
ZaCos.A_zimbraFeatureMailForwardingEnabled = "zimbraFeatureMailForwardingEnabled";
ZaCos.A_zimbraFeatureSharingEnabled="zimbraFeatureSharingEnabled";
ZaCos.A_zimbraFeatureNotebookEnabled="zimbraFeatureNotebookEnabled"
ZaCos.A_zimbraImapEnabled = "zimbraImapEnabled";
ZaCos.A_zimbraPop3Enabled = "zimbraPop3Enabled";
ZaCos.A_zimbraFeatureHtmlComposeEnabled = "zimbraFeatureHtmlComposeEnabled";
ZaCos.A_zimbraFeatureGalAutoCompleteEnabled = "zimbraFeatureGalAutoCompleteEnabled";
ZaCos.A_zimbraFeatureSkinChangeEnabled = "zimbraFeatureSkinChangeEnabled";
ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled = "zimbraFeatureOutOfOfficeReplyEnabled";
ZaCos.A_zimbraFeatureNewMailNotificationEnabled = "zimbraFeatureNewMailNotificationEnabled";

//security
ZaCos.A_zimbraPasswordLockoutEnabled = "zimbraPasswordLockoutEnabled";
ZaCos.A_zimbraPasswordLockoutDuration = "zimbraPasswordLockoutDuration";
ZaCos.A_zimbraPasswordLockoutMaxFailures = "zimbraPasswordLockoutMaxFailures";
ZaCos.A_zimbraPasswordLockoutFailureLifetime = "zimbraPasswordLockoutFailureLifetime";

//internal attributes - do not send these to the server
ZaCos.A_zimbraMailAllServersInternal = "allserversarray";
ZaCos.A_zimbraMailHostPoolInternal = "hostpoolarray";
ZaCos.A_zimbraInstalledSkinPool = "zimbraInstalledSkinPool";
ZaCos.A_zimbraInstalledZimletPool = "zimbraInstalledZimletPool";

ZaCos.loadMethod =
function (by, val) {
	var soapDoc = AjxSoapDoc.create("GetCosRequest", "urn:zimbraAdmin", null);
	var el = soapDoc.set("cos", val);
	el.setAttribute("by", by);
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.GetCosResponse;
	this.initFromJS(resp.cos[0]);
}
ZaItem.loadMethods["ZaCos"].push(ZaCos.loadMethod);

ZaCos.prototype.refresh = 
function () {
	this.load("name", this.attrs[ZaCos.A_name]);
}

ZaCos.prototype.initFromJS =
function (obj) {
	ZaItem.prototype.initFromJS.call(this, obj);
	
	this[ZaCos.A_zimbraMailAllServersInternal] = new AjxVector();
	this[ZaCos.A_zimbraMailHostPoolInternal] = new AjxVector();
	
	var hostVector = new ZaItemVector();
	if(this.attrs[ZaCos.A_zimbraMailHostPool] instanceof Array) {	
		for(sname in this.attrs[ZaCos.A_zimbraMailHostPool]) {
			if(this._app.getServerMap()[this.attrs[ZaCos.A_zimbraMailHostPool][sname]]) {
				hostVector.add(this._app.getServerMap()[this.attrs[ZaCos.A_zimbraMailHostPool][sname]]);
			} else {
				var newServer = new ZaServer(this._app);
				newServer.load("id", this.attrs[ZaCos.A_zimbraMailHostPool][sname]);
				hostVector.add(newServer);
			}
		}
	} else if(typeof(this.attrs[ZaCos.A_zimbraMailHostPool]) == 'string'){
		if(this._app.getServerMap()[this.attrs[ZaCos.A_zimbraMailHostPool]]) {
			hostVector.add(this._app.getServerMap()[this.attrs[ZaCos.A_zimbraMailHostPool]]);
		} else {
			var newServer = new ZaServer(this._app);
			newServer.load("id", this.attrs[ZaCos.A_zimbraMailHostPool]);
			hostVector.add(newServer);
		}
	}
	this[ZaCos.A_zimbraMailHostPoolInternal] = hostVector;
	
	if (typeof ZaDomainAdmin == "function") {
		if (this.attrs[ZaCos.A_zimbraDomainAdminMaxMailQuota] >= 0) {
			this[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'TRUE';
		}else{
			this[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = 'FALSE' ;
		}
	}

}

/**
* public ZaCos.rename
* @param name - name for the new COS
* @param attrs - map of attributes
**/
ZaCos.prototype.create = 
function(name, mods) {
	var soapDoc = AjxSoapDoc.create("CreateCosRequest", "urn:zimbraAdmin", null);
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
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.CreateCosResponse;
	this.initFromJS(resp.cos[0]);
}

/**
* public ZaCos.rename
* @param newName - new name
**/
ZaCos.prototype.rename = 
function(newName) {
	var soapDoc = AjxSoapDoc.create("RenameCosRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	soapDoc.set("newName", newName);	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.RenameCosResponse;
	this.initFromJS(resp.cos[0]);	
}

/**
* public ZaCos.remove
* sends DeleteCosRequest SOAP command
**/
ZaCos.prototype.remove = 
function() {
	var soapDoc = AjxSoapDoc.create("DeleteCosRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
}
/**
* public ZaCos.modifyMethod
* @param mods - map of modified attributes
**/
ZaCos.modifyMethod = 
function (mods) {
	var soapDoc = AjxSoapDoc.create("ModifyCosRequest", "urn:zimbraAdmin", null);
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
				//set empty values
				var attr = soapDoc.set("a", "");
				attr.setAttribute("n", aname);
			}
		} else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.ModifyCosResponse;
	this.initFromJS(resp.cos[0])
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
function(app) {
	var soapDoc = AjxSoapDoc.create("GetAllCosRequest", "urn:zimbraAdmin", null);	
	var getAllCosCmd = new ZmCsfeCommand ();
	var params = new Object ();
	params.soapDoc = soapDoc ;
	var resp = getAllCosCmd.invoke(params).Body.GetAllCosResponse;
	var list = new ZaItemList(ZaCos, app);
	list.loadFromJS(resp);
	//list.sortByName();	
	
	return list;
}

ZaCos.getDefaultCos4Account =
function (accountName, cosListArray){
	if (!cosListArray) {
		throw (new AjxException ("No cos is available.")) ;
	}
	var defaultCos ;
	var defaultDomainCos ;
	var cnt = cosListArray.length;
	for(var i = 0; i < cnt; i++) {
		if(cosListArray[i].name == "default") {
			defaultCos = cosListArray[i];
		}
	}
	
	if (!accountName && cosListArray.length > 0) {
		return defaultCos; //default cos
	}
	
	var domainName = ZaAccount.getDomain(accountName);
	var domainCosId ;
	if (domainName && ((!this._domains) || (!this._domains[domainName]))){
		//send the GetDomainRequest
		var soapDoc = AjxSoapDoc.create("GetDomainRequest", "urn:zimbraAdmin", null);	
		var domainEl = soapDoc.set("domain", domainName);
		domainEl.setAttribute ("by", "name");
		var getDomainCommand = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var resp = getDomainCommand.invoke(params).Body.GetDomainResponse;
		var domain = new ZaItem ();
		domain.initFromJS (resp.domain[0]);
		domainCosId = domain.attrs[ZaDomain.A_domainDefaultCOSId] ;
		
		//keep the domain instance, so the future call is not needed.
		//it is used in new account and edit account
		if (this._domains) {
			this._domains[domainName] = domain ;
		}
	}else{
		domainCosId = this._domains[domainName].attrs[ZaDomain.A_domainDefaultCOSId] ;
	}	
		
	//when domainCosId doesn't exist, we always set default cos
	if (!domainCosId) {
		return defaultCos ;
	}else{
		return ZaCos.getCosById(cosListArray, domainCosId);
	}
}

ZaCos.getCosById = 
function (cosListArray, cosId) {
	var cnt = cosListArray.length;
	for(var i = 0; i < cnt; i++) {
		if(cosListArray[i].id == cosId) {
			return cosListArray[i];
		}
	}
}

ZaCos.myXModel = {
	items: [
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaCos.A_zimbraMailAllServersInternal, type:_OBJECT_, ref:ZaCos.A_zimbraMailAllServersInternal},
		{id:ZaCos.A_zimbraMailHostPoolInternal, type:_OBJECT_, ref:ZaCos.A_zimbraMailHostPoolInternal},
		{id:ZaCos.A_zimbraNotes, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraNotes},
		{id:ZaCos.A_zimbraMailQuota, type:_MAILQUOTA_, ref:"attrs."+ZaCos.A_zimbraMailQuota}, 
		{id:ZaCos.A_zimbraMinPwdLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMinPwdLength, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraMaxPwdLength, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMaxPwdLength, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraPasswordMinUpperCaseChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinUpperCaseChars, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraPasswordMinLowerCaseChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinLowerCaseChars, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraPasswordMinPunctuationChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinPunctuationChars, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraPasswordMinNumericChars, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordMinNumericChars, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraMinPwdAge, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMinPwdAge, maxInclusive:2147483647, minInclusive:0}, 
		{id:ZaCos.A_zimbraMaxPwdAge, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraMaxPwdAge, maxInclusive:2147483647, minInclusive:0},
		{id:ZaCos.A_zimbraEnforcePwdHistory, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraEnforcePwdHistory, maxInclusive:2147483647, minInclusive:0},
		{id:ZaCos.A_zimbraPasswordLocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPasswordLocked},
		{id:ZaCos.A_name, type:_STRING_, ref:"attrs/"+ZaCos.A_name, pattern:/^[A-Za-z0-9]+$/},
		{id:ZaCos.A_description, type:_STRING_, ref:"attrs/"+ZaCos.A_description},
		{id:ZaCos.A_zimbraAttachmentsBlocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAttachmentsBlocked},
		{id:ZaCos.A_zimbraAuthTokenLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraAuthTokenLifetime},
		{id:ZaCos.A_zimbraAdminAuthTokenLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraAdminAuthTokenLifetime},
		{id:ZaCos.A_zimbraMailIdleSessionTimeout, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailIdleSessionTimeout},
		{id:ZaCos.A_zimbraContactMaxNumEntries, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraContactMaxNumEntries, maxInclusive:2147483647, minInclusive:0},
		{id:ZaCos.A_zimbraMailMinPollingInterval, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailMinPollingInterval},
		{id:ZaCos.A_zimbraMailMessageLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailMessageLifetime},
		{id:ZaCos.A_zimbraMailTrashLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailTrashLifetime},
		{id:ZaCos.A_zimbraMailSpamLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraMailSpamLifetime},
//pref		
		{id:ZaCos.A_zimbraPrefGroupMailBy, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefGroupMailBy},
		{id:ZaCos.A_zimbraPrefIncludeSpamInSearch, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefIncludeSpamInSearch, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefIncludeTrashInSearch, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefIncludeTrashInSearch, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefMailInitialSearch, type:_STRING_, ref:"attrs/"+ZaCos.A_zimbraPrefMailInitialSearch},
		{id:ZaCos.A_zimbraPrefMailItemsPerPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefMailItemsPerPage},
		{id:ZaCos.A_zimbraPrefUseKeyboardShortcuts, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefUseKeyboardShortcuts, type:_ENUM_},
		{id:ZaCos.A_zimbraAllowAnyFromAddress, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAllowAnyFromAddress, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefSaveToSent, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefSaveToSent, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefContactsPerPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefContactsPerPage, choices:[10,25,50,100]},
		{id:ZaCos.A_zimbraPrefMailItemsPerPage, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPrefMailItemsPerPage, choices:[10,25,50,100]},
		{id:ZaCos.A_zimbraPrefComposeInNewWindow, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefComposeInNewWindow, type:_ENUM_},				
		{id:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefComposeFormat, choices:ZaModel.COMPOSE_FORMAT_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefComposeFormat, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefAutoAddAddressEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_ENUM_},								
		{id:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_ENUM_},				
		{id:ZaCos.A_zimbraPrefGroupMailBy, choices:ZaModel.GROUP_MAIL_BY_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefGroupMailBy, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_ENUM_},
		{id:ZaCos.A_zimbraPrefShowSearchString, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefShowSearchString, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefMailSignatureStyle, choices:ZaModel.SIGNATURE_STYLE_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefMailSignatureStyle, type:_ENUM_,defaultValue:"internet"},				
		{id:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefMailPollingInterval, ref:"attrs/"+ZaCos.A_zimbraPrefMailPollingInterval, type:_MLIFETIME_},		
		{id:ZaCos.A_zimbraPrefCalendarUseQuickAdd, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarUseQuickAdd, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_ENUM_},		
		{id:ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, choices:ZaModel.REMINDER_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, type:_ENUM_},				
		{id:ZaCos.A_zimbraPrefSkin, ref:"attrs/"+ZaCos.A_zimbraPrefSkin, type:_STRING_},	
		{id:ZaCos.A_zimbraPrefGalAutoCompleteEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPrefGalAutoCompleteEnabled, type:_ENUM_},							
		{id:ZaCos.A_zimbraAvailableSkin, ref:"attrs/" + ZaCos.A_zimbraAvailableSkin, type:_LIST_, dataType: _STRING_,outputType:_LIST_},
		{id:ZaCos.A_zimbraZimletAvailableZimlets, ref:"attrs/" + ZaCos.A_zimbraZimletAvailableZimlets, type:_LIST_, dataType: _STRING_,outputType:_LIST_},		
		{id:ZaCos.A_zimbraInstalledSkinPool, ref:ZaCos.A_zimbraInstalledSkinPool, type:_LIST_, dataType: _STRING_},		
		{id:ZaCos.A_zimbraInstalledZimletPool, ref:ZaCos.A_zimbraInstalledZimletPool, type:_LIST_, dataType: _STRING_},				
//features
		{id:ZaCos.A_zimbraFeaturePop3DataSourceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeaturePop3DataSourceEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureIdentitiesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureIdentitiesEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureContactsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureContactsEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureCalendarEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureCalendarEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureTaggingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureTaggingEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureSavedSearchesEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureConversationsEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureConversationsEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureChangePasswordEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_ENUM_},		
		{id:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureFiltersEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureFiltersEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureGalEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureGalEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureMailForwardingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureMailForwardingEnabled, type:_ENUM_},		
		{id:ZaCos.A_zimbraFeatureNotebookEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureNotebookEnabled, type:_ENUM_},				
		{id:ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, type:_ENUM_},				
		{id:ZaCos.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraImapEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureHtmlComposeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_ENUM_},		
		{id:ZaCos.A_zimbraImapEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraImapEnabled, type:_ENUM_},		
		{id:ZaCos.A_zimbraPop3Enabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraPop3Enabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureSharingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSharingEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_ENUM_},
		{id:ZaCos.A_zimbraFeatureNewMailNotificationEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureNewMailNotificationEnabled, type:_ENUM_},				
		{id:ZaCos.A_zimbraFeatureSkinChangeEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureSkinChangeEnabled, type:_ENUM_},
		
		//security
		{id:ZaCos.A_zimbraPasswordLockoutEnabled, type:_ENUM_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutEnabled, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaCos.A_zimbraPasswordLockoutDuration, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutDuration},		
		{id:ZaCos.A_zimbraPasswordLockoutMaxFailures, type:_NUMBER_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutMaxFailures, maxInclusive:2147483647, minInclusive:0},		
		{id:ZaCos.A_zimbraPasswordLockoutFailureLifetime, type:_MLIFETIME_, ref:"attrs/"+ZaCos.A_zimbraPasswordLockoutFailureLifetime}				
		
	]
};
