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
* @class ZaAccountViewController controls display of a single Account
* @contructor ZaAccountViewController
* @param appCtxt
* @param container
* @param abApp
* @author Roland Schemers
* @author Greg Solovyev
**/

ZaAccountViewController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaAccountViewController");
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;
	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaAccountViewController.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
	this.deleteMsg = ZaMsg.Q_DELETE_ACCOUNT;
	this.tabConstructor = ZaAccountXFormView;
}

ZaAccountViewController.prototype = new ZaXFormViewController();
ZaAccountViewController.prototype.constructor = ZaAccountViewController;
ZaAccountViewController.helpURL = "managing_accounts/editing_accounts.htm";
ZaController.changeActionsStateMethods["ZaAccountViewController"] = new Array();
ZaController.setViewMethods["ZaAccountViewController"] = new Array();
ZaController.initToolbarMethods["ZaAccountViewController"] = new Array();
ZaXFormViewController.preSaveValidationMethods["ZaAccountViewController"] = new Array();
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
	var showNewAccount = false;
	if(ZaSettings.HAVE_MORE_DOMAINS || ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
		showNewAccount = true;
	} else {
		var domainList = ZaApp.getInstance().getDomainList().getArray();
		var cnt = domainList.length;
		for(var i = 0; i < cnt; i++) {
			if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ACCOUNT,domainList[i])) {
				showNewAccount = true;
				break;
			}	
		}
	}
		
	this._toolbarOrder.push(ZaOperation.SAVE);
	this._toolbarOrder.push(ZaOperation.CLOSE);
	this._toolbarOrder.push(ZaOperation.SEP);
	if(showNewAccount) {
		this._toolbarOrder.push(ZaOperation.NEW_WIZARD);
	}
	this._toolbarOrder.push(ZaOperation.DELETE);		
	
	
	this._toolbarOperations[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
	this._toolbarOperations[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));    	
	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	if(showNewAccount) {
		this._toolbarOperations[ZaOperation.NEW_WIZARD] = new ZaOperation(ZaOperation.NEW_WIZARD, ZaMsg.TBB_New, ZaMsg.ACTBB_New_tt, "Account", "AccountDis", new AjxListener(this, ZaAccountViewController.prototype._newButtonListener));
	}   			    	
	this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));    	    	
	
	this._toolbarOperations[ZaOperation.VIEW_MAIL] = new ZaOperation(ZaOperation.VIEW_MAIL, ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailboxDis", new AjxListener(this, ZaAccountViewController.prototype._viewMailListener));		
	this._toolbarOrder.push(ZaOperation.VIEW_MAIL);
	this._toolbarOperations[ZaOperation.REINDEX_MAILBOX] = new ZaOperation(ZaOperation.REINDEX_MAILBOX, ZaMsg.ACTBB_ReindexMbx, ZaMsg.ACTBB_ReindexMbx_tt, "ReindexMailboxes", "ReindexMailboxes", new AjxListener(this, ZaAccountViewController.prototype._reindexMbxListener));
	this._toolbarOrder.push(ZaOperation.REINDEX_MAILBOX);
					
}
ZaController.initToolbarMethods["ZaAccountViewController"].push(ZaAccountViewController.initToolbarMethod);

/**
* This listener is called when the Delete button is clicked. 
* member of ZaXFormViewController
* @param 	ev event object
**/
ZaAccountViewController.prototype.deleteButtonListener =
function(ev) {
	if(this._currentObject.id) {
		if(this._currentObject[ZaAccount.A2_zimbra_ds] || this._currentObject[ZaAccount.A2_ldap_ds]) {
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.WARN_DELETING_GAL_SYNC,[this._currentObject.name]), DwtMessageDialog.WARNING_STYLE);
		} else if (this._currentObject.attrs[ZaAccount.A_zimbraIsSystemResource] && this._currentObject.attrs[ZaAccount.A_zimbraIsSystemResource]=="TRUE") { 
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.WARN_DELETING_SYSTEM_RESOURCE,[this._currentObject.name]), DwtMessageDialog.WARNING_STYLE);
		} else {
			ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(this.deleteMsg, DwtMessageDialog.INFO_STYLE);
		}
		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.deleteAndGoAway, this, null);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else {
		ZaApp.getInstance().popView();
	}
}

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaAccount class
*/
ZaAccountViewController.setViewMethod =
function(entry) {
	try {
		this._initToolbar();
		//make sure these are last
		this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
		this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));		
		this._toolbarOrder.push(ZaOperation.NONE);
		this._toolbarOrder.push(ZaOperation.HELP);
		this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder, null, null, ZaId.VIEW_ACCT);
		
		if(!entry[ZaModel.currentTab])
			entry[ZaModel.currentTab] = "1";
	

		try {		  		
			if(!AjxUtil.isEmpty(entry.id)) {
				//console.log("loading the entry for the form");
				entry.refresh(false,true);
			}
	  		this._contentView = this._view = new this.tabConstructor(this._container,entry);
			var elements = new Object();
			elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;	
				
			var tabParams = {
				openInNewTab: true,
				tabId: this.getContentViewId()
			}
    		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams);

	    	//associate the controller with the view by viewId
		    ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
			//ZaApp.getInstance().pushView(ZaZimbraAdmin._ACCOUNT_VIEW);
			ZaApp.getInstance().pushView(this.getContentViewId()) ;
		} catch (ex) {
				// Data corruption may cause anexception. We should catch it here in order to display the form anyway.
			this._handleException(ex, null, null, false);
			if (ex.code ==  ZmCsfeException.SVC_PERM_DENIED) {
                if (this._contentView && this.getContentViewId()) {
				    //only pop the view when the view is actually created.
                    ZaApp.getInstance().popView();
                }
				return ;
				
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
		entry[ZaModel.currentTab] = "1";
		this._currentObject = entry;
		this._view.setObject(entry);
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._setView", null, false);
	}	
	this._cosChanged = false;
	this._domainsChanged = false;
}
ZaController.setViewMethods["ZaAccountViewController"].push(ZaAccountViewController.setViewMethod);

ZaAccountViewController.changeActionsStateMethod = function () {
	if(!this._currentObject)
		return;
		
	if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,this._currentObject))	{
		this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
	}

	if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,this._currentObject))	{
		this._toolbarOperations[ZaOperation.DELETE].enabled = false;
	}
   	var tmpObj = this._view.getObject();
        if(tmpObj.attrs != null && tmpObj.attrs[ZaAccount.A_mail] != null ) {
                var myitem = tmpObj.attrs[ZaAccount.A_mail];
                var myaccount = tmpObj.name;
                var mydomain = ZaAccount.getDomain(myaccount);
                var domainObj =  ZaDomain.getDomainByName(mydomain);
                if (myitem == "admin@"+mydomain || myitem == "root@"+mydomain || myitem == "postmaster@"+mydomain || myitem == "domainadmin@"+mydomain) {
                 this._toolbarOperations[ZaOperation.DELETE].enabled=false;
                }
                if (domainObj.attrs[ZaDomain.A_zimbraGalAccountId]){
                 if (myitem == domainObj.attrs[ZaDomain.A_zimbraGalAccountId]){
                        this._toolbarOperations[ZaOperation.DELETE].enabled=false;}
                }
                if (ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraSpamAccount]){
                        if (myitem == ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraSpamAccount].toString()){
                                this._toolbarOperations[ZaOperation.DELETE].enabled=false;}
                }
                if (ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraHamAccount]){
                        if (myitem == ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraHamAccount].toString()){
                                this._toolbarOperations[ZaOperation.DELETE].enabled=false;}
                }
                if (ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraAmavisQAccount]){
                        if (myitem == ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraAmavisQAccount].toString()){
                                this._toolbarOperations[ZaOperation.DELETE].enabled=false;}
                }
                if (ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraWikiAccount]){
                        if (myitem == ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraWikiAccount].toString()){
                                this._toolbarOperations[ZaOperation.DELETE].enabled=false;}
                }
                if (tmpObj.attrs[ZaAccount.A_isCCAccount]){
                        this._toolbarOperations[ZaOperation.DELETE].enabled=false;
                }
        }

	if(!ZaItem.hasRight(ZaAccount.REINDEX_MBX_RIGHT,this._currentObject))	{
		this._toolbarOperations[ZaOperation.REINDEX_MAILBOX].enabled = false;
	}
	if(this._toolbarOperations[ZaOperation.SAVE])	
		this._toolbarOperations[ZaOperation.SAVE].enabled = false;
}
ZaController.changeActionsStateMethods["ZaAccountViewController"].push(ZaAccountViewController.changeActionsStateMethod);


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

	if(!ZaAccount.checkValues(tmpObj))
		return false;
	
	if(ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT,tmpObj)) {
		//change password if new password is provided
		if(tmpObj.attrs[ZaAccount.A_password]!=null && tmpObj[ZaAccount.A2_confirmPassword]!=null && tmpObj.attrs[ZaAccount.A_password].length > 0) {
			try {
				this._currentObject.changePassword(tmpObj.attrs[ZaAccount.A_password]);
			} catch (ex) {
				this.popupErrorDialog(ZaMsg.FAILED_SAVE_ACCOUNT, ex);
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
		if(a == ZaAccount.A_password || a==ZaAccount.A_zimbraMailAlias || a == ZaItem.A_objectClass
                || a==ZaAccount.A2_mbxsize || a==ZaAccount.A_mail || a == ZaItem.A_zimbraId
                || a == ZaAccount.A_zimbraAvailableSkin || a == ZaAccount.A_zimbraZimletAvailableZimlets
                || a == ZaItem.A_zimbraACE) {
			continue;
		}	
		if(!ZaItem.hasWritePermission(a,tmpObj)) {
			continue;
		}		
		//check if the value has been modified
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(a==ZaAccount.A_uid) {
				continue; //skip uid, it is changed throw a separate request
			}
			if(tmpObj.attrs[a] instanceof Array) {
                if (!this._currentObject.attrs[a]) this._currentObject.attrs[a] = [] ;
                if(!this._currentObject.attrs[a] instanceof Array) {
                	this._currentObject.attrs[a] = [this._currentObject.attrs[a]];
                }
                if( tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}

	if(ZaTabView.isTAB_ENABLED(this._currentObject,ZaAccountXFormView.SKIN_TAB_ATTRS, ZaAccountXFormView.SKIN_TAB_RIGHTS)) {
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
		
	if(ZaTabView.isTAB_ENABLED(this._currentObject,ZaAccountXFormView.ZIMLET_TAB_ATTRS, ZaAccountXFormView.ZIMLET_TAB_RIGHTS)) {
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
		this._currentObject.modify(mods, tmpObj);
	} catch (ex) {
		if(ex.code == ZmCsfeException.ACCT_EXISTS) {
			this.popupErrorDialog(ZaMsg.FAILED_CREATE_ACCOUNT_1, ex);
		} else if(ex.code == ZmCsfeException.NO_SUCH_COS) {
			this.popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[tmpObj.attrs[ZaAccount.A_COSId]]), ex);
        } else {
			this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);	
		}
		return false;
	}
	//add-remove aliases
	var tmpObjCnt = -1;
	var currentObjCnt = -1;
	if(ZaTabView.isTAB_ENABLED(this._currentObject,ZaAccountXFormView.ALIASES_TAB_ATTRS, ZaAccountXFormView.ALIASES_TAB_RIGHTS)) {
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
				this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.WARNING_ALIAS_EXISTS, [failedAliases]), "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
				this._errorDialog.popup();			
			} else if(failedAliasesCnt > 1) {
				this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.WARNING_ALIASES_EXIST, [failedAliases]), "", DwtMessageDialog.WARNING_STYLE, ZaMsg.zimbraAdminTitle);
				this._errorDialog.popup();			
			}
		} catch (ex) {
			this.popupErrorDialog(ZaMsg.FAILED_ADD_ALIASES, ex);	
			return false;
		}
	}
	
	//check to see if the rename of account is needed.
	var newName=null;
	if(this._currentObject && tmpObj.name != this._currentObject.name) {
		//var emailRegEx = /^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpObj.name) ) {
			//show error msg
			this._errorDialog.setMessage(ZaMsg.ERROR_ACCOUNT_NAME_INVALID, null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}*/
		newName = tmpObj.name;
	}
	
	if(newName) {
		try {
			this._currentObject.rename(newName);
		} catch (ex) {
			if(ex.code == ZmCsfeException.ACCT_EXISTS) {
				this.popupErrorDialog(ZaMsg.FAILED_RENAME_ACCOUNT_1, ex);
			} else {
				this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);	
			}
			return false;
		}
	}

    //TODO: may need to check if the account type update is needed. update the domain account limits object
   
    return true;
}

// new button was pressed
ZaAccountViewController.prototype._newButtonListener =
function(ev) {
	try {
		var newAccount = new ZaAccount();
		newAccount.loadNewObjectDefaults("name", ZaSettings.myDomainName);		
		if(!ZaApp.getInstance()._newAccountWizard)
			ZaApp.getInstance()._newAccountWizard = new ZaNewAccountXWizard(this._container,newAccount);
        else { //update the account type if needed
            ZaApp.getInstance()._newAccountWizard.updateAccountType () ;    
        }

        ZaApp.getInstance()._newAccountWizard.setObject(newAccount);
		ZaApp.getInstance()._newAccountWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountViewController.prototype._newButtonListener", null, false);
	}
}

ZaAccountViewController.prototype._reindexMbxListener = 
function (ev) {
	try {

		if(!ZaApp.getInstance().dialogs["reindexWizard"])
			ZaApp.getInstance().dialogs["reindexWizard"] = new ReindexMailboxXDialog(this._container);	

		var obj = new ZaReindexMailbox();
		obj.mbxId = this._currentObject.id;
		ZaApp.getInstance().dialogs["reindexWizard"].setObject(obj);
		ZaApp.getInstance().dialogs["reindexWizard"].popup();
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
