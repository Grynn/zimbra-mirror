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

ZaApplianceSettings = function() {
	ZaItem.call(this,"ZaApplianceSettings");
	this.attrs = new Object();
	this.type = ZaItem.GLOBAL_CONFIG;
	this.attrsToGet = [ZaGlobalConfig.A_zimbraFileUploadMaxSize,ZaGlobalConfig.A_zimbraMtaRelayHost,ZaGlobalConfig.A_zimbraAttachmentsBlocked, 
	                   ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient,ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,
	                   ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraDefaultDomainName];
}

ZaApplianceSettings.prototype = new ZaItem;
ZaApplianceSettings.prototype.constructor = ZaApplianceSettings;
ZaItem.loadMethods["ZaApplianceSettings"] = new Array();
ZaItem.modifyMethods["ZaApplianceSettings"] = new Array();
ZaApplianceSettings.license = "license";
ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly = "zimbraAttachmentsViewInHtmlOnly";
ZaApplianceSettings.A_certs = "certs";
ZaApplianceSettings.A_serverName = "serverName";
ZaApplianceSettings.A_server = "server";
ZaApplianceSettings.myXModel = {
		items: [
		    {id:ZaApplianceSettings.A_serverName, type: _STRING_, ref:ZaApplianceSettings.A_server + "/attrs/" + ZaServer.A_ServiceHostname},    
			//license
			{id:ZaApplianceLicense.A_accountsLimit, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_accountsLimit},
			{id:ZaApplianceLicense.A_attachmentConversionEnabled, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_attachmentConversionEnabled},
			{id:ZaApplianceLicense.A_backupEnabled, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_backupEnabled },
			{id:ZaApplianceLicense.A_crossMailboxSearchEnabled, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_crossMailboxSearchEnabled },
			{id:ZaApplianceLicense.A_hierarchicalStorageManagementEnabled, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_hierarchicalStorageManagementEnabled },	
			{id:ZaApplianceLicense.A_iSyncAccountsLimit, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_iSyncAccountsLimit },
			{id:ZaApplianceLicense.A_installType, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_installType },
			{id:ZaApplianceLicense.A_issuedOn, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_issuedOn },
			{id:ZaApplianceLicense.A_issuedToEmail, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_issuedToEmail },
			{id:ZaApplianceLicense.A_issuedToName, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_issuedToName },		
			{id:ZaApplianceLicense.A_licenseId, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_licenseId },
			{id:ZaApplianceLicense.A_MAPIConnectorAccountsLimit, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_MAPIConnectorAccountsLimit },
			{id:ZaApplianceLicense.A_mobileSyncEnabled, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_mobileSyncEnabled},
			{id:ZaApplianceLicense.A_mobileSyncAccountsLimit, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_mobileSyncAccountsLimit },
			{id:ZaApplianceLicense.A_resellerName, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_resellerName },
			{id:ZaApplianceLicense.A_validFrom, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_validFrom },
			{id:ZaApplianceLicense.A_validUntil, type: _STRING_, ref:ZaApplianceSettings.license + "/" + ZaApplianceLicense.A_validUntil },
			{id:ZaApplianceLicense.InstallStatusMsg, type: _STRING_, ref:ZaApplianceSettings.license + "/"+ ZaApplianceLicense.InstallStatusMsg},
			{id:ZaApplianceLicense.InstallStatusCode, type: _STRING_, ref:ZaApplianceSettings.license + "/"+ ZaApplianceLicense.InstallStatusCode},
			{id:ZaApplianceLicense.Info_TotalAccounts, type: _STRING_, ref:ZaApplianceSettings.license + "/"+ ZaApplianceLicense.Info_TotalAccounts},
			
	       //config
			{id:ZaGlobalConfig.A_zimbraFileUploadMaxSize, ref:"attrs/" + ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _FILE_SIZE_, units: AjxUtil.SIZE_KILOBYTES },
			{id:ZaGlobalConfig.A_zimbraMtaRelayHost, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaRelayHost, type:_LIST_, listItem:{ type: _HOSTNAME_OR_IP_, maxLength: 256 }},
			{id:ZaGlobalConfig.A_zimbraAttachmentsBlocked, ref:"attrs/" + ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
			{id:ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
			{id:ZaGlobalConfig.A_zimbraMtaBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _LIST_, dataType: _STRING_ },
			{id:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, ref:"attrs/" + ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type: _LIST_, dataType: _STRING_ },
			{id:ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, ref:"attrs/" + ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
			{id:ZaGlobalConfig.A_zimbraDefaultDomainName, ref:"attrs/" + ZaGlobalConfig.A_zimbraDefaultDomainName, type:_STRING_, maxLength: 256},
	        {id:ZaGlobalConfig.A2_blocked_extension_selection, type:_LIST_},
	        {id:ZaGlobalConfig.A2_common_extension_selection, type:_LIST_},			
			//certificates
			{id:ZaApplianceSettings.A_certs, ref:ZaApplianceSettings.A_certs, type:_LIST_, 
				listItem:{type:_OBJECT_,items:ZaApplianceSSLCert.myXModel.items}
			}	
		]
};

ZaApplianceSettings.loadMethod = 
function(by, val) {
	var soapDoc, params, resp;
	
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
    soapDoc.setMethodAttribute("onerror", "continue");
	
    var getCfgDoc = soapDoc.set("GetAllConfigRequest", null, null, ZaZimbraAdmin.URN);
    getCfgDoc.setAttribute("attrs", this.attrsToGet.join(","));
    
    var getLicDoc = soapDoc.set("GetLicenseRequest", null, null, ZaZimbraAdmin.URN);
	
    var getCertsDoc = soapDoc.set("GetCertRequest", null, null, ZaZimbraAdmin.URN);
	getCertsDoc.setAttribute("type", "all");
	getCertsDoc.setAttribute("server", ZaDashBoard.server.id);
	
	this[ZaApplianceSettings.license] = new ZaApplianceLicense();
	this[ZaApplianceSettings.A_certs] = [];
	params = new Object();
	params.soapDoc = soapDoc;
	var busyId = Dwt.getNextId();
	reqMgrParams.busyId = busyId;
	reqMgrParams.showBusy = true;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_dashboard.BUSY_LOADING_SETTINGS;
	reqMgrParams.delay = 0;
    var hasError = false ;
    var lastException;
	try {
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaApplianceSettings.loadMethod", null, false);
		    hasError  = true ;
            lastException = ex ;
        } else if(respObj.Body.BatchResponse.Fault) {
			var fault = respObj.Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
		
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaApplianceSettings.loadMethod", null, false);
                hasError = true ;
                lastException = ex ;
            }
		} else {
			var batchResp = respObj.Body.BatchResponse;
			
			if(batchResp.GetAllConfigResponse) {
				resp = batchResp.GetAllConfigResponse[0];
				this.initFromJS(resp);
			}
			
			if(batchResp.GetLicenseResponse) {
				resp = batchResp.GetLicenseResponse[0];
				if(resp && resp.license && resp.license[0]) {
					this[ZaApplianceSettings.license].initFromJS(resp.license[0]);
				}
				if(resp && resp.info && resp.info[0]) {
					this[ZaApplianceSettings.license].initFromJS(resp.info[0]);
				}
			}
			
			if(batchResp.GetCertResponse) {
				resp = batchResp.GetCertResponse[0];
				if(resp && resp.cert && resp.cert.length) {
					var cnt = resp.cert.length;
					for(var i=0;i<cnt;i++) {
						if(resp.cert[i]) {
							var certObj = {};
							ZaApplianceSSLCert.initFromJS.call(certObj,resp.cert[i]);
						}
						this[ZaApplianceSettings.A_certs].push(certObj);
					}
				}
			}			
		}	
	    if (hasError) {
	        throw lastException;
	    }
	} catch (ex) {
		throw(ex);
	}
}
ZaItem.loadMethods["ZaApplianceSettings"].push(ZaApplianceSettings.loadMethod);

ZaApplianceSettings.prototype.initFromJS = function(obj) {
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
	
ZaApplianceSettings.modifyMethod = function (mods) {
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
ZaItem.modifyMethods["ZaApplianceSettings"].push(ZaApplianceSettings.modifyMethod);
