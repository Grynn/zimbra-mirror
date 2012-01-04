/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaCosController controls display of a single COS
* @contructor ZaCosController
* @param appCtxt
* @param container
* @param abApp
**/

ZaCosController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaCosController");
	this._UICreated = false;	
	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaCosController.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
	this._helpButtonText = ZaCosController.helpButtonText;
	this.deleteMsg = ZaMsg.Q_DELETE_COS;
	this.objType = ZaEvent.S_COS;
	this.tabConstructor = ZaCosXFormView;
}
ZaCosController.helpURL = "cos/creating_classes_of_service.htm";
ZaCosController.helpButtonText = ZaMsg.helpEditCOS;
ZaCosController.prototype = new ZaXFormViewController();
ZaCosController.prototype.constructor = ZaCosController;
ZaController.initToolbarMethods["ZaCosController"] = new Array();
ZaController.initPopupMenuMethods["ZaCosController"] = new Array();
ZaController.setViewMethods["ZaCosController"] = new Array();
ZaController.changeActionsStateMethods["ZaCosController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaCos class
*/

ZaCosController.prototype.show = 
function(entry) {
	//check if the tab with the same cos ei
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
	}
}

ZaCosController.changeActionsStateMethod = function () {
	if(this._currentObject.name == "default") {
		this._toolbarOperations[ZaOperation.DELETE].enabled = false;
        this._popupOperations[ZaOperation.DELETE].enabled = false;
	} else if(!ZaItem.hasRight(ZaCos.DELETE_COS_RIGHT,this._currentObject))	{
		this._toolbarOperations[ZaOperation.DELETE].enabled = false;
        this._popupOperations[ZaOperation.DELETE].enabled = false;
	}
	

	if(this._toolbarOperations[ZaOperation.SAVE])
		this._toolbarOperations[ZaOperation.SAVE].enabled = false;
		
}
ZaController.changeActionsStateMethods["ZaCosController"].push(ZaCosController.changeActionsStateMethod);

ZaCosController.initPopupMenuMethod =
function () {
	this._popupOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.COSTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));

	if(ZaItem.hasRight(ZaCos.CREATE_COS_RIGHT, ZaZimbraAdmin.currentAdminAccount)) {
		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, ZaCosController.prototype._newButtonListener, [true]));
	}
	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
}
ZaController.initPopupMenuMethods["ZaCosController"].push(ZaCosController.initPopupMenuMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaCos class
*/
ZaCosController.setViewMethod =
function(entry) {
	try {
		entry[ZaModel.currentTab] = "1"
		if(entry.id)
			entry.load("id", entry.id);
			
		this._currentObject = entry;
		
         //create toolbar
		this._initToolbar();
        this._initPopupMenu();
		//always add Help button at the end of the toolbar		
		this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
		this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
		this._toolbarOrder.push(ZaOperation.NONE);
		this._toolbarOrder.push(ZaOperation.HELP);	
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_COS);

	  	this._contentView = this._view = new this.tabConstructor(this._container,  entry);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
        if (!appNewUI) {
            elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

            var tabParams = {
                openInNewTab: true,
                tabId: this.getContentViewId()
            }
            ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
        }
        else
            ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;

		ZaApp.getInstance().pushView(this.getContentViewId());
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
		if(!entry.id || (entry.name == "default")) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
		}	
		this._view.setDirty(false);
	  	this._view.setObject(entry);

	} catch (ex) {
		this._handleException(ex, "ZaCosController.prototype._setView", null, false);	
	}
	
}
ZaController.setViewMethods["ZaCosController"].push(ZaCosController.setViewMethod);

/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaCosController.initToolbarMethod = 
function () {
	this._toolbarOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.COSTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
	this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.COSTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));    	
   	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	
	if(ZaItem.hasRight(ZaCos.CREATE_COS_RIGHT, ZaZimbraAdmin.currentAdminAccount)) {
		this._toolbarOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, ZaCosController.prototype._newButtonListener, [true]));		
	}	
	this._toolbarOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
	this._toolbarOrder.push(ZaOperation.SAVE);
	this._toolbarOrder.push(ZaOperation.CLOSE);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.NEW);
	this._toolbarOrder.push(ZaOperation.DELETE);			
}
ZaController.initToolbarMethods["ZaCosController"].push(ZaCosController.initToolbarMethod);

ZaCosController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
        this._appbarOperation[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "", "", new AjxListener(this, this.saveButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaCosController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
        this._appbarOrder.push(ZaOperation.SAVE);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
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
	var tmpObj = this._view.getObject();
	var isNew = false;
	//Check the data
    if(!ZaCos.checkValues(tmpObj))
			return false;


	var mods = new Object();
	//var changeDetails = new Object();
	if(!tmpObj.id)
		isNew = true;
		
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || a == ZaItem.A_zimbraId || a == ZaCos.A_zimbraAvailableSkin
                || a == ZaCos.A_zimbraZimletAvailableZimlets || a == ZaCos.A_zimbraMailHostPool
                || a == ZaItem.A_zimbraACE || a== ZaItem.A_zimbraCreateTimestamp) {
			continue;
		}
		if(!ZaItem.hasWritePermission(a,tmpObj)) {
			continue;
		}
		//check if the value has been modified or the object is new
		if (isNew || (this._currentObject.attrs[a] != tmpObj.attrs[a]) ) {
			mods[a] = tmpObj.attrs[a];
		}
	}
	//check if host pool has been changed

	if(tmpObj.attrs[ZaCos.A_zimbraMailHostPool] != null) {
		var tmpMods = [];
		if(!(tmpObj.attrs[ZaCos.A_zimbraMailHostPool] instanceof Array)) {
			tmpMods = [tmpObj.attrs[ZaCos.A_zimbraMailHostPool]];
		} else {
			var cnt = tmpObj.attrs[ZaCos.A_zimbraMailHostPool].length;
			tmpMods = [];
			for(var i = 0; i < cnt; i++) {
				tmpMods.push(tmpObj.attrs[ZaCos.A_zimbraMailHostPool][i]);
			}
		}
		//check if changed
		if(!isNew && this._currentObject.attrs[ZaCos.A_zimbraMailHostPool] != null) {
			if(this._currentObject.attrs[ZaCos.A_zimbraMailHostPool] instanceof Array) {
				if(tmpMods.join(",") != this._currentObject.attrs[ZaCos.A_zimbraMailHostPool].join(",")) {
					mods[ZaCos.A_zimbraMailHostPool] = tmpMods;
				}
			} else if (tmpMods.join(",") != [this._currentObject.attrs[ZaCos.A_zimbraMailHostPool]].join(",")) {
				mods[ZaCos.A_zimbraMailHostPool] = tmpMods;
			}
		} else {
			mods[ZaCos.A_zimbraMailHostPool] = tmpMods;
		}
	} else if(this._currentObject.attrs[ZaCos.A_zimbraMailHostPool] != null) {
		mods[ZaCos.A_zimbraMailHostPool] = "";
	}

	if(tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] != null) {
		var tmpMods = [];
		if(!(tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] instanceof Array)) {
			tmpMods = [tmpObj.attrs[ZaCos.A_zimbraAvailableSkin]];
		} else {
			var cnt = tmpObj.attrs[ZaCos.A_zimbraAvailableSkin].length;
			tmpMods = [];
			for(var i = 0; i < cnt; i++) {
				tmpMods.push(tmpObj.attrs[ZaCos.A_zimbraAvailableSkin][i]);
			}
		}
			//check if changed
		if(this._currentObject.attrs[ZaCos.A_zimbraAvailableSkin] != null) {
			if(this._currentObject.attrs[ZaCos.A_zimbraAvailableSkin] instanceof Array) {
				if(tmpMods.join(",") != this._currentObject.attrs[ZaCos.A_zimbraAvailableSkin].join(",")) {
					mods[ZaCos.A_zimbraAvailableSkin] = tmpMods;
				}
			} else if (tmpMods.join(",") != [this._currentObject.attrs[ZaCos.A_zimbraAvailableSkin]].join(",")) {
				mods[ZaCos.A_zimbraAvailableSkin] = tmpMods;
			}
		} else {
			mods[ZaCos.A_zimbraAvailableSkin] = tmpMods;
		}
	} else if(this._currentObject.attrs[ZaCos.A_zimbraAvailableSkin] != null) {
		mods[ZaCos.A_zimbraAvailableSkin] = "";
	}
		

	if(tmpObj.attrs[ZaCos.A_zimbraZimletAvailableZimlets] != null) {
		var tmpMods = [];
		if(!(tmpObj.attrs[ZaCos.A_zimbraZimletAvailableZimlets] instanceof Array)) {
			tmpMods = [tmpObj.attrs[ZaCos.A_zimbraZimletAvailableZimlets]];
		} else {
			var cnt = tmpObj.attrs[ZaCos.A_zimbraZimletAvailableZimlets].length;
			tmpMods = [];
			for(var i = 0; i < cnt; i++) {
				tmpMods.push(tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets][i]);
			}
		}
		if(isNew) {
			mods[ZaCos.A_zimbraZimletAvailableZimlets] = tmpMods;
		} else {
			//check if changed
			if(this._currentObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] != null) {
				if(this._currentObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] instanceof Array) {
					if(tmpMods.join(",") != this._currentObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets].join(",")) {
						mods[ZaCos.A_zimbraZimletAvailableZimlets] = tmpMods;
					}
				} else if (tmpMods.join(",") != [this._currentObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets]].join(",")) {
					mods[ZaCos.A_zimbraZimletAvailableZimlets] = tmpMods;
				}
			} else {
				mods[ZaCos.A_zimbraZimletAvailableZimlets] = tmpMods;
			}			
		}
	} else if(this._currentObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] != null) {
		mods[ZaCos.A_zimbraZimletAvailableZimlets] = "";
	}
	
		
	//check if need to rename
	if(!isNew) {
		if(tmpObj.name != this._currentObject.name) {
            if(this._currentObject.name=="default"||this._currentObject.name=="defaultExternal"){
                this._errorDialog.setMessage( AjxMessageFormat.format(ZaMsg.FAILED_RENAME_COS_DEFAULT,this._currentObject.name), null, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
				this._errorDialog.popup();
                return false;
            }
			newName=tmpObj.name;
			//changeDetails["newName"] = newName;
			try {
				this._currentObject.rename(newName);
			} catch (ex) {
				var detailStr = "";
				for (var prop in ex) {
					detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
				}
				if(ex.code == ZmCsfeException.COS_EXISTS) {
					this._errorDialog.setMessage(ZaMsg.FAILED_RENAME_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
					this._errorDialog.popup();
				} else {
					this._handleException(ex, "ZaCosController.prototype._saveChanges", null, false);	
				}
				return false;
			}
		}
	}

    if (this._currentObject[ZaModel.currentTab]!= tmpObj[ZaModel.currentTab])
             this._currentObject[ZaModel.currentTab] = tmpObj[ZaModel.currentTab];
	//save changed fields
	try {
		if(isNew) {
			this._currentObject.create(tmpObj.name, mods);
			//if creation took place - fire a CreationEvent
			this.fireCreationEvent(this._currentObject);
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.CosCreated,[tmpObj.name]));
		} else {
			this._currentObject.modify(mods);
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.CosModified,[this._currentObject.name]));
			//if modification took place - fire a ChangeEvent
			//changeDetails["obj"] = this._currentObject;
			//changeDetails["mods"] = mods;
			//this.fireChangeEvent(this._currentObject);
		}
	} catch (ex) {
		var detailStr = "";
		for (var prop in ex) {
			if(ex[prop] instanceof Function) 
				continue;
				
			detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
		}
		if(ex.code == ZmCsfeException.COS_EXISTS) {
			this._errorDialog.setMessage(ZaMsg.FAILED_CREATE_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);				
			this._errorDialog.popup();
		} else {
			this._handleException(ex, "ZaCosController.prototype._saveChanges", null, false);	
		}
		return false;
	}
	return true;
	
}

ZaCosController.prototype.newCos = 
function () {
	var newCos = new ZaCos();
	var defCos = ZaCos.getCosByName("default");
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) || (aname = ZaItem.A_zimbraCreateTimestamp))
			continue;			
		newCos.attrs[aname] = defCos.attrs[aname];
	}	
	this._setView(newCos, true);
}

// new button was pressed
ZaCosController.prototype._newButtonListener =
function(openInNewTab, ev) {
	if (openInNewTab) {
		ZaCosListController.prototype._newButtonListener.call (this) ;
	}else{
		if(this._view.isDirty()) {
			//parameters for the confirmation dialog's callback 
			var args = new Object();		
			args["params"] = null;
			args["obj"] = this;
			args["func"] = ZaCosController.prototype.newCos;
			//ask if the user wants to save changes		
			//ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON]);								
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
		} else {
			this.newCos();
		}	
	}
}
