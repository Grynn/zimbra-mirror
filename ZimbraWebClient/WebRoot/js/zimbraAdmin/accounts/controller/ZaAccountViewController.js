/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
* @class ZaAccountViewController controls display of a single Account
* @contructor ZaAccountViewController
* @param appCtxt
* @param container
* @param abApp
* @author Roland Schemers
* @author Greg Solovyev
**/

ZaAccountViewController = function(appCtxt, container, app) {
	ZaXFormViewController.call(this, appCtxt, container, app, "ZaAccountViewController");
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;
	this._helpURL = ZaAccountViewController.helpURL;
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
	this._toolbarOperations = new Array();
	this.tabConstructor = ZaAccountXFormView;
}

ZaAccountViewController.prototype = new ZaXFormViewController();
ZaAccountViewController.prototype.constructor = ZaAccountViewController;
ZaAccountViewController.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/editing_accounts.htm";		
ZaController.initToolbarMethods["ZaAccountViewController"] = new Array();
ZaController.setViewMethods["ZaAccountViewController"] = new Array();
//public methods

/**
*	@method show
*	@param entry - isntance of ZaAccount class
*	@param skipRefresh - forces to skip entry.refresh() call. 
*		   When getting account from an alias the account is retreived from the server using ZaAccount.load() 
* 		   so there is no need to refresh it.
*/

ZaAccountViewController.prototype.show = 
function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaAccountViewController.initToolbarMethod =
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", new AjxListener(this, ZaAccountViewController.prototype._newButtonListener)));   			    	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
	if(ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED)
		this._toolbarOperations.push(new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailboxDis", new AjxListener(this, ZaAccountViewController.prototype._viewMailListener)));		

	if(ZaSettings.ACCOUNTS_REINDEX_ENABLED)
		this._toolbarOperations.push(new ZaOperation(ZaOperation.REINDEX_MAILBOX, ZaMsg.ACTBB_ReindexMbx, ZaMsg.ACTBB_ReindexMbx_tt, "ReindexMailboxes", "ReindexMailboxes", new AjxListener(this, ZaAccountViewController.prototype._reindexMbxListener)));					
}
ZaController.initToolbarMethods["ZaAccountViewController"].push(ZaAccountViewController.initToolbarMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaAccountViewController.setViewMethod =
function(entry) {
	try {

		if(!this._UICreated) {

			this._initToolbar();
			//make sure these are last
			this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
			this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));		
			
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);
	
	  		this._contentView = this._view = new this.tabConstructor(this._container, this._app);
			var elements = new Object();
			elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	
			
			var tabParams = {
				openInNewTab: true,
				tabId: this.getContentViewId()
			}
				  		
	    	this._app.createView(this.getContentViewId(), elements, tabParams);
	    	this._UICreated = true;
	    	//associate the controller with the view by viewId
	    	this._app._controllers[this.getContentViewId ()] = this ;
  		}
		//this._app.pushView(ZaZimbraAdmin._ACCOUNT_VIEW);
		this._app.pushView(this.getContentViewId()) ;
		if(entry.id) {
			try {
				entry.refresh(!ZaSettings.COSES_ENABLED);
			} catch (ex) {
				// Data corruption may cause anexception. We should catch it here in order to display the form anyway.
				this._handleException(ex, null, null, false);
				if (ex.code ==  ZmCsfeException.SVC_PERM_DENIED) {
					this._app.popView();
					return ;
					
				}
			}
		}
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
		if(!entry.id) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
		}	
		this._view.setDirty(false);
		entry.attrs[ZaAccount.A_password] = null; //get rid of VALUE-BLOCKED
		entry[ZaModel.currentTab] = "1"
		this._view.setObject(entry);
		this._currentObject = entry;
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._setView", null, false);
	}	
	this._cosChanged = false;
	this._domainsChanged = false;
}
ZaController.setViewMethods["ZaAccountViewController"].push(ZaAccountViewController.setViewMethod);
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
	
	//Check the data
	if(tmpObj.attrs == null ) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}
		
	var mods = new Object();

	if(!ZaAccount.checkValues(tmpObj, this._app))
		return false;
	
	if(ZaSettings.ACCOUNTS_CHPWD_ENABLED) {
		//change password if new password is provided
		if(tmpObj.attrs[ZaAccount.A_password]!=null && tmpObj[ZaAccount.A2_confirmPassword]!=null && tmpObj.attrs[ZaAccount.A_password].length > 0) {
			try {
				this._currentObject.changePassword(tmpObj.attrs[ZaAccount.A_password]);
			} catch (ex) {
				this.popupErrorDialog(ZaMsg.FAILED_SAVE_ACCOUNT, ex, true);
				return false;				
				
			}
		}
	}
	
	//set the cosId to "" if the autoCos is enabled.
	if (tmpObj[ZaAccount.A2_autoCos] == "TRUE") {
		tmpObj.attrs[ZaAccount.A_COSId] = "" ;
	}
	
	//transfer the fields from the tmpObj to the _currentObject
	for (var a in tmpObj.attrs) {
		if(a == ZaAccount.A_password || a==ZaAccount.A_zimbraMailAlias || a == ZaItem.A_objectClass || a==ZaAccount.A2_mbxsize || a==ZaAccount.A_mail || a == ZaItem.A_zimbraId || a == ZaAccount.A_zimbraAvailableSkin || a == ZaAccount.A_zimbraZimletAvailableZimlets) {
			continue;
		}	
		//check if the value has been modified
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(a==ZaAccount.A_uid) {
				continue; //skip uid, it is changed throw a separate request
			}
			if(tmpObj.attrs[a] instanceof Array) {
				if(this._currentObject.attrs[a] && tmpObj.attrs[a] 
					&& tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}

	if(ZaSettings.SKIN_PREFS_ENABLED) {
		if(tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] != null) {
			if(!(tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin] instanceof Array)) {
				mods[ZaAccount.A_zimbraAvailableSkin] = [tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin]];
			} else {
				var cnt = tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin].length;
				mods[ZaAccount.A_zimbraAvailableSkin] = [];
				for(var i = 0; i < cnt; i++) {
					mods[ZaAccount.A_zimbraAvailableSkin].push(tmpObj.attrs[ZaAccount.A_zimbraAvailableSkin][i]);
				}
			}
		} else if(this._currentObject.attrs[ZaAccount.A_zimbraAvailableSkin] != null) {
			mods[ZaAccount.A_zimbraAvailableSkin] = "";
		}
	}
		
	if(ZaSettings.ZIMLETS_ENABLED) {
		if(tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] != null) {
			if(!(tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] instanceof Array)) {
				mods[ZaAccount.A_zimbraZimletAvailableZimlets] = [tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets]];
			} else {
				var cnt = tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets].length;
				if(cnt==0) {
					//no zimlets
					if(this._currentObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] == null || !(this._currentObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets].length==1 && this._currentObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets][0] == ZaZimlet.NULL_ZIMLET))
						mods[ZaAccount.A_zimbraZimletAvailableZimlets] = [ZaZimlet.NULL_ZIMLET];
				} else {
					mods[ZaAccount.A_zimbraZimletAvailableZimlets] = [];
					for(var i = 0; i < cnt; i++) {
						mods[ZaAccount.A_zimbraZimletAvailableZimlets].push(tmpObj.attrs[ZaAccount.A_zimbraZimletAvailableZimlets][i]);
					}
				}
			}
		} else if(this._currentObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] != null) {
			mods[ZaAccount.A_zimbraZimletAvailableZimlets] = "";
		}
	}
	//save changed fields
	try {	
		this._currentObject.modify(mods);
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS) {
			this.popupErrorDialog(ZaMsg.FAILED_CREATE_ACCOUNT_1, ex, true);
		} else {
			this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);	
		}
		return false;
	}
	//add-remove aliases
	var tmpObjCnt = -1;
	var currentObjCnt = -1;
	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
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
					if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]) {
						/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix])) {
							//show error msg
							this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_ALIAS_INVALID,[tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]), null, DwtMessageDialog.CRITICAL_STYLE, null);
							this._errorDialog.popup();		
							break;						
						}*/
						if(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix].indexOf("@") != tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix].lastIndexOf("@")) {
							//show error msg
							this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_ALIAS_INVALID,[tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]), null, DwtMessageDialog.CRITICAL_STYLE, null);
							this._errorDialog.popup();		
							break;						
						}						
						this._currentObject.addAlias(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
					}
				} catch (ex) {
					if(ex.code == ZmCsfeException.ACCT_EXISTS) {
						//if failed because account exists just show a warning
						var account = this._findAlias(tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]);
						switch(account.type) {
							case ZaItem.DL:
								if(account.name == tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS3,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS4,[account.name, tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]);								
								}
							break;
							case ZaItem.ACCOUNT:
								if(account.name == tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS2,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS1,[account.name, tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]);								
								}							
							break;	
							case ZaItem.RESOURCE:
								if(account.name == tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]) {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS5,[account.name]);								
								} else {
									failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS6,[account.name, tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]);								
								}							
							break;							
							default:
								failedAliases += "<br>" +AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS0,[tmpObj.attrs[ZaAccount.A_zimbraMailAlias][ix]]);							
							break;
						}
						failedAliasesCnt++;
					} else {
						//if failed for another reason - jump out
						throw (ex);
					}
				}
			}
	
			if(failedAliasesCnt == 1) {
				this._errorDialog.setMessage(ZaMsg.WARNING_ALIAS_EXISTS + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
				this._errorDialog.popup();			
			} else if(failedAliasesCnt > 1) {
				this._errorDialog.setMessage(ZaMsg.WARNING_ALIASES_EXIST + failedAliases, "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
				this._errorDialog.popup();			
			}
		} catch (ex) {
			/*for (var prop in ex) {
				detailStr = detailStr + prop + " - " + ex[prop] + "\n";				
			}*/
				
			/*this._errorDialog.setMessage(ZaMsg.FAILED_ADD_ALIASES, detailStr, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.zimbraAdminTitle);
			this._errorDialog.popup();
			*/
			this.popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex, true);	
			return false;
		}
	}
	
	//check to see if the rename of account is needed.
	var newName=null;
	if(this._currentObject && tmpObj.name != this._currentObject.name) {
		//var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if(!AjxUtil.EMAIL_FULL_RE.test(tmpObj.name) ) {
			//show error msg
			this._errorDialog.setMessage(ZaMsg.ERROR_ACCOUNT_NAME_INVALID, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
		newName = tmpObj.name;
	}
	
	if(newName) {
		try {
			this._currentObject.rename(newName);
		} catch (ex) {
			if(ex.code == ZmCsfeException.ACCT_EXISTS) {
				this.popupErrorDialog(ZaMsg.FAILED_RENAME_ACCOUNT_1, ex, true);
			} else {
				this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);	
			}
			return false;
		}
	}
	return true;
}

// new button was pressed
ZaAccountViewController.prototype._newButtonListener =
function(ev) {
	try {
		var newAccount = new ZaAccount(this._app);
		if(!this._app._newAccountWizard)
			this._app._newAccountWizard = new ZaNewAccountXWizard(this._container, this._app);	
			
		this._app._newAccountWizard.setObject(newAccount);
		this._app._newAccountWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._newButtonListener", null, false);
	}
}

ZaAccountViewController.prototype._reindexMbxListener = 
function (ev) {
	try {

		if(!this._app.dialogs["reindexWizard"])
			this._app.dialogs["reindexWizard"] = new ReindexMailboxXDialog(this._container, this._app);	

		var obj = new ZaReindexMailbox();
		obj.mbxId = this._currentObject.id;
		this._app.dialogs["reindexWizard"].setObject(obj);
		this._app.dialogs["reindexWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._reindexMbxListener", null, false);
	}

}

ZaAccountViewController.prototype._viewMailListener =
function(ev) {
	try {
		if(this._currentObject && this._currentObject.id) {
			ZaAccountListController._viewMailListenerLauncher.call(this, this._currentObject);
		}
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._viewMailListener", null, false);			
	}
}

ZaAccountViewController.prototype._handleException = 
function (ex, method, params, restartOnError, obj) {
	if(ex.code == ZmCsfeException.SVC_WRONG_HOST) {
		var szMsg = ZaMsg.ERROR_WRONG_HOST;
		if(ex.detail) {
			szMsg +="<br>Details:<br>";
			szMsg += ex.detail;
		}
		this._errorDialog.setMessage(szMsg, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();					
	} else if(ex.code == ZmCsfeException.ACCT_EXISTS) {
		this._errorDialog.setMessage(ZaMsg.ERROR_ACCOUNT_EXISTS, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();
	} else {
		ZaController.prototype._handleException.call(this, ex, method, params, restartOnError, obj);				
	}	
}