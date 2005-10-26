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

}

ZaClusterStatus._clusters = {};

ZaClusterStatus.getStatus = function() {
	var soapDoc = AjxSoapDoc.create("GetClusterStatusRequest", "urn:zimbraAdmin", null);
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetClusterStatusResponse;
	return new ZaCluster()._initFromDom(resp);
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
};


ZaClusterStatus.mergeWithZimbraServiceStatus = function (statusVector) {
	var zStatusMap = {};
	var arr = statusVector.getArray();
	var i;
	var tmp;
	for (i = 0 ; i < arr.length; ++i) {
		tmp = zStatusMap[arr[i].serverName];
		if (tmp == null) {
			zStatusMap[arr[i].serverName] = [arr[i]];
		} else {
			tmp.push(arr[i]);
		}
	}

	var cluster;
	var clusterStatusVector = new AjxVector();
	DBG.dumpObj(ZaClusterStatus._clusters);
	for (cn in ZaClusterStatus._clusters){
		cluster = ZaClusterStatus._clusters[cn];
		arr = cluster._serviceArray;

		var len = arr.length;
		var prev = null;
		// asign cluster status to each server, if the server is
		// listed in the status array.
		//
		// iterate through the status array, and lookup the status server name 
		// in the services object.
		var s, zStatusArr, zStatus, j;
		for (i = 0 ; i < len ; ++i) {
			s = new ZaServerStatus(arr[i].name);
			s.clustered = true;
			s.serverName = arr[i].name;
			s.clusterStatus = arr[i].status;
			s.physicalServerName = arr[i].owner;
			s.clusterName = cluster.name;
			zStatusArr = zStatusMap[arr[i].name];
			if (zStatusArr != null) {
				for (j = 0 ; j < zStatusArr.length; ++j) {
					zStatus = zStatusArr[j];
					zStatus.__seen = true;
					s.services.push(new ZaServiceStatus(zStatus.serviceName, zStatus.time, zStatus.status));
				}
			}
			clusterStatusVector.add(s);
		}

		arr = statusVector.getArray();
		for (i = 0 ; i < arr.length; ++i) {
			if (!arr[i].__seen) {
				delete arr[i].__seen;
				s = new ZaServerStatus(arr[i].serverName);
				s.clustered = false;
				s.physicalServerName = arr[i].serverName;
				s.services = [new ZaServiceStatus(arr[i].serviceName, arr[i].time, arr[i].status)];
				clusterStatusVector.add(s);
			}
		}
		var s, sN;	
		var vectorNeedsSort = false;
		// make a pass through the servers, and find the ones that are not
		// used, and add them to the statusVector.
		for (sN in cluster._servers) {
			if (!cluster._isServerInUse(sN)){
				var st = new ZaServerStatus(cluster._servers[sN].name, true, "stopped","(not assigned)");
				clusterStatusVector.add(st);
				vectorNeedsSort = true;
			}
		}
	}

	// sort the vector, if we've changed anything.
	if ( vectorNeedsSort ) {
		statusVector.sort(ZaClusterStatus.compare);
	}
	return clusterStatusVector;
};

ZaClusterStatus.compare = function (a,b){
	return (a.physicalServerName < b.physicalServerName)? -1: ((a.physicalServerName > b.physicalServerName)? 1: 0);
};

ZaClusterStatus._isServerInUse = function (serverName) {
	return (ZaClusterStatus._usedServers[serverName] != null)? true: false;
};

ZaClusterStatus.NOT_APPLICABLE = "N/A";
function ZaServerStatus(physicalServerName, clustered, clusterStatus, name, serviceArr) {
	this.serverName = (name !== (void 0))? name: ZaClusterStatus.NOT_APPLICABLE;
	this.clustered = clustered;
	this.clusterStatus = clusterStatus;
	this.physicalServerName = (physicalServerName !== (void 0))? physicalServerName: ZaClusterStatus.NOT_APPLICABLE;
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

function ZaCluster () {}

ZaCluster.prototype._initFromDom = function (node) {
	if (node.servers != null) {
		this.name = node.clusterName[0]._content;
		ZaClusterStatus._clusters[this.name] = this;
		var i = 0;
		this._servers = {};
		this._services = {};
		this._serviceArray = [];
		this._usedServers = {};
		this._serverArray = [];
		for (i = 0; i < node.servers[0].server.length ; ++i) {
			this._servers[node.servers[0].server[i].name] = node.servers[0].server[i];
			this._serverArray.push(node.servers[0].server[i]);
		}
		for (i = 0; i < node.services[0].service.length ; ++i) {
			this._services[node.services[0].service[i].name] = node.services[0].service[i];
			this._serviceArray.push(node.services[0].service[i]);
			this._usedServers[node.services[0].service[i].owner] = true;
		}
	}
};

ZaCluster.prototype._isServerInUse = function (serverName) {
	return (this._usedServers[serverName] != null)? true: false;
};

