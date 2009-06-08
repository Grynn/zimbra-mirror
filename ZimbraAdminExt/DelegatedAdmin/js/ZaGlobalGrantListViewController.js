/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
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

ZaGlobalGrantListViewController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container,"ZaGlobalGrantListViewController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();
    
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "da_process/da_global_acl.htm?locid="+AjxEnv.DEFAULT_LOCALE;
    this.addCreationListener(new AjxListener(this, this.handleGrantCreation));
    this.addRemovalListener(new AjxListener(this, this.handleGrantRemoval));			
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

    ZaApp.getInstance().pushView(this.getContentViewId());
	if (list != null) {
		this._list = list;
        this._updateUI () ;
    }
}

/*
1) Display the new list with either added or removed. Make sure that sorting or paging are correct
2) update the paging information.

 */
ZaGlobalGrantListViewController.prototype._updateUI =
function() {
    if (!this._list) return ;

//    this.RESULTSPERPAGE = 2 ;
    this.numPages = this._list.getVector().size() / this.RESULTSPERPAGE ;
    var s_result_start_n = (this._currentPageNum - 1) * this.RESULTSPERPAGE + 1;
	var s_result_end_n = this._currentPageNum  * this.RESULTSPERPAGE;
    if(this.numPages <= this._currentPageNum) {
		s_result_end_n = this._list.getVector().size () ;
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], true);
	}
	if(this._currentPageNum == 1) {
		this._toolbar.enable([ZaOperation.PAGE_BACK], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_BACK], true);
	}

    var tmpArr = new Array();

    for(var ix = s_result_start_n - 1; ix < s_result_end_n; ix++) {
        tmpArr.push(this._list.getVector().getArray()[ix]);
    }
    //add the default column sortable
    this._contentView._bSortAsc = (this._currentSortOrder=="1");
    this._contentView.set(AjxVector.fromArray(tmpArr), this._contentView._defaultColumnSortable);

	//update the search result number count now
	var srCountBt = this._toolbar.getButton (ZaOperation.SEARCH_RESULT_COUNT) ;
	if (srCountBt ) {
        var total ;
		if  (this._list == null || this._list.getVector().size () <= 0) {
			s_result_end_n = 0;
			s_result_start_n = 0;
            total = 0 ;
		} else{
            total = this._list.getVector().size () ;
        }
		srCountBt.setText ( AjxMessageFormat.format (ZaMsg.searchResultCount,
				[s_result_start_n + " - " + s_result_end_n, total]));
	}

    this._removeList = new Array();
    this.changeActionsState();

}

ZaListViewController.prototype._nextPageListener =
function (ev) {
	if(this._currentPageNum < this.numPages) {
		this._currentPageNum++;
	}
    this._updateUI () ;
}

ZaListViewController.prototype._prevPageListener =
function (ev) {
	if(this._currentPageNum > 1) {
		this._currentPageNum--;
	}
    this._updateUI () ;
}


ZaGlobalGrantListViewController.initToolbarMethod =
function () {

    this._toolbarOrder.push(ZaOperation.NEW);
//    this._toolbarOrder.push(ZaOperation.EDIT);
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._toolbarOrder.push(ZaOperation.NONE);

    this._toolbarOrder.push(ZaOperation.PAGE_BACK) ;
    this._toolbarOrder.push(ZaOperation.PAGE_FORWARD) ;
    this._toolbarOrder.push(ZaOperation.HELP);

    this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW, com_zimbra_delegatedadmin.Bt_grant, com_zimbra_delegatedadmin.Grant_New_tt, "GlobalPermission", "GlobalPermissionDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._newButtonListener));
//    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit,com_zimbra_delegatedadmin.RIGHT_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._editButtonListener));
   	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, com_zimbra_delegatedadmin.Bt_revoke, com_zimbra_delegatedadmin.Grant_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._deleteButtonListener));
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
    this._toolbarOperations[ZaOperation.PAGE_BACK] = new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener));

    //add the acount number counts
    ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);
    this._toolbarOperations[ZaOperation.PAGE_FORWARD] = new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener));
    this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));


}
ZaController.initToolbarMethods["ZaGlobalGrantListViewController"].push(ZaGlobalGrantListViewController.initToolbarMethod);

ZaGlobalGrantListViewController.initPopupMenuMethod =
function () {
   	this._popupOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, com_zimbra_delegatedadmin.Bt_revoke, com_zimbra_delegatedadmin.Grant_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._deleteButtonListener));
//    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.SERTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaGlobalGrantListViewController.prototype._deleteButtonListener));

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
//      Disable double click for the global grant list
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
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show(this._list);
			}
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
            this._currentPageNum = 1;
//			if (this._contentView) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show(this._list);
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
            this._currentPageNum = 1;
//			if (this._contentView ) this._contentView.setUI();
			if(ZaApp.getInstance().getCurrentController() == this) {
			    this.show(this._list);
			}
		}
	}
}

// new button was pressed
ZaGlobalGrantListViewController.prototype._newButtonListener =
function(ev) {
	var newGrant = new ZaGrant();
//	ZaApp.getInstance().getGrantViewController().show(newGrant);
	if(!this.grantRightDlg) {
		this.grantRightDlg = new ZaGrantDialog (
                ZaApp.getInstance().getAppCtxt().getShell(),
                ZaApp.getInstance(), com_zimbra_delegatedadmin.Title_grant_rights);
		this.grantRightDlg.registerCallback(ZaGrantDialog.ADD_FINISH_BUTTON, ZaGrantDialog.grantGlobalGrant, this, null);
        this.grantRightDlg.registerCallback(ZaGrantDialog.ADD_MORE_BUTTON, ZaGrantDialog.grantMoreGlobalGrant, this, null);
	}

	var obj = {};
	obj[ZaGrant.A_target] = ZaGrant.GLOBAL_TARGET_NAME;
    obj[ZaGrant.A_target_type] = "global" ;

    obj.setAttrs = {} ;
    obj.setAttrs.all = true ;
    this.grantRightDlg.setObject(obj);
	this.grantRightDlg.popup();
}

/**
* This listener is called when the Delete button is clicked.
**/
ZaGlobalGrantListViewController.prototype._deleteButtonListener =
function(ev) {

    var selectedGrant = this._contentView.getSelection();
    if (selectedGrant && selectedGrant.length > 0) {
        if(!this.revokeRightDlg) {
            this.revokeRightDlg = new ZaMsgDialog (
                    ZaApp.getInstance().getAppCtxt().getShell(),
                    null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);
        }
        this.revokeRightDlg.registerCallback(DwtDialog.YES_BUTTON, ZaGrantsListView.revokeGlobalGrant, this, null);
        var confirmMsg =  com_zimbra_delegatedadmin.confirm_delete_grants + ZaTargetPermission.getDlMsgFromGrant(selectedGrant) ;
        this.revokeRightDlg.setMessage (confirmMsg,  DwtMessageDialog.INFO_STYLE) ;
        this.revokeRightDlg.popup ();
    } else {
        ZaApp.getInstance().getCurrentController().popupMsgDialog (com_zimbra_delegatedadmin.no_grant_selected_msg) ;
    }
}

/**
* This listener is called when the item in the list is double clicked. It call ZaGrantController.show method
* in order to display the Grant View
**/
ZaGlobalGrantListViewController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		/*
        if(ev.item) {
			this._selectedItem = ev.item;
			ZaApp.getInstance().getGrantViewController().show(ev.item);
		} */
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

ZaGlobalGrantListViewController.changeActionsStateMethod =
function () {
	if(this._contentView) {
		var cnt = this._contentView.getSelectionCount();
		 if (cnt == 1){
            if(this._toolbarOperations[ZaOperation.DELETE])
				this._toolbarOperations[ZaOperation.DELETE].enabled = true;

			if(this._popupOperations[ZaOperation.DELETE])
				this._popupOperations[ZaOperation.DELETE].enabled = true;

		} else {
			if(this._toolbarOperations[ZaOperation.DELETE])
				this._toolbarOperations[ZaOperation.DELETE].enabled = false;

			if(this._popupOperations[ZaOperation.DELETE])
				this._popupOperations[ZaOperation.DELETE].enabled = false;

		}
	}
}
ZaController.changeActionsStateMethods["ZaGlobalGrantListViewController"].push(ZaGlobalGrantListViewController.changeActionsStateMethod);