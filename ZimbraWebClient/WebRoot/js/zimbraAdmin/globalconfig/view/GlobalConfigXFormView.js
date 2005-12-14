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
		  content: ZaMsg.Alert_GlobalConfig
		},
		{type:_TAB_BAR_,  ref:ZaModel.currentTab,relevantBehavior:_HIDE_,
		 	containerCssStyle: "padding-top:0px",
			choices:[
				{value:1, label:ZaMsg.NAD_Tab_General},
				{value:2, label:ZaMsg.NAD_Tab_Attachments},
				{value:3, label:ZaMsg.NAD_Tab_MTA},
				{value:4, label:ZaMsg.NAD_Tab_POP},
				{value:5, label:ZaMsg.NAD_Tab_IMAP},
				{value:6, label:ZaMsg.NAD_Tab_AntiSpam},
				{value:7, label:ZaMsg.NAD_Tab_AntiVirus}
			]
		},
		{type:_SWITCH_, items:[
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 1",
					colSizes:["300px","150", "*"],
				 	items:[
						{ ref: ZaGlobalConfig.A_zimbraGalMaxResults, type:_INPUT_, 
						  label: ZaMsg.NAD_GalMaxResults, width: "5em",
						  onChange:ZaTabView.onFormFieldChanged
						},
						{ ref: ZaGlobalConfig.A_zimbraDefaultDomainName, type:_OSELECT1_, 
						  label: ZaMsg.NAD_DefaultDomainName, //width: "10em",
						  choices: this._app.getDomainListChoices(), 
						  onChange:ZaTabView.onFormFieldChanged
						}
					]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2", items:[
				  	{ type: _GROUP_, 
				  	  label: ZaMsg.NAD_Attach_IncomingAttachments, labelCssStyle: "vertical-align:top",
				  	  items: [
						{ ref: ZaGlobalConfig.A_zimbraAttachmentsBlocked, type: _CHECKBOX_, 
						  label: ZaMsg.NAD_Attach_RemoveAllAttachments,
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
				  	  	  ref: ZaGlobalConfig.A_zimbraMtaBlockedExtension, type: _DWT_ADD_REMOVE_,
				  	  	  listCssClass: "DwtAddRemoveListView ZaGlobalAttachExt", sorted: true,
				  	  	  id: "addremove_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension,
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
			    	  	  	var addRemoveId = form.getId()+"_addremove_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension;
			    	  	  	var addRemoveFormItem = form.getItemById(addRemoveId);

							// NOTE: Need a special way to stop the widget from
							//		 updating w/o affecting the state change from
							//		 it. Otherwise, the instance data and the
							//		 target list won't stay in sync.
			    	  	  	addRemoveFormItem._skipUpdate = true;
			    	  	  	
			    	  	  	var addRemoveWidget = addRemoveFormItem .getWidget();
			    	  	  	addRemoveWidget.addTargetItem(value);

			    	  	  	addRemoveFormItem._skipUpdate = false;
			    	  	  	
			    	  	  	var newExtId = form.getId()+"_input_"+ZaGlobalConfig.A_zimbraMtaBlockedExtension;
			    	  	  	var newExtFormItem =form.getItemById(newExtId);
			    	  	  	newExtFormItem.setInstanceValue("");
			    	  	  	form.refresh();
			    	  	  }
			    	  	}
			    	]}
				]},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 3", items:[
					{ type: _GROUP_, label: ZaMsg.NAD_MTA_Authentication, labelCssStyle: "vertical-align:top",
					  items: [
					  	{ ref: ZaGlobalConfig.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
					   	  label: ZaMsg.NAD_MTA_AuthenticationEnabled,
					   	  trueValue: "TRUE", falseValue: "FALSE",
					   	  onChange: ZaTabView.onFormFieldChanged
				   	    },
				   	    { ref: ZaGlobalConfig.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
				   	      relevant: "instance.attrs[ZaGlobalConfig.A_zimbraMtaAuthEnabled] == 'TRUE'", relevantBehavior: _DISABLE_,
				   	      label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
				   	      trueValue: "TRUE", falseValue: "FALSE",
					   	  onChange: ZaTabView.onFormFieldChanged
					   	}
				   	]},
					{ type: _SEPARATOR_, numCols: 2 },
					{ type: _COMPOSITE_, useParentTable: false,
					  label: ZaMsg.NAD_MTA_WebMailHostname, labelCssStyle: "vertical-align:top",
					  items: [
						{ ref: ZaGlobalConfig.A_zimbraSmtpHostname, type: _INPUT_, 
						  labelLocation: _NONE_, width: "18em",
						  onChange:ZaTabView.onFormFieldChanged
						}
					]},
					{ type: _COMPOSITE_, useParentTable: false,
					  label: ZaMsg.NAD_MTA_WebMailPort, labelCssStyle: "vertical-align:top",
					  items: [
						{ ref: ZaGlobalConfig.A_zimbraSmtpPort, type: _OUTPUT_, 
						  label: null, labelLocation: _NONE_,labelLocation: _LEFT_, width: "4em"
					    }
					]},						
					{ ref: ZaGlobalConfig.A_zimbraMtaRelayHost, type: _INPUT_,
					  label: ZaMsg.NAD_MTA_RelayHostname, width: "18em",
					  onChange:ZaTabView.onFormFieldChanged
					},
					{ ref: ZaGlobalConfig.A_zimbraMtaMaxMessageSize, type: _INPUT_, 
					  label: ZaMsg.NAD_MTA_MaxMsgSize, width: "6em",
						  onChange:ZaTabView.onFormFieldChanged
  						},
						{ type: _GROUP_, label: ZaMsg.NAD_MTA_Options, labelCssStyle: "vertical-align:top",
					  items: [
					  	{ ref: ZaGlobalConfig.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
					  	  label: ZaMsg.NAD_MTA_DnsLookups, labelLocation: _RIGHT_,
					  	  trueValue: "TRUE", falseValue: "FALSE",
						  onChange:ZaTabView.onFormFieldChanged
					  	}
					]},
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
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 4", 
					colSizes:["300px","150", "*"],
					items: [
						{ type: _GROUP_, numCols: 2,
						  label: ZaMsg.NAD_POP_Service, labelCssStyle: "vertical-align:top",
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3ServerEnabled, type: _CHECKBOX_, 
							  label: ZaMsg.NAD_POP_Enabled,
							  trueValue: "TRUE", falseValue: "FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
						  	}
						]},
						{ type: _GROUP_, numCols: 2,
						  label: ZaMsg.NAD_POP_SSL, labelCssStyle: "vertical-align:top",
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraPop3SSLServerEnabled, type: _CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_POP_Enabled,
							  trueValue: "TRUE", falseValue: "FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
						  	}
						]},
						{ type: _GROUP_, label: ZaMsg.NAD_POP_Options, labelCssStyle: "vertical-align:top",
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraPop3CleartextLoginEnabled, type: _CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraPop3ServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_POP_CleartextLoginEnabled,
						  	  trueValue: "TRUE", falseValue: "FALSE", 
						  	  onChange: ZaTabView.onFormFieldChanged
					  	  	}
						]}
					]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 5",
					colSizes:["300px","150", "*"],
					items: [
						{ type: _GROUP_, numCols: 2,
						  label: ZaMsg.NAD_IMAP_Service, labelCssStyle: "vertical-align:top",
						  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapServerEnabled, type:_CHECKBOX_, 
							  label: ZaMsg.NAD_IMAP_Enabled, 
							  trueValue:"TRUE", falseValue:"FALSE", 
								  onChange:ZaTabView.onFormFieldChanged
  							  	}
			  	  	  	]},
						{ type: _GROUP_, numCols: 2,
						  label: ZaMsg.NAD_IMAP_SSLService, labelCssStyle: "vertical-align:top",
						  items: [
						  	{ ref: ZaGlobalConfig.A_zimbraImapSSLServerEnabled, type:_CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
						  	  label: ZaMsg.NAD_IMAP_Enabled, 
						  	  trueValue:"TRUE", falseValue:"FALSE", 
						  	  onChange:ZaTabView.onFormFieldChanged
					  	  	}
					  	]},
					  	{ type: _GROUP_, label: ZaMsg.NAD_IMAP_Options, labelCssStyle: "vertical-align:top",
					  	  items: [
							{ ref: ZaGlobalConfig.A_zimbraImapCleartextLoginEnabled, type:_CHECKBOX_, 
					  	  	  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraImapServerEnabled] == 'TRUE'", 
					  	  	  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_IMAP_CleartextLoginEnabled, 
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange:ZaTabView.onFormFieldChanged
						  	}
					  	]}
					]
				},
				// anti-spam
				{ type: _CASE_, relevant: "instance[ZaModel.currentTab] == 6", 
				  items: [
				  	{ type: _GROUP_, label: ZaMsg.NAD_Spam_Checking, items: [
					  	{ ref: ZaGlobalConfig.A_zimbraSpamCheckEnabled, type: _CHECKBOX_,
					  	  label: ZaMsg.NAD_Spam_CheckingEnabled,
						  trueValue:"TRUE", falseValue:"FALSE", 
						  onChange: ZaTabView.onFormFieldChanged
				  	    }
			  	    ]},
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
				  	}
				]},
				// security: anti-virus
				{ type: _CASE_, relevant: "instance[ZaModel.currentTab] == 7", 
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
				]}
			]
		}	
	];
};
ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(GlobalConfigXFormView.myXFormModifier);