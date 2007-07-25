if (AjxEnv.hasFirebug) console.debug("Loaded ZaCertViewController.js");

function ZaCertViewController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app,"ZaCertViewController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();			
	//this.ServerPool = [];
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/NEED_THE_CERT_HELP.htm";					
}

ZaCertViewController.prototype = new ZaController();
ZaCertViewController.prototype.constructor = ZaCertViewController;

ZaController.initToolbarMethods["ZaCertViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaCertViewController"] = new Array();

ZaCertViewController.prototype.show = 
function(certs) {
    if (!this._UICreated) {
		this._createUI();
	} 	

	if (certs != null) {
		this._contentView.set(certs);
	}	
	
	this._app.pushView(this.getContentViewId());
}


//Cert Actions
//1. New Wizards
//2. Edit Wizards
ZaCertViewController.initToolbarMethod =
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh", new AjxListener(this, ZaCertViewController.prototype.refreshListener)));	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.TBB_New_Cert_tt, "Backup", "Backup", new AjxListener(this, ZaCertViewController.prototype._newCertListener)));				
	this._toolbarOperations.push(new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.TBB_Edit_Cert_tt, "RestoreMailbox", "RestoreMailboxDis", new AjxListener(this, ZaCertViewController.prototype._editCertListener)));		   	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));				
}

ZaController.initToolbarMethods["ZaCertViewController"].push(ZaCertViewController.initToolbarMethod);

ZaCertViewController.initPopupMenuMethod =
function () {
    this._popupOperations.push(new ZaOperation(ZaOperation.VIEW, ZaMsg.TBB_View, ZaMsg.PQTBB_View_tt, "Properties", "PropertiesDis", new AjxListener(this, ZaCertViewController.prototype._viewButtonListener)));
}
ZaController.initPopupMenuMethods["ZaCertViewController"].push(ZaCertViewController.initPopupMenuMethod);

ZaCertViewController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaCertView(this._container);
		this._initToolbar();
		if(this._toolbarOperations && this._toolbarOperations.length) {
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations); 
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		}
		/*
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._actionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		}*/
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		this._app.createView(this.getContentViewId(), elements, tabParams) ;

		//this._contentView.addSelectionListener(new AjxListener(this, this._listSelectionListener));
		//this._contentView.addActionListener(new AjxListener(this, this._listActionListener));			
	
		this._UICreated = true;
		this._app._controllers[this.getContentViewId ()] = this ;
		
	} catch (ex) {
		this._handleException(ex, "ZaCertViewController.prototype._createUI", null, false);
		return;
	}	
}


ZaCertViewController.prototype._newCertListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Launch the new certificates wizard ... ") ;
	
	

}

ZaCertViewController.prototype._editCertListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Launch the certificate modifications wizard ... ") ;
	
}

ZaCertViewController.prototype.refreshListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Refresh the certificates ... ") ;
	
}
