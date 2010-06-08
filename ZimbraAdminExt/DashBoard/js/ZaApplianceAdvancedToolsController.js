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
* @class ZaApplianceAdvancedToolsController 
* @contructor ZaApplianceAdvancedToolsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaApplianceAdvancedToolsController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaApplianceAdvancedToolsController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm#appliance/zap_managing_the_server_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = ZaApplianceAdvancedToolsView;					
}

ZaApplianceAdvancedToolsController.prototype = new ZaXFormViewController();
ZaApplianceAdvancedToolsController.prototype.constructor = ZaApplianceAdvancedToolsController;

//ZaApplianceAdvancedToolsController.STATUS_VIEW = "ZaApplianceAdvancedToolsController.STATUS_VIEW";
ZaController.initToolbarMethods["ZaApplianceAdvancedToolsController"] = new Array();
ZaController.setViewMethods["ZaApplianceAdvancedToolsController"] = [];
ZaController.changeActionsStateMethods["ZaApplianceAdvancedToolsController"] = [];

ZaOperation.ACCOUNT_IMPORT_WIZARD =  ++ZA_OP_INDEX;
ZaOperation.MIGRATION_WIZARD =  ++ZA_OP_INDEX;

ZaApp.prototype.getApplianceAdvancedToolsController = function() {
	var c  = new ZaApplianceAdvancedToolsController(this._appCtxt, this._container, this);
	return c ;
}

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/

ZaApplianceAdvancedToolsController.prototype.show = function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaApplianceAdvancedToolsController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.INSTALL_ZCS_LICENSE] = new ZaOperation(ZaOperation.INSTALL_ZCS_LICENSE, com_zimbra_dashboard.InstallLicenseButton, com_zimbra_dashboard.InstallLicenseButton_tt, "UpdateLicense", "UpdateLicense", new AjxListener(this, this.installLicenseButtonListener));
	this._toolbarOperations[ZaOperation.INSTALL_ZCS_CERTIFICATE] = new ZaOperation(ZaOperation.INSTALL_ZCS_CERTIFICATE, com_zimbra_dashboard.TBB_launch_cert_wizard, com_zimbra_dashboard.TBB_launch_cert_wizard_tt, "InstallCertificate", "InstallCertificate", new AjxListener(this, this.installCertListener));
	this._toolbarOperations[ZaOperation.MIGRATION_WIZARD] = new ZaOperation(ZaOperation.MIGRATION_WIZARD, com_zimbra_dashboard.TBB_migration_wizard, com_zimbra_dashboard.TBB_migration_wizard_tt, "BulkProvision", "BulkProvision", new AjxListener(this, this.openMigrationWizard));
	this._toolbarOperations[ZaOperation.ACCOUNT_IMPORT_WIZARD] = new ZaOperation(ZaOperation.ACCOUNT_IMPORT_WIZARD, com_zimbra_dashboard.NewButton_Import, com_zimbra_dashboard.NewButton_Import_tt, "BulkProvision", "BulkProvision", new AjxListener(this, this.openBulkProvisionDialog));
	this._toolbarOrder.push(ZaOperation.INSTALL_ZCS_LICENSE);
	this._toolbarOrder.push(ZaOperation.INSTALL_ZCS_CERTIFICATE);
	this._toolbarOrder.push(ZaOperation.MIGRATION_WIZARD);
	this._toolbarOrder.push(ZaOperation.ACCOUNT_IMPORT_WIZARD);
}
ZaController.initToolbarMethods["ZaApplianceAdvancedToolsController"].push(ZaApplianceAdvancedToolsController.initToolbarMethod);


ZaApplianceAdvancedToolsController.setViewMethod = function (item) {
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
            tabId: this.getContentViewId(),
            closable:false,
            selected:false
        }
        ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
        ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
       // ZaApp.getInstance().pushView(this.getContentViewId());        		
        item.load();
        item[ZaModel.currentTab] = "1"
        item.id = ZaItem.GLOBAL_CONFIG;
        this._view.setDirty(false);
        this._view.setObject(item);
	} catch (ex) {		
		this._handleException(ex, "ZaApplianceAdvancedToolsController.prototype.show", null, false);
	}
	this._currentObject = item;	
}
ZaController.setViewMethods["ZaApplianceAdvancedToolsController"].push(ZaApplianceAdvancedToolsController.setViewMethod) ;

ZaApplianceAdvancedToolsController.prototype.setEnabled = 
function(enable) {
	this._view.setEnabled(enable);
}

ZaApplianceAdvancedToolsController.prototype.openMigrationWizard = function () {
    try {
		var bp = new ZaBulkProvision();
		bp[ZaBulkProvision.A2_provAction] = ZaBulkProvision.ACTION_GENERATE_MIG_XML;
		bp[ZaBulkProvision.A2_generatedFileLink] = null;
		bp[ZaBulkProvision.A2_maxResults] = "0";
		bp[ZaBulkProvision.A2_GalLdapFilter] = "(objectClass=organizationalPerson)";
		bp[ZaBulkProvision.A2_generatePassword] = "TRUE";
		bp[ZaBulkProvision.A2_provisionUsers] = "TRUE";
		bp[ZaBulkProvision.A2_importMails] = "TRUE";
		bp[ZaBulkProvision.A2_importContacts] = "TRUE";
		bp[ZaBulkProvision.A2_importTasks] = "TRUE";
		bp[ZaBulkProvision.A2_importCalendar] = "TRUE";
		bp[ZaBulkProvision.A2_InvalidSSLOk] = "TRUE";
		bp[ZaBulkProvision.A2_genPasswordLength] = 8;
		bp[ZaBulkProvision.A2_ZimbraAdminLogin] = ZaZimbraAdmin.currentUserLogin;
		bp[ZaBulkProvision.A2_createDomains] = "TRUE";
		ZaApp.getInstance().dialogs["migrationWizard"] = new ZaMigrationXWizard(DwtShell.getShell(window),bp);
		ZaApp.getInstance().dialogs["migrationWizard"].setObject(bp);
		ZaApp.getInstance().dialogs["migrationWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaApplianceAdvancedToolsController.prototype.openMigrationWizard", null, false);
	}
};

ZaApplianceAdvancedToolsController.prototype.openBulkProvisionDialog = function () {
    try {
		var bp = new ZaBulkProvision();
		bp[ZaBulkProvision.A2_provAction] = ZaBulkProvision.ACTION_IMPORT_LDAP;
		bp[ZaBulkProvision.A2_generatedFileLink] = null;
		bp[ZaBulkProvision.A2_maxResults] = "0";
		bp[ZaBulkProvision.A2_GalLdapFilter] = "(objectClass=organizationalPerson)";
		bp[ZaBulkProvision.A2_generatePassword] = "TRUE";
		bp[ZaBulkProvision.A2_genPasswordLength] = 8;
		bp[ZaBulkProvision.A2_ZimbraAdminLogin] = ZaZimbraAdmin.currentUserLogin;
		bp[ZaBulkProvision.A2_createDomains] = "TRUE";
		ZaApp.getInstance().dialogs["importAccountsWizard"] = new ZaBulkImportXWizard(DwtShell.getShell(window),bp);
		ZaApp.getInstance().dialogs["importAccountsWizard"].setObject(bp);
		ZaApp.getInstance().dialogs["importAccountsWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaApplianceAdvancedToolsController.prototype.openBulkProvisionDialog", null, false);
	}
};

ZaApplianceAdvancedToolsController.prototype.installCertListener = function(ev) {
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

ZaApplianceAdvancedToolsController.prototype.finishCertificateWizard = function() {
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
		this._handleException(ex, "ZaApplianceAdvancedToolsController.prototype.finishCertificateWizard", null, false);
	}	
}

ZaApplianceAdvancedToolsController.prototype.certificateInstallCallback = function (resp){		
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_cert_manager.CERT_INSTALL_STATUS_1 + ": " + ZaApplianceSSLCert.getCause(detailMsg), "ZaApplianceAdvancedToolsController.prototype.certificateInstallCallback", AjxException.UNKNOWN_ERROR, detailMsg) ;
				//throw new Error(resp._data.msg) ;
		} else{
			var installResponse = resp._data.Body.InstallCertResponse ;
			if (installResponse) {
				this.popupMsgDialog(com_zimbra_dashboard.CertificateInstallationSuccess);
        		var certs = ZaApplianceSSLCert.getCerts(ZaDashBoard.server.id);
        		this._contentView._localXForm.setInstanceValue(certs, ZaApplianceAdvancedTools.A_certs);
			}
		}
	} catch (ex){
		this.popupErrorDialog(ex.msg, ex, true);
	}
}

ZaApplianceAdvancedToolsController.prototype.installLicenseButtonListener = function (ev) {
	if(!this.licenseInstallWizard) {
		this.licenseInstallWizard = ZaApp.getInstance().dialogs["licenseInstallWizard"] = new ZaApplianceLicenseWizard(this._container);
		this.licenseInstallWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishLicenseWizard, this, null);
	}
				
	this.licenseInstallWizard.popup();
};

ZaApplianceAdvancedToolsController.prototype.finishLicenseWizard = function() {
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
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaApplianceAdvancedToolsController.prototype.finishLicenseWizard", null, false);
	}
};

ZaApplianceAdvancedToolsController.prototype.installCallback = function (resp){
	this.licenseInstallWizard.popdown();
	var xform = this._view._localXForm;
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_dashboard.LI_INSTALL_STATUS_1 + ": " + ZaApplianceLicense.getCause(detailMsg), "ZaApplianceAdvancedToolsController.prototype.installCallback", AjxException.UNKNOWN_ERROR, detailMsg);
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
				throw new AjxException(com_zimbra_dashboard.LIW_ERROR_0, "ZaApplianceAdvancedToolsController.prototype.installCallback", AjxException.UNKNOWN_ERROR) ;
			}
		}
	} catch (ex){
		xform.setInstanceValue(ex.msg, ZaApplianceLicense.InstallStatusMsg);
		xform.setInstanceValue(-1, ZaApplianceLicense.InstallStatusCode);
		this.popupErrorDialog(ex.msg, ex, true);
	}
};