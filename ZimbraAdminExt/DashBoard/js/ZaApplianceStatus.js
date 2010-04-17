/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaApplianceStatus 
* @contructor ZaApplianceStatus
* @param app
* @author Greg Solovyev
**/
ZaApplianceStatus = function() {
	ZaItem.call(this,"ZaApplianceStatus");
	this._init();	
}

ZaApplianceStatus.prototype = new ZaItem;
ZaApplianceStatus.prototype.constructor = ZaApplianceStatus;
ZaItem.loadMethods["ZaApplianceStatus"] = new Array();
ZaItem.initMethods["ZaApplianceStatus"] = new Array();

ZaApplianceStatus.SVC_SPELL = "spell";
ZaApplianceStatus.SVC_LOGGER = "logger";
ZaApplianceStatus.SVC_MTA = "mta";
ZaApplianceStatus.SVC_LDAP = "ldap";
ZaApplianceStatus.SVC_MEMCACHED = "memcached";
ZaApplianceStatus.SVC_MAILBOX = "mailbox";
ZaApplianceStatus.SVC_CONVERTD = "convertd";
ZaApplianceStatus.SVC_IMAPPROXY = "imapproxy";
ZaApplianceStatus.SVC_STATS = "stats";
ZaApplianceStatus.SVC_SNMP = "snmp";
ZaApplianceStatus.SVC_AS = "antispam";
ZaApplianceStatus.SVC_AV = "antivirus";


ZaApplianceStatus.prototype.load = function (by, val) {
	ZaItem.prototype.load.call(this,by,val,true, false);
}

ZaApplianceStatus.loadMethod = 
function() {
	try {
		this.serviceMap = {};
	    var statusURL = "https://localhost:5480/cgi-bin/getStatus.pl?stype=service";
	    var url = "/service/proxy?target=" + AjxStringUtil.urlComponentEncode(statusURL);
	    var busyId = Dwt.getNextId();
	    DwtShell.getShell(window).setBusyDialogText(com_zimbra_dashboard.BUSY_REQUESTING_STATUS)
	    DwtShell.getShell(window).setBusy(true,busyId, true, 50);	    
	    var response = AjxRpc.invoke(null, url, null, null, true);
	    var myDoc = AjxXmlDoc.createFromDom(response.xml);
	    var serviceNodes = myDoc.getElementsByTagName("service");
	    var cnt = serviceNodes.length;
	    for(var i = 0; i< cnt; i++) {
	    	this.serviceMap[serviceNodes[i].getAttribute("name")] = (serviceNodes[i].getAttribute("status") == "Running"); 
	    }
	    DwtShell.getShell(window).setBusy(false,busyId,false);
	} catch (ex) {
		ZaApp.getInstance().getStatusViewController()._handleException(ex, "ZaApplianceStatus.loadMethod", null, false);		
	}	
}

ZaItem.loadMethods["ZaApplianceStatus"].push(ZaApplianceStatus.loadMethod);

ZaApplianceStatus.initMethod = function () {
	this.serverMap = new Object();
	this.statusVector = new AjxVector();
	this.id = Dwt.getNextId();
}
ZaItem.initMethods["ZaApplianceStatus"].push(ZaApplianceStatus.initMethod);

ZaApplianceStatus.prototype.getStatusVector = 
function() {
	return this.statusVector;
}

ZaApplianceStatus.compare = function (a,b) {
	return (a.serverName < b.serverName)? -1: ((a.serverName > b.serverName)? 1: 0);
};
