/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012 Zimbra, Inc.
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

/*---Disable Proxy Wizard Model---*/
if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_proxy_config"]) {
function ZaDisableProxy () {
	ZaItem.call(this, "ZaDisableProxy");
	this._init();
	this.type = ZaItem.DISABLE_PROXY ; 
}

ZaItem.DISABLE_PROXY = "disable_proxy" ;
ZaDisableProxy.prototype = new ZaItem ;
ZaDisableProxy.prototype.constructor = ZaDisableProxy;

ZaDisableProxy.myXModel = {
	items: [
		// web proxy
		{id: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraMailMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailMode, choices: ZaProxyConfig.MAIL_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraMailPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailPort},
		{id: ZaProxyConfig.A_zimbraMailSSLPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLPort},
		{id: ZaProxyConfig.A_zimbraAdminPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraAdminPort},
		
		// mail proxy
		{id: ZaProxyConfig.A_zimbraReverseProxyMailEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraImapBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapBindPort},
		{id: ZaProxyConfig.A_zimbraImapSSLBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapSSLBindPort},
		{id: ZaProxyConfig.A_zimbraPop3BindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3BindPort},
		{id: ZaProxyConfig.A_zimbraPop3SSLBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3SSLBindPort},
        {id: ZaProxyConfig.A_zimbraImapCleartextLoginEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapCleartextLoginEnabled, choices: ZaModel.BOOLEAN_CHOICES},
        {id: ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled, choices: ZaModel.BOOLEAN_CHOICES},

		// utility
		{id: ZaProxyConfig.A2_mbx_name_array, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_proxy_name_array, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_target_up_servers, type: _LIST_, itemType: _STRING_}
	]
}

ZaDisableProxy.init = function () {
	this.attrs = {};
	
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] = "FALSE";
	this.attrs[ZaProxyConfig.A_zimbraMailMode] = "http";
	this.attrs[ZaProxyConfig.A_zimbraMailPort] = ZaProxyConfig.DEFAULT_MAIL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraMailSSLPort] = ZaProxyConfig.DEFAULT_MAIL_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraAdminPort] = ZaProxyConfig.DEFAULT_ADMIN_CONSOLE_PORT_ZCS;
	
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] = "FALSE";
	this.attrs[ZaProxyConfig.A_zimbraImapBindPort] = ZaProxyConfig.DEFAULT_IMAP_PORT;
	this.attrs[ZaProxyConfig.A_zimbraImapSSLBindPort] = ZaProxyConfig.DEFAULT_IMAP_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraPop3BindPort] = ZaProxyConfig.DEFAULT_POP3_PORT;
	this.attrs[ZaProxyConfig.A_zimbraPop3SSLBindPort] = ZaProxyConfig.DEFAULT_POP3_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraImapCleartextLoginEnabled] = "FALSE";
	this.attrs[ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled] = "FALSE";
	
	this.initServerList();
}

ZaItem.initMethods["ZaDisableProxy"]= [ZaDisableProxy.init];

ZaDisableProxy.prototype.initServerList = function() {
	this[ZaProxyConfig.A2_proxy_name_array] = [];
	this[ZaProxyConfig.A2_mbx_name_array] = [];
	var servers = ZaServer.getAll().getArray();
	for(var i = 0; i < servers.length; i++) {
		var s = servers[i];
		if (s.attrs[ZaServer.A_zimbraMailProxyServiceEnabled]) { // we want to disable the "enabled" proxy server
			this[ZaProxyConfig.A2_proxy_name_array].push(s["zimbraServiceHostname"]);
		}
		
		if (s.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
			this[ZaProxyConfig.A2_mbx_name_array].push(s["zimbraServiceHostname"]);
		}
	}
	
	// As default, all mailbox servers are upstream/lookup target servers
	var mbxServers = this[ZaProxyConfig.A2_mbx_name_array];
	
	// Update proxy server choices
	ZaDisableProxyWizard.proxyServerChoices.setChoices(this[ZaProxyConfig.A2_proxy_name_array]);
	ZaDisableProxyWizard.proxyServerChoices.dirtyChoices();
	
	// Cache the all servers data for the later use
	var allServers = {};
	for(var i = 0; i < servers.length; i++) {
		var s = servers[i];
		allServers[s["zimbraServiceHostname"]] = s;
	}
	this[ZaProxyConfig.A2_all_servers] = allServers;
}

/*---Enable Proxy Wizard---*/
function ZaDisableProxyWizard (parent) {
	var w = "500px" ;
	if (AjxEnv.isIE) {
		w = "550px" ;
	}
	ZaXWizardDialog.call(this, parent, null, com_zimbra_proxy_config.LBL_DisableProxyWizTitle, w, "330px", "ZaDisableProxyWizard");

	this.stepChoices = [
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepSelectServer, value: ZaDisableProxyWizard.STEP_SELECT_SERVER},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepWebProxy,     value: ZaDisableProxyWizard.STEP_CONFIG_WEBPROXY},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepMailProxy,    value: ZaDisableProxyWizard.STEP_CONFIG_MAILPROXY},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepFinish,       value: ZaDisableProxyWizard.STEP_FINISH}
	];
	
	this.initForm(ZaDisableProxy.myXModel, this.getMyXForm());
	this._localXForm.setController();
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaDisableProxyWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaDisableProxyWizard.prototype.handleXFormChange));	
}

ZaDisableProxyWizard.prototype = new ZaXWizardDialog;
ZaDisableProxyWizard.prototype.constructor = ZaDisableProxyWizard;
ZaDisableProxyWizard.proxyServerChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaDisableProxyWizard.mbxServerChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaXDialog.XFormModifiers["ZaDisableProxyWizard"] = new Array();
ZaDisableProxyWizard.helpURL = location.pathname + "help/admin/html/tools/config_proxy.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

ZaDisableProxyWizard.STEP_INDEX = 1 ;
ZaDisableProxyWizard.STEP_SELECT_SERVER = ZaDisableProxyWizard.STEP_INDEX++;
ZaDisableProxyWizard.STEP_CONFIG_WEBPROXY = ZaDisableProxyWizard.STEP_INDEX++;
ZaDisableProxyWizard.STEP_CONFIG_MAILPROXY = ZaDisableProxyWizard.STEP_INDEX++;
ZaDisableProxyWizard.STEP_FINISH = ZaDisableProxyWizard.STEP_INDEX++;


ZaDisableProxyWizard.prototype.handleXFormChange = function () {
	var cStep = this._containedObject[ZaModel.currentStep];

	var obj = this._containedObject;
	if (cStep == ZaDisableProxyWizard.STEP_SELECT_SERVER) {
		if (!AjxUtil.isEmpty(obj[ZaProxyConfig.A2_target_server])){
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		}
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaDisableProxyWizard.prototype.popup = function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(ZaDisableProxyWizard.STEP_SELECT_SERVER);	
}

ZaDisableProxyWizard.prototype.changeButtonStateForStep = function(stepNum) {
	if(stepNum == ZaDisableProxyWizard.STEP_SELECT_SERVER) {
		// first step, prev is disabled
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		if (AjxUtil.isEmpty(this._containedObject[ZaProxyConfig.A2_target_server])) {
			// we can't continue until there is a target server
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(stepNum == ZaDisableProxyWizard.STEP_FINISH) {
		// last step, next is dsiabled
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	}
}

ZaDisableProxyWizard.prototype.goPage = function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaDisableProxyWizard.prototype.finishWizard = function() {
	try {	
		this.applyProxyConfig();
		this.popdown();	
			
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDisableProxyWizard.prototype.finishWizard", null, false);
	}
}

/**
 * Write the configured attrs to server
 */
ZaDisableProxyWizard.prototype.applyProxyConfig = function() {
	var instance = this._localXForm.getInstance() ;
	var allServers = instance[ZaProxyConfig.A2_all_servers];
	var proxyServer = allServers[instance[ZaProxyConfig.A2_target_server]];

	// 1) modify proxy server
	var proxyDoc = AjxSoapDoc.create("ModifyServerRequest", ZaZimbraAdmin.URN);
	proxyDoc.set("id", proxyServer.id);
	if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] == "FALSE") {
		// apply web proxy config
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, "FALSE");
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraMailSSLProxyPort, "0", md);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraMailProxyPort, "0", md);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraAdminProxyPort, "0", md);
	}
	
	if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] == "FALSE") {
		// apply mail proxy config
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyMailEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled]);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraImapProxyBindPort, "0", md);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraPop3ProxyBindPort, "0", md);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraImapSSLProxyBindPort, "0", md);
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraPop3SSLProxyBindPort, "0", md);
	}
	
	if (proxyServer.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] == "FALSE" &&
		proxyServer.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] == "FALSE") {
		ZaDisableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraServiceEnabled, "proxy", null, "-");
	}
    
	var params1 = new Object();
	params1.soapDoc = proxyDoc;	
	var reqMgrParams1 = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_SERVER
	}
	var resp = ZaRequestMgr.invoke(params1, reqMgrParams1).Body.ModifyServerResponse;		
	proxyServer.initFromJS(resp.server[0]);	
	
	// 2) modify upstream servers
	
	var upServers = proxyServer.attrs[ZaProxyConfig.A_zimbraReverseProxyUpstreamServers];
	if (upServers == null) {
		upServers = instance[ZaProxyConfig.A2_mbx_name_array];
	}
	
	var batchDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	batchDoc.setMethodAttribute("onerror", "stop");
	for (var s in upServers) {
		var md = batchDoc.set("ModifyServerRequest", null, null, ZaZimbraAdmin.URN);
		var server = allServers[upServers[s]];
		var id = server.id;
		batchDoc.set("id", id, md);
		
		if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] == "FALSE") {
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailSSLPort, instance.attrs[ZaProxyConfig.A_zimbraMailSSLPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailPort, instance.attrs[ZaProxyConfig.A_zimbraMailPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailMode, instance.attrs[ZaProxyConfig.A_zimbraMailMode], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraAdminPort, instance.attrs[ZaProxyConfig.A_zimbraAdminPort], md);
		}
		
		if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] == "FALSE") {
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapBindPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3BindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3BindPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapSSLBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapSSLBindPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3SSLBindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3SSLBindPort], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapCleartextLoginEnabled, instance.attrs[ZaProxyConfig.A_zimbraImapCleartextLoginEnabled], md);
			ZaDisableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled, instance.attrs[ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled], md);
		}
	}
	
	var params2 = new Object();
	params2.soapDoc = batchDoc;	
	var reqMgrParams2 = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_SERVER
	}
	var resp2 = ZaRequestMgr.invoke(params2, reqMgrParams2).Body.BatchResponse;

}

/**
 * Set attr to doc.
 * if val is undefined, doc[attr] = obj[attr];
 * otherwise, doc[attr] = val;
 * 
 * op, optional, may be "+" or "-"
 */
ZaDisableProxyWizard.setAttr = function(doc, attr, val, parent, op) {
	var attribute;
	if (parent) {
		attribute = doc.set("a", val, parent);
	} else {
		attribute = doc.set("a", val);
	}
	if (op) {
		attribute.setAttribute("n", op + attr);
	} else {
		attribute.setAttribute("n", attr);
	}
} 

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaDisableProxyWizard.prototype.setObject =
function(obj) {
	this._containedObject = obj ;
	
	// initialize the XWizard Model
	this._containedObject[ZaModel.currentStep] = obj[ZaModel.currentStep] || ZaDisableProxyWizard.STEP_SELECT_SERVER;
	
	if (AjxUtil.arrayContains(obj[ZaProxyConfig.A2_proxy_name_array], obj[ZaProxyConfig.A2_current_server])) {
		this._containedObject[ZaProxyConfig.A2_target_server] = obj[ZaProxyConfig.A2_current_server];
	} else {
		this._containedObject[ZaProxyConfig.A2_target_server] = undefined;
    }
 
	this._localXForm.setInstance(this._containedObject);
}

ZaDisableProxyWizard.isWebProxyDisabled = function() {
	var webProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyHttpEnabled);
	return (webProxyEnabled == "FALSE");
}

ZaDisableProxyWizard.isMailProxyDisabled = function() {
	var mailProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyMailEnabled);
	return (mailProxyEnabled == "FALSE");
}

ZaDisableProxyWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	
	// case 1: select server
	var case_select_server = {
		type:_CASE_, numCols:1, colSizes:["350px"],
		tabGroupKey:ZaDisableProxyWizard.STEP_SELECT_SERVER, caseKey:ZaDisableProxyWizard.STEP_SELECT_SERVER,
		align:_LEFT_, valign:_TOP_, width:"90%"
	};
	
	case_select_server.items = [
		{type: _SPACER_, height: 10},
		{type: _OUTPUT_, colSpan: 2, value: com_zimbra_proxy_config.LBL_ProxySelectProxyServer},
		{type: _SPACER_, height: 10},
		{type: _GROUP_, colSpan: "*", colSizes: ["260px", "*"],
		 items:[
			{type: _OSELECT1_, ref: ZaProxyConfig.A2_target_server, 
			 label: com_zimbra_proxy_config.LBL_ProxySelectProxyServerToDisableInDetail, 
			 labelLocation:_LEFT_, labelCssStyle: "text-align:left",
			 choices: ZaDisableProxyWizard.proxyServerChoices,
			 editable: false
			}
		 ]
		}
	];
	cases.push(case_select_server);
	
	// case 2: web proxy config
	var case_config_webproxy = {
		type:_CASE_, numCols:1, colSizes:["350px"], 
		tabGroupKey:ZaDisableProxyWizard.STEP_CONFIG_WEBPROXY, caseKey:ZaDisableProxyWizard.STEP_CONFIG_WEBPROXY,
		align:_LEFT_, valign:_TOP_, width:"90%"};
			
	case_config_webproxy.items = [
		{type: _GROUP_, numCols:2, colSpan: "*", colSizes:["100px","*"],
		 items: [
			{ type:_OUTPUT_, colSpan: "*", value: com_zimbra_proxy_config.LBL_ProxyWebProxyConfig, cssStyle: "font-weight:bold"},
			{ type:_OUTPUT_ , ref: ZaProxyConfig.A2_target_server, 
			  labelLocation:_LEFT_ , labelCssStyle: "text-align: left; font-weight:bold",
			  label: com_zimbra_proxy_config.LBL_ProxyServerName
			}
		 ]
		},
		{type:_SPACER_, height: 10},
		{type: _GROUP_, numCols:2, colSizes: ["120px", "auto"],
		 items: [
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyDisableWebProxy,
			 ref: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
			 trueValue: "FALSE", falseValue: "TRUE", labelCssStyle: "text-align: right"
			} // meant to converse true & false value
		 ]
		},
		{type: _GROUP_, numCols:2, colSizes: ["200px", "auto"],
		 visibilityChecks: [ZaDisableProxyWizard.isWebProxyDisabled],
		 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled],
		 items: [
			{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyWebMode,
			 ref: ZaProxyConfig.A_zimbraMailMode,
			 width: "60px"
			},
			{type:_SPACER_, height: 10},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpPort,
			 ref: ZaProxyConfig.A_zimbraMailPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpSSLPort,
			 ref: ZaProxyConfig.A_zimbraMailSSLPort,
			 width: "60px"
			},
			{type: _SPACER_, height: 10},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyAdminPort,
			 ref: ZaProxyConfig.A_zimbraAdminPort,
			 width: "60px"
			}
		 ]
		},
		{ type:_SPACER_, height: 10}
	];
	cases.push(case_config_webproxy);

	// case 3: mail proxy config
	var case_config_mailproxy = {
			type:_CASE_, numCols:1, colSizes:["350px"], 
			tabGroupKey:ZaDisableProxyWizard.STEP_CONFIG_MAILPROXY, caseKey:ZaDisableProxyWizard.STEP_CONFIG_MAILPROXY,
			align:_LEFT_, valign:_TOP_, width:"90%"};
				
	case_config_mailproxy.items = [
		{type: _GROUP_, numCols:2, colSpan: "*", colSizes:["100px","*"],
		 items: [
			{ type:_OUTPUT_, colSpan: "*", value: com_zimbra_proxy_config.LBL_ProxyMailProxyConfig, cssStyle: "font-weight:bold"},
			{ type:_OUTPUT_ , ref: ZaProxyConfig.A2_target_server, 
			  labelLocation:_LEFT_ , labelCssStyle: "text-align: left; font-weight:bold",
			  label: com_zimbra_proxy_config.LBL_ProxyServerName
			}
		 ]
		},
		{type:_SPACER_, height: 10},
		{type: _GROUP_, numCols:2, colSizes: ["120px", "auto"],
		 items: [
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyEnableMailProxy,
			 ref: ZaProxyConfig.A_zimbraReverseProxyMailEnabled,
			 trueValue: "FALSE", falseValue: "TRUE", labelCssStyle: "text-align: right"
			}  // mean to convese the true and false value
		 ]
		},
		{type: _GROUP_, numCols:2, colSizes: ["200px", "auto"],
		 visibilityChecks: [ZaDisableProxyWizard.isMailProxyDisabled],
		 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled],
		 items: [
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapPort,
			 ref: ZaProxyConfig.A_zimbraImapBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapSSLPort,
			 ref: ZaProxyConfig.A_zimbraImapSSLBindPort,
			 width: "60px"
			},
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_EnableImapCleartextLogin,
			 ref: ZaProxyConfig.A_zimbraImapCleartextLoginEnabled,
			 trueValue: "TRUE", falseValue: "FALSE",
			 labelCssStyle: "text-align: right"
			},
			{type: _SPACER_, height: 10},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3Port,
			 ref: ZaProxyConfig.A_zimbraPop3BindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3SSLPort,
			 ref: ZaProxyConfig.A_zimbraPop3SSLBindPort,
			 width: "60px"
			},
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_EnablePop3CleartextLogin,
			 ref: ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled,
			 trueValue: "TRUE", falseValue: "FALSE",
			 labelCssStyle: "text-align: right"
			}
		 ]
		}
	];
	cases.push(case_config_mailproxy);
	
	// case 4: complete
	var case_config_finish = {type:_CASE_, numCols:1, colSizes:["350px"], 
		tabGroupKey: ZaDisableProxyWizard.STEP_FINISH, caseKey: ZaDisableProxyWizard.STEP_FINISH,
		align:_LEFT_, valign:_TOP_};
	
	case_config_finish.items = [
		{type: _GROUP_, numCols:1, colSpan: "*", colSizes:["400px"],
	     items: [
			{ type:_SPACER_, height: 10},
			{ type:_OUTPUT_, 
			  labelLocation:_LEFT_ , labelCssStyle: "text-align: left",
			  label: com_zimbra_proxy_config.MSG_ProxyDisableFinish
			}
		 ]
		}
	];
	cases.push(case_config_finish);

    var w = "470px" ;  //500px-padding-left:15-padding-right:15
    if (AjxEnv.isIE) {
        w = "520px" ;
    }

    xFormObject.items = [
		{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
         choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
		{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
		{type:_SWITCH_,  width:w, align:_LEFT_, valign:_TOP_, items:cases}
	];
}
ZaXDialog.XFormModifiers["ZaDisableProxyWizard"].push(ZaDisableProxyWizard.myXFormModifier);
}