/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @constructor
* @class ZaDomainListController
* This is a singleton object that controls all the user interaction with the list of ZaDomain objects
**/
function ZaDomainListController(appCtxt, container, app) {
	ZaListViewController.call(this, appCtxt, container, app,"ZaDomainListController");
	this._helpURL = ZaDomainListController.helpURL;	
	this._currentQuery = new ZaSearchQuery("", [ZaSearch.DOMAINS], false, "");
	this._currentSortField = ZaDomain.A_domainName;
	this.objType = ZaEvent.S_DOMAIN;	
	this.RESULTSPERPAGE = ZaDomain.RESULTSPERPAGE; 
	this.MAXSEARCHRESULTS = ZaDomain.MAXSEARCHRESULTS;	
}

ZaDomainListController.prototype = new ZaListViewController();
ZaDomainListController.prototype.constructor = ZaDomainListController;
ZaDomainListController.helpURL =  "/zimbraAdmin/adminhelp/html/WebHelp/managing_domains/managing_domains.htm";
ZaController.initToolbarMethods["ZaDomainListController"] = new Array();
ZaController.initPopupMenuMethods["ZaDomainListController"] = new Array();


ZaDomainListController.prototype.show = function (doPush) {
	var callback = new AjxCallback(this, this.searchCallback, {limit:ZaDomain.RESULTSPERPAGE,CONS:ZaDomain,show:doPush});
	var searchParams = {
			query:this._currentQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:this.RESULTSPERPAGE*(this._currentPageNum-1),
			sortAscending:"1",
			limit:this.RESULTSPERPAGE,
			callback:callback
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaDomainListController.prototype._show = 
function (list, openInNewTab) {
	this._updateUI(list);
	this._app.pushView(ZaZimbraAdmin._DOMAINS_LIST_VIEW);
	if (openInNewTab) {//when a ctrl shortcut is pressed
		
	}else{ //open in the main tab
		this.updateMainTab ("Domain") ;	
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
function () {
	this._contentView = new ZaDomainListView(this._container);
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
	this._app.createView(ZaZimbraAdmin._DOMAINS_LIST_VIEW, elements);

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
		this._changeActionsState();	
	}
}

ZaDomainListController.prototype._listActionListener =
function (ev) {
	this._changeActionsState();
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
	if(this._contentView.getSelectionCount()>0) {
		var arrItems = this._contentView.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			if(arrItems[key]) {
				this._removeList.push(arrItems[key]);
			}
		}
	}
	if(this._removeList.length) {
		dlgMsg = ZaMsg.Q_DELETE_DOMAINS;
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
		dlgMsg += "</ul>";
		this._removeConfirmMessageDialog.setMessage(dlgMsg, DwtMessageDialog.INFO_STYLE);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainListController.prototype._deleteDomainsCallback, this);
		this._removeConfirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainListController.prototype._donotDeleteDomainsCallback, this);		
		this._removeConfirmMessageDialog.popup();
	}
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

ZaDomainListController.prototype._changeActionsState = 
function () {
	var cnt = this._contentView.getSelectionCount();
	if(cnt == 1) {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.AUTH_WIZARD, ZaOperation.GAL_WIZARD];
		this._toolbar.enable(opsArray, true);
		this._actionMenu.enable(opsArray, true);
	} else if (cnt > 1){
		var opsArray1 = [ZaOperation.EDIT, ZaOperation.AUTH_WIZARD, ZaOperation.GAL_WIZARD];
		this._toolbar.enable(opsArray1, false);
		this._actionMenu.enable(opsArray1, false);

		var opsArray2 = [ZaOperation.DELETE];
		this._toolbar.enable(opsArray2, true);
		this._actionMenu.enable(opsArray2, true);
	} else {
		var opsArray = [ZaOperation.EDIT, ZaOperation.DELETE, ZaOperation.AUTH_WIZARD, ZaOperation.GAL_WIZARD];
		this._toolbar.enable(opsArray, false);
		this._actionMenu.enable(opsArray, false);
	}
}

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
				ZaDomain.initNotebook(this._newDomainWizard.getObject(),callback) ;
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
		ZaDomain.modifyAuthSettings(this._authWizard.getObject(), this._currentObject);
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
		ZaDomain.modifyGalSettings(this._galWizard.getObject(),this._currentObject); 
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