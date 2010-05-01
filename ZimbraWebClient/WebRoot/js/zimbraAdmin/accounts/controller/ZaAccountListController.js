/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaAccountListController This is a singleton class that controls all the user interaction with the list of ZaAccount objects
* @param appCtxt
* @param container
* @param app
* @extends ZaController
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaAccountListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container, "ZaAccountListController");
    //Account operations
   	this._toolbarOperations = new Object();
   	this._popupOperations = new Object();			
   	
	this._currentPageNum = 1;
	this._currentQuery = null;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = "1";
	this.searchTypes = [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES];
	this.pages = new Object();
	this._searchPanel = null;
	this._searchField = null;                                          
	this._defaultType = ZaItem.ACCOUNT;
	this._helpURL = ZaAccountListController.helpURL;
	this.objType = ZaEvent.S_ACCOUNT;	
	this.fetchAttrs = ZaSearch.standardAttributes;
}

ZaAccountListController.prototype = new ZaListViewController();
ZaAccountListController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/provisioning_accounts.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaController.initToolbarMethods["ZaAccountListController"] = new Array();
ZaController.initPopupMenuMethods["ZaAccountListController"] = new Array();
ZaController.changeActionsStateMethods["ZaAccountListController"] = new Array(); 


ZaAccountListController.prototype.show = function (doPush) {
	var busyId = Dwt.getNextId();
	var callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,CONS:null,show:doPush,busyId:busyId});
	
	var searchParams = {
			query:this._currentQuery ,
			types:this.searchTypes,
			sortBy:this._currentSortField,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:this._currentSortOrder,
			limit:this.RESULTSPERPAGE,
            attrs: [this.fetchAttrs, ZaAccount.A_zimbraIsDelegatedAdminAccount, ZaAccount.A_zimbraIsAdminAccount, ZaAccount.A_zimbraIsSystemResource].join(),
			callback:callback,
			controller: this,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING,
			skipCallbackIfCancelled:false
	}
	
	ZaSearch.searchDirectory(searchParams);
}

ZaAccountListController.prototype._show = 
function (list, openInNewTab, openInSearchTab) {
	this._updateUI(list, openInNewTab, openInSearchTab);
//	ZaApp.getInstance().pushView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId (), openInNewTab, openInSearchTab);
	this.updateToolbar();
	//TODO: need to standardize the way to handle the tab.
	//hacking: currently, dllistview, aliasListView, accountListView and resourceListView share the same controller instance. It is BAD!
	//It should be changed when we allow the list view to be open in a new tab
	if (openInSearchTab) {
		ZaApp.getInstance().updateSearchTab();
	}else{
		ZaApp.getInstance().updateTab(this.getMainTab(), ZaApp.getInstance()._currentViewId );
	}
}

ZaAccountListController.prototype.setDefaultType = function (type) {
	// set the default type,
	this._defaultType = type;
	
};

ZaAccountListController.prototype.updateToolbar = 
function () {
	if(!this._toolbar)
		return;
	
	var type = this._defaultType ;	
	var newButton = this._toolbar.getButton(ZaOperation.NEW_MENU);	
	if (newButton != null) {
		newButton.removeSelectionListeners();
		// set the new menu action
		if (type == ZaItem.ACCOUNT ) {
			newButton.setToolTipContent(ZaMsg.ACTBB_New_tt);
			newButton.setImage("Account");
			newButton.addSelectionListener(this._newAcctListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.ACTBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.ACTBB_Delete_tt);
			if(this._toolbar.getButton(ZaOperation.CHNG_PWD))			
				this._toolbar.getButton(ZaOperation.CHNG_PWD).setToolTipContent(ZaMsg.ACTBB_ChngPwd_tt);
			
			if(this._toolbar.getButton(ZaOperation.EXPIRE_SESSION))	
				this._toolbar.getButton(ZaOperation.EXPIRE_SESSION).setToolTipContent(ZaMsg.ACTBB_ExpireSessions_tt);
				
		} else if (type == ZaItem.ALIAS) {
			newButton.setToolTipContent(ZaMsg.ALTBB_New_tt);
			newButton.setImage("AccountAlias");
			newButton.addSelectionListener(this._newALListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.ACTBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.ALTBB_Delete_tt);
			if(this._toolbar.getButton(ZaOperation.CHNG_PWD))
				this._toolbar.getButton(ZaOperation.CHNG_PWD).setToolTipContent(ZaMsg.ACTBB_ChngPwd_tt);
		} else if (type == ZaItem.DL) {
			newButton.setToolTipContent(ZaMsg.DLTBB_New_tt);
			newButton.setImage("DistributionList");
			newButton.addSelectionListener(this._newDLListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.DLTBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.DLTBB_Delete_tt);
		} else if (type == ZaItem.RESOURCE ){
		  	newButton.setToolTipContent(ZaMsg.RESTBB_New_tt);
			newButton.setImage("Resource");
			newButton.addSelectionListener(this._newResListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.RESBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.RESBB_Delete_tt);
			if(this._toolbar.getButton(ZaOperation.CHNG_PWD))
				this._toolbar.getButton(ZaOperation.CHNG_PWD).setToolTipContent(ZaMsg.RESBB_CHNG_PWD_tt);
		}
	}
}


ZaAccountListController.prototype.set = 
function(accountList) {
	this.show(accountList);
}

ZaAccountListController.prototype.setPageNum = 
function (pgnum) {
	this._currentPageNum = Number(pgnum);
}

ZaAccountListController.prototype.getPageNum = 
function () {
	return this._currentPageNum;
}

ZaAccountListController.prototype.getTotalPages = 
function () {
	return this.numPages;
}

ZaAccountListController.prototype.setFetchAttrs = 
function (fetchAttrs) {
	this.fetchAttrs = fetchAttrs;
}

ZaAccountListController.prototype.getFetchAttrs = 
function () {
	return this.fetchAttrs;
}

ZaAccountListController.prototype.setQuery = 
function (query) {
	this._currentQuery = query;
}

ZaAccountListController.prototype.getQuery = 
function () {
	return this._currentQuery;
}

ZaAccountListController.prototype.setSearchTypes = 
function (searchTypes) {
	this.searchTypes = searchTypes;
}

ZaAccountListController.prototype.getSearchTypes = 
function () {
	return this.searchTypes;
}

ZaAccountListController.prototype.setSortOrder = 
function (sortOrder) {
	if(sortOrder===true)
		this._currentSortOrder = "1";
	else if(sortOrder===false)
		this._currentSortOrder = "0";
	else
		this._currentSortOrder = sortOrder;
}

ZaAccountListController.prototype.getSortOrder = 
function () {
	return this._currentSortOrder;
}

ZaAccountListController.prototype.setSortField = 
function (sortField) {
	this._currentSortField = sortField;
}

ZaAccountListController.prototype.getSortField = 
function () {
	return this._currentSortField;
}



ZaAccountListController.initPopupMenuMethod =
function () {
    this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
	this._popupOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener));
	
	if(this._defaultType == ZaItem.ACCOUNT) {
		this._popupOperations[ZaOperation.CHNG_PWD] = new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener));
		this._popupOperations[ZaOperation.EXPIRE_SESSION] = new ZaOperation(ZaOperation.EXPIRE_SESSION, ZaMsg.ACTBB_ExpireSessions, ZaMsg.ACTBB_ExpireSessions_tt, "ExpireSession", "ExpireSessionDis", new AjxListener(this, ZaAccountListController.prototype._expireSessionListener));
	}

	this._popupOperations[ZaOperation.VIEW_MAIL] = new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener));		

	if(this._defaultType == ZaItem.ALIAS) {	
		this._popupOperations[ZaOperation.MOVE_ALIAS] = new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener));
	}		    	
}
ZaController.initPopupMenuMethods["ZaAccountListController"].push(ZaAccountListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaAccountListController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
	var newMenuOpList = new Array();
	var showNewAccount = false;
	var showNewDL = false;
	var showNewCalRes = false;
	var showNewAlias = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showNewAccount = true;
		showNewDL = true;
		showNewCalRes = true;
		showNewAlias = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,domainList[i])) {
				showNewAccount = true;
			}	
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_CALRES,domainList[i])) {
				showNewCalRes = true;
			}
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_DL,domainList[i])) {
				showNewDL = true;
			}				
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ALIAS,domainList[i])) {
				showNewAlias = true;
			}
			if(showNewAlias && showNewDL && showNewCalRes && showNewAccount) {
				break;
			}
		}
	}
	
	if(showNewAccount) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.ACTBB_New_menuItem, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener));
	}
	if(showNewAlias) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.ALTBB_New_menuItem, ZaMsg.ALTBB_New_tt, "AccountAlias", "AccountAliasDis", this._newALListener));
	}
	if(showNewDL) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.DLTBB_New_menuItem, ZaMsg.DLTBB_New_tt, "DistributionList", "DistributionListDis", this._newDLListener));
	}
	if(showNewCalRes) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.RESTBB_New_menuItem, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", this._newResListener));
	}	
		
	if(showNewAccount && this._defaultType == ZaItem.ACCOUNT) {
		this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener, 
								   ZaOperation.TYPE_MENU, newMenuOpList);
    } else if (showNewAlias && this._defaultType == ZaItem.ALIAS) {
		this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ALTBB_New_tt, "AccountAlias", "AccountAliasDis", this._newALListener, 
								   ZaOperation.TYPE_MENU, newMenuOpList);
    }else if(showNewCalRes && this._defaultType == ZaItem.RESOURCE) {
		this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", this._newResListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList);
    } else if(showNewDL && this._defaultType == ZaItem.DL) {    	
		this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "DistributionList", "DistributionListDis", this._newDLListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList);
    } 
	
    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener));
	
	if(this._defaultType == ZaItem.ACCOUNT) {
		this._toolbarOperations[ZaOperation.CHNG_PWD] = new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener));
		this._toolbarOperations[ZaOperation.EXPIRE_SESSION] = new ZaOperation(ZaOperation.EXPIRE_SESSION, ZaMsg.ACTBB_ExpireSessions, ZaMsg.ACTBB_ExpireSessions_tt, "ExpireSession", "ExpireSessionDis", new AjxListener(this, ZaAccountListController.prototype._expireSessionListener));
	}
	this._toolbarOperations[ZaOperation.VIEW_MAIL] = new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener));		

	if(this._defaultType == ZaItem.ALIAS) {	
		this._toolbarOperations[ZaOperation.MOVE_ALIAS] = new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener));		    	
	}
	
	if(this._toolbarOperations[ZaOperation.NEW_MENU]) {
		this._toolbarOrder.push(ZaOperation.NEW_MENU);
	}
	this._toolbarOrder.push(ZaOperation.EDIT);
	this._toolbarOrder.push(ZaOperation.DELETE);
	if(this._defaultType == ZaItem.ACCOUNT) {
		this._toolbarOrder.push(ZaOperation.CHNG_PWD);
		this._toolbarOrder.push(ZaOperation.EXPIRE_SESSION);
	}
	this._toolbarOrder.push(ZaOperation.VIEW_MAIL);
	if(this._defaultType == ZaItem.ALIAS) {
		this._toolbarOrder.push(ZaOperation.MOVE_ALIAS);
	}		
}
ZaController.initToolbarMethods["ZaAccountListController"].push(ZaAccountListController.initToolbarMethod);

//private and protected methods
ZaAccountListController.prototype._createUI = 
function (openInNewTab, openInSearchTab) {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = new ZaAccountListView(this._container, this._defaultType);
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	
	this._newDLListener = new AjxListener(this, ZaAccountListController.prototype._newDistributionListListener);
	this._newAcctListener = new AjxListener(this, ZaAccountListController.prototype._newAccountListener);
	this._newResListener = new AjxListener(this, ZaAccountListController.prototype._newResourceListener);
	this._newALListener = new AjxListener(this, ZaAccountListController.prototype._newAliasListener);
   
    this._initToolbar();
	//always add Help and navigation buttons at the end of the toolbar    
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);	
	this._toolbarOperations[ZaOperation.PAGE_BACK] = new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener));
	
	this._toolbarOrder.push(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.PAGE_BACK);
	this._toolbarOrder.push(ZaOperation.PAGE_FORWARD);
	this._toolbarOrder.push(ZaOperation.HELP);	
	//add the acount number counts
	ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);
	
	this._toolbarOperations[ZaOperation.PAGE_FORWARD] = new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener));
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				

	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	//ZaApp.getInstance().createView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW, elements);
	var tabParams = {
		openInNewTab: false,
		tabId: this.getContentViewId(),
		tab: openInSearchTab ? this.getSearchTab() : this.getMainTab() 
	}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams);
	
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	if(!ZaApp.getInstance().dialogs["ConfirmMessageDialog"])
		ZaApp.getInstance().dialogs["ConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON]);			
	
	this._UICreated = true;
	
}

ZaAccountListController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	}else{
		ZaApp.getInstance().popView () ;
	}
}

// new account button was pressed
ZaAccountListController.prototype._newAccountListener =
function(ev) {

	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAccount = new ZaAccount();
		newAccount.loadNewObjectDefaults("name", ZaSettings.myDomainName);
		
		if(!ZaApp.getInstance().dialogs["newAccountWizard"])
			ZaApp.getInstance().dialogs["newAccountWizard"] = new ZaNewAccountXWizard(this._container,newAccount);	
        else { //update the account type if needed
            ZaApp.getInstance().dialogs["newAccountWizard"].updateAccountType () ;    
        }

		ZaApp.getInstance().dialogs["newAccountWizard"].setObject(newAccount);
		ZaApp.getInstance().dialogs["newAccountWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAccountListener", null, false);
	}
}

// new alias button was pressed
ZaAccountListController.prototype._newAliasListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAlias = new ZaAlias();
		newAlias.getAttrs = {all:true};
		newAlias.setAttrs = {all:true};		
		newAlias._defaultValues = {attrs:{}};
		newAlias.rights = {};
		//newAlias.loadNewObjectDefaults("name", ZaSettings.myDomainName);
		if(!ZaApp.getInstance().dialogs["newAliasDialog"]) {
			ZaApp.getInstance().dialogs["newAliasDialog"] = new ZaNewAliasXDialog(
				this._container, "550px", "100px",ZaMsg.New_Alias_Title );	
			ZaApp.getInstance().dialogs["newAliasDialog"].registerCallback(
					DwtDialog.OK_BUTTON, ZaAlias.prototype.addAlias, 
					newAlias, ZaApp.getInstance().dialogs["newAliasDialog"]._localXForm );								
		}

		ZaApp.getInstance().dialogs["newAliasDialog"].setObject(newAlias);
		ZaApp.getInstance().dialogs["newAliasDialog"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAliasListener", null, false);
	}
}


ZaAccountListController.prototype._newDistributionListListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (this);
		var newDL = new ZaDistributionList();
		//newDL.getAttrs = {all:true};
		//newDL.setAttrs = {all:true};
		newDL.rights = {};
		newDL._defaultValues = {attrs:{}};
		newDL.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		newDL.rights[ZaDistributionList.RENAME_DL_RIGHT]=true;
		newDL.rights[ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]=true;
		newDL.rights[ZaDistributionList.ADD_DL_MEMBER_RIGHT]=true;
		ZaApp.getInstance().getDistributionListController().show(newDL);
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newDistributionListListener", null, false);
	}

};

ZaAccountListController.prototype._newResourceListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (this);
		var newResource = new ZaResource();
		//newResource.getAttrs = {all:true};
		//newResource._defaultValues = {attrs:{}};	
		newResource.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		if(!ZaApp.getInstance().dialogs["newResourceWizard"])
			ZaApp.getInstance().dialogs["newResourceWizard"] = new ZaNewResourceXWizard(this._container);	

		ZaApp.getInstance().dialogs["newResourceWizard"].setObject(newResource);
		ZaApp.getInstance().dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newResourceListener", null, false);
	}
}


/**
* This listener is called when the item in the list is double clicked. It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaAccountListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		//console.log("double click");
		if(ev.item) {
			//console.log("edit item");
			this._editItem(ev.item);
		}
	} else {
		//console.log("single click");
		var act = new AjxTimedAction(this,ZaController.prototype.changeActionsState,[ev]);
		this.changeAcStateAcId = AjxTimedAction.scheduleAction(act,ZaController.CLICK_DELAY);
		//this.changeActionsState();
	}
}

ZaAccountListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaAccountListController.prototype._editButtonListener =
function(ev) {
	EmailAddr_XFormItem.resetDomainLists.call (this) ;
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._editItem(item);
	}
}

ZaAccountListController.prototype._editItem = function (item) {
	if(this.changeAcStateAcId) {
		AjxTimedAction.cancelAction(this.changeAcStateAcId);
		this.changeAcStateAcId = null;
	}	
	//check if the item already open in a tab
	var itemId = item.id ;
	if((item.type == ZaItem.ALIAS) && item.attrs && item.attrs[ZaAlias.A_AliasTargetId]) {
		itemId = item.attrs[ZaAlias.A_AliasTargetId];
	}
	var type = item.type;
	var viewContstructor = ZaAccountXFormView;
	if (type == ZaItem.ACCOUNT) {
		viewContstructor = ZaAccountXFormView;	
	} else if (type == ZaItem.DL) {
		viewContstructor = ZaDLXFormView;	
	} else if (type == ZaItem.RESOURCE ){
		viewContstructor = ZaResourceXFormView;
	} else if (type == ZaItem.ALIAS) {
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {	
			viewController = ZaAccountXFormView;
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
		    viewController = ZaDLXFormView;
		}
	}
		
	if (! this.selectExistingTabByItemId(itemId,viewContstructor)){
//		DBG.println("TYPE == ", item.type);
		if (type == ZaItem.ACCOUNT) {
			//this._selectedItem = ev.item;
			ZaApp.getInstance().getAccountViewController().show(item, true);
		} else if (type == ZaItem.DL) {
			ZaApp.getInstance().getDistributionListController().show(item, true);
		} else if(type == ZaItem.ALIAS) {
			var targetObj = item.getAliasTargetObj() ;
			
			if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {			
				ZaApp.getInstance().getAccountViewController().show(targetObj, true);
			}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
				ZaApp.getInstance().getDistributionListController().show(targetObj, true);
			}
		} else if (type == ZaItem.RESOURCE ){
			ZaApp.getInstance().getResourceController(itemId).show(item, true);
		}
	}
};
/**
* This listener is called when the Change Password button is clicked. 
**/
ZaAccountListController.prototype._chngPwdListener =
function(ev) {
	if(this._contentView.getSelectionCount()==1) {
		this._chngPwdDlg = new ZaAccChangePwdXDlg(ZaApp.getInstance().getAppCtxt().getShell(), "400px","90px");
		var item = this._contentView.getSelection()[0];
		item.loadEffectiveRights("id", item.id, false);
		this._chngPwdDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountListController._changePwdOKCallback, this, item);				
		this._chngPwdDlg.setTitle(ZaMsg.CHNP_Title + " (" + item.name + ")");
		var obj = new Object();
		obj[ZaAccount.A2_confirmPassword]="";
		obj.attrs = {};
		obj.attrs[ZaAccount.A_password]="";
		obj.attrs[ZaAccount.A_zimbraPasswordMustChange]=false;
		obj.getAttrs = item.getAttrs;
		obj.setAttrs = item.setAttrs;
		this._chngPwdDlg.setObject(obj)
		this._chngPwdDlg.popup();
	}
}

ZaAccountListController.prototype._expireSessionListener = 
function(ev) {
	if(this._contentView.getSelectionCount()==1) {
		var item = this._contentView.getSelection()[0];
		item.loadEffectiveRights("id", item.id, false);
		if(ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item)) {
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.WARN_EXPIRE_SESSIONS, DwtMessageDialog.WARNING_STYLE);
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaAccountListController.prototype.expireSessions, this, [item]);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
			ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
		} else {
			this.popupMsgDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_PERMISSION_FOR_OPERATION_ON, [item.name ? item.name : item.attrs[ZaAccount.A_accountName]]), true);
		}
	}
}

ZaAccountListController.prototype.expireSessions = 
function(acct) {
	ZaApp.getInstance().dialogs["confirmMessageDialog"].popdown();
	mods = {};
	mods[ZaAccount.A_zimbraAuthTokenValidityValue] = (!acct.attrs[ZaAccount.A_zimbraAuthTokenValidityValue] ? 1 : (parseInt(acct.attrs[ZaAccount.A_zimbraAuthTokenValidityValue])+1)); 
	acct.modify(mods,acct);
}  

ZaAccountListController._viewMailListenerLauncher = 
function(account) {
	try {
		var obj;
		var accId;
		if(account.type == ZaItem.ACCOUNT || account.type == ZaItem.RESOURCE) {
			obj = ZaAccount.getViewMailLink(account.id);
			accId = account.id;
		} else if(account.type == ZaItem.ALIAS && account.attrs[ZaAlias.A_AliasTargetId]) {
			obj = ZaAccount.getViewMailLink(account.attrs[ZaAlias.A_AliasTargetId]);
			accId = account.attrs[ZaAlias.A_AliasTargetId];
			account = new ZaAccount();
		} else {
			return;
		}
		if(!account[ZaAccount.A2_publicMailURL]) {
			account.load("id", accId);
		}
		if(!obj.authToken || !obj.lifetime)
			throw new AjxException(ZaMsg.ERROR_FAILED_TO_GET_CREDENTIALS, AjxException.UNKNOWN, "ZaAccountListController.prototype._viewMailListener");

		var mServer = [account[ZaAccount.A2_publicMailURL], "/service/preauth?authtoken=",obj.authToken,"&isredirect=1"].join("");
		mServer = AjxStringUtil.trim(mServer,true);
		var win = window.open(mServer, "_blank");
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController._viewMailListenerLauncher", null, false);			
	}	
}

ZaAccountListController.prototype._viewMailListener =
function(ev) {
	try {
//		var el = this._contentView.getSelectedItems().getLast();
	//	if(el) {
			//var account = DwtListView.prototype.getItemFromElement.call(this, el); {
		var accounts = this._contentView.getSelection();
		if(accounts && accounts.length) {
			var account = accounts[0];
			if(account) {
				ZaAccountListController._viewMailListenerLauncher.call(this, account);
			}
		}	
			
		//}
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._viewMailListener", null, false);			
	}
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaAccountListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	this._itemsInTabList = [] ;
	this._haveAliases = false;
	this._haveAccounts = false;
	this._haveDls = false;
	this._haveDomains = false;	
	if(this._contentView.getSelectionCount()>0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			var item = arrItems[key];
			if (item) {
				//detect whether the deleting item is open in a tab
				if (ZaApp.getInstance().getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);			
					if(!this._haveAliases && item.type == ZaItem.ALIAS) {
						this._haveAliases = true;
					} else if(!this._haveAccounts && item.type == ZaItem.ACCOUNT) {
						this._haveAccounts = true;
					} else if(!this._haveDls && item.type == ZaItem.DL) {
						this._haveDls = true;
					}  else if(!this._haveDomains && item.type == ZaItem.DOMAIN) {
						this._haveDomains = true;
					}
				}
			}
		}
	}
	
	if (this._itemsInTabList.length > 0) {
		if(!ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
			ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], 
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC]);			
		}
		
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; 
		msg += ZaAccountListController.getDlMsgFromList (this._itemsInTabList) ;
		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaAccountListController.prototype._closeTabsBeforeRemove, this);
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaAccountListController.prototype._deleteAccountsInRemoveList, this);		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	} else{
		this._deleteAccountsInRemoveList ();
	}
	
}

ZaAccountListController.prototype._closeTabsBeforeRemove =
function () {
	//DBG.println (AjxDebug.DBG1, "Close the tabs before Remove ...");
	/*var tabGroup = ZaApp.getInstance().getTabGroup();
	for (var i=0; i< this._itemsInTabList.length ; i ++) {
		var item = this._itemsInTabList[i];
		tabGroup.removeTab (tabGroup.getTabByItemId(item.id)) ;
		this._removeList.push(item);
	}*/
	this.closeTabsInRemoveList();
	//ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	this._deleteAccountsInRemoveList();
}

ZaAccountListController.prototype._deleteAccountsInRemoveList =
function () {
	if (ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	}
	if(this._removeList.length > 0) {
		var dlgMsg;
		if(this._haveDls && !(this._haveAccounts || this._haveAliases ||this._haveDomains)) {
			dlgMsg = ZaMsg.Q_DELETE_DLS;
		} else if(this._haveAccounts && !(this._haveDls || this._haveAliases || this._haveDomains)) {
			dlgMsg = ZaMsg.Q_DELETE_ACCOUNTS;
		} else if(this._haveAliases && !(this._haveDls || this._haveAccounts || this._haveDomains)) {
			dlgMsg = ZaMsg.Q_DELETE_ALIASES;
		} else if(this._haveDomains && !(this._haveAliases || this._haveAccounts || this._haveDomains)) {
			dlgMsg = ZaMsg.Q_DELETE_DOMAINS;
		} else {
			dlgMsg = ZaMsg.Q_DELETE_OBJECTS;
		}
		dlgMsg += ZaAccountListController.getDlMsgFromList (this._removeList);
		var cnt = this._removeList.length;
		var sysResources = [];
		for(var i=0; i< cnt; i++) {
			if(this._removeList[i].attrs[ZaAccount.A_zimbraIsSystemResource] && this._removeList[i].attrs[ZaAccount.A_zimbraIsSystemResource]=="TRUE") {
				dlgMsg += "<br/>";
				dlgMsg += ZaMsg.WARN_DELETING_SYSTEM_RESOURCES;
				break;
			}
		}
		ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].setMessage(dlgMsg,  DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaAccountListController.prototype._deleteAccountsCallback, this);
		ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, ZaAccountListController.prototype._donotDeleteAccountsCallback, this);		
		ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].popup();
	}
}

ZaAccountListController.getDlMsgFromList =
function (listArr) {
		var dlgMsg =  "<br><ul>";
		var i=0;
		for(var key in listArr) {
			if(i > 19) {
				dlgMsg += "<li>...</li>";
				break;
			}
			dlgMsg += "<li>";
			var szAccName = listArr[key].attrs[ZaAccount.A_displayname] ? listArr[key].attrs[ZaAccount.A_displayname] : listArr[key].name;
            if(szAccName.length > 50) {
                var beginIx = 0;
                var endIx = 50;
				do {
                    if (endIx >= szAccName.length) {
                        dlgMsg +=  szAccName.slice(beginIx);     
                    } else {
                        dlgMsg +=  szAccName.slice(beginIx, endIx);
                    }
					beginIx = endIx;
					endIx += 50 ;
                    
					dlgMsg +=  "<br />";	
				} while (beginIx < szAccName.length) ;
			} else {
				dlgMsg += szAccName;
			}
			dlgMsg += "</li>";
			i++;
		}
		dlgMsg += "</ul>";	
		return dlgMsg ;
}


ZaAccountListController.prototype._deleteAccountsCallback = 
function () {

	if(!ZaApp.getInstance().dialogs["removeProgressDlg"]) {
		ZaApp.getInstance().dialogs["removeProgressDlg"] = new DeleteAcctsPgrsDlg(this._container, "500px","300px");
	}
	ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].popdown();
	ZaApp.getInstance().dialogs["removeProgressDlg"].popup();
	ZaApp.getInstance().dialogs["removeProgressDlg"].setObject(this._removeList);
	ZaApp.getInstance().dialogs["removeProgressDlg"].startDeletingAccounts();

}



ZaAccountListController.prototype._donotDeleteAccountsCallback = 
function () {
	this._removeList = new Array();
	ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].popdown();
}


ZaAccountListController._changePwdOKCallback = 
function (item) {
	//check the passwords, if they are ok then save the password, else show error
	if(this._chngPwdDlg) {
		try {
			if(!this._chngPwdDlg.getPassword() || this._chngPwdDlg.getPassword().length < 1) {
				ZaApp.getInstance().dialogs["errorMsgDlg"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON]);							
				ZaApp.getInstance().dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_REQUIRED, null, DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE]);
				ZaApp.getInstance().dialogs["errorMsgDlg"].popup();				
			} else if(this._chngPwdDlg.getPassword() != this._chngPwdDlg.getConfirmPassword()) {
				ZaApp.getInstance().dialogs["errorMsgDlg"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON]);							
				ZaApp.getInstance().dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_MISMATCH, null,DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE]);
				ZaApp.getInstance().dialogs["errorMsgDlg"].popup();				
			} else {
				//check password
				var myCos = null;
				var maxPwdLen = null;
				var minPwdLen = null;	
				item.refresh(true,false);
				if(item.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
					minPwdLen = item.attrs[ZaAccount.A_zimbraMinPwdLength];
				} 
				
				if(item.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
					maxPwdLen = item.attrs[ZaAccount.A_zimbraMaxPwdLength];
				} 
				
				if (minPwdLen == null) {
					minPwdLen = item._defaultValues[ZaAccount.A_zimbraMinPwdLength];
				}			
				
				if (maxPwdLen == null) {
					minPwdLen = item._defaultValues[ZaAccount.A_zimbraMaxPwdLength];
				}		
				
				if (maxPwdLen == null) {
					maxPwdLen = Number.POSITIVE_INFINITY;
				}
				
				if(minPwdLen == null) {
					minPwdLen = 1;
				}
				
				var szPwd = this._chngPwdDlg.getPassword();
				if(szPwd.length < minPwdLen || AjxStringUtil.trim(szPwd).length < minPwdLen) { 
					//show error msg
					//this._chngPwdDlg.popdown();
					ZaApp.getInstance().dialogs["errorMsgDlg"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON]);												
					ZaApp.getInstance().dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					ZaApp.getInstance().dialogs["errorMsgDlg"].popup();
				} else if(AjxStringUtil.trim(szPwd).length > maxPwdLen) { 
					//show error msg
					//this._chngPwdDlg.popdown();
					ZaApp.getInstance().dialogs["errorMsgDlg"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON]);																	
					ZaApp.getInstance().dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					ZaApp.getInstance().dialogs["errorMsgDlg"].popup();
				} else {		
					item.changePassword(szPwd);
					this._chngPwdDlg.popdown();	//close the dialog
				}
			}
			if (this._chngPwdDlg.getMustChangePassword()) {
				//item.attrs[ZaAccount.A_zimbraPasswordMustChange] = "TRUE";
				var mods = new Object();
				mods[ZaAccount.A_zimbraPasswordMustChange] = "TRUE";
				item.modify(mods);
			}

		} catch (ex) {
			if(ex.code == ZmCsfeException.ACCT_INVALID_PASSWORD ) {
				var szMsg = ZaMsg.ERROR_PASSWORD_INVALID;
				if(ex.detail) {
					szMsg +="<br>Details:<br>";
					szMsg += ex.detail;
				}
				ZaApp.getInstance().dialogs["errorDialog"].setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
				ZaApp.getInstance().dialogs["errorDialog"].popup();
			} else {
				this._handleException(ex, "ZaAccountListController._changePwdOKCallback", null, false);			
			}
			return;
		}
	}
}

ZaAccountListController.changeActionsStateMethod = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
            if (item.type == ZaItem.ALIAS || item.type == ZaItem.DL) {
                if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
                    this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
                }

                if(this._popupOperations[ZaOperation.CHNG_PWD]) {
                    this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
                }
                
            	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}	
            }

            if (((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.DL))
                || (item.type == ZaItem.DL)) {
                if (this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
                    this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }

                if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
                }
            }

            if (item.type == ZaItem.DL) {
                if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
                    this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
                }
                if(this._popupOperations[ZaOperation.VIEW_MAIL])
				 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
					 
				if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
				 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
                	    
            }
			if (item.type == ZaItem.ACCOUNT) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					//console.log("loading effective rights for a list item");
					item.loadEffectiveRights("id", item.id, false);
					//console.log("loaded rights for a list item");
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT, item)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}	
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}						
					}									
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.ACCOUNT))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT,item.targetObj)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}		
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item.targetObj)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}						
					}
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.RESOURCE))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!enable) {
						if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item.targetObj)) {
							 if(this._popupOperations[ZaOperation.VIEW_MAIL])
							 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
							 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
						}
						if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item.targetObj)) {
							 if(this._popupOperations[ZaOperation.DELETE])
							 	this._popupOperations[ZaOperation.DELETE].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.DELETE])
							 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
						}	
						if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item.targetObj)) {
							 if(this._popupOperations[ZaOperation.CHNG_PWD])
							 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
							 
							 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
							 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
						}		
					}
				}
			} else if(item.type == ZaItem.RESOURCE) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
						 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item)) {
						 if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
					}	
					if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item)) {
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
						 
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
					}		
				}				
			}
        } else {
			if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}        	
			if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
				this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.EDIT]) {	
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
				this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
				this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.DELETE]) {	
				this._toolbarOperations[ZaOperation.DELETE].enabled = false;
			}
			
			if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}    			
			if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
				this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._popupOperations[ZaOperation.EDIT]) {	
				this._popupOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.CHNG_PWD]) {
				this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
				this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.DELETE]) {	
				this._popupOperations[ZaOperation.DELETE].enabled = false;
			}				
		}		
	} else if (cnt > 1){
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;		
		}
		
		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}					
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
			this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;		
		}
	} else {
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.DELETE]) {
			this._toolbarOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS])	{
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}	
				
		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}				
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.DELETE]) {
			this._popupOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
			this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}	
	}
}
ZaController.changeActionsStateMethods["ZaAccountListController"].push(ZaAccountListController.changeActionsStateMethod);

ZaAccountListController.prototype._moveAliasListener = 
function (ev) {
	try {
		var alias;
		var alias = this._contentView.getSelection()[0];
		//make sure this is an alias
		if(!alias || alias.type!=ZaItem.ALIAS) {
			return;			
		}
		if(!ZaApp.getInstance().dialogs["moveAliasDialog"]) {
			ZaApp.getInstance().dialogs["moveAliasDialog"] = new MoveAliasXDialog(this._container, "400px", "300px");
		}
		ZaApp.getInstance().dialogs["moveAliasDialog"].setAlias(alias);
		ZaApp.getInstance().dialogs["moveAliasDialog"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._moveAliasListener", null, false);
	}
	return;
}
