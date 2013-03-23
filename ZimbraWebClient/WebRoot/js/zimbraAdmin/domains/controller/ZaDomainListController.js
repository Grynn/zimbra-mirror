/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012 VMware, Inc.
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
* @class ZaDomainListController
* This is a singleton object that controls all the user interaction with the list of ZaDomain objects
**/
ZaDomainListController = function(appCtxt, container) {
	ZaListViewController.call(this, appCtxt, container, "ZaDomainListController");
	this._helpURL = ZaDomainListController.helpURL;
	this._helpButtonText = ZaMsg.helpManageDomains;
	this._currentQuery = ""
	this._currentPageNum = 1;
	this._currentSortField = ZaDomain.A_domainName;
	this.objType = ZaEvent.S_DOMAIN;
    this._defaultType = ZaItem.DOMAIN;
	this.RESULTSPERPAGE = ZaDomain.RESULTSPERPAGE; 
	this.MAXSEARCHRESULTS = ZaDomain.MAXSEARCHRESULTS;	
}

ZaDomainListController.prototype = new ZaListViewController();
ZaDomainListController.prototype.constructor = ZaDomainListController;
ZaDomainListController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/managing_domains.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaController.initToolbarMethods["ZaDomainListController"] = new Array();
ZaController.initPopupMenuMethods["ZaDomainListController"] = new Array();
ZaController.changeActionsStateMethods["ZaDomainListController"] = new Array(); 

ZaDomainListController.prototype.show = function (doPush,openInNewTab) {

    if(!ZaZimbraAdmin.hasGlobalDomainListAccess() && this._currentQuery == "") {
        var domainNameList = ZaApp.getInstance()._domainNameList;
        if(domainNameList && (domainNameList instanceof Array) && domainNameList.length > 0) {
            for(var i = 0; i < domainNameList.length; i++)
                this._currentQuery += "(" + ZaDomain.A_domainName + "=" + domainNameList[i] + ")";
            if(domainNameList.length > 1)
                this._currentQuery = "(|" + this._currentQuery + ")";
        } else {
            this._list = new ZaItemList(ZaDomain);
            this.numPages = 0;
            this._searchTotal = 0;
            if(doPush) this._show(this._list);
            else this._updateUI(this._list);
            return;
        }
    }

	var busyId = Dwt.getNextId();
	var callback = new AjxCallback(this, this.searchCallback, {openInNewTab:openInNewTab,limit:ZaDomain.RESULTSPERPAGE,CONS:ZaDomain,show:doPush, busyId:busyId});
	var searchParams = {
			query:this._currentQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:"1",
			limit:this.RESULTSPERPAGE,
			callback:callback,
			controller: this,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false,
			attrs:[ZaDomain.A_description, ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType]		
	}
    this.scrollSearchParams={
        query:this._currentQuery,
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			sortAscending:"1",
			controller: this,
			showBusy:true,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false,
			attrs:[ZaDomain.A_description, ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType]
    };
	ZaSearch.searchDirectory(searchParams);
}

ZaDomainListController.prototype._show = 
function (list,  openInNewTab, openInSearchTab, hasMore) {
	this._updateUI(list, openInNewTab, openInSearchTab, hasMore);
	//ZaApp.getInstance().pushView(ZaZimbraAdmin._DOMAINS_LIST_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId(), openInNewTab, openInSearchTab);
    return;
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
			if(ZaApp.getInstance().getCurrentController() == this) {
				this.show();			
			} else if(this.objType && ev.type==this.objType && this._UICreated) {
                this.show(false);
            }
			this.changeActionsState();
             ZaZimbraAdmin.getInstance().getOverviewPanelController().refreshRelatedTreeByEdit (ev.getDetails());
		}
	}
}


ZaDomainListController.initPopupMenuMethod =
function () {
	if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_TOP_DOMAIN, ZaZimbraAdmin.currentAdminAccount)) {
		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, ZaDomainListController.prototype._newButtonListener));
	}
	this._popupOperations[ZaOperation.EDIT]=new ZaOperation(ZaOperation.EDIT,ZaMsg.TBB_Edit, ZaMsg.DTBB_Edit_tt, "Edit", "EditDis",  new AjxListener(this, ZaDomainListController.prototype._editButtonListener));    	
	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaDomainListController.prototype._deleteButtonListener));    	    	
    	if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_TOP_DOMAIN, ZaZimbraAdmin.currentAdminAccount)){
		this._popupOperations[ZaOperation.ADD_DOMAIN_ALIAS]=new ZaOperation(ZaOperation.ADD_DOMAIN_ALIAS,ZaMsg.TBB_AddDomainAlias, ZaMsg.DTBB_addDomainAlias_tt, "DomainAlias", "DomainAliasDis", new AjxListener(this, ZaDomainListController.prototype._addDomainAliasListener));
	}
	this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS]=new ZaOperation(ZaOperation.VIEW_DOMAIN_ACCOUNTS,ZaMsg.Domain_view_accounts, ZaMsg.Domain_view_accounts_tt, "Search", "SearchDis", new AjxListener(this, this.viewAccountsButtonListener));
	this._popupOperations[ZaOperation.GAL_WIZARD]=new ZaOperation(ZaOperation.GAL_WIZARD,ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainListController.prototype._galWizButtonListener));   		
	this._popupOperations[ZaOperation.AUTH_WIZARD]=new ZaOperation(ZaOperation.AUTH_WIZARD,ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainListController.prototype._authWizButtonListener));
    /* bug 71235, remove auto provisioning
     this._popupOperations[ZaOperation.AUTOPROV_WIZARD]=new ZaOperation(ZaOperation.AUTOPROV_WIZARD,ZaMsg.DTBB_AutoProvConfigWiz, ZaMsg.DTBB_AutoProvConfigWiz_tt, "Backup", "BackupDis", new AjxListener(this, ZaDomainListController.prototype._autoProvWizButtonListener));
    */
}
ZaController.initPopupMenuMethods["ZaDomainListController"].push(ZaDomainListController.initPopupMenuMethod);

//private and protected methods
ZaDomainListController.prototype._createUI = 
function (openInNewTab, openInSearchTab) {
	this._contentView = new ZaDomainListView(this._container, this._defaultType);
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	// create the menu operations/listeners first	
	//always add Help and navigation buttons at the end of the toolbar    
	//add the acount number counts
	//ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);
	
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
    ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
	this._initPopupMenu();
	this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_DMLIST, ZaId.MENU_POP);
	
	//set a selection listener on the account list view
	this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	this._removeConfirmMessageDialog = ZaApp.getInstance().dialogs["removeConfirmMessageDialog"] = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON],null,ZaId.CTR_PREFIX + ZaId.VIEW_DMLIST + "_removeConfirm");			
	this._forceRemoveMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON],null,ZaId.CTR_PREFIX + ZaId.VIEW_DMLIST + "_forceRemoveConfirm");
    this._forceRemoveMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainListController.prototype._forceDeleteDomainCallback, this);
    this._forceRemoveMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainListController.prototype._donotForceDeleteDomainsCallback, this);
    this._forceRemoveMessageDialog._button[DwtDialog.YES_BUTTON].setText(ZaMsg.FORCE_DELETE_BUTTON);

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
            var item = ev.item ;
            if (item.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.local) {
                ZaApp.getInstance().getDomainController().show(item);
            } else if ( item.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.alias) {
                ZaApp.getInstance().getDomainAliasWizard(true).editDomainAlias (item, true) ;
            }
            var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_domains]);
            ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, item.name, null, false, false, item, undefined, true);
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
        this._editItem(item);
	}
}

ZaDomainListController.prototype._editItem =
function(item) {
    if (item.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.local) {
        ZaApp.getInstance().getDomainController().show(item);
    } else if ( item.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.alias) {
        ZaApp.getInstance().getDomainAliasWizard(true).editDomainAlias (item, true) ;
    }
    var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_domains]);
    ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, item.name, null, false, false, item, undefined, true);
}

ZaDomainListController.prototype._addDomainAliasListener =
function (ev) {
    var domain = new ZaDomain () ;
    if(this._contentView.getSelectionCount() == 1) {
             var item = this._contentView.getSelection()[0];
             domain[ZaDomain.A2_zimbraDomainAliasTarget] = item;
    }
    var domainAliasWizard = ZaApp.getInstance().getDomainAliasWizard () ;
    domainAliasWizard.registerCallback(DwtDialog.OK_BUTTON,
            ZaDomain.prototype.createDomainAlias, domain,
            domainAliasWizard._localXForm);
    domainAliasWizard.setObject(domain);
    domainAliasWizard.popup();
}

// new button was pressed
ZaDomainListController.prototype._newButtonListener =
function(ev) {
	try {
		var domain = new ZaDomain();
			
		domain.getAttrs = {all:true};
		/*domain.setAttrs = {all:true};
		domain.rights = {};
		domain._defaultValues = {attrs:{}};*/
		domain.loadNewObjectDefaults("name","domain.tld");
        if(!ZaApp.getInstance().dialogs["ZaNewDomainXWizard"])
		    ZaApp.getInstance().dialogs["ZaNewDomainXWizard"] = new ZaNewDomainXWizard(this._container, domain);
        this._newDomainWizard = ZaApp.getInstance().dialogs["ZaNewDomainXWizard"];
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
			this._openConfigGAL(item);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._showGalWizard", null, false);
	}
}

ZaDomainListController.prototype._openConfigGAL =
function(item) {
	this._currentObject = item;
    item.load("name", item.attrs[ZaDomain.A_domainName],false,true);

    ZaApp.getInstance().dialogs["ZaGALConfigXWizard"] = new ZaGALConfigXWizard(this._container,item)
    this._galWizard = ZaApp.getInstance().dialogs["ZaGALConfigXWizard"];
    item._extid=ZaUtil.getItemUUid();
    item._editObject = item;

    this._galWizard.setObject(item);
    this._galWizard.popup();
}

ZaDomainListController.prototype._authWizButtonListener =
function(ev) {
	try {
		if(this._contentView.getSelectionCount() == 1) {
			var item = this._contentView.getSelection()[0];
			this._openAuthWiz(item);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._showAuthWizard", null, false);
	}
}

ZaDomainListController.prototype._openAuthWiz =
function (item) {
    this._currentObject = item;
    item.load("name", item.attrs[ZaDomain.A_domainName],false,true);
    this._authWizard = ZaApp.getInstance().dialogs["ZaTaskAuthConfigWizard"] = new ZaTaskAuthConfigWizard(this._container);
    item._extid=ZaUtil.getItemUUid();
    item._editObject = item;

    this._authWizard.setObject(item);
    this._authWizard.popup();
}

ZaDomainListController.prototype._autoProvWizButtonListener =
function(ev) {
	try {
		if(this._contentView.getSelectionCount() == 1) {
			var item = this._contentView.getSelection()[0];
			this._currentObject = item;
			item.load("name", item.attrs[ZaDomain.A_domainName],false,true);
            if(!this._autoProvWizard) {
                if(ZaApp.getInstance().dialogs["ZaTaskAutoProvDialog"])
                     this._autoProvWizard = ZaApp.getInstance().dialogs["ZaTaskAutoProvDialog"];
                else
			        this._autoProvWizard = ZaApp.getInstance().dialogs["ZaTaskAutoProvDialog"] = new ZaTaskAutoProvDialog(this._container, ZaMsg.NAD_AutoProvConfigTitle);//ZaAutoProvConfigXWizard(this._container);
            }
            item._extid=ZaUtil.getItemUUid();
            item._editObject = item;
            this._autoProvWizard.registerCallback(DwtDialog.OK_BUTTON, ZaTaskAutoProvDialog.prototype.finishWizard, this._autoProvWizard, null);

            item.currentTab = "1";
			this._autoProvWizard.setObject(item);
			this._autoProvWizard.popup();
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._autoProvWizButtonListener", null, false);
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
				//detect whether the deleting item is open in a tab
				if (ZaApp.getInstance().getTabGroup() && ZaApp.getInstance().getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);
				}
			}
		}
	}
	
	if (this._itemsInTabList.length > 0) {
		if(!ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
			ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], 
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC],
						ZaId.CTR_PREFIX + ZaId.VIEW_DMLIST + "_ConfirmDeleteItemsInTab");			
		}
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; ;
		msg += ZaDomainListController.getDlMsgFromList (this._itemsInTabList) ;
		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaDomainListController.prototype._closeTabsBeforeRemove, this);
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaDomainListController.prototype._deleteDomainInRemoveList, this);		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	}else{
		this._deleteDomainInRemoveList ();
	}
}

ZaDomainListController.prototype.viewAccountsButtonListener  =
function (ev) {
    if(this._contentView.getSelectionCount() == 1) {
        var item = this._contentView.getSelection()[0];
        var domainName = item.name ;
        ZaDomain.searchAccountsInDomain (domainName) ;
	}
}

ZaDomainListController.prototype._closeTabsBeforeRemove =
function () {
	this.closeTabsInRemoveList() ;
	this._deleteDomainInRemoveList();
}

ZaDomainListController.prototype._deleteDomainInRemoveList =
function () {
	if (ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popdown();
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
    var i = 0;
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
    if(!this._successRemList)
	    this._successRemList=new Array();
    for(var key in this._removeList) {
        if(this._removeList[key] && AjxUtil.indexOf(this._successRemList, this._removeList[key]) == -1) {
            try {
                this._removeList[key].remove();
                this._successRemList.push(this._removeList[key]);
            } catch (ex) {
                this._removeConfirmMessageDialog.popdown();
                if(ex.code == ZmCsfeException.DOMAIN_NOT_EMPTY) {
                    this._forceDeleteDomain(this._removeList[key]);
                    //this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_NOT_EMPTY, null, DwtMessageDialog.CRITICAL_STYLE, null);
                    //this._errorDialog.popup();
                } else {
                    this._handleException(ex, "ZaDomainListController.prototype._deleteDomainsCallback", null, false);
                }
                return;
            }
            this._list.remove(this._removeList[key]); //remove from the list
        }
    }
	this.fireRemovalEvent(this._successRemList);
    this._successRemList = null;
	this._removeConfirmMessageDialog.popdown();
	this._contentView.setUI();
	this.show();
}

ZaDomainListController.prototype._donotDeleteDomainsCallback = 
function () {
	this._removeList = new Array();
	this._removeConfirmMessageDialog.popdown();
}

ZaDomainListController.prototype._getAllAccountDomain =
function (domainName) {
    if (domainName) {
        var controller = ZaApp.getInstance().getCurrentController();
        var busyId = Dwt.getNextId();

        controller._currentQuery = "" ;
        var searchTypes = [ZaSearch.ACCOUNTS, ZaSearch.DLS, ZaSearch.ALIASES, ZaSearch.RESOURCES] ;

        if(controller.setSearchTypes)
            controller.setSearchTypes(searchTypes);
	    controller._currentDomain = domainName;
	    controller.fetchAttrs = AjxBuffer.concat(ZaAlias.searchAttributes,",",
                        ZaDistributionList.searchAttributes,",",
                        ZaResource.searchAttributes,",",
                        ZaSearch.standardAttributes);

        var searchParams = {
            query:controller._currentQuery,
            domain: controller._currentDomain,
            types:searchTypes,
            attrs:controller.fetchAttrs,
            controller: controller,
            showBusy:true,
            busyId:busyId,
            busyMsg:ZaMsg.BUSY_SEARCHING
        }
        var resp = ZaSearch.searchDirectory(searchParams);
        if(resp && resp.Body.SearchDirectoryResponse) {
            var response = resp.Body.SearchDirectoryResponse;
            var acctlist = new ZaItemList(ZaAccount);
            acctlist.loadFromJS(response);
            return acctlist.getArray();
        } else return null;
    }else {
        var currentController = ZaApp.getInstance().getCurrentController () ;
        currentController.popupErrorDialog(ZaMsg.ERROR_NO_DOMAIN_NAME) ;
    }
    return null;
}

ZaDomainListController.prototype._forceDeleteDomain =
function (domain) {
    var acctlist = this._getAllAccountDomain(domain.name);
    if(acctlist && acctlist.length > 0) {
        var sysacctNum = 0;
        var regularacctNum = 0;
        for (var i = 0; i < acctlist.length; i++) {
            if(acctlist[i].attrs[ZaAccount.A_zimbraIsSystemAccount] == "TRUE")
                sysacctNum ++;
            else regularacctNum++;
        }
        this._forceRemoveAccountList = acctlist;
        this._forceRemoveDomain = domain;
        var dlgMsg = AjxMessageFormat.format(ZaMsg.Q_FORCE_DELETE_DOMAIN, [domain.name, sysacctNum, regularacctNum]);
        dlgMsg += ZaDomainListController.getDlMsgFromList (this._forceRemoveAccountList);
        this._forceRemoveMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
        this._forceRemoveMessageDialog.popup();
    }
}

ZaDomainListController.prototype._forceDeleteDomainCallback =
function() {
    this._forceRemoveMessageDialog.popdown();
    var acctList = this._forceRemoveAccountList;
    var dom = this._forceRemoveDomain;
    if(!acctList || !dom) return;
    try {
        for(var i = 0; i < acctList.length; i++) {
            acctList[i].remove();
        }
        this._forceRemoveAccountList = null;
        this._deleteDomainsCallback();
    } catch (ex) {
        this._handleException(ex, "ZaDomainListController.prototype._forceDeleteDomainCallback", null, false);
    }
}

ZaDomainListController.prototype._donotForceDeleteDomainsCallback =
function () {
    this._forceRemoveMessageDialog.popdown();
    if(this._forceRemoveDomain) {
        AjxUtil.arrayRemove(this._removeList,this._forceRemoveDomain);
        this._deleteDomainsCallback();
    }
}

ZaDomainListController.changeActionsStateMethod = 
function () {
	if(!this._contentView)
		return;
		
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var item = this._contentView.getSelection()[0];
		if(item) {
			if(item.attrs[ZaDomain.A_domainType] == "alias"){
				
				if(this._popupOperations[ZaOperation.ADD_DOMAIN_ALIAS])
                                        this._popupOperations[ZaOperation.ADD_DOMAIN_ALIAS].enabled=false;


				if(this._popupOperations[ZaOperation.AUTOPROV_WIZARD])
                                        this._popupOperations[ZaOperation.AUTOPROV_WIZARD].enabled=false;
			}
			
			if(item.attrs[ZaDomain.A_zimbraDomainStatus] == ZaDomain.DOMAIN_STATUS_SHUTDOWN) {
					
				if(this._popupOperations[ZaOperation.EDIT])
					this._popupOperations[ZaOperation.EDIT].enabled=false;
			
				if(this._popupOperations[ZaOperation.AUTH_WIZARD])
					this._popupOperations[ZaOperation.AUTH_WIZARD].enabled=false;

				if(this._popupOperations[ZaOperation.AUTOPROV_WIZARD])
					this._popupOperations[ZaOperation.AUTOPROV_WIZARD].enabled=false;

				if(this._popupOperations[ZaOperation.GAL_WIZARD])
					this._popupOperations[ZaOperation.GAL_WIZARD].enabled=false;

                if (this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS])
                    this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS].enabled=false;                    
            } else {
				if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				
				if(!(ZaDomain.canConfigureGal(item))) {
					
					if(this._popupOperations[ZaOperation.GAL_WIZARD])
						this._popupOperations[ZaOperation.GAL_WIZARD].enabled=false;						
				}
		
				if(!(ZaDomain.canConfigureAuth(item))) {
			
					if(this._popupOperations[ZaOperation.AUTH_WIZARD])
						this._popupOperations[ZaOperation.AUTH_WIZARD].enabled=false;
				}

				if(!(ZaDomain.canConfigureAutoProv(item))) {

					if(this._popupOperations[ZaOperation.AUTOPROV_WIZARD])
						this._popupOperations[ZaOperation.AUTOPROV_WIZARD].enabled=false;
				}

				if(!item.rights[ZaDomain.RIGHT_DELETE_DOMAIN]) {
					
					if(this._popupOperations[ZaOperation.DELETE]) {
						this._popupOperations[ZaOperation.DELETE].enabled=false;
					}
				}				
            }
		}
	} else if (cnt > 1){
		if(this._popupOperations[ZaOperation.AUTH_WIZARD])
			this._popupOperations[ZaOperation.AUTH_WIZARD].enabled=false;

		if(this._popupOperations[ZaOperation.AUTOPROV_WIZARD])
			this._popupOperations[ZaOperation.AUTOPROV_WIZARD].enabled=false;

		if(this._popupOperations[ZaOperation.GAL_WIZARD])
			this._popupOperations[ZaOperation.GAL_WIZARD].enabled=false;
		
		if(this._popupOperations[ZaOperation.EDIT])
			this._popupOperations[ZaOperation.EDIT].enabled=false;

        if (this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS])
            this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS].enabled=false;    
    } else {
			
		if(this._popupOperations[ZaOperation.EDIT])
			this._popupOperations[ZaOperation.EDIT].enabled=false;
		
		if(this._popupOperations[ZaOperation.DELETE])
			this._popupOperations[ZaOperation.DELETE].enabled=false;
		
		if(this._popupOperations[ZaOperation.AUTH_WIZARD])
			this._popupOperations[ZaOperation.AUTH_WIZARD].enabled=false;

		if(this._popupOperations[ZaOperation.AUTOPROV_WIZARD])
			this._popupOperations[ZaOperation.AUTOPROV_WIZARD].enabled=false;
					
		if(this._popupOperations[ZaOperation.GAL_WIZARD])
			this._popupOperations[ZaOperation.GAL_WIZARD].enabled=false;

        if (this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS])
            this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS].enabled=false;    
    }
}
ZaController.changeActionsStateMethods["ZaDomainListController"].push(ZaDomainListController.changeActionsStateMethod);

ZaDomainListController.prototype._finishNewButtonListener =
function(ev) {
	try {
		var obj = this._newDomainWizard.getObject();
		var domain = ZaItem.create(obj,ZaDomain,"ZaDomain");
		if(domain != null) {
			this._newDomainWizard.popdown();
			//if creation took place - fire an DomainChangeEvent
			this._fireDomainCreationEvent(domain);
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_CREATE, this);
			evt.setDetails(domain);
			this.handleCreation(evt);
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DomainCreated,[domain.name]));
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

ZaDomainListController.prototype.notifyAllOpenTabs =
function(editObject) {
    this._currentObject = editObject;
    this._notifyAllOpenTabs();
}

ZaDomainListController.prototype._notifyAllOpenTabs =
function() {
        var warningMsg = "<br><ul>";
        var hasItem = false;
        for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
                var tab = ZaAppTabGroup._TABS.get(i) ;
                var v = tab.getAppView() ;
                if (v && v._containedObject && v._containedObject.name) {
                        var acctName = v._containedObject.name;
                        var l = acctName.indexOf('@');
                        var domain = null;
                        if(l > 0) domain = acctName.substring(l+1);
                        if((domain != null && domain == this._currentObject.attrs[ZaDomain.A_domainName])
				|| (domain == null && acctName == this._currentObject.attrs[ZaDomain.A_domainName]))
			{
                                warningMsg += "<li>" + acctName + "</li>";
                                hasItem = true;
                        }
                }
        }
        warningMsg += "</ul></br>";
        if(hasItem)
                ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.WARN_CHANGE_AUTH_METH + warningMsg);

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
		this._notifyAllOpenTabs();
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._finishAuthButtonListener", null, false);
	}
	return;
}

ZaDomainListController.prototype._finishAutoProvButtonListener =
function(ev) {
	try {


        if(!this._autoProvWizard._checkGeneralConfig() || !this._autoProvWizard._checkEagerConfig()
                || !this._autoProvWizard._checkLazyConfig()) {
            return;
        }
        var savedObj = this._autoProvWizard.getObject();
        this._autoProvWizard._combineConfigureValues(savedObj);
		ZaDomain.modifyAutoPovSettings.call(this._currentObject,savedObj);
		this._fireDomainChangeEvent(this._currentObject);
		this._autoProvWizard.popdown();
		this._notifyAllOpenTabs();
	} catch (ex) {
		this._handleException(ex, "ZaDomainListController.prototype._finishAutoProvButtonListener", null, false);
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
