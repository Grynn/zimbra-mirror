/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaResourceController controls display of a single resource
 * @author Charles Cao
 * resource controller 
 */  
ZaResourceController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaResourceController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/managing_resource.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaResourceController.helpButtonText;
	this.deleteMsg = ZaMsg.Q_DELETE_RES;
	this.objType = ZaEvent.S_ACCOUNT;	
	this.tabConstructor = ZaResourceXFormView;	
}

ZaResourceController.prototype = new ZaXFormViewController();
ZaResourceController.prototype.constructor = ZaResourceController;
ZaResourceController.helpButtonText = ZaMsg.helpManageResourceAccount;

ZaController.initToolbarMethods["ZaResourceController"] = new Array();
ZaController.initPopupMenuMethods["ZaResourceController"] = new Array();
ZaController.setViewMethods["ZaResourceController"] = [];
ZaController.changeActionsStateMethods["ZaResourceController"] = new Array();

ZaResourceController.prototype.toString = function () {
	return "ZaResourceController";
};

ZaResourceController.prototype.setDirty = function (isDirty) {
	this._toolbar.getButton(ZaOperation.SAVE).setEnabled(isDirty) ;
    if (appNewUI)
        ZaZimbraAdmin.getInstance().getCurrentAppBar().enableButton(ZaOperation.SAVE, isDirty);
}

ZaResourceController.prototype.handleXFormChange = function (ev) {
	if(ev && ev.form.hasErrors()) { 
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
        if (appNewUI)
            ZaZimbraAdmin.getInstance().getCurrentAppBar().enableButton(ZaOperation.SAVE, false);
	}	/*
	else if(ev && ev.formItem instanceof Dwt_TabBar_XFormItem) {	
		//do nothing - only switch the tab and it won't change the dirty status of the xform
		//this._view.setDirty (false);	
	}else if (this._UICreated){
		this._view.setDirty (true);
		//this._toolbar.getButton(ZaOperation.SAVE).setEnabled(true);
	}*/
}
ZaResourceController.prototype.show =
function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaResourceController.changeActionsStateMethod = function () {
	if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,this._currentObject))	{
		this._toolbarOperations[ZaOperation.DELETE].enabled = false;
	}

    var isToEnable = (this._view && this._view.isDirty());

    if(this._toolbarOperations[ZaOperation.SAVE])
        this._toolbarOperations[ZaOperation.SAVE].enabled = isToEnable;

    if(this._popupOperations[ZaOperation.SAVE]) {
        this._popupOperations[ZaOperation.SAVE].enabled = isToEnable;
    }
}
ZaController.changeActionsStateMethods["ZaResourceController"].push(ZaResourceController.changeActionsStateMethod);

ZaResourceController.setViewMethod =
function (entry)	{
	this._createUI(entry);
	try {
		//ZaApp.getInstance().pushView(ZaZimbraAdmin._RESOURCE_VIEW);
		ZaApp.getInstance().pushView(this.getContentViewId());
		if(!entry.id) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
			//get the calendar resource by id
			entry.load("id", entry.id, null);			
		}	
		this._view.setDirty(false);
		entry[ZaModel.currentTab] = "1" ;
	
		this._view.setObject(entry);
		//disable the save button at the beginning of showing the form
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
        if (appNewUI)
            ZaZimbraAdmin.getInstance().getCurrentAppBar().enableButton(ZaOperation.SAVE, false);
		this._currentObject = entry;
	} catch (ex) {
		this._handleException(ex, "ZaResourceController.prototype.show", null, false);
	}	
};
ZaController.setViewMethods["ZaResourceController"].push(ZaResourceController.setViewMethod);

ZaResourceController.initToolbarMethod =
function () {
	var showNewCalRes = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showNewCalRes = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_CALRES,domainList[i])) {
				showNewCalRes = true;
				break;
			}	
		}
	}		

   	this._toolbarOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
   	this._toolbarOrder.push(ZaOperation.SAVE);
   	this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));    	
   	this._toolbarOrder.push(ZaOperation.CLOSE);
   	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.SEP);
   	if(showNewCalRes) {
		this._toolbarOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", new AjxListener(this, this.newButtonListener));
		this._toolbarOrder.push(ZaOperation.NEW);
   	}
   	this._toolbarOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.RESTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
   	this._toolbarOrder.push(ZaOperation.DELETE);
	
}
ZaController.initToolbarMethods["ZaResourceController"].push(ZaResourceController.initToolbarMethod);

ZaResourceController.initPopupMenuMethod =
function () {
	var showNewCalRes = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showNewCalRes = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_CALRES,domainList[i])) {
				showNewCalRes = true;
				break;
			}
		}
	}

   	this._popupOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
   	this._popupOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));
   	if(showNewCalRes) {
		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.RESTBB_New_tt, "Resource", "ResourceDis", new AjxListener(this, this.newButtonListener));
   	}
   	this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.RESTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));

    this._popupOrder.push(ZaOperation.NEW);
    this._popupOrder.push(ZaOperation.SAVE);
    this._popupOrder.push(ZaOperation.CLOSE);
    this._popupOrder.push(ZaOperation.DELETE);
}
ZaController.initPopupMenuMethods["ZaResourceController"].push(ZaResourceController.initPopupMenuMethod);
/*
ZaResourceController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
        this._appbarOperation[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "", "", new AjxListener(this, this.saveButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaResourceController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
        this._appbarOrder.push(ZaOperation.SAVE);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
}*/

ZaResourceController.prototype.getPopUpOperation =
function() {
    return this._popupOperations;
}

ZaResourceController.prototype.newResource = function () {
	try {
		var newResource = new ZaResource();
		//newResource.getAttrs = {all:true};
		//newResource._defaultValues = {attrs:{}};	
		newResource.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		
		if(!ZaApp.getInstance().dialogs["newResourceWizard"])
			ZaApp.getInstance().dialogs["newResourceWizard"]= new ZaNewResourceXWizard(this._container);	

		ZaApp.getInstance().dialogs["newResourceWizard"].setObject(newResource);
		ZaApp.getInstance().dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaResourceController.prototype.newResource", null, false);
	}
}

// new button was pressed
ZaResourceController.prototype.newButtonListener =
function(ev) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = this;
		args["func"] = ZaResourceController.prototype.newResource;
		//ask if the user wants to save changes		
		//ZaApp.getInstance().dialogs["confirmMessageDialog"] = ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON]);								
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else {
		this.newResource();
	}	
}

//private and protected methods
ZaResourceController.prototype._createUI = 
function (entry) {
	//create accounts list view
	// create the menu operations/listeners first	
	this._contentView = this._view = new this.tabConstructor(this._container, entry);

    this._initToolbar();
    this._initPopupMenu();
	//always add Help button at the end of the toolbar    
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));		
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_RES);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
    if(!appNewUI) {
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	//ZaApp.getInstance().createView(ZaZimbraAdmin._RESOURCE_VIEW, elements);
	var tabParams = {
			openInNewTab: true,
			tabId: this.getContentViewId()
		}
	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
    } else
         ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
	this._removeConfirmMessageDialog = new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.VIEW_RES + "_removeConfirm");			
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}


/**
* saves the changes in the fields, calls modify or create on the current ZaResource
* @return Boolean - indicates if the changes were succesfully saved
**/
ZaResourceController.prototype._saveChanges  =
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
		this.popupMsgDialog(dlgMsg, true);
		return false;
	}
	//check if the data is copmlete 
	var tmpObj = this._view.getObject();
	var newName=null;
	
	//Check the data
	if(tmpObj.attrs == null ) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}
	
	ZaResource.prototype.setLdapAttrsFromSchedulePolicy.call(tmpObj);
	
	//check if need to rename
	if(this._currentObject && tmpObj.name != this._currentObject.name) {
		//var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {*/
		if(!AjxUtil.isValidEmailNonReg(tmpObj.name)) {
			//show error msg
			this._errorDialog.setMessage(ZaMsg.ERROR_ACCOUNT_NAME_INVALID, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
		newName = tmpObj.name;
	}

	var mods = new Object();
	
	if(!ZaResource.checkValues(tmpObj))
		return false;
		
	if(ZaItem.hasAnyRight([ZaResource.SET_CALRES_PASSWORD_RIGHT, ZaResource.CHANGE_CALRES_PASSWORD_RIGHT],tmpObj)) {
		//change password if new password is provided
		if(tmpObj.attrs[ZaResource.A_password]!=null && tmpObj[ZaResource.A2_confirmPassword]!=null && tmpObj.attrs[ZaResource.A_password].length > 0) {
			try {
				this._currentObject.changePassword(tmpObj.attrs[ZaResource.A_password]);
			} catch (ex) {
				this.popupErrorDialog(ZaMsg.FAILED_SAVE_ACCOUNT, ex);
				return false;				
				
			}
		}
	}
			
	var changeDetails = new Object();
	
	//set the cosId to "" if the autoCos is enabled.
	if (tmpObj[ZaResource.A2_autoCos] == "TRUE") {
		tmpObj.attrs[ZaResource.A_COSId] = "" ;
	}
	
	//check if need to rename
	if(newName) {
		changeDetails["newName"] = newName;
		try {
			this._currentObject.rename(newName);
		} catch (ex) {
			if(ex.code == ZmCsfeException.ACCT_EXISTS) {
				this.popupErrorDialog(ZaMsg.FAILED_RENAME_ACCOUNT_1, ex);
			} else {
				this._handleException(ex, "ZaResourceController.prototype._saveChanges", null, false);	
			} 
			return false;
		}
	}	
	
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if(a == ZaResource.A_password || a == ZaItem.A_objectClass ||  a==ZaResource.A_mail
                || a == ZaItem.A_zimbraId || a == ZaItem.A_zimbraACE) {
			continue;
		}	
		if(!ZaItem.hasWritePermission(a,tmpObj)) {
			continue;
		}
		//check if the value has been modified
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(a==ZaResource.A_uid) {
				continue; //skip uid, it is changed throw a separate request
			}
			if(a == ZaResource.A_COSId && !AjxUtil.isEmpty(tmpObj.attrs[ZaResource.A_COSId]) && !ZaItem.ID_PATTERN.test(tmpObj.attrs[ZaResource.A_COSId])) {
				this.popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[tmpObj.attrs[ZaResource.A_COSId]]), null);
				return false;
			}
			if(tmpObj.attrs[a] instanceof Array && this._currentObject.attrs[a] instanceof Array) {
				if(tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}
      if (this._currentObject[ZaModel.currentTab]!= tmpObj[ZaModel.currentTab])
             this._currentObject[ZaModel.currentTab] = tmpObj[ZaModel.currentTab];
	//save changed fields
	try {	
		this._currentObject.modify(mods);
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS) {
			this.popupErrorDialog(ZaMsg.FAILED_CREATE_ACCOUNT_1, ex);
		} else if(ex.code == ZmCsfeException.NO_SUCH_COS) {
			this.popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[tmpObj.attrs[ZaResource.A_COSId]]), ex);
        } else {
			this._handleException(ex, "ZaResourceController.prototype._saveChanges", null, false);	
		}
		return false;
	}
    ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.ResourceModified,[this._currentObject.name]));
	return true;
};
