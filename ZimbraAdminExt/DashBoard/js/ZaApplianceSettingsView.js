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
* @class ZaApplianceSettingsView
* @contructor
* @param parent
* @param entry
* @author Greg Solovyev
**/
ZaApplianceSettingsView = function(parent, entry) {
	ZaTabView.call(this, parent, "ZaApplianceSettingsView");
	this.TAB_INDEX = 0;	
	this.initForm(ZaApplianceSettings.myXModel,this.getMyXForm(entry), null);
}

ZaApplianceSettingsView.prototype = new ZaTabView();
ZaApplianceSettingsView.prototype.constructor = ZaApplianceSettingsView;
ZaTabView.XFormModifiers["ZaApplianceSettingsView"] = new Array();

ZaApplianceSettingsView.prototype.getTitle =
function () {
	return ZaMsg.GlobalConfig_view_title;
}

ZaApplianceSettingsView.onRepeatRemove =
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}

ZaApplianceSettingsView.prototype.getTabIcon =
function () {
	return "GlobalSettings";
}

ZaApplianceSettingsView.prototype.getTabTitle =
function () {
	return this.getTitle();
}

ZaApplianceSettingsView.prototype.getTabToolTip =
function () {
	return this.getTitle ();
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaItem object to display
**/
ZaApplianceSettingsView.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	this._containedObject.type = entry.type;
	this._containedObject.name = entry.name;
	
	if(entry.id)
		this._containedObject.id = entry.id;
	
	if(entry.rights)
		this._containedObject.rights = entry.rights;
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;
		
	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = [].concat(entry.attrs[a]);
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject[ZaApplianceSettings.license] = {};
	if(entry[ZaApplianceSettings.license] && entry[ZaApplianceSettings.license].attrs) {
		
		for (var a in entry[ZaApplianceSettings.license].attrs) {
			if(entry[ZaApplianceSettings.license].attrs[a] instanceof Array) {
				this._containedObject[ZaApplianceSettings.license][a] = [].concat(entry[ZaApplianceSettings.license].attrs[a]);
			} else {
				this._containedObject[ZaApplianceSettings.license][a] = entry[ZaApplianceSettings.license].attrs[a];
			}
		}	
	}
	
	if(entry[ZaApplianceSettings.A_server]) {
		this._containedObject[ZaApplianceSettings.A_server] = entry[ZaApplianceSettings.A_server];
	}
	if(entry[ZaApplianceSettings.A_certs]) {
		this._containedObject[ZaApplianceSettings.A_certs] = entry[ZaApplianceSettings.A_certs];
	}
	this._containedObject[ZaApplianceSettings.license][ZaApplianceLicense.InstallStatusCode] = 0;
	this._containedObject[ZaApplianceSettings.license][ZaApplianceLicense.InstallStatusMsg] = "";
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);
	
	this.formDirtyLsnr = new AjxListener(ZaApp.getInstance().getCurrentController(), ZaXFormViewController.prototype.handleXFormChange);
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, this.formDirtyLsnr);
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, this.formDirtyLsnr);	
	
	this.updateTab();
}

ZaApplianceSettingsView.blockedExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, null);
	}
}

ZaApplianceSettingsView.commonExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, null);
	}
}

ZaApplianceSettingsView.shouldEnableRemoveAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension)));
}

ZaApplianceSettingsView.shouldEnableRemoveButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection)));
}

ZaApplianceSettingsView.shouldEnableAddButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection)));
}

ZaApplianceSettingsView.shouldEnableAddAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension)));
}

ZaApplianceSettingsView.removeExt = function () {
	var blockedExtArray = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var selectedExtArray = this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection);
	var newBlockedExtArray = AjxUtil.arraySubstract(blockedExtArray,selectedExtArray);
	this.setInstanceValue(newBlockedExtArray,ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);	
}

ZaApplianceSettingsView.removeAllExt = function () {
	this.setInstanceValue([],ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.setInstanceValue([],ZaGlobalConfig.A2_blocked_extension_selection);
	this.getForm().parent.setDirty(true);	
}

ZaApplianceSettingsView.addCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);	
	this.getForm().parent.setDirty(true);	
}

ZaApplianceSettingsView.addAllCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);			
}

ZaApplianceSettingsView.addNewExt = function() {
	var extStr = this.getInstanceValue(ZaGlobalConfig.A_zimbraNewExtension);
	if(AjxUtil.isEmpty(extStr))
		return;
		
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;	
	var newExtArr = extStr.split(/[\s,;]+/);
	if(AjxUtil.isEmpty(newExtArr))
		return;
	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.setInstanceValue(null,ZaGlobalConfig.A_zimbraNewExtension);
	this.getForm().parent.setDirty(true);
}

ZaApplianceSettingsView.GENERAL_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, ZaGlobalConfig.A_zimbraFileUploadMaxSize, ZaGlobalConfig.A_zimbraGalMaxResults, ZaGlobalConfig.A_zimbraDefaultDomainName,ZaGlobalConfig.A_zimbraScheduledTaskNumThreads];
ZaApplianceSettingsView.GENERAL_TAB_RIGHTS = [];

ZaApplianceSettingsView.ATTACHMENTS_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension];
ZaApplianceSettingsView.ATTACHMENTS_TAB_RIGHTS = [];

ZaApplianceSettingsView.MTA_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMtaAuthEnabled, ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, ZaGlobalConfig.A_zimbraSmtpHostname,
	ZaGlobalConfig.A_zimbraSmtpPort, ZaGlobalConfig.A_zimbraMtaRelayHost, ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled];
ZaApplianceSettingsView.MTA_TAB_RIGHTS = [];

ZaApplianceSettingsView.IMAP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraImapServerEnabled, ZaGlobalConfig.A_zimbraImapSSLServerEnabled, ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraImapNumThreads];
ZaApplianceSettingsView.IMAP_TAB_RIGHTS = [];

ZaApplianceSettingsView.POP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraPop3ServerEnabled, ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraPop3NumThreads];
ZaApplianceSettingsView.POP_TAB_RIGHTS = [];

ZaApplianceSettingsView.ASAV_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSpamKillPercent, ZaGlobalConfig.A_zimbraSpamTagPercent, ZaGlobalConfig.A_zimbraSpamSubjectTag,
	ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, ZaGlobalConfig.A_zimbraVirusWarnRecipient];
ZaApplianceSettingsView.ASAV_TAB_RIGHTS = [];

ZaApplianceSettingsView.INTEROP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraFreebusyExchangeURL, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername,
	ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg];
ZaApplianceSettingsView.INTEROP_TAB_RIGHTS = [ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT];

ZaApplianceSettingsView.SKIN_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSkinForegroundColor, ZaGlobalConfig.A_zimbraSkinBackgroundColor,ZaGlobalConfig.A_zimbraSkinSecondaryColor,
	ZaGlobalConfig.A_zimbraSkinSelectionColor, ZaGlobalConfig.A_zimbraSkinLogoURL, ZaGlobalConfig.A_zimbraSkinLogoLoginBanner, ZaGlobalConfig.A_zimbraSkinLogoAppBanner ];

ZaApplianceSettingsView.SKIN_TAB_RIGHTS = [];

ZaApplianceSettingsView.myXFormModifier = function(xFormObject, entry) {
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6, _tab7, _tab8;
	
    var tabBarChoices = [];
    var switchItems = [];
	_tab1 = ++this.TAB_INDEX;
	var case1 = {type:_ZATABCASE_, caseKey:_tab1, colSizes:["auto"],numCols:1,
	items:[
		{type:_ZAGROUP_,
			items:[
				{ref:ZaGlobalConfig.A_zimbraDefaultDomainName, type:_DYNSELECT_,
					label: ZaMsg.NAD_DefaultDomainName,
					toolTipContent:ZaMsg.tt_StartTypingDomainName,
					dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
					dataFetcherClass:ZaSearch,editable:true
				},
                {ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_,
					  label: ZaMsg.NAD_DOC_MaxUploadSize, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
				},
			    { ref: ZaGlobalConfig.A_zimbraMtaRelayHost, type: _REPEAT_,
		  	  		label: ZaMsg.NAD_MTA_RelayMTA,
			  		labelLocation:_LEFT_,
			  		align:_LEFT_,
			  		repeatInstance:"",
					showAddButton:true, 
					showRemoveButton:true, 
					showAddOnNextRow:true,
					addButtonLabel:ZaMsg.Add_zimbraSmtpHostname, 
					removeButtonLabel:ZaMsg.Remove_zimbraSmtpHostname,
					removeButtonCSSStyle: "margin-left: 50px",
			  		items: [
						{ref:".",label:null,labelLocation:_NONE_,
							type:_HOSTPORT_,
							onClick: "ZaController.showTooltip",
					 		toolTipContent: ZaMsg.tt_MTA_RelayMTA,
					 		onMouseout: "ZaController.hideTooltip"
						}
					]
		  		},	  						
            ]
		}
	]};    
	switchItems.push(case1);
	tabBarChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});


	_tab2 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab2, label:ZaMsg.NAD_Tab_Attachments});
    var case2 = 	
    {type:_ZATABCASE_, caseKey:_tab2, id:"appliance_settings_form_attachment_tab", numCols:1, items:[
			{type: _GROUP_,  id:"appliance_attachment_settings", width: "98%", numCols: 2, colSizes:[250, "*"], items: [
				{ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_RADIO_, groupname:"appliance_attachment_settings",
					msgName:ZaMsg.NAD_GlobalRemoveAllAttachments,label:ZaMsg.NAD_GlobalRemoveAllAttachments, labelLocation:_RIGHT_, 
					onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_RIGHT_,
					valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
					visibilityChecks:[],
					updateElement:function () {
						this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_BLOCKED);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue("TRUE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
						this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);
						this.getForm().parent.setDirty(true);

					}
				},
				{ref:ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, type:_RADIO_, groupname:"appliance_attachment_settings", 
					msgName:ZaMsg.NAD_GlobalAttachmentsViewInHtmlOnly,label:ZaMsg.NAD_GlobalAttachmentsViewInHtmlOnly, labelLocation:_RIGHT_, 
					onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_RIGHT_,
					visibilityChecks:[],									
					valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
					updateElement:function () {
						this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_ONLY);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
						this.setInstanceValue("TRUE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);	
						this.getForm().parent.setDirty(true);																					

					}
				},
				{ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_RADIO_, groupname:"appliance_attachment_settings", 
					msgName:ZaMsg.NAD_GlobalAttachmentsViewCOS,label:ZaMsg.NAD_GlobalAttachmentsViewCOS, labelLocation:_RIGHT_, 
					onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_RIGHT_,
					visibilityChecks:[],									
					valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
					updateElement:function () {
						this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_COS);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
						this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);
						this.getForm().parent.setDirty(true);
					}
				},				
				{ ref: ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _CHECKBOX_,
			  		label: ZaMsg.LBL_zimbraMtaBlockedExtensionWarnRecipient,
			  		trueValue:"TRUE", falseValue:"FALSE"
				}					
			]},
			{type: _GROUP_, numCols:2, colSizes: ["330px","470px"], items:[
				{type:_GROUP_,  numCols: 1,
					items:[
					    {type:_SPACER_, height:"10"},
	    				{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder",//height: 400,
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.NAD_GlobalBlockedExtensions, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref:ZaGlobalConfig.A_zimbraMtaBlockedExtension, type:_DWT_LIST_,
									cssClass: "VAMIDLTarget",
									onSelection:ZaApplianceSettingsView.blockedExtSelectionListener
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, numCols:4, colSizes:[125,5, 125,"*"],
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, 
											onActivate:"ZaApplianceSettingsView.removeAllExt.call(this)",
										   	enableDisableChecks:[ZaApplianceSettingsView.shouldEnableRemoveAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, 
										   	onActivate:"ZaApplianceSettingsView.removeExt.call(this)",
										   	enableDisableChecks:[ZaApplianceSettingsView.shouldEnableRemoveButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A2_blocked_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									    },
										{type:_CELLSPACER_}
									]
								}
							]
	    				}
					]
				 },
				 {type: _GROUP_, numCols: 1,
					items: [
					    {type:_SPACER_, height:"10"},
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder",//height: 400,
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
								   	items: [
										{type:_OUTPUT_, value:ZaMsg.NAD_GlobalCommonExtensions, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type:_DWT_LIST_, 
									cssClass: "VAMIDLSource",
									onSelection:ZaApplianceSettingsView.commonExtSelectionListener
								},
							    {type:_SPACER_, height:"5"},
							    {type:_GROUP_, numCols:7, colSizes:[100,5,100,5,100,60,100],
									items: [
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddSelected, 
											onActivate:"ZaApplianceSettingsView.addCommonExt.call(this)",
											enableDisableChecks:[ZaApplianceSettingsView.shouldEnableAddButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A2_common_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
									    {type:_CELLSPACER_},
									    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll,
											onActivate:"ZaApplianceSettingsView.addAllCommonExt.call(this)",
											enableDisableChecks:[ZaApplianceSettingsView.shouldEnableAddAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_TEXTFIELD_, cssStyle:"width:60px;", ref:ZaGlobalConfig.A_zimbraNewExtension,
											label:ZaMsg.NAD_Attach_NewExtension,
											visibilityChecks:[],
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Attach_AddExtension, 
											onActivate:"ZaApplianceSettingsView.addNewExt.call(this)",
											enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaGlobalConfig.A_zimbraNewExtension],[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraNewExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										}
								  	]
							    }
							]
						  }
				    	]
				    }
				 ]}
			]};
    switchItems.push (case2) ;
	_tab3 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab3, label:com_zimbra_dashboard.LicenseTabTitle});
	var case3 = {type:_ZATABCASE_, caseKey:_tab3, numCols:2, colSizes:["300px","*"],
		items:[
            {type: _SPACER_, height: 10},
            //license file installation successful status, need to define relavant variable
            {type: _OUTPUT_, ref: ZaApplianceLicense.InstallStatusMsg, colSpan: "2",
                    width: "600px", align: _CENTER_, cssStyle: "border: solid thin",
                    visibilityChecks:[[XForm.checkInstanceValueNot,ZaApplianceLicense.InstallStatusCode,0]],bmolsnr:true,
                    visibilityChangeEventSources:[ZaApplianceLicense.InstallStatusCode]
            },
            //title
            {type: _OUTPUT_, value: com_zimbra_dashboard.LI_INFO_TITLE , colSpan: "2", width: "600px", align: _CENTER_ },
            //Customer name
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_issuedToName, label: com_zimbra_dashboard.LB_company_name, align: _LEFT_,visibilityChecks:[],bmolsnr:true},
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_installType, label: com_zimbra_dashboard.LB_license_type, align: _LEFT_,visibilityChecks:[],bmolsnr:true},
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_licenseId, label: com_zimbra_dashboard.LB_license_id, align: _LEFT_,bmolsnr:true },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_issuedOn, label: com_zimbra_dashboard.LB_issue_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_validFrom, label: com_zimbra_dashboard.LB_effective_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_validUntil, label: com_zimbra_dashboard.LB_expiration_date, align: _LEFT_,
            	getDisplayValue:ZaApplianceLicense.getLocalDate,bmolsnr:true
            },
            { type:_OUTPUT_, ref: ZaApplianceLicense.A_accountsLimit, label: com_zimbra_dashboard.LB_account_limit, align: _LEFT_,visibilityChecks:[],
            	getDisplayValue:function(val) {
            		var totalAccounts = this.getInstanceValue(ZaApplianceLicense.Info_TotalAccounts);
            		var retVal = val;
            		if (totalAccounts >= 0){
            			retVal += " " + AjxMessageFormat.format(com_zimbra_dashboard.LI_ACCOUNTS_USED,[totalAccounts]);
            		} else if (totalAccounts == -1){
            			retVal += " " + com_zimbra_dashboard.LI_ACCOUNT_COUNTING ;
            		} else{
            			retVal += " " + com_zimbra_dashboard.LI_ACCOUNT_COUNT_ERROR ;
            		}
            		return retVal;
            	},bmolsnr:true
            }
        ]
    };    
	switchItems.push(case3);
    
	_tab4 = ++this.TAB_INDEX;
    tabBarChoices.push ({value:_tab4, label:com_zimbra_dashboard.CertificatesTabTitle});
    var case4 = 	
    {type:_ZATABCASE_, caseKey:_tab4, id:"appliance_settings_form_certificates_tab", colSizes:["275px","275px"],numCols:2, items:[
		//{type:_OUTPUT_,ref:ZaApplianceSettings.A_serverName, label:com_zimbra_dashboard.CERT_SERVER_NAME,labelLocation:_LEFT_},
		{ type: _DWT_ALERT_,
			style: DwtAlert.WARNING,
			iconVisible: true, bmolsnr:true, 
			content: com_zimbra_dashboard.DidNotFindAnyCertificates,
			colSpan:2,
			visibilityChecks:[[XForm.checkInstanceValueEmty,ZaApplianceSettings.A_certs]],ref:null
		},
		{type:_REPEAT_,ref:ZaApplianceSettings.A_certs,	showAddButton:false,colSpan:2,bmolsnr:true,
			visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaApplianceSettings.A_certs]],enableDisableChecks:[],
			showRemoveButton:false,
			showAddOnNextRow:false,
			items:[
			    {type:_ZAGROUP_, colSizes:["200px","*"], items:[
					{ type: _OUTPUT_,bmolsnr:true,
						style: DwtAlert.INFORMATION,colSpan:2,
						visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaApplianceSSLCert.A_type]],ref:ZaApplianceSSLCert.A_type,
						getDisplayValue:function(val) {
							return AjxMessageFormat.format(com_zimbra_dashboard.Cert_Service_title, val);
						},
						label:null,labelLocation:_NONE_
					},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_subject, label:com_zimbra_dashboard.CERT_INFO_SUBJECT,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_issuer, label:com_zimbra_dashboard.CERT_INFO_ISSUER,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_validation_days_ro, label:com_zimbra_dashboard.CERT_INFO_VALIDATION_DAYS,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true},
					{type:_OUTPUT_,ref:ZaApplianceSSLCert.A_subject_alt, label:com_zimbra_dashboard.CERT_INFO_SubjectAltName,labelLocation:_LEFT_,visibilityChecks:[], bmolsnr:true}
				]}
			]
		}
	]}
    switchItems.push(case4);
    xFormObject.items = [
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar",
		 	containerCssStyle: "padding-top:0px",
			choices: tabBarChoices 
		},
		{type:_SWITCH_, items: switchItems}
	];
};
ZaTabView.XFormModifiers["ZaApplianceSettingsView"].push(ZaApplianceSettingsView.myXFormModifier);
