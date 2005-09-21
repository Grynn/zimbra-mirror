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
	this._accountsTi = null;
	this._statusTi = null;
	this._domainsMap = new Object();
	this._serversMap = new Object();	
	this._app = appCtxt.getAppController().getApp(ZaZimbraAdmin.ADMIN_APP);
	this._setView();
	this._currentDomain = "";	
	this._currentServer = new Object();		
}

ZaOverviewPanelController.prototype = new ZaController;
ZaOverviewPanelController.prototype.constructor = ZaOverviewPanelController;

ZaOverviewPanelController._ACCOUNTS = 1;
ZaOverviewPanelController._DOMAINS = 2;
ZaOverviewPanelController._COS = 3;
ZaOverviewPanelController._STATUS = 4;
ZaOverviewPanelController._SERVERS = 5;
ZaOverviewPanelController._GLOBAL_SETTINGS = 6;
ZaOverviewPanelController._STATISTICS = 7;
ZaOverviewPanelController._DISTRIBUTION_LISTS = 8;

ZaOverviewPanelController._ACCOUNTS_SUB_TREE = 1000;
ZaOverviewPanelController._STATISTICS_SUB_TREE = 10000;
ZaOverviewPanelController._DISTRIBUTION_LISTS_SUB_TREE = 100000;

ZaOverviewPanelController._TID = "TID";
ZaOverviewPanelController._OBJ_ID = "OBJ_ID";

ZaOverviewPanelController.prototype.toString = 
function() {
	return "ZaOverviewPanelController";
}

ZaOverviewPanelController.prototype.getOverviewPanel =
function() {
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
* This listener is invoked by any controller that can create an ZaDomain object
**/
ZaOverviewPanelController.prototype.handleDomainCreation = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		if(ev.getDetails()) {
			var newDomain = ev.getDetails();
			var ti1 = new DwtTreeItem(this._accountsTi);			
			ti1.setText(newDomain.name);	
			ti1.setImage("AccountByDomain");
			ti1.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._ACCOUNTS_SUB_TREE);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, newDomain.name);
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
					if((detls[key] instanceof ZaDomain) && this._domainsMap[detls[key].name]) {
						this._accountsTi._removeChild(this._domainsMap[detls[key].name]);		
					}
				}
			} else if(detls instanceof ZaDomain) {
				if(this._domainsMap[detls.name]) {
					this._accountsTi._removeChild(this._domainsMap[detls.name]);		
				}
			}
		}
	}
}


ZaOverviewPanelController.prototype.setCurrentDomain = 
function (newDomain) {
	this._currentDomain = newDomain;
}

ZaOverviewPanelController.prototype.setCurrentServer =
function (newServer) {
	this._currentServer = newServer;
}

ZaOverviewPanelController.prototype.getCurrentDomain = 
function () {
	return this._currentDomain;
}

ZaOverviewPanelController.prototype.getCurrentServer = 
function () {
	return this._currentServer;
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

	this._statusTi = new DwtTreeItem(tree);
	this._statusTi.setText(ZaMsg.OVP_status);
	this._statusTi.setImage("Status");
	this._statusTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._STATUS);

	this.statisticsTi = new DwtTreeItem(tree);
	this.statisticsTi.setText(ZaMsg.OVP_statistics);
	this.statisticsTi.setImage("Statistics");
	this.statisticsTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._STATISTICS);
	
	try {
		//add server nodes
		var serverList = this._app.getServerList().getArray();
		if(serverList && serverList.length) {
			var cnt = serverList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this.statisticsTi);			
				ti1.setText(serverList[ix].name);	
				ti1.setImage("StatisticsByServer");
				ti1.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._STATISTICS_SUB_TREE);
				ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
				this._serversMap[serverList[ix].id] = ti1;
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
	}

	
	this._accountsTi = new DwtTreeItem(tree);
	this._accountsTi.setText(ZaMsg.OVP_accounts);
	this._accountsTi.setImage("Account");
	this._accountsTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._ACCOUNTS);

	try {
		//add domain nodes
		var domainList = this._app.getDomainList().getArray();
		if(domainList && domainList.length) {
			var cnt = domainList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this._accountsTi);			
				ti1.setText(domainList[ix].name);	
				ti1.setImage("AccountByDomain");
				ti1.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._ACCOUNTS_SUB_TREE);
				ti1.setData(ZaOverviewPanelController._OBJ_ID, domainList[ix].name);
				this._domainsMap[domainList[ix].name] = ti1;
			}
		}
	} catch (ex) {
		this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
	}

	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_cos);
	ti.setImage("COS");
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._COS);
	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_domains);
	ti.setImage("Domain");
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._DOMAINS);

	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_servers);
	ti.setImage("Server");
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._SERVERS);
	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_global);
	ti.setImage("GlobalSettings");
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._GLOBAL_SETTINGS);	

 	ti = new DwtTreeItem(tree);
 	ti.setText(ZaMsg.OVP_distributionLists);
 	// TODO - ICON for distribution lists
 	ti.setImage("GlobalSettings");
 	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._DISTRIBUTION_LISTS);	

 	try {
 		//add domain nodes
 		var domainList = this._app.getDomainList().getArray();
 		if(domainList && domainList.length) {
 			var cnt = domainList.length;
 			for(var ix=0; ix< cnt; ix++) {
 				var ti1 = new DwtTreeItem(ti);
 				ti1.setText(domainList[ix].name);	
				ti1.setImage("AccountByDomain");
 				ti1.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._DISTRIBUTION_LISTS_SUB_TREE);
 				ti1.setData(ZaOverviewPanelController._OBJ_ID, domainList[ix].name);
 				//this._domainsMap[domainList[ix].name] = ti1;
 			}
 		}
 	} catch (ex) {
 		this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
 	}

	
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
					case ZaOverviewPanelController._COS:
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getCosListController(), ZaCosListController.prototype.show, ZaCos.getAll(this._app));
						} else {
							this._app.getCosListController().show(ZaCos.getAll(this._app));
						}
						break;
					case ZaOverviewPanelController._ACCOUNTS:
//						var queryHldr = this._app.getAccountListController().getQuery();
						var queryHldr = this._getCurrentQueryHolder();
						queryHldr.isByDomain = false;
						queryHldr.byValAttr = false;
						queryHldr.queryString = "";
						this._app.getAccountListController().setPageNum(1);					
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getAccountListController(), ZaAccountListController.prototype.show,ZaSearch.searchByQueryHolder(queryHldr,this._app.getAccountListController().getPageNum(), ZaAccount.A_uid, null,this._app));
						} else {					
							this._app.getAccountListController().show(ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
						}
						this._app.getAccountListController().setQuery(queryHldr);	
						break;					
					case ZaOverviewPanelController._DOMAINS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getDomainListController(), ZaDomainListController.prototype.show, ZaDomain.getAll(this._app));
						} else {					
							this._app.getDomainListController().show(ZaDomain.getAll(this._app));
						}

						break;			
					case ZaOverviewPanelController._SERVERS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll());
						} else {					
							this._app.getServerListController().show(ZaServer.getAll());
						}

						break;									
					case ZaOverviewPanelController._STATUS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getStatusViewController(),ZaStatusViewController.prototype.show, null);
						} else {					
							this._app.getStatusViewController().show();
						}
						break;		
					case ZaOverviewPanelController._STATISTICS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalStatsController(),ZaGlobalStatsController.prototype.show, null);
						} else {					
							this._app.getGlobalStatsController().show();
						}
						break;		
					case ZaOverviewPanelController._GLOBAL_SETTINGS:

						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, this._app.getGlobalConfig());
						} else {					
							this._app.getGlobalConfigViewController().show(this._app.getGlobalConfig());
						}
						break;		
 					case ZaOverviewPanelController._DISTRIBUTION_LISTS_SUB_TREE:
						// fall through
					case ZaOverviewPanelController._ACCOUNTS_SUB_TREE:
						this.setCurrentDomain(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
//						var queryHldr = this._app.getAccountListController().getQuery();
						var queryHldr = this._getCurrentQueryHolder();
						queryHldr.isByDomain = true;
						queryHldr.byValAttr = this._currentDomain;
						queryHldr.queryString = "";
						this._app.getAccountListController().setPageNum(1);	
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getAccountListController(), ZaAccountListController.prototype.show,ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
						} else {					
							this._app.getAccountListController().show(ZaSearch.searchByQueryHolder(queryHldr,1, ZaAccount.A_uid, null,this._app));
						}
						this._app.getAccountListController().setQuery(queryHldr);
						break;		
					case ZaOverviewPanelController._STATISTICS_SUB_TREE:

						this.setCurrentServer(this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerStatsController(), ZaServerStatsController.prototype.show,this._currentServer);
						} else {					
							this._app.getServerStatsController().show(this._currentServer);
						}

						break;
//  				case ZaOverviewPanelController._DISTRIBUTION_LISTS_SUB_TREE:
//  					this.setCurrentDomain(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
//  					var currCont = this._app.getCurrentController();
//  					var distController = this._app.getDistributionListController(this._currentDomain);
//  					var searchResults = ZaSearch.searchByDomain(this._currentDomain, [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS],1, ZaAccount.A_uid, true, this._app);
//  					if (currCont) {
//  						currCont.switchToNextView(distController, distController.show, searchResults);
//  					} else {
//  						distController.show(searchResults);
//  					}
//  					break;
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
