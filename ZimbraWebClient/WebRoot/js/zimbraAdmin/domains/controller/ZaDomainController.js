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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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

function ZaDomainController(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container, app, "ZaDomainController");
	this._UICreated = false;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_domains/managing_domains.htm";			
	this.deleteMsg = ZaMsg.Q_DELETE_DOMAIN;	
	this.objType = ZaEvent.S_DOMAIN;	
}

ZaDomainController.prototype = new ZaXFormViewController();
ZaDomainController.prototype.constructor = ZaDomainController;


/**
*	@method show
*	@param entry - isntance of ZaDomain class
*/

ZaDomainController.prototype.show = 
function(entry) {
	this._setView(entry);
}


/**
*	@method _setView 
*	@param entry - isntance of ZaDomain class
*/
ZaDomainController.prototype._setView =
function(entry) {
	if(!this._UICreated) {
		this._view = new ZaDomainXFormView(this._container, this._app);
   		this._ops = new Array();
   		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.DTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
   		this._ops.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.DTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
		this._ops.push(new ZaOperation(ZaOperation.SEP));
   		this._ops.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, this._newButtonListener)));
  		this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
		this._ops.push(new ZaOperation(ZaOperation.SEP));
   		this._ops.push(new ZaOperation(ZaOperation.GAL_WIZARD, ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainController.prototype._galWizButtonListener)));   		
   		this._ops.push(new ZaOperation(ZaOperation.AUTH_WIZARD, ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainController.prototype._authWizButtonListener)));   		   		
   		this._ops.push(new ZaOperation(ZaOperation.INIT_NOTEBOOK, ZaMsg.DTBB_InitNotebook, ZaMsg.DTBB_InitNotebook_tt, "NewNotebook", "NewNotebookDis", new AjxListener(this, ZaDomainController.prototype._initNotebookButtonListener)));   		   		   		
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
		this._toolbar = new ZaToolBar(this._container, this._ops);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	    this._app.createView(ZaZimbraAdmin._DOMAIN_VIEW, elements);
		this._UICreated = true;
	} 
	this._app.pushView(ZaZimbraAdmin._DOMAIN_VIEW);
	this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);  		
	if(!entry.id) {
		this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
	} else {
		this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
	}
	this._view.setDirty(false);
	entry[ZaModel.currentTab] = "1";
	if(entry.attrs[ZaDomain.A_zimbraNotebookAccount])
		this._toolbar.getButton(ZaOperation.INIT_NOTEBOOK).setEnabled(false);
	else
		this._toolbar.getButton(ZaOperation.INIT_NOTEBOOK).setEnabled(true);
		
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}

ZaDomainController.prototype._saveChanges = 
function () {
	var tmpObj = this._view.getObject();
	var mods = new Object();
	var haveSmth = false;
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
	if(tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname].join(",").valueOf() !=  this._currentObject.attrs[ZaDomain.A_zimbraVirtualHostname].join(",").valueOf()) {
		mods[ZaDomain.A_zimbraVirtualHostname] = tmpObj.attrs[ZaDomain.A_zimbraVirtualHostname] ;
		haveSmth = true;		
	}
	if(tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount] != this._currentObject.attrs[ZaDomain.A_zimbraNotebookAccount]) {
		mods[ZaDomain.A_zimbraNotebookAccount] = tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount] ;
		haveSmth = true;
	}	
	if(!haveSmth)
		return true;
	
	 
	this._currentObject.modify(mods);
	
	return true;
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
		ZaDomain.modifyGalSettings(this._galWizard.getObject(),this._currentObject); 
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
		ZaDomain.modifyAuthSettings(this._authWizard.getObject(), this._currentObject);
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
				var callback = new AjxCallback(this, this.initNotebookCallback);
				ZaDomain.initNotebook(this._newDomainWizard.getObject(),callback) ;
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
function (arg) {
	if(!arg)
		return;
	if(arg.isException()) {
		this._handleException(arg.getException(), "ZaDomainController.prototype._initNotebookCallback", null, false);
	} 
}

ZaDomainController.prototype._okDomainNotebookListener =
function(ev) {
	try {
		this._initDomainNotebookDlg.popdown();
		var callback = new AjxCallback(this, this.initNotebookCallback);
		ZaDomain.initNotebook(this._initDomainNotebookDlg.getObject(),callback) ;
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._okDomainNotebookListener", null, false);
	}
	return;
}

ZaDomainController.prototype._initNotebookButtonListener = 
function (ev) {
	try {
		this._initDomainNotebookDlg = this._app.dialogs["initDomainNotebook"] = new ZaDomainNotebookXDialog(this._container, this._app, "550px", "300px");	
		this._initDomainNotebookDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainController.prototype._okDomainNotebookListener, this, null);			
		this._initDomainNotebookDlg.setObject(this._currentObject);
		this._initDomainNotebookDlg.popup();
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
