/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaAccountXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaAccountXFormView (parent, app) {
	ZaTabView.call(this, parent, app);	
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_ACTIVE]}, 
		{value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_MAINTENANCE]}, 
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_LOCKED]},
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount._ACCOUNT_STATUS[ZaAccount.ACCOUNT_STATUS_CLOSED]}
	];
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
}

ZaAccountXFormView.prototype = new ZaTabView();
ZaAccountXFormView.prototype.constructor = ZaAccountXFormView;

/**
* @method setObject sets the object contained in the view
* @param entry - ZaAccount object to display
**/
ZaAccountXFormView.prototype.setObject =
function(entry) {
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
	if(entry.id)
		this._containedObject.id = entry.id;
		
	var cosList = this._app.getCosList().getArray();
	
	/**
	* If this account does not have a COS assigned to it - assign default COS
	**/
	if(this._containedObject.attrs[ZaAccount.A_COSId]) {	
		for(var ix in cosList) {
			/**
			* Find the COS assigned to this account 
			**/
			if(cosList[ix].id == this._containedObject.attrs[ZaAccount.A_COSId]) {
				this._containedObject.cos = cosList[ix];
				break;
			}
		}
	}
	if(!this._containedObject.cos) {
		/**
		* We did not find the COS assigned to this account,
		* this means that the COS was deleted or wasn't assigned, therefore assign default COS to this account
		**/
		for(var i in cosList) {
			/**
			* Find the COS assigned to this account 
			**/
			if(cosList[i].name == "default") {
				this._containedObject.cos = cosList[i];
				this._containedObject.attrs[ZaAccount.A_COSId] = cosList[i].id;										
				break;
			}
		}
		if(!this._containedObject.cos) {
			//default COS was not found - just assign the first COS
			if(cosList && cosList.length > 0) {
				this._containedObject.cos = cosList[0];
				this._containedObject.attrs[ZaAccount.A_COSId] = cosList[0].id;					
			}
		}
	}
	if(!this._containedObject.cos) {
		this._containedObject.cos = cosList[0];
	}	
	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname];
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword];
	
   	this._containedObject.globalConfig = this._app.getGlobalConfig();
   	
			
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
}

ZaAccountXFormView.generateDisplayName =
function (instance, firstName, lastName, initials) {
	var oldDisplayName = instance.attrs[ZaAccount.A_displayname];
	
	if(firstName)
		instance.attrs[ZaAccount.A_displayname] = firstName;
	else
		instance.attrs[ZaAccount.A_displayname] = "";
		
	if(initials) {
		instance.attrs[ZaAccount.A_displayname] += " ";
		instance.attrs[ZaAccount.A_displayname] += initials;
		instance.attrs[ZaAccount.A_displayname] += ".";
	}
	if(lastName) {
		if(instance.attrs[ZaAccount.A_displayname].length > 0)
			instance.attrs[ZaAccount.A_displayname] += " ";
			
	    instance.attrs[ZaAccount.A_displayname] += lastName;
	} 
	if(instance.attrs[ZaAccount.A_displayname] == oldDisplayName) {
		return false;
	} else {
		return true;
	}
}

ZaAccountXFormView.onCOSChanged = 
function(value, event, form) {
	var cosList = form.getController().getCosList().getArray();
	var cnt = cosList.length;
	for(var i = 0; i < cnt; i++) {
		if(cosList[i].id == value) {
			form.getInstance().cos = cosList[i];
			break;
		}
	}
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

ZaAccountXFormView.prototype.getMyXForm = function() {	
//	var domainName = this._app._appCtxt.getAppController().getOverviewPanelController().getCurrentDomain();
	var domainName;
	if(!domainName) {
		domainName = this._app.getDomainList().getArray()[0].name;
	}
	var emptyAlias = " @" + domainName;
	var xFormObject = {
		tableCssStyle:"width:100%;overflow:auto;",
		items: [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","200px"],
						items: [	
							{type:_AJX_IMAGE_, src:"Person_32", label:null, rowSpan:2},
							{type:_OUTPUT_, ref:ZaAccount.A_displayname, label:null,cssClass:"AdminTitle", rowSpan:2},
							{type:_OUTPUT_, ref:ZaAccount.A_COSId, label:ZaMsg.NAD_ClassOfService+":",  choices:this._app.getCosListChoices()},							
							{type:_OUTPUT_, ref:ZaAccount.A_accountStatus, label:ZaMsg.NAD_AccountStatus+":", labelLocation:_LEFT_, choices:this.accountStatusChoices},												
							{type:_OUTPUT_, ref:ZaAccount.A_name, label:ZaMsg.NAD_Email+":", labelLocation:_LEFT_, required:false},
							{type:_OUTPUT_, ref:ZaAccount.A_mailHost, label:ZaMsg.NAD_MailServer+":"},
							{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID},														
							{type:_OUTPUT_, ref:ZaAccount.A2_mbxsize, label:ZaMsg.usedQuota+":",
								getDisplayValue:function() {
									var val = this.getInstanceValue();
									if(!val) 
										val = "0 MB ";
									else {
										val = Number(val / 1048576).toFixed(3) + " MB ";
									}
									var quotaUsed = "";
									
									if(this.getInstance() != null)
										quotaUsed = this.getInstanceValue(ZaAccount.A2_quota);
										
									val += ZaMsg.Of + " " + quotaUsed + " MB";									
									return val;
								}
							}
						]
					}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			}
		]
	};
	var tabChoices = new Array();
	tabChoices.push({value:1, label:ZaMsg.TABT_GeneralPage});
	tabChoices.push({value:2, label:ZaMsg.TABT_ContactInfo});

	if(ZaSettings.ACCOUNTS_FEATURES_ENABLED)
		tabChoices.push({value:3, label:ZaMsg.TABT_Features});
					
	if(ZaSettings.ACCOUNTS_PREFS_ENABLED)
		tabChoices.push({value:4, label:ZaMsg.TABT_Preferences});

	if(ZaSettings.ACCOUNTS_ALIASES_ENABLED)
		tabChoices.push({value:5, label:ZaMsg.TABT_Aliases});

	if(ZaSettings.ACCOUNTS_FORWARDING_ENABLED)
		tabChoices.push({value:6, label:ZaMsg.TABT_Forwarding});

	if(ZaSettings.ACCOUNTS_ADVANCED_ENABLED)
		tabChoices.push({value:7, label:ZaMsg.TABT_Advanced});

	xFormObject.items.push({type:_TAB_BAR_,  ref:ZaModel.currentTab,choices:tabChoices,cssClass:"ZaTabBar"});
	xFormObject.items.push(
		{type:_SWITCH_, align:_LEFT_, valign:_TOP_, 
			items:[
				{type:_CASE_,  relevant:"instance[ZaModel.currentTab] == 1", height:"400px", align:_LEFT_, valign:_TOP_, 
					items:[
						{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName+":", labelLocation:_LEFT_,onChange:ZaTabView.onFormFieldChanged,forceUpdate:true},
						{ref:ZaAccount.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService+":",label:ZaMsg.NAD_ClassOfService+":", labelLocation:_LEFT_, choices:this._app.getCosListChoices(), onChange:ZaAccountXFormView.onCOSChanged},
						{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPasswordMustChange, align:_LEFT_, type:_CHECKBOX_,  msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label"},
						{ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						},
						{ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:50,  onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
								}
								this.getForm().itemChanged(this, elementValue, event);
							}
						},	
						{ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150, onChange:ZaTabView.onFormFieldChanged,
							elementChanged: function(elementValue,instanceValue, event) {
								if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
									ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
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
											if(ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials])) {
											//	this.getForm().itemChanged(this, elementValue, event);
												this.getForm().parent.setDirty(true);
											}
										}
										this.getForm().itemChanged(this, elementValue, event);
									}
								}
							]
						},
						{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,label:ZaMsg.NAD_AccountStatus+":", labelLocation:_LEFT_, choices:this.accountStatusChoices, onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes+":", labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, width:"30em"},
					]
				}, 
				{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentTab] == 2",
					items: [
						{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},														
						{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_postalAddress, type:_TEXTFIELD_, msgName:ZaMsg.NAD_postalAddress,label:ZaMsg.NAD_postalAddress+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150},
						{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, width:150}
					]
				},
				{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentTab] == 3 && ZaSettings.ACCOUNTS_FEATURES_ENABLED",
					items: [
						{ref:ZaAccount.A_zimbraFeatureContactsEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraFeatureCalendarEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
						{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}								
					]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 4 && ZaSettings.ACCOUNTS_PREFS_ENABLED",
					items :[
						{ref:ZaAccount.A_prefSaveToSent, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},														
						{ref:ZaAccount.A_zimbraPrefComposeFormat, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat+":", labelLocation:_LEFT_, labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefGroupMailBy, type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefContactsPerPage, type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_SUPER_SELECT1_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null,onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPER_LIFETIME_, msgName:ZaMsg.A_zimbraPrefMailPollingInterval,label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefShowSearchString, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
						{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,label:ZaMsg.NAD_useQuickAdd+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},							
						{ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},														
						{type:_SEPARATOR_},							
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged},							
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_prefMailSignatureEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,label:ZaMsg.NAD_prefMailSignatureEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,trueValue:"internet", falseValue:"outlook"},
						{ref:ZaAccount.A_prefMailSignature, type:_TEXTAREA_, msgName:ZaMsg.NAD_prefMailSignature,label:ZaMsg.NAD_prefMailSignature+":", labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, colSpan:3, width:"30em"},
						{type:_SEPARATOR_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply+":", labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged, colSpan:3, width:"30em"}							
					]
				},
				{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentTab] == 5 && ZaSettings.ACCOUNTS_ALIASES_ENABLED",
					items: [
						{type:_OUTPUT_, value:ZaMsg.NAD_EditAliasesGroup},
						{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAlias, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAlias,
							items: [
								{ref:".", type:_EMAILADDR_, label:null, onChange:ZaTabView.onFormFieldChanged}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}
					]
				},
				{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentTab] == 6 && ZaSettings.ACCOUNTS_FORWARDING_ENABLED", 
					items: [
						{type:_OUTPUT_, value:ZaMsg.NAD_EditFwdGroup},
						{ref:ZaAccount.A_zimbraMailForwardingAddress, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAddress,
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged, width:250}
							],
							onRemove:ZaAccountXFormView.onRepeatRemove
						}
					]
				},
				{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentTab] == 7 && ZaSettings.ACCOUNTS_ADVANCED_ENABLED",
					items: [
						{type:_GROUP_, 
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments+":", 
									labelLocation:_LEFT_, 
									labelCssStyle:"width:250px;", trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,
									label:ZaMsg.NAD_AttachmentsViewInHtmlOnly+":", 
									labelLocation:_LEFT_, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								},	
								{ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled+":", labelLocation:_LEFT_, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,									
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_, 
							items: [
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", 
									msgName:ZaMsg.NAD_MailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"width:250px;"
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_, 
							items: [
								{ref:ZaAccount.A_zimbraMinPwdLength, labelCssStyle:"width:250px;", 
									type:_SUPER_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMinPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMaxPwdAge, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, textFieldCssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraPasswordLocked, type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_PwdLocked,label:ZaMsg.NAD_PwdLocked+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},							
						{type:_GROUP_, 
							items: [
								{ref:ZaAccount.A_zimbraAuthTokenLifetime, labelCssStyle:"width:250px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},								
								{ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}
							]
						},
						{type:_SEPARATOR_, colSpan:"*"},
						{type:_GROUP_,
							items: [
								{ref:ZaAccount.A_isAdminAccount,labelCssStyle:"width:250px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label"}
							]
						}
			
					]
				}
			]
		}	
	);
	return xFormObject;
};
