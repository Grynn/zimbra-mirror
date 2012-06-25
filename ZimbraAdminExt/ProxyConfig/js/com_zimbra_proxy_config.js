/**
 * This zimlet will add proxy config features to Zimbra Admin Console.
 * It push additional XForm items and XModel items into the XForm Object
 * of global configs and server configs. There is no need to add custom
 * MVC code.
 *
 * @author jiankuan@zimbra.com
 * @since  ZCS 8.0
 */

if (window.console && window.console.log) {
	console.log("Loading com_zimbra_proxy_config.js");
}


if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_proxy_config"]) {

/*------------------------Model Section------------------------*/

// we only add tabs to global config and server xform view, therefore we need not to
// create ZaItem and ZaXFormView object. ZaProxyConfig is used for namespace. It's not
// a ZaItem like ZaCos or ZaAccount
ZaProxyConfig = function() {};

ZaProxyConfig.MAIL_MODE_CHOICES = ["http", "https", "both", "mixed", "redirect"];
ZaProxyConfig.CLIENT_CERT_MODE_CHOICES = ["off", "on", "optional"];
ZaProxyConfig.STARTTLS_MODE_CHOICES = [
	{label: com_zimbra_proxy_config.LBL_ProxyStartTLSMode_OFF,  value: "off"},
	{label: com_zimbra_proxy_config.LBL_ProxyStartTLSMode_ON,   value: "on"},
	{label: com_zimbra_proxy_config.LBL_ProxyStartTLSMode_ONLY, value: "only"}
];
ZaProxyConfig.PROXY_LOG_LEVEL_CHOICES = ["crit", "error", "warn", "notice", "info", "debug_zimbra", "debug_http", "debug_mail", "debug"];

// Proxy Advanced Configurations
ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses = "zimbraReverseProxyWorkerProcesses";
ZaProxyConfig.A_zimbraReverseProxyWorkerConnections = "zimbraReverseProxyWorkerConnections";
ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled = "zimbraReverseProxyDnsLookupInServerEnabled";
ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname = "zimbraReverseProxyGenConfigPerVirtualHostname";
ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress = "zimbraReverseProxyAdminIPAddress";
ZaProxyConfig.A_zimbraReverseProxyLogLevel = "zimbraReverseProxyLogLevel";
ZaProxyConfig.A_zimbraReverseProxyUpstreamServers = "zimbraReverseProxyUpstreamServers";
ZaProxyConfig.A_zimbraReverseProxyAvailableLookupTargets = "zimbraReverseProxyAvailableLookupTargets";

// Web Proxy Configurations
ZaProxyConfig.A_zimbraReverseProxyHttpEnabled = "zimbraReverseProxyHttpEnabled";
ZaProxyConfig.A_zimbraReverseProxyMailMode = "zimbraReverseProxyMailMode";
ZaProxyConfig.A_zimbraMailProxyPort = "zimbraMailProxyPort";
ZaProxyConfig.A_zimbraMailSSLProxyPort = "zimbraMailSSLProxyPort";
ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort = "zimbraMailSSLProxyClientCertPort";
ZaProxyConfig.A_zimbraReverseProxyClientCertMode = "zimbraReverseProxyClientCertMode";
ZaProxyConfig.A_zimbraReverseProxyAdminEnabled = "zimbraReverseProxyAdminEnabled";
ZaProxyConfig.A_zimbraAdminProxyPort = "zimbraAdminProxyPort";
ZaProxyConfig.A_zimbraAdminPort = "zimbraAdminPort";
ZaProxyConfig.A_zimbraMailPort = "zimbraMailPort";
ZaProxyConfig.A_zimbraMailSSLPort = "zimbraMailSSLPort";
ZaProxyConfig.A_zimbraMailMode = "zimbraMailMode";
ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled = "zimbraReverseProxySSLToUpstreamEnabled";

// Mail Proxy Configurations
ZaProxyConfig.A_zimbraReverseProxyMailEnabled = "zimbraReverseProxyMailEnabled";
ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval = "zimbraReverseProxyAuthWaitInterval";
ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled = "zimbraReverseProxyImapSaslPlainEnabled";
ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled = "zimbraReverseProxyPop3SaslPlainEnabled";
ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled = "zimbraReverseProxyImapSaslGssapiEnabled";
ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled = "zimbraReverseProxyPop3SaslGssapiEnabled";
ZaProxyConfig.A_zimbraImapProxyBindPort = "zimbraImapProxyBindPort";
ZaProxyConfig.A_zimbraPop3ProxyBindPort = "zimbraPop3ProxyBindPort";
ZaProxyConfig.A_zimbraImapSSLProxyBindPort = "zimbraImapSSLProxyBindPort";
ZaProxyConfig.A_zimbraPop3SSLProxyBindPort = "zimbraPop3SSLProxyBindPort";
ZaProxyConfig.A_zimbraImapBindPort = "zimbraImapBindPort";
ZaProxyConfig.A_zimbraPop3BindPort = "zimbraPop3BindPort";
ZaProxyConfig.A_zimbraImapSSLBindPort = "zimbraImapSSLBindPort";
ZaProxyConfig.A_zimbraPop3SSLBindPort = "zimbraPop3SSLBindPort";
ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode = "zimbraReverseProxyImapStartTlsMode";
ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode = "zimbraReverseProxyPop3StartTlsMode";
ZaProxyConfig.A_zimbraImapCleartextLoginEnabled = "zimbraImapCleartextLoginEnabled";
ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled = "zimbraPop3CleartextLoginEnabled";

// other
ZaProxyConfig.A_zimbraServiceEnabled = "zimbraServiceEnabled";
ZaProxyConfig.A_zimbraReverseProxyLookupTarget = "zimbraReverseProxyLookupTarget";
ZaProxyConfig.A_zimbraImapCleartextLoginEnabled = "zimbraImapCleartextLoginEnabled";
ZaProxyConfig.A_zimbraPop3CleartextLoginEnabled = "zimbraPop3CleartextLoginEnabled";

// utility
ZaProxyConfig.A2_proxy_name_array = "proxy_name_array";
ZaProxyConfig.A2_mbx_name_array = "mbx_name_array";
ZaProxyConfig.A2_target_server = "proxy_target_server";
ZaProxyConfig.A2_target_up_servers = "proxy_target_up_servers";
ZaProxyConfig.A2_target_lt_servers = "proxy_target_lt_servers";
ZaProxyConfig.A2_current_server = "proxy_current_server"; // the server in whose UI the wizard is opened
ZaProxyConfig.A2_all_mailbox_as_upstream = "proxy_all_mailbox_as_upstream";
ZaProxyConfig.A2_all_mailbox_as_lookuptarget = "proxy_all_mailbox_as_lookuptarget";
ZaProxyConfig.A2_all_servers = "proxy_all_servers";

// Default Values
ZaProxyConfig.DEFAULT_MAIL_MODE = "http";
ZaProxyConfig.DEFAULT_MAIL_PORT = 80;
ZaProxyConfig.DEFAULT_MAIL_PORT_ZCS = 8080;
ZaProxyConfig.DEFAULT_MAIL_SSL_PORT = 443;
ZaProxyConfig.DEFAULT_MAIL_SSL_PORT_ZCS = 7443;
ZaProxyConfig.DEFAULT_ADMIN_CONSOLE_PORT = 9071;
ZaProxyConfig.DEFAULT_ADMIN_CONSOLE_PORT_ZCS = 7071;

ZaProxyConfig.DEFAULT_IMAP_PORT = 143;
ZaProxyConfig.DEFAULT_IMAP_PORT_ZCS = 7143;
ZaProxyConfig.DEFAULT_IMAP_SSL_PORT = 993;
ZaProxyConfig.DEFAULT_IMAP_SSL_PORT_ZCS = 7993;

ZaProxyConfig.DEFAULT_POP3_PORT = 110;
ZaProxyConfig.DEFAULT_POP3_PORT_ZCS = 7110;
ZaProxyConfig.DEFAULT_POP3_SSL_PORT = 900;
ZaProxyConfig.DEFAULT_POP3_SSL_PORT_ZCS = 7900;

//append model definitions to ZaGlobalConfig.myXModel
if (ZaGlobalConfig && ZaGlobalConfig.myXModel) {
	ZaGlobalConfig.myXModel.items.push(
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, minInclusive: "1", maxInclusive: "16"},
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, minInclusive: "1"},
		{id: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress, type: _LIST_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress, listItem:{type: _HOSTNAME_OR_IP_, maxLength: 256} },
		{id: ZaProxyConfig.A_zimbraReverseProxyLogLevel, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyLogLevel, choices: ZaProxyConfig.PROXY_LOG_LEVEL_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyMailMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailMode, choices: ZaProxyConfig.MAIL_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraMailProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailProxyPort},
		{id: ZaProxyConfig.A_zimbraMailSSLProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLProxyPort},
		{id: ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyClientCertMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyClientCertMode, choices: ZaProxyConfig.CLIENT_CERT_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraAdminProxyPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraAdminProxyPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyMailEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval, type: _LIFETIME_NUMBER_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval, minInclusive: 0},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraImapProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3ProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3ProxyBindPort},
		{id: ZaProxyConfig.A_zimbraImapSSLProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapSSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort, type: _PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3SSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES}
	);
}

//append model definitions to ZaServer.myXModel
if (ZaServer && ZaServer.myXModel) {
	ZaServer.myXModel.items.push(
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, type: _COS_INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, minInclusive: "1", maxInclusive: "16"},
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, type: _COS_INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, minInclusive: "1"},
		{id: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyLogLevel, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyLogLevel, choices: ZaProxyConfig.PROXY_LOG_LEVEL_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyHttpEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyMailMode, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailMode, choices: ZaProxyConfig.MAIL_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraMailProxyPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailProxyPort},
		{id: ZaProxyConfig.A_zimbraMailSSLProxyPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLProxyPort},
		{id: ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyClientCertMode, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyClientCertMode, choices: ZaProxyConfig.CLIENT_CERT_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAdminEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraAdminProxyPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraAdminProxyPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyMailEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyMailEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraImapProxyBindPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3ProxyBindPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3ProxyBindPort},
		{id: ZaProxyConfig.A_zimbraImapSSLProxyBindPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraImapSSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort, type: _COS_PORT_, ref: "attrs/" + ZaProxyConfig.A_zimbraPop3SSLProxyBindPort},
		{id: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, type: _COS_ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode, choices: ZaProxyConfig.STARTTLS_MODE_CHOICES}
	);
}

/*---------------------Controller Section---------------------*/
ZaOperation.ENABLE_PROXY = ++ZA_OP_INDEX;

ZaProxyConfig.initPopupMenu = function () {
	
	if (!ZaProxyConfig.isProxyInstalledInAnyServer())
		return;
	
	// here "this" is controller
	this._popupOperations[ZaOperation.ENABLE_PROXY] = (new ZaOperation(ZaOperation.ENABLE_PROXY, "Enable Proxy", "Enable Proxy",
                                           "Deploy", "Deploy", new AjxListener(this, ZaProxyConfig._enableProxyBtnListener)));
	
	this._popupOperations[ZaOperation.DISABLE_PROXY] = (new ZaOperation(ZaOperation.ENABLE_PROXY, "Disable Proxy", "Disable Proxy",
            "Undeploy", "Undeploy", new AjxListener(this, ZaProxyConfig._disableProxyBtnListener)));
}

if(ZaController.initPopupMenuMethods["ZaServerController"]) {
	ZaController.initPopupMenuMethods["ZaServerController"].push(ZaProxyConfig.initPopupMenu);	
}

if(ZaController.initPopupMenuMethods["ZaServerListController"]) {
	ZaController.initPopupMenuMethods["ZaServerListController"].push(ZaProxyConfig.initPopupMenu);	
}

ZaProxyConfig._enableProxyBtnListener = function (ev) {
	try {
		var ep = new ZaEnableProxy();
		
		if(!this._enableProxyWiz) {
			this._enableProxyWiz = new ZaEnableProxyWizard(this._container, ep);
		}
		
		if(this._currentObject) {
			ep[ZaProxyConfig.A2_current_server] = this._currentObject["zimbraServiceHostname"]; 
		}
		
		this._enableProxyWiz.setObject(ep);
		this._enableProxyWiz.popup();
	} catch (ex) {
		this._handleException(ex, "ZaProxyConfig._enableProxyBtnListener", null, false);
	}
}

ZaProxyConfig._disableProxyBtnListener = function (ev) {
	try {
		var dp = new ZaDisableProxy();
		
		if(!this._disableProxyWiz) {
			this._disableProxyWiz = new ZaDisableProxyWizard(this._container, dp);
		}
		
		if (this._currentObject) {
			dp[ZaProxyConfig.A2_current_server] = this._currentObject["zimbraServiceHostname"];
		}
		
		this._disableProxyWiz.setObject(dp);
		this._disableProxyWiz.popup();
	} catch (ex) {
		this._handleException(ex, "ZaProxyConfig._disableProxyBtnListener", null, false);
	}
}

ZaProxyConfig.isProxyInstalledInAnyServer = function() {
	if (ZaProxyConfig._proxy_installed) {
		return ZaProxyConfig._proxy_installed;
	}
	var servers = ZaServer.getAll().getArray();
	for (var i = 0; i < servers.length; i++) {
		var s = servers[i];
		if (s.attrs[ZaServer.A_zimbraMailProxyServiceInstalled]) {
			ZaProxyConfig._proxy_installed = true;
			break;
		}
	}
	
	if (!ZaProxyConfig._proxy_installed) {
		ZaProxyConfig._proxy_installed = false;
	}
	// TODO: maybe should set a timer to clear this cached result
	
	return ZaProxyConfig._proxy_installed;
}

ZaProxyConfig.isProxyWizardEnabled = function(obj, attrsArray, rightsArray) {
	
	if(!obj)
		return true;
	
	if(AjxUtil.isEmpty(attrsArray) && AjxUtil.isEmpty(rightsArray))
		return true;
		
	if(!AjxUtil.isEmpty(attrsArray)) {
		var cntAttrs = attrsArray.length;
		for(var i=0; i< cntAttrs; i++) {
			if(ZaItem.hasWritePermission(attrsArray[i], obj)) {
				return true;
			}
		}
	} 
	
	if(!AjxUtil.isEmpty(rightsArray)) {
		var cntRights = rightsArray.length;
		for(var i=0; i< cntRights; i++) {
			if(ZaItem.hasRight(rightsArray[i], obj)) {
				return true;
			}
		}
	}
}

ZaProxyConfig.changeActionsStateMethod = function () {
    var obj = this._currentObject; // here "this" is ZaServerController

	var attrsArray = ZaProxyConfig.ENABLE_PROXY_ATTRS;
	var rightsArray = ZaProxyConfig.ENABLE_PROXY_RIGHTS;
	
	// check to enable "Enable Proxy Wizard" or not
	var isToEnable = ZaProxyConfig.isProxyWizardEnabled(obj, attrsArray, rightsArray);

	if(this._popupOperations[ZaOperation.ENABLE_PROXY]) {
        this._popupOperations[ZaOperation.ENABLE_PROXY].enabled = isToEnable;
    }
	
	// check to enable "Disable Proxy Wizard" or not
	isToEnable = ZaProxy.isProxyWizardEnabled(obj, attrsArray, rightsArray);
	
	if(this._popupOperations[ZaOperation.DISABLE_PROXY]) {
        this._popupOperations[ZaOperation.DISABLE_PROXY].enabled = isToEnable;
    }
}
ZaController.changeActionsStateMethods["ZaServerController"].push(ZaProxyConfig.changeActionsStateMethod);
	
/*------------------------View Section------------------------*/

/** enable/disable checks */
ZaProxyConfig.isWebProxyEnabled = function() {
	var webProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyHttpEnabled);
	return (webProxyEnabled == "TRUE");
}

ZaProxyConfig.isAdminProxyEnabled = function() {
	if (!ZaProxyConfig.isWebProxyEnabled.call(this)) { return false;}
	var adminProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyAdminEnabled);
	return (adminProxyEnabled == "TRUE");
}

ZaProxyConfig.isClientCertAuthEnabled = function() {
	if (!ZaProxyConfig.isWebProxyEnabled.call(this)) { return false;}
	var clientCertAuthEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyClientCertMode);
	return (clientCertAuthEnabled == "on" || clientCertAuthEnabled == "optional");
}

ZaProxyConfig.isMailProxyEnabled = function() {
	var mailProxyEnabled = this.getInstanceValue(ZaProxyConfig.A_zimbraReverseProxyMailEnabled);
	return (mailProxyEnabled == "TRUE");
}

/** attrs for delegate admin */
ZaProxyConfig.PROXY_CONFIG_GENERAL_ATTRS = [
	ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses,
	ZaProxyConfig.A_zimbraReverseProxyWorkerConnections,
	ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled,
	ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
	ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname
];

// zimbraReverseProxyAdminIPAddress is global only attributes
ZaProxyConfig.GLOBAL_PROXY_CONFIG_GENERAL_ATTRS =
	ZaProxyConfig.PROXY_CONFIG_GENERAL_ATTRS.push (
			ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress);

	
ZaProxyConfig.PROXY_CONFIG_WEB_PROXY_ATTRS = [
	ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
	ZaProxyConfig.A_zimbraReverseProxyMailMode,
	ZaProxyConfig.A_zimbraMailProxyPort,
	ZaProxyConfig.A_zimbraMailSSLProxyPort,
	//ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort,
	//ZaProxyConfig.A_zimbraReverseProxyClientCertMode, bug 71233
	ZaProxyConfig.A_zimbraReverseProxyAdminEnabled,
	ZaProxyConfig.A_zimbraAdminProxyPort
];

ZaProxyConfig.PROXY_CONFIG_MAIL_PROXY_ATTRS = [
	ZaProxyConfig.A_zimbraReverseProxyMailEnabled,
	ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled,
	ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled,
	ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled,
	ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled,
	ZaProxyConfig.A_zimbraImapProxyBindPort,
	ZaProxyConfig.A_zimbraPop3ProxyBindPort,
	ZaProxyConfig.A_zimbraImapSSLProxyBindPort,
	ZaProxyConfig.A_zimbraPop3SSLProxyBindPort,
	ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode,
	ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode
]

//ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval is global only attribute
ZaProxyConfig.GLOBAL_PROXY_CONFIG_MAIL_PROXY_ATTRS =
	ZaProxyConfig.PROXY_CONFIG_MAIL_PROXY_ATTRS.push(
			ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval);

ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_ATTRS = [].concat(
		ZaProxyConfig.GLOBAL_PROXY_CONFIG_GENERAL_ATTRS,
		ZaProxyConfig.PROXY_CONFIG_WEB_PROXY_ATTRS,
		ZaProxyConfig.GLOBAL_PROXY_CONFIG_MAIL_PROXY_ATTRS);

ZaProxyConfig.SERVER_PROXY_CONFIG_GLOBAL_TAB_ATTRS = [].concat(
		ZaProxyConfig.PROXY_CONFIG_GENERAL_ATTRS,
		ZaProxyConfig.PROXY_CONFIG_WEB_PROXY_ATTRS,
		ZaProxyConfig.PROXY_CONFIG_MAIL_PROXY_ATTRS);

ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_RIGHTS = [];
ZaProxyConfig.SERVER_PROXY_CONFIG_TAB_RIGHTS = [];

ZaProxyConfig.ENABLE_PROXY_ATTRS = [
    	ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
    	ZaProxyConfig.A_zimbraReverseProxyMailMode,
    	ZaProxyConfig.A_zimbraMailMode,
    	ZaProxyConfig.A_zimbraMailPort,
    	ZaProxyConfig.A_zimbraMailProxyPort,
    	ZaProxyConfig.A_zimbraMailSSLPort,
    	ZaProxyConfig.A_zimbraMailSSLProxyPort,
    	ZaProxyConfig.A_zimbraReverseProxyAdminEnabled,
    	ZaProxyConfig.A_zimbraAdminProxyPort,
    	ZaProxyConfig.A_zimbraReverseProxyMailEnabled,
    	ZaProxyConfig.A_zimbraImapProxyBindPort,
    	ZaProxyConfig.A_zimbraImapBindPort,
    	ZaProxyConfig.A_zimbraPop3ProxyBindPort,
    	ZaProxyConfig.A_zimbraPop3BindPort,
    	ZaProxyConfig.A_zimbraImapSSLProxyBindPort,
    	ZaProxyConfig.A_zimbraImapSSLBindPort,
    	ZaProxyConfig.A_zimbraPop3SSLProxyBindPort,
    	ZaProxyConfig.A_zimbraPop3SSLBindPort,
    	ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode,
    	ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode,
        ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses,
        ZaProxyConfig.A_zimbraReverseProxyWorkerConnections,
        ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled,
        ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
        ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname,
        ZaProxyConfig.A_zimbraReverseProxyLookupTarget,
        ZaProxyConfig.A_zimbraReverseProxyUpstreamServers,
        ZaProxyConfig.A_zimbraReverseProxyAvailableLookupTargets
];

ZaProxyConfig.ENABLE_PROXY_RIGHTS = [
        "listServer"
];

/** global level proxy config modifer */
ZaProxyConfig.myGlobalXFormModifier = function(xFormObject, entry) {
	
	if (!ZaProxyConfig.isProxyInstalledInAnyServer()) // don't show anything about proxy if proxy is not installed at all
		return;

	if (ZaTabView.isTAB_ENABLED(entry, ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_ATTRS,
									   ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_RIGHTS)) {
		var proxyConfigCaseKey = ++this.TAB_INDEX;
		var proxyConfigCase = {
			type: _ZATABCASE_, caseKey: proxyConfigCaseKey,
			colSizes: ["auto"], numCols: 1, paddingStyle: "padding-left:15px;", width: "98%",
			items: [
				{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "auto"],
				 label: com_zimbra_proxy_config.LBL_ProxyWebProxyConfig,
				 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
				                    ZaProxyConfig.PROXY_CONFIG_WEB_PROXY_ATTRS]],
				 items: [
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyEnableWebProxy,
					 ref: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyWebProxyMode,
					 ref: ZaProxyConfig.A_zimbraReverseProxyMailMode,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyUseSSLToUpstream,
					 ref: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpProxyPort,
					 ref: ZaProxyConfig.A_zimbraMailProxyPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyHttpSSLProxyPort,
					 ref: ZaProxyConfig.A_zimbraMailSSLProxyPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
					},
					{type: _SPACER_, height: 10},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyAdminEnabled,
					 ref: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled,
					 trueValue: "TRUE", falseValue: "FALSE",
					 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyAdminProxyPort,
					 ref: ZaProxyConfig.A_zimbraAdminProxyPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isAdminProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
					                                   ZaProxyConfig.A_zimbraReverseProxyAdminEnabled]
					}/*,
					  ---bug 71233, temporarily remove 2-way SSL features from UI---
					 {type: _SPACER_, height: 10},
					{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyClientCertAuthMode,
					 ref: ZaProxyConfig.A_zimbraReverseProxyClientCertMode,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyClientCertAuthPort,
					 ref: ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isClientCertAuthEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
					                                   ZaProxyConfig.A_zimbraReverseProxyClientCertMode]
					} */
				 ]
				},
				{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "auto"],
				 label: com_zimbra_proxy_config.LBL_ProxyMailProxyConfig,
				 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
				                    ZaProxyConfig.GLOBAL_PROXY_CONFIG_MAIL_PROXY_ATTRS]],
				 items: [
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyEnableMailProxy,
					 ref: ZaProxyConfig.A_zimbraReverseProxyMailEnabled,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyAuthWaitTime,
					 ref: ZaProxyConfig.A_zimbraReverseProxyAuthWaitInterval,
					 width: "60px",
					 getDisplayValue: function(value) {return parseInt(value);}, // only assume the number is in seconds
					 elementChanged: function(elementValue, instanceValue, event) {
						 instanceValue = elementValue + "s";
						 this.getForm().itemChanged(this, instanceValue, event);
					 },
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _SPACER_, height: 10},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapProxyPort,
					 ref: ZaProxyConfig.A_zimbraImapProxyBindPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyImapSSLProxyPort,
					 ref: ZaProxyConfig.A_zimbraImapSSLProxyBindPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyImapEnablePlainAuth,
					 ref: ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled,
					 trueValue: "TRUE", falseValue: "FALSE",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyImapEnableGssapiAuth,
					 ref: ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled,
					 trueValue: "TRUE", falseValue: "FALSE",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyImapStartTlsMode,
					 ref: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode,
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _SPACER_, height: 10},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3ProxyPort,
					 ref: ZaProxyConfig.A_zimbraPop3ProxyBindPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyPop3SSLProxyPort,
					 ref: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort,
					 width: "60px",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyPop3EnablePlainAuth,
					 ref: ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled,
					 trueValue: "TRUE", falseValue: "FALSE",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyPop3EnableGssapiAuth,
					 ref: ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled,
					 trueValue: "TRUE", falseValue: "FALSE",
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					},
					{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyPop3StartTlsMode,
					 ref: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode,
					 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
					 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
					}
				 ]
				}, // _ZA_TOP_GROUPER_
				{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "auto"],
				 label: com_zimbra_proxy_config.LBL_ProxyGeneralConfig,
				 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
				                    ZaProxyConfig.GLOBAL_PROXY_CONFIG_GENERAL_ATTRS]],
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
					 ref: ZaProxyConfig.A_zimbraReverseProxyLogLevel
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyAllowServerResolveRoute,
					 ref: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _DWT_ALERT_, style: DwtAlert.INFO, iconVisible: true, colSpan: "*",
					 content: com_zimbra_proxy_config.MSG_GenConfigPerVirtualHostname,
					 // this is to make sure the alert will always apply the below item's visibility
					 visibilityChecks: [[ZaItem.hasReadPermission,
					                     ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname]]
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyGenConfigPerVirtualHostname,
					 ref: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _SPACER_, height: 10},
					{type: _DWT_ALERT_, style: DwtAlert.INFO, iconVisible: true, colSpan: "*",
					 content: com_zimbra_proxy_config.MSG_ReverseProxyAdminIPAddress,
					 // this is to make sure the alert will always apply the below item's visibility
					 visibilityChecks: [[ZaItem.hasReadPermission,
					                     ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress]]
					},
					{type: _REPEAT_, label: com_zimbra_proxy_config.LBL_ProxyAdminIPAddresses,
					 ref: ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress,
					 repeatInstance:"", labelWrap: true ,
					 showAddButton: true, showRemoveButton: true, showAddOnNextRow: true,
					 items: [
					 	{ref: ".", type: _TEXTFIELD_, label: null}
					 ]
					}
				 ]
				} // _ZA_TOP_GROUPER_
			]
		};

		//items[1] of global config XForm object is a _TAB_BAR_, see GlobalConfigXFormView.js
		var tabBarChoices = xFormObject.items[1].choices;
		tabBarChoices.push({value: proxyConfigCaseKey, label: com_zimbra_proxy_config.OVT_Proxy});

		// items[2] of global config XForm is a _SWITCH_, see GlobalConfigXFormView.js
		var switchItems = xFormObject.items[2].items;
		switchItems.push(proxyConfigCase);
	}
}

ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaProxyConfig.myGlobalXFormModifier);


/** server level visibility check functions */
ZaProxyConfig.isProxyInstalled = function() {
	return XForm.checkInstanceValue.call(this, ZaServer.A_zimbraMailProxyServiceInstalled, true);
}

ZaProxyConfig.isProxyEnabled = function() {
	return XForm.checkInstanceValue.call(this, ZaServer.A_zimbraMailProxyServiceEnabled, true);
}

ZaProxyConfig.isProxyInstalledAndEnabled = function() {
	return (ZaProxyConfig.isProxyInstalled.call(this) && ZaProxyConfig.isProxyEnabled.call(this));
}

ZaProxyConfig.isProxyNotInstalledOrEnabled = function() {
	return !ZaProxyConfig.isProxyInstalledAndEnabled.call(this);
}

/** server level proxy config modifer */
ZaProxyConfig.myServerXFormModifier = function(xFormObject, entry) {
	
	if (!ZaProxyConfig.isProxyInstalledInAnyServer()) // don't show anything about proxy if proxy is not installed at all
		return;

	if (ZaTabView.isTAB_ENABLED(entry, ZaProxyConfig.SERVER_PROXY_CONFIG_TAB_ATTRS,
									   ZaProxyConfig.SERVER_PROXY_CONFIG_TAB_RIGHTS)) {
		var proxyConfigCaseKey = ++this.TAB_INDEX;
		var proxyConfigCase = {
			type: _ZATABCASE_, caseKey: proxyConfigCaseKey,
			colSizes: ["auto"], numCols: 1, paddingStyle: "padding-left:15px;", width: "98%",
			items: [
			    {type: _GROUP_, numCols: 1,
			     visibilityChecks: [ZaProxyConfig.isProxyInstalledAndEnabled],
			     visibilityChangeEventSources: [ZaServer.A_zimbraMailProxyServiceInstalled,
			                                    ZaServer.A_zimbraMailProxyServiceEnabled],
			     items: [
					{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "*"],
					 label: com_zimbra_proxy_config.LBL_ProxyWebProxyConfig,
					 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					                    ZaProxyConfig.PROXY_CONFIG_WEB_PROXY_ATTRS]],
					 items: [
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyEnableWebProxy,
						 ref: ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
	
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyWebProxyMode,
						 ref: ZaProxyConfig.A_zimbraReverseProxyMailMode,
						 colSpan: "2", // the colSpan here and below are to fix the problem brought by 2 kinds of super control implementation.
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyUseSSLToUpstream,
						 ref: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, colSpan: "3",
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyHttpProxyPort,
						 ref: ZaProxyConfig.A_zimbraMailProxyPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyHttpSSLProxyPort,
						 ref: ZaProxyConfig.A_zimbraMailSSLProxyPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
						},
						{type: _SPACER_, height: 10, colSpan: "3"},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyAdminEnabled,
						 ref: ZaProxyConfig.A_zimbraReverseProxyAdminEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyAdminProxyPort,
						 ref: ZaProxyConfig.A_zimbraAdminProxyPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isAdminProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
						                                   ZaProxyConfig.A_zimbraReverseProxyAdminEnabled]
						}/*,
						  ---bug 71233, temporarily remove 2-way SSL features from UI---
						 {type: _SPACER_, height: 10},
						{type: _SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyClientCertAuthMode,
						 ref: ZaProxyConfig.A_zimbraReverseProxyClientCertMode,
						 width: "60px",
						 enableDisableChecks: [ZaProxyConfig.isWebProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled]
						},
						{type: _TEXTFIELD_, label: com_zimbra_proxy_config.LBL_ProxyClientCertAuthPort,
						 ref: ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort,
						 width: "60px",
						 enableDisableChecks: [ZaProxyConfig.isClientCertAuthEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyHttpEnabled,
						                                   ZaProxyConfig.A_zimbraReverseProxyClientCertMode]
						} */
					 ]
					},
					{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "*"],
					 label: com_zimbra_proxy_config.LBL_ProxyMailProxyConfig,
					 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					                    ZaProxyConfig.PROXY_CONFIG_MAIL_PROXY_ATTRS]],
					 items: [
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyEnableMailProxy,
						 ref: ZaProxyConfig.A_zimbraReverseProxyMailEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 colSpan: "3",  // the colSpan here and below are to fix the problem brought by 2 kinds of super control implementation.
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SPACER_, height: 10, colSpan: 3},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyImapProxyPort,
						 ref: ZaProxyConfig.A_zimbraImapProxyBindPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyImapSSLProxyPort,
						 ref: ZaProxyConfig.A_zimbraImapSSLProxyBindPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyImapEnablePlainAuth,
						 ref: ZaProxyConfig.A_zimbraReverseProxyImapSaslPlainEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 colSpan: "3",
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyImapEnableGssapiAuth,
						 ref: ZaProxyConfig.A_zimbraReverseProxyImapSaslGssapiEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyImapStartTlsMode,
						 ref: ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode,
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "2",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SPACER_, height: 10, colSpan: "3"},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyPop3ProxyPort,
						 ref: ZaProxyConfig.A_zimbraPop3ProxyBindPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyPop3SSLProxyPort,
						 ref: ZaProxyConfig.A_zimbraPop3SSLProxyBindPort,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyPop3EnablePlainAuth,
						 ref: ZaProxyConfig.A_zimbraReverseProxyPop3SaslPlainEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", colSpan: "3",
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyPop3EnableGssapiAuth,
						 ref: ZaProxyConfig.A_zimbraReverseProxyPop3SaslGssapiEnabled,
						 trueValue: "TRUE", falseValue: "FALSE", colSpan: "3",
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						},
						{type: _SUPER_SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyPop3StartTlsMode,
						 ref: ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode,
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "2",
						 onChange: ZaServerXFormView.onFormFieldChanged,
						 enableDisableChecks: [ZaProxyConfig.isMailProxyEnabled],
						 enableDisableChangeEventSources: [ZaProxyConfig.A_zimbraReverseProxyMailEnabled]
						}
					 ]
					},
					{type: _ZA_TOP_GROUPER_, numCols:2, colSizes: ["275px", "*"],
					 label: com_zimbra_proxy_config.LBL_ProxyGeneralConfig,
					 visibilityChecks:[[ZATopGrouper_XFormItem.isGroupVisible,
					                    ZaProxyConfig.PROXY_CONFIG_GENERAL_ATTRS]],
					 items: [
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyWorkerProcessNum,
						 ref: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_TEXTFIELD_, txtBoxLabel: com_zimbra_proxy_config.LBL_ProxyWorkerConnectionNum,
						 ref: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections,
						 textFieldWidth: "60px", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal, colSpan: "3",
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_SELECT1_, label: com_zimbra_proxy_config.LBL_ProxyLogLevel,
						 ref: ZaProxyConfig.A_zimbraReverseProxyLogLevel,
						 resetToSuperLabel: ZaMsg.NAD_ResetToGlobal, colSpan: "2",
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyAllowServerResolveRoute,
						 ref: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
						 trueValue: "TRUE", falseValue: "FALSE",
						 resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
					     onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _DWT_ALERT_, style: DwtAlert.INFO, iconVisible: true, colSpan: "3",
						 content: com_zimbra_proxy_config.MSG_GenConfigPerVirtualHostname,
						 // this is to make sure the alert will always apply the below item's visibility
						 visibilityChecks: [[ZaItem.hasReadPermission,
						                     ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname]]
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyGenConfigPerVirtualHostname,
						 ref: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, colSpan: "3",
						trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged
						}
					 ]
					}// _ZA_TOP_GROUPER_
				 ]
				}, // _GROUP_
				{type: _GROUP_, numCols: 1, colSize: ["*"], colSpan: "*",
				 items: [
				    {type: _DWT_ALERT_, style: DwtAlert.WARNING, iconVisible: true, colSpan: "*",
				     content: com_zimbra_proxy_config.MSG_NeedProxyInstalledAndEnabled}
				 ],
				 visibilityChecks: [ZaProxyConfig.isProxyNotInstalledOrEnabled],
				 visibilityChangeEventSources: [ZaServer.A_zimbraMailProxyServiceInstalled,
				                                ZaServer.A_zimbraMailProxyServiceEnabled]
				}
			]
		}; // _ZATABCASE_

		// items[1] of server XForm object is a _TAB_BAR_, see ZaServerConfigXFormView.js
		var tabBarChoices = xFormObject.items[1].choices;
		tabBarChoices.push({value: proxyConfigCaseKey, label: com_zimbra_proxy_config.OVT_Proxy});
		
		// items[2] of server XForm is a _SWITCH_, see ZaServerFormView.js
		var switchItems = xFormObject.items[2].items;
		switchItems.push(proxyConfigCase);
	}
}

ZaTabView.XFormModifiers["ZaServerXFormView"].push(ZaProxyConfig.myServerXFormModifier);

} // if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_proxy_config"])