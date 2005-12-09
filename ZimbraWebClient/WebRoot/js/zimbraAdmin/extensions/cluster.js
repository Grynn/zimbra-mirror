function ZaCluster () {

};
ZaCluster.PRFX_Node = "status_node";
ZaCluster.PRFX_clusterName = "status_clustername";
if(ZaGlobalConfig) {
	ZaGlobalConfig.A_zimbraComponentAvailable_cluster = "_" + ZaGlobalConfig.A_zimbraComponentAvailable+"_cluster";
}

if(ZaItem.loadMethods["ZaStatus"]) {
	ZaCluster.clusterLoad = function () {
		if (this._globalConfig == null) {
			this._globalConfig = this._app.getGlobalConfig();
		}
		if(!AjxUtil.isSpecified(this._globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_cluster])) {
			return;
		}
		var soapDoc = AjxSoapDoc.create("GetClusterStatusRequest", "urn:zimbraAdmin", null);
		var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false).Body.GetClusterStatusResponse;
		var clusterName = 	resp.clusterName[0]._content;
		if(resp.servers && resp.servers[0] && resp.servers[0].server) {
			var servers = resp.servers[0].server;
			if(servers) {
				var cntMachines = servers.length;
				var machines = new Object();
				for(var i = 0; i < cntMachines; i++) {
					var name = servers[i].name;
					if(!name)
						continue;
						
					machines[name] = new Object();
					machines[name].name = servers[i].name;
					machines[name].status = servers[i].status;
					machines[name].used = false;				
				}
			}
		}
		if(resp.services && resp.services[0] && resp.services[0].service) {
			var services = resp.services[0].service;
			var cntServices = services.length;
			//add or update this.serverMap with logical servers received in <services>
			for(var i = 0; i < cntServices; i++) {
				var logicalServerName = services[i].name;
				var owner;
				if(services[i].owner && services[i].owner != "none") {
					owner = services[i].owner;
				} else if(services[i].lastOwner && services[i].lastOwner != "none") {
					owner = services[i].lastOwner;
				}
				if(owner && machines[owner]) {
					machines[owner].used = true;
				}
				if(!this.serverMap[logicalServerName]) {
					//if this logical server was not reported by GetServiceStatusRequest - add it to this.serverMap
					this.serverMap[logicalServerName] = new Object();
					this.serverMap[logicalServerName].serviceMap = null;
					this.serverMap[logicalServerName].status = 1;
					this.serverMap[logicalServerName].name = logicalServerName;
					this.statusVector.add(this.serverMap[logicalServerName]);
				}
				this.serverMap[logicalServerName].owner = owner;
				this.serverMap[logicalServerName].state = services[i].state;	
				this.serverMap[logicalServerName].restarts = services[i].restarts;		
				this.serverMap[logicalServerName].clusterName = clusterName;			
			}
			for(var ix in machines) {
				if(!machines[ix].used) {
					if(!this.serverMap[ix]) {
						this.serverMap[ix] = new Object();
						this.serverMap[ix].serviceMap = null;
						this.serverMap[ix].status = 2;
						this.serverMap[ix].state = ZaMsg.CSLV_standby;
						this.serverMap[ix].name = "";
						this.serverMap[ix].owner = ix;
						this.statusVector.add(this.serverMap[ix]);
					}
				}
			}
		}
	}
	ZaItem.loadMethods["ZaStatus"].push(ZaCluster.clusterLoad);
}


ZaServicesListView.prototype._getHeaderList =
function() {
	var headerList = [
		new ZaListHeaderItem(ZaStatus.PRFX_Server, ZaMsg.STV_Server_col, null, 250, false, null, true, true),
		new ZaListHeaderItem(ZaCluster.PRFX_Node, ZaMsg.CSLV_Node, null, 150, false, null, true, true),
	    new ZaListHeaderItem(ZaCluster.PRFX_clusterName, ZaMsg.CSLV_clusterName, null, 80, false, null, true, true),		
		new ZaListHeaderItem(ZaStatus.PRFX_Service, ZaMsg.STV_Service_col, null, 100, false, null, true, true),
		new ZaListHeaderItem(ZaStatus.PRFX_Time, ZaMsg.STV_Time_col, null, null, false, null, true, true)
	];
	return headerList;
}

ZaServicesListView._writeElement =
function(html, idx, item, onlyServiceInfo, serviceName) {
	html[idx++] = "<table ";
	if (onlyServiceInfo) {
		html[idx++] = "class='ZaServicesListView_table'";
	} else {
		html[idx++] = "class='ZaServicesListView_server_table'";
	}

	html[idx++] = "_serviceInfo=";
	html[idx++] = onlyServiceInfo;
	html[idx++] = ">";
	html[idx++] = "<tr>";
	var cnt = this._headerList.length;
	for(var i = 0; i < cnt; i++) {
		var id = this._headerList[i]._id;
		if(id.indexOf(ZaStatus.PRFX_Server)==0) {
			if (onlyServiceInfo) {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = " aligh=left>";
				html[idx++] = AjxStringUtil.htmlEncode(" ");
				html[idx++] = "</td>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = "><table cellpadding=0 cellspacing=0 border=0 style='table-layout:fixed;'>";
				html[idx++] = "<tr>";
				if(item.serviceMap) {
					html[idx++] = "<td width=\"12px\" aligh=left onclick=\'javascript:ZaServicesListView.expand(event, this)\'>";
					html[idx++] = AjxImg.getImageHtml("NodeExpanded");
					html[idx++] = "</td>";
				}
				html[idx++] = "<td align=left width=20>"
				if(item.status == 1) {
					html[idx++] = AjxImg.getImageHtml("Check");
				} else if (item.status == 0){
					html[idx++] = AjxImg.getImageHtml("Cancel");
				} else {
					html[idx++] = "&nbsp;";
				}
				html[idx++] = "</td>";		
				html[idx++] = "<td>";
				html[idx++] = AjxStringUtil.htmlEncode(item.name);
				if (item.state != null && item.state != "started") {
					html[idx++] = "&nbsp;(";
					html[idx++] = item.state;
					html[idx++] = ")";
				} 
				html[idx++] = "</td>";				
				html[idx++] = "</tr></table></td>";
			}
		} else if (id.indexOf(ZaCluster.PRFX_Node)==0){
			if (onlyServiceInfo) {
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width;
				html[idx++] = " aligh=left>";
				html[idx++] = AjxStringUtil.htmlEncode(" ");
				html[idx++] = "</td>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = (this._headerList[i]._width);
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width-12;
				html[idx++] = " aligh=left>";
				html[idx++] = AjxStringUtil.htmlEncode(item.owner);
				html[idx++] = "</td>";
			}
	   } else if(id.indexOf(ZaCluster.PRFX_clusterName)==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>"
			if (onlyServiceInfo) {
				html[idx++] = "&nbsp;";
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(item.clusterName);
			}
			html[idx++] = "</td>";	   			
	   } else if(id.indexOf(ZaStatus.PRFX_Service)==0) {
			if (onlyServiceInfo) {
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width;
				html[idx++] = " ><table cellpadding=0 cellspacing=0 border=0><tr><td width=20>";
				if(item.status==1) {
					html[idx++] = AjxImg.getImageHtml("Check");
				} else {
					html[idx++] = AjxImg.getImageHtml("Cancel");
				}				
				html[idx++] = "</td><td>";
				html[idx++] = AjxStringUtil.htmlEncode(serviceName);
				html[idx++] = "</td></tr></table></td>";
			} else {
				html[idx++] = "<td width=";
				html[idx++] = this._headerList[i]._width;
				html[idx++] = " aligh=left>";

				html[idx++] = AjxStringUtil.htmlEncode(" ");
				html[idx++] = "</td>";
			}

		} else if(id.indexOf(ZaStatus.PRFX_Time)==0) {
			html[idx++] = "<td width=";
			html[idx++] = this._headerList[i]._width;
			html[idx++] = " aligh=left>";
			if (onlyServiceInfo){
				html[idx++] = AjxStringUtil.htmlEncode(item.time);
			} else {
				html[idx++] = AjxStringUtil.htmlEncode(" ");
			}
			html[idx++] = "</td>";
		}
	}
	html[idx++] = "</tr></table>";
	return idx;
}