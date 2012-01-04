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
	this._helpButtonText = ZaGlobalConfigViewController.helpButtonText;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = GlobalConfigXFormView;					
}

ZaGlobalConfigViewController.prototype = new ZaXFormViewController();
ZaGlobalConfigViewController.prototype.constructor = ZaGlobalConfigViewController;
ZaGlobalConfigViewController.helpButtonText = ZaMsg.helpManageGlobalSettings;

//ZaGlobalConfigViewController.STATUS_VIEW = "ZaGlobalConfigViewController.STATUS_VIEW";
ZaController.initToolbarMethods["ZaGlobalConfigViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaGlobalConfigViewController"] = new Array();
ZaController.setViewMethods["ZaGlobalConfigViewController"] = [];
ZaController.changeActionsStateMethods["ZaGlobalConfigViewController"] = [];
ZaXFormViewController.preSaveValidationMethods["ZaGlobalConfigViewController"] = new Array();
//qin
ZaController.saveChangeCheckMethods["ZaGlobalConfigViewController"] = new Array();

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

ZaGlobalConfigViewController.initPopupMenuMethod =
function () {
    for (var key in this._toolbarOperations) {
        // For zimlet issue.

        this._popupOperations[key] = ZaOperation.duplicate(this._toolbarOperations[key]);
    }
}
ZaController.initPopupMenuMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.initPopupMenuMethod);

ZaGlobalConfigViewController.prototype.getAppBarAction =
function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
        this._appbarOperation[ZaOperation.SAVE]= new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "", "", new AjxListener(this, this.saveButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaGlobalConfigViewController.prototype.getAppBarOrder =
function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
        this._appbarOrder.push(ZaOperation.SAVE);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
}

ZaGlobalConfigViewController.setViewMethod = function (item) {
    try {
	    if ( !this._UICreated || (this._view == null) || (this._toolbar == null)) {
            this._initToolbar();
            this._initPopupMenu();
            this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
            this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
            this._toolbarOrder.push(ZaOperation.NONE);
            this._toolbarOrder.push(ZaOperation.HELP);
            this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder, null, null, ZaId.VIEW_GSET);
            this._contentView = this._view = new this.tabConstructor(this._container,item);
            var elements = new Object();
            elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
            if (!appNewUI) {
                elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
                var tabParams = {
                    openInNewTab: false,
                    tabId: this.getContentViewId(),
                    tab: this.getMainTab()
                }
                ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
            } else
                ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
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
		
		//check policy service
		var numPolicyService = tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaPolicyService].length;
                if( (numPolicyService !=  this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaPolicyService].length) || 
                       (tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaPolicyService].join("") != this._currentObject.attrs[ZaGlobalConfig.A_zimbraMtaPolicyService].join(""))) {
                        dirty = true;
                }
                for(var ix=0;ix<numPolicyService;ix++) {
                        restrictions.push("check_policy_service "+tmpObj.attrs[ZaGlobalConfig.A_zimbraMtaPolicyService][ix]);
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

	// check validation expression, which should be email-like pattern
        if(tmpObj.attrs[ZaGlobalConfig.A_zimbraMailAddressValidationRegex]) {
                var regList = tmpObj.attrs[ZaGlobalConfig.A_zimbraMailAddressValidationRegex];
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

        //transfer the fields from the tmpObj to the _currentObject, since _currentObject is an instance of ZaDomain
        var mods = new Object();

        // execute other plugin methods
        if(ZaController.saveChangeCheckMethods["ZaGlobalConfigViewController"]) {
                var methods = ZaController.saveChangeCheckMethods["ZaGlobalConfigViewController"];
                var cnt = methods.length;
                for(var i = 0; i < cnt; i++) {
                        if(typeof(methods[i]) == "function")
                               methods[i].call(this, mods, tmpObj, this._currentObject);
                }
        }
	
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
		else if(!(this._currentObject.attrs[a] instanceof Array))
			this._currentObject.attrs[a] = [this._currentObject.attrs[a]];
                
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
    if (this._currentObject[ZaModel.currentTab]!= tmpObj[ZaModel.currentTab])
             this._currentObject[ZaModel.currentTab] = tmpObj[ZaModel.currentTab];
	//var changeDetails = new Object();
	this._currentObject.modify(mods,tmpObj);

    // skin modification needs to restart server
    if(mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinForegroundColor)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinBackgroundColor)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinSecondaryColor)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinSelectionColor)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinLogoURL)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinLogoLoginBanner)
            ||  mods.hasOwnProperty(ZaGlobalConfig.A_zimbraSkinLogoAppBanner)
            ) {
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
                    this._handleException(ex, "ZaGlobalConfigViewController.prototype._saveChange", null, false);
                    return false;
            	}

    }
    ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(ZaMsg.GlobalConfigModified);
	return true;
}

ZaGlobalConfigViewController.prototype.validateMyNetworks = ZaServerController.prototype.validateMyNetworks;
ZaXFormViewController.preSaveValidationMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.prototype.validateMyNetworks);

ZaGlobalConfigViewController.prototype.openFlushCacheDlg =
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
