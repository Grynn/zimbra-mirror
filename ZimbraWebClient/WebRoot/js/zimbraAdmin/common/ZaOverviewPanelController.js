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
* @class ZaOverviewPanelController
* @contructor ZaOverviewPanelController
* Controls the navigation tree.
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaOverviewPanelController = function(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app, "ZaOverviewPanelController");
	this._init(appCtxt, container);
	this._setView();
}

ZaOverviewPanelController.prototype = new ZaController;
ZaOverviewPanelController.prototype.constructor = ZaOverviewPanelController;
ZaOverviewPanelController.overviewTreeListeners = new Object();
ZaOverviewPanelController.treeModifiers = new Array();
ZaOverviewPanelController._TID = "TID";
ZaOverviewPanelController._OBJ_ID = "OBJ_ID";

ZaOverviewPanelController.prototype.toString = 
function() {
	return "ZaOverviewPanelController";
}

ZaOverviewPanelController.prototype.getOverviewPanel =
function() {
	if(!this._overviewPanel) {
		this._setView();
	}
	return this._overviewPanel;
}

/**
* @param ev
* This listener is invoked by any controller that can create an ZaCos object
**/
ZaOverviewPanelController.prototype.handleCosCreation = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		if(ev.getDetails()) {
			var newCos = ev.getDetails();
			var ti1 = new DwtTreeItem(this._cosTi);			
			ti1.setText(newCos.name);	
			ti1.setImage("COS");
			ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_VIEW);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, newCos.id);
			this._cosMap[newCos.id] = ti1;
		}
	}
}

/**
* @param ev
* This listener is invoked by  any controller that can change a ZaCos object
* the purpose of this listener is to keep labels of COS sub tree nodes in sync with COSes
**/
ZaOverviewPanelController.prototype.handleCosChange =
function (ev) {
	if(ev) {
		var detls = ev.getDetails();	
		if(detls && (detls instanceof Array)) {		
			if(detls["obj"]) {
				if(this._cosMap[detls["obj"].id])
					this._cosMap[detls["obj"].id].setText(detls["obj"].name);
			}
		}else if (detls && (detls instanceof ZaCos)){
			if(this._cosMap[detls.id])
					this._cosMap[detls.id].setText(detls.name);
		}
	}
}

/**
* @param ev
* This listener is invoked by  any controller that can remove an ZaCos object
**/
ZaOverviewPanelController.prototype.handleCosRemoval = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		var detls = ev.getDetails();		
		if(detls) {
			if(detls && (detls instanceof Array)) {
				for (var key in detls) {
					if((detls[key] instanceof ZaCos) && this._cosMap[detls[key].id]) {
						this._cosTi.removeChild(this._cosMap[detls[key].id]);		
					}
				}
			} else if(detls && (detls instanceof ZaCos)) {
				if(this._cosMap[detls.id]) {
					this._cosTi.removeChild(this._cosMap[detls.id]);		
				}
			}
		}
	}
}


ZaOverviewPanelController.prototype.searchDomains = function() {
/*	if(this._app) {
		this._app.searchDomains();
	} else {	*/
		var callback = new AjxCallback(this, this.domainSearchCallback);
		var domainListController = this._app.getDomainListController ();
		domainListController._currentQuery = "(zimbraDomainType=local)" ;
		var searchParams = {
				query: domainListController._currentQuery, 
				types:[ZaSearch.DOMAINS],
				sortBy:ZaDomain.A_domainName,
				offset:"0",
				sortAscending:"1",
				limit:ZaDomain.MAXSEARCHRESULTS,
				callback:callback,
				controller: this
		}
		ZaSearch.searchDirectory(searchParams);
	//}
}

ZaOverviewPanelController.prototype.domainSearchCallback = 
function (resp) {
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaOverviewPanelController.prototype.domainSearchCallback"));
		}
		if(resp.isException()) {
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaOverviewPanelController.prototype.domainSearchCallback");
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList(ZaDomain, this._app);	
			list.loadFromJS(response);

			this.updateDomainList(list);
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._app.getCurrentController()._handleException(ex, "ZaOverviewPanelController.prototype.searchCallback");	
		} else {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.queryParseError, ex);
		}		
	}
}
ZaOverviewPanelController.prototype.updateSavedSearchTreeList =
function () {
	var isExpanded = this._savedSearchTi.getExpanded();
	
	//remove the old treeitems
	for (var i=0; i<this._savedSearchMapArr.length; i++) {
		this._savedSearchTi.removeChild(this._savedSearchMapArr[i]);
	}
	
	this._savedSearchMapArr = [];
	//add the new tree items
	try {	
		var savedSearchList = this._app.getSavedSearchList();
		if(savedSearchList && savedSearchList.length) {
			var cnt = savedSearchList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this._savedSearchTi);			
				ti1.setText(savedSearchList[ix].name);	
				ti1.setImage("SearchFolder");
				ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SEARCH_LIST_VIEW);
				ti1.setData("name", savedSearchList[ix].name);
				ti1.setData("query", savedSearchList[ix].query); //keep the query information here
				this._savedSearchMapArr.push(ti1);
			}
		}
		//keep the orginal expanded state
		if (isExpanded) this._savedSearchTi.setExpanded(true, false);
	} catch (ex) {
		this._handleException(ex, "ZaOverviewPanelController.prototype.updateSavedSearchTreeList ", null, false);
	}
}

ZaOverviewPanelController.prototype.updateDomainList = 
function (list) {
	var domainList = list.getArray();
	for (var key in this._domainsMap) {
		this._domainsTi.removeChild(this._domainsMap[key]);		
	}
	this._domainsMap = new Object();	
	//add domain nodes
	if(domainList && domainList.length) {
		var cnt = domainList.length;
		for(var ix=0; ix< cnt; ix++) {
			var ti1 = new DwtTreeItem(this._domainsTi);			
			ti1.setText(domainList[ix].name);	
			ti1.setImage("Domain");
			ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAIN_VIEW);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, domainList[ix].id);
			this._domainsMap[domainList[ix].id] = ti1;
		}
	}
}

/**
* @param ev
* This listener is invoked by any controller that can create an ZaServer object
**/
ZaOverviewPanelController.prototype.handleServerCreation = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		if(ev.getDetails()) {
			var newServer = ev.getDetails();
			var ti1 = new DwtTreeItem(this._serversTi);			
			ti1.setText(newServer.name);	
			ti1.setImage("Server");
			ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVER_VIEW);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, newServer.id);
			this._serversMap[newServer.id] = ti1;

			var ti2 = new DwtTreeItem(this._statisticsTi);			
			ti2.setText(newServer.name);	
			ti2.setImage("StatisticsByServer");
			ti2.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS_BY_SERVER);
			ti2.setData(ZaOverviewPanelController._OBJ_ID, newServer.id);
			this._serversStatsMap[newServer.id] = ti2;

	
		}
	}
}
/**
* @param ev
* This listener is invoked by any controller that can change an ZaServer object
* the purpose of this listener is to keep labels of Servers sub tree nodes and 
* Server Statistics sub tree nodes in sync with Servers
**/
ZaOverviewPanelController.prototype.handleServerChange =
function (ev) {
	if(ev) {
		var detls = ev.getDetails();	
		if(detls instanceof Array) {	
			if(detls && detls["obj"]) {
				if(this._serversMap[detls["obj"].id])
					this._serversMap[detls["obj"].id].setText(detls["obj"].name);
				if(this._serversStatsMap[detls["obj"].id])
					this._serversStatsMap[detls["obj"].id].setText(detls["obj"].name);		
			}
		}else if (detls){
			if(this._serversMap[detls.id])
				this._serversMap[detls.id].setText(detls.name);
			if(this._serversStatsMap[detls.id])
				this._serversStatsMap[detls.id].setText(detls.name);	
		}
	}
}

/**
* @param ev
* This listener is invoked by any controller that can remove an ZaServer object
**/
ZaOverviewPanelController.prototype.handleServerRemoval = 
function (ev) {
	if(ev) {
		var detls = ev.getDetails();		
		if(detls) {
			if(detls instanceof Array) {
				for (var key in detls) {
					if((detls[key] instanceof ZaServer)) {
					 	if(this._serversMap[detls[key].id]) {
							this._serversTi.removeChild(this._serversMap[detls[key].id]);		
						}
					 	if(this._serversStatsMap[detls[key].id]) {
							this._statisticsTi.removeChild(this._serversStatsMap[detls[key].id]);								
						}
						
					}
				}
			} else if(detls instanceof ZaServer) {
				if(this._serversMap[detls.id]) {
					this._serversTi.removeChild(this._serversMap[detls.id]);		
				}
				if(this._serversStatsMap[detls.id]) {
					this._statisticsTi.removeChild(this._serversStatsMap[detls.id]);		
				}				
			}
		}
	}
}

ZaOverviewPanelController.prototype.setCurrentDomain = 
function (newDomain) {
	this._currentDomain = newDomain;
}


ZaOverviewPanelController.prototype.getCurrentDomain = 
function () {
	return this._currentDomain;
}

//protected and private methods
/**
* @method init
* this method creates and initializes any members of this class
* This method is called by the contructor after the superconstructor and before the _setView
**/
ZaOverviewPanelController.prototype._init = 
function (appCtxt, container) {
	this._overviewPanel = null;
	this._addressesTi = null;
	this._configTi = null;
	this._monitoringTi = null;
	this._cosTi = null;
	this._domainsTi = null;
	this._serversTi = null;
	this.statusTi = null;
	this._savedSearchTi = null ;
	this._currentDomain = "";	
	this._app = appCtxt.getAppController().getApp(ZaZimbraAdmin.ADMIN_APP);
			
	if(ZaSettings.DOMAINS_ENABLED)
		this._domainsMap = new Object();
	
	if(ZaSettings.SERVERS_ENABLED)
		this._serversMap = new Object();	
	
	if(ZaSettings.SERVER_STATS_ENABLED)
		this._serversStatsMap = new Object();
	
	if(ZaSettings.COSES_ENABLED)	
		this._cosMap = new Object();
	
	if(ZaSettings.MAILQ_ENABLED)
		this._mailqMap = new Object();
		
	if (ZaSettings.SAVE_SEARCH_ENABLED) 
		this._savedSearchMapArr = [] ;
}

ZaOverviewPanelController.prototype._setView =
function() {
	this._overviewPanel = new ZaOverviewPanel(this._container, "OverviewPanel", DwtControl.ABSOLUTE_STYLE);
	this._overviewPanel.setScrollStyle(DwtControl.SCROLL);
	ZaSearch.loadPredefinedSearch() ;
	this._buildFolderTree();
	//this._overviewPanel.getFolderTree().setSelection(this._inboxTreeItem);
	this._overviewPanel.zShow(true);
}

ZaOverviewPanelController.prototype._buildFolderTree =
function() {
	var tree = this._overviewPanel.getFolderTree();
	var l = new AjxListener(this, this._overviewTreeListener);
	tree.addSelectionListener(l);

	var ti;
	if(ZaSettings.ADDRESSES_ENABLED) {
		this._addressesTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._addressesTi.enableSelection(false);
		this._addressesTi.setText(ZaMsg.OVP_addresses);
		this._addressesTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADDRESSES);
			
		this.accountTi = ti = new DwtTreeItem(this._addressesTi);
		ti.setText(ZaMsg.OVP_accounts);
		ti.setImage("Account");
		ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
	
		ti = new DwtTreeItem(this._addressesTi);
		ti.setText(ZaMsg.OVP_aliases);
		ti.setImage("AccountAlias");
		ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ALIASES_LIST_VIEW);
	
		ti = new DwtTreeItem(this._addressesTi);
		ti.setText(ZaMsg.OVP_distributionLists);
		ti.setImage("Group");
		ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW);
		
		ti = new DwtTreeItem(this._addressesTi);
		ti.setText(ZaMsg.OVP_resources);
		ti.setImage("Resource");
		ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._RESOURCE_VIEW);
		
		this._addressesTi.addSeparator();
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = ZaOverviewPanelController.accountListTreeListener;
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ALIASES_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW] = ZaOverviewPanelController.dlListTreeListener;		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._RESOURCE_VIEW] = ZaOverviewPanelController.resourceListTreeListener;		
	}
		
	if(ZaSettings.SYSTEM_CONFIG_ENABLED) {	
		this._configTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._configTi.enableSelection(false);
		this._configTi.setText(ZaMsg.OVP_configuration);
		this._configTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SYS_CONFIG);	
		
		this._cosTi = new DwtTreeItem(this._configTi);
		this._cosTi.setText(ZaMsg.OVP_cos);
		this._cosTi.setImage("COS");
		this._cosTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_LIST_VIEW);
			
		try {
			//add COS nodes
			var cosList = this._app.getCosList();
			if(cosList && cosList.size()) {
				var idHash = cosList.getIdHash();
				for(var ix in idHash) {
					var ti1 = new DwtTreeItem(this._cosTi);			
					ti1.setText(idHash[ix].name);	
					ti1.setImage("COS");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_VIEW);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, idHash[ix].id);
					this._cosMap[idHash[ix].id] = ti1;
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}	
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_LIST_VIEW] = ZaOverviewPanelController.cosListTreeListener;		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_VIEW] = ZaOverviewPanelController.cosTreeListener;				
		
		this._domainsTi = new DwtTreeItem(this._configTi);
		this._domainsTi.setText(ZaMsg.OVP_domains);
		this._domainsTi.setImage("Domain");
		this._domainsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAINS_LIST_VIEW);
	
		try {
			//add domain nodes
			this.searchDomains();
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
			
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAINS_LIST_VIEW] = ZaOverviewPanelController.domainListTreeListener;		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAIN_VIEW] = ZaOverviewPanelController.domainTreeListener;				
	
		this._serversTi = new DwtTreeItem(this._configTi);
		this._serversTi.setText(ZaMsg.OVP_servers);
		this._serversTi.setImage("Server");
		this._serversTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVERS_LIST_VIEW);
		
		try {
			//add server nodes
//			DBG.println(AjxDebug.DBG1, "add server nodes ");				
			var serverList = this._app.getServerList().getArray();
			if(serverList && serverList.length) {
				var cnt = serverList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem(this._serversTi);			
					ti1.setText(serverList[ix].name);	
					ti1.setImage("Server");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVER_VIEW);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
					this._serversMap[serverList[ix].id] = ti1;					
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
			
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVERS_LIST_VIEW] = ZaOverviewPanelController.serverListTreeListener;		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_VIEW] = ZaOverviewPanelController.serverTreeListener;				

		if(ZaSettings.ZIMLETS_ENABLED) {
			this._zimletsTi = new DwtTreeItem(this._configTi);
			this._zimletsTi.setText(ZaMsg.OVP_zimlets);
			this._zimletsTi.setImage("Zimlet");
			this._zimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ZIMLET_LIST_VIEW);
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = ZaOverviewPanelController.zimletListTreeListener;					
		}

		if(ZaSettings.ADMIN_ZIMLETS_ENABLED) {
			this._adminZimletsTi = new DwtTreeItem(this._configTi);
			this._adminZimletsTi.setText(ZaMsg.OVP_adminZimlets);
			this._adminZimletsTi.setImage("AdminExtension");
			this._adminZimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW);
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = ZaOverviewPanelController.adminExtListTreeListener;					
		}
				
		ti = new DwtTreeItem(this._configTi);
		ti.setText(ZaMsg.OVP_global);
		ti.setImage("GlobalSettings");
		ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_SETTINGS);	

		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_SETTINGS] = ZaOverviewPanelController.globalSettingsTreeListener;				
		this._configTi.addSeparator();	
		
	}
	if(ZaSettings.MONITORING_ENABLED) {
		this._monitoringTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._monitoringTi.enableSelection(false);	
		this._monitoringTi.setText(ZaMsg.OVP_monitoring);
		this._monitoringTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._MONITORING);
		
	
		this.statusTi = new DwtTreeItem(this._monitoringTi);
		this.statusTi.setText(ZaMsg.OVP_status);
		this.statusTi.setImage("Status");
		this.statusTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATUS);
	
		this._statisticsTi = new DwtTreeItem(this._monitoringTi);
		this._statisticsTi.setText(ZaMsg.OVP_statistics);
		this._statisticsTi.setImage("Statistics");
		this._statisticsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS);
		
		try {
			//add server statistics nodes
//			DBG.println(AjxDebug.DBG1, "add server statistics nodes ");	
			var serverList = this._app.getServerList().getArray();
			if(serverList && serverList.length) {
				var cnt = serverList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem(this._statisticsTi);			
					ti1.setText(serverList[ix].name);	
					ti1.setImage("StatisticsByServer");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS_BY_SERVER);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
					this._serversStatsMap[serverList[ix].id] = ti1;
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
		
		this._monitoringTi.addSeparator();
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATUS] = ZaOverviewPanelController.statusTreeListener;		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATISTICS] = ZaOverviewPanelController.statsTreeListener;				
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATISTICS_BY_SERVER] = ZaOverviewPanelController.statsByServerTreeListener;						
	}
	
	if(ZaSettings.TOOLS_ENABLED) {
		this._toolsTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._toolsTi.enableSelection(false);	
		this._toolsTi.setText(ZaMsg.OVP_tools);
		this._toolsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._MONITORING);
		
		this._postqTi = new DwtTreeItem(this._toolsTi);
		this._postqTi.setText(ZaMsg.OVP_postq);
		this._postqTi.setImage("Queue");
		this._postqTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_VIEW);
		
		try {
			//add server statistics nodes
			var mtaList = this._app.getPostQList().getArray();
			if(mtaList && mtaList.length) {
				var cnt = mtaList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem(this._postqTi);			
					ti1.setText(mtaList[ix].name);	
					ti1.setImage("Queue");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, mtaList[ix].id);
					this._mailqMap[mtaList[ix].id] = ti1;
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
		this._toolsTi.addSeparator();
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_VIEW] = ZaOverviewPanelController.postqTreeListener;				
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = ZaOverviewPanelController.postqByServerTreeListener;						
	}
		
	//SavedSearches Tree	
	if(ZaSettings.SAVE_SEARCH_ENABLED) {
		this._savedSearchTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._savedSearchTi.enableSelection(false);
		this._savedSearchTi.setText(ZaMsg.OVP_savedSearches);
		this._savedSearchTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SEARCHES);
		
		try {	
			var savedSearchList = this._app.getSavedSearchList();
			if(savedSearchList && savedSearchList.length) {
				var cnt = savedSearchList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem(this._savedSearchTi);			
					ti1.setText(savedSearchList[ix].name);	
					ti1.setImage("SearchFolder");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SEARCH_LIST_VIEW);
					ti1.setData("name", savedSearchList[ix].name);
					ti1.setData("query", savedSearchList[ix].query); //keep the query information here
					this._savedSearchMapArr.push(ti1);
				}
			}
		} catch (ex) {
			this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
		}
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SEARCH_LIST_VIEW] = ZaOverviewPanelController.searchListTreeListener;
	}
	
		
	if(ZaSettings.ADDRESSES_ENABLED)
		this._addressesTi.setExpanded(true, false);

	if(ZaSettings.SYSTEM_CONFIG_ENABLED)	
		this._configTi.setExpanded(true, false);
	
	if(ZaSettings.MONITORING_ENABLED)
		this._monitoringTi.setExpanded(true, false);

	if(ZaSettings.TOOLS_ENABLED)
		this._toolsTi.setExpanded(true, false);

	
	if (ZaSettings.SAVE_SEARCH_ENABLED) 
		this._savedSearchTi.setExpanded(true, false);
			
	//Instrumentation code start
	if(ZaOverviewPanelController.treeModifiers) {
		var methods = ZaOverviewPanelController.treeModifiers;
		var cnt = methods.length;
		for(var i = 0; i < cnt; i++) {
			if(typeof(methods[i]) == "function") {
				methods[i].call(this,tree);
			}
		}
	}	
	//Instrumentation code end			
}



ZaOverviewPanelController.prototype._overviewTreeListener =
function(ev) {
	try {
		var eventHandler = null ;
		var treeItemType = ev.item.getData(ZaOverviewPanelController._TID);
		if (treeItemType != null && 
			ZaOverviewPanelController.overviewTreeListeners[treeItemType] &&
			typeof (ZaOverviewPanelController.overviewTreeListeners[treeItemType]) == "function") {
			eventHandler = ZaOverviewPanelController.overviewTreeListeners[treeItemType] ;
		}
		if (eventHandler) {
			if (ev.detail == DwtTree.ITEM_SELECTED ) {
					eventHandler.call(this, ev);
				
			}else if (ev.detail == DwtTree.ITEM_ACTIONED) {
				if (treeItemType == ZaZimbraAdmin._SEARCH_LIST_VIEW) { //saved search item is actioned.
					if (AjxEnv.hasFirebug) console.debug("Saved Search tree Item is actioned.") ;
					eventHandler.call(this, ev) ;
				}	
			}
		}
	} catch (ex) {
			if(!ex) {
				ex = new ZmCsfeException("Unknown error", AjxException.UNKNOWN_ERROR, "ZaOverviewPanelController.prototype._overviewTreeListener", "Unknown error")
			}
			this._handleException(ex, "ZaOverviewPanelController.prototype._overviewTreeListener", null, false);
		}
}

/* default tree listeners */

ZaOverviewPanelController.cosTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getCosController(),
		 ZaCosController.prototype.show,
		 this._app.getCosList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	} else {					
		this._app.getCosController().show(this._app.getCosList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	}	
}

ZaOverviewPanelController.domainTreeListener = function (ev) {
	//var domain = new ZaDomain(this._app);
	//domain.name = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	//domain.attrs[ZaDomain.A_domainName]=ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	//domain.name = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	//domain.load("name",ev.item.getData(ZaOverviewPanelController._OBJ_ID));	
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getDomainController(),
		 ZaDomainController.prototype.show,this._app.getDomainList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	} else {	
						
		this._app.getDomainController().show(domain);
	}
}

ZaOverviewPanelController.serverTreeListener = function (ev) {
//	DBG.println(AjxDebug.DBG1, "ZaOverviewPanelController.serverTreeListener called");
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getServerController(),
		 ZaServerController.prototype.show,
		 this._app.getServerList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	} else {					
		this._app.getServerController().show(this._app.getServerList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	}
}

ZaOverviewPanelController.statsByServerTreeListener = function (ev) {
	var currentServer = this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
	var curController = this._app.getCurrentController() ;
	if(curController) {
		curController.switchToNextView(this._app.getServerStatsController(), ZaServerStatsController.prototype.show,currentServer);
	} else {
		curController = this._app.getServerStatsController();			
		curController.show(currentServer);
	}
	//refresh the MbxPage when the server tree item is clicked
	/* It should be done in the ZaServerMBXStatsPage._render method.
	var mbxPage = curController._contentView ? curController._contentView._mbxPage : null ;
	if (mbxPage) {
		mbxPage._initialized = false ; //force mbxPage.showMe to query the server again.
		if (curController._contentView._currentTabKey == ZaServerMBXStatsPage.TAB_KEY) { //MbxPage is the current page
			//we need to manually call the showMe()
			DBG.println("Invoke the ZaServerMBXStatsPage.showMe() to update the mbx quotas.");
			mbxPage.showMe();
		}
	}*/
}

ZaOverviewPanelController.globalSettingsTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, this._app.getGlobalConfig());
	} else {					
		this._app.getGlobalConfigViewController().show(this._app.getGlobalConfig());
	}
}

ZaOverviewPanelController.statsTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getGlobalStatsController(),ZaGlobalStatsController.prototype.show, null);
	} else {					
		this._app.getGlobalStatsController().show();
	}
}

ZaOverviewPanelController.statusTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getStatusViewController(),ZaStatusViewController.prototype.show, null);
	} else {					
		this._app.getStatusViewController().show();
	}
}

ZaOverviewPanelController.serverListTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll(this._app));
	} else {					
		this._app.getServerListController().show(ZaServer.getAll(this._app));
	}
}

ZaOverviewPanelController.domainListTreeListener = function (ev) {
	var domainListController = this._app.getDomainListController ();
		domainListController._currentQuery = "(zimbraDomainType=local)" ;
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(domainListController, ZaDomainListController.prototype.show, true);
	} else {					
		domainListController.show(true);
	}
	//this.searchDomains();
	this._modifySearchMenuButton(ZaItem.DOMAIN) ;
}

ZaOverviewPanelController.aliasListTreeListener = function (ev) {
	this._showAccountsView(ZaItem.ALIAS,ev);
	this._modifySearchMenuButton(ZaItem.ALIAS) ;	
}

ZaOverviewPanelController.dlListTreeListener = function (ev) {
	this._showAccountsView(ZaItem.DL,ev);
	this._modifySearchMenuButton(ZaItem.DL) ;
}

ZaOverviewPanelController.accountListTreeListener = function (ev) {
	this._showAccountsView(ZaItem.ACCOUNT,ev);
	this._modifySearchMenuButton(ZaItem.ACCOUNT) ;
}

ZaOverviewPanelController.resourceListTreeListener = function (ev) {
	this._showAccountsView(ZaItem.RESOURCE,ev);
	this._modifySearchMenuButton(ZaItem.RESOURCE) ;
}

ZaOverviewPanelController.searchListTreeListener = function (ev) {
	var searchField = this._app.getSearchListController()._searchField ;
	var name = ev.item.getData("name") ;
	var query = ev.item.getData("query");
	if (ev.detail == DwtTree.ITEM_SELECTED) {
		if (AjxEnv.hasFirebug) console.debug("Run the saved search ...") ;
		searchField.selectSavedSearch(name, query);
	}else if (ev.detail == DwtTree.ITEM_ACTIONED){
		searchField._currentSavedSearch = {name: name, query: query};
		searchField.getSavedSearchActionMenu().popup(0, ev.docX, ev.docY);
	}
}

ZaOverviewPanelController.zimletListTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getZimletListController(), ZaZimletListController.prototype.show, ZaZimlet.getAll(this._app,ZaZimlet.EXCLUDE_EXTENSIONS));
	} else {
		this._app.getZimletListController().show(ZaZimlet.getAll(this._app,ZaZimlet.EXCLUDE_EXTENSIONS));
	}	
}

ZaOverviewPanelController.adminExtListTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getAdminExtListController(), ZaAdminExtListController.prototype.show, ZaZimlet.getAll(this._app, ZaZimlet.EXCLUDE_MAIL ));
	} else {
		this._app.getAdminExtListController().show(ZaZimlet.getAll(this._app, ZaZimlet.EXCLUDE_MAIL ));
	}	
}


ZaOverviewPanelController.cosListTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getCosListController(), ZaCosListController.prototype.show, ZaCos.getAll(this._app));
	} else {
		this._app.getCosListController().show(ZaCos.getAll(this._app));
	}
}

ZaOverviewPanelController.postqTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getMTAListController(), ZaMTAListController.prototype.show, ZaMTA.getAll(this._app));
	} else {
		this._app.getMTAListController().show(ZaServer.getAll(this._app));
	}
}

ZaOverviewPanelController.postqByServerTreeListener = function (ev) {
	var currentServer = this._app.getPostQList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(this._app.getMTAController(), ZaMTAController.prototype.show,currentServer);
	} else {					
		this._app.getMTAController().show(currentServer);
	}
}


ZaOverviewPanelController.prototype._modifySearchMenuButton = 
function (itemType) {
	if (itemType) {
		var searchListController = this._app.getSearchListController(); 
		switch (itemType) {
			case ZaItem.ACCOUNT:
				searchListController._searchField.accFilterSelected(); break ;
			case ZaItem.ALIAS:
				searchListController._searchField.aliasFilterSelected(); break ;
			case ZaItem.DL:
				searchListController._searchField.dlFilterSelected(); break ;
			case ZaItem.RESOURCE:
				searchListController._searchField.resFilterSelected(); break ;
			case ZaItem.DOMAIN:
				searchListController._searchField.domainFilterSelected(); break ;
		}
	}
} 