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
* @class ZaApp
* @constructor ZaApp
* @param appCtxt instance of ZaAppCtxt
* @param container
* @author Greg Solovyev
**/
ZaApp = function(appCtxt, container) {
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
	this._serverIdChoices = null;
	this._serverMap = null;
	this._controllers = new Object();
	this.dialogs = {};
	this._tabGroup = null ;

}
ZaApp.instance = null;
ZaApp.getInstance = function (appCtxt, container) {
	if(!ZaApp.instance) {
	//	console.log("Trying to get ZaApp before it is instantiated") ;
		if(!AjxUtil.isEmpty(appCtxt) && !AjxUtil.isEmpty(container)) {
		//	console.log("Instantiating ZaApp ....") ; 
			ZaApp.instance = new ZaApp(appCtxt, container);
		}
	}
	return ZaApp.instance;
}
ZaApp.prototype.constructor = ZaApp;

ZaApp.prototype.toString = 
function() {
	return "ZaApp";
}

ZaApp.checkMyRight = function(targetType,targetBy,targetVal,right,attrs) {
	var soapDoc = AjxSoapDoc.create("CheckRightRequest", ZaZimbraAdmin.URN, null);
	var elGrantee = soapDoc.set("grantee", ZaZimbraAdmin.currentUserId);
	elGrantee.setAttribute("by","id");
	var elTarget = soapDoc.set("target", targetVal);
	elTarget.setAttribute("type", targetType);
	elTarget.setAttribute("by", targetBy);
	var elRight = soapDoc.set("right", right);
	var cnt = attrs.length;
	if(cnt>0) {
		var elAttrs = soapDoc.set("attrs","")
		for(var i=0;i<cnt;i++) {
			var elA = soapDoc.set("a",attrs[i].val,elAttrs);
			elA.setAttribute("n", attrs[i].n);
		}	
	}

	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_REQUESTING_ACCESS_RIGHTS ;
	try {
		var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CheckRightResponse;
		return resp;
	} catch (ex) {
		//not implemented yet
	}
	
}

ZaApp.prototype.initDialogs = function () {
	this.dialogs["errorDialog"] = this._appCtxt.getErrorDialog(true);
	this.dialogs["msgDialog"] = this._appCtxt.getMsgDialog(true);
	this.dialogs["confirmMessageDialog"] = this._appCtxt.getConfirmMsgDialog(true);
	this.dialogs["confirmMessageDialog2"] = this._appCtxt.getConfirmMsgDialog2(true);
}

ZaApp.prototype.getDomainAliasWizard = function (isEdit) {
    var dialog ;
    if (isEdit) {
        dialog = this.dialogs["editDomainAliasWizard"]
            = new ZaDomainAliasEditWizard(this._container, "400px", "80px", ZaMsg.Title_Edit_domain_alias);
    }else{
        dialog = this.dialogs["newDomainAliasWizard"]
            = new ZaDomainAliasWizard(this._container, "400px", "80px", ZaMsg.Title_Create_domain_alias);
    }
    return dialog;
}

ZaApp.prototype.launch =
function(appCtxt) {
	if(ZaSettings.DASHBOARD_VIEW && ZaApp.prototype.getDashBoardController) {
		var dashBoardController = ZaApp.getInstance().getDashBoardController(ZaSettings.DASHBOARD_VIEW);
		if(ZaApp.getInstance().getCurrentController()) {
			ZaApp.getInstance().getCurrentController().switchToNextView(dashBoardController, ZaDashBoardController.prototype.show,true);
		} else {					
			dashBoardController.show(true);
		}
	} else if(appNewUI) {
        var ctl = this._appCtxt.getAppController().getOverviewPanelController();
        var homePath = ZaTree.getPathByArray([ZaMsg.OVP_home]);
		ctl.getOverviewPanel().getFolderTree().setSelectionByPath(homePath);
        var historyObject = new ZaHistory(homePath, ZaMsg.OVP_home);
        ZaZimbraAdmin.getInstance().updateHistory(historyObject, true);
    }
    else {
		if(ZaSettings.TREE_ENABLED) {	
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.GLOBAL_STATUS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl.statusTi);
				//this.getStatusViewController().show(false);
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl.accountTi);		
				//this._appCtxt.getAppController()._showAccountsView(ZaItem.ACCOUNT,null);
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl.aliasTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl.dlTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl.resourceTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl._serversTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl._domainsTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.COS_LIST_VIEW]) {
				var ctl = this._appCtxt.getAppController().getOverviewPanelController();
				ctl.getOverviewPanel().getFolderTree().setSelection(ctl._cosTi);				
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.SERVER_STATS_VIEW]) {
			    var serverArray = [];
			    var serverList = ZaApp.getInstance().getServerList();
			    var currentServer = null;
			    if(serverList) {
			    	serverArray = serverList.getArray();
			    	if(serverArray && serverArray[0]) {
			    		serverArray[0].load();
			    		currentServer = serverArray[0];
			    	}
			    }
				var curController = ZaApp.getInstance().getServerStatsController();			
				curController.show(currentServer,false);				
			}
			
			/*if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DOMAIN_LIST_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
				this.searchDomains("");
			}	*/	
		} else {
			if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ACCOUNT_LIST_VIEW]) {
				ZaController.prototype._showAccountsView.call(ZaItem.ACCOUNT,null);
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.ALIAS_LIST_VIEW]) {
				ZaController.prototype._showAccountsView.call(ZaItem.ALIAS,null);
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.DL_LIST_VIEW]) {
				ZaController.prototype._showAccountsView.call(ZaItem.DL,null);
			} else if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.RESOURCE_LIST_VIEW]) {
				ZaController.prototype._showAccountsView.call(ZaItem.RESOURCE,null);
			} 
		}
	}
}

ZaApp.prototype.getAppCtxt = 
function() {
	return this._appCtxt;
}

ZaApp.prototype.getCurrentController = 
function() {
    var currentViewId = this._appViewMgr.getCurrentView();
	return this._controllers[currentViewId];
}

ZaApp.prototype.getControllerById =
function (id) {
	return this._controllers[id] ;
}

/**
* View controllers
**/
ZaApp.prototype.getStatusViewController =
function(viewId) {
	if(!viewId)
		viewId = ZaZimbraAdmin._STATUS;
			
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c  = this._controllers[viewId] = new ZaStatusViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getServerStatsController =
function(viewId) {

	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c  = this._controllers[viewId] = new ZaServerStatsController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getGlobalStatsController =
function(viewId) {
	if(!viewId)
		viewId = ZaZimbraAdmin._STATISTICS;
		
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaGlobalStatsController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getServerStatsListController =
function(viewId) {
	if(!viewId)
		viewId = ZaZimbraAdmin._SERVER_LIST_FOR_STATISTICS_VIEW;

	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaServerStatsListController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getGlobalConfigViewController =
function(viewId) {
	if(!viewId)
		viewId = ZaZimbraAdmin._GLOBAL_SETTINGS;
	
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c  = this._controllers[viewId] = new ZaGlobalConfigViewController(this._appCtxt, this._container, this);
		//c.addSettingsChangeListener(new AjxListener(this, ZaApp.prototype.handleSettingsChange));
		return c ;
	}
}

ZaApp.prototype.getSearchListController =
function() {
	if (this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] = new ZaSearchListController(this._appCtxt, this._container);
		this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW].addRemovalListener(new AjxListener(this.getSearchListController(), this.getSearchListController().handleRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._SEARCH_LIST_VIEW] ;
}

ZaApp.prototype.getSearchBuilderController =
function() {
	if (this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] = new ZaSearchBuilderController(this._appCtxt, this._container);
		this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW].addRemovalListener(new AjxListener(this.getSearchBuilderController(), this.getSearchBuilderController().handleRemoval));							
	}
	return this._controllers[ZaZimbraAdmin._SEARCH_BUILDER_VIEW] ;
}

ZaApp.prototype.getSearchBuilderToolbarController = ZaApp.prototype.getSearchBuilderController ;


ZaApp.prototype.getAccountListController =
function(viewId, newController) {
	if(!viewId)
		viewId = ZaZimbraAdmin._ACCOUNTS_LIST_VIEW;
			
	//this is used by SearchListController to associate its view with a new 
	//account list controller
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else if (viewId || newController) {
		var c = this._controllers[viewId] = new ZaAccountListController(this._appCtxt, this._container, this);
		c.addRemovalListener(new AjxListener(c, c.handleRemoval));							
		c.addCreationListener(new AjxListener(c, c.handleCreation));									
		return c ;
	}

}

ZaApp.prototype.getAccountViewController =
function(isAlias) {
	var c = new ZaAccountViewController(this._appCtxt, this._container, this);
	var viewId = ZaZimbraAdmin._ACCOUNTS_LIST_VIEW ;
	if (isAlias) {
		viewId = ZaZimbraAdmin._ALIASES_LIST_VIEW ;
	}
	c.addChangeListener(new AjxListener(this.getAccountListController(viewId), ZaAccountListController.prototype.handleChange));
	c.addCreationListener(new AjxListener(this.getAccountListController(viewId), ZaAccountListController.prototype.handleCreation));	
	c.addRemovalListener(new AjxListener(this.getAccountListController(viewId), ZaAccountListController.prototype.handleRemoval));			
	return c ;
}

ZaApp.prototype.getAdminExtListController = 
function() {
	if (this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] = new ZaAdminExtListController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].addRemovalListener(new AjxListener(this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].handleRemoval));							
		this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].addCreationListener(new AjxListener(this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].handleCreation));			
		this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].addChangeListener(new AjxListener(this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW].handleChange));
	}
	
	return this._controllers[ZaZimbraAdmin._ADMIN_ZIMLET_LIST_VIEW] ;
}

ZaApp.prototype.getZimletListController =
function() {
	if (this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW] = new ZaZimletListController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].addRemovalListener(new AjxListener(this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].handleRemoval));							
		this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].addCreationListener(new AjxListener(this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].handleCreation));			
		this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].addChangeListener(new AjxListener(this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW], this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW].handleChange));
	}
	
	return this._controllers[ZaZimbraAdmin._ZIMLET_LIST_VIEW] ;
}

ZaApp.prototype.getZimletController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c  = new ZaZimletViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getDistributionListController = 
function (viewId) {
		if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = new ZaDLController(this._appCtxt, this._container, this);
		c.addCreationListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW), ZaAccountListController.prototype.handleCreation));			
		c.addRemovalListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW), ZaAccountListController.prototype.handleRemoval));			
		c.addChangeListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._DISTRIBUTION_LISTS_LIST_VIEW), ZaAccountListController.prototype.handleChange));
		return c ;
	}
	
};

ZaApp.prototype.getResourceController = 
function (viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = new ZaResourceController(this._appCtxt, this._container, this);
		c.addCreationListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._RESOURCE_LIST_VIEW), ZaAccountListController.prototype.handleCreation));			
		c.addRemovalListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._RESOURCE_LIST_VIEW), ZaAccountListController.prototype.handleRemoval));			
		c.addChangeListener(new AjxListener(this.getAccountListController(ZaZimbraAdmin._RESOURCE_LIST_VIEW), ZaAccountListController.prototype.handleChange));
		return c ;
	}
};

ZaApp.prototype.getDomainListController =
function(viewId, newController) {
	if(!viewId)
		viewId = ZaZimbraAdmin._DOMAINS_LIST_VIEW;
			
	//this is used by SearchListController to associate its view with a new 
	//domain list controller
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else if (viewId || newController) {
		var c = this._controllers[viewId] = new ZaDomainListController(this._appCtxt, this._container, this);
		c.addCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));					
		c.addRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));
        c.addChangeListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainChange));
		return c ;
	}
}

ZaApp.prototype.getDomainController =
function(viewId) {
	
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaDomainController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		c.addChangeListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainChange));
		c.addChangeListener(new AjxListener(c, ZaDomainController.prototype.handleDomainChange));
		c.addCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));
		c.addCreationListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleCreation));
		c.addRemovalListener(new AjxListener(this.getDomainListController(), this.getDomainListController().handleRemoval));
		c.addRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));

		return c ;
	}
}

ZaApp.prototype.getMTAListController =
function () {
	if (this._controllers[ZaZimbraAdmin._POSTQ_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._POSTQ_VIEW] = new ZaMTAListController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._POSTQ_VIEW];
}

ZaApp.prototype.getMTAController =
function (viewId) {

	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaMTAController(this._appCtxt, this._container, this);
		c.addChangeListener(new AjxListener(this.getMTAListController(), ZaMTAListController.prototype.handleMTAChange));		
		c.addChangeListener(new AjxListener(c, ZaMTAController.prototype.handleMTAChange));				
		return c ;
	}
}

ZaApp.prototype.getServerListController =
function() {
	if (this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW] = new ZaServerListController(this._appCtxt, this._container, this);								
	}
	return this._controllers[ZaZimbraAdmin._SERVERS_LIST_VIEW];
}

ZaApp.prototype.getServerController =
function(viewId) {

	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaServerController(this._appCtxt, this._container, this);
		c.addServerChangeListener(new AjxListener(this, ZaApp.prototype.handleServerChange));		
		c.addServerChangeListener(new AjxListener(this.getServerListController(), ZaServerListController.prototype.handleServerChange));		
		if(ZaSettings.TREE_ENABLED) {
			c.addServerChangeListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleServerChange));									
		}
		return c ;
	}
	
}

ZaApp.prototype.getCosListController =
function() {
	if (this._controllers[ZaZimbraAdmin._COS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._COS_LIST_VIEW] = new ZaCosListController(this._appCtxt, this._container, this);
		this._controllers[ZaZimbraAdmin._COS_LIST_VIEW].addRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));
		if(ZaSettings.TREE_ENABLED) {
			this._controllers[ZaZimbraAdmin._COS_LIST_VIEW].addRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosRemoval));
		}
	}
	return this._controllers[ZaZimbraAdmin._COS_LIST_VIEW];
}


ZaApp.prototype.getCosController =
function() {
	var c = new ZaCosController(this._appCtxt, this._container, this);
		
	c.addChangeListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleChange));
	if(ZaSettings.TREE_ENABLED) {
		c.addChangeListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosChange));						
	}
	c.addCreationListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCreation));	
	if(ZaSettings.TREE_ENABLED) {
		c.addCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosCreation));				
	}
	c.addRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));			
	c.addRemovalListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleRemoval));			
	if(ZaSettings.TREE_ENABLED) {
		c.addRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleCosRemoval));						
	}
	return c ;

}

ZaApp.prototype.getHelpViewController =
function(viewId) {

	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaHelpViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getMigrationWizController = 
function(viewId) {
	
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaMigrationWizController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.searchDomains = function(query) {
	var busyId = Dwt.getNextId () ;
	var callback = new AjxCallback(this, this.domainSearchCallback, {busyId:busyId});
	var searchParams = {
			query:query, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:"0",
			sortAscending:"1",
			limit:ZaDomain.MAXSEARCHRESULTS,
			callback:callback,
			controller: this.getCurrentController(),
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false,
			attrs:[ZaDomain.A_description, ZaDomain.A_domainName,ZaDomain.A_zimbraDomainStatus,ZaItem.A_zimbraId, ZaDomain.A_domainType]			
	}
	ZaSearch.searchDirectory(searchParams);
}

ZaApp.prototype.scheduledSearchDomains = function(domainItem) {
	var busyId = Dwt.getNextId () ;
	var callback = new AjxCallback(this, this.domainSearchCallback, {domainItem:domainItem, busyId:busyId});
	var searchParams = {
			query: this._domainQuery, 
			types:[ZaSearch.DOMAINS],
			sortBy:ZaDomain.A_domainName,
			offset:"0",
			sortAscending:"1",
			limit:ZaDomain.MAXSEARCHRESULTS,
			callback:callback,
			controller: this.getCurrentController(),
			showBusy:true,
			busyId:busyId,
			busyMsg:ZaMsg.BUSY_SEARCHING_DOMAINS,
			skipCallbackIfCancelled:false,
			attrs:[ZaDomain.A_domainName, ZaItem.A_zimbraId]			
	}
	ZaSearch.searchDirectory(searchParams);
//	DBG.println(AjxDebug.DBG1, "Searching for domains "+ ev.keyCode +" char code " + (new Date()).getTime());
}

ZaApp.prototype.domainSearchCallback = 
function (params, resp) {
	var domainItem = params.domainItem ? params.domainItem : null; 
		
	try {
		if(params.busyId)
			this._appCtxt.getShell().setBusy(false, params.busyId);
			
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaListViewController.prototype.searchCallback"));
		}
		if(resp.isException()) {
			//throw(resp.getException());
			ZaSearch.handleTooManyResultsException(resp.getException(), "ZaApp.prototype.domainSearchCallback");
		} else {
			ZaSearch.TOO_MANY_RESULTS_FLAG = false ;
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var domainList = new ZaItemList(ZaDomain);	
			domainList.loadFromJS(response);
			domainList.loadEffectiveRights();
			if(ZaSettings.TREE_ENABLED) {
				this._appCtxt.getAppController().getOverviewPanelController().updateDomainList(domainList);
			}
			if (domainItem != null && domainItem instanceof XFormItem && this._domainList.size() <= 0) {
				domainItem.setError(ZaMsg.ERROR_NO_SUCH_DOMAIN) ;
				var event = new DwtXFormsEvent(this, domainItem, domainItem.getInstanceValue());
				domainItem.getForm().notifyListeners(DwtEvent.XFORMS_VALUE_ERROR, event);
			}
		}
	} catch (ex) {
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this.getCurrentController()._handleException(ex, "ZaApp.prototype.domainSearchCallback");	
		} else {
			this.getCurrentController().popupErrorDialog(ZaMsg.queryParseError, ex);
		}		
	}
}
ZaApp.prototype.getDomainList =
function(refresh) {
	if (refresh || this._domainList == null) {
		this._domainList = ZaDomain.getAll();
		this._domainList.loadEffectiveRights();
	}
	return this._domainList;	
}

ZaApp.prototype.getSavedSearchList =
function (refresh) {
	if (refresh || ZaSearch.SAVED_SEARCHES.length <=0) {
        if (ZaSearchField.canViewSavedSearch) {
		    ZaSearch.updateSavedSearch (ZaSearch.getSavedSearches()) ;
        }
	}
	
	return ZaSearch.SAVED_SEARCHES ;
}

ZaApp.prototype.getServerByName =
function(serverName) {
	if (this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerByName :: this._serverList is null ");
		this._serverList = ZaServer.getAll();
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
		this._serverList = ZaServer.getAll();
		
		if(this._serverList) {
			var tmpArray = this._serverList.getArray();
			this._mbsList = ZaItemList(ZaServer);
			if(tmpArray) {
				var resArray = new Array();
				var cnt = tmpArray.length;
				for(var i=0;i>cnt;i++) {
					if(tmpArray[i].attrs[ZaServer.A_zimbraMailboxServiceInstalled]) {
						this._mbsList.add(tmpArray[i]);
					}
				}
			}
		}
	}
	return this._serverList;	
}

ZaApp.prototype.getMbsList =
function(refresh) {
	if (refresh || this._mbsList == null) {
		this._mbsList = ZaServer.getAllMBSs();
	}
	return this._mbsList;	
}

ZaApp.prototype.getPostQList = 
function (refresh) {
	if (refresh || this._postqList == null) {
		this._postqList = ZaMTA.getAll();
	}
	return this._postqList;	
}

ZaApp.prototype.getMailServers =
function(refresh) {
	if (refresh || this._mbsList == null) {
		this._mbsList = ZaServer.getAllMBSs([ZaServer.A_ServiceHostname, ZaServer.A_description, ZaServer.A_zimbraServiceEnabled, ZaServer.A_zimbraServiceInstalled, ZaItem.A_zimbraId]);
		//this._serverList = ZaServer.getAll([ZaServer.A_ServiceHostname, ZaServer.A_description, ZaServer.A_zimbraServiceEnabled, ZaServer.A_zimbraServiceInstalled, ZaItem.A_zimbraId]);
	}
	var resArray = new Array();
	var tmpArray = this._mbsList.getArray();
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
		this._serverList = ZaServer.getAll();
	}
	if(refresh || this._serverChoices == null) {
		var hashMap = this._serverList.getIdHash();
		var mailServerArr = [];
		for (var i in hashMap) {
			if (hashMap[i].attrs[ZaServer.A_zimbraMailboxServiceEnabled]){
				mailServerArr.push(hashMap[i]);
			}
		}
		if(this._serverChoices == null) {
			this._serverChoices = new XFormChoices(mailServerArr, XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
		} else {	
			this._serverChoices.setChoices(mailServerArr);
			this._serverChoices.dirtyChoices();
		}
	}
	return this._serverChoices;	
}

ZaApp.prototype.getServerIdListChoices =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll();
	}
	if(refresh || this._serverIdChoices == null) {
		var hashMap = this._serverList.getIdHash();
		var mailServerArr = [];
		for (var i in hashMap) {
			if (hashMap[i].attrs[ZaServer.A_zimbraMailboxServiceEnabled]){
				var obj = new Object();
				obj[ZaServer.A_ServiceHostname] = hashMap[i].attrs[ZaServer.A_ServiceHostname];
				obj.id = hashMap[i].id;
				mailServerArr.push(obj);
			}
		}
		if(this._serverIdChoices == null) {
			this._serverIdChoices = new XFormChoices(mailServerArr, XFormChoices.OBJECT_LIST, "id", ZaServer.A_ServiceHostname);
		} else {	
			this._serverIdChoices.setChoices(mailServerArr);
			this._serverIdChoices.dirtyChoices();
		}
	}
	return this._serverIdChoices;	
}

ZaApp.prototype.getServerMap =
function(refresh) {
	if(refresh || this._serverList == null) {
//		DBG.println(AjxDebug.DBG1, "ZaApp.prototype.getServerMap :: this._serverList is null ");						
		this._serverList = ZaServer.getAll();
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
	if (refresh || !this._cosList) {
		var query = "";
		if(!ZaZimbraAdmin.hasGlobalCOSSListAccess()) {
			var cosNameList = ZaApp.getInstance()._cosNameList;
			if(AjxUtil.isEmpty(cosNameList)) {
				ZaApp.getInstance()._cosNameList = cosNameList = ZaCos.getEffectiveCosList(ZaZimbraAdmin.currentAdminAccount.id);
			}
			if(cosNameList.length == 0) {
				this._cosList = new ZaItemList(ZaCos);
				return this._cosList;
			} 
			for(var i = 0; i < cosNameList.length; i++)
				query += "(" + ZaCos.A_name + "=" + cosNameList[i] + ")";
			if(cosNameList.length > 1)
				query = "(|" + query + ")"; 
		}

		var searchParams = {
			query: query ,
			types:[ZaSearch.COSES],
			sortBy:"id",
			offset:0,
			sortAscending:"1",
			limit:ZaDomain.RESULTSPERPAGE,
			callback:null,
			attrs:[ZaCos.A_name,ZaCos.A_description].join(),
			controller: this.getCurrentController()
		}
		var response = ZaSearch.searchDirectory(searchParams).Body.SearchDirectoryResponse;
		this._cosList = new ZaItemList(ZaCos);		
		this._cosList.loadFromJS(response);
	}
	return this._cosList;	
}

ZaApp.prototype.getCosListChoices =
function(refresh) {
	if (refresh || this._cosList == null) {
		
		//this._cosList = ZaCos.getAll(this);
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

ZaApp.prototype.getAccountStats =
function(refresh) {
    if (refresh || this._accountStats == null) {
        this._accountStats = ZaSearch.getAccountStats();
    }
    return this._accountStats;
}

ZaApp.prototype.getGlobalConfig =
function(refresh) {
	if (refresh || this._globalConfig == null) {
		this._globalConfig = new ZaGlobalConfig();
	}
	return this._globalConfig;	
}

ZaApp.prototype.getInstalledSkins = 
function(refresh) {
    try {
        if (refresh || this._installedSkins == null) {
            var soapDoc = AjxSoapDoc.create("GetAllSkinsRequest", ZaZimbraAdmin.URN, null);

	        var csfeParams = new Object();
	        csfeParams.soapDoc = soapDoc;
	        var reqMgrParams = {} ;
	        reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
            try {
                this._installedSkins = [];
                var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetAllSkinsResponse;
                if (resp && resp.skin) {
                    for(var i = 0; i < resp.skin.length;i++) {
                        this._installedSkins.push(resp.skin[i].name);
                    }
                }
            } catch (ex) {
                //not implemented yet
            }
        }
    	return this._installedSkins;
    }catch (e) {
        return null ;
    }
}

/**
* @param ev
* This listener is invoked by any controller that can create an ZaDomain object
**/
ZaApp.prototype.handleDomainCreation = 
function (ev) {
	if(ev) {
		//update the overpanel
        this.searchDomains();
        //update the domain list. We separate two search domains because domain list view only need the first page
        // result, but the overpanel will show more results. It could potentially be combined into one search.
        this.getDomainListController().show ();

        if(appNewUI)
            ZaZimbraAdmin.getInstance().getOverviewPanelController().refreshRelatedTree (ev.getDetails());
	}
}

/**
* @param ev
* This listener is invoked by ZaCosController or any other controller that can remove an ZaCos object
**/
ZaApp.prototype.handleCosRemoval = 
function (ev) {
	if(ev) {
		if(!this._cosList) {
			this._cosList=ZaCos.getAll();
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

            if(appNewUI)
                ZaZimbraAdmin.getInstance().refreshHistoryTreeByDelete(ev.getDetails());
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
			this._serverList=ZaServer.getAll();
			if(this._serverChoices == null) {
				this._serverChoices = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
			} else {	
				this._serverChoices.setChoices(this._serverList.getArray());
				this._serverChoices.dirtyChoices();
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
function(viewName, elements, tabParams) {
	this._appViewMgr.createView(viewName, elements);
	
	//create new tabs or modify tab
	/* tabParams {
	 * 	openInNewTab: true/false,
	 *  tabId: The tabId which will be either set for new Tab or the updating tab
	 *  tab: the tab to be updated
	 *  view: 
	 * }
	 */
	
	if (tabParams.openInNewTab) {
		this.createTab (tabParams);
	}else{
		this.updateTab (tabParams.tab, tabParams.tabId);
	}
	
}

ZaApp.prototype.createTab =
function () {
	if (arguments.length == 0) return;
	var tabId;
	var closable = true;
	var selected = true;
	var onOpen = null;
	if (typeof arguments[0] == "object") {
		tabId = arguments[0]["tabId"];
		closable = AjxUtil.isEmpty(arguments[0]["closable"]) ? true : false;
		selected = AjxUtil.isEmpty(arguments[0]["selected"]) ? true : false;
		onOpen = AjxUtil.isEmpty(arguments[0]["onOpen"]) ? null : arguments[0]["onOpen"];
	} else {
		tabId = arguments[0]; 
	}
	var tabGroup = this.getTabGroup() ;
	var appView = this.getViewById(tabId) [ZaAppViewMgr.C_APP_CONTENT] ;
	var params = {
		id: tabId ,
		icon: appView.getTabIcon (),
		label: appView.getTabTitle () ,
		toolTip: appView.getTabToolTip () || appView.getTabTitle () ,
		closable: closable,
		selected: selected,
		onOpen:onOpen
	}
	
	var tab = new ZaAppTab (tabGroup,params );
	/*
				entry.name, entry.getTabIcon() , null, null, 
				true, true, ZaApp.getInstance()._currentViewId) ;
	tab.setToolTipContent( entry.getTabToolTip()) ; */
}

/**
 * tab: the tab to be updated
 * tabId: the new id associated with the tab
 */
ZaApp.prototype.updateTab =
function ( tab, tabId ) {
	
	var tabGroup = this.getTabGroup() ;
	if (tabGroup._searchTab && tabGroup._searchTab == tab) {
		this.updateSearchTab() ;
	}else{	
		var appView = this.getViewById(tabId)[ZaAppViewMgr.C_APP_CONTENT];
		var icon = appView.getTabIcon (); //the view class should implement the getTabIcon () function
		var titleLabel = appView.getTabTitle () ; //the view class should implement the getTabTitle () function
	
		tab.setToolTipContent (appView.getTabToolTip() || appView.getTabTitle ()) ;
		tab.resetLabel (titleLabel) ;
		tab.setImage (icon) ;
	}
	
	tab.setTabId (tabId) ; //set the new tabId to the existing tab
	
	if (! tab.isSelected()) {
		tabGroup.selectTab(tab);
	}
}

ZaApp.prototype.updateSearchTab =
function () {
	var searchTab = this.getTabGroup().getSearchTab() ;
	searchTab.setImage (ZaSearchListView.prototype.getTabIcon()) ;
	searchTab.resetLabel (ZaSearchListView.prototype.getTabTitle()) ;
	searchTab.setToolTipContent (
		ZaSearchListView.prototype.getTabToolTip.call(this._controllers[searchTab.getTabId()])) ;
}

ZaApp.prototype.pushView =
function(name, openInNewTab, openInSearchTab) {
	this._currentViewId = this._appViewMgr.pushView(name);
	//may need to select the corresponding tab, but will cause deadlock
	/* 
	var tabGroup = this.getTabGroup () ;
	tabGroup.selectTab (tabGroup.getTabById(this._currentViewId)) ;
	*/
	//check if there is a tab associated with the view
    if (!appNewUI) {
	var tabGroup = this.getTabGroup () ;
	var cTab = tabGroup.getTabById(this._currentViewId);
	if (cTab) {
		this.updateTab (cTab, this._currentViewId) ;
	}else if (openInNewTab) {
		this.createTab (this._currentViewId) ;
	}else if (openInSearchTab) {
		this.updateTab (tabGroup.getSearchTab(), this._currentViewId) ; 
	}else {
		this.updateTab (tabGroup.getMainTab(), this._currentViewId) ; 
	}
    }
}

ZaApp.prototype.popView =
function() {
	var oldCurrentViewId = this._currentViewId ;
	this._currentViewId = this._appViewMgr.popView();
    if (!appNewUI) {
	this.getTabGroup().removeCurrentTab(true) ;
    }
	//dispose the view and remove the controller
	this.disposeView (oldCurrentViewId);
	
}

ZaApp.prototype.disposeView =
function (viewId, closeHidden) {
	var view = this.getViewById (viewId) ;
	if(closeHidden) {
		this._appViewMgr.removeHiddenView(viewId);
	}
	for (var n in view) {
		if (view[n] instanceof DwtComposite) {
			view[n].dispose () ;
		}else{
			view[n] = null ;
		}
	} 
	
	//destroy the controller also
	if (this._controllers[viewId] != null) {
		this._controllers[viewId] = null ;
	} 
}

ZaApp.prototype.setView =
function(name, force) {
	return this._appViewMgr.setView(name, force);
}

ZaApp.prototype.getViewById =
function (id) {
	return	this.getAppViewMgr()._views[id] ;
}
// Abstract methods


/**
* Clears an app's state.
*/
ZaApp.prototype.reset =
function(active) {
}

ZaApp.prototype.setTabGroup =
function (tabGroup) {
	this._tabGroup = tabGroup ;	
}

ZaApp.prototype.getTabGroup =
function () {
	return this._tabGroup ;	
	
}
