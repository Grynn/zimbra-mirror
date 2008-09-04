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
* @class ZaDomainListController
* This is a singleton object that controls all the user interaction with the list of ZaDomain objects
**/
ZaDomainListController = function(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app,"ZaDomainListController");
	this._helpURL = ZaDomainListController.helpURL;	
	this._currentQuery = ""
	this._currentPageNum = 1;
	this._currentSortField = ZaDomain.A_domainName;
	this.objType = ZaEvent.S_DOMAIN;	
	this.RESULTSPERPAGE = ZaDomain.RESULTSPERPAGE; 
	this.MAXSEARCHRESULTS = ZaDomain.MAXSEARCHRESULTS;	
}

ZaDomainListController.prototype = new ZaListViewController();
ZaDomainListController.prototype.constructor = ZaDomainListController;
ZaDomainListController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/managing_domains.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaController.initToolbarMethods["ZaDomainListController"] = new Array();
ZaController.initPopupMenuMethods["ZaDomainListController"] = new Array();
ZaListViewController.changeActionsStateMethods["ZaDomainListController"] = new Array(); 

ZaDomainListController.prototype.show = function (doPush) {
	var callback = new AjxCallback(this, this.searchCallback, {limit:ZaDomain.RESULTSPERPAGE,CONS:ZaDomain,show:doPush});
	var searchParams = {
			query:this._currentQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:"1",
			limit:this.RESULTSPERPAGE,
			callback:callback,
			controller: this
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaDomainListController.prototype._show = 
function (list,  openInNewTab, openInSearchTab) {
	this._updateUI(list, openInNewTab, openInSearchTab);
	//this._app.pushView(ZaZimbraAdmin._DOMAINS_LIST_VIEW);
	this._app.pushView(this.getContentViewId(), openInNewTab, openInSearchTab);
	
	if (openInSearchTab) {
		this._app.updateSearchTab();
	}else{
		this._app.updateTab(this.getMainTab(), this._app._currentViewId );
	}
}



/**
* @return ZaItemList - the list currently displaid in the list view
**/
ZaDomainListController.prototype.getList = 
function() {
	return this._list;
}


ZaDomainListController.prototype.set = 
function(domainList) {
	this.show(domainList);
}

/**
* @param ev
* This listener is invoked by ZaAccountViewController or any other controller that can change an ZaDomain object
**/
ZaDomainListController.prototype.handleDomainChange = 
function (ev) {
	//if any of the data that is currently visible has changed - update the view
	if(ev) {
		var details = ev.getDetails();
		//details["modFields"] is outdated.
		//if(details["modFields"] && (details["modFields"][ZaDomain.A_description] || details["modFields"][ZaDomain.A_domainName])) {
		if (details){
			if (this._list) this._list.replace(details);
			if (this._contentView) this._contentView.setUI();
			if(this._app.getCurrentController() == this) {
				this.show();			
			}
			this.changeActionsState();			
		}
	}
}


ZaDomainListController.initPopupMenuMethod =
function () {
   	this._popupOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, ZaDomainListController.prototype._newButtonListener)));
   	this._popupOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.DTBB_Edit_tt, "Properties", "PropertiesDis",  new AjxListener(this, ZaDomainListController.prototype._editButtonListener)));    	
   	this._popupOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaDomainListController.prototype._deleteButtonListener)));    	    	
	this._popupOperations.push(new ZaOperation(ZaOperation.GAL_WIZARD, ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainListController.prototype._galWizButtonListener)));   		
	this._popupOperations.push(new ZaOperation(ZaOperation.AUTH_WIZARD, ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainListController.prototype._authWizButtonListener)));   		   		

}
ZaController.initPopupMenuMethods["ZaDomainListController"].push(ZaDomainListController.initPopupMenuMethod);

/**
* This method is called from {@link ZaController#_initToolbar}
**/
ZaDomainListController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, ZaDomainListController.prototype._newButtonListener)));
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.DTBB_Edit_tt, "Properties", "PropertiesDis",  new AjxListener(this, ZaDomainListController.prototype._editButtonListener)));    	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaDomainListController.prototype._deleteButtonListener)));    	    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.GAL_WIZARD, ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainListController.prototype._galWizButtonListener)));   		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.AUTH_WIZARD, ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainListController.prototype._authWizButtonListener)));   		   		
	
}
ZaController.initToolbarMethods["ZaDomainListController"].push(ZaDomainListController.initToolbarMethod);

//private and protected methods
ZaDomainListController.prototype._createUI = 
function (openInNewTab, openInSearchTab) {
	this._contentView = new ZaDomainListView(this._container);
	this._app._controllers[this.getContentViewId ()] = this ;
	// create the menu operations/listeners first	
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
	//this._app.createView(ZaZimbraAdmin._DOMAINS_LIST_VIEW, elements);
	var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: openInSearchTab ? this.getSearchTab() : this.getMainTab() 
		}
	this._app.createView(this.getContentViewId(), elements, tabParams) ;
	
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	this._removeConfirmMessageDialog = this._app.dialogs["removeConfirmMessageDialog"] = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
		
	this._UICreated = true;
}

/**
*	Private method that notifies listeners that a new ZaDomain is created
* 	@param details
*/
ZaDomainListController.prototype._fireDomainCreationEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_CREATE)) {
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_CREATE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_CREATE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._fireDomainCreationEvent", details, false);	
	}
}

/**
*	Private method that notifies listeners to that the controlled ZaDomain is changed
* 	@param details
*/
ZaDomainListController.prototype._fireDomainChangeEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_MODIFY, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._fireDomainChangeEvent", details, false);	
	}
}

/**
* This listener is called when the item in the list is double clicked. It call ZaDomainController.show method
* in order to display the Domain View
**/
ZaDomainListController.prototype._listSelectionListener =
function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._app.getDomainController().show(ev.item);
		}
	} else {
		this.changeActionsState();	
	}
}

ZaDomainListController.prototype._listActionListener =
function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

/**
* This listener is called when the Edit button is clicked. 
* It call ZaDomainController.show method
* in order to display the Domain View
**/
ZaDomainListController.prototype._editButtonListener =
function(ev) {
	if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		this._app.getDomainController().show(item);
	}
}

// new button was pressed
ZaDomainListController.prototype._newButtonListener =
function(ev) {
	try {
		var domain = new ZaDomain(this._app);
		this._newDomainWizard = this._app.dialogs["newDomainWizard"] = new ZaNewDomainXWizard(this._container, this._app);	
		this._newDomainWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainListController.prototype._finishNewButtonListener, this, null);			
		this._newDomainWizard.setObject(domain);
		this._newDomainWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._newButtonListener", null, false);
	}
}


ZaDomainListController.prototype._galWizButtonListener =
function(ev) {
	try {
		if(this._contentView.getSelectionCount() == 1) {
			var item = this._contentView.getSelection()[0];
			this._currentObject = item;
			this._galWizard = this._app.dialogs["galWizard"] = new ZaGALConfigXWizard(this._container, this._app);	
			this._galWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainListController.prototype._finishGalButtonListener, this, null);			
			this._galWizard.setObject(item);
			this._galWizard.popup();
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._showGalWizard", null, false);
	}
}


ZaDomainListController.prototype._authWizButtonListener =
function(ev) {
	try {
		if(this._contentView.getSelectionCount() == 1) {
			var item = this._contentView.getSelection()[0];
			this._currentObject = item;
			this._authWizard = this._app.dialogs["authWizard"] = new ZaAuthConfigXWizard(this._container, this._app);	
			this._authWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainListController.prototype._finishAuthButtonListener, this, null);			
			this._authWizard.setObject(item);
			this._authWizard.popup();
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._showAuthWizard", null, false);
	}
}
/**
* This listener is called when the Delete button is clicked. 
**/
ZaDomainListController.prototype._deleteButtonListener =
function(ev) {
	this._removeList = new Array();
	this._itemsInTabList = [] ;
	if(this._contentView.getSelectionCount()>0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			var item = arrItems[key];
			if (item) {
				if (this._app.getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);
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
		msg += ZaDomainListController.getDlMsgFromList (this._itemsInTabList) ;
		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaDomainListController.prototype._closeTabsBeforeRemove, this);
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaDomainListController.prototype._deleteDomainInRemoveList, this);		
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	}else{
		this._deleteDomainInRemoveList ();
	}
}


ZaDomainListController.prototype._closeTabsBeforeRemove =
function () {
	this.closeTabsInRemoveList() ;
	this._deleteDomainInRemoveList();
}

ZaDomainListController.prototype._deleteDomainInRemoveList =
function () {
	if (this._app.dialogs["ConfirmDeleteItemsInTabDialog"]) {
		this._app.dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
	}
	if(this._removeList.length) {
		var dlgMsg = ZaMsg.Q_DELETE_DOMAINS;
		dlgMsg += ZaDomainListController.getDlMsgFromList (this._removeList);
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainListController.prototype._deleteDomainsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainListController.prototype._donotDeleteDomainsCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
	
} 

ZaDomainListController.getDlMsgFromList =
function (listArr) {
	var	dlgMsg = "<br><ul>";
	for(var key in listArr) {
		if(i > 19) {
			dlgMsg += "<li>...</li>";
			break;
		}
		dlgMsg += "<li>";
		if(listArr[key].name.length > 50) {
			//split it
			var endIx = 49;
			var beginIx = 0; //
			while(endIx < listArr[key].name.length) { //
				dlgMsg +=  listArr[key].name.slice(beginIx, endIx); //
				beginIx = endIx + 1; //
				if(beginIx >= (listArr[key].name.length) ) //
					break;
				
				endIx = ( listArr[key].name.length <= (endIx + 50) ) ? listArr[key].name.length-1 : (endIx + 50);
				dlgMsg +=  "<br>";	
			}
		} else {
			dlgMsg += listArr[key].name;
		}
		dlgMsg += "</li>";
		i++;
	}
	dlgMsg += "</ul>";
	return dlgMsg ;
}

ZaDomainListController.prototype._deleteDomainsCallback = 
function () {
	var successRemList=new Array();
	for(var key in this._removeList) {
		if(this._removeList[key]) {
			try {
				this._removeList[key].remove();
				successRemList.push(this._removeList[key]);					
			} catch (ex) {
				this._removeConfirmMessageDialog.popdown();
				if(ex.code == ZmCsfeException.DOMAIN_NOT_EMPTY) {
					this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_NOT_EMPTY, null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorDialog.popup();			
				} else {
					this._handleException(ex, "ZaDomainListController.prototype._deleteDomainsCallback", null, false);				
				}
				return;
			}
		}
		this._list.remove(this._removeList[key]); //remove from the list
	}
	this.fireRemovalEvent(successRemList); 		
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaDomainListController.prototype._donotDeleteDomainsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaDomainListController.changeActionsStateMethod = 
function (enableArray,disableArray) {
	if(!this._contentView)
		return;
		
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
			if(!(item.attrs[ZaDomain.A_zimbraDomainStatus] == ZaDomain.DOMAIN_STATUS_SHUTDOWN)) {
				enableArray.push(ZaOperation.EDIT);
				enableArray.push(ZaOperation.DELETE);
				enableArray.push(ZaOperation.AUTH_WIZARD);				
				enableArray.push(ZaOperation.GAL_WIZARD);				
			} else {
				enableArray.push(ZaOperation.EDIT);
				disableArray.push(ZaOperation.DELETE);
				disableArray.push(ZaOperation.AUTH_WIZARD);				
				disableArray.push(ZaOperation.GAL_WIZARD);							
			}
		}
	} else if (cnt > 1){
		enableArray.push(ZaOperation.DELETE);
		disableArray.push(ZaOperation.AUTH_WIZARD);				
		disableArray.push(ZaOperation.GAL_WIZARD);		
		disableArray.push(ZaOperation.EDIT);
	} else {
		disableArray.push(ZaOperation.EDIT);
		disableArray.push(ZaOperation.DELETE);
		disableArray.push(ZaOperation.AUTH_WIZARD);				
		disableArray.push(ZaOperation.GAL_WIZARD);		
	}
}
ZaListViewController.changeActionsStateMethods["ZaDomainListController"].push(ZaDomainListController.changeActionsStateMethod);

ZaDomainListController.prototype._finishNewButtonListener =
function(ev) {
	try {
		var domain = ZaDomain.create(this._newDomainWizard.getObject(), this._app);
		if(domain != null) {
			this._newDomainWizard.popdown();
			//if creation took place - fire an DomainChangeEvent
			this._fireDomainCreationEvent(domain);
			if(this._newDomainWizard.getObject()[ZaDomain.A_CreateNotebook]=="TRUE") {
				var params = new Object();
/*				if(this._newDomainWizard.getObject()[ZaDomain.A_OverwriteNotebookACLs]) {
					params[ZaDomain.A_OverwriteNotebookACLs] = true;*/
					params.obj = this._newDomainWizard.getObject();
/*				} else
					params[ZaDomain.A_OverwriteNotebookACLs] = false;
*/					
				var callback = new AjxCallback(this, this.initNotebookCallback, params);				
				ZaDomain.initNotebook(this._newDomainWizard.getObject(),callback, this) ;
			}			
			

		
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_CREATE, this);
			evt.setDetails(domain);
			this.handleCreation(evt);
		}
	} catch (ex) {
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_EXISTS, ex);
		} else {
			this._handleException(ex, "ZaDomainListController.prototype._finishNewButtonListener", null, false);
		}
	}
	return;
}

ZaDomainListController.prototype._finishAuthButtonListener =
function(ev) {
	try {
		ZaDomain.modifyAuthSettings.call(this._currentObject,this._authWizard.getObject());
		//var changeDetails = new Object();
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
		this._fireDomainChangeEvent(this._currentObject);
		this._authWizard.popdown();
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._finishAuthButtonListener", null, false);
	}
	return;
}

ZaDomainListController.prototype._finishGalButtonListener =
function(ev) {
	try {
		//var changeDetails = new Object();
		ZaDomain.modifyGalSettings.call(this._currentObject,this._galWizard.getObject()); 
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
		this._fireDomainChangeEvent(this._currentObject);
		this._galWizard.popdown();
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._finishGalButtonListener", null, false);
	}
	return;
}

ZaDomainListController.prototype.initNotebookCallback = 
function (params, resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		this._handleException(resp.getException(), "ZaDomainListController.prototype.initNotebookCallback", null, false);
		return;
	} 
//	if(params[ZaDomain.A_OverwriteNotebookACLs] && params.obj!=null) {
	var callback = new AjxCallback(this, this.setNotebookAclsCallback);				
	ZaDomain.setNotebookACLs(params.obj, callback) ;
//	}
	
}

ZaDomainListController.prototype.setNotebookAclsCallback = 
function (resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		this._handleException(resp.getException(), "ZaDomainListController.prototype.setNotebookAclsCallback", null, false);
		return;
	} 
}