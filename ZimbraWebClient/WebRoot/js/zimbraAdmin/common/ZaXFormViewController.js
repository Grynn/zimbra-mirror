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
* @class ZaXFormViewController base class for all Za***ViewControllers
* @contructor ZaXFormViewController
* @param appCtxt
* @param container
* @param app
* @param iKeyName
* @author Greg Solovyev
**/

function ZaXFormViewController(appCtxt, container, app, iKeyName) {
	if (arguments.length == 0) return;
	ZaController.call(this, appCtxt, container, app, iKeyName);
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
}

ZaXFormViewController.prototype = new ZaController();
ZaXFormViewController.prototype.constructor = ZaXFormViewController;

/**
*	Method that notifies listeners to that the controlled ZaAccount is removed
* 	@param details
*/
ZaXFormViewController.prototype.fireRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(this.objType);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.fireRemovalEvent", details, false);	
	}
}

/**
*	Method that notifies listeners to that the controlled object is changed
* 	@param details
*/
ZaXFormViewController.prototype.fireChangeEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(this.objType);
			evt.set(ZaEvent.E_MODIFY, this);
			if(details)
				evt.setDetails(details);			
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.fireChangeEvent", null, false);	
	}
}

/**
*	Private method that notifies listeners that a new object is created
* 	@param details
*/
ZaXFormViewController.prototype.fireCreationEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_CREATE)) {
			var evt = new ZaEvent(this.objType);
			evt.set(ZaEvent.E_CREATE, this);
			if(details)
				evt.setDetails(details);
				
			this._evtMgr.notifyListeners(ZaEvent.E_CREATE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.fireCreationEvent", details, false);	
	}

}

//Listeners for default toolbar buttons (close, save, delete)
/**
* handles the Close button click. Returns to the list view.
**/ 
ZaXFormViewController.prototype.closeButtonListener =
function(ev) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app;
		args["func"] = ZaApp.prototype.popView;
		//ask if the user wants to save changes		
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this._app.popView();
	}	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaXFormViewController.prototype.deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		this._confirmMessageDialog.setMessage(this.deleteMsg, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this.deleteAndGoAway, this, null);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
		this._confirmMessageDialog.popup();
	} else {
		this._app.popView();
	}
}

/**
* @param 	ev event object
* This method handles "save" button click
**/
ZaXFormViewController.prototype.saveButtonListener =
function(ev) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);
			if(this._toolbar)
				this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);		
		
			this._currentObject.refresh(false);	
			this._view.setObject(this._currentObject);			
			this.fireChangeEvent();			
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype._saveButtonListener", null, false);
	}
	return;
}

ZaXFormViewController.prototype.saveAndGoAway =
function (params) {
	try {
		this.closeCnfrmDlg();		
		if(this._saveChanges()) {
			params["func"].call(params["obj"], params["params"]);	
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.saveAndGoAway", null, false);
	}
}


ZaXFormViewController.prototype.deleteAndGoAway = 
function () {
	try {
		if(this._currentObject.id) {
			this._currentObject.remove();
			this.fireRemovalEvent(this._currentObject);
		}
		this.closeCnfrmDlg();	
		this._app.popView();			
	} catch (ex) {
		this.closeCnfrmDlg();	
		this._handleException(ex, "ZaXFormViewController.prototype.deleteAndGoAway", null, false);				
	}
}

/**
* Leaves current view without saving any changes
**/
ZaXFormViewController.prototype.discardAndGoAway = 
function (params) {
	this.closeCnfrmDlg();
	params["func"].call(params["obj"], params["params"]);		
}
/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaXFormViewController.prototype.switchToNextView = 
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
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		func.call(nextViewCtrlr, params);
	}
}

ZaXFormViewController.prototype.setDirty = 
function (isD) {
	if(!this._toolbar || !this._toolbar.getButton(ZaOperation.SAVE))
		return;
		
	if(isD)
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
}

