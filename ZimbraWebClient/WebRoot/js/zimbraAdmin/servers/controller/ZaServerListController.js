/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* @constructor
* @class ZaServerListController
* This is a singleton object that controls all the user interaction with the list of ZaServer objects
**/
function ZaServerListController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app);
	this._evtMgr = new AjxEventMgr();
}

ZaServerListController.prototype = new ZaController();
ZaServerListController.prototype.constructor = ZaServerListController;

ZaServerListController.SERVER_VIEW = "ZaServerListController.SERVER_VIEW";

ZaServerListController.prototype.show = 
function(list) {
    if (!this._appView) {
    	//create toolbar
    	this._ops = new Array();
    	this._ops.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, ZaImg.I_PROPERTIES, ZaImg.I_PROPERTIES, new AjxListener(this, ZaServerListController.prototype._editButtonListener)));    	
    	this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, ZaImg.I_DELETE, ZaImg.I_DELETE, new AjxListener(this, ZaServerListController.prototype._deleteButtonListener)));    	    	
		this._toolbar = new ZaToolBar(this._container, this._ops);    
 	
		//create Servers list view
		this._contentView = new ZaServerListView(this._container);
		this._appView = this._app.createView(ZaServerListController.SERVER_VIEW, [this._toolbar,  this._contentView]);

    	//context menu
    	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._ops);
	
		if (list != null)
			this._contentView.set(list.getVector());

		this._app.pushView(ZaServerListController.SERVER_VIEW);			
		
		//set a selection listener on the Server list view
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);					
		//this.refresh();
	} else {
		if (list != null)
			this._contentView.set(list.getVector());	
			
		this._app.pushView(ZaServerListController.SERVER_VIEW);
	}
	this._app.setCurrentController(this);
	this._removeList = new Array();
	if (list != null)
		this._list = list;
		
	this._changeActionsState();		
}

/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaServerListController.prototype.getList = 
function() {
	return this._list;
}

/*
ZaServerListController.prototype.refresh = 
function() {
	try {
		this._contentView.set(this._app.getServerList(true).getVector());
	} catch (ex) {
		this._handleException(ex, ZaServerListController.prototype.refresh, null, false);
	}
}
*/

ZaServerListController.prototype.set = 
function(serverList) {
	this.show(serverList);
}

/**
* @param ev
* This listener is invoked by  any controller that can change an ZaServer object
**/
ZaServerListController.prototype.handleServerChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		if(details["modFields"] && (details["modFields"][ZaServer.A_description] )) {
			this._contentView.setUI();
			if(this._app.getCurrentController() == this) {
				this.show();			
			}
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaServerController or any other controller that can create an ZaServer object
**/
ZaServerListController.prototype.handleServerCreation = 
function (ev) {
	if(ev) {
		//add the new ZaServer to the controlled list
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
* This listener is invoked by ZaServerController or any other controller that can remove an ZaServer object
**/
ZaServerListController.prototype.handleServerRemoval = 
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

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaServerListController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaServerListController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

/**
* Adds listener to removal of an ZaServer 
* @param listener
**/
ZaServerListController.prototype.addServerRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/*
// refresh button was pressed
ZaServerListController.prototype._refreshButtonListener =
function(ev) {
	this.refresh();
}
*/

/**
*	Private method that notifies listeners to that the controlled ZaServer (are) removed
* 	@param details
*/
ZaServerListController.prototype._fireServerRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_SERVER);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaServerListController.prototype._fireServerRemovalEvent, details, false);	
	}
}


// new button was pressed
ZaServerListController.prototype._newButtonListener =
function(ev) {
	var newServer = new ZaServer();
	this._app.getServerController().show(newServer);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaServerController.show method
* in order to display the Server View
**/
ZaServerListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			this._app.getServerController().show(ev.item);
		}
	} else {
		this._changeActionsState();	
	}
}

ZaServerListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked. 
* It call ZaServerController.show method
* in order to display the Server View
**/
ZaServerListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()) {
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		this._app.getServerController().show(item);
	}
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaServerListController.prototype._deleteButtonListener =
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
		dlgMsg = ZaMsg.Q_DELETE_SERVERS;
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
		this._removeConfirmMessageDialog.setMessage(dlgMsg, null, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerListController.prototype._deleteServersCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaServerListController.prototype._donotDeleteServersCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
}

ZaServerListController.prototype._deleteServersCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);					
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, ZaServerListController.prototype._deleteServersCallback, null, false);
				return;
			}
		}
		this._list.remove(this._removeList[key]); //remove from the list
	}
	this._fireServerRemovalEvent(successRemList); 		
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaServerListController.prototype._donotDeleteServersCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaServerListController.prototype._changeActionsState = 
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