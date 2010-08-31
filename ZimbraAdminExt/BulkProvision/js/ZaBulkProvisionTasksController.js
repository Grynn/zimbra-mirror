/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @constructor
* @class ZaBulkProvisionTasksController
* @author Greg Solovyev
**/
ZaBulkProvisionTasksController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container, "ZaBulkProvisionTasksController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	this.objType = ZaEvent.S_BULK_PROVISION_TASK;
 	this._helpURL = ZaBulkProvisionTasksController.helpURL;
}

ZaBulkProvisionTasksController.prototype = new ZaListViewController();
ZaBulkProvisionTasksController.prototype.constructor = ZaBulkProvisionTasksController;
ZaBulkProvisionTasksController.helpURL = location.pathname + "help/admin/html/managing_accounts/how_to_provision_multiple_accounts.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
ZaController.initToolbarMethods["ZaBulkProvisionTasksController"] = new Array();
ZaController.initPopupMenuMethods["ZaBulkProvisionTasksController"] = new Array();
ZaController.changeActionsStateMethods["ZaBulkProvisionTasksController"] = new Array(); 
ZaOperation.BULK_DATA_IMPORT = ++ ZA_OP_INDEX;
ZaOperation.ACCOUNT_IMPORT_WIZARD =  ++ZA_OP_INDEX;
ZaOperation.MIGRATION_WIZARD =  ++ZA_OP_INDEX;

ZaBulkProvisionTasksController.prototype.show = 
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	ZaApp.getInstance().pushView(this.getContentViewId());
}

ZaBulkProvisionTasksController.initToolbarMethod =
function () {
	var showBulkProvision = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showBulkProvision = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,domainList[i])) {
				showBulkProvision = true;
				break;
			}	
		}
	}	
	if(showBulkProvision) {    	
		this._toolbarOperations[ZaOperation.ACCOUNT_IMPORT_WIZARD] = new ZaOperation(ZaOperation.ACCOUNT_IMPORT_WIZARD, com_zimbra_bulkprovision.NewButton_Import, com_zimbra_bulkprovision.NewButton_Import_tt, "BulkProvision", "BulkProvision", new AjxListener(this, this.openBulkProvisionDialog,{}));        
		this._toolbarOperations[ZaOperation.MIGRATION_WIZARD] = new ZaOperation(ZaOperation.MIGRATION_WIZARD, com_zimbra_bulkprovision.TBB_migration_wizard, com_zimbra_bulkprovision.TBB_migration_wizard_tt, "ApplianceMigration", "ApplianceMigration", new AjxListener(this, this.openMigrationWizard,{}));
		this._toolbarOrder.push(ZaOperation.ACCOUNT_IMPORT_WIZARD) ;
		this._toolbarOrder.push(ZaOperation.MIGRATION_WIZARD) ;
	}
	
	this._toolbarOperations[ZaOperation.BULK_DATA_IMPORT]=new ZaOperation(ZaOperation.BULK_DATA_IMPORT,com_zimbra_bulkprovision.TB_IMAP_Import, com_zimbra_bulkprovision.TB_IMAP_Import_tt, "ApplianceMigration", "ApplianceMigration", new AjxListener(this, this.bulkDataImportListener,{}));				
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));

	this._toolbarOrder.push(ZaOperation.BULK_DATA_IMPORT);
	this._toolbarOrder.push(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.HELP);					
}
ZaController.initToolbarMethods["ZaBulkProvisionTasksController"].push(ZaBulkProvisionTasksController.initToolbarMethod);

ZaBulkProvisionTasksController.prototype.openBulkProvisionDialog = function (params,ev) {
    try {
		var obj = null;
		if (params && params.obj) {
			obj = params.obj;
		} else {
			obj = new ZaBulkProvision();
		}
		if(params && params.hideWiz) {
			if(ZaApp.getInstance().dialogs[params.hideWiz]) {
				ZaApp.getInstance().dialogs[params.hideWiz].popdown();
			}
		}    	
		obj[ZaBulkProvision.A2_provAction] = ZaBulkProvision.ACTION_IMPORT_LDAP;
		obj[ZaBulkProvision.A2_generatedFileLink] = null;
		obj[ZaBulkProvision.A2_maxResults] = "0";
		obj[ZaBulkProvision.A2_GalLdapFilter] = "(objectClass=organizationalPerson)";
		obj[ZaBulkProvision.A2_generatePassword] = "TRUE";
		obj[ZaBulkProvision.A2_genPasswordLength] = 8;
		obj[ZaBulkProvision.A2_ZimbraAdminLogin] = ZaZimbraAdmin.currentUserLogin;
		obj[ZaBulkProvision.A2_createDomains] = "TRUE";
		ZaApp.getInstance().dialogs["importAccountsWizard"] = new ZaBulkImportXWizard(DwtShell.getShell(window),obj);
		if(params && params.prevCallback) {
			ZaApp.getInstance().dialogs["importAccountsWizard"].prevCallback = params.prevCallback;
		} else {
			ZaApp.getInstance().dialogs["importAccountsWizard"].prevCallback = null;
		}

		if(params && params.finishCallback) {
			ZaApp.getInstance().dialogs["importAccountsWizard"].registerCallback(DwtWizardDialog.FINISH_BUTTON, params.finishCallback);
		} 

		ZaApp.getInstance().dialogs["importAccountsWizard"].setObject(obj);
		ZaApp.getInstance().dialogs["importAccountsWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaBulkProvisionTasksController.prototype.openBulkProvisionDialog", null, false);
	}
};

ZaBulkProvisionTasksController.prototype.openMigrationWizard = function (params,ev) {
    try {
		var obj = null;
		if (params && params.obj) {
			obj = params.obj;
		} else {
			obj = new ZaBulkProvision();
		}
		if(params && params.hideWiz) {
			if(ZaApp.getInstance().dialogs[params.hideWiz]) {
				ZaApp.getInstance().dialogs[params.hideWiz].popdown();
			}
		}
    	
		obj[ZaBulkProvision.A2_provAction] = ZaBulkProvision.ACTION_GENERATE_MIG_XML;
		obj[ZaBulkProvision.A2_generatedFileLink] = null;
		obj[ZaBulkProvision.A2_maxResults] = "0";
		obj[ZaBulkProvision.A2_GalLdapFilter] = "(objectClass=organizationalPerson)";
		obj[ZaBulkProvision.A2_generatePassword] = "TRUE";
		obj[ZaBulkProvision.A2_provisionUsers] = "TRUE";
		obj[ZaBulkProvision.A2_importMails] = "TRUE";
		obj[ZaBulkProvision.A2_importContacts] = "TRUE";
		obj[ZaBulkProvision.A2_importTasks] = "TRUE";
		obj[ZaBulkProvision.A2_importCalendar] = "TRUE";
		obj[ZaBulkProvision.A2_InvalidSSLOk] = "TRUE";
		obj[ZaBulkProvision.A2_genPasswordLength] = 8;
		obj[ZaBulkProvision.A2_ZimbraAdminLogin] = ZaZimbraAdmin.currentUserLogin;
		obj[ZaBulkProvision.A2_createDomains] = "TRUE";
		
		ZaApp.getInstance().dialogs["migrationWizard"] = new ZaMigrationXWizard(DwtShell.getShell(window),obj);
		
		if(params && params.prevCallback) {
			ZaApp.getInstance().dialogs["migrationWizard"].prevCallback = params.prevCallback;
		} else {
			ZaApp.getInstance().dialogs["migrationWizard"].prevCallback = null;
		}
		ZaApp.getInstance().dialogs["migrationWizard"].setObject(obj);
		ZaApp.getInstance().dialogs["migrationWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaBulkProvisionTasksController.prototype.openMigrationWizard", null, false);
	}
};

ZaBulkProvisionTasksController.prototype.bulkDataImportListener = 
function (params,ev) {
    try {
		if(!ZaApp.getInstance().dialogs["bulkDataImportWizard"]) {
			ZaApp.getInstance().dialogs["bulkDataImportWizard"] = new ZaBulkDataImportXWizard(this._container);
		}
		var obj = null;
		if (params && params.obj) {
			obj = params.obj;
		} else {
			obj = new ZaBulkProvision();
			obj[ZaModel.currentStep] = ZaBulkDataImportXWizard.STEP_INTRODUCTION;
			obj[ZaBulkProvision.A2_provisionUsers] = "TRUE";
			obj[ZaBulkProvision.A2_importEmail] = "TRUE";
			obj[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_XML;
			obj[ZaBulkProvision.A2_sourceServerType] = ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP;
		}
		if(params && params.hideWiz) {
			if(ZaApp.getInstance().dialogs[params.hideWiz]) {
				ZaApp.getInstance().dialogs[params.hideWiz].popdown();
			}
		}
		if(params && params.prevCallback) {
			ZaApp.getInstance().dialogs["bulkDataImportWizard"].prevCallback = params.prevCallback;
		} else {
			ZaApp.getInstance().dialogs["bulkDataImportWizard"].prevCallback = null;
		}
		ZaApp.getInstance().dialogs["bulkDataImportWizard"].setObject(obj);
		ZaApp.getInstance().dialogs["bulkDataImportWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaBulkProvisionTasksController.prototype.bulkDataImportListener", null, false);
	}

}

ZaBulkProvisionTasksController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaBulkProvisionTasksView(this._container);
		this._initToolbar();
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder); 
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);

		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaBulkProvisionTasksController.prototype._createUI", null, false);
		return;
	}	
}

ZaBulkProvisionTasksController.prototype.set = 
function(serverList) {
	this.show(serverList);
}