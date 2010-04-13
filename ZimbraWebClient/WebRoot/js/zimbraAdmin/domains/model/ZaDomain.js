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
* @class ZaDomain
* @ constructor ZaDomain
* @param app reference to the application instance
* Data model for zimbraDomain object
* @author Greg Solovyev
**/

ZaDomain = function() {
	ZaItem.call(this,  "ZaDomain");
	this.attrs = new Object();
	this.id = "";
	this.name="";
	this.type=ZaItem.DOMAIN;

	//default attributes
	this.attrs[ZaDomain.A_zimbraGalMode] = ZaDomain.GAL_Mode_internal;
    this.attrs[ZaDomain.A_AuthMech] = ZaDomain.AuthMech_zimbra;
	this.attrs[ZaDomain.A_GALSyncUseGALSearch]='TRUE';
	this.notebookAcls = {};
	this[ZaDomain.A_NotebookTemplateFolder] = "Template";
	this[ZaDomain.A_NotebookTemplateDir] = "/opt/zimbra/wiki/Template";
	this.notebookAcls[ZaDomain.A_NotebookAllACLs] = {r:0,w:0,i:0,d:0,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookPublicACLs] = {r:0,w:0,i:0,d:0,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookDomainACLs] = {r:1,w:1,i:1,d:1,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookUserACLs] = [/*{name:"", acl:{r:0,w:0,i:0,d:0,a:0,x:0}}*/];
	this.notebookAcls[ZaDomain.A_NotebookGroupACLs] = [/*{name:"", acl:{r:0,w:0,i:0,d:0,a:0,x:0}, 
			toString:function () {
				return [this.name,this.acl[r],this.acl[w],this.acl[i],this.acl[d],this.acl[a],this.acl[x]].join();
			}
		}*/];

    this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts ] = [];
    this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts ] = [];
}
ZaDomain.DEF_WIKI_ACC = "domain_wiki";
ZaDomain.WIKI_FOLDER_ID = "12";
ZaDomain.RESULTSPERPAGE = ZaSettings.RESULTSPERPAGE; 
ZaDomain.MAXSEARCHRESULTS = ZaSettings.MAXSEARCHRESULTS;
ZaDomain.prototype = new ZaItem;
ZaDomain.prototype.constructor = ZaDomain;
ZaDomain.ACLLabels = {r:ZaMsg.ACL_R, w:ZaMsg.ACL_W, i:ZaMsg.ACL_I, a:ZaMsg.ACL_A, d:ZaMsg.ACL_D, x:ZaMsg.ACL_X};
ZaItem.loadMethods["ZaDomain"] = new Array();
ZaItem.initMethods["ZaDomain"] = new Array();
ZaItem.modifyMethods["ZaDomain"] = new Array();
ZaItem.createMethods["ZaDomain"] = new Array();

ZaDomain.DOMAIN_STATUS_ACTIVE = "active";
ZaDomain.DOMAIN_STATUS_MAINTENANCE = "maintenance";
ZaDomain.DOMAIN_STATUS_LOCKED = "locked";
ZaDomain.DOMAIN_STATUS_CLOSED = "closed";
ZaDomain.DOMAIN_STATUS_SUSPENDED = "suspended";
ZaDomain.DOMAIN_STATUS_SHUTDOWN = "shutdown";

ZaDomain._domainStatus = 
function(val) {
	var desc = ZaDomain._DOMAIN_STATUS[val];
	return (desc == null) ? val : desc;
}


/* Translation of Domain status values into screen names */
ZaDomain._DOMAIN_STATUS = new Object ();
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_ACTIVE] = ZaMsg.domainStatus_active;
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_CLOSED] = ZaMsg.domainStatus_closed;
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_LOCKED] = ZaMsg.domainStatus_locked;
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_SUSPENDED] = ZaMsg.domainStatus_suspended;
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_MAINTENANCE] = ZaMsg.domainStatus_maintenance;
ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_SHUTDOWN] = ZaMsg.domainStatus_shutdown;

ZaDomain.domainStatusChoices = [
	{value:ZaDomain.DOMAIN_STATUS_ACTIVE, label:ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_ACTIVE]}, 
	{value:ZaDomain.DOMAIN_STATUS_CLOSED, label:ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_CLOSED]},
	{value:ZaDomain.DOMAIN_STATUS_LOCKED, label: ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_LOCKED]},
	{value:ZaDomain.DOMAIN_STATUS_MAINTENANCE, label:ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_MAINTENANCE]},
	{value:ZaDomain.DOMAIN_STATUS_SUSPENDED, label:ZaDomain._DOMAIN_STATUS[ZaDomain.DOMAIN_STATUS_SUSPENDED]}
];	


//attribute name constants, this values are taken from zimbra.schema
ZaDomain.A_description = "description";
ZaDomain.A_notes = "zimbraNotes";
ZaDomain.A_domainName = "zimbraDomainName";
ZaDomain.A_domainType = "zimbraDomainType" ;
ZaDomain.A_domainDefaultCOSId = "zimbraDomainDefaultCOSId";
ZaDomain.A_zimbraDomainStatus = "zimbraDomainStatus";
ZaDomain.A_zimbraPublicServiceHostname = "zimbraPublicServiceHostname";
ZaDomain.A_zimbraPublicServicePort = "zimbraPublicServicePort";
ZaDomain.A_zimbraPublicServiceProtocol = "zimbraPublicServiceProtocol";
ZaDomain.A_zimbraDNSCheckHostname = "zimbraDNSCheckHostname";

//GAL search
ZaDomain.A_zimbraGalMaxResults = "zimbraGalMaxResults";
ZaDomain.A_zimbraGalMode = "zimbraGalMode";
ZaDomain.A_GalLdapURL = "zimbraGalLdapURL";
ZaDomain.A_GalLdapSearchBase = "zimbraGalLdapSearchBase";
ZaDomain.A_GalLdapBindDn = "zimbraGalLdapBindDn";
ZaDomain.A_GalLdapBindPassword = "zimbraGalLdapBindPassword";
ZaDomain.A_GalLdapBindPasswordConfirm = "zimbraGalLdapBindPasswordConfirm";
ZaDomain.A_GalLdapFilter = "zimbraGalLdapFilter";
ZaDomain.A_zimbraGalAutoCompleteLdapFilter = "zimbraGalAutoCompleteLdapFilter";
//GAL Sync
ZaDomain.A_zimbraGalSyncLdapURL = "zimbraGalSyncLdapURL";
ZaDomain.A_zimbraGalSyncLdapSearchBase="zimbraGalSyncLdapSearchBase";
ZaDomain.A_zimbraGalSyncLdapFilter="zimbraGalSyncLdapFilter";
ZaDomain.A_zimbraGalSyncLdapAuthMech="zimbraGalSyncLdapAuthMech";
ZaDomain.A_zimbraGalSyncLdapBindDn="zimbraGalSyncLdapBindDn";
ZaDomain.A_zimbraGalSyncLdapBindPassword="zimbraGalSyncLdapBindPassword";

//GAL Sync accounts
ZaDomain.A_zimbraGalAccountId = "zimbraGalAccountId";
 
//Auth
ZaDomain.A_AuthMech = "zimbraAuthMech";
ZaDomain.A_AuthLdapURL = "zimbraAuthLdapURL";
ZaDomain.A_AuthLdapUserDn = "zimbraAuthLdapBindDn";
ZaDomain.A_AuthLdapSearchBase = "zimbraAuthLdapSearchBase";
ZaDomain.A_AuthLdapSearchFilter = "zimbraAuthLdapSearchFilter";
ZaDomain.A_AuthLdapSearchBindDn ="zimbraAuthLdapSearchBindDn";
ZaDomain.A_AuthLdapSearchBindPassword="zimbraAuthLdapSearchBindPassword";

ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled = "zimbraAdminConsoleDNSCheckEnabled";
ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled = "zimbraAdminConsoleCatchAllAddressEnabled";
ZaDomain.A_zimbraAdminConsoleSkinEnabled = "zimbraAdminConsoleSkinEnabled";
ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled = "zimbraAdminConsoleLDAPAuthEnabled" ;
ZaDomain.A_zimbraAuthLdapStartTlsEnabled = "zimbraAuthLdapStartTlsEnabled";

//internal attributes - not synched with the server code yet
//GAL               
ZaDomain.A_GALServerType = "galservertype";
ZaDomain.A_GALSyncServerType = "galsyncservertype";
ZaDomain.A_GALSyncUseGALSearch = "galsyncusegalsearch";
//ZaDomain.A_GALServerName = "galservername";
//ZaDomain.A_GALServerPort = "galserverport";
//ZaDomain.A_GALUseSSL = "galusessl";
ZaDomain.A_GALSearchTestMessage = "galsearchtestmessage";
ZaDomain.A_GALSyncTestMessage = "galsynctestmessage";
ZaDomain.A_GALSearchTestResultCode = "galsearchtestresutcode";
ZaDomain.A_GALSyncTestResultCode = "galsynctestresutcode";
ZaDomain.A_GALSampleQuery = "samplequery";
ZaDomain.A_UseBindPassword = "usebindpassword";
ZaDomain.A_SyncUseBindPassword = "syncusebindpassword";
ZaDomain.A_GALTestSearchResults = "galtestsearchresults";
ZaDomain.A_NotebookTemplateDir = "templatedir";
ZaDomain.A_NotebookTemplateFolder = "templatefolder";
ZaDomain.A_NotebookAccountPassword = "noteBookAccountPassword";
ZaDomain.A_NotebookAccountPassword2 = "noteBookAccountPassword2";
ZaDomain.A_CreateNotebook = "createNotebook";
ZaDomain.A_OverwriteTemplates = "overwritetemplates";
ZaDomain.A_OverwriteNotebookACLs = "overwritenotebookacls";
ZaDomain.A_NotebookPublicACLs = "pub";
ZaDomain.A_NotebookAllACLs = "all";
ZaDomain.A_NotebookDomainACLs = "dom";
ZaDomain.A_NotebookUserACLs = "usr";
ZaDomain.A_NotebookGroupACLs = "grp";
ZaDomain.A_NotebookGuestACLs = "guest";
ZaDomain.A_allNotebookACLS = "allNotebookACLS";
//values
ZaDomain.GAL_Mode_internal = "zimbra";
ZaDomain.GAL_Mode_external = "ldap";
ZaDomain.GAL_Mode_both = "both";
ZaDomain.GAL_ServerType_ad = "ad";
ZaDomain.GAL_ServerType_ldap = "ldap";

//Auth
ZaDomain.A_AuthADDomainName = "zimbraAuthADDomainName";
//ZaDomain.A_AuthLDAPServerName = "zimbraAuthLDAPServerName";
ZaDomain.A_AuthLDAPSearchBase = "zimbraAuthLDAPSearchBase";
//ZaDomain.A_AuthLDAPServerPort = "zimbraAuthLDAPServerPort";
ZaDomain.A_AuthTestUserName = "authtestusername";
ZaDomain.A_AuthTestPassword = "authtestpassword";
ZaDomain.A_AuthTestMessage = "authtestmessage";
ZaDomain.A_AuthTestResultCode = "authtestresutcode";
ZaDomain.A_AuthComputedBindDn = "authcomputedbinddn";
ZaDomain.A_AuthUseBindPassword = "authusebindpassword";
ZaDomain.A_AuthLdapSearchBindPasswordConfirm = "authldapsearchBindpasswordconfirm";
ZaDomain.A_GalSyncLdapBindPasswordConfirm = "syncldappasswordconfirm";
ZaDomain.A_zimbraVirtualHostname = "zimbraVirtualHostname";
//server value constants
ZaDomain.AuthMech_ad = "ad";
ZaDomain.AuthMech_ldap = "ldap";
ZaDomain.AuthMech_zimbra = "zimbra";
ZaDomain.A_zimbraNotebookAccount = "zimbraNotebookAccount";

//interop
ZaDomain.A_zimbraFreebusyExchangeAuthUsername = "zimbraFreebusyExchangeAuthUsername" ;
ZaDomain.A_zimbraFreebusyExchangeAuthPassword = "zimbraFreebusyExchangeAuthPassword" ;
ZaDomain.A_zimbraFreebusyExchangeAuthScheme  = "zimbraFreebusyExchangeAuthScheme" ;
ZaDomain.A_zimbraFreebusyExchangeURL ="zimbraFreebusyExchangeURL";
ZaDomain.A_zimbraFreebusyExchangeUserOrg = "zimbraFreebusyExchangeUserOrg" ;

ZaDomain.A_zimbraAvailableSkin = "zimbraAvailableSkin";
ZaDomain.A_zimbraZimletDomainAvailableZimlets = "zimbraZimletDomainAvailableZimlets" ;
//hosted attributes
ZaDomain.A_zimbraDomainCOSMaxAccounts = "zimbraDomainCOSMaxAccounts" ;
ZaDomain.A_zimbraDomainFeatureMaxAccounts = "zimbraDomainFeatureMaxAccounts" ;
ZaDomain.A2_account_limit = "account_limit" ;

//skin properties
ZaDomain.A_zimbraSkinForegroundColor = "zimbraSkinForegroundColor" ;
ZaDomain.A_zimbraSkinBackgroundColor = "zimbraSkinBackgroundColor" ;
ZaDomain.A_zimbraSkinSecondaryColor = "zimbraSkinSecondaryColor" ;
ZaDomain.A_zimbraSkinSelectionColor  = "zimbraSkinSelectionColor" ;

ZaDomain.A_zimbraSkinLogoURL ="zimbraSkinLogoURL" ;
ZaDomain.A_zimbraSkinLogoLoginBanner = "zimbraSkinLogoLoginBanner" ;
ZaDomain.A_zimbraSkinLogoAppBanner = "zimbraSkinLogoAppBanner" ;

ZaDomain.A_zimbraPrefTimeZoneId = "zimbraPrefTimeZoneId" ;
ZaDomain.A_zimbraAdminConsoleLoginMessage = "zimbraAdminConsoleLoginMessage" ;
ZaDomain.A2_allowClearTextLDAPAuth = "allowClearTextLdapAuth" ;
ZaDomain.A2_isTestingGAL = "isTestingGAL";
ZaDomain.A2_isTestingSync = "isTestingSync";
ZaDomain.A2_isTestingAuth = "isTestingAuth";
ZaDomain.A2_acl_selection_cache = "acl_selection_cache";
ZaDomain.A2_gal_sync_accounts = "gal_sync_accounts";
ZaDomain.A2_new_gal_sync_account_name = "new_gal_sync_account_name";
ZaDomain.A2_new_internal_gal_ds_name = "new_internal_gal_ds_name";
ZaDomain.A2_new_external_gal_ds_name = "new_external_gal_ds_name";
ZaDomain.A2_new_internal_gal_polling_interval = "new_internal_gal_polling_interval";
ZaDomain.A2_new_external_gal_polling_interval = "new_external_gal_polling_interval";
ZaDomain.A2_create_gal_acc = "create_gal_acc";
//result codes returned from Check* requests
ZaDomain.Check_OK = "check.OK";
ZaDomain.Check_UNKNOWN_HOST="check.UNKNOWN_HOST";
ZaDomain.Check_CONNECTION_REFUSED = "check.CONNECTION_REFUSED";
ZaDomain.Check_SSL_HANDSHAKE_FAILURE = "check.SSL_HANDSHAKE_FAILURE";
ZaDomain.Check_COMMUNICATION_FAILURE = "check.COMMUNICATION_FAILURE";
ZaDomain.Check_AUTH_FAILED = "check.AUTH_FAILED";
ZaDomain.Check_AUTH_NOT_SUPPORTED = "check.AUTH_NOT_SUPPORTED";
ZaDomain.Check_NAME_NOT_FOUND = "check.NAME_NOT_FOUND";
ZaDomain.Check_INVALID_SEARCH_FILTER = "check.INVALID_SEARCH_FILTER";
ZaDomain.Check_FAILURE = "check.FAILURE"; 
ZaDomain.Check_FAULT = "Fault";
ZaDomain.Check_SKIPPED = "Skiped";

ZaDomain.AUTH_MECH_CHOICES = [ZaDomain.AuthMech_ad,ZaDomain.AuthMech_ldap,ZaDomain.AuthMech_zimbra];

ZaDomain.LOCAL_DOMAIN_QUERY = "(zimbraDomainType=local)";

//constants for rights
ZaDomain.RIGHT_CREATE_TOP_DOMAIN = "createTopDomain";
ZaDomain.RIGHT_DELETE_DOMAIN = "deleteDomain";
ZaDomain.RIGHT_RENAME_DOMAIN = "renameDomain";
ZaDomain.RIGHT_CREATE_SUB_DOMAIN = "createSubDomain";
ZaDomain.RIGHT_CREATE_ACCOUNT = "createAccount";
ZaDomain.RIGHT_CREATE_CALRES = "createCalendarResource";
ZaDomain.RIGHT_CREATE_DL = "createDistributionList";
ZaDomain.RIGHT_CREATE_ALIAS = "createAlias";
ZaDomain.RIGHT_ADMIN_LOGIN_AS = "adminLoginAs";
ZaDomain.RIGHT_CHECK_MX_RECORD = "checkDomainMXRecord";
ZaDomain.cacheCounter = 0;
ZaDomain.staticDomainByNameCacheTable = {};
ZaDomain.staticDomainByIdCacheTable = {};
ZaDomain.putDomainToCache = function(domain) {
	if(ZaDomain.cacheCounter==100) {
		ZaDomain.staticDomainByNameCacheTable = {};
		ZaDomain.staticDomainByIdCacheTable = {};
		ZaDomain.cacheCounter = 0;
	}
		
	if(!ZaDomain.staticDomainByNameCacheTable[domain.name] || !ZaDomain.staticDomainByIdCacheTable[domain.id]) {
		ZaDomain.cacheCounter++;
	}

    ZaDomain.staticDomainByNameCacheTable[domain.name] = domain;
    ZaDomain.staticDomainByIdCacheTable[domain.id] = domain;
}
ZaDomain.compareACLs = function (val1, val2) {
	if(AjxUtil.isEmpty(val1.name) && AjxUtil.isEmpty(val2.name)) {
		if(AjxUtil.isEmpty(val1.gt) && AjXUtil.isEmpty(val2.gt)) {
			return 0;
		}
		
		if(val1.gt == val2.gt) 
			return 0;
		
		if(val1.gt < val2.gt)
			return -1;

		if(val1.gt > val2.gt)
			return 1;
	} else {
		if(val1.gt == val2.gt) {
			if(val1.name == val2.name)
				return 0;
			if(val1.name < val2.name)
				return -1;
			if(val1.name > val2.name)
				return 1;	
		} else {
			if(val1.gt == val2.gt) 
				return 0;
			
			if(val1.gt < val2.gt)
				return -1;
	
			if(val1.gt > val2.gt)
				return 1;
		}	
	}		
}
//Use ZaSearch.SearchDirectory
//In order to keep the domain list synchronized with server, we use synchronous call here.
ZaDomain.getAll =
function() {
	var params = {
		query: ZaDomain.LOCAL_DOMAIN_QUERY, 
		types:[ZaSearch.DOMAINS],
		sortBy:ZaDomain.A_domainName,
		offset:"0",
		attrs:[ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId],
		sortAscending:"1",
		limit:ZaDomain.MAXSEARCHRESULTS,
		ignoreTooManyResultsException: true,
		exceptionFrom: "ZaDomain.getAll",
		controller: ZaApp.getInstance().getCurrentController()
	}
	var list = new ZaItemList(ZaDomain);
	var responce = ZaSearch.searchDirectory(params);
	if(responce) {
		var resp = responce.Body.SearchDirectoryResponse;
		if(resp != null) {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;
			list.loadFromJS(resp);		
		}
	}
	return list;
}

/**
* Creates a new ZaDomain. This method makes SOAP request (CreateDomainRequest) to create a new domain record in LDAP. 
* @param attrs
* @param name 
* @return ZaDomain
**/
ZaDomain.createMethod =
function(tmpObj, newDomain) {

	if(tmpObj.attrs == null) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_UNKNOWN, null);
		return null;	
	}
	
	//name
	if(tmpObj.attrs[ZaDomain.A_domainName] ==null || tmpObj.attrs[ZaDomain.A_domainName].length < 1) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DOMAIN_NAME_REQUIRED);
		return null;
	}
	tmpObj.name = tmpObj.attrs[ZaDomain.A_domainName];
	//check values
	if(!AjxUtil.isEmpty(tmpObj.attrs[ZaDomain.A_zimbraGalMaxResults]) && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaDomain.A_zimbraGalMaxResults])) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR , [ZaMsg.NAD_GalMaxResults]));
		return null;
	}
	
	if(tmpObj.name.length > 256 || tmpObj.attrs[ZaDomain.A_domainName].length > 256) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DOMAIN_NAME_TOOLONG);
		return null;
	}
	
	if(tmpObj.attrs[ZaDomain.A_zimbraGalMode]!=ZaDomain.GAL_Mode_internal) {	
		//check that Filter is provided and at least one server
		if(!tmpObj.attrs[ZaDomain.A_GalLdapFilter]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_FILTER_REQUIRED);			
			return null;
		}
		if(!tmpObj.attrs[ZaDomain.A_GalLdapURL] || tmpObj.attrs[ZaDomain.A_GalLdapURL].length < 1) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);					
			return null;
		}
	} 	
	if(tmpObj.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra) {	
		if(!tmpObj.attrs[ZaDomain.A_AuthLdapURL]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
			return null;
		}
	}
	/*var domainRegEx = AjxUtil.DOMAIN_NAME_FULL_RE;
	if( !domainRegEx.test(tmpObj.attrs[ZaDomain.A_domainName]) ) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DOMAIN_NAME_INVALID);
		return null;
	}
	var nonAlphaNumEx = /[^a-zA-Z0-9\-\.]+/;
	if(nonAlphaNumEx.test(tmpObj.attrs[ZaDomain.A_domainName]) ) {
		//show error msg
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_DOMAIN_NAME_INVALID);
		return null;
	}*/	

	var soapDoc = AjxSoapDoc.create("CreateDomainRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("name", tmpObj.attrs[ZaDomain.A_domainName]);
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalMode]);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);	

	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalMaxResults]);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMaxResults);

	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_notes]);
	attr.setAttribute("n", ZaDomain.A_notes);	
	
	if(tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]);
		attr.setAttribute("n", ZaDomain.A_zimbraAuthLdapStartTlsEnabled);	
	}
		
	if(tmpObj.attrs[ZaDomain.A_AuthLdapURL]) {
		var temp = tmpObj.attrs[ZaDomain.A_AuthLdapURL].join(" ");
		attr = soapDoc.set("a", temp);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	
				
		if(tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE" && tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
			//check that we don't have ldaps://
			if(temp.indexOf("ldaps://") > -1) {
				ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
			}		
		}		
	}
	
	attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_description]);
	attr.setAttribute("n", ZaDomain.A_description);		

	if(tmpObj.attrs[ZaDomain.A_zimbraGalMode] != ZaDomain.GAL_Mode_internal) {
		temp = tmpObj.attrs[ZaDomain.A_GalLdapURL].join(" ");
		attr = soapDoc.set("a", temp);
		attr.setAttribute("n", ZaDomain.A_GalLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapFilter]);
		attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	
		
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter]);
		attr.setAttribute("n", ZaDomain.A_zimbraGalAutoCompleteLdapFilter);			
	}

	if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		//set bind DN to default for AD
		attr = soapDoc.set("a", "%u@"+tmpObj.attrs[ZaDomain.A_AuthADDomainName]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	} else if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
	
	/*	if(tmpObj.attrs[ZaDomain.A_AuthLdapSearchFilter] ==null || tmpObj.attrs[ZaDomain.A_AuthLdapSearchFilter].length < 1) {
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_FILTER_REQUIRED);
			return null;
		}
	*/
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchFilter]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchFilter);
			
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBase);	
		
		if(tmpObj[ZaDomain.A_AuthUseBindPassword] && tmpObj[ZaDomain.A_AuthUseBindPassword] == "TRUE") {
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBindDn]);
			attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindDn);	
			
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBindPassword]);
			attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindPassword);			
		}
	}

	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);	

	if(tmpObj.attrs[ZaDomain.A_domainDefaultCOSId]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_domainDefaultCOSId]);
		attr.setAttribute("n", ZaDomain.A_domainDefaultCOSId);	
	}
	
	if(tmpObj.attrs[ZaDomain.A_zimbraPublicServiceHostname]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraPublicServiceHostname]);
		attr.setAttribute("n", ZaDomain.A_zimbraPublicServiceHostname);	
	}
		
	
	if(tmpObj.attrs[ZaDomain.A_zimbraDNSCheckHostname]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraDNSCheckHostname]);
		attr.setAttribute("n", ZaDomain.A_zimbraDNSCheckHostname);	
	}
	
	if(tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled]);
		attr.setAttribute("n", ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled);	
	}

    if(tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled]);
		attr.setAttribute("n", ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled);
	}

    if(tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled]);
		attr.setAttribute("n", ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled);
	}

    if(tmpObj.attrs[ZaDomain.A_zimbraDomainStatus]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraDomainStatus]);
		attr.setAttribute("n", ZaDomain.A_zimbraDomainStatus);	
	}
		
	if(tmpObj.attrs[ZaDomain.A_domainMaxAccounts]) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_domainMaxAccounts]);
		attr.setAttribute("n", ZaDomain.A_domainMaxAccounts);	
	}
	
	if(tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname]) {
		if(tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname] instanceof Array) {
			var cnt = tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname].length;
			for(var ix=0; ix<cnt; ix++) {
				attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname][ix]);
				attr.setAttribute("n", ZaDomain.A_zimbraVirtualHostname);					
			}
		} else {
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname]);
			attr.setAttribute("n", ZaDomain.A_zimbraVirtualHostname);					
		}
	}
	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_CREATE_DOMAIN
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.CreateDomainResponse;	
	newDomain.initFromJS(resp.domain[0]);
}
ZaItem.createMethods["ZaDomain"].push(ZaDomain.createMethod);

ZaDomain.createGalAccounts = function (tmpObj,newDomain) {
	if(tmpObj[ZaDomain.A2_create_gal_acc] && tmpObj[ZaDomain.A2_create_gal_acc]=="TRUE" && tmpObj[ZaDomain.A2_new_gal_sync_account_name] && 
		(tmpObj[ZaDomain.A2_new_internal_gal_ds_name] || tmpObj[ZaDomain.A2_new_external_gal_ds_name])) {
		var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "stop");
		if(tmpObj[ZaDomain.A2_new_gal_sync_account_name].indexOf("@") < 0) {
			tmpObj[ZaDomain.A2_new_gal_sync_account_name] = [tmpObj[ZaDomain.A2_new_gal_sync_account_name],"@",tmpObj.attrs[ZaDomain.A_domainName]].join("");
		}
		if((tmpObj.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_internal || tmpObj.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_both)
			&& tmpObj[ZaDomain.A2_new_gal_sync_account_name] && tmpObj[ZaDomain.A2_new_internal_gal_ds_name]) {
			var createInternalDSDoc = soapDoc.set("CreateGalSyncAccountRequest", null, null, ZaZimbraAdmin.URN); 
			createInternalDSDoc.setAttribute("name", tmpObj[ZaDomain.A2_new_internal_gal_ds_name]);
			createInternalDSDoc.setAttribute("folder", "_"+tmpObj[ZaDomain.A2_new_internal_gal_ds_name]);
			createInternalDSDoc.setAttribute("type", "zimbra");
			createInternalDSDoc.setAttribute("domain", tmpObj.attrs[ZaDomain.A_domainName]);
			//zimbraDataSourcePollingInterval		
			soapDoc.set("account", tmpObj[ZaDomain.A2_new_gal_sync_account_name],createInternalDSDoc).setAttribute("by","name");
			soapDoc.set("a", tmpObj[ZaDomain.A2_new_internal_gal_polling_interval],createInternalDSDoc).setAttribute("n",ZaDataSource.A_zimbraDataSourcePollingInterval);
		}
		
		if(tmpObj.attrs[ZaDomain.A_zimbraGalMode] != ZaDomain.GAL_Mode_internal
			&& tmpObj[ZaDomain.A2_new_gal_sync_account_name] && tmpObj[ZaDomain.A2_new_external_gal_ds_name]) {
			var createExternalDSDoc = soapDoc.set("CreateGalSyncAccountRequest", null, null, ZaZimbraAdmin.URN); 
			createExternalDSDoc.setAttribute("name", tmpObj[ZaDomain.A2_new_external_gal_ds_name]);
			createExternalDSDoc.setAttribute("folder", "_"+tmpObj[ZaDomain.A2_new_external_gal_ds_name]);
			createExternalDSDoc.setAttribute("type", "ldap");		
			createExternalDSDoc.setAttribute("domain", tmpObj.attrs[ZaDomain.A_domainName]);
			soapDoc.set("account", tmpObj[ZaDomain.A2_new_gal_sync_account_name],createExternalDSDoc).setAttribute("by","name");
			soapDoc.set("a", tmpObj[ZaDomain.A2_new_external_gal_polling_interval],createExternalDSDoc).setAttribute("n","zimbraDataSourcePollingInterval");
		}	
		
		try {
			params = new Object();
			params.soapDoc = soapDoc;	
			var reqMgrParams ={
				controller:ZaApp.getInstance().getCurrentController(),
				busyMsg : ZaMsg.BUSY_CREATING_GALDS,
				showBusy:true
			}
			var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
			if(respObj.isException && respObj.isException()) {
				ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaDomain.createGalAccounts", null, false);
			    hasError  = true ;
                lastException = ex ;
            } else if(respObj.Body.BatchResponse.Fault) {
				var fault = respObj.Body.BatchResponse.Fault;
				if(fault instanceof Array)
					fault = fault[0];
			
				if (fault) {
					// JS response with fault
					var ex = ZmCsfeCommand.faultToEx(fault);
					ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaDomain.createGalAccounts", null, false);
                    hasError = true ;
                    lastException = ex ;
                }
			} else {
				var batchResp = respObj.Body.BatchResponse;
			}
		} catch (ex) {
			//show the error and go on
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.createGalAccounts", null, false);
		    hasError = true ;
            lastException = ex ;
		}			
	}			
}
ZaItem.createMethods["ZaDomain"].push(ZaDomain.createGalAccounts);

ZaDomain.prototype.loadNewObjectDefaults = function (domainBy, domain, cosBy, cos) {
	ZaItem.prototype.loadNewObjectDefaults.call(this,domainBy, domain, cosBy, cos);
	this[ZaDomain.A2_new_gal_sync_account_name] = "galsync";
	this[ZaDomain.A2_new_internal_gal_ds_name] = "zimbra";
	this[ZaDomain.A2_new_external_gal_ds_name] = "ldap";
		
}
ZaDomain.canConfigureAuth = function (obj) {
	return (ZaItem.hasWritePermission(ZaDomain.A_AuthMech,obj) 
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapURL,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapUserDn,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapSearchBase,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapSearchFilter,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapSearchBindDn,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_AuthLdapSearchBindPassword,obj));
}
ZaDomain.canConfigureGal = function (obj) {
	return (ZaItem.hasRight(ZaDomain.RIGHT_ADMIN_LOGIN_AS,obj)
		&& ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalAccountId,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalMode,obj) 
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapURL,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapSearchBase,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapBindDn,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapBindDn,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapBindPassword,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_GalLdapFilter,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalAutoCompleteLdapFilter,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapURL,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapSearchBase,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapFilter,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapAuthMech,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapBindDn,obj)
		&& ZaItem.hasWritePermission(ZaDomain.A_zimbraGalSyncLdapBindPassword,obj));
}

ZaDomain.canConfigureWiki = function (obj) {
	if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,obj) && ZaItem.hasWritePermission(ZaDomain.A_zimbraNotebookAccount,obj) && ZaItem.hasRight(ZaDomain.RIGHT_ADMIN_LOGIN_AS,obj))
		return true;
}

ZaDomain.testAuthSettings = 
function (obj, callback) {
	var soapDoc = AjxSoapDoc.create("CheckAuthConfigRequest", ZaZimbraAdmin.URN, null);
	var attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);
	
	var temp = obj.attrs[ZaDomain.A_AuthLdapURL].join(" ");
	attr = soapDoc.set("a", temp);
	attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	
	
	if(obj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {	
		attr = soapDoc.set("a", "%u@"+obj.attrs[ZaDomain.A_AuthADDomainName]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	}
	if(obj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
		if(obj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]) {
			attr = soapDoc.set("a", obj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]);
			attr.setAttribute("n", ZaDomain.A_zimbraAuthLdapStartTlsEnabled);	
		}			
			
		if(obj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE") {
			//check that we don't have ldaps://
			if(temp.indexOf("ldaps://") > -1) {
				ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
			}		
		}
					
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBase);
	
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapSearchFilter]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchFilter);

		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapSearchBindDn]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindDn);
	
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_AuthLdapSearchBindPassword]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindPassword);
	}

	
	attr = soapDoc.set("name", obj[ZaDomain.A_AuthTestUserName]);
	attr = soapDoc.set("password", obj[ZaDomain.A_AuthTestPassword]);	
	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	params.callback = callback;
	command.invoke(params);	

}

ZaDomain.getRevokeNotebookACLsRequest = function (permsToRevoke, soapDoc) {
	var cnt = permsToRevoke.length;
	for(var i = 0; i < cnt; i++) {
		var folderActionRequest = soapDoc.set("FolderActionRequest", null, null, "urn:zimbraMail");			
		var actionEl = soapDoc.set("action", "",folderActionRequest);
		actionEl.setAttribute("id", ZaDomain.WIKI_FOLDER_ID);	
		actionEl.setAttribute("op", "!grant");	
		actionEl.setAttribute("zid", permsToRevoke[i].zid);				
	}
}

ZaDomain.getGrantNotebookACLsRequest = function (obj, soapDoc) {
	var folderActionRequest = soapDoc.set("FolderActionRequest", null, null, "urn:zimbraMail");
	var actionEl = soapDoc.set("action", "",folderActionRequest);
	actionEl.setAttribute("id", ZaDomain.WIKI_FOLDER_ID);	
	actionEl.setAttribute("op", "grant");	
	var grantEl = soapDoc.set("grant", "",actionEl);	
	grantEl.setAttribute("gt", obj.gt);
	if(obj.name) {
		grantEl.setAttribute("d", obj.name);
	}
	var perms = "";
	for(var a in obj.acl) {
		if(obj.acl[a]==1)
			perms+=a;
	}
	grantEl.setAttribute("perm", perms);				
}

ZaDomain.getNotebookACLsRequestOld = function (obj, soapDoc) {
	if(obj.notebookAcls) {
		for(var gt in obj.notebookAcls) {
			if(obj.notebookAcls[gt] instanceof Array) {
				var grants = obj.notebookAcls[gt];
				var cnt = grants.length;
				for (var i =0; i < cnt; i++) {
					var grantObj = grants[i];
					if(!grantObj.name || !grantObj.acl)
						continue;
						
					var folderActionRequest = soapDoc.set("FolderActionRequest", null, null, "urn:zimbraMail");
					var actionEl = soapDoc.set("action", "",folderActionRequest);	
					actionEl.setAttribute("id", ZaDomain.WIKI_FOLDER_ID);	
					actionEl.setAttribute("op", "grant");					
					var grantEl = soapDoc.set("grant", "",actionEl);
					grantEl.setAttribute("gt", gt);
					grantEl.setAttribute("d", grantObj.name);
					var perms = "";
					for(var a in grantObj.acl) {
						if(grantObj.acl[a]==1)
							perms+=a;
					}	
					grantEl.setAttribute("perm", perms);				
				}
			} else {
				var folderActionRequest = soapDoc.set("FolderActionRequest", null, null, "urn:zimbraMail");
				var actionEl = soapDoc.set("action", "",folderActionRequest);
				actionEl.setAttribute("id", ZaDomain.WIKI_FOLDER_ID);	
				actionEl.setAttribute("op", "grant");	
				var grantEl = soapDoc.set("grant", "",actionEl);	
				grantEl.setAttribute("gt", gt);
				if(gt==ZaDomain.A_NotebookDomainACLs) {
					grantEl.setAttribute("d", obj.attrs[ZaDomain.A_domainName]);
				}
				var perms = "";
				for(var a in obj.notebookAcls[gt]) {
					if(obj.notebookAcls[gt][a]==1)
						perms+=a;
				}
				grantEl.setAttribute("perm", perms);					
			}
		}
	}
}

ZaDomain.setNotebookACLs = function (obj, callback) {
	var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "stop");
	ZaDomain.getNotebookACLsRequestOld	(obj,soapDoc);			
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.accountName = obj.attrs[ZaDomain.A_zimbraNotebookAccount];
			
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	command.invoke(params);				
}

ZaDomain.initNotebook = function (obj, callback, controller) {
	var soapDoc = AjxSoapDoc.create("InitNotebookRequest", ZaZimbraAdmin.URN, null);
	if(obj[ZaDomain.A_NotebookTemplateDir]) {
		var attr = soapDoc.set("template", obj[ZaDomain.A_NotebookTemplateDir]);
		if(obj[ZaDomain.A_NotebookTemplateFolder]) {
			attr.setAttribute("dest", obj[ZaDomain.A_NotebookTemplateFolder]);			
		}
	}
	
	if(obj.attrs[ZaDomain.A_zimbraNotebookAccount]) {
		var attr = soapDoc.set("name", obj.attrs[ZaDomain.A_zimbraNotebookAccount]);
		if(obj[ZaDomain.A_NotebookAccountPassword]) {
			soapDoc.set("password", obj[ZaDomain.A_NotebookAccountPassword]);			
		}
	}
	var attr = soapDoc.set("domain", obj.attrs[ZaDomain.A_domainName]);
	attr.setAttribute("by", "name");
	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	var reqMgrParams = {
		controller : controller,
		busyMsg : ZaMsg.BUSY_INIT_NOTEBOOK
	}
	ZaRequestMgr.invoke(params, reqMgrParams);	
}

ZaDomain.testSyncSettings = function (obj, callback){
	var soapDoc = AjxSoapDoc.create("CheckGalConfigRequest", ZaZimbraAdmin.URN, null);
	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);

	var temp = obj.attrs[ZaDomain.A_GalLdapURL].join(" ");
	attr = soapDoc.set("a", temp);
	attr.setAttribute("n", ZaDomain.A_GalLdapURL);	
	
	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapSearchBase]);
	attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapFilter]);
	attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	

	if(obj.attrs[ZaDomain.A_GalLdapBindDn]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);
	}

	if(obj.attrs[ZaDomain.A_GalLdapBindPassword]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);
	}

//	
	if(obj.attrs[ZaDomain.A_GALSyncUseGALSearch]=="FALSE") {
		if(obj.attrs[ZaDomain.A_zimbraGalSyncLdapURL]) {
			var temp = obj.attrs[ZaDomain.A_zimbraGalSyncLdapURL].join(" ");
			attr = soapDoc.set("a", temp);
			attr.setAttribute("n", ZaDomain.A_zimbraGalSyncLdapURL);	
		}
		
		if(obj.attrs[ZaDomain.A_zimbraGalSyncLdapSearchBase]) {
			attr = soapDoc.set("a", obj.attrs[ZaDomain.A_zimbraGalSyncLdapSearchBase]);
			attr.setAttribute("n", ZaDomain.A_zimbraGalSyncLdapSearchBase);	
		}
	
		if(obj.attrs[ZaDomain.A_zimbraGalSyncLdapFilter]) {
			attr = soapDoc.set("a", obj.attrs[ZaDomain.A_zimbraGalSyncLdapFilter]);
			attr.setAttribute("n", ZaDomain.A_zimbraGalSyncLdapFilter);	
		}
	
		if(obj.attrs[ZaDomain.A_zimbraGalSyncLdapBindDn]) {
			attr = soapDoc.set("a", obj.attrs[ZaDomain.A_zimbraGalSyncLdapBindDn]);
			attr.setAttribute("n", ZaDomain.A_zimbraGalSyncLdapBindDn);
		}
	
		if(obj.attrs[ZaDomain.A_GalLdapBindPassword]) {
			attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindPassword]);
			attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);
		}
	}

	soapDoc.set("action", "sync");	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	params.callback = callback;
	command.invoke(params);	
}

ZaDomain.testGALSettings = function (obj, callback, sampleQuery) {
	
	//search
	var soapDoc = AjxSoapDoc.create("CheckGalConfigRequest", ZaZimbraAdmin.URN, null);
	var attr = soapDoc.set("a", ZaDomain.GAL_Mode_external);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);

	var temp = obj.attrs[ZaDomain.A_GalLdapURL].join(" ");
	attr = soapDoc.set("a", temp);
	attr.setAttribute("n", ZaDomain.A_GalLdapURL);	
	
	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapSearchBase]);
	attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

	attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapFilter]);
	attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	

	if(obj.attrs[ZaDomain.A_GalLdapBindDn]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindDn]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);
	}

	if(obj.attrs[ZaDomain.A_GalLdapBindPassword]) {
		attr = soapDoc.set("a", obj.attrs[ZaDomain.A_GalLdapBindPassword]);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);
	}
	soapDoc.set("query", "*" + sampleQuery + "*");
	soapDoc.set("action", "search");
	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = true;
	params.callback = callback;
	command.invoke(params);
}

ZaDomain.modifyGalSettings = 
function(tmpObj) {
	var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "stop");
	
	if(tmpObj[ZaDomain.A2_create_gal_acc] && tmpObj[ZaDomain.A2_create_gal_acc]=="TRUE" && tmpObj[ZaDomain.A2_new_internal_gal_ds_name] || tmpObj[ZaDomain.A2_new_external_gal_ds_name]) {
		if(tmpObj[ZaDomain.A2_new_gal_sync_account_name]) {
			if(tmpObj[ZaDomain.A2_new_gal_sync_account_name].indexOf("@") < 0) {
				tmpObj[ZaDomain.A2_new_gal_sync_account_name] = [tmpObj[ZaDomain.A2_new_gal_sync_account_name],"@",tmpObj.attrs[ZaDomain.A_domainName]].join("");
			}
		} 
		 
		if((tmpObj.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_internal || tmpObj.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_both)
			&& tmpObj[ZaDomain.A2_new_internal_gal_ds_name]) {
			var createInternalDSDoc = soapDoc.set("CreateGalSyncAccountRequest", null, null, ZaZimbraAdmin.URN); 
			createInternalDSDoc.setAttribute("name", tmpObj[ZaDomain.A2_new_internal_gal_ds_name]);
			createInternalDSDoc.setAttribute("folder", "_"+tmpObj[ZaDomain.A2_new_internal_gal_ds_name]);
			createInternalDSDoc.setAttribute("type", "zimbra");
			createInternalDSDoc.setAttribute("domain", tmpObj.attrs[ZaDomain.A_domainName]);		
			if(tmpObj[ZaDomain.A2_new_gal_sync_account_name]) {
				soapDoc.set("account", tmpObj[ZaDomain.A2_new_gal_sync_account_name],createInternalDSDoc).setAttribute("by","name");
								
			} else if (tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) {
				soapDoc.set("account", tmpObj[ZaDomain.A2_gal_sync_accounts][0].name,createInternalDSDoc).setAttribute("by","name");

			}
			if(tmpObj[ZaDomain.A2_new_internal_gal_polling_interval]) {
				soapDoc.set("a", tmpObj[ZaDomain.A2_new_internal_gal_polling_interval],createInternalDSDoc).setAttribute("n",ZaDataSource.A_zimbraDataSourcePollingInterval);				
			}/* else if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds] && tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs) {
								soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],createInternalDSDoc).setAttribute("n",ZaDataSource.A_zimbraDataSourcePollingInterval);
			}*/
		}
		
		if(tmpObj.attrs[ZaDomain.A_zimbraGalMode] != ZaDomain.GAL_Mode_internal
			&& tmpObj[ZaDomain.A2_new_external_gal_ds_name]) {
			var createExternalDSDoc = soapDoc.set("CreateGalSyncAccountRequest", null, null, ZaZimbraAdmin.URN); 
			createExternalDSDoc.setAttribute("name", tmpObj[ZaDomain.A2_new_external_gal_ds_name]);
			createExternalDSDoc.setAttribute("folder", "_"+tmpObj[ZaDomain.A2_new_external_gal_ds_name]);
			createExternalDSDoc.setAttribute("type", "ldap");		
			createExternalDSDoc.setAttribute("domain", tmpObj.attrs[ZaDomain.A_domainName]);
			if(tmpObj[ZaDomain.A2_new_gal_sync_account_name]) {
				soapDoc.set("account", tmpObj[ZaDomain.A2_new_gal_sync_account_name],createExternalDSDoc).setAttribute("by","name");
			}  else if (tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) {
				soapDoc.set("account", tmpObj[ZaDomain.A2_gal_sync_accounts][0].name,createExternalDSDoc).setAttribute("by","name");
			}
			
			if(tmpObj[ZaDomain.A2_new_external_gal_polling_interval]) {
				soapDoc.set("a", tmpObj[ZaDomain.A2_new_external_gal_polling_interval],createExternalDSDoc).setAttribute("n",ZaDataSource.A_zimbraDataSourcePollingInterval);				
			}/* else if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds] && tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs) {
				soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],createExternalDSDoc).setAttribute("n",ZaDataSource.A_zimbraDataSourcePollingInterval);
			}*/
			
		}	
	}
	if(tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) {
		if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds] 
			&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds]
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs) {
			if(this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
			tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
				var modifyDSDoc = soapDoc.set("ModifyDataSourceRequest", null, null, ZaZimbraAdmin.URN);
				soapDoc.set("id", this[ZaDomain.A2_gal_sync_accounts][0].id, modifyDSDoc);
				var ds = soapDoc.set("dataSource", null,modifyDSDoc);
				ds.setAttribute("id", this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].id);
				var attr = soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],ds);
				attr.setAttribute("n", ZaDataSource.A_zimbraDataSourcePollingInterval);				
			}
			
		}
		
		if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds] 
			&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds]
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs) {
			if(this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
			tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
				var modifyDSDoc = soapDoc.set("ModifyDataSourceRequest", null, null, ZaZimbraAdmin.URN);
				soapDoc.set("id", this[ZaDomain.A2_gal_sync_accounts][0].id, modifyDSDoc);
				var ds = soapDoc.set("dataSource", null,modifyDSDoc);
				ds.setAttribute("id", this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].id);
				var attr = soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],ds);
				attr.setAttribute("n", ZaDataSource.A_zimbraDataSourcePollingInterval);
			}
			
		}
	}	
	var modifyDomainDoc = soapDoc.set("ModifyDomainRequest", null, null, ZaZimbraAdmin.URN);
	soapDoc.set("id", this.id,modifyDomainDoc);
	
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalMode],modifyDomainDoc);
	attr.setAttribute("n", ZaDomain.A_zimbraGalMode);	

	if(tmpObj.attrs[ZaDomain.A_zimbraGalMode] != ZaDomain.GAL_Mode_internal) {
		var temp = tmpObj.attrs[ZaDomain.A_GalLdapURL].join(" ");
		attr = soapDoc.set("a", temp,modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_GalLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapSearchBase],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_GalLdapSearchBase);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindDn],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindDn);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapBindPassword],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_GalLdapBindPassword);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_GalLdapFilter],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_GalLdapFilter);	
		
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_zimbraGalAutoCompleteLdapFilter);		
	}
	if(this[ZaDomain.A_zimbraGalMaxResults] != tmpObj.attrs[ZaDomain.A_zimbraGalMaxResults],modifyDomainDoc) {
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraGalMaxResults],modifyDomainDoc);
		attr.setAttribute("n", ZaDomain.A_zimbraGalMaxResults);	
	}
		
	try {
		params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams ={
			controller:ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_CREATING_GALDS,
			showBusy:true
		}
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaDomain.modifyGalSettings", null, false);
		    hasError  = true ;
            lastException = ex ;
        } else if(respObj.Body.BatchResponse.Fault) {
			var fault = respObj.Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
			
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaDomain.modifyGalSettings", null, false);
                hasError = true ;
                lastException = ex ;
            }
		} else {
/*			var batchResp = respObj.Body.BatchResponse;
			var resp = batchResp.ModifyDomainResponse[0];	
			this.initFromJS(resp.domain[0]);*/
			this.refresh(false,true);
			ZaDomain.putDomainToCache(this);			
		}
	} catch (ex) {
		//show the error and go on
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.modifyGalSettings", null, false);
	    hasError = true ;
        lastException = ex ;
	}			
}	

ZaDomain.modifyAuthSettings = 
function(tmpObj) {

	var soapDoc = AjxSoapDoc.create("ModifyDomainRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	
	var attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthMech]);
	attr.setAttribute("n", ZaDomain.A_AuthMech);	
	
	if(tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		var temp = tmpObj.attrs[ZaDomain.A_AuthLdapURL].join(" ");
		attr = soapDoc.set("a", temp);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	

		attr = soapDoc.set("a", "%u@"+tmpObj.attrs[ZaDomain.A_AuthADDomainName]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
	} else if (tmpObj.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {

		if(tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]) {
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled]);
			attr.setAttribute("n", ZaDomain.A_zimbraAuthLdapStartTlsEnabled);	
		}		
		
		var temp = tmpObj.attrs[ZaDomain.A_AuthLdapURL].join(" ");
		
		if(tmpObj.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE") {
			//check that we don't have ldaps://
			if(temp.indexOf("ldaps://") > -1) {
				ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
			}		
		}		
		attr = soapDoc.set("a", temp);
		attr.setAttribute("n", ZaDomain.A_AuthLdapURL);	

		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchFilter]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchFilter);
			
		attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBase]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBase);	
		
		if(tmpObj[ZaDomain.A_AuthUseBindPassword] && tmpObj[ZaDomain.A_AuthUseBindPassword] == "TRUE") {
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBindDn]);
			attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindDn);	
			
			attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapSearchBindPassword]);
			attr.setAttribute("n", ZaDomain.A_AuthLdapSearchBindPassword);			
		}
		
		/*attr = soapDoc.set("a", tmpObj.attrs[ZaDomain.A_AuthLdapUserDn]);
		attr.setAttribute("n", ZaDomain.A_AuthLdapUserDn);	
		*/
	
	}
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_DOMAIN
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyDomainResponse;	
	this.initFromJS(resp.domain[0]);	
}

ZaDomain.prototype.setStatus = function (newStatus) {
	var soapDoc = AjxSoapDoc.create("ModifyDomainStatusRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);

	var params = new Object();
	params.soapDoc = soapDoc;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_DOMAIN_STATUS
	}	
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyDomainStatusResponse;	
	this.attrs[ZaDomain.A_zimbraDomainStatus] = resp.domain.status;
}

/**
* @param mods - map of modified attributes that will be sent to the server
* modifies object's information in the database
**/
ZaDomain.modifyMethod =
function(mods,tmpObj) {
	var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "stop");
	
	var modifyDomainDoc = soapDoc.set("ModifyDomainRequest", null, null, ZaZimbraAdmin.URN);
	soapDoc.set("id", this.id,modifyDomainDoc);
	
    for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt) {
				for(var ix=0; ix <cnt; ix++) {
					if(mods[aname][ix]) { //if there is an empty element in the array - don't send it
						var attr = soapDoc.set("a", mods[aname][ix],modifyDomainDoc);
						attr.setAttribute("n", aname);
					}
				}
			} else {
				var attr = soapDoc.set("a", "",modifyDomainDoc);
				attr.setAttribute("n", aname);
			}
		} else {		
			var attr = soapDoc.set("a", mods[aname],modifyDomainDoc);
			attr.setAttribute("n", aname);
		}
    }
    
	if(tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) { 
		if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds] 
			&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds]
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs) {
			if(this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
			tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
				var modifyDSDoc = soapDoc.set("ModifyDataSourceRequest", null, null, ZaZimbraAdmin.URN);
				soapDoc.set("id", this[ZaDomain.A2_gal_sync_accounts][0].id, modifyDSDoc);
				var ds = soapDoc.set("dataSource", null,modifyDSDoc);
				ds.setAttribute("id", this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].id);
				var attr = soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],ds);
				attr.setAttribute("n", ZaDataSource.A_zimbraDataSourcePollingInterval);				
			}
		}
		
		if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds] 
			&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds]
			&& this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs) {
			if(this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
			tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
				var modifyDSDoc = soapDoc.set("ModifyDataSourceRequest", null, null, ZaZimbraAdmin.URN);
				soapDoc.set("id", this[ZaDomain.A2_gal_sync_accounts][0].id, modifyDSDoc);
				var ds = soapDoc.set("dataSource", null,modifyDSDoc);
				ds.setAttribute("id", this[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].id);
				var attr = soapDoc.set("a", tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval],ds);
				attr.setAttribute("n", ZaDataSource.A_zimbraDataSourcePollingInterval);
			}
		}
	}	
	
	try {
		params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams ={
			controller:ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_DOMAIN,
			showBusy:true
		}
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaDomain.modifyGalSettings", null, false);
		    hasError  = true ;
            lastException = ex ;
        } else if(respObj.Body.BatchResponse.Fault) {
			var fault = respObj.Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
			
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaDomain.modifyGalSettings", null, false);
                hasError = true ;
                lastException = ex ;
            }
		} else {
			var batchResp = respObj.Body.BatchResponse;
			var resp = batchResp.ModifyDomainResponse[0];	
			this.initFromJS(resp.domain[0]);
		}
	} catch (ex) {
		//show the error and go on
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.modifyGalSettings", null, false);
	    hasError = true ;
        lastException = ex ;
	}		    

	this.refresh(false,true);
	ZaDomain.putDomainToCache(this);
}
ZaItem.modifyMethods["ZaDomain"].push(ZaDomain.modifyMethod);

ZaDomain.prototype.initFromJS = 
function (obj) {
    ZaItem.prototype.initFromJS.call(this, obj);

    if (!(this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] instanceof Array)) {
        if (this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts]) {
            this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] = [this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts]];
        } else {
            this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts] = [];
        }
    }

    if (!(this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts] instanceof Array)) {
        if (this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts]) {
            this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts] = [this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts]];
        } else {
            this.attrs[ZaDomain.A_zimbraDomainFeatureMaxAccounts] = [];
        }
    }


    if(!(this.attrs[ZaDomain.A_zimbraVirtualHostname] instanceof Array)) {
		if(this.attrs[ZaDomain.A_zimbraVirtualHostname])
			this.attrs[ZaDomain.A_zimbraVirtualHostname] = [this.attrs[ZaDomain.A_zimbraVirtualHostname]];	
		else
			this.attrs[ZaDomain.A_zimbraVirtualHostname] = new Array();
	}
	if(!this.attrs[ZaDomain.A_AuthMech]) {
		this.attrs[ZaDomain.A_AuthMech] = ZaDomain.AuthMech_zimbra; //default value
	}
	if(!this.attrs[ZaDomain.A_zimbraGalMode]) {
		this.attrs[ZaDomain.A_zimbraGalMode] = ZaDomain.GAL_Mode_internal; //default value
	}

	if(this.attrs[ZaDomain.A_AuthLdapURL]) {
		// Split Auth URL into an array 
		var temp = this.attrs[ZaDomain.A_AuthLdapURL];
		this.attrs[ZaDomain.A_AuthLdapURL] = temp.split(" ");

	} else this.attrs[ZaDomain.A_AuthLdapURL] = new Array();
	if(this.attrs[ZaDomain.A_GalLdapURL])	{	
		var temp = this.attrs[ZaDomain.A_GalLdapURL];
		this.attrs[ZaDomain.A_GalLdapURL] = temp.split(" ");		
	} else this.attrs[ZaDomain.A_GalLdapURL] = new Array();
	
	if(this.attrs[ZaDomain.A_zimbraGalMode]) {
		if(this.attrs[ZaDomain.A_zimbraGalMode] == "ldap" || this.attrs[ZaDomain.A_zimbraGalMode] == "both") {
			if(this.attrs[ZaDomain.A_GalLdapFilter] == "ad") {
				this.attrs[ZaDomain.A_GALServerType] = "ad";
			} else {
				this.attrs[ZaDomain.A_GALServerType] = "ldap";
			}
			if(!this.attrs[ZaDomain.A_zimbraGalSyncLdapURL]) {
				this.attrs[ZaDomain.A_GALSyncUseGALSearch]="TRUE";
			} else {
				this.attrs[ZaDomain.A_GALSyncUseGALSearch]="FALSE";
				if(this.attrs[ZaDomain.A_zimbraGalSyncLdapBindDn] || this.attrs[ZaDomain.A_zimbraGalSyncLdapBindPassword]) {
					this.attrs[ZaDomain.A_SyncUseBindPassword] = "TRUE";
				} else {
					this.attrs[ZaDomain.A_SyncUseBindPassword] = "FALSE";
				}
				if(this.attrs[ZaDomain.A_zimbraGalSyncLdapFilter] == "ad") {
					this.attrs[ZaDomain.A_GALSyncServerType] = "ad";
				} else {
					this.attrs[ZaDomain.A_GALSyncServerType] = "ldap";
				}
			}
		} else {
			this.attrs[ZaDomain.A_GALSyncUseGALSearch]="TRUE";
		}
	} else {
		this.attrs[ZaDomain.A_zimbraGalMode] = "zimbra";
		this.attrs[ZaDomain.A_GALSyncUseGALSearch]="TRUE";
	}
	
	if(this.attrs[ZaDomain.A_GalLdapBindDn] || this.attrs[ZaDomain.A_GalLdapBindPassword]) {
		this.attrs[ZaDomain.A_UseBindPassword] = "TRUE";
	} else {
		this.attrs[ZaDomain.A_UseBindPassword] = "FALSE";
	}
	
	//
	//if(this.attrs[ZaDomain.A_AuthADDomainName]);
	if(!AjxUtil.isEmpty(this.attrs[ZaDomain.A_AuthLdapUserDn])) {
		this.attrs[ZaDomain.A_AuthADDomainName] = ZaAccount.getDomain(this.attrs[ZaDomain.A_AuthLdapUserDn]);	
	}
		
	if(this.attrs[ZaDomain.A_AuthLdapSearchBindDn] || this.attrs[ZaDomain.A_AuthLdapSearchBindPassword]) {
		this[ZaDomain.A_AuthUseBindPassword] = "TRUE";
	} else {
		this[ZaDomain.A_AuthUseBindPassword] = "FALSE";
	}
		
	this[ZaDomain.A_GALSampleQuery] = "john";
	if(!this.attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter])
		this.attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter] = "(|(cn=%s*)(sn=%s*)(gn=%s*)(mail=%s*))";
}

ZaDomain.prototype.parseNotebookFolderAcls = function (resp) {
	try {
		if(resp.isException && resp.isException()) {
			throw(resp.getException());
		}
		
		var response;
		if(resp.getResponse)
			response = resp.getResponse().Body.GetFolderResponse;
		else
			response = resp.Body.GetFolderResponse;
			
		if(response && response.folder && response.folder[0] && response.folder[0].acl
			&& response.folder[0].acl.grant) {
			var grants = response.folder[0].acl.grant;
			var cnt = grants.length;
			this[ZaDomain.A_allNotebookACLS] = [];
			for (var gi = 0; gi < cnt; gi++) {
				var grant = grants[gi];
				var grantObj = {
					r:grant.perm.indexOf("r")>=0 ? 1 : 0,
					w:grant.perm.indexOf("w")>=0 ? 1 : 0,
					i:grant.perm.indexOf("i")>=0 ? 1 : 0,
					d:grant.perm.indexOf("d")>=0 ? 1 : 0,
					a:grant.perm.indexOf("a")>=0 ? 1 : 0,
					x:grant.perm.indexOf("x")>=0 ? 1 : 0,
					toString:function () {
						return this.r+this.w+this.i+this.d+this.a+this.x;
					}
				};
				this[ZaDomain.A_allNotebookACLS].push({acl:grantObj, name:grant.d, zid:grant.zid, gt:grant.gt, 
						toString:function() {
							return (this.gt+":"+this.name+":"+this.grantObj.toString());
						}
					});
			}
			this[ZaDomain.A_allNotebookACLS]._version = 0;
			
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.prototype.parseNotebookFolderAcls", null, false);	
	}
}
/**
* Returns HTML for a tool tip for this domain.
*/
ZaDomain.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350'>";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("Domain");			
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

ZaDomain.prototype.remove = 
function(callback) {
	var soapDoc = AjxSoapDoc.create("DeleteDomainRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_DOMAIN
	}
	ZaRequestMgr.invoke(params, reqMgrParams);	
}

ZaDomain.getLoginMessage = function () {
    var domain = ZaDomain.getDomainByName (ZaSettings.myDomainName, true) ;
    return domain.attrs[ZaDomain.A_zimbraAdminConsoleLoginMessage]  ;
}

ZaDomain.getDomainByName = 
function(domName) {
	if(!domName)
		return null;
	var domain = ZaDomain.staticDomainByNameCacheTable[domName];
	if(!domain) {
		domain = new ZaDomain();
		try {
			domain.load("name", domName, false, true);
		} catch (ex) {
			/*if(ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
				return null;
			} else {
				throw (ex);
			}*/
            /*if (ZaApp.getInstance() && ZaApp.getInstance().getCurrentController()) {
                ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.getDomainByName", null, false);
            }*/
            throw (ex);
        }

		ZaDomain.putDomainToCache(domain);
	} 
	return domain;	
} 

ZaDomain.getDomainById = 
function (domId) {
	if(!domId)
		return null;
		
	var domain = ZaDomain.staticDomainByIdCacheTable[domId];
	if(!domain) {
		domain = new ZaDomain();
		try {
			domain.load("id", domId, false, true);
		} catch (ex) {
			if(ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
				return null;
			} else {
				throw (ex);
			}
		}
		ZaDomain.putDomainToCache(domain);
	}
	return domain;
}

ZaDomain.loadMethod = 
function(by, val) {
	by = by ? by : "name";
	val = val ? val : this.attrs[ZaDomain.A_domainName];
	this.notebookAcls[ZaDomain.A_NotebookAllACLs] = {r:0,w:0,i:0,d:0,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookPublicACLs] = {r:0,w:0,i:0,d:0,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookDomainACLs] = {r:1,w:1,i:1,d:1,a:0,x:0};
	this.notebookAcls[ZaDomain.A_NotebookUserACLs] = [/*{name:"", acl:{r:0,w:0,i:0,d:0,a:0,x:0}, 
			toString:function () {
				return [this.name,this.acl[r],this.acl[w],this.acl[i],this.acl[d],this.acl[a],this.acl[x]].join();
			}}*/];
	this.notebookAcls[ZaDomain.A_NotebookGroupACLs] = [/*{name:"", acl:{r:0,w:0,i:0,d:0,a:0,x:0}, 
			toString:function () {
				return [this.name,this.acl[r],this.acl[w],this.acl[i],this.acl[d],this.acl[a],this.acl[x]].join();
			}
		}*/];

	var soapDoc = AjxSoapDoc.create("GetDomainRequest", ZaZimbraAdmin.URN, null);
	soapDoc.getMethod().setAttribute("applyConfig", "false");
	var elBy = soapDoc.set("domain", val);
	elBy.setAttribute("by", by);
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
	}
	
	var params = new Object();
	params.soapDoc = soapDoc;	
	
	var reqMgrParams = {
		controller : (ZaApp.getInstance() ? ZaApp.getInstance().getCurrentController() : null),
		busyMsg : ZaMsg.BUSY_GET_DOMAIN
	}
	var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
	if(respObj.isException && respObj.isException()) {
		if(ZaApp.getInstance() && ZaApp.getInstance().getCurrentController()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaDomain.loadMethod", null, false);
		}
    } else {
		var resp = respObj.Body.GetDomainResponse;
		this.initFromJS(resp.domain[0]);
    }
}
ZaItem.loadMethods["ZaDomain"].push(ZaDomain.loadMethod);

ZaDomain.loadDataSources = function (by, val) {
	if(this.attrs[ZaDomain.A_zimbraGalAccountId]) {
		if(!(this.attrs[ZaDomain.A_zimbraGalAccountId] instanceof Array)) {
			this.attrs[ZaDomain.A_zimbraGalAccountId] = [this.attrs[ZaDomain.A_zimbraGalAccountId]];
		}
		this[ZaDomain.A2_gal_sync_accounts] = [];
		
		for(var i=0; i< this.attrs[ZaDomain.A_zimbraGalAccountId].length; i++) {
			try {
				var galSyncAccount = new ZaAccount();
				galSyncAccount.load("id", this.attrs[ZaDomain.A_zimbraGalAccountId][i], false, false);
				this[ZaDomain.A2_gal_sync_accounts].push(galSyncAccount);
			} catch (ex) {
				if (ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT) {
					if(ZaApp.getInstance() && ZaApp.getInstance().getCurrentController())
						ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_GALSYNC_ACCOUNT_INVALID,[this.name,this.attrs[ZaDomain.A_zimbraGalAccountId][i]]), ex, true);
				} else {
					if(ZaApp.getInstance() && ZaApp.getInstance().getCurrentController())
						ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.loadDataSources", null, false);
				}	
			}
		}
	}
}
ZaItem.loadMethods["ZaDomain"].push(ZaDomain.loadDataSources);

ZaDomain.loadNotebookACLs = function(by, val) {
    if(this.attrs[ZaDomain.A_zimbraDomainStatus] != ZaDomain.DOMAIN_STATUS_MAINTENANCE && this.attrs[ZaDomain.A_zimbraDomainStatus] != ZaDomain.DOMAIN_STATUS_SUSPENDED) {                                                                                    
	    if(this.attrs[ZaDomain.A_zimbraNotebookAccount]) {
			var soapDoc = AjxSoapDoc.create("GetFolderRequest", "urn:zimbraMail", null);
			var getFolderCommand = new ZmCsfeCommand();
			var params = new Object();
			params.soapDoc = soapDoc;
			params.accountName = this.attrs[ZaDomain.A_zimbraNotebookAccount];
		
			var folderEl = soapDoc.set("folder", "");
			folderEl.setAttribute("l", ZaDomain.WIKI_FOLDER_ID);	
			try {
				this.parseNotebookFolderAcls(getFolderCommand.invoke(params));
			} catch (ex) {
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomain.loadMethod", null, false);
			}
		}
    }
}
ZaItem.loadMethods["ZaDomain"].push(ZaDomain.loadNotebookACLs);

ZaDomain.loadCatchAll = function () {
	if((this.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] && this.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] == "TRUE")
		|| (AjxUtil.isEmpty(this.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled]) && 
			(!AjxUtil.isEmpty(this._defaultValues.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled]) &&
				this._defaultValues.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] == "TRUE") )) {
		
		var acc = ZaAccount.getCatchAllAccount (this.name);
		if(!AjxUtil.isEmpty(acc) && !AjxUtil.isEmpty(acc.id) && !AjxUtil.isEmpty(acc.name)) {
			this [ZaAccount.A_zimbraMailCatchAllAddress] = acc;
		} else {
			this [ZaAccount.A_zimbraMailCatchAllAddress] = null;
		}
	} else {
		this [ZaAccount.A_zimbraMailCatchAllAddress] = null;
	}
}
ZaItem.loadMethods["ZaDomain"].push(ZaDomain.loadCatchAll);

ZaDomain.checkDomainMXRecord = 
function(obj, callback) {
	var soapDoc = AjxSoapDoc.create("CheckDomainMXRecordRequest", ZaZimbraAdmin.URN, null);
	var elBy = soapDoc.set("domain", obj.id);
	elBy.setAttribute("by", "id");
	var params = new Object();
	params.soapDoc = soapDoc;	
	if(callback) {
		params.asyncMode = true;
		params.callback = callback;
	}
	var reqMgrParams = {
		controller : (ZaApp.getInstance() ? ZaApp.getInstance().getCurrentController() : null),
		busyMsg : ZaMsg.BUSY_CHECKING_MX
	}
	ZaRequestMgr.invoke(params, reqMgrParams);
}


ZaDomain.aclXModel = {
	items: [
		{id:"acl",
			type:_OBJECT_,
			items: [
				{id:"r", type:_NUMBER_},
				{id:"w", type:_NUMBER_},
				{id:"d", type:_NUMBER_},
				{id:"i", type:_NUMBER_},
				{id:"a", type:_NUMBER_},				
				{id:"x", type:_NUMBER_}
		]},
		{id:"name", type:_STRING_},
		{id:"gt",  type:_STRING_}	
	]
}

ZaDomain.myXModel = {
	items: [
    	{id:"getAttrs",type:_LIST_},
    	{id:"setAttrs",type:_LIST_},
    	{id:"rights",type:_LIST_},	
		{id:"name", type:_STRING_, ref:"name"},
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp},
		{id:ZaDomain.A_domainName, type:_STRING_, ref:"attrs/" + ZaDomain.A_domainName, maxLength:255},
		{id:ZaDomain.A_zimbraPublicServiceHostname, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraPublicServiceHostname, maxLength:255},
		{id:ZaDomain.A_zimbraDNSCheckHostname, type:_COS_STRING_, ref:"attrs/" + ZaDomain.A_zimbraDNSCheckHostname, maxLength:255},		
		{id:ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_zimbraAdminConsoleDNSCheckEnabled},
        {id:ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled},
        {id:ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled},    
        {id:ZaDomain.A_zimbraAdminConsoleSkinEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_zimbraAdminSkinAddressEnabled},
        {id:ZaDomain.A_zimbraVirtualHostname, type:_LIST_, listItem:{type:_STRING_, maxLength:255}, ref:"attrs/" + ZaDomain.A_zimbraVirtualHostname},
         ZaItem.descriptionModelItem,   
        {id:ZaDomain.A_notes, type:_STRING_, ref:"attrs/" + ZaDomain.A_notes},
		{id:ZaDomain.A_domainDefaultCOSId, type:_STRING_, ref:"attrs/" + ZaDomain.A_domainDefaultCOSId},		
		{id:ZaDomain.A_zimbraGalMode, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraGalMode},
		{id:ZaDomain.A_zimbraGalMaxResults, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_zimbraGalMaxResults, maxInclusive:2147483647, minInclusive:1},					
		{id:ZaDomain.A_GALServerType, type:_STRING_, ref:"attrs/" + ZaDomain.A_GALServerType},
		{id:ZaDomain.A_GALSyncServerType, type:_STRING_, ref:"attrs/" + ZaDomain.A_GALSyncServerType},
		{id:ZaDomain.A_GALSyncUseGALSearch, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_GALSyncUseGALSearch},
		{id:ZaDomain.A_GalLdapFilter, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapFilter,required:true},
		{id:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraGalAutoCompleteLdapFilter},		
		{id:ZaDomain.A_GalLdapSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapSearchBase},
		{id:ZaDomain.A_UseBindPassword, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_UseBindPassword},
		{id:ZaDomain.A_SyncUseBindPassword, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_SyncUseBindPassword},
		{id:ZaDomain.A_GalLdapURL, type:_LIST_,  listItem:{type:_SHORT_URL_}, ref:"attrs/" + ZaDomain.A_GalLdapURL},
		{id:ZaDomain.A_zimbraGalSyncLdapURL, type:_LIST_,  listItem:{type:_SHORT_URL_}, ref:"attrs/" + ZaDomain.A_zimbraGalSyncLdapURL},
		{id:ZaDomain.A_GalLdapBindDn, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindDn},
		{id:ZaDomain.A_GalLdapBindPassword, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindPassword},
		{id:ZaDomain.A_GalLdapBindPasswordConfirm, type:_STRING_, ref:"attrs/" + ZaDomain.A_GalLdapBindPasswordConfirm},
		{id:ZaDomain.A_zimbraGalAccountId, type:_LIST_, listItem:{type:_STRING_}, ref:"attrs/" + ZaDomain.A_zimbraGalAccountId},
		{id:ZaDomain.A2_create_gal_acc, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:ZaDomain.A2_create_gal_acc},
		{id:ZaDomain.A2_new_gal_sync_account_name, type:_STRING_, ref:ZaDomain.A2_new_gal_sync_account_name},
		{id:ZaDomain.A2_new_internal_gal_ds_name, type:_STRING_, ref:ZaDomain.A2_new_internal_gal_ds_name},
		{id:ZaDomain.A2_new_external_gal_ds_name, type:_STRING_, ref:ZaDomain.A2_new_external_gal_ds_name},
		{id:ZaDomain.A2_new_internal_gal_polling_interval, type:_MLIFETIME_, ref:ZaDomain.A2_new_internal_gal_polling_interval},
		{id:ZaDomain.A2_new_external_gal_polling_interval, type:_MLIFETIME_, ref:ZaDomain.A2_new_external_gal_polling_interval},		
		{id:ZaDomain.A2_gal_sync_accounts, type:_LIST_, listItem:{type:_OBJECT_, items:ZaAccount.myXModel.items}, ref:ZaDomain.A2_gal_sync_accounts},
		{id:ZaDomain.A_AuthLdapUserDn, type:_STRING_,ref:"attrs/" + ZaDomain.A_AuthLdapUserDn},
		{id:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaDomain.A_zimbraAuthLdapStartTlsEnabled},
		{id:ZaDomain.A_AuthLDAPServerName, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLDAPServerName},
		{id:ZaDomain.A_AuthLDAPSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLDAPSearchBase},
		{id:ZaDomain.A_AuthLDAPServerPort, type:_NUMBER_, ref:"attrs/" + ZaDomain.A_AuthLDAPServerPort, maxInclusive:2147483647},
		{id:ZaDomain.A_AuthMech, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthMech},
		{id:ZaDomain.A_AuthLdapURL, type:_LIST_,  listItem:{type:_SHORT_URL_}, ref:"attrs/" + ZaDomain.A_AuthLdapURL},
		{id:ZaDomain.A_AuthADDomainName, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthADDomainName},
		{id:ZaDomain.A_AuthLdapSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLdapSearchBase},		
		{id:ZaDomain.A_AuthLdapSearchFilter, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLdapSearchFilter},		
		{id:ZaDomain.A_AuthLdapSearchBindDn, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLdapSearchBindDn},		
		{id:ZaDomain.A_AuthLdapSearchBindPassword, type:_STRING_, ref:"attrs/" + ZaDomain.A_AuthLdapSearchBindPassword},		
		{id:ZaDomain.A_zimbraDomainStatus, type:_STRING_, ref:"attrs/"+ZaDomain.A_zimbraDomainStatus},
		{id:ZaDomain.A_AuthTestUserName, type:_STRING_},
		{id:ZaDomain.A_AuthTestPassword, type:_STRING_},
		{id:ZaDomain.A_AuthTestMessage, type:_STRING_},
		{id:ZaDomain.A_AuthTestResultCode, type:_STRING_},
		{id:ZaDomain.A_AuthTestMessage, type:_STRING_},
		{id:ZaDomain.A_AuthComputedBindDn, type:_STRING_},
		{id:ZaDomain.A_GALSearchTestMessage, type:_STRING_},
		{id:ZaDomain.A_GALSyncTestMessage, type:_STRING_},
		{id:ZaDomain.A_GALSearchTestResultCode, type:_STRING_},
		{id:ZaDomain.A_GALSyncTestResultCode, type:_STRING_},		
		{id:ZaDomain.A_GALSampleQuery, type:_STRING_,required:true},
		{id:ZaDomain.A_AuthUseBindPassword, type:_STRING_,type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaDomain.A_AuthLdapSearchBindPasswordConfirm, type:_STRING_},				
		
		{id:ZaDomain.A_zimbraPrefTimeZoneId,type:_STRING_, ref:"attrs/"+ZaDomain.A_zimbraPrefTimeZoneId, choices:ZaSettings.timeZoneChoices},
        {id:ZaModel.currentStep, type:_NUMBER_, ref:ZaModel.currentStep, maxInclusive:2147483647},
		{id:ZaDomain.A2_acl_selection_cache, type:_LIST_},
		{id:ZaDomain.A_GALTestSearchResults, ref:ZaDomain.A_GALTestSearchResults, type:_LIST_, 
			listItem: {type:_OBJECT_, 
				items:[
					{id:"email", type:_STRING_},
					{id:"fullName", type:_STRING_},					
					{id:"firstName", type:_STRING_},										
					{id:"lastName", type:_STRING_}														
				]
			}
		},		
		{id:ZaDomain.A_NotebookTemplateDir, type:_STRING_, ref:ZaDomain.A_NotebookTemplateDir},
		{id:ZaDomain.A_NotebookTemplateFolder, type:_STRING_, ref:ZaDomain.A_NotebookTemplateFolder},
		{id:ZaDomain.A_NotebookAccountPassword, type:_STRING_},
		{id:ZaDomain.A_NotebookAccountPassword2, type:_STRING_},		
		{id:ZaDomain.A_CreateNotebook, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaDomain.A_OverwriteTemplates, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaDomain.A_zimbraNotebookAccount, type:_STRING_, ref:"attrs/" +ZaDomain.A_zimbraNotebookAccount},
		{id:ZaDomain.A_NotebookAllACLs, ref:"notebookAcls/"+ZaDomain.A_NotebookAllACLs, type:_OBJECT_,
			items: [
				{id:"r", type:_NUMBER_},
				{id:"w", type:_NUMBER_},
				{id:"d", type:_NUMBER_},
				{id:"i", type:_NUMBER_},
				{id:"a", type:_NUMBER_},				
				{id:"x", type:_NUMBER_}
			]
		},
		{id:ZaDomain.A_NotebookDomainACLs, ref:"notebookAcls/"+ZaDomain.A_NotebookDomainACLs, type:_OBJECT_,
			items: [
				{id:"r", type:_NUMBER_},
				{id:"w", type:_NUMBER_},
				{id:"d", type:_NUMBER_},
				{id:"i", type:_NUMBER_},
				{id:"a", type:_NUMBER_},				
				{id:"x", type:_NUMBER_}
			]
		},
		{id:ZaDomain.A_NotebookPublicACLs, ref:"notebookAcls/"+ZaDomain.A_NotebookPublicACLs, type:_OBJECT_,
			items: [
				{id:"r", type:_NUMBER_},
				{id:"w", type:_NUMBER_},
				{id:"d", type:_NUMBER_},
				{id:"i", type:_NUMBER_},
				{id:"a", type:_NUMBER_},				
				{id:"x", type:_NUMBER_}
			]
		}, 
		{id:ZaDomain.A_NotebookGroupACLs, ref:"notebookAcls/"+ZaDomain.A_NotebookGroupACLs, type:_LIST_,
			listItem:{type:_OBJECT_,
				items: [
					{id:"acl", /*type:_LIST_, 
						listItem:{*/
							type:_OBJECT_,
							items: [
								{id:"r", type:_NUMBER_},
								{id:"w", type:_NUMBER_},
								{id:"d", type:_NUMBER_},
								{id:"i", type:_NUMBER_},
								{id:"a", type:_NUMBER_},				
								{id:"x", type:_NUMBER_}
							]
//						}
					},
					{id:"name", type:_STRING_},
					{id:"gt",  type:_STRING_}
				]
			}
		}, 
		{id:ZaDomain.A_NotebookUserACLs, ref:"notebookAcls/"+ZaDomain.A_NotebookUserACLs, type:_LIST_,
			listItem:{type:_OBJECT_,
				items: [
					{id:"acl",/* type:_LIST_, 
						listItem:{*/
							type:_OBJECT_,
							items: [
								{id:"r", type:_NUMBER_},
								{id:"w", type:_NUMBER_},
								{id:"d", type:_NUMBER_},
								{id:"i", type:_NUMBER_},
								{id:"a", type:_NUMBER_},				
								{id:"x", type:_NUMBER_}
							]
						//}
					},
					{id:"name", type:_STRING_},
					{id:"gt",  type:_STRING_}
				]
			}
		},
		{id:ZaDomain.A_allNotebookACLS, ref:ZaDomain.A_allNotebookACLS, type:_LIST_,
			listItem:{type:_OBJECT_,
				items: [
					{id:"acl", type:_OBJECT_,
						items: [
							{id:"r", type:_NUMBER_},
							{id:"w", type:_NUMBER_},
							{id:"d", type:_NUMBER_},
							{id:"i", type:_NUMBER_},
							{id:"a", type:_NUMBER_},				
							{id:"x", type:_NUMBER_}
						]
					},
					{id:"name", type:_STRING_}, //null, domain name, group name, user name
					{id:"gt",  type:_STRING_} //grp, usr, dom, pub, all, guest
				]
			}
		},
      {id:ZaDomain.A_zimbraZimletDomainAvailableZimlets, type:_LIST_,
          ref:"attrs/" + ZaDomain.A_zimbraZimletDomainAvailableZimlets,
          dataType: _STRING_ ,outputType:_LIST_},
      { id:ZaAccount.A_zimbraMailCatchAllAddress, ref:ZaAccount.A_zimbraMailCatchAllAddress , type:_OBJECT_,items:[ {id:"id", type:_STRING_},{id:"name", type:_STRING_}] },
      { id:ZaDomain.A_zimbraDomainCOSMaxAccounts, ref:"attrs/" + ZaDomain.A_zimbraDomainCOSMaxAccounts ,
                 type:_LIST_ , dataType: _STRING_ ,outputType:_LIST_ },
      { id:ZaDomain.A_zimbraDomainFeatureMaxAccounts, ref:"attrs/" + ZaDomain.A_zimbraDomainFeatureMaxAccounts ,
                        type:_LIST_ , dataType: _STRING_ ,outputType:_LIST_ },

       //skin properties
      { id:ZaDomain.A_zimbraSkinForegroundColor, ref:"attrs/" + ZaDomain.A_zimbraSkinForegroundColor, type: _COS_STRING_ },
      { id:ZaDomain.A_zimbraSkinBackgroundColor, ref:"attrs/" + ZaDomain.A_zimbraSkinBackgroundColor, type: _COS_STRING_ },
      { id:ZaDomain.A_zimbraSkinSecondaryColor, ref:"attrs/" + ZaDomain.A_zimbraSkinSecondaryColor, type: _COS_STRING_ },
      { id:ZaDomain.A_zimbraSkinSelectionColor, ref:"attrs/" + ZaDomain.A_zimbraSkinSelectionColor, type: _COS_STRING_ },

      { id:ZaDomain.A_zimbraSkinLogoURL, ref:"attrs/" + ZaDomain.A_zimbraSkinLogoURL, type:_COS_STRING_ },
      { id:ZaDomain.A_zimbraSkinLogoLoginBanner, ref:"attrs/" + ZaDomain.A_zimbraSkinLogoLoginBanner, type:_COS_STRING_ },
      { id:ZaDomain.A_zimbraSkinLogoAppBanner, ref:"attrs/" + ZaDomain.A_zimbraSkinLogoAppBanner, type:_COS_STRING_ },

        //interop
       { id:ZaDomain.A_zimbraFreebusyExchangeAuthUsername, ref:"attrs/" + ZaDomain.A_zimbraFreebusyExchangeAuthUsername, type: _COS_STRING_ },
       { id:ZaDomain.A_zimbraFreebusyExchangeAuthPassword, ref:"attrs/" + ZaDomain.A_zimbraFreebusyExchangeAuthPassword, type: _COS_STRING_ },
       { id:ZaDomain.A_zimbraFreebusyExchangeAuthScheme, ref:"attrs/" + ZaDomain.A_zimbraFreebusyExchangeAuthScheme,
             type: _COS_ENUM_ , choices: [{value: "basic", label: ZaMsg.choice_basic}, {value: "form", label: ZaMsg.choice_form}]},
       { id:ZaDomain.A_zimbraFreebusyExchangeURL, ref:"attrs/" + ZaDomain.A_zimbraFreebusyExchangeURL, type: _COS_STRING_ } ,
       { id:ZaDomain.A_zimbraFreebusyExchangeUserOrg, ref:"attrs/" + ZaDomain.A_zimbraFreebusyExchangeUserOrg, type: _COS_STRING_ },
       {id:ZaDomain.A2_isTestingGAL, ref:ZaDomain.A2_isTestingGAL, type:_NUMBER_},
       {id:ZaDomain.A2_isTestingSync, ref:ZaDomain.A2_isTestingSync, type:_NUMBER_},
       {id:ZaDomain.A2_isTestingAuth, ref:ZaDomain.A2_isTestingAuth, type:_NUMBER_}
    ]
};


/**
 * Domain Level Account Limits Object, it is a client side only domain property and used
 * by both domain view and account view.  The value is based on the zimbraDomainCOSMaxAccounts
 *  zimbraDomainCOSMaxAccounts is a multi value attribute, its value is in format of "cosName:account_limits",
 * eg.
 *  zimbraDomainCOSMaxAccounts = professional:10
 *
 *  //this value is built on demand
 *
 *  domain.account_limits =
 *   {
 *      cosName : { max: 10,
 *                  used: 2 , //used value is got by the searchDirectory on this domain
 *                  available: 8
 *                  }
 *   }
 *
 **/

ZaDomain.prototype.getUsedDomainAccounts = function () {
    var total = 0 ;    
    var accountCountsByCoses = this.getAccountCountsByCoses();
    if (accountCountsByCoses && accountCountsByCoses.length > 0) {
      for (var i = 0 ; i < accountCountsByCoses.length; i ++) {
        var count = accountCountsByCoses[i]._content ;
        total += parseInt(count) ;
      }
    }
    return total ;
}

ZaDomain.prototype.getAccountCountsByCoses = function () {
    try {
        var soapDoc = AjxSoapDoc.create("CountAccountRequest", ZaZimbraAdmin.URN, null);
        var elBy = soapDoc.set("domain", this.name);
        elBy.setAttribute("by", "name");
        //var command = new ZmCsfeCommand();
        var params = new Object();
        params.soapDoc = soapDoc;

        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_COUNT_ACCOUNTS
        }
        var resp = ZaRequestMgr.invoke(params, reqMgrParams);
        var accountCountsByCoses = resp.Body.CountAccountResponse.cos ;
        return accountCountsByCoses ;
    }catch (ex) {
        ZaApp.getInstance().getCurrentController().popupErrorDialog(
                AjxMessageFormat.format(ZaMsg.ERROR_GET_USED_ACCOUNTS, [this.name]), ex);
    }
}

ZaDomain.prototype.updateUsedAccounts = function () {
    this.updateMaxAccounts() ;  //make sure all the defined cos in cosMaxAccounts are initialized

    //need to call the CountAccountRequest
    var accountCountsByCoses = this.getAccountCountsByCoses();
    if (accountCountsByCoses && accountCountsByCoses.length > 0) {
        for (var i = 0 ; i < accountCountsByCoses.length; i ++) {
            var aCosName =  accountCountsByCoses[i].name ;
            if (!this[ZaDomain.A2_account_limit][aCosName])
                this[ZaDomain.A2_account_limit][aCosName] = {} ;
            this[ZaDomain.A2_account_limit][aCosName].used =
                                            parseInt (accountCountsByCoses[i]._content) ;
        }
    }

    for (aCosName in this[ZaDomain.A2_account_limit]) {
        if (!this[ZaDomain.A2_account_limit][aCosName].used)
            this[ZaDomain.A2_account_limit][aCosName].used = 0;
    }
}

ZaDomain.prototype.getUsedAccounts =
function (cosName, refresh) {
    if (!this[ZaDomain.A2_account_limit]) this[ZaDomain.A2_account_limit] = {};
    if (!this[ZaDomain.A2_account_limit][cosName])  this[ZaDomain.A2_account_limit][cosName] = {} ;

    if (refresh || (this[ZaDomain.A2_account_limit][cosName].used == null)) {
        this.updateUsedAccounts();   
    }
 
    return this[ZaDomain.A2_account_limit][cosName].used ;
}

ZaDomain.prototype.getMaxAccounts = function (cosName, refresh) {
    if (! this [ZaDomain.A2_account_limit] )  this[ZaDomain.A2_account_limit] = {} ;

    //var cosName = ZaCos.getCosById (cosId).name ;
    if (!this[ZaDomain.A2_account_limit][cosName]) this[ZaDomain.A2_account_limit][cosName] = {} ;

    if (! this[ZaDomain.A2_account_limit][cosName].max || refresh) {
        //retrieve the total allowed accounts
       this.updateMaxAccounts ();
    }

    return  this[ZaDomain.A2_account_limit][cosName].max ;
}

//init or refresh the cos max accounts
ZaDomain.prototype.updateMaxAccounts = function () {
    this[ZaDomain.A2_account_limit] = {} ;
    
    var cosMaxAccounts = this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts];
    for (var i=0; i < cosMaxAccounts.length; i ++) {
        var val = cosMaxAccounts[i].split(":") ;
        var cos = ZaCos.getCosById (val[0]) ;
        if (cos == null) {
                ZaApp.getInstance().getCurrentController.popupErrorDialog(
                    AjxMessageFormat.format(ZaMsg.ERROR_INVALID_ACCOUNT_TYPE, [val[0]]));
            return ;
        }
        var n = cos.name ;

        this[ZaDomain.A2_account_limit][n] = {} ;
        this[ZaDomain.A2_account_limit][n].max = val [1] ;
    }
}

ZaDomain.prototype.getAvailableAccounts = function (cosName, refresh) {
    //var cosName = ZaCos.getCosById (cosId).name ;
    if (!this[ZaDomain.A2_account_limit][cosName]) this[ZaDomain.A2_account_limit][cosName] = {} ;
//    if (! this [ZaDomain.A2_account_limit][cosName].available
//           || refresh ) {
        //retrieve the used accounts
        var used = this.getUsedAccounts (cosName, refresh);
        var max = this.getMaxAccounts (cosName, refresh) ;
        this [ZaDomain.A2_account_limit][cosName].available = max - used ;
//    }

    return this[ZaDomain.A2_account_limit][cosName].available;
}

//Account types is only available when ZimbraDomainCOSMaxAccounts are set
ZaDomain.prototype.getAccountTypes = function () {
    var types = [] ;
    var cosMaxAccounts = this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts];
    if (cosMaxAccounts && cosMaxAccounts.length > 0 ) {
        for (var i=0; i < cosMaxAccounts.length; i ++) {
            var val = cosMaxAccounts[i].split(":") ;
            types.push (val[0]) ;
        }
    }

    return types ;
}

ZaDomain.getTotalLimitsPerAccountTypes = function (cosMaxAccounts) {
    var total = 0 ;
    //var cosMaxAccounts = this.attrs[ZaDomain.A_zimbraDomainCOSMaxAccounts];
    if (cosMaxAccounts && cosMaxAccounts.length > 0 ) {
        for (var i=0; i < cosMaxAccounts.length; i ++) {
            var val = cosMaxAccounts[i].split(":") ;
            total += new Number (val [1]);
        }
    }

    return total ;
}

ZaDomain.searchAccountsInDomain =
function (domainName) {
    if (domainName) {
        var controller = ZaApp.getInstance().getSearchListController();
        var busyId = Dwt.getNextId();
        var callback =  new AjxCallback(controller, controller.searchCallback, {limit:controller.RESULTSPERPAGE,show:true,busyId:busyId});
        controller._currentQuery = "" ;
        var searchTypes = [ZaSearch.ACCOUNTS, ZaSearch.DLS, ZaSearch.ALIASES, ZaSearch.RESOURCES] ;

        if(controller.setSearchTypes)
            controller.setSearchTypes(searchTypes);
        var searchParams = {
                query:controller._currentQuery,
                domain: domainName,
                types:searchTypes,
                attrs:AjxBuffer.concat(ZaAlias.searchAttributes,",",
                	ZaDistributionList.searchAttributes,",",
                	ZaResource.searchAttributes,",",
                	ZaSearch.standardAttributes),
                callback:callback,
                controller: controller,
				showBusy:true,
				busyId:busyId,
				busyMsg:ZaMsg.BUSY_SEARCHING,
				skipCallbackIfCancelled:true                
        }
        ZaSearch.searchDirectory(searchParams);
    }else {
        var currentController = ZaApp.getInstance().getCurrentController () ;
        currentController.popupErrorDialog(ZaMsg.ERROR_NO_DOMAIN_NAME) ;
    }
}

