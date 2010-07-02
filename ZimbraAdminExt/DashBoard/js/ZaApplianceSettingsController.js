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
* @class ZaApplianceSettingsController 
* @contructor ZaApplianceSettingsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaApplianceSettingsController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaApplianceSettingsController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm#appliance/zap_managing_the_server_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = ZaApplianceSettingsView;					
}

ZaApplianceSettingsController.prototype = new ZaXFormViewController();
ZaApplianceSettingsController.prototype.constructor = ZaApplianceSettingsController;

//ZaApplianceSettingsController.STATUS_VIEW = "ZaApplianceSettingsController.STATUS_VIEW";
ZaController.initToolbarMethods["ZaApplianceSettingsController"] = new Array();
ZaController.setViewMethods["ZaApplianceSettingsController"] = [];
ZaController.changeActionsStateMethods["ZaApplianceSettingsController"] = [];

ZaOperation.ACCOUNT_IMPORT_WIZARD =  ++ZA_OP_INDEX;
ZaOperation.MIGRATION_WIZARD =  ++ZA_OP_INDEX;

ZaApp.prototype.getApplianceSettingsController = function() {
	var c  = new ZaApplianceSettingsController(this._appCtxt, this._container, this);
	return c ;
}

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/
ZaApplianceSettingsController.prototype.addSettingsChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

ZaApplianceSettingsController.prototype.show = function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaApplianceSettingsController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.SAVE] = new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));    			
	this._toolbarOrder.push(ZaOperation.SAVE);
}
ZaController.initToolbarMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.initToolbarMethod);


ZaApplianceSettingsController.setViewMethod = function (item) {
    try {
    	this._initToolbar();
    	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
        this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
        this._toolbarOrder.push(ZaOperation.NONE);
        this._toolbarOrder.push(ZaOperation.HELP);
        this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder);
        this._contentView = this._view = new this.tabConstructor(this._container,item);
        var elements = new Object();
        elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
        elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
        var tabParams = {
            openInNewTab: true,
            tabId: this.getContentViewId(),
            closable:false,
            selected:false
        }
        ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
        ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
       // ZaApp.getInstance().pushView(this.getContentViewId());        		
        item.load();
        item[ZaModel.currentTab] = "1"
        item.id = ZaItem.GLOBAL_CONFIG;
        this._view.setDirty(false);
        this._view.setObject(item);
	} catch (ex) {		
		this._handleException(ex, "ZaApplianceSettingsController.prototype.show", null, false);
	}
	this._currentObject = item;	
}
ZaController.setViewMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.setViewMethod) ;
                                           
ZaApplianceSettingsController.prototype.setEnabled = 
function(enable) {
	this._view.setEnabled(enable);
}

ZaApplianceSettingsController.changeActionsStateMethod =
function () {
    if(this._toolbarOperations[ZaOperation.SAVE]) {
        this._toolbarOperations[ZaOperation.SAVE].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaApplianceSettingsController"].push(ZaApplianceSettingsController.changeActionsStateMethod);


ZaApplianceSettingsController.prototype._saveChanges =
function () {
	var tmpObj = this._view.getObject();
	var isNew = false;
	if(tmpObj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	
	//check if domain is real
	if(tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]) {
		if(tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName] != this._currentObject.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]) {
			var testD = new ZaDomain();
			try {
				testD.load("name",tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]);
			} catch (ex) {
				if (ex.code == ZmCsfeException.NO_SUCH_DOMAIN) {
					this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_WRONG_DOMAIN_IN_GS, [tmpObj.attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]]), null, DwtMessageDialog.CRITICAL_STYLE, null);
					this._errorDialog.popup();	
					return false;	
				} else {
					throw (ex);
				}
			}
		}
	}	

	//transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaDomain
	var mods = new Object();
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || a == ZaGlobalConfig.A_zimbraAccountClientAttr ||
		a == ZaGlobalConfig.A_zimbraServerInheritedAttr || a == ZaGlobalConfig.A_zimbraDomainInheritedAttr ||
		a == ZaGlobalConfig.A_zimbraCOSInheritedAttr || a == ZaGlobalConfig.A_zimbraGalLdapAttrMap || 
		a == ZaGlobalConfig.A_zimbraGalLdapFilterDef || /^_/.test(a) || a == ZaGlobalConfig.A_zimbraMtaBlockedExtension || a == ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension
                || a == ZaItem.A_zimbraACE)
			continue;

		if(!ZaItem.hasWritePermission(a,tmpObj)) {
			continue;
		}		
		
		if ((this._currentObject.attrs[a] != tmpObj.attrs[a]) && !(this._currentObject.attrs[a] == undefined && tmpObj.attrs[a] === "")) {
			if(tmpObj.attrs[a] instanceof Array) {
                if (!this._currentObject.attrs[a]) 
                	this._currentObject.attrs[a] = [] ;
                	
                if( tmpObj.attrs[a].join(",").valueOf() !=  this._currentObject.attrs[a].join(",").valueOf()) {
					mods[a] = tmpObj.attrs[a];
				}
			} else {
				mods[a] = tmpObj.attrs[a];
			}				
		}
	}
	//check if blocked extensions are changed
	if(!AjxUtil.isEmpty(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		if(
			(
				(!this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension] || !this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].length))
				|| (tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].join("") != this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension].join(""))
			) {
			mods[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension];
		} 
	} else if (AjxUtil.isEmpty(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])  && !AjxUtil.isEmpty(this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaBlockedExtension])) {
		mods[ZaGlobalConfig.A_zimbraMtaBlockedExtension] = "";
	}

    //check the change of secure connections
    if (tmpObj [ZaApplianceSettings.A2_secureConnection] != this._currentObject [ZaApplianceSettings.A2_secureConnection]) {
        if (tmpObj [ZaApplianceSettings.A2_secureConnection] == "TRUE") {
            mods [ZaGlobalConfig.A_zimbraMailMode] = "redirect" ;
            mods [ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled] = "FALSE";
            mods [ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled] = "FALSE";
            mods [ZaGlobalConfig.A_zimbraImapSSLServerEnabled] = "TRUE" ;
            mods [ZaGlobalConfig.A_zimbraPop3SSLServerEnabled] = "TRUE" ;
        } else if (tmpObj [ZaApplianceSettings.A2_secureConnection] == "FALSE") {
            mods [ZaGlobalConfig.A_zimbraMailMode] = "both" ;
            mods [ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled] = "TRUE";
            mods [ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled] = "TRUE";
            mods [ZaGlobalConfig.A_zimbraImapSSLServerEnabled] = "TRUE" ;
            mods [ZaGlobalConfig.A_zimbraPop3SSLServerEnabled] = "TRUE" ;
        }
    }

	//save the model

	this._currentObject.modify(mods);
	
	return true;
}


