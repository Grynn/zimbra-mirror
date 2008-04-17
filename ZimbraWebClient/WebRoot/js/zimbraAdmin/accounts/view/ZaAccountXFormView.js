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
* This class describes a view of a single email Account
* @class ZaAccountXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
ZaAccountXFormView = function(parent, app) {
	ZaTabView.call(this, parent, app, "ZaAccountXFormView");	
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_ACTIVE)},
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_CLOSED)},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKED)},
		{value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_MAINTENANCE)}
	];
	this.TAB_INDEX = 0;
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
	
	this._domains = {} ;
}

ZaAccountXFormView.prototype = new ZaTabView();
ZaAccountXFormView.prototype.constructor = ZaAccountXFormView;
ZaTabView.XFormModifiers["ZaAccountXFormView"] = new Array();
ZaAccountXFormView.prototype.TAB_INDEX=0;
ZaAccountXFormView.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaAccountXFormView.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);

/**
* Sets the object contained in the view
* @param entry - {ZaAccount} object to display
**/
ZaAccountXFormView.prototype.setObject =
function(entry) {
	//handle the special attributes to be displayed in xform
	entry.manageSpecialAttrs();
	
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			for(var aa in entry.attrs[a]) {
				this._containedObject.attrs[a][aa] = entry.attrs[a][aa];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	if(entry.id)
		this._containedObject.id = entry.id;
	
	//add the member group
	this._containedObject[ZaAccount.A2_memberOf] = entry [ZaAccount.A2_memberOf];
	//add the memberList page information
	this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
	
	if ((typeof ZaDomainAdmin == "function")) {
		this._containedObject[ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed] = entry [ZaAccount.A2_zimbraDomainAdminMailQuotaAllowed];
	}	
	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		if(this._containedObject.attrs[ZaAccount.A_zimbraMailAlias]) {
			if(!this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] instanceof Array) {
				this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] = [this._containedObject.attrs[ZaAccount.A_zimbraMailAlias]];		
			}
		}		
	}	
	
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		if(this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress]) {
			if(!this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress] instanceof Array) {
				this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress] = [this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress]];		
			}
		}		
	}
					
	if(ZaSettings.COSES_ENABLED) {	
		/**
		* If this account does not have a COS assigned to it - assign default COS
		**/
		if(this._containedObject.attrs[ZaAccount.A_COSId]) {	
			this._containedObject[ZaAccount.A2_autoCos] = "FALSE" ;
			this._containedObject.cos = this._app.getCosList().getItemById(this._containedObject.attrs[ZaAccount.A_COSId]);
		}
		if(!this._containedObject.cos) {
			this._containedObject[ZaAccount.A2_autoCos] = "TRUE" ;
			/**
			* We did not find the COS assigned to this account,
			* this means that the COS was deleted or wasn't assigned, therefore assign default COS to this account
			**/
			ZaAccount.setDefaultCos(this._containedObject, this._app.getCosList(), this._app) ;
			if(!this._containedObject.cos) {
				//default COS was not found - just assign the first COS
				var hashMap = this._app.getCosList().getIdHash();
				for(var id in hashMap) {
					this._containedObject.cos = hashMap[id];
					this._containedObject.attrs[ZaAccount.A_COSId] = id;					
					break;
				}
			}
		}
	}
	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname];
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword];
	
	if(ZaSettings.GLOBAL_CONFIG_ENABLED) {
		this._containedObject.globalConfig = this._app.getGlobalConfig();
	}
   	
			
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
	
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		var skins = entry.attrs[ZaAccount.A_zimbraAvailableSkin];
		if(skins != null && skins != "") {
			if (AjxUtil.isString(skins))	 {
				skins = [skins];
			}
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = skins;
		} else {
			this._containedObject.attrs[ZaAccount.A_zimbraAvailableSkin] = null;		
		}

		var skins = this._app.getInstalledSkins();
		if(skins == null) {
			skins = [];
		} else if (AjxUtil.isString(skins))	 {
			skins = [skins];
		}
		
		ZaAccountXFormView.themeChoices.setChoices(skins);
		ZaAccountXFormView.themeChoices.dirtyChoices();		
		
	}
	
	if(ZaSettings.ZIMLETS_ENABLED) {
		var zimlets = entry.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
		if(zimlets != null && zimlets != "") {
			if (AjxUtil.isString(zimlets))	 {
				zimlets = [zimlets];
			}
			this._containedObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] = zimlets;
		} else
			this._containedObject.attrs[ZaAccount.A_zimbraZimletAvailableZimlets] = null;		
		
		
		//get sll Zimlets
		var allZimlets = ZaZimlet.getAll(this._app, "extension");
		if(allZimlets == null) {
			allZimlets = [];
		} 
		
		if(allZimlets instanceof ZaItemList || allZimlets instanceof AjxVector)
			allZimlets = allZimlets.getArray();
		
		//convert objects to strings	
		var cnt = allZimlets.length;
		var _tmpZimlets = [];
		for(var i=0; i<cnt; i++) {
			var zimlet = allZimlets[i];
			_tmpZimlets.push(zimlet.name);
		}
		ZaAccountXFormView.zimletChoices.setChoices(_tmpZimlets);
		ZaAccountXFormView.zimletChoices.dirtyChoices();		
	}
			
	this._localXForm.setInstance(this._containedObject);
	//update the tab
	this.updateTab();
}

ZaAccountXFormView.gotSkins = function () {
	if(!ZaSettings.SKIN_PREFS_ENABLED)
		return false;
	else 
		return ((this.parent._app.getInstalledSkins() != null) && (this.parent._app.getInstalledSkins().length > 0));
}



ZaAccountXFormView.onCOSChanged = 
function(value, event, form) {
	var cosList = form.getController().getCosList();
	form.getInstance().cos = cosList.getItemById(value);
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaAccountXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}

ZaAccountXFormView.isPasswordLockoutEnabled = function () {
	return (this.instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE' ||
		(!this.instance.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] && 
			this.instance.cos && 
			this.instance.cos.attrs[ZaAccount.A_zimbraPasswordLockoutEnabled] == 'TRUE')
		);
}

ZaAccountXFormView.isSendingFromAnyAddressAllowed = function () {
	return (this.instance.attrs[ZaAccount.A_zimbraAllowAnyFromAddress] == 'TRUE' ||
		(!this.instance.attrs[ZaAccount.A_zimbraAllowAnyFromAddress] && 
			this.instance.cos && 
			this.instance.cos.attrs[ZaAccount.A_zimbraAllowAnyFromAddress] == 'TRUE')
		);
}

ZaAccountXFormView.isMailSignatureEnabled = function () {
	return (this.instance.attrs[ZaAccount.A_prefMailSignatureEnabled] == 'TRUE');
}

ZaAccountXFormView.isOutOfOfficeReplyEnabled = function () {
	return (this.instance.attrs[ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled] == 'TRUE');
}

ZaAccountXFormView.isMailNotificationAddressEnabled = function () {
	return (this.instance.attrs[ZaAccount.A_zimbraPrefNewMailNotificationEnabled] == 'TRUE');
}

ZaAccountXFormView.aliasSelectionListener = 
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		instance.alias_selection_cache = arr;
	} else 
		instance.alias_selection_cache = null;
		
	this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editAliasButtonListener.call(this);
	}	
}

ZaAccountXFormView.isEditAliasEnabled = function () {
	return (this.instance.alias_selection_cache != null && this.instance.alias_selection_cache.length==1);
}

ZaAccountXFormView.isDeleteAliasEnabled = function () {
	return (this.instance.alias_selection_cache != null && this.instance.alias_selection_cache.length>0);
}

ZaAccountXFormView.deleteAliasButtonListener = function () {
	var instance = this.getInstance();
	if(instance.alias_selection_cache != null) {
		var cnt = instance.alias_selection_cache.length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraMailAlias]) {
			for(var i=0;i<cnt;i++) {
				var cnt2 = instance.attrs[ZaAccount.A_zimbraMailAlias].length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(instance.attrs[ZaAccount.A_zimbraMailAlias][k]==instance.alias_selection_cache[i]) {
						instance.attrs[ZaAccount.A_zimbraMailAlias].splice(k,1);
						break;	
					}
				}
			}
				
		}
	}
	this.getForm().parent.setDirty(true);
	this.getForm().refresh();
}

ZaAccountXFormView.editAliasButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.alias_selection_cache && instance.alias_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editAliasDlg) {
			formPage.editAliasDlg = new ZaEditAliasXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.Edit_Alias_Title);
			formPage.editAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateAlias, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance.alias_selection_cache[0];
		var cnt = instance.attrs[ZaAccount.A_zimbraMailAlias].length;
		for(var i=0;i<cnt;i++) {
			if(instance.alias_selection_cache[0]==instance.attrs[ZaAccount.A_zimbraMailAlias][i]) {
				obj[ZaAlias.A_index] = i;
				break;		
			}
		}
		
		formPage.editAliasDlg.setObject(obj);
		formPage.editAliasDlg.popup();		
	}
}

ZaAccountXFormView.updateAlias = function () {
	if(this.parent.editAliasDlg) {
		this.parent.editAliasDlg.popdown();
		var obj = this.parent.editAliasDlg.getObject();
		var instance = this.getInstance();
		if(obj[ZaAlias.A_index] >=0 && instance.attrs[ZaAccount.A_zimbraMailAlias][obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {
			instance.alias_selection_cache=new Array();
			instance.attrs[ZaAccount.A_zimbraMailAlias][obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			instance.attrs[ZaAccount.A_zimbraMailAlias]._version++;
			this.parent.setDirty(true);	
			this.refresh();				
		}
	}
}

ZaAccountXFormView.addAliasButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addAliasDlg) {
		formPage.addAliasDlg = new ZaEditAliasXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.Add_Alias_Title);
		formPage.addAliasDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.addAlias, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addAliasDlg.setObject(obj);
	formPage.addAliasDlg.popup();		
}

ZaAccountXFormView.addAlias  = function () {
	if(this.parent.addAliasDlg) {
		this.parent.addAliasDlg.popdown();
		var obj = this.parent.addAliasDlg.getObject();
		if(obj[ZaAccount.A_name] && obj[ZaAccount.A_name].length>1) {
			var instance = this.getInstance();
			instance.attrs[ZaAccount.A_zimbraMailAlias].push(obj[ZaAccount.A_name]);
			instance.alias_selection_cache=new Array();
			this.parent.setDirty(true);
			this.refresh();	
		}
	}
}

ZaAccountXFormView.isEditFwdAddrEnabled = function () {
	return (this.instance.fwdAddr_selection_cache != null && this.instance.fwdAddr_selection_cache.length==1);
}

ZaAccountXFormView.isDeleteFwdAddrEnabled = function () {
	return (this.instance.fwdAddr_selection_cache != null && this.instance.fwdAddr_selection_cache.length>0);
}

ZaAccountXFormView.deleteFwdAddrButtonListener = function () {
	var instance = this.getInstance();
	if(instance.fwdAddr_selection_cache != null) {
		var cnt = instance.fwdAddr_selection_cache.length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraMailForwardingAddress]) {
			for(var i=0;i<cnt;i++) {
				var cnt2 = instance.attrs[ZaAccount.A_zimbraMailForwardingAddress].length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(instance.attrs[ZaAccount.A_zimbraMailForwardingAddress][k]==instance.fwdAddr_selection_cache[i]) {
						instance.attrs[ZaAccount.A_zimbraMailForwardingAddress].splice(k,1);
						break;	
					}
				}
			}
				
		}
	}
	this.getForm().parent.setDirty(true);
	this.getForm().refresh();
}

ZaAccountXFormView.fwdAddrSelectionListener = 
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		instance.fwdAddr_selection_cache = arr;
	} else 
		instance.fwdAddr_selection_cache = null;
		
	this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editFwdAddrButtonListener.call(this);
	}	
}

ZaAccountXFormView.editFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.fwdAddr_selection_cache && instance.fwdAddr_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editFwdAddrDlg) {
			formPage.editFwdAddrDlg = new ZaEditFwdAddrXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"400px", "150px",ZaMsg.Edit_FwdAddr_Title);
			formPage.editFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateFwdAddr, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance.fwdAddr_selection_cache[0];
		var cnt = instance.attrs[ZaAccount.A_zimbraMailForwardingAddress].length;
		for(var i=0;i<cnt;i++) {
			if(instance.fwdAddr_selection_cache[0]==instance.attrs[ZaAccount.A_zimbraMailForwardingAddress][i]) {
				obj[ZaAlias.A_index] = i;
				break;		
			}
		}
		
		formPage.editFwdAddrDlg.setObject(obj);
		formPage.editFwdAddrDlg.popup();		
	}
}

ZaAccountXFormView.updateFwdAddr = function () {
	if(this.parent.editFwdAddrDlg) {
		this.parent.editFwdAddrDlg.popdown();
		var obj = this.parent.editFwdAddrDlg.getObject();
		var instance = this.getInstance();
		if(obj[ZaAlias.A_index] >=0 && instance.attrs[ZaAccount.A_zimbraMailForwardingAddress][obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {
			instance.fwdAddr_selection_cache=new Array();
			instance.attrs[ZaAccount.A_zimbraMailForwardingAddress][obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			instance.attrs[ZaAccount.A_zimbraMailForwardingAddress]._version++;
			this.parent.setDirty(true);	
			this.refresh();				
		}
	}
}

ZaAccountXFormView.addFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addFwdAddrDlg) {
		formPage.addFwdAddrDlg = new ZaEditFwdAddrXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"400px", "150px",ZaMsg.Add_FwdAddr_Title);
		formPage.addFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.addFwdAddr, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addFwdAddrDlg.setObject(obj);
	formPage.addFwdAddrDlg.popup();		
}

ZaAccountXFormView.addFwdAddr  = function () {
	if(this.parent.addFwdAddrDlg) {
		this.parent.addFwdAddrDlg.popdown();
		var obj = this.parent.addFwdAddrDlg.getObject();
		if(obj[ZaAccount.A_name] && obj[ZaAccount.A_name].length>1) {
			var instance = this.getInstance();
			instance.attrs[ZaAccount.A_zimbraMailForwardingAddress].push(obj[ZaAccount.A_name]);
			instance.fwdAddr_selection_cache=new Array();
			this.parent.setDirty(true);
			this.refresh();	
		}
	}
}

//interop account
ZaAccountXFormView.fpSelectionListener =
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		instance.fp_selection_cache = arr;
	} else
		instance.fp_selection_cache = null;

	this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editFpButtonListener.call(this);
	}
}

ZaAccountXFormView.isEditFpEnabled = function () {
	return (this.instance.fp_selection_cache != null && this.instance.fp_selection_cache.length==1
            && this.instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length > 0);
}

ZaAccountXFormView.isDeleteFpEnabled = function () {
	return (this.instance.fp_selection_cache != null && this.instance.fp_selection_cache.length>0
              && this.instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length > 0);
}

ZaAccountXFormView.isPushFpEnabled = function () {
	return (this.instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length > 0);
}

ZaAccountXFormView.deleteFpButtonListener = function () {
	var instance = this.getInstance();
	if(instance.fp_selection_cache != null) {
		var cnt = instance.fp_selection_cache.length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraForeignPrincipal]) {
			for(var i=0;i<cnt;i++) {
				var cnt2 = instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length-1;
				for(var k=cnt2;k>=0;k--) {
					if(instance.attrs[ZaAccount.A_zimbraForeignPrincipal][k]==instance.fp_selection_cache[i]) {
						instance.attrs[ZaAccount.A_zimbraForeignPrincipal].splice(k,1);
						break;
					}
				}
			}

		}
	}
	this.getForm().parent.setDirty(true);
	this.getForm().refresh();
}

ZaAccountXFormView.editFpButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.fp_selection_cache && instance.fp_selection_cache[0]) {
		var formPage = this.getForm().parent;
		if(!formPage.editFpDlg) {
			formPage.editFpDlg = new ZaEditFpXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.Edit_Fp_Title);
			formPage.editFpDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateFp, this.getForm(), null);
		}
		var obj = ZaFp.getObject (instance.fp_selection_cache[0]) ;
		var cnt = instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length;
		for(var i=0;i<cnt;i++) {
			if(instance.fp_selection_cache[0]==instance.attrs[ZaAccount.A_zimbraForeignPrincipal][i]) {
				obj[ZaFp.A_index] = i;
				break;
			}
		}

		formPage.editFpDlg.setObject(obj);
		formPage.editFpDlg.popup();
	}
}

ZaAccountXFormView.pushFpButtonListener = function () {
	var instance = this.getInstance();
    var app = this.getForm().parent._app ;
    if (this.getForm().parent.isDirty()) {
       app.getCurrentController().popupMsgDialog (ZaMsg.DIRTY_SAVE_ACCT, true);
    }else if (instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length > 0) {
	   ZaFp.push (app, instance.id);
  	}
}

ZaAccountXFormView.updateFp = function () {
	if(this.parent.editFpDlg) {
        this.parent.editFpDlg.popdown();
		var obj = this.parent.editFpDlg.getObject();
		var instance = this.getInstance();
		if(obj[ZaFp.A_index] >=0 && instance.attrs[ZaAccount.A_zimbraForeignPrincipal][obj[ZaFp.A_index]] != ZaFp.getEntry (obj) ) {
			instance.fp_selection_cache=new Array();
			instance.attrs[ZaAccount.A_zimbraForeignPrincipal][obj[ZaFp.A_index]] = ZaFp.getEntry (obj);
			instance.attrs[ZaAccount.A_zimbraForeignPrincipal]._version++;
			this.parent.setDirty(true);
			this.refresh();
		}
	}
}

ZaAccountXFormView.addFpButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
    
    if(!formPage.addFpDlg) {
		formPage.addFpDlg = new ZaEditFpXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px",ZaMsg.Add_Fp_Title);
		formPage.addFpDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.addFp, this.getForm(), null);
	}

    var obj = {};
    obj [ZaFp.A_prefix] = "" ;
    obj [ZaFp.A_name] = "";
    obj [ZaFp.A_index] = -1 ;
    
    formPage.addFpDlg.setObject(obj);
	formPage.addFpDlg.popup();
}

ZaAccountXFormView.addFp  = function () {
	if(this.parent.addFpDlg) {
        var obj = this.parent.addFpDlg.getObject();
        var app = this.parent._app ;
        var instance = this.getInstance();
        var currentFps =  instance.attrs[ZaAccount.A_zimbraForeignPrincipal] ;
        if (ZaFp.findDupPrefixFp(currentFps, obj[ZaFp.A_prefix])){
            app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_ONE_FP_PREFIX_ALLOWED, null);
        }  else {
            this.parent.addFpDlg.popdown();
            if(ZaFp.getEntry(obj).length > 0) {
                currentFps.push(ZaFp.getEntry(obj));
               // instance.fp_selection_cache=new Array();
                this.parent.setDirty(true);
                this.refresh();
            }
        }
    }
}

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaAccountXFormView.myXFormModifier = function(xFormObject) {	

	var domainName;
	if(ZaSettings.DOMAINS_ENABLED) {
		domainName = this._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		if(!domainName && this._app.getDomainList().size() > 0)
			domainName = this._app.getDomainList().getArray()[0].name;
	} else 
		domainName = ZaSettings.myDomainName;

		
	var emptyAlias = " @" + domainName;
	var headerItems = [{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:2},{type:_OUTPUT_, ref:ZaAccount.A_displayname, label:null,cssClass:"AdminTitle", rowSpan:2}];
	if(ZaSettings.COSES_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_COSId, labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, choices:this._app.getCosListChoices()});
	}
	if(ZaSettings.SERVERS_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_mailHost, labelLocation:_LEFT_,label:ZaMsg.NAD_MailServer});
	}
	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_accountStatus, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false});
	headerItems.push({type:_OUTPUT_,ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID});
	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A2_mbxsize, label:ZaMsg.usedQuota + ":",
						getDisplayValue:function() {
							var val = this.getInstanceValue();
							if(!val) 
								val = "0 MB ";
							else {
								val = Number(val / 1048576).toFixed(3) + " MB ";
							}									
							return val;
						}
					});
					
	headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_zimbraLastLogonTimestamp, 
						label:ZaMsg.ALV_Last_Login +":", labelLocation:_LEFT_,
						getDisplayValue:function() {
							var val = this.getInstanceValue();
							return ZaAccount.getLastLoginTime(val) ;
						}	
					 });
    //assigned quota
    headerItems.push ({type:_OUTPUT_,ref:ZaAccount.A2_quota, label:ZaMsg.assignedQuota + ":",
                        getDisplayValue:function() {
							var val = this.getInstanceValue();
                            return val + " MB" ;
						}
					});

    this.tabChoices = new Array();
	
	var _tab1 = ++this.TAB_INDEX;
	var _tab2 = ++this.TAB_INDEX;	
	var _tab3 = ++this.TAB_INDEX;	
	var _tab4 = ++this.TAB_INDEX;	
	var _tab5 = ++this.TAB_INDEX;		
	var _tab6 = ++this.TAB_INDEX;			
	var _tab7 = ++this.TAB_INDEX;	
	var _tab8 = ++this.TAB_INDEX;			
	var _tab9 = ++this.TAB_INDEX;		
	var _tab10 = ++this.TAB_INDEX;
    var _tab11 = ++this.TAB_INDEX;
		
/*	var _tab1 = 1;
	var _tab2 = 2;	
	var _tab3 = 3;	
	var _tab4 = 4;	
	var _tab5 = 5;		
	var _tab6 = 6;			
	var _tab7 = 7;	
	var _tab8 = 8;			
	var _tab9 = 9;		
	var _tab10 = 10;*/
	
	this.tabChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
	this.tabChoices.push({value:_tab2, label:ZaMsg.TABT_ContactInfo});
	this.tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});

	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED)
		this.tabChoices.push({value:_tab4, label:ZaMsg.TABT_Features});
					
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED)
		this.tabChoices.push({value:_tab5, label:ZaMsg.TABT_Preferences});

	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED)
		this.tabChoices.push({value:_tab6, label:ZaMsg.TABT_Aliases});

	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED)
		this.tabChoices.push({value:_tab7, label:ZaMsg.TABT_Forwarding});

    if (ZaSettings.ACCOUNTS_INTEROP_ENABLED) {
        this.tabChoices.push({value: _tab8, label: ZaMsg.TABT_Interop}) ;
    }

    if(ZaSettings.SKIN_PREFS_ENABLED)
		this.tabChoices.push({value:_tab9, label:ZaMsg.TABT_Themes});

	if(ZaSettings.ZIMLETS_ENABLED) 
		this.tabChoices.push({value:_tab10, label:ZaMsg.TABT_Zimlets});
			
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED)
		this.tabChoices.push({value:_tab11, label:ZaMsg.TABT_Advanced});


	var cases = [];
	var case1 = {type:_ZATABCASE_,  relevant:("instance[ZaModel.currentTab] == " + _tab1),   
		numCols:1};
	
	var case1Items = [
		 {type: _DWT_ALERT_, ref: ZaAccount.A2_domainLeftAccounts, relevant: "instance[ZaAccount.A2_domainLeftAccounts] != null",
				relevantBehavior: _HIDE_ , containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false
		 }, 
		{type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountNameGrouper, id:"account_form_name_group",
			colSizes:["275px","*"],numCols:2,
			items:[
			{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
							 labelLocation:_LEFT_,onChange:ZaAccount.setDomainChanged,forceUpdate:true},
			{ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, 
				labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccount.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:50,  onChange:ZaTabView.onFormFieldChanged,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccount.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
				elementChanged: function(elementValue,instanceValue, event) {
					if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
						ZaAccount.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
					}
					this.getForm().itemChanged(this, elementValue, event);
				}
			},
			{type:_GROUP_, numCols:3, nowrap:true, width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, 
				items: [
					{ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,	cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged, 
						relevant:"instance[ZaAccount.A2_autodisplayname] == \"FALSE\"",
						relevantBehavior:_DISABLE_
					},
					{ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
						elementChanged: function(elementValue,instanceValue, event) {
							if(elementValue=="TRUE") {
								if(ZaAccount.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials])) {
								//	this.getForm().itemChanged(this, elementValue, event);
									this.getForm().parent.setDirty(true);
								}
							}
							this.getForm().itemChanged(this, elementValue, event);
						}
					}
				]
			},
			{ref:ZaAccount.A_zimbraMailCanonicalAddress, type:_TEXTFIELD_,width:250,
				msgName:ZaMsg.NAD_CanonicalFrom,label:ZaMsg.NAD_CanonicalFrom, labelLocation:_LEFT_,  
				onChange:ZaTabView.onFormFieldChanged, align:_LEFT_
			},
			{ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_,
			  msgName:ZaMsg.NAD_zimbraHideInGal,
			  label:ZaMsg.NAD_zimbraHideInGal, trueValue:"TRUE", falseValue:"FALSE", 
			  onChange:ZaTabView.onFormFieldChanged
			}
		]}
	];

	var setupGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountSetupGrouper, id:"account_form_setup_group", 
		colSizes:["275px","*"],numCols:2,
		items: [
			{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,
				label:ZaMsg.NAD_AccountStatus, 
				labelLocation:_LEFT_, choices:this.accountStatusChoices, onChange:ZaTabView.onFormFieldChanged
			}
		]
	}
		
	if(ZaSettings.COSES_ENABLED) {
		setupGroup.items.push(
			{type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
				items: [
					{ref:ZaAccount.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService,label: null, 
						relevant:"instance[ZaAccount.A2_autoCos]==\"FALSE\"", relevantBehavior:_DISABLE_ ,
						labelLocation:_LEFT_, choices:this._app.getCosListChoices(), onChange:ZaAccountXFormView.onCOSChanged },
					{ref:ZaAccount.A2_autoCos, type:_CHECKBOX_, 
						msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
						trueValue:"TRUE", falseValue:"FALSE" ,
						elementChanged: function(elementValue,instanceValue, event) {
							this.getForm().parent.setDirty(true);
							if(elementValue=="TRUE") {
								ZaAccount.setDefaultCos(this.getInstance(), this.getForm().parent._app.getCosList(), this.getForm().parent._app);	
							}
							this.getForm().itemChanged(this, elementValue, event);
						}
					}
				]
			});
	}
	
	setupGroup.items.push({ref:ZaAccount.A_isAdminAccount, type:_CHECKBOX_, 
							msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin,
							trueValue:"TRUE", falseValue:"FALSE",relevantBehavior:_HIDE_,
							onChange:ZaTabView.onFormFieldChanged
						});
	case1Items.push(setupGroup);
	
	var passwordGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_form_password_group", 
		colSizes:["275px","*"],numCols:2,
		items:[
		{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
			label:ZaMsg.NAD_Password, labelLocation:_LEFT_, 
			cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged
		},
		{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
			label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, 
			cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged
		},
		{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,  
			msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE", 
		onChange:ZaTabView.onFormFieldChanged}
		]
	};
	case1Items.push(passwordGroup);														
	
	var notesGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_form_notes_group",
		colSizes:["275px","*"],numCols:2,
	 	items:[

		{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,
			label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", 
			onChange:ZaTabView.onFormFieldChanged
		},
		{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,
			label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", 
			onChange:ZaTabView.onFormFieldChanged, width:"30em"
		}
		]
	};

	case1Items.push(notesGroup);
	case1.items = case1Items;
	cases.push(case1);
	var case2={type:_ZATABCASE_, numCols:1, relevant:("instance[ZaModel.currentTab] == " + _tab2),
					items: [
						{type:_ZAGROUP_, 
							items:[
								{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250}
							]
						},
						{type:_ZAGROUP_, 
							items:[					
								{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},
								{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},														
								{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250}
							]
						},
						{type:_ZAGROUP_, 
							items:[						
								{ref:ZaAccount.A_street, type:_TEXTAREA_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},
								{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},
								{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250},
								{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:100},
								{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:250}
							]
						}							
					]
				};
	cases.push(case2);
	
	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
	
	//MemberOf Tab
	var case3={type:_ZATABCASE_, numCols:2, relevant:("instance[ZaModel.currentTab] == " + _tab3), colSizes: ["50%","50%"],
					items: [
						{type:_SPACER_, height:"10"},
						//layout rapper around the direct/indrect list						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
								//direct member group
								//{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "100%", colSizes:["auto"], //height: 400,
								{type:_ZALEFT_GROUPER_, numCols:1, width: "100%", 
									label:ZaMsg.Account_DirectGroupLabel,
									items:[
										/*{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_DirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},*/
										{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
											forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
												   relevant:"ZaAccountMemberOfListView.shouldEnableAllButton.call(this, ZaAccount.A2_directMemberList)",
												   onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)",
												   relevantBehavior:_DISABLE_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
											      onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
											      relevant:"ZaAccountMemberOfListView.shouldEnableAddRemoveButton.call(this, ZaAccount.A2_directMemberList)",
											      relevantBehavior:_DISABLE_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_directMemberList)"
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													relevantBehavior:_DISABLE_, 
													relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_directMemberList)"
											    },								       
												{type:_CELLSPACER_}									
											]
										}		
									]
								},	
								//{type:_CELLSPACER_},	
								{type:_SPACER_, height:"10"},	
								//indirect member group
							//	{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "100%", //colSizes:["auto"], height: "48%",
								{type:_ZALEFT_GROUPER_, numCols:1,  width: "100%", label:ZaMsg.Account_IndirectGroupLabel,
									items:[
								/*		{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_IndirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},*/
										//{type:_SPACER_, height:"5"},
										{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
											forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_indirectMemberList)"
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													relevantBehavior:_DISABLE_, 
													relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_indirectMemberList)"
											    },								       
												{type:_CELLSPACER_}									
											]
										}
									]
								}
								//{type:_CELLSPACER_}	
							]
						},

						//non member group
						//{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "98%", //colSizes:["auto"], height: "98%",
						{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_NonGroupLabel,
							items:[
								/*{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.Account_NonGroupLabel, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},*/
								{type:_GROUP_, numCols:5, colSizes:[30, "auto",10,80, 120,20], width:"100%", 
								   items:[
								   		{type:_OUTPUT_, value:ZaMsg.DLXV_LabelFind, nowrap:true},
										{ref:"query", type:_TEXTFIELD_, width:"100%", label:null,
									      elementChanged: function(elementValue,instanceValue, event) {
											  var charCode = event.charCode;
											  if (charCode == 13 || charCode == 3) {
											      ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
											  } else {
											      this.getForm().itemChanged(this, elementValue, event);
											  }
								      		}
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
										   onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
										},
										{ref: ZaAccount.A2_showSameDomain, type: _CHECKBOX_, align:_RIGHT_, msgName:ZaMsg.NAD_SearchSameDomain,
												label:AjxMessageFormat.format (ZaMsg.NAD_SearchSameDomain),
												labelCssClass:"xform_label",
												labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",
												relevantBehavior: _HIDE_, relevant: "ZaSettings.DOMAINS_ENABLED"
										}										
									]
						         },
						        {type:_SPACER_, height:"5"},
								
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "100%", height: 455,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
									//createPopupMenu: 
									forceUpdate: true },
									
								{type:_SPACER_, height:"5"},	
								//add action buttons
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
									items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
										onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										relevant:"ZaAccountMemberOfListView.shouldEnableAddRemoveButton.call(this, ZaAccount.A2_nonMemberList)",
										relevantBehavior:_DISABLE_},
									   {type:_CELLSPACER_},
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
										onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
										relevant:"ZaAccountMemberOfListView.shouldEnableAllButton.call(this, ZaAccount.A2_nonMemberList)",
										relevantBehavior:_DISABLE_},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableBackButton.call(this, ZaAccount.A2_nonMemberList)",
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	relevantBehavior:_DISABLE_, relevant:"ZaAccountMemberOfListView.shouldEnableForwardButton.call(this, ZaAccount.A2_nonMemberList)",
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"									
										},								       
										{type:_CELLSPACER_}	
									  ]
							    }								
							]
						},
						{type: _GROUP_, width: "100%", items: [
								{type:_CELLSPACER_}
							]
						}
						
						//{type:_CELLSPACER_}		
					]
				};
	cases.push(case3);		
					
	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED) {
		cases.push({type:_ZATABCASE_,id:"account_form_features_tab",  numCols:1, width:"100%", relevant:("instance[ZaModel.currentTab] == " + _tab4),
				items: [
					{ type: _DWT_ALERT_,
					  containerCssStyle: "padding-top:20px;width:400px;",
					  style: DwtAlert.WARNING,
					  iconVisible: false, 
					  content: ZaMsg.NAD_CheckFeaturesInfo
					},				
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraMajorFeature, id:"account_form_features_major", colSizes:["auto"],numCols:1,
						items:[	
							{ref:ZaAccount.A_zimbraFeatureMailEnabled,
								type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureMailEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailEnabled, 
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,
								type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureContactsEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureContactsEnabled, 
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureCalendarEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureCalendarEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", 
								onChange:ZaTabView.onFormFieldChanged},		
							{ref:ZaAccount.A_zimbraFeatureTasksEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureTaskEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureTaskEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", 
								onChange:ZaTabView.onFormFieldChanged},													
							{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureNotebookEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureBriefcasesEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},								
							{ref:ZaAccount.A_zimbraFeatureIMEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureIMEnavbled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureIMEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},								
							{ref:ZaAccount.A_zimbraFeatureOptionsEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureOptionsEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureOptionsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraGeneralFeature, id:"account_form_features_general", colSizes:["auto"],numCols:1,
						items:[							
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureTaggingEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureTaggingEnabled, 
								trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,checkBoxLabel:ZaMsg.NAD_FeatureChangePasswordEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}	,
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},														
							{ref:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,checkBoxLabel:ZaMsg.NAD_FeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraMailFeature, id:"account_form_features_mail", colSizes:["auto"],numCols:1,
						relevant: "(((instance.attrs[ZaAccount.A_zimbraFeatureMailEnabled] == null) && (instance.cos.attrs[ZaAccount.A_zimbraFeatureMailEnabled] == 'TRUE')) ||  (instance.attrs[ZaAccount.A_zimbraFeatureMailEnabled] == 'TRUE'))", relevantBehavior: _DISABLE_,
						items:[													
							{ref:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}	,
							{ref:ZaAccount.A_zimbraFeatureFlaggingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureFlaggingEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureFlaggingEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}	,
							{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,checkBoxLabel:ZaMsg.NAD_zimbraImapEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,checkBoxLabel:ZaMsg.NAD_zimbraPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},		
							{ref:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraExternalPop3Enabled,checkBoxLabel:ZaMsg.NAD_zimbraExternalPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},		
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,checkBoxLabel:ZaMsg.NAD_FeatureConversationsEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,checkBoxLabel:ZaMsg.NAD_FeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureIdentitiesEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureIdentitiesEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureIdentitiesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE", 
								onChange:ZaTabView.onFormFieldChanged
							}							
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraCalendarFeature, id:"account_form_features_calendar",colSizes:["auto"],numCols:1,
						relevant: "(((instance.attrs[ZaAccount.A_zimbraFeatureCalendarEnabled] == null) && (instance.cos.attrs[ZaAccount.A_zimbraFeatureCalendarEnabled] == 'TRUE')) ||  (instance.attrs[ZaAccount.A_zimbraFeatureCalendarEnabled] == 'TRUE'))", relevantBehavior: _DISABLE_,
						items:[						
							{ref:ZaAccount.A_zimbraFeatureGroupCalendarEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}	
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraIMFeature, id:"account_form_features_im", colSizes:["auto"],numCols:1,
						relevant: "(((instance.attrs[ZaAccount.A_zimbraFeatureIMEnabled] == null) && (instance.cos.attrs[ZaCos.A_zimbraFeatureIMEnabled] == 'TRUE')) ||  (instance.attrs[ZaAccount.A_zimbraFeatureIMEnabled] == 'TRUE'))", relevantBehavior: _HIDE_,
						items:[	
							{ref:ZaAccount.A_zimbraFeatureInstantNotify,
								 type:_SUPER_CHECKBOX_,
								 msgName:ZaMsg.NAD_zimbraFeatureInstantNotify,
								 checkBoxLabel:ZaMsg.NAD_zimbraFeatureInstantNotify,
								 trueValue:"TRUE",
								 falseValue:"FALSE",
								 resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
								 onChange:ZaTabView.onFormFieldChanged
							}											
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraSearchFeature, id:"account_form_features_search", colSizes:["auto"],numCols:1,
						items:[						
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,checkBoxLabel:ZaMsg.NAD_FeatureAdvancedSearchEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,checkBoxLabel:ZaMsg.NAD_FeatureSavedSearchesEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,checkBoxLabel:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
						]
					}
				]
			});
	}
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED) {
		var prefItems = [
						{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"account_form_prefs_general_header",
							items: [
								{type:_OUTPUT_,value:ZaMsg.NAD_GeneralOptions}
							],
							cssStyle:"padding-top:5px; padding-bottom:5px"
						},
						{type:_ZA_PLAIN_GROUPER_, id:"account_prefs_general",colSizes:["auto"],numCols:1,
							items :[
								{ref:ZaAccount.A_zimbraPrefClientType,
									type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px",colSizes:["375px","190px"], 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zzimbraPrefClientType,
									label:ZaMsg.NAD_zimbraPrefClientType, 
									labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPER_TEXTFIELD_,
									colSizes:["175px","375px","190px"], 
									msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefMailInitialSearch, 
									labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPrefShowSearchString, 
									colSizes:["175px","375px","190px"],
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefShowSearchString,checkBoxLabel:ZaMsg.NAD_zimbraPrefShowSearchString,trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,  
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged,
									colSizes:["175px","375px","190px"]
								},
								{ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, 
									colSizes:["175px","375px","190px"],
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.NAD_prefKeyboardShort, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								
								{ref:ZaAccount.A_zimbraPrefWarnOnExit, type:_SUPER_CHECKBOX_, nowrap:false,labelWrap:true,
									colSizes:["175px","375px","190px"],	
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.NAD_zimbraPrefWarnOnExit,
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									labelWrap: true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, checkBoxLabel:ZaMsg.NAD_zimbraPrefShowSelectionCheckbox,
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged}	
							]
						},	
						{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"account_form_prefs_mail_header",
							items: [
								{type:_OUTPUT_,value:ZaMsg.NAD_MailOptions}
							],
							cssStyle:"padding-top:5px; padding-bottom:5px"
						},
						{type:_ZA_PLAIN_GROUPER_, id:"account_prefs_mail_general",colSizes:["175px","auto"],numCols:2,
							items :[
								{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, 
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefDisplayExternalImages, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefDisplayExternalImages,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefDisplayExternalImages, 
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},	
								{ref:ZaAccount.A_zimbraPrefGroupMailBy,
									type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px",colSizes:["375px","190px"], 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,
									label:ZaMsg.NAD_zimbraPrefGroupMailBy, 
									labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, 
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,
									label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefMailDefaultCharset, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"], 
									msgName:ZaMsg.NAD_zimbraPrefMailDefaultCharset,label:ZaMsg.NAD_zimbraPrefMailDefaultCharset,
									 labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_ZA_TOP_GROUPER_,colSizes:["175px","auto"], id:"account_prefs_mail_receiving",
							label:ZaMsg.NAD_MailOptionsReceiving,
							items :[
								{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  
									onChange:ZaTabView.onFormFieldChanged,colSpan:2,
									nowrap:false,labelWrap:true									
								},							
								{ref:ZaAccount.A_zimbraMailMinPollingInterval, 
									type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraMailMinPollingInterval,
									txtBoxLabel:ZaMsg.NAD_zimbraMailMinPollingInterval+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									onChange:ZaTabView.onFormFieldChanged,
									colSpan:2
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, 
									type:_ZA_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,
									label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, 
									type:_TEXTFIELD_, 
									msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,
									label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress, 
									labelLocation:_LEFT_,  
									onChange:ZaTabView.onFormFieldChanged,
									relevant:"ZaAccountXFormView.isMailNotificationAddressEnabled.call(this)",
									relevantBehavior:_DISABLE_,
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,
									label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled, trueValue:"TRUE", 
									falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged
								},							
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, 
									type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									onChange:ZaTabView.onFormFieldChanged,
									colSpan:2
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, 
									type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,
									label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, 
									width:"30em",
									relevant:"ZaAccountXFormView.isOutOfOfficeReplyEnabled.call(this)",
								 	relevantBehavior: _DISABLE_										
									
								}
							]
						},						
						{type:_ZA_TOP_GROUPER_, colSizes:["175px","auto"], id:"account_prefs_mail_sending",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsSending,
							items :[
								{ref:ZaAccount.A_prefSaveToSent,  
									colSpan:2,								
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_prefSaveToSent,
									checkBoxLabel:ZaMsg.NAD_prefSaveToSent,
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraAllowAnyFromAddress,  
									colSpan:2,								
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraAllowAnyFromAddress,
									checkBoxLabel:ZaMsg.NAD_zimbraAllowAnyFromAddress,
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged},	
									
								{ref:ZaAccount.A_zimbraAllowFromAddress,
									type:_REPEAT_,
									label:ZaMsg.NAD_zimbraAllowFromAddress,
									labelLocation:_LEFT_, 
									addButtonLabel:ZaMsg.NAD_AddAddress, 
									align:_LEFT_,
									repeatInstance:emptyAlias, 
									showAddButton:true, 
									showRemoveButton:true, 
									showAddOnNextRow:true, 
//									alwaysShowAddButton:true,
									removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
									items: [
										{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged,width:"200px"}
									],
									onRemove:ZaAccountXFormView.onRepeatRemove,
									relevant: "!(ZaAccountXFormView.isSendingFromAnyAddressAllowed.call(this))",
								 	relevantBehavior: _HIDE_									
								}															
							]
						},
						{type:_ZA_TOP_GROUPER_,colSizes:["175px","565px"], id:"account_prefs_mail_composing",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsComposing,
							items :[
								{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, 
									//colSpan:2,
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefComposeInNewWindow,
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefComposeFormat, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefComposeFormat,
									label:ZaMsg.NAD_zimbraPrefComposeFormat, 
									onChange:ZaTabView.onFormFieldChanged},
								,
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize, 
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily, 
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, 
									type:_SUPER_DWT_COLORPICKER_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, 
									//colSpan:2,								
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, 
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_prefMailSignatureEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,
									label:ZaMsg.NAD_prefMailSignatureEnabled,  
									trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged},	
								{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, 
									//colSpan:2,								
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									colSizes:["175px","375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefMailSignatureStyle, 
									onChange:ZaTabView.onFormFieldChanged,
									trueValue:"internet", falseValue:"outlook"},
								{ref:ZaAccount.A_zimbraMailSignatureMaxLength, 
									//colSpan:2,	
									type:_SUPER_TEXTFIELD_, 
									colSizes:["175px","375px","190px"], 						
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									labelLocation:_LEFT_, 
									msgName:ZaMsg.NAD_zimbraMailSignatureMaxLength,
									txtBoxLabel:ZaMsg.NAD_zimbraMailSignatureMaxLength, 
									onChange:ZaTabView.onFormFieldChanged,
									textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_prefMailSignature, type:_TEXTAREA_, 
									msgName:ZaMsg.NAD_prefMailSignature,
									label:ZaMsg.NAD_prefMailSignature, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", 
									onChange:ZaTabView.onFormFieldChanged, width:"30em",
									relevant:"ZaAccountXFormView.isMailSignatureEnabled.call(this)",
								 	relevantBehavior: _DISABLE_										
								}
						
							]
						},						
						{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"account_form_prefs_contacts_header",
							items: [
								{type:_OUTPUT_,value:ZaMsg.NAD_ContactsOptions}
							],
							cssStyle:"padding-top:5px; padding-bottom:5px"
						},				
						{type:_ZA_PLAIN_GROUPER_, id:"account_prefs_contacts_general",colSizes:["175px","auto"],
							//label:ZaMsg.NAD_ContactsOptions,
							items :[
								{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,
									colSpan:2
								},							
								{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled,colSpan:2,
									colSizes:["175px","375px","190px"], 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},	
								{ref:ZaAccount.A_zimbraPrefContactsPerPage, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,onChange:ZaTabView.onFormFieldChanged}		
							]
						},

						{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"account_form_prefs_calendar_header",
							items: [
								{type:_OUTPUT_,value:ZaMsg.NAD_CalendarOptions}
							],
							cssStyle:"padding-top:5px; padding-bottom:5px"
						},					
						{type:_ZA_PLAIN_GROUPER_, id:"account_prefs_calendar_general",colSizes:["175px","565px"],
							//label:ZaMsg.NAD_CalendarOptions,
							items :[
								{ref:ZaAccount.A_zimbraPrefTimeZoneId, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"], 
									msgName:ZaMsg.NAD_zimbraPrefTimeZoneId,label:ZaMsg.NAD_zimbraPrefTimeZoneId+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
									colSizes:["375px","190px"], labelCssStyle:"width:175px", 
									type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,  onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_CHECKBOX_,
								colSizes:["175px","375px","190px"], resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,checkBoxLabel:ZaMsg.NAD_alwaysShowMiniCal, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, 
								colSizes:["175px","375px","190px"], type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,checkBoxLabel:ZaMsg.NAD_useQuickAdd, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, 
								colSizes:["175px","375px","190px"], type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,checkBoxLabel:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged}
							]
						}						
					];
		cases.push({type:_ZATABCASE_, id:"account_form_prefs_tab", numCols:1, 
					width:"100%", relevant:("instance[ZaModel.currentTab] == " + _tab5),
					/*colSizes:["275px","275px","150px"],*/ items :prefItems});
	}


	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED) {
		cases.push({type:_ZATABCASE_, id:"account_form_aliases_tab", width:"100%", numCols:1,colSizes:["auto"],
					relevant:("instance[ZaModel.currentTab] == " + _tab6),
					items: [
						{type:_ZA_TOP_GROUPER_, id:"account_form_aliases_group",borderCssClass:"LowPadedTopGrouperBorder",
							 width:"100%", numCols:1,colSizes:["auto"],
							label:ZaMsg.NAD_EditAliasesGroup,
							items :[
								{ref:ZaAccount.A_zimbraMailAlias, type:_DWT_LIST_, height:"200", width:"350px", 
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									headerList:null,onSelection:ZaAccountXFormView.aliasSelectionListener
								},
								{type:_GROUP_, numCols:5, width:"350px", colSizes:["100px","auto","100px","auto","100px"], 
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteAliasButtonListener.call(this);",
											relevant:"ZaAccountXFormView.isDeleteAliasEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editAliasButtonListener.call(this);",
											relevant:"ZaAccountXFormView.isEditAliasEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addAliasButtonListener.call(this);"
										}
									]
								}
							]
						}
						/*{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAlias, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAlias,
							removeButtonCSSStyle: "margin-left: 50px",
							items: [
								{ref:".", type:_EMAILADDR_, label:null, onChange:ZaTabView.onFormFieldChanged}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}*/
					]
				});
	}
	
	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED) {
		cases.push({type:_ZATABCASE_,id:"account_form_forwarding_tab", width:"100%", numCols:1,colSizes:["auto"],
					relevant:("instance[ZaModel.currentTab] == " + _tab7), 
					items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_EditFwdTopGroupGrouper,
							id:"account_form_user_forwarding_addr",colSizes:["auto"],numCols:1,
							items :[					
							{
								ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								type:_SUPER_CHECKBOX_, colSpan:2,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,  
								trueValue:"TRUE", falseValue:"FALSE",
								onChange:ZaTabView.onFormFieldChanged
							},
							{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"], 
						  		items:[					  	
									{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, 
										type:_ZA_CHECKBOX_, 
										msgName:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled,
										label:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled, 
										trueValue:"TRUE", falseValue:"FALSE",
										onChange:ZaTabView.onFormFieldChanged
									},	
									{type:_SPACER_},						
									{ref:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_TEXTFIELD_, width:230,
										msgName:ZaMsg.NAD_zimbraPrefMailForwardingAddress,
										label:ZaMsg.NAD_zimbraPrefMailForwardingAddress+":", labelLocation:_LEFT_,  
										onChange:ZaTabView.onFormFieldChanged,
										relevantBehavior:_DISABLE_, align:_LEFT_,
										relevant:"this.getModel().getInstanceValue(this.getInstance(),ZaAccount.A_zimbraFeatureMailForwardingEnabled) == \"TRUE\""
									},
								  	{type:_SPACER_}								
								]
						  	}
							
						]},
						{type:_ZA_PLAIN_GROUPER_, id:"account_form_forwarding_group",
							 numCols:2,label:null,colSizes:["275px","425px"],
							items :[
                               {type: _DWT_ALERT_, colSpan: 2,
                                            containerCssStyle: "padding:10px;padding-top: 0px; width:100%;",
                                            style: DwtAlert.WARNING,
                                            iconVisible: true,
                                            content: ZaMsg.Alert_Bouncing_Reveal_Hidden_Adds
                                        },
                                {ref:ZaAccount.A_zimbraMailForwardingAddress, type:_DWT_LIST_, height:"200", width:"350px",
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									headerList:null,onSelection:ZaAccountXFormView.fwdAddrSelectionListener,label:ZaMsg.NAD_EditFwdGroup
								},
								{type:_GROUP_, numCols:6, width:"625px",colSizes:["275","100px","auto","100px","auto","100px"], colSpan:2,
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteFwdAddrButtonListener.call(this);",
											relevant:"ZaAccountXFormView.isDeleteFwdAddrEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editFwdAddrButtonListener.call(this);",
											relevant:"ZaAccountXFormView.isEditFwdAddrEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addFwdAddrButtonListener.call(this);"
										}
									]
								}
							]
						}						
						/*{type:_SPACER_,colSpan:2},
						{type:_SEPARATOR_,colSpan:2},
						{type:_SPACER_,colSpan:2},
						{ref:ZaAccount.A_zimbraMailForwardingAddress,type:_REPEAT_,
							labelCssClass:"xform_label", label:ZaMsg.NAD_EditFwdGroup,colSpan:"*", labelLocation:_LEFT_, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							align:_LEFT_,colSpan:"*",
							repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							showAddOnNextRow:true, 
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged, width:230}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}*/
					]
				});
	}

    if(ZaSettings.ACCOUNTS_INTEROP_ENABLED) {
		cases.push({type:_ZATABCASE_, id:"account_form_interop_tab", width:"100%", numCols:1,colSizes:["auto"],
					relevant:("instance[ZaModel.currentTab] == " + _tab8),
					items: [
						{type:_ZA_TOP_GROUPER_, id:"account_form_interop_group",
                            borderCssClass:"LowPadedTopGrouperBorder",
							 width:"100%", numCols:1,colSizes:["auto"],
							label:ZaMsg.NAD_EditFpGroup,
							items :[
								{ref:ZaAccount.A_zimbraForeignPrincipal, type:_DWT_LIST_, height:"200", width:"350px",
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
									headerList:null,onSelection:ZaAccountXFormView.fpSelectionListener
								},
								{type:_GROUP_, numCols:7, width:"350px", colSizes:["100px","auto","100px","auto","100px", "auto","100px"],
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Push,width:"100px",
											onActivate:"ZaAccountXFormView.pushFpButtonListener.call(this);",
											relevant:"ZaAccountXFormView.isPushFpEnabled.call(this)",
                                            relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                            onActivate:"ZaAccountXFormView.deleteFpButtonListener.call(this);",
                                            relevant:"ZaAccountXFormView.isDeleteFpEnabled.call(this)",
                                            relevantBehavior:_DISABLE_
                                        },
                                        {type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                            onActivate:"ZaAccountXFormView.editFpButtonListener.call(this);",
                                            relevant:"ZaAccountXFormView.isEditFpEnabled.call(this)",
                                            relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addFpButtonListener.call(this);"
										}
									]
								}
							]
						}
					]
				});
	}

    if(ZaSettings.SKIN_PREFS_ENABLED) {
		cases.push({type:_ZATABCASE_, id:"account_form_themes_tab", numCols:1,
            relevant:("instance[ZaModel.currentTab] == " + _tab9),
			items:[
				{type:_SPACER_},
				{type:_GROUP_, 
					items:[
					{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,choices:this._app.getInstalledSkins(),
						relevant:"ZaAccountXFormView.gotSkins.call(this)"}
					] 
				},
				{type:_SPACER_},
				{type:_SUPER_ZIMLET_SELECT_CHECK_,
					selectRef:ZaAccount.A_zimbraAvailableSkin, 
					ref:ZaAccount.A_zimbraAvailableSkin, 
					choices:ZaAccountXFormView.themeChoices,
					onChange: ZaTabView.onFormFieldChanged,
					relevant:("instance[ZaModel.currentTab] == " + _tab9),
					relevantBehavior:_HIDE_,
					limitLabel:ZaMsg.NAD_LimitThemesTo
				}
			] 
		});
	}	

	if(ZaSettings.ZIMLETS_ENABLED) {
		cases.push({type:_ZATABCASE_, id:"account_form_zimlets_tab", numCols:1,
            relevant:("instance[ZaModel.currentTab] == " + _tab10),
			items:[
				{type:_ZAGROUP_, numCols:1,colSizes:["auto"], 
					items: [
						{type:_SUPER_ZIMLET_SELECT_CHECK_,
							selectRef:ZaAccount.A_zimbraZimletAvailableZimlets, 
							ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
							choices:ZaAccountXFormView.zimletChoices,
							onChange: ZaTabView.onFormFieldChanged,
							relevant:("instance[ZaModel.currentTab] == " + _tab10),
							relevantBehavior:_HIDE_,
							limitLabel:ZaMsg.NAD_LimitZimletsTo
						}
					]
				}
			] 
		});
	}
	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED) {
		cases.push({type:_ZATABCASE_, id:"account_form_advanced_tab", numCols:1,
					relevant:("instance[ZaModel.currentTab] == " + _tab11),
					items: [
						{type:_ZA_TOP_GROUPER_, id:"account_attachment_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_AttachmentsGrouper,
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,
									checkBoxLabel:ZaMsg.NAD_RemoveAllAttachments, 
									trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								}
							]
						},

						{type:_ZA_TOP_GROUPER_, id:"account_quota_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_QuotaGrouper,
							items: [
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_MailQuota+":", msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,txtBoxLabel:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraQuotaWarnPercent, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnPercent, msgName:ZaMsg.NAD_QuotaWarnPercent,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnInterval, type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnInterval, msgName:ZaMsg.NAD_QuotaWarnInterval,labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnMessage, type:_SUPER_TEXTAREA_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnMessage, msgName:ZaMsg.NAD_QuotaWarnMessage,
									labelCssStyle:"vertical-align:top", textAreaWidth:"250px",
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								}
							]
						},

						{type:_ZA_TOP_GROUPER_,id:"account_password_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_PasswordGrouper,
							items: [
								{ref:ZaAccount.A_zimbraPasswordLocked, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_PwdLocked,checkBoxLabel:ZaMsg.NAD_PwdLocked, 
								 	trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMinPwdLength, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_passMinLength,
									txtBoxLabel:ZaMsg.NAD_passMinLength+":", 
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,txtBoxLabel:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},

								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
																
								{ref:ZaAccount.A_zimbraMinPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,txtBoxLabel:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMaxPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,txtBoxLabel:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,txtBoxLabel:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"password_lockout_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_FailedLoginGrouper,
							items :[
								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
									trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPER_TEXTFIELD_, 
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
								 	relevantBehavior: _DISABLE_,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPER_LIFETIME_, 
									colSpan:3,
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
									relevantBehavior: _DISABLE_,
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPER_LIFETIME_, 
									colSpan:3,									
									relevant: "ZaAccountXFormView.isPasswordLockoutEnabled.call(this)",
									relevantBehavior: _DISABLE_,								
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"white-space:normal;",
									nowrap:false,labelWrap:true
								}																		
								
							]
						},
						{type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_TimeoutGrouper,id:"timeout_settings",
							items: [
								{ref:ZaAccount.A_zimbraAdminAuthTokenLifetime,
									type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_AdminAuthTokenLifetime,
									txtBoxLabel:ZaMsg.NAD_AdminAuthTokenLifetime+":",
									onChange:ZaTabView.onFormFieldChanged,
									relevant:"instance.attrs[ZaAccount.A_isAdminAccount]==\'TRUE\'",
									relevantBehavior:_DISABLE_
								},								
								{ref:ZaAccount.A_zimbraAuthTokenLifetime,
									type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_AuthTokenLifetime,
									txtBoxLabel:ZaMsg.NAD_AuthTokenLifetime+":",
									onChange:ZaTabView.onFormFieldChanged},										
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, 
									type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailIdleSessionTimeout,
									txtBoxLabel:ZaMsg.NAD_MailIdleSessionTimeout+":",
									onChange:ZaTabView.onFormFieldChanged}															
							]
						},
                        { type:_ZA_TOP_GROUPER_, colSizes:["auto"], numCols:1,
							label:ZaMsg.NAD_MailRetentionGrouper, id: "mailretention_settings",
							items: [
                                { type: _DWT_ALERT_,
                                  containerCssStyle: "padding:10px;padding-top: 0px; width:100%;",
                                  style: DwtAlert.WARNING,
                                  iconVisible: false,
                                  content: ZaMsg.Alert_EnableMailRetention,
                                  relevant:"ZaAccount.isEmailRetentionPolicyEnabled.call (this) == false",
						          relevantBehavior:_HIDE_
                                },
                                { type: _GROUP_ ,
                                    relevant:"ZaAccount.isEmailRetentionPolicyEnabled.call (this) == true",
						            relevantBehavior: _DISABLE_ ,
                                    items: [
                                        {ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPER_LIFETIME2_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.NAD_MailMessageLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailMessageLifetime+":",
                                            onChange:ZaTabView.onFormFieldChanged},
                                        {ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailTrashLifetime+":",
                                            onChange:ZaTabView.onFormFieldChanged},
                                        {ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.NAD_MailSpamLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailSpamLifetime,
                                            onChange:ZaTabView.onFormFieldChanged}
                                    ]
                                }
                            ]
                        },
                        {type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,
								label:ZaMsg.NAD_InteropGrouper,   id: "interop_settings",
							items: [
                                { ref: ZaAccount.A_zimbraFreebusyExchangeUserOrg, type: _SUPER_TEXTFIELD_,
                                    textFieldWidth: "250px",
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                    msgName:ZaMsg.NAD_ExchangeUserGroup,
                                    txtBoxLabel:ZaMsg.NAD_ExchangeUserGroup, labelLocation:_LEFT_,
                                    textFieldCssClass:"admin_xform_number_input",
                                    onChange:ZaTabView.onFormFieldChanged
                                }
                            ]
                        },
                        {type: _SPACER_ , height: "10px" }  //add some spaces at the bottom of the page
                    ]
                });
	}
	
	xFormObject.tableCssStyle="width:100%;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header",
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["90px","350px","100px","200px"],items:headerItems}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:this.tabChoices,cssClass:"ZaTabBar", id:"xform_tabbar"},
			{type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
	];
};
ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaAccountXFormView.myXFormModifier);
