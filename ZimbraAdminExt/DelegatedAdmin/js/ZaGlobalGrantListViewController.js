
ZaGlobalGrantListViewController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaGlobalGrantListViewController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();

	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_grants/managing_global_grants.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaGlobalGrantListViewController.prototype = new ZaListViewController();
ZaGlobalGrantListViewController.prototype.constructor = ZaGlobalGrantListViewController;

ZaController.initToolbarMethods["ZaGlobalGrantListViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaGlobalGrantListViewController"] = new Array();
ZaController.changeActionsStateMethods["ZaGlobalGrantListViewController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaGrant {@link ZaGrant} objects
**/
ZaGlobalGrantListViewController.prototype.show =
function(list, openInNewTab) {
    if (!this._UICreated) {
		this._createUI();
	}
	if (list != null)
        this._contentView.set(list.getVector());
//        this._contentView.set(list);

    //ZaApp.getInstance().pushView(ZaZimbraAdmin._SERVERS_LIST_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._removeList = new Array();
	if (list != null)
		this._list = list;

	this.changeActionsState();
}

ZaGlobalGrantListViewController.initToolbarMethod =
function () {

    this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, com_zimbra_delegatedadmin.RIGHT_New_tt, "Account", "AccountDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._newButtonListener));
    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit,com_zimbra_delegatedadmin.RIGHT_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._editButtonListener));
   	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, com_zimbra_delegatedadmin.RIGHT_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._deleteButtonListener));
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));

    this._toolbarOrder.push(ZaOperation.NEW);
    this._toolbarOrder.push(ZaOperation.EDIT);
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
}
ZaController.initToolbarMethods["ZaGlobalGrantListViewController"].push(ZaGlobalGrantListViewController.initToolbarMethod);

ZaGlobalGrantListViewController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._editButtonListener));
    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._deleteButtonListener));

}
ZaController.initPopupMenuMethods["ZaGlobalGrantListViewController"].push(ZaGlobalGrantListViewController.initPopupMenuMethod);

ZaGlobalGrantListViewController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaGrantsListView(this._container, null, DwtControl.ABSOLUTE_STYLE );
		this._initToolbar();
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
//		ZaApp.getInstance().createView(ZaZimbraAdmin._grants_LIST_VIEW, elements);
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab()
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;

		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));
		this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);

		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	} catch (ex) {
		this._handleException(ex, "ZaGlobalGrantListViewController.prototype._createUI", null, false);
		return;
	}
}


/*
ZaGlobalGrantListViewController.prototype.refresh =
function() {
	try {
		this._contentView.set(ZaApp.getInstance().getGrantList(true).getVector());
	} catch (ex) {
		this._handleException(ex, ZaGlobalGrantListViewController.prototype.refresh, null, false);
	}
}
*/

ZaGlobalGrantListViewController.prototype.set =
function(grantsList) {
	this.show(grantsList);
}

/**
* @param ev
* This listener is invoked by  any controller that can change an ZaGrant object
**/
ZaGlobalGrantListViewController.prototype.handleGrantChange =
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		//if(details["modFields"] && (details["modFields"][ZaGrant.A_description] )) {
		if (details) {
			if (this._list) this._list.replace (details);
			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();
			}
			this.changeActionsState();
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaGrantController or any other controller that can create an ZaGrant object
**/
ZaGlobalGrantListViewController.prototype.handleGrantCreation =
function (ev) {
	if(ev) {
		//add the new ZaGrant to the controlled list
		if(ev.getDetails()) {
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
* This listener is invoked by ZaGrantController or any other controller that can remove an ZaGrant object
**/
ZaGlobalGrantListViewController.prototype.handleGrantRemoval =
function (ev) {
	if(ev) {
		//add the new ZaAccount to the controlled list
		if(ev.getDetails()) {
			if (this._list) this._list.remove(ev.getDetails());
			if (this._contentView ) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();
			}
		}
	}
}

/**
* Adds listener to removal of an ZaGrant
* @param listener
**/
ZaGlobalGrantListViewController.prototype.addGrantRemovalListener =
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/*
// refresh button was pressed
ZaGlobalGrantListViewController.prototype._refreshButtonListener =
function(ev) {
	this.refresh();
}
*/

/**
*	Private method that notifies listeners to that the controlled ZaGrant (are) removed
* 	@param details
*/
ZaGlobalGrantListViewController.prototype._fireGrantRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_SERVER);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaGlobalGrantListViewController.prototype._fireGrantRemovalEvent, details, false);
	}
}


// new button was pressed
ZaGlobalGrantListViewController.prototype._newButtonListener =
function(ev) {
	var newGrant = new ZaGrant();
	ZaApp.getInstance().getGrantViewController().show(newGrant);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaGrantController.show method
* in order to display the Grant View
**/
ZaGlobalGrantListViewController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getGrantViewController().show(ev.item);
		}
	} else {
		this.changeActionsState();
	}
}

ZaGlobalGrantListViewController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked.
* It call ZaGrantController.show method
* in order to display the Grant View
**/
ZaGlobalGrantListViewController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getGrantViewController().show(item);
	}
}

/**
* This listener is called when the Delete button is clicked.
**/
ZaGlobalGrantListViewController.prototype._deleteButtonListener =
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
		this._removeConfirmMessageDialog.setMessage(dlgMsg,DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaGlobalGrantListViewController.prototype._deletegrantsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaGlobalGrantListViewController.prototype._donotDeletegrantsCallback, this);
		this._removeConfirmMessageDialog.popup();
	}
}

ZaGlobalGrantListViewController.prototype._deletegrantsCallback =
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, ZaGlobalGrantListViewController.prototype._deletegrantsCallback, null, false);
				return;
			}
		}
		if (this._list) this._list.remove(this._removeList[key]); //remove from the list
	}
	this._fireGrantRemovalEvent(successRemList);
	this._removeConfirmMessageDialog.popdown();
	if (this._contentView) this._contentView.setUI();
	this.show();
}

ZaGlobalGrantListViewController.prototype._donotDeletegrantsCallback =
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaGlobalGrantListViewController.changeActionsState =
function () {
	if(this._contentView) {
		var cnt = this._contentView.getSelectionCount();
		 if (cnt > 1){
			if(this._toolbarOperations[ZaOperation.EDIT])
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;

			if(this._popupOperations[ZaOperation.EDIT])
				this._popupOperations[ZaOperation.EDIT].enabled = false;

		} else if (cnt <1) {
			if(this._toolbarOperations[ZaOperation.EDIT])
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;

			if(this._popupOperations[ZaOperation.EDIT])
				this._popupOperations[ZaOperation.EDIT].enabled = false;

		}
	}
}
ZaController.changeActionsStateMethods["ZaGlobalGrantListViewController"].push(ZaGlobalGrantListViewController.changeActionsStateMethod);