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
* @class ZaServerController controls display of a single Server
* @contructor ZaServerController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

function ZaServerController(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app,"ZaServerController");
	this._UICreated = false;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_servers/managing_servers.htm";				
	this._toolbarOperations = new Array();
	this.deleteMsg = ZaMsg.Q_DELETE_SERVER;	
	this.objType = ZaEvent.S_SERVER;	
}

ZaServerController.prototype = new ZaXFormViewController();
ZaServerController.prototype.constructor = ZaServerController;

ZaController.initToolbarMethods["ZaServerController"] = new Array();
ZaController.setViewMethods["ZaServerController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaServer class
*/
ZaServerController.prototype.show = 
function(entry) {
	this._setView(entry);
	this.setDirty(false);
}


ZaServerController.prototype.setEnabled = 
function(enable) {
	//this._view.setEnabled(enable);
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
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.validateChanges, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		func.call(nextViewCtrlr, params);
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
ZaServerController.initToolbarMethod = 
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.SERTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
}
ZaController.initToolbarMethods["ZaServerController"].push(ZaServerController.initToolbarMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaServerController.setViewMethod =
function(entry) {
	entry.load();
	if(!this._UICreated) {
		this._createUI();
	} 
	this._app.pushView(ZaZimbraAdmin._SERVER_VIEW);
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaServerController"].push(ZaServerController.setViewMethod);

/**
* @method _createUI
**/
ZaServerController.prototype._createUI =
function () {
	this._view = new ZaServerXFormView(this._container, this._app);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    this._app.createView(ZaZimbraAdmin._SERVER_VIEW, elements);
	this._UICreated = true;
}

ZaServerController.prototype._saveChanges =
function (obj) {
	this._currentObject.modify(obj);
	this._view.setDirty(false);	
	this.fireChangeEvent(this._currentObject);
	return true;
}

ZaServerController.prototype._saveChangesOld =
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
		if(a == ZaItem.A_objectClass || /^_/.test(a) || a == ZaServer.A_zimbraServiceInstalled)
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
	
		if(obj[ZaServer.A_Volumes]) {			
		
		
			var tmpVolumeMap = new Array();
			var cnt = obj[ZaServer.A_Volumes].length;
			for(var i = 0; i < cnt; i++) {
				tmpVolumeMap.push(obj[ZaServer.A_Volumes][i]);
			}
		
			//create new Volumes
			cnt = tmpVolumeMap.length;
			for(var i = 0; i < cnt; i++) {
				//consider only new rows (no VolumeID)
				//ignore empty rows, Bug 4425
				if(!tmpVolumeMap[i][ZaServer.A_VolumeId] && tmpVolumeMap[i][ZaServer.A_VolumeName] && tmpVolumeMap[i][ZaServer.A_VolumeRootPath]) {
					this._currentObject.createVolume(tmpVolumeMap[i]);			
				}
			}
	
			//modify existing volumes
			cnt--;	
			
			var cnt2 = this._currentObject[ZaServer.A_Volumes].length;
			for(var i = cnt; i >= 0; i--) {
				var newVolume = tmpVolumeMap[i];
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
							this._currentObject.modifyVolume(tmpVolumeMap[i]);
						}
						tmpVolumeMap.splice(i,1);
					}
				}
			}
		}

		//modify current volumes
		if(this._currentObject[ZaServer.A_CurrentMsgVolumeId] != obj[ZaServer.A_CurrentPrimaryMsgVolumeId] && obj[ZaServer.A_CurrentPrimaryMsgVolumeId]) {
			this._currentObject.setCurrentVolume(obj[ZaServer.A_CurrentPrimaryMsgVolumeId], ZaServer.PRI_MSG);
		}
		if(this._currentObject[ZaServer.A_CurrentIndexVolumeId] != obj[ZaServer.A_CurrentIndexVolumeId] && obj[ZaServer.A_CurrentIndexVolumeId]) {
			this._currentObject.setCurrentVolume(obj[ZaServer.A_CurrentIndexVolumeId], ZaServer.INDEX);
		}
	}
	//save the model
	//var changeDetails = new Object();
	this._currentObject.modify(mods);
	this._view.setDirty(false);	
	//if modification took place - fire an ServerChangeEvent
	//changeDetails["obj"] = this._currentObject;
	//changeDetails["modFields"] = mods;
	this.fireChangeEvent(this._currentObject);
	return true;
}

/**
* @param params - optional, params that contain a callback function 
* that will be called if the user answers "Yes",
* an argument for the callback function,
* and an object on which this function will be called
**/
ZaServerController.prototype.validateChanges =
function (params) {
	//check if we are removing volumes
	var obj = this._view.getObject();
	if(obj[ZaServer.A_RemovedVolumes] && obj[ZaServer.A_RemovedVolumes].length > 0 ) {
		if(this._app.dialogs["confirmMessageDialog"])
			this._app.dialogs["confirmMessageDialog"].popdown();
			
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);	
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_DELETE_VOLUMES,  DwtMessageDialog.WARNING_STYLE);
		var args;
		var callBack = ZaServerController.prototype._saveChangesCallback;
		if(!params || !params["func"]) {
			args = null;
		} else {
			callBack = ZaServerController.prototype._saveChangesCallback;
			args = params;		
		}
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, callBack, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();		
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
			this.closeCnfrmDlg();
			if(params) {
				params["func"].call(params["obj"], params["params"]);
			} else {
				this._setView(this._currentObject);
			}
		}
	} catch (ex) {
		//if exception thrown - don't go away
		this._handleException(ex, "ZaServerController.prototype._saveChangesCallback", null, false);
	}	
}

/**
* handles "save" button click
* calls modify or create on the current ZaDomain
**/
ZaServerController.prototype.saveButtonListener =
function(ev) {
	try {
		this.validateChanges();
		
	} catch (ex) {
		//if exception thrown - don' go away
		this._handleException(ex, "ZaServerController.prototype.saveButtonListener", null, false);
	}
}



/**
* handles the Close button click. Returns to the list view.
**/ 
ZaServerController.prototype.closeButtonListener =
function(ev) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app;
		args["func"] = ZaApp.prototype.popView;

		//ask if the user wants to save changes		
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES,  DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.validateChanges, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		this._app.popView();
	}	
}