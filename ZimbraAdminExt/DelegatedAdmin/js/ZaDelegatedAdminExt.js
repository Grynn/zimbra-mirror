/**
 * This is used to add the extension views to the main admin console UI
 *
 *
 **/


if (AjxEnv.hasFirebug) console.debug("Loaded ZaDelegatedAdminExt.js");

function ZaDelegatedAdminExt() {
}
;

//ZaSettings.RIGHTS_ENABLED = true ;
//ZaSettings.GRANTS_ENABLED = true ;
ZaEvent.S_RIGHT = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_GRANT = ZaEvent.EVENT_SOURCE_INDEX++;
ZaItem.RIGHT = "right";
ZaItem.GRANT = "grant";

ZaZimbraAdmin._RIGHTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._RIGHTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._GRANTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaSettings.RIGHT_LIST_VIEW = "rightListView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.RIGHT_LIST_VIEW, label: com_zimbra_delegatedadmin.UI_Comp_RightListView });
ZaSettings.OVERVIEW_CONFIG_ITEMS.push(ZaSettings.RIGHT_LIST_VIEW);
ZaSettings.GLOBAL_PERMISSION_VIEW = "globalPermissionView";
ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.GLOBAL_PERMISSION_VIEW, label: com_zimbra_delegatedadmin.UI_Comp_GlobalPermView });
ZaSettings.OVERVIEW_CONFIG_ITEMS.push(ZaSettings.GLOBAL_PERMISSION_VIEW);

if (ZaOverviewPanelController.treeModifiers) {
    ZaOverviewPanelController.treeModifiers.push(ZaRight.rightsOvTreeModifier);
    ZaOverviewPanelController.treeModifiers.push(ZaGrant.grantsOvTreeModifier);
}

ZaApp.prototype.getRightViewController =
function(viewId) {
    if (viewId && this._controllers[viewId] != null) {
        return this._controllers[viewId];
    } else {
        var c = this._controllers[viewId] = new ZaRightViewController(this._appCtxt, this._container, this);
        return c;
    }
}

ZaApp.prototype.getRightsListController =
function() {
    if (this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW] == null) {
        this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW] = new ZaRightsListViewController(this._appCtxt, this._container, this);
    }
    return this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW];
}


ZaApp.prototype.getGrantViewController =
function(viewId) {
    if (viewId && this._controllers[viewId] != null) {
        return this._controllers[viewId];
    } else {
        var c = this._controllers[viewId] = new ZaGrantViewController(this._appCtxt, this._container, this);
        return c;
    }
}

ZaApp.prototype.getGlobalGrantListController =
function() {
    if (this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] == null) {
        this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] = new ZaGlobalGrantListViewController(this._appCtxt, this._container, this);
    }
    return this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW];
}


//Add the View Rights toolbar button
ZaOperation.VIEW_EFFECTIVE_RIGHTS = ++ ZA_OP_INDEX;
if (ZaController.initToolbarMethods["ZaAccountListController"]) {
    ZaAccountListController.initExtraToolbarMethod = function () {
        this._toolbarOperations [ZaOperation.VIEW_EFFECTIVE_RIGHTS] =
        new ZaOperation(ZaOperation.VIEW_EFFECTIVE_RIGHTS, com_zimbra_delegatedadmin.ACTBB_ViewRights,
                com_zimbra_delegatedadmin.ACTBB_ViewRights_tt, "RightObject", "RightObjectDis",
                new AjxListener(this, ZaDelegatedAdminExt._viewRightsListener)
                );
        if (this._defaultType == ZaItem.ACCOUNT || this._defaultType == ZaItem.DL) {
            for (var i = 0; i < this._toolbarOrder.length; i ++) {
                if (this._toolbarOrder[i] == ZaOperation.NONE) {
                    this._toolbarOrder.splice(i, 0, ZaOperation.VIEW_EFFECTIVE_RIGHTS);
                    break;
                }
            }
        }

    }

    ZaController.initToolbarMethods["ZaAccountListController"].push(ZaAccountListController.initExtraToolbarMethod);
}

ZaDelegatedAdminExt._viewRightsListener =
function (ev) {
    var item ;
    if (this instanceof ZaAccountListController) {
        var selectedItems = this._contentView.getSelection() ;
        if (selectedItems && selectedItems.length == 1) {
            item = selectedItems [0];
        }
    } else if (this instanceof ZaAccountViewController || this instanceof ZaDLController) {
        item = this._currentObject;
    }

    if (item != null) {
        var effectiveRights = new ZaEffectiveRights(item);
        effectiveRights.load();
        var erCtrl = new ZaEffectiveRightsViewController(this._appCtxt, this._container) ;
        erCtrl.show(effectiveRights, true);
    }
}

ZaDelegatedAdminExt.changeActionsStateMethod =
function () {
    var cnt = this._contentView.getSelectionCount();
    if (cnt == 1) {
        var item = this._contentView.getSelection()[0];
        if (item) {
            if (item.type != ZaItem.ACCOUNT && item.type != ZaItem.DL) {
                if (this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS]) {
                    this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
                }
            } else if (item.attrs [ZaAccount.A_zimbraIsDelegatedAdminAccount] != "TRUE"
                    && item.attrs [ZaDistributionList.A_isAdminGroup] != "TRUE") {
                //we don't need to show the rights for system admin account since it has all the rights
                //we don't show the non-admin accounts since they have no rights.
                if (this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS]) {
                    this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
                }
            }
        } else {
            if (this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS]) {
                this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
            }
        }
    } else {
        if (this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS]) {
            this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
        }
    }
}
ZaController.changeActionsStateMethods["ZaAccountListController"].push(ZaDelegatedAdminExt.changeActionsStateMethod);

//Add the view rights button to the account view
if (ZaController.initToolbarMethods["ZaAccountViewController"]) {
    ZaDelegatedAdminExt.initExtraAccountViewToolbarMethod =
    function () {
        this._toolbarOperations [ZaOperation.VIEW_EFFECTIVE_RIGHTS] =
        new ZaOperation(ZaOperation.VIEW_EFFECTIVE_RIGHTS, com_zimbra_delegatedadmin.ACTBB_ViewRights,
                com_zimbra_delegatedadmin.ACTBB_ViewRights_tt, "RightObject", "RightObjectDis",
                new AjxListener(this, ZaDelegatedAdminExt._viewRightsListener)
                );
        this._toolbarOrder.push(ZaOperation.VIEW_EFFECTIVE_RIGHTS);

    }
    ZaController.initToolbarMethods["ZaAccountViewController"].push(ZaDelegatedAdminExt.initExtraAccountViewToolbarMethod);
}

ZaDelegatedAdminExt.changeAccountViewActionStateMethod = function () {
    if (!this._currentObject)
        return;

    if (this._currentObject.attrs [ZaAccount.A_zimbraIsDelegatedAdminAccount] != "TRUE") {
        this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaAccountViewController"].push(ZaDelegatedAdminExt.changeAccountViewActionStateMethod);

//add the view rights button to the DL view
if (ZaController.initToolbarMethods["ZaDLController"]) {
    ZaDelegatedAdminExt.initExtraDLViewToolbarMethod =
    function () {
        this._toolbarOperations [ZaOperation.VIEW_EFFECTIVE_RIGHTS] =
        new ZaOperation(ZaOperation.VIEW_EFFECTIVE_RIGHTS, com_zimbra_delegatedadmin.ACTBB_ViewRights,
                com_zimbra_delegatedadmin.ACTBB_ViewRights_tt, "RightObject", "RightObjectDis",
                new AjxListener(this, ZaDelegatedAdminExt._viewRightsListener)
                );
        this._toolbarOrder.push(ZaOperation.VIEW_EFFECTIVE_RIGHTS);

    }
    ZaController.initToolbarMethods["ZaDLController"].push(ZaDelegatedAdminExt.initExtraDLViewToolbarMethod);
}

ZaDelegatedAdminExt.changeDLViewActionStateMethod = function () {
    if (!this._currentObject)
        return;

    if (this._currentObject.attrs [ZaDistributionList.A_isAdminGroup] != "TRUE") {
        this._toolbarOperations[ZaOperation.VIEW_EFFECTIVE_RIGHTS].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaDLController"].push(ZaDelegatedAdminExt.changeDLViewActionStateMethod);





