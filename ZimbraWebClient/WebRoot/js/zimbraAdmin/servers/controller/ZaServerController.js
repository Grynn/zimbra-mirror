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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ZaServerController controls display of a single Domain
* @contructor ZaServerController
* @param appCtxt
* @param container
* @param abApp
**/

function ZaServerController(appCtxt, container, abApp) {
	ZaController.call(this, appCtxt, container, abApp);
	this._evtMgr = new AjxEventMgr();
	this._UICreated = false;
}

ZaServerController.prototype = new ZaController();
ZaServerController.prototype.constructor = ZaServerController;

//ZaServerController.VIEW = "ZaServerController.VIEW";

/**
*	@method show
*	@param entry - isntance of ZaServer class
*/

ZaServerController.prototype.show = 
function(entry) {
	this._setView(entry);
//	this._app.setCurrentController(this);
	this.setDirty(false);
}


ZaServerController.prototype.setEnabled = 
function(enable) {
	//this._view.setEnabled(enable);
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaServerController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

/**
* Adds listener to modifications in the contained ZaServer 
* @param listener
**/
ZaServerController.prototype.addServerChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaServer 
* @param listener
**/
ZaServerController.prototype.removeServerChangeListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);    	
}

ZaServerController.prototype.setDirty = 
function (isD) {
	if(isD)
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaServerController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = params;
		args["obj"] = nextViewCtrlr;
		args["func"] = func;
		//ask if the user wants to save changes			
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES, null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaServerController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
	
		func.call(nextViewCtrlr, params);
	}

}

/**
*	Private method that notifies listeners to that the controlled ZaServer is changed
* 	@param details
*/
ZaServerController.prototype._fireServerChangeEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(ZaEvent.S_SERVER);
			evt.set(ZaEvent.E_MODIFY, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaServerController.prototype._fireServerChangeEvent", details, false);	
	}
}



/**
*	@method _setView 
*	@param entry - isntance of ZaDomain class
*/
ZaServerController.prototype._setView =
function(entry) {
	if(!this._UICreated) {
		this._view = new ZaServerXFormView(this._container, this._app);
	  	//this._view = new ZaServerView(this._container, this._app);
   		this._ops = new Array();
   		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.SERTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, ZaServerController.prototype._saveButtonListener)));
   		this._ops.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, ZaServerController.prototype._closeButtonListener)));    	

		this._toolBar = new ZaToolBar(this._container, this._ops);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolBar;		
	  
	    this._app.createView(ZaZimbraAdmin._SERVER_VIEW, elements);
		this._UICreated = true;
	} 
	this._app.pushView(ZaZimbraAdmin._SERVER_VIEW);
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}

ZaServerController.prototype._saveChanges =
function (obj) {
//	var tmpObj = this._view.getObject();
	var isNew = false;
	if(obj.attrs == null) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;	
	}

	// update zimbraServiceEnabled
	if (obj.attrs[ZaServer.A_zimbraServiceInstalled]) {
		// get list of actually enabled fields
		var enabled = [];
		for (var i = 0; i < obj.attrs[ZaServer.A_zimbraServiceInstalled].length; i++) {
			var service = obj.attrs[ZaServer.A_zimbraServiceInstalled][i];
			if (obj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service]) {
				enabled.push(service);
			}			
		}
		
		// see if list of actually enabled fields is same as before
		var dirty = enabled.length > 0;
		if (obj.attrs[ZaServer.A_zimbraServiceEnabled]) {
			var prevEnabled = AjxUtil.isString(obj.attrs[ZaServer.A_zimbraServiceEnabled])
							? [ obj.attrs[ZaServer.A_zimbraServiceEnabled] ]
							: obj.attrs[ZaServer.A_zimbraServiceEnabled];
			dirty = enabled.length != prevEnabled.length;		
			if (!dirty) {
				for (var i = 0; i < prevEnabled.length; i++) {
					var service = prevEnabled[i];
					if (!obj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service]) {
						dirty = true;
						break;
					}
				}
			}
		}
		
		// save new list of enabled fields
		if (dirty) {
			obj.attrs[ZaServer.A_zimbraServiceEnabled] = enabled;
		}
	}

	//transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaDomain
	var mods = new Object();
	for (var a in obj.attrs) {
		if(a == ZaItem.A_objectClass || /^_/.test(a))
			continue;
		if (this._currentObject.attrs[a] != obj.attrs[a] ) {
			mods[a] = obj.attrs[a];
		}
	}

	//save the model
	var changeDetails = new Object();
	this._currentObject.modify(mods);
	//if modification took place - fire an ServerChangeEvent
	changeDetails["obj"] = this._currentObject;
	changeDetails["modFields"] = mods;
	this._fireServerChangeEvent(changeDetails);

	return true;
}

ZaServerController.prototype._saveChangesCallback = 
function (obj) {
	if(this._saveChanges(obj)) {
		this._view.setDirty(false);		
		//this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false); 
		this._confirmMessageDialog.popdown();
	}
}
/**
* @param params		   - params["params"] - arguments to pass to the method specified in func parameter
* 					     params["obj"] - the controller of the next view
*						 params["func"] - the method to call on the nextViewCtrlr in order to navigate to the next view
* This method saves changes in the current view and calls the method on the controller of the next view
**/
ZaServerController.prototype._saveAndGoAway =
function (params) {
	try {
		var tmpObj = this._view.getObject();
		if(this._saveChanges(tmpObj)) {
			this._confirmMessageDialog.popdown();	
			params["func"].call(params["obj"], params["params"]);	
				
		}
	} catch (ex) {
		//if exception thrown - don't go away
		this._handleException(ex, "ZaServerController.prototype._saveAndGoAway", null, false);
	}
}

/**
* Leaves current view without saving any changes
**/
ZaServerController.prototype._discardAndGoAway = 
function (params) {
	this._confirmMessageDialog.popdown();
	params["func"].call(params["obj"], params["params"]);		

}

/**
* handles "save" button click
* calls modify or create on the current ZaDomain
**/
ZaServerController.prototype._saveButtonListener =
function(ev) {
	try {
		var tmpObj = this._view.getObject();
		//check if disabling email service
		if((this._currentObject.attrs[ZaServer.A_zimbraUserServicesEnabled]=="TRUE") && (tmpObj.attrs[ZaServer.A_zimbraUserServicesEnabled]=="FALSE")) {
			//ask if the user wants to save changes		
			this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
			this._confirmMessageDialog.setMessage(ZaMsg.NAD_Dialog_ShutdownEmailService, null, DwtMessageDialog.WARNING_STYLE);
			this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerController.prototype._saveChangesCallback, this, tmpObj);		
			this._confirmMessageDialog.popup();
		
		} else {
			if(this._saveChanges(tmpObj)) {
				this._view.setDirty(false);		
			}
		}
	} catch (ex) {
		//if exception thrown - don' go away
		this._handleException(ex, "ZaServerController.prototype._saveButtonListener", null, false);
	}
}

/**
* handles the Close button click. Returns to the list view.
**/ 
ZaServerController.prototype._closeButtonListener =
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
		this._confirmMessageDialog.setMessage(ZaMsg.NAD_Dialog_SaveChanges, null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaServerController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this._app.popView();
//		this._app.getServerListController().show();
	}	
}



ZaServerController.prototype._closeCnfrmDlg = 
function () {
	this._confirmMessageDialog.popdown();	
}

