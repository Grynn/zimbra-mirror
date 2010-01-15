/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
ZaRightViewController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaRightViewController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_rights/managing_rights.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._toolbarOperations = new Array();
	this.deleteMsg = ZaMsg.Q_DELETE_RIGHT;
	this.objType = ZaEvent.S_RIGHT;
	this.tabConstructor = ZaRightView ;
}

ZaRightViewController.prototype = new ZaXFormViewController();
ZaRightViewController.prototype.constructor = ZaRightViewController;

ZaController.initToolbarMethods["ZaRightViewController"] = new Array();
ZaController.setViewMethods["ZaRightViewController"] = new Array();
ZaXFormViewController.preSaveValidationMethods["ZaRightViewController"] = new Array();

/**
*	@method show
*	@param entry - isntance of ZaRight class
*/
ZaRightViewController.prototype.show =
function(entry) {
	if (! this.selectExistingTabByItemId(entry.name)){
		this._setView(entry, true);
		//this.setDirty(false);
	}
}

/**
* Adds listener to modifications in the contained ZaRight
* @param listener
**/
ZaRightViewController.prototype.addRightChangeListener =
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaRight
* @param listener
**/
ZaRightViewController.prototype.removeRightChangeListener =
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaRightViewController.prototype.switchToNextView =
function (nextViewCtrlr, func, params) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback
		var args = new Object();
		args["params"] = params;
		args["obj"] = nextViewCtrlr;
		args["func"] = func;
		//ask if the user wants to save changes
		//ZaApp.getInstance().dialogs["confirmMessageDialog"] = ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON]);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.validateChanges, this, args);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else {
		ZaController.prototype.switchToNextView.call(this, nextViewCtrlr, func, params);
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
ZaRightViewController.initToolbarMethod =
function () {
//	this._toolbarOrder.push(ZaOperation.SAVE);
//	this._toolbarOrder.push(ZaOperation.DOWNLOAD_RIGHT_CONFIG);
	this._toolbarOrder.push(ZaOperation.CLOSE);
//	this._toolbarOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.SERTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
//	this._toolbarOperations[ZaOperation.DOWNLOAD_RIGHT_CONFIG]=new ZaOperation(ZaOperation.DOWNLOAD_RIGHT_CONFIG,ZaMsg.TBB_DownloadConfig, ZaMsg.SERTBB_DownloadConfig_tt, "DownloadServerConfig", "DownloadServerConfig", new AjxListener(this, this.downloadConfigButtonListener));
	this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));
}
ZaController.initToolbarMethods["ZaRightViewController"].push(ZaRightViewController.initToolbarMethod);

/**
*	@method setViewMethod
*	@param entry - isntance of ZaDomain class
*/
ZaRightViewController.setViewMethod =
function(entry) {
	entry.load(null, null, true);
	if(!this._UICreated) {
		this._createUI();
	}
//	ZaApp.getInstance().pushView(ZaZimbraAdmin._RIGHT_VIEW);
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaRightViewController"].push(ZaRightViewController.setViewMethod);

/**
* @method _createUI
**/
ZaRightViewController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);

	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
    //ZaApp.getInstance().createView(ZaZimbraAdmin._RIGHT_VIEW, elements);
    var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}

ZaRightViewController.prototype._saveChanges =
function () {
	var obj = this._view.getObject();
	this._currentObject.modify(obj);
	this._view.setDirty(false);
	this.fireChangeEvent(this._currentObject);
	return true;
}

/**
* handles "save" button click
* calls modify on the current ZaRight
**/
ZaRightViewController.prototype.saveButtonListener =
function(ev) {
	try {
		this.validateChanges();

	} catch (ex) {
		//if exception thrown - don' go away
		this._handleException(ex, "ZaRightViewController.prototype.saveButtonListener", null, false);
	}
}
