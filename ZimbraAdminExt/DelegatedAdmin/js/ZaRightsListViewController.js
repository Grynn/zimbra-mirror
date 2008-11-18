
ZaRightsListViewController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaRightsListViewController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();

	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_rights/managing_rights.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaRightsListViewController.prototype = new ZaListViewController();
ZaRightsListViewController.prototype.constructor = ZaRightsListViewController;

ZaController.initToolbarMethods["ZaRightsListViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaRightsListViewController"] = new Array();
ZaListViewController.changeActionsStateMethods["ZaRightsListViewController"] = new Array();

/**
* @param list {ZaItemList} a list of ZaRight {@link ZaRight} objects
**/
ZaRightsListViewController.prototype.show =
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

ZaRightsListViewController.initToolbarMethod =
function () {

    this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, com_zimbra_delegatedadmin.RIGHT_New_tt, "Account", "AccountDis", new AjxListener(this, ZaRightsListViewController.prototype._newButtonListener));
    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit,com_zimbra_delegatedadmin.RIGHT_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaRightsListViewController.prototype._editButtonListener));
   	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, com_zimbra_delegatedadmin.RIGHT_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaRightsListViewController.prototype._deleteButtonListener));
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));

    this._toolbarOrder.push(ZaOperation.NEW);
    this._toolbarOrder.push(ZaOperation.EDIT);
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
}
ZaController.initToolbarMethods["ZaRightsListViewController"].push(ZaRightsListViewController.initToolbarMethod);

ZaRightsListViewController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.SERTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaRightsListViewController.prototype._editButtonListener));
    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaRightsListViewController.prototype._deleteButtonListener));

}
ZaController.initPopupMenuMethods["ZaRightsListViewController"].push(ZaRightsListViewController.initPopupMenuMethod);

ZaRightsListViewController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaRightsListView(this._container);
		this._initToolbar();
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

		this._initPopupMenu();
		this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
//		ZaApp.getInstance().createView(ZaZimbraAdmin._RIGHTS_LIST_VIEW, elements);
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
		this._handleException(ex, "ZaRightsListViewController.prototype._createUI", null, false);
		return;
	}
}


/*
ZaRightsListViewController.prototype.refresh =
function() {
	try {
		this._contentView.set(ZaApp.getInstance().getRightList(true).getVector());
	} catch (ex) {
		this._handleException(ex, ZaRightsListViewController.prototype.refresh, null, false);
	}
}
*/

ZaRightsListViewController.prototype.set =
function(rightsList) {
	this.show(rightsList);
}

/**
* @param ev
* This listener is invoked by  any controller that can change an ZaRight object
**/
ZaRightsListViewController.prototype.handleRightChange =
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		//if(details["modFields"] && (details["modFields"][ZaRight.A_description] )) {
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
* This listener is invoked by ZaRightController or any other controller that can create an ZaRight object
**/
ZaRightsListViewController.prototype.handleRightCreation =
function (ev) {
	if(ev) {
		//add the new ZaRight to the controlled list
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
* This listener is invoked by ZaRightController or any other controller that can remove an ZaRight object
**/
ZaRightsListViewController.prototype.handleRightRemoval =
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
* Adds listener to removal of an ZaRight
* @param listener
**/
ZaRightsListViewController.prototype.addRightRemovalListener =
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/*
// refresh button was pressed
ZaRightsListViewController.prototype._refreshButtonListener =
function(ev) {
	this.refresh();
}
*/

/**
*	Private method that notifies listeners to that the controlled ZaRight (are) removed
* 	@param details
*/
ZaRightsListViewController.prototype._fireRightRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_SERVER);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaRightsListViewController.prototype._fireRightRemovalEvent, details, false);
	}
}


// new button was pressed
ZaRightsListViewController.prototype._newButtonListener =
function(ev) {
	var newRight = new ZaRight();
	ZaApp.getInstance().getRightViewController().show(newRight);
}

/**
* This listener is called when the item in the list is double clicked. It call ZaRightController.show method
* in order to display the Right View
**/
ZaRightsListViewController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getRightViewController().show(ev.item);
		}
	} else {
		this.changeActionsState();
	}
}

ZaRightsListViewController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}
/**
* This listener is called when the Edit button is clicked.
* It call ZaRightController.show method
* in order to display the Right View
**/
ZaRightsListViewController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		ZaApp.getInstance().getRightViewController().show(item);
	}
}

/**
* This listener is called when the Delete button is clicked.
**/
ZaRightsListViewController.prototype._deleteButtonListener =
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
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaRightsListViewController.prototype._deleteRightsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaRightsListViewController.prototype._donotDeleteRightsCallback, this);
		this._removeConfirmMessageDialog.popup();
	}
}

ZaRightsListViewController.prototype._deleteRightsCallback =
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				this._handleException(ex, ZaRightsListViewController.prototype._deleteRightsCallback, null, false);
				return;
			}
		}
		if (this._list) this._list.remove(this._removeList[key]); //remove from the list
	}
	this._fireRightRemovalEvent(successRemList);
	this._removeConfirmMessageDialog.popdown();
	if (this._contentView) this._contentView.setUI();
	this.show();
}

ZaRightsListViewController.prototype._donotDeleteRightsCallback =
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaRightsListViewController.changeActionsState =
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
ZaListViewController.changeActionsStateMethods["ZaRightsListViewController"].push(ZaRightsListViewController.changeActionsStateMethod);