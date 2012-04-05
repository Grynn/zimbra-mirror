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
* @class ZaDomainController controls display of a single Domain
* @contructor ZaDomainController
* @param appCtxt
* @param container
* @param abApp
**/

ZaDomainController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container,"ZaDomainController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/managing_domains.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaDomainController.helpButtonText;
	this._toolbarOperations = new Array();			
	this.deleteMsg = ZaMsg.Q_DELETE_DOMAIN;	
	this.objType = ZaEvent.S_DOMAIN;
	this.tabConstructor = ZaDomainXFormView;				
}

ZaDomainController.prototype = new ZaXFormViewController();
ZaDomainController.prototype.constructor = ZaDomainController;
ZaDomainController.helpButtonText = ZaMsg.helpEditDomains;

ZaController.changeActionsStateMethods["ZaDomainController"] = new Array();
ZaController.initToolbarMethods["ZaDomainController"] = new Array();
ZaController.initPopupMenuMethods["ZaDomainController"] = new Array();
ZaController.setViewMethods["ZaDomainController"] = new Array();
ZaController.saveChangeCheckMethods["ZaDomainController"] = new Array();
ZaController.postChangeMethods["ZaDomainController"] = new Array();

/**
*	@method show
*	@param entry - isntance of ZaDomain class
*/

ZaDomainController.prototype.show = 
function(entry) {
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
	}
}

ZaDomainController.changeActionsStateMethod = function () {
    var isToEnable = (this._view && this._view.isDirty());

    if(this._toolbarOperations[ZaOperation.SAVE])
        this._toolbarOperations[ZaOperation.SAVE].enabled = isToEnable;

    if(this._popupOperations[ZaOperation.SAVE]) {
        this._popupOperations[ZaOperation.SAVE].enabled = isToEnable;
    }
		
	if(this._currentObject.attrs[ZaDomain.A_zimbraDomainStatus] == ZaDomain.DOMAIN_STATUS_SHUTDOWN) {
		if(this._toolbarOperations[ZaOperation.DELETE])
			this._toolbarOperations[ZaOperation.DELETE].enabled = false;

		if(this._toolbarOperations[ZaOperation.GAL_WIZARD])
			this._toolbarOperations[ZaOperation.GAL_WIZARD].enabled = false;
					
		if(this._toolbarOperations[ZaOperation.AUTH_WIZARD])
			this._toolbarOperations[ZaOperation.AUTH_WIZARD].enabled = false;

	} else {

		if(this._toolbarOperations[ZaOperation.GAL_WIZARD] && !ZaDomain.canConfigureGal(this._currentObject)) {
			this._toolbarOperations[ZaOperation.GAL_WIZARD].enabled = false;
		}

		if(this._toolbarOperations[ZaOperation.AUTH_WIZARD]	&& !ZaDomain.canConfigureAuth(this._currentObject)) {
			this._toolbarOperations[ZaOperation.AUTH_WIZARD].enabled = false;
		}
	}		
}
ZaController.changeActionsStateMethods["ZaDomainController"].push(ZaDomainController.changeActionsStateMethod);

/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaDomainController.initToolbarMethod =          
function () {                                    
	this._toolbarOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.DTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));
	this._toolbarOrder.push(ZaOperation.SAVE);		

	this._toolbarOperations[ZaOperation.CLOSE]=new ZaOperation(ZaOperation.CLOSE,ZaMsg.TBB_Close, ZaMsg.DTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener));    	
	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);


	this._toolbarOrder.push(ZaOperation.CLOSE);
	this._toolbarOrder.push(ZaOperation.SEP);

	if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_TOP_DOMAIN, ZaZimbraAdmin.currentAdminAccount)
	|| ZaItem.hasRight(ZaDomain.RIGHT_CREATE_SUB_DOMAIN, this._currentObject)) {
		this._toolbarOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, this._newButtonListener));
		this._toolbarOrder.push(ZaOperation.NEW);		
	}

	if(ZaItem.hasRight(ZaDomain.RIGHT_DELETE_DOMAIN,this._currentObject))	{
		this._toolbarOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
		this._toolbarOrder.push(ZaOperation.DELETE);		    	    	
	}
		
    this._toolbarOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS]=new ZaOperation(ZaOperation.VIEW_DOMAIN_ACCOUNTS,ZaMsg.Domain_view_accounts, ZaMsg.Domain_view_accounts_tt, "Search", "SearchDis", new AjxListener(this, this.viewAccountsButtonListener));
    this._toolbarOrder.push(ZaOperation.VIEW_DOMAIN_ACCOUNTS);


    //if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_GAL_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
    if(ZaDomain.canConfigureGal(this._currentObject))	{
		this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
		this._toolbarOperations[ZaOperation.GAL_WIZARD]=new ZaOperation(ZaOperation.GAL_WIZARD,ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainController.prototype._galWizButtonListener));   		
		this._toolbarOrder.push(ZaOperation.SEP);
		this._toolbarOrder.push(ZaOperation.GAL_WIZARD);			
	}
	if(ZaDomain.canConfigureAuth(this._currentObject)) {
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_AUTH_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		this._toolbarOperations[ZaOperation.AUTH_WIZARD]=new ZaOperation(ZaOperation.AUTH_WIZARD,ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainController.prototype._authWizButtonListener));
		this._toolbarOrder.push(ZaOperation.AUTH_WIZARD);		   		   		
	}

	if(ZaItem.hasRight(ZaDomain.RIGHT_CHECK_MX_RECORD,this._currentObject)) {
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_CHECK_MX_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
	   	this._toolbarOperations[ZaOperation.CHECK_MX_RECORD]=new ZaOperation(ZaOperation.CHECK_MX_RECORD,ZaMsg.DTBB_CheckMX, ZaMsg.DTBB_CheckMX_tt, "ReindexMailboxes", "ReindexMailboxes", new AjxListener(this, ZaDomainController.prototype._checkMXButtonListener));
		this._toolbarOrder.push(ZaOperation.CHECK_MX_RECORD);	   	
	}

	/* bug 71235, remove auto provisioning
	if(ZaDomain.canConfigureAutoProv(this._currentObject)) {
		this._toolbarOperations[ZaOperation.AUTOPROV_WIZARD]=new ZaOperation(ZaOperation.AUTOPROV_WIZARD,ZaMsg.DTBB_AutoProvConfigWiz,
                ZaMsg.DTBB_AutoProvConfigWiz_tt, "Backup", "BackupDis",
                new AjxListener(this, ZaDomainController.prototype._autoProvWizButtonListener));
		this._toolbarOrder.push(ZaOperation.AUTOPROV_WIZARD);
	} */

}
ZaController.initToolbarMethods["ZaDomainController"].push(ZaDomainController.initToolbarMethod);


ZaDomainController.initPopupMenuMethod =
function () {

	this._popupOperations[ZaOperation.SAVE]=new ZaOperation(ZaOperation.SAVE,ZaMsg.TBB_Save, ZaMsg.DTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));


	if(ZaItem.hasRight(ZaDomain.RIGHT_CREATE_TOP_DOMAIN, ZaZimbraAdmin.currentAdminAccount)
	|| ZaItem.hasRight(ZaDomain.RIGHT_CREATE_SUB_DOMAIN, this._currentObject)) {
		this._popupOperations[ZaOperation.NEW]=new ZaOperation(ZaOperation.NEW,ZaMsg.TBB_New, ZaMsg.DTBB_New_tt, "Domain", "DomainDis", new AjxListener(this, this._newButtonListener));
	}

	if(ZaItem.hasRight(ZaDomain.RIGHT_DELETE_DOMAIN,this._currentObject))	{
		this._popupOperations[ZaOperation.DELETE]=new ZaOperation(ZaOperation.DELETE,ZaMsg.TBB_Delete, ZaMsg.DTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
	}

    this._popupOperations[ZaOperation.VIEW_DOMAIN_ACCOUNTS]=new ZaOperation(ZaOperation.VIEW_DOMAIN_ACCOUNTS,ZaMsg.Domain_view_accounts, ZaMsg.Domain_view_accounts_tt, "Search", "SearchDis", new AjxListener(this, this.viewAccountsButtonListener));

    //if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_GAL_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
    if(ZaDomain.canConfigureGal(this._currentObject))	{
		this._popupOperations[ZaOperation.GAL_WIZARD]=new ZaOperation(ZaOperation.GAL_WIZARD,ZaMsg.DTBB_GAlConfigWiz, ZaMsg.DTBB_GAlConfigWiz_tt, "GALWizard", "GALWizardDis", new AjxListener(this, ZaDomainController.prototype._galWizButtonListener));
	}
	if(ZaDomain.canConfigureAuth(this._currentObject)) {
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_AUTH_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		this._popupOperations[ZaOperation.AUTH_WIZARD]=new ZaOperation(ZaOperation.AUTH_WIZARD,ZaMsg.DTBB_AuthConfigWiz, ZaMsg.DTBB_AuthConfigWiz_tt, "AuthWizard", "AuthWizardDis", new AjxListener(this, ZaDomainController.prototype._authWizButtonListener));
	}

	if(ZaItem.hasRight(ZaDomain.RIGHT_CHECK_MX_RECORD,this._currentObject)) {
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_CHECK_MX_WIZ] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
	   	this._popupOperations[ZaOperation.CHECK_MX_RECORD]=new ZaOperation(ZaOperation.CHECK_MX_RECORD,ZaMsg.DTBB_CheckMX, ZaMsg.DTBB_CheckMX_tt, "ReindexMailboxes", "ReindexMailboxes", new AjxListener(this, ZaDomainController.prototype._checkMXButtonListener));
	}

	/* bug 71235, remove auto provisioning
	if(ZaDomain.canConfigureAutoProv(this._currentObject)) {
		this._popupOperations[ZaOperation.AUTOPROV_WIZARD]=new ZaOperation(ZaOperation.AUTOPROV_WIZARD,ZaMsg.DTBB_AutoProvConfigWiz,
                ZaMsg.DTBB_AutoProvConfigWiz_tt, "Backup", "BackupDis",
                new AjxListener(this, ZaDomainController.prototype._autoProvWizButtonListener));
	} */

}
ZaController.initPopupMenuMethods["ZaDomainController"].push(ZaDomainController.initPopupMenuMethod);

ZaDomainController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
        this._appbarOperation[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "", "", new AjxListener(this, this.saveButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaDomainController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
        this._appbarOrder.push(ZaOperation.SAVE);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
}

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaDomainController.setViewMethod =
function(entry) {
	entry.load("id", entry.id,false,true);
	this._currentObject = entry;
	this._createUI(entry);
 
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
}
ZaController.setViewMethods["ZaDomainController"].push(ZaDomainController.setViewMethod);

/**
* @method _createUI
**/
ZaDomainController.prototype._createUI =
function (entry) {
	this._contentView = this._view = new this.tabConstructor(this._container, entry);

	this._initToolbar();
    this._initPopupMenu();
	//always add Help button at the end of the toolbar
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));							
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);	
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_DOMAIN);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
    if (!appNewUI) {
        elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
        var tabParams = {
            openInNewTab: true,
            tabId: this.getContentViewId()
        }
        ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
    } else
        ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
	this._UICreated = true;
	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
}

ZaDomainController.prototype._saveChanges = 
function () {
	var tmpObj = this._view.getObject();
	//Check the data
	if(tmpObj.attrs == null ) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	var mods = new Object();
	var haveSmth = false; //what is this variable for?
    var renameNotebookAccount = false;
    var catchAllChanged = false ;
	var skinChanged = false;
	
	this._currentObject["mods"] = mods;

    if (!(AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress]) && AjxUtil.isEmpty(this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress])) 
    	&& (tmpObj[ZaAccount.A_zimbraMailCatchAllAddress] != this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress])) {
         catchAllChanged = true ;
    }

        // execute other plugin methods
        if(ZaController.saveChangeCheckMethods["ZaDomainController"]) {
                var methods = ZaController.saveChangeCheckMethods["ZaDomainController"];
                var cnt = methods.length;
                for(var i = 0; i < cnt && !haveSmth; i++) {
                        if(typeof(methods[i]) == "function")
                               haveSmth =  methods[i].call(this, mods, tmpObj, this._currentObject);
                }
        }

	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_zimbraId || a==ZaDomain.A_domainName  || a == ZaDomain.A_domainType
                || a == ZaItem.A_zimbraACE) {
			continue;
		}
		if(!ZaItem.hasWritePermission(a,tmpObj)) {
				continue;
		}
		if (!(AjxUtil.isEmpty(this._currentObject.attrs[a]) && AjxUtil.isEmpty(tmpObj.attrs[a]))) {
			if(tmpObj.attrs[a] instanceof Array) {
					if(
						!(this._currentObject.attrs[a] instanceof Array) 
						|| (this._currentObject.attrs[a] && tmpObj.attrs[a] && tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf())
                  		|| (this._currentObject.attrs[a] == null && tmpObj.attrs[a] != null)
                    	|| (this._currentObject.attrs[a] != null && (tmpObj.attrs[a] == null || tmpObj.attrs[a].length == 0)) 
                    )
                    {
						mods[a] = tmpObj.attrs[a];
						haveSmth = true;
					}	
			} else if(tmpObj.attrs[a] != this._currentObject.attrs[a]) {
				mods[a] = tmpObj.attrs[a];
				haveSmth = true;
				if(a == ZaDomain.A_zimbraSkinForegroundColor || a == ZaDomain.A_zimbraSkinBackgroundColor || 
					a == ZaDomain.A_zimbraSkinSecondaryColor || a == ZaDomain.A_zimbraSkinSelectionColor ||
					a == ZaDomain.A_zimbraSkinLogoURL || a == ZaDomain.A_zimbraSkinLogoLoginBanner || 
					a == ZaDomain.A_zimbraSkinLogoAppBanner) {
					skinChanged = true;
				}				
			}
		}
	}

	if(!this.checkCertKeyValid(tmpObj.attrs[ZaDomain.A_zimbraSSLCertificate],tmpObj.attrs[ZaDomain.A_zimbraSSLPrivateKey]))
		return false;
	// check validation expression, which should be email-like pattern
	if(tmpObj.attrs[ZaDomain.A_zimbraMailAddressValidationRegex]) {
		var regList = tmpObj.attrs[ZaDomain.A_zimbraMailAddressValidationRegex];
		var islegal = true;
		var regval = null;
		if(regList && regList instanceof Array) {
			for(var i = 0; i < regList.length && islegal; i++) {
				if (regList[i].indexOf("@") == -1) {
					islegal = false;
					regval = regList[i];
				}
			}
		} else if(regList) {
                        if (regList.indexOf("@") == -1) {
				islegal = false;
				regval = regList;
			}
		}
		if(!islegal) {
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_MSG_EmailValidReg, regval), 
				null, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
                        this._errorDialog.popup();
			return islegal;
		}
	}

	if(!haveSmth) {
		if(tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) { 
			if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds] 
				&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs
				&& this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds]
				&& this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs) {
				if(this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
				tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
					haveSmth = true;
				}
			}
		}
	}
	
	if(!haveSmth) {
		if(tmpObj[ZaDomain.A2_gal_sync_accounts] && tmpObj[ZaDomain.A2_gal_sync_accounts][0]) { 
			if(tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds] 
				&& tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs
				&& this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds]
				&& this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs) {
				if(this._currentObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval] !=
				tmpObj[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[ZaDataSource.A_zimbraDataSourcePollingInterval]) {
					haveSmth = true;
				}
			}
		}
	}
	if(haveSmth || catchAllChanged) {
		try { 
			if(renameNotebookAccount) {
				var account = new ZaAccount();
				account.load(ZaAccount.A_name,this._currentObject.attrs[ZaDomain.A_zimbraNotebookAccount]);
				account.rename(tmpObj.attrs[ZaDomain.A_zimbraNotebookAccount]);
			}

            //change the catchAllMailAddress for the account
            if (catchAllChanged) {
                //1. remove the old account catchAll
                if(!AjxUtil.isEmpty(this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress]) && !AjxUtil.isEmpty(this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress].id)) {
                	ZaAccount.modifyCatchAll (this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress].id, "") ;
                } else if (this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress] && ZaItem.ID_PATTERN.test(this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress])) {
                	ZaAccount.modifyCatchAll (this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress], "") ;
                }
                if(!AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress]) && !AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress].id)) {
                //2. Add the new account catchAll
                	ZaAccount.modifyCatchAll (tmpObj[ZaAccount.A_zimbraMailCatchAllAddress].id, this._currentObject.attrs[ZaDomain.A_domainName]) ;
                } else if(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress] && ZaItem.ID_PATTERN.test(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress])) {
                	ZaAccount.modifyCatchAll (tmpObj[ZaAccount.A_zimbraMailCatchAllAddress], this._currentObject.attrs[ZaDomain.A_domainName]) ;	
                	
                }
                if(!AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress])  && !AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress].id)) {
                //3. Set the new catchAll value to the current object
                	this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress] = tmpObj[ZaAccount.A_zimbraMailCatchAllAddress] ;
                } else if (!AjxUtil.isEmpty(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress]) && ZaItem.ID_PATTERN.test(tmpObj[ZaAccount.A_zimbraMailCatchAllAddress])) {
                	var acc = new ZaAccount(ZaApp.getInstance());
                	acc.load("id",tmpObj[ZaAccount.A_zimbraMailCatchAllAddress],false,true);
                	this._currentObject[ZaAccount.A_zimbraMailCatchAllAddress] = acc;
                }
            }

			if(haveSmth) {
				try {	
					this._currentObject.modify(mods, tmpObj);
				} catch (ex) {
					this._handleException(ex, "ZaAccountViewController.prototype._saveChanges", null, false);	
					return false;
				}
            }
            if(skinChanged) {
            	//get domains
            	try {
            		var mbxSrvrs = ZaApp.getInstance().getMailServers();
            		var serverList = [];
            		var cnt = mbxSrvrs.length;
            		for(var i=0; i<cnt; i++) {
            			if(ZaItem.hasRight(ZaServer.FLUSH_CACHE_RIGHT,mbxSrvrs[i])) {
            				serverList.push(mbxSrvrs[i]);
            			}
            		}
            		
            		if(serverList.length > 0) {
						ZaApp.getInstance().dialogs["confirmMessageDialog2"].setMessage(ZaMsg.Domain_flush_cache_q, DwtMessageDialog.INFO_STYLE);
						ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.YES_BUTTON, this.openFlushCacheDlg, this, [serverList]);		
						ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDelDlg, this, null);				
						ZaApp.getInstance().dialogs["confirmMessageDialog2"].popup();             			
            		}
            		
            	} catch (ex) {
					if (ex.code ==  ZmCsfeException.SVC_PERM_DENIED) {
						return;
					} else {
						throw (ex);
					}           		
            	}
           	
            }

            if (this._currentObject[ZaModel.currentTab]!= tmpObj[ZaModel.currentTab])
                this._currentObject[ZaModel.currentTab] = tmpObj[ZaModel.currentTab];

            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DomainModified,[this._currentObject.name]));
			return true;
		} catch (ex) {
			this._handleException(ex,"ZaDomainController.prototype._saveChanges");
		}
	} else {
        ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.DomainModified,[this._currentObject.name]));
		return true;
	}
}

ZaDomainController.prototype.openFlushCacheDlg = 
function (serverList) {
	ZaApp.getInstance().dialogs["confirmMessageDialog2"].popdown(); 
	if(!ZaApp.getInstance().dialogs["flushCacheDialog"]) {
		ZaApp.getInstance().dialogs["flushCacheDialog"] = new ZaFlushCacheXDialog(this._container);
	}
	serverList._version = 1;
	for(var i=0;i<serverList.length;i++) {
		serverList[i]["status"] = 0;
	}
	obj = {statusMessage:null,flushZimlet:false,flushSkin:true,flushLocale:false,serverList:serverList,status:0};
	ZaApp.getInstance().dialogs["flushCacheDialog"].setObject(obj);
	ZaApp.getInstance().dialogs["flushCacheDialog"].popup();
}

ZaDomainController.prototype.newDomain = 
function () {
	var newName = "";
	if(!this._currentDomainName) {
		this._currentDomainName = this._currentObject.attrs[ZaDomain.A_domainName];
	}	
	
	if(this._currentDomainName)
		newName = "." + this._currentDomainName;

	this._currentObject = new ZaDomain();
	
	this._currentObject.getAttrs = {all:true};
	this._currentObject.loadNewObjectDefaults("name","foo"+newName);
	this._currentObject.attrs[ZaDomain.A_domainName] = newName;
	this._showNewDomainWizard();
}

ZaDomainController.prototype._showNewDomainWizard = 
function () {
	try {
        if(!ZaApp.getInstance().dialogs["newDomainWizard"])
		    ZaApp.getInstance().dialogs["newDomainWizard"] = new ZaNewDomainXWizard(this._container, this._currentObject);
        this._newDomainWizard = ZaApp.getInstance().dialogs["newDomainWizard"];
		this._newDomainWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishNewButtonListener, this, null);			
		this._newDomainWizard.setObject(this._currentObject);
		this._newDomainWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showNewDomainWizard", null, false);
	}
}

// new button was pressed
ZaDomainController.prototype._newButtonListener =
function(ev) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = null;
		args["obj"] = ZaApp.getInstance().getDomainController();
		args["func"] = ZaDomainController.prototype.newDomain;
		//ask if the user wants to save changes		
		//ZaApp.getInstance().dialogs["confirmMessageDialog"] = ZaApp.getInstance().dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON]);								
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.saveAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
	} else {
		this.newDomain();
	}	
}


ZaDomainController.prototype.viewAccountsButtonListener  =
function (ev) {
   var domainName = this._view.getObject().name ;
   ZaDomain.searchAccountsInDomain (domainName) ;
}

ZaDomainController.prototype._galWizButtonListener =
function(ev) {
	try {
		this._galWizard = ZaApp.getInstance().dialogs["galWizard"] = new ZaGALConfigXWizard(this._container,this._currentObject);
        if(appNewUI){
            this._currentObject._extid=ZaUtil.getItemUUid();
            this._currentObject._editObject = this._currentObject;
        }else{
            this._galWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishGalButtonListener, this, null);
        }
		this._galWizard.setObject(this._currentObject);
		this._galWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showGalWizard", null, false);
	}
}


ZaDomainController.prototype._authWizButtonListener =
function(ev) {
	try {
        if(!this._authWizard) {
            if(appNewUI){
               this._authWizard = ZaApp.getInstance().dialogs["authWizard"] =  new ZaTaskAuthConfigWizard(this._container);
               this._currentObject._extid=ZaUtil.getItemUUid();
               this._currentObject._editObject = this._currentObject;
            } else{
                 this._authWizard = ZaApp.getInstance().dialogs["authWizard"] =  new ZaAuthConfigXWizard(this._container);
                 this._authWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishAuthButtonListener, this, null);
            }

        }
		this._authWizard.setObject(this._currentObject);
		this._authWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._showAuthWizard", null, false);
	}
}

ZaDomainController.prototype._autoProvWizButtonListener =
function(ev) {
	try {
		//this._autoProvWizard = ZaApp.getInstance().dialogs["autoProvWizard"] =  new ZaAutoProvConfigXWizard(this._container);
		//this._autoProvWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, ZaDomainController.prototype._finishAutoProvButtonListener, this, null);
        if(!this._autoProvWizard) {
            if(ZaApp.getInstance().dialogs["autoProvWizard"])
                 this._autoProvWizard = ZaApp.getInstance().dialogs["autoProvWizard"];
            else
                this._autoProvWizard = ZaApp.getInstance().dialogs["autoProvWizard"] = new ZaTaskAutoProvDialog(this._container, ZaMsg.NAD_AutoProvConfigTitle);//ZaAutoProvConfigXWizard(this._container);
        }
        if(appNewUI){
                this._currentObject._extid=ZaUtil.getItemUUid();
                this._currentObject._editObject = this._currentObject;
                this._autoProvWizard.registerCallback(DwtDialog.OK_BUTTON, ZaTaskAutoProvDialog.prototype.finishWizard, this._autoProvWizard, null);
        }else {
                this._autoProvWizard.registerCallback(DwtDialog.OK_BUTTON, ZaDomainListController.prototype._finishAutoProvButtonListener, this, null);
        }

        this._currentObject.currentTab = "1";
		this._autoProvWizard.setObject(this._currentObject);
		this._autoProvWizard.popup();
	} catch (ex) {
			this._handleException(ex, "ZaDomainController.prototype._autoProvWizButtonListener", null, false);
	}
}

ZaDomainController.prototype._finishGalButtonListener =
function(ev) {
	try {
		//var changeDetails = new Object();
		ZaDomain.modifyGalSettings.call(this._currentObject, this._galWizard.getObject()); 
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
		this.fireChangeEvent(this._currentObject);
		this._view.setObject(this._currentObject);		
		this._galWizard.popdown();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._finishGalButtonListener", null, false);
	}
	return;
}

ZaDomainController.prototype._notifyAllOpenTabs =
function() {
	var warningMsg = "<br><ul>";
	var hasItem = false;
        for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
                var tab = ZaAppTabGroup._TABS.get(i) ;
                var v = tab.getAppView() ;
                if (v && v._containedObject && v._containedObject.name) {
			var acctName = v._containedObject.name;
			var l = acctName.indexOf('@');
			var domain = null;
			if(l > 0) domain = acctName.substring(l+1);
			if(domain != null && domain == this._currentObject.attrs[ZaDomain.A_domainName]) {
				warningMsg += "<li>" + acctName + "</li>";
				hasItem = true;
			}
                }
        }
	warningMsg += "</ul></br>";
	if(hasItem)
		ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.WARN_CHANGE_AUTH_METH + warningMsg);	
}


ZaDomainController.prototype._finishAuthButtonListener =
function(ev) {
	try {
		ZaDomain.modifyAuthSettings.call(this._currentObject,this._authWizard.getObject());
		//var changeDetails = new Object();
		//if a modification took place - fire an DomainChangeEvent
		//changeDetails["obj"] = this._currentObject;
	
		this.fireChangeEvent(this._currentObject);
		this._view.setObject(this._currentObject);
		this._authWizard.popdown();
		this._notifyAllOpenTabs();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._finishAuthButtonListener", null, false);
	}
	return;
}

ZaDomainController.prototype._finishAutoProvButtonListener =
function(ev) {
	try {
        if(!this._autoProvWizard._checkGeneralConfig() || !this._autoProvWizard._checkEagerConfig()
                || !this._autoProvWizard._checkLazyConfig()) {
            return;
        }
        var savedObj = this._autoProvWizard.getObject();
        this._autoProvWizard._combineConfigureValues(savedObj);
		ZaDomain.modifyAutoPovSettings.call(this._currentObject,savedObj);
		this._view.setObject(this._currentObject);
		this._autoProvWizard.popdown();
		this._notifyAllOpenTabs();
	} catch (ex) {
		this._handleException(ex, "ZaDomainController.prototype._finishAutoProvButtonListener", null, false);
	}
	return;
}

/**
* @param 	ev event object
* This method handles "finish" button click in "New Domain" dialog
**/

ZaDomainController.prototype._finishNewButtonListener =
function(ev) {
	try {
		var obj = this._newDomainWizard.getObject();
		var domain = ZaItem.create(obj,ZaDomain,"ZaDomain");
		domain.load("id",domain.id,false,true);
		if(domain != null) {
			//if creation took place - fire an DomainChangeEvent
			this.fireCreationEvent(domain);
			if(domain.rights && domain.rights[ZaDomain.RIGHT_DELETE_DOMAIN])
				this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);
					
			this._newDomainWizard.popdown();
		}
	} catch (ex) {
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_EXISTS, ex);		
		} else {
			this._handleException(ex, "ZaDomainController.prototype._finishNewButtonListener", null, false);
		}
	}
	return;
}

ZaDomainController.prototype._checkMXButtonListener = 
function (ev) {
	var callback = new AjxCallback(this, this.checkMXCallback);
	ZaDomain.checkDomainMXRecord(this._currentObject, callback);
}


ZaDomainController.prototype.checkMXCallback = 
function (resp) {
	if(!resp)
		return;
	if(resp.isException()) {
		//var ex = resp.getException();
		//if(ex.msg && (ex.msg.indexOf("NameNotFoundException")>0 || ex.msg.indexOf("NoMXRecordsForDomain")>0)) {
		//	this.popupErrorDialog(AjxMessageFormat.format(ZaMsg.failedToGetMXRecords, [this._currentObject.name]));
		//} else {
		//	this._handleException(resp.getException(), "ZaDomainController.prototype.checkMXCallback", null, false);
		//}
		this.popupErrorDialog(AjxMessageFormat.format(ZaMsg.failedToGetMXRecords, [this._currentObject.name]));
		return;
	} 
	var response = resp.getResponse().Body.CheckDomainMXRecordResponse;
	if(response.code[0]._content=="Ok") {
		this.popupMsgDialog(ZaMsg.MX_RecordCheckSuccess);
	} else {
		var msgArray = [];
		msgArray.push(ZaMsg.foundTheseMXRecords);
		if(response.entry && response.entry.length>0) {
			var cnt = response.entry.length;
			for (var i=0;i<cnt;i++) {
				msgArray.push(response.entry[i]._content);
			}
		}
		this._errorDialog.setMessage(response.message[0]._content, msgArray.join("<br/>"), DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
		this._errorDialog.popup();
	}
	
}

ZaDomainController.prototype._handleException = 
function (ex, method, params, restartOnError, obj) {
	if(ex.code == ZmCsfeException.DOMAIN_NOT_EMPTY) {
		this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_NOT_EMPTY);
		
	} else if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
		this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_EXISTS);
		
	} else {
		ZaController.prototype._handleException.call(this, ex, method, params, restartOnError, obj);				
	}	
}

ZaDomainController.prototype.checkCertKeyValid = 
function(cert, prvkey) {
	if(cert && prvkey) {
		var params = {
			type: "comm",
			cert: cert,
			prvkey: prvkey
		};
		resp = ZaCert.verifyCertKey(ZaApp.getInstance(), params);

		if(!resp){
                        this._errorDialog.setMessage(ZaMsg.SERVER_ERROR, ZaMsg.ERROR_DOMAIN_CERT_VERIFY, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
                        this._errorDialog.popup();
                        return false;
                 
		}

		var verifyResult = resp.verifyResult;
		if(verifyResult == "false") {
	                this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_CERT_KEY_VERIFY, ZaMsg.ALERT_DOMAIN_CERT_KEY, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
        	        this._errorDialog.popup();
			return false;
		 }else if(verifyResult == "invalid") {
                        this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_CERT_KEY_INVALID, null, DwtMessageDialog.CRITICAL_STYLE, ZaMsg.
zimbraAdminTitle);
                        this._errorDialog.popup();
                        return false;
		 }else if(verifyResult == "true") {
			return true;
		 } else return false;

	} else if(!cert && prvkey) {
                        this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_CERT_MISSING, null, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
                        this._errorDialog.popup();
			return false;
	} else if(cert && !prvkey) {
                        this._errorDialog.setMessage(ZaMsg.ERROR_DOMAIN_KEY_MISSING, null, DwtMessageDialog.CRITICAL_STYLE, ZabMsg.zimbraAdminTitle);
                        this._errorDialog.popup();
                        return false;
	}
	return true;
}

ZaDomainController.prototype.handleDomainChange =
function (ev) {
	var methods = ZaController.postChangeMethods["ZaDomainController"];
	for (var i in methods) {
		var method = methods[i];
		if (typeof(method) == "function") {
			method.call(this, ev);
		}
	}
}
