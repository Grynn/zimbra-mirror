/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* @class ZaServer
* This class represents zimbraServer objects. ZaServer extends ZaItem
* @author Greg Solovyev
* @contructor ZaServer
* @param app reference to the application instance
**/
ZaServer = function() {
	ZaItem.call(this, "ZaServer");
	this._init();
	//The type is required. The application tab uses it to show the right icon
	this.type = ZaItem.SERVER ; 
}
ZaServer.modelExtensions = [];
ZaServer.prototype = new ZaItem;
ZaServer.prototype.constructor = ZaServer;
ZaItem.loadMethods["ZaServer"] = new Array();
ZaItem.initMethods["ZaServer"] = new Array();
ZaItem.modifyMethods["ZaServer"] = new Array();
ZaItem.modelExtensions["ZaServer"] = new Array();
//attribute name constants, this values are taken from zimbra.schema
ZaServer.A_name = "cn";
ZaServer.A_description = "description";
ZaServer.A_notes = "zimbraNotes";
ZaServer.A_Service = "zimbraService";
ZaServer.A_ServiceHostname = "zimbraServiceHostname";
ZaServer.A_zimbraMailPort = "zimbraMailPort";
ZaServer.A_zimbraMailSSLPort = "zimbraMailSSLPort";
ZaServer.A_zimbraMailMode = "zimbraMailMode";
ZaServer.A_zimbraMailReferMode = "zimbraMailReferMode";
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
ZaServer.A_zimbraMailProxyServiceInstalled = "_"+ZaServer.A_zimbraServiceInstalled+"_proxy";
ZaServer.A_zimbraVmwareHAServiceInstalled = "_"+ ZaServer.A_zimbraServiceInstalled+"_vmwareha";

ZaServer.A_zimbraReverseProxyHttpEnabled = "zimbraReverseProxyHttpEnabled";
ZaServer.A_zimbraServiceEnabled = "zimbraServiceEnabled";
ZaServer.A_zimbraLdapServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_ldap";
ZaServer.A_zimbraMailboxServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_mailbox";
ZaServer.A_zimbraMtaServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_mta";
ZaServer.A_zimbraSnmpServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_snmp";
ZaServer.A_zimbraAntiVirusServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_antivirus";
ZaServer.A_zimbraAntiSpamServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_antispam";
ZaServer.A_zimbraSpellServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_spell";
ZaServer.A_zimbraLoggerServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_logger";
ZaServer.A_zimbraMailProxyServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_proxy";
ZaServer.A_zimbraVmwareHAServiceEnabled = "_"+ZaServer.A_zimbraServiceEnabled+"_vmwareha";

// MTA
ZaServer.A_zimbraMtaSaslAuthEnable = "zimbraMtaSaslAuthEnable";
ZaServer.A_zimbraMtaDnsLookupsEnabled = "zimbraMtaDnsLookupsEnabled";
ZaServer.A_zimbraMtaRelayHost = "zimbraMtaRelayHost";
ZaServer.A_zimbraMtaTlsAuthOnly = "zimbraMtaTlsAuthOnly";
ZaServer.A_zimbraMtaMyNetworks = "zimbraMtaMyNetworks";
//milter server
ZaServer.A_zimbraMilterBindPort = "zimbraMilterBindPort";
ZaServer.A_zimbraMilterBindAddress = "zimbraMilterBindAddress";
ZaServer.A_zimbraMilterServerEnabled = "zimbraMilterServerEnabled";

//smtp
ZaServer.A_zimbraSmtpHostname  = "zimbraSmtpHostname";
ZaServer.A_SmtpPort = "zimbraSmtpPort";
ZaServer.A_SmtpTimeout = "zimbraSmtpTimeout";
//Lmtp
ZaServer.A_LmtpAdvertisedName = "zimbraLmtpAdvertisedName";
ZaServer.A_LmtpBindAddress = "zimbraLmtpBindAddress";
ZaServer.A_LmtpBindPort = "zimbraLmtpBindPort";
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

//proxy lookup target
ZaServer.A_zimbraReverseProxyLookupTarget = "zimbraReverseProxyLookupTarget";

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
ZaServer.A_isCurrent = "isCurrent";

//VAMI Appliance Update
ZaServer.A_zimbraApplianceVendor = "zimbraApplianceVendor";
ZaServer.A_zimbraApplianceName = "zimbraApplianceName";
ZaServer.A_zimbraApplianceVersion = "zimbraApplianceVersion";
ZaServer.A_zimbraApplianceFullVersion = "zimbraApplianceFullVersion";
ZaServer.A_zimbraApplianceDetails = "zimbraApplianceDetails";
ZaServer.A_zimbraApplianceDefaultRepUrl = "zimbraApplianceDefaultRepUrl";
ZaServer.A_zimbraApplianceCustomRepUrl = "zimbraApplianceCustomRepUrl";
ZaServer.A_zimbraApplianceCustomRepUrlUser = "zimbraApplianceCustomRepUrlUser";
ZaServer.A_zimbraApplianceCustomRepUrlPass = "zimbraApplianceCustomRepUrlPass";
ZaServer.A_zimbraApplianceUpdateOption = "zimbraApplianceUpdateOption";
ZaServer.A_zimbraApplianceUpdateType = "zimbraApplianceUpdateType";
ZaServer.A_zimbraApplianceUpdatehourofrun = "zimbraApplianceUpdatehourofrun";

//bind ip address
ZaServer.A_zimbraMailBindAddress = "zimbraMailBindAddress";
ZaServer.A_zimbraMailSSLBindAddress = "zimbraMailSSLBindAddress";
ZaServer.A_zimbraMailSSLClientCertBindAddress = "zimbraMailSSLClientCertBindAddress";
ZaServer.A_zimbraAdminBindAddress = "zimbraAdminBindAddress";

//spnego
ZaServer.A_zimbraSpnegoAuthPrincipal = "zimbraSpnegoAuthPrincipal";
ZaServer.A_zimbraSpnegoAuthTargetName = "zimbraSpnegoAuthTargetName";

// Auto provision
ZaServer.A_zimbraAutoProvPollingInterval = "zimbraAutoProvPollingInterval";
ZaServer.A_zimbraAutoProvScheduledDomains = "zimbraAutoProvScheduledDomains";

// web client authentication
ZaServer.A_zimbraMailSSLClientCertMode = "zimbraMailSSLClientCertMode";
ZaServer.A_zimbraMailSSLClientCertPort = "zimbraMailSSLClientCertPort";
ZaServer.A_zimbraMailSSLProxyClientCertPort = "zimbraMailSSLProxyClientCertPort";
ZaServer.A_zimbraReverseProxyMailMode = "zimbraReverseProxyMailMode";
ZaServer.A_zimbraReverseProxyClientCertMode = "zimbraReverseProxyClientCertMode";
ZaServer.A_zimbraReverseProxyClientCertCA = "zimbraReverseProxyClientCertCA";

// other
ZaServer.A_zimbraScheduledTaskNumThreads = "zimbraScheduledTaskNumThreads" ;
ZaServer.A_zimbraMailPurgeSleepInterval = "zimbraMailPurgeSleepInterval" ;
ZaServer.A_zimbraIsMonitorHost = "zimbraIsMonitorHost";
ZaServer.A_showVolumes = "show_volumes"; //this attribute is immutable
ZaServer.A_zimbraLogHostname = "zimbraLogHostname";
ZaServer.A_isCurrentVolume = "isCurrentVolume";
ZaServer.STANDALONE = "standalone";
ZaServer.MASTER = "master";
ZaServer.SLAVE = "slave";
ZaServer.A2_volume_selection_cache = "volume_selection_cache";

ZaServer.MSG = 1;
ZaServer.INDEX = 10;
ZaServer.currentkeys = {};
ZaServer.currentkeys[ZaServer.MSG] = ZaServer.A_CurrentMsgVolumeId;
ZaServer.currentkeys[ZaServer.INDEX] = ZaServer.A_CurrentIndexVolumeId;
ZaServer.volumeTypes =[ZaServer.MSG,ZaServer.INDEX];

ZaServer.updateoptions = {};
ZaServer.updateoptions[0] = ZaMsg.NONE = 111;
ZaServer.updateoptions[2] = ZaMsg.EVERYDAY = 112;
ZaServer.updateoptions[3] = ZaMsg.EVERYSUNDAY = 113;
ZaServer.updateoptions[4] = ZaMsg.EVERYMONDAY =114;
ZaServer.updateoptions[5] = ZaMsg.EVERYTUESDAY = 115;
ZaServer.updateoptions[6] = ZaMsg.EVERYWEDNESDAY = 116;
ZaServer.updateoptions[7] = ZaMsg.EVERYTHURSDAY = 117;
ZaServer.updateoptions[8] = ZaMsg.EVERYFRIDAY = 118;
ZaServer.updateoptions[9] = ZaMsg.EVERYSATURDAY = 119;

ZaServer.updatehourofrun = {};
ZaServer.updatehourofrun[0] = ZaMsg.ONEAM = 201;
ZaServer.updatehourofrun[1] = ZaMsg.TWOAM = 202;
ZaServer.updatehourofrun[2] = ZaMsg.THREEAM = 203;
ZaServer.updatehourofrun[3] = ZaMsg.FOURAM = 204;
ZaServer.updatehourofrun[4] = ZaMsg.FIVEAM = 205;
ZaServer.updatehourofrun[5] = ZaMsg.SIXAM = 206;
ZaServer.updatehourofrun[6] = ZaMsg.SEVENAM = 207;
ZaServer.updatehourofrun[7] = ZaMsg.EIGHTAM = 208;
ZaServer.updatehourofrun[8] = ZaMsg.NINEAM = 209;
ZaServer.updatehourofrun[9] = ZaMsg.TENAM = 210;
ZaServer.updatehourofrun[10] = ZaMsg.ELEVENAM = 211;
ZaServer.updatehourofrun[11] = ZaMsg.TWELVEAM = 212;
ZaServer.updatehourofrun[12] = ZaMsg.TWELVEPM = 312;
ZaServer.updatehourofrun[13] = ZaMsg.ELEVENPM = 311; 
ZaServer.updatehourofrun[14] = ZaMsg.TENPM = 310;
ZaServer.updatehourofrun[15] = ZaMsg.NINEPM = 309;
ZaServer.updatehourofrun[16] = ZaMsg.EIGHTPM = 308;
ZaServer.updatehourofrun[17] = ZaMsg.SEVENPM = 307;
ZaServer.updatehourofrun[18] = ZaMsg.SIXPM = 306;
ZaServer.updatehourofrun[19] = ZaMsg.FIVEPM = 305;
ZaServer.updatehourofrun[20] = ZaMsg.FOURPM = 304;
ZaServer.updatehourofrun[21] = ZaMsg.THREEPM = 303;
ZaServer.updatehourofrun[22] = ZaMsg.TWOPM = 302;
ZaServer.updatehourofrun[23] = ZaMsg.ONEPM =301;

ZaServer.APPLIANCEUPDATE_CD = 2;
ZaServer.APPLIANCEUPDATE_DEFAULTREP = 1;
ZaServer.APPLIANCEUPDATE_CUSTOMREP = 3;

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
ZaServer.DOT_TO_CIDR["0x80000000"] = ZaServer.DOT_TO_CIDR["128.0.0.0"] = 1;
ZaServer.DOT_TO_CIDR["0xc0000000"] = ZaServer.DOT_TO_CIDR["192.0.0.0"] = 2;
ZaServer.DOT_TO_CIDR["0xe0000000"] = ZaServer.DOT_TO_CIDR["224.0.0.0"] = 3;
ZaServer.DOT_TO_CIDR["0xf0000000"] = ZaServer.DOT_TO_CIDR["240.0.0.0"] = 4;
ZaServer.DOT_TO_CIDR["0xf8000000"] = ZaServer.DOT_TO_CIDR["248.0.0.0"] = 5;
ZaServer.DOT_TO_CIDR["0xfc000000"] = ZaServer.DOT_TO_CIDR["252.0.0.0"] = 6;
ZaServer.DOT_TO_CIDR["0xfe000000"] = ZaServer.DOT_TO_CIDR["254.0.0.0"] = 7;
ZaServer.DOT_TO_CIDR["0xff000000"] = ZaServer.DOT_TO_CIDR["255.0.0.0"] = 8;

ZaServer.DOT_TO_CIDR["0xff800000"] = ZaServer.DOT_TO_CIDR["255.128.0.0"] = 9;
ZaServer.DOT_TO_CIDR["0xffc00000"] = ZaServer.DOT_TO_CIDR["255.192.0.0"] = 10;
ZaServer.DOT_TO_CIDR["0xffe00000"] = ZaServer.DOT_TO_CIDR["255.224.0.0"] = 11;
ZaServer.DOT_TO_CIDR["0xfff00000"] = ZaServer.DOT_TO_CIDR["255.240.0.0"] = 12;
ZaServer.DOT_TO_CIDR["0xfff80000"] = ZaServer.DOT_TO_CIDR["255.248.0.0"] = 13;
ZaServer.DOT_TO_CIDR["0xfffc0000"] = ZaServer.DOT_TO_CIDR["255.252.0.0"] = 14;
ZaServer.DOT_TO_CIDR["0xfffe0000"] = ZaServer.DOT_TO_CIDR["255.254.0.0"] = 15;
ZaServer.DOT_TO_CIDR["0xffff0000"] = ZaServer.DOT_TO_CIDR["255.255.0.0"] = 16;

ZaServer.DOT_TO_CIDR["0xffff8000"] = ZaServer.DOT_TO_CIDR["255.255.128.0"] = 17;
ZaServer.DOT_TO_CIDR["0xffffc000"] = ZaServer.DOT_TO_CIDR["255.255.192.0"] = 16;
ZaServer.DOT_TO_CIDR["0xffffe000"] = ZaServer.DOT_TO_CIDR["255.255.224.0"] = 19;
ZaServer.DOT_TO_CIDR["0xfffff000"] = ZaServer.DOT_TO_CIDR["255.255.240.0"] = 20;
ZaServer.DOT_TO_CIDR["0xfffff800"] = ZaServer.DOT_TO_CIDR["255.255.248.0"] = 21;
ZaServer.DOT_TO_CIDR["0xfffffc00"] = ZaServer.DOT_TO_CIDR["255.255.252.0"] = 22;
ZaServer.DOT_TO_CIDR["0xfffffe00"] = ZaServer.DOT_TO_CIDR["255.255.254.0"] = 23;
ZaServer.DOT_TO_CIDR["0xffffff00"] = ZaServer.DOT_TO_CIDR["255.255.255.0"] = 24;

ZaServer.DOT_TO_CIDR["0xffffff80"] = ZaServer.DOT_TO_CIDR["255.255.255.128"] = 25;
ZaServer.DOT_TO_CIDR["0xffffffc0"] = ZaServer.DOT_TO_CIDR["255.255.255.192"] = 26;
ZaServer.DOT_TO_CIDR["0xffffffe0"] = ZaServer.DOT_TO_CIDR["255.255.255.224"] = 27;
ZaServer.DOT_TO_CIDR["0xfffffff0"] = ZaServer.DOT_TO_CIDR["255.255.255.240"] = 28;
ZaServer.DOT_TO_CIDR["0xfffffff8"] = ZaServer.DOT_TO_CIDR["255.255.255.248"] = 29;
ZaServer.DOT_TO_CIDR["0xfffffffc"] = ZaServer.DOT_TO_CIDR["255.255.255.252"] = 30;
ZaServer.DOT_TO_CIDR["0xfffffffe"] = ZaServer.DOT_TO_CIDR["255.255.255.254"] = 31;
ZaServer.DOT_TO_CIDR["0xffffffff"] = ZaServer.DOT_TO_CIDR["255.255.255.255"] = 32;

ZaServer.FLUSH_CACHE_RIGHT = "flushCache";
ZaServer.MANAGE_VOLUME_RIGHT = "manageVolume";

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
        var patrn=/^[A-Fa-f0-9:.]+$/;
   if(patrn.test(addrString)){
        var temp1= addrString.indexOf(".");
        var temp2= addrString.indexOf(":");
        var temp3= addrString.indexOf("::");

	   if(temp3!=-1){
        var octets = addrString.split("::");
        if(octets.length !=2) {
                return -1;
        }
         else if(temp1==-1&&temp2>-1){
        octetsub0=octets[0].split(":");
        octetsub1=octets[1].split(":");
          if (octetsub0.length+octetsub1.length>7)
           return -1;
           else{
                   for(var j=0;j<octetsub0.length;j++){
                     if(octetsub0[j].length>4)
                     return -1;
                   }
                   for(var j=0;j<octetsub1.length;j++){
                     if(octetsub1[j].length>4)
                     return -1;
                   }

           }
        }else if((temp1>-1)){
                      if(octets[0]!="")
                      return -1;
                      else{
                          octetsub=octets[1].split(".");
                          for(var j=0;j<octetsub.length;j++){
                          if(octetsub[j]>parseInt("255"))
                          return -1;
                          }
             }
            }
                   return 0;
    }

        if(temp1!=-1){
              var  octets = addrString.split(".");
              if(octets.length !=4) {
                return -1;
              } else{
                    for(var i=0;i<octets.length;i++){
                    var j=octets[i];
                    if(j>parseInt("255")){
                    return -1;
              }
}
        var addrNumber = Math.pow(256,3)*parseInt(octets[0]) + Math.pow(256,2)*parseInt(octets[1]) + Math.pow(256,1)*parseInt(octets[2]) + parseInt(octets[3]);
        return addrNumber;
             }
     }

        if(temp2!=-1){
        var octets = addrString.split(":");
        if(octets.length !=8) {
                return -1;
       } else {
        for(var i=0;i<octets.length;i++){
        if(octets[i].length>4)
        return -1;
        }
 return 0;
    }
        }
}
return -1;
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
		{id:ZaServer.A_VolumeCompressionThreshold, type:_NUMBER_,defaultValue:4096},
		{id:"_index", type:_NUMBER_}				
	],
	type:_OBJECT_
}
		
ZaServer.myXModel = {
	items: [
		{id:ZaItem.A_zimbraId, type:_STRING_, ref:"attrs/" + ZaItem.A_zimbraId},
		{id:ZaItem.A_zimbraCreateTimestamp, ref:"attrs/" + ZaItem.A_zimbraCreateTimestamp},
		{id:ZaServer.A_name, ref:"attrs/" + ZaServer.A_name, type:_STRING_},
//		{id:ZaServer.A_description, ref:"attrs/" +  ZaServer.A_description, type:_STRING_},
         ZaItem.descriptionModelItem,   
        {id:ZaServer.A_notes, ref:"attrs/" +  ZaServer.A_notes, type:_STRING_, maxLength:1024},
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
        {id:ZaServer.A_zimbraVmwareHAServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraVmwareHAServiceEnabled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailProxyServiceEnabled, ref:"attrs/"+ZaServer.A_zimbraMailProxyServiceEnabled, type: _ENUM_, choices: [false,true] },		
		{id:ZaServer.A_zimbraReverseProxyLookupTarget, ref:"attrs/"+ZaServer.A_zimbraReverseProxyLookupTarget, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
		{id:ZaServer.A_zimbraLdapServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraLdapServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailboxServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMailboxServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMtaServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMtaServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSnmpServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraSnmpServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiVirusServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraAntiVirusServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraAntiSpamServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraAntiSpamServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraSpellServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraSpellServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraLoggerServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraLoggerServiceInstalled, type: _ENUM_, choices: [false,true] },
		{id:ZaServer.A_zimbraMailProxyServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraMailProxyServiceInstalled, type: _ENUM_, choices: [false,true] },
        {id:ZaServer.A_zimbraVmwareHAServiceInstalled, ref:"attrs/"+ZaServer.A_zimbraVmwareHAServiceInstalled, type: _ENUM_, choices: [false,true] },
		// MTA
		{id:ZaServer.A_zimbraMtaSaslAuthEnable, ref:"attrs/" +  ZaServer.A_zimbraMtaSaslAuthEnable, type: _COS_ENUM_, choices: ["yes", "no"] },
		{id:ZaServer.A_zimbraMtaTlsAuthOnly, ref:"attrs/" +  ZaServer.A_zimbraMtaTlsAuthOnly, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		{id:ZaServer.A_zimbraMtaRelayHost, ref:"attrs/" +  ZaServer.A_zimbraMtaRelayHost,  type: _COS_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_zimbraMtaMyNetworks, ref:"attrs/" +  ZaServer.A_zimbraMtaMyNetworks, type:_COS_STRING_, maxLength: 10240 },
		{id:ZaServer.A_zimbraMtaDnsLookupsEnabled, ref:"attrs/" +  ZaServer.A_zimbraMtaDnsLookupsEnabled, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
		//milter server
		{id:ZaServer.A_zimbraMilterBindAddress, ref:"attrs/" +  ZaServer.A_zimbraMilterBindAddress, type:_LIST_, listItem:{type:_HOSTNAME_OR_IP_, maxLength: 128} },
		{id:ZaServer.A_zimbraMilterBindPort, ref:"attrs/" +  ZaServer.A_zimbraMilterBindPort, type:_COS_PORT_},
		{id:ZaServer.A_zimbraMilterServerEnabled, ref:"attrs/" +  ZaServer.A_zimbraMilterServerEnabled, type: _COS_ENUM_, choices: ZaModel.BOOLEAN_CHOICES },
        //spnego
		{id:ZaServer.A_zimbraSpnegoAuthTargetName, ref:"attrs/" +  ZaServer.A_zimbraSpnegoAuthTargetName, type:_STRING_},
        {id:ZaServer.A_zimbraSpnegoAuthPrincipal, ref:"attrs/" +  ZaServer.A_zimbraSpnegoAuthPrincipal, type:_STRING_},
        {id:ZaServer.A_zimbraMailSSLClientCertMode, ref:"attrs/" +  ZaServer.A_zimbraMailSSLClientCertMode, type:_COS_STRING_, choices:["Disabled","NeedClientAuth","WantClientAuth"]},
        {id:ZaServer.A_zimbraMailSSLClientCertPort, ref:"attrs/" +  ZaServer.A_zimbraMailSSLClientCertPort, type:_COS_PORT_},
        {id:ZaServer.A_zimbraMailSSLProxyClientCertPort, ref:"attrs/" +  ZaServer.A_zimbraMailSSLProxyClientCertPort, type:_COS_PORT_},
        {id:ZaServer.A_zimbraReverseProxyMailMode, ref:"attrs/" +  ZaServer.A_zimbraReverseProxyMailMode, type:_COS_STRING_, choices:["http","https","both","mixed","redirect"]},
        {id:ZaServer.A_zimbraReverseProxyClientCertMode, ref:"attrs/" +  ZaServer.A_zimbraReverseProxyClientCertMode, type:_COS_STRING_, choices:["on","off","optional"]},
        {id:ZaServer.A_zimbraReverseProxyClientCertCA, ref:"attrs/" + ZaServer.A_zimbraReverseProxyClientCertCA, type:_STRING_},
		// ...other...
		{id:ZaServer.A_zimbraSmtpHostname, ref:"attrs/" +  ZaServer.A_zimbraSmtpHostname, type:_COS_LIST_, listItem:{type:_HOSTNAME_OR_IP_, maxLength: 256} },
		{id:ZaServer.A_SmtpPort, ref:"attrs/" +  ZaServer.A_SmtpPort, type:_COS_PORT_},
		{id:ZaServer.A_SmtpTimeout, ref:"attrs/" + ZaServer.A_SmtpTimeout, type:_COS_NUMBER_, minInclusive: 0, maxInclusive:2147483647 },
		{id:ZaServer.A_LmtpAdvertisedName, ref:"attrs/" +  ZaServer.A_LmtpAdvertisedName, type:_STRING_, maxLength: 128 },
		{id:ZaServer.A_LmtpBindAddress, ref:"attrs/" +  ZaServer.A_LmtpBindAddress, type:_HOSTNAME_OR_IP_, maxLength: 256 },
		{id:ZaServer.A_LmtpBindPort, ref:"attrs/" +  ZaServer.A_LmtpBindPort, type:_COS_PORT_},		
		{id:ZaServer.A_zimbraScheduledTaskNumThreads, ref:"attrs/" +  ZaServer.A_zimbraScheduledTaskNumThreads, type:_COS_INT_, minInclusive: 1, maxInclusive:2147483647 },
		{id:ZaServer.A_zimbraMailPurgeSleepInterval, ref:"attrs/" +  ZaServer.A_zimbraMailPurgeSleepInterval, type:_COS_MLIFETIME_, minInclusive: 0, maxInclusive:2147483647 },
		{id:ZaServer.A_zimbraPop3NumThreads, ref:"attrs/" +  ZaServer.A_zimbraPop3NumThreads, type:_COS_INT_, minInclusive: 0, maxInclusive:2147483647 },		
		{id:ZaServer.A_zimbraImapNumThreads, ref:"attrs/" +  ZaServer.A_zimbraImapNumThreads, type:_COS_INT_, minInclusive: 0, maxInclusive:2147483647 },		
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
		
		//ip address bindings
		{id:ZaServer.A_zimbraMailBindAddress, ref:"attrs/" +  ZaServer.A_zimbraMailBindAddress, type:_IP_},
		{id:ZaServer.A_zimbraMailSSLBindAddress, ref:"attrs/" +  ZaServer.A_zimbraMailSSLBindAddress, type:_IP_ },
		{id:ZaServer.A_zimbraMailSSLClientCertBindAddress, ref:"attrs/" +  ZaServer.A_zimbraMailSSLClientCertBindAddress, type:_IP_ },
		{id:ZaServer.A_zimbraAdminBindAddress, ref:"attrs/" +  ZaServer.A_zimbraAdminBindAddress, type:_IP_ },

        // auto provision
        {id:ZaServer.A_zimbraAutoProvPollingInterval, ref:"attrs/" + ZaServer.A_zimbraAutoProvPollingInterval, type: _COS_MLIFETIME_, minInclusive: 0 },
        {id:ZaServer.A_zimbraAutoProvScheduledDomains, ref:"attrs/" +  ZaServer.A_zimbraAutoProvScheduledDomains, type:_LIST_, listItem:{type:_STRING_, maxLength: 256} },
		{id:ZaServer.A_ImapCleartextLoginEnabled, ref:"attrs/" + ZaServer.A_ImapCleartextLoginEnabled, type:_COS_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_RedologEnabled, ref:"attrs/" + ZaServer.A_RedologEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},		
		{id:ZaServer.A_RedologLogPath, ref:"attrs/" + ZaServer.A_RedologLogPath, type:_STRING_},		
		{id:ZaServer.A_RedologArchiveDir, ref:"attrs/" + ZaServer.A_RedologArchiveDir, type:_STRING_},		
		{id:ZaServer.A_RedologBacklogDir, ref:"attrs/" + ZaServer.A_RedologBacklogDir, type:_STRING_},		
		{id:ZaServer.A_RedologRolloverFileSizeKB, ref:"attrs/" + ZaServer.A_RedologRolloverFileSizeKB, type:_NUMBER_, minInclusive: 0, maxInclusive:2147483647 },
		{id:ZaServer.A_RedologFsyncIntervalMS, ref:"attrs/" + ZaServer.A_RedologFsyncIntervalMS, type:_NUMBER_, minInclusive: 0, maxInclusive:2147483647 },
		{id:ZaServer.A_MasterRedologClientConnections, ref:"attrs/" + ZaServer.A_MasterRedologClientConnections, type:_STRING_},		
		{id:ZaServer.A_MasterRedologClientTimeoutSec, ref:"attrs/" + ZaServer.A_MasterRedologClientTimeoutSec, type:_STRING_},		
		{id:ZaServer.A_MasterRedologClientTcpNoDelay, ref:"attrs/" + ZaServer.A_MasterRedologClientTcpNoDelay, type:_STRING_},		
		{id:ZaServer.A_zimbraUserServicesEnabled, ref:"attrs/" + ZaServer.A_zimbraUserServicesEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
		//volumes
		{id:ZaServer.A_Volumes,ref:ZaServer.A_Volumes, type:_LIST_, listItem:ZaServer.volumeObjModel},
		{id:ZaServer.A_showVolumes, ref:ZaServer.A_showVolumes, type: _ENUM_, choices: [false,true]},
		{id:ZaServer.A2_volume_selection_cache, ref:ZaServer.A2_volume_selection_cache, type:_LIST_},
		{id:ZaServer.A_CurrentIndexVolumeId, ref:ZaServer.A_CurrentIndexVolumeId, type:_NUMBER_},
		{id:ZaServer.A_CurrentMsgVolumeId, ref:ZaServer.A_CurrentMsgVolumeId, type:_NUMBER_},
		//VAMI update
		{id:ZaServer.A_zimbraApplianceVendor, ref:"attrs/" +  ZaServer.A_zimbraApplianceVendor, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceName, ref:"attrs/" +  ZaServer.A_zimbraApplianceName, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceVersion, ref:"attrs/" +  ZaServer.A_zimbraApplianceVersion, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceFullVersion, ref:"attrs/" +  ZaServer.A_zimbraApplianceFullVersion, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceDetails, ref:"attrs/" +  ZaServer.A_zimbraApplianceDetails, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceDefaultRepUrl, ref:"attrs/" +  ZaServer.A_zimbraApplianceDefaultRepUrl, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceCustomRepUrl, ref:"attrs/" +  ZaServer.A_zimbraApplianceCustomRepUrl, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceCustomRepUrlUser, ref:"attrs/" +  ZaServer.A_zimbraApplianceCustomRepUrlUser, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceCustomRepUrlPass, ref:"attrs/" +  ZaServer.A_zimbraApplianceCustomRepUrlPass, type:_STRING_, maxLength: 256 },
		{id:ZaServer.A_zimbraApplianceUpdateOption, ref:"attrs/" +  ZaServer.A_zimbraApplianceUpdateOption, type:_LIST_},
		{id:ZaServer.A_zimbraApplianceUpdateType, ref:"attrs/" +  ZaServer.A_zimbraApplianceUpdateType, type:_LIST_},
		{id:ZaServer.A_zimbraApplianceUpdatehourofrun, ref:"attrs/" +  ZaServer.A_zimbraApplianceUpdatehourofrun, type:_LIST_}
    ]
};
		
ZaServer.prototype.toString = function() {
	return this.name;
}

ZaServer.getServerByName = 
function(serverName) {
	if(!serverName)
		return null;
	var server = ZaServer.staticServerByNameCacheTable[serverName];
	if(!server) {
		domain = new ZaServer();
		try {
			server.load("name", serverName, false, true);
		} catch (ex) {
            throw (ex);
        }

		ZaServer.putServeToCache(server);
	} 
	return server;	
} 

ZaServer.getServerById = 
function (serverId) {
	if(!serverId)
		return null;
		
	var server = ZaServer.staticServerByIdCacheTable[serverId];
	if(!server) {
		server = new ZaServer();
		try {
			server.load("id", serverId, false, true);
		} catch (ex) {
			throw (ex);
		}
		ZaServer.putServeToCache(server);
	}
	return server;
}

ZaServer.getAllMBSs =
function(attrs) {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", ZaZimbraAdmin.URN, null);	
	soapDoc.getMethod().setAttribute("service", "mailbox");
	soapDoc.getMethod().setAttribute("applyConfig", "false");
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;
	if(attrs) {
		soapDoc.setMethodAttribute("attrs", attrs.join(","));
	}	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaServer);
	list.loadFromJS(resp);	
	return list;
}

ZaServer.getAll =
function(attrs) {
	var soapDoc = AjxSoapDoc.create("GetAllServersRequest", ZaZimbraAdmin.URN, null);	
	soapDoc.getMethod().setAttribute("applyConfig", "false");
//	var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	params.asyncMode=false;
	if(attrs) {
		soapDoc.setMethodAttribute("attrs", attrs.join(","));
	}	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_ALL_SERVER
	}
	var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.GetAllServersResponse;	
	var list = new ZaItemList(ZaServer);
	list.loadFromJS(resp);	
	return list;
}

ZaServer.modifyMethod = function (tmpObj) {
	if(tmpObj.attrs == null) {
		//show error msg
		//ZaApp.getInstance().getCurrentController()._errorDialog.setMessage(ZaMsg.ERROR_UNKNOWN, null, DwtMessageDialog.CRITICAL_STYLE, null);
		//ZaApp.getInstance().getCurrentController()._errorDialog.popup();		
		return;	
	}
	
	if(ZaItem.hasWritePermission(ZaServer.A_zimbraServiceEnabled,tmpObj)) {
		// update zimbraServiceEnabled
		var svcInstalled = AjxUtil.isString(tmpObj.attrs[ZaServer.A_zimbraServiceInstalled])
								? [ tmpObj.attrs[ZaServer.A_zimbraServiceInstalled] ]
								: tmpObj.attrs[ZaServer.A_zimbraServiceInstalled];
		if (svcInstalled) {
			// get list of actually enabled fields
			var enabled = [];
			for (var i = 0; i < svcInstalled.length; i++) {
				var service = svcInstalled[i];
				if (service == "vmware-ha") {
					if (tmpObj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+"vmwareha"]) 
						enabled.push("vmware-ha");
					continue;
				}
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
						if (service == "vmware-ha") {
							if(!tmpObj.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+"vmware"]){
								dirty = true;
								break;
							} else {
								continue;
							}

						}
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
	}
	//modify volumes
	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled] && ZaItem.hasRight(ZaServer.MANAGE_VOLUME_RIGHT,this)) {
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
	
	var hasSomething = false;	
	//create a ModifyServerRequest SOAP request
	var soapDoc = AjxSoapDoc.create("ModifyServerRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", this.id);
	//get the list of changed fields
	var mods = new Object();
	for (var a in tmpObj.attrs) {
		if(a == ZaItem.A_objectClass || /^_/.test(a) || a == ZaServer.A_zimbraServiceInstalled
                || a == ZaItem.A_zimbraACE)
			continue;
		
		if(!ZaItem.hasWritePermission(a,this)) {
			continue;
		}
		
		hasSomething = true;
		if (this.attrs[a] != tmpObj.attrs[a] ) {
			if(tmpObj.attrs[a] instanceof Array) {
				if (!this.attrs[a]) {
					this.attrs[a] = [];
				}

				if (! this.attrs[a] instanceof Array) {
					this.attrs[a] = [this.attrs[a]];
				}

				if (tmpObj.attrs[a].join(",").valueOf() !=  this.attrs[a].join(",").valueOf()) {
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
				}	
			} else {
				var attr = soapDoc.set("a", tmpObj.attrs[a]);
				attr.setAttribute("n", a);
			}
		}
	}
	if(hasSomething) {
		//modify the server
		var params = new Object();
		params.soapDoc = soapDoc;	
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_SERVER
		}
		var resp = ZaRequestMgr.invoke(params, reqMgrParams).Body.ModifyServerResponse;		
		this.initFromJS(resp.server[0]);		
	}
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

ZaServer.loadMethod = 
function(by, val) {
	var _by = by ? by : "id";
	var _val = val ? val : this.id
	var soapDoc = AjxSoapDoc.create("GetServerRequest", ZaZimbraAdmin.URN, null);
	var elBy = soapDoc.set("server", _val);
	elBy.setAttribute("by", _by);
	soapDoc.setMethodAttribute("applyConfig", "false");
	if(!this.getAttrs.all && !AjxUtil.isEmpty(this.attrsToGet)) {
		soapDoc.setMethodAttribute("attrs", this.attrsToGet.join(","));
	}	
	
	//var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;	
	params.asyncMode = false;
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_GET_SERVER
	}
	resp = ZaRequestMgr.invoke(params, reqMgrParams);		
	this.initFromJS(resp.Body.GetServerResponse.server[0]);
	
	//this._defaultValues = ZaApp.getInstance().getGlobalConfig();
	soapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	soapDoc.setMethodAttribute("onerror", "continue");	
	
	if(this.attrs[ZaServer.A_zimbraMailboxServiceEnabled] && ZaItem.hasRight(ZaServer.MANAGE_VOLUME_RIGHT,this)) {
		var getAllVols = soapDoc.set("GetAllVolumesRequest", null, null, ZaZimbraAdmin.URN);
		var getCurrentVols = soapDoc.set("GetCurrentVolumesRequest", null, null, ZaZimbraAdmin.URN);
	}				
	var getAllVols = soapDoc.set("GetServerNIfsRequest", null, null, ZaZimbraAdmin.URN);
	var server = soapDoc.set("server", _val, getAllVols);
	server.setAttribute("by", _by);
	try {
		params = new Object();
		params.soapDoc = soapDoc;	
		params.asyncMode = false;
		if(this.attrs && this.attrs[ZaServer.A_zimbraMailboxServiceInstalled] && this.attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
			params.targetServer = this.id;
		}
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_GET_SERVER
		}
		
		var respObj = ZaRequestMgr.invoke(params, reqMgrParams);
		this[ZaServer.A_Volumes] = new Array();
		
		if(respObj.isException && respObj.isException()) {
			ZaApp.getInstance().getCurrentController()._handleException(respObj.getException(), "ZaServer.loadMethod", null, false);
		} 
		if (respObj.Body.BatchResponse) {
			if(respObj.Body.BatchResponse.Fault) {
				var fault = respObj.Body.BatchResponse.Fault;
				if(fault instanceof Array)
					fault = fault[0];
			
				if (fault) {
					// JS response with fault
					var ex = ZmCsfeCommand.faultToEx(fault);
					ZaApp.getInstance().getCurrentController()._handleException(ex,"ZaServer.loadMethod", null, false);
				}
			} 
		
			var batchResp = respObj.Body.BatchResponse;
			if(batchResp.GetAllVolumesResponse) {
				resp = batchResp.GetAllVolumesResponse[0];
				this.parseMyVolumes(resp);
			}
				
			if(batchResp.GetCurrentVolumesResponse) {
				resp = batchResp.GetCurrentVolumesResponse[0];
				this.parseCurrentVolumesResponse(resp);
			}
				
			if(batchResp.GetServerNIfsResponse) {
				resp = batchResp.GetServerNIfsResponse[0];
				this.parseNIFsResponse(resp);
			}
		}
	} catch (ex) {
		//show the error and go on
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaServer.loadMethod", null, false);
	}		
}

ZaItem.loadMethods["ZaServer"].push(ZaServer.loadMethod);

ZaServer.prototype.parseNIFsResponse = 
function(resp) {
	if(resp && resp.ni) {
		var NIs = resp.ni;
		var cnt = NIs.length;
		this.nifs = [];
		for(var i=0;i<cnt;i++) {
			var ni = {};
			ZaItem.prototype.initFromJS.call(ni, NIs[i]);
			this.nifs.push(ni);
		}
	}
}
//ZaItem.loadMethods["ZaServer"].push(ZaServer.loadNIFS);

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
			if(service == "vmware-ha"){
				this.attrs["_"+ZaServer.A_zimbraServiceInstalled+"_"+"vmwareha"] = true;
                        	this.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+"vmwareha"] = false;
				continue;
			}
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
			if (service == "vmware-ha") {
				this.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+"vmwareha"] = true;
				continue;
			}

			this.attrs["_"+ZaServer.A_zimbraServiceEnabled+"_"+service] = true;
		}
	}
	this[ZaServer.A_ServiceHostname] = this.attrs[ZaServer.A_ServiceHostname]; // a hack for New Account Wizard	
	this[ZaServer.A_showVolumes] = this.attrs[ZaServer.A_zimbraMailboxServiceEnabled];
	if(this.attrs[ZaServer.A_zimbraSmtpHostname] && !(this.attrs[ZaServer.A_zimbraSmtpHostname] instanceof Array)) {
		this.attrs[ZaServer.A_zimbraSmtpHostname] = [this.attrs[ZaServer.A_zimbraSmtpHostname]];
	}
	
	if(this._defaultValues && this._defaultValues.attrs[ZaServer.A_zimbraSmtpHostname] && !(this._defaultValues.attrs[ZaServer.A_zimbraSmtpHostname]  instanceof Array)) {
		this._defaultValues.attrs[ZaServer.A_zimbraSmtpHostname]  = [this._defaultValues.attrs[ZaServer.A_zimbraSmtpHostname]];
	}
	
	if(this.attrs[ZaServer.A_zimbraMilterBindAddress] && !(this.attrs[ZaServer.A_zimbraMilterBindAddress] instanceof Array)) {
                this.attrs[ZaServer.A_zimbraMilterBindAddress] = [this.attrs[ZaServer.A_zimbraMilterBindAddress]];
        }

        if(this._defaultValues && this._defaultValues.attrs[ZaServer.A_zimbraMilterBindAddress] && !(this._defaultValues.attrs[ZaServer.A_zimbraMilterBindAddress]  instanceof Array)) {
                this._defaultValues.attrs[ZaServer.A_zimbraMilterBindAddress]  = [this._defaultValues.attrs[ZaServer.A_zimbraMilterBindAddress]];
        }

	if(this.attrs[ZaServer.A_zimbraAutoProvScheduledDomains] && !(this.attrs[ZaServer.A_zimbraAutoProvScheduledDomains] instanceof Array)) {
        this.attrs[ZaServer.A_zimbraAutoProvScheduledDomains] = [this.attrs[ZaServer.A_zimbraAutoProvScheduledDomains]];
    }

}

ZaServer.prototype.parseCurrentVolumesResponse =
function (resp) {
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

ZaServer.prototype.parseMyVolumes = 
function(resp) {
	var volumes = resp.volume;
	if(volumes) {
		var cnt = volumes.length;
		for (var i=0; i< cnt;  i++) {
			this[ZaServer.A_Volumes].push(volumes[i]);	
		}
	}
}

ZaServer.compareVolumesByName = function (a,b) {
	
	if(a[ZaServer.A_VolumeName]>b[ZaServer.A_VolumeName])
		return 1;
	if(a[ZaServer.A_VolumeName]<b[ZaServer.A_VolumeName])
		return -1;
	return 0;
	
}

ZaServer.prototype.loadVolumes = function (callback) {
	var soapDoc = AjxSoapDoc.create("GetAllVolumesRequest", ZaZimbraAdmin.URN, null);
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: callback ? true : false,
		callback: callback ? callback : null		
	}
	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_LOADING_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.prototype.deleteVolume =
function (id, callback) {
	if(!id)
		return false;
		
	var soapDoc = AjxSoapDoc.create("DeleteVolumeRequest", ZaZimbraAdmin.URN, null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: callback ? true : false,
		callback: callback ? callback : null		
	}
	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_DELETE_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.prototype.createVolume =
function (volume) {
	if(!volume)
		return false;
	var soapDoc = AjxSoapDoc.create("CreateVolumeRequest", ZaZimbraAdmin.URN, null);		
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
		controller : ZaApp.getInstance().getCurrentController(),
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
	var soapDoc = AjxSoapDoc.create("ModifyVolumeRequest", ZaZimbraAdmin.URN, null);		
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
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_MODIFY_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.prototype.setCurrentVolume = function (id, type) {
	if(!id || !type)
		return false;	
	var soapDoc = AjxSoapDoc.create("SetCurrentVolumeRequest", ZaZimbraAdmin.URN, null);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeType, type);		
	soapDoc.getMethod().setAttribute(ZaServer.A_VolumeId, id);	
	var params = {
		soapDoc: soapDoc,
		targetServer: this.id,
		asyncMode: false
	}
	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : ZaMsg.BUSY_SET_VOL
	}
	ZaRequestMgr.invoke(params, reqMgrParams) ;
}

ZaServer.initMethod = function () {
	this.attrs = new Object();
	this.id = "";
	this.name="";
}
ZaItem.initMethods["ZaServer"].push(ZaServer.initMethod);

ZaServer.flushCache = function (params) {
	var soapDoc = AjxSoapDoc.create("FlushCacheRequest", ZaZimbraAdmin.URN, null);
	var elCache = soapDoc.set("cache", null);
	
	var type = [];
	if(params.flushSkin)
		type.push("skin")
	if(params.flushLocale)	
		type.push("locale");
	if(params.flushZimlet)	
		type.push("zimlet");
		
	elCache.setAttribute("type", type.join(","));		
	
	var reqMgrParams = {
		controller : ZaApp.getInstance().getCurrentController(),
		busyMsg : params.busyMsg ? params.busyMsg : ZaMsg.BUSY_FLUSH_CACHE,
		busyId:params.busyId
	}
	
	var reqParams = {
		soapDoc: soapDoc,
		targetServer: params.serverId ? params.serverId : params.serverList[params.ix].attrs[ZaItem.A_zimbraId],
		asyncMode: params.callback ? true : false,
		callback: params.callback ? params.callback : null
	}
	ZaRequestMgr.invoke(reqParams, reqMgrParams) ;
}
