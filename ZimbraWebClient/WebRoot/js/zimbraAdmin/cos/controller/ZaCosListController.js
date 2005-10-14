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
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaCosListController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app);
	this._evtMgr = new AjxEventMgr();		
}

ZaCosListController.prototype = new ZaController();
ZaCosListController.prototype.constructor = ZaCosListController;

//ZaCosListController.COS_VIEW = "ZaCosListController.COS_VIEW";

ZaCosListController.prototype.show = 
function(list) {
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
		this._app.createView(ZaZimbraAdmin._COS_LIST_VIEW, elements);
		if (list != null)
			this._contentView.set(list.getVector());

    	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._ops);		
		this._app.pushView(ZaZimbraAdmin._COS_LIST_VIEW);
		
		//set a selection listener on the account list view
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);							
	} else {
		if (list != null)
			this._contentView.set(list.getVector());	

		this._app.pushView(ZaZimbraAdmin._COS_LIST_VIEW);
	}
//	this._app.setCurrentController(this);		
	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this._changeActionsState();			
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaCosListController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
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
		//add the new ZaAccount to the controlled list
		if(ev.getDetails()) {
			this._list.add(ev.getDetails());
			this._contentView.setUI();
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
		if(ev.getDetails()) {
			this._list.remove(ev.getDetails());
			this._contentView.setUI();
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
		if(details["mods"][ZaCos.A_name] || details["mods"][ZaCos.A_description]) {
			this._contentView.setUI();
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

/**
*	Private method that notifies listeners to that the controlled ZaCos is (are) removed
* 	@param details
*/
ZaCosListController.prototype._fireCOSRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_COS);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaCosListController.prototype._fireCOSRemovalEvent, details, false);	
	}
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
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()) {
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		if(item && item.attrs) { //copy the attributes from the selected COS to the new COS
			for(var aname in item.attrs) {
				if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) )
					continue;			
				newCos.attrs[aname] = item.attrs[aname];
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
		this._changeActionsState();	
	}
}


ZaCosListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaCosListController.show method
* in order to display the COS View
**/
ZaCosListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()) {
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		this._app.getCosController().show(item);
	}
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaCosListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getArray()) {
		var arrDivs = this._contentView.getSelectedItems().getArray();
		for(var key in arrDivs) {
			var item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			if(item) {
				this._removeList.push(item);
			}
		}
	}
	if(this._removeList.length) {
		dlgMsg = ZaMsg.Q_DELETE_COS;
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
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosListController.prototype._deleteCosCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosListController.prototype._donotDeleteCosCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
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
		this._list.remove(this._removeList[key]); //remove from the list
	}
	this._fireCOSRemovalEvent(successRemList); 	
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaCosListController.prototype._donotDeleteCosCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaCosListController.prototype._changeActionsState = 
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