/**
* @class ZaCosController controls display of a single COS
* @contructor ZaCosController
* @param appCtxt
* @param container
* @param abApp
**/

function ZaCosController(appCtxt, container, abApp) {
	ZaController.call(this, appCtxt, container, abApp);
	this._evtMgr = new AjxEventMgr();
	this._confirmMessageDialog;
	this._UICreated = false;	
}

ZaCosController.prototype = new ZaController();
ZaCosController.prototype.constructor = ZaCosController;

ZaCosController.VIEW = "ZaCosController.VIEW";

/**
*	@method show
*	@param entry - isntance of ZaCos class
*/

ZaCosController.prototype.show = 
function(entry) {
	this._setView(entry);
	this._app.setCurrentController(this);
}



/**
* Adds listener to modifications in the contained ZaCos 
* @param listener
**/
ZaCosController.prototype.addCosChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaCos 
* @param listener
**/
ZaCosController.prototype.removeCosChangeListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);    	
}

/**
* Adds listener to creation of an ZaCos 
* @param listener
**/
ZaCosController.prototype.addCosCreationListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_CREATE, listener);
}

/**
* Removes listener to creation of an ZaCos 
* @param listener
**/
ZaCosController.prototype.removeCosCreationListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_CREATE, listener);    	
}

/**
* Adds listener to removal of an ZaCos 
* @param listener
**/
ZaCosController.prototype.addCosRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/**
* Removes listener to removal of an ZaCos 
* @param listener
**/
ZaCosController.prototype.removeCosRemovalListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_REMOVE, listener);    	
}

/**
* @param nextViewCtrlr - the controller of the next view
* @param func		   - the method to call on the nextViewCtrlr in order to navigate to the next view
* @param params		   - arguments to pass to the method specified in func parameter
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaCosController.prototype.switchToNextView = 
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
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {

		func.call(nextViewCtrlr, params);
	}
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaCosController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

ZaCosController.prototype.setDirty = 
function (isD) {
	if(isD)
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
}
/**
*	Private method that notifies listeners to that the controlled ZaCos is changed
* 	@param details
*/
ZaCosController.prototype._fireCosChangeEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(ZaEvent.S_COS);
			evt.set(ZaEvent.E_MODIFY, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._fireCosChangeEvent, details, false);
	}
}

/**
*	Private method that notifies listeners that a new ZaCos is created
* 	@param details
*/
ZaCosController.prototype._fireCosCreationEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_CREATE)) {
			var evt = new ZaEvent(ZaEvent.S_COS);
			evt.set(ZaEvent.E_CREATE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_CREATE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._fireCosCreationEvent, details, false);	
	}
}

/**
*	Private method that notifies listeners to that the controlled ZaCos is removed
* 	@param details
*/
ZaCosController.prototype._fireCosRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_COS);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._fireCosRemovalEvent, details, false);	
	}
}

/**
*	@method _setView 
*	@param entry - isntance of ZaCos class
*/
ZaCosController.prototype._setView =
function(entry) {
	try {
	   	//create toolbar
		if(!this._UICreated) {
		   	this._ops = new Array();
	   		this._ops.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, ZaImg.I_NEWCOS, ZaImg.I_NEWCOS, new AjxListener(this, ZaCosController.prototype._newButtonListener)));
	   		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.COSTBB_Save_tt, ZaImg.I_SAVE, ZaImg.ID_SAVE, new AjxListener(this, ZaCosController.prototype._saveButtonListener)));
	   		this._ops.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.COSTBB_Close_tt, ZaImg.I_UNDO, ZaImg.I_UNDO, new AjxListener(this, ZaCosController.prototype._closeButtonListener)));    	
	   		this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, ZaImg.I_DELETE, ZaImg.I_DELETE, new AjxListener(this, ZaCosController.prototype._deleteButtonListener)));    	    	
			this._toolBar = new ZaToolBar(this._container, this._ops);
	
		  	//this._view = new ZaCosView(this._container, this._app, entry.id);
		  	this._view = new ZaCosXFormView(this._container, this._app, entry.id);
		    this._app.createView(ZaCosController.VIEW, [this._toolBar, this._view]);  	
		    this._UICreated = true;
	  	}
	
		this._app.pushView(ZaCosController.VIEW);
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
		if(!entry.id) {
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(true);  				
		}	
		this._view.setDirty(false);
		entry[ZaModel.currentTab] = "1"
	  	this._view.setObject(entry);

	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._setView, null, false);	
	}
	this._currentObject = entry;
}

/**
* saves the changes in the fields, calls modify or create on the current ZaCos
* @return Boolean - indicates if the changes were succesfully saved
**/
ZaCosController.prototype._saveChanges =
function () {

	//check if the XForm has any errors
	if(this._view.getMyForm().hasErrors()) {
		var errItems = this._view.getMyForm().getItemsInErrorState();
		var dlgMsg = ZaMsg.CORRECT_ERRORS;
		dlgMsg +=  "<br><ul>";
		var i = 0;
		for(var key in errItems) {
			if(i > 19) {
				dlgMsg += "<li>...</li>";
				break;
			}
			if(key == "size") continue;
			var label = errItems[key].getInheritedProperty("msgName");
			if(!label && errItems[key].getParentItem()) { //this might be a part of a composite
				label = errItems[key].getParentItem().getInheritedProperty("msgName");
			}
			if(label) {
				dlgMsg += "<li>";
				dlgMsg +=label;			
				dlgMsg += "</li>";
			}
			i++;
		}
		dlgMsg += "</ul>";
		this.popupMsgDialog(dlgMsg, null, true);
		return false;
	}
	
	//check if the data is copmlete 
	var tmpObj = this._view.getObject();
	var isNew = false;
	//Check the data
	if(tmpObj.attrs == null) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;	
	}

	//name
	if(tmpObj.attrs[ZaCos.A_name] == null || tmpObj.attrs[ZaCos.A_name].length < 1 ) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_NAME_REQUIRED, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	} else {
		tmpObj.name = tmpObj.attrs[ZaCos.A_name];
	}

	if(tmpObj.name.length > 256 || tmpObj.attrs[ZaCos.A_name].length > 256) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_COS_NAME_TOOLONG, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}
	
	/**
	* check values
	**/
	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraMailQuota])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailQuota + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}

	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_ContactMaxNumEntries + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}
	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinLength + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}
	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxLength + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}	
	
	if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) > 0) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_MAX_MIN_PWDLENGTH, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}	

	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinAge + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}		
	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxAge + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}		
	
	if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) > 0) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_MAX_MIN_PWDAGE, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}
		
	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraAuthTokenLifetime])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AuthTokenLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}

	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraAdminAuthTokenLifetime])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AdminAuthTokenLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}		
	
	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailMessageLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}			

	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailTrashLifetime])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailTrashLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}	
	
	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailSpamLifetime])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailSpamLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}		
	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraPrefContactsPerPage])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_PrefContactsPerPage + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}	
	if(!AjxUtil.isNonNegativeInteger(tmpObj.attrs[ZaCos.A_zimbraEnforcePwdHistory])) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passEnforceHistory + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;
	}	
		
	var mods = new Object();
	var changeDetails = new Object();
	if(!tmpObj.id)
		isNew = true;
		
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if( (a == ZaItem.A_objectClass) || (a == ZaItem.A_zimbraId) || (a == ZaCos.A_zimbraMailHostPool))
			continue;
		//check if the value has been modified or the object is new
		if (isNew || (this._currentObject.attrs[a] != tmpObj.attrs[a]) ) {
			mods[a] = tmpObj.attrs[a];
		}
	}
	//check if host pool has been changed
	var poolServerIds = new Array();
	if(tmpObj[ZaCos.A_zimbraMailHostPoolInternal]) {
		var cnt = tmpObj[ZaCos.A_zimbraMailHostPoolInternal].length;
		for(var i = 0; i < cnt; i ++) {
			poolServerIds.push(tmpObj[ZaCos.A_zimbraMailHostPoolInternal][i].id);
		}
		if(poolServerIds.toString() != this._currentObject[ZaCos.A_zimbraMailHostPoolInternal].toString()) {
			mods[ZaCos.A_zimbraMailHostPool] = poolServerIds;
		}
	}
	
	//check if need to rename
	if(!isNew) {
		if(tmpObj.name != this._currentObject.name) {
			newName=tmpObj.name;
			changeDetails["newName"] = newName;
			try {
				this._currentObject.rename(newName);
			} catch (ex) {
				if (ex.code == AjxCsfeException.SVC_AUTH_EXPIRED || ex.code == AjxCsfeException.SVC_AUTH_REQUIRED || ex.code == AjxCsfeException.NO_AUTH_TOKEN) {
						this._showLoginDialog();
				} else {
					var detailStr = "";
					for (var prop in ex) {
						detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
					}
					if(ex.code == AjxCsfeException.COS_EXISTS) {
						this._msgDialog.setMessage(ZaMsg.FAILED_RENAME_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
						this._msgDialog.popup();
					} else {
						this._msgDialog.setMessage(ZaMsg.FAILED_RENAME_COS, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
						this._msgDialog.popup();
					}
				}
				return false;
			}
		}
	}
	//save changed fields
	try {	
		if(isNew) {
			this._currentObject.create(tmpObj.name, mods);
			//if creation took place - fire an CosChangeEvent
			this._fireCosCreationEvent(this._currentObject);
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(true);	
		} else {
			this._currentObject.modify(mods);
			//if modification took place - fire an CosChangeEvent
			changeDetails["obj"] = this._currentObject;
			changeDetails["mods"] = mods;
			this._fireCosChangeEvent(changeDetails);
		}
	} catch (ex) {
		if (ex.code == AjxCsfeException.SVC_AUTH_EXPIRED || ex.code == AjxCsfeException.SVC_AUTH_REQUIRED || ex.code == AjxCsfeException.NO_AUTH_TOKEN) {
				this._showLoginDialog();
		} else {
			var detailStr = "";
			for (var prop in ex) {
				detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
			}
			if(ex.code == AjxCsfeException.COS_EXISTS) {
				this._msgDialog.setMessage(ZaMsg.FAILED_CREATE_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);				
				this._msgDialog.popup();
			} else {
				if(isNew) {
					this._msgDialog.setMessage(ZaMsg.FAILED_CREATE_COS, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
				} else {
					this._msgDialog.setMessage(ZaMsg.FAILED_SAVE_COS, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
				}
				this._msgDialog.popup();
			}
		}
		return false;
	}
	return true;
	
}
/**
* @param params		   - params["params"] - arguments to pass to the method specified in func parameter
* 					     params["obj"] - the controller of the next view
*						 params["func"] - the method to call on the nextViewCtrlr in order to navigate to the next view
* This method saves changes in the current view and calls the method on the controller of the next view
**/
ZaCosController.prototype._saveAndGoAway =
function (params) {
	this._confirmMessageDialog.popdown();			
	try {
		if(this._saveChanges()) {
			params["func"].call(params["obj"], params["params"]);	

		}
	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._saveAndGoAway, null, false);
	}
}

/**
* Leaves current view without saving any changes
**/
ZaCosController.prototype._discardAndGoAway = 
function (params) {
	this._confirmMessageDialog.popdown();
	try {
		params["func"].call(params["obj"], params["params"]);		

	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._discardAndGoAway, null, false);
	}
}
/**
* @param 	ev event object
* This method handles "save" button click
**/
ZaCosController.prototype._saveButtonListener =
function(ev) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);	
			this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);					
			this._view.setObject(this._currentObject, true);				
		}
	} catch (ex) {
		this._handleException(ex, ZaCosController.prototype._saveButtonListener, null, false);
	}
}

/**
* handles the Close button click. Returns to the list view.
**/ 
ZaCosController.prototype._closeButtonListener =
function(ev) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app.getCosListController();
		args["func"] = ZaCosListController.prototype.show;
		//ask if the user wants to save changes		
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._confirmMessageDialog.setMessage("Do you want so save current changes?", null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {

		this._app.getCosListController().show();
	}	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaCosController.prototype._deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);						
		this._confirmMessageDialog.setMessage("Are you sure you want to delete this COS?", null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosController.prototype._deleteAndGoAway, this, null);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosController.prototype._closeCnfrmDlg, this, null);				
		this._confirmMessageDialog.popup();
	} else {
		this._app.getCosListController().show();
	}
}

ZaCosController.prototype._deleteAndGoAway = 
function () {
	try {
		if(this._currentObject.id) {
			this._currentObject.remove();
			this._fireCosRemovalEvent(this._currentObject);
		}
		this._app.getCosListController().show();
		this._confirmMessageDialog.popdown();	

	} catch (ex) {
		this._confirmMessageDialog.popdown();	
		this._handleException(ex, ZaCosController.prototype._deleteAndGoAway, null, false);				
	}
}

ZaCosController.prototype._closeCnfrmDlg = 
function () {
	this._confirmMessageDialog.popdown();	
}

ZaCosController.prototype.newCos = 
function () {
	var newCos = new ZaCos(this._app);
	this._setView(newCos);
}

// new button was pressed
ZaCosController.prototype._newButtonListener =
function(ev) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app.getCosController();
		args["func"] = ZaCosController.prototype.newCos;
		//ask if the user wants to save changes		
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._confirmMessageDialog.setMessage("Do you want so save current changes?", null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaCosController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaCosController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this.newCos();
	}	
}
