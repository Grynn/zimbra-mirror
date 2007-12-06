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
* @class ZaDomainController controls display of a single Domain
* @contructor ZaDomainController
* @param appCtxt
* @param container
* @param abApp
**/

ZaDomainController = function(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container, app, "ZaDomainController");
	this._UICreated = false;
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_domains/managing_domains.htm";	
	this._toolbarOperations = new Array();			
	this.deleteMsg = ZaMsg.Q_DELETE_DOMAIN;	
	this.objType = ZaEvent.S_DOMAIN;
	this.tabConstructor = ZaDomainXFormView;				
}

ZaDomainController.prototype = new ZaXFormViewController();
ZaDomainController.prototype.constructor = ZaDomainController;

ZaController.initToolbarMethods["ZaDomainController"] = new Array();
ZaController.setViewMethods["ZaDomainController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaDomain class
*/

ZaDomainController.prototype.show = 
function(entry) {
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
	}
}

/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaDomainController.initToolbarMethod = 
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.DTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.DTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, this._newButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.GAL_WIZARD, ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainController.prototype._galWizButtonListener)));   		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.AUTH_WIZARD, ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainController.prototype._authWizButtonListener)));   		   		
	this._toolbarOperations.push(new ZaOperation(ZaOperation.INIT_NOTEBOOK, ZaMsg.DTBB_InitNotebook, ZaMsg.DTBB_InitNotebook_tt, "NewNotebook", "NewNotebookDis", new AjxListener(this, ZaDomainController.prototype._initNotebookButtonListener)));   		   		   		

}
ZaController.initToolbarMethods["ZaDomainController"].push(ZaDomainController.initToolbarMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaDomainController.setViewMethod =
function(entry) {
	entry.load("name", entry.attrs[ZaDomain.A_domainName]);
	if(!this._UICreated) {
		this._createUI();
	} 
	this._app.pushView(this.getContentViewId());
	this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);  		

	if(entry.attrs[ZaDomain.A_zimbraDomainStatus] == ZaDomain.DOMAIN_STATUS_SHUTDOWN) {
		this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);
		this._toolbar.getButton(ZaOperation.GAL_WIZARD).setEnabled(false);
		this._toolbar.getButton(ZaOperation.AUTH_WIZARD).setEnabled(false);		
		this._toolbar.getButton(ZaOperation.INIT_NOTEBOOK).setEnabled(false);
	} else {
		if(!entry.id) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
		}			
		this._toolbar.getButton(ZaOperation.GAL_WIZARD).setEnabled(true);
		this._toolbar.getButton(ZaOperation.AUTH_WIZARD).setEnabled(true);		
		
		if(entry.attrs[ZaDomain.A_zimbraNotebookAccount])
			this._toolbar.getButton(ZaOperation.INIT_NOTEBOOK).setEnabled(false);
		else
			this._toolbar.getButton(ZaOperation.INIT_NOTEBOOK).setEnabled(true);
	}
	this._view.setDirty(false);
	
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaDomainController"].push(ZaDomainController.setViewMethod);

/**
* @method _createUI
**/
ZaDomainController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container, this._app);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	
    var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}  		
    this._app.createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	this._app._controllers[this.getContentViewId ()] = this ;
}

ZaDomainController.prototype.saveButtonListener =
function(ev) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);
			if(this._toolbar)
				this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);		
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype.saveButtonListener", null, false);
	}
	return;
}

ZaDomainController.prototype._saveChanges = 
function () {
	var tmpObj = this._view.getObject();
	var mods = new Object();
	var haveSmth = false; //what is this variable for?
	var renameNotebookAccount = false;
	for(var a in tmpObj.attrs) {
		/** ???? This block of code basically will not reflect the change of the array attributes, such as virtualhost 
		if(tmpObj.attrs[a]!=null && tmpObj.attrs[a] instanceof Array)
			continue;
		**/
		
		if(a == ZaItem.A_zimbraId || a==ZaDomain.A_domainName)
			continue;
		
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(tmpObj.attrs[a] instanceof Array) {
					if(this._currentObject.attrs[a] && tmpObj.attrs[a] 
						&& tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
						mods[a] = tmpObj.attrs[a];
						haveSmth = true;
					}	
			}else if(tmpObj.attrs[a] != this._currentObject.attrs[a]) {
				mods[a] = tmpObj.attrs[a];
				haveSmth = true;
			}
		}
	}
	/*
	if(tmpObj.attrs[ZaDomain.A_notes] != this._currentObject.attrs[ZaDomain.A_notes]) {
		mods[ZaDomain.A_notes] = tmpObj.attrs[ZaDomain.A_notes] ;
		haveSmth = true;
	}
	if(tmpObj.attrs[ZaDomain.A_description] != this._currentObject.attrs[ZaDomain.A_description]) {
		mods[ZaDomain.A_description] = tmpObj.attrs[ZaDomain.A_description] ;
		haveSmth = true;
	}
	if(tmpObj.attrs[ZaDomain.A_domainDefaultCOSId] != this._currentObject.attrs[ZaDomain.A_domainDefaultCOSId]) {
		mods[ZaDomain.A_domainDefaultCOSId] = tmpObj.attrs[ZaDomain.A_domainDefaultCOSId] ;
		haveSmth = true;
	}	

	if(tmpObj.attrs[ZaDomain.A_zimbraPublicServiceHostname] != this._currentObject.attrs[ZaDomain.A_zimbraPublicServiceHostname]) {
		mods[ZaDomain.A_zimbraPublicServiceHostname] = tmpObj.attrs[ZaDomain.A_zimbraPublicServiceHostname] ;
		haveSmth = true;
	}	
	
	if(tmpObj.attrs[ZaDomain.A_domainMaxAccounts] != this._currentObject.attrs[ZaDomain.A_domainMaxAccounts]) {
		mods[ZaDomain.A_domainMaxAccounts] = tmpObj.attrs[ZaDomain.A_domainMaxAccounts] ;
		haveSmth = true;
	}
	
	if(tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname].join(",").valueOf() !=  this._currentObject.attrs[ZaDomain.A_zimbraVirtualHostname].join(",").valueOf()) {
		mods[ZaDomain.A_zimbraVirtualHostname] = tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname] ;
		haveSmth = true;		
	}
	if(tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount] != this._currentObject.attrs[ZaDomain.A_zimbraNotebookAccount]) {
		mods[ZaDomain.A_zimbraNotebookAccount] = tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount] ;
		haveSmth = true;
		renameNotebookAccount = true;
	}	
	
	if(tmpObj.attrs[ZaDomain.A_zimbraDomainStatus] != this._currentObject.attrs[ZaDomain.A_zimbraDomainStatus]) {
		mods[ZaDomain.A_zimbraDomainStatus] = tmpObj.attrs[ZaDomain.A_zimbraDomainStatus] ;
		haveSmth = true;
	}
	*/
	var writeACLs = false;	
	//var changeStatus = false;	
	var permsToRevoke = [];
	//check if any notebook permissions are changed
	if(tmpObj[ZaDomain.A_allNotebookACLS]._version > 0) {
		writeACLs = true;	
		var cnt = this._currentObject[ZaDomain.A_allNotebookACLS].length;
		for(var i = 0; i < cnt; i++) {
			if(this._currentObject[ZaDomain.A_allNotebookACLS][i].gt == ZaDomain.A_NotebookUserACLs ||
				this._currentObject[ZaDomain.A_allNotebookACLS][i].gt == ZaDomain.A_NotebookGroupACLs ||
				this._currentObject[ZaDomain.A_allNotebookACLS][i].gt == ZaDomain.A_NotebookDomainACLs)	{
				var cnt2 = tmpObj[ZaDomain.A_allNotebookACLS].length;
				var foundUser = false;
				for(var j = 0; j < cnt2; j++) {
					if(tmpObj[ZaDomain.A_allNotebookACLS][j].name == this._currentObject[ZaDomain.A_allNotebookACLS][i].name) {
						foundUser = true;
						break;
					}
				}
				if(!foundUser && this._currentObject[ZaDomain.A_allNotebookACLS][i].zid) {
					permsToRevoke.push(this._currentObject[ZaDomain.A_allNotebookACLS][i].zid);
				}
			}
		
		}
	}
/*	if(tmpObj.attrs[ZaDomain.A_zimbraDomainStatus] != this._currentObject.attrs[ZaDomain.A_zimbraDomainStatus]) {
		changeStatus = true;
	}	*/	
	if(haveSmth || writeACLs) {
		try { 
			var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
			soapDoc.setMethodAttribute("onerror", "stop");		
			if(renameNotebookAccount) {
				var account = new ZaAccount(this._app);
				account.load(ZaAccount.A_name,this._currentObject.attrs[ZaDomain.A_zimbraNotebookAccount]);
				account.rename(tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount]);
			}
			if(haveSmth) {
				var modifyDomainRequest = soapDoc.set("ModifyDomainRequest", null, null, "urn:zimbraAdmin");
//				modifyDomainRequest.setAttribute("xmlns", "urn:zimbraAdmin");
			
				soapDoc.set("id", this._currentObject.id,modifyDomainRequest);
				for (var aname in mods) {
					//multy value attribute
					if(mods[aname] instanceof Array) {
						var cnt = mods[aname].length;
						if(cnt) {
							for(var ix=0; ix <cnt; ix++) {
								if(mods[aname][ix]) { //if there is an empty element in the array - don't send it
									var attr = soapDoc.set("a", mods[aname][ix],modifyDomainRequest);
									attr.setAttribute("n", aname);
								}
							}
						} else {
							var attr = soapDoc.set("a", "",modifyDomainRequest);
							attr.setAttribute("n", aname);
						}
					} else {		
						var attr = soapDoc.set("a", mods[aname],modifyDomainRequest);
						attr.setAttribute("n", aname);
					}
				}
	
			}
	
			var command = new ZmCsfeCommand();
			var params = new Object();
			
			if(writeACLs) {
				if(permsToRevoke.length>0) {
					ZaDomain.getRevokeACLsrequest(permsToRevoke, soapDoc);
				}
				params.accountName = tmpObj[ZaDomain.A_NotebookAccountName] ? tmpObj[ZaDomain.A_NotebookAccountName] : tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount];					
				ZaDomain.getNotebookACLsRequest	(tmpObj,soapDoc);
				
			}
	
			/*if(changeStatus) {
				var modifyDomainStatusRequest = soapDoc.set("ModifyDomainStatusRequest", null, null, "urn:zimbraAdmin"); 
//				modifyDomainStatusRequest.setAttribute("xmlns", "urn:zimbraAdmin");
				soapDoc.set("id", this._currentObject.id,modifyDomainStatusRequest);
				soapDoc.set("status", tmpObj.attrs[ZaDomain.A_zimbraDomainStatus],modifyDomainStatusRequest);
			}*/
			params.soapDoc = soapDoc;	
			var callback = new AjxCallback(this, this.saveChangesCallback);	
			params.asyncMode = true;
			params.callback = callback;			
			var reqMgrParams = {
				controller : this._app.getCurrentController(),
				busyMsg : ZaMsg.BUSY_MODIFY_DOMAIN
			}	
			ZaRequestMgr.invoke(params, reqMgrParams);			
			//command.invoke(params);
			return true;
		} catch (ex) {
			this._handleException(ex,"ZaDomainController.prototype._saveChanges");
		}
	}
}



ZaDomainController.prototype.newDomain = 
function () {
	this._currentObject = new ZaDomain();
	this._showNewDomainWizard();
}

ZaDomainController.prototype._showNewDomainWizard = 
function () {
	try {
		this._newDomainWizard = this._app.dialogs["newDomainWizard"] = new ZaNewDomainXWizard(this._container, this._app);	
		this._newDomainWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishNewButtonListener, this, null);			
		this._newDomainWizard.setObject(this._currentObject);
		this._newDomainWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showNewDomainWizard", null, false);
	}
}

// new button was pressed
ZaDomainController.prototype._newButtonListener =
function(ev) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app.getDomainController();
		args["func"] = ZaDomainController.prototype.newDomain;
		//ask if the user wants to save changes		
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		this.newDomain();
	}	
}


ZaDomainController.prototype._galWizButtonListener =
function(ev) {
	try {
		this._galWizard = this._app.dialogs["galWizard"] = new ZaGALConfigXWizard(this._container, this._app);	
		this._galWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishGalButtonListener, this, null);			
		this._galWizard.setObject(this._currentObject);
		this._galWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showGalWizard", null, false);
	}
}


ZaDomainController.prototype._authWizButtonListener =
function(ev) {
	try {
		this._authWizard = this._app.dialogs["authWizard"] =  new ZaAuthConfigXWizard(this._container, this._app);	
		this._authWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishAuthButtonListener, this, null);			
		this._authWizard.setObject(this._currentObject);
		this._authWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showAuthWizard", null, false);
	}
}

ZaDomainController.prototype._finishGalButtonListener =
function(ev) {
	try {
		//var changeDetails = new Object();
		ZaDomain.modifyGalSettings.call(this._currentObject, this._galWizard.getObject()); 
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
		this.fireChangeEvent(this._currentObject);
		this._view.setObject(this._currentObject);		
		this._galWizard.popdown();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._finishGalButtonListener", null, false);
	}
	return;
}

ZaDomainController.prototype._finishAuthButtonListener =
function(ev) {
	try {
		ZaDomain.modifyAuthSettings.call(this._currentObject,this._authWizard.getObject());
		//var changeDetails = new Object();
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
	
		this.fireChangeEvent(this._currentObject);
		this._view.setObject(this._currentObject);
		this._authWizard.popdown();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._finishAuthButtonListener", null, false);
	}
	return;
}

/**
* @param 	ev event object
* This method handles "finish" button click in "New Domain" dialog
**/

ZaDomainController.prototype._finishNewButtonListener =
function(ev) {
	try {
		var domain = ZaDomain.create(this._newDomainWizard.getObject(), this._app);
		if(domain != null) {
			//if creation took place - fire an DomainChangeEvent
			this.fireCreationEvent(domain);
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);	
			this._newDomainWizard.popdown();		
			if(this._newDomainWizard.getObject()[ZaDomain.A_CreateNotebook]=="TRUE") {
				var params = new Object();
			//	if(this._newDomainWizard.getObject()[ZaDomain.A_OverwriteNotebookACLs]) {
					params[ZaDomain.A_OverwriteNotebookACLs] = true;
					params.obj = this._newDomainWizard.getObject();
			//	} else
					params[ZaDomain.A_OverwriteNotebookACLs] = false;
					
				var callback = new AjxCallback(this, this.initNotebookCallback, params);				
				ZaDomain.initNotebook(this._newDomainWizard.getObject(),callback, this) ;
			}
		}
	} catch (ex) {
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_EXISTS, ex);		
		} else {
			this._handleException(ex, "ZaDomainController.prototype._finishNewButtonListener", null, false);
		}
	}
	return;
}

ZaDomainController.prototype.initNotebookCallback = 
function (params, resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		this._handleException(resp.getException(), "ZaDomainController.prototype._initNotebookCallback", null, false);
		return;
	} 
//	if(params[ZaDomain.A_OverwriteNotebookACLs] && params.obj!=null) {
		var callback = new AjxCallback(this, this.setNotebookAclsCallback);				
		ZaDomain.setNotebookACLs(params.obj, callback) ;
//	}	
	this._currentObject.refresh();
	this.show(this._currentObject);
}

ZaDomainController.prototype.setNotebookAclsCallback = 
function (resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		this._handleException(resp.getException(), "ZaDomainController.prototype.setNotebookAclsCallback", null, false);
		return;
	} 
}

ZaDomainController.prototype.saveChangesCallback = 
function (resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		this._handleException(resp.getException(), "ZaDomainController.prototype.setNotebookAclsCallback", null, false);
		return;
	} 
	var response;
	if(resp.getResponse)
		response = resp.getResponse().Body.BatchResponse;
	else
		response = resp.Body.BatchResponse;
	
	if(response.Fault) {
		for(var ix in response.Fault) {
			var ex = ZmCsfeCommand.faultToEx(response.Fault[ix], "ZaDomainController.prototype.saveChangesCallback");
			this._handleException(ex, "ZaDomainController.prototype.saveChangesCallback", null, false);
		}
	}
	/*if(response.ModifyDomainResponse && response.ModifyDomainResponse.domain && response.ModifyDomainResponse.domain[0]) {
		this._currentObject.initFromJS(response.ModifyDomainResponse.domain[0]);
	}*/

	this._currentObject.refresh(false);	
	this._view.setObject(this._currentObject);			
	this.fireChangeEvent(this._currentObject);			
}

ZaDomainController.prototype._finishDomainNotebookListener =
function(ev) {
	try {
		var obj = this._initDomainNotebookWiz.getObject();
		if(obj[ZaDomain.A_NotebookAccountPassword] != obj[ZaDomain.A_NotebookAccountPassword2]) {
			this.popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return;
		}
		this._initDomainNotebookWiz.popdown();
		var params = new Object();
		params.obj = obj;
			
		var callback = new AjxCallback(this, this.initNotebookCallback, params);
		ZaDomain.initNotebook(this._initDomainNotebookWiz.getObject(),callback, this) ;
	} catch (ex) {
		this._initDomainNotebookWiz.popdown();
		this._handleException(ex, "ZaDomainController.prototype._finishDomainNotebookListener", null, false);
	}
	return;
}

ZaDomainController.prototype._initNotebookButtonListener = 
function (ev) {
	try {
		this._initDomainNotebookWiz = this._app.dialogs["initDomainNotebookWiz"] = new ZaDomainNotebookXWizard(this._container, this._app);	
		this._initDomainNotebookWiz.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishDomainNotebookListener, this, null);			
		this._initDomainNotebookWiz.setObject(this._currentObject);
		this._initDomainNotebookWiz.popup();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._initNotebookButtonListener", null, false);
	}	
}

ZaDomainController.prototype._handleException = 
function (ex, method, params, restartOnError, obj) {
	if(ex.code == ZmCsfeException.DOMAIN_NOT_EMPTY) {
		this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_NOT_EMPTY, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();			
	} else if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
		this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_EXISTS, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup(this._getDialogXY());
	} else {
		ZaController.prototype._handleException.call(this, ex, method, params, restartOnError, obj);				
	}	
}
