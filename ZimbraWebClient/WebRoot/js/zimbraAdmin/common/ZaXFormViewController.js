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

function ZaXFormViewController(appCtxt, container, app, iKeyName) {
	if (arguments.length == 0) return;
	this._currentObject = null;
	ZaController.call(this, appCtxt, container, app, iKeyName);
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
}

ZaXFormViewController.prototype = new ZaController();
ZaXFormViewController.prototype.constructor = ZaXFormViewController;
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
			args["obj"] = this._app;		
			args["params"] = null;
			args["func"] = ZaApp.prototype.popView;
		}
		//ask if the user wants to save changes		
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else if (noPopView){
		func.call(obj, params) ;
	}else{
		this._app.popView();
		//this._app.getTabGroup().removeCurrentTab(true) ;
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
		this._app.dialogs["confirmMessageDialog"].setMessage(this.deleteMsg, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.deleteAndGoAway, this, null);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		this._app.popView();
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
		if(this._saveChanges()) {
			this._view.setDirty(false);
			if(this._toolbar)
				this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);		
		
			this._currentObject.refresh(false);	
			this._view.setObject(this._currentObject);			
			this.fireChangeEvent(this._currentObject);		
			EmailAddr_XFormItem.resetDomainLists.call (this) ;	
		}
	} catch (ex) {
		this._handleException(ex, "ZaXFormViewController.prototype.saveButtonListener", null, false);
	}
	return;
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
			//this._app.getTabGroup().removeCurrentTab(true) ;
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
		this._app.popView();		
		//this._app.getTabGroup().removeCurrentTab(true) ;	
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
/**
* member of ZaXFormViewController
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
		this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES,  DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		func.call(nextViewCtrlr, params);
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
//	this._app.pushView(ZaZimbraAdmin._SERVER_VIEW);
	this._app.pushView(this.getContentViewId());
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}

/**
* @method _createUI
**/
ZaXFormViewController.prototype._createUI =
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

ZaXFormViewController.prototype._findAlias = function (alias) {
	var searchQuery = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(alias), [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS, ZaSearch.RESOURCES], null, false);
	// this search should only return one result
	var results = ZaSearch.searchByQueryHolder(searchQuery, 1, null, null, this._app);
	return results.list.getArray()[0];
};

