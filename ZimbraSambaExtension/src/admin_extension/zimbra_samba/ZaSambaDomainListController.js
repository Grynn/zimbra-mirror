/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaSambaDomainListController
* This is a singleton object that controls all the user interaction with the list of ZaSambaDomain objects
**/
function ZaSambaDomainListController(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaSambaDomainListController");
   	this._toolbarOperations = new Object();
   	this._popupOperations = new Object();
}

ZaSambaDomainListController.prototype = new ZaListViewController();
ZaSambaDomainListController.prototype.constructor = ZaSambaDomainListController;
ZaController.initToolbarMethods["ZaSambaDomainListController"] = new Array();
ZaController.initPopupMenuMethods["ZaSambaDomainListController"] = new Array();
ZaController.changeActionsStateMethods["ZaSambaDomainListController"] = new Array();

ZaSambaDomainListController.prototype.show = 
function(list) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	if (list != null)
		this._contentView.set(list.getVector());
	
	ZaApp.getInstance().pushView(this.getContentViewId());			

	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this.changeActionsState();		
}

ZaSambaDomainListController.initToolbarMethod =
function () {
   	this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.SERTBB_new_tt, "Domain", "DomainDis", new AjxListener(this, this._newButtonListener));    			
   	this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSambaDomainListController.prototype._editButtonListener));    	
   	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSambaDomainListController.prototype._deleteButtonListener));    	    	
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	
	this._toolbarOrder.push(ZaOperation.NEW);
	this._toolbarOrder.push(ZaOperation.EDIT);
	this._toolbarOrder.push(ZaOperation.DELETE);
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);					
					
}
ZaController.initToolbarMethods["ZaSambaDomainListController"].push(ZaSambaDomainListController.initToolbarMethod);

ZaSambaDomainListController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSambaDomainListController.prototype._editButtonListener));    	
	this._popupOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSambaDomainListController.prototype._deleteButtonListener));    	    	
}
ZaController.initPopupMenuMethods["ZaSambaDomainListController"].push(ZaSambaDomainListController.initPopupMenuMethod);


ZaSambaDomainListController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaSambaDomainListView(this._container);
		this._initToolbar();
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations); 
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		
		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}		
		ZaApp.getInstance().createView(this.getContentViewId(), elements,tabParams);

		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = ZaApp.getInstance().dialogs["removeConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);											
		this._UICreated = true;
	} catch (ex) {
		this._handleException(ex, "ZaSambaDomainListController.prototype._createUI", null, false);
		return;
	}	
}


ZaSambaDomainListController.prototype.set = 
function(sambaDomainList) {
	this.show(sambaDomainList);
}


// new button was pressed
ZaSambaDomainListController.prototype._newButtonListener =
function(ev) {
	var newSambaDomain = new ZaSambaDomain();
	ZaApp.getInstance().getSambaDomainController().show(newSambaDomain);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaSambaDomainController.show method
* in order to display the Server View
**/
ZaSambaDomainListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getSambaDomainController().show(ev.item);
		}
	} else {
		this.changeActionsState();	
	}
}

ZaSambaDomainListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaSambaDomainController.show method
* in order to display the Server View
**/
ZaSambaDomainListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getSambaDomainController().show(item);
	}
}

ZaSambaDomainListController.changeActionsStateMethod = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
	} else if (cnt > 1){
		if(this._toolbarOperations[ZaOperation.EDIT])
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;

		if(this._popupOperations[ZaOperation.EDIT])
			this._popupOperations[ZaOperation.EDIT].enabled = false;
	} else {
		if(this._toolbarOperations[ZaOperation.EDIT])
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;

		if(this._popupOperations[ZaOperation.EDIT])
			this._popupOperations[ZaOperation.EDIT].enabled = false;
	}
}
ZaController.changeActionsStateMethods["ZaSambaDomainListController"].push(ZaSambaDomainListController.changeActionsStateMethod);


/**
* @param ev
* This listener is invoked by ZaSambaDomainController or any other controller that can create a SambaDomain object
**/
ZaSambaDomainListController.prototype.handleCreation = 
function (ev) {
	if(ev) {
		if(ev.getDetails() && this._list) {
			if (this._list) this._list.add(ev.getDetails());
			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaSambaDomainController or any other controller that can remove a SambaDomain object
**/
ZaSambaDomainListController.prototype.handleRemoval = 
function (ev) {
	if(ev) {
		if(ev.getDetails() && this._list) {
			if (this._list) this._list.remove(ev.getDetails());
			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();			
			}
		}
	}
}
/**
* @param ev
* This listener is invoked by ZaSambaDomainController or any other controller that can modify a SambaDomain object
**/
ZaSambaDomainListController.prototype.handleChange =
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		if (details) {
			if (this._list) {
				this._list.replace(details);
			}
			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaSambaDomainListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	if(this._contentView.getSelectionCount() > 0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			if(arrItems[key]) {
				this._removeList.push(arrItems[key]);
			}
		}
	}
	if(this._removeList.length) {
		dlgMsg = "Are you sure you want to delete the following group(s)?";
		dlgMsg +=  "<br><ul>";
		var i=0;
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
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this._deleteCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, this._donotDeleteCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
}

ZaSambaDomainListController.prototype._deleteCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);				
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, "ZaSambaDomainListController.prototype._deleteCallback", null, false);
				return;
			}
		}
		if (this._list) this._list.remove(this._removeList[key]); //remove from the list
	}
	this.fireRemovalEvent(successRemList); 	
	this._removeConfirmMessageDialog.popdown();
	if (this._contentView) this._contentView.setUI();
	this.show();
}

ZaSambaDomainListController.prototype._donotDeleteCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}