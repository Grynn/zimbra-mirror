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
* @class GlobalConfigXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
GlobalConfigXFormView = function(parent, entry) {
	ZaTabView.call(this, {
		parent:parent, 
		iKeyName:"GlobalConfigXFormView",
		contextId:ZaId.TAB_GSET_EDIT
	});
	this.TAB_INDEX = 0;	
	this.initForm(ZaGlobalConfig.myXModel,this.getMyXForm(entry), null);
}

GlobalConfigXFormView.prototype = new ZaTabView();
GlobalConfigXFormView.prototype.constructor = GlobalConfigXFormView;
ZaTabView.XFormModifiers["GlobalConfigXFormView"] = new Array();

GlobalConfigXFormView.prototype.getTitle =
function () {
	return ZaMsg.GlobalConfig_view_title;
}

GlobalConfigXFormView.onRepeatRemove =
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
	form.parent.setDirty(true);
}

GlobalConfigXFormView.prototype.getTabIcon =
function () {
	return "GlobalSettings";
}

GlobalConfigXFormView.prototype.getTabTitle =
function () {
	return this.getTitle();
}

GlobalConfigXFormView.prototype.getTabToolTip =
function () {
	return this.getTitle ();
}


GlobalConfigXFormView.blockedExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_blocked_extension_selection, null);
	}
}

GlobalConfigXFormView.commonExtSelectionListener = function () {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaGlobalConfig.A2_common_extension_selection, null);
	}
}

GlobalConfigXFormView.shouldEnableRemoveAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension)));
}

GlobalConfigXFormView.shouldEnableRemoveButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection)));
}

GlobalConfigXFormView.shouldEnableAddButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection)));
}

GlobalConfigXFormView.shouldEnableAddAllButton = function () {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension)));
}

GlobalConfigXFormView.removeExt = function () {
	var blockedExtArray = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var selectedExtArray = this.getInstanceValue(ZaGlobalConfig.A2_blocked_extension_selection);
	var newBlockedExtArray = AjxUtil.arraySubstract(blockedExtArray,selectedExtArray);
	this.setInstanceValue(newBlockedExtArray,ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.removeAllExt = function () {
	this.setInstanceValue([],ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.setInstanceValue([],ZaGlobalConfig.A2_blocked_extension_selection);
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.addCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);	
	this.getForm().parent.setDirty(true);	
}

GlobalConfigXFormView.addAllCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension);
	commonExtArr = AjxUtil.isEmpty(commonExtArr) ? [] : commonExtArr;
	newExtArr = AjxUtil.isEmpty(newExtArr) ? [] : newExtArr;
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	this.getForm().parent.setDirty(true);			
}

GlobalConfigXFormView.addNewExt = function() {
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

GlobalConfigXFormView.GENERAL_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, ZaGlobalConfig.A_zimbraFileUploadMaxSize, ZaGlobalConfig.A_zimbraGalMaxResults, ZaGlobalConfig.A_zimbraDefaultDomainName,ZaGlobalConfig.A_zimbraScheduledTaskNumThreads];
GlobalConfigXFormView.GENERAL_TAB_RIGHTS = [];

GlobalConfigXFormView.ATTACHMENTS_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension];
GlobalConfigXFormView.ATTACHMENTS_TAB_RIGHTS = [];

GlobalConfigXFormView.MTA_TAB_ATTRS = [ZaGlobalConfig.A_zimbraMtaAuthEnabled, ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, ZaGlobalConfig.A_zimbraSmtpHostname,
	ZaGlobalConfig.A_zimbraSmtpPort, ZaGlobalConfig.A_zimbraMtaRelayHost, ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled];
GlobalConfigXFormView.MTA_TAB_RIGHTS = [];

GlobalConfigXFormView.IMAP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraImapServerEnabled, ZaGlobalConfig.A_zimbraImapSSLServerEnabled, ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraImapNumThreads];
GlobalConfigXFormView.IMAP_TAB_RIGHTS = [];

GlobalConfigXFormView.POP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraPop3ServerEnabled, ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled,
	ZaGlobalConfig.A_zimbraPop3NumThreads];
GlobalConfigXFormView.POP_TAB_RIGHTS = [];

GlobalConfigXFormView.ASAV_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSpamKillPercent, ZaGlobalConfig.A_zimbraSpamTagPercent, ZaGlobalConfig.A_zimbraSpamSubjectTag,
	ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, ZaGlobalConfig.A_zimbraVirusWarnRecipient];
GlobalConfigXFormView.ASAV_TAB_RIGHTS = [];

GlobalConfigXFormView.INTEROP_TAB_ATTRS = [ZaGlobalConfig.A_zimbraFreebusyExchangeURL, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername,
	ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg];
GlobalConfigXFormView.INTEROP_TAB_RIGHTS = [ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT];

GlobalConfigXFormView.SKIN_TAB_ATTRS = [ZaGlobalConfig.A_zimbraSkinForegroundColor, ZaGlobalConfig.A_zimbraSkinBackgroundColor,ZaGlobalConfig.A_zimbraSkinSecondaryColor,
	ZaGlobalConfig.A_zimbraSkinSelectionColor, ZaGlobalConfig.A_zimbraSkinLogoURL, ZaGlobalConfig.A_zimbraSkinLogoLoginBanner, ZaGlobalConfig.A_zimbraSkinLogoAppBanner ];

GlobalConfigXFormView.SKIN_TAB_RIGHTS = [];

GlobalConfigXFormView.myXFormModifier = function(xFormObject, entry) {
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab1, _tab2, _tab3, _tab4, _tab5, _tab6, _tab7, _tab8;
	
    var tabBarChoices = [];
    var switchItems = [];
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.GENERAL_TAB_ATTRS, GlobalConfigXFormView.GENERAL_TAB_RIGHTS)) {
    	_tab1 = ++this.TAB_INDEX;
    	var case1 = {type:_ZATABCASE_, caseKey:_tab1,
				colSizes:["auto"],numCols:1,
				items:[
					{type:_ZAGROUP_,
						items:[
							{ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_zimbraGalMaxResults, msgName:ZaMsg.MSG_zimbraGalMaxResults, 
							  labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
							},
							{ref:ZaGlobalConfig.A_zimbraDefaultDomainName, type:_DYNSELECT_,
								label: ZaMsg.NAD_DefaultDomainName,
								toolTipContent:ZaMsg.tt_StartTypingDomainName,
								dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
								dataFetcherClass:ZaSearch,editable:true
							},
							{ref: ZaGlobalConfig.A_zimbraScheduledTaskNumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_zimbraScheduledTaskNumThreads, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
							},
							{ref: ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, type:_LIFETIME_,
							  label: ZaMsg.LBL_zimbraMailPurgeSleepInterval, width: "5em"
							} ,
                          { ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_,
								  label: ZaMsg.NAD_DOC_MaxUploadSize, labelLocation:_LEFT_, cssClass:"admin_xform_number_input"
	  						}
                        ]
					}
				]
			};    
   			switchItems.push(case1);
   			tabBarChoices.push({value:_tab1, label:ZaMsg.TABT_GeneralPage});
    }
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.ATTACHMENTS_TAB_ATTRS, GlobalConfigXFormView.ATTACHMENTS_TAB_RIGHTS)) {
    	_tab2 = ++this.TAB_INDEX;
        tabBarChoices.push ({value:_tab2, label:ZaMsg.NAD_Tab_Attachments});
        var case2 = 	
        {type:_ZATABCASE_, caseKey:_tab2, id:"gs_form_attachment_tab", numCols:2, colSizes: ["30%","70%"], items:[
 				{type: _GROUP_,  id:"attachment_settings", width: "98%", numCols: 2, colSpan:2, colSizes:[250, "*"], items: [
					{ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type: _CHECKBOX_,
				  		label: ZaMsg.NAD_GlobalRemoveAllAttachments,
				  		trueValue: "TRUE", falseValue: "FALSE"
					},
					{ ref: ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _CHECKBOX_,
				  		label: ZaMsg.LBL_zimbraMtaBlockedExtensionWarnRecipient,
				  		trueValue:"TRUE", falseValue:"FALSE"
					}					
				]},
				{type:_GROUP_, width: "98%", numCols: 1,
					items:[
					    {type:_SPACER_, height:"10"},
        				{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%",  //height: 400,
							items:[
								{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
							   		items: [
										{type:_OUTPUT_, value:ZaMsg.NAD_GlobalBlockedExtensions, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref:ZaGlobalConfig.A_zimbraMtaBlockedExtension, type:_DWT_LIST_, height:"200", width:"98%",
									cssClass: "DLTarget", cssStyle:"margin-left: 5px; ",
									onSelection:GlobalConfigXFormView.blockedExtSelectionListener
								},
								{type:_SPACER_, height:"5"},
								{type:_GROUP_, width:"100%", numCols:4, colSizes:[125,5, 125,"*"],
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:120,
											onActivate:"GlobalConfigXFormView.removeAllExt.call(this)",
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:120,
										   	onActivate:"GlobalConfigXFormView.removeExt.call(this)",
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A2_blocked_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
									    },
										{type:_CELLSPACER_}
									]
								}
							]
        				}
					]
				 },
				 {type: _GROUP_, width: "98%", numCols: 1,
					items: [
					    {type:_SPACER_, height:"10"},
						{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%",  //height: 400,
							items:[
								{type:_GROUP_,  numCols:2, width:"98%", colSizes:["auto", "auto"],
								   	items: [
										{type:_OUTPUT_, value:ZaMsg.NAD_GlobalCommonExtensions, cssClass:"RadioGrouperLabel"},
										{type:_CELLSPACER_}
									]
								},
								{ref:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource",
									onSelection:GlobalConfigXFormView.commonExtSelectionListener
								},
							    {type:_SPACER_, height:"5"},
							    {type:_GROUP_, width:"98%", numCols:7, colSizes:[135,5,135,5,100,60,125],
									items: [
									   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddSelected, width:130,
											onActivate:"GlobalConfigXFormView.addCommonExt.call(this)",
											enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A2_common_extension_selection,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
									    {type:_CELLSPACER_},
									    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:130,
											onActivate:"GlobalConfigXFormView.addAllCommonExt.call(this)",
											enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddAllButton,[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_TEXTFIELD_, cssStyle:"width:60px;", ref:ZaGlobalConfig.A_zimbraNewExtension,
											label:ZaMsg.NAD_Attach_NewExtension,
											visibilityChecks:[],
											enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Attach_AddExtension, width:120,
											onActivate:"GlobalConfigXFormView.addNewExt.call(this)",
											enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaGlobalConfig.A_zimbraNewExtension],[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaBlockedExtension]],
											enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraNewExtension,ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										}
								  	]
							    }
							]
						  }
				    	]
				    }
				]};
        switchItems.push (case2) ;
    }
    
    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.MTA_TAB_ATTRS, GlobalConfigXFormView.MTA_TAB_RIGHTS)) {
    	_tab3 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab3, label:ZaMsg.NAD_Tab_MTA});
        var case3 = 		{type:_ZATABCASE_, caseKey:_tab3,
					colSizes:["auto"],numCols:1,id:"global_mta_tab",
					items: [
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_AuthenticationGrp,
							items:[
							  	{ ref: ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
							   	  label:ZaMsg.NAD_MTA_Authentication,
							   	  trueValue: "TRUE", falseValue: "FALSE"
						   	    },
						   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
						  	  		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaAuthEnabled,ZaGlobalConfig.A_zimbraMtaTlsAuthOnly],
						  	  		enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraMtaTlsAuthOnly],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraMtaAuthEnabled,"TRUE"]],

				   	    		  label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
						   	      trueValue: "TRUE", falseValue: "FALSE"

							   	}
							 ]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_NetworkGrp,id:"mta_network_group",
							items:[
								{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _REPEAT_,
						  	  		label: ZaMsg.LBL_zimbraSmtpHostname,
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
										{ ref:".", type: _TEXTFIELD_, label:null,labelLocation:_NONE_,
								  			toolTipContent: ZaMsg.tt_zimbraSmtpHostname
										}
							  		]
						  		},
								{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_,
								  label: ZaMsg.NAD_MTA_WebMailPort
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
						  		},							                                                                      { ref: ZaGlobalConfig.A_zimbraBasicAuthRealm, type: _TEXTFIELD_,
                                                                  label:ZaMsg.Domain_zimbraBasicAuthRealm,
                                                                  toolTipContent: ZaMsg.Domain_zimbraBasicAuthRealm
                                                                },
								{ type: _DWT_ALERT_,
									containerCssStyle: "padding-bottom:0px",
									style: DwtAlert.INFO,
									iconVisible: true,
									content: ZaMsg.Domain_InboundSMTPNote,
									colSpan:"*"
								},
								{ ref: ZaGlobalConfig.A_zimbraDNSCheckHostname, type: _TEXTFIELD_,
								  label:ZaMsg.Domain_zimbraDNSCheckHostname,
								  toolTipContent: ZaMsg.Domain_zimbraDNSCheckHostname
								},
							  	{ ref: ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
							  	  label: ZaMsg.NAD_MTA_DnsLookups,
							  	  trueValue: "TRUE", falseValue: "FALSE"
							  	}
							]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_Messages,
							items:[
								{ ref: ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _TEXTFIELD_,
								  label: ZaMsg.NAD_MTA_MaxMsgSize, width: "6em"
		  						},
	  							{ ref: ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP, type: _CHECKBOX_,
									label: ZaMsg.NAD_add_x_orginate_IP, trueValue: "TRUE", falseValue: "FALSE"
								}
							]
						},
						{type:_ZA_TOP_GROUPER_,label: ZaMsg.NAD_MTA_ProtocolChecks,
							items:[
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_invalid_hostname

						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_hostname
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_sender
						  	}
						]},
						{ type: _ZA_TOP_GROUPER_, label: ZaMsg.NAD_MTA_DnsChecks,
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownClient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_client
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_hostname
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_sender_domain
						  	}
						]},
						{ type: _ZA_PLAIN_GROUPER_,
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectRblClient, type: _REPEAT_,
						  	  label: ZaMsg.NAD_MTA_reject_rbl_client,
							  labelLocation:_LEFT_,
							  align:_LEFT_,
							  repeatInstance:"",
							  showAddButton:true,
							  showRemoveButton:true,
							  showAddOnNextRow:true,
							  items: [
								{ref:".", type:_TEXTFIELD_, label:null}
							  ]/*,
							  onRemove:GlobalConfigXFormView.onRepeatRemove*/
						  	}
						]}

				]};
        switchItems.push (case3) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.IMAP_TAB_ATTRS, GlobalConfigXFormView.IMAP_TAB_RIGHTS)) {
    	_tab4 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab4, label:ZaMsg.NAD_Tab_IMAP});
        var case4 ={type:_ZATABCASE_, caseKey:_tab4,
					colSizes:["auto"],numCols:1,id:"global_imap_tab",
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false,
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_ServiceGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapServerEnabled, type:_CHECKBOX_,
							  label: ZaMsg.IMAP_Service,
							  trueValue:"TRUE", falseValue:"FALSE"
	  						},
						  	{ ref: ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraImapServerEnabled,ZaGlobalConfig.A_zimbraImapSSLServerEnabled],
						  	  enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraImapSSLServerEnabled],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraImapServerEnabled,'TRUE']],
						  	  label: ZaMsg.IMAP_SSLService,
						  	  trueValue:"TRUE", falseValue:"FALSE"
					  	  	},
							{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraImapServerEnabled,ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled],
						  	  enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraImapServerEnabled,'TRUE']],
							  label: ZaMsg.IMAP_CleartextLoginEnabled,
							  trueValue:"TRUE", falseValue:"FALSE"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapNumThreads, type:_TEXTFIELD_,
                              enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraImapServerEnabled, ZaGlobalConfig.A_zimbraImapNumThreads],
                              enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraImapNumThreads],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraImapServerEnabled,'TRUE']],
							  label: ZaMsg.IMAP_NumThreads,
							  width: "5em"
						  	}
						  ]
						}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_SSLPort,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_Proxy_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_IMAP_SSL_Proxy_Port,
							  width: "5em"
						  	}
						  ]
						}*/
					]
				};
        switchItems.push (case4) ;
    }

    if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.POP_TAB_ATTRS, GlobalConfigXFormView.POP_TAB_RIGHTS)) {
    	_tab5 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab5, label:ZaMsg.NAD_Tab_POP});
        var case5 = 		{type:_ZATABCASE_, caseKey:_tab5,
					colSizes:["auto"],numCols:1,id:"global_pop_tab",
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false,
						  content: ZaMsg.Alert_ServerRestart
						},
						{type: _ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_ServiceGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3ServerEnabled, type: _CHECKBOX_,
							  label: ZaMsg.NAD_POP_Service,
							  trueValue: "TRUE", falseValue: "FALSE"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraPop3ServerEnabled,ZaGlobalConfig.A_zimbraPop3SSLServerEnabled],
						  	  enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraPop3SSLServerEnabled],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraPop3ServerEnabled,'TRUE']],
							  label: ZaMsg.NAD_POP_SSL,
							  trueValue: "TRUE", falseValue: "FALSE"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraPop3ServerEnabled,ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled],
						  	  enableDisableChecks:[[ZaItem.hasWritePermission,ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled],[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraPop3ServerEnabled,'TRUE']],
						  	  label: ZaMsg.NAD_POP_CleartextLoginEnabled,
						  	  trueValue: "TRUE", falseValue: "FALSE"
					  	  	},
					  	  	{ ref: ZaGlobalConfig.A_zimbraPop3NumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_POP_NumThreads, width: "5em"
							}
						]}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_NetworkGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3BindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_Port+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_SSL_Port,
							 width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3ProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_proxy_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.LBL_POP_SSL_proxy_Port,
							 width: "5em"
						  	}
						  ]}*/
					]
				};
        switchItems.push (case5) ;
    }

	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.ASAV_TAB_ATTRS, GlobalConfigXFormView.ASAV_TAB_RIGHTS)) {
    	_tab6 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab6, label:ZaMsg.NAD_Tab_ASAV});
        var case6 = 	// anti-spam
				{type: _ZATABCASE_, caseKey:_tab6,
					colSizes:["auto"],numCols:1,id:"global_asav_tab",
				 	items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AS_Settings,
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraSpamKillPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_KillPercent, width: "4em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamTagPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_TagPercent, width: "4em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamSubjectTag, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_SubjectPrefix, width: "20em"
						  	}
						  ]
						},
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AV_Settings,
						  items: [
					  	    {ref: ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, type: _INPUT_,
					  	     label: ZaMsg.NAD_Virus_DefUpdateFreq, width: "3em",
					  	     getDisplayValue: function(value) { return parseInt(value); },
					  	     elementChanged: function(elementValue, instanceValue, event) {
						     instanceValue = elementValue+"h";
							   	this.getForm().itemChanged(this, instanceValue, event);
							 }
					  	    },
				  	    	{ ref: ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, type: _CHECKBOX_,
					   	      label: ZaMsg.NAD_Virus_BlockEncrypted,
							  trueValue:"TRUE", falseValue:"FALSE"
					  	    },
						  	{ ref: ZaGlobalConfig.A_zimbraVirusWarnRecipient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_Virus_NotifyRecipient,
							  trueValue:"TRUE", falseValue:"FALSE"
						  	}
						  ]
						}
					]
				} 
			;
        switchItems.push (case6) ;
    }

  	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.INTEROP_TAB_ATTRS, GlobalConfigXFormView.INTEROP_TAB_RIGHTS)) {
    	_tab7 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab7, label:ZaMsg.TABT_Interop});
        var case7 = 		// Interop
				{type: _ZATABCASE_, caseKey:_tab7,
					colSizes:["auto"],numCols:1,id:"global_interop_tab",
				 	items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_Exchange_Settings,
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraFreebusyExchangeURL, type: _TEXTFIELD_,
						  	  label: ZaMsg.NAD_Exchange_URL, width: "30em"
						  	},
                            { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthScheme, type: _OSELECT1_,
						  	  label: ZaMsg.NAD_Exchange_Auth_Schema
						  	},
                              { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthUsername, type: _TEXTFIELD_,
						  	  label: ZaMsg.NAD_Exchange_Auth_User, width: "20em"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraFreebusyExchangeAuthPassword, type: _PASSWORD_,
						  	  label: ZaMsg.NAD_Exchange_Auth_Password, width: "20em"
						  	},
                            { ref: ZaGlobalConfig.A_zimbraFreebusyExchangeUserOrg, type: _TEXTFIELD_,
						  	  label: ZaMsg.LBL_zimbraFreebusyExchangeUserOrg, width: "20em"
						  	},
                              {type: _SPACER_, height: 10 },
                              {type: _GROUP_, colSpan:2, numCols:5, colSizes: ["120px", "10px", "100px", "20px", "120px" ], items :[
                                  {   type:_CELLSPACER_ },
                                  {
                                      type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Check_Settings, width: "15em",
                                      onActivate: ZaItem.checkInteropSettings, enableDisableChecks:[[ZaItem.hasRight,ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT,ZaZimbraAdmin.currentAdminAccount]]
                                  } ,
                                  {   type:_CELLSPACER_ } ,
                                  {
                                      type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Clear_Settings, width: "15em",
                                      onActivate: ZaItem.clearInteropSettings,
                                      enableDisableChecks:[[ZaItem.hasRight,ZaGlobalConfig.CHECK_EXCHANGE_AUTH_CONFIG_RIGHT,ZaZimbraAdmin.currentAdminAccount]]
                                  },
                                  {   type:_CELLSPACER_ }
                                 ]
                              }

                          ]
						}
					]
				};
        switchItems.push (case7) ;
    }

  	if(ZaTabView.isTAB_ENABLED(entry,GlobalConfigXFormView.SKIN_TAB_ATTRS, GlobalConfigXFormView.SKIN_TAB_RIGHTS)) {
    	_tab8 = ++this.TAB_INDEX;

        tabBarChoices.push ({value:_tab8, label:ZaMsg.TABT_Themes});
        var case8 =             //skin properties
                {type: _ZATABCASE_, caseKey:_tab8,
					colSizes:["auto"],numCols:1,id:"global_skin_tab",
				 	items: [
                        {type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_Skin_Color_Settings,
                            items: [
								{ type: _DWT_ALERT_,
									style: DwtAlert.INFO,
									iconVisible: true, 
									content: ZaMsg.Domain_flush_cache_q,
									colSpan:2,
									visibilityChecks:[],ref:null
								},                            
                                {ref:ZaGlobalConfig.A_zimbraSkinForegroundColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinForegroundColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinBackgroundColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinBackgroundColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinSecondaryColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSecondaryColor,
                                    labelLocation:_LEFT_,
                                    buttonImage: "Color", width: "50px"
                                },
                                {ref:ZaGlobalConfig.A_zimbraSkinSelectionColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSelectionColor,
                                    buttonImage: "Color", width: "50px" ,
                                    labelLocation:_LEFT_
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
        switchItems.push (case8) ;
    }

    xFormObject.items = [
		{ type: _DWT_ALERT_,
		  cssClass: "DwtTabTable",
		  containerCssStyle: "padding-bottom:0px",
		  style: DwtAlert.WARNING,
		  iconVisible: false,
		  content: ZaMsg.Alert_GlobalConfig,
		  id:"xform_header"
		},
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,id:"xform_tabbar",
		 	containerCssStyle: "padding-top:0px",
			choices: tabBarChoices 
		},
		{type:_SWITCH_, items: switchItems
		}
	];
};
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(GlobalConfigXFormView.myXFormModifier);
