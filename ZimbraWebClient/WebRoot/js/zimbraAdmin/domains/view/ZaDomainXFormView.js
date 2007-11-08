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
* @class ZaDomainXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaDomainXFormView = function(parent, app) {
	ZaTabView.call(this, parent, app,"ZaDomainXFormView");	
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
	this.initForm(ZaDomain.myXModel,this.getMyXForm());
}

ZaDomainXFormView.prototype = new ZaTabView();
ZaDomainXFormView.prototype.constructor = ZaDomainXFormView;
ZaTabView.XFormModifiers["ZaDomainXFormView"] = new Array();

ZaDomainXFormView.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}
/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaDomainXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	this._containedObject.name = entry.name;
	this._containedObject.id = entry.id;
	this._containedObject.type = entry.type ;
	
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			var cnt = entry.attrs[a].length;
			for(var ix = 0; ix < cnt; ix++) {
				this._containedObject.attrs[a][ix]=entry.attrs[a][ix];
			}
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
			_newAclObj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};
			for (var a in aclObj.acl) {
				_newAclObj.acl[a] = aclObj.acl[a];
			}					
			this._containedObject[ZaDomain.A_allNotebookACLS][i] = _newAclObj;
		}	
	}	
	this._localXForm.setInstance(this._containedObject);
	this.updateTab();
}

ZaDomainXFormView.aclSelectionListener = 
function (ev) {
	var instance = this.getInstance();

	var arr = this.widget.getSelection();	
	if(arr && arr.length)
		instance.acl_selection_cache = arr;
	else 
		instance.acl_selection_cache = null;
		
	this.getForm().refresh();
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		ZaDomainXFormView.editButtonListener.call(this);
	}	
}

ZaDomainXFormView.isDeleteAclEnabled = function () {
	var retVal = true;
	if (this.instance.acl_selection_cache != null && this.instance.acl_selection_cache.length>0) {
		var cnt = this.instance.acl_selection_cache.length;
		for(var i=0; i<cnt;i++) {
			if(this.instance.acl_selection_cache[i].gt==ZaDomain.A_NotebookPublicACLs || 
				this.instance.acl_selection_cache[i].gt==ZaDomain.A_NotebookAllACLs || 
				this.instance.acl_selection_cache[i].gt ==ZaDomain.A_NotebookGuestACLs) {		
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
	return (this.instance.acl_selection_cache != null && this.instance.acl_selection_cache.length==1);
}

ZaDomainXFormView.hasACEName = function () {
	return (this.instance.attrs[ZaDomain.A_domainName] != this.instance.name);
}

ZaDomainXFormView.addButtonListener =
function () {
	var formPage = this.getForm().parent;
	if(!formPage.addAclDlg) {
		formPage.addAclDlg = new ZaAddDomainAclXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px");
		formPage.addAclDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.addAcl, this.getForm(), null);						
	}
	var obj = {};
	obj.gt = ZaDomain.A_NotebookUserACLs;
	obj.name = "";
	obj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};	
	formPage.addAclDlg.setObject(obj);
	formPage.addAclDlg.popup();
}

ZaDomainXFormView.addAcl = 
function () {
	if(this.parent.addAclDlg) {
		this.parent.addAclDlg.popdown();
		var obj = this.parent.addAclDlg.getObject();
		var aclsArr = this.getInstance()[ZaDomain.A_allNotebookACLS];
		var cnt = aclsArr.length;
		var foundObj = false;
		for(var i = 0; i < cnt; i++) {
			if(aclsArr[i].name == obj.name && aclsArr[i].gt == obj.gt) {
				for(var a in obj.acl) {
					if(obj.acl[a]) {
						aclsArr[i].acl[a] = obj.acl[a];
					}
				}
				foundObj = true;
				break;
			}
		}
		if(!foundObj) {
			aclsArr.push(obj);
		}
		aclsArr._version++;
		this.refresh();
		this.parent.setDirty(true);	
	}	
}

ZaDomainXFormView.editButtonListener =
function () {
	var instance = this.getInstance();
	if(instance.acl_selection_cache && instance.acl_selection_cache[0]) {	
		var formPage = this.getForm().parent;
		if(!formPage.editAclDlg) {
			formPage.editAclDlg = new ZaEditDomainAclXDialog(formPage._app.getAppCtxt().getShell(), formPage._app,"550px", "150px");
			formPage.editAclDlg.registerCallback(DwtDialog.OK_BUTTON, ZaDomainXFormView.updateAcl, this.getForm(), null);						
		}
		var obj = {};
		obj.gt = instance.acl_selection_cache[0].gt;
		obj.name = instance.acl_selection_cache[0].name;
		obj.acl = {r:0,w:0,i:0,d:0,a:0,x:0};
		for(var a in instance.acl_selection_cache[0].acl) {
			obj.acl[a] = instance.acl_selection_cache[0].acl[a];
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
		var dirty = false;
		if(obj.name != this.getInstance().acl_selection_cache[0].name) {
			dirty = true;
		} else {
			for(var a in obj.acl) {
				if(obj.acl[a] != this.getInstance().acl_selection_cache[0].acl[a]) {
					dirty = true;
					break;
				}
			}
		}
		if(dirty) {
			this.getInstance().acl_selection_cache[0].acl = obj.acl;
			this.getInstance().acl_selection_cache[0].name = obj.name;			
			this.getInstance()[ZaDomain.A_allNotebookACLS]._version++;
			this.refresh();
			this.parent.setDirty(true);	
		}		
	}
}

ZaDomainXFormView.deleteButtonListener = 
function () {
	var instance = this.getInstance();
	if(instance.acl_selection_cache) {
		var cnt = instance.acl_selection_cache.length;
		for(var i=0; i<cnt;i++) {
			if(instance.acl_selection_cache[i].name && (instance.acl_selection_cache[i].gt==ZaDomain.A_NotebookGroupACLs ||
			 instance.acl_selection_cache[i].gt==ZaDomain.A_NotebookUserACLs ||
			 instance.acl_selection_cache[i].gt==ZaDomain.A_NotebookDomainACLs)) {
				var cnt2 = instance[ZaDomain.A_allNotebookACLS].length-1;
				for(var j=cnt2; j >= 0; j--) {
					if(instance[ZaDomain.A_allNotebookACLS][j].name == instance.acl_selection_cache[i].name) {
						instance[ZaDomain.A_allNotebookACLS].splice(j,1);
						break;
					}
				}
			} else if (instance.acl_selection_cache[i].gt) {
				var cnt2 = instance[ZaDomain.A_allNotebookACLS].length-1;
				for(var j=cnt2; j >= 0; j--) {
					if(instance[ZaDomain.A_allNotebookACLS][j].gt == instance.acl_selection_cache[i].gt) {
						instance[ZaDomain.A_allNotebookACLS][j].acl = {r:0,w:0,i:0,d:0,a:0,x:0};
						break;
					}
				}
			}
		}
	}
	instance[ZaDomain.A_allNotebookACLS]._version++; 
	this.getForm().refresh();
	this.getForm().parent.setDirty(true);	
}

ZaDomainXFormView.onFormFieldChanged = 
function (value, event, form) {
	var instance = this.getInstance();
	if(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN)) {
		var oldVal = this.getInstanceValue();
		return oldVal;
	} else {
		return ZaTabView.onFormFieldChanged.call(this, value, event, form);
	}
}

ZaDomainXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	
	var headerList = new Array();
	headerList[0] = new ZaListHeaderItem("gt", ZaMsg.Domain_Notebook_type_col, null, "150px", false, null, false, true);
	headerList[1] = new ZaListHeaderItem("name", ZaMsg.Domain_Notebook_name_col, null,"200px", false, null, false, true);
	headerList[2] = new ZaListHeaderItem("acl", ZaMsg.Domain_Notebook_perms_col, null, "200px", null, null, false, true);							
	
	
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Domain_32", label:null},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", rowSpan:2},				
						{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-left:2px; padding-bottom:5px"
		},	

		{type:_TAB_BAR_,  ref:ZaModel.currentTab,
			choices:[
				{value:1, label:ZaMsg.Domain_Tab_General},
				{value:2, label:ZaMsg.Domain_Tab_GAL},
				{value:3, label:ZaMsg.Domain_Tab_Authentication},
				{value:4, label:ZaMsg.Domain_Tab_VirtualHost},
				{value:5, label:ZaMsg.Domain_Tab_Notebook}				
			],cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_, items:[
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 1", 
				colSizes:["300px","*"],
				items:[
						{ type: _DWT_ALERT_,
							relevantBehavior:_HIDE_,
							relevant:"(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN))",
							containerCssStyle: "padding-bottom:0px",
							style: DwtAlert.WARNING,
							iconVisible: true, 
							content: ZaMsg.Domain_Locked_Note,
							colSpan:"*"
						},
						{ ref: "name", type:_OUTPUT_, 
						  label:ZaMsg.Domain_DomainName
						},
						{ ref: ZaDomain.A_domainName, type:_OUTPUT_, 
						  label:ZaMsg.Domain_ACEName+":",relevant:"ZaDomainXFormView.hasACEName.call(this)", relevantBehavior:_HIDE_
						},	
						{ ref: ZaDomain.A_zimbraPublicServiceHostname, type:_INPUT_, 
						  label:ZaMsg.Domain_zimbraPublicServiceHostname, width:250,
						  onChange:ZaDomainXFormView.onFormFieldChanged
					  	},											
						{ ref: ZaDomain.A_description, type:_INPUT_, 
						  label:ZaMsg.NAD_Description, width:250,
						  onChange:ZaDomainXFormView.onFormFieldChanged
					  	},
						{ref:ZaDomain.A_domainDefaultCOSId, type:_OSELECT1_, 
							label:ZaMsg.Domain_DefaultCOS, labelLocation:_LEFT_, 
							choices:this._app.getCosListChoices(), onChange:ZaDomainXFormView.onFormFieldChanged
						},	
						{ref:ZaDomain.A_zimbraDomainStatus, type:_OSELECT1_, msgName:ZaMsg.Domain_zimbraDomainStatus,
							label:ZaMsg.Domain_zimbraDomainStatus+":", 
							labelLocation:_LEFT_, choices:ZaDomain.domainStatusChoices, onChange:ZaDomainXFormView.onFormFieldChanged
						},									  	
						{ ref: ZaDomain.A_notes, type:_TEXTAREA_, 
						  label:ZaMsg.NAD_Notes, labelCssStyle:"vertical-align:top", width:250,
						  onChange:ZaDomainXFormView.onFormFieldChanged
						}
					]
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 2", 
				colSizes:["300px","*"],
					items: [
						{ type: _DWT_ALERT_,
							relevantBehavior:_HIDE_,
							relevant:"(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN))",
							containerCssStyle: "padding-bottom:0px",
							style: DwtAlert.WARNING,
							iconVisible: true, 
							content: ZaMsg.Domain_Locked_Note,
							colSpan:"*"
						},
						{ref:ZaDomain.A_GalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes},
						{ref:ZaDomain.A_GalMaxResults, type:_OUTPUT_, label:ZaMsg.NAD_GalMaxResults, autoSaveValue:true},
						{type:_GROUP_, relevant:"instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,useParentTable:true, colSpan:"*",
							items: [
								{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_},
								{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == ZaDomain.GAL_ServerType_ldap", relevantBehavior:_HIDE_},
								{ref:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_zimbraGalAutoCompleteLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == ZaDomain.GAL_ServerType_ldap", relevantBehavior:_HIDE_},								
								{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_},
								{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								},								
								{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}
							]
						}
					]						
				}, 
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 3", 
					colSizes:["300px","*"],
					items: [
						{ type: _DWT_ALERT_,
							relevantBehavior:_HIDE_,
							relevant:"(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN))",
							containerCssStyle: "padding-bottom:0px",
							style: DwtAlert.WARNING,
							iconVisible: true, 
							content: ZaMsg.Domain_Locked_Note,
							colSpan:"*"
						},
						{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs},
						{type:_GROUP_,useParentTable:true, colSpan:"*", relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad",
							items:[
								{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								}										
							]
						},
						{type:_GROUP_,useParentTable:true, colSpan:"*", relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap",
							items:[
								{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								},
								{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
								{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_}											
							]
						}
					]						
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 4", cssStyle:"padding-left:10px",
					items:[
						{ type: _DWT_ALERT_,
							relevantBehavior:_HIDE_,
							relevant:"(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN))",
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
								],
								onRemove:ZaDomainXFormView.onRepeatRemove
						}
					]
				}, 
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 5",cssStyle:"padding-left:10px",
					items : [
						{ type: _DWT_ALERT_,
							relevantBehavior:_HIDE_,
							relevant:"(instance.attrs[ZaDomain.A_zimbraDomainStatus] && (instance.attrs[ZaDomain.A_zimbraDomainStatus]==ZaDomain.DOMAIN_STATUS_SHUTDOWN))",
							containerCssStyle: "padding-bottom:0px",
							style: DwtAlert.WARNING,
							iconVisible: true, 
							content: ZaMsg.Domain_Locked_Note,
							colSpan:"*"
						},
						{type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: true, 
						  content: ZaMsg.Alert_NotebookNotInitialized,
						  relevant:"instance.attrs[ZaDomain.A_zimbraNotebookAccount] == null",
						  relevantBehavior:_HIDE_
						},
						{type:_GROUP_,  numCols:2,
							relevant:"instance.attrs[ZaDomain.A_zimbraNotebookAccount] != null",
							relevantBehavior:_HIDE_,
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
											relevant:"ZaDomainXFormView.isDeleteAclEnabled.call(this)", relevantBehavior:_DISABLE_
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,
											onActivate:"ZaDomainXFormView.editButtonListener.call(this);",
											relevant:"ZaDomainXFormView.isEditAclEnabled.call(this)", relevantBehavior:_DISABLE_
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
				}
			]
		}	
	];
}

ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainXFormView.myXFormModifier);