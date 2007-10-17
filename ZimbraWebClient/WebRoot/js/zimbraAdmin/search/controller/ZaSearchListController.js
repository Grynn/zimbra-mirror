/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

/**
* @constructor
* @class ZaSearchListController This is a singleton class that controls all the user interaction with the list of ZaAccount objects
* @param appCtxt
* @param container
* @param app
* @extends ZaController
* @author Greg Solovyev
**/
ZaSearchListController = function(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app, "ZaSearchListController");
    //Account operations
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
   	
	this._currentPageNum = 1;
	this._currentQuery = null;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = "1";
	this.searchTypes = [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES, ZaSearch.DOMAINS];
	this.pages = new Object();
	this._searchPanel = null;
	this._searchField = null;
	this._helpURL = ZaSearchListController.helpURL;
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;	
	this.fetchAttrs = ZaSearch.standardAttributes;
}

ZaSearchListController.prototype = new ZaListViewController();
ZaSearchListController.prototype.constructor = ZaSearchListController;
ZaSearchListController.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/provisioning_accounts.htm";
ZaController.initToolbarMethods["ZaSearchListController"] = new Array();
ZaController.initPopupMenuMethods["ZaSearchListController"] = new Array();
ZaListViewController.changeActionsStateMethods["ZaSearchListController"] = new Array();
ZaSearchListController.prototype.show = function (doPush) {
	var callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,CONS:null,show:doPush});
	/*
	if (this._currentQuery == null) {
		this._currentQuery =  (ZaSearch._currentQuery ? ZaSearch._currentQuery : "");
	}*/
	
	var searchParams = {
			query: this._currentQuery, 
			types:this.searchTypes,
			sortBy:this._currentSortField,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
			attrs:this.fetchAttrs,
			callback:callback,
			controller: this
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaSearchListController.prototype._show = 
function (list, openInNewTab, openInSearchTab) {
	this._updateUI(list, openInNewTab, openInSearchTab);
	//this._app.pushView(ZaZimbraAdmin._SEARCH_LIST_VIEW);
	this._app.pushView(this.getContentViewId());
}

/**
* searh panel
*/	
ZaSearchListController.prototype.getSearchPanel = 
function () {
	if(!this._searchPanel) {
	    this._searchPanel = new DwtComposite(this._app.getAppCtxt().getShell(), "SearchPanel", DwtControl.ABSOLUTE_STYLE);
	    
		// Create search toolbar and setup browse tool bar button handlers
		this._searchToolBar = new ZaSearchToolBar(this._searchPanel, null, this._app);
	    
		// Setup search field handler
		this._searchField = this._searchToolBar.getSearchField();
		this._searchField.registerCallback(ZaSearchListController.prototype._searchFieldCallback, this);	
		this._searchPanel.zShow(true);		
	}
	return this._searchPanel;
}

ZaSearchListController.prototype.set = 
function(accountList) {
	this.show(accountList);
}

ZaSearchListController.prototype.setPageNum = 
function (pgnum) {
	this._currentPageNum = Number(pgnum);
}

ZaSearchListController.prototype.getPageNum = 
function () {
	return this._currentPageNum;
}

ZaSearchListController.prototype.getTotalPages = 
function () {
	return this.numPages;
}

ZaSearchListController.prototype.setFetchAttrs = 
function (fetchAttrs) {
	this.fetchAttrs = fetchAttrs;
}

ZaSearchListController.prototype.getFetchAttrs = 
function () {
	return this.fetchAttrs;
}

ZaSearchListController.prototype.setQuery = 
function (query) {
	this._currentQuery = query;
}

ZaSearchListController.prototype.getQuery = 
function () {
	return this._currentQuery;
}

ZaSearchListController.prototype.setSearchTypes = 
function (searchTypes) {
	this.searchTypes = searchTypes;
}

ZaSearchListController.prototype.getSearchTypes = 
function () {
	return this.searchTypes;
}

ZaSearchListController.prototype.setSortOrder = 
function (sortOrder) {
	this._currentSortOrder = sortOrder;
}

ZaSearchListController.prototype.getSortOrder = 
function () {
	return this._currentSortOrder;
}

ZaSearchListController.prototype.setSortField = 
function (sortField) {
	this._currentSortField = sortField;
}

ZaSearchListController.prototype.getSortField = 
function () {
	return this._currentSortField;
}

/*********** Search Field Callback */
ZaSearchListController.prototype._searchFieldCallback =
function(params) {
	var callback;
	var controller = this;
	callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,show:true});
	/*if((AjxUtil.indexOf(params.types,ZaSearch.DOMAINS)>-1) && 
			((AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1) || 
				(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) || 
				(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) ||
				(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1))) {

			callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,show:true});			
	} else if (AjxUtil.indexOf(params.types,ZaSearch.DOMAINS)>-1) {
		controller = this._app.getDomainListController(null, true);
	} else if((AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1) || 
				(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) || 
				(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) ||
				(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1)) {
		
		controller = this._app.getAccountListController(null, true);
		
		if((AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1)&& 
				!(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) && 
				!(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) &&
				!(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1) ) {
			controller.setDefaultType(ZaItem.ACCOUNT);
		} else if(!(AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1)&& 
				(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) && 
				!(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) &&
				!(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1)) { 
			controller.setDefaultType(ZaItem.ALIAS);
		} else if(!(AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1)&& 
				!(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) && 
				(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) &&
				!(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1)) { 
			controller.setDefaultType(ZaItem.RESOURCE);
		} else if(!(AjxUtil.indexOf(params.types,ZaSearch.ACCOUNTS)>-1)&& 
				!(AjxUtil.indexOf(params.types,ZaSearch.ALIASES)>-1) && 
				!(AjxUtil.indexOf(params.types,ZaSearch.RESOURCES)>-1) &&
				(AjxUtil.indexOf(params.types,ZaSearch.DLS)>-1)) { 
			controller.setDefaultType(ZaItem.DL);
		} 
	}*/
	if(controller.setSearchTypes)
		controller.setSearchTypes(params.types);
	
	controller._currentQuery = params.query ;
		
	var callback = new AjxCallback(controller, controller.searchCallback, {limit:controller.RESULTSPERPAGE,show:true, openInSearchTab: true});
	var searchParams = {
			query:params.query, 
			types:params.types,
			sortBy:params.sortBy,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
			attrs:ZaSearch.standardAttributes,
			callback:callback,
			controller: controller
	}
	ZaSearch.searchDirectory(searchParams);
}


ZaSearchListController.initPopupMenuMethod =
function () {
    this._popupOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSearchListController.prototype._editButtonListener)));
	this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSearchListController.prototype._deleteButtonListener)));
	if(ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED)	
		this._popupOperations.push(new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener)));		    	
	
}
ZaController.initPopupMenuMethods["ZaSearchListController"].push(ZaSearchListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaSearchListController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
    this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaSearchListController.prototype._editButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaSearchListController.prototype._deleteButtonListener)));
	if(ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED) {	
		this._toolbarOperations.push(new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener)));		    	
	}	
}
ZaController.initToolbarMethods["ZaSearchListController"].push(ZaSearchListController.initToolbarMethod);

ZaSearchListController.prototype.reset =
function () {
	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
   	
	this._currentPageNum = 1;
	this._currentQuery = null;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = "1";
	this.pages = new Object();
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;	
}

//private and protected methods
ZaSearchListController.prototype._createUI = 
function () {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = new ZaSearchListView(this._container, this._app);
	this._app._controllers[this.getContentViewId ()] = this ;
	this._newDLListener = new AjxListener(this, ZaSearchListController.prototype._newDistributionListListener);
	this._newAcctListener = new AjxListener(this, ZaSearchListController.prototype._newAccountListener);
	this._newResListener = new AjxListener(this, ZaSearchListController.prototype._newResourceListener);

    this._initToolbar();
	//always add Help and navigation buttons at the end of the toolbar    
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener)));
	//add the acount number counts
	ZaSearch.searchResultCountsView(this._toolbarOperations);
	this._toolbarOperations.push(new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				

	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	//this._app.createView(ZaZimbraAdmin._SEARCH_LIST_VIEW, elements);
	//always open the search list view in the search tab
	var tabParams = {
		openInNewTab: false,
		tabId: this.getContentViewId(),
		tab: this._app.getTabGroup().getSearchTab ()
	}
	this._app.createView(this.getContentViewId(), elements, tabParams) ;
	
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	this._removeConfirmMessageDialog = this._app.dialogs["ConfirmMessageDialog"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
	this._UICreated = true;
}

ZaSearchListController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	}else{
		this._app.popView () ;
	}
	
	//reset the search text when the search list view/tab is closed
	var searchFieldXForm = this._searchField._localXForm ;
	var searchFieldItem = searchFieldXForm.getItemsById(ZaSearch.A_query)[0];
	searchFieldItem.getElement().value = "" ;
}

// new account button was pressed
ZaSearchListController.prototype._newAccountListener =
function(ev) {

	try {
		var newAccount = new ZaAccount(this._app);
		if(!this._app.dialogs["newAccountWizard"])
			this._app.dialogs["newAccountWizard"] = new ZaNewAccountXWizard(this._container, this._app);	

		this._app.dialogs["newAccountWizard"].setObject(newAccount);
		this._app.dialogs["newAccountWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newAccountListener", null, false);
	}
}

ZaSearchListController.prototype._newDistributionListListener =
function(ev) {
	try {
		var newDL = new ZaDistributionList(this._app);
		this._app.getDistributionListController().show(newDL);
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newDistributionListListener", null, false);
	}

};

ZaSearchListController.prototype._newResourceListener =
function(ev) {
	try {
		var newResource = new ZaResource(this._app);
		if(!this._app.dialogs["newResourceWizard"])
			this._app.dialogs["newResourceWizard"] = new ZaNewResourceXWizard(this._container, this._app);	

		this._app.dialogs["newResourceWizard"].setObject(newResource);
		this._app.dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaSearchListController.prototype._newResourceListener", null, false);
	}
}


/**
* This listener is called when the item in the list is double clicked. It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._editItem(ev.item);
		}
	} else {
		this.changeActionsState();
	}
}

ZaSearchListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._editItem(item);
	}
}

/**
* This listener is called when the Delete button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaSearchListController.prototype._deleteButtonListener =
function(ev) {
	ZaAccountListController.prototype._deleteButtonListener.call(this, ev);
}

ZaSearchListController.prototype._deleteAccountsInRemoveList =
function (ev) {
	ZaAccountListController.prototype._deleteAccountsInRemoveList.call (this, ev) ;
}

ZaSearchListController.prototype._editItem = function (item) {
	var type = item.type;
	if (type == ZaItem.ACCOUNT) {
		this._app.getAccountViewController().show(item);
	} else if (type == ZaItem.DL) {
		this._app.getDistributionListController().show(item);
	} else if(type == ZaItem.ALIAS) {
		var targetObj = item.getAliasTargetObj() ;
		
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {			
			this._app.getAccountViewController().show(targetObj, true);
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
			this._app.getDistributionListController().show(targetObj, true);
		}
	} else if (type == ZaItem.RESOURCE ){
		this._app.getResourceController().show(item);
	} else if (type==ZaItem.DOMAIN) {
		this._app.getDomainController().show(item);
	}
};

ZaSearchListController.changeActionsStateMethod = 
function (opsArray1, opsArray2) {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];		
		opsArray1.push(ZaOperation.EDIT)
		opsArray1.push(ZaOperation.DELETE);
		if(item.type == ZaItem.ALIAS) {
			opsArray1.push(ZaOperation.MOVE_ALIAS);
		} else {
			opsArray2.push(ZaOperation.MOVE_ALIAS);			
		}	
	} else if (cnt > 1){
		opsArray1.push(ZaOperation.DELETE);
		opsArray2.push(ZaOperation.EDIT)
		opsArray2.push(ZaOperation.MOVE_ALIAS);
	} else {
		opsArray2.push(ZaOperation.DELETE);
		opsArray2.push(ZaOperation.EDIT)
		opsArray2.push(ZaOperation.MOVE_ALIAS);		
	}
}
ZaListViewController.changeActionsStateMethods["ZaSearchListController"].push(ZaSearchListController.changeActionsStateMethod);