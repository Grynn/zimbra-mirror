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
* @class ZaDomainController controls display of a single Domain
* @contructor ZaDomainController
* @param appCtxt
* @param container
* @param abApp
**/

function ZaDomainController(appCtxt, container, abApp) {
	ZaController.call(this, appCtxt, container, abApp);
	this._evtMgr = new AjxEventMgr();
	this._UICreated = false;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_domains/managing_domains.htm";			
}

ZaDomainController.prototype = new ZaController();
ZaDomainController.prototype.constructor = ZaDomainController;

//ZaDomainController.VIEW = "ZaDomainController.VIEW";

/**
*	@method show
*	@param entry - isntance of ZaDomain class
*/

ZaDomainController.prototype.show = 
function(entry) {
	this._setView(entry);
//	this._app.setCurrentController(this);
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaDomainController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

/**
* Adds listener to modifications in the contained ZaDomain 
* @param listener
**/
ZaDomainController.prototype.addDomainChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaDomain 
* @param listener
**/
ZaDomainController.prototype.removeDomainChangeListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);    	
}

/**
* Adds listener to creation of an ZaDomain 
* @param listener
**/
ZaDomainController.prototype.addDomainCreationListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_CREATE, listener);
}

/**
* Removes listener to creation of an ZaDomain 
* @param listener
**/
ZaDomainController.prototype.removeDomainCreationListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_CREATE, listener);    	
}

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/
ZaDomainController.prototype.addDomainRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/**
* Removes listener to removal of an ZaDomain 
* @param listener
**/
ZaDomainController.prototype.removeDomainRemovalListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_REMOVE, listener);    	
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaDomainController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = params;
		args["obj"] = nextViewCtrlr;
		args["func"] = func;
		//ask if the user wants to save changes			
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES,  DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
	
		func.call(nextViewCtrlr, params);
	}

}

ZaDomainController.prototype.setDirty = 
function (isD) {
	if(isD)
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
}

/**
*	Private method that notifies listeners to that the controlled ZaDomain is changed
* 	@param details
*/
ZaDomainController.prototype._fireDomainChangeEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_MODIFY, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._fireDomainChangeEvent", details, false);	
	}
}

/**
*	Private method that notifies listeners that a new ZaDomain is created
* 	@param details
*/
ZaDomainController.prototype._fireDomainCreationEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_CREATE)) {
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_CREATE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_CREATE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._fireDomainCreationEvent", details, false);	
	}
}

/**
*	Private method that notifies listeners to that the controlled ZaDomain is removed
* 	@param details
*/
ZaDomainController.prototype._fireDomainRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_DOMAIN);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._fireDomainRemovalEvent", details, false);	
	}
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
   		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.DTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, ZaDomainController.prototype._saveButtonListener)));
   		this._ops.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.DTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, ZaDomainController.prototype._closeButtonListener)));    	
		this._ops.push(new ZaOperation(ZaOperation.SEP));
   		this._ops.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, ZaDomainController.prototype._newButtonListener)));
  		this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, ZaDomainController.prototype._deleteButtonListener)));    	    	
		this._ops.push(new ZaOperation(ZaOperation.SEP));
   		this._ops.push(new ZaOperation(ZaOperation.GAL_WIZARD, ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainController.prototype._galWizButtonListener)));   		
   		this._ops.push(new ZaOperation(ZaOperation.AUTH_WIZARD, ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainController.prototype._authWizButtonListener)));   		   		
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
		this._toolBar = new ZaToolBar(this._container, this._ops);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolBar;		
	    this._app.createView(ZaZimbraAdmin._DOMAIN_VIEW, elements);
		this._UICreated = true;
	} 
	this._app.pushView(ZaZimbraAdmin._DOMAIN_VIEW);
	this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);  		
	if(!entry.id) {
		this._toolBar.getButton(ZaOperation.DELETE).setEnabled(false);  			
	} else {
		this._toolBar.getButton(ZaOperation.DELETE).setEnabled(true);  				
	}
	this._view.setDirty(false);
	entry[ZaModel.currentTab] = "1"
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
	if(!haveSmth)
		return true;
	
	 
	this._currentObject.modify(mods);
	
	return true;
}

/**
* @param params		   - params["params"] - arguments to pass to the method specified in func parameter
* 					     params["obj"] - the controller of the next view
*						 params["func"] - the method to call on the nextViewCtrlr in order to navigate to the next view
* This method saves changes in the current view and calls the method on the controller of the next view
**/
ZaDomainController.prototype._saveAndGoAway =
function (params) {
	try {
		this._confirmMessageDialog.popdown();		
		if(this._saveChanges()) {
			
			params["func"].call(params["obj"], params["params"]);	
		}
	} catch (ex) {
		//if exception thrown - don' go away
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_EXISTS, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup(this._getDialogXY());
		} else {
			this._handleException(ex, "ZaDomainController.prototype._saveAndGoAway", null, false);
		}
	}
}

/**
* Leaves current view without saving any changes
**/
ZaDomainController.prototype._discardAndGoAway = 
function (params) {
	this._confirmMessageDialog.popdown();

	params["func"].call(params["obj"], params["params"]);		
}



/**
* handles "save" button click
* calls modify or create on the current ZaDomain
**/
ZaDomainController.prototype._saveButtonListener =
function(ev) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);		
			this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false); 
			this._view.setObject(this._currentObject, true);
		}
	} catch (ex) {
		//if exception thrown - don' go away
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_EXISTS, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup(this._getDialogXY());
		} else {
			this._handleException(ex, "ZaDomainController.prototype._saveButtonListener", null, false);
		}
	}
}

/**
* handles the Close button click. Returns to the list view.
**/ 
ZaDomainController.prototype._closeButtonListener =
function(ev) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app;
		args["func"] = ZaApp.prototype.popView;
		//ask if the user wants to save changes		
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES,  DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this._app.popView();
		//this._app.getDomainListController().show();
	}	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaDomainController.prototype._deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);						
		this._confirmMessageDialog.setMessage("Are you sure you want to delete this Domain?",  DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainController.prototype._deleteAndGoAway, this, null);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainController.prototype._closeCnfrmDlg, this, null);				
		this._confirmMessageDialog.popup();
	} else {
		return;
	}
}

ZaDomainController.prototype._deleteAndGoAway = 
function () {
	try {
		if(this._currentObject.id) {
			this._currentObject.remove();
			this._fireDomainRemovalEvent(this._currentObject);
		}
		this._app.getDomainListController().show();
		this._confirmMessageDialog.popdown();	
			
	} catch (ex) {
		this._confirmMessageDialog.popdown();	
		if(ex.code == ZmCsfeException.DOMAIN_NOT_EMPTY) {
			this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_NOT_EMPTY, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();			
		} else {
			this._handleException(ex, "ZaDomainController.prototype._deleteAndGoAway", null, false);				
		}
	}
}

ZaDomainController.prototype._closeCnfrmDlg = 
function () {
	this._confirmMessageDialog.popdown();	
}

ZaDomainController.prototype.newDomain = 
function () {
	this._currentObject = new ZaDomain();
	this._showNewDomainWizard();
}

ZaDomainController.prototype._showNewDomainWizard = 
function () {
	try {
		this._newDomainWizard = new ZaNewDomainXWizard(this._container, this._app);	
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
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaDomainController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaDomainController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this.newDomain();
	}	
}


ZaDomainController.prototype._galWizButtonListener =
function(ev) {
	try {
		this._galWizard = new ZaGALConfigXWizard(this._container, this._app);	
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
		this._authWizard = new ZaAuthConfigXWizard(this._container, this._app);	
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
		var changeDetails = new Object();
		ZaDomain.modifyGalSettings(this._galWizard.getObject(),this._currentObject); 
		//if a modification took place - fire an DomainChangeEvent
		changeDetails["obj"] = this._currentObject;
		this._fireDomainChangeEvent(changeDetails);
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
		var changeDetails = new Object();
		//if a modification took place - fire an DomainChangeEvent
		changeDetails["obj"] = this._currentObject;
	
		this._fireDomainChangeEvent(changeDetails);
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
			this._fireDomainCreationEvent(domain);
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(true);	
			this._newDomainWizard.popdown();		
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
