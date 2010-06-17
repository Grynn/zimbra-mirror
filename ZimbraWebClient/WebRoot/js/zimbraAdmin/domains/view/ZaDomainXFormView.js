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
* @class ZaDomainXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaDomainXFormView = function(parent, entry) {
	ZaTabView.call(this, parent,"ZaDomainXFormView");	
	this.GALModes = [
		{label:ZaMsg.GALMode_internal, value:ZaDomain.GAL_Mode_internal},
		{label:ZaMsg.GALMode_external, value:ZaDomain.GAL_Mode_external}, 
		{label:ZaMsg.GALMode_both, value:ZaDomain.GAL_Mode_both}
  	];
  	this.GALServerTypes = [
		{label:ZaMsg.GALServerType_ldap, value:ZaDomain.GAL_ServerType_ldap},
		{label:ZaMsg.GALServerType_ad, value:ZaDomain.GAL_ServerType_ad} 
	];	
	
	this.AuthMechs = [
		{label:ZaMsg.AuthMech_zimbra, value:ZaDomain.AuthMech_zimbra},
		{label:ZaMsg.AuthMech_ldap, value:ZaDomain.AuthMech_ldap},
		{label:ZaMsg.AuthMech_ad, value:ZaDomain.AuthMech_ad}		
	];
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	this.catchAllChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	this.TAB_INDEX = 0;	
	this.initForm(ZaDomain.myXModel,this.getMyXForm(entry), null);
}

ZaDomainXFormView.prototype = new ZaTabView();
ZaDomainXFormView.prototype.constructor = ZaDomainXFormView;
ZaTabView.XFormModifiers["ZaDomainXFormView"] = new Array();

ZaDomainXFormView.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
/*
ZaDomainXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	this.setInstanceValue(list);
	form.parent.setDirty(true);
}*/
/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaDomainXFormView.prototype.setObject =
function(entry) {
    ZaAccount.prototype.manageSpecialAttrs.call (entry) ;

    this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	
	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	this._containedObject.name = entry.name;
	this._containedObject.id = entry.id;
	this._containedObject.type = entry.type ;
	
	for (var a in entry.attrs) {
        var modelItem = this._localXForm.getModel().getItem(a) ;
        if ((modelItem != null && modelItem.type == _LIST_) || (entry.attrs[a] != null && entry.attrs[a] instanceof Array)) {  
        	//need deep clone
            this._containedObject.attrs [a] = ZaItem.deepCloneListItem (entry.attrs[a]);
        } else {
            this._containedObject.attrs[a] = entry.attrs[a];
        }
    }
	if(!this._containedObject.attrs[ZaDomain.A_zimbraDomainStatus]) {
		this._containedObject.attrs[ZaDomain.A_zimbraDomainStatus] = ZaDomain.DOMAIN_STATUS_ACTIVE;
	}
	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

	this._containedObject[ZaDomain.A_NotebookTemplateFolder]=entry[ZaDomain.A_NotebookTemplateFolder];
	this._containedObject[ZaDomain.A_NotebookTemplateDir]=entry[ZaDomain.A_NotebookTemplateDir];	


	this._containedObject[ZaDomain.A_allNotebookACLS] = [];
	if(entry[ZaDomain.A_allNotebookACLS])	{
		this._containedObject[ZaDomain.A_allNotebookACLS]._version=entry[ZaDomain.A_allNotebookACLS]._version ? entry[ZaDomain.A_allNotebookACLS]._version : 1;		
		var cnt = entry[ZaDomain.A_allNotebookACLS].length;
		for(var i = 0; i < cnt; i++) {
			var aclObj = entry[ZaDomain.A_allNotebookACLS][i];
			var _newAclObj = {};
			_newAclObj.gt=aclObj.gt;
			_newAclObj.name = aclObj.name;
			_newAclObj.zid = aclObj.zid;
			_newAclObj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};
			for (var a in aclObj.acl) {
				_newAclObj.acl[a] = aclObj.acl[a];
			}					
			this._containedObject[ZaDomain.A_allNotebookACLS][i] = _newAclObj;
		}	
	}

    if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.ZIMLETS_TAB_ATTRS, ZaDomainXFormView.ZIMLETS_TAB_RIGHTS)) {

		//get all Zimlets
		var allZimlets = ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS);
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
		ZaDomainXFormView.zimletChoices.setChoices(_tmpZimlets);
		ZaDomainXFormView.zimletChoices.dirtyChoices();
	}

    //set the catchAllChoices
    var isCatchAllEnabled = this._containedObject.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] ?
    			this._containedObject.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] : this._containedObject._defaultValues.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] ;
    			
    if (isCatchAllEnabled && isCatchAllEnabled == "TRUE") {
        this._containedObject[ZaAccount.A_zimbraMailCatchAllAddress] = entry [ZaAccount.A_zimbraMailCatchAllAddress] ;
        this.catchAllChoices.setChoices ([entry[ZaAccount.A_zimbraMailCatchAllAddress]]) ;
        this.catchAllChoices.dirtyChoices();
    }
    
 	//if(ZaSettings.COSES_ENABLED) {	
	if(this._containedObject.attrs[ZaDomain.A_domainDefaultCOSId] && this._containedObject.getAttrs[ZaDomain.A_domainDefaultCOSId]) {	
		var cos = ZaCos.getCosById(this._containedObject.attrs[ZaDomain.A_domainDefaultCOSId]);
		this.cosChoices.setChoices([cos]);
		this.cosChoices.dirtyChoices();
	}
	//}

	this._containedObject[ZaDomain.A2_gal_sync_accounts] = [];
	if(entry[ZaDomain.A2_gal_sync_accounts] && entry[ZaDomain.A2_gal_sync_accounts][0]) {
		this._containedObject[ZaDomain.A2_gal_sync_accounts][0] = new ZaAccount();
		this._containedObject[ZaDomain.A2_gal_sync_accounts][0].name = entry[ZaDomain.A2_gal_sync_accounts][0].name;
		this._containedObject[ZaDomain.A2_gal_sync_accounts][0].id = entry[ZaDomain.A2_gal_sync_accounts][0].id;
		this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_datasources] = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_datasources]; 
		//this._containedObject[ZaDomain.A2_gal_sync_accounts][0].attrs = ZaItem.deepCloneListItem (entry[ZaDomain.A2_gal_sync_accounts][0].attrs);
		if(entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds]) {
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds] = new ZaDataSource();
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs = [];
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].name = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].name;
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].id = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].id;
			for (var a in entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs) {
		        var modelItem = this._localXForm.getModel().getItem(a) ;
		        if (entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a] != null && entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a] instanceof Array) {  
		        	//need deep clone
		            this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a] = ZaItem.deepCloneListItem (entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a]);
		        } else {
		            this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a] = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs[a];
		        }
		    }
		}
		if(entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds]) {
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds] = new ZaDataSource();
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].name = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].name;
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].id = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].id; 
			this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs = [];
			for (var a in entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs) {
		        var modelItem = this._localXForm.getModel().getItem(a) ;
		        if (entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[a] != null && entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[a] instanceof Array) {  
		        	//need deep clone
		            this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs [a] = ZaItem.deepCloneListItem (entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[a]);
		        } else {
		            this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[a] = entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_ldap_ds].attrs[a];
		        }
		    }		    
			//this._containedObject[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs = ZaItem.deepCloneListItem (entry[ZaDomain.A2_gal_sync_accounts][0][ZaAccount.A2_zimbra_ds].attrs); 
		}
	}
	
    this._localXForm.setInstance(this._containedObject);        
	this.updateTab();
}

ZaDomainXFormView.isCatchAllEnabled = function () {
    /*var form = this;
    var instance = form.getInstance () ;
    var isCatchAllEnabled = instance.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled]
               || instance._defaultValues.attrs[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled] ;

    return (isCatchAllEnabled == "TRUE" ? true : false) ;*/
    return (this.getInstanceValue(ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled) == "TRUE");
}

ZaDomainXFormView.aclSelectionListener = 
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length)
		this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_acl_selection_cache,arr);
		//instance[ZaDomain.A2_acl_selection_cache].acl_selection_cache = arr;
	else 
		this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_acl_selection_cache,null);
		//instance.acl_selection_cache = null;
		
	//this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDomainXFormView.editButtonListener.call(this);
	}	
}

ZaDomainXFormView.isDeleteAclEnabled = function () {
	var retVal = true;
	if (!AjxUtil.isEmpty(this.getInstanceValue(ZaDomain.A2_acl_selection_cache))) {
		var arr = this.getInstanceValue(ZaDomain.A2_acl_selection_cache);
		var cnt = arr.length;
		for(var i=0; i<cnt;i++) {
			if(arr[i].gt==ZaDomain.A_NotebookPublicACLs || 
				arr[i].gt==ZaDomain.A_NotebookAllACLs || 
				arr[i].gt ==ZaDomain.A_NotebookGuestACLs) {		
				retVal = false;
				break;
			}
		}
	} else {
		retVal = false;
	}
	
	return retVal;
}

ZaDomainXFormView.isEditAclEnabled = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDomain.A2_acl_selection_cache)) && this.getInstanceValue(ZaDomain.A2_acl_selection_cache).length==1);
}

ZaDomainXFormView.hasACEName = function () {
	return (this.getInstanceValue(ZaDomain.A_domainName) != this.getInstanceValue("name"));
}

ZaDomainXFormView.resetAllColorThemes = function () {
    var form = this.getForm() ;
//    var instance = form.getInstance () ;
    this.setInstanceValue (null, ZaDomain.A_zimbraSkinForegroundColor) ;
    this.setInstanceValue (null, ZaDomain.A_zimbraSkinBackgroundColor) ;
    this.setInstanceValue (null, ZaDomain.A_zimbraSkinSecondaryColor) ;
    this.setInstanceValue (null, ZaDomain.A_zimbraSkinSelectionColor) ;

    form.parent.setDirty(true);
    form.refresh () ;        
}

ZaDomainXFormView.isDomainModeNotInternal = function () {
	return (this.getInstanceValue(ZaDomain.A_zimbraGalMode) !=ZaDomain.GAL_Mode_internal);
}

ZaDomainXFormView.addButtonListener =
function () {
	var formPage = this.getForm().parent;
	if(!formPage.addAclDlg) {
		formPage.addAclDlg = new ZaAddDomainAclXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px");
		formPage.addAclDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addAcl, this.getForm(), null);						
	}
	var obj = {};
	obj.gt = ZaDomain.A_NotebookUserACLs;
	obj.name = "";
	obj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};	
	formPage.addAclDlg.setObject(obj);
	formPage.addAclDlg.popup();
}

ZaDomainXFormView.modifyAclCallback = 
function (params,resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);	
		
		if(!resp && !ZaApp.getInstance().getCurrentController()._currentRequest.cancelled) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaDomainController.prototype.modifyAclCallback"));
		} else if(resp.isException && resp.isException()) {
			throw(resp.getException());
		} else if(resp.getResponse().Body && resp.getResponse().Body.BatchResponse && resp.getResponse().Body.BatchResponse.Fault) {
			var fault = resp.getResponse().Body.BatchResponse.Fault;
			if(fault instanceof Array)
				fault = fault[0];
					
			if (fault) {
				// JS response with fault
				var ex = ZmCsfeCommand.faultToEx(fault);
				throw(ex);
			}
		}
			
		var domain = new ZaDomain();
		var instance = this.getInstance();
		domain.attrs[ZaDomain.A_zimbraNotebookAccount] = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraNotebookAccount);
		domain.attrs[ZaDomain.A_zimbraDomainStatus] = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraDomainStatus);
		domain.id = this.getInstance().id;
		domain.attrs[ZaItem.A_zimbraId] = this.getModel().getInstanceValue(this.getInstance(),ZaItem.A_zimbraId);
		domain.name = this.getInstance().name;
		ZaDomain.loadNotebookACLs.call(domain);
		var oldArray = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_allNotebookACLS);
		if(oldArray) {
			domain[ZaDomain.A_allNotebookACLS]._version = oldArray._version + 1;
		} else
			domain[ZaDomain.A_allNotebookACLS]._version = 1;
		
		this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A_allNotebookACLS,domain[ZaDomain.A_allNotebookACLS]);	
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDomainXFormView.modifyAclCallback");
	}
}

ZaDomainXFormView.addAcl = 
function () {
	if(this.parent.addAclDlg) {
		this.parent.addAclDlg.popdown();
		var obj = this.parent.addAclDlg.getObject();

		var accountName = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraNotebookAccount);
		var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "stop");		
		ZaDomain.getGrantNotebookACLsRequest(obj,soapDoc);

		var params = new Object();
	
		if(accountName)
			params.accountName = accountName;
			
		var busyId = Dwt.getNextId();
		params.soapDoc = soapDoc;		
		params.asyncMode = true;	
		params.busyId = busyId;
		params.callback = new AjxCallback(this,ZaDomainXFormView.modifyAclCallback,[params]);	

		var reqMgrParams = {
			showBusy:true,
			busyId:busyId,
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_FOLDER_PERMISSIONS
		}	
		ZaRequestMgr.invoke(params, reqMgrParams);		
	}	
}

ZaDomainXFormView.editButtonListener =
function () {
	var instance = this.getInstance();
	if(instance[ZaDomain.A2_acl_selection_cache] && instance[ZaDomain.A2_acl_selection_cache][0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editAclDlg) {
			formPage.editAclDlg = new ZaEditDomainAclXDialog(ZaApp.getInstance().getAppCtxt().getShell(), "550px", "150px");
			formPage.editAclDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.updateAcl, this.getForm(), null);						
		}
		var obj = {};
		obj.gt = instance[ZaDomain.A2_acl_selection_cache][0].gt;
		obj.name = instance[ZaDomain.A2_acl_selection_cache][0].name;
		obj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};
		for(var a in instance[ZaDomain.A2_acl_selection_cache][0].acl) {
			obj.acl[a] = instance[ZaDomain.A2_acl_selection_cache][0].acl[a];
		}
		formPage.editAclDlg.setObject(obj);
		formPage.editAclDlg.popup();		
	}
}


ZaDomainXFormView.updateAcl = 
function () {
	if(this.parent.editAclDlg) {
		this.parent.editAclDlg.popdown();
		var obj = this.parent.editAclDlg.getObject();
		var aclSelection = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A2_acl_selection_cache);
		var dirty = false;
		for(var a in obj.acl) {
			if(obj.acl[a] != aclSelection[0].acl[a]) {
				dirty = true;
				break;
			}
		}
		if(dirty) {
			var accountName = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraNotebookAccount);
			var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
			soapDoc.setMethodAttribute("onerror", "stop");		
			ZaDomain.getGrantNotebookACLsRequest(obj,soapDoc);
	
			var params = new Object();
		
			if(accountName)
				params.accountName = accountName;
				
			var busyId = Dwt.getNextId();
			params.soapDoc = soapDoc;		
			params.asyncMode = true;	
			params.busyId = busyId;
			params.callback = new AjxCallback(this,ZaDomainXFormView.modifyAclCallback,[params]);	
	
			var reqMgrParams = {
				showBusy:true,
				busyId:busyId,
				controller : ZaApp.getInstance().getCurrentController(),
				busyMsg : ZaMsg.BUSY_MODIFY_FOLDER_PERMISSIONS
			}	
			ZaRequestMgr.invoke(params, reqMgrParams);					
		}
		/*if(dirty) {
			aclSelection = [];
			aclSelection[0] = obj;
			var allNoteBookACLs = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_allNotebookACLS);
			var newNoteBookACLs = [];
			newNoteBookACLs._version = allNoteBookACLs._version + 1;
			var cnt = allNoteBookACLs.length;
			for(var i=0; i<cnt; i ++) {
				if(obj.name && allNoteBookACLs[i].name && (allNoteBookACLs[i].name == obj.name)) {
					newNoteBookACLs.push(obj);
				} else if(!obj.name && !allNoteBookACLs[i].name && (allNoteBookACLs[i].gt == obj.gt)) {
					newNoteBookACLs.push(obj);					
				} else {
					newNoteBookACLs.push(allNoteBookACLs[i]);
				}
			}
			this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A2_acl_selection_cache,aclSelection);
			//this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A_allNotebookACLS,newNoteBookACLs);
			
			
			//this.parent.setDirty(true);	
		}		*/
	}
}

ZaDomainXFormView.deleteButtonListener = 
function () {
	var aclSelectionCache = this.getInstanceValue(ZaDomain.A2_acl_selection_cache);
	if(AjxUtil.isEmpty(aclSelectionCache))
		return;
	var cnt = aclSelectionCache.length;
	if(cnt > 0) {
		var accountName = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraNotebookAccount);
		var soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		soapDoc.setMethodAttribute("onerror", "stop");
		var accountName = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_zimbraNotebookAccount);
		ZaDomain.getRevokeNotebookACLsRequest(aclSelectionCache,soapDoc);
		
		var params = new Object();
		if(accountName)
			params.accountName = accountName;

		var busyId = Dwt.getNextId();
		
		params.soapDoc = soapDoc;		
		params.asyncMode = true;	
		params.busyId = busyId;
		params.callback = new AjxCallback(this,ZaDomainXFormView.modifyAclCallback,[params]);	
		var reqMgrParams = {
			showBusy:true,
			busyId:busyId,
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_FOLDER_PERMISSIONS
		}	
		ZaRequestMgr.invoke(params, reqMgrParams);					
	}
	
	
	
	/*var allNoteBookACLs = this.getInstanceValue(ZaDomain.A_allNotebookACLS);

	if(AjxUtil.isEmpty(allNoteBookACLs))
		return;

	var newNoteBookACLs = AjxUtil.arraySubstract(allNoteBookACLs,aclSelectionCache,ZaDomain.compareACLs);
	newNoteBookACLs._version = allNoteBookACLs._version+1;
	*/
	/*var cnt = allNoteBookACLs.length;

	for(var i=0; i<cnt;i++) {
		if(aclSelectionCache[i].name && (aclSelectionCache[i].gt==ZaDomain.A_NotebookGroupACLs ||
		 aclSelectionCache[i].gt==ZaDomain.A_NotebookUserACLs ||
		 aclSelectionCache[i].gt==ZaDomain.A_NotebookDomainACLs)) {
			var cnt2 = allNoteBookACLs.length-1;
			for(var j=0; j < cnt2; j++) {
				if(allNoteBookACLs[j].name == aclSelectionCache[i].name) {
					continue;
				} else {
					newNoteBookACLs.push(allNoteBookACLs[j]);
				}
			}
		} else if (aclSelectionCache[i].gt) {
			var cnt2 = allNoteBookACLs.length-1;
			for(var j=cnt2; j >= 0; j--) {
				if(allNoteBookACLs[j].gt == aclSelectionCache[i].gt) {
					
					allNotebookACLS[j].acl = {r:0,w:0,i:0,d:0,a:0,x:0};
					break;
				}
			}
			
		}
	}*/
	
	//this.getModel().setInstanceValue(this.getInstance(),ZaDomain.A_allNotebookACLS,newNoteBookACLs);
	//instance[ZaDomain.A_allNotebookACLS]._version++; 
	//this.getForm().refresh();
	//this.getForm().parent.setDirty(true);	
}

ZaDomainXFormView.onFormFieldChanged = 
function (value, event, form) {
	var instance = this.getInstance();
	if(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN)) {
		var oldVal = this.getInstanceValue();
		return oldVal;
	} else {
		this.setInstanceValue(value);
		return value;
	}
}

ZaDomainXFormView.preProcessCOS = 
function(value, form) {
	var val = value;
	if(ZaItem.ID_PATTERN.test(value))  {
		val = value;
	} else {
		var cos = ZaCos.getCosByName(value, form.parent._app);
		if(cos) {
			val = cos.id;
		} 
	}
	return val;
}

ZaDomainXFormView.GAL_TAB_ATTRS = [ZaDomain.A_zimbraGalMode,ZaDomain.A_zimbraGalMaxResults,ZaDomain.A_GalLdapFilter,
	ZaDomain.A_zimbraGalAutoCompleteLdapFilter,ZaDomain.A_GalLdapSearchBase,ZaDomain.A_GalLdapURL,ZaDomain.A_GalLdapBindDn];
ZaDomainXFormView.GAL_TAB_RIGHTS = [];

ZaDomainXFormView.AUTH_TAB_ATTRS = [ZaDomain.A_AuthMech,ZaDomain.A_AuthLdapUserDn,ZaDomain.A_AuthLdapURL,
	ZaDomain.A_zimbraAuthLdapStartTlsEnabled,ZaDomain.A_AuthLdapSearchFilter,ZaDomain.A_AuthLdapSearchBase,
	ZaDomain.A_AuthLdapSearchBindDn];
ZaDomainXFormView.AUTH_TAB_RIGHTS = [];

ZaDomainXFormView.VH_TAB_ATTRS = [ZaDomain.A_zimbraVirtualHostname];
ZaDomainXFormView.VH_TAB_RIGHTS = [];

ZaDomainXFormView.WIKI_TAB_ATTRS = [ZaDomain.A_zimbraNotebookAccount];
ZaDomainXFormView.WIKI_TAB_RIGHTS = [];

ZaDomainXFormView.INTEROP_TAB_ATTRS = [ZaDomain.A_zimbraFreebusyExchangeURL, ZaDomain.A_zimbraFreebusyExchangeAuthScheme,
	ZaDomain.A_zimbraFreebusyExchangeAuthUsername, ZaDomain.A_zimbraFreebusyExchangeAuthPassword,ZaDomain.A_zimbraFreebusyExchangeUserOrg];
ZaDomainXFormView.INTEROP_TAB_RIGHTS = [];

ZaDomainXFormView.ZIMLETS_TAB_ATTRS = [ZaDomain.A_zimbraZimletDomainAvailableZimlets];
ZaDomainXFormView.ZIMLETS_TAB_RIGHTS = [];

ZaDomainXFormView.SKIN_TAB_ATTRS = [ZaDomain.A_zimbraSkinForegroundColor, ZaDomain.A_zimbraSkinBackgroundColor,ZaDomain.A_zimbraSkinSecondaryColor,
	ZaDomain.A_zimbraSkinSelectionColor, ZaDomain.A_zimbraSkinLogoURL, ZaDomain.A_zimbraSkinLogoLoginBanner, ZaDomain.A_zimbraSkinLogoAppBanner ];

ZaDomainXFormView.SKIN_TAB_RIGHTS = [];

ZaDomainXFormView.myXFormModifier = function(xFormObject,entry) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem("gt", ZaMsg.Domain_Notebook_type_col, null, "150px", false, null, false, true);
	headerList[1] = new ZaListHeaderItem("name", ZaMsg.Domain_Notebook_name_col, null,"200px", false, null, false, true);
	headerList[2] = new ZaListHeaderItem("acl", ZaMsg.Domain_Notebook_perms_col, null, "auto", null, null, false, true);							



    xFormObject.items = [ ];
	
	xFormObject.items.push({type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Domain_32", label:null,rowSpan:3},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle",
                            height: 32, rowSpan:3},				
						{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID,visibilityChecks:[ZaItem.hasReadPermission]},
						{type:_OUTPUT_, ref:ZaItem.A_zimbraCreateTimestamp, 
							label:ZaMsg.LBL_zimbraCreateTimestamp, labelLocation:_LEFT_,
							getDisplayValue:function() {
								var val = ZaItem.formatServerTime(this.getInstanceValue());
								if(!val)
									return ZaMsg.Server_Time_NA;
								else
									return val;
							},
							visibilityChecks:[ZaItem.hasReadPermission]	
						}						
					]
				}
			],
			cssStyle:"padding-top:5px; padding-left:2px; padding-bottom:5px"
	});	
	var tabIx = ++this.TAB_INDEX;
	var tabBar = {type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:[],cssClass:"ZaTabBar", id:"xform_tabbar"};
	tabBar.choices.push({value:tabIx, label:ZaMsg.TABT_GeneralPage});
	var switchGroup = {type:_SWITCH_, items:[]};
	var case1 = {type:_ZATABCASE_, caseKey:tabIx, 
		colSizes:["275px","*"],
		items:[
			{ type: _DWT_ALERT_,
				visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SHUTDOWN]],
				visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
				containerCssStyle: "padding-bottom:0px",
				style: DwtAlert.WARNING,
				iconVisible: true, 
				content: ZaMsg.Domain_Locked_Note,
				colSpan:"*"
			},
			{ ref: "name", type:_OUTPUT_, 
			  label:ZaMsg.Domain_DomainName                        
			},
            {ref:ZaAccount.A_zimbraMailCatchAllAddress, id: ZaAccount.A_zimbraMailCatchAllAddress, type:_DYNSELECT_,
                visibilityChecks:[ZaDomainXFormView.isCatchAllEnabled] ,
				visibilityChangeEventSources:[ZaDomain.A_zimbraAdminConsoleCatchAllAddressEnabled],
                dataFetcherClass:null,
                choices:this.catchAllChoices,
                emptyText:ZaMsg.enterSearchTerm,
                dataFetcherInstance:true,
                width:250,
				/**
				 * @argument callArgs {value, event, callback}
				 */
                dataFetcherMethod:function (callArgs) {
                	try {
							var value = callArgs["value"];
							var event = callArgs["event"];
							var callback = callArgs["callback"];
							var busyId = Dwt.getNextId();
							
							var params = new Object();
							dataCallback = new AjxCallback(this, ZaSearch.prototype.dynSelectDataCallback, {callback:callback, busyId:busyId});
							params.types = [ZaSearch.ACCOUNTS, ZaSearch.DLS];
							params.callback = dataCallback;
							params.sortBy = ZaAccount.A_name;
							params.domain = this.name;
							params.query = ZaSearch.getSearchByNameQuery(value,params.types);
							params.controller = ZaApp.getInstance().getCurrentController();
							params.showBusy = true;
							params.busyMsg = ZaMsg.BUSY_SEARCHING;
							params.busyId = busyId;
							params.skipCallbackIfCancelled = false; 							
							ZaSearch.searchDirectory(params);
						} catch (ex) {
							this._app.getCurrentController()._handleException(ex, "ZaSearch.prototype.dynSelectDataFetcher");		
						}
					
                },
                label:ZaMsg.L_catchAll, labelLocation:_LEFT_,
                onChange:ZaDomainXFormView.onFormFieldChanged,
                getDisplayValue:function(newValue) {
                	if(newValue && newValue.name)
                		return newValue.name;
                	else {
                		if(!AjxUtil.isEmpty(newValue))
                			return newValue;
                		else
                			return "";
                	}
                }
            },

			{ ref: ZaDomain.A_domainName, type:_OUTPUT_,
			  label:ZaMsg.Domain_ACEName+":",visibilityChecks:[ZaDomainXFormView.hasACEName], visibilityChangeEventSources:[ZaDomain.A_domainName]
			},
            {ref:ZaDomain.A_zimbraPrefTimeZoneId, type:_OSELECT1_, msgName:ZaMsg.MSG_zimbraPrefTimeZoneId,
                   label:ZaMsg.LBL_zimbraPrefTimeZoneId, labelLocation:_LEFT_,
                   onChange:ZaDomainXFormView.onFormFieldChanged
            }

         ]
	};

	case1.items.push({ ref: ZaDomain.A_zimbraPublicServiceHostname, type:_TEXTFIELD_, 
	  label:ZaMsg.Domain_zimbraPublicServiceHostname, width:250,
	  onChange:ZaDomainXFormView.onFormFieldChanged
  	});

	var group = {type:_GROUP_,colSpan:"2", id:"dns_check_group",items: [], width:"100%"};
	case1.items.push({ type: _DWT_ALERT_,
		containerCssStyle: "padding-bottom:0px",
		style: DwtAlert.INFO,
		iconVisible: true, 
		content: ZaMsg.Domain_InboundSMTPNote,
		colSpan:"2"});
		group.items.push({ref: ZaDomain.A_zimbraDNSCheckHostname, type:_SUPER_TEXTFIELD_, colSpan:2,
		txtBoxLabel:ZaMsg.Domain_zimbraDNSCheckHostname, onChange:ZaDomainXFormView.onFormFieldChanged,resetToSuperLabel:ZaMsg.NAD_ResetToGlobal});
	
	case1.items.push(group);
	case1.items.push(ZaItem.descriptionXFormItem) ;
    /* case1.items.push({ ref: ZaDomain.A_description, type: _INPUT_,
	  	label:ZaMsg.NAD_Description, width:250,
	  	onChange:ZaDomainXFormView.onFormFieldChanged});   */
	case1.items.push(
		{ref:ZaDomain.A_domainDefaultCOSId, type:_DYNSELECT_, 
				label:ZaMsg.Domain_DefaultCOS, labelLocation:_LEFT_, 
				inputPreProcessor:ZaDomainXFormView.preProcessCOS,
				searchByProcessedValue:false,
				dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
				choices:this.cosChoices,
				dataFetcherClass:ZaSearch,
				emptyText:ZaMsg.enterSearchTerm,
				editable:true,
				getDisplayValue:function(newValue) {
					// dereference through the choices array, if provided
					//newValue = this.getChoiceLabel(newValue);
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
				}
		});
	case1.items.push({ref:ZaDomain.A_zimbraDomainStatus, type:_OSELECT1_, msgName:ZaMsg.Domain_zimbraDomainStatus,
				label:ZaMsg.LBL_zimbraDomainStatus, 
				labelLocation:_LEFT_, choices:ZaDomain.domainStatusChoices, onChange:ZaDomainXFormView.onFormFieldChanged});

	case1.items.push({ ref: ZaDomain.A_notes, type:_TEXTAREA_, 
				  label:ZaMsg.NAD_Notes, labelCssStyle:"vertical-align:top", width:250,
				  onChange:ZaDomainXFormView.onFormFieldChanged});

	
			
	switchGroup.items.push(case1);
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.GAL_TAB_ATTRS, ZaDomainXFormView.GAL_TAB_RIGHTS)) {	
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.Domain_Tab_GAL});
		var case2 = {type:_ZATABCASE_, caseKey:tabIx,
			colSizes:["300px","*"],
			items: [
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SHUTDOWN]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Locked_Note,
					colSpan:"*"
				},
				{ref:ZaDomain.A_zimbraGalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes,visibilityChecks:[ZaItem.hasReadPermission] },
				{ref:ZaDomain.A_zimbraGalMaxResults, type:_TEXTFIELD_, label:ZaMsg.LBL_zimbraGalMaxResults, msgName:ZaMsg.MSG_zimbraGalMaxResults, autoSaveValue:true, labelLocation:_LEFT_,
                    cssClass:"admin_xform_number_input"},
				{ref:(ZaDomain.A2_gal_sync_accounts + "[0].name"), type:_OUTPUT_,label:ZaMsg.Domain_GalSyncAccount,
					visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_gal_sync_accounts]]
				},
				{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds + ".name"), label:ZaMsg.Domain_InternalGALDSName, type:_OUTPUT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
					visibilityChecks:[
						ZaNewDomainXWizard.isDomainModeNotExternal,
						[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
					]
				},
				{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds + ".attrs." + ZaDataSource.A_zimbraDataSourcePollingInterval), 
					type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_internal, labelLocation:_LEFT_,
					msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_internal,
					visibilityChecks:[
						ZaNewDomainXWizard.isDomainModeNotExternal,
						[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
					]						
				},				
				{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds + ".name"), label:ZaMsg.Domain_ExternalGALDSName, type:_OUTPUT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
					visibilityChecks:[
						ZaNewDomainXWizard.isDomainModeNotInternal,
						[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
					]
				},	
				{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds + ".attrs." + ZaDataSource.A_zimbraDataSourcePollingInterval), 
					type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_external, labelLocation:_LEFT_,
					msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_external,
					visibilityChecks:[
						ZaNewDomainXWizard.isDomainModeNotInternal,
						[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
					]						
				},															
				{type:_GROUP_, visibilityChecks:[ZaDomainXFormView.isDomainModeNotInternal], visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],useParentTable:true, colSpan:"*",
					items: [
						{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_},
						{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
							visibilityChangeEventSources:[ZaDomain.A_GALServerType]
						},
						{ref:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_zimbraGalAutoCompleteLdapFilter, labelLocation:_LEFT_, 
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
							visibilityChangeEventSources:[ZaDomain.A_GALServerType]
						},								
						{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_},
						{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.LBL_Domain_GalLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
							items:[
								{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
							]
						},								
						{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]]
							
						}
					]
				}
			]						
		};
		switchGroup.items.push(case2);
	}
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.AUTH_TAB_ATTRS, ZaDomainXFormView.AUTH_TAB_RIGHTS)) {
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.Domain_Tab_Authentication});
		var case3 = {type:_ZATABCASE_, caseKey:tabIx, 
			colSizes:["300px","*"],
			items: [
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SHUTDOWN]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Locked_Note,
					colSpan:"*"
				},
				{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs},
				{type:_GROUP_,useParentTable:true, colSpan:"*", 
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
					visibilityChangeEventSources:[ZaDomain.A_AuthMech],
					items:[
						{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
						{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
							items:[
								{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
							]
						}										
					]
				},
				{type:_GROUP_,useParentTable:true, colSpan:"*", 
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
					visibilityChangeEventSources:[ZaDomain.A_AuthMech],
					items:[
						{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
						{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
							items:[
								{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
							]
						},
						{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},
						{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_},
						{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_},
						{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
						{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, 
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
							visibilityChangeEventSources:[ZaDomain.A_AuthUseBindPassword]
						}											
					]
				}
			]
		};
		switchGroup.items.push(case3);	
	}
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.VH_TAB_ATTRS, ZaDomainXFormView.VH_TAB_RIGHTS)) {
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.Domain_Tab_VirtualHost});
		var case4 = {type:_ZATABCASE_, caseKey:tabIx,
			cssStyle:"padding-left:10px",
			items:[
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SHUTDOWN]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Locked_Note,
					colSpan:"*"
				},					
				{type:_DWT_ALERT_,content:null,ref:ZaDomain.A_domainName,
					getDisplayValue: function (itemVal) {
						return AjxMessageFormat.format(ZaMsg.Domain_VH_Explanation,itemVal);
					},
					colSpan:"*",
					iconVisible: false,
					align:_CENTER_,				
					style: DwtAlert.INFORMATION
				},
				{ref:ZaDomain.A_zimbraVirtualHostname, type:_REPEAT_, label:null, repeatInstance:"", showAddButton:true, showRemoveButton:true, 
						addButtonLabel:ZaMsg.NAD_AddVirtualHost, 
						showAddOnNextRow:true,
						removeButtonLabel:ZaMsg.NAD_RemoveVirtualHost,
						items: [
							{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaDomainXFormView.onFormFieldChanged}
						]/*,
						onRemove:ZaDomainXFormView.onRepeatRemove*/
				}
			]
		};
		switchGroup.items.push(case4);	
	}
	if(ZaDomainXFormView.WIKI_TAB_ATTRS && ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.WIKI_TAB_ATTRS, ZaDomainXFormView.WIKI_TAB_RIGHTS)) {
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.Domain_Tab_Notebook});
		var case5 = {type:_ZATABCASE_, caseKey:tabIx,cssStyle:"padding-left:10px",
			items : [
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SHUTDOWN]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Locked_Note,
					colSpan:"*"
				},
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SUSPENDED]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Suspended_Note,
					colSpan:"*"
				},				
				{ type: _DWT_ALERT_,
					visibilityChangeEventSources:[ZaDomain.A_zimbraDomainStatus],
					visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_MAINTENANCE]],
					containerCssStyle: "padding-bottom:0px",
					style: DwtAlert.WARNING,
					iconVisible: true, 
					content: ZaMsg.Domain_Maintenance_Note,
					colSpan:"*"
				},				

				{type: _DWT_ALERT_,
				  containerCssStyle: "padding-bottom:0px",
				  style: DwtAlert.WARNING,
				  iconVisible: true, 
				  content: ZaMsg.Alert_NotebookNotInitialized,
				  visibilityChecks:[[XForm.checkInstanceValueEmty,ZaDomain.A_zimbraNotebookAccount]],
				  visibilityChangeEventSources:[ZaDomain.A_zimbraNotebookAccount]
				},
				{type:_GROUP_,  numCols:2,
					visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A_zimbraNotebookAccount],
						[XForm.checkInstanceValueNot,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_MAINTENANCE],
						[XForm.checkInstanceValueNot,ZaDomain.A_zimbraDomainStatus,ZaDomain.DOMAIN_STATUS_SUSPENDED]
						],
				  	visibilityChangeEventSources:[ZaDomain.A_zimbraNotebookAccount],
					items: [
						{ref:ZaDomain.A_zimbraNotebookAccount, type:_EMAILADDR_, 
							label:ZaMsg.Domain_NotebookAccountName, labelLocation:_LEFT_,
							width:250,onChange:ZaDomainXFormView.onFormFieldChanged
						},	
						{type:_SPACER_, height:10},							
						{ref:ZaDomain.A_allNotebookACLS, colSpan:"*", type:_DWT_LIST_, height:"250", width:"100%", 
						 	forceUpdate: true, preserveSelection:true, multiselect:true,cssClass: "DLSource", 
						 	onSelection:ZaDomainXFormView.aclSelectionListener, headerList:headerList, 
							widgetClass:ZaNotebookACLListView
						},	
						{type:_SPACER_, height:10},															
						{type:_GROUP_, numCols:5, colSpan:"*",  tableCssClass:"search_field_tableCssClass", cssClass:"qsearch_field_bar", width:"95%", 
							items: [
								{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,
									onActivate:"ZaDomainXFormView.deleteButtonListener.call(this);",
									enableDisableChecks:[ZaDomainXFormView.isDeleteAclEnabled],
									enableDisableChangeEventSources:[ZaDomain.A2_acl_selection_cache]
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,
									onActivate:"ZaDomainXFormView.editButtonListener.call(this);",
									enableDisableChangeEventSources:[ZaDomain.A2_acl_selection_cache],
									enableDisableChecks:[ZaDomainXFormView.isEditAclEnabled]
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,
									onActivate:"ZaDomainXFormView.addButtonListener.call(this);"
								}
							]
						 }
					]
				}
			]
		};
		switchGroup.items.push(case5);
	}
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.INTEROP_TAB_ATTRS, ZaDomainXFormView.INTEROP_TAB_RIGHTS)) {	
        tabIx = ++this.TAB_INDEX;
        tabBar.choices.push({value:tabIx, label:ZaMsg.TABT_Interop});
        var case6 = {type: _ZATABCASE_, caseKey:tabIx,
			colSizes:["auto"],numCols:1,id:"global_interop_tab",
		 	items: [
				{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_Exchange_Settings,
					items: [
						{ ref: ZaDomain.A_zimbraFreebusyExchangeURL,
                             type: _SUPER_TEXTFIELD_, width: "30em" ,
                             resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                             onChange: ZaDomainXFormView.onFormFieldChanged ,
                             txtBoxLabel: ZaMsg.NAD_Exchange_URL
						},
						{ ref: ZaDomain.A_zimbraFreebusyExchangeAuthScheme, label: ZaMsg.NAD_Exchange_Auth_Schema,
							type: _SUPER_SELECT1_,   resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					    	onChange: ZaDomainXFormView.onFormFieldChanged
        	           	},
						{ ref: ZaDomain.A_zimbraFreebusyExchangeAuthUsername,
                            resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
				  	  		type: _SUPER_TEXTFIELD_, width: "20em",
                         	txtBoxLabel: ZaMsg.NAD_Exchange_Auth_User,
					  		onChange: ZaDomainXFormView.onFormFieldChanged
				  		},
					  	{ ref: ZaDomain.A_zimbraFreebusyExchangeAuthPassword, type: _PASSWORD_,
					  	  label: ZaMsg.NAD_Exchange_Auth_Password, width: "20em",
						  onChange: ZaDomainXFormView.onFormFieldChanged
					  	},
                        { ref: ZaDomain.A_zimbraFreebusyExchangeUserOrg, type: _SUPER_TEXTFIELD_ ,
                          resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					  	  txtBoxLabel: ZaMsg.LBL_zimbraFreebusyExchangeUserOrg, width: "30em",
						  onChange: ZaDomainXFormView.onFormFieldChanged
					  	},
                        {type: _SPACER_, height: 10 },
                        {type: _GROUP_, colSpan:2, numCols:5, colSizes: ["120px", "*", "50px", "*", "auto" ], 
                        	items :[
                            	{type:_CELLSPACER_ },
                              	{
                                  type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Check_Settings, width: "15em",
                                  onActivate: ZaItem.checkInteropSettings
                              	},
                              	{type:_CELLSPACER_},
                                {type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Clear_Settings, width: "15em",
                                    onActivate: ZaItem.clearInteropSettings
                              	},
                              	{type:_CELLSPACER_}    
                             ]
                        }
					]
				}
			]
		};
		switchGroup.items.push(case6);	
	}   
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.ZIMLETS_TAB_ATTRS, ZaDomainXFormView.ZIMLETS_TAB_RIGHTS)) {
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.TABT_Zimlets});
       	var case7 = {type:_ZATABCASE_, id:"account_form_zimlets_tab", numCols:1,
        	caseKey:tabIx,
			items:[
            	{type:_ZAGROUP_, numCols:1,colSizes:["auto"],
					items: [
                    	{type: _OUTPUT_, value: ZaMsg.NAD_LimitZimletsToDomain, cssStyle:"margin-left: 275px;" },
                    	{type:_ZA_ZIMLET_SELECT_COMBO_,
                            selectRef:ZaDomain.A_zimbraZimletDomainAvailableZimlets,
							ref:ZaDomain.A_zimbraZimletDomainAvailableZimlets,
							choices:ZaDomainXFormView.zimletChoices
						}
					]
				}
			]
		};
    	switchGroup.items.push(case7);
	}
    //domain skin properties
	if(ZaTabView.isTAB_ENABLED(entry,ZaDomainXFormView.SKIN_TAB_ATTRS, ZaDomainXFormView.SKIN_TAB_RIGHTS)) {
		tabIx = ++this.TAB_INDEX;
		tabBar.choices.push({value:tabIx, label:ZaMsg.TABT_Themes});
       	var case8 = {type:_ZATABCASE_, id:"domain_form_skin_tab", colSizes:["auto"],numCols:1,
        	caseKey:tabIx,
			items:[
            	{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_Skin_Color_Settings, colSizes:["275px","*"],
					items: [
						{ type: _DWT_ALERT_,
							style: DwtAlert.INFO,
							iconVisible: true, 
							content: ZaMsg.Domain_Chameleon_Note,
							colSpan:2,
							visibilityChecks:[],ref:null
						},
                    	{ref:ZaDomain.A_zimbraSkinForegroundColor,
                            type: _SUPER_DWT_COLORPICKER_,
                            label:ZaMsg.NAD_zimbraSkinForegroundColor,
                            labelLocation:_LEFT_,
                            resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                            buttonImage: "Color", width: "50px"
                        }  ,
                        {ref:ZaDomain.A_zimbraSkinBackgroundColor,
                            type: _SUPER_DWT_COLORPICKER_,
                            label:ZaMsg.NAD_zimbraSkinBackgroundColor,
                            labelLocation:_LEFT_,  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                            buttonImage: "Color", width: "50px"
                        }  ,
                        {ref:ZaDomain.A_zimbraSkinSecondaryColor, 
                            type: _SUPER_DWT_COLORPICKER_,
                            resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                            label:ZaMsg.NAD_zimbraSkinSecondaryColor,
                            labelLocation:_LEFT_,
                            buttonImage: "Color", width: "50px"
                        },
                        {ref:ZaDomain.A_zimbraSkinSelectionColor,
                            type: _SUPER_DWT_COLORPICKER_,
                            label:ZaMsg.NAD_zimbraSkinSelectionColor,
                            labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                            buttonImage: "Color", width: "50px"
                        },
                        {type:_GROUP_,  colSpan: 2, cssStyle: "margin-top: 10px; margin-left: 200px", items: [
                                {type: _DWT_BUTTON_,  label: ZaMsg.bt_ResetAllSkinColor,
                                    onActivate: ZaDomainXFormView.resetAllColorThemes }
                           ]
                        }
                    ]
				}
            ]
		};
    	switchGroup.items.push(case8);
	}
    
    xFormObject.items.push(tabBar);
	xFormObject.items.push(switchGroup);
}

ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainXFormView.myXFormModifier);


ZaAccMiniListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
}

ZaAccMiniListView.prototype = new ZaListView;
ZaAccMiniListView.prototype.constructor = ZaAccMiniListView;

ZaAccMiniListView.prototype.toString = function() {
	return "ZaAccMiniListView";
};

ZaAccMiniListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	}
}

//-------------------------------------------------------------------------------------------------------
//List View for the zimbraDomainCOSMaxAccounts

ZaDomainCOSMaxAccountsListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
    this._app = this.parent.parent._app ;
    
}

ZaDomainCOSMaxAccountsListView.prototype = new ZaListView;
ZaDomainCOSMaxAccountsListView.prototype.constructor = ZaDomainCOSMaxAccountsListView;

ZaDomainCOSMaxAccountsListView.prototype.toString = function() {
	return "ZaDomainCOSMaxAccountsListView";
};

ZaDomainCOSMaxAccountsListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	}
}


ZaDomainCOSMaxAccountsListView.prototype._createItemHtml =
function(item) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

    var itemArr = item.split(":");
    var cosId = itemArr [0];
    var cos = ZaCos.getCosById(cosId) ;
    var cosDisplayValue ;
    
    if (cos) {
        cosDisplayValue = cos.name ;

        /*if (ZaSettings.isDomainAdmin) {
            var cosDescription = cos.attrs[ZaCos.A_description] ;
            if (cosDescription)
                cosDisplayValue = cosDescription ;
        }*/
    } else {
        cosDisplayValue = AjxMessageFormat.format (ZaMsg.ERROR_INVALID_COS_VALUE, [cosId]) ;
    }

    var limits = itemArr [1] ;

    var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
    //cos
    html[idx++] = "<td width=" + this._headerList[0]._width + ">";
    html[idx++] = AjxStringUtil.htmlEncode(cosDisplayValue);
    html[idx++] = "</td>";

    // limits
    html[idx++] = "<td align='left' width=" + this._headerList[1]._width + "><nobr>";
    html[idx++] = AjxStringUtil.htmlEncode(limits);
    html[idx++] = "</nobr></td>";

	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaDomainCOSMaxAccountsListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");

	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br />",
                  ZaMsg.NO_LIMITS, 
                  "</td></tr></table>");

	div.innerHTML = buffer.toString();
	this._addRow(div);
};

//-------------------------------------------------------------------------------------------------------
//List View for the zimbraDomainFeatureMaxAccounts

ZaDomainFeatureMaxAccountsListView = function(parent, className, posStyle, headerList) {
	if (arguments.length == 0) return;
	ZaListView.call(this, parent, className, posStyle, headerList);
	this.hideHeader = true;
    this._app = this.parent.parent._app ;

}

ZaDomainFeatureMaxAccountsListView.prototype = new ZaListView;
ZaDomainFeatureMaxAccountsListView.prototype.constructor = ZaDomainFeatureMaxAccountsListView;

ZaDomainFeatureMaxAccountsListView.prototype.toString = function() {
	return "ZaDomainFeatureMaxAccountsListView";
};

ZaDomainFeatureMaxAccountsListView.prototype.createHeaderHtml = function (defaultColumnSort) {
	if(!this.hideHeader) {
		DwtListView.prototype.createHeaderHtml.call(this,defaultColumnSort);
	}
}


ZaDomainFeatureMaxAccountsListView.prototype._createItemHtml =
function(item) {
	var html = new Array(50);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

    var itemArr = item.split(":");
    var feature = ZaUtil.getListItemLabel(ZaCos.MAJOR_FEATURES_CHOICES, itemArr [0]) || itemArr [0];

    //get the feature display value
    /*
    var cos = ZaCos.getCosById(cosId, this._app) ;
    var cosDisplayValue ;

    if (cos) {
        cosDisplayValue = cos.name ;

        if (ZaSettings.isDomainAdmin) {
            var cosDescription = cos.attrs[ZaCos.A_description] ;
            if (cosDescription)
                cosDisplayValue = cosDescription ;
        }
    } else {
        cosDisplayValue = AjxMessageFormat.format (ZaMsg.ERROR_INVALID_COS_VALUE, [cosId]) ;
    } */

    var limits = itemArr [1] ;

    var idx = 0;
	html[idx++] = "<table width='100%' cellspacing='2' cellpadding='0'>";

	html[idx++] = "<tr>";
    //cos
    html[idx++] = "<td width=" + this._headerList[0]._width + ">";
    html[idx++] = AjxStringUtil.htmlEncode(feature);
    html[idx++] = "</td>";

    // limits
    html[idx++] = "<td align='left' width=" + this._headerList[1]._width + "><nobr>";
    html[idx++] = AjxStringUtil.htmlEncode(limits);
    html[idx++] = "</nobr></td>";

	html[idx++] = "</tr></table>";
	div.innerHTML = html.join("");
	return div;
}


ZaDomainFeatureMaxAccountsListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = document.createElement("div");

	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br />",
                  ZaMsg.NO_LIMITS,
                  "</td></tr></table>");

	div.innerHTML = buffer.toString();
	this._addRow(div);
};
