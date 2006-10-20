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
	this.isExtension = false;
	this.objType = ZaEvent.S_ZIMLET;
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
	if (list != null) {
		this._contentView.set(list.getVector());
	
		if(list.getArray() && list.getArray()[0])
			this.isExtension = (list.getArray()[0].attrs[ZaZimlet.A_zimbraZimletIsExtension]=="TRUE");

		this._list = list;
	}		
	this._app.pushView(ZaZimbraAdmin._ZIMLET_LIST_VIEW);			

	this._removeList = new Array();
		
	this._changeActionsState();		
}

ZaZimletListController.initToolbarMethod =
function () {
//   	this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaZimletListController.prototype._editButtonListener)));    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DEPLOY_ZIMLET, ZaMsg.TBB_DeployNew, ZaMsg.TBB_DeployNew_tt, "RestoreMailbox", "RestoreMailboxDis", new AjxListener(this, this.deployZimletListener)));				
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Undeploy, ZaMsg.DTBB_Undeploy_tt, "Delete", "DeleteDis", new AjxListener(this, this._undeployButtonListener)));    	    		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
}
ZaController.initToolbarMethods["ZaZimletListController"].push(ZaZimletListController.initToolbarMethod);

ZaZimletListController.initPopupMenuMethod =
function () {
//   	this._popupOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaZimletListController.prototype._editButtonListener)));    	
   	this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Undeploy, ZaMsg.DTBB_Undeploy_tt, "Delete", "DeleteDis", new AjxListener(this, this._undeployButtonListener)));    	    		

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
	/*	if(ev.item) {
			this._selectedItem = ev.item;
			this._app.getZimletController().show(ev.item);
		}*/
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
/*	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getZimletController().show(item);
	}*/
}

/**
* This listener is called when the Undeploy button is clicked. 
**/
ZaZimletListController.prototype._undeployButtonListener =
function(ev) {
	this._removeList = new Array();
	if(this._contentView.getSelectionCount()>0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			if(arrItems[key]) {
				this._removeList.push(arrItems[key]);
			}
		}
	}
	if(this._removeList.length) {
		var dlgMsg;
		if(this.isExtension) {
			dlgMsg = ZaMsg.Q_UNDEPLOY_ADMIN_EXTENSIONS;			
		} else {
			dlgMsg = ZaMsg.Q_UNDEPLOY_ZIMLETS;			
		}

		dlgMsg += "<br>";
		for(var key in this._removeList) {
			if(i > 19) {
				dlgMsg += "<li>...</li>";
				break;
			}
			dlgMsg += "<li>";
			if(this._removeList[key].name.length > 50) {
				//split it
				var endIx = 49;
				var beginIx = 0; //
				while(endIx < this._removeList[key].name.length) { //
					dlgMsg +=  this._removeList[key].name.slice(beginIx, endIx); //
					beginIx = endIx + 1; //
					if(beginIx >= (this._removeList[key].name.length) ) //
						break;
					
					endIx = ( this._removeList[key].name.length <= (endIx + 50) ) ? this._removeList[key].name.length-1 : (endIx + 50);
					dlgMsg +=  "<br>";	
				}
			} else {
				dlgMsg += this._removeList[key].name;
			}
			dlgMsg += "</li>";
			i++;
		}
		dlgMsg += "</ul>";
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaZimletListController.prototype._undeployZimletsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaZimletListController.prototype._donotUndeployZimletsCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
}

ZaZimletListController.prototype._undeployZimletsCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);					
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, "ZaZimletListController.prototype._undeployZimletsCallback", null, false);				
				return;
			}
		}
		this._list.remove(this._removeList[key]); //remove from the list
	}
	this.fireRemovalEvent(successRemList); 		
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaZimletListController.prototype._donotUndeployZimletsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
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