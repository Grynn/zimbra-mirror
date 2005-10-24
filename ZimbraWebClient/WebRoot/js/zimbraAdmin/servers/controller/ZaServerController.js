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
	this._helpURL = "/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/managing_servers/managing_servers.htm";				
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
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerController.prototype._validateChanges, this, args);		
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
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							

		this._toolBar = new ZaToolBar(this._container, this._ops);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolBar;		
	  
	    this._app.createView(ZaZimbraAdmin._SERVER_VIEW, elements);
		this._UICreated = true;
	} 
	this._app.pushView(ZaZimbraAdmin._SERVER_VIEW);
	this._view.setDirty(false);
	entry.load();
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}

ZaServerController.prototype._saveChanges =
function (obj) {

	var isNew = false;
	if(obj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	// update zimbraServiceEnabled
	var svcInstalled = AjxUtil.isString(obj.attrs[ZaServer.A_zimbraServiceInstalled])
							? [ obj.attrs[ZaServer.A_zimbraServiceInstalled] ]
							: obj.attrs[ZaServer.A_zimbraServiceInstalled];
	if (svcInstalled) {
		// get list of actually enabled fields
		var enabled = [];
		for (var i = 0; i < svcInstalled.length; i++) {
			var service = svcInstalled[i];
			if (obj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service]) {
				enabled.push(service);
			}			
		}
		
		// see if list of actually enabled fields is same as before
		
		var dirty = enabled.length > 0; 
		if (this._currentObject.attrs[ZaServer.A_zimbraServiceEnabled]) {
			var prevEnabled = AjxUtil.isString(this._currentObject.attrs[ZaServer.A_zimbraServiceEnabled])
							? [ this._currentObject.attrs[ZaServer.A_zimbraServiceEnabled] ]
							: this._currentObject.attrs[ZaServer.A_zimbraServiceEnabled];
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


	//remove Volumes
	if(this._currentObject.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
		if(obj[ZaServer.A_RemovedVolumes]) {
			var cnt = obj[ZaServer.A_RemovedVolumes].length;
			for(var i = 0; i < cnt; i++) {
				if(obj[ZaServer.A_RemovedVolumes][i][ZaServer.A_VolumeId] > 0) {
					this._currentObject.deleteVolume(obj[ZaServer.A_RemovedVolumes][i][ZaServer.A_VolumeId]);			
				}
			}
		}
	
		//create new Volumes
		if(obj[ZaServer.A_Volumes]) {
			var cnt = obj[ZaServer.A_Volumes].length;
			for(var i = 0; i < cnt; i++) {
				if(!obj[ZaServer.A_Volumes][i][ZaServer.A_VolumeId]) {
					this._currentObject.createVolume(obj[ZaServer.A_Volumes][i]);			
				}
			}
	
			//modify existing volumes
			cnt--;	
			
			var cnt2 = this._currentObject[ZaServer.A_Volumes].length;
			for(var i = cnt; i >= 0; i--) {
				var newVolume = obj[ZaServer.A_Volumes][i];
				var oldVolume;
				for (var ix =0; ix < cnt2; ix++) {
					oldVolume = this._currentObject[ZaServer.A_Volumes][ix];
					if(oldVolume[ZaServer.A_VolumeId] == newVolume[ZaServer.A_VolumeId]) {
						//check attributes
						var modified = false;
						for(var attr in oldVolume) {
							if(oldVolume[attr] != newVolume[attr]) {
								modified = true;
								break;
							}
						}
						
						if(modified) {
							this._currentObject.modifyVolume(obj[ZaServer.A_Volumes][i]);
						}
						obj[ZaServer.A_Volumes].splice(i,1);
					}
				}
			}
		}

		//modify current volumes
		if(this._currentObject[ZaServer.A_CurrentPrimaryMsgVolumeId] != obj[ZaServer.A_CurrentPrimaryMsgVolumeId] && obj[ZaServer.A_CurrentPrimaryMsgVolumeId]) {
			this._currentObject.setCurrentVolume(obj[ZaServer.A_CurrentPrimaryMsgVolumeId], ZaServer.PRI_MSG);
		}
		if(this._currentObject[ZaServer.A_CurrentSecondaryMsgVolumeId] != obj[ZaServer.A_CurrentSecondaryMsgVolumeId] && obj[ZaServer.A_CurrentSecondaryMsgVolumeId]) {
			this._currentObject.setCurrentVolume(obj[ZaServer.A_CurrentSecondaryMsgVolumeId], ZaServer.SEC_MSG);
		}
		if(this._currentObject[ZaServer.A_CurrentIndexMsgVolumeId] != obj[ZaServer.A_CurrentIndexMsgVolumeId] && obj[ZaServer.A_CurrentIndexMsgVolumeId]) {
			this._currentObject.setCurrentVolume(obj[ZaServer.A_CurrentIndexMsgVolumeId], ZaServer.INDEX);
		}
	}
	//save the model
	var changeDetails = new Object();
	this._currentObject.modify(mods);
	this._view.setDirty(false);	
	//if modification took place - fire an ServerChangeEvent
	changeDetails["obj"] = this._currentObject;
	changeDetails["modFields"] = mods;
	this._fireServerChangeEvent(changeDetails);
	return true;
}

/**
* @param params - optional, params that contain a callback function 
* that will be called if the user answers "Yes",
* an argument for the callback function,
* and an object on which this function will be called
**/
ZaServerController.prototype._validateChanges =
function (params) {
	//check if we are removing volumes
	var obj = this._view.getObject();
	if(obj[ZaServer.A_RemovedVolumes] && obj[ZaServer.A_RemovedVolumes].length > 0 ) {
		if(this._confirmMessageDialog)
			this._confirmMessageDialog.popdown();
			
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);	
		this._confirmMessageDialog.setMessage(ZaMsg.Q_DELETE_VOLUMES,  DwtMessageDialog.WARNING_STYLE);
		var args;
		var callBack = ZaServerController.prototype._saveChangesCallback;
		if(!params || !params["callback"]) {
			args = null;
		} else {
			callBack = ZaServerController.prototype._saveChangesCallback;
			args = params;		
		}
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, callBack, this, args);		
		this._confirmMessageDialog.popup();		
	} else {
		this._saveChangesCallback(params);
	}
}

/**
* @param params - optional, contains parameters for the next call
**/
ZaServerController.prototype._saveChangesCallback = 
function (params) {
	try {
		var obj = this._view.getObject();
		if(this._saveChanges(obj)) {
			if(this._confirmMessageDialog)
				this._confirmMessageDialog.popdown();
			if(params) {
				params["func"].call(params["obj"], params["params"]);
			}
		}
	} catch (ex) {
		//if exception thrown - don't go away
		this._handleException(ex, "ZaServerController.prototype._saveChangesCallback", null, false);
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
		this._validateChanges();
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
		this._confirmMessageDialog.setMessage(ZaMsg.Q_SAVE_CHANGES,  DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaServerController.prototype._validateChanges, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaServerController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this._app.popView();
	}	
}



ZaServerController.prototype._closeCnfrmDlg = 
function () {
	this._confirmMessageDialog.popdown();	
}

