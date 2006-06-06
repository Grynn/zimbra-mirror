/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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
function ZaDomainXFormView (parent, app) {
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

	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
}

ZaDomainXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle="width:100%;overflow:auto;";
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Domain_32", label:null},
						{type:_OUTPUT_, ref:ZaDomain.A_domainName, label:null,cssClass:"AdminTitle", rowSpan:2},				
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
				{value:4, label:ZaMsg.Domain_Tab_VirtualHost}
			],cssClass:"ZaTabBar"
		},
		{type:_SWITCH_, items:[
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 1", 
				colSizes:["300px","*"],
				items:[
						{ ref: ZaDomain.A_domainName, type:_OUTPUT_, 
						  label:ZaMsg.Domain_DomainName
						},
						{ ref: ZaDomain.A_description, type:_INPUT_, 
						  label:ZaMsg.NAD_Description, width:250,
						  onChange:ZaTabView.onFormFieldChanged
					  	},
						{ref:ZaDomain.A_domainDefaultCOSId, type:_OSELECT1_, 
							label:ZaMsg.Domain_DefaultCOS, labelLocation:_LEFT_, 
							choices:this._app.getCosListChoices(), onChange:ZaTabView.onFormFieldChanged
						},						  	
						{ ref: ZaDomain.A_notes, type:_TEXTAREA_, 
						  label:ZaMsg.NAD_Notes, labelCssStyle:"vertical-align:top", width:250,
						  onChange:ZaTabView.onFormFieldChanged
						}
					]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2", 
				colSizes:["300px","*"],
					items: [
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
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 3", 
					colSizes:["300px","*"],
					items: [
						{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs},
						{type:_GROUP_,useParentTable:true, colSpan:"*", relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad",
							items:[
								{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								}										
							]
						},
						{type:_GROUP_,useParentTable:true, colSpan:"*", relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap",
							items:[
								{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
								{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
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
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 4", 
					items:[
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
									{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged}
								],
								onRemove:ZaDomainXFormView.onRepeatRemove
						}
					]
				}
			]
		}	
	];
}

ZaTabView.XFormModifiers["ZaDomainXFormView"].push(ZaDomainXFormView.myXFormModifier);