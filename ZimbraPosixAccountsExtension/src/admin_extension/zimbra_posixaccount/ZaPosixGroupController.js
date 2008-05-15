/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaPosixGroupController controls display of a single Server
* @contructor ZaPosixGroupController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

function ZaPosixGroupController(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app,"ZaPosixGroupController");
	this._UICreated = false;
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_servers/managing_servers.htm";				
	this._toolbarOperations = new Array();
	this.deleteMsg = "Are you sure you want to delete this group?";	
	this.objType = ZaEvent.S_SERVER;	
}

ZaPosixGroupController.prototype = new ZaXFormViewController();
ZaPosixGroupController.prototype.constructor = ZaPosixGroupController;

ZaController.initToolbarMethods["ZaPosixGroupController"] = new Array();
ZaController.setViewMethods["ZaPosixGroupController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaPosixGroup class
*/
ZaPosixGroupController.prototype.show = 
function(entry) {
	this._setView(entry);
	this.setDirty(false);
}


ZaPosixGroupController.prototype.setEnabled = 
function(enable) {
	//this._contentView.setEnabled(enable);
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaPosixGroupController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	if(this._contentView.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = params;
		args["obj"] = nextViewCtrlr;
		args["func"] = func;
		//ask if the user wants to save changes			
		//this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._contentView.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
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
ZaPosixGroupController.initToolbarMethod = 
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.SERTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, this._newButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
	
}
ZaController.initToolbarMethods["ZaPosixGroupController"].push(ZaPosixGroupController.initToolbarMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaPosixGroup class
*/
ZaPosixGroupController.setViewMethod =
function(entry) {
	if(entry.name)
		entry.load("name", entry.name);
		
	if(!this._UICreated) {
		this._createUI();
	} 
	this._app.pushView(this.getContentViewId());
//	this._app.pushView(ZaZimbraAdmin._POSIX_GROUP_VIEW);
	this._contentView.setDirty(false);
	this._contentView.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaPosixGroupController"].push(ZaPosixGroupController.setViewMethod);

/**
* @method _createUI
**/
ZaPosixGroupController.prototype._createUI =
function () {
	this._contentView = this._view = new ZaPosixGroupXFormView(this._container, this._app);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
//    this._app.createView(ZaZimbraAdmin._POSIX_GROUP_VIEW, elements);
    var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}  	
    this._app.createView(this.getContentViewId(), elements, tabParams) 
    this._app._controllers[this.getContentViewId ()] = this ;
	this._UICreated = true;
}

ZaPosixGroupController.prototype._saveChanges =
function () {
	//check if the XForm has any errors
	if(this._contentView.getMyForm().hasErrors()) {
		var errItems = this._contentView.getMyForm().getItemsInErrorState();
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
				if(label.substring(label.length-1,1)==":") {
					label = label.substring(0, label.length-1);
				}
			}
			if(label) {
				dlgMsg += "<li>";
				dlgMsg +=label;			
				dlgMsg += "</li>";
			}
			i++;
		}
		dlgMsg += "</ul>";
		this.popupMsgDialog(dlgMsg,  true);
		return false;
	}
	
	//check if the data is copmlete 
	var obj = this._contentView.getObject();
	var isNew = false;

	if(!obj.id)
		isNew = true;
	
	//transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaZambaDomain
	var mods = new Object();
	
	for (var a in obj.attrs) {
		if (this._currentObject.attrs[a] != obj.attrs[a] ) {
			mods[a] = obj.attrs[a];
		}
	}

	try {	
		if(isNew) {
			this._currentObject = ZaItem.create(obj, ZaPosixGroup, "ZaPosixGroup", this._app);
			this.fireCreationEvent(this._currentObject);
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);	
		} else {
			this._currentObject.modify(mods);
			this.fireChangeEvent(this._currentObject);
		}
	} catch (ex) {
		var detailStr = "";
		for (var prop in ex) {
			if(ex[prop] instanceof Function) 
				continue;
				
			detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
		}
		this._handleException(ex, "ZaPosixGroupController.prototype._saveChanges", null, false);	
		return false;
	}
		
	this._contentView.setDirty(false);	
	return true;
}

ZaPosixGroupController.prototype.newPosixGroup = 
function () {
	var newPosixGroup = new ZaPosixGroup(this._app);
	this._setView(newPosixGroup);
}

// new button was pressed
ZaPosixGroupController.prototype._newButtonListener =
function(ev) {
	if(this._contentView.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this;
		args["func"] = ZaPosixGroupController.prototype.newPosixGroup;
		//ask if the user wants to save changes		
		this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._contentView.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		this.newPosixGroup();
	}	
}
