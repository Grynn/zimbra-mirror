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
* @class GlobalConfigXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
GlobalConfigXFormView = function(parent) {
	ZaTabView.call(this, parent, "GlobalConfigXFormView");
	this.TAB_INDEX = 0;	
	this.initForm(ZaGlobalConfig.myXModel,this.getMyXForm());
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

GlobalConfigXFormView.addCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A2_common_extension_selection);
	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);		
}

GlobalConfigXFormView.addAllCommonExt = function () {
	var commonExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaBlockedExtension);
	var newExtArr = this.getInstanceValue(ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension);
	
	this.setInstanceValue(AjxUtil.mergeArrays(commonExtArr,newExtArr),ZaGlobalConfig.A_zimbraMtaBlockedExtension);			
}

GlobalConfigXFormView.myXFormModifier = function(xFormObject) {
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	var _tab1 = ++this.TAB_INDEX;
	var _tab2 = ++this.TAB_INDEX;	
	var _tab3 = ++this.TAB_INDEX;	
	var _tab4 = ++this.TAB_INDEX;	
	var _tab5 = ++this.TAB_INDEX;		
	var _tab6 = ++this.TAB_INDEX;	
	var _tab7 = ++this.TAB_INDEX;		
	var _tab8 = ++this.TAB_INDEX;	

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
			choices:[
				{value:1, label:ZaMsg.TABT_GeneralPage},
				{value:2, label:ZaMsg.NAD_Tab_Attachments},
				{value:3, label:ZaMsg.NAD_Tab_MTA},
				{value:4, label:ZaMsg.NAD_Tab_IMAP},
				{value:5, label:ZaMsg.NAD_Tab_POP},
				{value:6, label:ZaMsg.NAD_Tab_ASAV},
                {value:7, label:ZaMsg.TABT_Interop},
                {value:8, label:ZaMsg.TABT_Skin}    
            ]
		},
		{type:_SWITCH_, items:[
			{type:_ZATABCASE_, caseKey:_tab1,
				colSizes:["auto"],numCols:1,
				items:[
					{type:_ZAGROUP_,
						items:[
							{ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_INPUT_,
							  label: ZaMsg.NAD_GalMaxResults, width: "5em"
							},
							/*{ref: ZaGlobalConfig.A_zimbraDefaultDomainName, type:_OSELECT1_,
								label: ZaMsg.NAD_DefaultDomainName, //width: "10em",
								choices:EmailAddr_XFormItem.domainChoices,editable:true,
            	            	keyUp:function(newValue, ev) {
        	                        var n = "";
            	                    if (newValue)
                	                    n = String(newValue).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
	                	          	var query = "(zimbraDomainName=" + n + "*)";
                        	        this.getForm().getController().searchDomains(query);
                            	}
							},*/
							{ref:ZaGlobalConfig.A_zimbraDefaultDomainName, type:_DYNSELECT_,
								label: ZaMsg.NAD_DefaultDomainName,
								dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
								dataFetcherClass:ZaSearch,editable:true
							},
							{ref: ZaGlobalConfig.A_zimbraScheduledTaskNumThreads, type:_INPUT_,
							  label: ZaMsg.NAD_zimbraScheduledTaskNumThreads, width: "5em"
							},
							{ref: ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, type:_LIFETIME_,
							  label: ZaMsg.NAD_zimbraMailPurgeSleepInterval, width: "5em"
							} ,
                          { ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_,
								  label: ZaMsg.NAD_DOC_MaxUploadSize, width: "6em"
	  						}
                        ]
					}
				]
			},
			{type:_ZATABCASE_, caseKey:_tab2, id:"gs_form_attachment_tab", numCols:2, colSizes: ["50%","50%"], items:[
				//{type: _GROUP_, id:"attachment_settings",
					//label: ZaMsg.NAD_Attach_IncomingAttachments, labelCssStyle: "vertical-align:top",
					//items: [
					
				 {type:_GROUP_, width: "98%", numCols: 1,   
					items:[	
						{type:_SPACER_, height:"5"}, 						    
 						{type: _GROUP_, width: "98%", numCols: 2, colSizes:[100, "*"], items: [
							{ref:ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _CHECKBOX_,
						  		label: ZaMsg.NAD_GlobalRemoveAllAttachments,
						  		trueValue: "TRUE", falseValue: "FALSE"
					   		}
					   	]},
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
								{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"*"], 
									items:[
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveAllButton],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaBlockedExtension]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80,
										   	enableDisableChecks:[GlobalConfigXFormView.shouldEnableRemoveButton],
									   		enableDisableChangeEventSources:[ZaGlobalConfig.A2_blocked_extension_selection]										
									    },
										{type:_CELLSPACER_}									
									]
								}								
							]
        				}
					]
				 },
				 {type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.NAD_GlobalAddBlockedExtensions,	
					items:[
						{type:_SPACER_, height:"5"}, 			      
					    {type:_GROUP_, numCols:3, width:"98%", 
						   items:[
								{type:_TEXTFIELD_, cssClass:"admin_xform_name_input", ref:ZaGlobalConfig.A_zimbraNewExtension, label:ZaMsg.NAD_Attach_NewExtension},
								{type:_DWT_BUTTON_, label:ZaMsg.NAD_Attach_AddExtension, width:80}
							]
					    },
					    {type:_SPACER_, height:"5"},
					    {type:_SPACER_, height:"3"},
					    {type:_OUTPUT_, value:ZaMsg.NAD_GlobalCommonExtensions,  cssClass:"xform_label_left",
        					width: AjxEnv.isIE ? 100 : 94, cssStyle:"text-align: right;"
        				},						    
						{ref:ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource",
							onSelection:GlobalConfigXFormView.commonExtSelectionListener
						},
					    {type:_SPACER_, height:"5"},
					    {type:_GROUP_, width:"98%", numCols:8, colSizes:[85,5, 85,"*"],
							items: [
							   	{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
									onActivate:"GlobalConfigXFormView.addCommonExt.call(this,event)",
									enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddButton],
									enableDisableChangeEventSources:[ZaGlobalConfig.A2_common_extension_selection]										
								},
							    {type:_CELLSPACER_},
							    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
									onActivate:"GlobalConfigXFormView.addAllCommonExt.call(this,event)",
									enableDisableChecks:[GlobalConfigXFormView.shouldEnableAddAllButton],
									enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension]										
								},
								{type:_CELLSPACER_}	
						  	]
					    }
					]
				  }
				    //]},
				    
				  	/*{type: _GROUP_,
				  	  label: "", labelCssStyle: "vertical-align:top",
				  	  items: [
				  	  	{ type: _SEPARATOR_, colSpan: "*" },
				  	    { type: _OUTPUT_, labelLocation: _NONE_,
				  	      value: ZaMsg.NAD_Attach_RemoveAttachmentsByExt, colSpan: "*"
			  	      	},
				  	  	{ sourceRef: ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,
				  	  	  ref: ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _DWT_CHOOSER_,
				  	  	  listCssClass: "DwtChooserListView ZaGlobalAttachExt", sorted: true,
				  	  	  id: "chooser_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension
				  	  	}
			    	]},
			    	{ type: _GROUP_, label: "", labelCssStyle: "vertical-align:top",
			    	  useParentTable: false, numCols: 3,
			    	  items: [
			    	  	{ ref: ZaGlobalConfig.A_zimbraNewExtension, type: _INPUT_,
				  	  	  id: "input_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension,
			    	  	  label: ZaMsg.NAD_Attach_NewExtension
			    	  	},
			    	  	{ type: _DWT_BUTTON_, label: ZaMsg.NAD_Attach_AddExtension,
			    	  	  onActivate: function() {
			    	  	  	var form = this.getForm();
			    	  	  	form.onCloseForm(); // HACK
			    	  	  	var value = form.get(ZaGlobalConfig.A_zimbraNewExtension);
			    	  	  	if (!value) {
			    	  	  		return;
		    	  	  		}
			    	  	  	value = value.replace(/^\s+/,"").replace(/\s+$/,"");
			    	  	  	if (value == "") {
			    	  	  		return;
			    	  	  	}

							// NOTE: The id property is needed by the list view
			    	  	  	value = new String(value);
			    	  	  	value.id = "id_"+value;

			    	  	  	// NOTE: form item's id is prefixed with form's id + underscore
			    	  	  	var chooserId = form.getId()+"_chooser_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension;
			    	  	  	var chooserFormItem = form.getItemById(chooserId);

							// NOTE: Need a special way to stop the widget from
							//		 updating w/o affecting the state change from
							//		 it. Otherwise, the instance data and the
							//		 target list won't stay in sync.
			    	  	  	chooserFormItem._skipUpdate = true;

			    	  	  	var chooserWidget = chooserFormItem.getWidget();
							chooserWidget.addItems(value, DwtChooserListView.SOURCE, true);

			    	  	  	chooserFormItem._skipUpdate = false;

			    	  	  	var newExtId = form.getId()+"_input_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension;
			    	  	  	var newExtFormItem =form.getItemById(newExtId);
			    	  	  	newExtFormItem.setInstanceValue("");
			    	  	  	form.parent.setDirty(true);
			    	  	  	form.refresh();
			    	  	  }
			    	  	}
			    	]}*/
				]},
				{type:_ZATABCASE_, caseKey:_tab3,
					colSizes:["auto"],numCols:1,id:"global_mta_tab",
					items: [
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_AuthenticationGrp,
							items:[
							  	{ ref: ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
							   	  label:ZaMsg.NAD_MTA_Authentication,
							   	  trueValue: "TRUE", falseValue: "FALSE"
						   	    },
						   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
						  	  		enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraMtaAuthEnabled],
						  	  		enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraMtaAuthEnabled,"TRUE"]],						   	      
						   	      //relevant: "instance.attrs[ZaGlobalConfig.A_zimbraMtaAuthEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				   	    		  label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
						   	      trueValue: "TRUE", falseValue: "FALSE"

							   	}
							 ]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_NetworkGrp,id:"mta_network_group",
							items:[
								{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _TEXTFIELD_,
								  label:ZaMsg.NAD_MTA_WebMailHostname,
								  toolTipContent: ZaMsg.tt_MTA_WebMailHostname
								},
								{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_,
								  label: ZaMsg.NAD_MTA_WebMailPort
							    },
								{ref:ZaGlobalConfig.A_zimbraMtaRelayHost,label:ZaMsg.NAD_MTA_RelayMTA,
									type:_HOSTPORT_,
									 onClick: "ZaController.showTooltip",
							 		 toolTipContent: ZaMsg.tt_MTA_RelayMTA,
							 		 onMouseout: "ZaController.hideTooltip"
								},
/*								{ref:ZaGlobalConfig.A_zimbraMtaMyNetworks,label:ZaMsg.NAD_MTA_MyNetworks,
									type:_TEXTFIELD_ ,
									toolTipContent: ZaMsg.tt_MTA_MyNetworks
								},*/
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
							  ],
							  onRemove:GlobalConfigXFormView.onRepeatRemove
						  	}
						]}

				]},
				{type:_ZATABCASE_, caseKey:_tab4,
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
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraImapServerEnabled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraImapServerEnabled,'TRUE']],					  	  	  
						  	  label: ZaMsg.IMAP_SSLService,
						  	  trueValue:"TRUE", falseValue:"FALSE"
					  	  	},
							{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraImapServerEnabled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraImapServerEnabled,'TRUE']],							
							  label: ZaMsg.IMAP_CleartextLoginEnabled,
							  trueValue:"TRUE", falseValue:"FALSE"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapNumThreads, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_NumThreads,
							  width: "5em"
						  	}
						  ]
						}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp,
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_Port+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_SSLPort+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_Proxy_Port+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.IMAP_SSL_Proxy_Port+":",
							  width: "5em"
						  	}
						  ]
						}*/
					]
				},
				{type:_ZATABCASE_, caseKey:_tab5,
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
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraPop3ServerEnabled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraPop3ServerEnabled,'TRUE']],							  	
							  label: ZaMsg.NAD_POP_SSL,
							  trueValue: "TRUE", falseValue: "FALSE"
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_,
						  	  enableDisableChangeEventSources:[ZaGlobalConfig.A_zimbraPop3ServerEnabled],
						  	  enableDisableChecks:[[XForm.checkInstanceValue,ZaGlobalConfig.A_zimbraPop3ServerEnabled,'TRUE']],					  	  	  
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
							  label: ZaMsg.NAD_POP_Port+":",
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_POP_SSL_Port,
							 width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3ProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_POP_proxy_Port,
							  width: "5em"
						  	},
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, type:_TEXTFIELD_,
							  label: ZaMsg.NAD_POP_SSL_proxy_Port,
							 width: "5em"
						  	}
						  ]}*/
					]
				},
				// anti-spam
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
				} ,
				// Interop
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
						  	  label: ZaMsg.NAD_ExchangeUserGroup, width: "20em"
						  	},

                              {type: _GROUP_, colSpan:2, numCols:3, colSizes: ["150px", "*", "auto" ], items :[
                                  {   type:_CELLSPACER_ },
                                  {
                                      type: _DWT_BUTTON_ , colSpan: 2, label: ZaMsg.Check_Settings, width: "15em",
                                      onActivate: ZaItem.checkInteropSettings
                                  } ,
                                  {   type:_CELLSPACER_ }
                                 ]
                              }

                          ]
						}
					]
				},
                //skin properties
                {type: _ZATABCASE_, caseKey:_tab8,
					colSizes:["auto"],numCols:1,id:"global_skin_tab",
				 	items: [
                        {type:_ZA_TOP_GROUPER_,  label:ZaMsg.NAD_Skin_Settings,//colSizes:["175px","*"],
                            items: [
                                {ref:ZaGlobalConfig.A_zimbraSkinForegroundColor,
                                    type:_DWT_COLORPICKER_,
        //                            labelCssStyle:"width:175px", colSizes:["375px","190px"],
                                    //msgName:ZaMsg.NAD_zimbraPrefHtmlEditorDefaultFontColor,
                                    label:ZaMsg.NAD_zimbraSkinForegroundColor,
                                    labelLocation:_LEFT_,
                                    onChange:ZaTabView.onFormFieldChanged
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinBackgroundColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinBackgroundColor,
                                    labelLocation:_LEFT_,
                                    onChange:ZaTabView.onFormFieldChanged
                                }  ,
                                {ref:ZaGlobalConfig.A_zimbraSkinSecondaryColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSecondaryColor,
                                    labelLocation:_LEFT_,
                                    onChange:ZaTabView.onFormFieldChanged
                                },
                                {ref:ZaGlobalConfig.A_zimbraSkinSelectionColor,
                                    type:_DWT_COLORPICKER_,
                                    label:ZaMsg.NAD_zimbraSkinSelectionColor,
                                    labelLocation:_LEFT_,
                                    onChange:ZaTabView.onFormFieldChanged
                                }
                            ]
                        }
                    ]
                }
            ]
		}
	];
};
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(GlobalConfigXFormView.myXFormModifier);
