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
ZaAccountListController = function(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app, "ZaAccountListController");
    //Account operations
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
   	
	this._currentPageNum = 1;
//	this._currentQuery = new ZaSearchQuery("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES], false, "");
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
ZaAccountListController.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/provisioning_accounts.htm";
ZaController.initToolbarMethods["ZaAccountListController"] = new Array();
ZaController.initPopupMenuMethods["ZaAccountListController"] = new Array();
ZaListViewController.changeActionsStateMethods["ZaAccountListController"] = new Array(); 


ZaAccountListController.prototype.show = function (doPush) {
	var callback = new AjxCallback(this, this.searchCallback, {limit:this.RESULTSPERPAGE,CONS:null,show:doPush});
	
	var searchParams = {
			query:this._currentQuery ,
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

ZaAccountListController.prototype._show = 
function (list, openInNewTab, openInSearchTab) {
	this._updateUI(list, openInNewTab, openInSearchTab);
//	this._app.pushView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
	this._app.pushView(this.getContentViewId (), openInNewTab, openInSearchTab);
	this.updateToolbar();
	//TODO: need to standardize the way to handle the tab.
	//hacking: currently, dllistview, aliasListView, accountListView and resourceListView share the same controller instance. It is BAD!
	//It should be changed when we allow the list view to be open in a new tab
	if (openInSearchTab) {
		this._app.updateSearchTab();
	}else{
		this._app.updateTab(this.getMainTab(), this._app._currentViewId );
	}
	
	/*
	if (openInNewTab) {
		
	}else{
		var icon ;
		switch (this._defaultType) {
			case ZaItem.DL :
				icon = "Group"; break ;
			case ZaItem.ALIAS :
				icon = "AccountAlias" ; break ;
			case ZaItem.RESOURCE : 
				icon = "Resource" ; break ;	
			default :
				icon = "Account" ;
		}
		this.updateMainTab (icon);
	}*/
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
			newButton.setImage("Group");
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
    this._popupOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener)));
	this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener)));
	if(ZaSettings.ACCOUNTS_CHPWD_ENABLED)
		this._popupOperations.push(new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener)));

	if(ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED)
		this._popupOperations.push(new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener)));		

	if(ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED)	
		this._popupOperations.push(new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener)));		    	
}
ZaController.initPopupMenuMethods["ZaAccountListController"].push(ZaAccountListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaAccountListController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
	var newMenuOpList = new Array();
	newMenuOpList.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.ACTBB_New_menuItem, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener));
	
	newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.ALTBB_New_menuItem, ZaMsg.ALTBB_New_tt, "AccountAlias", "AccountAliasDis", this._newALListener));
		
	if(ZaSettings.DISTRIBUTION_LISTS_ENABLED) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.DLTBB_New_menuItem, ZaMsg.DLTBB_New_tt, "Group", "GroupDis", this._newDLListener));
	}
	
	if(ZaSettings.RESOURCES_ENABLED) {
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.RESTBB_New_menuItem, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", this._newResListener));
	}
		
	if(this._defaultType == ZaItem.ACCOUNT || this._defaultType == ZaItem.ALIAS) {
		this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener, 
								   ZaOperation.TYPE_MENU, newMenuOpList));
    } else if (this._defaultType == ZaItem.ALIAS) {
		this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ALTBB_New_tt, "AccountAlias", "AccountAliasDis", this._newALListener, 
								   ZaOperation.TYPE_MENU, newMenuOpList));
    }else if(this._defaultType == ZaItem.RESOURCE) {
		this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", this._newResListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList));
    	
    } else if(this._defaultType == ZaItem.DL) {    	
		this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Group", "GroupDis", this._newDLListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList));
    	
    } 
    this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener)));
	if(ZaSettings.ACCOUNTS_CHPWD_ENABLED && this._defaultType == ZaItem.ACCOUNT)
		this._toolbarOperations.push(new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener)));

	if(ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED)
		this._toolbarOperations.push(new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, ZaAccountListController.prototype._viewMailListener)));		

	if(ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED && this._defaultType == ZaItem.ALIAS) {	
		this._toolbarOperations.push(new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "MoveAlias", "MoveAlias", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener)));		    	
	}
}
ZaController.initToolbarMethods["ZaAccountListController"].push(ZaAccountListController.initToolbarMethod);

//private and protected methods
ZaAccountListController.prototype._createUI = 
function (openInNewTab, openInSearchTab) {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = new ZaAccountListView(this._container, this._app, this._defaultType);
	this._app._controllers[this.getContentViewId ()] = this ;
	
	this._newDLListener = new AjxListener(this, ZaAccountListController.prototype._newDistributionListListener);
	this._newAcctListener = new AjxListener(this, ZaAccountListController.prototype._newAccountListener);
	this._newResListener = new AjxListener(this, ZaAccountListController.prototype._newResourceListener);
	this._newALListener = new AjxListener(this, ZaAccountListController.prototype._newAliasListener);
   
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
	//this._app.createView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW, elements);
	var tabParams = {
		openInNewTab: false,
		tabId: this.getContentViewId(),
		tab: openInSearchTab ? this.getSearchTab() : this.getMainTab() 
	}
	this._app.createView(this.getContentViewId(), elements, tabParams);
	
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	if(!this._app.dialogs["ConfirmMessageDialog"])
		this._app.dialogs["ConfirmMessageDialog"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
	
	this._UICreated = true;
	
}

ZaAccountListController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	if (noPopView) {
		func.call(obj, params) ;
	}else{
		this._app.popView () ;
	}
}

// new account button was pressed
ZaAccountListController.prototype._newAccountListener =
function(ev) {

	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAccount = new ZaAccount(this._app);
		if(!this._app.dialogs["newAccountWizard"])
			this._app.dialogs["newAccountWizard"] = new ZaNewAccountXWizard(this._container, this._app);	

		this._app.dialogs["newAccountWizard"].setObject(newAccount);
		this._app.dialogs["newAccountWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAccountListener", null, false);
	}
}

// new alias button was pressed
ZaAccountListController.prototype._newAliasListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAlias = new ZaAlias(this._app);
		if(!this._app.dialogs["newAliasDialog"]) {
			this._app.dialogs["newAliasDialog"] = new ZaNewAliasXDialog(
				this._container, this._app,"550px", "100px",ZaMsg.New_Alias_Title );	
			this._app.dialogs["newAliasDialog"].registerCallback(
					DwtDialog.OK_BUTTON, ZaAlias.prototype.addAlias, 
					newAlias, this._app.dialogs["newAliasDialog"]._localXForm );								
		}

		this._app.dialogs["newAliasDialog"].setObject(newAlias);
		this._app.dialogs["newAliasDialog"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAliasListener", null, false);
	}
}


ZaAccountListController.prototype._newDistributionListListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (this);
		var newDL = new ZaDistributionList(this._app);
		this._app.getDistributionListController().show(newDL);
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newDistributionListListener", null, false);
	}

};

ZaAccountListController.prototype._newResourceListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (this);
		var newResource = new ZaResource(this._app);
		if(!this._app.dialogs["newResourceWizard"])
			this._app.dialogs["newResourceWizard"] = new ZaNewResourceXWizard(this._container, this._app);	

		this._app.dialogs["newResourceWizard"].setObject(newResource);
		this._app.dialogs["newResourceWizard"].popup();
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
		if(ev.item) {
			this._editItem(ev.item);
		}
	} else {
		this.changeActionsState();
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
	//check if the item already open in a tab
	var itemId = item.id ;
	if((item.type == ZaItem.ALIAS) && item.attrs && item.attrs[ZaAlias.A_AliasTargetId]) {
		itemId = item.attrs[ZaAlias.A_AliasTargetId];
	}
	var type = item.type;
	var viewContstructor = ZaAccountXFormView;
	if (type == ZaItem.ACCOUNT) {
		viewContstructor = ZaAccountXFormView ;	
	} else if (type == ZaItem.DL) {
		viewContstructor = ZaDLXFormView ;	
	} else if (type == ZaItem.RESOURCE ){
		viewContstructor = ZaResourceXFormView;
	} else if (type == ZaItem.ALIAS) {
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {	
			viewController = ZaAccountXFormView ;
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
		    viewController = ZaDLXFormView ;
		}
	}
		
	if (! this.selectExistingTabByItemId(itemId,viewContstructor)){
//		DBG.println("TYPE == ", item.type);
		if (type == ZaItem.ACCOUNT) {
			//this._selectedItem = ev.item;
			this._app.getAccountViewController().show(item, true);
		} else if (type == ZaItem.DL) {
			this._app.getDistributionListController().show(item, true);
		} else if(type == ZaItem.ALIAS) {
			var targetObj = item.getAliasTargetObj() ;
			
			if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {			
				this._app.getAccountViewController().show(targetObj, true);
			}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
				this._app.getDistributionListController().show(targetObj, true);
			}
		} else if (type == ZaItem.RESOURCE ){
			this._app.getResourceController(itemId).show(item, true);
		}
	}
};
/**
* This listener is called when the Change Password button is clicked. 
**/
ZaAccountListController.prototype._chngPwdListener =
function(ev) {
	if(this._contentView.getSelectionCount()==1) {
		this._chngPwdDlg = new ZaAccChangePwdXDlg(this._app.getAppCtxt().getShell(), this._app,"400px","90px");
		var item = this._contentView.getSelection()[0];
		this._chngPwdDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountListController._changePwdOKCallback, this, item);				
		this._chngPwdDlg.setTitle(ZaMsg.CHNP_Title + " (" + item.name + ")");
		var obj = new Object();
		obj[ZaAccount.A2_confirmPassword]="";
		obj.attrs = {};
		obj.attrs[ZaAccount.A_password]="";
		obj.attrs[ZaAccount.A_zimbraPasswordMustChange]=false;
		this._chngPwdDlg.setObject(obj)
		this._chngPwdDlg.popup();
	}
}

ZaAccountListController._viewMailListenerLauncher = 
function(account) {
	try {
		var obj;
		if(account.type == ZaItem.ACCOUNT || account.type == ZaItem.RESOURCE) {
			obj = ZaAccount.getViewMailLink(account.id,this._app);
		} else if(account.type == ZaItem.ALIAS && account.attrs[ZaAlias.A_AliasTargetId]) {
			obj = ZaAccount.getViewMailLink(account.attrs[ZaAlias.A_AliasTargetId]);
		} else {
			return;
		}
		var ms = account.attrs[ZaAccount.A_mailHost] ? account.attrs[ZaAccount.A_mailHost].toLowerCase() : location.hostname.toLowerCase();
		//find my server
		var servers = this._app.getServerList().getArray();
		var cnt = servers.length;
		var mailPort = 80;
		var mailProtocol = "http";
		
		for (var i = 0; i < cnt; i++) {
			if(servers[i].attrs[ZaServer.A_ServiceHostname].toLowerCase() == ms) {
				if(servers[i].attrs[ZaServer.A_zimbraMailMode] && (servers[i].attrs[ZaServer.A_zimbraMailMode] == "https" || servers[i].attrs[ZaServer.A_zimbraMailMode] == "mixed")) { //if there is SSL, use SSL
					mailPort = servers[i].attrs[ZaServer.A_zimbraMailSSLPort];
					mailProtocol = "https";
				} else if (servers[i].attrs[ZaServer.A_zimbraMailPort] && parseInt(servers[i].attrs[ZaServer.A_zimbraMailPort]) > 0) { //otherwize use HTTP
					mailPort = servers[i].attrs[ZaServer.A_zimbraMailPort];
					mailProtocol = "http";
				}
				break;
			}
		}

		if(!obj.authToken || !obj.lifetime)
			throw new AjxException(ZaMsg.ERROR_FAILED_TO_GET_CREDENTIALS, AjxException.UNKNOWN, "ZaAccountListController.prototype._viewMailListener");

		var mServer = [mailProtocol, "://", ms, ":", mailPort, "/service/preauth?authtoken=",obj.authToken,"&isredirect=1"].join("");
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
				if (this._app.getTabGroup().getTabByItemId (item.id)) {
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
		if(!this._app.dialogs["ConfirmDeleteItemsInTabDialog"]) {
			this._app.dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], this._app,
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC]);			
		}
		
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; ;
		msg += ZaAccountListController.getDlMsgFromList (this._itemsInTabList) ;
		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaAccountListController.prototype._closeTabsBeforeRemove, this);
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaAccountListController.prototype._deleteAccountsInRemoveList, this);		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	}else{
		this._deleteAccountsInRemoveList ();
	}
	
}

ZaAccountListController.prototype._closeTabsBeforeRemove =
function () {
	//DBG.println (AjxDebug.DBG1, "Close the tabs before Remove ...");
	/*var tabGroup = this._app.getTabGroup();
	for (var i=0; i< this._itemsInTabList.length ; i ++) {
		var item = this._itemsInTabList[i];
		tabGroup.removeTab (tabGroup.getTabByItemId(item.id)) ;
		this._removeList.push(item);
	}*/
	this.closeTabsInRemoveList();
	//this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	this._deleteAccountsInRemoveList();
}

ZaAccountListController.prototype._deleteAccountsInRemoveList =
function () {
	if (this._app.dialogs["ConfirmDeleteItemsInTabDialog"]) {
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
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
		
		this._app.dialogs["ConfirmMessageDialog"].setMessage(dlgMsg,  DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["ConfirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaAccountListController.prototype._deleteAccountsCallback, this);
		this._app.dialogs["ConfirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, ZaAccountListController.prototype._donotDeleteAccountsCallback, this);		
		this._app.dialogs["ConfirmMessageDialog"].popup();
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
				//split it
				var endIx = 49;
				var beginIx = 0; //
				while(endIx < szAccName.length) { //
					dlgMsg +=  szAccName.slice(beginIx, endIx); //
					beginIx = endIx + 1; //
					if(beginIx >= (szAccName.length) ) //
						break;
					
					endIx = ( szAccName.length <= (endIx + 50) ) ? szAccName.length-1 : (endIx + 50);
					dlgMsg +=  "<br>";	
				}
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

	if(!this._app.dialogs["removeProgressDlg"]) {
		this._app.dialogs["removeProgressDlg"] = new DeleteAcctsPgrsDlg(this._container, this._app,"500px","300px");
	}
	this._app.dialogs["ConfirmMessageDialog"].popdown();
	this._app.dialogs["removeProgressDlg"].popup();
	this._app.dialogs["removeProgressDlg"].setObject(this._removeList);
	this._app.dialogs["removeProgressDlg"].startDeletingAccounts();

}



ZaAccountListController.prototype._donotDeleteAccountsCallback = 
function () {
	this._removeList = new Array();
	this._app.dialogs["ConfirmMessageDialog"].popdown();
}


ZaAccountListController._changePwdOKCallback = 
function (item) {
	//check the passwords, if they are ok then save the password, else show error
	if(this._chngPwdDlg) {
		try {
			if(!this._chngPwdDlg.getPassword() || this._chngPwdDlg.getPassword().length < 1) {
				this._app.dialogs["errorMsgDlg"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);							
				this._app.dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_REQUIRED, null, DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE]);
				this._app.dialogs["errorMsgDlg"].popup();				
			} else if(this._chngPwdDlg.getPassword() != this._chngPwdDlg.getConfirmPassword()) {
				this._app.dialogs["errorMsgDlg"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);							
				this._app.dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_MISMATCH, null,DwtMessageDialog.TITLE[DwtMessageDialog.CRITICAL_STYLE]);
				this._app.dialogs["errorMsgDlg"].popup();				
			} else {
				//check password
				var myCos = null;
				var maxPwdLen = null;
				var minPwdLen = null;	
				item.refresh(!ZaSettings.COSES_ENABLED);
				if(item.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
					minPwdLen = item.attrs[ZaAccount.A_zimbraMinPwdLength];
				} 
				
				if(item.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
					maxPwdLen = item.attrs[ZaAccount.A_zimbraMaxPwdLength];
				} 
				
				if(!item.attrs[ZaAccount.A_COSId] && ZaSettings.COSES_ENABLED) {
					var cosList = this._app.getCosList().getArray();
					item.attrs[ZaAccount.A_COSId] = cosList[0].id;
				}
				
				if (minPwdLen == null) {
					if(item.attrs[ZaAccount.A_COSId] && ZaSettings.COSES_ENABLED) {
						myCos = new ZaCos(this._app);
						myCos.load("id", item.attrs[ZaAccount.A_COSId]);
						if(myCos.attrs[ZaCos.A_zimbraMinPwdLength] > 0) {
							minPwdLen = myCos.attrs[ZaCos.A_zimbraMinPwdLength];
						}
					}
				}			
				
				if (maxPwdLen == null) {
					if(item.attrs[ZaAccount.A_COSId] && ZaSettings.COSES_ENABLED) {
						if(!myCos) { 
							myCos = new ZaCos(this._app);
							myCos.load("id", item.attrs[ZaAccount.A_COSId]);
						}
						if(myCos.attrs[ZaCos.A_zimbraMaxPwdLength] > 0) {
							maxPwdLen = myCos.attrs[ZaCos.A_zimbraMaxPwdLength];
						}		
					}
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
					this._app.dialogs["errorMsgDlg"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);												
					this._app.dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._app.dialogs["errorMsgDlg"].popup();
				} else if(AjxStringUtil.trim(szPwd).length > maxPwdLen) { 
					//show error msg
					//this._chngPwdDlg.popdown();
					this._app.dialogs["errorMsgDlg"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);																	
					this._app.dialogs["errorMsgDlg"].setMessage(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._app.dialogs["errorMsgDlg"].popup();
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
				this._app.dialogs["errorDialog"].setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
				this._app.dialogs["errorDialog"].popup();
			} else {
				this._handleException(ex, "ZaAccountListController._changePwdOKCallback", null, false);			
			}
			return;
		}
	}
}

ZaAccountListController.changeActionsStateMethod = 
function (opsArray1,opsArray2) {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
			opsArray1.push(ZaOperation.EDIT);
			opsArray1.push(ZaOperation.DELETE);
			if(item.type == ZaItem.ALIAS) {
				opsArray1.push(ZaOperation.MOVE_ALIAS);
				opsArray1.push(ZaOperation.VIEW_MAIL);
			} else if(item.type == ZaItem.ACCOUNT) {
				opsArray1.push(ZaOperation.VIEW_MAIL);
				opsArray1.push(ZaOperation.CHNG_PWD);
			} else if(item.type == ZaItem.RESOURCE) {
				opsArray1.push(ZaOperation.CHNG_PWD);
				opsArray1.push(ZaOperation.VIEW_MAIL);
			}
		} else {
			opsArray2.push(ZaOperation.EDIT);
			opsArray2.push(ZaOperation.CHNG_PWD);		
			opsArray2.push(ZaOperation.VIEW_MAIL);				
			opsArray2.push(ZaOperation.MOVE_ALIAS);						
			opsArray2.push(ZaOperation.DELETE);
		}		
	} else if (cnt > 1){
		opsArray2.push(ZaOperation.EDIT);
		opsArray2.push(ZaOperation.CHNG_PWD);		
		opsArray2.push(ZaOperation.VIEW_MAIL);				
		opsArray2.push(ZaOperation.MOVE_ALIAS);						
		opsArray1.push(ZaOperation.DELETE);
	} else {
		opsArray2.push(ZaOperation.EDIT);
		opsArray2.push(ZaOperation.DELETE);		
		opsArray2.push(ZaOperation.CHNG_PWD);
		opsArray2.push(ZaOperation.VIEW_MAIL);
		opsArray2.push(ZaOperation.MOVE_ALIAS);				
	}
}
ZaListViewController.changeActionsStateMethods["ZaAccountListController"].push(ZaAccountListController.changeActionsStateMethod);

ZaAccountListController.prototype._moveAliasListener = 
function (ev) {
	try {
		var alias;
		var alias = this._contentView.getSelection()[0];
		//make sure this is an alias
		if(!alias || alias.type!=ZaItem.ALIAS) {
			return;			
		}
		if(!this._app.dialogs["moveAliasDialog"]) {
			this._app.dialogs["moveAliasDialog"] = new MoveAliasXDialog(this._container, this._app, "400px", "300px");
		}
		this._app.dialogs["moveAliasDialog"].setAlias(alias);
		this._app.dialogs["moveAliasDialog"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._moveAliasListener", null, false);
	}
	return;
}
