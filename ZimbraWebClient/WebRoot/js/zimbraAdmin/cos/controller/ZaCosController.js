/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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
* @class ZaCosController controls display of a single COS
* @contructor ZaCosController
* @param appCtxt
* @param container
* @param abApp
**/

ZaCosController = function(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app, "ZaCosController");
	this._UICreated = false;	
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/cos/creating_classes_of_service.htm";		
	this.deleteMsg = ZaMsg.Q_DELETE_COS;
	this.objType = ZaEvent.S_COS;
	this._toolbarOperations = new Array();	
	this.tabConstructor = ZaCosXFormView;
}

ZaCosController.prototype = new ZaXFormViewController();
ZaCosController.prototype.constructor = ZaCosController;
ZaController.initToolbarMethods["ZaCosController"] = new Array();
ZaController.setViewMethods["ZaCosController"] = new Array();

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


/**
*	@method setViewMethod 
*	@param entry - isntance of ZaCos class
*/
ZaCosController.setViewMethod =
function(entry) {
	try {
	   	//create toolbar
		if(!this._UICreated) {
			this._initToolbar();
			//always add Help button at the end of the toolbar		
			this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
			this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);
	
		  	this._contentView = this._view = new this.tabConstructor(this._container, this._app, entry.id);
			var elements = new Object();
			elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;			  	
		    //this._app.createView(ZaZimbraAdmin._COS_VIEW, elements);
		    var tabParams = {
				openInNewTab: true,
				tabId: this.getContentViewId()
			}  	
		    this._app.createView(this.getContentViewId(), elements, tabParams) ;
			this._UICreated = true;
			this._app._controllers[this.getContentViewId ()] = this ;
	  	}
	
		//this._app.pushView(ZaZimbraAdmin._COS_VIEW);
		this._app.pushView(this.getContentViewId());
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
		if(!entry.id || (entry.name == "default")) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
		}	
		this._view.setDirty(false);
		entry[ZaModel.currentTab] = "1"
	  	this._view.setObject(entry);

	} catch (ex) {
		this._handleException(ex, "ZaCosController.prototype._setView", null, false);	
	}
	this._currentObject = entry;
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
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.COSTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.COSTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.COSTBB_New_tt, "NewCOS", "NewCOSDis", new AjxListener(this, ZaCosController.prototype._newButtonListener, [true])));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.COSTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
}
ZaController.initToolbarMethods["ZaCosController"].push(ZaCosController.initToolbarMethod);


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
	if(tmpObj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;	
	}

	//name
	if(tmpObj.attrs[ZaCos.A_name] == null || tmpObj.attrs[ZaCos.A_name].length < 1 ) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_NAME_REQUIRED, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	} else {
		tmpObj.name = tmpObj.attrs[ZaCos.A_name];
	}

	if(tmpObj.name.length > 256 || tmpObj.attrs[ZaCos.A_name].length > 256) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_COS_NAME_TOOLONG, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
	/**
	* check values
	**/
	
	//if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars])) {
   if (tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinUpperCaseChars])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinUpperCaseChars + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	

	if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinLowerCaseChars])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinLowerCaseChars + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
	if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinPunctuationChars])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinPunctuationChars + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}

	if(tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars] != null && tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPasswordMinNumericChars])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordMinNumericChars + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}

	if(tmpObj.attrs[ZaCos.A_zimbraMailQuota] != null && tmpObj.attrs[ZaCos.A_zimbraMailQuota] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMailQuota])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailQuota + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}

	if(tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries] != null && tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraContactMaxNumEntries])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_ContactMaxNumEntries + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
	if(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] != null && tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinLength + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
	if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] != null && tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxLength + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
	
	if (tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength] != null &&  tmpObj.attrs[ZaCos.A_zimbraMinPwdLength] != null) {
		if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdLength]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdLength]) > 0) {
			//show error msg
			this._errorDialog.setMessage(ZaMsg.ERROR_MAX_MIN_PWDLENGTH, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
			this._errorDialog.popup();		
			return false;
		}	
	}

	if(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMinAge + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}		
	
	if(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passMaxAge + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}		
	
	if(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge] != null && tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge] != null ){
		if(parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) < parseInt(tmpObj.attrs[ZaCos.A_zimbraMinPwdAge]) && parseInt(tmpObj.attrs[ZaCos.A_zimbraMaxPwdAge]) > 0) {
			//show error msg
			this._errorDialog.setMessage(ZaMsg.ERROR_MAX_MIN_PWDAGE, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
			this._errorDialog.popup();		
			return false;
		}
	}
		
	if(tmpObj.attrs[ZaCos.A_zimbraAuthTokenLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraAuthTokenLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AuthTokenLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}

	if(tmpObj.attrs[ZaCos.A_zimbraPrefOutOfOfficeCacheDuration] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPrefOutOfOfficeCacheDuration])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
/*	if(!AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraAdminAuthTokenLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_AdminAuthTokenLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}		*/
	if(tmpObj.attrs[ZaCos.A_zimbraMailIdleSessionTimeout] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailIdleSessionTimeout])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailIdleSessionTimeout + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}			
	
	if(tmpObj.attrs[ZaCos.A_zimbraPrefMailPollingInterval] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPrefMailPollingInterval])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPrefMailPollingInterval + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
	
	var n_minPollingInterval = tmpObj.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
	
	if(n_minPollingInterval != null && !AjxUtil.isLifeTime(n_minPollingInterval)) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraMailMinPollingInterval + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}
	
	var o_minPollingInterval = this._currentObject.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
	if (o_minPollingInterval != null && ZaUtil.getLifeTimeInSeconds (n_minPollingInterval)
		 > ZaUtil.getLifeTimeInSeconds(o_minPollingInterval)){	
		this.popupMsgDialog(AjxMessageFormat.format (ZaMsg.tt_minPollingIntervalWarning, [o_minPollingInterval, n_minPollingInterval]),  true);
	}
	
	if(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailMessageLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailMessageLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}			

	if(tmpObj.attrs[ZaCos.A_zimbraMailTrashLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailTrashLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailTrashLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
	
	if(tmpObj.attrs[ZaCos.A_zimbraMailSpamLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraMailSpamLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_MailSpamLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}		

	if(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutDuration] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutDuration])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordLockoutDuration + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	

	if(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutFailureLifetime] != null && !AjxUtil.isLifeTime(tmpObj.attrs[ZaCos.A_zimbraPasswordLockoutFailureLifetime])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
			
	if(tmpObj.attrs[ZaCos.A_zimbraPrefContactsPerPage] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraPrefContactsPerPage])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_PrefContactsPerPage + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
	if(tmpObj.attrs[ZaCos.A_zimbraEnforcePwdHistory] != null && !AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaCos.A_zimbraEnforcePwdHistory])) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_INVALID_VALUE + ": " + ZaMsg.NAD_passEnforceHistory + " ! ", null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
		this._errorDialog.popup();		
		return false;
	}	
		
	//check that current theme is part of selected themes
	if(tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] !=null && tmpObj.attrs[ZaCos.A_zimbraAvailableSkin].length > 0 && tmpObj.attrs[ZaCos.A_zimbraPrefSkin] ) {
		var arr = tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] instanceof Array ? tmpObj.attrs[ZaCos.A_zimbraAvailableSkin] : [tmpObj.attrs[ZaCos.A_zimbraAvailableSkin]];
		var cnt = arr.length;
		var found=false;
		for(var i=0; i < cnt; i++) {
			if(arr[i]==tmpObj.attrs[ZaCos.A_zimbraPrefSkin]) {
				found=true;
				break;
			}
		}
		if(!found) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format (ZaMsg.COS_WarningCurrentThemeNotAvail, [tmpObj.attrs[ZaCos.A_zimbraPrefSkin], tmpObj.attrs[ZaCos.A_zimbraPrefSkin]]), null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
			this._errorDialog.popup();		
			return false;			
		}
	}	
	var mods = new Object();
	//var changeDetails = new Object();
	if(!tmpObj.id)
		isNew = true;
		
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || a == ZaItem.A_zimbraId || a == ZaCos.A_zimbraAvailableSkin || a == ZaCos.A_zimbraZimletAvailableZimlets || a == ZaCos.A_zimbraMailHostPool) {
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

	if(ZaSettings.SKIN_PREFS_ENABLED) {
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
	}
		
	if(ZaSettings.ZIMLETS_ENABLED) {
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
	}
		
	//check if need to rename
	if(!isNew) {
		if(tmpObj.name != this._currentObject.name) {
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
					this._errorDialog.setMessage(ZaMsg.FAILED_RENAME_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
					this._errorDialog.popup();
				} else {
					this._handleException(ex, "ZaCosController.prototype._saveChanges", null, false);	
				}
				return false;
			}
		}
	}
	//save changed fields
	try {	
		if(isNew) {
			this._currentObject.create(tmpObj.name, mods);
			//if creation took place - fire a CreationEvent
			this.fireCreationEvent(this._currentObject);
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);	
		} else {
			this._currentObject.modify(mods);
			//if modification took place - fire a ChangeEvent
			//changeDetails["obj"] = this._currentObject;
			//changeDetails["mods"] = mods;
			this.fireChangeEvent(this._currentObject);
		}
	} catch (ex) {
		var detailStr = "";
		for (var prop in ex) {
			if(ex[prop] instanceof Function) 
				continue;
				
			detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
		}
		if(ex.code == ZmCsfeException.COS_EXISTS) {
			this._errorDialog.setMessage(ZaMsg.FAILED_CREATE_COS_1, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);				
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
	var newCos = new ZaCos(this._app);
	var defCos = new ZaCos(this._app);
	defCos.load("name", "default");
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) )
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
			this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);								
			this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
			this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
			this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
			this._app.dialogs["confirmMessageDialog"].popup();
		} else {
			this.newCos();
		}	
	}
}
