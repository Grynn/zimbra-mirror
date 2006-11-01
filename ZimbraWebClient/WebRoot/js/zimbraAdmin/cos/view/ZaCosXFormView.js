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
* @class ZaCosXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaCosXFormView (parent, app) {
	ZaTabView.call(this, parent, app, "ZaCosXFormView");	
	this.initForm(ZaCos.myXModel,this.getMyXForm());
}

ZaCosXFormView.prototype = new ZaTabView();
ZaCosXFormView.prototype.constructor = ZaCosXFormView;
ZaTabView.XFormModifiers["ZaCosXFormView"] = new Array();

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
	this._containedObject[ZaCos.A_zimbraMailHostPoolInternal] = entry[ZaCos.A_zimbraMailHostPoolInternal].clone();
	
	if (typeof ZaDomainAdmin == "function") {
		this._containedObject[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = entry [ZaCos.A2_zimbraDomainAdminMailQuotaAllowed];
	}
	
	var skins = entry.attrs[ZaCos.A_zimbraAvailableSkin];
	_tmpSkins = [];
	if(skins == null) {
		skins = [];
	} else if (AjxUtil.isString(skins))	 {
		skins = [skins];
	}
	
	//convert strings to objects
	for(var i=0; i<skins.length; i++) {
		var skin = skins[i];
		_tmpSkins[i] = new String(skin);
		_tmpSkins[i].id = "id_"+skin;
	}
	this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = _tmpSkins;
	

	var skins = this._app.getInstalledSkins();
	var _tmpSkins = [];
	if(skins == null) {
		skins = [];
	} else if (AjxUtil.isString(skins))	 {
		skins = [skins];
	}

	//convert strings to objects
	var cnt = skins.length;
	for(var i=0; i<cnt; i++) {
		var skin = skins[i];
		_tmpSkins[i] = new String(skin);
		_tmpSkins[i].id = "id_"+skin;
	}
	this._containedObject[ZaCos.A_zimbraInstalledSkinPool] = _tmpSkins;
	
	var zimlets = entry.attrs[ZaCos.A_zimbraZimletAvailableZimlets];
	var _tmpZimlets = [];
	if(zimlets == null) {
		zimlets = [];
	} else if (AjxUtil.isString(zimlets))	 {
		zimlets = [zimlets];
	}
	
	//convert strings to objects
	var cnt = zimlets.length;
	for(var i=0; i<cnt; i++) {
		var zimlet = zimlets[i];
		_tmpZimlets[i] = new String(zimlet);
		_tmpZimlets[i].id = "id_"+zimlet;
	}
	this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = _tmpZimlets;

	var zimlets = ZaZimlet.getAll(this._app, "extension");
	_tmpZimlets = [];
	if(zimlets == null) {
		zimlets = [];
	} 
	
	if(zimlets instanceof ZaItemList || zimlets instanceof AjxVector)
		zimlets = zimlets.getArray();
	
	var cnt = zimlets.length;
	//convert strings to objects	
	for(var i=0; i<cnt; i++) {
		var zimlet = zimlets[i];
		_tmpZimlets[i] = new String(zimlet.name);
		_tmpZimlets[i].id = "id_"+zimlet.name;
	}
	this._containedObject[ZaCos.A_zimbraInstalledZimletPool] = _tmpZimlets;
	
	this._containedObject[ZaCos.A_zimbraMailAllServersInternal] = AjxVector.fromArray(this._app.getMailServers());
  	
  	
  	this._containedObject.globalConfig = this._app.getGlobalConfig();
  	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
}

ZaCosXFormView.gotSkins = function () {
	return ((this.parent._app.getInstalledSkins() != null) && (this.parent._app.getInstalledSkins().length > 0));
}

ZaCosXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.items = [
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
			},	
			{type:_TAB_BAR_,  ref:ZaModel.currentTab,
				choices:[
					{value:1, label:ZaMsg.TABT_GeneralPage},
					{value:2, label:ZaMsg.TABT_Features},
					{value:3, label:ZaMsg.TABT_Preferences},					
					{value:4, label:ZaMsg.TABT_Themes},						
					{value:5, label:ZaMsg.TABT_Zimlets},											
					{value:6, label:ZaMsg.TABT_ServerPool},										
					{value:7, label:ZaMsg.TABT_Advanced}										
				],cssClass:"ZaTabBar"
			},
			{type:_SWITCH_, 
				items:[
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 1", 
					colSizes:["150px","*"],
						items:[
							{ref:ZaCos.A_name, type:_INPUT_, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, required:true, width: "20em"},
							{ref:ZaCos.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, width: "30em"},
							{ref:ZaCos.A_zimbraNotes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged,width: "30em"}							
						]
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2",
						colSizes:["300px","*"],
						items: [
							{ref:ZaCos.A_zimbraFeatureContactsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraFeatureCalendarEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label",align:_LEFT_},														
							{ref:ZaCos.A_zimbraFeatureTaggingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureConversationsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureFiltersEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraFeatureGalEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraImapEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPop3Enabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureSharingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,label:ZaMsg.NAD_zimbraFeatureSharingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureMailForwardingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureNotebookEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,label:ZaMsg.NAD_zimbraFeatureNotebookEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraFeatureNewMailNotificationEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},	
							{ref:ZaCos.A_zimbraFeatureSkinChangeEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,label:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_}							
						]
					},
					{type:_CASE_,relevant:"instance[ZaModel.currentTab] == 3",
					colSizes:["300px","*"],
						items :[
							{ref:ZaCos.A_zimbraPrefSaveToSent, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefGroupMailBy, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
							{ref:ZaCos.A_zimbraPrefComposeInNewWindow, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefComposeFormat, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailSignatureStyle, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged, trueValue:"internet", falseValue:"outlook",labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefGalAutoCompleteEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},														
							{type:_SEPARATOR_},	
							{ref:ZaCos.A_zimbraPrefContactsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailItemsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailInitialSearch, type:_TEXTFIELD_, cssClass:"admin_xform_name_input", msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefMailPollingInterval, type:_LIFETIME_, msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
							{ref:ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},							
							{ref:ZaCos.A_zimbraPrefShowSearchString, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_CHECKBOX_, msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefCalendarUseQuickAdd, type:_CHECKBOX_, msgName:ZaMsg.NAD_useQuickAdd,label:ZaMsg.NAD_useQuickAdd, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},							
							{ref:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_},
							{type:_SEPARATOR_},	
							{ref:ZaCos.A_zimbraPrefUseKeyboardShortcuts, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefKeyboardShort,label:ZaMsg.NAD_prefKeyboardShort, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==4", numCols:2, colSizes:["10px", "400px"],
						items: [
							{type:_CELLSPACER_},
							{	sourceRef: ZaCos.A_zimbraInstalledSkinPool, ref:ZaCos.A_zimbraAvailableSkin, 
								type:_DWT_CHOOSER_, sorted: true, 
					  	  		onChange: ZaTabView.onFormFieldChanged,
					  	  	  	listSize:"90%", forceUpdate:true,
					  	  	  	widgetClass:ZaSkinPoolChooser,
					  	  	  	relevant:"ZaCosXFormView.gotSkins.call(this)",
					  	  	  	updateElement:function(value) {
					  	  	  		this.updateWidget(value, true, function() {return this.id; });	
					  	  	  	}
					  	  	},
							{type:_CELLSPACER_},					  	  	
							{type:_GROUP_,items:[
								{	
									ref:ZaCos.A_zimbraPrefSkin, type:_OSELECT1_, 
									msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged,choices:this._app.getInstalledSkins(),
									relevant:"ZaCosXFormView.gotSkins.call(this)"
								}					  	  							
							]}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==5", numCols:2, colSizes:["10px", "400px"],
						items: [
							{type:_CELLSPACER_},
							{	sourceRef: ZaCos.A_zimbraInstalledZimletPool, ref:ZaCos.A_zimbraZimletAvailableZimlets, 
								type:_DWT_CHOOSER_, sorted: true, 
					  	  		onChange: ZaTabView.onFormFieldChanged,
					  	  	  	listSize:"90%", forceUpdate:true,
					  	  	  	widgetClass:ZaZimletPoolChooser,
					  	  	  	updateElement:function(value) {
					  	  	  		this.updateWidget(value, true, function() {return this.id; });	
					  	  	  	}
					  	  	}
						]
					},					
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==6", numCols:2, colSizes:["10px", "400px"],
						items: [
							{type:_CELLSPACER_},
							{ sourceRef: ZaCos.A_zimbraMailAllServersInternal,
					  	  	  ref: ZaCos.A_zimbraMailHostPoolInternal, type: _DWT_CHOOSER_,
							  listCssClass: "DwtChooserListView ZaCosServerPool", sorted: true,
					  	  	  onChange: ZaTabView.onFormFieldChanged, widgetClass:ZaCosServerPoolChooser,
					  	  	  listSize:"90%", forceUpdate:true,
					  	  	  updateElement:function(value) {
					  	  	  		this.updateWidget(value, true, function() {return this.id; });	
					  	  	  }
					  	  	}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab]==7", id:"cos_form_advanced_tab",
						numCols:1,
						items: [
							{type:_GROUP_, id:"cos_attachment_settings",
								items :[						
									{ref:ZaCos.A_zimbraAttachmentsBlocked, type:_CHECKBOX_,  msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", labelCssStyle:"width:250px;", align:_LEFT_},
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, id:"cos_quota_settings",
								items: [											
									{ref:ZaCos.A_zimbraMailQuota, type:_TEXTFIELD_, msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraContactMaxNumEntries, type:_INPUT_, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},
								]
							},
							
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_,id:"cos_password_settings",
								items: [							
									{ref:ZaCos.A_zimbraMinPwdLength, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdLength, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},

									{ref:ZaCos.A_zimbraPasswordMinUpperCaseChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,label:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinLowerCaseChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,label:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinPunctuationChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,label:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinNumericChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,label:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},
									
									{ref:ZaCos.A_zimbraMinPwdAge, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdAge, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraEnforcePwdHistory, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, id:"cos_password_lockout_settings",
								items :[
									{ref:ZaCos.A_zimbraPasswordLocked, type:_CHECKBOX_, 
										msgName:ZaMsg.NAD_PwdLocked,
										label:ZaMsg.NAD_PwdLocked, labelLocation:_LEFT_, 
										trueValue:"TRUE", falseValue:"FALSE", 
										onChange:ZaTabView.onFormFieldChanged,
										labelCssClass:"xform_label",labelCssStyle:"width:250px;", align:_LEFT_
									},
									{ref:ZaCos.A_zimbraPasswordLockoutEnabled, type:_CHECKBOX_, 
										msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,
										label:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
										labelLocation:_LEFT_, align:_LEFT_, 
										labelCssClass:"xform_label", labelCssStyle:"width:250px;",
										trueValue:"TRUE", falseValue:"FALSE", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraPasswordLockoutMaxFailures, type:_TEXTFIELD_, 
										relevant: "instance.attrs[ZaCos.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
									 	relevantBehavior: _DISABLE_,
										label:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
										labelLocation:_LEFT_, labelCssStyle:"width:250px;",
										cssClass:"admin_xform_number_input", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraPasswordLockoutDuration, type:_LIFETIME_, 
										relevant: "instance.attrs[ZaCos.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
										relevantBehavior: _DISABLE_,
										label:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
										labelLocation:_LEFT_, labelCssStyle:"width:250px;",
										textFieldCssClass:"admin_xform_number_input", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraPasswordLockoutFailureLifetime, type:_LIFETIME_, 
										relevant: "instance.attrs[ZaCos.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
										relevantBehavior: _DISABLE_,								
										label:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetimeSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutFailureLifetime,
										labelLocation:_LEFT_, 
										textFieldCssClass:"admin_xform_number_input", 
										onChange:ZaTabView.onFormFieldChanged,
										labelCssStyle:"width:250px;white-space:normal;",
										nowrap:false,labelWrap:true
									}																		
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, 
								items: [														
									{ref:ZaCos.A_zimbraAuthTokenLifetime, type:_LIFETIME_, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_, labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},																		
									{ref:ZaCos.A_zimbraMailIdleSessionTimeout, type:_LIFETIME_, msgName:ZaMsg.NAD_MailIdleSessionTimeout,label:ZaMsg.NAD_MailIdleSessionTimeout+":",labelLocation:_LEFT_, labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},																											
									{ref:ZaCos.A_zimbraMailMessageLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_,labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailTrashLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_, labelCssStyle:"width:250px;",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailSpamLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime, labelLocation:_LEFT_,labelCssStyle:"width:250px;", onChange:ZaTabView.onFormFieldChanged}
								]
							}
						]
					}
				]
			}	
		];
};
ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaCosXFormView.myXFormModifier);