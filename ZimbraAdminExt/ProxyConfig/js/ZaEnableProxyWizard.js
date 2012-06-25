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

/*---Enable Proxy Wizard Model---*/
function ZaEnableProxy () {
	ZaItem.call(this, "ZaEnableProxy");
	this._init();
	this.type = ZaItem.ENABLE_PROXY ; 
}

ZaItem.ENABLE_PROXY = "enable_proxy" ;
ZaEnableProxy.prototype = new ZaItem ;
ZaEnableProxy.prototype.constructor = ZaEnableProxy;

ZaEnableProxy.myXModel = {
	items: [
		// web proxy
		{id: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyMailMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailMode, choices: ZaProxyConfig.MAIL_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraMailPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailPort},
		{id: ZaProxyConfig.A_zimbraMailProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailProxyPort},
		{id: ZaProxyConfig.A_zimbraMailSSLPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLPort},
		{id: ZaProxyConfig.A_zimbraMailSSLProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLProxyPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraAdminPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraAdminPort},
		{id: ZaProxyConfig.A_zimbraAdminProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraAdminProxyPort},
		{id: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		
		// mail proxy
		{id: ZaProxyConfig.A_zimbraReverseProxyMailEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraImapBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapBindPort},
		{id: ZaProxyConfig.A_zimbraImapProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapProxyBindPort},
		{id: ZaProxyConfig.A_zimbraImapSSLProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapSSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraImapSSLBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapSSLBindPort},
		{id: ZaProxyConfig.A_zimbraPop3BindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3BindPort},
		{id: ZaProxyConfig.A_zimbraPop3ProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3ProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3SSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3SSLBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3SSLBindPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES},
		
		// advanced
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, minInclusive: "1", maxInclusive: "16"},
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, minInclusive: "1"},
		{id: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyLogLevel, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyLogLevel, choices: ZaProxyConfig.PROXY_LOG_LEVEL_CHOICES},

		// utility
		{id: ZaProxyConfig.A2_mbx_name_array, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_proxy_name_array, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_target_server, type: _STRING_},
		{id: ZaProxyConfig.A2_target_up_servers, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_target_lt_servers, type: _LIST_, itemType: _STRING_},
		{id: ZaProxyConfig.A2_all_mailbox_as_upstream, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A2_all_mailbox_as_lookuptarget, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES}
	]
}

ZaEnableProxy.init = function () {
	this.attrs = {};
	
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] = "TRUE";
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyMailMode] = ZaProxyConfig.DEFAULT_MAIL_MODE;
	this.attrs[ZaProxyConfig.A_zimbraMailPort] = ZaProxyConfig.DEFAULT_MAIL_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraMailProxyPort] = ZaProxyConfig.DEFAULT_MAIL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraMailSSLPort] = ZaProxyConfig.DEFAULT_MAIL_SSL_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraMailSSLProxyPort] = ZaProxyConfig.DEFAULT_MAIL_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyAdminEnabled] = "FALSE";
	this.attrs[ZaProxyConfig.A_zimbraAdminPort] = ZaProxyConfig.DEFAULT_ADMIN_CONSOLE_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraAdminProxyPort] = ZaProxyConfig.DEFAULT_ADMIN_CONSOLE_PORT;
	this.attrs[ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled] = "TRUE";
	
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] = "TRUE";
	this.attrs[ZaProxyConfig.A_zimbraImapBindPort] = ZaProxyConfig.DEFAULT_IMAP_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraImapProxyBindPort] = ZaProxyConfig.DEFAULT_IMAP_PORT;
	this.attrs[ZaProxyConfig.A_zimbraImapSSLBindPort] = ZaProxyConfig.DEFAULT_IMAP_SSL_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraImapSSLProxyBindPort] = ZaProxyConfig.DEFAULT_IMAP_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraPop3BindPort] = ZaProxyConfig.DEFAULT_POP3_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraPop3ProxyBindPort] = ZaProxyConfig.DEFAULT_POP3_PORT;
	this.attrs[ZaProxyConfig.A_zimbraPop3SSLBindPort] = ZaProxyConfig.DEFAULT_POP3_SSL_PORT_ZCS;
	this.attrs[ZaProxyConfig.A_zimbraPop3SSLProxyBindPort] = ZaProxyConfig.DEFAULT_POP3_SSL_PORT;
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode] = "only";
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode] = "only";
	
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses] = 4;
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyWorkerConnections] = 10240;
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyLogLevel] = "info";
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname] = "TRUE";
	this.attrs[ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled] = "TRUE";
	
	this[ZaProxyConfig.A2_all_mailbox_as_upstream] = "TRUE";
	this[ZaProxyConfig.A2_all_mailbox_as_lookuptarget] = "TRUE";
	
	this.initServerList();
}

ZaItem.initMethods["ZaEnableProxy"]= [ZaEnableProxy.init];

ZaEnableProxy.prototype.initServerList = function() {
	this[ZaProxyConfig.A2_proxy_name_array] = [];
	this[ZaProxyConfig.A2_mbx_name_array] = [];
	var servers = ZaServer.getAll().getArray();
	for(var i = 0; i < servers.length; i++) {
		var s = servers[i];
		if (s.attrs[ZaServer.A_zimbraMailProxyServiceInstalled]) {
			this[ZaProxyConfig.A2_proxy_name_array].push(s["zimbraServiceHostname"]);
		}
		
		if (s.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
			this[ZaProxyConfig.A2_mbx_name_array].push(s["zimbraServiceHostname"]);
		}
	}
	
	// As default, all mailbox servers are upstream/lookup target servers
	var mbxServers = this[ZaProxyConfig.A2_mbx_name_array];
	var upServers = [];
	var ltServers = [];
	for (var j = 0; j < mbxServers.length; j++) {
		upServers.push(mbxServers[j]);
		ltServers.push(mbxServers[j]);
	}
	this[ZaProxyConfig.A2_target_up_servers] = upServers;
	this[ZaProxyConfig.A2_target_lt_servers] = ltServers;
	
	// Update proxy server choices
	ZaEnableProxyWizard.proxyServerChoices.setChoices(this[ZaProxyConfig.A2_proxy_name_array]);
	ZaEnableProxyWizard.proxyServerChoices.dirtyChoices();
	
	// Update mailbox server choices
	ZaEnableProxyWizard.mbxServerChoices.setChoices(this[ZaProxyConfig.A2_mbx_name_array]);
	ZaEnableProxyWizard.mbxServerChoices.dirtyChoices();
	
	// Cache the all servers data for the later use
	var allServers = {};
	for(var i = 0; i < servers.length; i++) {
		var s = servers[i];
		allServers[s["zimbraServiceHostname"]] = s;
	}
	this[ZaProxyConfig.A2_all_servers] = allServers;
}

/*---Enable Proxy Wizard---*/
function ZaEnableProxyWizard (parent) {
	var w = "500px" ;
	if (AjxEnv.isIE) {
		w = "550px" ;
	}
	ZaXWizardDialog.call(this, parent, null, com_zimbra_proxy_config.LBL_EnableProxyWizTitle, w, "330px", "ZaEnableProxyWizard");

	this.stepChoices = [
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepSelectServer, value: ZaEnableProxyWizard.STEP_SELECT_SERVER},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepWebProxy,     value: ZaEnableProxyWizard.STEP_CONFIG_WEBPROXY},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepMailProxy,    value: ZaEnableProxyWizard.STEP_CONFIG_MAILPROXY},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepAdvanced,     value: ZaEnableProxyWizard.STEP_CONFIG_ADVANCED},
		{label: com_zimbra_proxy_config.LBL_ProxyWizardStepFinish,       value: ZaEnableProxyWizard.STEP_FINISH}
	];
	
	this.initForm(ZaEnableProxy.myXModel, this.getMyXForm());
	this._localXForm.setController();
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaEnableProxyWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaEnableProxyWizard.prototype.handleXFormChange));	
}

ZaEnableProxyWizard.prototype = new ZaXWizardDialog;
ZaEnableProxyWizard.prototype.constructor = ZaEnableProxyWizard;
ZaEnableProxyWizard.proxyServerChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaEnableProxyWizard.mbxServerChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaXDialog.XFormModifiers["ZaEnableProxyWizard"] = new Array();
ZaEnableProxyWizard.helpURL = location.pathname + "help/admin/html/tools/config_proxy.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

ZaEnableProxyWizard.STEP_INDEX = 1 ;
ZaEnableProxyWizard.STEP_SELECT_SERVER = ZaEnableProxyWizard.STEP_INDEX++;
ZaEnableProxyWizard.STEP_CONFIG_WEBPROXY = ZaEnableProxyWizard.STEP_INDEX++;
ZaEnableProxyWizard.STEP_CONFIG_MAILPROXY = ZaEnableProxyWizard.STEP_INDEX++;
ZaEnableProxyWizard.STEP_CONFIG_ADVANCED = ZaEnableProxyWizard.STEP_INDEX++;
ZaEnableProxyWizard.STEP_FINISH = ZaEnableProxyWizard.STEP_INDEX++;


ZaEnableProxyWizard.prototype.handleXFormChange = function () {
	var cStep = this._containedObject[ZaModel.currentStep];

	var obj = this._containedObject;
	if (cStep == ZaEnableProxyWizard.STEP_SELECT_SERVER) {
		if (!AjxUtil.isEmpty(obj[ZaProxyConfig.A2_target_server]) &&
			this.isUpstreamServersConfigValid() &&
			this.isLookupTargetServersConfigValid()){
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		}
	}
}

ZaEnableProxyWizard.prototype.isUpstreamServersConfigValid = function() {
	var obj = this._containedObject;
	if (!AjxUtil.isEmpty(obj[ZaProxyConfig.A2_target_up_servers]) ||
		obj[ZaProxyConfig.A2_all_mailbox_as_upstream] == "TRUE") {
		return true;
	} else {
		return false;
	}
}

ZaEnableProxyWizard.prototype.isLookupTargetServersConfigValid = function() {
	var obj = this._containedObject;
	if (!AjxUtil.isEmpty(obj[ZaProxyConfig.A2_target_lt_servers]) ||
		obj[ZaProxyConfig.A2_all_mailbox_as_lookuptarget] == "TRUE") {
		return true;
	} else {
		return false;
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaEnableProxyWizard.prototype.popup = function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(ZaEnableProxyWizard.STEP_SELECT_SERVER);	
}

ZaEnableProxyWizard.prototype.changeButtonStateForStep = function(stepNum) {
	if(stepNum == ZaEnableProxyWizard.STEP_SELECT_SERVER) {
		// first step, prev is disabled
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		if (AjxUtil.isEmpty(this._containedObject[ZaProxyConfig.A2_target_server])) {
			// we can't continue until there is a target server
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(stepNum == ZaEnableProxyWizard.STEP_FINISH) {
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

ZaEnableProxyWizard.prototype.goPage = function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaEnableProxyWizard.prototype.finishWizard = function() {
	try {	
		this.applyProxyConfig();
		this.popdown();	
			
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaEnableWizard.prototype.finishWizard", null, false);
	}
}

/**
 * Write the configured attrs to server
 */
ZaEnableProxyWizard.prototype.applyProxyConfig = function() {
	var instance = this._localXForm.getInstance() ;
	
	// 1) modify upstream servers
	var flags = {};
	var allServers = instance[ZaProxyConfig.A2_all_servers];
	var targetServers;
	if (instance[ZaProxyConfig.A2_all_mailbox_as_upstream] == "TRUE") {
		targetServers = instance[ZaProxyConfig.A2_mbx_name_array];
	} else {
		targetServers = instance[ZaProxyConfig.A2_target_up_servers];
	}
	
	for (var i = 0; i < targetServers.length; i++) {
		var s = targetServers[i];
		flags[s] = {isUpstream: true};
	}
	
	if (instance[ZaProxyConfig.A2_all_mailbox_as_lookuptarget] == "TRUE") {
		targetServers = instance[ZaProxyConfig.A2_mbx_name_array];
	} else {
		targetServers = instance[ZaProxyConfig.A2_target_lt_servers];
	}
	
	for (var i = 0; i < targetServers.length; i++) {
		var s = targetServers[i];
		if (!flags[s]) {
			flags[s] = {isLookupTarget: true};
		}
	}
	
	var batchDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	batchDoc.setMethodAttribute("onerror", "stop");
	for (var s in flags) {
		var md = batchDoc.set("ModifyServerRequest", null, null, ZaZimbraAdmin.URN);
		var id = allServers[s].id;
		batchDoc.set("id", id, md);
		
		ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraReverseProxyLookupTarget, "TRUE", md);
		
		if (flags[s].isUpstream) {
			// apply web proxy in upstream
			if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] == "TRUE") {
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailSSLPort, instance.attrs[ZaProxyConfig.A_zimbraMailSSLPort], md);
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailPort, instance.attrs[ZaProxyConfig.A_zimbraMailPort], md);
				if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled] == "TRUE") {
					ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailMode, "https", md);
				} else {
					ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraMailMode, "http", md);
				}
				
				if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyAdminEnabled] == "TRUE") {
					ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraAdminPort, instance.attrs[ZaProxyConfig.A_zimbraAdminPort], md);
				}
			}
			
			// apply mail proxy in upstream
			if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] == "TRUE") {
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapBindPort], md);
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3BindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3BindPort], md);
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapSSLBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapSSLBindPort], md);
				ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3SSLBindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3SSLBindPort], md);
				if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled] != "TRUE") {
					ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraImapCleartextLoginEnabled, "TRUE", md);
					ZaEnableProxyWizard.setAttr(batchDoc, ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled, "TRUE", md);
				}
			}
		}
	}
	
	var params1 = new Object();
	params1.soapDoc = batchDoc;	
	var reqMgrParams1 = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_SERVER
	}
	var resp1 = ZaRequestMgr.invoke(params1, reqMgrParams1).Body.BatchResponse;
	
	
	// 2) modify proxy server
	var proxyServer = allServers[instance[ZaProxyConfig.A2_target_server]];
	
	var proxyDoc = AjxSoapDoc.create("ModifyServerRequest", ZaZimbraAdmin.URN);
	proxyDoc.set("id", proxyServer.id);
	if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled] == "TRUE") {
		// apply web proxy config
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyMailMode, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailMode]);
		var mailmode = instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailMode];
		if (mailmode != "https") {
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraMailProxyPort, instance.attrs[ZaProxyConfig.A_zimbraMailProxyPort]);
		}
		if (mailmode != "http") {
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraMailSSLProxyPort, instance.attrs[ZaProxyConfig.A_zimbraMailSSLProxyPort]);
		}
		if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyAdminEnabled] == "TRUE") {
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyAdminEnabled]);
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraAdminProxyPort, instance.attrs[ZaProxyConfig.A_zimbraAdminProxyPort]);
		}
	}
	
	if (instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled] == "TRUE") {
		// apply mail proxy config
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyMailEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyMailEnabled]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraImapProxyBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapProxyBindPort]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraImapSSLProxyBindPort, instance.attrs[ZaProxyConfig.A_zimbraImapSSLProxyBindPort]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraPop3ProxyBindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3ProxyBindPort]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraPop3SSLProxyBindPort, instance.attrs[ZaProxyConfig.A_zimbraPop3SSLProxyBindPort]);
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode]);
	}
	
	// apply advanced proxy config
	ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses]);
	ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyWorkerConnections]);
	ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled]);
	ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyLogLevel, instance.attrs[ZaProxyConfig.A_zimbraReverseProxyLogLevel]);
	ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, instance.attrs[ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled]);
	
	// apply upstream settings
	if (instance[ZaProxyConfig.A2_all_mailbox_as_upstream]) {
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyUpstreamServers, "");
	} else {
		var upArr = instance[ZaProxyConfig.A2_target_up_servers];
		for (var i = 0; i < upArr.length; i++) {
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyUpstreamServers, upArr[i]);
		}
	}
	
	if (instance[ZaProxyConfig.A2_all_mailbox_as_lookuptarget]) {
		ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyAvailableLookupTargets, "");
	} else {
		var ltArr = instance[ZaProxyConfig.A2_target_lt_servers];
		for (var i = 0; i < ltArr.length; i++) {
			ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraReverseProxyAvailableLookupTargets, ltArr[i]);
		}
	}
	
    ZaEnableProxyWizard.setAttr(proxyDoc, ZaProxyConfig.A_zimbraServiceEnabled, "proxy", null, "+");
    
	// 3) send request and renew server with the response
	var params2 = new Object();
	params2.soapDoc = proxyDoc;	
	var reqMgrParams2 = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_SERVER
	}
	var resp = ZaRequestMgr.invoke(params2, reqMgrParams2).Body.ModifyServerResponse;		
	proxyServer.initFromJS(resp.server[0]);		
}

/**
 * Set attr to doc.
 * if val is undefined, doc[attr] = obj[attr];
 * otherwise, doc[attr] = val;
 * 
 * op, optional, may be "+" or "-"
 */
ZaEnableProxyWizard.setAttr = function(doc, attr, val, parent, op) {
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
ZaEnableProxyWizard.prototype.setObject =
function(obj) {
	this._containedObject = obj ;
	
	// initialize the XWizard Model
	this._containedObject[ZaModel.currentStep] = obj[ZaModel.currentStep] || ZaEnableProxyWizard.STEP_SELECT_SERVER;
	
	if (AjxUtil.arrayContains(obj[ZaProxyConfig.A2_proxy_name_array], obj[ZaProxyConfig.A2_current_server])) {
		this._containedObject[ZaProxyConfig.A2_target_server] = obj[ZaProxyConfig.A2_current_server];
	} else {
		this._containedObject[ZaProxyConfig.A2_target_server] = undefined;
    }
 
	this._localXForm.setInstance(this._containedObject);
}

ZaEnableProxyWizard.isWebProxyEnabled = function() {
	var webProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyHttpEnabled);
	return (webProxyEnabled == "TRUE");
}

ZaEnableProxyWizard.isAdminProxyEnabled = function() {
	var adminProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyAdminEnabled);
	return (adminProxyEnabled == "TRUE");
}

ZaEnableProxyWizard.isMailProxyEnabled = function() {
	var mailProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyMailEnabled);
	return (mailProxyEnabled == "TRUE");
}

ZaEnableProxyWizard.isProxyMailModeNotHTTP = function() {
	var mailmode = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyMailMode);
	return (mailmode != "http");
}

ZaEnableProxyWizard.isProxyMailModeNotHTTPS = function() {
	var mailmode = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyMailMode);
	return (mailmode != "https");
}

ZaEnableProxyWizard.isSSLToUpstreamEnabled = function() {
	var uptossl = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled);
	return (uptossl == "TRUE");
}

ZaEnableProxyWizard.isSSLToUpstreamDisabled = function() {
	return !ZaEnableProxyWizard.isSSLToUpstreamEnabled.call(this);
}

ZaEnableProxyWizard.isNotAllMbxAsUp = function() {
	var allMbxAsUp = this.getInstanceValue(ZaProxyConfig.A2_all_mailbox_as_upstream);
	return (allMbxAsUp == "FALSE");
}

ZaEnableProxyWizard.isNotAllMbxAsLT = function() {
	var allMbxAsLT = this.getInstanceValue(ZaProxyConfig.A2_all_mailbox_as_lookuptarget);
	return (allMbxAsLT == "FALSE");
}

ZaEnableProxyWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	
	// case 1: select server
	var case_select_server = {
		type:_CASE_, numCols:1, colSizes:["350px"],
		tabGroupKey:ZaEnableProxyWizard.STEP_SELECT_SERVER, caseKey:ZaEnableProxyWizard.STEP_SELECT_SERVER,
		align:_LEFT_, valign:_TOP_, width:"90%"
	};
	
	case_select_server.items = [
		{type: _SPACER_, height: 10},
		{type: _OUTPUT_, colSpan: 2, value: com_zimbra_proxy_config.LBL_ProxySelectProxyServer},
		{type: _SPACER_, height: 10},
		{type: _GROUP_, colSpan: "*", colSizes: ["260px", "*"],
		 items:[
			{type: _OSELECT1_, ref: ZaProxyConfig.A2_target_server, 
			 label: com_zimbra_proxy_config.LBL_ProxySelectProxyServerToEnableInDetail, 
			 labelLocation:_LEFT_, labelCssStyle: "text-align:left",
			 choices: ZaEnableProxyWizard.proxyServerChoices,
			 editable: false
			}
		]
		},
		{type: _SPACER_, height: 20},
		{type: _GROUP_, colSpan: "*", colSizes: ["15px", "*"],
		 items:[
			{type: _OUTPUT_, colSpan: 2, value: com_zimbra_proxy_config.LBL_ProxySelectUpServer},
			{type: _SPACER_, height: 10},
			{type: _CHECKBOX_, ref: ZaProxyConfig.A2_all_mailbox_as_upstream,
			 label: com_zimbra_proxy_config.LBL_ProxyAllMailboxAsUp,
			 trueValue: "TRUE", falseValue: "FALSE", labelLocation: _RIGHT_
			},
			{type: _SPACER_, height: "10px"},
			{type: _OUTPUT_, colSpan: "*", label: "", labelCssSytle: "padding-left:10px",
			 value: com_zimbra_proxy_config.LBL_ProxyLimitUp,
			 visibilityChecks: [ZaEnableProxyWizard.isNotAllMbxAsUp],
			 visibilityChangeEventSources: [ZaProxyConfig.A2_all_mailbox_as_upstream]
			},
			{type: _OSELECT_CHECK_, ref: ZaProxyConfig.A2_target_up_servers, colSpan: "*",
			 cssStyle: "margin-bottom:5px;margin-top:5px;border:2px inset gray;",
			 width: "250px", choices: ZaEnableProxyWizard.mbxServerChoices,
			 visibilityChecks: [ZaEnableProxyWizard.isNotAllMbxAsUp],
			 visibilityChangeEventSources: [ZaProxyConfig.A2_all_mailbox_as_upstream]
			}
		 ]
		},
		{type: _SPACER_, height: 20},
		{type: _GROUP_,  colSpan: "*", colSizes: ["15px", "*"],
		 items:[
			{type: _OUTPUT_, colSpan: 2, value: com_zimbra_proxy_config.LBL_ProxySelectLTServer},
			{type: _SPACER_, height: 10},
			{type: _CHECKBOX_, ref: ZaProxyConfig.A2_all_mailbox_as_lookuptarget,
			 label: com_zimbra_proxy_config.LBL_ProxyAllMailboxAsLT,
			 trueValue: "TRUE", falseValue: "FALSE", labelLocation: _RIGHT_
			},
			{type: _SPACER_, height: "10px"},
			{type: _OUTPUT_, colSpan: "*", label: "", labelCssSytle: "padding-left:10px",
			 value: com_zimbra_proxy_config.LBL_ProxyLimitLT,
			 visibilityChecks: [ZaEnableProxyWizard.isNotAllMbxAsLT],
			 visibilityChangeEventSources: [ZaProxyConfig.A2_all_mailbox_as_lookuptarget]
			},
			{type: _OSELECT_CHECK_, ref: ZaProxyConfig.A2_target_lt_servers, colSpan: "*",
			 cssStyle: "margin-bottom:5px;margin-top:5px;border:2px inset gray;",
			 width: "250px", choices: ZaEnableProxyWizard.mbxServerChoices,
			 visibilityChecks: [ZaEnableProxyWizard.isNotAllMbxAsLT],
			 visibilityChangeEventSources: [ZaProxyConfig.A2_all_mailbox_as_lookuptarget]
			}
			 
		 ]
		}	
	];
	cases.push(case_select_server);
	
	// case 2: web proxy config
	var case_config_webproxy = {
		type:_CASE_, numCols:1, colSizes:["350px"], 
		tabGroupKey:ZaEnableProxyWizard.STEP_CONFIG_WEBPROXY, caseKey:ZaEnableProxyWizard.STEP_CONFIG_WEBPROXY,
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
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyEnableWebProxy,
			 ref: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
			 trueValue: "TRUE", falseValue: "FALSE", labelCssStyle: "text-align: right"
			}
		 ]
		},
		{type: _GROUP_, numCols:2, colSizes: ["200px", "auto"],
		 visibilityChecks: [ZaEnableProxyWizard.isWebProxyEnabled],
		 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled],
		 items: [
			{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyWebProxyMode,
			 ref: ZaProxyConfig.A_zimbraReverseProxyMailMode,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpProxyPort,
			 ref: ZaProxyConfig.A_zimbraMailProxyPort,
			 width: "60px",
			 visibilityChecks: [ZaEnableProxyWizard.isProxyMailModeNotHTTPS],
			 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailMode]
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpSSLProxyPort,
			 ref: ZaProxyConfig.A_zimbraMailSSLProxyPort,
			 width: "60px",
			 visibilityChecks: [ZaEnableProxyWizard.isProxyMailModeNotHTTP],
			 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailMode]
			},
			{type:_SPACER_, height: 10},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpUpPort,
			 ref: ZaProxyConfig.A_zimbraMailPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpSSLUpPort,
			 ref: ZaProxyConfig.A_zimbraMailSSLPort,
			 width: "60px"
			},
			{type: _SPACER_, height: 10},
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyAdminEnabled,
			 ref: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled,
			 trueValue: "TRUE", falseValue: "FALSE", labelCssStyle: "text-align: right"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyAdminProxyPort,
			 ref: ZaProxyConfig.A_zimbraAdminProxyPort,
			 width: "60px",
			 visibilityChecks: [ZaEnableProxyWizard.isAdminProxyEnabled],
			 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyAdminEnabled]
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyAdminUpPort,
			 ref: ZaProxyConfig.A_zimbraAdminPort,
			 width: "60px",
			 visibilityChecks: [ZaEnableProxyWizard.isAdminProxyEnabled],
			 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyAdminEnabled]
			},
		 ]
		},
		{ type:_SPACER_, height: 10}
	];
	cases.push(case_config_webproxy);

	// case 3: mail proxy config
	var case_config_mailproxy = {
			type:_CASE_, numCols:1, colSizes:["350px"], 
			tabGroupKey:ZaEnableProxyWizard.STEP_CONFIG_MAILPROXY, caseKey:ZaEnableProxyWizard.STEP_CONFIG_MAILPROXY,
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
			 trueValue: "TRUE", falseValue: "FALSE", labelCssStyle: "text-align: right"
			}
		 ]
		},
		{type: _GROUP_, numCols:2, colSizes: ["200px", "auto"],
		 visibilityChecks: [ZaEnableProxyWizard.isMailProxyEnabled],
		 visibilityChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled],
		 items: [
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapProxyPort,
			 ref: ZaProxyConfig.A_zimbraImapProxyBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapSSLProxyPort,
			 ref: ZaProxyConfig.A_zimbraImapSSLProxyBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapUpPort,
			 ref: ZaProxyConfig.A_zimbraImapBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapSSLUpPort,
			 ref: ZaProxyConfig.A_zimbraImapSSLBindPort,
			 width: "60px"
			},
			{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyImapStartTlsMode,
			 ref: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode
			},
			{type: _SPACER_, height: 10},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3ProxyPort,
			 ref: ZaProxyConfig.A_zimbraPop3ProxyBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3SSLProxyPort,
			 ref: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3UpPort,
			 ref: ZaProxyConfig.A_zimbraPop3BindPort,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3SSLUpPort,
			 ref: ZaProxyConfig.A_zimbraPop3SSLBindPort,
			 width: "60px"
			},
			{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyPop3StartTlsMode,
			 ref: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode
			}
		 ]
		}
	];
	cases.push(case_config_mailproxy);

	// case 4: advanced config
	var case_config_advanced = {type:_CASE_, numCols:1, colSizes:["350px"], 
			tabGroupKey: ZaEnableProxyWizard.STEP_CONFIG_ADVANCED, caseKey: ZaEnableProxyWizard.STEP_CONFIG_ADVANCED,
			align:_LEFT_, valign:_TOP_};
			
	case_config_advanced.items = [
		{type: _GROUP_, numCols:2, colSpan: "*", colSizes:["100px", "*"],
		 items: [
			{ type:_OUTPUT_, colSpan: "*", value: com_zimbra_proxy_config.LBL_ProxyGeneralConfig, cssStyle: "font-weight:bold"},
			{ type:_OUTPUT_ , ref: ZaProxyConfig.A2_target_server, 
			  labelLocation:_LEFT_ , labelCssStyle: "text-align: left; font-weight:bold",
			  label: com_zimbra_proxy_config.LBL_ProxyServerName
			}
		 ]
		},
		{type:_SPACER_, height: 10},
		{type: _GROUP_, numCols:2, colSizes: ["250px", "auto"],
		 items: [
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyWorkerProcessNum,
			 ref: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses,
			 width: "60px"
			},
			{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyWorkerConnectionNum,
			 ref: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections,
			 width: "60px"
			},
			{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyLogLevel,
			 ref: ZaProxyConfig.A_zimbraReverseProxyLogLevel,
			 getDisplayValue: function (val) {
				 return val;
			 }
			},
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyUseSSLToUpstream,
			 ref: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled,
			 trueValue: "TRUE", falseValue: "FALSE",labelCssStyle: "text-align: right"
			},
			{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyAllowServerResolveRoute,
			 ref: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
			 labelCssStyle: "text-align: right",
			 trueValue: "FALSE", falseValue: "TRUE" // the true and false value are meant to be reversed
			}
		  ]
		},
        { type:_SPACER_, height: 10}
    ];
	cases.push(case_config_advanced);
	
	var case_config_finish = {type:_CASE_, numCols:1, colSizes:["350px"], 
		tabGroupKey: ZaEnableProxyWizard.STEP_FINISH, caseKey: ZaEnableProxyWizard.STEP_FINISH,
		align:_LEFT_, valign:_TOP_};
	
	case_config_finish.items = [
		{type: _GROUP_, numCols:1, colSpan: "*", colSizes:["400px"],
	     items: [
			{ type:_SPACER_, height: 10},
			{ type:_OUTPUT_, 
			  labelLocation:_LEFT_ , labelCssStyle: "text-align: left",
			  label: com_zimbra_proxy_config.MSG_ProxyEnableFinish
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
ZaXDialog.XFormModifiers["ZaEnableProxyWizard"].push(ZaEnableProxyWizard.myXFormModifier);
