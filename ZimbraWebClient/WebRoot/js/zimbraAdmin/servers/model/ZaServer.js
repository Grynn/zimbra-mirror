/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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
* @class ZaServer
* This class represents zimbraServer objects. ZaServer extends ZaItem
* @author Greg Solovyev
* @contructor ZaServer
* @param app reference to the application instance
**/
ZaServer = function(app) {
	ZaItem.call(this, app,"ZaServer");
	this._init(app);
	//The type is required. The application tab uses it to show the right icon
	this.type = ZaItem.SERVER ; 
}

ZaServer.prototype = new ZaItem;
ZaServer.prototype.constructor = ZaServer;
ZaItem.loadMethods["ZaServer"] = new Array();
ZaItem.initMethods["ZaServer"] = new Array();
ZaItem.modifyMethods["ZaServer"] = new Array();

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
ZaServer.A_CurrentIndexVolumeId = "current_index_volume_id";
ZaServer.A_CurrentMsgVolumeId = "current_msg_volume_id";

//mail proxy
ZaServer.A_zimbraImapProxyBindPort="zimbraImapProxyBindPort";
ZaServer.A_zimbraImapSSLProxyBindPort="zimbraImapSSLProxyBindPort";
ZaServer.A_zimbraPop3ProxyBindPort="zimbraPop3ProxyBindPort";
ZaServer.A_zimbraPop3SSLProxyBindPort="zimbraPop3SSLProxyBindPort";

// other
ZaServer.A_zimbraScheduledTaskNumThreads = "zimbraScheduledTaskNumThreads" ;
ZaServer.A_zimbraIsMonitorHost = "zimbraIsMonitorHost";
ZaServer.A_showVolumes = "show_volumes"; //this attribute is immutable
ZaServer.A_zimbraLogHostname = "zimbraLogHostname";
ZaServer.A_isCurrentVolume = "isCurrentVolume";
ZaServer.STANDALONE = "standalone";
ZaServer.MASTER = "master";
ZaServer.SLAVE = "slave";

ZaServer.MSG = 1;
ZaServer.INDEX = 10;
ZaServer.currentkeys = {};
ZaServer.currentkeys[ZaServer.MSG] = ZaServer.A_CurrentMsgVolumeId;
ZaServer.currentkeys[ZaServer.INDEX] = ZaServer.A_CurrentIndexVolumeId;
ZaServer.volumeTypes =[ZaServer.MSG,ZaServer.INDEX];

ZaServer.DEFAULT_IMAP_PORT=143;
ZaServer.DEFAULT_IMAP_SSL_PORT=993;
ZaServer.DEFAULT_POP3_PORT=110;
ZaServer.DEFAULT_POP3_SSL_PORT=900;

ZaServer.DEFAULT_IMAP_PORT_ZCS=7143;
ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS=7993;
ZaServer.DEFAULT_POP3_PORT_ZCS=7110;
ZaServer.DEFAULT_POP3_SSL_PORT_ZCS=7900;

ZaServer.ERR_NOT_CIDR = 1;
ZaServer.ERR_NOT_STARTING_ADDR = 2;

ZaServer.DOT_TO_CIDR = {};
ZaServer.DOT_TO_CIDR["128.0.0.0"] = 1;
ZaServer.DOT_TO_CIDR["192.0.0.0"] = 2;
ZaServer.DOT_TO_CIDR["224.0.0.0"] = 3;
ZaServer.DOT_TO_CIDR["240.0.0.0"] = 4;
ZaServer.DOT_TO_CIDR["248.0.0.0"] = 5;
ZaServer.DOT_TO_CIDR["252.0.0.0"] = 6;
ZaServer.DOT_TO_CIDR["254.0.0.0"] = 7;
ZaServer.DOT_TO_CIDR["255.0.0.0"] = 8;

ZaServer.DOT_TO_CIDR["255.128.0.0"] = 9;
ZaServer.DOT_TO_CIDR["255.192.0.0"] = 10;
ZaServer.DOT_TO_CIDR["255.224.0.0"] = 11;
ZaServer.DOT_TO_CIDR["255.240.0.0"] = 12;
ZaServer.DOT_TO_CIDR["255.248.0.0"] = 13;
ZaServer.DOT_TO_CIDR["255.252.0.0"] = 14;
ZaServer.DOT_TO_CIDR["255.254.0.0"] = 15;
ZaServer.DOT_TO_CIDR["255.255.0.0"] = 16;

ZaServer.DOT_TO_CIDR["255.255.128.0"] = 17;
ZaServer.DOT_TO_CIDR["255.255.192.0"] = 16;
ZaServer.DOT_TO_CIDR["255.255.224.0"] = 19;
ZaServer.DOT_TO_CIDR["255.255.240.0"] = 20;
ZaServer.DOT_TO_CIDR["255.255.248.0"] = 21;
ZaServer.DOT_TO_CIDR["255.255.252.0"] = 22;
ZaServer.DOT_TO_CIDR["255.255.254.0"] = 23;
ZaServer.DOT_TO_CIDR["255.255.255.0"] = 24;

ZaServer.DOT_TO_CIDR["255.255.255.128"] = 25;
ZaServer.DOT_TO_CIDR["255.255.255.192"] = 26;
ZaServer.DOT_TO_CIDR["255.255.255.224"] = 27;
ZaServer.DOT_TO_CIDR["255.255.255.240"] = 28;
ZaServer.DOT_TO_CIDR["255.255.255.248"] = 29;
ZaServer.DOT_TO_CIDR["255.255.255.252"] = 30;
ZaServer.DOT_TO_CIDR["255.255.255.254"] = 31;
ZaServer.DOT_TO_CIDR["255.255.255.255"] = 32;

ZaServer.isValidPostfixSubnetString = function(mask) {
	//is this a CIDR
	var pos = mask.indexOf("/");
	var lastPos = mask.lastIndexOf("/");
	if(pos==-1 || pos!=lastPos) {
		//error! this is not a valid CIDR
		return ZaServer.ERR_NOT_CIDR;
	}
	var numNetworkBits = parseInt(mask.substr(lastPos+1,(mask.length-lastPos-1)));
	if(isNaN(numNetworkBits) || numNetworkBits=="" || numNetworkBits == null || numNetworkBits < 1) {
		return ZaServer.ERR_NOT_CIDR;
	}

	//convert the address to a number
	var addrString = mask.substr(0,lastPos);
	var addrNumber = ZaServer.octetsToLong(addrString);
	if(addrNumber < 0) {
		return ZaServer.ERR_NOT_CIDR;
	}

	//do we have a starting address?
	var maskNumber = 0;
	var lastIndex = 32 - numNetworkBits;
	for(var j=31; j>=lastIndex;j-- ) {
		maskNumber += Math.pow(2,j);
	}
	if(addrNumber != ZaServer.applyMask(addrNumber, maskNumber)) {
		return ZaServer.ERR_NOT_STARTING_ADDR;
	}
	return 0;
}
/**
 * extract number of network bits from CIDR string
 * @return integer
 */
ZaServer.iGetNumNetBits = function(mask) {
	var pos = mask.indexOf("/");
	var lastPos = mask.lastIndexOf("/");
	var numNetworkBits = parseInt(mask.substr(lastPos+1,(mask.length-lastPos-1)));
	return numNetworkBits;
}
/**
 * @member ZaServer.oGetStartingAddress
 * @argument mask - CIDR representation of a network (A.B.C.D/E)
 * @return octet string that represents the last address of the network segment
 */
ZaServer.oGetStartingAddress = function (mask) {
	return ZaServer.longToOctets(ZaServer.lGetStartingAddress(mask));
}

/**
 * @member ZaServer.lGetStartingAddress
 * @argument mask - CIDR representation of a network (A.B.C.D/E)
 * @return long number represents the first address of the network segment
 */
ZaServer.lGetStartingAddress = function (mask) {
	var numNetworkBits = ZaServer.iGetNumNetBits(mask);
	var lastPos = mask.lastIndexOf("/");
	//convert the address to a number
	var addrString = mask.substr(0,lastPos);
	var addrNumber = ZaServer.octetsToLong(addrString);
	var maskNumber = 0;
	var lastIndex = 32 - numNetworkBits;
	for(var j=31; j>=lastIndex;j-- ) {
		maskNumber += Math.pow(2,j);
	}	
	var firstAddr = ZaServer.applyMask(addrNumber, maskNumber);
	return firstAddr;
}

/**
 * @member ZaServer.lGetEndingAddress
 * @argument firstAddr - long
 * @argument numNetBits - int
 * @return long number represents the last address of the network segment
 */
ZaServer.lGetEndingAddress = function (firstAddr, numNetBits) {
	var lLastAddr = firstAddr + Math.pow(2,(32 - numNetBits))-1;
	return lLastAddr;
}

/**
 * @member ZaServer.applyMask
 * @argument addr1 - long address
 * @argument netMask - long network mask
 * @return long number that represents the starting address of the network defined by addr1 and netMask
 */

ZaServer.applyMask = function (addr1, netMask) {
	var val = (addr1 & netMask);
	if(val >= 0) {
		return val;
	} else 	{
		return (4294967296+val);
	}
}

ZaServer.octetsToLong = function (addrString) {
	var octets = addrString.split(".");
	if(octets.length !=4) {
		return -1;
	}	
	var addrNumber = Math.pow(256,3)*parseInt(octets[0]) + Math.pow(256,2)*parseInt(octets[1]) + Math.pow(256,1)*parseInt(octets[2]) + parseInt(octets[3]);
	return addrNumber;
}

ZaServer.longToOctets = function(addrNumber) {
	var ip1 = Math.floor(addrNumber/Math.pow(256,3));
    var ip2 = Math.floor((addrNumber%Math.pow(256,3))/Math.pow(256,2));
    var ip3 = Math.floor(((addrNumber%Math.pow(256,3))%Math.pow(256,2))/Math.pow(256,1));
    var ip4 = Math.floor((((addrNumber%Math.pow(256,3))%Math.pow(256,2))%Math.pow(256,1))/Math.pow(256,0));
    return [ip1,ip2,ip3,ip4].join(".");
}

ZaServer.volumeTypeChoices = new XFormChoices({1:ZaMsg.VM_VOLUME_Msg, 10:ZaMsg.VM_VOLUME_Index}, XFormChoices.HASH);
ZaServer.volumeObjModel = {
	items: [
		{id:ZaServer.A_isCurrentVolume, type: _ENUM_, choices: [false,true]	},
		{id:ZaServer.A_VolumeId, type:_NUMBER_},
		{id:ZaServer.A_VolumeName, type:_STRING_},
		{id:ZaServer.A_VolumeType, type:_ENUM_, choices:ZaServer.volumeTypes,defaultValue:ZaServer.MSG},
		{id:ZaServer.A_VolumeRootPath, type:_STRING_},
		{id:ZaServer.A_VolumeCompressBlobs, type:_ENUM_, choices:[false,true], defaultValue:true},
		{id:ZaServer.A_VolumeCompressionThreshold, type:_NUMBER_,defaultValue:4096}				
	],
	type:_OBJECT_
}
		
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
		{id:ZaServer.A_zimbraMtaMyNetworks, ref:"attrs/" +  ZaServer.A_zimbraMtaMyNetworks, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraMtaDnsLookupsEnabled, ref:"attrs/" +  ZaServer.A_zimbraMtaDnsLookupsEnabled, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		// ...other...
		{id:ZaServer.A_SmtpHostname, ref:"attrs/" +  ZaServer.A_SmtpHostname, type:_COS_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_SmtpPort, ref:"attrs/" +  ZaServer.A_SmtpPort, type:_COS_PORT_},
		{id:ZaServer.A_SmtpTimeout, ref:"attrs/" + ZaServer.A_SmtpTimeout, type:_COS_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_LmtpAdvertisedName, ref:"attrs/" +  ZaServer.A_LmtpAdvertisedName, type:_STRING_, maxLength: 128 },
		{id:ZaServer.A_LmtpBindAddress, ref:"attrs/" +  ZaServer.A_LmtpBindAddress, type:_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_LmtpBindPort, ref:"attrs/" +  ZaServer.A_LmtpBindPort, type:_COS_PORT_},		
		{id:ZaServer.A_LmtpNumThreads, ref:"attrs/" +  ZaServer.A_LmtpNumThreads, type:_COS_NUMBER_, minInclusive: 0 },
		{id:ZaServer.A_zimbraScheduledTaskNumThreads, ref:"attrs/" +  ZaServer.A_zimbraScheduledTaskNumThreads, type:_COS_NUMBER_, minInclusive: 1 },
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
		{id:ZaServer.A_Volumes, type:_LIST_, listItem:ZaServer.volumeObjModel},
		{id:ZaServer.A_showVolumes, ref:ZaServer.A_showVolumes, type: _ENUM_, choices: [false,true]}
	]	
};
		
ZaServer.prototype.toString = function() {
	return this.name;
}

ZaServer.getAll =
function(app) {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", "urn:zimbraAdmin", null);	
//	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;	
	var reqMgrParams = {
		controller : app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaServer, app);
	list.loadFromJS(resp);	
	return list;
}

ZaServer.modifyMethod = function (tmpObj) {
	if(tmpObj.attrs == null) {
		//show error msg
		this._app.getCurrentController()._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		this._app.getCurrentController()._errorDialog.popup();		
		return false;	
	}
	
	// update zimbraServiceEnabled
	var svcInstalled = AjxUtil.isString(tmpObj.attrs[ZaServer.A_zimbraServiceInstalled])
							? [ tmpObj.attrs[ZaServer.A_zimbraServiceInstalled] ]
							: tmpObj.attrs[ZaServer.A_zimbraServiceInstalled];
	if (svcInstalled) {
		// get list of actually enabled fields
		var enabled = [];
		for (var i = 0; i < svcInstalled.length; i++) {
			var service = svcInstalled[i];
			if (tmpObj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service]) {
				enabled.push(service);
			}			
		}
		
		// see if list of actually enabled fields is same as before
		
		var dirty = false; 
		
		if (this.attrs[ZaServer.A_zimbraServiceEnabled]) {
			var prevEnabled = AjxUtil.isString(this.attrs[ZaServer.A_zimbraServiceEnabled])
							? [ this.attrs[ZaServer.A_zimbraServiceEnabled] ]
							: this.attrs[ZaServer.A_zimbraServiceEnabled];
							
			dirty = (enabled.length != prevEnabled.length);		
			
			if (!dirty) {
				for (var i = 0; i < prevEnabled.length; i++) {
					var service = prevEnabled[i];
					if (!tmpObj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service]) {
						dirty = true;
						break;
					}
				}
			}
		}
		
		// save new list of enabled fields
		if (dirty) {
			tmpObj.attrs[ZaServer.A_zimbraServiceEnabled] = enabled;
		}
	}	

	//modify volumes
	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
		//remove Volumes
		if(tmpObj[ZaServer.A_RemovedVolumes]) {
			var cnt = tmpObj[ZaServer.A_RemovedVolumes].length;
			for(var i = 0; i < cnt; i++) {
				if(tmpObj[ZaServer.A_RemovedVolumes][i][ZaServer.A_VolumeId] > 0) {
					this.deleteVolume(tmpObj[ZaServer.A_RemovedVolumes][i][ZaServer.A_VolumeId]);			
				}
			}
		}
	
		if(tmpObj[ZaServer.A_Volumes]) {			
			var tmpVolumeMap = new Array();
			var cnt = tmpObj[ZaServer.A_Volumes].length;
			for(var i = 0; i < cnt; i++) {
				tmpVolumeMap.push(tmpObj[ZaServer.A_Volumes][i]);
			}
		
			//create new Volumes
			cnt = tmpVolumeMap.length;
			for(var i = 0; i < cnt; i++) {
				//consider only new rows (no VolumeID)
				//ignore empty rows, Bug 4425
				if(!(tmpVolumeMap[i][ZaServer.A_VolumeId]>0) && tmpVolumeMap[i][ZaServer.A_VolumeName] && tmpVolumeMap[i][ZaServer.A_VolumeRootPath]) {
					var newId = this.createVolume(tmpVolumeMap[i]);	
					if(newId>0) {
						//find if we assigned this volume to current volumes
						for(var key in ZaServer.currentkeys) {
							if(tmpObj[ZaServer.currentkeys[key]] == tmpVolumeMap[i][ZaServer.A_VolumeId]) {
								tmpObj[ZaServer.currentkeys[key]] = newId;
							}
						}
					}		
				}
			}
	
			//modify existing volumes
			cnt--;	
			var cnt2 = this[ZaServer.A_Volumes].length;
			for(var i = cnt; i >= 0; i--) {
				var newVolume = tmpVolumeMap[i];
				var oldVolume;
				for (var ix =0; ix < cnt2; ix++) {
					oldVolume = this[ZaServer.A_Volumes][ix];
					if(oldVolume[ZaServer.A_VolumeId] == newVolume[ZaServer.A_VolumeId]) {
						//check attributes
						var modified = false;
						for(var attr in oldVolume) {
							if(oldVolume[attr] != newVolume[attr]) {
								modified = true;
								break;
							}
						}
						
						if(modified) {
							this.modifyVolume(tmpVolumeMap[i]);
						}
						tmpVolumeMap.splice(i,1);
					}
				}
			}
		}

		//set current volumes
		for(var key in ZaServer.currentkeys) {
			if(tmpObj[ZaServer.currentkeys[key]] && (!this[ZaServer.currentkeys[key]] || (this[ZaServer.currentkeys[key]] !=tmpObj[ZaServer.currentkeys[key]]))) {
				this.setCurrentVolume(tmpObj[ZaServer.currentkeys[key]], key);
			}
			
		}
	}	
		
	//create a ModifyServerRequest SOAP request
	var soapDoc = AjxSoapDoc.create("ModifyServerRequest", "urn:zimbraAdmin", null);
	soapDoc.set("id", this.id);
	//get the list of changed fields
	var mods = new Object();
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || /^_/.test(a) || a == ZaServer.A_zimbraServiceInstalled)
			continue;
		
		if (this.attrs[a] != tmpObj.attrs[a] ) {
			if(tmpObj.attrs[a] instanceof Array) {
				var array = tmpObj.attrs[a];
				if (array.length > 0) {
					for (var i = 0; i < array.length; i++) {
						var attr = soapDoc.set("a", array[i]);
						attr.setAttribute("n", a);
					}
				} else {
					var attr = soapDoc.set("a");
					attr.setAttribute("n", a);
				}	
			} else {
				var attr = soapDoc.set("a", tmpObj.attrs[a]);
				attr.setAttribute("n", a);
			}
		}
	}
	//modify the server
//	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyServerResponse;		
	this.initFromJS(resp.server[0]);		

}
ZaItem.modifyMethods["ZaServer"].push(ZaServer.modifyMethod);


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
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams);	
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
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_SERVER
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);		
	this.initFromJS(resp.Body.GetServerResponse.server[0]);
	
	this.cos = this._app.getGlobalConfig();

	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
		this.getMyVolumes();
		this.getCurrentVolumes();
	}
}

ZaItem.loadMethods["ZaServer"].push(ZaServer.loadMethod);

ZaServer.loadNIFS = 
function(by, val, withConfig) {
	var _by = by ? by : "id";
	var _val = val ? val : this.id
	var soapDoc = AjxSoapDoc.create("GetServerNIfsRequest", "urn:zimbraAdmin", null);
	var elBy = soapDoc.set("server", _val);
	elBy.setAttribute("by", _by);
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_SERVER
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);
	if(resp.Body.GetServerNIfsResponse && resp.Body.GetServerNIfsResponse.ni) {
		var NIs = resp.Body.GetServerNIfsResponse.ni;
		var cnt = NIs.length;
		this.nifs = [];
		for(var i=0;i<cnt;i++) {
			var ni = {};
			ZaItem.prototype.initFromJS.call(ni, NIs[i]);
			this.nifs.push(ni);
		}
	}
}

ZaItem.loadMethods["ZaServer"].push(ZaServer.loadNIFS);

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
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	params.targetServer = this.id;
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_VOL
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);		
	var resp = resp.Body.GetCurrentVolumesResponse;
	
	var volumes = resp.volume;
	if(volumes) {
		var cnt = volumes.length;
		for (var i=0; i< cnt;  i++) {
			var volume = volumes[i];
			for(var key in ZaServer.currentkeys) {
				if(volume[ZaServer.A_VolumeType]==key) {
					this[ZaServer.currentkeys[key]] = volume[ZaServer.A_VolumeId];
				}
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
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	params.targetServer = this.id;
	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_VOL
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);		
	var resp = resp.Body.GetAllVolumesResponse;

	var volumes = resp.volume;
	if(volumes) {
		var cnt = volumes.length;
		for (var i=0; i< cnt;  i++) {
			this[ZaServer.A_Volumes].push(volumes[i]);	
		}
	}/*
	
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
	}*/
}

ZaServer.compareVolumesByName = function (a,b) {
	
	if(a[ZaServer.A_VolumeName]>b[ZaServer.A_VolumeName])
		return 1;
	if(a[ZaServer.A_VolumeName]<b[ZaServer.A_VolumeName])
		return -1;
	return 0;
	
}

ZaServer.prototype.deleteVolume =
function (id) {
	if(!id)
		return false;
		
	var soapDoc = AjxSoapDoc.create("DeleteVolumeRequest", "urn:zimbraAdmin", null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: false
	}
	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
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
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: false
	}
	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_CREATE_VOL
	}
	var response = ZaRequestMgr.invoke(params, reqMgrParams) ;
	if(response.Body && response.Body.CreateVolumeResponse && response.Body.CreateVolumeResponse.volume) {
		return response.Body.CreateVolumeResponse.volume[0][ZaServer.A_VolumeId];
	}
	
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
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: false
	}
	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.prototype.setCurrentVolume = function (id, type) {
	if(!id || !type)
		return false;	
	var soapDoc = AjxSoapDoc.create("SetCurrentVolumeRequest", "urn:zimbraAdmin", null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeType, type);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: false
	}
	
	var reqMgrParams = {
		controller : this._app.getCurrentController(),
		busyMsg : ZaMsg.BUSY_SET_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.initMethod = function (app) {
	this.attrs = new Object();
	this.id = "";
	this.name="";
}
ZaItem.initMethods["ZaServer"].push(ZaServer.initMethod);
