/**
* @class ZaServerXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerXFormView (parent, app) {
	ZaTabView.call(this, parent, app);	
	this.initForm(ZaServer.myXModel,this.getMyXForm());
}

ZaServerXFormView.prototype = new ZaTabView();
ZaServerXFormView.prototype.constructor = ZaServerXFormView;

ZaServerXFormView.onFormFieldChanged = 
function (value, event, form) {
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaServerXFormView.prototype.getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;position:static;overflow:auto;",
		items: [
			{type:_GROUP_, cssClass:"AdminTitleBar", colSpan: "*", 
				items: [
					{type:_OUTPUT_, ref:ZaServer.A_name, label:ZaMsg.NAD_Server},
					{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
				]
			},
			{ type: _DWT_ALERT_,
			  style: DwtAlert.WARNING,
			  iconVisible: false, 
			  content: ZaMsg.Alert_ServerDetails
			},
			{type:_TAB_BAR_, ref:ZaModel.currentTab,
				choices:[
					{value:1, label:ZaMsg.NAD_Tab_General},
					{value:2, label:ZaMsg.NAD_Tab_Services},
					{value:3, label:ZaMsg.NAD_Tab_MTA},
					{value:4, label:ZaMsg.NAD_Tab_IMAP},					
					{value:5, label:ZaMsg.NAD_Tab_POP}										
				]
			},
			{type:_SWITCH_, items:[
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 1", 
						items:[
							{ref:ZaServer.A_name, type:_OUTPUT_, label:ZaMsg.NAD_DisplayName, labelLocation:_LEFT_},
							{ ref: ZaServer.A_description, type:_INPUT_, 
							  label:ZaMsg.NAD_Description, width: "30em",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_ServiceHostname, type:_INPUT_, 
							  label:ZaMsg.NAD_ServiceHostname, width: "18em",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_LmtpAdvertisedName, type:_INPUT_, 
							  label: ZaMsg.NAD_LmtpAdvertisedName, width: "18em",
							  onChange: ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_LmtpBindAddress, type:_INPUT_, 
							  label:ZaMsg.NAD_LmtpBindAddress, width: "18em",
							  onChange:ZaServerXFormView.onFormFieldChanged
							},
							/***
							{ref:ZaServer.A_LmtpBindPort, type:_INPUT_, label:ZaMsg.NAD_LmtpBindPort, labelLocation:_LEFT_, onChange:ZaServerXFormView.onFormFieldChanged, autoSaveValue:true},									
							/***/
							{ ref: ZaServer.A_notes, type:_TEXTAREA_, 
							  label: ZaMsg.NAD_Notes, labelCssStyle: "vertical-align:top", width: "30em",
							  onChange:ZaServerXFormView.onFormFieldChanged
						    }
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2", 
						items:[
							{ type: _GROUP_, label: ZaMsg.NAD_Service_EnabledServices, labelCssStyle: "vertical-align:top",
							  items: [
							  	{ ref: ZaServer.A_zimbraLdapServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraLdapServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_LDAP,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	},
							  	{ ref: ZaServer.A_zimbraMailboxServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraMailboxServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_Mailbox,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	},
							  	{ ref: ZaServer.A_zimbraMtaServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraMtaServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_MTA,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	},
							  	{ ref: ZaServer.A_zimbraSnmpServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraSnmpServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_SNMP,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	},
							  	{ ref: ZaServer.A_zimbraAntiSpamServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraAntiSpamServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_AntiSpam,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	},
							  	{ ref: ZaServer.A_zimbraAntiVirusServiceEnabled, type: _CHECKBOX_,
							  	  relevant: "instance.attrs[ZaServer.A_zimbraAntiVirusServiceInstalled]", relevantBehavior: _DISABLE_,
							  	  label: ZaMsg.NAD_Service_AntiVirus,
						  	      onChange: ZaServerXFormView.onFormFieldChanged
							  	}
							]}
						]
					}, 
					{ type: _CASE_, relevant: "instance[ZaModel.currentTab] == 3",
				      items: [
				        { type: _GROUP_, 
				          label: ZaMsg.NAD_MTA_Authentication, labelCssStyle: "vertical-align:top",
				          items: [
					      	{ ref: ZaServer.A_zimbraMtaAuthEnabled, type: _CHECKBOX_,
					      	  label: ZaMsg.NAD_MTA_AuthenticationEnabled,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged
				      	    },
					      	{ ref: ZaServer.A_zimbraMtaTlsAuthOnly, type: _CHECKBOX_,
					      	  relevant: "instance.attrs[ZaServer.A_zimbraMtaAuthEnabled] == 'TRUE'", 
					      	  relevantBehavior: _DISABLE_,
					      	  label: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged
				      	    }
				      	]},
				      	{ type: _SEPARATOR_, numCols: 2 },
				      	{ type: _GROUP_, numCols: 4,
				      	  label: ZaMsg.NAD_MTA_WebMailHostname,
				      	  items: [
							{ ref: ZaServer.A_SmtpHostname, type:_INPUT_, 
							  labelPosition: _NONE_, width: "18em",
							  onChange: ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_SmtpPort, type:_OUTPUT_, 
							  label: ZaMsg.NAD_MTA_WebMailPort, width: "4em"
							}
						]},
						{ ref: ZaServer.A_SmtpTimeout, type:_INPUT_, 
						  label: ZaMsg.NAD_MTA_WebMailTimeout, width: "4em",
						  onChange: ZaServerXFormView.onFormFieldChanged
						},
				      	{ ref: ZaServer.A_zimbraMtaRelayHost, type: _INPUT_,
				      	  label: ZaMsg.NAD_MTA_RelayHostname, width: "18em",
				      	  onChange: ZaServerXFormView.onFormFieldChanged
				      	},
				        { type: _GROUP_, label: ZaMsg.NAD_MTA_Options, labelCssStyle: "vertical-align:top",
				          items: [
					      	{ ref: ZaServer.A_zimbraMtaDnsLookupsEnabled, type: _CHECKBOX_,
					      	  label: ZaMsg.NAD_MTA_DnsLookups,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged
				      	    }
				      	]}
				    ]},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 4", 
						items:[
							{ type: _DWT_ALERT_,
							  labelLocation: _LEFT_, label: "",
							  style: DwtAlert.WARNING,
							  iconVisible: false, 
							  content: ZaMsg.Alert_ServerRestart,
							  alertCssClass: "DwtAlertBare"
							},
							{ type: _GROUP_, numCols: 2,
							  label: ZaMsg.NAD_IMAP_Service, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_ImapServerEnabled, type:_CHECKBOX_, 
								  label: ZaMsg.NAD_IMAP_Enabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged
							  	}
							  	/***
								{ ref: ZaServer.A_ImapBindPort, type:_INPUT_, 
								  relevant: "instance.attrs[ZaServer.A_ImapServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_IMAP_Port, width: "4em",
								  onChange: ZaServerXFormView.onFormFieldChanged
								}
								/***/
							]},
							{ type: _GROUP_, numCols: 2,
							  label: ZaMsg.NAD_IMAP_SSLService, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_ImapSSLServerEnabled, type:_CHECKBOX_, 
								  relevant: "instance.attrs[ZaServer.A_ImapServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_IMAP_Enabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged 
								}
								/***
								{ ref: ZaServer.A_ImapSSLBindPort, type:_INPUT_, 
								  relevant: "instance.attrs[ZaServer.A_ImapServerEnabled] == 'TRUE' && "+
								  			"instance.attrs[ZaServer.A_ImapSSLServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_IMAP_Port, width: "4em",
								  onChange: ZaServerXFormView.onFormFieldChanged
							  	}
							  	/***/
							]},
							{ type: _GROUP_,
							  label: ZaMsg.NAD_IMAP_Options, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_ImapCleartextLoginEnabled, type:_CHECKBOX_, 
								  relevant: "instance.attrs[ZaServer.A_ImapServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_IMAP_CleartextLoginEnabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged
								}
							]}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 5", 
						items:[
							{ type: _DWT_ALERT_,
							  labelLocation: _LEFT_, label: "",
							  style: DwtAlert.WARNING,
							  iconVisible: false, 
							  content: ZaMsg.Alert_ServerRestart,
							  alertCssClass: "DwtAlertBare"
							},
							{ type: _GROUP_,
							  label: ZaMsg.NAD_POP_Service, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_Pop3ServerEnabled, type:_CHECKBOX_, 
								  label: ZaMsg.NAD_POP_Enabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged
							  	}
							]},
							{ type: _GROUP_, numCols: 2,
							  label: ZaMsg.NAD_POP_Address,
							  items: [
								{ ref: ZaServer.A_Pop3BindAddress, type:_INPUT_, 
								  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  labelPosition: _NONE_, width: "18em",
								  onChange:ZaServerXFormView.onFormFieldChanged
							  	}
							  	/***
								{ ref: ZaServer.A_Pop3BindPort, type:_INPUT_, 
								  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_POP_Port, width: "4em",
								  onChange:ZaServerXFormView.onFormFieldChanged
							  	}
							  	/***/
							]},
							{ ref: ZaServer.A_Pop3AdvertisedName, type:_INPUT_, 
							  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
							  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_POP_AdvertisedName, width: "18em",
							  onChange: ZaServerXFormView.onFormFieldChanged
							},
							{ ref: ZaServer.A_Pop3NumThreads, type:_INPUT_, 
							  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
							  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_POP_NumThreads, width: "5em",
							  onChange: ZaServerXFormView.onFormFieldChanged
							},
							{ type: _GROUP_, numCols: 2,
							  label: ZaMsg.NAD_POP_SSL, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_Pop3SSLServerEnabled, type:_CHECKBOX_, 
								  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_POP_Enabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged
								}
								/***
								{ ref: ZaServer.A_Pop3SSLBindPort, type:_INPUT_, 
								  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE' && "+
								  			"instance.attrs[ZaServer.A_Pop3SSLServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_POP_Port, width: "4em",
								  onChange: ZaServerXFormView.onFormFieldChanged
							  	}
							  	/***/
							]},
							{ type: _GROUP_,
							  label: ZaMsg.NAD_POP_Options, labelCssStyle: "vertical-align:top",
							  items: [
								{ ref: ZaServer.A_Pop3CleartextLoginEnabled, type:_CHECKBOX_, 
								  relevant: "instance.attrs[ZaServer.A_Pop3ServerEnabled] == 'TRUE'",
								  relevantBehavior: _DISABLE_,
								  label: ZaMsg.NAD_POP_CleartextLoginEnabled, 
								  trueValue: "TRUE", falseValue: "FALSE", 
								  onChange: ZaServerXFormView.onFormFieldChanged
								}
							]},
						]
					}
					
				]
			}	
		]
	};
	return xFormObject;
};