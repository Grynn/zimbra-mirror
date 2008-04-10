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
* @class ZaCosXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaCosXFormView = function(parent, app) {
	ZaTabView.call(this, parent, app, "ZaCosXFormView");	
	this.initForm(ZaCos.myXModel,this.getMyXForm());
}

ZaCosXFormView.prototype = new ZaTabView();
ZaCosXFormView.prototype.constructor = ZaCosXFormView;
ZaTabView.XFormModifiers["ZaCosXFormView"] = new Array();
ZaCosXFormView.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaCosXFormView.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaCosXFormView.prototype.setObject =
function(entry) {
	//handle the special attributes to be displayed in xform
	entry.manageSpecialAttrs();
	
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	
	if(entry.id)
		this._containedObject.id = entry.id;
		
	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
//	this._containedObject[ZaCos.A_zimbraMailHostPoolInternal] = entry[ZaCos.A_zimbraMailHostPoolInternal].clone();
	
	if (typeof ZaDomainAdmin == "function") {
		this._containedObject[ZaCos.A2_zimbraDomainAdminMailQuotaAllowed] = entry [ZaCos.A2_zimbraDomainAdminMailQuotaAllowed];
	}
	
	var servers = entry.attrs[ZaCos.A_zimbraMailHostPool];
	if(servers != null && servers != "") {
		if (AjxUtil.isString(servers))	 {
			this._containedObject.attrs[ZaCos.A_zimbraMailHostPool] = [servers];
		} else {
			var cnt = servers.length;
			this._containedObject.attrs[ZaCos.A_zimbraMailHostPool] = [];
			for(var i = 0; i < cnt; i ++) {
				this._containedObject.attrs[ZaCos.A_zimbraMailHostPool].push(servers[i]);					
			}
		}
	} else {
		this._containedObject.attrs[ZaCos.A_zimbraMailHostPool] = null;		
	}

	
/*	if(entry.attrs[ZaCos.A_zimbraMailHostPool] instanceof Array)
		servers = entry.attrs[ZaCos.A_zimbraMailHostPool];
	else
		servers = [entry.attrs[ZaCos.A_zimbraMailHostPool]];
		
	if(servers != null) {
		var cnt = servers.length;
		this._containedObject.attrs[ZaCos.A_zimbraMailHostPool] = [];
		for(var i = 0; i < cnt; i ++) {
			this._containedObject.attrs[ZaCos.A_zimbraMailHostPool].push(servers[i]);					
		}
	} else {
		this._containedObject.attrs[ZaCos.A_zimbraMailHostPool] = null;		
	}
*/
	
	if(ZaSettings.SKIN_PREFS_ENABLED) {
		var skins = entry.attrs[ZaCos.A_zimbraAvailableSkin];
		if(skins != null && skins != "") {
			if (AjxUtil.isString(skins))	 {
				this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = [skins];
			} else {
				var cnt = skins.length;
				this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = [];
				for(var i = 0; i < cnt; i ++) {
					this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin].push(skins[i]);					
				}
			}

		} else {
			this._containedObject.attrs[ZaCos.A_zimbraAvailableSkin] = null;		
		}

		var skins = this._app.getInstalledSkins();
		if(skins == null) {
			skins = [];
		} else if (AjxUtil.isString(skins))	 {
			skins = [skins];
		}
		
		ZaCosXFormView.themeChoices.setChoices(skins);
		ZaCosXFormView.themeChoices.dirtyChoices();		
		
	}
	
	
	if(ZaSettings.ZIMLETS_ENABLED) {
		var zimlets = entry.attrs[ZaCos.A_zimbraZimletAvailableZimlets];
		if(zimlets != null && zimlets != "") {
			if (AjxUtil.isString(zimlets))	 {
				this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = [zimlets];
			} else {
				var cnt = zimlets.length;
				this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = [];
				for(var i = 0; i < cnt; i ++) {
					this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets].push(zimlets[i]);					
				}
			}
		} else
			this._containedObject.attrs[ZaCos.A_zimbraZimletAvailableZimlets] = null;		
		
		
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
		ZaCosXFormView.zimletChoices.setChoices(_tmpZimlets);
		ZaCosXFormView.zimletChoices.dirtyChoices();		
	}	

	
//	this._containedObject[ZaCos.A_zimbraMailAllServersInternal] = AjxVector.fromArray(this._app.getMailServers());
  	
  	
  	this._containedObject.globalConfig = this._app.getGlobalConfig();
  	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	this.updateTab();
}

ZaCosXFormView.gotSkins = function () {
	return ((this.parent._app.getInstalledSkins() != null) && (this.parent._app.getInstalledSkins().length > 0));
}

ZaCosXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.items = [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
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
				],cssClass:"ZaTabBar", id:"xform_tabbar"
			},
			{type:_SWITCH_, 
				items:[
					{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 1", 
					colSizes:["auto"],numCols:1,
						items:[
							{type:_ZAGROUP_,
								items:[
									{ref:ZaCos.A_name, type:_INPUT_, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, required:true, width: "20em"},
									{ref:ZaCos.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input", onChange:ZaTabView.onFormFieldChanged, width: "30em"},
									{ref:ZaCos.A_zimbraNotes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged,width: "30em"}							
								]
							}
						]
					}, 
					{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 2",
						colSizes:["auto"],numCols:1,id:"cos_form_features_tab",
						items: [
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraMajorFeature, id:"cos_form_features_major",
								items:[	
									{ref:ZaCos.A_zimbraFeatureMailEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureMailEnabled,label:ZaMsg.NAD_zimbraFeatureMailEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
									{ref:ZaCos.A_zimbraFeatureContactsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
									{ref:ZaCos.A_zimbraFeatureCalendarEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},														
									{ref:ZaCos.A_zimbraFeatureTasksEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureTaskEnabled,label:ZaMsg.NAD_FeatureTaskEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},														
									{ref:ZaCos.A_zimbraFeatureNotebookEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureNotebookEnabled,label:ZaMsg.NAD_zimbraFeatureNotebookEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureBriefcasesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,label:ZaMsg.NAD_zimbraFeatureBriefcasesEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureIMEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureIMEnabled,label:ZaMsg.NAD_zimbraFeatureIMEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureOptionsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureOptionsEnabled,label:ZaMsg.NAD_zimbraFeatureOptionsEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
									//zimbraMobile from the extension
								]
							},
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraGeneralFeature, id:"cos_form_features_general",
								items:[	
									{ref:ZaCos.A_zimbraFeatureTaggingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
									{ref:ZaCos.A_zimbraFeatureSharingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureSharingEnabled,label:ZaMsg.NAD_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureChangePasswordEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled, trueValue:"TRUE", falseValue:"FALSE",onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureSkinChangeEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,label:ZaMsg.NAD_zimbraFeatureSkinChangeEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureHtmlComposeEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},							
									{ref:ZaCos.A_zimbraFeatureShortcutAliasesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled,label:ZaMsg.NAD_zimbraFeatureShortcutAliasesEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},	
									{ref:ZaCos.A_zimbraFeatureGalEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},															
									{ref:ZaCos.A_zimbraFeatureGalAutoCompleteEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled,label:ZaMsg.NAD_zimbraFeatureGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
								]
							},			
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraMailFeature, id:"cos_form_features_mail",
								relevant: "instance.attrs[ZaCos.A_zimbraFeatureMailEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
								items:[	
										{ref:ZaCos.A_zimbraFeatureMailPriorityEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled,label:ZaMsg.NAD_zimbraFeatureMailPriorityEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeatureFlaggingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureFlaggingEnabled,label:ZaMsg.NAD_zimbraFeatureFlaggingEnabled,  trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraImapEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraPop3Enabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeaturePop3DataSourceEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraExternalPop3Enabled,label:ZaMsg.NAD_zimbraExternalPop3Enabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeatureMailForwardingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled,label:ZaMsg.NAD_zimbraFeatureMailForwardingEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeatureConversationsEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},								
										{ref:ZaCos.A_zimbraFeatureFiltersEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraFeatureOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
										{ref:ZaCos.A_zimbraFeatureNewMailNotificationEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraFeatureNewMailNotificationEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},	
										{ref:ZaCos.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled,label:ZaMsg.NAD_zimbraFeatureMailPollingIntervalPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},	
										{ref:ZaCos.A_zimbraFeatureIdentitiesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureIdentitiesEnabled,label:ZaMsg.NAD_FeatureIdentitiesEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
										
								]
							},
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraCalendarFeature, id:"cos_form_features_calendar",
								relevant: "instance.attrs[ZaCos.A_zimbraFeatureCalendarEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
								items:[	
									{ref:ZaCos.A_zimbraFeatureGroupCalendarEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled,label:ZaMsg.NAD_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
								]
							},
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraIMFeature, id:"cos_form_features_im",
								relevant: "instance.attrs[ZaCos.A_zimbraFeatureIMEnabled] == 'TRUE'", relevantBehavior: _HIDE_,
								items:[	
									{ref:ZaCos.A_zimbraFeatureInstantNotify,
									 type:_CHECKBOX_,
									 msgName:ZaMsg.NAD_zimbraFeatureInstantNotify,
									 label:ZaMsg.NAD_zimbraFeatureInstantNotify,
									 trueValue:"TRUE",
									 falseValue:"FALSE",
									 onChange:ZaTabView.onFormFieldChanged}
									
								]
							},
							{type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_zimbraSearchFeature, id:"cos_form_features_search",
								items:[	
									{ref:ZaCos.A_zimbraFeatureAdvancedSearchEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},								
									{ref:ZaCos.A_zimbraFeatureSavedSearchesEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged}
								]
							}
						]
					},
					{type:_ZATABCASE_,relevant:"instance[ZaModel.currentTab] == 3", id:"cos_for_prefs_tab",numCols:1, items :[
							{type:_SPACER_,height:"10px", colSpan: "*" },
							{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"cos_form_prefs_general_header",
								items: [
									{type:_OUTPUT_,value:ZaMsg.NAD_GeneralOptions}
								],
								cssStyle:"padding-top:5px; padding-bottom:5px"
							},
							{type:_ZA_PLAIN_GROUPER_, id:"account_prefs_general", items :[
								{ref:ZaCos.A_zimbraPrefClientType, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefClientType,
									label:ZaMsg.NAD_zimbraPrefClientType, labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefShowSearchString, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefShowSearchString,
									label:ZaMsg.NAD_zimbraPrefShowSearchString, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefMailInitialSearch, type:_TEXTFIELD_, cssClass:"admin_xform_name_input", 
									msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch, 
									labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefImapSearchFoldersEnabled, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled,
									label:ZaMsg.NAD_zimbraPrefImapSearchFoldersEnabled, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefUseKeyboardShortcuts, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_prefKeyboardShort,label:ZaMsg.NAD_prefKeyboardShort, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},									
								{ref:ZaCos.A_zimbraPrefWarnOnExit, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefWarnOnExit,label:ZaMsg.NAD_zimbraPrefWarnOnExit, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefShowSelectionCheckbox, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefShowSelectionCheckbox,label:ZaMsg.NAD_zimbraPrefShowSelectionCheckbox, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefIMAutoLogin, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefIMAutoLogin,label:ZaMsg.NAD_zimbraPrefIMAutoLogin, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraJunkMessagesIndexingEnabled, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled,
									label:ZaMsg.NAD_zimbraJunkMessagesIndexingEnabled, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								}
							]},							
							{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"cos_form_prefs_mail_header",
								items: [
									{type:_OUTPUT_,value:ZaMsg.NAD_MailOptions}
								],
								cssStyle:"padding-top:5px; padding-bottom:5px"
							},
							{type:_ZA_PLAIN_GROUPER_, id:"cos_prefs_mail_general",items :[
								{ref:ZaCos.A_zimbraPrefMessageViewHtmlPreferred, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,
									label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefDisplayExternalImages, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefDisplayExternalImages,
									label:ZaMsg.NAD_zimbraPrefDisplayExternalImages, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefGroupMailBy, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,
									label:ZaMsg.NAD_zimbraPrefGroupMailBy, labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefMailItemsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,
									label:ZaMsg.NAD_zimbraPrefMailItemsPerPage, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefMailDefaultCharset, type:_OSELECT1_,
									 msgName:ZaMsg.NAD_zimbraPrefMailDefaultCharset,label:ZaMsg.NAD_zimbraPrefMailDefaultCharset, 
									 labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,
									 labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								}
							]},							
							{type:_ZA_TOP_GROUPER_, id:"cos_prefs_mail_receiving",label:ZaMsg.NAD_MailOptionsReceiving,items :[							
								{ref:ZaCos.A_zimbraPrefMailPollingInterval, type:_LIFETIME_, 
									msgName:ZaMsg.NAD_zimbraPrefMailPollingInterval,
									label:ZaMsg.NAD_zimbraPrefMailPollingInterval+":", labelLocation:_LEFT_, 
									onChange:ZaCosXFormView.validatePollingInterval,
									labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								},
								{ref:ZaCos.A_zimbraMailMinPollingInterval, type:_LIFETIME_, 
									msgName:ZaMsg.NAD_zimbraMailMinPollingInterval, 
									label:ZaMsg.NAD_zimbraMailMinPollingInterval+":", labelLocation:_LEFT_, 
									onChange:ZaCosXFormView.validatePollingInterval,
									labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								},
								{ref:ZaCos.A_zimbraPrefOutOfOfficeCacheDuration, type:_LIFETIME_, 
									msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration, 
									label:ZaMsg.NAD_zimbraPrefOutOfOfficeCacheDuration+":", labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged,
									labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								}
							]},
							{type:_ZA_TOP_GROUPER_, id:"cos_prefs_mail_sending",borderCssClass:"LowPadedTopGrouperBorder",label:ZaMsg.NAD_MailOptionsSending,items :[							
								{ref:ZaCos.A_zimbraPrefSaveToSent, type:_CHECKBOX_, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},							
								{ref:ZaCos.A_zimbraAllowAnyFromAddress, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraAllowAnyFromAddress,label:ZaMsg.NAD_zimbraAllowAnyFromAddress, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged
								},							
								{ref:ZaCos.A_zimbraPrefComposeInNewWindow, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,
									label:ZaMsg.NAD_zimbraPrefComposeInNewWindow, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},							
								{ref:ZaCos.A_zimbraPrefComposeFormat, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
								{ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontFamily, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontFamily, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontSize, type:_OSELECT1_, msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize,
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontSize, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefHtmlEditorDefaultFontColor, type:_DWT_COLORPICKER_, msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
									height: "25px",
									label:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefForwardReplyInOriginalFormat, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,
									label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat, trueValue:"TRUE", falseValue:"FALSE",
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefMailSignatureStyle, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,
									label:ZaMsg.NAD_zimbraPrefMailSignatureStyle, onChange:ZaTabView.onFormFieldChanged, 
									trueValue:"internet", falseValue:"outlook"
								},
								{ref:ZaCos.A_zimbraMailSignatureMaxLength, type:_TEXTFIELD_, 
									msgName:ZaMsg.NAD_zimbraMailSignatureMaxLength,
									label:ZaMsg.NAD_zimbraMailSignatureMaxLength, labelLocation:_LEFT_, 
									cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}						
							]},
							{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"cos_form_prefs_contacts_header",
								items: [
									{type:_OUTPUT_,value:ZaMsg.NAD_ContactsOptions}
								],
								cssStyle:"padding-top:5px; padding-bottom:5px"
							},				
							{type:_ZA_PLAIN_GROUPER_, id:"cos_prefs_contacts_general", items :[
								{ref:ZaCos.A_zimbraPrefAutoAddAddressEnabled, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,
									label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},
								{ref:ZaCos.A_zimbraPrefGalAutoCompleteEnabled, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled,
									label:ZaMsg.NAD_zimbraPrefGalAutoCompleteEnabled, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},														
								{ref:ZaCos.A_zimbraPrefContactsPerPage, type:_OSELECT1_, msgName:ZaMsg.NAD_PrefContactsPerPage,
									label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, 
									onChange:ZaTabView.onFormFieldChanged
								}							
							]},
							{type:_GROUP_, cssClass:"ZaHeader2", colSpan: "*", id:"cos_form_prefs_calendar_header",
								items: [
									{type:_OUTPUT_,value:ZaMsg.NAD_CalendarOptions}
								],
								cssStyle:"padding-top:5px; padding-bottom:5px"
							},					

							{type:_ZA_PLAIN_GROUPER_, id:"cos_prefs_calendar_general",items :[
								{ref:ZaCos.A_zimbraPrefTimeZoneId, type:_OSELECT1_,
									 msgName:ZaMsg.NAD_zimbraPrefTimeZoneId,label:ZaMsg.NAD_zimbraPrefTimeZoneId+":", 
									 labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,
									 labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								},
								{ref:ZaCos.A_zimbraPrefCalendarApptReminderWarningTime, type:_OSELECT1_,
									 msgName:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime,label:ZaMsg.NAD_zimbraPrefCalendarApptReminderWarningTime+":", 
									 labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged,
									 labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								},							
								{ref:ZaCos.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,
									labelCssStyle:"white-space:normal;",nowrap:false,labelWrap:true
								},							

								{ref:ZaCos.A_zimbraPrefCalendarUseQuickAdd, type:_CHECKBOX_, msgName:ZaMsg.NAD_useQuickAdd,
									label:ZaMsg.NAD_useQuickAdd, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								},							
								{ref:ZaCos.A_zimbraPrefUseTimeZoneListInCalendar, type:_CHECKBOX_, 
									msgName:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar,
									label:ZaMsg.NAD_zimbraPrefUseTimeZoneListInCalendar, trueValue:"TRUE", falseValue:"FALSE", 
									onChange:ZaTabView.onFormFieldChanged
								}
							]}										
						]
					},
					{type:_ZATABCASE_, numCols:1, relevant:"instance[ZaModel.currentTab]==4", 
						items: [
							{type:_ZAGROUP_,items:[							{	
								ref:ZaCos.A_zimbraPrefSkin, type:_OSELECT1_, 
								msgName:ZaMsg.NAD_zimbraPrefSkin,label:ZaMsg.NAD_zimbraPrefSkin, labelLocation:_LEFT_, 
								onChange:ZaTabView.onFormFieldChanged,choices:this._app.getInstalledSkins(),
								relevant:"ZaCosXFormView.gotSkins.call(this)"
							}]},
							{type:_ZAGROUP_, numCols:1,colSizes:["auto"], 
								items: [
									{type:_ZIMLET_SELECT_RADIO_,
										selectRef:ZaCos.A_zimbraAvailableSkin, 
										ref:ZaCos.A_zimbraAvailableSkin, 
										choices:ZaCosXFormView.themeChoices,
										onChange: ZaTabView.onFormFieldChanged,
										relevant:("instance[ZaModel.currentTab]==4"),
										relevantBehavior:_HIDE_,
										radioBoxLabel1:ZaMsg.COS_DontLimitThemes,
										radioBoxLabel2:ZaMsg.COS_LimitThemesTo										
									}
								]
							}							
						]
					},
					{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab]==5", 
						items: [
							{type:_ZAGROUP_, numCols:1,colSizes:["auto"], 
								items: [
									{type:_ZIMLET_SELECT_,
										selectRef:ZaCos.A_zimbraZimletAvailableZimlets, 
										ref:ZaCos.A_zimbraZimletAvailableZimlets, 
										choices:ZaCosXFormView.zimletChoices,
										onChange: ZaTabView.onFormFieldChanged,
										relevant:("instance[ZaModel.currentTab]==5"),
										relevantBehavior:_HIDE_,
										selectLabel:ZaMsg.COS_COSLimitZimletsTo
									}
								]
							}							
						]
					},
					{type:_ZATABCASE_, numCols:1, relevant:"instance[ZaModel.currentTab]==6", 
						items: [
							{type:_ZAGROUP_, numCols:1,colSizes:["auto"], 
								items: [
									{type:_ZIMLET_SELECT_RADIO_,
										selectRef:ZaCos.A_zimbraMailHostPool, 
										ref:ZaCos.A_zimbraMailHostPool, 
										choices:this._app.getServerIdListChoices(),
										onChange: ZaTabView.onFormFieldChanged,
										relevant:("instance[ZaModel.currentTab]==6"),
										relevantBehavior:_HIDE_,
										radioBoxLabel1:ZaMsg.ServerPool_Donotlimit,
										radioBoxLabel2:ZaMsg.COS_LimitServersTo										
									}
								]
							}	
						]
					},					
					/*,					
					{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab]==6", numCols:2, colSizes:["10px", "400px"],
						items: [
							{type:_SPACER_},
							{type:_CELLSPACER_},	
							{ sourceRef: ZaCos.A_zimbraMailAllServersInternal,
					  	  	  ref: ZaCos.A_zimbraMailHostPoolInternal, type: _DWT_CHOOSER_,
							  listCssClass: "DwtChooserListView ZaCosServerPool", sorted: true,
					  	  	  onChange: ZaTabView.onFormFieldChanged, widgetClass:ZaCosServerPoolChooser,
					  	  	  listSize:"90%", forceUpdate:true,
					  	  	  listHeight: "500px",
					  	  	  updateElement:function(value) {
					  	  	  		this.updateWidget(value, true, function() {return this.id; });	
					  	  	  }
					  	  	}
						]
					},*/
					{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab]==7", id:"cos_form_advanced_tab",
						numCols:1,
						items: [
							{type:_ZA_TOP_GROUPER_, id:"cos_attachment_settings",
								label:ZaMsg.NAD_AttachmentsGrouper,
								items :[						
									{ref:ZaCos.A_zimbraAttachmentsBlocked, type:_CHECKBOX_,  msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label",  align:_LEFT_}
								]
							},
							{type:_ZA_TOP_GROUPER_, id:"cos_quota_settings",
								label:ZaMsg.NAD_QuotaGrouper,
								items: [											
									{ref:ZaCos.A_zimbraMailQuota, type:_TEXTFIELD_, msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraContactMaxNumEntries, type:_INPUT_, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",  onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraQuotaWarnPercent, type:_INPUT_, msgName:ZaMsg.NAD_QuotaWarnPercent,label:ZaMsg.NAD_QuotaWarnPercent, labelLocation:_LEFT_, cssClass:"admin_xform_number_input",  onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraQuotaWarnInterval, type:_LIFETIME_, msgName:ZaMsg.NAD_QuotaWarnInterval,label:ZaMsg.NAD_QuotaWarnInterval, labelLocation:_LEFT_,  onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraQuotaWarnMessage, type:_TEXTAREA_, msgName:ZaMsg.NAD_QuotaWarnMessage,label:ZaMsg.NAD_QuotaWarnMessage, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", onChange:ZaTabView.onFormFieldChanged,width: "30em"}
								]
							},
							{type:_ZA_TOP_GROUPER_,id:"cos_password_settings",
								label:ZaMsg.NAD_PasswordGrouper,
								items: [
									{ type: _DWT_ALERT_,
									  containerCssStyle: "padding-bottom:0px",
									  style: DwtAlert.WARNING,
									  iconVisible: false, 
									  content: ZaMsg.Alert_InternalPassword
									},							
									{ref:ZaCos.A_zimbraPasswordLocked, type:_CHECKBOX_, 
										msgName:ZaMsg.NAD_PwdLocked,
										label:ZaMsg.NAD_PwdLocked, 
										trueValue:"TRUE", falseValue:"FALSE", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraMinPwdLength, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdLength, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input",onChange:ZaTabView.onFormFieldChanged},

									{ref:ZaCos.A_zimbraPasswordMinUpperCaseChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars,label:ZaMsg.NAD_zimbraPasswordMinUpperCaseChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinLowerCaseChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars,label:ZaMsg.NAD_zimbraPasswordMinLowerCaseChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinPunctuationChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinPunctuationChars,label:ZaMsg.NAD_zimbraPasswordMinPunctuationChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraPasswordMinNumericChars, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPasswordMinNumericChars,label:ZaMsg.NAD_zimbraPasswordMinNumericChars+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},

									{ref:ZaCos.A_zimbraMinPwdAge, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMaxPwdAge, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraEnforcePwdHistory, type:_TEXTFIELD_, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", onChange:ZaTabView.onFormFieldChanged}
								]
							},
							{type:_ZA_TOP_GROUPER_, id:"cos_password_lockout_settings",
								label:ZaMsg.NAD_FailedLoginGrouper,
								items :[

									{ref:ZaCos.A_zimbraPasswordLockoutEnabled, type:_CHECKBOX_, 
										msgName:ZaMsg.NAD_zimbraPasswordLockoutEnabled,
										label:ZaMsg.NAD_zimbraPasswordLockoutEnabled, 
										trueValue:"TRUE", falseValue:"FALSE", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraPasswordLockoutMaxFailures, type:_TEXTFIELD_, 
										relevant: "instance.attrs[ZaCos.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
									 	relevantBehavior: _DISABLE_,
										label:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutMaxFailuresSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutMaxFailures,
										labelLocation:_LEFT_, 
										cssClass:"admin_xform_number_input", 
										onChange:ZaTabView.onFormFieldChanged
									},
									{ref:ZaCos.A_zimbraPasswordLockoutDuration, type:_LIFETIME_, 
										relevant: "instance.attrs[ZaCos.A_zimbraPasswordLockoutEnabled] == 'TRUE'",
										relevantBehavior: _DISABLE_,
										label:ZaMsg.NAD_zimbraPasswordLockoutDuration+":",
										subLabel:ZaMsg.NAD_zimbraPasswordLockoutDurationSub,
										msgName:ZaMsg.NAD_zimbraPasswordLockoutDuration,
										labelLocation:_LEFT_, 
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
										labelCssStyle:"white-space:normal;",
										nowrap:false,labelWrap:true
									}																		
								]
							},
							{type:_ZA_TOP_GROUPER_, 
								label:ZaMsg.NAD_TimeoutGrouper,
								items: [														
									{ref:ZaCos.A_zimbraAdminAuthTokenLifetime, type:_LIFETIME_, msgName:ZaMsg.NAD_AdminAuthTokenLifetime,label:ZaMsg.NAD_AdminAuthTokenLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},																										
									{ref:ZaCos.A_zimbraAuthTokenLifetime, type:_LIFETIME_, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},																		
									{ref:ZaCos.A_zimbraMailIdleSessionTimeout, type:_LIFETIME_, msgName:ZaMsg.NAD_MailIdleSessionTimeout,label:ZaMsg.NAD_MailIdleSessionTimeout+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}
								]
							},
                            {type:_ZA_TOP_GROUPER_,
								label:ZaMsg.NAD_MailRetentionGrouper,
								items: [
                                    { type: _DWT_ALERT_,
									  containerCssStyle: "padding-bottom:0px",
									  style: DwtAlert.WARNING,
									  iconVisible: false,
									  content: ZaMsg.Alert_MailRetention
									},
                                    {ref:ZaCos.A_zimbraMailMessageLifetime, type:_LIFETIME2_, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailTrashLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged},
									{ref:ZaCos.A_zimbraMailSpamLifetime, type:_LIFETIME1_, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime, labelLocation:_LEFT_, onChange:ZaTabView.onFormFieldChanged}
								]
							},
                            {type:_ZA_TOP_GROUPER_, 
								label:ZaMsg.NAD_InteropGrouper,
								items: [
									{ref:ZaCos.A_zimbraFreebusyExchangeUserOrg, type:_TEXTFIELD_,
                                        msgName:ZaMsg.NAD_ExchangeUserGroup, width: "250px",
                                        label:ZaMsg.NAD_ExchangeUserGroup,labelLocation:_LEFT_,
                                        onChange:ZaTabView.onFormFieldChanged
                                    }
								]
							} ,
                            {type: _SPACER_ , height: "10px" }  //add some spaces at the bottom of the page
                        ]
					}
				]
			}	
		];
};
ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaCosXFormView.myXFormModifier);

ZaCosXFormView.validatePollingInterval =
function (value, event, form) {
	DBG.println(AjxDebug.DBG3, "The polling interval = " + value);
	var instance = form.getInstance ();
	this.setInstanceValue(value);
	var prefPollingInterval = instance.attrs[ZaCos.A_zimbraPrefMailPollingInterval] ;
	var minPollingInterval = instance.attrs[ZaCos.A_zimbraMailMinPollingInterval] ;
	var prefPollingIntervalItem = form.getItemsById (ZaCos.A_zimbraPrefMailPollingInterval)[0];
	try {
		if (ZaUtil.getLifeTimeInSeconds(prefPollingInterval) < ZaUtil.getLifeTimeInSeconds(minPollingInterval)){
			prefPollingIntervalItem.setError (ZaMsg.tt_mailPollingIntervalError + minPollingInterval) ;
			form.parent.setDirty(false);	
		}else{
			prefPollingIntervalItem.clearError();	
			form.parent.setDirty(true);	
		}
	}catch (e){
		prefPollingIntervalItem.setError (e.message);
		form.parent.setDirty(false);
	}
}

