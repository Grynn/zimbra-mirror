/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
* This class describes a view of a single email Account
* @class ZaAccountXFormView
* @contructor
* @param parent {DwtComposite}
* @param app {ZaApp}
* @author Greg Solovyev
**/
ZaAccountXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent,  
		iKeyName:"ZaAccountXFormView",
		contextId:ZaId.TAB_ACCT_EDIT
	});	
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_ACTIVE)},
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_CLOSED)},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKED)},
        {value:ZaAccount.ACCOUNT_STATUS_LOCKOUT, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKOUT), visible: false},    
        {value:ZaAccount.ACCOUNT_STATUS_PENDING, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_PENDING)},
        {value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_MAINTENANCE)}
	];
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	this.TAB_INDEX = 0;
	this._domains = {} ;
	//console.time("ZaAccountXFormView.initForm");
	//DBG.timePt(AjxDebug.PERF, "started initForm");
	this.initForm(ZaAccount.myXModel,this.getMyXForm(entry), null);
    this._localXForm._setAllowSelection();//bug13705,allow account copyable
	//console.timeEnd("ZaAccountXFormView.initForm");
	//DBG.timePt(AjxDebug.PERF, "finished initForm");
}

ZaAccountXFormView.prototype = new ZaTabView();
ZaAccountXFormView.prototype.constructor = ZaAccountXFormView;
ZaTabView.XFormModifiers["ZaAccountXFormView"] = new Array();
ZaTabView.ObjectModifiers["ZaAccountXFormView"] = [] ;
ZaAccountXFormView.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaAccountXFormView.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);

/**
 * * Get Tab's Icon according to different account's type
 * **/
ZaAccountXFormView.prototype.getTabIcon = 
function () {
	if (this._containedObject && this._containedObject.attrs) {
		var resultType;
                var account = this._containedObject;
		if(account.attrs[ZaAccount.A_zimbraIsAdminAccount]=="TRUE" ) {
                       resultType = "AdminUser";
                } else if (account.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] == "TRUE") {
                       resultType = "DomainAdminUser";
                } else if (account.attrs[ZaAccount.A_zimbraIsSystemResource] == "TRUE") {
                       resultType = "SystemResource";
                } else {
                       resultType = "Account";
                }
		return resultType;	
	}else{
		return "Account" ;
	}
}

/**
* Sets the object contained in the view
* @param entry - {ZaAccount} object to display
**/
ZaAccountXFormView.prototype.setObject =
function(entry) {
	//handle the special attributes to be displayed in xform
	//TODO  manageSpecialAttrs can be part of ZaItem.ObjectModifiers ;
    entry.manageSpecialAttrs();
	entry.modifyObject();

    this._containedObject = new Object();
	this._containedObject.attrs = new Object();
    
    for (var a in entry.attrs) {
		var modelItem = this._localXForm.getModel().getItem(a) ;
        if ((modelItem != null && modelItem.type == _LIST_) || (entry.attrs[a] != null && entry.attrs[a] instanceof Array)) {  
        	//need deep clone
            this._containedObject.attrs [a] =
                    ZaItem.deepCloneListItem (entry.attrs[a]);
        } else {
            this._containedObject.attrs[a] = entry.attrs[a];
        }
	}
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type;

	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
    else this._containedObject.setAttrs = {};
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
	
	if(entry.id)
		this._containedObject.id = entry.id;
	
	//add the member group, need a deep clone
//	this._containedObject[ZaAccount.A2_memberOf] = entry [ZaAccount.A2_memberOf];
//    this._containedObject[ZaAccount.A2_memberOf] = {};
    this._containedObject[ZaAccount.A2_memberOf] =
                ZaAccountMemberOfListView.cloneMemberOf(entry);
    
    //add the memberList page information
	this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ALIASES_TAB_ATTRS, ZaAccountXFormView.ALIASES_TAB_RIGHTS)) {
		if(this._containedObject.attrs[ZaAccount.A_zimbraMailAlias]) {
			if(!this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] instanceof Array) {
				this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] = [this._containedObject.attrs[ZaAccount.A_zimbraMailAlias]];		
			}
		}		
	}	
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.FORWARDING_TAB_ATTRS, ZaAccountXFormView.FORWARDING_TAB_RIGHTS)) {
		if(this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress]) {
			if(!this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress] instanceof Array) {
				this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress] = [this._containedObject.attrs[ZaAccount.A_zimbraMailForwardingAddress]];		
			}
		}		
	}
					
	if(this._containedObject.attrs[ZaAccount.A_COSId]) {	
		this._containedObject[ZaAccount.A2_autoCos] = "FALSE" ;
	}
	if(!this._containedObject.attrs[ZaAccount.A_COSId]) {
		this._containedObject[ZaAccount.A2_autoCos] = "TRUE" ;
	}
	if(this._containedObject.setAttrs[ZaAccount.A_COSId]) {
		var cos = ZaCos.getCosById(this._containedObject.attrs[ZaAccount.A_COSId]);	
		this.cosChoices.setChoices([cos]);
		this.cosChoices.dirtyChoices();
	}

	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname];
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword];
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
	
    //check the account type here 
    var domainName = ZaAccount.getDomain (this._containedObject.name) ;
    var domainObj =  ZaDomain.getDomainByName (domainName) ;
    this._containedObject[ZaAccount.A2_accountTypes] = domainObj.getAccountTypes () ;
    this._containedObject[ZaAccount.A2_currentAccountType] = entry[ZaAccount.A2_currentAccountType]  ;
//    ZaAccountXFormView.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
	if(!entry.getAttrs || entry.getAttrs[ZaAccount.A_zimbraAvailableSkin] || entry.getAttrs.all) {
		var skins = ZaApp.getInstance().getInstalledSkins();
		
		if(AjxUtil.isEmpty(skins)) {
			
			if(domainObj && domainObj.attrs && !AjxUtil.isEmpty(domainObj.attrs[ZaDomain.A_zimbraAvailableSkin])) {
				//if we cannot get all zimlets try getting them from domain
				skins = domainObj.attrs[ZaDomain.A_zimbraAvailableSkin];
			} else if(entry._defaultValues && entry._defaultValues.attrs && !AjxUtil.isEmpty(entry._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin])) {
				//if we cannot get all zimlets from domain either, just use whatever came in "defaults" which would be what the COS value is
				skins = entry._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin];
			} else {
				skins = [];
			}
		} else {
			if (AjxUtil.isString(skins))	 {
				skins = [skins];
			}
		}
		
		ZaAccountXFormView.themeChoices.setChoices(skins);
		ZaAccountXFormView.themeChoices.dirtyChoices();		
		
	}
	
	if(!entry.getAttrs || entry.getAttrs[ZaAccount.A_zimbraZimletAvailableZimlets] || entry.getAttrs.all) {
		//get sll Zimlets
		var allZimlets = ZaZimlet.getAll("extension");

		if(!AjxUtil.isEmpty(allZimlets) && allZimlets instanceof ZaItemList || allZimlets instanceof AjxVector)
			allZimlets = allZimlets.getArray();

		if(AjxUtil.isEmpty(allZimlets)) {
			
			if(domainObj && domainObj.attrs && !AjxUtil.isEmpty(domainObj.attrs[ZaDomain.A_zimbraZimletDomainAvailableZimlets])) {
				//if we cannot get all zimlets try getting them from domain
				allZimlets = domainObj.attrs[ZaDomain.A_zimbraZimletDomainAvailableZimlets];
			} else if(entry._defaultValues && entry._defaultValues.attrs && !AjxUtil.isEmpty(entry._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets])) {
				//if we cannot get all zimlets from domain either, just use whatever came in "defaults" which would be what the COS value is
				allZimlets = entry._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
			} else {
				allZimlets = [];
			}
			ZaAccountXFormView.zimletChoices.setChoices(allZimlets);
			ZaAccountXFormView.zimletChoices.dirtyChoices();
			
		} else {
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
	}

    if (domainObj && domainObj.attrs &&
        domainObj.attrs[ZaDomain.A_AuthMech] &&
        (domainObj.attrs[ZaDomain.A_AuthMech] != ZaDomain.AuthMech_zimbra) ) {
        this._containedObject[ZaAccount.A2_isExternalAuth] = true;
    } else {
        this._containedObject[ZaAccount.A2_isExternalAuth] = false;
    }

	if(ZaItem.modelExtensions["ZaAccount"]) {
		for(var i = 0; i< ZaItem.modelExtensions["ZaAccount"].length;i++) {
			var ext = ZaItem.modelExtensions["ZaAccount"][i];
			if(entry[ext]) {
                if (entry[ext] instanceof Array) {
                    this._containedObject[ext] = ZaItem.deepCloneListItem (entry[ext]);
                    if (entry[ext]._version) {
                        this._containedObject[ext]._version = entry[ext]._version;
                    }

                } else {
                    this._containedObject[ext] = {};
                    for (var a in entry[ext]) {
                        var modelItem = this._localXForm.getModel().getItem(a) ;
                        if ((modelItem != null && modelItem.type == _LIST_)
                           || (entry[ext][a] != null && entry[ext][a] instanceof Array))
                        {  //need deep clone
                            this._containedObject[ext][a] =
                                    ZaItem.deepCloneListItem (entry[ext][a]);
                        } else {
                            this._containedObject[ext][a] = entry[ext][a];
                        }
                    }
                }
			}
			
		}
	}

	this.modifyContainedObject () ;

    this._localXForm.setInstance(this._containedObject);
    
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
	
	//update the tab
    if (!appNewUI)
	    this.updateTab();
}

ZaAccountXFormView.gotNoSkins = function() {
	return !ZaAccountXFormView.gotSkins.call(this);
}

ZaAccountXFormView.gotSkins = function () {
	return (
			( (ZaApp.getInstance() != null) 
			  && (ZaApp.getInstance().getInstalledSkins() != null) 
			  && (ZaApp.getInstance().getInstalledSkins().length > 0)
			 ) 
             || !AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A_zimbraAvailableSkin))
             || !AjxUtil.isEmpty(this.getInstance()._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin])
           );
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

ZaAccountXFormView.cosGroupItemId = "cos_grouper_" + Dwt.getNextId();
ZaAccountXFormView.prototype.updateCosGrouper =
function () {
    var item = this._localXForm.getItemsById (ZaAccountXFormView.cosGroupItemId) [0] ;
    item.items[0].setElementEnabled(true);
    item.updateElement() ;
}
/*
ZaAccountXFormView.onRepeatRemove = 
function (index, form) {
	var path = this.getRefPath();
	this.getModel().removeRow(this.getInstance(), path, index);
	this.items[index].clearError();
	this.getForm().setIsDirty(true,this);	
	//form.parent.setDirty(true);
}*/


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

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountMemberOfListView._addSelectedLists(this.getForm(), arr);
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

    if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountMemberOfListView._removeSelectedLists(this.getForm(), arr);
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
					if(aliasArr[k]==instance[ZaAccount.A2_alias_selection_cache][i]) {
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
		obj[ZaAccount.A_name] = instance[ZaAccount.A2_alias_selection_cache][0];
		var cnt = instance.attrs[ZaAccount.A_zimbraMailAlias].length;
		for(var i=0;i<cnt;i++) {
			if(instance[ZaAccount.A2_alias_selection_cache][0]==instance.attrs[ZaAccount.A_zimbraMailAlias][i]) {
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
			//get domain name
			var domain;
			var domainName = ZaAccount.getDomain(obj[ZaAccount.A_name]);
			try {
				domain = ZaDomain.getDomainByName(domainName);
			} catch (ex) {
				
			}
			//check if have access to create aliases in this domain
			if(!domain || !ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ALIAS, domain)) {		
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_PERMISSION_CREATE_ALIAS, [domainName])) ;
			} else {
                var viewController = null;
				viewController = ZaApp.getInstance().getControllerById (this.parent.__internalId);

				var account = null;
				if(viewController) {
					account = viewController._findAlias(obj[ZaAccount.A_name]);
				}

				if(account) {
					var warning = null;
                    switch(account.type) {
							case ZaItem.DL:
								if(account.name == obj[ZaAccount.A_name]) {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS3,[account.name]);
								} else {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS4,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							case ZaItem.ACCOUNT:
								if(account.name == obj[ZaAccount.A_name]) {
									warning= AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS2,[account.name]);
								} else {
									warning= AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS1,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							case ZaItem.RESOURCE:
								if(account.name == obj[ZaAccount.A_name]) {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS5,[account.name]);
								} else {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS6,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							default:
								warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS0,[obj[ZaAccount.A_name]]);
							break;
                    }
					ZaApp.getInstance().getCurrentController().popupErrorDialog(warning);
				} else {
                    arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
                    this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr);
                    this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A2_alias_selection_cache, new Array());
                    this.parent.setDirty(true);
                }
			}
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
			//get domain name
			var domain;
			var domainName = ZaAccount.getDomain(obj[ZaAccount.A_name]);
			try {
				domain = ZaDomain.getDomainByName(domainName);
			} catch (ex) {
				
			}
			//check if have access to create aliases in this domain
			if(!domain || !ZaItem.hasRight(ZaDomain.RIGHT_CREATE_ALIAS, domain)) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_PERMISSION_CREATE_ALIAS, [domainName])) ;
			} else {
				var viewController = null;
				viewController = ZaApp.getInstance().getControllerById (this.parent.__internalId);
				
				var account = null; 
				if(viewController) {
					account = viewController._findAlias(obj[ZaAccount.A_name]);
				}
				
				if(account) {
					var warning = null;
                    switch(account.type) {
							case ZaItem.DL:
								if(account.name == obj[ZaAccount.A_name]) {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS3,[account.name]);
								} else {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS4,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							case ZaItem.ACCOUNT:
								if(account.name == obj[ZaAccount.A_name]) {
									warning= AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS2,[account.name]);
								} else {
									warning= AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS1,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							case ZaItem.RESOURCE:
								if(account.name == obj[ZaAccount.A_name]) {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS5,[account.name]);
								} else {
									warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS6,[account.name, obj[ZaAccount.A_name]]);
								}
							break;
							default:
								warning = AjxMessageFormat.format(ZaMsg.WARNING_EACH_ALIAS0,[obj[ZaAccount.A_name]]);
							break;
                    }
					ZaApp.getInstance().getCurrentController().popupErrorDialog(warning);
				}
				else {
					var instance = this.getInstance();
					var arr = instance.attrs[ZaAccount.A_zimbraMailAlias]; 
					arr.push(obj[ZaAccount.A_name]);
					this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_zimbraMailAlias, arr);
					this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A2_alias_selection_cache, new Array());
					this.parent.setDirty(true);
				}
				
			}
		}
	}
}


ZaAccountXFormView.isAuthfromInternal =
function(acctName) {
	var res = true;
	var domainName = null
	var acct = acctName.split("@");
	if (acct.length == 2) domainName = acct[1];
	else domainName = acct[0];
	
	if(domainName) {
		var domainObj = ZaDomain.getDomainByName(domainName);
		if(domainObj.attrs[ZaDomain.A_AuthMech] != ZaDomain.AuthMech_zimbra){
			res = false;;
		}
		if(!res && domainObj.attrs[ZaDomain.A_zimbraAuthFallbackToLocal] == "TRUE")
			res = true;
	}
	return res;
}

ZaAccountXFormView.isAuthfromInternalSync =
function(domainName, attrName) {

        var acctName = null;
        if(attrName) {
                var instance = this.getInstance();
                if(instance)
                        acctName = this.getInstanceValue(attrName);

        }
        if(!acctName) acctName = domainName;
        return ZaAccountXFormView.isAuthfromInternal(acctName);
}


ZaAccountXFormView.isEditFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache)) && this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache).length==1);
}

ZaAccountXFormView.isDeleteFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_fwdAddr_selection_cache)));
}

ZaAccountXFormView.isEditCalFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_calFwdAddr_selection_cache)) && this.getInstanceValue(ZaAccount.A2_calFwdAddr_selection_cache).length==1);
}

ZaAccountXFormView.isDeleteCalFwdAddrEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_calFwdAddr_selection_cache)));
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
					if(arr[k]==instance[ZaAccount.A2_fwdAddr_selection_cache][i]) {
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

ZaAccountXFormView.deleteCalFwdAddrButtonListener = function () {
	var instance = this.getInstance();	
	if(instance[ZaAccount.A2_calFwdAddr_selection_cache] != null) {
		var cnt = instance[ZaAccount.A2_calFwdAddr_selection_cache].length;
		if(cnt && instance.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]) {
			var arr = instance.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo];
			for(var i=0;i<cnt;i++) {
				var cnt2 = arr.length-1;				
				for(var k=cnt2;k>=0;k--) {
					if(arr[k]==instance[ZaAccount.A2_calFwdAddr_selection_cache][i]) {
						arr.splice(k,1);
						break;	
					}
				}
			}
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, arr);
			this.getModel().setInstanceValue(instance, ZaAccount.A2_calFwdAddr_selection_cache, []);	
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

ZaAccountXFormView.calFwdAddrSelectionListener = 
function (ev) {
	var arr = this.widget.getSelection();	
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_calFwdAddr_selection_cache, arr);	
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_calFwdAddr_selection_cache, []);
	}	
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaAccountXFormView.editCalFwdAddrButtonListener.call(this);
	}	
}

ZaAccountXFormView.editFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	if(instance[ZaAccount.A2_fwdAddr_selection_cache] && instance[ZaAccount.A2_fwdAddr_selection_cache][0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editFwdAddrDlg) {
			formPage.editFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(),"400px", "150px",ZaMsg.Edit_FwdAddr_Title);
			formPage.editFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateFwdAddr, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance[ZaAccount.A2_fwdAddr_selection_cache][0];
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

ZaAccountXFormView.editCalFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	if(instance[ZaAccount.A2_calFwdAddr_selection_cache] && instance[ZaAccount.A2_calFwdAddr_selection_cache][0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editCalFwdAddrDlg) {
			formPage.editCalFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(),"400px", "150px",ZaMsg.Edit_FwdAddr_Title);
			formPage.editCalFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.updateCalFwdAddr, this.getForm(), null);						
		}
		var obj = {};
		obj[ZaAccount.A_name] = instance[ZaAccount.A2_calFwdAddr_selection_cache][0];
		var cnt = instance.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo].length;
		for(var i=0;i<cnt;i++) {
			if(instance[ZaAccount.A2_calFwdAddr_selection_cache][0]==instance.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo][i]) {
				obj[ZaAlias.A_index] = i;
				break;		
			}
		}
		
		formPage.editCalFwdAddrDlg.setObject(obj);
		formPage.editCalFwdAddrDlg.popup();		
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

ZaAccountXFormView.updateCalFwdAddr = function () {
	if(this.parent.editCalFwdAddrDlg) {
		this.parent.editCalFwdAddrDlg.popdown();
		var obj = this.parent.editCalFwdAddrDlg.getObject();
		var instance = this.getInstance();
		var arr = instance.attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo];
		if(obj[ZaAlias.A_index] >=0 && arr[obj[ZaAlias.A_index]] != obj[ZaAccount.A_name] ) {
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_calFwdAddr_selection_cache, []);
			arr[obj[ZaAlias.A_index]] = obj[ZaAccount.A_name];
			this.getModel().setInstanceValue(instance, ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, arr);
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

ZaAccountXFormView.addCalFwdAddrButtonListener =
function () {
	var instance = this.getInstance();
	var formPage = this.getForm().parent;
	if(!formPage.addCalFwdAddrDlg) {
		formPage.addCalFwdAddrDlg = new ZaEditFwdAddrXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "400px", "150px",ZaMsg.Add_FwdAddr_Title);
		formPage.addCalFwdAddrDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountXFormView.addCalFwdAddr, this.getForm(), null);						
	}
	
	var obj = {};
	obj[ZaAccount.A_name] = "";
	obj[ZaAlias.A_index] = - 1;
	formPage.addCalFwdAddrDlg.setObject(obj);
	formPage.addCalFwdAddrDlg.popup();		
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

ZaAccountXFormView.addCalFwdAddr  = function () {
	if(this.parent.addCalFwdAddrDlg) {
		this.parent.addCalFwdAddrDlg.popdown();
		var obj = this.parent.addCalFwdAddrDlg.getObject();
		if(obj[ZaAccount.A_name] && obj[ZaAccount.A_name].length>1) {
			var arr = this.getInstance().attrs[ZaAccount.A_zimbraPrefCalendarForwardInvitesTo];
			arr.push(obj[ZaAccount.A_name]);
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, arr);
			this.getModel().setInstanceValue(this.getInstance(), ZaAccount.A2_calFwdAddr_selection_cache, []);
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

ZaAccountXFormView.isAccountsTypeAlertInvisible = function () {
        var val = this.getInstanceValue(ZaAccount.A2_showAccountTypeMsg);
        return (AjxUtil.isEmpty(val));
}

ZaAccountXFormView.isAccountTypeSet = function () {
	 return !ZaAccount.isAccountTypeSet(this.getInstance());
}

ZaAccountXFormView.CONTACT_TAB_ATTRS = [ZaAccount.A_telephoneNumber,
        ZaAccount.A_homePhone,
        ZaAccount.A_mobile,
        ZaAccount.A_pager ,
		ZaAccount.A_company,
        ZaAccount.A_title,
        ZaAccount.A_facsimileTelephoneNumber,
		ZaAccount.A_street, 
		ZaAccount.A_city, 
		ZaAccount.A_state,
		ZaAccount.A_zip,
		ZaAccount.A_country];
ZaAccountXFormView.CONTACT_TAB_RIGHTS = [];

ZaAccountXFormView.ACCOUNT_NAME_GROUP_ATTRS = [ZaAccount.A_name,
        ZaAccount.A_firstName,
        ZaAccount.A_initials,
        ZaAccount.A_lastName,
        ZaAccount.A_displayname,
	//ZaAccount.A_zimbraMailCanonicalAddress,
	ZaAccount.A_zimbraHideInGal 
];

ZaAccountXFormView.MEMBEROF_TAB_ATTRS = [];		
ZaAccountXFormView.MEMBEROF_TAB_RIGHTS = [ZaAccount.GET_ACCOUNT_MEMBERSHIP_RIGHT];

ZaAccountXFormView.FEATURE_TAB_ATTRS = [ZaAccount.A_zimbraFeatureManageZimlets,
	ZaAccount.A_zimbraFeatureReadReceiptsEnabled,
	ZaAccount.A_zimbraFeatureMailEnabled,
	ZaAccount.A_zimbraFeatureContactsEnabled,
	ZaAccount.A_zimbraFeatureCalendarEnabled,
	ZaAccount.A_zimbraFeatureTasksEnabled,
	//ZaAccount.A_zimbraFeatureNotebookEnabled,
	ZaAccount.A_zimbraFeatureBriefcasesEnabled,
	//ZaAccount.A_zimbraFeatureBriefcaseDocsEnabled,
	//ZaAccount.A_zimbraFeatureIMEnabled,
	ZaAccount.A_zimbraFeatureOptionsEnabled,
	ZaAccount.A_zimbraFeatureTaggingEnabled,
	ZaAccount.A_zimbraFeatureSharingEnabled,
	ZaAccount.A_zimbraFeatureChangePasswordEnabled,
	ZaAccount.A_zimbraFeatureSkinChangeEnabled,
	ZaAccount.A_zimbraFeatureHtmlComposeEnabled,
	//ZaAccount.A_zimbraFeatureShortcutAliasesEnabled,
	ZaAccount.A_zimbraFeatureGalEnabled,
	ZaAccount.A_zimbraFeatureMAPIConnectorEnabled,
	ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled,
	ZaAccount.A_zimbraFeatureMailPriorityEnabled,
	ZaAccount.A_zimbraFeatureFlaggingEnabled,
	ZaAccount.A_zimbraImapEnabled,
	ZaAccount.A_zimbraPop3Enabled,
	ZaAccount.A_zimbraFeatureImapDataSourceEnabled,
	ZaAccount.A_zimbraFeaturePop3DataSourceEnabled,
	ZaAccount.A_zimbraFeatureMailSendLaterEnabled,
	//ZaAccount.A_zimbraFeatureFreeBusyViewEnabled,
	ZaAccount.A_zimbraFeatureConversationsEnabled,
	ZaAccount.A_zimbraFeatureFiltersEnabled,
	ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled,
	ZaAccount.A_zimbraFeatureNewMailNotificationEnabled,
	ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled,
	ZaAccount.A_zimbraFeatureIdentitiesEnabled,
	ZaAccount.A_zimbraFeatureGroupCalendarEnabled,
	//ZaAccount.A_zimbraFeatureInstantNotify,
  ZaAccount.A_zimbraFeaturePeopleSearchEnabled,
  ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,
	ZaAccount.A_zimbraFeatureSavedSearchesEnabled,
	ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled,
	ZaAccount.A_zimbraFeatureImportFolderEnabled,
    ZaAccount.A_zimbraFeatureExportFolderEnabled,
	ZaAccount.A_zimbraDumpsterEnabled,
	ZaAccount.A_zimbraFeatureSMIMEEnabled,
	ZaAccount.A_zimbraFeatureManageSMIMECertificateEnabled,
    ZaAccount.A_zimbraFeatureCalendarReminderDeviceEmailEnabled
];

ZaAccountXFormView.FEATURE_TAB_RIGHTS = [];
ZaAccountXFormView.PREFERENCES_TAB_ATTRS = [
	ZaAccount.A_zimbraPrefReadReceiptsToAddress,
	ZaAccount.A_zimbraPrefMailSendReadReceipts,
	ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar,
	ZaAccount.A_zimbraPrefCalendarUseQuickAdd,
	ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal,
	ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
	ZaAccount.A_zimbraPrefTimeZoneId,
	ZaAccount.A_zimbraPrefGalAutoCompleteEnabled,
	ZaAccount.A_zimbraPrefAutoAddAddressEnabled,
	ZaAccount.A_zimbraPrefMailSignature,
	ZaAccount.A_zimbraMailSignatureMaxLength,
	//ZaAccount.A_zimbraPrefMailSignatureStyle,
	ZaAccount.A_zimbraPrefMailSignatureEnabled,
	ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat,
	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor,
	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily,
	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize,
	ZaAccount.A_zimbraPrefComposeFormat,
	ZaAccount.A_zimbraPrefComposeInNewWindow,
	ZaAccount.A_zimbraAllowFromAddress,
	ZaAccount.A_zimbraAllowAnyFromAddress,
	ZaAccount.A_zimbraPrefSaveToSent,
	ZaAccount.A_zimbraPrefOutOfOfficeReply,
	ZaAccount.A_zimbraPrefNewMailNotificationAddress,
	ZaAccount.A_zimbraPrefNewMailNotificationEnabled,
	ZaAccount.A_zimbraMailMinPollingInterval,
	ZaAccount.A_zimbraPrefMailPollingInterval,
	ZaAccount.A_zimbraPrefAutoSaveDraftInterval,
    ZaAccount.A_zimbraPrefMailSoundsEnabled,
    ZaAccount.A_zimbraPrefMailFlashIcon,
    ZaAccount.A_zimbraPrefMailFlashTitle,
	ZaAccount.A_zimbraPrefMailDefaultCharset,
	ZaAccount.A_zimbraMaxMailItemsPerPage,
	ZaAccount.A_zimbraPrefMailItemsPerPage,
	ZaAccount.A_zimbraPrefGroupMailBy,
	ZaAccount.A_zimbraPrefDisplayExternalImages,
	ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,
	ZaAccount.A_zimbraPrefLocale,
	ZaAccount.A_zimbraJunkMessagesIndexingEnabled,
	ZaAccount.A_zimbraPrefShowSelectionCheckbox,
	ZaAccount.A_zimbraPrefWarnOnExit,
    ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit,    
	ZaAccount.A_zimbraPrefUseKeyboardShortcuts,
	ZaAccount.A_zimbraPrefImapSearchFoldersEnabled,
	ZaAccount.A_zimbraPrefShowSearchString,
	ZaAccount.A_zimbraPrefMailInitialSearch,
	ZaAccount.A_zimbraPrefClientType,
	ZaAccount.A_zimbraPrefCalendarInitialView,
	ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek,
	ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges,
	ZaAccount.A_zimbraPrefCalendarApptVisibility,
	ZaAccount.A_zimbraPrefCalendarAutoAddInvites,
	ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled,
	ZaAccount.A_zimbraPrefCalendarReminderFlashTitle,
	ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite,
	ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite,
	ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf,
	ZaAccount.A_zimbraPrefCalendarToasterEnabled,
	ZaAccount.A_zimbraPrefCalendarShowPastDueReminders,
	ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled,
	ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled
];
ZaAccountXFormView.PREFERENCES_TAB_RIGHTS = [];	

ZaAccountXFormView.ALIASES_TAB_ATTRS = [ZaAccount.A_zimbraMailAlias];
ZaAccountXFormView.ALIASES_TAB_RIGHTS = [ZaAccount.ADD_ACCOUNT_ALIAS_RIGHT, ZaAccount.REMOVE_ACCOUNT_ALIAS_RIGHT];

ZaAccountXFormView.FORWARDING_TAB_ATTRS = [ZaAccount.A_zimbraFeatureMailForwardingEnabled,
	ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled,
	ZaAccount.A_zimbraMailForwardingAddress,
	ZaAccount.A_zimbraPrefCalendarForwardInvitesTo];
ZaAccountXFormView.FORWARDING_TAB_RIGHTS = [];

ZaAccountXFormView.INTEROP_TAB_ATTRS = [ZaAccount.A_zimbraForeignPrincipal];
ZaAccountXFormView.INTEROP_TAB_RIGHTS = [];

ZaAccountXFormView.SKIN_TAB_ATTRS = [ZaAccount.A_zimbraPrefSkin,ZaAccount.A_zimbraAvailableSkin];
ZaAccountXFormView.SKIN_TAB_RIGHTS = [];

ZaAccountXFormView.ZIMLET_TAB_ATTRS = [ZaAccount.A_zimbraZimletAvailableZimlets];
ZaAccountXFormView.ZIMLET_TAB_RIGHTS = [];

ZaAccountXFormView.ADVANCED_TAB_ATTRS = [ZaAccount.A_zimbraAttachmentsBlocked,
	ZaAccount.A_zimbraMailQuota,
	ZaAccount.A_zimbraContactMaxNumEntries,
	ZaAccount.A_zimbraQuotaWarnPercent,
	ZaAccount.A_zimbraQuotaWarnInterval,
	ZaAccount.A_zimbraQuotaWarnMessage,
	ZaAccount.A_zimbraPasswordLocked,
	ZaAccount.A_zimbraMinPwdLength,
	ZaAccount.A_zimbraMaxPwdLength,
	ZaAccount.A_zimbraPasswordMinUpperCaseChars,
	ZaAccount.A_zimbraPasswordMinLowerCaseChars,
	ZaAccount.A_zimbraPasswordMinPunctuationChars,
	ZaAccount.A_zimbraPasswordMinNumericChars,
	ZaAccount.A_zimbraPasswordMinDigitsOrPuncs,
	ZaAccount.A_zimbraMinPwdAge,
	ZaAccount.A_zimbraMaxPwdAge,
	ZaAccount.A_zimbraEnforcePwdHistory,
	ZaAccount.A_zimbraPasswordLockoutEnabled,
	ZaAccount.A_zimbraPasswordLockoutMaxFailures,
	ZaAccount.A_zimbraPasswordLockoutDuration,
	ZaAccount.A_zimbraPasswordLockoutFailureLifetime,
	ZaAccount.A_zimbraAdminAuthTokenLifetime,
	ZaAccount.A_zimbraAuthTokenLifetime,
	ZaAccount.A_zimbraMailIdleSessionTimeout,
	ZaAccount.A_zimbraMailMessageLifetime,
	ZaAccount.A_zimbraMailTrashLifetime,
	ZaAccount.A_zimbraMailSpamLifetime,
	ZaAccount.A_zimbraFreebusyExchangeUserOrg,
	ZaAccount.A_zimbraMailTransport	
	];
ZaAccountXFormView.ADVANCED_TAB_RIGHTS = [];

ZaAccountXFormView.addressItemsPool = null;
ZaAccountXFormView.addressItemsPoolForDialog = null;
ZaAccountXFormView.getAddressFormItem = function(){
	// the subItems of Address Items only init once;
	if(AjxUtil.isEmpty(ZaAccountXFormView.addressItemsPool)){
		ZaAccountXFormView.addressItemsPool = new Object();
		ZaAccountXFormView.addressItemsPool[ZaAccount.A_zip] =  {ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip,
            labelLocation:_LEFT_, width:100};
		ZaAccountXFormView.addressItemsPool[ZaAccount.A_state] = {ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPool[ZaAccount.A_street] = {ref:ZaAccount.A_street, type:_TEXTAREA_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPool[ZaAccount.A_city] = {ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPool[ZaAccount.A_country] = {ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country,
            labelLocation:_LEFT_, width:250};
		
	}
	var addressFormItems = new Array();
	var addressFormItemsOrders = new Array();
	if(ZaZimbraAdmin.isLanguage("ja")){
		addressFormItemsOrders = [ZaAccount.A_zip, ZaAccount.A_state, ZaAccount.A_city, ZaAccount.A_street, ZaAccount.A_country];
	}
	else{
		addressFormItemsOrders = [ZaAccount.A_street, ZaAccount.A_city, ZaAccount.A_state, ZaAccount.A_zip, ZaAccount.A_country]; 
	}
	
	for(var i = 0; i < addressFormItemsOrders.length; i++){
		addressFormItems.push(ZaAccountXFormView.addressItemsPool[addressFormItemsOrders[i]]);
	}
	return addressFormItems;
}
ZaAccountXFormView.getAddressFormItemForDialog = function(){
	// the subItems of Address Items only init once;
	if(AjxUtil.isEmpty(ZaAccountXFormView.addressItemsPoolForDialog)){
		ZaAccountXFormView.addressItemsPoolForDialog = new Object();
		ZaAccountXFormView.addressItemsPoolForDialog[ZaAccount.A_zip] =  {ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip,
            labelLocation:_LEFT_, width:100};
		ZaAccountXFormView.addressItemsPoolForDialog[ZaAccount.A_state] = {ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPoolForDialog[ZaAccount.A_street] = {ref:ZaAccount.A_street, type:_TEXTAREA_, msgName:ZaMsg.NAD_street,label:ZaMsg.NAD_street,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPoolForDialog[ZaAccount.A_city] = {ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city,
            labelLocation:_LEFT_, width:250};
		ZaAccountXFormView.addressItemsPoolForDialog[ZaAccount.A_country] = {ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country,
            labelLocation:_LEFT_, width:250};

	}
	var addressFormItems = new Array();
	var addressFormItemsOrders = new Array();
	if(ZaZimbraAdmin.isLanguage("ja")){
		addressFormItemsOrders = [ZaAccount.A_zip, ZaAccount.A_state, ZaAccount.A_city, ZaAccount.A_street, ZaAccount.A_country];
	}
	else{
		addressFormItemsOrders = [ZaAccount.A_street, ZaAccount.A_city, ZaAccount.A_state, ZaAccount.A_zip, ZaAccount.A_country];
	}

	for(var i = 0; i < addressFormItemsOrders.length; i++){
		addressFormItems.push(ZaAccountXFormView.addressItemsPoolForDialog[addressFormItemsOrders[i]]);
	}
	return addressFormItems;
}

ZaAccountXFormView.accountNameInfoPool = null;
ZaAccountXFormView.getAccountNameInfoItem = function(){
	if(AjxUtil.isEmpty(ZaAccountXFormView.accountNameInfoPool)){
		ZaAccountXFormView.accountNameInfoPool = new Object();
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_name] = {ref:ZaAccount.A_name, type:_EMAILADDR_,
					 msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName, bmolsnr:false,
                                        labelLocation:_LEFT_,onChange:ZaAccount.setDomainChanged,forceUpdate:true,
                                        enableDisableChecks:[[ZaItem.hasRight,ZaAccount.RENAME_ACCOUNT_RIGHT]],
                                        visibilityChecks:[]
            ,domainPartWidth:"100%", domainContainerWidth: "100%"
                                },
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_firstName] = {ref:ZaAccount.A_firstName, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName, 
					labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_initials] = {ref:ZaAccount.A_initials, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_, 
					cssClass:"admin_xform_name_input", width:50,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(), this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),elementValue);
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_lastName] = {ref:ZaAccount.A_lastName, type:_TEXTFIELD_, 
					msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_, 
					cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(),  this.getInstanceValue(ZaAccount.A_firstName), elementValue ,this.getInstanceValue(ZaAccount.A_initials));
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaAccountXFormView.accountNameInfoPool["ZaAccountDisplayInfoGroup"] = {type:_GROUP_, numCols:3, nowrap:true,
                    attributeName: ZaAccount.A_displayname,
					width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName, labelLocation:_LEFT_,
                                        visibilityChecks:[[ZaItem.hasReadPermission,ZaAccount.A_displayname]],
                                        items: [
                                                {ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,     cssClass:"admin_xform_name_input", width:150,
                                                        enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autodisplayname,"FALSE"],ZaItem.hasWritePermission],
                                                        enableDisableChangeEventSources:[ZaAccount.A2_autodisplayname],bmolsnr:true,
                                                        visibilityChecks:[]
                                                },
                                                {ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE", subLabel:"", helpTooltip: false,
                                                        elementChanged: function(elementValue,instanceValue, event) {
                                                                if(elementValue=="TRUE") {
                                                                        if(ZaAccount.generateDisplayName.call(this, this.getInstance(), this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),this.getInstanceValue(ZaAccount.A_initials))) {
                                                                                this.getForm().parent.setDirty(true);
                                                                        }
                                                                }
                                                                this.getForm().itemChanged(this, elementValue, event);
                                                        },
                                                        enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_displayname]],
                            visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_displayname]]

                                                }
                                        ]
                                },
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_zimbraHideInGal]={ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_,
				  			msgName:ZaMsg.LBL_zimbraHideInGal,
				  			label:ZaMsg.LBL_zimbraHideInGal, trueValue:"TRUE", falseValue:"FALSE"
				},
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_zimbraPhoneticFirstName] = {
					ref:ZaAccount.A_zimbraPhoneticFirstName, type:_TEXTFIELD_, 
					msgName:ZaMsg.NAD_zimbraPhoneticFirstName,label:ZaMsg.NAD_zimbraPhoneticFirstName,
                                        labelLocation:_LEFT_, cssClass:"admin_xform_name_input",width:150
                                };
		ZaAccountXFormView.accountNameInfoPool[ZaAccount.A_zimbraPhoneticLastName] = {
                                        ref:ZaAccount.A_zimbraPhoneticLastName, type:_TEXTFIELD_,
                                        msgName:ZaMsg.NAD_zimbraPhoneticLastName,label:ZaMsg.NAD_zimbraPhoneticLastName,
                                        labelLocation:_LEFT_, cssClass:"admin_xform_name_input",width:150
                                };

	}

	var accountNameFormItems = new Array();
        var accountNameItemsOrders = new Array();
        if(ZaZimbraAdmin.isLanguage("ja")){
		accountNameItemsOrders = [ZaAccount.A_name, ZaAccount.A_zimbraPhoneticLastName, ZaAccount.A_lastName, ZaAccount.A_initials, ZaAccount.A_zimbraPhoneticFirstName, ZaAccount.A_firstName, "ZaAccountDisplayInfoGroup", ZaAccount.A_zimbraHideInGal];
        }
        else{
		accountNameItemsOrders = [ZaAccount.A_name, ZaAccount.A_firstName, ZaAccount.A_initials, ZaAccount.A_lastName,"ZaAccountDisplayInfoGroup", ZaAccount.A_zimbraHideInGal];
        }

        for(var i = 0; i < accountNameItemsOrders.length; i++){
                accountNameFormItems.push(ZaAccountXFormView.accountNameInfoPool[accountNameItemsOrders[i]]);
        }
        return accountNameFormItems;
}

/**
* This method is added to the map {@link ZaTabView#XFormModifiers}
* @param xFormObject {Object} a definition of the form. This method adds/removes/modifies xFormObject to construct
* an Account view. 
**/
ZaAccountXFormView.myXFormModifier = function(xFormObject, entry) {
	
	var domainName;
	try {
		domainName = ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		if(!domainName && ZaApp.getInstance().getDomainList().size() > 0)
			domainName = ZaApp.getInstance().getDomainList().getArray()[0].name;
	} catch (ex) { 
		domainName = ZaSettings.myDomainName;
		if(ex.code != ZmCsfeException.SVC_PERM_DENIED) {
			throw(ex);
		}		
	}
		
	var emptyAlias = " @" + domainName;
	var headerItems = [{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:3},
        {type:_OUTPUT_, ref:ZaAccount.A_displayname, label:null,cssClass:"AdminTitle", height:"auto", width:350, rowSpan:3, cssStyle:"word-wrap:break-word;overflow:hidden",
        visibilityChecks:[ZaItem.hasReadPermission],
            getDisplayValue:function(newValue) {
                return AjxStringUtil.htmlEncode(newValue);
            }
        }];
	/*headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_COSId,valueChangeEventSources:[ZaAccount.A_COSId], labelLocation:_LEFT_, label:ZaMsg.NAD_ClassOfService, choices:this.cosChoices,getDisplayValue:function(newValue) {
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
			visibilityChecks:[ZaItem.hasReadPermission]	
	});*/

    if (!entry.isExternal && ZaItem.hasReadPermission(ZaAccount.A_mailHost, entry)) {
            headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_mailHost, labelLocation:_LEFT_,label:ZabMsg.attrDesc_mailHost});
    } else if(entry.isExternal && ZaItem.hasReadPermission(ZaAccount.A_zimbraMailTransport, entry)) {
        headerItems.push({type:_OUTPUT_, ref:ZaAccount.A_zimbraMailTransport, labelLocation:_LEFT_,label:ZabMsg.attrDesc_mailHost});
    }

    if (ZaItem.hasReadPermission(ZaAccount.A_accountStatus, entry)) {
	    headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_accountStatus, label:ZaMsg.NAD_AccountStatus, labelLocation:_LEFT_, choices:this.accountStatusChoices});
    }

    if (ZaItem.hasReadPermission(ZaAccount.A_name, entry)) {
    	headerItems.push({type:_OUTPUT_,ref:ZaAccount.A_name, label:ZaMsg.NAD_Email, labelLocation:_LEFT_, required:false, cssStyle:"word-wrap:break-word;overflow:hidden"});
    }

    if (ZaItem.hasReadPermission(ZaItem.A_zimbraId, entry)) {
        headerItems.push({type:_OUTPUT_,ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID});
    }

    if(ZaItem.hasReadPermission(ZaAccount.A_zimbraMailQuota,entry) && ZaItem.hasRight(ZaAccount.GET_MAILBOX_INFO_RIGHT,entry)) {
			headerItems.push(
				{type:_OUTPUT_,ref:ZaAccount.A2_mbxsize, 
					label:ZaMsg.LBL_quota,
					getDisplayValue:function() {
						var usedVal = this.getInstanceValue();
						var formatter = AjxNumberFormat.getNumberInstance();
						if(!usedVal)
							usedVal = "0";
						else {
							usedVal = Number(usedVal / 1048576).toFixed(3);
							usedVal = formatter.format(usedVal);
						}

						var quotaLimit = this.getInstanceValue(ZaAccount.A_zimbraMailQuota);
						if(!quotaLimit || quotaLimit == "0") {
							quotaLimit = ZaMsg.Unlimited;
						} else {
							quotaLimit = formatter.format(quotaLimit);
						}

						if(quotaLimit == ZaMsg.Unlimited) {
							return AjxMessageFormat.format (ZaMsg.unlimitedQuotaValueTemplate,[usedVal,quotaLimit]);
						} else {
							return AjxMessageFormat.format (ZaMsg.quotaValueTemplate,[usedVal,quotaLimit]);
						}
					},
					valueChangeEventSources:[ZaAccount.A_zimbraMailQuota,ZaAccount.A2_mbxsize]
				});
	} else if(ZaItem.hasReadPermission(ZaAccount.A_zimbraMailQuota,entry)) {
		    //assigned quota
		headerItems.push ({type:_OUTPUT_,ref:ZaAccount.A_zimbraMailQuota, label:ZaMsg.LBL_assignedQuota,
        	getDisplayValue:function() {
				var val = this.getInstanceValue();
				if(!val || val == "0")
					val = ZaMsg.Unlimited;
					
				if(val == ZaMsg.Unlimited) {
                	return AjxMessageFormat.format (ZaMsg.unlimitedAssignedQuotaValueTemplate,[val]);
				} else {
					return AjxMessageFormat.format (ZaMsg.assignedQuotaTemplate,[val]);
				}
			},
			bmolsnr:true
		});
	} else if(ZaItem.hasRight(ZaAccount.GET_MAILBOX_INFO_RIGHT,entry)) {
		headerItems.push({type:_OUTPUT_,ref:ZaAccount.A2_mbxsize, label:ZaMsg.LBL_usedQuota,
			getDisplayValue:function() {
				var val = this.getInstanceValue();
				if(!val) 
					val = "0";
				else {
					val = Number(val / 1048576).toFixed(3);
				}									
				return AjxMessageFormat.format (ZaMsg.usedQuotaTemplate,[val]);
			},
			bmolsnr:true
		});		
	}

	if (ZaItem.hasReadPermission(ZaAccount.A_zimbraLastLogonTimestamp, entry))	{			
	    headerItems.push(
            {type:_OUTPUT_, ref:ZaAccount.A_zimbraLastLogonTimestamp,
                label:ZaMsg.LBL_Last_Login, labelLocation:_LEFT_,
                getDisplayValue:function() {
                    var val = this.getInstanceValue();
                    return ZaAccount.getLastLoginTime(val) ;
                }
             });
    }

    if (ZaItem.hasReadPermission(ZaItem.A_zimbraCreateTimestamp, entry))	{
	    headerItems.push(
                    {
                        type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp,
						label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
						getDisplayValue:function() {
							var val = ZaItem.formatServerTime(this.getInstanceValue());
							if(!val)
								return ZaMsg.Server_Time_NA;
							else
								return val;
						}	
					 });
    }
					 
    this.tabChoices = new Array();
	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6, _tab7, _tab8, _tab9, _tab10, _tab11;
	
	_tab1 = ++this.TAB_INDEX;
	this.tabChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.CONTACT_TAB_ATTRS, ZaAccountXFormView.CONTACT_TAB_RIGHTS)) {
		_tab2 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab2, label:ZaMsg.TABT_ContactInfo});	
	}
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.MEMBEROF_TAB_ATTRS, ZaAccountXFormView.MEMBEROF_TAB_RIGHTS)) {
		_tab3 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});	
	}

	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.FEATURE_TAB_ATTRS, ZaAccountXFormView.FEATURE_TAB_RIGHTS)) {
		_tab4 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab4, label:ZaMsg.TABT_Features});	
	}
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.PREFERENCES_TAB_ATTRS, ZaAccountXFormView.PREFERENCES_TAB_RIGHTS)) {
		_tab5 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab5, label:ZaMsg.TABT_Preferences});	
	}
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ALIASES_TAB_ATTRS, ZaAccountXFormView.ALIASES_TAB_RIGHTS)) {
		_tab6 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab6, label:ZaMsg.TABT_Aliases});	
	}
			
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.FORWARDING_TAB_ATTRS, ZaAccountXFormView.FORWARDING_TAB_RIGHTS)) {
		_tab7 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab7, label:ZaMsg.TABT_Forwarding});	
	}
				
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.INTEROP_TAB_ATTRS, ZaAccountXFormView.INTEROP_TAB_RIGHTS)) {
		_tab8 = ++this.TAB_INDEX;
		this.tabChoices.push({value: _tab8, label: ZaMsg.TABT_Interop}) ;	
	}
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.SKIN_TAB_ATTRS, ZaAccountXFormView.SKIN_TAB_RIGHTS)) {
		_tab9 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab9, label:ZaMsg.TABT_Themes});	
	}
	 			
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ZIMLET_TAB_ATTRS, ZaAccountXFormView.ZIMLET_TAB_RIGHTS)) {
		_tab10 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab10, label:ZaMsg.TABT_Zimlets});	
	}
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ADVANCED_TAB_ATTRS, ZaAccountXFormView.ADVANCED_TAB_RIGHTS)) {
		_tab11 = ++this.TAB_INDEX;
		this.tabChoices.push({value:_tab11, label:ZaMsg.TABT_Advanced});	
	}

	var cases = [];

		var case1 = {type:_ZATABCASE_,caseKey:_tab1,
            paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
			numCols:1};
		
		var case1Items = [
			 {type: _DWT_ALERT_, ref: ZaAccount.A2_domainLeftAccounts,
			 	visibilityChecks:[ZaAccountXFormView.isDomainLeftAccountsAlertVisible],
			 	visibilityChangeEventSources:[ZaAccount.A2_domainLeftAccounts,ZaAccount.A_name, ZaAccount.A2_accountTypes],
				containerCssStyle: "width:400px;",
				bmolsnr:true,
				style: DwtAlert.WARNING, iconVisible: false
			 },
	
	        //account types group
	        {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountTypeGrouper, id:"account_type_group",
	                colSpan: "*", numCols: 1, colSizes: ["100%"],
	                visibilityChecks:[ZaAccountXFormView.isAccountTypeGrouperVisible, ZaAccount.isShowAccountType],
	                visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId, ZaAccount.A_name, ZaAccount.A2_showAccountTypeMsg],
	                items: [
	                    {type: _DWT_ALERT_,
	                    	visibilityChecks:[ZaAccountXFormView.isAccountTypeSet, ZaAccountXFormView.isAccountsTypeAlertInvisible],
	                    	visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId, ZaAccount.A_name,ZaAccount.A2_showAccountTypeMsg],
	                    	containerCssStyle: "width:400px;",
	                        style: DwtAlert.CRITICAL, iconVisible: false ,
	                        content: ZaMsg.ERROR_ACCOUNT_TYPE_NOT_SET
	                    },
                    	    {type: _DWT_ALERT_, ref: ZaAccount.A2_showAccountTypeMsg,
                        	visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaAccount.A2_showAccountTypeMsg]],
                        	visibilityChangeEventSources:[ZaAccount.A2_showAccountTypeMsg, ZaAccount.A_name],
                        	bmolsnr:true,
                        	containerCssStyle: "width:400px;",
                        	style: DwtAlert.WARNING, iconVisible: false
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
				visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			ZaAccountXFormView.ACCOUNT_NAME_GROUP_ATTRS]],				
				items:ZaAccountXFormView.getAccountNameInfoItem()

			}
		];
	
		var setupGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_AccountSetupGrouper, id:"account_form_setup_group",
			colSizes:["275px","*"],numCols:2,
			items: [
				{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,
					label:ZaMsg.NAD_AccountStatus, bmolsnr:true,
					labelLocation:_LEFT_, choices:this.accountStatusChoices
				}
			],
			visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_accountStatus,
                        			ZaAccount.A_COSId,
                        			ZaAccount.A_zimbraIsAdminAccount]]]			
		}
			

		setupGroup.items.push(
			{type:_GROUP_, numCols:3,colSizes:["156px","22px","100px"], nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
				visibilityChecks:[[ZaItem.hasReadPermission,ZaAccount.A_COSId]], attributeName: ZaAccount.A_COSId,
				id: ZaAccountXFormView.cosGroupItemId,
				items: [
					{ref:ZaAccount.A_COSId, type:_DYNSELECT_,label: null, choices:this.cosChoices,
						//inputPreProcessor:ZaAccountXFormView.preProcessCOS,
						enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autoCos,"FALSE"],
                            [ZaItem.hasWritePermission,ZaAccount.A_COSId]],
						enableDisableChangeEventSources:[ZaAccount.A2_autoCos],
						visibilityChecks:[],
						bmolsnr:false,
						width:"auto",
						toolTipContent:ZaMsg.tt_StartTypingCOSName,
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
						visibilityChecks:[], subLabel:"",
                        enableDisableChecks:[ [ZaItem.hasWritePermission,ZaAccount.A_COSId]],
						msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
						trueValue:"TRUE", falseValue:"FALSE" , helpTooltip: false,
						elementChanged: function(elementValue,instanceValue, event) {
							this.getForm().parent.setDirty(true);
							if(elementValue=="TRUE") {
								var defaultCos = ZaCos.getDefaultCos4Account(this.getInstance()[ZaAccount.A_name]);
								if(defaultCos && defaultCos.id) {
									this.getInstance()._defaultValues = defaultCos;
									this.getModel().setInstanceValue(this.getInstance(),ZaAccount.A_COSId,defaultCos.id);
									//instance.attrs[ZaAccount.A_COSId] = defaultCos.id;	
								}									
								//ZaAccount.setDefaultCos(this.getInstance());	
							}
							this.getForm().itemChanged(this, elementValue, event);
						}
					}
				]
			});

	
	setupGroup.items.push({ref:ZaAccount.A_zimbraIsAdminAccount, type:_CHECKBOX_,
            msgName:ZaMsg.NAD_IsSystemAdminAccount,label:ZaMsg.NAD_IsSystemAdminAccount,
			bmolsnr:true, trueValue:"TRUE", falseValue:"FALSE",
			visibilityChecks:[[XForm.checkInstanceValueNot,ZaAccount.A_zimbraIsExternalVirtualAccount,"TRUE"],[ZaItem.hasReadPermission,ZaAccount.A_zimbraIsAdminAccount]],
			visibilityChangeEventSources:[ZaAccount.A_zimbraIsExternalVirtualAccount,ZaAccount.A_zimbraIsAdminAccount]
	});
	case1Items.push(setupGroup);
	
	var passwordGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_form_password_group",
		visibilityChecks:[[ZaItem.hasRight,ZaAccount.SET_PASSWORD_RIGHT],
                          [XForm.checkInstanceValueNot,ZaAccount.A2_isExternalAuth,true]
            ],
        visibilityChangeEventSources:[ZaAccount.A2_isExternalAuth],
		colSizes:["275px","*"],numCols:2,
		items:[ 
                { type: _DWT_ALERT_, containerCssStyle: "padding-bottom:0px",
                      //style: DwtAlert.WARNING,iconVisible: (!ZaAccountXFormView.isAuthfromInternal(entry.name)),
                      //content: ((ZaAccountXFormView.isAuthfromInternal(entry.name))?ZaMsg.Alert_InternalPassword:ZaMsg.Alert_ExternalPassword)
                      style: DwtAlert.WARNING,iconVisible: false,
                      content: ZaMsg.Alert_InternalPassword

                },
		{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
			label:ZaMsg.NAD_Password, labelLocation:_LEFT_,
			visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]], 
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
			label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,
			visibilityChecks:[], enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]],
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,
			msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,
			trueValue:"TRUE", falseValue:"FALSE",
			visibilityChecks:[], enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
		},
		{ref:ZaAccount.A_zimbraAuthLdapExternalDn,type:_TEXTFIELD_,width:256,
                       	msgName:ZaMsg.NAD_AuthLdapExternalDn,label:ZaMsg.NAD_AuthLdapExternalDn, labelLocation:_LEFT_, 
			align:_LEFT_, toolTipContent: ZaMsg.tt_AuthLdapExternalDn
		}
		]
	};
	case1Items.push(passwordGroup);														

	var notesGroup = {type:_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_form_notes_group",
		colSizes:["275px","*"],numCols:2,
		visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, [ZaAccount.A_notes, ZaAccount.A_description]]],
	 	items:[
	        ZaItem.descriptionXFormItem,
			{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,
				label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:"30em"
			}
		]
	};

	case1Items.push(notesGroup);
	case1.items = case1Items;
	cases.push(case1);

	if(_tab2) {
		var case2={type:_ZATABCASE_, numCols:1, caseKey:_tab2,
            paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),  align:_CENTER_,
					items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.LBL_phone, id:"contact_form_phone_group",
							width:"100%", numCols:2,colSizes: ["275px","100%"],
							items:[
								{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber,
                                 labelLocation:_LEFT_, width:250} ,
                                {ref:ZaAccount.A_homePhone, type:_TEXTFIELD_, msgName:ZaMsg.NAD_homePhone,label:ZaMsg.NAD_homePhone,
                                 labelLocation:_LEFT_, width:250} ,
                                {ref:ZaAccount.A_mobile, type:_TEXTFIELD_, msgName:ZaMsg.NAD_mobile,label:ZaMsg.NAD_mobile,
                                 labelLocation:_LEFT_, width:250} ,
                                {ref:ZaAccount.A_pager, type:_TEXTFIELD_, msgName:ZaMsg.NAD_pager,label:ZaMsg.NAD_pager,
                                 labelLocation:_LEFT_, width:250},
                                 {ref:ZaAccount.A_facsimileTelephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_facsimileTelephoneNumber,
                                 label:ZaMsg.NAD_facsimileTelephoneNumber, labelLocation:_LEFT_, width:250}

							]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.LBL_company, id:"contact_form_company_group",
							width:"100%", numCols:2,colSizes: ["275px","100%"],
							items:[	
								{ref:ZaAccount.A_zimbraPhoneticCompany, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPhoneticCompany,
                                 label:ZaMsg.NAD_zimbraPhoneticCompany, labelLocation:_LEFT_, width:250, visibilityChecks:[[ZaZimbraAdmin.isLanguage, "ja"]]},
								{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_,
                                 width:250} ,
                                {ref:ZaAccount.A_title,  type:_TEXTFIELD_, msgName:ZaMsg.NAD_title,label:ZaMsg.NAD_title, labelLocation:_LEFT_,
                                 width:250}
							]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.LBL_address, id:"contact_form_address_group",
							width:"100%", numCols:2,colSizes: ["275px","100%"],
							items: ZaAccountXFormView.getAddressFormItem() 
						}							
					]
				};
		cases.push(case2);
	}
	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
	
	if(_tab3) {
		//MemberOf Tab
		var case3={type:_ZATABCASE_, numCols:2, caseKey:_tab3, colSizes: ["390px","390px"],
            paddingStyle:(appNewUI? "padding-left:15px;":null), cellpadding:(appNewUI?2:0),
					items: [
						{type:_SPACER_, height:"10"},
						//layout rapper around the direct/indrect list						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
								//direct member group
								{type:_ZALEFT_GROUPER_, numCols:1, width: "100%", 
									label:ZaMsg.Account_DirectGroupLabel,
									containerCssStyle: "padding-top:5px",
									items:[
										{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "98%", height: 200,
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
												{type:_CELLSPACER_, height:"100%"},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAddRemoveButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberListSelected],
											      	onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)"
											    },
												{type:_CELLSPACER_,height:"100%"},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberList +"_offset"]
											    },								       
												{type:_CELLSPACER_, height:"100%"},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_more"]
											    },								       
												{type:_CELLSPACER_, height:"100%"}									
											]
										}		
									]
								},	
								{type:_SPACER_, height:"10"},	
								//indirect member group
								{type:_ZALEFT_GROUPER_, numCols:1,  width: "100%", label:ZaMsg.Account_IndirectGroupLabel,
									containerCssStyle: "padding-top:5px",
									items:[
										{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "98%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
                                            onSelection:ZaAccountXFormView.indirectMemberOfSelectionListener,
                                            forceUpdate: true }	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_CELLSPACER_, height:"100%"},
												{type:_CELLSPACER_, height:"100%"},
												{type:_CELLSPACER_, height:"100%"},
												{type:_CELLSPACER_, height:"100%"},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_offset"]
											    },								       
												{type:_CELLSPACER_, height:"100%"},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
											      	enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_more"]
											    },								       
												{type:_CELLSPACER_, height:"100%"}									
											]
										}
									]
								}
							]
						},

						//non member group
						{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_NonGroupLabel,
							containerCssStyle: "padding-top:5px",
							items:[
								{type:_GROUP_, numCols:5, colSizes:[55, "auto",10,80, 120,20], width:"100%", 
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
                                                subLabel:"",
												labelCssClass:"xform_label",
												labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",
												enableDisableChecks:[],
												visibilityChecks:[]
										}										
									]
						         },
						        {type:_SPACER_, height:"5"},
								
								{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "98%", height: 455,
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
									   	{type:_CELLSPACER_, height:"100%"},
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAllButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)"
										},
										{type:_CELLSPACER_, height:"100%"},
										{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
											enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_offset"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
										},								       
										{type:_CELLSPACER_, height:"100%"},
										{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
										 	enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_more"],
											enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList]],
											onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"									
										},								       
										{type:_CELLSPACER_, height:"100%"}	
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
	if(_tab4) {
		cases.push({type:_ZATABCASE_,id:"account_form_features_tab",  numCols:1, width:"100%", caseKey:_tab4,
        paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
				items: [
					{ type: _DWT_ALERT_,
					  containerCssStyle: "padding-top:20px;width:400px;",
					  style: DwtAlert.WARNING,
					  iconVisible: false, 
					  content: ZaMsg.NAD_CheckFeaturesInfo
					},				
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraMajorFeature, id:"account_form_features_major", colSizes:["auto"],numCols:1,
						visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
							[ZaAccount.A_zimbraFeatureMailEnabled, 
							 ZaAccount.A_zimbraFeatureContactsEnabled,
							 ZaAccount.A_zimbraFeatureCalendarEnabled,
							 ZaAccount.A_zimbraFeatureTasksEnabled,
							 ZaAccount.A_zimbraFeatureBriefcasesEnabled,
							 ZaAccount.A_zimbraFeatureOptionsEnabled
							 ]]
						],
						items:[	
							{ref:ZaAccount.A_zimbraFeatureMailEnabled,
								type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureMailEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"
							},							
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,
								type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureContactsEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureContactsEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureCalendarEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureCalendarEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureTasksEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureTaskEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureTaskEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},													
							//{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_CHECKBOX_,
							//	resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							//	msgName:ZaMsg.LBL_zimbraFeatureNotebookEnabled,
							//	checkBoxLabel:ZaMsg.LBL_zimbraFeatureNotebookEnabled,  
							//	trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureBriefcasesEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},								
							//{ref:ZaAccount.A_zimbraFeatureIMEnabled, type:_SUPER_CHECKBOX_, 
							//	resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							//	msgName:ZaMsg.LBL_zimbraFeatureIMEnavbled,
							//	checkBoxLabel:ZaMsg.LBL_zimbraFeatureIMEnabled,  
							//	trueValue:"TRUE", falseValue:"FALSE"},								
							{ref:ZaAccount.A_zimbraFeatureOptionsEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureOptionsEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureOptionsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"}
						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraGeneralFeature, id:"account_form_features_general", colSizes:["auto"],numCols:1,
						visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
							[ZaAccount.A_zimbraFeatureTaggingEnabled, 
							 ZaAccount.A_zimbraFeatureSharingEnabled,
							 ZaAccount.A_zimbraFeatureChangePasswordEnabled,
							 ZaAccount.A_zimbraFeatureSkinChangeEnabled,
							 ZaAccount.A_zimbraFeatureManageZimlets,
							 ZaAccount.A_zimbraFeatureHtmlComposeEnabled,
							 ZaAccount.A_zimbraFeatureGalEnabled,
							 ZaAccount.A_zimbraFeatureMAPIConnectorEnabled,
							 ZaAccount.A_zimbraFeatureBriefcasesEnabled,
							 ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled,
							 ZaAccount.A_zimbraFeatureImportFolderEnabled,
                             ZaAccount.A_zimbraFeatureExportFolderEnabled,
							 ZaAccount.A_zimbraDumpsterEnabled,
                             ZaAccount.A_zimbraFeatureCrocodocEnabled
							 ]]
						],
						items:[							
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureTaggingEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureTaggingEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSharingEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureManageZimlets, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureManageZimlets,checkBoxLabel:ZaMsg.LBL_zimbraFeatureManageZimlets, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled, trueValue:"TRUE", falseValue:"FALSE"},														
							//{ref:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGalEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMAPIConnectorEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureImportFolderEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureImportFolderEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureImportFolderEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                            {ref:ZaAccount.A_zimbraFeatureExportFolderEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureExportFolderEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureExportFolderEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraDumpsterEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraDumpsterEnabled, checkBoxLabel:ZaMsg.LBL_zimbraDumpsterEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
					        {ref:ZaAccount.A_zimbraFeatureCrocodocEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureCrocodocrEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureCrocodocrEnabled, trueValue:"TRUE", falseValue:"FALSE"}

						]
					},	
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraMailFeature, id:"account_form_features_mail", colSizes:["auto"],numCols:1,
						visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
							[ZaAccount.A_zimbraFeatureMailPriorityEnabled,
							 ZaAccount.A_zimbraFeatureFlaggingEnabled,
							 ZaAccount.A_zimbraImapEnabled,
							 ZaAccount.A_zimbraPop3Enabled,
							 ZaAccount.A_zimbraFeatureImapDataSourceEnabled,
							 ZaAccount.A_zimbraFeaturePop3DataSourceEnabled,
							 ZaAccount.A_zimbraFeatureMailSendLaterEnabled,
							 ZaAccount.A_zimbraFeatureConversationsEnabled,
							 ZaAccount.A_zimbraFeatureFiltersEnabled,
							 ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled,
							 ZaAccount.A_zimbraFeatureNewMailNotificationEnabled,
							 ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled,
							 ZaAccount.A_zimbraFeatureIdentitiesEnabled,
							 ZaAccount.A_zimbraFeatureReadReceiptsEnabled
							 ]]
						],						
						enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureMailEnabled,"TRUE"]],
						enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailEnabled, ZaAccount.A_COSId],
						items:[													
							{ref:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled, trueValue:"TRUE", falseValue:"FALSE"}	,
							{ref:ZaAccount.A_zimbraFeatureFlaggingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFlaggingEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureFlaggingEnabled, trueValue:"TRUE", falseValue:"FALSE"}	,
							{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraImapEnabled,checkBoxLabel:ZaMsg.LBL_zimbraImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPop3Enabled,checkBoxLabel:ZaMsg.LBL_zimbraPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureImapDataSourceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraExternalImapEnabled,checkBoxLabel:ZaMsg.LBL_zimbraExternalImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},									
							{ref:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraExternalPop3Enabled,checkBoxLabel:ZaMsg.LBL_zimbraExternalPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMailSendLaterEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureConversationsEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureConversationsEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFiltersEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureIdentitiesEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							},
							{ref:ZaAccount.A_zimbraFeatureReadReceiptsEnabled,
								type:_SUPER_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureReadReceiptsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							}							
						]
					},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraCalendarFeature, id:"account_form_features_calendar",colSizes:["auto"],numCols:1,
						visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
							[ZaAccount.A_zimbraFeatureGroupCalendarEnabled,
							 //ZaAccount.A_zimbraFeatureFreeBusyViewEnabled,
                             ZaAccount.A_zimbraFeatureCalendarReminderDeviceEmailEnabled
							 ]]
						],						
						enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureCalendarEnabled,"TRUE"]],
						enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureCalendarEnabled,ZaAccount.A_COSId],
						items:[						
							{ref:ZaAccount.A_zimbraFeatureGroupCalendarEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							//{ref:ZaAccount.A_zimbraFeatureFreeBusyViewEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                            {ref:ZaAccount.A_zimbraFeatureCalendarReminderDeviceEmailEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
						]
					},
				//	{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraIMFeature, id:"account_form_features_im", colSizes:["auto"],numCols:1,
				//		visibilityChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureIMEnabled,"TRUE"]],
				//		visibilityChangeEventSources:[ZaAccount.A_zimbraFeatureIMEnabled,ZaAccount.A_COSId],
				//		visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
				//			[ZaAccount.A_zimbraFeatureInstantNotify ]]
				//		],
				//		items:[	
				//			{ref:ZaAccount.A_zimbraFeatureInstantNotify,
				//				 type:_SUPER_CHECKBOX_,
				//				 msgName:ZaMsg.LBL_zimbraFeatureInstantNotify,
				//				 checkBoxLabel:ZaMsg.LBL_zimbraFeatureInstantNotify,
				//				 trueValue:"TRUE",
				//				 falseValue:"FALSE",
				//				 resetToSuperLabel:ZaMsg.NAD_ResetToCOS
				//			}
				//		]
				//	},
					{type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraSearchFeature, id:"account_form_features_search", colSizes:["auto"],numCols:1,
						visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
							[ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,
							ZaAccount.A_zimbraFeatureSavedSearchesEnabled,
							ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled,
							ZaAccount.A_zimbraFeaturePeopleSearchEnabled
							]]
						],
						items:[
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
						  {ref:ZaAccount.A_zimbraFeaturePeopleSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"}
						]
					},
                                        {type:_ZA_TOP_GROUPER_, label: ZaMsg.NAD_zimbraSMIMEFeature, id:"account_form_features_smime", colSizes:["auto"],numCols:1,
                                                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,             
                                      		[ZaAccount.A_zimbraFeatureManageSMIMECertificateEnabled, ZaAccount.A_zimbraFeatureSMIMEEnabled]]],
                                                items:[
                                                 {ref:ZaAccount.A_zimbraFeatureSMIMEEnabled,
                                                        type:_SUPER_CHECKBOX_,
                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                        msgName:ZaMsg.LBL_zimbraFeatureSMIMEEnabled,
                                                        checkBoxLabel:ZaMsg.LBL_zimbraFeatureSMIMEEnabled,
                                                        trueValue:"TRUE", falseValue:"FALSE"},

                                                 {ref:ZaAccount.A_zimbraFeatureManageSMIMECertificateEnabled, 
							type:_SUPER_CHECKBOX_, 
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							msgName:ZaMsg.LBL_zimbraFeatureManageSMIMECertificateEnabled,
							checkBoxLabel:ZaMsg.LBL_zimbraFeatureManageSMIMECertificateEnabled, 
							trueValue:"TRUE", falseValue:"FALSE"}
                                                ]
                                        }
				]
			});
	}
	if(_tab5) {
		var prefItems = [
						{type:_ZA_TOP_GROUPER_, id:"account_prefs_general",colSizes:["275px","auto"],numCols:2,
                            label: ZaMsg.NAD_GeneralOptions,
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraPrefClientType,
                        			ZaAccount.A_zimbraPrefMailInitialSearch,
                        			ZaAccount.A_zimbraPrefShowSearchString,
                        			ZaAccount.A_zimbraPrefImapSearchFoldersEnabled,
                        			ZaAccount.A_zimbraPrefUseKeyboardShortcuts,
//                        			ZaAccount.A_zimbraMailCanonicalAddress,
                        			ZaAccount.A_zimbraPrefWarnOnExit,
                        			ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit,
                        			ZaAccount.A_zimbraPrefShowSelectionCheckbox,
                        			ZaAccount.A_zimbraJunkMessagesIndexingEnabled,
                        			ZaAccount.A_zimbraPrefLocale
                        			]]
                        	],
							items :[
								{ref:ZaAccount.A_zimbraPrefClientType,
									type:_SUPER_SELECT1_,
                                    colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPrefClientType,
									label:ZaMsg.LBL_zimbraPrefClientType, 
									labelLocation:_LEFT_
								},
								{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPER_TEXTFIELD_,
									msgName:ZaMsg.LBL_zimbraPrefMailInitialSearch,
									txtBoxLabel:ZaMsg.LBL_zimbraPrefMailInitialSearch,
                                     labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
                                    labelCssStyle:"border-right: 1px solid",
									labelLocation:_LEFT_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefShowSearchString,
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefShowSearchString,checkBoxLabel:ZaMsg.LBL_zimbraPrefShowSearchString,trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled,  
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts,
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraPrefUseKeyboardShortcuts, trueValue:"TRUE", falseValue:"FALSE"},
								
								{ref:ZaAccount.A_zimbraPrefWarnOnExit, type:_SUPER_CHECKBOX_, nowrap:false,labelWrap:true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraPrefWarnOnExit,
									trueValue:"TRUE", falseValue:"FALSE"},
                                {ref:ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit, type:_SUPER_CHECKBOX_, nowrap:false,labelWrap:true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZabMsg.LBL_zimbraPrefAdminConsoleWarnOnExit,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_SUPER_CHECKBOX_,
									labelWrap: true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, checkBoxLabel:ZaMsg.LBL_zimbraPrefShowSelectionCheckbox,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, 
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraJunkMessagesIndexingEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraJunkMessagesIndexingEnabled, 
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefLocale, type:_SUPER_SELECT1_,
                                    colSpan:2,
                                    choices: ZaSettings.getLocaleChoices(),
                                    msgName:ZaMsg.LBL_zimbraPrefLocale,label:ZaMsg.LBL_zimbraPrefLocale,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS}
                            ]                                                             
						},	
						{type:_ZA_TOP_GROUPER_, id:"account_prefs_standard_client",colSizes:["275px","auto"],numCols:2,
							label:ZaMsg.NAD_MailOptionsStandardClient,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
								[
									ZaAccount.A_zimbraMaxMailItemsPerPage,
									ZaAccount.A_zimbraPrefMailItemsPerPage
								]]
							],
							items :[
								{ref:ZaAccount.A_zimbraMaxMailItemsPerPage, 
									type:_SUPER_SELECT1_, 
									editable:true,
									inputSize:4,
									choices:[10,25,50,100,250,500,1000],
									msgName:ZaMsg.MSG_zimbraMaxMailItemsPerPage,
									label:ZaMsg.LBL_zimbraMaxMailItemsPerPage, labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null},									
								{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, 
									type:_SUPER_SELECT1_, 
									editable:false,
									msgName:ZaMsg.MSG_zimbraPrefMailItemsPerPage,
									label:ZaMsg.LBL_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null}							
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"account_prefs_mail_general",
                            label: ZaMsg.NAD_MailOptions,
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
								[
									ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,
									ZaAccount.A_zimbraPrefDisplayExternalImages,
									ZaAccount.A_zimbraPrefGroupMailBy,
									ZaAccount.A_zimbraPrefMailDefaultCharset,
									ZaAccount.A_zimbraPrefMailToasterEnabled,
                                    ZaAccount.A_zimbraPrefMessageIdDedupingEnabled,
                                    ZaAccount.A_zimbraPrefItemsPerVirtualPage,
								]]
							],
							items :[
								{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred, 
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefDisplayExternalImages, 
									type:_SUPER_CHECKBOX_,  colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefDisplayExternalImages,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefDisplayExternalImages, 
									trueValue:"TRUE", falseValue:"FALSE"},	
								{ref:ZaAccount.A_zimbraPrefGroupMailBy,
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefGroupMailBy,
									label:ZaMsg.LBL_zimbraPrefGroupMailBy, 
									labelLocation:_LEFT_},

								{ref:ZaAccount.A_zimbraPrefMailDefaultCharset, type:_SUPER_SELECT1_,
									msgName:ZaMsg.LBL_zimbraPrefMailDefaultCharset,label:ZaMsg.LBL_zimbraPrefMailDefaultCharset,
									 labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
								{ref:ZaAccount.A_zimbraPrefMailToasterEnabled,
                                     type:_SUPER_CHECKBOX_,  colSpan:2,
                                     resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                     msgName:ZaMsg.MSG_zimbraPrefMailToasterEnabled,
                                     checkBoxLabel:ZaMsg.LBL_zimbraPrefMailToasterEnabled,
                                     trueValue:"TRUE", falseValue:"FALSE"},
                                {ref:ZaAccount.A_zimbraPrefMessageIdDedupingEnabled,
                                    type:_SUPER_CHECKBOX_,  colSpan:2,
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                    msgName:ZaMsg.MSG_zimbraPrefMessageIdDedupingEnabled,
                                    checkBoxLabel:ZaMsg.LBL_zimbraPrefMessageIdDedupingEnabled,
                                    trueValue:"TRUE", falseValue:"FALSE"},
                                {ref:ZaAccount.A_zimbraPrefItemsPerVirtualPage, type:_SUPER_TEXTFIELD_,
                                     colSizes:["275px", "275px", "*"],colSpan:2,
				     msgName:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage,
                                     txtBoxLabel:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage, 
				     labelLocation:_LEFT_,
resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
textFieldCssClass:"admin_xform_number_input"}
							]
						},
						{type:_ZA_TOP_GROUPER_,colSizes:["275px","100%"], id:"account_prefs_mail_receiving", numCols: 2,
							label:ZaMsg.NAD_MailOptionsReceiving,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
								[ZaAccount.A_zimbraPrefMailPollingInterval, 
								ZaAccount.A_zimbraMailMinPollingInterval,
								ZaAccount.A_zimbraPrefMailSoundsEnabled,
								ZaAccount.A_zimbraPrefMailFlashIcon,
								ZaAccount.A_zimbraPrefMailFlashTitle,
								ZaAccount.A_zimbraPrefNewMailNotificationEnabled,
								ZaAccount.A_zimbraPrefNewMailNotificationAddress,
								ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,
								ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration,
								ZaAccount.A_zimbraPrefOutOfOfficeReply,
								ZaAccount.A_zimbraPrefMailSendReadReceipts,
								ZaAccount.A_zimbraPrefReadReceiptsToAddress]]
							],							
							items :[
								{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_,
                                     labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
									msgName:ZaMsg.MSG_zimbraPrefMailPollingInterval,
									txtBoxLabel:ZaMsg.LBL_zimbraPrefMailPollingInterval, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
									nowrap:false,labelWrap:true									
								},							
								{ref:ZaAccount.A_zimbraMailMinPollingInterval,
                                    labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
									type:_SUPER_LIFETIME_,
									msgName:ZaMsg.MSG_zimbraMailMinPollingInterval,
									txtBoxLabel:ZaMsg.LBL_zimbraMailMinPollingInterval, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2
								},
                                {ref:ZaAccount.A_zimbraPrefMailSoundsEnabled,
									type:_SUPER_CHECKBOX_, colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.LBL_playSound,
									checkBoxLabel:ZaMsg.LBL_playSound,
									trueValue:"TRUE", falseValue:"FALSE"
								},
                                {ref:ZaAccount.A_zimbraPrefMailFlashIcon,
									type:_SUPER_CHECKBOX_,  colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.LBL_flashIcon,
									checkBoxLabel:ZaMsg.LBL_flashIcon,
									trueValue:"TRUE", falseValue:"FALSE"
								},
                                {ref:ZaAccount.A_zimbraPrefMailFlashTitle,
									type:_SUPER_CHECKBOX_, colSpan:2,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.LBL_flashTitle,
									checkBoxLabel:ZaMsg.LBL_flashTitle,
									trueValue:"TRUE", falseValue:"FALSE"
								},         
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, 
									type:_ZA_CHECKBOX_, 
									msgName:ZaMsg.LBL_zimbraPrefNewMailNotificationEnabled,
									label:ZaMsg.LBL_zimbraPrefNewMailNotificationEnabled,
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, 
									type:_TEXTFIELD_, 
									msgName:ZaMsg.LBL_zimbraPrefNewMailNotificationAddress,
									label:ZaMsg.LBL_zimbraPrefNewMailNotificationAddress, 
									labelLocation:_LEFT_,
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraPrefNewMailNotificationEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefNewMailNotificationAddress]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefNewMailNotificationEnabled],
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefOutOfOfficeReplyEnabled,
									label:ZaMsg.LBL_zimbraPrefOutOfOfficeReplyEnabled, trueValue:"TRUE", 
									falseValue:"FALSE"
								},							
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, 
									type:_SUPER_LIFETIME_,
									msgName:ZaMsg.MSG_zimbraPrefOutOfOfficeCacheDuration,
									txtBoxLabel:ZaMsg.LBL_zimbraPrefOutOfOfficeCacheDuration, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, 
									type:_TEXTAREA_, msgName:ZaMsg.LBL_zimbraPrefOutOfOfficeReply,
									label:ZaMsg.LBL_zimbraPrefOutOfOfficeReply, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", 
									width:"30em",
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefOutOfOfficeReply]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled]
								},
								{ref:ZaAccount.A_zimbraPrefMailSendReadReceipts, 
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									label:ZaMsg.LBL_zimbraPrefMailSendReadReceipts, 
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureReadReceiptsEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefMailSendReadReceipts]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureReadReceiptsEnabled]						
								},
								{ref:ZaAccount.A_zimbraPrefReadReceiptsToAddress, 
									type:_TEXTFIELD_, 
									msgName:ZaMsg.MSG_zimbraPrefReadReceiptsToAddress,
									label:ZaMsg.LBL_zimbraPrefReadReceiptsToAddress, 
									labelLocation:_LEFT_,
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureReadReceiptsEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefReadReceiptsToAddress]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureReadReceiptsEnabled],
									nowrap:false,labelWrap:true
								}								
							]
						},						
						{type:_ZA_TOP_GROUPER_, colSizes:["275px","100%"], id:"account_prefs_mail_sending",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsSending,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
								[ZaAccount.A_zimbraPrefSaveToSent,
								 ZaAccount.A_zimbraAllowAnyFromAddress,
								 ZaAccount.A_zimbraAllowFromAddress
								]]
							],							
							items :[
								{ref:ZaAccount.A_zimbraPrefSaveToSent,  
									colSpan:2,								
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefSaveToSent,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefSaveToSent,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraAllowAnyFromAddress,  
									colSpan:2,								
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraAllowAnyFromAddress,
									checkBoxLabel:ZaMsg.LBL_zimbraAllowAnyFromAddress,
									trueValue:"TRUE", falseValue:"FALSE"},	
									
								{ref:ZaAccount.A_zimbraAllowFromAddress,
									type:_REPEAT_,
									nowrap:false,labelWrap:true,
									label:ZaMsg.LBL_zimbraAllowFromAddress,
									msgName:ZaMsg.MSG_zimbraAllowFromAddress,
									labelLocation:_LEFT_, 
									addButtonLabel:ZaMsg.NAD_AddAddress, 
									align:_LEFT_,
									repeatInstance:emptyAlias,
									showAddButton:true,
									showRemoveButton:true,
									showAddOnNextRow:true,
									removeButtonLabel:ZaMsg.NAD_RemoveAddress,
									items: [
										{
											ref:".", type:_TEXTFIELD_, label:null,width:"200px", 
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraAllowFromAddress]],
											visibilityChecks:[[ZaItem.hasReadPermission,ZaAccount.A_zimbraAllowFromAddress]]
										}
									],
									//onRemove:ZaAccountXFormView.onRepeatRemove,
									visibilityChecks:[ZaAccountXFormView.isSendingFromAnyAddressDisAllowed,[ZaItem.hasReadPermission,ZaAccount.A_zimbraAllowFromAddress]],
									visibilityChangeEventSources:[ZaAccount.A_zimbraAllowAnyFromAddress, ZaAccount.A_zimbraAllowFromAddress, ZaAccount.A_COSId]								
								}															
							]
						},
						{type:_ZA_TOP_GROUPER_,colSizes:["275px","100%"], id:"account_prefs_mail_composing",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsComposing,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
								[
									ZaAccount.A_zimbraPrefComposeInNewWindow,
								 	ZaAccount.A_zimbraPrefComposeFormat,
								 	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize,
								 	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily,
								 	ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor,
								 	ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat,
								 	ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled,
								 	ZaAccount.A_zimbraMailSignatureMaxLength,
								 	ZaAccount.A_zimbraPrefMailSignature,
									ZaAccount.A_zimbraPrefAutoSaveDraftInterval
								]]
							],
							items :[
								{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, 
									colSpan:2,
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefComposeInNewWindow,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefComposeInNewWindow,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefComposeFormat, 
									//colSpan:2,
									type:_SUPER_SELECT1_, 
									nowrap:false,labelWrap:true,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefComposeFormat,
									label:ZaMsg.LBL_zimbraPrefComposeFormat},
								
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, 
									//colSpan:2,
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize,
									label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, 
									//colSpan:2,
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily,
									label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, 
									type:_SUPER_DWT_COLORPICKER_,
									labelCssStyle:"width:269px",
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor,
									label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, 
									colSpan:2,
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat, 
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled, 
									colSpan:2,
									type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
									trueValue:"TRUE", falseValue:"FALSE"},									
								{ref:ZaAccount.A_zimbraPrefMailSignatureEnabled, 
									type:_ZA_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefMailSignatureEnabled,
									label:ZaMsg.LBL_zimbraPrefMailSignatureEnabled,  
									trueValue:"TRUE", falseValue:"FALSE"},	
								/*{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, 
									//colSpan:2,								
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									colSizes:["195px","375px","190px"],
									msgName:ZaMsg.MSG_zimbraPrefMailSignatureStyle,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefMailSignatureStyle,
									trueValue:"internet", falseValue:"outlook"},*/
								{ref:ZaAccount.A_zimbraMailSignatureMaxLength, 
									colSpan:2,
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									labelLocation:_LEFT_, 
									msgName:ZaMsg.MSG_zimbraMailSignatureMaxLength,
									txtBoxLabel:ZaMsg.LBL_zimbraMailSignatureMaxLength,
									textFieldCssClass:"admin_xform_number_input"},
								{ref:ZaAccount.A_zimbraPrefMailSignature, type:_TEXTAREA_, 
									msgName:ZaMsg.MSG_zimbraPrefMailSignature,
									label:ZaMsg.LBL_zimbraPrefMailSignature, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", width:"30em",
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefMailSignatureEnabled],
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraPrefMailSignatureEnabled,"TRUE"]]
								},
                                {ref:ZaAccount.A_zimbraPrefAutoSaveDraftInterval, type:_SUPER_LIFETIME_,
                                    msgName:ZaMsg.MSG_zimbraPrefAutoSaveDraftInterval,
                                    txtBoxLabel:ZaMsg.LBL_zimbraPrefAutoSaveDraftInterval,
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
                                    nowrap:false,labelWrap:true
                                }
						
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"account_prefs_contacts_general",
							label:ZaMsg.NAD_ContactsOptions,
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        		[
									ZaAccount.A_zimbraPrefAutoAddAddressEnabled,
									ZaAccount.A_zimbraPrefGalAutoCompleteEnabled
                        		]]
                        	],
							items :[
								{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_CHECKBOX_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled,checkBoxLabel:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",
									colSpan:2
								},							
								{ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled,colSpan:2,
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE"}
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"account_prefs_calendar_general",
							label:ZaMsg.NAD_CalendarOptions,
                            visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        		[
                        			ZaAccount.A_zimbraPrefTimeZoneId,
                        			ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
                        			ZaAccount.A_zimbraPrefCalendarInitialView,
                        			ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek,
                        			ZaAccount.A_zimbraPrefCalendarApptVisibility,
                        			ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled,
                        			ZaAccount.A_zimbraPrefCalendarShowPastDueReminders,
                        			ZaAccount.A_zimbraPrefCalendarToasterEnabled,
                        			ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf,
                        			ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite,
                        			ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite,
                        			ZaAccount.A_zimbraPrefCalendarReminderFlashTitle,
                        			ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled,
                        			ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply,
                        			ZaAccount.A_zimbraPrefCalendarAutoAddInvites,
                        			ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges,
                        			ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal,
                        			ZaAccount.A_zimbraPrefCalendarUseQuickAdd,
                        			ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar
                        		]]
                        	],
							items :[
								{ref:ZaAccount.A_zimbraPrefTimeZoneId, type:_SUPER_SELECT1_,
								    valueWidth: appNewUI?"220px":"265px",
									msgName:ZaMsg.MSG_zimbraPrefTimeZoneId,label:ZaMsg.LBL_zimbraPrefTimeZoneId, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
								{ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
									type:_SUPER_SELECT1_, msgName:ZaMsg.MSG_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.LBL_zimbraPrefCalendarApptReminderWarningTime, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
								{ref:ZaAccount.A_zimbraPrefCalendarInitialView,
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPrefCalendarInitialView,
									label:ZaMsg.LBL_zimbraPrefCalendarInitialView, 
									labelLocation:_LEFT_
								},
								{ref:ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek, 
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPrefCalendarFirstDayOfWeek,
									label:ZaMsg.LBL_zimbraPrefCalendarFirstDayOfWeek, 
									labelLocation:_LEFT_
								},
								{ref:ZaAccount.A_zimbraPrefCalendarApptVisibility, 
									type:_SUPER_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPrefCalendarApptVisibility,
									label:ZaMsg.LBL_zimbraPrefCalendarApptVisibility, 
									labelLocation:_LEFT_
								},
								{ref:ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled,
									type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefAppleIcalDelegationEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefAppleIcalDelegationEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefCalendarShowPastDueReminders, type:_SUPER_CHECKBOX_,
									 colSpan:2,resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarShowPastDueReminders,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarShowPastDueReminders, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarToasterEnabled, type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarToasterEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarToasterEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf, type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarAllowCancelEmailToSelf,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowCancelEmailToSelf, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite, type:_SUPER_CHECKBOX_,
									colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarAllowPublishMethodInvite,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowPublishMethodInvite, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite, type:_SUPER_CHECKBOX_,
									 colSpan:2,resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarAllowForwardedInvite,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowForwardedInvite, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarReminderFlashTitle, type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.MSG_zimbraPrefCalendarReminderFlashTitle,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarReminderFlashTitle, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled, type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.LBL_zimbraPrefCalendarReminderSoundsEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarReminderSoundsEnabled, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},								
								{ref:ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply, type:_SUPER_CHECKBOX_,
									 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									msgName:ZaMsg.LBL_zimbraPrefCalendarSendInviteDeniedAutoReply,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarSendInviteDeniedAutoReply, 
									trueValue:"TRUE", falseValue:"FALSE",
									nowrap:false,labelWrap:true
								},
								{ref:ZaAccount.A_zimbraPrefCalendarAutoAddInvites, type:_SUPER_CHECKBOX_,
								 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges, type:_SUPER_CHECKBOX_,
								 colSpan:2,resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_CHECKBOX_,
								 colSpan:2, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, 
								 colSpan:2,type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarUseQuickAdd,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarUseQuickAdd, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, 
								 colSpan:2,type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar,checkBoxLabel:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar,trueValue:"TRUE", falseValue:"FALSE"}
							]
						}						
					];
		cases.push({type:_ZATABCASE_, id:"account_form_prefs_tab", numCols:1,
            paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
					 caseKey:_tab5,
					/*colSizes:["275px","275px","150px"],*/ items :prefItems});
	}


	if(_tab6) {
		cases.push({type:_ZATABCASE_, id:"account_form_aliases_tab", width:"100%", numCols:1,colSizes:["auto"],
            paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
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
											enableDisableChecks:[ZaAccountXFormView.isDeleteAliasEnabled,[ZaItem.hasRight,ZaAccount.REMOVE_ACCOUNT_ALIAS_RIGHT]],
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editAliasButtonListener.call(this);",id:"editAliasButton",
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache],
											enableDisableChecks:[ZaAccountXFormView.isEditAliasEnabled,[ZaItem.hasRight,ZaAccount.ADD_ACCOUNT_ALIAS_RIGHT],[ZaItem.hasRight,ZaAccount.REMOVE_ACCOUNT_ALIAS_RIGHT]]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											enableDisableChecks:[[ZaItem.hasRight,ZaAccount.ADD_ACCOUNT_ALIAS_RIGHT]],
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache],
											onActivate:"ZaAccountXFormView.addAliasButtonListener.call(this);"
										}
									]
								}
							]
						}
					]
				});
	}
	
	if(_tab7) {
		cases.push({type:_ZATABCASE_,id:"account_form_forwarding_tab", width:"100%", numCols:1,colSizes:["auto"],
					caseKey:_tab7, paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
					items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_EditFwdTopGroupGrouper,
							id:"account_form_user_forwarding_addr",colSizes:["275px","100%"],
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible, 
								[
									ZaAccount.A_zimbraFeatureMailForwardingEnabled
								]]
							],
							items :[					
							{
								ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								type:_SUPER_CHECKBOX_, colSpan: 2,
								colSizes:["275", "275", "*"],
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailForwardingEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							},
									{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, 
										type:_ZA_CHECKBOX_, 
										msgName:ZaMsg.LBL_zimbraPrefMailLocalDeliveryDisabled,
										label:ZaMsg.LBL_zimbraPrefMailLocalDeliveryDisabled, 
										trueValue:"TRUE", falseValue:"FALSE"
									},
									{ref:ZaAccount.A_zimbraPrefMailForwardingAddress, type:_TEXTFIELD_,width:"350px",

labelCssClass:"xform_label", cssClass:"admin_xform_name_input",
										label:ZaMsg.LBL_zimbraPrefMailForwardingAddress, 
										msgName:ZaMsg.LBL_zimbraPrefMailForwardingAddressMsg,

nowrap:false, labelWrap:true,
										labelLocation:_LEFT_,
                                        labelCssStyle:(appNewUI?"text-align:left;":_UNDEFINED_),
										align:_LEFT_,
										visibilityChecks:[ZaItem.hasReadPermission],
										enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureMailForwardingEnabled,"TRUE"]],
										enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailForwardingEnabled, ZaAccount.A_COSId]										
									},
                                {type:_GROUP_, colSizes:["275px", "*"], numCols: 2, width: "100%", colSpan:2,items:[
								{ref:ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, type:_DWT_LIST_, height:"100", width:"350px",
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource",
									headerList:null,onSelection:ZaAccountXFormView.calFwdAddrSelectionListener,label:ZaMsg.zimbraPrefCalendarForwardInvitesTo,
                                    labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
                                    labelCssStyle:(appNewUI?"text-align:left;border-right: 1px solid":_UNDEFINED_),
									visibilityChecks:[ZaItem.hasReadPermission]
								},
								{type:_GROUP_, numCols:6, width:"625px",colSizes:["275","100px","auto","100px","auto","100px"], colSpan:2,
									visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]],
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteCalFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isDeleteCalFwdAddrEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]],
											enableDisableChangeEventSources:[ZaAccount.A2_calFwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editCalFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isEditCalFwdAddrEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]],
											enableDisableChangeEventSources:[ZaAccount.A2_calFwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
	                                       {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]],                                        
											onActivate:"ZaAccountXFormView.addCalFwdAddrButtonListener.call(this);"
										}
									]
								},
                               {type: _DWT_ALERT_, colSpan: 2,
                                   containerCssStyle: "padding:10px;padding-top: 0px; width:100%;",
                                   style: DwtAlert.WARNING,
                                   iconVisible: true,
                                   content: ZaMsg.Alert_Bouncing_Reveal_Hidden_Adds
                                },
                                {ref:ZaAccount.A_zimbraMailForwardingAddress, type:_DWT_LIST_, height:"100", width:"350px",
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									headerList:null,onSelection:ZaAccountXFormView.fwdAddrSelectionListener,label:ZaMsg.NAD_EditFwdGroup,
                                    labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label"),
                                    labelCssStyle:(appNewUI?"text-align:left;border-right: 1px solid":_UNDEFINED_),
									visibilityChecks:[ZaItem.hasReadPermission]
								},
								{type:_GROUP_, numCols:6, width:"625px",colSizes:["275","100px","auto","100px","auto","100px"], colSpan:2,
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isDeleteFwdAddrEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraMailForwardingAddress]],
											enableDisableChangeEventSources:[ZaAccount.A2_fwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											onActivate:"ZaAccountXFormView.editFwdAddrButtonListener.call(this);",
											enableDisableChecks:[ZaAccountXFormView.isEditFwdAddrEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraMailForwardingAddress]],
											enableDisableChangeEventSources:[ZaAccount.A2_fwdAddr_selection_cache]
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraMailForwardingAddress]],                                        
											onActivate:"ZaAccountXFormView.addFwdAddrButtonListener.call(this);"
										}
									]
								}
							]
						}
                        ]}
					]
				});
	}

	if(_tab8) {
		cases.push({type:_ZATABCASE_, id:"account_form_interop_tab", width:"100%", numCols:1,colSizes:["auto"],
                    paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
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
											enableDisableChecks:[ZaAccountXFormView.isPushFpEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraForeignPrincipal]],
											enableDisableChangeEventSources:[ZaAccount.A_zimbraForeignPrincipal]
										},
										{type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
                                            onActivate:"ZaAccountXFormView.deleteFpButtonListener.call(this);",
                                            enableDisableChecks:[ZaAccountXFormView.isDeleteFpEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraForeignPrincipal]],
                                            enableDisableChangeEventSources:[ZaAccount.A2_fp_selection_cache]
                                        },
                                        {type:_CELLSPACER_},
                                        {type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
                                            onActivate:"ZaAccountXFormView.editFpButtonListener.call(this);",
                                            enableDisableChecks:[ZaAccountXFormView.isEditFpEnabled,[ZaItem.hasWritePermission,ZaAccount.A_zimbraForeignPrincipal]],
                                            enableDisableChangeEventSources:[ZaAccount.A2_fp_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraForeignPrincipal]],
											onActivate:"ZaAccountXFormView.addFpButtonListener.call(this);"
										}
									]
								}
							]
						}
					]
				});
	}

	if(_tab9) {
		cases.push({type:_ZATABCASE_, id:"account_form_themes_tab", numCols:1,
            caseKey:_tab9,
			items:[
				{type:_SPACER_},
				{type:_GROUP_, 
					items:[
						{ref:ZaAccount.A_zimbraPrefSkin, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
	                        msgName:ZaMsg.LBL_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_,
	                        choices:ZaAccountXFormView.themeChoices,
							visibilityChecks:[ZaAccountXFormView.gotSkins]
						},
	                    {type:_OUTPUT_,ref:ZaAccount.A_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_, 
	                   	  visibilityChecks:[ZaAccountXFormView.gotNoSkins]
	                    }
					] 
				},
				{type:_SPACER_},
				{type:_SUPER_SELECT_CHECK_,
					selectRef:ZaAccount.A_zimbraAvailableSkin, 
					ref:ZaAccount.A_zimbraAvailableSkin, 
					choices:ZaAccountXFormView.themeChoices,
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaAccountXFormView.gotSkins],
					visibilityChangeEventSources:[ZaModel.currentTab],
					caseKey:_tab9, caseVarRef:ZaModel.currentTab,
					limitLabel:ZaMsg.NAD_LimitThemesTo
				},
				{type:_DWT_ALERT_,colSpan:2,style: DwtAlert.WARNING, iconVisible:true,
					visibilityChecks:[ZaAccountXFormView.gotNoSkins],
					value:ZaMsg.ERROR_CANNOT_FIND_SKINS_FOR_ACCOUNT
				}
			] 
		});
	}	

	if(_tab10) {
		cases.push({type:_ZATABCASE_, id:"account_form_zimlets_tab", numCols:1,
            caseKey:_tab10, 
			items:[
				{type:_GROUP_, numCols:1,colSizes:["auto"],
					items: [
						{type:_SUPER_ZIMLET_SELECT_,
							selectRef:ZaAccount.A_zimbraZimletAvailableZimlets, 
							ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
							choices:ZaAccountXFormView.zimletChoices,
							visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
							visibilityChangeEventSources:[ZaModel.currentTab],
							caseKey:_tab10, caseVarRef:ZaModel.currentTab,
							limitLabel:ZaMsg.NAD_LimitZimletsTo
						},
						{type: _DWT_ALERT_,
							containerCssStyle: "padding-bottom:0px",
							style: DwtAlert.INFO,
							iconVisible: false,
							content: ZaMsg.Zimlet_Note
						}
					]
				}
			] 
		});
	}
	if(_tab11) {
		cases.push({type:_ZATABCASE_, id:"account_form_advanced_tab", numCols:1,
        paddingStyle:(appNewUI? "padding-left:15px;":null), width:(appNewUI? "98%":"100%"), cellpadding:(appNewUI?2:0),
					caseKey:_tab11, 
					items: [
						{type:_ZA_TOP_GROUPER_, id:"account_attachment_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_AttachmentsGrouper,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraAttachmentsBlocked]]],
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
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraMailForwardingAddressMaxLength,
                        			ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs,
                        			ZaAccount.A_zimbraMailQuota,
                        			ZaAccount.A_zimbraContactMaxNumEntries,
                        			ZaAccount.A_zimbraQuotaWarnPercent,
                        			ZaAccount.A_zimbraQuotaWarnInterval,
                        			ZaAccount.A_zimbraQuotaWarnMessage]],
                        			[XForm.checkInstanceValueNot,ZaAccount.A_zimbraIsExternalVirtualAccount,"TRUE"]],
							items: [
								{ref:ZaAccount.A_zimbraMailForwardingAddressMaxLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxLength,
                                     colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailForwardingAddressMaxLength, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxNumAddrs,
                                     colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailForwardingAddressMaxNumAddrs, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},							
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                     colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailQuota, msgName:ZaMsg.MSG_zimbraMailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraContactMaxNumEntries,
                                     colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraContactMaxNumEntries, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraQuotaWarnPercent, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.LBL_zimbraQuotaWarnPercent,
                                     colSpan:1,
									msgName:ZaMsg.MSG_zimbraQuotaWarnPercent,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnInterval, type:_SUPER_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                     colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraQuotaWarnInterval, 
									msgName:ZaMsg.MSG_zimbraQuotaWarnInterval,labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnMessage, type:_SUPER_TEXTAREA_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.MSG_zimbraQuotaWarnMessage,
									msgName:ZaMsg.LBL_zimbraQuotaWarnMessage,
                                     colSpan:1,
									labelCssStyle:"vertical-align:top", textAreaWidth:"250px", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								}
								
							]
						},

						{type:_ZA_TOP_GROUPER_, id:"account_datasourcepolling_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_DataSourcePolling,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraDataSourceMinPollingInterval, 
							ZaAccount.A_zimbraDataSourcePop3PollingInterval,
							ZaAccount.A_zimbraDataSourceImapPollingInterval,
							ZaAccount.A_zimbraDataSourceCalendarPollingInterval,
							ZaAccount.A_zimbraDataSourceRssPollingInterval,
							ZaAccount.A_zimbraDataSourceCaldavPollingInterval
							]]],
							items: [
                                                                {ref:ZaAccount.A_zimbraDataSourceMinPollingInterval, type:_SUPER_LIFETIME_,
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceMinPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceMinPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourcePop3PollingInterval, type:_SUPER_LIFETIME_,
                                                                        sgName:ZaMsg.MSG_zimbraDataSourcePop3PollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourcePop3PollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceImapPollingInterval, type:_SUPER_LIFETIME_,
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceImapPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceImapPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceCalendarPollingInterval, type:_SUPER_LIFETIME_,
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceCalendarPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceCalendarPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceRssPollingInterval, type:_SUPER_LIFETIME_,
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceRssPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceRssPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceCaldavPollingInterval, type:_SUPER_LIFETIME_,
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceCaldavPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceCaldavPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:1,
                                                                        nowrap:false,labelWrap:true
                                                                }
							]
						},
                                                
                                                {type:_ZA_TOP_GROUPER_, id:"account_proxyalloweddomain_settings",
               					 	label: ZaMsg.NAD_ProxyAllowedDomains,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        					[ZaAccount.A_zimbraProxyAllowedDomains]]],
               						items:[
                   					{
                       						ref: ZaAccount.A_zimbraProxyAllowedDomains,
                       						label:ZaMsg.LBL_zimbraProxyAllowedDomains, 
                       						labelCssStyle:"vertical-align:top",
                       						type:_SUPER_REPEAT_,
                       						resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                       						repeatInstance:"",
                                            colSizes:["275px", "*"],
                       						addButtonLabel:ZaMsg.NAD_ProxyAddAllowedDomain ,
                       						removeButtonLabel: ZaMsg.NAD_ProxyRemoveAllowedDomain,
                       						showAddButton:true,
                       						showRemoveButton:true,
                       						showAddOnNextRow:true,
                       						repeatItems: [
                               					{ref:".", type:_TEXTFIELD_,
                                				enableDisableChecks:[ZaItem.hasWritePermission] ,
                                  				visibilityChecks:[[ZaItem.hasReadPermission,ZaAccount.A_zimbraProxyAllowedDomains]],
                                  				enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraProxyAllowedDomains]],
                                  				width: "15em"}
                                				]			
                     					}
               						]				
             					},

						{type:_ZA_TOP_GROUPER_,id:"account_password_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_PasswordGrouper,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraPasswordLocked,
                        			ZaAccount.A_zimbraMinPwdLength,
                        			ZaAccount.A_zimbraMaxPwdLength,
                        			ZaAccount.A_zimbraPasswordMinUpperCaseChars,
                        			ZaAccount.A_zimbraPasswordMinLowerCaseChars,
                        			ZaAccount.A_zimbraPasswordMinPunctuationChars,
                        			ZaAccount.A_zimbraPasswordMinNumericChars,
                        			ZaAccount.A_zimbraPasswordMinDigitsOrPuncs,
                        			ZaAccount.A_zimbraMinPwdAge,
                        			ZaAccount.A_zimbraMaxPwdAge,
                        			ZaAccount.A_zimbraEnforcePwdHistory]]],
							items: [ 
						                { type: _DWT_ALERT_, containerCssStyle: "padding-bottom:0px", colSpan:3,
						                      style: DwtAlert.WARNING,iconVisible: (!ZaAccountXFormView.isAuthfromInternal(entry.name)),
						                      content: ((ZaAccountXFormView.isAuthfromInternal(entry.name))?ZaMsg.Alert_InternalPassword:ZaMsg.Alert_ExternalPassword)
						                },
								{ref:ZaAccount.A_zimbraPasswordLocked, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_PwdLocked,
									checkBoxLabel:ZaMsg.NAD_PwdLocked, 
								 	trueValue:"TRUE", falseValue:"FALSE",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMinPwdLength, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMinPwdLength,
									txtBoxLabel:ZaMsg.LBL_zimbraMinPwdLength, 
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMaxPwdLength,txtBoxLabel:ZaMsg.LBL_zimbraMaxPwdLength,
									labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinUpperCaseChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinUpperCaseChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinLowerCaseChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinLowerCaseChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinPunctuationChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinPunctuationChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinNumericChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinNumericChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinDigitsOrPuncs, 
									type:_SUPER_TEXTFIELD_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinDigitsOrPuncs,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinDigitsOrPuncs, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},																
								{ref:ZaAccount.A_zimbraMinPwdAge, 
									type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_passMinAge,txtBoxLabel:ZaMsg.LBL_passMinAge, labelLocation:_LEFT_,
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMaxPwdAge, 
									type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_passMaxAge,txtBoxLabel:ZaMsg.LBL_passMaxAge, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, 
									type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraEnforcePwdHistory,
									txtBoxLabel:ZaMsg.LBL_zimbraEnforcePwdHistory, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
									visibilityChecks:[],enableDisableChecks:[[ZaAccountXFormView.isAuthfromInternalSync, entry.name, ZaAccount.A_name]]
								}
							]
						},
						{type:_ZA_TOP_GROUPER_, id:"password_lockout_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_FailedLoginGrouper,
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraPasswordLockoutEnabled,
                        			ZaAccount.A_zimbraPasswordLockoutMaxFailures,
                        			ZaAccount.A_zimbraPasswordLockoutDuration,
                        			ZaAccount.A_zimbraPasswordLockoutFailureLifetime]]],							
							items :[
								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, 
									type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPasswordLockoutEnabled, colSpan:1,
									checkBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutEnabled, 
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPER_TEXTFIELD_, 
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutMaxFailures,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_,
                                    colSpan:1,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPER_LIFETIME_, 
									colSpan:1,
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutDuration,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutDuration,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPER_LIFETIME_, 
									colSpan:1,
									enableDisableChecks: [[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId],								
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutFailureLifetime,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutFailureLifetime,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"white-space:normal; border-right: 1px solid ;",
									nowrap:false,labelWrap:true
								}																		
								
							]
						},
						{type:_ZA_TOP_GROUPER_, colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_TimeoutGrouper,id:"timeout_settings",
							visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraAdminAuthTokenLifetime,
                        			ZaAccount.A_zimbraAuthTokenLifetime,
                        			ZaAccount.A_zimbraMailIdleSessionTimeout]]],							
							items: [
								{ref:ZaAccount.A_zimbraAdminAuthTokenLifetime,
									type:_SUPER_LIFETIME_,
									colSpan:1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraAdminAuthTokenLifetime,
									txtBoxLabel:ZaMsg.LBL_zimbraAdminAuthTokenLifetime,
									//enableDisableChecks:[ZaAccountXFormView.isAdminAccount],
									enableDisableChecks:[ZaAccount.isAdminAccount],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraIsAdminAccount, ZaAccount.A_zimbraIsDelegatedAdminAccount]
								},								
								{ref:ZaAccount.A_zimbraAuthTokenLifetime,
									type:_SUPER_LIFETIME_,
									colSpan:1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraAuthTokenLifetime,
									txtBoxLabel:ZaMsg.LBL_zimbraAuthTokenLifetime},										
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, 
									type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailIdleSessionTimeout,
									colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailIdleSessionTimeout}															
							]
						},
                        { type:_ZA_TOP_GROUPER_, colSizes:["auto"], numCols:1,
                        	visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraMailMessageLifetime,
                        			ZaAccount.A_zimbraMailTrashLifetime,
                        			ZaAccount.A_zimbraMailSpamLifetime]],[XForm.checkInstanceValue,ZaAccount.A_zimbraIsExternalVirtualAccount,"FALSE"]],
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
                                { type: _GROUP_ , width: "100%",
                                    visibilityChecks:[ZaAccount.isEmailRetentionPolicyEnabled],
                                    visibilityChangeEventSources:[ZaAccount.A_mailHost],
                                    items: [
                                        {ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPER_LIFETIME2_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.MSG_zimbraMailMessageLifetime,
                                            txtBoxLabel:ZaMsg.LBL_zimbraMailMessageLifetime,
                                            labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label")
                                        },
                                        {ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraMailTrashLifetime,
                                            txtBoxLabel:ZaMsg.LBL_zimbraMailTrashLifetime,
                                            labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label")
                                        },
                                        {ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPER_LIFETIME1_,
                                            resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                            msgName:ZaMsg.MSG_zimbraMailSpamLifetime,
                                            txtBoxLabel:ZaMsg.LBL_zimbraMailSpamLifetime,
                                            labelCssClass:(appNewUI?"gridGroupBodyLabel":"xform_label")
                                        }
                                    ]
                                }
                            ]
                        },
                        {type:_ZA_TOP_GROUPER_, //colSizes:["auto"],numCols:1,
								label:ZaMsg.NAD_InteropGrouper,   id: "interop_settings",
								visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraFreebusyExchangeUserOrg]]],
							items: [
                                { ref: ZaAccount.A_zimbraFreebusyExchangeUserOrg, type: _SUPER_TEXTFIELD_,
                                    textFieldWidth: "220px",
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                    msgName:ZaMsg.LBL_zimbraFreebusyExchangeUserOrg,
                                    txtBoxLabel:ZaMsg.LBL_zimbraFreebusyExchangeUserOrg, labelLocation:_LEFT_,
                                    textFieldCssClass:"admin_xform_number_input"
                                }
                            ]
                        },

                	{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_MailTransportGrouper, id:"mailtransport_setting",
                                colSizes:["275px","*"],numCols:2,
                                visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
                                                [ZaAccount.A_zimbraMailTransport]]],
                                items:[
                                {ref:ZaAccount.A_zimbraMailTransport, type:_TEXTFIELD_, msgName:ZaMsg.NAD_MailTransport,label:ZaMsg.NAD_MailTransport,
                                        labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150
                                },
                            	{type:_OUTPUT_,ref:".",label:"", labelLocation:_LEFT_, value: ZaMsg.MSG_MailTransportMessage}

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
                        {type:_GROUP_,	numCols:4, width: "100%", colSizes:["90px","300px","100px","*"],items:headerItems}
                    ],
                    cssStyle:"padding-top:5px; padding-bottom:5px"
                },
                {type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:this.tabChoices,cssClass:"ZaTabBar", cssStyle:(appNewUI?"display:none;":""), id:"xform_tabbar"},
                {type:_SWITCH_, align:_LEFT_, valign:_TOP_, items:cases}
        ];
};
ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaAccountXFormView.myXFormModifier);

ZaAccountXFormView.prototype.getTabChoices =
function() {
    return this.tabChoices;
}
