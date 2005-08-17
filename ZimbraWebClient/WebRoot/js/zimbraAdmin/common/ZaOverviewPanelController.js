// TODO: Make sure current app is highlighted
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

ZaOverviewPanelController._ACCOUNTS_SUB_TREE = 1000;
ZaOverviewPanelController._STATISTICS_SUB_TREE = 10000;

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
			ti1.setImage(ZaImg.I_DOMAIN);
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
	this._statusTi.setImage(ZaImg.I_STATUS);
	this._statusTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._STATUS);

	this.statisticsTi = new DwtTreeItem(tree);
	this.statisticsTi.setText(ZaMsg.OVP_statistics);
	this.statisticsTi.setImage(ZaImg.I_STATS);
	this.statisticsTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._STATISTICS);
	
	try {
		//add server nodes
		var serverList = this._app.getServerList().getArray();
		if(serverList && serverList.length) {
			var cnt = serverList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this.statisticsTi);			
				ti1.setText(serverList[ix].name);	
				ti1.setImage(ZaImg.I_STATSBYSERVER);
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
	this._accountsTi.setImage(ZaImg.I_ACCOUNT);
	this._accountsTi.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._ACCOUNTS);

	try {
		//add domain nodes
		var domainList = this._app.getDomainList().getArray();
		if(domainList && domainList.length) {
			var cnt = domainList.length;
			for(var ix=0; ix< cnt; ix++) {
				var ti1 = new DwtTreeItem(this._accountsTi);			
				ti1.setText(domainList[ix].name);	
				ti1.setImage(ZaImg.I_ACCOUNTBYDOMAIN);
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
	ti.setImage(ZaImg.I_COS);
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._COS);
	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_domains);
	ti.setImage(ZaImg.I_DOMAIN);
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._DOMAINS);

	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_servers);
	ti.setImage(ZaImg.I_SERVER);
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._SERVERS);
	
	ti = new DwtTreeItem(tree);
	ti.setText(ZaMsg.OVP_global);
	ti.setImage(ZaImg.I_GLOBALSETTINGS);
	ti.setData(ZaOverviewPanelController._TID, ZaOverviewPanelController._GLOBAL_SETTINGS);	
	
	tree.setSelection(this._statusTi, true);
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
						DBG.println("cos");
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getCosListController(), ZaCosListController.prototype.show, ZaCos.getAll(this._app));
						} else {
							this._app.getCosListController().show(ZaCos.getAll(this._app));
						}
						break;
					case ZaOverviewPanelController._ACCOUNTS:
						DBG.println("accounts");
						this._app.getAccountListController().setPageNum(1);					
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getAccountListController(), ZaAccountListController.prototype.show,ZaAccount.getAll(this._app));
						} else {					
							this._app.getAccountListController().show(ZaAccount.getAll(this._app));
						}
						var curQuery = new ZaAccountQuery("", false, "");							
						this._app.getAccountListController().setQuery(curQuery);	
						break;					
					case ZaOverviewPanelController._DOMAINS:
						DBG.println("domains");				
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getDomainListController(), ZaDomainListController.prototype.show, ZaDomain.getAll());
						} else {					
							this._app.getDomainListController().show(ZaDomain.getAll());
						}
						//this._app.getDomainListController().show(ZaDomain.getAll());
						break;			
					case ZaOverviewPanelController._SERVERS:
						DBG.println("servers");				
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerListController(), ZaServerListController.prototype.show, ZaServer.getAll());
						} else {					
							this._app.getServerListController().show(ZaServer.getAll());
						}
						//this._app.getDomainListController().show(ZaDomain.getAll());
						break;									
					case ZaOverviewPanelController._STATUS:
						DBG.println("status");
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getStatusViewController(),ZaStatusViewController.prototype.show, null);
						} else {					
							this._app.getStatusViewController().show();
						}
						break;		
					case ZaOverviewPanelController._STATISTICS:
						DBG.println("statistics");
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalStatsController(),ZaGlobalStatsController.prototype.show, null);
						} else {					
							this._app.getGlobalStatsController().show();
						}
						break;		
					case ZaOverviewPanelController._GLOBAL_SETTINGS:
						DBG.println("globalsettings");
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getGlobalConfigViewController(),ZaGlobalConfigViewController.prototype.show, this._app.getGlobalConfig());
						} else {					
							this._app.getGlobalConfigViewController().show(this._app.getGlobalConfig());
						}
						break;		
					case ZaOverviewPanelController._ACCOUNTS_SUB_TREE:
						DBG.println("accounts by domain");
						this.setCurrentDomain(ev.item.getData(ZaOverviewPanelController._OBJ_ID));
						this._app.getAccountListController().setPageNum(1);	
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getAccountListController(), ZaAccountListController.prototype.show,ZaAccount.searchByDomain(this._currentDomain, 1, ZaAccount.A_uid, true, this._app));
						} else {					
							this._app.getAccountListController().show(ZaAccount.searchByDomain(this._currentDomain, 1, ZaAccount.A_uid, true, this._app));
						}
						var curQuery = new ZaAccountQuery("", true,this._currentDomain);							
						this._app.getAccountListController().setQuery(curQuery);
						break;		
					case ZaOverviewPanelController._STATISTICS_SUB_TREE:
						DBG.println("statistics by server");
						this.setCurrentServer(this._app.getServerList().getItemById(ev.item.getData(ZaOverviewPanelController._OBJ_ID)));
						if(this._app.getCurrentController()) {
							this._app.getCurrentController().switchToNextView(this._app.getServerStatsController(), ZaServerStatsController.prototype.show,this._currentServer);
						} else {					
							this._app.getServerStatsController().show(this._currentServer);
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
