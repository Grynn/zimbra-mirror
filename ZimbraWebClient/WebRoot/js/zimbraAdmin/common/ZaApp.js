/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaApp
* @constructor ZaApp
* @param appCtxt instance of ZaAppCtxt
* @param container
* @author Greg Solovyev
**/
function ZaApp(appCtxt, container) {
	if (arguments.length == 0) return;
	this._name = ZaZimbraAdmin.ADMIN_APP;
	this._appCtxt = appCtxt;
	this._appViewMgr = appCtxt.getAppViewMgr();
	this._container = container;
	this._currentController = null;
	this._currentViewId = null;
	this._cosListChoices = null;//new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");	
	this._domainListChoices = null;//new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");	
	this._serverChoices = null; 
	this._serverChoices2 = null; 	
	this._serverMap = null;
	this._controllers = new Object();
	this.dialogs = {};
}

ZaApp.prototype.constructor = ZaApp;

ZaApp.prototype.toString = 
function() {
	return "ZaApp";
}

ZaApp.prototype.launch =
function(appCtxt) {
	if(ZaSettings.STATUS_ENABLED) {
		this.getStatusViewController().show();
	} else if(ZaSettings.ADDRESSES_ENABLED) {
		this._appCtxt.getAppController()._showAccountsView([ZaItem.ACCOUNT,ZaItem.DL,ZaItem.ALIAS],null);
	}

	if(ZaSettings.DOMAINS_ENABLED) {
		this.searchDomains("");
	}
}

ZaApp.prototype.setActive =
function(active) {
	if (active) {
		if(ZaSettings.STATUS_ENABLED) {
			this.getStatusViewController().show();	
		} else if(ZaSettings.ADDRESSES_ENABLED) {
			this._appCtxt.getAppController()._showAccountsView([ZaItem.ACCOUNT,ZaItem.DL,ZaItem.ALIAS],null);
		}
	}
}

ZaApp.prototype.getAppCtxt = 
function() {
	return this._appCtxt;
}

ZaApp.prototype.getCurrentController = 
function(ctrlr) {
	return this._controllers[this._currentViewId];
}

/**
* View controllers
**/
ZaApp.prototype.getStatusViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._STATUS] == null)
		this._controllers[ZaZimbraAdmin._STATUS] = new ZaStatusViewController(this._appCtxt, this._container, this);
	return this._controllers[ZaZimbraAdmin._STATUS];
}

ZaApp.prototype.getServerStatsController =
function() {
	if (this._controllers[ZaZimbraAdmin._STATISTICS_BY_SERVER] == null)
		this._controllers[ZaZimbraAdmin._STATISTICS_BY_SERVER] = new ZaServerStatsController(this._appCtxt, this._container, this);
	return this._controllers[ZaZimbraAdmin._STATISTICS_BY_SERVER];
}

ZaApp.prototype.getGlobalStatsController =
function() {
	if (this._controllers[ZaZimbraAdmin._STATISTICS] == null)
		this._controllers[ZaZimbraAdmin._STATISTICS] = new ZaGlobalStatsController(this._appCtxt, this._container, this);
	return this._controllers[ZaZimbraAdmin._STATISTICS];
}

ZaApp.prototype.getGlobalConfigViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._GLOBAL_SETTINGS] == null)
		this._controllers[ZaZimbraAdmin._GLOBAL_SETTINGS]  = new ZaGlobalConfigViewController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._GLOBAL_SETTINGS] .addSettingsChangeListener(new AjxListener(this, ZaApp.prototype.handleSettingsChange));
	return this._controllers[ZaZimbraAdmin._GLOBAL_SETTINGS] ;
}

ZaApp.prototype.getSearchListController =
function() {
	if (this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] = new ZaSearchListController(this._appCtxt, this._container, this);
//		this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleAccountRemoval));					
		this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW].addRemovalListener(new AjxListener(this.getSearchListController(), this.getSearchListController().handleRemoval));							
		//the view of the search list is only controlled by the searchlistcontroller, no need to add the domainlistcontroller listener
		//this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW].addRemovalListener(new AjxListener(this.getDomainListController(), this.getDomainListController().handleRemoval));									
	}
	return this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] ;
}

ZaApp.prototype.getSearchBuilderController =
function() {
	if (this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] = new ZaSearchBuilderController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW].addRemovalListener(new AjxListener(this.getSearchBuilderController(), this.getSearchBuilderController().handleRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] ;
}

ZaApp.prototype.getSearchBuilderToolbarController = ZaApp.prototype.getSearchBuilderController ;
/*
function() {
	if (this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_TOOLBAR_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_TOOLBAR_VIEW] = new ZaSearchBuilderToolbarController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_TOOLBAR_VIEW].addRemovalListener(new AjxListener(this.getSearchBuilderToolbarController(), this.getSearchBuilderToolbarController().handleRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_TOOLBAR_VIEW] ;
}*/

ZaApp.prototype.getAccountListController =
function() {
	if (this._controllers[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW] = new ZaAccountListController(this._appCtxt, this._container, this);
//		this._controllers[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleAccountRemoval));					
		this._controllers[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW].addRemovalListener(new AjxListener(this.getAccountListController(), this.getAccountListController().handleRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._ACCOUNTS_LIST_VIEW]
}

ZaApp.prototype.getAccountViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW] = new ZaAccountViewController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW].addChangeListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleChange));
		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW].addCreationListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleCreation));	
		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW].addRemovalListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleRemoval));			
//		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW].addCreationListener(new AjxListener(this, ZaApp.prototype.handleAccountCreation));			
//		this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleAccountRemoval));					
	}
	return this._controllers[ZaZimbraAdmin._ACCOUNT_VIEW];
}

ZaApp.prototype.getZimletListController =
function() {
	if (this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = new ZaZimletListController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW]
}

ZaApp.prototype.getZimletController =
function() {
	if (this._controllers[ZaZimbraAdmin._ZIMLET_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ZIMLET_VIEW] = new ZaZimletViewController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._ZIMLET_VIEW];
}

ZaApp.prototype.getDistributionListController = 
function (domain) {
	if (this._controllers[ZaZimbraAdmin._DL_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._DL_VIEW] = new ZaDLController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._DL_VIEW].addCreationListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleCreation));			
		this._controllers[ZaZimbraAdmin._DL_VIEW].addRemovalListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleRemoval));			
		this._controllers[ZaZimbraAdmin._DL_VIEW].addChangeListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleChange));
	}
	return this._controllers[ZaZimbraAdmin._DL_VIEW];
};

ZaApp.prototype.getResourceController = 
function () {
	if (this._controllers[ZaZimbraAdmin._RESOURCE_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._RESOURCE_VIEW] = new ZaResourceController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._RESOURCE_VIEW].addCreationListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleCreation));			
		this._controllers[ZaZimbraAdmin._RESOURCE_VIEW].addRemovalListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleRemoval));			
		this._controllers[ZaZimbraAdmin._RESOURCE_VIEW].addChangeListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleChange));
	}
	return this._controllers[ZaZimbraAdmin._RESOURCE_VIEW];
};

ZaApp.prototype.getDomainListController =
function() {
	if (this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW] = new ZaDomainListController(this._appCtxt, this._container, this);
		
		this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW].addCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));					
//		this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW].addCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainCreation));							

		this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));							
//		this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW].addRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainRemoval));						
		
	}
	return this._controllers[ZaZimbraAdmin._DOMAINS_LIST_VIEW];
}

ZaApp.prototype.getDomainController =
function() {
	if (this._controllers[ZaZimbraAdmin._DOMAIN_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW] = new ZaDomainController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addChangeListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainChange));

		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));					
		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addCreationListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleCreation));	
//		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainCreation));				

		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addRemovalListener(new AjxListener(this.getDomainListController(), this.getDomainListController().handleRemoval));			
		this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));							
	//	this._controllers[ZaZimbraAdmin._DOMAIN_VIEW].addRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainRemoval));						
	}

	return this._controllers[ZaZimbraAdmin._DOMAIN_VIEW];
}

ZaApp.prototype.getMTAListController =
function () {
	if (this._controllers[ZaZimbraAdmin._POSTQ_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._POSTQ_VIEW] = new ZaMTAListController(this._appCtxt, this._container, this);
/*		this._controllers[ZaZimbraAdmin._POSTQ_VIEW].addServerRemovalListener(new AjxListener(this, ZaApp.prototype.handleServerRemoval));	
		this._controllers[ZaZimbraAdmin._POSTQ_VIEW].addServerRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleServerRemoval));							*/
	}
	return this._controllers[ZaZimbraAdmin._POSTQ_VIEW];
}

ZaApp.prototype.getMTAController =
function () {
	if (this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW] = new ZaMTAController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW].addChangeListener(new AjxListener(this.getMTAListController(), ZaMTAListController.prototype.handleMTAChange));		
		this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW].addChangeListener(new AjxListener(this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW], ZaMTAController.prototype.handleMTAChange));				
/*		this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW].addServerRemovalListener(new AjxListener(this, ZaApp.prototype.handleServerRemoval));	
		this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW].addServerRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleServerRemoval));							*/
	}
	return this._controllers[ZaZimbraAdmin._POSTQ_BY_SERVER_VIEW];
}

ZaApp.prototype.getServerListController =
function() {
	if (this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW] = new ZaServerListController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW].addServerRemovalListener(new AjxListener(this, ZaApp.prototype.handleServerRemoval));	
		this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW].addServerRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleServerRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW];
}

ZaApp.prototype.getServerController =
function() {
	if (this._controllers[ZaZimbraAdmin._SERVER_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SERVER_VIEW] = new ZaServerController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._SERVER_VIEW].addServerChangeListener(new AjxListener(this, ZaApp.prototype.handleServerChange));		
		this._controllers[ZaZimbraAdmin._SERVER_VIEW].addServerChangeListener(new AjxListener(this.getServerListController(), ZaServerListController.prototype.handleServerChange));		
		this._controllers[ZaZimbraAdmin._SERVER_VIEW].addServerChangeListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleServerChange));									
	}
	return this._controllers[ZaZimbraAdmin._SERVER_VIEW];
}

ZaApp.prototype.getCosListController =
function() {
	if (this._controllers[ZaZimbraAdmin._COS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._COS_LIST_VIEW] = new ZaCosListController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._COS_LIST_VIEW].addCosRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));			
		this._controllers[ZaZimbraAdmin._COS_LIST_VIEW].addCosRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosRemoval));									
	}
	return this._controllers[ZaZimbraAdmin._COS_LIST_VIEW];
}


ZaApp.prototype.getCosController =
function() {
	if (this._controllers[ZaZimbraAdmin._COS_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._COS_VIEW] = new ZaCosController(this._appCtxt, this._container, this);
		//since we are creating the COS controller now - register all the interested listeners with it
		this._controllers[ZaZimbraAdmin._COS_VIEW].addChangeListener(new AjxListener(this, ZaApp.prototype.handleCosChange));			
		this._controllers[ZaZimbraAdmin._COS_VIEW].addChangeListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosChange));
		this._controllers[ZaZimbraAdmin._COS_VIEW].addChangeListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosChange));						

		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosCreationListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosCreation));	
		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosCreationListener(new AjxListener(this, ZaApp.prototype.handleCosCreation));			
		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosCreation));				
		
		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));			
		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosRemovalListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosRemoval));			
		this._controllers[ZaZimbraAdmin._COS_VIEW].addCosRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosRemoval));						
	}
	return this._controllers[ZaZimbraAdmin._COS_VIEW];
}

ZaApp.prototype.getHelpViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._HELP_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._HELP_VIEW] = new ZaHelpViewController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._HELP_VIEW];
}

ZaApp.prototype.getMigrationWizController = 
function() {
	if (this._controllers[ZaZimbraAdmin._MIGRATION_WIZ_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._MIGRATION_WIZ_VIEW] = new ZaMigrationWizController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._MIGRATION_WIZ_VIEW];
}

ZaApp.prototype.searchDomains = function(query) {
	var callback = new AjxCallback(this, this.domainSearchCallback);
	var searchParams = {
			query:query, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:"0",
			sortAscending:"0",
			limit:ZaDomain.MAXSEARCHRESULTS,
			callback:callback
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaApp.prototype.domainSearchCallback = 
function (resp) {
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			this._domainList = new ZaItemList(ZaDomain, this);	
			this._domainList.loadFromJS(response);
			this._appCtxt.getAppController().getOverviewPanelController().updateDomainList(this._domainList);				
			EmailAddr_XFormItem.domainChoices.setChoices(this._domainList.getArray());
			EmailAddr_XFormItem.domainChoices.dirtyChoices();
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this.getCurrentController()._handleException(ex, "ZaListViewController.prototype.searchCallback");	
		} else {
			this.getCurrentController().popupErrorDialog(ZaMsg.queryParseError, ex);
		}		
	}
}
ZaApp.prototype.getDomainList =
function(refresh) {
	if (refresh || this._domainList == null) {
		this._domainList = ZaDomain.getAll(this);
		/*EmailAddr_XFormItem.domainChoices.setChoices(this._domainList.getArray());
		EmailAddr_XFormItem.domainChoices.dirtyChoices();*/
	}
	return this._domainList;	
}

ZaApp.prototype.getDomainListChoices =
function(refresh) {
	if (refresh || this._domainList == null) {
		this._domainList = ZaDomain.getAll(this);
	}
	if(refresh || this._domainListChoices == null) {
		if(this._domainListChoices == null)
			this._domainListChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");	

		this._domainListChoices.setChoices(this._domainList.getArray());
		this._domainListChoices.dirtyChoices();

	}
	return this._domainListChoices;	
}

ZaApp.prototype.getServerByName =
function(serverName) {
	if (this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerByName :: this._serverList is null ");
		this._serverList = ZaServer.getAll(this);
	}
	var cnt = this._serverList.getArray().length;
	var myServer = new ZaServer(this);
	for(var i = 0; i < cnt; i++) {
		if(this._serverList.getArray()[i].attrs[ZaServer.A_ServiceHostname] == serverName)
			return this._serverList.getArray()[i];
	}
	if(i == cnt) {
		myServer.load("name", serverName);
	}
	return myServer;	
}

ZaApp.prototype.getServerList =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll(this);
	}
	return this._serverList;	
}

ZaApp.prototype.getPostQList = 
function (refresh) {
	if (refresh || this._postqList == null) {
		this._postqList = ZaMTA.getAll(this);
	}
	return this._postqList;	
}

ZaApp.prototype.getMailServers =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll(this);
	}
	var resArray = new Array();
	var tmpArray = this._serverList.getArray();
	var cnt = tmpArray.length;
	for(var i = 0; i < cnt; i++) {
		if(tmpArray[i].attrs[ZaServer.A_zimbraMailboxServiceEnabled]) {
			resArray.push(tmpArray[i]);
		}
	}
	return resArray;
}



ZaApp.prototype.getServerListChoices =
function(refresh) {
	if (refresh || this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerListChoices :: this._serverList is null ");		
		this._serverList = ZaServer.getAll(this);
	}
	if(refresh || this._serverChoices == null) {
		if(this._serverChoices == null) {
			this._serverChoices = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");
		} else {	
			this._serverChoices.setChoices(this._serverList.getArray());
			this._serverChoices.dirtyChoices();
		}
	}
	return this._serverChoices;	
}

ZaApp.prototype.getClusterServerChoices = 
function(refresh){
	if (refresh || this._clusterServerList == null) {
		this._clusterServerList = ZaClusterStatus.getServerList();
	}
	if (refresh || this._clusterServerChoices == null) {
		if (this._clusterServerChoices == null ) {
			this._clusterServerChoices = new XFormChoices(this._clusterServerList, XFormChoices.OBJECT_LIST, "name", "name");
		} else {
			this._clusterServerChoices.setChoices(this._clusterServerList);
			this._clusterServerChoices.dirtyChoices();
		}
	}
	return this._clusterServerChoices;
};

ZaApp.prototype.getServerListChoices2 =
function(refresh) {
	if (refresh || this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerListChoices2 :: this._serverList is null ");				
		this._serverList = ZaServer.getAll(this);
	}
	if(refresh || this._serverChoices2 == null) {
		var arr = this._serverList.getArray();
		var mailServerArr = [];
		for (var i = 0 ; i < arr.length; ++i) {
			if (arr[i].attrs[ZaServer.A_zimbraMailboxServiceEnabled]){
				mailServerArr.push(arr[i]);
			}
		}
		if(this._serverChoices2 == null) {
			this._serverChoices2 = new XFormChoices(mailServerArr, XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
		} else {	
			this._serverChoices2.setChoices(mailServerArr);
			this._serverChoices2.dirtyChoices();
		}
	}
	return this._serverChoices2;	
}

ZaApp.prototype.getServerMap =
function(refresh) {
	if(refresh || this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerMap :: this._serverList is null ");						
		this._serverList = ZaServer.getAll(this);
	}
	if(refresh || this._serverMap == null) {
		this._serverMap = new Object();
		var cnt = this._serverList.getArray().length;
		for (var i = 0; i < cnt; i ++) {
			this._serverMap[this._serverList.getArray()[i].id] = this._serverList.getArray()[i];
		}
	}
	return this._serverMap;
}

ZaApp.prototype.getCosList =
function(refresh) {
	if (refresh || this._cosList == null) {
		this._cosList = ZaCos.getAll(this);
	}
	return this._cosList;	
}

ZaApp.prototype.getCosListChoices =
function(refresh) {
	if (refresh || this._cosList == null) {
		this._cosList = ZaCos.getAll(this);
	}
	if(refresh || this._cosListChoices == null) {
		if(this._cosListChoices == null)
			this._cosListChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");	

		this._cosListChoices.setChoices(this._cosList.getArray());
		this._cosListChoices.dirtyChoices();

	}
	return this._cosListChoices;	
}

/*
ZaApp.prototype.getStatusList =
function(refresh) {
	if (refresh || this._statusList == null) {
		this._statusList = ZaStatus.loadStatusTable();
	}
	return this._statusList;	
}
*/
/*
ZaApp.prototype.getAccountList =
function(refresh) {
	if (refresh || this._accountList == null) {
		this._accountList = ZaSearch.getAll(this).list;
	}
	return this._accountList;	
}*/

ZaApp.prototype.getGlobalConfig =
function(refresh) {
	if (refresh || this._globalConfig == null) {
		this._globalConfig = new ZaGlobalConfig(this);
	}
	return this._globalConfig;	
}

ZaApp.prototype.getInstalledSkins = 
function(refresh) {
	return this.getGlobalConfig(refresh).attrs[ZaGlobalConfig.A_zimbraInstalledSkin];
}

/**
* @param ev
* This listener is invoked by any controller that can create an ZaDomain object
**/
ZaApp.prototype.handleDomainCreation = 
function (ev) {
	if(ev) {
		this.searchDomains();
	}
}

/**
* @param ev
* This listener is invoked by any controller that can create an ZaCos object
**/
ZaApp.prototype.handleCosCreation = 
function (ev) {
	if(ev) {
		//add the new ZaCos to the controlled list
		if(ev.getDetails()) {
			if(!this._cosList) {
				this._cosList=ZaCos.getAll(this);
			} else {
				this._cosList.add(ev.getDetails());
			}
			if(this._cosListChoices == null) {
				this._cosListChoices = new XFormChoices(this._cosList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");	
			} else {
				this._cosListChoices.setChoices(this._cosList.getArray());
				this._cosListChoices.dirtyChoices();			
			}
		}
	}
}

/**
* @param ev
* This listener is invoked by any controller that can change an ZaCos object
**/
ZaApp.prototype.handleCosChange = 
function (ev) {
	if(ev) {
		//add the new ZaCos to the controlled list
		if(ev.getDetails()) {
			if(!this._cosList) {
				this._cosList=ZaCos.getAll(this);
			} else {
				//find the modified COS 
				var cnt = this._cosList.getArray().length;
				for(var i = 0; i < cnt; i ++) {
					if(this._cosList.getArray()[i].id == ev.getDetails().id) {
						this._cosList.getArray()[i] = ev.getDetails();
						break;
					}
				}
			}
			
			if(this._cosListChoices == null) {
				this._cosListChoices = new XFormChoices(this._cosList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");	
			} else {
				this._cosListChoices.setChoices(this._cosList.getArray());
				this._cosListChoices.dirtyChoices();			
			}
		}
	}
}
/**
* @param ev
* This listener is invoked by any controller that can create an ZaAccount object
**/
/*
ZaApp.prototype.handleAccountCreation = 
function (ev) {
	if(ev) {
		//add the new ZaAccount to the controlled list
		if(ev.getDetails()) {
			if(!this._accountList) {
				this._accountList=ZaSearch.getAll().list;
			} else {
				this._accountList.add(ev.getDetails());
			}
		}
	}
}
*/
/**
* @param ev
* This listener is invoked by ZaAccountViewController or any other controller that can remove an ZaAccount object
**/
/*
ZaApp.prototype.handleAccountRemoval = 
function (ev) {
	if(ev) {
		if(!this._accountList) {
			this._accountList=ZaSearch.getAll().list;
		} else {
			//remove the ZaAccount from the controlled list
			var detls = ev.getDetails();
			if(detls && (detls instanceof Array)) {
				for (var key in detls) {
					this._accountList.remove(detls[key]);
				}
			} else if(detls && (detls instanceof ZaAccount)) {
				this._accountList.remove(ev.getDetails());
			}
		}
	}
}
*/
/**
* @param ev
* This listener is invoked by ZaCosController or any other controller that can remove an ZaCos object
**/
ZaApp.prototype.handleCosRemoval = 
function (ev) {
	if(ev) {
		if(!this._cosList) {
			this._cosList=ZaCos.getAll(this);
		} else {
			//remove the ZaCos from the controlled list
			var detls = ev.getDetails();
			if(detls && (detls instanceof Array)) {
				for (var key in detls) {
					this._cosList.remove(detls[key]);
				}
			} else if(detls && (detls instanceof ZaCos)) {
				this._cosList.remove(ev.getDetails());
			}
		}
		if(this._cosListChoices == null) {
			this._cosListChoices = new XFormChoices(this._cosList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");	
		} else {
			this._cosListChoices.setChoices(this._cosList.getArray());
			this._cosListChoices.dirtyChoices();			
		}
	}
}

ZaApp.prototype.handleServerChange = 
function (ev) {
	if(ev) {
		if(this._serverList) {
			this._serverList=ZaServer.getAll(this);
			if(this._serverChoices == null) {
				this._serverChoices = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");
			} else {	
				this._serverChoices.setChoices(this._serverList.getArray());
				this._serverChoices.dirtyChoices();
			}

			if(this._serverChoices2 == null) {
				this._serverChoices2 = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
			} else {	
				this._serverChoices2.setChoices(this._serverList.getArray());
				this._serverChoices2.dirtyChoices();
			}

			this._serverMap = new Object();
			var cnt = this._serverList.getArray().length;
			for (var i = 0; i < cnt; i ++) {
				this._serverMap[this._serverList.getArray()[i].id] = this._serverList.getArray()[i];
			}						
		} 
	}
}

/**
* @param ev
* This listener is invoked by any controller that can remove an ZaServer object
**/
ZaApp.prototype.handleServerRemoval = 
function (ev) {
	if(ev) {
		if(!this._serverList) {
			this._serverList=ZaServer.getAll(this);
		} else {
			//remove the ZaCos from the controlled list
			var detls = ev.getDetails();
			if(detls && (detls instanceof Array)) {
				for (var key in detls) {
					this._serverList.remove(detls[key]);
				}
			} else if(detls && (detls instanceof ZaServer)) {
				this._serverList.remove(ev.getDetails());
			}
		}
		if(this._serverChoices == null) {
			this._serverChoices = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, "id", "name");
		} else {	
			this._serverChoices.setChoices(this._serverList.getArray());
			this._serverChoices.dirtyChoices();
		}

		if(this._serverChoices2 == null) {
			this._serverChoices2 = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
		} else {	
			this._serverChoices2.setChoices(this._serverList.getArray());
			this._serverChoices2.dirtyChoices();
		}		
		
		this._serverMap = new Object();
		var cnt = this._serverList.getArray().length;
		for (var i = 0; i < cnt; i ++) {
			this._serverMap[this._serverList.getArray()[i].id] = this._serverList.getArray()[i];
		}		
	}
}
/**
* @param ev
* This listener is invoked by ZaDomainController or any other controller that can remove an ZaDomain object
**/
ZaApp.prototype.handleDomainRemoval = 
function (ev) {
	if(ev) {
		this.searchDomains();
	}
}

/**
* @param ev
* This listener is invoked by ZaDomainController or any other controller that can remove an ZaDomain object
**/
ZaApp.prototype.handleDomainChange = 
function (ev) {
	if(ev) {
		this.searchDomains();
	}
}

ZaApp.prototype.handleSettingsChange = 
function(ev) {
	if(ev) {
		this._globalConfig = new ZaGlobalConfig(this);
	}
}

/**
* Returns the app's name.
*/
ZaApp.prototype.getName =
function() {
	return this._name;
}

/**
* Returns the app view manager.
*/
ZaApp.prototype.getAppViewMgr = 
function() {
	return this._appViewMgr;
}

ZaApp.prototype.createView =
function(viewName, elements) {
	this._appViewMgr.createView(viewName, elements);
}

ZaApp.prototype.pushView =
function(name) {
	this._currentViewId = this._appViewMgr.pushView(name);
}

ZaApp.prototype.popView =
function() {
	this._currentViewId = this._appViewMgr.popView();
}

ZaApp.prototype.setView =
function(name, force) {
	return this._appViewMgr.setView(name, force);
}

// Abstract methods


/**
* Clears an app's state.
*/
ZaApp.prototype.reset =
function(active) {
}
