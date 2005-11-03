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
* @class ZaServerXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaServerXFormView (parent, app) {
	ZaTabView.call(this, parent, app);	
	this.initForm(ZaServer.myXModel,this.getMyXForm());
	this._localXForm.setController(this._app);
}

ZaServerXFormView.prototype = new ZaTabView();
ZaServerXFormView.prototype.constructor = ZaServerXFormView;

ZaServerXFormView.onFormFieldChanged = 
function (value, event, form) {
	form.parent.setDirty(true);
	this.setInstanceValue(value);
	return value;
}

ZaServerXFormView.prototype.setObject = 
function (entry) {
	this.entry = entry;
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();
	
	for(var a in entry) {
		if(typeof(entry[a]) == "object" || typeof(entry[a]) == "array" || entry[a] instanceof Array) {
			continue;
		}
		this._containedObject[a] = entry[a];
	}
	
	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	
	this._containedObject.hsm = new Object();	
	for (var a in entry.hsm) {
		this._containedObject.hsm[a] = entry.hsm[a];
	}
	if(entry[ZaServer.A_Volumes]) {
		this._containedObject[ZaServer.A_Volumes] = new Array();
		var cnt = entry[ZaServer.A_Volumes].length;
		for (var i = 0; i < cnt; i++) {
			this._containedObject[ZaServer.A_Volumes][i] = new Object();
			for (var at in entry[ZaServer.A_Volumes][i]) {
				this._containedObject[ZaServer.A_Volumes][i][at] = entry[ZaServer.A_Volumes][i][at];
			}
		}
		
		if(entry[ZaServer.A_CurrentPrimaryMsgVolumeId]) {
			this._containedObject[ZaServer.A_CurrentPrimaryMsgVolumeId] = entry[ZaServer.A_CurrentPrimaryMsgVolumeId];
		}
		if(entry[ZaServer.A_CurrentSecondaryMsgVolumeId]) {
			this._containedObject[ZaServer.A_CurrentSecondaryMsgVolumeId] = entry[ZaServer.A_CurrentSecondaryMsgVolumeId];
		}	
		if(entry[ZaServer.A_CurrentIndexMsgVolumeId]) {
			this._containedObject[ZaServer.A_CurrentIndexMsgVolumeId] = entry[ZaServer.A_CurrentIndexMsgVolumeId];
		}	
	}
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._containedObject.cos = entry.cos;
	this._containedObject[ZaServer.A_showVolumeAndHSM] = entry[ZaServer.A_showVolumeAndHSM];

	this._localXForm.setInstance(this._containedObject);	
}

ZaServerXFormView.isCurrent = function() {
	var volumeId = this.getModel().getInstanceValue(this.getInstance(), this.__parentItem.refPath + "/" +  ZaServer.A_VolumeId);
	return (volumeId != null && 
		(volumeId == this.getInstance()[ZaServer.A_CurrentPrimaryMsgVolumeId] ||
			volumeId == this.getInstance()[ZaServer.A_CurrentIndexMsgVolumeId] ||
			volumeId == this.getInstance()[ZaServer.A_CurrentSecondaryMsgVolumeId])
		);
}

ZaServerXFormView.isExistingVolume = function() {
	var volumeId = parseInt(this.getModel().getInstanceValue(this.getInstance(), this.__parentItem.refPath + "/" +  ZaServer.A_VolumeId));
	return (volumeId > 0);
}

ZaServerXFormView.onVolumeRemove = function (index, form) {
	var path = this.getRefPath();
	if(!this.getInstance()[ZaServer.A_RemovedVolumes]) {
		this.getInstance()[ZaServer.A_RemovedVolumes] = new Array();
	}
	//remove only existing volumes, ignore volumes that have not been saved yet
	if(this.getModel().getInstanceValue(this.getInstance(), path)[index][ZaServer.A_VolumeId]) {
		this.getInstance()[ZaServer.A_RemovedVolumes].push(this.getModel().getInstanceValue(this.getInstance(), path)[index]);
	}
	form.parent.setDirty(true);
	this.getModel().removeRow(this.getInstance(), path, index);	
}

ZaServerXFormView.makeCurrentHandler = function(ev) {
	var instance = this.getInstance();
	var parentRefPath = this.getParentItem().refPath;
	var model = this.getModel();
	var myVolumeType = parseInt(model.getInstanceValue(instance, (parentRefPath + '/' + ZaServer.A_VolumeType)));
	var myVolumeId = parseInt(model.getInstanceValue(instance, (parentRefPath + '/' + ZaServer.A_VolumeId)));
	switch(myVolumeType) {
		case ZaServer.PRI_MSG:
			instance[ZaServer.A_CurrentPrimaryMsgVolumeId] = myVolumeId;
		break;
		case ZaServer.SEC_MSG:
			instance[ZaServer.A_CurrentSecondaryMsgVolumeId] = myVolumeId;		
		break;
		case ZaServer.INDEX:
			instance[ZaServer.A_CurrentIndexMsgVolumeId] = myVolumeId;				
		break;
	}
	form = this.getForm();
	form.refresh();
	form.parent.setDirty(true);
}





ZaServerXFormView.getTLSEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_zimbraMtaAuthEnabled);
	return value == 'TRUE';
}

ZaServerXFormView.getIMAPEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_ImapServerEnabled);
	return value == 'TRUE';
}

ZaServerXFormView.getPOP3Enabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_Pop3ServerEnabled);
	return value == 'TRUE';
}

ZaServerXFormView.getMailboxEnabled = function () {
	var value = this.getModel().getInstanceValue(this.getInstance(),ZaServer.A_showVolumeAndHSM);
	return value;
}

ZaServerXFormView.prototype.getMyXForm = function() {	
	var xFormObject = {
		tableCssStyle:"width:100%;position:static;overflow:auto;",
		items: [
			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
						items: [
							{type:_AJX_IMAGE_, src:"Server_32", label:null, rowSpan:2},
							{type:_OUTPUT_, ref:ZaServer.A_name, label:null,cssClass:"AdminTitle", rowSpan:2},				
							{type:_OUTPUT_, ref:ZaServer.A_ServiceHostname, label:ZaMsg.NAD_ServiceHostname+":"},
							{type:_OUTPUT_, ref:ZaItem.A_zimbraId, label:ZaMsg.NAD_ZimbraID}
						]
					}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},
			{type:_TAB_BAR_, ref:ZaModel.currentTab,
				relevantBehavior:_HIDE_,
				containerCssStyle: "padding-top:0px",
				choices:[
					{value:1, label:ZaMsg.NAD_Tab_General},
					{value:2, label:ZaMsg.NAD_Tab_Services},
					{value:3, label:ZaMsg.NAD_Tab_MTA},
					{value:4, label:ZaMsg.NAD_Tab_IMAP},					
					{value:5, label:ZaMsg.NAD_Tab_POP},
					{value:6, label:ZaMsg.NAD_Tab_VolumeMgt}										
				],
				cssClass:"ZaTabBar"
			},
			
			/*{type:_TAB_BAR_, ref:ZaModel.currentTab,
				relevant:"instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]",
				relevantBehavior:_HIDE_,
				containerCssStyle: "padding-top:0px",
				choices:[
					{value:1, label:ZaMsg.NAD_Tab_General},
					{value:2, label:ZaMsg.NAD_Tab_Services},
					{value:3, label:ZaMsg.NAD_Tab_MTA},
					{value:4, label:ZaMsg.NAD_Tab_IMAP},					
					{value:5, label:ZaMsg.NAD_Tab_POP},
					{value:6, label:ZaMsg.NAD_Tab_VolumeMgt},										
					{value:7, label:ZaMsg.NAD_Tab_HSM}					
				],
				cssClass:"ZaTabBar"
			},*/
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
				        { type: _GROUP_, numCols:1,
				          label: ZaMsg.NAD_MTA_Authentication, labelCssStyle: "vertical-align:top",
				          items: [
					      	{ ref: ZaServer.A_zimbraMtaAuthEnabled, type: _SUPER_CHECKBOX_,
					      	  checkBoxLabel:ZaMsg.NAD_MTA_AuthenticationEnabled,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					      	  label:null, anchorCssStyle:"width:200px;text-align:right;"
				      	    },
					      	{ ref: ZaServer.A_zimbraMtaTlsAuthOnly, type: _SUPER_CHECKBOX_,
					      	  relevant:"ZaServerXFormView.getTLSEnabled.call(item)",
					      	  relevantBehavior: _DISABLE_,
					      	  checkBoxLabel: ZaMsg.NAD_MTA_TlsAuthenticationOnly,
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
  					      	  label:null, anchorCssStyle:"width:200px;text-align:right;"
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
				        { type: _GROUP_, numCols:1,
				        	label: ZaMsg.NAD_MTA_Options, labelCssStyle: "vertical-align:top",
				          items: [
					      	{ ref: ZaServer.A_zimbraMtaDnsLookupsEnabled, type:_SUPER_CHECKBOX_,
					      	  checkBoxLabel: ZaMsg.NAD_MTA_DnsLookups,
					      	  label:null,
					      	  anchorCssStyle:"width:200px;text-align:right;",
					      	  trueValue: "TRUE", falseValue: "FALSE",
					      	  onChange: ZaServerXFormView.onFormFieldChanged,
					      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal
				      	    }
				      	]}
				    ]},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 4", 
						items:[
							{ type: _DWT_ALERT_,
							  cssClass: "DwtTabTable",
							  containerCssStyle: "padding-bottom:0px",
							  style: DwtAlert.WARNING,
							  iconVisible: false, 
							  content: ZaMsg.Alert_ServerRestart
							},							
							{ type: _GROUP_, numCols: 1,
							  label: ZaMsg.NAD_IMAP_Service, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_ImapServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_IMAP_Enabled,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null, anchorCssStyle:"width:200px;text-align:right;"
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
							{ type: _GROUP_, numCols: 1,
							  label: ZaMsg.NAD_IMAP_SSLService, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_ImapSSLServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_IMAP_Enabled,
						      	  relevant:"ZaServerXFormView.getIMAPEnabled.call(item)",
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null, anchorCssStyle:"width:200px;text-align:right;",
						      	  relevantBehavior:_DISABLE_
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
							{ type: _GROUP_, numCols: 1,
							  label: ZaMsg.NAD_IMAP_CleartextLoginEnabled, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_ImapCleartextLoginEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_IMAP_Enabled,
						      	  relevant:"ZaServerXFormView.getIMAPEnabled.call(item)",
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null,
						      	  anchorCssStyle:"width:200px;text-align:right;",
						      	  relevantBehavior:_DISABLE_
					      	    }							  
							  ]
							 }
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 5", 
						items:[
							{ type: _DWT_ALERT_,
							  cssClass: "DwtTabTable",
							  containerCssStyle: "padding-bottom:0px",
							  style: DwtAlert.WARNING,
							  iconVisible: false, 
							  content: ZaMsg.Alert_ServerRestart
							},
							{ type: _GROUP_,numCols:1,
							  label: ZaMsg.NAD_POP_Service, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_Pop3ServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_POP_Enabled,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null, anchorCssStyle:"width:200px;text-align:right;",
						      	  relevantBehavior:_DISABLE_
					      	    }							  
							   ]
							},
							{ type: _GROUP_, numCols: 2,
							  label: ZaMsg.NAD_POP_Address,
							  items: [
								{ ref: ZaServer.A_Pop3BindAddress, type:_INPUT_, 
								  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
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
							  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
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
							{ type: _GROUP_, numCols: 1,
							  label: ZaMsg.NAD_POP_SSL, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_Pop3SSLServerEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_POP_Enabled,
						      	  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
						      	  relevantBehavior: _DISABLE_,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null, anchorCssStyle:"width:200px;text-align:right;",
						      	  relevantBehavior:_DISABLE_
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
							{ type: _GROUP_,numCols: 1,
							  label: ZaMsg.NAD_POP_Options, labelCssStyle: "vertical-align:top",
							  items: [
						      	{ ref: ZaServer.A_Pop3CleartextLoginEnabled, type: _SUPER_CHECKBOX_,
						      	  checkBoxLabel:ZaMsg.NAD_POP_CleartextLoginEnabled,
						      	  relevant:"ZaServerXFormView.getPOP3Enabled.call(item)",
						      	  relevantBehavior: _DISABLE_,
						      	  trueValue: "TRUE", falseValue: "FALSE",
						      	  onChange: ZaServerXFormView.onFormFieldChanged,
						      	  resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						      	  label:null, anchorCssStyle:"width:200px;text-align:right;",
						      	  relevantBehavior:_DISABLE_
					      	    }							  
							  ]}
						]
					},
					{type:_CASE_, relevant:"((instance[ZaModel.currentTab] == 6) && ZaServerXFormView.getMailboxEnabled.call(item))", 
						items:[
							{type:_GROUP_, numCols:3,
								relevant:"instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM] && instance[ZaServer.A_showVolumeAndHSM]",
								relevantBehavior:_HIDE_,
								items: [
									{ref:ZaServer.A_zimbraHsmAge, type:_SUPER_LIFETIME_, 
										msgName:ZaMsg.NAD_HSM_Threshold,
										label:ZaMsg.NAD_HSM_Threshold+":", 
										resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
										labelLocation:_LEFT_, labelCssStyle:"width:190px;",
										onChange:ZaTabView.onFormFieldChanged
									},
									{type:_SPACER_, colSpan:"*"},	
									{type:_DWT_ALERT_, ref:"progressMsg",content: null,
										relevant:"instance.hsm.progressMsg!=null",
										colSpan:"*",
										relevantBehavior:_HIDE_,
						 				iconVisible: true,
										align:_CENTER_,				
										style: DwtAlert.INFORMATION
									}									
								/*	{type:_SPACER_, colSpan:"*"},														
									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_LastStart,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMstartDate,
										getDisplayValue:function(val) {
											if(val)
												return AjxBuffer.concat(AjxDateUtil.simpleComputeDateStr(new Date(parseInt(val))),"&nbsp;",
																					AjxDateUtil.computeTimeString(new Date(parseInt(val))));									
											else
												return "";
										},labelCssStyle:"width:190px;"
									},
									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_LastEnd,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMendDate,
										getDisplayValue:function(val) {
											if(val)								
												return AjxBuffer.concat(AjxDateUtil.simpleComputeDateStr(new Date(parseInt(val))),"&nbsp;",
																					AjxDateUtil.computeTimeString(new Date(parseInt(val))));									
											else
												return "";
										},labelCssStyle:"width:190px;"
									}
									*/
								]
							},		
							{type:_SPACER_, colSpan:"*"},					
							{type:_GROUP_, numCols:5,  
								items: [
									{width:"96px", type:_OUTPUT_, label:null, value:ZaMsg.NAD_VM_VolumeName},
									{width:"246px", type:_OUTPUT_, label:null, value:ZaMsg.NAD_VM_VolumeRootPath},
									{type:_OUTPUT_, label:null, width:"126px", value:ZaMsg.NAD_VM_VolumeType},
								  	{type: _OUTPUT_,label:null,width:"96px", value:ZaMsg.NAD_VM_VolumeCompressBlobs},									
								  	{width:"120px", type:_OUTPUT_, label:null, value:ZaMsg.NAD_VM_VolumeCompressThreshold},
								  	{type:_OUTPUT_,width:"50px", value:"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"}
								]
							},
							{type:_SPACER_, colSpan:"*"},
							{ref:ZaServer.A_Volumes, type:_REPEAT_, showAddButton:true, showRemoveButton:true, remove_relevant:"!(ZaServerXFormView.isCurrent.call(item))",
								onRemove:ZaServerXFormView.onVolumeRemove,removeButtonLabel:ZaMsg.VOLUME_REPEAT_REMOVE, addButtonLabel:ZaMsg.VOLUME_REPEAT_ADD,
								showAddOnNextRow:true,
								items: [
									{ref:ZaServer.A_VolumeName, width:"100px", type:_TEXTFIELD_, label:null,onChange: ZaServerXFormView.onFormFieldChanged},
									{ref:ZaServer.A_VolumeRootPath, width:"250px", type:_TEXTFIELD_, label:null,
										relevant:"!(ZaServerXFormView.isCurrent.call(item))",
										relevantBehavior:_DISABLE_, onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoicesNoHSM,width:"100px", label:null,
										relevant:"!(model.getInstanceValue(instance, (item.__parentItem.refPath + '/' + ZaServer.A_VolumeId))) && !instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]",
										relevantBehavior:_HIDE_, onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoicesAll,width:"100px", label:null,
										relevant:"!(model.getInstanceValue(instance, (item.__parentItem.refPath + '/' + ZaServer.A_VolumeId))) && instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]",
										relevantBehavior:_HIDE_, onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ref:ZaServer.A_VolumeType, type:_OSELECT1_, choices:ZaServer.volumeTypeChoicesHSM,width:"135px", label:null,
										relevant:"model.getInstanceValue(instance, (item.__parentItem.refPath + '/' + ZaServer.A_VolumeId)) && instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM] && item.getInstanceValue() != ZaServer.INDEX && !ZaServerXFormView.isCurrent.call(item)",
										relevantBehavior:_HIDE_, onChange: ZaServerXFormView.onFormFieldChanged
									},
									{ref:ZaServer.A_VolumeType, type:_OUTPUT_,width:"132px", label:null,
										relevant:"model.getInstanceValue(instance, (item.__parentItem.refPath + '/' + ZaServer.A_VolumeId)) && (ZaServerXFormView.isCurrent.call(item) || item.getInstanceValue() == ZaServer.INDEX)",
										relevantBehavior:_HIDE_,
										getDisplayValue:function(val) {
											if(!val)
												return "";
												
											var value = parseInt(val);
											switch(value ) {
												case ZaServer.PRI_MSG :
													return ZaMsg.NAD_HSM_PrimaryMsg;
												case ZaServer.SEC_MSG:
													return ZaMsg.NAD_HSM_SecMsg;
												case ZaServer.INDEX:
													return ZaMsg.NAD_HSM_Index;
												default :
													return val;
											}
										}
									},									
								  	{ref:ZaServer.A_VolumeCompressBlobs, trueValue:1, falsevalue:0, type: _CHECKBOX_,width:"100px", label:null, onChange: ZaServerXFormView.onFormFieldChanged},									
								  	{ref:ZaServer.A_VolumeCompressionThreshold, onChange: ZaServerXFormView.onFormFieldChanged, width:"100px", type:_TEXTFIELD_,label:null/*, label:ZaMsg.NAD_bytes, labelLocation:_RIGHT_,labelCssStyle:"text-align:left"*/},
								  	{type:_OUTPUT_,width:"50px",value:ZaMsg.NAD_VM_CurrentVolume, relevant:"ZaServerXFormView.isCurrent.call(item)"},
								  	{type:_DWT_BUTTON_, label:ZaMsg.NAD_VM_MAKE_CURRENT, toolTipContent:ZaMsg.NAD_VM_MAKE_CURRENT_TT, onActivate:ZaServerXFormView.makeCurrentHandler, relevant:"( !(ZaServerXFormView.isCurrent.call(item)) && ZaServerXFormView.isExistingVolume.call(item))"}
								]
							}
						]
					},
					{type:_CASE_, relevant:"((instance[ZaModel.currentTab] == 6) && !instance[ZaServer.A_showVolumeAndHSM])", 					
						items: [
							{ type: _DWT_ALERT_,
							  cssClass: "DwtTabTable",
							  containerCssStyle: "padding-bottom:0px",
							  style: DwtAlert.WARNING,
							  iconVisible: true, 
							  content: ZaMsg.Alert_VM_MailboxServiceNotInstalled,
							   colSpan:"*"
							}						
						]
					
					}/*,
					{type:_CASE_, relevant: "instance[ZaModel.currentTab] == 7 && instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM] && instance[ZaServer.A_showVolumeAndHSM]", 
						numCols:2,
						items: [

							{type:_GROUP_, numCols:4,
								items: [
									{ref:ZaServer.A_zimbraHsmAge, type:_SUPER_LIFETIME_, 
										msgName:ZaMsg.NAD_HSM_Threshold,
										label:ZaMsg.NAD_HSM_Threshold+":", 
										resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
										labelLocation:_LEFT_, labelCssStyle:"width:190px;",
										onChange:ZaTabView.onFormFieldChanged
									}
								]
							},
							{type:_SPACER_, colSpan:"*"},
							{ type: _DWT_ALERT_,
								  style: DwtAlert.WARNING,
								  iconVisible: false, 
								  content: ZaMsg.Alert_HSM,
								  colSpan:"*"
							},		
							{type:_GROUP_, numCols:2,
								items: [
,
									{type:_DWT_BUTTON_, label:ZaMsg.NAD_HSM_StartHsm,
										relevant:"instance.hsm[ZaServer.A_HSMrunning]==0",
										width:"120px",
										onActivate:ZaServerXFormView.runHsm,colSpan:2
									},
									{type:_DWT_BUTTON_, label:ZaMsg.NAD_HSM_AbortHsm,
										relevant:"instance.hsm[ZaServer.A_HSMrunning]==1",
										width:"120px",
										onActivate:ZaServerXFormView.abortHsm,colSpan:2
									},
									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_Status, labelLocation:_LEFT_,
										ref:ZaServer.A_HSMrunning, choices:ZaServer.HSM_StatusChoices,
										relevant:"!instance.hsm[ZaServer.A_HSMwasAborted]",relevantBehavior:_HIDE_,labelCssStyle:"width:190px;"
									},
									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_Status, labelLocation:_LEFT_,								
										value:ZaMsg.NAD_HSM_Aborted, 
										relevant:"instance.hsm[ZaServer.A_HSMwasAborted]",relevantBehavior:_HIDE_,labelCssStyle:"width:190px;"
									},

									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_NumBlobsMoved,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMnumBlobsMoved,labelCssStyle:"width:190px;"
									},							

									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_ProcessedNumOfMbx,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMnumMailboxes,labelCssStyle:"width:190px;"
									},							

									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_RemainingNumOfMbx,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMremainingMailboxes,labelCssStyle:"width:190px;"
									},								

									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_LastStart,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMstartDate,
										getDisplayValue:function(val) {
											if(val)
												return AjxBuffer.concat(AjxDateUtil.simpleComputeDateStr(new Date(parseInt(val))),"&nbsp;",
																					AjxDateUtil.computeTimeString(new Date(parseInt(val))));									
											else
												return "";
										},labelCssStyle:"width:190px;"
									},

									{type:_OUTPUT_, label:ZaMsg.NAD_HSM_LastEnd,  labelLocation:_LEFT_,
										ref:ZaServer.A_HSMendDate,
										getDisplayValue:function(val) {
											if(val)								
												return AjxBuffer.concat(AjxDateUtil.simpleComputeDateStr(new Date(parseInt(val))),"&nbsp;",
																					AjxDateUtil.computeTimeString(new Date(parseInt(val))));									
											else
												return "";
										},labelCssStyle:"width:190px;"
									},
									{type:_SPACER_, colSpan:"*"},
									{type:_DWT_BUTTON_, label:ZaMsg.NAD_HSM_Refresh,
										onActivate:ZaServerXFormView.refreshHsm,
										width:"120px"
									},
									{type:_OUTPUT_, label:null, value:" "}									
								]
							}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 7 && !instance[ZaServer.A_showVolumeAndHSM] && instance.cos.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_HSM]", 					
						items: [
							{ 
							  type: _DWT_ALERT_,
							  cssClass: "DwtTabTable",
							  containerCssStyle: "padding-bottom:0px",
							  style: DwtAlert.WARNING,
							  iconVisible: true, 
							  content: ZaMsg.Alert_HSM_MailboxServiceNotInstalled,
							   colSpan:"*"
							}						
						]
					
					}*/
				]
			}	
		]
	};
	return xFormObject;
};
