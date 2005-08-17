/**
* @constructor
* @class ZaAccountListController
* @param appCtxt
* @param container
* @param app
* This is a singleton object that controls all the user interaction with the list of ZaAccount objects
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaAccountListController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app);
	this._evtMgr = new AjxEventMgr();			
	this._currentPageNum = 1;
	this._currentQuery = new ZaAccountQuery("", false, "");
//	this._searchResult=null;
//	this._totalPages = 1;
	this._currentSortField = ZaAccount.A_uid;
	this._currentSortOrder = true;
	this.pages = new Object();
}

ZaAccountListController.prototype = new ZaController();
ZaAccountListController.prototype.constructor = ZaAccountListController;

ZaAccountListController.ACCOUNT_VIEW = "ZaAccountListController.ACCOUNT_VIEW";

ZaAccountListController.prototype.show = 
function(searchResult) {
    if (!this._appView) {

		//create accounts list view
		this._contentView = new ZaAccountListView(this._container, this._app);
    	//toolbar
    	this._ops = new Array();

   		this._ops.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, ZaImg.I_ACCOUNT, ZaImg.I_ACCOUNT, new AjxListener(this, ZaAccountListController.prototype._newButtonListener)));    	
    	this._ops.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, ZaImg.I_PROPERTIES, ZaImg.I_PROPERTIES, new AjxListener(this, ZaAccountListController.prototype._editButtonListener)));
    	this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, ZaImg.I_DELETE, ZaImg.I_DELETE, new AjxListener(this, ZaAccountListController.prototype._deleteButtonListener)));
		this._ops.push(new ZaOperation(ZaOperation.CHNG_PWD, ZaMsg.TBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, ZaImg.I_PADLOCK, ZaImg.I_PADLOCK, new AjxListener(this, ZaAccountListController.prototype._chngPwdListener)));
		this._ops.push(new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.TBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, ZaImg.I_PADLOCK, ZaImg.I_PADLOCK, new AjxListener(this, ZaAccountListController.prototype._viewMailListener)));		


		
    	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._ops);
    
   		var haveBackup = false;
		var globalConf = this._app.getGlobalConfig();

    	if(globalConf && globalConf.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_hotbackup])
			this._ops.push(new ZaOperation(ZaOperation.MAIL_RESTORE, ZaMsg.TBB_RestoreMailbox, ZaMsg.ACTBB_Restore_tt, ZaImg.I_ACCOUNT, ZaImg.I_ACCOUNT, new AjxListener(this, ZaAccountListController.prototype._restoreMailListener)));		
			
		this._ops.push(new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Back, ZaMsg.PrevPage_tt, ZaImg.I_BACK_ARROW, ZaImg.ID_BACK_ARROW,  new AjxListener(this, ZaAccountListController.prototype._prevPageListener)));
		this._ops.push(new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Forward, ZaMsg.NextPage_tt, ZaImg.I_FORWARD_ARROW, ZaImg.ID_FORWARD_ARROW, new AjxListener(this, ZaAccountListController.prototype._nextPageListener)));

		this._toolbar = new ZaToolBar(this._container, this._ops);    
		

		this._appView = this._app.createView(ZaAccountListController.ACCOUNT_VIEW, [this._toolbar,  this._contentView]);

    	//context menu

		if (searchResult && searchResult.list != null) {
			var tmpArr = new Array();
			var cnt = searchResult.list.getArray().length;
			for(var ix = 0; ix < cnt; ix++) {
				tmpArr.push(searchResult.list.getArray()[ix]);
			}
			this._contentView.set(AjxVector.fromArray(tmpArr));	
		}
		this._app.pushView(ZaAccountListController.ACCOUNT_VIEW);			
		
		//set a selection listener on the account list view
		this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
		this._removeConfirmMessageDialog = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
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
		this._app.pushView(ZaAccountListController.ACCOUNT_VIEW);
	}
	this._app.setCurrentController(this);		
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

ZaAccountListController.prototype.preloadNextPage = 
function() {
	if((this._currentPageNum + 1) <= this.pages[this._currentPageNum].numPages && !this.pages[this._currentPageNum+1]) {
		this.pages[this._currentPageNum+1] = ZaAccount.searchByQueryHolder(this._currentQuery,this._currentPageNum+1, this._currentSortField, this._currentSortOrder, this._app)
	}		
	this._shell.setBusy(false);
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

ZaAccountListController.prototype.setQuery = 
function (query) {
	this._currentQuery = query;
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
/**
* Adds listener to removal of an ZaAccount 
* @param listener
**/
ZaAccountListController.prototype.addAccountRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
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
		this.show(ZaAccount.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));			
	} else {
		var searchResult = ZaAccount.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
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
			var srchResult = ZaAccount.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
			while(this._currentPageNum > 1) { 
				if(srchResult.numPages < this._currentPageNum) {
					this._currentPageNum--;
					srchResult = ZaAccount.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app);
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

// new button was pressed
ZaAccountListController.prototype._newButtonListener =
function(ev) {
	try {
		var newAccount = new ZaAccount(this._app);
		this._newAccountWizard = new ZaNewAccountXWizard(this._container, this._app);	
//		this._newAccountWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaAccountListController.prototype._finishNewButtonListener, this, null);			
		this._newAccountWizard.setObject(newAccount);
		this._newAccountWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newButtonListener", null, false);
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
			//this._selectedItem = ev.item;
			this._app.getAccountViewController().show(ev.item);
		}
	} else {
		this._changeActionsState();
	}
}

ZaAccountListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaAccountViewController.show method
* in order to display the Account View
**/
ZaAccountListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getZast()){
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getZast());
		this._app.getAccountViewController().show(item);
	}
}


/**
* This listener is called when the Change Password button is clicked. 
**/
ZaAccountListController.prototype._chngPwdListener =
function(ev) {
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getZast()) {
		this._chngPwdDlg = new ZaAccChangePwdDlg(this._appView.shell, this._app);
		var item = DwtListView.prototype.getItemFromElement.call(this, this._contentView.getSelectedItems().getZast());
		this._chngPwdDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountListController._changePwdOKCallback, this, item);				
		this._chngPwdDlg.popup(item.attrs[ZaAccount.A_zimbraPasswordMustChange]);
	}
}
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

ZaAccountListController.prototype._viewMailListener =
function(ev) {
	try {
		var el = this._contentView.getSelectedItems().getZast();
		if(el) {
			var account = DwtListView.prototype.getItemFromElement.call(this, el);
			if(account) {
				var obj = ZaAccount.getViewMailLink(account.id);
				var win = window.open("about:blank", "_blank");
				var ms = account.attrs[ZaAccount.A_mailHost] ? account.attrs[ZaAccount.A_mailHost] : location.hostname;
				//find my server
				var servers = this._app.getServerList().getArray();
				var cnt = servers.length;
				var mailPort = 80;
				var mailProtocol = "http";
				
				for (var i = 0; i < cnt; i++) {
					if(servers[i].attrs[ZaServer.A_ServiceHostname] == ms) {
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
			this.show(ZaAccount.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));	
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
			this.show(ZaAccount.searchByQueryHolder(this._currentQuery,this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));
		}
/*		if(this._currentQuery.isByDomain) {
			this.show(ZaAccount.searchByDomain(_currentQuery.byWhatAttr,_currentQuery.byValAttr,--this._currentPageNum, this._currentSortField, this._currentSortOrder));	
		} else {
			this.show(ZaAccount.search(this._currentQuery.queryString, --this._currentPageNum, this._currentSortField, this._currentSortOrder));
		}*/
	} 
	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaAccountListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	if(this._contentView.getSelectedItems() && this._contentView.getSelectedItems().getArray()) {
		var arrDivs = this._contentView.getSelectedItems().getArray();
		var item = null;
		for(var key in arrDivs) {
			item = DwtListView.prototype.getItemFromElement.call(this, arrDivs[key]);
			if(item) {
				this._removeList.push(item);
			}
		}
	}
	if(this._removeList.length) {
		var dlgMsg = ZaMsg.Q_DELETE_ACCOUNTS;
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
		this._removeConfirmMessageDialog.setMessage(dlgMsg, null, DwtMessageDialog.INFO_STYLE);
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
				if(ex.code == AjxCsfeException.SVC_WRONG_HOST) {
					var szMsg = ZaMsg.ERROR_WRONG_HOST;
					if(ex.detail) {
						szMsg +="<br>Details:<br>";
						szMsg += ex.detail;
					}
					this._msgDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._msgDialog.popup();					
				} else {
					this._handleException(ex, "ZaAccountListController.prototype._deleteAccountsCallback", null, false);
				}
				return;
			}
		}
	}
	this._fireAccountRemovalEvent(successRemList); 
	this._removeConfirmMessageDialog.popdown();
	this.show(ZaAccount.searchByQueryHolder(this._currentQuery, this._currentPageNum, this._currentSortField, this._currentSortOrder, this._app));			
}

ZaAccountListController.prototype._donotDeleteAccountsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaAccountListController._changePwdOKCallback = 
function (item) {
	//check the passwords, if they are ok then save the password, else show error
	if(this._chngPwdDlg) {
		try {
			if(!this._chngPwdDlg.getPassword() || this._chngPwdDlg.getPassword().length < 1) {
				this._chngPwdDlg.popdown();	//close the dialog
				this._errorMsgDlg = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.OK_BUTTON], this._app);							
				this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_REQUIRED, null, DwtMessageDialog.CRITICAL_STYLE);
				this._errorMsgDlg.popup();				
			} else if(this._chngPwdDlg.getPassword() != this._chngPwdDlg.getConfirmPassword()) {
				this._chngPwdDlg.popdown();	//close the dialog
				this._errorMsgDlg = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.OK_BUTTON], this._app);							
				this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_MISMATCH, null, DwtMessageDialog.CRITICAL_STYLE);
				this._errorMsgDlg.popup();				
			} else {
				//check password
				var myCos = null;
				var maxPwdLen = Number.POSITIVE_INFINITY;
				var minPwdLen = 1;	
				
				if(item.attrs[ZaAccount.A_COSId]) {
					myCos = new ZaCos(this._app);
					myCos.load("id", item.attrs[ZaAccount.A_COSId]);
					if(myCos.attrs[ZaCos.A_minPwdLength] > 0) {
						minPwdLen = myCos.attrs[ZaCos.A_minPwdLength];
					}
					if(myCos.attrs[ZaCos.A_maxPwdLength] > 0) {
						maxPwdLen = myCos.attrs[ZaCos.A_maxPwdLength];
					}		
				}			
				var szPwd = this._chngPwdDlg.getPassword();
				if(szPwd.length < minPwdLen || AjxStringUtil.trim(szPwd).length < minPwdLen) { 
					//show error msg
					this._chngPwdDlg.popdown();
					this._errorMsgDlg = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.OK_BUTTON], this._app);												
					this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_TOOSHORT + "<br>" + ZaMsg.NAD_passMinLength + ": " + minPwdLen, null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorMsgDlg.popup();
				} else if(AjxStringUtil.trim(szPwd).length > maxPwdLen) { 
					//show error msg
					this._chngPwdDlg.popdown();
					this._errorMsgDlg = new ZaMsgDialog(this._appView.shell, null, [DwtDialog.OK_BUTTON], this._app);																	
					this._errorMsgDlg.setMessage(ZaMsg.ERROR_PASSWORD_TOOLONG+ "<br>" + ZaMsg.NAD_passMaxLength + ": " + maxPwdLen, null, DwtMessageDialog.CRITICAL_STYLE, null);
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
			this._chngPwdDlg.popdown();
			if(ex.code == AjxCsfeException.INVALID_PASSWORD ) {
				var szMsg = ZaMsg.ERROR_PASSWORD_INVALID;
				if(ex.detail) {
					szMsg +="<br>Details:<br>";
					szMsg += ex.detail;
				}
				this._msgDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
				this._msgDialog.popup();
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
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.CHNG_PWD, ZaOperation.VIEW_MAIL];
		this._toolbar.enable(opsArray, true);
		this._actionMenu.enable(opsArray, true);
	} else if (cnt > 1){
		var opsArray1 = [ZaOperation.EDIT, ZaOperation.CHNG_PWD, ZaOperation.VIEW_MAIL];
		this._toolbar.enable(opsArray1, false);
		this._actionMenu.enable(opsArray1, false);

		var opsArray2 = [ZaOperation.DELETE];
		this._toolbar.enable(opsArray2, true);
		this._actionMenu.enable(opsArray2, true);
	} else {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.CHNG_PWD, ZaOperation.VIEW_MAIL];
		this._toolbar.enable(opsArray, false);
		this._actionMenu.enable(opsArray, false);
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

