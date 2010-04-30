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
* @class ZaApplianceSettingsController 
* @contructor ZaApplianceSettingsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaApplianceSettingsController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaApplianceSettingsController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_global_settings/global_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = ZaApplianceSettingsView;					
}

ZaApplianceSettingsController.prototype = new ZaXFormViewController();
ZaApplianceSettingsController.prototype.constructor = ZaApplianceSettingsController;

//ZaApplianceSettingsController.STATUS_VIEW = "ZaApplianceSettingsController.STATUS_VIEW";
ZaController.initToolbarMethods["ZaApplianceSettingsController"] = new Array();
ZaController.setViewMethods["ZaApplianceSettingsController"] = [];
ZaController.changeActionsStateMethods["ZaApplianceSettingsController"] = [];

ZaApp.prototype.getApplianceSettingsController = function() {
	var c  = new ZaApplianceSettingsController(this._appCtxt, this._container, this);
	return c ;
}

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/
ZaApplianceSettingsController.prototype.addSettingsChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

ZaApplianceSettingsController.prototype.show = function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaApplianceSettingsController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.SAVE] = new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));    			
	this._toolbarOperations[ZaOperation.DOWNLOAD_GLOBAL_CONFIG] = new ZaOperation(ZaOperation.DOWNLOAD_GLOBAL_CONFIG, ZaMsg.TBB_DownloadConfig, ZaMsg.GLOBTBB_DownloadConfig_tt, "DownloadGlobalConfig", "DownloadGlobalConfig", new AjxListener(this, this.downloadConfigButtonListener));
	this._toolbarOperations[ZaOperation.INSTALL_ZCS_LICENSE] = new ZaOperation(ZaOperation.INSTALL_ZCS_LICENSE, com_zimbra_dashboard.InstallLicenseButton, com_zimbra_dashboard.InstallLicenseButton_tt, "UpdateLicense", "UpdateLicense", new AjxListener(this, this.installLicenseButtonListener));
	this._toolbarOperations[ZaOperation.INSTALL_ZCS_CERTIFICATE] = new ZaOperation(ZaOperation.INSTALL_ZCS_CERTIFICATE, com_zimbra_dashboard.TBB_launch_cert_wizard, com_zimbra_dashboard.TBB_launch_cert_wizard_tt, "InstallCertificate", "InstallCertificate", new AjxListener(this, this.installCertListener));
	this._toolbarOrder.push(ZaOperation.SAVE);
	this._toolbarOrder.push(ZaOperation.DOWNLOAD_GLOBAL_CONFIG);
	this._toolbarOrder.push(ZaOperation.INSTALL_ZCS_LICENSE);
	this._toolbarOrder.push(ZaOperation.INSTALL_ZCS_CERTIFICATE);
}
ZaController.initToolbarMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.initToolbarMethod);


ZaApplianceSettingsController.setViewMethod = function (item) {
    try {
    	this._initToolbar();
    	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
        this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
        this._toolbarOrder.push(ZaOperation.NONE);
        this._toolbarOrder.push(ZaOperation.HELP);
        this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder);
        this._contentView = this._view = new this.tabConstructor(this._container,item);
        var elements = new Object();
        elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
        elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
        var tabParams = {
            openInNewTab: true,
            tabId: this.getContentViewId()
        }
        ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
        ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
        ZaApp.getInstance().pushView(this.getContentViewId());        		
        item.load();
        item[ZaModel.currentTab] = "1"
        item.id = ZaItem.GLOBAL_CONFIG;
        this._view.setDirty(false);
        this._view.setObject(item);
	} catch (ex) {		
		this._handleException(ex, "ZaApplianceSettingsController.prototype.show", null, false);
	}
	this._currentObject = item;	
}
ZaController.setViewMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.setViewMethod) ;

ZaApplianceSettingsController.prototype.setEnabled = 
function(enable) {
	this._view.setEnabled(enable);
}

ZaApplianceSettingsController.changeActionsStateMethod =
function () {
    if(this._toolbarOperations[ZaOperation.SAVE]) {
        this._toolbarOperations[ZaOperation.SAVE].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.changeActionsStateMethod);

ZaApplianceSettingsController.prototype.installCertListener = function(ev) {
	if(!this.certificateInstallWizard) {
		this.certificateInstallWizard = ZaApp.getInstance().dialogs["certificateInstallWizard"] = new ZaApplianceSSLCertWizard (this._container);
		this.certificateInstallWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishCertificateWizard, this, null);		
	}
	var cert = new ZaApplianceSSLCert();
	cert.setTargetServer (ZaDashBoard.server.id);		
	cert.init() ;
	this.certificateInstallWizard.setObject(cert);	
	this.certificateInstallWizard.popup();				
}

ZaApplianceSettingsController.prototype.finishCertificateWizard = function() {
	try {	
		// Basically, it will do two things:
		//1) install the cert
		//2) Upon the successful install, the cert tab information will be updated
		var instance = this.certificateInstallWizard._localXForm.getInstance () ;
		var validationDays = instance[ZaApplianceSSLCert.A_validation_days] ;
		
		var selfType = instance[ZaApplianceSSLCert.A_type_self] ;
		var commType = instance[ZaApplianceSSLCert.A_type_comm] ;    
		var csrType = instance[ZaApplianceSSLCert.A_type_csr] ;
		
		var contentElement =  null ;
		if (selfType) {
			type = ZaApplianceSSLCert.A_type_self ;  
		}else if (commType) {
			type = ZaApplianceSSLCert.A_type_comm ;
		}else if (csrType){
			this.certificateInstallWizard.popdown();
			return ;
		}else{
			throw new Exeption ("Unknow installation type") ;		
		}
		
		var callback = new AjxCallback(this, this.certificateInstallCallback);
		var params = {
			type: type,
			validation_days: validationDays,
			comm_cert: this.certificateInstallWizard.uploadResults,
            subject: this.certificateInstallWizard._containedObject.attrs,
            keysize: this.certificateInstallWizard._containedObject.keysize,
			callback: callback 
		}
		ZaApplianceSSLCert.installCert (params, ZaDashBoard.server.id) ;
			
		this.certificateInstallWizard.popdown();	
			
	} catch (ex) {
		this._handleException(ex, "ZaApplianceSettingsController.prototype.finishCertificateWizard", null, false);
	}	
}

ZaApplianceSettingsController.prototype.certificateInstallCallback = function (resp){		
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_cert_manager.CERT_INSTALL_STATUS_1 + ": " + ZaApplianceSSLCert.getCause(detailMsg), "ZaApplianceSettingsController.prototype.certificateInstallCallback", AjxException.UNKNOWN_ERROR, detailMsg) ;
				//throw new Error(resp._data.msg) ;
		} else{
			var installResponse = resp._data.Body.InstallCertResponse ;
			if (installResponse) {
				this.popupMsgDialog(com_zimbra_dashboard.CertificateInstallationSuccess);
        		var certs = ZaApplianceSSLCert.getCerts(ZaDashBoard.server.id);
        		this._contentView._localXForm.setInstanceValue(certs, ZaApplianceSettings.A_certs);
			}
		}
	} catch (ex){
		this.popupErrorDialog(ex.msg, ex, true);
	}
}
/**
* handles "download" button click. Launches file download in a new window
**/
ZaApplianceSettingsController.prototype.downloadConfigButtonListener = 
function(ev) {
	window.open("/service/collectldapconfig/");
};

ZaApplianceSettingsController.prototype.installLicenseButtonListener = function (ev) {
	if(!this.licenseInstallWizard) {
		this.licenseInstallWizard = ZaApp.getInstance().dialogs["licenseInstallWizard"] = new ZaApplianceLicenseWizard(this._container);
		this.licenseInstallWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishLicenseWizard, this, null);
	}
				
	this.licenseInstallWizard.popup();
};

ZaApplianceSettingsController.prototype.finishLicenseWizard = function() {
	try {	
		//check if current license is a 10 day trial
        var licenseObj = new ZaApplianceLicense();
        licenseObj.load();

        if(licenseObj && licenseObj.attrs && licenseObj.attrs[ZaApplianceLicense.A_validUntil] && licenseObj.attrs[ZaApplianceLicense.A_validFrom]) {
	        var dTo =  ZaApplianceLicense.getLocalDate(licenseObj.attrs[ZaApplianceLicense.A_validUntil]);
	        var dFrom = ZaApplianceLicense.getLocalDate(licenseObj.attrs[ZaApplianceLicense.A_validFrom]);
	        var dDelta = dTo.getTime() - dFrom.getTime();
	        if(dDelta < 1296000000 && dDelta > 0) { 
	        	ZaApplianceLicense.removeLicense();
	        	ZaApplianceLicense.flushLicenseCache();
	        }
        }
		var xform = this._view._localXForm;
		xform.setInstanceValue(0, ZaApplianceLicense.InstallStatusCode);
		var soapDoc = AjxSoapDoc.create("InstallLicenseRequest", "urn:zimbraAdmin", null);
		var contentElement = soapDoc.set("content", "");
		contentElement.setAttribute("aid", this.licenseInstallWizard.attId );
		
		var params = new Object();
		params.soapDoc = soapDoc;
		var callback = new AjxCallback(this, this.installCallback);
		if(callback) {
			params.asyncMode = true;
			params.callback = callback;
		}
		var reqMgrParams = {
			controller : this,
			busyMsg : ZaMsg.BUSY_INSTALLING_LICENSE
		}
		ZaRequestMgr.invoke(params, reqMgrParams);		
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaApplianceSettingsController.prototype.finishLicenseWizard", null, false);
	}
};

ZaApplianceSettingsController.prototype.installCallback = function (resp){
	this.licenseInstallWizard.popdown();
	var xform = this._view._localXForm;
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_dashboard.LI_INSTALL_STATUS_1 + ": " + ZaApplianceLicense.getCause(detailMsg), "ZaApplianceSettingsController.prototype.installCallback", AjxException.UNKNOWN_ERROR, detailMsg);
		} else{
			var installResponse = resp._data.Body.InstallLicenseResponse ;
			if (installResponse) {
		        var licenseObj = new ZaApplianceLicense();
		        licenseObj.load();
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_accountsLimit], ZaApplianceLicense.A_accountsLimit);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_issuedToName], ZaApplianceLicense.A_issuedToName);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_installType], ZaApplianceLicense.A_installType);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_licenseId], ZaApplianceLicense.A_licenseId);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_issuedOn], ZaApplianceLicense.A_issuedOn);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_validFrom], ZaApplianceLicense.A_validFrom);
		        xform.setInstanceValue(licenseObj.attrs[ZaApplianceLicense.A_validUntil], ZaApplianceLicense.A_validUntil);
				xform.setInstanceValue(com_zimbra_dashboard.LI_INSTALL_STATUS_0, ZaApplianceLicense.InstallStatusMsg);
				xform.setInstanceValue(1, ZaApplianceLicense.InstallStatusCode);		        
			}else{
				throw new AjxException(com_zimbra_dashboard.LIW_ERROR_0, "ZaApplianceSettingsController.prototype.installCallback", AjxException.UNKNOWN_ERROR) ;
			}
		}
	} catch (ex){
		xform.setInstanceValue(ex.msg, ZaApplianceLicense.InstallStatusMsg);
		xform.setInstanceValue(-1, ZaApplianceLicense.InstallStatusCode);
		this.popupErrorDialog(ex.msg, ex, true);
	}
};

ZaApplianceSettingsController.prototype._saveChanges =
function () {
	var tmpObj = this._view.getObject();
	var isNew = false;
	if(tmpObj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	
	//check if domain is real
	if(tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]) {
		if(tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName] != this._currentObject.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]) {
			var testD = new ZaDomain();
			try {
				testD.load("name",tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]);
			} catch (ex) {
				if (ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
					this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_WRONG_DOMAIN_IN_GS, [tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]]), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorDialog.popup();	
					return false;	
				} else {
					throw (ex);
				}
			}
		}
	}	

	//transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaDomain
	var mods = new Object();
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || a == ZaGlobalConfig.A_zimbraAccountClientAttr ||
		a == ZaGlobalConfig.A_zimbraServerInheritedAttr || a == ZaGlobalConfig.A_zimbraDomainInheritedAttr ||
		a == ZaGlobalConfig.A_zimbraCOSInheritedAttr || a == ZaGlobalConfig.A_zimbraGalLdapAttrMap || 
		a == ZaGlobalConfig.A_zimbraGalLdapFilterDef || /^_/.test(a) || a == ZaGlobalConfig.A_zimbraMtaBlockedExtension || a == ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension
                || a == ZaItem.A_zimbraACE)
			continue;

		if(!ZaItem.hasWritePermission(a,tmpObj)) {
			continue;
		}		
		
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(tmpObj.attrs[a] instanceof Array) {
                if (!this._currentObject.attrs[a]) 
                	this._currentObject.attrs[a] = [] ;
                	
                if( tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}
	//check if blocked extensions are changed
	if(!AjxUtil.isEmpty(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		if(
			(
				(!this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension] || !this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].length))
				|| (tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].join("") != this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].join(""))
			) {
			mods[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension];
		} 
	} else if (AjxUtil.isEmpty(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])  && !AjxUtil.isEmpty(this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		mods[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = "";
	}		

	//save the model

	this._currentObject.modify(mods);
	
	return true;
}


