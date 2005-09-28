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
* @class ZaOverviewPanelController
* @contructor ZaOverviewPanelController
* Controls the navigation tree.
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaOverviewPanelController(appCtxt, container) {
	ZaController.call(this, appCtxt, container);
	this._overviewPanel = null;
	this._addressesTi = null;
	this._configTi = null;
	this._monitoringTi = null;
	this._cosTi = null;
	this._domainsTi = null;
	this._serversTi = null;
	this._statusTi = null;
	
	this._domainsMap = new Object();
	this._serversMap = new Object();	
	this._serversStatsMap = new Object();
	this._cosMap = new Object();
	
	this._app = appCtxt.getAppController().getApp(ZaZimbraAdmin.ADMIN_APP);
	this._setView();
	this._currentDomain = "";	
}

ZaOverviewPanelController.prototype = new ZaController;
ZaOverviewPanelController.prototype.constructor = ZaOverviewPanelController;

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
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaOverviewPanelController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
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
			this._cosMap[newCos.name] = ti1;
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
		if(detls && delts["obj"]) {
			if(this._cosMap[delts["obj"].id])
				this._cosMap[delts["obj"].id].setText(delts["obj"].name);
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
			if(detls instanceof Array) {
				for (var key in detls) {
					if((detls[key] instanceof ZaCos) && this._cosMap[detls[key].id]) {
						this._cosTi._removeChild(this._cosMap[detls[key].id]);		
					}
				}
			} else if(detls instanceof ZaCos) {
				if(this._cosMap[detls.name]) {
					this._cosTi._removeChild(this._cosMap[detls.id]);		
				}
			}
		}
	}
}
/**
* @param ev
* This listener is invoked by any controller that can create an ZaDomain object
**/
ZaOverviewPanelController.prototype.handleDomainCreation = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		if(ev.getDetails()) {
			var newDomain = ev.getDetails();
			var ti1 = new DwtTreeItem(this._domainsTi);			
			ti1.setText(newDomain.name);	
			ti1.setImage("Domain");
			ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAIN_VIEW);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, newDomain.id);
			this._domainsMap[newDomain.name] = ti1;
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaDomainController or any other controller that can remove an ZaDomain object
**/
ZaOverviewPanelController.prototype.handleDomainRemoval = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		var detls = ev.getDetails();		
		if(detls) {
			if(detls instanceof Array) {
				for (var key in detls) {
					if((detls[key] instanceof ZaDomain) && this._domainsMap[detls[key].id]) {
						this._domainsTi._removeChild(this._domainsMap[detls[key].id]);		
					}
				}
			} else if(detls instanceof ZaDomain) {
				if(this._domainsMap[detls.name]) {
					this._domainsTi._removeChild(this._domainsMap[detls.id]);		
				}
			}
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
		if(detls && delts["obj"]) {
			if(this._serversMap[delts["obj"].id])
				this._serversMap[delts["obj"].id].setText(delts["obj"].name);
			if(this._serversStatsMap[delts["obj"].id])
				this._serversStatsMap[delts["obj"].id].setText(delts["obj"].name);		
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
							this._serversTi._removeChild(this._serversMap[detls[key].id]);		
						}
					 	if(this._serversStatsMap[detls[key].id]) {
							this._statisticsTi._removeChild(this._serversStatsMap[detls[key].id]);								
						}
						
					}
				}
			} else if(detls instanceof ZaServer) {
				if(this._serversMap[detls.id]) {
					this._serversTi._removeChild(this._serversMap[detls.id]);		
				}
				if(this._serversStatsMap[detls.id]) {
					this._statisticsTi._removeChild(this._serversStatsMap[detls.id]);		
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


ZaOverviewPanelController.prototype._setView =
function() {
	this._overviewPanel = new ZaOverviewPanel(this._container, "OverviewPanel", DwtControl.ABSOLUTE_STYLE);
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

	this._addressesTi = new DwtTreeItem(tree, null, null, null, null, "overviewPanelHeader");
	this._addressesTi.setText(ZaMsg.OVP_addresses);
	this._addressesTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADDRESSES);
		
	ti = new DwtTreeItem(this._addressesTi);
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


	this._configTi = new DwtTreeItem(tree, null, null, null, null, "overviewPanelHeader");
	this._configTi.setText(ZaMsg.OVP_configuration);
	this._configTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SYS_CONFIG);	
	
	ti = new DwtTreeItem(this._configTi);
	ti.setText(ZaMsg.OVP_global);
	ti.setImage("GlobalSettings");
	ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_SETTINGS);	

	this._serversTi = new DwtTreeItem(this._configTi);
	this._serversTi.setText(ZaMsg.OVP_servers);
	this._serversTi.setImage("Server");
	this._serversTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVERS_LIST_VIEW);
	
	try {
		//add server nodes
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
		
	this._domainsTi = new DwtTreeItem(this._configTi);
	this._domainsTi.setText(ZaMsg.OVP_domains);
	this._domainsTi.setImage("Domain");
	this._domainsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAINS_LIST_VIEW);

	try {
		//add domain nodes
		var domainList = this._app.getDomainList().getArray();
		if(domainList && domainList.length) {
			var cnt = domainList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this._domainsTi);			
				ti1.setText(domainList[ix].name);	
				ti1.setImage("Domain");
				ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAIN_VIEW);
				ti1.setData(ZaOverviewPanelController._OBJ_ID, domainList[ix].id);
				this._domainsMap[domainList[ix].name] = ti1;
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
	}
	
	this._cosTi = new DwtTreeItem(this._configTi);
	this._cosTi.setText(ZaMsg.OVP_cos);
	this._cosTi.setImage("COS");
	this._cosTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_LIST_VIEW);
		
	try {
		//add COS nodes
		var cosList = this._app.getCosList().getArray();
		if(cosList && cosList.length) {
			var cnt = cosList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this._cosTi);			
				ti1.setText(cosList[ix].name);	
				ti1.setImage("COS");
				ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_VIEW);
				ti1.setData(ZaOverviewPanelController._OBJ_ID, cosList[ix].id);
				this._cosMap[cosList[ix].name] = ti1;
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
	}
				
	this._monitoringTi = new DwtTreeItem(tree, null, null, null, null, "overviewPanelHeader");
	this._monitoringTi.setText(ZaMsg.OVP_monitoring);
	this._monitoringTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._MONITORING);
	

	this._statusTi = new DwtTreeItem(this._monitoringTi);
	this._statusTi.setText(ZaMsg.OVP_status);
	this._statusTi.setImage("Status");
	this._statusTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATUS);

	this._statisticsTi = new DwtTreeItem(this._monitoringTi);
	this._statisticsTi.setText(ZaMsg.OVP_statistics);
	this._statisticsTi.setImage("Statistics");
	this._statisticsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS);
	
	try {
		//add server statistics nodes
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
	
	this._addressesTi.setExpanded(true, false);
	this._configTi.setExpanded(true, false);
	this._monitoringTi.setExpanded(true, false);
	tree.setSelection(this._statusTi, true);	
}


ZaOverviewPanelController.prototype._getCurrentQueryHolder = 
function () {
	var srchField = this._app.getAccountListController()._searchField;
	var curQuery = new ZaSearchQuery("", [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], false, "");							
	if(srchField) {
		var obj = srchField.getObject();
		if(obj) {
			curQuery.types = new Array();
			if(obj[ZaSearch.A_fAliases]=="TRUE") {
				curQuery.types.push(ZaSearch.ALIASES);
			}
			if(obj[ZaSearch.A_fdistributionlists]=="TRUE") {
				curQuery.types.push(ZaSearch.DLS);
			}			
			if(obj[ZaSearch.A_fAccounts]=="TRUE") {
				curQuery.types.push(ZaSearch.ACCOUNTS);
			}			
		}
	}
	return curQuery;
}

ZaOverviewPanelController.prototype._overviewTreeListener =
function(ev) {

	if (ev.detail == DwtTree.ITEM_SELECTED) {
	//DBG.dumpObj(ev);	
		var treeItemType = ev.item.getData(ZaOverviewPanelController._TID);
		//DBG.println("ti = "+treeItemType);
		try {
			if (treeItemType != null) {
				switch (treeItemType) {
					case ZaZimbraAdmin._COS_LIST_VIEW:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getCosListController(), ZaCosListController.prototype.show, ZaCos.getAll(this._app));
						} else {
							this._app.getCosListController().show(ZaCos.getAll(this._app));
						}
						break;
					case ZaZimbraAdmin._ACCOUNTS_LIST_VIEW:

						this._showAccountsView(ZaItem.ACCOUNT,ev);
						break;
 					case ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW:

						this._showAccountsView(ZaItem.DL,ev);
						break;
					case ZaZimbraAdmin._ALIASES_LIST_VIEW:
						
						this._showAccountsView(ZaItem.ALIAS,ev);
						break;
					case ZaZimbraAdmin._DOMAINS_LIST_VIEW:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getDomainListController(), ZaDomainListController.prototype.show, ZaDomain.getAll(this._app));
						} else {					
							this._app.getDomainListController().show(ZaDomain.getAll(this._app));
						}

						break;			
					case ZaZimbraAdmin._SERVERS_LIST_VIEW:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll());
						} else {					
							this._app.getServerListController().show(ZaServer.getAll());
						}

						break;									
					case ZaZimbraAdmin._STATUS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getStatusViewController(),ZaStatusViewController.prototype.show, null);
						} else {					
							this._app.getStatusViewController().show();
						}
						break;		
					case ZaZimbraAdmin._STATISTICS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalStatsController(),ZaGlobalStatsController.prototype.show, null);
						} else {					
							this._app.getGlobalStatsController().show();
						}
						break;		
					case ZaZimbraAdmin._GLOBAL_SETTINGS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, this._app.getGlobalConfig());
						} else {					
							this._app.getGlobalConfigViewController().show(this._app.getGlobalConfig());
						}
						break;		
					case ZaZimbraAdmin._STATISTICS_BY_SERVER:
						var currentServer = this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerStatsController(), ZaServerStatsController.prototype.show,currentServer);
						} else {					
							this._app.getServerStatsController().show(currentServer);
						}

						break;
					case ZaZimbraAdmin._SERVER_VIEW:
						
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerController(),
							 ZaServerController.prototype.show,
							 this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						} else {					
							this._app.getServerController().show(this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						}				
						break;
					case ZaZimbraAdmin._DOMAIN_VIEW:
						
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getDomainController(),
							 ZaDomainController.prototype.show,
							 this._app.getDomainList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						} else {					
							this._app.getDomainController().show(this._app.getDomainList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						}				
						break;		
					case ZaZimbraAdmin._COS_VIEW:
						
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getCosController(),
							 ZaCosController.prototype.show,
							 this._app.getCosList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						} else {					
							this._app.getCosController().show(this._app.getCosList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						}				
						break;											
				}
			}
		} catch (ex) {
			if(!ex) {
				ex = new ZmCsfeException("Unknown error", AjxException.UNKNOWN_ERROR, "ZaOverviewPanelController.prototype._overviewTreeListener", "Unknown error")
			}
			this._handleException(ex, "ZaOverviewPanelController.prototype._overviewTreeListener", null, false);
		}
	}
}

ZaOverviewPanelController.prototype._showAccountsView = function (defaultType, ev) {
	var queryHldr = this._getCurrentQueryHolder();
	queryHldr.isByDomain = false;
	queryHldr.byValAttr = false;
	queryHldr.queryString = "";
	queryHldr.types = [ZaSearch.TYPES[defaultType]];
	if(defaultType == ZaItem.DL) {
		queryHldr.fetchAttrs = ZaDistributionList.searchAttributes
	} else {
		queryHldr.fetchAttrs = ZaSearch.standardAttributes;
	}
	var acctListController = this._app.getAccountListController();
	acctListController.setPageNum(1);	
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(acctListController, ZaAccountListController.prototype.show,ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
	} else {					
		acctListController.show(ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
	}
	acctListController.setDefaultType(defaultType);
	acctListController.setQuery(queryHldr);
};

