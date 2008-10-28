
//add the bulk provision account toolbar button
if (ZaController.initToolbarMethods["ZaAccountListController"]) {
    ZaOperation.BULK_PROVISION = ++ ZA_OP_INDEX ;
    ZaAccountListController.initExtraToolbarMethod = function () {
        this._toolbarOperations.push(
                new ZaOperation(ZaOperation.BULK_PROVISION, com_zimbra_bulkprovision.ACTBB_BulkProvision,
                        com_zimbra_bulkprovision.ACTBB_BulkProvision_tt, "Account", "AccountDis", 
                        new AjxListener(this, ZaAccountListController.prototype._bulkProvisionListener)
                        ));
    }

    ZaController.initToolbarMethods["ZaAccountListController"].push(ZaAccountListController.initExtraToolbarMethod);
}

 ZaAccountListController.prototype._bulkProvisionListener =
 function (ev) {
     try {
		if(!ZaApp.getInstance().dialogs["bulkProvisionWizard"]) {
			ZaApp.getInstance().dialogs["bulkProvisionWizard"] = new ZaBulkProvisionWizard(this._container);
		}
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].setObject(new ZaBulkProvision());
		ZaApp.getInstance().dialogs["bulkProvisionWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._bulkProvisionListener", null, false);
	}
	return;
 }