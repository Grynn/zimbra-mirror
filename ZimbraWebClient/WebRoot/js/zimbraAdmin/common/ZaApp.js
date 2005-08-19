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
	this._cosListChoices = null;//new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");	
	this._domainListChoices = null;//new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");	
	this._serverChoices = null; 
	this._serverChoices2 = null; 	
	this._serverMap = null;
}

ZaApp.prototype.constructor = ZaApp;

ZaApp.prototype.toString = 
function() {
	return "ZaApp";
}

ZaApp.prototype.launch =
function(appCtxt) {
	this.getStatusViewController().show();
	//this.getAccountListController().show(ZaAccount.getAll());
}

ZaApp.prototype.setActive =
function(active) {
	if (active) {
		this.getStatusViewController().show();
	}
}

ZaApp.prototype.getAppCtxt = 
function() {
	return this._appCtxt;
}

ZaApp.prototype.setCurrentController = 
function(ctrlr) {
	this._currentController = ctrlr;
}

ZaApp.prototype.getCurrentController = 
function(ctrlr) {
	return this._currentController;
}


/**
* View controllers
**/
ZaApp.prototype.getStatusViewController =
function() {
	if (this._statusViewController == null)
		this._statusViewController = new ZaStatusViewController(this._appCtxt, this._container, this);
	return this._statusViewController;
}

ZaApp.prototype.getServerStatsController =
function() {
	if (this._serverStatsController == null)
		this._serverStatsController = new ZaServerStatsController(this._appCtxt, this._container, this);
	return this._serverStatsController;
}

ZaApp.prototype.getGlobalStatsController =
function() {
	if (this._globalStatsController == null)
		this._globalStatsController = new ZaGlobalStatsController(this._appCtxt, this._container, this);
	return this._globalStatsController;
}

ZaApp.prototype.getGlobalConfigViewController =
function() {
	if (this._globalConfigViewController == null)
		this._globalConfigViewController = new ZaGlobalConfigViewController(this._appCtxt, this._container, this);
	return this._globalConfigViewController;
}

ZaApp.prototype.getAccountListController =
function() {
	if (this._accountListController == null) {
		this._accountListController = new ZaAccountListController(this._appCtxt, this._container, this);
		this._accountListController.addAccountRemovalListener(new AjxListener(this, ZaApp.prototype.handleAccountRemoval));					
		this._accountListController.addAccountRemovalListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleAccountRemoval));							
	}
	return this._accountListController;
}

ZaApp.prototype.getAccountViewController =
function() {
	if (this._accountViewController == null) {
		this._accountViewController = new ZaAccountViewController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		this._accountViewController.addAccountChangeListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleAccountChange));
		this._accountViewController.addAccountCreationListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleAccountCreation));	
		this._accountViewController.addAccountRemovalListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleAccountRemoval));			
		this._accountViewController.addAccountCreationListener(new AjxListener(this, ZaApp.prototype.handleAccountCreation));			
		this._accountViewController.addAccountRemovalListener(new AjxListener(this, ZaApp.prototype.handleAccountRemoval));					
	}
	return this._accountViewController;
}

ZaApp.prototype.getNewAccountWizController =
function() {
	if (this._newAccountWizController == null) {
		this._newAccountWizController = new ZaNewAccountWizController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		this._newAccountWizController.addAccountCreationListener(new AjxListener(this.getAccountListController(), ZaAccountListController.prototype.handleAccountCreation));	
		this._newAccountWizController.addAccountCreationListener(new AjxListener(this, ZaApp.prototype.handleAccountCreation));			
	}
	return this._newAccountWizController;
}


ZaApp.prototype.getDomainListController =
function() {
	if (this._domainListController == null) {
		this._domainListController = new ZaDomainListController(this._appCtxt, this._container, this);
		
		this._domainListController.addDomainCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));					
		this._domainListController.addDomainCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainCreation));				

		this._domainListController.addDomainRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));							
		this._domainListController.addDomainRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainRemoval));						
		
	}
	return this._domainListController;
}

ZaApp.prototype.getDomainController =
function() {
	if (this._domainController == null) {
		this._domainController = new ZaDomainController(this._appCtxt, this._container, this);
		//since we are creating the account controller now - register all the interested listeners with it
		this._domainController.addDomainChangeListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainChange));

		this._domainController.addDomainCreationListener(new AjxListener(this, ZaApp.prototype.handleDomainCreation));					
		this._domainController.addDomainCreationListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainCreation));	
		this._domainController.addDomainCreationListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainCreation));				

		this._domainController.addDomainRemovalListener(new AjxListener(this.getDomainListController(), ZaDomainListController.prototype.handleDomainRemoval));			
		this._domainController.addDomainRemovalListener(new AjxListener(this, ZaApp.prototype.handleDomainRemoval));							
		
		this._domainController.addDomainRemovalListener(new AjxListener(this._appCtxt.getAppController().getOverviewPanelController(), ZaOverviewPanelController.prototype.handleDomainRemoval));						
	}

	return this._domainController;
}

ZaApp.prototype.getServerListController =
function() {
	if (this._serverListController == null) {
		this._serverListController = new ZaServerListController(this._appCtxt, this._container, this);
		this._serverListController.addServerRemovalListener(new AjxListener(this, ZaApp.prototype.handleServerRemoval));	
	
	}
	return this._serverListController;
}

ZaApp.prototype.getServerController =
function() {
	if (this._serverController == null) {
		this._serverController = new ZaServerController(this._appCtxt, this._container, this);
		this._serverController.addServerChangeListener(new AjxListener(this, ZaApp.prototype.handleServerChange));		
		this._serverController.addServerChangeListener(new AjxListener(this.getServerListController(), ZaServerListController.prototype.handleServerChange));		
	}
	return this._serverController;
}

ZaApp.prototype.getCosListController =
function() {
	if (this._cosListController == null) {
		this._cosListController = new ZaCosListController(this._appCtxt, this._container, this);
		this._cosListController.addCosRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));			
	}
	return this._cosListController;
}


ZaApp.prototype.getCosController =
function() {
	if (this._cosController == null) {
		this._cosController = new ZaCosController(this._appCtxt, this._container, this);
		//since we are creating the COS controller now - register all the interested listeners with it
		this._cosController.addCosChangeListener(new AjxListener(this, ZaApp.prototype.handleCosChange));			
		this._cosController.addCosChangeListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosChange));

		this._cosController.addCosCreationListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosCreation));	
		this._cosController.addCosCreationListener(new AjxListener(this, ZaApp.prototype.handleCosCreation));			

		this._cosController.addCosRemovalListener(new AjxListener(this, ZaApp.prototype.handleCosRemoval));			
		this._cosController.addCosRemovalListener(new AjxListener(this.getCosListController(), ZaCosListController.prototype.handleCosRemoval));			
	}
	return this._cosController;
}

ZaApp.prototype.getDomainList =
function(refresh) {
	if (refresh || this._domainList == null) {
		this._domainList = ZaDomain.getAll();
		EmailAddr_XFormItem.domainChoices.setChoices(this._domainList.getArray());
		EmailAddr_XFormItem.domainChoices.dirtyChoices();
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
		this._serverList = ZaServer.getAll();
	}
	var cnt = this._serverList.getArray().length;
	var myServer = new ZaServer(this);
	for(var i = 0; i < cnt; i++) {
		if(this._serverList.getArray()[i].attrs[ZaServer.A_ServiceHostname] == serverName)
			return this._serverList.getArray()[i];
	}
	if(i == cnt) {
		myServer = new ZaServer();
		myServer.load("name", serverName);
	}
	return myServer;	
}

ZaApp.prototype.getServerList =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll();
	}
	return this._serverList;	
}

ZaApp.prototype.getServerListChoices =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll();
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

ZaApp.prototype.getServerListChoices2 =
function(refresh) {
	if (refresh || this._serverList == null) {
		this._serverList = ZaServer.getAll();
	}
	if(refresh || this._serverChoices2 == null) {
		if(this._serverChoices2 == null) {
			this._serverChoices2 = new XFormChoices(this._serverList.getArray(), XFormChoices.OBJECT_LIST, ZaServer.A_ServiceHostname, ZaServer.A_ServiceHostname);
		} else {	
			this._serverChoices2.setChoices(this._serverList.getArray());
			this._serverChoices2.dirtyChoices();
		}
	}
	return this._serverChoices2;	
}

ZaApp.prototype.getServerMap =
function(refresh) {
	if(refresh || this._serverList == null) {
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

ZaApp.prototype.getStatusList =
function(refresh) {
	if (refresh || this._statusList == null) {
		this._statusList = ZaStatus.loadStatusTable();
	}
	return this._statusList;	
}

ZaApp.prototype.getAccountList =
function(refresh) {
	if (refresh || this._accountList == null) {
		this._accountList = ZaAccount.getAll(this).list;
	}
	return this._accountList;	
}

ZaApp.prototype.getGlobalConfig =
function(refresh) {
	if (refresh || this._globalConfig == null) {
		this._globalConfig = new ZaGlobalConfig(this);
	}
	return this._globalConfig;	
}


/**
* @param ev
* This listener is invoked by any controller that can create an ZaDomain object
**/
ZaApp.prototype.handleDomainCreation = 
function (ev) {
	if(ev) {
		//add the new ZaDomain to the controlled list
		if(ev.getDetails()) {
			if(!this._domainList) {
				this._domainList=ZaDomain.getAll();
			}
			this._domainList.add(ev.getDetails());
			EmailAddr_XFormItem.domainChoices.setChoices(this._domainList.getArray());
			EmailAddr_XFormItem.domainChoices.dirtyChoices();	
			if(this._domainListChoices == null) {
				this._domainListChoices = new XFormChoices(this._domainList.getArray(), XFormChoices.OBJECT_LIST, "name", "name");	
			} else {
				this._domainListChoices.setChoices(this._domainList.getArray());
				this._domainListChoices.dirtyChoices();			
			}					
		}
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
					if(this._cosList.getArray()[i].id == ev.getDetails()["obj"].id) {
						this._cosList.getArray()[i] = ev.getDetails()["obj"];
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
ZaApp.prototype.handleAccountCreation = 
function (ev) {
	if(ev) {
		//add the new ZaAccount to the controlled list
		if(ev.getDetails()) {
			if(!this._accountList) {
				this._accountList=ZaAccount.getAll().list;
			} else {
				this._accountList.add(ev.getDetails());
			}
		}
	}
}

/**
* @param ev
* This listener is invoked by ZaAccountViewController or any other controller that can remove an ZaAccount object
**/
ZaApp.prototype.handleAccountRemoval = 
function (ev) {
	if(ev) {
		if(!this._accountList) {
			this._accountList=ZaAccount.getAll().list;
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
			this._serverList=ZaServer.getAll();
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
			this._serverList=ZaServer.getAll();
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
		if(!this._domainList) {
			this._domainList=ZaDomain.getAll();
		} else {
			//remove the ZaDomain from the controlled list
			var detls = ev.getDetails();
			if(detls && (detls instanceof Array)) {
				for (var key in detls) {
					this._domainList.remove(detls[key]);
				}
			} else if(detls && (detls instanceof ZaDomain)) {
				this._domainList.remove(ev.getDetails());
			}
		}
		EmailAddr_XFormItem.domainChoices.setChoices(this._domainList.getArray());
		EmailAddr_XFormItem.domainChoices.dirtyChoices();		
		if(this._domainListChoices == null) {
			this._domainListChoices = new XFormChoices(this._domainList.getArray(), XFormChoices.OBJECT_LIST, "name", "name");	
		} else {
			this._domainListChoices.setChoices(this._domainList.getArray());
			this._domainListChoices.dirtyChoices();			
		}			
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

// Convenience functions that call through to app view manager. See ZaAppViewMgr for details.

ZaApp.prototype.setAppView =
function(view) {
	this._appViewMgr.setAppView(this._name, view);
}

ZaApp.prototype.createView =
function(viewName, elements, popCallback, style, isVolatile, isAppView) {
	return this._appViewMgr.createView(viewName, this._name, elements, popCallback, style, isVolatile, isAppView);
}

ZaApp.prototype.pushView =
function(name, force) {
	return this._appViewMgr.pushView(name, force);
}
/*
ZaApp.prototype.popView =
function(force) {
	return this._appViewMgr.popView(force);
}
*/
ZaApp.prototype.setView =
function(name, force) {
	return this._appViewMgr.setView(name, force);
}

// Abstract methods

/**
* Run when the activation state of an app changes.
*/
ZaApp.prototype.activate =
function(active) {
}

/**
* Clears an app's state.
*/
ZaApp.prototype.reset =
function(active) {
}
