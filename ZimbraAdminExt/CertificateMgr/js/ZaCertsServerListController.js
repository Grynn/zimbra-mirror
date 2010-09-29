/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaCertsServerListController
* This is a singleton object that controls all the user interaction with the list of ZaServer objects
* @author Greg Solovyev
**/
ZaCertsServerListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container, "ZaCertsServerListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	
	//TODO helpURL needs to be changed
	this._helpURL = location.pathname + "help/admin/html/tools/creating_certificates.htm?locid=" + AjxEnv.DEFAULT_LOCALE;	
}

ZaCertsServerListController.prototype = new ZaListViewController();
ZaCertsServerListController.prototype.constructor = ZaCertsServerListController;

ZaController.initToolbarMethods["ZaCertsServerListController"] = new Array();
ZaController.initPopupMenuMethods["ZaCertsServerListController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaServer {@link ZaServer} objects
**/
ZaCertsServerListController.prototype.show = 
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	if (list != null)
		this._contentView.set(list.getVector());
		
	ZaApp.getInstance().pushView(this.getContentViewId());
	if (list != null)
		this._list = list;
			
	this.changeActionsState();	
}

ZaCertsServerListController.initToolbarMethod =
function () {
    this._toolbarOperations.push(new ZaOperation(ZaOperation.VIEW, com_zimbra_cert_manager.TBB_view_cert, com_zimbra_cert_manager.TBB_view_cert_tt, "ViewCertificate", "ViewCertificate", new AjxListener(this, ZaCertsServerListController.prototype.viewCertListener)));	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, com_zimbra_cert_manager.TBB_launch_cert_wizard, com_zimbra_cert_manager.TBB_launch_cert_wizard_tt, "InstallCertificate", "InstallCertificate", 
   			new AjxListener(this, ZaCertsServerListController.prototype._newCertListener)));				
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, com_zimbra_cert_manager.TBB_Help, com_zimbra_cert_manager.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
}
ZaController.initToolbarMethods["ZaCertsServerListController"].push(ZaCertsServerListController.initToolbarMethod);

ZaCertsServerListController.initPopupMenuMethod =
function () {
   	this._popupOperations.push(new ZaOperation(ZaOperation.VIEW, com_zimbra_cert_manager.TBB_view_cert, com_zimbra_cert_manager.TBB_view_cert_tt, "ViewCertificate", "ViewCertificate", new AjxListener(this, ZaCertsServerListController.prototype.viewCertListener)));	
   	this._popupOperations.push(new ZaOperation(ZaOperation.NEW, com_zimbra_cert_manager.TBB_launch_cert_wizard, com_zimbra_cert_manager.TBB_launch_cert_wizard_tt, "InstallCertificate", "InstallCertificate", new AjxListener(this, ZaCertsServerListController.prototype._newCertListener)));				
}
ZaController.initPopupMenuMethods["ZaCertsServerListController"].push(ZaCertsServerListController.initPopupMenuMethod);

ZaCertsServerListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaCertsServerListView(this._container);
		this._initToolbar();
		if(this._toolbarOperations && this._toolbarOperations.length) {
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations); 
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		}
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//ZaApp.getInstance().createView(ZaZimbraAdmin._SERVERS_LIST_VIEW, elements);
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;

		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], ZaApp.getInstance());					
			
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaCertsServerListController.prototype._createUI", null, false);
		return;
	}	
}

ZaCertsServerListController.prototype.set = 
function(serverList) {
	this.show(serverList);
}

// new button was pressed
ZaCertsServerListController.prototype._newCertListener =
function(ev) {
	if(console && console.log) console.log("ZaCertsServerListController.prototype._newCertListener: Launch the new certificates wizard ... ") ;
	var serverId = null;
	//TODO: the selectedItem might be from the previous selection
	if (this._selectedItem && this._selectedItem.id) {
		serverId = this._selectedItem.id ;
	}
	ZaCert.launchNewCertWizard.call (this, serverId) ;
}
                                    
ZaCertsServerListController.prototype.viewCertListener = function (ev) {
	if(console && console.log) console.log("View the certificates ... ") ;
	ZaApp.getInstance().getCertViewController().show(
		ZaCert.getCerts(ZaApp.getInstance(), this._selectedItem.id),
		this._selectedItem.id) ;
}

/**
* This listener is called when the item in the list is double clicked. It call ZaServerController.show method
* in order to display the Server View
**/
ZaCertsServerListController.prototype._listSelectionListener =
function(ev) {
	if(ev.item) {
			this._selectedItem = ev.item;
	}
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
			ZaApp.getInstance().getCertViewController().show(
				ZaCert.getCerts(ZaApp.getInstance(), this._selectedItem.id),
				this._selectedItem.id);
	} else {
		this.changeActionsState();	
	}
}

ZaCertsServerListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

ZaCertsServerListController.prototype.changeActionsState = 
function () {
	if(this._contentView) {
		var cnt = this._contentView.getSelectionCount();
		if(cnt == 1) {
			var opsArray = [ZaOperation.VIEW, ZaOperation.NEW];
			this._toolbar.enable(opsArray, true);
			this._actionMenu.enable(opsArray, true);
		} else {
			var opsArray = [ZaOperation.VIEW];
			this._toolbar.enable(opsArray, false);
			this._actionMenu.enable(opsArray, false);
		}
	}
}