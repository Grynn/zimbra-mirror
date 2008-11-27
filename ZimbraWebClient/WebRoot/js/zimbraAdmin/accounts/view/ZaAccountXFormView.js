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
ZaAccountXFormView = function(parent) {
	ZaTabView.call(this, parent,  "ZaAccountXFormView");	
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_ACTIVE)},
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_CLOSED)},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKED)},
        {value:ZaAccount.ACCOUNT_STATUS_LOCKOUT, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKOUT), visible: false},    
        {value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_MAINTENANCE)}
	];
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	this.TAB_INDEX = 0;
	this._domains = {} ;
	//console.time("ZaAccountXFormView.initForm");
	//DBG.timePt(AjxDebug.PERF, "started initForm");
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
	//console.timeEnd("ZaAccountXFormView.initForm");
	//DBG.timePt(AjxDebug.PERF, "finished initForm");
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
	this._containedObject.type = entry.type;
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
	
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
					
	//if(ZaSettings.COSES_ENABLED) {	
		/**
		* If this account does not have a COS assigned to it - assign default COS
		**/
		if(this._containedObject.attrs[ZaAccount.A_COSId]) {	
			this._containedObject[ZaAccount.A2_autoCos] = "FALSE" ;
			
			//this._containedObject._defaultValues = ZaApp.getInstance().getCosList().getItemById(this._containedObject.attrs[ZaAccount.A_COSId]);
			//this._containedObject._defaultValues = ZaCos.getCosById(this._containedObject.attrs[ZaAccount.A_COSId]);
			
		}
		if(!this._containedObject.attrs[ZaAccount.A_COSId]) {
			this._containedObject[ZaAccount.A2_autoCos] = "TRUE" ;
			//We do not need to find the COS when displaying an account, because all default (fall-back-to) values are returned in GetEfectiveRightsRequest
			/**
			* We did not find the COS assigned to this account,
			* this means that the COS was deleted or wasn't assigned, therefore assign default COS to this account
			**/
			//this._containedObject._defaultValues = ZaCos.getDefaultCos4Account(this._containedObject.name);
			/*ZaAccount.setDefaultCos(this._containedObject) ;
			if(!this._containedObject.cos) {
				//default COS was not found - just assign the first COS
				var hashMap = ZaApp.getInstance().getCosList().getIdHash();
				for(var id in hashMap) {
					this._containedObject._defaultValues = hashMap[id];
					this._containedObject.attrs[ZaAccount.A_COSId] = id;					
					break;
				}
			}*/
		}
	if(this._containedObject.setAttrs[ZaAccount.A_COSId]) {
		var cos = ZaCos.getCosById(this._containedObject.attrs[ZaAccount.A_COSId]);	
		this.cosChoices.setChoices([cos]);
		this.cosChoices.dirtyChoices();
	}
	//}
	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname];
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword];
	
	/*if(ZaSettings.GLOBAL_CONFIG_ENABLED) {
		this._containedObject.globalConfig = ZaApp.getInstance().getGlobalConfig();
	}*/
   	
			
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

		var skins = ZaApp.getInstance().getInstalledSkins();
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
		var allZimlets = ZaZimlet.getAll("extension");
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

    //check the account type here 
    var domainName = ZaAccount.getDomain (this._containedObject.name) ;
    var domainObj =  ZaDomain.getDomainByName (domainName) ;
    this._containedObject[ZaAccount.A2_accountTypes] = domainObj.getAccountTypes () ;
    
    this._localXForm.setInstance(this._containedObject);
	//update the tab
	this.updateTab();
}

ZaAccountXFormView.gotSkins = function () {
	if(!ZaSettings.SKIN_PREFS_ENABLED)
		return false;
	else 
		return ((this.getController().getInstalledSkins() != null) && (this.getController().getInstalledSkins().length > 0));
}

ZaAccountXFormView.preProcessCOS = 
function(value,  form) {
	var val = value;
	if(ZaItem.ID_PATTERN.test(value))  {
		val = value;
	} else {
		var cos = ZaCos.getCosByName(value);
		if(cos)
			val = cos.id;
	}
    return val;
}

//update the account type output and it is called when the domain name is changed.
ZaAccountXFormView.accountTypeItemId = "account_type_output_" + Dwt.getNextId();
ZaAccountXFormView.prototype.updateAccountType =
function ()  {
    var item = this._localXForm.getItemsById (ZaAccountXFormView.accountTypeItemId) [0] ;
    item.updateElement(ZaAccount.getAccountTypeOutput.call(item, true)) ;
}

ZaAccountXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}


ZaAccountXFormView.isSendingFromAnyAddressDisAllowed = function () {
	return (this.getInstanceValue(ZaAccount.A_zimbraAllowAnyFromAddress) != 'TRUE');
}

ZaAccountXFormView.aliasSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_alias_selection_cache, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_alias_selection_cache, null);
	}		
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editAliasButtonListener.call(this);
	}	
}

ZaAccountXFormView.nonMemberOfSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_nonMemberListSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_nonMemberListSelected, null);
	}
}

ZaAccountXFormView.directMemberOfSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_directMemberListSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_directMemberListSelected, null);
	}
}


ZaAccountXFormView.indirectMemberOfSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_indirectMemberListSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_indirectMemberListSelected, null);
	}
}


ZaAccountXFormView.isEditAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_alias_selection_cache)) && this.getInstanceValue(ZaAccount.A2_alias_selection_cache).length==1);
}

ZaAccountXFormView.isDeleteAliasEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_alias_selection_cache)));
}

ZaAccountXFormView.deleteAliasButtonListener = function () {
	var instance = this.getInstance();
	if(instance[ZaAccount.A2_alias_selection_cache] != null) {
		var cnt = instance[ZaAccount.A2_alias_selection_cache].length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraMailAlias]) {
			var aliasArr = instance.attrs[ZaAccount.A_zimbraMailAlias];
			for(var i=0;i<cnt;i++) {
				var cnt2 = aliasArr.length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(aliasArr[k]==instance.alias_selection_cache[i]) {
						aliasArr.splice(k,1);
						break;	
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraMailAlias, aliasArr);	
		}
	}
	this.getModel().setInstanceValue(instance, ZaAccount.A2_alias_selection_cache, []);
	this.getForm().parent.setDirty(true);
}

ZaAccountXFormView.editAliasButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.alias_selection_cache && instance.alias_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editAliasDlg) {
			formPage.editAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Edit_Alias_Title);
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
		var arr = instance.attrs[ZaAccount.A_zimbraMailAlias];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {			
			arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr); 
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A2_alias_selection_cache, new Array());
			this.parent.setDirty(true);	
		}
	}
}

ZaAccountXFormView.addAliasButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addAliasDlg) {
		formPage.addAliasDlg = new ZaEditAliasXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Add_Alias_Title);
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
			var arr = instance.attrs[ZaAccount.A_zimbraMailAlias]; 
			arr.push(obj[ZaAccount.A_name]);
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr);
			this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A2_alias_selection_cache, new Array());
			this.parent.setDirty(true);
		}
	}
}

ZaAccountXFormView.isEditFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache)) && this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache).length==1);
}

ZaAccountXFormView.isDeleteFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache)));
}

ZaAccountXFormView.deleteFwdAddrButtonListener = function () {
	var instance = this.getInstance();	
	if(instance[ZaAccount.A2_fwdAddr_selection_cache] != null) {
		var cnt = instance[ZaAccount.A2_fwdAddr_selection_cache].length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraMailForwardingAddress]) {
			var arr = instance.attrs[ZaAccount.A_zimbraMailForwardingAddress];
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance.fwdAddr_selection_cache[i]) {
						arr.splice(k,1);
						break;	
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraMailForwardingAddress, arr);
			this.getModel().setInstanceValue(instance, ZaAccount.A2_fwdAddr_selection_cache, []);	
		}
	}
	this.getForm().parent.setDirty(true);
}

ZaAccountXFormView.fwdAddrSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fwdAddr_selection_cache, arr);	
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fwdAddr_selection_cache, []);
	}	
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
			formPage.editFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(),"400px", "150px",ZaMsg.Edit_FwdAddr_Title);
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
		var arr = instance.attrs[ZaAccount.A_zimbraMailForwardingAddress];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fwdAddr_selection_cache, []);
			arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraMailForwardingAddress, arr);
			this.parent.setDirty(true);	
		}
	}
}

ZaAccountXFormView.addFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addFwdAddrDlg) {
		formPage.addFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "400px", "150px",ZaMsg.Add_FwdAddr_Title);
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
			var arr = this.getInstance().attrs[ZaAccount.A_zimbraMailForwardingAddress];
			arr.push(obj[ZaAccount.A_name]);
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A_zimbraMailForwardingAddress, arr);
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fwdAddr_selection_cache, []);
			this.parent.setDirty(true);
		}
	}
}

//interop account
ZaAccountXFormView.fpSelectionListener =
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fp_selection_cache, arr);
	} else
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fp_selection_cache, []);

	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editFpButtonListener.call(this);
	}
}

ZaAccountXFormView.isEditFpEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fp_selection_cache)) && this.getInstanceValue(ZaAccount.A2_fp_selection_cache).length==1
            && !AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A_zimbraForeignPrincipal)));
}

ZaAccountXFormView.isDeleteFpEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fp_selection_cache)));
}

ZaAccountXFormView.isPushFpEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A_zimbraForeignPrincipal)));
}

ZaAccountXFormView.deleteFpButtonListener = function () {
	var instance = this.getInstance();
	if(!AjxUtil.isEmpty(instance.fp_selection_cache)) {
		var cnt = instance.fp_selection_cache.length;
		var arr = instance.attrs[ZaAccount.A_zimbraForeignPrincipal];
		if(cnt && !AjxUtil.isEmpty(arr)) {
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance.fp_selection_cache[i]) {
						arr.splice(k,1);
						break;
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraForeignPrincipal, arr);
			this.getModel().setInstanceValue(instance, ZaAccount.A2_fp_selection_cache, []);
		}
	}
	this.getForm().parent.setDirty(true);
}

ZaAccountXFormView.editFpButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.fp_selection_cache && instance.fp_selection_cache[0]) {
		var formPage = this.getForm().parent;
		if(!formPage.editFpDlg) {
			formPage.editFpDlg = new ZaEditFpXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Edit_Fp_Title);
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

    if (this.getForm().parent.isDirty()) {
       ZaApp.getInstance().getCurrentController().popupMsgDialog (ZaMsg.DIRTY_SAVE_ACCT, true);
    } else if (instance.attrs[ZaAccount.A_zimbraForeignPrincipal].length > 0) {
	   ZaFp.push (instance.id);
  	}
}

ZaAccountXFormView.updateFp = function () {
	if(this.parent.editFpDlg) {
        this.parent.editFpDlg.popdown();
		var obj = this.parent.editFpDlg.getObject();
		var arr = this.getInstance().attrs[ZaAccount.A_zimbraForeignPrincipal];
		if(obj[ZaFp.A_index] >=0 && arr[obj[ZaFp.A_index]] != ZaFp.getEntry (obj) ) {
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fp_selection_cache, []);
			arr[obj[ZaFp.A_index]] = ZaFp.getEntry (obj);
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A_zimbraForeignPrincipal, arr);
			this.parent.setDirty(true);
		}
	}
}

ZaAccountXFormView.addFpButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
    
    if(!formPage.addFpDlg) {
		formPage.addFpDlg = new ZaEditFpXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px",ZaMsg.Add_Fp_Title);
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
        var currentFps =  this.getInstance().attrs[ZaAccount.A_zimbraForeignPrincipal] ;
        if (ZaFp.findDupPrefixFp(currentFps, obj[ZaFp.A_prefix])){
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ONE_FP_PREFIX_ALLOWED, null);
        }  else {
            this.parent.addFpDlg.popdown();
            if(ZaFp.getEntry(obj).length > 0) {
                currentFps.push(ZaFp.getEntry(obj));
                this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A_zimbraForeignPrincipal, currentFps);
				this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_fp_selection_cache, []);
                this.parent.setDirty(true);
            }
        }
    }
}

ZaAccountXFormView.isDomainLeftAccountsAlertVisible = function () {
	var val1 = this.getInstanceValue(ZaAccount.A2_domainLeftAccounts);
	var val2 = this.getInstanceValue(ZaAccount.A2_accountTypes);
	return (!AjxUtil.isEmpty(val1) && AjxUtil.isEmpty(val2));
}

ZaAccountXFormView.isAccountTypeGrouperVisible = function () {
	 return !AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_accountTypes));
}

ZaAccountXFormView.isAccountTypeSet = function () {
	 return !ZaAccount.isAccountTypeSet(this.getInstance());
}


/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaAccountXFormView.myXFormModifier = function(xFormObject) {	
	
	var domainName;
	//if(ZaSettings.DOMAINS_ENABLED) {
	try {
		domainName = ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		if(!domainName && ZaApp.getInstance().getDomainList().size() > 0)
			domainName = ZaApp.getInstance().getDomainList().getArray()[0].name;
	} catch (ex) { 
		domainName = ZaSettings.myDomainName;
	}
		
	var emptyAlias = " @" + domainName;
	var headerItems = [{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:2},{type:_OUTPUT_, ref:ZaAccount.A_displayname, label:null,cssClass:"AdminTitle", rowSpan:2}];
	//if(ZaSettings.COSES_ENABLED) {
		headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_COSId,valueChangeEventSources:[ZaAccount.A_COSId], labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, choices:this.cosChoices,getDisplayValue:function(newValue) {
				if(ZaItem.ID_PATTERN.test(newValue)) {
					var cos = ZaCos.getCosById(newValue, this.getForm().parent._app);
					if(cos)
						newValue = cos.name;
					} 
					if (newValue == null) {
						newValue = "";
					} else {
						newValue = "" + newValue;
					}
					return newValue;
				},
				visibilityChecks:[XFormItem.prototype.hasReadPermission]	
		});
	//}
	headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_mailHost, labelLocation:_LEFT_,label:ZaMsg.NAD_MailServer,visibilityChecks:[XFormItem.prototype.hasReadPermission]});
	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_accountStatus, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices,visibilityChecks:[XFormItem.prototype.hasReadPermission]});
	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false,visibilityChecks:[XFormItem.prototype.hasReadPermission]});
	headerItems.push({type:_OUTPUT_,ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID,visibilityChecks:[XFormItem.prototype.hasReadPermission]});
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
						},
						visibilityChecks:[XFormItem.prototype.hasReadPermission]	
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
		
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_GENERAL_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_CONTACT_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])		
		this.tabChoices.push({value:_tab2, label:ZaMsg.TABT_ContactInfo});
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_MEMBEROF_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_FEATURES_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab4, label:ZaMsg.TABT_Features});
					
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_PREFS_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab5, label:ZaMsg.TABT_Preferences});

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ALIASES_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab6, label:ZaMsg.TABT_Aliases});

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_FORWARDING_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab7, label:ZaMsg.TABT_Forwarding});

    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_INTEROP_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) 
        this.tabChoices.push({value: _tab8, label: ZaMsg.TABT_Interop}) ;

    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_SKIN_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab9, label:ZaMsg.TABT_Themes});

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ZIMLET_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) 
		this.tabChoices.push({value:_tab10, label:ZaMsg.TABT_Zimlets});
			
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ADVANCED_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this.tabChoices.push({value:_tab11, label:ZaMsg.TABT_Advanced});


	var cases = [];
	//if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_GENERAL_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		var case1 = {type:_ZATABCASE_,caseKey:_tab1,   
			numCols:1};
		
		var case1Items = [
			 {type: _DWT_ALERT_, ref: ZaAccount.A2_domainLeftAccounts,
			 	visibilityChecks:[ZaAccountXFormView.isDomainLeftAccountsAlertVisible],
			 	visibilityChangeEventSources:[ZaAccount.A2_domainLeftAccounts,ZaAccount.A2_accountTypes],
				containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false
			 },
	
	        //account types group
	        {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountTypeGrouper, id:"account_type_group",
	                colSpan: "*", numCols: 1, colSizes: ["100%"], 
	                visibilityChecks:[ZaAccountXFormView.isAccountTypeGrouperVisible],
	                visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId],
	                items: [
	                    {type: _DWT_ALERT_, 
	                    	visibilityChecks:[ZaAccountXFormView.isAccountTypeSet],
	                    	visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId],
	                    	containerCssStyle: "width:400px;",
	                        style: DwtAlert.CRITICAL, iconVisible: false ,
	                        content: ZaMsg.ERROR_ACCOUNT_TYPE_NOT_SET
	                    }, 
	                    { type: _OUTPUT_, id: ZaAccountXFormView.accountTypeItemId ,
	                        getDisplayValue: ZaAccount.getAccountTypeOutput,
	                        //center the elements
	                        cssStyle: "width: 600px; margin-left: auto; margin-right: auto;"
	                    }
	               ]
	        },
	
	        {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountNameGrouper, id:"account_form_name_group",
				colSizes:["275px","*"],numCols:2,
				items:[
				{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
								 labelLocation:_LEFT_,onChange:ZaAccount.setDomainChanged,forceUpdate:true},
				{ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, 
					labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				},
				{ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:50,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),elementValue);
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				},
				{ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstanceValue(ZaAccount.A_firstName), elementValue ,this.getInstanceValue(ZaAccount.A_initials));
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				},
				{type:_GROUP_, numCols:3, nowrap:true, width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_,
					visibilityChecks:[XFormItem.prototype.hasReadPermission],
					ref:ZaAccount.A_displayname, 
					items: [
						{ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,	cssClass:"admin_xform_name_input", width:150,
							enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autodisplayname,"FALSE"],XFormItem.prototype.hasWritePermission],
							enableDisableChangeEventSources:[ZaAccount.A2_autodisplayname],bmolsnr:true,visibilityChecks:[]
						},
						{ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
							elementChanged: function(elementValue,instanceValue, event) {
								if(elementValue=="TRUE") {
									if(ZaAccount.generateDisplayName.call(this, this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),this.getInstanceValue(ZaAccount.A_initials))) {
										this.getForm().parent.setDirty(true);
									}
								}
								this.getForm().itemChanged(this, elementValue, event);
							},
							enableDisableChecks:[[XFormItem.prototype.hasWritePermission,ZaAccount.A_displayname]]
						}
					]
				},
				{ref:ZaAccount.A_zimbraMailCanonicalAddress, type:_TEXTFIELD_,width:250,
					msgName:ZaMsg.NAD_CanonicalFrom,label:ZaMsg.NAD_CanonicalFrom, labelLocation:_LEFT_, align:_LEFT_
				},
				{ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_,
				  msgName:ZaMsg.NAD_zimbraHideInGal,
				  label:ZaMsg.NAD_zimbraHideInGal, trueValue:"TRUE", falseValue:"FALSE"
				}
			]}
		];
	
		var setupGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountSetupGrouper, id:"account_form_setup_group", 
			colSizes:["275px","*"],numCols:2,
			items: [
				{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,
					label:ZaMsg.NAD_AccountStatus, 
					labelLocation:_LEFT_, choices:this.accountStatusChoices
				}
			]
		}
			
//		if(ZaSettings.COSES_ENABLED) {
			setupGroup.items.push(
				{type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
					visibilityChecks:[[XFormItem.prototype.hasWritePermission,ZaAccount.A_COSId]],
					items: [
						{ref:ZaAccount.A_COSId, type:_DYNSELECT_,label: null, choices:this.cosChoices,
							inputPreProcessor:ZaAccountXFormView.preProcessCOS,
							enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autoCos,"FALSE"]],
							enableDisableChangeEventSources:[ZaAccount.A2_autoCos],
							visibilityChecks:[],
							dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
							onChange:ZaAccount.setCosChanged,
							emptyText:ZaMsg.enterSearchTerm,
							dataFetcherClass:ZaSearch,editable:true,getDisplayValue:function(newValue) {
									if(ZaItem.ID_PATTERN.test(newValue)) {
										var cos = ZaCos.getCosById(newValue);
										if(cos)
											newValue = cos.name;
									} 
									if (newValue == null) {
										newValue = "";
									} else {
										newValue = "" + newValue;
									}
									return newValue;
								}
						},
						{ref:ZaAccount.A2_autoCos, type:_CHECKBOX_,
							visibilityChecks:[], 
							msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
							trueValue:"TRUE", falseValue:"FALSE" ,
							elementChanged: function(elementValue,instanceValue, event) {
								this.getForm().parent.setDirty(true);
								if(elementValue=="TRUE") {
									ZaAccount.setDefaultCos(this.getInstance());	
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						}
					]
				});
	//	}
		
		setupGroup.items.push({ref:ZaAccount.A_isAdminAccount, type:_CHECKBOX_, 
								msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin,
								trueValue:"TRUE", falseValue:"FALSE"
							});
		case1Items.push(setupGroup);
		
		var passwordGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_form_password_group", 
			visibilityChecks:[[XFormItem.prototype.hasRight,ZaAccount.SET_PASSWORD_RIGHT]],
			colSizes:["275px","*"],numCols:2,
			items:[
			{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
				label:ZaMsg.NAD_Password, labelLocation:_LEFT_,
				visibilityChecks:[],enableDisableChecks:[], 
				cssClass:"admin_xform_name_input"
			},
			{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
				label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,
				visibilityChecks:[], enableDisableChecks:[],
				cssClass:"admin_xform_name_input"
			},
			{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,
				visibilityChecks:[],  enableDisableChecks:[],
				msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE"}
			]
		};
		case1Items.push(passwordGroup);														
		
		var notesGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_form_notes_group",
			colSizes:["275px","*"],numCols:2,
		 	items:[
	
			{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,
				label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input"
			},
			{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,
				label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:"30em"
			}
			]
		};
	
		case1Items.push(notesGroup);
		case1.items = case1Items;
		cases.push(case1);
	//}
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_CONTACT_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		var case2={type:_ZATABCASE_, numCols:1, caseKey:_tab2, 
					items: [
						{type:_ZAGROUP_, 
							items:[
								{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, width:250}
							]
						},
						{type:_ZAGROUP_, 
							items:[					
								{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, width:250},														
								{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, width:250}
							]
						},
						{type:_ZAGROUP_, 
							items:[						
								{ref:ZaAccount.A_street, type:_TEXTAREA_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state, labelLocation:_LEFT_, width:250},
								{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip, labelLocation:_LEFT_, width:100},
								{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country, labelLocation:_LEFT_, width:250}
							]
						}							
					]
				};
		cases.push(case2);
	}
	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_MEMBEROF_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		//MemberOf Tab
		var case3={type:_ZATABCASE_, numCols:2, caseKey:_tab3, colSizes: ["50%","50%"],
					items: [
						{type:_SPACER_, height:"10"},
						//layout rapper around the direct/indrect list						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
								//direct member group
								{type:_ZALEFT_GROUPER_, numCols:1, width: "100%", 
									label:ZaMsg.Account_DirectGroupLabel,
									items:[
										{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
                                            onSelection:ZaAccountXFormView.directMemberOfSelectionListener,
                                            forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAllButton,ZaAccount.A2_directMemberList]],
													enableDisableChangeEventSources:[ZaAccount.A2_directMemberList],
												   	onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
												},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAddRemoveButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberListSelected],
											      	onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)"
											    },
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberList +"_offset"]
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_more"]
											    },								       
												{type:_CELLSPACER_}									
											]
										}		
									]
								},	
								{type:_SPACER_, height:"10"},	
								//indirect member group
								{type:_ZALEFT_GROUPER_, numCols:1,  width: "100%", label:ZaMsg.Account_IndirectGroupLabel,
									items:[
										{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
                                            onSelection:ZaAccountXFormView.indirectMemberOfSelectionListener,
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
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_offset"]
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_more"]
											    },								       
												{type:_CELLSPACER_}									
											]
										}
									]
								}
							]
						},

						//non member group
						{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_NonGroupLabel,
							items:[
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
								      		},
								      		visibilityChecks:[],
								      		enableDisableChecks:[]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
										   onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
										},
										{ref: ZaAccount.A2_showSameDomain, type: _CHECKBOX_, align:_RIGHT_, msgName:ZaMsg.NAD_SearchSameDomain,
												label:AjxMessageFormat.format (ZaMsg.NAD_SearchSameDomain),
												labelCssClass:"xform_label",
												labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",
												enableDisableChecks:[function() { return ZaSettings.DOMAINS_ENABLED; }],
												visibilityChecks:[]
										}										
									]
						         },
						        {type:_SPACER_, height:"5"},
								
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "100%", height: 455,
									cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
									headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
                                    onSelection:ZaAccountXFormView.nonMemberOfSelectionListener,
                                    forceUpdate: true },
									
								{type:_SPACER_, height:"5"},	
								//add action buttons
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
									items: [
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAddRemoveButton,ZaAccount.A2_nonMemberList]],
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberListSelected],
											onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)"
										},
									   	{type:_CELLSPACER_},
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAllButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)"
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_offset"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_more"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList]],
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
					]
				};
				
		cases.push(case3);		
	}				
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_FEATURES_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_,id:"account_form_features_tab",  numCols:1, width:"100%", caseKey:_tab4, 
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
								trueValue:"TRUE", falseValue:"FALSE"
							},							
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,
								type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureContactsEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureContactsEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureCalendarEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureCalendarEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureTasksEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureTaskEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureTaskEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},													
							{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureNotebookEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureBriefcasesEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},								
							{ref:ZaAccount.A_zimbraFeatureIMEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureIMEnavbled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureIMEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},								
							{ref:ZaAccount.A_zimbraFeatureOptionsEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_zimbraFeatureOptionsEnabled,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureOptionsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"}
						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraGeneralFeature, id:"account_form_features_general", colSizes:["auto"],numCols:1,
						items:[							
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureTaggingEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureTaggingEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,checkBoxLabel:ZaMsg.NAD_FeatureChangePasswordEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, trueValue:"TRUE", falseValue:"FALSE"}	,
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, trueValue:"TRUE", falseValue:"FALSE"},														
							{ref:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,checkBoxLabel:ZaMsg.NAD_FeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraMailFeature, id:"account_form_features_mail", colSizes:["auto"],numCols:1,
						enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureMailEnabled,"TRUE"]],
						enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailEnabled, ZaAccount.A_COSId],
						items:[													
							{ref:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled, trueValue:"TRUE", falseValue:"FALSE"}	,
							{ref:ZaAccount.A_zimbraFeatureFlaggingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureFlaggingEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureFlaggingEnabled, trueValue:"TRUE", falseValue:"FALSE"}	,
							{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,checkBoxLabel:ZaMsg.NAD_zimbraImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,checkBoxLabel:ZaMsg.NAD_zimbraPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureImapDataSourceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraExternalImapEnabled,checkBoxLabel:ZaMsg.NAD_zimbraExternalImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},									
							{ref:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraExternalPop3Enabled,checkBoxLabel:ZaMsg.NAD_zimbraExternalPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,checkBoxLabel:ZaMsg.NAD_FeatureConversationsEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,checkBoxLabel:ZaMsg.NAD_FeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureIdentitiesEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.NAD_FeatureIdentitiesEnabled,
								checkBoxLabel:ZaMsg.NAD_FeatureIdentitiesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							}							
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraCalendarFeature, id:"account_form_features_calendar",colSizes:["auto"],numCols:1,
						enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureCalendarEnabled,"TRUE"]],
						enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureCalendarEnabled,ZaAccount.A_COSId],
						items:[						
							{ref:ZaAccount.A_zimbraFeatureGroupCalendarEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled,checkBoxLabel:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE"}	
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraIMFeature, id:"account_form_features_im", colSizes:["auto"],numCols:1,
						visibilityChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureIMEnabled,"TRUE"]],
						visibilityChangeEventSources:[ZaAccount.A_zimbraFeatureIMEnabled,ZaAccount.A_COSId],
						items:[	
							{ref:ZaAccount.A_zimbraFeatureInstantNotify,
								 type:_SUPER_CHECKBOX_,
								 msgName:ZaMsg.NAD_zimbraFeatureInstantNotify,
								 checkBoxLabel:ZaMsg.NAD_zimbraFeatureInstantNotify,
								 trueValue:"TRUE",
								 falseValue:"FALSE",
								 resetToSuperLabel:ZaMsg.NAD_ResetToCOS
							}											
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraSearchFeature, id:"account_form_features_search", colSizes:["auto"],numCols:1,
						items:[						
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,checkBoxLabel:ZaMsg.NAD_FeatureAdvancedSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,checkBoxLabel:ZaMsg.NAD_FeatureSavedSearchesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,checkBoxLabel:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"}
						]
					}
				]
			});
	}
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_PREFS_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
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
									labelLocation:_LEFT_},
								{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPER_TEXTFIELD_,
									colSizes:["175px","375px","190px"], 
									msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefMailInitialSearch, 
									labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefShowSearchString, 
									colSizes:["175px","375px","190px"],
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefShowSearchString,checkBoxLabel:ZaMsg.NAD_zimbraPrefShowSearchString,trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,  
									trueValue:"TRUE", falseValue:"FALSE",
									colSizes:["175px","375px","190px"]
								},
								{ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, 
									colSizes:["175px","375px","190px"],
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.NAD_prefKeyboardShort, trueValue:"TRUE", falseValue:"FALSE"},
								
								{ref:ZaAccount.A_zimbraPrefWarnOnExit, type:_SUPER_CHECKBOX_, nowrap:false,labelWrap:true,
									colSizes:["175px","375px","190px"],	
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.NAD_zimbraPrefWarnOnExit,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									labelWrap: true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, checkBoxLabel:ZaMsg.NAD_zimbraPrefShowSelectionCheckbox,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled, 
									trueValue:"TRUE", falseValue:"FALSE"},
                                {ref:ZaAccount.A_zimbraPrefLocale, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
                                    choices: ZaSettings.getLocaleChoices(),
                                    msgName:ZaMsg.NAD_zimbraPrefLocale,label:ZaMsg.NAD_zimbraPrefLocale,
									 labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS}
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
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefDisplayExternalImages, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefDisplayExternalImages,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefDisplayExternalImages, 
									trueValue:"TRUE", falseValue:"FALSE"},	
								{ref:ZaAccount.A_zimbraPrefGroupMailBy,
									type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px",colSizes:["375px","190px"], 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,
									label:ZaMsg.NAD_zimbraPrefGroupMailBy, 
									labelLocation:_LEFT_},
								{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, 
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,
									label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null},
								{ref:ZaAccount.A_zimbraPrefMailDefaultCharset, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"], 
									msgName:ZaMsg.NAD_zimbraPrefMailDefaultCharset,label:ZaMsg.NAD_zimbraPrefMailDefaultCharset,
									 labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS}
							]
						},
						{type:_ZA_TOP_GROUPER_,colSizes:["175px","auto"], id:"account_prefs_mail_receiving",
							label:ZaMsg.NAD_MailOptionsReceiving,
							items :[
								{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
									nowrap:false,labelWrap:true									
								},							
								{ref:ZaAccount.A_zimbraMailMinPollingInterval, 
									type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraMailMinPollingInterval,
									txtBoxLabel:ZaMsg.NAD_zimbraMailMinPollingInterval+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, 
									type:_ZA_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,
									label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, 
									type:_TEXTFIELD_, 
									msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,
									label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress, 
									labelLocation:_LEFT_,
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraPrefNewMailNotificationEnabled,"TRUE"]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefNewMailNotificationEnabled],
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,
									label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled, trueValue:"TRUE", 
									falseValue:"FALSE"
								},							
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, 
									type:_SUPER_LIFETIME_, 
									colSizes:["175px","80px","295px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration,
									txtBoxLabel:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration+":", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, 
									type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,
									label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", 
									width:"30em",
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,"TRUE"]],
									enableDisableChangeEvantSources:[ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled]
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
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraAllowAnyFromAddress,  
									colSpan:2,								
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraAllowAnyFromAddress,
									checkBoxLabel:ZaMsg.NAD_zimbraAllowAnyFromAddress,
									trueValue:"TRUE", falseValue:"FALSE"},	
									
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
									removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
									items: [
										{ref:".", type:_TEXTFIELD_, label:null,width:"200px"}
									],
									onRemove:ZaAccountXFormView.onRepeatRemove,
									visibilityChecks:[ZaAccountXFormView.isSendingFromAnyAddressDisAllowed],
									visibilityChangeEventSources:[ZaAccount.A_zimbraAllowAnyFromAddress, ZaAccount.A_COSId]								
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
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefComposeFormat, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefComposeFormat,
									label:ZaMsg.NAD_zimbraPrefComposeFormat},
								
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, 
									type:_SUPER_DWT_COLORPICKER_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, 
									//colSpan:2,								
									type:_SUPER_CHECKBOX_, 
									colSizes:["175px","375px","190px"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, 
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_prefMailSignatureEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,
									label:ZaMsg.NAD_prefMailSignatureEnabled,  
									trueValue:"TRUE", falseValue:"FALSE"},	
								{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, 
									//colSpan:2,								
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									colSizes:["175px","375px","190px"],
									msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,
									checkBoxLabel:ZaMsg.NAD_zimbraPrefMailSignatureStyle,
									trueValue:"internet", falseValue:"outlook"},
								{ref:ZaAccount.A_zimbraMailSignatureMaxLength, 
									//colSpan:2,	
									type:_SUPER_TEXTFIELD_, 
									colSizes:["175px","375px","190px"], 						
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									labelLocation:_LEFT_, 
									msgName:ZaMsg.NAD_zimbraMailSignatureMaxLength,
									txtBoxLabel:ZaMsg.NAD_zimbraMailSignatureMaxLength,
									textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_prefMailSignature, type:_TEXTAREA_, 
									msgName:ZaMsg.NAD_prefMailSignature,
									label:ZaMsg.NAD_prefMailSignature, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", width:"30em",
									enableDisableChangeEventSources:[ZaAccount.A_prefMailSignatureEnabled],
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_prefMailSignatureEnabled,"TRUE"]]
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
									trueValue:"TRUE", falseValue:"FALSE",
									colSpan:2
								},							
								{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled,colSpan:2,
									colSizes:["175px","375px","190px"], 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE"},	
								{ref:ZaAccount.A_zimbraPrefContactsPerPage, type:_SUPER_SELECT1_,
									labelCssStyle:"width:175px", colSizes:["375px","190px"],
									msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null}		
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
									msgName:ZaMsg.NAD_zimbraPrefTimeZoneId,label:ZaMsg.NAD_zimbraPrefTimeZoneId+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
								{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
									colSizes:["375px","190px"], labelCssStyle:"width:175px", 
									type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
								{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_CHECKBOX_,
								colSizes:["175px","375px","190px"], resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,checkBoxLabel:ZaMsg.NAD_alwaysShowMiniCal, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, 
								colSizes:["175px","375px","190px"], type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,checkBoxLabel:ZaMsg.NAD_useQuickAdd, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, 
								colSizes:["175px","375px","190px"], type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,checkBoxLabel:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,trueValue:"TRUE", falseValue:"FALSE"}
							]
						}						
					];
		cases.push({type:_ZATABCASE_, id:"account_form_prefs_tab", numCols:1, 
					width:"100%", caseKey:_tab5, 
					/*colSizes:["275px","275px","150px"],*/ items :prefItems});
	}


	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ALIASES_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_, id:"account_form_aliases_tab", width:"100%", numCols:1,colSizes:["auto"],
					caseKey:_tab6, 
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
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteAliasButtonListener.call(this);",id:"deleteAliasButton",
											enableDisableChecks:[ZaAccountXFormView.isDeleteAliasEnabled],
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editAliasButtonListener.call(this);",id:"editAliasButton",
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache],
											enableDisableChecks:[ZaAccountXFormView.isEditAliasEnabled]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addAliasButtonListener.call(this);"
										}
									]
								}
							]
						}
					]
				});
	}
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_FORWARDING_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_,id:"account_form_forwarding_tab", width:"100%", numCols:1,colSizes:["auto"],
					caseKey:_tab7,  
					items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_EditFwdTopGroupGrouper,
							id:"account_form_user_forwarding_addr",colSizes:["auto"],numCols:1,
							items :[					
							{
								ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								type:_SUPER_CHECKBOX_, colSpan:2,
								checkBoxLabel:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							},
							{type:_GROUP_,numCols:3,colSpan:3,colSizes:["275px","275px","150px"], 
						  		items:[					  	
									{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, 
										type:_ZA_CHECKBOX_, 
										msgName:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled,
										label:ZaMsg.NAD_zimbraPrefMailLocalDeliveryDisabled, 
										trueValue:"TRUE", falseValue:"FALSE"
									},	
									{type:_SPACER_},						
									{ref:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_TEXTFIELD_, width:230,
										msgName:ZaMsg.NAD_zimbraPrefMailForwardingAddress,
										label:ZaMsg.NAD_zimbraPrefMailForwardingAddress+":", labelLocation:_LEFT_,
										align:_LEFT_,
										enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureMailForwardingEnabled,"TRUE"]],
										enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailForwardingEnabled, ZaAccount.A_COSId]										
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
											enableDisableChecks:[ZaAccountXFormView.isDeleteFwdAddrEnabled],
											enableDisableChangeEventSources:[ZaAccount.A2_fwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isEditFwdAddrEnabled],
											enableDisableChangeEventSources:[ZaAccount.A2_fwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addFwdAddrButtonListener.call(this);"
										}
									]
								}
							]
						}						
					]
				});
	}

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_INTEROP_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_, id:"account_form_interop_tab", width:"100%", numCols:1,colSizes:["auto"],
					caseKey:_tab8, 
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
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Push,width:"100px",
											onActivate:"ZaAccountXFormView.pushFpButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isPushFpEnabled],
											enableDisableChangeEventSources:[ZaAccount.A_zimbraForeignPrincipal]
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                            onActivate:"ZaAccountXFormView.deleteFpButtonListener.call(this);",
                                            enableDisableChecks:[ZaAccountXFormView.isDeleteFpEnabled],
                                            enableDisableChangeEventSources:[ZaAccount.A2_fp_selection_cache]
                                        },
                                        {type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                            onActivate:"ZaAccountXFormView.editFpButtonListener.call(this);",
                                            enableDisableChecks:[ZaAccountXFormView.isEditFpEnabled],
                                            enableDisableChangeEventSources:[ZaAccount.A2_fp_selection_cache]
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

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_SKIN_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_, id:"account_form_themes_tab", numCols:1,
            caseKey:_tab9,
			items:[
				{type:_SPACER_},
				{type:_GROUP_, 
					items:[
					{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_,choices:ZaApp.getInstance().getInstalledSkins(),
						visibilityChecks:[ZaAccountXFormView.gotSkins]}
					] 
				},
				{type:_SPACER_},
				{type:_SUPER_ZIMLET_SELECT_CHECK_,
					selectRef:ZaAccount.A_zimbraAvailableSkin, 
					ref:ZaAccount.A_zimbraAvailableSkin, 
					choices:ZaAccountXFormView.themeChoices,
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
					visibilityChangeEventSources:[ZaModel.currentTab],
					caseKey:_tab9, caseVarRef:ZaModel.currentTab,
					limitLabel:ZaMsg.NAD_LimitThemesTo
				}
			] 
		});
	}	

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ZIMLET_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_, id:"account_form_zimlets_tab", numCols:1,
            caseKey:_tab10, 
			items:[
				{type:_ZAGROUP_, numCols:1,colSizes:["auto"], 
					items: [
						{type:_SUPER_ZIMLET_SELECT_CHECK_,
							selectRef:ZaAccount.A_zimbraZimletAvailableZimlets, 
							ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
							choices:ZaAccountXFormView.zimletChoices,
							visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
							visibilityChangeEventSources:[ZaModel.currentTab],
							caseKey:_tab10, caseVarRef:ZaModel.currentTab,
							limitLabel:ZaMsg.NAD_LimitZimletsTo
						}
					]
				}
			] 
		});
	}
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNTS_ADVANCED_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		cases.push({type:_ZATABCASE_, id:"account_form_advanced_tab", numCols:1,
					caseKey:_tab11, 
					items: [
						{type:_ZA_TOP_GROUPER_, id:"account_attachment_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_AttachmentsGrouper,
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,
									checkBoxLabel:ZaMsg.NAD_RemoveAllAttachments, 
									trueValue:"TRUE", falseValue:"FALSE"
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
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,txtBoxLabel:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraQuotaWarnPercent, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnPercent, msgName:ZaMsg.NAD_QuotaWarnPercent,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnInterval, type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnInterval, msgName:ZaMsg.NAD_QuotaWarnInterval,labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnMessage, type:_SUPER_TEXTAREA_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.NAD_QuotaWarnMessage, msgName:ZaMsg.NAD_QuotaWarnMessage,
									labelCssStyle:"vertical-align:top", textAreaWidth:"250px", 
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
								 	trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraMinPwdLength, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_passMinLength,
									txtBoxLabel:ZaMsg.NAD_passMinLength+":", 
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,txtBoxLabel:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},

								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,txtBoxLabel:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
																
								{ref:ZaAccount.A_zimbraMinPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,txtBoxLabel:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraMaxPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,txtBoxLabel:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,txtBoxLabel:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input"}
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"password_lockout_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_FailedLoginGrouper,
							items :[
								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,
									checkBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPER_TEXTFIELD_, 
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPER_LIFETIME_, 
									colSpan:3,
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPER_LIFETIME_, 
									colSpan:3,									
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],								
									txtBoxLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
									toolTipContent:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
									textFieldCssClass:"admin_xform_number_input", 
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
									//enableDisableChecks:[ZaAccountXFormView.isAdminAccount],
									enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A_isAdminAccount,"TRUE"] ],
									enableDisableChangeEventSources:[ZaAccount.A_isAdminAccount]
								},								
								{ref:ZaAccount.A_zimbraAuthTokenLifetime,
									type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_AuthTokenLifetime,
									txtBoxLabel:ZaMsg.NAD_AuthTokenLifetime+":"},										
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, 
									type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailIdleSessionTimeout,
									txtBoxLabel:ZaMsg.NAD_MailIdleSessionTimeout+":"}															
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
                                  visibilityChecks:[ZaAccount.isEmailRetentionPolicyDisabled],
                                  visibilityChangeEventSources:[ZaAccount.A_mailHost]
                                },
                                { type: _GROUP_ ,
                                    enableDisableChecks:[ZaAccount.isEmailRetentionPolicyEnabled],
                                    enableDisableChangeEventSources:[ZaAccount.A_mailHost],
                                    items: [
                                        {ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPER_LIFETIME2_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.NAD_MailMessageLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailMessageLifetime+":"},
                                        {ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailTrashLifetime+":"},
                                        {ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.NAD_MailSpamLifetime,
                                            txtBoxLabel:ZaMsg.NAD_MailSpamLifetime}
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
                                    textFieldCssClass:"admin_xform_number_input"
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
