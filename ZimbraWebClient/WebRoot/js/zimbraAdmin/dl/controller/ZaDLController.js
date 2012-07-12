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
* @class ZaDLController controls display of a single Distribution list
 * @author EMC
 * Distribution list controller 
 */
ZaDLController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaDLController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/distribution_lists.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaDLController.helpButtonText;
	this.deleteMsg = ZaMsg.Q_DELETE_DL;
	this.objType = ZaEvent.S_ACCOUNT;
	this.tabConstructor = ZaDLXFormView;
	this._removeAliasArr = [];
	this._addAliasArr = [];		
}

ZaDLController.prototype = new ZaXFormViewController();
ZaDLController.prototype.constructor = ZaDLController;
ZaDLController.helpButtonText = ZaMsg.helpEditDL;


ZaController.initToolbarMethods["ZaDLController"] = new Array();
ZaController.initPopupMenuMethods["ZaDLController"] = new Array();
ZaController.setViewMethods["ZaDLController"] = [];
ZaController.changeActionsStateMethods["ZaDLController"] = new Array();

ZaDLController.prototype.toString = function () {
	return "ZaDLController";
};


ZaDLController.prototype.show = 
function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
	//if it has many members, that could slow down the performance when loading members, so make the get members request as async
	entry.schedulePostLoading(this);
}

ZaDLController.setViewMethod =
function (entry)	{
	try {
        var skipRight = false ;       
        if (entry.id == null) { //it is a new distribution list, ignore the right checking.
            skipRight = true ;
        }
	    entry.refresh (skipRight,true) ;
		this._createUI(entry);
		ZaApp.getInstance().pushView(this.getContentViewId());
		this._view.setDirty(false);
		entry[ZaModel.currentTab] = "1";
		this._view.setObject(entry);
		this._currentObject = entry;
	} catch (ex) {
		this._handleException(ex, "ZaDLController.prototype.show", null, false);
	}	
};
ZaController.setViewMethods["ZaDLController"].push(ZaDLController.setViewMethod);
                            

ZaDLController.changeActionsStateMethod = function () {
    var isToEnable = (this._view && this._view.isDirty());

    if(this._popupOperations[ZaOperation.SAVE]) {
        this._popupOperations[ZaOperation.SAVE].enabled = isToEnable;
    }

    if(!this._currentObject.id || !ZaItem.hasRight(ZaAccount.RIGHT_DELETE_DL,this._currentObject)) {
        this._popupOperations[ZaOperation.DELETE].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaDLController"].push(ZaDLController.changeActionsStateMethod);

ZaDLController.initToolbarMethod =
function () {
}
ZaController.initToolbarMethods["ZaDLController"].push(ZaDLController.initToolbarMethod);

ZaDLController.initPopupMenuMethod =
function () {
	var showNewDL = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showNewDL = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_DL,domainList[i])) {
				showNewDL = true;
				break;
			}
		}
	}
   	this._popupOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
   	this._popupOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));
   	//this._popupOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
   	if(showNewDL) {
		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.DLTBB_New_tt, "DistributionList", "DistributionListDis", new AjxListener(this, this.newButtonListener, [true]));
   	}
   	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.DLTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));

   	if(showNewDL) {
		this._popupOrder.push(ZaOperation.NEW);
   	}
	this._popupOrder.push(ZaOperation.DELETE);
	this._popupOrder.push(ZaOperation.SAVE);
	this._popupOrder.push(ZaOperation.CLOSE);
	this._popupOrder.push(ZaOperation.SEP);
}
ZaController.initPopupMenuMethods["ZaDLController"].push(ZaDLController.initPopupMenuMethod);

ZaDLController.prototype.getPopUpOperation =
function() {
    return this._popupOperations;
}
/*
ZaDLController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
    	this._appbarOperation[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
        this._appbarOperation[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "", "", new AjxListener(this, this.saveButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaDLController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
    	this._appbarOrder.push(ZaOperation.HELP);
        this._appbarOrder.push(ZaOperation.SAVE);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
}*/

ZaDLController.prototype.newDl = function () {
	var newDL = new ZaDistributionList();
	this.show(newDL);
}

// new button was pressed
ZaDLController.prototype.newButtonListener =
function(openInNewTab, ev) {
	if (openInNewTab) {
		ZaAccountListController.prototype._newDistributionListListener.call (this) ;
	}else{
		if(this._view.isDirty()) {
			//parameters for the confirmation dialog's callback 
			var args = new Object();		
			args["params"] = null;
			args["obj"] = this;
			args["func"] = ZaDLController.prototype.newDl;
			//ask if the user wants to save changes		
			//ZaApp.getInstance().dialogs["confirmMessageDialog"] = ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON]);								
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
		} else {
			this.newDl();
		}	
	}
}

//private and protected methods
ZaDLController.prototype._createUI = 
function (entry) {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = this._view = new this.tabConstructor(this._container, entry);

    this._initPopupMenu();
	//always add Help button at the end of the toolbar    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
    ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
	this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON],
	null, ZaId.CTR_PREFIX + ZaId.VIEW_DL + "_removeConfirm");			
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId()] = this ;
}

ZaDLController.prototype._saveChanges = function () {
	var retval = false;
	var newName = null;
	var obj;
	try { 
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
				if (!label && errItems[key].getLabel()) {
					label = errItems[key].getLabel();
				} else if(!label && errItems[key].getParentItem()) { //this might be a part of a composite
					if(errItems[key].getParentItem().getInheritedProperty("msgName")) {
						label = errItems[key].getParentItem().getInheritedProperty("msgName");
					} else {
						label = errItems[key].getParentItem().getLabel();
					}
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
			this.popupMsgDialog(dlgMsg, true);
			return false;
		}
		obj = this._view.getObject();
			
		if(!ZaDistributionList.checkValues(obj))
			return retval;

        if (this._currentObject[ZaModel.currentTab]!= obj[ZaModel.currentTab])
             this._currentObject[ZaModel.currentTab] = obj[ZaModel.currentTab];

		if (this._currentObject.id){
			this._currentObject.schedulePostLoading(this);
			this._currentObject.modify(null, obj);
			//check if need to rename
			if(this._currentObject && obj.name != this._currentObject.name && this._currentObject.id) {
				newName = obj.name;
			}		
					
			//check if need to rename
			if(newName) {
				try {
					this._currentObject.rename(newName);
				} catch (ex) {
					if(ex.code == ZmCsfeException.DISTRIBUTION_LIST_EXISTS) {
						this.popupErrorDialog(ZaMsg.FAILED_RENAME_DL_1, ex);
					} else {
						this.popupErrorDialog(ZaMsg.FAILED_RENAME_DL, ex);	
					}
					return retval;
				}
			}
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DLModified,[this._currentObject.name]));
		} else {
			this._currentObject = ZaItem.create(obj,ZaDistributionList,"ZaDistributionList");
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DLCreated,[this._currentObject.name]));
			//this._currentObject.id = dl.id;
            ZaApp.getInstance().getAccountListController().fireCreationEvent(this._currentObject);
		}
				
		//save changed fields
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS || ex.code == ZmCsfeException.DISTRIBUTION_LIST_EXISTS) {
			this.popupErrorDialog(ZaMsg.ERROR_dlWithThisNameExists, ex);
		} else {
			this._handleException(ex, "ZaDLController.prototype._saveChanges", null, false);	
		}
		return false;
	}		
	return true;
};

