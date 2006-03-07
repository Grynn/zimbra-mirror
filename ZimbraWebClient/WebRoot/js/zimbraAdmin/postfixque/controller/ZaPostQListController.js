/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaPostQListController
**/
function ZaPostQListController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app,"ZaPostQListController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_servers/managing_servers.htm";					
}

ZaPostQListController.prototype = new ZaController();
ZaPostQListController.prototype.constructor = ZaPostQListController;

ZaController.initToolbarMethods["ZaPostQListController"] = new Array();
ZaController.initPopupMenuMethods["ZaPostQListController"] = new Array();

ZaPostQListController.prototype.show = 
function(list) {

    if (!this._UICreated) {
		this._createUI();
	} 	

	if (list != null)
		this._contentView.set(list.getVector());
		
	this._app.pushView(ZaZimbraAdmin._POSTQ_VIEW);			
	
	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this._changeActionsState();		
}

ZaPostQListController.initToolbarMethod =
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.LABEL, ZaMsg.TBB_LastUpdated, ZaMsg.TBB_LastUpdated_tt, null, null, null,null,null,null,"refreshTime"));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, null, null, new AjxListener(this, this.refreshListener)));	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.VIEW, ZaMsg.TBB_View, ZaMsg.PQTBB_View_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaPostQListController.prototype._viewButtonListener)));    		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
   	
}
ZaController.initToolbarMethods["ZaPostQListController"].push(ZaPostQListController.initToolbarMethod);

ZaPostQListController.initPopupMenuMethod =
function () {
    this._popupOperations.push(new ZaOperation(ZaOperation.VIEW, ZaMsg.TBB_View, ZaMsg.PQTBB_View_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaPostQListController.prototype._viewButtonListener)));
}
ZaController.initPopupMenuMethods["ZaPostQListController"].push(ZaPostQListController.initPopupMenuMethod);

ZaPostQListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaPostQListView(this._container);
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
		this._app.createView(ZaZimbraAdmin._POSTQ_VIEW, elements);


		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);					
	
		
		this._UICreated = true;
	} catch (ex) {
		this._handleException(ex, "ZaPostQListController.prototype._createUI", null, false);
		return;
	}	
}

/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaPostQListController.prototype.getList = 
function() {
	return this._list;
}

/*
ZaPostQListController.prototype.refresh = 
function() {
	try {
		this._contentView.set(this._app.getServerList(true).getVector());
	} catch (ex) {
		this._handleException(ex, ZaPostQListController.prototype.refresh, null, false);
	}
}
*/

ZaPostQListController.prototype.set = 
function(serverList) {
	this.show(serverList);
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaPostQListController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}


/**
* This listener is called when the item in the list is double clicked. It call ZaPostQController.show method
* in order to display the MailQ View
**/
ZaPostQListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			this._app.getPostQController().show(ev.item);
		}
	} else {
		this._changeActionsState();	
	}
}

ZaPostQListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaPostQController.show method
* in order to display the MailQ View
**/
ZaPostQListController.prototype._viewButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getPostQController().show(item);
	}
}


ZaPostQListController.prototype._changeActionsState = 
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