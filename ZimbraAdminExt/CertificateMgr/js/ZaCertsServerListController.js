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
   	this._popupOperations = new Array();			
	this._helpURL = location.pathname + "help/admin/html/tools/creating_certificates.htm?locid=" + AjxEnv.DEFAULT_LOCALE;	
}

ZaCertsServerListController.prototype = new ZaListViewController();
ZaCertsServerListController.prototype.constructor = ZaCertsServerListController;
ZaController.changeActionsStateMethods["ZaCertsServerListController"] = new Array();
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

ZaCertsServerListController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.VIEW] = new ZaOperation(ZaOperation.VIEW, com_zimbra_cert_manager.TBB_view_cert, com_zimbra_cert_manager.TBB_view_cert_tt, "ViewCertificate", "ViewCertificate", new AjxListener(this, ZaCertsServerListController.prototype.viewCertListener));	
   	this._popupOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW, com_zimbra_cert_manager.TBB_launch_cert_wizard, com_zimbra_cert_manager.TBB_launch_cert_wizard_tt, "InstallCertificate", "InstallCertificate", new AjxListener(this, ZaCertsServerListController.prototype._newCertListener));				
}
ZaController.initPopupMenuMethods["ZaCertsServerListController"].push(ZaCertsServerListController.initPopupMenuMethod);

ZaCertsServerListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaCertsServerListView(this._container);
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
        ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
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
//	if(window.console && window.console.log) console.log("ZaCertsServerListController.prototype._newCertListener: Launch the new certificates wizard ... ") ;
	var serverId = null;
	
	if(this._contentView && this._contentView.getSelectionCount()==1 && this._contentView.getSelection()[0]) {
		serverId = this._contentView.getSelection()[0].id ;
	}
	ZaCert.launchNewCertWizard.call (this, serverId) ;
}
                                    
ZaCertsServerListController.prototype.viewCertListener = function (ev) {
	//if(window.console && window.console.log) console.log("View the certificates ... ") ;
	if(this._contentView && this._contentView.getSelectionCount()==1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getCertViewController().show(ZaCert.getCerts(ZaApp.getInstance(), item.id),item.id);
	    var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, com_zimbra_cert_manager.OVP_certs]);
	    ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, item.name, null, false, false, item);	
	}
	
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
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED && ev.item) {
			ZaApp.getInstance().getCertViewController().show(
				ZaCert.getCerts(ZaApp.getInstance(), ev.item.id),
				ev.item.id);
            var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, com_zimbra_cert_manager.OVP_certs]);
            ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, ev.item.name, null, false, false, ev.item);
	} else {
		this.changeActionsState();	
	}

}

ZaCertsServerListController.prototype._listActionListener =
function (ev) {
	if(ev.item) {
		this._selectedItem = ev.item;
	}
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

ZaCertsServerListController.changeActionsStateMethod = 
function () {
	if(this._contentView) {
		if(this._popupOperations[ZaOperation.NEW]) {
			this._popupOperations[ZaOperation.NEW].enabled = true;		
		}
		var cnt = this._contentView.getSelectionCount();
		if(this._popupOperations[ZaOperation.VIEW]) {
			this._popupOperations[ZaOperation.VIEW].enabled = (cnt == 1);		
		}
	}
}

if(ZaController.changeActionsStateMethods["ZaCertsServerListController"]) {
    ZaController.changeActionsStateMethods["ZaCertsServerListController"].push(ZaCertsServerListController.changeActionsStateMethod);
}