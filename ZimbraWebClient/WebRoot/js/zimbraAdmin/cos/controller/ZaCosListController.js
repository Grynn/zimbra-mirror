/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

ZaCosListController = function(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app, "ZaCosListController");
	this.objType = ZaEvent.S_COS;	
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/cos/class_of_service.htm";				
}

ZaCosListController.prototype = new ZaController();
ZaCosListController.prototype.constructor = ZaCosListController;

//ZaCosListController.COS_VIEW = "ZaCosListController.COS_VIEW";

ZaCosListController.prototype.show = 
function(list, openInNewTab) {
    if (!this._contentView) {
    	this._ops = new Array();
    	this._ops.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, ZaCosListController.prototype._newButtonListener)));
   	this._ops.push(new ZaOperation(ZaOperation.DUPLICATE, ZaMsg.TBB_Duplicate, ZaMsg.COSTBB_Duplicate_tt, "DuplicateCOS", "DuplicateCOSDis", new AjxListener(this, ZaCosListController.prototype._duplicateButtonListener)));    	    	
    	this._ops.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.COSTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaCosListController.prototype._editButtonListener)));    	
    	this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaCosListController.prototype._deleteButtonListener)));    	    	
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
		this._toolbar = new ZaToolBar(this._container, this._ops);
    
		this._contentView = new ZaCosListView(this._container);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		 
		//this._app.createView(ZaZimbraAdmin._COS_LIST_VIEW, elements);
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		this._app.createView(this.getContentViewId(), elements, tabParams) ;
		
		if (list != null)
			this._contentView.set(list.getVector());

    	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._ops);		
		//this._app.pushView(ZaZimbraAdmin._COS_LIST_VIEW);
		this._app.pushView(this.getContentViewId());
		
		//set a selection listener on the account list view
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = this._app.dialogs["removeConfirmMessageDialog"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);							

		this._UICreated = true;
		this._app._controllers[this.getContentViewId ()] = this ;
	} else {
		if (list != null)
			this._contentView.set(list.getVector());	

		//this._app.pushView(ZaZimbraAdmin._COS_LIST_VIEW);
		this._app.pushView(this.getContentViewId());
	}
//	this._app.setCurrentController(this);		
	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this.changeActionsState();		
	/*
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("COS") ;	
	}*/
}


ZaCosListController.prototype.refresh = 
function() {
}

/**
* @param ev
* This listener is invoked by ZaAccountController or any other controller that can create an ZaAccount object
**/
ZaCosListController.prototype.handleCosCreation = 
function (ev) {
	if(ev) {
		if(ev.getDetails() && this._list) {
			if (this._list) this._list.add(ev.getDetails());
			if (this._contentView) this._contentView.setUI();
			if(this._app.getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaCosController or any other controller that can remove an ZaCos object
**/
ZaCosListController.prototype.handleCosRemoval = 
function (ev) {
	if(ev) {
		//add the new ZaAccount to the controlled list
		if(ev.getDetails() && this._list) {
			if (this._list) this._list.remove(ev.getDetails());
			if (this._contentView) this._contentView.setUI();
			if(this._app.getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

ZaCosListController.prototype.handleCosChange =
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		//if(details && (details["mods"][ZaCos.A_name] || details["mods"][ZaCos.A_description])) {
		if (details) {
			if (this._list) {
				this._list.replace(details);
			}
			if (this._contentView) this._contentView.setUI();
			if(this._app.getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

/**
* Adds listener to removal of an ZaCos 
* @param listener
**/
ZaCosListController.prototype.addCosRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

// refresh button was pressed
ZaCosListController.prototype._refreshButtonListener =
function(ev) {
	this.refresh();
}


// duplicate button was pressed
ZaCosListController.prototype._duplicateButtonListener =
function(ev) {
	var newCos = new ZaCos(this._app); //new COS
	if(this._contentView && (this._contentView.getSelectionCount() == 1)) {
		var item = this._contentView.getSelection()[0];
		if(item && item.attrs) { //copy the attributes from the selected COS to the new COS
			for(var aname in item.attrs) {
				if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) )
					continue;	
					
				if ( (typeof item.attrs[aname] == "object") || (item.attrs[aname] instanceof Array)) {
					newCos.attrs[aname] = AjxUtil.createProxy(item.attrs[aname],3);
					/*for(var a in item.attrs[aname]) {
						newCos.attrs[aname][a]=item.attrs[aname][a];
					}*/
				} else {
					newCos.attrs[aname] = item.attrs[aname];
				}
			}
		}
	}	
	this._app.getCosController().show(newCos);
}

// new button was pressed
ZaCosListController.prototype._newButtonListener =
function(ev) {
	var newCos = new ZaCos(this._app);
	//load default COS
	var defCos = new ZaCos(this._app);
	defCos.load("name", "default");
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) )
			continue;			
		newCos.attrs[aname] = defCos.attrs[aname];
	}
	
	this._app.getCosController().show(newCos);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaCosController.show method
* in order to display the Cos View
**/
ZaCosListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._app.getCosController().show(ev.item);
		}
	} else {
		this.changeActionsState();	
	}
}


ZaCosListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaCosListController.show method
* in order to display the COS View
**/
ZaCosListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getCosController().show(item);
	}
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaCosListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	this._itemsInTabList = [] ;
	if(this._contentView.getSelectionCount() > 0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
		
		//	var item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			var item = arrItems[key];
			if (item) {
				if (this._app.getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);
				}
			}
		}
	}
	
	if (this._itemsInTabList.length > 0) {
		if(!this._app.dialogs["ConfirmDeleteItemsInTabDialog"]) {
			this._app.dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], this._app,
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC]);			
		}
		
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; ;
		msg += ZaCosListController.getDlMsgFromList (this._itemsInTabList) ;
		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaCosListController.prototype._closeTabsBeforeRemove, this);
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaCosListController.prototype._deleteCosInRemoveList, this);		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	}else{
		this._deleteCosInRemoveList ();
	}
}

ZaCosListController.prototype._closeTabsBeforeRemove =
function () {
	//DBG.println (AjxDebug.DBG1, "Close the tabs before Remove ...");
	this.closeTabsInRemoveList() ;
	/*
	var tabGroup = this._app.getTabGroup();
	for (var i=0; i< this._itemsInTabList.length ; i ++) {
		var item = this._itemsInTabList[i];
		tabGroup.removeTab (tabGroup.getTabByItemId(item.id)) ;
		this._removeList.push(item);
	}*/
	//this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	this._deleteCosInRemoveList();
}

ZaCosListController.prototype._deleteCosInRemoveList =
function () {
	if (this._app.dialogs["ConfirmDeleteItemsInTabDialog"]) {
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	}
	if(this._removeList.length) {
		var dlgMsg = ZaMsg.Q_DELETE_COSES;
		dlgMsg += ZaCosListController.getDlMsgFromList (this._removeList) ;
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosListController.prototype._deleteCosCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosListController.prototype._donotDeleteCosCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
	
} 

ZaCosListController.getDlMsgFromList =
function (listArr) {
	dlgMsg =  "<br><ul>";
	var i=0;
	for(var key in listArr) {
		if(i > 19) {
			dlgMsg += "<li>...</li>";
			break;
		}
		dlgMsg += "<li>";
		if(listArr[key].name.length > 50) {
			//split it
			var endIx = 49;
			var beginIx = 0; //
			while(endIx < listArr[key].name.length) { //
				dlgMsg +=  listArr[key].name.slice(beginIx, endIx); //
				beginIx = endIx + 1; //
				if(beginIx >= (listArr[key].name.length) ) //
					break;
				
				endIx = ( listArr[key].name.length <= (endIx + 50) ) ? listArr[key].name.length-1 : (endIx + 50);
				dlgMsg +=  "<br>";	
			}
		} else {
			dlgMsg += listArr[key].name;
		}
		dlgMsg += "</li>";
		i++;
	}
	dlgMsg += "</ul>";
	
	return dlgMsg ;
}


ZaCosListController.prototype._deleteCosCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);				
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, ZaCosListController.prototype._deleteCosCallback, null, false);
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

ZaCosListController.prototype._donotDeleteCosCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaCosListController.prototype.changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	var hasDefault = false;
	if(cnt >= 1) {
		var arrDivs = this._contentView.getSelectedItems().getArray();
		for(var key in arrDivs) {
			var item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			if(item) {
				if(item.name == "default") {
					hasDefault = true;
					break;
				}		
			}
		}
	}
		
	if(cnt == 1) {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DUPLICATE];
		if(!hasDefault) {
        	opsArray.push(ZaOperation.DELETE);
		} else {
			var opsArray2 = [ZaOperation.DELETE];
			this._toolbar.enable(opsArray2, false);
			this._actionMenu.enable(opsArray2, false);
		}

		this._toolbar.enable(opsArray, true);
		this._actionMenu.enable(opsArray, true);
	} else if (cnt > 1){
		var opsArray1 = [ZaOperation.EDIT, ZaOperation.DUPLICATE];
		this._toolbar.enable(opsArray1, false);
		this._actionMenu.enable(opsArray1, false);

		var opsArray2 = [ZaOperation.DELETE];
		if(!hasDefault) {
			this._toolbar.enable(opsArray2, true);
			this._actionMenu.enable(opsArray2, true);
		} else {
			this._toolbar.enable(opsArray2, false);
			this._actionMenu.enable(opsArray2, false);
		}
	} else {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.DUPLICATE];
		this._toolbar.enable(opsArray, false);
		this._actionMenu.enable(opsArray, false);
	}
}