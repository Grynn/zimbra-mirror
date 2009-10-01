/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaVersionCheckViewController 
* @contructor ZaVersionCheckViewController
* @param appCtxt
* @param container
* @author Greg Solovyev
**/
ZaVersionCheckViewController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaVersionCheckViewController");
	this._UICreated = false;
	this.objType = ZaEvent.S_GLOBALCONFIG;
	this.tabConstructor = ZaVersionCheckXFormView;					
}

ZaVersionCheckViewController.prototype = new ZaXFormViewController();
ZaVersionCheckViewController.prototype.constructor = ZaVersionCheckViewController;

ZaController.setViewMethods["ZaVersionCheckViewController"] = [];

ZaVersionCheckViewController.setViewMethod = function (item) {
    if(!this._UICreated) {
  		this._ops = new Array();
		this._ops.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
		this._ops.push(new ZaOperation(ZaOperation.NONE));
		this._ops.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));
		this._toolbar = new ZaToolBar(this._container, this._ops);

		this._contentView = this._view = new this.tabConstructor(this._container,item);
		var elements = new Object();
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
		elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab()
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
	item.load();
	try {
		item[ZaModel.currentTab] = "1"
		this._view.setDirty(false);
		this._view.setObject(item);
	} catch (ex) {
		this._handleException(ex, "ZaVersionCheckViewController.prototype.show", null, false);
	}
	this._currentObject = item;
}
ZaController.setViewMethods["ZaVersionCheckViewController"].push(ZaVersionCheckViewController.setViewMethod) ;

ZaVersionCheckViewController.prototype._saveChanges =
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
	if(!AjxUtil.isEmpty(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckSendNotifications])) {
		if(AjxUtil.isEmpty(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmail])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [com_zimbra_adminversioncheck.MSG_zimbraVersionCheckNotificationEmail]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
		
		if(AjxUtil.isEmpty(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [com_zimbra_adminversioncheck.MSG_zimbraVersionCheckNotificationEmailFrom]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
		
		if(AjxUtil.isEmpty(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationBody])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [com_zimbra_adminversioncheck.MSG_zimbraVersionCheckNotificationBody]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
		
		if(AjxUtil.isEmpty(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationSubject])) {
			//show error msg
			this._errorDialog.setMessage(AjxMessageFormat.format(ZaMsg.ERROR_INVALID_VALUE_FOR, [com_zimbra_adminversioncheck.MSG_zimbraVersionCheckNotificationSubject]), null, DwtMessageDialog.CRITICAL_STYLE, null);
			this._errorDialog.popup();		
			return false;
		}
	}
	
	//check if "from" account is real
	if(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom]) {
		if(tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom] != this._currentObject.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom]) {
			var testA = new ZaAccount();
			try {
				testD.load("name",tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom]);
			} catch (ex) {
				if (ex.code == ZmCsfeException.ACCT_NO_SUCH_ACCOUNT) {
					this._errorDialog.setMessage(AjxMessageFormat.format(com_zimbra_adminversioncheck.ERROR_WRONG_ACCOUNT, [tmpObj.attrs[ZaVersionCheck.A_zimbraVersionCheckNotificationEmailFrom]]), null, DwtMessageDialog.CRITICAL_STYLE, null);
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
	//save the model
	this._currentObject.modify(mods);
	return true;
}
