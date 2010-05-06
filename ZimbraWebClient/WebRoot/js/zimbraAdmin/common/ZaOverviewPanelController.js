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
* @class ZaOverviewPanelController
* @contructor ZaOverviewPanelController
* Controls the navigation tree.
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaOverviewPanelController = function(appCtxt, container) {
	ZaController.call(this, appCtxt, container,"ZaOverviewPanelController");
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
			var ti1 = new DwtTreeItem({parent:this._cosTi,className:"AdminTreeItem"});			
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
	var busyId = Dwt.getNextId () ;
	var callback = new AjxCallback(this, this.domainSearchCallback,{busyId:busyId});
	var domainListController = ZaApp.getInstance().getDomainListController ();
	
	domainListController._currentQuery = ZaDomain.LOCAL_DOMAIN_QUERY;
	var searchParams = {
			query: domainListController._currentQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:"0",
			sortAscending:"1",
			limit:ZaDomain.MAXSEARCHRESULTS,
			callback:callback,
			controller: this,
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false,
			attrs:[ZaDomain.A_domainName,ZaItem.A_zimbraId]			
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaOverviewPanelController.prototype.domainSearchCallback = 
function (params,resp) {
	try {
		if(params.busyId)
			ZaApp.getInstance().getAppCtxt().getShell().setBusy(false, params.busyId);
				
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaOverviewPanelController.prototype.domainSearchCallback"));
		}
		if(resp.isException()) {
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaOverviewPanelController.prototype.domainSearchCallback");
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false;
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList(ZaDomain);	
			list.loadFromJS(response);
			if(response.more) {
				ZaSettings.HAVE_MORE_DOMAINS = true;
			}
			this.updateDomainList(list);
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaOverviewPanelController.prototype.searchCallback");	
		} else {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.queryParseError, ex);
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
		var savedSearchList = ZaApp.getInstance().getSavedSearchList();
		if(savedSearchList && savedSearchList.length) {
			var cnt = savedSearchList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem({parent:this._savedSearchTi,className:"AdminTreeItem"});			
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
			var ti1 = new DwtTreeItem({parent:this._domainsTi,className:"AdminTreeItem"});			
			ti1.setText(domainList[ix].name);	
			ti1.setImage("Domain");
			ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DOMAIN_VIEW);
			ti1.setData(ZaOverviewPanelController._OBJ_ID, domainList[ix].id);
			this._domainsMap[domainList[ix].id] = ti1;
		}
	}
	list.loadEffectiveRights();
	ZaApp.getInstance()._domainList = list;
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
	this._app = ZaApp.getInstance();
	
//TODO:  ZaSettings.DOMAIN_AUTH_WIZ_ENABLED - LDAPAuthWizard enabled for the domain admin
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this._domainsMap = new Object();
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this._serversMap = new Object();	
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
		this._serversStatsMap = new Object();
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])	
		this._cosMap = new Object();
	
	this._mailqMap = new Object();
		
	if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) 
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
	var mtaList = ZaApp.getInstance().getPostQList().getArray();
	var showAddresses = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
	var showTools = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
	var showConfig = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
	var showMonitoring = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
	if(!showAddresses) {
		for(var i=0;i<ZaSettings.OVERVIEW_ADDRESSES_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_ADDRESSES_ITEMS[i]]) {
				showAddresses = true;
				break;
			}
		}
	}
	if(!showTools) {
		for(var i=0;i<ZaSettings.OVERVIEW_TOOLS_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_TOOLS_ITEMS[i]]) {
				showTools = true;
				break;
			}
		}
	}
	if(!showConfig) {
		for(var i=0;i<ZaSettings.OVERVIEW_CONFIG_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_CONFIG_ITEMS[i]]) {
				showConfig = true;
				break;
			}
		}
	}	
	if(!showMonitoring) {
		for(var i=0;i<ZaSettings.OVERVIEW_MONITORING_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_MONITORING_ITEMS[i]]) {
				showMonitoring = true;
				break;
			}
		}
	}	

	if(!showTools) {
		for(var i=0;i<ZaSettings.OVERVIEW_TOOLS_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_TOOLS_ITEMS[i]]) {
				showTools = true;
				break;
			}
		}
	}
	var ti;
	if(showAddresses) {
		this._addressesTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._addressesTi.enableSelection(false);
		this._addressesTi.setText(ZaMsg.OVP_addresses);
		this._addressesTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADDRESSES);
	    this._addressesTi.setSty
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {	
			this.accountTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem"});
			ti.setText(ZaMsg.OVP_accounts);
			ti.setImage("Account");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
		}
		
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this.aliasTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem"});
			ti.setText(ZaMsg.OVP_aliases);
			ti.setImage("AccountAlias");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ALIASES_LIST_VIEW);
		}
			
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this.dlTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem"});
			ti.setText(ZaMsg.OVP_distributionLists);
			ti.setImage("DistributionList");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW);
		}
		
        if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            this.resourceTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem"});
            ti.setText(ZaMsg.OVP_resources);
            ti.setImage("Resource");
            ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._RESOURCE_VIEW);
        }
		
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = ZaOverviewPanelController.accountListTreeListener;
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ALIASES_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
		ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW] = ZaOverviewPanelController.dlListTreeListener;		
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._RESOURCE_VIEW] = ZaOverviewPanelController.resourceListTreeListener;
        }
    }

	//TODO:  ZaSettings.DOMAIN_AUTH_WIZ_ENABLED - LDAPAuthWizard enabled for the domain admin	
	if(showConfig ) {	
		this._configTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._configTi.enableSelection(false);
		this._configTi.setText(ZaMsg.OVP_configuration);
		this._configTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SYS_CONFIG);	
		
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._cosTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
			this._cosTi.setText(ZaMsg.OVP_cos);
			this._cosTi.setImage("COS");
			this._cosTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_LIST_VIEW);
				
			try {
				//add COS nodes
				var cosList = ZaApp.getInstance().getCosList();
				if(cosList && cosList.size()) {
					var idHash = cosList.getIdHash();
					for(var ix in idHash) {
						var ti1 = new DwtTreeItem({parent:this._cosTi,className:"AdminTreeItem"});			
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
		}
		//TODO:  ZaSettings.DOMAIN_AUTH_WIZ_ENABLED - LDAPAuthWizard enabled for the domain admin
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._domainsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
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
		}
	
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            this._serversTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
            this._serversTi.setText(ZaMsg.OVP_servers);
            this._serversTi.setImage("Server");
            this._serversTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVERS_LIST_VIEW);

            try {
                //add server nodes
    //			DBG.println(AjxDebug.DBG1, "add server nodes ");
                var serverList = ZaApp.getInstance().getServerList().getArray();
                if(serverList && serverList.length) {
                    var cnt = serverList.length;
                    for(var ix=0; ix< cnt; ix++) {
                        var ti1 = new DwtTreeItem({parent:this._serversTi,className:"AdminTreeItem"});
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
        }
        
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._zimletsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
			this._zimletsTi.setText(ZaMsg.OVP_zimlets);
			this._zimletsTi.setImage("Zimlet");
			this._zimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ZIMLET_LIST_VIEW);
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = ZaOverviewPanelController.zimletListTreeListener;					
		}

		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ADMIN_ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._adminZimletsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
			this._adminZimletsTi.setText(ZaMsg.OVP_adminZimlets);
			this._adminZimletsTi.setImage("AdminExtension");
			this._adminZimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW);
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = ZaOverviewPanelController.adminExtListTreeListener;					
		}
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_CONFIG_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {		
			ti = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem"});
			ti.setText(ZaMsg.OVP_global);
			ti.setImage("GlobalSettings");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_SETTINGS);	
	
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_SETTINGS] = ZaOverviewPanelController.globalSettingsTreeListener;				
		}
	}
	if(showMonitoring) {
		this._monitoringTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._monitoringTi.enableSelection(false);	
		this._monitoringTi.setText(ZaMsg.OVP_monitoring);
		this._monitoringTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._MONITORING);
		
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {	
			this.statusTi = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem"});
			this.statusTi.setText(ZaMsg.OVP_status);
			this.statusTi.setImage("Status");
			this.statusTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATUS);
		}
		
//		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        if( ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._statisticsTi = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem"});
			this._statisticsTi.setText(ZaMsg.OVP_statistics);
			this._statisticsTi.setImage("Statistics");
			this._statisticsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS);
		}
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
//        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			try {
				//add server statistics nodes
	//			DBG.println(AjxDebug.DBG1, "add server statistics nodes ");	
				var serverList = ZaApp.getInstance().getServerList().getArray();
				if(serverList && serverList.length) {
					var cnt = serverList.length;
					for(var ix=0; ix< cnt; ix++) {
						var ti1;
						if(this._statisticsTi) 
							ti1 = new DwtTreeItem({parent:this._statisticsTi,className:"AdminTreeItem"});
						else
							ti1 = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem"});
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
		}
		
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATUS] = ZaOverviewPanelController.statusTreeListener;	
		
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) 	
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATISTICS] = ZaOverviewPanelController.statsTreeListener;				
		
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI])
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._STATISTICS_BY_SERVER] = ZaOverviewPanelController.statsByServerTreeListener;						
	}
	
	if(showTools) {
		this._toolsTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._toolsTi.enableSelection(false);	
		this._toolsTi.setText(ZaMsg.OVP_tools);
		this._toolsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._TOOLS);
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.MAILQ_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {		
			try {
				this._postqTi = new DwtTreeItem({parent:this._toolsTi,className:"AdminTreeItem"});
				this._postqTi.setText(ZaMsg.OVP_postq);
				this._postqTi.setImage("Queue");
				this._postqTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_VIEW);
		
				if(mtaList && mtaList.length) {
					var cnt = mtaList.length;
					for(var ix=0; ix< cnt; ix++) {
						var ti1 = new DwtTreeItem({parent:this._postqTi,className:"AdminTreeItem"});			
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
			
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_VIEW] = ZaOverviewPanelController.postqTreeListener;				
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = ZaOverviewPanelController.postqByServerTreeListener;
		}						
	}
		
	//SavedSearches Tree	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		this._savedSearchTi = new DwtTreeItem(tree, null, null, null, null, "overviewHeader");
		this._savedSearchTi.enableSelection(false);
		this._savedSearchTi.setText(ZaMsg.OVP_savedSearches);
		this._savedSearchTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SEARCHES);
		
		try {	
			var savedSearchList = ZaApp.getInstance().getSavedSearchList();
			if(savedSearchList && savedSearchList.length) {
				var cnt = savedSearchList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem({parent:this._savedSearchTi,className:"AdminTreeItem"});			
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

    //add the separater here
    if(this._addressesTi)  {
        this._addressesTi.addSeparator();
        this._addressesTi.setExpanded(true, false);
    }

    if(this._configTi) {
        this._configTi.addSeparator();
        this._configTi.setExpanded(true, false);
    }

    if(this._monitoringTi) {
        this._monitoringTi.addSeparator();
        this._monitoringTi.setExpanded(true, false);
    }

    if(this._toolsTi) {
        this._toolsTi.addSeparator();
        this._toolsTi.setExpanded(true, false);
    }

    if (this._savedSearchTi) {
        this._savedSearchTi.addSeparator();
        if (this._savedSearchMapArr && this._savedSearchMapArr.length > 0) {
            this._savedSearchTi.setExpanded(true, false);
        }
    }
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
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getCosController(),
		 ZaCosController.prototype.show,
		 ZaApp.getInstance().getCosList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	} else {					
		ZaApp.getInstance().getCosController().show(ZaApp.getInstance().getCosList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	}	
}

ZaOverviewPanelController.domainTreeListener = function (ev) {
	var domain = new ZaDomain();
	domain.id = ev.item.getData(ZaOverviewPanelController._OBJ_ID);	
	domain.attrs[ZaItem.A_zimbraId] = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getDomainController(),
		 ZaDomainController.prototype.show, 
		 domain /*ZaApp.getInstance().getDomainList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID))*/);
	} else {	
						
		ZaApp.getInstance().getDomainController().show(domain);
	}
}

ZaOverviewPanelController.serverTreeListener = function (ev) {
	var server = new ZaServer();
	server.id = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	server.attrs[ZaItem.A_zimbraId] = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
//	DBG.println(AjxDebug.DBG1, "ZaOverviewPanelController.serverTreeListener called");
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getServerController(),
		 ZaServerController.prototype.show,
		 server /*ZaApp.getInstance().getServerList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID))*/);
	} else {					
		ZaApp.getInstance().getServerController().show(ZaApp.getInstance().getServerList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
	}
}

ZaOverviewPanelController.statsByServerTreeListener = function (ev) {
	var currentServer = new ZaServer();
	currentServer.id = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	currentServer.attrs[ZaItem.A_zimbraId] = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
	currentServer.load("id", currentServer.id, false, true);
	var curController = ZaApp.getInstance().getCurrentController() ;
	if(curController) {
		curController.switchToNextView(ZaApp.getInstance().getServerStatsController(), ZaServerStatsController.prototype.show,[currentServer,true]);
	} else {
		curController = ZaApp.getInstance().getServerStatsController();			
		curController.show(currentServer,true);
	}
}

ZaOverviewPanelController.statsTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getGlobalStatsController(),ZaGlobalStatsController.prototype.show, null);
	} else {					
		ZaApp.getInstance().getGlobalStatsController().show();
	}
}

ZaOverviewPanelController.globalSettingsTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, ZaApp.getInstance().getGlobalConfig());
	} else {					
		ZaApp.getInstance().getGlobalConfigViewController().show(ZaApp.getInstance().getGlobalConfig());
	}
}



ZaOverviewPanelController.statusTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getStatusViewController(),ZaStatusViewController.prototype.show, null);
	} else {					
		ZaApp.getInstance().getStatusViewController().show();
	}
}

ZaOverviewPanelController.serverListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll([ZaServer.A_description, ZaServer.A_ServiceHostname, ZaItem.A_zimbraId]));
	} else {					
		ZaApp.getInstance().getServerListController().show(ZaServer.getAll([ZaServer.A_description, ZaServer.A_ServiceHostname, ZaItem.A_zimbraId]));
	}
}

ZaOverviewPanelController.domainListTreeListener = function (ev) {
	var domainListController = ZaApp.getInstance().getDomainListController ();
	
	//if we do not have access to domains we will only get our own domain in response anyway, so no need to add a query
	domainListController._currentQuery = ZaDomain.LOCAL_DOMAIN_QUERY;
			
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(domainListController, ZaDomainListController.prototype.show, true);
	} else {					
		domainListController.show(true);
	}

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
	var searchField = ZaApp.getInstance().getSearchListController()._searchField ;
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
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getZimletListController(), ZaZimletListController.prototype.show, ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS));
	} else {
		ZaApp.getInstance().getZimletListController().show(ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS));
	}	
}

ZaOverviewPanelController.adminExtListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getAdminExtListController(), ZaAdminExtListController.prototype.show, ZaZimlet.getAll(ZaZimlet.EXCLUDE_MAIL ));
	} else {
		ZaApp.getInstance().getAdminExtListController().show(ZaZimlet.getAll( ZaZimlet.EXCLUDE_MAIL ));
	}	
}


ZaOverviewPanelController.cosListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getCosListController(), ZaCosListController.prototype.show, true);
	} else {
		ZaApp.getInstance().getCosListController().show(true);
	}
}

ZaOverviewPanelController.postqTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getMTAListController(), ZaMTAListController.prototype.show, ZaMTA.getAll());
	} else {
		ZaApp.getInstance().getMTAListController().show(ZaServer.getAll());
	}
}

ZaOverviewPanelController.postqByServerTreeListener = function (ev) {
	var currentServer = ZaApp.getInstance().getPostQList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getMTAController(), ZaMTAController.prototype.show,currentServer);
	} else {					
		ZaApp.getInstance().getMTAController().show(currentServer);
	}
}


ZaOverviewPanelController.prototype._modifySearchMenuButton = 
function (itemType) {
	if (itemType) {
		var searchListController = ZaApp.getInstance().getSearchListController(); 
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
