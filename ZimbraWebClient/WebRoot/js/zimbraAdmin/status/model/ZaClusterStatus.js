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
	//	ZaStatus.call(this, ZaEvent.S_CLUSTER_STATUS);
}

// ZaClusterStatus.prototype = new ZaStatus;
// ZaClusterStatus.prototype.constructor = ZaClusterStatus;

ZaClusterStatus.getStatus = function() {
	var soapDoc = AjxSoapDoc.create("GetClusterStatusRequest", "urn:zimbraAdmin", null);
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetClusterStatusResponse;
	ZaClusterStatus._initFromDom(resp);
};

ZaClusterStatus.getCombinedStatus = function (serviceVector) {
	var cStatus = ZaClusterStatus.getStatus();
 	var vec = ZaClusterStatus.mergeWithZimbraServiceStatus(serviceVector);
 	return vec;	
};

ZaClusterStatus.getServerList = function (refresh) {
	if (refresh || ZaClusterStatus._servers == null) {
		ZaClusterStatus.getStatus();
	}
	return ZaClusterStatus._serverArray;
}

ZaClusterStatus._initFromDom = function (node) {
	if (node.servers != null) {
		var i = 0;
		ZaClusterStatus._servers = {};
		ZaClusterStatus._services = {};
		ZaClusterStatus._usedServers = {};
		ZaClusterStatus._serverArray = [];
		for (i = 0; i < node.servers[0].server.length ; ++i) {
			ZaClusterStatus._servers[node.servers[0].server[i].name] = node.servers[0].server[i];
			ZaClusterStatus._serverArray.push(node.servers[0].server[i]);
		}
		for (i = 0; i < node.services[0].service.length ; ++i) {
			ZaClusterStatus._services[node.services[0].service[i].name] = node.services[0].service[i];
			ZaClusterStatus._usedServers[node.services[0].service[i].owner] = true;
		}
	}
};

ZaClusterStatus.mergeWithZimbraServiceStatus = function (statusVector) {
	var arr = statusVector.getArray();
	var len = statusVector.size();
	var i;
	var clusterStatusVector = new AjxVector();
	var prev = null;
	// asign cluster status to each server, if the server is
	// listed in the status array.
	//
	// iterate through the status array, and lookup the status server name 
	// in the services object.
	var s;
	for (i = 0 ; i < len ; ++i) {
		if (prev == null || prev != arr[i].serverName){
			s = new ZaServerStatus(arr[i].serverName);
			var clusterSt = ZaClusterStatus._services[arr[i].serverName];
			s.clustered = false;
			s.clusterStatus = null;
			if (clusterSt != null) {
				s.clustered = true;
				s.clusterStatus = clusterSt.status;
				s.physicalServerName = clusterSt.owner;
			}
			s.services.push(new ZaServiceStatus(arr[i].serviceName, arr[i].time, arr[i].status));
			clusterStatusVector.add(s);
		} else {
			s.services.push(new ZaServiceStatus(arr[i].serviceName, arr[i].time, arr[i].status));
		}
		prev = arr[i].serverName;
	}

	var s, sN;	
	var vectorNeedsSort = false;
	// make a pass through the servers, and find the ones that are not
	// used, and add them to the statusVector.
	for (sN in ZaClusterStatus._servers) {
		if (!ZaClusterStatus._isServerInUse(sN)){
			var st = new ZaServerStatus(ZaClusterStatus._servers[sN].name, true, "stopped");
			clusterStatusVector.add(st);
			vectorNeedsSort = true;
		}
	}

	// sort the vector, if we've changed anything.
	if ( vectorNeedsSort ) {
		statusVector.sort(ZaStatus.compare);
	}
	DBG.println("clusterdStatusVector = " , clusterStatusVector._array);
	return clusterStatusVector;
};

ZaClusterStatus._isServerInUse = function (serverName) {
	return (ZaClusterStatus._usedServers[serverName] != null)? true: false;
};

function ZaServerStatus(name, clustered, clusterStatus, physicalServerName, serviceArr) {
	this.serverName = name;
	this.clustered = clustered;
	this.clusterStatus = clusterStatus;
	this.physicalServerName = (physicalServerName !== (void 0))? physicalServerName: "N/A";
	this.services = [];
	var i, t;
	if (serviceArr != null) {
		for (i = 0 ; i < serviceArr.length; ++i) {
			t = serviceArr[i];
			this.services.push( new ZaServiceStatus(t.name, t.time, t.status));
		}
	}
}

ZaServerStatus.prototype.toString = function () {
	var buf = new AjxBuffer();
	buf.append("[", this.serverName, "]\n");
	for (var i = 0; i < this.services.length; ++i) {
		buf.append(this.services[i].toString());
	}
	return buf.toString();
}

function ZaServiceStatus(name, lastCheckedTime, status) {
	this.serviceName = name;
	this.time = lastCheckedTime;
	this.status = status;
};

ZaServiceStatus.prototype.toString = function () {
	return AjxBuffer.concat("{", this.serviceName, ",", this.time, ",", this.status, "}");
};
