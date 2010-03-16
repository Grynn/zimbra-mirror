/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaXFormViewController base class for all Za***ViewControllers (for XForm views only)
* @extends ZaController
* @contructor 
* @param appCtxt
* @param container
* @param app
* @param iKeyName
* @author Greg Solovyev
* @see ZaAccountViewController
* @see ZaCosController
* @see ZaDomainController
* @see ZaGlobalConfigViewController
**/

ZaXFormViewController = function(appCtxt, container,iKeyName) {
	if (arguments.length == 0) return;
	this._currentObject = null;
	ZaController.call(this, appCtxt, container,iKeyName);
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
	this._toolbarOrder = new Array();
	this._toolbarOperations = new Array();
}

ZaXFormViewController.prototype = new ZaController();
ZaXFormViewController.prototype.constructor = ZaXFormViewController;

/**
* A stack of validation methods. These methods are called before calling @link ZaXFormViewController.prototype._saveChanges method.
**/
ZaXFormViewController.preSaveValidationMethods = new Object();

ZaXFormViewController.prototype.show = 
function(entry) {
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
	}
}

ZaXFormViewController.prototype.handleXFormChange = function (ev) {
	if(ev && ev.form.hasErrors() && this._toolbar && this._toolbar.getButton(ZaOperation.SAVE)) { 
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
	}
}
/**
* Method that notifies listeners to that the controlled ZaAccount is removed
* @param details {String}
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

//Listeners for default toolbar buttons (close, save, delete)
/**
* member of ZaXFormViewController
* @param 	ev event object
* 			noPopView - It should be set to true when close a hidden tab
* handles the Close button click. Returns to the list view.
**/ 
ZaXFormViewController.prototype.closeButtonListener =
function(ev, noPopView, func, obj, params) {
	//prompt if the user wants to save the changes
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();
		if (noPopView) {
			args["obj"] = obj ;
			args["func"] = func ;
			args["params"] = params ;
		}else{
			args["obj"] = ZaApp.getInstance();		
			args["params"] = null;
			args["func"] = ZaApp.prototype.popView;
		}
		//ask if the user wants to save changes		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else if (noPopView){
		func.call(obj, params) ;
	}else{
		if(this._view._localXForm && this._view.formDirtyLsnr) {
			this._view._localXForm.removeListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE,this._view.formDirtyLsnr);
			this._view._localXForm.removeListener(DwtEvent.XFORMS_VALUE_ERROR,this._view.formDirtyLsnr);
		}
		//this._app.getTabGroup().removeCurrentTab(true) ;
		ZaApp.getInstance().popView();
		//ZaApp.getInstance().getTabGroup().removeCurrentTab(true) ;
	}
}

/**
* This listener is called when the Delete button is clicked. 
* member of ZaXFormViewController
* @param 	ev event object
**/
ZaXFormViewController.prototype.deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(this.deleteMsg, DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.deleteAndGoAway, this, null);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else {
		ZaApp.getInstance().popView();
	}
}

/**
* This method handles "save" button click
* member of ZaXFormViewController
* @param 	ev event object
**/
ZaXFormViewController.prototype.saveButtonListener =
function(ev) {
	try {
		this.validateChanges();
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.saveButtonListener", null, false);
	}
	return;
}

ZaXFormViewController.prototype._saveChanges = function() {
	return true;
}
/**
* member of ZaXFormViewController
* @param params
* This method saves the object in the form and calls method specified in params["func"]
**/
ZaXFormViewController.prototype.saveAndGoAway =
function (params) {
	try {
		this.closeCnfrmDlg();		
		if(this._saveChanges()) {
			this.fireChangeEvent(this._currentObject);			
			params["func"].call(params["obj"], params["params"]);	
			//ZaApp.getInstance().getTabGroup().removeCurrentTab(true) ;
		}
	} catch (ex) {
		this._handleException(ex, ZaXFormViewController.prototype.saveAndGoAway, null, false);
	}
}

/**
* member of ZaXFormViewController
* @param params
* This method deletes the object in the form and closes the form
**/
ZaXFormViewController.prototype.deleteAndGoAway = 
function () {
	try {
		if(this._currentObject.id) {
			this._currentObject.remove();
			this.fireRemovalEvent(this._currentObject);
		}
		this.closeCnfrmDlg();	
		ZaApp.getInstance().popView();		
		//ZaApp.getInstance().getTabGroup().removeCurrentTab(true) ;	
	} catch (ex) {
		this.closeCnfrmDlg();	
		this._handleException(ex, "ZaXFormViewController.prototype.deleteAndGoAway", null, false);				
	}
}

/**
* member of ZaXFormViewController
* Leaves current view without saving any changes
**/
ZaXFormViewController.prototype.discardAndGoAway = 
function (params) {
	this.closeCnfrmDlg();
	params["func"].call(params["obj"], params["params"]);		
}


ZaXFormViewController.prototype.runValidationStack = 
function(params) {
	try {
		var cnt = this.validationStack.length;
		if(cnt>0) {
			var method = this.validationStack[cnt-1];
			this.validationStack.pop();
			method.call(this,params);
		} else {
			this._saveChangesCallback(params);
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.runValidationStack", null, false);
	}
}

/**
* @param params - optional, params that contain a callback function 
* that will be called if the user answers "Yes",
* an argument for the callback function,
* and an object on which this function will be called
**/
ZaXFormViewController.prototype.validateChanges =
function (params) {
	//check if we are removing volumes
	this.validationStack = [];
	if(ZaXFormViewController.preSaveValidationMethods[this._iKeyName]) {
		var cnt=ZaXFormViewController.preSaveValidationMethods[this._iKeyName].length;
		if(cnt>0) {
			for(var i=0;i<cnt;i++) {
				this.validationStack.push(ZaXFormViewController.preSaveValidationMethods[this._iKeyName][i]);
			}
		}
	}
	this.runValidationStack(params);
}

/**
* @param params - optional, contains parameters for the next call
**/
ZaXFormViewController.prototype._saveChangesCallback = 
function (params) {
	try {
		if(this._saveChanges()) {
			this._view.setDirty(false);
			if(this._toolbar)
				this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);		
		
			this.closeCnfrmDlg();

            if (typeof (this.changeActionsState) == "function") { //update the toolbar buttons
                this.changeActionsState ();
            }

			this._currentObject.refresh(false,true);	
			this.fireChangeEvent(this._currentObject);		
			if(params) {
				params["func"].call(params["obj"], params["params"]);
			} else {
				this._view.setObject(this._currentObject);			
			}
		}
	} catch (ex) {
		//if exception thrown - don't go away
		this._handleException(ex, "ZaXFormViewController.prototype._saveChangesCallback", null, false);
	}	
}


/**
* member of ZaXFormViewController
* enables/disables "Save" button on this Form's toolbar
* if there is no Save button, the method just returns
* @param isD {Boolean}
**/
ZaXFormViewController.prototype.setDirty = 
function (isD) {
	if(!this._toolbar || !this._toolbar.getButton(ZaOperation.SAVE))
		return;
		
	if(isD)
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(true);
	else
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
}

/**
*	@method setViewMethod 
*	@param entry - data object
*/
ZaXFormViewController.setViewMethod =
function(entry) {
	if(entry.load)
		entry.load();
		
	if(!this._UICreated) {
		this._createUI();
	} 
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}

/**
* @method _createUI
**/
ZaXFormViewController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
	
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}

ZaXFormViewController.prototype._findAlias = function (alias) {
    var types = [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES] ; 
    var searchQuery = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(alias, types), types, null, false);
	// this search should only return one result
	var results = ZaSearch.searchByQueryHolder(searchQuery, 1, null, null);
	return results.list.getArray()[0];
};

