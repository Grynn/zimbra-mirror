/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaDashBoardController 
* @contructor ZaDashBoardController
* @param appCtxt
* @param container
* @author Greg Solovyev
**/
ZaDashBoardController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaDashBoardController");
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;
	this._helpURL = ZaDashBoardController.helpURL;
	this.tabConstructor = ZaDashBoardView;
 	this._toolbarOperations = new Array();
}

ZaDashBoardController.prototype = new ZaXFormViewController();
ZaDashBoardController.prototype.constructor = ZaDashBoardController;
ZaController.initToolbarMethods["ZaDashBoardController"] = new Array();
ZaDashBoardController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/provisioning_accounts.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaOperation.MANAGE_DOMAINS = ++ZA_OP_INDEX;
ZaOperation.MANAGE_PROFILES = ++ZA_OP_INDEX;
ZaOperation.MANAGE_SETITNGS = ++ZA_OP_INDEX;
ZaOperation.SERVICES_LABEL = ++ZA_OP_INDEX;
ZaOperation.SERVICE_LDAP = ++ZA_OP_INDEX;
ZaOperation.SERVICE_MAILBOX = ++ZA_OP_INDEX;
ZaOperation.SERVICE_MTA = ++ZA_OP_INDEX;
ZaOperation.SERVICE_LOGGER = ++ZA_OP_INDEX;
ZaOperation.SERVICE_SPELL = ++ZA_OP_INDEX;
ZaOperation.SERVICE_CONVERTD = ++ZA_OP_INDEX;
ZaOperation.SERVICE_AS = ++ZA_OP_INDEX;
ZaOperation.SERVICE_AV = ++ZA_OP_INDEX;
ZaOperation.SERVICE_STATS = ++ZA_OP_INDEX;
ZaOperation.RESTART_SERVICES = ++ZA_OP_INDEX;
ZaOperation.STOP_SERVICES = ++ZA_OP_INDEX;

ZaSettings.SERVICE_BAR_ID = ZaSettings.SKIN_IDX++;
ZaSettings.SERVICE_BAR_DOM_ID = "skin_container_service_toolbar";
ZaAppViewMgr.C_SERVICE_BAR = "SERVICE_BAR";
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SERVICE_BAR] = ZaSettings.SERVICE_BAR_ID;
ZaSettings.INIT[ZaSettings.SERVICE_BAR_ID] = [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SERVICE_BAR_DOM_ID];

ZaDashBoardController.initToolbarMethod =
function () {
	var newMenuOpList = new Array();
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Account, com_zimbra_dashboard.NewButton_Account_tt, "Account", "AccountDis", new AjxListener(this,this.newAccSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_DL, com_zimbra_dashboard.NewButton_DL_tt, "DistributionList", "DistributionListDis", new AjxListener(this,this.newDLSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Alias, com_zimbra_dashboard.NewButton_Alias_tt, "AccountAlias", "AccountAlias", new AjxListener(this, this.newAliasSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Resource, com_zimbra_dashboard.NewButton_Resource_tt, "Resource", "ResourceDis", new AjxListener(this, this.newResourceSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Domain, com_zimbra_dashboard.NewButton_Domain_tt, "Domain", "DomainDis", new AjxListener(this, this.newDomainSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Profile, com_zimbra_dashboard.NewButton_Profile_tt, "NewCOS", "NewCOSDis", new AjxListener(this, this.newProfileSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Import, com_zimbra_dashboard.NewButton_Import_tt, "BulkProvision", "BulkProvision", new AjxListener(this, this.newImportAccountsSelected))); 
	
    this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton, com_zimbra_dashboard.NewButton_tt, 
			"Account", "AccountDis", this._newAcctListener, ZaOperation.TYPE_MENU, newMenuOpList);
	this._toolbarOrder.push(ZaOperation.NEW_MENU);	
	//this._toolbarOperations[ZaOperation.MANAGE_DOMAINS] = new ZaOperation(ZaOperation.MANAGE_DOMAINS, com_zimbra_dashboard.ManageDomains, com_zimbra_dashboard.ManageDomains_tt, "Domain", "DomainDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
	//this._toolbarOrder.push(ZaOperation.MANAGE_DOMAINS);
	//this._toolbarOperations[ZaOperation.MANAGE_PROFILES] = new ZaOperation(ZaOperation.MANAGE_PROFILES, com_zimbra_dashboard.ManageProfiles, com_zimbra_dashboard.ManageProfiles_tt, "COS", "COSDis", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
	//this._toolbarOrder.push(ZaOperation.MANAGE_PROFILES);
    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, this.editButtonListener));
    this._toolbarOrder.push(ZaOperation.EDIT);
    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
    this._toolbarOrder.push(ZaOperation.DELETE);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOperations[ZaOperation.MANAGE_SETITNGS] = new ZaOperation(ZaOperation.MANAGE_PROFILES, com_zimbra_dashboard.ServerSettings, com_zimbra_dashboard.ServerSettings_tt, "GlobalSettings", "GlobalSettings", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
	this._toolbarOrder.push(ZaOperation.MANAGE_SETITNGS);
    this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.PAGE_BACK] = new ZaOperation(ZaOperation.PAGE_BACK, ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this._prevPageListener));
	this._toolbarOrder.push(ZaOperation.PAGE_BACK);	
	//add the acount number counts
	ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);	
	this._toolbarOperations[ZaOperation.PAGE_FORWARD] = new ZaOperation(ZaOperation.PAGE_FORWARD, ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this._nextPageListener));
	this._toolbarOrder.push(ZaOperation.PAGE_FORWARD);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				
	this._toolbarOrder.push(ZaOperation.HELP);
}
ZaController.initToolbarMethods["ZaDashBoardController"].push(ZaDashBoardController.initToolbarMethod);

ZaDashBoardController.prototype.show = 
function(openInNewTab) {
    if (!this._contentView) {
    	this._initToolbar();
    	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);
    	
    	this._serviceBarOperations = {};
    	this._serviceBarOrder = [ZaOperation.SERVICES_LABEL,ZaOperation.SERVICE_LDAP,ZaOperation.SERVICE_MAILBOX,ZaOperation.SERVICE_MTA,
    	                         ZaOperation.SERVICE_SPELL,ZaOperation.SERVICE_CONVERTD,ZaOperation.SERVICE_AS,ZaOperation.SERVICE_AV,
    	                         ZaOperation.SERVICE_STATS,ZaOperation.STOP_SERVICES,ZaOperation.RESTART_SERVICES];
    	this._serviceBarOperations[ZaOperation.SERVICES_LABEL] = new ZaOperation(ZaOperation.LABEL,com_zimbra_dashboard.Services,null,null);
    	this._serviceBarOperations[ZaOperation.SERVICE_LDAP] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_LDAP,ZaStatus.SVC_LDAP,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_MAILBOX] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_MAILBOX,ZaStatus.SVC_MAILBOX,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_MTA] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_MTA,ZaStatus.SVC_MTA,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_SPELL] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_SPELL,ZaStatus.SVC_SPELL,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_CONVERTD] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_CONVERTD,ZaStatus.SVC_CONVERTD,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_AS] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_AS,ZaStatus.SVC_AS,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_AV] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_AV,ZaStatus.SVC_AV,"Check");
    	this._serviceBarOperations[ZaOperation.SERVICE_STATS] = new ZaOperation(ZaOperation.LABEL,ZaStatus.SVC_STATS,ZaStatus.SVC_STATS,"Check");
    	this._serviceBarOperations[ZaOperation.STOP_SERVICES] = new ZaOperation(ZaOperation.STOP_SERVICES, com_zimbra_dashboard.StopServices, com_zimbra_dashboard.StopServices_tt, "Cancel", "Cancel", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
    	this._serviceBarOperations[ZaOperation.RESTART_SERVICES] = new ZaOperation(ZaOperation.RESTART_SERVICES, com_zimbra_dashboard.RestartServices, com_zimbra_dashboard.RestartServices_tt, "Refresh", "Refresh", new AjxListener(this, ZaAccountListController.prototype._editButtonListener));
    	this._serviceBar = new ZaToolBar(this._container, this._serviceBarOperations,this._serviceBarOrder,null,"VAMIServicesToolBar");
    	var elements = new Object();
		this._contentView = new this.tabConstructor(this._container);
		elements[ZaAppViewMgr.C_SERVICE_BAR] = this._serviceBar;
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
    	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
			
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
    var entry = {attrs:{}};
    var gc = ZaApp.getInstance().getGlobalConfig();
    var statusObj = new ZaStatus();
    statusObj.load();
    if(statusObj.serverMap) {
    	for(var a in statusObj.serverMap) {
    		entry.serviceMap = statusObj.serverMap[a].serviceMap;
    		break;
    	}
    }

    entry.attrs = gc.attrs;
    entry.rights = gc.rights;
    entry.setAttrs = gc.setAttrs;
    entry.getAttrs = gc.getAttrs;
	ZaApp.getInstance().pushView(this.getContentViewId());
	entry[ZaDashBoard.searchResults] = [];
	this._contentView.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
};
