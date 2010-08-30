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
ZaBulkProvisionTasksController.prototype.show = 
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	ZaApp.getInstance().pushView(this.getContentViewId());
}

ZaBulkProvisionTasksController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.BULK_DATA_IMPORT]=new ZaOperation(ZaOperation.BULK_DATA_IMPORT,com_zimbra_bulkprovision.TB_IMAP_Import, com_zimbra_bulkprovision.TB_IMAP_Import_tt, "BulkProvision", "BulkProvision", new AjxListener(this, this.bulkDataImportListener));				
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	
	this._toolbarOrder.push(ZaOperation.BULK_DATA_IMPORT);
	this._toolbarOrder.push(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.HELP);					
}
ZaController.initToolbarMethods["ZaBulkProvisionTasksController"].push(ZaBulkProvisionTasksController.initToolbarMethod);

ZaController.initPopupMenuMethods["ZaBulkProvisionTasksController"].push(ZaBulkProvisionTasksController.initPopupMenuMethod);

ZaBulkProvisionTasksController.prototype.bulkDataImportListener = 
function (ev) {
    try {
		if(!ZaApp.getInstance().dialogs["bulkDataImportWizard"]) {
			ZaApp.getInstance().dialogs["bulkDataImportWizard"] = new ZaBulkDataImportXWizard(this._container);
		}
		ZaApp.getInstance().dialogs["bulkDataImportWizard"].setObject(new ZaBulkProvision());
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