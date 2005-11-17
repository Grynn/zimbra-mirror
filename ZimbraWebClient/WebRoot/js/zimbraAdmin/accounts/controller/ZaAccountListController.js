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
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaAccountListController
* @param appCtxt
* @param container
* @param app
* This is a singleton class that controls all the user interaction with the list of ZaAccount objects
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaAccountListController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app);
	this._evtMgr = new AjxEventMgr();			
	this._currentPageNum = 1;
	this._currentQuery = new ZaSearchQuery("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], false, "");
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = true;
	this.pages = new Object();
	this._searchPanel = null;
	this._searchField = null;
	this._defaultType = ZaItem.ACCOUNT;
	this._helpURL = "/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/managing_accounts/provisioning_accounts.htm";	
}

ZaAccountListController.prototype = new ZaController();
ZaAccountListController.prototype.constructor = ZaAccountListController;

//ZaAccountListController.ACCOUNT_VIEW = "ZaAccountListController.ACCOUNT_VIEW";

ZaAccountListController.prototype.show = 
function(searchResult) {
    if (!this._contentView) {
		//create accounts list view
		this._contentView = new ZaAccountListView(this._container, this._app);
    	//toolbar
    	//Account operations
    	this._ops = new Array();
		// first button in the toolbar is a menu.
		// create the menu operations/listeners first
		this._newDLListener = new AjxListener(this, ZaAccountListController.prototype._newDistributionListListener);
		this._newAcctListener = new AjxListener(this, ZaAccountListController.prototype._newAccountListener);
		var newMenuOpList = new Array();
		newMenuOpList.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.ACTBB_New_menuItem, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener));

		if(ZaSettings.DISTRIBUTION_LISTS_ENABLED) {
			newMenuOpList.push(new ZaOperation(ZaOperation.NEW, ZaMsg.DLTBB_New_menuItem, ZaMsg.DLTBB_New_tt, "Group", "GroupDis", this._newDLListener));
		}
		
		if(this._defaultType == ZaItem.ACCOUNT || this._defaultType == ZaItem.ALIAS) {
			this._ops.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", this._newAcctListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList));
    	} else if(this._defaultType == ZaItem.DL) {
			this._ops.push(new ZaOperation(ZaOperation.NEW_MENU, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Group", "GroupDis", this._newDLListener, 
									   ZaOperation.TYPE_MENU, newMenuOpList));
    	
    	}
    	this._ops.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener)));
    	this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener)));
		if(ZaSettings.ACCOUNTS_CHPWD_ENABLED)
			this._ops.push(new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, ZaAccountListController.prototype._chngPwdListener)));
	
		if(ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED)
			this._ops.push(new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailboxDis", new AjxListener(this, ZaAccountListController.prototype._viewMailListener)));		
	
		if(ZaSettings.ACCOUNTS_MOVE_ALIAS_ENABLED)	
			this._ops.push(new ZaOperation(ZaOperation.MOVE_ALIAS, ZaMsg.ACTBB_MoveAlias, ZaMsg.ACTBB_MoveAlias_tt, "ReadMailbox", "ReadMailboxDis", new AjxListener(this, ZaAccountListController.prototype._moveAliasListener)));		    	
    	
    	this._acctionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._ops);
    
		if(ZaSettings.ACCOUNTS_RESTORE_ENABLED) {
			var globalConf = this._app.getGlobalConfig();
	
	    	if(globalConf && globalConf.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_hotbackup])
				this._ops.push(new ZaOperation(ZaOperation.MAIL_RESTORE, ZaMsg.TBB_RestoreMailbox, ZaMsg.ACTBB_Restore_tt, "RestoreMailbox", "RestoreMailboxDis", new AjxListener(this, ZaAccountListController.prototype._restoreMailListener)));		
		}
		this._ops.push(new ZaOperation(ZaOperation.NONE));	
		this._ops.push(new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Back, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, ZaAccountListController.prototype._prevPageListener)));
		this._ops.push(new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Forward, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, ZaAccountListController.prototype._nextPageListener)));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				


/**
* Toolbar
*/
		this._toolbar = new ZaToolBar(this._container, this._ops);    
		
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
		this._app.createView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW, elements);

    	//context menu

		if (searchResult && searchResult.list != null) {
			var tmpArr = new Array();
			var cnt = searchResult.list.getArray().length;
			for(var ix = 0; ix < cnt; ix++) {
				tmpArr.push(searchResult.list.getArray()[ix]);
			}
			this._contentView.set(AjxVector.fromArray(tmpArr));	
		}
		this._app.pushView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);			
		
		//set a selection listener on the account list view
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
		//this.refresh();
	} else {
		if (searchResult && searchResult.list != null) {
			var tmpArr = new Array();
			var cnt = searchResult.list.getArray().length;
			for(var ix = 0; ix < cnt; ix++) {
				tmpArr.push(searchResult.list.getArray()[ix]);
			}
			if(cnt < 1) {
				//if the list is empty - go to the previous page
				
			}
			this._contentView.set(AjxVector.fromArray(tmpArr));	
		}
		this._app.pushView(ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
	}
//	this._app.setCurrentController(this);		
	this._removeList = new Array();
	if (searchResult && searchResult.list != null) {
		this.pages[this._currentPageNum] = searchResult;
	}
	this._changeActionsState();

	if(this.pages[this._currentPageNum].numPages <= this._currentPageNum) {
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_FORWARD], true);
	}
	if(this._currentPageNum == 1) {
		this._toolbar.enable([ZaOperation.PAGE_BACK], false);
	} else {
		this._toolbar.enable([ZaOperation.PAGE_BACK], true);
	}
	
	//this._schedule(ZaAccountListController.prototype.preloadNextPage);
}

ZaAccountListController.prototype.setDefaultType = function (type) {
	// set the default type,
	this._defaultType = type;
	if(!this._toolbar)
		return;
		
	var newButton = this._toolbar.getButton(ZaOperation.NEW_MENU);	
	if (newButton != null) {
		newButton.removeSelectionListeners();
		// set the new menu action
		if (type == ZaItem.ACCOUNT || type == ZaItem.ALIAS) {
			newButton.setToolTipContent(ZaMsg.ACTBB_New_tt);
			newButton.setImage("Account");
			newButton.setDisabledImage("AccountDis");
			newButton.addSelectionListener(this._newAcctListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.ACTBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.ACTBB_Delete_tt);
		} else if (type == ZaItem.DL) {
			newButton.setToolTipContent(ZaMsg.DLTBB_New_tt);
			newButton.setImage("Group");
			newButton.setDisabledImage("GroupDis");
			newButton.addSelectionListener(this._newDLListener);
			this._toolbar.getButton(ZaOperation.EDIT).setToolTipContent(ZaMsg.DLTBB_Edit_tt);
			this._toolbar.getButton(ZaOperation.DELETE).setToolTipContent(ZaMsg.DLTBB_Delete_tt);
		}
	}
};

/**
* searh panel
*/	
ZaAccountListController.prototype.getSearchPanel = 
function () {
	if(!this._searchPanel) {
	    this._searchPanel = new DwtComposite(this._app.getAppCtxt().getShell(), "SearchPanel", DwtControl.ABSOLUTE_STYLE);
	    
		// Create search toolbar and setup browse tool bar button handlers
		this._searchToolBar = new ZaSearchToolBar(this._searchPanel, null, this._app);
	    
		// Setup search field handler
		this._searchField = this._searchToolBar.getSearchField();
		this._searchField.registerCallback(ZaAccountListController.prototype._searchFieldCallback, this);	
		this._searchPanel.zShow(true);		
	}
	return this._searchPanel;
}

ZaAccountListController.prototype.preloadNextPage = 
function() {
	if((this._currentPageNum + 1) <= this.pages[this._currentPageNum].numPages && !this.pages[this._currentPageNum+1]) {
		this.pages[this._currentPageNum+1] = ZaSearch.searchByQueryHolder(this._currentQuery,this._currentPageNum+1, this._currentSortField, this._currentSortOrder, this._app)
	}		
	this._app.getAppCtxt().getShell().setBusy(false);
}
/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaAccountListController.prototype.getList = 
function() {
	return this.pages[this._currentPageNum];
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
	return this.pages[this._currentPageNum].numPages;
}

ZaAccountListController.prototype._setQuery = 
function (query) {
	this._currentQuery = query;
	searchObj = ZaSearch.getSearchFromQuery(query);
//	this._searchField.setObject(searchObj);
}

ZaAccountListController.prototype.getQuery = 
function () {
	return this._currentQuery;
}

ZaAccountListController.prototype.setSortOrder = 
function (sortOrder) {
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

ZaAccountListController.prototype.search =
function(searchQuery) {
	try {
		// if the search string starts with "$set:" then it is a command to the client 
		if (searchQuery.queryString.indexOf("$set:") == 0) {
			this._appCtxt.getClientCmdHdlr().execute((searchString.substr(5)).split(" "));
			return;
		}
		
		//this._searchField.setObject(searchString);
		this.pages = new Object();
		this._setQuery(searchQuery);
		this._currentPageNum = 1;
		this.show(ZaSearch.searchByQueryHolder(searchQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));	
	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaAccountListController.prototype.search", null, (this._inited) ? false : true);
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);	
		}
	}	
}

/**
* Adds listener to removal of an ZaAccount 
* @param listener
**/
ZaAccountListController.prototype.addAccountRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/*********** Search Field Callback */

ZaAccountListController.prototype._searchFieldCallback =
function(searchField, searchQuery) {
	this.search(searchQuery);
}

/**
*	Private method that notifies listeners to that the controlled ZaAccount is (are) removed
* 	@param details
*/
ZaAccountListController.prototype._fireAccountRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_ACCOUNT);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaAccountListController.prototype._fireAccountRemovalEvent, details, false);	
	}
}

/**
* @param ev
* This listener is invoked by ZaAccountViewController or any other controller that can change an ZaAccount object
**/
ZaAccountListController.prototype.handleAccountChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		this._contentView.setUI();
		if(this._app.getCurrentController() == this) {
			this.show();			
		}
	}
}

/**
* This listener is invoked by ZaAccountViewController or any other controller that can create an ZaAccount object
**/
ZaAccountListController.prototype.handleAccountCreation = 
function () {
	this.pages=new Object();
	if(this._app.getCurrentController() == this) {
		this.show(ZaSearch.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));			
	} else {
		var searchResult = ZaSearch.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
		if (searchResult && searchResult.list != null) {
			var tmpArr = new Array();
			var cnt = searchResult.list.getArray().length;
			for(var ix = 0; ix < cnt; ix++) {
				tmpArr.push(searchResult.list.getArray()[ix]);
			}
			this._contentView.set(AjxVector.fromArray(tmpArr));	
			this.pages[this._currentPageNum] = searchResult;
		}			
	}
}

/**
* @param ev
* This listener is invoked by ZaAccountViewController or any other controller that can remove an ZaAccount object
**/
ZaAccountListController.prototype.handleAccountRemoval = 
function (ev) {
	if(ev) {
		//add the new ZaAccount to the controlled list
		if(ev.getDetails()) {
			this.pages=new Object();
			var srchResult = ZaSearch.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
			while(this._currentPageNum > 1) { 
				if(srchResult.numPages < this._currentPageNum) {
					this._currentPageNum--;
					srchResult = ZaSearch.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
					if(srchResult.numPages >= this._currentPageNum)
						break;
				}
			}
			if(this._app.getCurrentController() == this) {
				this.show(srchResult);			
			} else {
				if (srchResult && srchResult.list != null) {
					var tmpArr = new Array();
					var cnt = srchResult.list.getArray().length;
					for(var ix = 0; ix < cnt; ix++) {
						tmpArr.push(srchResult.list.getArray()[ix]);
					}
					this._contentView.set(AjxVector.fromArray(tmpArr));	
					this.pages[this._currentPageNum] = srchResult;
				}					
			}
		}
	}
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaAccountListController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaAccountListController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

// new account button was pressed
ZaAccountListController.prototype._newAccountListener =
function(ev) {

	try {
		var newAccount = new ZaAccount(this._app);
		if(!this._app._newAccountWizard)
			this._app._newAccountWizard = new ZaNewAccountXWizard(this._container, this._app);	

		this._app._newAccountWizard.setObject(newAccount);
		this._app._newAccountWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAccountListener", null, false);
	}
}

ZaAccountListController.prototype._newDistributionListListener =
function(ev) {
	try {
		var newDL = new ZaDistributionList(this._app);
		this._app.getDistributionListController().show(newDL, ZaDLController.MODE_NEW);
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newDistributionListListener", null, false);
	}

};

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
		this._changeActionsState();
	}
}

ZaAccountListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._acctionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaAccountListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()){
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		this._editItem(item);
	}
}

ZaAccountListController.prototype._editItem = function (item) {
//	DBG.dumpObj(item, false, 1);
	var type = item.type;
	DBG.println("TYPE == ", item.type);
	if (type == ZaItem.ACCOUNT) {
		//this._selectedItem = ev.item;
		this._app.getAccountViewController().show(item);
	} else if (type == ZaItem.DL) {
		this._app.getDistributionListController().show(item, ZaDLController.MODE_EDIT);
	} else if(type == ZaItem.ALIAS) {
		var account = new ZaAccount(this._app);
		if(item.attrs && item.attrs[ZaAlias.A_AliasTargetId]) {
			account.load("id", item.attrs[ZaAlias.A_AliasTargetId], false);
			this._app.getAccountViewController().show(account);
		}
	}
};
/**
* This listener is called when the Change Password button is clicked. 
**/
ZaAccountListController.prototype._chngPwdListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()) {
		this._chngPwdDlg = new ZaAccChangePwdDlg(this._app.getAppCtxt().getShell(), this._app);
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		this._chngPwdDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountListController._changePwdOKCallback, this, item);				
		this._chngPwdDlg.popup(item.attrs[ZaAccount.A_zimbraPasswordMustChange]);
	}
}
/**
* Launches mail app to view user's email
**/
ZaAccountListController.launch = 
function (delegateToken, tokenLifetime, mailServer) {
	var form = this.document.createElement('form');
	form.style.display = 'none';
	this.document.body.appendChild(form);
	var html = new Array();
	var i = 0;
	if(!delegateToken)
		alert("Error! Failed to acquire authenticaiton token!");
			
	lifetime = tokenLifetime ? tokenLifetime : 300000;
					
	html[i++] = "<input type='hidden' name='authToken' value='" + delegateToken + "'>";
			
	if (tokenLifetime) {
		html[i++] = "<input type='hidden' name='atl' value='" + tokenLifetime + "'>";
	}
			
	form.innerHTML = html.join('');
		
				
	form.action = mailServer;
	form.method = 'post';
	form.submit();		
}

ZaAccountListController._viewMailListenerLauncher = 
function(account) {
	try {
		var obj;
		if(account.type == ZaItem.ACCOUNT) {
			obj = ZaAccount.getViewMailLink(account.id);
		} else if(account.type == ZaItem.ALIAS && account.attrs[ZaAlias.A_AliasTargetId]) {
			obj = ZaAccount.getViewMailLink(account.attrs[ZaAlias.A_AliasTargetId]);
		} else {
			return;
		}
		var win = window.open("about:blank", "_blank");
		var ms = account.attrs[ZaAccount.A_mailHost] ? account.attrs[ZaAccount.A_mailHost].toLowerCase() : location.hostname.toLowerCase();
		//find my server
		var servers = this._app.getServerList().getArray();
		var cnt = servers.length;
		var mailPort = 80;
		var mailProtocol = "http";
		
		for (var i = 0; i < cnt; i++) {
			if(servers[i].attrs[ZaServer.A_ServiceHostname].toLowerCase() == ms) {
				if(servers[i].attrs[ZaServer.A_zimbraMailSSLPort] && parseInt(servers[i].attrs[ZaServer.A_zimbraMailSSLPort]) > 0) { //if there is SSL, use SSL
					mailPort = servers[i].attrs[ZaServer.A_zimbraMailSSLPort];
					mailProtocol = "https";
				} else if (servers[i].attrs[ZaServer.A_zimbraMailPort] && parseInt(servers[i].attrs[ZaServer.A_zimbraMailPort]) > 0) { //otherwize use HTTP
					mailPort = servers[i].attrs[ZaServer.A_zimbraMailPort];
					mailProtocol = "http";
				}
				break;
			}
		}
		//TODO: get the port and hostname from zimbraServer object
		var mServer = mailProtocol + "://" + ms + ":" + mailPort + "/zimbra/auth/" + window.location.search;

		if(!obj.authToken || !obj.lifetime || !mServer)
			throw new AjxException("Failed to acquire credentials from the server", AjxException.UNKNOWN, "ZaAccountListController.prototype._viewMailListener");
			
		ZaAccountListController.launch.call(win, obj.authToken, obj.lifetime, mServer);
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController._viewMailListenerLauncher", null, false);			
	}		
}

ZaAccountListController.prototype._viewMailListener =
function(ev) {
	try {
		var el = this._contentView.getSelectedItems().getLast();
		if(el) {
			var account = DwtListView.prototype.getItemFromElement.call(this, el);
			if(account) {
				ZaAccountListController._viewMailListenerLauncher.call(this, account);
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._viewMailListener", null, false);			
	}
}


ZaAccountListController.prototype._nextPageListener = 
function (ev) {
	if(this._currentPageNum < this.pages[this._currentPageNum].numPages) {
		this._currentPageNum++;
		if(this.pages[this._currentPageNum]) {
			this.show(this.pages[this._currentPageNum])
		} else {
			this.show(ZaSearch.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));	
		}
	} 
}

ZaAccountListController.prototype._prevPageListener = 
function (ev) {
	if(this._currentPageNum > 1) {
		this._currentPageNum--;
		if(this.pages[this._currentPageNum]) {
			this.show(this.pages[this._currentPageNum])
		} else {
			this.show(ZaSearch.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));
		}

	} 
	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaAccountListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	var haveAliases = false;
	var haveAccounts = false;
	var haveDls = false;
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getArray()) {
		var arrDivs = this._contentView.getSelectedItems().getArray();
		var item = null;
		for(var key in arrDivs) {
			item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			if(item) {
				this._removeList.push(item);
			}
			if(!haveAliases && item.type == ZaItem.ALIAS) {
				haveAliases = true;
			} else if(!haveAccounts && item.type == ZaItem.ACCOUNT) {
				haveAccounts = true;
			} else if(!haveDls && item.type == ZaItem.DL) {
				haveDls = true;
			}
		}
	}
	if(this._removeList.length) {
		var dlgMsg;
		if(haveDls && !(haveAccounts || haveAliases)) {
			dlgMsg = ZaMsg.Q_DELETE_DLS;
		} else if(haveAccounts && !(haveDls || haveAliases)) {
			dlgMsg = ZaMsg.Q_DELETE_ACCOUNTS;
		} else if(haveAliases && !(haveDls || haveAccounts)) {
			dlgMsg = ZaMsg.Q_DELETE_ALIASES;
		} else {
			dlgMsg = ZaMsg.Q_DELETE_OBJECTS;
		}
		dlgMsg +=  "<br><ul>";
		var i=0;
		for(var key in this._removeList) {
			if(i > 19) {
				dlgMsg += "<li>...</li>";
				break;
			}
			dlgMsg += "<li>";
			var szAccName = this._removeList[key].attrs[ZaAccount.A_displayname] ? this._removeList[key].attrs[ZaAccount.A_displayname] : this._removeList[key].name;
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
		this._removeConfirmMessageDialog.setMessage(dlgMsg,  DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaAccountListController.prototype._deleteAccountsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaAccountListController.prototype._donotDeleteAccountsCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
}

ZaAccountListController.prototype._deleteAccountsCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				if(ex.code == ZmCsfeException.SVC_WRONG_HOST) {
					var szMsg = ZaMsg.ERROR_WRONG_HOST;
					if(ex.detail) {
						szMsg +="<br>Details:<br>";
						szMsg += ex.detail;
					}
					this._errorDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorDialog.popup();					
				} else {
					this._handleException(ex, "ZaAccountListController.prototype._deleteAccountsCallback", null, false);
				}
				return;
			}
		}
	}
	this._fireAccountRemovalEvent(successRemList); 
	this._removeConfirmMessageDialog.popdown();
	this.show(ZaSearch.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));			
}

ZaAccountListController.prototype._donotDeleteAccountsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaAccountListController.prototype.moveAlias = 
function() {
	//remove alias
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()){
		var alias = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
		//make sure this is an alias
		if(alias.type!=ZaItem.ALIAS) 
			return;
		
		var name = alias.name;
		try {
			alias.remove();
		} catch (ex) {
			this._handleException(ex, "ZaAccountListController._moveAliasCallback", null, false);
			return;
		}
		try {
			//get destination account		
			var srch = this._moveAliasDialog.getObject();
			if(srch[ZaSearch.A_selected] && srch[ZaSearch.A_selected].addAlias!=null) {
				//add alias
				srch[ZaSearch.A_selected].addAlias(name);
			}
//			this._moveAliasDialog.popdown();
		} catch (ex) {
			this._handleException(ex, "ZaAccountListController._moveAliasCallback", null, false);
		}
	}	
}

ZaAccountListController._changePwdOKCallback = 
function (item) {
	//check the passwords, if they are ok then save the password, else show error
	if(this._chngPwdDlg) {
		try {
			if(!this._chngPwdDlg.getPassword() || this._chngPwdDlg.getPassword().length < 1) {
				//this._chngPwdDlg.popdown();	//close the dialog
				this._errorMsgDlg = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);							
				this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_REQUIRED, null, DwtMessageDialog.CRITICAL_STYLE);
				this._errorMsgDlg.popup();				
			} else if(this._chngPwdDlg.getPassword() != this._chngPwdDlg.getConfirmPassword()) {
				//this._chngPwdDlg.popdown();	//close the dialog
				this._errorMsgDlg = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);							
				this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_MISMATCH, null, DwtMessageDialog.CRITICAL_STYLE);
				this._errorMsgDlg.popup();				
			} else {
				//check password
				var myCos = null;
//				var maxPwdLen = Number.POSITIVE_INFINITY;
				var maxPwdLen = null;
				var minPwdLen = null;	
				
				if(item.attrs[ZaAccount.A_zimbraMinPwdLength] != null) {
					minPwdLen = item.attrs[ZaAccount.A_zimbraMinPwdLength];
				} 
				
				if(item.attrs[ZaAccount.A_zimbraMaxPwdLength] != null) {
					maxPwdLen = item.attrs[ZaAccount.A_zimbraMaxPwdLength];
				} 
				
				if(!item.attrs[ZaAccount.A_COSId]) {
					var cosList = this._app.getCosList().getArray();
					item.attrs[ZaAccount.A_COSId] = cosList[0].id;
				}
				
				if (minPwdLen == null) {
					if(item.attrs[ZaAccount.A_COSId]) {
						myCos = new ZaCos(this._app);
						myCos.load("id", item.attrs[ZaAccount.A_COSId]);
						if(myCos.attrs[ZaCos.A_zimbraMinPwdLength] > 0) {
							minPwdLen = myCos.attrs[ZaCos.A_zimbraMinPwdLength];
						}
					}
				}			
				
				if (maxPwdLen == null) {
					if(item.attrs[ZaAccount.A_COSId]) {
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
					this._errorMsgDlg = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);												
					this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + String(ZaMsg.NAD_passMinLengthMsg).replace("{0}",minPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorMsgDlg.popup();
				} else if(AjxStringUtil.trim(szPwd).length > maxPwdLen) { 
					//show error msg
					//this._chngPwdDlg.popdown();
					this._errorMsgDlg = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.OK_BUTTON], this._app);																	
					this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + String(ZaMsg.NAD_passMaxLengthMsg).replace("{0}",maxPwdLen), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorMsgDlg.popup();
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
			//this._chngPwdDlg.popdown();
			if(ex.code == ZmCsfeException.INVALID_PASSWORD ) {
				var szMsg = ZaMsg.ERROR_PASSWORD_INVALID;
				if(ex.detail) {
					szMsg +="<br>Details:<br>";
					szMsg += ex.detail;
				}
				this._errorDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
				this._errorDialog.popup();
			} else {
				this._handleException(ex, "ZaAccountListController._changePwdOKCallback", null, false);			
			}
			return;
		}
	}
}

ZaAccountListController.prototype._changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE];
		var opsArray2 = new Array();
		if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()){
			var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
			if(item.type == ZaItem.ALIAS) {
				opsArray.push(ZaOperation.MOVE_ALIAS);
				opsArray.push(ZaOperation.VIEW_MAIL);
				
				opsArray2.push(ZaOperation.CHNG_PWD);
			} else if(item.type == ZaItem.ACCOUNT) {
				opsArray.push(ZaOperation.VIEW_MAIL);
				opsArray.push(ZaOperation.CHNG_PWD);				
				
				opsArray2.push(ZaOperation.MOVE_ALIAS);				
			} else if(item.type == ZaItem.DL) {
				opsArray2.push(ZaOperation.MOVE_ALIAS);
				opsArray2.push(ZaOperation.VIEW_MAIL);
				opsArray2.push(ZaOperation.CHNG_PWD);
			}
		}		
		this._toolbar.enable(opsArray, true);
		this._acctionMenu.enable(opsArray, true);
		
		this._toolbar.enable(opsArray2, false);
		this._acctionMenu.enable(opsArray2, false);
	} else if (cnt > 1){
		var opsArray1 = [ZaOperation.EDIT, ZaOperation.CHNG_PWD, ZaOperation.VIEW_MAIL, ZaOperation.MOVE_ALIAS];
		this._toolbar.enable(opsArray1, false);
		this._acctionMenu.enable(opsArray1, false);

		var opsArray2 = [ZaOperation.DELETE];
		this._toolbar.enable(opsArray2, true);
		this._acctionMenu.enable(opsArray2, true);
	} else {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.CHNG_PWD, ZaOperation.VIEW_MAIL,ZaOperation.MOVE_ALIAS];
		this._toolbar.enable(opsArray, false);
		this._acctionMenu.enable(opsArray, false);
	}
}


ZaAccountListController.prototype._restoreMailListener = 
function (ev) {
	try {
		this._newSingleAccountRestoreWizard = new SingleAccountRestoreXWizard(this._container, this._app);		
		var restore = new ZaRestore(this._app);
		this._newSingleAccountRestoreWizard.setObject(restore);		
		this._newSingleAccountRestoreWizard.popup();
//		var serverId = this._app.getServerList().getArray()[0].id;
	//	ZaBackup.queryBackups(serverId, null, null, null, "1");
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._restoreMailListener", null, false);
	}
	return;
}
 
ZaAccountListController.prototype._moveAliasListener = 
function (ev) {
	try {
		var alias;
		if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getLast()){
			var alias = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getLast());
			//make sure this is an alias
			if(!alias || alias.type!=ZaItem.ALIAS) {
				return;			
			}
		}
		if(!this._moveAliasDialog) {
			this._moveAliasDialog = new MoveAliasXDialog(this._container, this._app);
		}
		this._moveAliasDialog.setAlias(alias);
		this._moveAliasDialog.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._moveAliasListener", null, false);
	}
	return;
}
