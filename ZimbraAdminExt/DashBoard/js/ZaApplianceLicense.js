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

/**
* @class ZaApplianceLicense 
* @contructor ZaApplianceLicense
* @param app
* @author Greg Solovyev
**/
ZaApplianceLicense = function() {
	ZaItem.call(this,"ZaApplianceLicense");
	this._init();	
}


ZaApplianceLicense.prototype = new ZaItem;
ZaApplianceLicense.prototype.constructor = ZaApplianceLicense;
ZaItem.loadMethods["ZaApplianceLicense"] = new Array();
ZaItem.initMethods["ZaApplianceLicense"] = new Array();

if (ZaOperation) ZaOperation.INSTALL_ZCS_LICENSE = ++ZA_OP_INDEX;
ZaItem.LICENSE = "license";

ZaApplianceLicense.A_accountsLimit = "AccountsLimit";
ZaApplianceLicense.A_attachmentConversionEnabled = "AttachmentConversionEnabled";
ZaApplianceLicense.A_backupEnabled = "BackupEnabled";
ZaApplianceLicense.A_crossMailboxSearchEnabled = "CrossMailboxSearchEnabled";
ZaApplianceLicense.A_hierarchicalStorageManagementEnabled = "HierarchicalStorageManagementEnabled";
ZaApplianceLicense.A_iSyncAccountsLimit = "ISyncAccountsLimit";
ZaApplianceLicense.A_installType ="InstallType";
ZaApplianceLicense.A_issuedOn ="IssuedOn";
ZaApplianceLicense.A_issuedToEmail = "IssuedToEmail";
ZaApplianceLicense.A_issuedToName = "IssuedToName";
ZaApplianceLicense.A_licenseId = "LicenseId";
ZaApplianceLicense.A_MAPIConnectorAccountsLimit = "MAPIConnectorAccountsLimit";
ZaApplianceLicense.A_mobileSyncAccountsLimit = "MobileSyncAccountsLimit";
ZaApplianceLicense.A_mobileSyncEnabled = "MobileSyncEnabled";
ZaApplianceLicense.A_resellerName = "ResellerName" ;
ZaApplianceLicense.A_validFrom = "ValidFrom";
ZaApplianceLicense.A_validUntil = "ValidUntil";
ZaApplianceLicense.InstallStatusMsg = "LicenseInstallStatusMsg";
ZaApplianceLicense.Info_TotalAccounts = "TotalAccounts" ;
ZaApplianceLicense.InstallStatusCode = "LicenseInstallStatusCode";
ZaApplianceLicense.myXModel = {
items: [
	//license
	{id:ZaApplianceLicense.A_accountsLimit, type: _STRING_, ref: ZaApplianceLicense.A_accountsLimit},
	{id:ZaApplianceLicense.A_attachmentConversionEnabled, type: _STRING_, ref: ZaApplianceLicense.A_attachmentConversionEnabled},
	{id:ZaApplianceLicense.A_backupEnabled, type: _STRING_, ref: ZaApplianceLicense.A_backupEnabled },
	{id:ZaApplianceLicense.A_crossMailboxSearchEnabled, type: _STRING_, ref: ZaApplianceLicense.A_crossMailboxSearchEnabled },
	{id:ZaApplianceLicense.A_hierarchicalStorageManagementEnabled, type: _STRING_, ref: ZaApplianceLicense.A_hierarchicalStorageManagementEnabled },	
	{id:ZaApplianceLicense.A_iSyncAccountsLimit, type: _STRING_, ref: ZaApplianceLicense.A_iSyncAccountsLimit },
	{id:ZaApplianceLicense.A_installType, type: _STRING_, ref: ZaApplianceLicense.A_installType },
	{id:ZaApplianceLicense.A_issuedOn, type: _STRING_, ref: ZaApplianceLicense.A_issuedOn },
	{id:ZaApplianceLicense.A_issuedToEmail, type: _STRING_, ref: ZaApplianceLicense.A_issuedToEmail },
	{id:ZaApplianceLicense.A_issuedToName, type: _STRING_, ref: ZaApplianceLicense.A_issuedToName },		
	{id:ZaApplianceLicense.A_licenseId, type: _STRING_, ref: ZaApplianceLicense.A_licenseId },
	{id:ZaApplianceLicense.A_MAPIConnectorAccountsLimit, type: _STRING_, ref: ZaApplianceLicense.A_MAPIConnectorAccountsLimit },
	{id:ZaApplianceLicense.A_mobileSyncEnabled, type: _STRING_, ref: ZaApplianceLicense.A_mobileSyncEnabled},
	{id:ZaApplianceLicense.A_mobileSyncAccountsLimit, type: _STRING_, ref: ZaApplianceLicense.A_mobileSyncAccountsLimit },
	{id:ZaApplianceLicense.A_resellerName, type: _STRING_, ref: ZaApplianceLicense.A_resellerName },
	{id:ZaApplianceLicense.A_validFrom, type: _STRING_, ref: ZaApplianceLicense.A_validFrom },
	{id:ZaApplianceLicense.A_validUntil, type: _STRING_, ref: ZaApplianceLicense.A_validUntil },
	{id:ZaApplianceLicense.InstallStatusMsg, type: _STRING_, ref:ZaApplianceLicense.InstallStatusMsg},
	{id:ZaApplianceLicense.Info_TotalAccounts, type: _STRING_, ref:ZaApplianceLicense.Info_TotalAccounts},
	{id:ZaApplianceLicense.InstallStatusCode, type:_NUMBER_, ref:ZaApplianceLicense.InstallStatusCode}
]};
ZaApplianceLicense.getLocaleString =
function (serverStr) {
	if (serverStr == null) return null;
	
	var d = new Date();
	var yyyy = parseInt(serverStr.substr(0,4), 10);
	var MM = parseInt(serverStr.substr(4,2), 10);
	var dd = parseInt(serverStr.substr(6,2), 10);
	d.setFullYear(yyyy);
	d.setMonth(MM - 1);
	d.setMonth(MM - 1); // DON'T remove second call to setMonth (see bug #3839)
	d.setDate(dd);
	ZaLicense.parseLicenseTime(serverStr, d);
	return d;	
};
	
ZaApplianceLicense.prototype.load = function (by, val) {
	ZaItem.prototype.load.call(this,by,val,true, false);
};

ZaApplianceLicense.loadMethod = 
function() {
	try {
		var soapDoc = AjxSoapDoc.create("GetLicenseRequest", "urn:zimbraAdmin", null);
		var params = {};
		params.soapDoc = soapDoc; 
		params.asyncMode = false;				
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : com_zimbra_dashboard.BUSY_LOADING_LICENSE
		}
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetLicenseResponse;
		this.attrs = {};
		this.initFromJS(resp.license[0]);
		this.initFromJS(resp.info[0]);
	} catch (ex) {
		ZaApp.getInstance().getStatusViewController()._handleException(ex, "ZaApplianceStatus.loadMethod", null, false);		
	}	
};
ZaItem.loadMethods["ZaApplianceLicense"].push(ZaApplianceLicense.loadMethod);

ZaApplianceLicense.prototype.initFromJS = function (obj) {
	if(!obj)
		return;
	
	if(obj.attr) {
		var len = obj.attr.length;
		for(var ix = 0; ix < len; ix++) {
			if(!this.attrs[[obj.attr[ix].name]]) {
				this.attrs[[obj.attr[ix].name]] = obj.attr[ix]._content;
			} else {
				this.attrs[[obj.attr[ix].name]].push(obj.attr[ix]._content);
			}
		}
	}
};

ZaApplianceLicense.getCause = function (detailMsg) {
	var causeBy = /Caused by:\s*com.zimbra.cs.license.LicenseException:\s*(.*)/;
	
	var result = detailMsg.match(causeBy);
	if (result != null) {
    	return result [1] ;
	}else{
		return detailMsg ;
	}
};

