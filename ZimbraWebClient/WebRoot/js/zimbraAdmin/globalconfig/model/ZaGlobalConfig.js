/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

ZaGlobalConfig = function() {
	ZaItem.call(this,"ZaGlobalConfig");
	this.attrs = new Object();
	this.type = ZaItem.GLOBAL_CONFIG;
//	this.attrsInternal = new Object();	
	this.load();
}

ZaGlobalConfig.prototype = new ZaItem;
ZaGlobalConfig.prototype.constructor = ZaGlobalConfig;
ZaItem.loadMethods["ZaGlobalConfig"] = new Array();
ZaItem.modifyMethods["ZaGlobalConfig"] = new Array();

ZaGlobalConfig.MTA_RESTRICTIONS = [
	"reject_invalid_hostname", "reject_non_fqdn_hostname", "reject_non_fqdn_sender",
	"reject_unknown_client", "reject_unknown_hostname", "reject_unknown_sender_domain"
];

//general
ZaGlobalConfig.A_zimbraLastLogonTimestampFrequency = "zimbraLastLogonTimestampFrequency";
ZaGlobalConfig.A_zimbraDefaultDomainName = "zimbraDefaultDomainName";
ZaGlobalConfig.A_zimbraScheduledTaskNumThreads = "zimbraScheduledTaskNumThreads" ;
ZaGlobalConfig.A_zimbraMailPurgeSleepInterval = "zimbraMailPurgeSleepInterval" ;
		
// attachements
ZaGlobalConfig.A_zimbraAttachmentsBlocked = "zimbraAttachmentsBlocked";
ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient = "zimbraMtaBlockedExtensionWarnRecipient";
ZaGlobalConfig.A_zimbraMtaBlockedExtension = "zimbraMtaBlockedExtension";
ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension = "zimbraMtaCommonBlockedExtension";

// MTA
ZaGlobalConfig.A_zimbraMtaAuthEnabled = "zimbraMtaAuthEnabled";
ZaGlobalConfig.A_zimbraMtaTlsAuthOnly = "zimbraMtaTlsAuthOnly";
ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled  = "zimbraMtaDnsLookupsEnabled";
ZaGlobalConfig.A_zimbraMtaMaxMessageSize = "zimbraMtaMaxMessageSize";
ZaGlobalConfig.A_zimbraMtaRelayHost = "zimbraMtaRelayHost";
ZaGlobalConfig.A_zimbraMtaMyNetworks = "zimbraMtaMyNetworks";
//ZaGlobalConfig.A_zimbraMtaRelayHostInternal = "__zimbraMtaRelayHost";
//ZaGlobalConfig.A_zimbraMtaRelayPortInternal = "__zimbraMtaRelayPort";
ZaGlobalConfig.A_zimbraComponentAvailable = "zimbraComponentAvailable";
ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP = "zimbraSmtpSendAddOriginatingIP";
ZaGlobalConfig.A_zimbraDNSCheckHostname = "zimbraDNSCheckHostname";
ZaGlobalConfig.A_zimbraAdminConsoleDNSCheckEnabled = "zimbraAdminConsoleDNSCheckEnabled";
ZaGlobalConfig.A_zimbraAdminConsoleCatchAllAddressEnabled = "zimbraAdminConsoleCatchAllAddressEnabled";
ZaGlobalConfig.A_zimbraAdminConsoleSkinEnabled = "zimbraAdminConsoleSkinEnabled";
ZaGlobalConfig.A_zimbraAdminConsoleLDAPAuthEnabled = "zimbraAdminConsoleLDAPAuthEnabled" ;

// --protocol checks
ZaGlobalConfig.A_zimbraMtaRestriction = "zimbraMtaRestriction";
ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_invalid_hostname";
ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_non_fqdn_hostname";
ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_non_fqdn_sender";
// -- dns checks
ZaGlobalConfig.A_zimbraMtaRejectUnknownClient = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_unknown_client";
ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_unknown_hostname";
ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_unknown_sender_domain";
//rbl check
ZaGlobalConfig.A_zimbraMtaRejectRblClient = "_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_reject_rbl_client";
  
//Domain
ZaGlobalConfig.A_zimbraGalLdapFilterDef = "zimbraGalLdapFilterDef";
ZaGlobalConfig.A_zimbraGalMaxResults = "zimbraGalMaxResults";
ZaGlobalConfig.A_zimbraNotebookAccount = "zimbraNotebookAccount";
//Server
ZaGlobalConfig.A_zimbraLmtpNumThreads = "zimbraLmtpNumThreads";
ZaGlobalConfig.A_zimbraLmtpBindPort = "zimbraLmtpBindPort";
ZaGlobalConfig.A_zimbraPop3NumThreads = "zimbraPop3NumThreads";
ZaGlobalConfig.A_zimbraPop3BindPort = "zimbraPop3BindPort";
ZaGlobalConfig.A_zimbraRedologEnabled = "zimbraRedologEnabled";
ZaGlobalConfig.A_zimbraRedologLogPath = "zimbraRedologLogPath";
ZaGlobalConfig.A_zimbraRedologArchiveDir = "zimbraRedologArchiveDir";
ZaGlobalConfig.A_zimbraRedologBacklogDir = "zimbraRedologBacklogDir";
ZaGlobalConfig.A_zimbraRedologRolloverFileSizeKB = "zimbraRedologRolloverFileSizeKB";
ZaGlobalConfig.A_zimbraRedologFsyncIntervalMS = "zimbraRedologFsyncIntervalMS";
ZaGlobalConfig.A_zimbraFileUploadMaxSize = "zimbraFileUploadMaxSize"

// smtp
ZaGlobalConfig.A_zimbraSmtpHostname = "zimbraSmtpHostname";
ZaGlobalConfig.A_zimbraSmtpPort = "zimbraSmtpPort";
ZaGlobalConfig.A_zimbraSmtpTimeout = "zimbraSmtpTimeout";
// pop
ZaGlobalConfig.A_zimbraPop3BindPort="zimbraPop3BindPort";
ZaGlobalConfig.A_zimbraPop3ServerEnabled = "zimbraPop3ServerEnabled";
ZaGlobalConfig.A_zimbraPop3SSLBindPort = "zimbraPop3SSLBindPort";
ZaGlobalConfig.A_zimbraPop3SSLServerEnabled = "zimbraPop3SSLServerEnabled";
ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled = "zimbraPop3CleartextLoginEnabled";
// imap
ZaGlobalConfig.A_zimbraImapBindPort = "zimbraImapBindPort";
ZaGlobalConfig.A_zimbraImapServerEnabled = "zimbraImapServerEnabled";
ZaGlobalConfig.A_zimbraImapNumThreads = "zimbraImapNumThreads"
ZaGlobalConfig.A_zimbraImapSSLBindPort = "zimbraImapSSLBindPort";
ZaGlobalConfig.A_zimbraImapSSLServerEnabled = "zimbraImapSSLServerEnabled";
ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled = "zimbraImapCleartextLoginEnabled";
// anti-spam
ZaGlobalConfig.A_zimbraSpamKillPercent = "zimbraSpamKillPercent";
ZaGlobalConfig.A_zimbraSpamTagPercent = "zimbraSpamTagPercent";
ZaGlobalConfig.A_zimbraSpamSubjectTag = "zimbraSpamSubjectTag";
// anti-virus
ZaGlobalConfig.A_zimbraVirusWarnRecipient = "zimbraVirusWarnRecipient";
ZaGlobalConfig.A_zimbraVirusWarnAdmin = "zimbraVirusWarnAdmin";
ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency = "zimbraVirusDefinitionsUpdateFrequency";
ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive = "zimbraVirusBlockEncryptedArchive";
//immutable attrs
ZaGlobalConfig.A_zimbraAccountClientAttr = "zimbraAccountClientAttr";
ZaGlobalConfig.A_zimbraServerInheritedAttr = "zimbraServerInheritedAttr";
ZaGlobalConfig.A_zimbraDomainInheritedAttr = "zimbraDomainInheritedAttr";
ZaGlobalConfig.A_zimbraCOSInheritedAttr = "zimbraCOSInheritedAttr";
ZaGlobalConfig.A_zimbraGalLdapAttrMap = "zimbraGalLdapAttrMap";
ZaGlobalConfig.A_zimbraGalLdapFilterDef = "zimbraGalLdapFilterDef";

//mailproxy
ZaGlobalConfig.A_zimbraImapProxyBindPort="zimbraImapProxyBindPort";
ZaGlobalConfig.A_zimbraImapSSLProxyBindPort="zimbraImapSSLProxyBindPort";
ZaGlobalConfig.A_zimbraPop3ProxyBindPort="zimbraPop3ProxyBindPort";
ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort="zimbraPop3SSLProxyBindPort";
ZaGlobalConfig.A_zimbraReverseProxyLookupTarget = "zimbraReverseProxyLookupTarget";
// others
ZaGlobalConfig.A_zimbraInstalledSkin = "zimbraInstalledSkin";
ZaGlobalConfig.A_zimbraNewExtension = "_zimbraNewExtension";

ZaGlobalConfig.A_originalMonitorHost = "_originalMonitorHost";
ZaGlobalConfig.A_currentMonitorHost = "_currentMonitorHost";

//interop
ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername = "zimbraFreebusyExchangeAuthUsername" ;
ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword = "zimbraFreebusyExchangeAuthPassword" ;
ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme  = "zimbraFreebusyExchangeAuthScheme" ;
ZaGlobalConfig.A_zimbraFreebusyExchangeURL ="zimbraFreebusyExchangeURL";
ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg = "zimbraFreebusyExchangeUserOrg"  ;

//Skin Properties
ZaGlobalConfig.A_zimbraSkinForegroundColor = "zimbraSkinForegroundColor" ;
ZaGlobalConfig.A_zimbraSkinBackgroundColor = "zimbraSkinBackgroundColor" ;
ZaGlobalConfig.A_zimbraSkinSecondaryColor = "zimbraSkinSecondaryColor" ;
ZaGlobalConfig.A_zimbraSkinSelectionColor  = "zimbraSkinSelectionColor" ;
ZaGlobalConfig.A_zimbraSkinLogoURL ="zimbraSkinLogoURL" ;
ZaGlobalConfig.A_zimbraSkinLogoLoginBanner = "zimbraSkinLogoLoginBanner" ;
ZaGlobalConfig.A_zimbraSkinLogoAppBanner = "zimbraSkinLogoAppBanner" ;
ZaGlobalConfig.A2_blocked_extension_selection = "blocked_extension_selection";
ZaGlobalConfig.A2_common_extension_selection = "common_extension_selection";

ZaGlobalConfig.__configInstance = null;
ZaGlobalConfig.isDirty = true;

ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT = "checkExchangeAuthConfig"
ZaGlobalConfig.getInstance = function(refresh) {
	if(refresh || ZaGlobalConfig.isDirty || !ZaGlobalConfig.__configInstance) {
		ZaGlobalConfig.__configInstance = new ZaGlobalConfig();
		ZaGlobalConfig.isDirty = false;
	}
	return ZaGlobalConfig.__configInstance;
}

ZaGlobalConfig.loadMethod = 
function(by, val) {
	var soapDoc = AjxSoapDoc.create("GetAllConfigRequest", ZaZimbraAdmin.URN, null);
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
	}	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_CONFIG
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllConfigResponse;
	this.initFromJS(resp);	
}
ZaItem.loadMethods["ZaGlobalConfig"].push(ZaGlobalConfig.loadMethod);

ZaGlobalConfig.prototype.initFromJS = function(obj) {
	ZaItem.prototype.initFromJS.call(this, obj);
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = [this.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension]];
	}
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension] = [this.attrs[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension]];
	}
		
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost])) {
		this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost] = [this.attrs[ZaGlobalConfig.A_zimbraMtaRelayHost]];
	}
	
	if(AjxUtil.isString(this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname])) {
		this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname] = [this.attrs[ZaGlobalConfig.A_zimbraSmtpHostname]];
	}	
	// convert available components to hidden fields for xform binding
	var components = this.attrs[ZaGlobalConfig.A_zimbraComponentAvailable];
	if (components) {
		if (AjxUtil.isString(components)) {
			components = [ components ];
		}
		for (var i = 0; i < components.length; i++) {
			var component = components[i];
			this.attrs["_"+ZaGlobalConfig.A_zimbraComponentAvailable+"_"+component] = true;
		}
	}
	
	//init list of RBLs
	this.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient] = [];
	
	// convert restrictions to hidden fields for xform binding
	var restrictions = this.attrs[ZaGlobalConfig.A_zimbraMtaRestriction];
	if (restrictions) {
		if (AjxUtil.isString(restrictions)) {
			restrictions = [ restrictions ];
		}
		for (var i = 0; i < restrictions.length; i++) {
			if(restrictions[i].indexOf("reject_rbl_client")>-1) {
				var restriction = restrictions[i];
				var chunks = restriction.split(" ");
				if(chunks && chunks.length>0) {
					this.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].push(chunks[1]);
				}
			} else {
				var restriction = restrictions[i];
				this.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction] = true;
			}
		}
	}
	if(this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] != null && !(this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] instanceof Array)) {
		this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin] = [this.attrs[ZaGlobalConfig.A_zimbraInstalledSkin]];
	}
}

//ZaGlobalConfig.prototype.modify = 
ZaGlobalConfig.modifyMethod = function (mods) {
	var soapDoc = AjxSoapDoc.create("ModifyConfigRequest", ZaZimbraAdmin.URN, null);
	for (var aname in mods) {
		//multy value attribute
		if(mods[aname] instanceof Array) {
			var cnt = mods[aname].length;
			if(cnt > 0) {
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
			else {
				var attr = soapDoc.set("a");
				attr.setAttribute("n", aname);
			}
		} else {
			//bug fix 10354: ingnore the changed ZaLicense Properties
			if ((typeof ZaLicense == "function") && (ZaSettings.LICENSE_ENABLED)){
				if (ZaUtil.findValueInObjArrByPropertyName (ZaLicense.myXModel.items, aname, "id") > -1 ){
					continue ;
				}
			}
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	command.invoke(params);
	ZaGlobalConfig.isDirty = true;
}
ZaItem.modifyMethods["ZaGlobalConfig"].push(ZaGlobalConfig.modifyMethod);

// REVISIT: Move to a common location if needed by others
LifetimeNumber_XFormItem = function() {}
XModelItemFactory.createItemType("_LIFETIME_NUMBER_", "lifetime_number", LifetimeNumber_XFormItem, Number_XModelItem);
LifetimeNumber_XFormItem.prototype.validateType = function(value) {
	// strip off lifetime char (e.g. 's', 'h', 'd', ...)
	var number = value.substring(0, value.length - 1);
	this.validateNumber(number);
	return value;
}

ZaGlobalConfig.myXModel = {
	items:[
	  	// ...other...
		{ id:ZaGlobalConfig.A_zimbraGalMaxResults, ref:"attrs/" + ZaGlobalConfig.A_zimbraGalMaxResults , type:_NUMBER_, minInclusive: 0 },
		{ id:ZaGlobalConfig.A_zimbraDefaultDomainName, ref:"attrs/" + ZaGlobalConfig.A_zimbraDefaultDomainName, type:_STRING_, maxLength: 256},
		{ id:ZaGlobalConfig.A_zimbraScheduledTaskNumThreads, ref:"attrs/" + ZaGlobalConfig.A_zimbraScheduledTaskNumThreads , type:_NUMBER_, minInclusive: 1 },
		{ id:ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, type:_MLIFETIME_, ref:"attrs/"+ZaGlobalConfig.A_zimbraMailPurgeSleepInterval},
		
		{ id:ZaGlobalConfig.A_currentMonitorHost, ref: "attrs/"+ZaGlobalConfig.A_currentMonitorHost, type: _STRING_ },
		// attachments
		{ id:ZaGlobalConfig.A_zimbraAttachmentsBlocked, ref:"attrs/" + ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{ id:ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
		{ id:ZaGlobalConfig.A_zimbraMtaBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _LIST_, dataType: _STRING_ },
		{ id:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type: _LIST_, dataType: _STRING_ },
		// MTA
		{ id:ZaGlobalConfig.A_zimbraMtaAuthEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		{ id:ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		{ id:ZaGlobalConfig.A_zimbraSmtpHostname, ref:"attrs/" + ZaGlobalConfig.A_zimbraSmtpHostname, type:_LIST_, listItem:{ type:_HOSTNAME_OR_IP_, maxLength: 256} },
		{ id:ZaGlobalConfig.A_zimbraSmtpPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraSmtpPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraMtaMaxMessageSize, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _FILE_SIZE_, units: AjxUtil.SIZE_KILOBYTES, required: true },
		{ id:ZaGlobalConfig.A_zimbraFileUploadMaxSize, ref:"attrs/" + ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _FILE_SIZE_, units: AjxUtil.SIZE_KILOBYTES },
		{ id:ZaGlobalConfig.A_zimbraMtaRelayHost, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRelayHost, type:_LIST_, listItem:{ type: _HOSTNAME_OR_IP_, maxLength: 256 }},
		{ id:ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP, ref: "attrs/" + ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		
		{ id:ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		// -- protocol checks
		{ id:ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname, type: _ENUM_, choices: [false,true] },
		{ id:ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname, type: _ENUM_, choices: [false,true] },
		{ id:ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender, type: _ENUM_, choices: [false,true] },
		// -- dns checks
		{ id:ZaGlobalConfig.A_zimbraMtaRejectUnknownClient, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectUnknownClient, type: _ENUM_, choices: [false,true] },
		{ id:ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname, type: _ENUM_, choices: [false,true] },
		{ id:ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain, type: _ENUM_, choices: [false,true] },
		{id:ZaGlobalConfig.A_zimbraDNSCheckHostname, type:_STRING_, ref:"attrs/" + ZaGlobalConfig.A_zimbraDNSCheckHostname, maxLength:255},		
		{id:ZaGlobalConfig.A_zimbraAdminConsoleDNSCheckEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaGlobalConfig.A_zimbraAdminConsoleDNSCheckEnabled},

        {id:ZaGlobalConfig.A_zimbraAdminConsoleCatchAllAddressEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaGlobalConfig.A_zimbraAdminConsoleCatchAllAddressEnabled},
		{id:ZaGlobalConfig.A_zimbraAdminConsoleSkinEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaGlobalConfig.A_zimbraAdminConsoleSkinEnabled},
        {id:ZaGlobalConfig.A_zimbraAdminConsoleLDAPAuthEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaGlobalConfig.A_zimbraAdminConsoleLDAPAuthEnabled},    

        //rbl check
		{ id:ZaGlobalConfig.A_zimbraMtaRejectRblClient, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRejectRblClient, type: _LIST_, listItem:{type:_STRING_}},
		// smtp
		{ id:ZaGlobalConfig.A_zimbraSmtpTimeout, ref:"attrs/" + ZaGlobalConfig.A_zimbraSmtpTimeout, type:_NUMBER_, minInclusive: 0 },
		// pop
		{ id:ZaGlobalConfig.A_zimbraPop3ServerEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3ServerEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{ id:ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{ id:ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},				
		{ id:ZaGlobalConfig.A_zimbraPop3BindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3BindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraPop3SSLBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3SSLBindPort, type:_PORT_ },
		// imap
		{ id:ZaGlobalConfig.A_zimbraImapServerEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapServerEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},						
		{ id:ZaGlobalConfig.A_zimbraImapSSLServerEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},								
		{ id:ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},										
        { id:ZaGlobalConfig.A_zimbraImapNumThreads, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapNumThreads, type:_INT_,  minInclusive: 0, maxInclusive:2147483647  },
		{ id:ZaGlobalConfig.A_zimbraImapBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraImapSSLBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_PORT_ },
		// anti-spam
	  	{ id:ZaGlobalConfig.A_zimbraSpamCheckEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraSpamCheckEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
	  	{ id:ZaGlobalConfig.A_zimbraSpamKillPercent, ref:"attrs/" + ZaGlobalConfig.A_zimbraSpamKillPercent, type: _PERCENT_, fractionDigits: 0 },
	  	{ id:ZaGlobalConfig.A_zimbraSpamTagPercent, ref:"attrs/" + ZaGlobalConfig.A_zimbraSpamTagPercent, type: _PERCENT_, fractionDigits: 0 },
	  	{ id:ZaGlobalConfig.A_zimbraSpamSubjectTag, ref:"attrs/" + ZaGlobalConfig.A_zimbraSpamSubjectTag, type: _STRING_, whiteSpace: 'collapse', maxLength: 32 },
	  	// anti-virus
	  	{ id:ZaGlobalConfig.A_zimbraVirusCheckEnabled, ref:"attrs/" + ZaGlobalConfig.A_zimbraVirusCheckEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
	  	{ id:ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, ref:"attrs/" + ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, type: _LIFETIME_NUMBER_, minInclusive: 0, fractionDigits: 0 },
	  	{ id:ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, ref:"attrs/" + ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
	  	{ id:ZaGlobalConfig.A_zimbraVirusWarnAdmin, ref:"attrs/" + ZaGlobalConfig.A_zimbraVirusWarnAdmin, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
	  	{ id:ZaGlobalConfig.A_zimbraVirusWarnRecipient, ref:"attrs/" + ZaGlobalConfig.A_zimbraVirusWarnRecipient, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
	  	//proxy
		{ id:ZaGlobalConfig.A_zimbraImapProxyBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraPop3ProxyBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3ProxyBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraLmtpBindPort, ref:"attrs/" + ZaGlobalConfig.A_zimbraLmtpBindPort, type:_PORT_ },
		{ id:ZaGlobalConfig.A_zimbraPop3NumThreads, ref:"attrs/" + ZaGlobalConfig.A_zimbraPop3NumThreads, type:_INT_, minInclusive: 0, maxInclusive:2147483647 },		
		{ id:ZaGlobalConfig.A_zimbraInstalledSkin, ref:"attrs/" + ZaGlobalConfig.A_zimbraInstalledSkin, type:_LIST_, listItem:{type:_STRING_}},

        //skin properties
        { id:ZaGlobalConfig.A_zimbraSkinForegroundColor, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinForegroundColor, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraSkinBackgroundColor, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinBackgroundColor, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraSkinSecondaryColor, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinSecondaryColor, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraSkinSelectionColor, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinSelectionColor, type: _STRING_ },

        { id:ZaGlobalConfig.A_zimbraSkinLogoURL, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinLogoURL, type:_STRING_ },
        { id:ZaGlobalConfig.A_zimbraSkinLogoLoginBanner, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinLogoLoginBanner, type:_STRING_ },
        { id:ZaGlobalConfig.A_zimbraSkinLogoAppBanner, ref:"attrs/" + ZaGlobalConfig.A_zimbraSkinLogoAppBanner, type:_STRING_ },

         //interop
        { id:ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername, ref:"attrs/" + ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, ref:"attrs/" + ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, ref:"attrs/" + ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme,
            type: _ENUM_, choices: [{value: "basic", label: ZaMsg.choice_basic}, {value: "form", label: ZaMsg.choice_form}]},
        { id:ZaGlobalConfig.A_zimbraFreebusyExchangeURL, ref:"attrs/" + ZaGlobalConfig.A_zimbraFreebusyExchangeURL, type: _STRING_ },
        { id:ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg, ref:"attrs/" + ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg, type: _STRING_ },
        {id:ZaGlobalConfig.A2_blocked_extension_selection, type:_LIST_},
        {id:ZaGlobalConfig.A2_common_extension_selection, type:_LIST_}

    ]
}
