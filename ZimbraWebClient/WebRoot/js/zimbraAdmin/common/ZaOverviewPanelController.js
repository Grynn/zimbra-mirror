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
    if (appNewUI)
        return;
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
    if (appNewUI)
        return;
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
    if (appNewUI)
        return;
	if(ev) {
		//add the new ZaDomain to the controlled list
		var detls = ev.getDetails();		
		this.removeCosTreeItems(detls);
	}
}

/**
 * This listener is invoked by any controller that do a search.
 * @param ev
 */
ZaOverviewPanelController.prototype.handleSearchFinished =
function (ev) {
    if (appNewUI) {
        this.refreshSearchTree(ev);
    }
}

ZaOverviewPanelController.prototype.removeCosTreeItems = 
function(detls) {
    if (appNewUI)
        return;
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



ZaOverviewPanelController.prototype.searchDomains = function() {
	var busyId = Dwt.getNextId () ;
	//var callback = new AjxCallback(this, this.domainSearchCallback,{busyId:busyId});
	var domainListController = ZaApp.getInstance().getDomainListController ();
	
//	domainListController._currentQuery = ZaDomain.LOCAL_DOMAIN_QUERY;
    domainListController._currentQuery = "";
    if(!ZaZimbraAdmin.hasGlobalDomainListAccess()) {
        var domainNameList = ZaApp.getInstance()._domainNameList;
        if(!domainNameList || !(domainNameList instanceof Array) || domainNameList.length == 0) {
            ZaApp.getInstance()._domainList =  new ZaItemList(ZaDomain);
            return;
        }
        if(domainNameList && domainNameList instanceof Array) {
            for(var i = 0; i < domainNameList.length; i++)
                domainListController._currentQuery += "(" + ZaDomain.A_domainName + "=" + domainNameList[i] + ")";
            if(domainNameList.length > 1)
                domainListController._currentQuery = "(|" + domainListController._currentQuery + ")";
        }
    }

	var searchParams = {
			query: domainListController._currentQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:"0",
			sortAscending:"1",
			limit:ZaDomain.MAXSEARCHRESULTS,
			attrs:[ZaDomain.A_description, ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType]			
	}
	var resp = ZaSearch.searchDirectory(searchParams);
	this.domainSearchCallback(searchParams, resp);
}

ZaOverviewPanelController.prototype.domainSearchCallback = 
function (params,resp) {
	try {
				
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaOverviewPanelController.prototype.domainSearchCallback"));
		}
		ZaSearch.TOO_MANY_RESULTS_FLAG = false;
		var response = resp.Body.SearchDirectoryResponse;
		var list = new ZaItemList(ZaDomain);	
		list.loadFromJS(response);
		if(response.more) {
			ZaSettings.HAVE_MORE_DOMAINS = true;
		}
		this.updateDomainList(list);
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
    if (appNewUI) {
        var tree =this._overviewPanel.getFolderTree();
        var savedSearchPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_savedSearch]);
        tree.removeAllChild(savedSearchPath);

        try {
            var savedSearchList = ZaApp.getInstance().getSavedSearchList();
            if(savedSearchList && savedSearchList.length) {
                var cnt = savedSearchList.length;
                for(var ix=0; ix< cnt; ix++) {
                    var ti1 = new ZaTreeItemData({
                                        parent:savedSearchPath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch", null,ix+1),
                                        text: savedSearchList[ix].name,
                                        buildPath: this.getSearchItemPath(),
                                        mappingId: ZaZimbraAdmin._SEARCH_HOME_VIEW});
                    ti1.setData("name", savedSearchList[ix].name);
                    ti1.setData("query", savedSearchList[ix].query); //keep the query information here
                    tree.addTreeItemData(ti1);
                }

                var searchRootNode =  tree.getTreeItemByPath (savedSearchPath);
                if (searchRootNode) {
                   // TODO Improve Later
                    var showRootNode = tree.getTreeItemDataByPath(savedSearchPath);
                    var ti, currentAddNode, forceNode, key;
                    for (var i = 0; i < showRootNode.childrenData.size(); i++) {
                        currentAddNode =  showRootNode.childrenData.get(i);
                        if( currentAddNode.forceNode !== undefined)
                            forceNode = currentAddNode.forceNode;
                        else
                            forceNode = currentAddNode.childrenData.size() > 0 ? true: false;
                        ti = new ZaTreeItem({parent: searchRootNode,className:"AdminTreeItem",id:currentAddNode.id, forceNode: forceNode});
                        ti.setCount(currentAddNode.count);
                        ti.setText(currentAddNode.text);
                        ti.setImage(currentAddNode.image);
                        ti.setData(ZaOverviewPanelController._TID, currentAddNode.mappingId);
                        ti.setData("dataItem", currentAddNode);
                        for (key in currentAddNode._data) {
                            ti.setData(key, currentAddNode._data[key]);
                        }
                    }
                    searchRootNode.setExpanded(true);
                }
            }
        } catch (ex) {
            this._handleException(ex, "ZaOverviewPanelController.prototype._buildNewFolderTree", null, false);
        }
        return;
    }
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
	if (appNewUI){
		return; //new UI no need for this
	}

	var domainList = list.getArray();
	for (var key in this._domainsMap) {
		this._domainsTi.removeChild(this._domainsMap[key]);		
	}
	this._domainsMap = new Object();	
	//add domain nodes
	if(domainList && domainList.length) {
		var cnt = domainList.length;
		for(var ix=0; ix< cnt; ix++) {
			var ti1 = new DwtTreeItem({parent:this._domainsTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_CONFIGURATION,ZaId.TREEITEM_DOMAINS,null,ix+1)});
			ti1.setText(domainList[ix].name);	

			var itemType = ( domainList[ix].attrs [ZaDomain.A_domainType] );
			
			if( itemType == ZaDomain.domainTypes.alias ) {
				
					ti1.setImage("DomainAlias"); //Domain Alias
			
			}else { //itemType == ZaDomain.domainTypes.local
				
					ti1.setImage("Domain"); //Real Domain
				
			}
			
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

	this._overviewPanel = new ZaOverviewPanel({
                        parent:         this._container,
                        className:      "OverviewPanel",
                        posStyle:       DwtControl.ABSOLUTE_STYLE,
			id:		ZaId.PANEL_APP //ZaId.getOverviewId(ZaId.PANEL_APP)
	});

	this._overviewPanel.setScrollStyle(DwtControl.SCROLL);
    ZaSearch.loadPredefinedSearch() ;
    if (!appNewUI)
        this._buildFolderTree();
    else
        this._buildNewFolderTree();
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
		this._addressesTi = new DwtTreeItem({parent:tree,className:"overviewHeader",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_ADDRESS, true)});
		this._addressesTi.enableSelection(false);
		this._addressesTi.setText(ZaMsg.OVP_addresses);
		this._addressesTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADDRESSES);

		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {	
			this.accountTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_ADDRESS,null, ZaId.TREEITEM_ACCOUNT)});
			ti.setText(ZaMsg.OVP_accounts);
			ti.setImage("Account");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ACCOUNTS_LIST_VIEW);
		}
		
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this.aliasTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_ADDRESS,null, ZaId.TREEITEM_ALIASES)});
			ti.setText(ZaMsg.OVP_aliases);
			ti.setImage("AccountAlias");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ALIASES_LIST_VIEW);
		}
			
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this.dlTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_ADDRESS,null, ZaId.TREEITEM_DL)});
			ti.setText(ZaMsg.OVP_distributionLists);
			ti.setImage("DistributionList");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW);
		}
		
        if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            this.resourceTi = ti = new DwtTreeItem({parent:this._addressesTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_ADDRESS,null, ZaId.TREEITEM_RESOURCES)});
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

        if(!ZaZimbraAdmin.hasGlobalCOSSListAccess()) {
               var domainNamelist = ZaDomain.getEffectiveDomainList(ZaZimbraAdmin.currentAdminAccount.id);
               ZaApp.getInstance()._domainNameList = domainNamelist;

               var cosNamelist = ZaCos.getEffectiveCosList(ZaZimbraAdmin.currentAdminAccount.id);
               ZaApp.getInstance()._cosNameList = cosNamelist;

        }

	//TODO:  ZaSettings.DOMAIN_AUTH_WIZ_ENABLED - LDAPAuthWizard enabled for the domain admin	
	if(showConfig ) {	
		this._configTi = new DwtTreeItem({parent:tree,className:"overviewHeader",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION, true)});
		this._configTi.enableSelection(false);
		this._configTi.setText(ZaMsg.OVP_configuration);
		this._configTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SYS_CONFIG);	
		
		if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._cosTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_COS)});
			this._cosTi.setText(ZaMsg.OVP_cos);
			this._cosTi.setImage("COS");
			this._cosTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._COS_LIST_VIEW);
				
			try {
				//add COS nodes
				var cosList = ZaApp.getInstance().getCosList();
				if(cosList && cosList.size()) {
					var idHash = cosList.getIdHash();
					var index = 0;
					for(var ix in idHash) {
						var ti1 = new DwtTreeItem({parent:this._cosTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_CONFIGURATION,ZaId.TREEITEM_COS,null,++index)});
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
			this._domainsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_DOMAINS)});
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
        	try {
	        	var serverList = ZaApp.getInstance().getServerList().getArray();
	        	if(serverList && serverList.length) {
	        		var cnt = serverList.length;
	        		this._serversTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_SERVERS)});
	        		if(cnt>1) {
	        			this._serversTi.setText(ZaMsg.OVP_servers);
	        			this._serversTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVERS_LIST_VIEW);
		                for(var ix=0; ix< cnt; ix++) {
		                    var ti1 = new DwtTreeItem({parent:this._serversTi,className:"AdminTreeItem", id:ZaId.getTreeItemId(ZaId.PANEL_CONFIGURATION,ZaId.TREEITEM_SERVERS,null,ix+1)});
		                    ti1.setText(serverList[ix].name);
		                    ti1.setImage("Server");
		                    ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVER_VIEW);
		                    ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
		                    this._serversMap[serverList[ix].id] = ti1;
		                }
		                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVERS_LIST_VIEW] = ZaOverviewPanelController.serverListTreeListener;
	        		} else  {
	        			this._serversTi.setText(ZaMsg.OVP_serverSettings);
	        			this._serversTi.setData(ZaOverviewPanelController._OBJ_ID, serverList[0].id);
	        			this._serversTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SERVER_VIEW);
	        		}
	        		this._serversTi.setImage("Server");
	                //add server nodes
		            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_VIEW] = ZaOverviewPanelController.serverTreeListener;
	        	}
            } catch (ex) {
                this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
            }
        }
        
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._zimletsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_ZIMLETS)});
			this._zimletsTi.setText(ZaMsg.OVP_zimlets);
			this._zimletsTi.setImage("Zimlet");
			this._zimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ZIMLET_LIST_VIEW);

			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = ZaOverviewPanelController.zimletListTreeListener;					
		}

		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ADMIN_ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._adminZimletsTi = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_ADMINEXT)});
			this._adminZimletsTi.setText(ZaMsg.OVP_adminZimlets);
			this._adminZimletsTi.setImage("AdminExtension");
			this._adminZimletsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW);
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = ZaOverviewPanelController.adminExtListTreeListener;					
		}
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_CONFIG_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {		
			ti = new DwtTreeItem({parent:this._configTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_GSET)});
			ti.setText(ZaMsg.OVP_global);
			ti.setImage("GlobalSettings");
			ti.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._GLOBAL_SETTINGS);	
	
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_SETTINGS] = ZaOverviewPanelController.globalSettingsTreeListener;				
		}
	}
	if(showMonitoring) {
		this._monitoringTi = new DwtTreeItem({parent:tree,className:"overviewHeader",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_MONITORING, true)});
		this._monitoringTi.enableSelection(false);	
		this._monitoringTi.setText(ZaMsg.OVP_monitoring);
		this._monitoringTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._MONITORING);
		
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {	
			this.statusTi = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_MONITORING, null,ZaId.TREEITEM_SSTATUS)});
			this.statusTi.setText(ZaMsg.OVP_status);
			this.statusTi.setImage("Status");
			this.statusTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATUS);
		}
		
        if( ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			this._statisticsTi = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_MONITORING, null,ZaId.TREEITEM_SSTATIS)});
			this._statisticsTi.setText(ZaMsg.OVP_statistics);
			this._statisticsTi.setImage("Statistics");
			this._statisticsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._STATISTICS);
		}
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
			try {
				//add server statistics nodes
				var serverList = ZaApp.getInstance().getServerList().getArray();
				if(serverList && serverList.length) {
					var cnt = serverList.length;
					for(var ix=0; ix< cnt; ix++) {
						var ti1;
						if(this._statisticsTi) 
							ti1 = new DwtTreeItem({parent:this._statisticsTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP, ZaId.TREEITEM_SSTATIS, null,ix+1)});
						else
							ti1 = new DwtTreeItem({parent:this._monitoringTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_MONITORING, ZaId.TREEITEM_SSTATIS, null,ix+1)});
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
		this._toolsTi = new DwtTreeItem({parent:tree,className:"overviewHeader",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_TOOLS, true)});
		this._toolsTi.enableSelection(false);	
		this._toolsTi.setText(ZaMsg.OVP_tools);
		this._toolsTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._TOOLS);
		if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.MAILQ_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {		
			try {
				if(mtaList && mtaList.length) {
					var cnt = mtaList.length;
					this._postqTi = new DwtTreeItem({parent:this._toolsTi,className:"AdminTreeItem"});
					this._postqTi.setText(ZaMsg.OVP_postq);
					this._postqTi.setImage("Queue");
					if(cnt>1) {
						for(var ix=0; ix< cnt; ix++) {
							var ti1 = new DwtTreeItem({parent:this._postqTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_TOOLS, null,ix+1)});			
							ti1.setText(mtaList[ix].name);	
							ti1.setImage("Queue");
							ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW);
							ti1.setData(ZaOverviewPanelController._OBJ_ID, mtaList[ix].id);
							this._mailqMap[mtaList[ix].id] = ti1;
						}
						this._postqTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_VIEW);
						ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_VIEW] = ZaOverviewPanelController.postqTreeListener;
					} else {
						this._postqTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW);
						this._postqTi.setData(ZaOverviewPanelController._OBJ_ID, mtaList[0].id);
					}
					ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = ZaOverviewPanelController.postqByServerTreeListener;
				}
			} catch (ex) {
				this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
			}
			
			
		}						
	}
		
	//SavedSearches Tree	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		this._savedSearchTi = new DwtTreeItem({parent:tree,className:"overviewHeader",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_SEARCHS, true)});
		this._savedSearchTi.enableSelection(false);
		this._savedSearchTi.setText(ZaMsg.OVP_savedSearches);
		this._savedSearchTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._SEARCHES);
		
		try {	
			var savedSearchList = ZaApp.getInstance().getSavedSearchList();
			if(savedSearchList && savedSearchList.length) {
				var cnt = savedSearchList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem({parent:this._savedSearchTi,className:"AdminTreeItem",id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_SEARCHS, null,ix+1)});			
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

ZaId.PANEL_HOME = "Home";

ZaOverviewPanelController.prototype._buildNewFolderTree =
function() {
	var tree = this._overviewPanel.getFolderTree();
    var parentPath;
	var l = new AjxListener(this, this._overviewTreeListener);
	tree.addSelectionListener(l);
    var mtaList = ZaApp.getInstance().getPostQList().getArray();
    var showMonitor = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
    var showManageAccount = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
    var showAdministration = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];
    var showTool = ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI];

	if(!showMonitor) {
		for(var i=0;i<ZaSettings.OVERVIEW_MONITORING_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_MONITORING_ITEMS[i]]) {
				showMonitor = true;
				break;
			}
		}
	}

	if(!showManageAccount) {
		for(var i=0;i<ZaSettings.OVERVIEW_MANAGER_ACCOUNT_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_MANAGER_ACCOUNT_ITEMS[i]]) {
				showManageAccount = true;
				break;
			}
		}
	}

	if(!showAdministration) {
		for(var i=0;i<ZaSettings.OVERVIEW_ADMIN_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_ADMIN_ITEMS[i]]) {
				showAdministration = true;
				break;
			}
		}
	}

	if(!showTool) {
		for(var i=0;i<ZaSettings.OVERVIEW_TOOLS_ITEMS.length;i++) {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.OVERVIEW_TOOLS_ITEMS[i]]) {
				showTool = true;
				break;
			}
		}
	}
    //
    // There is no ACL for Download Page in the tool tree items. So tool will be shown here.
    //showTool = true;
    // Home is always added;
    var home = new ZaTreeItemData({parent:"",
                                   id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME, true),
                                   text:ZaMsg.OVP_home,
                                   mappingId:ZaZimbraAdmin._HOME_VIEW,
                                   image:"Home"
                                  });

    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._HOME_VIEW] = ZaOverviewPanelController.homeTreeListener;
    tree.setRootData(home);

    // Section Monitor Start
    if (showMonitor) {

        // Add Monitor Home Page
        var mi = new ZaTreeItemData({
                                        parent:ZaMsg.OVP_home,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "monHV"),
                                        text: ZaMsg.OVP_monitor,
                                        defaultSelectedItem: 1,
                                        className: "AdminHomeTreeItem",
                                        mappingId: ZaZimbraAdmin._MONITOR_HOME_VIEW,
                                        image:"Monitor"
                                    });
        tree.addTreeItemData(mi);

        // Add Monitor/Status
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            var ti = new ZaTreeItemData({
                                            parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor]),
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,"monHV",null, "overviewStatusHV"),
                                            text: ZaMsg.OVP_status,
                                            mappingId: ZaZimbraAdmin._SERVER_STATUS_VIEW
                                        });
            tree.addTreeItemData(ti);
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_STATUS_VIEW] = ZaOverviewPanelController.statusTreeListener;
        }
        // Add Monitor/Statistics
        //insert all the statistics view's subs to the /ZaMsg.OVP_home/ZaMsg.OVP_monitor/, and statistics view itself is the container of those subs
        if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            this.addSubTabsToParentTreeItem(mi, ZaGlobalStatsView.prototype.getTabChoices(), ZaZimbraAdmin._SERVER_STATISTICS_VIEW, true);
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_STATISTICS_VIEW] = ZaOverviewPanelController.statsTreeListener;

            ti = new ZaTreeItemData({
                                        parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor]),
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"monHV",null, "serverListForStatisticsHV"),
                                        text: ZaMsg.OVP_statistics,
                                        canShowOnRoot: false,
                                        mappingId: ZaZimbraAdmin._SERVER_LIST_FOR_STATISTICS_VIEW
                                        });
            tree.addTreeItemData(ti);
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_LIST_FOR_STATISTICS_VIEW] = ZaOverviewPanelController.serverListForStatisticsTreeListener;
        }
        // Add Monitor/Mail Queue
        if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.MAILQ_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            try {
                if(mtaList && mtaList.length) {
                    var cnt = mtaList.length;
                    var postTi;
                    if(cnt>1) {
                        postTi = new ZaTreeItemData({
                                        parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor]),
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"monHV",null, "postQueueHV"),
                                        text: ZaMsg.OVP_postq,
                                        mappingId:  ZaZimbraAdmin._POSTQ_VIEW});
                        tree.addTreeItemData(postTi);
                        for(var ix=0; ix< cnt; ix++) {
                            var ti1 = new ZaTreeItemData({
                                        parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor, ZaMsg.OVP_postq]),
                                        id:DwtId._makeId(postTi.id, ix + 1),
                                        text: mtaList[ix].name,
                                        mappingId: ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW});;
                            ti1.setData(ZaOverviewPanelController._OBJ_ID, mtaList[ix].id);
                            this._mailqMap[mtaList[ix].id] = ti1;
                            tree.addTreeItemData(ti1);
                        }
                        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_VIEW] = ZaOverviewPanelController.postqTreeListener;
                    } else {
                        postTi = new ZaTreeItemData({
                                        parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_monitor]),
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"monHV",null, "postQueueHV"),
                                        text: ZaMsg.OVP_postq,
                                        mappingId: ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW});
                        postTi.setData(ZaOverviewPanelController._OBJ_ID, mtaList[0].id);
                        tree.addTreeItemData(postTi);
                    }
                    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = ZaOverviewPanelController.postqByServerTreeListener;
                }
            } catch (ex) {
                this._handleException(ex, "ZaOverviewPanelController.prototype._buildFolderTree", null, false);
            }


        }
    }
    // Section Manager Account Start
    if (showManageAccount) {
        var accountMrgCallback = new AjxCallback(this, ZaOverviewPanelController.manageAccountTreeListener);
        var accountMgr = new ZaTreeItemData({
                                        parent:ZaMsg.OVP_home,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "manActHV"),
                                        text: ZaMsg.OVP_manageAccounts,
                                        className: "AdminHomeTreeItem",
                                        callback: accountMrgCallback,
                                        defaultSelectedItem: 1,
                                        //mappingId: ZaZimbraAdmin._MANAGE_ACCOUNT_HOME_VIEW,
                                        image: "MangeAccounts"
                                        });
        //ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._MANAGE_ACCOUNT_HOME_VIEW] = ZaOverviewPanelController.manageAccountTreeListener;
        tree.addTreeItemData(accountMgr);
        if(accountMgr) {
            var refpath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_manageAccounts]);
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                var acctitem =  new ZaTreeItemData({
                                        parent:refpath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "actLstHV"),
                                        text: ZaMsg.OVP_accounts,
                                        count: 0,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._ACCOUNTS_LIST_VIEW});
                acctitem.setData("TreeItemType", ZaItem.ACCOUNT);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = ZaOverviewPanelController.accountListTreeListener;
                tree.addTreeItemData(acctitem);
            }

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                var aliaitem =  new ZaTreeItemData({
                                        parent:refpath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "aliaLstHV"),
                                        text: ZaMsg.OVP_aliases,
                                        count: 0,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._ALIASES_LIST_VIEW});
                aliaitem.setData("TreeItemType", ZaItem.ALIAS);
                tree.addTreeItemData(aliaitem);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ALIASES_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
            }

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                var dlitem =  new ZaTreeItemData({
                                        parent:refpath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "dlLstHV"),
                                        text: ZaMsg.OVP_distributionLists,
                                        count: 0,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW});
                dlitem.setData("TreeItemType", ZaItem.DL);
                tree.addTreeItemData(dlitem);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW] = ZaOverviewPanelController.dlListTreeListener;
            }

            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                var resourceitem =  new ZaTreeItemData({
                                        parent:refpath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "resLstHV"),
                                        text: ZaMsg.OVP_resources,
                                        count: 0,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._RESOURCE_VIEW});
                resourceitem.setData("TreeItemType", ZaItem.RESOURCE);
                tree.addTreeItemData(resourceitem);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._RESOURCE_VIEW] = ZaOverviewPanelController.resourceListTreeListener;
            }
        }
    }

    // Section Configuration Start
    if (showAdministration) {
        ti = this._configure = new ZaTreeItemData({
                                        parent:ZaMsg.OVP_home,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "adminHV"),
                                        text: ZaMsg.OVP_configure,
                                        defaultSelectedItem: 1,
                                        className: "AdminHomeTreeItem",
                                        mappingId: ZaZimbraAdmin._ADMINISTRATION_HOME_VIEW,
                                        image: "Administration"
                                        });
        tree.addTreeItemData(ti);

        if (this._configure) {
            parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure]);
            if(!ZaZimbraAdmin.hasGlobalCOSSListAccess()) {
                   var domainNamelist = ZaDomain.getEffectiveDomainList(ZaZimbraAdmin.currentAdminAccount.id);
                   ZaApp.getInstance()._domainNameList = domainNamelist;

                   var cosNamelist = ZaCos.getEffectiveCosList(ZaZimbraAdmin.currentAdminAccount.id);
                   ZaApp.getInstance()._cosNameList = cosNamelist;

            }
            // Add Configuration /Cos
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                ti = new ZaTreeItemData({
                                            parent:parentPath,
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_COS),
                                            text: ZaMsg.OVP_cos,
                                            canShowOnRoot: false,
                                            forceNode: false,
                                            mappingId: ZaZimbraAdmin._COS_LIST_VIEW});
                tree.addTreeItemData(ti);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_LIST_VIEW] = ZaOverviewPanelController.cosListTreeListener;
            }

            // Add Configuration /Domain
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                ti = new ZaTreeItemData({
                                            parent:parentPath,
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_DOMAINS),
                                            text: ZaMsg.OVP_domains,
                                            canShowOnRoot: false,
                                            forceNode: false,
                                            mappingId: ZaZimbraAdmin._DOMAINS_LIST_VIEW});
                tree.addTreeItemData(ti);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAINS_LIST_VIEW] = ZaOverviewPanelController.domainListTreeListener;
            }

            // Add Configuration /Sever Setting
            if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                try {
                    var serverList = ZaApp.getInstance().getServerList().getArray();
                    if(serverList && serverList.length) {
                        var cnt = serverList.length;
                        var serverTi;
                        if(cnt>0) {
                            serverTi = new ZaTreeItemData({
                                            parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure]),
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, "serverHV"),
                                            text: ZaMsg.OVP_servers,
                                            canShowOnRoot: false,
                                            forceNode: false,
                                            mappingId:  ZaZimbraAdmin._SERVERS_LIST_VIEW});
                            tree.addTreeItemData(serverTi);
                            for(var ix=0; ix< cnt; ix++) {
                                var ti1 = new ZaTreeItemData({
                                            parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_servers]),
                                            id:DwtId._makeId(serverTi.id, ix + 1),
                                            text: serverList[ix].name,
                                            image: "Server",
                                            mappingId: ZaZimbraAdmin._SERVER_VIEW});;
                                ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
                                tree.addTreeItemData(ti1);
                            }
                            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVERS_LIST_VIEW] = ZaOverviewPanelController.serverListTreeListener;
                        } else {
                            serverTi = new ZaTreeItemData({
                                            parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure]),
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, "serverHV"),
                                            text: ZaMsg.OVP_serverSettings,
                                            image: "Server",
                                            mappingId: ZaZimbraAdmin._SERVER_VIEW});
                            serverTi.setData(ZaOverviewPanelController._OBJ_ID, serverList[0].id);
                            tree.addTreeItemData(serverTi);
                        }
                        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SERVER_VIEW] = ZaOverviewPanelController.serverTreeListener;
                    }
                } catch (ex) {
                    this._handleException(ex, "ZaOverviewPanelController.prototype._buildNewFolderTree", null, false);
                }
            }

            // Add Configuration / Global Settings
            if (ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_CONFIG_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                ti = new ZaTreeItemData({
                                            parent:parentPath,
                                            id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_GSET),
                                            text: ZaMsg.OVP_global,
                                            mappingId: ZaZimbraAdmin._GLOBAL_SETTINGS});
                tree.addTreeItemData(ti);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._GLOBAL_SETTINGS] = ZaOverviewPanelController.globalSettingsTreeListener;
            }

            // Add Configuration /Zimlets
            if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                ti = new ZaTreeItemData({
                                        parent:parentPath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_ZIMLETS),
                                        text: ZaMsg.OVP_zimlets,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._ZIMLET_LIST_VIEW});
                tree.addTreeItemData(ti);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = ZaOverviewPanelController.zimletListTreeListener;
            }

            // Add Configuration /Admin Ext
            if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ADMIN_ZIMLET_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
                ti = new ZaTreeItemData({
                                        parent:parentPath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_CONFIGURATION,null, ZaId.TREEITEM_ADMINEXT),
                                        text: ZaMsg.OVP_adminZimlets,
                                        canShowOnRoot: false,
                                        forceNode: false,
                                        mappingId: ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW});
                tree.addTreeItemData(ti);
                ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = ZaOverviewPanelController.adminExtListTreeListener;
            }
        }
    }

    if (showTool) {
        // Section Tool and Migration Start
        ti = new ZaTreeItemData({
                                        parent:ZaMsg.OVP_home,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "magHV"),
                                        text: ZaMsg.OVP_toolMig,
                                        className: "AdminHomeTreeItem",
                                        defaultSelectedItem: 1,
                                        mappingId: ZaZimbraAdmin._MIGRATION_HOME_VIEW,
                                        image: "ToolsAndMigration"
                                        });

        tree.addTreeItemData(ti);

        ti = new ZaTreeItemData({
                                        parent: ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_toolMig]),
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"magHV",null, "download"),
                                        text: ZaMsg.goToMigrationWiz,
                                        mappingId: ZaZimbraAdmin._DOWNLOAD_VIEW
                                        });
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOWNLOAD_VIEW] = ZaOverviewPanelController.downloadTreeListener;
        tree.addTreeItemData(ti);
    }

    // Section Search Start
    ti = new ZaTreeItemData({
                                    parent:ZaMsg.OVP_home,
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,ZaId.PANEL_HOME,null, "searchHV"),
                                    text: ZaMsg.OVP_search,
                                    className: "AdminHomeTreeItem",
                                    mappingId: ZaZimbraAdmin._SEARCH_HOME_VIEW,
                                    image: "SearchAll"
                                    });

    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SEARCH_HOME_VIEW] = ZaOverviewPanelController.newSearchListTreeListener;
    tree.addTreeItemData(ti);

    parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search]);
    var currentSearchTi = new ZaTreeItemData({
                                    parent:parentPath,
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"searchHV",null, "currentSearch"),
                                    text: ZaMsg.OVP_search,
                                    defaultSelectedItem: 1,
                                    image: "SearchAll",
                                    mappingId: ZaZimbraAdmin._SEARCH_HOME_VIEW});
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SEARCH_HOME_VIEW] = ZaOverviewPanelController.newSearchListTreeListener;
    tree.addTreeItemData(currentSearchTi);
    this.setSearchItemPath (ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]));

    ti = new ZaTreeItemData({
                                    parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]),
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch",null, "allResult"),
                                    text: ZaMsg.OVP_allSearchResult,
                                    mappingId: ZaZimbraAdmin._SEARCH_RESULT_VIEW});
    tree.addTreeItemData(ti);
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SEARCH_RESULT_VIEW] = ZaOverviewPanelController.searchResultTreeListener;
    ti = new ZaTreeItemData({
                                    parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]),
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch",null, "accountResult"),
                                    text: ZaMsg.OVP_accountSearchResult,
                                    mappingId: ZaZimbraAdmin._SEARCH_RESULT_VIEW});
    ti.setData("TreeItemType", ZaItem.ACCOUNT);
    tree.addTreeItemData(ti);

    ti = new ZaTreeItemData({
                                    parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]),
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch",null, "domainResult"),
                                    text: ZaMsg.OVP_domainSearchResult,
                                    mappingId: ZaZimbraAdmin._SEARCH_RESULT_VIEW});
    ti.setData("TreeItemType", ZaItem.DOMAIN);
    tree.addTreeItemData(ti);

    ti = new ZaTreeItemData({
                                    parent:ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]),
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch",null, "dlResult"),
                                    text: ZaMsg.OVP_dlSearchResult,
                                    mappingId: ZaZimbraAdmin._SEARCH_RESULT_VIEW});
    ti.setData("TreeItemType", ZaItem.DL);
    tree.addTreeItemData(ti);

    var searchOptionTi = new ZaTreeItemData({
                                    parent:parentPath,
                                    canShowOnRoot: false,
                                    id:ZaId.getTreeItemId(ZaId.PANEL_APP,"searchHV",null, "searchOption"),
                                    text: ZaMsg.OVP_searchOption });
    tree.addTreeItemData(searchOptionTi);
    currentSearchTi.addSilbings(searchOptionTi);
    // Add Option here.
    var optionBasePath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_searchOption]);
    var searchOptionTreeItem = ZaApp.getInstance().getSearchBuilderController().getFilterTreeItems();
    for (var i = 0; i < searchOptionTreeItem.length; i++) {
        ti = new ZaTreeItemData({
                                parent:optionBasePath,
                                canShowOnRoot: false,
                                id:ZaId.getTreeItemId(ZaId.PANEL_APP,"searchOption",null, i + 1),
                                mappingId: ZaZimbraAdmin._SEARCH_FILTER_VIEW,
                                buildPath: this.getSearchItemPath(),
                                text: searchOptionTreeItem[i].text });
        ti.setData("filterType", searchOptionTreeItem[i].filterType);
        tree.addTreeItemData(ti);
    }
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._SEARCH_FILTER_VIEW] = ZaSearchBuilderController.searchFilterTreeListener;

	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SAVE_SEARCH] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
        var savedSearchTi = new ZaTreeItemData({
                                        parent:parentPath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"searchHV",null, "savedSearch"),
                                        canShowOnRoot: false,
                                        text: ZaMsg.OVP_savedSearch });
        tree.addTreeItemData(savedSearchTi);
        currentSearchTi.addSilbings(savedSearchTi);
        try {
            var savedSearchList = ZaApp.getInstance().getSavedSearchList();
            if(savedSearchList && savedSearchList.length) {
                var savedSearchPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_savedSearch]);
                this._savedSearchPath = savedSearchPath;
                var cnt = savedSearchList.length;
                for(var ix=0; ix< cnt; ix++) {
                    var ti1 = new ZaTreeItemData({
                                        parent:savedSearchPath,
                                        id:ZaId.getTreeItemId(ZaId.PANEL_APP,"currentSearch", null,ix+1),
                                        text: savedSearchList[ix].name,
                                        buildPath: this.getSearchItemPath(),
                                        mappingId: ZaZimbraAdmin._SEARCH_HOME_VIEW});
                    ti1.setData("name", savedSearchList[ix].name);
                    ti1.setData("query", savedSearchList[ix].query); //keep the query information here
                    tree.addTreeItemData(ti1);
                }
            }
        } catch (ex) {
            this._handleException(ex, "ZaOverviewPanelController.prototype._buildNewFolderTree", null, false);
        }
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
}


ZaOverviewPanelController.prototype._overviewTreeListener =
function(ev) {
	try {
		var eventHandler = null;

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
				if (treeItemType == ZaZimbraAdmin._SEARCH_LIST_VIEW ||
                    treeItemType == ZaZimbraAdmin._SEARCH_HOME_VIEW) { //saved search item is actioned.
					//if(window.console && window.console.log) console.debug("Saved Search tree Item is actioned.") ;
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

ZaOverviewPanelController.prototype.setSearchItemPath = function (path) {
    this._searchItemPath = path;
}

ZaOverviewPanelController.prototype.getSearchItemPath = function (path) {
    return this._searchItemPath;
}

/* default tree listeners */
ZaOverviewPanelController.homeTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getHomeViewController(),ZaHomeController.prototype.show, null);
	} else {
		ZaApp.getInstance().getHomeViewController().show();
	}
}

ZaOverviewPanelController.cosTreeListener = function (ev) {
    var cos = ZaApp.getInstance().getCosList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getCosController(),
		 ZaCosController.prototype.show,
		 cos);
	} else {					
		ZaApp.getInstance().getCosController().show(cos);
	}
    if(appNewUI) {
        var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_cos]);
        var skipHistory = ev.item.getData("skipHistory");
        ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, cos.name, null, (skipHistory == "TRUE"), false, cos);
    }
}

ZaOverviewPanelController.domainTreeListener = function (ev) {
	var domain = new ZaDomain();
	domain.id = ev.item.getData(ZaOverviewPanelController._OBJ_ID);	
	domain.attrs[ZaItem.A_zimbraId] = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
    domain.load ("id", domain.id) ;

    var isLocal = (domain.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.local) ;
    var isAlias = (domain.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.alias) ;

	if(ZaApp.getInstance().getCurrentController()) {
        if (isLocal) {
            ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getDomainController(),
             ZaDomainController.prototype.show,
             domain /*ZaApp.getInstance().getDomainList(true).getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID))*/);
        } else if (isAlias) {
            ZaApp.getInstance().getDomainAliasWizard(true).editDomainAlias (domain, false) ;
        }
	} else {
        if (isLocal) {
		    ZaApp.getInstance().getDomainController().show(domain);
        } else if (isAlias) {
            ZaApp.getInstance().getDomainAliasWizard(true).editDomainAlias (domain, false) ;
        }
	}

    if (appNewUI) {
        var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure, ZaMsg.OVP_domains]);
        var skipHistory = ev.item.getData("skipHistory");
        ZaZimbraAdmin.getInstance().getOverviewPanelController().addObjectItem(parentPath, domain.name, null, (skipHistory == "TRUE"), false, domain);
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

ZaOverviewPanelController.statsByServerTabTreeListener = function (ev) {
	//only apply to new UI

	var curController = ZaApp.getInstance().getCurrentController();
	if(!curController) {
		var viewId = ev.item.getData("viewId");
		var currentView = ZaApp.getInstance().getAppViewMgr().getViewContentById(viewId);
		curController = ZaApp.getInstance().getServerStatsController(viewId);
	}

	ZaOverviewPanelController.switchToSubTabForNonXFormView(curController, ev.item);
}


ZaOverviewPanelController.statsByServerTreeListener = function (ev) {
	var currentServer = new ZaServer();

	if (!appNewUI){
		var serverId = ev.item.getData(ZaOverviewPanelController._OBJ_ID);
		currentServer.id = currentServer.attrs[ZaItem.A_zimbraId] = serverId;
		currentServer.load("id", serverId, false, true);

		var currentController = ZaApp.getInstance().getCurrentController() ;
		if(currentController) {
			currentController.switchToNextView(ZaApp.getInstance().getServerStatsController(), ZaServerStatsController.prototype.show,[currentServer,true]);
		} else {
			currentController = ZaApp.getInstance().getServerStatsController();
			currentController.show(currentServer,true);
		}
	}
	else {
		var viewId = ev.item.getData("viewId");
		ZaApp.getInstance().getAppViewMgr().pushView(viewId);
		var currentView = ZaApp.getInstance().getAppViewMgr().getViewContentById(viewId);
		var currentController = ZaApp.getInstance().getControllerById(viewId);
		var currentObject = currentView._containedObject;
		var serverId = currentObject.id;
		currentServer.id = currentServer.attrs[ZaItem.A_zimbraId] = serverId;
		currentServer.load("id", serverId, false, true);

		currentController.show(currentServer,true);
	}

}

ZaOverviewPanelController.statusTreeListener = function (ev) {
    if(ZaApp.getInstance().getCurrentController()) {
        ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getStatusViewController(),ZaStatusViewController.prototype.show, null);
    } else {
        ZaApp.getInstance().getStatusViewController().show();
    }
}

ZaOverviewPanelController.statsTreeListener = function (ev) {
    var curController = ZaApp.getInstance().getCurrentController();
    var statsController = ZaApp.getInstance().getGlobalStatsController();
    if(curController) {
        curController.switchToNextView(statsController, ZaGlobalStatsController.prototype.show, null);
    } else {
        statsController.show();
    }
    if (appNewUI) {
        ZaOverviewPanelController.switchToSubTabForNonXFormView(statsController, ev.item);
    }
}

ZaOverviewPanelController.switchToSubTabForNonXFormView = function (controllerOfView, treeItem) {

    var stepValue = treeItem.getData("tabValue");
    var view = ZaApp.getInstance().getAppViewMgr().getViewContentById(controllerOfView.getContentViewId());
    view.switchToTab(stepValue);    
    //controllerOfView._contentView.switchToTab();

}

ZaOverviewPanelController.serverListForStatisticsTreeListener = function (ev) {
//this list only shows in new UI
    var curController = ZaApp.getInstance().getCurrentController();
    var serverStatsListController = ZaApp.getInstance().getServerStatsListController();
    var allServersList = ZaServer.getAll([ZaServer.A_description, ZaServer.A_ServiceHostname, ZaItem.A_zimbraId]);

    if(curController) {
        curController.switchToNextView(serverStatsListController, ZaServerStatsListController.prototype.show, allServersList);
    } else {
        serverStatsListController.show(allServersList);
    }
}

ZaOverviewPanelController.serverListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll([ZaServer.A_description, ZaServer.A_ServiceHostname, ZaItem.A_zimbraId]));
	} else {					
		ZaApp.getInstance().getServerListController().show(ZaServer.getAll([ZaServer.A_description, ZaServer.A_ServiceHostname, ZaItem.A_zimbraId]));
	}
}

ZaOverviewPanelController.globalSettingsTreeListener = function (ev) {
    if(ZaApp.getInstance().getCurrentController()) {
        ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, ZaApp.getInstance().getGlobalConfig());
    } else {
        ZaApp.getInstance().getGlobalConfigViewController().show(ZaApp.getInstance().getGlobalConfig());
    }
    if (appNewUI) {
        var parentPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_configure]);
        var name = ev.item.getText();
        this.addObjectItem(parentPath, name, undefined, true, true, ev.item);
    }
}

ZaOverviewPanelController.domainListTreeListener = function (ev) {
	var domainListController = ZaApp.getInstance().getDomainListController ();

	//if we do not have access to domains we will only get our own domain in response anyway, so no need to add a query
//	domainListController._currentQuery = ZaDomain.LOCAL_DOMAIN_QUERY;
	domainListController._currentQuery = "";
    if(appNewUI) {
        var extquery = null;
        var actionType = ev.item.getData(ZaOverviewPanelController._TID);
        if(actionType == ZaZimbraAdmin._COS_DOMAIN_LIST_VIEW) {
            var cos = ev.item.getData("cosItem");
            extquery = "(" + ZaDomain.A_domainDefaultCOSId + "=" + cos.id + ")";
            if(cos.name == "default") {
                extquery = "(|(!(" + ZaDomain.A_domainDefaultCOSId + "=*))" + extquery + ")";
            }
        } else if(actionType == ZaZimbraAdmin._DOMAIN_ALIAS_LIST_VIEW) {
            var domain = ev.item.getData("domainItem");
            extquery = "(" + ZaDomain.A_zimbraDomainAliasTargetId + "=" + domain.id + ")";
        }

        if(extquery)
        domainListController._currentQuery = extquery;
    }

        if(!ZaZimbraAdmin.isGlobalAdmin()) {
            var domainNameList = ZaApp.getInstance()._domainNameList;
            if(domainNameList && domainNameList instanceof Array && domainNameList.length > 0) {
                for(var i = 0; i < domainNameList.length; i++)
                   domainListController._currentQuery += "(" + ZaDomain.A_domainName + "=" + domainNameList[i] + ")";
                if(domainNameList.length > 1)
                   domainListController._currentQuery = "(|" + domainListController._currentQuery + ")";
            }
        }

	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(domainListController, ZaDomainListController.prototype.show, true);
	} else {					
		domainListController.show(true);
	}

	this._modifySearchMenuButton(ZaItem.DOMAIN) ;
}

ZaOverviewPanelController.aliasListTreeListener = function (ev) {
    if(appNewUI && (ev.item.getData(ZaOverviewPanelController._TID) == ZaZimbraAdmin._ACCOUNT_ALIAS_LIST_VIEW) ||
                    ev.item.getData(ZaOverviewPanelController._TID) == ZaZimbraAdmin._DL_ALIAS_LIST_VIEW) {
        var targetId = ev.item.getData("aliasTargetId");
        var extquery = "("+ ZaAlias.A_AliasTargetId + "=" + targetId + ")";
        this._showAccountsView(ZaItem.ALIAS,ev, extquery);
    } else
        this._showAccountsView(ZaItem.ALIAS,ev);

    this._modifySearchMenuButton(ZaItem.ALIAS);
}

ZaOverviewPanelController.dlListTreeListener = function (ev) {
    var dls = ev.item.getData(ZaAccount.A2_memberOf);
    if(appNewUI && dls) {
        var direct_dls = dls[ZaAccount.A2_directMemberList];
        var indirect_dls = dls[ZaAccount.A2_indirectMemberList];

        var extquery = "";
        var id = "";
        for (var i = 0; i < (direct_dls.length + indirect_dls.length); i++) {
            if (i < direct_dls.length) {
                id = direct_dls[i].id;
            } else {
                id = indirect_dls[i - direct_dls.length].id;
            }
            extquery += "("+ ZaItem.A_zimbraId + "=" + id + ")";
        }
        extquery = "(|" + extquery + ")";
        this._showAccountsView(ZaItem.DL,ev, extquery);
    } else
	    this._showAccountsView(ZaItem.DL,ev);
	this._modifySearchMenuButton(ZaItem.DL) ;
}

ZaOverviewPanelController.accountListTreeListener = function (ev) {
    if(appNewUI && ev.item.getData(ZaOverviewPanelController._TID) == ZaZimbraAdmin._COS_ACCOUNT_LIST_VIEW) {
        var cos = ev.item.getData("cosItem");
        var extquery = "(" + ZaAccount.A_COSId + "=" + cos.id + ")";
        if(cos.name == "default") {
            extquery = "(|(!(" + ZaAccount.A_COSId + "=*))" + extquery + ")";
        }
        this._showAccountsView(ZaItem.ACCOUNT,ev, extquery);
    } else
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
		//if(window.console && window.console.log) console.debug("Run the saved search ...") ;
        if (query)
		    searchField.selectSavedSearch(name, query);
        else
            searchField.invokeCallback(); // Use the value in the current search fields;
	}else if (ev.detail == DwtTree.ITEM_ACTIONED){
		searchField._currentSavedSearch = {name: name, query: query};
		searchField.getSavedSearchActionMenu().popup(0, ev.docX, ev.docY);
	}
}

ZaOverviewPanelController.newSearchListTreeListener = function (ev) {
    var tree = this.getOverviewPanel().getFolderTree();
    var currentPath = tree.getABPath(ev.item.getData("dataItem"));
    if (currentPath ==  ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search])){
        var  searchPath = this.getSearchItemPath();
        tree.setSelectionByPath(searchPath, true, true);
    }
    var slController =  ZaApp.getInstance().getSearchListController();
	var searchField =   slController._searchField ;
	var name = ev.item.getData("name") ;
	var query = ev.item.getData("query");
	if (ev.detail == DwtTree.ITEM_SELECTED) {
		//if(window.console && window.console.log) console.debug("Run the saved search ...") ;
        if (query) {
            /*
            TODO:
            Improve it in D3
            var searchText = ZaMsg.OVP_search + " : " + name;
            var newPath = tree.renameTreeItem(this.getSearchItemPath(), searchText);
            this.setSearchItemPath(newPath);
            */
            searchField.setCurrentSavedSearch({name: name, query: query});
		    searchField.selectSavedSearch(name, query);
        } else {
            searchField.setCurrentSavedSearch ({});
            var searchParams = searchField.getCurrentSearchQuery();
            var displayName = searchField.getSearchFieldElement().value;
            if(searchField.searchSelectedType && searchField.searchSelectedType.length > 0){
               displayName += " In "+searchField.searchSelectedType;
            }

            var params = {
                  type:1,
                  unique:true,
                  disableForSearch: false,
                  query:searchParams.query,
                  searchType:searchParams.types,
                  displayName:displayName
            };
            if (!slController._uiContainer)
                slController._show();

            slController._uiContainer.removeAllBubbles(true);
            slController._uiContainer.addBubble(params);
        }
	} if (ev.detail == DwtTree.ITEM_ACTIONED && query){
		searchField._currentSavedSearch = {name: name, query: query};
		searchField.getSavedSearchActionMenu().popup(0, ev.docX, ev.docY);
	}
}

ZaOverviewPanelController.searchResultTreeListener = function (ev) {
	if (ev.detail == DwtTree.ITEM_SELECTED) {
        var itemType = ev.item.getData("TreeItemType");
        var slController = ZaApp.getInstance().getSearchListController();
        slController.reset();
        var searchField = slController._searchField;
        var contentView = slController._contentView;
        var skipNotify = false;
       if (itemType == ZaItem.ACCOUNT ) {
            if(searchField._containedObject[ZaSearch.A_fAccounts] == "FALSE"){
                contentView.set();
                skipNotify = true;
            }
            else
               searchField.accFilterSelectedFromResults();
        } else if (itemType == ZaItem.DOMAIN  ) {
            if(searchField._containedObject[ZaSearch.A_fDomains]== "FALSE" ){
                contentView.set();
                skipNotify = true;
            }
            else
                searchField.domainFilterSelectedFromResults();
        } else if (itemType == ZaItem.DL ) {
            if(searchField._containedObject[ZaSearch.A_fdistributionlists]== "FALSE"){
                contentView.set();
                skipNotify = true;
            }
            else
                searchField.dlFilterSelectedFromResults();
        } else {  //all results
            //searchField.allFilterSelected();
        }
        searchField.setCurrentSavedSearch ({});
        //searchField.invokeCallback(); // Use the value in the current search fields;
        var searchParams = searchField.getCurrentSearchQuery();
        var displayName = searchField.getSearchFieldElement().value;
        if(searchField.searchSelectedType && searchField.searchSelectedType.length > 0){
           displayName += " In "+searchField.searchSelectedType;
           if(itemType && itemType.length > 0)
                displayName += " & "+ itemType;
        }
        else if(itemType && itemType.length > 0)
                displayName += " In "+ itemType;

        var params = {
              type:1,
              unique:true,
              disableForSearch: skipNotify,
              query:searchParams.query,
              searchType:searchParams.types,
              displayName:displayName
         };
        if (!slController._uiContainer)
            slController._show();

        slController._uiContainer.removeAllBubbles(true);
        slController._uiContainer.addBubble(params,skipNotify);

        searchField.restoreSearchFilter(); //restore containedObject
	}
}

ZaOverviewPanelController.downloadTreeListener = function(ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getMigrationWizController(), ZaMigrationWizController.prototype.show, null);
	} else {
		ZaApp.getInstance().getMigrationWizController().show();
	}
}

ZaOverviewPanelController.zimletListTreeListener = function (ev) {
    ZaZimlet.getAll(ZaZimlet.EXCLUDE_EXTENSIONS, new AjxCallback(ZaOverviewPanelController._zimletListTreeListener));
};

ZaOverviewPanelController._zimletListTreeListener = function (zimlets) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getZimletListController(), ZaZimletListController.prototype.show, zimlets);
	} else {
		ZaApp.getInstance().getZimletListController().show(zimlets);
	}	
}

ZaOverviewPanelController.adminExtListTreeListener = function (ev) {
    ZaZimlet.getAll(ZaZimlet.EXCLUDE_MAIL, new AjxCallback(ZaOverviewPanelController._adminExtListTreeListener));
};

ZaOverviewPanelController._adminExtListTreeListener = function (zimlets) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getAdminExtListController(), ZaAdminExtListController.prototype.show, zimlets);
	} else {
		ZaApp.getInstance().getAdminExtListController().show(zimlets);
	}	
}


ZaOverviewPanelController.cosListTreeListener = function (ev) {
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(ZaApp.getInstance().getCosListController(), ZaCosListController.prototype.show, true);
	} else {
		ZaApp.getInstance().getCosListController().show(true);
	}
	this._modifySearchMenuButton(ZaItem.COS) ;
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

ZaOverviewPanelController.manageAccountTreeListener = function (ev) {
    var accountStat =  ZaApp.getInstance().getAccountStats(true);
    var tree = this._overviewPanel.getFolderTree();
    var rootItem = tree.getCurrentRootItem();
    var childitems = rootItem.getItems();

    for(var i = 0; i < childitems.length; i++) {
        var child = childitems[i];
        var attr = child.getData("TreeItemType");
        child.setCount(accountStat[attr]);
    }
}

ZaOverviewPanelController.accountListInDomainTreeListener = function(ev) {
    var domain = ev.item.getData("domainItem");
    ZaDomain.searchAccountsInDomain(domain.name);
}

ZaOverviewPanelController.memberListInDLTreeListener = function(ev) {
    var dl = ev.item.getData("dlItem");
    var members = dl[ZaDistributionList.A2_memberList];
    var query ="";
    if(members.length > 0){
       for(var i=0;i<members.length;i++){
            query+= "("+ZaAccount.A_mail+"="+members[i][ZaAccount.A_name]+")";
       }
       if(members.length>1)
           query = "(|" + query + ")";

        var types = [ZaSearch.ACCOUNTS,ZaSearch.DLS,ZaSearch.ALIASES] ;

        var controller = ZaApp.getInstance().getSearchListController();
        var busyId = Dwt.getNextId();
        var callback =  new AjxCallback(controller, controller.searchCallback, {limit:controller.RESULTSPERPAGE,show:true,busyId:busyId});

        controller.fetchAttrs = ZaSearch.standardAttributes;

        var searchParams = {
                query:query,
                types:types,
                attrs:controller.fetchAttrs,
                callback:callback,
                controller: controller,
                showBusy:true,
                busyId:busyId,
                busyMsg:ZaMsg.BUSY_SEARCHING,
                skipCallbackIfCancelled:false
        }

        ZaSearch.searchDirectory(searchParams);
    }

}

ZaOverviewPanelController.prototype.refreshAccountTree = function() {
    var targetPath = ZaMsg.OVP_home + ZaTree.SEPERATOR + ZaMsg.OVP_manageAccounts;
    var tree = this.getOverviewPanel().getFolderTree();
    var rootItem = tree.getCurrentRootItem();
    var rootPath = tree.getABPath(rootItem.getData("dataItem"));
    if(targetPath == rootPath) {
        ZaOverviewPanelController.manageAccountTreeListener.call(this);
    }
}

ZaOverviewPanelController.prototype.refreshSearchTree = function(ev) {
    var details = ev.getDetails();
    if (!details) {
        return;
    }

    var targetPath = ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_search, ZaMsg.OVP_search]);
    var tree = this.getOverviewPanel().getFolderTree();
    var rootItem = tree.getCurrentRootItem();
    var rootPath = tree.getABPath(rootItem.getData("dataItem"));
    if(targetPath != rootPath) {
        return;
    }

    var childItems = rootItem.getItems();
    for(var i = 0; i < childItems.length; i++) {
        var child = childItems[i];
        var attr = child.getData("TreeItemType");
        var count;
        if (attr) {
            count = details[attr];
        } else {
            count = details.searchTotal;
        }

        if (count) {
            child.setCount(count);
        } else {
            child.setCount(0);
        }
    }
}

ZaOverviewPanelController.manageRelatedTreeListener = function (patharr,relatedZaItem) {
    var tree = this.getOverviewPanel().getFolderTree();
    if (patharr.length == 0)
        return "";
    var targetParentPath =  patharr.join(ZaTree.SEPERATOR);
    var rootItem = tree.getCurrentRootItem();
    var rootPath = tree.getABPath(rootItem.getData("dataItem"));
    if (rootPath == targetParentPath){
            var parentItemData = tree.getTreeItemDataByPath(targetParentPath);
            if(parentItemData) {
                parentItemData.addRelatedObject(this.getRelatedList(targetParentPath,relatedZaItem));
            }
            ZaOverviewPanelController.updateRelatedTreeListener.call(this,patharr);
    }
}



ZaOverviewPanelController.updateRelatedTreeListener = function(patharr){
    var tree = this.getOverviewPanel().getFolderTree();
    if (patharr.length == 0)
        return "";
    var targetParentPath =  patharr.join(ZaTree.SEPERATOR);
    var relatePath = ZaTree.SEPERATOR  + ZaMsg.OVP_related;
    var showRootNode = tree.getTreeItemDataByPath(targetParentPath);
    tree.buildTree(showRootNode);   //update menuitem

    var selectedItems = tree.getCurrentSelectedItems().getArray();
    var oldselectedItem = selectedItems ? selectedItems[0]:null;
    var selectedItemPath = oldselectedItem ? tree.getABPath(oldselectedItem.getData("dataItem")):null;
    var newselectedItem = tree.getTreeItemByPath(selectedItemPath) || tree.getSelectedItem(showRootNode);
    var skipNotify = false;
    if(selectedItemPath.indexOf(targetParentPath+relatePath)==-1)
        skipNotify = true;
    tree.setSelection(newselectedItem,skipNotify);
}

ZaOverviewPanelController.prototype.refreshRelatedTree = function(items,skipCos,skipDomain,skipDL) {
  try{
        var itemArray = AjxUtil.toArray(items);
        var tempHashDomain = {};
        var tempHashCos = {};
        var tempHashDL = {};
        var tempHashAccount = {};
        var cosId, cosName, cos, domainName, domain, defaultCos,defaultCosName,
            dl,dlName,dlId;


        for(var i=0;i<itemArray.length;i++){
            var item = itemArray[i];

            if(AjxUtil.isEmpty(item))
                continue;

            if( item.type == ZaItem.ACCOUNT){
                if(!skipCos){
                    cos = ZaAccount.prototype.getCurrentCos.call(item);
                    cosName = cos[ZaAccount.A_name];
                    if(typeof(tempHashCos[cosName]) == "undefined"){
                        tempHashCos[cosName]=1;
                        ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_cos,cosName],cos) ;
                    }

                    if(typeof(tempHashCos["default"]) == "undefined"){//because defaultCos search all accounts
                        tempHashCos["default"]=1;
                        defaultCos = ZaCos.getCosByName("default");
                        ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_cos,"default"],defaultCos) ;
                    }
                }//cos


            }

            if(item.type == ZaItem.ALIAS){
                if(!skipDL){
                    var targetObj = item.targetObj;
                    var targetName = targetObj[ZaAccount.A_name];
                    if(targetObj.type == ZaItem.ACCOUNT){
                        if(typeof(tempHashAccount[targetName]) == "undefined"){
                            tempHashAccount[targetName] = 1;
                            targetObj.load();
                            ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_manageAccounts,ZaMsg.OVP_accounts,targetName],targetObj) ;
                        }
                    }
                    if(targetObj.type == ZaItem.DL){
                        if(typeof(tempHashDL[targetName]) == "undefined"){
                             tempHashDL[targetName] = 1;
                             targetObj.load();
                             ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_manageAccounts,ZaMsg.OVP_distributionLists,targetName],targetObj) ;
                        }
                    }
                }
            }

            if( item.type == ZaItem.ACCOUNT || item.type == ZaItem.DL){
                if(!skipDL){
                    if (!AjxUtil.isEmpty(item[ZaAccount.A2_memberOf]) &&
                        !AjxUtil.isEmpty(item[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList])
                        ) {
                        var member = item[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList];
                        for(var i=0; i<member.length; i++){
                            dlName = member[i][ZaAccount.A_name]
                            if(typeof(tempHashDL[dlName]) == "undefined"){
                                tempHashDL[dlName] = 1;
                                dl = new ZaDistributionList(member[i]["id"],dlName);
                                dl.load();
                                ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_manageAccounts,ZaMsg.OVP_distributionLists,dlName],dl) ;
                            }
                        }
                    }
                }
            }

            if(!skipDomain && ( item.type == ZaItem.ACCOUNT || item.type == ZaItem.ALIAS || item.type == ZaItem.DL || item.type == ZaItem.RESOURCE)){   //domain
                domainName=ZaAccount.getDomain(item[ZaAccount.A_name]);
                if(typeof(tempHashDomain[domainName]) == "undefined"){
                    domain =  ZaDomain.getDomainByName(domainName);
                    tempHashDomain[domainName] = 1;
                    ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_domains,domainName],domain) ;
                }
            }

            if(item.type == ZaItem.DOMAIN){
                  if(!skipCos && item.attrs[ZaDomain.A_domainType] == ZaDomain.domainTypes.local) {
                        cosId = item.attrs[ZaDomain.A_domainDefaultCOSId];
                        cos = ZaCos.getCosById(cosId);
                        if(!cos){
                            cos = ZaCos.getCosByName("default");
                        }
                        cosName = cos[ZaAccount.A_name];

                        if(typeof(tempHashCos[cosName]) == "undefined"){
                            tempHashCos[cosName]=1;
                            ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_cos,cosName],cos) ;
                        }

                        if(typeof(tempHashCos["default"]) == "undefined"){//because defaultCos search all domains
                            tempHashCos["default"]=1;
                            defaultCos = ZaCos.getCosByName("default");
                            ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_cos,"default"],defaultCos) ;
                        }
                  }else  if (!skipDomain && item.attrs [ZaDomain.A_domainType] == ZaDomain.domainTypes.alias) {
                        domainName = item.attrs[ZaDomain.A_zimbraMailCatchAllForwardingAddress] ;
                        domainName = domainName.replace("@", "") ;
                        if(typeof(tempHashDomain[domainName]) == "undefined"){
                              domain = ZaDomain.getDomainByName(domainName);
                              ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_domains,domainName],domain) ;
                        }


                  }
            }
        }
  }catch(ex){
        ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaOverviewPanelController.prototype.refreshRelatedTree");
  }

}

ZaOverviewPanelController.prototype.refreshRelatedTreeByEdit = function(newItem){
    try{

            if( AjxUtil.isEmpty(newItem))
                return ;

            if(newItem.type == ZaItem.DL){
                ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_manageAccounts,ZaMsg.OVP_distributionLists,newItem[ZaAccount.A_name]],newItem) ;
            }

            if(newItem.type == ZaItem.DOMAIN && newItem.attrs[ZaDomain.A_domainType] == ZaDomain.domainTypes.local
                && newItem.createGalAccount){
                 ZaOverviewPanelController.manageRelatedTreeListener.call(this,[ZaMsg.OVP_home,ZaMsg.OVP_configure,ZaMsg.OVP_domains,newItem[ZaAccount.A_name]],newItem) ;
            }


       }catch(ex){
            ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaOverviewPanelController.prototype.refreshRelatedTreeByEdit");
        }
   }


ZaOverviewPanelController.prototype._modifySearchMenuButton = 
function (itemType) {
	if (itemType) {
		var searchListController = ZaApp.getInstance().getSearchListController(); 
		if(!searchListController || !searchListController._searchField) {
			return;
		}
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
            case ZaItem.COS:
                searchListController._searchField.cosFilterSelected(); break ;
		}
	}
}

ZaOverviewPanelController.prototype.addAccountItem =
function(item, currentView) {
	var type = item.type;
    var relativePath = ZaMsg.OVP_accounts;
	if (type == ZaItem.ACCOUNT) {
        relativePath = ZaMsg.OVP_accounts;
	} else if (type == ZaItem.DL) {
        relativePath = ZaMsg.OVP_distributionLists;
	} else if (type == ZaItem.RESOURCE ){
        relativePath = ZaMsg.OVP_resources;
	} else if (type == ZaItem.ALIAS) {
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {
            relativePath = ZaMsg.OVP_accounts;
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
            relativePath =  ZaMsg.OVP_distributionLists;
		}else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_RESOURCE) {
            relativePath = ZaMsg.OVP_resources;
        }
	}

    var parentPath =  ZaTree.getPathByArray([ZaMsg.OVP_home, ZaMsg.OVP_manageAccounts, relativePath]);
    var name = item.name;
    this.addObjectItem(parentPath, name, currentView, false, false, item);
}

ZaOverviewPanelController.prototype.addObjectItem =
function (parentPath, name, currentView, skipHistory, skipNotify, relatedZaItem, mappingId2handlerMap) {
    if (!currentView) {
        currentView = ZaApp.getInstance().getAppViewMgr().getCurrentViewContent();
        if (!currentView ||
            !currentView.getTabChoices ||
            !currentView.getTabChoices()
            )
            return false;
    }

    var namePath = parentPath + ZaTree.SEPERATOR + name;
    var tree = this.getOverviewPanel().getFolderTree();
    var mappingId, handler, map;

    var needToAddNameNode = false;
    var needToAddTabNodes = false;
    var nameDataItem = tree.getTreeItemDataByPath (namePath);

    if (!nameDataItem) {
        needToAddNameNode = needToAddTabNodes = true;
    } else  if (nameDataItem.getChildrenNum() <= 0) {
        needToAddTabNodes = true;
    }

    var historyObject = new ZaHistory(namePath, name, relatedZaItem?relatedZaItem.type:null);
    ZaZimbraAdmin.getInstance().getHisotryMgr().addHistoryObj(historyObject);

    if (needToAddNameNode) {
        var parentDataItem = tree.getTreeItemDataByPath (parentPath);
        var index = parentDataItem.getChildrenNum();
        var parentId = parentDataItem.id;
        if ( !mappingId2handlerMap || !(map = mappingId2handlerMap["mainNode"]) ){
            //default main node(this name node) mapping relationship
            mappingId = ZaZimbraAdmin._XFORM_VIEW;
            handler = ZaOverviewPanelController.xformTreeListener;
        }else {
            mappingId = map["mappingId"];
            handler = map["handler"];
        }

        nameDataItem =   new ZaTreeItemData({
                            parent:parentPath,
                            mappingId: mappingId,
                            id:DwtId._makeId(parentId, index + 1),
                            image: (relatedZaItem?this.getIconByType(relatedZaItem.type):null),
                            defaultSelectedItem: 1,
                            text: name});
        tree.addTreeItemData(nameDataItem);
        nameDataItem.addRelatedObject(this.getRelatedList(parentPath,relatedZaItem));
        nameDataItem.addRecentObject(this.getRecentList());

        ZaOverviewPanelController.overviewTreeListeners[mappingId] = handler;
    }else{ //updaet relatedObject count
        nameDataItem.addRelatedObject(this.getRelatedList(parentPath,relatedZaItem));
        nameDataItem.addRecentObject(this.getRecentList());
    }


   // if (!nameDataItem.getData("viewId")) {  some view are not cached, so need to update viewId
        var currentViewId = ZaApp.getInstance().getAppViewMgr().getCurrentView();
        nameDataItem.setData("viewId", currentViewId);
    //}

    if (needToAddTabNodes) {
        if ( !mappingId2handlerMap || !(map = mappingId2handlerMap["tabNodes"]) ){
            //default sub tab nodes mapping relationship
            mappingId = ZaZimbraAdmin._XFORM_TAB_VIEW;
            handler = ZaOverviewPanelController.xformTabTreeListener;
        }else {
            mappingId = map["mappingId"];
            handler = map["handler"];
        }
        this.addSubTabsToParentTreeItem(nameDataItem, currentView.getTabChoices(), mappingId);
        ZaOverviewPanelController.overviewTreeListeners[mappingId] = handler;
    }

    tree.setSelectionByPath(namePath, !skipHistory, skipNotify);
    return true;
}


ZaOverviewPanelController.prototype.addObjectItemOri = function (parentPath, name, currentView, skipHistory, skipNotify, item) {
    if (!currentView) {
        currentView = ZaApp.getInstance().getAppViewMgr().getCurrentViewContent();
        if (!currentView)
            return;
        if (!currentView.getTabChoices)
            return;
        if (!currentView.getTabChoices())
            return;
    }

    var namePath = parentPath + ZaTree.SEPERATOR + name;
    var tree = this.getOverviewPanel().getFolderTree();
    var nameDataItem = tree.getTreeItemDataByPath (namePath);
    var isAddNameNode = false;
    var isAddTabNode = false;

    if (!nameDataItem) {
        isAddNameNode = true;
        isAddTabNode = true;
    } else {
        if (nameDataItem.getChildrenNum() == 0) {
            isAddTabNode = true;
        }
    }

    var historyObject = new ZaHistory(namePath, name, item?item.type:null);
    ZaZimbraAdmin.getInstance().getHisotryMgr().addHistoryObj(historyObject);

    if (isAddNameNode) {
        var parentDataItem = tree.getTreeItemDataByPath (parentPath);
        var index = parentDataItem.getChildrenNum();
        var parentId = parentDataItem.id;
        nameDataItem =   new ZaTreeItemData({
                            parent:parentPath,
                            image: (item?this.getIconByType(item.type):null),
                            mappingId: ZaZimbraAdmin._XFORM_VIEW,
                            id:DwtId._makeId(parentId, index + 1),
                            text: name});
        tree.addTreeItemData(nameDataItem);
        nameDataItem.addRelatedObject(this.getRelatedList(parentPath,item));
        nameDataItem.addRecentObject(this.getRecentList())
    }else{ //update related object count
        nameDataItem.addRelatedObject(this.getRelatedList(parentPath,item));
        nameDataItem.addRecentObject(this.getRecentList());
    }

    if (!nameDataItem.getData("viewId")) {
        var currentViewId = ZaApp.getInstance().getAppViewMgr().getCurrentView();
        nameDataItem.setData("viewId", currentViewId);
    }

    if (isAddTabNode) {
        this.addSubTabsToParentTreeItem(nameDataItem, currentView.getTabChoices(), ZaZimbraAdmin._XFORM_TAB_VIEW);
    }

    if (! ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._XFORM_VIEW])
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._XFORM_VIEW] = ZaOverviewPanelController.xformTreeListener;
    if (! ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._XFORM_TAB_VIEW])
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._XFORM_TAB_VIEW] = ZaOverviewPanelController.xformTabTreeListener;
    tree.setSelectionByPath(namePath, !skipHistory, skipNotify);
}

ZaOverviewPanelController.prototype.addSubTabsToParentTreeItem = function(parentItem, subTabs, mappingIdForAllSubTabs, isShowHistory) {
        var subTabItem, subTabInfo, subTabItemId;
        var tree = this.getOverviewPanel().getFolderTree();
        var parentPath = tree.getABPath(parentItem);
        var isShow = isShowHistory ? true: false;
        if (subTabs && subTabs.length > 0) {
            parentItem.setData("firstTab", subTabs[0].value);
            for (var i = 0; i < subTabs.length; i++) {
                subTabInfo = subTabs[i];
                subTabItemId = DwtId._makeId(parentItem.id, i+1);
                subTabItem = new ZaTreeItemData({
                                    parent: parentPath,
                                    id: subTabItemId,
                                    isShowHistory: isShow,
                                    text: subTabInfo.label,
                                    mappingId: mappingIdForAllSubTabs});
                subTabItem.setData("tabValue", subTabInfo.value);
                tree.addTreeItemData(subTabItem);
            }
        }
}

ZaOverviewPanelController.xformTabTreeListener = function(ev) {
    var viewId = ev.item.parent.getData("viewId");
    var stepValue = ev.item.getData("tabValue");
    ZaApp.getInstance().getAppViewMgr().pushView(viewId);
    var currentView = ZaApp.getInstance().getAppViewMgr().getViewContentById(viewId);
    if (ev.refresh) {
        var currentControll = ZaApp.getInstance().getControllerById(viewId);
        var currentObject = currentControll._currentObject;
        if (currentObject && currentObject.refresh) {
            currentObject.refresh(false, true);
            currentView.setObject(currentObject);
        }
    }
    if(currentView._localXForm){ //some views of zimlets are created by dwt
        currentView._localXForm.setInstanceValue(stepValue, ZaModel.currentTab);
        currentView._localXForm.refresh() ;
    }

}

ZaOverviewPanelController.xformTreeListener = function(ev) {
    var viewId = ev.item.getData("viewId");
    var stepValue = ev.item.getData("firstTab");
    ZaApp.getInstance().getAppViewMgr().pushView(viewId);
    var currentView = ZaApp.getInstance().getAppViewMgr().getViewContentById(viewId);
    if (ev.refresh) {
        var currentControll = ZaApp.getInstance().getControllerById(viewId);
        var currentObject = currentControll._currentObject;
        if (currentObject && currentObject.refresh) {
            currentObject.refresh(false, true);
            currentView.setObject(currentObject);
        }
    }
    currentView._localXForm.setInstanceValue(stepValue, ZaModel.currentTab);
    currentView._localXForm.refresh() ;
}

ZaOverviewPanelController.prototype.getRelatedList =
function(parentPath, item) {
    if(!item) return;
    if(item.type == ZaItem.ACCOUNT) {
        return this.getRelatedList4Account(parentPath, item);
    } else if (item.type == ZaItem.DL) {
        return this.getRelatedList4DL(parentPath, item);
    } else if (item.type == ZaItem.RESOURCE ){

    } else if (item.type == ZaItem.ALIAS) {
    } else if (item.type == ZaItem.DOMAIN) {
        return this.getRelatedList4Domain(parentPath, item);
    } else if (item.type == ZaItem.COS) {
        return this.getRelatedList4Cos(parentPath, item);
    }
    return [];
}


ZaOverviewPanelController.prototype.getRelatedList4DL =
function(parentPath, item) {
    var alias = item.attrs[ZaAccount.A_zimbraMailAlias];
    var members = item[ZaDistributionList.A2_memberList];
    var Tis = [];
    if(alias.length > 0) {
        var aliasTi = new ZaTreeItemData({
                    text: ZaMsg.TABT_Aliases,
                    //type: 1,
                    count:alias.length,
                    image:"AccountAlias",
                    mappingId: ZaZimbraAdmin._DL_ALIAS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.TABT_Aliases
                    }
                );
        aliasTi.setData("aliasTargetId", item.id);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DL_ALIAS_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
        Tis.push(aliasTi);
    }
    if(members.length > 0) {
        var membersTi = new ZaTreeItemData({
                    text: ZaMsg.DLXV_LabelListMembers,
                    count:members.length,
                    image:"DistributionList",
                    mappingId: ZaZimbraAdmin._DL_MEMBERS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.DLXV_LabelListMembers
                    }
                );
        membersTi.setData("dlItem", item);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DL_MEMBERS_LIST_VIEW] = ZaOverviewPanelController.memberListInDLTreeListener;
        Tis.push(membersTi);
    }
    return Tis;

}

ZaOverviewPanelController.prototype.getRelatedList4Account =
function(parentPath, item) {
    var alias = item.attrs[ZaAccount.A_zimbraMailAlias];
    var cos = ZaCos.getCosById(item.attrs[ZaAccount.A_COSId])
            || ZaCos.getDefaultCos4Account(item[ZaAccount.A_name]);
    var domainName = ZaAccount.getDomain(item[ZaAccount.A_name]);
    var domainObj =  ZaDomain.getDomainByName (domainName) ;
    //var zimletList = item.attrs[ZaAccount.A_zimbraZimletAvailableZimlets]
    //        || item._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];

    var Tis = [];
    if(alias.length > 0) {
        var aliasTi = new ZaTreeItemData({
                    text: ZaMsg.TABT_Aliases,
                    //type: 1,
                    count:alias.length,
                    image:"AccountAlias",
                    mappingId: ZaZimbraAdmin._ACCOUNT_ALIAS_LIST_VIEW,    //ZaZimbraAdmin._ALIASES_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.TABT_Aliases
                    }
                );
        aliasTi.setData("aliasTargetId", item.id);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ACCOUNT_ALIAS_LIST_VIEW] = ZaOverviewPanelController.aliasListTreeListener;
        Tis.push(aliasTi);
    }

    var dls = item[ZaAccount.A2_memberOf];
    if (dls != null) {
        var direct_dls = dls[ZaAccount.A2_directMemberList];
        var indirect_dls = dls[ZaAccount.A2_indirectMemberList];

        if ((direct_dls.length + indirect_dls.length) > 0) {
            var dlsTi = new ZaTreeItemData({
                    text: ZaMsg.OVP_distributionLists,
                    count:direct_dls.length + indirect_dls.length,
                    image:"DistributionList",
                    mappingId: ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.OVP_distributionLists
                }
            );
            dlsTi.setData(ZaAccount.A2_memberOf, dls);
            ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW] = ZaOverviewPanelController.dlListTreeListener;
            Tis.push(dlsTi);
        }
    }

    var cosTi = new ZaTreeItemData({
                text: cos.name,
                image:"COS",
                forceNode: true,
                mappingId: ZaZimbraAdmin._COS_VIEW,
                path: parentPath + ZaTree.SEPERATOR + cos.name
                }
            );
    cosTi.setData(ZaOverviewPanelController._OBJ_ID, cos.id);
    cosTi.setData("skipHistory", "TRUE");
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_VIEW] = ZaOverviewPanelController.cosTreeListener;
    Tis.push(cosTi);

    var domainTi = new ZaTreeItemData({
                text: domainName,
                image:"Domain",
                forceNode: true,
                mappingId: ZaZimbraAdmin._DOMAIN_VIEW,
                path: parentPath + ZaTree.SEPERATOR + domainName
                }
            );
    domainTi.setData(ZaOverviewPanelController._OBJ_ID, domainObj.id);
    domainTi.setData("skipHistory", "TRUE");
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAIN_VIEW] = ZaOverviewPanelController.domainTreeListener;
    Tis.push(domainTi);
    /*
    var zimletTi = new ZaTreeItemData({
                text: ZaMsg.TABT_Zimlets,
                //type: 1,
                count:zimletList.length,
                mappingId:ZaZimbraAdmin._ZIMLET_LIST_VIEW,
                path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.TABT_Zimlets
                }
            );
    ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = ZaOverviewPanelController.zimletListTreeListener;
    */
    return Tis;
}

ZaOverviewPanelController.prototype.getRelatedList4Cos =
function(parentPath, item) {
    var Tis = [];
    var count = item.countAllAccounts();
    if(count > 0) {
        var accountTi = new ZaTreeItemData({
                    text: ZaMsg.OVP_accounts,
                    count:count,
                    image:"Account",
                    mappingId: ZaZimbraAdmin._COS_ACCOUNT_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.OVP_accounts
                    }
                );
        accountTi.setData("cosItem", item);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_ACCOUNT_LIST_VIEW] = ZaOverviewPanelController.accountListTreeListener;
        Tis.push(accountTi);
    }
    count = item.countAllDomains();
    if(count > 0) {
        var domainTi = new ZaTreeItemData({
                    text: ZaMsg.OVP_domains,
                    count:count,
                    image:"Domain",
                    mappingId: ZaZimbraAdmin._COS_DOMAIN_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.OVP_domains
                    }
                );
        domainTi.setData("cosItem", item);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._COS_DOMAIN_LIST_VIEW] = ZaOverviewPanelController.domainListTreeListener;
        Tis.push(domainTi);
    }
    return Tis;
}

ZaOverviewPanelController.prototype.getRelatedList4Domain =
function(parentPath, item) {
    var Tis = [];
    var count = item.countAllAccounts();
    if(count > 0) {
        var accountTi = new ZaTreeItemData({
                    text: ZaMsg.OVP_accounts,
                    count:count,
                    image:"Account",
                    mappingId: ZaZimbraAdmin._DOMAIN_ACCOUNT_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.OVP_accounts
                    }
                );
        accountTi.setData("domainItem", item);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAIN_ACCOUNT_LIST_VIEW] = ZaOverviewPanelController.accountListInDomainTreeListener;
        Tis.push(accountTi);
    }

    count = item.countAllAliases();
    if(count > 0) {
        var aliasTi = new ZaTreeItemData({
                    text: ZaMsg.TABT_Aliases,
                    count:count,
                    image:"DomainAlias",
                    mappingId: ZaZimbraAdmin._DOMAIN_ALIAS_LIST_VIEW,
                    path: parentPath + ZaTree.SEPERATOR + item.name + ZaTree.SEPERATOR + ZaMsg.OVP_accounts
                    }
                );
        aliasTi.setData("domainItem", item);
        ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._DOMAIN_ALIAS_LIST_VIEW] = ZaOverviewPanelController.domainListTreeListener;
        Tis.push(aliasTi);
    }
    return Tis;
}

ZaOverviewPanelController.prototype.getRecentList =
function() {
    var historyMgr = ZaZimbraAdmin.getInstance().getHisotryMgr();
    var objList = historyMgr.getAllHistoryObj().getArray();
    var Tis = [];
    var ti = null;
    var image = null;
    for(var i = objList.length - 1; i > -1; i --) {
/*
        if(objList[i].type == ZaItem.ACCOUNT)
            image = "Account";
        else if(objList[i].type == ZaItem.COS)
            image = "COS";
        else if(objList[i].type == ZaItem.DOMAIN)
            image = "Domain";
        else if(objList[i].type == ZaItem.RESOURCE)
            image = "Resource";
        else if(objList[i].type == ZaItem.DL)
            image = "DistributionList";
        else if(objList[i].type == ZaItem.ALIAS)
            image = "AccountAlias";
        else if(objList[i].type == ZaItem.SERVER)
            image = "Server";
*/
        image = this.getIconByType(objList[i].type);
        ti = new ZaTreeItemData({
                text: objList[i].displayName,
                type:1,
                image:image,
                forceNode: (i+1 != objList.length),
                path: objList[i].path
                }
            );
        Tis.push(ti);
    }
    return Tis;
}

ZaOverviewPanelController.prototype.getIconByType = function(type) {
    var image = null;
    if(type == ZaItem.ACCOUNT)
        image = "Account";
    else if(type == ZaItem.COS)
        image = "COS";
    else if(type == ZaItem.DOMAIN)
        image = "Domain";
    else if(type == ZaItem.RESOURCE)
        image = "Resource";
    else if(type == ZaItem.DL)
        image = "DistributionList";
    else if(type == ZaItem.ALIAS)
        image = "AccountAlias";
    else if(type == ZaItem.SERVER)
        image = "Server";

    return image;
}
