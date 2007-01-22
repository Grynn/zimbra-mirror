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
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaServer
* This class represents zimbraServer objects. ZaServer extends ZaItem
* @author Greg Solovyev
* @contructor ZaServer
* @param app reference to the application instance
**/
function ZaServer(app) {
	ZaItem.call(this, app,"ZaServer");
	this._init(app);
}

ZaServer.prototype = new ZaItem;
ZaServer.prototype.constructor = ZaServer;
ZaItem.loadMethods["ZaServer"] = new Array();
ZaItem.initMethods["ZaServer"] = new Array();

//attribute name constants, this values are taken from zimbra.schema
ZaServer.A_name = "cn";
ZaServer.A_description = "description";
ZaServer.A_notes = "zimbraNotes";
ZaServer.A_Service = "zimbraService";
ZaServer.A_ServiceHostname = "zimbraServiceHostname";
ZaServer.A_zimbraMailPort = "zimbraMailPort";
ZaServer.A_zimbraMailSSLPort = "zimbraMailSSLPort";
ZaServer.A_zimbraMailMode = "zimbraMailMode";
// services
ZaServer.A_zimbraServiceInstalled = "zimbraServiceInstalled";
ZaServer.A_zimbraLdapServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_ldap";
ZaServer.A_zimbraMailboxServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_mailbox";
ZaServer.A_zimbraMtaServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_mta";
ZaServer.A_zimbraSnmpServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_snmp";
ZaServer.A_zimbraAntiVirusServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_antivirus";
ZaServer.A_zimbraAntiSpamServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_antispam";
ZaServer.A_zimbraSpellServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_spell";
ZaServer.A_zimbraLoggerServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_logger";
ZaServer.A_zimbraMailProxyServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_imapproxy";

ZaServer.A_zimbraServiceEnabled = "zimbraServiceEnabled";
ZaServer.A_zimbraLdapServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_ldap";
ZaServer.A_zimbraMailboxServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_mailbox";
ZaServer.A_zimbraMtaServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_mta";
ZaServer.A_zimbraSnmpServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_snmp";
ZaServer.A_zimbraAntiVirusServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_antivirus";
ZaServer.A_zimbraAntiSpamServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_antispam";
ZaServer.A_zimbraSpellServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_spell";
ZaServer.A_zimbraLoggerServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_logger";
ZaServer.A_zimbraMailProxyServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_imapproxy";
// MTA
ZaServer.A_zimbraMtaAuthEnabled = "zimbraMtaAuthEnabled";
ZaServer.A_zimbraMtaDnsLookupsEnabled = "zimbraMtaDnsLookupsEnabled";
ZaServer.A_zimbraMtaRelayHost = "zimbraMtaRelayHost";
ZaServer.A_zimbraMtaTlsAuthOnly = "zimbraMtaTlsAuthOnly";
ZaServer.A_zimbraMtaMyNetworks = "zimbraMtaMyNetworks";
//smtp
ZaServer.A_SmtpHostname  = "zimbraSmtpHostname";
ZaServer.A_SmtpPort = "zimbraSmtpPort";
ZaServer.A_SmtpTimeout = "zimbraSmtpTimeout";
//Lmtp
ZaServer.A_LmtpAdvertisedName = "zimbraLmtpAdvertisedName";
ZaServer.A_LmtpBindAddress = "zimbraLmtpBindAddress";
ZaServer.A_LmtpBindPort = "zimbraLmtpBindPort";
ZaServer.A_LmtpNumThreads = "zimbraLmtpNumThreads";
//pop3
ZaServer.A_zimbraPop3NumThreads = "zimbraPop3NumThreads";
ZaServer.A_Pop3AdvertisedName ="zimbraPop3AdvertisedName";
ZaServer.A_Pop3BindAddress = "zimbraPop3BindAddress";
ZaServer.A_zimbraPop3BindPort = "zimbraPop3BindPort";
ZaServer.A_zimbraPop3SSLBindPort = "zimbraPop3SSLBindPort";
ZaServer.A_Pop3SSLServerEnabled = "zimbraPop3SSLServerEnabled";
ZaServer.A_Pop3ServerEnabled = "zimbraPop3ServerEnabled"
ZaServer.A_Pop3CleartextLoginEnabled = "zimbraPop3CleartextLoginEnabled";
//imap
ZaServer.A_zimbraImapNumThreads="zimbraImapNumThreads";
ZaServer.A_zimbraImapBindPort="zimbraImapBindPort";
ZaServer.A_ImapServerEnabled="zimbraImapServerEnabled";
ZaServer.A_ImapSSLBindPort="zimbraImapSSLBindPort";
ZaServer.A_ImapSSLServerEnabled="zimbraImapSSLServerEnabled";
ZaServer.A_ImapCleartextLoginEnabled="zimbraImapCleartextLoginEnabled";

//redo log
ZaServer.A_RedologEnabled = "zimbraRedologEnabled";
ZaServer.A_RedologLogPath = "zimbraRedologLogPath";
ZaServer.A_RedologArchiveDir = "zimbraRedologArchiveDir";
ZaServer.A_RedologBacklogDir = "zimbraRedologBacklogDir";
ZaServer.A_RedologRolloverFileSizeKB = "zimbraRedologRolloverFileSizeKB";
ZaServer.A_RedologFsyncIntervalMS = "zimbraRedologFsyncIntervalMS";
//master role settings
ZaServer.A_MasterRedologClientConnections = "zimbraMasterRedologClientConnections";
ZaServer.A_MasterRedologClientTimeoutSec = "zimbraMasterRedologClientTimeoutSec";
ZaServer.A_MasterRedologClientTcpNoDelay = "zimbraMasterRedologClientTcpNoDelay";
//slave role settings
ZaServer.A_zimbraUserServicesEnabled = "zimbraUserServicesEnabled";

//Volume Management
ZaServer.A_RemovedVolumes = "removed_volumes";
ZaServer.A_Volumes = "volumes";
ZaServer.A_VolumeId = "id";
ZaServer.A_VolumeName = "name";
ZaServer.A_VolumeRootPath = "rootpath";
ZaServer.A_VolumeCompressBlobs = "compressBlobs";
ZaServer.A_VolumeCompressionThreshold = "compressionThreshold";
ZaServer.A_VolumeType = "type";
ZaServer.A_CurrentPrimaryMsgVolumeId = "current_pri_msg_volume_id";
ZaServer.A_CurrentSecondaryMsgVolumeId = "current_sec_msg_volume_id";
ZaServer.A_CurrentIndexMsgVolumeId = "current_index_volume_id";

//mail proxy
ZaServer.A_zimbraImapProxyBindPort="zimbraImapProxyBindPort";
ZaServer.A_zimbraImapSSLProxyBindPort="zimbraImapSSLProxyBindPort";
ZaServer.A_zimbraPop3ProxyBindPort="zimbraPop3ProxyBindPort";
ZaServer.A_zimbraPop3SSLProxyBindPort="zimbraPop3SSLProxyBindPort";

// other
ZaServer.A_zimbraIsMonitorHost = "zimbraIsMonitorHost";
ZaServer.A_showVolumes = "show_volumes"; //this attribute is immutable
ZaServer.A_zimbraLogHostname = "zimbraLogHostname";
ZaServer.A_isCurrentVolume = "isCurrentVolume";
ZaServer.STANDALONE = "standalone";
ZaServer.MASTER = "master";
ZaServer.SLAVE = "slave";

ZaServer.PRI_MSG = 1;
ZaServer.SEC_MSG = 2;
ZaServer.INDEX = 10;
ZaServer.volumeTypeChoices = new XFormChoices({1:ZaMsg.NAD_VOLUME_Msg, 10:ZaMsg.NAD_VOLUME_Index}, XFormChoices.HASH);

		
ZaServer.myXModel = {
	items: [
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaServer.A_name, ref:"attrs/" + ZaServer.A_name, type:_STRING_},
		{id:ZaServer.A_description, ref:"attrs/" +  ZaServer.A_description, type:_STRING_},
		{id:ZaServer.A_notes, ref:"attrs/" +  ZaServer.A_notes, type:_STRING_},		
		{id:ZaServer.A_Service, ref:"attrs/" +  ZaServer.A_Service, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_ServiceHostname, ref:"attrs/" +  ZaServer.A_ServiceHostname, type:_HOSTNAME_OR_IP_, maxLength: 256 },
		// Services
		{id:ZaServer.A_zimbraLdapServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraLdapServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailboxServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraMailboxServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMtaServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraMtaServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSnmpServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraSnmpServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiVirusServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraAntiVirusServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiSpamServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraAntiSpamServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSpellServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraSpellServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraLoggerServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraLoggerServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailProxyServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraMailProxyServiceEnabled, type: _ENUM_, choices: [false,true] },		
		
		{id:ZaServer.A_zimbraLdapServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraLdapServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailboxServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMailboxServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMtaServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMtaServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSnmpServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraSnmpServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiVirusServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraAntiVirusServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiSpamServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraAntiSpamServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSpellServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraSpellServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraLoggerServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraLoggerServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailProxyServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMailProxyServiceInstalled, type: _ENUM_, choices: [false,true] },				
		// MTA
		{id:ZaServer.A_zimbraMtaAuthEnabled, ref:"attrs/" +  ZaServer.A_zimbraMtaAuthEnabled, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		{id:ZaServer.A_zimbraMtaTlsAuthOnly, ref:"attrs/" +  ZaServer.A_zimbraMtaTlsAuthOnly, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		{id:ZaServer.A_zimbraMtaRelayHost, ref:"attrs/" +  ZaServer.A_zimbraMtaRelayHost, type: _COS_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_zimbraMtaMyNetworks, ref:"attrs/" +  ZaServer.A_zimbraMtaMyNetworks, type:_COS_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_zimbraMtaDnsLookupsEnabled, ref:"attrs/" +  ZaServer.A_zimbraMtaDnsLookupsEnabled, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		// ...other...
		{id:ZaServer.A_SmtpHostname, ref:"attrs/" +  ZaServer.A_SmtpHostname, type:_COS_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_SmtpPort, ref:"attrs/" +  ZaServer.A_SmtpPort, type:_COS_PORT_},
		{id:ZaServer.A_SmtpTimeout, ref:"attrs/" + ZaServer.A_SmtpTimeout, type:_COS_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_LmtpAdvertisedName, ref:"attrs/" +  ZaServer.A_LmtpAdvertisedName, type:_STRING_, maxLength: 128 },
		{id:ZaServer.A_LmtpBindAddress, ref:"attrs/" +  ZaServer.A_LmtpBindAddress, type:_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_LmtpBindPort, ref:"attrs/" +  ZaServer.A_LmtpBindPort, type:_COS_PORT_},		
		{id:ZaServer.A_LmtpNumThreads, ref:"attrs/" +  ZaServer.A_LmtpNumThreads, type:_COS_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_zimbraPop3NumThreads, ref:"attrs/" +  ZaServer.A_zimbraPop3NumThreads, type:_COS_NUMBER_, minInclusive: 0 },		
		{id:ZaServer.A_zimbraImapNumThreads, ref:"attrs/" +  ZaServer.A_zimbraImapNumThreads, type:_COS_NUMBER_, minInclusive: 0 },		
		{id:ZaServer.A_Pop3AdvertisedName, ref:"attrs/" +  ZaServer.A_Pop3AdvertisedName, type:_STRING_, maxLength: 128 },
		{id:ZaServer.A_Pop3BindAddress, ref:"attrs/" +  ZaServer.A_Pop3BindAddress, type:_HOSTNAME_OR_IP_, maxLength: 128 },
		{id:ZaServer.A_Pop3AdvertisedName, ref:"attrs/" +  ZaServer.A_Pop3AdvertisedName, type:_STRING_, maxLength: 128 },
		{id:ZaServer.A_Pop3BindAddress, ref:"attrs/" +  ZaServer.A_Pop3BindAddress, type:_HOSTNAME_OR_IP_, maxLength: 128 },
		{id:ZaServer.A_zimbraPop3BindPort, ref:"attrs/" +  ZaServer.A_zimbraPop3BindPort, type:_COS_PORT_ },
		{id:ZaServer.A_zimbraPop3SSLBindPort, ref:"attrs/" +  ZaServer.A_zimbraPop3SSLBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_Pop3SSLServerEnabled, ref:"attrs/" + ZaServer.A_Pop3SSLServerEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_Pop3ServerEnabled, ref:"attrs/" + ZaServer.A_Pop3ServerEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_Pop3CleartextLoginEnabled, ref:"attrs/" + ZaServer.A_Pop3CleartextLoginEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_zimbraImapBindPort, ref:"attrs/" + ZaServer.A_zimbraImapBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_ImapServerEnabled, ref:"attrs/" + ZaServer.A_ImapServerEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_ImapSSLBindPort, ref:"attrs/" + ZaServer.A_ImapSSLBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_ImapSSLServerEnabled, ref:"attrs/" + ZaServer.A_ImapSSLServerEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		//mail proxy
		{id:ZaServer.A_zimbraImapProxyBindPort, ref:"attrs/" +  ZaServer.A_zimbraImapProxyBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_zimbraImapSSLProxyBindPort, ref:"attrs/" +  ZaServer.A_zimbraImapSSLProxyBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_zimbraPop3ProxyBindPort, ref:"attrs/" +  ZaServer.A_zimbraPop3ProxyBindPort, type:_COS_PORT_ },
		{id:ZaServer.A_zimbraPop3SSLProxyBindPort, ref:"attrs/" +  ZaServer.A_zimbraPop3SSLProxyBindPort, type:_COS_PORT_ },
		
		{id:ZaServer.A_ImapCleartextLoginEnabled, ref:"attrs/" + ZaServer.A_ImapCleartextLoginEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_RedologEnabled, ref:"attrs/" + ZaServer.A_RedologEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_RedologLogPath, ref:"attrs/" + ZaServer.A_RedologLogPath, type:_STRING_},		
		{id:ZaServer.A_RedologArchiveDir, ref:"attrs/" + ZaServer.A_RedologArchiveDir, type:_STRING_},		
		{id:ZaServer.A_RedologBacklogDir, ref:"attrs/" + ZaServer.A_RedologBacklogDir, type:_STRING_},		
		{id:ZaServer.A_RedologRolloverFileSizeKB, ref:"attrs/" + ZaServer.A_RedologRolloverFileSizeKB, type:_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_RedologFsyncIntervalMS, ref:"attrs/" + ZaServer.A_RedologFsyncIntervalMS, type:_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_MasterRedologClientConnections, ref:"attrs/" + ZaServer.A_MasterRedologClientConnections, type:_STRING_},		
		{id:ZaServer.A_MasterRedologClientTimeoutSec, ref:"attrs/" + ZaServer.A_MasterRedologClientTimeoutSec, type:_STRING_},		
		{id:ZaServer.A_MasterRedologClientTcpNoDelay, ref:"attrs/" + ZaServer.A_MasterRedologClientTcpNoDelay, type:_STRING_},		
		{id:ZaServer.A_zimbraUserServicesEnabled, ref:"attrs/" + ZaServer.A_zimbraUserServicesEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		{id:ZaServer.A_Volumes, type:_LIST_, listItem:
			{type:_OBJECT_,
				items: [
					{id:ZaServer.A_isCurrentVolume, type: _ENUM_, choices: [false,true]	},
					{id:ZaServer.A_VolumeId, type:_NUMBER_},
					{id:ZaServer.A_VolumeName, type:_STRING_},
					{id:ZaServer.A_VolumeType, type:_ENUM_, choices:[ZaServer.PRI_MSG,ZaServer.SEC_MSG,ZaServer.INDEX],defaultValue:ZaServer.PRI_MSG},
					{id:ZaServer.A_VolumeRootPath, type:_STRING_},
					{id:ZaServer.A_VolumeCompressBlobs, type:_ENUM_, choices:[0,1], defaultValue:1},
					{id:ZaServer.A_VolumeCompressionThreshold, type:_NUMBER_,defaultValue:4096}				
				]
			}
		},
		{id:ZaServer.A_showVolumes, ref:ZaServer.A_showVolumes, type: _ENUM_, choices: [false,true]}
	]	
};
		
ZaServer.prototype.toString = function() {
	return this.name;
}

ZaServer.getAll =
function(app) {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", "urn:zimbraAdmin", null);	
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;	
//	DBG.println(AjxDebug.DBG1, "Sending GetAllServersRequest for Server");
	var resp = command.invoke(params).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaServer, app);
	list.loadFromJS(resp);	
	return list;
}

/**
* @param mods - map of modified attributes
* modifies object's information in the database
**/
ZaServer.prototype.modify =
function(mods) {
	var soapDoc = AjxSoapDoc.create("ModifyServerRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	for (var aname in mods) {
		if (mods[aname] instanceof Array) {
			var array = mods[aname];
			if (array.length > 0) {
				for (var i = 0; i < array.length; i++) {
					var attr = soapDoc.set("a", array[i]);
					attr.setAttribute("n", aname);
				}
			}
			else {
				var attr = soapDoc.set("a");
				attr.setAttribute("n", aname);
			}
		}
		else {
			var attr = soapDoc.set("a", mods[aname]);
			attr.setAttribute("n", aname);
		}
	}

	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params).Body.ModifyServerResponse;		
	this.initFromJS(resp.server[0]);
}

/**
* Returns HTML for a tool tip for this domain.
*/
ZaServer.prototype.getToolTip =
function() {
	// update/null if modified
	if (!this._toolTip) {
		var html = new Array(20);
		var idx = 0;
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0'>";
		html[idx++] = "<tr valign='center'><td colspan='2' align='left'>";
		html[idx++] = "<div style='border-bottom: 1px solid black; white-space:nowrap; overflow:hidden;width:350'>";
		html[idx++] = "<table cellpadding='0' cellspacing='0' border='0' style='width:100%;'>";
		html[idx++] = "<tr valign='center'>";
		html[idx++] = "<td><b>" + AjxStringUtil.htmlEncode(this.name) + "</b></td>";
		html[idx++] = "<td align='right'>";
		html[idx++] = AjxImg.getImageHtml("Server");		
		html[idx++] = "</td>";
		html[idx++] = "</table></div></td></tr>";
		html[idx++] = "<tr></tr>";
		idx = this._addAttrRow(ZaItem.A_description, html, idx);		
		idx = this._addAttrRow(ZaItem.A_zimbraId, html, idx);
		html[idx++] = "</table>";
		this._toolTip = html.join("");
	}
	return this._toolTip;
}

ZaServer.prototype.remove = 
function() {
	var soapDoc = AjxSoapDoc.create("DeleteServerRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var resp = command.invoke(params);	
//	ZmCsfeCommand.invoke(soapDoc, null, null, null, true);	
}

ZaServer.prototype.refresh = 
function() {
	this.load();	
}

ZaServer.loadMethod = 
function(by, val, withConfig) {
	var _by = by ? by : "id";
	var _val = val ? val : this.id
	var soapDoc = AjxSoapDoc.create("GetServerRequest", "urn:zimbraAdmin", null);
	if(withConfig) {
		soapDoc.getMethod().setAttribute("applyConfig", "1");	
	} else {
		soapDoc.getMethod().setAttribute("applyConfig", "0");		
	}
	var elBy = soapDoc.set("server", _val);
	elBy.setAttribute("by", _by);
	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	resp = command.invoke(params);		
	this.initFromJS(resp.Body.GetServerResponse.server[0]);
	
	this.cos = this._app.getGlobalConfig();

	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
		this.getMyVolumes();
		this.getCurrentVolumes();
	}
}

ZaItem.loadMethods["ZaServer"].push(ZaServer.loadMethod);

ZaServer.prototype.initFromJS = function(server) {
	ZaItem.prototype.initFromJS.call(this, server);
	// convert installed/enabled services to hidden fields for xform binding
	var installed = this.attrs[ZaServer.A_zimbraServiceInstalled];
	if (installed) {
		if (AjxUtil.isString(installed)) {
			installed = [ installed ];
		}
		for (var i = 0; i < installed.length; i++) {
			var service = installed[i];
			this.attrs["_"+ZaServer.A_zimbraServiceInstalled+"_"+service] = true;
			this.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service] = false;
		}
	}
	
	var enabled = this.attrs[ZaServer.A_zimbraServiceEnabled];
	if (enabled) {
		if (AjxUtil.isString(enabled)) {
			enabled = [ enabled ];
		}
		for (var i = 0; i < enabled.length; i++) {
			var service = enabled[i];
			this.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service] = true;
		}
	}
	this[ZaServer.A_ServiceHostname] = this.attrs[ZaServer.A_ServiceHostname]; // a hack for New Account Wizard	
	this[ZaServer.A_showVolumes] = this.attrs[ZaServer.A_zimbraMailboxServiceEnabled];
}

ZaServer.prototype.getCurrentVolumes =
function () {
	if(!this.id)
		return;
	var soapDoc = AjxSoapDoc.create("GetCurrentVolumesRequest", "urn:zimbraAdmin", null);
	//find out which server I am on
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;	
	
	
	var children = respNode.childNodes;
	for (var i=0; i< children.length;  i++) {
		var child = children[i];
		if(child.nodeName == 'volume') {
			if(child.getAttribute(ZaServer.A_VolumeType) == ZaServer.PRI_MSG) {
				this[ZaServer.A_CurrentPrimaryMsgVolumeId] =  child.getAttribute(ZaServer.A_VolumeId);
			} else if (child.getAttribute(ZaServer.A_VolumeType) == ZaServer.SEC_MSG) {
				this[ZaServer.A_CurrentSecondaryMsgVolumeId] =  child.getAttribute(ZaServer.A_VolumeId);			
			} else if (child.getAttribute(ZaServer.A_VolumeType) == ZaServer.INDEX) {
				this[ZaServer.A_CurrentIndexMsgVolumeId] =  child.getAttribute(ZaServer.A_VolumeId);						
			}
		}
	}
}
ZaServer.prototype.getMyVolumes = 
function() {
	this[ZaServer.A_Volumes] = new Array();
	if(!this.id)
		return;
	var soapDoc = AjxSoapDoc.create("GetAllVolumesRequest", "urn:zimbraAdmin", null);
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;	

	var children = respNode.childNodes;
	for (var i=0; i< children.length;  i++) {
		var child = children[i];
		if(child.nodeName == 'volume') {
			var volume = new Object();		
			volume[ZaServer.A_VolumeId] = child.getAttribute(ZaServer.A_VolumeId);
			volume[ZaServer.A_VolumeName] = child.getAttribute(ZaServer.A_VolumeName);
			volume[ZaServer.A_VolumeRootPath] = child.getAttribute(ZaServer.A_VolumeRootPath);			
			volume[ZaServer.A_VolumeCompressBlobs] = child.getAttribute(ZaServer.A_VolumeCompressBlobs);
			volume[ZaServer.A_VolumeCompressionThreshold] = child.getAttribute(ZaServer.A_VolumeCompressionThreshold);			
			volume[ZaServer.A_VolumeType] = child.getAttribute(ZaServer.A_VolumeType);						
			this[ZaServer.A_Volumes].push(volume);
		}
	}
}

ZaServer.prototype.deleteVolume =
function (id) {
	if(!id)
		return false;
		
	var soapDoc = AjxSoapDoc.create("DeleteVolumeRequest", "urn:zimbraAdmin", null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;		
}

ZaServer.prototype.createVolume =
function (volume) {
	if(!volume)
		return false;
	var soapDoc = AjxSoapDoc.create("CreateVolumeRequest", "urn:zimbraAdmin", null);		
	var elVolume = soapDoc.set("volume", null);
	elVolume.setAttribute("type", volume[ZaServer.A_VolumeType]);
	elVolume.setAttribute("name", volume[ZaServer.A_VolumeName]);	
	elVolume.setAttribute("rootpath", volume[ZaServer.A_VolumeRootPath]);		
	elVolume.setAttribute("compressBlobs", volume[ZaServer.A_VolumeCompressBlobs]);		
	elVolume.setAttribute("compressionThreshold", volume[ZaServer.A_VolumeCompressionThreshold]);			
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;		
}

ZaServer.prototype.modifyVolume =
function (volume) {
	if(!volume)
		return false;
	var soapDoc = AjxSoapDoc.create("ModifyVolumeRequest", "urn:zimbraAdmin", null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, volume[ZaServer.A_VolumeId]);	
	var elVolume = soapDoc.set("volume", null);
	elVolume.setAttribute("type", volume[ZaServer.A_VolumeType]);
	elVolume.setAttribute("name", volume[ZaServer.A_VolumeName]);	
	elVolume.setAttribute("rootpath", volume[ZaServer.A_VolumeRootPath]);		
	elVolume.setAttribute("compressBlobs", volume[ZaServer.A_VolumeCompressBlobs]);		
	elVolume.setAttribute("compressionThreshold", volume[ZaServer.A_VolumeCompressionThreshold]);			
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;		
}

ZaServer.prototype.setCurrentVolume = function (id, type) {
	if(!id || !type)
		return false;	
	var soapDoc = AjxSoapDoc.create("SetCurrentVolumeRequest", "urn:zimbraAdmin", null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeType, type);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var respNode = ZmCsfeCommand.invoke(soapDoc, false, null, this.id, true).firstChild;			
}

ZaServer.initMethod = function (app) {
	this.attrs = new Object();
	this.id = "";
	this.name="";
}
ZaItem.initMethods["ZaServer"].push(ZaServer.initMethod);
