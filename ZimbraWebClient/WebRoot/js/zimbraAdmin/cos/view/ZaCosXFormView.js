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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ZaCosXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaCosXFormView (parent, app) {
	ZaTabView.call(this, parent, app);	
	
	this.initForm(ZaCos.myXModel,this.getMyXForm());
}

ZaCosXFormView.prototype = new ZaTabView();
ZaCosXFormView.prototype.constructor = ZaCosXFormView;

/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaCosXFormView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	if(entry.id)
		this._containedObject.id = entry.id;
		
	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	this._containedObject[ZaCos.A_zimbraMailHostPoolInternal] = entry[ZaCos.A_zimbraMailHostPoolInternal];
	this._containedObject[ZaCos.A_zimbraMailAllServersInternal] = this._app.getMailServers();
  	this._containedObject.globalConfig = this._app.getGlobalConfig();
  	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
}


ZaCosXFormView.prototype.getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;overflow:auto;",
		items: [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
						items: [
							{type:_AJX_IMAGE_, src:"COS_32", label:null},
							{type:_OUTPUT_, ref:ZaCos.A_name, label:null,cssClass:"AdminTitle", rowSpan:2},				
							{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
						]
					}
				],
				cssStyle:"padding-top:5px; padding-left:2px; padding-bottom:5px"
			},	/*		
			{type:_GROUP_, cssClass:"AdminTitleBar", colSpan: "*", 
				items: [
					{type:_OUTPUT_, ref:ZaCos.A_name, label:ZaMsg.NAD_ClassOfService+":"},
					{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
				]
			},*/
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,
				choices:[
					{value:1, label:ZaMsg.TABT_GeneralPage},
					{value:2, label:ZaMsg.TABT_Features},
					{value:3, label:ZaMsg.TABT_Preferences},					
					{value:4, label:ZaMsg.TABT_ServerPool},										
					{value:5, label:ZaMsg.TABT_Advanced}										
				],cssClass:"ZaTabBar"
			},
			{type:_SWITCH_, 
				items:[
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 1", 
						items:[
							{ref:ZaCos.A_name, type:_INPUT_, msgName:ZaMsg.NAD_Name,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, required:true, width: "20em"},
							{ref:ZaCos.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, width: "30em"},
							{ref:ZaCos.A_zimbraNotes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes+":", labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged,width: "30em"}							
						]
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2",
						items: [
							{ref:ZaCos.A_zimbraFeatureContactsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraFeatureCalendarEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},														
							{ref:ZaCos.A_zimbraFeatureTaggingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureConversationsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureFiltersEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraFeatureGalEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraImapEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPop3Enabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_}														
						]
					},
					{type:_CASE_,relevant:"instance[ZaModel.currentTab] == 3",
						items :[
							{ref:ZaCos.A_zimbraPrefSaveToSent, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefGroupMailBy, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
							{ref:ZaCos.A_zimbraPrefComposeInNewWindow, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefComposeFormat, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailSignatureStyle, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, trueValue:"internet", falseValue:"outlook",labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{type:_SEPARATOR_},	
							{ref:ZaCos.A_zimbraPrefContactsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailItemsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailInitialSearch, type:_TEXTFIELD_, cssClass:"admin_xform_name_input", msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailPollingInterval, type:_LIFETIME_, msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefShowSearchString, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==4",
						items: [
							{ sourceRef: ZaCos.A_zimbraMailAllServersInternal,
					  	  	  ref: ZaCos.A_zimbraMailHostPoolInternal, type: _DWT_ADD_REMOVE_,
							  listCssClass: "DwtAddRemoveListView ZaGlobalAttachExt", sorted: true,
					  	  	  onChange: ZaTabView.onFormFieldChanged,
					  	  	  forceUpdate:true
					  	  	}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==5",
						items: [
							{type:_GROUP_, numCols:2,
								items :[
									{ref:ZaCos.A_zimbraAttachmentsBlocked, type:_CHECKBOX_,  msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
									{ref:ZaCos.A_zimbraAttachmentsViewInHtmlOnly, type:_CHECKBOX_, 
										msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,label:ZaMsg.NAD_AttachmentsViewInHtmlOnly+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_,
										relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
										relevantBehavior:_HIDE_
									},	
									{ref:ZaCos.A_zimbraAttachmentsIndexingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_,
										relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
										relevantBehavior:_HIDE_
									},
									{ref:ZaCos.A_zimbraMailQuota, type:_TEXTFIELD_, msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraContactMaxNumEntries, type:_INPUT_, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMinPwdLength, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdLength, type:_INPUT_, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMinPwdAge, type:_INPUT_, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdAge, type:_INPUT_, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraEnforcePwdHistory, type:_INPUT_, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordLocked, type:_CHECKBOX_, msgName:ZaMsg.NAD_PwdLocked,label:ZaMsg.NAD_PwdLocked+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
									{ref:ZaCos.A_zimbraAuthTokenLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},																		
									{ref:ZaCos.A_zimbraMailMessageLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailTrashLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailSpamLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}
								]
							}
						]
					}
				]
			}	
		]
	};
	return xFormObject;
};