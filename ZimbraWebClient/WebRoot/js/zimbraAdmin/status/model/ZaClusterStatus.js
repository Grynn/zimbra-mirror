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
**/
function ZaClusterStatus(app) {
	ZaItem.call(this, ZaEvent.S_CLUSTER_STATUS);
}

ZaClusterStatus.prototype = new ZaItem;
ZaClusterStatus.prototype.constructor = ZaClusterStatus;

ZaClusterStatus.getStatus = function() {
	var soapDoc = AjxSoapDoc.create("GetClusterStatusRequest", "urn:zimbraAdmin", null);
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetClusterStatusResponse;
	var status = new ZaClusterStatus();
	status.initFromDom(resp);
	return status;
};

ZaClusterStatus.prototype.initFromDom = function (node) {
	var i = 0;
	this.servers = {};
	this.services = {};
	for (i = 0; i < node.servers[0].server.length ; ++i) {
		this.servers[node.servers[0].server[i].name] = node.servers[0].server[i].status;
	}
	for (i = 0; i < node.services[0].service.length ; ++i) {
		this.services[node.services[0].service[i].owner] = node.services[0].service[i];
	}
};
