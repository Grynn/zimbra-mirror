
ZaEffectiveRightsViewController = function (appCtxt, container) {
ZaXFormViewController.call(this, appCtxt, container, "ZaEffectiveRightsViewController");
	this._UICreated = false;
	this._helpURL = ZaEffectiveRightsViewController.helpURL;

	this.tabConstructor = ZaEffectiveRightsXFormView;
}

ZaEffectiveRightsViewController.prototype = new ZaXFormViewController();
ZaEffectiveRightsViewController.prototype.constructor = ZaEffectiveRightsViewController;
ZaEffectiveRightsViewController.helpURL = location.pathname + ZaUtil.HELP_URL + "TODO_View_All_Effective_Rigthts.html?locid="+AjxEnv.DEFAULT_LOCALE;

ZaController.setViewMethods["ZaEffectiveRightsViewController"] = new Array();
ZaController.initToolbarMethods["ZaEffectiveRightsViewController"] = new Array();

/**
*	@method show
*	@param entry - isntance of ZaAccount class
*	@param skipRefresh - forces to skip entry.refresh() call.
*		   When getting account from an alias the account is retreived from the server using ZaAccount.load()
* 		   so there is no need to refresh it.
*/

ZaEffectiveRightsViewController.prototype.show =
function(entry, openInNewTab, skipRefresh) {
	this._setView(entry, openInNewTab, skipRefresh);
}

ZaEffectiveRightsViewController.initToolbarMethod =
function () {

    this._toolbarOrder.push(ZaOperation.REFRESH);
	this._toolbarOrder.push(ZaOperation.SEP);
    this._toolbarOrder.push(ZaOperation.CLOSE);

    this._toolbarOperations[ZaOperation.REFRESH] =new ZaOperation(ZaOperation.REFRESH,
            ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh",
            new AjxListener(this, this.refreshListener));
	this._toolbarOperations[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE,
            ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis",
            new AjxListener(this, this.closeButtonListener));
	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	
}
ZaController.initToolbarMethods["ZaEffectiveRightsViewController"].push(ZaEffectiveRightsViewController.initToolbarMethod);

ZaEffectiveRightsViewController.prototype.refreshListener = function () {
//	this.show();
}

/**
*	@method setViewMethod
*	@param entry - isntance of ZaAccount class
*/
ZaEffectiveRightsViewController.setViewMethod =
function(entry) {
	try {

		if(!this._UICreated) {
			this._initToolbar();
			//make sure these are last
			this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
			this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
			this._toolbarOrder.push(ZaOperation.NONE);
			this._toolbarOrder.push(ZaOperation.HELP);

			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations, this._toolbarOrder);

	  		this._contentView = this._view = new this.tabConstructor(this._container, entry);
			var elements = new Object();
			elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;

			var tabParams = {
				openInNewTab: true,
				tabId: this.getContentViewId()
			}

	    	ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams);
	    	this._UICreated = true;
	    	//associate the controller with the view by viewId
	    	ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
  		}

		ZaApp.getInstance().pushView(this.getContentViewId()) ;

       	entry[ZaModel.currentTab] = "1";
        entry[ZaEffectiveRights.A2_account_currentTab] = "10" ;
		this._currentObject = entry;
		this._view.setObject(entry);
	} catch (ex) {
		this._handleException(ex, "ZaEffectiveRightsViewController.prototype._setView", null, false);
	}
}
ZaController.setViewMethods["ZaEffectiveRightsViewController"].push(ZaEffectiveRightsViewController.setViewMethod);
