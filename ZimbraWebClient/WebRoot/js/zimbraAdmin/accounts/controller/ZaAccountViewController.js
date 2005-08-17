/**
* @class ZaAccountViewController controls display of a single Account
* @contructor ZaAccountViewController
* @param appCtxt
* @param container
* @param abApp
* @author Roland Schemers
* @author Greg Solovyev
**/

function ZaAccountViewController(appCtxt, container, abApp) {
	ZaController.call(this, appCtxt, container, abApp);
	this._evtMgr = new AjxEventMgr();
	this._confirmMessageDialog;
	this._UICreated = false;
}

ZaAccountViewController.prototype = new ZaController();
ZaAccountViewController.prototype.constructor = ZaAccountViewController;

ZaAccountViewController.VIEW = "ZaAccountViewController.VIEW";

//public methods

/**
*	@method show
*	@param entry - isntance of ZaAccount class
*/

ZaAccountViewController.prototype.show = 
function(entry) {
	this._setView(entry);
	this._app.setCurrentController(this);
}

/**
* @param nextViewCtrlr - the controller of the next view
* @param func		   - the method to call on the nextViewCtrlr in order to navigate to the next view
* @param params		   - arguments to pass to the method specified in func parameter
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaAccountViewController.prototype.switchToNextView = 
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
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaAccountViewController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaAccountViewController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		func.call(nextViewCtrlr, params);
	}
}

/**
* public getToolBar
* @return reference to the toolbar
**/
ZaAccountViewController.prototype.getToolBar = 
function () {
	return this._toolBar;	
}

ZaAccountViewController.prototype.setDirty = 
function (isD) {
	if(isD)
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
}

/**
* Adds listener to modifications in the contained ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.addAccountChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.removeAccountChangeListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);    	
}

/**
* Adds listener to creation of an ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.addAccountCreationListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_CREATE, listener);
}

/**
* Removes listener to creation of an ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.removeAccountCreationListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_CREATE, listener);    	
}

/**
* Adds listener to removal of an ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.addAccountRemovalListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_REMOVE, listener);
}

/**
* Removes listener to removal of an ZaAccount 
* @param listener
**/
ZaAccountViewController.prototype.removeAccountRemovalListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_REMOVE, listener);    	
}

//Private/protected methods

/**
* saves the changes in the fields, calls modify or create on the current ZaAccount
* @return Boolean - indicates if the changes were succesfully saved
**/
ZaAccountViewController.prototype._saveChanges =
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
	var newName=null;
	
	//Check the data
	if(tmpObj.attrs == null ) {
		//show error msg
		this._msgDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._msgDialog.popup();		
		return false;	
	}
	
	//check if need to rename
	if(this._currentObject && tmpObj.name != this._currentObject.name) {
		var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if(!emailRegEx.test(tmpObj.name) ) {
			//show error msg
			this._msgDialog.setMessage(ZaMsg.ERROR_ACCOUNT_NAME_INVALID, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._msgDialog.popup();		
			return false;
		}
		newName = tmpObj.name;
	}
	
	var myCos = null;
	var maxPwdLen = Number.POSITIVE_INFINITY;
	var minPwdLen = 1;	
	
	if(tmpObj.attrs[ZaAccount.A_COSId]) {
		myCos = new ZaCos(this._app);
		myCos.load("id", tmpObj.attrs[ZaAccount.A_COSId]);
		if(myCos.attrs[ZaCos.A_zimbraMinPwdLength] > 0) {
			minPwdLen = myCos.attrs[ZaCos.A_zimbraMinPwdLength];
		}
		if(myCos.attrs[ZaCos.A_zimbraMaxPwdLength] > 0) {
			maxPwdLen = myCos.attrs[ZaCos.A_zimbraMaxPwdLength];
		}		
	}
	

	var mods = new Object();
	var changeDetails = new Object();
	

	//check if need to rename
	if(newName) {
		changeDetails["newName"] = newName;
		try {
			this._currentObject.rename(newName);
		} catch (ex) {
			if (ex.code == AjxCsfeException.SVC_AUTH_EXPIRED || ex.code == AjxCsfeException.SVC_AUTH_REQUIRED || ex.code == AjxCsfeException.NO_AUTH_TOKEN) {
					this._showLoginDialog();
			} else {
				/*var detailStr = "";
				for (var prop in ex) {
					detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
				}*/
				if(ex.code == AjxCsfeException.ACCT_EXISTS) {
					this.popupMsgDialog(ZaMsg.FAILED_RENAME_ACCOUNT_1, ex, true);
					/*this._msgDialog.setMessage(ZaMsg.FAILED_RENAME_ACCOUNT_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
					this._msgDialog.popup();*/
				} else {
					this.popupMsgDialog(ZaMsg.FAILED_RENAME_ACCOUNT, ex, true);
				/*
					this._msgDialog.setMessage(ZaMsg.FAILED_RENAME_ACCOUNT, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
					this._msgDialog.popup();*/
				}
			}
			return false;
		}
	}

	if(!ZaAccount.checkValues(tmpObj, this._app))
		return false;
	
	//change password if new password is provided
	if(tmpObj.attrs[ZaAccount.A_password]!=null && tmpObj[ZaAccount.A2_confirmPassword]!=null && tmpObj.attrs[ZaAccount.A_password].length > 0) {
		try {
			this._currentObject.changePassword(tmpObj.attrs[ZaAccount.A_password]);
		} catch (ex) {
			/*var detailStr = "";
			for (var prop in ex) {
				detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
			}
			this._msgDialog.setMessage(ZaMsg.FAILED_SAVE_ACCOUNT, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
			this._msgDialog.popup();
			*/
			this.popupMsgDialog(ZaMsg.FAILED_SAVE_ACCOUNT, ex, true);
			return false;				
			
		}
	}
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if(a == ZaAccount.A_password || a==ZaAccount.A_zimbraMailAlias || a == ZaItem.A_objectClass || a==ZaAccount.A2_mbxsize || a==ZaAccount.A_mail || a == ZaItem.A_zimbraId) {
			continue;
		}	
		//check if the value has been modified
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] == "")) {
			if(a==ZaAccount.A_uid) {
				continue; //skip uid, it is changed throw a separate request
			}
			if(tmpObj.attrs[a] instanceof Array) {
				if(tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}

	//save changed fields
	try {	
		this._currentObject.modify(mods);
	} catch (ex) {
		if (ex.code == AjxCsfeException.SVC_AUTH_EXPIRED || ex.code == AjxCsfeException.SVC_AUTH_REQUIRED || ex.code == AjxCsfeException.NO_AUTH_TOKEN) {
				this._showLoginDialog();
		} else {
/*			var detailStr = "";
			for (var prop in ex) {
				detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
			}
*/			
			if(ex.code == AjxCsfeException.ACCT_EXISTS) {
				this.popupMsgDialog(ZaMsg.FAILED_CREATE_ACCOUNT_1, ex, true);
	/*			this._msgDialog.setMessage(ZaMsg.FAILED_CREATE_ACCOUNT_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);				
				this._msgDialog.popup();*/
			} else {
				this.popupMsgDialog(ZaMsg.FAILED_SAVE_ACCOUNT, ex, true);			
/*				this._msgDialog.setMessage(ZaMsg.FAILED_SAVE_ACCOUNT, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
				this._msgDialog.popup();*/
			}
		}
		return false;
	}
	//add-remove aliases
	var tmpObjCnt = -1;
	var currentObjCnt = -1;
	if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias]) {
		if(typeof tmpObj.attrs[ZaAccount.A_zimbraMailAlias] == "string") {
			var tmpStr = tmpObj.attrs[ZaAccount.A_zimbraMailAlias];
			tmpObj.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
			tmpObj.attrs[ZaAccount.A_zimbraMailAlias].push(tmpStr);
		}
		tmpObjCnt = tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length - 1;
	}
	
	if(this._currentObject.attrs[ZaAccount.A_zimbraMailAlias]) {
		if(typeof this._currentObject.attrs[ZaAccount.A_zimbraMailAlias] == "string") {
			var tmpStr = this._currentObject.attrs[ZaAccount.A_zimbraMailAlias];
			this._currentObject.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
			this._currentObject.attrs[ZaAccount.A_zimbraMailAlias].push(tmpStr);
		}
		currentObjCnt = this._currentObject.attrs[ZaAccount.A_zimbraMailAlias].length - 1;
	}

	//diff two arrays
	for(var tmpIx=tmpObjCnt; tmpIx >= 0; tmpIx--) {
		for(var currIx=currentObjCnt; currIx >=0; currIx--) {
			if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][tmpIx] == this._currentObject.attrs[ZaAccount.A_zimbraMailAlias][currIx]) {
				//this alias already exists
				tmpObj.attrs[ZaAccount.A_zimbraMailAlias].splice(tmpIx,1);
				this._currentObject.attrs[ZaAccount.A_zimbraMailAlias].splice(currIx,1);
				break;
			}
		}
	}
	//remove the aliases 
	if(currentObjCnt != -1) {
		currentObjCnt = this._currentObject.attrs[ZaAccount.A_zimbraMailAlias].length;
	} 
	try {
		for(var ix=0; ix < currentObjCnt; ix++) {
			this._currentObject.removeAlias(this._currentObject.attrs[ZaAccount.A_zimbraMailAlias][ix]);
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);
		return false;
	}
	if(tmpObjCnt != -1) {
		tmpObjCnt = tmpObj.attrs[ZaAccount.A_zimbraMailAlias].length;
	}
	var failedAliases = "";
	var failedAliasesCnt = 0;
	try {
		for(var ix=0; ix < tmpObjCnt; ix++) {
			try {
				if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix])
					this._currentObject.addAlias(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
			} catch (ex) {
				if(ex.code == AjxCsfeException.ACCT_EXISTS) {
					//if failed because account exists just show a warning
					failedAliases += ("<br>" + tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
					failedAliasesCnt++;
				} else {
					//if failed for another reason - jump out
					throw (ex);
				}
			}
		}
		if(failedAliasesCnt == 1) {
			this._msgDialog.setMessage(ZaMsg.WARNING_ALIAS_EXISTS + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
			this._msgDialog.popup();			
		} else if(failedAliasesCnt > 1) {
			this._msgDialog.setMessage(ZaMsg.WARNING_ALIASES_EXIST + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
			this._msgDialog.popup();			
		}
	} catch (ex) {
		/*for (var prop in ex) {
			detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
		}*/
			
		/*this._msgDialog.setMessage(ZaMsg.FAILED_ADD_ALIASES, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._msgDialog.popup();
		*/
		this.popupMsgDialog(ZaMsg.FAILED_ADD_ALIASES, ex, true);	
		return false;
	}
	return true;
}
/**
*	@method _setView 
*	@param entry - isntance of ZaAccount class
*/
ZaAccountViewController.prototype._setView =
function(entry) {
	try {

		if(!this._UICreated) {
	   		this._ops = new Array();
	 		this._ops.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.TBB_New, ZaMsg.ALTBB_New_tt, ZaImg.I_ACCOUNT, ZaImg.I_ACCOUNT, new AjxListener(this, ZaAccountViewController.prototype._newButtonListener)));   			    	
   			this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, ZaImg.I_SAVE, ZaImg.ID_SAVE, new AjxListener(this, ZaAccountViewController.prototype._saveButtonListener)));
   			this._ops.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, ZaImg.I_UNDO, ZaImg.I_UNDO, new AjxListener(this, ZaAccountViewController.prototype._closeButtonListener)));    	
   			this._ops.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ALTBB_Delete_tt, ZaImg.I_DELETE, ZaImg.I_DELETE, new AjxListener(this, ZaAccountViewController.prototype._deleteButtonListener)));    	    	
			this._toolBar = new ZaToolBar(this._container, this._ops);
	
	  		//this._view = new ZaAccountView(this._container, this._app, entry.id);
	  		this._view = new ZaAccountXFormView(this._container, this._app);
	    	this._app.createView(ZaAccountViewController.VIEW, [this._toolBar, this._view]);
	    	this._UICreated = true;
  		}
		this._app.pushView(ZaAccountViewController.VIEW);
		if(entry.id) {
			try {
				entry.refresh(false);
			} catch (ex) {
				// Data corruption may cause anexception. We should catch it here in order to display the form anyway.
				this._handleException(ex, null, null, false);
			}
		}
		this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);
		if(!entry.id) {
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(false);  			
	//		this._toolBar.getButton(ZaOperation.CHNG_PWD).setEnabled(false);  						
		} else {
			this._toolBar.getButton(ZaOperation.DELETE).setEnabled(true);  				
//			this._toolBar.getButton(ZaOperation.CHNG_PWD).setEnabled(true);  							
		}	
		this._view.setDirty(false);
		entry.attrs[ZaAccount.A_password] = null; //get rid of VALUE-BLOCKED
		entry[ZaModel.currentTab] = "1"
		this._view.setObject(entry);
//	  	this._view.setObject(entry, this._cosChanged, this._domainsChanged);
		this._currentObject = entry;
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._setView", null, false);
	}	
	this._cosChanged = false;
	this._domainsChanged = false;
	
}

/**
* @param params		   - params["params"] - arguments to pass to the method specified in func parameter
* 					     params["obj"] - the controller of the next view
*						 params["func"] - the method to call on the nextViewCtrlr in order to navigate to the next view
* This method saves changes in the current view and calls the method on the controller of the next view
**/
ZaAccountViewController.prototype._saveAndGoAway =
function (params) {
	try {
		this._confirmMessageDialog.popdown();			
		if(this._saveChanges()) {
			params["func"].call(params["obj"], params["params"]);	
		}
	} catch (ex) {
		//if exception thrown - don' go away
		if(ex.code == AjxCsfeException.ACCT_EXISTS) {
			this._msgDialog.setMessage(ZaMsg.ERROR_ACCOUNT_EXISTS, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._msgDialog.popup();
		} else {
			var mods = null;
			if(ex.mods) {
				mods = ex.mods;
			}
			this._handleException(ex, "ZaAccountViewController.prototype._saveAndGoAway", mods, false);
		}
	}
}

/**
* Leaves current view without saving any changes
**/
ZaAccountViewController.prototype._discardAndGoAway = 
function (params) {
	this._confirmMessageDialog.popdown();
	params["func"].call(params["obj"], params["params"]);		
}

/**
* deletes current ZaAccount and leaves the view
**/
ZaAccountViewController.prototype._deleteAndGoAway = 
function () {
	try {
		if(this._currentObject.id) {
			this._currentObject.remove();
			this._fireAccountRemovalEvent(this._currentObject);
		}
		this._app.getAccountListController().show();
		this._confirmMessageDialog.popdown();	

	} catch (ex) {
		this._confirmMessageDialog.popdown();	
		if(ex.code == AjxCsfeException.SVC_WRONG_HOST) {
			var szMsg = ZaMsg.ERROR_WRONG_HOST;
			if(ex.detail) {
				szMsg +="<br>Details:<br>";
				szMsg += ex.detail;
			}
			this._msgDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._msgDialog.popup();					
		} else {
			this._handleException(ex, "ZaAccountViewController.prototype._deleteAndGoAway", null, false);				
		}	
	}
}


ZaAccountViewController.prototype._closeCnfrmDlg = 
function () {
	this._confirmMessageDialog.popdown();	
}

//toolbar button listeners 

/**
* @param 	ev event object
* This method handles "save" button click
**/
ZaAccountViewController.prototype._saveButtonListener =
function(ev) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);
			if(this._toolBar)
				this._toolBar.getButton(ZaOperation.SAVE).setEnabled(false);		
		
			this._currentObject.refresh(false);	
			this._view.setObject(this._currentObject);			
			this._fireAccountChangeEvent();			
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._saveButtonListener", null, false);
	}
	return;
}

/**
* handles the Close button click. Returns to the list view.
**/ 
ZaAccountViewController.prototype._closeButtonListener =
function(ev) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this._app.getAccountListController();
		args["func"] = ZaAccountListController.prototype.show;
		//ask if the user wants to save changes		
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._confirmMessageDialog.setMessage("Do you want so save current changes?", null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaAccountViewController.prototype._saveAndGoAway, this, args);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaAccountViewController.prototype._discardAndGoAway, this, args);		
		this._confirmMessageDialog.popup();
	} else {
		this._app.getAccountListController().show();

	}	
}

/**
* This listener is called when the Delete button is clicked. 
**/
ZaAccountViewController.prototype._deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		this._confirmMessageDialog = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);						
		this._confirmMessageDialog.setMessage("Are you sure you want to delete this account?", null, DwtMessageDialog.INFO_STYLE);
		this._confirmMessageDialog.registerCallback(DwtDialog.YES_BUTTON, ZaAccountViewController.prototype._deleteAndGoAway, this, null);		
		this._confirmMessageDialog.registerCallback(DwtDialog.NO_BUTTON, ZaAccountViewController.prototype._closeCnfrmDlg, this, null);				
		this._confirmMessageDialog.popup();
	} else {
		this._app.getAccountListController().show();
	}
}


// new button was pressed
ZaAccountViewController.prototype._newButtonListener =
function(ev) {
	try {
		var newAccount = new ZaAccount(this._app);
		this._newAccountWizard = new ZaNewAccountXWizard(this._container, this._app);	
		this._newAccountWizard.setObject(newAccount);
		this._newAccountWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._newButtonListener", null, false);
	}
}

//event notifiers

/**
*	Private method that notifies listeners to that the controlled ZaAccount is changed
* 	@param details
*/
ZaAccountViewController.prototype._fireAccountChangeEvent =
function() {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_MODIFY)) {
			var evt = new ZaEvent(ZaEvent.S_ACCOUNT);
			evt.set(ZaEvent.E_MODIFY, this);
			this._evtMgr.notifyListeners(ZaEvent.E_MODIFY, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._fireAccountChangeEvent", null, false);	
	}
		
}

/**
*	Private method that notifies listeners to that the controlled ZaAccount is removed
* 	@param details
*/
ZaAccountViewController.prototype._fireAccountRemovalEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_REMOVE)) {
			var evt = new ZaEvent(ZaEvent.S_ACCOUNT);
			evt.set(ZaEvent.E_REMOVE, this);
			evt.setDetails(details);
			this._evtMgr.notifyListeners(ZaEvent.E_REMOVE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._fireAccountRemovalEvent", details, false);	
	}
}

/**
*	Private method that notifies listeners that a new ZaAccount is created
* 	@param details
*/
ZaAccountViewController.prototype._fireAccountCreationEvent =
function(details) {
	try {
		if (this._evtMgr.isListenerRegistered(ZaEvent.E_CREATE)) {
			var evt = new ZaEvent(ZaEvent.S_ACCOUNT);
			evt.set(ZaEvent.E_CREATE, this);
			if(details)
				evt.setDetails(details);
				
			this._evtMgr.notifyListeners(ZaEvent.E_CREATE, evt);
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype.__fireAccountCreationEvent", details, false);	
	}

}

