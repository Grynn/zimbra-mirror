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
 	this.currentPageNum = 1;
 	ZaDashBoardController.hijackMessages();
}

ZaDashBoardController.prototype = new ZaXFormViewController();
ZaDashBoardController.prototype.constructor = ZaDashBoardController;
ZaController.initToolbarMethods["ZaDashBoardController"] = new Array();
ZaController.changeActionsStateMethods["ZaDashBoardController"] = new Array();
ZaDashBoardController.helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm#appliance/zap_working_in_the_administration_console.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaOperation.MANAGE_DOMAINS = ++ZA_OP_INDEX;
ZaOperation.MANAGE_PROFILES = ++ZA_OP_INDEX;
ZaOperation.MANAGE_SETITNGS = ++ZA_OP_INDEX;
ZaOperation.PAGE_FORWARD2 = ++ZA_OP_INDEX;
ZaOperation.PAGE_BACK2 = ++ZA_OP_INDEX;

ZaSettings.SERVICE_BAR_ID = ZaSettings.SKIN_IDX++;
ZaSettings.SERVICE_BAR_DOM_ID = "skin_container_service_toolbar";
ZaAppViewMgr.C_SERVICE_BAR = "SERVICE_BAR";
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SERVICE_BAR] = ZaSettings.SERVICE_BAR_ID;
ZaSettings.INIT[ZaSettings.SERVICE_BAR_ID] = [null, ZaSettings.T_CONFIG, ZaSettings.D_STRING, ZaSettings.SERVICE_BAR_DOM_ID];

ZaDashBoardController.hijackMessages = function () {
	ZaMsg.COS_view_title = com_zimbra_dashboard.COS_view_title;
	ZaMsg.COSTBB_New_tt = com_zimbra_dashboard.COSTBB_New_tt;
	ZaMsg.COSTBB_Edit_tt = com_zimbra_dashboard.COSTBB_Edit_tt;
	ZaMsg.COSTBB_Delete_tt = com_zimbra_dashboard.COSTBB_Delete_tt;
	ZaMsg.COSTBB_Duplicate_tt = com_zimbra_dashboard.COSTBB_Duplicate_tt;
	ZaMsg.COSTBB_Save_tt = com_zimbra_dashboard.COSTBB_Save_tt;
	ZaMsg.Search_view_title = com_zimbra_dashboard.Search_view_title;
	ZaMsg.NAD_ResetToCOS = com_zimbra_dashboard.NAD_ResetToCOS;
	ZaMsg.Domain_DefaultCOS = com_zimbra_dashboard.Domain_DefaultCOS;
	ZaMsg.NAD_ClassOfService =  com_zimbra_dashboard.NAD_ClassOfService;
	ZaMsg.GlobalConfig_view_title = com_zimbra_dashboard.GlobalConfig_view_title;
	ZaMsg.Domain_DefaultCOS = com_zimbra_dashboard.Domain_DefaultCOS;
	ZaMsg.FAILED_RENAME_COS_1 = com_zimbra_dashboard.FAILED_RENAME_COS_1;
	ZaMsg.FAILED_RENAME_COS = com_zimbra_dashboard.FAILED_RENAME_COS;
	ZaMsg.FAILED_CREATE_COS_1 = com_zimbra_dashboard.FAILED_CREATE_COS_1;
	ZaMsg.FAILED_CREATE_COS = com_zimbra_dashboard.FAILED_CREATE_COS;
	ZaMsg.FAILED_SAVE_COS = com_zimbra_dashboard.FAILED_SAVE_COS;
	ZaMsg.ERROR_NO_SUCH_COS = com_zimbra_dashboard.ERROR_NO_SUCH_COS;
	ZaNewAccountXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm#appliance/zap_provisioning_a_new_account.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	/**
	 * hack to deprecate notebook/wiki UI
	 */
	ZaOperation.INIT_NOTEBOOK = null;
	ZaDomainXFormView.WIKI_TAB_ATTRS = null;
	
	/**
	 * hack to replace new domain wizard
	 */
	ZaDomainController.prototype.newDomain = function () {
		try {
			var newName = "";
			if(!this._currentDomainName) {
				this._currentDomainName = this._currentObject.attrs[ZaDomain.A_domainName];
			}	
			
			if(this._currentDomainName)
				newName = "." + this._currentDomainName;

			var domain = new ZaDomain();			
			domain.loadNewObjectDefaults();
			domain.attrs[ZaDomain.A_GALSyncUseGALSearch]="TRUE";
			domain[ZaDomain.A2_new_internal_gal_polling_interval] = "2d";
			domain[ZaDomain.A2_new_external_gal_polling_interval] = "2d";
			domain.attrs[ZaDomain.A_zimbraGalMaxResults] = 100;
			domain.attrs[ZaDomain.A_domainName] = newName;
			var dashBoardController = ZaApp.getInstance().getDashBoardController(ZaSettings.DASHBOARD_VIEW);
			dashBoardController._newDomainWizard = ZaApp.getInstance().dialogs["newDomainWizard"] = new ZaApplianceDomainXWizard(dashBoardController._container, domain);	
			dashBoardController._newDomainWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, dashBoardController.finishNewDomainButtonListener, dashBoardController, null);			
			dashBoardController._newDomainWizard.setObject(domain);
			dashBoardController._newDomainWizard.popup();
		} catch (ex) {
			this._handleException(ex, "ZaDashBoardController.prototype.newDomainSelected", null, false);
		}
	}
}

ZaDashBoardController.prototype.listActionListener = function (ev) {
	this.changeActionsState();
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

ZaDashBoardController.initToolbarMethod =
function () {
	var newMenuOpList = new Array();
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Account, com_zimbra_dashboard.NewButton_Account_tt, "Account", "AccountDis", new AjxListener(this,this.newAccSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_DL, com_zimbra_dashboard.NewButton_DL_tt, "DistributionList", "DistributionListDis", new AjxListener(this,this.newDLSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Alias, com_zimbra_dashboard.NewButton_Alias_tt, "AccountAlias", "AccountAlias", new AjxListener(this, this.newAliasSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Resource, com_zimbra_dashboard.NewButton_Resource_tt, "Resource", "ResourceDis", new AjxListener(this, this.newResourceSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Domain, com_zimbra_dashboard.NewButton_Domain_tt, "Domain", "DomainDis", new AjxListener(this, this.newDomainSelected)));
    newMenuOpList.push(new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton_Profile, com_zimbra_dashboard.NewButton_Profile_tt, "NewCOS", "NewCOSDis", new AjxListener(this, this.newProfileSelected)));
 
	this._popupOperations = {};
	
	
    this._toolbarOperations[ZaOperation.NEW_MENU] = new ZaOperation(ZaOperation.NEW_MENU, com_zimbra_dashboard.NewButton, com_zimbra_dashboard.NewButton_tt, 
			"Account", "AccountDis", null, ZaOperation.TYPE_MENU, newMenuOpList);
	this._toolbarOrder.push(ZaOperation.NEW_MENU);	
    this._toolbarOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, this.editButtonListener));
    this._toolbarOrder.push(ZaOperation.EDIT);
    this._popupOperations[ZaOperation.EDIT] = new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.ACTBB_Edit_tt, "Properties", "PropertiesDis", new AjxListener(this, this.editButtonListener));
    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._popupOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ACTBB_Delete_tt, "Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener));
    this._toolbarOperations[ZaOperation.CHNG_PWD]=new ZaOperation(ZaOperation.CHNG_PWD,ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, this.chngPwdListener));
    this._toolbarOrder.push(ZaOperation.CHNG_PWD);
    this._popupOperations[ZaOperation.CHNG_PWD]=new ZaOperation(ZaOperation.CHNG_PWD,ZaMsg.ACTBB_ChngPwd, ZaMsg.ACTBB_ChngPwd_tt, "Padlock", "PadlockDis", new AjxListener(this, this.chngPwdListener));
    this._toolbarOperations[ZaOperation.VIEW_MAIL]=new ZaOperation(ZaOperation.VIEW_MAIL,ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, this.viewMailListener));
    this._toolbarOrder.push(ZaOperation.VIEW_MAIL);
    this._popupOperations[ZaOperation.VIEW_MAIL]=new ZaOperation(ZaOperation.VIEW_MAIL,ZaMsg.ACTBB_ViewMail, ZaMsg.ACTBB_ViewMail_tt, "ReadMailbox", "ReadMailbox", new AjxListener(this, this.viewMailListener));
    this._toolbarOrder.push(ZaOperation.SEP);
//	this._toolbarOperations[ZaOperation.MANAGE_SETITNGS] = new ZaOperation(ZaOperation.MANAGE_PROFILES, com_zimbra_dashboard.ServerSettings, com_zimbra_dashboard.ServerSettings_tt, "GlobalSettings", "GlobalSettings", new AjxListener(this, this.openSettingsView));
//	this._toolbarOrder.push(ZaOperation.MANAGE_SETITNGS);
    this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);	
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOperations[ZaOperation.PAGE_BACK2] = new ZaOperation(ZaOperation.PAGE_BACK2, ZaMsg.Previous, ZaMsg.PrevPage_tt, "LeftArrow", "LeftArrowDis",  new AjxListener(this, this.prevPageListener));
	this._toolbarOrder.push(ZaOperation.PAGE_BACK2);	
	//add the acount number counts
	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
    this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOperations[ZaOperation.LABEL] = new ZaOperation(ZaOperation.LABEL, AjxMessageFormat.format (ZaMsg.searchResultCount, [0,0]),
			 null, null, null, null,null,null,"ZaSearchResultCountLabel",ZaOperation.SEARCH_RESULT_COUNT);
	this._toolbarOrder.push(ZaOperation.LABEL);
	this._toolbarOrder.push(ZaOperation.SEP);    
	//ZaSearch.searchResultCountsView(this._toolbarOperations, this._toolbarOrder);	
	this._toolbarOperations[ZaOperation.PAGE_FORWARD2] = new ZaOperation(ZaOperation.PAGE_FORWARD2, ZaMsg.Next, ZaMsg.NextPage_tt, "RightArrow", "RightArrowDis", new AjxListener(this, this.nextPageListener));
	this._toolbarOrder.push(ZaOperation.PAGE_FORWARD2);
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));				
	this._toolbarOrder.push(ZaOperation.HELP);
}
ZaController.initToolbarMethods["ZaDashBoardController"].push(ZaDashBoardController.initToolbarMethod);

ZaDashBoardController.prototype.show = 
function(openInNewTab) {
	ZaZimbraAdmin.isWarnOnExit = false;
    ZaZimbraAdmin.setOnbeforeunload(null);
        
    if (!this._contentView) {
    	this._initToolbar();
    	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);

    	var elements = new Object();
		this._contentView = new ZaDashBoardView(this._container);
		
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
    	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab(),
			closable:false
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
			
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
		elements = {};
		ZaApp.getInstance().getAppViewMgr().addComponents(elements,true);
	}
    var entry = {attrs:{}};    
	ZaApp.getInstance().pushView(this.getContentViewId());
	entry[ZaDashBoard.searchResults] = [];
	this._contentView.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
	this.changeActionsState();
    var serverArray = [];
    var serverList = ZaApp.getInstance().getServerList();
    if(serverList) {
    	serverArray = serverList.getArray();
    	if(serverArray && serverArray[0]) {
    		serverArray[0].load();
    		ZaDashBoard.server = serverArray[0];
    	}
    }	
    this.openSettingsView();
    this.openAdvancedToolsView();
};

ZaDashBoardController.prototype.editButtonListener = function(ev) {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	if(listItems && listItems[0]) {
		var listWidget = listItems[0].getWidget();
		if(listWidget) {
			var item = listWidget.getSelection()[0];
			this.editItem(item);
		}
	}
};

ZaDashBoardController.prototype.editItem = function (item) {
	var type = item.type;
	//check if the item already open in a tab
	var itemId = item.id ;
	if((item.type == ZaItem.ALIAS) && item.attrs && item.attrs[ZaAlias.A_AliasTargetId]) {
		itemId = item.attrs[ZaAlias.A_AliasTargetId];
	}
	var type = item.type;
	var viewContstructor = ZaAccountXFormView;
	if (type == ZaItem.ACCOUNT) {
		viewContstructor = ZaAccountXFormView;	
	} else if (type == ZaItem.DL) {
		viewContstructor = ZaDLXFormView;	
	} else if (type == ZaItem.RESOURCE ){
		viewContstructor = ZaResourceXFormView;
	} else if (type == ZaItem.ALIAS) {
		if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {	
			viewController = ZaAccountXFormView;
		} else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
		    viewController = ZaDLXFormView;
		}
	}
	if (!this.selectExistingTabByItemId(itemId,viewContstructor)){
		if (type == ZaItem.ACCOUNT) {
			var c = ZaApp.getInstance().getAccountViewController()
			c.show(item,true);
			c.addChangeListener(new AjxListener(this, this.handleAccChange));
		    c.addRemovalListener(new AjxListener(this, this.handleAccChange));
        } else if (type == ZaItem.DL) {
			var c = ZaApp.getInstance().getDistributionListController()
			c.show(item,true);
			c.addChangeListener(new AjxListener(this, this.handleDLChange));
            c.addRemovalListener(new AjxListener(this, this.handleDLChange));
		} else if(type == ZaItem.ALIAS) {
			var targetObj = item.getAliasTargetObj();
			
			if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_ACCOUNT) {			
				var c = ZaApp.getInstance().getAccountViewController();
				c.show(targetObj, true);
				c.addChangeListener(new AjxListener(this, this.handleAccChange));
                c.addRemovalListener(new AjxListener(this, this.handleAccChange));
			} else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.TARGET_TYPE_DL){
				var c = ZaApp.getInstance().getDistributionListController();
				c.show(targetObj, true);
				c.addChangeListener(new AjxListener(this, this.handleDLChange));
                c.addRemovalListener(new AjxListener(this, this.handleDLChange));
			}  else if (item.attrs[ZaAlias.A_targetType] == ZaAlias.RESOURCE){
				var c = ZaApp.getInstance().getResourceController();
				c.show(targetObj, true);
				c.addChangeListener(new AjxListener(this, this.handleResourceChange));
                c.addRemovalListener(new AjxListener(this, this.handleResourceChange));
			}
		} else if (type == ZaItem.RESOURCE){
			var c = ZaApp.getInstance().getResourceController();
			c.show(item,true);
			c.addChangeListener(new AjxListener(this, this.handleResourceChange));
            c.addRemovalListener(new AjxListener(this, this.handleResourceChange));
		} else if (type==ZaItem.DOMAIN) {
			var c = ZaApp.getInstance().getDomainController();
			c.show(item,true);
			c.addChangeListener(new AjxListener(this, this.handleDomainChange));
            c.addRemovalListener(new AjxListener(this, this.handleDomainChange));
		} else if (type==ZaItem.COS) {
			var c = ZaApp.getInstance().getCosController();
			c.show(item,true);
			c.addChangeListener(new AjxListener(this, this.handleProfileChange));
            c.addRemovalListener(new AjxListener(this, this.handleProfileChange));
		}
	}
};

ZaDashBoardController.prototype.openSettingsView = function () {
	if (! this.selectExistingTabByItemId(ZaItem.GLOBAL_CONFIG,ZaApplianceSettingsView)){
		ZaApp.getInstance().getApplianceSettingsController().show(new ZaApplianceSettings());
	}
};

ZaDashBoardController.prototype.openAdvancedToolsView = function () {
	if (! this.selectExistingTabByItemId(ZaItem.ADVANCED_TOOLS,ZaApplianceAdvancedToolsView)){
		ZaApp.getInstance().getApplianceAdvancedToolsController().show(new ZaApplianceAdvancedTools());
	}
};

ZaDashBoardController.prototype.deleteDialogOKBtnhandler = function() {
	ZaApp.getInstance().dialogs["removeProgressDlg"].popdown();
	this._contentView.searchAddresses(this._contentView.types);
};

ZaDashBoardController.prototype.handleProfileChange = function() {
	if(this._contentView.types[0] == ZaSearch.COSES) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}
};

//new button was pressed
ZaDashBoardController.prototype.newProfileSelected = function() {
	var newCos = new ZaCos();
	//load default COS
	var defCos = ZaCos.getCosByName("default");
	newCos.loadNewObjectDefaults();
	newCos.rights[ZaCos.RENAME_COS_RIGHT]=true;
	newCos.rights[ZaCos.CREATE_COS_RIGHT]=true;
	//copy values from default cos to the new cos
	for(var aname in defCos.attrs) {
		if( (aname == ZaItem.A_objectClass) || (aname == ZaItem.A_zimbraId) || (aname == ZaCos.A_name) || (aname == ZaCos.A_description) || (aname == ZaCos.A_notes) || (aname == ZaItem.A_zimbraCreateTimestamp))
			continue;			
		newCos.attrs[aname] = defCos.attrs[aname];
	}
	
	var c = ZaApp.getInstance().getCosController();
	c._helpURL = location.pathname + ZaUtil.HELP_URL + "administration_console_help.htm#appliance/zap_creating_account_profiles.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	c.show(newCos);
	c.addChangeListener(new AjxListener(this, this.handleProfileChange));
    c.addRemovalListener(new AjxListener(this, this.handleProfileChange));
};

ZaDashBoardController.prototype.newDomainSelected = function () {
	try {
		var domain = new ZaDomain();			
		domain.loadNewObjectDefaults();
		domain.attrs[ZaDomain.A_GALSyncUseGALSearch]="TRUE";
		domain[ZaDomain.A2_new_internal_gal_polling_interval] = "2d";
		domain[ZaDomain.A2_new_external_gal_polling_interval] = "2d";
		domain.attrs[ZaDomain.A_zimbraGalMaxResults] = 100;

		this._newDomainWizard = ZaApp.getInstance().dialogs["newDomainWizard"] = new ZaApplianceDomainXWizard(this._container, domain);	
		this._newDomainWizard.registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishNewDomainButtonListener, this, null);			
		this._newDomainWizard.setObject(domain);
		this._newDomainWizard.popup();
	} catch (ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.newDomainSelected", null, false);
	}

};

ZaDashBoardController.prototype.handleDomainChange = function() {
	if(this._contentView.types[0] == ZaSearch.DOMAINS) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}
};

ZaDashBoardController.prototype.finishNewDomainButtonListener = function() {
	try {
		this._newDomainWizard.getButton(ZaXWizardDialog.FINISH_BUTTON).setEnabled(false);
		this._newDomainWizard.popdown();
		var obj = this._newDomainWizard.getObject();		
		var domain = ZaItem.create(obj,ZaDomain,"ZaDomain");
		if(domain != null) {			
			/*if(this._newDomainWizard.getObject()[ZaDomain.A_CreateNotebook]=="TRUE") {
				var params = new Object();
				params.obj = obj;
				var callback = new AjxCallback(this, this.initNotebookCallback, params);				
				ZaDomain.initNotebook(obj,callback, this) ;
			}*/			
			this.popupMsgDialog(AjxMessageFormat.format(com_zimbra_dashboard.DomainCreated,[domain.attrs[ZaDomain.A_domainName]]));
			if(this._contentView.types[0] == ZaSearch.DOMAINS) {
				this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
			}						
		} else {
			this._newDomainWizard.getButton(ZaXWizardDialog.FINISH_BUTTON).setEnabled(true);
			this._newDomainWizard.popup();
		}
	} catch (ex) {
		this._newDomainWizard.popup();
		this._newDomainWizard.getButton(ZaXWizardDialog.FINISH_BUTTON).setEnabled(true);
		if(ex.code == ZmCsfeException.DOMAIN_EXISTS) {
			this.popupErrorDialog(ZaMsg.ERROR_DOMAIN_EXISTS, ex);
		} else {
			this._handleException(ex, "ZaDomainListController.prototype.finishNewDomainButtonListener", null, false);
		}
	}
	return;
};

ZaDashBoardController.prototype.createDomainAndResource = function(domainName) {
	try {
		var newDomain = new ZaDomain();
		newDomain.name=domainName;
		newDomain.attrs[ZaDomain.A_domainName] = domainName;
		var domain = ZaItem.create(newDomain,ZaDomain,"ZaDomain");
		if(domain != null) {
			if(this._contentView.types[0] == ZaSearch.DOMAINS) {
				this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
			}			
			this.closeCnfrmDelDlg();
			this.finishResourceWizard();
		}
	} catch(ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.createDomainAndResource", null, false);	
	}
}

ZaDashBoardController.prototype.finishResourceWizard = 
function() {
	try {		
		if(!ZaResource.checkValues(ZaApp.getInstance().dialogs["newResourceWizard"]._containedObject)) {
			return false;
		}
		var resource = ZaItem.create(ZaApp.getInstance().dialogs["newResourceWizard"]._containedObject, ZaResource, "ZaResource");
		if(resource != null) {
			ZaApp.getInstance().dialogs["newResourceWizard"].popdown();
			ZaApp.getInstance().getCurrentController().popupMsgDialog(AjxMessageFormat.format(ZaMsg.ResourceCreated,[resource.name]));
			if(this._contentView.types[0] == ZaSearch.RESOURCES || this._contentView.types[3] == ZaSearch.RESOURCES) {
				this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
			}			
		}
	} catch (ex) {
		switch(ex.code) {		
			case ZmCsfeException.ACCT_EXISTS:
				this.popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				this.popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID, ex);
				ZaApp.getInstance().getAppCtxt().getErrorDialog().showDetail(true);
			break;
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(ZaApp.getInstance().dialogs["newResourceWizard"]._containedObject.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndResource, this, [ZaAccount.getDomain(ZaApp.getInstance().dialogs["newResourceWizard"]._containedObject.name)]);		
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, ZaApp.getInstance().getCurrentController(), null);				
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].popup();  				
			break;
			default:
				this._handleException(ex, "ZaNewResourceXWizard.prototype.finishWizard", null, false);
			break;		
		}
	}
};

ZaDashBoardController.prototype.newAccSelected = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(ZaApp.getInstance().getCurrentController()) ;
		var newAccount = new ZaAccount();
		newAccount.loadNewObjectDefaults("name", ZaSettings.myDomainName);
		
		if(!ZaApp.getInstance().dialogs["newAccountWizard"]) {
			ZaApp.getInstance().dialogs["newAccountWizard"] = new ZaNewAccountXWizard(DwtShell.getShell(window),newAccount);	
		}
		ZaApp.getInstance().dialogs["newAccountWizard"].registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishAccountWizard, this, null);
		
		ZaApp.getInstance().dialogs["newAccountWizard"].setObject(newAccount);
		ZaApp.getInstance().dialogs["newAccountWizard"].popup();
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDashBoardController.prototype.newAccSelected", null, false);
	}	
	
};


ZaDashBoardController.prototype.newResourceSelected = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (ZaApp.getInstance().getCurrentController());
		var newResource = new ZaResource();
		newResource.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		if(!ZaApp.getInstance().dialogs["newResourceWizard"])
			ZaApp.getInstance().dialogs["newResourceWizard"] = new ZaNewResourceXWizard(DwtShell.getShell(window));	

		ZaApp.getInstance().dialogs["newResourceWizard"].registerCallback(DwtWizardDialog.FINISH_BUTTON, this.finishResourceWizard, this, null);
		
		ZaApp.getInstance().dialogs["newResourceWizard"].setObject(newResource);
		ZaApp.getInstance().dialogs["newResourceWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.newResourceSelected", null, false);
	}
};

ZaDashBoardController.prototype.finishAliasWizard = function(aliasObj, form) {
	aliasObj.addAlias(form);
	if(this._contentView.types[0] == ZaSearch.ALIASES || this._contentView.types[1] == ZaSearch.ALIASES) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}	
};

ZaDashBoardController.prototype.newAliasSelected = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAlias = new ZaAlias();
		newAlias.getAttrs = {all:true};
		newAlias.setAttrs = {all:true};		
		newAlias._defaultValues = {attrs:{}};
		newAlias.rights = {};
		//newAlias.loadNewObjectDefaults("name", ZaSettings.myDomainName);
		if(!ZaApp.getInstance().dialogs["newAliasDialog"]) {
			ZaApp.getInstance().dialogs["newAliasDialog"] = new ZaNewAliasXDialog(
				this._container, "550px", "100px",ZaMsg.New_Alias_Title );	
		}
		ZaApp.getInstance().dialogs["newAliasDialog"].registerCallback(DwtDialog.OK_BUTTON, this.finishAliasWizard, 
				this, [newAlias,ZaApp.getInstance().dialogs["newAliasDialog"]._localXForm]);
		ZaApp.getInstance().dialogs["newAliasDialog"].setObject(newAlias);
		ZaApp.getInstance().dialogs["newAliasDialog"].popup();		
	} catch (ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.newAliasSelected", null, false);	
	}
};

ZaDashBoardController.prototype.newDLSelected = function() {
	try {
		EmailAddr_XFormItem.resetDomainLists.call (ZaApp.getInstance().getCurrentController());
		var newDL = new ZaDistributionList();
		newDL.rights = {};
		newDL._defaultValues = {attrs:{}};
		newDL.loadNewObjectDefaults("name", ZaSettings.myDomainName);	
		newDL.rights[ZaDistributionList.RENAME_DL_RIGHT]=true;
		newDL.rights[ZaDistributionList.REMOVE_DL_MEMBER_RIGHT]=true;
		newDL.rights[ZaDistributionList.ADD_DL_MEMBER_RIGHT]=true;
		var dlc = ZaApp.getInstance().getDistributionListController();
		dlc.show(newDL);
		dlc.addChangeListener(new AjxListener(this, this.handleDLChange));
        dlc.addRemovalListener(new AjxListener(this, this.handleDLChange));	
	} catch (ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.newDLSelected", null, false);
	}

};

ZaDashBoardController.prototype.handleDLChange = function () {
	if(this._contentView.types[0] == ZaSearch.DLS || this._contentView.types[2] == ZaSearch.DLS) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}	
};

ZaDashBoardController.prototype.handleAccChange = function () {
	if(this._contentView.types[0] == ZaSearch.ACCOUNTS) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}	
};

ZaDashBoardController.prototype.handleResourceChange = function () {
	if(this._contentView.types[0] == ZaSearch.RESOURCES || this._contentView.types[3] == ZaSearch.RESOURCES) {
		this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
	}	
};

ZaDashBoardController.prototype.createDomainAndAccount = function(domainName) {
	try {
		var newDomain = new ZaDomain();
		newDomain.name=domainName;
		newDomain.attrs[ZaDomain.A_domainName] = domainName;
		var domain = ZaItem.create(newDomain,ZaDomain,"ZaDomain");
		if(domain != null) {
			if(this._contentView.types[0] == ZaSearch.DOMAINS) {
				this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
			}
			this.closeCnfrmDelDlg();			
			this.finishAccountWizard();
		}
	} catch(ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.createDomainAndAccount", null, false);	
	}
};

ZaDashBoardController.prototype.finishAccountWizard = function() {
	try {
		var obj = ZaApp.getInstance().dialogs["newAccountWizard"].getObject();
		if(!ZaAccount.checkValues(obj)) {
			return false;
		}
		var account = ZaItem.create(obj,ZaAccount,"ZaAccount");
		if(account != null) {
			//if creation took place - fire an change event
			ZaApp.getInstance().dialogs["newAccountWizard"].popdown();
			this.popupMsgDialog(AjxMessageFormat.format(ZaMsg.AccountCreated,[account.name]));	
			if(this._contentView.types[0] == ZaSearch.ACCOUNTS) {
				this._contentView.searchAddresses(this._contentView.types,this._contentView.offset);
			}
		}
	} catch (ex) {
		switch(ex.code) {		
			case ZmCsfeException.ACCT_EXISTS:
				this.popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				this.popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID, ex);
				ZaApp.getInstance().getAppCtxt().getErrorDialog().showDetail(true);
			break;
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(obj.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndAccount, this, [ZaAccount.getDomain(obj.name)]);		
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, this, null);				
				ZaApp.getInstance().dialogs["confirmDeleteMessageDialog"].popup();  				
			break;
			default:
				this._handleException(ex, "ZaNewAccountXWizard.prototype.finishWizard", null, false);
			break;		
		}
	}
};

ZaDashBoardController.prototype.deleteButtonListener = function(ev) {
	this._removeList = new Array();
	this._itemsInTabList = [] ;
	this._haveAliases = false;
	this._haveAccounts = false;
	this._haveDls = false;
	this._haveDomains = false;	
	this._haveCOSes = false;
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}	
	if(!ZaApp.getInstance().dialogs["removeProgressDlg"]) {
		ZaApp.getInstance().dialogs["removeProgressDlg"] = new DeleteAcctsPgrsDlg(this._container, "500px","300px");
	}
	ZaApp.getInstance().dialogs["removeProgressDlg"].registerCallback(DwtDialog.OK_BUTTON, this.deleteDialogOKBtnhandler, this, null);
	if(listWidget.getSelectionCount()>0) {
		var arrItems = listWidget.getSelection();
		var cnt = arrItems.length;
		for(var key =0; key < cnt; key++) {
			var item = arrItems[key];
			if (item) {
				//detect whether the deleting item is open in a tab
				if (ZaApp.getInstance().getTabGroup().getTabByItemId (item.id)) {
					this._itemsInTabList.push (item) ;
				}else{
					this._removeList.push(item);			
					if(!this._haveAliases && item.type == ZaItem.ALIAS) {
						this._haveAliases = true;
					} else if(!this._haveAccounts && item.type == ZaItem.ACCOUNT) {
						this._haveAccounts = true;
					} else if(!this._haveDls && item.type == ZaItem.DL) {
						this._haveDls = true;
					}  else if(!this._haveDomains && item.type == ZaItem.DOMAIN) {
						this._haveDomains = true;
					} else if(!this._haveCOSes && item.type == ZaItem.COS) {
						this._haveCOSes = true;
					}
				}
			}
		}
	}
	
	if (this._itemsInTabList.length > 0) {
		if(!ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"]) {
			ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"] = 
				new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell(), null, [DwtDialog.CANCEL_BUTTON], 
						[ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON_DESC , ZaMsgDialog.NO_DELETE_BUTTON_DESC]);			
		}
		
		
		var msg = ZaMsg.dl_warning_delete_accounts_in_tab ; 
		msg += ZaAccountListController.getDlMsgFromList (this._itemsInTabList) ;
		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].setMessage(msg, DwtMessageDialog.WARNING_STYLE);	
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.CLOSE_TAB_DELETE_BUTTON, ZaAccountListController.prototype._closeTabsBeforeRemove, this);
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].registerCallback(
				ZaMsgDialog.NO_DELETE_BUTTON, ZaAccountListController.prototype._deleteAccountsInRemoveList, this);		
		ZaApp.getInstance().dialogs["ConfirmDeleteItemsInTabDialog"].popup();
		
	} else{
		ZaAccountListController.prototype._deleteAccountsInRemoveList.call (this);
	}
	
};

ZaDashBoardController.prototype.chngPwdListener = function(ev) {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}
	if(listWidget.getSelectionCount()==1) {
		this._chngPwdDlg = new ZaAccChangePwdXDlg(ZaApp.getInstance().getAppCtxt().getShell(), "400px","90px");
		var item = listWidget.getSelection()[0];
		item.loadEffectiveRights("id", item.id, false);
		this._chngPwdDlg.registerCallback(DwtDialog.OK_BUTTON, ZaAccountListController._changePwdOKCallback, this, item);				
		this._chngPwdDlg.setTitle(ZaMsg.CHNP_Title + " (" + item.name + ")");
		var obj = new Object();
		obj[ZaAccount.A2_confirmPassword]="";
		obj.attrs = {};
		obj.attrs[ZaAccount.A_password]="";
		obj.attrs[ZaAccount.A_zimbraPasswordMustChange]=false;
		obj.getAttrs = item.getAttrs;
		obj.setAttrs = item.setAttrs;
		obj.rights = item.rights;		
		this._chngPwdDlg.setObject(obj)
		this._chngPwdDlg.popup();
	}
};

ZaDashBoardController.prototype.viewMailListener = function(ev) {
	try {
		var form = this._contentView._localXForm;
		var listItems = form.getItemsById("dashBoardSearchResults");
		var listWidget = null;
		if(listItems && listItems[0]) {
			listWidget = listItems[0].getWidget();
		} else {
			return;
		}
		var accounts = listWidget.getSelection();
		if(accounts && accounts.length) {
			var account = accounts[0];
			if(account) {
				ZaAccountListController._viewMailListenerLauncher.call(this, account);
			}
		}	
	} catch (ex) {
		this._handleException(ex, "ZaDashBoardController.prototype.viewMailListener", null, false);			
	}
};


ZaDashBoardController.prototype._expireSessionListener = function(ev) {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}
	if(listWidget.getSelectionCount()==1) {
		var item = listWidget.getSelection()[0];
		item.loadEffectiveRights("id", item.id, false);
		if(ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item)) {
		ZaApp.getInstance().dialogs["confirmMessageDialog"].setMessage(ZaMsg.WARN_EXPIRE_SESSIONS, DwtMessageDialog.WARNING_STYLE);
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, ZaAccountListController.prototype.expireSessions, this, [item]);		
			ZaApp.getInstance().dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.closeCnfrmDlg, this, null);				
			ZaApp.getInstance().dialogs["confirmMessageDialog"].popup();
		} else {
			this.popupMsgDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_PERMISSION_FOR_OPERATION_ON, [item.name ? item.name : item.attrs[ZaAccount.A_accountName]]), true);
		}
	}
};

ZaDashBoardController.prototype.setPageNum = 
function (pgnum) {
	this.currentPageNum = Number(pgnum);
};

ZaDashBoardController.prototype.getPageNum = 
function () {
	return this.currentPageNum;
};

ZaDashBoardController.prototype.nextPageListener = function (ev) {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}
	
	if(this.currentPageNum < this.numPages) {
		this.currentPageNum++;
		this._contentView.searchAddresses(this._contentView.types,ZaSettings.RESULTSPERPAGE*(this.currentPageNum-1))
	} 
};

ZaDashBoardController.prototype.prevPageListener = 	function (ev) {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}
		
	if(this.currentPageNum > 1) {
		this.currentPageNum--;
		this._contentView.searchAddresses(this._contentView.types,ZaSettings.RESULTSPERPAGE*(this.currentPageNum-1))
	} 
};

	
ZaDashBoardController.changeActionsStateMethod = function () {
	var form = this._contentView._localXForm;
	var listItems = form.getItemsById("dashBoardSearchResults");
	var listWidget = null;
	if(listItems && listItems[0]) {
		listWidget = listItems[0].getWidget();
	} else {
		return;
	}
	
	var cnt = listWidget.getSelectionCount();
	if(cnt == 1) {
		var item = listWidget.getSelection()[0];
		if(item) {
			if(item.type != ZaItem.ALIAS) {
				if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
					this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
				}
				
				if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
					this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
				}				
			}
            if (item.type == ZaItem.ALIAS || item.type == ZaItem.DL) {
                if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
                    this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
                }
                
                if(this._popupOperations[ZaOperation.CHNG_PWD]) {
                    this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
                }                
            }
            if(item.type == ZaItem.COS && item.name=="default") {
				if(this._toolbarOperations[ZaOperation.DELETE])
				 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
				
				if(this._popupOperations[ZaOperation.DELETE])
				 	this._popupOperations[ZaOperation.DELETE].enabled = false;   				
            }
            if(item.type == ZaItem.DOMAIN || item.type == ZaItem.COS) {
                if (this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
                    this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }   
                if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
                    this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
                }	
			   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				} 
			   	
                if (this._popupOperations[ZaOperation.VIEW_MAIL]) {
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }   
                if(this._popupOperations[ZaOperation.CHNG_PWD]) {
                    this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
                }	
			   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				} 			   	
            } else if (((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.DL))
                || (item.type == ZaItem.DL)) {
                if (this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
                    this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }
                if (this._popupOperations[ZaOperation.VIEW_MAIL]) {
                    this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;                                        
                }                

            } else if (item.type == ZaItem.DL) {
                if(this._toolbarOperations[ZaOperation.MOVE_ALIAS])	{
                    this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
                }					 
				if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
				 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false; 
				
                if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
                    this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
                }					 
				if(this._popupOperations[ZaOperation.VIEW_MAIL])
				 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false; 				
                	    
            } else if (item.type == ZaItem.ACCOUNT) {
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					//console.log("loading effective rights for a list item");
					item.loadEffectiveRights("id", item.id, false);
					//console.log("loaded rights for a list item");
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item)) {
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false; 
						 
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
							 this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false; 						 
					}
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item)) {
						if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;  
						
						if(this._popupOperations[ZaOperation.DELETE])
						 	this._popupOperations[ZaOperation.DELETE].enabled = false;  						
					}	
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT, item)) {
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false; 
						 
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
						 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false; 						 
					}		
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}					   	
					}
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.ACCOUNT))  {
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaAccount.VIEW_MAIL_RIGHT,item.targetObj)) {
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
						 
						 if(this._popupOperations[ZaOperation.VIEW_MAIL])
							 this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;   						 
					}	
					if(!ZaItem.hasRight(ZaAccount.DELETE_ACCOUNT_RIGHT,item.targetObj)) {
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
						 
						 if(this._popupOperations[ZaOperation.DELETE])
							 this._popupOperations[ZaOperation.DELETE].enabled = false;   						 
					}
					if(!ZaItem.hasRight(ZaAccount.SET_PASSWORD_RIGHT,item.targetObj)) {
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
						 
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
							 this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;   						 
					}			
					if(!ZaItem.hasWritePermission(ZaAccount.A_zimbraAuthTokenValidityValue,item.targetObj)) {    
					   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}
					   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
							this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
						}					   	
					}
				}
			} else if ((item.type == ZaItem.ALIAS) && (item.attrs[ZaAlias.A_targetType] == ZaItem.RESOURCE))  {
			   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
					this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}				
				if(!item.targetObj)
					item.targetObj = item.getAliasTargetObj() ;
					
				var enable = false;
				if (ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.targetObj.rights)) {
					item.targetObj.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!enable) {
						if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item.targetObj)) {
							 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
							 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false; 
							 
							 if(this._popupOperations[ZaOperation.VIEW_MAIL])
								 	this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false; 							 
						}
						if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item.targetObj)) {
							 if(this._toolbarOperations[ZaOperation.DELETE])
							 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
							 
							 if(this._popupOperations[ZaOperation.DELETE])
								 	this._popupOperations[ZaOperation.DELETE].enabled = false;   							 
						}	
						if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item.targetObj)) {
							 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
							 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
							 
							 if(this._popupOperations[ZaOperation.CHNG_PWD])
								 this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;   							 
						}		
					}
				}
			} else if(item.type == ZaItem.RESOURCE) {
			   	if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}
			   	
			   	if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
					this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
				}			   	
				var enable = false;
				if(ZaZimbraAdmin.currentAdminAccount.attrs[ZaAccount.A_zimbraIsAdminAccount] == 'TRUE') {
					enable = true;
				} else if (AjxUtil.isEmpty(item.rights)) {
					item.loadEffectiveRights("id", item.id, false);
				}
				if(!enable) {
					if(!ZaItem.hasRight(ZaResource.VIEW_RESOURCE_MAIL_RIGHT,item)) {
						 if(this._toolbarOperations[ZaOperation.VIEW_MAIL])
						 	this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;   
					}
					if(!ZaItem.hasRight(ZaResource.DELETE_CALRES_RIGHT,item)) {
						 if(this._toolbarOperations[ZaOperation.DELETE])
						 	this._toolbarOperations[ZaOperation.DELETE].enabled = false;   
						 
						 if(this._popupOperations[ZaOperation.DELETE])
							 	this._popupOperations[ZaOperation.DELETE].enabled = false;   						 
					}	
					if(!ZaItem.hasRight(ZaResource.SET_CALRES_PASSWORD_RIGHT, item)) {
						 if(this._toolbarOperations[ZaOperation.CHNG_PWD])
						 	this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;   
						 
						 if(this._popupOperations[ZaOperation.CHNG_PWD])
							 	this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;   						 
					}		
				}				
			}
        } else {
			if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}	        	
			if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
				this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.EDIT]) {	
				this._toolbarOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
				this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
				this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._toolbarOperations[ZaOperation.DELETE]) {	
				this._toolbarOperations[ZaOperation.DELETE].enabled = false;
			}

			if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
				this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
			}	        	
			if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
				this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
			}
			if(this._popupOperations[ZaOperation.EDIT]) {	
				this._popupOperations[ZaOperation.EDIT].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.CHNG_PWD]) {
				this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
			}
			if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
				this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
			}	
			if(this._popupOperations[ZaOperation.DELETE]) {	
				this._popupOperations[ZaOperation.DELETE].enabled = false;
			}
		}		
	} else if (cnt > 1){
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS]) {
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;		
		}

		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.MOVE_ALIAS]) {
		}		
	} else {
		if(this._toolbarOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._toolbarOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._toolbarOperations[ZaOperation.EDIT]) {	
			this._toolbarOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.DELETE]) {
			this._toolbarOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._toolbarOperations[ZaOperation.CHNG_PWD]) {
			this._toolbarOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._toolbarOperations[ZaOperation.VIEW_MAIL]) {
			this._toolbarOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._toolbarOperations[ZaOperation.MOVE_ALIAS])	{
			this._toolbarOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}	

		if(this._popupOperations[ZaOperation.EXPIRE_SESSION]) {	
			this._popupOperations[ZaOperation.EXPIRE_SESSION].enabled = false;
		}			
		if(this._popupOperations[ZaOperation.EDIT]) {	
			this._popupOperations[ZaOperation.EDIT].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.DELETE]) {
			this._popupOperations[ZaOperation.DELETE].enabled = false;
		}		
		if(this._popupOperations[ZaOperation.CHNG_PWD]) {
			this._popupOperations[ZaOperation.CHNG_PWD].enabled = false;
		}	
		if(this._popupOperations[ZaOperation.VIEW_MAIL]) {
			this._popupOperations[ZaOperation.VIEW_MAIL].enabled = false;
		}
		if(this._popupOperations[ZaOperation.MOVE_ALIAS])	{
			this._popupOperations[ZaOperation.MOVE_ALIAS].enabled = false;
		}		
	}
	
	var s_result_start_n = (this.currentPageNum - 1) * ZaSettings.RESULTSPERPAGE + 1;
	var s_result_end_n = this.currentPageNum  * ZaSettings.RESULTSPERPAGE;
	if(this.numPages <= this.currentPageNum) {
		s_result_end_n = this.searchTotal ;
		this._toolbarOperations[ZaOperation.PAGE_FORWARD2].enabled = false;
	}
	if(this.currentPageNum == 1) {
		this._toolbarOperations[ZaOperation.PAGE_BACK2].enabled = false;
	} 
};
ZaController.changeActionsStateMethods["ZaDashBoardController"].push(ZaDashBoardController.changeActionsStateMethod);
