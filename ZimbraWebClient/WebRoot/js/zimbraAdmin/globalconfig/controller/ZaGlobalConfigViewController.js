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
* @class ZaGlobalConfigViewController 
* @contructor ZaGlobalConfigViewController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
ZaGlobalConfigViewController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaGlobalConfigViewController");
	this._UICreated = false;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_global_settings/global_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = GlobalConfigXFormView;					
}

ZaGlobalConfigViewController.prototype = new ZaXFormViewController();
ZaGlobalConfigViewController.prototype.constructor = ZaGlobalConfigViewController;

//ZaGlobalConfigViewController.STATUS_VIEW = "ZaGlobalConfigViewController.STATUS_VIEW";
ZaController.initToolbarMethods["ZaGlobalConfigViewController"] = new Array();
ZaController.setViewMethods["ZaGlobalConfigViewController"] = [];
ZaController.changeActionsStateMethods["ZaGlobalConfigViewController"] = [];

/**
* Adds listener to removal of an ZaDomain 
* @param listener
**/
ZaGlobalConfigViewController.prototype.addSettingsChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

ZaGlobalConfigViewController.prototype.show = 
function(item, openInNewTab) {
	this._setView(item, false);
}

ZaGlobalConfigViewController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.SAVE] = new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener));    			
	this._toolbarOperations[ZaOperation.DOWNLOAD_GLOBAL_CONFIG] = new ZaOperation(ZaOperation.DOWNLOAD_GLOBAL_CONFIG, ZaMsg.TBB_DownloadConfig, ZaMsg.GLOBTBB_DownloadConfig_tt, "DownloadGlobalConfig", "DownloadGlobalConfig", new AjxListener(this, this.downloadConfigButtonListener));
	this._toolbarOrder.push(ZaOperation.SAVE);
	this._toolbarOrder.push(ZaOperation.DOWNLOAD_GLOBAL_CONFIG);
}
ZaController.initToolbarMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.initToolbarMethod);


ZaGlobalConfigViewController.setViewMethod = function (item) {
    try {
	    if ( !this._UICreated || (this._view == null) || (this._toolbar == null)) {
            this._initToolbar();
            this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
            this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
            this._toolbarOrder.push(ZaOperation.NONE);
            this._toolbarOrder.push(ZaOperation.HELP);
            this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder, null, null, ZaId.VIEW_GSET);
            this._contentView = this._view = new this.tabConstructor(this._container,item);
            var elements = new Object();
            elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
            elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
            var tabParams = {
                openInNewTab: false,
                tabId: this.getContentViewId(),
                tab: this.getMainTab()
            }
            //ZaApp.getInstance().createView(ZaZimbraAdmin._GLOBAL_SETTINGS,elements);
            ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
            this._UICreated = true;
            ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
        }
		//ZaApp.getInstance().pushView(ZaZimbraAdmin._GLOBAL_SETTINGS);
		ZaApp.getInstance().pushView(this.getContentViewId());
		item.load();
	
		item[ZaModel.currentTab] = "1"
		this._view.setDirty(false);
		this._view.setObject(item);
	} catch (ex) {
		this._handleException(ex, "ZaGlobalConfigViewController.prototype.show", null, false);
	}
	this._currentObject = item;
}
ZaController.setViewMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.setViewMethod) ;

ZaGlobalConfigViewController.prototype.setEnabled = 
function(enable) {
	this._view.setEnabled(enable);
}

ZaGlobalConfigViewController.changeActionsStateMethod =
function () {
    if(this._toolbarOperations[ZaOperation.SAVE]) {
        this._toolbarOperations[ZaOperation.SAVE].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.changeActionsStateMethod);


/**
* handles "download" button click. Launches file download in a new window
**/
ZaGlobalConfigViewController.prototype.downloadConfigButtonListener = 
function(ev) {
	window.open("/service/collectldapconfig/");
}

ZaGlobalConfigViewController.prototype._saveChanges =
function () {
	var tmpObj = this._view.getObject();
	var isNew = false;
	if(tmpObj.attrs == null) {
		//show error msg
		this._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._errorDialog.popup();		
		return false;	
	}

	//check values
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraSmtpPort,tmpObj)) {
		if(!AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaGlobalConfig.A_zimbraSmtpPort])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [ZaMsg.NAD_SmtpPort]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
	}	
	//check if domain is real
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraDefaultDomainName,tmpObj)) {
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
	}	
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraGalMaxResults,tmpObj)) {
		if(!AjxUtil.isNonNegativeLong(tmpObj.attrs[ZaGlobalConfig.A_zimbraGalMaxResults])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR,[ZaMsg.MSG_zimbraGalMaxResults]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}	
	}	
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraScheduledTaskNumThreads,tmpObj)) {
		if (tmpObj.attrs[ZaGlobalConfig.A_zimbraScheduledTaskNumThreads] &&
		 	 !AjxUtil.isPositiveInt(tmpObj.attrs[ZaGlobalConfig.A_zimbraScheduledTaskNumThreads])) {
				//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR,[ZaMsg.NAD_zimbraScheduledTaskNumThreads]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}	
	}	
	// update zimbraMtaRestriction (except RBLs)
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraMtaRestriction,tmpObj)) {
		var restrictions = [];
		for (var i = 0; i < ZaGlobalConfig.MTA_RESTRICTIONS.length; i++) {
			var restriction = ZaGlobalConfig.MTA_RESTRICTIONS[i];
			if (tmpObj.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction]) {
				restrictions.push(restriction);
			}			
		}
		var dirty = restrictions.length > 0;
		if (tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction]) {
			var prevRestrictions = AjxUtil.isString(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction])
			                     ? [ tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction] ]
			                     : tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction];
			dirty = restrictions.length != prevRestrictions.length;
			if (!dirty) {
				for (var i = 0; i < prevRestrictions.length; i++) {
					var restriction = prevRestrictions[i];
					if (!tmpObj.attrs["_"+ZaGlobalConfig.A_zimbraMtaRestriction+"_"+restriction]) {
						dirty = true;
						break;
					}
				}
			}
		}
		//check RBLs
		var numRBLs = tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].length;
		if( (numRBLs !=  this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].length) ||
			(tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].join("") != this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient].join(""))) {
			dirty = true;
		}
		for(var ix=0;ix<numRBLs;ix++) {
			restrictions.push("reject_rbl_client "+tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRejectRblClient][ix]);
		}
	
		if (dirty) {
			tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaRestriction] = restrictions;
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
	if(ZaItem.hasWritePermission(ZaGlobalConfig.A_zimbraMtaBlockedExtension,tmpObj)) {
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
	}	
	//save the model
	//var changeDetails = new Object();
	this._currentObject.modify(mods);
	
	return true;
}


