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
* @class GlobalConfigXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function GlobalConfigXFormView (parent, app) {
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
					colSizes:["300px","*"],
				 	items:[
						{ ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_INPUT_, 
						  label: ZaMsg.NAD_GalMaxResults, width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
						},
						{ ref: ZaGlobalConfig.A_zimbraDefaultDomainName, type:_OSELECT1_, 
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
						}
					]
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 2", id:"gs_form_attachment_tab", items:[
				  	{ type: _GROUP_, id:"attachment_settings",
				  	  label: ZaMsg.NAD_Attach_IncomingAttachments, labelCssStyle: "vertical-align:top",
				  	  items: [
						{ ref: ZaGlobalConfig.A_zimbraAttachmentsBlocked, type: _CHECKBOX_, 
						  label: ZaMsg.NAD_GlobalRemoveAllAttachments,
						  trueValue: "TRUE", falseValue: "FALSE", 
						  onChange: ZaTabView.onFormFieldChanged
				    	}
			    	]},
				  	{ type: _GROUP_, 
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
			    	  	  	form.refresh();
			    	  	  }
			    	  	}
			    	]}
				]},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 3", width:"100%",colSizes:["150px","400px"], 
					items:[
					  	{ ref: ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
					   	  label:ZaMsg.NAD_MTA_Authentication,labelLocation:_LEFT_,
					   	  trueValue: "TRUE", falseValue: "FALSE",
					   	  onChange: ZaTabView.onFormFieldChanged,
					   	  labelCssClass:"xform_label", align:_LEFT_
				   	    },
				   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
				   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraMtaAuthEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				   	      label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,labelLocation:_LEFT_,
				   	      trueValue: "TRUE", falseValue: "FALSE",
					   	  onChange: ZaTabView.onFormFieldChanged,
					   	  labelCssClass:"xform_label", align:_LEFT_
					   	},
						{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _TEXTFIELD_, 
						  onChange:ZaTabView.onFormFieldChanged,
						  label:ZaMsg.NAD_MTA_WebMailHostname,
						  toolTipContent: ZaMsg.tt_MTA_WebMailHostname
						},
						{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_, 
						  label: ZaMsg.NAD_MTA_WebMailPort
					    },
//					    { type: _SEPARATOR_, numCols: 2 },
/*					    {type:_OUTPUT_, ref:ZaGlobalConfig.A_zimbraMtaRelayHost, label:ZaMsg.NAD_MTA_RelayMTA},
						{ ref: ZaGlobalConfig.A_zimbraMtaRelayHostInternal, type: _TEXTFIELD_,
						  label: ZaMsg.NAD_MTA_RelayHostname, width: "18em",
						  onChange:ZaTabView.onFormFieldChanged,
						  cssClass:"admin_xform_name_input"
						},
						{ ref: ZaGlobalConfig.A_zimbraMtaRelayPortInternal, type: _TEXTFIELD_,
						  label: ZaMsg.NAD_MTA_RelayPort, width: "18em",
						  onChange:ZaTabView.onFormFieldChanged,
						  cssClass:"admin_xform_number_input"
						},								
						{ type: _SEPARATOR_, numCols: 2 },*/
						{ref:ZaGlobalConfig.A_zimbraMtaRelayHost,label:ZaMsg.NAD_MTA_RelayMTA,
							type:_HOSTPORT_,onChange:ZaTabView.onFormFieldChanged,
							 onClick: "ZaController.showTooltip",
					 		 toolTipContent: ZaMsg.tt_MTA_RelayMTA,
					 		 onMouseout: "ZaController.hideTooltip"
						},
						{ref:ZaGlobalConfig.A_zimbraMtaMyNetworks,label:ZaMsg.NAD_MTA_MyNetworks,
							type:_TEXTFIELD_,onChange:ZaTabView.onFormFieldChanged ,
							toolTipContent: ZaMsg.tt_MTA_MyNetworks
						},
						{ ref: ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _TEXTFIELD_, 
						  label: ZaMsg.NAD_MTA_MaxMsgSize, width: "6em",
						  onChange:ZaTabView.onFormFieldChanged
  						},
						{ ref: ZaGlobalConfig.A_zimbraFileUploadMaxSize, type: _TEXTFIELD_, 
						  label: ZaMsg.NAD_MTA_MaxUploadSize, width: "6em",
						  onChange:ZaTabView.onFormFieldChanged
  						},  						
					  	{ ref: ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
					  	  label: ZaMsg.NAD_MTA_DnsLookups,labelLocation:_LEFT_,
					  	  trueValue: "TRUE", falseValue: "FALSE",
						  onChange:ZaTabView.onFormFieldChanged,
						  labelCssClass:"xform_label", align:_LEFT_
					  	},
						{ type: _SEPARATOR_, numCols: 2 },
						{ type: _GROUP_, label: ZaMsg.NAD_MTA_ProtocolChecks, labelCssStyle: "vertical-align:top",
						  items: [
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
						{ type: _GROUP_, label: ZaMsg.NAD_MTA_DnsChecks, labelCssStyle: "vertical-align:top",
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
						]}
				]},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 4",
					width:"100%",colSizes:["100px","400px"], 
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false, 
						  content: ZaMsg.Alert_ServerRestart
						},
						{ ref: ZaGlobalConfig.A_zimbraImapServerEnabled, type:_CHECKBOX_, 
						  label: ZaMsg.NAD_IMAP_Service, labelLocation:_LEFT_,
						  labelCssClass:"xform_label", align:_LEFT_,
						  trueValue:"TRUE", falseValue:"FALSE", 
						  onChange:ZaTabView.onFormFieldChanged
  						},
					  	{ ref: ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_CHECKBOX_, 
				  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
				  	  	  relevantBehavior: _DISABLE_,
					  	  label: ZaMsg.NAD_IMAP_SSLService, labelLocation:_LEFT_,
					 	  labelCssClass:"xform_label", align:_LEFT_,
					  	  trueValue:"TRUE", falseValue:"FALSE", 
					  	  onChange:ZaTabView.onFormFieldChanged
				  	  	},
						{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_, 
				  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
				  	  	  relevantBehavior: _DISABLE_,
						  label: ZaMsg.NAD_IMAP_CleartextLoginEnabled,labelLocation:_LEFT_,
					 	  labelCssClass:"xform_label", align:_LEFT_, 
						  trueValue:"TRUE", falseValue:"FALSE", 
						  onChange:ZaTabView.onFormFieldChanged
					  	},  						
						{ ref: ZaGlobalConfig.A_zimbraImapBindPort, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_IMAP_Port,
						  width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
					  	},		
						{ ref: ZaGlobalConfig.A_zimbraImapSSLBindPort, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_IMAP_SSLPort,
						  width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
					  	},					  	
						{ ref: ZaGlobalConfig.A_zimbraImapProxyBindPort, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_IMAP_Proxy_Port,
						  width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
					  	},	
						{ ref: ZaGlobalConfig.A_zimbraImapSSLProxyBindPort, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_IMAP_SSL_Proxy_Port,
						  width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
					  	},	
						{ ref: ZaGlobalConfig.A_zimbraImapNumThreads, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_IMAP_NumThreads,
						  width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
					  	}					  						

					]
				},
				{type:_ZATABCASE_, relevant:"instance[ZaModel.currentTab] == 5", 
					width:"100%",colSizes:["100px","400px"], 
					items: [
						{ type: _DWT_ALERT_,
						  containerCssStyle: "padding-bottom:0px",
						  style: DwtAlert.WARNING,
						  iconVisible: false, 
						  content: ZaMsg.Alert_ServerRestart
						},					
						{ ref: ZaGlobalConfig.A_zimbraPop3ServerEnabled, type: _CHECKBOX_, 
						  label: ZaMsg.NAD_POP_Service,labelLocation:_LEFT_,
						  labelCssClass:"xform_label", align:_LEFT_,
						  trueValue: "TRUE", falseValue: "FALSE", 
						  onChange: ZaTabView.onFormFieldChanged
					  	},
					  							{ ref: ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type: _CHECKBOX_, 
				  	  	 // relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
				  	  	  //relevantBehavior: _DISABLE_,
						  label: ZaMsg.NAD_POP_SSL,labelLocation:_LEFT_,
						  labelCssClass:"xform_label", align:_LEFT_,
						  trueValue: "TRUE", falseValue: "FALSE", 
						  onChange: ZaTabView.onFormFieldChanged
					  	},
					  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_, 
				  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
				  	  	  relevantBehavior: _DISABLE_,
					  	  label: ZaMsg.NAD_POP_CleartextLoginEnabled,labelLocation:_LEFT_,
						  labelCssClass:"xform_label", align:_LEFT_,
					  	  trueValue: "TRUE", falseValue: "FALSE", 
					  	  onChange: ZaTabView.onFormFieldChanged
				  	  	},
						{ ref: ZaGlobalConfig.A_zimbraPop3BindPort, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_POP_Port,
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
					  	},	
						{ ref: ZaGlobalConfig.A_zimbraPop3NumThreads, type:_TEXTFIELD_, 
						  label: ZaMsg.NAD_POP_NumThreads, width: "5em",
						  onChange: ZaTabView.onFormFieldChanged
						}					  						  							  	

					]
				},
				// anti-spam
				{ type: _ZATABCASE_, relevant: "instance[ZaModel.currentTab] == 6", 
					width:"100%",colSizes:["300px","*"], 
				  items: [
				  	{ ref: ZaGlobalConfig.A_zimbraSpamCheckEnabled, type: _CHECKBOX_,
				  	  label: ZaMsg.NAD_Enable_ASAV,labelLocation:_LEFT_,
				 	  labelCssClass:"xform_label", align:_LEFT_, 
					  trueValue:"TRUE", falseValue:"FALSE", 
					  onChange: ZaTabView.onFormFieldChanged,
			  	      elementChanged: function(elementValue, instanceValue, event) {
					    this.getForm().getInstance().attrs[ZaGlobalConfig.A_zimbraVirusCheckEnabled] = elementValue;
					    this.getForm().itemChanged(this, elementValue, event);
					  }
			  	    },
					{ type: _SEPARATOR_, numCols: 2 },		
					{type:_OUTPUT_, colSpan:2, value:ZaMsg.NAD_AS_Settings},
				  	{ ref: ZaGlobalConfig.A_zimbraSpamKillPercent, type: _INPUT_,
			   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraSpamCheckEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				  	  label: ZaMsg.NAD_Spam_KillPercent, width: "4em",
					  onChange: ZaTabView.onFormFieldChanged
				  	},
				  	{ ref: ZaGlobalConfig.A_zimbraSpamTagPercent, type: _INPUT_,
			   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraSpamCheckEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				  	  label: ZaMsg.NAD_Spam_TagPercent, width: "4em",
					  onChange: ZaTabView.onFormFieldChanged
				  	},
				  	{ ref: ZaGlobalConfig.A_zimbraSpamSubjectTag, type: _INPUT_,
			   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraSpamCheckEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				  	  label: ZaMsg.NAD_Spam_SubjectPrefix, width: "20em",
					  onChange: ZaTabView.onFormFieldChanged
				  	},
				  	{ type: _SEPARATOR_, numCols: 2 },		
					{type:_OUTPUT_, colSpan:2, value:ZaMsg.NAD_AV_Settings},
			  	    { ref: ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, type: _INPUT_,
			  	      label: ZaMsg.NAD_Virus_DefUpdateFreq, width: "3em",
			  	      getDisplayValue: function(value) { return parseInt(value); },
			  	      elementChanged: function(elementValue, instanceValue, event) {
					    instanceValue = elementValue+"h";
					    this.getForm().itemChanged(this, instanceValue, event);
					  },
					  onChange: ZaTabView.onFormFieldChanged
			  	    },
			  	    { type: _GROUP_, label: ZaMsg.NAD_Virus_Options, labelCssStyle: "vertical-align:top", items: [
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
				  	]}				  	
				]}/*,
				// security: anti-virus
				{ type: _ZATABCASE_, relevant: "instance[ZaModel.currentTab] == 7", 
				colSizes:["300px","150", "*"],
				  items: [
			  	    { ref: ZaGlobalConfig.A_zimbraVirusDefinitionsUpdateFrequency, type: _INPUT_,
			  	      label: ZaMsg.NAD_Virus_DefUpdateFreq, width: "3em",
			  	      getDisplayValue: function(value) { return parseInt(value); },
			  	      elementChanged: function(elementValue, instanceValue, event) {
					    instanceValue = elementValue+"h";
					    this.getForm().itemChanged(this, instanceValue, event);
					  },
					  onChange: ZaTabView.onFormFieldChanged
			  	    },
			  	    { type: _GROUP_, label: ZaMsg.NAD_Virus_Options, labelCssStyle: "vertical-align:top", items: [
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
				  	]}
				]}*/
			]
		}	
	];
};
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(GlobalConfigXFormView.myXFormModifier);
