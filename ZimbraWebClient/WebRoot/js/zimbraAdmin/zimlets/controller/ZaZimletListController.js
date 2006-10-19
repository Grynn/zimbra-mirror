/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaZimletListController
* This is a singleton object that controls all the user interaction with the list of ZaZimlet objects
* @author Greg Solovyev
**/
function ZaZimletListController(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app,"ZaZimletListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_servers/managing_servers.htm";					
}

ZaZimletListController.prototype = new ZaListViewController();
ZaZimletListController.prototype.constructor = ZaZimletListController;

ZaController.initToolbarMethods["ZaZimletListController"] = new Array();
ZaController.initPopupMenuMethods["ZaZimletListController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaZimlet {@link ZaZimlet} objects
**/
ZaZimletListController.prototype.show = 
function(list) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	if (list != null)
		this._contentView.set(list.getVector());
	
	this._app.pushView(ZaZimbraAdmin._ZIMLET_LIST_VIEW);			

	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this._changeActionsState();		
}

ZaZimletListController.initToolbarMethod =
function () {
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaZimletListController.prototype._editButtonListener)));    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DEPLOY_ZIMLET, ZaMsg.TBB_DeployNew, ZaMsg.TBB_DeployNew_tt, "RestoreMailbox", "RestoreMailboxDis", new AjxListener(this, this.deployZimletListener)));				
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
}
ZaController.initToolbarMethods["ZaZimletListController"].push(ZaZimletListController.initToolbarMethod);

ZaZimletListController.initPopupMenuMethod =
function () {
   	this._popupOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaZimletListController.prototype._editButtonListener)));    	
   	//this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaZimletListController.prototype._deleteButtonListener)));    	    	
}
ZaController.initPopupMenuMethods["ZaZimletListController"].push(ZaZimletListController.initPopupMenuMethod);

ZaZimletListController.prototype.deployZimletListener = 
function (ev) {
	try {
		this._deployZimletWizard = new ZaZimletDeployXWizard(this._container, this._app);		
		var zimlet = new ZaZimlet(this._app);
		this._deployZimletWizard.setObject(zimlet);		
		this._deployZimletWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype.deployZimletListener", null, false);
	}
	return;
}

ZaZimletListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaZimletListView(this._container);
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
		this._app.createView(ZaZimbraAdmin._ZIMLET_LIST_VIEW, elements);


		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);					
	
		
		this._UICreated = true;
	} catch (ex) {
		this._handleException(ex, "ZaZimletListController.prototype._createUI", null, false);
		return;
	}	
}

ZaZimletListController.prototype.set = 
function(zimletList) {
	this.show(zimletList);
}


/**
* This listener is called when the item in the list is double clicked. It call ZaZimletController.show method
* in order to display the Zimlet View
**/
ZaZimletListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			this._app.getZimletController().show(ev.item);
		}
	} else {
		this._changeActionsState();	
	}
}

ZaZimletListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaZimletController.show method
* in order to display the Zimlet View
**/
ZaZimletListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getZimletController().show(item);
	}
}

ZaZimletListController.prototype._changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var opsArray = [ZaOperation.EDIT];
		this._toolbar.enable(opsArray, true);
		this._actionMenu.enable(opsArray, true);
	} else if (cnt > 1){
		var opsArray1 = [ZaOperation.EDIT];
		this._toolbar.enable(opsArray1, false);
		this._actionMenu.enable(opsArray1, false);
	} else {
		var opsArray = [ZaOperation.EDIT];
		this._toolbar.enable(opsArray, false);
		this._actionMenu.enable(opsArray, false);
	}
}