ZaAllGrantsViewController = function (appCtxt, container) {
    ZaXFormViewController.call(this, appCtxt, container, "ZaAllGrantsViewController");
    this._UICreated = false;
    this._helpURL = ZaAllGrantsViewController.helpURL;

    this.tabConstructor = ZaAllGrantsXFormView;
}

ZaAllGrantsViewController.prototype = new ZaXFormViewController();
ZaAllGrantsViewController.prototype.constructor = ZaAllGrantsViewController;
ZaAllGrantsViewController.helpURL = location.pathname + ZaUtil.HELP_URL + "TODO_View_All_Effective_Rigthts.html?locid=" + AjxEnv.DEFAULT_LOCALE;

ZaController.setViewMethods["ZaAllGrantsViewController"] = new Array();
ZaController.initToolbarMethods["ZaAllGrantsViewController"] = new Array();

/**
 *    @method show
 *    @param entry - isntance of ZaAccount class
 *    @param skipRefresh - forces to skip entry.refresh() call.
 *           When getting account from an alias the account is retreived from the server using ZaAccount.load()
 *            so there is no need to refresh it.
 */

ZaAllGrantsViewController.prototype.show =
function(entry, openInNewTab, skipRefresh) {
    this._setView(entry, openInNewTab, skipRefresh);
}

ZaAllGrantsViewController.initToolbarMethod =
function () {

    this._toolbarOrder.push(ZaOperation.NEW);
    this._toolbarOrder.push(ZaOperation.DELETE);
    this._toolbarOrder.push(ZaOperation.SEP);
    this._toolbarOrder.push(ZaOperation.CLOSE);

    this._toolbarOperations[ZaOperation.NEW] = new ZaOperation(ZaOperation.NEW,
            com_zimbra_delegatedadmin.Bt_grant, com_zimbra_delegatedadmin.Grant_New_tt,
            "GlobalPermission", "GlobalPermissionDis",
            new AjxListener(this, this.addGrantsListener));

    this._toolbarOperations[ZaOperation.DELETE] = new ZaOperation(ZaOperation.DELETE,
            com_zimbra_delegatedadmin.Bt_revoke, com_zimbra_delegatedadmin.Grant_Delete_tt,
            "Delete", "DeleteDis",
            new AjxListener(this, this.deleteGrantsListener));

    this._toolbarOperations[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE,
            ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis",
            new AjxListener(this, this.closeButtonListener));

    this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
}
ZaController.initToolbarMethods["ZaAllGrantsViewController"].push(ZaAllGrantsViewController.initToolbarMethod);

ZaAllGrantsViewController.prototype.addGrantsListener = function () {
    console.log("Add Grants ...");
}

ZaAllGrantsViewController.prototype.deleteGrantsListener = function () {
    console.log("Delete Grants ...");
}

/**
 *    @method setViewMethod
 *    @param entry - isntance of ZaAccount class
 */
ZaAllGrantsViewController.setViewMethod =
function(entry) {
    try {
        if (!this._UICreated) {
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
            ZaApp.getInstance()._controllers[this.getContentViewId()] = this;
        }

        ZaApp.getInstance().pushView(this.getContentViewId());

        entry[ZaModel.currentTab] = "1";
        this._currentObject = entry;
        this._view.setObject(entry);
    } catch (ex) {
        this._handleException(ex, "ZaAllGrantsViewController.prototype._setView", null, false);
    }
}
ZaController.setViewMethods["ZaAllGrantsViewController"].push(ZaAllGrantsViewController.setViewMethod);
