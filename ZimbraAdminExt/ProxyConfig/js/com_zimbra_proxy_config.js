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
ZaProxyConfig.STARTTLS_MODE_CHOICES = ["off", "on", "only"];

// Proxy Basic Configurations
ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses = "zimbraReverseProxyWorkerProcesses";
ZaProxyConfig.A_zimbraReverseProxyWorkerConnections = "zimbraReverseProxyWorkerConnections";
ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled = "zimbraReverseProxySSLToUpstreamEnabled";
ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled = "zimbraReverseProxyDnsLookupInServerEnabled";
ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname = "zimbraReverseProxyGenConfigPerVirtualHostname";
ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress = "zimbraReverseProxyAdminIPAddress";

// Web Proxy Configurations
ZaProxyConfig.A_zimbraReverseProxyHttpEnabled = "zimbraReverseProxyHttpEnabled";
ZaProxyConfig.A_zimbraReverseProxyMailMode = "zimbraReverseProxyMailMode";
ZaProxyConfig.A_zimbraMailProxyPort = "zimbraMailProxyPort";
ZaProxyConfig.A_zimbraMailSSLProxyPort = "zimbraMailSSLProxyPort";
ZaProxyConfig.A_zimbraMailSSLProxyClientCertPort = "zimbraMailSSLProxyClientCertPort";
ZaProxyConfig.A_zimbraReverseProxyClientCertMode = "zimbraReverseProxyClientCertMode";
ZaProxyConfig.A_zimbraReverseProxyAdminEnabled = "zimbraReverseProxyAdminEnabled";
ZaProxyConfig.A_zimbraAdminProxyPort = "zimbraAdminProxyPort";

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
ZaProxyConfig.A_zimbraReverseProxyImapStartTlsMode = "zimbraReverseProxyImapStartTlsMode";
ZaProxyConfig.A_zimbraReverseProxyPop3StartTlsMode = "zimbraReverseProxyPop3StartTlsMode";

//append model definitions to ZaGlobalConfig.myXModel
if (ZaGlobalConfig && ZaGlobalConfig.myXModel) {
	ZaGlobalConfig.myXModel.items.push(
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerProcesses, minInclusive: "1", maxInclusive: "16"},
		{id: ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, type: _INT_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyWorkerConnections, minInclusive: "1"},
		{id: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyGenConfigPerVirtualHostname, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, type: _ENUM_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled, choices: ZaModel.BOOLEAN_CHOICES},
		{id: ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress, type: _LIST_, ref: "attrs/" + ZaProxyConfig.A_zimbraReverseProxyAdminIPAddress, listItem:{type: _HOSTNAME_OR_IP_, maxLength: 256} },
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

/** global level proxy config modifer */
ZaProxyConfig.myGlobalXFormModifier = function(xFormObject, entry) {

	if (ZaTabView.isTAB_ENABLED(entry, ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_ATTRS,
									   ZaProxyConfig.GLOBAL_PROXY_CONFIG_TAB_RIGHTS)) {
		var proxyConfigCaseKey = ++this.TAB_INDEX;
		var proxyConfigCase = {
			type: _ZATABCASE_, caseKey: proxyConfigCaseKey,
			colSizes: ["auto"], numCols: 1, paddingStyle: "padding-left:15px;", width: "98%",
			items: [
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
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyUseSSLToUpstream,
					 ref: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled,
					 trueValue: "TRUE", falseValue: "FALSE"
					},
					{type: _CHECKBOX_, label: com_zimbra_proxy_config.LBL_ProxyAllowServerSendUpstreamHostName,
					 ref: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
					 trueValue: "FALSE", falseValue: "TRUE" // the true and false value are meant to be reversed
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
				},
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
				}
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
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyUseSSLToUpstream,
						 ref: ZaProxyConfig.A_zimbraReverseProxySSLToUpstreamEnabled, colSpan: "3",
						 trueValue: "TRUE", falseValue: "FALSE", resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
						 onChange: ZaServerXFormView.onFormFieldChanged
						},
						{type: _SUPER_CHECKBOX_, checkBoxLabel: com_zimbra_proxy_config.LBL_ProxyAllowServerSendUpstreamHostName,
						 ref: ZaProxyConfig.A_zimbraReverseProxyDnsLookupInServerEnabled,
						 trueValue: "FALSE", falseValue: "TRUE", // the true and false value are meant to be reversed
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
					},
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
					} // _ZA_TOP_GROUPER_
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