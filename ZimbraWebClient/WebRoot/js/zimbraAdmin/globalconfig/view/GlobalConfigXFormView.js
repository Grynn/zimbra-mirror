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
GlobalConfigXFormView = function(parent, app) {
	ZaTabView.call(this, parent, app,"GlobalConfigXFormView");
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

GlobalConfigXFormView.myXFormModifier = function(xFormObject) {	
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	
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
				{value:1, label:ZaMsg.NAD_Tab_General},
				{value:2, label:ZaMsg.NAD_Tab_Attachments},
				{value:3, label:ZaMsg.NAD_Tab_MTA},
				{value:4, label:ZaMsg.NAD_Tab_IMAP},
				{value:5, label:ZaMsg.NAD_Tab_POP},
				{value:6, label:ZaMsg.NAD_Tab_ASAV}
			]
		},
		{type:_SWITCH_, items:[
			{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 1", 
				colSizes:["auto"],numCols:1,
				items:[
					{type:_ZAGROUP_,
						items:[
							{ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_INPUT_, 
							  label: ZaMsg.NAD_GalMaxResults, width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
							},
							/*{ref: ZaGlobalConfig.A_zimbraDefaultDomainName, type:_OSELECT1_, 
								label: ZaMsg.NAD_DefaultDomainName, //width: "10em",
								choices:EmailAddr_XFormItem.domainChoices,editable:true,
								onChange:ZaTabView.onFormFieldChanged,
            	            	keyUp:function(newValue, ev) {
        	                        var n = "";
            	                    if (newValue)
                	                    n = String(newValue).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
	                	          	var query = "(zimbraDomainName=" + n + "*)";
                        	        this.getForm().getController().searchDomains(query);
                            	}
							},*/
							{ref:ZaGlobalConfig.A_zimbraDefaultDomainName, type:_DYNSELECT_,
								label: ZaMsg.NAD_DefaultDomainName, onChange:ZaTabView.onFormFieldChanged,
								dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
								dataFetcherClass:ZaSearch,choices:EmailAddr_XFormItem.domainChoices,editable:true
							},
							{ref: ZaGlobalConfig.A_zimbraScheduledTaskNumThreads, type:_INPUT_, 
							  label: ZaMsg.NAD_zimbraScheduledTaskNumThreads, width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
							},
							{ref: ZaGlobalConfig.A_zimbraMailPurgeSleepInterval, type:_LIFETIME_, 
							  label: ZaMsg.NAD_zimbraMailPurgeSleepInterval, width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
							}
						]
					}
				]
			},
			{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 2", id:"gs_form_attachment_tab", items:[
				{type: _GROUP_, id:"attachment_settings",
					label: ZaMsg.NAD_Attach_IncomingAttachments, labelCssStyle: "vertical-align:top",
					items: [
						{ref: ZaGlobalConfig.A_zimbraAttachmentsBlocked, type: _CHECKBOX_, 
						  label: ZaMsg.NAD_GlobalRemoveAllAttachments,
						  trueValue: "TRUE", falseValue: "FALSE", 
						  onChange: ZaTabView.onFormFieldChanged
					   	}
				    ]},
				  	{type: _GROUP_, 
				  	  label: "", labelCssStyle: "vertical-align:top",
				  	  items: [
				  	  	{ type: _SEPARATOR_, colSpan: "*" },
				  	    { type: _OUTPUT_, labelLocation: _NONE_,
				  	      value: ZaMsg.NAD_Attach_RemoveAttachmentsByExt, colSpan: "*"
			  	      	},
				  	  	{ sourceRef: ZaGlobalConfig.A_zimbraMtaCommonBlockedExtension,
				  	  	  ref: ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _DWT_CHOOSER_,
				  	  	  listCssClass: "DwtChooserListView ZaGlobalAttachExt", sorted: true,
				  	  	  id: "chooser_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension,
				  	  	  onChange: ZaTabView.onFormFieldChanged
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
			    	]}
				]},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 3", 
					colSizes:["auto"],numCols:1,id:"global_mta_tab",
					items: [
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_AuthenticationGrp,
							items:[	
							  	{ ref: ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
							   	  label:ZaMsg.NAD_MTA_Authentication, 
							   	  trueValue: "TRUE", falseValue: "FALSE",
							   	  onChange: ZaTabView.onFormFieldChanged
						   	    },
						   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
						   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraMtaAuthEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				   	    		  label: ZaMsg.NAD_MTA_TlsAuthenticationOnly, 
						   	      trueValue: "TRUE", falseValue: "FALSE",
							   	  onChange: ZaTabView.onFormFieldChanged
				
							   	}
							 ]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_NetworkGrp,
							items:[	
								{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _TEXTFIELD_, 
								  onChange:ZaTabView.onFormFieldChanged,
								  label:ZaMsg.NAD_MTA_WebMailHostname,
								  toolTipContent: ZaMsg.tt_MTA_WebMailHostname
								},
								{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_, 
								  label: ZaMsg.NAD_MTA_WebMailPort
							    },
								{ref:ZaGlobalConfig.A_zimbraMtaRelayHost,label:ZaMsg.NAD_MTA_RelayMTA,
									type:_HOSTPORT_,onChange:ZaTabView.onFormFieldChanged,
									 onClick: "ZaController.showTooltip",
							 		 toolTipContent: ZaMsg.tt_MTA_RelayMTA,
							 		 onMouseout: "ZaController.hideTooltip"
								},
/*								{ref:ZaGlobalConfig.A_zimbraMtaMyNetworks,label:ZaMsg.NAD_MTA_MyNetworks,
									type:_TEXTFIELD_,onChange:ZaTabView.onFormFieldChanged ,
									toolTipContent: ZaMsg.tt_MTA_MyNetworks
								},*/
														
							  	{ ref: ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
							  	  label: ZaMsg.NAD_MTA_DnsLookups,
							  	  trueValue: "TRUE", falseValue: "FALSE",
								  onChange:ZaTabView.onFormFieldChanged
							  	}
							]
						},
						{type:_ZA_TOP_GROUPER_,label:ZaMsg.Global_MTA_Messages,
							items:[	
								{ ref: ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _TEXTFIELD_, 
								  label: ZaMsg.NAD_MTA_MaxMsgSize, width: "6em",
								  onChange:ZaTabView.onFormFieldChanged
		  						},
								/*{ ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_, 
								  label: ZaMsg.NAD_MTA_MaxUploadSize, width: "6em",
								  onChange:ZaTabView.onFormFieldChanged
	  							},*/
	  							{ ref: ZaGlobalConfig.A_zimbraSmtpSendAddOriginatingIP, type: _CHECKBOX_,
									label: ZaMsg.NAD_add_x_orginate_IP, trueValue: "TRUE", falseValue: "FALSE",
					   	  			onChange: ZaTabView.onFormFieldChanged
								}  
							]
						},						
						{type:_ZA_TOP_GROUPER_,label: ZaMsg.NAD_MTA_ProtocolChecks, 
							items:[	
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectInvalidHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_invalid_hostname,
							  onChange: ZaTabView.onFormFieldChanged
							  
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_hostname,
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectNonFqdnSender, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_non_fqdn_sender,
							  onChange: ZaTabView.onFormFieldChanged
						  	}
						]},
						{ type: _ZA_TOP_GROUPER_, label: ZaMsg.NAD_MTA_DnsChecks, 
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownClient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_client,
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownHostname, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_hostname,
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraMtaRejectUnknownSenderDomain, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_MTA_reject_unknown_sender_domain,
							  onChange: ZaTabView.onFormFieldChanged
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
								{ref:".", type:_TEXTFIELD_, label:null, onChange:ZaTabView.onFormFieldChanged}
							  ],
							  onRemove:GlobalConfigXFormView.onRepeatRemove,
							  onChange: ZaTabView.onFormFieldChanged
						  	}
						]}
						
				]},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 4",
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
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange:ZaTabView.onFormFieldChanged
	  						},
						  	{ ref: ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.IMAP_SSLService, 
						  	  trueValue:"TRUE", falseValue:"FALSE", 
						  	  onChange:ZaTabView.onFormFieldChanged
					  	  	},
							{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
							  label: ZaMsg.IMAP_CleartextLoginEnabled,
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange:ZaTabView.onFormFieldChanged
						  	},  						
							{ ref: ZaGlobalConfig.A_zimbraImapNumThreads, type:_TEXTFIELD_, 
							  label: ZaMsg.IMAP_NumThreads,
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	}		
						  ]
						}/*,	
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_IMAP_NetworkGrp, 
						  items: [											  	
							{ ref: ZaGlobalConfig.A_zimbraImapBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.IMAP_Port+":",
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},		
							{ ref: ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.IMAP_SSLPort+":",
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},					  	
							{ ref: ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.IMAP_Proxy_Port+":",
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},	
							{ ref: ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.IMAP_SSL_Proxy_Port+":",
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	}
						  ]
						}*/
					]
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 5",
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
							  trueValue: "TRUE", falseValue: "FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type: _CHECKBOX_, 
					  	  	 // relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
					  	  	  //relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_POP_SSL,
							  trueValue: "TRUE", falseValue: "FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_POP_CleartextLoginEnabled,
						  	  trueValue: "TRUE", falseValue: "FALSE", 
						  	  onChange: ZaTabView.onFormFieldChanged
					  	  	},
					  	  	{ ref: ZaGlobalConfig.A_zimbraPop3NumThreads, type:_TEXTFIELD_, 
							  label: ZaMsg.NAD_POP_NumThreads, width: "5em",
							  onChange: ZaTabView.onFormFieldChanged
							}
						]}/*,
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.Global_POP_NetworkGrp, 
						  items: [							
							{ ref: ZaGlobalConfig.A_zimbraPop3BindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.NAD_POP_Port+":",
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},		
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.NAD_POP_SSL_Port,
							 width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},					  	
							{ ref: ZaGlobalConfig.A_zimbraPop3ProxyBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.NAD_POP_proxy_Port,
							  width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	},	
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLProxyBindPort, type:_TEXTFIELD_, 
							  label: ZaMsg.NAD_POP_SSL_proxy_Port,
							 width: "5em",
							  onChange:ZaTabView.onFormFieldChanged
						  	}
						  ]}*/
					]
				},
				// anti-spam
				{type: _ZATABCASE_, relevant: "instance[ZaModel.currentTab] == 6", 
					colSizes:["auto"],numCols:1,id:"global_asav_tab",
				 	items: [
						{type:_ZA_TOP_GROUPER_, label:ZaMsg.NAD_AS_Settings, 
						  items: [							
						  	{ ref: ZaGlobalConfig.A_zimbraSpamKillPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_KillPercent, width: "4em",
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamTagPercent, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_TagPercent, width: "4em",
							  onChange: ZaTabView.onFormFieldChanged
						  	},
						  	{ ref: ZaGlobalConfig.A_zimbraSpamSubjectTag, type: _INPUT_,
						  	  label: ZaMsg.NAD_Spam_SubjectPrefix, width: "20em",
							  onChange: ZaTabView.onFormFieldChanged
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
							 },
							 onChange: ZaTabView.onFormFieldChanged
					  	    },
				  	    	{ ref: ZaGlobalConfig.A_zimbraVirusBlockEncryptedArchive, type: _CHECKBOX_,
					   	      label: ZaMsg.NAD_Virus_BlockEncrypted,
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
					  	    },
						  	{ ref: ZaGlobalConfig.A_zimbraVirusWarnRecipient, type: _CHECKBOX_,
						  	  label: ZaMsg.NAD_Virus_NotifyRecipient,
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
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
